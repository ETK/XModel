/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.xpath.function.custom;

import java.util.ArrayList;
import java.util.List;

import dunnagan.bob.xmodel.IModelObject;
import dunnagan.bob.xmodel.xpath.XPath;
import dunnagan.bob.xmodel.xpath.expression.*;
import dunnagan.bob.xmodel.xpath.function.Function;

/**
 * A custom XPath which evaluates an string argument as an XPath expression. The expression
 * takes two or more arguments: the first argument contains the context of the evaluation and 
 * the second argument is the string containing the expression to be evaluated. The second 
 * argument is converted to a string as necessary. If the first argument contains more than
 * one node then the second argument will be evaluated once for each if its return type is
 * BOOLEAN or NODES. If the return type is BOOLEAN, then the function will be true iff each
 * evaluation is true. If the return type is NODES, then the function will return the list
 * of nodes from each evaluation.
 * The rest of the arguments are variable/expression pairs which initialize variables on the
 * context in which the first argument expression is evaluated.
 * FIXME: this function does not support binding
 */
public class EvaluateFunction extends Function
{
  public final static String name = "evaluate";
  
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
    return ResultType.UNDEFINED;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#getType(dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public ResultType getType( IContext context)
  {
    String spec = getArgument( 1).evaluateString( context);
    IExpression expr = XPath.createExpression( spec);
    return expr.getType( context);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#evaluateBoolean(dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public boolean evaluateBoolean( IContext context) throws ExpressionException
  {
    assertArgs( 2, Integer.MAX_VALUE);
    assertType( context, 0, ResultType.NODES);
    
    int count = getArguments().size();
    if ( (count % 2) != 0) throw new ExpressionException( this, "Expression must have even number of arguments.");

    IExpression expr = buildExpression( context);
    List<IModelObject> nodes = getArgument( 0).evaluateNodes( context);
    for( IModelObject object: nodes)
    {
      if ( !expr.evaluateBoolean( new Context( object))) 
        return false;
    }
    
    return true;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#evaluateNodes(dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public List<IModelObject> evaluateNodes( IContext context) throws ExpressionException
  {
    assertArgs( 2, Integer.MAX_VALUE);
    assertType( context, 0, ResultType.NODES);
    
    int count = getArguments().size();
    if ( (count % 2) != 0) throw new ExpressionException( this, "Expression must have even number of arguments.");
    
    IExpression expr = buildExpression( context);
    List<IModelObject> nodes = getArgument( 0).evaluateNodes( context);
    List<IModelObject> result = new ArrayList<IModelObject>();
    for( IModelObject object: nodes)
      result.addAll( expr.evaluateNodes( new Context( object))); 
    
    return result;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#evaluateNumber(dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public double evaluateNumber( IContext context) throws ExpressionException
  {
    assertArgs( 2, Integer.MAX_VALUE);
    assertType( context, 0, ResultType.NODES);
    
    int count = getArguments().size();
    if ( (count % 2) != 0) throw new ExpressionException( this, "Expression must have even number of arguments.");
    
    IExpression expr = buildExpression( context);
    IModelObject object = getArgument( 0).queryFirst( context);
    return expr.evaluateNumber( new Context( object)); 
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#evaluateString(dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public String evaluateString( IContext context) throws ExpressionException
  {
    assertArgs( 2, Integer.MAX_VALUE);
    assertType( context, 0, ResultType.NODES);
    
    int count = getArguments().size();
    if ( (count % 2) != 0) throw new ExpressionException( this, "Expression must have even number of arguments.");
    
    IExpression expr = buildExpression( context);
    IModelObject object = getArgument( 0).queryFirst( context);
    return expr.evaluateString( new Context( object)); 
  }

  /**
   * Build the expression to be evaluated.
   * @param context The context.
   * @return Returns the expression to be evaluated
   */
  private IExpression buildExpression( IContext context)
  {
    // build primary expression
    String spec = getArgument( 1).evaluateString( context);
    IExpression primary = XPath.createExpression( spec);
    
    // build variable assign clauses
    List<IExpression> args = getArguments();
    for( int i=2; i<args.size(); i+=2)
    {
      // get variable name
      IExpression varExpr = getArgument( i);
      String varName = varExpr.evaluateString( context);
      
      // get assignment
      IExpression valueExpr = getArgument( i+1);
      ResultType valueType = valueExpr.getType( context);
      switch( valueType)
      {
        case NODES:   primary.setVariable( varName, valueExpr.evaluateNodes( context)); break;
        case NUMBER:  primary.setVariable( varName, valueExpr.evaluateNumber( context)); break;
        case STRING:  primary.setVariable( varName, valueExpr.evaluateString( context)); break;
        case BOOLEAN: primary.setVariable( varName, valueExpr.evaluateBoolean( context)); break;
      }
    }
    
    return primary;
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#bind(dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public void bind( IContext context)
  {
    throw new UnsupportedOperationException( "evaluate() function does not support binding.");
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#unbind(dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public void unbind( IContext context)
  {
  }
}
