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
import java.util.Random;

import donatien.model.graph.Node;

/**
 * A dumb random layout.
 *
 */
public class HoleLayout extends Layout {

	Random rnd = new Random();
	Point2D center = new Point2D.Double();
	double repulsion = 0.1;
	
	public HoleLayout() {
		super();
		//rnd.setSeed(0);
	}
	
	@Override
	protected void init() {
		center.setLocation(getBounds().getCenterX(), getBounds().getCenterY());
	}

	@Override
	protected boolean step() {
		// Update nodes' coordinates
		for (Node n : graph.getNodes()) {
			double tx = (n.getX() - center.getX()) / (getBounds().getWidth() / 2);
			double ty = (n.getY() - center.getY()) / (getBounds().getHeight() / 2);

			int signx = tx > 0 ? 1 : -1;
			double amountx = 1 - Math.abs(ty);
			tx = signx * Math.pow(Math.abs(tx), 1 - repulsion * amountx);
			n.setX(tx * (getBounds().getWidth() / 2) + center.getX());
			//
			int signy = ty > 0 ? 1 : -1;
			double amounty = 1 - Math.abs(tx);
			ty = signy * Math.pow(Math.abs(ty), 1 - repulsion * amounty);
			n.setY(ty * (getBounds().getHeight() / 2) + center.getY());
		}
		
		try {
			Thread.sleep(50);
		} catch (Exception e) {
			
		}
		
		// Finished.
		return false;		
	}
	
	@Override
	protected void updateCoordinates() {
	}
	
	@Override
	public float getLinkOpacity(float previousOpacity, float aboveOpacity) {
		return previousOpacity;
	}
}
