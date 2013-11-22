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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import donatien.file_io.parsers.GraphMLParser;
import donatien.layout.Layout;
import donatien.model.graph.Graph;

public class MainWindow extends JFrame {	
	JViews views = null;
	JGraphList graphs = null;
	Layout selectedLayout = null; // default initial layout for graphs

	public MainWindow() {
		super("Donatien");
		
		views = new JViews();
		getContentPane().add(views, BorderLayout.CENTER);
		graphs = new JGraphList(views);
		JScrollPane scroll = new JScrollPane(graphs);
//		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scroll, BorderLayout.WEST);
		scroll.setPreferredSize(new Dimension(200, 1500));
		
//		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphs, scroll);
//		splitPane.setOneTouchExpandable(true);
//		splitPane.setDividerLocation(150);
//		getContentPane().add(splitPane);
		
		JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);
		menubar.add(new JMenu("File") {
			{
				add(new JMenuItem("Load directory...") {
					{
						addActionListener(new ActionListener() {							
							@Override
							public void actionPerformed(ActionEvent e) {
								JFileChooser chooser = new JFileChooser("Data/");
								chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
								chooser.setAcceptAllFileFilterUsed(false);
								if (chooser.showOpenDialog(MainWindow.this) == JFileChooser.APPROVE_OPTION) {
									clearGraphFiles();
									try {
										addGraphFiles(chooser.getSelectedFile().getAbsolutePath() + "/");
									} catch (Exception e2) {
										e2.printStackTrace();
									}
									repaint();
								}
							}

						});
					}
				});
			}
		});
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void clearGraphFiles() {
		graphs.clearGraphIcons();
	}

	public void addGraphFile(String name, String file) {
		GraphIcon icon = new GraphIcon();
		icon.setTitle(name);
		icon.filename = file;
		graphs.addGraphIcon(icon);
	}

	public void addGraphFiles(String dirname) {
		File dir = new File(dirname);
		String[] filenames = dir.list();
		if (filenames == null)
			return;
		for (String fn : filenames) {
			if (fn.endsWith(".xml")) {
				File f = new File(fn);
				String name = f.getName();
				name = name.substring(0, name.length()-4);
				addGraphFile(name, dirname + fn);
			}
		}
	}
	
	public void buildGraphSelector(final String defaultFile) {
		// FIXME
		Timer t = new Timer(700, new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				graphs.load(defaultFile);
			}
		});
		t.setRepeats(false);
		t.start();
	}
	
	public void addLayout(String name, Layout layout) {
		AllLayouts.allLayouts.put(name, layout);
		AllLayouts.allLayoutNames.add(name); // remember ordering
	}
	
	public void addLayoutSeparator() {
		AllLayouts.separators.add(AllLayouts.allLayoutNames.size());
	}
	
	public void setDefaultLayout(String layout) {
		AllLayouts.setDefaultLayout(layout);
	}
	
	static GraphMLParser parser = new GraphMLParser();
	
	private void addGraph(Graph graph) {
		Layer layer = new Layer();
		views.addLayer(layer);
		layer.setGraph(graph);
		layer.setDefaultLayout();
		repaint();
	}
	
	private void removeGraph(Graph graph) {
		Layer layer = views.getLayer(graph);
		if (layer == null)
			return;
		if (layer.layout != null)
			layer.layout.stopLayoutAsync();
		layer.setLayout("", null);
		layer.setGraph(null);
		views.removeLayer(layer);
		repaint();
	}
	
	private static Graph loadGraph(String name, String filename) {
		try {
			Graph g;
			if (filename.startsWith("http")) {
				g = parser.parse(new URL(filename));
			} else {
				g = parser.parse(new File(filename));
			}
			g.setName(name);
			return g;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void loadGraphs(ArrayList<String> names, ArrayList<String> filenames) {
		
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		ArrayList<Graph> currentGraphs = new ArrayList<Graph>();
		for (Graph l : views.getGraphs())
			currentGraphs.add(l);
		
		ArrayList<Graph> keep = new ArrayList<Graph>();
		for (Graph g : currentGraphs) {
			if (names.contains(g.getName()))
				keep.add(g);
			else
				removeGraph(g);
		}
		
		ArrayList<String> remainingNames = views.getGraphNames();
		for (int i = 0; i < filenames.size(); i++) {
			String name = names.get(i);
			String filename = filenames.get(i);
			if (!remainingNames.contains(name)) {
				Graph g = loadGraph(name, filename);
				if (g != null)
					addGraph(g);
			}				
		}
		
		//views.layoutAll();
		
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}
