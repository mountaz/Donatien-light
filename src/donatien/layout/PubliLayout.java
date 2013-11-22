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
import java.awt.geom.Rectangle2D;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

import donatien.gui.Grid;
import donatien.gui.Grid.Line;
import donatien.model.graph.Node;

public class PubliLayout extends Layout {

	static final String[] GENRES = new String[] {"paper", "person", "keyword", "keyword_t","collection"};
	static final double[] RADII = new double[] {0.55, 0.7, 1,0.9, 0.25};
	
	abstract class Axis {
		abstract double getValue(String s);
		abstract double getMin();
		abstract double getMax();
		abstract void getTicks(ArrayList<Line> ticks);
	}
	
	////////////////////////////////////////
	
	Axis stringSort = new Axis() {
		final char minChar = 65; // A
		final char maxChar = 90; // Z
		double getValue(String s) {
			if (s == null || s.length() == 0) return 0.5;
			return monototicStringHash(s, minChar, maxChar);
		}
		double getMin() {
			return 0;
		}
		double getMax() {
			return 1;
		}
		void getTicks(ArrayList<Line> ticks) {
			for (char c = minChar; c <= maxChar; c++)
				ticks.add(new Line(Character.toString(c), (c - minChar) / (double)(maxChar - minChar), 1)); 
		}
	};
	
	Axis djb2StringHash = new Axis() {
		double getValue(String s) {
			if (s == null || s.length() == 0) return 0.5;
			return djb2hash(s);
		}
		double getMin() {
			return Integer.MIN_VALUE;
		}
		double getMax() {
			return Integer.MAX_VALUE;
		}
		void getTicks(ArrayList<Line> ticks) {
		}
	};
	
	Axis javaStringHash = new Axis() {
		double getValue(String s) {
			if (s == null || s.length() == 0) return 0.5;
			return s.hashCode();
		}
		double getMin() {
			return Integer.MIN_VALUE;
		}
		double getMax() {
			return Integer.MAX_VALUE;
		}
		void getTicks(ArrayList<Line> ticks) {
		}
	};
	
	Axis hyperbolicStringLength = new Axis() {
		final double k = 0.15;
		double getValue(String s) {
			if (s == null || s.length() == 0) return 0.5;
			float l = s.length();
			double jitter = ((double)djb2hash(s) - (double)Integer.MIN_VALUE) / ((double)Integer.MAX_VALUE - (double)Integer.MIN_VALUE) - 0.5;
			jitter *= 0.1; // was between -0.5 and 0.5
			l += jitter;
			return hyperbolicTransform(l, k);
		}
		double getMin() {
			return hyperbolicTransform(1, k);
		}
		double getMax() {
			return 1;
		}
		void getTicks(ArrayList<Line> ticks) {
			int n = 60;
			for (int i = 1; i <= n; i++) {
				double thickness = Math.min(1, 1.5 * (1 - i/(double)n));
				Line l = new Line(i + "", hyperbolicTransform(i, k), thickness);
				ticks.add(l);
			}
		}
	};
	
	Axis xAxis = stringSort;
	Axis yAxis = hyperbolicStringLength;
	
	////////////////////////////////////////
	
	public PubliLayout() {
		super();
	}
	
	@Override
	protected void init() {
	}

	@Override
	protected boolean step() {
		
		// Update node coords
		double maxXRadius = bounds.getWidth() / 2;
		double maxYRadius = bounds.getHeight() / 2;
		Point2D center = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
		for (Node n : graph.getNodes()) {
			String s = n.getProp("name");
			double x = (xAxis.getValue(s) - xAxis.getMin()) / (xAxis.getMax() - xAxis.getMin());
			//double y = (yAxis.getValue(s) - yAxis.getMin()) / (yAxis.getMax() - yAxis.getMin());
			
			double theta = x * 2 * Math.PI;
			double r = getRadius(n);
			n.setX(center.getX() + maxXRadius * r * Math.cos(theta));
			n.setY(center.getY() + maxYRadius * r * Math.sin(theta));
		}
		
		// Finished.
		return true;
	}
	
	protected double getRadius(Node n) {
		String genre = n.getProp("genre");
		if (genre == null)
			return 0;
		for (int i=0; i<GENRES.length; i++) {
			if (genre.equals(GENRES[i]))
				return RADII[i];
		}
		return 0;
	}
	
	@Override
	protected void updateCoordinates() {
	}
	
	protected static void clip(Node n, Rectangle2D clip) {
		if (n.getX() < clip.getX())
			n.setX(clip.getX());
		if (n.getX() > clip.getX() + clip.getWidth())
			n.setX(clip.getX() + clip.getWidth());
		if (n.getY() < clip.getY())
			n.setY(clip.getY());
		if (n.getY() > clip.getY() + clip.getHeight())
			n.setY(clip.getY() + clip.getHeight());
	}
	
	@Override
	public float getLinkOpacity(float previousOpacity, float aboveOpacity) {
		return 1;
	}
	
	@Override
	public Grid getGrid() {
		return null;
	}
	
	
	/**
	 * djb2 String hash algorithm
	 * @param s
	 * @return
	 */
    private static int djb2hash(String s)
    {
        int hash = 5381;
        for (byte c : s.getBytes())
            hash = ((hash << 5) + hash) + c; /* hash * 33 + c */
        return hash;
    }
	
	private static double hyperbolicTransform(double x, double k) {
		return 1 - (1 / (1 + k*x));
	}
	
    private static double monototicStringHash(String s, char min, char max) {
    	s = removeAccents(s).toUpperCase();
    	double x = 0;
    	double scale = 1;
    	for (int i = 0; i < s.length(); i++) {
    		//s = s.substring(i, i+1);
    		char c = s.charAt(i);
    		//System.err.println(Character.toString(c) + " " + (c-xmin));
    		x += (c - min) / (double)(max - min) * scale;
    		scale /= (max - min);
    	}
    	return x;
    }
    
    private static String removeAccents(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }
}
