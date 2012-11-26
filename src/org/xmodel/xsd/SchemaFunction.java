/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * SchemaFunction.java
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
package org.xmodel.xsd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xmodel.INode;
import org.xmodel.xpath.XPath;
import org.xmodel.xpath.expression.ExpressionException;
import org.xmodel.xpath.expression.IContext;
import org.xmodel.xpath.expression.IExpression;
import org.xmodel.xpath.function.Function;


/**
 * A custom XPath function which returns an element or attribute definition from a simplified schema.
 * The first argument is the root of the simplified schema. The second argument is either an node-set
 * or a string. If the second argument is a node-set, then the schema is searched for a matching 
 * schema definition for each node (see SchemaTrace for details). If the second argument is a string,
 * then that path is returned from the schema. The path may start with either a global complex type
 * or a global element.
 */
public class SchemaFunction extends Function
{
  public final static String name = "schema";
  
  /* (non-Javadoc)
   * @see org.xmodel.xpath.expression.IExpression#getName()
   */
  public String getName()
  {
    return name;
  }

  /* (non-Javadoc)
   * @see org.xmodel.xpath.expression.IExpression#getType()
   */
  public ResultType getType()
  {
    return ResultType.NODES;
  }

  /* (non-Javadoc)
   * @see org.xmodel.xpath.expression.Expression#evaluateNodes(org.xmodel.xpath.expression.IContext)
   */
  @Override
  public List<INode> evaluateNodes( IContext context) throws ExpressionException
  {
    assertArgs( 2, 2);
    assertType( context, 1, ResultType.NODES);
    
    INode schemaRoot = getArgument( 0).queryFirst( context);
    if ( schemaRoot == null) return Collections.emptyList();
    
    List<INode> result = new ArrayList<INode>();
    
    ResultType type = getArgument( 1).getType( context);
    if ( type == ResultType.NODES)
    {
      for( INode node: getArgument( 1).evaluateNodes( context))
      {
        INode schema = Schema.findSchema( schemaRoot, node); 
        if ( schema != null) result.add( schema);
      }
    }
    else if ( type == ResultType.STRING) 
    {
      String path = getArgument( 1).evaluateString( context);
      String[] parts = path.split( "/");
      globalFinder.setVariable( "name", parts[ 0]);
      INode schema = globalFinder.queryFirst( schemaRoot);
      for( int i=1; i<parts.length; i++)
      {
        INode childSchema = findChild( schema, parts[ i]);
        if ( childSchema == null) break;
        schema = childSchema;
      }
      if ( schema != null) result.add( schema);
    }
    else
    {
      throw new ExpressionException( this, "Second argument must return node-set or string.");
    }
      
    return result;
  }
  
  /**
   * Find a child schema element with the specified name.
   * @param parent The parent schema element.
   * @param name The name of the child.
   * @return Returns the child schema element.
   */
  private INode findChild( INode parent, String name)
  {
    childFinder.setVariable( "name", name);
    return childFinder.queryFirst( parent);
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.xpath.expression.Expression#notifyAdd(org.xmodel.xpath.expression.IExpression, 
   * org.xmodel.xpath.expression.IContext, java.util.List)
   */
  @Override
  public void notifyAdd( IExpression expression, IContext context, List<INode> nodes)
  {
    getParent().notifyChange( this, context);
  }

  /* (non-Javadoc)
   * @see org.xmodel.xpath.expression.Expression#notifyRemove(org.xmodel.xpath.expression.IExpression, 
   * org.xmodel.xpath.expression.IContext, java.util.List)
   */
  @Override
  public void notifyRemove( IExpression expression, IContext context, List<INode> nodes)
  {
    getParent().notifyChange( this, context);
  }

  /* (non-Javadoc)
   * @see org.xmodel.xpath.expression.Expression#notifyChange(org.xmodel.xpath.expression.IExpression, 
   * org.xmodel.xpath.expression.IContext, java.lang.String, java.lang.String)
   */
  @Override
  public void notifyChange( IExpression expression, IContext context, String newValue, String oldValue)
  {
    getParent().notifyChange( this, context);
  }
  
  private IExpression globalFinder = XPath.createExpression(
    "element[ @type = $name or @name = $name]");
  
  private IExpression childFinder = XPath.createExpression(
    "children/element[ @name = $name]");
}
