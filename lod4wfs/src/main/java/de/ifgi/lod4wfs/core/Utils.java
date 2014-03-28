package de.ifgi.lod4wfs.core;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import it.cutruzzula.lwkt.WKTParser;

public class Utils {

	public static String convertLiteraltoGML(String literal){
		
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
