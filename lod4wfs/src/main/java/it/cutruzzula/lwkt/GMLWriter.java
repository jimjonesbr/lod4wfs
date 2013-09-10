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

/** The core of GML writer, for internal use */
public class GMLWriter {

	final private static String GMLNS = " xmlns:gml=\"http://www.opengis.net/gml\"";
	
	private GMLWriter() {}

	protected static String getGML(AbstractGeometry input, int gmlVersion, String srsName) throws Exception {
		String result;

		if(input instanceof Point) {
			result = writePoint((Point)input, GMLNS, srsName, gmlVersion);
		}
		else if(input instanceof MultiPoint) {
			result = writeMultiPoint((MultiPoint)input, GMLNS, srsName, gmlVersion);
		}
		else if(input instanceof LineString) {
			result = writeLineString((LineString)input, GMLNS, srsName, gmlVersion);
		}
		else if(input instanceof MultiLineString) {
			result = writeMultiLineString((MultiLineString)input, GMLNS, srsName, gmlVersion);
		}
		else if(input instanceof Polygon) {
			result = writePolygon((Polygon)input, GMLNS, srsName, gmlVersion);
		}
		else if(input instanceof MultiPolygon) {
			result = writeMultiPolygon((MultiPolygon)input, GMLNS, srsName, gmlVersion);
		}
		else {
			throw new Exception("Invalid input");
		}

		return result;
	}

	private static String writePoint(Point p, String ns, String srsName, int version) {

		if(version == 2) {
			return GML2.writePoint(p, ns, srsName);
		}
		else {
			return GML3.writePoint(p, ns, srsName);
		}

	}

	private static String writeMultiPoint(MultiPoint mp, String ns, String srsName, int version) {

		if(version == 2) {
			return GML2.writeMultiPoint(mp, ns, srsName);
		}
		else {
			return GML3.writeMultiPoint(mp, ns, srsName);
		}

	}

	private static String writeLineString(LineString ls, String ns, String srsName, int version) {

		if(version == 2) {
			return GML2.writeLineString(ls, ns, srsName);
		}
		else {
			return GML3.writeCurve(ls, ns, srsName);
		}

	}

	private static String writeMultiLineString(MultiLineString mls, String ns, String srsName, int version) {

		if(version == 2) {
			return GML2.writeMultiLineString(mls, ns, srsName);
		}
		else {
			return GML3.writeMultiCurveV3(mls, ns, srsName);
		}

	}
	
	private static String writePolygon(Polygon p, String ns, String srsName, int version) {

		if(version == 2) {
			return GML2.writePolygon(p, ns, srsName);
		}
		else {
			return GML3.writePolygon(p, ns, srsName);
		}

	}

	private static String writeMultiPolygon(MultiPolygon mp, String ns, String srsName, int version) {

		if(version == 2) {
			return GML2.writeMultiPolygon(mp, ns, srsName);
		}
		else {
			return GML3.writeMultiSurface(mp, ns, srsName);
		}

	}

}
