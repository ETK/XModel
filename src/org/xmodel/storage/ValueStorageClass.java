package org.xmodel.storage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.xmodel.IModel;
import org.xmodel.IModelObject;
import org.xmodel.ModelListenerList;
import org.xmodel.PathListenerList;
import org.xmodel.external.ICachingPolicy;

/**
 * An IStorageClass that only stores the element value.
 */
public final class ValueStorageClass implements IStorageClass
{
  /* (non-Javadoc)
   * @see org.xmodel.storage.IStorageClass#storesModel()
   */
  @Override
  public boolean storesModel()
  {
    return false;
  }

  /* (non-Javadoc)
   * @see org.xmodel.storage.IStorageClass#setModel(org.xmodel.IModel)
   */
  @Override
  public void setModel( IModel model)
  {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see org.xmodel.storage.IStorageClass#getModel(org.xmodel.IModelObject)
   */
  @Override
  public IModel getModel()
  {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see org.xmodel.storage.IStorageClass#storesCachingPolicy()
   */
  @Override
  public boolean storesCachingPolicy()
  {
    return false;
  }

  /* (non-Javadoc)
   * @see org.xmodel.storage.IStorageClass#setDirty(boolean)
   */
  @Override
  public void setDirty( boolean dirty)
  {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see org.xmodel.storage.IStorageClass#getDirty()
   */
  @Override
  public boolean getDirty()
  {
    return false;
  }

  /* (non-Javadoc)
   * @see org.xmodel.storage.IStorageClass#setCachingPolicy(org.xmodel.external.ICachingPolicy)
   */
  @Override
  public void setCachingPolicy( ICachingPolicy cachingPolicy)
  {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see org.xmodel.storage.IStorageClass#getCachingPolicy()
   */
  @Override
  public ICachingPolicy getCachingPolicy()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see org.xmodel.storage.IStorageClass#storesChildren()
   */
  @Override
  public boolean storesChildren()
  {
    return false;
  }

  /* (non-Javadoc)
   * @see org.xmodel.storage.IStorageClass#getChildren()
   */
  @Override
  public List<IModelObject> getChildren()
  {
    return Collections.emptyList();
  }

  /* (non-Javadoc)
   * @see org.xmodel.storage.IStorageClass#storesAttributes(java.lang.String)
   */
  @Override
  public boolean storesAttributes( String name)
  {
    return (name.length() == 0);
  }

  /* (non-Javadoc)
   * @see org.xmodel.storage.IStorageClass#setAttribute(java.lang.String, java.lang.Object)
   */
  @Override
  public Object setAttribute( String attrName, Object attrValue)
  {
    if ( attrName.length() > 0) throw new UnsupportedOperationException();
    Object previous = value;
    value = attrValue;
    return previous;
  }

  /* (non-Javadoc)
   * @see org.xmodel.storage.IStorageClass#getAttribute(java.lang.String)
   */
  @Override
  public Object getAttribute( String attrName)
  {
    if ( attrName.length() > 0) throw new UnsupportedOperationException();
    return value;
  }

  /* (non-Javadoc)
   * @see org.xmodel.storage.IStorageClass#getAttributeNames()
   */
  @Override
  public Collection<String> getAttributeNames()
  {
    return (value != null)? attributes: Collections.<String>emptyList();
  }

  /* (non-Javadoc)
   * @see org.xmodel.storage.IStorageClass#storesModelListeners()
   */
  @Override
  public boolean storesModelListeners()
  {
    return false;
  }

  /* (non-Javadoc)
   * @see org.xmodel.storage.IStorageClass#getModelListeners()
   */
  @Override
  public ModelListenerList getModelListeners()
  {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see org.xmodel.storage.IStorageClass#storesPathListeners()
   */
  @Override
  public boolean storesPathListeners()
  {
    return false;
  }

  /* (non-Javadoc)
   * @see org.xmodel.storage.IStorageClass#getPathListeners()
   */
  @Override
  public PathListenerList getPathListeners()
  {
    throw new UnsupportedOperationException();
  }
  
  private final static List<String> attributes = Collections.singletonList( "");
  
  protected Object value;
}
