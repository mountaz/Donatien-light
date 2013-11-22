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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.ToolTipManager;

import donatien.model.graph.Edge;
import donatien.model.graph.Graph;
import donatien.model.graph.Node;
import donatien.util.GUIUtils;
import donatien.util.GUIUtils.AdvancedKeyListener;
import donatien.util.Pair;

/**
 * Shows several views side-by-side.
 * @author dragice
 *
 */
public class JViews extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener, AdvancedKeyListener {

	public ArrayList<View> views = new ArrayList<View>();
	
	// Interaction state
	private int oldX, oldY; // suivi de la souris
	private View selectedView = null;
	private LayerBar selectedBar = null;
	private Layer selectedLayer = null;
	private ArrayList<Pair<Layer, Node>> selectedNodes = null;
	private CheckBox selectedWidget = null;
	private CheckBox crossedWidget = null;
	private View draggedView = null; // used during drag and drop
	private View viewDropTarget = null;
	private int layerDropIndex = -1;
	private int viewDropIndex = -1;
	private View focusedView = null, xrayView = null;
	private boolean zoomingWithWheel = false;
	private int zoomingWithKeyboard = 0;
	private Shape snapShape = null;
	private Color snapShapeColor;
	ArrayList<Pair<Layer, Node>> snapSources = null, snapTargets = null;
	
	static Stroke borderStroke = new BasicStroke(2f);
	static Stroke dropTargetStroke = new BasicStroke(4f);
	static Color borderColor = new Color(0.7f, 0.7f, 0.7f);
	static int margin = 2;
	
	public enum LayoutMethod {
	    Horizontal, Vertical, SquareGrid
	}
	LayoutMethod layoutMethod = LayoutMethod.SquareGrid;

	public JViews() {
		super();
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				layoutViews();
				repaint();
			}
		});
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		GUIUtils.addAdvancedKeyListener(null, this, true);
		ToolTipManager.sharedInstance().setInitialDelay(150);
	}
	
	public void addView(View view) {
		addView(view, views.size());
	}
	
	public void addView(View view, int index) {
		if (views.contains(view))
			return;
		view.parent = this;
		views.add(index, view);
		layoutViews();
		repaint();
	}
	
	public void removeView(View view) {
		if (!views.contains(view))
			return;
		views.remove(view);
		view.parent = null;
		layoutViews();
		repaint();
	}
	
	public void addLayer(Layer layer) {
		if (getLayers().contains(layer))
			return;
//		if (getViews().size() > 0) { // DEBUG
//			getViews().get(0).addLayer(layer);
//		} else {
			View view = new View();
			view.addLayer(layer);
			addView(view);
//		}
		repaint();
	}
	
	public void removeLayer(Layer layer) {
		View view = getView(layer);
		if (view == null)
			return;
		view.removeLayer(layer);
		if (view.getLayers().size() == 0)
			removeView(view);
		repaint();
	}
	
	public View getView(Layer layer) {
		for (View v : views) {
			if (v.getLayers().contains(layer))
				return v;
		}
		return null;
	}
	
	public Layer getLayer(Graph graph) {
		for (View v : views) {
			Layer l = v.getLayer(graph);
			if (l != null)
				return l;
		}
		return null;
	}
	
	public void layoutViews() {
		int w = getWidth() - margin*2;
		int h = getHeight() - margin*2;
		int n = views.size();
		int x = 0, y = 0;
		int cols = 0;
		if (layoutMethod == LayoutMethod.Horizontal)
			cols = n;
		else if (layoutMethod == LayoutMethod.Vertical)
			cols = 1;
		else if (layoutMethod == LayoutMethod.SquareGrid)
			cols = (int)Math.ceil(Math.sqrt(n));
		int rows = (int)Math.ceil(n / (double)cols);
		for (View view : views) {
			Rectangle newbounds = new Rectangle(margin + x * w / cols, margin + y * h / rows, w / cols, h / rows);
			view.setBounds(newbounds);			
			x++;
			if (x == cols) {
				x = 0;
				y++;
			}
		}
	}
	
	public View pickView(int x, int y) {
		for (View view : views) {
			if (view.getBounds().contains(x, y))
				return view;
		}
		return null;
	}
	

	public ArrayList<View> getViews() {
		return views;
	}
	
	public ArrayList<Layer> getLayers() {
		ArrayList<Layer> layers = new ArrayList<Layer>();
		for (View v : views)
			layers.addAll(v.getLayers());
		return layers;
	}
	
	public ArrayList<Graph> getGraphs() {
		ArrayList<Graph> graphs = new ArrayList<Graph>();
		for (Layer l : getLayers())
			graphs.add(l.getGraph());
		return graphs;
	}
	
	public ArrayList<String> getGraphNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (Layer l : getLayers())
			names.add(l.getGraph().getName());
		return names;
	}
	
	public void removeEmptyViews() {
		ArrayList<View> remove = new ArrayList<View>();
		for (View v : views) {
			if (v.getNonEmptyLayerCount() == 0)
				remove.add(v);
		}
		for (View v : remove)
			removeView(v);
		if (remove.size() > 0)
			layoutViews();
	}
	
	public void layoutAll() {
		for (Layer l : getLayers())
			l.computeLayout();
	}
	
	public void updateLayoutWidgets() {
		for (View v : getViews())
			v.updateLayoutWidgets();
	}
	
	public Dimension getDefaultViewSize() {
		if (views.size() == 0)
			return new Dimension(getWidth(), getHeight());
		return new Dimension(views.get(0).getBounds().width, views.get(0).getBounds().height);
	}
	
	public void paint(Graphics g_) {
		Graphics2D g = (Graphics2D)g_;
		
		// All views
		for (View view : views) {
			paintView(g, view);
		}
		
		// Dragged view
		if (draggedView != null) {
			paintView(g, draggedView);
		}
		
		// Snap shape feedback
		if (snapShape != null) {
			g.setStroke(new BasicStroke(8));
			g.setColor(snapShapeColor);
			g.draw(snapShape);
		}
		
		// Drop target feedback
		if (draggedView != null) {
			Rectangle highlight = null;
			if (layerDropIndex != -1) {
				// insert inside view
				Rectangle vb = viewDropTarget.getBounds();
				Rectangle vcb = viewDropTarget.contentBounds;
				highlight = new Rectangle(
					vb.x + vcb.x,
					vb.y + vcb.y,
					vcb.width,
					vcb.height);
				g.setColor(GUIUtils.multiplyAlpha(viewDropTarget.getNextColor(), 0.5f));
			} else if (viewDropIndex != -1) {
				final int w = 2;
				// insert between views
				Rectangle vb = null;
				boolean left = true;
				if (viewDropTarget != null) {
					vb = viewDropTarget.getBounds(); 
					left = (views.indexOf(viewDropTarget) == viewDropIndex);
				} else if (viewDropIndex < views.size()) {
					vb = views.get(viewDropIndex).getBounds();
					left = true;
				} else {
					if (views.size() > 0)
						vb = views.get(viewDropIndex - 1).getBounds();
					else
						vb = getBounds();
					left = false;
				}
				highlight = new Rectangle(
					vb.x + (left ? 0 : vb.width) - w,
					vb.y,
					w * 2,
					vb.height
				);
				g.setColor(GUIUtils.multiplyAlpha(Colors.getPredefinedColor(0), 0.5f));
			}
			if (highlight != null) {
				g.setStroke(dropTargetStroke);
				g.draw(highlight);
			}
		}
	}
	
	protected void paintView(Graphics2D g, View view) {
		AffineTransform at0 = g.getTransform();
		Shape clip0 = g.getClip();
		g.translate(view.getBounds().x, view.getBounds().y);
		view.paint(g);
		g.setStroke(borderStroke);
		g.setColor(borderColor);
		int m = 0;//(int)(((BasicStroke)borderStroke).getLineWidth()/2);
		g.drawRect(m, m, view.getBounds().width-m*2, view.getBounds().height-m*2-1);
		g.setTransform(at0);
		g.setClip(clip0);
	}
	
	/////////////////////////////////////////////////////////////////////
	
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
	public void mouseMoved(MouseEvent e) {
		String tooltip = null;
		View pickedView = pickView(e.getX(), e.getY());
		
		// Update tooltip
		if (pickedView != null) {
			Pair<LayerBar, CheckBox> pickedw = pickedView.pickWidget(e.getX() - pickedView.getBounds().x, e.getY() - pickedView.getBounds().y);
			if (pickedw != null) {
				CheckBox selectedWidget = pickedw.getValue();
				if (selectedWidget != null)
					tooltip = selectedWidget.getName();
			}
		}
		setToolTipText(tooltip);
		
		// Update focused view
		if (!zoomingWithWheel && zoomingWithKeyboard == 0)
			focusedView = pickedView;
	}

	public void mousePressed(MouseEvent e) {
		oldX = e.getX();
		oldY = e.getY();
		selectedView = pickView(e.getX(), e.getY());
		selectedLayer = null;
		selectedNodes = null;
		selectedBar = null;
		selectedWidget = null;
		crossedWidget = null;
		if (selectedView != null) {
			Pair<LayerBar, CheckBox> pickedw = selectedView.pickWidget(e.getX() - selectedView.getBounds().x, e.getY() - selectedView.getBounds().y);
			if (pickedw != null && pickedw.getValue() != null) {
				selectedBar = pickedw.getKey();
				selectedWidget = pickedw.getValue();
				crossedWidget = selectedWidget;
				selectedWidget.press();
				selectedBar.repaint();
			} else {
				Pair<Layer, LayerBar> pickedb = selectedView.pickBar(e.getX() - selectedView.getBounds().x, e.getY() - selectedView.getBounds().y);
				if (pickedb != null) {
					selectedLayer = pickedb.getKey();
					selectedBar = pickedb.getValue();
				} else {
					selectedNodes = selectedView.pickNodes(e.getX() - selectedView.getBounds().x, e.getY() - selectedView.getBounds().y);
					if (selectedNodes != null && selectedNodes.isEmpty()) {
						selectedNodes = null;
						if (GUIUtils.isControlDown(e)) {
							selectedLayer = selectedView.getBottomMostVisibleLayer();
						}
					}
					if (selectedNodes != null && GUIUtils.isControlDown(e)) {
						Pair<Layer, Node> sel = selectedNodes.get(0);
						selectedNodes.clear();
						selectedNodes.add(sel);
					}
				}
			}
		}
//if (selected == null)
//	System.out.println(selected);
//else
//	System.out.println(selected.getProp("name"));
//		repaint();		
	}

	public void mouseDragged(MouseEvent e) {
		if (selectedView == null)
			return;
		int deltax = e.getX() - oldX;
		int deltay = e.getY() - oldY;
		if (selectedWidget != null) {
			checkWidgetCrossing(e.getX(), e.getY());
		} else if (selectedNodes != null) {
			// -- Move nodes
			for (Pair<Layer, Node> node : selectedNodes)
				node.getKey().moveNode(node.getValue(), deltax, deltay);
			// should we snap?
			updateSnap(selectedNodes);
			
		} else if (selectedBar != null && selectedLayer != null) {
			// -- Move layer
			if (draggedView == null)
				draggedView = detachLayer(selectedLayer, GUIUtils.isControlDown(e));
			draggedView.move(deltax, deltay);
			updateDropTarget();
		} else {
			// -- Pan view
			if (selectedLayer != null)
				selectedLayer.pan(deltax, deltay);
			else
				selectedView.pan(deltax, deltay);
		}
		oldX = e.getX();
		oldY = e.getY();
	}
	
	// -- Snapping code
	protected void updateSnap(ArrayList<Pair<Layer, Node>> snapSources) {
		final double pickRadius = 10;
		
		if (snapSources == null || snapSources.size() == 0) {
			this.snapSources = null;
			this.snapTargets = null;
			this.snapTargets = null;
			this.snapShapeColor = null;
			return;
		}
		
		// Compute snap source location by averaging active points
		Point2D.Double snapSourceLocation = getAverageNodeCenter(selectedView, snapSources);
		
		// Pick snap targets based on this point
		Rectangle2D.Double pickrect = new Rectangle2D.Double(snapSourceLocation.getX() - pickRadius/2.0, snapSourceLocation.getY() - pickRadius/2.0, pickRadius, pickRadius);
		ArrayList<Pair<Layer, Node>> picked = selectedView.pickNodeCenters(pickrect, false);
		ArrayList<Pair<Layer, Node>> snapTargets = new ArrayList<Pair<Layer, Node>>(); 
		for (Pair<Layer, Node> node : picked) { // keep only non-source nodes
			boolean isSource = false;
			for (Pair<Layer, Node> node2 : snapSources)
				isSource |= (node2.equals(node));
			if (!isSource)
				snapTargets.add(node);
		}

		// Compute avg shape and color
		if (snapTargets != null && snapTargets.size() > 0) {
			ArrayList<Pair<Layer, Node>> allNodesToSnap = new ArrayList<Pair<Layer, Node>>();
			allNodesToSnap.addAll(snapSources);
			allNodesToSnap.addAll(snapTargets);
			Pair<Area,Color> combined = combineNodeShapes(selectedView, allNodesToSnap, false, true);
			this.snapSources = snapSources;
			this.snapTargets = snapTargets;
			this.snapShape = combined.getKey();
			this.snapShapeColor = combined.getValue();
		} else {
			this.snapShape = null;
			this.snapSources = null;
			this.snapTargets = null;
			this.snapShapeColor = null;
		}
	}
	
	static Pair<Area, Color> combineNodeShapes(View view, ArrayList<Pair<Layer, Node>> nodes, boolean addRubberBandLinks, boolean globalCoordinates) {
		Area area = new Area();
		Color avgColor = null;
		Rectangle2D previousNodeBounds = null;
		Stroke rubberBandStroke = new BasicStroke(6); 
		for (int i=0; i<nodes.size(); i++) {
			Pair<Layer, Node> node2snap = nodes.get(i);
			Shape s = view.getShapeInView(node2snap.getKey(), node2snap.getValue(), 4);
			if (s != null) {
				if (globalCoordinates)
					s = AffineTransform.getTranslateInstance(view.bounds.getX(), view.bounds.getY()).createTransformedShape(s);
				area.add(new Area(s));
				if (avgColor == null)
					avgColor = node2snap.getKey().getBaseColor();
				else
					avgColor = GUIUtils.mix(avgColor, node2snap.getKey().getBaseColor(), (float)(Math.pow(0.5, i)));
				// add rubber-band-like links
				Rectangle2D currentNodeBounds = s.getBounds2D();
				if (previousNodeBounds != null && addRubberBandLinks) {
					Shape rubberBand = new Line2D.Double(previousNodeBounds.getCenterX(), previousNodeBounds.getCenterY(), currentNodeBounds.getCenterX(), currentNodeBounds.getCenterY());
					rubberBand = rubberBandStroke.createStrokedShape(rubberBand);
					try {
						area.add(new Area(rubberBand));
					} catch (Exception e) {
						
					}
				}
				previousNodeBounds = currentNodeBounds;
			}
		}
		if (avgColor != null)
			avgColor = GUIUtils.multiplyAlpha(avgColor, 0.2f);
		return new Pair<Area, Color>(area, avgColor);
	}
	
	// -- Snapping code
	protected void applySnap() {
		if (snapSources == null || snapTargets == null)
			return;
		ArrayList<Pair<Layer, Node>> allNodesToSnap = new ArrayList<Pair<Layer, Node>>();
		allNodesToSnap.addAll(snapSources);
		allNodesToSnap.addAll(snapTargets);
		Point2D.Double snapDestLocation = getAverageNodeCenter(selectedView, allNodesToSnap);
		for (Pair<Layer, Node> node2snap : allNodesToSnap) {
			selectedView.setPositionInView(node2snap.getKey(), node2snap.getValue(), snapDestLocation);
		}
		for (Layer l : selectedView.getLayers())
			l.animator.directCopyGraphToAnimatedFloats();
		this.snapShape = null;
		this.snapSources = null;
		this.snapTargets = null;
		this.snapShapeColor = null;
	}
	
	static Point2D.Double getAverageNodeCenter(View view, ArrayList<Pair<Layer, Node>> nodes) {
		Point2D.Double avg = new Point2D.Double(0, 0);
		for (Pair<Layer, Node> node : nodes) {
			Point2D p = view.getPositionInView(node.getKey(), node.getValue());
			p.setLocation(p.getX() + view.bounds.getX(), p.getY() + view.bounds.getX());
			avg.setLocation(avg.getX() + p.getX(), avg.getY() + p.getY());
		}
		avg.setLocation(avg.getX() / nodes.size(), avg.getY() / nodes.size());
		avg.setLocation(avg.getX() - view.bounds.getX(), avg.getY() - view.bounds.getY());
		return avg;
	}
	
	// -- Implements vertical / horizontal crossing on widgets
	protected void checkWidgetCrossing(int x, int y) {
		Pair<LayerBar, CheckBox> pickedw = null;
		int x0 = (int)selectedWidget.bounds.getCenterX() + selectedBar.bounds.x + selectedView.bounds.x;
		int y0 = (int)selectedWidget.bounds.getCenterY() + selectedBar.bounds.y + selectedView.bounds.y;
		if (selectedWidget.allowHorizontalCross && Math.abs(y - y0) < Math.abs(x - x0))
			// check for horizontal crossing
			pickedw = selectedView.pickWidget(x - selectedView.getBounds().x, y0 - selectedView.getBounds().y);
		else if (selectedWidget.allowVerticalCross && Math.abs(y - y0) > Math.abs(x - x0))
			// check for vertical crossing
			pickedw = selectedView.pickWidget(x0 - selectedView.getBounds().x, y - selectedView.getBounds().y);
		CheckBox newCrossedWidget = pickedw == null ? null : pickedw.getValue();
		if (newCrossedWidget != crossedWidget) {
			if (newCrossedWidget != null)
				newCrossedWidget.cross();
			selectedView.repaint(selectedView.layerBarsBounds);
			crossedWidget = newCrossedWidget;
		}
	}

	protected View detachLayer(Layer layer, boolean copy) {
		Rectangle lb = new Rectangle(layer.bar.getBounds());
		View v = layer.getParent();
		Rectangle vb = new Rectangle(v.getBounds());
		
		if (!copy) {
			v.removeLayer(layer);
		} else {
			layer = layer.copy();
		}
		
		View detached = new View();
		detached.setParent(this);
		detached.addLayer(layer);
		detached.setBounds(new Rectangle(vb.x + lb.x, vb.y + lb.y, vb.width, vb.height));
		detached.setTranslucent(true);
		repaint();
		return detached;
	}
	
	void updateDropTarget() {
		
		// Pick view
		View pv = null;
		Point viewCenter = null;
		if (draggedView != null) {
			viewCenter = new Point(
				(int)draggedView.getBounds().getCenterX(),
				(int)draggedView.getBounds().getCenterY());
			pv = pickView(viewCenter.x, viewCenter.y);
			
			if (pv == null && views.size() > 0) {
				// if nothing picked, take the closest between the first or the last view.
				View vfirst = views.get(0);
				View vlast = views.get(views.size() - 1);
				double dfirst = (vfirst.getBounds().getCenterX() - viewCenter.x) * (vfirst.getBounds().getCenterX() - viewCenter.x) + (vfirst.getBounds().getCenterY() - viewCenter.y) * (vfirst.getBounds().getCenterY() - viewCenter.y);
				double dlast = (vlast.getBounds().getCenterX() - viewCenter.x) * (vlast.getBounds().getCenterX() - viewCenter.x) + (vlast.getBounds().getCenterY() - viewCenter.y) * (vlast.getBounds().getCenterY() - viewCenter.y);
				if (dfirst < dlast)
					pv = vfirst;
				else
					pv = vlast;
			}
		}
			
		// Determine where we should insert the ghost
		int pl_index = -1;
		int pv_index = -1;
		if (pv != null) {
			Rectangle b = pv.getBounds();
			if (viewCenter.x < b.x + b.width / 4) {
				// insert left
				pv_index = views.indexOf(pv);
			} else if (viewCenter.x > b.x + b.width * 3 / 4) {
				// insert right
				pv_index = views.indexOf(pv) + 1;
			} else {
				// insert inside
				//				 Pair<Layer, LayerBar> picked = pv.pickBar(barCenter.x - pl.getBounds().x, barCenter.y - pl.getBounds().y);
				//				 if (picked != null) {
				//					 pl = picked.getKey();
				pl_index = pv.getLayers().size(); // FIXME
				//				 }
			}
		} else {
			// insert first (no view)
			pv_index = 0;
		}
		
		// Update drop target
		if (viewDropTarget != pv || layerDropIndex != pl_index || viewDropIndex != pv_index) {
			viewDropTarget = pv;
			layerDropIndex = pl_index;
			viewDropIndex = pv_index;
			if (draggedView != null) {
				if (layerDropIndex != -1) {
					// Drop inside view
					draggedView.getLayers().get(0).setBaseColor(viewDropTarget.getNextColor());
				} else if (viewDropIndex != -1) {
					// Drop between views
					draggedView.getLayers().get(0).setBaseColor(Colors.getPredefinedColor(0));
				}
			}
			repaint();
		}
	}
	
	public void mouseReleased(MouseEvent arg0) {
		
		// Drop?
		if (draggedView != null) {
			drop();
		}
		
		// Snap?
		if (snapSources != null && snapTargets != null) {
			applySnap();
		}
		
		selectedView = null;
		selectedLayer = null;
		selectedNodes = null;
		selectedBar = null;
		selectedWidget = null;
		crossedWidget = null;
		draggedView = null;
		viewDropTarget = null;
		layerDropIndex = -1;
		viewDropIndex = -1;
		snapShape = null;
		repaint();
	}
	
	void drop() {
		if (layerDropIndex == -1 && viewDropIndex == -1) {
			// cancel
			viewDropTarget = selectedView;
			layerDropIndex = selectedView.getLayers().size(); // FIXME
		}
		if (layerDropIndex != -1) {
			// Drop inside view
			Layer layer = draggedView.getLayers().get(0);
			draggedView.removeLayer(layer);
			viewDropTarget.addLayer(layer, layerDropIndex);
			removeEmptyViews();
		} else if (viewDropIndex != -1) {
			// Drop between views
			Layer layer = draggedView.getLayers().get(0);
			draggedView.removeLayer(layer);
			View v = new View();
			addView(v, viewDropIndex);
			v.addLayer(new MasterLayer());
			v.addLayer(layer);
			removeEmptyViews();
		}
		repaint();
	}
	
	Timer stopWheelZoomingTimer = null;
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!zoomingWithWheel) {
			zoomingWithWheel = true;
			repaint();
			if (stopWheelZoomingTimer != null)
				stopWheelZoomingTimer.restart();
			else {
				stopWheelZoomingTimer = new Timer(300, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						zoomingWithWheel = false;
						if (focusedView != null)
							focusedView.repaint();
						else
							repaint();
						stopWheelZoomingTimer = null;
					}
				});
				stopWheelZoomingTimer.setRepeats(false);
				stopWheelZoomingTimer.start();
			}
		}
		zoom(e.getPoint(), e.getWheelRotation(), e.getWheelRotation(), e);
	}
	
	public void zoom(Point center, double amount_x, double amount_y, InputEvent modifiers) {
		View hoveredView = pickView(center.x, center.y);
		if (hoveredView != null) {
			Point2D origin = new Point2D.Double(
				center.x - hoveredView.bounds.x,
				center.y - hoveredView.bounds.y);
			boolean noModifier = !modifiers.isShiftDown() && !GUIUtils.isControlDown(modifiers);
//			hoveredView.zoom(origin, amount, noModifier | modifiers.isControlDown(), noModifier | modifiers.isShiftDown());
			if (modifiers.isAltDown())
				amount_y = 0;
			hoveredView.zoom(origin, amount_x, amount_y, GUIUtils.isControlDown(modifiers), modifiers.isShiftDown(), !modifiers.isShiftDown());
		}
	}
	
//	public boolean isInteracting() {
//		return selectedNodes != null || draggedView != null || (selectedView != null && selectedWidget == null) || AnimationTimer.isRunning() || Layout.isLayoutRunning();
//	}
	
	public boolean isDraggingNode(View v) {
		return (selectedNodes != null && selectedView == v);
	}
	
	public boolean isXrayMode(View v) {
		return (xrayView == v);
	}
	
	public boolean isInteracting(Layer l) {
//System.err.println("Interacting: " + l.getTitle() + " animating " + l.animator.changed() + " computinglayout " + l.layout.isComputingLayout()
//	+ " zooming " + (l.parent == focusedView && (zoomingWithWheel || zoomingWithKeyboard > 0))
//	+ " ??? " + ((selectedView == l.parent && selectedWidget == null && selectedBar == null && selectedNodes == null) && !(selectedLayer != null && selectedLayer != l))
//	+ " moving node: ??" 
//	);
		if (l.animator.changed())
			return true; // animating
		if (l.layout != null && l.layout.isComputingLayout())
			return true; // computing layout
		if (l.parent == focusedView && (zoomingWithWheel || zoomingWithKeyboard > 0))
			return true; // zooming
		if (selectedView == l.parent && selectedWidget == null && selectedBar == null && selectedNodes == null) {
			if (selectedLayer != null && selectedLayer != l)
				return false; // panning
			return true; // ???
		}
		if (selectedNodes != null) {
			for (Pair<Layer, Node> n : selectedNodes)
				if (n.getKey() == l)
					return true; // moving node
		}
		return false;
	}
	
	View getDraggedView() {
		return draggedView;
	}

	void setDraggedView(View draggedView) {
		if (this.draggedView == draggedView)
			return;
		if (this.draggedView != null)
			this.draggedView.repaint();
		if (draggedView != null)
			draggedView.repaint();
		this.draggedView = draggedView;
		updateDropTarget();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressedOnce(KeyEvent e) {	
		int key = e.getKeyCode();

		// Zoom
		if (key == KeyEvent.VK_PAGE_UP  || key == KeyEvent.VK_PAGE_DOWN) {
			zoomingWithKeyboard ++;
			if (zoomingWithKeyboard > 0 && focusedView != null)
				focusedView.repaint();
		}
			
		// X-ray
		if (e.getKeyCode() == KeyEvent.VK_X && focusedView != null) {
			xrayView = focusedView;
			focusedView.invalidateBuffers();
			focusedView.repaint();
		}
		
		// Links
		if (e.getKeyCode() == KeyEvent.VK_L) {
			HIDE_LINKS = !HIDE_LINKS;
			for (View v: getViews()) {
				v.invalidateBuffers();
			}
			repaint();
		}
		
		// Links
		if (e.getKeyCode() == KeyEvent.VK_C) {
			HIDE_NONCLUSTER_EDGES = !HIDE_NONCLUSTER_EDGES;
			for (View v: getViews()) {
				for (Layer l : v.getLayers()) {
					Graph g = l.getGraph();
					boolean clusteredGraph = g.isClusteredGraph();
					for (Node n : g.getNodes()) {
						boolean visible = (!HIDE_NONCLUSTER_EDGES) || (!clusteredGraph) || n.isCluster();
						n.setVisible(visible);
					}
					for (Node n : g.getNodes()) {
						for (Edge ed : n.getNeighbour()) {
							boolean visible = (!HIDE_NONCLUSTER_EDGES) || (!clusteredGraph) || n.isVisible() || ed.getOtherNode(n).isVisible();
							ed.setVisible(visible);
						}
					}
				}
				v.invalidateBuffers();
			}
			repaint();
		}
		
		
		// Labels
		if (e.getKeyCode() == KeyEvent.VK_T) {
			HIDE_LABELS = !HIDE_LABELS;
			for (View v: getViews()) {
				for (Layer l : v.getLayers()) {
					Graph g = l.getGraph();
					if (g == null)
						continue;
					for (Node n : g.getNodes())
						n.invalidateGeometry();
				}
				v.invalidateBuffers();
			}
			repaint();
		}
	}

	@Override
	public void keyRepeated(KeyEvent e) {
		int key = e.getKeyCode();
		
		// Zoom
		if (key == KeyEvent.VK_PAGE_UP  || key == KeyEvent.VK_PAGE_DOWN) {
			if (focusedView != null) {
				Point center = new Point((int)focusedView.getBounds().getCenterX(), (int)focusedView.getBounds().getCenterY());
				double zoomfactor = key == KeyEvent.VK_PAGE_DOWN ? 0.5 : -0.5;
				zoom(center, zoomfactor, zoomfactor, e);
			}
		}	
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();

		// Zoom
		if (key == KeyEvent.VK_PAGE_UP  || key == KeyEvent.VK_PAGE_DOWN) {
			zoomingWithKeyboard --;
			if (zoomingWithKeyboard == 0 && focusedView != null)
				focusedView.repaint();
		}
		
		// X-ray
		if (e.getKeyCode() == KeyEvent.VK_X) {
			xrayView = null;
			if (focusedView != null) {
				focusedView.invalidateBuffers();
				focusedView.repaint();
			}
		}		
	}
	
	// TRUC CRADE TEMPORAIRE POUR L'ARTICLE
	public static boolean HIDE_LINKS = false;
	public static boolean HIDE_NONCLUSTER_EDGES = false;
	public static boolean HIDE_LABELS = false;
}
