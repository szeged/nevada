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

import com.arm.nevada.client.utils.StateSaverAndLoader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class StateSaverDialog extends Composite {

	private static StateSaverDialogUiBinder uiBinder = GWT.create(StateSaverDialogUiBinder.class);

	interface StateSaverDialogUiBinder extends UiBinder<Widget, StateSaverDialog> {
	}

	@UiField
	Button machineLoadButton;
	@UiField
	Button editorLoadButton;
	@UiField
	TextArea URLTextArea;
	@UiField
	TextArea machineTextArea;
	@UiField
	TextArea editorTextArea;
	private final StateSaverAndLoader stateSaverAndLoader;

	public StateSaverDialog(StateSaverAndLoader stateSaverAndLoader) {
		this.stateSaverAndLoader = stateSaverAndLoader;
		initWidget(uiBinder.createAndBindUi(this));
		URLTextArea.setText(stateSaverAndLoader.getStateURL());
		machineTextArea.setText(stateSaverAndLoader.getMachineJSON().toString());
		editorTextArea.setText(stateSaverAndLoader.getInstructionEditorJSON().toString());
	}

	@UiHandler("URLTextArea")
	void onURLTextAreaFocused(ClickEvent event) {
		URLTextArea.selectAll();
	}

	@UiHandler("machineTextArea")
	void onURLmachineTextAreaFocused(ClickEvent event) {
		machineTextArea.selectAll();
	}

	@UiHandler("editorTextArea")
	void oneditorTextAreaFocused(ClickEvent event) {
		editorTextArea.selectAll();
	}

	@UiHandler("machineLoadButton")
	void onMachineLoadButtonClicked(ClickEvent event) {
		stateSaverAndLoader.setMachine(machineTextArea.getText());
	}

	@UiHandler("editorLoadButton")
	void onEditorLoadButtonClicked(ClickEvent event) {
		stateSaverAndLoader.setInstructionEditor(editorTextArea.getText());
	}

}
