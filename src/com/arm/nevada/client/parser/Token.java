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

import java.util.Arrays;

import com.arm.nevada.client.interpreter.EnumDataType;
import com.arm.nevada.client.shared.ARMRegister;
import com.arm.nevada.client.shared.Out;
import com.arm.nevada.client.utils.DataTypeTools;

/**
 * Abstract class for tokens
 * 
 */
public class Token {
	MSG parse(String instruction, int pos, Arguments a) {
		assert false;
		return null;
	}

	/**
	 * This contains some information about the result of the tokens paring. 
	 */
	public static class MSG {
		private int position;
		private String message;
		private boolean error;

		public MSG(int pos, String message) {
			this.error = false;
			this.position = pos;
			this.message = message;
		}

		public MSG(int pos) {
			this(pos, null);
		}

		/**
		 * 
		 * @param pos The cursor's position after parsing the token.
		 * @return A new Message.
		 */
		public static MSG ok(int pos) {
			return new MSG(pos, null);
		}

		/**
		 * Gives back a new Message which contains information the failed token.
		 * @param pos Should be negative value. It's the successfully parsed ( -1 *position ) - 1  
		 * @param msg User readable information about the fail.
		 * @return   
		 */
		public static MSG error(int pos, String msg) {
			MSG result = new MSG(pos, msg);
			result.error = true;
			return result;
		}

		public int getPosition() {
			return this.position;
		}
		
		public String getMessage() {
			return this.message;
		}
		
		public int getBestParsedPos() {
			return -position - 1;
		}

		public boolean isError() {
			return error;
		}
	}
}

/**
 * Accepts any data type which is defined in EnumDataType. E.g.: ".u32"
 */
abstract class DataTypeToken extends Token {
	MSG parse(String instruction, int pos, Arguments a) {
		assert pos >= 0;
		int end = pos;
		while (end < instruction.length()) {
			char c = instruction.charAt(end);
			if (c == '.' || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9'))
				end++;
			else
				break;
		}
		a.setType(EnumDataType.getByAssemblyName(instruction
				.substring(pos, end)));
		if (a.getType() == null)
			return new MSG(-pos - 1, "Invalid data type");
		return MSG.ok(end);
	}
}

/**
 * Accepts all data type which is in EnumDataType.allType. In fact it should include all of the data types.
 */
class All extends DataTypeToken {
	static Token p() {
		return staticThis;
	}

	private static final All staticThis = new All();

	MSG parse(String instruction, int pos, Arguments a) {
		assert pos >= 0;
		int end = super.parse(instruction, pos, a).getPosition();
		if (end < 0)
			return MSG.error(-pos - 1, "Invalid data type");
		EnumDataType type = a.getType();
		if (Arrays.asList(EnumDataType.allType).contains(type)) {
			return MSG.ok(end);
		}
		return MSG.error(-pos - 1, "This type is incorrect here");
	}
}

/**
 * Accepts all unsigned data type. Like: .u8, u16 and so on.
 */
class AllU extends OneType {

	private static final AllU staticThis = new AllU();

	public AllU() {
		super(EnumDataType.allUnsigned);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts all signed data type. Like: .s8, s16 and so on.
 */
class AllS extends OneType {

	private static final AllS staticThis = new AllS();

	public AllS() {
		super(EnumDataType.allSigned);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts all signed data type which are maximum 32 bit wide: .s8, .s16, .s32
 */
class AllSignedSingle extends OneType {
	private static final AllSignedSingle staticThis = new AllSignedSingle();

	public AllSignedSingle() {
		super(EnumDataType.allSignedSingle);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts all unsigned, maximum 32 bit wide data types: .u8, .u16, .u32
 */
class AllUnsignedSingle extends OneType {

	private static final AllUnsignedSingle staticThis = new AllUnsignedSingle();

	public AllUnsignedSingle() {
		super(EnumDataType.allUnsignedSingle);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts all data type which maximum 32 bit wide.
 */
class AllISingle extends OneType {
	private static final AllISingle staticThis = new AllISingle();

	public AllISingle() {
		super(EnumDataType.allIntegerSingle);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * If there is data type, then accepts any of them, if can't parse data type, then it doesn't change anything and
 * accepts it.
 */
class OptType extends OneType {
	private static final OptType staticThis = new OptType();

	public OptType() {
		super(EnumDataType.allType);
	}

	@Override
	MSG parse(String instruction, int pos, Arguments a) {
		MSG result = super.parse(instruction, pos, a);
		if (!result.isError()) {
			return result;
		} else {
			return new MSG(pos);
		}
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts any integer type. So all: .i##, .u##, .s##
 */
class AllI extends OneType {
	private static final AllI staticThis = new AllI();

	public AllI() {
		super(EnumDataType.allInteger);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts all data type. The reason for the existence of this class is just for the logical meaning. The point is the
 * size.
 */
class AllSize extends OneType {
	private static final AllSize staticThis = new AllSize();

	public AllSize() {
		super(EnumDataType.allType);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts any type with maximum size of 32.
 */
class AllSingleWide extends OneType {
	private static final AllSingleWide staticThis = new AllSingleWide();

	public AllSingleWide() {
		super(EnumDataType.allSingleWide);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts any 8 bit wide data type.
 */
class All8 extends OneType {
	private static final All8 staticThis = new All8();

	public All8() {
		super(EnumDataType.all8);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts any 16 bit wide data type.
 */
class All16 extends OneType {
	private static final All16 staticThis = new All16();

	public All16() {
		super(EnumDataType.all16);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts any 32 bit wide data type.
 */
class All32 extends OneType {
	private static final All32 staticThis = new All32();

	public All32() {
		super(EnumDataType.all32);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts any 64 bit wide data type.
 */
class All64 extends OneType {
	private static final All64 staticThis = new All64();

	public All64() {
		super(EnumDataType.all64);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts any polynomial data type.
 */
class AllP extends OneType {
	private static final AllP staticThis = new AllP();

	public AllP() {
		super(EnumDataType.allPolynomial);
	}

	static Token p() {
		return staticThis;
	}
}

abstract class OneType extends DataTypeToken {
	private EnumDataType[] dataType;

	public OneType(EnumDataType... dataType) {
		this.dataType = dataType;
	}

	MSG parse(String instruction, int pos, Arguments a) {
		int end = super.parse(instruction, pos, a).getPosition();
		if (end < 0)
			return MSG.error(-pos - 1, "Invalid data type");
		EnumDataType type = a.getType();
		for (int i = 0; i < dataType.length; i++) {
			if (type == dataType[i]) {
				a.setType(type);
				return MSG.ok(end);
			}
		}
		return MSG.error(-pos - 1, "This type is incorrect here");
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class U8 extends OneType {

	private static final U8 staticThis = new U8();

	public U8() {
		super(EnumDataType._u8);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class U16 extends OneType {

	private static final U16 staticThis = new U16();

	public U16() {
		super(EnumDataType._u16);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class U32 extends OneType {

	private static final U32 staticThis = new U32();

	public U32() {
		super(EnumDataType._u32);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts .u64
 */
class U64 extends OneType {

	private static final U64 staticThis = new U64();

	public U64() {
		super(EnumDataType._u64);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class I8 extends OneType {

	private static final I8 staticThis = new I8();

	public I8() {
		super(EnumDataType._i8, EnumDataType._s8, EnumDataType._u8);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class I16 extends OneType {

	private static final I16 staticThis = new I16();

	public I16() {
		super(EnumDataType._i16, EnumDataType._s16, EnumDataType._u16);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class I32 extends OneType {

	private static final I32 staticThis = new I32();

	public I32() {
		super(EnumDataType._i32, EnumDataType._s32, EnumDataType._u32);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class I64 extends OneType {

	private static final I64 staticThis = new I64();

	public I64() {
		super(EnumDataType._i64, EnumDataType._s64, EnumDataType._u64);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class S8 extends OneType {

	private static final S8 staticThis = new S8();

	public S8() {
		super(EnumDataType._s8);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class S16 extends OneType {

	private static final S16 staticThis = new S16();

	public S16() {
		super(EnumDataType._s16);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class S32 extends OneType {

	private static final S32 staticThis = new S32();

	public S32() {
		super(EnumDataType._s32);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class S64 extends OneType {

	private static final S64 staticThis = new S64();

	public S64() {
		super(EnumDataType._s64);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts F32.
 */
class F extends F32 {

}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class _8 extends OneType {

	private static final _8 staticThis = new _8();

	public _8() {
		super(EnumDataType.all8);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class _16 extends OneType {

	private static final _16 staticThis = new _16();

	public _16() {
		super(EnumDataType.all16);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class _32 extends OneType {

	private static final _32 staticThis = new _32();

	public _32() {
		super(EnumDataType.all32);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class _64 extends OneType {

	private static final _64 staticThis = new _64();

	public _64() {
		super(EnumDataType.all64);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class F32 extends OneType {

	private static final F32 staticThis = new F32();

	public F32() {
		super(EnumDataType._f32);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class F64 extends OneType {

	private static final F64 staticThis = new F64();

	public F64() {
		super(EnumDataType._f64);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class F16 extends OneType {

	private static final F16 staticThis = new F16();

	public F16() {
		super(EnumDataType._f16);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class P8 extends OneType {

	private static final P8 staticThis = new P8();

	public P8() {
		super(EnumDataType._p8);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class P16 extends OneType {

	private static final P16 staticThis = new P16();

	public P16() {
		super(EnumDataType._p16);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class S32F32 extends OneType {

	private static final S32F32 staticThis = new S32F32();

	public S32F32() {
		super(EnumDataType._s32_f32);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class U32F32 extends OneType {

	private static final U32F32 staticThis = new U32F32();

	public U32F32() {
		super(EnumDataType._u32_f32);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class F32S32 extends OneType {

	private static final F32S32 staticThis = new F32S32();

	public F32S32() {
		super(EnumDataType._f32_s32);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class F32U32 extends OneType {

	private static final F32U32 staticThis = new F32U32();

	public F32U32() {
		super(EnumDataType._f32_u32);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class F32F16 extends OneType {

	private static final F32F16 staticThis = new F32F16();

	public F32F16() {
		super(EnumDataType._f32_f16);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts the named data type and the stricter types if exists.
 */
class F16F32 extends OneType {

	private static final F16F32 staticThis = new F16F32();

	public F16F32() {
		super(EnumDataType._f16_f32);
	}

	static Token p() {
		return staticThis;
	}
}

/**
 * Accepts at least one space character.
 */
class Space extends Token {
	private static final Space staticThis = new Space();

	static Token p() {
		return staticThis;
	}

	MSG parse(String instruction, int pos, Arguments a) {
		if (instruction.length() <= pos)
			return MSG.error(-pos - 1, "Space must be here");
		if (instruction.charAt(pos) != ' ')
			return MSG.error(-pos - 1, "Space must be here");
		pos++;
		while (instruction.length() > pos && instruction.charAt(pos) == ' ')
			pos++;
		return MSG.ok(pos);
	}
}

/**
 * Accepts a comma optionally surrounded by any number of space characters.
 */
class Comma extends Token {
	private static final Comma staticThis = new Comma();

	static Token p() {
		return staticThis;
	}

	MSG parse(String instruction, int pos, Arguments a) {
		while (instruction.length() > pos && instruction.charAt(pos) == ' ')
			pos++;
		if (instruction.length() <= pos)
			return MSG.error(-pos - 1, "Missing comma");
		if (instruction.charAt(pos) != ',')
			return MSG.error(-pos - 1, "Must be comma here");
		pos++;
		while (instruction.length() > pos && instruction.charAt(pos) == ' ')
			pos++;
		return MSG.ok(pos);
	}
}

class RegisterToken extends Token {

	protected RegisterToken() {
	}

	protected MSG parse(String instruction, int pos, Arguments a, char letter,
			int maxRegisterIndex) {
		if (instruction.length() <= pos)
			return MSG.error(-pos - 1, "No characters left");
		if (instruction.charAt(pos) != letter)
			return MSG.error(-pos - 1, "Not the proper register type");
		pos++;
		if (instruction.length() <= pos)
			return MSG.error(-pos - 1, "Index needed");
		if (instruction.charAt(pos) < '0' || instruction.charAt(pos) > '9')
			return MSG.error(-pos - 1, "Invalid characters in index");
		int register = instruction.charAt(pos) - '0';
		pos++;
		if (register != 0 && instruction.length() > pos
				&& instruction.charAt(pos) >= '0'
				&& instruction.charAt(pos) <= '9') {
			register = register * 10 + instruction.charAt(pos) - '0';
			if (register > maxRegisterIndex)
				return MSG.error(-pos - 1, "Register index out of interval");
			pos++;
		}
		a.getRegisterIndexes().add(register);
		return MSG.ok(pos);
	}
}

/**
 * Accepts one ARM register. (R0..R15). SP, LR and PC are also accepted
 */
class R extends RegisterToken {
	private static final R staticThis = new R();

	static Token p() {
		return staticThis;
	}

	protected R() {
		super();
	}

	@Override
	MSG parse(String instruction, int pos, Arguments a) {
		MSG superResult = super.parse(instruction, pos, a, 'r', 15);
		if (!superResult.isError()) {
			return superResult;
		} else {
			MSG alliasResult;
			if (instruction.length() <= pos)
				alliasResult = MSG.error(-pos - 1, "No characters left");
			if (instruction.length() < pos + 2) {
				alliasResult = MSG.error(-pos - 1, "Arm core register needed");
			} else {
				String allias = instruction.substring(pos, pos + 2);
				if (allias.equalsIgnoreCase("sp")) {
					a.getRegisterIndexes().add(ARMRegister.R13.getIndex());
					alliasResult = MSG.ok(pos + 2);
					pos += 2;
				}
				else if (allias.equalsIgnoreCase("lr")) {
					a.getRegisterIndexes().add(ARMRegister.R14.getIndex());
					alliasResult = MSG.ok(pos + 2);
					pos += 2;
				}
				else if (allias.equalsIgnoreCase("pc")) {
					a.getRegisterIndexes().add(ARMRegister.R15.getIndex());
					alliasResult = MSG.ok(pos + 2);
					pos += 2;
				}
				else
					alliasResult = MSG.error(-pos - 1, "Arm core register needed");
			}
			return alliasResult;
		}
	}
}

/**
 * Accepts one ARM register, but PC (R15) and SP (R13) are disallowed.
 */
class RNotPCNotSP extends R {
	private static final RNotPCNotSP staticThis = new RNotPCNotSP();

	static Token p() {
		return staticThis;
	}

	@Override
	public MSG parse(String instruction, int pos, Arguments a) {
		MSG superParserResult = super.parse(instruction, pos, a);
		if (superParserResult.isError()) {
			return superParserResult;
		} else {
			int index = a.getRegisterIndexes().get(a.getRegisterIndexes().size() - 1);
			if (index == ARMRegister.R15.getIndex() || index == ARMRegister.R13.getIndex()) {
				return MSG.error(-pos - 1, "PC (R15) and SP (R13) are permitted register here.");
			} else {
				return superParserResult;
			}
		}
	}
}

/**
 * Accepts one ARM register, but PC (R15) and SP (R13) are disallowed.
 */
class ROffset extends R {
	private static final ROffset staticThis = new ROffset();

	static Token p() {
		return staticThis;
	}

	private ROffset() {
		super();
	}

	MSG parse(String instruction, int pos, Arguments a) {
		int savePos = pos;
		pos = super.parse(instruction, pos, a).getPosition();
		if (pos < 0)
			return MSG.error(-savePos - 1, "Must be a valid core register");
		int index = a.getRegisterIndexes().get(a.getRegisterIndexes().size() - 1);
		if (index == ARMRegister.R13.getIndex() || index == ARMRegister.R15.getIndex())
			return MSG.error(-pos - 1, "R15 and R13 are not available here");
		return MSG.ok(pos);
	}
}

/**
 * Accepts one NEON D (Double) register. D0..D31
 */
class D extends RegisterToken {
	private static final D staticThis = new D();

	static Token p() {
		return staticThis;
	}

	protected D() {
		super();
	}

	MSG parse(String instruction, int pos, Arguments a) {
		return super.parse(instruction, pos, a, 'd', 31);
	}
}

/**
 * Accepts one NEON Q (QUAD) register. Q0..Q16
 */
class Q extends RegisterToken {
	private static final Q staticThis = new Q();

	static Token p() {
		return staticThis;
	}

	protected Q() {
		super();
	}

	MSG parse(String instruction, int pos, Arguments a) {
		return super.parse(instruction, pos, a, 'q', 15);
	}
}

/**
 * Accepts one or two NEON Q (QUAD) register. In both cases two indexes are saved.
 */
class Q2in1 extends Q {
	private static final Q2in1 staticThis = new Q2in1();

	static Token p() {
		return staticThis;
	}

	private Q2in1() {
		super();
	}

	MSG parse(String instruction, int pos, Arguments a) {
		MSG msg = super.parse(instruction, pos, a);
		if (!msg.isError()) {
			int parsed = a.getRegisterIndexes().get(a.getRegisterIndexes().size() - 1);
			a.getRegisterIndexes().add(parsed);
		}
		return msg;
	}
}

/**
 * Accepts one or two NEON D (Double) register. In both cases two indexes are saved.
 */
class D2in1 extends D {
	private static final D2in1 staticThis = new D2in1();

	static Token p() {
		return staticThis;
	}

	private D2in1() {
		super();
	}

	MSG parse(String instruction, int pos, Arguments a) {
		MSG msg = super.parse(instruction, pos, a);
		if (!msg.isError()) {
			int parsed = a.getRegisterIndexes().get(a.getRegisterIndexes().size() - 1);
			a.getRegisterIndexes().add(parsed);
		}
		return msg;
	}
}

/**
 * For internal use only.
 */
class GeneralSubregister extends Token {
	MSG parse(String instruction, int pos, Arguments a, EnumRegisterType regType, boolean hasSubIndex) {
		int savePos = pos;
		// Parse the register
		if (regType == EnumRegisterType.DOUBLE) {
			pos = D.p().parse(instruction, pos, a).getPosition();
			if (pos < 0)
				return MSG.error(-savePos - 1, "Incorrect D register");
		} else if (regType == EnumRegisterType.QUAD) {
			pos = Q.p().parse(instruction, pos, a).getPosition();
			if (pos < 0)
				return MSG.error(-savePos - 1, "Incorrect Q register");
		} else {
			throw new IllegalArgumentException("Register type must be DOUBLE or QUAD");
		}
		pos = WhiteSpace.p().parse(instruction, pos, a).getPosition();
		// Parse the index
		if (instruction.length() <= pos || instruction.charAt(pos) != '[')
			return MSG.error(-pos - 1, "Must be [ here");
		pos++;
		pos = WhiteSpace.p().parse(instruction, pos, a).getPosition();
		if (hasSubIndex) {
			Out<Integer> parsedIndex = new Out<Integer>();
			savePos = pos;
			pos = Utils.parseInteger(instruction, pos, parsedIndex);
			if (pos < 0)
				return MSG.error(-savePos - 1, "Not a valid index");
			pos = WhiteSpace.p().parse(instruction, pos, a).getPosition();
			// if (pos < 0)
			// return -savePos-1;
			int index = parsedIndex.getValue();
			if (index < 0 || index >= regType.getSize() / a.getType().getSizeInBits())
				return MSG.error(-savePos - 1, "Index out of bounds");
			a.setSubRegisterIndex(index);
			if (!check(a))
				return MSG.error(-savePos - 1, "Invalid combination of indexes");

		}
		if (instruction.length() <= pos || instruction.charAt(pos) != ']')
			return MSG.error(-pos - 1, "Must be ] here");
		pos++;

		return MSG.ok(pos);
	}

	boolean check(Arguments a) {
		return true;
	}
}

/**
 * Accepts e.g. "D0[1]". The maximum value in the braces are depends from the data type.
 */
class DSubReg extends GeneralSubregister {
	static Token p() {
		return staticThis;
	}

	private static final DSubReg staticThis = new DSubReg();

	MSG parse(String instruction, int pos, Arguments a) {
		return super.parse(instruction, pos, a, EnumRegisterType.DOUBLE, true);
	}
}

/**
 * D#[x]: D# is restricted to D0-D7 if size is 16, or D0-D15 otherwise.
 */
class DsubRegForScalar extends DSubReg {
	@Override
	boolean check(Arguments a) {
		boolean result = super.check(a);
		int regIndex = a.getRegisterIndexes().get(a.getRegisterIndexes().size());
		int size = a.getType().getSizeInBits();

		boolean d0D7Size16 = regIndex >= 0 && regIndex < 8 && size == 16;
		boolean d0D15Size32 = regIndex >= 0 && regIndex < 16 && size == 32;

		return result && (d0D7Size16 || d0D15Size32);
	}
}

/**
 * Accepts list similar to this: "{<D#[x]>, <D#+1[x]>, <D#+2[x]>}" but it has many types and rules.
 */
class DSubRegSetHasIndex extends GeneralSubregister {
	static DSubRegSetHasIndex p() {
		return staticThis;
	}

	private static final DSubRegSetHasIndex staticThis = new DSubRegSetHasIndex();

	public MSG parse(String instruction, int pos, Arguments a, boolean hasSubindex) {
		return super.parse(instruction, pos, a, EnumRegisterType.DOUBLE, hasSubindex);
	}
}

/**
 * Q#[#]
 */
class QSubReg extends GeneralSubregister {
	static Token p() {
		return staticThis;
	}

	private static final QSubReg staticThis = new QSubReg();

	MSG parse(String instruction, int pos, Arguments a) {
		return super.parse(instruction, pos, a, EnumRegisterType.QUAD, true);
	}
}

/**
 * Accepts optional whitespace. Whitespace means the space character!
 */
class WhiteSpace extends Token {
	static WhiteSpace p() {
		return staticThis;
	}

	private static final WhiteSpace staticThis = new WhiteSpace();

	MSG parse(String instruction, int pos, Arguments a) {
		while (instruction.length() > pos) {

			if (instruction.charAt(pos) == ' ') // TODO maybe should add other whitespace chars
				pos++;
			else
				break;
		}

		return MSG.ok(pos);
	}
}

/**
 * Accepts immediate value: #0x1234AB or #9343.
 */
class ImmAnyLong extends Token {
	static ImmAnyLong p() {
		return staticThis;
	}

	private static final ImmAnyLong staticThis = new ImmAnyLong();

	MSG parse(String instruction, int pos, Arguments a) {
		if (instruction.length() <= pos)
			return MSG.error(-pos - 1, "No characters left, immediate value needed");
		if (instruction.charAt(pos) != '#')
			return MSG.error(-pos - 1, "Must be a # here");
		pos++;
		pos = WhiteSpace.p().parse(instruction, pos, a).getPosition();

		Out<Long> parsedImmediate = new Out<Long>();
		int savePos = pos;
		pos = Utils.parseLong(instruction, pos, parsedImmediate);
		if (pos < 0)
			return MSG.error(-savePos - 1, "Can't parse number");
		a.setImmediateValue(parsedImmediate.getValue());
		if (!check(a)) {
			return MSG.error(-savePos - 1, "This immediate value is invalid at here");
		}
		return MSG.ok(pos);
	}

	boolean check(Arguments a) {
		return true;
	}
}

/**
 * Accepts immediate value in range: [0; (size of data type - 1)]
 */
class Imm0toSizeMinus1 extends ImmAnyLong {
	static Imm0toSizeMinus1 p() {
		return staticThis;
	}

	private static final Imm0toSizeMinus1 staticThis = new Imm0toSizeMinus1();

	@Override
	boolean check(Arguments a) {
		if (a.getImmediateValue() >= 0 && a.getImmediateValue() < a.getType().getSizeInBits())
			return true;
		else
			return false;
	}
}

/**
 * Accepts immediate value in range: [1; (size of data type)]
 */
class Imm1toSize extends ImmAnyLong {
	static Imm1toSize p() {
		return staticThis;
	}

	private static final Imm1toSize staticThis = new Imm1toSize();

	@Override
	boolean check(Arguments a) {
		if (a.getImmediateValue() > 0 && a.getImmediateValue() <= a.getType().getSizeInBits())
			return true;
		else
			return false;
	}
}

/**
 * Accepts immediate value which is the size of the data type.
 */
class ImmIsSize extends ImmAnyLong {
	static ImmIsSize p() {
		return staticThis;
	}

	private static final ImmIsSize staticThis = new ImmIsSize();

	@Override
	boolean check(Arguments a) {
		if (a.getImmediateValue() == a.getType().getSizeInBits())
			return true;
		else
			return false;
	}
}

/**
 * Accepts immediate value in range: [1; (size of data type) / 2]
 */
class Imm1toHalfSize extends ImmAnyLong {
	static Imm1toHalfSize p() {
		return staticThis;
	}

	private static final Imm1toHalfSize staticThis = new Imm1toHalfSize();

	@Override
	boolean check(Arguments a) {
		if (a.getImmediateValue() >= 1 && a.getImmediateValue() <= a.getType().getSizeInBits() / 2)
			return true;
		else
			return false;
	}
}

/**
 * Accepts immediate value which is zero.
 */
class ImmIsZero extends ImmAnyLong {
	static ImmIsZero p() {
		return staticThis;
	}

	private static final ImmIsZero staticThis = new ImmIsZero();

	@Override
	boolean check(Arguments a) {
		return a.getImmediateValue() == 0;
	}
}

/**
 * Accepts immediate values where: ((immediate value) * (size of data type)) / 8 &lt;= 7
 */
class ImmTimesDtDividedBy8IsMax7 extends ImmAnyLong {
	static ImmTimesDtDividedBy8IsMax7 p() {
		return staticThis;
	}

	private static final ImmTimesDtDividedBy8IsMax7 staticThis = new ImmTimesDtDividedBy8IsMax7();

	@Override
	boolean check(Arguments a) {
		return a.getImmediateValue() >= 0 && a.getImmediateValue() * (a.getType().getSizeInBits() / 8) < 8;
	}
}

/**
 * Accepts immediate values where: ((immediate value) * (size of data type)) / 8 &lt;= 15
 */
class ImmTimesDtDividedBy8IsMax15 extends ImmAnyLong {
	static ImmTimesDtDividedBy8IsMax15 p() {
		return staticThis;
	}

	private static final ImmTimesDtDividedBy8IsMax15 staticThis = new ImmTimesDtDividedBy8IsMax15();

	@Override
	boolean check(Arguments a) {
		return a.getImmediateValue() >= 0 && a.getImmediateValue() * (a.getType().getSizeInBits() / 8) < 16;
	}
}

/**
 * For inner use only.
 */
class ImmGeneralInstruction extends Token {
	EnumInstruction immType = null;

	MSG parse(String instruction, int pos, Arguments a) {
		if (instruction.length() <= pos)
			return MSG.error(-pos - 1, "No characters left, immediate value needed");
		if (instruction.charAt(pos) != '#')
			return MSG.error(-pos - 1, "Must be a # here");
		pos++;
		pos = WhiteSpace.p().parse(instruction, pos, a).getPosition();

		Out<Long> parsedImmediate = new Out<Long>();
		int savePos = pos;
		pos = Utils.parseAndExtendByBitSize(instruction, pos, a.getType(), parsedImmediate);
		if (pos < 0)
			return MSG.error(-savePos - 1, "Can't parse number");
		a.setImmediateValue(parsedImmediate.getValue());
		if (!DataTypeTools.isAdvSIMDExpandImm(parsedImmediate.getValue(), immType)) {
			return MSG.error(-savePos - 1, "This immediate is invalid here");
		}
		return MSG.ok(pos);
	}
}

/**
 * Accepts immediate values which is acceptable by the vmov instruction.
 */
class ImmVmov extends ImmGeneralInstruction {
	static ImmVmov p() {
		return staticThis;
	}

	private static final ImmVmov staticThis = new ImmVmov();

	ImmVmov() {
		immType = EnumInstruction.vmov;
	}
}

/**
 * Accepts immediate values which is acceptable by the vmvn instruction.
 */
class ImmVmvn extends ImmGeneralInstruction {
	static ImmVmvn p() {
		return staticThis;
	}

	private static final ImmVmvn staticThis = new ImmVmvn();

	ImmVmvn() {
		immType = EnumInstruction.vmvn;
	}
}

/**
 * Accepts immediate values which is acceptable by the vorr instruction.
 */
class ImmVorr extends ImmGeneralInstruction {
	static ImmVorr p() {
		return staticThis;
	}

	private static final ImmVorr staticThis = new ImmVorr();

	ImmVorr() {
		immType = EnumInstruction.vorr;
	}
}

/**
 * Accepts immediate values which is acceptable by the vbic instruction.
 */
class ImmVbic extends ImmGeneralInstruction {
	static ImmVbic p() {
		return staticThis;
	}

	private static final ImmVbic staticThis = new ImmVbic();

	ImmVbic() {
		immType = EnumInstruction.vbic;
	}
}

/**
 * This token isn't parses anything, it just sets the data type.
 */
class DefType extends Token {

	private final EnumDataType defaultType;

	public DefType(EnumDataType type) {
		this.defaultType = type;
	}

	/**
	 * Warning: use with care! It returns with a brand new Token, so consumes memory!
	 */
	static DefType p(EnumDataType type) {
		return new DefType(type);
	}

	MSG parse(String instruction, int pos, Arguments a) {
		a.setType(defaultType);
		return MSG.ok(pos);
	}
}

/**
 * Accepts list similar to this: "{<D#[x]>, <D#+1[x]>, <D#+2[x]>}" but it has many types and rules.
 */
class ListSubIndex extends Token {

	private final int desiredItemCount;
	private final int spacing;
	private final boolean hasBraces;
	private final boolean hasSubindex;

	/**
	 * Accepts list similar to this: "{<D#[x]>, <D#+1[x]>, <D#+2[x]>}" but it has many types and rules.
	 * 
	 * @param desiredItemCount
	 *            Desired count of list elements.
	 * @param spacing
	 *            The desired difference between the NEON register indexes. Should be 1 or 2.
	 * @param hasBraces
	 *            True if has "[]"
	 * @param hasSubindex
	 *            Can be true only if hasBraces is also true. If true then the [] has an index inside, so e.g. [3]
	 */
	public ListSubIndex(int desiredItemCount, int spacing, boolean hasBraces, boolean hasSubindex) {
		assert !(!hasBraces && hasSubindex);
		this.desiredItemCount = desiredItemCount;
		this.spacing = spacing;
		this.hasBraces = hasBraces;
		this.hasSubindex = hasSubindex;
	}

	static ListSubIndex p(int desiredItemCount, int spacing, boolean hasBraces, boolean hasSubindex) {
		return new ListSubIndex(desiredItemCount, spacing, hasBraces, hasSubindex);
	}

	MSG parse(String instruction, int pos, Arguments a) {
		Arguments emptyA = new Arguments();
		emptyA.setType(a.getType());
		assert desiredItemCount > 0;
		assert spacing == 1 || spacing == 2;
		assert !(!hasBraces && hasSubindex);
		// decide the list is defined by interval or enumeration
		boolean interval = false;
		int closingPos = instruction.substring(pos, instruction.length()).indexOf('}');
		if (closingPos < 0)
			return MSG.error(-pos - 1, "Lists must be closed with }");
		String list = instruction.substring(pos, pos + closingPos + 1);
		if (list.indexOf('-') != -1)
			interval = true;

		// common part
		int savePos = pos;
		pos = Utils.parseChar(instruction, pos, '{');
		if (pos < 0)
			return MSG.error(-savePos - 1, "Lists must start with {");
		pos = WhiteSpace.p().parse(instruction, pos, emptyA).getPosition();
		Arguments tempArgs = new Arguments();
		tempArgs.setType(emptyA.getType());

		// interval dependent part
		if (interval) {
			savePos = pos;
			if (hasBraces) {
				pos = DSubRegSetHasIndex.p().parse(instruction, pos, emptyA, hasSubindex).getPosition();
				if (pos < 0)
					return MSG.error(-savePos - 1, "Invalid start of the interval, invalid D sub");
			}
			else {
				pos = D.p().parse(instruction, pos, emptyA).getPosition();
				if (pos < 0)
					return MSG.error(-savePos - 1, "Invalid start of the interval, invalid D");
			}

			pos = WhiteSpace.p().parse(instruction, pos, tempArgs).getPosition();
			savePos = pos;
			pos = Utils.parseChar(instruction, pos, '-');
			if (pos < 0)
				return MSG.error(-savePos - 1, "Must be a - here");
			pos = WhiteSpace.p().parse(instruction, pos, tempArgs).getPosition();
			savePos = pos;
			if (hasBraces)
				pos = DSubRegSetHasIndex.p().parse(instruction, pos, tempArgs, hasSubindex).getPosition();
			else
				pos = D.p().parse(instruction, pos, tempArgs).getPosition();
			if (pos < 0)
				return MSG.error(-savePos - 1, "Invalid list ending D");
			if (hasSubindex)
				if (emptyA.getSubRegisterIndex() != tempArgs.getSubRegisterIndex())
					return MSG.error(-savePos - 1, "Sub indexes must be the same");
			if (tempArgs.getRegisterIndexes().get(0) - emptyA.getRegisterIndexes().get(0) != spacing * (desiredItemCount - 1))
				return MSG.error(-pos - 1, "Register count and/or spacing not correct");
		} else {
			int currentItemCount = 0;
			while (true) {
				savePos = pos;
				if (hasBraces)
					pos = DSubRegSetHasIndex.p().parse(instruction, pos, tempArgs, hasSubindex).getPosition();
				else
					pos = D.p().parse(instruction, pos, tempArgs).getPosition();
				if (pos < 0)
					return MSG.error(-savePos - 1, "Invalid D (maybe should forward the previous error");
				if (currentItemCount == 0) {
					emptyA.getRegisterIndexes().add(tempArgs.getRegisterIndexes().get(0));
					if (hasSubindex)
						emptyA.setSubRegisterIndex(tempArgs.getSubRegisterIndex());
				} else {
					if (emptyA.getRegisterIndexes().get(0) + spacing * currentItemCount != tempArgs.getRegisterIndexes().get(currentItemCount))
						return MSG.error(-savePos - 1, "Maybe invalid list element count or spacing");
					if (hasSubindex && emptyA.getSubRegisterIndex() != tempArgs.getSubRegisterIndex())
						return MSG.error(-savePos - 1, "Sub register indexes must be the same");
				}
				currentItemCount++;

				int tempPos = Comma.p().parse(instruction, pos, tempArgs).getPosition();
				if (tempPos < 0)
					break;
				else
					pos = tempPos;
			}
			if (currentItemCount != desiredItemCount)
				return MSG.error(-savePos - 1, "Invalid list element count");
		}

		pos = WhiteSpace.p().parse(instruction, pos, emptyA).getPosition();
		savePos = pos;
		pos = Utils.parseChar(instruction, pos, '}');
		if (pos < 0)
			return MSG.error(-savePos - 1, "List must be closed with }");
		a.getRegisterIndexes().add(emptyA.getRegisterIndexes().get(emptyA.getRegisterIndexes().size() - 1));
		a.setSubRegisterIndex(emptyA.getSubRegisterIndex());
		return MSG.ok(pos);
	}
}

/**
 * E.g. "[R3]", where R3 is any ARM register.
 */
class BaseAddress extends Token {
	private final int[] alignments;

	public BaseAddress(int[] alignments) {
		this.alignments = alignments;
	}

	static BaseAddress p(int[] alignments) {
		return new BaseAddress(alignments);
	}

	MSG parse(String instruction, int pos, Arguments a) {
		pos = Utils.parseCharAndWhitespace(instruction, pos, '[');
		if (pos < 0)
			return MSG.error(-pos - 1, "Base address must be started with [");
		int savePos = pos;
		pos = R.p().parse(instruction, pos, a).getPosition();
		if (pos < 0)
			return MSG.error(-savePos - 1, "Invalid core register");
		int index = a.getRegisterIndexes().get(a.getRegisterIndexes().size() - 1);
		if (index == ARMRegister.R15.getIndex() || index == ARMRegister.R13.getIndex())
			return MSG.error(-savePos - 1, "R13 and R15 are disallowed");

		int afterColonPos = Utils.parseCharSurroundedByWhitespace(instruction, pos, ':');
		if (afterColonPos >= 0) {
			pos = afterColonPos;
			Out<Integer> alignment = new Out<Integer>();
			savePos = pos;
			pos = Utils.parseInteger(instruction, afterColonPos, alignment);
			if (pos < 0)
				return MSG.error(-savePos - 1, "Not a number in alignment");
			if (alignment.getValue() == 8) // Unaligned data access can't be defined by this way
				return MSG.error(-savePos - 1, "Alignment=8 must be omitted, it's the default");
			boolean contains = false;
			for (int i = 0; i < alignments.length; i++) {
				if (alignments[i] == (int) alignment.getValue()) {
					contains = true;
					break;
				}
			}
			if (!contains) {
				return MSG.error(-savePos - 1, "The specified alignment is not valid here");
			}
			a.setAlignmentByte(alignment.getValue() / 8);
		} else {
			boolean contains = false;
			for (int i = 0; i < alignments.length; i++) {
				if (alignments[i] == 8) {
					contains = true;
					break;
				}
			}
			// if unaligned data access is enabled
			if (contains) {
				a.setAlignmentByte(1);
				pos = WhiteSpace.p().parse(instruction, pos, a).getPosition();
			}
			else
				return MSG.error(-savePos - 1, "Unaligned data access is prohibited");
		}
		savePos = pos;
		pos = Utils.parseChar(instruction, pos, ']');
		if (pos < 0)
			return MSG.error(-savePos - 1, "Base address must be closed with ]");
		return MSG.ok(pos);
	}
}

/**
 * Accepts a exclamation mark (!).
 */
class WriteBack extends Token {
	static WriteBack p() {
		return staticThis;
	}

	private static final WriteBack staticThis = new WriteBack();

	@Override
	MSG parse(String instruction, int pos, Arguments a) {
		pos = WhiteSpace.p().parse(instruction, pos, a).getPosition();
		int savePos = pos;
		pos = Utils.parseChar(instruction, pos, '!');
		if (pos < 0)
			return MSG.error(-savePos - 1, "Invalid write back character");
		return MSG.ok(pos);
	}
}

/**
 * Accepts the "FPSCR" string.
 */
class FPSCR extends Token {
	static FPSCR p() {
		return staticThis;
	}

	private static final FPSCR staticThis = new FPSCR();

	@Override
	MSG parse(String instruction, int pos, Arguments a) {
		String find = "fpscr";
		assert pos >= 0;
		if (instruction.length() < (pos + find.length())
				|| !instruction.substring(pos, pos + find.length()).equals(find)) {
			return MSG.error(pos, "FPSC should be here.");
		} else {
			return MSG.ok(pos + find.length());

		}
	}
}

/**
 * When this token parses, then the position should be at the end of the line, so shoudn't be more characters.
 */
class End extends Token {
	static End p() {
		return staticThis;
	}

	private static final End staticThis = new End();

	MSG parse(String instruction, int pos, Arguments a) {
		if (pos == instruction.length())
			return MSG.ok(pos);
		else {
			return MSG.error(-pos - 1, "Shouldn't be more characters");
		}
	}
}