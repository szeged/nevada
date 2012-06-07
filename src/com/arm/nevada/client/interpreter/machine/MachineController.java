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

import com.arm.nevada.client.interpreter.ErrorInstruction;
import com.arm.nevada.client.interpreter.Instruction;
import com.arm.nevada.client.interpreter.InstructionListForMachineController;
import com.arm.nevada.client.shared.events.ShowErrorMessageEvent;
import com.arm.nevada.client.shared.events.visualize.ClearRegisterChangedHighlightsEvenet;
import com.arm.nevada.client.view.shared.ExecuteToEndEvent;
import com.arm.nevada.client.view.shared.ExecutionControllerHandler;
import com.arm.nevada.client.view.shared.ResetExecutionEvent;
import com.arm.nevada.client.view.shared.ResetMachineEvent;
import com.arm.nevada.client.view.shared.RunNextInstructionEvent;
import com.arm.nevada.client.view.shared.RunToNextBreakpointEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;

public class MachineController implements ExecutionControllerHandler {
	private static final Logger logger = Logger.getLogger(MachineController.class.getName());

	private Machine machine;
	private InstructionListForMachineController instructions;
	private EventBus eventBus;

	public MachineController(Machine machine, EventBus eventBus) {
		this.instructions = new InstructionListForMachineController(machine, eventBus);

		this.machine = machine;
		this.eventBus = eventBus;
	
		registerEventHandlers();
	}

	public Machine getMachine() {
		return machine;
	}

	private void registerEventHandlers() {
		this.eventBus.addHandler(RunToNextBreakpointEvent.TYPE, this);
		this.eventBus.addHandler(RunNextInstructionEvent.TYPE, this);
		this.eventBus.addHandler(ExecuteToEndEvent.TYPE, this);
		this.eventBus.addHandler(ResetExecutionEvent.TYPE, this);
	}

	private void executeByPC(boolean allowReset) {
		Instruction instruction = instructions.getNextInstruction(); 
		if (instruction != null) {
			if (!(instruction instanceof ErrorInstruction)) {
				instructions.getNextInstruction().execute(machine);
			}
			else {
				logger.log(Level.FINE,"Can't execute invalid instruction. Nothing happend. Please fix the current line.");
				ShowErrorMessageEvent errorEvent = new ShowErrorMessageEvent("Can't execute invalid instruction. Please fix the current line.");
				fireEvent(errorEvent);
			}
		} else {
			String message = "Error, can't get the next instruction. Maybe the value of the PC is invalid.";
			ShowErrorMessageEvent errorMessage = new ShowErrorMessageEvent(message);
			fireEvent(errorMessage);
		}

		if (allowReset && machine.getPC() / 4 >= instructions.getInstructionCount())
			machine.setPC(0);
	}

	@Override
	public void onRunNextInstruction(RunNextInstructionEvent event) {
		logger.log(Level.FINE,"onRunNextInstruction");
		eventBus.fireEvent(new ClearRegisterChangedHighlightsEvenet());
		executeByPC(true);
	}

	@Override
	public void onRunToNextBreakpoint(RunToNextBreakpointEvent event) {
		logger.log(Level.FINE,"onRunToNextBreakpoint");
		eventBus.fireEvent(new ClearRegisterChangedHighlightsEvenet());

		int prevPC = machine.getPC();
		do {
			executeByPC(false);
			if (prevPC == machine.getPC()) {
				break;
			}

			prevPC = machine.getPC();
		} while (instructions.getNextInstruction() != null && !instructions.getNextInstruction().isBreakpoint());

		if (machine.getPC() / 4 >= instructions.getInstructionCount())
			machine.setPC(0);
	}

	@Override
	public void onExecuteToEnd(ExecuteToEndEvent event) {
		logger.log(Level.FINE,"onExecuteToEnd");
		eventBus.fireEvent(new ClearRegisterChangedHighlightsEvenet());

		int prevPC = machine.getPC();
		while (instructions.getNextInstruction() != null) {
			executeByPC(false);
			if (prevPC == machine.getPC()) {
				break;
			}

			prevPC = machine.getPC();
		}

		if (machine.getPC() / 4 >= instructions.getInstructionCount())
			machine.setPC(0);
	}

	@Override
	public void onResetExecuton(ResetExecutionEvent event) {
		logger.log(Level.FINE,"onResetExecuton");
		eventBus.fireEvent(new ClearRegisterChangedHighlightsEvenet());
		machine.setPC(0);
	}

	@Override
	public void onResetMachine(ResetMachineEvent event) {
		logger.log(Level.FINE,"onResetMachine");
		eventBus.fireEvent(new ClearRegisterChangedHighlightsEvenet());
		assert false : "Not implemented";
	}

	private void fireEvent(GwtEvent<?> event) {
		if (eventBus != null) {
			eventBus.fireEventFromSource(event, this);
		}
	}

}
