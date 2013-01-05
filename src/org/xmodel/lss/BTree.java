package org.xmodel.lss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A B+Tree implementation that supports arbitrary keys and uses an instance of IRandomAccessStore to load and store nodes.
 */
public class BTree<K>
{
  /**
   * Create a b+tree with the specified degree, which places bounds on the number of entries in each node.  The minimum number
   * of entries in a node is degree - 1, and the maximum number of nodes is 2 * degree - 1.  The implementation uses the specified
   * instance of IRandomAccessStore to store and retrieve nodes.
   * @param degree The degree of the b+tree.
   * @param storageController The storage controller for the database.
   */
  public BTree( int degree, StorageController<K> storageController) throws IOException
  {
    this( degree, storageController, null);
  }
  
  /**
   * Create a b+tree with the specified degree, which places bounds on the number of entries in each node.  The minimum number
   * of entries in a node is degree - 1, and the maximum number of nodes is 2 * degree - 1.  The implementation uses the specified
   * instance of IRandomAccessStore to store and retrieve nodes.
   * @param degree The degree of the b+tree.
   * @param recordFormat The record format.
   * @param storageController The store controller for the database.
   * @param comparator The key comparator.
   */
  public BTree( int degree, StorageController<K> storageController, Comparator<K> comparator) throws IOException
  {
    this.degree = degree;
    this.garbage = new ArrayList<BNode<K>>();
    this.storageController = storageController;

    int minKeys = degree - 1;
    int maxKeys = 2 * degree - 1;

    int storeDegree = storageController.readIndexDegree();
    if ( storeDegree == 0)
    {
      initActiveStore();
      root = new BNode<K>( this, minKeys, maxKeys, 0, 0, comparator);
    }
    else if ( storeDegree == degree)
    {
      long position = storageController.readIndexPointer();
      root = new BNode<K>( this, minKeys, maxKeys, position, 0, comparator);
      if ( position > 0) root.load();
    }
    else
    {
      throw new IllegalStateException();
    }
  }
  
  /**
   * Initialize the active store in the specified StorageController for use with this BTree.
   * @param storageController The storage controller.
   */
  protected void initActiveStore() throws IOException
  {
    storageController.writeIndexDegree( degree);
  }
  
  /**
   * Insert a record.
   * @param key The key.
   * @param pointer The pointer.
   * @return Returns 0 or the previous record associated with the key.
   */
  public long insert( K key, long pointer) throws IOException
  {
    return root.insert( key, pointer);
  }
  
  /**
   * Delete a record.
   * @param key The key.
   * @return Returns 0 or the pointer associated with the key.
   */
  public long delete( K key) throws IOException
  {
    long pointer = root.delete( key);
    if ( root.count() == 0 && root.getChildren().size() > 0) root = root.getChildren().get( 0);
    return pointer;
  }
  
  /**
   * Returns the pointer associated with the specified key.
   * @param key The key.
   * @return Returns 0 or the pointer associated with the specified key.
   */
  public long get( K key) throws IOException
  {
    return root.get( key);
  }
  
  /**
   * Update the index in the store.
   */
  public void store() throws IOException
  {
    // begin with clean slate
    storageController.flush();
    
    // update index
    root.store();
    
    // update index pointer
    storageController.writeIndexPointer( root.pointer);

    //
    // Mark index garbage. 
    // Failure just before this point could result in leaked garbage.
    //
    while( garbage.size() > 0)
    {
      BNode<K> node = garbage.remove( 0);
      if ( node.pointer > 0) storageController.markGarbage( node.pointer);
    }
    
    // flush changes
    storageController.flush();
  }
  
  /**
   * Add the specified node to the garbage.
   * @param node The node.
   */
  protected void markGarbage( BNode<K> node)
  {
    garbage.add( node);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return root.toString();
  }

  private int degree;
  protected StorageController<K> storageController;
  protected BNode<K> root;
  private List<BNode<K>> garbage;
  
  public static void main( String[] args) throws Exception
  {
    BTree<String> tree = new BTree<String>( 3, null, new Comparator<String>() {
      public int compare( String lhs, String rhs)
      {
        return lhs.compareTo( rhs);
      }
    });

    String s = "AFSHZ3UCY6RVLXDN7Q4MEP5BL2G89BW1I";

    for( int i=0; i<s.length(); i++)
    {
      String key = ""+s.charAt( i);
      System.out.printf( "Insert %s\n", key);
      tree.insert( key, 0);
      System.out.println( tree.root);
      System.out.println( "------------------------------------------------------------");
    }
    
    Cursor<String> cursor = tree.root.getCursor( "5");
    while( cursor.hasPrevious())
    {
      System.out.printf( "Traverse: %s\n", cursor.get().getKey());
      cursor = cursor.previous();
    }
    
//    for( int i=0; i<s.length(); i++)
//    {
//      String key = ""+s.charAt( i);
//      System.out.printf( "Delete %s\n", key);
//      tree.delete( key);
//      System.out.println( tree.root);
//      System.out.println( "------------------------------------------------------------");
//    }
  }
}
