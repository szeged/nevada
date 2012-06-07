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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.arm.nevada.client.view.design.ResourceBundle;
import com.arm.nevada.client.view.shared.HasMultipleValue;
import com.arm.nevada.client.view.shared.HasRegisterSelectedHandlers;
import com.arm.nevada.client.view.shared.MultipleValuePartChangeEventHandler;
import com.arm.nevada.client.view.shared.MultipleValuePartChangeEvent;
import com.arm.nevada.client.view.shared.RegisterSelectedEvent;
import com.arm.nevada.client.view.shared.RegisterSelectedEventHandler;
import com.arm.nevada.client.view.shared.RegisterViewSettings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MemoryDoubleElement extends Composite implements
		HasRegisterSelectedHandlers, HasMultipleValue {
	private static final Logger logger = Logger.getLogger(MemoryDoubleElement.class.getName());
	@UiField
	Label label;
	@UiField
	HTMLPanel container;
	@UiField(provided = true)
	DoubleRegister doubleRegister;
	@UiField
	ResourceBundle res;
	@SuppressWarnings("unused")
	private EventBus eventBus;
	private static MemoryDoubleElementUiBinder uiBinder = GWT
			.create(MemoryDoubleElementUiBinder.class);

	interface MemoryDoubleElementUiBinder extends
			UiBinder<Widget, MemoryDoubleElement> {
	}

	public MemoryDoubleElement(EventBus eventBus) {
		doubleRegister = new DoubleRegister(null);
		setEventBus(eventBus);
		initWidget(uiBinder.createAndBindUi(this));
		subscribeEvents();
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
		doubleRegister.setEventBus(eventBus);
	}

	private void subscribeEvents() {
		// selected events
		final MemoryDoubleElement that = this;
		ClickHandler a = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				logger.log(Level.FINE, "Click on Memory Double Element.");
				fireEvent(new RegisterSelectedEvent(that, event.getNativeEvent()));
			}
		};
		container.addDomHandler(a, ClickEvent.getType());
	}

	@UiHandler("doubleRegister")
	void onDoubleRegisterSelected(RegisterSelectedEvent event) {
		RegisterSelectedEvent forwardEvent = new RegisterSelectedEvent(this, event.getNativeEvent());
		fireEvent(forwardEvent);
	}

	@UiHandler("doubleRegister")
	void onBmeg(MultipleValuePartChangeEvent event) {
		MultipleValuePartChangeEvent forwardEvent = new MultipleValuePartChangeEvent(event.getOffset(), event.getValue());
		fireEvent(forwardEvent);
	}

	public void setSelected(String highlighted) {
		setSelected(Boolean.parseBoolean(highlighted));
	}

	public void setSelected(boolean selected) {
		if (selected)
			this.addStyleName(res.style().selected());
		else
			this.removeStyleName(res.style().selected());
	}

	public void setD(int d) {
		doubleRegister.setD(d);
		setLabel(d);
	}

	public int getD() {
		return doubleRegister.getD();
	}

	private void setLabel(int address) {
		label.setText((8 * address + 7) + ".." + (8 * address));
	}

	@Override
	public HandlerRegistration addValuePartChangedHandleHandler(
			MultipleValuePartChangeEventHandler handler) {
		return addHandler(handler, MultipleValuePartChangeEvent.TYPE);
	}

	@Override
	public void setAllValue(int[] values) {
		doubleRegister.setAllValue(values);
	}

	@Override
	public void setAllValue(int[] values, boolean fireEvent) {
		for (int i = 0; i < 2; i++) {
			setOneValue(i, values[i], fireEvent);
		}
	}

	@Override
	public void setOneValue(int offset, int value) {
		doubleRegister.setOneValue(offset, value);
	}

	@Override
	public void setOneValue(int index, int value, boolean fireEvent) {
		doubleRegister.setOneValue(index, value, fireEvent);
	}

	@Override
	public int[] getAllValue() {
		return doubleRegister.getAllValue();
	}

	@Override
	public int getOneValue(int index) {
		return doubleRegister.getOneValue(index);
	}

	@Override
	public HandlerRegistration addRegisterSelectedHandler(
			RegisterSelectedEventHandler handler) {
		return addHandler(handler, RegisterSelectedEvent.TYPE);
	}

	public void setRegisterViewSettings(RegisterViewSettings type) {
		doubleRegister.setRegisterViewSettings(type);
	}

	public RegisterViewSettings getRegisterViewSettings() {
		return doubleRegister.getRegisterViewSettings();
	}

	public void highlightBytesAdditive(int from, int to) {
		doubleRegister.highlightBytesAdditive(from, to);
	}

	public void removeHighlights() {
		doubleRegister.removeHighlights();
	}
}
