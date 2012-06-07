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
import com.arm.nevada.client.shared.Out;
import com.arm.nevada.client.shared.SpecialBits;
import com.arm.nevada.client.utils.DataTypeTools;

/*
 vadd,
 vaddhn,
 vaddl,
 vaddw,
 vhadd,
 vhsub,
 vpadal,
 vpadd,
 vpaddl,
 vraddhn,
 vrhadd,
 vrsubhn,
 vqadd,
 vqsub,
 vsub,
 vsubhn,
 vsubl,
 vsubw,
 */
public class ArithmeticInstructions extends Instruction {

	private int destionationIndex;
	private int source1Index;
	private int source2Index;
	private EnumRegisterType destinationRegisterType;
	private EnumRegisterType source1RegisterType;
	private EnumRegisterType source2RegisterType;
	private EnumInstruction instruction;
	private EnumDataType dataType;

	private boolean addElseSub = false;
	private boolean narrowAndHighHalf = false;
	private boolean longing = false;
	private boolean wide = false;
	private boolean halving = false;
	private boolean pairwise = false;
	private boolean accumulate = false;
	private boolean rounding = false;
	private boolean saturating = false;

	private int source1Size;
	private int source2Size;
	private int destSize;

	
	public ArithmeticInstructions(EnumInstruction instruction, EnumRegisterType destRegisterType){
		this.instruction = instruction;
		this.destinationRegisterType = destRegisterType;
		
		initializeFlags();
	}

	private void initializeFlags() {
		switch (instruction) {
		case vadd:
			addElseSub = true;
			break;
		case vaddhn:
			addElseSub = true;
			narrowAndHighHalf = true;
			break;
		case vaddl:
			longing = true;
			addElseSub = true;
			break;
		case vaddw:
			wide = true;
			addElseSub = true;
			break;
		case vhadd:
			halving = true;
			addElseSub = true;
			break;
		case vhsub:
			halving = true;
			addElseSub = false;
			break;
		case vpadal:
			pairwise = true;
			accumulate = true;
			longing = true;
			addElseSub = true;
			break;
		case vpadd:
			pairwise = true;
			addElseSub = true;
			break;
		case vpaddl:
			pairwise = true;
			longing = true;
			addElseSub = true;
			break;
		case vraddhn:
			rounding = true;
			narrowAndHighHalf = true;
			addElseSub = true;
			break;
		case vrhadd:
			rounding = true;
			halving = true;
			addElseSub = true;
			break;
		case vrsubhn:
			rounding = true;
			narrowAndHighHalf = true;
			addElseSub = false;
			break;
		case vqadd:
			saturating = true;
			addElseSub = true;
			break;
		case vqsub:
			saturating = true;
			addElseSub = false;
			break;
		case vsub:
			addElseSub = false;
			break;
		case vsubhn:
			narrowAndHighHalf = true;
			addElseSub = false;
			break;
		case vsubl:
			longing = true;
			addElseSub = false;
			break;
		case vsubw:
			wide = true;
			addElseSub = false;
			break;

		default:
			assert false : "invalid instruction";
			break;
		}
	}

	@Override
	public void bindArguments(Arguments arguments) {
		this.dataType = arguments.getType();
		
		if (arguments.getRegisterIndexes().size() > 2) {
			this.destionationIndex = arguments.getRegisterIndexes().get(0);
			this.source1Index = arguments.getRegisterIndexes().get(1);
			this.source2Index = arguments.getRegisterIndexes().get(2);
		} else {
			this.destionationIndex = arguments.getRegisterIndexes().get(0);
			this.source1Index = arguments.getRegisterIndexes().get(0);
			this.source2Index = arguments.getRegisterIndexes().get(1);
		}

		source2Size = dataType.getSizeInBits();
		if (narrowAndHighHalf) {
			source1RegisterType = EnumRegisterType.QUAD;
			source2RegisterType = EnumRegisterType.QUAD;
			source1Size = source2Size;
			destSize = source2Size / 2;
		} else if (wide) {
			source1RegisterType = EnumRegisterType.QUAD;
			source2RegisterType = EnumRegisterType.DOUBLE;
			source1Size = source2Size * 2;
			destSize = source2Size * 2;
		} else if (longing) {
			if (pairwise) {
				source1RegisterType = destinationRegisterType;
				source2RegisterType = destinationRegisterType;
				source1Size = source2Size;
			} else {
				source1RegisterType = EnumRegisterType.DOUBLE;
				source2RegisterType = EnumRegisterType.DOUBLE;
			}
			source1Size = source2Size;
			destSize = source2Size * 2;
		} else {
			source1RegisterType = destinationRegisterType;
			source2RegisterType = destinationRegisterType;
			source1Size = source2Size;
			destSize = source2Size;
		}
	}

	@Override
	public void execute(Machine machine) {
		NEONRegisterSet neonRS = machine.getNEONRegisterSet();
		long[] source1Parts = DataTypeTools.createPartListFromWordsLong(source1Size,
				neonRS.getRegisterValues(source1RegisterType, source1Index));
		long[] source2Parts = DataTypeTools.createPartListFromWordsLong(source2Size,
				neonRS.getRegisterValues(source2RegisterType, source2Index));
		long[] destParts = DataTypeTools.createPartListFromWordsLong(destSize,
				neonRS.getRegisterValues(destinationRegisterType, destionationIndex));

		if (pairwise) {
			long[] s1p;
			long[] s2p;
			if (longing) {
				s1p = new long[source1Parts.length / 2];
				s2p = new long[source1Parts.length / 2];
				for (int i = 0; i < source1Parts.length / 2; i++) {
					s1p[i] = source2Parts[2 * i + 0];
					s2p[i] = source2Parts[2 * i + 1];
				}
			} else {
				s1p = new long[source1Parts.length];
				s2p = new long[source2Parts.length];
				int cntr = 0;
				for (int i = 0; i < source1Parts.length / 2; i++, cntr++) {
					s1p[cntr] = source1Parts[i * 2 + 0];
					s2p[cntr] = source1Parts[i * 2 + 1];
				}
				for (int i = 0; i < source2Parts.length / 2; i++, cntr++) {
					s1p[cntr] = source2Parts[i * 2 + 0];
					s2p[cntr] = source2Parts[i * 2 + 1];
				}
			}
			source1Parts = s1p;
			source2Parts = s2p;
		}

		assert source1Parts.length == source2Parts.length && source1Parts.length == destParts.length;

		if (dataType.isFloatType()) {
			for (int i = 0; i < destParts.length; i++) {
				destParts[i] = calculateFloat(source1Parts[i], source2Parts[i], destParts[i]);
			}
		} else {
			for (int i = 0; i < destParts.length; i++) {
				destParts[i] = calculateInt(source1Parts[i], source2Parts[i], destParts[i], machine);
			}
		}
		int[] resultWords = DataTypeTools.createWordsFromOnePartPerLong(destSize, destParts);
		neonRS.setRegisterValues(destinationRegisterType, true, destionationIndex, resultWords);
		machine.incrementPCBy4();
		highlightDestinationRegisters(machine);
	}

	private void highlightDestinationRegisters(Machine machine) {
		machine.highlightNEONRegister(destinationRegisterType, destionationIndex);
	}

	private long calculateInt(long s1, long s2, long dest, Machine machine) {
		// boolean addElseSub;
		// boolean narrow = false;
		// boolean highHalf = false;
		// boolean longing = false;
		// boolean wide = false;
		// boolean halving = false;
		// boolean pairwise = false;
		// boolean accumulate = false;
		// boolean rounding = false;
		// boolean saturating = false;

		long result = 0;
		boolean signed = dataType.getSigned() == null || dataType.getSigned() == false ? false : true;
		if (signed) {
			s1 = DataTypeTools.extendToSingnedLong(s1, source1Size);
			s2 = DataTypeTools.extendToSingnedLong(s2, source2Size);
			dest = DataTypeTools.extendToSingnedLong(dest, destSize);
		}

		if (addElseSub) {
			result = s1 + s2;
		} else {
			result = s1 - s2;
		}

		if (rounding) {
			long rundingConst = 0;
			if (narrowAndHighHalf)
				rundingConst = 1l << (destSize - 1);
			else if (halving) {
				rundingConst = 1;
			} else
				assert false;
			result += rundingConst;
		}

		if (halving) {
			if (signed)
				result = result >> 1;
			else
				result = result >>> 1;
		}

		if (narrowAndHighHalf) {
			assert source1Size == source2Size;
			result = result >>> (source2Size / 2);
		} else if (wide || longing) {
			// nothing to do
		}

		if (accumulate) {
			// It's a mistake in the documentation A8.6.348 VPADAL
			// if (signed && result < 0) {
			// result = -result;
			// }
			result = dest + result;
		}

		if (saturating) {
			Out<Boolean> saturated = new Out<Boolean>();
			result = saturatingAddOrSubstract(s1, s2, destSize, saturated, addElseSub, signed);
			if (saturated.getValue()) {
				int fpscr = machine.getSpecialRegisters().getFPSCR();
				fpscr = DataTypeTools.setBit(fpscr, true, SpecialBits.FPSCR_QC);
				machine.getSpecialRegisters().setFPSCR(fpscr, true);
			}
		}

		result = result & DataTypeTools.getBitmaskLong(destSize);
		return result;
	}

	private long calculateFloat(long s1, long s2, long dest) {
		float f1 = DataTypeTools.intToFloat(DataTypeTools.integerFromLong(s1)[0]);
		float f2 = DataTypeTools.intToFloat(DataTypeTools.integerFromLong(s2)[0]);
		float result;

		if (addElseSub) {
			result = f1 + f2;
		} else {
			result = f1 - f2;
		}

		long longResult = DataTypeTools.LongFromIntegers(DataTypeTools.FloatToInt(result), 0);
		return longResult;
	}

	private long saturatingAddOrSubstract(long x, long y, int size, Out<Boolean> saturated, boolean addElseSub, boolean signed) {
		if (signed)
			if (addElseSub)
				return saturatingAddSigned(x, y, size, saturated);
			else
				return saturatingSubSigned(x, y, size, saturated);
		else if (addElseSub)
			return saturatingAddUnsigned(x, y, size, saturated);
		else
			return saturatingSubUnsigned(x, y, size, saturated);
	}

	private long saturatingAddSigned(long x, long y, int size, Out<Boolean> saturated) {
		saturated.setValue(false);
		long max = DataTypeTools.getBitmaskLong(size - 1);
		long min = ~max;
		if (x == 0 || y == 0 || (x > 0 ^ y > 0)) {
			// zero+N or one pos, another neg = no problems
			return x + y;
		} else if (x > 0) {
			// both pos, can only overflow
			saturated.setValue(max - x < y);
			return max - x < y ? max : x + y;
		} else {
			// both neg, can only underflow
			saturated.setValue(min - x > y);
			return min - x > y ? min : x + y;
		}
	}

	private long saturatingAddUnsigned(long x, long y, int size, Out<Boolean> saturated) {
		long result = x + y;
		result = DataTypeTools.getBitmaskLong(size) & result;
		if (DataTypeTools.unsignedGreaterEqualThan(result, x)) {
			// ok
			saturated.setValue(false);
			return (x + y);
		} else {
			// saturated
			saturated.setValue(true);
			return DataTypeTools.getBitmaskLong(size);
		}
	}

	private long saturatingSubSigned(long x, long y, int size, Out<Boolean> saturated) {
		long max = DataTypeTools.getBitmaskLong(size - 1);
		long min = ~max;
		saturated.setValue(false);
		if (y == min) {
			if (x < 0) {
				saturated.setValue(true);
				return min;
			} else
				return x - y;
		}
		y = -y;

		if (x == 0 || y == 0 || (x > 0 ^ y > 0)) {
			// zero+N or one pos, another neg = no problems
			return x + y;
		} else if (x > 0) {
			// both pos, can only overflow
			saturated.setValue(max - x < y);
			return max - x < y ? max : x + y;
		} else {
			// both neg, can only underflow
			saturated.setValue(min - x > y);
			return min - x > y ? min : x + y;
		}
	}

	private long saturatingSubUnsigned(long x, long y, int size, Out<Boolean> saturated) {
		saturated.setValue(false);
		if (DataTypeTools.unsignedGreaterThan(y, x)) {
			saturated.setValue(true);
			return 0;
		} else {
			return (x - y);
		}
	}

	@Override
	public EnumInstruction getInstructionName() {
		return instruction;
	}

	@Override
	public Instruction create() {
		return new ArithmeticInstructions(this.instruction, this.destinationRegisterType);
	}

	@Override
	public EnumDataType getDataType() {
		return dataType;
	}
}
