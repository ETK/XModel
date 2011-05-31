package org.xmodel.net.stream;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.xmodel.log.Log;
import org.xmodel.net.INetFramer;
import org.xmodel.net.INetReceiver;
import org.xmodel.net.INetSender;

/**
 * A class that represents a TCP connection.
 */
public class Connection implements INetSender
{
  /**
   * Create a new connection.
   * @param agent The agent that created the connection.
   * @param framer The message framer.
   * @param receiver The primary receiver.
   */
  Connection( ITcpAgent agent, INetFramer framer, INetReceiver receiver)
  {
    this.agent = agent;
    this.framer = framer;
    this.receiver = receiver;
  }
  
  /**
   * @return Returns the agent that created this connection.
   */
  public ITcpAgent getAgent()
  {
    return agent;
  }
  
  /**
   * @return Returns the remote host address.
   */
  public String getAddress()
  {
    return address.getAddress().getHostAddress();
  }
  
  /**
   * @return Returns the remote host port.
   */
  public int getPort()
  {
    return address.getPort();
  }
  
  /**
   * @return Returns null or the underlying channel.
   */
  public SocketChannel getChannel()
  {
    return channel;
  }

  /**
   * Called when the channel becomes connected.
   * @param channel The channel.
   */
  void connected( SocketChannel channel)
  {
    this.channel = channel;
    this.address = (InetSocketAddress)channel.socket().getRemoteSocketAddress();
    if ( sendBuffer == null) sendBuffer = ByteBuffer.allocateDirect( 4096);
  }
  
  /**
   * Close this connection.
   * @param nice True if the connection was closed nicely.
   */
  public void close( boolean nice)
  {
    try 
    {
      if ( channel != null) channel.close();
    }
    catch( IOException e)
    {
    }
    channel = null;
  }
  
  /**
   * @return Returns true if the connection is still open.
   */
  public boolean isOpen()
  {
    return channel != null;
  }

  /**
   * @return Returns the number of bytes read.
   */
  int read() throws IOException
  {
    int nread = channel.read( sendBuffer);
    sendBuffer.flip();
    
    while( true)
    {
      sendBuffer.mark();

      try
      {
        int consume = framer.frame( sendBuffer);
        int limit = sendBuffer.limit();
        if ( limit > consume)
        {
          // set limit to end of message
          sendBuffer.limit( sendBuffer.position() + consume);
          
          // pass message to receivers
          sendBuffer.reset();
          
          System.out.printf( "read: ");
          for( int i=sendBuffer.position(); i<sendBuffer.limit(); i++)
            System.out.printf(  "%02X", sendBuffer.get( i));
          System.out.println( "");
          
          receiver.receive( this, sendBuffer);
          
          // restore buffer limit and compact
          sendBuffer.limit( limit);
        }
        else
        {
          break;
        }
      }
      catch( BufferUnderflowException e)
      {
        break;
      }
    }
    
    sendBuffer.reset();
    sendBuffer.compact();
    
    return nread;
  }

  /**
   * Write the content of the specified buffer to the connection.
   * @param buffer The buffer.
   */
  public void write( ByteBuffer buffer) throws IOException
  {
    System.out.printf( "write: pos=%d, lim=%d: ", buffer.position(), buffer.limit());
    for( int i=buffer.position(); i<buffer.limit(); i++) System.out.printf( "%02X", buffer.get( i));
    System.out.println( "");
    
    channel.write( buffer);
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.net.nu.INetSender#send(java.nio.ByteBuffer)
   */
  @Override
  public boolean send( ByteBuffer buffer)
  {
    try
    {
      write( buffer);
      return true;
    }
    catch( IOException e)
    {
      return false;
    }
  }

  /* (non-Javadoc)
   * @see org.xmodel.net.nu.INetSender#send(java.nio.ByteBuffer, int)
   */
  @Override
  public ByteBuffer send( ByteBuffer buffer, int timeout)
  {
    receiveBuffer = null;
    send( buffer);
    try
    {
      agent.process( timeout);
      return receiveBuffer;
    }
    catch( Exception e)
    {
      log.exception( e);
    }
    return null;
  }

  /* (non-Javadoc)
   * @see org.xmodel.net.nu.INetSender#close()
   */
  @Override
  public void close()
  {
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return String.format( "%s:%d", getAddress(), getPort());
  }

  private ITcpAgent agent;
  private INetFramer framer;
  private INetReceiver receiver;
  private InetSocketAddress address;
  private SocketChannel channel;
  private ByteBuffer sendBuffer;
  private ByteBuffer receiveBuffer;
  private Log log = Log.getLog( "org.xmodel.net.stream");
}
