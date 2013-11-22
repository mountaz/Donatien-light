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
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import donatien.gui.CheckBox.InnerShape;
import donatien.layout.Layout;
import donatien.layout.MasterAlignLayout;
import donatien.util.GUIUtils;

public class LayerBar {

	String title = new String();
	String filename = new String(); // for graph file selector
	Rectangle bounds; // bounds as seen by the parent View (not Layer!)
	Layer parent;
	JViews views;
	String currentLayoutName = "";
	
	static Stroke borderStroke = new BasicStroke(1f);
	static Font title_font = new Font("sansserif", Font.BOLD, 12);
	static Font layout_font = new Font("sansserif", 0, 12);
	Color baseColor = Color.black;
	int title_right = 250;
	int layoutBars_right = 0;
	
	// widgets
	ArrayList<CheckBox> widgets = new ArrayList<CheckBox>();
	ArrayList<CheckBox> layoutWidgets = new ArrayList<CheckBox>();

	final CheckBox check_solo = new CheckBox("Solo", InnerShape.BigSquare, false, true, false, false, true, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			parent.parent.updateVisibilities();
		}
	});
	final CheckBox check_visible = new CheckBox("Visible", InnerShape.SmallSquare, false, true, true, false, true, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			parent.parent.updateVisibilities();
		}
	});
	final CheckBox check_close = new CheckBox("Close", InnerShape.Cross, false, true, true, false, true, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			parent.parent.removeLayer(parent);
		}
	});
	
	public LayerBar(Layer parent) {
		super();
		this.parent = parent;
		addWidgets();
	}
	
	public void addWidgets() {
		widgets.add(check_solo);
		widgets.add(check_visible);
		widgets.add(check_close);

		for (String layoutName: AllLayouts.allLayoutNames) {
			final String name = layoutName;
			final Layout layout = AllLayouts.allLayouts.get(layoutName);
			final CheckBox widget = new CheckBox(name, InnerShape.BigSquare, true, true, name == AllLayouts.getDefaultLayout(), true, true, null);
			widget.listener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (widget.isOn()) {
						// change or restart layout
						parent.setLayout(name, layout.clone() );
						AllLayouts.setDefaultLayout(name);
						updateLayoutWidgets();
						repaint();
					} else {
						// stop layout
						if (parent.layout != null && parent.layout.isComputingLayout())
							parent.layout.stopLayout();
						updateLayoutWidgets();
						repaint();
					}
				}
			};
			widget.setRadioGroup(layoutWidgets);
			widgets.add(widget);
			layoutWidgets.add(widget);
		}
	}
	
	public void layout() {
		int checky = (View.layerBarHeight - CheckBox.size)/2;
		int spacex = CheckBox.size + 4;
		check_solo.bounds.setLocation(5, checky);
		check_visible.bounds.setLocation(5 + spacex, checky);
		check_close.bounds.setLocation(bounds.width - CheckBox.size - 5, checky);
		
		int x = title_right;
		for (int i = 0; i<layoutWidgets.size(); i++) {
			if (AllLayouts.separators.contains(i))
				x += 4;
			CheckBox w = layoutWidgets.get(i);
			w.bounds.setLocation(x, checky);
			x += spacex - 4;
		}
		
		layoutBars_right = x;
	}
	
	public void updateLayoutWidgets() {
		boolean running = parent.layout != null && parent.layout.isComputingLayout();
		for (CheckBox w : layoutWidgets) {
			if (w.getName().equals(currentLayoutName)) {
				w.setOn(true);
				w.setFinished(!running);
			} else {
				w.setOn(false);
			}
			boolean enabled = parent.isVisible();
			// FIXME
			Layout layout = AllLayouts.allLayouts.get(w.getName());
			if (layout instanceof MasterAlignLayout) {
				enabled &= ((MasterAlignLayout)layout).canConfigureLayout(parent);
			}
			w.setEnabled(enabled);
		}
		repaint();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public void repaint() {
		if (parent != null && parent.parent != null) {
			parent.parent.repaint(bounds);
		}
	}
	
	public void paint(Graphics2D g) {
		Object aa = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		g.setStroke(borderStroke);
		Color fontColor = GUIUtils.mix(baseColor, Color.black, 0.3f);
		if (!parent.isVisible())
			fontColor = GUIUtils.multiplyAlpha(fontColor, 0.2f);
		
		// Border
		g.setColor(GUIUtils.mix(baseColor, Color.white, 0.95f));
		g.fillRect(0, 0, bounds.width, bounds.height);
		g.setColor(JViews.borderColor);
		g.drawRect(0, 0, bounds.width, bounds.height);
		
		// Title
		g.setColor(fontColor);
		g.setFont(title_font);
		Shape c0 = g.getClip();
		g.clip(new Rectangle(0, 0, title_right - 8, bounds.height));
		g.drawString(title, check_visible.bounds.x + check_visible.bounds.width + 6, bounds.height - 4);
		g.setClip(c0);
		
		// Widgets
		paintWidgets(g);
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aa);		
	}
	
	protected void paintWidgets(Graphics2D g) {
		// Widgets
		for (CheckBox w : widgets)
			w.paint(g);
		
		// Layout name
		Color fontColor = GUIUtils.mix(baseColor, Color.black, 0.3f);
		g.setColor(fontColor);
		g.setFont(layout_font);
		g.drawString(currentLayoutName, layoutBars_right + 6, bounds.height-4);
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
		layout();
	}
	
	public void move(int deltax, int deltay) {
		bounds.setBounds(bounds.x + deltax, bounds.y + deltay, bounds.width, bounds.height);
	}

	public Color getBaseColor() {
		return baseColor;
	}

	public void setBaseColor(Color baseColor) {
		this.baseColor = baseColor;
		for (CheckBox w : widgets)
			w.setBaseColor(baseColor);
	}
	
	public CheckBox pickWidget(int x, int y) {
		for (CheckBox w : widgets)
			if (w.isEnabled() && x >= w.bounds.x - 2 && x <= w.bounds.x + w.bounds.width + 2)
				return w;
		return null;
	}
	
	public boolean isNewMasterBar() {
		return false;
	}

}
