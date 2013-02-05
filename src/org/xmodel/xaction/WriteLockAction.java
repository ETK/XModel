package org.xmodel.xaction;

import java.util.concurrent.TimeUnit;

import org.xmodel.xpath.expression.IContext;
import org.xmodel.xpath.expression.IExpression;
import org.xmodel.xpath.expression.StatefulContext;

/**
 * An XAction that will acquire a lock on its execution context, if that context is an instance of ThreadPoolContext,
 * or throw an IllegalStateException.
 */
public class WriteLockAction extends GuardedAction
{
  /* (non-Javadoc)
   * @see org.xmodel.ui.swt.form.actions.XAction#configure(org.xmodel.ui.model.ViewModel)
   */
  @Override
  public void configure( XActionDocument document)
  {
    super.configure( document);
    script = document.createScript();
    timeoutExpr = document.getExpression( "timeout", true);
  }

  /* (non-Javadoc)
   * @see org.xmodel.xaction.GuardedAction#doAction(org.xmodel.xpath.expression.IContext)
   */
  @Override
  protected Object[] doAction( IContext context)
  {
    try
    {
      if ( timeoutExpr == null)
      {
        context.getLock().writeLock().lock();
      }
      else
      {
        double timeout = timeoutExpr.evaluateNumber( context);
        context.getLock().writeLock().tryLock( (int)timeout, TimeUnit.MILLISECONDS);
      }
    }
    catch( InterruptedException e)
    {
      throw new XActionException( "Lock aborted without execution.", e);
    }

    try
    {
      StatefulContext readContext = new StatefulContext( context);
      return script.run( readContext);
    }
    finally
    {
      context.getLock().writeLock().unlock();
    }
  }

  private ScriptAction script;
  private IExpression timeoutExpr;
}
