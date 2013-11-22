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

import donatien.gui.Grid;
import donatien.model.graph.Graph;

public abstract class Layout {

	// Used for grouping change notifications.
	static final int FPS = 60;
	
	static ArrayList<Layout> allRunningLayouts = new ArrayList<Layout>();

	Graph graph = null;
	Rectangle2D bounds = null;
	boolean initialized = false;
	Thread layoutThread = null;
	ArrayList<LayoutListener> listeners = new ArrayList<LayoutListener>();
	boolean computingLayout = false;
		
	public Layout() {
	}
	
	/**
	 * Adds a listener to the layout algorithm.
	 * 
	 * The action listener will be called every time the layout is updated. Calls to step()
	 * are grouped to speed up the computation in such a way that the action listener is not
	 * called more often than 60 times per second.
	 * 
	 * @param l
	 */
	public void addListener(LayoutListener l) {
		listeners.add(l);
	}
	
	public void removeListener(LayoutListener l) {
		listeners.remove(l);
	}
	
	/**
	 * Returns the graph on which this layout algorithm operates on.
	 * 
	 * @return
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * Sets the graph on which the layout algorithm will operate on.
	 * 
	 * @param graph
	 */
	public void setGraph(Graph graph) {
		if (isComputingLayout()) {
			throw (new IllegalStateException("setGraph() cannot be called while the layout is being computed."));
		}
		this.graph = graph;
		initialized = false;
	}
	
	/**
	 * Returns the bounding box on which the layout algorithm operates on.
	 * 
	 * @return
	 */
	public Rectangle2D getBounds() {
		return bounds;
	}
	
	/**
	 * Sets the bounding box for the layout algorithm. The layout algorithm will set nodes'
	 * x,y coordinates so that they stay within this bounding box. Node size is not taken
	 * into account.
	 * 
	 * @param bounds
	 */
	public void setBounds(Rectangle2D bounds) {
		if (isComputingLayout()) {
			throw (new IllegalStateException("setBounds() cannot be called while the layout is being computed."));
		}
		this.bounds = bounds;
		initialized = false;
	}
	
	/**
	 * Performs a random layout. Useful for initializing some layout algorithms.
	 */
	public void randomLayout() {
		final donatien.layout.Layout layout0 = new donatien.layout.RandomLayout();
		layout0.setGraph(graph);
		layout0.setBounds(bounds);
		layout0.startLayout();
		layout0.stopLayout(); // make sure it's finished
	}
	
	/**
	 * Starts the layout algorithm.
	 * 
	 * The layout is performed in a separate thread, so this method will return immediately.
	 * The thread will call step() until it returns true or until stopLayout() is called.
	 * 
	 * This method does not do anything if graph == null or bounds == null, or if the layout
	 * is already being computed.
	 */
	public void startLayout() {
		if (isComputingLayout())
			return;
		if (graph == null || bounds == null)
			return;

		layoutThread = new Thread() {
			public void run() {
				
				computingLayout = true;
				allRunningLayouts.add(Layout.this);
				
				for (LayoutListener l : listeners)
					l.layoutStarted();
				
		//		if (!initialized) {
					init();
					initialized = true;
		//		}
				
				boolean finished = false;
				do {
					long t0 = System.nanoTime();
					long t1 = t0 + 1000000 * 1000 / FPS;
					while (System.nanoTime() < t1 && !finished && layoutThread == this) {
						finished = step();
					}
					if (layoutThread == this) {
						updateCoordinates();
						for (LayoutListener l : listeners)
							l.layoutChanged();
					}
					
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
					}
				} while (layoutThread == this && !finished);
				
				computingLayout = false;
				allRunningLayouts.remove(Layout.this);
				if (finished) {
					for (LayoutListener l : listeners)
						l.layoutFinished();
				} else {
					for (LayoutListener l : listeners)
						l.layoutStopped();
				}
			}
		};
		layoutThread.setPriority(Thread.MIN_PRIORITY);
		layoutThread.start();
	}
	
	/**
	 * Stops the layout algorithm.
	 * 
	 * This method waits for the thread to die before it returns. This method should be
	 * called before changing the graph or the bounding box.
	 */
	public void stopLayout() {
		if (layoutThread == null)
			return;
		Thread prevLayoutThread = layoutThread;
		layoutThread = null;
		try {
			prevLayoutThread.join();
		} catch (InterruptedException e) {
		}
	}
	
	public void stopLayoutAsync() {
		if (layoutThread == null)
			return;
		Thread prevLayoutThread = layoutThread;
		allRunningLayouts.remove(Layout.this);
		layoutThread = null;
	}
	
	/**
	 * Returns true if the algorithm is currently computing the layout.
	 * 
	 * @return
	 */
	public boolean isComputingLayout() {
		return computingLayout; //layoutThread != null && layoutThread.isAlive();
	}
	
	public Layout clone() {
		try {
			return getClass().getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean isLayoutRunning() {
		return allRunningLayouts.size() > 0;
	}
	
	public float getLinkOpacity(float previousOpacity, float aboveOpacity) {
		return 1;
	}
	
	public Grid getGrid() {
		return null;
	}
	
	/**
	 * Gives the layout algorithm an opportunity to initialize itself.
	 * 
	 * This method will be called by startLayout() in case the graph and/or the bounding boxes
	 * have changed since the last call to startLayout().
	 * 
	 * Don't call this method directly. Call startLayout() instead.
	 */
	protected abstract void init();
	
	/**
	 * Executes one step of the layout algorithm. Returns true if the layout has finished
	 * computing, false otherwise.
	 * 
	 * This method returns true at the first call for non-iterative algorithms. It might also
	 * never return true. The duration of a step is left unspecified.
	 * 
	 * Don't call this method directly. Call startLayout() instead.
	 * 
	 * @return
	 */
	protected abstract boolean step();
	
	/**
	 * Copies the coordinates from the layout algorithm to the Graph object, if needed.
	 */
	protected abstract void updateCoordinates();
}
