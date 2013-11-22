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
package donatien.model.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.util.Hashtable;

import donatien.util.GUIUtils;

public class RenderingAttributes {
	
	static Hashtable<String, RenderingAttributes> nodeStyles = new Hashtable<String, RenderingAttributes>();
	static {
	}
	
	// Nodes
	public Stroke nodeStroke = new BasicStroke(0.75f);
	public Color nodeFillColor = new Color(0.9f, 0.9f, 0.95f);
	public Color nodeBorderColor = new Color(0.2f, 0.2f, 0.6f);
	public static Font labelFont = new Font("sansserif", 0, 12);
	public static Font labelFontNarrow = new Font("Arial narrow", 0, 12);
	public Color labelColor = new Color(0,0,0);
	Color shadowColor = new Color(0, 0, 0, 0.2f);
	boolean xrayMode = false;
	
	// Edges
	public Color edgeColor = new Color(0.2f, 0.2f, 0.6f);
	public Stroke edgeStroke = new BasicStroke(0.5f);
	double taperedEdgeWidth = 3.5;//3.0;
	Stroke selfEdgeStroke = new BasicStroke(2.5f); // for tapered mode
	double selfEdgeRadius = 40.0;

	// Grid
	public Color gridColor = new Color(0.5f, 0.5f, 0.5f);
	public Stroke gridStroke = new BasicStroke(0.3f);
	public Font gridFont = new Font("sansserif", 0, 12);

	// All
	public boolean highQuality = true;
	public double scaleFactor = 1; // Used for semantic zooming
	
	public RenderingAttributes(Color baseColor, float opacity, float linkOpacity, boolean highQuality, boolean showNodeCenters, double scaleFactor) {
		this.highQuality = highQuality;
		this.xrayMode = showNodeCenters;
		this.scaleFactor = scaleFactor;
		nodeFillColor = GUIUtils.mix(baseColor, Color.white, 0.95f);
		nodeBorderColor = GUIUtils.mix(baseColor, Color.black, 0.3f);
		labelColor = GUIUtils.mix(baseColor, Color.black, 0.5f);
		edgeColor = GUIUtils.multiplyAlpha(GUIUtils.mix(baseColor, Color.black, 0.3f), linkOpacity);
		
		if (!highQuality) {
			float brighten = 1 - (float)Math.pow(opacity, 0.5);
			edgeColor = GUIUtils.mix(edgeColor, Color.white, brighten);
			nodeBorderColor = GUIUtils.mix(nodeBorderColor, Color.white, brighten);
			labelColor = GUIUtils.mix(labelColor, Color.white, brighten);
			if (linkOpacity > 0) {
				brighten = 1 - (float)Math.pow(opacity * linkOpacity, 0.5);
				edgeColor = GUIUtils.mix(edgeColor, Color.white, brighten);
			} else
				edgeColor = new Color(0, 0, 0, 0);
		}
	}
	
	public static Color makeOpaque(Color c, Color background) {
		float opacity = c.getAlpha() / 255f;
		if (opacity == 1)
			return c;
		float brighten = (float)Math.pow(opacity, 0.5);
		c = new Color(c.getRed(), c.getGreen(), c.getBlue());
		c = GUIUtils.mix(c, background, 1-brighten);
		return c;
	}
}
