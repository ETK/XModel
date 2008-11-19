/*
 * XModel (XML Application Data Modeling Framework)
 * Author: Bob Dunnagan (bdunnagan@nc.rr.com)
 * Copyright Bob Dunnagan 2009. All rights reserved.
 */
package org.xmodel.dependency;


/**
 * An implementation of IDependency which defines dependency relationships between classes.
 */
@SuppressWarnings("unchecked")
public class ClassDependency implements IDependency
{
  public ClassDependency( Class first, Class second)
  {
    this.first = first;
    this.second = second;
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.IDependency#evaluate(java.lang.Object, java.lang.Object)
   */
  public boolean evaluate( Object target, Object dependent)
  {
    return target.getClass().equals( second) && dependent.getClass().equals( first);
  }
  
  Class first;
  Class second;
}
