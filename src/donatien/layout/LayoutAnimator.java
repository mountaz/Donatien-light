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


import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Vector;

import donatien.model.graph.Graph;
import donatien.model.graph.Node;
import donatien.util.GUIUtils;
import donatien.util.GUIUtils.AdvancedKeyListener;
import fr.aviz.animation.AnimatedFloat;
import fr.aviz.animation.AnimationListener;
import fr.aviz.animation.AnimationTimer;
import fr.aviz.animation.Flag;

public class LayoutAnimator implements LayoutListener {

	Graph graph;
	Layout layout;
	ArrayList<AnimatedFloat> x_coords = new ArrayList<AnimatedFloat>();
	ArrayList<AnimatedFloat> y_coords = new ArrayList<AnimatedFloat>();
	Flag flag = new Flag();
	AnimatedFloat linkOpacity = new AnimatedFloat(1, 0.15f, 0.002f, flag);
	int n_coords = 0;
	
	public LayoutAnimator() {
		AnimationTimer.addListener(new AnimationListener() {
			@Override
			public void animateEvent() {
				if (!AnimationTimer.isRunning())
					flag.changed = false;
			}
		});
		GUIUtils.addAdvancedKeyListener(null, new AdvancedKeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}
			
			@Override
			public void keyRepeated(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressedOnce(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SHIFT)
					setSlow(true);
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SHIFT)
					setSlow(false);
			}
		}, false);
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
		copyGraphToAnimatedFloats();
	}

	public Layout getLayout() {
		return layout;
	}
	
	public void setLayout(Layout layout) {
		if (this.layout != null)
			this.layout.removeListener(this);
		this.layout = layout;
		
		layout.addListener(this);
	}
	
	public void updateLinkOpacity(float previousOpacity, float aboveOpacity) {	
		linkOpacity.set(layout.getLinkOpacity(previousOpacity, aboveOpacity));
	}
	
	public void updateLinkOpacity(float linkOpacity) {
		this.linkOpacity.set(linkOpacity);
	}
	
	public double getX(int nodeIndex) {
		return x_coords.get(nodeIndex).get();
	}

	public double getY(int nodeIndex) {
		return y_coords.get(nodeIndex).get();
	}
	
	public boolean changed() {
		return flag.changed;
	}

	public void copyGraphToAnimatedFloats() {
		Vector<Node> nodes = graph.getNodes();
		int count = nodes.size();
		ensureCapacity(count);
		for (int i=0; i<count; i++) {
			x_coords.get(i).set(nodes.get(i).getX());
			y_coords.get(i).set(nodes.get(i).getY());
		}
	}
	
	public void directCopyGraphToAnimatedFloats() {
		if (graph == null) return;
		Vector<Node> nodes = graph.getNodes();
		int count = nodes.size();
		ensureCapacity(count);
		for (int i=0; i<count; i++) {
			x_coords.get(i).directSet(nodes.get(i).getX());
			y_coords.get(i).directSet(nodes.get(i).getY());
		}
	}
	
	public void directCopyToAnimatedFloats(int nodeIndex) {
		Vector<Node> nodes = graph.getNodes();
		x_coords.get(nodeIndex).directSet(nodes.get(nodeIndex).getX());
		y_coords.get(nodeIndex).directSet(nodes.get(nodeIndex).getY());
	}

	protected void copyAnimatedFloatsToGraph() {
		Vector<Node> nodes = graph.getNodes();
		int count = nodes.size();
		for (int i=0; i<count; i++) {
			nodes.get(i).setX(x_coords.get(i).get());
			nodes.get(i).setY(y_coords.get(i).get());
		}
	}

	protected void ensureCapacity(int size) {
		int add = size - x_coords.size();
		for (int i=0; i<add; i++) {
			x_coords.add(new AnimatedFloat(0, slow ? 0f : 0.15f, slow ? 0.015f : 0.002f, flag));
			y_coords.add(new AnimatedFloat(0, slow ? 0f : 0.15f, slow ? 0.015f : 0.002f, flag));
		}
	}
	
	
	boolean slow = false;
	public void setSlow(boolean slow) {
		if (slow != this.slow) {
			this.slow = slow;
			updateAnimatedFloatSpeed();
		}
	}	
	public void updateAnimatedFloatSpeed() {
		for (AnimatedFloat af : x_coords)
			af.setIncrement(slow ? 0f : 0.15f, slow ? 0.015f : 0.002f);
		for (AnimatedFloat af : y_coords)
			af.setIncrement(slow ? 0f : 0.15f, slow ? 0.015f : 0.002f);
	}
	
	@Override
	public void layoutStarted() {
		copyGraphToAnimatedFloats();
	}
	@Override
	public void layoutChanged() {
		copyGraphToAnimatedFloats();
	}
	@Override
	public void layoutStopped() {
		copyGraphToAnimatedFloats();
	}
	@Override
	public void layoutFinished() {
		copyGraphToAnimatedFloats();
	}

	public float getLinkOpacity() {
		return (float)linkOpacity.get();
	}
	
	public float directGetLinkOpacity() {
		return (float)linkOpacity.directGet();
	}

	public void setLinkOpacity(float linkOpacity) {
		this.linkOpacity.set(linkOpacity);
	}

	public Flag getFlag() {
		return flag;
	}
	
}
