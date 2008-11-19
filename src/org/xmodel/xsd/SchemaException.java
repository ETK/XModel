/*
 * XModel (XML Application Data Modeling Framework)
 * Author: Bob Dunnagan (bdunnagan@nc.rr.com)
 * Copyright Bob Dunnagan 2009. All rights reserved.
 */
package org.xmodel.xsd;

import org.xmodel.xml.XmlException;

/**
 * An exception generated when parsing XSD documents.
 */
@SuppressWarnings("serial")
public class SchemaException extends XmlException
{
  /**
   * Create an exception with the specified message.
   * @param message The message.
   */
  public SchemaException( String message)
  {
    super( message);
  }

  /**
   * Create an exception with the specified message and cause.
   * @param message The message.
   * @param cause The cause.
   */
  public SchemaException( String message, Throwable cause)
  {
    super( message, cause);
  }
}
