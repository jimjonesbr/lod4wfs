package de.ifgi.lod4wfs.factory;

/**
 * @author Jim Jones
 * @description Provides all standard WFS functions (GetCapabilities, DescribeFeatureType and GetFeature) for LOD data sources.
 */

import it.cutruzzula.lwkt.WKTParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.core.Triple;
import de.ifgi.lod4wfs.core.Utils;
import de.ifgi.lod4wfs.core.WFSFeature;
import de.ifgi.lod4wfs.infrastructure.JenaConnector;

public class AdapterLOD4WFS {

	private static AdapterLOD4WFS instance;
	private FactorySDAFeatures factorySDA;
	private FactoryFDAFeatures factoryFDA;

	private static JenaConnector jn;
	private static Logger logger = Logger.getLogger("LOD4WFS-Adapter");

	public AdapterLOD4WFS(){
		factorySDA = new FactorySDAFeatures();
		factoryFDA = new FactoryFDAFeatures();
		jn = new JenaConnector();
	}

	public static AdapterLOD4WFS getInstance() {
		if (instance == null) {
			instance = new AdapterLOD4WFS();
		}
		return instance;
	}


	public String describeFeatureType(WFSFeature feature){

		String featureName = FactoryWFS.getInstance().getLoadedModelFeature().expandPrefix((feature.getName()));	

		String describeFeatureTypeResponse = new String(); 
		ArrayList<Triple> predicates = new ArrayList<Triple>();

		if(feature.isFDAFeature()){

			predicates = factoryFDA.getPredicatesFDAFeatures(feature);

		} else { 


			predicates = factorySDA.getPredicatesSDAFeatures(featureName);

			//TODO: Verify the need of manual input of geometry predicate.
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

			String layerPrefix = FactoryWFS.getInstance().getLoadedModelFeature().expandPrefix((feature.getName()));	
			layerPrefix = layerPrefix.substring(0,layerPrefix.indexOf(":") + 1);			

			Element requestElement = document.getDocumentElement(); 						
			requestElement.setAttribute("targetNamespace", FactoryWFS.getInstance().getLoadedModelFeature().expandPrefix(layerPrefix));	

			for (Map.Entry<String, String> entry : FactoryWFS.getInstance().getLoadedModelFeature().getNsPrefixMap().entrySet())
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


				} else if(feature.isFDAFeature() && (predicates.get(i).getPredicate().equals(feature.getGeometryVariable()))){

					//TODO: create function to identify geometry type!!!
					sequence.setAttribute("type","gml:MultiPointPropertyType");

				} else {
					
					sequence.setAttribute("type",predicates.get(i).getObjectDataType());
					
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

	/**
	 * @see OGC Specification for WFS http://www.opengeospatial.org/standards/wfs
	 * @param geographic feature to be retrieved.
	 * @return XML Document containing the WFS GetFeature response with all geometries of a given feature together with their attribute table.
	 */
	public String getFeature(WFSFeature feature) {

		String getFeatureResponse = new String();
		String layerPrefix = new String();
		String geometryType = "";
		
		ArrayList<Triple> predicates = new ArrayList<Triple>();
		ResultSet rs;
		
		
		if(feature.isFDAFeature()){

			logger.info("Performing query at " + feature.getEndpoint()  + " to retrieve all geometries of [" + feature.getName() + "]  ...");
			predicates = factoryFDA.getPredicatesFDAFeatures(feature);

			rs = jn.executeQuery(feature.getQuery().toString(),feature.getEndpoint());

		} else {

			logger.info("Performing query at " + GlobalSettings.getDefaultSPARQLEndpoint()  + " to retrieve all geometries of [" + feature.getName() + "] ...");

			String featureName = FactoryWFS.getInstance().getLoadedModelFeature().expandPrefix(feature.getName());
			predicates = factorySDA.getPredicatesSDAFeatures(featureName);	

			rs = jn.executeQuery(factorySDA.generateGetFeatureSPARQL(featureName, predicates),GlobalSettings.getDefaultSPARQLEndpoint());

		}

		layerPrefix = FactoryWFS.getInstance().getLoadedModelFeature().shortForm(feature.getName());
		layerPrefix = layerPrefix.substring(0,layerPrefix.indexOf(":"));
		
		long countIteration = 0;
		
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

				for (Map.Entry<String, String> entry : FactoryWFS.getInstance().getLoadedModelFeature().getNsPrefixMap().entrySet()) {

					requestElement.setAttribute("xmlns:" + entry.getKey(), entry.getValue());

				}							

				logger.info("Creating GetFeature XML document for [" + feature.getName() + "] ...");

				XPath xpath = XPathFactory.newInstance().newXPath();
				NodeList myNodeList = (NodeList) xpath.compile("//FeatureCollection/text()").evaluate(document, XPathConstants.NODESET);           


				if(!feature.isFDAFeature()){
					Triple triple = new Triple();
					triple.setPredicate(factoryFDA.getGeometryPredicate(feature.getQuery()));
					predicates.add(triple);		
				}


				while (rs.hasNext()) {

					countIteration++;

					QuerySolution soln = rs.nextSolution();
					String currentGeometryName = "LODGEO_";
					Element currentGeometryElement = document.createElement(FactoryWFS.getInstance().getLoadedModelFeature().shortForm(feature.getName()));


					currentGeometryElement.setAttribute("fid", currentGeometryName + "" + countIteration);				

					Element rootGeometry = document.createElement("gml:featureMember");


					for (int i = 0; i < predicates.size(); i++) {

						if(feature.isFDAFeature()){

							Element elementGeometryPredicate = document.createElement(layerPrefix + ":" + predicates.get(i).getPredicate());


							if(predicates.get(i).getPredicate().equals(feature.getGeometryVariable())){														

								//TODO: Check if literal is already GML
								String wkt = soln.getLiteral("?"+feature.getGeometryVariable()).getString();
								String gml = Utils.convertWKTtoGML(wkt);
								Element GMLnode =  documentBuilder.parse(new ByteArrayInputStream(gml.getBytes())).getDocumentElement();		
								Node node = document.importNode(GMLnode, true);
								elementGeometryPredicate.appendChild(node);						
								rootGeometry.appendChild(elementGeometryPredicate);												
								currentGeometryElement.appendChild(elementGeometryPredicate);						
								rootGeometry.appendChild(currentGeometryElement);
																													
								
								if(!WKTParser.parse(Utils.removeCRSandTypefromWKT(wkt)).getType().equals(geometryType)){
									
									if(geometryType.equals("")){
										
										geometryType= WKTParser.parse(Utils.removeCRSandTypefromWKT(wkt)).getType().toString();
										
									} else {
										
										logger.error("The feature [" + feature.getName() + "] has multiple geometry types. This is not supported by the OGC WFS Standard. For this document, the geometry type ["+geometryType+"] will be assumed.");
										
									}
									
								}

							} else {

								Element elementAttribute = document.createElement(layerPrefix + ":" + predicates.get(i).getPredicate());							

								if(soln.get("?"+predicates.get(i).getPredicate().toString()) != null){
									elementAttribute.appendChild(document.createCDATASection(soln.get("?"+predicates.get(i).getPredicate()).toString()));	
								}

								currentGeometryElement.appendChild(elementAttribute);

							}


						} else {


							String predicateWithoutPrefix = new String();

							predicateWithoutPrefix =  this.removePredicateURL(predicates.get(i).getPredicate());

							Element elementGeometryPredicate = document.createElement(layerPrefix + ":" + predicateWithoutPrefix);

							if (predicates.get(i).getPredicate().equals(GlobalSettings.getGeometryPredicate().replaceAll("<", "").replace(">", ""))) {

								if(!Utils.getGeometryType(soln.getLiteral("?" + GlobalSettings.getGeometryVariable()).getString()).equals("INVALID")){

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

		}



		/**
		 * Generates a JSON file as output. Geometries are maintained as WKT.
		 */

		if(feature.getOutputFormat().equals("json")){

			StringBuilder json = new StringBuilder();			
			String jsonEntries = new String();

			if(!feature.isFDAFeature()){

				Triple triple = new Triple();

				//triple.setPredicate(GlobalSettings.getGeometryPredicate().replaceAll("<", "").replace(">", ""));
				triple.setPredicate(factoryFDA.getGeometryPredicate(feature.getQuery()));
				predicates.add(triple);	

			}

			jsonEntries = "[\n";

			logger.info("Creating JSON document for [" + feature.getName() + "]...");

			while (rs.hasNext()) {

				QuerySolution soln = rs.nextSolution();

				jsonEntries = jsonEntries + " {\n";
				for (int i = 0; i < predicates.size(); i++) {

					if(soln.get("?"+predicates.get(i).getPredicate()).isLiteral()){

						if(soln.getLiteral("?"+predicates.get(i).getPredicate()).getDatatype() != null){

							/**
							 * Checks if the literal is of type integer, long, byte or decimal, in order to avoid quotation marks -> "".
							 */
							if(soln.getLiteral("?"+predicates.get(i).getPredicate()).getDatatypeURI().trim().equals(GlobalSettings.getDefaultDecimalType()) ||
									soln.getLiteral("?"+predicates.get(i).getPredicate()).getDatatypeURI().trim().equals(GlobalSettings.getDefaultLongType()) ||
									soln.getLiteral("?"+predicates.get(i).getPredicate()).getDatatypeURI().trim().equals(GlobalSettings.getDefaultIntegerType()) ||
									soln.getLiteral("?"+predicates.get(i).getPredicate()).getDatatypeURI().trim().equals(GlobalSettings.getDefaultByteType()) ||
									soln.getLiteral("?"+predicates.get(i).getPredicate()).getDatatypeURI().trim().equals(GlobalSettings.getDefaultFloatType())) {


								if(!soln.getLiteral("?" + predicates.get(i).getPredicate()).getLexicalForm().toUpperCase().equals("NAN")){

									jsonEntries = jsonEntries + "  \"" + predicates.get(i).getPredicate().toString() +
											"\": " + soln.getLiteral("?" + predicates.get(i).getPredicate()).getValue();

								} else {

									jsonEntries = jsonEntries + "  \"" + predicates.get(i).getPredicate().toString() + "\": 0";

								}

							}

						} else {

							jsonEntries = jsonEntries + "  \"" + predicates.get(i).getPredicate().toString() +
									"\": \"" + soln.getLiteral("?" + predicates.get(i).getPredicate()).getValue().toString().replace("\"", "'") + "\"";
						}

					} else {

						jsonEntries = jsonEntries + "  \"" + predicates.get(i).getPredicate().toString() +
								"\": \"" + soln.get("?" + predicates.get(i).getPredicate()).toString().replace("\"", "'") + "\"";

					}


					if(i != predicates.size()-1 ){
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



		/**
		 * Generates a GeoJSON file as output. Geometries are encoded to GeoJSON.
		 */

		if(feature.getOutputFormat().equals("geojson")){

			StringBuilder geojson = new StringBuilder();

			String properties = new String();

			//int counter=0;

			geojson.append("{\"type\":\"FeatureCollection\",\"totalFeatures\":[PARAM_FEATURES],\"features\":[") ;

			if(!feature.isFDAFeature()){

				Triple triple = new Triple();
				triple.setPredicate(factoryFDA.getGeometryPredicate(feature.getQuery()));
				predicates.add(triple);	

			}

			logger.info("Creating GeoJSON document for [" + feature.getName() + "]...");

			while (rs.hasNext()) {

				countIteration++;
				QuerySolution soln = rs.nextSolution();
				geojson.append("\n{\"type\":\"Feature\",\n	\"id\":\"FEATURE_"+ countIteration +"\",	\n	\"geometry\":");
				properties = "\n\"properties\": {\n";

				for (int i = 0; i < predicates.size(); i++) {

					if(feature.isFDAFeature()){

						if(predicates.get(i).getPredicate().equals(feature.getGeometryVariable())){

							geojson.append(Utils.convertWKTtoGeoJSON(soln.getLiteral("?" + feature.getGeometryVariable()).getString()));

						} else {

							/**
							 * Checks if the literal is of type integer, long, byte or decimal, in order to avoid quotation marks -> "".
							 */

							if(soln.get("?"+predicates.get(i).getPredicate()).isLiteral()){


								if(soln.getLiteral("?"+predicates.get(i).getPredicate()).getDatatype() != null){


									if(soln.getLiteral("?"+predicates.get(i).getPredicate()).getDatatypeURI().trim().equals(GlobalSettings.getDefaultDecimalType()) ||
											soln.getLiteral("?"+predicates.get(i).getPredicate()).getDatatypeURI().trim().equals(GlobalSettings.getDefaultLongType()) ||
											soln.getLiteral("?"+predicates.get(i).getPredicate()).getDatatypeURI().trim().equals(GlobalSettings.getDefaultIntegerType()) ||
											soln.getLiteral("?"+predicates.get(i).getPredicate()).getDatatypeURI().trim().equals(GlobalSettings.getDefaultByteType()) ||
											soln.getLiteral("?"+predicates.get(i).getPredicate()).getDatatypeURI().trim().equals(GlobalSettings.getDefaultFloatType())) {


										if(!soln.getLiteral("?" + predicates.get(i).getPredicate()).getLexicalForm().toUpperCase().equals("NAN")){

											properties = properties + "\n	\"" +predicates.get(i).getPredicate().toString()+
													"\": " + soln.getLiteral("?"+predicates.get(i).getPredicate()).getValue() + "," ;

										}

									} 

								} else {

									properties = properties + "\n	\"" +predicates.get(i).getPredicate().toString()+
											"\": \"" + soln.getLiteral("?"+predicates.get(i).getPredicate()).toString().replace("\"", "'") + "\"," ;
								}

							} else {

								properties = properties + "\n	\"" +predicates.get(i).getPredicate().toString()+
										"\": \"" + soln.get("?"+predicates.get(i).getPredicate()).toString().replace("\"", "'") + "\"," ; 

							}

						}


					} else {

						
						if (predicates.get(i).getPredicate().equals(GlobalSettings.getGeometryPredicate().replaceAll("<", "").replace(">", ""))) {

							if(!Utils.getGeometryType(soln.getLiteral("?" + GlobalSettings.getGeometryVariable()).getString()).equals("INVALID")){

								geojson.append(Utils.convertWKTtoGeoJSON(soln.getLiteral("?"+GlobalSettings.getGeometryVariable()).getString()) );

							} else {

								properties = properties + "\"" +predicates.get(i).getPredicate().toString() +
										"\": \"" + soln.getLiteral("?"+GlobalSettings.getGeometryVariable()).getString().replace("\"", "'") ;

							}
						}
					}		               			
				}


				properties = properties.substring(0, properties.length()-1);
				geojson.append(properties+"}},");

			}

			geojson.deleteCharAt(geojson.length()-1);
			geojson.append("]}");

			getFeatureResponse = geojson.toString().replace("[PARAM_FEATURES]", Long.toString(countIteration));

			logger.info("GeoJSON document for [" + feature.getName() + "] with " + countIteration + " geometries successfully created.");

		}
		
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date date = new Date();
		
		feature.setLastAccess(dateFormat.format(date));		
		feature.setSize(getFeatureResponse.getBytes().length);
		feature.setGeometries(countIteration);
		feature.setGeometryType(geometryType);
		
		this.updateFeatureLog(feature);
		
		return getFeatureResponse;

	}




	/**
	 ** Private Methods.
	 **/

	
	private void updateFeatureLog(WFSFeature feature){

		String featureLogFile = "logs/features.log";
		String line = "";
		String splitBy = ";";
		String featureFullname = FactoryWFS.getInstance().getLoadedModelFeature().expandPrefix(feature.getName());
		StringBuilder fileContent = new StringBuilder();
		String featureNewLogData = featureFullname+";"+feature.getLastAccess()+";"+feature.getSize()+";"+feature.getGeometries()+";"+feature.getGeometryType()+"\n";

		boolean exists = false;

		try {

			BufferedReader br = new BufferedReader(new FileReader(featureLogFile));

			while ((line = br.readLine()) != null ) {

				String[] featureLogLine = line.split(splitBy);

				if(featureFullname.equals(featureLogLine[0].trim())){

					fileContent.append(featureNewLogData);
					exists = true;

				} else {

					if(!featureLogLine[0].trim().equals("")){

						fileContent.append(line+"\n");

					}

				}

			}

			if(!exists){

				fileContent.append(featureNewLogData);

			}

			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(featureLogFile, false))); 
			out.println(fileContent.toString());
			out.close();



			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	

	private String removePredicateURL(String predicate){

		return predicate.split("\\P{Alpha}+")[predicate.split("\\P{Alpha}+").length-1];

	}

}
