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
}
