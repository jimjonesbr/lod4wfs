package de.ifgi.lod4wfs.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
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

	private static Logger logger = Logger.getLogger("WFS-Servlet");
	
	public static void main(String[] args) throws Exception
	{			
		
		GlobalSettings.refreshSystemVariables();
		
		/**
		 * First parameter: SPARQL Endpoint address. 
		 */
		if (args.length >= 1) {
			
			GlobalSettings.setDefaultSPARQLEndpoint(args[0]);
		}
		
		/**
		 * Second parameter: Server port
		 */
		if (args.length == 2) {
			
			GlobalSettings.setDefaultPort(Integer.parseInt(args[1]));
			
		}
			
		Server server = new Server(GlobalSettings.getDefaultPort());
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date date = new Date();
		GlobalSettings.setStartupTime(dateFormat.format(date));

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/"+GlobalSettings.getDefaultServiceName());
		
    	server.setHandler(context);
		
		context.addServlet(new ServletHolder(new ServletWFS()),"/*");
		
		
//#####################		
		
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setDescriptor("jsp/xml/web.xml");
        webAppContext.setResourceBase("jsp/");
        webAppContext.setContextPath("/");
		
//*******************
		
		ContextHandler contextGUI = new ContextHandler("/admin");
	    contextGUI.setHandler(new HandlerGUI("Bonjoir"));
	        
		ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] { context, contextGUI, webAppContext});
        
        server.setHandler(contexts);	
        
//*******************
		
		Facade.getInstance().getCapabilities("1.0.0");
		
		server.start();
		
		
		logger.info("Service started:"+GlobalSettings.getCrLf() + GlobalSettings.getCrLf() +
				"LOD4WFS Adapter (Linked Open Data for Web Feature Service) " + GlobalSettings.getCrLf() + GlobalSettings.getCrLf() +
				"Institut für Geoinformatik | Universitäts- und Landesbibliothek " + GlobalSettings.getCrLf() +
				"Westfälische Wilhelms-Universität Münster" + GlobalSettings.getCrLf() +
				"http://www.uni-muenster.de/" + GlobalSettings.getCrLf() + GlobalSettings.getCrLf() +
								
				"Application Version: " + GlobalSettings.getAppVersion() + GlobalSettings.getCrLf() +
				"Startup time: " + GlobalSettings.getStartupTime() + GlobalSettings.getCrLf() +
				"Java Runtime: "+ System.getProperty("java.version") + GlobalSettings.getCrLf() +
				"Operating System: " + System.getProperty("os.name").toString() + " " + 
									   System.getProperty("os.version").toString() + " (" + 
									   System.getProperty("os.arch").toString()+")"+ GlobalSettings.getCrLf()+
				"Port: " + GlobalSettings.getDefaultPort() + GlobalSettings.getCrLf());
		
		server.join();


	}
}