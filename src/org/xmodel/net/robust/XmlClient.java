package org.xmodel.net.robust;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.xmodel.IModelObject;
import org.xmodel.compress.ICompressor;
import org.xmodel.compress.TabularCompressor;
import org.xmodel.compress.TabularCompressor.PostCompression;
import org.xmodel.util.Radix;

/**
 * A Client which uses a compressed XML message schema.
 */
public class XmlClient extends Client
{
  public XmlClient( String host, int port)
  {
    super( host, port);
    
    receivers = new HashMap<String, IReceiver>();
    
    addListener( new IListener() {
      public void notifyOpen( ISession session)
      {
        mid = 1;
      }
      public void notifyClose( ISession session)
      {
      }
      public void notifyConnect( ISession session)
      {
        compressor = new TabularCompressor( PostCompression.none);
        loop = new ClientMessageLoop(); 
        loop.start();
      }
      public void notifyDisconnect( ISession session)
      {
        loop.stop();
        loop = null;
        compressor = null;
      }
    });
  }

  /**
   * Add a receiver to the host. The receiver will began receiving messages at the next 
   * available opportunity. Only one receiver may be registered for each type. This method
   * cannot be called after the client is connected.
   * @param type The element name of the message root element.
   * @param receiver The receiver that will handle messages received by the host.
   */
  public void register( String type, IReceiver receiver)
  {
    if ( isOpen()) throw new IllegalStateException( "XmlClient is already open.");
    receivers.put( type, receiver);
  }
  
  /**
   * Unregister the receiver on the specified type.
   * @param type The type.
   */
  public void unregister( String type)
  {
    if ( isOpen()) throw new IllegalStateException( "XmlClient is already open.");
    receivers.remove( type);
  }
  
  /**
   * Send a message to the server.
   * @param message The message.
   */
  public void send( IModelObject message)
  {
    message.setID( Radix.convert( mid++, 36));
    byte[] compressed = compressor.compress( message);
    write( compressed);
  }

  /**
   * Send a message to the server and wait for the response.
   * @param message The message.
   * @param timeout The timeout (0 means forever).
   * @return Returns the response.
   */
  protected IModelObject sendAndWait( IModelObject message, int timeout) throws TimeoutException, InterruptedException
  {
    waitingID = Radix.convert( mid++, 36);
    message.setID( waitingID);
    
    // send message
    byte[] compressed = compressor.compress( message);
    write( compressed);
    
    // clean semaphore
    semaphore.drainPermits();
    
    // block on semaphore
    if ( timeout > 0)
    {
      if ( !semaphore.tryAcquire( timeout, TimeUnit.MILLISECONDS))
        throw new TimeoutException( "Timeout waiting for response to: "+message);
    }
    else
    {
      semaphore.acquire();
    }
    
    // return message
    IModelObject object = (response != null)? response.cloneTree(): null;
    response = null;
    return object;
  }
    
  /**
   * An interface for client message handlers.
   */
  public interface IReceiver
  {
    /**
     * Called when a message is received by the client.
     * @param client The client that received the message.
     * @param message The message that was received.
     */
    public void handle( XmlClient client, IModelObject message);
  }

  /**
   * A MessageLoop for the client.
   */
  public class ClientMessageLoop extends MessageLoop
  {
    public ClientMessageLoop()
    {
      super( "Client Message Loop", XmlClient.this);
    }
    
    /* (non-Javadoc)
     * @see org.xmodel.net.robust.MessageLoop#handle(org.xmodel.IModelObject)
     */
    @Override
    public void handle( IModelObject message)
    {
      if ( waitingID != null && message.getID().equals( waitingID))
      {
        waitingID = null;
        response = message;
        semaphore.release();
      }
      else
      {
        IReceiver receiver = receivers.get( message.getType());
        if ( receiver != null) receiver.handle( XmlClient.this, message);
      }
    }

    /* (non-Javadoc)
     * @see org.xmodel.net.robust.MessageLoop#terminated(java.lang.Throwable)
     */
    @Override
    public void terminated( Throwable t)
    {
      try { close();} catch( Exception e) {}
    }
  }
  
  private Map<String, IReceiver> receivers;
  private ICompressor compressor;
  private MessageLoop loop;
  private long mid;
  
  // send-and-wait
  private Semaphore semaphore;
  private String waitingID;
  private IModelObject response;
}
