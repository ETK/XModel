/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.xpath;

import java.util.*;

import dunnagan.bob.xmodel.*;
import dunnagan.bob.xmodel.memento.IMemento;

/**
 * An implementation of IModelObject which serves as a light-weight container for attributes of
 * other IModelObjects. This container is used during the evaluation of X-Path expressions.
 */
public class AttributeNode implements IModelObject
{
  /**
   * Create an Attribute to hold the given attribute information. 
   * @param attrName The name of the attribute.
   * @param source The object where the attribute is stored.
   */
  public AttributeNode( String attrName, IModelObject source)
  {
    this.attrName = attrName;
    this.source = source;
  }
  
  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getModel()
   */
  public IModel getModel()
  {
    return source.getModel();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getID()
   */
  public String getID()
  {
    return "";
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#setID(java.lang.String)
   */
  public void setID( String id)
  {
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#addAncestorListener(dunnagan.bob.xmodel.IAncestorListener)
   */
  public void addAncestorListener( IAncestorListener listener)
  {
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#addChild(dunnagan.bob.xmodel.IModelObject)
   */
  public void addChild( IModelObject object)
  {
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#addChild(dunnagan.bob.xmodel.IModelObject, int)
   */
  public void addChild( IModelObject object, int index)
  {
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getChild(int)
   */
  public IModelObject getChild( int index)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#removeChild(int)
   */
  public IModelObject removeChild( int index)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#addModelListener(dunnagan.bob.xmodel.IModelListener)
   */
  public void addModelListener( IModelListener listener)
  {
    source.addModelListener( new AttributeListener( this, listener));
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#addPathListener(dunnagan.bob.xmodel.IPath, dunnagan.bob.xmodel.IPathListener)
   */
  public void addPathListener( IPath path, IPathListener listener)
  {
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#cloneObject()
   */
  public IModelObject cloneObject()
  {
    return new AttributeNode( attrName, source);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#cloneTree()
   */
  public IModelObject cloneTree()
  {
    return cloneObject();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getReferent()
   */
  public IModelObject getReferent()
  {
    return this;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getAncestor(java.lang.String)
   */
  public IModelObject getAncestor( String type)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getAttribute(java.lang.String)
   */
  public Object getAttribute( String attrName)
  {
    if ( attrName.length() == 0) return source.getAttribute( this.attrName);
    return null;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getAttributeNode(java.lang.String)
   */
  public IModelObject getAttributeNode( String attrName)
  {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getAttributeNames()
   */
  public Collection<String> getAttributeNames()
  {
    List<String> names = new ArrayList<String>( 1);
    names.add( "");
    return names;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getChild(java.lang.String, java.lang.String)
   */
  public IModelObject getChild( String type, String name)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getChildren()
   */
  public List<IModelObject> getChildren()
  {
    return Collections.emptyList();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getChildren(java.lang.String, java.lang.String)
   */
  public List<IModelObject> getChildren( String type, String name)
  {
    return Collections.emptyList();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getChildren(java.lang.String)
   */
  public List<IModelObject> getChildren( String type)
  {
    return Collections.emptyList();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getValue()
   */
  public Object getValue()
  {
    return source.getAttribute( attrName);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getChildValue(java.lang.String)
   */
  public Object getChildValue( String type)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getCreateChild(java.lang.String, java.lang.String)
   */
  public IModelObject getCreateChild( String type, String name)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getCreateChild(java.lang.String)
   */
  public IModelObject getCreateChild( String type)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getFirstChild(java.lang.String)
   */
  public IModelObject getFirstChild( String type)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getModelListeners()
   */
  public ModelListenerList getModelListeners()
  {
    ModelListenerList list = source.getModelListeners();
    if ( list == null) return null;
    
    ModelListenerList result = null;
    Set<IModelListener> listeners = list.getListeners();
    for( IModelListener listener: listeners)
    {
      if ( listener instanceof AttributeListener)
      {
        AttributeListener attributeListener = (AttributeListener)listener;
        if ( attributeListener.attrNode.equals( this))
        {
          if ( result == null) result = new ModelListenerList();
          result.addListener( attributeListener.listener);
        }
      }
    }

    return result;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getPathListeners()
   */
  public PathListenerList getPathListeners()
  {
    if ( pathListeners == null) pathListeners = new PathListenerList();
    return pathListeners;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getName()
   */
  public String getName()
  {
    throw new UnsupportedOperationException();
    //return attrName;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getNumberOfChildren()
   */
  public int getNumberOfChildren()
  {
    return 0;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getNumberOfChildren(java.lang.String)
   */
  public int getNumberOfChildren( String type)
  {
    return 0;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getParent()
   */
  public IModelObject getParent()
  {
    return source;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getRoot()
   */
  public IModelObject getRoot()
  {
    return source.getRoot();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getType()
   */
  public String getType()
  {
    return attrName;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#getTypesOfChildren()
   */
  public Set<String> getTypesOfChildren()
  {
    return Collections.emptySet();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#identityPath()
   */
  public IPath identityPath()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#internal_setParent(dunnagan.bob.xmodel.IModelObject)
   */
  public IModelObject internal_setParent( IModelObject parent)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#internal_notifyParent(dunnagan.bob.xmodel.IModelObject, dunnagan.bob.xmodel.IModelObject)
   */
  public void internal_notifyParent( IModelObject newParent, IModelObject oldParent)
  {
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#internal_notifyAddChild(dunnagan.bob.xmodel.IModelObject, int)
   */
  public void internal_notifyAddChild( IModelObject child, int index)
  {
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#internal_notifyRemoveChild(dunnagan.bob.xmodel.IModelObject, int)
   */
  public void internal_notifyRemoveChild( IModelObject child, int index)
  {
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#internal_addChild(dunnagan.bob.xmodel.IModelObject, int)
   */
  public void internal_addChild( IModelObject child, int index)
  {
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#internal_removeChild(int)
   */
  public IModelObject internal_removeChild( int index)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#isDirty()
   */
  public boolean isDirty()
  {
    return false;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#isType(java.lang.String)
   */
  public boolean isType( String type)
  {
    return type.equals( attrName);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#peerPath(dunnagan.bob.xmodel.IModelObject)
   */
  public IPath peerPath( IModelObject peer)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#removeAncestorListener(dunnagan.bob.xmodel.IAncestorListener)
   */
  public void removeAncestorListener( IAncestorListener listener)
  {
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#removeAttribute(java.lang.String)
   */
  public Object removeAttribute( String attrName)
  {
    return null;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#removeChild(dunnagan.bob.xmodel.IModelObject)
   */
  public void removeChild( IModelObject object)
  {
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#removeChildren()
   */
  public void removeChildren()
  {
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#removeChildren(java.lang.String)
   */
  public void removeChildren( String type)
  {
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#removeFromParent()
   */
  public void removeFromParent()
  {
    source.removeAttribute( attrName);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#removeModelListener(dunnagan.bob.xmodel.IModelListener)
   */
  public void removeModelListener( IModelListener listener)
  {
    source.removeModelListener( new AttributeListener( this, listener));
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#removePathListener(dunnagan.bob.xmodel.IPath, dunnagan.bob.xmodel.IPathListener)
   */
  public void removePathListener( IPath path, IPathListener listener)
  {
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#setAttribute(java.lang.String, java.lang.Object)
   */
  public Object setAttribute( String attrName, Object attrValue)
  {
    if ( attrName.length() == 0) return source.setAttribute( this.attrName, attrValue);
    return null;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#setAttribute(java.lang.String)
   */
  public Object setAttribute( String attrName)
  {
    if ( attrName.length() == 0) return setValue( "");
    return null;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#setName(java.lang.String)
   */
  public void setName( String name)
  {
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#setValue(java.lang.Object)
   */
  public Object setValue( Object value)
  {
    return source.setAttribute( this.attrName, value);
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#typePath()
   */
  public IPath typePath()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#revertUpdate(dunnagan.bob.xmodel.memento.IMemento)
   */
  public void revertUpdate( IMemento memento)
  {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.IModelObject#restoreUpdate(dunnagan.bob.xmodel.memento.IMemento)
   */
  public void restoreUpdate( IMemento memento)
  {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals( Object object)
  {
    if ( object instanceof AttributeNode)
    {
      AttributeNode node = (AttributeNode)object;
      IModelObject thisSource = source;
      IModelObject nodeSource = node.source;
      return attrName.equals( node.attrName) && thisSource.equals( nodeSource);
    }
    else if ( object instanceof AttributeHistoryNode)
    {
      AttributeHistoryNode node = (AttributeHistoryNode)object;
      return attrName.equals( node.attrName);
    }
    return super.equals( object);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  public int hashCode()
  {
    return attrName.hashCode() ^ source.hashCode();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    Object attrValue = source.getAttribute( attrName);
    return (attrValue == null)? "": attrValue.toString();
  }

  /**
   * An intermediate listener for attribute changes of the source IModelObject, this listener
   * filters the events to those which are meaningful to listeners of this AttributeNode.
   */
  private class AttributeListener extends ModelListener
  {
    public AttributeListener( AttributeNode attrNode, IModelListener listener)
    {
      this.attrNode = attrNode;
      this.listener = listener;
    }
    
    public void notifyChange( IModelObject object, String attrName, Object newValue, Object oldValue)
    {
      if ( attrName.equals( attrNode.attrName))
        listener.notifyChange( attrNode, "", newValue, oldValue);
    }
  
    public void notifyClear( IModelObject object, String attrName, Object oldValue)
    {
      if ( attrName.equals( attrNode.attrName))
        listener.notifyClear( attrNode, "", oldValue);
    }
  
    public int hashCode()
    {
      return attrNode.hashCode() + listener.hashCode();
    }

    public boolean equals( Object object)
    {
      if ( object instanceof AttributeListener)
      {
        AttributeListener other = (AttributeListener)object;
        return attrNode.equals( other.attrNode) && listener.equals( other.listener);
      }
      return super.equals( object);
    }

    AttributeNode attrNode;
    IModelListener listener;
  }
  
  String attrName;
  IModelObject source;
  PathListenerList pathListeners;
}
