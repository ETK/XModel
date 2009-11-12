/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * NullContext.java
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
package org.xmodel.xpath.expression;

import org.xmodel.NullObject;

/**
 * An empty context.
 */
public class NullContext extends Context
{
  protected NullContext()
  {
    super( new NullObject());
  }
  
  public static NullContext getInstance()
  {
    NullContext instance = instances.get();
    if ( instance == null)
    {
      instance = new NullContext();
      instances.set( instance);
    }
    return instance;
  }
  
  private static ThreadLocal<NullContext> instances = new ThreadLocal<NullContext>();
}
