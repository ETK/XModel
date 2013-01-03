package org.xmodel.lss;

import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.util.Collections;
import java.util.Random;
import org.apache.catalina.tribes.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xmodel.lss.store.CompositeStore;
import org.xmodel.lss.store.IRandomAccessStore;
import org.xmodel.lss.store.IRandomAccessStoreFactory;
import org.xmodel.lss.store.MemoryStore;

public class DatabaseInsertTest
{
  @Before
  public void setUp() throws Exception
  {
    keyFormat = new IKeyFormat<String>() {
      public String readKey( IRandomAccessStore store) throws IOException
      {
        int length = store.readByte();
        byte[] bytes = new byte[ length];
        store.read( bytes, 0, length);
        return new String( bytes);
      }
      public void writeKey( IRandomAccessStore store, String key) throws IOException
      {
        store.writeByte( (byte)key.length());
        byte[] bytes = key.getBytes();
        store.write( bytes, 0, bytes.length);
      }
      public String extractKeyFromRecord( IRandomAccessStore store, long recordLength) throws IOException
      {
        int length = store.readByte();
        byte[] key = new byte[ length];
        store.read( key, 0, length);
        return new String( key, 0, length);
      }
      public String extractKeyFromRecord( byte[] content) throws IOException
      {
        int length = content[ 0];
        return new String( content, 1, length);
      }
    };
    
    recordFormat = new DefaultRecordFormat<String>( keyFormat);
  }

  @After
  public void tearDown() throws Exception
  {
  }

  @Test
  public void insertWriteFullIndex() throws IOException
  {
    MemoryStore store = new MemoryStore( 1000);
    BTree<String> btree = new BTree<String>( 2, recordFormat, store);
    Database<String> db = new Database<String>( btree, store, recordFormat);

    byte[] record = new byte[ 3];
    for( int i=0; i<7; i++)
    {
      if ( i == 5) btree.store();
      record[ 0] = 1; record[ 1] = (byte)(i + 65); record[ 2] = '#';
      String key = String.format( "%c", i+65);
      db.insert( key, record);
    }
    
    btree.store();
    
    btree = new BTree<String>( 2, recordFormat, store);
    db = new Database<String>( btree, store, recordFormat);

    for( int i=0; i<7; i++)
    {
      record[ 0] = 1; record[ 1] = (byte)(i + 65); record[ 2] = '#';
      String key = String.format( "%c", i+65);
      byte[] content = db.query( key);
      assertTrue( "Invalid record", Arrays.equals( record, content));
    }
  }
  
  @Test
  public void insertWritePartialIndex() throws IOException
  {
    MemoryStore store = new MemoryStore( 1000);
    BTree<String> btree = new BTree<String>( 2, recordFormat, store);
    Database<String> db = new Database<String>( btree, store, recordFormat);

    byte[] record = new byte[ 3];
    for( int i=0; i<7; i++)
    {
      if ( i == 5) btree.store();
      record[ 0] = 1; record[ 1] = (byte)(i + 65); record[ 2] = '#';
      String key = String.format( "%c", i+65);
      db.insert( key, record);
    }
    
    btree = new BTree<String>( 2, recordFormat, store);
    db = new Database<String>( btree, store, recordFormat);
    
    for( int i=0; i<7; i++)
    {
      record[ 0] = 1; record[ 1] = (byte)(i + 65); record[ 2] = '#';
      String key = String.format( "%c", i+65);
      byte[] content = db.query( key);
      assertTrue( "Invalid record", Arrays.equals( record, content));
    }
  }
  
  @Test
  public void deleteWriteFullIndex() throws IOException
  {
    MemoryStore store = new MemoryStore( 1000);
    BTree<String> btree = new BTree<String>( 2, recordFormat, store);
    Database<String> db = new Database<String>( btree, store, recordFormat);

    byte[] record = new byte[ 3];
    for( int i=0; i<7; i++)
    {
      record[ 0] = 1; record[ 1] = (byte)(i + 65); record[ 2] = '#';
      String key = String.format( "%c", i+65);
      db.insert( key, record);
    }

    btree.store();
    
    db.delete( "A");
    assertTrue( "Record not deleted", db.query( "A") == null);
    
    btree.store();
    
    btree = new BTree<String>( 2, recordFormat, store);
    db = new Database<String>( btree, store, recordFormat);
    
    assertTrue( "Record not deleted", db.query( "A") == null);
    
    for( int i=1; i<7; i++)
    {
      record[ 0] = 1; record[ 1] = (byte)(i + 65); record[ 2] = '#';
      String key = String.format( "%c", i+65);
      byte[] content = db.query( key);
      assertTrue( "Invalid record", Arrays.equals( record, content));
    }
  }
  
  @Test
  public void deleteWritePartialIndex() throws IOException
  {
    MemoryStore store = new MemoryStore( 1000);
    BTree<String> btree = new BTree<String>( 2, recordFormat, store);
    Database<String> db = new Database<String>( btree, store, recordFormat);

    byte[] record = new byte[ 3];
    for( int i=0; i<7; i++)
    {
      record[ 0] = 1; record[ 1] = (byte)(i + 65); record[ 2] = '#';
      String key = String.format( "%c", i+65);
      db.insert( key, record);
    }

    btree.store();
    
    db.delete( "A");
    assertTrue( "Record not deleted", db.query( "A") == null);

    btree = new BTree<String>( 2, recordFormat, store);
    db = new Database<String>( btree, store, recordFormat);
    
    assertTrue( "Record not deleted", db.query( "A") == null);
    
    for( int i=1; i<7; i++)
    {
      record[ 0] = 1; record[ 1] = (byte)(i + 65); record[ 2] = '#';
      String key = String.format( "%c", i+65);
      byte[] content = db.query( key);
      assertTrue( "Invalid record", Arrays.equals( record, content));
    }
  }
  
  @Test
  public void randomInsertDelete() throws IOException
  {
    int degree = 1000;
    MemoryStore store = new MemoryStore( 10000000);
    BTree<String> btree = new BTree<String>( degree, recordFormat, store);
    Database<String> db = new Database<String>( btree, store, recordFormat);

    Random random = new Random( 1);
    byte[] record = new byte[ 3];
    for( int i=0; i<100000; i++)
    {
      int j = random.nextInt( 26);
      record[ 0] = 1; record[ 1] = (byte)(j + 65); record[ 2] = '#';
      String key = String.format( "%c", 65 + j);
      db.insert( key, record);
      
      j = random.nextInt( 26);
      key = String.format( "%c", 65 + j);
      db.delete( key);
      
      if ( (i % 10000) == 0)
      {
        long t0 = System.nanoTime();
        btree = new BTree<String>( degree, recordFormat, store);
        db = new Database<String>( btree, store, recordFormat);
        
        long t1 = System.nanoTime();
        btree.store();
        
        long t2 = System.nanoTime();
        System.out.printf( "index=%1.3fms, store=%1.3fms\n", (t1 - t0) / 1e6, (t2 - t1) / 1e6);
      }
    }
  }
  
  @Test
  public void compositeStore() throws IOException
  {
    IRandomAccessStoreFactory factory = new IRandomAccessStoreFactory() {
      public IRandomAccessStore createInstance() 
      {
        return new MemoryStore( 1000);
      }
    };
    
    int degree = 3;
    CompositeStore store = new CompositeStore( Collections.<IRandomAccessStore>emptyList(), factory);
    BTree<String> btree = new BTree<String>( degree, recordFormat, store);
    Database<String> db = new Database<String>( btree, store, recordFormat);

    Random random = new Random( 1);
    byte[] record = new byte[ 3];
    for( int i=0; i<100000; i++)
    {
      int j = random.nextInt( 26);
      record[ 0] = 1; record[ 1] = (byte)(j + 65); record[ 2] = '#';
      String key = String.format( "%c", 65 + j);
      db.insert( key, record);
      
      j = random.nextInt( 26);
      key = String.format( "%c", 65 + j);
      db.delete( key);
      
      if ( (i % 10000) == 0)
      {
        long t0 = System.nanoTime();
        btree = new BTree<String>( degree, recordFormat, store);
        db = new Database<String>( btree, store, recordFormat);
        
        long t1 = System.nanoTime();
        btree.store();
        
        long t2 = System.nanoTime();
        System.out.printf( "index=%1.3fms, store=%1.3fms\n", (t1 - t0) / 1e6, (t2 - t1) / 1e6);
      }
    }
  }
  
  private IKeyFormat<String> keyFormat;
  private IRecordFormat<String> recordFormat;
}
