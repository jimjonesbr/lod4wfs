package de.ifgi.lod4wfs.factory;

import it.cutruzzula.lwkt.WKTParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import de.ifgi.lod4wfs.core.SPARQL;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.core.WFSFeature;
import de.ifgi.lod4wfs.core.Triple;
import de.ifgi.lod4wfs.infrastructure.JenaConnector;

/**
 * 
 * @author jones
 * @version 1.0
 * Class responsible for establishing communication with the triple store and providing the XML documents to the facade.
 */

public class FactoryWFSJena {

	private static JenaConnector jn;
	private static Model modelFeatures;	
	private static ArrayList<WFSFeature> dynamicFeatures;
	private static FactorySPARQLFeatures factorySPARQL;
	private static Logger logger = Logger.getLogger("WFS-Factory");
		
	public FactoryWFSJena(){
		jn = new JenaConnector();
		factorySPARQL = new FactorySPARQLFeatures();
		//Load variables defined at the settings file.
		GlobalSettings.loadVariables();
	}

	/**
	 * 
	 * @return A list of all geographic layers of a given SPARQL Endpoint.
	 */
	
	private ArrayList<WFSFeature> listWFSFeatures(){

		logger.info("Listing geographic layers at " + GlobalSettings.default_SPARQLEndpoint + " ...");
		
		ResultSet rs = jn.executeQuery(SPARQL.listNamedGraphs, GlobalSettings.default_SPARQLEndpoint);
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
			feature.setDynamic(false);
			
			CRS = soln.get("?wkt").toString();

			if(CRS.contains("<") || CRS.contains(">")){
				
				CRS = CRS.substring(CRS.indexOf("<"), CRS.indexOf(">"));
				CRS = CRS.replace("http://www.opengis.net/def/crs/EPSG/0/", "EPSG:");
				
				CRS = CRS.replace("<", "");
				CRS = CRS.replace(">", "");
				
				feature.setDefaultCRS(CRS);
			
			} else {
			
				feature.setDefaultCRS(GlobalSettings.defautlCRS);
				
			}
			
			result.add(feature);
			
		}
		
		
		/**
		 * Adding dynamic layers based on SPARQL Queries saved in the sparql directory.
		 */
		
		dynamicFeatures = factorySPARQL.listSPARQLFeatures(GlobalSettings.getSparqlDirectory());
		
		for (int i = 0; i < dynamicFeatures.size(); i++) {
			
			result.add(dynamicFeatures.get(i));

		}
		
		return result;
	}

	/**
	 * @see OGC Specification for WFS http://www.opengeospatial.org/standards/wfs
	 * @param version Version of the Capabilities Document.
	 * @return Capabilities Document
	 * Retrieves the Capabilities Document of a given WFS version.
	 */
	
	public String getCapabilities(String version){

		String resultCapabilities = new String();

		ArrayList<WFSFeature> features = new ArrayList<WFSFeature>(); 
		features = this.listWFSFeatures();
		this.generateLayersPrefixes(features);
		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			if (version.equals("1.0.0")) {

				//Document document = documentBuilder.parse("src/main/resources/wfs/CapabilitiesDocument_100.xml");
				Document document = documentBuilder.parse("wfs/CapabilitiesDocument_100.xml");
				
				/**
				 * Iterates through the layers' model and creates NameSpaces entries with the layers prefixes.
				 */
				
				Element requestElement = document.getDocumentElement(); 
								
				for (Map.Entry<String, String> entry : modelFeatures.getNsPrefixMap().entrySet())
				{
					requestElement.setAttribute("xmlns:" + entry.getKey(), entry.getValue());
				}
				
				logger.info("Creating Capabilities Document of " + GlobalSettings.getCanonicalHostName() + ":" + GlobalSettings.defaultPort + "/" + GlobalSettings.defaultServiceName + "/wfs ...");

				XPath xpath = XPathFactory.newInstance().newXPath();
				NodeList myNodeList = (NodeList) xpath.compile("//FeatureTypeList/text()").evaluate(document, XPathConstants.NODESET);           

				for (int i = 0; i < features.size(); i++) {
								
					Element name = document.createElement("Name");
					name.appendChild(document.createTextNode(modelFeatures.shortForm(features.get(i).getName())));
					Element title = document.createElement("Title");
					title.appendChild(document.createTextNode(features.get(i).getTitle()));
					Element featureAbstract = document.createElement("Abstract");
					featureAbstract.appendChild(document.createTextNode(features.get(i).getFeatureAbstract()));
					Element keywords = document.createElement("Keywords");
					keywords.appendChild(document.createTextNode(features.get(i).getKeywords()));
					Element SRS = document.createElement("SRS");
					SRS.appendChild(document.createTextNode(features.get(i).getCRS()));
					
					Element BBOX = document.createElement("LatLongBoundingBox");
					BBOX.setAttribute("maxy", "83.6274");
					BBOX.setAttribute("maxx", "-180");
					BBOX.setAttribute("miny", "-90");
					BBOX.setAttribute("minx", "180");
					
					Element p = document.createElement("FeatureType");
					p.appendChild(name);
					p.appendChild(title);
					p.appendChild(featureAbstract);
					p.appendChild(keywords);
					p.appendChild(SRS);
					p.appendChild(BBOX);
			        
					myNodeList.item(1).getParentNode().insertBefore(p, myNodeList.item(1));
										
				}
									        
				resultCapabilities = this.printXMLDocument(document);
			}


		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} 
		
		resultCapabilities = resultCapabilities.replace("PARAM_PORT", Integer.toString(GlobalSettings.defaultPort));
		resultCapabilities = resultCapabilities.replace("PARAM_HOST", GlobalSettings.getCanonicalHostName());
		resultCapabilities = resultCapabilities.replace("PARAM_SERVICE", GlobalSettings.defaultServiceName);
		
		return resultCapabilities;

	}


	/**
	 * @see OGC Specification for WFS http://www.opengeospatial.org/standards/wfs 
	 * @param layer geographic feature to be described.
	 * @return XML Document containing the WFS DescribeFeatureType response.
	 * Retrieves the XML Document containing the WFS DescribeFeatureType response, listing all properties related to a given feature with their data types.
	 */
	
	private String removePredicateURL(String predicate){
		
		return predicate.split("\\P{Alpha}+")[predicate.split("\\P{Alpha}+").length-1];
						
	}
	
	private boolean isDynamic(WFSFeature feature){
		
		boolean result = false;
		
		/**
		 * Checks if the selected layer is dynamic (based on pre-defined SPARQL Query)
		 */
		for (int i = 0; i < dynamicFeatures.size(); i++) {
			
			if(dynamicFeatures.get(i).getName().equals(modelFeatures.expandPrefix(feature.getName()))){
				result = true; 
				feature.setQuery(dynamicFeatures.get(i).getQuery());
				feature.setGeometryVariable(dynamicFeatures.get(i).getGeometryVariable());
				feature.setEndpoint(dynamicFeatures.get(i).getEndpoint());
				
			}
			
		}
		
		return result;
		
	}
	
	public String describeFeatureType(WFSFeature feature){


		boolean isDynamic = isDynamic(feature);
			
		String describeFeatureTypeResponse = new String(); 
		ArrayList<Triple> predicates = new ArrayList<Triple>();
		
		if(isDynamic){
			
			predicates = this.getPredicatesDynamicFeatures(feature);
			
		} else { 
				
			predicates = this.getPredicatesStandardFeatures(feature);
		}
		
		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			
			//Document document = documentBuilder.parse("src/main/resources/wfs/DescribeFeature_100.xml");
			Document document = documentBuilder.parse("wfs/DescribeFeature_100.xml");
					
			logger.info("Creating DescribeFeatureType XML document for " + feature.getName() + " ...");

			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList myNodeList = (NodeList) xpath.compile("//extension/sequence/text()").evaluate(document, XPathConstants.NODESET);           

			String layerPrefix = modelFeatures.shortForm(feature.getName());
			layerPrefix = layerPrefix.substring(0,layerPrefix.indexOf(":")+1);			
						
			Element requestElement = document.getDocumentElement(); 						
			requestElement.setAttribute("targetNamespace", modelFeatures.expandPrefix(layerPrefix));
			
			for (Map.Entry<String, String> entry : modelFeatures.getNsPrefixMap().entrySet())
			{
				requestElement.setAttribute("xmlns:" + entry.getKey(), entry.getValue());
				
			}
			
			
			for (int i = 0; i < predicates.size(); i++) {

				String predicateWithoutPrefix = new String();
				predicateWithoutPrefix =  predicates.get(i).getPredicate().substring(predicates.get(i).getPredicate().indexOf(":")+1, predicates.get(i).getPredicate().length());
				
				predicateWithoutPrefix = this.removePredicateURL(predicates.get(i).getPredicate());
				
				Element sequence = document.createElement("xsd:element");
				sequence.setAttribute("maxOccurs","1");
				sequence.setAttribute("minOccurs","0");
				sequence.setAttribute("name", predicateWithoutPrefix);
				sequence.setAttribute("nillable","true");
								
				//if(predicates.get(i).getPredicate().equals("geo:asWKT")){
				
				//System.out.println("predicate: " + predicates.get(i).getPredicate());
				//System.out.println(GlobalSettings.getGeometryPredicate().replaceAll("<", "").replace(">", ""));
				
				if(predicates.get(i).getPredicate().equals(GlobalSettings.getGeometryPredicate().replaceAll("<", "").replace(">", ""))){
					String featureType = new String();
					featureType = this.getFeatureType(feature);
			
					if(featureType.equals("gml:MultiPolygon") || featureType.equals("gml:Polygon")){
						sequence.setAttribute("type","gml:MultiPolygonPropertyType");
					}

					if(featureType.equals("gml:LineString")){
						sequence.setAttribute("type","gml:MultiLineStringPropertyType");
					}

					if(featureType.equals("gml:Point") || featureType.equals("gml:MultiPoint") ){
						sequence.setAttribute("type","gml:MultiPointPropertyType");
					}


				} else if(isDynamic && (predicates.get(i).getPredicate().equals(feature.getGeometryVariable()))){
					
					//TODO: create function to identify geometry type!!!
					sequence.setAttribute("type","gml:MultiPointPropertyType");
					
					
					
				} else {
					sequence.setAttribute("type",predicates.get(i).getObjectDataType());
				}

				myNodeList.item(0).getParentNode().insertBefore(sequence, myNodeList.item(0));

			}

			describeFeatureTypeResponse = this.printXMLDocument(document);

			describeFeatureTypeResponse = describeFeatureTypeResponse.replace("PARAM_NAME", feature.getName().substring(feature.getName().indexOf(":")+1, feature.getName().length()));
			describeFeatureTypeResponse = describeFeatureTypeResponse.replace("PARAM_TYPE", feature.getName());
			describeFeatureTypeResponse = describeFeatureTypeResponse.replace("PARAM_SERVER_PORT", Integer.toString(GlobalSettings.defaultPort));
			describeFeatureTypeResponse = describeFeatureTypeResponse.replace("PARAM_SERVICE", GlobalSettings.defaultServiceName);
			describeFeatureTypeResponse = describeFeatureTypeResponse.replace("PARAM_SERVER", java.net.InetAddress.getLocalHost().getHostName());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} 

		return describeFeatureTypeResponse;


	}

	/**
	 * @param feature geographic feature 
	 * @return Lists all predicates (properties) related to a given feature.  
	 */
	
	private ArrayList<Triple> getPredicatesDynamicFeatures(WFSFeature feature){
		
		logger.info("Listing available predicates for the dynamic feature " + feature.getName() + " ...");
		
		ArrayList<Triple> result = new ArrayList<Triple>();
		
		Query query = QueryFactory.create(feature.getQuery());
		
		//for (int i = 0; i < feature.getQuery().getResultVars().size(); i++) {
		for (int i = 0; i < query.getResultVars().size(); i++) {	
			Triple triple = new Triple();
			triple.setObjectDataType(GlobalSettings.defaultLiteralType);
			triple.setPredicate(query.getResultVars().get(i).toString());
			result.add(triple);
		
		}
			
		return result;
		
	}
	
	private ArrayList<Triple> getPredicatesStandardFeatures(WFSFeature feature){

		logger.info("Listing available predicates for " + feature.getName() + " ...");
	
		ResultSet rs = jn.executeQuery(SPARQL.listGeometryPredicates.replace("PARAM_LAYER", modelFeatures.expandPrefix(feature.getName())), GlobalSettings.default_SPARQLEndpoint);
		ArrayList<Triple> result = new ArrayList<Triple>();		

				
		while (rs.hasNext()) {

			Triple triple = new Triple();

			QuerySolution soln = rs.nextSolution();
			//triple.setPredicate(modelNameSpaces.shortForm(soln.getResource("?predicate").toString()));
			triple.setPredicate(soln.getResource("?predicate").toString());

			//System.out.println("Predicate from getPredicatesStandard" + triple.getPredicate());
			
			if (soln.get("?dataType")==null) {

				triple.setObjectDataType(GlobalSettings.defaultLiteralType);

			} else {

				//triple.setObjectDataType(modelNameSpaces.shortForm(soln.getResource("?dataType").getURI()));
				triple.setObjectDataType(soln.getResource("?dataType").toString().replace(GlobalSettings.getXsdNameSpace(), "xsd:"));
			}

			result.add(triple);			   
		}

		return result;
	}

	/**
	 * @param feature geographic feature 
	 * @return Data type of a given feature.
	 */
	private String getFeatureType (WFSFeature feature){

		logger.info("Getting geometry type for " + feature.getName() + " ...");
		
		//ResultSet rs = jn.executeQuery(this.getPrefixes(modelNameSpaces) + SPARQL.getFeatureType.replace("PARAM_LAYER", modelLayers.expandPrefix(layer.getName())));
		ResultSet rs = jn.executeQuery(SPARQL.getFeatureType.replace("PARAM_LAYER", modelFeatures.expandPrefix(feature.getName())),GlobalSettings.default_SPARQLEndpoint);
		//System.out.println("DELETE ME --> "+SPARQL.getFeatureType.replace("PARAM_LAYER", modelLayers.expandPrefix(layer.getName())));
		String geometryCoordinates = new String();
		
		
		while (rs.hasNext()) {

			QuerySolution soln = rs.nextSolution();					
			geometryCoordinates = soln.getLiteral("?wkt").getString();
		}

		try {

			geometryCoordinates = this.convertWKTtoGML(geometryCoordinates);
			
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			
			Document document = documentBuilder.parse(new InputSource(new ByteArrayInputStream(geometryCoordinates.getBytes("utf-8"))));

			geometryCoordinates = document.getDocumentElement().getNodeName();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return geometryCoordinates;
	}

	/**
	 * @see OGC Specification for WFS http://www.opengeospatial.org/standards/wfs
	 * @param feature geographic feature to be retrieved.
	 * @return XML Document containing the WFS GetFeature response with all geometries of a given feature together with their attribute table.
	 */
	public String getFeature(WFSFeature feature) {

		String getFeatureResponse = new String();
		String layerPrefix = new String();
		
		ArrayList<Triple> predicates = new ArrayList<Triple>();					
		ResultSet rs;
				
		
		
		if(isDynamic(feature)){
			
			logger.info("Performing query at " + feature.getEndpoint()  + " to retrieve all geometries of " + feature.getName() + "  ...");
			predicates = this.getPredicatesDynamicFeatures(feature);
			rs = jn.executeQuery(feature.getQuery().toString(),feature.getEndpoint());
			
			System.out.println("Endpoint --> "+feature.getEndpoint());
			
		} else {
		
			logger.info("Performing query at " + GlobalSettings.default_SPARQLEndpoint  + " to retrieve all geometries of " + feature.getName() + "  ...");
			predicates = this.getPredicatesStandardFeatures(feature);					
			rs = jn.executeQuery(this.generateGetFeatureSPARQL(feature, predicates),GlobalSettings.default_SPARQLEndpoint);
			
		}
		
		layerPrefix = modelFeatures.shortForm(feature.getName());
		System.out.println(layerPrefix);
		layerPrefix = layerPrefix.substring(0,layerPrefix.indexOf(":"));
		
		
		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder;

			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			//Document document = documentBuilder.parse("src/main/resources/wfs/GetFeature_100.xml");
			Document document = documentBuilder.parse("wfs/GetFeature_100.xml");

			//Build Name Spaces in the XML header.
			Element requestElement = document.getDocumentElement(); 
			
			for (Map.Entry<String, String> entry : modelFeatures.getNsPrefixMap().entrySet())
			{
				requestElement.setAttribute("xmlns:" + entry.getKey(), entry.getValue());
			}			
			
			long countIteration = 0;
			
			logger.info("Creating GetFeature XML document for " + feature.getName() + "...");

			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList myNodeList = (NodeList) xpath.compile("//FeatureCollection/text()").evaluate(document, XPathConstants.NODESET);           

			while (rs.hasNext()) {
				countIteration++;
				
				QuerySolution soln = rs.nextSolution();
				
//				String currentGeometryName = soln.getResource("?geometry").getLocalName();
				String currentGeometryName = "GEO_";
				Element currentGeometryElement = document.createElement(modelFeatures.shortForm(feature.getName()));
				
				
				currentGeometryElement.setAttribute("fid", currentGeometryName + "." + countIteration);				

				Element rootGeometry = document.createElement("gml:featureMember");
								
				for (int i = 0; i < predicates.size(); i++) {

					if(isDynamic(feature)){
						
						Element elementGeometryPredicate = document.createElement(layerPrefix + ":" + predicates.get(i).getPredicate());
						
						if(predicates.get(i).getPredicate().equals(feature.getGeometryVariable())){
														
							System.out.println("FACTORY: DELETE-ME ->> " + soln.get("?"+feature.getGeometryVariable()));
							System.out.println("?"+feature.getGeometryVariable());
							//String gml = this.convertWKTtoGML(soln.getLiteral("?"+feature.getGeometryVariable()).toString());		
							String gml = this.convertWKTtoGML(soln.getLiteral("?"+feature.getGeometryVariable()).getString());
							
							Element GMLnode =  documentBuilder.parse(new ByteArrayInputStream(gml.getBytes())).getDocumentElement();		
							Node dup = document.importNode(GMLnode, true);
							elementGeometryPredicate.appendChild(dup);						
							rootGeometry.appendChild(elementGeometryPredicate);												
							currentGeometryElement.appendChild(elementGeometryPredicate);						
							rootGeometry.appendChild(currentGeometryElement);					
							
						} else {
							
							Element elementAttribute = document.createElement(layerPrefix + ":" + predicates.get(i).getPredicate());
							elementAttribute.appendChild(document.createCDATASection(soln.get("?"+predicates.get(i).getPredicate()).toString()));														
							currentGeometryElement.appendChild(elementAttribute);

						}
						
						
						
					} else {
						
						
						String predicateWithoutPrefix = new String();
											
						predicateWithoutPrefix =  this.removePredicateURL(predicates.get(i).getPredicate());
						
						Element elementGeometryPredicate = document.createElement(layerPrefix + ":" + predicateWithoutPrefix);
																				
						//if (predicates.get(i).getPredicate().equals("geo:asWKT")) {			
						if (predicates.get(i).getPredicate().equals(GlobalSettings.getGeometryPredicate().replaceAll("<", "").replace(">", ""))) {
							
							//TODO: Automatic generate getGeometryVariable
							
							if(!FactorySPARQLFeatures.getGeometryType(removeReferenceSystem(soln.getLiteral("?"+GlobalSettings.getGeometryVariable()).getString())).equals("INVALID")){
															
								String gml = this.convertWKTtoGML(soln.getLiteral("?"+GlobalSettings.getGeometryVariable()).getString());											
								Element GMLnode =  documentBuilder.parse(new ByteArrayInputStream(gml.getBytes())).getDocumentElement();		
								Node dup = document.importNode(GMLnode, true);
								elementGeometryPredicate.appendChild(dup);						
								rootGeometry.appendChild(elementGeometryPredicate);												
								currentGeometryElement.appendChild(elementGeometryPredicate);						
								rootGeometry.appendChild(currentGeometryElement);					
		
							} else {
								
								logger.error("The feature [" + soln.get("?geometry") + "] has an invalid geometry literal.");
								
							}
							
						} else {
	
							Element elementAttribute = document.createElement(layerPrefix + ":" + predicateWithoutPrefix);
							//elementAttribute.appendChild(document.createCDATASection(modelNameSpaces.shortForm(soln.get("?"+predicateWithoutPrefix).toString())));
							elementAttribute.appendChild(document.createCDATASection(soln.get("?"+predicateWithoutPrefix).toString()));
						
							currentGeometryElement.appendChild(elementAttribute);
							
						}
	
					}
					
					myNodeList.item(1).getParentNode().insertBefore(rootGeometry, myNodeList.item(1));
				}
			}

			logger.info("XML Document with " + countIteration + " features successfully created.");
			
			getFeatureResponse = this.printXMLDocument(document);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		}

		return getFeatureResponse;

	}

	/**
	 * @param feature geographic feature
	 * @param predicates list of predicates from the feature of interest.
	 * @return SPARQL query for retrieving all geometries of a given feature and their related attributes.
	 */
	private String generateGetFeatureSPARQL(WFSFeature feature, ArrayList<Triple> predicates){

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
			
			selectClause = selectClause + "	?" + SPARQL_Variable + 	GlobalSettings.crlf ;
			whereClause = whereClause + "	?geometry <" + predicates.get(i).getPredicate() + "> ?" + SPARQL_Variable +" ." + GlobalSettings.crlf ; 

			
			variables.add(SPARQL_Variable);
		}

		String SPARQL = new String();

		SPARQL = " SELECT ?geometry "+ GlobalSettings.crlf + selectClause +
				 " WHERE { GRAPH <"+ modelFeatures.expandPrefix(feature.getName()) + "> {" + GlobalSettings.crlf +
				 "	?geometry a " + GlobalSettings.getGeometryClass() + " . "+ GlobalSettings.crlf + whereClause + " }}";

		return SPARQL;
		
	}

	/**
	 * @param features list of geographic features
	 * 
	 */
	//TODO implement a return type for generateLayersPrefixes(). Put value direct in a variable isn't recommended. 
	private void generateLayersPrefixes(ArrayList<WFSFeature> features){
		
		Pattern pattern = Pattern.compile("[^a-z0-9A-Z_]");
		modelFeatures = ModelFactory.createDefaultModel();
		
		for (int i = 0; i < features.size(); i++) {
						
			boolean scape = false;

			int size = features.get(i).getName().length()-1;
			int position = 0;

			while ((scape == false) && (size >= 0)) {

				Matcher matcher = pattern.matcher(Character.toString(features.get(i).getName().charAt(size)));

				boolean finder = matcher.find();

				if (finder==true) {

					position = size;
					scape=true;
					
				}

				size--;
			}
			
			if (modelFeatures.getNsURIPrefix(features.get(i).getName().substring(0, position+1))==null) {
				
				if (features.get(i).isDynamic()){
					
					//modelLayers.setNsPrefix("sparql"+ modelLayers.getNsPrefixMap().size(), layers.get(i).getName().substring(0, position+1) );
					modelFeatures.setNsPrefix("sparql", features.get(i).getName().substring(0, position+1) );
					
				} else {
					
					modelFeatures.setNsPrefix("ts"+ modelFeatures.getNsPrefixMap().size(), features.get(i).getName().substring(0, position+1) );
			
				}
			}
			
		}
		
	}

	/**
	 * 
	 * @param XML Document
	 * @return string containing a given XML Document contents.
	 */
	private String printXMLDocument(Document document){
		
		String XMLString = new String();
		StringWriter stringWriter = new StringWriter();		
		DOMSource source = new DOMSource(document);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		
		try {
			
			transformer = transformerFactory.newTransformer();
			StreamResult result = new StreamResult(stringWriter);
			transformer.transform(source, result);
			StringBuffer stringBuffer = stringWriter.getBuffer();
			XMLString = stringBuffer.toString();
			
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	
		return XMLString;
	}

	/**
	 * 
	 * @param wkt Well Known Text geometry to be converted.
	 * @return GML encoding of a given Well Known Text literal.
	 */
	
	private String removeReferenceSystem(String wkt){
	
		if(wkt.contains("<") && wkt.contains(">")){
			
			wkt = wkt.substring(wkt.indexOf(">") + 1, wkt.length());

		}

		return wkt;
	}
	
	private String convertWKTtoGML(String wkt){
		
		String gml = new String();
		
		try {
		
			if(wkt.contains("<") && wkt.contains(">")){
				String CRS = new String();
				
				CRS = wkt.substring(wkt.indexOf("<") + 1, wkt.indexOf(">"));// .replace("http://www.opengis.net/def/crs/EPSG/0/", "EPSG:");
				
				//wkt = wkt.substring(wkt.indexOf(">") + 1, wkt.length());
				wkt = removeReferenceSystem(wkt);
				
				gml = WKTParser.parseToGML2(wkt,CRS);
				
				
			} else {
			
				gml = WKTParser.parseToGML2(wkt,GlobalSettings.defautlCRS);
			
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return gml;
		
	}

}
