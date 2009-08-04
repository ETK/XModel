package org.xmodel.xpath.expression.nu;

import java.util.List;

/**
 * An extension of the IExpression interface which defines methods for operating on the
 * expression tree including construction of the tree, propagation of notifications and
 * setting the dirty state.
 */
public interface IExpressionNode extends IExpression
{
  /**
   * @return Returns null or the parent of this expression.
   */
  public IExpressionNode getParent();

  /**
   * @return Returns the children of this expression.
   */
  public List<IExpressionNode> getChildren();
  
  /**
   * Add a child to this expression.
   * @param child The child.
   */
  public void addChild( IExpressionNode child);
  
  /**
   * Remove a child from this expression.
   * @param child The child.
   */
  public void removeChild( IExpressionNode child);
  
  /**
   * An enumeration for the possible dirty states of the expression. The dirty state "none"
   * indicates that the expression result has not changed. The dirty state "tree" indicates
   * that the result of this expression may have changed. The dirty state "branch" indicates
   * that a descendant of this expression is dirty. The dirty state "all" indicates that 
   * the result of this expression may have changed, but incremental notification cannot be
   * provided.
   * <p>
   * Consider the for/return expression. Changes to the return expression do not require the
   * iterator expression to be reevaluated. In this case, the return expression will have the
   * "tree" dirty state, while the for expression to which it belongs will have the "branch"
   * dirty state.
   */
  public enum Dirty { none, tree, branch, all};

  /**
   * Returns the dirty state of this expression.
   * @return Returns the dirty state of this expression.
   */
  public Dirty getDirty();

  /**
   * Mark the dirty state of this expression.
   * @param dirty The dirty state.
   */
  public void markDirty( Dirty dirty);
}
