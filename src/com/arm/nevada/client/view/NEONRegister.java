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

import java.util.LinkedList;

import com.arm.nevada.client.view.design.ResourceBundle;
import com.arm.nevada.client.view.shared.HasMultipleValue;
import com.arm.nevada.client.view.shared.HasRegisterSelectedHandlers;
import com.arm.nevada.client.view.shared.MultipleValuePartChangeEventHandler;
import com.arm.nevada.client.view.shared.MultipleValuePartChangeEvent;
import com.arm.nevada.client.view.shared.RegisterSelectedEvent;
import com.arm.nevada.client.view.shared.RegisterSelectedEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class NEONRegister extends Composite implements
		HasRegisterSelectedHandlers, HasMultipleValue {

	private static NEONRegisterUiBinder uiBinder = GWT.create(NEONRegisterUiBinder.class);

	interface NEONRegisterUiBinder extends UiBinder<Widget, NEONRegister> {
	}

	LinkedList<FocusHandler> listeners = new LinkedList<FocusHandler>();

	@UiField
	ResourceBundle res;
	@UiField
	Label lowerD, higherD;
	@UiField
	Label Q;
	@UiField(provided = true)
	DoubleRegister lowDoubleRegister;
	@UiField(provided = true)
	DoubleRegister highDoubleRegister;
	@UiField
	HorizontalPanel container;
	@UiField
	HTMLPanel root;
	private DoubleRegister[] registers = new DoubleRegister[2];

	private int quadNumber;

	@SuppressWarnings("unused")
	private EventBus eventBus;

	public NEONRegister(EventBus eventBus) {
		highDoubleRegister = new DoubleRegister(null);
		lowDoubleRegister = new DoubleRegister(null);
		setEventBus(eventBus);
		initWidget(uiBinder.createAndBindUi(this));
		registers[0] = lowDoubleRegister;
		registers[1] = highDoubleRegister;
		subscribeEvents();
	}

	private void subscribeEvents() {
		// selected events
		final NEONRegister that = this;
		ClickHandler a = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				fireEvent(new RegisterSelectedEvent(that, event.getNativeEvent()));
			}
		};
		container.addDomHandler(a, ClickEvent.getType());
	}

	public void setSelected(String highlighted) {
		setSelected(Boolean.parseBoolean(highlighted));
	}

	public void setSelected(boolean selected) {
		if (selected)
			root.addStyleName(res.style().selected());
		else
			root.removeStyleName(res.style().selected());
	}

	public void setQuadLabel(String q) {
		int value = Integer.parseInt(q);
		setQuadLabel(value);
	}

	public void setQuadLabel(int value) {
		this.quadNumber = value;
		Q.setText("Q" + value);
		lowerD.setText("D" + value * 2);
		higherD.setText("D" + (value * 2 + 1));
	}

	public int getQuadNumber() {
		return quadNumber;
	}

	public DoubleRegister getHighDoubleRegister() {
		return highDoubleRegister;
	}

	public DoubleRegister getLowDoubleRegister() {
		return lowDoubleRegister;
	}

	@UiHandler("lowDoubleRegister")
	protected void onLowDoubleRegisterSelected(RegisterSelectedEvent event) {
		fireEvent(new RegisterSelectedEvent(this, event.getNativeEvent()));
	}

	@UiHandler("highDoubleRegister")
	protected void onHighDoubleRegisterSelected(RegisterSelectedEvent event) {
		fireEvent(new RegisterSelectedEvent(this, event.getNativeEvent()));
	}

	@UiHandler("lowDoubleRegister")
	protected void onLowPartValueChanged(MultipleValuePartChangeEvent event) {
		MultipleValuePartChangeEvent forwardEvent = new MultipleValuePartChangeEvent(0 + event.getOffset(), event.getValue());
		fireEvent(forwardEvent);
	}

	@UiHandler("highDoubleRegister")
	protected void onHighPartValueChanged(MultipleValuePartChangeEvent event) {
		MultipleValuePartChangeEvent forwardEvent = new MultipleValuePartChangeEvent(2 + event.getOffset(), event.getValue());
		fireEvent(forwardEvent);
	}

	@Override
	public HandlerRegistration addValuePartChangedHandleHandler(
			MultipleValuePartChangeEventHandler handler) {
		return addHandler(handler, MultipleValuePartChangeEvent.TYPE);
	}

	@Override
	public void setAllValue(int[] values) {
		lowDoubleRegister.setAllValue(new int[] { values[0], values[1] });
		highDoubleRegister.setAllValue(new int[] { values[2], values[3] });
	}

	@Override
	public void setAllValue(int[] values, boolean fireEvent) {
		for (int i = 0; i < 4; i++) {
			setOneValue(i, values[i], fireEvent);
		}
	}

	@Override
	public void setOneValue(int index, int value) {
		registers[index / 2].setOneValue(index % 2, value);
	}

	@Override
	public void setOneValue(int index, int value, boolean fireEvent) {
		setOneValue(index, value);
		if (fireEvent) {
			MultipleValuePartChangeEvent forwardEvent = new MultipleValuePartChangeEvent(index, value);
			fireEvent(forwardEvent);
		}
	}

	@Override
	public int[] getAllValue() {
		int[] values = new int[] { lowDoubleRegister.getOneValue(0),
				lowDoubleRegister.getOneValue(1),
				highDoubleRegister.getOneValue(0),
				highDoubleRegister.getOneValue(1) };
		return values;
	}

	@Override
	public int getOneValue(int index) {
		return registers[index / 2].getOneValue(index % 2);
	}

	@Override
	public HandlerRegistration addRegisterSelectedHandler(
			RegisterSelectedEventHandler handler) {
		return addHandler(handler, RegisterSelectedEvent.TYPE);
	}

	public DoubleRegister[] getRegisters() {
		return registers;
	}

	// min 0; max: 2*2*4=16
	public void highlightBytesAdditive(int from, int to) {
		if (from <= 7) {
			lowDoubleRegister.highlightBytesAdditive(from, to <= 7 ? to : 7);
		}
		if (to >= 8) {
			highDoubleRegister.highlightBytesAdditive(from >= 8 ? from - 8 : 0, to - 8);
		}
	}

	public void removeHighlights() {
		lowDoubleRegister.removeHighlights();
		highDoubleRegister.removeHighlights();
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
		highDoubleRegister.setEventBus(eventBus);
		lowDoubleRegister.setEventBus(eventBus);
	}

}
