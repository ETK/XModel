package org.xmodel.xaction.debug;

import org.junit.Test;
import org.xmodel.INode;
import org.xmodel.concurrent.SerialExecutorDispatcher;
import org.xmodel.net.XioClient;
import org.xmodel.net.XioServer;
import org.xmodel.net.Session;
import org.xmodel.xaction.XAction;
import org.xmodel.xml.IXmlIO.Style;
import org.xmodel.xml.XmlIO;
import org.xmodel.xpath.expression.StatefulContext;

public class DebuggerTest
{
  public final static String host = "localhost";
  public final static int port = 10000;
  public final static int timeout = 10000;
  
  @Test public void breakpointTest() throws Exception
  {
    System.setProperty( "xaction.debug", "true");
    
    StatefulContext context = new StatefulContext();
    
    XioServer server = new XioServer( host, port);
    server.setServerContext( context);
    server.setDispatcher( new SerialExecutorDispatcher( 2));
    server.start( false);
    
    INode script = new XmlIO().read( 
        "<script>" +
        "  <assign var='x'>1</assign>" +
        "  <script>" +
        "    <breakpoint/>" +
        "    <assign var='x'>2</assign>" +
        "  </script>" +
        "  <assign var='x'>3</assign>" +
        "</script>"
    );

    XioClient client = new XioClient( host, port, false);
    Session session = client.connect( timeout);
    session.execute( new StatefulContext(), new String[ 0], script, 0);
    
    Debugger debugger = XAction.getDebugger();
    INode stack = debugger.getStack();
    System.out.println( XmlIO.write( Style.printable, stack));
  }
}
