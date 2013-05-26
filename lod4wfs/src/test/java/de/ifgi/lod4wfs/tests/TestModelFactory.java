package de.ifgi.lod4wfs.tests;

import java.util.Map;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.core.SPARQL;
import de.ifgi.lod4wfs.infrastructure.JenaConnector;

public class TestModelFactory {

	
	
	public static void main(String[] args) {
		
			
	
		Model modelNameSpaces = ModelFactory.createDefaultModel();
		modelNameSpaces.setNsPrefix("xsd", GlobalSettings.xsdNameSpace );        
		modelNameSpaces.setNsPrefix("sf", GlobalSettings.sfNameSpace );
		modelNameSpaces.setNsPrefix("dc", GlobalSettings.dublinCoreNameSpace );
		modelNameSpaces.setNsPrefix("geo", GlobalSettings.geoSPARQLNameSpace );
		modelNameSpaces.setNsPrefix("rdf", GlobalSettings.RDFNameSpace);
		modelNameSpaces.setNsPrefix("dct", GlobalSettings.dublinCoreTermsNameSpace);
	
		String prefixes = new String();
		for (Map.Entry<String, String> entry : modelNameSpaces.getNsPrefixMap().entrySet())
		{
			prefixes = prefixes + "PREFIX " + entry.getKey() + ": <" + entry.getValue() + "> \n";
		}
		
		System.out.println(prefixes);
	}
}
