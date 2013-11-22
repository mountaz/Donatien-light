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
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import donatien.gui.JViews;
import donatien.layout.LayoutAnimator;


public class Graph extends PropertyModifiable {
		/** All nodes contained in the graph*/
		protected Vector<Node> nodes = new Vector<Node>();

		/** contains edges local to this graph **/
//		private ArrayList<Edge> l_edges= new ArrayList<Edge>();
		
		/** Indicate if the graph is directed or not*/
		private boolean directed = true;
		
		/** Indicate if the graph is clustered or not*/
		private boolean clustered = false;
		
		/** Name of the graph*/
		private String name = "";

		/**Parent Node*/
		private Node parent_node = null;
		
		
		
	
	/**
	 * Construct an Empty graph, directed, with no name.
	 */
	public Graph()
	{		
	}
	

	// charger fichier de keywords dans hashmap
	// creer des titleKeywords pour tous les noeuds du graphe
	public void addTitleKeywords(HashMap<String,ArrayList<String>> resultat) {
		HashMap<String,Node> dejaVu = new HashMap<String,Node>(); 
		ArrayList<Node> l= new ArrayList<Node>();
		Node tmp;
		Edge etmp;
		ArrayList<String> ll;
		for (Node n: nodes){
			ll = resultat.get(n.getId());
			if (ll != null){
				for (String s : ll){
					tmp = dejaVu.get(s);
					if (tmp !=null) {
					}else{
						tmp = new Node(s);
						tmp.putProp("name",s);
						tmp.putProp("genre", "keyword_t");
						l.add(tmp);
					}
					etmp = new Edge(n.getId(),n,tmp);
					tmp.edges.add(etmp);
					n.edges.add(etmp);
					System.out.println("adding edge "+tmp.getId()+"-"+ n.getId());
				}
			}
		}
		for (Node n: l){
			nodes.add(n);
		}

	}
 

	public void insertEdgeAndNodes(Node from, Node to){
		for (Edge e: from.getNeighbour()){
			if (e.getOtherNode(from).equals(to)) {
				return;
			}
		}
		for (Edge e: to.getNeighbour()){
			if (e.getOtherNode(to).equals(from)) return;
		}
		Edge e = new Edge(from.getId()+to.getId(), from, to);
		from.edges.add(e);
		to.edges.add(e);
		if (!nodes.contains(from)) nodes.add(from);
		if (!nodes.contains(to)) nodes.add(to);
	}
	
	public Graph computeKKGraph(){
		Graph g1 = new Graph();
		Node k;
		ArrayList<Node> lk;
		for (Node n: nodes){
			if (n.getProp("genre").equals("paper")) {
				lk = new ArrayList<Node>();
				for(Edge ek: n.getNeighbour()){
					k = ek.getOtherNode(n);
					if (k.getProp("genre").contains("keyword")) 
						lk.add(k);
				}
				for(int i = 0; i < lk.size();i++){
					for(int j = i+1; j < lk.size();j++){
						g1.insertEdgeAndNodes(lk.get(i), lk.get(j));
					}
					
				}
			}
		}
		return g1;
	}
	
	public void setCells(int k,Cell c){
		for (Node n : getNodes()) {
			n.updateCell(k,c);
		}
	};

	public Node getNodeByName(String label){
		for (Node n: nodes){
			if (n.getProp("name").equals(label)){
				return n;
			}
		}
		return null;
	}
	/**
	 * 
	 */
	public void addNode(Node n){
		nodes.add(n);
		n.index = nodes.size() - 1;
		n.setParent(this);
	}

	public void addNodeIfNotExist(Node n)
	{
//		if (!nodes.contains(n)&& !(n.getProp("name").contains("***"))){
		if (!nodes.contains(n)){
			addNode(n);
		}
	}
	
	public ArrayList<Graph> getSubGraphs(Graph g){
		Graph gprime;
		ArrayList<Graph> lesGraphes = new ArrayList<Graph>();
		HashMap<Edge,Node> ajoutEdges = new HashMap<Edge,Node>();
		for (Node n: g.getNodes()){
			gprime = n.getGraphContent();
			if (gprime != null){
				for(Node nprime: gprime.getNodes()){
					ajoutEdges.put(new Edge("id" + n.getId() + "-" + nprime.getId(),n, nprime),n);
				}
				lesGraphes.add(gprime);		
				lesGraphes.addAll(getSubGraphs(gprime));
			}
		}
		Node n;
		for (Edge e:ajoutEdges.keySet()){
			n = ajoutEdges.get(e);
			n.getNeighbour().add(e);
		}
		
		return lesGraphes;
	}

	public void unfoldGraph(Graph g){
		ArrayList<Graph> lesGraphes = getSubGraphs(g);
		for (Graph g1: lesGraphes){
			merge(g1);
		}
	}
	
	// marque les liens de containment
	// marque les noeuds de type cluster : "y" ou "n"
	// insere le niveau d'un noeud (distance à la racine dans l'arbre des clusters)
	public ArrayList<Graph> tagClusters(Graph g, int level){
		Graph gprime;
		ArrayList<Graph> lesGraphes = new ArrayList<Graph>();
		HashMap<Edge,Node> containmentEdges = new HashMap<Edge,Node>();
		for (Node n: g.getNodes()){
			gprime = n.getGraphContent();
			n.putProp("level", new Integer(0).toString());
			n.putProp("cluster", "n");
			if (gprime != null && gprime.getNodes().size()>0){
				n.putProp("cluster", "y");
				n.putProp("level", new Integer(level+1).toString());
				for(Node nprime: gprime.getNodes()){

					containmentEdges.put(new Edge("id" + n.getId() + "-" + nprime.getId(),n, nprime),n);
				}
				lesGraphes.add(gprime);		
				lesGraphes.addAll(tagClusters(gprime, level+1));
			}
			System.out.println(" ");
			System.out.println("NAME : "+n.getProp("name"));
			System.out.println("CLUSTER : "+n.getProp("cluster")+ " - "+n);
			System.out.println("LEVEL : "+n.getProp("level")+ " - "+n);
		}
		Node n;
		for (Edge e:containmentEdges.keySet()){
			n = containmentEdges.get(e);
			e.putProp("containment", "y");
			n.getNeighbour().add(e);
		}
		
		return lesGraphes;
	}

	public void tagClusters(){
		ArrayList<Graph> lesGraphes = tagClusters(this,0);
		for (Graph g1: lesGraphes){
			merge(g1);
		}
	}
	
	// Extrait le graphe contenant seulement les noeuds d'origine et les liens d'adjacences	
	public Graph getModel(){
		Graph g = new Graph();
		for (Node n : getNodes()){
			if (!(n.getProp("cluster").equalsIgnoreCase("y"))){
				g.addNode(n);
			}
		}
		return g;
	}
	

	public void merge(Graph g){
		for (Node n: g.getNodes()){
			this.addNode(n);
		}		
	}

	//creation d'un sous-graphe connexe, à partir d'un label et d'un nombre max de noeuds
	public Graph createSubGraph(String label,int maxNode){
		Graph g = new Graph();
		Node origine = getNodeByName(label);
		origine.putSystemProp("p", "y"); // p y = processed yes - p n = processed no
		g.addNode(origine);
		String ut;
		for (Edge e: origine.getNeighbour()){
			if(e.getOtherNode(origine)!=origine) {
				e.getOtherNode(origine).putSystemProp("p","n");
				if (e.getOtherNode(origine).getProp("name").contains("***")) {e.getOtherNode(origine).putSystemProp("p","y");}
				System.out.println("marquing n for "+e.getOtherNode(origine).toString());
			}
//			System.out.println("marquing no");
			g.addNodeIfNotExist(e.getOtherNode(origine));
		}
		int oldNodeSize = 0;
		while (g.nodes.size()<maxNode && g.nodes.size()>oldNodeSize){
			System.out.println("processing "+g.nodes.size());
			oldNodeSize = g.nodes.size();
			g.addNeighbourhood();
		}
		g.removeUndesirableEdges();//oh comme c'est pourri...
		return g;
	}
	
	public void removeUndesirableEdges(){
		ArrayList<Edge> edgesToProcess;
		for (Node n: nodes){
			edgesToProcess = new ArrayList<Edge>();
			if (n.getSystemProp("p")== "n"){	
				for (Edge e: n.getNeighbour()){
					edgesToProcess.add(e);
				}
				for (Edge e: edgesToProcess){
					if(!nodes.contains(e.getOtherNode(n))){
						System.out.println("removing edges");
						n.edges.remove(e);
				}
			}
			}
		}
	}
	
	public void addNeighbourhood(){
		ArrayList<Node> nodesToProcess= new ArrayList<Node>();
//		System.out.println("addNie "+nodes.size());
		String ut;
		for (Node n : nodes) {
				ut = (String) n.getSystemProp("p");
				if (ut != null && ut.equals("n")) {
					nodesToProcess.add(n);
				}
		}

		for (Node n: nodesToProcess){
			n.putSystemProp("p", "y");			
			for (Edge e: n.getNeighbour()){
				if (e.getOtherNode(n).getSystemProp("p") == null){
					addNodeIfNotExist(e.getOtherNode(n));
					e.getOtherNode(n).putSystemProp("p","n");
					if (e.getOtherNode(n).getProp("name").contains("***")) {e.getOtherNode(n).putSystemProp("p","y");}
					System.out.println("adding node");
				}
			}
		}
	}

	
	//Extraction d'une partie d'une composante connexe 

	public Graph createSubGraph(){
		Graph g = new Graph();
		HashSet<Node> from = new HashSet<Node>();
		HashSet<Node> to = new HashSet<Node>();
		HashSet<Edge> edges = new HashSet<Edge>();
		Node copyN;
		for (Node n : getNodes()) {
			for (Edge e : n.getNeighbour()) {
				edges.add(e);
			}
		}
		for (Edge e : edges) {
			from.add(e.getSource());
			to.add(e.getTarget());
		}
		from.retainAll(to);
		for (Node n : from) {
			copyN = n.lightCopy();
			g.addNode(copyN);
		}
		g.updateEdges();
		return g;
	}
	
	public void updateEdges(){
		ArrayList<Edge> dejaVu= new ArrayList<Edge>();
		Node q,r;
		Edge newAge; // :-) 
		for (Node p:getNodes()){
			q = (Node) p.getSystemProp("alias");
			for (Edge e: q.getNeighbour() ){
				if (!dejaVu.contains(e)){
					r = (Node) e.getOtherNode(q).getSystemProp("alias");
					if (getNodes().contains(r)){
						newAge= new Edge(e.getID(),(Node) e.getSource().getSystemProp("alias"),(Node) e.getTarget().getSystemProp("alias"));
						newAge.putProp("w",e.getProp("w"));
						p.getNeighbour().add(newAge);
					}
					dejaVu.add(e);}
			}
		}

}

	public void initCoords(int ox, int oy){
		for (Node n : getNodes()){
			n.setX(ox);
			n.setY(oy);
		}
	}
	
	
	/**
	 * Return the signature of the graph
	 * 
	 * @return signature
	 */
	public Vector<Node> getNodes(){
		return nodes;
	}
	
	public Node getNode(String id) {
		for (Node n : nodes) {
			if (n.getId().equals(id))
				return n;
		}
		return null;
	}
	
	
	/**
	 * Parent Node
	 *@return the parent node
	 */
	public Node getParentNode()
	{
		return parent_node;
	}
	
	/**
	 * Set Parent Node
	 */
	public void setParentNode(Node parent)
	{
		parent_node = parent;
	}
	
	public Vector<Edge> rebuildEdges() {
		Vector<Edge> edges = new Vector<Edge>();
		for (Node n : getNodes()) {
			for (Edge e : n.getNeighbour()) {
				if (e.getSource() == n)
					edges.add(e);
			}
		}
		return edges;
	}
	
	public void removeEdge(Edge e)
	{
		Node src = e.getSource();
		Node trg = e.getTarget();

		src.getNeighbour().remove(e);
		trg.getNeighbour().remove(e);
	}
	
	/**
	 * Remove a node and all this incidents edges.
	 * @param n
	 */
	public void removeNode(Node n)
	{
		Vector<Edge> edges = n.getNeighbour();
		for (int i=0;i<edges.size();i++)
		{
			Edge e = edges.get(i);
			
			if (e.getSource()!=n)
			{
				e.getSource().getNeighbour().remove(e);
			}
			
			if (e.getTarget()!=n)
			{
				e.getTarget().getNeighbour().remove(e);
			}
			
		}
		n.setParent(null);
		nodes.remove(n);
	}
	/**
	 * Modify the property directed of this graph
	 * 
	 * @param directed
	 */
	public void setDirected(boolean directed)
	{
		this.directed = directed;
	}
	
	/**
	 * Modify the name of this graph
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Return the name of this graph
	 * @return name of this graph
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Return true if this graph is directed else false.
	 * 
	 * @return TRUE if the graph is directed else FALSE
	 */
	public boolean isDirected()
	{
		return directed;
	}
	
	public boolean isClusteredGraph() {
		for (Node n: nodes) {
			if (n.isCluster())
				return true;
		}
		return false;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	

	public void paint(Graphics2D g, LayoutAnimator animator, RenderingAttributes att) {
		Rectangle2D clip = g.getClip().getBounds2D();
		int count = nodes.size();
	
		g.setStroke(att.nodeStroke);
//		for (Edge e : rebuildEdges()) {
//			e.paint(g, att);
//		}
		
		if (att.highQuality)
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		else
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		
		boolean isClusteredGraph = isClusteredGraph();
		
		if (att.edgeColor.getAlpha() > 0 && !JViews.HIDE_LINKS) {
			for (int i=0; i<count; i++) {
				Node n = nodes.get(i);
				for (Edge e : n.getNeighbour()) {
					if (e.getSource() == n) {
						int i2 = e.getOtherNode(n).index;
						if (animator == null)
							e.paint(g, att);
						else
							e.paint(g, animator.getX(i), animator.getY(i), animator.getX(i2), animator.getY(i2), att);
					}
				}
			}
		}
		
		if (att.highQuality && !att.xrayMode) {
			g.setColor(att.shadowColor);
			for (int i=0; i<count; i++) {
				Node n = nodes.get(i);
				if (n.intersects(clip))
					if (animator == null)
						n.paintShadow(g, att);
					else
						n.paintShadow(g, animator.getX(i), animator.getY(i), att);
			}
		}
	
		for (int i=0; i<count; i++) {
			Node n = nodes.get(i);
			if (n.intersects(clip))
				if (animator == null)
					n.paint(g, att);
				else
					n.paint(g, animator.getX(i), animator.getY(i), att);
		}
	}


}
