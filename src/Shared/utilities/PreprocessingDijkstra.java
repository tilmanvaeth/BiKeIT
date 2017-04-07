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

package utilities;



import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import mapModel.Edge;
import mapModel.Node;

public class PreprocessingDijkstra {
	
	private PriorityQueue<Node> queue;
	private HashMap<Node, NodeParameters> visited;
	
	private static class NodeParameters {
		
		private Edge e;
		private float distance;
		
		public NodeParameters(Edge e, float distance) {
			this.e = e;
			this.distance = distance;
		}
		
		public Edge getEdge() {
			return e;
		}
		
		public float getDistance() {
			return distance;
		}
		
	}
	
	public void computeArcFlags(Node target, List<Node> nodes) {
		
		visited = new HashMap<Node, NodeParameters>();	
		
		queue = new PriorityQueue<Node>(nodes.size(), new Comparator<Node>() {	
			
			/* relax the edges with the lowest total distance at highest priority */
			
			@Override
			public int compare(Node arg0, Node arg1) {	
				
				if(visited.get(arg0).getDistance() < visited.get(arg1).getDistance()){
					return -1;
				} else if(visited.get(arg0).getDistance() > visited.get(arg1).getDistance()) {
					return 1;
				} else {
					return 0;
				}
				
			}
			
		});
		
		visited.put(target, new NodeParameters(null, 0));
		queue.add(target);
		
		Node current;
		
		while (!queue.isEmpty()) {
			
			/* retrieve and delete the head of the queue */
			current = queue.poll();
			
			for (Edge e : current.getIncomingEdges()) {
				
				if(!e.getWay().isTrafficable()) {
					// the edge isn't trafficable with a bicycle
					continue;
				}
				
				Node source = e.getSource();
				float targetDistance = visited.get(current).getDistance();
				float alternativeDistance = targetDistance + e.getQuantifiedLength();
				
				if (!visited.containsKey(source)) {
					visited.put(source, new NodeParameters(e, Float.POSITIVE_INFINITY));					
				}
				
				NodeParameters sourceParameters = visited.get(source);
				
				if (alternativeDistance < sourceParameters.getDistance()) {
					visited.put(source, new NodeParameters(e, alternativeDistance));
					queue.remove(source);
					queue.add(source);
				}
				
			}
			
		}
		
		writeArcFlags(target, nodes);
		
	}
	
	private void writeArcFlags(Node target, List<Node> nodes) {
		
		byte region = target.getRegion();
		
		for (Entry<Node, NodeParameters> entry : visited.entrySet()) {
			
			Edge e = entry.getValue().getEdge();
			if (e!=null) {
				e.setFlag(region);
			}
			
		}
		
	}
	
}
