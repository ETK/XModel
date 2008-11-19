/*
 * XModel (XML Application Data Modeling Framework)
 * Author: Bob Dunnagan (bdunnagan@nc.rr.com)
 * Copyright Bob Dunnagan 2009. All rights reserved.
 */
package org.xmodel.diff;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.xmodel.IModelObject;


/**
 * An IXmlMatcher which provides methods to customize the behavior of a matcher precisely.
 * Elements and attributes from the left-hand-side and right-hand-side of the diff, which
 * are to be ignored, are explicitly specified, as are elements whose children are ordered.
 */
public class ConfiguredXmlMatcher extends DefaultXmlMatcher
{
  public ConfiguredXmlMatcher()
  {
    ignoreSet = new HashSet<IModelObject>();
    orderedSet = new HashSet<IModelObject>();
  }
  
  /**
   * Specify an element or attribute to be ignored.
   * @param node The element or attribute.
   */
  public void ignore( IModelObject node)
  {
    ignoreSet.add( node);
  }
  
  /**
   * Specify an element or attribute to be regarded (cancels previous <code>ignore</code>).
   * @param node The element or attribute.
   */
  public void regard( IModelObject node)
  {
    ignoreSet.remove( node);
  }
  
  /**
   * Specify elements and/or attributes to be ignored.
   * @param nodes The elements and/or attributes.
   */
  public void ignore( List<IModelObject> nodes)
  {
    ignoreSet.addAll( nodes);
  }
  
  /**
   * Specify elements and/or attributes to be regarded (cancels previous <code>ignore</code>).
   * @param nodes The elements and/or attributes.
   */
  public void regard( List<IModelObject> nodes)
  {
    ignoreSet.removeAll( nodes);
  }
  
  /**
   * Specify that the children of the specified parent are ordered.
   * @param parent The parent whose children are ordered.
   */
  public void setOrdered( IModelObject parent)
  {
    orderedSet.add( parent);
  }
  
  /**
   * Specify that the children of the specified parent are unordered (cancels previous <code>setOrdered</code>).
   * @param parent The parent whose children are unordered.
   */
  public void setUnordered( IModelObject parent)
  {
    orderedSet.remove( parent);
  }
  
  /**
   * Specify that the children of the specified parents are ordered.
   * @param parents The parents whose children are ordered.
   */
  public void setOrdered( List<IModelObject> parents)
  {
    orderedSet.addAll( parents);
  }
  
  /**
   * Specify that the children of the specified parents are unordered (cancels previous <code>setOrdered</code>).
   * @param parents The parents whose children are unordered.
   */
  public void setUnordered( List<IModelObject> parents)
  {
    orderedSet.removeAll( parents);
  }

  /* (non-Javadoc)
   * @see org.xmodel.diff.DefaultXmlMatcher#isList(org.xmodel.IModelObject)
   */
  @Override
  public boolean isList( IModelObject parent)
  {
    if ( orderedSet != null) return orderedSet.contains( parent);
    return super.isList( parent);
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.diff.DefaultXmlMatcher#shouldDiff(org.xmodel.IModelObject, boolean)
   */
  @Override
  public boolean shouldDiff( IModelObject object, boolean lhs)
  {
    if ( ignoreSet != null) return !ignoreSet.contains( object);
    return super.shouldDiff( object, lhs);
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.diff.DefaultXmlMatcher#shouldDiff(org.xmodel.IModelObject, java.lang.String, boolean)
   */
  @Override
  public boolean shouldDiff( IModelObject object, String attrName, boolean lhs)
  {
    if ( ignoreSet != null)
    {
      if ( attrName == null) return !ignoreSet.contains( null);
      IModelObject node = object.getAttributeNode( attrName);
      return !ignoreSet.contains( node);
    }
    return super.shouldDiff( object, attrName, lhs);
  }

  private Set<IModelObject> ignoreSet;
  private Set<IModelObject> orderedSet;
}
