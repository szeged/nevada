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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.arm.nevada.client.shared.events.MemoryMachineValueChangedEvent;
import com.arm.nevada.client.shared.events.MemoryMachineValueChangedEventHandler;
import com.arm.nevada.client.shared.events.MemorySettingsChangedAndClearMemoryEvent;
import com.arm.nevada.client.shared.events.MemorySettingsChangedAndClearMemoryEventHandler;
import com.arm.nevada.client.shared.events.MemoryViewValueChangedEvent;
import com.arm.nevada.client.shared.events.visualize.ClearRegisterChangedHighlightsEvenet;
import com.arm.nevada.client.shared.events.visualize.ClearRegisterChangedHighlightsEvenetHandler;
import com.arm.nevada.client.shared.events.visualize.MemoryChangedHighlightEvent;
import com.arm.nevada.client.shared.events.visualize.MemoryChangedHighlightEventHandler;
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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MemorySet extends Composite implements
		HasMultipleValue,
		MemoryMachineValueChangedEventHandler,
		MemoryChangedHighlightEventHandler,
		ClearRegisterChangedHighlightsEvenetHandler,
		MemorySettingsChangedAndClearMemoryEventHandler {
	private static final Logger logger = Logger.getLogger(MemorySet.class.getName());
	private static MemorySetUiBinder uiBinder = GWT
			.create(MemorySetUiBinder.class);

	interface MemorySetUiBinder extends UiBinder<Widget, MemorySet> {
	}

	private EventBus eventBus;
	// mod 16 byte == 0
	private int memorySizeinWords = 32;
	// 16byte aligned
	private int offsetInWords = 0;

	@UiField
	VerticalPanel container;
	@UiField
	TypeSelectorMenu typeSelector;
	@UiField
	PushButton settingsButton;
	// private int selected = 0;
	private List<MemoryDoubleElement> elements = new LinkedList<MemoryDoubleElement>();// MemoryDoubleElement[memorySizeinWords
																						// / 2];
	private SelectionManager selectionManager;
	private final Set<MemoryDoubleElement> highlighteds = new HashSet<MemoryDoubleElement>();

	public MemorySet(EventBus eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		for (int i = 0; i < memorySizeinWords / 2; i++) {
			RegisterViewSettings view = new RegisterViewSettings(false, 8, false, NumberFormat.decimal);
			MemoryDoubleElement element = new MemoryDoubleElement(null);
			element.setD(i);
			element.setRegisterViewSettings(view);
			elements.add(element);
			container.add(element);
		}
		this.setEventBus(eventBus);
		subscribeEvents();
		selectionManager = new SelectionManager(memorySizeinWords);
		selectionManager.change(0, false, false);
		updateRegisterSelection();
	}

	private void subscribeMemoryElement(MemoryDoubleElement element) {
		// register value changes
		MultipleValuePartChangeEventHandler memoryValueChangeHandler = new MultipleValuePartChangeEventHandler() {
			@Override
			public void onValuePartChanged(
					MultipleValuePartChangeEvent event) {
				MemoryDoubleElement source = (MemoryDoubleElement) event.getSource();
				int address = source.getD() * 2 + event.getOffset();
				MultipleValuePartChangeEvent forwardEvent = new MultipleValuePartChangeEvent(address, event.getValue());
				fireEvent(forwardEvent);

				MemoryViewValueChangedEvent viewEvent = new MemoryViewValueChangedEvent(address, event.getValue());
				fireEvent(viewEvent);

				logger.log(Level.FINE, "MemorySET fired value change: @" + address + ": " + event.getValue());
			}

		};

		// register selection
		RegisterSelectedEventHandler selectionHandler = new RegisterSelectedEventHandler() {
			@Override
			public void onFocusGet(RegisterSelectedEvent event) {
				MemoryDoubleElement source = (MemoryDoubleElement) event.getSource();
				typeSelector.setValue(source.getRegisterViewSettings());
				int tempOffset = offsetInWords;
				selectionManager.change(source.getD() - tempOffset / 2, event.getNativeEvent());
				updateRegisterSelection();
			}
		};

		element.addRegisterSelectedHandler(selectionHandler);
		element.addValuePartChangedHandleHandler(memoryValueChangeHandler);

	}

	private void subscribeEvents() {

		for (int i = 0; i < memorySizeinWords / 2; i++) {
			subscribeMemoryElement(elements.get(i));
		}
		// type selector changes
		ValueChangeHandler<RegisterViewSettings> selectorHandler = new ValueChangeHandler<RegisterViewSettings>() {
			@Override
			public void onValueChange(ValueChangeEvent<RegisterViewSettings> event) {
				selectorValueChanged(event.getValue());
			}
		};
		typeSelector.addValueChangeHandler(selectorHandler);

	}

	protected void selectorValueChanged(RegisterViewSettings type) {
		setSelectedRegistersDataType();
	}

	private void updateRegisterSelection() {
		for (int i = 0; i < elements.size(); i++) {
			elements.get(i).setSelected(selectionManager.isSelected(i));
		}
		typeSelector.setValue(elements.get(selectionManager.getLastClickedIndex()).getRegisterViewSettings());
	}

	protected void setSelectedRegistersDataType() {
		for (int i = 0; i < elements.size(); i++)
			if (selectionManager.isSelected(i))
				elements.get(i).setRegisterViewSettings(typeSelector.getValue());
	}

	@Override
	public void fireEvent(com.google.gwt.event.shared.GwtEvent<?> event) {
		if (eventBus != null)
			eventBus.fireEvent(event);
	};

	@Override
	public void setAllValue(int[] values) {
		setAllValue(values, false);
	}

	@Override
	public void setAllValue(int[] values, boolean fireEvent) {
		for (int i = 0; i < elements.size(); i++) {
			setOneValue(i + offsetInWords, values[i], fireEvent);
		}
	}

	@Override
	public void setOneValue(int index, int value) {
		elements.get((index - offsetInWords) / 2).setOneValue(index % 2, value);
	}

	@Override
	public void setOneValue(int index, int value, boolean fireEvent) {
		setOneValue(index, value);
		if (fireEvent) {
			MemoryViewValueChangedEvent forwardEvent = new MemoryViewValueChangedEvent(index, value);
			fireEvent(forwardEvent);
		}
	}

	@Override
	public int[] getAllValue() {
		int[] values = new int[memorySizeinWords];
		for (int i = 0; i < memorySizeinWords / 2; i++) {
			values[i * 2 + 0] = elements.get(i).getOneValue(0);
			values[i * 2 + 1] = elements.get(i).getOneValue(1);
		}
		return values;
	}

	@Override
	public int getOneValue(int index) {
		return elements.get(index / 2).getOneValue(index % 2);
	}

	@Override
	public HandlerRegistration addValuePartChangedHandleHandler(
			MultipleValuePartChangeEventHandler handler) {
		return addHandler(handler,
				MultipleValuePartChangeEvent.TYPE);
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	private void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
		for (int i = 0; i < elements.size(); i++) {
			elements.get(i).setEventBus(eventBus);
		}
		if (eventBus != null) {
			this.eventBus.addHandler(MemoryMachineValueChangedEvent.TYPE, this);
			this.eventBus.addHandler(MemoryChangedHighlightEvent.TYPE, this);
			this.eventBus.addHandler(ClearRegisterChangedHighlightsEvenet.TYPE, this);
			this.eventBus.addHandler(MemorySettingsChangedAndClearMemoryEvent.TYPE, this);
		}
	}

	@Override
	public void onElementChanged(MemoryMachineValueChangedEvent event) {
		setOneValue(event.getOffset(), event.getValue());
	}

	@Override
	public void onMemoryChangedHighlight(MemoryChangedHighlightEvent event) {
		int from = event.getFromByte() - 4 * offsetInWords;
		int to = event.getToByte() - 4 * offsetInWords;
		for (int i = from / 8; i <= to / 8; i += 1) {
			elements.get(i).highlightBytesAdditive(i == from / 8 ? from % 8 : 0, i == to / 8 ? to % 8 : 7);
			highlighteds.add(elements.get(i));
		}
	}

	@Override
	public void onClearRegisterChangedHighlights(ClearRegisterChangedHighlightsEvenet event) {
		for (MemoryDoubleElement element : highlighteds) {
			element.removeHighlights();
		}
		highlighteds.clear();
	}

	@UiHandler("settingsButton")
	void onSettingClicked(ClickEvent event) {
		MemorySettings ms = new MemorySettings(memorySizeinWords, offsetInWords, eventBus);
		ms.show();
	}

	@Override
	public void onMemorySettingsChangedAndClearMemory(MemorySettingsChangedAndClearMemoryEvent event) {
		if (event.getSource() == this) {
			return;
		}
		if (event.getNewSizeInWords() != null) {
			this.setSize(event.getNewSizeInWords());
		}
		if (event.getOffsetInWords() != null) {
			this.setOffset(event.getOffsetInWords());
		}
		setAllValue(new int[memorySizeinWords], false);
	}

	void setSize(int sizeInWords) {
		this.memorySizeinWords = sizeInWords;
		selectionManager.setSize(sizeInWords / 2);
		int doubleElementCount = sizeInWords / 2;
		if (doubleElementCount == elements.size())
			return;
		if (elements.size() > doubleElementCount) {
			for (int i = elements.size() - 1; i >= doubleElementCount; i--) {
				container.remove(elements.get(i));
				elements.remove(i);
			}
		} else {
			for (int i = elements.size(); i < doubleElementCount; i++) {
				MemoryDoubleElement newItem = new MemoryDoubleElement(getEventBus());
				newItem.setD(i + offsetInWords);
				elements.add(newItem);
				container.add(newItem);
				subscribeMemoryElement(newItem);
			}
		}
	}

	void setOffset(int offsetInWords) {
		if (this.offsetInWords == offsetInWords)
			return;
		int i = 0;
		for (MemoryDoubleElement element : elements) {
			element.setD(i + offsetInWords / 2);
			i++;
		}
		this.offsetInWords = offsetInWords;
	}
}
