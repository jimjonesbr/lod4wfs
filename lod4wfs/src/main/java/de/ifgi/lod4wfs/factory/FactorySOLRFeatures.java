package de.ifgi.lod4wfs.factory;

import it.cutruzzula.lwkt.WKTParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.client.solrj.response.LukeResponse;
import org.apache.solr.client.solrj.response.LukeResponse.FieldInfo;
import org.apache.solr.common.SolrDocumentList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.core.SOLRRecord;
import de.ifgi.lod4wfs.core.Utils;
import de.ifgi.lod4wfs.core.WFSFeature;
import de.ifgi.lod4wfs.infrastructure.SOLRConnector;

/**
 * @author Jim Jones
 */

public class FactorySOLRFeatures {

	private static Logger logger = Logger.getLogger("SOLRFeatures-Factory");

	public ArrayList<WFSFeature> listSOLRFeatures() {

		File[] files = new File(GlobalSettings.getFeatureDirectory()).listFiles();

		logger.info("Listing features from the direcoty " + System.getProperty("user.dir") + File.separator + GlobalSettings.getFeatureDirectory() + " ...");

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

			feature.setLowerCorner(GlobalSettings.getDefaultLowerCorner());
			feature.setUpperCorner(GlobalSettings.getDefaultUpperCorner());

			if(jObject.get("crs").getAsString().equals("")){

				feature.setCRS(GlobalSettings.getDefaultCRS());

			} else {

				feature.setCRS(jObject.get("crs").getAsString());

			}

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

		try {

			/**
			 *  "*" means all fields.
			 */
			if (feature.getFields().equals("*")){


				LukeRequest lukeRequest = new LukeRequest();
				lukeRequest.setNumTerms(1);
				LukeResponse lukeResponse;


				lukeResponse = lukeRequest.process(solr);

				List<FieldInfo> sorted = new ArrayList<FieldInfo>(lukeResponse.getFieldInfo().values());
				
				for (FieldInfo infoEntry : sorted) {

					SOLRRecord record = new SOLRRecord();
					
					record.setName(infoEntry.getName());
					
					if(infoEntry.getName().equals(feature.getGeometryVariable())){
						
						//TODO: Hard-coded geometry type!
						// record.setType(this.getSOLRGeometryType(feature));
												
						record.setType("gml:MultiPolygonPropertyType");
						
						
					} else {
					
						
						if(infoEntry.getType().equals("long")){
							
							record.setType(GlobalSettings.getDefaultLongType());
							
						} else if(infoEntry.getType().equals("text_general")){
							
							record.setType(GlobalSettings.getDefaultStringType());
							
						}  else if(infoEntry.getType().equals("location_rpt")){
							
							record.setType(GlobalSettings.getDefaultStringType());
							
						} else if(infoEntry.getType().equals("string")){
							
							record.setType(GlobalSettings.getDefaultStringType());
							
						} else if(infoEntry.getType().equals("date")){
							
							record.setType(GlobalSettings.getDefaultStringType());
							
						} else if(infoEntry.getType().equals("float")){
							
							record.setType(GlobalSettings.getDefaultFloatType());
							
						} else if(infoEntry.getType().equals("int")){
							
							record.setType(GlobalSettings.getDefaultIntegerType());
							
						} else {
							
							record.setType(GlobalSettings.getDefaultStringType());
							
							logger.warn("Unpexpected data type for SOLR Feature [" + feature.getName() + "] The field \"" + infoEntry.getName()  + "\" has the data type \"" + infoEntry.getType() + "\"");
							
						}
							
					}
					
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
		} catch (IOException e) {
			e.printStackTrace();
		}	

		return result;

	}


	public String getSOLRGeometryType(WFSFeature feature){

		logger.info("Getting geometry type for the SOLR-Based feature [" + feature.getName() + "] ...");

		String result = new String();
		int tmpLimit = feature.getLimit();
		feature.setLimit(1);

		SOLRConnector solrConnector = new SOLRConnector();
		SolrDocumentList rs = solrConnector.executeQuery(feature);

		for (int i = 0; i < rs.size(); i++) {

			result = Utils.getGeometryType(rs.get(i).getFieldValue(feature.getGeometryVariable()).toString());

		}

		feature.setLimit(tmpLimit);

		return result;

	}
}
