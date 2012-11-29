/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * ExternalElement.java
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
package org.xmodel.external;

import org.xmodel.Element;
import org.xmodel.IModel;
import org.xmodel.IModelObject;

/**
 * An implementation of IExternalReference which extends Element and does not provide listener semantics.
 */
public class ExternalElement extends Element implements IExternalReference
{  
  /**
   * Create the ExternalReference with the specified type which is not dirty.
   * @param type The type of this reference.
   */
  public ExternalElement( String type)
  {
    super( type);
    dirty = false;
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.external.IExternalReference#setCachingPolicy(org.xmodel.external.ICachingPolicy)
   */
  public void setCachingPolicy( ICachingPolicy newCachingPolicy)
  {
    if ( cachingPolicy != null)
    {
      try { clearCache();} catch( CachingException e) {}
      ICache cache = cachingPolicy.getCache();
      if ( cache != null) cache.remove( this);
    }
    cachingPolicy = newCachingPolicy;
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.IExternalReference#getStaticAttributes()
   */
  public String[] getStaticAttributes()
  {
    return getCachingPolicy().getStaticAttributes();
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.IExternalReference#setDirty(boolean)
   */
  public void setDirty( boolean dirty)
  {
    this.dirty = dirty;
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.IExternalReference#isDirty()
   */
  public boolean isDirty()
  {
    return dirty;
  }

  /* (non-Javadoc)
   * @see org.xmodel.reference.IExternalObject#getCachingPolicy()
   */
  public ICachingPolicy getCachingPolicy()
  {
    return cachingPolicy;
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.IExternalReference#sync()
   */
  public void sync() throws CachingException
  {
    ICachingPolicy cachingPolicy = getCachingPolicy();
    if ( cachingPolicy == null) throw new CachingException( "No caching policy to sync entity: "+this);
    setDirty( false);
    cachingPolicy.sync( this);
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.IExternalReference#transaction()
   */
  @Override
  public ITransaction transaction()
  {
    ICachingPolicy cachingPolicy = getCachingPolicy();
    if ( cachingPolicy == null) throw new CachingException( "No caching policy for this entity: "+this);
    return cachingPolicy.transaction();
  }

  /* (non-Javadoc)
   * @see org.xmodel.Element#notifyAccessAttributes(java.lang.String, boolean)
   */
  @Override
  protected void notifyAccessAttributes( String name, boolean write)
  {
    if ( cachingPolicy != null) cachingPolicy.notifyAccessAttributes( this, name, write);
  }

  /* (non-Javadoc)
   * @see org.xmodel.Element#notifyAccessChildren(boolean)
   */
  @Override
  protected void notifyAccessChildren( boolean write)
  {
    if ( cachingPolicy != null) cachingPolicy.notifyAccessChildren( this, write);
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.IExternalReference#clearCache()
   */
  public void clearCache() throws CachingException
  {
    ICachingPolicy cachingPolicy = getCachingPolicy();
    if ( cachingPolicy == null) throw new CachingException( "No caching policy to clear entity: "+this);
    cachingPolicy.clear( this);
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.Element#createObject(java.lang.String)
   */
  @Override
  public IModelObject createObject( String type)
  {
    return new ExternalElement( type);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  /* (non-Javadoc)
   * @see org.xmodel.external.IExternalReference#toString(java.lang.String)
   */
  public String toString( String indent)
  {
    IModel model = getModel();
    boolean wasSyncLocked = model.getSyncLock();
    model.setSyncLock( true);
    try
    {
      StringBuilder sb = new StringBuilder();
      sb.append( indent); sb.append( "&"); sb.append( super.toString()); sb.append( "\n");
      sb.append( getCachingPolicy().toString( indent+"  "));
      return sb.toString();
    }
    finally
    {
      model.setSyncLock( wasSyncLocked);
    }
  }

  /* (non-Javadoc)
   * @see org.xmodel.ModelObject#toString()
   */
  public String toString()
  {
    return toString( "");
  }

  private ICachingPolicy cachingPolicy;
  private boolean dirty;
}
