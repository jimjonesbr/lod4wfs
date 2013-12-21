package de.ifgi.lod4wfs.infrastructure;

import org.apache.log4j.Logger;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.reasoner.rulesys.builtins.IsDType;

/**
 * 
 * @author jones
 * @version 1.0
 */

public class JenaConnector {

	static Logger  logger = Logger.getLogger("JenaConnector.class");
	
	public JenaConnector() {
		super();
	}

	public ResultSet executeQuery(String SPARQL, String endpoint){
		
		Query query = QueryFactory.create(SPARQL);
		
		
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
		ResultSet results = qexec.execSelect();
		
		return results;
		
	}

}