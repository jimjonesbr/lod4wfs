package de.ifgi.lod4wfs.factory;

import it.cutruzzula.lwkt.WKTParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.google.gson.stream.JsonReader;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import de.ifgi.lod4wfs.core.WFSFeature;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.infrastructure.JenaConnector;

public class FactorySPARQLFeatures {

	private static Logger logger = Logger.getLogger("SPARQLFeatures-Factory");
	private static JenaConnector jn;
	
	public FactorySPARQLFeatures() {
		jn = new JenaConnector();
		
	}

	public ArrayList<WFSFeature> listSPARQLFeatures(String path) {

		File[] files = new File(path).listFiles();

		ArrayList<WFSFeature> result = new ArrayList<WFSFeature>();

		for (File file : files) {
			//logger.info("Listing geographic layers at " + GlobalSettings.default_SPARQLEndpoint + " ...");

			if(file.getName().endsWith(".sparql")){
				//System.out.println("File: " + path + file.getName());

				WFSFeature feature = new WFSFeature();
				
				feature = this.getSPARQLFeature(file.getName());
				
				if(feature != null){
					result.add(feature);
				}
				


			}

		}

		return result;
	}

	public static boolean existsFeature(String featureName){

		File[] files = new File(GlobalSettings.getSparqlDirectory()).listFiles();
		boolean result = false;
		
		for (File file : files) {

			if(file.getName().endsWith(".sparql")){

				try {

					FileReader fileReader = new FileReader(GlobalSettings.getSparqlDirectory()+file.getName());
					JsonReader jsonReader = new JsonReader(fileReader);
					jsonReader.beginObject();
						
					//System.out.println(file.getName());

					while (jsonReader.hasNext()) {

						String record = jsonReader.nextName();
						
						if(record.equals("name")){
							
							if(jsonReader.nextString().equals(GlobalSettings.getDynamicFeaturesNameSpace() + featureName)){
								
								result = true;
								
							}
							
						} else {
							
							jsonReader.nextString();
							
						}
					}

					jsonReader.endObject();
					jsonReader.close();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}


			}
		}

		return result;

	}

	public static void updateFeature(WFSFeature feature){


		
	}

	public static void deleteFeature(WFSFeature feature){

		File file = new File(feature.getFileName());
		
		file.delete();
				
	}

	public static boolean isQueryValid(String query){

		boolean result;

		try {
			QueryFactory.create(query);
			result = true;
		} catch (Exception e) {
			result = false;
		}


		return result;

	}

	public static boolean isVariableValid(WFSFeature feature){

		boolean result = false;

		try {
			Query query = QueryFactory.create(feature.getQuery());				

			for (int i = 0; i < query.getResultVars().size(); i++) {

				if (query.getResultVars().get(i).equals(feature.getGeometryVariable().replace("?", ""))){
					result = true;
				}

			}
		} catch (Exception e) {
			System.out.println("Invalid variable given.");
		}

		return result;
	}

	public static boolean isEndpointValid(String endpoint){

		boolean result = true;

		try {

			URL url = new URL(endpoint);
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			
			int responseCode = huc.getResponseCode();
			
			if (responseCode == 404) {
				System.out.println("URL canno be resolved -> " + endpoint);
				result = false;
			}
			
		} catch (MalformedURLException e) {
			result = false;
			System.out.println("Malformed URL. " + endpoint);
		} catch (IOException e) {
			e.printStackTrace();
		} 

		return result;

	}
	
	public static boolean isFeatureNameValid(String featureName){
		
		return featureName.matches("([A-Za-z0-9-_]+)");

	}

	public static void addFeature(WFSFeature feature){

		try {
			Writer writer = new FileWriter(GlobalSettings.getSparqlDirectory() + feature.getName() + ".sparql");

			writer.write("{\n");
			writer.write("\"name\":\""+ GlobalSettings.getDynamicFeaturesNameSpace() + feature.getName().toLowerCase() + "\",\n");
			writer.write("\"title\":\"" + feature.getTitle() + "\",\n");			
			writer.write("\"abstract\":\"" + feature.getFeatureAbstract() + "\",\n");
			writer.write("\"keywords\":\"" + feature.getKeywords() + "\",\n");
			writer.write("\"geometryVariable\":\"" + feature.getGeometryVariable() + "\",\n");
			writer.write("\"endpoint\":\"" + feature.getEndpoint() + "\",\n");
			writer.write("\"query\":\"" + feature.getQuery().replace("\"", "'") + "\"");
			writer.write("\n}");
			writer.close();


		} catch (IOException e) {
			e.printStackTrace();
		}



	}

	public ResultSet executeQuery(String SPARQL, String endpoint){
		
		return jn.executeQuery(SPARQL, endpoint);
		
	}

	public WFSFeature getSPARQLFeature(String fileName){
		
		File[] files = new File(GlobalSettings.getSparqlDirectory()).listFiles();
		WFSFeature feature = new WFSFeature();
		
		for (File file : files) {

			if(file.getName().endsWith(".sparql")){

				try {

					FileReader fileReader = new FileReader(GlobalSettings.getSparqlDirectory()+file.getName());
					JsonReader jsonReader = new JsonReader(fileReader);
					jsonReader.beginObject();
															
					if(file.getName().endsWith(fileName)){
						
						while (jsonReader.hasNext()) {
							
							
							while (jsonReader.hasNext()) {

								String name = jsonReader.nextName();

								if (name.equals("abstract")) {

									feature.setFeatureAbstract(jsonReader.nextString());

								} else if (name.equals("title")) {

									feature.setTitle(jsonReader.nextString());//

								} else if (name.equals("name")) {

									//feature.setName(jsonReader.nextString().replace(GlobalSettings.getDynamicFeaturesNameSpace(), ""));
									feature.setName(jsonReader.nextString());

								} else if (name.equals("query")) {

									String tmpQuery = jsonReader.nextString();

									try {
										Query query = QueryFactory.create(tmpQuery);
										feature.setQuery(query.toString());

									} catch (Exception e) {
										logger.error("Invalid SPARQL Query at " + file.getName() + ". The correspondent layer won't be listed in the Capabilities Document.");
										logger.error(tmpQuery);
									}


								} else if (name.equals("keywords")) {

									feature.setKeywords(jsonReader.nextString());

								} else if (name.equals("geometryVariable")) {

									feature.setGeometryVariable(jsonReader.nextString().replace("?", ""));

								} else if (name.equals("endpoint")) {

									feature.setEndpoint(jsonReader.nextString());

								}

								feature.setLowerCorner(GlobalSettings.defaultLowerCorner);
								feature.setUpperCorner(GlobalSettings.defaultUpperCorner);
								feature.setDefaultCRS(GlobalSettings.defautlCRS);
								feature.setDynamic(true);
								feature.setFileName(file.getName());

							}
							
						}

						jsonReader.endObject();
						jsonReader.close();
						
					} 

					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					logger.error("Error when parsing " + file.getName() + ".");
					logger.error(e.toString());
					e.printStackTrace();

				}


			}
		}

		
		return feature;
	}

	private static boolean isGeometryValid(String wkt){
		
		boolean result = true;
		
		try {
			WKTParser.parseToGML2(wkt);
			}
		catch(Exception e) {
			result = false;
		}
				
		return result;
		
	}
	
	public static String getGeometryType(String wkt){
		
		String result = new String();
		
		if(isGeometryValid(wkt.toUpperCase())){
			
			try {
				result = WKTParser.parse(wkt.toUpperCase()).getType().toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {
			result = "INVALID";
		}
		
		return result;
	}

}
