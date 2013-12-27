package de.ifgi.lod4wfs.tests;

import java.util.ArrayList;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.core.Triple;

public class ParseSPARQL {

	public static void main(String[] args) {
		
		String sparql = new String();
		
		sparql = "" +
		  "prefix geo:  <http://www.opengis.net/ont/geosparql/1.0#>  " +
		  "prefix my:   <http://ifgi.lod4wfs.de/resource/>    " +
		  "prefix dbpedia-owl:  <http://dbpedia.org/ontology/>    " +
		  "prefix dbpedia-prop: <http://dbpedia.org/property/>    " +
		  "prefix dbpedia:	  <http://dbpedia.org/resource/>   " +
		  "prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  " +  
		  "prefix dct: <http://purl.org/dc/terms/>   " +
		  "prefix xsd: <http://www.w3.org/2001/XMLSchema#>  " + 
		  "prefix sf:   <http://www.opengis.net/ont/sf#>    " +
		  "prefix dc:   <http://purl.org/dc/elements/1.1/>    " +
		  "prefix rdfs:				<http://www.w3.org/2000/01/rdf-schema#>  " +
		  " " + 
		  "SELECT ?geometry ?id ?description ?asWKT ?capital ?drivesOn ?language   " +
		  "WHERE {  " +
		  "	GRAPH <http://ifgi.lod4wfs.de/layer/cool_countries> {  " + 
		  "    	   ?geometry a geo:Geometry .  " +
		  "    	   ?geometry rdf:ID ?id .  " +
		  "    	   ?geometry dc:description ?description .  " +
		  "    	   ?geometry geo:asWKT ?asWKT .  " +
		  "    	   ?geometry rdfs:seeAlso ?external   }  " +			 
		  " SERVICE <http://dbpedia.org/sparql/> {  " +
		  "    	   ?external dbpedia-prop:drivesOn ?drivesOn .  " +
		  "    	   ?external dbpedia-owl:officialLanguage ?languageObj .  " +
		  "    	   ?languageObj rdfs:label ?language .  " +
		  "    	   FILTER(LANGMATCHES(LANG(?language), 'EN'))  " +
		  "    	   ?external dbpedia-owl:capital ?capitalObj .  " +
		  "    	   ?capitalObj rdfs:label ?capital .  " +
		  "    	   FILTER(LANGMATCHES(LANG(?capital), 'EN'))}}";  
		
		
		//System.out.println(QueryFactory.read(sparql));
		try {
			Query query = QueryFactory.create(sparql);				
			ArrayList<Triple> result = new ArrayList<Triple>();
			
			for (int i = 0; i < query.getResultVars().size(); i++) {
				
				Triple triple = new Triple();
				triple.setObjectDataType(GlobalSettings.defaultLiteralType);
				triple.setPredicate("?"+query.getResultVars().get(i).toString());
				result.add(triple);
				
				System.out.println(triple.getPredicate());
				System.out.println();
			}
		} catch (Exception e) {
			System.out.println("Problem!!");
		}
				
		
		
		
		

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		Model model = ModelFactory.createDefaultModel();
		
		// list the statements in the Model
		StmtIterator iter = model.listStatements();

		// print out the predicate, subject and object of each statement
		while (iter.hasNext()) {
		    Statement stmt      = iter.nextStatement();  // get next statement
		    Resource  subject   = stmt.getSubject();     // get the subject
		    Property  predicate = stmt.getPredicate();   // get the predicate
		    RDFNode   object    = stmt.getObject();      // get the object

		    System.out.print(subject.toString());
		    System.out.print(" " + predicate.toString() + " ");
		    if (object instanceof Resource) {
		       System.out.print(object.toString());
		    } else {
		        // object is a literal
		        System.out.print(" \"" + object.toString() + "\"");
		    }

		    System.out.println(" .");
		} 
		
		
		
	}
}
