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

package it.cutruzzula.lwkt.geometry;

import java.util.List;

/** The class represents a LINESTRING object. */
public class LineString extends AbstractGeometry {

	private List<Point> points;

	/** Constructor.
	 * @param points A java.util.List of Point objects
	 * @throws A generic exception if dimensions are not homogeneous
	 */
	public LineString(List<Point> points) throws Exception {
		this.points = points;

		if(!this.validate()) {
			throw new Exception("Invalid linestring");
		}

		this.dimensions = this.points.get(0).getDimensions();
	}

	/** Method to get the list of points.
	 * @return A java.util.List of Point objects
	 */
	public List<Point> getPoints() {
		return points;
	}

	/** Method to get the geometry type.
	 * @return &quot;LINESTRING&quot; constant
	 */
	@Override
	public String getType() {
		return "LINESTRING";
	}

	private boolean validate() {

		if(this.points == null || this.points.size() == 0) {
			return false;
		}

		boolean result = true;
		int dims = this.points.get(0).getDimensions();

		for(int i = 0; i < this.points.size(); i++) {

			if(this.points.get(i).getDimensions() != dims) {
				result = false;
				break;
			}

		}

		return result;
	}

}
