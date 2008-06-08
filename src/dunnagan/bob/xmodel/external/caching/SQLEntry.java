/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.external.caching;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import dunnagan.bob.xmodel.IModelObject;
import dunnagan.bob.xmodel.Xlate;
import dunnagan.bob.xmodel.external.CachingException;
import dunnagan.bob.xmodel.external.IExternalReference;
import dunnagan.bob.xmodel.xpath.XPath;
import dunnagan.bob.xmodel.xpath.expression.Context;
import dunnagan.bob.xmodel.xpath.expression.IExpression;

/**
 * A convenience class which handles the SQL statement definitions in the configuration of an SQLCachingPolicy.
 * The class is responsible for evaluating parameter expressions and assigning parameters to a statement.
 * <p>
 * SQL statement annotations have the form:
 * <sql>
 *   <statement>...</statement>
 *   <param>...</param>
 *   <param>...</param>
 *   ...
 * </sql>
 */
public class SQLEntry
{
  /**
   * Create an SQLStatement object for the specified <sql> annotation.
   * @param annotation The <sql> annotation.
   */
  public SQLEntry( IModelObject annotation)
  {
    // get statement
    sql = Xlate.childGet( annotation, "statement", "");
    
    // get parameters
    List<IModelObject> paramNodes = annotation.getChildren( "param");
    params = new IExpression[ paramNodes.size()];
    for( int i=0; i<paramNodes.size(); i++)
      params[ i] = XPath.createExpression( Xlate.get( paramNodes.get( i), ""));
  }
  
  /**
   * Returns the SQL string for this statement.
   * @return Returns the SQL string for this statement.
   */
  public String getSQL()
  {
    return sql;
  }
  
  /**
   * Returns the name of the tables defined in the query.
   * @param statement The PreparedStatement from which the ResultMetaData will be obtained.
   * @return Returns the name of the tables defined in the query.
   */
  public String[] getTables( PreparedStatement statement) throws CachingException
  {
    if ( tables != null) return tables;
    
    try
    {
      ResultSetMetaData meta = statement.getMetaData();
      tables = new String[ meta.getColumnCount()];
      for( int i=0; i<tables.length; i++) tables[ i] = meta.getTableName( i);
      return tables;
    }
    catch( SQLException e)
    {
      throw new CachingException( "Unable to get table names from metadata.", e);
    }
  }
  
  /**
   * Returns the names of the columns.
   * @param statement The PreparedStatement from which the ResultMetaData will be obtained.
   * @return Returns the names of the columns.
   */
  public String[] getColumns( PreparedStatement statement) throws CachingException
  {
    if ( columns != null) return columns;

    try
    {
      ResultSetMetaData meta = statement.getMetaData();
      columns = new String[ meta.getColumnCount()];
      for( int i=0; i<columns.length; i++) columns[ i] = meta.getColumnName( i);
      return columns;
    }
    catch( SQLException e)
    {
      throw new CachingException( "Unable to get column names from metadata.", e);
    }
  }
  
  /**
   * Evaluate the parameter expressions with the specified reference context and assign to statement.
   * @param reference The context of the parameter evaluations.
   * @param statement The statement.
   */
  public void assignParameters( IExternalReference reference, PreparedStatement statement) throws CachingException
  {
    try
    {
      for( int i=0; i<params.length; i++)
      {
        IExpression param = params[ i];
        switch( param.getType())
        {
          case NODES:
            throw new CachingException( "Illegal return type (NODES) for expression: "+param);
          
          case STRING:
          {
            String value = param.evaluateString( new Context( reference));
            statement.setString( i, value);
          }
          break;
          
          case NUMBER:
          {
            double value = param.evaluateNumber( new Context( reference));
            int intValue = (int)Math.floor( value);
            if ( intValue == value) statement.setInt( i, intValue); else statement.setDouble( i, value);
          }
          break;
          
          case BOOLEAN:
          {
            boolean value = param.evaluateBoolean( new Context( reference));
            statement.setBoolean( i, value);
          }
          break;
        }
      }
    }
    catch( SQLException e)
    {
      throw new CachingException( "Error setting sql parameters for reference: "+reference, e);
    }
  }
  
  String sql;
  String[] tables;
  String[] columns;
  IExpression[] params;
}
