package de.ifgi.lod4wfs.tests;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.stream.JsonReader;

public class GsonStreamAPIExample {

	private static final String jsonFilePath = "/home/jones/Downloads/sparql/test.sparql";

	public static void main(String[] args) {

		try {

			FileReader fileReader = new FileReader(jsonFilePath);

			JsonReader jsonReader = new JsonReader(fileReader);

			jsonReader.beginObject();

			while (jsonReader.hasNext()) {

				String name = jsonReader.nextName();

				if (name.equals("abstract")) {

					System.out.println("abstract: "+jsonReader.nextString());

				} else if (name.equals("title")) {

					System.out.println("title: "+jsonReader.nextString());

				} else if (name.equals("name")) {

					System.out.println("name: "+jsonReader.nextString());
					
				} else if (name.equals("query")) {

					System.out.println("query: "+jsonReader.nextString());
					
				} else if (name.equals("keywords")) {

					System.out.println("keywords: "+jsonReader.nextString());
					
				}
			}

			jsonReader.endObject();
			jsonReader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}