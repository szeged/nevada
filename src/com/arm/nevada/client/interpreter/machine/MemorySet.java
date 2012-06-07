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

package com.arm.nevada.client.interpreter.machine;

import com.arm.nevada.client.shared.events.MemoryMachineValueChangedEvent;
import com.arm.nevada.client.shared.events.MemorySettingsChangedAndClearMemoryEvent;
import com.arm.nevada.client.shared.events.MemorySettingsChangedAndClearMemoryEventHandler;
import com.arm.nevada.client.shared.events.MemoryViewValueChangedEvent;
import com.arm.nevada.client.shared.events.MemoryViewValueChangedEventHandler;
import com.arm.nevada.client.utils.DataTypeTools;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;

public class MemorySet extends Storage implements
		MemoryViewValueChangedEventHandler, MemorySettingsChangedAndClearMemoryEventHandler {
	private static final int defaultSizeInWord = 32;

	public MemorySet(EventBus eventBus) {
		super(defaultSizeInWord, eventBus);
	}

	@Override
	public void onElementChanged(MemoryViewValueChangedEvent event) {
		if (event.getSource() == this) {
			return;
		}
		setOneValue(event.getOffset(), event.getValue(), false);
	}

	/**
	 * The index must be offseted!
	 */
	@Override
	public void setOneValue(int index, int value, boolean fireEvent) {
		super.setOneValue(index, value, false);
		values[index - offsetInWords] = value;
		if (fireEvent) {
			fireValueChanged(index, value);
		}
	}

	/**
	 * The index must be offseted!
	 */
	@Override
	protected void fireValueChanged(int index, int value) {
		MemoryMachineValueChangedEvent forwardEvent = new MemoryMachineValueChangedEvent(index, value);
		fireEvent(forwardEvent);
	}

	public int getWord(int fromByteAddress) {
		// fromByteAddress = fromByteAddress - offsetInWords * 4;
		int lower = getOneValue(fromByteAddress / 4);
		if (fromByteAddress % 4 == 0)
			return lower;
		int higher = getOneValue(fromByteAddress / 4 + 1);
		int[] lParts = DataTypeTools.getParts(8, lower);
		int[] hParts = DataTypeTools.getParts(8, higher);
		int[] outParts = new int[4];
		int cntr = 0;
		for (int i = fromByteAddress % 4; i < 4; i++) {
			outParts[cntr] = lParts[i];
			cntr++;
		}
		for (int i = 0; cntr < 4; i++) {
			outParts[cntr] = hParts[i];
			cntr++;
		}
		int out = DataTypeTools.createByParts(outParts);
		return out;
	}

	@Override
	public int getOneValue(int index) {
		return super.getOneValue(index - offsetInWords);
	}

	public void setWord(int toByteAddress, int value, boolean fireEvent) {
		// toByteAddress = toByteAddress - offsetInWords * 4;
		if (toByteAddress % 4 == 0)
			this.setOneValue(toByteAddress / 4, value, true);
		else {
			int[] valueParts = DataTypeTools.getParts(8, value);
			int[] partsAt = DataTypeTools.getParts(8, this.getOneValue(toByteAddress / 4));
			int[] partsNext = DataTypeTools.getParts(8, this.getOneValue(toByteAddress / 4 + 1));
			int cntr = 0;
			for (int i = toByteAddress % 4; i < 4; i++)
				partsAt[i] = valueParts[cntr++];
			for (int i = 0; cntr < 4; i++)
				partsNext[i] = valueParts[cntr++];
			int at = DataTypeTools.createByParts(partsAt);
			int next = DataTypeTools.createByParts(partsNext);
			this.setOneValue(toByteAddress / 4, at, true);
			this.setOneValue(toByteAddress / 4 + 1, next, true);
		}
	}

	public void setValue(int toByteAddress, int value, int size, boolean fireEvent) {
		int original = this.getWord(toByteAddress);
		int newWord = original & ~DataTypeTools.getBitmask(size);
		newWord = newWord | (DataTypeTools.getBitmask(size) & value);
		this.setWord(toByteAddress, newWord, fireEvent);
	}

	public void setSize(int sizeInWord, boolean fireMemorySettingsChangedEvent, boolean fireMemoryClear) {
		if (this.values.length == sizeInWord)
			return;
		values = new int[sizeInWord];
		if (fireMemorySettingsChangedEvent) {
			MemorySettingsChangedAndClearMemoryEvent e = new MemorySettingsChangedAndClearMemoryEvent(sizeInWord);
			fireEvent(e);
		}
		clear(fireMemoryClear);
	}

	@Override
	public void onMemorySettingsChangedAndClearMemory(MemorySettingsChangedAndClearMemoryEvent event) {
		if (event.getSource() == this) {
			return;
		}
		if (event.getNewSizeInWords() != null)
			setSize(event.getNewSizeInWords(), false, false);
		if (event.getOffsetInWords() != null)
			setOffset(event.getOffsetInWords(), false);
		clear(false);
	}

	@Override
	public void initByJSONObject(JSONObject json) {
		int size = defaultSizeInWord;
		try {
			size = (int) json.get("sizeW").isNumber().doubleValue();
		} catch (Exception e) {
		}
		this.setSize(size, true, true);
		super.initByJSONObject(json);
	}

	@Override
	public JSONObject getAsJSONObject() {
		JSONObject asJSONObject = super.getAsJSONObject();
		if (this.getSizeInBytes() / 4 != defaultSizeInWord) {
			asJSONObject.put("sizeW", new JSONNumber(this.getSizeInBytes() / 4));
		}
		return asJSONObject;
	}

	@Override
	public void subscribeToEventBus() {
		if (eventBus == null) {
			return;
		}
		this.eventBus.addHandler(MemoryViewValueChangedEvent.TYPE, this);
		this.eventBus.addHandler(MemorySettingsChangedAndClearMemoryEvent.TYPE, this);
	}

	@Override
	protected void fireOffsetChanged(int newOffsetInWords) {
		MemorySettingsChangedAndClearMemoryEvent e = new MemorySettingsChangedAndClearMemoryEvent(null, newOffsetInWords);
		fireEvent(e);
	}
}
