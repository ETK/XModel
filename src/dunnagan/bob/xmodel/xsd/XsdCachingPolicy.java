/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.xsd;

import java.net.URL;

import dunnagan.bob.xmodel.IModelObject;
import dunnagan.bob.xmodel.Xlate;
import dunnagan.bob.xmodel.external.CachingException;
import dunnagan.bob.xmodel.external.ConfiguredCachingPolicy;
import dunnagan.bob.xmodel.external.ICache;
import dunnagan.bob.xmodel.external.IExternalReference;
import dunnagan.bob.xmodel.xml.XmlIO;

/**
 * A caching policy for loading schemas.  The caching policy loads all imports and includes when
 * the reference is synchronized to form a complete schema with one root.  The target of the 
 * reference is defined in the <i>url</i> attribute.
 */
public class XsdCachingPolicy extends ConfiguredCachingPolicy
{
  /**
   * Create caching policy with the specified cache.
   * @param cache The cache.
   */
  public XsdCachingPolicy( ICache cache)
  {
    super( cache);
    setStaticAttributes( new String[] { "id", "url"});
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.external.ConfiguredCachingPolicy#syncImpl(dunnagan.bob.xmodel.external.IExternalReference)
   */
  public void syncImpl( IExternalReference reference) throws CachingException
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
      Xsd xsd = new Xsd( url);
      IModelObject rootTag = xsd.getRoot();
      urlObject.addChild( rootTag);
      update( reference, urlObject);
    }
    catch( Exception e)
    {
      throw new CachingException( "Unable to sync url: "+url, e);
    }
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.external.ICachingPolicy#flush(dunnagan.bob.xmodel.external.IExternalReference)
   */
  public void flush( IExternalReference reference) throws CachingException
  {
    throw new UnsupportedOperationException();
  }
}
