/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package org.xmodel.external.caching;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.xmodel.IModelObject;
import org.xmodel.Xlate;
import org.xmodel.external.CachingException;
import org.xmodel.external.ConfiguredCachingPolicy;
import org.xmodel.external.ICache;
import org.xmodel.external.IExternalReference;
import org.xmodel.xml.XmlIO;
import org.xmodel.xpath.expression.IContext;


public class URLCachingPolicy extends ConfiguredCachingPolicy
{
  /**
   * Create a URLCachingPolicy which uses the specified cache.
   * @param cache The cache.
   */
  public URLCachingPolicy( ICache cache)
  {
    super( cache);
    setStaticAttributes( new String[] { "id", "url"});
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.ConfiguredCachingPolicy#configure(org.xmodel.xpath.expression.IContext, org.xmodel.IModelObject)
   */
  @Override
  public void configure( IContext context, IModelObject annotation) throws CachingException
  {
    super.configure( context, annotation);
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.ConfiguredCachingPolicy#syncImpl(org.xmodel.external.IExternalReference)
   */
  @Override
  protected void syncImpl( IExternalReference reference) throws CachingException
  {
    String string = Xlate.get( reference, "url", (String)null);
    if ( string == null) return;

    XmlIO xmlIO = new XmlIO();
    xmlIO.setFactory( getFactory());
    
    URL url = null;
    try
    {
      url = new URL( string);
      IModelObject urlObject = reference.cloneObject();
      IModelObject rootTag = xmlIO.read( url.openStream());
      urlObject.addChild( rootTag);
      update( reference, urlObject);
    }
    catch( Exception e)
    {
      throw new CachingException( "Unable to sync url: "+url, e);
    }
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.AbstractCachingPolicy#getURI(org.xmodel.external.IExternalReference)
   */
  @Override
  public URI getURI( IExternalReference reference) throws CachingException
  {
    try
    {
      String url = Xlate.get( reference, "url", (String)null);
      if ( url == null) throw new CachingException( "External reference does not have a url attribute: "+reference); 
      return new URI( url);
    }
    catch( URISyntaxException e)
    {
      throw new CachingException( "Unable to create URI for external reference: "+reference, e);
    }
  }
}
