/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package org.xmodel.xaction;

import org.xmodel.xpath.expression.IContext;
import org.xmodel.xpath.variable.IVariableScope;

/**
 * An interface for the implementation of an action defined in a viewmodel.
 */
public interface IXAction
{
  /**
   * Set the document from which this action will be configured.
   * @param document The document.
   */
  public void setDocument( XActionDocument document);
  
  /**
   * Returns the viewmodel for this action.
   * @return Returns the viewmodel for this action.
   */
  public XActionDocument getDocument();
  
  /**
   * Configure the action from the viewmodel.
   * @param document The viewmodel.
   */
  public void configure( XActionDocument document);

  /**
   * Run this action with an arbitrary context and return its variable scope.
   * @return Returns the variable scope of the context so variables can be examined.
   */
  public IVariableScope run();
  
  /**
   * Run the specified action given its viewmodel.
   * @param context The adapter context.
   */
  public void run( IContext context);
  
  /**
   * Returns true if the enclosing script should terminate.
   * @return Returns true if the enclosing script should terminate.
   */
  public boolean shouldReturn();
}
