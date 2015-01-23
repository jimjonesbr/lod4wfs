package de.ifgi.lod4wfs.facade;

import java.util.ArrayList;

import com.hp.hpl.jena.query.ResultSet;

import de.ifgi.lod4wfs.core.EscapeChars;
import de.ifgi.lod4wfs.core.WFSFeature;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.factory.FactoryFDAFeatures;
import de.ifgi.lod4wfs.factory.FactoryLOD4WFS;
import de.ifgi.lod4wfs.factory.FactoryWFS;

/**
 * @author jones
 * @version 1.0
 */
public class Facade {

	private static Facade instance;
	private FactoryFDAFeatures factoryFDA;
	private FactoryLOD4WFS factoryLOD4WFS;
	private FactoryLOD4WFS factorySOLR4WFS;

	private FactoryWFS factoryWFS;
	
	public Facade(){
		factoryFDA = new FactoryFDAFeatures();
		factoryLOD4WFS = new FactoryLOD4WFS();
		factoryWFS = new FactoryWFS();
	}

	public static Facade getInstance() {
		if (instance == null) {
			instance = new Facade();
		}
		return instance;
	}

	
	/**
	 * Methods for WFS Interface
	 */
	
	public String getFeature(WFSFeature layer){

		return factoryLOD4WFS.getFeature(layer);
	}

	public String describeFeatureType(WFSFeature feature){

		return FactoryWFS.getInstance().describeFeatureType(feature);
	}

	public String getCapabilities(String version){

		return FactoryWFS.getInstance().getCapabilities(version);
	}

	
	

	
	
	/**
	 * Methods for Web Interface
	 */
	
	public ArrayList<WFSFeature> listFDAFeatures(){
		
		return factoryFDA.listFDAFeatures(GlobalSettings.getFeatureDirectory());
	}
	
	public void addFeature(WFSFeature feature){		
		
		FactoryFDAFeatures.addFeature(feature);	
		
	}
	
	public boolean isQueryValid(String query){
		
		return FactoryFDAFeatures.isQueryValid(query);
		
	}
	
	public boolean isVariableValid(WFSFeature feature){
		
		return FactoryFDAFeatures.isVariableValid(feature);
		
	}
	
	public boolean isEndpointValid(String endpoint){
		
		return FactoryFDAFeatures.isEndpointValid(endpoint);
		
	}
	
	public boolean isFeatureNameValid(String featureName){
		
		return FactoryFDAFeatures.isFeatureNameValid(featureName);
		
	}
	
	public boolean existsFeature(String featureName){
		
		return FactoryFDAFeatures.existsFeature(featureName);
	}
	
	public void deleteFeature(WFSFeature feature){
		
		FactoryFDAFeatures.deleteFeature(feature);
	}
	
	public ResultSet executeQuery(String SPARQL, String endpoint){
		
		return factoryFDA.executeQuery(SPARQL, endpoint);
		
	}
	
	public WFSFeature getSPARQLFeature(String fileName){
		
		return factoryFDA.getFDAFeature(fileName);
		
	}
	
	public String forHTML(String string){
		
		return EscapeChars.forHTML(string);
		
	}
	
	public String getGeomeryType(String wkt){
		
		return FactoryFDAFeatures.getGeometryType(wkt);
		
	}
	
	public int getPreviewLimit(){
		
		return GlobalSettings.getPreviewLimit();
	}
}
