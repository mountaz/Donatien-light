/* This software is a part of Aviz API 
 * Copyright (C)2008 Pierre Dragicevic 
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
package fr.aviz.animation;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class AnimatedBounds {

	public AnimatedFloat x = new AnimatedFloat(0);
	public AnimatedFloat y = new AnimatedFloat(0);
	public AnimatedFloat width = new AnimatedFloat(0);
	public AnimatedFloat height = new AnimatedFloat(0);
	
	public AnimatedBounds() {
	}
	
	public Rectangle2D get() {
		return new Rectangle2D.Double(x.get(), y.get(), width.get(), height.get());
	}
	
	public Rectangle getInt() {
		return new Rectangle((int)Math.round(x.get()), (int)Math.round(y.get()), (int)Math.round(width.get()), (int)Math.round(height.get()));
	}
	
	public void set(Rectangle2D rec) {
		x.set(rec.getX());
		y.set(rec.getY());
		width.set(rec.getWidth());
		height.set(rec.getHeight());
	}
	
	public void directSet(Rectangle2D rec) {
		x.directSet(rec.getX());
		y.directSet(rec.getY());
		width.directSet(rec.getWidth());
		height.directSet(rec.getHeight());
	}
	
	public void directSet(double x2, double y2, double width2, double height2) {
		x.directSet(x2);
		y.directSet(y2);
		width.directSet(width2);
		height.directSet(height2);
	}
	
	public boolean changed() {
		return x.changed() || y.changed() || width.changed() || height.changed();
	}
}
