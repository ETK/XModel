package org.xmodel.log;

import java.util.HashMap;
import java.util.Map;

/**
 * Yet another logging facility.
 */
public class Log
{
//  public enum Level
//  {
//    fatal( 0x01),
//    severe( 0x02),
//    error( 0x04),
//    warn( 0x08),
//    info( 0x10),
//    debug( 0x20),
//    verbose( 0x40),
//    exception( 0x80),
//    problems( 0x8F),
//    all( 0xFF);
//    
//    private Level( int mask) { this.mask = mask;}
//    public final int mask;
//  }
  
  public final static int exception = 0x80;
  public final static int verbose = 0x40;
  public final static int debug = 0x20;
  public final static int info = 0x10;
  public final static int warn = 0x08;
  public final static int error = 0x04;
  public final static int severe = 0x02;
  public final static int fatal = 0x01;
  public final static int problems = exception | warn | error | severe | fatal;
  public final static int all = 0xff;

  /**
   * Returns the name of the specified logging level.
   * @param level The logging level.
   * @return Returns the name of the specified logging level.
   */
  public final static String getLevelName( int level)
  {
    switch( level)
    {
      case Log.exception: return "Exception";
      case Log.verbose:   return "Verbose";
      case Log.debug:     return "Debug";
      case Log.info:      return "Info";
      case Log.warn:      return "Warn";
      case Log.error:     return "Error";
      case Log.severe:    return "Severe";
      case Log.fatal:     return "Fatal";
    }
    return null;
  }

  /**
   * Returns the logging level given the case-insensitive name of the level.
   * @param name The name of the logging level.
   * @return Returns the logging level given the case-insensitive name of the level.
   */
  public final static int getLevelIndex( String name)
  {
    if ( name.equalsIgnoreCase( "verbose")) return Log.verbose;
    if ( name.equalsIgnoreCase( "debug")) return Log.debug;
    if ( name.equalsIgnoreCase( "info")) return Log.info;
    if ( name.equalsIgnoreCase( "warn")) return Log.warn;
    if ( name.equalsIgnoreCase( "error")) return Log.error;
    if ( name.equalsIgnoreCase( "severe")) return Log.severe;
    if ( name.equalsIgnoreCase( "fatal")) return Log.fatal;
    if ( name.equalsIgnoreCase( "exception")) return Log.exception;
    
    throw new IllegalArgumentException( String.format( "Invalid log level name, %s.", name));
  }
    
  /**
   * Returns the log with the specified name.
   * @param name The name of the log.
   * @return Returns the log with the specified name.
   */
  public static Log getLog( String name)
  {
    Log log = logs.get( name);
    if ( log == null) log = new Log( name);
    logs.put( name, log);
    return log;
  }
  
  /**
   * Returns the log with the fully-qualified name of the specified class.
   * @param clazz The class.
   * @return Returns the log with the fully-qualified name of the specified class.
   */
  public static Log getLog( Class<?> clazz)
  {
    return getLog( clazz.getName());
  }
  
  public Log( String name)
  {
    this.name = name;
    mask = -1;
  }
  
  /**
   * @return Returns the name of the log.
   */
  public String getName()
  {
    return name;
  }
  
  /**
   * Set the log level.
   * @param level The level.
   */
  public void setLevel( int level)
  {
    if ( mask < 0) configure();
    this.mask = level;
  }
  
  /**
   * Set the log sink (not thread-safe).
   * @param sink The log sink.
   */
  public void setSink( ILogSink sink)
  {
    if ( mask < 0) configure();
    this.sink = sink;
  }
  
  /**
   * Returns true if the specified logging level is enabled.
   * @param level The logging level.
   * @return Returns true if the specified logging level is enabled.
   */
  public boolean isLevelEnabled( int level)
  {
    if ( mask < 0) configure();
    return (mask & level) != 0;
  }
  
  /**
   * Log a verbose message.
   * @param message The message.
   */
  public void verbose( Object message)
  {
    log( verbose, message);
  }
  
  /**
   * Log a verbose message with a caught exception.
   * @param message The message.
   * @param Throwable The throwable that was caught.
   */
  public void verbose( Object message, Throwable throwable)
  {
    log( verbose, message, throwable);
  }
  
  /**
   * Log a verbose message.
   * @param format The format.
   * @param params The format arguments.
   */
  public void verbosef( String format, Object... params)
  {
    logf( verbose, format, params);
  }
  
  /**
   * Log a debug message.
   * @param message The message.
   */
  public void debug( Object message)
  {
    log( debug, message);
  }
  
  /**
   * Log a debug message with a caught exception.
   * @param message The message.
   * @param Throwable The throwable that was caught.
   */
  public void debug( Object message, Throwable throwable)
  {
    log( debug, message, throwable);
  }
  
  /**
   * Log a debug message.
   * @param format The format.
   * @param params The format arguments.
   */
  public void debugf( String format, Object... params)
  {
    logf( debug, format, params);
  }
  
  /**
   * Log an information message.
   * @param message The message.
   */
  public void info( Object message)
  {
    log( info, message);
  }
  
  /**
   * Log a info message with a caught exception.
   * @param message The message.
   * @param Throwable The throwable that was caught.
   */
  public void info( Object message, Throwable throwable)
  {
    log( info, message, throwable);
  }
  
  /**
   * Log an information message.
   * @param format The format.
   * @param params The format arguments.
   */
  public void infof( String format, Object... params)
  {
    logf( info, format, params);
  }
  
  /**
   * Log a warning message.
   * @param message The message.
   */
  public void warn( Object message)
  {
    log( warn, message);
  }
  
  /**
   * Log a warning message with a caught exception.
   * @param message The message.
   * @param Throwable The throwable that was caught.
   */
  public void warn( Object message, Throwable throwable)
  {
    log( warn, message, throwable);
  }
  
  /**
   * Log a warning message.
   * @param format The format.
   * @param params The format arguments.
   */
  public void warnf( String format, Object... params)
  {
    logf( warn, format, params);
  }
  
  /**
   * Log an error message.
   * @param message The message.
   */
  public void error( Object message)
  {
    log( error, message);
  }
  
  /**
   * Log an error message with a caught exception.
   * @param message The message.
   * @param Throwable The throwable that was caught.
   */
  public void error( Object message, Throwable throwable)
  {
    log( error, message, throwable);
  }
  
  /**
   * Log an error message.
   * @param format The format.
   * @param params The format arguments.
   */
  public void errorf( String format, Object... params)
  {
    logf( error, format, params);
  }
  
  /**
   * Log a severe message.
   * @param message The message.
   */
  public void severe( Object message)
  {
    log( severe, message);
  }
  
  /**
   * Log a severe message with a caught exception.
   * @param message The message.
   * @param Throwable The throwable that was caught.
   */
  public void severe( Object message, Throwable throwable)
  {
    log( severe, message, throwable);
  }
  
  /**
   * Log a severe message.
   * @param format The format.
   * @param params The format arguments.
   */
  public void severef( String format, Object... params)
  {
    logf( severe, format, params);
  }
  
  /**
   * Log a fatal message.
   * @param message The message.
   */
  public void fatal( Object message)
  {
    log( fatal, message);
  }
  
  /**
   * Log a fatal message with a caught exception.
   * @param message The message.
   * @param Throwable The throwable that was caught.
   */
  public void fatal( Object message, Throwable throwable)
  {
    log( fatal, message, throwable);
  }
  
  /**
   * Log a fatal message.
   * @param format The format.
   * @param params The format arguments.
   */
  public void fatalf( String format, Object... params)
  {
    logf( fatal, format, params);
  }
  
  /**
   * Log an exception.
   * @param throwable The exception.
   */
  public void exception( Throwable throwable)
  {
    if ( mask < 0) configure();
    if ( (mask & exception) == 0) return;
    sink.log( this, exception, throwable);
  }
  
  /**
   * Log an exception with a message.
   * @param throwable The exception.
   * @param format The format.
   * @param params The format arguments.
   */
  public void exceptionf( Throwable throwable, String format, Object... params)
  {
    if ( mask < 0) configure();
    if ( (mask & exception) == 0) return;
    sink.log( this, exception, String.format( format, params), throwable);
  }

  /**
   * Log a message.
   * @param level The logging level.
   * @param message The message.
   */
  public void log( int level, Object message)
  {
    if ( mask < 0) configure();
    if ( (mask & level) == 0) return;
    sink.log( this, level, message);
  }
  
  /**
   * Log a message with a caught exception.
   * @param level The logging level.
   * @param message The message.
   * @param throwable The throwable that was caught.
   */
  public void log( int level, Object message, Throwable throwable)
  {
    if ( mask < 0) configure();
    if ( (mask & level) == 0) return;
    sink.log( this, level, message, throwable);
  }
  
  /**
   * Log a message.
   * @param level The logging level.
   * @param format The format.
   * @param params The format arguments.
   */
  public void logf( int level, String format, Object... params)
  {
    if ( mask < 0) configure();
    if ( (mask & level) == 0) return;
    sink.log( this, level, String.format( format, params));
  }
  
  /**
   * Configure the log level and sink for this log.
   */
  private void configure()
  {
    mask = 0x9F;
    try
    {
      sink = new FormatSink( new ConsoleSink());
    }
    catch( Exception e)
    {
      sink = new FormatSink( new ConsoleSink());
    }
  }
  
  private static Map<String, Log> logs = new HashMap<String, Log>();

  private String name;
  private volatile int mask;
  private volatile ILogSink sink;
}
