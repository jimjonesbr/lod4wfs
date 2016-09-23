package de.ifgi.lod4wfs.factory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;
import de.ifgi.lod4wfs.core.Triple;
import de.ifgi.lod4wfs.core.WFSFeature;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.core.WFSFeatureContents;
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

	
	
	public static boolean isFeatureNameValid(String featureName){

		return featureName.matches("([A-Za-z0-9-_]+)");

	}

	
	public void addFeature(WFSFeature feature){

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


			//***************

			ArrayList<Triple> predicates = new ArrayList<Triple>();
			predicates = this.getDataTypesFDAFeatures(feature);

			String json = "\"toc\": [\n"; 

			for (int i = 0; i < predicates.size(); i++) {

				//if(!predicates.get(i).getPredicate().trim().equals(feature.getGeometryVariable().trim().replace("?", ""))) {

				if(predicates.get(i).getObjectDataType()==null){

					json = json + "	{\""+predicates.get(i).getPredicate() + "\": \"" + GlobalSettings.getDefaultLiteralType() + "\"}";

				} else {

					json = json + "	{\""+ predicates.get(i).getPredicate() + "\": \"" + predicates.get(i).getObjectDataType() + "\"}";


				}

				if(i != predicates.size()-1){

					json = json +",\n";

				}

				//}
			}

			json = json + "\n	],\n";

			writer.write(json);


			//*********

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

		WFSFeature feature = new WFSFeature();

		try {

			FileReader fileReader = new FileReader(GlobalSettings.getFeatureDirectory() + fileName);

			JsonParser jParser = new JsonParser();
			JsonObject jObject = (JsonObject) jParser.parse(fileReader);

			feature.setFeatureAbstract(jObject.get("abstract").getAsString());
			feature.setTitle(jObject.get("title").getAsString());
			feature.setName(jObject.get("name").getAsString());

			try {
				Query query = QueryFactory.create(jObject.get("query").getAsString());
				feature.setQuery(query.toString());

			} catch (Exception e) {
				logger.error("Invalid SPARQL Query at [" + fileName + "]. The correspondent layer won't be listed in the Capabilities Document.");
				logger.error(feature.getQuery());
			}

			feature.setKeywords(jObject.get("keywords").getAsString());
			feature.setGeometryVariable(jObject.get("geometryVariable").getAsString().replace("?", "")); 
			feature.setEndpoint(jObject.get("endpoint").getAsString());
			feature.setCRS(jObject.get("crs").getAsString());
			feature.setEnabled(jObject.get("enabled").getAsBoolean());

			JsonElement elem = jObject.get("toc");


			if(elem!=null){		

				for (int i = 0; i < elem.getAsJsonArray().size(); i++) {

					WFSFeatureContents content = new WFSFeatureContents();

					content.setField(elem.getAsJsonArray().get(i).getAsJsonObject().entrySet().iterator().next().getKey().toString());
					content.setFieldType(elem.getAsJsonArray().get(i).getAsJsonObject().entrySet().iterator().next().getValue().toString().replace("\"", ""));

					feature.addContent(content);

				}


			}

			feature.setLowerCorner(GlobalSettings.getDefaultLowerCorner());
			feature.setUpperCorner(GlobalSettings.getDefaultUpperCorner());
			feature.setAsFDAFeature(true);
			feature.setFileName(fileName);


		} catch (FileNotFoundException e1) {
			
			logger.error("Error loading ["+fileName+"]");
			
			e1.printStackTrace();

		}


		return feature;
	}

	/**
	 * @param WFS feature 
	 * @return Lists all predicates (properties) related to a given feature.  
	 */

	public ArrayList<Triple> getDataTypesFDAFeatures(WFSFeature feature){

		logger.info("Listing available predicates for the FDA feature " + feature.getName() + " ...");

		ArrayList<Triple> result = new ArrayList<Triple>();		
		Query query = QueryFactory.create(feature.getQuery());
		query.setLimit(1);
		
		ResultSet rs = this.executeQuery(query.toString(), feature.getEndpoint());
		QuerySolution qsol = rs.nextSolution();


		for (int i = 0; i < query.getResultVars().size(); i++) {	

			Triple triple = new Triple();

				
				if(qsol.get(query.getResultVars().get(i).toString()).isLiteral()){
					if(qsol.getLiteral(query.getResultVars().get(i)).getDatatypeURI()!=null){
					triple.setObjectDataType(qsol.getLiteral(query.getResultVars().get(i)).getDatatypeURI());
					}
				}
			
		

			triple.setPredicate(query.getResultVars().get(i).toString());
			result.add(triple);

		}

		return result;

	}


	public String getGeometryPredicate(String SPARQLQuery){

		Query query = QueryFactory.create(SPARQLQuery);
		String geometryPredicate = new String();

		final ArrayList<org.apache.jena.graph.Triple> triplesList = new ArrayList<org.apache.jena.graph.Triple>();

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

	
}
