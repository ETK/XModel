package org.xmodel.lss;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.xmodel.lss.BNode.Entry;
import org.xmodel.lss.StorageAllocator.LowestUtility;
import org.xmodel.lss.store.IRandomAccessStore;
import org.xmodel.lss.store.IRandomAccessStoreFactory;

/**
 * The StorageController class encapsulates one or more instances of IRandomAccessStore and provides
 * a high-level interface for the BTree and Database classes to store and retrieve information.  This
 * class defines the fundamental schema of the data, and allows data to be written across multiple
 * instances of IRandomAccessStore.
 */
public class StorageController<K>
{
  public final static int garbageFlag = 0x01;
  public final static int nodeFlag = 0x02;
  public final static int leafFlag = 0x04;
  public final static int rootFlag = 0x08;
  
  public final static int indexDegreeOffset = 0;
  public final static int indexDegreeLength = 4;
  public final static int indexPointerOffset = 4;
  public final static int indexPointerLength = 8;
  public final static int headerLength = indexDegreeLength + indexPointerLength;
  
  public final static int recordHeaderLength = 9;

  public StorageController( IRandomAccessStoreFactory factory, IKeyFormat<K> keyFormat, long newStoreThreshold) throws IOException
  {
    this( factory, Collections.<IRandomAccessStore>emptyList(), keyFormat, newStoreThreshold);
  }
  
  public StorageController( IRandomAccessStoreFactory factory, List<IRandomAccessStore> stores, IKeyFormat<K> keyFormat, long newStoreThreshold) throws IOException
  {
    this.keyFormat = keyFormat;
    this.allocator = new StorageAllocator( stores, factory);
    this.newStoreThreshold = newStoreThreshold;
  }
  
  /**
   * Read any records between the last stored index and the end of the store.
   * @param btree The BTree to be updated with the additional records.
   */
  public void finishIndex( BTree<K> btree) throws IOException
  {
    List<IRandomAccessStore> stores = allocator.getStores();
    IRandomAccessStore store = allocator.getStore( btree.root.getPointer());
    int index = stores.indexOf( store);
    
    for( int i=index; i<stores.size(); i++)
    {
      store = stores.get( i);
      long pointer = store.position();
      
      if ( i > index || pointer < StorageAllocator.headerLength)
        pointer = StorageAllocator.headerLength;
      
      while( pointer < store.length())
      {
        store.seek( pointer);
        
        byte flags = store.readByte();
        long length = store.readLong();
        long start = store.position();
        
        if ( length != 3)
          System.out.println();
        
        if ( (flags & garbageFlag) == 0)
        {
          K key = keyFormat.extractKeyFromRecord( store, length);
          if ( key != null) btree.insert( key, allocator.getStorePointer( store, pointer));
        }
        
        pointer = start + length;
      }
    }
  }
  
  /**
   * Returns true if the specified pointer is in the active store.
   * @param pointer The pointer.
   * @return Returns true if the specified pointer is in the active store.
   */
  public boolean isActiveStorePointer( long pointer)
  {
    return allocator.isActiveStorePointer( pointer);
  }
  
  /**
   * @return Returns the degree of the index.
   */
  public int readIndexDegree() throws IOException
  {
    IRandomAccessStore store = allocator.getActiveStore();
    store.seek( indexDegreeOffset);
    return store.readInt();
  }
  
  /**
   * Write the degree of the index.
   * @param degree The degree.
   */
  public void writeIndexDegree( int degree) throws IOException
  {
    IRandomAccessStore store = allocator.getActiveStore();
    store.seek( indexDegreeOffset);
    store.writeInt( degree);
  }
  
  /**
   * @return Returns the pointer to the most recently stored index.
   */
  public long readIndexPointer() throws IOException
  {
    IRandomAccessStore store = allocator.getActiveStore();
    store.seek( indexPointerOffset);
    return store.readLong();
  }
  
  /**
   * Writes the pointer to the most recently stored index.
   * @param pointer The absolute pointer to the root node.
   */
  public void writeIndexPointer( long pointer) throws IOException
  {
    IRandomAccessStore store = allocator.getActiveStore();
    store.seek( indexPointerOffset);
    store.writeLong( pointer);
  }
  
  /**
   * Read the record header at the specified location.
   * @param pointer The location of the record.
   * @param record Returns a record with the header fields set.
   */
  public void readHeader( long pointer, Record record) throws IOException
  {
    IRandomAccessStore store = allocator.getStoreAndSeek( pointer);
    readHeader( store, record);
  }

  /**
   * Read the record header at the current position in the specified store.
   * @param store The store.
   * @param record Returns a record with the header fields set.
   */
  public void readHeader( IRandomAccessStore store, Record record) throws IOException
  {
    record.setFlags( store.readByte());
    record.setLength( store.readLong());
  }

  /**
   * Write the record header.
   * @param pointer The location of the record.
   * @param record A record with the header fields set
   */
  public void writeHeader( long pointer, Record record) throws IOException
  {
    IRandomAccessStore store = allocator.getStoreAndSeek( pointer);
    store.writeByte( record.getFlags());
    store.writeLong( record.getLength());
  }
  
  /**
   * Read a record at the specified location.
   * @param pointer The location of the record.
   * @param record Returns the record.
   */
  public void readRecord( long pointer, Record record) throws IOException
  {
    IRandomAccessStore store = allocator.getStoreAndSeek( pointer);
    readRecord( store, record);
  }

  /**
   * Read a record from the current position in the specified store.
   * @param store The store.
   * @param record Returns the record.
   */
  public void readRecord( IRandomAccessStore store, Record record) throws IOException
  {
    byte flags = store.readByte();
    long length = store.readLong();
    
    record.setFlags( flags);
    
    if ( (flags & (nodeFlag | leafFlag)) == 0)
    {
      byte[] data = new byte[ (int)length];
      store.read( data, 0, data.length);
      record.setContent( data);
    }
    else
    {
      store.seek( store.position() + length);
    }
  }

  /**
   * Write a record at the current position.
   * @param content The content of the record.
   * @return Returns the position of the record.
   */
  public long writeRecord( byte[] content) throws IOException
  {
    IRandomAccessStore store = allocator.getActiveStore();

    // new records are always written to the end of the active store
    long pointer = store.length();
    store.seek( pointer);
    
    store.writeByte( (byte)0);
    store.writeLong( content.length);
    store.write( content, 0, content.length);
    
    pointer = allocator.getActiveStorePointer( pointer);
    
    if ( store.position() >= newStoreThreshold)
      allocator.addStore();

    return pointer;
  }

  /**
   * Read an index node at the current position.
   * @param node Returns the node.
   */
  public void readNode( BNode<K> node) throws IOException
  {
    IRandomAccessStore store = allocator.getStoreAndSeek( node.getPointer());

    node.clearEntries();
    
    byte flags = store.readByte();
    if ( (flags & nodeFlag) == 0) 
      throw new IllegalStateException( "Record is not an index node.");
    
    store.readLong();
    int count = store.readInt();
    boolean leaf = (flags & leafFlag) != 0;
    
    for( int i=0; i<count; i++)
    {
      K key = keyFormat.readKey( store);
      long pointer = store.readLong();
      Entry<K> entry = new Entry<K>( key, pointer);
      node.addEntry( entry);
    }
    
    if ( !leaf)
    {
      for( int i=0; i<=count; i++)
      {
        long childPointer = store.readLong();
        int childCount = store.readInt();
        BNode<K> child = new BNode<K>( node, childPointer, childCount);
        node.addChild( child);
      }
    }
  }

  /**
   * Write an index node from the current position.
   * @param node The node.
   * @return Returns the position of the node.
   */
  public long writeNode( BNode<K> node) throws IOException
  {
    IRandomAccessStore store = allocator.getActiveStore();
    
    List<Entry<K>> entries = node.getEntries();
    List<BNode<K>> children = node.getChildren();
    
    // new records are always written to the end of the active store
    long pointer = store.length();
    store.seek( pointer);
    node.setPointer( allocator.getActiveStorePointer( pointer));

    byte flags = (byte)((children.size() > 0)? nodeFlag: (nodeFlag | leafFlag));
    if ( node.parent() == null) flags |= rootFlag;
    
    store.writeByte( flags);
    store.writeLong( 0);
    long lengthPos = store.position();
    
    store.writeInt( node.count());
    for( int i=0; i<node.count(); i++)
    {
      Entry<K> entry = entries.get( i);
      keyFormat.writeKey( store, entry.getKey());
      store.writeLong( entry.getPointer());
    }

    if ( children.size() > 0)
    {
      for( int i=0; i<=node.count(); i++)
      {
        BNode<K> child = children.get( i);
        store.writeLong( child.getPointer());
        store.writeInt( child.count());
      }
    }
    
    long position = store.position();
    store.seek( lengthPos - 8);
    store.writeLong( position - lengthPos);
        
    return pointer;
  }
  
  /**
   * Flush changes made to stores controlled by this instance.
   */
  public void flush() throws IOException
  {
    allocator.flush();
  }

  /**
   * Mark the specified record as garbage.
   * @param pointer The pointer to the record.
   */
  public void markGarbage( long pointer) throws IOException
  {
    IRandomAccessStore store = allocator.getStoreAndSeek( pointer);
    long position = store.position();
    store.writeByte( (byte)garbageFlag);
    long length = store.readLong();
    store.garbage( position, 9 + length);
  }

  /**
   * Returns true if the specified record is garbage.
   * @param pointer The pointer to the record.
   * @return Returns true if the specified record is garbage.
   */
  public boolean isGarbage( long pointer) throws IOException
  {
    IRandomAccessStore store = allocator.getStoreAndSeek( pointer);
    return (store.readByte() & garbageFlag) != 0;
  }
  
  /**
   * Perform garbage collection and relocate records to the specified database.
   * @param database The database.
   * @return Returns true if there was garbage to collect.
   */
  public boolean garbageCollect( Database<K> database) throws IOException
  {
    LowestUtility result = allocator.getLowestUtilityStore();
    IRandomAccessStore store = result.store;
    if ( store == null || store == allocator.getActiveStore()) return false;
    
    if ( (StorageAllocator.headerLength + result.garbage) < store.length())
    {
      long position = StorageAllocator.headerLength;
      
      Record record = new Record();
      while( position < store.length())
      {
        store.seek( position);
  
        readHeader( store, record);
        if ( !record.isGarbage())
        {
          store.seek( position);
          if ( record.isIndex())
          {
            if ( record.isIndexRoot())
              database.storeIndex();
          }
          else
          {
            readRecord( store, record);
            K key = keyFormat.extractKeyFromRecord( record.getContent());
            System.out.printf( "Found non-garbage: %s\n", key);
            database.insert( key, record.getContent());
          }
        }
        
        position += StorageController.recordHeaderLength + record.getLength();
      }
    }
    
    allocator.removeStore( store);
    
    return true;
  }
  
  /**
   * Extract the key from the specified record.
   * @param pointer The pointer to the record.
   * @return Returns the key.
   */
  protected K extractKey( long pointer) throws IOException
  {
    IRandomAccessStore store = allocator.getStoreAndSeek( pointer);
    
    byte flags = store.readByte();
    long length = store.readLong();
    if ( (flags & (nodeFlag | garbageFlag)) != 0) return null;

    // extract key
    K key = keyFormat.extractKeyFromRecord( store, length);
    
    return key;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return allocator.toString();
  }

  private IKeyFormat<K> keyFormat;
  private StorageAllocator allocator;
  private long newStoreThreshold;
}
