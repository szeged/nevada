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

public class ConversationInstruction extends Instruction {

	private EnumDataType dataType;
	private EnumRegisterType sourceRegisterType;
	private EnumRegisterType destinationRegisterType;
	private int sourceIndex;
	private int destinationIndex;
	private EnumInstruction instruction;

	private int fractionBitCount = 0;
	private boolean fixed;
	private boolean signed;

	public ConversationInstruction(EnumInstruction instruction, EnumRegisterType destinationRegisterType, boolean fixed) {
		this.instruction = instruction;
		this.destinationRegisterType = destinationRegisterType;
		this.fixed = fixed;
	}

	@Override
	public void bindArguments(Arguments arguments) {
		dataType = arguments.getType();
		if (dataType == EnumDataType._f16_f32) {
			sourceRegisterType = EnumRegisterType.QUAD;
		}
		else if (dataType == EnumDataType._f32_f16)  {
			sourceRegisterType = EnumRegisterType.DOUBLE;
		}
		else {
			sourceRegisterType = destinationRegisterType;
		}
		destinationIndex = arguments.getRegisterIndexes().get(0);
		sourceIndex = arguments.getRegisterIndexes().get(1);

		if (fixed) {
			fractionBitCount = (int) arguments.getImmediateValue();
		}
		signed = !(dataType == EnumDataType._u32_f32 || dataType == EnumDataType._f32_u32);
	}

	@Override
	public void execute(Machine machine) {
		NEONRegisterSet neonRegSet = machine.getNEONRegisterSet();
		int sourceSize;
		int resultSize;
		if (dataType == EnumDataType._f32_f16) {
			sourceSize = 16;
			resultSize = 32;
		} else if (dataType == EnumDataType._f16_f32) {
			sourceSize = 32;
			resultSize = 16;
		} else {
			sourceSize = dataType.getSizeInBits();
			resultSize = dataType.getSizeInBits();
		}
		int[] sourceParts = DataTypeTools.createPartListFromWords(sourceSize, neonRegSet.getRegisterValues(sourceRegisterType, sourceIndex));
		int[] resultParts = new int[sourceParts.length];

		if (fixed) {
			if (dataType == EnumDataType._s32_f32 || dataType == EnumDataType._u32_f32) {
				for (int i = 0; i < sourceParts.length; i++) {
					resultParts[i] = calculateSingleToFixed(sourceParts[i]);
				}
			} else if (dataType == EnumDataType._f32_s32 || dataType == EnumDataType._f32_u32) {
				for (int i = 0; i < sourceParts.length; i++) {
					resultParts[i] = calculateFixedToSingle(sourceParts[i]);
				}
			} else
				assert false : "non valid instruction state";
		} else {
			if (dataType == EnumDataType._s32_f32 || dataType == EnumDataType._u32_f32) {
				for (int i = 0; i < sourceParts.length; i++) {
					resultParts[i] = calculateSingleToInteger(sourceParts[i]);
				}
			} else if (dataType == EnumDataType._f32_s32 || dataType == EnumDataType._f32_u32) {
				for (int i = 0; i < sourceParts.length; i++) {
					resultParts[i] = calculateIntegerToSingle(sourceParts[i]);
				}
			} else if (dataType == EnumDataType._f32_f16) {
				for (int i = 0; i < sourceParts.length; i++) {
					resultParts[i] = calculateHalfToSingle(sourceParts[i]);
				}
			} else if (dataType == EnumDataType._f16_f32) {
				for (int i = 0; i < sourceParts.length; i++) {
					resultParts[i] = calculateSingleToHalf(sourceParts[i]);
				}
			}
		}

		int[] resultWords = DataTypeTools.createWordsFromOnePartPerWord(resultSize, resultParts);
		neonRegSet.setRegisterValues(destinationRegisterType, true, destinationIndex, resultWords);
		machine.incrementPCBy4();
		highlightRegisters(machine);

	}

	private void highlightRegisters(Machine machine) {
		machine.highlightNEONRegister(destinationRegisterType, destinationIndex);
	}

	/**
	 * http://stackoverflow.com/questions/6162651/half-precision-floating-point-in-java
	 * 
	 * @param hbits
	 * @return
	 */
	private int calculateHalfToSingle(int hbits) {
		int mant = hbits & 0x03ff; // 10 bits mantissa
		int exp = hbits & 0x7c00; // 5 bits exponent
		if (exp == 0x7c00) { // NaN/Inf
			exp = 0x3fc00; // -> NaN/Inf
			mant = 0x200;
		}
		else if (exp != 0) // normalized value
		{
			exp += 0x1c000; // exp - 15 + 127
			if (mant == 0 && exp > 0x1c400) // smooth transition
				return (hbits & 0x8000) << 16 | exp << 13 | 0x3ff;
		}
		else if (mant != 0){ // && exp==0 -> subnormal
			exp = 0x1c400; // make it normal
			do {
				mant <<= 1; // mantissa * 2
				exp -= 0x400; // decrease exp by 1
			} while ((mant & 0x400) == 0); // while not normal
			mant &= 0x3ff; // discard subnormal bit
		} // else +/-0 -> +/-0
		return // combine all parts
		(hbits & 0x8000) << 16 // sign << ( 31 - 15 )
				| (exp | mant) << 13; // value << ( 23 - 10 )
	}

	/**
	 * http://stackoverflow.com/questions/6162651/half-precision-floating-point-in-java
	 * 
	 * @param single
	 * @return
	 */
	private int calculateSingleToHalf(int single) {
		int fbits = single;
		int sign = fbits >>> 16 & 0x8000; // sign only
		int val = (fbits & 0x7fffffff) + 0x1000; // rounded value

		if (val >= 0x47800000) // might be or become NaN/Inf
		{ // avoid Inf due to rounding
			if ((fbits & 0x7fffffff) >= 0x47800000)
			{ // is or must become NaN/Inf
				if (val < 0x7f800000) // was value but too large
					return sign | 0x7c00; // make it +/-Inf
				return sign | 0x7c00 | // remains +/-Inf or NaN
						(fbits & 0x007fffff) >>> 13; // keep NaN (and Inf) bits
			}
			return sign | 0x7bff; // unrounded not quite Inf
		}
		if (val >= 0x38800000) // remains normalized value
			return sign | val - 0x38000000 >>> 13; // exp - 127 + 15
		if (val < 0x33000000) // too small for subnormal
			return sign; // becomes +/-0
		val = (fbits & 0x7fffffff) >>> 23; // tmp exp for subnormal calc
		int out = sign | ((fbits & 0x7fffff | 0x800000) // add subnormal bit
				+ (0x800000 >>> val - 102) // round depending on cut off
		>>> 126 - val); // div by 2^(1-(exp-127+15)) and >> 13 | exp=0

		return out;
	}

	private int calculateFixedToSingle(int fixed) {
		float single;
		if (signed) {
			single = fixed;
		} else {
			long fixedL = DataTypeTools.LongFromIntegers(fixed, 0);
			single = fixedL;
		}
		single = single / (1l << fractionBitCount);
		int out = DataTypeTools.FloatToInt(single);
		return out;
	}

	private int calculateSingleToFixed(final int single) {
		float singleF = DataTypeTools.intToFloat(single);
		singleF = singleF * (1l << fractionBitCount);

		int fixed;
		if (signed) {
			fixed = (int) singleF;
		} else {
			if (singleF < 0) {
				fixed = 0;
			} else {
				long longVal = (long) singleF;
				if (longVal > DataTypeTools.getBitmaskLong(32))
					fixed = -1; // full ones
				else
					fixed = DataTypeTools.integerFromLong(longVal)[0];
			}
		}
		return fixed;
	}

	private int calculateIntegerToSingle(int integer) {
		return calculateFixedToSingle(integer);
	}

	private int calculateSingleToInteger(int single) {
		return calculateSingleToFixed(single);
	}

	@Override
	public EnumInstruction getInstructionName() {
		return instruction;
	}

	@Override
	public Instruction create() {
		return new ConversationInstruction(this.instruction, this.destinationRegisterType, this.fixed);
	}

	@Override
	public EnumDataType getDataType() {
		return dataType;
	}
}
