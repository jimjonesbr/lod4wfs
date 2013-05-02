package de.ifgi.lod4wfs.facade;

import java.util.ArrayList;

import de.ifgi.lod4wfs.core.SpatialObject;

public class Test {

	public static void main(String[] args) {

	ArrayList<SpatialObject> list = new ArrayList<SpatialObject>(); 
	list = Facade.getInstance().listSpatialObjects();

	for (int i = 0; i < list.size(); i++) {
		System.out.println(list.get(i).getName());
		System.out.println(list.get(i).getTitle());
		System.out.println(list.get(i).getFeatureAbstract());
		System.out.println(list.get(i).getDefaultCRS());
		System.out.println(list.get(i).getLowerCorner());
		System.out.println(list.get(i).getUpperCorner());
		for (int j = 0; j < list.get(i).getKeywords().getKeywordList().size(); j++) {
			System.out.println(list.get(i).getKeywords().getKeywordList().get(j));
		}
		
	}
	
	}
	
}
