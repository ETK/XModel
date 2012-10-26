package org.xmodel.net.bind;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.xmodel.IModelObject;
import org.xmodel.log.Log;
import org.xmodel.log.SLog;
import org.xmodel.net.nu.FullProtocolChannelHandler.Type;
import org.xmodel.net.nu.ProtocolException;

public class UnbindRequestProtocol
{
  public UnbindRequestProtocol( BindProtocol bundle)
  {
    this.bundle = bundle;
  }
  
  /**
   * Send an unbind request.
   * @param channel The channel.
   * @param netID The network identifier.
   * @return Returns null or the query result.
   */
  public void send( Channel channel, long netID) throws InterruptedException
  {
    log.debugf( "UnbindRequestProtocol.send: element=%X", netID);
    
    ChannelBuffer buffer = bundle.headerProtocol.writeHeader( Type.unbindRequest, 8);
    buffer.writeLong( netID);
    
    // ignoring write buffer overflow for this type of messaging
    channel.write( buffer);
  }
  
  /**
   * Handle a bind request.
   * @param channel The channel.
   * @param buffer The buffer.
   */
  public void handle( Channel channel, ChannelBuffer buffer) throws ProtocolException
  {
    long netID = buffer.readLong();
    
    IModelObject element = bundle.serverCompressor.findLocal( netID);
    if ( element == null) throw new ProtocolException( String.format( "Element %X not found", netID));
    
    log.debugf( "UnbindRequestProtocol.handle: element=%X", netID);
    
    bundle.dispatcher.execute( new UnbindRunnable( channel, netID, element));
  }
  
  /**
   * Unbind the element with the specified network identifier.
   * @param channel The channel.
   * @param netID The network identifier.
   * @param element The element.
   */
  private void unbind( Channel channel, long netID, IModelObject element)
  {
    try
    {
      UpdateListener listener = bundle.bindRequestProtocol.getListener( element);
      if ( listener != null) listener.uninstall( element);
    }
    catch( Exception e)
    {
      SLog.exception( this, e);
    }
  }
  
  private class UnbindRunnable implements Runnable
  {
    public UnbindRunnable( Channel channel, long netID, IModelObject element)
    {
      this.channel = channel;
      this.netID = netID;
      this.element = element;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
      unbind( channel, netID, element);
    }
    
    private Channel channel;
    private long netID;
    private IModelObject element;
  }
  
  private final static Log log = Log.getLog( UnbindRequestProtocol.class);

  private BindProtocol bundle;
}
