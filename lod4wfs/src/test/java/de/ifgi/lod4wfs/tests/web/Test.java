package de.ifgi.lod4wfs.tests.web;

import java.util.ArrayList;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import de.ifgi.lod4wfs.core.GeographicLayer;
import de.ifgi.lod4wfs.facade.Facade;
import de.ifgi.lod4wfs.factory.FactoryJena;

public class Test {

	public static void main(String[] args) {

//		ArrayList<SpatialObject> list = new ArrayList<SpatialObject>(); 
//		list = Facade.getInstance().listSpatialObjects();
//
//		for (int i = 0; i < list.size(); i++) {
//			System.out.println(list.get(i).getName());
//			System.out.println(list.get(i).getTitle());
//			System.out.println(list.get(i).getFeatureAbstract());
//			System.out.println(list.get(i).getDefaultCRS());
//			System.out.println(list.get(i).getLowerCorner());
//			System.out.println(list.get(i).getUpperCorner());
//			System.out.println(list.get(i).getKeywords());
//
//
//		}

		
	GeographicLayer layer = new GeographicLayer();
	layer.setName("http://ifgi.lod4wfs.de/layer/world");
	 

	//System.out.println(Facade.getInstance().describeFeatureType(geo));
	
	FactoryJena f = new FactoryJena();
	//f.createGetFeatureSPARQL(geo);
	
	
	
	//f.getFeatureType(geo);

	
	
//	FactoryJena fac = new FactoryJena();
//	fac.listGeometyPredicates(geo);
	
//	
//    Model m = ModelFactory.createDefaultModel();
//    String xsd = "http://www.w3.org/2001/XMLSchema#";
//    m.setNsPrefix( "xsd", xsd);
//    
//    m.write( System.out, "TURTLE" );
    
    }

}
