package de.ifgi.lod4wfs.tests;

import it.cutruzzula.lwkt.WKTParser;
import it.cutruzzula.lwkt.util.XmlUtils;

public class TestParseWKT {

	public static void main(String[] args) {
		
		
		//String wkt= "POLYGON ((30 10, 10 20, 20 40, 40 40, 30 10))";
		//String wkt="POLYGON ((-61.6866679999999974 17.0244409999999995, -61.8872220000000013 17.1052740000000014, -61.7944490000000002 17.1633299999999984, -61.6866679999999974 17.0244409999999995)) POLYGON ((-61.7291719999999984 17.6086080000000003, -61.8530579999999972 17.5830540000000006, -61.8730619999999973 17.7038879999999992, -61.7291719999999984 17.6086080000000003))";
		String wkt="POINT ( +007.966666 +053.483055 )";
		//String wkt = "LINESTRING (146.4685819999999978 -41.2414780000000007, 146.5747680000000059 -41.2511859999999970, 146.6404110000000003 -41.2551539999999974, 146.7661290000000065 -41.3323480000000032, 146.7941889999999887 -41.3441699999999983, 146.8221739999999897 -41.3629880000000014, 146.8634340000000122 -41.3802340000000015, 146.8995209999999929 -41.3794520000000006, 146.9295040000000085 -41.3782270000000025, 147.0080409999999915 -41.3560790000000011, 147.0983429999999998 -41.3629189999999980)";
		
		try {
			//To get output as GML version 2:
			String output1 = WKTParser.parseToGML2(wkt);
			
			//To get output as GML version 3;
			String output2 = WKTParser.parseToGML2(wkt,"EPSG:4326");

			//To get output as GML version 3, setting EPSG:4326 as srs-name;
			String output3 = WKTParser.parseToGML3(wkt, "EPSG:4326");
			
			System.out.println(output1);
			System.out.println(output2);
			System.out.println(output3);
			
//			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//			Document document = documentBuilder.parse("src/main/resources/NewFile.xml");
					  
//			System.out.println("xyz ->"  + document.getDocumentElement().getNodeName());

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
