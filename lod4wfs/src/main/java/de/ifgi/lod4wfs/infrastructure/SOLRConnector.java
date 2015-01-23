package de.ifgi.lod4wfs.infrastructure;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import com.hp.hpl.jena.query.ResultSet;

import de.ifgi.lod4wfs.core.GlobalSettings;
import de.ifgi.lod4wfs.core.SOLRRecord;
import de.ifgi.lod4wfs.core.WFSFeature;

/**
 * 
 * @author jones
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
			
			query.setStart(0);    
			query.setRows(feature.getLimit());
			
			query.addFilterQuery(feature.getSOLRGeometryField()+ ":\"" + feature.getSOLRSpatialConstraint() + "\"");


			if (!feature.getFields().equals("*")){

				String[] fields = new String[feature.getFields().split(",").length];
				fields = feature.getFields().split(",");

				for (int i = 0; i < fields.length; i++) {

					query.addField(fields[i]);

				}

			} else {

				query.setQuery("*");

			}

			QueryResponse response = solr.query(query);
			results= response.getResults();

		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return results;


	}

}