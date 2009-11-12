/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * XPathParserTokenManager.java
 * 
 * Copyright 2009 Robert Arvin Dunnagan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xmodel.xpath.parser.generated;
import java.util.List;
import java.util.ArrayList;
import org.xmodel.*;
import org.xmodel.xpath.*;
import org.xmodel.xpath.expression.*;
import org.xmodel.xpath.function.*;
import org.xmodel.xpath.function.custom.*;
import org.xmodel.xpath.variable.*;

@SuppressWarnings("unused")
public class XPathParserTokenManager implements XPathParserConstants
{
  public  java.io.PrintStream debugStream = System.out;
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0xc0000000000000L) != 0L)
            return 12;
         if ((active0 & 0x17eeffff00000L) != 0L)
         {
            jjmatchedKind = 50;
            return 39;
         }
         if ((active0 & 0x811000000000L) != 0L)
         {
            jjmatchedKind = 50;
            return 36;
         }
         return -1;
      case 1:
         if ((active0 & 0x810000000000L) != 0L)
         {
            jjmatchedKind = 50;
            jjmatchedPos = 1;
            return 35;
         }
         if ((active0 & 0x17effff800000L) != 0L)
         {
            jjmatchedKind = 50;
            jjmatchedPos = 1;
            return 39;
         }
         if ((active0 & 0x700000L) != 0L)
            return 39;
         return -1;
      case 2:
         if ((active0 & 0x1fffff0000000L) != 0L)
         {
            jjmatchedKind = 50;
            jjmatchedPos = 2;
            return 39;
         }
         if ((active0 & 0xf800000L) != 0L)
            return 39;
         return -1;
      case 3:
         if ((active0 & 0x1ffff80000000L) != 0L)
         {
            jjmatchedKind = 50;
            jjmatchedPos = 3;
            return 39;
         }
         if ((active0 & 0x70000000L) != 0L)
            return 39;
         return -1;
      case 4:
         if ((active0 & 0x180000000L) != 0L)
         {
            if (jjmatchedPos < 3)
            {
               jjmatchedKind = 50;
               jjmatchedPos = 3;
            }
            return -1;
         }
         if ((active0 & 0x1fffc00000000L) != 0L)
         {
            jjmatchedKind = 50;
            jjmatchedPos = 4;
            return 39;
         }
         if ((active0 & 0x200000000L) != 0L)
            return 39;
         return -1;
      case 5:
         if ((active0 & 0x1efe000000000L) != 0L)
         {
            if (jjmatchedPos != 5)
            {
               jjmatchedKind = 50;
               jjmatchedPos = 5;
            }
            return 39;
         }
         if ((active0 & 0x180000000L) != 0L)
         {
            if (jjmatchedPos < 3)
            {
               jjmatchedKind = 50;
               jjmatchedPos = 3;
            }
            return -1;
         }
         if ((active0 & 0x101c00000000L) != 0L)
            return 39;
         return -1;
      case 6:
         if ((active0 & 0x1ffe000000000L) != 0L)
         {
            jjmatchedKind = 50;
            jjmatchedPos = 6;
            return 39;
         }
         return -1;
      case 7:
         if ((active0 & 0x4000000000L) != 0L)
         {
            if (jjmatchedPos < 6)
            {
               jjmatchedKind = 50;
               jjmatchedPos = 6;
            }
            return -1;
         }
         if ((active0 & 0x1df8000000000L) != 0L)
         {
            if (jjmatchedPos != 7)
            {
               jjmatchedKind = 50;
               jjmatchedPos = 7;
            }
            return 39;
         }
         if ((active0 & 0x202000000000L) != 0L)
            return 39;
         return -1;
      case 8:
         if ((active0 & 0x4000000000L) != 0L)
         {
            if (jjmatchedPos < 6)
            {
               jjmatchedKind = 50;
               jjmatchedPos = 6;
            }
            return -1;
         }
         if ((active0 & 0xc78000000000L) != 0L)
            return 39;
         if ((active0 & 0x1380000000000L) != 0L)
         {
            if (jjmatchedPos != 8)
            {
               jjmatchedKind = 50;
               jjmatchedPos = 8;
            }
            return 39;
         }
         return -1;
      case 9:
         if ((active0 & 0xf00000000000L) != 0L)
         {
            if (jjmatchedPos != 9)
            {
               jjmatchedKind = 50;
               jjmatchedPos = 9;
            }
            return 39;
         }
         if ((active0 & 0x1080000000000L) != 0L)
            return 39;
         return -1;
      case 10:
         if ((active0 & 0x1f00000000000L) != 0L)
         {
            jjmatchedKind = 50;
            jjmatchedPos = 10;
            return 39;
         }
         return -1;
      case 11:
         if ((active0 & 0x1f00000000000L) != 0L)
         {
            jjmatchedKind = 50;
            jjmatchedPos = 11;
            return 39;
         }
         return -1;
      case 12:
         if ((active0 & 0x1f00000000000L) != 0L)
         {
            jjmatchedKind = 50;
            jjmatchedPos = 12;
            return 39;
         }
         return -1;
      case 13:
         if ((active0 & 0x1e00000000000L) != 0L)
         {
            jjmatchedKind = 50;
            jjmatchedPos = 13;
            return 39;
         }
         if ((active0 & 0x100000000000L) != 0L)
            return 39;
         return -1;
      case 14:
         if ((active0 & 0x1e00000000000L) != 0L)
         {
            jjmatchedKind = 50;
            jjmatchedPos = 14;
            return 39;
         }
         return -1;
      case 15:
         if ((active0 & 0x1c00000000000L) != 0L)
         {
            jjmatchedKind = 50;
            jjmatchedPos = 15;
            return 39;
         }
         if ((active0 & 0x200000000000L) != 0L)
            return 39;
         return -1;
      case 16:
         if ((active0 & 0xc00000000000L) != 0L)
            return 39;
         if ((active0 & 0x1000000000000L) != 0L)
         {
            jjmatchedKind = 50;
            jjmatchedPos = 16;
            return 39;
         }
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private final int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private final int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 33:
         return jjMoveStringLiteralDfa1_0(0x8000L);
      case 36:
         return jjStopAtPos(0, 53);
      case 40:
         return jjStopAtPos(0, 56);
      case 41:
         return jjStopAtPos(0, 57);
      case 42:
         return jjStopAtPos(0, 11);
      case 43:
         return jjStopAtPos(0, 10);
      case 44:
         return jjStopAtPos(0, 59);
      case 45:
         return jjStopAtPos(0, 12);
      case 46:
         jjmatchedKind = 54;
         return jjMoveStringLiteralDfa1_0(0x80000000000000L);
      case 47:
         jjmatchedKind = 6;
         return jjMoveStringLiteralDfa1_0(0x4000L);
      case 58:
         jjmatchedKind = 58;
         return jjMoveStringLiteralDfa1_0(0xc0000L);
      case 59:
         return jjStopAtPos(0, 52);
      case 60:
         jjmatchedKind = 8;
         return jjMoveStringLiteralDfa1_0(0x10000L);
      case 61:
         return jjStopAtPos(0, 7);
      case 62:
         jjmatchedKind = 9;
         return jjMoveStringLiteralDfa1_0(0x20000L);
      case 64:
         return jjStopAtPos(0, 5);
      case 91:
         return jjStopAtPos(0, 60);
      case 93:
         return jjStopAtPos(0, 61);
      case 97:
         return jjMoveStringLiteralDfa1_0(0x222002000000L);
      case 99:
         return jjMoveStringLiteralDfa1_0(0x4200000000L);
      case 100:
         return jjMoveStringLiteralDfa1_0(0x1080004000000L);
      case 101:
         return jjMoveStringLiteralDfa1_0(0x20000000L);
      case 102:
         return jjMoveStringLiteralDfa1_0(0x408000800000L);
      case 105:
         return jjMoveStringLiteralDfa1_0(0x600000L);
      case 108:
         return jjMoveStringLiteralDfa1_0(0x1000000L);
      case 109:
         return jjMoveStringLiteralDfa1_0(0x8000000L);
      case 110:
         return jjMoveStringLiteralDfa1_0(0x140900000000L);
      case 111:
         return jjMoveStringLiteralDfa1_0(0x100000L);
      case 112:
         return jjMoveStringLiteralDfa1_0(0x811000000000L);
      case 114:
         return jjMoveStringLiteralDfa1_0(0x400000000L);
      case 115:
         return jjMoveStringLiteralDfa1_0(0x40000000L);
      case 116:
         return jjMoveStringLiteralDfa1_0(0x90000000L);
      case 124:
         return jjStopAtPos(0, 13);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private final int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 46:
         if ((active0 & 0x80000000000000L) != 0L)
            return jjStopAtPos(1, 55);
         break;
      case 47:
         if ((active0 & 0x4000L) != 0L)
            return jjStopAtPos(1, 14);
         break;
      case 58:
         if ((active0 & 0x80000L) != 0L)
            return jjStopAtPos(1, 19);
         break;
      case 61:
         if ((active0 & 0x8000L) != 0L)
            return jjStopAtPos(1, 15);
         else if ((active0 & 0x10000L) != 0L)
            return jjStopAtPos(1, 16);
         else if ((active0 & 0x20000L) != 0L)
            return jjStopAtPos(1, 17);
         else if ((active0 & 0x40000L) != 0L)
            return jjStopAtPos(1, 18);
         break;
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0x41000000000L);
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0x1180cc1000000L);
      case 102:
         if ((active0 & 0x400000L) != 0L)
            return jjStartNfaWithStates_0(1, 22, 39);
         break;
      case 104:
         return jjMoveStringLiteralDfa2_0(active0, 0x210000000L);
      case 105:
         return jjMoveStringLiteralDfa2_0(active0, 0x4000000L);
      case 108:
         return jjMoveStringLiteralDfa2_0(active0, 0x20000000L);
      case 110:
         if ((active0 & 0x200000L) != 0L)
            return jjStartNfaWithStates_0(1, 21, 39);
         return jjMoveStringLiteralDfa2_0(active0, 0x202002000000L);
      case 111:
         return jjMoveStringLiteralDfa2_0(active0, 0x40c108800000L);
      case 114:
         if ((active0 & 0x100000L) != 0L)
            return jjStartNfaWithStates_0(1, 20, 39);
         return jjMoveStringLiteralDfa2_0(active0, 0x810000000000L);
      case 116:
         return jjMoveStringLiteralDfa2_0(active0, 0x20000000000L);
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
private final int jjMoveStringLiteralDfa2_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(0, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 99:
         return jjMoveStringLiteralDfa3_0(active0, 0x202000000000L);
      case 100:
         if ((active0 & 0x2000000L) != 0L)
            return jjStartNfaWithStates_0(2, 25, 39);
         else if ((active0 & 0x8000000L) != 0L)
            return jjStartNfaWithStates_0(2, 27, 39);
         return jjMoveStringLiteralDfa3_0(active0, 0x100000000L);
      case 101:
         return jjMoveStringLiteralDfa3_0(active0, 0x810010000000L);
      case 105:
         return jjMoveStringLiteralDfa3_0(active0, 0x200000000L);
      case 108:
         return jjMoveStringLiteralDfa3_0(active0, 0x408040000000L);
      case 109:
         return jjMoveStringLiteralDfa3_0(active0, 0x44000000000L);
      case 114:
         if ((active0 & 0x800000L) != 0L)
            return jjStartNfaWithStates_0(2, 23, 39);
         return jjMoveStringLiteralDfa3_0(active0, 0x1000000000L);
      case 115:
         return jjMoveStringLiteralDfa3_0(active0, 0x1180820000000L);
      case 116:
         if ((active0 & 0x1000000L) != 0L)
            return jjStartNfaWithStates_0(2, 24, 39);
         return jjMoveStringLiteralDfa3_0(active0, 0x20400000000L);
      case 118:
         if ((active0 & 0x4000000L) != 0L)
            return jjStartNfaWithStates_0(2, 26, 39);
         break;
      case 120:
         return jjMoveStringLiteralDfa3_0(active0, 0x80000000L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
private final int jjMoveStringLiteralDfa3_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 99:
         return jjMoveStringLiteralDfa4_0(active0, 0x1890000000000L);
      case 101:
         if ((active0 & 0x20000000L) != 0L)
            return jjStartNfaWithStates_0(3, 29, 39);
         return jjMoveStringLiteralDfa4_0(active0, 0x243100000000L);
      case 102:
         if ((active0 & 0x40000000L) != 0L)
            return jjStartNfaWithStates_0(3, 30, 39);
         break;
      case 108:
         return jjMoveStringLiteralDfa4_0(active0, 0x408200000000L);
      case 109:
         return jjMoveStringLiteralDfa4_0(active0, 0x4000000000L);
      case 110:
         if ((active0 & 0x10000000L) != 0L)
            return jjStartNfaWithStates_0(3, 28, 39);
         break;
      case 114:
         return jjMoveStringLiteralDfa4_0(active0, 0x20000000000L);
      case 116:
         return jjMoveStringLiteralDfa4_0(active0, 0x100880000000L);
      case 117:
         return jjMoveStringLiteralDfa4_0(active0, 0x400000000L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
private final int jjMoveStringLiteralDfa4_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 40:
         return jjMoveStringLiteralDfa5_0(active0, 0x180000000L);
      case 100:
         if ((active0 & 0x200000000L) != 0L)
            return jjStartNfaWithStates_0(4, 33, 39);
         break;
      case 101:
         return jjMoveStringLiteralDfa5_0(active0, 0x1994800000000L);
      case 105:
         return jjMoveStringLiteralDfa5_0(active0, 0x20000000000L);
      case 110:
         return jjMoveStringLiteralDfa5_0(active0, 0x1000000000L);
      case 111:
         return jjMoveStringLiteralDfa5_0(active0, 0x408000000000L);
      case 114:
         return jjMoveStringLiteralDfa5_0(active0, 0x400000000L);
      case 115:
         return jjMoveStringLiteralDfa5_0(active0, 0x242000000000L);
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
private final int jjMoveStringLiteralDfa5_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 41:
         if ((active0 & 0x80000000L) != 0L)
            return jjStopAtPos(5, 31);
         else if ((active0 & 0x100000000L) != 0L)
            return jjStopAtPos(5, 32);
         break;
      case 98:
         return jjMoveStringLiteralDfa6_0(active0, 0x20000000000L);
      case 100:
         if ((active0 & 0x800000000L) != 0L)
         {
            jjmatchedKind = 35;
            jjmatchedPos = 5;
         }
         return jjMoveStringLiteralDfa6_0(active0, 0x910000000000L);
      case 110:
         if ((active0 & 0x400000000L) != 0L)
            return jjStartNfaWithStates_0(5, 34, 39);
         return jjMoveStringLiteralDfa6_0(active0, 0x1084000000000L);
      case 112:
         return jjMoveStringLiteralDfa6_0(active0, 0x40000000000L);
      case 116:
         if ((active0 & 0x1000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 36, 39);
         return jjMoveStringLiteralDfa6_0(active0, 0x202000000000L);
      case 119:
         return jjMoveStringLiteralDfa6_0(active0, 0x408000000000L);
      default :
         break;
   }
   return jjStartNfa_0(4, active0);
}
private final int jjMoveStringLiteralDfa6_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 45:
         return jjMoveStringLiteralDfa7_0(active0, 0x100000000000L);
      case 97:
         return jjMoveStringLiteralDfa7_0(active0, 0x40000000000L);
      case 100:
         return jjMoveStringLiteralDfa7_0(active0, 0x1080000000000L);
      case 105:
         return jjMoveStringLiteralDfa7_0(active0, 0xc18000000000L);
      case 111:
         return jjMoveStringLiteralDfa7_0(active0, 0x202000000000L);
      case 116:
         return jjMoveStringLiteralDfa7_0(active0, 0x4000000000L);
      case 117:
         return jjMoveStringLiteralDfa7_0(active0, 0x20000000000L);
      default :
         break;
   }
   return jjStartNfa_0(5, active0);
}
private final int jjMoveStringLiteralDfa7_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(5, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0);
      return 7;
   }
   switch(curChar)
   {
      case 40:
         return jjMoveStringLiteralDfa8_0(active0, 0x4000000000L);
      case 97:
         return jjMoveStringLiteralDfa8_0(active0, 0x1080000000000L);
      case 99:
         return jjMoveStringLiteralDfa8_0(active0, 0x40000000000L);
      case 110:
         return jjMoveStringLiteralDfa8_0(active0, 0xc18000000000L);
      case 111:
         return jjMoveStringLiteralDfa8_0(active0, 0x100000000000L);
      case 114:
         if ((active0 & 0x2000000000L) != 0L)
         {
            jjmatchedKind = 37;
            jjmatchedPos = 7;
         }
         return jjMoveStringLiteralDfa8_0(active0, 0x200000000000L);
      case 116:
         return jjMoveStringLiteralDfa8_0(active0, 0x20000000000L);
      default :
         break;
   }
   return jjStartNfa_0(6, active0);
}
private final int jjMoveStringLiteralDfa8_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(6, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(7, active0);
      return 8;
   }
   switch(curChar)
   {
      case 41:
         if ((active0 & 0x4000000000L) != 0L)
            return jjStopAtPos(8, 38);
         break;
      case 45:
         return jjMoveStringLiteralDfa9_0(active0, 0x200000000000L);
      case 101:
         if ((active0 & 0x20000000000L) != 0L)
            return jjStartNfaWithStates_0(8, 41, 39);
         else if ((active0 & 0x40000000000L) != 0L)
            return jjStartNfaWithStates_0(8, 42, 39);
         break;
      case 103:
         if ((active0 & 0x8000000000L) != 0L)
         {
            jjmatchedKind = 39;
            jjmatchedPos = 8;
         }
         else if ((active0 & 0x10000000000L) != 0L)
         {
            jjmatchedKind = 40;
            jjmatchedPos = 8;
         }
         return jjMoveStringLiteralDfa9_0(active0, 0xc00000000000L);
      case 110:
         return jjMoveStringLiteralDfa9_0(active0, 0x1080000000000L);
      case 114:
         return jjMoveStringLiteralDfa9_0(active0, 0x100000000000L);
      default :
         break;
   }
   return jjStartNfa_0(7, active0);
}
private final int jjMoveStringLiteralDfa9_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(7, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(8, active0);
      return 9;
   }
   switch(curChar)
   {
      case 45:
         return jjMoveStringLiteralDfa10_0(active0, 0xd00000000000L);
      case 111:
         return jjMoveStringLiteralDfa10_0(active0, 0x200000000000L);
      case 116:
         if ((active0 & 0x80000000000L) != 0L)
         {
            jjmatchedKind = 43;
            jjmatchedPos = 9;
         }
         return jjMoveStringLiteralDfa10_0(active0, 0x1000000000000L);
      default :
         break;
   }
   return jjStartNfa_0(8, active0);
}
private final int jjMoveStringLiteralDfa10_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(8, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(9, active0);
      return 10;
   }
   switch(curChar)
   {
      case 45:
         return jjMoveStringLiteralDfa11_0(active0, 0x1000000000000L);
      case 114:
         return jjMoveStringLiteralDfa11_0(active0, 0x200000000000L);
      case 115:
         return jjMoveStringLiteralDfa11_0(active0, 0xd00000000000L);
      default :
         break;
   }
   return jjStartNfa_0(9, active0);
}
private final int jjMoveStringLiteralDfa11_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(9, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(10, active0);
      return 11;
   }
   switch(curChar)
   {
      case 45:
         return jjMoveStringLiteralDfa12_0(active0, 0x200000000000L);
      case 101:
         return jjMoveStringLiteralDfa12_0(active0, 0x100000000000L);
      case 105:
         return jjMoveStringLiteralDfa12_0(active0, 0xc00000000000L);
      case 111:
         return jjMoveStringLiteralDfa12_0(active0, 0x1000000000000L);
      default :
         break;
   }
   return jjStartNfa_0(10, active0);
}
private final int jjMoveStringLiteralDfa12_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(10, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(11, active0);
      return 12;
   }
   switch(curChar)
   {
      case 98:
         return jjMoveStringLiteralDfa13_0(active0, 0xc00000000000L);
      case 108:
         return jjMoveStringLiteralDfa13_0(active0, 0x100000000000L);
      case 114:
         return jjMoveStringLiteralDfa13_0(active0, 0x1000000000000L);
      case 115:
         return jjMoveStringLiteralDfa13_0(active0, 0x200000000000L);
      default :
         break;
   }
   return jjStartNfa_0(11, active0);
}
private final int jjMoveStringLiteralDfa13_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(11, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(12, active0);
      return 13;
   }
   switch(curChar)
   {
      case 45:
         return jjMoveStringLiteralDfa14_0(active0, 0x1000000000000L);
      case 101:
         return jjMoveStringLiteralDfa14_0(active0, 0x200000000000L);
      case 102:
         if ((active0 & 0x100000000000L) != 0L)
            return jjStartNfaWithStates_0(13, 44, 39);
         break;
      case 108:
         return jjMoveStringLiteralDfa14_0(active0, 0xc00000000000L);
      default :
         break;
   }
   return jjStartNfa_0(12, active0);
}
private final int jjMoveStringLiteralDfa14_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(12, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(13, active0);
      return 14;
   }
   switch(curChar)
   {
      case 105:
         return jjMoveStringLiteralDfa15_0(active0, 0xc00000000000L);
      case 108:
         return jjMoveStringLiteralDfa15_0(active0, 0x200000000000L);
      case 115:
         return jjMoveStringLiteralDfa15_0(active0, 0x1000000000000L);
      default :
         break;
   }
   return jjStartNfa_0(13, active0);
}
private final int jjMoveStringLiteralDfa15_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(13, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(14, active0);
      return 15;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa16_0(active0, 0x1000000000000L);
      case 102:
         if ((active0 & 0x200000000000L) != 0L)
            return jjStartNfaWithStates_0(15, 45, 39);
         break;
      case 110:
         return jjMoveStringLiteralDfa16_0(active0, 0xc00000000000L);
      default :
         break;
   }
   return jjStartNfa_0(14, active0);
}
private final int jjMoveStringLiteralDfa16_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(14, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(15, active0);
      return 16;
   }
   switch(curChar)
   {
      case 103:
         if ((active0 & 0x400000000000L) != 0L)
            return jjStartNfaWithStates_0(16, 46, 39);
         else if ((active0 & 0x800000000000L) != 0L)
            return jjStartNfaWithStates_0(16, 47, 39);
         break;
      case 108:
         return jjMoveStringLiteralDfa17_0(active0, 0x1000000000000L);
      default :
         break;
   }
   return jjStartNfa_0(15, active0);
}
private final int jjMoveStringLiteralDfa17_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(15, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(16, active0);
      return 17;
   }
   switch(curChar)
   {
      case 102:
         if ((active0 & 0x1000000000000L) != 0L)
            return jjStartNfaWithStates_0(17, 48, 39);
         break;
      default :
         break;
   }
   return jjStartNfa_0(16, active0);
}
private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private final int jjMoveNfa_0(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 40;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 35:
               case 39:
                  if ((0x3ff600000000000L & l) == 0L)
                     break;
                  if (kind > 50)
                     kind = 50;
                  jjCheckNAdd(39);
                  break;
               case 36:
                  if ((0x3ff600000000000L & l) == 0L)
                     break;
                  if (kind > 50)
                     kind = 50;
                  jjCheckNAdd(39);
                  break;
               case 0:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 4)
                        kind = 4;
                     jjCheckNAddTwoStates(8, 9);
                  }
                  else if ((0x2600L & l) != 0L)
                  {
                     if (kind > 1)
                        kind = 1;
                     jjCheckNAdd(1);
                  }
                  else if (curChar == 46)
                     jjCheckNAdd(12);
                  else if (curChar == 39)
                     jjCheckNAddTwoStates(6, 7);
                  else if (curChar == 34)
                     jjCheckNAddTwoStates(3, 4);
                  else if (curChar == 32)
                  {
                     if (kind > 1)
                        kind = 1;
                  }
                  break;
               case 1:
                  if ((0x2600L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAdd(1);
                  break;
               case 2:
                  if (curChar == 34)
                     jjCheckNAddTwoStates(3, 4);
                  break;
               case 3:
                  if ((0xfffffffbffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(3, 4);
                  break;
               case 4:
                  if (curChar == 34 && kind > 2)
                     kind = 2;
                  break;
               case 5:
                  if (curChar == 39)
                     jjCheckNAddTwoStates(6, 7);
                  break;
               case 6:
                  if ((0xffffff7fffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(6, 7);
                  break;
               case 7:
                  if (curChar == 39 && kind > 2)
                     kind = 2;
                  break;
               case 8:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 4)
                     kind = 4;
                  jjCheckNAddTwoStates(8, 9);
                  break;
               case 9:
                  if (curChar != 46)
                     break;
                  if (kind > 4)
                     kind = 4;
                  jjCheckNAdd(10);
                  break;
               case 10:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 4)
                     kind = 4;
                  jjCheckNAdd(10);
                  break;
               case 11:
                  if (curChar == 46)
                     jjCheckNAdd(12);
                  break;
               case 12:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 4)
                     kind = 4;
                  jjCheckNAdd(12);
                  break;
               case 14:
                  if (curChar == 40)
                     jjCheckNAddTwoStates(15, 16);
                  break;
               case 15:
                  if ((0xfffffdffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(15, 16);
                  break;
               case 16:
                  if (curChar == 41 && kind > 49)
                     kind = 49;
                  break;
               case 27:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 26;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 35:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                  {
                     if (kind > 50)
                        kind = 50;
                     jjCheckNAdd(39);
                  }
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 34;
                  break;
               case 36:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                  {
                     if (kind > 50)
                        kind = 50;
                     jjCheckNAdd(39);
                  }
                  if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 35;
                  break;
               case 0:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                  {
                     if (kind > 50)
                        kind = 50;
                     jjCheckNAdd(39);
                  }
                  if (curChar == 112)
                     jjstateSet[jjnewStateCnt++] = 36;
                  break;
               case 3:
                  jjAddStates(0, 1);
                  break;
               case 6:
                  jjAddStates(2, 3);
                  break;
               case 13:
                  if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 14;
                  break;
               case 15:
                  jjAddStates(4, 5);
                  break;
               case 17:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 13;
                  break;
               case 18:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 17;
                  break;
               case 19:
                  if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 18;
                  break;
               case 20:
                  if (curChar == 99)
                     jjstateSet[jjnewStateCnt++] = 19;
                  break;
               case 21:
                  if (curChar == 117)
                     jjstateSet[jjnewStateCnt++] = 20;
                  break;
               case 22:
                  if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 21;
                  break;
               case 23:
                  if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 22;
                  break;
               case 24:
                  if (curChar == 115)
                     jjstateSet[jjnewStateCnt++] = 23;
                  break;
               case 25:
                  if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 24;
                  break;
               case 26:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 25;
                  break;
               case 28:
                  if (curChar == 103)
                     jjstateSet[jjnewStateCnt++] = 27;
                  break;
               case 29:
                  if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 28;
                  break;
               case 30:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 29;
                  break;
               case 31:
                  if (curChar == 115)
                     jjstateSet[jjnewStateCnt++] = 30;
                  break;
               case 32:
                  if (curChar == 115)
                     jjstateSet[jjnewStateCnt++] = 31;
                  break;
               case 33:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 32;
                  break;
               case 34:
                  if (curChar == 99)
                     jjstateSet[jjnewStateCnt++] = 33;
                  break;
               case 37:
                  if (curChar == 112)
                     jjstateSet[jjnewStateCnt++] = 36;
                  break;
               case 38:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 50)
                     kind = 50;
                  jjCheckNAdd(39);
                  break;
               case 39:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 50)
                     kind = 50;
                  jjCheckNAdd(39);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 3:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(0, 1);
                  break;
               case 6:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(2, 3);
                  break;
               case 15:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(4, 5);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 40 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   3, 4, 6, 7, 15, 16, 
};
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, "\100", "\57", "\75", "\74", "\76", "\53", "\52", 
"\55", "\174", "\57\57", "\41\75", "\74\75", "\76\75", "\72\75", "\72\72", 
"\157\162", "\151\156", "\151\146", "\146\157\162", "\154\145\164", "\141\156\144", 
"\144\151\166", "\155\157\144", "\164\150\145\156", "\145\154\163\145", "\163\145\154\146", 
"\164\145\170\164\50\51", "\156\157\144\145\50\51", "\143\150\151\154\144", "\162\145\164\165\162\156", 
"\156\145\163\164\145\144", "\160\141\162\145\156\164", "\141\156\143\145\163\164\157\162", 
"\143\157\155\155\145\156\164\50\51", "\146\157\154\154\157\167\151\156\147", 
"\160\162\145\143\145\144\151\156\147", "\141\164\164\162\151\142\165\164\145", 
"\156\141\155\145\163\160\141\143\145", "\144\145\163\143\145\156\144\141\156\164", 
"\156\145\163\164\145\144\55\157\162\55\163\145\154\146", "\141\156\143\145\163\164\157\162\55\157\162\55\163\145\154\146", 
"\146\157\154\154\157\167\151\156\147\55\163\151\142\154\151\156\147", "\160\162\145\143\145\144\151\156\147\55\163\151\142\154\151\156\147", 
"\144\145\163\143\145\156\144\141\156\164\55\157\162\55\163\145\154\146", null, null, null, "\73", "\44", "\56", "\56\56", "\50", "\51", "\72", "\54", 
"\133", "\135", };
public static final String[] lexStateNames = {
   "DEFAULT", 
};
static final long[] jjtoToken = {
   0x3ff7fffffffffff5L, 
};
static final long[] jjtoSkip = {
   0x2L, 
};
protected SimpleCharStream input_stream;
private final int[] jjrounds = new int[40];
private final int[] jjstateSet = new int[80];
protected char curChar;
public XPathParserTokenManager(SimpleCharStream stream){
   if (SimpleCharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}
public XPathParserTokenManager(SimpleCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}
public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 40; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   String im = jjstrLiteralImages[jjmatchedKind];
   t.image = (im == null) ? input_stream.GetImage() : im;
   t.beginLine = input_stream.getBeginLine();
   t.beginColumn = input_stream.getBeginColumn();
   t.endLine = input_stream.getEndLine();
   t.endColumn = input_stream.getEndColumn();
   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

public Token getNextToken() 
{
  int kind;
  Token specialToken = null;
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {   
   try   
   {     
      curChar = input_stream.BeginToken();
   }     
   catch(java.io.IOException e)
   {        
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

}
