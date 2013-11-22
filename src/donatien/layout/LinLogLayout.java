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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import linlog.MinimizerBarnesHut;
import linlog.OptimizerModularity;
import donatien.model.graph.Edge;
import donatien.model.graph.Graph;
import donatien.model.graph.Node;
//import linlog.Edge;
//import linlog.Node;

/**
 * Noack's linlog layout algorithm.
 * 
 * @author dragice
 *
 */
public class LinLogLayout extends Layout {

	//Map<String,Map<String,Double>> linlogGraph = null;
	List<linlog.Node> nodes;
	List<linlog.Edge> edges;
	Map<linlog.Node,double[]> nodeToPosition;
	MinimizerBarnesHut minimizer;
	
	boolean clustersEnabled = false;
	
	public LinLogLayout() {
		super();
	}   
	
	protected static Map<String,Map<String,Double>> convertGraph(Graph graph) {
		Map<String,Map<String,Double>> result = new HashMap<String,Map<String,Double>>();
		Vector<Node> nodes = graph.getNodes();
		for (int n = 0; n < nodes.size(); n++) {
			String source = "" + n;
			result.put(source, new HashMap<String,Double>());
		}
		for (Edge e : graph.rebuildEdges()) {
			String source = "" + nodes.indexOf(e.getSource()); 
			String target = "" + nodes.indexOf(e.getTarget());
			double weight = 1;
			result.get(source).put(target, weight);
		}
		return result;
	}
	
	protected void writePositions(Map<linlog.Node,double[]> nodeToPosition, Map<linlog.Node,Integer> nodeToCluster, Rectangle2D dataBounds, Graph graph) {
		int nodeIndex = 0;
		for (linlog.Node node : nodeToPosition.keySet()) {
			double[] position = nodeToPosition.get(node);
			int cluster = 0;
			if (clustersEnabled)
				cluster = nodeToCluster.get(node); // unused for now
            nodeIndex = Integer.parseInt(node.name);
            Node n = graph.getNodes().get(nodeIndex);
            n.setX((position[0] - dataBounds.getX()) / dataBounds.getWidth() * bounds.getWidth() + bounds.getX());
            n.setY((position[1] - dataBounds.getY()) / dataBounds.getHeight() * bounds.getHeight() + bounds.getY());
            //nodeIndex++;
		}
	}	
	@Override
	protected void init() {
		Map<String,Map<String,Double>> linlogGraph = convertGraph(graph);
		linlogGraph = linlog.LinLogLayout.makeSymmetricGraph(linlogGraph);
        Map<String,linlog.Node> nameToNode = linlog.LinLogLayout.makeNodes(linlogGraph);
        nodes = new ArrayList<linlog.Node>(nameToNode.values());
        edges = linlog.LinLogLayout.makeEdges(linlogGraph,nameToNode);
        // FIXME: Random placement -> remove
        nodeToPosition = linlog.LinLogLayout.makeInitialPositions(nodes, false);
        
		// see class MinimizerBarnesHut for a description of the parameters;
		// for classical "nice" layout (uniformly distributed nodes), use
		//new MinimizerBarnesHut(nodes, edges, -1.0, 2.0, 0.05).minimizeEnergy(nodeToPosition, 100);
		
        minimizer = new MinimizerBarnesHut(nodes, edges, -2.0, 1.0, 0.05);
        minimizer.startMinimizeEnergy(nodeToPosition);
	}

	@Override
	protected boolean step() {

//		new MinimizerBarnesHut(nodes, edges, -1.0, 2.0, 0.05).minimizeEnergy(nodeToPosition, 1);

//		minimizer = new MinimizerBarnesHut(nodes, edges, -1.0, 2.0, 0.05);
//		minimizer.startMinimizeEnergy(nodeToPosition);
		return minimizer.minimizeEnergyStep();
	}

	@Override
	protected void updateCoordinates() {
		
        // see class OptimizerModularity for a description of the parameters
		Map<linlog.Node,Integer> nodeToCluster = null;
		if (clustersEnabled)
			nodeToCluster = new OptimizerModularity().execute(nodes, edges, false);
        
		// determine minimum and maximum positions of the nodes
        double minX = Double.MAX_VALUE; double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE; double maxY = -Double.MAX_VALUE;
        double diameter = 0;
        for (linlog.Node node : nodeToPosition.keySet()) {
            double[] position = nodeToPosition.get(node);
//            double diameter = Math.sqrt(node.weight);
            minX = Math.min(minX, position[0] - diameter/2);
            maxX = Math.max(maxX, position[0] + diameter/2);
            minY = Math.min(minY, position[1] - diameter/2);
            maxY = Math.max(maxY, position[1] + diameter/2);
        }

        // Write positions
		writePositions(nodeToPosition, nodeToCluster, new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY), graph);
	}
}
