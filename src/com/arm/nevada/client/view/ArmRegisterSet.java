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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.arm.nevada.client.shared.ARMRegister;
import com.arm.nevada.client.shared.SpecialRegiser;
import com.arm.nevada.client.shared.events.ARMRegisterValueChangedEvent;
import com.arm.nevada.client.shared.events.ARMRegisterValueChangedEventHandler;
import com.arm.nevada.client.shared.events.SpecialRegisterChangedEvent;
import com.arm.nevada.client.shared.events.SpecialRegisterChangedEventHandler;
import com.arm.nevada.client.shared.events.visualize.ARMRegisterChangedHighlightEvent;
import com.arm.nevada.client.shared.events.visualize.ARMRegisterChangedHighlightEventHandler;
import com.arm.nevada.client.shared.events.visualize.ClearRegisterChangedHighlightsEvenet;
import com.arm.nevada.client.shared.events.visualize.ClearRegisterChangedHighlightsEvenetHandler;
import com.arm.nevada.client.shared.events.visualize.ProgramCounterChangedEvenet;
import com.arm.nevada.client.shared.events.visualize.ProgramCounterChangedEvenetHandler;
import com.arm.nevada.client.view.design.ResourceBundle;
import com.arm.nevada.client.view.shared.HasMultipleValue;
import com.arm.nevada.client.view.shared.MultipleValuePartChangeEventHandler;
import com.arm.nevada.client.view.shared.MultipleValuePartChangeEvent;
import com.arm.nevada.client.view.shared.NumberFormat;
import com.arm.nevada.client.view.shared.RegisterSelectedEvent;
import com.arm.nevada.client.view.shared.RegisterSelectedEventHandler;
import com.arm.nevada.client.view.shared.RegisterViewSettings;
import com.arm.nevada.client.view.shared.SelectionManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ArmRegisterSet
		extends
		Composite
		implements
		HasMultipleValue,
		ARMRegisterValueChangedEventHandler,
		ClearRegisterChangedHighlightsEvenetHandler,
		ARMRegisterChangedHighlightEventHandler,
		ProgramCounterChangedEvenetHandler,
		SpecialRegisterChangedEventHandler {
	private static final Logger logger = Logger.getLogger(ArmRegisterSet.class.getName());
	protected static final int FPSCR_INDEX = 17;
	protected static final int PSR_INDEX = 16;
	protected static final int SP_INDEX = ARMRegister.R13.getIndex();
	protected static final int LR_INDEX = ARMRegister.R14.getIndex();
	protected static final int PC_INDEX = ARMRegister.R15.getIndex();
	private static ARMRegisterSetUiBinder uiBinder = GWT.create(ARMRegisterSetUiBinder.class);

	interface ARMRegisterSetUiBinder extends UiBinder<Widget, ArmRegisterSet> {
	}

	@UiField
	VerticalPanel container;
	@UiField
	ResourceBundle res;
	@UiField(provided = true)
	TypeSelectorMenu typeSelector = new TypeSelectorMenu(false);
	@UiField
	ArmRegister psr, fpscr;
	private EventBus eventBus;
	// private int selected = 0;
	private ArmRegister[] rRegisters = new ArmRegister[16];
	private ArmRegister[] allRegisters = new ArmRegister[rRegisters.length + 2];
	// private ArmRegister cpsr = new ArmRegister(null);
	private final Set<ArmRegister> highlighedRegisters = new HashSet<ArmRegister>();
	private SelectionManager selectionManager;

	// private final int[] values = new int[16];

	public ArmRegisterSet(EventBus eventBus) {
		initWidget(uiBinder.createAndBindUi(this));

		for (int i = 0; i < 16; i++) {
			RegisterViewSettings view = new RegisterViewSettings(false, 32, false, NumberFormat.decimal);
			ArmRegister element = new ArmRegister(null);
			element.setIndex(i);
			element.setLabelByIndex(i);
			rRegisters[i] = element;
			allRegisters[i] = element;
			rRegisters[i].setRegisterViewSettings(view);
			container.add(element);
		}
		rRegisters[PC_INDEX].setLabel("PC");
		rRegisters[LR_INDEX].setLabel("LR");
		rRegisters[SP_INDEX].setLabel("SP");

		allRegisters[16] = psr;
		allRegisters[17] = fpscr;
		psr.setIndex(16);
		fpscr.setIndex(17);
		RegisterViewSettings psrVirew = new RegisterViewSettings(false, 8, false, NumberFormat.decimal);
		psr.setRegisterViewSettings(psrVirew);
		fpscr.setRegisterViewSettings(psrVirew);

		this.setEventBus(eventBus);
		subscribeEvents();
		selectionManager = new SelectionManager(allRegisters.length);
		selectionManager.change(0, false, false);
		updateSelection();
	}

	private void updateSelection() {
		for (int i = 0; i < allRegisters.length; i++) {
			allRegisters[i].setSelected(selectionManager.isSelected(i));
		}
		typeSelector.setValue(allRegisters[selectionManager.getLastClickedIndex()].getRegisterViewSettings());
	}

	protected void updateDataType() {
		for (int i = 0; i < allRegisters.length; i++) {
			if (selectionManager.isSelected(i))
				allRegisters[i].setRegisterViewSettings(typeSelector.getValue());
		}
	}

	private void subscribeEvents() {
		RegisterSelectedEventHandler selectionChangeHandler = new RegisterSelectedEventHandler() {
			@Override
			public void onFocusGet(RegisterSelectedEvent event) {
				ArmRegister source = (ArmRegister) event.getSource();
				selectionManager.change(source.getIndex(), event.getNativeEvent());
				updateSelection();
			}
		};

		ValueChangeHandler<RegisterViewSettings> typeSelectorChangedHandler = new ValueChangeHandler<RegisterViewSettings>() {

			@Override
			public void onValueChange(ValueChangeEvent<RegisterViewSettings> event) {
				updateDataType();
			}
		};

		typeSelector.addValueChangeHandler(typeSelectorChangedHandler);

		ValueChangeHandler<Integer> valueChangeHandler = new ValueChangeHandler<Integer>() {

			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				ArmRegister source = (ArmRegister) event.getSource();
				MultipleValuePartChangeEvent forwardEvent = new MultipleValuePartChangeEvent(source.getIndex(), source.getValue());
				fireEvent(forwardEvent);
				if (source.getIndex() < 16) {
					ARMRegisterValueChangedEvent viewEvent = new ARMRegisterValueChangedEvent(source.getIndex(), source.getValue());
					fireEvent(viewEvent);
					logger.log(Level.FINE, "ARMSET fired value change");
				} else {
					if (source.getIndex() == PSR_INDEX) {
						SpecialRegisterChangedEvent specialChanged = new SpecialRegisterChangedEvent(SpecialRegiser.APSR, event.getValue());
						fireEvent(specialChanged);
					} else if (source.getIndex() == FPSCR_INDEX) {
						SpecialRegisterChangedEvent specialChanged = new SpecialRegisterChangedEvent(SpecialRegiser.FPSCR, event.getValue());
						fireEvent(specialChanged);
					}
				}
			}
		};

		for (int i = 0; i < allRegisters.length; i++) {
			allRegisters[i].addRegisterSelectedHandler(selectionChangeHandler);
		}
		for (int i = 0; i < allRegisters.length; i++) {
			allRegisters[i].addValueChangeHandler(valueChangeHandler);
		}

		eventBus.addHandler(ProgramCounterChangedEvenet.TYPE, this);

	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		if (eventBus != null)
			eventBus.fireEventFromSource(event, this);
	};

	@Override
	public void setAllValue(int[] values) {
		for (int i = 0; i < 16; i++) {
			setOneValue(i, values[i]);
		}
	}

	@Override
	public void setAllValue(int[] values, boolean fireEvent) {
		for (int i = 0; i < 16; i++) {
			setOneValue(i, values[i], fireEvent);
		}
	}

	@Override
	public int[] getAllValue() {
		int[] values = new int[16];
		for (int i = 0; i < 16; i++)
			values[i] = rRegisters[i].getValue();
		return values;
	}

	@Override
	public int getOneValue(int index) {
		return rRegisters[index].getValue();
	}

	@Override
	public void setOneValue(int index, int value) {
		rRegisters[index].setValue(value);
	}

	@Override
	public void setOneValue(int index, int value, boolean fireEvent) {
		setOneValue(index, value);
		if (fireEvent) {
			MultipleValuePartChangeEvent event = new MultipleValuePartChangeEvent(index, value);
			fireEvent(event);
		}
	}

	@Override
	public HandlerRegistration addValuePartChangedHandleHandler(MultipleValuePartChangeEventHandler handler) {
		return addHandler(handler, MultipleValuePartChangeEvent.TYPE);
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	private void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
		for (int i = 0; i < allRegisters.length; i++) {
			allRegisters[i].setEventBus(eventBus);
		}
		if (eventBus != null) {
			eventBus.addHandler(ARMRegisterValueChangedEvent.TYPE, this);
			eventBus.addHandler(ClearRegisterChangedHighlightsEvenet.TYPE, this);
			eventBus.addHandler(ARMRegisterChangedHighlightEvent.TYPE, this);
			eventBus.addHandler(SpecialRegisterChangedEvent.TYPE, this);
		}
	}

	@Override
	public void onElementChanged(ARMRegisterValueChangedEvent event) {
		if (event.getSource() != this)
			setOneValue(event.getOffset(), event.getValue());
	}

	@Override
	public void onClearRegisterChangedHighlights(ClearRegisterChangedHighlightsEvenet event) {
		for (ArmRegister toRemove : highlighedRegisters) {
			toRemove.removeHighlights();
		}
		highlighedRegisters.clear();
	}

	@Override
	public void onARMRegisterChangedHighlight(ARMRegisterChangedHighlightEvent event) {
		rRegisters[event.getIndex()].highlightByValueChanged();
		highlighedRegisters.add(rRegisters[event.getIndex()]);
	}

	@Override
	public void onProgramCounterChanged(ProgramCounterChangedEvenet event) {
		rRegisters[PC_INDEX].highlightByValueChanged();
		highlighedRegisters.add(rRegisters[PC_INDEX]);
	}

	@Override
	public void onSpecialRegisterChanged(SpecialRegisterChangedEvent event) {
		if (event.getSource() == this)
			return;
		if (event.getRegister() == SpecialRegiser.APSR) {
			psr.setValue(event.getValue());
		} else if (event.getRegister() == SpecialRegiser.FPSCR) {
			fpscr.setValue(event.getValue());
		}

	}

}
