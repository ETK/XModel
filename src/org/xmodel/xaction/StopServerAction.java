/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * StopServerAction.java
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

import org.xmodel.log.SLog;
import org.xmodel.net.Server;
import org.xmodel.xpath.expression.IContext;
import org.xmodel.xpath.expression.IExpression;

/**
 * An XAction that terminates a previously started server.
 * @see org.xmodel.xaction.StartServerAction StartServerAction.
 */
public class StopServerAction extends GuardedAction
{
  /* (non-Javadoc)
   * @see org.xmodel.xaction.GuardedAction#configure(org.xmodel.xaction.XActionDocument)
   */
  @Override
  public void configure( XActionDocument document)
  {
    super.configure( document);
    hostExpr = document.getExpression( "host", true);
    portExpr = document.getExpression( "port", true);
  }

  /* (non-Javadoc)
   * @see org.xmodel.xaction.GuardedAction#doAction(org.xmodel.xpath.expression.IContext)
   */
  @Override
  protected Object[] doAction( IContext context)
  {
    try
    {
      String host = (hostExpr != null)? hostExpr.evaluateString( context): "localhost";
      int port = (portExpr != null)? (int)portExpr.evaluateNumber( context): Server.defaultPort;
      String var = String.format( "%s:%d", host, port);
      StartServerAction.stop( context, var);
    }
    catch( IOException e)
    {
      SLog.warnf( this, "Server shutdown failed because: %s", e.getMessage());
    }
    
    return null;
  }

  private IExpression hostExpr;
  private IExpression portExpr;
}
