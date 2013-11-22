/* GraphML parser used to parse GraphML graphs
 * Copyright (C)2009 Guillaume Artignan
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
package donatien.file_io.parsers;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import donatien.model.graph.Edge;
import donatien.model.graph.Graph;
import donatien.model.graph.Node;
import donatien.model.graph.PropertyModifiable;


public class GraphMLParser extends Parser
{

	public Graph parse(Reader r) throws IOException 
	{		
		XMLParser parser = new XMLParser();
		return parser.parse(new InputSource(r));		
	}
	
	
	private class XMLParser extends DefaultHandler{
		
		/** Graph constructed */
		private Graph gph;

		private Stack<Graph> st_graph = new Stack<Graph>();
		private Stack<Node> st_node = new Stack<Node>();
		private HashMap<String, Node> nodes = new HashMap<String, Node>();
		
		private Vector<HashMap<String,String>> edges = new Vector<HashMap<String,String>>();
		private Node root;
		
		public Graph parse(InputSource in){
			root = new Node("");
			st_node.push(root);
			SAXParser parser;
			SAXParserFactory factory = SAXParserFactory.newInstance();
			try {
				parser = factory.newSAXParser();
				parser.parse(in, this);
			}
			 	catch (SAXException e) {e.printStackTrace();} 	
				catch (IOException e) {e.printStackTrace();} 
				catch (ParserConfigurationException e) {e.printStackTrace();}
				
			for (HashMap<String,String> att : edges){
				createEdge(att);
			}
			
			return gph;
		}
		
		public Graph parse(InputSource in, boolean clusters){
			root = new Node("");
			st_node.push(root);
			SAXParser parser;
			SAXParserFactory factory = SAXParserFactory.newInstance();
			try {
				parser = factory.newSAXParser();
				parser.parse(in, this);
			}
			 	catch (SAXException e) {e.printStackTrace();} 	
				catch (IOException e) {e.printStackTrace();} 
				catch (ParserConfigurationException e) {e.printStackTrace();}
			/*
			for (HashMap<String,String> att : edges){
				createEdge(att);
			}
			*/
			return gph;
		}
		
		private void createEdge(HashMap<String, String> atts) {

			String source = atts.get("source");
			String target = atts.get("target");
			
			Node n_src = nodes.get(source);
			Node n_trg = nodes.get(target);
			
			Edge e = new Edge(atts.get("id"),n_src,n_trg);
			System.out.println(n_src + " - "+ n_trg);
		
				
				n_src.getNeighbour().add(e);
				if (n_src!=n_trg){
					n_trg.getNeighbour().add(e);
				}
				
			affectPropertyModifiable(e, atts,"id","source","target");

		}

		private void affectPropertyModifiable(PropertyModifiable pm, HashMap<String, String> atts,String...unless) 
		{

			String keys[] = atts.keySet().toArray(new String[0]);
			for (int i=0;i<keys.length;i++){
				String key = keys[i];
				String val = atts.get(key);

				if (!existsIn(key, unless))
					pm.putProp(key, val);
			}		
		}

		/**
		 * Method invoked when the parser meet a start element.
		 */
		public void startElement(String namespaceURI, String localName, String name, Attributes atts) throws SAXException {
			if (name.equalsIgnoreCase("graph"))
			{
				st_graph.push(createGraph(atts));		
			}
			else if (name.equalsIgnoreCase("node")){
				Graph g = st_graph.peek();
				createNode(g,atts);
			}
			else if (name.equalsIgnoreCase("edge")){
				createEdge(atts);
			}
		}
		
		private void createEdge(Attributes atts) {
			String source = atts.getValue("source");
			String target = atts.getValue("target");
			
			Node n_src = nodes.get(source);
			Node n_trg = nodes.get(target);
			
			if (n_src==null || n_trg==null)
			{
				edges.add(copy(atts));
			}
			else
			{
				Edge e = new Edge(atts.getValue("id"),n_src,n_trg);
		
				
				n_src.getNeighbour().add(e);
				if (n_src!=n_trg)
				{
					n_trg.getNeighbour().add(e);
				}
				
				affectPropertyModifiable(e, atts,"id","source","target");
			}
		}

		private void createNode(Graph g, Attributes atts) {
			
			Node n = new Node(atts.getValue("id"));		
			affectPropertyModifiable(n,atts,"id");
			nodes.put(n.getId(),n);
			g.addNode(n);
			st_node.push(n);
			
		}

		private Graph createGraph(Attributes atts) {
			Graph g = new Graph();		
			affectPropertyModifiable(g,atts);
			Node parent = st_node.peek();
			if(parent!=root)
			{	
				g.setParentNode(parent);
				parent.setGraphContent(g);
			}
			return g;
		}
		
		private HashMap<String,String> copy(Attributes atts) {
			HashMap<String,String> map = new HashMap<String, String>();
			
			for (int i=0;i<atts.getLength();i++)
			{
				String val = atts.getValue(i);
				String key = atts.getQName(i);
//				System.out.println("--------------"+key);
				map.put(key, val);
			}	
			
			return map;
		}



		private void affectPropertyModifiable(PropertyModifiable pm, Attributes atts,String...unless) {
			for (int i=0;i<atts.getLength();i++)
			{
				String val = atts.getValue(i);
				String key = atts.getQName(i);
				
				if (!existsIn(key, unless))
					pm.putProp(key, val);
			}		
		}
		
		private boolean existsIn(String s, String[] unless){
			for (int i=0;i<unless.length;i++)
			{
				if (s.equalsIgnoreCase(unless[i]))
					return true;
			}
			
			return false;
		}

		/**
		 * Method invoked when the parser meet an end element.
		 */
		public void endElement(String namespaceURI, String localName, String name) throws SAXException {

			if (name.equalsIgnoreCase("graph")){
				gph = st_graph.pop();		
			}
			else if (name.equalsIgnoreCase("node"))
			{
				st_node.pop();
			}
		}
	}
	
	
}
