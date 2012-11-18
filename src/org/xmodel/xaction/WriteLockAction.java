package org.xmodel.xaction;

import java.util.concurrent.TimeUnit;
import org.xmodel.xpath.expression.IContext;

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
  }

  /* (non-Javadoc)
   * @see org.xmodel.xaction.GuardedAction#doAction(org.xmodel.xpath.expression.IContext)
   */
  @Override
  protected Object[] doAction( IContext context)
  {
    try
    {
      context.getModel().writeLock( 15, TimeUnit.MINUTES);
    }
    catch( InterruptedException e)
    {
      throw new XActionException( "Lock aborted without execution.", e);
    }

    try
    {
      return script.run( context);
    }
    finally
    {
      context.getModel().writeUnlock();
    }
  }
  
  private ScriptAction script;
}
