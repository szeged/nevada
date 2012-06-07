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

import java.util.List;

import com.arm.nevada.client.interpreter.machine.Machine;
import com.arm.nevada.client.interpreter.machine.NEONRegisterSet;
import com.arm.nevada.client.parser.Arguments;
import com.arm.nevada.client.parser.EnumInstruction;
import com.arm.nevada.client.parser.EnumRegisterType;
import com.arm.nevada.client.shared.SpecialRegiser;
import com.arm.nevada.client.utils.DataTypeTools;

public class ComparisonInstruction extends Instruction {

	private enum Compare {
		lessThan,
		lessEqual,
		greaterThan,
		greaterEqual,
		equal,
		test
	}

	private int destinationRegisterIndex;
	private int source1RegisterIndex;
	private int source2RegisterIndex;
	private EnumDataType dateType;

	private boolean compareToZero;
	private boolean absolute;
	private Compare compareType;
	private EnumInstruction instruction;
	private EnumRegisterType registerType;

	private static final int falseValue = 0;
	private static final int trueValue = 0xFFFFFFFF;

	public ComparisonInstruction(
			EnumInstruction instruction, EnumRegisterType destinationRegisterType, boolean compareToZero) {
		this.instruction = instruction;
		this.registerType = destinationRegisterType;
		this.compareToZero = compareToZero;
		
		initializeFlags();
	}

	public ComparisonInstruction(EnumInstruction instruction, EnumRegisterType destinationRegisterType) {
		this(instruction, destinationRegisterType, false);
	}
	
	private void initializeFlags(){
		switch (this.instruction) {
		case vacge:
			compareType = Compare.greaterEqual;
			absolute = true;
			break;
		case vacgt:
			compareType = Compare.greaterThan;
			absolute = true;
			break;
		case vacle:
			compareType = Compare.lessEqual;
			absolute = true;
			break;
		case vaclt:
			compareType = Compare.lessThan;
			absolute = true;
			break;
		case vceq:
			compareType = Compare.equal;
			break;
		case vcge:
			compareType = Compare.greaterEqual;
			break;
		case vcgt:
			compareType = Compare.greaterThan;
			break;
		case vcle:
			compareType = Compare.lessEqual;
			break;
		case vclt:
			compareType = Compare.lessThan;
			break;
		case vtst:
			compareType = Compare.test;
			break;
		default:
			assert false;
		}
	}
	
	@Override
	public void bindArguments(Arguments arguments) {
		this.dateType = arguments.getType();

		List<Integer> registerIndexes = arguments.getRegisterIndexes();

		destinationRegisterIndex = registerIndexes.get(0);
		if (compareToZero) {
			if (registerIndexes.size() == 1) {
				source1RegisterIndex = destinationRegisterIndex;
			} else {
				source1RegisterIndex = registerIndexes.get(1);
			}
		} else {
			if (registerIndexes.size() == 2) {
				source1RegisterIndex = destinationRegisterIndex;
				source2RegisterIndex = registerIndexes.get(1);
			} else {
				source1RegisterIndex = registerIndexes.get(1);
				source2RegisterIndex = registerIndexes.get(2);
			}
		}
	}

	@Override
	public void execute(Machine machine) {
		NEONRegisterSet neonRegisterSet = machine.getNEONRegisterSet();
		int[] source1Parts = DataTypeTools.getParts(dateType.getSizeInBits(), neonRegisterSet.getRegisterValues(registerType, source1RegisterIndex));
		int[] source2Parts;
		if (compareToZero)
			source2Parts = new int[source1Parts.length]; // default zero
		else
			source2Parts = DataTypeTools.getParts(dateType.getSizeInBits(), neonRegisterSet.getRegisterValues(registerType, source2RegisterIndex));
		int[] resultParts = new int[source1Parts.length];

		for (int partI = 0; partI < source1Parts.length; partI++) {
			int result = calculate(source1Parts[partI], source2Parts[partI], machine);
			resultParts[partI] = result;
		}
		int[] outWords = DataTypeTools.createWordsFromOnePartPerWord(dateType.getSizeInBits(), resultParts);
		neonRegisterSet.setRegisterValues(registerType, true, destinationRegisterIndex, outWords);
		highlightDestinationRegisters(machine);
		machine.incrementPCBy4();
	}

	private void highlightDestinationRegisters(Machine machine) {
		machine.highlightNEONRegister(registerType, destinationRegisterIndex);
	}

	private int calculate(int first, int second, Machine machine) {
		int out;
		if (dateType.isFloatType()) {
			// float
			int fpscr = machine.getSpecialRegisters().getOneValue(SpecialRegiser.FPSCR.getIndex());
			float f1 = DataTypeTools.Float32Value(first, fpscr);
			float f2 = DataTypeTools.Float32Value(second, fpscr);
			if (absolute) {
				f1 = Math.abs(f1);
				f2 = Math.abs(f2);
			}
			if (new Float(f1).isNaN() || new Float(f2).isNaN())
				return falseValue;
			switch (compareType) {
			case equal:
				out = f1 == f2 ? trueValue : falseValue;
				return out;
			case greaterThan:
				out = f1 > f2 ? trueValue : falseValue;
				return out;
			case greaterEqual:
				out = f1 >= f2 ? trueValue : falseValue;
				return out;
			case lessThan:
				out = f1 < f2 ? trueValue : falseValue;
				return out;
			case lessEqual:
				out = f1 <= f2 ? trueValue : falseValue;
				return out;
			default:
				assert false;
			}
		} else {
			// integer
			first = first & DataTypeTools.getBitmask(dateType.getSizeInBits());
			second = second & DataTypeTools.getBitmask(dateType.getSizeInBits());
			int size = this.dateType.getSizeInBits();
			boolean signed = dateType.getSigned() != null && dateType.getSigned();
			// signed
			switch (compareType) {
			case equal:
				out = first == second ? trueValue : falseValue;
				return out;
			case greaterThan:
				out = DataTypeTools.greaterThan(first, second, size, signed) ? trueValue : falseValue;
				return out;
			case greaterEqual:
				out = !DataTypeTools.greaterThan(second, first, size, signed) ? trueValue : falseValue;
				return out;
			case lessThan:
				out = DataTypeTools.greaterThan(second, first, size, signed) ? trueValue : falseValue;
				return out;
			case lessEqual:
				out = !DataTypeTools.greaterThan(first, second, size, signed) ? trueValue : falseValue;
				return out;
			case test:
				assert !signed : "there is no NEON instruction like this";
				out = (first & second) != 0 ? trueValue : falseValue;
				return out;
			default:
				assert false;
			}
		}
		assert false;
		return falseValue;
	}

	@Override
	public EnumInstruction getInstructionName() {
		return this.instruction;
	}

	@Override
	public Instruction create() {
		return new ComparisonInstruction(this.instruction, this.registerType, this.compareToZero);
	}

	@Override
	public EnumDataType getDataType() {
		return this.dateType;
	}
}
