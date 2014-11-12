package de.ifgi.lod4wfs.factory;

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
import org.xml.sax.SAXException;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.core.Triple;
import de.ifgi.lod4wfs.core.Utils;
import de.ifgi.lod4wfs.core.WFSFeature;
import de.ifgi.lod4wfs.infrastructure.JenaConnector;

public class FactoryWFS {
	
	private static FactoryWFS instance;
	private FactorySDAFeatures factorySDA;
	private FactoryFDAFeatures factoryFDA;
	private static Model modelFeatures;	

	private static ArrayList<WFSFeature> fdaFeatures;
	private static ArrayList<WFSFeature> sdaFeatures;
	
	private static JenaConnector jn;
	private static Logger logger = Logger.getLogger("WFS-Factory");

	public FactoryWFS(){
		factorySDA = new FactorySDAFeatures();
		factoryFDA = new FactoryFDAFeatures();
		jn = new JenaConnector();
	}

	public static FactoryWFS getInstance() {
		if (instance == null) {
			instance = new FactoryWFS();
		}
		return instance;
	}

	public String getCapabilities(String version){

		String resultCapabilities = new String();

		ArrayList<WFSFeature> features = new ArrayList<WFSFeature>(); 
		features = this.listFeatures();
		this.generateLayersPrefixes(features);
		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			if (version.equals("1.0.0")) {

				Document document = documentBuilder.parse("wfs/CapabilitiesDocument_100.xml");		

				 /**
				  * Iterates through the layers' model and creates NameSpaces entries with the layers prefixes.
				  */

				Element requestElement = document.getDocumentElement(); 
								
				for (Map.Entry<String, String> entry : modelFeatures.getNsPrefixMap().entrySet())
				{
					requestElement.setAttribute("xmlns:" + entry.getKey(), entry.getValue());
				}
				
				logger.info("Creating Capabilities Document of " + Utils.getCanonicalHostName() + ":" + GlobalSettings.defaultPort + "/" + GlobalSettings.defaultServiceName + "/wfs ...");

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
		resultCapabilities = resultCapabilities.replace("PARAM_HOST", Utils.getCanonicalHostName());
		resultCapabilities = resultCapabilities.replace("PARAM_SERVICE", GlobalSettings.defaultServiceName);
		
		return resultCapabilities;


		
	}
	
	public String describeFeatureType(WFSFeature feature){


		boolean isFDA = isFDAFeature(feature);
		
		String featureName = modelFeatures.expandPrefix(feature.getName());
		
		String describeFeatureTypeResponse = new String(); 
		ArrayList<Triple> predicates = new ArrayList<Triple>();
		
		if(isFDA){
			
			predicates = factoryFDA.getPredicatesFDAFeatures(feature);
			
		} else { 
				
			
			predicates = factorySDA.getPredicatesSDAFeatures(featureName);
			
			//TODO: Verify the need of manual input of geometry predicate
			Triple triple = new Triple();
			triple.setPredicate(GlobalSettings.getGeometryPredicate().replace("<", "").replace(">", ""));
			
			predicates.add(triple);
			
		}
		
		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			
			Document document = documentBuilder.parse("wfs/DescribeFeature_100.xml");
					
			logger.info("Creating DescribeFeatureType XML document for [" + feature.getName() + "] ...");

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
								
				/**
				 * Checks if predicate is the chosen geometry predicate in the settings file.
				 */
				if(predicates.get(i).getPredicate().equals(GlobalSettings.getGeometryPredicate().replaceAll("<", "").replace(">", ""))){
					String featureType = new String();
					featureType = factorySDA.getFeatureType(featureName);
			
					if(featureType.equals("gml:MultiPolygon") || featureType.equals("gml:Polygon")){
						sequence.setAttribute("type","gml:MultiPolygonPropertyType");
					}

					if(featureType.equals("gml:LineString")){
						sequence.setAttribute("type","gml:MultiLineStringPropertyType");
					}

					if(featureType.equals("gml:Point") || featureType.equals("gml:MultiPoint") ){
						sequence.setAttribute("type","gml:MultiPointPropertyType");
					}


				} else if(isFDA && (predicates.get(i).getPredicate().equals(feature.getGeometryVariable()))){
					
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
	 * @see OGC Specification for WFS http://www.opengeospatial.org/standards/wfs
	 * @param feature geographic feature to be retrieved.
	 * @return XML Document containing the WFS GetFeature response with all geometries of a given feature together with their attribute table.
	 */
	public String getFeature(WFSFeature feature) {

		String getFeatureResponse = new String();
		String layerPrefix = new String();
		Query query = new Query();
		
		ArrayList<Triple> predicates = new ArrayList<Triple>();					
		ResultSet rs;
				
		if(isFDAFeature(feature)){
			
			logger.info("Performing query at " + feature.getEndpoint()  + " to retrieve all geometries of [" + feature.getName() + "]  ...");
			predicates = factoryFDA.getPredicatesFDAFeatures(feature);
			
			query = QueryFactory.create(feature.getQuery().toString());
			rs = jn.executeQuery(feature.getQuery().toString(),feature.getEndpoint());
						
		} else {
		
			logger.info("Performing query at " + GlobalSettings.default_SPARQLEndpoint  + " to retrieve all geometries of [" + feature.getName() + "] ...");
			
			String featureName = modelFeatures.expandPrefix(feature.getName());
			predicates = factorySDA.getPredicatesSDAFeatures(featureName);	
					
			query = QueryFactory.create(factorySDA.generateGetFeatureSPARQL(featureName, predicates));
			rs = jn.executeQuery(factorySDA.generateGetFeatureSPARQL(featureName, predicates),GlobalSettings.default_SPARQLEndpoint);
			
		}
		
		layerPrefix = modelFeatures.shortForm(feature.getName());
		layerPrefix = layerPrefix.substring(0,layerPrefix.indexOf(":"));
		
		
		if(feature.getOutputFormat().equals("xml")){
		
			try {
	
				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder;
	
				documentBuilder = documentBuilderFactory.newDocumentBuilder();
				Document document = documentBuilder.parse("wfs/GetFeature_100.xml");
	
				/**
				 * Build Name Spaces in the XML header.
				 */
				Element requestElement = document.getDocumentElement(); 
				
				for (Map.Entry<String, String> entry : modelFeatures.getNsPrefixMap().entrySet())
				{
					requestElement.setAttribute("xmlns:" + entry.getKey(), entry.getValue());
				}			
				
				long countIteration = 0;
				
				logger.info("Creating GetFeature XML document for " + feature.getName() + "...");
	
				XPath xpath = XPathFactory.newInstance().newXPath();
				NodeList myNodeList = (NodeList) xpath.compile("//FeatureCollection/text()").evaluate(document, XPathConstants.NODESET);           
	
				
				if(!isFDAFeature(feature)){
					Triple triple = new Triple();
					triple.setPredicate(GlobalSettings.getGeometryPredicate().replaceAll("<", "").replace(">", ""));
					predicates.add(triple);		
				}
				
	
				while (rs.hasNext()) {
					countIteration++;
					
					QuerySolution soln = rs.nextSolution();
					String currentGeometryName = "GEO_";
					Element currentGeometryElement = document.createElement(modelFeatures.shortForm(feature.getName()));
					
					
					currentGeometryElement.setAttribute("fid", currentGeometryName + "" + countIteration);				
	
					Element rootGeometry = document.createElement("gml:featureMember");
									
					
					for (int i = 0; i < predicates.size(); i++) {
	
						if(isFDAFeature(feature)){
							
							Element elementGeometryPredicate = document.createElement(layerPrefix + ":" + predicates.get(i).getPredicate());
													
							
							if(predicates.get(i).getPredicate().equals(feature.getGeometryVariable())){														
	
								//TODO: Check if literal is already GML
								
								String gml = Utils.convertWKTtoGML(soln.getLiteral("?"+feature.getGeometryVariable()).getString());
								
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
							
							if (predicates.get(i).getPredicate().equals(GlobalSettings.getGeometryPredicate().replaceAll("<", "").replace(">", ""))) {
														
								if(!FactoryFDAFeatures.getGeometryType(soln.getLiteral("?" + GlobalSettings.getGeometryVariable()).getString()).equals("INVALID")){
																
									String gml = Utils.convertWKTtoGML(soln.getLiteral("?"+GlobalSettings.getGeometryVariable()).getString());											
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

		}
		
		/*
		 * Geometries are not enconded to 
		 */
		
		
		
		if(feature.getOutputFormat().equals("json")){
			
			StringBuilder json = new StringBuilder();
			
			String jsonEntries = new String();
						
						
			
			
			if(!isFDAFeature(feature)){
				
				Triple triple = new Triple();
				triple.setPredicate(GlobalSettings.getGeometryPredicate().replaceAll("<", "").replace(">", ""));
				predicates.add(triple);	
				
			}
			
			jsonEntries = "[\n";
			
			logger.info("Creating JSON document for [" + feature.getName() + "]...");
			
			while (rs.hasNext()) {
			
				QuerySolution soln = rs.nextSolution();
				
				jsonEntries = jsonEntries + " {\n";
				for (int i = 0; i < predicates.size(); i++) {

					//if(isFDAFeature(feature)){

					if(soln.get("?"+predicates.get(i).getPredicate()).isLiteral()){
						
						

						if(soln.getLiteral("?"+predicates.get(i).getPredicate()).getDatatype() != null){

							
							/**
							 * Checks if the literal is of type integer, long, byte or decimal.
							 */
							if(soln.getLiteral("?"+predicates.get(i).getPredicate()).getDatatypeURI().trim().equals(GlobalSettings.getDefaultDecimalType()) ||
							   soln.getLiteral("?"+predicates.get(i).getPredicate()).getDatatypeURI().trim().equals(GlobalSettings.getDefaultLongType()) ||
							   soln.getLiteral("?"+predicates.get(i).getPredicate()).getDatatypeURI().trim().equals(GlobalSettings.getDefaultIntegerType()) ||
							   soln.getLiteral("?"+predicates.get(i).getPredicate()).getDatatypeURI().trim().equals(GlobalSettings.getDefaultByteType())) {
																
								jsonEntries = jsonEntries + "  \"" + predicates.get(i).getPredicate().toString() +
										  "\": " + soln.getLiteral("?" + predicates.get(i).getPredicate()).getValue();

							}

						} else {
						
							jsonEntries = jsonEntries + "  \"" + predicates.get(i).getPredicate().toString() +
									  "\": \"" + soln.getLiteral("?" + predicates.get(i).getPredicate()).getValue().toString().replace("\"", "'") + "\"";
						}
						
					} else {
						
							jsonEntries = jsonEntries + "  \"" + predicates.get(i).getPredicate().toString() +
								  "\": \"" + soln.get("?" + predicates.get(i).getPredicate()).toString().replace("\"", "'") + "\"";

					}
					
					
					if(i != predicates.size()-1){
						jsonEntries = jsonEntries + ",\n";
					}


				}
			
				if(rs.hasNext()){
					jsonEntries = jsonEntries + "\n },\n";
				} else {
					jsonEntries = jsonEntries + "\n }\n";
				}
			}
        	
			
        	jsonEntries = jsonEntries + "\n]";
        	
        	json.append(jsonEntries);
        	
        	getFeatureResponse = json.toString();
        			
		}
		
		
		
		
		if(feature.getOutputFormat().equals("geojson")){
			
			StringBuilder geojson2 = new StringBuilder();
			
			String properties = new String();
						
			int counter=0;
			
			geojson2.append("{\"type\":\"FeatureCollection\",\"totalFeatures\":[PARAM_FEATURES],\"features\":[\n") ;
			
			if(!isFDAFeature(feature)){
				
				Triple triple = new Triple();
				triple.setPredicate(GlobalSettings.getGeometryPredicate().replaceAll("<", "").replace(">", ""));
				predicates.add(triple);	
				
			}
			
			logger.info("Creating GeoJSON document for [" + feature.getName() + "]...");
			
	        while (rs.hasNext()) {
	            
	        	counter++;
	            QuerySolution soln = rs.nextSolution();
	            geojson2.append("{\"type\":\"Feature\",\"id\":\"FEATURE_"+ counter +"\",\"geometry\":");
	        	properties = "\"properties\": {";
	        	
	        	for (int i = 0; i < predicates.size(); i++) {
	        		
		        	if(isFDAFeature(feature)){
		        		
		        		if(predicates.get(i).getPredicate().equals(feature.getGeometryVariable())){
		        			        		
		        			geojson2.append(Utils.convertWKTtoGeoJSON(soln.getLiteral("?"+feature.getGeometryVariable()).getString()));
		        			
		        		} else {
		        			
		        			properties = properties + "\"" +predicates.get(i).getPredicate().toString()+
		        					     "\": \""+soln.get("?"+predicates.get(i).getPredicate()).toString().replace("\"", "'")+"\",";
		        			
		        			
		        		}
		        		
		        	} else {
		        		
		        		
		        		if (predicates.get(i).getPredicate().equals(GlobalSettings.getGeometryPredicate().replaceAll("<", "").replace(">", ""))) {
							
							if(!FactoryFDAFeatures.getGeometryType(soln.getLiteral("?" + GlobalSettings.getGeometryVariable()).getString()).equals("INVALID")){

								geojson2.append(Utils.convertWKTtoGeoJSON(soln.getLiteral("?"+GlobalSettings.getGeometryVariable()).getString()) +",");
							
							} else {
								
			        			properties = properties + "\"" +predicates.get(i).getPredicate().toString()+
		        					         "\": \""+soln.getLiteral("?"+GlobalSettings.getGeometryVariable()).getString().replace("\"", "'")+"\",";

							}
		        		}
		        	}
		        	
	        	}
	        	
	        	properties= properties.substring(0, properties.length()-1);
	        	geojson2.append(properties+"}},");
	           
	        }
			
	        geojson2.deleteCharAt(geojson2.length()-1);
	        geojson2.append("]}");
			
	        getFeatureResponse = geojson2.toString().replace("[PARAM_FEATURES]", Integer.toString(counter));
	        	       
	        
		}
		
		
		logger.info("GeoJSON document for [" + feature.getName() + "] successfully created.");
		
		return getFeatureResponse;

	}
	
	

	
	
	
	
	/**
	 ** Private Methods.
	 **/
	
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
				
				if (features.get(i).isFDAFeature()){
					
					modelFeatures.setNsPrefix(GlobalSettings.getFDAPrefix(), features.get(i).getName().substring(0, position+1) );
					
				} else {
					
					modelFeatures.setNsPrefix(GlobalSettings.getSDAPrefix() + modelFeatures.getNsPrefixMap().size(), features.get(i).getName().substring(0, position+1) );
			
				}
			}
			
		}
		
	}
		
	 /** 
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
	
	private ArrayList<WFSFeature> listFeatures(){
				
		ArrayList<WFSFeature> result = new ArrayList<WFSFeature>();

		/**
		 * Adding FDA Features to the Capabilities Document.
		 */
		fdaFeatures = factoryFDA.listFDAFeatures(GlobalSettings.getSparqlDirectory());
		
		for (int i = 0; i < fdaFeatures.size(); i++) {
			
			result.add(fdaFeatures.get(i));

		}
		
		/**
		 * Adding SDA Features to the Capabilities Document. 
		 */		 
		sdaFeatures = factorySDA.listSDAFeatures();
		
		for (int i = 0; i < sdaFeatures.size(); i++) {
			
			result.add(sdaFeatures.get(i));

		}
				
		return result;
	}
			
	private boolean isFDAFeature(WFSFeature feature){
		
		boolean result = false;
		
		/**
		 * Checks if the selected layer is created via FDA (based on pre-defined SPARQL Query)
		 */
		for (int i = 0; i < fdaFeatures.size(); i++) {
			
			if(fdaFeatures.get(i).getName().equals(modelFeatures.expandPrefix(feature.getName()))){
				result = true; 
				feature.setQuery(fdaFeatures.get(i).getQuery());
				feature.setGeometryVariable(fdaFeatures.get(i).getGeometryVariable());
				feature.setEndpoint(fdaFeatures.get(i).getEndpoint());
				
			}
			
		}
		
		return result;
		
	}

	private String removePredicateURL(String predicate){
		
		return predicate.split("\\P{Alpha}+")[predicate.split("\\P{Alpha}+").length-1];
						
	}

}
