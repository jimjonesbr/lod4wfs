package de.ifgi.lod4wfs.web;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import de.ifgi.lod4wfs.core.GeographicLayer;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.facade.Facade;

public class ServletParser extends HttpServlet
{
	private String greeting="Linked Open Data for Web Feature Services Adapter";
	//private String version="Beta 0.1.0";
    private static Logger logger = Logger.getLogger("Server");
        
	public ServletParser(){
		
	}


	public ServletParser(String greeting)
	{
		this.greeting=greeting;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		Enumeration<String> listParameters = request.getParameterNames();
		String CapabilitiesDocuemnt = new String();
		String currentVersion = new String();
		String currentRequest = new String();
		String currentService = new String();
		String currentTypeName = new String();
		String currentSRSName = new String();

		System.out.println("Incoming request:\n");
		

		while (listParameters.hasMoreElements()) {
			String parameter = (String) listParameters.nextElement();

			System.out.println(parameter + " -> "+ request.getParameter(parameter)+"");

			if (parameter.toUpperCase().equals("VERSION")) {

				currentVersion=request.getParameter(parameter);
			}

			if(parameter.toUpperCase().equals("REQUEST")){

				currentRequest=request.getParameter(parameter);
			}

			if(parameter.toUpperCase().equals("TYPENAME")){

				currentTypeName=request.getParameter(parameter);
			}

			if(parameter.toUpperCase().equals("SRSNAME")){

				currentSRSName=request.getParameter(parameter);
			}

			if(parameter.toUpperCase().equals("SERVICE")){

				currentService=request.getParameter(parameter);
			}

		}


		System.out.println("\n");
		
		if(currentRequest.equals("GetCapabilities")){	

			if(currentVersion.equals("1.0.0")){
				
				
				logger.info("Processing " + currentRequest + "  ...");
				
				CapabilitiesDocuemnt = Facade.getInstance().getCapabilities(currentVersion);

			} else {

				CapabilitiesDocuemnt = "Version not supported.";
			}

			response.setContentType("text/xml");
			response.setStatus(HttpServletResponse.SC_OK);	
			response.getWriter().println(CapabilitiesDocuemnt);

			logger.info(currentRequest +  " request delivered. ");
			
			
		} else if (currentRequest.equals("GetFeature")) {

			GeographicLayer layer = new GeographicLayer();
			layer.setName(currentTypeName);
			response.setContentType("text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			
						
			logger.info("Processing " + currentRequest +  " request for the feature "+ layer.getName() + " ...");
			
			response.getWriter().println(Facade.getInstance().getFeature(layer));
			
			logger.info(currentRequest +  " request delivered. \n");
			
		} else if (currentRequest.equals("DescribeFeatureType")) {

			GeographicLayer layer = new GeographicLayer();
			layer.setName(currentTypeName);
			response.setContentType("text/xml");
			response.setStatus(HttpServletResponse.SC_OK);	
			
			
			logger.info("Processing " + currentRequest +  " request for the feature "+ layer.getName() + " ...");
			
			response.getWriter().println(Facade.getInstance().describeFeatureType(layer));
			
			logger.info(currentRequest +  " request delivered.");
		}
	}

}