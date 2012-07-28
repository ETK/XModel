/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * InvokeAction.java
 * 
 * Copyright 2009 Robert Arvin Dunnagan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
  /*
 * XModel (XML Application Data Modeling Framework)
 * Author: Bob Dunnagan (bdunnagan@nc.rr.com)
 * Copyright Bob Dunnagan 2009. All rights reserved.
 */
package org.xmodel.xaction;

import java.io.IOException;
import java.util.List;
import org.xmodel.IModelObject;
import org.xmodel.ModelAlgorithms;
import org.xmodel.ModelObject;
import org.xmodel.log.Log;
import org.xmodel.log.SLog;
import org.xmodel.net.Client;
import org.xmodel.net.ICallback;
import org.xmodel.net.Session;
import org.xmodel.xml.IXmlIO.Style;
import org.xmodel.xml.XmlIO;
import org.xmodel.xpath.expression.IContext;
import org.xmodel.xpath.expression.IExpression;
import org.xmodel.xpath.expression.StatefulContext;
import org.xmodel.xpath.variable.IVariableScope;

/**
 * An XAction that executes a script identified by an expression.
 * 
 * TODO: RunAction has several problems:
 *       
 * 1. There is no control over the variables that get returned in the scope of a remote invocation,
 *    which leads to huge execution responses.
 * 
 * 2. There is no support for the basic mechanism of function arguments.
 *    So, while it is easy to assign variables expected by the target of the RunAction,
 *    it is cumbersome to insulate the code surrounding the invocation fromt these variable
 *    assignments.
 */
public class RunAction extends GuardedAction
{
  /* (non-Javadoc)
   * @see org.xmodel.xaction.GuardedAction#configure(org.xmodel.xaction.XActionDocument)
   */
  @Override
  public void configure( XActionDocument document)
  {
    super.configure( document);

    var = Conventions.getVarName( document.getRoot(), false, "assign");    
    contextExpr = document.getExpression( "context", true);
    scriptExpr = document.getExpression();
    
    varsExpr = document.getExpression( "vars", true);
    hostExpr = document.getExpression( "host", true);
    portExpr = document.getExpression( "port", true);
    timeoutExpr = document.getExpression( "timeout", true);
    
    onCompleteExpr = document.getExpression( "onComplete", true);
    onSuccessExpr = document.getExpression( "onSuccess", true);
    onErrorExpr = document.getExpression( "onError", true);
  }

  /* (non-Javadoc)
   * @see org.xmodel.xaction.GuardedAction#doAction(org.xmodel.xpath.expression.IContext)
   */
  @Override 
  protected Object[] doAction( IContext context)
  {
    if ( hostExpr == null) runLocal( context); else runRemote( context);   
    return null;
  }

  /**
   * Perform local execution.
   * @param context The context.
   * @return Returns the execution result.
   */
  @SuppressWarnings("unchecked")
  private Object[] runLocal( IContext context)
  {
    Object[] results = null;

    IXAction script = getScript( context, scriptExpr);
    if ( script == null) return null;
    
    if ( contextExpr != null)
    {
      for( IModelObject localNode: contextExpr.query( context, null))
      {
        StatefulContext local = new StatefulContext( context, localNode);
        results = script.run( local);
      }
    }
    else
    {
      results = script.run( context);
    }
    
    if ( var != null && results != null && results.length > 0)
    {
      Object result = results[ 0];
      IVariableScope scope = context.getScope();
      if ( result instanceof List) scope.set( var, (List<IModelObject>)result);
      else if ( result instanceof String) scope.set( var, result.toString());
      else if ( result instanceof Number) scope.set( var, (Number)result);
      else if ( result instanceof Boolean) scope.set( var, (Boolean)result);
    }
    
    return null;
  }
  
  /**
   * Perform remote execution.
   * @param context The context.
   * @return Returns the execution result.
   */
  private Object[] runRemote( IContext context)
  {
    String host = (hostExpr != null)? hostExpr.evaluateString( context): null;
    int port = (portExpr != null)? (int)portExpr.evaluateNumber( context): -1;
    int timeout = (timeoutExpr != null)? (int)timeoutExpr.evaluateNumber( context): Integer.MAX_VALUE;

    IXAction onComplete = (onCompleteExpr != null)? getScript( context, onCompleteExpr): null;
    IXAction onSuccess = (onSuccessExpr != null)? getScript( context, onSuccessExpr): null;
    IXAction onError = (onErrorExpr != null)? getScript( context, onErrorExpr): null;
    
    String vars = (varsExpr != null)? varsExpr.evaluateString( context): "";
    String[] varArray = vars.split( "\\s*,\\s*");

    Client client = null;
    try
    {
      if ( log.isLevelEnabled( Log.debug))
      {
        log.debugf( "Remote on %s:%d, %s ...", host, port, getScriptDescription( context));
      }

      client = new Client( host, port, timeout, false);
      Session session = client.connect( timeout);
      
      // execute synchronously unless on of the async callback scripts exists
      if ( onComplete == null && onSuccess == null && onError == null)
      {
        try
        {
          Object[] result = session.execute( (StatefulContext)context, varArray, getScriptNode( context), timeout);
          if ( var != null && result != null && result.length > 0) context.getScope().set( var, result[ 0]);
        }
        finally
        {
          if ( client != null) try { client.disconnect();} catch( Exception e) {}
        }
      }
      else
      {
        Callback callback = new Callback( client, onComplete, onSuccess, onError);
        if ( session != null)
        {
          session.execute( (StatefulContext)context, varArray, getScriptNode( context), callback, timeout);
        }
        else
        {
          callback.onError( context);
        }
      }
      
      log.debug( "Finished remote.");
      return null;
    }
    catch( IOException e)
    {
      throw new XActionException( e);
    }
  }
  
  /**
   * Get the script node to be executed.
   * @param context The context.
   * @return Returns null or the script node.
   */
  private IModelObject getScriptNode( IContext context)
  {
    if ( scriptExpr != null) return scriptExpr.queryFirst( context);
    
    if ( inline == null)
    {
      inline = new ModelObject( "script");
      ModelAlgorithms.copyChildren( document.getRoot(), inline, null);
    }
    return inline;
  }
  
  /**
   * Get the script from the specified expression.
   * @param context The context.
   * @param expression The script expression.
   * @return Returns null or the script.
   */
  private IXAction getScript( IContext context, IExpression expression)
  {
    IXAction script = null;
    if ( expression != null)
    {
      IModelObject scriptNode = expression.queryFirst( context);
      CompiledAttribute attribute = (scriptNode != null)? (CompiledAttribute)scriptNode.getAttribute( "compiled"): null;
      if ( attribute != null) script = attribute.script;
      if ( script == null)
      {
        script = document.createScript( scriptNode);
        if ( script != null)
        {
          scriptNode.setAttribute( "compiled", new CompiledAttribute( script));
        }
        else
        {
          SLog.warnf( this, "Script not found: %s", expression);
        }
      }
    }
    return script;
  }
  
  private String getScriptDescription( IContext context)
  {
    if ( scriptExpr != null) return scriptExpr.toString();
    
    IModelObject node = getScriptNode( context);
    return XmlIO.write( Style.printable, node);
  }
  
  private final static class CompiledAttribute
  {
    public CompiledAttribute( IXAction script)
    {
      this.script = script;
    }
    
    public IXAction script;
  }
  
  private final static class Callback implements ICallback
  {
    public Callback( Client client, IXAction onComplete, IXAction onSuccess, IXAction onError)
    {
      this.client = client;
      this.onComplete = onComplete;
      this.onSuccess = onSuccess;
      this.onError = onError;
    }
    
    /* (non-Javadoc)
     * @see org.xmodel.net.ICallback#onComplete(org.xmodel.xpath.expression.IContext)
     */
    @Override
    public void onComplete( IContext context)
    {
      if ( onComplete != null) onComplete.run( context);
      if ( client != null) try { client.disconnect();} catch( Exception e) {}
    }

    /* (non-Javadoc)
     * @see org.xmodel.net.ICallback#onSuccess(org.xmodel.xpath.expression.IContext)
     */
    @Override
    public void onSuccess( IContext context)
    {
      if ( onSuccess != null) onSuccess.run( context);
    }

    /* (non-Javadoc)
     * @see org.xmodel.net.ICallback#onError(org.xmodel.xpath.expression.IContext)
     */
    @Override
    public void onError( IContext context)
    {
      if ( onError != null) onError.run( context);
    }
    
    private Client client;
    private IXAction onComplete;
    private IXAction onSuccess;
    private IXAction onError;
  }

  private final static Log log = Log.getLog( RunAction.class);
  
  private String var;
  private IExpression varsExpr;
  private IExpression contextExpr;
  private IExpression hostExpr;
  private IExpression portExpr;
  private IExpression timeoutExpr;
  private IExpression scriptExpr;
  private IModelObject inline;
  private IExpression onCompleteExpr;
  private IExpression onSuccessExpr;
  private IExpression onErrorExpr;
}
