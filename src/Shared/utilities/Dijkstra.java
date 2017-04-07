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



import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import mapModel.Edge;
import mapModel.Node;

public class Dijkstra {
	
	private PriorityQueue<Node> queue;
	private HashMap<Node, NodeParameters> visited;
	
	public Dijkstra() {
		visited = new HashMap<Node, NodeParameters>();	
		queue = new PriorityQueue<Node>(1, new Comparator<Node>() {	
			/* compare function for the add method of the priorityqueue*/
			@Override
			public int compare(Node arg0, Node arg1) {				
				if(visited.get(arg0).getDistance() < visited.get(arg1).getDistance()){
					return -1;
				} 
				else if(visited.get(arg0).getDistance() > visited.get(arg1).getDistance()) {
					return 1;
				}
				return 0;
			}
		});
	}
	
	private class NodeParameters {
		
		private Edge edge;
		
		private float distance;
		
		public NodeParameters(Edge edge, float distance) {
			this.edge = edge;
			this.distance = distance;
		}
		
		public Edge getEdge() {
			return edge;
		}
		
		public float getDistance() {
			return distance;
		}
	}
	
	public List<Edge> findQualifiedPath(Node start, Node target) {
		
		/* key is the node itself value is the parent node */
		visited.put(start, new NodeParameters(null, 0));
		queue.add(start);
		
		Node current;
		
		while (!queue.isEmpty()) {
			
			/* retrieve and delete the head of the queue */
			current = queue.poll();
			
			if(current.equals(target)) {
				break;
			}
			
			for(Edge e : current.getEdges()) {
				
				if(!e.getWay().isTrafficable()) {
					// the edge isn't trafficable with a bicycle
					continue;
				}
				
				if (e.getFlags() != 0 && !e.getFlag(target.getRegion())) {
					/* if the flag(s) is 0 the arc flags haven't been
					 * calculated for this route data, because each edge
					 * has it's own region flag set.
					 * 
					 * if flag isn't set for this region
					 * continue with next element(edge) of this for loop 
					 */
					continue;
				}

				Node connectedNode = e.getTarget();
				
				if(!visited.containsKey(connectedNode)) {
					visited.put(e.getTarget(), new NodeParameters(e, Float.POSITIVE_INFINITY));
				}

				float alternativeDistance = visited.get(current).getDistance() + e.getQuantifiedLength();
				
				if (alternativeDistance < visited.get(connectedNode).getDistance()) {
					
					visited.put(connectedNode, new NodeParameters(e, alternativeDistance));
					queue.remove(connectedNode);
					queue.add(connectedNode);
					
				}
				
			}
			
		}
		
		if(visited.containsKey(target)) {
			return createEdgeList(start, target);
		}
		
		/* the target node is not reachable from the source node*/
		return null;
		
	}
	
	private List<Edge> createEdgeList(Node start, Node target) {
		
		List<Edge> path = new LinkedList<Edge>();
		
		/* create the edge list for the return in reverse order*/
		Node current = target;
		Edge edge;
		
		// it's an endless loop if the target isn't reachable...
		// but shouldn't happen...
		while(!current.equals(start)) {
			edge = visited.get(current).getEdge();
			path.add(edge);
			current = edge.getSource();
		}
		
		/* corrects the order of the list */
		Collections.reverse(path);
		return path;
		
	}
	
}
