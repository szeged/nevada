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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.arm.nevada.client.interpreter.Instruction;
import com.arm.nevada.client.interpreter.InstructionForView;
import com.arm.nevada.client.shared.ARMRegister;
import com.arm.nevada.client.shared.events.ARMRegisterValueChangedEvent;
import com.arm.nevada.client.shared.events.AllInstructionReplacedInViewEvent;
import com.arm.nevada.client.shared.events.InstructionUpdatedInViewEvent;
import com.arm.nevada.client.shared.events.RemoveInstructionFromViewEvent;
import com.arm.nevada.client.shared.events.visualize.ProgramCounterChangedEvenet;
import com.arm.nevada.client.shared.events.visualize.ProgramCounterChangedEvenetHandler;
import com.arm.nevada.client.view.design.ResourceBundle;
import com.arm.nevada.client.view.shared.ToggleEditViewModeEvent;
import com.arm.nevada.client.view.shared.ToggleEditViewModeEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class InstructionEditor extends Composite implements
		ValueChangeHandler<InstructionForView>,
		ProgramCounterChangedEvenetHandler, ToggleEditViewModeEventHandler {
	private static final Logger logger = Logger.getLogger(InstructionEditor.class.getName());
	@UiField
	ResourceBundle res;
	@UiField
	TextArea textArea;
	@UiField
	Button editModeButton;
	@UiField
	VerticalPanel instructionList;
	@UiField
	DeckPanel modeSelector;
	@UiField
	Label captionWidget;

	private boolean editMode;
	private int selectedInstruction = 0;
	private EventBus eventBus;
	List<InstructionView> instructionViews = new LinkedList<InstructionView>();
	// List<HandlerRegistration> viewRegistrations = new
	// LinkedList<HandlerRegistration>();

	List<InstructionForView> lines = new LinkedList<InstructionForView>();

	private static InstructionEditorUiBinder uiBinder = GWT
			.create(InstructionEditorUiBinder.class);

	interface InstructionEditorUiBinder extends
			UiBinder<Widget, InstructionEditor> {
	}

	public InstructionEditor(EventBus eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.setEventbus(eventBus);
		setEditMode(true);
		setSelectedInstruction(0);
		addCommandLine("vadd.u8 q0, q1, q2");
		addCommandLine("vsub.8 q15, q2, q3");
		addCommandLine("vsub.s16 q13, q2, q3");
		addCommandLine("vsub.i16 d15, d2, d3");
		addCommandLine("vadd.i32 q0, q4, q5");
		addCommandLine("vand q0, q1, q2");
		addCommandLine("vand.u32 q0, q1, q2");
		addCommandLine("vand  q15, q14");
		addCommandLine("vorr q0, q14, q15");
		addCommandLine("vbic d5, d6, d14");
		addCommandLine("vand  d29, d28, d30");
		addCommandLine("vmov r1, r10, d22");
		addCommandLine("vmov d24, r1, r10");
		addCommandLine("vmov d25, d24");
		addCommandLine("vmov q13, q12");
		setEditMode(false);
	}

	private void addCommandLine(String command) {
		if (textArea.getText().lastIndexOf('\n') != textArea.getText().length() - 1)
			textArea.setText(textArea.getText() + "\n");
		textArea.setText(textArea.getText() + command + "\n");
	}

	public void setEditorText(String instructions) {
		textArea.setText(instructions);
	}

	@Override
	public void fireEvent(com.google.gwt.event.shared.GwtEvent<?> event) {
		if (eventBus != null)
			eventBus.fireEventFromSource(event, this);
	};

	@UiHandler("captionWidget")
	void onCaptionWidgetClicked(ClickEvent event) {
		setEditMode(!editMode);
	}

	@UiHandler("editModeButton")
	void onEditModeChanged(ClickEvent event) {
		setEditMode(!editMode);
	}

	private void setEditMode(boolean editMode) {
		this.editMode = editMode;
		if (editMode)
			switchToEditMode();
		else
			switchToViewMode();
	}

	private void switchToEditMode() {
		captionWidget.setText("Edit Mode");
		textArea.setText(getViewerString());
		modeSelector.showWidget(1);

	}

	private void switchToViewMode() {
		captionWidget.setText("View Mode");
		modeSelector.showWidget(0);
		String[] instructions = getEditorString().split("\n");
		List<InstructionForView> parsedLines = new LinkedList<InstructionForView>();
		for (String line : instructions) {
			InstructionForView parsedLine = new InstructionForView(line, false);
			parsedLines.add(parsedLine);
		}
		for (int i = 0; i < instructionViews.size() && i < parsedLines.size(); i++) {
			parsedLines.get(i).setBreakpoint(instructionViews.get(i).getValue().isBreakpoint());
		}
		displayInstructions(parsedLines);
	}

	private void displayInstructions(List<InstructionForView> instructions) {
		if (instructionViews.size() <= instructions.size()) {
			for (int instructionViewListSize = instructionViews.size(); instructionViewListSize < instructions.size(); instructionViewListSize++) {
				InstructionView instructionView = new InstructionView(this.eventBus);
				instructionView.setIndex(instructionViewListSize);
				instructionView.addValueChangeHandler(this);
				instructionView.getEventBus().addHandlerToSource(ProgramCounterChangedEvenet.TYPE, instructionView, instructionViewChangesPCHandler);
				// instructionView.addHandler(this, ValueChangeEvent.getType());

				instructionList.add(instructionView);
				instructionViews.add(instructionView);
			}
		} else {
			while (instructionList.getWidgetCount() > instructions.size()) {
				InstructionView view = (InstructionView) instructionList
						.getWidget(instructionList.getWidgetCount() - 1);
				RemoveInstructionFromViewEvent removeEvent = new RemoveInstructionFromViewEvent(view.getIndex());
				fireEvent(removeEvent);
				logger.log(Level.FINE, "fireRemove frim editor");
				instructionList.remove(view);
				instructionViews.remove(view);

				// instructionList.getWidgetCount() - 1

			}
		}

		List<Instruction> logicInstructions = new LinkedList<Instruction>();

		for (int i = 0; i < instructions.size(); i++) {
			instructionViews.get(i).setInstructionTextAndParse(instructions.get(i).getText());
			// TODO: should fire, but exception
			instructionViews.get(i).setBreakPoint(instructions.get(i).isBreakpoint(), false);
			InstructionForView viewInstruction = instructionViews.get(i).getValue();
			logicInstructions.add(viewInstruction.getInstruction());
		}
		AllInstructionReplacedInViewEvent forwardEvent = new AllInstructionReplacedInViewEvent(logicInstructions);
		fireEvent(forwardEvent);
		setSelectedInstruction(selectedInstruction);

	}

	ProgramCounterChangedEvenetHandler instructionViewChangesPCHandler = new ProgramCounterChangedEvenetHandler() {

		@Override
		public void onProgramCounterChanged(ProgramCounterChangedEvenet event) {
			if (event.getSource() == this)
				return;
			ARMRegisterValueChangedEvent RegisterByMachine = new ARMRegisterValueChangedEvent(ARMRegister.R15.getIndex(), event.getNewValue());
			fireEvent(RegisterByMachine);
		}
	};

	public EventBus getEventbus() {
		return eventBus;
	}

	private void setEventbus(EventBus eventBus) {
		this.eventBus = eventBus;
		if (eventBus != null) {
			eventBus.addHandler(ProgramCounterChangedEvenet.TYPE, this);
			eventBus.addHandler(ToggleEditViewModeEvent.TYPE, this);
		}
	}

	@Override
	public void onValueChange(ValueChangeEvent<InstructionForView> event) {
		InstructionView source = (InstructionView) event.getSource();
		InstructionUpdatedInViewEvent forwardEvent = new InstructionUpdatedInViewEvent(source.getIndex(), event.getValue().getInstruction());
		fireEvent(forwardEvent);
	}

	/**
	 * If the new PC is divisable by 4, then highlights the the proper instrcutionView. Else clears the highlight.
	 */
	@Override
	public void onProgramCounterChanged(ProgramCounterChangedEvenet event) {
		if (event.getNewValue() % 4 != 0) {
			setSelectedInstruction(-1);
		} else {
			setSelectedInstruction(event.getNewValue() / 4);
		}
	}

	/**
	 * Highlight an instructionView. Use negative value to clear highlight.
	 * 
	 * @param selectedInstruction
	 *            The index of the instruction to highlight. Invalid indexes are ignored.
	 */
	private void setSelectedInstruction(int selectedInstruction) {
		if (this.selectedInstruction >= 0 && instructionViews.size() > this.selectedInstruction) {
			instructionViews.get(this.selectedInstruction).removeStyleName(res.style().nextInstruction());
		}
		this.selectedInstruction = selectedInstruction;
		if (selectedInstruction >= 0 && instructionViews.size() > this.selectedInstruction) {
			instructionViews.get(this.selectedInstruction).addStyleName(res.style().nextInstruction());
		}
		logger.log(Level.FINE, "highlighted instruction changed");
	}

	/**
	 * Currently only saves the code and the breakpoint
	 * 
	 * @return The instructions is in the "code" key: JSONString, JSONNumber==1 if breakpoint
	 */
	public JSONObject getAsJSONObject() {
		JSONObject json = new JSONObject();
		JSONArray instructions = new JSONArray();
		if (editMode) {
			String[] lines = getCode().split("\n");
			for (int i = 0; i < lines.length; i++) {
				JSONArray currentJSON = new JSONArray();
				currentJSON.set(0, new JSONString(lines[i]));
				instructions.set(i, currentJSON);
			}
		} else {
			for (int i = 0; i < this.instructionViews.size(); i++) {
				InstructionForView instruction = instructionViews.get(i).getValue();
				JSONArray currentJSON = new JSONArray();
				currentJSON.set(0, new JSONString(instruction.getText()));
				if (instruction.isBreakpoint() && !editMode)
					currentJSON.set(1, new JSONNumber(1));
				instructions.set(i, currentJSON);
			}
		}

		json.put("code", instructions);
		return json;
	}

	private String getEditorString() {
		return textArea.getText();
	}

	private String getViewerString() {
		StringBuilder out = new StringBuilder();
		for (InstructionView view : instructionViews) {
			out.append(view.getValue().getText() + "\n");
		}
		return out.toString();
	}

	public void init(JSONObject json) {
		JSONArray instructions = json.get("code").isArray();
		List<InstructionForView> parsedInstructions = new LinkedList<InstructionForView>();
		for (int i = 0; i < instructions.size(); i++) {
			JSONArray instructionView = (JSONArray) instructions.get(i);
			String instruction = instructionView.get(0).isString().stringValue();

			boolean breakpoint = false;
			if (instructionView.get(1) != null) {
				int number = (int) instructionView.get(1).isNumber().doubleValue();
				if (number == 1)
					breakpoint = true;
			}
			parsedInstructions.add(new InstructionForView(instruction, breakpoint));
		}
		setInstructions(parsedInstructions);
		displayInstructions(parsedInstructions);
	}

	public void setInstructions(List<InstructionForView> instructions) {

	}

	public void setCode(String code) {
		textArea.setText(code);
		if (!editMode) {
			switchToViewMode();
		}
	}

	public String getCode() {
		if (editMode) {
			return getEditorString();
		} else
			return getViewerString();
	}

	@Override
	public void onToggleEditViewMode(ToggleEditViewModeEvent event) {
		setEditMode(!editMode);
	}

}
