package org.xmodel.log;

import org.xmodel.IModelObject;

/**
 * An implementation of Log.ISink that logs to one or more delegate ISink instances.
 */
public class MultiSink implements ILogSink
{
  /**
   * Create a MultiSink with the specified delegates.
   * @param delegates The delegates.
   */
  public MultiSink( ILogSink... delegates)
  {
    this.delegates = delegates;
  }
  
  /* (non-Javadoc)
   * @see org.xmodel.log.ILogSink#configure(org.xmodel.IModelObject)
   */
  @Override
  public void configure( IModelObject config)
  {
    ILogSink sink = LogManager.configure( config);
    if ( sink instanceof MultiSink)
    {
      delegates = ((MultiSink)sink).delegates;
    }
    else
    {
      delegates = new ILogSink[] { sink};
    }
  }

  /* (non-Javadoc)
   * @see org.xmodel.log.Log.ISink#log(org.xmodel.log.Log, int, java.lang.String, java.lang.Throwable)
   */
  @Override
  public void log( Log log, int level, Object message, Throwable throwable)
  {
    for( ILogSink sink: delegates) 
      if ( sink != null)
        sink.log( log, level, message, throwable);
  }

  /* (non-Javadoc)
   * @see org.xmodel.log.Log.ISink#log(org.xmodel.log.Log, int, java.lang.String)
   */
  @Override
  public void log( Log log, int level, Object message)
  {
    for( ILogSink sink: delegates) 
      if ( sink != null)
        sink.log( log, level, message);
  }

  /* (non-Javadoc)
   * @see org.xmodel.log.Log.ISink#log(org.xmodel.log.Log, int, java.lang.Throwable)
   */
  @Override
  public void log( Log log, int level, Throwable throwable)
  {
    for( ILogSink sink: delegates) 
      if ( sink != null)
        sink.log( log, level, throwable);
  }
  
  protected ILogSink[] delegates;
}
