/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.listeners;

import java.util.List;

import dunnagan.bob.xmodel.*;
import dunnagan.bob.xmodel.xpath.expression.IContext;

/**
 * A convenience class for notification for the addition and removal of leaves of the specified
 * model path. The notification happens regardless of how the object is added to or removed from the
 * model. The path may contain elements with any axis including ANCESTOR and DECENDENT.
 */
public class LeafListener extends ModelListener implements IPathListener
{
  /**
   * Create a LeafListener.
   */
  public LeafListener()
  {
    listener = this;
  }

  /**
   * Create a LeafListener which installs the given IModelListener on the leaves. The listener
   * will automatically be uninstalled when the leaf is removed from the path.
   * @param listener The listener to be installed.
   */
  public LeafListener( IModelListener listener)
  {
    this.listener = listener;
  }
  
  /**
   * Called when a domain object on the specified model path is added.
   * @param context The root of the path query.
   * @param path The model path of the domain object.
   * @param object The domain object which was added.
   */
  public void notifyAdd( IContext context, IPath path, List<IModelObject> nodes)
  {
  }

  /**
   * Called when a domain object on the specified model path is removed.
   * @param context The root of the path query.
   * @param path The model path of the domain object.
   * @param object The domain object which was removed.
   */
  public void notifyRemove( IContext context, IPath path, List<IModelObject> nodes)
  {
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IPathListener#notifyAdd(dunnagan.bob.xmodel.xpath.expression.IContext, 
   * dunnagan.bob.xmodel.IPath, int, java.util.List)
   */
  public void notifyAdd( IContext context, IPath path, int pathIndex, List<IModelObject> nodes)
  {
    if ( pathIndex == path.length()) 
    {
      for( IModelObject node: nodes) node.addModelListener( listener);
      notifyAdd( context, path, nodes);
    }
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IPathListener#notifyRemove(dunnagan.bob.xmodel.xpath.expression.IContext, 
   * dunnagan.bob.xmodel.IPath, int, java.util.List)
   */
  public void notifyRemove( IContext context, IPath path, int pathIndex, List<IModelObject> nodes)
  {
    if ( pathIndex == path.length()) 
    {
      notifyRemove( context, path, nodes);
      for( IModelObject node: nodes) node.removeModelListener( listener);
    }
  }
  
  IModelListener listener;
}
