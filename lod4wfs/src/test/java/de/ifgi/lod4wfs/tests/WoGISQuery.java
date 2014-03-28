package de.ifgi.lod4wfs.tests;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
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
import de.ifgi.lod4wfs.facade.Facade;

public class WoGISQuery {

    public static void main(String[] args) throws UnsupportedEncodingException {

        WoGISQuery queryDBpedia = new WoGISQuery();
        queryDBpedia.queryExternalSources();
    }

    public void queryExternalSources() throws UnsupportedEncodingException {
        //Defining SPARQL Query. This query lists, in all languages available, the
        //abstract entries on Wikipedia/DBpedia for the planet Mars.
        String sparqlQueryString2 = "PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#> " +
									"SELECT  ?abstract ?resource ?name ?entry (concat('POINT(', xsd:string(?long), ' ', xsd:string(?lat), ')') AS ?wkt) ?gss ?unitid " +
									"WHERE " +
									"  { ?x <http://www.w3.org/2000/01/rdf-schema#label> ?name . " +
									"    ?x <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat . " +
									"    ?x <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?long . " +
									"    ?x <http://data.ordnancesurvey.co.uk/ontology/admingeo/gssCode> ?gss . " +
									"    ?x <http://data.ordnancesurvey.co.uk/ontology/admingeo/hasUnitID> ?unitid . " +
									"    ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://data.ordnancesurvey.co.uk/ontology/admingeo/District> " +
									"    SERVICE <http://dbpedia.org/sparql/> " +
									"      { ?entry <http://www.w3.org/2000/01/rdf-schema#label> ?place . " +
									"        ?entry <http://dbpedia.org/ontology/abstract> ?abstract . " +
									"        ?entry <http://dbpedia.org/ontology/isPartOf> <http://dbpedia.org/resource/East_of_England> " +
									"        FILTER langMatches(lang(?place), 'EN') " +
									"        FILTER langMatches(lang(?abstract), 'EN') " +
									"        FILTER ( str(?place) = ?name ) " +
									"      } " +
									"  }";

        String sparql = "    prefix hxl: <http://hxl.humanitarianresponse.info/ns/#> " + 	 
" prefix geo: <http://www.opengis.net/ont/geosparql#>      " +
" SELECT DISTINCT ?apl ?apl_name ?area_name ?abstract WHERE {  " +    	
"  ?apl a hxl:APL .     	 " +
"  ?apl hxl:atLocation ?area  .  " +	
"  ?apl hxl:featureName ?apl_name . " +
"  ?area hxl:featureName ?area_name . " +
"  ?area geo:hasGeometry ?geometry .	 " +	
"  ?geometry geo:hasSerialization ?wkt . " +
"  SERVICE <http://dbpedia.org/sparql> {    " +
"    ?entry <http://www.w3.org/2000/01/rdf-schema#label> ?place . " +
"    ?entry <http://dbpedia.org/ontology/abstract> ?abstract . " +
"	 ?entry <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Place> " +
//"    FILTER langMatches(lang(?abstract), 'EN') " +
//"    FILTER langMatches(lang(?place), 'EN') " +
//"    FILTER ( ?place = ?area_name )        " +        
"  } " +
"} "; 
      
        
        String sparqlWilem = " PREFIX sem: <http://semanticweb.cs.vu.nl/2009/11/sem/> " +
			                 " PREFIX poseidon: <http://semanticweb.cs.vu.nl/poseidon/ns/instances/> " +
			                 " PREFIX eez: <http://semanticweb.cs.vu.nl/poseidon/ns/eez/> " +
			                 " PREFIX wgs84: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
			                 " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                	         " SELECT  ?place " +
							 " WHERE {" +
							 "    ?event sem:eventType ?event_type ." +
							 "    ?event sem:hasPlace ?place ." +
							 "    ?place wgs84:lat ?lat ." +
							 "    ?place wgs84:long ?long ." +
							 "	  ?place <http://semanticweb.cs.vu.nl/poseidon/ns/eez/inEEZ> ?eez ." +
							 "    ?eez   <http://www.geonames.org/ontology#inCountry> ?country ." +
							 "	  ?country <http://www.geonames.org/ontology#shortName> ?name ." +
							 "    SERVICE  <http://dbpedia.org/sparql> {    " +
							 "		?entry <http://dbpedia.org/property/commonName> ?commonName ." +
							 "      FILTER langMatches(lang(?commonName), 'EN')" +
							 "		FILTER (?commonName = ?name)" +
							 "    }" +	
							 "  } LIMIT 10";
        
        System.out.println(sparqlWilem+"\n");
        
        Query query = QueryFactory.create(sparqlWilem);
        ARQ.getContext().setTrue(ARQ.useSAX);
       
        
       
        //QueryExecution qexec = QueryExecutionFactory.sparqlService("http://data.ordnancesurvey.co.uk/datasets/os-linked-data/apis/sparql", query);
        
        //QueryExecution qexec = QueryExecutionFactory.sparqlService("http://hxl.humanitarianresponse.info/sparql", query);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://semanticweb.cs.vu.nl/lop/sparql/", query);
        
        System.out.println(query.toString());
        
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.IGNORE);
        
        
        
        
        ResultSet results = qexec.execSelect();

        
        while (results.hasNext()) {
            
        	QuerySolution soln = results.nextSolution();
        	
    		for (int i = 0; i < query.getResultVars().size(); i++) {	
    		
    			System.out.println(query.getResultVars().get(i) + " --->  " + soln.get("?" + query.getResultVars().get(i).toString()));
    			
    		}
           
    		System.out.println("\n");
        }
        
        qexec.close();


        
        System.out.println(sparqlWilem+"\n");
    }

}