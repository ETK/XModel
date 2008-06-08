/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.xpath.function;

import java.util.List;

import dunnagan.bob.xmodel.IModelObject;
import dunnagan.bob.xmodel.xpath.expression.ExpressionException;
import dunnagan.bob.xmodel.xpath.expression.IContext;
import dunnagan.bob.xmodel.xpath.expression.IExpression;

/**
 * A custom xpath function which prints activity to System.out.
 */
public class TraceFunction extends Function
{
  public final static String name = "trace";
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IExpression#getName()
   */
  public String getName()
  {
    return name;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IExpression#getType()
   */
  public ResultType getType()
  {
    return getArgument( 0).getType();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#getType(dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public ResultType getType( IContext context)
  {
    return getArgument( 0).getType( context);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#bind(dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public void bind( IContext context)
  {
    System.out.println( getPrefix( context));
    System.out.println( "  bind: "+context);
    getArgument( 0).bind( context);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#unbind(dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public void unbind( IContext context)
  {
    System.out.println( getPrefix( context));
    System.out.println( "  unbind: "+context);
    getArgument( 0).unbind( context);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#evaluateBoolean(dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public boolean evaluateBoolean( IContext context) throws ExpressionException
  {
    boolean result = getArgument( 0).evaluateBoolean( context);
    System.out.println( getPrefix( context));
    System.out.println( "  eval: "+context+", "+result);
    return result;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#evaluateNodes(dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public List<IModelObject> evaluateNodes( IContext context) throws ExpressionException
  {
    List<IModelObject> result = getArgument( 0).evaluateNodes( context);
    System.out.println( getPrefix( context));
    System.out.println( "  eval: "+context+", "+result.size());
    for( int i=0; i<result.size(); i++) System.out.println( "    ["+i+"] "+result.get( i));
    return result;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#evaluateNumber(dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public double evaluateNumber( IContext context) throws ExpressionException
  {
    double result = getArgument( 0).evaluateNumber( context);
    System.out.println( getPrefix( context));
    System.out.println( "  eval: "+context+", "+result);
    return result;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#evaluateString(dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public String evaluateString( IContext context) throws ExpressionException
  {
    String result = getArgument( 0).evaluateString( context);
    System.out.println( getPrefix( context));
    System.out.println( "  eval: "+context+", "+result);
    return result;
  }

  /**
   * Returns the string result of the second argument.
   * @param context The context.
   * @return Returns the string result of the second argument.
   */
  private String getPrefix( IContext context)
  {
    try
    {
      IExpression arg1 = getArgument( 1);
      if ( arg1 == null) return "\n"+toString();
      return arg1.evaluateString( context);
    }
    catch( ExpressionException e)
    {
      e.printStackTrace( System.err);
      return "Error in trace prefix: ";
    }
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyAdd(dunnagan.bob.xmodel.xpath.expression.IExpression, 
   * dunnagan.bob.xmodel.xpath.expression.IContext, java.util.List)
   */
  @Override
  public void notifyAdd( IExpression expression, IContext context, List<IModelObject> nodes)
  {
    System.out.println( getPrefix( context));
    System.out.println( "  added: "+context+", "+nodes.size());
    for( int i=0; i<nodes.size(); i++) System.out.println( "    ["+i+"] "+nodes.get( i));
    getParent().notifyAdd( this, context, nodes);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyRemove(dunnagan.bob.xmodel.xpath.expression.IExpression, 
   * dunnagan.bob.xmodel.xpath.expression.IContext, java.util.List)
   */
  @Override
  public void notifyRemove( IExpression expression, IContext context, List<IModelObject> nodes)
  {
    System.out.println( getPrefix( context));
    System.out.println( "  removed: "+context+", "+nodes.size());
    for( int i=0; i<nodes.size(); i++) System.out.println( "    ["+i+"] "+nodes.get( i));
    getParent().notifyRemove( this, context, nodes);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyChange(dunnagan.bob.xmodel.xpath.expression.IExpression, 
   * dunnagan.bob.xmodel.xpath.expression.IContext, boolean)
   */
  @Override
  public void notifyChange( IExpression expression, IContext context, boolean newValue)
  {
    System.out.println( getPrefix( context));
    System.out.println( "  changed: new="+newValue+", context="+context);
    getParent().notifyChange( this, context, newValue);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyChange(dunnagan.bob.xmodel.xpath.expression.IExpression, 
   * dunnagan.bob.xmodel.xpath.expression.IContext, double, double)
   */
  @Override
  public void notifyChange( IExpression expression, IContext context, double newValue, double oldValue)
  {
    System.out.println( getPrefix( context));
    System.out.println( "  changed: new="+newValue+", old="+oldValue+", context="+context);
    getParent().notifyChange( this, context, newValue, oldValue);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyChange(dunnagan.bob.xmodel.xpath.expression.IExpression, 
   * dunnagan.bob.xmodel.xpath.expression.IContext, java.lang.String, java.lang.String)
   */
  @Override
  public void notifyChange( IExpression expression, IContext context, String newValue, String oldValue)
  {
    System.out.println( getPrefix( context));
    System.out.println( "  changed: new="+newValue+", old="+oldValue+", context="+context);
    getParent().notifyChange( this, context, newValue, oldValue);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyChange(dunnagan.bob.xmodel.xpath.expression.IExpression, 
   * dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public void notifyChange( IExpression expression, IContext context)
  {
    System.out.println( getPrefix( context));
    System.out.println( "  changed: context="+context);
    getParent().notifyChange( this, context);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyValue(dunnagan.bob.xmodel.xpath.expression.IExpression, 
   * dunnagan.bob.xmodel.xpath.expression.IContext[], dunnagan.bob.xmodel.IModelObject, java.lang.Object, java.lang.Object)
   */
  @Override
  public void notifyValue( IExpression expression, IContext[] contexts, IModelObject object, Object newValue, Object oldValue)
  {
    System.out.println( getPrefix( contexts[ 0]));
    System.out.println( "  changed: new="+newValue+", old="+oldValue);
    getParent().notifyValue( this, contexts, object, newValue, oldValue);
  }
}
