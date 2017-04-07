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

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import main.Importer;
import mapModel.Edge;
import mapModel.MapModel;
import mapModel.Node;
import misc.Stopwatch;

import org.junit.Before;
import org.junit.Test;

import utilities.Dijkstra;


public class ArcFlagsTest {
	
	private List<Node> nodes;
	private static final int NUM_TESTS = 500;
	
	private Node getRandomNode() {
		
		boolean isTrafficable = false;
		int rand;
		Node node;
		
		// search a suitable node
		do {
			
			rand = (int) (Math.random()*(nodes.size()-1));
			node = nodes.get(rand);
			
			for (Edge e : node.getEdges()) {
				if (e.getWay().isTrafficable()) {
					isTrafficable = true;
					break;
				}
			}
			
		} while(!isTrafficable);
		
		return node;
		
	}
	
	@Before
	public void prepare() throws Exception {	

		System.out.println("Loading map model...");
		MapModel model = Importer.readMapModelFromDir();
		Importer.disposeSplash();
		nodes = new LinkedList<Node>(model.getNodeData().getAllNodes());
		
	}

	private class Result {
		public Node n1, n2;
		public double length;
		public Result(Node n1, Node n2, double length) {
			this.n1 = n1;
			this.n2 = n2;
			this.length = length;
		}
		@Override
		public boolean equals(Object o) {
			return (o instanceof Result && ((Result)o).length == length);
		}
	}
	
	@Test
	public void testArcFlags() {
		
		long totalTime = 0;
		
		System.out.println("Calculating "+NUM_TESTS+" routes with arc flags...");
		List<Result> results = new LinkedList<Result>();
		
		for (int i=0; i<NUM_TESTS; i++) {
			
			Node start = getRandomNode();
			System.out.println("Randomly picked node no "+start.getId()+" in region "+start.getRegion()+" as source.");

			Node target = getRandomNode();
			System.out.println("Randomly picked node no "+target.getId()+" in region "+target.getRegion()+" as target.");
			
			Stopwatch.start();
			List<Edge> route = (new Dijkstra()).findQualifiedPath(start, target);
			totalTime = Stopwatch.stop();
			
			results.add(new Result(start, target, this.calculateRouteLength(route)));
			
		}

		double averageTimeWithFlags = totalTime*1. / NUM_TESTS;
		System.out.println("Average Dijkstra duration with arc flags: "+averageTimeWithFlags+" ms");
		
		System.out.println("Resetting all arc flags...");
		
		// reset all arc flags
		for (Node node : nodes) {
			for (Edge e : node.getEdges()) {
				e.setFlags(0);
			}
		}
		
		totalTime = 0;
		System.out.println("Calculating the same routes, but this time without arc flags...");
		
		int counter = 1;
		for (Result r : results) {

			// calculate route without arc flags
			Stopwatch.start();
			List<Edge> route = (new Dijkstra()).findQualifiedPath(r.n1, r.n2);
			totalTime = Stopwatch.stop();
			double length = this.calculateRouteLength(route);
			
			if (length == 0 && r.length == 0) {
				System.out.println("Test "+counter+" passed! No route found both times.");
			} else if (length == r.length) {
				System.out.println("Test "+counter+" passed! Route length: "+length+" m");
			} else {
				System.out.println("Test "+counter+" failed!");
				System.out.println("From: "+r.n1.getId()+" in region "+r.n1.getRegion()+" to "+r.n2.getId()+" in region "+r.n2.getRegion());
				System.out.println("Route length with Flags: "+r.length+" m");
				System.out.println("Route length without Flags: "+length+" m");
			}
			
			counter++;
			assertEquals(length, r.length, 0.01d);
			
		}

		double averageTimeWithoutFlags = totalTime*1. / NUM_TESTS;
		System.out.println("Average Dijkstra duration without arc flags: "+averageTimeWithoutFlags+" ms");
		double speedup = averageTimeWithoutFlags / averageTimeWithFlags * 100;
		System.out.println("Speedup: "+speedup+"%");
		
		
	}
	
	private double calculateRouteLength(List<Edge> edges) {
		
		if (edges == null) {
			return 0;
		}
		
		double length = 0;
		
		for (Edge e : edges) {
			length += e.getLength();
		}
		
		return length;
		
	}

}
