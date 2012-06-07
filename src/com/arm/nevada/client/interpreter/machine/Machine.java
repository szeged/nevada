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

import com.arm.nevada.client.parser.EnumRegisterType;
import com.arm.nevada.client.shared.ARMRegister;
import com.arm.nevada.client.shared.events.visualize.ARMRegisterChangedHighlightEvent;
import com.arm.nevada.client.shared.events.visualize.MemoryChangedHighlightEvent;
import com.arm.nevada.client.shared.events.visualize.NEONRegisterChangedHighlightEvent;
import com.arm.nevada.client.shared.events.visualize.ProgramCounterChangedEvenet;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class Machine {
	private final NEONRegisterSet NEONRegisterSet;
	private final ARMRegisterSet armRegisterSet;
	private final MemorySet memorySet;
	private final SpecialRegisters specialRegisters;
	private EventBus eventBus;

	public Machine(EventBus eventBus) {
		NEONRegisterSet = new NEONRegisterSet(eventBus);
		armRegisterSet = new ARMRegisterSet(eventBus);
		memorySet = new MemorySet(eventBus);	// uses the default size
		specialRegisters = new SpecialRegisters(eventBus);
		this.eventBus = eventBus;
	}

	public int getPC() {
		return armRegisterSet.getOneValue(ARMRegister.R15.getIndex());
	}

	public void setPC(int value) {
		armRegisterSet.setOneValue(ARMRegister.R15.getIndex(), value, true);
	}

	public void incrementPCBy4() {
		armRegisterSet.setOneValue(ARMRegister.R15.getIndex(), armRegisterSet.getOneValue(15) + 4, true);
		eventBus.fireEvent(new ProgramCounterChangedEvenet(getPC()));
	}

	public NEONRegisterSet getNEONRegisterSet() {
		return NEONRegisterSet;
	}

	public ARMRegisterSet getArmRegisterSet() {
		return armRegisterSet;
	}

	public MemorySet getMemorySet() {
		return memorySet;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public void highlightNEONBytes(int from, int to) {
		eventBus.fireEvent(new NEONRegisterChangedHighlightEvent(from, to));
	}

	public void highlightNEONSubregister(EnumRegisterType regType, int dataSize, int NEONIndex, int subIndex) {
		int width = dataSize / 8;
		subIndex = subIndex * (dataSize / 8);
		dataSize = 8;
		int from = regType.getSizeInBytes() * NEONIndex + subIndex;
		int to = from + width;
		highlightNEONBytes(from, to - 1);
	}

	public void highlightMemoryBytes(int from, int to) {
		eventBus.fireEvent(new MemoryChangedHighlightEvent(from, to));
	}

	public void highlightARMRegister(int index) {
		eventBus.fireEvent(new ARMRegisterChangedHighlightEvent(index));
	}

	public void highlightNEONRegister(EnumRegisterType type, int index) {
		eventBus.fireEvent(new NEONRegisterChangedHighlightEvent(
				index * type.getSizeInBytes(),
				index * type.getSizeInBytes() + type.getSizeInBytes() - 1));
	}

	/**
	 * The keys for the storages: "arm", "memory" and "neon".
	 * 
	 * @return
	 */
	public JSONObject getAsJSONObject() {
		JSONObject machine = new JSONObject();
		machine.put("arm", this.armRegisterSet.getAsJSONObject());
		machine.put("memory", this.memorySet.getAsJSONObject());
		machine.put("neon", this.NEONRegisterSet.getAsJSONObject());
		boolean isAllSpecialRegZero = true;
		for (int i = 0; i < specialRegisters.getSize(); i++) {
			if (specialRegisters.getOneValue(i) != 0){
				isAllSpecialRegZero = false;
				break;
			}
		}
		if (!isAllSpecialRegZero)
			machine.put("spec", this.specialRegisters.getAsJSONObject());
		return machine;
	}

	/**
	 * Set the register and memory values.
	 * 
	 * @param machineJson
	 *            The object containing the values.
	 */
	public void init(JSONObject machineJson) {
		if (machineJson.containsKey("arm")) {
			JSONValue armValue = machineJson.get("arm");
			armRegisterSet.initByJSONObject((JSONObject) armValue);
		} else {
			armRegisterSet.clear(true);
		}

		if (machineJson.containsKey("neon")) {
			JSONValue NEONValue = machineJson.get("neon");
			NEONRegisterSet.initByJSONObject((JSONObject) NEONValue);
		} else {
			NEONRegisterSet.clear(true);
		}

		if (machineJson.containsKey("memory")) {
			JSONValue memoryValue = machineJson.get("memory");
			memorySet.initByJSONObject((JSONObject) memoryValue);
		} else {
			memorySet.clear(true);
		}

		if (machineJson.containsKey("spec")) {
			JSONValue specValue = machineJson.get("spec");
			specialRegisters.initByJSONObject((JSONObject) specValue);
		} else {
			specialRegisters.clear(true);
		}
	}

	public void fireEvent(GwtEvent<?> event) {
		if (eventBus != null)
			eventBus.fireEvent(event);
	}

	public SpecialRegisters getSpecialRegisters() {
		return specialRegisters;
	}

}
