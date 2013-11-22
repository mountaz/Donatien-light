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

import java.util.ArrayList;
import java.util.Vector;


public class DistanceMatrix<T> {

	ArrayList<T> columns;
	ArrayList<T> rows;
	double[][] distances;
	double maxDistance = 0;
	boolean forceSymmetry = false;

	public DistanceMatrix(ArrayList<T> columns, ArrayList<T> rows) {
		this.columns = columns;
		this.rows = rows;
		distances = new double[columns.size()][rows.size()];
	}
	
	public DistanceMatrix(Vector<T> columns, Vector<T> rows) {
		this(new ArrayList<T>(columns), new ArrayList<T>(rows));
	}
		
	public ArrayList<T> getColumns() {
		return columns;
	}

	public ArrayList<T> getRows() {
		return rows;
	}
	
	public int numColumns() {
		return columns.size();
	}
	
	public int numRows() {
		return rows.size();
	}
	
	/**
	 * Warning: slow for large matrices. Use indices when possible.
	 */
	public void setDistance(T column, T row, double distance) {
		setDistance(columns.indexOf(column), rows.indexOf(row), distance);
	}
	
	public void setDistance(int column, int row, double distance) {
		distances[column][row] = distance;
		if (forceSymmetry & row != column)
			distances[row][column] = distance;
		if (distance > maxDistance) {
			maxDistance = distance;
		}
	}
	
	/**
	 * Warning: slow for large matrices. Use indices when possible.
	 */
	public double getDistance(T column, T row) {
		return getDistance(columns.indexOf(column), rows.indexOf(row));
	}
	
	public double getDistance(int column, int row) {
		return distances[column][row];
	}

	public boolean isForceSymmetry() {
		return forceSymmetry;
	}

	public void setForceSymmetry(boolean forceSymmetry) {
		this.forceSymmetry = forceSymmetry;
	}
	
	public void normalize() {
		for (int c=0; c<columns.size(); c++)
			for (int r=0; r<rows.size(); r++)
				distances[c][r] /= maxDistance;
		maxDistance = 1;
	}
}
