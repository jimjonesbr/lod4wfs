package de.ifgi.lod4wfs.tests;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

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
        String sparqlQueryString2 = "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> " + 
									"PREFIX dbo: <http://dbpedia.org/ontology/>" +
									"SELECT ?s ?lat ?long (CONCAT('POINT(',?long, ' ', ?lat, ')') as ?wkt)" +
									"WHERE {" +
									"  ?s a dbo:Place ." +
									" ?s geo:lat ?lat ." +
									"  ?s geo:long ?long . " +
									//"BIND (CONCAT('POINT(',?long, ' ', ?lat, ')') as ?wkt)" +
									"} limit 10";

        Query query = QueryFactory.create(sparqlQueryString2);
        ARQ.getContext().setTrue(ARQ.useSAX);
       
       
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
        ResultSet results = qexec.execSelect();

        /* Total number of processors or cores available to the JVM */
        System.out.println("Available processors (cores): " + 
        Runtime.getRuntime().availableProcessors());

        /* Total amount of free memory available to the JVM */
        System.out.println("Free memory (bytes): " + 
        (Runtime.getRuntime().freeMemory()/1024)/1024);

        /* This will return Long.MAX_VALUE if there is no preset limit */
        long maxMemory = Runtime.getRuntime().maxMemory()/1024/1024;
        /* Maximum amount of memory the JVM will attempt to use */
        System.out.println("Maximum memory (bytes): " + 
        (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

        /* Total memory currently in use by the JVM */
        System.out.println("Total memory (bytes): " + 
        Runtime.getRuntime().totalMemory()/1024/1024);
   
        System.out.println("free memory: " + Runtime.getRuntime().freeMemory() / 1024/1024); 
        
        while (results.hasNext()) {
            
        	QuerySolution soln = results.nextSolution();
        	
    		for (int i = 0; i < query.getResultVars().size(); i++) {	
    		
    			//System.out.println(query.getResultVars().get(i) + " " + soln.get("?" + query.getResultVars().get(i).toString()));
    			
    		}
           
        }
        
        qexec.close();

        
    }

}