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

import com.arm.nevada.client.interpreter.ErrorInstruction;
import com.arm.nevada.client.interpreter.InstructionForView;
import com.arm.nevada.client.shared.events.ClearInformationsEvent;
import com.arm.nevada.client.shared.events.InvalidInstructionInformationEvent;
import com.arm.nevada.client.shared.events.information.InvalidInstructionEvent;
import com.arm.nevada.client.shared.events.visualize.ProgramCounterChangedEvenet;
import com.arm.nevada.client.view.design.ResourceBundle;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class InstructionView extends Composite implements HasValue<InstructionForView> {

	@UiField
	TextBox instructionTextBox;
	@UiField
	Image breakpointImage;
	@UiField
	ResourceBundle res;
	private InstructionForView instruction;
	private int index;
	private static InstructionViewUiBinder uiBinder = GWT
			.create(InstructionViewUiBinder.class);
	private final EventBus eventBus;
	private boolean isFocused = false;

	interface InstructionViewUiBinder extends UiBinder<Widget, InstructionView> {
	}

	public InstructionView(EventBus eventBus) {
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
		instruction = new InstructionForView();
		subscribeEvents();
		updateBreakPoint();
	}

	private void subscribeEvents() {
		ClickHandler clickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setBreakPoint(!instruction.isBreakpoint(), true);
			}
		};

		FocusHandler focusHandler = new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				isFocused = true;
				fireInvalidInstructionIfInvalid();
			}
		};
		instructionTextBox.addDomHandler(focusHandler, FocusEvent.getType());
		breakpointImage.addDomHandler(clickHandler, ClickEvent.getType());
	}

	protected void fireInvalidInstructionIfInvalid() {
		if (isValid())
			return;
		ErrorInstruction errorI = (ErrorInstruction) this.instruction.getInstruction();
		InvalidInstructionEvent event = new InvalidInstructionEvent(this.instruction.getText());
		InvalidInstructionInformationEvent errorInfoEvent = new InvalidInstructionInformationEvent(
				errorI.getErrorMessage(),
				errorI.getOriginalString(),
				errorI.getBestParsedLength());
		fireEventToSharedEventBus(event); // 2
		fireEventToSharedEventBus(errorInfoEvent);
	}

	private boolean isValid() {
		return !(this.instruction.getInstruction() instanceof ErrorInstruction);
	}

	public void fireEventToSharedEventBus(GwtEvent<?> event) { // 2
		if (this.getEventBus() != null) {
			getEventBus().fireEventFromSource(event, this);
		}
	}

	public InstructionView(EventBus eventBus, String instruction) {
		this(eventBus);
		setInstructionTextAndParse(instruction);
	}

	private void updateUi() {
		updateBreakPoint();
		updateValidity();
	}

	@UiHandler("instructionTextBox")
	protected void onInstructionDoubleClick(DoubleClickEvent event) {
		ProgramCounterChangedEvenet PCChangedEvent = new ProgramCounterChangedEvenet(index * 4);
		fireEventToSharedEventBus(PCChangedEvent);
	}

	@UiHandler("instructionTextBox")
	protected void onInstructionBlur(BlurEvent event) {
		isFocused = false;
		if (!isValid()) {
			ClearInformationsEvent clear = new ClearInformationsEvent();
			fireEventToSharedEventBus(clear);
		}
	}

	@UiHandler("instructionTextBox")
	protected void onInstructionTextBoxValueChanged(ValueChangeEvent<String> event) {
		instruction.setText(event.getValue());
		updateValidity();
		ValueChangeEvent.fire(this, this.instruction);
		if (isFocused)
			fireInvalidInstructionIfInvalid();
	}

	private void updateBreakPoint() {
		if (instruction != null && instruction.isBreakpoint()) {
			breakpointImage.setResource(res.breakpoint());
		}
		else {
			breakpointImage.setResource(res.noBreakpoint());
		}
	}

	private void updateValidity() {
		if (instruction.getInstruction() instanceof ErrorInstruction) {
			this.addStyleName(res.style().invalidInstruction());
		}
		else {
			this.removeStyleName(res.style().invalidInstruction());
		}
	}

	public boolean isBreakPoint() {
		return instruction.isBreakpoint();
	}

	public void setBreakPoint(boolean breakPoint, boolean fireEvent) {
		instruction.setBreakpoint(breakPoint);
		updateBreakPoint();
		if (fireEvent)
			ValueChangeEvent.fire(this, this.instruction);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<InstructionForView> handler) {

		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public InstructionForView getValue() {
		return this.instruction;
	}

	@Override
	public void setValue(InstructionForView value) {
		this.instruction = value;
		updateValidity();
		updateBreakPoint();
	}

	@Override
	public void setValue(InstructionForView value, boolean fireEvents) {
		setValue(value);
		if (fireEvents) {
			ValueChangeEvent.fire(this, value);
		}

	}

	public void setInstructionTextAndParse(String instruction) {
		this.instruction.setText(instruction);

		this.instructionTextBox.setText(instruction);
		this.instruction.setText(instruction);

		this.updateUi();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

}
