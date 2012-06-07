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

/**
 * Unfortunately the results are not absolutely identical to the real results.
 * It comes from the lack of precision of the DoubleToLong(). Currently it can't be fixed effectively. 
 *
 */
public class ReciprocalSqrtReciprocalEstimate extends Instruction {

	private EnumDataType dataType;
	private EnumRegisterType registerType;
	private int destinationIndex;
	private int sourceIndex;
		
	private EnumInstruction instruction;
	
	private int size;
	
	
	public ReciprocalSqrtReciprocalEstimate(EnumInstruction instruction,EnumRegisterType registerType) {
		this.instruction = instruction;
		this.registerType = registerType;
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
		NEONRegisterSet neonRegSet = machine.getNEONRegisterSet();
		int[] sourceParts = DataTypeTools.createPartListFromWords(size, neonRegSet.getRegisterValues(registerType, sourceIndex));

		int[] resultParts = new int[sourceParts.length];
		if (instruction == EnumInstruction.vrecpe){
			if (dataType == EnumDataType._f32){
				for (int i = 0; i < sourceParts.length; i++) {
					resultParts[i] = calculateReciprocalEstimateFloat(sourceParts[i]);
				}	
			} else if (dataType == EnumDataType._u32){
				for (int i = 0; i < sourceParts.length; i++) {
					resultParts[i] = calculateReciprocEstimateUnsigned32(sourceParts[i]);
				}	
			} else {
				assert false;
			}
		} else if (instruction == EnumInstruction.vrsqrte){
			if (dataType == EnumDataType._f32){
				for (int i = 0; i < sourceParts.length; i++) {
					resultParts[i] = calculateReciprocSqrtEstimateFloat(sourceParts[i]);
				}	
			} else if (dataType == EnumDataType._u32){
				for (int i = 0; i < sourceParts.length; i++) {
					resultParts[i] = calculateReciprocSqrtEstimateUnsigned32(sourceParts[i]);
				}	
			} else {
				assert false;
			}
		}  		

		int[] resultWords = DataTypeTools.createWordsFromOnePartPerWord(size, resultParts);
		neonRegSet.setRegisterValues(registerType, true, destinationIndex, resultWords);
		
		machine.incrementPCBy4();
		highlightRegisters(machine);
	}

	/**
	 * Impemented by the ARM documentation: A2-62  "FPRSqrtEstimate()" 
	 */
	private int calculateReciprocSqrtEstimateFloat(int value) {
 		float floatValue = DataTypeTools.intToFloat(value);
		int result;
		boolean negative = (value & 0x80000000) != 0;
		
		if (Float.isNaN(floatValue)){
			result = 0x7fc00000;
		} else if (floatValue == 0){
			result = floatValue > 0 ? 0x7f800000 : 0xff800000;
		} else if (negative){
			result = 0x7fc00000; 
		} else if (Float.isInfinite(floatValue)){
			result = 0;
		} else {
			long scaled;
			long valueLong = DataTypeTools.LongFromIntegers(value, 0);
			if (DataTypeTools.getBit(value, 23) == false){
				scaled = ((valueLong << 32) & 0x8000000000000000l) | 0x3fe0000000000000l | ((valueLong & 0x7FFFFFl) << 29);
			} else {
				scaled = ((valueLong << 32) & 0x8000000000000000l) | 0x3fd0000000000000l | ((valueLong & 0x7FFFFFl) << 29);
			}
			
			int valueExponent = (value & 0x7f800000) >>> 23;			
			int resultExp = (380 - valueExponent) / 2;
			long estimate = recipSqrtEstimate(scaled);
			
			result = (int)( ((estimate & 0x8000000000000000L) >>> 32) | ((resultExp & 255) << 23) | ((estimate >>> 29) &  0x7FFFFF));
			result++;	// it's a hack due to the lack of precision of recipEstimate(). Remove it after fix.
		}
		return result;
	}

	/**
	 * Impemented by the ARM documentation: A2-62  "UnsignedRSqrtEstimate()" 
	 */
	private int calculateReciprocSqrtEstimateUnsigned32(int value) {
		int result;
		if ((value & 0x3FFFFFFF) == 0)
			result = 0xFFFFFFFF;
		else {
			long longValue = DataTypeTools.LongFromIntegers(value, 0);
			long dp_operand;
			
			if (DataTypeTools.getBit(value, 31) == true){
				dp_operand = 0x3FE0000000000000L | ((longValue & 0x7FFFFFFFL) << 21L);			
			} else {
				dp_operand = 0x3FD0000000000000L | ((longValue & 0x3FFFFFFFL) << 22L);
			}
			long estimate = recipSqrtEstimate(dp_operand);
			result = 0x80000000 | ((int)((estimate & 0xFFFFFFFE00000L) >>> 21));
			result++;	// it's a hack due to the lack of precision of recipEstimate(). Remove it after fix.
		}
		return result;
	}
	
	/**
	 * Impemented by the ARM documentation: A2-62  "recip_sqrt_estimate(double a)" 
	 */
	private long recipSqrtEstimate(long longInput) {
		double doubleInput = DataTypeTools.longToDouble(longInput);
		int q0, q1, s;
		double r;

		if (doubleInput < 0.5){ /* range 0.25 <= a < 0.5 */
			q0 = (int)(doubleInput * 512.0); /* a in units of 1/512 rounded down */
			r = 1.0 / Math.sqrt(((double)q0 + 0.5) / 512.0); /* reciprocal root r */
		} else { /* range 0.5 <= a < 1.0 */
			q1 = (int)(doubleInput * 256.0); /* a in units of 1/256 rounded down */
			r = 1.0 / Math.sqrt(((double)q1 + 0.5) / 256.0); /* reciprocal root r */
		}
		s = (int)(256.0 * r + 0.5); /* r in units of 1/256 rounded to nearest */
		double resultDouble = ((double)s / 256.0);
		long resultLong = DataTypeTools.DoubleToLong(resultDouble);
		return resultLong;
	}

	private void highlightRegisters(Machine machine) {
		machine.highlightNEONRegister(registerType, destinationIndex);
	}

	/**
	 * Impemented by thy ARM documentation: A2-58  "UnsignedRecipEstimate()" 
	 */
	private int calculateReciprocEstimateUnsigned32(int value) {
		int result;
		if (value >= 0)
			result = 0xFFFFFFFF;
		else{
			long longValue = DataTypeTools.LongFromIntegers(value, 0);
			long dp_operand = 0x3FE0000000000000L | ((longValue & 0x7FFFFFFFL) << 21L);
			long estimate = recipEstimate(dp_operand);
			result = 0x80000000 | ((int)((estimate & 0xFFFFFFFE00000L) >>> 21));
			result++;	// it's a hack due to the lack of precision of recipEstimate(). Remove it after fix.
		}
		return result;
	}
	
	/**
	 * Impemented by thy ARM documentation: A2-58  "FPRecipEstimate()"
	 */
	private int calculateReciprocalEstimateFloat(final int value){
 		float floatValue = DataTypeTools.intToFloat(value);
		int result;
		boolean negative = floatValue < 0;
		if (Float.isNaN(floatValue)){
			result = 0x7fc00000;
		} else if (Float.isInfinite(floatValue) || floatValue == 0.0){
			result = floatValue > 0 ? 0x7f800000 : 0xff800000;
		} else if (Math.abs(floatValue) >= Math.pow(2.0, 126)){
			result = negative ? 0x80000000 : 0 ;
		} else {
			long valueLong = DataTypeTools.LongFromIntegers(value, 0);
			long scaled = ((valueLong << 32) & 0x8000000000000000l) | 0x3fe0000000000000l | ((valueLong & 0x7FFFFFl) << 29);
			int resultExp = 253 - DataTypeTools.getFloatExponentRaw(value);
			long estimate = recipEstimate(scaled);
			result = (int)( ((estimate & 0x8000000000000000L) >>> 32) | ((resultExp & 255) << 23) | ((estimate >>> 29) &  0x7FFFFF));
			result++;	// it's a hack due to the lack of precision of recipEstimate().  Remove it after fix.
		}
		return result;
	}
	
	/**
	 * Impemented by thy ARM documentation: A2-58 "recip_estimate()"
	 */
	private long recipEstimate(long value){
		double a = DataTypeTools.longToDouble(value);
		int q, s;
		double r;
		q = (int)(a * 512.0); /* a in units of 1/512 rounded down */
		r = 1.0 / (((double)q + 0.5) / 512.0); /* reciprocal r */
		s = (int)(256.0 * r + 0.5); /* r in units of 1/256 rounded to nearest */
		double doubleResult = (double)s / 256.0;
		long longResult = DataTypeTools.DoubleToLong(doubleResult);
		return longResult;
	}

	@Override
	public EnumInstruction getInstructionName() {
		return instruction;
	}

	@Override
	public Instruction create() {
		return new ReciprocalSqrtReciprocalEstimate(this.instruction, this.registerType);
	}

	@Override
	public EnumDataType getDataType() {
		return dataType;
	}
}
