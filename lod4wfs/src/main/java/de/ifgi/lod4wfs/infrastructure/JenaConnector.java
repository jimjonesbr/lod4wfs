package de.ifgi.lod4wfs.infrastructure;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.jena.atlas.web.HttpException;
import org.apache.log4j.Logger;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;

import de.ifgi.lod4wfs.core.Utils;

/**
 * 
 * @author Jim Jones
 * @version 1.0
 */

public class JenaConnector {

	static Logger  logger = Logger.getLogger("JenaConnector");
	
	public JenaConnector() {
		super();
	}

	public ResultSet executeQuery(String SPARQL, String endpoint){
	
		
		ResultSet results = null;
		
		if (Utils.isEndpointValid(endpoint)){
				
			Query query = QueryFactory.create(SPARQL);
			logger.info("SPARQL Query fired at the endpoint [" + endpoint + "]: \n\n" + SPARQL + "\n\n");
			QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
			
			results = qexec.execSelect();
				
		}
		
		return results;
		
	}

}