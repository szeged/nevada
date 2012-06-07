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
import com.arm.nevada.client.view.shared.HasRegisterSelectedHandlers;
import com.arm.nevada.client.view.shared.RegisterSelectedEvent;
import com.arm.nevada.client.view.shared.RegisterSelectedEventHandler;
import com.arm.nevada.client.view.shared.RegisterViewSettings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ArmRegister extends Composite implements
		HasRegisterSelectedHandlers, HasValue<Integer> {
	private static final Logger logger = Logger.getLogger(ArmRegister.class.getName());
	private static ARMRegisterUiBinder uiBinder = GWT.create(ARMRegisterUiBinder.class);

	interface ARMRegisterUiBinder extends UiBinder<Widget, ArmRegister> {
	}

	@UiField(provided = true)
	BasicRegister register;
	@UiField
	Label label;
	@UiField
	ResourceBundle res;
	@UiField
	HorizontalPanel container;
	@SuppressWarnings("unused")
	private EventBus eventBus;

	public ArmRegister(EventBus eventBus) {
		register = new BasicRegister(null);
		setEventBus(eventBus);
		initWidget(uiBinder.createAndBindUi(this));
		subscribeEvents();
	}

	public ArmRegister() {
		this(null);
	}

	private void subscribeEvents() {
		// selected events
		final ArmRegister that = this;
		ClickHandler armClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				logger.log(Level.FINE, "Click on ARM register, Ctrl hold: " + event.getNativeEvent().getCtrlKey());
				fireEvent(new RegisterSelectedEvent(that, event.getNativeEvent()));
			}
		};
		container.addDomHandler(armClickHandler, ClickEvent.getType());
	}

	private int index;

	@UiHandler("register")
	protected void onRegisterSelected(RegisterSelectedEvent event) {
		fireEvent(new RegisterSelectedEvent(this, event.getNativeEvent()));
		// eventBus.fireEvent(new RegisterSelectedEvent(this));
	}

	@UiHandler("register")
	protected void onRegisterValueChanged(ValueChangeEvent<Integer> event) {
		ValueChangeEvent.fire(this, getValue());
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

	@Override
	public HandlerRegistration addRegisterSelectedHandler(
			RegisterSelectedEventHandler handler) {
		return addHandler(handler, RegisterSelectedEvent.TYPE);
		// return eventBus.addHandler(RegisterSelectedEvent.TYPE, handler);
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setLabelByIndex(int index) {
		setLabel("R" + index);
	}

	public int getIndex() {
		return index;
	}

	public void setLabel(String label) {
		this.label.setText(label);
	}

	@Override
	public Integer getValue() {
		return register.getValue();
	}

	@Override
	public void setValue(Integer value) {
		register.setValue(value);
	}

	@Override
	public void setValue(Integer value, boolean fireEvents) {
		setValue(value);
		if (fireEvents)
			ValueChangeEvent.fire(this, value);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Integer> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public RegisterViewSettings getRegisterViewSettings() {
		return register.getRegisterViewSettings();
	}

	public void setRegisterViewSettings(RegisterViewSettings registerViewSettings) {
		this.register.setRegisterViewSettings(registerViewSettings);
		register.setRegisterViewSettings(registerViewSettings);
	}

	public void highlightByValueChanged() {
		register.highlightBytesAdditive(0, 3);
	}

	public void removeHighlights() {
		register.removeHighlights();
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
		register.setEventBus(eventBus);

	}

}
