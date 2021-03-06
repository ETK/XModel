/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * ICheck.java
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
import org.xmodel.IModelObject;


/**
 * An interface for classes which perform validation at specific schema loci.
 */
public interface ICheck
{
  /**
   * Validate the specified document locus against the schema locus.
   * @param documentLocus The document locus.
   * @return Returns true if the document locus is valid.
   */
  public boolean validate( IModelObject documentLocus);
  
  /**
   * Returns the errors generated by this schema check.
   * @param errors The list where the errors will be added.
   */
  public void getErrors( List<SchemaError> errors);
  
  /**
   * Returns the schema locus validated by this instance.
   * @return Returns the schema locus validated by this instance.
   */
  public IModelObject getSchemaLocus();
}
