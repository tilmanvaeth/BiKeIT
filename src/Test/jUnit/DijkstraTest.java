/*
 * BiKeIT - ein Routenplaner für Fahrradfahrer
 * Copyright (C) 2011-2012 Sven Esser, Manuel Fink, Thomas Keh,
 *                         Tilman Väth, Lukas Vojković, Fabian Winnen
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jUnit;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import mapModel.Edge;
import mapModel.Node;
import mapModel.Way;

import org.junit.Before;
import org.junit.Test;

import utilities.Dijkstra;

import dataTypes.Coordinate;
import dataTypes.WayType;


public class DijkstraTest {

		private Node a;
		private Node b;
		private Node c;
		private Node d;
		private Node e;
		private Node f;
		private Node g;
		private Node z;
		
		private Way w;
		private Way w2;
		private Way w3;
		
		private Edge e1;
		private Edge e2;
		private Edge e3;
		private Edge e4;
		private Edge e5;
		private Edge e6;
		private Edge e7;
		private Edge e8;
		private Edge e9;
		private Edge e10;
		
		private List<Edge> eList;
		private List<Edge> compare;
		
		@Before
		public void setUp() throws Exception {		
			a = new Node(new Coordinate((float)49.005504, (float)8.403461), 0);
			b = new Node(new Coordinate((float)49.002154, (float)8.403161), 1);
			c = new Node(new Coordinate((float)49.005954, (float)8.410199), 2);
			d = new Node(new Coordinate((float)49.002998, (float)8.424103), 3);
			e = new Node(new Coordinate((float)48.996185, (float)8.410628), 4);
			f = new Node(new Coordinate((float)48.987823, (float)8.412703), 5);
			g = new Node(new Coordinate((float)49.005950, (float)8.410199), 6);
			z = new Node(new Coordinate((float)49.005404, (float)8.410199), 7);
			
			a.setRegion((byte)1);
			b.setRegion((byte)1);
			c.setRegion((byte)1);
			d.setRegion((byte)1);
			e.setRegion((byte)2);
			f.setRegion((byte)1);
			g.setRegion((byte)1);
			
			w = new Way();
			w.setType(WayType.CYCLEWAY_T);
			
			w2 = new Way();
			w2.setType(WayType.RAILWAY);
			
			w3 = new Way();
			w3.setType(WayType.CITYANDCYCLE_B_NT);
			
			w.addNode(a);
			w.addNode(b);
			w.addNode(c);
			w.addNode(d);
			w.addNode(e);
			w.addNode(g);
			
			w2.addNode(f);
			
			w3.addNode(z);
			
			
			e1 = new Edge(a, b, w);
			e1.setFlag(a.getRegion());
			e1.setFlag(e.getRegion());
			
			e2 = new Edge(a, c, w);
			e2.setFlag(a.getRegion());
			e2.setFlag(e.getRegion());
			
			e3 = new Edge(c, b, w);
			e3.setFlag(c.getRegion());;
			
			e4 = new Edge(b, c, w);
			e4.setFlag(b.getRegion());
			
			e5 = new Edge(c, e, w);
			e5.setFlag(c.getRegion());
			e5.setFlag(e.getRegion());
			
			e6 = new Edge(b, d, w);
			e6.setFlag(b.getRegion());
			e6.setFlag(e.getRegion());
			
			e7 = new Edge(d, e, w);
			e7.setFlag(d.getRegion());
			
			e8 = new Edge(a, f, w2);
			
			e9 = new Edge(b, g, w);
			e9.setFlag(b.getRegion());
			e9.setFlag(e.getRegion());
			
			e10 = new Edge(c, g,w);
			e10.setFlag(c.getRegion());
			e10.setFlag(e.getRegion());

			a.addEdge(e1);
			a.addEdge(e2);
			c.addEdge(e3);
			c.addEdge(e5);
			c.addEdge(e10);
			b.addEdge(e4);
			b.addEdge(e6);
			b.addEdge(e9);
			d.addEdge(e7);	
			a.addEdge(e8);
			
			compare = new LinkedList<Edge>();
			
			compare.add(e2);
			compare.add(e5);
		}

		@Test
		public void testOneNode() {
			Dijkstra dijk = new Dijkstra(); 		
			/* Node z has no outgoing edges*/
			assertNull(dijk.findQualifiedPath(z, e));
		}
		
		@Test
		public void testInstanceFindQualifiedPath() {
			Dijkstra myD = new Dijkstra();
			eList = myD.findQualifiedPath(a, e);
			assertTrue(compare.equals(eList));
		}

}
	
