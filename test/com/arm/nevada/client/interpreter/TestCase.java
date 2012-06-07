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

import java.util.LinkedList;
import java.util.List;

import com.arm.nevada.client.interpreter.Instruction;
import com.arm.nevada.client.parser.Parser;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

public class TestCase {
	private String inputMachine;
	private String outputMachine;
	private List<Instruction> inputInstructions = new LinkedList<Instruction>();
	private final int startLine;

	/**
	 * inoutmachine, outputmachine, code, code, code...
	 * 
	 * @param lines
	 * @param startLine
	 */
	TestCase(String[] lines, int startLine) {

		this.startLine = startLine;
		inputMachine = lines[0];
		// outputMachine = JSONParser.parseStrict(lines[1]).isObject();
		outputMachine = lines[1];
		for (int i = 2; i < lines.length; i++) {
			getInputInstructions().add(Parser.Parse(lines[i]));
		}
	}

	public JSONObject getInputMachineAsJSON() {
		return JSONParser.parseStrict(inputMachine).isObject();
	}

	public String getExpectedOutputMachine() {
		return outputMachine;
	}

	public List<Instruction> getInputInstructions() {
		return inputInstructions;
	}

	public String getInputMachine() {
		return inputMachine;
	}

	public int getStartLine() {
		return startLine;
	}
}
