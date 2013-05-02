package de.ifgi.lod4wfs.factory;

import java.util.ArrayList;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import de.ifgi.lod4wfs.core.Constants;
import de.ifgi.lod4wfs.core.Keywords;
import de.ifgi.lod4wfs.core.SpatialObject;
import de.ifgi.lod4wfs.infrastructure.JenaConnector;

public class FactoryJena {

	private static FactoryJena instance;
	private static JenaConnector jn;

	public FactoryJena(){
		jn = new JenaConnector(Constants.SPARQL_Endpoint);

	}

	public static FactoryJena getInstance() {

		if (instance == null) {
			instance = new FactoryJena();
		}
		return instance;
	}

	public ArrayList<SpatialObject> listSpatialObjects(){

		ResultSet rs = jn.executeQuery(Constants.listSpatialObjects);
		ArrayList<SpatialObject> result = new ArrayList<SpatialObject>();
		SpatialObject sp = new SpatialObject();
		
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			sp.setName(soln.get("?spatialObject").toString());
			sp.setTitle(soln.getLiteral("?title").getValue().toString());
			sp.setFeatureAbstract(soln.getLiteral("?abstract").getValue().toString());
			sp.setKeywords(this.listSpatialObjectsKeywords(soln.get("?spatialObject").toString()));
			sp.setLowerCorner(Constants.lowerCorner);
			sp.setUpperCorner(Constants.upperCorner);
			sp.setDefaultCRS(Constants.defautlCRS);
			result.add(sp);
		}

		return result;
	}
	
	
	public Keywords listSpatialObjectsKeywords(String spatialObject){
		ResultSet rs = jn.executeQuery(Constants.listSpatialObjectsKeywords.replace("PARAM_SPOBJ", spatialObject));
		Keywords keyword = new Keywords();
		ArrayList<String> arrayList = new ArrayList<String>();
		
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			arrayList.add(soln.getLiteral("?keyword").getValue().toString());
		}
		
		keyword.setKeywordList(arrayList);
		
		return keyword;
	}
}
