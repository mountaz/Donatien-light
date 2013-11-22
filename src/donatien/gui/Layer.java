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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ListIterator;

import donatien.layout.CopyLayout;
import donatien.layout.Layout;
import donatien.layout.LayoutAnimator;
import donatien.layout.MasterAlignLayout;
import donatien.model.graph.Graph;
import donatien.model.graph.Node;
import donatien.model.graph.RenderingAttributes;
import donatien.util.GUIUtils;
import fr.aviz.animation.AnimatedFloat;
import fr.aviz.animation.AnimationListener;
import fr.aviz.animation.AnimationTimer;

/**
 * Contains a graph.
 * 
 * @author dragice
 *
 */
public class Layer implements AnimationListener {

	private boolean bufferNeedsRedraw = true;
	
	View parent;
	private Graph graph;
	private Grid grid;
	Layout layout = null; // current layout
	LayoutAnimator animator = new LayoutAnimator();
	AnimatedFloat visible = new AnimatedFloat(1, 0f, 0.12f);
	boolean enabled = true;

	double scaleFactor_x = 1, scaleFactor_y = 1;
	private Rectangle bounds; // component's bounds as seen by its parent
	double x, y; // graph translation within the view
	
	private int margin = 40; // internal margin for graph layout, in pixels
	boolean layoutNeverComputed = true;
	
	LayerBar bar;
	Color baseColor = Color.black;
	float opacity = 1;
	float previousLinkOpacity = 1;
	
	BufferedImage buffer = null;
		
	public Layer() {
		bar = new LayerBar(this);
		AnimationTimer.addListener(this);
	}
	
	public Layer copy() {
		Layer l = new Layer();
		l.setTitle(getTitle());
		l.setGraph(null); // graph not loaded yet
		l.setLayout(bar.currentLayoutName, new CopyLayout(graph, layout.getGrid()));
		l.animator.updateLinkOpacity(getCurrentLinkOpacity());
		l.x = x;
		l.y = y;
		l.scaleFactor_x = scaleFactor_x;
		l.scaleFactor_y = scaleFactor_y;
		GraphLoader.loadGraph(graph.getName(), graph.getProp("filename"), l);
		return l;
	}
	
	public void setGraph(Graph g) {
		this.graph = g;
		// FIXME: update everything.
		
		if (g == null)
			return;
		setTitle(g.getName() + "  (" + g.getNodes().size() + " nodes)");
		animator.setGraph(g);
		layoutNeverComputed = true;
	}
	
	public Graph getGraph() {
		return graph;
	}
	
	public void setDefaultLayout() {
		setLayout(AllLayouts.getDefaultLayout(), AllLayouts.allLayouts.get(AllLayouts.getDefaultLayout()).clone());
	}
	
	public Layer getLayerAbove(boolean onlyIfVisible) {
		if (parent == null)
			return null;
		return parent.getLayerAbove(this, onlyIfVisible);
	}
	
	protected float getCurrentLinkOpacity() {
		return animator.directGetLinkOpacity();
	}
	
	public Layout getLayout() {
		return layout;
	}
		
	public void setLayout(String name, Layout layout) {
		if (this.layout == layout)
			return;
					
		float previousLinkOpacity = getCurrentLinkOpacity();

		if (this.layout != null) {
			this.layout.stopLayoutAsync();
			bar.updateLayoutWidgets();
			this.layout.stopLayout();
//			this.layout.removeListener(this);
		}

		if (layout != null) {
//			layout.addListener(this);
			bar.updateLayoutWidgets();
		}
		
		this.layout = layout;
		this.grid = layout == null ? null : layout.getGrid();
		bar.currentLayoutName = name;
		animator.setLayout(layout);
		
		float aboveLinkOpacity = 1;
		Layer l = getLayerAbove(true);
		if (l != null)
			aboveLinkOpacity = l.getCurrentLinkOpacity();
		animator.updateLinkOpacity(previousLinkOpacity, aboveLinkOpacity);
				
		
		// FIXME
		if (layout != null && layout instanceof MasterAlignLayout) {
			MasterAlignLayout alignLayout = (MasterAlignLayout)layout;
			alignLayout.configureLayout(this);
		}
		
		if (getBounds() != null && !getBounds().isEmpty()) {
			parent.updateLayoutWidgets();
			computeLayout();		
		}
	}
	
	
	@Override
	public void animateEvent() {
		if (animator.changed() || visible.changed())
			repaint();
		if (!AnimationTimer.isRunning()) {
			repaint(); // animation stopped. redraw with high quality.
		}
	}
	
	public Rectangle2D getGraphBounds() {
		double margin = this.margin;// * scaleFactor;
		Rectangle2D b = new Rectangle2D.Double(margin, margin, bounds.getWidth() - margin*2, bounds.getHeight() - margin*2);
		return getInverseTransform().createTransformedShape(b).getBounds2D();
	}
	
	public void computeLayout() {
		if (layout == null || graph == null || getBounds().isEmpty())
			return;
		
		layout.stopLayout();
		layout.setGraph(graph);
		layout.setBounds(getGraphBounds());
		
		if (layoutNeverComputed) {
			layout.randomLayout(); // avoid starting with all points confounded
			layoutNeverComputed = false;
		}
		
		layout.startLayout();
	}
	
	/**
	 * Picks a node, with x, y specified in component's coordinates.
	 * 
	 * @param x
	 * @param y
	 * @param pickSize
	 * @return
	 */
	public ArrayList<Node> pickNodes(double x, double y) {
		
		if (graph == null)
			return null;
		
		if (visible.get() == 0 || opacity == 0)
			return null;
		
		 ArrayList<Node> nodes = new  ArrayList<Node>();
		
		// -- Create local pick point
		
		AffineTransform componentToLocal = getInverseTransform();
		Point2D localPickPoint = componentToLocal.transform(new Point2D.Double(x, y), null);
		
		// -- Pick
		
		int l = graph.getNodes().size();
		Node ng;
		//parcours du dernier au premier (ie avant plan vers arrière plan)
		for (ListIterator i = graph.getNodes().listIterator(l); i.hasPrevious();) {
			ng = (Node) i.previous();
			if (ng.contains(localPickPoint.getX(), localPickPoint.getY()))
				nodes.add(ng);
		}	
		return nodes;
	}
	
	/**
	 * Picks a node, with x, y specified in component's coordinates.
	 * 
	 * @param x
	 * @param y
	 * @param pickSize
	 * @return
	 */
	public ArrayList<Node> pickNodeCenters(Rectangle2D rec) {
		
		if (graph == null)
			return null;
		
		if (visible.get() == 0 || opacity == 0)
			return null;
		
		 ArrayList<Node> nodes = new  ArrayList<Node>();
		
		// -- Create local pick rectangle
		
		AffineTransform componentToLocal = getInverseTransform();
		Rectangle2D localPickRectangle = componentToLocal.createTransformedShape(rec).getBounds2D();
		
		// -- Pick
		
		int l = graph.getNodes().size();
		Node ng;
		//parcours du dernier au premier (ie avant plan vers arrière plan)
		for (ListIterator i = graph.getNodes().listIterator(l); i.hasPrevious();) {
			ng = (Node) i.previous();
			if (localPickRectangle.contains(ng.getX(), ng.getY()))
				nodes.add(ng);
		}	
		return nodes;
	}
	
	public Point2D getPositionInLayer(Node n) {
		Point2D p = new Point2D.Double(n.getX(), n.getY());
		getTransform().transform(p, p);
		return p;
	}
	
	public void setPositionInLayer(Node n, Point2D p) {
		getInverseTransform().transform(p, p);
		n.setX(p.getX());
		n.setY(p.getY());
		invalidateBuffer();
	}
	
	public Shape getShapeInLayer(Node n, double grow) {
		if (n.getShape() == null)
			return null;
		AffineTransform at = getTransform();
		Rectangle2D b = n.getShape().getBounds2D();
		at.concatenate(AffineTransform.getTranslateInstance(n.getX(), n.getY()));
		if (grow > 0) {
			final double margin = grow / scaleFactor_x;
			at.concatenate(AffineTransform.getScaleInstance(1 + margin / b.getWidth(), 1 + margin / b.getHeight()));
		}
		return at.createTransformedShape(n.getShape());
	}
	
	private void rebuildBuffer() {
		if (buffer == null || buffer.getWidth() < bounds.width || buffer.getHeight() < bounds.height) {
			buffer = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
		}
	}
	
	private void clearBuffer() {
		if (buffer == null)
			rebuildBuffer();
		Graphics2D g = (Graphics2D)buffer.getGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
		Rectangle2D.Double rect = new Rectangle2D.Double(0,0,bounds.getWidth(),bounds.getHeight()); 
		g.fill(rect);
	}
	
	public void paint(Graphics2D g2) {
						
		float opacity = this.opacity * (float)(visible.get());
		
		if (visible.get() == 0 || opacity == 0)
			return;
		
		Rectangle clip = new Rectangle(0, 0, bounds.width, bounds.height);
		Shape clip0 = g2.getClip();
		
		if (!clip0.getBounds().intersects(clip))
			return;
		
		boolean useBuffer = isHighQuality();

		if (useBuffer) {
			if (bufferNeedsRedraw) {
				// Draw inside buffer
				rebuildBuffer();
				clearBuffer();
				Graphics2D gb = (Graphics2D)buffer.getGraphics();
				gb.setClip(clip);
				paintContent(gb, true);
				bufferNeedsRedraw = false;
			}
			// draw buffer on the screen
			Composite c0 = g2.getComposite();
			if (opacity < 1)
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
			g2.clip(clip);
			g2.drawImage(buffer, 0, 0, null);
			g2.setClip(clip0);
			g2.setComposite(c0);

		} else {
			// Draw on screen
			clearBuffer();
			g2.clip(clip);
			paintContent(g2, isHighQuality());
			g2.setClip(clip0);
			invalidateBuffer();
		}
		
	}
	
	public void paintContent(Graphics2D g2, boolean highQuality) {
		
		if (!enabled)
			return;

		if (graph == null) {
			g2.setColor(GUIUtils.mix(baseColor, Color.black, 0.5f));
			int w = 100;
			g2.drawString("Loading graph...", (bounds.width - w)/2, bounds.height / 2);
			return;
		}
		
		AffineTransform at0 = g2.getTransform();
		g2.transform(getTransform());
		float linkOpacity = 1;
		if (animator != null)
			linkOpacity = animator.getLinkOpacity();
		boolean xrayMode = parent.parent.isXrayMode(parent);
		RenderingAttributes att = new RenderingAttributes(baseColor, opacity, linkOpacity, highQuality, xrayMode, scaleFactor_x);

		// Paint grid
		if (grid != null)
			grid.paint(g2, att);
		
		// Paint graph
		graph.paint(g2, animator, att);
		
		g2.setTransform(at0);
	}
	
	public void repaint() {
		//bufferNeedsRedraw = true;
		if (parent != null)
			parent.repaint();
	}
	
	public AffineTransform getTransform() {
		AffineTransform at = new AffineTransform();
		at.scale(scaleFactor_x, scaleFactor_y);
		at.translate(x, y);
		return at;
	}
	
	public AffineTransform getInverseTransform() {
		try {
			return getTransform().createInverse();
		} catch (NoninvertibleTransformException e) {
			return null;
		}
	}
	
	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		// correct pan
		if (this.bounds != null && !this.bounds.isEmpty() && !bounds.isEmpty()) {
			pan((bounds.width - this.bounds.width)/2.0, (bounds.height - this.bounds.height)/2.0);
		}
		this.bounds = bounds;
	}
	
	public void zoom(Point2D origin, double amount_x, double amount_y, boolean growObjects, boolean scatterObjects) {
		final double zoomSpeed = 1.1;
		double scaleMultiplier_x = 1 / Math.pow(zoomSpeed, amount_x);
		double scaleMultiplier_y = 1 / Math.pow(zoomSpeed, amount_y);
		
		if (growObjects) {
			// zoom
			scaleFactor_x *= scaleMultiplier_x;
			scaleFactor_y *= scaleMultiplier_y;
			
			// Correct translation
			double deltax = (1 - scaleMultiplier_x) * (origin.getX());
			double deltay = (1 - scaleMultiplier_y) * (origin.getY());
			pan(deltax, deltay);
		}
		
		// Correct for scenegraph expansion
		if (layout == null || !layout.isComputingLayout()) {
			if (growObjects && !scatterObjects) {
				Point2D origin_local = getInverseTransform().transform(origin, null);
				expand(origin_local, 1/scaleMultiplier_x, 1/scaleMultiplier_y);
			} else if (scatterObjects & !growObjects) {
				Point2D origin_local = getInverseTransform().transform(origin, null);
				expand(origin_local, scaleMultiplier_x, scaleMultiplier_y);
			}
		}
		
		invalidateBuffer();

		repaint();
	}
	
	public void expand(Point2D origin, double scale_x, double scale_y) {
		if (graph == null)
			return;
		for (Node n : graph.getNodes()) {
			n.setX((n.getX() - origin.getX()) * scale_x + origin.getX());
			n.setY((n.getY() - origin.getY()) * scale_y + origin.getY());
		}
		if (grid != null)
			grid.expand(origin, scale_x, scale_y);
		animator.directCopyGraphToAnimatedFloats();
	}
	
	public void moveNode(Node node, double deltax, double deltay) {
		if (scaleFactor_x !=0 && scaleFactor_y != 0){
			deltax /= scaleFactor_x;
			deltay /= scaleFactor_y;
		}
		node.deltaMove(deltax, deltay);
		animator.directCopyToAnimatedFloats(node.index);
		repaint();
	}
	
	public void pan(double deltax, double deltay) {
		if (scaleFactor_x !=0 && scaleFactor_y != 0){
			deltax /= scaleFactor_x;
			deltay /= scaleFactor_y;
		}
		x += deltax;
		y += deltay;
		invalidateBuffer();
		repaint();
	}

	public View getParent() {
		return parent;
	}

	public void setParent(View parent) {
		this.parent = parent;
	}

	public String getTitle() {
		return bar.getTitle();
	}

	public void setTitle(String title) {
		bar.setTitle(title);
	}

	public LayerBar getBar() {
		return bar;
	}

	public Color getBaseColor() {
		return baseColor;
	}

	public void setBaseColor(Color baseColor) {
		this.baseColor = baseColor;
		bar.setBaseColor(baseColor);
	}

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public boolean isHighQuality() {
		if (graph == null)
			return true;
		if (!parent.parent.isInteracting(this))
			return true;
		return false;
	}

	public boolean isVisible() {
		return visible.directGet() > 0;
	}

	public void setVisible(boolean visible) {
		double v = visible ? 1 : 0;
		if (this.visible.get() == v)
			return;
		if (bufferNeedsRedraw)
			this.visible.directSet(v);
		else
			this.visible.set(v);
		if (parent != null)
			parent.updateColors();
		repaint();
	}
	
	void invalidateBuffer() {
		bufferNeedsRedraw = true;
		parent.invalidateAlignmentData();
	}
	
	boolean isNewMasterLayer() {
		return bar.isNewMasterBar();
	}
}
