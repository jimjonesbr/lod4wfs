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
		
		// TODO Auto-generated constructor stub
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
		
		solrFeatureList = factorySOLR.listSOLRFeatures();
		fdaFeatureList = factoryFDA.listFDAFeatures();
		sdaFeatureList = factorySDA.listSDAFeatures();
		
		for (int i = 0; i < fdaFeatureList.size(); i++) {
			features.add(fdaFeatureList.get(i));
		}
		
		for (int i = 0; i < sdaFeatureList.size(); i++) {
			features.add(sdaFeatureList.get(i));
		}		

		for (int i = 0; i < solrFeatureList.size(); i++) {
			features.add(solrFeatureList.get(i));
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
				
				logger.info("Creating Capabilities Document from " + Utils.getCanonicalHostName() + ":" + GlobalSettings.defaultPort + "/" + GlobalSettings.defaultServiceName + "/wfs ...");

				XPath xpath = XPathFactory.newInstance().newXPath();
				NodeList myNodeList = (NodeList) xpath.compile("//FeatureTypeList/text()").evaluate(document, XPathConstants.NODESET);           

				/**
				 * Adding LOD features (SDA and FDA) in the Capabilities Document. 
				 */
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
		
		resultCapabilities = resultCapabilities.replace("PARAM_PORT", Integer.toString(GlobalSettings.defaultPort));
		resultCapabilities = resultCapabilities.replace("PARAM_HOST", Utils.getCanonicalHostName());
		resultCapabilities = resultCapabilities.replace("PARAM_SERVICE", GlobalSettings.defaultServiceName);
		
		return resultCapabilities;


		
	}
		
	
	public String describeFeatureType(WFSFeature feature){

	
		String describeFeatureTypeResponse = new String(); 
			
		if(this.isSOLRFeature(feature)){
			
			describeFeatureTypeResponse = AdapterSOLR4WFS.getInstance().describeFeatureType(feature);
		}
		
		
		if(this.isFDAFeature(feature)){
			
			describeFeatureTypeResponse = AdapterLOD4WFS.getInstance().describeFeatureType(feature);
			
		} 		

		
		if(this.isSDAFeature(feature)){
			
			describeFeatureTypeResponse = AdapterLOD4WFS.getInstance().describeFeatureType(feature);
			
		} 
		
		
		return describeFeatureTypeResponse;


	}

	
	public String getFeature(WFSFeature feature){
		
		String getFeatureResponse = new String(); 
	
		
		if(this.isSOLRFeature(feature)){
			
			getFeatureResponse = AdapterSOLR4WFS.getInstance().getFeature(feature);
			
		}
		
		
		if(this.isFDAFeature(feature)){
			
			getFeatureResponse = AdapterLOD4WFS.getInstance().getFeature(feature);
			
		} 

		
		if(this.isSDAFeature(feature)){
			
			getFeatureResponse = AdapterLOD4WFS.getInstance().getFeature(feature);
			
		} 
		
		
		return getFeatureResponse;
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
				//System.out.println(features.get(i).getName().substring(0, position+1));
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
