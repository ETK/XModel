/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * ListenerChain.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xmodel.IModelObject;
import org.xmodel.IPath;
import org.xmodel.IPathElement;
import org.xmodel.IPathListener;
import org.xmodel.path.PathListenerTraceEntry.Event;
import org.xmodel.xpath.XPath;
import org.xmodel.xpath.expression.IContext;
import org.xmodel.xpath.expression.IExpression;
import org.xmodel.xpath.expression.PathExpression;

import static org.xmodel.IAxis.*;

public class ListenerChain implements IListenerChain
{
  /**
   * Create a clone of the specified chain.
   * @param chain The chain.
   */
  public ListenerChain( IPath path, IListenerChain chain, IContext context, IPathListener listener)
  {
    this.path = path;
    this.context = context;
    this.listener = listener;
    this.installed = false;
    
    // clone links
    IListenerChainLink[] links = chain.getLinks();
    this.links = new IListenerChainLink[ links.length];
    for( int i=0; i<links.length; i++) this.links[ i] = links[ i].cloneOne( this);
 }
  
  /**
   * Create a listener chain with listeners at the specified priority.
   * @param path The path.
   * @param context The context.
   * @param listener The path listener.
   */
  public ListenerChain( IPath path, IContext context, IPathListener listener)
  {
    this.path = path;
    this.context = context;
    this.listener = listener;
    this.installed = false;
    buildListenerChain();
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.path.IListenerChain#getListeners()
   */
  public IListenerChainLink[] getLinks()
  {
    if ( links == null) return new IListenerChainLink[ 0];
    return links;
  }

  /* (non-Javadoc)
   * @see org.xmodel.path.IListenerChain#getPath()
   */
  public IPath getPath()
  {
    return path;
  }

  /* (non-Javadoc)
   * @see org.xmodel.path.IListenerChain#getContext()
   */
  public IContext getContext()
  {
    return context;
  }

  /* (non-Javadoc)
   * @see org.xmodel.path.IListenerChain#getPathListener()
   */
  public IPathListener getPathListener()
  {
    return installed? listener: null;
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.path.IListenerChain#install(org.xmodel.IModelObject)
   */
  public void install( IModelObject object)
  {
    if ( installed) return;
    installed = true;

    // notify each link that the context is being bound (see PredicateGuard)
    for( IListenerChainLink link: links) link.bind( context);
    
    // install first link
    if ( links.length > 0)
    {
      List<IModelObject> list = new ArrayList<IModelObject>( 1);
      list.add( object);
      links[ 0].install( list);
    }
  }

  /* (non-Javadoc)
   * @see org.xmodel.path.IListenerChain#uninstall(org.xmodel.IModelObject)
   */
  public void uninstall( IModelObject object)
  {
    if ( !installed) return;
    
    // notify each link that the context is being unbound (see PredicateGuard)
    for( IListenerChainLink link: links) link.unbind( context);
    
    // install first link
    if ( links.length > 0)
    {
      List<IModelObject> list = new ArrayList<IModelObject>( 1);
      list.add( object);
      links[ 0].uninstall( list);
    }
    installed = false;
  }

  /**
   * Trace the specified path and return the trace object.
   * @param spec The path or expression to be traced.
   * @return Returns the object which manages the trace.
   */
  public static PathListenerTrace trace( String spec)
  {
    synchronized( traces)
    {
      IPath compiled = XPath.createPath( spec);
      if ( compiled == null)
      {
        IExpression expression = XPath.createExpression( spec);
        if ( expression == null) return null;
        spec = expression.toString();
      }
      else
      {
        spec = compiled.toString();
      }
          
      PathListenerTrace trace = traces.get( spec);
      if ( trace == null)
      {
        trace = new PathListenerTrace();
        traces.put( spec, trace);
      }
      return trace;
    }
  }
  
  /**
   * Clear all traces.
   */
  public static void clearTraces()
  {
    synchronized( traces)
    {
      traces.clear();
    }
  }

  /* (non-Javadoc)
   * @see org.xmodel.path.IListenerChain#debugInstall(java.util.List, int)
   */
  public void debugInstall( List<IModelObject> list, int pathIndex)
  {
    if ( list.size() == 0) return;
    PathListenerTrace trace = getTrace();
    if ( trace != null && (!trace.isLeavesOnly() || pathIndex == path.length()))
    {
      PathListenerTraceEntry entry = new PathListenerTraceEntry( Event.install, path, pathIndex);
      entry.addTargets( list);
      trace.addEntry( entry);
    }
  }

  /* (non-Javadoc)
   * @see org.xmodel.path.IListenerChain#debugUninstall(java.util.List, int)
   */
  public void debugUninstall( List<IModelObject> list, int pathIndex)
  {
    if ( list.size() == 0) return;
    PathListenerTrace trace = getTrace();
    if ( trace != null && (!trace.isLeavesOnly() || pathIndex == path.length()))
    {
      PathListenerTraceEntry entry = new PathListenerTraceEntry( Event.remove, path, pathIndex);
      entry.addTargets( list);
      trace.addEntry( entry);
    }
  }

  /* (non-Javadoc)
   * @see org.xmodel.path.IListenerChain#debugIncrementalInstall(java.util.List, int)
   */
  public void debugIncrementalInstall( List<IModelObject> list, int pathIndex)
  {
    PathListenerTrace trace = getTrace();
    if ( trace != null && (!trace.isLeavesOnly() || pathIndex == path.length()))
    {
      PathListenerTraceEntry entry = new PathListenerTraceEntry( Event.install, path, pathIndex);
      entry.addTargets( list);
      trace.addEntry( entry);
    }
  }

  /* (non-Javadoc)
   * @see org.xmodel.path.IListenerChain#debugIncrementalUninstall(java.util.List, int)
   */
  public void debugIncrementalUninstall( List<IModelObject> list, int pathIndex)
  {
    PathListenerTrace trace = getTrace();
    if ( trace != null && (!trace.isLeavesOnly() || pathIndex == path.length()))
    {
      PathListenerTraceEntry entry = new PathListenerTraceEntry( Event.remove, path, pathIndex);
      entry.addTargets( list);
      trace.addEntry( entry);
    }
  }
  
  /**
   * Returns the trace for the path or for its enclosing expression.
   * @return Returns the trace for the path or for its enclosing expression.
   */
  private PathListenerTrace getTrace()
  {
    if ( traces.size() > 0)
    {
      PathListenerTrace trace = traces.get( path.toString());
//      if ( trace == null)
//      {
//        ExpressionNavigator navigator = new ExpressionNavigator( this);
//        if ( navigator != null)
//        {
//          navigator = navigator.getHead();
//          if ( navigator != null) 
//            trace = traces.get( navigator.toString());
//        }
//      }
      return trace;
    }
    return null;
  }

  /**
   * Add the appropriate IListenerChainLink instances to the chain for the associated path.
   */
  private void buildListenerChain()
  {
    List<IListenerChainLink> links = new ArrayList<IListenerChainLink>( path.length());
    for ( int i=0; i<=path.length(); i++)
    {
      IListenerChainLink link = createFanoutListener( i);
      if ( i > 0)
      {
        IPathElement pathElement = path.getPathElement( i-1);
        if ( pathElement.predicate() != null) link = new PredicateGuard( link);
      }
      links.add( link);
    }
    this.links = links.toArray( new IListenerChainLink[ 0]);
  }

  /**
   * Create the appropriate subclass of FanoutListener for the path element with the given index.
   * @param pathIndex The index of the path element.
   * @return Returns a subclass of FanoutListener appropriate for the specified path element.
   */
  private IListenerChainLink createFanoutListener( int pathIndex)
  {
    if ( pathIndex < path.length())
    {
      IPathElement pathElement = path.getPathElement( pathIndex);
      switch( pathElement.axis())
      {
        case ANCESTOR|SELF:
        case ANCESTOR:          return new AncestorAxisListener( this, pathIndex);
        case ATTRIBUTE:         return new AttributeAxisListener( this, pathIndex);
        case CHILD:             return new ChildAxisListener( this, pathIndex);
        case DESCENDANT|SELF:
        case DESCENDANT:        return new DescendantAxisListener( this, pathIndex);
//        case FOLLOWING:         return new FollowingAxisListener( this, pathIndex);
//        case FOLLOWING_SIBLING: return new FollowingSiblingAxisListener( this, pathIndex);
//        case PRECEDING:         return new PrecedingAxisListener( this, pathIndex);
//        case PRECEDING_SIBLING: return new PrecedingSiblingAxisListener( this, pathIndex);
        case NESTED|SELF:
        case NESTED:            return new NestedAxisListener( this, pathIndex);
        case PARENT:            return new ParentAxisListener( this, pathIndex);
        case ROOT:              return new RootAxisListener( this, pathIndex);
        case SELF:              return new SelfAxisListener( this, pathIndex);
        default:                throw new IllegalArgumentException( "Unsupported axis: "+pathElement);
      }
    }
    else
    {
      return new ChainTerminal( this);
    }
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    return path.hashCode() + context.hashCode() + listener.hashCode();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object object)
  {
    try
    {
      IListenerChain chain = (IListenerChain)object;
      if ( chain.getPath() != path) return false;
      if ( chain.getPathListener() != listener) return false;
      if ( !chain.getContext().equals( context)) return false;
      return true;
    }
    catch( ClassCastException e)
    {
      return false;
    }
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    if ( listener instanceof PathExpression)
      builder.append( ((PathExpression)listener).getPath());
    else   
      builder.append( listener.getClass().getSimpleName());
    
    builder.append( ", "); builder.append( context.getObject());
    return builder.toString();
  }
  
  static Map<String, PathListenerTrace> traces = new HashMap<String, PathListenerTrace>();
  
  private IListenerChainLink[] links;
  private IPath path;
  private IContext context;
  private IPathListener listener;
  private boolean installed;
}
