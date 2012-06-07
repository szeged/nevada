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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.arm.nevada.client.interpreter.AbsoluteAndNegateInstruction;
import com.arm.nevada.client.interpreter.ArithmeticInstructions;
import com.arm.nevada.client.interpreter.ComparisonInstruction;
import com.arm.nevada.client.interpreter.ConversationInstruction;
import com.arm.nevada.client.interpreter.CountInstruction;
import com.arm.nevada.client.interpreter.EnumDataType;
import com.arm.nevada.client.interpreter.Instruction;
import com.arm.nevada.client.interpreter.LogicalInstruction;
import com.arm.nevada.client.interpreter.MemoryInstruction;
import com.arm.nevada.client.interpreter.MinimumAndMaximumInstruction;
import com.arm.nevada.client.interpreter.MoveFPSCAndRAPSR;
import com.arm.nevada.client.interpreter.MoveInstruction;
import com.arm.nevada.client.interpreter.MultiplyInstruction;
import com.arm.nevada.client.interpreter.ReciprocalSqrtReciprocalEstimate;
import com.arm.nevada.client.interpreter.ReciprocalSqrtReciprocalStep;
import com.arm.nevada.client.interpreter.ReverseInstruction;
import com.arm.nevada.client.interpreter.ShiftInstruction;
import com.arm.nevada.client.interpreter.TableInstruction;
import com.arm.nevada.client.interpreter.VdupInstruction;
import com.arm.nevada.client.interpreter.VextInstruction;
import com.arm.nevada.client.interpreter.VmovInstruction;
import com.arm.nevada.client.interpreter.VswpInstruction;
import com.arm.nevada.client.interpreter.VtrnInstruction;
import com.arm.nevada.client.interpreter.ZipInstruction;

/**
 * This helper class for defining the list of valid instructions also the required parameters.
 * 
 */
public class InstructionFormats {

		private static final HashMap<EnumInstruction, List<InstructionForm>> instructionList;

		static {
			instructionList = new HashMap<EnumInstruction, List<InstructionForm>>();
			fillInstructions00();
			fillInstructions01();
		}

		private static void fillInstructions00(){
			//ARITHMETIC2
			//vadd int
			append(new ArithmeticInstructions(EnumInstruction.vadd, EnumRegisterType.QUAD), AllI.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vadd, EnumRegisterType.QUAD), AllI.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vadd, EnumRegisterType.DOUBLE), AllI.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vadd, EnumRegisterType.DOUBLE), AllI.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vadd float
			append(new ArithmeticInstructions(EnumInstruction.vadd, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vadd, EnumRegisterType.QUAD), F32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vadd, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vadd, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vsub int
			append(new ArithmeticInstructions(EnumInstruction.vsub, EnumRegisterType.QUAD), AllI.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vsub, EnumRegisterType.QUAD), AllI.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vsub, EnumRegisterType.DOUBLE), AllI.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vsub, EnumRegisterType.DOUBLE), AllI.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vsub float
			append(new ArithmeticInstructions(EnumInstruction.vsub, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vsub, EnumRegisterType.QUAD), F32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vsub, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vsub, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vaddhn
			append(new ArithmeticInstructions(EnumInstruction.vaddhn, EnumRegisterType.DOUBLE), I16.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vaddhn, EnumRegisterType.DOUBLE), I32.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vaddhn, EnumRegisterType.DOUBLE), I64.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			
			//vsubhn
			append(new ArithmeticInstructions(EnumInstruction.vsubhn, EnumRegisterType.DOUBLE), I16.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vsubhn, EnumRegisterType.DOUBLE), I32.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vsubhn, EnumRegisterType.DOUBLE), I64.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Q.p());

			//vaddl
			append(new ArithmeticInstructions(EnumInstruction.vaddl, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vaddl, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			
			//vaddw
			append(new ArithmeticInstructions(EnumInstruction.vaddw, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vaddw, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vaddw, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vaddw, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), D.p());

			//vsubl
			append(new ArithmeticInstructions(EnumInstruction.vsubl, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vsubl, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			
			//vsubw
			append(new ArithmeticInstructions(EnumInstruction.vsubw, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vsubw, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vsubw, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vsubw, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), D.p());
			
			//vhadd
			append(new ArithmeticInstructions(EnumInstruction.vhadd, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vhadd, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vhadd, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vhadd, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vhadd, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vhadd, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vhadd, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vhadd, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vhsub
			append(new ArithmeticInstructions(EnumInstruction.vhsub, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vhsub, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vhsub, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vhsub, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vhsub, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vhsub, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vhsub, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vhsub, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vpadal
			append(new ArithmeticInstructions(EnumInstruction.vpadal, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vpadal, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vpadal, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vpadal, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			//vpadd integer
			append(new ArithmeticInstructions(EnumInstruction.vpadd, EnumRegisterType.DOUBLE), AllISingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vpadd, EnumRegisterType.DOUBLE), AllISingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vpadd float
			append(new ArithmeticInstructions(EnumInstruction.vpadd, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vpadd, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vpaddl
			append(new ArithmeticInstructions(EnumInstruction.vpaddl, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vpaddl, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vpaddl, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vpaddl, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			//vraddhn
			append(new ArithmeticInstructions(EnumInstruction.vraddhn, EnumRegisterType.DOUBLE), I16.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vraddhn, EnumRegisterType.DOUBLE), I32.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vraddhn, EnumRegisterType.DOUBLE), I64.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			
			//vrsubn
			append(new ArithmeticInstructions(EnumInstruction.vrsubhn, EnumRegisterType.DOUBLE), I16.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vrsubhn, EnumRegisterType.DOUBLE), I32.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vrsubhn, EnumRegisterType.DOUBLE), I64.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			
			//vrhadd
			append(new ArithmeticInstructions(EnumInstruction.vrhadd, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vrhadd, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vrhadd, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vrhadd, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vrhadd, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vrhadd, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vrhadd, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vrhadd, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vqadd
			append(new ArithmeticInstructions(EnumInstruction.vqadd, EnumRegisterType.QUAD), AllS.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vqadd, EnumRegisterType.QUAD), AllS.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vqadd, EnumRegisterType.QUAD), AllU.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vqadd, EnumRegisterType.QUAD), AllU.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vqadd, EnumRegisterType.DOUBLE), AllS.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vqadd, EnumRegisterType.DOUBLE), AllS.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vqadd, EnumRegisterType.DOUBLE), AllU.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vqadd, EnumRegisterType.DOUBLE), AllU.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vqsub
			append(new ArithmeticInstructions(EnumInstruction.vqsub, EnumRegisterType.QUAD), AllS.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vqsub, EnumRegisterType.QUAD), AllS.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vqsub, EnumRegisterType.QUAD), AllU.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vqsub, EnumRegisterType.QUAD), AllU.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ArithmeticInstructions(EnumInstruction.vqsub, EnumRegisterType.DOUBLE), AllS.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vqsub, EnumRegisterType.DOUBLE), AllS.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vqsub, EnumRegisterType.DOUBLE), AllU.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ArithmeticInstructions(EnumInstruction.vqsub, EnumRegisterType.DOUBLE), AllU.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//LOGICAL2
			//vand register
			append(new LogicalInstruction(EnumInstruction.vand, EnumRegisterType.QUAD, false), OptType.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new LogicalInstruction(EnumInstruction.vand, EnumRegisterType.QUAD, false), OptType.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new LogicalInstruction(EnumInstruction.vand, EnumRegisterType.DOUBLE, false), OptType.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new LogicalInstruction(EnumInstruction.vand, EnumRegisterType.DOUBLE, false), OptType.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vand immediate
			append(new LogicalInstruction(EnumInstruction.vand, EnumRegisterType.QUAD, true), OptType.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), ImmVbic.p());
			append(new LogicalInstruction(EnumInstruction.vand, EnumRegisterType.QUAD, true), OptType.p(), Space.p(), Q2in1.p(), Comma.p(), ImmVbic.p());
			append(new LogicalInstruction(EnumInstruction.vand, EnumRegisterType.DOUBLE, true), OptType.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), ImmVbic.p());
			append(new LogicalInstruction(EnumInstruction.vand, EnumRegisterType.DOUBLE, true), OptType.p(), Space.p(), D2in1.p(), Comma.p(), ImmVbic.p());
			
			//vbic immediate
			append(new LogicalInstruction(EnumInstruction.vbic, EnumRegisterType.QUAD, true), OptType.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), ImmVbic.p());
			append(new LogicalInstruction(EnumInstruction.vbic, EnumRegisterType.QUAD, true), OptType.p(), Space.p(), Q2in1.p(), Comma.p(), ImmVbic.p());
			append(new LogicalInstruction(EnumInstruction.vbic, EnumRegisterType.DOUBLE, true), OptType.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), ImmVbic.p());
			append(new LogicalInstruction(EnumInstruction.vbic, EnumRegisterType.DOUBLE, true), OptType.p(), Space.p(), D2in1.p(), Comma.p(), ImmVbic.p());
			
			//vbic register
			append(new LogicalInstruction(EnumInstruction.vbic, EnumRegisterType.QUAD, false), OptType.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new LogicalInstruction(EnumInstruction.vbic, EnumRegisterType.QUAD, false), OptType.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new LogicalInstruction(EnumInstruction.vbic, EnumRegisterType.DOUBLE, false), OptType.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new LogicalInstruction(EnumInstruction.vbic, EnumRegisterType.DOUBLE, false), OptType.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//veor register
			append(new LogicalInstruction(EnumInstruction.veor, EnumRegisterType.QUAD, false), OptType.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new LogicalInstruction(EnumInstruction.veor, EnumRegisterType.QUAD, false), OptType.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new LogicalInstruction(EnumInstruction.veor, EnumRegisterType.DOUBLE, false), OptType.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new LogicalInstruction(EnumInstruction.veor, EnumRegisterType.DOUBLE, false), OptType.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vbif register
			append(new LogicalInstruction(EnumInstruction.vbif, EnumRegisterType.QUAD, false), OptType.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new LogicalInstruction(EnumInstruction.vbif, EnumRegisterType.QUAD, false), OptType.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new LogicalInstruction(EnumInstruction.vbif, EnumRegisterType.DOUBLE, false), OptType.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new LogicalInstruction(EnumInstruction.vbif, EnumRegisterType.DOUBLE, false), OptType.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vbit register
			append(new LogicalInstruction(EnumInstruction.vbit, EnumRegisterType.QUAD, false), OptType.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new LogicalInstruction(EnumInstruction.vbit, EnumRegisterType.QUAD, false), OptType.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new LogicalInstruction(EnumInstruction.vbit, EnumRegisterType.DOUBLE, false), OptType.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new LogicalInstruction(EnumInstruction.vbit, EnumRegisterType.DOUBLE, false), OptType.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vbsl register
			append(new LogicalInstruction(EnumInstruction.vbsl, EnumRegisterType.QUAD, false), OptType.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new LogicalInstruction(EnumInstruction.vbsl, EnumRegisterType.QUAD, false), OptType.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new LogicalInstruction(EnumInstruction.vbsl, EnumRegisterType.DOUBLE, false), OptType.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new LogicalInstruction(EnumInstruction.vbsl, EnumRegisterType.DOUBLE, false), OptType.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vorr register
			append(new LogicalInstruction(EnumInstruction.vorr, EnumRegisterType.QUAD, false), OptType.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new LogicalInstruction(EnumInstruction.vorr, EnumRegisterType.QUAD, false), OptType.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new LogicalInstruction(EnumInstruction.vorr, EnumRegisterType.DOUBLE, false), OptType.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new LogicalInstruction(EnumInstruction.vorr, EnumRegisterType.DOUBLE, false), OptType.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vorr immediate
			append(new LogicalInstruction(EnumInstruction.vorr, EnumRegisterType.QUAD, true), OptType.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), ImmVorr.p());
			append(new LogicalInstruction(EnumInstruction.vorr, EnumRegisterType.QUAD, true), OptType.p(), Space.p(), Q2in1.p(), Comma.p(), ImmVorr.p());
			append(new LogicalInstruction(EnumInstruction.vorr, EnumRegisterType.DOUBLE, true), OptType.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), ImmVorr.p());
			append(new LogicalInstruction(EnumInstruction.vorr, EnumRegisterType.DOUBLE, true), OptType.p(), Space.p(), D2in1.p(), Comma.p(), ImmVorr.p());
			
			//vorn register
			append(new LogicalInstruction(EnumInstruction.vorn, EnumRegisterType.QUAD, false), OptType.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new LogicalInstruction(EnumInstruction.vorn, EnumRegisterType.QUAD, false), OptType.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new LogicalInstruction(EnumInstruction.vorn, EnumRegisterType.DOUBLE, false), OptType.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new LogicalInstruction(EnumInstruction.vorn, EnumRegisterType.DOUBLE, false), OptType.p(), Space.p(), D2in1.p(), Comma.p(), D.p());

			//MOVE
			// special vmov
			append(new VmovInstruction(VmovInstruction.Mode.ARM_TO_D), Space.p(), D.p(), Comma.p(), RNotPCNotSP.p(), Comma.p(), RNotPCNotSP.p());
			append(new VmovInstruction(VmovInstruction.Mode.D_TO_ARM), Space.p(), RNotPCNotSP.p(), Comma.p(), RNotPCNotSP.p(), Comma.p(), D.p());
			append(new VmovInstruction(VmovInstruction.Mode.ARM_TO_DSUB), AllSingleWide.p(), Space.p(), DSubReg.p(), Comma.p(), RNotPCNotSP.p());
			append(new VmovInstruction(VmovInstruction.Mode.ARM_TO_DSUB), DefType.p(EnumDataType._32), Space.p(), DSubReg.p(), Comma.p(), RNotPCNotSP.p());
			append(new VmovInstruction(VmovInstruction.Mode.DSUB_TO_ARM), AllSignedSingle.p(), Space.p(), RNotPCNotSP.p(), Comma.p(), DSubReg.p());
			append(new VmovInstruction(VmovInstruction.Mode.DSUB_TO_ARM), AllUnsignedSingle.p(), Space.p(), RNotPCNotSP.p(), Comma.p(), DSubReg.p());
			append(new VmovInstruction(VmovInstruction.Mode.DSUB_TO_ARM), All32.p(), Space.p(), RNotPCNotSP.p(), Comma.p(), DSubReg.p());
			append(new VmovInstruction(VmovInstruction.Mode.DSUB_TO_ARM), DefType.p(EnumDataType._32), Space.p(), RNotPCNotSP.p(), Comma.p(), DSubReg.p());

			//ordinary move
			//vmov neon imm
			append(new MoveInstruction(EnumInstruction.vmov, EnumRegisterType.QUAD, true), All.p(), Space.p(), Q.p(), Comma.p(), ImmVmov.p());
			append(new MoveInstruction(EnumInstruction.vmov, EnumRegisterType.DOUBLE, true), All.p(), Space.p(), D.p(), Comma.p(), ImmVmov.p());

			//vmov neon neon
			append(new MoveInstruction(EnumInstruction.vmov, EnumRegisterType.QUAD, false), OptType.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new MoveInstruction(EnumInstruction.vmov, EnumRegisterType.DOUBLE, false), OptType.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			//vmvn neon imm
			append(new MoveInstruction(EnumInstruction.vmvn, EnumRegisterType.QUAD, true), All.p(), Space.p(), Q.p(), Comma.p(), ImmVmvn.p());
			append(new MoveInstruction(EnumInstruction.vmvn, EnumRegisterType.DOUBLE, true), All.p(), Space.p(), D.p(), Comma.p(), ImmVmvn.p());

			//vmvn neon neon
			append(new MoveInstruction(EnumInstruction.vmvn, EnumRegisterType.QUAD, false), OptType.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new MoveInstruction(EnumInstruction.vmvn, EnumRegisterType.DOUBLE, false), OptType.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			//vmovn neon neon
			append(new MoveInstruction(EnumInstruction.vmovn, EnumRegisterType.DOUBLE, false), I16.p(), Space.p(), D.p(), Comma.p(), Q.p());
			append(new MoveInstruction(EnumInstruction.vmovn, EnumRegisterType.DOUBLE, false), I32.p(), Space.p(), D.p(), Comma.p(), Q.p());
			append(new MoveInstruction(EnumInstruction.vmovn, EnumRegisterType.DOUBLE, false), I64.p(), Space.p(), D.p(), Comma.p(), Q.p());
			
			//vmovl neon neon
			append(new MoveInstruction(EnumInstruction.vmovl, EnumRegisterType.QUAD, false), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), D.p());
			append(new MoveInstruction(EnumInstruction.vmovl, EnumRegisterType.QUAD, false), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), D.p());
			
			//vqmov{u}n neon neon
			append(new MoveInstruction(EnumInstruction.vqmovun, EnumRegisterType.DOUBLE, false), S16.p(), Space.p(), D.p(), Comma.p(), Q.p());
			append(new MoveInstruction(EnumInstruction.vqmovun, EnumRegisterType.DOUBLE, false), S32.p(), Space.p(), D.p(), Comma.p(), Q.p());
			append(new MoveInstruction(EnumInstruction.vqmovun, EnumRegisterType.DOUBLE, false), S64.p(), Space.p(), D.p(), Comma.p(), Q.p());
			
			append(new MoveInstruction(EnumInstruction.vqmovn, EnumRegisterType.DOUBLE, false), U16.p(), Space.p(), D.p(), Comma.p(), Q.p());
			append(new MoveInstruction(EnumInstruction.vqmovn, EnumRegisterType.DOUBLE, false), U32.p(), Space.p(), D.p(), Comma.p(), Q.p());
			append(new MoveInstruction(EnumInstruction.vqmovn, EnumRegisterType.DOUBLE, false), U64.p(), Space.p(), D.p(), Comma.p(), Q.p());
			append(new MoveInstruction(EnumInstruction.vqmovn, EnumRegisterType.DOUBLE, false), S16.p(), Space.p(), D.p(), Comma.p(), Q.p());
			append(new MoveInstruction(EnumInstruction.vqmovn, EnumRegisterType.DOUBLE, false), S32.p(), Space.p(), D.p(), Comma.p(), Q.p());
			append(new MoveInstruction(EnumInstruction.vqmovn, EnumRegisterType.DOUBLE, false), S64.p(), Space.p(), D.p(), Comma.p(), Q.p());
			
			//DUPLICATE
			// #vdup
			append(new VdupInstruction(false, EnumRegisterType.QUAD), AllSingleWide.p(), Space.p(), Q.p(), Comma.p(), DSubReg.p());
			append(new VdupInstruction(false, EnumRegisterType.DOUBLE), AllSingleWide.p(), Space.p(), D.p(), Comma.p(), DSubReg.p());
			append(new VdupInstruction(true, EnumRegisterType.QUAD), AllSingleWide.p(), Space.p(), Q.p(), Comma.p(), R.p());
			append(new VdupInstruction(true, EnumRegisterType.DOUBLE), AllSingleWide.p(), Space.p(), D.p(), Comma.p(), R.p());

			// VLD: #vld1 #vld2 #vld3 #vld4
			// #vld1: all
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ALL, 1, 1, false), All.p(), Space.p(), ListSubIndex.p(1, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }));
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ALL, 1, 1, false), All.p(), Space.p(), ListSubIndex.p(1, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ALL, 1, 1, true), All.p(), Space.p(), ListSubIndex.p(1, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());

			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ALL, 2, 1, false), All.p(), Space.p(), ListSubIndex.p(2, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }));
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ALL, 2, 1, false), All.p(), Space.p(), ListSubIndex.p(2, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ALL, 2, 1, true), All.p(), Space.p(), ListSubIndex.p(2, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), WriteBack.p());

			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ALL, 3, 1, false), All.p(), Space.p(), ListSubIndex.p(3, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }));
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ALL, 3, 1, false), All.p(), Space.p(), ListSubIndex.p(3, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ALL, 3, 1, true), All.p(), Space.p(), ListSubIndex.p(3, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());

			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ALL, 4, 1, false), All.p(), Space.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }));
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ALL, 4, 1, false), All.p(), Space.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ALL, 4, 1, true), All.p(), Space.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }), WriteBack.p());

			// #vld1: One
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE, 1, 1, false), All16.p(), Space.p(), ListSubIndex.p(1, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 16 }));
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE, 1, 1, false), All16.p(), Space.p(), ListSubIndex.p(1, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 16 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE, 1, 1, true), All16.p(), Space.p(), ListSubIndex.p(1, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 16 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE, 1, 1, false), All32.p(), Space.p(), ListSubIndex.p(1, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }));
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE, 1, 1, false), All32.p(), Space.p(), ListSubIndex.p(1, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE, 1, 1, true), All32.p(), Space.p(), ListSubIndex.p(1, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE, 1, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(1, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }));
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE, 1, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(1, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE, 1, 1, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(1, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }), WriteBack.p());

			// #vld1: Repeat one
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE_REPEAT, 1, 1, false), All16.p(), Space.p(), ListSubIndex.p(1, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 16 }));
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE_REPEAT, 1, 1, false), All16.p(), Space.p(), ListSubIndex.p(1, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 16 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE_REPEAT, 1, 1, true), All16.p(), Space.p(), ListSubIndex.p(1, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 16 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE_REPEAT, 1, 1, false), All32.p(), Space.p(), ListSubIndex.p(1, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 32 }));
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE_REPEAT, 1, 1, false), All32.p(), Space.p(), ListSubIndex.p(1, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE_REPEAT, 1, 1, true), All32.p(), Space.p(), ListSubIndex.p(1, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE_REPEAT, 1, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(1, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8 }));
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE_REPEAT, 1, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(1, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE_REPEAT, 1, 1, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(1, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8 }), WriteBack.p());

			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE_REPEAT, 2, 1, false), All16.p(), Space.p(), ListSubIndex.p(2, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 16 }));
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE_REPEAT, 2, 1, false), All16.p(), Space.p(), ListSubIndex.p(2, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 16 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE_REPEAT, 2, 1, true), All16.p(), Space.p(), ListSubIndex.p(2, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 16 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE_REPEAT, 2, 1, false), All32.p(), Space.p(), ListSubIndex.p(2, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 32 }));
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE_REPEAT, 2, 1, false), All32.p(), Space.p(), ListSubIndex.p(2, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE_REPEAT, 2, 1, true), All32.p(), Space.p(), ListSubIndex.p(2, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE_REPEAT, 2, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(2, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8 }));
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE_REPEAT, 2, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(2, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld1, MemoryInstruction.Mode.ONE_REPEAT, 2, 1, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(2, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8 }), WriteBack.p());

			// vld2: all
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ALL, 2, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(2, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }));
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ALL, 2, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(2, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ALL, 2, 1, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(2, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), WriteBack.p());

			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ALL, 2, 2, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(2, 2, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }));
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ALL, 2, 2, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(2, 2, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ALL, 2, 2, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(2, 2, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), WriteBack.p());

			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ALL, 4, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }));
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ALL, 4, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ALL, 4, 1, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }), WriteBack.p());

			// vld2: one
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE, 2, 1, false), All8.p(), Space.p(), ListSubIndex.p(2, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 16 }));
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE, 2, 1, false), All8.p(), Space.p(), ListSubIndex.p(2, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 16 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE, 2, 1, true), All8.p(), Space.p(), ListSubIndex.p(2, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 16 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE, 2, 1, false), All16.p(), Space.p(), ListSubIndex.p(2, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }));
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE, 2, 1, false), All16.p(), Space.p(), ListSubIndex.p(2, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE, 2, 1, true), All16.p(), Space.p(), ListSubIndex.p(2, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE, 2, 1, false), All32.p(), Space.p(), ListSubIndex.p(2, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }));
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE, 2, 1, false), All32.p(), Space.p(), ListSubIndex.p(2, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE, 2, 1, true), All32.p(), Space.p(), ListSubIndex.p(2, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());

			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE, 2, 2, false), All16.p(), Space.p(), ListSubIndex.p(2, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }));
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE, 2, 2, false), All16.p(), Space.p(), ListSubIndex.p(2, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE, 2, 2, true), All16.p(), Space.p(), ListSubIndex.p(2, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE, 2, 2, false), All32.p(), Space.p(), ListSubIndex.p(2, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }));
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE, 2, 2, false), All32.p(), Space.p(), ListSubIndex.p(2, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE, 2, 2, true), All32.p(), Space.p(), ListSubIndex.p(2, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());

			// vld2: repeat one
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE_REPEAT, 2, 1, false), All8.p(), Space.p(), ListSubIndex.p(2, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 16 }));
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE_REPEAT, 2, 1, false), All8.p(), Space.p(), ListSubIndex.p(2, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 16 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE_REPEAT, 2, 1, true), All8.p(), Space.p(), ListSubIndex.p(2, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 16 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE_REPEAT, 2, 1, false), All16.p(), Space.p(), ListSubIndex.p(2, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 32 }));
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE_REPEAT, 2, 1, false), All16.p(), Space.p(), ListSubIndex.p(2, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE_REPEAT, 2, 1, true), All16.p(), Space.p(), ListSubIndex.p(2, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE_REPEAT, 2, 1, false), All32.p(), Space.p(), ListSubIndex.p(2, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }));
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE_REPEAT, 2, 1, false), All32.p(), Space.p(), ListSubIndex.p(2, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE_REPEAT, 2, 1, true), All32.p(), Space.p(), ListSubIndex.p(2, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());

			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE_REPEAT, 2, 2, false), All8.p(), Space.p(), ListSubIndex.p(2, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 16 }));
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE_REPEAT, 2, 2, false), All8.p(), Space.p(), ListSubIndex.p(2, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 16 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE_REPEAT, 2, 2, true), All8.p(), Space.p(), ListSubIndex.p(2, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 16 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE_REPEAT, 2, 2, false), All16.p(), Space.p(), ListSubIndex.p(2, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 32 }));
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE_REPEAT, 2, 2, false), All16.p(), Space.p(), ListSubIndex.p(2, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE_REPEAT, 2, 2, true), All16.p(), Space.p(), ListSubIndex.p(2, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE_REPEAT, 2, 2, false), All32.p(), Space.p(), ListSubIndex.p(2, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }));
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE_REPEAT, 2, 2, false), All32.p(), Space.p(), ListSubIndex.p(2, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld2, MemoryInstruction.Mode.ONE_REPEAT, 2, 2, true), All32.p(), Space.p(), ListSubIndex.p(2, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());

			// vld3: all
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ALL, 3, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8 }));
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ALL, 3, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ALL, 3, 1, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ALL, 3, 2, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 2, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }));
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ALL, 3, 2, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 2, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ALL, 3, 2, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 2, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());

			// vld3: one
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ONE, 3, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }));
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ONE, 3, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ONE, 3, 1, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ONE, 3, 2, false), All16.p(), Space.p(), ListSubIndex.p(3, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }));
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ONE, 3, 2, false), All16.p(), Space.p(), ListSubIndex.p(3, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ONE, 3, 2, true), All16.p(), Space.p(), ListSubIndex.p(3, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ONE, 3, 2, false), All32.p(), Space.p(), ListSubIndex.p(3, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }));
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ONE, 3, 2, false), All32.p(), Space.p(), ListSubIndex.p(3, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ONE, 3, 2, true), All32.p(), Space.p(), ListSubIndex.p(3, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }), WriteBack.p());

			// vld3: one repeat
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ONE_REPEAT, 3, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8 }));
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ONE_REPEAT, 3, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ONE_REPEAT, 3, 1, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ONE_REPEAT, 3, 2, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8 }));
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ONE_REPEAT, 3, 2, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld3, MemoryInstruction.Mode.ONE_REPEAT, 3, 2, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8 }), WriteBack.p());

			// vld4: all
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ALL, 4, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }));
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ALL, 4, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ALL, 4, 1, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ALL, 4, 2, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(4, 2, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }));
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ALL, 4, 2, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(4, 2, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ALL, 4, 2, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(4, 2, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }), WriteBack.p());

			// vld4: one
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE, 4, 1, false), All8.p(), Space.p(), ListSubIndex.p(4, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }));
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE, 4, 1, false), All8.p(), Space.p(), ListSubIndex.p(4, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE, 4, 1, true), All8.p(), Space.p(), ListSubIndex.p(4, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE, 4, 1, false), All16.p(), Space.p(), ListSubIndex.p(4, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }));
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE, 4, 1, false), All16.p(), Space.p(), ListSubIndex.p(4, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE, 4, 1, true), All16.p(), Space.p(), ListSubIndex.p(4, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE, 4, 1, false), All32.p(), Space.p(), ListSubIndex.p(4, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }));
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE, 4, 1, false), All32.p(), Space.p(), ListSubIndex.p(4, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE, 4, 1, true), All32.p(), Space.p(), ListSubIndex.p(4, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), WriteBack.p());

			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE, 4, 2, false), All16.p(), Space.p(), ListSubIndex.p(4, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }));
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE, 4, 2, false), All16.p(), Space.p(), ListSubIndex.p(4, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE, 4, 2, true), All16.p(), Space.p(), ListSubIndex.p(4, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE, 4, 2, false), All32.p(), Space.p(), ListSubIndex.p(4, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }));
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE, 4, 2, false), All32.p(), Space.p(), ListSubIndex.p(4, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE, 4, 2, true), All32.p(), Space.p(), ListSubIndex.p(4, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), WriteBack.p());

			// vld4: one repeat
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE_REPEAT, 4, 1, false), All8.p(), Space.p(), ListSubIndex.p(4, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 32 }));
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE_REPEAT, 4, 1, false), All8.p(), Space.p(), ListSubIndex.p(4, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE_REPEAT, 4, 1, true), All8.p(), Space.p(), ListSubIndex.p(4, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE_REPEAT, 4, 1, false), All16.p(), Space.p(), ListSubIndex.p(4, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }));
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE_REPEAT, 4, 1, false), All16.p(), Space.p(), ListSubIndex.p(4, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE_REPEAT, 4, 1, true), All16.p(), Space.p(), ListSubIndex.p(4, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE_REPEAT, 4, 1, false), All32.p(), Space.p(), ListSubIndex.p(4, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }));
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE_REPEAT, 4, 1, false), All32.p(), Space.p(), ListSubIndex.p(4, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE_REPEAT, 4, 1, true), All32.p(), Space.p(), ListSubIndex.p(4, 1, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), WriteBack.p());

			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE_REPEAT, 4, 2, false), All8.p(), Space.p(), ListSubIndex.p(4, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 32 }));
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE_REPEAT, 4, 2, false), All8.p(), Space.p(), ListSubIndex.p(4, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE_REPEAT, 4, 2, true), All8.p(), Space.p(), ListSubIndex.p(4, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE_REPEAT, 4, 2, false), All16.p(), Space.p(), ListSubIndex.p(4, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }));
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE_REPEAT, 4, 2, false), All16.p(), Space.p(), ListSubIndex.p(4, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE_REPEAT, 4, 2, true), All16.p(), Space.p(), ListSubIndex.p(4, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE_REPEAT, 4, 2, false), All32.p(), Space.p(), ListSubIndex.p(4, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }));
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE_REPEAT, 4, 2, false), All32.p(), Space.p(), ListSubIndex.p(4, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vld4, MemoryInstruction.Mode.ONE_REPEAT, 4, 2, true), All32.p(), Space.p(), ListSubIndex.p(4, 2, true, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), WriteBack.p());

			// VST: #vst1 #vst2 #vst3 #vst4
			// #vst1: ALL
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ALL, 1, 1, false), All.p(), Space.p(), ListSubIndex.p(1, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }));
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ALL, 1, 1, false), All.p(), Space.p(), ListSubIndex.p(1, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ALL, 1, 1, true), All.p(), Space.p(), ListSubIndex.p(1, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ALL, 2, 1, false), All.p(), Space.p(), ListSubIndex.p(2, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }));
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ALL, 2, 1, false), All.p(), Space.p(), ListSubIndex.p(2, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ALL, 2, 1, true), All.p(), Space.p(), ListSubIndex.p(2, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ALL, 3, 1, false), All.p(), Space.p(), ListSubIndex.p(3, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }));
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ALL, 3, 1, false), All.p(), Space.p(), ListSubIndex.p(3, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ALL, 3, 1, true), All.p(), Space.p(), ListSubIndex.p(3, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ALL, 4, 1, false), All.p(), Space.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }));
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ALL, 4, 1, false), All.p(), Space.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ALL, 4, 1, true), All.p(), Space.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }), WriteBack.p());

			// vst1: ONE
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ONE, 1, 1, false), All8.p(), Space.p(), ListSubIndex.p(1, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }));
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ONE, 1, 1, false), All8.p(), Space.p(), ListSubIndex.p(1, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ONE, 1, 1, true), All8.p(), Space.p(), ListSubIndex.p(1, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ONE, 1, 1, false), All16.p(), Space.p(), ListSubIndex.p(1, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 16 }));
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ONE, 1, 1, false), All16.p(), Space.p(), ListSubIndex.p(1, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 16 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ONE, 1, 1, true), All16.p(), Space.p(), ListSubIndex.p(1, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 16 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ONE, 1, 1, false), All32.p(), Space.p(), ListSubIndex.p(1, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }));
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ONE, 1, 1, false), All32.p(), Space.p(), ListSubIndex.p(1, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst1, MemoryInstruction.Mode.ONE, 1, 1, true), All32.p(), Space.p(), ListSubIndex.p(1, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), WriteBack.p());

			// vst2: ALL
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ALL, 2, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(2, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }));
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ALL, 2, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(2, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ALL, 2, 1, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(2, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ALL, 2, 2, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(2, 2, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }));
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ALL, 2, 2, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(2, 2, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ALL, 2, 2, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(2, 2, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ALL, 4, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }));
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ALL, 4, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ALL, 4, 1, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }), WriteBack.p());

			// vst2: ONE
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ONE, 2, 1, false), All8.p(), Space.p(), ListSubIndex.p(2, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 16 }));
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ONE, 2, 1, false), All8.p(), Space.p(), ListSubIndex.p(2, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 16 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ONE, 2, 1, true), All8.p(), Space.p(), ListSubIndex.p(2, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 16 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ONE, 2, 1, false), All16.p(), Space.p(), ListSubIndex.p(2, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }));
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ONE, 2, 1, false), All16.p(), Space.p(), ListSubIndex.p(2, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ONE, 2, 1, true), All16.p(), Space.p(), ListSubIndex.p(2, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ONE, 2, 1, false), All32.p(), Space.p(), ListSubIndex.p(2, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }));
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ONE, 2, 1, false), All32.p(), Space.p(), ListSubIndex.p(2, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ONE, 2, 1, true), All32.p(), Space.p(), ListSubIndex.p(2, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());

			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ONE, 2, 2, false), All16.p(), Space.p(), ListSubIndex.p(2, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }));
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ONE, 2, 2, false), All16.p(), Space.p(), ListSubIndex.p(2, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ONE, 2, 2, true), All16.p(), Space.p(), ListSubIndex.p(2, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ONE, 2, 2, false), All32.p(), Space.p(), ListSubIndex.p(2, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }));
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ONE, 2, 2, false), All32.p(), Space.p(), ListSubIndex.p(2, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst2, MemoryInstruction.Mode.ONE, 2, 2, true), All32.p(), Space.p(), ListSubIndex.p(2, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());

			// vst3: ALL
			append(new MemoryInstruction(EnumInstruction.vst3, MemoryInstruction.Mode.ALL, 3, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }));
			append(new MemoryInstruction(EnumInstruction.vst3, MemoryInstruction.Mode.ALL, 3, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst3, MemoryInstruction.Mode.ALL, 3, 1, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vst3, MemoryInstruction.Mode.ALL, 3, 2, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 2, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }));
			append(new MemoryInstruction(EnumInstruction.vst3, MemoryInstruction.Mode.ALL, 3, 2, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 2, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst3, MemoryInstruction.Mode.ALL, 3, 2, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 2, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());

			// vst3: ONE
			append(new MemoryInstruction(EnumInstruction.vst3, MemoryInstruction.Mode.ONE, 3, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }));
			append(new MemoryInstruction(EnumInstruction.vst3, MemoryInstruction.Mode.ONE, 3, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst3, MemoryInstruction.Mode.ONE, 3, 1, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(3, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vst3, MemoryInstruction.Mode.ONE, 3, 2, false), All16.p(), Space.p(), ListSubIndex.p(3, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }));
			append(new MemoryInstruction(EnumInstruction.vst3, MemoryInstruction.Mode.ONE, 3, 2, false), All16.p(), Space.p(), ListSubIndex.p(3, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst3, MemoryInstruction.Mode.ONE, 3, 2, true), All16.p(), Space.p(), ListSubIndex.p(3, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vst3, MemoryInstruction.Mode.ONE, 3, 2, false), All32.p(), Space.p(), ListSubIndex.p(3, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }));
			append(new MemoryInstruction(EnumInstruction.vst3, MemoryInstruction.Mode.ONE, 3, 2, false), All32.p(), Space.p(), ListSubIndex.p(3, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst3, MemoryInstruction.Mode.ONE, 3, 2, true), All32.p(), Space.p(), ListSubIndex.p(3, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8 }), WriteBack.p());

			// vst4: ALL
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ALL, 4, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }));
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ALL, 4, 1, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ALL, 4, 1, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ALL, 4, 2, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(4, 2, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }));
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ALL, 4, 2, false), AllSingleWide.p(), Space.p(), ListSubIndex.p(4, 2, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ALL, 4, 2, true), AllSingleWide.p(), Space.p(), ListSubIndex.p(4, 2, false, false), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128, 256 }), WriteBack.p());

			// vst4: ONE
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ONE, 4, 1, false), All8.p(), Space.p(), ListSubIndex.p(4, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }));
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ONE, 4, 1, false), All8.p(), Space.p(), ListSubIndex.p(4, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ONE, 4, 1, true), All8.p(), Space.p(), ListSubIndex.p(4, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ONE, 4, 1, false), All16.p(), Space.p(), ListSubIndex.p(4, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }));
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ONE, 4, 1, false), All16.p(), Space.p(), ListSubIndex.p(4, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ONE, 4, 1, true), All16.p(), Space.p(), ListSubIndex.p(4, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ONE, 4, 1, false), All32.p(), Space.p(), ListSubIndex.p(4, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }));
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ONE, 4, 1, false), All32.p(), Space.p(), ListSubIndex.p(4, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ONE, 4, 1, true), All32.p(), Space.p(), ListSubIndex.p(4, 1, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), WriteBack.p());

			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ONE, 4, 2, false), All8.p(), Space.p(), ListSubIndex.p(4, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }));
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ONE, 4, 2, false), All8.p(), Space.p(), ListSubIndex.p(4, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ONE, 4, 2, true), All8.p(), Space.p(), ListSubIndex.p(4, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 32 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ONE, 4, 2, false), All16.p(), Space.p(), ListSubIndex.p(4, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }));
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ONE, 4, 2, false), All16.p(), Space.p(), ListSubIndex.p(4, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ONE, 4, 2, true), All16.p(), Space.p(), ListSubIndex.p(4, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64 }), WriteBack.p());
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ONE, 4, 2, false), All32.p(), Space.p(), ListSubIndex.p(4, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }));
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ONE, 4, 2, false), All32.p(), Space.p(), ListSubIndex.p(4, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), Comma.p(), ROffset.p());
			append(new MemoryInstruction(EnumInstruction.vst4, MemoryInstruction.Mode.ONE, 4, 2, true), All32.p(), Space.p(), ListSubIndex.p(4, 2, true, true), Comma.p(), BaseAddress.p(new int[] { 8, 64, 128 }), WriteBack.p());
			
			//SHIFT instructions
			//vqrshl
			append(new ShiftInstruction(EnumInstruction.vqrshl, EnumRegisterType.QUAD, false), AllU.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ShiftInstruction(EnumInstruction.vqrshl, EnumRegisterType.QUAD, false), AllU.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ShiftInstruction(EnumInstruction.vqrshl, EnumRegisterType.DOUBLE, false), AllU.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ShiftInstruction(EnumInstruction.vqrshl, EnumRegisterType.DOUBLE, false), AllU.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new ShiftInstruction(EnumInstruction.vqrshl, EnumRegisterType.QUAD, false), AllS.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ShiftInstruction(EnumInstruction.vqrshl, EnumRegisterType.QUAD, false), AllS.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ShiftInstruction(EnumInstruction.vqrshl, EnumRegisterType.DOUBLE, false), AllS.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ShiftInstruction(EnumInstruction.vqrshl, EnumRegisterType.DOUBLE, false), AllS.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vshl
			append(new ShiftInstruction(EnumInstruction.vshl, EnumRegisterType.DOUBLE, true), AllI.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), Imm0toSizeMinus1.p());
			append(new ShiftInstruction(EnumInstruction.vshl, EnumRegisterType.QUAD, true), AllI.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Imm0toSizeMinus1.p());
			append(new ShiftInstruction(EnumInstruction.vshl, EnumRegisterType.DOUBLE, true), AllI.p(), Space.p(), D.p(), Comma.p(), Imm0toSizeMinus1.p());
			append(new ShiftInstruction(EnumInstruction.vshl, EnumRegisterType.QUAD, true), AllI.p(), Space.p(), Q.p(), Comma.p(), Imm0toSizeMinus1.p());
			
			append(new ShiftInstruction(EnumInstruction.vshl, EnumRegisterType.DOUBLE, false), AllS.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ShiftInstruction(EnumInstruction.vshl, EnumRegisterType.QUAD, false), AllS.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ShiftInstruction(EnumInstruction.vshl, EnumRegisterType.DOUBLE, false), AllU.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ShiftInstruction(EnumInstruction.vshl, EnumRegisterType.QUAD, false), AllU.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ShiftInstruction(EnumInstruction.vshl, EnumRegisterType.DOUBLE, false), AllS.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ShiftInstruction(EnumInstruction.vshl, EnumRegisterType.QUAD, false), AllS.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ShiftInstruction(EnumInstruction.vshl, EnumRegisterType.DOUBLE, false), AllU.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ShiftInstruction(EnumInstruction.vshl, EnumRegisterType.QUAD, false), AllU.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			
			//vshr
			append(new ShiftInstruction(EnumInstruction.vshr, EnumRegisterType.DOUBLE, true), AllS.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vshr, EnumRegisterType.QUAD, true), AllS.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vshr, EnumRegisterType.DOUBLE, true), AllS.p(), Space.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vshr, EnumRegisterType.QUAD, true), AllS.p(), Space.p(), Q.p(), Comma.p(), Imm1toSize.p());
			
			append(new ShiftInstruction(EnumInstruction.vshr, EnumRegisterType.DOUBLE, true), AllU.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vshr, EnumRegisterType.QUAD, true), AllU.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vshr, EnumRegisterType.DOUBLE, true), AllU.p(), Space.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vshr, EnumRegisterType.QUAD, true), AllU.p(), Space.p(), Q.p(), Comma.p(), Imm1toSize.p());
			
			//vsra
			append(new ShiftInstruction(EnumInstruction.vsra, EnumRegisterType.DOUBLE, true), AllS.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vsra, EnumRegisterType.QUAD, true), AllS.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vsra, EnumRegisterType.DOUBLE, true), AllS.p(), Space.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vsra, EnumRegisterType.QUAD, true), AllS.p(), Space.p(), Q.p(), Comma.p(), Imm1toSize.p());
			
			append(new ShiftInstruction(EnumInstruction.vsra, EnumRegisterType.DOUBLE, true), AllU.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vsra, EnumRegisterType.QUAD, true), AllU.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vsra, EnumRegisterType.DOUBLE, true), AllU.p(), Space.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vsra, EnumRegisterType.QUAD, true), AllU.p(), Space.p(), Q.p(), Comma.p(), Imm1toSize.p());
			
			//vrshl
			append(new ShiftInstruction(EnumInstruction.vrshl, EnumRegisterType.DOUBLE, false), AllS.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ShiftInstruction(EnumInstruction.vrshl, EnumRegisterType.QUAD, false), AllS.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ShiftInstruction(EnumInstruction.vrshl, EnumRegisterType.DOUBLE, false), AllU.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ShiftInstruction(EnumInstruction.vrshl, EnumRegisterType.QUAD, false), AllU.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ShiftInstruction(EnumInstruction.vrshl, EnumRegisterType.DOUBLE, false), AllS.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ShiftInstruction(EnumInstruction.vrshl, EnumRegisterType.QUAD, false), AllS.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ShiftInstruction(EnumInstruction.vrshl, EnumRegisterType.DOUBLE, false), AllU.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ShiftInstruction(EnumInstruction.vrshl, EnumRegisterType.QUAD, false), AllU.p(), Space.p(), Q.p(), Comma.p(), Q.p());

			//vrshr
			append(new ShiftInstruction(EnumInstruction.vrshr, EnumRegisterType.DOUBLE, true), AllS.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vrshr, EnumRegisterType.QUAD, true), AllS.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vrshr, EnumRegisterType.DOUBLE, true), AllS.p(), Space.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vrshr, EnumRegisterType.QUAD, true), AllS.p(), Space.p(), Q.p(), Comma.p(), Imm1toSize.p());
			
			append(new ShiftInstruction(EnumInstruction.vrshr, EnumRegisterType.DOUBLE, true), AllU.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vrshr, EnumRegisterType.QUAD, true), AllU.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vrshr, EnumRegisterType.DOUBLE, true), AllU.p(), Space.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vrshr, EnumRegisterType.QUAD, true), AllU.p(), Space.p(), Q.p(), Comma.p(), Imm1toSize.p());
			
			//vrsra
			append(new ShiftInstruction(EnumInstruction.vrsra, EnumRegisterType.DOUBLE, true), AllS.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vrsra, EnumRegisterType.QUAD, true), AllS.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vrsra, EnumRegisterType.DOUBLE, true), AllS.p(), Space.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vrsra, EnumRegisterType.QUAD, true), AllS.p(), Space.p(), Q.p(), Comma.p(), Imm1toSize.p());
			
			append(new ShiftInstruction(EnumInstruction.vrsra, EnumRegisterType.DOUBLE, true), AllU.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vrsra, EnumRegisterType.QUAD, true), AllU.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vrsra, EnumRegisterType.DOUBLE, true), AllU.p(), Space.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vrsra, EnumRegisterType.QUAD, true), AllU.p(), Space.p(), Q.p(), Comma.p(), Imm1toSize.p());
			
			//vshll  //only VSHLL<c><q>.<type><size> <Qd>, <Dm>, #<imm>
			append(new ShiftInstruction(EnumInstruction.vshll, EnumRegisterType.DOUBLE, true), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vshll, EnumRegisterType.DOUBLE, true), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vshll, EnumRegisterType.DOUBLE, true), AllISingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), ImmIsSize.p());
			
			//vshrn  //only VSHRN<c><q>.I<size> <Dd>, <Qm>, #<imm>
			append(new ShiftInstruction(EnumInstruction.vshrn, EnumRegisterType.QUAD, true), I16.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vshrn, EnumRegisterType.QUAD, true), I32.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vshrn, EnumRegisterType.QUAD, true), I64.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vshrn, EnumRegisterType.QUAD, true), _16.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vshrn, EnumRegisterType.QUAD, true), _32.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vshrn, EnumRegisterType.QUAD, true), _64.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			
			//vrshrn  //VRSHRN<c><q>.I<size> <Dd>, <Qm>, #<imm>
			append(new ShiftInstruction(EnumInstruction.vrshrn, EnumRegisterType.QUAD, true), I16.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vrshrn, EnumRegisterType.QUAD, true), I32.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vrshrn, EnumRegisterType.QUAD, true), I64.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vrshrn, EnumRegisterType.QUAD, true), _16.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vrshrn, EnumRegisterType.QUAD, true), _32.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vrshrn, EnumRegisterType.QUAD, true), _64.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			
			//vsli
			append(new ShiftInstruction(EnumInstruction.vsli, EnumRegisterType.DOUBLE, true), AllI.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vsli, EnumRegisterType.QUAD, true), AllI.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vsli, EnumRegisterType.DOUBLE, true), AllI.p(), Space.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vsli, EnumRegisterType.QUAD, true), AllI.p(), Space.p(), Q.p(), Comma.p(), Imm1toSize.p());
			
			//vsri
			append(new ShiftInstruction(EnumInstruction.vsri, EnumRegisterType.DOUBLE, true), AllI.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vsri, EnumRegisterType.QUAD, true), AllI.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vsri, EnumRegisterType.DOUBLE, true), AllI.p(), Space.p(), D.p(), Comma.p(), Imm1toSize.p());
			append(new ShiftInstruction(EnumInstruction.vsri, EnumRegisterType.QUAD, true), AllI.p(), Space.p(), Q.p(), Comma.p(), Imm1toSize.p());
			
			//VQSHL{u}
			append(new ShiftInstruction(EnumInstruction.vqshl, EnumRegisterType.QUAD, false), AllS.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ShiftInstruction(EnumInstruction.vqshl, EnumRegisterType.QUAD, false), AllS.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ShiftInstruction(EnumInstruction.vqshl, EnumRegisterType.QUAD, false), AllU.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ShiftInstruction(EnumInstruction.vqshl, EnumRegisterType.QUAD, false), AllU.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ShiftInstruction(EnumInstruction.vqshl, EnumRegisterType.DOUBLE, false), AllS.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ShiftInstruction(EnumInstruction.vqshl, EnumRegisterType.DOUBLE, false), AllS.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new ShiftInstruction(EnumInstruction.vqshl, EnumRegisterType.DOUBLE, false), AllU.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ShiftInstruction(EnumInstruction.vqshl, EnumRegisterType.DOUBLE, false), AllU.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			append(new ShiftInstruction(EnumInstruction.vqshl, EnumRegisterType.DOUBLE, true), AllS.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), Imm0toSizeMinus1.p());
			append(new ShiftInstruction(EnumInstruction.vqshl, EnumRegisterType.QUAD, true), AllS.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Imm0toSizeMinus1.p());
			append(new ShiftInstruction(EnumInstruction.vqshl, EnumRegisterType.DOUBLE, true), AllS.p(), Space.p(), D.p(), Comma.p(), Imm0toSizeMinus1.p());
			append(new ShiftInstruction(EnumInstruction.vqshl, EnumRegisterType.QUAD, true), AllS.p(), Space.p(), Q.p(), Comma.p(), Imm0toSizeMinus1.p());
			append(new ShiftInstruction(EnumInstruction.vqshl, EnumRegisterType.DOUBLE, true), AllU.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), Imm0toSizeMinus1.p());
			append(new ShiftInstruction(EnumInstruction.vqshl, EnumRegisterType.QUAD, true), AllU.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Imm0toSizeMinus1.p());
			append(new ShiftInstruction(EnumInstruction.vqshl, EnumRegisterType.DOUBLE, true), AllU.p(), Space.p(), D.p(), Comma.p(), Imm0toSizeMinus1.p());
			append(new ShiftInstruction(EnumInstruction.vqshl, EnumRegisterType.QUAD, true), AllU.p(), Space.p(), Q.p(), Comma.p(), Imm0toSizeMinus1.p());
			
			append(new ShiftInstruction(EnumInstruction.vqshlu, EnumRegisterType.DOUBLE, true), AllS.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), Imm0toSizeMinus1.p());
			append(new ShiftInstruction(EnumInstruction.vqshlu, EnumRegisterType.QUAD, true), AllS.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Imm0toSizeMinus1.p());
			append(new ShiftInstruction(EnumInstruction.vqshlu, EnumRegisterType.DOUBLE, true), AllS.p(), Space.p(), D.p(), Comma.p(), Imm0toSizeMinus1.p());
			append(new ShiftInstruction(EnumInstruction.vqshlu, EnumRegisterType.QUAD, true), AllS.p(), Space.p(), Q.p(), Comma.p(), Imm0toSizeMinus1.p());
			
			
			//vqrshr{U}n
			append(new ShiftInstruction(EnumInstruction.vqrshrn, EnumRegisterType.QUAD, true), S16.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vqrshrn, EnumRegisterType.QUAD, true), S32.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vqrshrn, EnumRegisterType.QUAD, true), S64.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vqrshrn, EnumRegisterType.QUAD, true), U16.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vqrshrn, EnumRegisterType.QUAD, true), U32.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vqrshrn, EnumRegisterType.QUAD, true), U64.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			
			append(new ShiftInstruction(EnumInstruction.vqrshrun, EnumRegisterType.QUAD, true), S16.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vqrshrun, EnumRegisterType.QUAD, true), S32.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vqrshrun, EnumRegisterType.QUAD, true), S64.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());

			//vqshr{u}n
			append(new ShiftInstruction(EnumInstruction.vqshrn, EnumRegisterType.QUAD, true), S16.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vqshrn, EnumRegisterType.QUAD, true), S32.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vqshrn, EnumRegisterType.QUAD, true), S64.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vqshrn, EnumRegisterType.QUAD, true), U16.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vqshrn, EnumRegisterType.QUAD, true), U32.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vqshrn, EnumRegisterType.QUAD, true), U64.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			
			append(new ShiftInstruction(EnumInstruction.vqshrun, EnumRegisterType.QUAD, true), S16.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vqshrun, EnumRegisterType.QUAD, true), S32.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			append(new ShiftInstruction(EnumInstruction.vqshrun, EnumRegisterType.QUAD, true), S64.p(), Space.p(), D.p(), Comma.p(), Q.p(), Comma.p(), Imm1toHalfSize.p());
			
			//Comparison instructions
			//VACGE, VACGT, VACLE,VACLT
			append(new ComparisonInstruction(EnumInstruction.vacge, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vacge, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vacge, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ComparisonInstruction(EnumInstruction.vacge, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			
			append(new ComparisonInstruction(EnumInstruction.vacgt, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vacgt, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vacgt, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ComparisonInstruction(EnumInstruction.vacgt, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			
			append(new ComparisonInstruction(EnumInstruction.vacle, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vacle, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vacle, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ComparisonInstruction(EnumInstruction.vacle, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			
			append(new ComparisonInstruction(EnumInstruction.vaclt, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vaclt, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vaclt, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ComparisonInstruction(EnumInstruction.vaclt, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			
			//vceq reg
			append(new ComparisonInstruction(EnumInstruction.vceq, EnumRegisterType.DOUBLE), AllISingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vceq, EnumRegisterType.DOUBLE), AllISingle.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vceq, EnumRegisterType.QUAD), AllISingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ComparisonInstruction(EnumInstruction.vceq, EnumRegisterType.QUAD), AllISingle.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			
			append(new ComparisonInstruction(EnumInstruction.vceq, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vceq, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vceq, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ComparisonInstruction(EnumInstruction.vceq, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			
			//vceq imm
			append(new ComparisonInstruction(EnumInstruction.vceq, EnumRegisterType.DOUBLE, true), AllISingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vceq, EnumRegisterType.DOUBLE, true), AllISingle.p(), Space.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vceq, EnumRegisterType.QUAD, true), AllISingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vceq, EnumRegisterType.QUAD, true), AllISingle.p(), Space.p(), Q.p(), Comma.p(), ImmIsZero.p());
			
			append(new ComparisonInstruction(EnumInstruction.vceq, EnumRegisterType.DOUBLE, true), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vceq, EnumRegisterType.DOUBLE, true), F32.p(), Space.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vceq, EnumRegisterType.QUAD, true), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vceq, EnumRegisterType.QUAD, true), F32.p(), Space.p(), Q.p(), Comma.p(), ImmIsZero.p());
			
			//vcge reg
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			
			//vcge imm
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.DOUBLE, true), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.DOUBLE, true), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.QUAD, true), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.QUAD, true), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), ImmIsZero.p());
			
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.DOUBLE, true), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.DOUBLE, true), F32.p(), Space.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.QUAD, true), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vcge, EnumRegisterType.QUAD, true), F32.p(), Space.p(), Q.p(), Comma.p(), ImmIsZero.p());
			
			//vcgt reg
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			
			//vcgt imm
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.DOUBLE, true), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.DOUBLE, true), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.QUAD, true), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.QUAD, true), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), ImmIsZero.p());
			
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.DOUBLE, true), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.DOUBLE, true), F32.p(), Space.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.QUAD, true), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vcgt, EnumRegisterType.QUAD, true), F32.p(), Space.p(), Q.p(), Comma.p(), ImmIsZero.p());
			
			//vcle imm
			append(new ComparisonInstruction(EnumInstruction.vcle, EnumRegisterType.DOUBLE, true), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vcle, EnumRegisterType.DOUBLE, true), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vcle, EnumRegisterType.QUAD, true), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vcle, EnumRegisterType.QUAD, true), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), ImmIsZero.p());
			
			append(new ComparisonInstruction(EnumInstruction.vcle, EnumRegisterType.DOUBLE, true), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vcle, EnumRegisterType.DOUBLE, true), F32.p(), Space.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vcle, EnumRegisterType.QUAD, true), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vcle, EnumRegisterType.QUAD, true), F32.p(), Space.p(), Q.p(), Comma.p(), ImmIsZero.p());
			
			//vclt imm
			append(new ComparisonInstruction(EnumInstruction.vclt, EnumRegisterType.DOUBLE, true), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vclt, EnumRegisterType.DOUBLE, true), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vclt, EnumRegisterType.QUAD, true), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vclt, EnumRegisterType.QUAD, true), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), ImmIsZero.p());
			
			append(new ComparisonInstruction(EnumInstruction.vclt, EnumRegisterType.DOUBLE, true), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vclt, EnumRegisterType.DOUBLE, true), F32.p(), Space.p(), D.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vclt, EnumRegisterType.QUAD, true), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), ImmIsZero.p());
			append(new ComparisonInstruction(EnumInstruction.vclt, EnumRegisterType.QUAD, true), F32.p(), Space.p(), Q.p(), Comma.p(), ImmIsZero.p());
			
			//vtst reg
			append(new ComparisonInstruction(EnumInstruction.vtst, EnumRegisterType.DOUBLE), AllISingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vtst, EnumRegisterType.DOUBLE), AllISingle.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ComparisonInstruction(EnumInstruction.vtst, EnumRegisterType.QUAD), AllISingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ComparisonInstruction(EnumInstruction.vtst, EnumRegisterType.QUAD), AllISingle.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			
			
		}
		
		private static void fillInstructions01(){

			// Multiply instructions
			//VMLA
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, false), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, false), AllSignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, false), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, false), AllSignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, false), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, false), AllUnsignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, false), AllUnsignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, false), AllUnsignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
						
			
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, false), AllISingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, false), AllISingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, false), AllISingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, false), AllISingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, false), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, false), F32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, false), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, false), F32.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, true), S16.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, true), S32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, true), S16.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, true), S32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, true), S16.p(), Space.p(), Q2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, true), S32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, true), S16.p(), Space.p(), D2in1.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, true), S32.p(), Space.p(), D2in1.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());		

			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, true), U16.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, true), U32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, true), U16.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, true), U32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, true), U16.p(), Space.p(), Q2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, true), U32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, true), U16.p(), Space.p(), D2in1.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, true), U32.p(), Space.p(), D2in1.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, true), I16.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, true), I32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, true), I16.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, true), I32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, true), I16.p(), Space.p(), Q2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, true), I32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, true), I16.p(), Space.p(), D2in1.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, true), I32.p(), Space.p(), D2in1.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, true), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, true), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.QUAD, true), F32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmla, EnumRegisterType.DOUBLE, true), F32.p(), Space.p(), D2in1.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
						
			
			//VMLAL
			append(new MultiplyInstruction(EnumInstruction.vmlal, EnumRegisterType.QUAD, false), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmlal, EnumRegisterType.QUAD, false), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmlal, EnumRegisterType.QUAD, false), AllISingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			
			append(new MultiplyInstruction(EnumInstruction.vmlal, EnumRegisterType.QUAD, true), S16.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmlal, EnumRegisterType.QUAD, true), S32.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			
			append(new MultiplyInstruction(EnumInstruction.vmlal, EnumRegisterType.QUAD, true), U16.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmlal, EnumRegisterType.QUAD, true), U32.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			
			append(new MultiplyInstruction(EnumInstruction.vmlal, EnumRegisterType.QUAD, true), I16.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmlal, EnumRegisterType.QUAD, true), I32.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			
			append(new MultiplyInstruction(EnumInstruction.vmlal, EnumRegisterType.QUAD, true), F32.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			
			
			// VMLS
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, false), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, false), AllSignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, false), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, false), AllSignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, false), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, false), AllUnsignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, false), AllUnsignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, false), AllUnsignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, false), AllISingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, false), AllISingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, false), AllISingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, false), AllISingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, false), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, false), F32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, false), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, false), F32.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, true), S16.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, true), S32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, true), S16.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, true), S32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, true), S16.p(), Space.p(), Q2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, true), S32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, true), S16.p(), Space.p(), D2in1.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, true), S32.p(), Space.p(), D2in1.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());		

			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, true), U16.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, true), U32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, true), U16.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, true), U32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, true), U16.p(), Space.p(), Q2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, true), U32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, true), U16.p(), Space.p(), D2in1.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, true), U32.p(), Space.p(), D2in1.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, true), I16.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, true), I32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, true), I16.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, true), I32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, true), I16.p(), Space.p(), Q2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, true), I32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, true), I16.p(), Space.p(), D2in1.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, true), I32.p(), Space.p(), D2in1.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, true), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, true), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.QUAD, true), F32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmls, EnumRegisterType.DOUBLE, true), F32.p(), Space.p(), D2in1.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			
			//VMLSL
			append(new MultiplyInstruction(EnumInstruction.vmlsl, EnumRegisterType.QUAD, false), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmlsl, EnumRegisterType.QUAD, false), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmlsl, EnumRegisterType.QUAD, false), AllISingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			
			append(new MultiplyInstruction(EnumInstruction.vmlsl, EnumRegisterType.QUAD, true), S16.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmlsl, EnumRegisterType.QUAD, true), S32.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			
			append(new MultiplyInstruction(EnumInstruction.vmlsl, EnumRegisterType.QUAD, true), U16.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmlsl, EnumRegisterType.QUAD, true), U32.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			
			append(new MultiplyInstruction(EnumInstruction.vmlsl, EnumRegisterType.QUAD, true), I16.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmlsl, EnumRegisterType.QUAD, true), I32.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			
			append(new MultiplyInstruction(EnumInstruction.vmlsl, EnumRegisterType.QUAD, true), F32.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			
			// VMUL
			//quad
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, false), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, false), AllSignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, false), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, false), AllUnsignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, false), AllISingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, false), AllISingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, false), AllP.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, false), AllP.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, false), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, false), F32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), I16.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), I16.p(), Space.p(), Q2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), I32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), I32.p(), Space.p(), Q2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), S16.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), S16.p(), Space.p(), Q2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), S32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), S32.p(), Space.p(), Q2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), U16.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), U16.p(), Space.p(), Q2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), U32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), U32.p(), Space.p(), Q2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), F32.p(), Space.p(), Q2in1.p(), Comma.p(), DsubRegForScalar.p());
			
			//double
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, false), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, false), AllSignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, false), AllUnsignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, false), AllUnsignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, false), AllISingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, false), AllISingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, false), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, false), F32.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), I16.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), I16.p(), Space.p(), D2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), I32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), I32.p(), Space.p(), D2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), S16.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), S16.p(), Space.p(), D2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), S32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), S32.p(), Space.p(), D2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), U16.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), U16.p(), Space.p(), D2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), U32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), U32.p(), Space.p(), D2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmul, EnumRegisterType.QUAD, true), F32.p(), Space.p(), D2in1.p(), Comma.p(), DsubRegForScalar.p());
			
			// VMULL
			append(new MultiplyInstruction(EnumInstruction.vmull, EnumRegisterType.QUAD, false), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmull, EnumRegisterType.QUAD, false), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmull, EnumRegisterType.QUAD, false), AllISingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmull, EnumRegisterType.QUAD, false), AllP.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vmull, EnumRegisterType.QUAD, true), I16.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmull, EnumRegisterType.QUAD, true), I32.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmull, EnumRegisterType.QUAD, true), S16.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmull, EnumRegisterType.QUAD, true), S32.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmull, EnumRegisterType.QUAD, true), U16.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmull, EnumRegisterType.QUAD, true), U32.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vmull, EnumRegisterType.QUAD, true), F32.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			
			// VQDMLAL
			append(new MultiplyInstruction(EnumInstruction.vqdmlal, EnumRegisterType.QUAD, false), S16.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmlal, EnumRegisterType.QUAD, false), S32.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmlal, EnumRegisterType.QUAD, true), S16.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmlal, EnumRegisterType.QUAD, true), S32.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			
			// VQDMLSL
			append(new MultiplyInstruction(EnumInstruction.vqdmlsl, EnumRegisterType.QUAD, false), S16.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmlsl, EnumRegisterType.QUAD, false), S32.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmlsl, EnumRegisterType.QUAD, true), S16.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmlsl, EnumRegisterType.QUAD, true), S32.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			
			// VQDMULH
			//quad type
			append(new MultiplyInstruction(EnumInstruction.vqdmulh, EnumRegisterType.QUAD, false), S16.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmulh, EnumRegisterType.QUAD, false), S32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmulh, EnumRegisterType.QUAD, true), S16.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmulh, EnumRegisterType.QUAD, true), S32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			
			append(new MultiplyInstruction(EnumInstruction.vqdmulh, EnumRegisterType.QUAD, false), S16.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmulh, EnumRegisterType.QUAD, false), S32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmulh, EnumRegisterType.QUAD, true), S16.p(), Space.p(), Q2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmulh, EnumRegisterType.QUAD, true), S32.p(), Space.p(), Q2in1.p(), Comma.p(), DsubRegForScalar.p());
			
			//double
			append(new MultiplyInstruction(EnumInstruction.vqdmulh, EnumRegisterType.DOUBLE, false), S16.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmulh, EnumRegisterType.DOUBLE, false), S32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmulh, EnumRegisterType.DOUBLE, true), S16.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmulh, EnumRegisterType.DOUBLE, true), S32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			
			append(new MultiplyInstruction(EnumInstruction.vqdmulh, EnumRegisterType.DOUBLE, false), S16.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmulh, EnumRegisterType.DOUBLE, false), S32.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmulh, EnumRegisterType.DOUBLE, true), S16.p(), Space.p(), D2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmulh, EnumRegisterType.DOUBLE, true), S32.p(), Space.p(), D2in1.p(), Comma.p(), DsubRegForScalar.p());
			
			// vqrdmulh
			//quad type
			append(new MultiplyInstruction(EnumInstruction.vqrdmulh, EnumRegisterType.QUAD, false), S16.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vqrdmulh, EnumRegisterType.QUAD, false), S32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vqrdmulh, EnumRegisterType.QUAD, true), S16.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vqrdmulh, EnumRegisterType.QUAD, true), S32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), DsubRegForScalar.p());
			
			append(new MultiplyInstruction(EnumInstruction.vqrdmulh, EnumRegisterType.QUAD, false), S16.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vqrdmulh, EnumRegisterType.QUAD, false), S32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MultiplyInstruction(EnumInstruction.vqrdmulh, EnumRegisterType.QUAD, true), S16.p(), Space.p(), Q2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vqrdmulh, EnumRegisterType.QUAD, true), S32.p(), Space.p(), Q2in1.p(), Comma.p(), DsubRegForScalar.p());
			
			//double
			append(new MultiplyInstruction(EnumInstruction.vqrdmulh, EnumRegisterType.DOUBLE, false), S16.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vqrdmulh, EnumRegisterType.DOUBLE, false), S32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vqrdmulh, EnumRegisterType.DOUBLE, true), S16.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vqrdmulh, EnumRegisterType.DOUBLE, true), S32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			
			append(new MultiplyInstruction(EnumInstruction.vqrdmulh, EnumRegisterType.DOUBLE, false), S16.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vqrdmulh, EnumRegisterType.DOUBLE, false), S32.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vqrdmulh, EnumRegisterType.DOUBLE, true), S16.p(), Space.p(), D2in1.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vqrdmulh, EnumRegisterType.DOUBLE, true), S32.p(), Space.p(), D2in1.p(), Comma.p(), DsubRegForScalar.p());
			
			// VQDMULL
			append(new MultiplyInstruction(EnumInstruction.vqdmull, EnumRegisterType.QUAD, false), S16.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmull, EnumRegisterType.QUAD, false), S32.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmull, EnumRegisterType.QUAD, true), S16.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			append(new MultiplyInstruction(EnumInstruction.vqdmull, EnumRegisterType.QUAD, true), S32.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), DsubRegForScalar.p());
			
			
			// ABSOLUTE AND NEGATE instructions
			//vaba			
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vaba, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vaba, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vaba, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vaba, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			
			//vabal
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vabal, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vabal, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			
			//vabd			
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vabd, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vabd, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vabd, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vabd, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vabd, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vabd, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			
			//vabdl
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vabdl, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vabdl, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), D.p(), Comma.p(), D.p());
			
			//vabs			
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vabs, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vabs, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p());

			append(new AbsoluteAndNegateInstruction(EnumInstruction.vabs, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vabs, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p());
			//append(new AbsoluteAndNegateInstruction(EnumInstruction.vabs, EnumRegisterType.DOUBLE), F64.p(), Space.p(), D.p(), Comma.p(), D.p());
			//append(new AbsoluteAndNegateInstruction(EnumInstruction.vabs, EnumRegisterType.QUAD), F64.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			
			//vqabs
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vqabs, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vqabs, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p());

			//vneg
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vneg, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vneg, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vneg, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vneg, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			//vqneg
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vqneg, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new AbsoluteAndNegateInstruction(EnumInstruction.vqneg, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			//CONVERSION INSTRUCTIONS
			//vcvt : fixed < - > single
			append(new ConversationInstruction(EnumInstruction.vcvt, EnumRegisterType.QUAD, true), S32F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Imm1toSize.p());
			append(new ConversationInstruction(EnumInstruction.vcvt, EnumRegisterType.DOUBLE, true), S32F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), Imm1toSize.p());
			
			append(new ConversationInstruction(EnumInstruction.vcvt, EnumRegisterType.QUAD, true), F32S32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Imm1toSize.p());
			append(new ConversationInstruction(EnumInstruction.vcvt, EnumRegisterType.DOUBLE, true), F32S32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), Imm1toSize.p());
			
			append(new ConversationInstruction(EnumInstruction.vcvt, EnumRegisterType.QUAD, true), U32F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Imm1toSize.p());
			append(new ConversationInstruction(EnumInstruction.vcvt, EnumRegisterType.DOUBLE, true), U32F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), Imm1toSize.p());
			
			append(new ConversationInstruction(EnumInstruction.vcvt, EnumRegisterType.QUAD, true), F32U32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Imm1toSize.p());
			append(new ConversationInstruction(EnumInstruction.vcvt, EnumRegisterType.DOUBLE, true), F32U32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), Imm1toSize.p());
			
			// vcvt: single < - > integer
			append(new ConversationInstruction(EnumInstruction.vcvt, EnumRegisterType.QUAD, false), S32F32.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ConversationInstruction(EnumInstruction.vcvt, EnumRegisterType.DOUBLE, false), S32F32.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			append(new ConversationInstruction(EnumInstruction.vcvt, EnumRegisterType.QUAD, false), F32S32.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ConversationInstruction(EnumInstruction.vcvt, EnumRegisterType.DOUBLE, false), F32S32.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			append(new ConversationInstruction(EnumInstruction.vcvt, EnumRegisterType.QUAD, false), U32F32.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ConversationInstruction(EnumInstruction.vcvt, EnumRegisterType.DOUBLE, false), U32F32.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			append(new ConversationInstruction(EnumInstruction.vcvt, EnumRegisterType.QUAD, false), F32U32.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ConversationInstruction(EnumInstruction.vcvt, EnumRegisterType.DOUBLE, false), F32U32.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			//vcvt: half < - > single precision
			append(new ConversationInstruction(EnumInstruction.vcvt, EnumRegisterType.QUAD, false), F32F16.p(), Space.p(), Q.p(), Comma.p(), D.p());
			append(new ConversationInstruction(EnumInstruction.vcvt, EnumRegisterType.DOUBLE, false), F16F32.p(), Space.p(), D.p(), Comma.p(), Q.p());
			
			//COUNT INSTRUCTIONS
			//vcls
			append(new CountInstruction(EnumInstruction.vcls, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new CountInstruction(EnumInstruction.vcls, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			//vclz
			append(new CountInstruction(EnumInstruction.vclz, EnumRegisterType.QUAD), AllISingle.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new CountInstruction(EnumInstruction.vclz, EnumRegisterType.DOUBLE), AllISingle.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			//vcnt
			append(new CountInstruction(EnumInstruction.vcnt, EnumRegisterType.QUAD), _8.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new CountInstruction(EnumInstruction.vcnt, EnumRegisterType.DOUBLE), _8.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			//ZIP INSTRUCTIONS
			//vzip
			append(new ZipInstruction(EnumInstruction.vzip, EnumRegisterType.QUAD), AllSingleWide.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ZipInstruction(EnumInstruction.vzip, EnumRegisterType.DOUBLE), AllSingleWide.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			//vuzip
			append(new ZipInstruction(EnumInstruction.vuzp, EnumRegisterType.QUAD), AllSingleWide.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ZipInstruction(EnumInstruction.vuzp, EnumRegisterType.DOUBLE), AllSingleWide.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			//VECTOR TRANSPOSE
			//vtrn
			append(new VtrnInstruction(EnumRegisterType.QUAD), AllSingleWide.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new VtrnInstruction(EnumRegisterType.DOUBLE), AllSingleWide.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			//TABLE INSTRUCTION
			//vtbl
			append(new TableInstruction(EnumInstruction.vtbl, EnumRegisterType.DOUBLE, 1), _8.p(), Space.p(), D.p(), Comma.p(), ListSubIndex.p(1, 1, false, false), Comma.p(), D.p());
			append(new TableInstruction(EnumInstruction.vtbl, EnumRegisterType.DOUBLE, 2), _8.p(), Space.p(), D.p(), Comma.p(), ListSubIndex.p(2, 1, false, false), Comma.p(), D.p());
			append(new TableInstruction(EnumInstruction.vtbl, EnumRegisterType.DOUBLE, 3), _8.p(), Space.p(), D.p(), Comma.p(), ListSubIndex.p(3, 1, false, false), Comma.p(), D.p());
			append(new TableInstruction(EnumInstruction.vtbl, EnumRegisterType.DOUBLE, 4), _8.p(), Space.p(), D.p(), Comma.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), D.p());
			
			//vtbx
			append(new TableInstruction(EnumInstruction.vtbx, EnumRegisterType.DOUBLE, 1), _8.p(), Space.p(), D.p(), Comma.p(), ListSubIndex.p(1, 1, false, false), Comma.p(), D.p());
			append(new TableInstruction(EnumInstruction.vtbx, EnumRegisterType.DOUBLE, 2), _8.p(), Space.p(), D.p(), Comma.p(), ListSubIndex.p(2, 1, false, false), Comma.p(), D.p());
			append(new TableInstruction(EnumInstruction.vtbx, EnumRegisterType.DOUBLE, 3), _8.p(), Space.p(), D.p(), Comma.p(), ListSubIndex.p(3, 1, false, false), Comma.p(), D.p());
			append(new TableInstruction(EnumInstruction.vtbx, EnumRegisterType.DOUBLE, 4), _8.p(), Space.p(), D.p(), Comma.p(), ListSubIndex.p(4, 1, false, false), Comma.p(), D.p());
			
			//SWAP INSTRUCTION
			//vswp
			append(new VswpInstruction(EnumRegisterType.QUAD), All.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new VswpInstruction(EnumRegisterType.DOUBLE), All.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new VswpInstruction(EnumRegisterType.QUAD), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new VswpInstruction(EnumRegisterType.DOUBLE), Space.p(), D.p(), Comma.p(), D.p());
			
			//REVERSE INSTRUCTION
			//vrev32
			append(new ReverseInstruction(EnumInstruction.vrev16, EnumRegisterType.QUAD), _8.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ReverseInstruction(EnumInstruction.vrev16, EnumRegisterType.DOUBLE), _8.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			append(new ReverseInstruction(EnumInstruction.vrev32, EnumRegisterType.QUAD), _8.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ReverseInstruction(EnumInstruction.vrev32, EnumRegisterType.DOUBLE), _8.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ReverseInstruction(EnumInstruction.vrev32, EnumRegisterType.QUAD), _16.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ReverseInstruction(EnumInstruction.vrev32, EnumRegisterType.DOUBLE), _16.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			append(new ReverseInstruction(EnumInstruction.vrev64, EnumRegisterType.QUAD), _8.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ReverseInstruction(EnumInstruction.vrev64, EnumRegisterType.DOUBLE), _8.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ReverseInstruction(EnumInstruction.vrev64, EnumRegisterType.QUAD), _16.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ReverseInstruction(EnumInstruction.vrev64, EnumRegisterType.DOUBLE), _16.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ReverseInstruction(EnumInstruction.vrev64, EnumRegisterType.QUAD), _32.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ReverseInstruction(EnumInstruction.vrev64, EnumRegisterType.DOUBLE), _32.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			//MINIMUM AND MAXIMUM INSTRUCTION
			//vmin
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmin, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmin, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmin, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmin, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmin, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmin, EnumRegisterType.QUAD), F32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmin, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmin, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmin, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmin, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmin, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmin, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vmax
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmax, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmax, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmax, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmax, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmax, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmax, EnumRegisterType.QUAD), F32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmax, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmax, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmax, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmax, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmax, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vmax, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vpmin
			append(new MinimumAndMaximumInstruction(EnumInstruction.vpmin, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vpmin, EnumRegisterType.QUAD), AllUnsignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vpmin, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vpmin, EnumRegisterType.QUAD), AllSignedSingle.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vpmin, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vpmin, EnumRegisterType.QUAD), F32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			
			append(new MinimumAndMaximumInstruction(EnumInstruction.vpmin, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vpmin, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vpmin, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vpmin, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vpmin, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vpmin, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vpmax
			// there is NO QUAD version!
			append(new MinimumAndMaximumInstruction(EnumInstruction.vpmax, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vpmax, EnumRegisterType.DOUBLE), AllUnsignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vpmax, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vpmax, EnumRegisterType.DOUBLE), AllSignedSingle.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vpmax, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new MinimumAndMaximumInstruction(EnumInstruction.vpmax, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//RECIPROCAL ESTIMATE
			append(new ReciprocalSqrtReciprocalEstimate(EnumInstruction.vrecpe, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ReciprocalSqrtReciprocalEstimate(EnumInstruction.vrecpe, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ReciprocalSqrtReciprocalEstimate(EnumInstruction.vrecpe, EnumRegisterType.QUAD), U32.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ReciprocalSqrtReciprocalEstimate(EnumInstruction.vrecpe, EnumRegisterType.DOUBLE), U32.p(), Space.p(), D.p(), Comma.p(), D.p());
			
			//vrsqrte
			append(new ReciprocalSqrtReciprocalEstimate(EnumInstruction.vrsqrte, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ReciprocalSqrtReciprocalEstimate(EnumInstruction.vrsqrte, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p());
			append(new ReciprocalSqrtReciprocalEstimate(EnumInstruction.vrsqrte, EnumRegisterType.QUAD), U32.p(), Space.p(), Q.p(), Comma.p(), Q.p());
			append(new ReciprocalSqrtReciprocalEstimate(EnumInstruction.vrsqrte, EnumRegisterType.DOUBLE), U32.p(), Space.p(), D.p(), Comma.p(), D.p());
			//FIXME: add testcases
			
			//RECIPROCAL (SQUARE ROOT) STEP
			//vrsqrts
			append(new ReciprocalSqrtReciprocalStep(EnumInstruction.vrsqrts, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ReciprocalSqrtReciprocalStep(EnumInstruction.vrsqrts, EnumRegisterType.QUAD), F32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ReciprocalSqrtReciprocalStep(EnumInstruction.vrsqrts, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ReciprocalSqrtReciprocalStep(EnumInstruction.vrsqrts, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//vrecps
			append(new ReciprocalSqrtReciprocalStep(EnumInstruction.vrecps, EnumRegisterType.QUAD), F32.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p());
			append(new ReciprocalSqrtReciprocalStep(EnumInstruction.vrecps, EnumRegisterType.QUAD), F32.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p());
			append(new ReciprocalSqrtReciprocalStep(EnumInstruction.vrecps, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p());
			append(new ReciprocalSqrtReciprocalStep(EnumInstruction.vrecps, EnumRegisterType.DOUBLE), F32.p(), Space.p(), D2in1.p(), Comma.p(), D.p());
			
			//EXTRACT
			//vext
			append(new VextInstruction(EnumInstruction.vext, EnumRegisterType.QUAD), All.p(), Space.p(), Q.p(), Comma.p(), Q.p(), Comma.p(), Q.p() , Comma.p(), ImmTimesDtDividedBy8IsMax15.p());
			append(new VextInstruction(EnumInstruction.vext, EnumRegisterType.DOUBLE), All.p(), Space.p(), D.p(), Comma.p(), D.p(), Comma.p(), D.p() , Comma.p(), ImmTimesDtDividedBy8IsMax7.p());
			append(new VextInstruction(EnumInstruction.vext, EnumRegisterType.QUAD), All.p(), Space.p(), Q2in1.p(), Comma.p(), Q.p(), Comma.p(), ImmTimesDtDividedBy8IsMax15.p());
			append(new VextInstruction(EnumInstruction.vext, EnumRegisterType.DOUBLE), All.p(), Space.p(), D2in1.p(), Comma.p(), D.p(), Comma.p(), ImmTimesDtDividedBy8IsMax7.p());
			
			//VMRS and VMSR
			append(new MoveFPSCAndRAPSR(EnumInstruction.vmrs), Space.p(), R.p(), Comma.p(), FPSCR.p());
			append(new MoveFPSCAndRAPSR(EnumInstruction.vmsr), Space.p(), FPSCR.p(), Comma.p(), R.p());
		}
		
		private static void append(Instruction create, Token... tokens) {
			EnumInstruction instruction = create.getInstructionName();
			List<InstructionForm> list = instructionList.get(instruction);
			if (list == null) {
				list = new ArrayList<InstructionForm>();
				instructionList.put(instruction, list);
			}

			Token[] formatsWithEnd = new Token[tokens.length + 1];
			for (int i = 0; i < tokens.length; i++) {
				formatsWithEnd[i] = tokens[i];
			}
			formatsWithEnd[tokens.length] = End.p();

			list.add(new InstructionForm(create, formatsWithEnd));
		}

		/***
		 * Returns a list of InstuctionForms for the given insturction name.
		 * 
		 * @param instruction
		 * @return List of instruction formats for the given instruction.
		 */
		public static List<InstructionForm> get(EnumInstruction instruction) {
			return instructionList.get(instruction);
		}
}
