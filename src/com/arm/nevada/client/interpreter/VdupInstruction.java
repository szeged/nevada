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

public class VdupInstruction extends Instruction {

	private Arguments arguments;
	private EnumInstruction instructionName;
	private boolean fromArm;
	private EnumRegisterType destinationRegisterType;

	public VdupInstruction(boolean fromArm, EnumRegisterType registerType) {
		this.instructionName = EnumInstruction.vdup;
		this.fromArm = fromArm;
		this.destinationRegisterType = registerType;
	}
	
	@Override
	public void bindArguments(Arguments arguments) {
		this.arguments = arguments;
	}

	@Override
	public void execute(Machine machine) {
		int size = arguments.getType().getSizeInBits();
		int sourceValue;
		
		if (this.fromArm) {
			int wholeValue = machine.getArmRegisterSet().getOneValue(arguments.getRegisterIndexes().get(1));
			// The source element is the least significant 8, 16, or 32 bits of
			// the ARM core register
			sourceValue = DataTypeTools.getParts(size, wholeValue)[0];
		} else {
			int[] wholeValue = machine.getNEONRegisterSet().getDouble(arguments.getRegisterIndexes().get(1));
			int inWord = size * arguments.getSubRegisterIndex() < 32 ? 0 : 1;
			int indexInWord = arguments.getSubRegisterIndex() - inWord * (32 / size);
			sourceValue = DataTypeTools.getParts(size, wholeValue[inWord])[indexInWord];
		}

		int[] oneWordParts = new int[32 / size];
		for (int i = 0; i < oneWordParts.length; i++) {
			oneWordParts[i] = sourceValue;
		}
		int oneWordValue = DataTypeTools.createByParts(oneWordParts);

		if (this.destinationRegisterType == EnumRegisterType.DOUBLE) {
			machine.getNEONRegisterSet().setDouble(arguments.getRegisterIndexes().get(0), true, oneWordValue, oneWordValue);
		} else {
			machine.getNEONRegisterSet().setQuad(arguments.getRegisterIndexes().get(0), true, oneWordValue, oneWordValue, oneWordValue, oneWordValue);
		}
		machine.highlightNEONRegister(this.destinationRegisterType, arguments.getRegisterIndexes().get(0));
		machine.incrementPCBy4();
	}

	@Override
	public VdupInstruction create() {
		return new VdupInstruction(this.fromArm, this.destinationRegisterType);
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
