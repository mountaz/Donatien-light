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
import java.awt.Rectangle;
import java.awt.Stroke;

import donatien.util.GUIUtils;

public class GraphIcon {

	String title = new String();
	String filename = new String();
	Rectangle bounds; // bounds as seen by the parent JGraphList
	
	static Stroke borderStroke = new BasicStroke(1f);
	static Font font = new Font("sansserif", Font.BOLD, 12);
	Color baseColor = new Color(0.5f, 0.5f, 0.5f);
	boolean translucent;
	
	public GraphIcon() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public void paint(Graphics2D g) {
		g.setStroke(borderStroke);
		if (translucent)
			g.setColor(GUIUtils.multiplyAlpha(GUIUtils.mix(baseColor, Color.white, 0.9f), 0.6f));
		else
			g.setColor(GUIUtils.mix(baseColor, Color.white, 0.95f));
		g.fillRect(0, 0, bounds.width, bounds.height);
		g.setColor(JViews.borderColor);
		g.drawRect(0, 0, bounds.width, bounds.height);
		g.setColor(GUIUtils.mix(baseColor, Color.black, 0.4f));
		g.setFont(font);
		g.drawString(title, 5, bounds.height - 4);
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	
	public void move(int deltax, int deltay) {
		bounds.setBounds(bounds.x + deltax, bounds.y + deltay, bounds.width, bounds.height);
	}
}
