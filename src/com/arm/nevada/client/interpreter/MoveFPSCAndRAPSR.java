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
import com.arm.nevada.client.parser.Arguments;
import com.arm.nevada.client.parser.EnumInstruction;
import com.arm.nevada.client.shared.ARMRegister;
import com.arm.nevada.client.shared.SpecialBits;
import com.arm.nevada.client.utils.DataTypeTools;

public class MoveFPSCAndRAPSR extends Instruction {

	private EnumInstruction instruction;
	private int registerIndex;

	public MoveFPSCAndRAPSR(EnumInstruction instruction) {
		this.instruction = instruction;
	}
	
	@Override
	public void bindArguments(Arguments arguments) {
		this.registerIndex = arguments.getRegisterIndexes().get(0);
		assert instruction == EnumInstruction.vmrs || instruction == EnumInstruction.vmsr;
	}

	@Override
	public void execute(Machine machine) {
		if (instruction == EnumInstruction.vmrs) {

			if (registerIndex != ARMRegister.R15.getIndex()) {
				int fpscr = machine.getSpecialRegisters().getFPSCR();
				machine.getArmRegisterSet().setOneValue(registerIndex, fpscr, true);
				// highligh R
			} else {
				int fpscr = machine.getSpecialRegisters().getFPSCR();
				int apsr = machine.getSpecialRegisters().getAPSR();
				DataTypeTools.setBit(apsr, DataTypeTools.getBit(fpscr, SpecialBits.FPSCR_N), SpecialBits.APSR_N);
				DataTypeTools.setBit(apsr, DataTypeTools.getBit(fpscr, SpecialBits.FPSCR_Z), SpecialBits.APSR_Z);
				DataTypeTools.setBit(apsr, DataTypeTools.getBit(fpscr, SpecialBits.FPSCR_C), SpecialBits.APSR_C);
				DataTypeTools.setBit(apsr, DataTypeTools.getBit(fpscr, SpecialBits.FPSCR_V), SpecialBits.APSR_V);
				machine.getSpecialRegisters().setAPSR(apsr, true);
				// highlight APSR.N.Z.C.V
			}
		} else if (instruction == EnumInstruction.vmsr) {
			int register = machine.getArmRegisterSet().getOneValue(registerIndex);
			machine.getSpecialRegisters().setFPSCR(register, true);
			// highlight FPSCR
		} else
			assert false;
		machine.incrementPCBy4();
		highlightChangedRegisters(machine);
	}

	private void highlightChangedRegisters(Machine machine) {
		if (instruction == EnumInstruction.vmrs) {
			if (registerIndex != ARMRegister.R15.getIndex()) {
				// highligh R
				machine.highlightARMRegister(registerIndex);
			} else {
				// highlight APSR.N.Z.C.V
				// FIXME: Implement
			}
		} else if (instruction == EnumInstruction.vmsr) {
			// highlight FPSCR
			// FIXME: Implement
		} else
			assert false;
	}

	@Override
	public EnumInstruction getInstructionName() {
		return instruction;
	}

	@Override
	public Instruction create() {
		return new MoveFPSCAndRAPSR(this.instruction);
	}

	@Override
	public EnumDataType getDataType() {
		return null;
	}
}
