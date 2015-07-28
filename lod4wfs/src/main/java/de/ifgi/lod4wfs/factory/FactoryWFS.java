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
import de.ifgi.lod4wfs.core.Utils;
import de.ifgi.lod4wfs.core.WFSFeature;

/**
 * @author Jim Jones
 * @tutorial: https://www.overleaf.com/1324048zhbcdw#/3368311/
 */

public class FactoryWFS {

	private static FactoryWFS instance;

	private FactorySDAFeatures factorySDA;
	private FactoryFDAFeatures factoryFDA;
	private FactorySOLRFeatures factorySOLR;

	private static Model modelFeatures;	

	private static ArrayList<WFSFeature> fdaFeatureList;
	private static ArrayList<WFSFeature> sdaFeatureList;
	private static ArrayList<WFSFeature> solrFeatureList;

	private static Logger logger = Logger.getLogger("WFS-Factory");

	public FactoryWFS() {

		factorySDA = new FactorySDAFeatures();
		factoryFDA = new FactoryFDAFeatures();
		factorySOLR = new FactorySOLRFeatures();

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

		fdaFeatureList = null;
		sdaFeatureList = null;
		solrFeatureList = null;

		GlobalSettings.refreshSystemVariables();

		if(GlobalSettings.isSolrEnabled()){

			solrFeatureList = factorySOLR.listSOLRFeatures();

			for (int i = 0; i < solrFeatureList.size(); i++) {
				features.add(solrFeatureList.get(i));
			}	

		} else {

			logger.warn("SOLR Features support disabled.");

		}

		if(GlobalSettings.isFdaEnabled()){

			fdaFeatureList = factoryFDA.listFDAFeatures();

			for (int i = 0; i < fdaFeatureList.size(); i++) {

				/**
				 * Checks if the feature is enabled for download.				
				 */
				if(fdaFeatureList.get(i).isEnabled()){
					
					features.add(fdaFeatureList.get(i));

				} else {

					logger.warn("FDA Feature [" + fdaFeatureList.get(i).getName() + "] disabled. This feature will not appear in the Capabilities Document.");

				}
			}

		} else {

			logger.warn("FDA Features support disabled.");

		}

		if(GlobalSettings.isSdaEnabled()){

			sdaFeatureList = factorySDA.listSDAFeatures();

			for (int i = 0; i < sdaFeatureList.size(); i++) {
				features.add(sdaFeatureList.get(i));
			}

		} else {

			logger.warn("SDA Features support disabled.");

		}


		this.generateLayersPrefixes(features);

		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			if (version.equals("1.0.0")) {

				Document document = documentBuilder.parse("wfs/CapabilitiesDocument_100.xml");		

				/**
				 * Iterates through the layers' model and creates NameSpaces' entries with the layers prefixes.
				 */

				Element requestElement = document.getDocumentElement(); 

				for (Map.Entry<String, String> entry : modelFeatures.getNsPrefixMap().entrySet())
				{
					requestElement.setAttribute("xmlns:" + entry.getKey(), entry.getValue());
				}

				logger.info("Creating Capabilities Document from " + Utils.getCanonicalHostName() + ":" + GlobalSettings.getDefaultPort() + "/" + GlobalSettings.getDefaultServiceName() + "/wfs ...");

				XPath xpath = XPathFactory.newInstance().newXPath();
				NodeList nodeList = (NodeList) xpath.compile("//FeatureTypeList/text()").evaluate(document, XPathConstants.NODESET);           

				/**
				 * Adding LOD features (SDA and FDA) in the Capabilities Document. 
				 */
				for (int i = 0; i < features.size(); i++) {

					Element nameElement = document.createElement("Name");
					nameElement.appendChild(document.createTextNode(modelFeatures.shortForm(features.get(i).getName())));
					Element titleElement = document.createElement("Title");
					titleElement.appendChild(document.createTextNode(features.get(i).getTitle()));
					Element featureAbstractElement = document.createElement("Abstract");
					featureAbstractElement.appendChild(document.createTextNode(features.get(i).getFeatureAbstract()));
					Element keywordsElement = document.createElement("Keywords");
					keywordsElement.appendChild(document.createTextNode(features.get(i).getKeywords()));
					Element crsElement = document.createElement("SRS");
					crsElement.appendChild(document.createTextNode(features.get(i).getCRS()));

					Element bboxElement = document.createElement("LatLongBoundingBox");
					bboxElement.setAttribute("maxy", "83.6274");
					bboxElement.setAttribute("maxx", "-180");
					bboxElement.setAttribute("miny", "-90");
					bboxElement.setAttribute("minx", "180");

					Element featureElement = document.createElement("FeatureType");
					featureElement.appendChild(nameElement);
					featureElement.appendChild(titleElement);
					featureElement.appendChild(featureAbstractElement);
					featureElement.appendChild(keywordsElement);
					featureElement.appendChild(crsElement);
					featureElement.appendChild(bboxElement);

					nodeList.item(1).getParentNode().insertBefore(featureElement, nodeList.item(1));

				}

				resultCapabilities = Utils.printXMLDocument(document);
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

		resultCapabilities = resultCapabilities.replace("PARAM_PORT", Integer.toString(GlobalSettings.getDefaultPort()));
		resultCapabilities = resultCapabilities.replace("PARAM_HOST", Utils.getCanonicalHostName());
		resultCapabilities = resultCapabilities.replace("PARAM_SERVICE", GlobalSettings.getDefaultServiceName());

		return resultCapabilities;


	}


	public String describeFeatureType(WFSFeature feature){


		String describeFeatureTypeResponse = new String(); 

		if(GlobalSettings.isSolrEnabled()){

			if(this.isSOLRFeature(feature)){

				describeFeatureTypeResponse = AdapterSOLR4WFS.getInstance().describeFeatureType(feature);
			}

		} 

		if(GlobalSettings.isFdaEnabled()){

			if(this.isFDAFeature(feature)){

				describeFeatureTypeResponse = AdapterLOD4WFS.getInstance().describeFeatureType(feature);

			} 		

		} 

		if(GlobalSettings.isSdaEnabled()){

			if(this.isSDAFeature(feature)){

				describeFeatureTypeResponse = AdapterLOD4WFS.getInstance().describeFeatureType(feature);

			} 

		}

		return describeFeatureTypeResponse;


	}


	public String getFeature(WFSFeature feature){

		String getFeatureResponse = new String(); 


		if(GlobalSettings.isSolrEnabled()){

			if(this.isSOLRFeature(feature)){

				getFeatureResponse = AdapterSOLR4WFS.getInstance().getFeature(feature);

			}

		} 

		if (GlobalSettings.isFdaEnabled()) {

			if(this.isFDAFeature(feature)){

				getFeatureResponse = AdapterLOD4WFS.getInstance().getFeature(feature);

			} 

		} 

		if(GlobalSettings.isSdaEnabled()){


			if(this.isSDAFeature(feature)){

				getFeatureResponse = AdapterLOD4WFS.getInstance().getFeature(feature);

			} 

		}
		return getFeatureResponse;
	}


	//TODO implement a return type for generateLayersPrefixes(). Put a value direct in a variable isn't recommended. 
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

				if (finder == true) {

					position = size;
					scape = true;

				}

				size--;
			}



			if(features.get(i).isSOLRFeature()){

				modelFeatures.setNsPrefix(GlobalSettings.getSOLRPrefix(), features.get(i).getName().substring(0, position+1) );

			}

			if (modelFeatures.getNsURIPrefix(features.get(i).getName().substring(0, position + 1)) == null) {

				if (features.get(i).isFDAFeature()){

					modelFeatures.setNsPrefix(GlobalSettings.getFDAPrefix(), features.get(i).getName().substring(0, position+1) );

				} else {

					modelFeatures.setNsPrefix(GlobalSettings.getSDAPrefix() + modelFeatures.getNsPrefixMap().size(), features.get(i).getName().substring(0, position + 1) );

				}

			}

		}

	}

	private boolean isFDAFeature(WFSFeature feature){

		boolean result = false;

		/**
		 * Checks if the selected layer is created via FDA (based on pre-defined SPARQL Query)
		 */
		for (int i = 0; i < fdaFeatureList.size(); i++) {

			if(fdaFeatureList.get(i).getName().equals(modelFeatures.expandPrefix(feature.getName()))){

				result = true; 
				feature.setQuery(fdaFeatureList.get(i).getQuery());
				feature.setGeometryVariable(fdaFeatureList.get(i).getGeometryVariable());
				feature.setEndpoint(fdaFeatureList.get(i).getEndpoint());
				feature.setAsFDAFeature(true);

			}

		}

		return result;

	}

	private boolean isSDAFeature(WFSFeature feature){

		boolean result = false;

		/**
		 * Checks if the selected layer is created via SDA.
		 */
		for (int i = 0; i < sdaFeatureList.size(); i++) {

			if(sdaFeatureList.get(i).getName().equals(modelFeatures.expandPrefix(feature.getName()))){
				result = true; 
				feature.setGeometryVariable(sdaFeatureList.get(i).getGeometryVariable());
				feature.setEndpoint(sdaFeatureList.get(i).getEndpoint());
				feature.setAsSDAFeature(true);
			}

		}

		return result;

	}

	private boolean isSOLRFeature(WFSFeature feature){

		boolean result = false;

		/**
		 * Checks if the selected layer is created via FDA (based on pre-defined SPARQL Query)
		 */
		for (int i = 0; i < solrFeatureList.size(); i++) {

			if(solrFeatureList.get(i).getName().equals(modelFeatures.expandPrefix(feature.getName()))){
				result = true; 
				feature.setQuery(solrFeatureList.get(i).getQuery());
				feature.setGeometryVariable(solrFeatureList.get(i).getGeometryVariable());
				feature.setEndpoint(solrFeatureList.get(i).getEndpoint());

			}

		}

		return result;

	}

	public ArrayList<WFSFeature> getLoadedFDAFeatures(){

		return fdaFeatureList;
	}

	public ArrayList<WFSFeature> getLoadedSOLRFeatures(){

		return solrFeatureList;
	}

	public Model getLoadedModelFeature(){

		return modelFeatures;

	}

}
