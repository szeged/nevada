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

public class ReverseInstruction extends Instruction {

	private EnumDataType dataType;
	private EnumRegisterType registerType;
	private Integer destinationIndex;
	private Integer sourceIndex;
	private EnumInstruction instruction;
	private Integer size;
	private int regionSize;

	public ReverseInstruction(EnumInstruction instruction, EnumRegisterType registerType) {
		this.instruction = instruction;
		this.registerType = registerType;
		
		initializeFlags();
	}
	
	private void initializeFlags() {
		switch (this.instruction) {
		case vrev16:
			regionSize = 16;
			break;
		case vrev32:
			regionSize = 32;
			break;
		case vrev64:
			regionSize = 64;
			break;
		default:
			assert false;
			break;
		}
	}
	
	@Override
	public void bindArguments(Arguments arguments) {
		this.dataType = arguments.getType();
		this.destinationIndex = arguments.getRegisterIndexes().get(0);
		this.sourceIndex = arguments.getRegisterIndexes().get(1);
		this.size = dataType.getSizeInBits();
	}

	@Override
	public void execute(Machine machine) {
		int partPerRegion = regionSize / size;
		int regionCount = registerType.getSize() / regionSize;

		NEONRegisterSet neonRegSet = machine.getNEONRegisterSet();
		int[] sourceParts = DataTypeTools.createPartListFromWords(size, neonRegSet.getRegisterValues(registerType, sourceIndex));
		int[] resultParts = new int[sourceParts.length];

		for (int regionI = 0; regionI < regionCount; regionI++) {
			for (int elemI = 0; elemI < partPerRegion; elemI++) {
				resultParts[regionI * partPerRegion + (partPerRegion - elemI - 1)] = sourceParts[regionI * partPerRegion + elemI];
			}
		}

		int[] resultWords = DataTypeTools.createWordsFromOnePartPerWord(size, resultParts);
		neonRegSet.setRegisterValues(registerType, true, destinationIndex, resultWords);
		machine.incrementPCBy4();
		highlightRegisters(machine);
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
		return new ReverseInstruction(this.instruction, this.registerType);
	}

	@Override
	public EnumDataType getDataType() {
		return dataType;
	}
}
