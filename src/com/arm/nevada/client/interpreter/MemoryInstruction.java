/*
 * Copyright (C) 2011, 2012 University of Szeged
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY UNIVERSITY OF SZEGED ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL UNIVERSITY OF SZEGED OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.arm.nevada.client.interpreter;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.arm.nevada.client.interpreter.machine.Machine;
import com.arm.nevada.client.parser.Arguments;
import com.arm.nevada.client.parser.EnumInstruction;
import com.arm.nevada.client.parser.EnumRegisterType;
import com.arm.nevada.client.shared.events.AlignmentErrorEvent;
import com.arm.nevada.client.shared.events.SegmentationFaultEvent;
import com.arm.nevada.client.utils.DataTypeTools;

public class MemoryInstruction extends Instruction {
	private static final Logger logger = Logger.getLogger(MemoryInstruction.class.getName());

	public static enum Mode {
		ALL,
		ONE,
		ONE_REPEAT;
	}

	private EnumDataType dataType;
	private int elementCount;
	private Mode mode;
	private boolean writeBack;
	private int spacing;
	private int structureCount;
	private EnumInstruction instruction;
	private int doubleStartIndex;
	private int baseRegisterIndex;
	private int subRegisterIndex;
	private Integer offsetRegisterIndex = null;
	private int alignmentInByte;

	public MemoryInstruction(EnumInstruction instruction, Mode mode, int elementCount, int spacing, boolean writaBack) {
		this.instruction = instruction;
		this.mode = mode;
		this.elementCount = elementCount;
		this.spacing = spacing;
		this.writeBack = writaBack;
		
		initializeFlags();
	}

	private void initializeFlags() {
		switch (this.instruction) {
		case vst1:
		case vld1:
			structureCount = 1;
			break;
		case vst2:
		case vld2:
			structureCount = 2;
			break;
		case vst3:
		case vld3:
			structureCount = 3;
			break;
		case vst4:
		case vld4:
			structureCount = 4;
			break;
		default:
			throw new IllegalArgumentException("Invalid instruction for VST");
		}
	}

	@Override
	public void bindArguments(Arguments arguments) {
		this.dataType = arguments.getType();
		this.doubleStartIndex = arguments.getRegisterIndexes().get(0);
		this.baseRegisterIndex = arguments.getRegisterIndexes().get(1);
		this.offsetRegisterIndex = arguments.getRegisterIndexes().size() >= 3 ? arguments.getRegisterIndexes().get(2) : null;
		this.subRegisterIndex = arguments.getSubRegisterIndex();
		this.alignmentInByte = arguments.getAlignmentByte();
	}

	@Override
	public void execute(Machine machine) {
		// It is okay, since only vld1 ALL and vst1 ALL are 64 bit, but they don't care the type.
		final int size = dataType.getSizeInBits() == 64 ? 32 : dataType.getSizeInBits();
		final int writeBaseAddress = machine.getArmRegisterSet().getOneValue(baseRegisterIndex);
		final int partsPerWord = 32 / size;

		if (writeBaseAddress % alignmentInByte != 0) {
			AlignmentErrorEvent alignmentError = new AlignmentErrorEvent(writeBaseAddress, alignmentInByte * 8);
			machine.fireEvent(alignmentError);
			logger.log(Level.FINE, "GenerateAlignmentException() " + writeBaseAddress + "%" + (alignmentInByte));
			return;
		}

		Integer increment = null;
		int readBytes = 0;
		if (mode == Mode.ALL) {
			readBytes = 8 * elementCount;
		} else if (mode == Mode.ONE || mode == Mode.ONE_REPEAT) {
			readBytes = size / 8 * structureCount;
		} else
			assert false;

		int offsetBytes = machine.getMemorySet().getOffset() * 4;
		if (writeBaseAddress - offsetBytes < 0
				|| machine.getMemorySet().getSizeInBytes() + offsetBytes < writeBaseAddress + readBytes) {
			// FIXME: maybe unsigned comparison should be done
			SegmentationFaultEvent segmentationFault = new SegmentationFaultEvent();
			machine.fireEvent(segmentationFault);
			System.out.println("Segmentation fault");
			return;
		}

		switch (instruction) {
		case vld1:
		case vld2:
		case vld3:
		case vld4:
			// case vld
			executeVLD(machine, size, writeBaseAddress, partsPerWord);
			break;
		case vst1:
		case vst2:
		case vst3:
		case vst4:
			executeVST(machine, size, writeBaseAddress, partsPerWord);

			int changedBytes = mode == Mode.ALL ? elementCount * 8 : elementCount * size / 8;
			machine.highlightMemoryBytes(writeBaseAddress, writeBaseAddress + changedBytes - 1);
			break;

		} // switch instruction

		if (writeBack) {
			increment = readBytes;
		} else if (offsetRegisterIndex != null) {
			increment = machine.getArmRegisterSet().getOneValue(offsetRegisterIndex);
		}

		if (increment != null) {
			machine.getArmRegisterSet().setOneValue(
					baseRegisterIndex,
					writeBaseAddress + increment,
					true);
			machine.highlightARMRegister(baseRegisterIndex);
		}

		machine.incrementPCBy4();

	}

	private void executeVST(Machine machine, final int size,
			final int writeBaseAddress, final int partsPerWord) {
		
		if(this.mode == Mode.ALL) {
			// get the parts
			int[] allPart = new int[64 / size * elementCount];
			for (int element = 0; element < elementCount; element++) {
				int doubleIndex = doubleStartIndex + element * spacing;
				for (int word = 0; word < 2; word++) {
					int wordValue = machine.getNEONRegisterSet().getOneValue(doubleIndex * 2 + word);
					int[] wordParts = DataTypeTools.getParts(size, wordValue);
					for (int part = 0; part < partsPerWord; part++) {
						allPart[element * 64 / size + word * partsPerWord + part] = wordParts[part];
					}
				}
			}

			// interleave the parts
			int[] allPartReordered = new int[64 / size * elementCount];
			for (int structure = 0; structure < structureCount; structure++) {
				for (int inStructure = 0; inStructure < allPart.length / structureCount; inStructure++) {
					allPartReordered[structure + inStructure * structureCount] = allPart[inStructure + allPart.length / structureCount
							* structure];
				}
			}

			// create words from parts
			int[] reorderedWords = new int[elementCount * 2];
			for (int word = 0; word < elementCount * 2; word++) {
				int[] wordParts = new int[partsPerWord];
				for (int part = 0; part < partsPerWord; part++) {
					wordParts[part] = allPartReordered[word * partsPerWord + part];
				}
				reorderedWords[word] = DataTypeTools.createByParts(wordParts);
			}

			// write to memory
			for (int word = 0; word < elementCount * 2; word++) {
				machine.getMemorySet().setWord(writeBaseAddress + 4 * word, reorderedWords[word], true);
			}
		}
		else if (this.mode == Mode.ONE){
			int[] values = new int[elementCount];
			for (int element = 0; element < elementCount; element++) {
				int doubleIndex = doubleStartIndex + element * spacing;
				values[element] = machine.getNEONRegisterSet().getSubRegister(EnumRegisterType.DOUBLE, size, doubleIndex, subRegisterIndex);
			}

			for (int valueIndex = 0; valueIndex < elementCount; valueIndex++)
				machine.getMemorySet().setValue(writeBaseAddress + valueIndex * size / 8, values[valueIndex], size, true);
		}
	}

	private void executeVLD(Machine machine, final int size,
			final int writeBaseAddress, final int partsPerWord) {
		
		if(this.mode == Mode.ALL) {
			int currentAddress = writeBaseAddress;
			int[] allPart = new int[64 / size * elementCount];
			int filledParts = 0;

			for (int elementCounter = 0; elementCounter < elementCount * 2; elementCounter++) {
				int word = machine.getMemorySet().getWord(currentAddress);
				int[] wordParts = DataTypeTools.getParts(size, word);
				for (int partInWordCounter = 0; partInWordCounter < wordParts.length; partInWordCounter++) {
					allPart[filledParts] = wordParts[partInWordCounter];
					filledParts++;
				}
				currentAddress += 4;
			}

			int[][] structsAsParts = new int[structureCount][(elementCount / structureCount) * 2 * partsPerWord];

			for (int i = 0; i < allPart.length; i++) {
				structsAsParts[i % structureCount][i / structureCount] = allPart[i];
			}

			int[][] structsAsWords = new int[structureCount][elementCount / structureCount * 2];
			for (int struct = 0; struct < structureCount; struct++) {
				int[] structParts = structsAsParts[struct];
				for (int word = 0; word < structParts.length / partsPerWord; word++) {
					int[] wordParts = new int[partsPerWord];
					for (int i = 0; i < partsPerWord; i++) {
						wordParts[i] = structParts[partsPerWord * word + i];
					}
					int createdWord = DataTypeTools.createByParts(wordParts);
					structsAsWords[struct][word] = createdWord;
				}
			}

			int currentDoubleIndex = doubleStartIndex;
			for (int struct = 0; struct < structureCount; struct++) {
				int[] structWords = structsAsWords[struct];
				assert structWords.length % 2 == 0;
				for (int word = 0; word < structWords.length; word += 2) {
					machine.getNEONRegisterSet().setDouble(currentDoubleIndex, true, structsAsWords[struct][word],
							structsAsWords[struct][word + 1]);
					machine.highlightNEONRegister(EnumRegisterType.DOUBLE, currentDoubleIndex);
					currentDoubleIndex += spacing;
				}
			}
		}
		else {
			// Common
			int[] parts = new int[structureCount];
			for (int i = 0; i < structureCount; i++) {
				int currentBaseAddress = writeBaseAddress + i * size / 8;
				int currentWord = machine.getMemorySet().getWord(currentBaseAddress);
				parts[i] = DataTypeTools.getParts(size, currentWord)[0];

			}
			// ONE
			if (mode == Mode.ONE) {
				for (int structure = 0; structure < structureCount; structure++) {
					int currentDIndex = doubleStartIndex + structure * spacing;
					machine.getNEONRegisterSet().setSubRegister(EnumRegisterType.DOUBLE, size, currentDIndex, subRegisterIndex, parts[structure]);
					machine.highlightNEONSubregister(EnumRegisterType.DOUBLE, size, currentDIndex, subRegisterIndex);
				}
			}
			// ONE_REPEAT
			else if (mode == Mode.ONE_REPEAT) {
				for (int structure = 0; structure < structureCount; structure++) {
					for (int dInStructure = 0; dInStructure < elementCount / structureCount; dInStructure++) {
						int currentDIndex =
								doubleStartIndex
										+ elementCount / structureCount * structure * spacing
										+ spacing * dInStructure;
						int[] currentWordParts = new int[partsPerWord];
						for (int part = 0; part < partsPerWord; part++) {
							currentWordParts[part] = parts[structure];
						}
						int currentWord = DataTypeTools.createByParts(currentWordParts);
						machine.getNEONRegisterSet().setDouble(currentDIndex, true, currentWord, currentWord);
						machine.highlightNEONRegister(EnumRegisterType.DOUBLE, currentDIndex);
					}
				}
			}
		}
	}

	@Override
	public EnumInstruction getInstructionName() {
		return this.instruction;
	}

	@Override
	public Instruction create() {
		return new MemoryInstruction(this.instruction, this.mode, this.elementCount, this.spacing, this.writeBack);
	}

	@Override
	public EnumDataType getDataType() {
		return this.dataType;
	}
}
