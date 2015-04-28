package de.ifgi.lod4wfs.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import de.ifgi.lod4wfs.core.Utils;
import de.ifgi.lod4wfs.core.WFSFeature;
import de.ifgi.lod4wfs.facade.Facade;

/**
 * @author Jim Jones
 */

public class ServletWFS extends HttpServlet
{
	
    /**
	 * Automatically generated serial version UID.
	 */
	
	private static final long serialVersionUID = 1463163373195766944L;
	private static Logger logger = Logger.getLogger("Web-Interface");

	public ServletWFS(){
		
		super();
		
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
		String currentOutputFormat = new String();
		String currentOptionsFormat = new String();
		String currentMaxFeature = new String();

		System.out.println("\nIncoming request:\n");


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

			if(parameter.toUpperCase().equals("OUTPUTFORMAT")){

				currentOutputFormat=request.getParameter(parameter);
				
			}

			if(parameter.toUpperCase().equals("FORMAT_OPTIONS")){

				currentOptionsFormat=request.getParameter(parameter);
				
			}
			
			if (parameter.toUpperCase().equals("MAXFEATURE")) {

				currentMaxFeature = request.getParameter(parameter);
				
			}

		}

		/**
		 * Checking if the current request is valid
		 */

		String validRequest = new String();
		validRequest = this.validateRequest(currentVersion, currentRequest, currentService, currentTypeName, currentSRSName, currentOutputFormat, currentOptionsFormat);

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

				WFSFeature layer = new WFSFeature();
				layer.setName(currentTypeName);

				logger.info("Processing " + currentRequest +  " request for the feature [" + layer.getName() + "] ...");

				response.setStatus(HttpServletResponse.SC_OK);


				if (currentOutputFormat.toUpperCase().equals("TEXT/JAVASCRIPT")) {				

					if (currentOptionsFormat.toUpperCase().equals("CALLBACK:LOADGEOJSON")){

						layer.setOutputFormat("geojson");
						response.setContentType("text/javascript");
						response.getWriter().println("loadGeoJson(" + Facade.getInstance().getFeature(layer) + ")");

					} else if (currentOptionsFormat.toUpperCase().equals("JSON")){

						layer.setOutputFormat("json");
						response.setContentType("text/javascript");
						response.getWriter().println(Facade.getInstance().getFeature(layer));


					} else if (currentOptionsFormat.toUpperCase().equals("ZIP")){

						response.setContentType("application/zip");

						File zipFile = Utils.compressFile(Facade.getInstance().getFeature(layer), layer.getName().replace(":", "_") + ".json");						
						byte[] buffer = new byte[128];

						logger.info("Feature " + layer.getName() + " successfully compressed.");

						FileInputStream fileInput = new FileInputStream(zipFile);
						response.setHeader("Content-Disposition", "attachment;filename=\"" + layer.getName().replace(":", "_") + ".zip\"");

						int totalRead = 0;
						int readBytes = 0;

						while(totalRead < zipFile.length()) {

							if(zipFile.length()-totalRead > 128) {

								readBytes = fileInput.read(buffer, 0, 128);
								totalRead += readBytes;

							} else {

								readBytes=fileInput.read(buffer,0,(int)zipFile.length() - totalRead);
								totalRead= totalRead+readBytes; 
								
							}
							
							response.getOutputStream().write(buffer, 0, readBytes);

						}

						fileInput.close();

					} else {

						response.getWriter().println(Facade.getInstance().getFeature(layer));
					}


				} else if (currentOutputFormat.toUpperCase().equals("GML2") || 
						currentOutputFormat.toUpperCase().isEmpty()) {

					layer.setOutputFormat("xml");

					if (currentOptionsFormat.toUpperCase().equals("ZIP")){

						response.setContentType("application/zip");

						File zipFile = Utils.compressFile(Facade.getInstance().getFeature(layer), layer.getName().replace(":", "_")+".xml");						
						byte[] buffer = new byte[128];

						logger.info("Feature [" + layer.getName() + "] successfully compressed.");

						FileInputStream fileInput = new FileInputStream(zipFile);
						response.setHeader("Content-Disposition", "attachment;filename=\"" + layer.getName().replace(":", "_") + ".zip\"");

						int totalRead = 0;
						int readBytes = 0;

						while(totalRead < zipFile.length()) {

							if(zipFile.length()-totalRead > 128) {

								readBytes = fileInput.read(buffer, 0, 128);
								totalRead += readBytes;

							} else {

								readBytes=fileInput.read(buffer,0,(int)zipFile.length() - totalRead);
								totalRead= totalRead+readBytes; }
							response.getOutputStream().write(buffer, 0, readBytes);

						}

						fileInput.close();		

					} else {

						response.setContentType("text/xml");
						response.getWriter().println(Facade.getInstance().getFeature(layer));

					}


				}


				logger.info(currentRequest +  " request delivered. \n");

				/**
				 * DescribeFeatureType request
				 */

			} else if (currentRequest.toUpperCase().equals("DESCRIBEFEATURETYPE")) {

				WFSFeature layer = new WFSFeature();
				layer.setName(currentTypeName);
				response.setContentType("text/xml");
				response.setStatus(HttpServletResponse.SC_OK);	

				logger.info("Processing " + currentRequest +  " request for the feature ["+ layer.getName() + "] ...");

				response.getWriter().println(Facade.getInstance().describeFeatureType(layer));

				logger.info(currentRequest +  " request delivered.");

			}

		} else {

			response.getWriter().println(validRequest);

		}

	}


	private String validateRequest(String version, String request, String service, String typeName, String SRS, String outputFormat, String formatOptions){

		String result = new String();
		boolean valid = true;
		try {

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

				/**
				 * Supported output formats:
				 * 	GeoJSON (text/javascript)
				 * 	GML2    
				 *  ZIP 
				 */
			} else if (!outputFormat.toUpperCase().equals("TEXT/JAVASCRIPT") && 
					!outputFormat.isEmpty() && //GML2 is assumed for requests without an explicit output format. 
					!outputFormat.toUpperCase().equals("GML2")){

				result = result.replace("PARAM_REPORT", "Invalid output format for " + request + ". The output format '"+ outputFormat + "' is not supported.");
				result = result.replace("PARAM_CODE", "InvalidOutputFormat");
				logger.error("Invalid output format for " + request + ". The output format " + outputFormat + " is not supported.");
				valid = false;

			} else if (!formatOptions.toUpperCase().equals("ZIP") &&
					!formatOptions.toUpperCase().equals("CALLBACK:LOADGEOJSON") &&
					!formatOptions.toUpperCase().equals("JSON") &&
					!formatOptions.toUpperCase().isEmpty()){

				result = result.replace("PARAM_REPORT", "Invalid output format option for " + request + ". The output format option '" + formatOptions + "' is not supported.");
				result = result.replace("PARAM_CODE", "InvalidOutputFormatOption");
				logger.error("Invalid output format option for " + request + ". The output format option '" + formatOptions + "' is not supported.");
				valid = false;

			}// else if (currentMax)

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