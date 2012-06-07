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

package com.arm.nevada.client.parser;

import com.arm.nevada.client.interpreter.EnumDataType;
import com.arm.nevada.client.shared.Out;
import com.arm.nevada.client.utils.DataTypeTools;

public class Utils {

	/**
	 * Parses only valid DIGITS according to the radix. (Can't read sign.)
	 * 
	 * @param instruction
	 * @param pos
	 * @param radix
	 * @param parsed
	 * @return
	 */
	private static String getIntegerString(String instruction, int pos, int radix) {
		if (instruction.length() <= pos)
			throw new IndexOutOfBoundsException("pos: " + pos + " length: " + instruction.length());
		final int start = pos;
		while (instruction.length() > pos && isValidCharacterInRadix(instruction.charAt(pos), radix)) {
			pos++;
		}
		return instruction.substring(start, pos);
	}

	/**
	 * Reads characters which is valid to a float number. It's not guaranteed that the parsed string is a valid number.
	 * 
	 * @param instruction
	 * @param pos
	 * @return The parsed string.
	 */
	private static String getNumericFloatString(String instruction, int pos) {
		if (instruction.length() <= pos)
			throw new IndexOutOfBoundsException("pos: " + pos + " length: " + instruction.length());
		final int start = pos;
		char atPos;
		while (instruction.length() > pos) {
			atPos = instruction.charAt(pos);
			if ((atPos >= '0' && atPos <= '9') || atPos == '.' || atPos == '-' || atPos == 'e' || atPos == 'E')
				pos++;
			else
				break;
		}
		return instruction.substring(start, pos);
	}

	/**
	 * Parses characters which valid for a float number.
	 * 
	 * @param instruction
	 * @param pos
	 * @param decimals
	 *            The string ncluding the float.
	 * @return
	 */
	private static int getFloatParts(String instruction, int pos, Out<String> decimals) {
		if (instruction.length() <= pos) {
			assert false;
			return -1;
		}
		instruction = instruction.toLowerCase();

		String numericString = getNumericFloatString(instruction, pos);
		if (numericString == null)
			return -pos - 1;
		pos += numericString.length();
		decimals.setValue(numericString);
		return pos;
	}

	/**
	 * Parses any number that fits to an Integer.
	 * 
	 * @param instruction
	 * @param pos
	 * @param parsed
	 *            Out parameter!
	 * @return The position after parsing.
	 */
	public static int parseInteger(String instruction, int pos, Out<Integer> parsed) {

		Out<Integer> radixOut = new Out<Integer>();
		Out<Boolean> negativeOut = new Out<Boolean>();
		Out<String> decimals = new Out<String>();
		int savedPos = pos;
		pos = getNumberParts(instruction, pos, radixOut, negativeOut, decimals);
		if (pos < 0) {
			return -savedPos - 1;
		}

		String number;
		if (negativeOut.getValue()) {
			number = "-" + decimals.getValue();
		} else {
			number = decimals.getValue();
		}

		int currentParsed;
		try {
			currentParsed = Integer.parseInt(number, radixOut.getValue());
		} catch (Exception e) {
			return -savedPos - 1;
		}
		parsed.setValue(currentParsed);
		return pos;
	}

	/**
	 * Parses any number that fits to a Long.
	 * 
	 * @param instruction
	 * @param pos
	 * @param parsed
	 *            Out parameter!
	 * @return The position after parsing.
	 */
	public static int parseLong(String instruction, int pos, Out<Long> parsed) {

		Out<Integer> radixOut = new Out<Integer>();
		Out<Boolean> negativeOut = new Out<Boolean>();
		Out<String> decimals = new Out<String>();
		int savedPos = pos;
		pos = getNumberParts(instruction, pos, radixOut, negativeOut, decimals);
		if (pos < 0) {
			return -savedPos - 1;
		}

		String number;
		if (negativeOut.getValue()) {
			number = "-" + decimals.getValue();
		} else {
			number = decimals.getValue();
		}

		long currentParsed;
		try {
			currentParsed = Long.parseLong(number, radixOut.getValue());
		} catch (Exception e) {
			return -savedPos - 1;
		}
		parsed.setValue(currentParsed);
		return pos;
	}

	/**
	 * Parse a number, checks the size to fit in the defined size, when fits then expand the read number to long, by
	 * repeating. Positive and negative numbers are also accepted.<br>
	 * If the input number is positive then the interval is [0, 2<sup>size</sup>]<br>
	 * If the input number is negative then the interval is [-2<sup>size-1</sup>, 2<sup>size-1</sup>-1]<br>
	 * 
	 * @param instruction
	 *            the input string
	 * @param pos
	 *            Starting position in the input string
	 * @param parsed
	 *            Output parameter.
	 * @return The character index after parsing the number, if can't parse then -1.
	 */
	public static int parseAndExtendByBitSize(String instruction, int pos, EnumDataType type, Out<Long> parsed) {
		int bitSize = type.getSizeInBits();

		Out<Integer> radixOut = new Out<Integer>();
		Out<Boolean> negativeOut = new Out<Boolean>();
		Out<String> decimals = new Out<String>();

		if (type == EnumDataType._f32) {
			int savedPos = pos;
			pos = getFloatParts(instruction, pos, decimals);
			float value;
			try {
				value = Float.parseFloat(decimals.getValue());
			} catch (NumberFormatException e) {
				return -savedPos - 1;
			}
			Integer valueAsInt = DataTypeTools.parseFloatToUnsignedInteger(value + "");
			if (valueAsInt == null)
				return -savedPos - 1;
			parsed.setValue(DataTypeTools.multiplyToLong(valueAsInt, bitSize));
			return pos;
		} else {
			int savedPos = pos;
			pos = getNumberParts(instruction, pos, radixOut, negativeOut, decimals);

			if (pos < 0) {
				return -savedPos - 1;
			}

			long absoluteValue;

			if (bitSize == 64) {
				try {
					parsed.setValue(create64BitLong(negativeOut.getValue(), decimals.getValue(), radixOut.getValue()));
				} catch (NumberFormatException s) {
					return -1;
				}
				return pos;
			} else {
				try {
					absoluteValue = Long.parseLong(decimals.getValue(), radixOut.getValue());
				} catch (NumberFormatException s) {
					return -1;
				}

				int negativeMax = (DataTypeTools.getBitmask(bitSize) >>> 1) + 1;
				int positive = DataTypeTools.getBitmask(bitSize);
				long max = DataTypeTools.LongFromIntegers(negativeOut.getValue() ? negativeMax : positive, 0);
				if (DataTypeTools.unsignedGreaterThan(absoluteValue, max))
					return -savedPos - 1;
				try {
					long number = create64BitLong(negativeOut.getValue(), decimals.getValue(), radixOut.getValue());
					long out = DataTypeTools.multiplyToLong(number, bitSize);
					parsed.setValue(out);
					return pos;
				} catch (NumberFormatException e) {
					assert false : "It should never happen";
					return -savedPos - 1;
				}
			}
		}
	}

	/**
	 * Reads any integer number. The result is returned in String.
	 * 
	 * @param instruction
	 * @param pos
	 * @param radixOut
	 *            The detected radix. If starts with "0x" or "-0x" then 16, else 10.
	 * @param negativeOut
	 *            True, when negative, else false.
	 * @param decimals
	 *            The digits corresponding to the parsed number.
	 * @return The end position after parsing.
	 */
	static int getNumberParts(String instruction, int pos, Out<Integer> radixOut, Out<Boolean> negativeOut, Out<String> decimals) {
		if (instruction.length() <= pos)
			assert false;
		instruction = instruction.toLowerCase();
		boolean negative = false;
		char atPos = instruction.charAt(pos);
		if (atPos == '-') {
			negative = true;
			pos++;
		} else if (atPos == '+') {
			negative = false;
			pos++;
		}
		if (instruction.length() <= pos)
			return -pos - 1;
		atPos = instruction.charAt(pos);
		int radix = 10;
		if (atPos == '0' && instruction.length() > pos + 1 && instruction.charAt(pos + 1) == 'x') {
			radix = 16;
			pos += 2;
			if (instruction.length() <= pos)
				return -pos - 1;
			atPos = instruction.charAt(pos);
		}
		String numericString = getIntegerString(instruction, pos, radix);
		if (numericString == null)
			return -pos - 1;
		pos += numericString.length();

		radixOut.setValue(radix);
		negativeOut.setValue(negative);
		decimals.setValue(numericString);
		return pos;
	}

	/**
	 * Returns the value of the character in the defined radix.
	 * 
	 * @param character
	 * @param radix
	 * @return
	 * @throws IllegalArgumentException
	 */
	private static int getCharacterValue(char character, int radix) {
		int value = Character.digit(character, radix);
		if (value == -1)
			throw new IllegalArgumentException("Invalid radix: " + radix + " + character: " + character + " pair");
		if (radix < 2 || radix > Character.MAX_RADIX)
			throw new IllegalArgumentException("Invalid prefix: " + radix);
		if (value >= radix)
			throw new IllegalArgumentException("Invalid radix: " + radix + " character: " + character + " pair");
		return value;
	}

	/**
	 * Decides is it a valid character in the defined radix.
	 * 
	 * @param character
	 * @param radix
	 * @return
	 */
	private static boolean isValidCharacterInRadix(char character, int radix) {
		try {
			getCharacterValue(character, radix);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Parses a signed or an unsigned number to a long.
	 * 
	 * @param negative
	 * @param number
	 * @param radix
	 * @return
	 * @throws NumberFormatException
	 */
	public static long create64BitLong(boolean negative, String number, int radix) throws NumberFormatException {
		if (negative) {
			number = "-" + number;
			return Long.parseLong(number, radix);
		} else {
			if (number.length() <= 2) {
				return Long.parseLong(number, radix);
			}

			String higherPart = number.substring(0, number.length() - 1);
			String lowerPart = number.substring(number.length() - 1, number.length());
			long high = Long.parseLong(higherPart, radix);
			long low = Long.parseLong(lowerPart, radix);
			if (DataTypeTools.unsignedGreaterEqualThan(high, 0xFFFFFFFFFFFFFFFFl - low))
				throw new NumberFormatException(number);
			long out = high * radix + low;
			return out;
		}
	}

	public static int parseChar(String instruction, int pos, char c) {
		if (pos >= instruction.length())
			assert false;
		if (instruction.length() <= pos)
			return -1;
		if (instruction.charAt(pos) != c)
			return -pos - 1;
		else
			return pos + 1;
	}

	public static int parseCharSurroundedByWhitespace(String instruction, int pos, char c) {
		pos = WhiteSpace.p().parse(instruction, pos, null).getPosition();
		int savedPos = pos;
		pos = parseChar(instruction, pos, c);
		if (pos < 0)
			return -savedPos - 1;
		pos = WhiteSpace.p().parse(instruction, pos, null).getPosition();
		return pos;
	}

	public static int parseCharAndWhitespace(String instruction, int pos, char c) {
		int savedPos = pos;
		pos = parseChar(instruction, pos, c);
		if (pos < 0)
			return -savedPos - 1;
		pos = WhiteSpace.p().parse(instruction, pos, null).getPosition();
		return pos;
	}
}
