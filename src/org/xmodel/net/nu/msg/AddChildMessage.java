package org.xmodel.net.nu.msg;

public class AddChildMessage extends Message
{
  public final static byte type = 5;
  
  public AddChildMessage( String path, byte[] child, int index)
  {
    super( type);
    
    setLength( (4 + path.length()) + (4 + child.length) + 4);
    
    this.path = path;
    this.child = child;
    this.index = index;
  }
  
  /**
   * @return Returns the index-path relative to the root.
   */
  public String getPath()
  {
    return path;
  }

  /**
   * @return Returns the child to be added.
   */
  public byte[] getChild()
  {
    return child;
  }
  
  /**
   * @return Returns the insertion index.
   */
  public int getIndex()
  {
    return index;
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.net.nu.msg.Message#read()
   */
  @Override
  protected void read()
  {
    path = readString();
    child = readBytes();
    index = readInt();
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.net.nu.msg.Message#write()
   */
  @Override
  protected void write()
  {
    writeString( path);
    writeBytes( child, 0, child.length);
    writeInt( index);
  }

  private String path;
  private byte[] child;
  private int index;
}