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

package com.arm.nevada.client.interpreter;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.arm.nevada.client.interpreter.machine.Machine;
import com.arm.nevada.client.shared.events.AllInstructionReplacedInViewEvent;
import com.arm.nevada.client.shared.events.AllInstructionReplacedInViewEventHandler;
import com.arm.nevada.client.shared.events.InstructionUpdatedInViewEvent;
import com.arm.nevada.client.shared.events.InstructionUpdatedInViewEventHandler;
import com.arm.nevada.client.shared.events.RemoveInstructionFromViewEvent;
import com.arm.nevada.client.shared.events.RemoveInstructionFromViewEventHandler;
import com.google.gwt.event.shared.EventBus;

public class InstructionListForMachineController implements InstructionUpdatedInViewEventHandler, AllInstructionReplacedInViewEventHandler,
		RemoveInstructionFromViewEventHandler {
	private static final Logger logger = Logger.getLogger(InstructionListForMachineController.class.getName());

	private EventBus eventBus;
	private List<Instruction> instructions;
	private Machine machine;

	public InstructionListForMachineController(Machine machine, EventBus eventBus) {
		this.instructions = new LinkedList<Instruction>();

		this.machine = machine;
		this.eventBus = eventBus;

		registerEventHandlers();
	}

	/**
	 * Returns the next instruction by the Program Counter of the machine. The machine previously must be set by the
	 * setMachine(). The PC's value should be divisible by 4.
	 * 
	 * @return The instruction by the Program Counter. If the PC is not valid then null.
	 */
	public Instruction getNextInstruction() {
		int pc = getMachine().getPC();
		if (pc % 4 != 0 || pc < 0 || pc / 4 > instructions.size() - 1) {
			return null;
		}

		return instructions.get(pc / 4);
	}

	private void registerEventHandlers() {
		this.eventBus.addHandler(InstructionUpdatedInViewEvent.TYPE, this);
		this.eventBus.addHandler(AllInstructionReplacedInViewEvent.TYPE, this);
		this.eventBus.addHandler(RemoveInstructionFromViewEvent.TYPE, this);
	}

	public Machine getMachine() {
		return machine;
	}

	@Override
	public void onAllInstructionReplacedInView(
			AllInstructionReplacedInViewEvent event) {
		instructions = event.getInstructionList();
	}

	@Override
	public void onInstructionUpdatedInView(InstructionUpdatedInViewEvent event) {
		instructions.set(event.getIndex(), event.getInstruction());
	}

	@Override
	public void onRemoveInstructionFromView(RemoveInstructionFromViewEvent event) {
		instructions.remove(event.getIndex());
		logger.log(Level.FINE, "remove in InstructionListForMachineController");
	}

	public int getInstructionCount() {
		return instructions.size();
	}

}
