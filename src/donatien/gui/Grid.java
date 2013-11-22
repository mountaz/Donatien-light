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
package donatien.gui;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Hashtable;

import donatien.model.graph.RenderingAttributes;

public abstract class Grid {

	public static class Line {
		public String label;
		public double pos;
		public double thickness;
		public Line(String label, double pos, double thickness) {
			this.label = label;
			this.pos = pos;
			this.thickness = thickness;
		}
	}
	
	Hashtable<Integer, ArrayList<Line>> lines = new Hashtable<Integer, ArrayList<Line>>();
	
	public Grid() {
		
	}
	
	protected void addLine(int dimension, String label, double pos, double thickness) {
		ArrayList<Line> lines_;
		if (!lines.containsKey(dimension)) {
			lines_ = new ArrayList<Line>();
			lines.put(dimension, lines_);
		} else
			lines_ = lines.get(dimension);
		lines_.add(new Line(label, pos, thickness));
	}
	
	/**
	 * f(x) = ax + b
	 */
	protected void linearTransform(int dimension, double a, double b) {
		ArrayList<Line> lines_ = lines.get(dimension);
		if (lines_ == null)
			return;
		for (Line line : lines_) {
			line.pos = line.pos * a + b;
		}
	}
	
	protected ArrayList<Line> getLines(int dimension) {
		if (!lines.containsKey(dimension))
			return new ArrayList<Line>();
		else
			return lines.get(dimension);
	}
	
	public void clear() {
		lines.clear();
	}

	public abstract void expand(Point2D origin, double scale_x, double scale_y);

	public abstract void paint(Graphics2D g, RenderingAttributes att);
	
}
