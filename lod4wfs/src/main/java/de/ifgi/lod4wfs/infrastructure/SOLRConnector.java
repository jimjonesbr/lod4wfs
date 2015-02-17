package de.ifgi.lod4wfs.infrastructure;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.core.WFSFeature;

/**
 * @author Jim Jones
 */

public class SOLRConnector {

	static Logger  logger = Logger.getLogger("SOLRConnector");

	public SOLRConnector() {
		
		super();
		
	}

	@SuppressWarnings("deprecation")
	public SolrDocumentList executeQuery(WFSFeature feature){
		
		HttpSolrServer solr = new HttpSolrServer(feature.getEndpoint());
		SolrQuery query = new SolrQuery();
		SolrDocumentList results = new SolrDocumentList();

		try {
			
			logger.info("Performing query for the SOLR Feature [" + feature.getName() + "]:" + GlobalSettings.getCrLf() + GlobalSettings.getCrLf() + 
					"Title: " + feature.getTitle() + GlobalSettings.getCrLf() +
					"Endpoint: " + feature.getEndpoint() + GlobalSettings.getCrLf() +					
					"Spatial Constraint: \"" + feature.getSOLRGeometryField()+ ":" + feature.getSOLRSpatialConstraint() + "\"" + GlobalSettings.getCrLf() +
					"Filter: " + feature.getSOLRFilter() + GlobalSettings.getCrLf() + 
					"Fields: " + feature.getFields() + GlobalSettings.getCrLf() +
					"Sorting: " + feature.getSOLRSorting() + GlobalSettings.getCrLf() +
					"Limit: " + feature.getLimit() + GlobalSettings.getCrLf() 
					
					);
			
			query.setStart(0);    
			query.setRows(feature.getLimit());
			
			if(!feature.getSOLRSpatialConstraint().trim().equals("*") || !feature.getSOLRGeometryField().trim().equals("*") ){
			
				query.addFilterQuery(feature.getSOLRGeometryField()+ ":\"" + feature.getSOLRSpatialConstraint() + "\"");
				
			}
			
			if(!feature.getSOLRSorting().trim().equals("*")){
				
				ORDER order = null;
				boolean valid = true;
				
				if(feature.getSOLRSorting().split(":")[1].toUpperCase().trim().equals("ASC")){
				
					order = ORDER.asc;
					
				} else 	if(feature.getSOLRSorting().split(":")[1].toUpperCase().trim().equals("DESC")){
					
					order = ORDER.desc;
					
				} else {
					
					logger.error("Invalid sorting string. Expected \"asc\" or \"desc\", but \"" + feature.getSOLRSorting().split(":")[1] + "\" was given.");
					valid = false;
				}
				
				if(valid){
					
					query.setSortField(feature.getSOLRSorting().split(":")[0], order);
					
				}

			}
			
			if(!feature.getSOLRFilter().equals("*")){
			
				String[] filter = new String[feature.getSOLRFilter().split(",").length];
				filter = feature.getSOLRFilter().split(",");
				
				for (int i = 0; i < filter.length; i++) {
				
					query.addFilterQuery(filter[i].trim());

				}
								
			}
			
			if (!feature.getFields().equals("*")){

				String[] fields = new String[feature.getFields().split(",").length];
				fields = feature.getFields().split(",");

				for (int i = 0; i < fields.length; i++) {
					
					query.setQuery("*");
					query.addField(fields[i].trim());
				
				}

			} else {

				query.setQuery("*:*");

			}

			
			QueryResponse response = solr.query(query);
			results = response.getResults();
			
		
		} catch (SolrServerException e) {

			e.printStackTrace();
		}

		return results;


	}

}