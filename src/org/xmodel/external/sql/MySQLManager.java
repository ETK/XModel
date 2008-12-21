/*
 * XModel (XML Application Data Modeling Framework)
 * Author: Bob Dunnagan (bdunnagan@nc.rr.com)
 * Copyright Bob Dunnagan 2009. All rights reserved.
 */
package org.xmodel.external.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import org.xmodel.IModelObject;
import org.xmodel.Xlate;
import org.xmodel.external.CachingException;

/**
 * An SQLManager for the MySQL database.
 */
public class MySQLManager implements SQLManager
{
  public MySQLManager()
  {
    drivers = new HashMap<String, JDCConnectionDriver>();
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.external.caching.SQLCachingPolicy.SQLManager#configure(org.xmodel.IModelObject)
   */
  public void configure( IModelObject annotation) throws CachingException
  {
    String database = Xlate.childGet( annotation, "database", (String)null);
    if ( database == null) throw new CachingException( "Database not defined in annotation: "+annotation);
    
    url = urlPrefix + database;
   
    login = Xlate.childGet( annotation, "login", (String)null);
    if ( database == null) throw new CachingException( "Login not defined in annotation: "+annotation);
    
    password = Xlate.childGet( annotation, "password", (String)null);
    if ( database == null) throw new CachingException( "Password not defined in annotation: "+annotation);
  }

  /**
   * Returns a connection to the database.
   * @return Returns a connection to the database.
   */
  public Connection getConnection() throws CachingException
  {
    try
    {
      // load jdbc driver
      if ( !init)
      {
        init = true;
        Class.forName( driverClassName);    
        
      }
    
      JDCConnectionDriver driver = null;
      synchronized( drivers)
      {
        driver = drivers.get( url);
        if ( driver == null)
        {
          driver = new JDCConnectionDriver( driverClassName, url, login, password);
          drivers.put( url, driver);
        }
      }
      
      // return connection
      return DriverManager.getConnection( url, login, password);    
    }
    catch( Exception e)
    {
      throw new CachingException( "Unable to open connection.", e);
    }
  }  
  
  /* (non-Javadoc)
   * @see org.xmodel.external.caching.SQLCachingPolicy.SQLManager#prepareStatement(java.lang.String)
   */
  public PreparedStatement prepareStatement( String sql) throws CachingException
  {
    try
    {
      Connection connection = getConnection();
      return connection.prepareStatement( sql);
    }
    catch( Exception e)
    {
      throw new CachingException( "Unable to create prepared statement for sql: "+sql, e);
    }
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.sql.SQLManager#prepareStatement(java.lang.String, int, int, int)
   */
  public PreparedStatement prepareStatement( String sql, int type, int concurrency, int holdability) throws CachingException
  {
    try
    {
      Connection connection = getConnection();
      return connection.prepareStatement( sql, type, concurrency, holdability);
    }
    catch( Exception e)
    {
      throw new CachingException( "Unable to create prepared statement for sql: "+sql, e);
    }
  }

  private final static String urlPrefix = "jdbc:mysql://127.0.0.1/";
  private final static String driverClassName = "com.mysql.jdbc.Driver";
  
  private static Map<String, JDCConnectionDriver> drivers;
  
  private boolean init;
  private String url;
  private String login;
  private String password;
}
