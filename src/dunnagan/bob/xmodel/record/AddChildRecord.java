/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.record;

import dunnagan.bob.xmodel.IModelObject;
import dunnagan.bob.xmodel.IPath;
import dunnagan.bob.xmodel.ModelAlgorithms;

/**
 * An implementation of IChangeRecord for adding children. If the path to the child does not exist
 * in the target object when the record is applied, then the path will be created.
 */
public class AddChildRecord extends AbstractChangeRecord
{
  /**
   * Create an unbound change record for the specified identity path. The change record represents
   * the adding of a child object.
   * @param path The identity path of the target object.
   * @param child The child to be removed.
   */
  public AddChildRecord( IPath path, IModelObject child)
  {
    super( path);
    this.child = child;
    index = -1;
  }
  
  /**
   * Create an unbound change record for the specified identity path. The change record represents
   * the adding of a child object at the specified index.
   * @param path The identity path of the target object.
   * @param child The child to be removed.
   * @param index The index where the child will be added.
   */
  public AddChildRecord( IPath path, IModelObject child, int index)
  {
    super( path);
    this.child = child;
    this.index = index;
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IChangeRecord#getType()
   */
  public int getType()
  {
    return ADD_CHILD;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IChangeRecord#getChild()
   */
  public IModelObject getChild()
  {
    return super.getChild();
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.transaction.AbstractChangeRecord#getIndex()
   */
  public int getIndex()
  {
    return index;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IChangeRecord#applyChange(dunnagan.bob.xmodel.IModelObject)
   */
  public void applyChange( IModelObject root)
  {
    if ( path == null) return;

    // create the subtree
    ModelAlgorithms.createPathSubtree( root, path, null, null);

    // apply change
    IModelObject target = path.queryFirst( root);
    if ( index < 0) target.addChild( child); else target.addChild( child, index);
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return "add child: "+child+", index: "+index+", path: "+path;
  }
  
  IModelObject child;
  int index;
}
