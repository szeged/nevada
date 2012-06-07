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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.arm.nevada.client.parser.EnumRegisterType;
import com.arm.nevada.client.shared.events.NEONRegisterValueChangedEvent;
import com.arm.nevada.client.shared.events.NEONRegisterValueChangedEventHandler;
import com.arm.nevada.client.utils.DataTypeTools;
import com.google.gwt.event.shared.EventBus;

public class NEONRegisterSet extends Storage
		implements
		IDoubleWordDataSet,
		IQuadWordDataSet,
		NEONRegisterValueChangedEventHandler {
	private static final Logger logger = Logger.getLogger(NEONRegisterSet.class.getName());

	public NEONRegisterSet(EventBus eventBus) {
		super(16 * 4, eventBus);
	}

	public int getSubRegister(EnumRegisterType registerType, int size, int index, int subIndex) {
		assert size <= 32 && size >= 1;
		int wordIndex = index * registerType.getSize() / 32 + (size * subIndex / 32);
		int wordValue = getOneValue(wordIndex);

		int inWord = subIndex * size / 32;
		int wordSubIndex = subIndex - inWord * 32 / size;
		return DataTypeTools.getParts(size, wordValue)[wordSubIndex];
	}

	public void setSubRegister(EnumRegisterType registerType, int size, int index, int subIndex, int value) {
		assert size <= 32 && size >= 1;
		int wordIndex = index * registerType.getSize() / 32 + (size * subIndex / 32);
		int inWord = subIndex * size / 32;
		int wordSubIndex = subIndex - inWord * 32 / size;

		int original = this.getOneValue(wordIndex);
		int[] parts = DataTypeTools.getParts(size, original);
		parts[wordSubIndex] = value;

		int newValue = DataTypeTools.createByParts(parts);
		this.setOneValue(wordIndex, newValue, true);
	}

	public int[] getRegisterValues(EnumRegisterType type, int index) {
		int[] out;
		switch (type) {
		case DOUBLE:
			out = this.getDouble(index);
			break;
		case QUAD:
			out = this.getQuad(index);
			break;
		case SINGLE:
			out = new int[] { this.getOneValue(index) };
		default:
			assert false;
			return null;
		}

		return out;
	}

	public long[] getRegisterValuesLong(EnumRegisterType type, int index) {
		long[] out;
		int[] values;
		switch (type) {
		case DOUBLE:
			values = this.getDouble(index);
			out = new long[] { DataTypeTools.LongFromIntegers(values[0], values[1]) };
			break;
		case QUAD:
			values = this.getQuad(index);
			out = new long[] {
					DataTypeTools.LongFromIntegers(values[0], values[1]),
					DataTypeTools.LongFromIntegers(values[2], values[3]) };
			break;
		default:
			assert false;
			return null;
		}

		return out;
	}

	public void setRegisterValues(EnumRegisterType type, boolean fireEvent, int index, int... values) {
		assert (values.length == type.getSizeInBytes() / 4);
		switch (type) {
		case DOUBLE:
			this.setDouble(index, fireEvent, values);
			break;
		case QUAD:
			this.setQuad(index, fireEvent, values);
			break;
		case SINGLE:
			this.setOneValue(index, values[0], true);
		default:
			assert false;
			return;
		}
	}

	@Override
	public int[] getDouble(int index) {
		int[] out = new int[2];
		out[0] = getOneValue(index * 2 + 0);
		out[1] = getOneValue(index * 2 + 1);
		return out;
	}

	@Override
	public void setDouble(int index, int... values) {
		assert values.length == 2;
		setOneValue(2 * index + 0, values[0], true);
		setOneValue(2 * index + 1, values[1], true);
	}

	@Override
	public void setDouble(int index, boolean fireEvent, int... values) {
		setDouble(index, values);
		if (fireEvent) {
			for (int i = 0; i < values.length; i++) {
				fireValueChanged(2 * index + i, values[i]);
			}
		}

	}

	@Override
	public int[] getQuad(int index) {
		int[] out = new int[4];
		out[0] = getOneValue(index * 4 + 0);
		out[1] = getOneValue(index * 4 + 1);
		out[2] = getOneValue(index * 4 + 2);
		out[3] = getOneValue(index * 4 + 3);
		return out;
	}

	@Override
	public void setQuad(int index, int... values) {
		setOneValue(4 * index + 0, values[0], true);
		setOneValue(4 * index + 1, values[1], true);
		setOneValue(4 * index + 2, values[2], true);
		setOneValue(4 * index + 3, values[3], true);
	}

	@Override
	public void setQuad(int index, boolean fireEvent, int... values) {
		setQuad(index, values);
		if (fireEvent) {
			for (int i = 0; i < values.length; i++) {
				fireValueChanged(4 * index + i, values[i]);
			}
		}

	}

	@Override
	public void onElementChanged(NEONRegisterValueChangedEvent event) {
		if (event.getSource() == this) {
			return;
		}
		this.setOneValue(event.getOffset(), event.getValue(), false);
	}

	@Override
	protected void fireValueChanged(int index, int value) {
		NEONRegisterValueChangedEvent event = new NEONRegisterValueChangedEvent(index, value);
		fireEvent(event);
	}

	@Override
	public void subscribeToEventBus() {
		if (eventBus == null) {
			return;
		}
		this.eventBus.addHandler(NEONRegisterValueChangedEvent.TYPE, this);
	}

	@Override
	protected void setOffset(int offsetInWords, boolean fireOffsetChangedEvent) {
		logger.log(Level.WARNING, "Attempted to change the offset of the NEON register set.");
	};
	
	@Override
	protected void fireOffsetChanged(int newOffsetInWords) {
		logger.log(Level.WARNING, "Attempted to fire offset changing of the NEON register set.");
	}

}
