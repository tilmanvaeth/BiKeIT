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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import main.Importer;
import mapModel.Area;
import mapModel.City;
import mapModel.CityData;
import mapModel.Edge;
import mapModel.MapModel;
import mapModel.Node;
import mapModel.NodeData;
import mapModel.StreetData;
import mapModel.TerrainData;
import mapModel.Way;
import mapModel.ZoomLevelDependentData;

import org.junit.Test;

import utilities.Exporter;

import dataTypes.AreaType;
import dataTypes.CityType;
import dataTypes.Coordinate;
import dataTypes.WayType;


public class ExporterImporterTest {

	static MapModel mapModel;
	static CityData cityData;
	static NodeData nodeData;
	static Node node1;
	static ZoomLevelDependentData[] zlvlData;
	
	static {
		cityData = new CityData();
		City city1 = new City("TestCity1", new Coordinate(10f, 50f));
		city1.setType(CityType.CITY);
		City city2 = new City("TestCity2", new Coordinate(15f, 50f));
		city2.setType(CityType.STATE);
		cityData.addCity(city1);
		cityData.addCity(city2);
		
		
		nodeData = new NodeData();
		
		

		Way way = new Way();
		Node[] nodes = new Node[100];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = new Node(new Coordinate(23f+i*0.02f, 10f-0.03f*i), i);
			nodeData.addNode(nodes[i]);
			way.addNode(nodes[i]);
		}
		node1 = nodes[0];
		
		StreetData sd = new StreetData(10);
		way.setType(WayType.CITYANDCYCLE_T);
		sd.addWay(way);
		
		TerrainData td = new TerrainData(10);
		Area area = new Area("Area name", way.getNodes());
		area.setType(AreaType.BUILDING);
		td.addArea(area);
		
		zlvlData = new ZoomLevelDependentData[1];
		zlvlData[0] = new ZoomLevelDependentData(sd, td, 10);
		
		for (int i = 0; i < nodes.length; i++) {
			nodes[i].addEdge(new Edge(nodes[i], nodes[(i+1)%100], way));
			nodes[i].addEdge(new Edge(nodes[i], nodes[(99+i)%100], way));
		}
		
		mapModel = new MapModel(nodeData, cityData, zlvlData);
	}
	
	@Test
	public void mapModeltest() {
		Exporter.saveMapModelToDir("/Users/Fabian/Eclipse_WS/Anwendungsprogramm/bin", mapModel);
		MapModel mM = Importer.readMapModelFromDir();
		
		assertTrue(mM.equals(mapModel));
		node1.setAltitude((short) 2);
		assertFalse(mM.equals(mapModel));
		node1.setAltitude((short) 0);
		assertTrue(mM.equals(mapModel));
		node1.getEdges().get(0).setFlag((byte) 6);
		assertFalse(mM.equals(mapModel));
	}
	

}
