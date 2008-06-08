/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.xpath.expression;

import dunnagan.bob.xmodel.xpath.function.BooleanFunction;

/**
 * An implementation of IExpression which represents an X-Path 1.0 logical expression.
 */
public class LogicalExpression extends AbstractBinaryBooleanExpression
{
  public enum Operator { OR, AND};
  
  /**
   * Create an OrExpression with the given operator.
   * @param operator The type of logical expression.
   */
  public LogicalExpression( Operator operator)
  {
    this.operator = operator;
  }
  
  /**
   * Create an OrExpression with the given operator, lhs and rhs expressions.
   * @param operator The type of logical expression.
   * @param lhs The left-hand-side of the expression.
   * @param rhs The right-hand-side of the expression.
   */
  public LogicalExpression( Operator operator, IExpression lhs, IExpression rhs)
  {
    this.operator = operator;
    addArgument( lhs);
    addArgument( rhs);
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IExpression#getName()
   */
  public String getName()
  {
    return "logical";
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#addArgument(
   * dunnagan.bob.xmodel.xpath.expression.IExpression)
   */
  public void addArgument( IExpression argument)
  {
    if ( argument.getType() != ResultType.BOOLEAN)
      argument = new BooleanFunction( argument);
    super.addArgument( argument);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.AbstractBinaryBooleanExpression#evaluate(
   * dunnagan.bob.xmodel.xpath.expression.IContext, dunnagan.bob.xmodel.xpath.expression.IExpression, 
   * dunnagan.bob.xmodel.xpath.expression.IExpression)
   */
  public boolean evaluate( IContext context, IExpression lhs, IExpression rhs) throws ExpressionException
  {
    boolean result0 = lhs.evaluateBoolean( context);
    boolean result1 = rhs.evaluateBoolean( context);
    return (operator == Operator.OR)? (result0 || result1): (result0 && result1);
  }

  /**
   * Returns the result of applying this operation to the operands.
   * @param arg0 The first operand.
   * @param arg1 The second operand.
   * @return Returns the result of applying this operation to the operands.
   */
  private boolean compareResult( boolean arg0, boolean arg1)
  {
    return (operator == Operator.OR)? (arg0 || arg1): (arg0 && arg1);
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#cloneOne()
   */
  @Override
  protected IExpression cloneOne()
  {
    return new LogicalExpression( operator);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    IExpression lhs = getArgument( 0);
    IExpression rhs = getArgument( 1);
    switch( operator)
    {
      case OR: return lhs.toString()+" or "+rhs.toString();
      case AND: return lhs.toString()+" and "+rhs.toString();
    }
    return null;
  }

  Operator operator;
}
