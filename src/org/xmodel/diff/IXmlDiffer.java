/*
 * XModel (XML Application Data Modeling Framework)
 * Author: Bob Dunnagan (bdunnagan@nc.rr.com)
 * Copyright Bob Dunnagan 2009. All rights reserved.
 */
package org.xmodel.diff;

import org.xmodel.IChangeSet;
import org.xmodel.IModelObject;

/**
 * An interface for xml differencing algorithms. The methods are defined in terms of a left-hand-side 
 * tree and a right-hand-side tree. The left-hand-side tree is the tree which will be updated if the 
 * change set records are applied.
 * <p>
 * The children of any element within a document fragment can be considered as an ordered list or as
 * an unordered set of elements. When an implementation of IXmlDiffer is comparing the children of
 * two elements, it needs to know whether to perform an ordered or an unordered comparison. This 
 * information is provided by the IXmlMatcher interface. The IXmlMatcher interface is also responsible
 * for finding a match for a particular child and determining whether two elements match.
 */
public interface IXmlDiffer
{
  /**
   * Set the IXmlMatcher used to compare two IModelObjects.
   * @param matcher The matcher.
   */
  public void setMatcher( IXmlMatcher matcher);
  
  /**
   * Returns the matcher used to compare two IModelObjects.
   * @return Returns the matcher used to compare two IModelObjects.
   */
  public IXmlMatcher getMatcher();
  
  /**
   * Find the difference between the lhs argument and rhs argument and create change records
   * which, when applied, will make the lhs argument identical to the rhs argument. Some
   * implementations may choose to diff only portions of the subtree. If the change set is 
   * null the method will exit as soon as it finds a discrepancy.
   * @param lhs The left-hand-side tree.
   * @param rhs The right-hand-side tree.
   * @param changeSet The change set where the change records will be added.
   * @return Returns true if the objects are identical.
   */
  public boolean diff( IModelObject lhs, IModelObject rhs, IChangeSet changeSet);

  /**
   * Perform a diff and immediately apply the change records. When this method returns, the
   * lhs argument should be identical to the rhs argument.  Some implementations may choose
   * to diff only portions of the subtree.
   * @param lhs The left-hand-side tree.
   * @param rhs The right-hand-side tree.
   * @return Returns true if the objects are identical.
   */
  public boolean diffAndApply( IModelObject lhs, IModelObject rhs);
  
  /**
   * Diff just the attributes of the lhs and rhs objects. If the change set is null the 
   * method will exit as soon as it finds a discrepancy.
   * @param lhs The left-hand-side tree.
   * @param rhs The right-hand-side tree.
   * @param changeSet The change set where change records will be created.
   * @return Returns true if the objects have identical attributes.
   */
  public boolean diffAttributes( IModelObject lhs, IModelObject rhs, IChangeSet changeSet);
  
  /**
   * Diff just the children of the lhs and rhs objects.  If the change set is null the 
   * method will exit as soon as it finds a discrepancy.
   * @param lhs The left-hand-side tree.
   * @param rhs The right-hand-side tree.
   * @param changeSet The change set where change records will be created.
   * @return Returns true if the objects have identical attributes.
   */
  public boolean diffChildren( IModelObject lhs, IModelObject rhs, IChangeSet changeSet);
}
