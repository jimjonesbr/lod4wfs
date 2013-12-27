package de.ifgi.lod4wfs.tests;

import java.io.FileWriter;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.ifgi.lod4wfs.core.WFSFeature;
import de.ifgi.lod4wfs.facade.Facade;


public class TestServer {

	class BagOfPrimitives {
		  private int value1 = 1;
		  private String value2 = "abc";
		  private transient int value3 = 3;
		  BagOfPrimitives() {
		    // no-args constructor
		  }
		}
	
	public static void main(String[] args) throws Exception
	{

		
        Writer writer = new FileWriter("/home/jones/Desktop/Output.json");
//
//        Gson gson = new GsonBuilder().create();
//        
//        gson.toJson("Hello", writer);
//        gson.toJson(123, writer);
//        gson.toJson(1, writer);
//        
//        		writer.close();

      
					WFSFeature feature = new WFSFeature();
					
					feature.setName("name_sipuada");

        			
        			Gson gson = new Gson();
        			String json = gson.toJson(feature); 
        				
        			//gson.toJson(json,writer);
        			writer.write(json);
        			writer.close();
        			
        			System.out.println(Facade.getInstance().isFeatureNameValid("name"));
        			
		
        			System.out.println(Facade.getInstance().existsFeature("cool_countries"));
	
	}


	

}
