/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.xpath.expression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dunnagan.bob.xmodel.IModel;
import dunnagan.bob.xmodel.IModelObject;
import dunnagan.bob.xmodel.xpath.variable.IVariableScope;
import dunnagan.bob.xmodel.xpath.variable.Precedences;

/**
 * An implementation of IContext which supports variable assignment. The same instance of this class 
 * must be used for both binding and unbinding. See the Context class for more information.
 */
public class StatefulContext implements IContext
{
  /**
   * Create a context for the given context node with position and size equal to one.
   * @param object The context node.
   */
  public StatefulContext( IModelObject object)
  {
    this( new ContextScope(), object, 1, 1);
  }
  
  /**
   * Create a context for the given context node.
   * @param object The context node.
   * @param position The context position.
   * @param size The context size.
   */
  public StatefulContext( IModelObject object, int position, int size)
  {
    this( new ContextScope(), object, position, size);
  }
    
  /**
   * Create a context for the given context node with position and size equal to one and
   * use the specified scope to store variables.
   * @param scope The scope to be associated with this context.
   * @param object The context node.
   */
  public StatefulContext( IVariableScope scope, IModelObject object)
  {
    this( scope, object, 1, 1);
  }
  
  /**
   * Create a context for the given context node and use the specified scope to store variables.
   * @param scope The scope to be associated with this context.
   * @param object The context node.
   * @param position The context position.
   * @param size The context size.
   */
  public StatefulContext( IVariableScope scope, IModelObject object, int position, int size)
  {
    this.object = object;
    this.position = position;
    this.size = size;
    this.updates = new HashMap<IExpression, Integer>( 1);
    this.scope = (scope != null)? scope: new ContextScope();
  }

  /**
   * Create a context with the specified parent. This method is useful for 
   * creating a nested context with its own local context variable scope.
   * @param scope A context whose scope will be shared.
   * @param object The context node.
   */
  public StatefulContext( IContext scope, IModelObject object)
  {
    this( new ContextScope( scope.getScope()), object, 1, 1);
  }
  
  /**
   * Create a context with the specified parent. This method is useful for 
   * creating a nested context with its own local context variable scope.
   * @param scope A context whose scope will be shared.
   * @param object The context node.
   * @param position The context position.
   * @param size The context size.
   */
  public StatefulContext( IContext scope, IModelObject object, int position, int size)
  {
    this( new ContextScope( scope.getScope()), object, position, size);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IContext#set(java.lang.String, java.lang.String)
   */
  public String set( String name, String value)
  {
    return scope.set( name, value);
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IContext#set(java.lang.String, java.lang.Number)
   */
  public Number set( String name, Number value)
  {
    return scope.set( name, value);
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IContext#set(java.lang.String, java.lang.Boolean)
   */
  public Boolean set( String name, Boolean value)
  {
    return scope.set( name, value);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IContext#set(java.lang.String, java.util.List)
   */
  public List<IModelObject> set( String name, List<IModelObject> value)
  {
    return scope.set( name, value);
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IContext#set(java.lang.String, dunnagan.bob.xmodel.IModelObject)
   */
  public List<IModelObject> set( String name, IModelObject value)
  {
    return scope.set( name, value);
  }

  /**
   * Define the specified variable with the specified expression. This method may only be called
   * once for a given variable. The definition is permanent.
   * @param name The name of the variable.
   * @param expression The expression.
   */
  public void define( String name, IExpression expression)
  {
    scope.define( name, expression);
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
   * @see dunnagan.bob.xmodel.xpath.expression.IContext#getModel()
   */
  public IModel getModel()
  {
    return (object != null)? object.getModel(): null;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IContext#getParent()
   */
  public IContext getParent()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IContext#getRoot()
   */
  public IContext getRoot()
  {
    return this;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IContext#getContextNode()
   */
  public IModelObject getObject()
  {
    return object;
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IContext#getPosition()
   */
  public int getPosition()
  {
    return position;
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IContext#getSize()
   */
  public int getSize()
  {
    return size;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IContext#notifyBind(dunnagan.bob.xmodel.xpath.expression.IExpression)
   */
  public void notifyBind( IExpression expression)
  {
    updates.remove( expression);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IContext#notifyUnbind(dunnagan.bob.xmodel.xpath.expression.IExpression)
   */
  public void notifyUnbind( IExpression expression)
  {
    updates.remove( expression);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IContext#notifyUpdate(dunnagan.bob.xmodel.xpath.expression.IExpression)
   */
  public void notifyUpdate( IExpression expression)
  {
    updates.put( expression, getModel().getUpdateID());
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IContext#markUpdate(dunnagan.bob.xmodel.xpath.expression.IExpression)
   */
  public void markUpdate( IExpression expression)
  {
    updates.remove( expression);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IContext#shouldUpdate(dunnagan.bob.xmodel.xpath.expression.IExpression)
   */
  public boolean shouldUpdate( IExpression expression)
  {
    Integer lastUpdate = updates.get( expression);
    return lastUpdate == null || lastUpdate == 0 || lastUpdate != getModel().getUpdateID();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IContext#getLastUpdate(dunnagan.bob.xmodel.xpath.expression.IExpression)
   */
  public int getLastUpdate( IExpression expression)
  {
    Integer lastUpdate = updates.get( expression);
    if ( lastUpdate == null) return 0;
    return lastUpdate;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IContext#getScope()
   */
  public IVariableScope getScope()
  {
    return scope;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.variable.IVariableScope#cloneOne()
   */
  public IVariableScope cloneOne()
  {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append( "$( "); sb.append( position);
    sb.append( ", "); sb.append( size);
    sb.append( ", "); sb.append( object);
    sb.append( ")");
    return sb.toString();
  }
  
  private IModelObject object;
  private int position;
  private int size;
  private Map<IExpression, Integer> updates;
  private IVariableScope scope;
}