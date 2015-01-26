package de.ifgi.lod4wfs.infrastructure;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import de.ifgi.lod4wfs.core.WFSFeature;

/**
 * 
 * @author Jim Jones
 * @version 1.0
 */

public class SOLRConnector {

	static Logger  logger = Logger.getLogger("SOLRConnector");

	public SOLRConnector() {
		super();
	}

	public SolrDocumentList executeQuery(WFSFeature feature){
		
		HttpSolrServer solr = new HttpSolrServer(feature.getEndpoint());
		SolrQuery query = new SolrQuery();
		SolrDocumentList results = new SolrDocumentList();

		try {
			
			logger.info("Performing query for the SOLR Feature [" + feature.getName() + "]:\n" +
					"\nEndpoint: " + feature.getEndpoint() +
					"\nSpatial Constraint: " + feature.getSOLRGeometryField()+ ":" + feature.getSOLRSpatialConstraint() + 
					"\nFilter: " + feature.getSOLRFilter() + 
					"\nFields: " + feature.getFields() +
					"\nLimit: " + feature.getLimit() + "\n"
					);
			
			query.setStart(0);    
			query.setRows(feature.getLimit());
			
			query.addFilterQuery(feature.getSOLRGeometryField()+ ":\"" + feature.getSOLRSpatialConstraint() + "\"");


			if (!feature.getFields().equals("*")){

				String[] fields = new String[feature.getFields().split(",").length];
				fields = feature.getFields().split(",");

				for (int i = 0; i < fields.length; i++) {
					
					query.setQuery("*");
					query.addField(fields[i].trim());
				
				}

			} else {

				query.setQuery("*");

			}

			
			QueryResponse response = solr.query(query);
			results = response.getResults();
			
			System.out.println(results.size());
		
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return results;


	}

}