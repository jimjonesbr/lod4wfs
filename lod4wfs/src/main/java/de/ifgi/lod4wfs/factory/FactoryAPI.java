package de.ifgi.lod4wfs.factory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.core.WFSFeature;

public class FactoryAPI {

	private static Logger logger = Logger.getLogger("API-Factory");
	
	public static String getSystemInfo() {
		
		StringWriter strWriter = new StringWriter();
		JsonWriter writer = new JsonWriter(strWriter);
		String result = "";
		
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
			result = gson.toJson(je);
			
		} catch (IOException e) {

			e.printStackTrace();
		} 
		
		return result;
		
	}
	
	public FactoryAPI() {
		
		super();
		
	}

	public static String listFeatures(){

		ArrayList<WFSFeature> features = FactoryWFS.getInstance().getFeatureList();

		JSONArray list = new JSONArray();
		String result ="";
		
		for (int i = 0; i < features.size(); i++) {
			
			JSONObject obj = new JSONObject();
			obj.put("title", features.get(i).getTitle());
			obj.put("name", features.get(i).getName());
			obj.put("abstract", features.get(i).getFeatureAbstract());
			obj.put("keywords", features.get(i).getKeywords());
			obj.put("geometryVariable", features.get(i).getGeometryVariable());
			obj.put("crs", features.get(i).getCRS());
			obj.put("endpoint", features.get(i).getEndpoint());
			obj.put("enabled", features.get(i).isEnabled());
			obj.put("query", features.get(i).getQuery());			

			if(features.get(i).isSOLRFeature()) { obj.put("type", "solr");} else
			if(features.get(i).isFDAFeature()) { obj.put("type", "fda");} else
			if(features.get(i).isSDAFeature()) { obj.put("type", "sda");} 
						
			list.put(obj);
			
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			Object json = mapper.readValue(list.toString(), Object.class);
			result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json).replace("\n", GlobalSettings.getCrLf());
			logger.info("Feature list created.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
					
		return result;
	}

	public static String raiseException(String message){
		
		return "Error processing the request:\n"+message;
		
	}

}


