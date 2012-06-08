package org.xmodel.net;

import java.io.IOException;
import java.util.List;

import org.xmodel.net.stream.Connection;
import org.xmodel.net.stream.TcpServer;

/**
 * A class that implements the server-side of the network caching policy protocol.
 * This class is not thread-safe.
 */
public class Server extends Protocol
{
  public static int defaultPort = 27613;
  
  /**
   * Create a server bound to the specified local address and port.
   * @param host The local interface address.
   * @param port The local interface port.
   * @param timeout The timeout for message response.
   * @param debug True if debugging is enabled on this server.
   */
  public Server( String host, int port, int timeout) throws IOException
  {
    super( timeout);
    server = new TcpServer( host, port, this);
  }

  /**
   * Get server address.
   * @return Returns the server address.
   */
  public String getHost()
  {
    return server.getHost();
  }
  
  /**
   * Get server port.
   * @return Returns the server port.
   */
  public int getPort()
  {
    return server.getPort();
  }
  
  /**
   * Start the server.
   * @param daemon True if the server thread should be a daemon.
   */
  public void start( boolean daemon) throws IOException
  {
    server.start( daemon);
  }
  
  /**
   * Stop the server.
   */
  public void stop()
  {
    server.stop();
  }

  /**
   * Returns the Connection instances to the specified remote host.
   * @param host The remote host.
   * @param port The remote port.
   * @return Returns the Connection instances to the specified remote host.
   */
  public List<Connection> getConnections( String host, int port)
  {
    return server.getConnections( host, port);
  }
  
  private TcpServer server;
}
