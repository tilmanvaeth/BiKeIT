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

package mapModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dataTypes.Coordinate;
import dataTypes.CoordinateRect;


public class Node extends Element{
	
	private int id; 
	private short altitude;
	private byte region;
	private float latitude;
	private float longitude;
	
	/*
	 * preprocessing program:	incoming edges
	 * user program:			outgoing edges
	 */
	private List<Edge> edges = null;
//	private List<Edge> incomingEdges = new ArrayList<Edge>(10);
	
	public Node(Coordinate position, int id) {
		this.latitude = position.getLatitude();
		this.longitude = position.getLongitude();
		this.id = id;
	}
	
	@Override 
	public boolean equals(Object o) {
		if (!(o instanceof Node))
			return false;
		
		Node other = (Node)o;
		if (other.altitude != this.altitude)
			return false;
		
		if (other.id != this.id)
			return false;
		
		if (other.latitude != this.latitude)
			return false;
		
		if (other.longitude != this.longitude)
			return false;
		
		if (other.region != this.region) 
			return false;
		
		if (!this.getEdges().equals(other.getEdges())) {
			return false;
		/*
		if (this.edges != null) {
			if (other.edges == null)
				return false;
			
			if (this.edges.size() != other.edges.size())
				return false;
			
			
			
			for (Edge e1 : this.edges) {
				boolean found = false;
				for (Edge e2 : other.edges) {
					if (e1.equals(e2)) {
						found = true;
						break;
					}
				}
				if (!found)
					return false;
			}*/
		}
		return true;
	}
	
	public void setAltitude(short altitude) {
		this.altitude = altitude;
	}

	public void setRegion(byte region) {
		this.region = region;
	}

	/*
	 * Only for the user program!
	 */
	public void addEdge(Edge edge) {
		if (edges == null) {
			edges = new ArrayList<Edge>();
		}
		edges.add(edge);
	}

	/*
	 * Only for the preprocessing program!
	 */
	public void addIncomingEdge(Edge edge) {
		if (edges == null) {
			edges = new ArrayList<Edge>();
		}
		edges.add(edge);
	}
	
	public int getId() {
		return id;
	}

	public Coordinate getPosition() {
		return new Coordinate(this.latitude, this.longitude);
	}

	public short getAltitude() {
		return altitude;
	}

	/*
	 * Only for the user program!
	 */
	public List<Edge> getEdges() {
		if (edges == null)
			return Collections.emptyList();
		return edges;
	}

	/*
	 * Only for the preprocessing program!
	 */
	public List<Edge> getIncomingEdges() {
		if (edges == null)
			return Collections.emptyList();
		return edges;
	}

	public byte getRegion() {
		return region;
	}
	
	public float getLatitude() {
		return this.latitude;
	}
	
	public float getLongitude() {
		return this.longitude;
	}
	
	/**
	 * Check if the node intersects the given frame
	 */
	@Override
	public boolean isIntersect(CoordinateRect frame) {
		return frame.isContains(this.getPosition());
	}
	
	public void trim() {
		if (edges == null)
			return;
		
		((ArrayList<Edge>)edges).trimToSize();
	}
	
	public boolean hasEdgeToNode(Node tar) {
		if (edges == null)
			return false;
		for (Edge e: edges) {
			if (e.getTarget() == tar) return true;
		}
		return false;
	}
	
	public void removeEdge(Edge e) {
		edges.remove(e);
	}
}
