package de.ifgi.lod4wfs.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.facade.Facade;

/**
 * @author Jim Jones
 */

public class Start{
	public static String startTime;

	public static void main(String[] args) throws Exception
	{			
		
		GlobalSettings.loadVariables();
		
		/**
		 * First parameter: SPARQL Endpoint address. 
		 */
		if (args.length >= 1) {
			
			GlobalSettings.default_SPARQLEndpoint = args[0];
		}
		
		/**
		 * Second parameter: Server port
		 */
		if (args.length == 2) {
			
			GlobalSettings.defaultPort = Integer.parseInt(args[1]);
		}
			
		Server server = new Server(GlobalSettings.defaultPort);
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date date = new Date();
		GlobalSettings.startupTime = dateFormat.format(date);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/"+GlobalSettings.defaultServiceName);
		
    	server.setHandler(context);
		
		context.addServlet(new ServletHolder(new ServletWFS()),"/*");
		
		
//#####################		
		
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setDescriptor("jsp/xml/web.xml");
        webAppContext.setResourceBase("jsp/");
        webAppContext.setContextPath("/");
		
//***************
		
		ContextHandler contextGUI = new ContextHandler("/admin");
	    contextGUI.setHandler(new HandlerGUI("Bonjoir"));
	        
		ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] { context, contextGUI, webAppContext});
        
        server.setHandler(contexts);	
        
//*******************		
		
		Facade.getInstance().getCapabilities("1.0.0");
		
		server.start();
			
		System.out.println(GlobalSettings.crlf +
				"LOD4WFS Adapter (Linked Open Data for Web Feature Service) BETA 0.4 " + GlobalSettings.crlf +
				"Institut für Geoinformatik | Universitäts- und Landesbibliothek " + GlobalSettings.crlf +
				"Westfälische Wilhelms-Universität Münster" + GlobalSettings.crlf +
				"http://www.uni-muenster.de/" + GlobalSettings.crlf + GlobalSettings.crlf +
				
				"Startup time: " + GlobalSettings.startupTime + GlobalSettings.crlf +
				"Port: " + GlobalSettings.defaultPort + GlobalSettings.crlf);
		
		server.join();


	}
}