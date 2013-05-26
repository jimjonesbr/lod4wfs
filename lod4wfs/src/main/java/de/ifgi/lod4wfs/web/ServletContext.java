package de.ifgi.lod4wfs.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import de.ifgi.lod4wfs.core.GlobalSettings;

/**
 * 
 * @author jones
 * @version 1.0
 */

public class ServletContext{
	public static String startTime;

	public static void main(String[] args) throws Exception
	{

		Server server = new Server(GlobalSettings.defaultPort);

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		startTime = dateFormat.format(date);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/"+GlobalSettings.defaultServiceName);
		
		server.setHandler(context);
		
		context.addServlet(new ServletHolder(new ServletWFS()),"/*");
//		context.addServlet(new ServletHolder(new ServletGUI("Buongiorno Mondo")),"/it/*");

		//context.addServlet(new ServletHolder(new ServletWFS("Service started at: " + startTime )),"/wfs/*");

		
		server.start();

		System.out.println("Web Feature Service Adapter started\n\n" +
				"Startup time: " + startTime + "\n" +
				"Port: " + GlobalSettings.defaultPort + "\n");
		
		server.join();


	}
}