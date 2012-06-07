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

package com.arm.nevada.client.interpreter.machine;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.arm.nevada.client.shared.ARMRegister;
import com.arm.nevada.client.shared.events.ARMRegisterValueChangedEvent;
import com.arm.nevada.client.shared.events.ARMRegisterValueChangedEventHandler;
import com.arm.nevada.client.shared.events.visualize.ProgramCounterChangedEvenet;
import com.google.gwt.event.shared.EventBus;

public class ARMRegisterSet extends Storage implements ARMRegisterValueChangedEventHandler {
	private static final Logger logger = Logger.getLogger(ARMRegisterSet.class.getName());

	public ARMRegisterSet(final EventBus eventBus) {
		super(16, eventBus);
	}

	@Override
	public void onElementChanged(ARMRegisterValueChangedEvent event) {
		if (event.getSource() == this){
			return;
		}
		setOneValue(event.getOffset(), event.getValue(), false);
	}

	@Override
	public void setOneValue(int index, int value, boolean fireEvent) {
		super.setOneValue(index, value, fireEvent);
		if (index == ARMRegister.R15.getIndex()) {
			fireEvent(new ProgramCounterChangedEvenet(value));
		}
	}

	@Override
	protected void fireValueChanged(int index, int value) {
		ARMRegisterValueChangedEvent forwardEvent = new ARMRegisterValueChangedEvent(index, value);
		if (index == ARMRegister.R15.getIndex()){
			ProgramCounterChangedEvenet PCChanged = new ProgramCounterChangedEvenet(value);
			fireEvent(PCChanged);
		}
		fireEvent(forwardEvent);
	}

	@Override
	public void subscribeToEventBus() {
		if (eventBus == null){
			return;
		}
		eventBus.addHandler(ARMRegisterValueChangedEvent.TYPE, this);
	}

	@Override
	protected void setOffset(int offsetInWords, boolean fireOffsetChangedEvent) {
		logger.log(Level.WARNING, "Attepmpted to change the offset of the ARM register set.");
	};
	
	@Override
	protected void fireOffsetChanged(int newOffsetInWords) {
		logger.log(Level.WARNING, "Attepmpted to fire offset changing of the ARM register set.");
	}
}
