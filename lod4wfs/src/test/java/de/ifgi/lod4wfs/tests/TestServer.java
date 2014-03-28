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

import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.core.WFSFeature;
import de.ifgi.lod4wfs.facade.Facade;
import de.ifgi.lod4wfs.factory.FactoryFDAFeatures;


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

		
		System.out.println(FactoryFDAFeatures.isFeatureNameValid("heiß"));

	

		
	}
}
