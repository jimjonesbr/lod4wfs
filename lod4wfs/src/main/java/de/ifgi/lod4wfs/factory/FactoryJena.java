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
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import de.ifgi.lod4wfs.core.SPARQL;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.core.GeographicLayer;
import de.ifgi.lod4wfs.core.Triple;
import de.ifgi.lod4wfs.infrastructure.JenaConnector;

/**
 * 
 * @author jones
 * @version 1.0
 * Class responsible for establishing communication with the triple store and providing the XML documents to the facade.
 */

public class FactoryJena {

	private static JenaConnector jn;
	private static Model modelNameSpaces = ModelFactory.createDefaultModel();
	private static Model modelLayers;	
	
	private static Logger logger = Logger.getLogger("Factory");
	
	public FactoryJena(){
		jn = new JenaConnector(GlobalSettings.default_SPARQLEndpoint);
					
		modelNameSpaces.setNsPrefix("xsd", GlobalSettings.xsdNameSpace );        
		modelNameSpaces.setNsPrefix("sf", GlobalSettings.sfNameSpace );
		modelNameSpaces.setNsPrefix("dc", GlobalSettings.dublinCoreNameSpace );
		modelNameSpaces.setNsPrefix("geo", GlobalSettings.geoSPARQLNameSpace );
		modelNameSpaces.setNsPrefix("rdf", GlobalSettings.RDFNameSpace);
		modelNameSpaces.setNsPrefix("dct", GlobalSettings.dublinCoreTermsNameSpace);
		modelNameSpaces.setNsPrefix("vocab", GlobalSettings.testVocabulary);

		//Load variables defined at the settings file.
		GlobalSettings.loadVariables();
	}

	/**
	 * 
	 * @param model
	 * @return Prefix header of all vocabularies used in the system, to be used in SPARQL Queries.
	 */
	private String getPrefixes(Model model){
		
		String prefixes = new String();
		for (Map.Entry<String, String> entry : modelNameSpaces.getNsPrefixMap().entrySet())
		{
			prefixes = prefixes + "PREFIX " + entry.getKey() + ": <" + entry.getValue() + "> \n";
		}
		
		return prefixes;
	}
	
	/**
	 * 
	 * @return A list of all geographic layers of a given SPARQL Endpoint.
	 */
	
	private ArrayList<GeographicLayer> listGeographicLayers(){

		logger.info("Listing geographic layers at " + GlobalSettings.default_SPARQLEndpoint + " ...");
		
		//ResultSet rs = jn.executeQuery(this.getPrefixes(modelNameSpaces) + SPARQL.listNamedGraphs);
		ResultSet rs = jn.executeQuery(SPARQL.listNamedGraphs);
		
		ArrayList<GeographicLayer> result = new ArrayList<GeographicLayer>();
		
		String CRS = new String();
		
		while (rs.hasNext()) {
			GeographicLayer layer = new GeographicLayer();
			QuerySolution soln = rs.nextSolution();
			layer.setName(soln.get("?graphName").toString());
			layer.setTitle(soln.getLiteral("?title").getValue().toString());
			layer.setFeatureAbstract(soln.getLiteral("?abstract").getValue().toString());
			layer.setKeywords(soln.getLiteral("?keywords").getValue().toString());
			layer.setLowerCorner(GlobalSettings.defaultLowerCorner);
			layer.setUpperCorner(GlobalSettings.defaultUpperCorner);
						
			CRS = soln.get("?wkt").toString();

			if(CRS.contains("<") || CRS.contains(">")){
				
				CRS = CRS.substring(CRS.indexOf("<"), CRS.indexOf(">"));
				CRS = CRS.replace("http://www.opengis.net/def/crs/EPSG/0/", "EPSG:");
				
				CRS = CRS.replace("<", "");
				CRS = CRS.replace(">", "");
				
				layer.setDefaultCRS(CRS);
			
			} else {
			
				layer.setDefaultCRS(GlobalSettings.defautlCRS);
				
			}
			
			result.add(layer);
			
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

		ArrayList<GeographicLayer> layers = new ArrayList<GeographicLayer>(); 
		layers = this.listGeographicLayers();
		this.generateLayersPrefixes(layers);
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
								
				for (Map.Entry<String, String> entry : modelLayers.getNsPrefixMap().entrySet())
				{
					requestElement.setAttribute("xmlns:" + entry.getKey(), entry.getValue());
				}

				
				logger.info("Creating Capabilities Document of " + GlobalSettings.getCanonicalHostName() + ":" + GlobalSettings.defaultPort + "/" + GlobalSettings.defaultServiceName + "/wfs ...");

				XPath xpath = XPathFactory.newInstance().newXPath();
				NodeList myNodeList = (NodeList) xpath.compile("//FeatureTypeList/text()").evaluate(document, XPathConstants.NODESET);           

				for (int i = 0; i < layers.size(); i++) {
								
					Element name = document.createElement("Name");
					name.appendChild(document.createTextNode(modelLayers.shortForm(layers.get(i).getName())));
					Element title = document.createElement("Title");
					title.appendChild(document.createTextNode(layers.get(i).getTitle()));
					Element featureAbstract = document.createElement("Abstract");
					featureAbstract.appendChild(document.createTextNode(layers.get(i).getFeatureAbstract()));
					Element keywords = document.createElement("Keywords");
					keywords.appendChild(document.createTextNode(layers.get(i).getKeywords()));
					Element SRS = document.createElement("SRS");
					SRS.appendChild(document.createTextNode(layers.get(i).getCRS()));
					
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
	
	public String describeFeatureType(GeographicLayer layer){

		String describeFeatureTypeResponse = new String(); 
		ArrayList<Triple> predicates = new ArrayList<Triple>();
		predicates = this.getGeometriesPredicates(layer);

		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			
			//Document document = documentBuilder.parse("src/main/resources/wfs/DescribeFeature_100.xml");
			Document document = documentBuilder.parse("wfs/DescribeFeature_100.xml");
			
			//TODO implement prefixes namespaces from layers model 
			
			logger.info("Creating DescribeFeatureType XML document for " + layer.getName() + " ...");

			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList myNodeList = (NodeList) xpath.compile("//extension/sequence/text()").evaluate(document, XPathConstants.NODESET);           

			String layerPrefix = modelLayers.shortForm(layer.getName());
			layerPrefix = layerPrefix.substring(0,layerPrefix.indexOf(":")+1);			
						
			Element requestElement = document.getDocumentElement(); 						
			requestElement.setAttribute("targetNamespace", modelLayers.expandPrefix(layerPrefix));
			
			
			for (Map.Entry<String, String> entry : modelLayers.getNsPrefixMap().entrySet())
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
				sequence.setAttribute("name",predicateWithoutPrefix);
				sequence.setAttribute("nillable","true");

				//TODO Try to fix hardcoded geo:asWKT to the describeFeature operation 
				System.out.println("DELETE MEPredicates: " + predicates.get(i).getPredicate());
				
					
				//if(predicates.get(i).getPredicate().equals("geo:asWKT")){
				if(predicates.get(i).getPredicate().equals("http://www.opengis.net/ont/geosparql/1.0#asWKT")){
					String featureType = new String();
					featureType = this.getFeatureType(layer);
			
					if(featureType.equals("gml:MultiPolygon") || featureType.equals("gml:Polygon")){
						sequence.setAttribute("type","gml:MultiPolygonPropertyType");
					}

					if(featureType.equals("gml:LineString")){
						sequence.setAttribute("type","gml:MultiLineStringPropertyType");
					}

					if(featureType.equals("gml:Point") || featureType.equals("gml:MultiPoint") ){
						sequence.setAttribute("type","gml:MultiPointPropertyType");
					}


				} else {
					sequence.setAttribute("type",predicates.get(i).getObjectDataType());
				}

				myNodeList.item(0).getParentNode().insertBefore(sequence, myNodeList.item(0));

			}

			describeFeatureTypeResponse = this.printXMLDocument(document);

			describeFeatureTypeResponse = describeFeatureTypeResponse.replace("PARAM_NAME", layer.getName().substring(layer.getName().indexOf(":")+1, layer.getName().length()));
			describeFeatureTypeResponse = describeFeatureTypeResponse.replace("PARAM_TYPE", layer.getName());
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
	 * @param layer geographic feature 
	 * @return Lists all predicates (properties) related to a given feature.  
	 */
	private ArrayList<Triple> getGeometriesPredicates(GeographicLayer layer){

		logger.info("Listing available predicates for " + layer.getName() + " ...");
		
		//ResultSet rs = jn.executeQuery(this.getPrefixes(modelNameSpaces) + SPARQL.listGeometryPredicates.replace("PARAM_LAYER", modelLayers.expandPrefix(layer.getName())));
		ResultSet rs = jn.executeQuery(SPARQL.listGeometryPredicates.replace("PARAM_LAYER", modelLayers.expandPrefix(layer.getName())));
		ArrayList<Triple> result = new ArrayList<Triple>();		

		System.out.println("DELETE ME: " + SPARQL.listGeometryPredicates.replace("PARAM_LAYER", modelLayers.expandPrefix(layer.getName())));
		
		while (rs.hasNext()) {

			Triple triple = new Triple();

			QuerySolution soln = rs.nextSolution();
			//triple.setPredicate(modelNameSpaces.shortForm(soln.getResource("?predicate").toString()));
			triple.setPredicate(soln.getResource("?predicate").toString());

			System.out.println("DELETE ME  predicate string -->" + soln.getResource("?predicate").toString());
			
			if (soln.get("?dataType")==null) {

				triple.setObjectDataType(GlobalSettings.defaultLiteralType);

			} else {

				triple.setObjectDataType(modelNameSpaces.shortForm(soln.getResource("?dataType").getURI()));
			}

			result.add(triple);			   
		}

		return result;
	}

	/**
	 * @param layer geographic feature 
	 * @return Data type of a given feature.
	 */
	private String getFeatureType (GeographicLayer layer){

		logger.info("Getting geometry type for " + layer.getName() + " ...");
		
		//ResultSet rs = jn.executeQuery(this.getPrefixes(modelNameSpaces) + SPARQL.getFeatureType.replace("PARAM_LAYER", modelLayers.expandPrefix(layer.getName())));
		ResultSet rs = jn.executeQuery(SPARQL.getFeatureType.replace("PARAM_LAYER", modelLayers.expandPrefix(layer.getName())));
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return geometryCoordinates;
	}

	/**
	 * @see OGC Specification for WFS http://www.opengeospatial.org/standards/wfs
	 * @param layer geographic feature to be retrieved.
	 * @return XML Document containing the WFS GetFeature response with all geometries of a given feature together with their attribute table.
	 */
	public String getFeature(GeographicLayer layer) {

		String getFeatureResponse = new String();
		String layerPrefix = new String();
		ArrayList<Triple> predicates = new ArrayList<Triple>();
		predicates = this.getGeometriesPredicates(layer);
		
		logger.info("Performing query at " + GlobalSettings.default_SPARQLEndpoint  + " to retrieve all geometries of " + layer.getName() + "  ...");
		
		System.out.println("DELETE ME GetFeature: " + this.generateGetFeatureSPARQL(layer, predicates));
		
		ResultSet rs = jn.executeQuery(this.getPrefixes(modelNameSpaces) + " \n" + this.generateGetFeatureSPARQL(layer, predicates));
			
		layerPrefix = modelLayers.shortForm(layer.getName());
		layerPrefix = layerPrefix.substring(0,layerPrefix.indexOf(":"));
		
		
		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder;

			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			//Document document = documentBuilder.parse("src/main/resources/wfs/GetFeature_100.xml");
			Document document = documentBuilder.parse("wfs/GetFeature_100.xml");

			
			//
			//Build Name Spaces in the XML header.
			Element requestElement = document.getDocumentElement(); 
			
			for (Map.Entry<String, String> entry : modelLayers.getNsPrefixMap().entrySet())
			{
				requestElement.setAttribute("xmlns:" + entry.getKey(), entry.getValue());
			}			
			
			//
			

			long countIteration = 0;
			
			logger.info("Creating GetFeature XML document for " + layer.getName() + "...");

			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList myNodeList = (NodeList) xpath.compile("//FeatureCollection/text()").evaluate(document, XPathConstants.NODESET);           

			while (rs.hasNext()) {
				countIteration++;
				
				QuerySolution soln = rs.nextSolution();
				
				//String currentGeometryName = GlobalSettings.defaultServiceName + ":" + soln.getResource("?geometry").getLocalName();				
				//Element currentGeometryElement = document.createElement(currentGeometryName);
				
				
				String currentGeometryName = soln.getResource("?geometry").getLocalName();
				Element currentGeometryElement = document.createElement(modelLayers.shortForm(layer.getName()));
				
				
				currentGeometryElement.setAttribute("fid", currentGeometryName + "." + countIteration);				

				Element rootGeometry = document.createElement("gml:featureMember");
				
				for (int i = 0; i < predicates.size(); i++) {

					String predicateWithoutPrefix = new String();
					
					//predicateWithoutPrefix =  predicates.get(i).getPredicate().substring(predicates.get(i).getPredicate().indexOf(":")+1, predicates.get(i).getPredicate().length());
					
					predicateWithoutPrefix =  this.removePredicateURL(predicates.get(i).getPredicate());
					
					//Element elementGeometryPredicate = document.createElement(GlobalSettings.defaultServiceName + ":" + predicateWithoutPrefix);
					Element elementGeometryPredicate = document.createElement(layerPrefix + ":" + predicateWithoutPrefix);
					
										
					
					//TODO Fix hardcoded geo:asWKT 
					
					//if (predicates.get(i).getPredicate().equals("geo:asWKT")) {
					if (predicates.get(i).getPredicate().equals("http://www.opengis.net/ont/geosparql/1.0#asWKT")) {
						String gml = this.convertWKTtoGML(soln.getLiteral("?asWKT").getString().toString());
											
						Element GMLnode =  documentBuilder.parse(new ByteArrayInputStream(gml.getBytes())).getDocumentElement();		

						Node dup = document.importNode(GMLnode, true);

						elementGeometryPredicate.appendChild(dup);
						
						rootGeometry.appendChild(elementGeometryPredicate);
												
						currentGeometryElement.appendChild(elementGeometryPredicate);
						
						rootGeometry.appendChild(currentGeometryElement);
						
						System.out.println("Geometry found!");						
	
					} else {

						Element elementAttribute = document.createElement(layerPrefix + ":" + predicateWithoutPrefix);
						elementAttribute.appendChild(document.createCDATASection(modelNameSpaces.shortForm(soln.get("?"+predicateWithoutPrefix).toString())));
					
						currentGeometryElement.appendChild(elementAttribute);
						
					}

					myNodeList.item(1).getParentNode().insertBefore(rootGeometry, myNodeList.item(1));

				}

				
			}

			logger.info("XML Document with " + countIteration + " features successfully created.");
			
//			DOMSource source = new DOMSource(document);
//
//			TransformerFactory transformerFactory = TransformerFactory.newInstance();
//			Transformer transformer = transformerFactory.newTransformer();
//			
//			StringWriter stringWriter = new StringWriter();			
//			StreamResult result = new StreamResult(stringWriter);
//			
//			
//			transformer.transform(source, result);
//			StringBuffer stringBuffer = stringWriter.getBuffer();
//			
//
//			getFeatureResponse = stringBuffer.toString();

			getFeatureResponse = this.printXMLDocument(document);
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return getFeatureResponse;

	}

	/**
	 * @param layer geographic feature
	 * @param predicates list of predicates from the feature of interest.
	 * @return SPARQL query for retrieving all geometries of a given feature and their related attributes.
	 */
	private String generateGetFeatureSPARQL(GeographicLayer layer, ArrayList<Triple> predicates){

		String selectClause = new String();
		String whereClause = new String();
		ArrayList<String> variables = new ArrayList<String>();
		
		for (int i = 0; i < predicates.size(); i++) {

			String SPARQL_Variable = new String();
			SPARQL_Variable = this.removePredicateURL(predicates.get(i).getPredicate());
			
		
			//tmp = predicates.get(i).getPredicate().substring(predicates.get(i).getPredicate().indexOf(":")+1, predicates.get(i).getPredicate().length()) ;
			
			//Check if more than one variable with the same name is generated.
			if(variables.contains(SPARQL_Variable)){
				SPARQL_Variable = SPARQL_Variable + i;
			}
			
			selectClause = selectClause + " ?" + SPARQL_Variable + " \n" ;
			whereClause = whereClause + "?geometry <" + predicates.get(i).getPredicate() + "> ?" + SPARQL_Variable +" .\n"; 

			
			variables.add(SPARQL_Variable);
		}

		String SPARQL = new String();

		SPARQL = " SELECT ?geometry \n" + selectClause +
				" WHERE { GRAPH <"+ modelLayers.expandPrefix(layer.getName()) + "> {" +
				"?geometry a " + GlobalSettings.getGeometryClass() + " . \n" + whereClause + "}}";

		return SPARQL;
		
	}

	/**
	 * @param layers list of geographic features
	 * 
	 */
	//TODO implement a return type for generateLayersPrefixes(). Put value direct in a variable isn't recommended. 
	private void generateLayersPrefixes(ArrayList<GeographicLayer> layers){
		
		Pattern pattern = Pattern.compile("[^a-z0-9A-Z_]");
		modelLayers = ModelFactory.createDefaultModel();
		
		for (int i = 0; i < layers.size(); i++) {

			boolean scape = false;

			int size = layers.get(i).getName().length()-1;
			int position = 0;

			while ((scape == false) && (size >= 0)) {

				Matcher matcher = pattern.matcher(Character.toString(layers.get(i).getName().charAt(size)));

				boolean finder = matcher.find();

				if (finder==true) {

					position = size;
					scape=true;
					
				}

				size--;
			}
			
			if (modelLayers.getNsURIPrefix(layers.get(i).getName().substring(0, position+1))==null) {
				
				modelLayers.setNsPrefix("gl"+ modelLayers.getNsPrefixMap().size(), layers.get(i).getName().substring(0, position+1) );	
			
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return XMLString;
	}

	/**
	 * 
	 * @param wkt Well Known Text geometry to be converted.
	 * @return GML encoding of a given Well Known Text literal.
	 */
	private String convertWKTtoGML(String wkt){
		
		String gml = new String();
		
		try {
		
			if(wkt.contains("<") && wkt.contains(">")){
				String CRS = new String();
				
				CRS = wkt.substring(wkt.indexOf("<") + 1, wkt.indexOf(">"));// .replace("http://www.opengis.net/def/crs/EPSG/0/", "EPSG:");
				
				wkt = wkt.substring(wkt.indexOf(">") + 1, wkt.length());
				
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
