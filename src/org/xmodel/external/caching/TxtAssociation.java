/*
 * XModel (XML Application Data Modeling Framework)
 * Author: Bob Dunnagan (bdunnagan@nc.rr.com)
 * Copyright Bob Dunnagan 2009. All rights reserved.
 */
package org.xmodel.external.caching;

import java.io.File;
import org.xmodel.IModelObject;
import org.xmodel.external.CachingException;


/**
 * An IFileAssociation for various text file associations including <i>.txt</i> and associations for various 
 * programming language files such as html, java, perl and python.
 */
public class TxtAssociation implements IFileAssociation
{
  /* (non-Javadoc)
   * @see org.xmodel.external.caching.IFileAssociation#getAssociations()
   */
  public String[] getExtensions()
  {
    return extensions;
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.caching.IFileAssociation#apply(org.xmodel.IModelObject, java.io.File)
   */
  public void apply( IModelObject parent, File file) throws CachingException
  {
    try
    {
      
    }
    catch( Exception e)
    {
      throw new CachingException( "Unable read text file: "+file, e);
    }
  }
  
  private final static String[] extensions = { 
    ".txt", ".css", ".html", ".htm", ".java", ".rtf", ".pl", ".py"};
}
