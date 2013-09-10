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

/** The class represents a MULTILINESTRING object. */
public class MultiLineString extends AbstractGeometry {

	private List<LineString> lineStrings;

	/** Constructor.
	 * @param lineStrings A java.util.List of LineString objects
	 * @throws A generic exception if dimensions are not homogeneous
	 */
	public MultiLineString(List<LineString> lineStrings) throws Exception {
		this.lineStrings = lineStrings;
		
		if(!this.validate()) {
			throw new Exception("Invalid multilinestring");
		}
		
		this.dimensions = this.lineStrings.get(0).getDimensions();
	}

	/** Method to get the list of line-strings.
	 * @return A java.util.List of LineString objects
	 */
	public List<LineString> getLineStrings() {
		return lineStrings;
	}

	/** Method to get the geometry type.
	 * @return &quot;MULTILINESTRING&quot; constant
	 */
	@Override
	public String getType() {
		return "MULTILINESTRING";
	}

	private boolean validate() {

		if(this.lineStrings == null || this.lineStrings.size() == 0) {
			return false;
		}

		boolean result = true;
		int dims = this.lineStrings.get(0).getDimensions();

		for(int i = 0; i < this.lineStrings.size(); i++) {

			if(this.lineStrings.get(i).getDimensions() != dims) {
				result = false;
				break;
			}

		}

		return result;
	}

}
