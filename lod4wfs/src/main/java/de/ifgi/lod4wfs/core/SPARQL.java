package de.ifgi.lod4wfs.core;

/**
 * Class responsible for providing all constants used in the system.  
 * 
 * @author jones
 * @version 1.0
 * 
 */
public class SPARQL {
			
	public static String listGeometryPredicates = 
										" SELECT DISTINCT ?predicate (datatype(?object) AS ?dataType) " +
										" WHERE { GRAPH <PARAM_LAYER> { " +
										"		?geometry ?predicate ?object . " +
										"		?geometry a " + GlobalSettings.getGeometryClass() + " .}  " +
										" } "; 
	
	public static String listNamedGraphs =  
										    " SELECT ?graphName ?abstract ?keywords ?title ?wkt " + 
											" WHERE { GRAPH ?graph { " + 
											"	?graphName " + GlobalSettings.getAbstractPredicate() + " ?abstract . " +
											"	?graphName " + GlobalSettings.getTitlePredicate() + " ?title . " +
											"	?graphName " + GlobalSettings.getKeywordsPredicate() + " ?keywords  . " +
											"	{ SELECT DISTINCT ?wkt WHERE { GRAPH ?graph {?geometry " + GlobalSettings.getGeometryPredicate() + " ?wkt} } LIMIT 1 }} " +
											"}";		
	
	public static String getFeatureType =   " SELECT ?wkt " +
											" WHERE { GRAPH <PARAM_LAYER> { " +
											" ?geometry a " + GlobalSettings.getGeometryClass() + " . " +
											" ?geometry " + GlobalSettings.getGeometryPredicate() + " ?wkt " +
											" }} LIMIT 1 ";
			
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