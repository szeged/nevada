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

import com.arm.nevada.client.shared.events.NEONRegisterValueChangedEvent;
import com.arm.nevada.client.shared.events.NEONRegisterValueChangedEventHandler;
import com.arm.nevada.client.shared.events.visualize.ClearRegisterChangedHighlightsEvenet;
import com.arm.nevada.client.shared.events.visualize.ClearRegisterChangedHighlightsEvenetHandler;
import com.arm.nevada.client.shared.events.visualize.NEONRegisterChangedHighlightEvent;
import com.arm.nevada.client.shared.events.visualize.NEONRegisterChangedHighlightEventHandler;
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NEONRegisterSet extends Composite implements HasMultipleValue,
		NEONRegisterValueChangedEventHandler,
		ClearRegisterChangedHighlightsEvenetHandler,
		NEONRegisterChangedHighlightEventHandler {
	private static final Logger logger = Logger.getLogger(NEONRegisterSet.class.getName());
	private static NEONRegisterSetUiBinder uiBinder = GWT
			.create(NEONRegisterSetUiBinder.class);

	interface NEONRegisterSetUiBinder extends UiBinder<Widget, NEONRegisterSet> {
	}

	@UiField
	ResourceBundle res;
	@UiField
	VerticalPanel NEONRegisterSetContainer;
	@UiField
	TypeSelectorMenu higherSelector, lowerSelector;
	@UiField
	Image linker;

	private EventBus eventBus;
	private NEONRegister[] registers;
	// private int selectedQ;
	private SelectionManager selectionManager;
	private boolean linked;
	private final Set<NEONRegister> highlighedRegisters = new HashSet<NEONRegister>();
	private TypeSelectorMenu[] typeSelectors = new TypeSelectorMenu[2];

	public NEONRegisterSet(EventBus eventBus) {

		initWidget(uiBinder.createAndBindUi(this));
		typeSelectors[0] = lowerSelector;
		typeSelectors[1] = higherSelector;
		registers = new NEONRegister[16];

		for (int i = 0; i < 16; i++) {
			RegisterViewSettings view1 = new RegisterViewSettings(false, 8, false, NumberFormat.decimal);
			RegisterViewSettings view2 = new RegisterViewSettings(false, 8, false, NumberFormat.decimal);
			registers[i] = new NEONRegister(null);
			registers[i].setQuadLabel(i);
			registers[i].getRegisters()[0].setRegisterViewSettings(view1);
			registers[i].getRegisters()[1].setRegisterViewSettings(view2);
			NEONRegisterSetContainer.add(registers[i]);

		}
		this.setEventBus(eventBus);
		subscribeEvents();
		selectionManager = new SelectionManager(16);

		selectionManager.change(0, false, false);
		updateRegisterSelection();

		setLinked(true);
	}

	private void subscribeEvents() {
		// linker
		ClickHandler linkHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setLinked(!linked);
			}
		};
		linker.addDomHandler(linkHandler, ClickEvent.getType());

		// Selector value changes
		ValueChangeHandler<RegisterViewSettings> lowerSelectorHandler = new ValueChangeHandler<RegisterViewSettings>() {
			@Override
			public void onValueChange(ValueChangeEvent<RegisterViewSettings> event) {
				selectorValueChanged(0, event.getValue());
			}
		};
		ValueChangeHandler<RegisterViewSettings> higherSelectorHandler = new ValueChangeHandler<RegisterViewSettings>() {
			@Override
			public void onValueChange(ValueChangeEvent<RegisterViewSettings> event) {
				selectorValueChanged(1, event.getValue());
			}
		};
		lowerSelector.addValueChangeHandler(lowerSelectorHandler);
		higherSelector.addValueChangeHandler(higherSelectorHandler);

		// register values
		MultipleValuePartChangeEventHandler valueChangeHandler = new MultipleValuePartChangeEventHandler() {
			@Override
			public void onValuePartChanged(MultipleValuePartChangeEvent event) {
				NEONRegister source = (NEONRegister) event.getSource();
				MultipleValuePartChangeEvent forwardEvent = new MultipleValuePartChangeEvent(source.getQuadNumber() + event.getOffset(),
						event.getValue());
				fireEvent(forwardEvent);

				int offset = source.getQuadNumber() * 4 + event.getOffset();
				int value = event.getValue();
				NEONRegisterValueChangedEvent viewEvent = new NEONRegisterValueChangedEvent(offset, value);
				fireEvent(viewEvent);

				logger.log(Level.FINE, "NEONSET fired value change");
			}
		};

		// register selection
		RegisterSelectedEventHandler selectionChangedHandler = new RegisterSelectedEventHandler() {
			@Override
			public void onFocusGet(RegisterSelectedEvent event) {
				NEONRegister source = (NEONRegister) event.getSource();
				selectionManager.change(source.getQuadNumber(), event.getNativeEvent());
				updateRegisterSelection();
			}
		};

		for (int i = 0; i < 16; i++) {
			registers[i].addRegisterSelectedHandler(selectionChangedHandler);
			registers[i].addValuePartChangedHandleHandler(valueChangeHandler);
		}
	}

	protected void selectorValueChanged(int lowerOrHigherSelector, RegisterViewSettings value) {
		if (linked) {
			typeSelectors[lowerOrHigherSelector == 0 ? 1 : 0]
					.setValue(typeSelectors[lowerOrHigherSelector].getValue(), false);
		}
		setSelectedRegistersDataType(lowerOrHigherSelector);
	}

	private void updateRegisterSelection() {
		DoubleRegister lastLowDouble = registers[selectionManager.getLastClickedIndex()].getLowDoubleRegister();
		DoubleRegister lastHighDouble = registers[selectionManager.getLastClickedIndex()].getHighDoubleRegister();
		for (int i = 0; i < registers.length; i++) {
			registers[i].setSelected(selectionManager.isSelected(i));
		}
		if (linked) {
			if (!lastLowDouble.getRegisterViewSettings().isEqualTo(lastHighDouble.getRegisterViewSettings()))
				setLinked(false);
		}
		lowerSelector.setValue(lastLowDouble.getRegisterViewSettings());
		higherSelector.setValue(lastHighDouble.getRegisterViewSettings());
	}

	protected void setSelectedRegistersDataType(int lowerOrHigherDouble) {
		for (int i = 0; i < registers.length; i++)
			if (selectionManager.isSelected(i)) {
				registers[i].getRegisters()[lowerOrHigherDouble]
						.setRegisterViewSettings(typeSelectors[lowerOrHigherDouble].getValue());
				if (linked)
					registers[i].getRegisters()[lowerOrHigherDouble == 0 ? 1 : 0].
							setRegisterViewSettings(typeSelectors[lowerOrHigherDouble == 0 ? 1 : 0].getValue());
			}
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		eventBus.fireEventFromSource(event, this);
	}

	@Override
	public void setAllValue(int[] values) {
		int[] NEONValue = new int[4];
		for (int i = 0; i < 16; i++) {
			NEONValue[0] = values[i * 4 + 0];
			NEONValue[1] = values[i * 4 + 1];
			NEONValue[2] = values[i * 4 + 2];
			NEONValue[3] = values[i * 4 + 3];
			registers[i].setAllValue(NEONValue);
		}
	}

	@Override
	public void setOneValue(int index, int value) {
		registers[index / 4].setOneValue(index % 4, value);
	}

	/**
	 * 4*16 integer needed
	 */
	@Override
	public void setAllValue(int[] values, boolean fireEvent) {
		for (int i = 0; i < 4 * 16; i++) {
			setOneValue(i, values[i], fireEvent);
		}
	}

	@Override
	public int[] getAllValue() {
		int[] values = new int[4 * 16];

		for (int i = 0; i < 16; i++) {
			int[] NEONValue = registers[i].getAllValue();
			values[4 * i + 0] = NEONValue[0];
			values[4 * i + 1] = NEONValue[1];
			values[4 * i + 2] = NEONValue[2];
			values[4 * i + 3] = NEONValue[3];
		}
		return values;
	}

	@Override
	public int getOneValue(int index) {
		return registers[index / 4].getOneValue(index % 4);
	}

	@Override
	public void setOneValue(int index, int value, boolean fireEvent) {
		setOneValue(index, value);
		if (fireEvent) {
			MultipleValuePartChangeEvent forwardEvent = new MultipleValuePartChangeEvent(index, value);
			fireEvent(forwardEvent);
		}
	}

	@Override
	public HandlerRegistration addValuePartChangedHandleHandler(
			MultipleValuePartChangeEventHandler handler) {
		return addHandler(handler, MultipleValuePartChangeEvent.TYPE);
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	private void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
		for (int i = 0; i < registers.length; i++) {
			registers[i].setEventBus(eventBus);
		}
		if (eventBus != null) {
			eventBus.addHandler(NEONRegisterValueChangedEvent.TYPE, this);
			eventBus.addHandler(ClearRegisterChangedHighlightsEvenet.TYPE, this);
			eventBus.addHandler(NEONRegisterChangedHighlightEvent.TYPE, this);
		}
	}

	@Override
	public void onElementChanged(NEONRegisterValueChangedEvent event) {
		if (event.getSource() != this)
			setOneValue(event.getOffset() * 1 + 0, event.getValue());
	}

	@Override
	public void onClearRegisterChangedHighlights(ClearRegisterChangedHighlightsEvenet event) {
		for (NEONRegister toRemove : highlighedRegisters) {
			toRemove.removeHighlights();
		}
		highlighedRegisters.clear();
	}

	public boolean isLinked() {
		return linked;
	}

	public void setLinked(boolean linked) {
		this.linked = linked;
		logger.log(Level.FINE, "linked: " + linked);
		if (linked) {
			higherSelector.setValue(new RegisterViewSettings(lowerSelector.getValue()), true);
			linker.setResource(res.linkedImage());
		} else {
			linker.setResource(res.unLinkedImage());
		}
	}

	@Override
	public void onNEONRegisterChangedHighlight(NEONRegisterChangedHighlightEvent event) {
		int from = event.getFromByte();
		int to = event.getToByte();
		for (int i = from / 16; i <= to / 16; i += 1) {
			registers[i].highlightBytesAdditive(i == from / 16 ? from % 16 : 0, i == to / 16 ? to % 16 : 15);
			highlighedRegisters.add(registers[i]);
		}
	}

}
