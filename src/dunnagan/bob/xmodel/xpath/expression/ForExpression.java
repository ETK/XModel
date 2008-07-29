/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.xpath.expression;

import java.util.*;

import dunnagan.bob.xmodel.IChangeSet;
import dunnagan.bob.xmodel.IModel;
import dunnagan.bob.xmodel.IModelObject;
import dunnagan.bob.xmodel.IModelObjectFactory;
import dunnagan.bob.xmodel.memento.IMemento;
import dunnagan.bob.xmodel.memento.VariableMemento;
import dunnagan.bob.xmodel.xpath.expression.IExpression.ResultType;
import dunnagan.bob.xmodel.xpath.variable.*;

/**
 * A partial implementation of the X-Path 2.0 for/return expression. Since this is a hybrid
 * X-Path 1.0/2.0 implementation, both the iterator and the return expressions must return
 * node-sets. Also, this expression does not behave as expected when the iterator variable
 * is enclosed in the string() or number() functions and this usage should be avoided.
 */
public class ForExpression extends Expression
{
  public ForExpression( String variable)
  {
    this.variable = variable;
    this.forScope = new VariableScope( "for", Precedences.forScope);
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IExpression#getName()
   */
  public String getName()
  {
    return "for-return";
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
  @Override
  public List<IModelObject> evaluateNodes( IContext context) throws ExpressionException
  {
    assertType( context, 0, ResultType.NODES);

    IExpression arg0 = getArgument( 0);
    IExpression arg1 = getArgument( 1);

    IVariableSource source = getVariableSource();
    try
    {
      source.addScope( forScope);
      List<IModelObject> nodes = arg0.evaluateNodes( context);
      List<IModelObject> result = new ArrayList<IModelObject>();
      for( IModelObject node: nodes)
      {
        forScope.set( variable, node);
        assertType( context, 1, ResultType.NODES);
        result.addAll( arg1.evaluateNodes( context));
      }
      return result;
    }
    finally
    {
      source.removeScope( forScope);
    }
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#createSubtree(dunnagan.bob.xmodel.xpath.expression.IContext, 
   * dunnagan.bob.xmodel.IModelObjectFactory, dunnagan.bob.xmodel.IChangeSet)
   */
  @Override
  public void createSubtree( IContext context, IModelObjectFactory factory, IChangeSet undo)
  {
    List<IModelObject> nodes = getArgument( 0).evaluateNodes( context);
    for( int i=0; i<nodes.size(); i++)
    {
      getArgument( 1).createSubtree( new SubContext( context, nodes.get( i), i+1, nodes.size()), factory, undo);
    }
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IExpression#bind(
   * dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public void bind( IContext context)
  {
    IExpression arg0 = getArgument( 0);
    IExpression arg1 = getArgument( 1);
    
    // bind iterator expression
    arg0.bind( context);
    
    // bind return expression
    try
    {
      List<IModelObject> nodes = arg0.evaluateNodes( context);
      for( IModelObject node: nodes)
      {
        ReturnContext returnContext = new ReturnContext( context, variable, node);
        arg1.bind( returnContext);
      }
    }
    catch( ExpressionException e)
    {
      if ( parent != null) parent.handleException( this, context, e);
    }
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IExpression#unbind(
   * dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public void unbind( IContext context)
  {
    IExpression arg0 = getArgument( 0);
    IExpression arg1 = getArgument( 1);
    
    // unbind iterator expression
    arg0.unbind( context);
    
    // unbind return expression
    try
    {
      List<IModelObject> nodes = arg0.evaluateNodes( context);
      for( IModelObject node: nodes)
      {
        ReturnContext returnContext = new ReturnContext( context, variable, node);
        arg1.unbind( returnContext);
      }
    }
    catch( ExpressionException e)
    {
      if ( parent != null) parent.handleException( this, context, e);
    }
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyAdd(
   * dunnagan.bob.xmodel.xpath.expression.IExpression, dunnagan.bob.xmodel.xpath.expression.IContext, 
   * java.util.List)
   */
  @Override
  public void notifyAdd( IExpression expression, IContext context, List<IModelObject> nodes)
  {
    IExpression arg0 = getArgument( 0);
    IExpression arg1 = getArgument( 1);
    if ( expression == arg0)
    {
      for( IModelObject node: nodes)
      {
        ReturnContext returnContext = new ReturnContext( context, variable, node);
        try
        {
          arg1.bind( returnContext);
          List<IModelObject> result = arg1.evaluateNodes( returnContext);
          if ( result.size() > 0) parent.notifyAdd( this, context, result);
        }
        catch( ExpressionException e)
        {
          parent.handleException( this, context, e);
        }
      }
    }
    else if ( expression == arg1)
    {
      if ( parent != null) parent.notifyAdd( this, context.getParent(), nodes);
    }
    else
    {
      throw new IllegalStateException( "Notification from expression which is not an argument.");
    }
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyRemove(
   * dunnagan.bob.xmodel.xpath.expression.IExpression, dunnagan.bob.xmodel.xpath.expression.IContext, 
   * java.util.List)
   */
  @Override
  public void notifyRemove( IExpression expression, IContext context, List<IModelObject> nodes)
  {
    IExpression arg0 = getArgument( 0);
    IExpression arg1 = getArgument( 1);
    if ( expression == arg0)
    {
      IModel model = context.getModel();
      for( IModelObject node: nodes)
      {
        model.revert();
        ReturnContext returnContext = new ReturnContext( context, variable, node);
        try
        {
          arg1.unbind( returnContext);
          List<IModelObject> result = arg1.evaluateNodes( returnContext);
          model.restore();
          if ( parent != null && result.size() > 0) parent.notifyRemove( this, context, result);
        }
        catch( ExpressionException e)
        {
          if ( parent != null) parent.handleException( this, context, e);
        }
      }
    }
    else if ( expression == arg1)
    {
      if ( parent != null) parent.notifyRemove( this, context.getParent(), nodes);
    }
    else
    {
      throw new IllegalStateException( "Notification from expression which is not an argument.");
    }
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyChange(
   * dunnagan.bob.xmodel.xpath.expression.IExpression, dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public void notifyChange( IExpression expression, IContext context)
  {
    IExpression arg0 = getArgument( 0);
    IExpression arg1 = getArgument( 1);
    
    context.getModel().revert();
    Collection<IModelObject> oldNodes = arg0.evaluateNodes( context, null);
    if ( oldNodes.size() > 5) oldNodes = new HashSet<IModelObject>( oldNodes);
    
    context.getModel().restore();
    Collection<IModelObject> newNodes = arg0.evaluateNodes( context, null);
    if ( newNodes.size() > 5) newNodes = new HashSet<IModelObject>( newNodes);

    // unbind removed nodes
    context.getModel().revert();
    for( IModelObject node: oldNodes)
      if ( !newNodes.contains( node))
      {
        ReturnContext returnContext = new ReturnContext( context, variable, node);
        arg1.unbind( returnContext);
      }
    
    // bind added nodes
    context.getModel().restore();
    for( IModelObject node: newNodes)
      if ( !oldNodes.contains( node))
      {
        ReturnContext returnContext = new ReturnContext( context, variable, node);
        arg1.bind( returnContext);
      }

    // foward notification
    if ( parent != null)
    {
      if ( expression == arg1) context = context.getParent();
      parent.notifyChange( this, context);
    }
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyValue(d
   * unnagan.bob.xmodel.xpath.expression.IExpression, dunnagan.bob.xmodel.xpath.expression.IContext[], 
   * dunnagan.bob.xmodel.IModelObject, java.lang.Object, java.lang.Object)
   */
  @Override
  public void notifyValue( IExpression expression, IContext[] contexts, IModelObject object, Object newValue, Object oldValue)
  {
    if ( expression == getArgument( 0))
    {
      super.notifyValue( expression, contexts, object, newValue, oldValue);
    }
    else
    {
      IContext[] parents = new IContext[ contexts.length];
      for( int i=0; i<parents.length; i++) parents[ i] = contexts[ i].getParent();
      super.notifyValue( expression, parents, object, newValue, oldValue);
    }
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#requiresValueNotification(
   * dunnagan.bob.xmodel.xpath.expression.IExpression)
   */
  @Override
  public boolean requiresValueNotification( IExpression argument)
  {
    // always require value notification from the iterator expression since we cannot
    // determine here how the variable is used in the return expression
    if ( argument == getArgument( 0)) return true;
    
    // require value notification from the return expression if the parent requires value notification
    if ( parent.requiresValueNotification( this) && argument == getArgument( 1)) return true;
    return false;
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#cloneOne()
   */
  @Override
  protected IExpression cloneOne()
  {
    return new ForExpression( variable);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "for ");
    builder.append( '$');
    builder.append( variable);
    builder.append( " in ");
    builder.append( getArgument( 0));
    builder.append( " return\n");
    builder.append( "  ");
    builder.append( getArgument( 1));
    return builder.toString();
  }
  
  String variable;
  IExpression returnExprClone;
  IVariableScope forScope;
}

/**
 * A context used to bind each node in the iterator node-set to the return expression.
 * This context should be rewritten without Context for a base-class since it has the
 * overhead of the <code>updates</code> table.  Note that the bound variable never 
 * changes so variable notification isn't necessary.
 */
class ReturnContext extends Context
{
  /**
   * Create a ReturnContext for the specified ForExpression context.
   * @param context The ForExpression input context.
   * @param name The name of the iterator variable.
   * @param node The iteration node.
   */
  public ReturnContext( IContext context, String name, IModelObject node)
  {
    super( new ReturnScope( context.getScope(), name, node), context.getObject(), context.getPosition(), context.getSize());
    parent = context;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Context#getParent()
   */
  @Override
  public IContext getParent()
  {
    return parent;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Context#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object arg0)
  {
    if ( arg0 instanceof ReturnContext)
    {
      ReturnContext context = (ReturnContext)arg0;
      return parent.equals( context.getParent()) && context.scope.equals( scope);
    }
    return false;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Context#hashCode()
   */
  @Override
  public int hashCode()
  {
    return super.hashCode() ^ scope.hashCode();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Context#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append( "return:");
    sb.append( super.toString());
    return sb.toString();
  }

  IContext parent;
}

/**
 * An implementation of IVariableScope which resolves the iterator variable for the expression to which
 * it belongs and resolves all other variables with an optional parent context scope. ReturnScope does
 * not bind its variable.
 */
class ReturnScope implements IVariableScope
{
  /**
   * Create a ReturnScope to hold the specified variable.
   * @param parent The parent scope.
   * @param name The name of the variable.
   * @param node The value of the variable.
   */
  ReturnScope( IVariableScope parent, String name, IModelObject node)
  {
    this.parent = parent;
    this.name = name;
    this.value = Collections.singletonList( node);
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#getName()
   */
  public String getName()
  {
    return "context";
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#getPrecedence()
   */
  public int getPrecedence()
  {
    return Precedences.contextScope;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#set(java.lang.String, java.lang.Boolean)
   */
  public Boolean set( String name, Boolean value)
  {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#set(java.lang.String, dunnagan.bob.xmodel.IModelObject)
   */
  public List<IModelObject> set( String name, IModelObject value)
  {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#set(java.lang.String, java.util.List)
   */
  public List<IModelObject> set( String name, List<IModelObject> value)
  {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#set(java.lang.String, java.lang.Number)
   */
  public Number set( String name, Number value)
  {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#set(java.lang.String, java.lang.String)
   */
  public String set( String name, String value)
  {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#setPojo(java.lang.String, java.lang.Object, dunnagan.bob.xmodel.IModelObjectFactory)
   */
  public Object setPojo( String name, Object pojo, IModelObjectFactory factory)
  {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#define(java.lang.String, dunnagan.bob.xmodel.xpath.expression.IExpression)
   */
  public void define( String name, IExpression expression)
  {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#get(java.lang.String, dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  public Object get( String name, IContext context) throws ExpressionException
  {
    if ( name.equals( this.name)) return value;
    if ( parent == null) return null;
    return parent.get( name, context);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#get(java.lang.String)
   */
  public Object get( String name)
  {
    if ( name.equals( this.name)) return value;
    if ( parent == null) return null;
    return parent.get( name);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#getPojo(java.lang.String)
   */
  public Object getPojo( String name)
  {
    if ( name.equals( this.name)) return value;
    if ( parent == null) return null;
    return parent.getPojo( name);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#getAll()
   */
  public Collection<String> getAll()
  {
    Set<String> names = new HashSet<String>();
    if ( parent != null) names.addAll( parent.getAll());
    names.add( name);
    return names;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#getSource()
   */
  public IVariableSource getSource()
  {
    return source;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#getType(java.lang.String)
   */
  public ResultType getType( String name)
  {
    if ( name.equals( this.name)) return ResultType.NODES;
    return parent.getType( name);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#getType(java.lang.String, dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  public ResultType getType( String name, IContext context)
  {
    if ( name.equals( this.name)) return ResultType.NODES;
    return parent.getType( name, context);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#internal_setSource(dunnagan.bob.xmodel.xpath.variable.IVariableSource)
   */
  public void internal_setSource( IVariableSource source)
  {
    this.source = source;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#isDefined(java.lang.String)
   */
  public boolean isDefined( String name)
  {
    if ( name.equals( this.name)) return true;
    if ( parent == null) return false;
    return parent.isDefined( name);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#isBound(java.lang.String)
   */
  public boolean isBound( String name)
  {
    if ( name.equals( this.name)) return false;
    if ( parent == null) return false;
    return parent.isBound( name);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#addListener(java.lang.String, 
   * dunnagan.bob.xmodel.xpath.expression.IContext, dunnagan.bob.xmodel.xpath.variable.IVariableListener)
   */
  public void addListener( String name, IContext context, IVariableListener listener)
  {
    if ( name.equals( this.name)) return;
    parent.addListener( name, context, listener);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#removeListener(java.lang.String, 
   * dunnagan.bob.xmodel.xpath.expression.IContext, dunnagan.bob.xmodel.xpath.variable.IVariableListener)
   */
  public void removeListener( String name, IContext context, IVariableListener listener)
  {
    if ( name.equals( this.name)) return;
    parent.removeListener( name, context, listener);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#restore(dunnagan.bob.xmodel.memento.IMemento)
   */
  public void restore( IMemento iMemento)
  {
    VariableMemento memento = (VariableMemento)iMemento;
    if ( memento.varName.equals( name)) return;
    parent.restore( iMemento);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#revert(dunnagan.bob.xmodel.memento.IMemento)
   */
  public void revert( IMemento iMemento)
  {
    VariableMemento memento = (VariableMemento)iMemento;
    if ( memento.varName.equals( name)) return;
    parent.revert( iMemento);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#copyFrom(dunnagan.bob.xmodel.xpath.variable.IVariableScope)
   */
  public void copyFrom( IVariableScope scope)
  {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object arg0)
  {
    if ( arg0 instanceof ReturnScope)
    {
      ReturnScope scope = (ReturnScope)arg0;
      return scope.name.equals( name) && scope.value.equals( value);
    }
    return false;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    return name.hashCode() ^ value.hashCode();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#cloneOne()
   */
  public IVariableScope cloneOne()
  {
    throw new UnsupportedOperationException();
  }
  
  IVariableSource source;
  IVariableScope parent;
  String name;
  Object value;
}