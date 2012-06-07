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

import com.arm.nevada.client.parser.Parser;

public class InstructionForView {
	private String text = null;
	private Instruction instruction = null;

	public InstructionForView() {

	}

	public InstructionForView(String text) {
		this.setText(text);
	}

	public InstructionForView(String text, boolean breakpoint) {
		this.setText(text);
		this.setBreakpoint(breakpoint);
	}

	public boolean isBreakpoint() {
		return this.instruction !=null && this.instruction.isBreakpoint();
	}

	public void setBreakpoint(boolean breakpoint) {
		if(this.instruction != null) {
			this.instruction.setBreakpoint(breakpoint);
		}
	}

	/**
	 * @return The containing instruction.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Set the containing instruction's text and parse it.
	 * 
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
		instruction = Parser.Parse(getText());
	}

	public Instruction getInstruction() {
		return instruction;
	}
}