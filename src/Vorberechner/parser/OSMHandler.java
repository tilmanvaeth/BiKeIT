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

package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import mapModel.Area;
import mapModel.City;
import mapModel.Node;
import mapModel.Way;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import utilities.Logger;
import utilities.LoggerFactory;
import dataTypes.AreaType;
import dataTypes.CityType;
import dataTypes.Coordinate;
import dataTypes.WayType;

public class OSMHandler extends DefaultHandler {


	private static final Logger logger = LoggerFactory.getLogger(OSMHandler.class);

	private static final String GRADE3 = "grade3";
	private static final String PRIVATE = "private";
	private static final String OSM = "osm";
	private static final String SUBURB = "suburb";
	private static final String COMMERCIAL = "commercial";
	private static final String RETAIL = "retail";
	private static final String ENTRANCE = "entrance";
	private static final String AQUEDUCT = "aqueduct";
	private static final String BRIDGE = "bridge";
	private static final String ISLAND = "island";
	private static final String CANAL = "canal";
	private static final String RIVERBANK = "riverbank";
	private static final String DITCH = "ditch";
	private static final String STREAM = "stream";
	private static final String RIVER = "river";
	private static final String OPPOSITE = "opposite";
	private static final String NAME = "name";
	private static final String NAMEDE = "name:de";
	private static final String BUILDING = "building";
	private static final String TAG = "tag";
	private static final String NODE = "node";
	private static final String WAY = "way";
	private static final String ND = "nd";
	private static final String AREA = "area";
	private static final String YES = "yes";
	private static final String NO = "no";
	private static final String PLACE = "place";
	private static final String CITY = "city";
	private static final String TOWN = "town";
	private static final String VILLAGE = "village";
	private static final String STATE = "state";
	private static final String V = "v";
	private static final String K = "k";
	private static final String FARM = "farm";
	private static final String FARMLAND = "farmland";
	private static final String FOREST = "forest";
	private static final String GRASS = "grass";
	private static final String GREENFIELD = "greenfield";
	private static final String MEADOW = "meadow";
	private static final String VG = "village_green";
	private static final String BEACH = "beach";
	private static final String SAND = "sand";
	private static final String WATER = "water";
	private static final String WOOD = "wood";
	private static final String GRASSLAND = "grassland";
	private static final String AEROWAY = "aeroway";
	private static final String AERODROME = "aerodrome";
	private static final String LEISURE = "leisure";
	private static final String PARK = "park";
	private static final String GARDEN = "garden";
	private static final String GOLF = "golf_course";
	private static final String LANDUSE = "landuse";
	private static final String NATURAL = "natural";
	private static final String ADMINL = "admin_level";
	private static final String HIGHWAY = "highway";
	private static final String PEDESTRIAN = "pedestrian";
	private static final String CYCLEWAY = "cycleway";
	private static final String FOOTWAY = "footway";
	private static final String WATERWAY = "waterway";
	private static final String RAILWAY = "railway";
	private static final String ABANDONED = "abandoned";
	private static final String CONSTRUCTION = "construction";
	private static final String JUNCTION = "junction";
	private static final String ROUNDABOUT = "roundabout";
	private static final String TRACKTYPE = "tracktype";
	private static final String GRADE4 = "grade4";
	private static final String GRADE5 = "grade5";
	private static final String TUNNEL = "tunnel";
	private static final String BICYCLE = "bicycle";
	private static final String FOOT = "foot";
	private static final String ACCESS = "access";
	private static final String ONEWAY = "oneway";
	private static final String OL = "opposite_lane";
	private static final String OT = "opposite_track";
	private static final String MOTORWAY = "motorway";
	private static final String ML = "motorway_link";
	private static final String TRUNK = "trunk";
	private static final String TL = "trunk_link";
	private static final String PRIMARY = "primary";
	private static final String PL = "primary_link";
	private static final String SECONDARY = "secondary";
	private static final String SL = "secondary_link";
	private static final String LS = "living_street";
	private static final String RESIDENTIAL = "residential";
	private static final String ROAD = "road";
	private static final String UNC = "unclassified";	
	private static final String TERTIARY = "tertiary";
	private static final String TEL = "tertiary_link";
	private static final String STEPS = "steps";
	private static final String PATH = "path";
	private static final String TRACK = "track";
	private static final String SERVICE = "service";
	
	private final Stack<String> eleStack;
	private final OSMParser parser;
	private Node n;
	private City c;
	private Way w;
	private Area a;
	private Boolean toAdd;
	private Boolean isCity;
	private String cityName;
	private Integer nodeId;
	private HashMap<Integer, Node> nodes;
	private HashSet<Node> nodesUsed;
	private AreaType areatype;
	private WayType waytype;
	private Boolean bicycle;
	private boolean access;
	private boolean possibleOneWay;
//	private long wayCount = 0;
//	private long nodeCount = 0;
//	private long areaCount = 0;
//	private long cityCount = 0;
	private long nullNodes = 0;

	/**
	 *  Creates an OSMHandler, which extends the DefaultHandler
	 * @param parser - The parser that created the Handler. Needed
	 * 				to write the elements in the lists directly. 
	 */
	public OSMHandler(OSMParser parser) {
		this.parser = parser;
		this.toAdd = false;
		this.isCity = false;
		this.eleStack = new Stack<String>();
		this.nodes = new HashMap<Integer, Node>();
		this.nodesUsed = new HashSet<Node>();
		this.areatype = null;
		this.waytype = null;
		this.access = true;
		this.bicycle = false;
	}

	/**
	 * For every qName, there will be run another part of this Method.
	 * Nodes, cities, areas and ways will be created and filled with information.
	 * While that, they will be marked to be added, or eventually not.
	 */
	    @Override
	    public void startElement(String uri, String localName, String qName,
	        Attributes attrs) throws SAXException {
	    
	    	if (NODE.equals(qName)) {
	    		createNode(attrs);
	    	} 
	    	else if (TAG.equals(qName) && NODE.equals(eleStack.peek())) {
	    		nodeTags(attrs);
	    	} 
	    	else if (WAY.equals(qName)) {
	    		createWay();
	    	} 
	    	else if (ND.equals(qName) && WAY.equals(eleStack.peek())) {
	    		addNodeToWay(attrs);
	    	} 
	    	else if (TAG.equals(qName) && WAY.equals(eleStack.peek())) {
	    		if (w != null) {
	    			wayTags(attrs);
	    			buildArea(attrs);
	    		} else if (a != null)
	    			areaTags(attrs);
	      }
	      eleStack.push(qName);
	    }

	    /**
	     * This method will actually add the created elements to the designated lists. 
	     */
	    @Override
	    public void endElement(String uri, String localName, String qName)
	        throws SAXException {
	    	String element = eleStack.pop();
	    	if (element.equals(NODE) && isCity) {
	    		parser.getCities().add(c);
	    		nodes.put(nodeId, n);
//	    		cityCount++;
//	    		nodeCount++;
//	    		if (cityCount % 10 == 0) {
//	    			logger.updateLog("Read " + cityCount + " cities.");
//	    		}
	    	} else if (element.equals(NODE) ) {
	    		nodes.put(nodeId, n);
//	    		nodeCount++;
//	    		if (nodeCount % 100 == 0) {
//	    			logger.updateLog("Read " + nodeCount + " nodes.");
//	    		}
	    	} else if (element.equals(WAY) && w == null && a != null && toAdd) {
	    		parser.getAreas().add(a);
//	    		areaCount++;
//	    		if (areaCount % 100 == 0) {
//	    			logger.updateLog("Read " + areaCount + " areas.");
//	    		}
	    	} else if (element.equals(WAY) && toAdd && a == null && w != null) {
	    		parser.getWays().add(w);
//	    		wayCount++;
//	    		if (wayCount % 100 == 0) {
//	    			logger.updateLog("Read " + wayCount + " ways.");
//	    		}
	    	} else if (element.equals(OSM)) {
	    		logger.updateLog("Found " + nullNodes + " not existing nodes.");
	    	}
	    }
	    
	    /*
	     * This private method creates nodes.
	     */
	    private void createNode(Attributes attrs) {
	    	n = new Node(new Coordinate(Float.parseFloat(attrs.getValue("lat")),
    				Float.parseFloat(attrs.getValue("lon"))),
    				Integer.parseInt(attrs.getValue("id")));
	    	c = null;
	    	nodeId = new Integer(Integer.parseInt(attrs.getValue("id")));
	    	cityName = null;
    		toAdd = false;
    		isCity = false;
	    }
	    
	    private void createWay() {
	    	w = new Way();
	    	w.setType(WayType.UNKNOWN_NT);
    		a = null;
    		toAdd = false;
    		waytype = null;
    		areatype = null;
    		access = true;
    		bicycle = true;
    		possibleOneWay = true;
	    }
	    
	    private void addNodeToWay(Attributes attrs) {
	    	String id = attrs.getValue("ref");
    		Node tempNode = nodes.get(new Integer(id));
    		if (tempNode != null) {
    			w.addNode(tempNode);
    			nodesUsed.add(tempNode);
    		} else {
    			logger.updateLog("Nullnode:" + id);
    			nullNodes++;
    		}
	    }
	    
	    /*
	     * This private methode checks the tags of the nodes.
	     * Actually, it only checks for cities and their name. 
	     */
	    private void nodeTags(Attributes attrs) {
    		String key = attrs.getValue(K);
    		String value = attrs.getValue(V);
    		if (PLACE.equals(key)) {
    			if (CITY.equals(value)) {
    				isCity = true;
    				c = new City(cityName, n.getPosition());
    				cityName = null;
    				c.setType(CityType.CITY);
    			} else if (TOWN.equals(value)) {
    				isCity = true;
    				c = new City(cityName, n.getPosition());
    				cityName = null;
    				c.setType(CityType.TOWN);
    			} else if (VILLAGE.equals(value)) {
    				isCity = true;
    				c = new City(cityName, n.getPosition());
    				cityName = null;
    				c.setType(CityType.VILLAGE);
    			} else if (STATE.equals(value)) {
    				isCity = true;
    				c = new City(cityName, n.getPosition());
    				cityName = null;
    				c.setType(CityType.STATE);
    			} else if (SUBURB.equals(value)) {
    				isCity = true;
    				c = new City(cityName, n.getPosition());
    				cityName = null;
    				c.setType(CityType.SUBRUB);
    			}
    		} else if (NAMEDE.equals(key) || (NAME).equals(key)) {
    			cityName = value;
    			if (isCity)	{
    				c.setName(cityName);
    			} 
    		} 
	    }
	    
	    private void buildArea(Attributes attrs) {
	    	String key = attrs.getValue(K);
	    	String value = attrs.getValue(V);
	    	if (AREA.equals(key) && !NO.equals(value)) 
	    		createArea();
	    	else if (BUILDING.equals(key) && !ENTRANCE.equals(value)) 
	    		createBuilding();
	    	else if (AEROWAY.equals(key) && AERODROME.equals(value))
	    		createAeroWay();
	    	else if (LEISURE.equals(key) && (PARK.equals(value) 
	    			|| GARDEN.equals(attrs.getValue(V)) 
	    			|| GOLF.equals(attrs.getValue(V)))) 
	    		createGrass();
	    	else if (LANDUSE.equals(key))
	    		createLanduse(value);
	    	else if (NATURAL.equals(key))
	    		createNatural(value);
		    /*else if (BOUNDARY.equals(key) && ADMIN.equals(value)) {
	    		//a = new Area(w.getName(), w.getNodes());
	    		//w = null;
	    	} else if (ADMINL.equals(key))	{
	    		//createAdminLevel(value);
	    		a = null;
	    		//toAdd = false;
	    	}*/ else if (HIGHWAY.equals(key) && (PEDESTRIAN.equals(value)
	    			|| CYCLEWAY.equals(attrs.getValue(V)) || FOOTWAY.equals(value))) {
		    	areatype = AreaType.SQUARE;
    			toAdd = true;
		    } else if (WATERWAY.equals(key) && RIVERBANK.equals(value)) {
		    	a = new Area(w.getName(), w.getNodes());
				a.setType(AreaType.RIVERBANK);
				toAdd = true;
				w = null;
		    } else if (PLACE.equals(key) && ISLAND.equals(value)) {
		    	a = new Area(w.getName(), w.getNodes());
				a.setType(AreaType.ISLAND);
				toAdd = true;
				w = null;
		    }
	    }
	    
	    private void createBuilding() {
	    	a = new Area(w.getName(), w.getNodes());
			a.setType(AreaType.BUILDING);
			toAdd = true;
			w = null;
			    }
	    
	    private void createArea() {
			a = new Area(w.getName(), w.getNodes());
	      	if (areatype != null) {
	      		a.setType(areatype);
	      		toAdd = true;
    		} else if (w.getType() == WayType.INCITY_T || w.getType() == WayType.FOOTWAY 
    				|| w.getType() == WayType.FOOTANDCYCLE_T || w.getType() == WayType.CYCLEWAY_T) {
    			a.setType(AreaType.SQUARE);
    			toAdd = true;
    		} else {
    			a.setType(AreaType.UNCLASSIFIED);
    			toAdd = false;
    		}
    		w = null;
	    }
	    
	    private void createAeroWay() {
    		a = new Area(w.getName(), w.getNodes());
    		a.setType(AreaType.AERODROME);
    	    		w = null;
    		toAdd = true;
	    }
	    
	    private void createGrass() {	    	
   			a = new Area(w.getName(), w.getNodes());
   			a.setType(AreaType.GRASS);
    		w = null;
    		toAdd = true;
	    }
	    
	    private void createLanduse(String value) {
    		if (FARM.equals(value) || FARMLAND.equals(value)) {
    			a = new Area(w.getName(), w.getNodes());
    			a.setType(AreaType.FIELD);
    			w = null;
	    		toAdd = true;
    		} else if (FOREST.equals(value)) {
    			a = new Area(w.getName(), w.getNodes());
    			a.setType(AreaType.FOREST);
    			w = null;
	    		toAdd = true;
    		} else if (GRASS.equals(value) || GREENFIELD.equals(value) 
    				|| MEADOW.equals(value) || VG.equals(value)) {
    			a = new Area(w.getName(), w.getNodes());
    			a.setType(AreaType.GRASS);
    			w = null;
	    		toAdd = true;
    		} else if (RESIDENTIAL.equals(value) || RETAIL.endsWith(value) || COMMERCIAL.equals(value)) {
    			a = new Area(w.getName(), w.getNodes());
    			a.setType(AreaType.CITY);
    			w = null;
    			toAdd = true;
    		}
	    }
	    
	    private void createNatural(String value) {
    		if (BEACH.equals(value) || SAND.equals(value)) {
    			a = new Area(w.getName(), w.getNodes());
    			a.setType(AreaType.SAND);
    			w = null;
    			toAdd = true;
    		} else if (WATER.equals(value)) {
    			a = new Area(w.getName(), w.getNodes());
    			a.setType(AreaType.WATER);
    			w = null;
	    		toAdd = true;
    		} else if (WOOD.equals(value)) {
    			a = new Area(w.getName(), w.getNodes());
    			a.setType(AreaType.FOREST);
    			w = null;
	    		toAdd = true;
    		} else if (GRASSLAND.equals(value)) {
    			a = new Area(w.getName(), w.getNodes());
    			a.setType(AreaType.GRASS);
    			w = null;
	    		toAdd = true;
    		}
	    }
	    
//	    private void createAdminLevel(String value) {
//	    	try {
//	    	int adminLevel = Integer.parseInt(value);
//    		switch (adminLevel) {
//    		case (2): 
////    			a = new Area(w.getName(), w.getNodes());
////    			a.setAreatype(AreaType.COUNTRY);
////    			w = null;
////    			toAdd = true;
////    			break;
//    			w.setType(WayType.COUNTRY_BORDER);
//				toAdd = true;
//				break;
//    		case (4): 
////    			a = new Area(w.getName(), w.getNodes());
////    			a.setAreatype(AreaType.STATE);
////    			w = null;
//    			w.setType(WayType.STATE_BORDER);
//    			toAdd = true;
//    			break; 
//    		default: 
//    			w = null;
//    			a = null;
//    			break;
//    		}
//	    	} catch (NumberFormatException e) {
//	    		
//	    	}
//	    }
	    
	    /*
	     * This method creates areas, and put some information in them.
	     * It checks some of the tags, which indicates areas.
	     */
	    private void areaTags(Attributes attrs) {
	    	String key = attrs.getValue(K);
	    	String value = attrs.getValue(V);
	    	if (AEROWAY.equals(key) && AERODROME.equals(attrs.getValue(V))) {
	    		a.setType(AreaType.AERODROME);
	    		toAdd = true;
	    	} else if (LEISURE.equals(key) && (PARK.equals(attrs.getValue(V)) 
	    			|| GARDEN.equals(attrs.getValue(V)) 
	    			|| GOLF.equals(attrs.getValue(V)))) {
	    		a.setType(AreaType.GRASS);
	    		toAdd = true;
	    	} else if (LANDUSE.equals(key)) {
	    		checkLanduse(value);
	    	} else if (NATURAL.equals(key)) {
	    		checkNatural(value);
	    	}/* else if (ADMINL.equals(key)) {
	    		//checkAdminLevel(value);
	    		//toAdd = false;
	    		a = null;
	    	}*/ else if (HIGHWAY.equals(key) && (PEDESTRIAN.equals(attrs.getValue(V))
	    			|| CYCLEWAY.equals(attrs.getValue(V)) || FOOTWAY.equals(attrs.getValue(V)))) {
	    		a.setType(AreaType.SQUARE);
	    		toAdd = true;
	    	} else if (NAME.equals(key)) {
	    		a.setName(value);
	    	} else if (PLACE.equals(key) && ISLAND.equals(value)){
	    		a.setType(AreaType.ISLAND);
	    		toAdd = true;
	    	} else if (WATERWAY.equals(key) && RIVERBANK.equals(value)) {
	    		a.setType(AreaType.RIVERBANK);
	    		toAdd = true;
	    	}
	    }
	    
	    private void checkLanduse(String value) {
	    	if (FARM.equals(value) || FARMLAND.equals(value)) {
    			a.setType(AreaType.FIELD);
    			toAdd = true;
    		} else if (FOREST.equals(value)) {
    			a.setType(AreaType.FOREST);
    			toAdd = true;
    		} else if (GRASS.equals(value) || GREENFIELD.equals(value) 
    				|| MEADOW.equals(value) || VG.equals(value)) {
    			a.setType(AreaType.GRASS);
    			toAdd = true;
    		} else if (RESIDENTIAL.equals(value) || RETAIL.endsWith(value) || COMMERCIAL.equals(value)) {
    			a.setType(AreaType.CITY);
    			toAdd = true;
    		}
	    }
	    
	    private void checkNatural(String value) {
	    	if (BEACH.equals(value) || SAND.equals(value)) {
    			a.setType(AreaType.SAND);
    			toAdd = true;
    		} else if (WATER.equals(value)) {
    			a.setType(AreaType.WATER);
	    		toAdd = true;
    		} else if (WOOD.equals(value)) {
    			a.setType(AreaType.FOREST);
	    		toAdd = true;
    		} else if (GRASSLAND.equals(value)) {
    			a.setType(AreaType.GRASS);
	    		toAdd = true;
    		}
	    }
	    
	    private void checkAdminLevel(String value) {
	    	int adminLevel = Integer.parseInt(value);
	    	switch (adminLevel) {
        		case (2): 
//        			a.setAreatype(AreaType.COUNTRY);
        			w.setType(WayType.COUNTRY_BORDER);
        			toAdd = true;
        			break;
        		case (4): 
//        			a.setAreatype(AreaType.STATE);
        			w.setType(WayType.STATE_BORDER);
        			toAdd = true;
        			break; 
        		default: 
        			w = null;
        			a = null;
        			break;
        		}
	    }
	    
	    /*
	     * This method checks the tags for ways.
	     */
	    private void wayTags(Attributes attrs) {
	    	String key = attrs.getValue(K);
	    	String value = attrs.getValue(V);
    		if (NAME.equals(key)/* || "ref".equals(key)*/) {
    			w.setName(value);
    		} else if (HIGHWAY.equals(key)) {
    			highWay(value);
    		} else if (WATERWAY.equals(key) && (RIVER.equals(value) || CANAL.equals(value))) {
    			if (waytype != WayType.TUNNEL) {
    				toAdd = true;
        			w.setType(WayType.RIVER);
    			}
    		} else if (WATERWAY.equals(key) && (STREAM.equals(value) || DITCH.equals(value))) {
    			if (waytype != WayType.TUNNEL) {
    				toAdd = true;
    				w.setType(WayType.STREAM);
    			}
    		} else if (RAILWAY.equals(key) 
    				&& !(ABANDONED.equals(value) || CONSTRUCTION.equals(value))) {
    			toAdd = true;
    			if (waytype != null) {
	    			switch (waytype) {
	    			case BRIDGE : w.setType(WayType.RAILWAY_B); break;
	    			case TUNNEL : w.setType(WayType.RAILWAY_TU); break;
	    			default : w.setType(WayType.RAILWAY);
	    			}
    			} else {
    				 w.setType(WayType.RAILWAY);
    			}
    		} else if (CYCLEWAY.equals(key)) {
    			cycleLane(value);
    		} else if (JUNCTION.equals(key) && ROUNDABOUT.equals(value)) {
    			w.setType(access ? WayType.ROUNDABOUT_T : WayType.ROUNDABOUT_NT);
    		} else if (TRACKTYPE.equals(key)) {
    			trackType(value);
    		} else if (BICYCLE.equals(key) && YES.equals(value)) {
    			bicycle();
    		} else if (FOOT.equals(key) && YES.equals(value)) {
    			foot();
    		} else if (ACCESS.equals(key) && (NO.equals(value) || PRIVATE.equals(value))) {
    			access();
    		} else if (ONEWAY.equals(key) && YES.equals(value)) {
    			w.setOneway(possibleOneWay);	
    		} else if (TUNNEL.equals(key) && YES.equals(value)) {
				bridgeAndTunnel(WayType.TUNNEL);
			} else if (BRIDGE.equals(key) && !AQUEDUCT.equals(value) && !NO.equals(value)) {
				bridgeAndTunnel(WayType.BRIDGE);
			} else if (BICYCLE.equals(key) && NO.equals(value)) {
				bicycle = false;
				WayType type = w.getType();
				if (type != null) {
					switch (type) {
					case FOOTANDCYCLE_NT : w.setType(WayType.FOOTWAY); break;
					case FOOTANDCYCLE_T : w.setType(WayType.FOOTWAY); break;
					case FOOTANDCYCLE_B_NT : w.setType(WayType.FOOTWAY_B); break;
					case FOOTANDCYCLE_B_T : w.setType(WayType.FOOTWAY_B); break;
					case FOOTANDCYCLE_TU_NT : w.setType(WayType.FOOTWAY_TU); break;
					case FOOTANDCYCLE_TU_T : w.setType(WayType.FOOTWAY_TU); break;
					}
				}
			} else if (ADMINL.equals(key))	
	    		checkAdminLevel(value);
	    }
	    
	    private void access() {
	    	access = false;
	    	WayType type = w.getType();
	    	if (type != null) {
	    		switch (type) {
	    		case CYCLEWAY_T : w.setType(WayType.CYCLEWAY_NT); break;
	    		case CYCLEWAY_B_T : w.setType(WayType.CYCLEWAY_B_NT); break;
	    		case CYCLEWAY_TU_T : w.setType(WayType.CYCLEWAY_TU_NT); break;
	    		case INCITY_T : w.setType(WayType.INCITY_NT); break;
	    		case INCITY_B_T : w.setType(WayType.INCITY_B_NT); break;
	    		case INCITY_TU_T : w.setType(WayType.INCITY_TU_NT); break;
	    		case CITYANDCYCLE_T : w.setType(WayType.CITYANDCYCLE_NT); break;
	    		case CITYANDCYCLE_B_T : w.setType(WayType.CITYANDCYCLE_B_NT); break;
	    		case CITYANDCYCLE_TU_T : w.setType(WayType.CITYANDCYCLE_TU_NT); break;
	    		case TERTIARY_T : w.setType(WayType.TERTIARY_NT); break;
	    		case TERTIARY_B_T : w.setType(WayType.TERTIARY_B_NT); break;
	    		case TERTIARY_TU_T : w.setType(WayType.TERTIARY_TU_NT); break;
	    		case TERTIARYCYCLE_T : w.setType(WayType.TERTIARYCYCLE_NT); break;
	    		case TERTIARYCYCLE_B_T : w.setType(WayType.TERTIARYCYCLE_B_NT); break;
	    		case TERTIARYCYCLE_TU_T : w.setType(WayType.TERTIARYCYCLE_TU_NT); break;
	    		case DIRTROAD_T : w.setType(WayType.DIRTROAD_NT); break;
	    		case PATH_T : w.setType(WayType.PATH_NT); break;
	    		case UNKNOWN_T : w.setType(WayType.UNKNOWN_NT); break;
	    		case UNKNOWN_B_T : w.setType(WayType.UNKNOWN_B_NT); break;
	    		case UNKNOWN_TU_T : w.setType(WayType.UNKNOWN_TU_NT); break;
	    		case PRIMARYCYCLE_T : w.setType(WayType.PRIMARYCYCLE_B_NT); break;
	    		case PRIMARYCYCLE_B_T : w.setType(WayType.PRIMARYCYCLE_B_NT); break;
	    		case PRIMARYCYCLE_TU_T : w.setType(WayType.PRIMARYCYCLE_TU_NT); break;
	    		case SECONDARY_T : w.setType(WayType.SECONDARY_NT); break;
	    		case SECONDARY_B_T : w.setType(WayType.SECONDARY_B_NT); break;
	    		case SECONDARY_TU_T : w.setType(WayType.SECONDARY_TU_NT); break;
	    		case SECONDARYCYCLE_T : w.setType(WayType.SECONDARYCYCLE_NT); break;
	    		case SECONDARYCYCLE_B_T : w.setType(WayType.SECONDARYCYCLE_B_NT); break;
	    		case SECONDARYCYCLE_TU_T : w.setType(WayType.SECONDARYCYCLE_TU_NT); break;
	    		case ROUNDABOUT_T : w.setType(WayType.ROUNDABOUT_NT); break;
	    		case SERVICE_T : w.setType(WayType.SERVICE_NT); break;
	    		case SERVICE_B_T : w.setType(WayType.SERVICE_B_NT); break;
	    		case SERVICE_TU_T : w.setType(WayType.SERVICE_TU_NT); break;
	    		case FOOTANDCYCLE_T : w.setType(WayType.FOOTANDCYCLE_NT); break;
	    		case FOOTANDCYCLE_TU_T : w.setType(WayType.FOOTANDCYCLE_TU_NT); break;
	    		case FOOTANDCYCLE_B_T : w.setType(WayType.FOOTANDCYCLE_B_NT); break;
	    		}
	    	}
	    }
	    
	    private void bicycle() {
	    	if (waytype != null) {
	    		switch (waytype) {
	    		case BRIDGE : 
	    			w.setType(access ? WayType.FOOTANDCYCLE_B_T : WayType.FOOTANDCYCLE_B_NT);
	    			waytype = access ? WayType.CYCLEWAY_B_T : WayType.CYCLEWAY_B_NT;
	    			break;
	    		case TUNNEL :
	    			w.setType(access ? WayType.FOOTANDCYCLE_TU_T : WayType.FOOTANDCYCLE_TU_NT);
	    			waytype = access ? WayType.CYCLEWAY_TU_T : WayType.CYCLEWAY_TU_NT;
	    			break;
	    		default : 
	    			w.setType(access ? WayType.FOOTANDCYCLE_T : WayType.FOOTANDCYCLE_NT);
	    			waytype = access ? WayType.CYCLEWAY_T : WayType.CYCLEWAY_NT;
	    		}
	    	} else {
	    		w.setType(access ? WayType.FOOTANDCYCLE_T : WayType.FOOTANDCYCLE_NT);
	    		waytype = access ? WayType.CYCLEWAY_T : WayType.CYCLEWAY_NT;
	    	}
	    }
	    
	    private void foot() {
	    	if (waytype != null) {
	    		switch (waytype) {
	    		case BRIDGE : 
	    			w.setType(access ? WayType.FOOTANDCYCLE_B_T : WayType.FOOTANDCYCLE_B_NT);
	    			waytype = WayType.FOOTWAY_B;
	    			break;
	    		case TUNNEL :
	    			w.setType(access ? WayType.FOOTANDCYCLE_TU_T : WayType.FOOTANDCYCLE_TU_NT);
	    			waytype = WayType.FOOTWAY_TU;
	    			break;
	    		default : 
	    			w.setType(access ? WayType.FOOTANDCYCLE_T : WayType.FOOTANDCYCLE_NT);
	    			waytype = WayType.FOOTWAY;
	    		}
	    	} else {
	    		w.setType(access ? WayType.FOOTANDCYCLE_T : WayType.FOOTANDCYCLE_NT);
	    		waytype = WayType.FOOTWAY;
	    	}
	    }
	    
	    private void bridgeAndTunnel(WayType type) {
	    	String typeName = "";
	    	if (type == WayType.BRIDGE)
	    		typeName = "_B";
	    	else if (type == WayType.TUNNEL)
	    		typeName = "_TU";
	    	switch (w.getType()) {
	    	case PRIMARY : w.setType(WayType.valueOf("PRIMARY" + typeName)); break;
	    	case PRIMARYCYCLE_T: w.setType(WayType.valueOf("PRIMARYCYCLE" + typeName + "_T")); break;
	    	case PRIMARYCYCLE_NT : w.setType(WayType.valueOf("PRIMARYCYCLE" + typeName + "_NT")); break;
	    	case SECONDARY_T: w.setType(WayType.valueOf("SECONDARY" + typeName + "_T")); break;
	    	case SECONDARY_NT : w.setType(WayType.valueOf("SECONDARY" + typeName + "_NT")); break;
	    	case SECONDARYCYCLE_T : w.setType(WayType.valueOf("SECONDARYCYCLE" + typeName +"_T")); break;
	    	case SECONDARYCYCLE_NT : w.setType(WayType.valueOf("SECONDARYCYCLE" + typeName +"_NT")); break;
	    	case TERTIARY_NT: w.setType(WayType.valueOf("TERTIARY" + typeName + "_NT")); break;
	    	case TERTIARY_T: w.setType(WayType.valueOf("TERTIARY" + typeName + "_T")); break;
	    	case TERTIARYCYCLE_T : w.setType(WayType.valueOf("TERTIARYCYCLE" + typeName + "_T")); break;
	    	case TERTIARYCYCLE_NT : w.setType(WayType.valueOf("TERTIARYCYCLE" + typeName + "_NT")); break;
	    	case INCITY_T: w.setType(WayType.valueOf("INCITY" + typeName + "_T")); break;
	    	case INCITY_NT: w.setType(WayType.valueOf("INCITY" + typeName + "_NT")); break;
	    	case CITYANDCYCLE_T: w.setType(WayType.valueOf("CITYANDCYCLE" + typeName + "_T")); break;
	    	case CITYANDCYCLE_NT: w.setType(WayType.valueOf("CITYANDCYCLE" + typeName + "_NT")); break;
	    	case RAILWAY : w.setType(WayType.valueOf("RAILWAY" + typeName)); break;
	    	case FOOTWAY : w.setType(WayType.valueOf("FOOTWAY" + typeName)); break;
	    	case FOOTANDCYCLE_T : w.setType(WayType.valueOf("FOOTANDCYCLE" + typeName + "_T")); break;
	    	case FOOTANDCYCLE_NT : w.setType(WayType.valueOf("FOOTANDCYCLE" + typeName + "_NT")); break;
	    	case CYCLEWAY_T : w.setType(WayType.valueOf("CYCLEWAY" + typeName + "_T")); break;
	    	case CYCLEWAY_NT : w.setType(WayType.valueOf("CYCLEWAY" + typeName + "_NT")); break;
	    	case MOTORWAY : w.setType(WayType.valueOf("MOTORWAY" + typeName)); break;
	    	case MOTORWAY_L : w.setType(WayType.valueOf("MOTORWAY" + typeName + "_L")); break;
	    	case FASTLANE : w.setType(WayType.valueOf("FASTLANE" + typeName)); break;
	    	case FASTLANE_L : w.setType(WayType.valueOf("FASTLANE" + typeName + "_L")); break;
	    	case UNKNOWN_T : w.setType(WayType.valueOf("UNKNOWN" + typeName + "_T")); break;
	    	case UNKNOWN_NT : w.setType(WayType.valueOf("UNKNOWN" + typeName + "_NT")); break;
	    	case SERVICE_T : w.setType(WayType.valueOf("SERVICE" + typeName + "_T")); break;
	    	case SERVICE_NT : w.setType(WayType.valueOf("SERVICE" + typeName + "_NT")); break;
	    	case RIVER : toAdd = false; break;
	    	case STREAM : toAdd = false; break;
	    	}
	    	if (waytype != null) {
		    	switch (waytype) {
		    	case CYCLEWAY_T : waytype = WayType.valueOf("CYCLEWAY" + typeName + "_T"); break;
		    	case CYCLEWAY_NT : waytype = WayType.valueOf("CYCLEWAY" + typeName + "_NT"); break;
		    	case FOOTWAY : waytype = WayType.valueOf("FOOTWAY" + typeName); break;
		    	default : waytype = type;
		    	}
	    	} else {
	    		waytype = type;
	    	}
	    }
	    
	    private void trackType(String value) {
	    	if (GRADE4.equals(value) || GRADE5.equals(value) || GRADE3.equals(value)) {
	    		switch (w.getType()) {
	    		case PATH_T: 
	    			w.setType(WayType.DIRTROAD_T);
	    			break;
	    		case PATH_NT:
	    			w.setType(WayType.DIRTROAD_NT);
	    			break;
	    		default:
	    			waytype = access ? WayType.DIRTROAD_T : WayType.DIRTROAD_NT;
	    		}
			}
	    }
	    
	    private void cycleLane(String value) {
	    	if (OPPOSITE.equals(value)
					|| OL.equals(value)
					|| OT.equals(value)) {
				w.setOneway(false);
				possibleOneWay = false;
			}
	    	switch (w.getType()) {
	    	case PRIMARY : cyclePrimary(); break;
	    	case SECONDARY_T : cycleSecondary(); break;
	    	case SECONDARY_NT : cycleSecondaryNotTraf(); break;
	    	case TERTIARY_T : cycleOutTraf(); break;
	    	case TERTIARY_NT : cycleOutNotTraf(); break;
	    	case INCITY_T : cycleCityTraf(); break;
	    	case INCITY_NT : cycleCityNotTraf(); break;
	    	default : cycleDefault();
	    		
	    	}
	    }
	    
	    private void cyclePrimary() {
	    	if (waytype != null) {
	    		switch (waytype) {
	    		case BRIDGE : w.setType(access ? WayType.PRIMARYCYCLE_B_T : WayType.PRIMARYCYCLE_B_NT); break;
	    		case TUNNEL : w.setType(access ? WayType.PRIMARYCYCLE_TU_T : WayType.PRIMARYCYCLE_TU_NT); break;
	    		default : w.setType(access ? WayType.PRIMARYCYCLE_T : WayType.PRIMARYCYCLE_NT);
	    		}
    		} else {
    			w.setType(access ? WayType.PRIMARYCYCLE_T : WayType.PRIMARYCYCLE_NT);
    		}
	    }
	    
	    private void cycleSecondary() {
	    	if (waytype != null) {
	    		switch (waytype) {
	    		case BRIDGE : w.setType(WayType.SECONDARYCYCLE_B_T); break;
	    		case TUNNEL : w.setType(WayType.SECONDARYCYCLE_TU_T); break;
	    		default : w.setType(WayType.SECONDARYCYCLE_T);
	    		}
    		} else {
    			w.setType(access ? WayType.SECONDARYCYCLE_T : WayType.SECONDARYCYCLE_NT);
    		}
	    }
	    
	    private void cycleSecondaryNotTraf() {
	    	if (waytype != null) {
	    		switch (waytype) {
	    		case BRIDGE : w.setType(WayType.SECONDARYCYCLE_B_NT); break;
	    		case TUNNEL : w.setType(WayType.SECONDARYCYCLE_TU_NT); break;
	    		default : w.setType(WayType.SECONDARYCYCLE_NT);
	    		}
    		} else {
    			w.setType(access ? WayType.SECONDARYCYCLE_T : WayType.SECONDARYCYCLE_NT);
    		}
	    }
	    
	    private void cycleOutTraf() {
	    	if (waytype != null) {
	    		switch (waytype) {
	    		case BRIDGE : w.setType(WayType.TERTIARYCYCLE_B_T); break;
	    		case TUNNEL : w.setType(WayType.TERTIARYCYCLE_TU_T); break;
	    		default : w.setType(WayType.TERTIARYCYCLE_T);
	    		}
    		} else {
    			w.setType(WayType.TERTIARYCYCLE_T);
    		}
	    }
	    
	    private void cycleOutNotTraf() {
	    	if (waytype != null) {
	    		switch (waytype) {
	    		case BRIDGE : w.setType(WayType.TERTIARYCYCLE_B_NT); break;
	    		case TUNNEL : w.setType(WayType.TERTIARYCYCLE_TU_NT); break;
	    		default : w.setType(WayType.TERTIARYCYCLE_NT);
	    		}
    		} else {
    			w.setType(WayType.TERTIARYCYCLE_NT);
    		}
	    }
	    
	    private void cycleCityTraf() {
	    	if (waytype != null) {
	    		switch (waytype) {
	    		case BRIDGE : w.setType(WayType.CITYANDCYCLE_B_T); break;
	    		case TUNNEL : w.setType(WayType.CITYANDCYCLE_TU_T); break;
	    		default : w.setType(WayType.CITYANDCYCLE_T);
	    		}
    		} else {
    			w.setType(WayType.CITYANDCYCLE_T);
    		}
	    }
	    
	    private void cycleCityNotTraf() {
	    	if (waytype != null) {
	    		switch (waytype) {
	    		case BRIDGE : w.setType(WayType.CITYANDCYCLE_B_T); break;
	    		case TUNNEL : w.setType(WayType.CITYANDCYCLE_TU_T); break;
	    		default : w.setType(WayType.CITYANDCYCLE_NT);
	    		}
    		} else {
    			w.setType(WayType.CITYANDCYCLE_NT);
    		}
	    }
	    
	    private void cycleDefault() {
	    	if (waytype != null) {
	    		switch (waytype) {
	    		case BRIDGE : waytype = access ? WayType.CYCLEWAY_B_T : WayType.CYCLEWAY_B_NT; break;
	    		case TUNNEL : waytype = access ? WayType.CYCLEWAY_TU_T : WayType.CYCLEWAY_TU_NT; break;
	    		default : waytype = access ? WayType.CYCLEWAY_T : WayType.CYCLEWAY_NT;
	    		}
    		} else {
    			waytype = access ? WayType.CYCLEWAY_T : WayType.CYCLEWAY_NT;
    		}
	    }
	    
	    private void highWay(String value) {
	    	if (MOTORWAY.equals(value)) {
				motorway("");
			} else if (ML.equals(value)) {
				motorway("_L");
			} else if (TRUNK.equals(value)) {
				fastLane("");
			} else if (TL.equals(value)) {
				fastLane("_L");
			} else if (PRIMARY.equals(value) || PL.equals(value)) {
				primary();
			} else if (SECONDARY.equals(value) || SL.equals(value)) {
				secondary();
			} else if (LS.equals(value) || RESIDENTIAL.equals(value)) {
				inCity();
			} else if (ROAD.equals(value) || UNC.equals(value)) {
				unclassified();
			} else if (TERTIARY.equals(value) || TEL.equals(value)) {
				tertiary();
			} else if (PEDESTRIAN.equals(value) || FOOTWAY.equals(value)) {
				footway();
			} else if (STEPS.equals(value)) {
				toAdd = true;
				w.setType(WayType.STEPS);
	    	} else if (CYCLEWAY.equals(value)) {
				cycleway();
			} else if (PATH.equals(value) || TRACK.equals(value)) {
				toAdd = true;
				if (waytype == WayType.DIRTROAD_T || waytype == WayType.DIRTROAD_NT) 
					w.setType(waytype);
				else
					w.setType(access ? WayType.PATH_T : WayType.PATH_NT);
			} else if (SERVICE.equals(value)) {
				service();
			}
				
	    }
	    
	    private void motorway(String suffix) {
	    	toAdd = true;
			if (waytype != null) {
				switch (waytype) {
				case BRIDGE : w.setType(WayType.valueOf("MOTORWAY_B" + suffix)); break;
				case TUNNEL : w.setType(WayType.valueOf("MOTORWAY_TU" + suffix)); break;
				default : w.setType(WayType.valueOf("MOTORWAY" + suffix));
				}
			} else {
				if (suffix.equals("_L"))
					w.setType(WayType.MOTORWAY_L);
				else
					w.setType(WayType.MOTORWAY);
			}
	    }
	    
	    private void fastLane(String suffix) {
	    	toAdd = true;
			if (waytype != null) {
				switch (waytype) {
				case BRIDGE : w.setType(WayType.valueOf("FASTLANE_B" + suffix)); break;
				case TUNNEL : w.setType(WayType.valueOf("FASTLANE_TU" + suffix)); break;
				default : w.setType(WayType.valueOf("FASTLANE" + suffix));
				}
			} else {
				if (suffix.equals("_L"))
					w.setType(WayType.FASTLANE_L);
				else 
					w.setType(WayType.FASTLANE);
			}
	    }

	    private void unclassified() {
	    	toAdd = true;
			if (waytype != null) {
				switch (waytype) {
				case BRIDGE : w.setType(access ? WayType.UNKNOWN_B_T : WayType.UNKNOWN_B_NT); break;
				case TUNNEL : w.setType(access ? WayType.UNKNOWN_TU_T : WayType.UNKNOWN_TU_NT); break;
				default: w.setType(access ? WayType.UNKNOWN_T : WayType.UNKNOWN_NT);
				}
			} else {
				w.setType(access ? WayType.UNKNOWN_T : WayType.UNKNOWN_NT);
			}
	    }
	    
	    private void footway() {
	    	toAdd = true;
			if (waytype != null) {
				switch (waytype) {
				case BRIDGE :
					if (!bicycle) w.setType(WayType.FOOTWAY_B);
					else w.setType(access ? WayType.FOOTANDCYCLE_B_T : WayType.FOOTANDCYCLE_B_NT);
					break;
				case TUNNEL :
					if (!bicycle) w.setType(WayType.FOOTWAY_TU);
					else w.setType(access ? WayType.FOOTANDCYCLE_TU_T : WayType.FOOTANDCYCLE_TU_NT);
					break;
				case CYCLEWAY_T : w.setType(WayType.FOOTANDCYCLE_T); break;
				case CYCLEWAY_NT : w.setType(WayType.FOOTANDCYCLE_NT); break;
				case CYCLEWAY_B_T : w.setType(WayType.FOOTANDCYCLE_B_T); break;
				case CYCLEWAY_B_NT : w.setType(WayType.FOOTANDCYCLE_B_NT); break;
				case CYCLEWAY_TU_T : w.setType(WayType.FOOTANDCYCLE_TU_T); break;
				case CYCLEWAY_TU_NT : w.setType(WayType.FOOTANDCYCLE_TU_NT); break;
				default :
					if (!bicycle) w.setType(WayType.FOOTWAY);
					else w.setType(access ? WayType.FOOTANDCYCLE_T : WayType.FOOTANDCYCLE_NT);
				}
			} else {
				if (!bicycle) w.setType(WayType.FOOTWAY);
				else w.setType(access ? WayType.FOOTANDCYCLE_T : WayType.FOOTANDCYCLE_NT);
			}
	    }
	    
	    private void cycleway() {
	    	toAdd = true;
			if (waytype != null) {
				switch (waytype) {
				case BRIDGE : w.setType(access ? WayType.CYCLEWAY_B_T : WayType.CYCLEWAY_B_NT); break;
				case TUNNEL : w.setType(access ? WayType.CYCLEWAY_TU_T : WayType.CYCLEWAY_TU_NT); break;
				case FOOTWAY : w.setType(access ? WayType.FOOTANDCYCLE_T : WayType.FOOTANDCYCLE_NT); break;		
				case FOOTWAY_B : w.setType(access ? WayType.FOOTANDCYCLE_B_T : WayType.FOOTANDCYCLE_B_NT); break;
				case FOOTWAY_TU : w.setType(access ? WayType.FOOTANDCYCLE_TU_T : WayType.FOOTANDCYCLE_TU_NT); break;
				default : w.setType(access ? WayType.CYCLEWAY_T : WayType.CYCLEWAY_NT);
				}	
			} else {
				w.setType(access ? WayType.CYCLEWAY_T : WayType.CYCLEWAY_NT);
			}
	    }
	    
	    private void service() {
	    	toAdd = true;
			if (waytype != null) {
				switch (waytype) {
				case BRIDGE : w.setType(access ? WayType.SERVICE_B_T : WayType.SERVICE_B_NT); break;
				case TUNNEL : w.setType(access ? WayType.SERVICE_TU_T : WayType.SERVICE_TU_NT); break;
				default : w.setType(access ? WayType.SERVICE_T : WayType.SERVICE_NT);
				}
			} else {
				w.setType(access ? WayType.SERVICE_T : WayType.SERVICE_NT);
			}
	    }
	    
	    private void primary() {
	    	toAdd = true;
	    	if (waytype != null) {
		    	switch (waytype) {
		    	case CYCLEWAY_T : w.setType(WayType.PRIMARYCYCLE_T); break;
		    	case CYCLEWAY_NT : w.setType(WayType.PRIMARYCYCLE_NT); break;
		    	case CYCLEWAY_B_T : w.setType(WayType.PRIMARYCYCLE_B_T); break;
		    	case CYCLEWAY_TU_T : w.setType(WayType.PRIMARYCYCLE_TU_T); break;
		    	case CYCLEWAY_B_NT : w.setType(WayType.PRIMARYCYCLE_B_NT); break;
		    	case CYCLEWAY_TU_NT : w.setType(WayType.PRIMARYCYCLE_TU_NT); break;
		    	case BRIDGE : w.setType(WayType.PRIMARY_B); break;
		    	case TUNNEL : w.setType(WayType.PRIMARY_TU); break;
		    	default : w.setType(WayType.PRIMARY);
		    	}
	    	} else {
	    		w.setType(WayType.PRIMARY);
	    	}
	    }
	    
	    private void secondary() {
	    	toAdd = true;
	    	if (waytype != null) {
		    	switch (waytype) {
		    	case CYCLEWAY_T : w.setType(WayType.SECONDARYCYCLE_T); break;
		    	case CYCLEWAY_NT : w.setType(WayType.SECONDARYCYCLE_NT); break;
		    	case CYCLEWAY_B_T : w.setType(WayType.SECONDARYCYCLE_B_T); break;
		    	case CYCLEWAY_TU_T : w.setType(WayType.SECONDARYCYCLE_TU_T); break;
		    	case CYCLEWAY_B_NT : w.setType(WayType.SECONDARYCYCLE_B_NT); break;
		    	case CYCLEWAY_TU_NT : w.setType(WayType.SECONDARYCYCLE_TU_NT); break;
		    	case BRIDGE : w.setType(access ? WayType.SECONDARY_B_T : WayType.SECONDARY_B_NT); break;
		    	case TUNNEL : w.setType(access ? WayType.SECONDARY_TU_T : WayType.SECONDARY_TU_NT); break;
		    	default : w.setType(access ? WayType.SECONDARY_T : WayType.SECONDARY_NT);
		    	}
	    	} else {
	    		w.setType(access ? WayType.SECONDARY_T : WayType.SECONDARY_NT);
	    	}
	    }
	    
	    private void inCity() {
	    	toAdd = true;
	    	if (waytype != null) {
		    	switch (waytype) {
		    	case CYCLEWAY_T : w.setType(WayType.CITYANDCYCLE_T); break;
		    	case CYCLEWAY_NT : w.setType(WayType.CITYANDCYCLE_NT); break;
		    	case CYCLEWAY_B_T : w.setType(WayType.CITYANDCYCLE_B_T); break;
		    	case CYCLEWAY_TU_T : w.setType(WayType.CITYANDCYCLE_TU_T); break;
		    	case CYCLEWAY_B_NT : w.setType(WayType.CITYANDCYCLE_B_NT); break;
		    	case CYCLEWAY_TU_NT : w.setType(WayType.CITYANDCYCLE_TU_NT); break;
		    	case BRIDGE : w.setType(access ? WayType.INCITY_B_T : WayType.INCITY_B_NT); break;
		    	case TUNNEL : w.setType(access ? WayType.INCITY_TU_T : WayType.INCITY_TU_NT); break;
		    	default : w.setType(access ? WayType.INCITY_T : WayType.INCITY_NT);
		    	}
	    	} else {
	    		w.setType(access ? WayType.INCITY_T : WayType.INCITY_NT);
	    	}
	    }
	    
	    private void tertiary() {
	    	toAdd = true;
	    	if (waytype != null) {
		    	switch (waytype) {
		    	case CYCLEWAY_T : w.setType(WayType.TERTIARYCYCLE_T); break;
		    	case CYCLEWAY_NT : w.setType(WayType.TERTIARYCYCLE_NT); break;
		    	case CYCLEWAY_B_T : w.setType(WayType.TERTIARYCYCLE_B_T); break;
		    	case CYCLEWAY_TU_T : w.setType(WayType.TERTIARYCYCLE_TU_T); break;
		    	case CYCLEWAY_B_NT : w.setType(WayType.TERTIARYCYCLE_B_NT); break;
		    	case CYCLEWAY_TU_NT : w.setType(WayType.TERTIARYCYCLE_TU_NT); break;
		    	case BRIDGE : w.setType(access ? WayType.TERTIARY_B_T : WayType.TERTIARY_B_NT); break;
		    	case TUNNEL : w.setType(access ? WayType.TERTIARY_TU_T : WayType.TERTIARY_TU_NT); break;
		    	default : w.setType(access ? WayType.TERTIARY_T : WayType.TERTIARY_NT);
		    	}
	    	} else {
	    		w.setType(access ? WayType.TERTIARY_T : WayType.TERTIARY_NT);
	    	}
	    }
	    
	   public List<Node> getUsedNodes() {
		   return new ArrayList<Node>(nodesUsed);
	   }
}
