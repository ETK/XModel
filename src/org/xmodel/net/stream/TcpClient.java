package org.xmodel.net.stream;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xmodel.log.Log;

public class TcpClient extends TcpManager
{
  public TcpClient() throws IOException
  {
    pending = Collections.synchronizedMap( new HashMap<Channel, Connection>());
  }
  
  /**
   * Connect to the specified remote address.
   * @param host The remote host.
   * @param port The remote port.
   * @param timeout The timeout in milliseconds.
   * @param listener The listener for socket events.
   */
  public Connection connect( String host, int port, int timeout, ITcpListener listener) throws IOException
  {
    InetSocketAddress address = new InetSocketAddress( host, port);
    SocketChannel channel = SocketChannel.open();
    
    Connection connection = createConnection( channel, SelectionKey.OP_CONNECT, listener);
    pending.put( channel, connection);
    
    channel.connect( address);
    
    Request request = new Request();
    request.channel = channel;
    request.buffer = null;
    
    // wait for connect to complete or timeout
    if ( !process( timeout)) return null;
    
    // start client thread
    start();
    
    return connection;
  }
  
  /**
   * Reconnect the specified connection.
   * @param connection The connection.
   * @param timeout The timeout in milliseconds.
   * @return Returns true if the connection was reestablished.
   */
  public boolean reconnect( Connection connection, int timeout) throws IOException
  {
    stop();
    
    SocketChannel channel = connection.getChannel();
    if ( channel != null)
    {
      try { channel.close();} catch( Exception e) {}
    }
    
    pending.remove( channel);
    
    InetSocketAddress address = new InetSocketAddress( connection.getAddress(), connection.getPort());
    channel = SocketChannel.open();
    
    channel.register( selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
    
    pending.put( channel, connection);
    channel.connect( address);
    
    // wait for reconnect to complete or timeout
    if ( !process( timeout)) return false;
    
    // start client thread
    start();
    
    return true;
  }

  /* (non-Javadoc)
   * @see org.xmodel.net.stream.TcpManager#process(int)
   */
  protected boolean process( int timeout) throws IOException
  {
    // wait for connection
    if ( selector.select( timeout) == 0) 
      return false; 
      
    // handle events
    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
    while( iter.hasNext())
    {
      SelectionKey readyKey = iter.next();
      iter.remove();
      
      if ( !readyKey.isValid()) continue;
      
      try
      {
        if ( readyKey.isConnectable())
        {
          connect( readyKey);
        }
        else
        {
          if ( readyKey.isReadable())
          {
            read( readyKey);
          }
          else if ( readyKey.isWritable())
          {
            write( readyKey);
          }
        }
      }
      catch( IOException e)
      {
        log.exception( e);
      }
    }
    
    return true;
  }
  
  /**
   * Process a newly connected socket.
   * @param key The selection key.
   */
  private void connect( SelectionKey key) throws IOException
  {
    SocketChannel channel = (SocketChannel)key.channel();
    Connection connection = pending.remove( channel);
    try
    {
      channel.finishConnect();
      channel.register( selector, SelectionKey.OP_READ);
      connection.connected( channel);
    }
    catch( IOException e)
    {
      connection.close( false);
    }
  }
  
  private final static Log log = Log.getLog( "org.xmodel.net.stream");
  private Map<Channel, Connection> pending;
}
