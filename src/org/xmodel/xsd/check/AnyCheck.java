/*
 * XModel (XML Application Data Modeling Framework)
 * Author: Bob Dunnagan (bdunnagan@nc.rr.com)
 * Copyright Bob Dunnagan 2009. All rights reserved.
 */
package org.xmodel.xsd.check;

import org.xmodel.IModelObject;

public class AnyCheck extends ConstraintCheck
{
  public AnyCheck( IModelObject schemaLocus)
  {
    super( schemaLocus);
  }

  /* (non-Javadoc)
   * @see org.xmodel.xsd.nu.ConstraintCheck#validateOnce(org.xmodel.IModelObject, int, int)
   */
  @Override
  public boolean validateOnce( IModelObject documentLocus, int start, int end)
  {
    // TODO: need to handle different types of any constraints
    count = end - start; 
    return true;
  }

  /* (non-Javadoc)
   * @see org.xmodel.xsd.nu.ConstraintCheck#anyCount()
   */
  @Override
  public int anyCount()
  {
    return count;
  }
  
  private int count;
}
