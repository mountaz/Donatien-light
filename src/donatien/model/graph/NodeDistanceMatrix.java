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
package donatien.model.graph;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import donatien.gui.Layer;

/**
 * Builds a distance matrix between nodes in two different graphs based on the
 * Euclidian distances between the nodes after layout. Distances are computed on
 * normalized coordinates (between 0 and 1) based on the graphs' visual bounds.
 * Nodes which are perfectly aligned have a distance of zero.
 * 
 * In accordance with the matrix representation, the first graph is called the column
 * graph, the second one is the row graph.
 * 
 * @author dragice
 *
 */
public class NodeDistanceMatrix extends DistanceMatrix<Node> {

	Rectangle2D columnGraphBounds, rowGraphBounds;
	Graph columnGraph, rowGraph;
	
	static Point2D tmpPoint1 = new Point2D.Double();
	static Point2D tmpPoint2 = new Point2D.Double();
	
	protected NodeDistanceMatrix(Graph columnGraph, Graph rowGraph) {
		super(columnGraph.getNodes(), rowGraph.getNodes());
		this.columnGraph = columnGraph;
		this.columnGraphBounds = null;
		this.rowGraph = rowGraph;
		this.rowGraphBounds = null;		
	}
	
	public NodeDistanceMatrix(Layer columnGraphView, Layer rowGraphView) {
		this(columnGraphView.getGraph(), columnGraphView.getGraphBounds(), rowGraphView.getGraph(), rowGraphView.getGraphBounds());
	}
	
	/**
	 * 
	 * @param g1
	 * @param bounds1 used to align the graph layouts
	 * @param g2 
	 * @param bounds2 used to align the graph layouts
	 */
	public NodeDistanceMatrix(Graph columnGraph, Rectangle2D columnGraphBounds, Graph rowGraph, Rectangle2D rowGraphBounds) {
		super(columnGraph.getNodes(), rowGraph.getNodes());
		this.columnGraph = columnGraph;
		this.columnGraphBounds = columnGraphBounds;
		this.rowGraph = rowGraph;
		this.rowGraphBounds = rowGraphBounds;
		setForceSymmetry(false);
		updateDistances();
	}
	
	public void updateDistances() {
		Node nc, nr;
		for (int c=0; c<numColumns(); c++) {
			nc = columns.get(c);
			for (int r=0; r<numRows(); r++) {
				nr = rows.get(r);
				setDistance(c, r, computeDistance(nc, columnGraphBounds, nr, rowGraphBounds));
			}
		}
		// normalize again because the graph's visual bounds might not match the actual graph bounds
		normalize();
	}
	
	protected static double computeDistance(Node n1, Rectangle2D bounds1, Node n2, Rectangle2D bounds2) {
		tmpPoint1.setLocation(n1.getX(), n1.getY());
		normalize(tmpPoint1, bounds1);
		tmpPoint2.setLocation(n2.getX(), n2.getY());
		normalize(tmpPoint2, bounds2);
		double euclidianDistance = tmpPoint1.distance(tmpPoint2);
		return euclidianDistance;
	}
	
	protected static void normalize(Point2D p, Rectangle2D bounds) {
		p.setLocation(
			(p.getX() - bounds.getX()) / bounds.getWidth(),
			(p.getY() - bounds.getY()) / bounds.getHeight()); 
	}

	public Graph getColumnGraph() {
		return columnGraph;
	}

	public Graph getRowGraph() {
		return rowGraph;
	}	

}
