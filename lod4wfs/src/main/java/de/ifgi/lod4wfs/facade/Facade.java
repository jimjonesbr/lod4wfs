package de.ifgi.lod4wfs.facade;


import de.ifgi.lod4wfs.core.GeographicLayer;
import de.ifgi.lod4wfs.factory.FactoryJena;

public class Facade {

	private static Facade instance;
	private FactoryJena factory;
	private String CapabilitiesDocument100 = new String();
	
	
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
		
		this.CapabilitiesDocument100 = factory.getCapabilities("1.0.0");
		
	}
	
	public String getFeature(GeographicLayer layer){
		
		return factory.getFeature(layer);
	}
	
	public String describeFeatureType(GeographicLayer geographicLayer){
		
		return factory.describeFeatureType(geographicLayer);
	}

	
	public String getCapabilities(String version){
		
		String result = new String();
		if (version.equals("1.0.0")) {
			result = this.CapabilitiesDocument100;
		}
		
		return result;
	}

}
