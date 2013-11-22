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
import donatien.model.graph.Edge;
import donatien.model.graph.Node;

/**
 * A barycentric layout that puts nodes of level n at the barycenter of their neighbours of level n-1.
 *
 */
public class MultilevelBarycentricLayout extends Layout {

	public MultilevelBarycentricLayout() {
		super();
	}
	
	@Override
	protected void init() {
	}

	@Override
	protected boolean step() {
		
		int level = 0;
		Point2D.Double sumOfNeighbourCoordinates = new Point2D.Double();
		int numberOfNeighboursFound = 0;
		boolean foundNodes = false;
		do {
			String previousLevel_str = (level - 1) + "";
			String level_str = level + "";
			foundNodes = false;
			for (Node n : graph.getNodes()) {
				if (level_str.equals(n.getProp("level"))) {
					sumOfNeighbourCoordinates.setLocation(0, 0);
					numberOfNeighboursFound = 0;
					for (Edge edge : n.getNeighbour()) {
						Node neighbour = edge.getOtherNode(n);
						if (previousLevel_str.equals(neighbour.getProp("level"))) {
							sumOfNeighbourCoordinates.x += neighbour.getX();
							sumOfNeighbourCoordinates.y += neighbour.getY();
							numberOfNeighboursFound++;
						}
					}
					if (numberOfNeighboursFound != 0) {
						n.setX(sumOfNeighbourCoordinates.x / numberOfNeighboursFound);
						n.setY(sumOfNeighbourCoordinates.y / numberOfNeighboursFound);
					}
					foundNodes = true;
				}
			}
			level++;
		} while (foundNodes);
		
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
}
