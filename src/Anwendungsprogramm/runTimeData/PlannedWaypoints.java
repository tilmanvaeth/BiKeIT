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

package runTimeData;

import java.util.ArrayList;
import java.util.List;

import mapModel.Node;


public class PlannedWaypoints {

	List<Node> waypoints;
	
	public PlannedWaypoints() {
		waypoints = new ArrayList<Node>(2);
	}
	
	public void addWaypoint(Node node) {
		waypoints.add(node);
	}
	
	public void deleteWaypoint(int index) {
		waypoints.remove(index);
	}
	
	public void moveWaypoint(int index, Node dest) {
		waypoints.set(index, dest);
	}
	
	public List<Node> getWaypoints() {
		return waypoints;
	}
	
	public Node getWaypoint(int index) {
		return waypoints.get(index);
	}
	
	public void reset() {
		waypoints.clear();
	}
	
	public int getSize() {
		return waypoints.size();
	}
}
