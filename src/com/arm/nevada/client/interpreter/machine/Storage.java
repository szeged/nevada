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

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;

public abstract class Storage {
	private static final Logger logger = Logger.getLogger(Storage.class.getName());
	protected int[] values;
	protected EventBus eventBus;
	public static final int defaultOffsetInWords = 0;

	protected int offsetInWords = 0;

	public abstract void subscribeToEventBus();

	protected abstract void fireValueChanged(int index, int value);

	protected abstract void fireOffsetChanged(int newOffsetInWords);

	protected Storage(int sizeInWords, EventBus eventBus) {
		values = new int[sizeInWords];
		setEventBus(eventBus);
		subscribeToEventBus();
	}

	public int getOffset() {
		return offsetInWords;
	}

	public void fireEvent(GwtEvent<?> event) {
		if (eventBus == null) {
			return;
		}
		eventBus.fireEventFromSource(event, this);
	}

	public void setOneValue(int index, int value, boolean fireEvent) {
		values[index - getOffset()] = value;
		if (fireEvent) {
			fireValueChanged(index, value);
		}
	}

	public JSONObject getAsJSONObject() {
		JSONObject jsonObject = new JSONObject();
		if (getOffset() != defaultOffsetInWords) {
			jsonObject.put("offsetW", new JSONNumber(getOffset()));
		}
		for (int i = 0; i < this.values.length; i++) {
			if (values[i] != 0) {
				jsonObject.put(i + getOffset() + "", new JSONNumber(values[i]));
			}
		}
		return jsonObject;
	}

	public void initByJSONObject(JSONObject json) {
		Set<String> keySet = json.keySet();
		int offset = defaultOffsetInWords;
		try {
			offset = (int) json.get("offsetW").isNumber().doubleValue();
		} catch (Exception e) {
		}
		setOffset(offset, true);
		int index = 0, value = 0;
		for (String key : keySet) {
			try {
				index = Integer.parseInt(key);
				value = Integer.parseInt(json.get(key).toString());
				values[index - getOffset()] = value;
			} catch (Exception e) {
				logger.log(Level.FINE, "Error during parsing json object: key:" + key + " value: " + json.get(key));
			}
		}
		for (int i = 0; i < values.length; i++) {
			fireValueChanged(i + getOffset(), values[i]);
		}
	}

	protected void setOffset(int offsetInWords, boolean fireOffsetChangedEvent) {
		if (this.getOffset() == offsetInWords) {
			return;
		}
		this.offsetInWords = offsetInWords;
		if (fireOffsetChangedEvent) {
			fireOffsetChanged(getOffset());
		}
		//clear();
	}

	public int getOneValue(int index) {
		return values[index];
	}

	public int getSizeInBytes() {
		return values.length * 4;
	}

	/**
	 * Set each value to zero.
	 * 
	 * @param fireEvent
	 *            If true then sends value changed event.
	 */
	public void clear(boolean fireEvent) {
		for (int i = 0; i < values.length; i++) {
			setOneValue(i + getOffset(), 0, fireEvent);
		}
	}

	/**
	 * @return The storage size in words.
	 */
	public int getSize() {
		return values.length;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}
}
