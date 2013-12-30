package de.ifgi.lod4wfs.tests;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.ifgi.lod4wfs.core.WFSFeature;
import de.ifgi.lod4wfs.facade.Facade;


public class TestServer {

	class BagOfPrimitives {
		  private int value1 = 1;
		  private String value2 = "abc";
		  private transient int value3 = 3;
		  BagOfPrimitives() {
		    // no-args constructor
		  }
		}
	
	public static void main(String[] args) 
	{

		
	
		
		try {	
			URL url = new URL("http://www.dbpedia.org/sparql");
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			
			int responseCode = huc.getResponseCode();
			
			if (responseCode != 404) {
				System.out.println("GOOD");
			} else {
				System.out.println("BAD");
			}
		} catch (IOException e) {
		
			e.printStackTrace();
		}

	

		
	}
}
