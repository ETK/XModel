/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * XPathParserConstants.java
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

public interface XPathParserConstants {

  int EOF = 0;
  int LITERAL = 2;
  int _DIGIT_ = 3;
  int NUMBER = 4;
  int ABBREVIATED_ATTRIBUTE_AXIS = 5;
  int SLASH = 6;
  int EQ = 7;
  int LT = 8;
  int GT = 9;
  int PLUS = 10;
  int STAR = 11;
  int MINUS = 12;
  int UNION = 13;
  int SLASHSLASH = 14;
  int NEQ = 15;
  int LE = 16;
  int GE = 17;
  int ASSIGN = 18;
  int DOTDOT = 19;
  int OR = 20;
  int IN = 21;
  int IF = 22;
  int FOR = 23;
  int LET = 24;
  int AND = 25;
  int DIV = 26;
  int MOD = 27;
  int THEN = 28;
  int ELSE = 29;
  int AXIS_SELF = 30;
  int NT_TEXT = 31;
  int NT_NODE = 32;
  int AXIS_CHILD = 33;
  int RETURN = 34;
  int AXIS_NESTED = 35;
  int AXIS_PARENT = 36;
  int AXIS_ANCESTOR = 37;
  int NT_COMMENT = 38;
  int AXIS_FOLLOWING = 39;
  int AXIS_PRECEDING = 40;
  int AXIS_ATTRIBUTE = 41;
  int AXIS_NAMESPACE = 42;
  int AXIS_DESCENDANT = 43;
  int AXIS_NESTED_OR_SELF = 44;
  int AXIS_ANCESTOR_OR_SELF = 45;
  int AXIS_FOLLOWING_SIBLING = 46;
  int AXIS_PRECEDING_SIBLING = 47;
  int AXIS_DESCENDANT_OR_SELF = 48;
  int NT_PI = 49;
  int NCName = 50;
  int NCNameChar = 51;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "<token of kind 1>",
    "<LITERAL>",
    "<_DIGIT_>",
    "<NUMBER>",
    "\"@\"",
    "\"/\"",
    "\"=\"",
    "\"<\"",
    "\">\"",
    "\"+\"",
    "\"*\"",
    "\"-\"",
    "\"|\"",
    "\"//\"",
    "\"!=\"",
    "\"<=\"",
    "\">=\"",
    "\":=\"",
    "\"::\"",
    "\"or\"",
    "\"in\"",
    "\"if\"",
    "\"for\"",
    "\"let\"",
    "\"and\"",
    "\"div\"",
    "\"mod\"",
    "\"then\"",
    "\"else\"",
    "\"self\"",
    "\"text()\"",
    "\"node()\"",
    "\"child\"",
    "\"return\"",
    "\"nested\"",
    "\"parent\"",
    "\"ancestor\"",
    "\"comment()\"",
    "\"following\"",
    "\"preceding\"",
    "\"attribute\"",
    "\"namespace\"",
    "\"descendant\"",
    "\"nested-or-self\"",
    "\"ancestor-or-self\"",
    "\"following-sibling\"",
    "\"preceding-sibling\"",
    "\"descendant-or-self\"",
    "<NT_PI>",
    "<NCName>",
    "<NCNameChar>",
    "\";\"",
    "\"$\"",
    "\".\"",
    "\"..\"",
    "\"(\"",
    "\")\"",
    "\":\"",
    "\",\"",
    "\"[\"",
    "\"]\"",
  };

}
