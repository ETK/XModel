/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * XmlException.java
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
package org.xmodel.xml;

/**
 * An exception generated when parsing XML documents.
 */
@SuppressWarnings("serial")
public class XmlException extends Exception
{
  /**
   * Create an exception with the specified message.
   * @param message The message.
   */
  public XmlException( String message)
  {
    super( message);
  }
  
  /**
   * Create an exception with the specified message and cause.
   * @param message The message.
   * @param cause The cause.
   */
  public XmlException( String message, Throwable cause)
  {
    super( message);
    initCause( cause);
  }
}
