/*
 * XModel (XML Application Data Modeling Framework)
 * Author: Bob Dunnagan (bdunnagan@nc.rr.com)
 * Copyright Bob Dunnagan 2009. All rights reserved.
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
