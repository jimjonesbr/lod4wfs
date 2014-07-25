package de.ifgi.lod4wfs.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

		StringBuilder geoJSONStringBuilder = new StringBuilder();
		geoJSONStringBuilder.append(wkt.replace("(", "[").replace(")", "]").replace(", ",","));
		String geoType = geoJSONStringBuilder.substring(0, geoJSONStringBuilder.indexOf("[")).trim();
		geoJSONStringBuilder.delete(geoJSONStringBuilder.indexOf("[")-1, geoJSONStringBuilder.indexOf("["));

		boolean flagNumber = false;
		StringBuilder geoJSONOutuput = new StringBuilder();


		for (int i = 0; i < geoJSONStringBuilder.length(); i++) {

			if(geoJSONStringBuilder.charAt(i)=='[' ||
					geoJSONStringBuilder.charAt(i)==']' ||	
					geoJSONStringBuilder.charAt(i)=='.' ||
					geoJSONStringBuilder.charAt(i)=='-'){


				geoJSONOutuput.append(geoJSONStringBuilder.charAt(i));

			} else {

				if(Character.isDigit(geoJSONStringBuilder.charAt(i)) && flagNumber==false){

					if (!geoType.toUpperCase().equals("POINT")){

						//Checking if the coordinate has negative values.
						if(geoJSONStringBuilder.charAt(i-1)=='-'){
							//Closes the pair of coordinates with a squared bracket '['
							geoJSONOutuput.insert(geoJSONOutuput.length()-1, '[');

						} else {

							geoJSONOutuput.append("[");

						}
					}

					flagNumber = true;

					geoJSONOutuput.append(geoJSONStringBuilder.charAt(i));

				} else

					if(Character.isDigit(geoJSONStringBuilder.charAt(i)) && flagNumber==true){

						geoJSONOutuput.append(geoJSONStringBuilder.charAt(i));

					}

				if(geoJSONStringBuilder.charAt(i)==' '){

					geoJSONOutuput.append(",");

				}

				if(geoJSONStringBuilder.charAt(i)==','){

					geoJSONOutuput.append("],[");

				}
			}
		}

		if (!geoType.toUpperCase().equals("POINT")){

			geoJSONOutuput.append("]");
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

		return "{\"type\":\""+ geoType + "\",\"coordinates\":" +geoJSONOutuput+"},";


	}

	//TODO: to be implemented
	public static boolean isGML(String literal){

		return true;

	}

	//TODO: to be implemented
	public static boolean isWKT(String literal){

		return true;

	}

	//TODO: to be implemented
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

	public static File compressFile(String fileContent, String fileName) throws IOException{

		Path tempFile;


		tempFile = Files.createTempFile(null, ".tmp");

		//System.out.format("The temporary file has been created: %s%n", tempFile);

		File file = new File(tempFile.toString());
		FileWriter fw = new FileWriter(file);

		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(fileContent);
		bw.close();

		byte[] buffer = new byte[1024];

		File zipfile = new File(Files.createTempFile(null, ".zip").toString());
		
		FileOutputStream fos = new FileOutputStream(zipfile);
		ZipOutputStream zos = new ZipOutputStream(fos);

		ZipEntry ze= new ZipEntry(fileName);
		zos.putNextEntry(ze);
		FileInputStream in = new FileInputStream(file);

		int len;

		while ((len = in.read(buffer)) > 0) {
			zos.write(buffer, 0, len);
		}

		in.close();
		zos.closeEntry();
		zos.close();

		return zipfile;


	}
}
