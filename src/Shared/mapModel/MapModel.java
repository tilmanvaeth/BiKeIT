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

import java.util.List;
import java.util.Set;

import utilities.MercadorProjection;
import dataTypes.Zoomlevel;



public class MapModel {

	private NodeData nodeData;
	private CityData cityData;
	private ZoomLevelDependentData[] zoomLevelData;
	
	public MapModel(NodeData n, CityData c, ZoomLevelDependentData[] z) {
		this.nodeData = n;
		this.cityData = c;
		this.zoomLevelData = z;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MapModel))
			return false;
		
		MapModel other = (MapModel)o;
		if (!(this.nodeData == null ? other.nodeData == null : this.nodeData.equals(other.nodeData)))
			return false;
		
		if (!(this.cityData == null ? other.cityData == null : this.cityData.equals(other.cityData)))
			return false;
		
		if (!(this.zoomLevelData == null ? other.zoomLevelData == null : other.zoomLevelData != null))
			return false;
		
		if (this.zoomLevelData != null) {
			if(this.zoomLevelData.length != other.zoomLevelData.length)
				return false;
				
		
			for (int i = 0; i < this.zoomLevelData.length; i++) {
				if (!this.zoomLevelData[i].equals(other.zoomLevelData[i]))
					return false;
			}
		}
		return true;
	}
	
	public int getDistinctZoomlevelDatas() {
		return zoomLevelData.length;
	}
	
	public NodeData getNodeData() {
		return nodeData;
	}

	public CityData getCityData() {
		return cityData;
	}
	
	public ZoomLevelDependentData getMapData(int value) {
		return zoomLevelData[value];
	}
	
	public ZoomLevelDependentData getMapDataForZoomlevel(Zoomlevel zoomlevel) {
		int zoom = zoomlevel.getValue();
		for (int i = 0; i < zoomLevelData.length; i++) {
			if (zoom <= zoomLevelData[i].getMaximalZoomlevel()) {
				return zoomLevelData[i];
			}
		}
		return zoomLevelData[zoomLevelData.length-1];
	}
	
	public Node getNearestNode(dataTypes.Coordinate c, int zoom) {
		
		int[] coord = MercadorProjection.getIndexXY(c, nodeData.getNativeZoomlevel());
		Set<Node> nodes = nodeData.getNodes(coord[0], coord[1], nodeData.getNativeZoomlevel());
		
		Node nearestNode = null;
		double minDistance = Float.MAX_VALUE;
		double curDistance;
		
		for (Node curNode : nodes) {
			
			if (curNode.getEdges() == null)
				continue;
			if (curNode.getEdges().size() == 0)
				continue;
			
			// --- check if its a trafficable node ---
			boolean trafficable = false;
			List<Edge> edges = curNode.getEdges();
			for (Edge e : edges) {
				if (e.getWay().isTrafficable()) {
					trafficable = true;
					break;
				}
			}

			if (!trafficable)
				continue;
			// ---
			
			curDistance = c.distanceToCoordinate(curNode.getPosition());
			if (curDistance < minDistance) {
				nearestNode = curNode;
				minDistance = curDistance;
			}
		}
		
		return nearestNode;
	}
}
