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

public class TableInstruction extends Instruction {

	private EnumDataType dataType;
	private EnumRegisterType registerType;
	private int destinationRegisterIndex;
	private int indexVectorIndex;
	private int tableRegisterIndex;
	private int tableLengthInRegister;
	private Integer size;
	private EnumInstruction instruction;
	// true when write 0 if he index out of range, false if don't change
	private boolean overwriteElseNotChange;

	public TableInstruction(EnumInstruction instruction, EnumRegisterType registerType, int listElementCount) {
		this.instruction = instruction;
		this.registerType = registerType;
		this.tableLengthInRegister = listElementCount;
		
		initializeFlags();
	}

	private void initializeFlags() {
		switch (instruction) {
		case vtbl:
			overwriteElseNotChange = true;
			break;
		case vtbx:
			overwriteElseNotChange = false;
			break;
		default:
			assert false;
		}
	}
	
	@Override
	public void bindArguments(Arguments arguments) {
		this.dataType = arguments.getType();
		this.destinationRegisterIndex = arguments.getRegisterIndexes().get(0);
		this.indexVectorIndex = arguments.getRegisterIndexes().get(2);
		this.tableRegisterIndex = arguments.getRegisterIndexes().get(1);
		this.size = dataType.getSizeInBits();

		assert (this.registerType == EnumRegisterType.DOUBLE);
		assert (this.size == 8);
	}

	@Override
	public void execute(Machine machine) {
		NEONRegisterSet neonRegSet = machine.getNEONRegisterSet();
		int[] destination = DataTypeTools.createPartListFromWords(size, neonRegSet.getRegisterValues(registerType, destinationRegisterIndex));
		int[] indexVector = DataTypeTools.createPartListFromWords(size, neonRegSet.getRegisterValues(registerType, indexVectorIndex));
		int[] table = new int[tableLengthInRegister * EnumRegisterType.DOUBLE.getSize() / size];
		for (int listI = 0; listI < tableLengthInRegister; listI++) {
			int[] sub = DataTypeTools
					.createPartListFromWords(size, neonRegSet.getRegisterValues(EnumRegisterType.DOUBLE, tableRegisterIndex + listI));
			for (int subI = 0; subI < sub.length; subI++) {
				table[listI * EnumRegisterType.DOUBLE.getSize() / size + subI] = sub[subI];
			}
		}
		int[] resultPartList = calculateTable(indexVector, table, destination);

		int[] resultWords = DataTypeTools.createWordsFromOnePartPerWord(size, resultPartList);
		neonRegSet.setRegisterValues(registerType, true, destinationRegisterIndex, resultWords);
		machine.incrementPCBy4();
		highlightRegisters(machine);
	}

	private void highlightRegisters(Machine machine) {
		machine.highlightNEONRegister(registerType, destinationRegisterIndex);
	}

	/**
	 * 
	 * @param indexVector
	 *            Indexes as part list.
	 * @param table
	 * @return The result as part list.
	 */
	private int[] calculateTable(int[] indexVector, int[] table, int[] destination) {
		int length = table.length;
		int[] result = new int[indexVector.length];
		for (int i = 0; i < indexVector.length; i++) {
			if (indexVector[i] >= length) {
				if (overwriteElseNotChange)
					result[i] = 0;
				else
					result[i] = destination[i];
			}
			else
				result[i] = table[indexVector[i]];
		}
		return result;
	}

	@Override
	public EnumInstruction getInstructionName() {
		return instruction;
	}

	@Override
	public Instruction create() {
		return new TableInstruction(this.instruction, this.registerType, this.tableLengthInRegister);
	}

	@Override
	public EnumDataType getDataType() {
		return dataType;
	}
}
