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

import java.util.List;

import org.junit.Test;

import com.arm.nevada.client.interpreter.Instruction;
import com.arm.nevada.client.interpreter.machine.Machine;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.junit.client.GWTTestCase;

public class InstructionTest extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "com.arm.nevada.Nevada";
	}

	@Test
	public void testExecute() {
		TestcasePool testCasePool = new TestcasePool();
		List<TestCase> testCases = testCasePool.getTestCases();

		int failedTests = 0;
		int testCounter = 0;
		for (TestCase testCase : testCases) {
			testCounter++;
			Machine machine = new Machine(new SimpleEventBus());
			machine.init(testCase.getInputMachineAsJSON());
			for (Instruction instruction : testCase.getInputInstructions()) {
				instruction.execute(machine);
			}
			String realOutput = machine.getAsJSONObject().toString();
			String expectedOutput = testCase.getExpectedOutputMachine();
			if (expectedOutput.equals(realOutput))
				System.out.println("OK #" + testCounter + " @line: " + testCase.getStartLine());
			else {
				System.out.println("FAILED #" + testCounter + " @line: " + testCase.getStartLine());
				System.out.println("EXPECTED:\n" + expectedOutput);
				System.out.println("BUT GET :\n" + realOutput);
				failedTests++;
			}
		}
		assertTrue("Number of failed tests: " + failedTests, failedTests == 0);
	}
}
