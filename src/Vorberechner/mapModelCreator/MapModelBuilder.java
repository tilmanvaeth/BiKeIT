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

package mapModelCreator;

import java.util.LinkedList;
import java.util.List;

import utilities.Logger;
import utilities.LoggerFactory;
import dataTypes.AltitudeData;
import dataTypes.Coordinate;
import dataTypes.Zoomlevel;
import mapModel.*;

public class MapModelBuilder {
	private static final Logger logger = LoggerFactory.getLogger(MapModelBuilder.class);
	
	private List<Node> nodes;
	private List<Way> ways;
	private List<Area> areas;
	private List<City> cities;
	private AltitudeData[] altitudeData = null;
	private MapModel mapModel;
		
	public void setNodes(List<Node> n) {
		this.nodes = n;
	}
	
	public void setWays(List<Way> w) {
		this.ways = w;
		
//		for (int i = 0; i < w.size(); i++) {
//			w.get(i).setId(i);
//		}
	}
	
	public void setAreas(List<Area> a) {
		this.areas = a;
	}
	
	public void setCities(List<City> c) {
		this.cities = c;
	}
	
	public void setAltitudeData(AltitudeData[] altitudeData) {
		this.altitudeData = altitudeData;
	}

	public void buildMapModelData() {
		logger.startAndLog("Start creating map model.");
		NodeData nodeData = new NodeData();
		for (Node n : nodes) {
			nodeData.addNode(n);
		}
		logger.log("Nodes created.");
		
		CityData cityData = new CityData();
		for (City c : cities) {
			cityData.addCity(c);
		}
		logger.log("Cities created.");
		
		ZoomLevelDependentData[] zoomLevelData = new ZoomLevelDependentData[3];
		zoomLevelData[0] = this.buildZoomlevelDependentData(0, 3, 6);
		zoomLevelData[1] = this.buildZoomlevelDependentData(1, 12, 13);
		zoomLevelData[2] = this.buildZoomlevelDependentData(2, Zoomlevel.ZOOMLEVEL_MAX-2, Zoomlevel.ZOOMLEVEL_MAX);
		
		
		LinkedList<Edge> edges = new LinkedList<Edge>();
		
		int numWays = ways.size();
		logger.log("Number of ways: " + numWays);
		
		for (Way w: ways) {
			edges.addAll(splitWayToEdges(w));
		}
		
		logger.log("Edges created.");
		
		
		
		mapModel = new MapModel(nodeData, cityData, zoomLevelData);
		logger.stopAndLog("Map model created");
		
		
		nodeData = null;
		cityData = null;
		zoomLevelData = null;
	}
	
	private ZoomLevelDependentData buildZoomlevelDependentData(int index, int nativeZoomlevel, int maxZoomlevel) {
		final StreetData streetData = new StreetData(nativeZoomlevel);
		final TerrainData terrainData = new TerrainData(nativeZoomlevel);
		
		for (Way w : ways) {
			streetData.addWay(w);
		}
		
		for (Area a : areas) {
			terrainData.addArea(a);
		}
		
		return new ZoomLevelDependentData(streetData, terrainData, maxZoomlevel);
	}
	
	public MapModel getMapModel() {
		return mapModel;
	}
	
	private LinkedList<Edge> splitWayToEdges(Way way) {
		LinkedList<Edge> edges = new LinkedList<Edge>();
		
		List<Node> wayNodes = way.getNodes();
		
		Node curNode = null;
		Node nextNode = null;
		
		if (way.isOneway()) {
			for (int i = 0; i < wayNodes.size() - 1; i++) {
				curNode = wayNodes.get(i);
				nextNode = wayNodes.get(i + 1);
								
				Edge edge = new Edge(curNode, nextNode, way);
				// add incoming(!) edge to node
				nextNode.addIncomingEdge(edge);
				edges.add(edge);
				
				getAlt(curNode);
				
			}
			if (nextNode != null)
				getAlt(nextNode);
		}
		else {
			for (int i = 0; i < wayNodes.size() - 1; i++) {
				curNode = wayNodes.get(i);
				nextNode = wayNodes.get(i + 1);		
								
				Edge edge = new Edge(nextNode, curNode, way);
				// add incoming(!) edge to node
				curNode.addIncomingEdge(edge);
				edges.add(edge);
				
				edge = new Edge(curNode, nextNode, way);
				// add incoming(!) edge to node
				nextNode.addIncomingEdge(edge);
				edges.add(edge);
				getAlt(curNode);
			}
			if (nextNode != null)
				getAlt(nextNode);
		}
//		wayNodes = null;
//		curNode = null;
//		nextNode = null;
//		
//		way.freeNodes();
//		System.gc();
		
		return edges;
	}
	
	private void getAlt(Node n) {
		if (altitudeData != null) {
			int j = 0;
			boolean found = false;
			while (j < altitudeData.length && !found) {
				Coordinate node = n.getPosition();
				Coordinate lowerleft = altitudeData[j].getLowerLeftCorner();
				boolean lat = node.getLatitude() > lowerleft.getLatitude()
						&& node.getLatitude() < lowerleft.getLatitude() + altitudeData[j].getHeight() * altitudeData[j].getPixelSize();
				boolean lon = node.getLongitude() >lowerleft.getLongitude()
				&& node.getLongitude() < lowerleft.getLongitude() + altitudeData[j].getWidth() * altitudeData[j].getPixelSize();
				if (lat && lon) 
					found = true;
				else 
					j++;
			}
			if (found)
				n.setAltitude(altitudeData[j].getAltitude(n.getPosition()));
		}
	}	
}
