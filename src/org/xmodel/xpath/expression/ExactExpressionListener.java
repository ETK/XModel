/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * ExactExpressionListener.java
 * 
 * Copyright 2009 Robert Arvin Dunnagan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xmodel.xpath.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xmodel.IModel;
import org.xmodel.IModelObject;
import org.xmodel.diff.AbstractListDiffer;
import org.xmodel.xpath.expression.IExpression.ResultType;


/**
 * An implementation of IExpression which provides more precise notification for expressions which
 * return a node-set. Typically, the context of nodes which are incrementally added or removed is
 * not provided. This listener reevaluates the entire expression each time the node-set changes and
 * determines the position and node-set size. This complete context information is provided via two
 * new notification methods.
 * <p>
 * When using this class, there is no reason to override the <code>notifyAdd</code> or
 * <code>notifyRemove</code> methods from IExpression. Instead, override the methods which have
 * the <code>NodeUpdate<code> argument. Whenever possible, use the <code>ExpressionListener<code>
 * class instead.
 * <p>
 * The <code>notifyChange( IExpression, IContext)</code> method does not need to be overridden. The
 * implementation will provide notification to one of the other notification methods.
 * FIXME: ExactExpressionListener has state which can be corrupted by nested listener notification.
 * @deprecated This class is tries to do its work in the ListDiffer. 
 */
public abstract class ExactExpressionListener extends ExpressionListener
{
  /**
   * Called when nodes are inserted into the node-set of the specified expression. Override this
   * method to receive notification when nodes are added to the node-set of the expression.
   * @param expression The expression.
   * @param context The context.
   * @param nodes The node-set containing the inserts.
   * @param start The start of the new nodes.
   * @param count The count of new nodes.
   */
  public abstract void notifyInsert( IExpression expression, IContext context, List<IModelObject> nodes, int start, int count);

  /**
   * Called when nodes are removed from the node-set of the specified expression. Override this
   * method to receive notification when nodes are removed to the node-set of the expression.
   * @param expression The expression.
   * @param context The context.
   * @param nodes The complete node-set.
   * @param start The start of the removed nodes.
   * @param count The count of removed nodes.
   */
  public abstract void notifyRemove( IExpression expression, IContext context, List<IModelObject> nodes, int start, int count);

  /* (non-Javadoc)
   * @see org.xmodel.xpath.expression.IExpressionListener#notifyAdd(
   * org.xmodel.xpath.expression.IExpression, org.xmodel.xpath.expression.IContext, 
   * java.util.List)
   */
  public void notifyAdd( IExpression expression, IContext context, List<IModelObject> inserts)
  {
    IModel model = context.getModel();
    long lastUpdate = context.getLastUpdate( expression);
    if ( context.shouldUpdate( expression))
    {
      context.notifyUpdate( expression);
      try
      {
        // revert and reevaluate
        // lastUpdateID == 0 means context has never been notified
        List<IModelObject> oldNodes = Collections.emptyList();
        if ( lastUpdate != 0)
        {
          model.revert();
          oldNodes = expression.evaluateNodes( context);
        }
  
        // restore and reevaluate
        model.restore();
        List<IModelObject> newNodes = expression.evaluateNodes( context);

        // difference lists and notify
        this.expression = expression;
        this.context = context;
        ListDiffer differ = new ListDiffer();
        differ.diff( oldNodes, newNodes);
      }
      catch( ExpressionException e)
      {
        handleException( expression, context, e);
      }
    }
  }

  /* (non-Javadoc)
   * @see org.xmodel.xpath.expression.IExpressionListener#notifyRemove(
   * org.xmodel.xpath.expression.IExpression, org.xmodel.xpath.expression.IContext, 
   * java.util.List)
   */
  public void notifyRemove( IExpression expression, IContext context, List<IModelObject> deletes)
  {
    IModel model = context.getModel();
    long lastUpdate = context.getLastUpdate( expression);
    if ( context.shouldUpdate( expression))
    {
      context.notifyUpdate( expression);
      try
      {
        // revert and reevaluate
        model.revert();
        List<IModelObject> oldNodes = expression.evaluateNodes( context);
        model.restore();
  
        // restore and reevaluate
        List<IModelObject> newNodes = Collections.emptyList();
        if ( lastUpdate != 0) 
          newNodes = expression.evaluateNodes( context);

        // difference lists and notify
        this.expression = expression;
        this.context = context;
        ListDiffer differ = new ListDiffer();
        differ.diff( oldNodes, newNodes);
      }
      catch( ExpressionException e)
      {
        handleException( expression, context, e);
      }
    }
  }

  /* (non-Javadoc)
   * @see org.xmodel.xpath.expression.IExpressionListener#notifyChange(
   * org.xmodel.xpath.expression.IExpression, org.xmodel.xpath.expression.IContext)
   */
  public void notifyChange( IExpression expression, IContext context)
  {
    IModel model = context.getModel();
    long lastUpdate = context.getLastUpdate( expression);
    if ( expression.getType( context) == ResultType.NODES)
    {
      try
      {
        List<IModelObject> oldNodes = Collections.emptyList();
        
        // revert and reevaluate
        if ( lastUpdate != 0)
        {
          model.revert();
          oldNodes = expression.evaluateNodes( context);
        }
  
        // restore and reevaluate
        model.restore();
        List<IModelObject> newNodes = expression.evaluateNodes( context);

        // difference lists and notify
        this.expression = expression;
        this.context = context;
        ListDiffer differ = new ListDiffer();
        differ.diff( oldNodes, newNodes);
      }
      catch( ExpressionException e)
      {
        handleException( expression, context, e);
      }
    }
    else
    {
      super.notifyChange( expression, context);
    }
  }

  @SuppressWarnings("unchecked")
  private class ListDiffer extends AbstractListDiffer
  {
    public void diff( List lhs, List rhs)
    {
      nodes = new ArrayList<IModelObject>( lhs);
      super.diff( lhs, rhs);
      nodes = null;
    }
    public void notifyInsert( final List lhs, int lIndex, int lAdjust, final List rhs, int rIndex, int count)
    {
      try
      {
        int start = lIndex + lAdjust;
        nodes.addAll( start, rhs.subList( rIndex, rIndex+count));
        ExactExpressionListener.this.notifyInsert( expression, context, nodes, start, count);
      }
      catch( Exception e)
      {
        ExactExpressionListener.this.handleException( expression, context, e);
      }
    }
    public void notifyRemove( final List lhs, int lIndex, int lAdjust, final List rhs, int count)
    {
      try
      {
        int start = lIndex + lAdjust;
        ExactExpressionListener.this.notifyRemove( expression, context, nodes, start, count);
        for( int i=0; i<count; i++) nodes.remove( start);
      }
      catch( Exception e)
      {
        ExactExpressionListener.this.handleException( expression, context, e);
      }
    }
    
    List<IModelObject> nodes;
  };
    
  // current context and expression
  IExpression expression;
  IContext context;
}
