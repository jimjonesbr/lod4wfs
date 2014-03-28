package de.ifgi.lod4wfs.tests;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.URIParameter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.apache.xerces.util.URI;
import org.apache.xerces.util.URI.MalformedURIException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.ifgi.lod4wfs.core.WFSFeature;

public class TestNamedGraphMapping {

	private static Model model = ModelFactory.createDefaultModel();

	public static void main(String[] args) throws MalformedURLException, URISyntaxException, MalformedURIException {

		//		String prefix = new String();
		//		prefix="http://ifgi.lod4wfs.de/layer/";		
		//		


		//uri="http://ifgi.lod4wfs.de/layer/brazil_amazon_roads";

		//uri="file:///home/jones/parliament/abc?$!aaa";
		//		URI auri = new URI(uri);
		////		
		//		System.out.println("Host: " + auri.getHost());
		//		System.out.println("Path: " + auri.getPath());
		//		System.out.println(auri.getAuthority());
		//		System.out.println(auri.getScheme());
		//		System.out.println(auri.getQueryString());
		//		System.out.println(auri.getSchemeSpecificPart());
		//		System.out.println(auri.getRegBasedAuthority());
		//		
		//		QName qn = new QName(uri);
		//		

		//System.out.println("zero " + uri.charAt(0));

		Pattern pattern = Pattern.compile("[^a-z0-9A-Z_]");

		ArrayList<String> namedGraphs = new ArrayList<String>();
		namedGraphs.add("http://ifgi.lod4wfs.de/layer/brazil_amazon_roads");
		namedGraphs.add("http://ifgi.lod4wfs.de/layer2/brazil_amazon_roads");


		for (int i = 0; i < namedGraphs.size(); i++) {

			boolean scape = false;

			int size = namedGraphs.get(i).length()-1;
			int position = 0;

			while ((scape == false) && (size >= 0)) {

				Matcher matcher = pattern.matcher(Character.toString(namedGraphs.get(i).charAt(size)));

				boolean finder = matcher.find();

				if (finder==true) {
					System.out.println("Position: " + size);
					position = size;
					scape=true;
				}

				size--;
			}

			model.setNsPrefix("la"+ i, namedGraphs.get(i).substring(0, position+1) );

			//			System.out.println(namedGraphs.get(i).substring(position+1, namedGraphs.get(i).length()));
			//			System.out.println(namedGraphs.get(i).substring(0, position+1));
			//			System.out.println("la"+ i +":"+namedGraphs.get(i).substring(position+1, namedGraphs.get(i).length()));

			System.out.println(model.shortForm(namedGraphs.get(i)));
		}

		System.out.println("--------------------------------------------------");

		
		Model model2 = ModelFactory.createDefaultModel();
		model2.setNsPrefix("abc", "http://www.aaa.cc/abc/");
		model2.setNsPrefix("abc2", "http://www.aaa.cc/abc/");


		System.out.println(model2.getNsURIPrefix("http://www.aaa.cc/abc/"));
		
		System.out.println(model2.getNsPrefixMap().size());
		
		model2 = ModelFactory.createDefaultModel();
		
		System.out.println(model2.getNsPrefixMap().size());
		
		//		URL url = new URL(uri);
		//		System.out.println(url.toURI());



		//		model.setNsPrefix("abc", prefix );


		//		URL aURL = new URL("http://example.com:80/docs/books/tutorial/index.html	?name=networking#DOWNLOADING");
		//		URL aURL = new URL(uri);
		//		URL anotherURL = new URL(uri);
		//		
		//		System.out.println("protocol = " + aURL.getProtocol());
		//		System.out.println("authority = " + aURL.getAuthority());
		//		System.out.println("host = " + aURL.getHost());
		//		System.out.println("port = " + aURL.getPort());
		//		System.out.println("path = " + aURL.getPath());
		//		System.out.println("query = " + aURL.getQuery());
		//		System.out.println("filename = " + aURL.getFile());
		//		System.out.println("ref = " + aURL.getRef());


		//		System.out.println("Compacting: " + model.shortForm(uri));
		//		System.out.println("Expaning: " + model.expandPrefix(model.shortForm(uri)));
	}
}
