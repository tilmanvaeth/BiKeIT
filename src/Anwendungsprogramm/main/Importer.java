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

package main;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
import userIterface.SplashScreen;
import utilities.GeometricDataStructure;
import dataTypes.AreaType;
import dataTypes.CityType;
import dataTypes.Coordinate;
import dataTypes.TileCoord;
import dataTypes.WayType;


public class Importer {
	
	private static String version = "0.07";
	private static int numDistictZoomlevelData;
	private static int[] zoomlevelDataMaxZoomlevel;
	private static int numWays;
	private static int numAreas;
	private static int numNodes;
	private static int numCities;
	private static List<Way> wayReferences;
	private static List<Area> areaReferences;
	private static List<Node> nodeReferences;
	private static List<City> cityReferences;
	private static SplashScreen splash;
	
	public static MapModel readMapModelFromDir() {
		
		// shows a splash screen
		// please update percentage from time to time
		splash = new SplashScreen();
		
		readInfo();
		splash.setPercentage(5);

		wayReferences = new ArrayList<Way>(numWays);
		areaReferences = new ArrayList<Area>(numAreas);
		nodeReferences = new ArrayList<Node>(numNodes);
		cityReferences = new ArrayList<City>(numCities);
		
		readNodes();
		splash.setPercentage(10);
		readCities();
		splash.setPercentage(20);
		readWays();
		splash.setPercentage(30);
		readAreas();
		splash.setPercentage(40);

		final NodeData nodeData = readNodeData();
		
		final CityData cityData = readCityData();
		cityReferences.clear();
		cityReferences = null;
		
		final ZoomLevelDependentData[] zLvlDatas = new ZoomLevelDependentData[numDistictZoomlevelData];
		for (int i = 0; i < numDistictZoomlevelData; i++) {
			zLvlDatas[i] = readZoomLevelDependentData(i);
			splash.setPercentage(40. + i*(50./(numDistictZoomlevelData-1)));
			
			final int fi = i;
			new Thread(new Runnable() {
				public void run() {
					zLvlDatas[fi].trim();
				}
			}).start();
		}
		areaReferences.clear();
		areaReferences = null;
		

		readEdges();
		
		nodeReferences.clear();
		nodeReferences = null;
		wayReferences.clear();
		wayReferences = null;
		
		splash.setPercentage(95);
		
		return new MapModel(nodeData, cityData, zLvlDatas);
	}
	
	public static void disposeSplash() {
		splash.dispose();
		splash = null;
	}
	
	
	
	
	/**
	 * 
	 */
	private static void readInfo() {
		try {
			InputStream fileInStream = ClassLoader.getSystemClassLoader().getResourceAsStream("info.bid");
			DataInputStream infoInStream = new DataInputStream (new BufferedInputStream(fileInStream));
			
			final String exporterVersion = readString(infoInStream);
			numDistictZoomlevelData = infoInStream.readInt();
			zoomlevelDataMaxZoomlevel = new int[numDistictZoomlevelData];
			for (int i = 0; i < numDistictZoomlevelData; i ++) {
				zoomlevelDataMaxZoomlevel[i] = infoInStream.readInt();
			}
			numNodes = infoInStream.readInt();
			numCities = infoInStream.readInt();
			numWays = infoInStream.readInt();
			numAreas = infoInStream.readInt();
			
			System.out.println(numNodes + " nodes");
			System.out.println(numCities + " cities");
			System.out.println(numWays + " ways");
			System.out.println(numAreas + " areas");
			
			if (!exporterVersion.equals(version)) {
				System.err.println("The files found were exportet in a different Version and cannot be imported");
				System.exit(0);
			}
			
			infoInStream.close();
		} 
		catch (IOException e) {
			System.out.println ("IO exception = " + e );
		}
	}
	
	
	/**
	 * 
	 */
	private static void readWays() {
		System.out.print("Starting to read in Ways ...");
		
		try {
			InputStream fileInStream = ClassLoader.getSystemClassLoader().getResourceAsStream("ways.bid");
			DataInputStream dataInStream = new DataInputStream (new BufferedInputStream(fileInStream));
			
			final int numWays = dataInStream.readInt();
			for (int i = 0; i < numWays; i++) {
				wayReferences.add(readWay(dataInStream));
			}
			System.out.println(numWays + " way read!");
			
			dataInStream.close();
		} 
		catch (IOException e) {
			System.out.println ("IO exception = " + e );
		}
	}
	
	
	/**
	 * 
	 */
	private static void readAreas() {
		System.out.print("Starting to read in Areas ...");
		
		try {
			InputStream fileInStream = ClassLoader.getSystemClassLoader().getResourceAsStream("areas.bid");
			DataInputStream dataInStream = new DataInputStream (new BufferedInputStream(fileInStream));
			
			final int numAreas = dataInStream.readInt();
			for (int i = 0; i < numAreas; i++) {
				areaReferences.add(readArea(dataInStream));
			}
			System.out.println(numAreas + " area read!");
			
			dataInStream.close();
		} 
		catch (IOException e) {
			System.out.println ("IO exception = " + e );
		}
	}
	
	
	/**
	 * 
	 */
	private static void readCities() {
		System.out.print("Starting to read in Cities ...");
		
		try {
			InputStream fileInStream = ClassLoader.getSystemClassLoader().getResourceAsStream("cities.bid");
			DataInputStream dataInStream = new DataInputStream (new BufferedInputStream(fileInStream));
			
			final int numCities = dataInStream.readInt();
			for (int i = 0; i < numCities; i++) {
				cityReferences.add(readCity(dataInStream));
			}
			System.out.println(numCities + " city read!");
			
			dataInStream.close();
		} 
		catch (IOException e) {
			System.out.println ("IO exception = " + e );
		}
	}
	
	
	/**
	 * 
	 */
	private static void readNodes() {
		System.out.print("Starting to read in Nodes ...");
		
		try {
			InputStream fileInStream = ClassLoader.getSystemClassLoader().getResourceAsStream("nodes.bid");
			DataInputStream dataInStream = new DataInputStream (new BufferedInputStream(fileInStream));
			
			final int numNodes = dataInStream.readInt();
			for (int i = 0; i < numNodes; i++) {
				nodeReferences.add(readNode(dataInStream));
			}
			System.out.println(numNodes + " node read!");
			
			dataInStream.close();
		} 
		catch (IOException e) {
			System.out.println ("IO exception = " + e );
		}
	}
	
	
	/**
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private static City readCity(DataInputStream stream) throws IOException {
		final String name = readString(stream);
		final float lon = stream.readFloat();
		final float lat = stream.readFloat();
		final int cityTypeOrdinal = stream.readByte();
		
		final City city = new City(name, new Coordinate(lat, lon));
		city.setType(CityType.values()[cityTypeOrdinal]);
		return city;
	}
	
	
	/**
	 * 
	 * @param stream
	 * @param dataStruc
	 * @return
	 * @throws IOException
	 */
	private static Way readWay(DataInputStream stream) throws IOException {
		final String name = readString(stream);
		final int wTypeOrdinal = stream.readByte();
		final ArrayList<Node> nodes = readNodeIDs(stream);
		
		final Way way = new Way();
		way.setName(name);
		way.setType(WayType.values()[wTypeOrdinal]);
		way.setNodes(nodes);
		
		return way;
	}
	
	
	/**
	 * 
	 * @param stream
	 * @param dataStruc
	 * @return
	 * @throws IOException
	 */
	private static Area readArea(DataInputStream stream) throws IOException {
		final String name = readString(stream);
		final int aTypeOrdinal = stream.readByte();
		final List<Node> nodes = readNodeIDs(stream);
		
		final Area area = new Area(name, nodes);
		area.setType(AreaType.values()[aTypeOrdinal]);

		return area;
	}
	
	
	/**
	 * 
	 * @param stream
	 * @param dataStruc
	 * @return
	 * @throws IOException
	 */
	private static void readEdge(DataInputStream stream) throws IOException {
		final int sourceRef = stream.readInt();
		final int targetRef = stream.readInt();
		final int wayRef = stream.readInt();
		final long arcflags = stream.readLong();
		
		Node source = nodeReferences.get(sourceRef);
		
		final Edge edge = new Edge(source, 
				nodeReferences.get(targetRef), 
				wayReferences.get(wayRef));
		edge.setFlags(arcflags);
		
		// add outgoing(!) edge
		source.addEdge(edge);
	}
	
	
	/**
	 * 
	 * @param stream
	 * @param dataStruc
	 * @return
	 * @throws IOException
	 */
	private static Node readNode(DataInputStream stream) throws IOException {
		final int id = stream.readInt();
		final short altitude = stream.readShort();
		final float lon = stream.readFloat();
		final float lat = stream.readFloat();
		final byte region = stream.readByte();
		
		final Node node = new Node(new Coordinate(lat, lon), id);
		node.setAltitude(altitude);
		node.setRegion(region);
		
		return node;
	}
	
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	private static void readEdges() {
		System.out.print("Starting to read in Edges ...");
		
		try {
			InputStream fileInStream = ClassLoader.getSystemClassLoader().getResourceAsStream("edges.bid");
			DataInputStream dataInStream = new DataInputStream (new BufferedInputStream(fileInStream));
			
			final int numEdges = dataInStream.readInt();
			for (int i = 0; i < numEdges; i++) {
				readEdge(dataInStream);
			}
			System.out.println(numEdges + " edges read!");
			
			dataInStream.close();
		} 
		catch (IOException e) {
			System.out.println ("IO exception = " + e );
		}
	}
	
	
	/**
	 * 
	 * @return
	 */
	private static CityData readCityData() {
		System.out.print("Starting to read in City data structure...");
		GeometricDataStructure<City> cityStruc = new GeometricDataStructure<City>();
		
		try {
			InputStream cityFileStream = ClassLoader.getSystemClassLoader().getResourceAsStream("cityData.bid");
		    DataInputStream cityDataStream = new DataInputStream (new BufferedInputStream(cityFileStream));

		    final int numEntries =  cityDataStream.readInt();
		    for (int i = 0; i < numEntries; i ++) {
		    	final int indexX = cityDataStream.readInt();
		    	final int indexY = cityDataStream.readInt();
		    	final TileCoord key = new TileCoord(indexX, indexY);
		    	final int numCities = cityDataStream.readInt();
		    	for (int j = 0; j < numCities; j++) {
		    		final int cityRef = cityDataStream.readInt();
		    		cityStruc.put(key, cityReferences.get(cityRef));
		    	}
		    }
		    
		    cityFileStream.close ();;
			System.out.println(numEntries + " cities read!");
		}
		catch (IOException e) {
			System.out.println ("IO exception = " + e );
		}
		
		return new CityData(cityStruc);
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	private static NodeData readNodeData() {
		System.out.print("Starting to read in Node data structure...");
		GeometricDataStructure<Node> nodeStruc = new GeometricDataStructure<Node>();
		
		try {
			InputStream nodeFileStream = ClassLoader.getSystemClassLoader().getResourceAsStream("nodeData.bid");
		    DataInputStream nodeDataStream = new DataInputStream (new BufferedInputStream(nodeFileStream));

		    final int numEntries =  nodeDataStream.readInt();
		    for (int i = 0; i < numEntries; i ++) {
		    	final int indexX = nodeDataStream.readInt();
		    	final int indexY = nodeDataStream.readInt();
		    	final TileCoord key = new TileCoord(indexX, indexY);
		    	final int numNodes = nodeDataStream.readInt();
		    	for (int j = 0; j < numNodes; j++) {
		    		final int nodeRef = nodeDataStream.readInt();
		    		nodeStruc.put(key, nodeReferences.get(nodeRef));
		    	}
		    }
		    
		    nodeFileStream.close();
			System.out.println(numEntries + " nodes read!");
		}
		catch (IOException e) {
			System.out.println ("IO exception = " + e );
		}
		
		return new NodeData(nodeStruc);
	}
	
	
	
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	private static ZoomLevelDependentData readZoomLevelDependentData(int value) {
		try {
			InputStream fileInStream = ClassLoader.getSystemClassLoader().getResourceAsStream("streetData" + value + ".bid");
		    DataInputStream dataInStream = new DataInputStream (new BufferedInputStream(fileInStream));

			System.out.print("Starting to read in StreetData "+value+"...");
			final StreetData streetData = readStreetData(dataInStream);
			System.out.println("Finished to read in StreetData "+value+"!");
			
		    fileInStream.close ();
		    
		    fileInStream = ClassLoader.getSystemClassLoader().getResourceAsStream("terrainData" + value + ".bid");
		    dataInStream = new DataInputStream (new BufferedInputStream(fileInStream));
			
		    System.out.print("Starting to read in TerrainData "+value+"...");
			final TerrainData terrainData = readTerrainData(dataInStream);
			System.out.println("Finished to read in TerrainData "+value+"!");
			
		    fileInStream.close ();
		    
		    return new ZoomLevelDependentData(streetData, terrainData, zoomlevelDataMaxZoomlevel[value]);
		}
		catch (IOException e) {
			System.out.println ("IO exception = " + e );
		}
		return null;
	}
	
	
	private static StreetData readStreetData(DataInputStream stream) throws IOException {
		GeometricDataStructure<Way> wayStruc = new GeometricDataStructure<Way>();
		
		final int zoomlevel = stream.readInt();
		final int numEntries =  stream.readInt();
		for (int i = 0; i < numEntries; i ++) {
		    final int indexX = stream.readInt();
		    final int indexY = stream.readInt();
		    final TileCoord key = new TileCoord(indexX, indexY);
		    final int numWays = stream.readInt();
		    for (int j = 0; j < numWays; j++) {
		    	final int wayRef = stream.readInt();
		    	wayStruc.put(key, wayReferences.get(wayRef));
		    }
		}
		
		return new StreetData(zoomlevel, wayStruc);
	}
	
	
	private static TerrainData readTerrainData(DataInputStream stream) throws IOException {
		GeometricDataStructure<Area> areaStruc = new GeometricDataStructure<Area>();
		
		final int zoomlevel = stream.readInt();
		final int numEntries =  stream.readInt();
		for (int i = 0; i < numEntries; i ++) {
		    final int indexX = stream.readInt();
		    final int indexY = stream.readInt();
		    final TileCoord key = new TileCoord(indexX, indexY);
		    final int numAreas = stream.readInt();
		    for (int j = 0; j < numAreas; j++) {
		    	final int areaRef = stream.readInt();
		    	areaStruc.put(key, areaReferences.get(areaRef));
		    }
		}
		
		return new TerrainData(zoomlevel, areaStruc);
	}
	
	
	
	/**
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private static String readString(DataInputStream stream) throws IOException {
		final int size = stream.readInt();
		String result = "";
		for (int i = 0; i < size; i++) {
			result += stream.readChar();
		}
		return result;
	}
	
	
	
	/**
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private static ArrayList<Node> readNodeIDs(DataInputStream stream) throws IOException {
		final int numNodes = stream.readInt();
		final ArrayList<Node> nodes = new ArrayList<Node>(numNodes);
		for(int i = 0; i < numNodes; i++) {
			final int nodeRef = stream.readInt();
			nodes.add(nodeReferences.get(nodeRef));
		}
		//nodes.trimToSize();
		return nodes;
	}
	
}
