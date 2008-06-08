/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.xpath.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dunnagan.bob.xmodel.IModelObject;
import dunnagan.bob.xmodel.xpath.expression.ExpressionException;
import dunnagan.bob.xmodel.xpath.expression.IContext;
import dunnagan.bob.xmodel.xpath.expression.IExpression;

/**
 * An implementation of IExpression which represents an X-Path 2.0 fn:distinct function. The
 * name of the function does not include the "fn" namespace, however.
 */
public class DistinctValuesFunction extends Function
{
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IExpression#getName()
   */
  public String getName()
  {
    return "distinct-values";
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IExpression#getType()
   */
  public ResultType getType()
  {
    return ResultType.NODES;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#evaluateNodes(
   * dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  public List<IModelObject> evaluateNodes( IContext context) throws ExpressionException
  {
    assertArgs( 1, 1);
    assertType( context, ResultType.NODES);
    
    List<IModelObject> nodes = getArgument( 0).evaluateNodes( context);
    List<IModelObject> result = new ArrayList<IModelObject>( nodes.size());
    Map<Object, IModelObject> map = new HashMap<Object, IModelObject>( nodes.size());
    for( IModelObject node: nodes)
    {
      Object value = node.getValue();
      IModelObject match = map.get( value);
      if ( match == null)
      {
        map.put( value, node);
        result.add( node);
      }
    }
    return result;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyAdd(
   * dunnagan.bob.xmodel.xpath.expression.IExpression, dunnagan.bob.xmodel.xpath.expression.IContext, 
   * java.util.List)
   */
  @Override
  public void notifyAdd( IExpression expression, IContext context, List<IModelObject> nodes)
  {
    IExpression parent = getParent();
    if ( parent != null) parent.notifyChange( this, context);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyRemove(
   * dunnagan.bob.xmodel.xpath.expression.IExpression, dunnagan.bob.xmodel.xpath.expression.IContext, 
   * java.util.List)
   */
  @Override
  public void notifyRemove( IExpression expression, IContext context, List<IModelObject> nodes)
  {
    IExpression parent = getParent();
    if ( parent != null) parent.notifyChange( this, context);
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IExpression#requiresValueNotification(
   * dunnagan.bob.xmodel.xpath.expression.IExpression)
   */
  @Override
  public boolean requiresValueNotification( IExpression argument)
  {
    IExpression parent = getParent();
    if ( parent == null) return false;
    return parent.requiresValueNotification( this);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IExpression#notifyValue(java.util.Collection, 
   * dunnagan.bob.xmodel.IModelObject, java.lang.Object, java.lang.Object)
   */
  public void notifyValue( IExpression expression, IContext[] contexts, IModelObject object, Object newValue, Object oldValue)
  {
    IExpression parent = getParent();
    if ( parent != null) parent.notifyValue( this, contexts, object, newValue, oldValue);
  }
}
