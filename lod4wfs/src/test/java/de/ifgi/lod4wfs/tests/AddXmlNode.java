package de.ifgi.lod4wfs.tests;

import java.io.StringWriter;
import java.util.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.*;
import javax.xml.parsers.*;

public class AddXmlNode {
    public static void main(String[] args) throws Exception {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse("src/main/resources/CapabilitiesDocument_100.xml");
        Element root = document.getDocumentElement();

        // Root Element
        Element rootElement = document.getDocumentElement();

//        
//        Text a = document.createTextNode("FeatureType");
//        Element p = document.createElement("meu_node");
//        p.appendChild(a);
//        nodes.item(0).getParentNode().insertBefore(p, nodes.item(0));
        
        Collection<Features> feature = new ArrayList<Features>();
        feature.add(new Features());

        for (Features i : feature) {
        	

            // server elements
            Element elementFeature = document.createElement("FeatureType");
            //rootElement.appendChild(elementFeature);

            Element name = document.createElement("name");
            name.appendChild(document.createTextNode(i.getName()));
            elementFeature.appendChild(name);

            
            Element port = document.createElement("port");
            port.appendChild(document.createTextNode(Integer.toString(i.getPort())));
            elementFeature.appendChild(port);
//            //root.getParentNode().insertBefore(elementFeature, port);

            
            
            //root.appendChild(elementFeature);
            
            // O ouro!!!

            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList myNodeList = (NodeList) xpath.compile("//FeatureTypeList/text()").evaluate(document, XPathConstants.NODESET);           
            
            Element p = document.createElement("FeatureType");
            p.appendChild(name);
            p.appendChild(port);
            myNodeList.item(1).getParentNode().insertBefore(p, myNodeList.item(1));

            // Fim o ouro!!!

        }

        DOMSource source = new DOMSource(document);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter sw = new StringWriter();
        
        StreamResult result2 = new StreamResult("/home/jones/Desktop/server.xml");
        transformer.transform(source, result2);
        
        StreamResult result = new StreamResult(sw);
        transformer.transform(source, result);
        
        StringBuffer sb = sw.getBuffer();
        System.out.println(sb.toString());
       
    }

    public static class Features {
        public String getName() { return "foo"; }
        public Integer getPort() { return 12345; }
    }
}