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

import donatien.model.graph.Edge;
import donatien.model.graph.Graph;
import donatien.model.graph.Node;


public class Util {
	private static int initNumber=0;
	public static boolean verbose= false;
	public static double round(double what, int howmuch) { 
		return (double)( (int)(what * Math.pow(10,howmuch) + .5) ) / Math.pow(10,howmuch); 
		} 
	public static void incrInitNumber() { 
		initNumber++; 
		} 
	public static int getInitNumber() { 
		return initNumber; 
		} 

	public static void printNodes(Graph g) {	
		for (Node n : g.getNodes()){
			n.putProp("degree",String.valueOf(n.getNeighbour().size()));
			System.out.println("ID : "+n.getId());
			System.out.println("NAME : "+n.getProp("name"));
			System.out.println("POIDS : "+n.getProp("w"));
			//		System.out.println("DEGREE:" + n.getProp("degree"));
			System.out.println("---------");
		}
	}
	public static int printEdges(Graph g) {
		int cpt = 0;
		for (Node n : g.getNodes()){
			for (Edge e : n.getNeighbour()){
				cpt++;
				Node other = e.getOtherNode(n);
				System.out.println("EDGE BETWEEN "+other.getProp("name")+" AND "+n.getProp("name")+" : "+e.getProp("w"));
			}
		}
		return cpt;
	}



	
}
