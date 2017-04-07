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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
import dataTypes.TileCoord;


public class Exporter {
	
	private static final Logger logger = LoggerFactory.getLogger(Exporter.class);
	
	private static String pathToDir;
	private static String version = "0.07";
	private static Map<Way, Integer> wayReferences;
	private static Map<Area, Integer> areaReferences;
	private static Map<Node, Integer> nodeReferences;
	private static Map<City, Integer> cityReferences;
	private static List<Way> wayList;
	private static List<Area> areaList;
	private static List<City> cityList;
	private static List<Node> nodeList;
	//private static List<Edge> edgeReferences;
	
	@SuppressWarnings("rawtypes")
	private static Future nodeDataWritten;
	@SuppressWarnings("rawtypes")
	private static Future cityDataWritten;
	@SuppressWarnings("rawtypes")
	private static Future[] terrainDataWritten;
	@SuppressWarnings("rawtypes")
	private static Future[] streetDataWritten;
	@SuppressWarnings("rawtypes")
	private static Future edgesWritten; 
	@SuppressWarnings("rawtypes")
	private static Future nodesWritten;
	@SuppressWarnings("rawtypes")
	private static Future waysWritten;
	@SuppressWarnings("rawtypes")
	private static Future areasWritten;
	@SuppressWarnings("rawtypes")
	private static Future citiesWritten;
	@SuppressWarnings("rawtypes")
	private static Future infoWritten;
	
	
	private static final LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
	private static final ThreadPoolExecutor tPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), Long.MAX_VALUE, TimeUnit.NANOSECONDS, workQueue);
	
	
	public static void saveMapModelToDir(String ptd, final MapModel model) {
		logger.startAndLog("Start exporting the map model.");
		pathToDir = ptd;
		init(model);
		
		writeNodeData(model.getNodeData());
		writeCityData(model.getCityData());
		for (int value = 0; value < model.getDistinctZoomlevelDatas() ; value++) {
			writeZoomlevelData(model, value);
		}
		
		waitForDataWritten(model);
		
		writeEdges(model);
		writeNodes();
		writeWays();
		writeAreas();
		writeCities();
		
		writeInfo(model);

		waitForAll();
		tPool.shutdown();
		logger.stopAndLog("Finished exporting the map model");
	}
	
	
	
	private static void waitForDataWritten(MapModel model) {
		boolean allFinished = false;
		
		while (!allFinished) {
			try {
				Thread.sleep(1000);
				logger.updateLog("Waiting for the data to be written.");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			allFinished = nodeDataWritten.isDone() && cityDataWritten.isDone();
			if (allFinished) {
				for (int value = 0; value < model.getDistinctZoomlevelDatas() ; value++) {
					if (!terrainDataWritten[value].isDone() || !streetDataWritten[value].isDone()) {
						allFinished = false;
						break;
					}
				}
			}
		}
	}
	
	private static void waitForAll() {
		boolean allFinished = false;
		
		while (!allFinished) {
			try {
				Thread.sleep(1000);
				logger.updateLog("Waiting for the references to be written.");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			allFinished = nodesWritten.isDone() && edgesWritten.isDone() && areasWritten.isDone() && waysWritten.isDone() && citiesWritten.isDone() && infoWritten.isDone();
		}
	}
	
	
	
	private static void init(MapModel model) {
		wayList = new LinkedList<Way>();
		areaList = new LinkedList<Area>();
		nodeList = new LinkedList<Node>();
		cityList = new LinkedList<City>();
		wayReferences = new HashMap<Way, Integer>(100000);
		areaReferences = new HashMap<Area, Integer>(100000);
		nodeReferences = new HashMap<Node, Integer>(100000);
		cityReferences = new HashMap<City, Integer>(100000);
		//edgeReferences = new LinkedList<Edge>();
		
		streetDataWritten = new Future[model.getDistinctZoomlevelDatas()];
		terrainDataWritten = new Future[model.getDistinctZoomlevelDatas()];
	}
	
	
	private static synchronized int referenceWay(Way way) {
		if (!wayReferences.containsKey(way)) {
			final int index = wayList.size();
			wayList.add(way);
			wayReferences.put(way, index);
			return index;
		}
		return wayReferences.get(way);
	}
	
	private static synchronized int referenceNode(Node node) {
		if (!nodeReferences.containsKey(node)) {
			final int index = nodeList.size();
			nodeList.add(node);
			nodeReferences.put(node, index);
			return index;
		}
		return nodeReferences.get(node);
	}
	
	private static synchronized int referenceCity(City city) {
		if (!cityReferences.containsKey(city)) {
			final int index = cityList.size();
			cityList.add(city);
			cityReferences.put(city, index);
			return index;
		}
		return cityReferences.get(city);
	}
	
	private static synchronized int referenceArea(Area area) {
		if (!areaReferences.containsKey(area)) {
			final int index = areaList.size();
			areaList.add(area);
			areaReferences.put(area, index);
			return index;
		}
		return areaReferences.get(area);
	}
	
	
	
	/**
	 * Writes information of the mapmodel
	 * 
	 * The output is as follows:
	 * 
	 * 		String			exporterVersion
	 * 		int				numDistinctZoomlevelData
	 * 		numDistinctZoomlevelData x
	 * 			int			maxZoomlevel
	 * 		int				numNodes
	 * 		int 			numCities
	 * 		int 			numWays
	 * 		int 			numAreas
	 * 
	 * @param model
	 */
	private static void writeInfo(final MapModel model) {
		infoWritten = tPool.submit(new Runnable() {

			@Override
			public void run() {
				Thread.currentThread().setName("infoOut");
				Logger threadLogger = new Logger();
				threadLogger.startAndLog("Start writing info.bid.");
				try {
					FileOutputStream infoFileStream = new FileOutputStream (new File(pathToDir + "/info.bid"));
				    DataOutputStream infoDataStream = new DataOutputStream (infoFileStream);
				    
				    writeString(version, infoDataStream);
				    infoDataStream.writeInt(model.getDistinctZoomlevelDatas());
				    for (int i = 0; i < model.getDistinctZoomlevelDatas(); i++) {
				    	infoDataStream.writeInt(model.getMapData(i).getMaximalZoomlevel());
				    }
				    infoDataStream.writeInt(nodeReferences.size());
				    infoDataStream.writeInt(cityReferences.size());
				    infoDataStream.writeInt(wayReferences.size());
				    infoDataStream.writeInt(areaReferences.size());
				    
				    infoFileStream.close ();
				}
				catch (IOException e) {
					System.out.println ("IO exception = " + e );
				}
				threadLogger.stopAndLog("Finished writing info.bid");
			}
			
		});
		
		
	}
	

	/**
	 * Writes a City in binary
	 * 
	 * A City is written as follows:
	 * 
	 * 			String			name
	 * 			float			longitude
	 * 			float 			latitude
	 * 			byte			cityTypeOrdinal
	 * 
	 * 
	 * @param city the city to write
	 * @param stream the stream to write to
	 * @throws IOException
	 */
	private static void writeCity(City city, DataOutputStream stream) throws IOException {
		writeString(city.getName(), stream);
		stream.writeFloat(city.getPosition().getLongitude());
		stream.writeFloat(city.getPosition().getLatitude());
		stream.writeByte(city.getType().ordinal());
	}
	
	
	
	/**
	 * Writes a Way in binary
	 * 
	 * A Way is written as follows:
	 * 
	 *			int				id
	 * 			String 			name
	 * 			byte			wayTypOrdinal
	 * 			List<Node>(id)	nodes
	 * 
	 * @param way the way to write
	 * @param stream the stream to write to
	 * @throws IOException
	 */
	private static void writeWay(Way way, DataOutputStream stream) throws IOException {
		writeString(way.getName(), stream);
    	stream.writeByte(way.getType().ordinal());
    	writeNodeIDs(way.getNodes(), stream);
	}
	
	
	
	/**
	 * Writes an Area as binary
	 * 
	 * The Area is written as follows:
	 * 
	 * 			String				name
	 * 			byte				AreaTypeOrdinal
	 * 			List<Node>(ID)		nodes
	 * 				
	 * 			
	 * @param area
	 * @param stream
	 * @throws IOException
	 */
	private static void writeArea(Area area, DataOutputStream stream) throws IOException {
		if (area.getType() == null) {
    		logger.log("Area has no type: " + area.getNodes().get(0).getId());
    		System.exit(1);
    	}
		
		writeString(area.getName(), stream);
    	stream.writeByte(area.getType().ordinal());
    	writeNodeIDs(area.getNodes(), stream);
	}
	
	
	
	/**
	 * Writes an Edge as binary
	 * 
	 * The Edge is written as follows:
	 * 
	 * 			int 				SourceNodeRef
	 * 			int					TargetNodeRef
	 * 			int					WayRef
	 * 			long				arcflags
	 * 
	 * 
	 * @param edge the edge to write
	 * @param stream the stream to write to
	 * @throws IOException
	 */
	private static void writeEdge(Edge edge, DataOutputStream stream) throws IOException {
		stream.writeInt(referenceNode(edge.getSource()));
		stream.writeInt(referenceNode(edge.getTarget()));
		stream.writeInt(referenceWay(edge.getWay()));
		stream.writeLong(edge.getFlags());
	}
	
	
	
	/**
	 * Writes a Node in binary
	 * 
	 * A Node is written as follows:
	 * 	
	 * 			int 				id
	 * 			short 				altitude
	 * 			float 				longitude
	 * 			float 				latitude
	 * 			byte 				region
	 * 				
	 * 
	 * @param node the node to write
	 * @param stream the stream to write to
	 * @throws IOException
	 */
	private static void writeNode(Node node, DataOutputStream stream) throws IOException {
		stream.writeInt(node.getId());
		stream.writeShort(node.getAltitude());
		stream.writeFloat(node.getPosition().getLongitude());
		stream.writeFloat(node.getPosition().getLatitude());
		stream.writeByte(node.getRegion());
	}
	
	
	
	/**
	 * Writes a all Edges to (named edges.bid)
	 * 
	 * The output is as follows:
	 * 
	 * 			int					numEdges
	 * 			numEdges x
	 * 				Edge			edge
	 * 
	 * @param edges edges to write
	 * @param value the value to name the data
	 */
	private static void writeEdges(final MapModel mapModel) {
		edgesWritten = tPool.submit(new Runnable() {

			@Override
			public void run() {
				Thread.currentThread().setName("edgesOut");
				Logger threadLogger = new Logger();
				threadLogger.startAndLog("Start collecting edges.");
				final List<Edge> edges = new LinkedList<Edge>();
				for (Node node : mapModel.getNodeData().getAllNodes()) {
					edges.addAll(node.getIncomingEdges());
				}
				threadLogger.stopAndLog("Collected " + edges.size() + " edges");
				
				threadLogger.startAndLog("Start writing the edges.");
				try {
					FileOutputStream fileOutStream = new FileOutputStream (new File(pathToDir + "/edges.bid"));
				    DataOutputStream dataOutStream = new DataOutputStream (fileOutStream);

				    dataOutStream.writeInt(edges.size());
				    for (Edge e : edges) {
				    	writeEdge(e, dataOutStream);
				    }
				    fileOutStream.close ();
				    
				    threadLogger.stopAndLog("Wrote " + edges.size() + " edges");
				}
				catch (IOException e) {
					System.out.println ("IO exception = " + e );
				}
			}
			
		});
		
	}
	
	
	/**
	 * Writes a all Nodes to (named nodes.bid)
	 * 
	 * The output is as follows:
	 * 
	 * 			int					numNodes
	 * 			numNodes x
	 * 				Node			node
	 * 
	 * @param edges edges to write
	 * @param value the value to name the data
	 */
	private static void writeNodes() {
		nodesWritten = tPool.submit(new Runnable() {

			@Override
			public void run() {
				Thread.currentThread().setName("nodesOut");
				Logger threadLogger = new Logger();
				threadLogger.startAndLog("Start writing the nodes.");
				try {
					FileOutputStream fileOutStream = new FileOutputStream (new File(pathToDir + "/nodes.bid"));
				    DataOutputStream dataOutStream = new DataOutputStream (fileOutStream);

				    dataOutStream.writeInt(nodeList.size());
				    for (Node n : nodeList) {
				    	writeNode(n, dataOutStream);
				    }
				    fileOutStream.close ();
				    
				    threadLogger.stopAndLog("Wrote " + nodeList.size() + " nodes");
				}
				catch (IOException e) {
					System.out.println ("IO exception = " + e );
				}
			}
			
		});
		
	}
	
	
	/**
	 * Writes a all Ways to (named ways.bid)
	 * 
	 * The output is as follows:
	 * 
	 * 			int					numWays
	 * 			numWays x
	 * 				Way				way
	 * 
	 * @param edges edges to write
	 * @param value the value to name the data
	 */
	private static void writeWays() {
		waysWritten = tPool.submit(new Runnable() {

			@Override
			public void run() {
				Thread.currentThread().setName("waysOut");
				Logger threadLogger = new Logger();
				threadLogger.startAndLog("Start writing the ways.");
				try {
					FileOutputStream fileOutStream = new FileOutputStream (new File(pathToDir + "/ways.bid"));
				    DataOutputStream dataOutStream = new DataOutputStream (fileOutStream);

				    dataOutStream.writeInt(wayList.size());
				    for (Way w : wayList) {
				    	writeWay(w, dataOutStream);
				    }
				    fileOutStream.close ();
				    
				    threadLogger.stopAndLog("Wrote " + wayList.size() + " ways");
				}
				catch (IOException e) {
					System.out.println ("IO exception = " + e );
				}
			}
		});
		
		
	}
	
	
	/**
	 * Writes a all Areas to (named areas.bid)
	 * 
	 * The output is as follows:
	 * 
	 * 			int					numAreas
	 * 			numAreas x
	 * 				Area			area
	 * 
	 * @param edges edges to write
	 * @param value the value to name the data
	 */
	private static void writeAreas() {
		areasWritten = tPool.submit(new Runnable() {

			@Override
			public void run() {
				Thread.currentThread().setName("areasOut");
				Logger threadLogger = new Logger();
				threadLogger.startAndLog("Start writing the areas.");
				try {
					FileOutputStream fileOutStream = new FileOutputStream (new File(pathToDir + "/areas.bid"));
				    DataOutputStream dataOutStream = new DataOutputStream (fileOutStream);

				    dataOutStream.writeInt(areaList.size());
				    for (Area n : areaList) {
				    	writeArea(n, dataOutStream);
				    }
				    fileOutStream.close ();
				    
				    threadLogger.stopAndLog("Wrote " + areaList.size() + " areas");
				}
				catch (IOException e) {
					System.out.println ("IO exception = " + e );
				}
			}
		});
		
	}
	
	
	/**
	 * Writes a all Cities to (named cities.bid)
	 * 
	 * The output is as follows:
	 * 
	 * 			int					numCities
	 * 			numCities x
	 * 				City			city
	 * 
	 * @param edges edges to write
	 * @param value the value to name the data
	 */
	private static void writeCities() {
		citiesWritten = tPool.submit(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setName("citiesOut");
				Logger threadLogger = new Logger();
				threadLogger.startAndLog("Start writing the cities.");
				try {
					FileOutputStream fileOutStream = new FileOutputStream (new File(pathToDir + "/cities.bid"));
				    DataOutputStream dataOutStream = new DataOutputStream (fileOutStream);

				    dataOutStream.writeInt(cityList.size());
				    for (City n : cityList) {
				    	writeCity(n, dataOutStream);
				    }
				    fileOutStream.close ();
				    
				    threadLogger.stopAndLog("Wrote " + cityReferences.size() + " cities");
				}
				catch (IOException e) {
					System.out.println ("IO exception = " + e );
				}
			}
		});
		
	}
	
	
	/**
	 * Writes the given CityData to the dir
	 * 
	 * The Output is as follows:
	 * 
	 * 		int				numEntrys
	 * 		numEntrys x
	 * 			int			indexX
	 * 			int 		indexY
	 * 			int 		numCities
	 * 			numCities x
	 * 				int		cityRef
	 * 
	 * 
	 * @param cityData
	 */
	private static void writeCityData(final CityData cityData) {
		cityDataWritten = tPool.submit(new Runnable() {

			@Override
			public void run() {
				Thread.currentThread().setName("cityDataOut");
				Logger threadLogger = new Logger();
				threadLogger.startAndLog("Start writing city data.");
				try {
					FileOutputStream cityFileStream = new FileOutputStream (new File(pathToDir + "/cityData.bid"));
				    DataOutputStream cityDataStream = new DataOutputStream (cityFileStream);

				    final Set<Entry<TileCoord, ArrayList<City>>> entries = cityData.getAllEntries();
				    
				    cityDataStream.writeInt(entries.size());
					for (Entry<TileCoord, ArrayList<City>> entry : entries) {
						final List<City> cities = entry.getValue();
						cityDataStream.writeInt(entry.getKey().getX());
						cityDataStream.writeInt(entry.getKey().getY());
						cityDataStream.writeInt(cities.size());
						for (City c : cities) {
							cityDataStream.writeInt(referenceCity(c));
						}
					} 
				    cityFileStream.close ();
				    threadLogger.stopAndLog("Wrote " + entries.size() + " city entries.");
				}
				catch (IOException e) {
					System.out.println ("IO exception = " + e );
				}
			}
			
		});
		
	}
	
	
	
	/**
	 * Writes the given NodeData to the dir
	 * 
	 * The Output is as follows:
	 * 
	 * 		int				numEntrys
	 * 		numEntrys x
	 * 			int			indexX
	 * 			int 		indexY
	 * 			int 		numNodes
	 * 			numNodes x
	 * 				int		nodeRef
	 * 
	 * 
	 * @param nodeData
	 */
	private static void writeNodeData(final NodeData nodeData) {
		nodeDataWritten = tPool.submit(new Runnable() {

			@Override
			public void run() {
				Thread.currentThread().setName("nodeDataOut");
				Logger threadLogger = new Logger();
				threadLogger.startAndLog("Start writing node data.");
				try {
					FileOutputStream nodeFileStream = new FileOutputStream (new File(pathToDir + "/nodeData.bid"));
				    DataOutputStream nodeDataStream = new DataOutputStream (nodeFileStream);

				    final Set<Entry<TileCoord, ArrayList<Node>>> entries = nodeData.getAllEntries();
				    
				    nodeDataStream.writeInt(entries.size());
					for (Entry<TileCoord, ArrayList<Node>> entry : entries) {
						final List<Node> nodes = entry.getValue();
						nodeDataStream.writeInt(entry.getKey().getX());
						nodeDataStream.writeInt(entry.getKey().getY());
						nodeDataStream.writeInt(nodes.size());
						for (Node n : nodes) {
							nodeDataStream.writeInt(referenceNode(n));
						}
					} 
				    nodeFileStream.close ();
				    threadLogger.stopAndLog("Wrote " + entries.size() + " node entries");
				}
				catch (IOException e) {
					System.out.println ("IO exception = " + e );
				}
			}
			
		});
		
	}
	
	
	
	private static void writeZoomlevelData(final MapModel mapModel,final  int value) {
		
		 streetDataWritten[value] = tPool.submit(new Runnable() {

			@Override
			public void run() {
				Thread.currentThread().setName("streetData"+value+"Out");
				Logger threadLogger = new Logger();
				threadLogger.startAndLog("Start writing street data " + value);
				try {
					
					FileOutputStream streetFileStream = new FileOutputStream (new File(pathToDir + "/streetData" + value + ".bid"));
				    DataOutputStream streetDataStream = new DataOutputStream (streetFileStream);

				    writeStreetData(mapModel.getMapData(value).getStreetData(), streetDataStream);
				    
				    streetFileStream.close ();
				    threadLogger.stopAndLog("Wrote street data " +value);
				}
				catch (IOException e) {
					System.out.println ("IO exception = " + e );
				}
			}
			
		});
		
		
		 terrainDataWritten[value] = tPool.submit(new Runnable() {

			@Override
			public void run() {
				Thread.currentThread().setName("terrainData"+value+"Out");
				Logger threadLogger = new Logger();
				threadLogger.startAndLog("Start writing terrain data " + value);
				try {
					FileOutputStream terrainFileStream = new FileOutputStream (new File(pathToDir + "/terrainData" + value + ".bid"));
				    DataOutputStream terrainDataStream = new DataOutputStream (terrainFileStream);

				    writeTerrainData(mapModel.getMapData(value).getTerrainData(), terrainDataStream);
				    
				    terrainFileStream.close ();
				    threadLogger.stopAndLog("Wrote terrain data " +value);
				}
				catch (IOException e) {
					System.out.println ("IO exception = " + e );
				}
				
			}
		});
	}
	

	
	/**
	 * Writes a streetData object as binary
	 * 
	 * the output is as follows:
	 * 
	 * 		int				zoomlevel
	 * 		int				numEntrys
	 * 		numEntrys x
	 * 			int			indexX
	 * 			int 		indexY
	 * 			int 		numWays
	 * 			numWays x
	 * 				int		wayRef
	 * @param streetData
	 * @param stream
	 * @throws IOException
	 */
	private static void writeStreetData(StreetData streetData, DataOutputStream stream) throws IOException {
		final Set<Entry<TileCoord, ArrayList<Way>>> entries = streetData.getAllEntries();
		
		stream.writeInt(streetData.getZoomlevel());
		stream.writeInt(entries.size());
		
		int total = entries.size();
		int curr = 1;
		
		for (Entry<TileCoord, ArrayList<Way>> entry : entries) {
			final List<Way> ways = entry.getValue();
			stream.writeInt(entry.getKey().getX());
			stream.writeInt(entry.getKey().getY());
			stream.writeInt(ways.size());
			
			
			for (Way w : ways) {
				stream.writeInt(referenceWay(w));
			}
			logger.updateLog("Wrote way " + curr + "/" + total);
		}
	}
	
	
	
	/**
	 * Writes a terrainData object as binary
	 * 
	 * the output is as follows:
	 * 
	 * 		int				zoomlevel
	 * 		int				numEntrys
	 * 		numEntrys x
	 * 			int			indexX
	 * 			int 		indexY
	 * 			int 		numAreas
	 * 			numAreas x
	 * 				int		areaRef	
	 * @param streetData
	 * @param stream
	 * @throws IOException
	 */
	private static void writeTerrainData(TerrainData terrainData, DataOutputStream stream) throws IOException {
		final Set<Entry<TileCoord, ArrayList<Area>>> entries = terrainData.getAllEntries();
		
		int total = entries.size();
		int curr = 1;
		
		stream.writeInt(terrainData.getZoomlevel());
		stream.writeInt(entries.size());
		for (Entry<TileCoord, ArrayList<Area>> entry : entries) {
			final List<Area> areas = entry.getValue();
			stream.writeInt(entry.getKey().getX());
			stream.writeInt(entry.getKey().getY());
			stream.writeInt(areas.size());
			for (Area a : areas) {
				stream.writeInt(referenceArea(a));
			}
			logger.updateLog("Wrote area " + curr + "/" + total);
		}
		
	}

	
	
	/**
	 * Writes a String as binary
	 * 
	 * The String is written as follows:
	 * 	
	 * 		int			numChars
	 * 		numChars x
	 * 			char	name
	 * 
	 * @param string the string to write
	 * @param stream the stream to write to
	 * @throws IOException 
	 * 
	 */
	private static void writeString(String string, DataOutputStream stream) throws IOException {
		stream.writeInt(string.length());
		stream.writeChars(string);
	}

	
	
	/**
	 * Writes a list of nodes as ids in binary
	 * 
	 * the output is as follows:
	 * 
	 * 		int			numNodes
	 * 		numNodes x
	 * 			int		nodeRef
	 * 
	 * @param nodes the list of nodes to write
	 * @param stream the stream to write to
	 * @throws IOException
	 */
	private static void writeNodeIDs(List<Node> nodes, DataOutputStream stream) throws IOException {
    	stream.writeInt(nodes.size());
    	for (Node n: nodes) {
    		stream.writeInt(referenceNode(n));
    	}
	}
	
	public static void writeMetisInputFile(List<Node> nodes) {
		try {
			FileWriter outFile = new FileWriter("graphInput.txt");
			PrintWriter out = new PrintWriter(outFile);
			
			logger.startAndLog("Start creating an input file for METIS.");
						
			ArrayList<String> outPutLines = new ArrayList<String>(nodes.size());
			int edges = 0;
			
			HashMap<Node, LinkedList<Edge>> temporaryEdges = new HashMap<Node, LinkedList<Edge>>();
			HashMap<Node, Integer> nodeIndices = new HashMap<Node, Integer>(nodes.size());
			
			int i = 1;
			for (Node n: nodes) {
				nodeIndices.put(n, i);
				i++;
			}
			
			for (Node n : nodes) {
				for (Edge e: n.getEdges()) {
					if (e.getWay().isTrafficable() && !e.getTarget().hasEdgeToNode(e.getSource())) {
						if (!temporaryEdges.containsKey(n)) {
							temporaryEdges.put(n, new LinkedList<Edge>());
						}
						temporaryEdges.get(n).add(new Edge(e.getTarget(), e.getSource(), e.getWay()));
					}
				}
			}
						
			logger.log("Created temporary edges for an undirected graph as needed by METIS.");
			
			for (Node n : nodes) {
				String outPutLine = "";
				for (Edge e : n.getEdges()) {
					if (e.getWay().isTrafficable()) {
						outPutLine += nodeIndices.get(e.getTarget()) + " ";
						edges++;
					}
				}
				if (temporaryEdges.get(n) != null) {
					for (Edge e : temporaryEdges.get(n)) {
						outPutLine += nodeIndices.get(e.getTarget()) + " ";
						edges++;
					}
				}
				outPutLines.add(outPutLine);				
			}
			
			out.println(nodes.size() + " " + edges / 2);
			for (String s: outPutLines) {
				if (!s.equals("")) out.println(s.substring(0, s.length() - 1));
				else out.println("");
			}
			
			for (LinkedList<Edge> edgeList : temporaryEdges.values()) {
				for (Edge e : edgeList) {
					e.removeFromNode();
				}
			}
			
			temporaryEdges = null;
			
			logger.stopAndLog("Created the input file for METIS");
			
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public static void readMetisOutputFile(List<Node> nodes) {
		try {
			FileReader inFile = new FileReader("graphInput.txt.part.64");
			BufferedReader in = new BufferedReader(inFile);
			
			logger.startAndLog("Start reading the output file of METIS.");
						
			for (Node n: nodes) {
				try {
					n.setRegion(Byte.parseByte(in.readLine()));
				}
				catch(NumberFormatException e) {
					System.err.println("Corrupted metis output file!");
					e.printStackTrace();
					for (Node n1: nodes) {
						n1.setRegion((byte) 0);
					}
					break;
				}
			}
			
			logger.stopAndLog("Added regions to "+nodes.size()+" nodes based on the METIS output file");
			
			in.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

}
