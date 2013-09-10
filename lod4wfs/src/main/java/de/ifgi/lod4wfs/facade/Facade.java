package de.ifgi.lod4wfs.facade;

import de.ifgi.lod4wfs.core.GeographicLayer;
import de.ifgi.lod4wfs.factory.FactoryJena;

/**
 * 
 * @author jones
 * @version 1.0
 */
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

	public String getFeature(GeographicLayer layer){

		return factory.getFeature(layer);
	}

	public String describeFeatureType(GeographicLayer geographicLayer){

		return factory.describeFeatureType(geographicLayer);
	}

	public String getCapabilities(String version){

		return factory.getCapabilities(version);
	}

}
