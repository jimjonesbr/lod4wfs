package de.ifgi.lod4wfs.core;

import java.io.File;
import java.io.IOException;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

/**
 * @author Jim Jones
 */

public class GlobalSettings {

	private static int defaultPort;
	private static String defaultCRS = "";
	private static String defaultSPARQLEndpoint= "";

	public static String defaultLowerCorner = "-180.0 -78.11";
	public static String defaultUpperCorner = "180.0 83.57";	
	public static String defaultLiteralType = "xsd:string";	
	public static String defaultDecimalType = "";
	public static String defaultIntegerType = "";
	public static String defaultStringType = "";
	public static String defaultLongType = "";
	public static String defaultDateType = "";
	public static String defaultWKTType = "";
	public static String defaultByteType = "";
	public static String defaultFloatType = "";
	public static String xsdNameSpace = "";
	public static String startupTime = "";
	//TODO Fix bounding box in the Capabilities Document!
	
	private static String defaultServiceName = "";
	private static String crlf = System.getProperty("line.separator");
 	private static String abstractPredicate ="";
    private static String titlePredicate ="";
    private static String keywordsPredicate ="";
    private static String geometryPredicate ="";
    private static String geometryClass ="";
    private static String geometryVariable ="";
    private static String featureDirectory ="";
    private static String fdaFeaturesNameSpace = "";
    private static String solrFeaturesNameSpace = "";
    private static String predicatesContainer = "";
    private static String featureConnector= "";
    private static String sdaPrefix= "";
    private static String fdaPrefix= "";
    private static String solrPrefix= "";    
    private static boolean sdaEnable = false;
    private static boolean fdaEnable = false;
    private static boolean solrEnable = false;
    
    
    
    public static String getDefaultServiceName() {
		return defaultServiceName;
	}


	public static String getCrlf() {
		return crlf;
	}


	public static String getDefaultSPARQLEndpoint() {
		return defaultSPARQLEndpoint;
	}
    
    
	public static void setDefaultSPARQLEndpoint(String defaultSPARQLEndpoint) {
		GlobalSettings.defaultSPARQLEndpoint = defaultSPARQLEndpoint;
	}

	public static String getDefaultCRS(){
    	return defaultCRS;
    }

    public static int getDefaultPort(){
    	return defaultPort;
    }
            
    public static void setDefaultPort(int defaultPort) {
		GlobalSettings.defaultPort = defaultPort;
	}

	public static boolean isSdaEnable() {
		return sdaEnable;
	}

	public static boolean isFdaEnable() {
		return fdaEnable;
	}

	public static boolean isSolrEnable() {
		return solrEnable;
	}

	private static int previewLimit = 5;
    
    public static String getFDAFeaturesNameSpace(){
    	return fdaFeaturesNameSpace;
    }

    public static String getSOLRFeaturesNameSpace(){
    	return solrFeaturesNameSpace;
    }
    
    public static String getFeatureDirectory(){
        return featureDirectory;
    }
    
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

    public static String getXsdNameSpace(){
        return xsdNameSpace;
    }
    
    public static String getGeometryVariable(){
        return geometryVariable;
    }

    public static int getPreviewLimit(){
        return previewLimit;
    }

    public static String getPredicatesContainer(){
        return predicatesContainer;
    }

    public static String getFeatureConnector(){
        return featureConnector;
    }
    
    public static String getSDAPrefix(){
        return sdaPrefix;
    }

    public static String getFDAPrefix(){
        return fdaPrefix;
    }

    public static String getSOLRPrefix(){
        return solrPrefix;
    }
    
    public static String getDefaultDecimalType(){
        return defaultDecimalType;
    }

    public static String getDefaultStringType(){
        return defaultStringType;
    }
    
    public static String getDefaultIntegerType(){
        return defaultIntegerType;
    }

    public static String getDefaultDateType(){
        return defaultDateType;
    }
    
    public static String getDefaultLongType(){
        return defaultLongType;
    }

    public static String getDefaultWKTType(){
        return defaultWKTType;
    }
    
    public static String getDefaultByteType(){
        return defaultByteType;
    }
    
    public static String getDefaultFloatType(){
        return defaultFloatType;
    }
    
    public static void refreshSystemVariables(){
	       
	        Wini ini;
	        
	        try {
	        	
	            ini = new Wini(new File("settings/settings.jim"));

	            titlePredicate = ini.get("GetCapabilities", "title");           
	            abstractPredicate = ini.get("GetCapabilities", "abstract");           
	            keywordsPredicate = ini.get("GetCapabilities", "keywords");
	           
	            geometryPredicate = ini.get("Geometry", "geometryPredicate");
	            geometryClass = ini.get("Geometry", "geometryClass");
	            geometryVariable = ini.get("Geometry", "geometryVariable");
	            predicatesContainer = ini.get("Geometry", "predicatesContainer");
	            featureConnector = ini.get("Geometry", "featureConnector");
	            featureConnector = ini.get("Geometry", "wktLiteral");
	            
	            xsdNameSpace = ini.get("SystemDefaults", "xsdNameSpace");
	            fdaPrefix = ini.get("SystemDefaults", "fdaPrefix");
	            sdaPrefix = ini.get("SystemDefaults", "sdaPrefix");
	            solrPrefix = ini.get("SystemDefaults", "solrPrefix");
	            defaultCRS = ini.get("SystemDefaults", "defaultCRS").trim();
	            defaultServiceName = ini.get("SystemDefaults", "serviceName").trim();

	            fdaFeaturesNameSpace = ini.get("SystemDefaults", "fdaFeaturesNameSpace");
	            solrFeaturesNameSpace = ini.get("SystemDefaults", "solrFeaturesNameSpace");
	            defaultSPARQLEndpoint = ini.get("Server", "SPARQLEndpointURL");
	            featureDirectory = ini.get("Server", "SPARQLDirectory");
	            defaultPort = Integer.valueOf(ini.get("Server", "defaultPort"));
	            defaultDecimalType  = ini.get("SystemDefaults", "decimalLiteral").replace("<", "").replace(">", "");
	            defaultStringType  = ini.get("SystemDefaults", "stringLiteral").replace("<", "").replace(">", "");
	            defaultIntegerType  = ini.get("SystemDefaults", "integerLiteral").replace("<", "").replace(">", "");
	            defaultLongType  = ini.get("SystemDefaults", "longLiteral").replace("<", "").replace(">", "");
	            defaultFloatType  = ini.get("SystemDefaults", "floatLiteral").replace("<", "").replace(">", "");
	            
	            if(ini.get("SystemDefaults", "fdaEnabled").trim().equals("true")){
	            	
	            	fdaEnable = true;
	            	
	            }

	            if(ini.get("SystemDefaults", "sdaEnabled").trim().equals("true")){
	            	
	            	sdaEnable = true;
	            	
	            }
	            
	            if(ini.get("SystemDefaults", "solrEnabled").trim().equals("true")){
	            	
	            	solrEnable = true;
	            	
	            }
	            
	            previewLimit = Integer.valueOf(ini.get("WebInterface", "PreviewLimit"));
	            
	            
	            
	        } catch (InvalidFileFormatException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	       
	       
	    }	

	
}
