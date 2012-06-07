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

public class ReciprocalSqrtReciprocalStep extends Instruction {
	private EnumDataType dataType;
	private EnumRegisterType registerType;
	private int source1Index;
	private int source2Index;
	private int destinationIndex;

	private EnumInstruction instruction;

	private int size;

	public ReciprocalSqrtReciprocalStep(EnumInstruction instruction, EnumRegisterType registerType) {
		this.instruction = instruction;
		this.registerType = registerType;
	}
	
	@Override
	public void bindArguments(Arguments arguments) {
		this.dataType = arguments.getType();
		this.destinationIndex = arguments.getRegisterIndexes().get(0);
		this.source1Index = arguments.getRegisterIndexes().get(1);
		this.source2Index = arguments.getRegisterIndexes().get(2);

		this.size = this.dataType.getSizeInBits();
	}

	@Override
	public void execute(Machine machine) {
		NEONRegisterSet neonRegSet = machine.getNEONRegisterSet();
		int[] source1Parts = DataTypeTools.createPartListFromWords(size, neonRegSet.getRegisterValues(registerType, source1Index));
		int[] source2Parts = DataTypeTools.createPartListFromWords(size, neonRegSet.getRegisterValues(registerType, source2Index));

		int[] resultParts = new int[source1Parts.length];

		if (instruction == EnumInstruction.vrecps) {
			for (int i = 0; i < source1Parts.length; i++) {
				resultParts[i] = calculateReciprocStep(source1Parts[i], source2Parts[i]);
			}
		} else if (instruction == EnumInstruction.vrsqrts) {
			for (int i = 0; i < source1Parts.length; i++) {
				resultParts[i] = calculateReciprocSquareRootStep(source1Parts[i], source2Parts[i]);
			}
		} else
			assert false;

		int[] resultWords = DataTypeTools.createWordsFromOnePartPerWord(size, resultParts);
		neonRegSet.setRegisterValues(registerType, true, destinationIndex, resultWords);
		machine.incrementPCBy4();
		highlightRegisters(machine);
	}

	private int calculateReciprocStep(int op1, int op2) {
		float op1Float = DataTypeTools.intToFloat(op1);
		float op2Float = DataTypeTools.intToFloat(op2);
		int result;
		if (Float.isNaN(op1Float) || Float.isNaN(op2Float)) {
			result = 0x7fc00000;
		} else {
			boolean op1Inf = Float.isInfinite(op1Float);
			boolean op2Inf = Float.isInfinite(op2Float);
			boolean op1Zero = op1Float == 0;
			boolean op2Zero = op2Float == 0;
			float product;
			if ((op1Inf && op2Zero) || (op1Zero && op2Inf)) {
				product = 0.0f;
			} else {
				product = op1Float * op2Float;
			}
			float resultFloat = 2.0f - product;
			result = DataTypeTools.FloatToInt(resultFloat);
		}
		return result;
	}

	private int calculateReciprocSquareRootStep(int op1, int op2) {
		float op1Float = DataTypeTools.intToFloat(op1);
		float op2Float = DataTypeTools.intToFloat(op2);
		int result;
		if (Float.isNaN(op1Float) || Float.isNaN(op2Float)) {
			result = 0x7fc00000;
		} else {
			boolean op1Inf = Float.isInfinite(op1Float);
			boolean op2Inf = Float.isInfinite(op2Float);
			boolean op1Zero = op1Float == 0;
			boolean op2Zero = op2Float == 0;
			float product;
			if ((op1Inf && op2Zero) || (op1Zero && op2Inf)) {
				product = 0.0f;
			} else {
				product = op1Float * op2Float;
			}
			float resultFloat = (3.0f - product) / 2.0f;
			result = DataTypeTools.FloatToInt(resultFloat);
		}
		return result;
	}

	private void highlightRegisters(Machine machine) {
		machine.highlightNEONRegister(registerType, destinationIndex);
	}

	@Override
	public EnumInstruction getInstructionName() {
		return instruction;
	}

	@Override
	public Instruction create() {
		return new ReciprocalSqrtReciprocalStep(this.instruction, this.registerType);
	}

	@Override
	public EnumDataType getDataType() {
		return dataType;
	}
}
