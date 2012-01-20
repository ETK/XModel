package org.xmodel.compress.serial;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.xmodel.compress.CompressorException;
import org.xmodel.compress.ISerializer;

/**
 * An implementation of ISerializer that serializes java.lang.Number.
 */
public class NumberSerializer implements ISerializer
{
  /* (non-Javadoc)
   * @see org.xmodel.compress.ISerializer#readObject(java.io.DataInput)
   */
  @Override
  public Object readObject( DataInput input) throws IOException, ClassNotFoundException, CompressorException
  {
    byte type = input.readByte();
    if ( type == 0x13)
    {
      return input.readFloat();
    }
    else if ( type == 0x14)
    {
      return input.readDouble();
    }
    else if ( type == 0x15)
    {
      int length = input.readByte() & 0xFF;
      byte[] bytes = new byte[ length];
      BigInteger unscaledValue = new BigInteger( bytes);
      int scale = input.readInt();
      return new BigDecimal( unscaledValue, scale);
    }
    else if ( type == 0x01)
    {
      return input.readByte();
    }
    else if ( type == 0x02)
    {
      return input.readShort();
    }
    else if ( type == 0x03)
    {
      return input.readInt();
    }
    else if ( type == 0x04)
    {
      return input.readLong();
    }
    else if ( type == 0x05)
    {
      int length = input.readByte() & 0xFF;
      byte[] bytes = new byte[ length];
      return new BigInteger( bytes);
    }
    
    throw new IOException( "Illegal number type specifier.");
  }

  /* (non-Javadoc)
   * @see org.xmodel.compress.ISerializer#writeObject(java.io.DataOutput, java.lang.Object)
   */
  @Override
  public int writeObject( DataOutput output, Object object) throws IOException, CompressorException
  {
    if ( object instanceof Float) 
    {
      output.writeByte( 0x13);
      output.writeFloat( (Float)object);
    }
    else if ( object instanceof Double) 
    {
      output.writeByte( 0x14);
      output.writeDouble( (Double)object);
    }
    else if ( object instanceof BigDecimal) 
    {
      output.writeByte( 0x15);
      BigDecimal value = (BigDecimal)object;
      BigInteger unscaled = value.unscaledValue();
      byte[] unscaledBytes = unscaled.toByteArray();
      output.writeByte( (byte)unscaledBytes.length);
      output.write( unscaledBytes);
      output.writeInt( value.scale());
    }
    else if ( object instanceof Byte) 
    {
      output.writeByte( 0x01);
      output.writeByte( (Byte)object);
    }
    else if ( object instanceof Short) 
    {
      output.writeByte( 0x02);
      output.writeShort( (Short)object);
    }
    else if ( object instanceof Integer) 
    {
      output.writeByte( 0x03);
      output.writeInt( (Integer)object);
    }
    else if ( object instanceof Long) 
    {
      output.writeByte( 0x04);
      output.writeLong( (Long)object);
    }
    else if ( object instanceof BigInteger) 
    {
      output.writeByte( 0x05);
      BigInteger value = (BigInteger)object;
      byte[] bytes = value.toByteArray();
      output.writeByte( (byte)bytes.length);
      output.write( bytes);
    }
    
    throw new IllegalArgumentException();
  }
}
