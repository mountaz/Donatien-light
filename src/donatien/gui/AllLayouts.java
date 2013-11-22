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

import java.util.ArrayList;
import java.util.HashMap;

import donatien.layout.EllipseProjectionLayout;
import donatien.layout.HoleLayout;
import donatien.layout.Layout;
import donatien.layout.MasterAlignLayout;
import donatien.layout.MultilevelBarycentricLayout;

public class AllLayouts {

	static ArrayList<String> allLayoutNames = new ArrayList<String>(); // remember ordering
	static ArrayList<Integer> separators = new ArrayList<Integer>();
	static HashMap<String, Layout> allLayouts = new HashMap<String, Layout>();
	
	private static String defaultLayout = null;
	public static String getDefaultLayout() {
		return defaultLayout;
	}
	public static void setDefaultLayout(String defaultLayout) {
		Layout layout = allLayouts.get(defaultLayout);
		if ( !(layout instanceof MasterAlignLayout) && !(layout instanceof EllipseProjectionLayout) && !(layout instanceof MultilevelBarycentricLayout) && !(layout instanceof HoleLayout)) // FIXME -- add a isValidDefaultLayout() method in Layout class 
			AllLayouts.defaultLayout = defaultLayout;
	}
}
