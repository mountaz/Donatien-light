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

import java.awt.Color;

public class MasterLayer extends Layer {

	public MasterLayer() {
		super();
		bar = new MasterLayerBar(this);
		setTitle("New Master Graph");
		enabled = false;
		setVisible(true);
		setGraph(null);		
	}
	
	public static Color getDefaultBaseColor() {
		return new Color(0.85f, 0.85f, 0.85f);
	}
	
}
