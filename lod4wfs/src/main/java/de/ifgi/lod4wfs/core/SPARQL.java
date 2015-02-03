package de.ifgi.lod4wfs.core;

/**
 * Default SPARQL Queries used in the system.
 * @author jones
 * 
 */

public class SPARQL {
	
	
	public static String listFeaturePredicates = 
										" SELECT DISTINCT ?predicate (datatype(?object) AS ?dataType) " + GlobalSettings.getCrlf() +
										" WHERE { GRAPH <PARAM_LAYER> { " + GlobalSettings.getCrlf() +
										"       	?geometry ?predicate ?object . " + GlobalSettings.getCrlf() +
										"			?geometry a " + GlobalSettings.getPredicatesContainer() +  ". " + GlobalSettings.getCrlf() +
										"		} " + GlobalSettings.getCrlf() +
										"		FILTER(?predicate != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ) " + GlobalSettings.getCrlf() +
										"		FILTER(?predicate != " + GlobalSettings.getFeatureConnector() + " ) " + GlobalSettings.getCrlf() +
										" } " + GlobalSettings.getCrlf()  ; 
	
	public static String listNamedGraphs =  
										    " SELECT ?graphName ?abstract ?keywords ?title ?wkt " + GlobalSettings.getCrlf() +
											" WHERE { GRAPH ?graph { " + GlobalSettings.getCrlf() + 
											"	?graphName " + GlobalSettings.getAbstractPredicate() + " ?abstract . " + GlobalSettings.getCrlf() +
											"	?graphName " + GlobalSettings.getTitlePredicate() + " ?title . " + GlobalSettings.getCrlf() +
											"	?graphName " + GlobalSettings.getKeywordsPredicate() + " ?keywords  . " + GlobalSettings.getCrlf() +
											"	{ SELECT DISTINCT ?wkt WHERE { " + GlobalSettings.getCrlf() +
											"		GRAPH ?graph {?geometry " + GlobalSettings.getGeometryPredicate() + " ?wkt} } LIMIT 1 }} " + GlobalSettings.getCrlf() +
											" }" + GlobalSettings.getCrlf() ;		
	
	public static String getFeatureType =   " SELECT ?geometryLiteral " + GlobalSettings.getCrlf() +
											" WHERE { GRAPH <PARAM_LAYER> { " + GlobalSettings.getCrlf() +
											" 	?geometry a " + GlobalSettings.getGeometryClass() + " . " + GlobalSettings.getCrlf() +
											" 	?geometry " + GlobalSettings.getGeometryPredicate() + " ?geometryLiteral " + GlobalSettings.getCrlf() +
											" }} LIMIT 1 " + GlobalSettings.getCrlf() ;
			
}
