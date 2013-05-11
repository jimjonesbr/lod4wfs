package de.ifgi.lod4wfs.factory;

import it.cutruzzula.lwkt.WKTParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
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
	private static Model model = ModelFactory.createDefaultModel();

	public FactoryJena(){
		jn = new JenaConnector(GlobalSettings.SPARQL_Endpoint);

		//TODO Fix redundancy of GlobalSettings prefixes with ModelFactory prefixes
		model.setNsPrefix("xsd", GlobalSettings.xsdNameSpace );        
		model.setNsPrefix("sf", GlobalSettings.sfNameSpace );
		model.setNsPrefix("dc", GlobalSettings.dublinCoreNameSpace );
		model.setNsPrefix("geo", GlobalSettings.geoSPARQLNameSpace );
		model.setNsPrefix("rdf", GlobalSettings.RDFNameSpace);
		model.setNsPrefix("dct", GlobalSettings.dublinCoreTermsNameSpace);

	}

	private ArrayList<GeographicLayer> listGeographicLayers(){

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

	public String createCapabilitiesDocument(String version){

		String resultCapabilities = new String();

		ArrayList<GeographicLayer> list = new ArrayList<GeographicLayer>(); 
		list = this.listGeographicLayers();

		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();


			if (version.equals("1.0.0")) {

				Document document = documentBuilder.parse("src/main/resources/CapabilitiesDocument_100.xml");

				for (int i = 0; i < list.size(); i++) {
								
					Element name = document.createElement("Name");
					name.appendChild(document.createTextNode(list.get(i).getName()));
					Element featureAbstract = document.createElement("Abstract");
					featureAbstract.appendChild(document.createTextNode(list.get(i).getFeatureAbstract()));
					Element title = document.createElement("Title");
					title.appendChild(document.createTextNode(list.get(i).getTitle()));
					Element keywords = document.createElement("Keywords");
					keywords.appendChild(document.createTextNode(list.get(i).getKeywords()));
					Element SRS = document.createElement("SRS");
					SRS.appendChild(document.createTextNode(GlobalSettings.defautlCRS));
					//	Element BBOX = document.createElement("LatLongBoundingBox maxx=\"-73.90782\" maxy=\"40.882078\" minx=\"-74.047185\" miny=\"40.679648\"");
					//	BBOX.appendChild(document.createTextNode(""));

					XPath xpath = XPathFactory.newInstance().newXPath();
					NodeList myNodeList = (NodeList) xpath.compile("//FeatureTypeList/text()").evaluate(document, XPathConstants.NODESET);           

					Element p = document.createElement("FeatureType");
					p.appendChild(name);
					p.appendChild(featureAbstract);
					p.appendChild(title);
					p.appendChild(keywords);
					p.appendChild(SRS);
					//p.appendChild(BBOX);
					myNodeList.item(1).getParentNode().insertBefore(p, myNodeList.item(1));

				}

				DOMSource source = new DOMSource(document);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				StringWriter sw = new StringWriter();

				StreamResult result = new StreamResult(sw);
				transformer.transform(source, result);

				StringBuffer sb = sw.getBuffer();

				resultCapabilities = sb.toString(); //FileUtils.readWholeFileAsUTF8("src/main/resources/CapabilitiesDocument_100.xml");

			}

			if (version.equals("2.0.0")) {

				//result = FileUtils.readWholeFileAsUTF8("src/main/resources/CapabilitiesDocument_200.xml");

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
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultCapabilities;

	}

	// TODO Check need for loading capabilities document by start-up.
	// TODO Fix dependency on the commented geometry on the XML File DescribeFeatureType_100.
	// TODO Implement standardized exception for wrong requests.
	// TODO Implement use of jogl for logging operations.

	public String describeFeatureType(GeographicLayer geographicLayer){

		String describeFeatureTypeResponse = new String(); 
		ArrayList<Triple> predicates = new ArrayList<Triple>();
		predicates = this.getGeometriesPredicates(geographicLayer);

		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse("src/main/resources/DescribeFeature_100.xml");

			for (int i = 0; i < predicates.size(); i++) {

				XPath xpath = XPathFactory.newInstance().newXPath();
				NodeList myNodeList = (NodeList) xpath.compile("//extension/sequence/text()").evaluate(document, XPathConstants.NODESET);           

				String predicateWithoutPrefix = new String();
				predicateWithoutPrefix =  predicates.get(i).getPredicate().substring(predicates.get(i).getPredicate().indexOf(":")+1, predicates.get(i).getPredicate().length());

				Element sequence = document.createElement("xsd:element");
				sequence.setAttribute("maxOccurs","1");
				sequence.setAttribute("minOccurs","0");
				sequence.setAttribute("name",predicateWithoutPrefix);
				sequence.setAttribute("nillable","true");

				//TODO Try to fix hardcoded geo:asWKT to the describeFeature operation 

				if(predicates.get(i).getPredicate().equals("geo:asWKT")){
					String featureType = new String();
					featureType = this.getFeatureType(geographicLayer);

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


			DOMSource source = new DOMSource(document);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			StringWriter stringWriter = new StringWriter();

			StreamResult result = new StreamResult(stringWriter);
			transformer.transform(source, result);

			StringBuffer stringBuffer = stringWriter.getBuffer();


			describeFeatureTypeResponse = stringBuffer.toString();

			//describeFeatureTypeResponse = FileUtils.readWholeFileAsUTF8("src/main/resources/DescribeFeature_100.xml");
			describeFeatureTypeResponse = describeFeatureTypeResponse.replace("PARAM_NAME", geographicLayer.getName());
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
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return describeFeatureTypeResponse;


	}

	public ArrayList<Triple> getGeometriesPredicates(GeographicLayer layer){

		ResultSet rs = jn.executeQuery(SPARQL.listGeometryPredicates.replace("PARAM_LAYER", layer.getName()));
		ArrayList<Triple> result = new ArrayList<Triple>();		

		while (rs.hasNext()) {

			Triple triple = new Triple();

			QuerySolution soln = rs.nextSolution();
			triple.setPredicate(model.shortForm(soln.getResource("?predicate").toString()));

			if (soln.get("?dataType")==null) {

				triple.setObjectDataType(GlobalSettings.defaultDataType);

			} else {

				triple.setObjectDataType(model.shortForm(soln.getResource("?dataType").getURI()));
			}

			result.add(triple);			   
		}

		return result;
	}

	private String getFeatureType (GeographicLayer layer){

		ResultSet rs = jn.executeQuery(SPARQL.getFeatureType.replace("PARAM_LAYER", layer.getName()));
		String result = new String();

		while (rs.hasNext()) {

			QuerySolution soln = rs.nextSolution();					

			result = soln.getLiteral("?wkt").getString();
		}

		try {

			result = WKTParser.parseToGML2(result);

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			Document document = documentBuilder.parse(new InputSource(new ByteArrayInputStream(result.getBytes("utf-8"))));

			result = document.getDocumentElement().getNodeName();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public String getFeature(GeographicLayer layer) {

		String getFeatureResponse = new String();
		ResultSet rs = jn.executeQuery(SPARQL.prefixes +" \n" + this.createGetFeatureSPARQL(layer));


		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder;

			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse("src/main/resources/GetFeature_100.xml");

			ArrayList<Triple> predicates = new ArrayList<Triple>();
			predicates = this.getGeometriesPredicates(layer);

			long countIteration = 0;
			while (rs.hasNext()) {
				countIteration++;
				XPath xpath = XPathFactory.newInstance().newXPath();
				NodeList myNodeList = (NodeList) xpath.compile("//FeatureCollection/text()").evaluate(document, XPathConstants.NODESET);           
				
				QuerySolution soln = rs.nextSolution();
				
				String currentGeometryName = GlobalSettings.defaultServiceName + ":" + soln.getResource("?geometry").getLocalName();
				
				Element currentGeometryElement = document.createElement(currentGeometryName);
				Element rootGeometry = document.createElement("gml:featureMember");
				
				for (int i = 0; i < predicates.size(); i++) {

					String predicateWithoutPrefix = new String();
					predicateWithoutPrefix =  predicates.get(i).getPredicate().substring(predicates.get(i).getPredicate().indexOf(":")+1, predicates.get(i).getPredicate().length());

					
					Element elementGeometryPredicate = document.createElement(GlobalSettings.defaultServiceName + ":" +predicateWithoutPrefix);
					elementGeometryPredicate.setAttribute("fid", currentGeometryName + "." + countIteration);
					

					
					//TODO Fix hardcoded geo:asWKT 

					if (predicates.get(i).getPredicate().equals("geo:asWKT")) {

						String GML = WKTParser.parseToGML2(soln.getLiteral("?asWKT").getString().toString());
						Element GMLnode =  documentBuilder.parse(new ByteArrayInputStream(GML.getBytes())).getDocumentElement();		
						Node dup = document.importNode(GMLnode, true);

						elementGeometryPredicate.appendChild(dup);
						rootGeometry.appendChild(elementGeometryPredicate);
						currentGeometryElement.appendChild(elementGeometryPredicate);
						
						rootGeometry.appendChild(currentGeometryElement);
						
	

					} else {


						
						Element elementAttribute = document.createElement(GlobalSettings.defaultServiceName + ":" + predicateWithoutPrefix);
						elementAttribute.appendChild(document.createCDATASection(model.shortForm(soln.get("?"+predicateWithoutPrefix).toString())));
					
						currentGeometryElement.appendChild(elementAttribute);
						
					}


					myNodeList.item(0).getParentNode().insertBefore(rootGeometry, myNodeList.item(1));
					


				}

				// TODO iterate over query result and convert to GML2
				// TODO Create method for making transformation and deliver XML result as String.

			}

			DOMSource source = new DOMSource(document);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			StringWriter sw = new StringWriter();

			StreamResult result = new StreamResult(sw);
			transformer.transform(source, result);

			StringBuffer sb = sw.getBuffer();


			getFeatureResponse= sb.toString();


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
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return getFeatureResponse;

	}

	public String createGetFeatureSPARQL(GeographicLayer layer){

		ArrayList<Triple> predicates = new ArrayList<Triple>();
		predicates = this.getGeometriesPredicates(layer);

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
				" WHERE { GRAPH <"+ layer.getName() + "> {" +
				"?geometry a geo:Geometry . \n" + whereClause + "}}";

		return SPARQL;
	}

}
