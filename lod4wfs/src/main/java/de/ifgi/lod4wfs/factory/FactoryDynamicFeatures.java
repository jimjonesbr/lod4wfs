package de.ifgi.lod4wfs.factory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.google.gson.stream.JsonReader;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import de.ifgi.lod4wfs.core.WFSFeature;
import de.ifgi.lod4wfs.core.GlobalSettings;

public class FactoryDynamicFeatures {

	private static Logger logger = Logger.getLogger("DynamicFeatures-Factory");

	public FactoryDynamicFeatures() {
		super();
	}


	public static ArrayList<WFSFeature> listDynamicFeatures(String path) {

		File[] files = new File(path).listFiles();
		boolean isValidQuery = false;

		ArrayList<WFSFeature> result = new ArrayList<WFSFeature>();

		for (File file : files) {
			//logger.info("Listing geographic layers at " + GlobalSettings.default_SPARQLEndpoint + " ...");

			if(file.getName().endsWith(".sparql")){
				//System.out.println("File: " + path + file.getName());

				WFSFeature feature = new WFSFeature();

				try {

					FileReader fileReader = new FileReader(path+file.getName());
					JsonReader jsonReader = new JsonReader(fileReader);
					jsonReader.beginObject();


					while (jsonReader.hasNext()) {

						String name = jsonReader.nextName();

						if (name.equals("abstract")) {

							feature.setFeatureAbstract(jsonReader.nextString());

						} else if (name.equals("title")) {

							feature.setTitle(jsonReader.nextString());//

						} else if (name.equals("name")) {

							feature.setName(jsonReader.nextString());

						} else if (name.equals("query")) {

							String tmpQuery = jsonReader.nextString();

							try {
								Query query = QueryFactory.create(tmpQuery);
								feature.setQuery(query.toString());
								isValidQuery = true;
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


					if(isValidQuery){
						result.add(feature);
					}

					jsonReader.endObject();
					jsonReader.close();

				} catch (FileNotFoundException e) {
					e.printStackTrace();

				} catch (IOException e) {
					logger.error("Error when parsing " + file.getName() + ".");
					logger.error(e.toString());
					e.printStackTrace();

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

			System.out.println(query.toString());

			for (int i = 0; i < query.getResultVars().size(); i++) {

				if (query.getResultVars().get(i).equals(feature.getGeometryVariable().replace("?", ""))){
					result = true;
				}

				System.out.println(query.getResultVars().get(i));

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

		} catch (MalformedURLException e) {
			result = false;
			System.out.println("Invalid URL.");
		}

		return result;

	}

	public static boolean isFeatureNameValid(String featureName){

		return featureName.matches("([A-Za-z0-9-_]+)");

	}

	public static void addFeature(WFSFeature feature){

		try {
			Writer writer = new FileWriter(GlobalSettings.getSparqlDirectory() + feature.getName() + ".sparql");

			//String json = gson.toJson(feature); 		

			//writer.write(json);

			//gson.toJson("query:"+feature.getQuery().toString(), writer);
			//gson.toJson(feature.getQuery(), String.class, writer);
			
			System.out.println("\"name\":\"http://sparql.lod4wfs.de/" + feature.getName().toLowerCase() + "\",\n");
			writer.write("{\n");
			writer.write("\"name\":\"http://sparql.lod4wfs.de/" + feature.getName() + "\",\n");
			writer.write("\"title\":\"" + feature.getTitle() + "\",\n");			
			writer.write("\"abstract\":\"" + feature.getFeatureAbstract() + "\",\n");
			writer.write("\"keywords\":\"" + feature.getKeywords() + "\",\n");
			writer.write("\"geometryVariable\":\"" + feature.getGeometryVariable() + "\",\n");
			writer.write("\"endpoint\":\"" + feature.getEndpoint() + "\",\n");
			writer.write("\"query\":\"" + feature.getQuery().replace("\"", "'") + "\"");
			writer.write("\n}");
			writer.close();

			//System.out.println("JSON Created -> " + json);

		} catch (IOException e) {
			e.printStackTrace();
		}



	}

}
