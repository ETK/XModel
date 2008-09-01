/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package org.xmodel.memento;

import org.xmodel.IModelObject;

/**
 * An IMemento for the updates which involve removing an attribute.
 */
public class RemoveAttributeMemento implements IMemento
{
  /* (non-Javadoc)
   * @see org.xmodel.memento.IMemento#revert()
   */
  public void revert()
  {
    object.revertUpdate( this);
  }

  /* (non-Javadoc)
   * @see org.xmodel.memento.IMemento#restore()
   */
  public void restore()
  {
    object.restoreUpdate( this);
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.memento.IMemento#clear()
   */
  public void clear()
  {
    object = null;
    attrName = null;
    oldValue = null;
  }
  
  public IModelObject object;
  public String attrName;
  public Object oldValue;
}
