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
package donatien.gui;

import java.io.File;
import java.net.URL;

import donatien.file_io.parsers.GraphMLParser;
import donatien.model.graph.Graph;

/**
 * Loads a graph asynchronously.
 * 
 * @author dragice
 *
 */
public class GraphLoader {

	public static void loadGraph(final String name, final String filename, final Layer layer) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				loadGraph_sync(name, filename, layer);
			}
		});
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}	
	
	private static void loadGraph_sync(String name, String filename, Layer layer) {
		Graph g;
		GraphMLParser parser = new GraphMLParser();
		try {
			if (filename.startsWith("http")) {
				g = parser.parse(new URL(filename));
			} else {
				g = parser.parse(new File(filename));
			}
		} catch (Exception e) {
			e.printStackTrace();
			g = new Graph(); // empty graph
			layer.setGraph(g);
			return;
		}
		g.setName(name);
		g.tagClusters();
		g.putProp("filename", filename);
		layer.setGraph(g);
		layer.computeLayout();
		layer.repaint();
	}
}
