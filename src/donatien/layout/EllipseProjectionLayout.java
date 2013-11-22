/* This software is a light version of Donatien, a program created 
 * for the comparison and matching of graphs and clustered graphs
 * Copyright (C)2010 Pierre Dragicevic and Mountaz Hascoët
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see<http://www.gnu.org/licenses/>.
 */
package donatien.layout;

import java.awt.geom.Point2D;

import donatien.gui.Grid;
import donatien.model.graph.Node;

/**
 * A barycentric layout that puts nodes of level n+1 at the barycenter of their neighbours of level n.
 *
 */
public class EllipseProjectionLayout extends Layout {

	//final static double maxRadius = 1.0; // relative radius. A value of 1.0 takes the whole bounds
	final static String[] ignoreList = new String[] { 
//		"cluster", // property
//		"n"        // value
	};
	
	public EllipseProjectionLayout() {
		super();
	}
	
	@Override
	protected void init() {
	}

	@Override
	protected boolean step() {

		// Update node coords
		Point2D center = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
		Point2D.Double intersection1 = new Point2D.Double();
		Point2D.Double intersection2 = new Point2D.Double();
		for (Node n : graph.getNodes()) {
			if (isIgnored(n))
				continue;
			Point2D.Double nrelative = new Point2D.Double(n.getX() - center.getX(), n.getY() - center.getY());
			
			// Compute the radius according to level
			String lev = n.getProp("level");
			int level = (lev == null) ? 0 : Integer.parseInt(lev);
			double relativeRadius = 1.0 / (level + 1);
			double XRadius = bounds.getWidth() / 2 * relativeRadius;
			double YRadius = bounds.getHeight() / 2 * relativeRadius;
			
			// Compute the two intersections between an ellipse and a line going through its origin
			// (see http://mathworld.wolfram.com/Ellipse-LineIntersection.html)
			double alpha = XRadius * YRadius / Math.sqrt(XRadius * XRadius * nrelative.y * nrelative.y + YRadius * YRadius * nrelative.x * nrelative.x);
			intersection1.x = alpha * nrelative.x;
			intersection1.y = alpha * nrelative.y;
			double dist1 = nrelative.distance(intersection1);
			intersection2.x = - alpha * nrelative.x;
			intersection2.y = - alpha * nrelative.y;
			double dist2 = nrelative.distance(intersection2);
			// Take the closest intersection
			Point2D.Double projection = dist1 < dist2 ? intersection1 : intersection2;
			n.setX(center.getX() + projection.x);
			n.setY(center.getY() + projection.y);
		}
		
		// Finished.
		return true;
	}
	
	@Override
	protected void updateCoordinates() {
	}

	@Override
	public float getLinkOpacity(float previousOpacity, float aboveOpacity) {
		return previousOpacity;
	}
	
	@Override
	public Grid getGrid() {
		return null;
	}
	
	protected static boolean isIgnored(Node n) {
		for (int i=0; i<ignoreList.length; i+=2) {
			String value = n.getProp(ignoreList[i]);
			String value2 = ignoreList[i+1];
			if ((value == null && value2 == null) || (value != null && value.equals(value2)))
				return true;
		}
		if (!n.isVisible())
			return true;
		return false;
	}
	
	private static double hyperbolicTransform(double x, double k) {
		return 1 - (1 / (1 + k*x));
	}
}
