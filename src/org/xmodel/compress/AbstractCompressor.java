/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package org.xmodel.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.xmodel.IModelObject;


/**
 * Abstract base implementation with get/set for compression level.
 */
public abstract class AbstractCompressor implements ICompressor
{
  /* (non-Javadoc)
   * @see org.xmodel.compress.ICompressor#compress(org.xmodel.IModelObject)
   */
  public byte[] compress( IModelObject element) throws CompressorException
  {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    compress( element, stream);
    byte[] bytes = stream.toByteArray();
    return bytes;
  }

  /* (non-Javadoc)
   * @see org.xmodel.compress.ICompressor#decompress(byte[], int)
   */
  public IModelObject decompress( byte[] bytes, int offset) throws CompressorException
  {
    ByteArrayInputStream stream = new ByteArrayInputStream( bytes, offset, bytes.length - offset);
    IModelObject result = decompress( stream);
    return result;
  }
}
