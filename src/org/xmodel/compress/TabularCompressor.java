/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * TabularCompressor.java
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
package org.xmodel.compress;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.xmodel.IModelObject;
import org.xmodel.ModelObjectFactory;
import org.xmodel.Xlate;


/**
 * An implementation of ICompressor which creates a table of element tags so that the text of the
 * element tag can be replaced by the table index. If the size of the content of the message exceeds
 * a certain limit, then the entire message (except for a single header byte) is compressed using
 * the ZIP or BZIP2 compression algorithms (BZip2 is provided by the org.apache.tools.bzip2
 * library). This implementation provides only slightly better compression than the BZip2 library
 * compressing XML text. However, it appears to be significantly faster than serializing to XML and
 * then performing post compression because redundant tags are serialized to the output stream as
 * small numbers before compression.
 * <p>
 * For fastest operation, turn post-compression off. There are still significant savings in space
 * with post-compression turned off especially if predefined tables are used. For best compression,
 * use BZIP2 post-compression. ZIP compression is only slightly slower than no compression and
 * substantially reduces the size.
 * <p>
 * The tag table used by the compressor can be predefined. In this mode of operation the table is
 * not included in the output of the compressor and the same instance of the compressor will be able
 * to decompress the stream. Another instance of the compressor with the same predefined table will
 * also be able to decompress the stream. The table may be predefined manually or using a XSD or
 * simplified schema. In the latter case, the schema is traversed and indices are assigned to each
 * element in the schema. Therefore, two compressor instances which reference the same schema can
 * compress and decompress the stream without the need for the stream to contain the tag table.
 */
public class TabularCompressor extends AbstractCompressor
{
  public static enum PostCompression { none, zip, bzip2};
  
  /**
   * Create a TabularCompressor and use ZIP post compression. This provides a good tradeoff
   * between speed and size. For faster operation, turn post compression off. For somewhat
   * better compression, use BZIP2.
   */
  public TabularCompressor()
  {
    this( PostCompression.zip);
  }
  
  /**
   * Create a TabularCompressor and specify the type of post compression to use.
   * @param post The type of post compression.
   */
  public TabularCompressor( PostCompression post)
  {
    this.post = post;
    this.factory = new ModelObjectFactory();
    this.map = new LinkedHashMap<String, Integer>();
    this.table = new ArrayList<String>();
    this.predefined = false;
  }

  /**
   * Predefine the tag table. If the compressor does not encounter tags which are not defined
   * in the table then the table will not be written to the output. This is useful when all
   * the tags are known in advance.
   * @param table The table.
   */
  public void defineTagTable( List<String> table)
  {
    this.table = table;
    map.clear();
    for( int i=0; i<table.size(); i++) map.put( table.get( i), i);
    predefined = true;
  }
  
  /**
   * Clear the predefined tag table.
   */
  public void clearTagTable()
  {
    map.clear();
    table.clear();
    predefined = false;
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.compress.ICompressor#compress(org.xmodel.IModelObject, java.io.OutputStream)
   */
  public void compress( IModelObject element, OutputStream finalArrayOut) throws CompressorException
  {
    ByteArrayOutputStream contentArrayOut = new ByteArrayOutputStream( 1024);
    DataOutputStream contentOut = new DataOutputStream( contentArrayOut);
    
    try
    {
      // create content and decide if compression is required
      writeElement( contentOut, element);
      boolean compress = contentArrayOut.size() > compressionThreshold;
      
      // write header
      byte header = 0;
      if ( compress && post == PostCompression.zip) header |= 0x80;
      if ( compress && post == PostCompression.bzip2) header |= 0x40;
      if ( predefined) header |= 0x20;
      finalArrayOut.write( header);
      
      // optionally compress everything but header
      OutputStream rawOut = finalArrayOut;
      if ( compress && post == PostCompression.zip) rawOut = new DeflaterOutputStream( finalArrayOut);
      
      // write table
      if ( !predefined)
      {
        DataOutputStream tableOut = new DataOutputStream( rawOut);
        writeTable( tableOut);
        tableOut.flush();
      }
      
      // write content
      contentOut.flush();
      byte[] content = contentArrayOut.toByteArray();
      rawOut.write( content);
      rawOut.flush();
      rawOut.close();
    }
    catch( IOException e)
    {
      throw new CompressorException( e);
    }
    
    predefined = true;
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.compress.ICompressor#decompress(java.io.InputStream)
   */
  public IModelObject decompress( InputStream rawArrayIn) throws CompressorException
  {
    try
    {
      // read header
      byte header = (byte)rawArrayIn.read();
      boolean predefined = (header & 0x20) != 0;
      
      PostCompression post = PostCompression.none;
      if ( (header & 0x80) != 0) post = PostCompression.zip;
      if ( (header & 0x40) != 0) post = PostCompression.bzip2;

      // optionally decompress everything but header
      InputStream rawIn = rawArrayIn;
      if ( post == PostCompression.zip) rawIn = new InflaterInputStream( rawArrayIn);
      DataInputStream dataIn = new DataInputStream( rawIn);
      
      // read table
      if ( !predefined) readTable( dataIn);
      
      // read content
      IModelObject element = readElement( dataIn);
      dataIn.close();
      
      return element;
    }
    catch( IOException e)
    {
      throw new CompressorException( "Error in data stream: ", e);
    }
  }

  /**
   * Read an element from the input stream.
   * @param stream The input stream.
   * @return Returns the new element.
   */
  private IModelObject readElement( DataInputStream stream) throws IOException, CompressorException
  {
    // read tag name
    String type = readHash( stream);
    
    // create element
    IModelObject element = factory.createObject( null, type);
    readAttributes( stream, element);
    readChildren( stream, element);
    
    return element;
  }
  
  /**
   * Write an element to the output stream.
   * @param stream The output stream.
   * @param element The element.
   */
  private void writeElement( DataOutputStream stream, IModelObject element) throws IOException, CompressorException
  {
    // write tag name
    writeHash( stream, element.getType());
    
    // write attributes and children
    writeAttributes( stream, element);
    writeChildren( stream, element);
  }
  
  /**
   * Read the attributes in the specified buffer and populate the node.
   * @param stream The input stream.
   * @param node The node whose attributes are being read.
   */
  private void readAttributes( DataInputStream stream, IModelObject node) throws IOException, CompressorException
  {
    // read count
    int count = readValue( stream);
    
    // read attributes
    for( int i=0; i<count; i++)
    {
      String attrName = readHash( stream);
      String attrValue = readText( stream);
      node.setAttribute( attrName, attrValue);
    }
  }
  
  /**
   * Read the children in the specified buffer and populate the node.
   * @param stream The input stream.
   * @param node The node whose children are being read.
   */
  private void readChildren( DataInputStream stream, IModelObject node) throws IOException, CompressorException
  {
    // read count
    int count = readValue( stream);
    
    // read children
    for( int i=0; i<count; i++)
    {
      IModelObject child = readElement( stream);
      node.addChild( child);
    }
  }

  /**
   * Write the attributes of the specified node into the buffer at the given offset.
   * @param stream The output stream.
   * @param node The node whose attributes are to be written.
   */
  private void writeAttributes( DataOutputStream stream, IModelObject node) throws IOException, CompressorException
  {
    // write count
    Collection<String> attrNames = node.getAttributeNames();
    writeValue( stream, attrNames.size());
    
    // write attributes
    for( String attrName: attrNames)
    {
      writeHash( stream, attrName);
      writeText( stream, Xlate.get( node, attrName, ""));
    }
  }
  
  /**
   * Write the children of the specified node into the buffer at the given offset.
   * @param stream The output stream.
   * @param node The node whose children are to be written.
   */
  private void writeChildren( DataOutputStream stream, IModelObject node) throws IOException, CompressorException
  {
    // write count
    List<IModelObject> children = node.getChildren();
    writeValue( stream, children.size());
    
    // write children
    for( IModelObject child: children)
      writeElement( stream, child);
  }
  
  /**
   * Read a hashed name from the stream.
   * @param stream The input stream.
   * @return Returns the name.
   */
  private String readHash( DataInputStream stream) throws IOException, CompressorException
  {
    int index = readValue( stream);
    if ( index >= table.size()) throw new CompressorException( "Table entry not found.");
    return table.get( index);
  }
  
  /**
   * Write a hashed name to the stream.
   * @param stream The output stream.
   * @param name The name.
   */
  private void writeHash( DataOutputStream stream, String name) throws IOException, CompressorException
  {
    Integer hash = map.get( name);
    if ( hash == null)
    {
      hash = hashIndex++;
      table.add( name);
      map.put( name, hash);
      predefined = false;
    }
    writeValue( stream, hash);
  }
  
  /**
   * Read text from the specified stream.
   * @param stream The input stream.
   * @return Returns the text.
   */
  private String readText( DataInputStream stream) throws IOException
  {
    int length = readValue( stream);
    byte[] bytes = new byte[ length];
    stream.readFully( bytes);
    return new String( bytes);
  }
  
  /**
   * Write text to the specified stream.
   * @param stream The output stream.
   * @param text The text.
   */
  private void writeText( DataOutputStream stream, String text) throws CompressorException, IOException
  {
    writeValue( stream, text.length());
    stream.writeBytes( text);
  }

  /**
   * Read the hash table from the stream.
   * @param stream The input stream.
   */
  private void readTable( DataInputStream stream) throws IOException, CompressorException
  {
    table = new ArrayList<String>();
    
    // read table size
    int count = readValue( stream);
    
    // read entries
    StringBuilder builder = new StringBuilder();
    for( int i=0; i<count; i++)
    {
      builder.setLength( 0);
      for( byte b = stream.readByte(); b != 0; b = stream.readByte())
      {
        builder.append( (char)b);
      }
      
      table.add( builder.toString());
    }
  }
  
  /**
   * Write the hash table to the stream.
   * @param stream The output stream.
   */
  private void writeTable( DataOutputStream stream) throws IOException, CompressorException
  {
    //System.out.println( "TabularCompressor: writing table...");
    
    // write table size
    Set<String> keys = map.keySet();
    writeValue( stream, keys.size());

    // write entries
    for( String key: keys) 
    {
      stream.writeBytes( key);
      stream.write( 0);
    }
  }
  
  /**
   * Read a value stored at the hash size.
   * @param stream The input stream.
   * @return Returns the value.
   */
  private int readValue( DataInputStream stream) throws IOException
  {
    int b1 = stream.readUnsignedByte();
    if ( (b1 & 0x00000080) != 0)
    {
      b1 &= 0x0000007f;
      int b2 = stream.readUnsignedByte();
      int b3 = stream.readUnsignedByte();
      int b4 = stream.readUnsignedByte();
      return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
    }
    else
    {
      return b1;
    }
  }
  
  /**
   * Write a value at the hash size.
   * @param stream The output stream.
   * @param value The value.
   */
  private void writeValue( DataOutputStream stream, int value) throws IOException, CompressorException
  {
    if ( value > 127)
    {
      value |= 0x80000000;
      stream.writeInt( value);
    }
    else
    {
      stream.writeByte( value);
    }
  }
    
  private final static int compressionThreshold = 256;
  
  private List<String> table;
  private Map<String, Integer> map;
  private int hashIndex;
  private boolean predefined;
  private PostCompression post;
}
