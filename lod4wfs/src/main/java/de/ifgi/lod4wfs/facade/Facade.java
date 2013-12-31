package de.ifgi.lod4wfs.facade;

import java.util.ArrayList;

import com.hp.hpl.jena.query.ResultSet;

import de.ifgi.lod4wfs.core.EscapeChars;
import de.ifgi.lod4wfs.core.WFSFeature;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.factory.FactorySPARQLFeatures;
import de.ifgi.lod4wfs.factory.FactoryWFSJena;

/**
 * 
 * @author jones
 * @version 1.0
 */
public class Facade {

	private static Facade instance;
	private FactoryWFSJena factoryWFS;
	private FactorySPARQLFeatures factorySPARQL;

	public Facade(){
		factoryWFS = new FactoryWFSJena();
		factorySPARQL = new FactorySPARQLFeatures();
	}

	public static Facade getInstance() {
		if (instance == null) {
			instance = new Facade();
		}
		return instance;
	}

	public String getFeature(WFSFeature layer){

		return factoryWFS.getFeature(layer);
	}

	public String describeFeatureType(WFSFeature geographicLayer){

		return factoryWFS.describeFeatureType(geographicLayer);
	}

	public String getCapabilities(String version){

		return factoryWFS.getCapabilities(version);
	}

	public ArrayList<WFSFeature> listDynamicFeatures(){
		
		return factorySPARQL.listSPARQLFeatures(GlobalSettings.getSparqlDirectory());
	}
	
	public void addFeature(WFSFeature feature){		
		
		FactorySPARQLFeatures.addFeature(feature);	
		
	}
	
	public boolean isQueryValid(String query){
		
		return FactorySPARQLFeatures.isQueryValid(query);
		
	}
	
	public boolean isVariableValid(WFSFeature feature){
		
		return FactorySPARQLFeatures.isVariableValid(feature);
		
	}
	
	public boolean isEndpointValid(String endpoint){
		
		return FactorySPARQLFeatures.isEndpointValid(endpoint);
		
	}
	
	public boolean isFeatureNameValid(String featureName){
		
		return FactorySPARQLFeatures.isFeatureNameValid(featureName);
		
	}
	
	public boolean existsFeature(String featureName){
		
		return FactorySPARQLFeatures.existsFeature(featureName);
	}
	
	public void deleteFeature(WFSFeature feature){
		
		FactorySPARQLFeatures.deleteFeature(feature);
	}
	
	public ResultSet executeQuery(String SPARQL, String endpoint){
		
		return factorySPARQL.executeQuery(SPARQL, endpoint);
		
	}
	
	public WFSFeature getSPARQLFeature(String fileName){
		
		return factorySPARQL.getSPARQLFeature(fileName);
		
	}
	
	public String forHTML(String string){
		
		return EscapeChars.forHTML(string);
		
	}
}
