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

import java.util.ArrayList;
import java.util.List;

/** The class represents a POLYGON object. */
public class Polygon extends AbstractGeometry {

	private List<MultiPoint> multiPoints;

	/** Constructor.
	 * @param multiPoints A java.util.List of MultiPoint objects
	 * @throws A generic exception if dimensions are not homogeneous or rings are not closed
	 */
	public Polygon(List<MultiPoint> multiPoints) throws Exception {
		this.multiPoints = multiPoints;

		if(!this.validate()) {
			throw new Exception("Invalid polygon");
		}

		this.dimensions = this.multiPoints.get(0).getDimensions();
	}

	/** Method to get the list of multi-points.
	 * @return A java.util.List of MultiPoint objects
	 */
	public List<MultiPoint> getMultiPoints() {
		return multiPoints;
	}
	
	/** Method to get the external multi-point.
	 * @return A MultiPoint object
	 */
	public MultiPoint getExterior() {
		return this.multiPoints.get(0);
	}
	
	/** Method to get the list of internal multi-points.
	 * @return A java.util.List of MultiPoint objects
	 */
	public List<MultiPoint> getInteriors() {
		List<MultiPoint> results = new ArrayList<MultiPoint>();
		
		for(int i = 1; i < this.multiPoints.size(); i++) {
			results.add(this.multiPoints.get(i));
		}
		
		return results;
	}

	/** Method to get the geometry type.
	 * @return &quot;POLYGON&quot; constant
	 */
	@Override
	public String getType() {
		return "POLYGON";
	}

	private boolean validate() {

		if(this.multiPoints == null || this.multiPoints.size() == 0) {
			return false;
		}

		boolean result = true;
		int dims = this.multiPoints.get(0).getDimensions();
		Point first, last;

		for(int i = 0; i < this.multiPoints.size(); i++) {

			if(this.multiPoints.get(i).getDimensions() != dims) {
				result = false;
				break;
			}

			if(this.multiPoints.get(i).getPoints().size() < 4) {
				result = false;
				break;
			}

			first = this.multiPoints.get(i).getPoints().get(0);
			last = this.multiPoints.get(i).getPoints().get(this.multiPoints.get(i).getPoints().size() - 1);
		
			if(!first.equals(last)) {
				result = false;
				break;
			}
			
		}

		return result;
	}

}
