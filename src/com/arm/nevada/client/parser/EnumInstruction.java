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

public enum EnumInstruction {
	vand,
	vbic,
	veor,
	vbif,
	vbit,
	vbsl,
	vorr,
	vorn,

	vmov,
	vqmovn,
	vqmovun,
	vmovn,
	vmovl,
	vmvn,

	vdup,
	vld1,
	vld2,
	vld3,
	vld4,
	vst1,
	vst2,
	vst3,
	vst4,

	vadd,
	vaddhn,
	vaddl,
	vaddw,
	vhadd,
	vhsub,
	vpadal,
	vpadd,
	vpaddl,
	vraddhn,
	vrhadd,
	vrsubhn,
	vqadd,
	vqsub,
	vsub,
	vsubhn,
	vsubl,
	vsubw,

	vqrshl,
	vqrshrn,
	vqrshrun,
	vqshl,
	vqshlu,
	vqshrn,
	vqshrun,
	vrshl,
	vshl,
	vrshr,
	vshr,
	vrshrn,
	vshrn,
	vshll,
	vrsra,
	vsra,
	vsli,
	vsri,

	vacge,
	vacgt,
	vacle,
	vaclt,
	vceq,
	vcge,
	vcgt,
	vcle,
	vclt,
	vtst,

	vmla,
	vmlal,
	vmls,
	vmlsl,
	vmul,
	vmull,
	vqdmlal,
	vqdmlsl,
	vqdmulh,
	vqrdmulh,
	vqdmull,

	vaba,
	vabal,
	vabd,
	vabdl,
	vabs,
	vqabs,

	vcvt,

	vcls,
	vclz,
	vcnt,

	vzip,
	vuzp,

	vtrn,

	vtbl,
	vtbx,

	vswp,

	vneg,
	vqneg,

	vrev16,
	vrev32,
	vrev64,

	vmax,
	vmin,
	vpmax,
	vpmin,

	vrecpe,
	vrsqrte,

	vrecps,
	vrsqrts,

	vext,

	vmrs,
	vmsr;
}
