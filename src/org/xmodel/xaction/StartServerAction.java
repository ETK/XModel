/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * StartServerAction.java
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
package org.xmodel.xaction;

import java.io.IOException;

import org.xmodel.IModelObject;
import org.xmodel.IModelObjectFactory;
import org.xmodel.Xlate;
import org.xmodel.log.Log;
import org.xmodel.net.Server;
import org.xmodel.xpath.expression.IContext;
import org.xmodel.xpath.expression.IExpression;
import org.xmodel.xpath.expression.StatefulContext;


/**
 * An XAction which creates and starts a ModelServer.
 */
public class StartServerAction extends GuardedAction
{
  /* (non-Javadoc)
   * @see org.xmodel.xaction.GuardedAction#configure(org.xmodel.xaction.XActionDocument)
   */
  @Override
  public void configure( XActionDocument document)
  {
    super.configure( document);
    
    factory = getFactory( document.getRoot());
    
    // get assign
    assign = Xlate.get( document.getRoot(), "assign", (String)null);
    
    // get host and port
    hostExpr = document.getExpression( "host", true);
    portExpr = document.getExpression( "port", true);
    
    // get context expression
    sourceExpr = document.getExpression();
  }

  /* (non-Javadoc)
   * @see org.xmodel.xaction.GuardedAction#doAction(org.xmodel.xpath.expression.IContext)
   */
  @Override
  protected Object[] doAction( IContext context)
  {
    // get context
    IModelObject source = (sourceExpr != null)? sourceExpr.queryFirst( context): null;
    
    // start server
    try
    {
      String host = (hostExpr != null)? hostExpr.evaluateString( context): "127.0.0.1";
      int port = (portExpr != null)? (int)portExpr.evaluateNumber( context): 27700;
      
      server = new Server( host, port);
      server.setContext( (source != null)? new StatefulContext( context.getScope(), source): context);
      server.start();
      
      StatefulContext stateful = (StatefulContext)context;
      IModelObject object = factory.createObject( null, "server");
      object.setValue( server);
      stateful.set( assign, object);
    }
    catch( IOException e)
    {
      log.exception( e);
    }
    
    return null;
  }
  
  private static Log log = Log.getLog( "org.xmodel.xaction");
  
  private IModelObjectFactory factory;
  private Server server;
  private String assign;
  private IExpression hostExpr;
  private IExpression portExpr;
  private IExpression sourceExpr;
}
