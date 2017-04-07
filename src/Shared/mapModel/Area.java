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
import dataTypes.AreaType;
import dataTypes.CoordinateRect;






public class Area extends Element {

	private String name;
	
	private List<Node> nodes = null;
	private AreaType areatype;
	
	public Area(String name, List<Node> nodes) {
		if (nodes instanceof ArrayList) {
			this.nodes = nodes;
		} else {
			this.nodes = new ArrayList<Node>(nodes);
		}
		this.name = name;
	}
	
	public void setType(AreaType type) {
		this.areatype = type;
	}
	
	public String getName() {
		return name;
	}
	
	public AreaType getType() {
		return areatype;
	}
	
	public List<Node> getNodes() {
		if (nodes == null)
			return Collections.emptyList();
		return nodes;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof Area))
			return false;
		
		Area other = (Area)o;
		//name
		if (this.name != other.name) {
			if (this.name == null ? other.name != null : this.name.length() != other.name.length()) {
				return false;
			}
			
			if (!this.name.equals(other.name)) {
				return false;
			}
		}
		//type
		if (this.areatype != other.areatype) {
			return false;
		}
		//nodes
		if (this.nodes != other.nodes) {
			if (this.nodes == null ? other.nodes != null : this.nodes.size() != other.nodes.size()) {
				return false;
			}
			if (!this.nodes.equals(other.nodes)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if the area intersects the given frame
	 */
	@Override
	public boolean isIntersect(CoordinateRect frame) {
		if (nodes == null) {
			return false;
		}
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
		//TODO tile is inside Area
		return false;
	}
	
}
