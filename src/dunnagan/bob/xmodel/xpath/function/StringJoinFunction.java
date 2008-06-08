/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.xpath.function;

import java.util.List;

import dunnagan.bob.xmodel.IModelObject;
import dunnagan.bob.xmodel.Xlate;
import dunnagan.bob.xmodel.xpath.expression.ExpressionException;
import dunnagan.bob.xmodel.xpath.expression.IContext;
import dunnagan.bob.xmodel.xpath.expression.IExpression;

/**
 * An implementation of the XPath 2.0 fn:string-join function. 
 */
public class StringJoinFunction extends Function
{
  public final static String name = "string-join";
  
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
    return ResultType.STRING;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#evaluateString(dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public String evaluateString( IContext context) throws ExpressionException
  {
    assertArgs( 2, 2);
    assertType( context, 0, ResultType.NODES);
    assertType( context, 1, ResultType.STRING);

    List<IModelObject> sequence = getArgument( 0).evaluateNodes( context);
    String seperator = getArgument( 1).evaluateString( context);
    return stringJoin( sequence, seperator);
  }

  /**
   * Returns a string consisting of the values of the specified nodes seperated by the specified string.
   * @param sequence The sequence of nodes.
   * @param separator The separator string.
   * @return Returns a string consisting of the values of the specified nodes seperated by the specified string.
   */
  private String stringJoin( List<IModelObject> sequence, String separator)
  {
    StringBuilder builder = new StringBuilder();
    for( int i=0; i<sequence.size()-1; i++)
    {
      IModelObject node = sequence.get( i);
      builder.append( Xlate.get( node, ""));
      builder.append( separator);
    }
    
    if ( sequence.size() > 0)
    {
      IModelObject node = sequence.get( sequence.size()-1);
      builder.append( Xlate.get( node, ""));
    }
    
    return builder.toString();
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyAdd(dunnagan.bob.xmodel.xpath.expression.IExpression, 
   * dunnagan.bob.xmodel.xpath.expression.IContext, java.util.List)
   */
  @Override
  public void notifyAdd( IExpression expression, IContext context, List<IModelObject> nodes)
  {
    getParent().notifyChange( this, context);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyRemove(dunnagan.bob.xmodel.xpath.expression.IExpression, 
   * dunnagan.bob.xmodel.xpath.expression.IContext, java.util.List)
   */
  @Override
  public void notifyRemove( IExpression expression, IContext context, List<IModelObject> nodes)
  {
    getParent().notifyChange( this, context);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyChange(dunnagan.bob.xmodel.xpath.expression.IExpression, 
   * dunnagan.bob.xmodel.xpath.expression.IContext, java.lang.String, java.lang.String)
   */
  @Override
  public void notifyChange( IExpression expression, IContext context, String newValue, String oldValue)
  {
    getParent().notifyChange( this, context);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyChange(dunnagan.bob.xmodel.xpath.expression.IExpression, 
   * dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public void notifyChange( IExpression expression, IContext context)
  {
    getParent().notifyChange( this, context);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyValue(dunnagan.bob.xmodel.xpath.expression.IExpression, 
   * dunnagan.bob.xmodel.xpath.expression.IContext[], dunnagan.bob.xmodel.IModelObject, java.lang.Object, java.lang.Object)
   */
  @Override
  public void notifyValue( IExpression expression, IContext[] contexts, IModelObject object, Object newValue, Object oldValue)
  {
    for( IContext context: contexts) getParent().notifyChange( this, context);
  }
}
