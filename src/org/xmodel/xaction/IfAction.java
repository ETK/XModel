/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package org.xmodel.xaction;

import org.xmodel.IModelObject;
import org.xmodel.xpath.XPath;
import org.xmodel.xpath.expression.IContext;
import org.xmodel.xpath.expression.IExpression;

/**
 * An action which executes on of two actions depending on the result of a condition evaluation.
 */
public class IfAction extends XAction
{
  /* (non-Javadoc)
   * @see org.xmodel.ui.swt.form.actions.XAction#configure(org.xmodel.ui.model.ViewModel)
   */
  @Override
  public void configure( XActionDocument document)
  {
    super.configure( document);
    
    IModelObject root = document.getRoot();
    whenExpr = document.getExpression( root.getAttributeNode( "when"));
    thenScript = document.createScript( thenPath);
    elseScript = document.createScript( elsePath);
  }

  /* (non-Javadoc)
   * @see org.xmodel.ui.swt.form.IXAction#run(org.xmodel.xpath.expression.IContext)
   */
  public void doRun( IContext context)
  {
    if ( whenExpr.evaluateBoolean( context)) thenScript.run( context); else elseScript.run( context);
  }

  private final static IExpression thenPath = XPath.createExpression( "then/*");
  private final static IExpression elsePath = XPath.createExpression( "else/*");
  
  private IExpression whenExpr;
  private ScriptAction thenScript;
  private ScriptAction elseScript;
}
