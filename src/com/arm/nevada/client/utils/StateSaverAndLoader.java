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

package com.arm.nevada.client.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.arm.nevada.client.interpreter.machine.Machine;
import com.arm.nevada.client.view.InstructionEditor;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Window;

public class StateSaverAndLoader {
	private static final Logger logger = Logger.getLogger(StateSaverAndLoader.class.getName());
	private final InstructionEditor instructionEditor;
	private final Machine machine;

	public StateSaverAndLoader(Machine machine, InstructionEditor instructionEditor) {
		this.machine = machine;
		this.instructionEditor = instructionEditor;

	}

	public static void restoreState(Machine machine, InstructionEditor instructionEditor) {
		String machineQuery = com.google.gwt.user.client.Window.Location.getParameter("machine");
		if (machineQuery != null) {
			JSONObject machineJson = (JSONObject) JSONParser.parseStrict(URL.decode(machineQuery));
			machine.init(machineJson);
		}

		String codeQuery = com.google.gwt.user.client.Window.Location.getParameter("code");
		if (codeQuery != null) {
			JSONObject codeJson = (JSONObject) JSONParser.parseStrict(URL.decode(codeQuery));
			instructionEditor.init(codeJson);
		}
	}

	public void setMachine(String JSONString) {
		JSONObject machineJson = (JSONObject) JSONParser.parseStrict(JSONString);
		machine.init(machineJson);
	}

	public void setInstructionEditor(String JSONString) {
		JSONObject codeJson = (JSONObject) JSONParser.parseStrict(JSONString);
		instructionEditor.init(codeJson);
	}

	public static String saveState(Machine machine, InstructionEditor instructionEditor) {
		// String href = com.google.gwt.user.client.Window.Location.getHref();
		String href = Window.Location.getHost() + Window.Location.getPath();

		String debug = Window.Location.getParameter("gwt.codesvr");
		if (debug != null)
			href += "?gwt.codesvr=" + debug;
		logger.log(Level.FINE, "href: " + href);
		JSONObject machineState = machine.getAsJSONObject();
		JSONObject editorState = instructionEditor.getAsJSONObject();

		String machineEncoded = URL.encodePathSegment(machineState.toString());
		String editorEncoded = URL.encodePathSegment(editorState.toString());

		String fullURL = href;
		if (debug == null) {
			fullURL += '?';
		} else {
			fullURL += '&';
		}
		fullURL += "machine=" + machineEncoded + "&code=" + editorEncoded;

		System.out.println("out: \n" + fullURL + "\n\n");
		return fullURL;
	}

	public String getStateURL() {
		return saveState(machine, instructionEditor);
	}

	public JSONObject getMachineJSON() {
		return machine.getAsJSONObject();
	}

	public JSONObject getInstructionEditorJSON() {
		return instructionEditor.getAsJSONObject();
	}
}
