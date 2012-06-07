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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.arm.nevada.client.shared.events.ClearInformationsEvent;
import com.arm.nevada.client.shared.events.information.NumberFormatErrorEvent;
import com.arm.nevada.client.utils.DataTypeTools;
import com.arm.nevada.client.view.design.ResourceBundle;
import com.arm.nevada.client.view.shared.HasRegisterSelectedHandlers;
import com.arm.nevada.client.view.shared.RegisterSelectedEvent;
import com.arm.nevada.client.view.shared.RegisterSelectedEventHandler;
import com.arm.nevada.client.view.shared.RegisterViewSettings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class BasicRegister extends Composite implements HasValue<Integer>,
		HasRegisterSelectedHandlers {
	private static final Logger logger = Logger.getLogger(BasicRegister.class.getName());
	@UiField
	TextBox edit8_3, edit8_2, edit8_1, edit8_0, edit16_1, edit16_0, edit32_0;
	@UiField
	DeckPanel deckTypeSelector;
	@UiField
	ResourceBundle res;
	private int value;
	private boolean[] invalidBoxes;
	private RegisterViewSettings registerViewSettings;
	private Set<TextBox> highlighteds = new HashSet<TextBox>();

	private TextBox[] textBoxes;
	private TextBox[] textBoxes4;
	private TextBox[] textBoxes2;
	private TextBox[] textBoxes1;
	private TextBox[] activeTextBoxes;
	private EventBus eventBus;

	private static BasicRegisterUiBinder uiBinder = GWT
			.create(BasicRegisterUiBinder.class);

	interface BasicRegisterUiBinder extends UiBinder<Widget, BasicRegister> {
	}

	public BasicRegister(EventBus eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		initFields();
		setEventBus(eventBus);
		// setDataType(DataTypeSingleRegister._8);
		subscribeTextBoxEvents();
	}

	private void initFields() {
		textBoxes = new TextBox[] { edit8_3, edit8_2, edit8_1, edit8_0,
				edit16_1, edit16_0, edit32_0 };
		textBoxes4 = new TextBox[] { edit8_0, edit8_1, edit8_2, edit8_3 };
		textBoxes2 = new TextBox[] { edit16_0, edit16_1 };
		textBoxes1 = new TextBox[] { edit32_0 };
		setRegisterViewSettings(new RegisterViewSettings());
		for (int i = 0; i < textBoxes.length; i++)
			textBoxes[i].setText("0");
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	// @Override
	public void fireEventToEventBus(GwtEvent<?> event) {
		if (this.eventBus != null) {
			eventBus.fireEventFromSource(event, this);
		}
	}

	private int readValue() {
		Integer readedPart = null;

		int[] values = new int[activeTextBoxes.length];
		for (int i = 0; i < values.length; i++) {
			if (registerViewSettings.isFloating()) {
				Integer parsedFloat = DataTypeTools.parseFloatToUnsignedInteger(activeTextBoxes[0].getText());
				readedPart = parsedFloat;
			} else {
				readedPart = DataTypeTools.parseFormattedIntegerString(
						activeTextBoxes[i].getText(),
						registerViewSettings.isSigned(),
						registerViewSettings.getSizeInBits(),
						registerViewSettings.getNumberFormat().getRadix());
			}
			if (readedPart == null) {
				values[i] = 0;
				invalidBoxes[i] = true;
				activeTextBoxes[i].addStyleName(res.style().invalidNumber());
			} else {
				values[i] = readedPart;
				invalidBoxes[i] = false;
				activeTextBoxes[i].removeStyleName(res.style().invalidNumber());
			}
		}
		return DataTypeTools.createByParts(values);

	}

	private void showProperTextBoxes() {

		switch (registerViewSettings.getSizeInBits()) {
		case 8:
			deckTypeSelector.showWidget(0);
			activeTextBoxes = textBoxes4;
			invalidBoxes = new boolean[4];
			return;
		case 16:
			deckTypeSelector.showWidget(1);
			activeTextBoxes = textBoxes2;
			invalidBoxes = new boolean[2];
			return;
		case 32:
			deckTypeSelector.showWidget(2);
			activeTextBoxes = textBoxes1;
			invalidBoxes = new boolean[1];
			return;
		}
		logger.log(Level.FINE, "basic: size not available here: " + registerViewSettings.getSizeInBits());
	}

	private void fillVisibleTextBoxes(boolean modifyInvalids) {
		if (registerViewSettings.isFloating()) {
			activeTextBoxes[0].setText(DataTypeTools.FormatFloatInt(value));
		} else {
			String[] stringPartsOfValue = DataTypeTools.getPartsAsString(value, registerViewSettings.isSigned(),
					registerViewSettings.getSizeInBits(), registerViewSettings.getNumberFormat().getRadix());
			for (int i = 0; i < stringPartsOfValue.length; i++) {
				if (modifyInvalids || !invalidBoxes[i])
					activeTextBoxes[i].setText(stringPartsOfValue[i]);
			}
		}
		if (modifyInvalids)
			this.clearInvalids();
	}

	// Event Handlers
	private void subscribeTextBoxEvents() {
		// final BasicRegister that = this;
		ChangeHandler changeHandler = new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String rawValue = ((TextBox) event.getSource()).getText();
				if (rawValue.isEmpty()) {
					setValue(getValue());
				} else {
					int value = readValue();
					setValueLeaveInvalids(value, true);
					fireInformationIfInvalid((TextBox) event.getSource());
				}
			}
		};

		FocusHandler informationReqHandler = new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				fireInformationIfInvalid((TextBox) event.getSource());
			}
		};

		BlurHandler clearHandler = new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				fireClearInformationIfInvalid((TextBox) event.getSource());
			}
		};

		for (TextBox tb : textBoxes) {
			tb.addChangeHandler(changeHandler);
			tb.addDomHandler(informationReqHandler, FocusEvent.getType());
			tb.addDomHandler(clearHandler, BlurEvent.getType());
		}
	}

	void fireInformationIfInvalid(TextBox source) {
		for (int i = 0; i < activeTextBoxes.length; i++) {
			if (source == activeTextBoxes[i] && invalidBoxes[i] == true) {
				NumberFormatErrorEvent event = new NumberFormatErrorEvent(registerViewSettings, source.getText());
				fireEventToEventBus(event);
			}
		}
	}

	void fireClearInformationIfInvalid(TextBox source) {
		for (int i = 0; i < activeTextBoxes.length; i++) {
			if (source == activeTextBoxes[i] && invalidBoxes[i] == true) {
				ClearInformationsEvent clearEvent = new ClearInformationsEvent();
				fireEventToEventBus(clearEvent);
			}
		}
	}

	@Override
	public Integer getValue() {
		return value;
	}

	private void setValueLeaveInvalids(Integer value, boolean fireEvent) {
		this.value = value;
		fillVisibleTextBoxes(false);
		if (fireEvent) {
			ValueChangeEvent.fire(this, value);
		}
	}

	@Override
	public void setValue(Integer value) {
		this.value = value;
		fillVisibleTextBoxes(true);
	}

	@Override
	public void setValue(Integer value, boolean fireEvents) {
		setValue(value);
		if (fireEvents) {
			ValueChangeEvent.fire(this, value);
		}
	}

	@Override
	public HandlerRegistration addRegisterSelectedHandler(
			RegisterSelectedEventHandler handler) {
		return addHandler(handler, RegisterSelectedEvent.TYPE);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Integer> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public RegisterViewSettings getRegisterViewSettings() {
		// return registerViewSettings;
		return new RegisterViewSettings(registerViewSettings);
	}

	public void setRegisterViewSettings(RegisterViewSettings registerViewSettings) {
		this.registerViewSettings = registerViewSettings;
		showProperTextBoxes();
		clearInvalids();
		fillVisibleTextBoxes(true);
	}

	private void clearInvalids() {
		for (int i = 0; i < invalidBoxes.length; i++) {
			invalidBoxes[i] = false;
			activeTextBoxes[i].removeStyleName(res.style().invalidNumber());
		}

	}

	public void highlightBytesAdditive(int from, int to) {
		textBoxes1[0].addStyleName(res.style().valueJustChanged());
		highlighteds.add(textBoxes1[0]);
		for (int i = from; i <= to; i++) {
			textBoxes2[i / 2].addStyleName(res.style().valueJustChanged());
			highlighteds.add(textBoxes2[i / 2]);
			textBoxes4[i].addStyleName(res.style().valueJustChanged());
			highlighteds.add(textBoxes4[i]);
		}
	}

	public void removeHighlights() {
		for (TextBox current : highlighteds) {
			current.removeStyleName(res.style().valueJustChanged());
		}
		highlighteds.clear();
	}

}
