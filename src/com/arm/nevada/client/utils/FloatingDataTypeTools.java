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

import com.google.gwt.i18n.client.NumberFormat;

public class FloatingDataTypeTools {
	/*
	 * FLOAT STUFFS
	 */
	public static float Float32Value(int input, int fpscr) {
		int exp = (input & 0x7f800000) >>> 23;
		int frac = (input & 0x007fffff);
		int sign = (input & 0x80000000) >>> 63;
		float value;
		if (exp == 0) {
			if (frac == 0 || DataTypeTools.getBit(fpscr, 24) == true) {
				// type = zero
				value = 0.0f;
				if (frac != 0) { // denormalized input flushed to zero
					// FPProcessException(FPExc_InputDenorm, fpscr_val);
				}
			} else {
				// type == nonzero;
				// value = (float) (Math.pow(2.0f, -126.0f) * (frac * Math.pow(2.0f, -23.0f)));
				value = DataTypeTools.intToFloat(input);
			}
		} else if (exp == 0xFF) {
			if (frac == 0) {
				if (sign == 1)
					value = Float.POSITIVE_INFINITY;
				else
					value = Float.NEGATIVE_INFINITY;
			} else {
				value = Float.NaN;
			}
		} else {
			value = DataTypeTools.intToFloat(input & 0x7fffffff);
		}

		return value;
	}

	// First do this
	public static float intToFloat(int from) {
		float single = Numbers.intBitsToFloat(from);
		return single;
	}

	public static double longToDouble(long from) {
		double doubleVal = Numbers.longBitsToDouble(from);
		return doubleVal;
	}

	public static int FloatToInt(float from) {
		int bits = Numbers.floatToIntBits(from);
		return bits;
		// return parseFloatToUnsignedItneger(from + "");
	}

	/**
	 * TODO: FIXME:THIS ISN'T PROVIDE EXACT VALUE!
	 * 
	 * @param from
	 * @return
	 */
	public static long DoubleToLong(Double from) {
		// long bits = Double.doubleToLongBits(from);
		long bits = Numbers.doubleToLongBits(from);
		return bits;
	}

	public static String FormatFloatInt(int from) {
		float parsedFloat = intToFloat(from);
		String outString = NumberFormat.getFormat("0.########E0").format(parsedFloat);
		return outString;
	}

	public static Integer parseFloatToUnsignedInteger(String floatString) {
		floatString = floatString.trim().toLowerCase();
		float out;
		try {
			out = Float.parseFloat(floatString);
		} catch (Exception e) {
			if (floatString.equals("infinity")
					|| floatString.equals("+infinity")
					|| floatString.equals("+inf")
					|| floatString.equals("inf"))
				return 0x7f800000;
			else if (floatString.equals("-infinity") || floatString.equals("-inf"))
				return 0xff800000;
			else if (floatString.equals("nan") || floatString.equals("qnan"))
				return 0x7fc00000;
			else if (floatString.equals("snan"))
				return 0x7f800001;
			else
				return null;
		}
		return FloatToInt(out);
	}

	/**
	 * +127 offset to the real exponent. 8 bit.
	 * 
	 * @param value
	 * @return
	 */
	public int getFloatExponentRaw(float value) {
		int intValue = DataTypeTools.FloatToInt(value);
		return getFloatExponentRaw(intValue);
	}

	/**
	 * +127 offset to the real exponent. 8 bit.
	 * 
	 * @param value
	 * @return
	 */
	public static int getFloatExponentRaw(int value) {
		int result = (value >>> 23) & 0xFF;
		return result;
	}
}
