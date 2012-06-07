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

import java.util.List;

import com.arm.nevada.client.interpreter.Instruction;
import com.google.gwt.event.shared.GwtEvent;

public class AllInstructionReplacedInViewEvent extends GwtEvent<AllInstructionReplacedInViewEventHandler> {

	public static final Type<AllInstructionReplacedInViewEventHandler> TYPE = new Type<AllInstructionReplacedInViewEventHandler>();
	private List<Instruction> instructionList;

	public AllInstructionReplacedInViewEvent(List<Instruction> instructionList) {
		this.instructionList = instructionList;
	}

	@Override
	public GwtEvent.Type<AllInstructionReplacedInViewEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AllInstructionReplacedInViewEventHandler handler) {
		handler.onAllInstructionReplacedInView(this);
	}

	public List<Instruction> getInstructionList() {
		return instructionList;
	}

}
