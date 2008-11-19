/*
 * XModel (XML Application Data Modeling Framework)
 * Author: Bob Dunnagan (bdunnagan@nc.rr.com)
 * Copyright Bob Dunnagan 2009. All rights reserved.
 */
package org.xmodel.external.caching;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xmodel.IModelObject;
import org.xmodel.ModelAlgorithms;
import org.xmodel.Xlate;
import org.xmodel.external.CachingException;
import org.xmodel.external.ConfiguredCachingPolicy;
import org.xmodel.external.ICache;
import org.xmodel.external.IExternalReference;
import org.xmodel.xpath.XPath;
import org.xmodel.xpath.expression.IExpression;


/**
 * A caching policy which will populate its external reference with references to all of the XML
 * files in a specified folder. The XML files are identified by the ".xml" extension. This 
 * implementation does not support locking.
 */
public class FolderCachingPolicy extends ConfiguredCachingPolicy
{
  /**
   * Create a FolderCachingPolicy which uses the specified cache.
   * @param cache The cache.
   */
  public FolderCachingPolicy( ICache cache)
  {
    super( cache);
    setStaticAttributes( new String[] { "id", "type", "path"});

    fileCachingPolicy = new FileCachingPolicy( cache);
    
    defineNextStage( folderPath, this, true);
    defineNextStage( filePath, fileCachingPolicy, true);
    
    // create default filter
    filter = new FilenameFilter() {
      public boolean accept( File dir, String name)
      {
        File file = new File( dir, name);
        if ( file.isDirectory()) return true;
        return name.endsWith( ".xml");
      }
    };
  }

  /**
   * Set the FilenameFilter used to filter files in a folder.
   * @param filter The filter.
   */
  public void setFilenameFilter( FilenameFilter filter)
  {
    this.filter = filter;
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.external.ICachingPolicy#checkout(
   * org.xmodel.external.IExternalReference)
   */
  public void checkout( IExternalReference reference)
  {
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.ICachingPolicy#checkin(
   * org.xmodel.external.IExternalReference)
   */
  public void checkin( IExternalReference reference)
  {
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.ICachingPolicy#sync(
   * org.xmodel.external.IExternalReference)
   */
  public void syncImpl( IExternalReference reference) throws CachingException
  {
    String path = Xlate.get( reference, "path", "");
    File folder = new File( path);
    if ( folder.isDirectory())
    {
      File[] files = folder.listFiles( filter);
      try
      {
        IModelObject newFolder = getFactory().createObject( null, reference.getType());
        ModelAlgorithms.copyAttributes( reference, newFolder);
        for( File file: files)
        {
          if ( file.isDirectory())
          {
            IModelObject folderEntry = getFactory().createObject( newFolder, file.getName());
            folderEntry.setAttribute( "path", file.getPath());
            folderEntry.setAttribute( "type", "folder");
            newFolder.addChild( folderEntry);
          }
          else
          {
            String tag = getRootTag( file);
            if ( tag != null)
            {
              IModelObject fileEntry = getFactory().createObject( newFolder, "file");
              fileEntry.setID( file.getName());
              fileEntry.setAttribute( "path", file.getParent());
              newFolder.addChild( fileEntry);
            }
          }
        }
        
        // update
        update( reference, newFolder);
      }
      catch( Exception e)
      {
        throw new CachingException( "Unable to sync reference: "+reference, e);
      }
    }
  }

  /**
   * Returns the root tag for the specified file. Currently, this method does not open the file.
   * Instead, it merely trims the file extension and uses the file name for the root tag.
   * @param file Any file.
   * @return Returns the root tag for the specified file.
   */
  static public String getRootTag( File file)
  {
    Pattern tagPattern = Pattern.compile( "^\\s*<\\s*([a-zA-Z0-9_-]+)\\s*>");
    try
    {
      FileReader fileReader = new FileReader( file);
      BufferedReader reader = new BufferedReader( fileReader);
      while( reader.ready())
      {
        String line = reader.readLine();
        Matcher matcher = tagPattern.matcher( line);
        if ( matcher.matches())
        {
          String tag = matcher.group( 1);
          return tag;
        }
      }
    }
    catch( IOException e)
    {
      e.printStackTrace( System.err);
    }
    return null;
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.external.AbstractCachingPolicy#getURI(org.xmodel.external.IExternalReference)
   */
  @Override
  public URI getURI( IExternalReference reference) throws CachingException
  {
    String path = Xlate.get( reference, "path", (String)null);
    if ( path == null) throw new CachingException( "Path not defined for reference: "+reference);
    File folder = new File( path);
    return folder.toURI();
  }

  static final IExpression folderPath = XPath.createExpression( "*[ @type='folder']");
  static final IExpression filePath = XPath.createExpression( "file/*");
    
  private FilenameFilter filter;
  private FileCachingPolicy fileCachingPolicy;
}
