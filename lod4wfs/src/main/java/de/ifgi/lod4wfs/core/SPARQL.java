package de.ifgi.lod4wfs.core;

/**
 * Default SPARQL Queries used in the system.
 * @author jones
 * @version 1.0
 * 
 */

public class SPARQL {
	
	
	public static String listGeometryPredicates = 
										" SELECT DISTINCT ?predicate (datatype(?object) AS ?dataType) " + GlobalSettings.crlf +
										" WHERE { GRAPH <PARAM_LAYER> { " + GlobalSettings.crlf +
										"        ?geometry ?predicate ?object . " + GlobalSettings.crlf +
										"        ?geometry a " + GlobalSettings.getGeometryClass() + " .}  " + GlobalSettings.crlf +
										"		 FILTER(?predicate != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ) " + GlobalSettings.crlf +
										" } " + GlobalSettings.crlf  ; 
	
	public static String listNamedGraphs =  
										    " SELECT ?graphName ?abstract ?keywords ?title ?wkt " + GlobalSettings.crlf +
											" WHERE { GRAPH ?graph { " + GlobalSettings.crlf + 
											"	?graphName " + GlobalSettings.getAbstractPredicate() + " ?abstract . " + GlobalSettings.crlf +
											"	?graphName " + GlobalSettings.getTitlePredicate() + " ?title . " + GlobalSettings.crlf +
											"	?graphName " + GlobalSettings.getKeywordsPredicate() + " ?keywords  . " + GlobalSettings.crlf +
											"	{ SELECT DISTINCT ?wkt WHERE { " + GlobalSettings.crlf +
											"		GRAPH ?graph {?geometry " + GlobalSettings.getGeometryPredicate() + " ?wkt} } LIMIT 1 }} " + GlobalSettings.crlf +
											" }" + GlobalSettings.crlf ;		
	
	public static String getFeatureType =   " SELECT ?wkt " + GlobalSettings.crlf +
											" WHERE { GRAPH <PARAM_LAYER> { " + GlobalSettings.crlf +
											" 	?geometry a " + GlobalSettings.getGeometryClass() + " . " + GlobalSettings.crlf +
											" 	?geometry " + GlobalSettings.getGeometryPredicate() + " ?wkt " + GlobalSettings.crlf +
											" }} LIMIT 1 " + GlobalSettings.crlf ;
			
}


/*
PREFIX geo:  <http://www.opengis.net/spec/geosparql/1.0#> 
PREFIX dct: <http://purl.org/dc/terms/> 

SELECT ?feature ?title ?abstract ?keywords WHERE {
   ?feature a geo:SpatialObject .
   ?feature dct:abstract ?abstract .
   ?feature dct:title ?title .
   ?feature dct:subject ?keywords
}


#List spatial objects and counts its geometries
PREFIX geo:  <http://www.opengis.net/spec/geosparql/1.0#> 
PREFIX dct: <http://purl.org/dc/terms/> 

SELECT ?feature (COUNT(?geometries) AS ?numberGeometries) WHERE {
   ?feature a geo:SpatialObject .
   ?feature geo:hasGeometry ?geometries .
} GROUP BY ?feature


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