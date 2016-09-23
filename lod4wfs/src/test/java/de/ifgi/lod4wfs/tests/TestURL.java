package de.ifgi.lod4wfs.tests;

import de.ifgi.lod4wfs.core.Utils;


public class TestURL {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println(Utils.isEndpointValid("http://linkeddata.uni-muenster.de:8088/parliament/sparql"));
		System.out.println(Utils.isEndpointValid("http://dbpedia.org/sparql"));

	}

}
