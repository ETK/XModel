/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.xpath.expression;

import java.util.Collections;
import java.util.List;

import dunnagan.bob.xmodel.IModel;
import dunnagan.bob.xmodel.IModelObject;
import dunnagan.bob.xmodel.xpath.expression.IExpression.ResultType;

/**
 * An ExpressionListener which provides the complete new and old node-sets each time the value
 * of a node-set expression is updated. All of the other notification mechanisms are the same
 * as ExpressionListener.
 */
public abstract class ExtentExpressionListener extends ExpressionListener
{
  /**
   * Called whenever a node-set expression is updated.
   * @param expression The expression.
   * @param context The context.
   * @param newSet The complete new node-set.
   * @param oldSet The complete old node-set.
   */
  public abstract void notifyChange( IExpression expression, IContext context, List<IModelObject> newSet, List<IModelObject> oldSet);
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.ExpressionListener#notifyAdd(
   * dunnagan.bob.xmodel.xpath.expression.IExpression, dunnagan.bob.xmodel.xpath.expression.IContext, java.util.List)
   */
  @Override
  public void notifyAdd( IExpression expression, IContext context, List<IModelObject> nodes)
  {
    IModel model = context.getModel();
    List<IModelObject> oldSet = Collections.emptyList();
    try
    {
      model.revert();
      oldSet = expression.evaluateNodes( context);
    }
    catch( ExpressionException e)
    {
      handleException( expression, context, e);
    }
    finally
    {
      model.restore();
    }

    List<IModelObject> newSet = Collections.emptyList();
    try
    {
      newSet = expression.evaluateNodes( context);
    }
    catch( ExpressionException e)
    {
      handleException( expression, context, e);
    }
    
    // notify
    notifyChange( expression, context, newSet, oldSet);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.ExpressionListener#notifyRemove(
   * dunnagan.bob.xmodel.xpath.expression.IExpression, dunnagan.bob.xmodel.xpath.expression.IContext, java.util.List)
   */
  @Override
  public void notifyRemove( IExpression expression, IContext context, List<IModelObject> nodes)
  {
    IModel model = context.getModel();
    List<IModelObject> oldSet = Collections.emptyList();
    try
    {
      model.revert();
      oldSet = expression.evaluateNodes( context);
    }
    catch( ExpressionException e)
    {
      handleException( expression, context, e);
    }
    finally
    {
      model.restore();
    }

    List<IModelObject> newSet = Collections.emptyList();
    try
    {
      newSet = expression.evaluateNodes( context);
    }
    catch( ExpressionException e)
    {
      handleException( expression, context, e);
    }
    
    // notify
    notifyChange( expression, context, newSet, oldSet);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.ExpressionListener#notifyChange(
   * dunnagan.bob.xmodel.xpath.expression.IExpression, dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public void notifyChange( IExpression expression, IContext context)
  {
    if ( expression.getType( context) == ResultType.NODES)
    {
      IModel model = context.getModel();
      List<IModelObject> oldSet = Collections.emptyList();
      try
      {
        model.revert();
        oldSet = expression.evaluateNodes( context);
      }
      catch( ExpressionException e)
      {
        handleException( expression, context, e);
      }
      finally
      {
        model.restore();
      }
  
      List<IModelObject> newSet = Collections.emptyList();
      try
      {
        newSet = expression.evaluateNodes( context);
      }
      catch( ExpressionException e)
      {
        handleException( expression, context, e);
      }
      
      // notify
      notifyChange( expression, context, newSet, oldSet);
    }
  }
}
