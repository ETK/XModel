/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.external.caching;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dunnagan.bob.xmodel.IModelObject;
import dunnagan.bob.xmodel.Xlate;
import dunnagan.bob.xmodel.external.CachingException;
import dunnagan.bob.xmodel.external.ConfiguredCachingPolicy;
import dunnagan.bob.xmodel.external.ExternalReference;
import dunnagan.bob.xmodel.external.ICache;
import dunnagan.bob.xmodel.external.IExternalReference;
import dunnagan.bob.xmodel.external.UnboundedCache;
import dunnagan.bob.xmodel.xml.XmlException;
import dunnagan.bob.xmodel.xml.XmlIO;
import dunnagan.bob.xmodel.xpath.XPath;
import dunnagan.bob.xmodel.xpath.expression.Context;
import dunnagan.bob.xmodel.xpath.expression.IContext;
import dunnagan.bob.xmodel.xpath.expression.IExpression;

/**
 * A ConfiguredCachingPolicy which creates a datamodel for a file or folder of the file system.
 * The datamodel recursively creates external references for each nested xml file or folder. An element
 * representing an xml file or folder is named with the file name. The root external reference must
 * define the <i>path</i> attribute which specifies the base path in the file system.  The name of 
 * the root element is appended to the base path to find the absolute path of the root.
 * <p>
 * Files which end with .xip are associated with the TabularCompressor compression format.
 * <p>
 * NOTE: This caching policy must be unique for each root external reference.
 */
public class FileSystemCachingPolicy extends ConfiguredCachingPolicy
{
  public FileSystemCachingPolicy()
  {
    this( new UnboundedCache());
  }
  
  public FileSystemCachingPolicy( ICache cache)
  {
    super( cache);
    
    setStaticAttributes( new String[] { "*"});
    
    associations = new HashMap<String, IFileAssociation>();
    addAssociation( txtAssociation);
    addAssociation( xipAssociation);
    addAssociation( xmlAssociation);
  }

  /**
   * Add the specified file association.
   * @param association The association.
   */
  public void addAssociation( IFileAssociation association)
  {
    for( String extension: association.getExtensions())
      associations.put( extension, association);
  }
  
  /**
   * Remove the specified file extension association.
   * @param extension The file extension (including the dot).
   */
  public void removeAssociation( String extension)
  {
    associations.remove( extension);
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.external.ConfiguredCachingPolicy#configure(dunnagan.bob.xmodel.IModelObject)
   */
  @Override
  public void configure( IContext context, IModelObject annotation) throws CachingException
  {
    parentContext = context;
    pathExpr = Xlate.get( annotation, "path", defaultPathExpr);

    // Currently, associations are not configurable because there is no ClassLoader available here.
    // Create a sub-class which extends the associations instead.
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.external.ConfiguredCachingPolicy#syncImpl(dunnagan.bob.xmodel.external.IExternalReference)
   */
  @Override
  protected void syncImpl( IExternalReference reference) throws CachingException
  {
    // save root
    if ( fileSystemRoot == null) fileSystemRoot = reference;
   
    // just in case
    reference.removeChildren();
    
    // sync
    File path = buildPath( reference);
    if ( path.isDirectory())
    {
      for( String member: path.list())
      {
        ExternalReference child = new ExternalReference( member);
        child.setCachingPolicy( this);
        child.setDirty( true);
        reference.addChild( child);
      }
    }
    else if ( path.exists() && path.canRead())
    {
      String name = path.getName();
      int index = name.lastIndexOf( '.');
      if ( index >= 0)
      {
        String extension = name.substring( index);
        IFileAssociation association = associations.get( extension);
        association.apply( reference, path);
      }
    }
  }
  
  /**
   * Build the path for the specified file system element.
   * @param element The file system element.
   * @return Returns the absolute path.
   */
  private File buildPath( IModelObject element) throws CachingException
  {
    // get levels
    List<String> levels = new ArrayList<String>();
    while( element != fileSystemRoot)
    {
      levels.add( element.getType());
      element = element.getParent();
    }
    levels.add( fileSystemRoot.getType());
      
    // get base path
    String userDir = System.getProperty( "user.dir");
    String basePath = pathExpr.evaluateString( new Context( parentContext, fileSystemRoot));
    basePath = basePath.replaceFirst( "\\~", userDir.replaceAll( "\\\\", "\\\\\\\\"));
    
    // build path
    StringBuilder path = new StringBuilder();
    path.append( basePath);
    
    for( int i=levels.size()-1; i>=0; i--)
    {
      path.append( File.separatorChar);
      path.append( levels.get( i));
    }
    
    return new File( path.toString());
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.external.ICachingPolicy#flush(dunnagan.bob.xmodel.external.IExternalReference)
   */
  public void flush( IExternalReference reference) throws CachingException
  {
    File path = buildPath( reference);
    if ( path.isDirectory())
      throw new CachingException( 
        "Directory cannot be flushed: "+reference);
    
    try
    {
      (new XmlIO()).write( reference.getChild( 0), path);
    }
    catch( XmlException e)
    {
      throw new CachingException( "Unable to flush reference: "+reference, e);
    }
  }
  
  private final static IExpression defaultPathExpr = XPath.createExpression( 
    "@path");
  
  private final static IFileAssociation txtAssociation = new TxtAssociation();
  private final static IFileAssociation xipAssociation = new XipAssociation();
  private final static IFileAssociation xmlAssociation = new XmlAssociation();
  
  private IContext parentContext;
  private IExpression pathExpr;
  private IExternalReference fileSystemRoot;
  private Map<String, IFileAssociation> associations;
}
