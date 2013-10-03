package de.ifgi.lod4wfs.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import de.ifgi.lod4wfs.core.GeographicLayer;
import de.ifgi.lod4wfs.facade.Facade;

/**
 * @author jones
 * @version 1.0
 */

public class ServletWFS extends HttpServlet
{
	private String greeting="Linked Open Data for Web Feature Services Adapter";
	private static Logger logger = Logger.getLogger("Web-Interface");

	public ServletWFS(){

	}


	public ServletWFS(String greeting)
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

		System.out.println("\nIncoming request:\n");
		
//		System.out.println(request.getRequestURL());
//		System.out.println(request.getRequestURI());
//		System.out.println(request.getQueryString() + "\n");

		// TODO Implement MAXFEATURE for GetFeature
		
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
		
		/**
		 * Checking if the current request is valid
		 */

		String validRequest = new String();
		validRequest = this.validateRequest(currentVersion,currentRequest, currentService,currentTypeName,currentSRSName);

		if(validRequest.equals("valid")){

			System.out.println("\n");

			/**
			 * GetCapabilities request
			 */

			if(currentRequest.toUpperCase().equals("GETCAPABILITIES")){	

				if(currentVersion.equals("1.0.0")){

					logger.info("Processing " + currentRequest + "  ...");

					CapabilitiesDocuemnt = Facade.getInstance().getCapabilities(currentVersion);

				} 

				response.setContentType("text/xml");
				response.setStatus(HttpServletResponse.SC_OK);	
				response.getWriter().println(CapabilitiesDocuemnt);

				logger.info(currentRequest +  " request delivered. ");

				/**
				 * GetFeature request
				 */

			} else if (currentRequest.toUpperCase().equals("GETFEATURE")) {

				GeographicLayer layer = new GeographicLayer();
				layer.setName(currentTypeName);
				response.setContentType("text/xml");
				response.setStatus(HttpServletResponse.SC_OK);


				logger.info("Processing " + currentRequest +  " request for the feature "+ layer.getName() + " ...");
							
				response.getWriter().println(Facade.getInstance().getFeature(layer));
			
				logger.info(currentRequest +  " request delivered. \n");

				/**
				 * DescribeFeatureType request
				 */
				
			} else if (currentRequest.toUpperCase().equals("DESCRIBEFEATURETYPE")) {

				GeographicLayer layer = new GeographicLayer();
				layer.setName(currentTypeName);
				response.setContentType("text/xml");
				response.setStatus(HttpServletResponse.SC_OK);	

				logger.info("Processing " + currentRequest +  " request for the feature "+ layer.getName() + " ...");

				response.getWriter().println(Facade.getInstance().describeFeatureType(layer));

				logger.info(currentRequest +  " request delivered.");

			}

		} else {

			response.getWriter().println(validRequest);

		}

	}


	private String validateRequest(String version, String request, String service, String typeName, String SRS){

		String result = new String();
		boolean valid = true;
		try {

			//result = new Scanner(new File("src/main/resources/wfs/ServiceExceptionReport.xml")).useDelimiter("\\Z").next();
			result = new Scanner(new File("wfs/ServiceExceptionReport.xml")).useDelimiter("\\Z").next();

			if(!service.toUpperCase().equals("WFS")){

				if(service.isEmpty()){
					
					result = result.replace("PARAM_REPORT", "No service provided in the request.");
					result = result.replace("PARAM_CODE", "ServiceNotProvided");
					logger.error("No service provided in the request.");					
					
				} else {
					result = result.replace("PARAM_REPORT", "Service " + service + " is not supported by this server.");
					result = result.replace("PARAM_CODE", "ServiceNotSupported");
					logger.error("Service " + service + " is not supported by this server.");
				}
				
				valid = false;

			} else if (!version.equals("1.0.0")){

				if (version.isEmpty()){

					result = result.replace("PARAM_REPORT", "Web Feature Service version not informed.");
					result = result.replace("PARAM_CODE", "VersionNotProvided");
					logger.error("Web Feature Service version not informed.");

				} else {

					result = result.replace("PARAM_REPORT", "WFS version " + version + " is not supported by this server.");
					result = result.replace("PARAM_CODE", "VersionNotSupported");
					logger.error("WFS version " + version + " is not supported by this server.");


				}

				valid = false;

			} else if(!request.toUpperCase().equals("GETCAPABILITIES") && 
					!request.toUpperCase().equals("DESCRIBEFEATURETYPE") &&
					!request.toUpperCase().equals("GETFEATURE")){

				result = result.replace("PARAM_REPORT", "Operation " + request + " not supported by WFS.");
				result = result.replace("PARAM_CODE", "OperationNotSupported");
				logger.error("Operation " + request + " not supported by WFS.");
				valid = false;

			} else if (request.toUpperCase().equals("DESCRIBEFEATURETYPE") && typeName.isEmpty()){

				result = result.replace("PARAM_REPORT", "No feature provided for " + request + ".");
				result = result.replace("PARAM_CODE", "FeatureNotProvided");
				logger.error("No feature provided for " + request + ".");
				valid = false;

			} else if (request.toUpperCase().equals("GETFEATURE") && typeName.isEmpty()){

				result = result.replace("PARAM_REPORT", "No feature provided for " + request + ".");
				result = result.replace("PARAM_CODE", "FeatureNotProvided");
				logger.error("No feature provided for " + request + ".");
				valid = false;

			}

			if(!valid){
				result = result.replace("PARAM_LOCATOR", request);
			} else {
				result = "valid";
			}


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


		return result;
	}

}