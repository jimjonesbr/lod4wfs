package de.ifgi.lod4wfs.factory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.google.gson.stream.JsonReader;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

import de.ifgi.lod4wfs.core.GeographicLayer;
import de.ifgi.lod4wfs.core.GlobalSettings;

public class DynamicLayers {
	
	private static Logger logger = Logger.getLogger("DynamicLayers-Module");

	public DynamicLayers() {
		super();
		// TODO Auto-generated constructor stub
	}


	public static ArrayList<GeographicLayer> loadDynamicLayers(String path) {

		File[] files = new File(path).listFiles();
		boolean isValidQuery = false;
		
		ArrayList<GeographicLayer> result = new ArrayList<GeographicLayer>();
		
		for (File file : files) {
			//logger.info("Listing geographic layers at " + GlobalSettings.default_SPARQLEndpoint + " ...");
			
			if(file.getName().endsWith(".sparql")){
				//System.out.println("File: " + path + file.getName());

				GeographicLayer layer = new GeographicLayer();
				
				try {

					FileReader fileReader = new FileReader(path+file.getName());
					JsonReader jsonReader = new JsonReader(fileReader);
					jsonReader.beginObject();

									
					while (jsonReader.hasNext()) {

						String name = jsonReader.nextName();

						if (name.equals("abstract")) {
							
							layer.setFeatureAbstract(jsonReader.nextString());
							
						} else if (name.equals("title")) {

							layer.setTitle(jsonReader.nextString());//
														
						} else if (name.equals("name")) {

							layer.setName(jsonReader.nextString());
							
						} else if (name.equals("query")) {
							
							String tmpQuery = jsonReader.nextString();
							
							try {
								Query query = QueryFactory.create(tmpQuery);
								layer.setQuery(query);
								isValidQuery = true;
							} catch (Exception e) {
								logger.error("Invalid SPARQL Query at " + file.getName() + ". The correspondent layer won't be listed in the Capabilities Document.");
								logger.error(tmpQuery);
							}
							

						} else if (name.equals("keywords")) {

							layer.setKeywords(jsonReader.nextString());

						} else if (name.equals("geometryVariable")) {

							layer.setGeometryVariable(jsonReader.nextString().replace("?", ""));

						} else if (name.equals("endpoint")) {

							layer.setEndpoint(jsonReader.nextString());

						}
																		
						layer.setLowerCorner(GlobalSettings.defaultLowerCorner);
						layer.setUpperCorner(GlobalSettings.defaultUpperCorner);
						layer.setDefaultCRS(GlobalSettings.defautlCRS);
						layer.setDynamic(true);
						
					}
					
					
					if(isValidQuery){
						result.add(layer);
					}
					
					jsonReader.endObject();
					jsonReader.close();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
					
				} catch (IOException e) {
					logger.error("Error when parsing " + file.getName() + ".");
					logger.error(e.toString());
					e.printStackTrace();
					
				}


			}

		}

		return result;
	}




}
