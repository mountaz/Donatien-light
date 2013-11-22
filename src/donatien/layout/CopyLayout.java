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

import donatien.gui.Grid;
import donatien.model.graph.Graph;
import donatien.model.graph.Node;

/**
 * A layout that copies the layout of another graph into the current graph.
 */
public class CopyLayout extends Layout {

	Graph model;
	Grid grid;
	
	public CopyLayout(Graph model, Grid grid) {
		super();
		this.model = model;
		this.grid = grid;
	}
	
	@Override
	protected void init() {
	}

	@Override
	protected boolean step() {
		// Update nodes' coordinates
		int count = graph.getNodes().size();
		for (int i=0; i<count; i++) {
			Node n = graph.getNodes().get(i);
			Node m = model.getNodes().get(i);
			n.setX(m.getX());
			n.setY(m.getY());
		}
		
		// Finished.
		return true;		
	}
	
	@Override
	protected void updateCoordinates() {
	}
	
	@Override
	public Grid getGrid() {
		return grid;
	}

}
