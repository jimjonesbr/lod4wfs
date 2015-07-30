package de.ifgi.lod4wfs.factory;

import java.io.BufferedReader;
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
import java.util.Iterator;
import org.apache.log4j.Logger;
import com.google.gson.stream.JsonReader;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementVisitorBase;
import com.hp.hpl.jena.sparql.syntax.ElementWalker;
import de.ifgi.lod4wfs.core.Triple;
import de.ifgi.lod4wfs.core.WFSFeature;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.infrastructure.JenaConnector;

/**
 * @author Jim Jones
 */

public class FactoryFDAFeatures {

	private static Logger logger = Logger.getLogger("FDAFeatures-Factory");
	private static JenaConnector jn;
	private BufferedReader br;

	public FactoryFDAFeatures() {
		jn = new JenaConnector();

	}

	public ArrayList<WFSFeature> listFDAFeatures() {

		File[] files = new File(GlobalSettings.getFeatureDirectory()).listFiles();

		ArrayList<WFSFeature> result = new ArrayList<WFSFeature>();

		logger.info("Listing features from the direcoty " + System.getProperty("user.dir") + File.separator + GlobalSettings.getFeatureDirectory() + " ...");

		for (File file : files) {

			if(file.getName().endsWith(".sparql")){

				WFSFeature feature = new WFSFeature();

				feature = this.getFDAFeature(file.getName());

				if(feature != null){
					
					WFSFeature featureLog = new WFSFeature();
					featureLog = getFeatureLog(feature);
					
					feature.setSize(featureLog.getSize());
					feature.setGeometries(featureLog.getGeometries());
					feature.setLastAccess(featureLog.getLastAccess());
					feature.setGeometryType(featureLog.getGeometryType());
					
					feature.setAsFDAFeature(true);
					result.add(feature);
					
				}

			}

		}

		logger.info("Total FDA Features: " + result.size());

		return result;

	}

	
	private WFSFeature getFeatureLog(WFSFeature feature){

		WFSFeature result = new WFSFeature();

		String featureLogFile = "logs/features.log";
		String line = "";
		String splitBy = ";";

		try {
			br = new BufferedReader(new FileReader(featureLogFile));

			while ((line = br.readLine()) != null ) {

				String[] featureLogLine = line.split(splitBy);
				
				
				
				if(feature.getName().equals(featureLogLine[0])){
					
					if(!(featureLogLine.length < 5)){

						result.setLastAccess(featureLogLine[1]);
						result.setSize(Double.parseDouble(featureLogLine[2]));
						result.setGeometries(Long.parseLong(featureLogLine[3]));
						result.setGeometryType(featureLogLine[4]);
						
					} else {
						
						logger.error("Invalid entry at features.log");
						
					}
					

				}

			}

		} catch (FileNotFoundException e) {
			logger.error("The features.log file does not exist or is corrupted.");
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Error reading features.log");
			e.printStackTrace();
		}

		return result;
		
	}
	
	public static boolean existsFeature(String featureName){

		File[] files = new File(GlobalSettings.getFeatureDirectory()).listFiles();
		boolean result = false;

		for (File file : files) {

			if(file.getName().endsWith(".sparql")){

				try {

					FileReader fileReader = new FileReader(GlobalSettings.getFeatureDirectory() + file.getName());
					JsonReader jsonReader = new JsonReader(fileReader);
					jsonReader.setLenient(true);
					jsonReader.beginObject();

					while (jsonReader.hasNext()) {

						String record = jsonReader.nextName();

						if(record.equals("name")){

							if(jsonReader.nextString().equals(GlobalSettings.getFDAFeaturesNameSpace() + featureName)){

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

	//TODO: FDA existsFeature() to be implemented.
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

			logger.error("Invalid variable given.");

		}

		return result;
	}

	public static boolean isEndpointValid(String endpoint){

		boolean result = true;

		try {

			URL url = new URL(endpoint);
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();

			int responseCode = huc.getResponseCode();

			//TODO: Implement function to validate Endpoint
			if (responseCode == 404) {

				logger.error("URL cannot be resolved -> " + endpoint);

			}

		} catch (MalformedURLException e) {
			result = false;
			System.out.println("Malformed URL -> " + endpoint);
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
			Writer writer = new FileWriter(GlobalSettings.getFeatureDirectory() + feature.getName() + ".sparql");

			writer.write("{\n");
			writer.write("\"name\":\""+ GlobalSettings.getFDAFeaturesNameSpace() + feature.getName().toLowerCase() + "\",\n");
			writer.write("\"title\":\"" + feature.getTitle() + "\",\n");			
			writer.write("\"abstract\":\"" + feature.getFeatureAbstract() + "\",\n");
			writer.write("\"keywords\":\"" + feature.getKeywords() + "\",\n");
			writer.write("\"geometryVariable\":\"" + feature.getGeometryVariable() + "\",\n");
			writer.write("\"endpoint\":\"" + feature.getEndpoint() + "\",\n");
			writer.write("\"crs\":\"" + feature.getCRS() + "\",\n");
			
			if (feature.isEnabled()) {
				writer.write("\"enabled\":\"true\",\n");
			} else {
				writer.write("\"enabled\":\"false\",\n");
			}
			
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

	public WFSFeature getFDAFeature(String fileName){

		File[] files = new File(GlobalSettings.getFeatureDirectory()).listFiles();
		WFSFeature feature = new WFSFeature();

		for (File file : files) {

			if(file.getName().endsWith(".sparql")){

				try {

					FileReader fileReader = new FileReader(GlobalSettings.getFeatureDirectory() + file.getName());
					JsonReader jsonReader = new JsonReader(fileReader);
					jsonReader.setLenient(true);
					jsonReader.beginObject();

					if(file.getName().endsWith(fileName)){

						while (jsonReader.hasNext()) {

							while (jsonReader.hasNext()) {

								String name = jsonReader.nextName();

								if (name.equals("abstract")) {

									feature.setFeatureAbstract(jsonReader.nextString());

								} else if (name.equals("title")) {

									feature.setTitle(jsonReader.nextString());

								} else if (name.equals("name")) {

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

								} else if (name.equals("crs")) {

									feature.setCRS(jsonReader.nextString());

								} else if (name.equals("enabled")) {

									if(jsonReader.nextString().endsWith("false")){
										feature.setEnabled(false);
									} else {
										feature.setEnabled(true);
									}									

								}

								feature.setLowerCorner(GlobalSettings.getDefaultLowerCorner());
								feature.setUpperCorner(GlobalSettings.getDefaultUpperCorner());
								feature.setAsFDAFeature(true);
								feature.setFileName(file.getName());

							}

						}

						jsonReader.endObject();
						jsonReader.close();

					} 


				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					logger.error("Error parsing " + file.getName() + ".");
					logger.error(e.toString());
					e.printStackTrace();

				}


			}
		}


		return feature;
	}

	/**
	 * @param WFS feature 
	 * @return Lists all predicates (properties) related to a given feature.  
	 */

	public ArrayList<Triple> getPredicatesFDAFeatures(WFSFeature feature){

		logger.info("Listing available predicates for the FDA feature " + feature.getName() + " ...");

		ArrayList<Triple> result = new ArrayList<Triple>();		
		Query query = QueryFactory.create(feature.getQuery());

		for (int i = 0; i < query.getResultVars().size(); i++) {	

			Triple triple = new Triple();
			triple.setObjectDataType(GlobalSettings.getDefaultLiteralType());
			triple.setPredicate(query.getResultVars().get(i).toString());
			result.add(triple);

		}

		return result;

	}

	public String getGeometryPredicate(String SPARQLQuery){

		Query query = QueryFactory.create(SPARQLQuery);
		String geometryPredicate = new String();

		final ArrayList<com.hp.hpl.jena.graph.Triple> triplesList = new ArrayList<com.hp.hpl.jena.graph.Triple>();

		// This will walk through all parts of the query
		ElementWalker.walk(query.getQueryPattern(),
				// For each element...
				new ElementVisitorBase() {
			// ...when it's a block of triples...
			public void visit(ElementPathBlock el) {
				// ...go through all the triples...
				Iterator<TriplePath> triples = el.patternElts();

				while (triples.hasNext()) {
					// ...and grab the subject

					triplesList.add(triples.next().asTriple());

				}


			}
		}
				);



		for (int i = 0; i < triplesList.size(); i++) {

			if(triplesList.get(i).getObject().toString().equals("?wkt")){

				geometryPredicate = triplesList.get(i).getPredicate().toString();
			}


		}

		return geometryPredicate;

	}

	public void enableFeature(boolean enable){
		
		
	}
}
