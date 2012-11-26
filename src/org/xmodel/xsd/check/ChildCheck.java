/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * ChildCheck.java
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
package org.xmodel.xsd.check;

import java.util.List;
import org.xmodel.INode;
import org.xmodel.Xlate;
import org.xmodel.xsd.check.SchemaError.Type;


public class ChildCheck extends ConstraintCheck
{
  public ChildCheck( INode schemaLocus)
  {
    super( schemaLocus);
    childType = Xlate.get( schemaLocus, "");
  }

  /* (non-Javadoc)
   * @see org.xmodel.xsd.nu.ConstraintCheck#validateOnce(org.xmodel.IModelObject, int, int)
   */
  @Override
  public boolean validateOnce( INode documentLocus, int start, int end)
  {
    index = start;
    INode child = documentLocus.getChild( index);
    if ( child != null && child.isType( childType)) 
    {
      index++;
      return true;
    }
    return false;
  }

  /* (non-Javadoc)
   * @see org.xmodel.xsd.check.AbstractCheck#getErrors(java.util.List)
   */
  @Override
  public void getErrors( List<SchemaError> errors)
  {
    if ( errorLocus != null)
    {
      if ( occurrences < getMinOccurrences()) 
        errors.add( new SchemaError( Type.missingElement, getSchemaLocus(), errorLocus));
      if ( occurrences > getMaxOccurrences()) 
        errors.add( new SchemaError( Type.illegalElement, getSchemaLocus(), errorLocus));
    }
  }
  
  private String childType;
}
