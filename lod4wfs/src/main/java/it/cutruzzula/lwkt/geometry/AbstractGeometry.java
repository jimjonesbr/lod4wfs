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

/** The abstract class to derive WKT objects */
public abstract class AbstractGeometry {
	
	protected int dimensions;
	
	/** Method to get the number of SRS dimensions.
	 * @return The number of SRS dimensions
	 */
	final public int getDimensions() {
		return this.dimensions;
	}
	
	/** Method to get the geometry type.
	 * @return The string constant of the type, for example &quot;POINT&quot; or &quot;POLYGON&quot;
	 */
	public abstract String getType();

}
