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
									"PREFIX sf:	 <http://www.opengis.net/ont/sf#> " +
									"PREFIX parliament: <http://parliament.semwebcentral.org/parliament#> \n";
	
//	public static String listSpatialObjects= prefixes + 
//										    " SELECT ?feature ?title ?abstract ?keywords " +
//										    " WHERE { " +
//		    								"   ?feature a geo:Feature . " +
//											"   ?feature dct:abstract ?abstract . " +
//											"   ?feature dct:title ?title . " +
//											"   ?feature dct:subject ?keywords }"; 
	

	public static String listSpatialObjectsKeywords= prefixes +
											"SELECT ?keyword WHERE { " +
											"<PARAM_SPOBJ> dct:subject ?keyword}";
	
	public static String getFeature = prefixes +
											" SELECT ?geometry ?wkt WHERE { " +
											" 	<PARAM_SPOBJ> geo:hasGeometry ?geometry . " + 
											"	?geometry geo:asWKT ?wkt } ";
	
//	public static String listGeometryPredicates = prefixes + 
//											" SELECT DISTINCT ?predicate (datatype(?object) AS ?dataType) WHERE { " +
//										    " <PARAM_SPOBJ> geo:hasGeometry ?geometry ." + 
//										    " ?geometry ?predicate ?object} ";
	
	public static String listGeometryPredicates = prefixes +
										" SELECT DISTINCT ?predicate (datatype(?object) AS ?dataType) " +
										" WHERE { GRAPH <PARAM_LAYER> { " +
										"		?geometry ?predicate ?object . " +
										"		?geometry a geo:Geometry .}  " +
										" }"; 
	
	
	public static String listNamedGraphs = prefixes +  
										   " SELECT ?graphName ?abstract ?keywords ?title " + 
											"WHERE { GRAPH ?graph { " + 
											"	?graphName dct:abstract ?abstract . " +
											"	?graphName dct:title ?title . " +
											"	?graphName dct:subject ?keywords  .} " +
											"}";		
	
	public static String getFeatureType = prefixes + "SELECT ?wkt " +
													" WHERE { GRAPH <PARAM_LAYER> { " +
													" ?geometry a geo:Geometry . " +
													" ?geometry geo:asWKT ?wkt " +
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