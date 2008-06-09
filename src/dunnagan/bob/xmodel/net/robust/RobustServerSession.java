/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.net.robust;

import java.io.IOException;
import java.net.Socket;

/**
 * An extension of RobustSession which can be used as a ServerSession.
 */
public class RobustServerSession extends RobustSession implements IServerSession
{
  /**
   * Create a blocking, bounded queue on the specified session.
   * The session is owned by this instance and will be closed when the queue is closed.
   * @param session The session.
   * @param queueLength The maximum length of the queue.
   */
  public RobustServerSession( ServerSession session, int queueLength)
  {
    super( session, queueLength);
    server = session.getServer();
    session.addListener( handler);
  }

  /**
   * Create a blocking, bounded queue on the specified session.
   * The session is owned by this instance and will be closed when the queue is closed.
   * @param session The session.
   * @param queueLength The maximum length of the queue.
   * @param writeTimeout The time to wait before write failure (-1 means infinite).
   */
  public RobustServerSession( ServerSession session, int queueLength, int writeTimeout)
  {
    super( session, queueLength, writeTimeout);
    server = session.getServer();
    session.addListener( handler);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.net.robust.IServerSession#getServer()
   */
  public Server getServer()
  {
    return ((IServerSession)session).getServer();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.net.IServerSession#initialize(java.net.Socket)
   */
  public void initialize( Socket newSocket) throws IOException
  {
    ((ServerSession)getDelegate()).initialize( newSocket);
  }
  
  private final ISession.Listener handler = new ISession.Listener() {
    public void notifyOpen( ISession session)
    {
    }
    public void notifyClose( ISession session)
    {
      server.removeSession( RobustServerSession.this);
    }
    public void notifyConnect( ISession session)
    {
    }
    public void notifyDisconnect( ISession session)
    {
    }
  };
  
  private Server server;
}
