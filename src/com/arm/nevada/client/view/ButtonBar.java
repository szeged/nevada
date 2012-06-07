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

import com.arm.nevada.client.shared.events.ShowHelpEvent;
import com.arm.nevada.client.shared.events.ShowInitializatorEvent;
import com.arm.nevada.client.shared.events.ShowSessionManagerEvent;
import com.arm.nevada.client.view.shared.ResetExecutionEvent;
import com.arm.nevada.client.view.shared.RunNextInstructionEvent;
import com.arm.nevada.client.view.shared.RunToNextBreakpointEvent;
import com.arm.nevada.client.view.shared.ToggleEditViewModeEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class ButtonBar extends Composite {

	@UiField
	PushButton stepButton, runButton, resetButton, viewEditButton, loadSaveButton, helpButton, randomizeButton;

	private static ButtonBarUiBinder uiBinder = GWT.create(ButtonBarUiBinder.class);

	private EventBus eventBus;

	interface ButtonBarUiBinder extends UiBinder<Widget, ButtonBar> {
	}

	public ButtonBar(EventBus eventBus) {
		this.setEventBus(eventBus);
		initWidget(uiBinder.createAndBindUi(this));
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public void fireEventToSharedBus(GwtEvent<?> event) {
		if (eventBus != null) {
			eventBus.fireEventFromSource(event, this);
		}
	}

	@UiHandler("stepButton")
	void onStep(ClickEvent event) {
		fireEventToSharedBus(new RunNextInstructionEvent());
	}

	@UiHandler("runButton")
	void onRun(ClickEvent event) {
		fireEventToSharedBus(new RunToNextBreakpointEvent());
	}

	@UiHandler("resetButton")
	void onReset(ClickEvent event) {
		fireEventToSharedBus(new ResetExecutionEvent());
	}

	@UiHandler("viewEditButton")
	void onViewEdit(ClickEvent event) {
		fireEventToSharedBus(new ToggleEditViewModeEvent());
	}

	@UiHandler("loadSaveButton")
	void onLoadSave(ClickEvent event) {
		fireEventToSharedBus(new ShowSessionManagerEvent());
	}

	@UiHandler("helpButton")
	void onHelp(ClickEvent event) {
		fireEventToSharedBus(new ShowHelpEvent());
	}

	@UiHandler("randomizeButton")
	void onRandomize(ClickEvent event) {
		fireEventToSharedBus(new ShowInitializatorEvent());
	}
}
