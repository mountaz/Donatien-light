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
import java.util.ArrayList;
import java.util.Hashtable;

import donatien.gui.Layer;
import donatien.gui.MasterLayer;
import donatien.gui.View;
import donatien.util.Pair;

public class MasterGraph extends Graph {

	public MasterGraph(Layer layer) {

		super();

		View view = layer.getParent();
		
		view.updateAlignmentData();
		ArrayList<ArrayList<Pair<Layer, Node>>> alignmentGroups = view.getAlignmentGroups();
		ArrayList<Node> masterNodes = new ArrayList<Node>();
		Hashtable<Node, Node> nodeToMasterNode = new Hashtable<Node, Node>();
		
		// Create master nodes
		int counter = 0;
		for (ArrayList<Pair<Layer, Node>> ag : alignmentGroups) {
			Node mn = new Node(""+counter);
			mn.prop.put("name", getGroupName(ag));
			Point2D.Double p = view.getAverageNodeCenter(ag);
			layer.setPositionInLayer(mn, p);
			counter++;
			masterNodes.add(mn);
			for (Pair<Layer, Node> ln : ag) {
				Node slave = ln.getValue();
				nodeToMasterNode.put(slave, mn);
				mn.slaves.add(ln);
///System.err.println(mn.getProp("name") + " -> " + slave.getProp("name"));
			}
		}
		
		// Re-create edges
		counter = 0;
		for (Layer l : view.getLayers()) {
			if (l instanceof MasterLayer)
				continue;
			Graph g = l.getGraph();
			for (Node n : g.getNodes()) {
				for (Edge e : n.getNeighbour()) {
					if (e.getSource() == n) {
//						n.getNeighbour().c
						Node mn1 = nodeToMasterNode.get(n);
						Node mn2 = nodeToMasterNode.get(e.getOtherNode(n));
						if (!mn1.getVoisins().contains(mn2)) {
							Edge me = new Edge(mn1.getId()+mn2.getId(), mn1, mn2);							
							mn1.edges.add(me);
							mn2.edges.add(me);
						}						
					}
				}
			}
			counter++;
		}
		
		// Add master nodes
		for (Node n : masterNodes)
			addNode(n);
	}
	
	protected String getGroupName(ArrayList<Pair<Layer, Node>> nodes) {
		if (nodes.size() == 0)
			return "";
		// name of the first node
		String name = nodes.get(0).getValue().label;
		if (nodes.size() > 1)
			name += " (" + nodes.size() + ")";
		return name;
	}
	
	public Node getMasterNode(Node slave) {
		for (Node n : getNodes()) {
			// FIXME: the method contains does not work probably because the equals method uses IDs
//			if (n.slaves.contains(slave))
//				return n;
			for (Pair<Layer,Node> s : n.slaves) {
				if (s.getValue() == slave)
					return  n;
			}
		}
		return null;
	}
	
}
