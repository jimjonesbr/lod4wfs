package de.ifgi.lod4wfs.tests;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

import de.ifgi.lod4wfs.core.Utils;


public class Test {
    public static void main(String[] args) throws Exception {
    	
    	
    	
    	
    	
	

    	 RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
    	  List<String> aList = bean.getInputArguments();

    	  for (int i = 0; i < aList.size(); i++) {
    	    System.out.println( aList.get( i ) );

    	  }
    	
    	
    	
    }

    private static void printByName(String prefix, String canonicalHostName)
            throws UnknownHostException {
        System.out.println();
        InetAddress[] allMyIps = InetAddress.getAllByName(canonicalHostName);
        for (int i = 0; i < allMyIps.length; i++) {
            String subPrefix = prefix + "[" + i + "]";
            System.out.println(subPrefix);
            System.out.println();
            InetAddress myAddress = allMyIps[i];
            printInetAddress("  " + subPrefix, myAddress);
        }
    }

    private static void printInetAddress(String prefix, InetAddress myAddress) {
        System.out.println(prefix + ".toString: " + myAddress);
        System.out.println(prefix + ".hostName: " + myAddress.getHostName());
        System.out.println(prefix + ".canonicalHostName: " + myAddress.getCanonicalHostName());
        System.out.println(prefix + ".getHostAddress: " + myAddress.getHostAddress());
    }
 }