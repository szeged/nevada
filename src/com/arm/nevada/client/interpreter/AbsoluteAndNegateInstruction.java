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
import com.arm.nevada.client.shared.SpecialBits;
import com.arm.nevada.client.shared.SpecialRegiser;
import com.arm.nevada.client.utils.DataTypeTools;

public class AbsoluteAndNegateInstruction extends Instruction {

	private EnumInstruction instruction;

	private boolean longing = false;
	private boolean accumulate = false;
	private boolean difference = false;

	private EnumDataType dataType;
	private EnumRegisterType sourceRegisterType;
	private EnumRegisterType destinationRegisterType;
	private int source1Index;
	private int source2Index;
	private int destinationIndex;

	private boolean saturate;

	private boolean negateInstruction;

	
	public AbsoluteAndNegateInstruction(EnumInstruction instruction, EnumRegisterType destinationRegisterType) {
		this.instruction = instruction;
		this.destinationRegisterType = destinationRegisterType;
		
		initializeFlags();
	}

	private void initializeFlags() {
		switch (this.instruction) {
		case vaba:
			accumulate = true;
			difference = true;
			break;
		case vabal:
			accumulate = true;
			difference = true;
			longing = true;
			break;
		case vabd:
			difference = true;
			break;
		case vabdl:
			difference = true;
			longing = true;
			break;
		case vabs:
			break;
		case vqabs:
			saturate = true;
			break;
		case vneg:
			negateInstruction = true;
			break;
		case vqneg:
			negateInstruction = true;
			saturate = true;
			break;
		default:
			assert false : "Invalid insstruction";
		}
		
		if (longing) {
			sourceRegisterType = EnumRegisterType.DOUBLE;
		}
		else {
			sourceRegisterType = this.destinationRegisterType;
		}
	}
	
	@Override
	public void bindArguments(Arguments arguments) {
		dataType = arguments.getType();
		destinationIndex = arguments.getRegisterIndexes().get(0);
		source1Index = arguments.getRegisterIndexes().get(1);
		if (difference) {
			source2Index = arguments.getRegisterIndexes().get(2);
		}
	}

	@Override
	public void execute(Machine machine) {
		NEONRegisterSet neonRegSet = machine.getNEONRegisterSet();
		int size = dataType.getSizeInBits();
		assert size != 64;
		int outSize = longing ? size * 2 : size;

		int[] op1Parts = null;
		int[] op2Parts = null;
		long[] destParts = null;

		op1Parts = DataTypeTools.getParts(size, neonRegSet.getRegisterValues(sourceRegisterType, source1Index));
		if (difference)
			op2Parts = DataTypeTools.getParts(size, neonRegSet.getRegisterValues(sourceRegisterType, source2Index));
		if (accumulate)
			destParts = DataTypeTools.createPartListFromWordsLong(outSize, neonRegSet.getRegisterValues(destinationRegisterType, destinationIndex));

		int[] resultPartList = new int[longing ? 2 * op1Parts.length : op1Parts.length];
		if (negateInstruction){
			if (dataType == EnumDataType._f32) {
				for (int i = 0; i < op1Parts.length; i++) {
					resultPartList[i] = negateFloat32(op1Parts[i]);
				}
			} else {
				for (int i = 0; i < op1Parts.length; i++) {
					resultPartList[i] = negateInteger(machine, op1Parts[i]);
				}
			}
		} else {
			if (dataType == EnumDataType._f32) {
				for (int i = 0; i < op1Parts.length; i++) {
					resultPartList[i] = absoluteFloat32(op1Parts[i], difference ? op2Parts[i] : 0, accumulate ? ((int) destParts[i]) : 0);
				}
			} else {
				for (int i = 0; i < op1Parts.length; i++) {
					long res = absoluteInteger(machine, op1Parts[i], difference ? op2Parts[i] : 0, accumulate ? destParts[i] : 0);
					long bitMask = DataTypeTools.getBitmaskLong(outSize);
					res = res & bitMask;
					int[] resParts = DataTypeTools.getParts(size, res);
					if (longing) {
						resultPartList[2 * i + 0] = resParts[0];
						resultPartList[2 * i + 1] = resParts[1];
					} else {
						resultPartList[i] = resParts[0];
					}
				}
			}
		}

		int[] registerWords = DataTypeTools.createWordsFromOnePartPerWord(size, resultPartList);
		neonRegSet.setRegisterValues(destinationRegisterType, true, destinationIndex, registerWords);
		machine.incrementPCBy4();
		highlightRegisters(machine);
	}

	private void highlightRegisters(Machine machine) {
		machine.highlightNEONRegister(destinationRegisterType, destinationIndex);
	}

	/**
	 * 
	 * @param op1
	 *            unextended
	 * @param op2
	 *            unextended, if not needed by the instruction then not used
	 * @param dest
	 *            unextended, if not needed by the instruction then not used
	 * @return
	 */
	private long absoluteInteger(Machine machine, int op1, int op2, long dest) {
		long op1l = op1;
		long op2l = op2;
		long result = 0;
		int size = dataType.getSizeInBits();
		int outSize = longing ? size * 2 : size;
		boolean signedInteger = dataType.isInteger() && dataType.getSigned();

		long inMask = DataTypeTools.getBitmaskLong(size);
		long outMask = DataTypeTools.getBitmaskLong(outSize);

		if (signedInteger) {
			op1l = DataTypeTools.extendToSingnedLong(op1l, size);
			op2l = difference ? DataTypeTools.extendToSingnedLong(op2l, size) : 0;
			dest = accumulate ? DataTypeTools.extendToSingnedLong(dest, outSize) : 0;
		} else {
			op1l = op1l & inMask;
			op2l = difference ? op2l & inMask : 0;
			dest = accumulate ? dest & outMask : 0;
		}

		if (difference) {
			long absDiff = op1l - op2l;
			absDiff = Math.abs(absDiff);
			result += absDiff;
		}

		if (accumulate)
			result += dest;
		
		if (saturate)
			if (op1l == ~DataTypeTools.getBitmaskLong(size - 1)){  // equals to the minimum
				setCumulativeSaturationFlag(machine, true);
				op1l++;
			}

		if (!difference && !accumulate)
			result = Math.abs(op1l);

		result = result & outMask;
		return result;
	}

	private void setCumulativeSaturationFlag(Machine machine, boolean set) {
		int fpscr = machine.getSpecialRegisters().getOneValue(SpecialRegiser.FPSCR.getIndex());
		fpscr = DataTypeTools.setBit(fpscr, set, SpecialBits.FPSCR_QC);
		machine.getSpecialRegisters().setOneValue(SpecialRegiser.FPSCR, fpscr, true);
	}

	private int absoluteFloat32(int op1, int op2, int dest) {
		if (!accumulate && !difference)
			return DataTypeTools.setBit(op1, false, 31);

		float op1F = DataTypeTools.intToFloat(op1);
		float op2F = DataTypeTools.intToFloat(op2);
		float result = 0;

		if (accumulate) {
			float destF = DataTypeTools.intToFloat(dest);
			result += destF;
		}

		if (difference) {
			float absDiff = op1F - op2F;
			absDiff = Math.abs(absDiff);
			result += absDiff;
		}

		int resultInt = DataTypeTools.FloatToInt(result);
		return resultInt;
	}

	private int negateInteger(Machine machine, int value) {
		assert !difference && !accumulate && !longing;
		int result;
		int size = dataType.getSizeInBits();
		int bitmask = DataTypeTools.getBitmask(size);
		if (saturate) {
			result = DataTypeTools.extendSignedToInt(value, size);
			if (result == ~DataTypeTools.getBitmask(size - 1)) {	// equals to minimum value
				result++;
				setCumulativeSaturationFlag(machine, true);
			}
			result = -result;
			result = result & bitmask;
		} else {
			result = (-value) & bitmask;
		}
		return result;
	}
	
	private int negateFloat32(int value){
		assert !difference && !accumulate && !longing;
		assert !saturate;
		int result = value ^ 0x80000000;
		return result;
	}
	
	@Override
	public EnumInstruction getInstructionName() {
		return instruction;
	}

	@Override
	public Instruction create() {
		return new AbsoluteAndNegateInstruction(this.instruction, this.destinationRegisterType);
	}

	@Override
	public EnumDataType getDataType() {
		return dataType;
	}
}
