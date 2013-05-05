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
import de.ifgi.lod4wfs.core.SpatialObject;
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
		
		System.out.println("Incoming request:\n");
		
		
		while (listParameters.hasMoreElements()) {
			String string = (String) listParameters.nextElement();

			System.out.println(string + " -> "+ request.getParameter(string)+"");

			if (string.toUpperCase().equals("VERSION")) {
				
				if(request.getParameter(string).equals("1.0.0")){
					CapabilitiesDocuemnt = Facade.getInstance().getCapabilities(request.getParameter(string));
				}

				if(request.getParameter(string).equals("2.0.0")){
					CapabilitiesDocuemnt = "Version not supported.";
				}
			}

			//if(request.getParameter(string).toUpperCase().equals("WFS")){ 
					
				
			if(request.getParameter(string).toUpperCase().equals("GETCAPABILITIES")){
								
				ArrayList<SpatialObject> list = new ArrayList<SpatialObject>(); 
				list = Facade.getInstance().listSpatialObjects();

				for (int i = 0; i < list.size(); i++) {
//					response.setContentType("text/xml");
//					response.setStatus(HttpServletResponse.SC_OK);				
//					response.getWriter().println("<h1>Feature: "+list.get(i).getName()+"</h1>");
//					response.getWriter().println("<h3>Title: "+list.get(i).getTitle()+"</h3>");
//					response.getWriter().println("<h3>Abstract: "+list.get(i).getFeatureAbstract()+"</h3>");
//					response.getWriter().println("<h3>CRS: "+list.get(i).getDefaultCRS()+"</h3>");
//					response.getWriter().println("<h3>Lower Corner: "+list.get(i).getLowerCorner()+"</h3>");
//					response.getWriter().println("<h3>Upper Corner: "+list.get(i).getUpperCorner()+"</h3>");
					
				}
			}
				
			//}





		}

		//File fXmlFile = new File("/home/jones/Desktop/staff.xml");
		
		response.setContentType("text/xml");
		response.setStatus(HttpServletResponse.SC_OK);	
		
		//String tmp = FileUtils.readWholeFileAsUTF8("/home/jones/Desktop/CapDoc2.xml");
		//String tmp = FileUtils.readWholeFileAsUTF8("/media/jones/Dateien/ifgi/MSc/Thesis/GEOSERVER.xml");
		//String tmp = FileUtils.readWholeFileAsUTF8("/media/jones/Dateien/ifgi/MSc/Thesis/MODEL-WFS.xml");
		//String tmp = FileUtils.readWholeFileAsUTF8("src/main/resources/CapabilitiesDocument_100.xml");
		
		//String tmp = Facade.getInstance().getCapabilities(versionx);
		response.getWriter().println(CapabilitiesDocuemnt);
				
		//response.setContentType("text/html");
		//response.setStatus(HttpServletResponse.SC_OK);
		//response.getWriter().println("<h1>"+greeting+"</h1>");
		//response.getWriter().println("<h2>"+version+"</h2>");
		//response.getWriter().println("session=" + request.getSession(true).getId());


		//		File fXmlFile = new File("/Users/mkyong/staff.xml");
		//		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		//		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		//		Document doc = dBuilder.parse(fXmlFile);

		//		System.out.println("\nParameters Values:\n");
		//		System.out.println("REQUEST: "+request.getParameter("REQUEST"));
		//		System.out.println("VERSION: "+request.getParameter("VERSION"));
		//		System.out.println("SERVICE: "+request.getParameter("SERVICE"));
		//		

		//		String serv = request.getParameter("SERVICE");
		//		
		//		System.out.println(serv);
		//		
		//		if(serv=="WFS"){
		//			
		//			System.out.println("Web Feature Serice");
		//			
		//		}
		//		
		//System.out.println("Full Request: "+request.getRequestURL());

	}
}