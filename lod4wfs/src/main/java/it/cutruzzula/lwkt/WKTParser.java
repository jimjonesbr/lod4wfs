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

package it.cutruzzula.lwkt;

import it.cutruzzula.lwkt.geometry.AbstractGeometry;
import it.cutruzzula.lwkt.geometry.LineString;
import it.cutruzzula.lwkt.geometry.MultiLineString;
import it.cutruzzula.lwkt.geometry.MultiPoint;
import it.cutruzzula.lwkt.geometry.MultiPolygon;
import it.cutruzzula.lwkt.geometry.Point;
import it.cutruzzula.lwkt.geometry.Polygon;
import it.cutruzzula.lwkt.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/** The core class of the LWKT parser */
public class WKTParser {

	private WKTParser() {}

	/** Method to parse a WKT string to get GML 2 output.<br/><br/>
	 * @param input The WKT string
	 * @return The GML v2 content
	 * @throws Exception A generic exception thrown in parsing
	 */
	public static String parseToGML2(String input) throws Exception {
		return GMLWriter.getGML(parse(input), 2, null);
	}

	/** Method to parse a WKT string to get GML 2 output.<br/><br/>
	 * @param input The WKT string
	 * @param srsName The SRS to write in output GML
	 * @return The GML v2 content
	 * @throws Exception A generic exception thrown in parsing
	 */
	public static String parseToGML2(String input, String srsName) throws Exception {
		return GMLWriter.getGML(parse(input), 2, srsName);
	}

	/** Method to parse a WKT string to get GML 3 output (for example MultiSurface instead of MultiPolygon).<br/><br/>
	 * @param input The WKT string
	 * @return The GML v3 content
	 * @throws Exception A generic exception thrown in parsing
	 */
	public static String parseToGML3(String input) throws Exception {
		return GMLWriter.getGML(parse(input), 3, null);
	}

	/** Method to parse a WKT string to get GML 3 output (for example MultiSurface instead of MultiPolygon).<br/><br/>
	 * @param input The WKT string
	 * @param srsName The SRS to write in output GML
	 * @return The GML v3 content
	 * @throws Exception A generic exception thrown in parsing
	 */
	public static String parseToGML3(String input, String srsName) throws Exception {
		return GMLWriter.getGML(parse(input), 3, srsName);
	}

	/** Method to parse a WKT string.<br/><br/>
	 * @param input The WKT string
	 * @return The An AbstractGeometry object (instance of Point, or MultiPoint, or LineString, or MultiLineString, or Polygon, or MultiPolygon)
	 * @throws Exception A generic exception thrown in parsing
	 */
	public static AbstractGeometry parse(String input) throws Exception {
		AbstractGeometry result;

		if(input == null || !input.trim().matches("^(\\w+)(\\sZ){0,1}\\s*\\(\\s*(.*)\\s*\\)$")) {
			System.out.println("DELETE-ME: - >> "+input);
			throw new Exception("Invalid input");
		}

		String s = input.substring(0, input.indexOf('(')).trim();
		String content = input.substring(input.indexOf('(') + 1, input.lastIndexOf(')'));

		if(s.equals("POINT")) {
			result = pointFromWkt(content, 2);
		}
		else if(s.equals("POINTZ") || s.equals("POINT Z")) {
			result = pointFromWkt(content, 3);
		}
		else if(s.equals("MULTIPOINT")) {
			result = multiPointFromWkt(content, 2);
		}
		else if(s.equals("MULTIPOINTZ") || s.equals("MULTIPOINT Z")) {
			result = multiPointFromWkt(content, 3);
		}
		else if(s.equals("LINESTRING")) {
			result = lineStringFromWkt(content, 2);
		}
		else if(s.equals("LINESTRINGZ") || s.equals("LINESTRING Z")) {
			result = lineStringFromWkt(content, 3);
		}
		else if(s.equals("MULTILINESTRING")) {
			result = multiLineStringFromWkt(content, 2);
		}
		else if(s.equals("MULTILINESTRINGZ") || s.equals("MULTILINESTRING Z")) {
			result = multiLineStringFromWkt(content, 3);
		}
		else if(s.equals("POLYGON")) {
			result = polygonFromWkt(content, 2);
		}
		else if(s.equals("POLYGONZ") || s.equals("POLYGON Z")) {
			result = polygonFromWkt(content, 3);
		}
		else if(s.equals("MULTIPOLYGON")) {
			result = multiPolygonFromWkt(content, 2);
		}
		else if(s.equals("MULTIPOLYGONZ") || s.equals("MULTIPOLYGON Z")) {
			result = multiPolygonFromWkt(content, 3);
		}
		else {
			throw new Exception("Invalid input");
		}

		return result;
	}

	private static Point pointFromWkt(String input, int dims) throws Exception {
		Point result;
		List<String> coords = StringUtils.splitSpaces(input);

		if(coords.size() != dims) {
			throw new Exception("Invalid dimension");
		}

		if(dims == 3) {
			result = new Point(Double.parseDouble(coords.get(0)), Double.parseDouble(coords.get(1)), Double.parseDouble(coords.get(2)));
		}
		else {
			result = new Point(Double.parseDouble(coords.get(0)), Double.parseDouble(coords.get(1)));
		}

		return result;
	}

	private static MultiPoint multiPointFromWkt(String input, int dims) throws Exception {
		List<String> parts = StringUtils.splitCommas(input);
		List<Point> points = new ArrayList<Point>();
		Point p;

		for(int i = 0; i < parts.size(); i++) {
			p = pointFromWkt(parts.get(i), dims);

			if(p.getDimensions() != dims) {
				throw new Exception("Invalid dimension");
			}

			points.add(p);
		}

		return new MultiPoint(points);
	}

	private static LineString lineStringFromWkt(String input, int dims) throws Exception {
		List<String> parts = StringUtils.splitCommas(input);
		List<Point> points = new ArrayList<Point>();
		Point p;

		for(int i = 0; i < parts.size(); i++) {
			p = pointFromWkt(parts.get(i), dims);

			if(p.getDimensions() != dims) {
				throw new Exception("Invalid dimension");
			}

			points.add(p);
		}

		return new LineString(points);
	}

	private static MultiLineString multiLineStringFromWkt(String input, int dims) throws Exception {
		List<String> parts = StringUtils.splitParentCommas(input);
		List<LineString> lineStrings = new ArrayList<LineString>();
		LineString ls;

		for(int i = 0; i < parts.size(); i++) {
			ls = lineStringFromWkt(parts.get(i), dims);

			if(ls.getDimensions() != dims) {
				throw new Exception("Invalid dimension");
			}

			lineStrings.add(ls);
		}

		return new MultiLineString(lineStrings);
	}

	private static Polygon polygonFromWkt(String input, int dims) throws Exception {
		List<String> parts = StringUtils.splitParentCommas(input);
		List<MultiPoint> multiPoints = new ArrayList<MultiPoint>();
		MultiPoint mp;

		for(int i = 0; i < parts.size(); i++) {
			mp = multiPointFromWkt(parts.get(i), dims);

			if(mp.getDimensions() != dims) {
				throw new Exception("Invalid dimension");
			}

			multiPoints.add(mp);
		}

		return new Polygon(multiPoints);
	}

	private static MultiPolygon multiPolygonFromWkt(String input, int dims) throws Exception {
		List<String> parts = StringUtils.splitDoubleParentCommas(input);
		List<Polygon> polygons = new ArrayList<Polygon>();
		Polygon p;

		for(int i = 0; i < parts.size(); i++) {
			p = polygonFromWkt("(" + parts.get(i) + ")", dims);

			if(p.getDimensions() != dims) {
				throw new Exception("Invalid dimension");
			}

			polygons.add(p);
		}

		return new MultiPolygon(polygons);
	}

}
