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

public class MinimumAndMaximumInstruction extends Instruction {

	private EnumDataType dataType;
	private int data1Index;
	private int data2Index;
	private EnumInstruction instruction;
	private EnumRegisterType destinationRegisterType;
	private EnumRegisterType sourceRegisterType;

	private Boolean minimumElseMaximum;
	private boolean pairwise;
	private int destinationRegisterIndex;
	private int size;

	public MinimumAndMaximumInstruction(EnumInstruction instruction, EnumRegisterType registerType) {
		this.instruction = instruction;
		this.destinationRegisterType = registerType;
		this.sourceRegisterType = registerType;
		
		initializeFlags();
	}
	
	private void initializeFlags() {
		switch (instruction) {
		case vmin:
			minimumElseMaximum = true;
			pairwise = false;
			break;
		case vmax:
			minimumElseMaximum = false;
			pairwise = false;
			break;
		case vpmin:
			minimumElseMaximum = true;
			pairwise = true;
			break;
		case vpmax:
			minimumElseMaximum = false;
			pairwise = true;
			break;
		default:
			assert false;
			break;
		}
	}
	
	@Override
	public void bindArguments(Arguments arguments) {
		this.dataType = arguments.getType();
		this.size = dataType.getSizeInBits();

		this.destinationRegisterIndex = arguments.getRegisterIndexes().get(0);
		this.data1Index = arguments.getRegisterIndexes().get(1);
		this.data2Index = arguments.getRegisterIndexes().get(2);
	}

	@Override
	public void execute(Machine machine) {
		int[] resultParts;
		if (pairwise) {
			resultParts = execPairwise(machine);
		}
		else {
			resultParts = execRegisterWise(machine);
		}

		int[] resultWords = DataTypeTools.createWordsFromOnePartPerWord(size, resultParts);
		machine.getNEONRegisterSet().setRegisterValues(destinationRegisterType, true, destinationRegisterIndex, resultWords);
		machine.incrementPCBy4();
		highlightRegisters(machine);
	}

	private int calculate(int op1, int op2) {
		int result;
		if (dataType.isFloatType()) {
			result = calculateFloat(op1, op2);
		}
		else {
			result = calculateInteger(op1, op2);
		}
		return result;
	}

	private int[] execRegisterWise(Machine machine) {
		assert !pairwise;
		NEONRegisterSet neonRegSet = machine.getNEONRegisterSet();
		int[] source1Parts = DataTypeTools.createPartListFromWords(size, neonRegSet.getRegisterValues(sourceRegisterType, data1Index));
		int[] source2Parts = DataTypeTools.createPartListFromWords(size, neonRegSet.getRegisterValues(sourceRegisterType, data2Index));
		int[] resultParts = new int[source1Parts.length];
		for (int i = 0; i < source2Parts.length; i++) {
			resultParts[i] = calculate(source1Parts[i], source2Parts[i]);
		}
		return resultParts;
	}

	private int[] execPairwise(Machine machine) {
		assert pairwise;
		NEONRegisterSet neonRegSet = machine.getNEONRegisterSet();
		int[] source1Parts = DataTypeTools.createPartListFromWords(size, neonRegSet.getRegisterValues(sourceRegisterType, data1Index));
		int[] source2Parts = DataTypeTools.createPartListFromWords(size, neonRegSet.getRegisterValues(sourceRegisterType, data2Index));
		int[][] sources = new int[][] { source1Parts, source2Parts };

		int[] resultParts = new int[source1Parts.length];
		for (int sourceI = 0; sourceI < sources.length; sourceI++) {
			for (int i = 0; i < source1Parts.length; i += 2) {
				resultParts[(sourceI * source1Parts.length + i) / 2] = calculate(sources[sourceI][i], sources[sourceI][i + 1]);
			}
		}
		return resultParts;
	}

	private int calculateInteger(int op1, int op2) {
		int result;
		if (dataType.getSigned()) {
			int op1Signed = DataTypeTools.extendSignedToInt(op1, size);
			int op2Signed = DataTypeTools.extendSignedToInt(op2, size);
			if (minimumElseMaximum)
				result = op1Signed >= op2Signed ? op2 : op1;
			else
				result = op1Signed <= op2Signed ? op2 : op1;
		} else if (minimumElseMaximum)
			result = DataTypeTools.unsignedGreaterEqualThan(op1, op2) ? op2 : op1;
		else
			result = DataTypeTools.unsignedGreaterEqualThan(op1, op2) ? op1 : op2;
		return result;
	}

	private int calculateFloat(int op1, int op2) {
		int result;
		float op1Float = DataTypeTools.intToFloat(op1);
		float op2Float = DataTypeTools.intToFloat(op2);
		if (Float.isNaN(op1Float) || Float.isNaN(op2Float))
			result = 0x7fc00000;
		else if (minimumElseMaximum)
			result = op1Float >= op2Float ? op2 : op1;
		else
			result = op1Float <= op2Float ? op2 : op1;
		return result;
	}

	private void highlightRegisters(Machine machine) {
		machine.highlightNEONRegister(destinationRegisterType, destinationRegisterIndex);
	}

	@Override
	public EnumInstruction getInstructionName() {
		return instruction;
	}

	@Override
	public Instruction create() {
		return new MinimumAndMaximumInstruction(this.instruction, this.destinationRegisterType);
	}

	@Override
	public EnumDataType getDataType() {
		return dataType;
	}
}
