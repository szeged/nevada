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

public enum EnumDataType {
	None(null, null, null, false, false, false, false, null, null),

	_8(".8", null, 8, false, false, false, true, null, null),
	_i8(".i8", null, 8, false, false, false, true, null, null),
	_s8(".s8", true, 8, false, false, false, true, null, null),
	_u8(".u8", false, 8, false, false, false, true, null, null),

	_16(".16", null, 16, false, false, false, true, null, null),
	_i16(".i16", null, 16, false, false, false, true, null, null),
	_s16(".s16", true, 16, false, false, false, true, null, null),
	_u16(".u16", false, 16, false, false, false, true, null, null),

	_32(".32", null, 32, false, false, false, true, null, null),
	_i32(".i32", null, 32, false, false, false, true, null, null),
	_s32(".s32", true, 32, false, false, false, true, null, null),
	_u32(".u32", false, 32, false, false, false, true, null, null),

	_f16(".f16", null, 16, true, false, false, false, null, null), // float
	_f32(".f32", null, 32, true, false, false, false, null, null), // float
	_f64(".f64", null, 64, true, false, false, false, null, null), // float
	_f(".f", null, 32, true, false, false, false, null, null), // float

	_64(".64", null, 64, false, false, false, true, null, null),
	_i64(".i64", null, 64, false, false, false, true, null, null),
	_s64(".s64", true, 64, false, false, false, true, null, null),
	_u64(".u64", false, 64, false, false, false, true, null, null),

	_s32_f32(".s32.f32", null, 32, false, true, false, false, EnumDataType._s32, EnumDataType._f32),
	_u32_f32(".u32.f32", null, 32, false, true, false, false, EnumDataType._u32, EnumDataType._f32),
	_f32_s32(".f32.s32", null, 32, false, true, false, false, EnumDataType._f32, EnumDataType._s32),
	_f32_u32(".f32.u32", null, 32, false, true, false, false, EnumDataType._f32, EnumDataType._u32),
	_f32_f16(".f32.f16", null, null, false, true, false, false, EnumDataType._f32, EnumDataType._f16),
	_f16_f32(".f16.f32", null, null, false, true, false, false, EnumDataType._f16, EnumDataType._f32),

	_p8(".p8", null, 8, false, false, true, false, null, null),
	_p16(".p16", null, 16, false, false, true, false, null, null);

	private final Boolean signed;
	private final Integer sizeInBits;
	private final boolean floatType;
	private final boolean converter;
	private final EnumDataType from;
	private final EnumDataType to;
	private final String assemblyName;
	private final boolean polynomial;
	private final boolean integer;

	private EnumDataType(
			String assemblyName,
			Boolean signed, Integer bitSize,
			boolean floatType,
			boolean converter,
			boolean polynomial,
			boolean integer,
			EnumDataType from,
			EnumDataType to) {
		this.assemblyName = assemblyName;
		this.signed = signed;
		this.sizeInBits = bitSize;
		this.floatType = floatType;
		this.converter = converter;
		this.polynomial = polynomial;
		this.integer = integer;
		this.from = from;
		this.to = to;
	}

	public int getSizeBitmask() {
		switch (this.getSizeInBits()) {
		case 8:
			return 0x000000FF;
		case 16:
			return 0x0000FFFF;
		case 32:
			return 0xFFFFFFFF;
		default:
			assert false;
		}
		return -1;
	}

	public static EnumDataType getByAssemblyName(String name) {
		for (EnumDataType current : EnumDataType.values())
			if (current != EnumDataType.None && current.getAssemblyName().equals(name))
				return current;
		return null;
	}

	public static final EnumDataType[] allSizeType = { _8, _16, _32, _64 };
	public static final EnumDataType[] allUnsigned = { _u8, _u16, _u32, _u64 };
	public static final EnumDataType[] allSigned = { _s8, _s16, _s32, _s64 };
	public static final EnumDataType[] allSignumDontCare = { _i8, _i16, _i32, _i64, _8, _16, _32, _64 };
	public static final EnumDataType[] allUnsignedSingle = { _u8, _u16, _u32 };
	public static final EnumDataType[] allSignedSingle = { _s8, _s16, _s32 };
	public static final EnumDataType[] allSignumDontCareSingle = { _i8, _i16, _i32, _8, _16, _32 };
	public static final EnumDataType[] allInteger = { _8, _i8, _s8, _u8, _16, _i16, _s16, _u16, _32, _i32, _s32, _u32, _64, _i64, _s64, _u64 };
	public static final EnumDataType[] allIntegerSingle = { _8, _i8, _s8, _u8, _16, _i16, _s16, _u16, _32, _i32, _s32, _u32 };
	public static final EnumDataType[] allSingleWide = { _8, _i8, _s8, _u8, _16, _i16, _s16, _u16, _32, _i32, _s32, _u32, _f, _f16, _f32 };
	public static final EnumDataType[] allType = { _8, _i8, _s8, _u8, _16, _i16, _s16, _u16, _32, _i32, _s32, _u32, _64, _i64, _s64, _u64, _f16, _f32 };
	public static final EnumDataType[] all8 = { _8, _i8, _s8, _u8, _p8 };
	public static final EnumDataType[] all16 = { _16, _i16, _s16, _u16, _p16 };
	public static final EnumDataType[] all32 = { _32, _i32, _s32, _u32, _f32 };
	public static final EnumDataType[] all64 = { _64, _i64, _s64, _u64 };
	public static final EnumDataType[] allPolynomial = { _p8, _p16 };

	public Boolean getSigned() {
		return signed;
	}

	public Integer getSizeInBits() {
		return sizeInBits;
	}

	public boolean isFloatType() {
		return floatType;
	}

	public boolean isConverter() {
		return converter;
	}

	public EnumDataType getFrom() {
		return from;
	}

	public EnumDataType getTo() {
		return to;
	}

	public String getAssemblyName() {
		return assemblyName;
	}

	public boolean isPolynomial() {
		return polynomial;
	}

	public boolean isInteger() {
		return integer;
	}

}
