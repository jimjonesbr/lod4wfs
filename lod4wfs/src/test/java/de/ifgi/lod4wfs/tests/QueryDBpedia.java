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
        String sparqlQueryString2 = " SELECT ?abstract " +
                                    " WHERE {{ " +
                                         "   <http://dbpedia.org/resource/Mars> " +
                                         "      <http://dbpedia.org/ontology/abstract> " +
                                         "          ?abstract }}";

        Query query = QueryFactory.create(sparqlQueryString2);
        ARQ.getContext().setTrue(ARQ.useSAX);
       //Executing SPARQL Query and pointing to the DBpedia SPARQL Endpoint 
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://DBpedia.org/sparql", query);
       //Retrieving the SPARQL Query results
        ResultSet results = qexec.execSelect();
       //Iterating over the SPARQL Query results
        
        System.out.println(query.getResultURIs().get(0).toString());
        
        while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            //Printing DBpedia entries' abstract.
//            System.out.println(soln.get("?abstract"));
//            System.out.println(results.getResultVars().size());
//            System.out.println(results.getResultVars().get(0).toString());
        }
        qexec.close();

        
    }

}