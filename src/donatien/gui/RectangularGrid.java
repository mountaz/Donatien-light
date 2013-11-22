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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import donatien.model.graph.RenderingAttributes;
import donatien.util.FastFontMetrics;
import donatien.util.GUIUtils;

public class RectangularGrid extends Grid {

	static final int HORIZONTAL = 0;
	static final int VERTICAL = 1;

	Rectangle2D bounds;
	Line2D.Double tmpLine = new Line2D.Double();

	public RectangularGrid() {
		super();
	}

	public void setBounds(Rectangle2D bounds) {
		this.bounds = bounds;
	}

	public Rectangle2D getBounds() {
		return bounds;
	}

	public void addHorizontalLine(String label, double y, double thickness) {
		addLine(HORIZONTAL, label, y, thickness);
	}

	public void addVerticalLine(String label, double x, double thickness) {
		addLine(VERTICAL, label, x, thickness);
	}

	public void expand(Point2D origin, double scale_x, double scale_y) {
		linearTransform(HORIZONTAL, scale_y, origin.getY() * (1 - scale_y));
		linearTransform(VERTICAL, scale_x, origin.getX() * (1 - scale_x));
		bounds.setRect(
				(bounds.getX() - origin.getX()) * scale_x + origin.getX(),
				(bounds.getY() - origin.getY()) * scale_y + origin.getY(),
				bounds.getWidth() * scale_x,
				bounds.getHeight() * scale_y);
	}

	public void paint(Graphics2D g, RenderingAttributes att) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		setFontAttributes(g, att);

		if (getLines(HORIZONTAL) != null) {
			double min_ty = Double.NEGATIVE_INFINITY;
			ArrayList<Line> lines = new ArrayList<Line>(getLines(HORIZONTAL));
			for (Line y : lines) {
				setStrokeAttributes(g, att, y.thickness);
				// Draw line
				tmpLine.setLine(bounds.getX(), y.pos, bounds.getMaxX(), y.pos);
				g.draw(tmpLine);
				// Draw label
				if (y.label != null) {
					FastFontMetrics.Metrics m = FastFontMetrics.getMetrics(g, y.label);
					double tx = bounds.getX();
					double ty = y.pos;
					if (ty > min_ty) {
						FastFontMetrics.drawCenteredString(g, y.label, tx - 4, ty, 1, 0.5);
						min_ty = ty + m.bounds.getHeight();
						// Draw tick mark
						tmpLine.setLine(bounds.getX(), y.pos, bounds.getX() - 3, y.pos);
						g.draw(tmpLine);
					}
				}
			}
		}

		if (getLines(VERTICAL) != null) {
			double min_tx = Double.NEGATIVE_INFINITY;
			ArrayList<Line> lines = new ArrayList<Line>(getLines(VERTICAL));
			for (Line x : lines) {
				setStrokeAttributes(g, att, x.thickness);
				tmpLine.setLine(x.pos, bounds.getY(), x.pos, bounds.getMaxY());
				g.draw(tmpLine);
				// Draw label
				if (x.label != null) {
					FastFontMetrics.Metrics m = FastFontMetrics.getMetrics(g, x.label);
					double tx = x.pos;
					double ty = bounds.getY();
					if (tx > min_tx) {
						FastFontMetrics.drawCenteredString(g, x.label, tx, ty - 4, 0.5, 1);
						min_tx = tx + m.bounds.getWidth() + 3;
						// Draw tick mark
						tmpLine.setLine(x.pos, bounds.getY(), x.pos, bounds.getY() - 3);
						g.draw(tmpLine);
					}
				}
			}
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

	protected void setFontAttributes(Graphics2D g, RenderingAttributes att) {
		final int minFontSize = 8;
		double size = g.getTransform().getScaleX() * g.getFont().getSize();
		if (size < minFontSize) {
			g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), (int)(minFontSize / g.getTransform().getScaleX())));
		}
	}
	
}
