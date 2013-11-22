/* Datastructures for graph representation adapted from work by Guillaume Artignan
 * Copyright (C)2010 Guillaume Artignan, Pierre Dragicevic and Mountaz Hascoët
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import donatien.util.GUIUtils;


/**
 * An Edge is an element with properties, linking two nodes.
 * @author artignan
 *
 */
public class Edge extends PropertyModifiable
{
	/**Identifier of an edge*/
	private String id ;
	/**Node source of the edge*/
	private Node src;
	/**Node target of the edge*/
	private Node trg;
	
	/**
	 * Construct an Edge with a node source and a node target
	 * 
	 * @param id the identifier of our edge constructed
	 * @param src the node source of our edge.
	 * @param trg the node target of our edge.
	 */
	public Edge(String id,Node src,Node trg)
	{
		this.id = id;
		this.src = src;
		this.trg = trg;
	}
	
	/**
	 * Getter returning the node source of our edge
	 * 
	 * @return the node source
	 */
	public Node getSource()
	{
		return src;
	}
	
	/**
	 * Getter returning the node source of our edge 
	 * @return the node source
	 */
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Edge))
			return false;
		Edge e = (Edge) o;
		if (this.getID().equals(e.getID())) {
			return true;
		} 
		return false;
	}
	
	/**
	 * Return the node target of our edge
	 * 
	 * @return the node target
	 */
	public Node getTarget()
	{
		return trg;
	}
	
	
	
	/**
	 * Getter on the identifier of our edge.
	 * 
	 * @return the identifier of our edge
	 */
	public String getID()
	{ 
		if (id == null)
			return src.getId() + " -> " + trg.getId(); 
		return id;
	}
	
	/**
	 * Return a representative string of our edge
	 * 
	 * @return The representative string of our edge.
	 */
	public String toString()
	{
		return id;
	}
	
	/**
	 * Give the other extremity than n. If the source = n then return the target of this edge
	 * Else if the target = n return the source, Else return null.
	 * @param n an extremity of our edge
	 * @return the other extremity
	 */
	public Node getOtherNode(Node n)
	{
		if (this.src==n) return this.trg;
		else if (this.trg==n) return this.src;
		else return null;
	}
	
	///////////////////////////////////////////////////////////////////////////////
	// Painting code
	///////////////////////////////////////////////////////////////////////////////
	
	Line2D.Double lineShape = new Line2D.Double();
	Path2D.Double taperedShape = null;
	Ellipse2D.Double selfShape = null;
	boolean directed = true;
	boolean visible = true;
	
	// Temporary fix for the scenario
	boolean invertEdges = true;

	public void paint(Graphics2D g, RenderingAttributes att) {
		paint(g, src.getX(), src.getY(), trg.getX(), trg.getY(), att);
	}
	
	public void paint(Graphics2D g, double nx1, double ny1, double nx2, double ny2, RenderingAttributes att) {
		
		if (!visible)
			return;
//		if (getTarget() == getSource()){
//			return;
//		}
		double taperedEdgeWidth = att.taperedEdgeWidth;
		Color edgeColor = att.edgeColor;
		
		if (invertEdges) {
			double xx = nx1;
			double yy = ny1;
			nx1 = nx2;
			ny1 = ny2;
			nx2 = xx;
			ny2 = yy;
		}

		// -- Containment edges
		if ("y".equals(getProp("containment"))) {
			taperedEdgeWidth *= 10;
			edgeColor = GUIUtils.mix(edgeColor, Color.white, 0.75f);
		}
		
		g.setColor(edgeColor);
		
		if (src == trg) {
			
			// SELF

			if (selfShape == null)
				selfShape = new Ellipse2D.Double();
			selfShape.setFrame(nx1 - att.selfEdgeRadius*0.5, ny1 - att.selfEdgeRadius*0.8, att.selfEdgeRadius, att.selfEdgeRadius);
			if (directed)
				g.setStroke(att.selfEdgeStroke);
			else
				g.setStroke(att.edgeStroke);
			g.draw(selfShape);
			return;
		}
			
		
		if (nx1 == nx2 && ny1 == ny2)
			return;
		
		if (!directed) {
			
			// LINE
			
			lineShape.setLine(nx1, ny1, nx2, ny2);
			g.draw(lineShape);
		} else {
			
			// TAPERED

			if (taperedShape == null) {
				taperedShape = new Path2D.Double(Path2D.WIND_NON_ZERO, 4);
			}
			if (lineShape.getX1() != nx1 || lineShape.getY1() != ny1 || lineShape.getX2() != nx2 || lineShape.getY2() != ny2) {
				lineShape.setLine(nx1, ny1, nx2, ny2);
				taperedShape.reset();
				double x0 = nx1;
				double y0 = ny1;
				double x1 = nx2;
				double y1 = ny2;
				double d = Math.sqrt((x1-x0)*(x1-x0) + (y1-y0)*(y1-y0));
				double dx = (y0 - y1) / d * taperedEdgeWidth / 2;
				double dy = (x1 - x0) / d * taperedEdgeWidth / 2;
				taperedShape.moveTo(x0 - dx, y0 - dy);
				taperedShape.lineTo(x0 + dx, y0 + dy);
				taperedShape.lineTo(x1, y1);
				taperedShape.closePath();
			}
			g.fill(taperedShape);
		}
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
