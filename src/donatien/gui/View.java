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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import donatien.model.graph.Graph;
import donatien.model.graph.Node;
import donatien.util.GUIUtils;
import donatien.util.Pair;

public class View {
	static int layerBarHeight = 18;

	JViews parent;
	
	private ArrayList<Layer> layers = new ArrayList<Layer>();
	
	// Total bounds as seen by its parent
	Rectangle bounds;
	
	// Local geometry
	Rectangle layerBarsBounds = new Rectangle();
	Rectangle contentBounds = new Rectangle();
	
	boolean translucent = false;
	
	// Alignment feedback
	ArrayList<ArrayList<Pair<Layer, Node>>> alignmentGroups = new ArrayList<ArrayList<Pair<Layer, Node>>>();
	private boolean alignmentDataNeedUpdate = true;
	private boolean alignmentShapesNeedUpdate = true;
	ArrayList<Shape> alignmentShapes = new ArrayList<Shape>();
	ArrayList<Color> alignmentShapeColors = new ArrayList<Color>();
	ArrayList<ArrayList<Color>> alignmentShapeColorList = new ArrayList<ArrayList<Color>>();
	
	static Stroke borderStroke = new BasicStroke(1f);
	
	public View() {

	}
	
	public void addLayer(Layer layer) {
		addLayer(layer, layers.size());
	}
	
	public void addLayer(Layer layer, int index) {
				
		layer.setParent(this);
		layer.setBounds(contentBounds);
		layers.add(index, layer);
		layout();
		updateColors();
		updateRadioGroups();
		parent.updateLayoutWidgets();
		if (getSoloLayer() != null) {
			layer.bar.check_solo.setOn(true);
			updateVisibilities();
		}
		repaint();
	}
	
	public int getVisibleLayers() {
		int count = 0;
		for (Layer l : layers)
			if (l.isVisible())
				count++;
		return count;
	}
	
	public void removeLayer(Layer layer) {
		layer.setParent(null);
		layers.remove(layer);
		layout();
		updateColors();
		updateRadioGroups();
		parent.updateLayoutWidgets();
		repaint();
		parent.removeEmptyViews();
	}
	
	public void updateColors() {
//		float opacity = (float)Math.pow(1f/layers.size(), 0.4f);
//		opacity = Math.max(0.2f, opacity);
		float opacity = 1;
		if (getVisibleLayers() > 1)
			opacity = 0.75f;
		int colorIndex = 0;
		for (Layer l : layers) {
			if (!(l instanceof MasterLayer)) {
				l.setBaseColor(Colors.getPredefinedColor(colorIndex));
				l.setOpacity(opacity);
				colorIndex++;
			} else {
				l.setBaseColor(MasterLayer.getDefaultBaseColor());
				l.setOpacity(opacity);
			}
		}
	}
	
	public Color getNextColor() {
		int colorIndex = 0;
		for (Layer l : layers) {
			if (!(l instanceof MasterLayer))
				colorIndex++;
		}
		return Colors.getPredefinedColor(colorIndex);
	}
	
	public void updateRadioGroups() {
		ArrayList<CheckBox> solos = new ArrayList<CheckBox>();
		for (Layer l : layers) {
			solos.add(l.bar.check_solo);
			l.bar.check_solo.setRadioGroup(solos);
		}
	}
	
	public void updateLayoutWidgets() {
		for (Layer l : layers) {
			l.bar.updateLayoutWidgets();
		}		
	}
	
	public ArrayList<Layer> getLayers() {
		return layers;
	}
	
	public int getNonEmptyLayerCount() {
		int n = 0;
		for (Layer l : layers)
			if (l.getGraph() != null)
				n++;
		return n;
	}
	
	public Layer getLayer(Graph graph) {
		for (Layer l : layers)
			if (l.getGraph() == graph)
				return l;
		return null;
	}
	
	public Layer getLayer(String graphName, boolean onlyIfVisible) {
		for (Layer l : layers)
			if (l.getGraph() != null && l.getGraph().getName() != null && l.getGraph().getName().equals(graphName))
				if (!onlyIfVisible || l.isVisible())
					return l;
		return null;
	}
	
	public Layer getLayerAbove(Layer layer, boolean onlyIfVisible) {
		int l = layers.indexOf(layer);
		if (l <= 0)
			return null;
		for (int i = l-1; i >=0; i--) {
			if (!onlyIfVisible || layers.get(i).isVisible())
				return layers.get(i);
		}
		return null;
	}
	
	public Layer getBottomMostVisibleLayer() {
		for (int i = getLayers().size() - 1; i >= 0; i--) {
			Layer l = getLayers().get(i);
			if (l.isVisible())
				return l;
		}
		return null;
	}
	
	public View getPreviousView() {
		if (parent == null)
			return null;
		int i = parent.getViews().indexOf(this);
		if (i <= 0)
			return null;
		return parent.getViews().get(i - 1);
	}
	
	/**
	 * Picks a node, with x, y specified in component's coordinates.
	 * 
	 * @param x
	 * @param y
	 * @param pickSize
	 * @return
	 */
	public ArrayList<Pair<Layer, Node>> pickNodes(double x, double y) {
		ArrayList<Pair<Layer, Node>> lnodes = new ArrayList<Pair<Layer, Node>>();
		for (int i = getLayers().size() - 1; i >= 0; i--) {
			Layer l = getLayers().get(i);
			if (!l.isVisible())
				continue;
			ArrayList<Node> nodes = l.pickNodes(x - contentBounds.x, y - contentBounds.y);
			if (nodes != null) {
				for (Node n : nodes)
					lnodes.add(new Pair<Layer, Node>(l, n));
			}
		}
		return lnodes;
	}
	
	/**
	 * Picks a node, with x, y specified in component's coordinates.
	 * 
	 * @param x
	 * @param y
	 * @param pickSize
	 * @return
	 */
	public ArrayList<Pair<Layer, Node>> pickNodeCenters(Rectangle2D rec, boolean includeMasterLayers) {
		ArrayList<Pair<Layer, Node>> lnodes = new ArrayList<Pair<Layer, Node>>();
		Rectangle2D lrec = new Rectangle2D.Double(rec.getX() - contentBounds.x, rec.getY() - contentBounds.y, rec.getWidth(), rec.getHeight());
		for (int i = getLayers().size() - 1; i >= 0; i--) {
			Layer l = getLayers().get(i);
			if (!l.isVisible())
				continue;
			if ((!includeMasterLayers) && l instanceof MasterLayer)
				continue;
			ArrayList<Node> nodes = l.pickNodeCenters(lrec);
			if (nodes != null) {
				for (Node n : nodes)
					lnodes.add(new Pair<Layer, Node>(l, n));
			}
		}
		return lnodes;
	}
	
	/**
	 * Picks a node, with x, y specified in component's coordinates.
	 * 
	 * @param x
	 * @param y
	 * @param pickSize
	 * @return
	 */
//	private Pair<Layer, Node> pickNode(double x, double y) {
//		for (int i = getLayers().size() - 1; i >= 0; i--) {
//			Layer l = getLayers().get(i);
//			if (!l.isVisible())
//				continue;
//			Node n = l.pickNode(x - contentBounds.x, y - contentBounds.y);
//			if (n != null)
//				return new Pair<Layer, Node>(l, n);
//		}
//		return null;
//	}
	
	public Pair<Layer, LayerBar> pickBar(double x, double y) {
		for (int i = getLayers().size() - 1; i >= 0; i--) {
			Layer l = getLayers().get(i);
			if (l.enabled && l.getBar().getBounds().contains(new Point2D.Double(x, y))) {
				return new Pair<Layer, LayerBar>(l, l.getBar());
			}
		}
		return null;
	}
	
	public Pair<LayerBar, CheckBox> pickWidget(int x, int y) {
		for (Layer l : layers) {
			LayerBar bar = l.getBar();
			if (bar.bounds.contains(new Point2D.Double(x, y))) {
				return new Pair<LayerBar, CheckBox>(bar,
					bar.pickWidget(x - bar.bounds.x, y - bar.bounds.y)); 
			}
		}
		return null;
	}
	
	public void pan(double dx, double dy) {
		for (Layer l : layers)
			l.pan(dx, dy);
	}
	
	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
		layout();
	}
	
	public void move(int deltax, int deltay) {
		repaint();
		bounds.setBounds(bounds.x + deltax, bounds.y + deltay, bounds.width, bounds.height);
		repaint();
	}
	
	public void layout() {
		
		if (bounds == null)
			return;
		
		// internal layout
		int ysplit = layerBarHeight * layers.size();
		layerBarsBounds.setBounds(0, 0, bounds.width, ysplit);
		contentBounds.setBounds(0, ysplit + 2, bounds.width, bounds.height - ysplit - 2);
		for (int i = 0; i < layers.size(); i++) {
			Layer l = layers.get(i);			
			// Update content bounds
			l.setBounds(new Rectangle(contentBounds));
			// Update layer bar bounds
			int y = i * layerBarHeight;
			l.getBar().setBounds(new Rectangle(layerBarsBounds.x, layerBarsBounds.y + y, layerBarsBounds.width, layerBarHeight));
		}
	}
	
	public void zoom(Point2D origin, double amount_x, double amount_y, boolean bottomMost, boolean growObjects, boolean scatterObjects) {
		if (bottomMost) {
			Layer l = getBottomMostVisibleLayer();
			if (l != null) {
				Point2D o = new Point2D.Double(origin.getX() - contentBounds.x, origin.getY() - contentBounds.y);
				l.zoom(o, amount_x, amount_y, growObjects, scatterObjects);
			}
		} else {
			for (Layer l : layers) {
				Point2D o = new Point2D.Double(origin.getX() - contentBounds.x, origin.getY() - contentBounds.y);
				l.zoom(o, amount_x, amount_y, growObjects, scatterObjects);
			}
		}
	}

	public void repaint() {
		if (parent != null && bounds != null) {
			parent.repaint(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4);
		}
	}
	
	public void repaint(Rectangle r) {
		if (parent != null && bounds != null) {
			parent.repaint(bounds.x + r.x, bounds.y + r.x, r.width, r.height);
		}
	}
	
	public void paint(Graphics2D g) {
		
		// Background & init
		if (isTranslucent())
			g.setColor(new Color(1f, 1f, 1f, 0.25f));
		else
			g.setColor(Color.white);
		g.fillRect(0, 0, bounds.width, bounds.height);
		Graphics2D g2 = (Graphics2D)g;
		
		AffineTransform at0 = g.getTransform();
		
		// Layer bars
		for (int i = 0; i < layers.size(); i++) {
			LayerBar bar = layers.get(i).getBar();
			g.translate(bar.bounds.x, bar.bounds.y);
			bar.paint(g);
			g.setTransform(at0);
		}
		
		// Layers
		if (g.getClip().intersects(contentBounds)) {
			g.translate(contentBounds.x, contentBounds.y);
			for (Layer l : layers) {
				l.paint(g);
			}
			g.setTransform(at0);
		}
		
		// Display alignment shapes
		if (!isLayerInteracting()) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (alignmentDataNeedUpdate)
				updateAlignmentData();
			if (alignmentShapesNeedUpdate)
				try {
					updateAlignmentShapes();
				} catch (Exception e) {
					e.printStackTrace();
				}
			if (alignmentShapes.size() > 0) {
				Shape clip0 = g.getClip();
				g.clip(contentBounds);
				Stroke thinStroke = new BasicStroke(1);
				Stroke thickStroke = new BasicStroke(4);
				Color border = new Color(1f, 1f, 1f);
				for (int i=0; i<alignmentShapes.size(); i++) {
					g.setColor(alignmentShapeColors.get(i));
					g.setStroke(thickStroke);
					g.draw(alignmentShapes.get(i));
					//
					Rectangle2D b = alignmentShapes.get(i).getBounds2D();
					ArrayList<Color> colorList = alignmentShapeColorList.get(i);
					double r = 5;
					double mx = (b.getX() + b.getWidth()/2);
//					g.setColor(Color.white);
//					g.fillRect((int)(mx - colorList.size()*r/2) - 1, (int)b.getY() - 2, (int)(colorList.size() * r) + 2, (int)r + 2);
					for (int c=0; c < colorList.size(); c++) {
						g.setColor(GUIUtils.multiplyAlpha(colorList.get(c), 1f));
						int x = (int)(mx - colorList.size()*r/2 + r * (colorList.size() - 1 - c));
						g.fillRect(x, (int)b.getY() - 2, (int)r, (int)r);
						g.setColor(border);
						g.setStroke(thinStroke);
						g.drawRect(x, (int)b.getY() - 2, (int)r, (int)r);
					}
				}
				g.setClip(clip0);
			}
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
	}
	
	public boolean isLayerInteracting() {
		for (Layer l : layers)
			if (parent.isInteracting(l))
				return true;
		return false;
	}

	public JViews getParent() {
		return parent;
	}

	public void setParent(JViews parent) {
		this.parent = parent;
	}

	public boolean isTranslucent() {
		return translucent;
	}

	public void setTranslucent(boolean translucent) {
		this.translucent = translucent;
	}
	
	public Layer getSoloLayer() {
		Layer soloLayer = null;
		for (Layer l : layers)
			if (l.bar.check_solo.isOn())
				soloLayer = l;
		return soloLayer;
	}
	
	public void updateVisibilities() {
		
		// Unique layer
//		if (layers.size() == 1) {
//			layers.get(0).bar.check_solo.setOn(false);
//			layers.get(0).bar.check_solo.setEnabled(false);
//			layers.get(0).bar.check_visible.setOn(true);
//			layers.get(0).bar.check_visible.setEnabled(false);
//			return;
//		}
//		
		Layer soloLayer = getSoloLayer();
		
		if (soloLayer != null) {
			for (Layer l : layers) {
				if (l.isNewMasterLayer())
					continue;
				l.setVisible(l == soloLayer);
				l.bar.check_visible.setEnabled(false);
//				l.bar.check_solo.setEnabled(layers.size() > 1);
			}
		} else {
			for (Layer l : layers) {
				if (l.isNewMasterLayer())
					continue;
				l.setVisible(l.bar.check_visible.isOn());
				l.bar.check_visible.setEnabled(true);
//				l.bar.check_solo.setEnabled(layers.size() > 1);
			}
		}
		
		if (parent != null) {
			parent.updateLayoutWidgets();
		}
		
		invalidateAlignmentData();
	}
	
	public int getTotalNodes() {
		int nodes = 0;
		for (Layer l : layers) {
			if (l.isVisible())
				nodes += l.getGraph().getNodes().size();
		}
		return nodes;
	}
	
	public void invalidateBuffers() {
		for (Layer l : layers)
			l.invalidateBuffer();
	}
	
	public Point2D getPositionInView(Layer l, Node n) {
		Point2D p = l.getPositionInLayer(n);
		p.setLocation(p.getX() + contentBounds.x, p.getY() + contentBounds.y);
		return p;
	}
	
	public void setPositionInView(Layer l, Node n, Point2D p) {
		p = new Point2D.Double(p.getX(), p.getY());
		p.setLocation(p.getX() - contentBounds.x, p.getY() - contentBounds.y);
		l.setPositionInLayer(n, p);
	}
	
	public Shape getShapeInView(Layer l, Node n, double grow) {
		Shape s = l.getShapeInLayer(n, grow);
		if (s == null)
			return null;
		return (AffineTransform.getTranslateInstance(contentBounds.x, contentBounds.y)).createTransformedShape(s);
	}
	
	/**
	 * FIXME! Use the distance matrix instead.
	 * @param model
	 */
	public void updateAlignmentData() {
		
//System.out.println("Updating alignment data on view " + parent.getViews().indexOf(this));
		
		// -- Also invalidate further views
		if (parent != null) {
			int viewIndex = parent.getViews().indexOf(this);
			for (int i=viewIndex+1; i<parent.getViews().size(); i++) {
				View v = parent.getViews().get(i);
				v.invalidateAlignmentData();
				v.invalidateBuffers();
				v.repaint();
			}
		}

		// -- Use alignment data from the previous view (FIXME)
//		int viewIndex;
//		if (parent != null && (viewIndex = parent.getViews().indexOf(this)) > 0) {
//			View model = parent.getViews().get(viewIndex - 1);
//			alignmentGroups.clear();
//			for (ArrayList<Pair<Layer, Node>> group : model.alignmentGroups) {
//				ArrayList<Pair<Layer, Node>> thisgroup = new ArrayList<Pair<Layer, Node>>();
//				for (Pair<Layer, Node> node : group) {
//					Layer thislayer = getLayer(node.getKey().getGraph().getName(), true);
//					if (thislayer == null || !thislayer.isVisible())
//						continue;
//					Node thisnode = thislayer.getGraph().getNodes().get(node.getValue().index);
//					thisgroup.add(new Pair<Layer, Node>(thislayer, thisnode));
//				}
//				alignmentGroups.add(thisgroup);
//			}
//			alignmentDataNeedUpdate = false;
//			return;
//		}
		
		// -- Build the list of points to probe
		double epsilon = 0.000001; // tolerance in pixels
		double epsilonSq = epsilon * epsilon;
		ArrayList<Point2D> probePoints = new ArrayList<Point2D>();
		for (Layer l : layers) {
			if (l instanceof MasterLayer || l.getGraph() == null || !l.isVisible())
				continue;
			for (Node n : l.getGraph().getNodes()) {
				Point2D p = getPositionInView(l, n);
				boolean separate = true;
				for (Point2D pp : probePoints)
					separate &= pp.distanceSq(p) > epsilonSq;
				if (separate)
					probePoints.add(p);
			}
		}
		
		// -- Pick nodes based on probe points and group them
		alignmentGroups.clear();
		
		Rectangle2D.Double pickRec = new Rectangle2D.Double();
		for (Point2D p : probePoints) {
			pickRec.setRect(p.getX() - epsilon, p.getY() - epsilon, epsilon*2, epsilon*2);
			ArrayList<Pair<Layer, Node>> picked = pickNodeCenters(pickRec, false);
			//if (picked.size() > 1) {
				alignmentGroups.add(picked);
			//}
			for (Pair<Layer, Node> ln : picked)
				ln.getValue().matched = picked.size() > 1;
		}
		
		alignmentDataNeedUpdate = false;
	}
	
	public ArrayList<ArrayList<Pair<Layer, Node>>> getAlignmentGroups() {
		if (alignmentDataNeedUpdate)
			updateAlignmentData();
		return alignmentGroups;
	}
	
	/**
	 * 
	 */
	protected void updateAlignmentShapes() {
		// -- Build shapes
		alignmentShapes.clear();
		alignmentShapeColors.clear();
		alignmentShapeColorList.clear();
		for (ArrayList<Pair<Layer, Node>> group : alignmentGroups) {
			if (group.size() > 1) {
				Pair<Area,Color> combined = JViews.combineNodeShapes(this, group, true, false);
				alignmentShapes.add(combined.getKey());
				alignmentShapeColors.add(combined.getValue());
				ArrayList<Color> colorList = new ArrayList<Color>();
				for (Pair<Layer, Node> ln : group)
					colorList.add(ln.getKey().getBaseColor());
				alignmentShapeColorList.add(colorList);
			}
		}
		alignmentShapesNeedUpdate = false;
	}
	
	public Point2D.Double getAverageNodeCenter(ArrayList<Pair<Layer, Node>> nodes) {
		Point2D.Double avg = new Point2D.Double(0, 0);
		for (Pair<Layer, Node> node : nodes) {
			Point2D p = getPositionInView(node.getKey(), node.getValue());
			avg.setLocation(avg.getX() + p.getX(), avg.getY() + p.getY());
		}
		avg.setLocation(avg.getX() / nodes.size(), avg.getY() / nodes.size());
		return avg;
	}
	
	void invalidateAlignmentData() {
		alignmentDataNeedUpdate = true;
		alignmentShapesNeedUpdate = true;
	}
	
	public MasterLayer getActiveMasterLayer() {
		// return first visible master layer
		for (Layer l : getLayers()) {
			if (l instanceof MasterLayer && l.isVisible() && l.getGraph() != null)
				return (MasterLayer)l;
		}
		return null;
	}

}
