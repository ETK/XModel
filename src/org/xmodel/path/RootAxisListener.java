/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * RootAxisListener.java
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
package org.xmodel.path;

import org.xmodel.INode;
import org.xmodel.IPath;
import org.xmodel.xpath.expression.IContext;

/**
 * An implementation of IFanoutListener for an IPathElement with the <i>ANCESTOR</i> axis.
 */
public class RootAxisListener extends FanoutListener
{
  public RootAxisListener( IListenerChain chain, int chainIndex)
  {
    super( chain, chainIndex);
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.path.FanoutListener#installListeners(org.xmodel.IModelObject)
   */
  protected void installListeners( INode object)
  {
    INode ancestor = object;
    while( ancestor != null)
    {
      object.addModelListener( this);
      ancestor = ancestor.getParent();
    }
  }

  /* (non-Javadoc)
   * @see org.xmodel.path.FanoutListener#uninstallListeners(org.xmodel.IModelObject)
   */
  protected void uninstallListeners( INode object)
  {
    INode ancestor = object;
    while( ancestor != null)
    {
      object.removeModelListener( this);
      ancestor = ancestor.getParent();
    }
  }

  /* (non-Javadoc)
   * @see org.xmodel.path.IListenerChainLink#cloneOne(org.xmodel.path.IListenerChain)
   */
  public IListenerChainLink cloneOne( IListenerChain chain)
  {
    return new RootAxisListener( chain, chainIndex);
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.path.ListenerChainLink#notifyParent(org.xmodel.IModelObject, 
   * org.xmodel.IModelObject, org.xmodel.IModelObject)
   */
  public void notifyParent( INode child, INode newParent, INode oldParent)
  {
    IPath path = getListenerChain().getPath();
    IContext context = getListenerChain().getContext();
    if ( oldParent != null)
    {
      uninstallListeners( oldParent);

      // uninstall next link
      INode root = oldParent.getRoot();
      if ( fanoutElement.evaluate( context, path, root))
        getNextListener().incrementalUninstall( root);
    }
    else
    {
      if ( fanoutElement.evaluate( context, path, child))
        getNextListener().incrementalUninstall( child);
    }
    
    if ( newParent != null)
    {
      installListeners( newParent);

      // install next link
      INode root = child.getRoot();
      if ( fanoutElement.evaluate( context, path, root))
        getNextListener().incrementalInstall( root);
    }
    else
    {
      if ( fanoutElement.evaluate( context, path, child))
        getNextListener().incrementalInstall( child);
    }
  }
}
