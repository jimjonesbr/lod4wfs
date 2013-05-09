package de.ifgi.lod4wfs.facade;

import java.util.ArrayList;
import de.ifgi.lod4wfs.core.GeographicLayer;
import de.ifgi.lod4wfs.factory.FactoryJena;

public class Facade {

	private static Facade instance;
	private FactoryJena factory;
	private String CapabilitiesDocument_v1_0_0 = new String();
	
	public Facade(){
		factory = new FactoryJena();
	}

	public static Facade getInstance() {
		if (instance == null) {
			instance = new Facade();
		}
		return instance;
	}

	public void loadCapabilitiesDocuments(){
		
		System.out.println("Loading capabilities document...");
		this.CapabilitiesDocument_v1_0_0 = factory.createCapabilitiesDocument("1.0.0");
		System.out.println("Capabilities document loaded.");
		
	}
	
	//TODO: Delete! Only supposed to be accessed at the Factory level.
	public ArrayList<GeographicLayer> listSpatialObjects(){
		
		return factory.listGeographicLayers();
	}
		
	public String getFeature(GeographicLayer spatialObject){
		
		return factory.createFeatureCollection(spatialObject);
	}
	
	public String describeFeatureType(GeographicLayer geographicLayer){
		
		return factory.describeFeatureType(geographicLayer);
	}
	
	public String getCapabilities(String version){
		
		String result = new String();
		if (version.equals("1.0.0")) {
			result = this.CapabilitiesDocument_v1_0_0;
		}
		
		return result;
	}

}
