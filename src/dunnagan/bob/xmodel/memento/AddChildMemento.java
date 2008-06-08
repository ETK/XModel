/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.memento;

import dunnagan.bob.xmodel.IModelObject;

/**
 * An IMemento for the updates which involve adding a child.
 */
public class AddChildMemento implements IMemento
{
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.memento.IMemento#revert()
   */
  public void revert()
  {
    if ( parent != null)
      parent.revertUpdate( this);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.memento.IMemento#restore()
   */
  public void restore()
  {
    if ( parent != null)
      parent.restoreUpdate( this);
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.memento.IMemento#clear()
   */
  public void clear()
  {
    parent = null;
    child = null;
    index = 0;
  }
  
  public IModelObject parent;
  public IModelObject child;
  public int index;
}
