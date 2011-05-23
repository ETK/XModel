package org.xmodel.net.nu;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Client
{
  public Client( IRecipient recipient)
  {
    this.recipient = recipient;
    pending = new HashMap<Channel, Connection>();
    connected = new HashMap<Channel, Connection>();
  }
  
  /**
   * Connect to the specified remote address.
   * @param host The remote host.
   * @param port The remote port.
   */
  public Connection connect( String host, int port) throws IOException
  {
    address = new InetSocketAddress( host, port);
    SocketChannel channel = SocketChannel.open();
    channel.configureBlocking( false);
    
    if ( selector == null) selector = SelectorProvider.provider().openSelector();
    channel.register( selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
    
    channel.connect( address);
    
    Connection connection = new Connection( recipient);
    pending.put( channel, connection);
    return connection;
  }
   
  /**
   * Reconnect the specified connection.
   * @param connection The connection.
   */
  void reconnect( Connection connection) throws IOException
  {
    SocketChannel channel = connection.getChannel();
    if ( channel != null)
    {
      try { channel.close();} catch( Exception e) {}
    }
    
    pending.remove( channel);
    connected.remove( channel);
    
    address = new InetSocketAddress( connection.getAddress(), connection.getPort());
    channel = SocketChannel.open();
    channel.configureBlocking( false);
    
    if ( selector == null) selector = SelectorProvider.provider().openSelector();
    channel.register( selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
    
    pending.put( channel, connection);
    channel.connect( address);
  }
  
  /**
   * Process next event from the socket and wait forever.
   */
  public void process() throws IOException
  {
    process( 0);
  }
  
  /**
   * Process next event from the socket.
   * @param timeout The amount of time to wait.
   */
  public void process( int timeout) throws IOException
  {
    if ( selector.select( timeout) == 0) return; 
    
    Set<SelectionKey> readyKeys = selector.selectedKeys();
    for( SelectionKey readyKey: readyKeys)
    {
      SocketChannel channel = (SocketChannel)readyKey.channel();
      try
      {
        int readyOps = readyKey.readyOps();
        if ( (readyOps & SelectionKey.OP_CONNECT) != 0)
        {
          Connection connection = pending.remove( channel);
          try
          {
            channel.finishConnect();
            channel.register( selector, SelectionKey.OP_READ);
            connected.put( channel, connection);
            connection.connected( channel);
          }
          catch( IOException e)
          {
            connection.close( false);
          }
        }
        else
        {
          Connection connection = connected.get( channel);
          try
          {
            int nread = connection.read();
            if ( nread < 0)
            {
              connected.remove( channel);
              connection.close( true);
            }
          }
          catch( IOException e)
          {
            connection.close( false);
          }
        }
      }
      catch( CancelledKeyException e)
      {
        Connection connection = connected.get( address);
        connection.close( false);
      }
    }
    
    readyKeys.clear();
  }
  
  private InetSocketAddress address;
  private Selector selector;
  private Map<Channel, Connection> pending;
  private Map<Channel, Connection> connected;
  private IRecipient recipient;
  
  public static void main( String[] args) throws Exception
  {
    String[] split = args[ 0].split( "[:]");
    
    IRecipient recipient = new IRecipient() {
      public void connected( Connection connection)
      {
        System.out.println( "Connected.");
      }
      public void disconnected( Connection connection, boolean nice)
      {
        System.out.println( "Disconnected: "+nice);
      }
      public int received( Connection connection, ByteBuffer buffer)
      {
        return 0;
      }
    };
    
    Client clients = new Client( recipient);
    Connection connection = clients.connect( split[ 0], Integer.parseInt( split[ 1]));
    clients.process();
    
    while( true)
    {
      System.out.printf( "%s\n", new Date());
      clients.process( 10000);
      clients.reconnect( connection); 
    }
  }
}
