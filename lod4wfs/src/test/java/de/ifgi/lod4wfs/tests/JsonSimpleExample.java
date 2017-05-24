package de.ifgi.lod4wfs.tests;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;


public class JsonSimpleExample {
	
     public static void main(String[] args) {

		JSONObject obj = new JSONObject();
		obj.put("name", "mkyong.com");
		obj.put("age", new Integer(100));
	
//		JSONArray list = new JSONArray();
	
		JSONArray tmp = new JSONArray();
		
		tmp.put(obj);
		tmp.put(obj);
	
//	try {
//
//		FileWriter file = new FileWriter("/home/jones/Schreibtisch/test.json");
//		file.write(obj.toString());
//		file.flush();
//		file.close();
//
//	} catch (IOException e) {
//		e.printStackTrace();
//	}

	System.out.print(tmp);

     }

}