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

import it.cutruzzula.lwkt.geometry.LineString;
import it.cutruzzula.lwkt.geometry.MultiLineString;
import it.cutruzzula.lwkt.geometry.MultiPoint;
import it.cutruzzula.lwkt.geometry.MultiPolygon;
import it.cutruzzula.lwkt.geometry.Point;
import it.cutruzzula.lwkt.geometry.Polygon;
import it.cutruzzula.lwkt.util.XmlUtils;

/** The GML version 2 writer, for internal use */
public class GML2 {
	
	private GML2() {}
	
	protected static String writePoint(Point p, String ns, String srsName) {
		StringBuilder sb = new StringBuilder();
		String s1;

		if(srsName != null) {
			s1 = " srsName=\"" + XmlUtils.escapeTags(srsName) + "\"";
		}
		else {
			s1 = "";
		}

		sb.append("<gml:Point" + ns + s1 + ">");
		sb.append("<gml:coordinates>" + p.getX() + "," + p.getY());

		if(p.getDimensions() == 3) {
			sb.append("," + p.getZ());
		}

		sb.append("</gml:coordinates>");
		sb.append("</gml:Point>");
		return sb.toString();
	}
	
	protected static String writeMultiPoint(MultiPoint mp, String ns, String srsName) {
		StringBuilder sb = new StringBuilder();
		String s1;

		if(srsName != null) {
			s1 = " srsName=\"" + XmlUtils.escapeTags(srsName) + "\"";
		}
		else {
			s1 = "";
		}

		sb.append("<gml:MultiPoint" + ns + s1 + ">");

		for(int i = 0; i < mp.getPoints().size(); i++) {
			sb.append("<gml:pointMember>");
			sb.append(writePoint(mp.getPoints().get(i), "", null));
			sb.append("</gml:pointMember>");
		}

		sb.append("</gml:MultiPoint>");
		return sb.toString();
	}
	
	protected static String writeLineString(LineString ls, String ns, String srsName) {
		StringBuilder sb = new StringBuilder();
		String s1;

		if(srsName != null) {
			s1 = " srsName=\"" + XmlUtils.escapeTags(srsName) + "\"";
		}
		else {
			s1 = "";
		}

		sb.append("<gml:LineString" + ns + s1 + ">");
		sb.append("<gml:coordinates>");
		StringBuilder sbPos = new StringBuilder();

		for(int i = 0; i < ls.getPoints().size(); i++) {
			sbPos.append(ls.getPoints().get(i).getX() + "," + ls.getPoints().get(i).getY());

			if(ls.getDimensions() == 3) {
				sbPos.append("," + ls.getPoints().get(i).getZ());
			}

			sbPos.append(" ");
		}

		sb.append(sbPos.toString().trim());
		sb.append("</gml:coordinates>");
		sb.append("</gml:LineString>");
		return sb.toString();
	}
	
	protected static String writeMultiLineString(MultiLineString mls, String ns, String srsName) {
		StringBuilder sb = new StringBuilder();
		String s1;

		if(srsName != null) {
			s1 = " srsName=\"" + XmlUtils.escapeTags(srsName) + "\"";
		}
		else {
			s1 = "";
		}

		sb.append("<gml:MultiLineString" + ns + s1 + ">");

		for(int i = 0; i < mls.getLineStrings().size(); i++) {
			sb.append("<gml:lineStringMember>");
			sb.append(writeLineString(mls.getLineStrings().get(i), "", null));
			sb.append("</gml:lineStringMember>");
		}

		sb.append("</gml:MultiLineString>");
		return sb.toString();
	}
	
	protected static String writePolygon(Polygon p, String ns, String srsName) {
		StringBuilder sb = new StringBuilder();
		String s1;
		MultiPoint mp;

		if(srsName != null) {
			s1 = " srsName=\"" + XmlUtils.escapeTags(srsName) + "\"";
		}
		else {
			s1 = "";
		}

		sb.append("<gml:Polygon" + ns + s1 + ">");
		sb.append("<gml:outerBoundaryIs>");
		sb.append("<gml:LinearRing>");
		sb.append("<gml:coordinates>");
		mp = p.getExterior();
		StringBuilder sbPos = new StringBuilder();

		for(int i = 0; i < mp.getPoints().size(); i++) {
			sbPos.append(mp.getPoints().get(i).getX() + "," + mp.getPoints().get(i).getY());

			if(mp.getDimensions() == 3) {
				sbPos.append("," + mp.getPoints().get(i).getZ());
			}
			
			sbPos.append(" ");
		}

		sb.append(sbPos.toString().trim());
		sb.append("</gml:coordinates>");
		sb.append("</gml:LinearRing>");
		sb.append("</gml:outerBoundaryIs>");

		for(int i = 0; i < p.getInteriors().size(); i++) {
			sb.append("<gml:innerBoundaryIs>");
			sb.append("<gml:LinearRing>");
			sb.append("<gml:coordinates>");
			mp = p.getInteriors().get(i);
			sbPos = new StringBuilder();

			for(int j = 0; j < mp.getPoints().size(); j++) {
				sbPos.append(mp.getPoints().get(j).getX() + "," + mp.getPoints().get(j).getY());

				if(mp.getDimensions() == 3) {
					sbPos.append("," + mp.getPoints().get(j).getZ());
				}
				
				sbPos.append(" ");
			}

			sb.append(sbPos.toString().trim());
			sb.append("</gml:coordinates>");
			sb.append("</gml:LinearRing>");
			sb.append("</gml:innerBoundaryIs>");
		}

		sb.append("</gml:Polygon>");
		return sb.toString();
	}
	
	protected static String writeMultiPolygon(MultiPolygon mp, String ns, String srsName) {
		StringBuilder sb = new StringBuilder();
		String s1;

		if(srsName != null) {
			s1 = " srsName=\"" + XmlUtils.escapeTags(srsName) + "\"";
		}
		else {
			s1 = "";
		}

		sb.append("<gml:MultiPolygon" + ns + s1 + ">");

		for(int i = 0; i < mp.getPolygons().size(); i++) {
			sb.append("<gml:polygonMember>");
			sb.append(writePolygon(mp.getPolygons().get(i), "", null));
			sb.append("</gml:polygonMember>");
		}

		sb.append("</gml:MultiPolygon>");
		return sb.toString();
	}

}
