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

import com.arm.nevada.client.interpreter.machine.MachineController;
import com.arm.nevada.client.shared.events.ShowHelpEvent;
import com.arm.nevada.client.shared.events.ShowHelpEventHandler;
import com.arm.nevada.client.shared.events.ShowInitializatorEvent;
import com.arm.nevada.client.shared.events.ShowInitializatorEventHandler;
import com.arm.nevada.client.shared.events.ShowSessionManagerEvent;
import com.arm.nevada.client.shared.events.ShowSessionManagerEventHandler;
import com.arm.nevada.client.utils.StateSaverAndLoader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

public class AppUI extends Composite implements
		ShowSessionManagerEventHandler,
		ShowHelpEventHandler,
		ShowInitializatorEventHandler {

	private static AppUIUiBinder uiBinder = GWT.create(AppUIUiBinder.class);

	interface AppUIUiBinder extends UiBinder<Widget, AppUI> {
	}

	@UiField(provided = true)
	ArmRegisterSet ARMRegisters;
	@UiField(provided = true)
	MemorySet MemorySet;
	@UiField(provided = true)
	NEONRegisterSet NEONRegisters;
	@UiField(provided = true)
	InstructionEditor instructionEditor;
	@UiField(provided = true)
	InformationReporter informationReporter;
	@UiField(provided = true)
	ButtonBar buttonBar;

	private EventBus eventBus;
	private MachineController machineController;

	public AppUI(EventBus eventBus, MachineController machineController) {
		this.eventBus = eventBus;
		this.machineController = machineController;
		initComponents();
		
		initWidget(uiBinder.createAndBindUi(this));
		
		registerEventHandlers(eventBus);
	}

	public InstructionEditor getInstructionEditor() {
		return instructionEditor;
	}


	private void initComponents() {
		this.ARMRegisters = new ArmRegisterSet(eventBus);
		this.MemorySet = new MemorySet(eventBus);
		this.NEONRegisters = new NEONRegisterSet(eventBus);
		this.instructionEditor = new InstructionEditor(eventBus);
		this.informationReporter = new InformationReporter(eventBus);
		this.buttonBar = new ButtonBar(eventBus);
	}
	
	@Override
	public void onShowSessionManager(ShowSessionManagerEvent event) {
		StateSaverAndLoader stateSaver =
				new StateSaverAndLoader(machineController.getMachine(), instructionEditor);

		DialogBox popup = new DialogBox();
		popup.add(new StateSaverDialog(stateSaver));
		popup.setAnimationEnabled(true);
		popup.setAutoHideEnabled(true);
		popup.setGlassEnabled(true);
		popup.center();
		popup.show();
	}

	@Override
	public void onShowHelp(ShowHelpEvent event) {
		Window.open("help/manual.html", "_blank", "");
	}

	@Override
	public void onShowInitializator(ShowInitializatorEvent event) {
		ValueInitializator initializator = new ValueInitializator(eventBus);
		initializator.show();
	}

	private void registerEventHandlers(EventBus eventBus) {
		eventBus.addHandler(ShowSessionManagerEvent.TYPE, this);
		eventBus.addHandler(ShowHelpEvent.TYPE, this);
		eventBus.addHandler(ShowInitializatorEvent.TYPE, this);
	}
}