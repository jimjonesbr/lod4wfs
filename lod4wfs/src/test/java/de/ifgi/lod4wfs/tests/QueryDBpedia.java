package de.ifgi.lod4wfs.tests;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.jena.query.ARQ;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.core.Triple;

public class QueryDBpedia {

    public static void main(String[] args) throws UnsupportedEncodingException {

        QueryDBpedia queryDBpedia = new QueryDBpedia();
        queryDBpedia.queryExternalSources();
    }

    public void queryExternalSources() throws UnsupportedEncodingException {
        //Defining SPARQL Query. This query lists, in all languages available, the
        //abstract entries on Wikipedia/DBpedia for the planet Mars.

    	
//    	String sparqlQueryString2 = "prefix maps:<http://geographicknowledge.de/vocab/maps#> " +
//"        			prefix phen:<http://geographicknowledge.de/vocab/historicmapsphen#> " +
//"prefix dbp:<http://dbpedia.org/resource/> " +
//"prefix dbp-de:<http://de.dbpedia.org/resource/> " +
//"prefix xsd:<http://www.w3.org/2001/XMLSchema#> " +
//"prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
//"prefix time:<http://www.w3.org/2006/time#> " +
//"prefix sf:<http://www.opengis.net/ont/sf#> " +
//"prefix geo:<http://www.opengis.net/ont/geosparql/1.0#> " +
//"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> " +
//
//" SELECT DISTINCT ?map ?class WHERE { " +
//" ?map maps:represents ?g . " +
//" ?map maps:mapsTime '1840'^^xsd:gYear . " +
//" GRAPH ?g {dbp:Hildesheim ?p ?o . " +
//" ?instance a ?cl . " +
//" }  " +
//" ?instance a ?class .  " +
//" ?class rdfs:subClassOf phen:Landcover.  " +
//" }";

    	String sparqlQueryString2 = "SELECT ?geo ?gml WHERE {<http://example.com/my/fme-gen-4f929834-7188-452c-bbc0-b17eedd08727> <http://www.opengis.net/ont/geosparql#hasGeometry> ?geo ." +
    												"?geo <http://www.opengis.net/ont/geosparql#asGML> ?gml} LIMIT 10";
    	Query query = QueryFactory.create(sparqlQueryString2);
        ARQ.getContext().setTrue(ARQ.useSAX);
       
       
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://ec2-54-229-171-74.eu-west-1.compute.amazonaws.com:7200/repositories/stan_data", query);
        ResultSet results = qexec.execSelect();

        
        while (results.hasNext()) {
            
        	QuerySolution soln = results.nextSolution();
        	
    		for (int i = 0; i < query.getResultVars().size(); i++) {	
    		
    			System.out.println(query.getResultVars().get(i) + " " + soln.get("?" + query.getResultVars().get(i).toString()));
    			
    		}
           
        }
        
        qexec.close();

        
    }

}