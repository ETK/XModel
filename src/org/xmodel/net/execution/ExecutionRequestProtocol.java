package org.xmodel.net.execution;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.xmodel.IModelObject;
import org.xmodel.Xlate;
import org.xmodel.log.Log;
import org.xmodel.log.SLog;
import org.xmodel.net.IXioCallback;
import org.xmodel.net.XioExecutionException;
import org.xmodel.net.XioChannelHandler.Type;
import org.xmodel.net.execution.ExecutionResponseProtocol.ResponseTask;
import org.xmodel.xaction.IXAction;
import org.xmodel.xaction.XActionDocument;
import org.xmodel.xpath.expression.IContext;
import org.xmodel.xpath.expression.StatefulContext;

public class ExecutionRequestProtocol
{
  public ExecutionRequestProtocol( ExecutionProtocol bundle)
  {
    this.bundle = bundle;
    this.document = new XActionDocument();
  }
  
  /**
   * Reset this instance by releasing internal resources.  This method should be called after 
   * the channel is closed to prevent conflict between protocol traffic and the freeing of resources.
   */
  public void reset()
  {
    document = new XActionDocument();
  }
  
  /**
   * Add a default package to the execution environment.
   * @param packageName The name of the package.
   */
  public void addDefaultPackage( String packageName)
  {
    document.addPackage( packageName);
  }
  
  /**
   * Send an execution request via the specified channel.
   * @param channel The channel.
   * @param context The local context.
   * @param vars Shared variables from the local context.
   * @param element The script element to execute.
   * @param timeout The timeout in milliseconds.
   * @return Returns the result.
   */
  public Object[] send( Channel channel, IContext context, String[] vars, IModelObject element, int timeout) throws XioExecutionException, IOException, InterruptedException
  {
    int correlation = bundle.responseProtocol.nextCorrelation();
    log.debugf( "ExecutionRequestProtocol.send (sync): corr=%d, vars=%s, @name=%s, timeout=%d", correlation, Arrays.toString( vars), Xlate.get( element, "name", ""), timeout);
    
    IModelObject request = ExecutionSerializer.buildRequest( context, vars, element);
    ChannelBuffer buffer2 = bundle.requestCompressor.compress( request);
    
    ChannelBuffer buffer1 = bundle.headerProtocol.writeHeader( 0, Type.executeRequest, 4 + buffer2.readableBytes(), correlation);
    
    // ignoring write buffer overflow for this type of messaging
    channel.write( ChannelBuffers.wrappedBuffer( buffer1, buffer2));
    
    return (timeout > 0)? bundle.responseProtocol.waitForResponse( correlation, timeout): null;
  }
  
  /**
   * Send an asynchronous execution request via the specified channel.
   * @param channel The channel.
   * @param context The local context.
   * @param vars Shared variables from the local context.
   * @param element The script element to execute.
   * @param callback The callback.
   * @param timeout The timeout in milliseconds.
   */
  public void send( Channel channel, IContext context, String[] vars, IModelObject element, IXioCallback callback, int timeout) throws IOException, InterruptedException
  {
    ResponseTask task = new ResponseTask( context, callback);
    if ( timeout != Integer.MAX_VALUE)
    {
      ScheduledFuture<?> timer = bundle.scheduler.schedule( new ResponseTimeout( task), timeout, TimeUnit.MILLISECONDS);
      task.setTimer( timer);
    }
    
    int correlation = bundle.responseProtocol.nextCorrelation( task);
    log.debugf( "ExecutionRequestProtocol.send (async): corr=%d, vars=%s, @name=%s, timeout=%d", correlation, Arrays.toString( vars), Xlate.get( element, "name", ""), timeout);
    
    IModelObject request = ExecutionSerializer.buildRequest( context, vars, element);
    ChannelBuffer buffer2 = bundle.requestCompressor.compress( request);
    
    ChannelBuffer buffer1 = bundle.headerProtocol.writeHeader( 0, Type.executeRequest, 4 + buffer2.readableBytes(), correlation);
    
    // ignoring write buffer overflow for this type of messaging
    channel.write( ChannelBuffers.wrappedBuffer( buffer1, buffer2));
  }
  
  /**
   * Handle a request.
   * @param channel The channel.
   * @param buffer The buffer.
   */
  public void handle( Channel channel, ChannelBuffer buffer) throws IOException
  {
    int correlation = buffer.readInt();

    if ( bundle.context == null)
    {
      bundle.responseProtocol.send( channel, correlation, null, (Object[])null);
      return;
    }
    
    IModelObject element = ExecutionSerializer.readRequest( bundle.responseCompressor.decompress( buffer), bundle.context);
    checkPrivileges( element);
    
    RequestRunnable runnable = new RequestRunnable( channel, correlation, element);
    bundle.context.getModel().dispatch( runnable);
  }
  
  /**
   * Verify that execution privileges allow the specified script element to be executed.
   * @param element The script element.
   */
  private void checkPrivileges( IModelObject element) throws IOException
  {
    if ( privilege != null && !privilege.isPermitted( bundle.context, element))
    {
      throw new IOException( "Script contains restricted operations.");
    }
  }
  
  /**
   * Compile the specified script element.
   * @param element The script element.
   * @return Returns the script.
   */
  private IXAction compile( IModelObject element) throws IOException
  {
    document.setRoot( element);
    IXAction script = document.getAction( element);
    if ( script != null) return script;
    throw new IOException( String.format( "Unable to resolve IXAction class: %s.", element.getType()));
  }

  /**
   * Execute the specifed script.
   * @param channel The channel.
   * @param correlation The correlation.
   * @param element The script element.
   */
  private void execute( Channel channel, int correlation, IModelObject element)
  {
    try
    {
      bundle.context.getModel().writeLock();
    }
    catch( InterruptedException e)
    {
      SLog.warnf( this, "Thread interrupted, remote-exec aborted.");
      return;
    }
    
    StatefulContext context = new StatefulContext( bundle.context);
    try
    {
      IXAction script = compile( element);
      Object[] results = script.run( context);
      bundle.responseProtocol.send( channel, correlation, context, results);
    }
    catch( Exception e)
    {
      try
      {
        bundle.responseProtocol.send( channel, correlation, context, e);
      }
      catch( Exception e2)
      {
        SLog.warnf( this, "Unable to send exception execution response because %s.", e2.getMessage());
      }
      
      SLog.exceptionf( this, e, "Exception thrown during remote execution: ");
    }
    finally
    {
      bundle.context.getModel().writeUnlock();
    }
  }
  
  private class RequestRunnable implements Runnable
  {
    public RequestRunnable( Channel channel, int correlation, IModelObject element)
    {
      this.channel = channel;
      this.correlation = correlation;
      this.element = element;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
      execute( channel, correlation, element);
    }

    private Channel channel;
    private int correlation;
    private IModelObject element;
  }
  
  private class ResponseTimeout implements Runnable
  {
    public ResponseTimeout( ResponseTask task)
    {
      this.task = task;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
      task.setError( "timeout");
      bundle.context.getModel().dispatch( task);
    }

    private ResponseTask task;
  }
  
  private final static Log log = Log.getLog( ExecutionRequestProtocol.class);

  private ExecutionProtocol bundle;
  private XActionDocument document;
  private ExecutionPrivilege privilege;
}
