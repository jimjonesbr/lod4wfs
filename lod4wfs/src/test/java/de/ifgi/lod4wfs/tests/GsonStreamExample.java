package de.ifgi.lod4wfs.tests;


import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.jena.base.Sys;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.google.inject.spi.Message;

import de.ifgi.lod4wfs.core.GlobalSettings;



public class GsonStreamExample {
	public static void main(String[] args) {

		JsonWriter writer;
		StringWriter strWriter = new StringWriter();

		writer = new JsonWriter(strWriter);

		try {
			writer.beginObject();
			writer.name("startupTime").value(GlobalSettings.getStartupTime());			
			writer.name("version").value(GlobalSettings.getAppVersion());
			writer.name("java").value(System.getProperty("java.version")); 
			writer.name("os").value(System.getProperty("os.name").toString() + " " + 
					   System.getProperty("os.version").toString() + " (" + 
					   System.getProperty("os.arch").toString()+")");
			
			writer.name("port").value(GlobalSettings.getDefaultPort());
			writer.name("endpoint").value(GlobalSettings.getDefaultSPARQLEndpoint());
			writer.name("timeout").value(GlobalSettings.getConnectionTimeOut());
			writer.name("fda").value(GlobalSettings.isFdaEnabled());
			writer.name("sda").value(GlobalSettings.isSdaEnabled());
			writer.name("solr").value(GlobalSettings.isSolrEnabled());
			writer.name("featureDirectory").value(GlobalSettings.getFeatureDirectory());
			writer.name("limit").value(GlobalSettings.getPreviewLimit());
			
			writer.endObject(); 
			writer.close();
		
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(strWriter.toString());
			String prettyJsonString = gson.toJson(je);


			System.out.println(strWriter.toString());
			System.out.println(prettyJsonString);
			
		} catch (IOException e) {

			e.printStackTrace();
		} 


	}

}