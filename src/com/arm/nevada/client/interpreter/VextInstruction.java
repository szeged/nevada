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

public class VextInstruction extends Instruction {
	private EnumDataType dataType;
	private EnumRegisterType registerType;
	private int source1Index;
	private int source2Index;
	private int destinationIndex;

	private EnumInstruction instruction;

	private int size;
	private int immediateValue;

	public VextInstruction(EnumInstruction instruction, EnumRegisterType registerType) {
		this.instruction = instruction;
		this.registerType = registerType;
		this.size = 8;
	}
	
	@Override
	public void bindArguments(Arguments arguments) {
		this.dataType = arguments.getType();
		this.destinationIndex = arguments.getRegisterIndexes().get(0);
		this.source1Index = arguments.getRegisterIndexes().get(1);
		this.source2Index = arguments.getRegisterIndexes().get(2);

		this.immediateValue = (int) arguments.getImmediateValue() * (dataType.getSizeInBits() / 8);
	}

	@Override
	public void execute(Machine machine) {
		NEONRegisterSet neonRegSet = machine.getNEONRegisterSet();
		int[] source1Parts = DataTypeTools.createPartListFromWords(size, neonRegSet.getRegisterValues(registerType, source1Index));
		int[] source2Parts = DataTypeTools.createPartListFromWords(size, neonRegSet.getRegisterValues(registerType, source2Index));

		int[] resultParts = calculate(source1Parts, source2Parts);

		int[] resultWords = DataTypeTools.createWordsFromOnePartPerWord(size, resultParts);
		neonRegSet.setRegisterValues(registerType, true, destinationIndex, resultWords);
		machine.incrementPCBy4();
		highlightRegisters(machine);
	}

	private void highlightRegisters(Machine machine) {
		machine.highlightNEONRegister(registerType, destinationIndex);
	}

	private int[] calculate(int[] source1Parts, int[] source2Parts) {
		int[] resultParts = new int[source1Parts.length];
		int counter = 0;
		for (int i = immediateValue; i < source1Parts.length; i++, counter++) {
			resultParts[counter] = source1Parts[i];
		}
		for (int i = 0; counter < registerType.getSize() / 8; i++, counter++) {
			resultParts[counter] = source2Parts[i];
		}
		return resultParts;
	}

	@Override
	public EnumInstruction getInstructionName() {
		return instruction;
	}

	@Override
	public Instruction create() {
		return new VextInstruction(this.instruction, this.registerType);
	}

	@Override
	public EnumDataType getDataType() {
		return dataType;
	}
}
