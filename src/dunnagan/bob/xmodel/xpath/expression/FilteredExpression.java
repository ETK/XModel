/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.xpath.expression;

import java.util.*;

import dunnagan.bob.xmodel.*;

/**
 * An implementation of IExpression which represents an X-Path 1.0 filtered expression.
 * FilteredExpression will forward update notifications from its primary expression or from any
 * predicate in its predicate list. The implementation of the evaluateBoolean method returns after
 * it finds the first node. Since most expressions occur inside of a predicate, which evaluates its
 * expression as a boolean, this should be much faster.
 */
public class FilteredExpression extends Expression
{
  /**
   * Create a filtered expression which filters the nodes in the node-set returned by
   * the lhs expression using the boolean result of the rhs predicate expression which
   * must be set separately as arguments.
   */
  public FilteredExpression()
  {
  }
  
  /**
   * Create a filtered expression which filters the nodes in the node-set returned by
   * the lhs expression using the boolean result of the rhs predicate expression.
   * @param lhs The expression returning the unfiltered node-set.
   * @param rhs The predicate expression which filters the node-set. 
   */
  public FilteredExpression( IExpression lhs, IExpression rhs)
  {
    addArgument( lhs);
    addArgument( rhs);
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IExpression#getName()
   */
  public String getName()
  {
    return "filtered";
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
    IExpression arg0 = getArgument( 0);
    IExpression arg1 = getArgument( 1);
    List<IModelObject> unfiltered = arg0.evaluateNodes( context);
    List<IModelObject> filtered = new ArrayList<IModelObject>( unfiltered.size());
    for ( int i=0; i<unfiltered.size(); i++)
    {
      IModelObject object = unfiltered.get( i);
      IContext filterContext = new SubContext( context, object, i+1, unfiltered.size());
      switch( arg1.getType( context))
      {
        case NODES:   
          filtered.addAll( arg1.evaluateNodes( filterContext));
          break;
        
        case BOOLEAN:
          if ( arg1.evaluateBoolean( filterContext))
            filtered.add( object);
          break;
      }
    }
    
    return filtered;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IExpression#evaluateBoolean(
   * dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  public boolean evaluateBoolean( IContext context) throws ExpressionException
  {
    IExpression arg0 = getArgument( 0);
    IExpression arg1 = getArgument( 1);
    List<IModelObject> unfiltered = arg0.evaluateNodes( context);
    for ( int i=0; i<unfiltered.size(); i++)
    {
      IModelObject object = unfiltered.get( i);
      IContext filterContext = new SubContext( context, object, i+1, unfiltered.size());
      switch( arg1.getType( context))
      {
        case NODES:   
          List<IModelObject> filtered = arg1.evaluateNodes( filterContext);
          if ( filtered.size() > 0) return true;
          break;
        
        case BOOLEAN:
          if ( arg1.evaluateBoolean( filterContext)) return true;
          break;
      }
    }
    
    return false;
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#createSubtree(dunnagan.bob.xmodel.xpath.expression.IContext, 
   * dunnagan.bob.xmodel.IModelObjectFactory, dunnagan.bob.xmodel.IChangeSet)
   */
  @Override
  public void createSubtree( IContext context, IModelObjectFactory factory, IChangeSet undo)
  {
    List<IModelObject> nodes = getArgument( 0).query( context, null);
    for( int i=0; i<nodes.size(); i++)
    {
      getArgument( 1).createSubtree( new SubContext( context, nodes.get( i), i+1, nodes.size()), factory, undo);
    }
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IExpression#bind(dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  public void bind( IContext context)
  {
    IExpression arg0 = getArgument( 0);
    IExpression arg1 = getArgument( 1);
    arg0.bind( context);

    List<IModelObject> nodes = arg0.evaluateNodes( context);
    for( int i=0; i<nodes.size(); i++)
    {
      IModelObject node = nodes.get( i);
      arg1.bind( new SubContext( context, node, i+1, nodes.size()));
    }
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IExpression#unbind(dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  public void unbind( IContext context)
  {
    IExpression arg0 = getArgument( 0);
    IExpression arg1 = getArgument( 1);
    arg0.unbind( context);

    List<IModelObject> nodes = arg0.evaluateNodes( context);
    for( int i=0; i<nodes.size(); i++)
    {
      IModelObject node = nodes.get( i);
      arg1.unbind( new SubContext( context, node, i+1, nodes.size()));
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
      List<IModelObject> lhsNodes = arg0.evaluateNodes( context);
      int index = Collections.indexOfSubList( lhsNodes, nodes);
      int count = lhsNodes.size();
      
      // optimize handling of input node-set changes
      if ( arg1.getType( context) == ResultType.BOOLEAN)
      {
        List<IModelObject> result = new ArrayList<IModelObject>( nodes.size());
        for( IModelObject node: nodes)
        {
          IContext filterContext = new SubContext(  context, node, (index++)+1, count);
          if ( arg1.evaluateBoolean( filterContext, false)) result.add( node);
          arg1.bind( filterContext);
        }
        
        // note that the ordering of binding vs. parent notification doesn't matter
        // because this optimization only applies to boolean expressions which do not
        // produce initial notifications.
        if ( result.size() > 0) parent.notifyAdd( this, context, result);
      }
      else
      {
        List<IModelObject> filterNodes = new ArrayList<IModelObject>();
        for( IModelObject node: nodes)
        {
          IContext filterContext = new SubContext(  context, node, (index++)+1, count);
          arg1.bind( filterContext);
          filterNodes.addAll( arg1.evaluateNodes( filterContext));
        }
        if ( filterNodes.size() > 0) parent.notifyAdd( this, context, filterNodes);
      }
    }
    else if ( expression == arg1)
    {
      // forward rhs notification to parent
      parent.notifyAdd( this, context.getParent(), nodes);
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
      context.getModel().revert();
      List<IModelObject> lhsNodes = arg0.evaluateNodes( context);
      
      context.getModel().restore();
      int index = Collections.indexOfSubList( lhsNodes, nodes);
      int count = lhsNodes.size();
      
      // optimize handling of input node-set changes
      if ( arg1.getType( context) == ResultType.BOOLEAN)
      {
        context.getModel().revert();
        List<IModelObject> result = new ArrayList<IModelObject>( nodes.size());
        for( IModelObject node: nodes)
        {
          IContext filterContext = new SubContext( context, node, (index++)+1, count);
          if ( arg1.evaluateBoolean( filterContext, false)) result.add( node);
          arg1.unbind( filterContext);
        }
        
        // note that the ordering of binding vs. parent notification doesn't matter
        // because this optimization only applies to boolean expressions which do not
        // produce initial notifications.
        context.getModel().restore();
        if ( result.size() > 0) parent.notifyRemove( this, context, result);
      }
      else
      {
        context.getModel().revert();
        List<IModelObject> filterNodes = new ArrayList<IModelObject>();
        for( IModelObject node: nodes)
        {
          IContext filterContext = new SubContext(  context, node, (index++)+1, count);
          arg1.unbind( filterContext);
          filterNodes.addAll( arg1.evaluateNodes( filterContext));
        }
        context.getModel().restore();
        if ( filterNodes.size() > 0) parent.notifyRemove( this, context, filterNodes);
      }
    }
    else if ( expression == arg1)
    {
      // forward rhs notification to parent
      parent.notifyRemove( this, context.getParent(), nodes);
    }
    else
    {
      throw new IllegalStateException( "Notification from expression which is not an argument.");
    }
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyChange(
   * dunnagan.bob.xmodel.xpath.expression.IExpression, dunnagan.bob.xmodel.xpath.expression.IContext, boolean)
   */
  @Override
  public void notifyChange( IExpression expression, IContext context, boolean newValue)
  {
    List<IModelObject> list = Collections.singletonList( context.getObject());
    if ( newValue)
      parent.notifyAdd( this, context.getParent(), list);
    else
      parent.notifyRemove( this, context.getParent(), list);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.Expression#notifyChange(
   * dunnagan.bob.xmodel.xpath.expression.IExpression, dunnagan.bob.xmodel.xpath.expression.IContext)
   */
  @Override
  public void notifyChange( IExpression expression, IContext context)
  {
    IModel model = context.getModel();
    switch( expression.getType( context))
    {
      case NODES:
      {
        // revert and reevaluate
        model.revert();
        Collection<IModelObject> oldNodes = expression.evaluateNodes( context);
        if ( oldNodes.size() > 3) oldNodes = new LinkedHashSet<IModelObject>( oldNodes);

        // restore and reevaluate
        model.restore();
        Collection<IModelObject> newNodes = expression.evaluateNodes( context);
        if ( newNodes.size() > 3) newNodes = new LinkedHashSet<IModelObject>( newNodes);

        // notify nodes removed
        List<IModelObject> removedSet = new ArrayList<IModelObject>( newNodes.size());
        for( IModelObject node: oldNodes) if ( !newNodes.contains( node)) removedSet.add( node);
        if ( removedSet.size() > 0) notifyRemove( expression, context, removedSet);
        
        // notify nodes added
        List<IModelObject> addedSet = new ArrayList<IModelObject>( newNodes.size());
        for( IModelObject node: newNodes) if ( !oldNodes.contains( node)) addedSet.add( node);
        if ( addedSet.size() > 0) notifyAdd( expression, context, addedSet);
      }
      break;
              
      case BOOLEAN:
      {
        // revert and reevaluate
        model.revert();
        boolean oldValue = expression.evaluateBoolean( context);

        // restore and reevaluate
        model.restore();
        boolean newValue = expression.evaluateBoolean( context);
        
        if ( newValue != oldValue) notifyChange( expression, context, newValue);
      }
      break;
    }      
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IExpression#requiresValueNotification(
   * dunnagan.bob.xmodel.xpath.expression.IExpression)
   */
  @Override
  public boolean requiresValueNotification( IExpression argument)
  {
    IExpression arg0 = getArgument( 0);
    IExpression arg1 = getArgument( 1);
    
    // need value notification if type of arg1 is undefined
    ResultType type1 = arg1.getType();
    if ( type1 == ResultType.UNDEFINED) return true;
    
    // need value notification from arg0 so arg1 can examine value
    if ( argument == arg0 && type1 == ResultType.BOOLEAN) return true;
    
    // need value notification from arg1 if arg1 returns nodes and parent needs value notification
    if ( argument == arg1 && type1 == ResultType.NODES && parent.requiresValueNotification( this)) return true;
    return false;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xpath.expression.IExpression#notifyValue(
   * dunnagan.bob.xmodel.xpath.expression.IContext[], dunnagan.bob.xmodel.IModelObject, 
   * java.lang.Object, java.lang.Object)
   */
  @Override
  public void notifyValue( IExpression expression, IContext[] contexts, IModelObject object, Object newValue, Object oldValue)
  {
    // unwind context
    if ( expression == getArgument( 1))
    {
      IContext[] parents = new IContext[ contexts.length];
      for( int i=0; i<parents.length; i++) parents[ i] = contexts[ i].getParent();
      contexts = parents;
    }

    // notify
    if ( expression.getType( contexts[ 0]) == ResultType.BOOLEAN)
    {
      object.getModel().revert();
      for( IContext context: contexts) unbind( context);
      
      object.getModel().restore();
      for( IContext context: contexts) bind( context);
  
      // reevaluate and notify listeners
      for( IContext context: contexts) notifyChange( this, context);
    }
    else
    {
      parent.notifyValue( this, contexts, object, newValue, oldValue);
    }
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    IExpression lhs = getArgument( 0);
    IExpression rhs = getArgument( 1);
    if ( rhs.getType() == ResultType.BOOLEAN)
    {
      return "("+lhs.toString()+")"+rhs.toString();
    }
    else if ( rhs instanceof PathExpression)
    {
      PathExpression pathExpression = (PathExpression)rhs;
      IPath path = pathExpression.getPath();
      IPathElement element = path.getPathElement( 0);
      if ( element.hasAxis( IAxis.DESCENDANT))
        return "("+lhs.toString()+")//"+rhs.toString();
      else
        return "("+lhs.toString()+")/"+rhs.toString();
    }
    else
    {
      return "("+lhs.toString()+")/("+rhs.toString()+")";
    }
  }
}