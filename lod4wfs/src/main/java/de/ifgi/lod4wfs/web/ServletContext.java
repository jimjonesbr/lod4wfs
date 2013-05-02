package de.ifgi.lod4wfs.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ServletContext
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
 
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/lod4wfs");
        server.setHandler(context);
        
        context.addServlet(new ServletHolder(new ServletParser()),"/*");
        context.addServlet(new ServletHolder(new ServletParser("Buongiorno Mondo")),"/it/*");
        context.addServlet(new ServletHolder(new ServletParser("Bonjour le Monde")),"/fr/*");
        
        context.addServlet(new ServletHolder(new ServletParser("Connected to lod4wfs.")),"/wfs/*");
 
        server.start();
        server.join();
    }
}