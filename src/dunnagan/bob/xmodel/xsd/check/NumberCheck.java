/*
 * XModel
 * Author: Bob Dunnagan
 * Copyright 2005. All rights reserved.
 */
package dunnagan.bob.xmodel.xsd.check;

import java.math.BigDecimal;

import dunnagan.bob.xmodel.IModelObject;
import dunnagan.bob.xmodel.Xlate;

public class NumberCheck extends AbstractCheck
{
  public NumberCheck( IModelObject schemaLocus)
  {
    super( schemaLocus);
    floatNode = schemaLocus.getFirstChild( "float");
    integerNode = schemaLocus.getFirstChild( "integer");
    minNode = schemaLocus.getFirstChild( "min");
    maxNode = schemaLocus.getFirstChild( "max");
    minString = Xlate.get( minNode, "");
    maxString = Xlate.get( maxNode, "");
  }

  /* (non-Javadoc)
   * @see dunnagan.bob.xmodel.xsd.nu.ICheck#validateImpl(dunnagan.bob.xmodel.IModelObject)
   */
  protected boolean validateImpl( IModelObject documentLocus)
  {
    String string = Xlate.get( documentLocus, "");
    if ( integerNode != null && string.contains( ".")) return false;
    
    try
    {
      if ( minNode != null)
      {
        if ( minString.length() >= 18)
        {
          BigDecimal minValue = new BigDecimal( minString);
          BigDecimal value = new BigDecimal( string);
          if ( Xlate.get( minNode, "exclusive", false))
          {
            if ( value.compareTo( minValue) <= 0) return false;
          }
          else
          {
            if ( value.compareTo( minValue) < 0) return false;
          }
        }
        else
        {
          if ( floatNode != null)
          {
            double minValue = Double.valueOf( minString);
            double value = Double.valueOf( string);
            if ( Xlate.get( minNode, "exclusive", false))
            {
              if ( value <= minValue) return false;
            }
            else
            {
              if ( value < minValue) return false;
            }
          }
          else
          {
            long minValue = Long.valueOf( minString);
            long value = Long.valueOf( string);
            if ( Xlate.get( minNode, "exclusive", false))
            {
              if ( value <= minValue) return false;
            }
            else
            {
              if ( value < minValue) return false;
            }
          }
        }
      }
      
      if ( maxNode != null)
      {
        if ( maxString.length() >= 18)
        {
          BigDecimal maxValue = new BigDecimal( maxString);
          BigDecimal value = new BigDecimal( string);
          if ( Xlate.get( maxNode, "exclusive", false))
          {
            if ( value.compareTo( maxValue) >= 0) return false;
          }
          else
          {
            if ( value.compareTo( maxValue) > 0) return false;
          }
        }
        else
        {
          if ( floatNode != null)
          {
            double maxValue = Double.valueOf( maxString);
            double value = Double.valueOf( string);
            if ( Xlate.get( maxNode, "exclusive", false))
            {
              if ( value >= maxValue) return false;
            }
            else
            {
              if ( value > maxValue) return false;
            }
          }
          else
          {
            long maxValue = Long.valueOf( maxString);
            long value = Long.valueOf( string);
            if ( Xlate.get( maxNode, "exclusive", false))
            {
              if ( value >= maxValue) return false;
            }
            else
            {
              if ( value > maxValue) return false;
            }
          }
        }
      }
    }
    catch( NumberFormatException e)
    {
      return false;
    }
    
    return true;
  }

  IModelObject floatNode;
  IModelObject integerNode;
  IModelObject minNode;
  IModelObject maxNode;
  String minString;
  String maxString;
}
