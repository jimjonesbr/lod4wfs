package de.ifgi.lod4wfs.tests;

import de.ifgi.lod4wfs.factory.FactoryFDAFeatures;


public class TestURL {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println(FactoryFDAFeatures.isEndpointValid("htp://hxl.humanitarianresponse.info/sparql"));
		System.out.println(FactoryFDAFeatures.isEndpointValid("http://dbpedia.org/sparql"));

	}

}
