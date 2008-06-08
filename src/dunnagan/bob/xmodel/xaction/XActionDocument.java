/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.xaction;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dunnagan.bob.xmodel.IModelObject;
import dunnagan.bob.xmodel.IPath;
import dunnagan.bob.xmodel.ModelAlgorithms;
import dunnagan.bob.xmodel.Xlate;
import dunnagan.bob.xmodel.xml.XmlIO;
import dunnagan.bob.xmodel.xpath.XPath;
import dunnagan.bob.xmodel.xpath.expression.IExpression;

public class XActionDocument
{
  protected XActionDocument()
  {
    this( null, XActionDocument.class.getClassLoader());
  }
  
  /**
   * Construct with a ClassLoader.
   * @param loader The loader.
   */
  public XActionDocument( ClassLoader loader)
  {
    this( null, loader);
  }
  
  /**
   * Construct with root.
   * @param root The root of the document.
   */
  public XActionDocument( IModelObject root)
  {
    this( root, XActionDocument.class.getClassLoader());
  }
  
  /**
   * Construct with root and ClassLoader.
   * @param root The root of the document.
   * @param loader The loader.
   */
  public XActionDocument( IModelObject root, ClassLoader loader)
  {
    packages = new ArrayList<String>();
    varList = new ArrayList<Variable>();
    this.loader = loader;
    if ( root != null) setRoot( root);
  }
  
  /**
   * Sets the root of the view model. Adapters configured with this model will be updated.
   * @param root The new root of the view model.
   */
  public void setRoot( IModelObject root)
  {
    this.root = root;

    // initialize packages
    packages.add( "dunnagan.bob.xmodel.xaction");
    
    // load variables
    loadVariables( root);
    
    // load defined packages
    for( IModelObject packageElement: packagePath.query( root, null))
    {
      String packageName = Xlate.get( packageElement, "");
      packages.add( packageName);
    }
  }

  /**
   * Returns the root of the view model.
   * @return Returns the root of the view model.
   */
  public IModelObject getRoot()
  {
    return root;
  }
  
  /**
   * Returns the document nesting depth.
   * @return Returns the document nesting depth.
   */
  public int getDepth()
  {
    return depth;
  }

  /**
   * Add a fully-qualified Java package to the list of packages searched when loading classes.
   * @param packageName The fully-qualified Java package name.
   */
  public void addPackage( String packageName)
  {  
    if ( !packages.contains( packageName))
      packages.add( 0, packageName);
  }

  /**
   * Remove a fully-qualified Java package from the list of packages searched when loading classes.
   * @param packageName The fully-qualified Java package name.
   */
  public void removePackage( String packageName)
  {
    packages.remove( packageName);
  }

  /**
   * Returns the list of packages.
   * @return Returns the list of packages.
   */
  public List<String> getPackages()
  {
    return packages;
  }

  /**
   * Set the ClassLoader for this document.
   * @param loader The ClassLoader instance.
   */
  public void setClassLoader( ClassLoader loader)
  {
    this.loader = loader;
  }
  
  /**
   * Returns the ClassLoader defined for this document.
   * @return Returns the ClassLoader defined for this document.
   */
  public ClassLoader getClassLoader()
  {
    return loader;
  }
  
  /**
   * Returns the text of the root of the model.
   * @return Returns the text of the root of the model.
   */
  public String getString()
  {
    IModelObject root = getRoot();
    if ( root == null) return "";
    return Xlate.get( root, "");
  }

  /**
   * Returns the text of the first leaf of the specified path.
   * @param path A path relative to the root of the view model.
   * @return Returns the text of the first leaf of the specified path.
   */
  public String getString( IPath path)
  {
    IModelObject root = getRoot();
    if ( root == null) return "";
    IModelObject object = path.queryFirst( root);
    return Xlate.get( object, "");
  }

  /**
   * Returns the text of the first child with the specified type.
   * @param childType The type of the child.
   * @return Returns the text of the first child with the specified type.
   */
  public String getString( String childType)
  {
    IModelObject root = getRoot();
    if ( root == null) return "";
    IModelObject object = root.getFirstChild( childType);
    return Xlate.get( object, "");
  }

  /**
   * Returns the text of the leaves of the specified path.
   * @param path A path relative to the root of the view model.
   * @return Returns the text of the leaves of the specified path.
   */
  public List<String> getStrings( IPath path)
  {
    IModelObject root = getRoot();
    if ( root == null) return Collections.emptyList();
    
    List<IModelObject> leaves = path.query( root, null);
    List<String> strings = new ArrayList<String>( leaves.size());
    for ( IModelObject leaf: leaves)
    {
      String string = Xlate.get( leaf, "");
      strings.add( string);
    }
    return strings;
  }

  /**
   * Returns the text of the children with the specified type.
   * @param childType The type of the children.
   * @return Returns the text of the children with the specified type.
   */
  public List<String> getStrings( String childType)
  {
    IModelObject root = getRoot();
    if ( root == null) return Collections.emptyList();
    
    List<IModelObject> children = root.getChildren( childType);
    List<String> strings = new ArrayList<String>( children.size());
    for ( IModelObject child: children)
    {
      String string = Xlate.get( child, "");
      strings.add( string);
    }
    return strings;
  }

  /**
   * Returns the expression defined by the root of the model.
   * @return Returns the expression defined by the root of the model.
   */
  public IExpression getExpression()
  {
    IModelObject root = getRoot();
    if ( root == null) return null;
    return getExpression( root);
  }

  /**
   * Returns the expression defined in an element or an attribute. If the flexible flag
   * is false then the expression is taken from the value of the child element with the
   * specified name. If the flexible flag is true, then the expression is taken from 
   * either the attribute with the specified name or the child element.
   * @param name The name of the child element and/or attribute.
   * @param flexible True if attribute definition is allowed.
   * @return Returns the expression.
   */
  public IExpression getExpression( String name, boolean flexible)
  {
    IModelObject root = getRoot();
    if ( root == null) return null;
    
    if ( flexible)
    {
      IModelObject node = root.getAttributeNode( name);
      IExpression expression = getExpression( node);
      if ( expression != null) return expression;
    }
    
    IModelObject object = root.getFirstChild( name);
    return getExpression( object);
  }

  /**
   * Returns the expression defined by the first node in the specified path relative to the root.
   * @param path The path which identifies the location of the expression.
   * @return Returns the expression identified by the specified path.
   */
  public IExpression getExpression( IPath path)
  {
    IModelObject root = getRoot();
    if ( root == null) return null;
    IModelObject object = path.queryFirst( root);
    return getExpression( object);
  }
  
  /**
   * Returns a list of expressions defined by the leaves of the specified path.  The IExpression
   * instances will be cached in attributes of the leaves of the path.
   * @param path A path relative to the root of the view model.
   * @return Returns a list of expressions defined by the leaves of the specified path.
   */
  public List<IExpression> getExpressions( IPath path)
  {
    IModelObject root = getRoot();
    if ( root == null) return Collections.emptyList();
    
    List<IModelObject> leaves = path.query( root, null);
    List<IExpression> expressions = new ArrayList<IExpression>( leaves.size());
    for ( IModelObject leaf: leaves)
    {
      IExpression expression = getExpression( leaf);
      if ( expression != null) expressions.add( expression);
    }
    return expressions;
  }

  /**
   * Returns a list of expressions defined by the children with the specified type.  The IExpression
   * instances will be cached in attributes of the children.
   * @param childType The type of the children.
   * @return Returns a list of expressions defined by the children with the specified type.
   */
  public List<IExpression> getExpressions( String childType)
  {
    IModelObject root = getRoot();
    if ( root == null) return Collections.emptyList();
    
    List<IModelObject> children = root.getChildren( childType);
    List<IExpression> expressions = new ArrayList<IExpression>( children.size());
    for ( IModelObject child: children)
    {
      IExpression expression = getExpression( child);
      if ( expression != null) expressions.add( expression);
    }
    return expressions;
  }

  /**
   * Returns the expression defined in the value of the specified object.  The object must be an 
   * object within the scope of this view-model meaning that it is not accessible from another 
   * instance of ViewModel.
   * @param object The object containing the expression.
   * @return Returns the expression defined in the value of the specified object.
   */
  public IExpression getExpression( IModelObject object)
  {
    if ( object == null) return null;

    // get expression text
    String string = Xlate.get( object, "").trim();
    if ( string.length() == 0) return null;
    
    // create expression and cache
    IExpression expression = (IExpression)object.getAttribute( cachedExpressionAttribute);
    if ( expression == null)
    {
      expression = XPath.createExpression( string);
      
      // don't set local variable (use global variable instead)
      if ( expression != null) setVariables( expression);
      
      try { object.setAttribute( cachedExpressionAttribute, expression);} catch( UnsupportedOperationException e) {}
    }
    
    return expression;
  }
    
  /**
   * Create a script from the first child of the specified type.
   * @param childType The child element type.
   * @return Returns the new script.
   */
  public ScriptAction createScript( String childType)
  {
    IModelObject child = root.getFirstChild( childType);
    if ( child == null) return new ScriptAction();
    return createScript( child);
  }

  /**
   * Create a script from the elements returned by the specified expression.
   * @param expression An expression evaluated relative to the root of the document.
   * @return Returns null or the new script.
   */
  public ScriptAction createScript( IExpression expression)
  {
    if ( root == null) return null;
    List<IModelObject> elements = expression.query( root, null);
    return createScript( elements);
  }
  
  /**
   * Create a script from the specified element.
   * @param element The root of the script.
   * @return Returns the new script.
   */
  public ScriptAction createScript( IModelObject element)
  {
    if ( element == null) return null;
    ScriptAction script = new ScriptAction();
    script.configure( getDocument( element));
    return script;
  }

  /**
   * Create a script from the specified elements representing individual XActions.
   * @param elements The elements representing the XActions of the script.
   * @return Returns the new script.
   */
  public ScriptAction createScript( List<IModelObject> elements)
  {
    return new ScriptAction( this, elements);
  }
  
  /**
   * Returns the action defined on the specified view-model object or null.  If the object argument
   * is null then null is returned.
   * @param object A view-model action object.
   * @return Returns the action defined on the specified view-model object or null.
   */
  public IXAction getAction( IModelObject object)
  {
    if ( object == null) return null;

    // function call type 1
    String functionName = Xlate.get( object, "function", (String)null);
    if ( functionName != null) return findFunction( object, functionName);
    
    // function call type 2
    functionName = object.getType();
    IXAction function = findFunction( object, functionName);
    if ( function != null) return function;

    // load class using element name
    String className = Xlate.get( object, "class", (String)null);
    if ( className == null || !className.endsWith( "Action"))
    {
      for( int i=packages.size()-1; i>=0; i--)
      {
        IXAction action = getAction( packages.get( i), null, object);
        if ( action != null) return action;
      }
    }

    // load fully-qualified class name
    else if ( className.indexOf( '.') > 0)
    {
      int index = className.lastIndexOf( '.');
      String packageName = className.substring( 0, index);
      className = className.substring( index+1);
      IXAction action = getAction( packageName, className, object);
      if ( action != null) return action;
    }
    
    // try loading class from defined packages
    else
    {
      for( int i=packages.size()-1; i>=0; i--)
      {
        IXAction action = getAction( packages.get( i), className, object);
        if ( action != null) return action;
      }
    }
    
    // summarize class loader and packaging to help diagnose why class was not found
    System.out.println( "Unable to resolve IXAction class: "+ModelAlgorithms.createIdentityPath( object));
    System.out.println( (new XmlIO()).write( 2, object));
    
    // print defined packages
    System.out.println( "\nPackages (processed from last to first): ");
    for( String name: packages) System.out.println( "    "+name);
    
    return null;
  }

  /**
   * Attempts to load the fully-qualified action class from the specified package.
   * @param packageName The fully-qualified package name.
   * @param className The unqualified class name.
   * @param object The action configuration.
   * @return Returns null or the action.
   */
  @SuppressWarnings("unchecked")
  protected IXAction getAction( String packageName, String className, IModelObject object)
  {
    // create fully-qualified class name
    if ( className != null && ( packageName.length() != 0)) className = packageName+"."+className;
      
    // use element type to construct built-in class name
    if ( className == null)
    {
      StringBuilder builder = new StringBuilder( object.getType());
      builder.setCharAt( 0, Character.toUpperCase( builder.charAt( 0)));
      builder.insert( 0, packageName+".");
      builder.append( "Action");
      className = builder.toString();
    }
    
    // load class
    try
    {
      // create new action document
      XActionDocument document = getDocument( object);
      
      // load class
      Class clss = loader.loadClass( className);
      IXAction action = (IXAction)clss.newInstance();
      action.setDocument( document);
      action.configure( document);

      return action;
    }
    catch (ClassNotFoundException cnfe)
    {
      return null;
    }
    catch( Exception e)
    {
      e.printStackTrace();
      return null;
    }
  }
  
  /**
   * Returns the action defined on the specified leaf or null.
   * @param path The leaf path.
   * @return Returns the action defined on the specified leaf or null.
   */
  public IXAction getAction( IPath path)
  {
    IModelObject root = getRoot();
    if ( root == null) return null;
    return getAction( path.queryFirst( root));
  }

  /**
   * Returns the action defined on the specified child or null.
   * @param object A view-model action object.
   * @return Returns the action defined on the specified child or null.
   */
  public IXAction getAction( String childType)
  {
    IModelObject root = getRoot();
    if ( root == null) return null;
    return getAction( root.getFirstChild( childType));
  }
  
  /**
   * Returns the actions defined by the specified elements.
   * @param objects The action elements.
   * @return Returns the actions defined by the specified elements.
   */
  public List<IXAction> getActions( List<IModelObject> objects)
  {
    List<IXAction> actions = new ArrayList<IXAction>( objects.size());
    for( IModelObject object: objects)
    {
      IXAction action = getAction( object);
      if ( action != null) actions.add( action);
    }
    return actions;
  }

  /**
   * Returns the actions addressed by the specified expression.
   * @param expression An expression relative to the root of the document.
   * @return Returns the actions addressed by the specified expression.
   */
  public List<IXAction> getActions( IExpression expression)
  {
    List<IModelObject> elements = expression.query( getRoot(), null);
    List<IXAction> result = new ArrayList<IXAction>( elements.size());
    for( IModelObject element: elements)
    {
      IXAction action = getAction( element);
      if ( action != null) result.add( action);
    }
    return result;
  }

  /**
   * Returns the action corresponding to the named function.
   * @param locus The locus where the search begins.
   * @param name The name of the function.
   * @return Returns the function action.
   */
  private IXAction findFunction( IModelObject locus, String name)
  {
    functionFinder.setVariable( "name", name);
    IModelObject declaration = functionFinder.queryFirst( locus);
    if ( declaration == null) return null;
    return new ScriptAction( getClassLoader(), declaration);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xaction.XActionDocument#error(java.lang.String, java.lang.Exception)
   */
  public void error( String message, Exception exception)
  {
    StringWriter writer = new StringWriter();
    writer.append( message);
    writer.append( '\n');
    exception.printStackTrace( new PrintWriter( writer));
    System.err.println( writer.toString());
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xaction.XActionDocument#error(java.lang.String)
   */
  public void error( String message)
  {
    System.err.println( message);
  }

  /**
   * Returns a clone of this document on the specified object.
   * @param root The root of the new document.
   * @return Returns the new document.
   */
  public XActionDocument getDocument( IModelObject root)
  {
    XActionDocument document = new XActionDocument( loader);
    document.depth = depth+1;
    
    // set root
    document.setRoot( root);
    
    // add packages
    for( String packageName: packages)
      document.addPackage( packageName);
    
    // return
    return document;
  }
  
  /**
   * Returns a clone of this document on the specified relative path.
   * @param path The relative path.
   * @return Returns the new document.
   */
  public XActionDocument getDocument( IPath path)
  {
    IModelObject object = path.queryFirst( getRoot());
    return (object != null)? getDocument( object): null;
  }

  /**
   * Returns a clone of this document on the specified child object.
   * @param childType The type of child.
   * @return Returns the new document.
   */
  public XActionDocument getDocument( String childType)
  {
    IModelObject object = getRoot().getFirstChild( childType);
    return (object != null)? getDocument( object): null;
  }

  /**
   * Read the variable definitions from the view-model at the specified root.
   * @param root The root of the view-model.
   */
  private void loadVariables( IModelObject root)
  {
    varList.clear();
    List<IModelObject> variableSet = variablePath.query( root, null);
    for( IModelObject variable: variableSet)
    {
      String varName = Xlate.get( variable, "name", (String)null);
      if ( varName != null)
      {
        IExpression varExpression = getExpression( variable);
        Variable var = new Variable();
        var.name = varName;
        var.expression = varExpression;
        varList.add( var);
      }
    }
  }

  /**
   * Set all of the defined variables on the specified expression. The variable expressions are
   * all evaluated relative to the root of the view-model.
   * @param expression The expression.
   */
  protected void setVariables( IExpression expression)
  {
    for( Variable var: varList)
      if ( var.value != null)
      {
        // everything should be a string coming from an xml file
        expression.setVariable( var.name, var.value.toString());
      }
      else
      {
        expression.setVariable( var.name, var.expression);
      }
  }
    
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return ModelAlgorithms.createIdentityPath( getRoot()).toString();
  }

  /**
   * A data-structure to hold a variable definition.
   */
  private class Variable
  {
    String name;
    Object value;
    IExpression expression;
  }

  private final IPath functionFinder = XPath.createPath( 
    "ancestor-or-self::*/functions/function[ @name = $name]");

  private static final String cachedExpressionAttribute = 
    "xm:compiled";
  
  private final IExpression packagePath = XPath.createExpression( 
    "for $a in reverse( ancestor-or-self::*)" +
    "return $a/package");
  
  private static final IPath variablePath = XPath.createPath( 
    "ancestor-or-self::*/variable");
  
  private int depth;
  private ClassLoader loader;
  private IModelObject root;
  private List<String> packages;
  private List<Variable> varList;
}
