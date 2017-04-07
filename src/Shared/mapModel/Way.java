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

import utilities.Geometry;
import dataTypes.CoordinateRect;
import dataTypes.WayType;


public class Way extends Element {

	private String name = "";
	private boolean isOneway = false;
	private List<Node> nodes = null;
	private WayType type;
	
	public boolean equals(Object o) {
		if (!(o instanceof Way))
			return false;
		
		Way other = (Way)o;
		
		if (this.isOneway != other.isOneway) 
			return false;
		
		if (this.type != other.type)
			return false;
		
		if (!this.name.equals(other.name))
			return false;
		
		if (this.nodes != null) {
			if (other.nodes == null)
				return false;
			
			if (this.nodes.size() != other.nodes.size())
				return false;
			
			for (Node n1 : this.nodes) {
				boolean found = false;
				for (Node n2 : other.nodes) {
					if (n1.equals(n2)) {
						found = true;
						break;
					}
				}
				if (!found)
					return false;
			}
		}
		return true;
	}

	public void setNodes(List<Node> nodes) {
		if (nodes instanceof ArrayList)
			this.nodes = nodes;
		else 
			this.nodes = new ArrayList<Node>(nodes);
	}
	
	public void setName(String n) {
		this.name = n;
	}

	public void setType(WayType w) {
		type = w;
	}

	public void setOneway(boolean b) {
		this.isOneway = b;
	}
	
	public void addNode(Node n) {
		if (nodes == null) {
			nodes = new ArrayList<Node>();
		}
		nodes.add(n);
	}

	public String getName() {
		return name;
	}
	
	public WayType getType() {
		return type;
	}

	public boolean isOneway() {
		return isOneway;
	}
	
	public List<Node> getNodes() {
		if (nodes == null)
			return Collections.emptyList();
		return nodes;
	}
	
	public boolean isTrafficable() {
		return type.isTrafficable();
	}


	@Override
	public boolean isIntersect(CoordinateRect frame) {
		if (nodes == null)
			return false;
		for (int i = 0; i < nodes.size() - 1; i++) {
			float p1x = nodes.get(i).getLongitude();
			float p1y = nodes.get(i).getLatitude();

			float p2x = nodes.get(i+1).getLongitude();
			float p2y = nodes.get(i+1).getLatitude();
			
			boolean intersects =  Geometry.isLineIntersectingRectangle(p1x, p1y, p2x, p2y,
					frame.getUL().getLongitude(), frame.getUL().getLatitude(),
					frame.getLR().getLongitude(), frame.getLR().getLatitude());
			
			if (intersects)
				return true;
		}
		return false;
	}
	
}
