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
import java.awt.geom.Point2D;

import donatien.gui.Layer;
import donatien.gui.MasterLayer;
import donatien.gui.View;
import donatien.model.graph.MasterGraph;
import donatien.model.graph.Node;
import donatien.util.Pair;

public class MasterAlignLayout extends Layout {

	Layer layer;
	MasterLayer master;
	
	public MasterAlignLayout() {
		super();
	}
	
	@Override
	protected void init() {
		// Update master graph
		View view = layer.getParent();
		master = view.getActiveMasterLayer();
	}

	@Override
	protected boolean step() {
		
		if (master == null || layer == null)
			return true;

		// Update nodes coordinates
		if (layer instanceof MasterLayer)
			alignMasterToSlaves();
		else
			alignSlavesToMaster();
		
		// Finished.
		return true;		
	}
	
	protected void alignMasterToSlaves() {
		
		View view = layer.getParent();
		
		Point2D.Double p = new Point2D.Double();
		
		for (Node mn : graph.getNodes()) {
			p.setLocation(0, 0);
			int n = 0;
			for (Pair<Layer,Node> sn : mn.getSlaves()) {
				if (sn.getKey().isVisible()) {
					Point2D sn_pos = view.getPositionInView(sn.getKey(), sn.getValue());
					p.x += sn_pos.getX();
					p.y += sn_pos.getY();
					n++;
				}
			}
			p.x /= n;
			p.y /= n;
			view.setPositionInView(layer, mn, p);
		}
		
		
	}
	
	protected void alignSlavesToMaster() {
		MasterGraph mgraph = (MasterGraph)master.getGraph();
		
		if (mgraph == null)
			return;
		
		View view = layer.getParent();
		for (Node n : graph.getNodes()) {
			Node mn = mgraph.getMasterNode(n);
			Point2D mn_pos = view.getPositionInView(master, mn); 
			view.setPositionInView(layer, n, mn_pos);
		}
	}
	
	public boolean canConfigureLayout(Layer l) {
		return l != null && l.getParent() != null && l.getParent().getActiveMasterLayer() != null;
	}
	
	public void configureLayout(Layer l) {
		layer = l;
	}
	
	@Override
	protected void updateCoordinates() {
	}

}
