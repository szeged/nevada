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
import com.arm.nevada.client.parser.EnumRegisterType;
import com.arm.nevada.client.utils.DataTypeTools;

public class VmovInstruction extends Instruction {
	public static enum Mode {
		ARM_TO_DSUB,
		DSUB_TO_ARM,
		ARM_TO_D,
		D_TO_ARM,
		IMM_TO_Q,
		IMM_TO_D;
	}

	private Arguments arguments;
	private EnumInstruction instructionName;
	private Mode mode;

	public VmovInstruction(Mode mode) {
		this.instructionName = EnumInstruction.vmov;
		this.mode = mode;
	}

	@Override
	public void bindArguments(Arguments arguments) {
		this.arguments = arguments;
		if (this.mode == Mode.ARM_TO_DSUB && arguments.getType() == null) {
			this.arguments.setType(EnumDataType._32);
		}
	}

	@Override
	public void execute(Machine machine) {
		int from = -1;
		int to = arguments.getRegisterIndexes().get(0);
		if (arguments.getRegisterIndexes().size() >= 2)
			from = arguments.getRegisterIndexes().get(1);
		EnumDataType dataType = arguments.getType();
		// int typeSize = arguments.getType().getSizeInBits();
		switch (mode) {
		case ARM_TO_D:
			// VMOV<c><q> <Dm>, <Rt>, <Rt2>
			int[] ARMValues = new int[2];
			ARMValues[0] = machine.getArmRegisterSet().getOneValue(arguments.getRegisterIndexes().get(1));
			ARMValues[1] = machine.getArmRegisterSet().getOneValue(arguments.getRegisterIndexes().get(2));
			machine.getNEONRegisterSet().setDouble(to, true, ARMValues);
			machine.highlightNEONRegister(EnumRegisterType.DOUBLE, to);
			break;
		case D_TO_ARM:
			// VMOV<c><q> <Rt>, <Rt2>, <Dm>
			int[] doubleValue = machine.getNEONRegisterSet().getDouble(arguments.getRegisterIndexes().get(2));
			machine.getArmRegisterSet().setOneValue(arguments.getRegisterIndexes().get(0), doubleValue[0], true);
			machine.getArmRegisterSet().setOneValue(arguments.getRegisterIndexes().get(1), doubleValue[1], true);
			machine.highlightARMRegister(arguments.getRegisterIndexes().get(0));
			machine.highlightARMRegister(arguments.getRegisterIndexes().get(1));
			break;
		case ARM_TO_DSUB:
			// VMOV<c>{.<size>} <Dd[x]>, <Rt>
			// <size> 8 16 32; if omitted: 32
			int armValue = machine.getArmRegisterSet().getOneValue(from);
			int newPart = DataTypeTools.getParts(dataType.getSizeInBits(), armValue)[0];
			int subIndex = arguments.getSubRegisterIndex();
			machine.getNEONRegisterSet().setSubRegister(
					EnumRegisterType.DOUBLE,
					dataType.getSizeInBits(),
					to,
					subIndex,
					newPart);
			machine.highlightNEONSubregister(EnumRegisterType.DOUBLE, dataType.getSizeInBits(), to, subIndex);
			break;
		case DSUB_TO_ARM:
			// VMOV<c>{.<dt>} <Rt>, <Dn[x]>
			// <dt> s8 s16 u8 u16 32; omitted: 32
			int subValue = machine.getNEONRegisterSet().getSubRegister(
					EnumRegisterType.DOUBLE, dataType.getSizeInBits(), from, arguments.getSubRegisterIndex());
			if (dataType.getSizeInBits() != 32 && dataType.getSigned() == true) {
				subValue = DataTypeTools.extendSignedToInt(subValue, dataType.getSizeInBits());
			}
			machine.getArmRegisterSet().setOneValue(to, subValue, true);
			machine.highlightARMRegister(to);
			break;
		}
		machine.incrementPCBy4();
	}

	@Override
	public VmovInstruction create() {
		return new VmovInstruction(this.mode);
	}

	@Override
	public EnumInstruction getInstructionName() {
		return instructionName;
	}

	@Override
	public EnumDataType getDataType() {
		return arguments.getType();
	}
}
