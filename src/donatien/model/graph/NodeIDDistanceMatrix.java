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


/**
 * Creates a binary (0/1) distance matrix based on node IDs.
 * 
 * FIXME: Rename NodeDistanceMatrix to NodeEuclidianDistanceMatrix and have this class and the current NodeDistanceMatrix class
 * derive from a common NodeDistanceMatrix subclass where the distance semantics is left unspecified.
 * 
 * @author dragice
 *
 */
public class NodeIDDistanceMatrix extends NodeDistanceMatrix {

	public NodeIDDistanceMatrix(Graph columnGraph, Graph rowGraph) {
		super(columnGraph, rowGraph);
		setForceSymmetry(false);
		updateDistances();
	}
	
	public void updateDistances() {
		Node nc, nr;
		for (int c=0; c<numColumns(); c++) {
			nc = columns.get(c);
			for (int r=0; r<numRows(); r++) {
				nr = rows.get(r);
				setDistance(c, r, nc.getId().equals(nr.getId()) ? 0 : Double.POSITIVE_INFINITY);
			}
		}
	}	
}
