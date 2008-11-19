/*
 * XModel (XML Application Data Modeling Framework)
 * Author: Bob Dunnagan (bdunnagan@nc.rr.com)
 * Copyright Bob Dunnagan 2009. All rights reserved.
 */
package org.xmodel.xsd.check;

import org.xmodel.IModelObject;
import org.xmodel.Xlate;

/**
 * A string value check.
 */
public class StringCheck extends AbstractCheck
{
  public StringCheck( IModelObject schemaLocus)
  {
    super( schemaLocus);
    IModelObject minObject = schemaLocus.getFirstChild( "min");
    minLength = (minObject != null)? Xlate.get( minObject, 0): -1;
    IModelObject maxObject = schemaLocus.getFirstChild( "max");
    maxLength = (maxObject != null)? Xlate.get( maxObject, 0): -1;
  }

  /* (non-Javadoc)
   * @see org.xmodel.xsd.nu.ICheck#validateImpl(org.xmodel.IModelObject)
   */
  protected boolean validateImpl( IModelObject documentLocus)
  {
    String value = Xlate.get( documentLocus, "");
    if ( minLength >= 0 && value.length() < minLength) return false;
    if ( maxLength >= 0 && value.length() > maxLength) return false;
    return true;
  }
  
  private int minLength;
  private int maxLength;
}
