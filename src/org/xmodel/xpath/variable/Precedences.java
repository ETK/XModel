/*
 * XModel (XML Application Data Modeling Framework)
 * Author: Bob Dunnagan (bdunnagan@nc.rr.com)
 * Copyright Bob Dunnagan 2009. All rights reserved.
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
