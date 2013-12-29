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
									"} ";

        Query query = QueryFactory.create(sparqlQueryString2);
        ARQ.getContext().setTrue(ARQ.useSAX);
       
        byte ptext[] = sparqlQueryString2.getBytes("ISO-8859-1"); 
        String value = new String(ptext, "UTF-8"); 
        
        System.out.println(value);
                     
        if(!query.hasLimit()){
        	query.setLimit(2);
        }
        
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://DBpedia.org/sparql", query);
        ResultSet results = qexec.execSelect();

		
		System.out.println(query.hasLimit());
        
        while (results.hasNext()) {
            
        	QuerySolution soln = results.nextSolution();
        	
    		for (int i = 0; i < query.getResultVars().size(); i++) {	
    		
    			System.out.println(query.getResultVars().get(i) + " " + soln.get("?" + query.getResultVars().get(i).toString()));
    			
    		}
           
        }
        
        qexec.close();

        
    }

}