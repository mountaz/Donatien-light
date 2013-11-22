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

import java.util.ArrayList;

import donatien.layout.Util;

// subgraph est utile pour tous les traitements sur les sous-graphes.
// attention un sous graphe n'est pas un graphe, enfin, pas encore, et/ou ne sera peut-etre même jamais
public class SubGraph {
	protected ArrayList <Edge> edges = new ArrayList<Edge>();
	protected ArrayList <Node> nodes = new ArrayList <Node>();
	protected Node rootNode;

	SubGraph(Node n){
		rootNode = n;
	}
	SubGraph(Node n, int level){
		rootNode = n;
		if (!nodes.contains(n) ){
			nodes.add(n);
		}			
		buildNodes(n,level);
		buildEdges();
	}

	public void print (){
		if (Util.verbose){
		int i=0;
		for (Node n : nodes){
			i++;
			System.out.println("Node "+i+" : "+ n.getProp("name"));
		}
		i = 0;
		for (Edge e : edges){
			System.out.println("Edge "+i+" from "+e.getSource().getProp("name")+" to "+ e.getTarget().getProp("name"));
		}
		}
	}


	void buildNodes(Node n, int level){
		Node _n;
		if (level >0){
			for (Edge e : n.getNeighbour()){
				_n = e.getOtherNode(n);
				if (!nodes.contains(_n) ){
					nodes.add(_n);
				}
				buildNodes(_n, level-1);
			}
		}
	}
	
	// suppose que buildNodes a déjà construit les nodes
	void buildEdges(){
		for (Node n : nodes){
			for (Edge e : n.getNeighbour()){
				addEdge(e);
			}
		}
	}

	// Ajoute une edge en vérifiant 
	//  	1 - que ce n'est pas une edge qui existe déjà et 
	// 		2 - que les deux extrémités sont bien dans le graphe
	public void addEdge (Edge e){
		Node p,q;
		if (!edges.contains(e)){
			p = e.getSource();
			q = e.getTarget();
			if (nodes.contains(p) && nodes.contains(q)){
				edges.add(e);
			}
		}
	}
	
	public ArrayList<Node> getNodes (){
		return nodes;
	}
	public ArrayList<Edge> getEdges (Node n){
		ArrayList<Edge> l = new ArrayList<Edge>();
		for (Node no: getNodes()){
			for (Edge e: no.getNeighbour()){
				if (edges.contains(e)&& !l.contains(e)){
					l.add(e);
				}
			}
		}
		return l;
	}
	
	public int getNodeCount (){
		return nodes.size();
	}
	
	public int getEdgeCount (){
		return edges.size();
	}
	public int getEdgeCount (Node n){
		ArrayList<Edge> l = getEdges(n);
		return l.size();
	}
	
	public ArrayList<Edge> getLocalEdges (Node n){
		ArrayList<Edge> _edges = new ArrayList<Edge>();
		for (Edge e : n.getNeighbour()){
			if (edges.contains(e)){
				_edges.add(e);
			}
		}
		return _edges;	
	}

}	
