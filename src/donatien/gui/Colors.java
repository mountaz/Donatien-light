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

public class Colors {

	static Color[] colors = new Color[] {
		new Color(58, 46, 133),
		new Color(193,0,135),
		new Color(51, 204, 205),
		new Color(255,128,0),
		new Color(157, 0,25),
		new Color(59, 218, 0),
		new Color(205, 0, 116),
	};
	
	public static Color getPredefinedColor(int i) {
		return colors[i % colors.length];
	}
}
