package de.ifgi.lod4wfs.facade;

import java.util.ArrayList;

import de.ifgi.lod4wfs.core.SpatialObject;
import de.ifgi.lod4wfs.factory.FactoryJena;

public class Facade {

	private static Facade instance;
	private FactoryJena factory;

	public Facade(){
		factory = new FactoryJena();
	}

	public static Facade getInstance() {
		if (instance == null) {
			instance = new Facade();
		}
		return instance;
	}


	public ArrayList<SpatialObject> listSpatialObjects(){
		return factory.listSpatialObjects();
	}
	
	public String getCapabilities(){
		return factory.createCapabilitiesDocument();
	}

}
