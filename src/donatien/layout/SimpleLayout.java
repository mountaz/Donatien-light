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
package donatien.layout;

import java.util.ArrayList;
import java.util.Collections;

import donatien.gui.Grid;
import donatien.gui.RectangularGrid;
import donatien.model.graph.Cell;
import donatien.model.graph.Graph;
import donatien.model.graph.Node;
import donatien.model.graph.Stamp;

public class SimpleLayout extends Layout {
	double oX, oY,  wX, wY;
	int nbX, nbY, stepNum, maxStep = 3; //maxstep devra être remplacé par l'ordre de la signature ie le nb de dimensions prises en compte dans la signature
	Graph g;
	ArrayList<Double> x = new ArrayList<Double>();
	ArrayList<Double> y = new ArrayList<Double>();
	RectangularGrid grid = new RectangularGrid();

	// Hyperbolic rendering options
	boolean hyperbolicTransformEnabled = true;
	final double hyperbolic_magnification_x = 0.15;
	final double hyperbolic_magnification_y = 0.05;
	
	// Global state for init() and step()
	Cell mainCell;
	double maxX, maxY;
	int nextNodeToProcess = 0;

    //attention init est appelée bien plus souvent qu'à son tour, pour une raison qui restera mystérieusement mystérieuse...
	//c'est facheux car certains calculs couteux ne devraient pas s'effectuer si souvent et les tests scabreux pour leur éviter de se multiplier inutilement sont bien des tests scabreux
	protected void init() {
//		System.out.println("");
//		System.out.println("========================== init start "+stepNum + " " + getBounds() + " " + getGraph());		
		g = getGraph();
		
		// The following computations are only required when not in hyperbolic mode.
		// In hyperbolic mode, the reference frame is independent from min and max values
		// so we can lazily compute stamps in the step() method, node per node.
		if (!hyperbolicTransformEnabled) {
			// Compute all stamps in advance
			// NB: on n'aurait pas besoin de ce test SI init cote structure n'était pas le même que l'init coté graphique mais, pour cela, hélas, on peut attendre la version suivante dont la date de sorite n'est pas imminente :-(
			// Now the node knows if its stamp has been already computed, so no test is required.
			for (Node n : g.getNodes())
				n.computeStamp();
			// Compute min and max values
			updateMinMax();
		}

		// Create main cell
		mainCell = new Cell(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), (int) maxX, (int) maxY);
		
		// Create grid
		createGrid();

		// Now node positions are updated in step(), one by one. This allows to see the
		// progress of the computation on large graphs.
		// We start with the nodes that are displayed on top.
		nextNodeToProcess = g.getNodes().size() - 1;
	}
	
	@Override
	protected boolean step() {
		
		if (nextNodeToProcess < 0)
			return true; // finished
		
		Node n = g.getNodes().get(nextNodeToProcess);
		n.computeStamp(); // if already done, node won't do it twice
		ccc(n, 0, mainCell);
		nextNodeToProcess--;
		return false;
	}
	
	public void ccc(Node n, int k, Cell c){
		double padX = c.getPadX();
		double padY = c.getPadY();
		double a=0,b=0;
		double x = c.getX();
		double y = c.getY();
		Stamp stamp = n.getStamp();

		if (k<stamp.size()){
			a = stamp.get(k).getX();
			b = stamp.get(k).getY();
		}else{
			System.out.println("Warning : stamp shorter than you expect...");
		}
		double dx = a * padX;
		double dy = b * padY;
		
		if (hyperbolicTransformEnabled) {
			dx = hyperbolicTransform(a, hyperbolic_magnification_x, c.getWidth());
			dy = hyperbolicTransform(b, hyperbolic_magnification_y, c.getHeight());
		}
		
		x += dx;
		y += dy;
		
		n.setX(x);
		n.setY(y);
	}
	
	/**
	 * Takes a x-value between 0 and infinite and returns a value between 0 and maxx.
	 * k is the hyperbolic magnification factor and gives the shape of the hyperbolic function.
	 */
	private static double hyperbolicTransform(double x, double k, double maxx) {
		return maxx * (1 - (1 / (1 + x*k)));
	}
	
	protected void updateMinMax() {
		int k = 0;
		double newX, newY;
		for (Node n : g.getNodes()){
			if (k < n.getSizeStamp() ){
				newX = n.getStamp(k).getX();
				newY = n.getStamp(k).getY();
				if (!x.contains(newX)){x.add(newX);}
				if (!y.contains(newY)){y.add(newY);}
			}else return;
//			Collections.sort(x);
//			Collections.sort(y);
		}
		nbX = x.size();
		nbY = y.size();	
		maxX = Collections.max(x);
		maxY = Collections.max(y);
	}
	
	protected void createGrid() {
		grid.clear();
		grid.setBounds(bounds.getBounds2D());
		int maxi = hyperbolicTransformEnabled ? 50 : mainCell.getNbX();
			maxi = 50;
		for (int i = 0; i <= maxi; i++) {
			double x = mainCell.getX() + i * mainCell.getPadX();
			double thickness = 1;
			if (hyperbolicTransformEnabled) {
				x = mainCell.getX() + hyperbolicTransform(i, hyperbolic_magnification_x, mainCell.getWidth());
				thickness = Math.min(1, 1.5 * (1 - i/(double)maxi));
			}
			grid.addVerticalLine(i + "", x, thickness);
		}
		int maxj = hyperbolicTransformEnabled ? 100 : mainCell.getNbY();
		for (int j = 0; j <= maxj; j++) {
			double y = mainCell.getY() + j * mainCell.getPadY();
			double thickness = 1;
			if (hyperbolicTransformEnabled) {
				y = mainCell.getY() + hyperbolicTransform(j, hyperbolic_magnification_y, mainCell.getHeight());
				thickness = Math.min(1, 1.5 * (1 - j/(double)maxj));
			}
			grid.addHorizontalLine(j + "", y, thickness);
		}
	}

	@Override
	protected void updateCoordinates() {
		// Already done, do nothing.
	}
	
//	public float getLinkOpacity(float previousOpacity, float aboveOpacity) {
//		return 0;
//	}
//	
	public Grid getGrid() {
		return grid;
	}

}
