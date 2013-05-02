package de.ifgi.lod4wfs.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;

public class TestServer {

	public static void main(String[] args) throws Exception
	{
	    Server server = new Server(8080);
	    
	    
        ContextHandler context = new ContextHandler();
        context.setContextPath("/test");
        context.setResourceBase(".");
        
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        server.setHandler(context);

        
	    server.setHandler(new HelloHandler());
	 
	    server.start();
	    server.join();
	}
	
}
