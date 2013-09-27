/*
 	LWKT - A light WKT parser written in Java

 	Copyright (C) 2011 Francesco Cutruzzula' (www.cutruzzula.it)

 	This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package it.cutruzzula.lwkt.util;

import java.util.ArrayList;
import java.util.List;

/** String utility methods */
public class StringUtils {
	
	private StringUtils() {}
	
	/** Method to split a string using spaces (&quot;/\\s+/&quot; regex).
	 * @param input The input string
	 * @return A java.util.List&lt;String&gt; with tokens != ""
	 */
	public static List<String> splitSpaces(String input) {
		List<String> results = new ArrayList<String>();
		String parts[] = input.split("\\s+");
		
		for(int i = 0; i < parts.length; i++) {
			
			if(parts[i].length() > 0) {
				results.add(parts[i]);
			}
			
		}
		
		return results;
	}
	
	/** Method to split a string using commas (&quot;/\\s&#42;,\\s&#42;/&quot; regex).
	 * @param input The input string
	 * @return A java.util.List&lt;String&gt; with tokens != ""
	 */
	public static List<String> splitCommas(String input) {
		List<String> results = new ArrayList<String>();
		String parts[] = input.split("\\s*,\\s*");
		
		for(int i = 0; i < parts.length; i++) {
			
			if(parts[i].trim().length() > 0) {
				results.add(parts[i].trim());
			}
			
		}
		
		return results;
	}
	
	/** Method to split a string using parent commas, for example on input ((TOKEN1),(TOKEN2)) (&quot;/\\)\\s#42;,\\s#42;\\(/&quot; regex).
	 * @param input The input string
	 * @return A java.util.List&lt;String&gt; with tokens != ""
	 */
	public static List<String> splitParentCommas(String input) throws Exception {
		List<String> results = new ArrayList<String>();
		
		if(input.charAt(0) != '(' || input.charAt(input.length() - 1) != ')') {
			throw new Exception("Invalid input");
		}
		
		String parts[] = input.substring(1, input.length() - 1).split("\\)\\s*,\\s*\\(");
		
		for(int i = 0; i < parts.length; i++) {
			
			if(parts[i].trim().length() > 0) {
				results.add(parts[i].trim());
			}
			
		}
		
		return results;
	}
	
	public static List<String> splitDoubleParentCommas(String input) throws Exception {
		List<String> results = new ArrayList<String>();
		
		if(input.charAt(0) != '(' || input.charAt(1) != '(' || input.charAt(input.length() - 2) != ')' || input.charAt(input.length() - 1) != ')') {
			throw new Exception("Invalid input");
		}
		
		String parts[] = input.substring(2, input.length() - 2).split("\\)\\)\\s*,\\s*\\(\\(");
		
		for(int i = 0; i < parts.length; i++) {
			
			if(parts[i].trim().length() > 0) {
				results.add(parts[i].trim());
			}
			
		}
		
		return results;
	}

}