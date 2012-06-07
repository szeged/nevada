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

public class VswpInstruction extends Instruction {

	private EnumDataType dataType;
	private EnumRegisterType registerType;
	private int data1Index;
	private int data2Index;

	public VswpInstruction(EnumRegisterType registerType) {
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
		NEONRegisterSet neonRegSet = machine.getNEONRegisterSet();
		int[] values1 = neonRegSet.getRegisterValues(registerType, data1Index);
		int[] values2 = neonRegSet.getRegisterValues(registerType, data2Index);

		neonRegSet.setRegisterValues(registerType, true, data1Index, values2);
		neonRegSet.setRegisterValues(registerType, true, data2Index, values1);

		machine.incrementPCBy4();
		highlightRegisters(machine);
	}

	private void highlightRegisters(Machine machine) {
		machine.highlightNEONRegister(registerType, data1Index);
		machine.highlightNEONRegister(registerType, data2Index);
	}

	@Override
	public EnumInstruction getInstructionName() {
		return EnumInstruction.vswp;
	}

	@Override
	public Instruction create() {
		return new VswpInstruction(this.registerType);
	}

	@Override
	public EnumDataType getDataType() {
		return dataType;
	}
}
