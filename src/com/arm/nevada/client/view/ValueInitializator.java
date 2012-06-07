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

import java.util.Random;

import com.arm.nevada.client.shared.events.NEONRegisterValueChangedEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

public class ValueInitializator extends Composite {

	private static ValueInitializatorUiBinder uiBinder = GWT.create(ValueInitializatorUiBinder.class);

	interface ValueInitializatorUiBinder extends UiBinder<Widget, ValueInitializator> {
	}

	@UiField
	DialogBox dialogBox;
	@UiField
	Button closeButton, randomizeButton;
	private EventBus eventBus;

	public ValueInitializator(EventBus eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.setEventBus(eventBus);
	}

	private void randomize() {
		Random r = new Random();
		for (int i = 0; i < 16 * 4; i++) {
			NEONRegisterValueChangedEvent event = new NEONRegisterValueChangedEvent(i, r.nextInt());
			fireToEventBus(event);
		}
	}

	private void fireToEventBus(GwtEvent<?> event) {
		if (eventBus != null)
			eventBus.fireEventFromSource(event, this);
	}

	public void fireEventToSharedEventBus(GwtEvent<?> event) {
		if (this.eventBus == null)
			return;
		eventBus.fireEventFromSource(event, this);
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@UiHandler("randomizeButton")
	void onrandomizeButtonClicked(ClickEvent event) {
		randomize();
	}

	@UiHandler("closeButton")
	void onCloseButtonClicked(ClickEvent event) {
		dialogBox.hide();
	}

	public void show() {
		dialogBox.center();
		dialogBox.show();
	}

}
