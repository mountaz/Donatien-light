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
package donatien;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.MalformedURLException;

import donatien.file_io.exception.NotSupportedFormatException;
import donatien.gui.MainWindow;
import donatien.layout.EllipseProjectionLayout;
import donatien.layout.HoleLayout;
import donatien.layout.LabelLayout;
import donatien.layout.LinLogLayout;
import donatien.layout.MasterAlignLayout;
import donatien.layout.MultilevelBarycentricLayout;
import donatien.layout.PolarLayout;
import donatien.layout.PubliLayout;
import donatien.layout.RandomLayout;
import donatien.layout.SimpleLayout;
import donatien.layout.SimplePolarLayout;

public class Main {

	public static void main(String[] args) throws MalformedURLException, IOException, NotSupportedFormatException 	{
				
		// Create the main window
		MainWindow win = new MainWindow();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension maxdim = new Dimension(1280, 1024);
		Dimension winsize = new Dimension(Math.min(screen.width, maxdim.width), Math.min(screen.height, maxdim.height));
		win.setBounds((screen.width - winsize.width)/2, (screen.height - winsize.height)/2, winsize.width, winsize.height);
		
		// Graphs to test
		win.addGraphFiles("Data/");
		
		// Layout algorithms to test.
		win.addLayout("Random", new RandomLayout());
		win.addLayoutSeparator();
		win.addLayout("Simple", new SimpleLayout());
		win.addLayout("SimplePolar", new SimplePolarLayout());
		win.addLayout("Polar", new PolarLayout());
		win.addLayout("Alpha", new LabelLayout());
		win.addLayout("Pub Rings", new PubliLayout());
		win.addLayoutSeparator();
		win.addLayout("Noack linlog", new LinLogLayout());
		win.addLayoutSeparator();
		win.addLayout("Push around", new HoleLayout());
		win.addLayout("Multilevel Barycentric", new MultilevelBarycentricLayout());
		win.addLayout("Ellipse projection", new EllipseProjectionLayout());
		win.addLayout("Master align", new MasterAlignLayout());
		win.setDefaultLayout("Noack linlog"); // default layout

		// Show the main window
		win.setVisible(true);
	}
}
