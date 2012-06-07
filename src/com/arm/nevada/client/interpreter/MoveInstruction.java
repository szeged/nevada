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
import com.arm.nevada.client.shared.SpecialBits;
import com.arm.nevada.client.utils.DataTypeTools;

public class MoveInstruction extends Instruction {
	private EnumRegisterType destRegisterType, sourceRegisterType;
	private Integer destinationIndex, sourceIndex;
	private EnumDataType dataType;
	private EnumInstruction instruction;
	private boolean immediate;
	private Long immedateValue;

	private boolean narrow;
	private boolean longing;
	private boolean saturating;
	private boolean forceUnsignedSaturating;
	private boolean negate;

	public MoveInstruction(EnumInstruction instruction, EnumRegisterType destinationRegisterType, boolean immediate) {
		this.instruction = instruction;
		this.destRegisterType = destinationRegisterType;
		this.immediate = immediate;
		
		initializeFlags();
	}
	
	private void initializeFlags() {
		switch (this.instruction) {
		case vmov:
			break;
		case vqmovn:
			saturating = true;
			narrow = true;
			break;
		case vqmovun:
			forceUnsignedSaturating = true;
			saturating = true;
			narrow = true;
			break;
		case vmovn:
			narrow = true;
			break;
		case vmovl:
			longing = true;
			break;
		case vmvn:
			negate = true;
			break;
		default:
			assert false;
			break;
		}
		
		if (this.immediate) {
			this.sourceRegisterType = null;
		} else {
			if (narrow) {
				this.sourceRegisterType = EnumRegisterType.QUAD;
			} else if (longing) {
				this.sourceRegisterType = EnumRegisterType.DOUBLE;
			} else { // both immediate and neon-neon
				this.sourceRegisterType = this.destRegisterType;
			}
		}
	}
	
	@Override
	public void bindArguments(Arguments arguments) {
		this.destinationIndex = arguments.getRegisterIndexes().get(0);

		if (this.immediate) {
			this.immedateValue = arguments.getImmediateValue();
		} else {
			this.sourceIndex = arguments.getRegisterIndexes().get(1);
		}

		this.dataType = arguments.getType();
		if (this.dataType == null) {
			this.dataType = EnumDataType._64;
		}
	}

	@Override
	public void execute(Machine machine) {
		int sourceSize = dataType.getSizeInBits();
		int destSize;
		if (narrow)
			destSize = sourceSize / 2;
		else if (longing)
			destSize = sourceSize * 2;
		else
			destSize = sourceSize;
		NEONRegisterSet neonRS = machine.getNEONRegisterSet();

		long[] source1Parts;
		if (immediate) {
			long[] immParts = DataTypeTools.createPartListFromWordsLong(sourceSize, DataTypeTools.integerFromLong(immedateValue));
			source1Parts = new long[destRegisterType.getSize() / sourceSize];
			for (int i = 0; i < source1Parts.length; i++) {
				source1Parts[i] = immParts[i % (64 / sourceSize)];
			}
		} else {
			source1Parts = DataTypeTools.createPartListFromWordsLong(sourceSize, neonRS.getRegisterValues(sourceRegisterType, sourceIndex));
		}

		long[] resultParts = new long[source1Parts.length];

		for (int i = 0; i < resultParts.length; i++) {
			resultParts[i] = calculate(source1Parts[i], machine);
		}

		int[] resultWords = DataTypeTools.createWordsFromOnePartPerLong(destSize, resultParts);
		neonRS.setRegisterValues(destRegisterType, true, destinationIndex, resultWords);
		machine.incrementPCBy4();
		highlightChangedRegisters(machine);
	}

	private long calculate(long l, Machine machine) {
		int sourceSize = dataType.getSizeInBits();
		int destSize;
		if (narrow)
			destSize = sourceSize / 2;
		else if (longing)
			destSize = sourceSize * 2;
		else
			destSize = sourceSize;
		if (longing) {
			assert dataType.getSigned() != null;
			if (dataType.getSigned())
				l = DataTypeTools.extendToSingnedLong(l, sourceSize);
		} else if (narrow && saturating) {
			assert dataType.getSigned() != null;
			if (dataType.getSigned())
				l = DataTypeTools.extendToSingnedLong(l, sourceSize);
			boolean saturated = false;
			if (dataType.getSigned() == false) {
				// unsigned
				if (DataTypeTools.isSaturatingLong(l, destSize, false)) {
					l = DataTypeTools.getSaturatingLong(l, destSize, false);
					saturated = true;
				}
			} else {
				// signed
				if (forceUnsignedSaturating) {
					assert dataType.getSigned() == true;
					if (l < 0) {
						l = 0;
						saturated = true;
					} else if (l > DataTypeTools.getBitmaskLong(destSize)/* dest unsigned max */) {
						l = DataTypeTools.getBitmaskLong(destSize);
						saturated = true;
					}
				} else {
					if (DataTypeTools.isSaturatingLong(l, destSize, dataType.getSigned())) {
						l = DataTypeTools.getSaturatingLong(l, destSize, dataType.getSigned());
						saturated = true;
					}
				}
			}
			if (saturated) {
				int fpscr = machine.getSpecialRegisters().getFPSCR();
				fpscr = DataTypeTools.setBit(fpscr, true, SpecialBits.FPSCR_QC);
				machine.getSpecialRegisters().setFPSCR(fpscr, true);
			}

		}
		else if (negate) {
			l = ~l;
		}
		long mask = DataTypeTools.getBitmaskLong(destSize);
		l = l & mask;
		return l;
	}

	private void highlightChangedRegisters(Machine machine) {
		machine.highlightNEONRegister(destRegisterType, destinationIndex);
	}

	@Override
	public EnumInstruction getInstructionName() {
		return instruction;
	}

	@Override
	public Instruction create() {
		return new MoveInstruction(this.instruction, this.destRegisterType, this.immediate);
	}

	@Override
	public EnumDataType getDataType() {
		return dataType;
	}
}
