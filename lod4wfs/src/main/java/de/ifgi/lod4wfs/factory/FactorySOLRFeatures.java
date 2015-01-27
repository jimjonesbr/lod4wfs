package de.ifgi.lod4wfs.factory;

import it.cutruzzula.lwkt.WKTParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.core.SOLRRecord;
import de.ifgi.lod4wfs.core.WFSFeature;

public class FactorySOLRFeatures {

	private static Logger logger = Logger.getLogger("SOLRFeatures-Factory");

	public ArrayList<WFSFeature> listSOLRFeatures() {

		File[] files = new File(GlobalSettings.getFeatureDirectory()).listFiles();

		logger.info("Listing features from the direcoty [application root]/" + GlobalSettings.getFeatureDirectory() + " ...");
		
		ArrayList<WFSFeature> result = new ArrayList<WFSFeature>();

		for (File file : files) {

			if(file.getName().endsWith(".solr")){

				WFSFeature feature = new WFSFeature();

				feature = this.getSOLRFeature(file.getName());

				if(feature != null){
					result.add(feature);
				}

			}

		}

		logger.info("Total SOLR Features: " + result.size());
		
		return result;
	}

	public WFSFeature getSOLRFeature(String fileName){

		WFSFeature feature = new WFSFeature();

		try {

			FileReader fileReader = new FileReader(GlobalSettings.getFeatureDirectory() + fileName);

			JsonParser jParser = new JsonParser();
			JsonObject jObject = (JsonObject) jParser.parse(fileReader);

			feature.setFeatureAbstract(jObject.get("abstract").getAsString());
			feature.setTitle(jObject.get("title").getAsString());
			feature.setName(jObject.get("name").getAsString());
			feature.setKeywords(jObject.get("keywords").getAsString());
			feature.setGeometryVariable(jObject.get("geometryVariable").getAsString()); 
			feature.setEndpoint(jObject.get("endpoint").getAsString());
			feature.setSOLRSorting(jObject.get("order").getAsString());
			feature.setLimit(jObject.get("limit").getAsInt());
			feature.setFields(jObject.get("fields").getAsString());
			feature.setSOLRFilter(jObject.get("filter").getAsString());
			
			JsonElement elem = jObject.get("spatialFilter");
			feature.setSOLRSpatialConstraint(elem.getAsJsonObject().get("spatialConstraint").getAsString());
			feature.setSOLRGeometryField(elem.getAsJsonObject().get("geometryField").getAsString());				

			feature.setLowerCorner(GlobalSettings.defaultLowerCorner);
			feature.setUpperCorner(GlobalSettings.defaultUpperCorner);
			feature.setDefaultCRS(GlobalSettings.defautlCRS);
			feature.setAsSOLR();
			feature.setFileName(fileName);
			feature.setAsSOLRFeature(true);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
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

		//Removing Spatial Reference System
		if(wkt.contains("<") && wkt.contains(">")){

			wkt = wkt.substring(wkt.indexOf(">") + 1, wkt.length());

		}

		//Removing literal type.
		if(wkt.contains("^^")){

			wkt = wkt.substring(0, wkt.indexOf("^^"));

		}

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

	/**
	 * @param WFS feature 
	 * @return Lists all predicates (properties) related to a given feature.  
	 */

	public ArrayList<SOLRRecord> getSOLRFeatureFields(WFSFeature feature){

		logger.info("Listing available fields for the SOLR-Based feature [" + feature.getName() + "] ...");

		ArrayList<SOLRRecord> result = new ArrayList<SOLRRecord>();		

		HttpSolrServer solr = new HttpSolrServer(feature.getEndpoint());
		SolrQuery query = new SolrQuery();

		try {

			
			/**
			 * Where "*" means all fields.
			 */
			if (feature.getFields().equals("*")){

				query.setStart(0);    
				query.setRows(1);			
				query.addFilterQuery(feature.getSOLRGeometryField() + ":\"" + feature.getSOLRSpatialConstraint() + "\"");
			
				query.setQuery(feature.getFields());
								
				QueryResponse response = solr.query(query);
				SolrDocumentList results = response.getResults();
				
				List<String> fieldsList = Arrays.asList(results.get(0).getFieldNames().toString().split(","));

				for (int i = 0; i < fieldsList.size(); i++) {

					SOLRRecord record = new SOLRRecord();

					/**
					 * Removing the [ and ] from the fields string.  
					 */
					record.setName(fieldsList.get(i).toString().replace("[", "").replace("]", "").trim());
					record.setType(GlobalSettings.getDefaultStringType());

					result.add(record);

				}


			} else {

				String[] fields = new String[feature.getFields().split(",").length];
				fields = feature.getFields().split(",");

				SOLRRecord geometryField = new SOLRRecord();					
				geometryField.setType(this.getSOLRGeometryType(feature));
				geometryField.setName(feature.getSOLRGeometryField());			
				result.add(geometryField);
				
				for (int i = 0; i < fields.length; i++) {

					SOLRRecord record = new SOLRRecord();

					record.setName(fields[i].trim());
					record.setType(GlobalSettings.getDefaultStringType());

					result.add(record);


				}
			}



		} catch (SolrServerException e) {
			e.printStackTrace();
		}	

		return result;

	}


	public String getSOLRGeometryType(WFSFeature feature){

		//TODO: Implement function to retrieve Geometry Type from SOLRRecords.

		logger.info("Getting geometry type for the SOLR-Based feature [" + feature.getName() + "] ...");

		return "gml:MultiPolygon";

	}
}
