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

public class CountInstruction extends Instruction {

	private EnumDataType dataType;
	private EnumRegisterType registerType;
	private int sourceIndex;
	private int destinationIndex;

	private EnumInstruction instruction;

	private int size;

	public CountInstruction(EnumInstruction instruction, EnumRegisterType destinationRegisterType) {
		this.instruction = instruction;
		this.registerType = destinationRegisterType;
	}
	
	@Override
	public void bindArguments(Arguments arguments) {		
		dataType = arguments.getType();
		destinationIndex = arguments.getRegisterIndexes().get(0);
		sourceIndex = arguments.getRegisterIndexes().get(1);

		size = dataType.getSizeInBits();
	}

	@Override
	public void execute(Machine machine) {
		NEONRegisterSet neonRegSet = machine.getNEONRegisterSet();
		int[] sourceParts = DataTypeTools.createPartListFromWords(size, neonRegSet.getRegisterValues(registerType, sourceIndex));
		int[] resultParts = new int[sourceParts.length];

		if (instruction == EnumInstruction.vcls) {
			for (int i = 0; i < sourceParts.length; i++) {
				resultParts[i] = countLeadingSignBits(sourceParts[i]);
			}
		} else if (instruction == EnumInstruction.vclz) {
			for (int i = 0; i < sourceParts.length; i++) {
				resultParts[i] = countLeadingZeros(sourceParts[i]);
			}
		} else if (instruction == EnumInstruction.vcnt) {
			for (int i = 0; i < sourceParts.length; i++) {
				resultParts[i] = countSetBits(sourceParts[i]);
			}
		} else
			assert false;
		int[] resultWords = DataTypeTools.createWordsFromOnePartPerWord(size, resultParts);
		neonRegSet.setRegisterValues(registerType, true, destinationIndex, resultWords);
		machine.incrementPCBy4();
		highlightRegisters(machine);
	}

	private void highlightRegisters(Machine machine) {
		machine.highlightNEONRegister(registerType, destinationIndex);
	}

	private int countLeadingSignBits(int source) {
		if ((source << 32 - size) < 0)
			source = ~source;
		int result = countLeadingZeros(source) - 1;
		return result;
	}

	private int countLeadingZeros(int source) {
		source = source & DataTypeTools.getBitmask(size);
		int timesShifted;
		for (timesShifted = 0; source != 0; timesShifted++) {
			source = source >>> 1;
		}
		int result = size - timesShifted;
		return result;
	}

	private int countSetBits(int source) {
		source = source & DataTypeTools.getBitmask(size);
		int count = 0;
		for (int i = 0; i < size; i++) {
			count += source % 2;
			source = source >>> 1;
		}
		return count;
	}

	@Override
	public EnumInstruction getInstructionName() {
		return instruction;
	}

	@Override
	public Instruction create() {
		return new CountInstruction(this.instruction, this.registerType);
	}

	@Override
	public EnumDataType getDataType() {
		return dataType;
	}
}
