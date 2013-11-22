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


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;

import donatien.gui.JViews;
import donatien.gui.Layer;
import donatien.util.FastFontMetrics;
import donatien.util.GUIUtils;
import donatien.util.Pair;


public class Node extends PropertyModifiable
{
	
	/**Identifiers of a node */
	private String id;
	
	/** Stamp of a node */
	private Stamp stamp = null;	//degree, number of edges, nb neighbours niveau 2, number of edges niveau 2, whatever
	
	/** List of adjacent edges*/
	Vector<Edge> edges = new Vector<Edge>();
	
	/** Contain a graph */
	private Graph content = new Graph();
	
	/** parent graph*/
	private Graph parent_graph = new Graph();
	private ArrayList<Cell> cells = new ArrayList<Cell>();
	
	double x, y;
	double ro, theta;
	
	public int index; // used for speeding up link drawing
	public boolean anchored = false; // used by the spring align algorithm
	ArrayList<Pair<Layer, Node>> slaves = new ArrayList<Pair<Layer, Node>>(); // used by master nodes
	public boolean matched = false; // used for hiding labels for unmatched nodes
	
	public Point2D.Double getStamp(int i){
		if (stamp != null && i < stamp.size()){
			return stamp.get(i);
		} 
		return null;
	}
	public Stamp getStamp(){
		return stamp;
	}
	
	public void deltaMove(double x, double y) {
		this.x += x;
		this.y += y;
	}

	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
		
	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * Construct a node without neighbours, with no dynamic attribute.
	 * An an identifier id.
	 * */
	public Node(String id){
		this.id = id;
		setGraphContent(new Graph());		
	}

	//attention ne copie pas correctement les noeuds dans graphes clusterisés
	
	public Node lightCopy(){
		Node n = new Node(this.id);
		n.putSystemProp("alias", this);
		this.putSystemProp("alias", n);
		n.putProp("name", this.getProp("name"));
		setGraphContent(new Graph());	
		return n;
	}
	
	/** 
	 * Return all incident edges. Warning ! A loop will appear only once.
	 * The degree is then the size of this returned list plus one if there exists a loop.
	 * @return All incident edges
	 */
	public Vector<Edge> getNeighbour()
	{
		if (edges ==null)return new Vector();
		return edges;
	}
	
	/** 
	 * @return All nodes
	 */
	public HashSet<Node> getVoisins(){
		HashSet<Node> hs = new HashSet<Node>();
		for (Edge e: getNeighbour()){
			hs.add(e.getOtherNode(this));
		}
		return hs;
	}
	
	/** 
	 * Useful for testing with contains
	 * @return true if equals, false otherwise
	 */
	@Override
	public boolean equals(Object o){
		if (!(o instanceof Node))
			return false;
		Node n = (Node)o;
		return (n.id.equals(this.id));
	}	

	/** 
	 * Computes signature e.g. d, e, ee, dd
	 * 	 * @return All incident edges
	 */
	public void printStamp(){
		System.out.println(getProp("name")+": ( "+stamp.get(0)+" , "+stamp.get(1)+" ) - ( "+stamp.get(2)+" , "+stamp.get(3)+" )");
	}
	
	public void updateCell(int i, Cell c){
		if (cells.size()>i){
			cells.set(i, c);
			System.out.println("update "+i+"-"+this.edges.size());
		}else{
			cells.add(i, c);
			System.out.println("add"+i+"-"+this.edges.size());
		}
	}
	
	
	public void ccc(int a, int b, Cell c){
		double padX = c.getPad('x');
		double padY = c.getPad('y');
		x = c.getX();
		y = c.getY();

		x += a * padX;
		y += b * padY;
	}
//	
//	public void ccc(int k, Cell c){
//		double padX = c.getPadX();
//		double padY = c.getPadY();
//		double a=0,b=0;
//		x = c.getX();
//		y = c.getY();
//
//		if (k<stamp.size()){
//			a = stamp.get(k).getX();
//			b = stamp.get(k).getY();
//		}else{
//			System.out.println("Warning : stamp shorter than you expect...");
//		}
//		x += a * padX;
//		y += b * padY;
//	}

	// CBC = Computes Barycentric Coordinates
	public void cbc(int k, Cell c){
		ArrayList<Point2D.Double> references = new ArrayList<Point2D.Double>() ;
		int n = stamp.size() ; // nb de points pour calcul du barycentre
		Point2D.Double P0,P1,P2,P3;
		double angle = 2*Math.PI/n;
		double rox =  c.getWidth()/2 -c.getMarginX();
		double roy =  c.getHeight()/2 -c.getMarginY();
		double x1,y1;
		System.out.println("Point :" + getProp("name"));

		if (k<stamp.size()){
			P0 = new Point2D.Double(0,0);
			P1 = new Point2D.Double(0,c.getHeight()-c.getMarginY());
			P2 = new Point2D.Double(0,c.getWidth()-c.getMarginX());
			P3 = new Point2D.Double(c.getWidth()-c.getMarginX(),c.getHeight()-c.getMarginY());
			
			//calculer les coordonnées barycentriques
			//en pensant à normaliser les stamp pour que points restent à l'intérieur de l'ellipse	
			x = c.getCenterX();
			y = c.getCenterY();

			double ci ;

			ci = 2 * stamp.get(0).getX()/c.getWidth();
			x += ci * P0.getX(); 
			y += ci * P0.getY();
			ci = 2 * stamp.get(0).getY()/c.getHeight();
			x += ci * P1.getX(); 
			y += ci * P1.getY();
			
			ci = 2 * stamp.get(1).getX()/c.getWidth();
			x += ci * P2.getX(); 
			y += ci * P2.getY();
			ci = 2 * stamp.get(1).getY()/c.getHeight();
			x += ci * P3.getX(); 
			y += ci * P3.getY();
			
//				x =  P.getX(); 
//				y =  P.getY();
				System.out.println("coordes :" + x+" "+y);
				System.out.println("ref :" + P0.x+" "+P0.y);
				System.out.println("ref :" + P1.x+" "+P1.y);
				System.out.println("ref :" + P2.x+" "+P2.y);
				System.out.println("ref :" + P3.x+" "+P3.y);
			}
		}

	// CBC = Computes Barycentric Coordinates
	//reprendre ici pour étendre le calcul de cbc
	public void cbc_bugge(int k, Cell c){
		ArrayList<Point2D.Double> references = new ArrayList<Point2D.Double>() ;
		int n = stamp.size() ; // nb de points pour calcul du barycentre
		double angle = 2*Math.PI/n;
		double rox =  c.getWidth()/2 -c.getMarginX();
		double roy =  c.getHeight()/2 -c.getMarginY();
		double x1,y1;
		System.out.println("Point :" + getProp("name"));

		if (k<stamp.size()){
			for (int i = 0; i<stamp.size();i++){
				//initialiser les barycentres sur une ellipse de rayon max
				x1 = rox * Math.cos(angle) + c.getCenterX();
				y1 = roy * Math.sin(angle) + c.getCenterY();
				angle+= angle;
				references.add(new Point2D.Double(x1,y1));
			}
			//calculer les coordonnées barycentriques
			//en pensant à normaliser les stamp pour que points restent à l'intérieur de l'ellipse	
			x = 0;
			y = 0;
			double inertie = 1/ (double) n;
			for (Point2D.Double P:references){
				//nb on prend la somme des stampx/stampy, c'est arbitraire, plein d'autres possibles à explorer: min, max, etc voire doubler les reférences pour discriminer mieux en mettant x et y pour chacune
//				x += (stamp.get(k).getX()+stamp.get(k).getY())/c.getMaxValue().getX() * P.getX(); 
//				y += (stamp.get(k).getX()+stamp.get(k).getY())/c.getMaxValue().getY() * P.getY();
				inertie = stamp.get(k).getX();
				x += inertie * P.getX(); 
				y += inertie * P.getY();
//				x =  P.getX(); 
//				y =  P.getY();
				System.out.println("coordes :" + x+" "+y);
				System.out.println("ref :" + P.x+" "+P.y);
			}
		}
	}
	
	// CPC = ComputesPolarCoordinates ou aussi
	// Polar dans une Cellule, hi hi hi !!	
	public void cpc(int k, Cell c){
		double x1, y1, rox, roy;
		if (k < stamp.size()){
			rox = stamp.get(k).getX() * c.getRadius().getX();
			roy = stamp.get(k).getY() * c.getRadius().getY();
			theta = stamp.get(k).getY()  * c.getAngle();
			x1 = rox * Math.cos(theta);
			y1 = roy * Math.sin(theta);
			x = c.getCenterX() + x1;
			y = c.getCenterY() + y1;
		}
		//			System.out.println("Coords "+ getProp("name")+ " : " + x);
	}
	
	
	// deuxième version de Polar utile quand on veut prendre les rangs au lieu des valeurs a et b sont les rang pour x et pour y respectivement
	public void cpc(double a, double b, Cell c){
		double x1, y1, rox, roy;
		rox = a * c.getRadius().getX();
		roy = a * c.getRadius().getY();
		theta = b * c.getAngle();
		x1 = rox * Math.cos(theta);
		y1 = roy * Math.sin(theta);
		x = c.getCenterX() + x1;
		y = c.getCenterY() + y1;
		//			System.out.println("Coords "+ getProp("name")+ " : " + x);
	}


	
	public int getSizeStamp(){
		return stamp.size();
	}
	/** 
	 * Computes signature e.g. d, e, ee, dd
	 * 	 * @return All incident edges
	 */
	
	public Stamp computeStamp(){
		if (stamp != null)
			return stamp;
		SubGraph g1 = new SubGraph(this, 1);
		g1.print();
		SubGraph g2 = new SubGraph(this, 2);
		g2.print();
		stamp= new Stamp();
		stamp.add(0, new Point2D.Double((double) g1.getNodeCount(), (double) g1.getEdgeCount())); 	// degré et le nb d'arête voisinage direct ~ coefficient clustering
		stamp.add(1, new Point2D.Double((double) g2.getNodeCount(),(double) g2.getEdgeCount())); 	// le nb de noeuds /edges (distance 2) ~ coefficient clustering
		return stamp;
	}
	
	
	/** 
	 * Return all incident edges. Warning ! A loop will appear only once.
	 * The degree is then the size of this returned list plus one if there exists a loop.
	 * @return All incident edges
	 */
	public int getNeighbourCount()
	{
		return edges.size();
	}
	
	/**
	 * Return the graph contained in the node
	 * @return The graph contained in the node
	 */
	public Graph getGraphContent()
	{
		return content;
	}
	
	/**
	 * Change the graph contained in the node
	 * 
	 * @param g
	 */
	public void setGraphContent(Graph g)
	{
		this.content.setParentNode(null);
		this.content = g;
		this.content.setParentNode(this);
	}
	
	/**
	 * 
	 */
	public void setParent(Graph g)
	{
		this.parent_graph = g;
	}
	
	/**
	 * @return graphe
	 */
	public Graph getParent()
	{
		return parent_graph;
	}

	/**
	 * @return Return a representative string.
	 */
	public String toString()
	{
		return "Node "+id;
	}

	/**
	 * Return the identifier of this node
	 * @return the identifier of this node
	 */
	public String getId() {
		
		return id;
	}

	
	public boolean isCluster() {
		String c = getProp("cluster");
		return (c != null && c.equals("y"));
	}
	
	
	// ============ Paint code ============
	
	Shape shape = null;
	Rectangle2D shapeBounds = null;
	String label = null;
	String type = null;
	boolean initialized = false;
	Font font;
	public boolean visible = true; 
	static Rectangle2D.Double tmpRect = new Rectangle2D.Double();
	
	public void invalidateGeometry() {
		initialized = false;
	}
	
	/**
	 * Initialize the node's geometry and internal layout
	 * @param g
	 */
	public void init(Graphics2D g) {
		String cluster = getProp("cluster");
		if (cluster != null && cluster.equals("y")){
			Ellipse2D.Double ellipse = new Ellipse2D.Double(-15, -15, 30, 30);
			shape = ellipse;

			shapeBounds = ellipse.getBounds2D();
			initialized = true;
			font = RenderingAttributes.labelFont;
			font = new Font(font.getName(), Font.BOLD, font.getSize());
			label = JViews.HIDE_LABELS ? null : getProp("name");//  + getProp("genre");
			
		} else {
			type = getProp("genre");
			double margin_x = 2, margin_y = 4, round = 10;
			if (type != null) { // =========== FIXME
				if (type.equals("keyword") || type.equals("keyword_t"))
					margin_y = 0;
				else if (type.equals("collection"))
					margin_y = 8;
			}
			
			if (isMasterNode()) {
				margin_x += 2;
				margin_y += 2;
			}
							
			boolean hideLabel = JViews.HIDE_LABELS && !matched;
			label = hideLabel ? null : getProp("name");//  + getProp("genre");
			if (label != null) {
				final int maxCharacters = 30;
				final int maxCharacters2 = 40;
				if (label.length() > maxCharacters) {
					String label2 = label.substring(0, Math.min(label.length(), maxCharacters2));
					if (label2.length() < label.length())
						label = label2 + "...";
					font = RenderingAttributes.labelFontNarrow;
				} else
					font = RenderingAttributes.labelFont;
			}
			
			if (type != null) { // =========== FIXME
				if (type.equals("paper"))
					font = new Font(font.getName(), Font.BOLD, font.getSize());
			}
			
			g.setFont(font);
			Rectangle2D b = FastFontMetrics.getBounds(g, label);
			double shape_h = b.getHeight() + margin_y * 2;
			double shape_w = Math.max(shape_h, b.getWidth() + margin_x * 2);
			RoundRectangle2D.Double rec = new RoundRectangle2D.Double(-shape_w / 2, -shape_h / 2, shape_w, shape_h, round, round);
			shape = rec;
			shapeBounds = rec.getBounds2D();
			initialized = true;
		}
	}
	
	/**
	 * Paint the node.
	 * @param g
	 */
	public void paint(Graphics2D g, double x, double y, RenderingAttributes att) {
		if (!visible)
			return;
		if (!initialized)
			init(g);
		AffineTransform at0 = g.getTransform();
		g.translate(x, y);
		
		// -- Node background
		if (!att.xrayMode) {
			g.setColor(att.nodeFillColor);
			if (type != null) { // =========== FIXME
				if (type.equals("keyword") || type.equals("keyword_t"))
					g.setColor(GUIUtils.mix(att.nodeFillColor, Color.white, 0.8f));
				else if (type.equals("collection"))
					g.setColor(GUIUtils.mix(att.nodeFillColor, att.nodeBorderColor, 0.1f));
			}
			g.fill(shape);
		}
		
		// -- Node outline
		g.setStroke(att.nodeStroke);
		if (type != null) { // =========== FIXME
			if (type.equals("paper") && !att.xrayMode)
				g.setStroke(new BasicStroke(2));
		}
		g.setColor(att.nodeBorderColor);
		g.draw(shape);
		
		// -- Node label
		if (!att.xrayMode) {
			if (label != null) {
				//				g.setFont(att.labelFont);
				g.setColor(att.labelColor);
				//g.drawString(label, (int)label_x, (int)label_y);
				g.setFont(font);
				FastFontMetrics.drawCenteredString(g, label, 0, 0, 0.5, 0.55);
			}
		}
		
		// -- Node center (in x-ray mode)
		if (att.xrayMode) {
			g.setColor(Color.white);
			double r = 2.5 / att.scaleFactor;
			tmpRect.setRect(-r, -r, r*2, r*2);
			g.fill(tmpRect);
			g.setColor(att.nodeBorderColor);
			r = 1.5 / att.scaleFactor;
			tmpRect.setRect(-r, -r, r*2, r*2);
			g.fill(tmpRect);
		}
		
		g.setTransform(at0);
	}
	
	public void paint(Graphics2D g, RenderingAttributes att) {
		paint(g, this.x, this.y, att);
	}
	
	public void paintShadow(Graphics2D g, RenderingAttributes att) {
		paintShadow(g, this.x, this.y, att);
	}

	public void paintShadow(Graphics2D g, double x, double y, RenderingAttributes att) {
		if (!visible)
			return;
		if (!initialized)
			init(g);
		AffineTransform at0 = g.getTransform();
		g.translate(x+3, y+3);
//		g.setColor(shadowColor);
		g.fill(shape);
		g.setTransform(at0);
	}
	
	public boolean intersects(Rectangle2D rec) {
		if (shapeBounds == null)
			return true;
		return (x + shapeBounds.getX() < rec.getMaxX() && x + shapeBounds.getMaxX() > rec.getX()
				&& y + shapeBounds.getY() < rec.getMaxY() && y + shapeBounds.getMaxY() > rec.getY());
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	// ============ Interaction code =============

	/**
	 * Picking
	 */
	public boolean contains(double x, double y) {
		if (shape == null)
			return false;
		return shape.contains(x - this.x, y - this.y);
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public boolean isMasterNode() {
		return slaves.size() > 0;
	}

	public ArrayList<Pair<Layer, Node>> getSlaves() {
		return slaves;
	}

}
