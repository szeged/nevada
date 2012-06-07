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

import com.arm.nevada.client.view.shared.ExecuteToEndEvent;
import com.arm.nevada.client.view.shared.ResetExecutionEvent;
import com.arm.nevada.client.view.shared.RunNextInstructionEvent;
import com.arm.nevada.client.view.shared.RunToNextBreakpointEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class ExecutionController extends Composite implements HasHandlers {

	private static ExecutionControllerUiBinder uiBinder = GWT
			.create(ExecutionControllerUiBinder.class);

	interface ExecutionControllerUiBinder extends
			UiBinder<Widget, ExecutionController> {
	}

	@UiField
	PushButton toNextBreakpointButton, stepButton, executeToEndButton, resetExecutionButton;
	private EventBus eventBus;

	public ExecutionController(EventBus eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.setEventBus(eventBus);
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		if (eventBus != null)
			eventBus.fireEvent(event);
	}

	@UiHandler("toNextBreakpointButton")
	void onToNextBreakpointButtonClicked(ClickEvent event) {
		fireEvent(new RunToNextBreakpointEvent());
	}

	@UiHandler("stepButton")
	void onStepButtonClicked(ClickEvent event) {
		fireEvent(new RunNextInstructionEvent());
	}

	@UiHandler("executeToEndButton")
	void onExecuteToEndButtonClicked(ClickEvent event) {
		fireEvent(new ExecuteToEndEvent());
	}

	@UiHandler("resetExecutionButton")
	void onResetExecutionButtonClicked(ClickEvent event) {
		fireEvent(new ResetExecutionEvent());
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	private void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

}
