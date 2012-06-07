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

public class TestcasePool {
	private List<TestCase> testCases = new LinkedList<TestCase>();
	String[] lines;

	public TestcasePool() {
		String allTestString = res.INSTANCE.synchronous().getText();
		allTestString = allTestString.replace("\r\n", "\n");
		lines = allTestString.split("\n");
		int actualStart = 0;
		int actualEnd = 0;
		for (int line = 0; line < lines.length; line++) {
			if (lines[line].trim().equals("<TESTCASE>"))
				actualStart = line + 1;
			else if (lines[line].trim().equals("</TESTCASE>")) {
				actualEnd = line;
				testCases.add(newTestCase(actualStart, actualEnd));
			}
		}
	}

	/**
	 * 
	 * @param actualStart
	 *            inclusive
	 * @param actualEnd
	 *            exclusive
	 * @return
	 */
	private TestCase newTestCase(int actualStart, int actualEnd) {
		String[] codes = new String[actualEnd - actualStart];
		for (int i = actualStart; i < actualEnd; i++) {
			codes[i - actualStart] = lines[i];
		}
		TestCase testCase = new TestCase(codes, actualStart);
		return testCase;
	}

	public List<TestCase> getTestCases() {
		return testCases;
	}

}
