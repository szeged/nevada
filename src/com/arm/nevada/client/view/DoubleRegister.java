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

package com.arm.nevada.client.view;

import com.arm.nevada.client.view.shared.HasMultipleValue;
import com.arm.nevada.client.view.shared.HasRegisterSelectedHandlers;
import com.arm.nevada.client.view.shared.MultipleValuePartChangeEventHandler;
import com.arm.nevada.client.view.shared.MultipleValuePartChangeEvent;
import com.arm.nevada.client.view.shared.RegisterSelectedEvent;
import com.arm.nevada.client.view.shared.RegisterSelectedEventHandler;
import com.arm.nevada.client.view.shared.RegisterViewSettings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Widget;

public class DoubleRegister extends Composite implements HasMultipleValue,
		HasRegisterSelectedHandlers {

	private static NEONRegisterUiBinder uiBinder = GWT
			.create(NEONRegisterUiBinder.class);

	interface NEONRegisterUiBinder extends UiBinder<Widget, DoubleRegister> {
	}

	@UiField(provided = true)
	BasicRegister higherRegister, lowerRegister;
	@UiField
	DeckPanel selector;
	@UiField(provided = true)
	DoubleEditor doubleEditor;
	private BasicRegister[] registers = new BasicRegister[2];
	private int d = 0;
	private RegisterViewSettings registerViewSettings;
	private int[] values = new int[2];
	@SuppressWarnings("unused")
	private EventBus eventBus;

	public DoubleRegister(EventBus eventBus) {
		higherRegister = new BasicRegister(null);
		lowerRegister = new BasicRegister(null);
		doubleEditor = new DoubleEditor(null);
		setEventBus(eventBus);
		initWidget(uiBinder.createAndBindUi(this));
		registers[0] = lowerRegister;
		registers[1] = higherRegister;
		setRegisterViewSettings(new RegisterViewSettings());
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
		lowerRegister.setEventBus(eventBus);
		higherRegister.setEventBus(eventBus);
		doubleEditor.setEventBus(eventBus);
	}

	public void setD(int d) {
		this.d = d;
	}

	public int getD() {
		return d;
	}

	@UiHandler("higherRegister")
	public void onHigherRegisterChanged(ValueChangeEvent<Integer> event) {
		MultipleValuePartChangeEvent forwardEvent = new MultipleValuePartChangeEvent(1, higherRegister.getValue());
		fireEvent(forwardEvent);
		values[1] = event.getValue();
	}

	@UiHandler("lowerRegister")
	public void onLowerRegisterChanged(ValueChangeEvent<Integer> event) {
		MultipleValuePartChangeEvent forwardEvent = new MultipleValuePartChangeEvent(0, lowerRegister.getValue());
		fireEvent(forwardEvent);
		values[0] = event.getValue();
	}

	@UiHandler("doubleEditor")
	protected void onDoubleEditorValueChanged(ValueChangeEvent<int[]> event) {
		MultipleValuePartChangeEvent forwardEvent = new MultipleValuePartChangeEvent(0, event.getValue()[0]);
		fireEvent(forwardEvent);
		forwardEvent = new MultipleValuePartChangeEvent(1, event.getValue()[1]);
		fireEvent(forwardEvent);
		values = event.getValue();
	}

	@UiHandler("higherRegister")
	protected void onHigherRegisterSelected(RegisterSelectedEvent event) {
		fireEvent(new RegisterSelectedEvent(this, event.getNativeEvent()));
	}

	@UiHandler("lowerRegister")
	protected void onLowerRegisterSelected(RegisterSelectedEvent event) {
		fireEvent(new RegisterSelectedEvent(this, event.getNativeEvent()));
	}

	@Override
	public HandlerRegistration addValuePartChangedHandleHandler(MultipleValuePartChangeEventHandler handler) {
		return addHandler(handler, MultipleValuePartChangeEvent.TYPE);
	}

	@Override
	public HandlerRegistration addRegisterSelectedHandler(RegisterSelectedEventHandler handler) {
		return addHandler(handler, RegisterSelectedEvent.TYPE);
	}

	@Override
	public void setAllValue(int[] values) {
		this.values[0] = values[0];
		this.values[1] = values[1];
		lowerRegister.setValue(values[0]);
		higherRegister.setValue(values[1]);
	}

	@Override
	public void setAllValue(int[] values, boolean fireEvent) {
		for (int i = 0; i < 2; i++) {
			setOneValue(i, values[i], fireEvent);
		}
	}

	@Override
	public void setOneValue(int index, int value) {
		assert (index == 0 || index == 1);
		values[index] = value;
		if (registerViewSettings.getSizeInBits() == 64) {
			int[] newValueToPass = new int[2];
			newValueToPass[0] = index == 0 ? value : this.getOneValue(0);
			newValueToPass[1] = index == 1 ? value : this.getOneValue(1);
			doubleEditor.setValue(newValueToPass);
		} else {
			registers[index].setValue(value);
		}
	}

	@Override
	public void setOneValue(int index, int value, boolean fireEvent) {
		setOneValue(index, value);
		if (fireEvent) {
			MultipleValuePartChangeEvent event = new MultipleValuePartChangeEvent(index, value);
			fireEvent(event);
		}
	}

	@Override
	public int[] getAllValue() {
		if (registerViewSettings.getSizeInBits() == 64) {
			return doubleEditor.getValue();
		} else {

			return new int[] {
					lowerRegister.getValue(),
					higherRegister.getValue() };
		}
	}

	@Override
	public int getOneValue(int index) {
		assert (index == 0 || index == 1);
		if (registerViewSettings.getSizeInBits() == 64) {
			return doubleEditor.getValue()[index];
		} else if (index == 0)
			return lowerRegister.getValue();
		else
			return higherRegister.getValue();
	}

	public BasicRegister[] getRegisters() {
		return registers;
	}

	public RegisterViewSettings getRegisterViewSettings() {
		return registerViewSettings;
	}

	public void setRegisterViewSettings(RegisterViewSettings registerViewSettings) {
		this.registerViewSettings = registerViewSettings;
		if (registerViewSettings.getSizeInBits() == 64) {
			set64BitMode(true);

		} else {
			set64BitMode(false);
			registers[0].setRegisterViewSettings(registerViewSettings);
			registers[1].setRegisterViewSettings(registerViewSettings);
		}
	}

	// min 0; max: 2*4=8
	public void highlightBytesAdditive(int from, int to) {
		if (from < 4)
			lowerRegister.highlightBytesAdditive(from, to <= 3 ? to : 3);
		if (to >= 4)
			higherRegister.highlightBytesAdditive(from >= 4 ? from - 4 : 0, to - 4);
		doubleEditor.highlightByValueChanged(true);
	}

	public void removeHighlights() {
		lowerRegister.removeHighlights();
		higherRegister.removeHighlights();
		doubleEditor.highlightByValueChanged(false);
	}

	private void set64BitMode(boolean mode64) {
		this.doubleEditor.setRegisterViewSettings(this.registerViewSettings);
		if (mode64) {
			doubleEditor.setValue(new int[] { values[0], values[1] });
			selector.showWidget(1);
		} else {
			registers[0].setValue(values[0]);
			registers[1].setValue(values[1]);
			selector.showWidget(0);
		}
	}
}
