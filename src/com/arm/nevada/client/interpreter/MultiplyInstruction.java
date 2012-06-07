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

public class MultiplyInstruction extends Instruction {
	private int source1RegisterIndex;
	private int destinationRegisterIndex;
	private Integer subRegisterIndex;
	private EnumDataType dataType;
	private boolean scalar = false;

	private boolean accumulate = false;
	private boolean substract = false;
	private boolean longing = false;
	private boolean doubling = false;
	private boolean saturating = false;
	private boolean rounding = false;
	private boolean highHalf = false;
	private EnumInstruction instruction;
	private EnumRegisterType destRegisterType;
	private EnumRegisterType sourceRegisterType;
	private Integer source2RegisterIndex;

	public MultiplyInstruction(EnumInstruction instruction, EnumRegisterType destRegisterType, boolean scalar) {
		this.instruction = instruction;
		this.destRegisterType = destRegisterType;
		this.scalar = scalar;
		
		initializeFlags();
	}
	
	private void initializeFlags() {
		switch (this.instruction) {
		case vmla:
			accumulate = true;
			break;
		case vmlal:
			accumulate = true;
			longing = true;
			break;
		case vmls:
			substract = true;
			break;
		case vmlsl:
			substract = true;
			longing = true;
			break;
		case vmul:
			break;
		case vmull:
			longing = true;
			break;
		case vqdmlal:
			saturating = true;
			doubling = true;
			accumulate = true;
			longing = true;
			break;
		case vqdmlsl:
			saturating = true;
			doubling = true;
			substract = true;
			longing = true;
			break;
		case vqdmulh:
			saturating = true;
			doubling = true;
			highHalf = true;
			break;
		case vqrdmulh:
			saturating = true;
			rounding = true;
			doubling = true;
			highHalf = true;
			break;
		case vqdmull:
			saturating = true;
			doubling = true;
			longing = true;
			break;
		default:
			assert false;
			break;
		}

		if (this.longing) {
			this.sourceRegisterType = EnumRegisterType.DOUBLE;
		}
		else {
			this.sourceRegisterType = this.destRegisterType;
		}
	}

	@Override
	public void bindArguments(Arguments arguments) {
		this.dataType = arguments.getType();
		
		if (this.scalar) {
			this.subRegisterIndex = arguments.getSubRegisterIndex();
		}
		
		this.destinationRegisterIndex = arguments.getRegisterIndexes().get(0);
		this.source1RegisterIndex = arguments.getRegisterIndexes().get(1);
		this.source2RegisterIndex = arguments.getRegisterIndexes().get(2);
	}

	@Override
	public void execute(Machine machine) {
		NEONRegisterSet neonRS = machine.getNEONRegisterSet();
		int size = dataType.getSizeInBits();
		int destSize = longing ? size * 2 : size;
		int[] op1s = DataTypeTools.getParts(size, neonRS.getRegisterValues(sourceRegisterType, source1RegisterIndex));
		int[] op2s;
		long[] destVals = DataTypeTools.createPartListFromWordsLong(destSize, neonRS.getRegisterValues(destRegisterType, destinationRegisterIndex));
		if (scalar) {
			op2s = new int[op1s.length];
			int scalarValue = neonRS.getSubRegister(EnumRegisterType.DOUBLE, size, source2RegisterIndex, subRegisterIndex);
			for (int i = 0; i < op2s.length; i++) {
				op2s[i] = scalarValue;
			}
		} else {
			op2s = DataTypeTools.getParts(size, neonRS.getRegisterValues(sourceRegisterType, source2RegisterIndex));
		}

		int[] resultWords;
		if (dataType.isFloatType()) {
			int[] results = new int[op1s.length];
			for (int i = 0; i < op1s.length; i++) {
				float op1 = DataTypeTools.intToFloat(op1s[i]);
				float op2 = DataTypeTools.intToFloat(op2s[i]);
				float dest = DataTypeTools.intToFloat(DataTypeTools.integerFromLong(destVals[i])[0]);
				float result = calculateFloat(machine, op1, op2, dest);
				results[i] = DataTypeTools.FloatToInt(result);
			}
			resultWords = DataTypeTools.createWordsFromOnePartPerWord(size, results);
		} else {
			long[] results = new long[op1s.length];
			int outSize = longing ? size * 2 : size;
			if (dataType.isPolynomial()) {
				for (int i = 0; i < op1s.length; i++) {
					long result = calculatePolynomial(machine, op1s[i], op2s[i]);
					results[i] = result;
				}
			} else {
				// integer
				boolean signed = dataType.getSigned() != null && dataType.getSigned() == true;
				if (signed) {
					for (int i = 0; i < op1s.length; i++) {
						long dest = DataTypeTools.extendToSingnedLong(destVals[i], outSize);
						long result = calculateIntegerSigned(machine, op1s[i], op2s[i], dest);
						results[i] = result;
					}
				} else {
					for (int i = 0; i < op1s.length; i++) {
						long result = calculateIntegerUnsigned(machine, op1s[i], op2s[i], destVals[i]);
						results[i] = result;
					}
				}
			}
			resultWords = DataTypeTools.createWordsFromOnePartPerLong(outSize, results);
		}
		neonRS.setRegisterValues(destRegisterType, true, destinationRegisterIndex, resultWords);
		machine.incrementPCBy4();
		highlightDestinationRegisters(machine);
	}

	private void highlightDestinationRegisters(Machine machine) {
		machine.highlightNEONRegister(destRegisterType, destinationRegisterIndex);
	}

	private long calculateIntegerUnsigned(Machine machine, final int op1, final int op2, long dest) {
		// assert !accumulate;
		// assert !substract;
		// assert !longing;
		assert !doubling;
		assert !saturating; // there is no saturating instruction with unsigned type
		assert !rounding; // there is no rounding instruction with unsigned type
		assert !highHalf;

		int inSize = dataType.getSizeInBits();
		assert inSize <= 32;
		int outSize = longing ? inSize * 2 : inSize;

		long bitMask = DataTypeTools.getBitmaskLong(inSize);
		long op1l = DataTypeTools.LongFromIntegers(op1, 0) & bitMask;
		long op2l = DataTypeTools.LongFromIntegers(op2, 0) & bitMask;
		long result = op1l * op2l;

		if (accumulate)
			result = dest + result;
		else if (substract)
			result = dest - result;

		result = result & DataTypeTools.getBitmaskLong(outSize);
		return result;
	}

	private long calculateIntegerSigned(Machine machine, final int op1, final int op2, long dest) {
		assert dataType.getSigned() != null && dataType.getSigned() == true;
		assert !(highHalf && longing);

		int inSize = dataType.getSizeInBits();
		assert inSize == 8 || inSize == 16 || inSize == 32;
		int outSize = longing ? inSize * 2 : inSize;

		long op1l = DataTypeTools.extendSignedToInt(op1, inSize);
		long op2l = DataTypeTools.extendSignedToInt(op2, inSize);
		long result = op1l * op2l;

		if (saturating && op1l == op2l && op1l == DataTypeTools.getMinValueLong(inSize, true)) {
			// there is no other case for saturating when the out size is 2 * inSize
			setSaturatingBit(machine);
			result = DataTypeTools.getMaxValueLong(2 * inSize, true); // 1l << (2 * inSize); // - * - = maximum value
			if (highHalf) {
				result = result >> inSize;
			} else if (longing) {

			}
			return result;
		}

		if (doubling)
			result *= 2;

		long addend = 0;
		if (accumulate || substract) {
			addend = dest;
			if (substract)
				result = -result;
		}

		if (highHalf) {
			assert saturating;
			if (rounding) {
				long rounding_const = 1l << (inSize - 1);
				result += rounding_const;
			}
			result = result >> inSize;
		}

		if (saturating) {
			Out<Boolean> saturated = new Out<Boolean>();
			result = DataTypeTools.signedSaturatingAdd(addend, result, outSize, saturated);
			if (saturated.getValue()) {
				setSaturatingBit(machine);
			}
		} else {
			result = addend + result;
		}

		if (longing)
			result = result & DataTypeTools.getBitmaskLong(2 * inSize);
		else
			result = result & DataTypeTools.getBitmaskLong(inSize);
		return result;
	}

	private float calculateFloat(Machine machine, final float op1, final float op2, float dest) {
		assert !longing;
		assert !doubling;
		assert !saturating;
		assert !rounding;
		assert !highHalf;
		assert dataType.isFloatType();

		float out = op1 * op2;
		if (accumulate)
			out = dest + out;
		else if (substract)
			out = dest - out;
		return out;
	}

	private long calculatePolynomial(Machine machine, final int op1, final int op2) {
		assert !accumulate;
		assert !substract;
		// assert !longing;
		assert !doubling;
		assert !saturating;
		assert !rounding;
		assert !highHalf;

		int size = dataType.getSizeInBits();
		long out = DataTypeTools.polynominalMultiplicate(op1, size, op2, size);

		int outSize = longing ? size * 2 : size;
		out = out & DataTypeTools.getBitmask(outSize);
		return out;
	}

	private void setSaturatingBit(Machine machine) {
		int fpscr = machine.getSpecialRegisters().getFPSCR();
		fpscr = DataTypeTools.setBit(fpscr, true, SpecialBits.FPSCR_QC);
		machine.getSpecialRegisters().setFPSCR(fpscr, true);
	}

	@Override
	public EnumInstruction getInstructionName() {
		return instruction;
	}

	@Override
	public Instruction create() {
		return new MultiplyInstruction(this.instruction, this.destRegisterType, this.scalar);
	}

	@Override
	public EnumDataType getDataType() {
		return dataType;
	}
}
