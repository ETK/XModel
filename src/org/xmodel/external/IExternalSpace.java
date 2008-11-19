/*
 * XModel (XML Application Data Modeling Framework)
 * Author: Bob Dunnagan (bdunnagan@nc.rr.com)
 * Copyright Bob Dunnagan 2009. All rights reserved.
 */
package org.xmodel.external;

import java.net.URI;
import java.util.List;
import org.xmodel.IModelObject;

/**
 * An interface for creating a data-model to represent a URI specification for a particular URI scheme.
 * This interface is used by the <code>fn:doc</code> function to resolve a URI.
 */
public interface IExternalSpace
{
  /**
   * Returns true if this space serves the scheme of the specified URI.
   * @param uri The URI.
   * @return Returns true if this space serves the scheme of the specified URI.
   */
  public boolean contains( URI uri);
  
  /**
   * Execute the specified URI query. The result will be an empty set for non-node-set queries.
   * @param uri The URI specification.
   * @return Returns the result-set of the query.
   */
  public List<IModelObject> query( URI uri) throws CachingException;
}
