/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * Precedences.java
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
package org.xmodel.xpath.variable;

/**
 * The precedences for all variable scopes defined by the xmodel framework.
 */
public interface Precedences
{
  public final static int globalScope = 0;
  public final static int localScope = 1;
  public final static int contextScope = 2;
  public final static int forScope = 3;
  public final static int userScope1 = 4;
  public final static int userScope2 = 5;
}
