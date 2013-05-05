package de.ifgi.lod4wfs.core;

/**
 * Class responsible for providing all constants used in the system.  
 * 
 * @author jones
 * @version 1.0
 * 
 */
public class SPARQL {
	
	public static String prefixes = "" +
									"PREFIX geo:  <http://www.opengis.net/ont/geosparql/1.0#>  " + 
									"PREFIX my:   <http://big.that.de/1.0/>  " +
									"PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> " + 
									"PREFIX dbpedia-prop: <http://dbpedia.org/property/>  " +
									"PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + 
									"PREFIX dct: <http://purl.org/dc/terms/> " +
									"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  " +
									"PREFIX sf:	 <http://www.opengis.net/ont/sf#> \n";
	
	public static String listSpatialObjects= prefixes + 
										    " SELECT ?spatialObject ?title ?abstract ?keywords " +
										    " WHERE { " +
		    								"   ?spatialObject a geo:SpatialObject . " +
											"   ?spatialObject dct:abstract ?abstract . " +
											"   ?spatialObject dct:title ?title . " +
											"   ?spatialObject dct:subject ?keywords }"; 
	

	public static String listSpatialObjectsKeywords= prefixes +
											"SELECT ?keyword WHERE { " +
											"<PARAM_SPOBJ> dct:subject ?keyword}";
	
}


/*
PREFIX geo:  <http://www.opengis.net/spec/geosparql/1.0#> 
PREFIX dct: <http://purl.org/dc/terms/> 

SELECT ?spatialObject ?title ?abstract ?keywords WHERE {
   ?spatialObject a geo:SpatialObject .
   ?spatialObject dct:abstract ?abstract .
   ?spatialObject dct:title ?title .
   ?spatialObject dct:subject ?keywords
}


#List spatial objects and counts its geometries
PREFIX geo:  <http://www.opengis.net/spec/geosparql/1.0#> 
PREFIX dct: <http://purl.org/dc/terms/> 

SELECT ?spatialObject (COUNT(?geometries) AS ?numberGeometries) WHERE {
   ?spatialObject a geo:SpatialObject .
   ?spatialObject geo:hasGeometry ?geometries .
} GROUP BY ?spatialObject


# calculating bbox and envelope

PREFIX geo:  <http://www.opengis.net/ont/geosparql/1.0#>
PREFIX geof: <http://www.opengis.net/def/function/geosparql/>
PREFIX sf: <http://www.opengis.net/ont/sf#>

SELECT 
*
WHERE {
   ?geom geo:asWKT ?wkt .
   BIND(geof:boundary(?wkt) as ?bbox) .
   BIND(geof:envelope(?wkt) as ?envelope) .
} LIMIT 10

*/