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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

// une cellule est une zone autonome du point de vue de son layout et qui s'insère dans le layout d'autres cell grace à une transformation
public class Cell extends Rectangle2D.Double{
	private boolean on = false;
	private int nbX, nbY; //nb de lignes et de colonnes, ou nb cercles et cartiers de cercles
	private double padX,padY;
	private double marginX=10,marginY=10;
	
	public double getPadX() {
		return padX;
	}
	public double getMarginX() {
		return marginX;
	}
	public double getMarginY() {
		return marginY;
	}
	public void setPadX(double padX) {
		this.padX = padX;
	}
	public double getPadY() {
		return padY;
	}
	public void setPadY(double padY) {
		this.padY = padY;
	}
	private ArrayList<Node> nodes = new ArrayList<Node>();

	public Cell (double x, double y, double w, double h){
		super(x, y, w, h);
	}
	public Cell (double x, double y, double w, double h, int nbX,int nbY){
		super(x, y, w, h);
		this.nbX = nbX;
		this.nbY = nbY;
		this.padX = w/(double) nbX;
		this.padY = h/(double) nbY;	
	}
	
	
	public boolean isOff(){
		return !on;
	}
	
	public double getPad(char a){
		switch (a){
			case 'x': if (nbX!= 0) return width/nbX;
			case 'y': if (nbY!= 0) return height/nbY;
			default: return 0;
		}
	}
	
	public Point2D.Double getRadius(){
		Point2D.Double P = new Point2D.Double(width /(2*nbX)-marginX, height/(2*nbY)-marginY);		
		return P;
	}
	public Point2D.Double getMaxValue(){
		if (nbX ==0){nbX = 10;}
		if (nbX ==0){nbY = 10;}
		
		Point2D.Double P = new Point2D.Double((double) nbX, (double) nbY);		
		return P;
	}
	public double getAngle(){
		double alpha = (2*Math.PI)/nbY;		
		return alpha;
	}
	
	public boolean isOn(){
		return on;
	}
	public int getNbX() {
		return nbX;
	}
	public int getNbY() {
		return nbY;
	}
	
}
