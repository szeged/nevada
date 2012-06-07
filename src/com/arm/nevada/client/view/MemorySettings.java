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

import com.arm.nevada.client.shared.events.MemorySettingsChangedAndClearMemoryEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MemorySettings extends Composite {

	private static final Logger logger = Logger.getLogger(InstructionEditor.class.getName());

	@UiField
	TextBox sizeTextBox, offsetTextBox;

	@UiField
	ButtonBase saveButton, cancelButton;

	@UiField
	DialogBox dialogBox;

	private static MemorySettingsUiBinder uiBinder = GWT.create(MemorySettingsUiBinder.class);

	private EventBus eventBus;

	private int sizeInWords;

	private int offset;

	interface MemorySettingsUiBinder extends UiBinder<Widget, MemorySettings> {
	}

	public MemorySettings(int sizeInWords, int offset, EventBus eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.setEventBus(eventBus);
		this.setSizeInWords(sizeInWords, true);
		this.setOffset(offset, true);

	}

	@UiHandler("saveButton")
	void onSave(ClickEvent event) {
		Integer size = readSize();
		Integer offset = readOffset();

		fireEventToSharedEventBus(new MemorySettingsChangedAndClearMemoryEvent(size, offset));
		dialogBox.hide();
	}

	@UiHandler("cancelButton")
	void onCancel(ClickEvent event) {
		dialogBox.hide();
	}

	Integer readSize() {
		Integer out = null;
		try {
			int value = Integer.parseInt(sizeTextBox.getText());
			if (value >= 0) {
				// FIXME: allow odd numbers, for this GUI must be improved
				out = value + value % 2;
			}
		} catch (NumberFormatException e) {
			logger.log(Level.FINE, "Incorrect size specified.");
		}
		return out;
	}

	Integer readOffset() {
		Integer out = null;
		try {
			int value = Integer.parseInt(offsetTextBox.getText());
			if (value >= 0) {
				out = value + value % 2;
			}
		} catch (NumberFormatException e) {
			logger.log(Level.FINE, "Incorrect offset specified.");
		}
		return out;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public void fireEventToSharedEventBus(GwtEvent<?> event) {
		if (this.eventBus == null)
			return;
		eventBus.fireEventFromSource(event, this);
	}

	public int getSizeInWords() {
		return sizeInWords;
	}

	public void setSizeInWords(int sizeInWords, boolean updateGUI) {
		this.sizeInWords = sizeInWords;
		if (updateGUI)
			sizeTextBox.setText(String.valueOf(sizeInWords));
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset, boolean updateGUI) {
		this.offset = offset;
		if (updateGUI)
			offsetTextBox.setText(String.valueOf(offset));
	}

	public void show() {
		dialogBox.center();
		dialogBox.show();
	}

}
