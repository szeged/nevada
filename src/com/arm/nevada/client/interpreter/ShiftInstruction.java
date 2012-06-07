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

import com.arm.nevada.client.interpreter.machine.Machine;
import com.arm.nevada.client.interpreter.machine.NEONRegisterSet;
import com.arm.nevada.client.parser.Arguments;
import com.arm.nevada.client.parser.EnumInstruction;
import com.arm.nevada.client.parser.EnumRegisterType;
import com.arm.nevada.client.utils.DataTypeTools;

public class ShiftInstruction extends Instruction {
	private boolean immediate;

	private boolean left;
	private boolean round = false;
	private boolean changeSize = false;
	private boolean narrow = false;
	private boolean changeOnlyShiftedBits = false;
	private boolean accumulate = false;
	private boolean saturating = false;
	private boolean saturatingUnsigned = false;
	private boolean forceLogicalShift = false;

	private EnumInstruction instruction;
	private EnumDataType dataType;
	private int immediateValue;
	private Integer destinationIndex;
	private Integer sourceDataIndex;
	private Integer shiftRegisterIndex;
	private EnumRegisterType sourceRegisterType;

	private EnumRegisterType destinationRegisterType;
	private Integer destSize;

	public ShiftInstruction(EnumInstruction instruction, EnumRegisterType sourceRegisterType, boolean immediate) {
		this.instruction = instruction;
		this.sourceRegisterType = sourceRegisterType;
		this.immediate = immediate;
		
		this.initializeFlags();
	}
	
	private void initializeFlags() {
		switch (this.instruction) {
		case vrshl:
			left = true;
			round = true;
			break;
		case vshl:
			left = true;
			// forceLogicalShift = true;
			break;
		case vrshr:
			left = false;
			round = true;
			break;
		case vshr:
			left = false;
			break;
		case vrshrn:
			left = false;
			forceLogicalShift = true;
			round = true;
			changeSize = true;
			narrow = true;
			break;
		case vshrn:
			left = false;
			forceLogicalShift = true;
			changeSize = true;
			narrow = true;
			break;
		case vshll:
			left = true;
			changeSize = true;
			narrow = false;
			break;
		case vrsra:
			left = false;
			round = true;
			accumulate = true;
			break;
		case vsra:
			left = false;
			accumulate = true;
			break;
		case vsli:
			left = true;
			forceLogicalShift = true;
			changeOnlyShiftedBits = true;
			break;
		case vsri:
			left = false;
			forceLogicalShift = true;
			changeOnlyShiftedBits = true;
			break;
		case vqrshl:
			left = true;
			round = true;
			saturating = true;
			saturatingUnsigned = false;
			break;
		case vqrshrn:
			left = false;
			round = true;
			changeSize = true;
			narrow = true;
			saturating = true;
			saturatingUnsigned = false;
			break;
		case vqrshrun:
			left = false;
			round = true;
			changeSize = true;
			narrow = true;
			saturating = true;
			saturatingUnsigned = true;
			break;
		case vqshl:
			left = true;
			saturating = true;
			saturatingUnsigned = false;
			break;
		case vqshlu:
			left = true;
			saturating = true;
			saturatingUnsigned = true;
			break;
		case vqshrn:
			left = false;
			changeSize = true;
			narrow = true;
			saturating = true;
			saturatingUnsigned = false;
			break;
		case vqshrun:
			left = false;
			changeSize = true;
			narrow = true;
			saturating = true;
			saturatingUnsigned = true;
			break;
		default:
			assert false;
			break;
		}
	}
	
	@Override
	public void bindArguments(Arguments arguments) {
		this.dataType = arguments.getType();
		
		if (this.immediate) {
			
			this.immediateValue = (int) arguments.getImmediateValue();
			if (arguments.getRegisterIndexes().size() == 2) {
				this.destinationIndex = arguments.getRegisterIndexes().get(0);
				this.sourceDataIndex = arguments.getRegisterIndexes().get(1);
			} else if (arguments.getRegisterIndexes().size() == 1) {
				this.destinationIndex = arguments.getRegisterIndexes().get(0);
				this.sourceDataIndex = arguments.getRegisterIndexes().get(0);
			} else
				assert false;
		}
		else {
			if (arguments.getRegisterIndexes().size() == 3) {
				this.destinationIndex = arguments.getRegisterIndexes().get(0);
				this.sourceDataIndex = arguments.getRegisterIndexes().get(1);
				this.shiftRegisterIndex = arguments.getRegisterIndexes().get(2);
			} else if (arguments.getRegisterIndexes().size() == 2) {
				this.destinationIndex = arguments.getRegisterIndexes().get(0);
				this.sourceDataIndex = arguments.getRegisterIndexes().get(0);
				this.shiftRegisterIndex = arguments.getRegisterIndexes().get(1);
			} else {
				assert false;
			}
		}

		if (changeSize) {
			if (narrow) {
				assert sourceRegisterType == EnumRegisterType.QUAD;
				destinationRegisterType = EnumRegisterType.DOUBLE;
				destSize = dataType.getSizeInBits() / 2;
			} else {
				assert sourceRegisterType == EnumRegisterType.DOUBLE;
				destinationRegisterType = EnumRegisterType.QUAD;
				destSize = dataType.getSizeInBits() * 2;
			}
		} else {
			destSize = dataType.getSizeInBits();
			destinationRegisterType = sourceRegisterType;
		}
	}

	@Override
	public void execute(Machine machine) {
		NEONRegisterSet neonRegisterSet = machine.getNEONRegisterSet();
		boolean signedDataType = dataType.getSigned() == null ? false : dataType.getSigned();

		final int orgSize = dataType.getSizeInBits();
		int currentSize = orgSize;

		int[] currentIntParts = new int[sourceRegisterType.getSize() / currentSize];
		long[] currentLongParts = new long[sourceRegisterType.getSize() / currentSize];

		if (currentSize == 64) {
			currentLongParts = neonRegisterSet.getRegisterValuesLong(sourceRegisterType, sourceDataIndex);
		} else {
			int[] words = neonRegisterSet.getRegisterValues(sourceRegisterType, sourceDataIndex);
			for (int i = 0; i < words.length; i++) {
				int[] parts = DataTypeTools.getParts(currentSize, words[i]);
				for (int j = 0; j < parts.length; j++) {
					currentIntParts[i * 32 / currentSize + j] = signedDataType ? DataTypeTools.extendSignedToInt(parts[j], currentSize) : parts[j];
				}
			}
		}

		if (changeSize && !narrow) {
			if (currentSize == 32) {
				currentLongParts = extendFrom32To64(currentIntParts, signedDataType);
			} else {
				currentIntParts = extendToMax32bit(currentIntParts, orgSize, signedDataType);
			}
			currentSize *= 2;
		}

		int[] leftShiftAmount = getLeftShiftAmount(neonRegisterSet);

		long[] destParts = DataTypeTools.createPartListFromWordsLong(destSize,
				machine.getNEONRegisterSet().getRegisterValues(destinationRegisterType, destinationIndex));

		int afterShiftSize = narrow ? currentSize / 2 : currentSize;
		if (currentSize == 64) {
			for (int partI = 0; partI < currentLongParts.length; partI++) {
				currentLongParts[partI] = calculate64Bit(machine, currentLongParts[partI], leftShiftAmount[partI], currentSize, afterShiftSize,
						destParts[partI]);
			}
			assert afterShiftSize == 64 || afterShiftSize == 32;
			if (afterShiftSize == 32) {
				for (int i = 0; i < currentLongParts.length; i++) {
					currentIntParts[i] = DataTypeTools.integerFromLong(currentLongParts[i])[0];
				}
			}
		} else {
			for (int partI = 0; partI < currentIntParts.length; partI++) {
				currentIntParts[partI] = calculateMax32bit(machine, currentIntParts[partI], leftShiftAmount[partI], currentSize, afterShiftSize,
						destParts[partI]);
			}
		}
		currentSize = afterShiftSize;

		if (currentSize == 64) {
			store64(machine, currentLongParts, destinationRegisterType, destinationIndex);
		} else {
			storeMax32(machine, currentSize, currentIntParts, destinationRegisterType, destinationIndex);
		}
		highlightRegisters(machine);
		machine.incrementPCBy4();
	}

	private void highlightRegisters(Machine machine) {
		machine.highlightNEONRegister(destinationRegisterType, destinationIndex);
	}

	private int[] getLeftShiftAmount(NEONRegisterSet neonRegisterSet) {
		int size = dataType.getSizeInBits();
		int count = this.sourceRegisterType.getSize() / size;
		int[] out = new int[count];

		if (immediate) {
			for (int i = 0; i < out.length; i++) {
				out[i] = left ? immediateValue : -immediateValue;
			}
		} else {
			for (int i = 0; i < count; i++) {
				// shift amount is the lower 8 bit
				out[i] = neonRegisterSet.getSubRegister(sourceRegisterType, 8, this.shiftRegisterIndex, size / 8 * i);
				out[i] = DataTypeTools.extendSignedToInt(out[i], 8);
			}
		}
		return out;
	}

	private int[] extendToMax32bit(int[] input, int orgSize, boolean signed) {
		int[] output = new int[input.length];
		for (int i = 0; i < input.length; i++) {
			if (signed) {
				output[i] = DataTypeTools.extendSignedToInt(input[i], orgSize);
			} else {
				output[i] = input[i];
			}
		}
		return output;
	}

	private long[] extendFrom32To64(int[] input, boolean signed) {
		long[] output = new long[input.length];
		for (int i = 0; i < input.length; i++) {
			if (signed) {
				output[i] = input[i]; // automatic signed cast from int32 to long
			} else {
				output[i] = DataTypeTools.LongFromIntegers(input[i], 0);
			}
		}
		return output;
	}

	private int roundMax32Bit(int value, int leftShitftAmount) {
		if (leftShitftAmount >= 0)
			return value;
		else if (leftShitftAmount < -32)
			return -value;
		int roundConst = 1 << (-leftShitftAmount - 1);
		value += roundConst;
		return value;
	}

	private long round64bit(long value, int leftShiftAmount) {
		if (leftShiftAmount >= 0 || leftShiftAmount < -64)
			return value;
		long roundConst = 1l << (-leftShiftAmount - 1);
		value += roundConst;
		return value;
	}

	private void saturated(Machine machine) {
		int apsr = machine.getSpecialRegisters().getAPSR();
		apsr = DataTypeTools.setBit(apsr, true, 27);
		machine.getSpecialRegisters().setAPSR(apsr, true);
	}

	/**
	 * Saturate, shift, round, "insert", accumulate, Narrow! NO Extending
	 * 
	 * @param source
	 *            source value
	 * @param leftShiftAmount
	 *            can be negative
	 * @param originalSize
	 *            size of source
	 * @param outSize
	 *            The output size, used for narrowing. SourceSize >= outSize
	 * @return
	 */
	private int calculateMax32bit(Machine machine, final int source, int leftShiftAmount, final int originalSize, final int outSize, long destValue) {
		if (leftShiftAmount == 0 || source == 0)
			return source;

		if (dataType.getSigned() != null && dataType.getSigned() == true) {
			destValue = DataTypeTools.extendToSingnedLong(destValue, dataType.getSizeInBits());
		}

		boolean originalValueNonNegative = source >= 0;
		int current = source;
		if (round)
			current = roundMax32Bit(current, leftShiftAmount);

		if (leftShiftAmount > 32 || leftShiftAmount < -32) {
			leftShiftAmount = leftShiftAmount < 0 ? -32 : 32;
		}

		// saturate
		if (saturating) {
			boolean typeUnsigned = dataType.getSigned() == null || !dataType.getSigned();
			boolean satUnsigned = saturatingUnsigned;
			Integer saturated = DataTypeTools.saturateMax32bit(originalSize, outSize, source, leftShiftAmount, typeUnsigned, satUnsigned);
			if (saturated != null) {
				saturated(machine);
				return saturated;
			}
		}

		// shift
		// if (leftShiftAmount < 0 && -leftShiftAmount > 32) {
		// current = 0;
		// } else
		if (leftShiftAmount >= 0) {
			current = current << (leftShiftAmount / 2);
			current = current << (leftShiftAmount / 2);
			current = current << (leftShiftAmount % 2);

		} else {
			if (forceLogicalShift || originalValueNonNegative) {
				current = current >>> (-leftShiftAmount / 2);
				current = current >>> (-leftShiftAmount / 2);
				current = current >>> (-leftShiftAmount % 2);
			} else {
				current = current >> (-leftShiftAmount / 2);
				current = current >> (-leftShiftAmount / 2);
				current = current >> (-leftShiftAmount % 2);
			}
		}

		// optional "inserting" method
		if (changeOnlyShiftedBits) {
			assert outSize == originalSize;
			int bitMask = DataTypeTools.getBitmask(outSize);
			if (instruction == EnumInstruction.vsli) {
				bitMask = bitMask << (leftShiftAmount / 2);
				bitMask = bitMask << (leftShiftAmount / 2);
				bitMask = bitMask << (leftShiftAmount % 2);
			} else {
				bitMask = bitMask >>> (-leftShiftAmount / 2);
				bitMask = bitMask >>> (-leftShiftAmount / 2);
				bitMask = bitMask >>> (-leftShiftAmount % 2);
			}
			current = current | (DataTypeTools.integerFromLong(destValue)[0] & ~bitMask);
		}

		// optional accumulate
		if (accumulate) {
			current += destValue;
		}

		return current;
	}

	/**
	 * Saturate, shift, round, "insert", accumulate, Narrow! NO Extending
	 * 
	 * @param source
	 *            source value
	 * @param leftShiftAmount
	 *            can be negative
	 * @param originalSize
	 *            size of source
	 * @param outSize
	 *            The output size, used for narrowing. SourceSize >= outSize
	 * @return
	 */
	private long calculate64Bit(Machine machine, final long source, int leftShiftAmount, final int originalSize, final int outSize, long destValue) {
		if (leftShiftAmount == 0 || source == 0)
			return source;

		if (dataType.getSigned() != null && dataType.getSigned() == true) {
			destValue = DataTypeTools.extendToSingnedLong(destValue, dataType.getSizeInBits());
		}

		boolean originalValueNonNegative = source >= 0;
		long current = source;

		// round
		if (round)
			current = round64bit(current, leftShiftAmount);

		if (leftShiftAmount > 64 || leftShiftAmount < -64) {
			leftShiftAmount = leftShiftAmount < 0 ? -64 : 64;
		}

		// saturate
		if (saturating) {
			boolean typeUnsigned = dataType.getSigned() == null || !dataType.getSigned();
			boolean satUnsigned = saturatingUnsigned;
			Long saturated = DataTypeTools.saturate64bit(originalSize, outSize, source, leftShiftAmount, typeUnsigned, satUnsigned);
			if (saturated != null) {
				saturated(machine);
				return saturated;
			}
		}

		if (leftShiftAmount >= 0) {
			current = current << (leftShiftAmount / 2);
			current = current << (leftShiftAmount / 2);
			current = current << (leftShiftAmount % 2);
		} else {
			if (forceLogicalShift || originalValueNonNegative) {
				current = current >>> (-leftShiftAmount / 2);
				current = current >>> (-leftShiftAmount / 2);
				current = current >>> (-leftShiftAmount % 2);
			}
			else {
				current = current >> (-leftShiftAmount / 2);
				current = current >> (-leftShiftAmount / 2);
				current = current >> (-leftShiftAmount % 2);
			}
		}

		// optional "inserting" method
		if (changeOnlyShiftedBits) {
			assert outSize == originalSize;
			long bitMask = DataTypeTools.getBitmaskLong(outSize);
			if (instruction == EnumInstruction.vsli) {
				bitMask = bitMask << (leftShiftAmount / 2);
				bitMask = bitMask << (leftShiftAmount / 2);
				bitMask = bitMask << (leftShiftAmount % 2);
			} else {
				bitMask = bitMask >>> (-leftShiftAmount / 2);
				bitMask = bitMask >>> (-leftShiftAmount / 2);
				bitMask = bitMask >>> (-leftShiftAmount % 2);
			}
			current = current | (destValue & ~bitMask);
		}

		// optional accumulate
		if (accumulate) {
			current += destValue;
		}

		return current;
	}

	private void storeMax32(Machine machine, int size, int[] parts, EnumRegisterType regType, int destIndex) {
		final int doulbeCount = regType.getSize() / 64;
		int[] allWords = new int[regType.getSize() / 32];

		for (int doubleI = 0; doubleI < doulbeCount; doubleI++) {
			for (int wordI = 0; wordI < 2; wordI++) {
				int[] wordParts = new int[32 / size];
				for (int partI = 0; partI < 32 / size; partI++) {
					wordParts[partI] = parts[doubleI * 64 / size + wordI * 32 / size + partI];
				}
				allWords[doubleI * 2 + wordI] = DataTypeTools.createByParts(wordParts);
			}
		}
		machine.getNEONRegisterSet().setRegisterValues(regType, true, destIndex, allWords);
	}

	private void store64(Machine machine, long[] parts, EnumRegisterType regType, int destIndex) {
		final int doulbeCount = regType.getSize() / 64;

		int[] allWord = new int[doulbeCount * 2];
		for (int doubleI = 0; doubleI < doulbeCount; doubleI++) {
			int[] wordsFromLong = DataTypeTools.integerFromLong(parts[doubleI]);
			allWord[doubleI * 2 + 0] = wordsFromLong[0];
			allWord[doubleI * 2 + 1] = wordsFromLong[1];
		}
		machine.getNEONRegisterSet().setRegisterValues(regType, true, destIndex, allWord);
	}

	@Override
	public EnumInstruction getInstructionName() {
		return instruction;
	}

	@Override
	public Instruction create() {
		return new ShiftInstruction(this.instruction, this.sourceRegisterType, this.immediate);
	}

	@Override
	public EnumDataType getDataType() {
		return dataType;
	}
}
