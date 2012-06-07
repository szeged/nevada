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

import com.arm.nevada.client.parser.EnumInstruction;
import com.arm.nevada.client.shared.Out;

public class DataTypeTools extends FloatingDataTypeTools {

	/**
	 * @param bitSize
	 *            Number of trailing ones.
	 * @return E.g.: 0x000000FF when bitSize is 8.
	 */
	public static int getBitmask(int bitSize) {
		assert (bitSize >= 0 && bitSize <= 32);
		// because the 32 bit shift is 0 shift in java
		if (bitSize == 0){
			return 0;
		}
		int bitMask = -1;
		bitMask = bitMask >>> (32 - bitSize);
		return bitMask;
	}

	/**
	 * @param bitSize
	 *            Number of trailing ones.
	 * @return E.g.: 0x00000000000000FF when bitSize is 8.
	 */
	public static long getBitmaskLong(int bitSize) {
		assert (bitSize >= 0 && bitSize <= 64);
		// because the 64 bit shift is 0 shift in java
		if (bitSize == 0){
			return 0;
		}
		long bitMask = -1;
		bitMask = bitMask >>> (64 - bitSize);
		return bitMask;
	}

	/**
	 * 
	 * @param bitSize
	 *            Must be 1, 2, 4, 8, 16 or 32.
	 * @param value
	 *            The value to split.
	 * @return
	 */
	public static int[] getParts(int bitSize, int value) {
		assert bitSize != 64;
		int bitMask = getBitmask(bitSize);

		int[] parts = new int[32 / bitSize];
		int shift = value;
		for (int i = 0; i < 32 / bitSize; i++) {
			parts[i] = shift & bitMask;
			shift = shift >>> bitSize;
		}
		return parts;
	}

	public static int[] getParts(int bitSize, long value) {
		assert bitSize != 64;
		long bitMask = getBitmaskLong(bitSize);

		int[] parts = new int[64 / bitSize];
		long shift = value;
		for (int i = 0; i < 64 / bitSize; i++) {
			parts[i] = (int) (shift & bitMask);
			shift = shift >>> bitSize;
		}
		return parts;
	}

	/**
	 * 
	 * @param size
	 *            Maximum 32 bit!
	 * @param values
	 * @return
	 */
	public static int[] getParts(int size, int[] values) {
		int[] out = new int[values.length * 32 / size];
		for (int wordI = 0; wordI < values.length; wordI++) {
			int[] wordParts = getParts(size, values[wordI]);
			for (int partI = 0; partI < 32 / size; partI++)
				out[wordI * 32 / size + partI] = wordParts[partI];
		}
		return out;
	}

	public static int[] createWordsFromOnePartPerLong(int size, long[] parts) {
		int[] parts32;
		if (size == 64) {
			parts32 = new int[2 * parts.length];
			for (int i = 0; i < parts.length; i++) {
				int[] longParts = integerFromLong(parts[i]);
				parts32[2 * i + 0] = longParts[0];
				parts32[2 * i + 1] = longParts[1];
			}
			return parts32;
		} else {
			parts32 = new int[parts.length];
			for (int i = 0; i < parts.length; i++) {
				parts32[i] = integerFromLong(parts[i])[0];
			}
			return createWordsFromOnePartPerWord(size, parts32);
		}
	}

	public static int[] createWordsFromOnePartPerWord(int size, int[] parts) {
		assert parts.length % (32 / size) == 0;
		int[] out = new int[parts.length / (32 / size)];
		for (int wordI = 0; wordI < out.length; wordI++) {
			int[] wordParts = new int[32 / size];
			for (int partI = 0; partI < 32 / size; partI++) {
				wordParts[partI] = parts[wordI * 32 / size + partI];
			}
			out[wordI] = createByParts(wordParts);
		}
		return out;
	}

	public static long[] createPartListFromWordsLong(int bitSize, int words[]) {
		long[] out;
		if (bitSize == 64) {
			out = new long[words.length / 2];
			for (int i = 0; i < out.length; i++) {
				long part = LongFromIntegers(words[2 * i], words[2 * i + 1]);
				out[i] = part;
			}
		} else {
			int[] out32 = createPartListFromWords(bitSize, words);
			out = new long[out32.length];
			for (int i = 0; i < out32.length; i++) {
				out[i] = LongFromIntegers(out32[i], 0);
			}
		}
		return out;
	}

	public static int[] createPartListFromWords(int bitSize, int words[]) {
		int[] out = new int[words.length * 32 / bitSize];
		for (int wordI = 0; wordI < words.length; wordI++) {
			int[] wordParts = getParts(bitSize, words[wordI]);
			for (int partI = 0; partI < wordParts.length; partI++) {
				out[wordI * 32 / bitSize + partI] = wordParts[partI];
			}
		}
		return out;
	}

	public static long[] getPartsLong(int bitSize, long value) {
		long bitMask = getBitmaskLong(bitSize);
		long[] parts = new long[64 / bitSize];
		long shift = value;
		for (int i = 0; i < 64 / bitSize; i++) {
			parts[i] = shift & bitMask;
			shift = shift >>> bitSize;
		}
		return parts;
	}

	public static String formatInt(int input, boolean signed, int bitSize, int radix) {
		int out;
		if (bitSize == 32) {
			if (signed)
				out = input;
			else {
				long a = LongFromIntegers(input, 0);
				return Long.toString(a, radix).toUpperCase();
			}
		} else if (bitSize == 16) {
			input = 0xFFFF & input;
			if (!signed) {
				out = input;
			} else {
				out = extendSignedToInt(input, 16);
			}
		} else if (bitSize == 8) {
			input = 0xFF & input;
			if (!signed) {
				out = input;
			} else {
				out = extendSignedToInt(input, 8);
			}
		} else {
			assert false : "converter: invalid bit size: " + bitSize;
			out = 0;
		}
		return Integer.toString(out, radix).toUpperCase();
	}

	public static String formatLong(long input, boolean signed, int radix) {
		assert signed;
		return Long.toString(input, radix).toUpperCase();
	}

	public static String[] getPartsAsString(int input, boolean signed, int bitSize, int radix) {
		String[] outParts = new String[32 / bitSize];
		int[] intParts = getParts(bitSize, input);
		for (int i = 0; i < intParts.length; i++) {
			outParts[i] = formatInt(intParts[i], signed, bitSize, radix);
		}
		return outParts;
	}

	public static long LongFromIntegers(int lower, int higher) {
		long higherLong = (long) higher << 32;
		long lowerLong = (long) lower & 0x00000000FFFFFFFFL;
		return higherLong | lowerLong;
	}

	public static int[] integerFromLong(long value) {
		int lower = (int) (0x00000000FFFFFFFFL & value);
		int higher = (int) ((0xFFFFFFFF00000000L & value) >>> 32);
		return new int[] { lower, higher };

	}

	public static Integer parseFormattedIntegerString(String input, boolean signed, int bitSize, int radix) {
		if (bitSize == 32) {
			if (!signed) {
				try {
					long u = Long.parseLong(input, radix);
					if (u < 0)
						return null;
					else if (u > DataTypeTools.getBitmaskLong(32))
						return null;
					else
						return (int) u;
				} catch (Exception e) {
					return null;
				}
			} else {
				try {
					return Integer.parseInt(input, radix);
				} catch (NumberFormatException e) {
					return null;
				}
			}
		} else if (bitSize == 16) {
			try {
				int out = Integer.parseInt(input, radix);
				if (signed) {
					if (out < -0x8000 || out >= 0x8000)
						return null;
				} else {
					if (out < 0 || out > 0xFFFF)
						return null;
				}
				return out;

			} catch (Exception e) {
			}
		} else if (bitSize == 8) {
			try {
				int out = Integer.parseInt(input, radix);
				if (signed) {
					if (out < -0x80 || out >= 0x80)
						return null;
				} else {
					if (out < 0 || out > 0xFF)
						return null;
				}
				return out;

			} catch (Exception e) {
			}
		}
		return null;
	}

	public static Long parseFormattedLongString(String input, boolean signed, int radix) {
		assert (signed);
		try {
			return Long.parseLong(input, radix);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Assembles an int32 from parts.
	 * 
	 * @param parts
	 *            the number of parts must be 1, 2 or 4.
	 * @return
	 */
	public static int createByParts(int... parts) {
		int single = 0;
		int bitmask = getBitmask(32 / parts.length);
		for (int partIndexInWord = parts.length - 1; partIndexInWord >= 0; partIndexInWord--) {
			single = single << (32 / parts.length);
			single = single | (bitmask & parts[partIndexInWord]);
		}
		return single;
	}

	public static int parseString(String text, int radix) {
		int value = Integer.parseInt(text, radix);
		return value;
	}

	public static String valueToString(int value, int radix) {
		return Integer.toString(value, radix);
	}

	public static String valueToString(long value, int radix) {
		return Long.toString(value, radix);
	}

	public boolean IsAdvSIMDExpandImm_OLD(long value, boolean op, Boolean c3, Boolean c2, Boolean c1, Boolean c0) {
		int[] integers = integerFromLong(value);
		boolean parsed = false;
		if (op) {
			int[] lowerParts = getParts(2, integers[0]);
			int[] higherParts = getParts(2, integers[1]);
			parsed = true;
			for (int i = 0; i < lowerParts.length / 2; i += 2) {
				if (lowerParts[i] != lowerParts[i + 1] || higherParts[i] != higherParts[i + 1]) {
					parsed = false;
					break;
				}
			}
			if (parsed)
				return true;

		} else {

		}
		if (integers[0] != integers[1])
			return false;

		return false;
	}

	public static boolean isAdvSIMDExpandImm(long value, EnumInstruction inst, int bitSize) {
		long extenededValue = multiplyToLong(value, bitSize);
		return isAdvSIMDExpandImm(extenededValue, inst);
	}

	public static boolean isAdvSIMDExpandImm(int value, EnumInstruction inst, int bitSize) {
		long extenededValue = multiplyToLong(value, bitSize);
		return isAdvSIMDExpandImm(extenededValue, inst);
	}

	public static boolean isAdvSIMDExpandImm(long value, EnumInstruction inst) {
		EnumInstruction VMOV = EnumInstruction.vmov;
		EnumInstruction VORR = EnumInstruction.vorr;
		EnumInstruction VMVN = EnumInstruction.vmvn;
		EnumInstruction VBIC = EnumInstruction.vbic;
		assert inst == VMOV || inst == VORR || inst == VMVN || inst == VBIC;

		int[] parts32 = integerFromLong(value);

		if (parts32[0] == parts32[1]) {

			// 000
			if (true) {
				if ((parts32[0] | 0x000000FF) == 0x000000FF)
					return true;
			}
			// 001
			if (true) {
				if ((parts32[0] | 0x0000FF00) == 0x0000FF00)
					return true;
			}
			// 010
			if (true) {
				if ((parts32[0] | 0x00FF0000) == 0x00FF0000)
					return true;
			}
			// 011
			if (true) {
				if ((parts32[0] | 0xFF000000) == 0xFF000000)
					return true;
			}

			int[] parts16low = getParts(16, parts32[0]);
			if (parts16low[0] == parts16low[1]) {
				// 100
				if (true) {
					if ((parts16low[0] | 0x00FF) == 0x00FF)
						return true;
				}
				// 101
				if (true) {
					if ((parts16low[0] | 0xFF00) == 0xFF00)
						return true;
				}
			}
			// 110
			if (inst == VMOV || inst == VMVN) {
				// c0 == 0
				if (inst == VMOV || inst == VMVN) {
					if ((parts32[0] | 0x0000FFFF) == 0x0000FFFF && (parts32[0] & 0x000000FF) == 0x000000FF)
						return true;
				}
				// c0 == 1
				if (true) { // musn't use else
					if ((parts32[0] | 0x00FFFFFF) == 0x00FFFFFF && (parts32[0] & 0x0000FFFF) == 0x0000FFFF)
						return true;
				}
			}
		}
		// 111
		if (inst == VMOV) {
			// imm64 = Replicate(imm8, 8);
			// c0 == 0 && op == 0
			if (inst == VMOV) {
				if (parts32[0] == parts32[0]) {
					int[] parts8low = getParts(8, parts32[0]);
					if (parts8low[0] == parts8low[1]
							&& parts8low[0] == parts8low[2]
							&& parts8low[0] == parts8low[3])
						return true;
					// foundAccepteble = true;

				}
			}
			// imm64 = imm8a:imm8b:imm8c:imm8d:imm8e:imm8f:imm8g:imm8h;
			// c0 == 0 && op == 1
			if (inst == VMOV || inst == VMVN) {
				int[] parts8low = getParts(8, parts32[0]);
				int[] parts8high = getParts(8, parts32[1]);
				boolean fit = true;
				for (int i = 0; i < 4; i++)
					if ((parts8low[i] != 0xFF && parts8low[i] != 0x00)
							|| (parts8high[i] != 0xFF && parts8high[i] != 0x00)) {
						fit = false;
						break;
					}
				if (fit)
					return true;
				// foundAccepteble = true;

			}
			// imm32 = imm8<7>:NOT(imm8<6>):Replicate(imm8<6>,5):imm8<5:0>:Zeros(19);
			// imm64 = Replicate(imm32, 2);
			// c0 == 1 && op == 0
			if (inst == VMOV || inst == VMVN) {
				if (parts32[0] == parts32[0]) {
					if ((parts32[0] | 0xFFFF0000) == 0xFFFF0000)
						if ((parts32[0] << 1) >>> 31 == 0 && (parts32[0] << 2) >>> 27 == 31
								|| (parts32[0] << 1) >>> 31 == 1 && (parts32[0] << 2) >>> 27 == 0)
							return true;
				}
			}
		}
		return false;
	}

	/**
	 * Logical 32bit extension for signed numbers.
	 * 
	 * @param value
	 *            the input nmber
	 * @param bitSize
	 *            The width of the input number
	 * @return 32 bit signed, with the same logical value as the input.
	 */
	public static int extendSignedToInt(int value, int bitSize) {
		int significantPart = getParts(bitSize, value)[0];
		if ((significantPart >>> bitSize - 1) == 0)
			return significantPart;
		else
			return onesExtendInt(value, bitSize);
	}

	public static long extendToSingnedLong(long value, int bitSize) {
		value = value & getBitmaskLong(bitSize);
		if (bitSize == 64)
			return value;
		else {
			int valueInt = extendSignedToInt(integerFromLong(value)[0], bitSize);
			if (valueInt >= 0) {
				return LongFromIntegers(valueInt, 0);
			} else {
				return LongFromIntegers(valueInt, -1); // FF..FValueInt
			}
		}
	}

	/**
	 * Fills with ones every bit with higher= index than the "bitsize".
	 * 
	 * @param value
	 * @param bitSize
	 * @return
	 */
	private static int onesExtendInt(int value, int bitSize) {
		int ones = ~getBitmask(bitSize);
		return ones | value;
	}

	/**
	 * Repeats the lower bits of the "value" until it fills a 64 bit long.
	 * 
	 * @param value
	 * @param bitSize
	 *            The amount of significant bits in the value.
	 * @return
	 */
	public static long multiplyToLong(int value, int bitSize) {
		int significand = getParts(bitSize, value)[0];
		int[] wordParts = new int[32 / bitSize];
		for (int i = 0; i < wordParts.length; i++) {
			wordParts[i] = significand;
		}
		int extendedToWord = createByParts(wordParts);
		long out = LongFromIntegers(extendedToWord, extendedToWord);

		return out;
	}

	/**
	 * Repeats the lower bits of the "value" until it fills a 64 bit long.
	 * 
	 * @param value
	 * @param bitSize
	 *            The amount of significant bits in the value.
	 * @return
	 */
	public static long multiplyToLong(long value, int bitSize) {
		if (bitSize == 64)
			return value;
		int word = integerFromLong(value)[0];
		return multiplyToLong(word, bitSize);
	}

	/**
	 * left > right
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	public static boolean unsignedGreaterThan(int left, int right) {
		return (right ^ left) >= 0 ? right < left : right >= 0;
	}

	/**
	 * left >= right
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	public static boolean unsignedGreaterEqualThan(int left, int right) {
		if (left == right)
			return true;
		else
			return unsignedGreaterThan(left, right);
	}

	/**
	 * left > right
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	public static boolean unsignedGreaterThan(long left, long right) {
		return (right ^ left) >= 0 ? right < left : right >= 0;
	}

	/**
	 * left >= right
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	public static boolean unsignedGreaterEqualThan(long left, long right) {
		if (left == right)
			return true;
		else
			return unsignedGreaterThan(left, right);
	}

	/**
	 * 
	 * @param left
	 * @param right
	 * @param size
	 *            size > 0 && size <=32
	 * @return
	 */
	public static boolean unsignedGreaterThan(int left, int right, int size) {
		assert size > 0 && size <= 32;
		if (size == 32)
			return unsignedGreaterThan(left, right);
		left = left & getBitmask(size);
		right = right & getBitmask(size);
		return left > right;
	}

	/**
	 * 
	 * @param left
	 * @param right
	 * @param size
	 *            size > 0 && size <=32
	 * @return
	 */
	public static boolean signedGreaterThan(int left, int right, int size) {
		assert size > 0 && size <= 32;
		left = left << (32 - size);
		right = right << (32 - size);
		return left > right;
	}

	public static boolean greaterThan(int left, int right, int size, boolean signed) {
		if (signed)
			return signedGreaterThan(left, right, size);
		else
			return unsignedGreaterThan(left, right, size);
	}

	// /////
	// Saturation stuffs
	// /////
	public static boolean isSaturatingSigned(int value, int sizeInBits) {
		if (value > getBitmask(sizeInBits - 1))
			return true;
		else if (value < -(getBitmask(sizeInBits - 1) + 1))
			return true;
		else
			return false;
	}

	public static int getSaturatingSigned(int value, int sizeInBits) {
		if (value > getBitmask(sizeInBits - 1))
			return getBitmask(sizeInBits - 1);
		else if (value < ((-1 & ~getBitmask(sizeInBits)) | (getBitmask(sizeInBits - 1) + 1)))
			return ((-1 & ~getBitmask(sizeInBits)) | (getBitmask(sizeInBits - 1) + 1));
		else
			return getBitmask(sizeInBits) & value;
	}

	public static int getSaturatingUnsigned(int value, int sizeInBits) {
		if (DataTypeTools.unsignedGreaterThan(value, getBitmaskLong(sizeInBits)))
			return getBitmask(sizeInBits);
		else
			return getBitmask(sizeInBits) & value;
	}

	public static int getSaturating(int value, int sizeInBits, boolean signed) {
		if (signed)
			return getSaturatingSigned(value, sizeInBits);
		else
			return getSaturatingUnsigned(value, sizeInBits);
	}

	// ///////////

	public static long getSaturatingSignedLong(long value, int sizeInBits) {
		if (value > getBitmaskLong(sizeInBits - 1))
			return getBitmaskLong(sizeInBits - 1);
		else if (value < ((-1l & ~getBitmaskLong(sizeInBits)) | (getBitmaskLong(sizeInBits - 1) + 1)))
			return ((-1l & ~getBitmaskLong(sizeInBits)) | (getBitmaskLong(sizeInBits - 1) + 1));
		else
			return getBitmaskLong(sizeInBits) & value;
	}

	public static boolean isSaturatingSignedLong(long value, int sizeInBits) {
		if (value > getBitmaskLong(sizeInBits - 1))
			return true;
		else if (value < ((-1l & ~getBitmaskLong(sizeInBits)) | (getBitmaskLong(sizeInBits - 1) + 1)))
			return true;
		else
			return false;
	}

	public static long getSaturatingUnsignedLong(long value, int sizeInBits) {
		if (DataTypeTools.unsignedGreaterThan(value, getBitmaskLong(sizeInBits)))
			return getBitmaskLong(sizeInBits);
		else
			return getBitmaskLong(sizeInBits) & value;
	}

	public static boolean isSaturatingUnsignedLong(long value, int sizeInBits) {
		if (DataTypeTools.unsignedGreaterThan(value, getBitmaskLong(sizeInBits)))
			return true;
		else
			return false;
	}

	public static long getSaturatingLong(long value, int sizeInBits, boolean signed) {
		if (signed)
			return getSaturatingSignedLong(value, sizeInBits);
		else
			return getSaturatingUnsignedLong(value, sizeInBits);
	}

	public static boolean isSaturatingLong(long value, int sizeInBits, boolean signed) {
		if (signed)
			return isSaturatingSignedLong(value, sizeInBits);
		else
			return isSaturatingUnsignedLong(value, sizeInBits);
	}

	public static boolean isNegative(int source, int size) {
		int extendedNumber = extendSignedToInt(source, size);
		return extendedNumber < 0;
	}

	/**
	 * 
	 * @param source
	 *            Unsigned number.
	 * @param size
	 *            Number of significant bits
	 * @return Number of leading zeros.
	 */
	public static int countLeadingZeros(int source, int size) {
		int count = size;
		source = source & getBitmask(size);
		while (source != 0) {
			source = source >>> 1;
			count--;
		}
		return count;
	}

	/**
	 * 
	 * @param source
	 *            Unsigned number.
	 * @param size
	 *            Number of significant bits
	 * @return Number of leading zeros.
	 */
	public static int countLeadingZeros(long source, int size) {
		int count = size;
		source = source & getBitmaskLong(size);
		while (source != 0) {
			source = source >>> 1;
			count--;
		}
		return count;
	}

	/**
	 * Returns a "size" sized number, with the specified arguments.
	 * 
	 * @param size
	 * @param signed
	 * @return
	 */
	public static int getMaxValue(int size, boolean signed) {
		assert size > 0;
		if (signed) {
			return getBitmask(size - 1);
		} else {
			return getBitmask(size);
		}
	}

	/**
	 * Returns a "size" sized number, with the specified arguments.
	 * 
	 * @param size
	 *            64; 32; 16; 8;
	 * @param signed
	 * @return
	 */
	public static long getMaxValueLong(int size, boolean signed) {
		assert size > 0;
		if (signed) {
			return getBitmaskLong(size - 1);
		} else {
			return getBitmaskLong(size);
		}
	}

	/**
	 * Returns a "size" sized number, with the specified arguments.
	 * 
	 * @param size
	 * @param signed
	 * @return
	 */
	public static int getMinValue(int size, boolean signed) {
		assert size > 0;
		if (signed) {
			return ~DataTypeTools.getBitmask(size - 1); // 1 << (size - 1);
		} else {
			return 0;
		}
	}

	/**
	 * Returns a "size" sized number, with the specified arguments.
	 * 
	 * @param size
	 * @param signed
	 * @return
	 */
	public static long getMinValueLong(int size, boolean signed) {
		assert size > 0;
		if (signed) {
			return ~DataTypeTools.getBitmaskLong(size - 1);// 1l << (size - 1);
		} else {
			return 0;
		}
	}

	public static int setBit(int source, boolean set, int bitIndex) {
		assert bitIndex >= 0 && bitIndex < 32;
		int bitmask = 1 << bitIndex;
		if (set)
			return source | bitmask;
		else
			return source & ~bitmask;
	}

	public static boolean getBit(int value, int index) {
		assert index >= 0 && index < 32;
		int bitmask = 1 << index;
		value = value & bitmask;
		return value != 0;
	}

	public static Integer saturateMax32bit(int originalSize, int outSize, long source, int leftShiftAmount, boolean typeUnsigned, boolean satUnsigned) {
		assert originalSize >= outSize;
		if (source == 0 || leftShiftAmount == 0) {
			return null;
		}
		// boolean typeUnsigned = dataType.getSigned() == null || !dataType.getSigned();
		// boolean satUnsigned = saturatingUnsigned;

		boolean numberNegative = !typeUnsigned && source < 0;

		if (leftShiftAmount < 0 && -leftShiftAmount >= originalSize) {
			return null;
		}

		if (satUnsigned) {
			assert !typeUnsigned;
			if (numberNegative) {
				return 0; // 0
			} else {
				int clz = DataTypeTools.countLeadingZeros(source, originalSize);
				// if (leftShiftAmount >= 0)
				if (clz - (originalSize - outSize) < leftShiftAmount)
					return DataTypeTools.getBitmask(outSize); // 11..1
			}

		} else {
			if (typeUnsigned) {
				int clz = DataTypeTools.countLeadingZeros(source, originalSize);
				if (clz - (originalSize - outSize) < leftShiftAmount)
					return DataTypeTools.getBitmask(outSize);
			} else {
				if (numberNegative) {
					int clo = DataTypeTools.countLeadingZeros(~source, originalSize) - 1;
					if (clo - (originalSize - outSize) < leftShiftAmount)
						return 1 << (outSize - 1);
				} else {
					int clz = DataTypeTools.countLeadingZeros(source, originalSize) - 1;
					if (clz - (originalSize - outSize) < leftShiftAmount)
						return DataTypeTools.getBitmask(outSize - 1); // DataTypeTools.getMaxValue(size, true);
				}
			}
		}
		return null;
	}

	public static Long saturate64bit(int originalSize, int outSize, long source, int leftShiftAmount, boolean typeUnsigned, boolean satUnsigned) {
		assert originalSize >= outSize;
		if (source == 0 || leftShiftAmount == 0) {
			return null;
		}
		// boolean typeUnsigned = dataType.getSigned() == null || !dataType.getSigned();
		// boolean satUnsigned = saturatingUnsigned;

		boolean numberNegative = !typeUnsigned && source < 0;

		if (satUnsigned) {
			assert !typeUnsigned;
			if (numberNegative) {
				return 0l; // 0
			} else {
				int clz = DataTypeTools.countLeadingZeros(source, originalSize);
				if (clz - (originalSize - outSize) < leftShiftAmount)
					return DataTypeTools.getBitmaskLong(outSize); // 11..1
			}

		} else {
			if (typeUnsigned) {
				int clz = DataTypeTools.countLeadingZeros(source, originalSize);
				if (clz - (originalSize - outSize) < leftShiftAmount)
					return DataTypeTools.getBitmaskLong(outSize);
			} else {
				if (numberNegative) {
					int clo = DataTypeTools.countLeadingZeros(~source, originalSize) - 1;
					if (clo - (originalSize - outSize) < leftShiftAmount)
						return 1l << (outSize - 1);
				} else {
					int clz = DataTypeTools.countLeadingZeros(source, originalSize) - 1;
					if (clz - (originalSize - outSize) < leftShiftAmount)
						return DataTypeTools.getBitmaskLong(outSize - 1); // DataTypeTools.getMaxValue(size, true);
				}
			}
		}
		return null;
	}

	public static long polynominalMultiplicate(int op1, int op1Size, int op2, int op2Size) {
		assert op1Size <= 32;
		assert op2Size <= 32;
		op1 = op1 & getBitmask(op1Size);
		op2 = op2 & getBitmask(op2Size);
		long result = 0;
		long extended_op2 = LongFromIntegers(op2, 0);
		for (int i = 0; i < op1Size - 1; i++)
			if (getBit(op1, i))
				result = result ^ (extended_op2 << i);
		return result;
	}

	public static long signedSatQ(long value, int size, Out<Boolean> saturated) {
		long maxValue = getBitmaskLong(size - 1);
		saturated.setValue(false);
		if (value > maxValue) {
			saturated.setValue(true);
			return maxValue;
		}
		long minValue = ~getBitmaskLong(size - 1);
		if (value < minValue) {
			saturated.setValue(true);
			return minValue;
		}
		return value;
	}

	public static long signedSaturatingAdd(long op1, long op2, int size, Out<Boolean> outSaturated) {
		op1 = extendToSingnedLong(op1, size);
		op2 = extendToSingnedLong(op2, size);
		long result = op1 + op2;
		if (op1 >= 0 && op2 >= 0) {
			if (result < 0) {
				outSaturated.setValue(true);
				return getMaxValueLong(size, true);
			}
		}
		if (op1 < 0 && op2 < 0) {
			if (result >= 0) {
				outSaturated.setValue(true);
				return getMinValueLong(size, true);
			}
		}
		result = signedSatQ(result, size, outSaturated);
		return result;
	}

	/**
	 * 
	 * @param partList
	 * @param size
	 *            size should be 8, 16, 32 or 64
	 * @return
	 */
	public static int[] createWordsFromPartArrayLong(long[] partList, int size) {
		if (size == 64) {
			int[] out = new int[partList.length * 2];
			for (int i = 0; i < partList.length; i++) {
				int[] wordPair = integerFromLong(partList[i]);
				out[i * 2 + 0] = wordPair[0];
				out[i * 2 + 1] = wordPair[1];
			}
			return out;
		} else {
			int[] partList32 = new int[partList.length];
			for (int i = 0; i < partList.length; i++) {
				partList32[i] = integerFromLong(partList[i])[0];
			}
			return createWordsFromPartArray(partList32, size);
		}
	}

	/**
	 * 
	 * @param partList
	 * @param size
	 *            size shoud be 8, 16 or 32
	 * @return
	 */
	public static int[] createWordsFromPartArray(int[] partList, int size) {
		int[] out = new int[partList.length / (32 / size)];
		for (int wordI = 0; wordI < out.length; wordI++) {
			int[] parts = new int[32 / size];
			for (int partI = 0; partI < 32 / size; partI++) {
				parts[partI] = partList[wordI * 32 / size + partI];
			}
			out[wordI] = createByParts(parts);
		}
		return out;
	}

	public static int saturatingLogicalShift(int value, int leftShiftAmount, boolean signed) {
		if (leftShiftAmount == 0)
			return value;
		if (leftShiftAmount > 32 || leftShiftAmount < -32)
			leftShiftAmount = leftShiftAmount < 0 ? -32 : 32;
		if (leftShiftAmount >= 32) {

		}
		int result;
		if (leftShiftAmount > 0) {
			result = value << leftShiftAmount;
		} else {
			if (signed) {
				result = value >> leftShiftAmount;
			} else {
				result = value >>> leftShiftAmount;
			}
		}
		return result;
	}
}
