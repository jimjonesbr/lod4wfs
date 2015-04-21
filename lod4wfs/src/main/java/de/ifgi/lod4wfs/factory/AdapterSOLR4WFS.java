package de.ifgi.lod4wfs.factory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrDocumentList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.core.SOLRRecord;
import de.ifgi.lod4wfs.core.Utils;
import de.ifgi.lod4wfs.core.WFSFeature;
import de.ifgi.lod4wfs.infrastructure.SOLRConnector;


public class AdapterSOLR4WFS {

	private static AdapterSOLR4WFS instance;
	private FactorySOLRFeatures factorySOLR;
	private static Logger logger = Logger.getLogger("SOLR4WFS-Adapter");

	public AdapterSOLR4WFS(){

		factorySOLR = new FactorySOLRFeatures();

	}

	public static AdapterSOLR4WFS getInstance() {
		
		if (instance == null) {
			
			instance = new AdapterSOLR4WFS();
			
		}
		
		return instance;
	}


	private WFSFeature expandSOLRFeature(WFSFeature feature){

		//TODO: verificar a necessidade de expandir prefixos!!!
		String featureName = FactoryWFS.getInstance().getLoadedModelFeature().expandPrefix((feature.getName()));		
		ArrayList<WFSFeature> solrFeatureList = new ArrayList<WFSFeature>();

		solrFeatureList = FactoryWFS.getInstance().getLoadedSOLRFeatures();

		for (int i = 0; i < solrFeatureList.size(); i++) {

			if (featureName.equals(solrFeatureList.get(i).getName())){

				feature = solrFeatureList.get(i);

			}

		}

		return feature;
		
	} 

	public String describeFeatureType(WFSFeature feature){

		String describeFeatureTypeResponse = new String(); 
		ArrayList<SOLRRecord> fields = new ArrayList<SOLRRecord>();

		feature = this.expandSOLRFeature(feature);

		fields = factorySOLR.getSOLRFeatureFields(feature);

		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			Document document = documentBuilder.parse("wfs/DescribeFeature_100.xml");

			logger.info("Creating DescribeFeatureType XML document for [" + feature.getName() + "] ...");

			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList myNodeList = (NodeList) xpath.compile("//extension/sequence/text()").evaluate(document, XPathConstants.NODESET);           

			String layerPrefix = FactoryWFS.getInstance().getLoadedModelFeature().shortForm(feature.getName());
			layerPrefix = layerPrefix.substring(0,layerPrefix.indexOf(":") + 1);			

			Element requestElement = document.getDocumentElement(); 						
			requestElement.setAttribute("targetNamespace", FactoryWFS.getInstance().getLoadedModelFeature().expandPrefix(layerPrefix));

			for (Map.Entry<String, String> entry : FactoryWFS.getInstance().getLoadedModelFeature().getNsPrefixMap().entrySet())

			{
				requestElement.setAttribute("xmlns:" + entry.getKey(), entry.getValue());

			}


			for (int i = 0; i < fields.size(); i++) {

				Element sequence = document.createElement("xsd:element");
				sequence.setAttribute("maxOccurs","1");
				sequence.setAttribute("minOccurs","0");
				sequence.setAttribute("name", fields.get(i).getName());
				sequence.setAttribute("nillable","true");

				if(fields.get(i).getName().equals(feature.getSOLRGeometryField())){

					sequence.setAttribute("type",factorySOLR.getSOLRGeometryType(feature));

				} else {
					
					sequence.setAttribute("type",fields.get(i).getType());
				}

				myNodeList.item(0).getParentNode().insertBefore(sequence, myNodeList.item(0));

			}

			describeFeatureTypeResponse = Utils.printXMLDocument(document);

			describeFeatureTypeResponse = describeFeatureTypeResponse.replace("PARAM_NAME", feature.getName().substring(feature.getName().indexOf(":")+1, feature.getName().length()));
			describeFeatureTypeResponse = describeFeatureTypeResponse.replace("PARAM_TYPE", feature.getName());
			describeFeatureTypeResponse = describeFeatureTypeResponse.replace("PARAM_SERVER_PORT", Integer.toString(GlobalSettings.getDefaultPort()));
			describeFeatureTypeResponse = describeFeatureTypeResponse.replace("PARAM_SERVICE", GlobalSettings.getDefaultServiceName());
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


	public String getFeature(WFSFeature feature) {

		String getFeatureResponse = new String();
		String layerPrefix = new String();

		layerPrefix = GlobalSettings.getSOLRPrefix();

		feature = this.expandSOLRFeature(feature);

		ArrayList<SOLRRecord> fields = new ArrayList<SOLRRecord>();
		fields = factorySOLR.getSOLRFeatureFields(feature);

		logger.info("Performing query at " + feature.getEndpoint()  + " to retrieve the geometries of [" + feature.getName() + "]  ...");

		SolrDocumentList rs = new SolrDocumentList();

		SOLRConnector solrConnector = new SOLRConnector();
		rs = solrConnector.executeQuery(feature);


		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder;

			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse("wfs/GetFeature_100.xml");

			/**
			 * Build Name Spaces in the XML header.
			 */
			Element requestElement = document.getDocumentElement(); 

			for (Map.Entry<String, String> entry : FactoryWFS.getInstance().getLoadedModelFeature().getNsPrefixMap().entrySet()) {

				requestElement.setAttribute("xmlns:" + entry.getKey(), entry.getValue());

			}			


			logger.info("Creating GetFeature XML document with " + rs.size() +  " records for [" + feature.getName() + "] ...");

			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList myNodeList = (NodeList) xpath.compile("//FeatureCollection/text()").evaluate(document, XPathConstants.NODESET);           

			int invalid = 0;
			int empty = 0;

			for (int i = 0; i < rs.size(); i++) {

				String currentGeometryName = "SOLRGEO_";

				Element currentGeometryElement = document.createElement(FactoryWFS.getInstance().getLoadedModelFeature().shortForm(feature.getName()));

				currentGeometryElement.setAttribute("fid", currentGeometryName + "" + i);				

				Element rootGeometry = document.createElement("gml:featureMember");
				
				for (int j = 0; j < fields.size(); j++) {


					Element elementGeometryPredicate = document.createElement(layerPrefix + ":" + feature.getGeometryVariable());

					if(fields.get(j).getName().equals(feature.getGeometryVariable())){														


						if(rs.get(i).getFieldValue(feature.getGeometryVariable()) == null){

							//logger.error("Record skipped at SOLR Feature [" + feature.getName() + "]. The field [" + feature.getGeometryVariable()  + "] is empty.");
							
							empty = empty +1;

						} else {

							String wkt = new String();
							wkt = rs.get(i).getFieldValue(feature.getGeometryVariable()).toString();
							wkt = wkt.replace("[", "").replace("]", "");
							
							if (Utils.isWKT(wkt)){

								String gml = Utils.convertWKTtoGML(wkt);

								Element GMLnode =  documentBuilder.parse(new ByteArrayInputStream(gml.getBytes())).getDocumentElement();		
								Node dup = document.importNode(GMLnode, true);
								elementGeometryPredicate.appendChild(dup);						
								rootGeometry.appendChild(elementGeometryPredicate);												
								currentGeometryElement.appendChild(elementGeometryPredicate);						
								rootGeometry.appendChild(currentGeometryElement);					

							} else {

								invalid = invalid + 1;
								logger.error("Record skipped at SOLR Feature [" + feature.getName() + "]. Invalid WKT geometry for [" + feature.getGeometryVariable() + "]: " + wkt);
							}

						}

					} else {

						Element elementAttribute = document.createElement(layerPrefix + ":" + fields.get(j).getName());							

						if(rs.get(i).getFieldValue(fields.get(j).getName().toString()) != null){

							String fieldValue = new String();
							fieldValue = rs.get(i).getFieldValue(fields.get(j).getName().toString()).toString().replace("[", "").replace("]", "");
							elementAttribute.appendChild(document.createCDATASection(fieldValue));

						} else {

							elementAttribute.appendChild(document.createCDATASection("-"));
						}

						currentGeometryElement.appendChild(elementAttribute);

					}


				}

				
				myNodeList.item(1).getParentNode().insertBefore(rootGeometry, myNodeList.item(1));

			}

			if (empty != 0){
				
				logger.error(empty + " records skipped at SOLR Feature [" + feature.getName() + "]. Geometry field [" + feature.getGeometryVariable()  + "] is empty.");
				
			}

			if (invalid != 0){
				
				logger.error(empty + " records skipped at SOLR Feature [" + feature.getName() + "]. Geometry field [" + feature.getGeometryVariable()  + "] with invalid WKT geometries.");
				
			}
			
			logger.info("XML Document for ["+ feature.getName() +"] successfully created.");

			getFeatureResponse = Utils.printXMLDocument(document);

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


	public String getSOLRGeometryType(WFSFeature feature){

		feature.setLimit(1);



		return null;

	}
}




