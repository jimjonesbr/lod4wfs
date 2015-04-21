package de.ifgi.lod4wfs.facade;

import java.util.ArrayList;
import com.hp.hpl.jena.query.ResultSet;
import de.ifgi.lod4wfs.core.EscapeChars;
import de.ifgi.lod4wfs.core.Utils;
import de.ifgi.lod4wfs.core.WFSFeature;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.factory.FactoryFDAFeatures;
import de.ifgi.lod4wfs.factory.FactoryWFS;

/**
 * @author Jim Jones
 */

public class Facade {

	private static Facade instance;
	private FactoryFDAFeatures factoryFDA;	
	
	public Facade(){

		factoryFDA = new FactoryFDAFeatures();

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

		return FactoryWFS.getInstance().getFeature(layer);
	}

	public String describeFeatureType(WFSFeature feature){

		return FactoryWFS.getInstance().describeFeatureType(feature);
	}

	public String getCapabilities(String version){

		return FactoryWFS.getInstance().getCapabilities(version);
	}

	
	

	
	
	/**
	 * Methods for Web Interface (LOD4WFS)
	 */
	
	public ArrayList<WFSFeature> listFDAFeatures(){
		
		return factoryFDA.listFDAFeatures();
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
	
	public String getCoordinateReferenceSystem(String wkt){
		
		return Utils.getCoordinateReferenceSystem(wkt);
		
	}
	
	public WFSFeature getSPARQLFeature(String fileName){
		
		return factoryFDA.getFDAFeature(fileName);
		
	}
	
	public String forHTML(String string){
		
		return EscapeChars.forHTML(string);
		
	}
	
	public String getGeomeryType(String wkt){
		
		return Utils.getGeometryType(wkt);
		
	}
	
	public int getPreviewLimit(){
		
		return GlobalSettings.getPreviewLimit();
		
	}
}
