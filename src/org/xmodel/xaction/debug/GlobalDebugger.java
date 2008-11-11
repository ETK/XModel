package org.xmodel.xaction.debug;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import org.xmodel.net.ModelServer;
import org.xmodel.util.Radix;
import org.xmodel.xaction.IXAction;
import org.xmodel.xpath.expression.IContext;
import org.xmodel.xpath.expression.IExpression;

/**
 * An implementation of IDebugger which handles notifications from all threads and sends
 * the notifications to a delegate IDebugger instance specific to each thread.
 */
public class GlobalDebugger implements IDebugger
{
  public GlobalDebugger( ModelServer server)
  {
    this.server = server;
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.xaction.debug.IDebugger#push(org.xmodel.xpath.expression.IContext, org.xmodel.xaction.IXAction)
   */
  public void push( IContext context, IXAction action)
  {
    getDebugger().push( context, action);
  }

  /* (non-Javadoc)
   * @see org.xmodel.xaction.debug.IDebugger#pop()
   */
  public Frame pop()
  {
    return getDebugger().pop();
  }

  /* (non-Javadoc)
   * @see org.xmodel.xaction.debug.IDebugger#scriptEnding()
   */
  public void scriptEnding()
  {
    getDebugger().scriptEnding();
  }

  /**
   * Set the target thread for debug requests: null indicates all threads.
   * @param id The thread id or null.
   */
  public void setTargetThread( String id)
  {
    threadID = id;
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.xaction.debug.IDebugger#getVariableValue(int, java.lang.String)
   */
  public Object getVariableValue( int stackFrameIndex, String variable)
  {
    IDebugger debugger = debuggers.get( threadID);
    if ( debugger != null) return debugger.getVariableValue( stackFrameIndex, variable);
    return null;
  }

  /* (non-Javadoc)
   * @see org.xmodel.xaction.debug.IDebugger#report()
   */
  public void report()
  {
    for( IDebugger debugger: getTargetDebuggers())
      debugger.report();
  }

  /* (non-Javadoc)
   * @see org.xmodel.xaction.debug.IDebugger#resume()
   */
  public void resume()
  {
    // set suspend flag for newly discovered threads
    suspend = false;
    
    // resume already discovered threads
    for( IDebugger debugger: getTargetDebuggers())
      debugger.resume();
  }

  /* (non-Javadoc)
   * @see org.xmodel.xaction.debug.IDebugger#suspend()
   */
  public void suspend()
  {
    // set suspend flag to suspend newly discovered threads
    suspend = true;
    
    // suspend already discovered threads
    for( IDebugger debugger: getTargetDebuggers())
      debugger.suspend();
  }

  /* (non-Javadoc)
   * @see org.xmodel.xaction.debug.IDebugger#stepInto()
   */
  public void stepInto()
  {
    IDebugger debugger = debuggers.get( threadID);
    if ( debugger != null) debugger.stepInto();
  }

  /* (non-Javadoc)
   * @see org.xmodel.xaction.debug.IDebugger#stepOver()
   */
  public void stepOver()
  {
    IDebugger debugger = debuggers.get( threadID);
    if ( debugger != null) debugger.stepOver();
  }

  /* (non-Javadoc)
   * @see org.xmodel.xaction.debug.IDebugger#stepReturn()
   */
  public void stepReturn()
  {
    IDebugger debugger = debuggers.get( threadID);
    if ( debugger != null) debugger.stepReturn();
  }

  /* (non-Javadoc)
   * @see org.xmodel.xaction.debug.IDebugger#createBreakpoint(java.lang.String, java.lang.String)
   */
  public void createBreakpoint( String file, String path)
  {
    for( IDebugger debugger: getTargetDebuggers())
      debugger.createBreakpoint( file, path);
  }

  /* (non-Javadoc)
   * @see org.xmodel.xaction.debug.IDebugger#removeBreakpoint(java.lang.String, java.lang.String)
   */
  public void removeBreakpoint( String file, String path)
  {
    for( IDebugger debugger: getTargetDebuggers())
      debugger.removeBreakpoint( file, path);
  }

  /* (non-Javadoc)
   * @see org.xmodel.xaction.debug.IDebugger#setFilters(org.xmodel.xpath.expression.IExpression, org.xmodel.xpath.expression.IExpression)
   */
  public void setFilters( IExpression fileFilter, IExpression scriptFilter)
  {
    this.fileFilter = fileFilter;
    this.scriptFilter = scriptFilter;
  }

  /**
   * Returns the configured file filter.
   * @return Returns the configured file filter.
   */
  public IExpression getFileFilter()
  {
    return fileFilter;
  }
  
  /**
   * Returns the configured script filter.
   * @return Returns the configured script filter.
   */
  public IExpression getScriptFilter()
  {
    return scriptFilter;
  }
  
  /**
   * Returns the target debuggers corresponding to the selected thread id.
   * @return Returns the target debuggers corresponding to the selected thread id.
   */
  protected Collection<IDebugger> getTargetDebuggers()
  {
    if ( threadID != null)
    {
      IDebugger debugger = debuggers.get( threadID);
      return Collections.singletonList( debugger);
    }
    else
    {
      return debuggers.values();
    }
  }

  /**
   * Returns the IDebugger instance for this thread.
   * @return Returns the IDebugger instance for this thread.
   */
  protected IDebugger getDebugger()
  {
    Thread thread = Thread.currentThread();
    String threadID = Radix.convert( thread.getId(), 36);
    IDebugger debugger = debuggers.get( threadID);
    if ( debugger == null)
    {
      debugger = new Debugger( this, threadID, thread.getName(), server);
      debuggers.put( threadID, debugger);
   
      // suspend newly discovered thread if globally suspended
      if ( suspend) debugger.suspend();
    }
    return debugger;
  }
  
  private static Hashtable<String, IDebugger> debuggers = new Hashtable<String, IDebugger>();
  
  private ModelServer server;
  private String threadID;
  private boolean suspend;
  private IExpression fileFilter;
  private IExpression scriptFilter;
}
