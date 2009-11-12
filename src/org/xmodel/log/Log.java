/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * Log.java
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
package org.xmodel.log;

import org.apache.log4j.Logger;

/**
 * Convenience class for automatically setting the default logging format.
 */
public class Log
{
  /**
   * Returns the logger with the specified name.
   * @param name The name of the logger.
   * @return Returns the logger with the specified name.
   */
  public static Logger getLog( String name)
  {
    return Logger.getLogger( name);
  }
}
