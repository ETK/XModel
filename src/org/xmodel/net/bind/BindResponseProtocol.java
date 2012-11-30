package org.xmodel.net.bind;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.xmodel.IModelObject;
import org.xmodel.external.IExternalReference;
import org.xmodel.log.Log;
import org.xmodel.net.XioChannelHandler.Type;

public class BindResponseProtocol
{
  public BindResponseProtocol( BindProtocol bundle)
  {
    this.bundle = bundle;
    this.counter = new AtomicInteger( 1);
    this.pending = new ConcurrentHashMap<Integer, BindRecord>();
  }
  
  /**
   * Reset this instance by releasing internal resources.  This method should be called after 
   * the channel is closed to prevent conflict between protocol traffic and the freeing of resources.
   */
  public void reset()
  {
    pending.clear();
  }
  
  /**
   * Send a bind response.
   * @param channel The channel.
   * @param correlation The correlation number.
   * @param element Null or the element identified by the bind request query.
   */
  public void send( Channel channel, int correlation, IModelObject element) throws IOException
  {
    log.debugf( "BindResponseProtocol.send: corr=%d, found=%s", correlation, (element != null)? "true": "false");
    
    if ( element != null)
    {
      ChannelBuffer buffer2 = bundle.responseCompressor.compress( element);
      ChannelBuffer buffer1 = bundle.headerProtocol.writeHeader( 4, Type.bindResponse, buffer2.readableBytes());
      buffer1.writeInt( correlation);
      
      // ignoring write buffer overflow for this type of messaging
      channel.write( ChannelBuffers.wrappedBuffer( buffer1, buffer2));
    }
    else
    {
      ChannelBuffer buffer = bundle.headerProtocol.writeHeader( 4, Type.bindResponse, 0);
      buffer.writeInt( correlation);
      
      // ignoring write buffer overflow for this type of messaging
      channel.write( buffer);
    }
  }
  
  /**
   * Handle the next bind response message in the specified buffer.
   * @param channel The channel.
   * @param buffer The buffer.
   * @param length The message length.
   */
  public void handle( Channel channel, ChannelBuffer buffer, long length) throws IOException
  {
    int correlation = buffer.readInt();
    
    BindRecord record = pending.remove( correlation);
    if ( record != null) 
    {
      if ( length > 4) bundle.requestCompressor.decompress( buffer, record.reference);
      record.semaphore.release();
    }
    
    log.debugf( "BindResponseProtocol.handle: corr=%d, element=%s", correlation, record.reference.getType());
  }

  /**
   * Allocates the next correlation number.
   * @param reference The reference being remotely bound.
   * @return Returns the correlation number.
   */
  protected synchronized int nextCorrelation( IExternalReference reference)
  {
    int correlation = counter.getAndIncrement();
    pending.put( correlation, new BindRecord( reference));
    return correlation;
  }
  
  /**
   * Wait for a response to the request with the specified correlation number.
   * @param correlation The correlation number.
   * @param timeout The timeout in milliseconds.
   * @return Returns false if the timeout expires before the response is received.
   */
  protected boolean waitForResponse( long correlation, int timeout) throws InterruptedException
  {
    try
    {
      BindRecord record = pending.get( (int)correlation);
      return record.semaphore.tryAcquire( timeout, TimeUnit.MILLISECONDS);
    }
    finally
    {
      pending.remove( correlation);
    }
  }

  private final static class BindRecord
  {
    public BindRecord( IExternalReference reference)
    {
      this.reference = reference;
      this.semaphore = new Semaphore( 0);
    }
    
    public IExternalReference reference;
    public Semaphore semaphore;
  }
  
  private final static Log log = Log.getLog( BindResponseProtocol.class);

  private BindProtocol bundle;
  private AtomicInteger counter;
  private Map<Integer, BindRecord> pending;
}
