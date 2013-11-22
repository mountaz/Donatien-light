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

import donatien.model.graph.Cell;
import donatien.model.graph.Graph;
import donatien.model.graph.Node;

public class SimplePolarLayout extends Layout {
	double oX, oY,  wX, wY;
	int nbX, nbY, stepNum, maxStep = 3; //maxstep devra être remplacé par l'ordre de la signature ie le nb de dimensions prises en compte dans la signature
	Graph g;
	Graph lastGraph;
	ArrayList<Double> x = new ArrayList<Double>();
	ArrayList<Double> y = new ArrayList<Double>();

    //attention init est appelée bien plus souvent qu'à son tour, pour une raison qui restera mystérieusement mystérieuse...
	//c'est facheux car certains calculs couteux ne devraient pas s'effectuer si souvent et les tests scabreux pour leur éviter de se multiplier inutilement sont bien des tests scabreux
	protected void init() {
//		System.out.println("");
//		System.out.println("========================== init start "+stepNum + " " + getBounds() + " " + getGraph());		
		g = getGraph();
		// NB: on n'aurait pas besoin de ce test SI init cote structure n'était pas le même que l'init coté graphique mais, pour cela, hélas, on peut attendre la version suivante dont la date de sorite n'est pas imminente :-(
		if (g != lastGraph) {
			for (Node n : g.getNodes()){
				n.computeStamp();
			}
			lastGraph = g;
		}
		int k = 0;
		double newX, newY;
		for (Node n : g.getNodes()){
			if (k < n.getSizeStamp() ){
				newX = n.getStamp(k).getX();
				newY = n.getStamp(k).getY();
				if (!x.contains(newX)){x.add(newX);}
				if (!y.contains(newY)){y.add(newY);}
			}else return;
			Collections.sort(x);
			Collections.sort(y);
		}
		nbX = x.size();
		nbY = y.size();	
		double maxX = Collections.max(x);
		double maxY = Collections.max(y);	
		//		Cell c = new Cell(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), maxX,maxY);
//		Cell c = new Cell(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), (int) maxX, (int) maxY);
//		reprendre ici ce bug infame du à un sérieux emmelage de pinceaux entre rang et max values et badaboum
		Cell c = new Cell(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 10, 19);
		for (Node n : g.getNodes()){
			n.cpc(0, c);
		}
	}
	

	
	@Override
	protected boolean step() {
		return true; // finished
	}

	@Override
	protected void updateCoordinates() {
		// Already done, do nothing.
	}
	
//	public float getLinkOpacity(float previousOpacity, float aboveOpacity) {
//		return 0;
//	}

}