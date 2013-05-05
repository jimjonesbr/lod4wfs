package de.ifgi.lod4wfs.factory;

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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.util.FileUtils;

import de.ifgi.lod4wfs.core.SPARQL;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.core.SpatialObject;
import de.ifgi.lod4wfs.facade.Facade;
import de.ifgi.lod4wfs.infrastructure.JenaConnector;

public class FactoryJena {

	//	private static FactoryJena instance;
	private static JenaConnector jn;
	private String result;

	public FactoryJena(){
		jn = new JenaConnector(GlobalSettings.SPARQL_Endpoint);
	}

	//	public static FactoryJena getInstance() {
	//
	//		if (instance == null) {
	//			instance = new FactoryJena();
	//		}
	//		return instance;
	//	}

	public ArrayList<SpatialObject> listSpatialObjects(){

		ResultSet rs = jn.executeQuery(SPARQL.listSpatialObjects);
		ArrayList<SpatialObject> result = new ArrayList<SpatialObject>();
		SpatialObject sp = new SpatialObject();

		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			sp.setName(soln.get("?spatialObject").toString());
			sp.setTitle(soln.getLiteral("?title").getValue().toString());
			sp.setFeatureAbstract(soln.getLiteral("?abstract").getValue().toString());
			sp.setKeywords(soln.getLiteral("?keywords").getValue().toString());
			sp.setLowerCorner(GlobalSettings.defaultLowerCorner);
			sp.setUpperCorner(GlobalSettings.defaultUpperCorner);
			sp.setDefaultCRS(GlobalSettings.defautlCRS);
			result.add(sp);
		}

		return result;
	}

	public String createCapabilitiesDocument(String version){

		String resultCapabilities = new String();
        
		ArrayList<SpatialObject> list = new ArrayList<SpatialObject>(); 
		list = this.listSpatialObjects();

		try {

	        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

	        
			if (version.equals("1.0.0")) {
				
		        Document document = documentBuilder.parse("src/main/resources/CapabilitiesDocument_100.xml");
		        
				for (int i = 0; i < list.size(); i++) {
					//TODO: Iterate over available spatial objects to build the capabilities document.
					
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
//		            Element BBOX = document.createElement("LatLongBoundingBox maxx=\"-73.90782\" maxy=\"40.882078\" minx=\"-74.047185\" miny=\"40.679648\"");
//		            BBOX.appendChild(document.createTextNode(""));
		            
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
				
				result = FileUtils.readWholeFileAsUTF8("src/main/resources/CapabilitiesDocument_200.xml");
				
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


	//	public Keywords listSpatialObjectsKeywords(String spatialObject){
	//		ResultSet rs = jn.executeQuery(Constants.listSpatialObjectsKeywords.replace("PARAM_SPOBJ", spatialObject));
	//		Keywords keyword = new Keywords();
	//		ArrayList<String> arrayList = new ArrayList<String>();
	//		
	//		while (rs.hasNext()) {
	//			QuerySolution soln = rs.nextSolution();
	//			arrayList.add(soln.getLiteral("?keyword").getValue().toString());
	//		}
	//		
	//		keyword.setKeywordList(arrayList);
	//		
	//		return keyword;
	//	}
}
