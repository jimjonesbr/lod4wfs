package de.ifgi.lod4wfs.factory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.stream.JsonReader;

import de.ifgi.lod4wfs.core.GeographicLayer;
import de.ifgi.lod4wfs.core.GlobalSettings;

public class DynamicLayers {

	public static void main(String[] args) throws IOException {

		//loadDynamicLayers("/home/jones/Downloads/sparql/");

	}


	public static ArrayList<GeographicLayer> loadDynamicLayers(String path) {

		File[] files = new File(path).listFiles();
		
		ArrayList<GeographicLayer> result = new ArrayList<GeographicLayer>();
		
		for (File file : files) {
			
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

							layer.setQuery(jsonReader.nextString());

						} else if (name.equals("keywords")) {

							layer.setKeywords(jsonReader.nextString());

						} else if (name.equals("geometryVariable")) {

							layer.setGeometryVariable(jsonReader.nextString().replace("?", ""));

						}
																		
						layer.setLowerCorner(GlobalSettings.defaultLowerCorner);
						layer.setUpperCorner(GlobalSettings.defaultUpperCorner);
						layer.setDefaultCRS(GlobalSettings.defautlCRS);
						layer.setDynamic(true);
						
					}
					
					result.add(layer);
					jsonReader.endObject();
					jsonReader.close();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}


			}

		}

		return result;
	}




}
