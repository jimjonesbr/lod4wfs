package de.ifgi.lod4wfs.tests;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class QueryDBpedia {

    public static void main(String[] args) {

        QueryDBpedia queryDBpedia = new QueryDBpedia();
        queryDBpedia.queryExternalSources();
    }

    public void queryExternalSources() {
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
									"} LIMIT 1";

        Query query = QueryFactory.create(sparqlQueryString2);
        ARQ.getContext().setTrue(ARQ.useSAX);
       
        //Executing SPARQL Query and pointing to the DBpedia SPARQL Endpoint 
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://DBpedia.org/sparql", query);
       //Retrieving the SPARQL Query results
        ResultSet results = qexec.execSelect();
       //Iterating over the SPARQL Query results
        
       
        
        while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            //Printing DBpedia entries' abstract.
            System.out.println(soln.get("?wkt"));
            System.out.println(soln.get("?lat"));
            System.out.println(soln.get("?long"));
            System.out.println(soln.get("?s"));
            
            
//            System.out.println(results.getResultVars().size());
//            System.out.println(results.getResultVars().get(0).toString());
        }
        qexec.close();

        
    }

}