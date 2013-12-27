package de.ifgi.lod4wfs.facade;

import java.util.ArrayList;

import de.ifgi.lod4wfs.core.WFSFeature;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.factory.FactoryDynamicFeatures;
import de.ifgi.lod4wfs.factory.FactoryWFSJena;

/**
 * 
 * @author jones
 * @version 1.0
 */
public class Facade {

	private static Facade instance;
	private FactoryWFSJena factory;

	public Facade(){
		factory = new FactoryWFSJena();
	}

	public static Facade getInstance() {
		if (instance == null) {
			instance = new Facade();
		}
		return instance;
	}

	public String getFeature(WFSFeature layer){

		return factory.getFeature(layer);
	}

	public String describeFeatureType(WFSFeature geographicLayer){

		return factory.describeFeatureType(geographicLayer);
	}

	public String getCapabilities(String version){

		return factory.getCapabilities(version);
	}

	public ArrayList<WFSFeature> listDynamicFeatures(){
		
		return FactoryDynamicFeatures.listDynamicFeatures(GlobalSettings.getSparqlDirectory());
	}
	
	public void addFeature(WFSFeature feature){		
		
		FactoryDynamicFeatures.addFeature(feature);	
		
	}
	
	public boolean isQueryValid(String query){
		
		return FactoryDynamicFeatures.isQueryValid(query);
		
	}
	
	public boolean isVariableValid(WFSFeature feature){
		
		return FactoryDynamicFeatures.isVariableValid(feature);
		
	}
	
	public boolean isEndpointValid(String endpoint){
		
		return FactoryDynamicFeatures.isEndpointValid(endpoint);
		
	}
	
	public boolean isFeatureNameValid(String featureName){
		
		return FactoryDynamicFeatures.isFeatureNameValid(featureName);
		
	}
	
	public boolean existsFeature(String featureName){
		
		return FactoryDynamicFeatures.existsFeature(featureName);
	}
}
