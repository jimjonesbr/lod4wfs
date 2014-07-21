package de.ifgi.lod4wfs.core;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import it.cutruzzula.lwkt.WKTParser;

public class Utils {

	public static String convertWKTtoGML(String literal){
		
		String gml = new String();
		
		if(isWKT(literal)){
			
			try {
			
				if(literal.contains("<") && literal.contains(">")){
					String CRS = new String();
				
					
					//Extracting Reference System
					if(literal.contains("<") && literal.contains(">")){
						
						CRS = literal.substring(literal.indexOf("<") + 1, literal.indexOf(">"));
						literal = literal.substring(literal.indexOf(">") + 1, literal.length());
						
					}
					
					//Removing Literal Type
					if(literal.contains("^^")){
						
						literal = literal.substring(0, literal.indexOf("^^"));
						
					}
									
					gml = WKTParser.parseToGML2(literal,CRS);
					
					
				} else {
				
					gml = WKTParser.parseToGML2(literal,GlobalSettings.defautlCRS);
				
				}
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return gml;
		
	}

	public static String convertWKTtoGeoJSON(String wkt){
		
		
		
		if(wkt.contains("<") && wkt.contains(">")){
			String CRS = new String();
		
			
			//Extracting Reference System
			if(wkt.contains("<") && wkt.contains(">")){
				
				CRS = wkt.substring(wkt.indexOf("<") + 1, wkt.indexOf(">"));
				wkt = wkt.substring(wkt.indexOf(">") + 1, wkt.length());
				
			}
			
			//Removing Literal Type
			if(wkt.contains("^^")){
				
				wkt = wkt.substring(0, wkt.indexOf("^^"));
				
			}
							
		}
		
		
		//String geojson = wkt.replace("(", "[").replace(")", "]").replace(", ",","); 
		StringBuilder geojson2 = new StringBuilder();
		geojson2.append(wkt.replace("(", "[").replace(")", "]").replace(", ",","));
		//String geoType = geojson.substring(0, geojson.indexOf("[")).trim();
		String geoType = geojson2.substring(0, geojson2.indexOf("[")).trim();

		//geojson = geojson.substring(geojson.indexOf("["),geojson.length()).trim();
		//geojson2.append(geojson2.substring(geojson2.indexOf("["),geojson2.length()).trim());
		geojson2.delete(geojson2.indexOf("[")-1, geojson2.indexOf("["));
		boolean flagNumber = false;

		//String res = new String();
		StringBuilder res = new StringBuilder();

		//for (int i = 0; i < geojson.length(); i++) {
		for (int i = 0; i < geojson2.length(); i++) {
			
			if(geojson2.charAt(i)=='[' || 
					geojson2.charAt(i)==']' ||	
					geojson2.charAt(i)=='.'){

				//res = res + geojson2.charAt(i);
				res.append(geojson2.charAt(i));

			}  

			if(Character.isDigit(geojson2.charAt(i)) && flagNumber==false){
				if (!geoType.toUpperCase().equals("POINT")){
					//res = res + "[";
					res.append("[");
				}
				flagNumber = true;
				//res = res + geojson2.charAt(i);
				res.append(geojson2.charAt(i));
			} else 

				if(Character.isDigit(geojson2.charAt(i)) && flagNumber==true){
					//res = res + geojson2.charAt(i);
					res.append(geojson2.charAt(i));
				}

			if(geojson2.charAt(i)==' '){
				//res = res + ",";
				res.append(",");

			} 

			if(geojson2.charAt(i)==','){
				//res = res + "],[";
				res.append("],[");
			}

		}

		if (!geoType.toUpperCase().equals("POINT")){
			//res = res + "]";
			res.append("]");
		}

		if (geoType.toUpperCase().equals("POINT")){
			geoType = "Point";
		} else if (geoType.toUpperCase().equals("MULTIPOLYGON")){
			geoType = "MultiPolygon";
		} else if (geoType.toUpperCase().equals("POLYGON")){
			geoType = "Polygon";
		} else if (geoType.toUpperCase().equals("MULTIPOINT")){
			geoType = "MultiPoint";
		} else if (geoType.toUpperCase().equals("LINESTRING")){
			geoType = "LineString";
		} else if (geoType.toUpperCase().equals("MULTILINESTRING")){
			geoType = "MultiLineString";
		} else if (geoType.toUpperCase().equals("GEOMETRYCOLLECTION")){
			geoType = "GeometryCollection";
		}
		
//		geojson = "{\"type\":\""+ geoType + "\",\"coordinates\":" +res+"},";


		return  "{\"type\":\""+ geoType + "\",\"coordinates\":" +res+"},";

	
	}
	public static boolean isGML(String literal){
		
		return true;
		
	}

	public static boolean isWKT(String literal){
		
		return true;
		
	}

	public static boolean isGeoJSON(String literal){
		
		return true;
		
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
