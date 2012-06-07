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

public class RegisterViewSettings {

	private boolean floating = false;
	private int sizeInBits = 8;
	private boolean signed = false;
	private NumberFormat numberFormat = NumberFormat.decimal;

	public RegisterViewSettings() {
	}

	public RegisterViewSettings(boolean floating, int sizeInBits, boolean signed, NumberFormat numberFormat) {
		this.setFloating(floating);
		this.setSizeInBits(sizeInBits);
		this.setSigned(signed);
		this.setNumberFormat(numberFormat);

	}

	public RegisterViewSettings(RegisterViewSettings elemetViewSettings) {
		this.floating = elemetViewSettings.floating;
		this.sizeInBits = elemetViewSettings.sizeInBits;
		this.setSigned(elemetViewSettings.isSigned());
		this.numberFormat = elemetViewSettings.numberFormat;
	}

	public String getText() {
		String out = "";

		if (isFloating()) {
			out += "Float" + getSizeInBits();
			return out;
		}

		out += getNumberFormat().getText() + " ";
		out += isSigned() ? "int" : "uint";

		out += getSizeInBits();

		return out;
	}

	public boolean isEqualTo(RegisterViewSettings other) {
		if (this.isFloating() == other.isFloating())
			if (this.getSizeInBits() == other.getSizeInBits())
				if (this.getNumberFormat() == other.getNumberFormat())
					if (this.isSigned() == other.isSigned())
						return true;
		return false;
	}

	public boolean isFloating() {
		return floating;
	}

	public void setFloating(boolean floating) {
		this.floating = floating;
	}

	public int getSizeInBits() {
		return sizeInBits;
	}

	public void setSizeInBits(int sizeInBits) {
		this.sizeInBits = sizeInBits;
	}

	public NumberFormat getNumberFormat() {
		return numberFormat;
	}

	public void setNumberFormat(NumberFormat numberFormat) {
		this.numberFormat = numberFormat;
	}

	public boolean isSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

}
