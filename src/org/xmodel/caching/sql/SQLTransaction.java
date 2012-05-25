package org.xmodel.caching.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.xmodel.external.CachingException;
import org.xmodel.external.ITransaction;
import org.xmodel.log.SLog;

/**
 * An implementation of ITransaction for SQL databases.
 */
public class SQLTransaction implements ITransaction
{
  public SQLTransaction( SQLTableCachingPolicy cachingPolicy)
  {
    this.cachingPolicy = cachingPolicy;
    this.state = State.ready;
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.ITransaction#state()
   */
  @Override
  public State state()
  {
    return state;
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.ITransaction#lock(int)
   */
  @Override
  public boolean lock( int timeout)
  {
    try
    {
      connection = cachingPolicy.getSQLProvider().leaseConnection();
      connection.setAutoCommit( false);
      state = State.lock;
      return true;
    }
    catch( SQLException e)
    {
      SLog.errorf( this, e.getMessage());
      return false;
    }
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.ITransaction#unlock()
   */
  @Override
  public void unlock()
  {
    try
    {
      connection.setAutoCommit( false);
      state = State.ready;
    }
    catch( SQLException e)
    {
      SLog.error( this, e.getMessage());
      
      throw new CachingException( String.format(
          "Failed to restore database to auto commit mode: %s",
          connection), e);
    }
    finally
    {
      cachingPolicy.getSQLProvider().releaseConnection( connection);
      cachingPolicy.transactionComplete( this);
      connection = null;
    }
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.ITransaction#commit()
   */
  @Override
  public boolean commit()
  {
    try
    {
      cachingPolicy.commit( connection);
      connection.commit();
      state = State.commit;
      return true;
    }
    catch( SQLException e)
    {
      rollback();
      
      SLog.error( this, e.getMessage());
      state = State.error;
      return false;
    }
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.ITransaction#rollback()
   */
  @Override
  public boolean rollback()
  {
    try
    {
      connection.rollback();
      state = State.rollback;
      return true;
    }
    catch( SQLException e)
    {
      SLog.error( this, e.getMessage());
      state = State.error;
      return false;
    }
  }

  private SQLTableCachingPolicy cachingPolicy;
  private Connection connection;
  private State state;
}