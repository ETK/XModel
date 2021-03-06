/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * XipAssociation.java
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
package org.xmodel.caching;

import org.xmodel.external.CachingException;
import org.xmodel.external.ICachingPolicy;

/**
 * An IFileAssociation for zip files with the <i>.zip</i> extension.
 */
public class ZipAssociation extends AbstractFileAssociation
{
  /* (non-Javadoc)
   * @see org.xmodel.external.caching.IFileAssociation#getAssociations()
   */
  public String[] getExtensions()
  {
    return extensions;
  }

  /* (non-Javadoc)
   * @see org.xmodel.caching.AbstractFileAssociation#getCachingPolicy(org.xmodel.external.ICachingPolicy, java.lang.String)
   */
  @Override
  public ICachingPolicy getCachingPolicy( ICachingPolicy parent, String name) throws CachingException
  {
    return new ZipCachingPolicy();
  }

  private final static String[] extensions = { ".zip"};
}
