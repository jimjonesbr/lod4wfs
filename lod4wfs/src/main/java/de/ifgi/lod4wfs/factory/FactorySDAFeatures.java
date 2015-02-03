package de.ifgi.lod4wfs.factory;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.core.SPARQL;
import de.ifgi.lod4wfs.core.Triple;
import de.ifgi.lod4wfs.core.Utils;
import de.ifgi.lod4wfs.core.WFSFeature;
import de.ifgi.lod4wfs.infrastructure.JenaConnector;

/**
 * @author Jim Jones
 */

public class FactorySDAFeatures {

	private static Logger logger = Logger.getLogger("SDAFeatures-Factory");
	private static JenaConnector jn;
	
	public FactorySDAFeatures() {

		jn = new JenaConnector();
		
	}
	
	public ArrayList<WFSFeature> listSDAFeatures(){

		logger.info("Listing features from the SPARQL Endpoint " + GlobalSettings.getDefaultSPARQLEndpoint() + " ...");
		
		ResultSet rs = jn.executeQuery(SPARQL.listNamedGraphs, GlobalSettings.getDefaultSPARQLEndpoint());
		ArrayList<WFSFeature> result = new ArrayList<WFSFeature>();
		
		String CRS = new String();
		
		while (rs.hasNext()) {
			WFSFeature feature = new WFSFeature();
			QuerySolution soln = rs.nextSolution();
			feature.setName(soln.get("?graphName").toString());
			feature.setTitle(soln.getLiteral("?title").getValue().toString());
			feature.setFeatureAbstract(soln.getLiteral("?abstract").getValue().toString());
			feature.setKeywords(soln.getLiteral("?keywords").getValue().toString());
			feature.setLowerCorner(GlobalSettings.defaultLowerCorner);
			feature.setUpperCorner(GlobalSettings.defaultUpperCorner);
			feature.setAsSDAFeature(true);
			
			
			CRS = soln.get("?wkt").toString();

			if(CRS.contains("<") || CRS.contains(">")){
				
				CRS = CRS.substring(CRS.indexOf("<"), CRS.indexOf(">"));
				CRS = CRS.replace("http://www.opengis.net/def/crs/EPSG/0/", "EPSG:");
				
				CRS = CRS.replace("<", "");
				CRS = CRS.replace(">", "");
				
				feature.setDefaultCRS(CRS);
			
			} else {
			
				feature.setDefaultCRS(GlobalSettings.getDefaultCRS());
				
			}
			
			result.add(feature);
			
		}
				
		logger.info("Total SDA Features: " + result.size());
		
		return result;
	}
	
	public ArrayList<Triple> getPredicatesSDAFeatures(String feature){

		logger.info("Listing available predicates for [" + feature + "] ...");

		ResultSet rs = jn.executeQuery(SPARQL.listFeaturePredicates.replace("PARAM_LAYER", feature), GlobalSettings.getDefaultSPARQLEndpoint());
		ArrayList<Triple> result = new ArrayList<Triple>();		

				
		while (rs.hasNext()) {

			Triple triple = new Triple();
			QuerySolution soln = rs.nextSolution();
			triple.setPredicate(soln.getResource("?predicate").toString());
			
			if (soln.get("?dataType")==null) {

				triple.setObjectDataType(GlobalSettings.defaultLiteralType);

			} else {

				triple.setObjectDataType(soln.getResource("?dataType").toString().replace(GlobalSettings.getXsdNameSpace(), "xsd:"));
			}

		
			result.add(triple);			   
		}


		
		return result;
	}

	public String generateGetFeatureSPARQL(String feature, ArrayList<Triple> predicates){

		String selectClause = new String();
		String whereClause = new String();
		ArrayList<String> variables = new ArrayList<String>();
		
		for (int i = 0; i < predicates.size(); i++) {

			String SPARQL_Variable = new String();
			SPARQL_Variable = this.removePredicateURL(predicates.get(i).getPredicate());
			
			//Check if more than one variable with the same name is generated.
			if(variables.contains(SPARQL_Variable)){
				SPARQL_Variable = SPARQL_Variable + i;
			}
			
			selectClause = selectClause + "	?" + SPARQL_Variable + 	GlobalSettings.getCrlf() ;
			whereClause = whereClause + "	?feature <" + predicates.get(i).getPredicate() + "> ?" + SPARQL_Variable +" ." + GlobalSettings.getCrlf() ; 

			
			variables.add(SPARQL_Variable);
		}

		String SPARQL = new String();

		selectClause = selectClause +" ?"+ GlobalSettings.getGeometryVariable() + GlobalSettings.getCrlf() ;
		
		SPARQL = " SELECT ?geometry " + GlobalSettings.getCrlf() + selectClause +
				" WHERE { GRAPH <"+ feature + "> {" + GlobalSettings.getCrlf() +
				 "	?feature a " + GlobalSettings.getPredicatesContainer() + " . "+ GlobalSettings.getCrlf() +
				 "	?feature " + GlobalSettings.getFeatureConnector() + " ?geometry . "+ GlobalSettings.getCrlf() +
				 "	?geometry a " + GlobalSettings.getGeometryClass() + " . " + GlobalSettings.getCrlf() + 
				 "	?geometry " + GlobalSettings.getGeometryPredicate() + " ?"+ GlobalSettings.getGeometryVariable() + " . " + GlobalSettings.getCrlf() +
				 whereClause + " }}";

		return SPARQL;
		
	}
		
	private String removePredicateURL(String predicate){
		
		return predicate.split("\\P{Alpha}+")[predicate.split("\\P{Alpha}+").length-1];
						
	}
	
	/**
	 * @param WFS feature 
	 * @return Data type of a given feature.
	 */
	public String getFeatureType (String feature){

		logger.info("Getting geometry type for [" + feature + "] ...");
		
		ResultSet rs = jn.executeQuery(SPARQL.getFeatureType.replace("PARAM_LAYER", feature),GlobalSettings.getDefaultSPARQLEndpoint());

		String geometryCoordinates = new String();	
		
		while (rs.hasNext()) {

			QuerySolution soln = rs.nextSolution();					
			geometryCoordinates = soln.getLiteral("?geometryLiteral").getString();
		}

		try {

			//TODO: Check if literal already is GML. 
			
			geometryCoordinates = Utils.convertWKTtoGML(geometryCoordinates);
			
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			
			Document document = documentBuilder.parse(new InputSource(new ByteArrayInputStream(geometryCoordinates.getBytes("utf-8"))));

			geometryCoordinates = document.getDocumentElement().getNodeName();

		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return geometryCoordinates;
	}
	
}



