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
import java.util.ArrayList;

public class Stamp implements Comparable{
	public ArrayList<Point2D.Double> stamps = new ArrayList<Point2D.Double>();
	public int card = 1;
	
	@Override
	public String toString(){
		String s="";
		int i = 0;
		for (Point2D.Double point: stamps){
			s+=(int)point.getX();
			s+="-";
			s+=(int)point.getY();
			s+="-";
		}
		s+=card;
		s+="-";
		return s;
	}
	@Override
	public boolean equals(Object o){
		if (!(o instanceof Stamp)) 
			return false;
		Stamp s = (Stamp)o;
		int n1 = s.stamps.size();
		int n2 = stamps.size();
		if (n1 != n2) return false;
		for (int i = 0;i<n1;i++){
//			System.out.println("Compare stamp = "+get(i)+" "+ s.get(i));
			if (get(i).getX() != s.get(i).getX() && get(i).getY() != s.get(i).getY()){
				return false;
			}
		}
		return true;
	}
	
	public int size(){
		return stamps.size();
	}
	
	public Point2D.Double get(int i){
		if (i < stamps.size()){
			return stamps.get(i);
		}else{
			return null;
		}
	}
	
	public void set(int i, Point2D.Double p){
		stamps.set(i,p);
	}
	
	public void add(int i, Point2D.Double p){
		stamps.add(i,p);
	}

	// attention l'ordre de l'empreinte est important
	@Override
	public int compareTo(Object o) {
		if (!(o instanceof Stamp))
			return +1;
		Stamp s = (Stamp)o;
		int n = stamps.size();
		for (int i = 0; i<n;i++){
			if (this.get(i).getX()>s.get(i).getX())return +1; 
			else if (this.get(i).getX()<s.get(i).getX()) return -1;
			if (this.get(i).getY()>s.get(i).getY())return +1; 
			else if (this.get(i).getY()<s.get(i).getY()) return -1;
		}
		return 0;
	}
}

