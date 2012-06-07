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

public class VtrnInstruction extends Instruction {

	private EnumDataType dataType;
	private EnumRegisterType registerType;
	private int data1Index;
	private int data2Index;

	public VtrnInstruction(EnumRegisterType registerType) {
		this.registerType = registerType;
	}
	
	@Override
	public void bindArguments(Arguments arguments) {
		this.dataType = arguments.getType();
		this.data1Index = arguments.getRegisterIndexes().get(0);
		this.data2Index = arguments.getRegisterIndexes().get(1);
	}

	@Override
	public void execute(Machine machine) {
		int size = dataType.getSizeInBits();
		NEONRegisterSet neonRegSet = machine.getNEONRegisterSet();
		int[] data1Parts = DataTypeTools.createPartListFromWords(size, neonRegSet.getRegisterValues(registerType, data1Index));
		int[] data2Parts = DataTypeTools.createPartListFromWords(size, neonRegSet.getRegisterValues(registerType, data2Index));

		vtrn(data1Parts, data2Parts);

		int[] result1Words = DataTypeTools.createWordsFromOnePartPerWord(size, data1Parts);
		int[] result2Words = DataTypeTools.createWordsFromOnePartPerWord(size, data2Parts);
		neonRegSet.setRegisterValues(registerType, true, data1Index, result1Words);
		neonRegSet.setRegisterValues(registerType, true, data2Index, result2Words);
		machine.incrementPCBy4();
		highlightRegisters(machine);
	}

	private void vtrn(int[] data1Parts, int[] data2Parts) {
		int save;
		for (int i = 0; i < data2Parts.length; i += 2) {
			save = data1Parts[i + 1];
			data1Parts[i + 1] = data2Parts[i + 0];
			data2Parts[i + 0] = save;
		}
	}

	private void highlightRegisters(Machine machine) {
		machine.highlightNEONRegister(registerType, data1Index);
		machine.highlightNEONRegister(registerType, data2Index);
	}

	@Override
	public EnumInstruction getInstructionName() {
		return EnumInstruction.vtrn;
	}

	@Override
	public Instruction create() {
		return new VtrnInstruction(this.registerType);
	}

	@Override
	public EnumDataType getDataType() {
		return dataType;
	}
}
