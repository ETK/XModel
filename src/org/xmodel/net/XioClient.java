package org.xmodel.net;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.xmodel.GlobalSettings;
import org.xmodel.xpath.expression.IContext;
import org.xmodel.xpath.expression.StatefulContext;

/**
 * This class provides an interface for the client-side of the protocol.
 * (thread-safe)
 */
public class XioClient extends XioPeer
{
  public XioClient()
  {
    this( GlobalSettings.getInstance().getScheduler(), Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
  }
  
  /**
   * Create a client that uses an NioClientSocketChannelFactory configured with tcp-no-delay and keep-alive.
   * @param scheduler The scheduler used for protocol timers.
   * @param bossExecutor The NioClientSocketChannelFactory boss executor.
   * @param workerExecutor The NioClientSocketChannelFactory worker executor.
   */
  public XioClient( ScheduledExecutorService scheduler, Executor bossExecutor, Executor workerExecutor)
  {
    this( new StatefulContext(), new StatefulContext(), scheduler, bossExecutor, workerExecutor);
  }
  
  /**
   * Create a client that uses an NioClientSocketChannelFactory configured with tcp-no-delay and keep-alive.
   * @param bindContext The context for the remote bind protocol.
   * @param executeContext The context for the remote execution protocol.
   */
  public XioClient( IContext bindContext, IContext executeContext)
  {
    this( bindContext, executeContext, GlobalSettings.getInstance().getScheduler(), Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
  }
  
  /**
   * Create a client that uses an NioClientSocketChannelFactory configured with tcp-no-delay and keep-alive.
   * @param bindContext The context for the remote bind protocol.
   * @param executeContext The context for the remote execution protocol.
   * @param scheduler The scheduler used for protocol timers.
   * @param bossExecutor The NioClientSocketChannelFactory boss executor.
   * @param workerExecutor The NioClientSocketChannelFactory worker executor.
   */
  public XioClient( IContext bindContext, IContext executeContext, ScheduledExecutorService scheduler, Executor bossExecutor, Executor workerExecutor)
  {
    super( new XioChannelHandler( bindContext, executeContext, scheduler));
    
    bootstrap = new ClientBootstrap( new NioClientSocketChannelFactory( bossExecutor, workerExecutor));
    bootstrap.setOption( "tcpNoDelay", true);
    bootstrap.setOption( "keepAlive", true);
    
    bootstrap.setPipelineFactory( new ChannelPipelineFactory() {
      public ChannelPipeline getPipeline() throws Exception
      {
        return Channels.pipeline( handler);
      }
    });
    
    connected = new AtomicBoolean( false);
  }
  
  /**
   * Connect this client to the specified server. A client may only be connected to one server.
   * @param address The address of the server.
   * @param port The port of the server.
   * @return Returns true if the connection was established.
   */
  public ChannelFuture connect( String address, int port)
  {
    if ( connected.getAndSet( true)) throw new IllegalStateException( "Client is already connected.");
    
    ChannelFuture future = bootstrap.connect( new InetSocketAddress( address, port));
    channel = future.getChannel();
    
    return future;
  }
  
  /**
   * Attempt to connect this client to the specified server the specified number of times.
   * @param address The address of the server.
   * @param port The port of the server.
   * @param retries The maximum number of retries.
   * @return Returns 
   */
  public boolean connect( String address, int port, int retries)
  {
  }
  
  /**
   * @return Returns true if the connection to the server is established.
   */
  public boolean isConnected()
  {
    return channel.isConnected();
  }
  
  /**
   * Close the connection.
   */
  public void close()
  {
    if ( isConnected())
    {
      channel.close().awaitUninterruptibly();
      reset();
    }
  }
  
  /**
   * Release resources and prepare this client to make another connection.
   */
  protected void reset()
  {
    // release netty resources
    bootstrap.getFactory().releaseExternalResources();
    
    // release protocol resources
    bind.reset();
    execute.reset();
    
    // prepare for another connection
    handler = new XioChannelHandler( bind.context, execute.context, execute.scheduler);
  }
    
  private ClientBootstrap bootstrap;
  private AtomicBoolean connected;
}
