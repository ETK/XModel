/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * CancelTriggerAction.java
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

import org.xmodel.IModelObject;
import org.xmodel.log.SLog;
import org.xmodel.xaction.trigger.ITrigger;
import org.xmodel.xpath.expression.IContext;
import org.xmodel.xpath.expression.IExpression;

/**
 * An XAction which cancels a previously created trigger. The trigger must have
 * been successfully assigned to its instance variable. The trigger must be 
 * cancelled within the same context in which it was created.
 */
public class CancelTriggerAction extends GuardedAction
{
  /* (non-Javadoc)
   * @see org.xmodel.xaction.GuardedAction#configure(org.xmodel.xaction.XActionDocument)
   */
  @Override
  public void configure( XActionDocument document)
  {
    super.configure( document);
    instanceExpr = document.getExpression();
  }

  /* (non-Javadoc)
   * @see org.xmodel.xaction.GuardedAction#doAction(org.xmodel.xpath.expression.IContext)
   */
  @Override
  protected Object[] doAction( IContext context)
  {
    for( IModelObject holder: instanceExpr.query( context, null))
    {
      ITrigger trigger = (ITrigger)holder.getValue();
      if ( trigger != null) 
      {
        holder.setValue( null);
        trigger.deactivate( context);
        SLog.debug( this, trigger);
      }
    }
    
    return null;
  }

  private IExpression instanceExpr;
}
