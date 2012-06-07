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

package com.arm.nevada.client.parser;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.arm.nevada.client.interpreter.ErrorInstruction;
import com.arm.nevada.client.interpreter.Instruction;
import com.arm.nevada.client.parser.Token.MSG;

public class Parser {
	private static final Logger logger = Logger.getLogger(Parser.class.getName());
	/**
	 * Parses a user written assembly line.
	 * 
	 * @param line
	 *            One user written assembly code.
	 * @return A parsed, executable instruction filled with the arguments.
	 */
	public static Instruction Parse(String line) {
		Arguments arguments = new Arguments();
		// Locale.ENGLISH
		line = line.trim().replace('\t', ' ').toLowerCase(); // FIXME: real whitespace replace needed

		int pos = 0;
		while (line.length() > pos && (line.charAt(pos) >= 'a' && line.charAt(pos) <= 'z' || line.charAt(pos) >= '0' && line.charAt(pos) <= '9'))
			pos++;
		
		List<InstructionForm> forms;
		try {
			EnumInstruction currentInstruction = EnumInstruction.valueOf(line.substring(0, pos));
			forms = InstructionFormats.get(currentInstruction);
		} catch (Exception e) {
			forms = null;
		}

		if (forms == null) {
			logger.log(Level.FINE, "null: there is no definition for the instruction: " + line.substring(0, pos) + "\n");
			String errorText = "Invalid instruction: " + line.substring(0, pos);
			ErrorInstruction out = new ErrorInstruction(errorText, 0, line);
			return out;
		}

		final int start = pos;
		InstructionForm parsedForm = null;
		MSG best = null;
		for (InstructionForm form : forms) {
			arguments = new Arguments();
			MSG current = new MSG(start, "");
			for (Token format : form.getTokens()) {
				current = format.parse(line, current.getPosition(), arguments);
				if (current.getPosition() < 0) {
					if (best == null || best.getPosition() > current.getPosition()) {
						best = current;
					}
					break;
				}
			}
			if (current.getPosition() >= 0 && current.getPosition() == line.length()) {
				parsedForm = form;
				break;
			}
		}
		if (parsedForm == null) {
			logger.log(Level.FINE, "Can't parse line: " + line);
			ErrorInstruction out = new ErrorInstruction(best.getMessage(), best.getBestParsedPos(), line);
			return out;
		}

		try {
			// GWT.create(parsedForm.getCreate());
			return parsedForm.buildInstruction(arguments);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}