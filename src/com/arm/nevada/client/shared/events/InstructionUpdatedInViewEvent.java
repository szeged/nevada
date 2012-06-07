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

package com.arm.nevada.client.shared.events;

import com.arm.nevada.client.interpreter.Instruction;
import com.google.gwt.event.shared.GwtEvent;

public class InstructionUpdatedInViewEvent extends GwtEvent<InstructionUpdatedInViewEventHandler> {

	public static final Type<InstructionUpdatedInViewEventHandler> TYPE = new Type<InstructionUpdatedInViewEventHandler>();
	private int index;
	private Instruction instruction;

	public InstructionUpdatedInViewEvent(int index, Instruction instruction) {
		this.index = index;
		this.instruction = instruction;
	}

	@Override
	public Type<InstructionUpdatedInViewEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(InstructionUpdatedInViewEventHandler handler) {
		handler.onInstructionUpdatedInView(this);
	}

	public int getIndex() {
		return index;
	}

	public Instruction getInstruction() {
		return instruction;
	}

}
