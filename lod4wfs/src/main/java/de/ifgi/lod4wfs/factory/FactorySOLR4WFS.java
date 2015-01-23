package de.ifgi.lod4wfs.factory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.core.SOLRRecord;
import de.ifgi.lod4wfs.core.Utils;
import de.ifgi.lod4wfs.core.WFSFeature;


public class FactorySOLR4WFS {

	private static FactorySOLR4WFS instance;
	private FactorySOLRFeatures factorySOLR;
	private static Logger logger = Logger.getLogger("SOLR4WFS-Factory");
	private static Model modelFeatures;	
	
	public FactorySOLR4WFS(){
		
		factorySOLR = new FactorySOLRFeatures();
		
	}
	
	public static FactorySOLR4WFS getInstance() {
		if (instance == null) {
			instance = new FactorySOLR4WFS();
		}
		return instance;
	}
	

	public String getFeature(WFSFeature feature){
		
		
		
		return null;
		
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

		
		//String featureName = FactoryWFS.getInstance().getLoadedModelFeature().expandPrefix((feature.getName()));
		
		String describeFeatureTypeResponse = new String(); 
		ArrayList<SOLRRecord> fields = new ArrayList<SOLRRecord>();
					
//		ArrayList<WFSFeature> solrFeatureList = new ArrayList<WFSFeature>();
//		solrFeatureList = FactoryWFS.getInstance().getLoadedSOLRFeatures();
//		
//		for (int i = 0; i < solrFeatureList.size(); i++) {
//		
//			if (featureName.equals(solrFeatureList.get(i).getName())){
//				
//				feature = solrFeatureList.get(i);
//				
//			}
//			
//		}
		
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
			layerPrefix = layerPrefix.substring(0,layerPrefix.indexOf(":")+1);			
						
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
								
				/**
				 * Checks if predicate is the chosen geometry predicate in the settings file.
				 */
				
												
				if(fields.get(i).getName().equals(feature.getSOLRGeometryField())){
					
					String featureType = new String();
					
					featureType = factorySOLR.getSOLRGeometryType(feature);
			
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
					sequence.setAttribute("type",fields.get(i).getType());
				}

				myNodeList.item(0).getParentNode().insertBefore(sequence, myNodeList.item(0));

			}

			describeFeatureTypeResponse = Utils.printXMLDocument(document);

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
			
			
			
			if(features.get(i).isSOLRFeature()){
				
				modelFeatures.setNsPrefix(GlobalSettings.getSOLRPrefix(), features.get(i).getName().substring(0, position+1) );
				System.out.println(features.get(i).getName().substring(0, position+1));
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
}