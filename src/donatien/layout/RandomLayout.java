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

import java.util.Random;

import donatien.model.graph.Node;

public class RandomLayout extends Layout {

	Random rnd = new Random();
	
	public RandomLayout() {
		super();
		//rnd.setSeed(0);
	}
	
	@Override
	protected void init() {
	}

	@Override
	protected boolean step() {
		// Update nodes' coordinates
		for (Node n : graph.getNodes()) {
			n.setX(bounds.getX() + rnd.nextDouble() * bounds.getWidth());
			n.setY(bounds.getY() + rnd.nextDouble() * bounds.getHeight());
		}
		
		// Finished.
		return true;		
	}
	
	@Override
	protected void updateCoordinates() {
	}

}
