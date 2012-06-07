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

import com.arm.nevada.client.shared.events.information.NumberFormatErrorEvent;
import com.arm.nevada.client.utils.DataTypeTools;
import com.arm.nevada.client.view.design.ResourceBundle;
import com.arm.nevada.client.view.shared.RegisterViewSettings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DoubleEditor extends Composite implements HasValue<int[]> {

	private static DoubleEditorUiBinder uiBinder = GWT.create(DoubleEditorUiBinder.class);

	interface DoubleEditorUiBinder extends UiBinder<Widget, DoubleEditor> {
	}

	@UiField
	TextBox doubleTextBox;
	@UiField
	ResourceBundle res;
	private int[] values = new int[2];
	private RegisterViewSettings registerViewSettings;
	private boolean valid = true;
	private EventBus eventBus;

	public DoubleEditor(EventBus eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		setEventBus(eventBus);
	}

	@UiHandler("doubleTextBox")
	void onErrorInformaionRequestByFocus(FocusEvent event) {
		fireErrorEventIfNecessary((TextBox) event.getSource());
	}

	void fireErrorEventIfNecessary(TextBox source) {
		if (!this.valid) {
			fireEventToEventBus(new NumberFormatErrorEvent(registerViewSettings, source.getText()));
		}
	}

	@UiHandler("doubleTextBox")
	void onEditorValueChanged(ValueChangeEvent<String> event) {
		if (event.getValue().isEmpty()) {
			setValue(getValue());
			return;
		}
		Long newValue = DataTypeTools.parseFormattedLongString(
				event.getValue(),
				true,
				registerViewSettings.getNumberFormat().getRadix());
		if (newValue == null) {
			setValid(false);
			this.setValueLeaveInvalids(new int[] { 0, 0 }, true);
			// setValue(new int[] { 0, 0 }, true);
			fireErrorEventIfNecessary(doubleTextBox);
		} else {
			setValid(true);
			setValueLeaveInvalids(DataTypeTools.integerFromLong(newValue), true);
		}
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public void fireEventToEventBus(GwtEvent<?> event) {
		if (this.eventBus != null) {
			eventBus.fireEvent(event);
		}
	}

	private void setValid(boolean valid) {
		this.valid = valid;
		if (valid) {
			doubleTextBox.removeStyleName(res.style().invalidNumber());
		} else {
			doubleTextBox.addStyleName(res.style().invalidNumber());
		}
	}

	public RegisterViewSettings getRegisterViewSettings() {
		return registerViewSettings;
	}

	public void setRegisterViewSettings(RegisterViewSettings registerViewSettings) {
		this.registerViewSettings = registerViewSettings;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<int[]> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public int[] getValue() {
		return new int[] { values[0], values[1] };
	}

	private void setValueLeaveInvalids(int[] value, boolean fireEvent) {
		this.values[0] = value[0];
		this.values[1] = value[1];
		updateViewValue(false);
		if (fireEvent) {
			ValueChangeEvent.fire(this, value);
		}
	}

	@Override
	public void setValue(int[] value) {
		this.values[0] = value[0];
		this.values[1] = value[1];
		updateViewValue(true);
	}

	@Override
	public void setValue(int[] value, boolean fireEvents) {
		setValue(value);
		if (fireEvents)
			ValueChangeEvent.fire(this, new int[] { values[0], values[1] });
	}

	private void updateViewValue(boolean modifyInvalid) {
		if (modifyInvalid || valid) {
			long longValue = DataTypeTools.LongFromIntegers(values[0], values[1]);
			doubleTextBox.setText(DataTypeTools.formatLong(longValue, true, registerViewSettings.getNumberFormat().getRadix()));
		}
		if (modifyInvalid && !valid) {
			setValid(true);
		}
	}

	public void highlightByValueChanged(boolean highlight) {
		if (highlight)
			this.doubleTextBox.addStyleName(res.style().valueJustChanged());
		else
			this.doubleTextBox.removeStyleName(res.style().valueJustChanged());
	}

}
