package de.ifgi.lod4wfs.core;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

public class GlobalSettings {

	public static int defaultPort = 8088;
	public static String defautlCRS = "EPSG:4326";
	//public static String defautlCRS = "http://www.opengis.net/def/crs/EPSG/0/4326";

	//Parliament
	//public static String default_SPARQLEndpoint= "http://recife:8088/parliament/sparql";
	
	//Fuseki
	//public static String SPARQL_Endpoint= "http://recife:3030/lod4wfs/query";
	
	//OWLIM
	public static String default_SPARQLEndpoint= "http://recife:8080/openrdf-sesame/repositories/lod4wfs";
	
	
	public static String defaultLowerCorner = "-180.0 -78.11";
	public static String defaultUpperCorner = "180.0 83.57";
	public static String defaultServiceName = "lod4wfs";

	public static String defaultLiteralType = "xsd:string";
	public static String xsdNameSpace = "http://www.w3.org/2001/XMLSchema#";
	public static String sfNameSpace = "http://www.opengis.net/ont/sf#";
	public static String dublinCoreNameSpace = "http://purl.org/dc/elements/1.1/";
	public static String geoSPARQLNameSpace = "http://www.opengis.net/ont/geosparql/1.0#";
	public static String RDFNameSpace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static String dublinCoreTermsNameSpace =  "http://purl.org/dc/terms/";
	public static String testVocabulary =  "http://test.vocabulary.com/";
	//TODO Fix bounding box in the Capabilities Document!
	//public static String BBOX ="LatLongBoundingBox maxx=\"-73.90782\" maxy=\"40.882078\" minx=\"-74.047185\" miny=\"40.679648\"";

	
	
	 private static String abstractPredicate ="";
	    private static String titlePredicate ="";
	    private static String keywordsPredicate ="";
	    private static String geometryPredicate ="";
	    private static String geometryClass ="";
	   
	    public static String getAbstractPredicate(){
	        return abstractPredicate;
	    }
	   
	    public static String getTitlePredicate(){
	        return titlePredicate;
	    }

	    public static String getKeywordsPredicate(){
	        return keywordsPredicate;
	    }
	   
	    public static String getGeometryPredicate(){
	        return geometryPredicate;
	    }
	   
	    public static String getGeometryClass(){
	        return geometryClass;
	    }
	   
	    public static void loadVariables(){
	       
	        Wini ini;
	        try {
	            ini = new Wini(new File("settings.jim"));

	            titlePredicate = ini.get("GetCapabilities", "title");           
	            abstractPredicate = ini.get("GetCapabilities", "abstract");           
	            keywordsPredicate = ini.get("GetCapabilities", "keywords");
	           
	            geometryPredicate = ini.get("Geometry", "geometryPredicate");
	            geometryClass = ini.get("Geometry", "geometryClass");
	           
	           
	        } catch (InvalidFileFormatException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	       
	       
	    }	
	
	

	public static String getCanonicalHostName(){
		
		String result = new String();
		
		try {
			
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();

			for (int networkInterfaceNumber = 0; en.hasMoreElements(); networkInterfaceNumber++) {
				
				NetworkInterface intf = en.nextElement();
				Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();

				for (int addressNumber = 0; enumIpAddr.hasMoreElements(); addressNumber++) {
					
					InetAddress ipAddr = enumIpAddr.nextElement();
					
					if(networkInterfaceNumber==2 && addressNumber==1){

						result = ipAddr.getCanonicalHostName();
						
					}
				}


			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
}
