package de.ifgi.lod4wfs.tests;

import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * source : www.javabeat.net
 */
public class Log4jXmlTest {
	private static Logger logger = Logger.getLogger
			(Log4jXmlTest.class);
	public static void main (String args[]){
		DOMConfigurator.configure("src/main/resources/log4j.xml");

		try {

			SimpleLayout layout = new SimpleLayout();
			ConsoleAppender consoleAppender = new ConsoleAppender( layout );
			FileAppender fileAppender;

			fileAppender = new FileAppender( layout, "logs/MeineLogDatei.log", true );

			logger.addAppender( fileAppender );
			
		} catch (IOException e) {
			e.printStackTrace();
		}


		
		logger.info("Test Log 3");
	}

}