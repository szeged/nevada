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

import com.arm.nevada.client.shared.SpecialRegiser;
import com.arm.nevada.client.shared.events.SpecialRegisterChangedEvent;
import com.arm.nevada.client.shared.events.SpecialRegisterChangedEventHandler;
import com.google.gwt.event.shared.EventBus;

public class SpecialRegisters extends Storage implements SpecialRegisterChangedEventHandler {
	private static final Logger logger = Logger.getLogger(SpecialRegisters.class.getName());

	public SpecialRegisters(EventBus eventBus) {
		super(2, eventBus);
	}

	public void setOneValue(SpecialRegiser register, int value, boolean fireEvent) {
		setOneValue(register.getIndex(), value, fireEvent);
	}

	public int getAPSR() {
		return this.getOneValue(SpecialRegiser.APSR.getIndex());
	}

	public void setAPSR(int value, boolean fireEvent) {
		this.setOneValue(SpecialRegiser.APSR.getIndex(), value, true);
	}

	public int getFPSCR() {
		return this.getOneValue(SpecialRegiser.FPSCR.getIndex());
	}

	public void setFPSCR(int value, boolean fireEvent) {
		this.setOneValue(SpecialRegiser.FPSCR.getIndex(), value, true);
	}

	@Override
	public void onSpecialRegisterChanged(SpecialRegisterChangedEvent event) {
		if (event.getSource() == this)
			return;
		this.setOneValue(event.getRegister().getIndex(), event.getValue(), true);
	}

	@Override
	protected void fireValueChanged(int index, int value) {
		SpecialRegisterChangedEvent event = new SpecialRegisterChangedEvent(SpecialRegiser.getByIndex(index), value);
		fireEvent(event);
	}

	@Override
	public void subscribeToEventBus() {
		if (eventBus == null) {
			return;
		}
		eventBus.addHandler(SpecialRegisterChangedEvent.TYPE, this);
	}

	@Override
	protected void setOffset(int offsetInWords, boolean fireOffsetChangedEvent) {
		logger.log(Level.WARNING, "Attepmpted to change the offset of the special register set.");
	};
	
	@Override
	protected void fireOffsetChanged(int newOffsetInWords) {
		logger.log(Level.WARNING, "Attepmpted to fire offset changing of the special register set.");
	}
}
