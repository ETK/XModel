/*
 * Stonewall Networks, Inc.
 *
 *   Project: XModel
 *   Author:  bdunnagan
 *   Date:    Mar 12, 2008
 *
 * Copyright 2008.  Stonewall Networks, Inc.
 */
package org.xmodel.external.caching;

import java.util.Properties;
import org.xmodel.IModelObject;
import org.xmodel.external.CachingException;
import org.xmodel.external.ConfiguredCachingPolicy;
import org.xmodel.external.ICache;
import org.xmodel.external.IExternalReference;


/**
 * A caching policy which converts the system environment variables into elements. Each environment
 * variable is stored in a <i>property</i> element which has a <i>name</i> attribute and whose value
 * is the value of the Java system property.
 */
public class EnvironmentCachingPolicy extends ConfiguredCachingPolicy
{
  public EnvironmentCachingPolicy( ICache cache)
  {
    super( cache);
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.ConfiguredCachingPolicy#syncImpl(org.xmodel.external.IExternalReference)
   */
  @Override
  protected void syncImpl( IExternalReference reference) throws CachingException
  {
    reference.removeChildren();
    Properties properties = System.getProperties();
    for( Object key: properties.keySet())
    {
      IModelObject property = getFactory().createObject( reference, "property");
      property.setAttribute( "id", key);
      property.setValue( properties.get( key));
      reference.addChild( property);
    }
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.ICachingPolicy#flush(org.xmodel.external.IExternalReference)
   */
  public void flush( IExternalReference reference) throws CachingException
  {
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.ICachingPolicy#insert(org.xmodel.external.IExternalReference, 
   * org.xmodel.IModelObject, int, boolean)
   */
  public void insert( IExternalReference parent, IModelObject object, int index, boolean dirty) throws CachingException
  {
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.ICachingPolicy#remove(org.xmodel.external.IExternalReference, org.xmodel.IModelObject)
   */
  public void remove( IExternalReference parent, IModelObject object) throws CachingException
  {
  }
}
