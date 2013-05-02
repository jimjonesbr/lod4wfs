package de.ifgi.lod4wfs.infrastructure;

import org.apache.log4j.Logger;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;

public class JenaConnector {
	String endpointURL;
	static Logger  logger = Logger.getLogger("JenaConnector.class");
	
	public JenaConnector(String url) {
		super();
		endpointURL = url;
	}

	public ResultSet executeQuery(String SPARQL){
		
		Query query = QueryFactory.create(SPARQL);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(this.endpointURL, query);
		ResultSet results = qexec.execSelect();

		return results;
		
	}

}