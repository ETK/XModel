/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.net;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import dunnagan.bob.xmodel.IDispatcher;

/**
 * An implementation of IDispatcher which allows runnables to be executed in the current thread.
 */
public class ManualDispatcher implements IDispatcher
{
  public ManualDispatcher()
  {
    queue = new ConcurrentLinkedQueue<Runnable>();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.net.IDispatcher#execute(java.lang.Runnable)
   */
  public void execute( Runnable runnable)
  {
    while( !queue.offer( runnable))
    {
      try { Thread.sleep( 10);} catch( Exception e) {}
    }
  }
  
  /**
   * Dequeue and execute all the runnables on the queue.
   */
  public void process()
  {
    Runnable runnable = queue.poll();
    while( runnable != null)
    {
      runnable.run();
      runnable = queue.poll();
    }
  }
  
  private Queue<Runnable> queue;
}
