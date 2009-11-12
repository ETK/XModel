/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * DeepListener.java
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
package org.xmodel.net.caching;

import org.xmodel.IModelObject;
import org.xmodel.external.NonSyncingListener;
import org.xmodel.net.caching.QueryProtocol.ServerQuery;

/**
 * A non-syncing listener that provides notifications to a remote client.
 */
public class DeepListener extends NonSyncingListener
{
  public DeepListener( QueryProtocol protocol, ServerQuery query)
  {
    this.protocol = protocol;
    this.query = query;
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.external.NonSyncingListener#notifyAddChild(org.xmodel.IModelObject, org.xmodel.IModelObject, int)
   */
  @Override
  public void notifyAddChild( IModelObject parent, IModelObject child, int index)
  {
    super.notifyAddChild( parent, child, index);
    protocol.sendObjectAddUpdate( query, parent, child, index);
  }

  /* (non-Javadoc)
   * @see org.xmodel.external.NonSyncingListener#notifyRemoveChild(org.xmodel.IModelObject, org.xmodel.IModelObject, int)
   */
  @Override
  public void notifyRemoveChild( IModelObject parent, IModelObject child, int index)
  {
    super.notifyRemoveChild( parent, child, index);
    protocol.sendObjectRemoveUpdate( query, parent, child, index);
  }

  /* (non-Javadoc)
   * @see org.xmodel.ModelListener#notifyChange(org.xmodel.IModelObject, java.lang.String, java.lang.Object, java.lang.Object)
   */
  @Override
  public void notifyChange( IModelObject object, String attrName, Object newValue, Object oldValue)
  {
    protocol.sendAttributeChangeUpdate( query, object, attrName, (newValue == null)? null: newValue.toString());
  }

  /* (non-Javadoc)
   * @see org.xmodel.ModelListener#notifyClear(org.xmodel.IModelObject, java.lang.String, java.lang.Object)
   */
  @Override
  public void notifyClear( IModelObject object, String attrName, Object oldValue)
  {
    protocol.sendAttributeClearUpdate( query, object, attrName);
  }
  
  private QueryProtocol protocol;
  private ServerQuery query;
}
