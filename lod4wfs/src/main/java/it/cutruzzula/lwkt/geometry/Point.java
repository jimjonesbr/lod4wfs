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

/** The class represents a POINT object. */
public class Point extends AbstractGeometry {

	private double x;
	private double y;
	private double z;

	/** Constructor for 2D point.
	 * @param x The x coordinate
	 * @param y The y coordinate
	 */
	public Point(double x, double y) {
		this.dimensions = 2;
		this.x = x;
		this.y = y;
		this.z = 0.0d;
	}

	/** Constructor for 3D point.
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param z The z dimension
	 */
	public Point(double x, double y, double z) {
		this.dimensions = 3;
		this.x = x;
		this.y = y;
		this.z = z;
	}


	/** Method to get the x coordinate.
	 * @return The x coordinate
	 */
	public double getX() {
		return x;
	}

	/** Method to get the y coordinate.
	 * @return The y coordinate
	 */
	public double getY() {
		return y;
	}

	/** Method to get the z dimension.
	 * @return The z dimension
	 */
	public double getZ() {
		return z;
	}

	
	public boolean equals(Object o) {

		if(o == null || !(o instanceof Point)) {
			return false;
		}

		Point p = (Point)o;
		return p.getX() == this.x && p.getY() == this.y && p.getDimensions() == this.dimensions && 
		(p.getDimensions() == 2 || (p.getDimensions() == 3 && p.getZ() == this.z));
	}

	/** Method to get the geometry type.
	 * @return &quot;POINT&quot; constant
	 */
	@Override
	public String getType() {
		return "POINT";
	}

}
