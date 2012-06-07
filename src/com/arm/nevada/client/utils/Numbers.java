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
/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
//http://code.google.com/p/quake2-gwt-port/source/browse/src/com/google/gwt/corp/compatibility/Numbers.java
//package com.google.gwt.corp.compatibility;

//import com.googlecode.gwtgl.array.Float32Array;
//import com.googlecode.gwtgl.array.Int32Array;
//import com.googlecode.gwtgl.array.Int8Array;

public class Numbers {
  
  static final double LN2 = Math.log(2);
  
  public static final int floatToIntBits(float f) {
//        wfa.set(0, f);
//        return wia.get(0);
          
    if (Float.isNaN(f)) {
    	return 0x7fc00000;
//      return 0x7f800001;
    } 
    int signBit;
    if (f == 0) {
      return (1/f == Float.NEGATIVE_INFINITY) ? 0x80000000 : 0;
    } else if (f < 0)  {
      f = -f;
      signBit = 0x80000000;
    } else {
      signBit = 0;
    }
    if (f == Float.POSITIVE_INFINITY) {
      return signBit | 0x7f800000;
    }
    
    int exponent = (int) (Math.log(f) / LN2);
    if (exponent < -126) {
      exponent = -126;
    } 
    int significand = (int) (0.5 + f * Math.exp(-(exponent - 23) * LN2));
    
    // Handle exponent rounding issues & denorm
    if ((significand & 0x01000000) != 0) {
      significand >>= 1;
      exponent++;
    } else if ((significand & 0x00800000) == 0) {
      if (exponent == -126) {
        return signBit | significand;
      } else {
        significand <<= 1;
        exponent--;
      }
    }
    
    return signBit | ((exponent + 127) << 23) | (significand & 0x007fffff);
  }
  
  /**
   * based on floatToIntBits().
   * Unfortunately it doesn't provide exact value.
   * @param f
   * @return
   */
  public static final long doubleToLongBits(double f) {
		      
		if (Double.isNaN(f)) {
			return 0x7ff8000000000000L;
		} 
		long signBit;
		if (f == 0) {
		  return (1/f == Double.NEGATIVE_INFINITY) ? 0x8000000000000000L : 0;
		} else if (f < 0)  {
		  f = -f;
		  signBit = 0x8000000000000000L;
		} else {
		  signBit = 0;
		}
		if (f == Double.POSITIVE_INFINITY) {
		  return signBit | 0x7ff0000000000000L;
		}
		
		long exponent = (long) (Math.log(f) / LN2);
		if (exponent < -1022) {
		  exponent = -1022;
		} 
		long significand = (long) (0.5d + f * Math.exp(-(exponent - 52) * LN2));
		
		// Handle exponent rounding issues & denorm
		if ((significand & 0x20000000000000l) != 0) {
		  significand >>= 1;
		  exponent++;
		} else if ((significand & 0x10000000000000L) == 0) {
		  if (exponent == -1022) {
		    return signBit | significand;
		  } else {
		    significand <<= 1;
		    exponent--;
		  }
		}
		
		return signBit | ((exponent + 1023) << 52) | (significand & 0xFFFFFFFFFFFFFL);
  }
  
//  static Int8Array wba = Int8Array.create(4);
//  static Int32Array wia = Int32Array.create(wba.getBuffer(), 0, 1);
//  static Float32Array wfa = Float32Array.create(wba.getBuffer(), 0, 1);
  
  public static final float intBitsToFloat(int i) {
//        wba.set(0, (byte) (i >> 24));
//        wba.set(1, (byte) (i >> 16));
//        wba.set(2, (byte) (i >> 8));
//        wba.set(3, (byte) (i));
//          wia.set(0, i);
//        return wfa.get(0);
        
        
    int exponent = (i >>> 23) & 255;
    int significand = i & 0x007fffff;
    float result;
    if (exponent == 0) {
      result = (float) (Math.exp((-126 - 23) * LN2) * significand);
    } else if (exponent == 255) {
      result = significand == 0 ? Float.POSITIVE_INFINITY : Float.NaN;
    } else {
      result = (float) (Math.exp((exponent - 127 - 23) * LN2) * (0x00800000 | significand));
    }
    
    return (i & 0x80000000) == 0 ? result : -result;
  }
  
  /**
   * Rewritten from intBitsToFloat().
   */
  public static final double longBitsToDouble(long i) {
    
	  long exponent = (i >>> 52) & 0x7ff;
	  long significand = i & 0xFFFFFFFFFFFFFl;
	  double result;
	if (exponent == 0) {
	  result = (double) (Math.exp((-1022 - 52) * LN2) * significand);
	} else if (exponent == 0x7ff) {
	  result = significand == 0 ? Double.POSITIVE_INFINITY : Double.NaN;
	} else {
	  result = (double) (Math.exp((exponent - 1023 - 52) * LN2) * (0x10000000000000l | significand));
	}
	
	return (i & 0x8000000000000000l) == 0 ? result : -result;
}
  
//  public static final long doubleToLongBits(Double d) {
//    throw new RuntimeException("NYI");
//  }
//
//  
//  public static final double longBitsToDouble(long l) {
//    throw new RuntimeException("NYI");
//  }
//
//
//  public static long doubleToRawLongBits(double value) {
//    throw new RuntimeException("NYI: Numbers.doubleToRawLongBits");
//  }
}
