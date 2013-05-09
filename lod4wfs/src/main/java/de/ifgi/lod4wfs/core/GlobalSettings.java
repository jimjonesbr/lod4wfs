package de.ifgi.lod4wfs.core;

public class GlobalSettings {

	public static int defaultPort = 8080;
	public static String defautlCRS = "EPSG:4326";
	public static String SPARQL_Endpoint= "http://recife:8081/parliament/sparql";
	public static String defaultLowerCorner = "-180.0 -78.11";
	public static String defaultUpperCorner = "180.0 83.57";
	public static String defaultServiceName = "lod4wfs";
	
	public static String defaultDataType = "xsd:string";
	public static String xsdNameSpace = "http://www.w3.org/2001/XMLSchema#";
	public static String sfNameSpace = "http://www.opengis.net/ont/sf#";
	
	public static String BBOX ="LatLongBoundingBox maxx=\"-73.90782\" maxy=\"40.882078\" minx=\"-74.047185\" miny=\"40.679648\"";
	//LatLongBoundingBox maxx="-73.90782" maxy="40.882078" minx="-74.047185" miny="40.679648"
	
		
}
