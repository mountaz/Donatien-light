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

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;

import donatien.gui.EllipticGrid;
import donatien.gui.Grid;
import donatien.model.graph.Cell;
import donatien.model.graph.Graph;
import donatien.model.graph.Node;

public class PolarLayout extends Layout {
	double oX, oY,  wX, wY;
	double nLines = 50, nColonnes = 91 - 64; 
	double[][] xcoords = new double[(int)nLines][(int)nColonnes]; 
	double[][] ycoords = new double[(int)nLines][(int)nColonnes]; 
	int nbX, nbY, stepNum, maxStep = 3; //maxstep devra être remplacé par l'ordre de la signature ie le nb de dimensions prises en compte dans la signature
	Graph g;
	ArrayList<Double> x = new ArrayList<Double>();
	ArrayList<Double> y = new ArrayList<Double>();
	public EllipticGrid grid = new EllipticGrid(new Rectangle2D.Double());

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
		// Update node coords
		for (Node n : graph.getNodes()) {
			String s = n.getProp("name");
			cccc(n, 0, mainCell);
		}
				
		return true;
	}
		
	public void cccc(Node n, int k, Cell c){
		double padX = c.getPadX();
		double padY = c.getPadY();
		double a=0,b=0;
		double x = c.getCenterX();
		double y = c.getCenterY();
		
		// Update node coords
		String s = n.getProp("name");
		int size = s.length() ;
		if (size < 0) size = 0;
		if (size > nLines - 2) size = (int) nLines -3;

		a = size ; 
		b = getColonne(s,nColonnes);
		if (a < nLines && b <nColonnes){
			x = x+xcoords[(int)a][(int)b];
			y = y+ycoords[(int)a][(int)b];
		}else{
			System.out.println("gros bug dans PolarLayout");
		}
		n.setX(x);
		n.setY(y);
	}
	
	private double getColonne(String s, double nc) {
		int codeAscii = (int) s.toUpperCase().charAt(0);
		int codeA = 64;
		if (codeAscii > 91 ) codeAscii = 90;
		if (codeAscii < codeA-1 ) codeAscii = codeA;
		double b = (codeAscii - codeA) ;
		System.out.println(b+" ~ "+codeA+" ~ " +codeAscii);
		return b;
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
		System.out.println("creategrid");
		grid.clear();
		grid.setBounds(bounds.getBounds2D());
		double a=0,b=0,w,h;
//		w= grid.getBounds().width;
//		h= grid.getBounds().height;
		w= 470;
		h= 350;
		double nParalleles = nLines;
		double nMeridiens = nColonnes;
		for (double i = 0; i < nParalleles; i++) {
			a = i*w/nParalleles;
			b = i*h/nParalleles;
			grid.addCubitus(a, b);
			for (double teta = 0; teta < nMeridiens; teta ++) {
				 xcoords[(int)i][(int)teta]=a*Math.cos(2*teta*Math.PI/nMeridiens);
				 ycoords[(int)i][(int)teta]=b*Math.sin(2*teta*Math.PI/nMeridiens);
			}
		}
		for (double teta = 0; teta < nMeridiens; teta ++) {
			grid.addRadius(a,b,2*teta*Math.PI/nMeridiens);
		}
	}

	@Override
	protected void updateCoordinates() {
		// Already done, do nothing.
	}
	
//	public float getLinkOpacity(float previousOpacity, float aboveOpacity) {
//		return 0;
//	}
	
	public Grid getGrid() {
		return grid;
	}

}
