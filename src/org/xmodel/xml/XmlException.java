/*
 * XModel (XML Application Data Modeling Framework)
 * Author: Bob Dunnagan (bdunnagan@nc.rr.com)
 * Copyright Bob Dunnagan 2009. All rights reserved.
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
