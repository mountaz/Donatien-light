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
import java.awt.event.ActionListener;
import java.util.ArrayList;

import donatien.util.GUIUtils;

public class CheckBox {

	public enum InnerShape {
		BigSquare, SmallSquare, Cross, Plus
	};
	
	static final int size = View.layerBarHeight - 8;
	Rectangle bounds = new Rectangle(0, 0, size, size);
	boolean on;
	boolean finished = false; // for asynchronous commands such as layouts
	boolean enabled = true;
	boolean allowVerticalCross = true;
	boolean allowHorizontalCross = true;
	Color baseColor = Color.black;
	ActionListener listener;
	ArrayList<CheckBox> radiogroup = new ArrayList<CheckBox>();
	String name;
	InnerShape innerShape;
	boolean previewTitle = false;
	Font previewFont = new Font("Helvetica", 0, 9);
	
	public CheckBox(String name, InnerShape innerShape, boolean previewTitle, boolean enabled, boolean on, boolean allowHorizontalCross, boolean allowVerticalCross, ActionListener a) {
		this.name = name;
		this.innerShape = innerShape;
		this.previewTitle = previewTitle;
		this.enabled = enabled;
		this.on = on;
		this.allowHorizontalCross = allowHorizontalCross;
		this.allowVerticalCross = allowVerticalCross;
		this.listener = a;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	
	public void paint(Graphics2D g) {
		Color border_c = GUIUtils.mix(baseColor, Color.black, 0.3f);
		Color inside_c = GUIUtils.mix(baseColor, Color.black, 0.3f);
		if (!enabled) {
			border_c = GUIUtils.multiplyAlpha(border_c, 0.2f);
			inside_c = GUIUtils.multiplyAlpha(inside_c, 0.2f);
		} else if (finished) {
			inside_c = GUIUtils.multiplyAlpha(inside_c, 0.3f);
		}
		g.setColor(border_c);
		g.draw(bounds);
		if (on) {
			g.setColor(inside_c);
			if (innerShape == InnerShape.BigSquare) {
				final int m = 2;
				g.fillRect(bounds.x + m, bounds.y + m, bounds.width - m*2 + 1, bounds.height - m*2 + 1);
			} else if (innerShape == InnerShape.SmallSquare) {
				final int m = 3;
				g.fillRect(bounds.x + m, bounds.y + m, bounds.width - m*2 + 1, bounds.height - m*2 + 1);
			} else if (innerShape == InnerShape.Cross) {
				g.setColor(border_c);
				final int m = 2;
				g.setStroke(new BasicStroke(1));
				g.drawLine(bounds.x + m, bounds.y + m, bounds.x + bounds.width - m, bounds.y + bounds.height - m);
				g.drawLine(bounds.x + m, bounds.y + bounds.height - m, bounds.x + bounds.width - m, bounds.y + m);
			} else if (innerShape == InnerShape.Plus) {
				g.setColor(border_c);
				final int m = 2;
				g.setStroke(new BasicStroke(1));
				g.drawLine(bounds.x + bounds.width / 2, bounds.y + m, bounds.x + bounds.width / 2, bounds.y + bounds.height - m);
				g.drawLine(bounds.x + m, bounds.y + bounds.height / 2, bounds.x + bounds.width - m, bounds.y + bounds.height / 2);
			}
		} else if (previewTitle) {
			g.setColor(GUIUtils.multiplyAlpha(border_c, 0.4f));
			g.setFont(previewFont);
			g.drawString(name.substring(0, 1), bounds.x + 2, bounds.y + 9);
		}
	}
	
	public boolean isOn() {
		return on;
	}

	/**
	 * Changes the state without calling action listeners 
	 * 
	 * @param on
	 */
	public void setOn(boolean on) {
		this.on = on;		
		if (on && radiogroup.size() > 0) {
			for (CheckBox b : radiogroup) {
				if (b != this)
					b.setOn(false);
			}
		}
	}
	
	public void press() {
		setOn(!(on && !finished));
		listener.actionPerformed(null);
	}
	
	public void cross() {
		setOn(!(on && !finished));
		listener.actionPerformed(null);
	}
	
	public void setRadioGroup(ArrayList<CheckBox> group) {
		this.radiogroup = group;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Color getBaseColor() {
		return baseColor;
	}

	public void setBaseColor(Color color) {
		this.baseColor = color;
	}

	public boolean isAllowVerticalCross() {
		return allowVerticalCross;
	}

	public void setAllowVerticalCross(boolean allowVerticalCross) {
		this.allowVerticalCross = allowVerticalCross;
	}

	public boolean isAllowHorizontalCross() {
		return allowHorizontalCross;
	}

	public void setAllowHorizontalCross(boolean allowHorizontalCross) {
		this.allowHorizontalCross = allowHorizontalCross;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
