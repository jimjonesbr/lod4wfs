package de.ifgi.lod4wfs.core;

/**
 * Default SPARQL Queries used in the system. (Used for SDA Features only)
 * @author Jim Jones
 */

public class SPARQL {
	
	
	public static String listFeaturePredicates = 
										" SELECT DISTINCT ?predicate (datatype(?object) AS ?dataType) " + GlobalSettings.getCrLf() +
										" WHERE { GRAPH <PARAM_LAYER> { " + GlobalSettings.getCrLf() +
										"       	?geometry ?predicate ?object . " + GlobalSettings.getCrLf() +
										"			?geometry a " + GlobalSettings.getPredicatesContainer() +  ". " + GlobalSettings.getCrLf() +
										"		} " + GlobalSettings.getCrLf() +
										"		FILTER(?predicate != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ) " + GlobalSettings.getCrLf() +
										"		FILTER(?predicate != " + GlobalSettings.getFeatureConnector() + " ) " + GlobalSettings.getCrLf() +
										" } " + GlobalSettings.getCrLf()  ; 
	
	public static String listNamedGraphs =  
										    " SELECT ?graphName ?abstract ?keywords ?title ?wkt " + GlobalSettings.getCrLf() +
											" WHERE { GRAPH ?graph { " + GlobalSettings.getCrLf() + 
											"	?graphName " + GlobalSettings.getAbstractPredicate() + " ?abstract . " + GlobalSettings.getCrLf() +
											"	?graphName " + GlobalSettings.getTitlePredicate() + " ?title . " + GlobalSettings.getCrLf() +
											"	?graphName " + GlobalSettings.getKeywordsPredicate() + " ?keywords  . " + GlobalSettings.getCrLf() +
											"	{ SELECT DISTINCT ?wkt WHERE { " + GlobalSettings.getCrLf() +
											"		GRAPH ?graph {?geometry " + GlobalSettings.getGeometryPredicate() + " ?wkt} } LIMIT 1 }} " + GlobalSettings.getCrLf() +
											" }" + GlobalSettings.getCrLf() ;		
	
	public static String getFeatureType =   " SELECT ?geometryLiteral " + GlobalSettings.getCrLf() +
											" WHERE { GRAPH <PARAM_LAYER> { " + GlobalSettings.getCrLf() +
											" 	?geometry a " + GlobalSettings.getGeometryClass() + " . " + GlobalSettings.getCrLf() +
											" 	?geometry " + GlobalSettings.getGeometryPredicate() + " ?geometryLiteral " + GlobalSettings.getCrLf() +
											" }} LIMIT 1 " + GlobalSettings.getCrLf() ;
			
}
