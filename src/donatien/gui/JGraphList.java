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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

public class JGraphList extends JComponent implements MouseInputListener {

	ArrayList<GraphIcon> fileIcons = new ArrayList<GraphIcon>();
	JViews dest;
	int margin = 3;
	int starty = 17;
	Stroke selectionStroke = new BasicStroke(1.5f);
	String title = "Graphs";
	int max_y = 0;
	
	// Interaction state
	int offsetX, offsetY;
	GraphIcon selectedIcon = null;
	GraphIcon ghostIcon = null;
	View ghostView = null;
	boolean showGhostIcon = false;
	boolean showGhostView = false;
	
	public JGraphList(JViews dest) {
		super();
		this.dest = dest;
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				layoutIcons();
				repaint();
			}
		});
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void addGraphIcon(GraphIcon icon) {
		fileIcons.add(icon);
		layoutIcons();
		repaint();
	}
	
	public void clearGraphIcons() {
		fileIcons.clear();
		selectedIcon = null;
		layoutIcons();
		repaint();
	}
	
	protected void layoutIcons() {
		int w = getWidth();
		int h = getHeight();
		int y = 0;
		for (GraphIcon b : fileIcons) {
			b.setBounds(new Rectangle(margin, starty + margin + y, w - 2*margin, View.layerBarHeight));
			y += View.layerBarHeight;
		}
		max_y = y;
	}
	
	public void load(String name) {
		GraphIcon icon2load = null;
		for (GraphIcon icon : fileIcons) {
			if (icon.getTitle().equals(name))
				icon2load = icon;
		}
		if (icon2load == null)
			return;
		selectedIcon = icon2load;
		Layer l = new Layer();
		l.setGraph(null);
		l.setDefaultLayout();
		dest.addLayer(l);
		GraphLoader.loadGraph(icon2load.getTitle(), icon2load.filename, l);
		repaint();
	}
	
	public void paint(Graphics g_) {
		Graphics2D g = (Graphics2D)g_;
		g.setColor(Color.white);
		g.fill(getBounds());
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Title
		g.setColor(new Color(0.6f, 0.6f, 0.6f));
		g.setFont(new Font("Sanserif", Font.BOLD, 12));
		g.drawString(title, margin + 5, starty - 2);
		
		// Icon list
		AffineTransform at0 = g.getTransform();
		for (GraphIcon icon : fileIcons) {
			g.translate(icon.bounds.x, icon.bounds.y);
			icon.paint(g);
			g.setTransform(at0);
		}
		
		// Selection
		if (selectedIcon != null) {
			g.setStroke(selectionStroke);
			g.setColor(Color.black);
			g.draw(selectedIcon.getBounds());
		}
		
		// Ghost
		if (ghostIcon != null && showGhostIcon) {
			g.translate(ghostIcon.bounds.x, ghostIcon.bounds.y);
			ghostIcon.paint(g);
			g.setTransform(at0);
		}
	}
	
	public GraphIcon pick(int x, int y) {
		for (GraphIcon icon : fileIcons) {
			if (icon.bounds.contains(x, y))
				return icon;
		}
		return null;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		selectedIcon = pick(e.getX(), e.getY());
		if (selectedIcon != null) {
			offsetX = e.getX() - selectedIcon.getBounds().x;
			offsetY = e.getY() - selectedIcon.getBounds().y;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
		if (selectedIcon != null) {			
			Point pdest = getLocationInDest(e.getX(), e.getY());
			showGhostIcon = getBounds().contains(e.getX(), e.getY());
			showGhostView = dest.getBounds().contains(pdest);			
			if (showGhostIcon) {
				if (ghostIcon == null) {
					ghostIcon = new GraphIcon();
					ghostIcon.setTitle(selectedIcon.getTitle());
					ghostIcon.filename = selectedIcon.filename;
					ghostIcon.translucent = true;
					ghostIcon.setBounds(new Rectangle(selectedIcon.getBounds()));
				}
				ghostIcon.getBounds().setLocation(e.getX() - offsetX, e.getY() - offsetY);
			}
			dest.setDraggedView(showGhostView ? ghostView : null);
			if (showGhostView) {
				if (ghostView == null) {
					Layer l = new Layer();
					l.setGraph(null); // graph not loaded yet
					l.setDefaultLayout();
					l.setTitle(selectedIcon.getTitle());
					Dimension vs = dest.getDefaultViewSize();
					ghostView = new View();
					ghostView.setParent(dest);
					ghostView.setBounds(new Rectangle(pdest.x - vs.width / 2, pdest.y - offsetY, vs.width, vs.height));
					ghostView.addLayer(l);
					ghostView.setTranslucent(true);
					l.setParent(ghostView);
					dest.setDraggedView(ghostView);
					GraphLoader.loadGraph(selectedIcon.getTitle(), selectedIcon.filename, l);
				}
				ghostView.repaint();
				ghostView.getBounds().setLocation(pdest.x - ghostView.bounds.width / 2, pdest.y - offsetY);
				dest.updateDropTarget();
				ghostView.repaint();
			}
			repaint();
		}
	}
	
	protected Point getLocationInDest(int x, int y) {
		Rectangle b = getBounds();
		Rectangle bd = dest.getBounds();
		return new Point(x + b.x - bd.y, y + b.y - bd.y);
	}
	
	@Override
	public void mouseReleased(MouseEvent arg0) {
		if (selectedIcon != null && ghostView != null && showGhostView) {
			dest.setDraggedView(ghostView);
			dest.drop();
			dest.setDraggedView(null);
		}
//		selectedIcon = null;
		ghostIcon = null;
		ghostView = null;
		repaint();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(250, max_y);
	}
}
