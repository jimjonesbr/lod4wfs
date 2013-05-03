package de.ifgi.lod4wfs.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ServletContext{
	public static String startTime;
	
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
		
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		startTime = dateFormat.format(date);
		
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        //TODO: Create welcome page and attach to /lod4wfs 
        //TODO: Implement log4j for logging errors and transactions.
        
        context.setContextPath("/lod4wfs");
        server.setHandler(context);
        
        context.addServlet(new ServletHolder(new ServletParser()),"/*");
//        context.addServlet(new ServletHolder(new ServletParser("Buongiorno Mondo")),"/it/*");
//        context.addServlet(new ServletHolder(new ServletParser("Bonjour le Monde")),"/fr/*");
        
        context.addServlet(new ServletHolder(new ServletParser("Service started at: " + startTime )),"/wfs/*");
 
        server.start();
        server.join();
    }
}