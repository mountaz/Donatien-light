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
package donatien.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;

public class FastFontMetrics {

	static class Key {
		String text;
		Font font;
		public Key(String text, Font font) {
			this.text = text;
			this.font = font;
		}
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Key))
				return false;
			Key k = (Key)o;
			return text.equals(k.text) && font.equals(k.font);
		}
	}
	
	public static class Metrics {
		public Rectangle2D bounds;
		public Point2D origin;
		public Metrics(Rectangle2D bounds, Point2D origin) {
			this.bounds = bounds;
			this.origin = origin;
		}
	}
	
	static Hashtable<Key, Metrics> cachedMetrics = new Hashtable<Key, Metrics>();
	static Metrics nullMetrics = new Metrics(new Rectangle2D.Double(0, 0, 0, 0), new Point2D.Double(0, 0));
	
	public static Rectangle2D getBounds(Graphics2D g, String text) {
		return getMetrics(g, text).bounds;
	}
	
	public static Metrics getMetrics(Graphics2D g, String text) {
		if (text == null) return nullMetrics;
		Metrics m = null;
		Key k = new Key(text, g.getFont());
		if (!cachedMetrics.containsKey(k)) {
			FontMetrics fm = g.getFontMetrics();
			Rectangle2D b = fm.getStringBounds(text, g);
			m = new Metrics(b, new Point2D.Double(b.getX(), b.getY() + fm.getAscent() * 0.95));
			cachedMetrics.put(k, m);
		} else {
			m = cachedMetrics.get(k);
		}
		return m;
	}
	
	public static void clearMetricsCache() {
		cachedMetrics.clear();
	}
	
	/**
	 * Alignment: 0 = left/top, 0.5 = middle, 1 = right/bottom.
	 */
	public static void drawCenteredString(Graphics2D g, String str, double x, double y, double alignx, double aligny) {
		if (g.getTransform().getScaleX() * g.getFont().getSize() < 6)
			return;
		Metrics m = getMetrics(g, str);
		double x0 = x - m.bounds.getWidth() * alignx;
		double y0 = y - m.bounds.getHeight() * aligny;
		g.drawString(str,
			(int)Math.round(x0 + (m.origin.getX() - m.bounds.getX())),// ),
			(int)Math.round(y0 + (m.origin.getY() - m.bounds.getY())));// + m.origin.getY() - m.bounds.getY()));			
	}
}
