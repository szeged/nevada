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

public class ZipInstruction extends Instruction {

	private EnumDataType dataType;
	private EnumRegisterType registerType;
	private int source1Index;
	private int source2Index;

	private EnumInstruction instruction;

	public ZipInstruction(EnumInstruction instruction, EnumRegisterType destRegisterType) {
		this.instruction = instruction;
		this.registerType = destRegisterType;
	}
	
	@Override
	public void bindArguments(Arguments arguments) {
		this.dataType = arguments.getType();
		this.source1Index = arguments.getRegisterIndexes().get(0);
		this.source2Index = arguments.getRegisterIndexes().get(1);
	}

	@Override
	public void execute(Machine machine) {
		int size = dataType.getSizeInBits();
		NEONRegisterSet neonRegSet = machine.getNEONRegisterSet();
		int[] source1Parts = DataTypeTools.createPartListFromWords(size, neonRegSet.getRegisterValues(registerType, source1Index));
		int[] source2Parts = DataTypeTools.createPartListFromWords(size, neonRegSet.getRegisterValues(registerType, source2Index));

		if (instruction == EnumInstruction.vzip) {
			zip(source1Parts, source2Parts);
		} else if (instruction == EnumInstruction.vuzp) {
			unzip(source1Parts, source2Parts);
		} else {
			assert false;
		}

		int[] result1Words = DataTypeTools.createWordsFromOnePartPerWord(size, source1Parts);
		int[] result2Words = DataTypeTools.createWordsFromOnePartPerWord(size, source2Parts);
		neonRegSet.setRegisterValues(registerType, true, source1Index, result1Words);
		neonRegSet.setRegisterValues(registerType, true, source2Index, result2Words);
		machine.incrementPCBy4();
		highlightRegisters(machine);
	}

	private void highlightRegisters(Machine machine) {
		machine.highlightNEONRegister(registerType, source1Index);
		machine.highlightNEONRegister(registerType, source2Index);
	}

	/**
	 * The input parameters also the outputs.
	 * 
	 * @param source1
	 * @param source2
	 */
	private void unzip(int[] source1, int[] source2) {
		int[] allOutValue = new int[2 * source1.length];
		for (int i = 0; i < source1.length; i++) {
			allOutValue[i] = source1[i];
			allOutValue[source1.length + i] = source2[i];
		}
		for (int i = 0; i < source1.length; i++) {
			source1[i] = allOutValue[2 * i + 0];
			source2[i] = allOutValue[2 * i + 1];
		}
	}

	/**
	 * The input parameters also the outputs.
	 * 
	 * @param source1
	 * @param source2
	 */
	private void zip(int[] source1, int[] source2) {
		int[] allOutValue = new int[2 * source1.length];
		for (int i = 0; i < source1.length; i++) {
			allOutValue[2 * i + 0] = source1[i];
			allOutValue[2 * i + 1] = source2[i];
		}
		for (int i = 0; i < source1.length; i++) {
			source1[i] = allOutValue[i];
			source2[i] = allOutValue[source1.length + i];
		}
	}

	@Override
	public EnumInstruction getInstructionName() {
		return instruction;
	}

	@Override
	public Instruction create() {
		return new ZipInstruction(this.instruction, this.registerType);
	}

	@Override
	public EnumDataType getDataType() {
		return dataType;
	}
}
