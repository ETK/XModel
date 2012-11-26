package org.xmodel.net.bind;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.xmodel.INode;
import org.xmodel.log.Log;
import org.xmodel.net.XioChannelHandler.Type;

public class SyncResponseProtocol
{
  public SyncResponseProtocol( BindProtocol bundle)
  {
    this.bundle = bundle;
    this.counter = new AtomicInteger( 1);
    this.queues = new ConcurrentHashMap<Integer, SynchronousQueue<INode>>();
  }
  
  /**
   * Reset this instance by releasing internal resources.  This method should be called after 
   * the channel is closed to prevent conflict between protocol traffic and the freeing of resources.
   */
  public void reset()
  {
    queues.clear();
  }
  
  /**
   * Send a sync response.
   * @param channel The channel.
   * @param correlation The correlation number.
   * @param element Null or the element identified by the bind request query.
   */
  public void send( Channel channel, int correlation, INode element) throws IOException
  {
    log.debugf( "SyncResponseProtocol.send: corr=%d, found=%s", correlation, (element != null)? "true": "false");
    
    ChannelBuffer buffer2 = bundle.responseCompressor.compress( element);
    ChannelBuffer buffer1 = bundle.headerProtocol.writeHeader( 4, Type.syncResponse, buffer2.readableBytes());
    buffer1.writeInt( correlation);
    
    // ignoring write buffer overflow for this type of messaging
    channel.write( ChannelBuffers.wrappedBuffer( buffer1, buffer2));
  }
  
  /**
   * Handle the next bind response message in the specified buffer.
   * @param channel The channel.
   * @param buffer The buffer.
   */
  public void handle( Channel channel, ChannelBuffer buffer) throws IOException
  {
    int correlation = buffer.readInt();
    INode element = bundle.requestCompressor.decompress( buffer);
    
    log.debugf( "SyncResponseProtocol.handle: corr=%d, element=%s", correlation, element.getType());
    
    SynchronousQueue<INode> queue = queues.remove( correlation);
    if ( queue != null) queue.offer( element); 
  }

  /**
   * Allocates the next correlation number.
   * @return Returns the correlation number.
   */
  protected int nextCorrelation()
  {
    int correlation = counter.getAndIncrement();
    queues.put( correlation, new SynchronousQueue<INode>());
    return correlation;
  }
  
  /**
   * Wait for a response to the request with the specified correlation number.
   * @param correlation The correlation number.
   * @param timeout The timeout in milliseconds.
   * @return Returns null or the response.
   */
  protected INode waitForResponse( long correlation, int timeout) throws InterruptedException
  {
    try
    {
      SynchronousQueue<INode> queue = queues.get( correlation);
      return queue.poll( timeout, TimeUnit.MILLISECONDS);
    }
    finally
    {
      queues.remove( correlation);
    }
  }

  private final static Log log = Log.getLog( SyncResponseProtocol.class);

  private BindProtocol bundle;
  private AtomicInteger counter;
  private Map<Integer, SynchronousQueue<INode>> queues;
}
