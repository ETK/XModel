/*
 * XModel (XML Application Data Modeling Framework)
 * Author: Bob Dunnagan (bdunnagan@nc.rr.com)
 * Copyright Bob Dunnagan 2009. All rights reserved.
 */
package org.xmodel;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.xmodel.util.Fifo;


/**
 * An iterator which visits all of the preceding elements of the starting element (see preceding axis).
 * References are only visited once to prevent infinite loops.
 */
public class PrecedingIterator implements Iterator<IModelObject>
{
  public PrecedingIterator( IModelObject root)
  {
    fifo = new Fifo<IModelObject>();
    pushPrevious( root);
    references = new HashSet<IModelObject>();
  }
  
  /* (non-Javadoc)
   * @see java.util.Iterator#hasNext()
   */
  public boolean hasNext()
  {
    while( !fifo.empty() && !shouldTraverse( fifo.peek())) fifo.pop();
    return !fifo.empty();
  }

  /* (non-Javadoc)
   * @see java.util.Iterator#next()
   */
  public IModelObject next()
  {
    IModelObject object = (IModelObject)fifo.pop();
    pushPrevious( object);
    return object;
  }
  
  /**
   * Push the previous element following the specified object.
   * @param object The object.
   */
  private void pushPrevious( IModelObject object)
  {
    // push next sibling
    if ( !pushPreviousSibling( object))
    {
      // else push parent sibling
      IModelObject parent = object.getParent();
      if ( parent != null) pushPreviousSibling( parent);
    }
  }
  
  /**
   * Push the previous sibling of the specified object.
   * @param object The object.
   * @return Returns true if a sibling was found.
   */
  private boolean pushPreviousSibling( IModelObject object)
  {
    IModelObject parent = object.getParent();
    if ( parent != null)
    {
      List<IModelObject> siblings = parent.getChildren();
      int index = siblings.indexOf( object) - 1;
      if ( index >= 0) 
      {
        fifo.push( siblings.get( index));
        return true;
      }
    }
    return false;
  }

  /* (non-Javadoc)
   * @see java.util.Iterator#remove()
   */
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
  
  /**
   * Returns true if the specified object should be traversed.
   * @param object The object.
   * @return Returns true if the specified object should be traversed.
   */
  protected boolean shouldTraverse( IModelObject object)
  {
    IModelObject referent = object.getReferent();
    if ( referent != object)
    {
      if ( references.contains( object)) return false;
      references.add( object);
    }
    return true;
  }
  
  private Fifo<IModelObject> fifo;
  private Set<IModelObject> references;
}
