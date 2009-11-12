/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * ManualDispatcher.java
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
package org.xmodel;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


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
   * @see org.xmodel.net.IDispatcher#execute(java.lang.Runnable)
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
