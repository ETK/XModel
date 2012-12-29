package org.xmodel.lss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmodel.lss.IRecordFormat.RecordType;

public class Analysis<K>
{
  public Analysis( IRecordFormat<K> recordFormat)
  {
    this.recordFormat = recordFormat;
  }
  
  /**
   * Scans the entire store and returns pointers to all garbage records.
   * @param store The store.
   * @return Returns the list of pointers.
   */
  public List<Long> findGarbage( IRandomAccessStore store) throws IOException
  {
    store.seek( 4 + 8);
    
    List<Long> result = new ArrayList<Long>();
    while( store.position() < store.length())
    {
      long position = store.position();
      RecordType type = recordFormat.advance( store);
      if ( type == RecordType.garbage) result.add( position);
    }
    
    return result;
  }
   
  /**
   * Compute the degree of fragmentation of the entire store as, (f - fm) / f, where f is the number
   * of free bytes, and fm is the size of the largest free block.
   * @param store The store.
   * @return Returns the score.
   */
  public double computeFragmentation( IRandomAccessStore store) throws IOException
  {
    store.seek( 4 + 8);

    double f = 0;
    double fm = 0;
    while( store.position() < store.length())
    {
      long position = store.position();
      
      RecordType type = recordFormat.advance( store);
      if ( type == RecordType.garbage)
      {
        long length = store.position() - position;
        if ( length > fm) fm = length;
        f += length;
      }
    }
    
    //System.out.printf( "f=%f, fm=%f\n", f, fm);
    return (f > 0)? ((f - fm) / f): 0;
  }
  
  private IRecordFormat<K> recordFormat;
}
