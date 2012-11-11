package org.xmodel.net.execution;

import java.util.concurrent.ScheduledExecutorService;
import org.xmodel.IDispatcher;
import org.xmodel.compress.ICompressor;
import org.xmodel.compress.TabularCompressor;
import org.xmodel.net.nu.HeaderProtocol;
import org.xmodel.xpath.expression.IContext;

public class ExecutionProtocol
{
  public ExecutionProtocol( HeaderProtocol headerProtocol, IContext context, IDispatcher dispatcher, ScheduledExecutorService scheduler)
  {
    this.context = context;
    this.dispatcher = dispatcher;
    this.headerProtocol = headerProtocol;
    this.requestProtocol = new ExecutionRequestProtocol( this);
    this.responseProtocol = new ExecutionResponseProtocol( this);
    this.scheduler = scheduler;
    this.upstreamCompressor = new TabularCompressor( true);
    this.downstreamCompressor = new TabularCompressor( true);
  }
  
  /**
   * Reset this instance by releasing internal resources.  This method should be called after 
   * the channel is closed to prevent conflict between protocol traffic and the freeing of resources.
   */
  public void reset()
  {
    headerProtocol.reset();
    requestProtocol.reset();
    responseProtocol.reset();
  }
  
  public IContext context;
  public IDispatcher dispatcher;
  public HeaderProtocol headerProtocol;
  public ExecutionRequestProtocol requestProtocol;
  public ExecutionResponseProtocol responseProtocol;
  public ScheduledExecutorService scheduler;
  public ICompressor upstreamCompressor;
  public ICompressor downstreamCompressor;
}
