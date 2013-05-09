package de.ifgi.lod4wfs.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.hp.hpl.jena.util.FileUtils;
import de.ifgi.lod4wfs.core.GeographicLayer;
import de.ifgi.lod4wfs.facade.Facade;

public class ServletParser extends HttpServlet
{
	private String greeting="Linked Open Data for Web Feature Services Adapter";

	//private String version="Beta 0.1.0";

	public ServletParser(){}


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


		if(currentRequest.equals("GetCapabilities")){	

			if(currentVersion.equals("1.0.0")){

				CapabilitiesDocuemnt = Facade.getInstance().getCapabilities(currentVersion);

			} else if(currentVersion.equals("2.0.0")){

				CapabilitiesDocuemnt = "Version not supported.";
			}

			response.setContentType("text/xml");
			response.setStatus(HttpServletResponse.SC_OK);	
			response.getWriter().println(CapabilitiesDocuemnt);

			
		} else if (currentRequest.equals("GetFeature")) {

			GeographicLayer sp = new GeographicLayer();
			sp.setName(currentTypeName);
			response.setContentType("text/xml");
			response.setStatus(HttpServletResponse.SC_OK);	
			response.getWriter().println(Facade.getInstance().getFeature(sp));
			
		} else if (currentRequest.equals("DescribeFeatureType")) {

			GeographicLayer layer = new GeographicLayer();
			layer.setName(currentTypeName);
			response.setContentType("text/xml");
			response.setStatus(HttpServletResponse.SC_OK);	
			response.getWriter().println(Facade.getInstance().describeFeatureType(layer));
			

		}
	}

}