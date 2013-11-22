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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import donatien.model.graph.RenderingAttributes;
import donatien.util.GUIUtils;


public class EllipticGrid  extends Grid{
	ArrayList<Line2D.Double> radius = new ArrayList<Line2D.Double>();
	ArrayList<Ellipse2D.Double> cubitus = new ArrayList<Ellipse2D.Double>(); 
	Rectangle2D.Double james;
	
	public EllipticGrid(Rectangle2D.Double bounds){
		james = bounds; 
	}
	
	public EllipticGrid(Rectangle2D bounds){
		james = new Rectangle2D.Double((double) bounds.getX(),(double) bounds.getY(),(double) bounds.getWidth(),(double) bounds.getHeight()); 
	}
	
	public Rectangle2D.Double getBounds(){
		return james;
	}
	
	public void setBounds(Rectangle2D bounds){
		james = new Rectangle2D.Double((double) bounds.getX(),(double) bounds.getY(),(double) bounds.getWidth(),(double) bounds.getHeight()); 
	}

	public void addCubitus(double ra,double rb){
		Ellipse2D.Double e = new Ellipse2D.Double(getBounds().getCenterX()-ra,getBounds().getCenterY()-rb,2*ra,2*rb);
		cubitus.add(e);
	}
	
	public void addRadius(double a, double b,double teta){
		double x = getBounds().getCenterX();
		double y = getBounds().getCenterY();
		Line2D.Double l = new Line2D.Double(x,y,x+a*Math.cos(teta),y+b*Math.sin(teta));
		radius.add(l);
	}
	
	public void paint(Graphics2D g, RenderingAttributes att) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(new Color(0, 0, 0, 0.4f));
		g.setStroke(new BasicStroke(0.25f));
		setFontAttributes(g, att);

		Point2D.Double p = new Point2D.Double(this.getBounds().getCenterX(), this.getBounds().getCenterY());

		for (Line2D.Double l : radius){
			g.draw(l);
		}
		for (Ellipse2D.Double e : cubitus){
			g.draw(e);
		}
	}
	
	protected void setStrokeAttributes(Graphics2D g, RenderingAttributes att, double thickness) {
		Color color = att.gridColor;
		if (thickness == 1) {
			g.setStroke(att.gridStroke);
			g.setColor(color);
		} else {
			float w = ((BasicStroke)att.gridStroke).getLineWidth() * (float)thickness;
			g.setStroke(new BasicStroke(w));
			g.setColor(GUIUtils.multiplyAlpha(color, (float)thickness));
		}
	}
	public void clear() {
		radius.clear();
		cubitus.clear();
	}

	protected void setFontAttributes(Graphics2D g, RenderingAttributes att) {
		final int minFontSize = 8;
		double size = g.getTransform().getScaleX() * g.getFont().getSize();
		if (size < minFontSize) {
			g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), (int)(minFontSize / g.getTransform().getScaleX())));
		}
	}

	@Override
	public void expand(Point2D origin, double scale_x, double scale_y) {
		for (Line2D.Double l : radius){
			l.x1 = (l.x1 - origin.getX())*scale_x + origin.getX();
			l.y1 = (l.y1 - origin.getY())*scale_y + origin.getY();
			l.x2 = (l.x2 - origin.getX())*scale_x + origin.getX();
			l.y2 = (l.y2 - origin.getY())*scale_y + origin.getY();
		}
		for (Ellipse2D.Double e : cubitus){
			e.x = (e.x - origin.getX())*scale_x + origin.getX();
			e.y = (e.y - origin.getY())*scale_y + origin.getY();
			e.width = e.width*scale_x ;
			e.height = e.height*scale_y ;
		}
	}
	
}
