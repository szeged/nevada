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

package com.arm.nevada.client.view.shared;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.NativeEvent;

public class SelectionManager {
	private static final Logger logger = Logger.getLogger(SelectionManager.class.getName());
	private boolean[] selectedList;
	private int lastClickedIndex = 0;

	// minimum size: 1;
	public SelectionManager(int size) {
		selectedList = new boolean[size];
		selectedList[0] = true;
	}

	public void change(int nowClicked, boolean ctrl, boolean shift) {
		if (shift) {
			int from = nowClicked > getLastClickedIndex() ? getLastClickedIndex() : nowClicked;
			int to = nowClicked < getLastClickedIndex() ? getLastClickedIndex() : nowClicked;
			for (int i = from; i <= to; i++) {
				selectedList[i] = true;
			}
		} else if (ctrl) {
			selectedList[nowClicked] = true;
		} else {
			Arrays.fill(selectedList, false);
			selectedList[nowClicked] = true;
		}
		lastClickedIndex = nowClicked;
	}

	public void change(int index, NativeEvent event) {
		change(index, event.getCtrlKey(), event.getShiftKey());
	}

	public int getFirstSelected() {
		for (int i = 0; i < selectedList.length; i++) {
			if (selectedList[i]) {
				return i;
			}
		}
		logger.log(Level.FINE, "There is no selected item. The first has been selected.");
		selectedList[0] = true;
		return 0;

	}

	public boolean isSelected(int i) {
		return selectedList[i];
	}

	public int getLastClickedIndex() {
		return lastClickedIndex;
	}

	public void setSize(int newSize) {
		if (selectedList.length == newSize)
			return;
		boolean[] newSelectedList = new boolean[newSize];
		for (int i = 0; i < selectedList.length && i < newSize; i++)
			newSelectedList[i] = selectedList[i];
		if (lastClickedIndex >= newSize)
			lastClickedIndex = newSize - 1;

		selectedList = newSelectedList;
	}
}
