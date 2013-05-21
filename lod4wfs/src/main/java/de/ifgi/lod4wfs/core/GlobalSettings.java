package de.ifgi.lod4wfs.core;

public class GlobalSettings {

	public static int defaultPort = 8081;
	public static String defautlCRS = "EPSG:4326";
	public static String SPARQL_Endpoint= "http://recife:8081/parliament/sparql";
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

	//TODO Fix bounding box in the Capabilities Document!
	public static String BBOX ="LatLongBoundingBox maxx=\"-73.90782\" maxy=\"40.882078\" minx=\"-74.047185\" miny=\"40.679648\"";
	//LatLongBoundingBox maxx="-73.90782" maxy="40.882078" minx="-74.047185" miny="40.679648"
	
		
}
