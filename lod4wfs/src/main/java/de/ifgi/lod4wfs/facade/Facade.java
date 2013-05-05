package de.ifgi.lod4wfs.facade;

import java.util.ArrayList;
import de.ifgi.lod4wfs.core.SpatialObject;
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
		
		this.CapabilitiesDocument_v1_0_0 = factory.createCapabilitiesDocument("1.0.0");
		
	}
	
	//TODO: Delete! Only supposed to be accessed at the Factory level.
	public ArrayList<SpatialObject> listSpatialObjects(){
		return factory.listSpatialObjects();
	}
	
	
	public String getCapabilities(String version){
		
		String result = new String();
		if (version.equals("1.0.0")) {
			result = this.CapabilitiesDocument_v1_0_0;
		}
		
		return result;
	}

}
