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

import com.arm.nevada.client.shared.events.AlignmentErrorEvent;
import com.arm.nevada.client.shared.events.AlignmentErrorEventHandler;
import com.arm.nevada.client.shared.events.ClearInformationsEvent;
import com.arm.nevada.client.shared.events.ClearInformationsEventEventHandler;
import com.arm.nevada.client.shared.events.InvalidInstructionInformationEvent;
import com.arm.nevada.client.shared.events.InvalidInstructionInformationEventHandler;
import com.arm.nevada.client.shared.events.SegmentationFaultEvent;
import com.arm.nevada.client.shared.events.SegmentationFaultEventHandler;
import com.arm.nevada.client.shared.events.ShowErrorMessageEvent;
import com.arm.nevada.client.shared.events.ShowErrorMessageEventHandler;
import com.arm.nevada.client.shared.events.information.InvalidInstructionEvent;
import com.arm.nevada.client.shared.events.information.InvalidInstructionEventHandler;
import com.arm.nevada.client.shared.events.information.NumberFormatErrorEvent;
import com.arm.nevada.client.shared.events.information.NumberFormatErrorEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class InformationReporter extends Composite
		implements AlignmentErrorEventHandler,
		SegmentationFaultEventHandler,
		NumberFormatErrorEventHandler,
		InvalidInstructionEventHandler,
		ShowErrorMessageEventHandler,
		InvalidInstructionInformationEventHandler,
		ClearInformationsEventEventHandler {

	@UiField
	HasText messageLabel;
	@UiField
	MyStyle localStyle;
	@UiField
	SimplePanel box;

	private static InformationReporterUiBinder uiBinder = GWT.create(InformationReporterUiBinder.class);
	private EventBus eventBus;

	interface InformationReporterUiBinder extends UiBinder<Widget, InformationReporter> {
	}

	interface MyStyle extends CssResource {
		String highlightNew();
	}

	public InformationReporter(EventBus eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.setEventBus(eventBus);
	}

	private void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
		if (eventBus != null) {
			this.eventBus.addHandler(SegmentationFaultEvent.TYPE, this);
			this.eventBus.addHandler(AlignmentErrorEvent.TYPE, this);
			this.eventBus.addHandler(NumberFormatErrorEvent.TYPE, this);
			this.eventBus.addHandler(InvalidInstructionEvent.TYPE, this);
			this.eventBus.addHandler(ShowErrorMessageEvent.TYPE, this);
			this.eventBus.addHandler(InvalidInstructionInformationEvent.TYPE, this);
			this.eventBus.addHandler(ClearInformationsEvent.TYPE, this);
		}
	}

	private void addMessage(String messge) {
		messageLabel.setText(messge);
		highlight(this);
	}

	private void startHighlightHelper() {
		box.addStyleName(localStyle.highlightNew());
	}

	private void startClearHighlightHelper() {
		box.removeStyleName(localStyle.highlightNew());
	}

	public static native void highlight(InformationReporter obj) /*-{
		function set() {
			setTimeout(
					function() {
						obj.@com.arm.nevada.client.view.InformationReporter::startHighlightHelper()();
					}, 10)
		}
		obj.@com.arm.nevada.client.view.InformationReporter::startClearHighlightHelper()();
		set();

	}-*/;

	public static native void clear(InformationReporter obj) /*-{
		function set2() {
			setTimeout(
					function() {
						obj.@com.arm.nevada.client.view.InformationReporter::startClearHighlightHelper()();
					}, 10)
		}

		set2();

	}-*/;

	@Override
	public void onSegmentationFault(SegmentationFaultEvent event) {
		addMessage("ERROR during execution: Segmentation fault occured!");
	}

	@Override
	public void onAlignmentError(AlignmentErrorEvent event) {
		addMessage("ERROR during execution: Expected alignment: " + event.getExpectedAlignment() + " (" + event.getExpectedAlignment() / 8 + " byte)"
				+ ", but address was: " + event.getAddress());
	}

	@Override
	public void onNumberFormatErrorEvent(NumberFormatErrorEvent event) {
		String message = "Invalid value: \"" + event.getInvalidValue() + "\", it must be: " + event.getExpectedType().getText();
		addMessage(message);
	}

	@Override
	public void onInvalidInstructionEvent(InvalidInstructionEvent event) {
		String message = "Can't parse instruction: " + event.getInvalidInstruction();
		addMessage(message);
	}

	@Override
	public void onShowErrorMessage(ShowErrorMessageEvent event) {
		addMessage(event.getMessage());
	}

	@Override
	public void onInvalidInstructionInformation(InvalidInstructionInformationEvent event) {
		String msg = event.getErrorMessage() + " - Can't Parse after: ";
		msg += event.getOriginalInsriction().substring(0, event.getBestLength());
		addMessage(msg);
	}

	@Override
	public void onClearInformations(ClearInformationsEvent event) {
		this.clearMessage();
	}

	private void clearMessage() {
		messageLabel.setText("");
		clear(this);
	}
}
