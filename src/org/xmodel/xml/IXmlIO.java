/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * IXmlIO.java
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
package org.xmodel.xml;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import org.xmodel.IModelObject;
import org.xmodel.IModelObjectFactory;


/**
 * An interface for generating an XModel from an XML document and vice versa.
 */
public interface IXmlIO
{
  public enum Whitespace { keep, trim};  
  public enum Style { compact, printable, html};
  
  /**
   * Specify how whitespace should be handled.
   * @param whitespace Whitespace handling.
   */
  public void setWhitespace( Whitespace whitespace);
  
  /**
   * Set the factory to use to create objects when parsing XML.
   * @param factory The factory.
   */
  public void setFactory( IModelObjectFactory factory);
  
  /**
   * Set the maximum number of lines to be written (0 for unlimited).
   * @param count Zero or the maximum number of lines.
   */
  public void setMaxLines( int count);
  
  /**
   * Read the XML document in the specified string.
   * @param xml A string containing a well-formed XML document.
   * @return Returns the root of the model.
   */
  public IModelObject read( String xml) throws XmlException;
  
  /**
   * Read the XML document at the specified URL.
   * @param url The url pointing to an XML document.
   * @return Returns the root of the model.
   */
  public IModelObject read( URL url) throws XmlException;

  /**
   * Read an XML document from the specified stream.
   * @param stream The stream.
   * @return Returns the root of the model.
   */
  public IModelObject read( InputStream stream) throws XmlException;

  /**
   * Returns a list with one entry per line in the output document. Each entry contains the element whose
   * textual representation in the output document occurs on the line number corresponding to the entry
   * index.  Line numbers which do not have start-tags will be null.  Note that the actual line number
   * is the list index plus one.
   * @return Returns a list representing the element on each line of the output document.
   */
  public List<IModelObject> getLineInformation();
  
  /**
   * Write the subtree with the specified root to a string and return it.
   * @param root The root of the subtree.
   */
  public String write( IModelObject root);
  
  /**
   * Write the subtree with the specified root to the specified file.
   * @param root The root of the subtree.
   * @param file The file to be written.
   */
  public void write( IModelObject root, File file) throws XmlException;
  
  /**
   * Write the subtree with the specified root to the specified PrintStream.
   * @param root The root of the subtree.
   * @param stream The output stream.
   */
  public void write( IModelObject root, OutputStream stream) throws XmlException;
  
  /**
   * Write the subtree with the specified root to a string and return it.
   * @param depth The number of spaces to indent.
   * @param root The root of the subtree.
   */
  public String write( int depth, IModelObject root);
  
  /**
   * Write the subtree with the specified root to the specified file.
   * @param depth The number of spaces to indent.
   * @param root The root of the subtree.
   * @param file The file to be written.
   */
  public void write( int depth, IModelObject root, File file) throws XmlException;
  
  /**
   * Write the subtree with the specified root to the specified PrintStream.
   * @param depth The number of spaces to indent.
   * @param root The root of the subtree.
   * @param stream The output stream.
   */
  public void write( int depth, IModelObject root, OutputStream stream) throws XmlException;
  
  /**
   * Tell the implementation not to generate objects or attributes with the specified prefix on read.
   * @param prefix The prefix to be skipped.
   */
  public void skipInputPrefix( String prefix);
  
  /**
   * Tell the implementation not to generate objects or attributes with the specified prefix on write.
   * @param prefix The prefix to be skipped.
   */
  public void skipOutputPrefix( String prefix);
  
  /**
   * Set the output style.
   * @param style The output style.
   */
  public void setOutputStyle( Style style);
}
