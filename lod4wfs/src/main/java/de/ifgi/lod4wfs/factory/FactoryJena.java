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

public class FactoryJena {

	private static JenaConnector jn;
	private static Model modelNameSpaces = ModelFactory.createDefaultModel();
	private static Model modelLayers;	
	private static Logger logger = Logger.getLogger("Factory");
	
	public FactoryJena(){
		jn = new JenaConnector(GlobalSettings.SPARQL_Endpoint);

		//TODO Fix redundancy of GlobalSettings prefixes with ModelFactory prefixes
		modelNameSpaces.setNsPrefix("xsd", GlobalSettings.xsdNameSpace );        
		modelNameSpaces.setNsPrefix("sf", GlobalSettings.sfNameSpace );
		modelNameSpaces.setNsPrefix("dc", GlobalSettings.dublinCoreNameSpace );
		modelNameSpaces.setNsPrefix("geo", GlobalSettings.geoSPARQLNameSpace );
		modelNameSpaces.setNsPrefix("rdf", GlobalSettings.RDFNameSpace);
		modelNameSpaces.setNsPrefix("dct", GlobalSettings.dublinCoreTermsNameSpace);

	}

	private ArrayList<GeographicLayer> listGeographicLayers(){

		logger.info("Listing geographic layers (Named Graphs at " + GlobalSettings.SPARQL_Endpoint + " ...");
		
		ResultSet rs = jn.executeQuery(SPARQL.listNamedGraphs);
		
		ArrayList<GeographicLayer> result = new ArrayList<GeographicLayer>();
		

		while (rs.hasNext()) {
			GeographicLayer layer = new GeographicLayer();
			QuerySolution soln = rs.nextSolution();
			layer.setName(soln.get("?graphName").toString());
			layer.setTitle(soln.getLiteral("?title").getValue().toString());
			layer.setFeatureAbstract(soln.getLiteral("?abstract").getValue().toString());
			layer.setKeywords(soln.getLiteral("?keywords").getValue().toString());
			layer.setLowerCorner(GlobalSettings.defaultLowerCorner);
			layer.setUpperCorner(GlobalSettings.defaultUpperCorner);
			layer.setDefaultCRS(GlobalSettings.defautlCRS);
			result.add(layer);
			
		}
		
		return result;
	}

	public String getCapabilities(String version){

		String resultCapabilities = new String();

		ArrayList<GeographicLayer> layers = new ArrayList<GeographicLayer>(); 
		layers = this.listGeographicLayers();
		this.generateLayersPrefixes(layers);
		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			if (version.equals("1.0.0")) {

				Document document = documentBuilder.parse("src/main/resources/CapabilitiesDocument_100.xml");
				

				/**
				 * Iterates through the layers' model and creates namespaces entries with the layers prefixes.
				 */
				Element requestElement = document.getDocumentElement(); 
								
				for (Map.Entry<String, String> entry : modelLayers.getNsPrefixMap().entrySet())
				{
					requestElement.setAttribute("xmlns:" + entry.getKey(), entry.getValue());
				}

				
				logger.info("Creating Capabilities Document...");

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
					SRS.appendChild(document.createTextNode(GlobalSettings.defautlCRS));
					//	Element BBOX = document.createElement("LatLongBoundingBox maxx=\"-73.90782\" maxy=\"40.882078\" minx=\"-74.047185\" miny=\"40.679648\"");
					//	BBOX.appendChild(document.createTextNode(""));


					Element p = document.createElement("FeatureType");
					p.appendChild(name);
					p.appendChild(title);
					p.appendChild(featureAbstract);
					p.appendChild(keywords);
					p.appendChild(SRS);
			        
					myNodeList.item(1).getParentNode().insertBefore(p, myNodeList.item(1));

				}
				
					        
				
//				DOMSource source = new DOMSource(document);
//
//				TransformerFactory transformerFactory = TransformerFactory.newInstance();
//				Transformer transformer = transformerFactory.newTransformer();
//				
//				StringWriter sw = new StringWriter();
//
//				StreamResult result = new StreamResult(sw);
//				transformer.transform(source, result);
//
//				StringBuffer sb = sw.getBuffer();
//
//				resultCapabilities = sb.toString(); //FileUtils.readWholeFileAsUTF8("src/main/resources/CapabilitiesDocument_100.xml");

				resultCapabilities = this.printXMLDocument(document);
			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return resultCapabilities;

	}

	// TODO Check need for loading capabilities document by start-up.
	// TODO Fix dependency on the commented geometry on the XML File DescribeFeatureType_100.

	public String describeFeatureType(GeographicLayer layer){

		String describeFeatureTypeResponse = new String(); 
		ArrayList<Triple> predicates = new ArrayList<Triple>();
		predicates = this.getGeometriesPredicates(layer);

		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			
			Document document = documentBuilder.parse("src/main/resources/DescribeFeature_100.xml");
			
			//TODO implement prefixes namespaces from layers model 
			
			logger.info("Creating DescribeFeatureType XML document for " + layer.getName() + " ...");

			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList myNodeList = (NodeList) xpath.compile("//extension/sequence/text()").evaluate(document, XPathConstants.NODESET);           

			for (int i = 0; i < predicates.size(); i++) {

				String predicateWithoutPrefix = new String();
				predicateWithoutPrefix =  predicates.get(i).getPredicate().substring(predicates.get(i).getPredicate().indexOf(":")+1, predicates.get(i).getPredicate().length());
				
				Element requestElement = document.getDocumentElement(); 
				
				for (Map.Entry<String, String> entry : modelLayers.getNsPrefixMap().entrySet())
				{
					requestElement.setAttribute("xmlns:" + entry.getKey(), entry.getValue());
				}
				
				Element sequence = document.createElement("xsd:element");
				sequence.setAttribute("maxOccurs","1");
				sequence.setAttribute("minOccurs","0");
				sequence.setAttribute("name",predicateWithoutPrefix);
				sequence.setAttribute("nillable","true");

				//TODO Try to fix hardcoded geo:asWKT to the describeFeature operation 

				if(predicates.get(i).getPredicate().equals("geo:asWKT")){
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

				myNodeList.item(1).getParentNode().insertBefore(sequence, myNodeList.item(0));

			}

			
			
//			DOMSource source = new DOMSource(document);
//			TransformerFactory transformerFactory = TransformerFactory.newInstance();
//			Transformer transformer = transformerFactory.newTransformer();
//			StringWriter stringWriter = new StringWriter();
//
//			StreamResult result = new StreamResult(stringWriter);
//			transformer.transform(source, result);
//
//			StringBuffer stringBuffer = stringWriter.getBuffer();

			describeFeatureTypeResponse = this.printXMLDocument(document);

			describeFeatureTypeResponse = describeFeatureTypeResponse.replace("PARAM_NAME", layer.getName().substring(layer.getName().indexOf(":")+1, layer.getName().length()));
			describeFeatureTypeResponse = describeFeatureTypeResponse.replace("PARAM_SERVER_PORT", Integer.toString(GlobalSettings.defaultPort));
			describeFeatureTypeResponse = describeFeatureTypeResponse.replace("PARAM_SERVICE", GlobalSettings.defaultServiceName);
			describeFeatureTypeResponse = describeFeatureTypeResponse.replace("PARAM_SERVER", java.net.InetAddress.getLocalHost().getHostName());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return describeFeatureTypeResponse;


	}

	public ArrayList<Triple> getGeometriesPredicates(GeographicLayer layer){

		logger.info("Listing available predicates for " + layer.getName() + " ...");
		
		ResultSet rs = jn.executeQuery(SPARQL.listGeometryPredicates.replace("PARAM_LAYER", modelLayers.expandPrefix(layer.getName())));
		ArrayList<Triple> result = new ArrayList<Triple>();		

		while (rs.hasNext()) {

			Triple triple = new Triple();

			QuerySolution soln = rs.nextSolution();
			triple.setPredicate(modelNameSpaces.shortForm(soln.getResource("?predicate").toString()));

			if (soln.get("?dataType")==null) {

				triple.setObjectDataType(GlobalSettings.defaultLiteralType);

			} else {

				triple.setObjectDataType(modelNameSpaces.shortForm(soln.getResource("?dataType").getURI()));
			}

			result.add(triple);			   
		}

		return result;
	}

	private String getFeatureType (GeographicLayer layer){

		logger.info("Getting geometry type for " + layer.getName() + " ...");
		
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

	public String getFeature(GeographicLayer layer) {

		String getFeatureResponse = new String();
		ArrayList<Triple> predicates = new ArrayList<Triple>();
		predicates = this.getGeometriesPredicates(layer);
		
		logger.info("Performing query at " + GlobalSettings.SPARQL_Endpoint  + " to retrieve all geometries of " + layer.getName() + "  ...");
		
		ResultSet rs = jn.executeQuery(SPARQL.prefixes +" \n" + this.generateGetFeatureSPARQL(layer, predicates));


		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder;

			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse("src/main/resources/GetFeature_100.xml");


			long countIteration = 0;
			
			logger.info("Creating GetFeature XML document for " + layer.getName() + "...");

			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList myNodeList = (NodeList) xpath.compile("//FeatureCollection/text()").evaluate(document, XPathConstants.NODESET);           

			while (rs.hasNext()) {
				countIteration++;
				
				QuerySolution soln = rs.nextSolution();
				
				String currentGeometryName = GlobalSettings.defaultServiceName + ":" + soln.getResource("?geometry").getLocalName();
				
				Element currentGeometryElement = document.createElement(currentGeometryName);
				currentGeometryElement.setAttribute("fid", currentGeometryName + "." + countIteration);				

				Element rootGeometry = document.createElement("gml:featureMember");
				
				for (int i = 0; i < predicates.size(); i++) {

					String predicateWithoutPrefix = new String();
					
					predicateWithoutPrefix =  predicates.get(i).getPredicate().substring(predicates.get(i).getPredicate().indexOf(":")+1, predicates.get(i).getPredicate().length());
					Element elementGeometryPredicate = document.createElement(GlobalSettings.defaultServiceName + ":" + predicateWithoutPrefix);

					//TODO Fix hardcoded geo:asWKT 

					if (predicates.get(i).getPredicate().equals("geo:asWKT")) {
					
						String gml = this.convertWKTtoGML(soln.getLiteral("?asWKT").getString().toString());
											
						Element GMLnode =  documentBuilder.parse(new ByteArrayInputStream(gml.getBytes())).getDocumentElement();		

						Node dup = document.importNode(GMLnode, true);

						elementGeometryPredicate.appendChild(dup);
						
						rootGeometry.appendChild(elementGeometryPredicate);
												
						currentGeometryElement.appendChild(elementGeometryPredicate);
						
						rootGeometry.appendChild(currentGeometryElement);
						
						
	
					} else {

						Element elementAttribute = document.createElement(GlobalSettings.defaultServiceName + ":" + predicateWithoutPrefix);
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

	private String generateGetFeatureSPARQL(GeographicLayer layer, ArrayList<Triple> predicates){

		String selectClause = new String();
		String whereClause = new String();

		for (int i = 0; i < predicates.size(); i++) {

			String tmp = new String();

			tmp= predicates.get(i).getPredicate().substring(predicates.get(i).getPredicate().indexOf(":")+1, predicates.get(i).getPredicate().length()) ;

			selectClause = selectClause + " ?" + tmp + " \n" ;
			whereClause = whereClause + "?geometry " + predicates.get(i).getPredicate() + " ?" + tmp +" .\n"; 

		}

		String SPARQL = new String();

		SPARQL = " SELECT ?geometry \n" + selectClause +
				" WHERE { GRAPH <"+ modelLayers.expandPrefix(layer.getName()) + "> {" +
				"?geometry a geo:Geometry . \n" + whereClause + "}}";

		return SPARQL;
		
	}

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
					//System.out.println("Position: " + size);
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

	private String convertWKTtoGML(String wkt){
		
		String gml = new String();
		
		try {
		
			gml = WKTParser.parseToGML2(wkt);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return gml;
		
	}

}
