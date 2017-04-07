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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mapModel.Area;
import mapModel.City;
import mapModel.Node;
import mapModel.Way;

import org.xml.sax.SAXException;

import utilities.Logger;
import utilities.LoggerFactory;
import dataTypes.AreaType;
import dataTypes.WayType;

public class OSMParser {

	private static final Logger logger = LoggerFactory.getLogger(OSMParser.class);
	
	private List<Node> nodes;
	private List<Way> ways;
	private List<Area> areas;
	private List<City> cities;
	private String pathToFile;
	private SAXParser parser;
	private SAXParserFactory factory = SAXParserFactory.newInstance();
	private OSMHandler handler = new OSMHandler(this);
	
	/**
	 * This method constructs a new OSMParser-object
	 * and tries to get a SAXParser.
	 * If an error occur an errormessage is displayed and the program will shut down.
	 * 
	 * @param ptF - The path to the file
	 */
	public OSMParser(String ptf) {
		this.pathToFile = ptf;
		cities = new LinkedList<City>();
		ways = new LinkedList<Way>();
		areas = new LinkedList<Area>();
		try {
			parser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			System.err.println("Unable to create new XML-SAXParser.");
			System.exit(0);
		} catch (SAXException e) {
			System.err.println("Unable to create new XML-SAXParser.");
			System.exit(0);
		}
	}
	
	/**
	 * This method starts the parsing. 
	 * If anything went wrong an errormessage will be displayed and the program will shut down.
	 * After the parser terminates, all areas, which are marked as squares will be split into ways, so
	 * they are trafficable. Then all cities without a name will be deleted.
	 */
	public void parseOSM() {
		logger.startAndLog("Start parsing.");
		try {
			parser.parse(pathToFile, handler);
		} catch (SAXException e) {
			System.err.println("Parsing error.");
			System.exit(0);
		} catch (IOException e) {
			System.err.println("Unable to read file.");
			System.exit(0);
		}
		logger.stopAndLog("Finished parsing");

		this.nodes = handler.getUsedNodes();
		for (Area a : areas) {
			if (a.getType() == AreaType.SQUARE) {
				Way w = new Way();
				w.setName(a.getName());
				w.setType(WayType.SQUARE_T);
				w.setNodes(a.getNodes());
				ways.add(w);
			}
		}
		logger.log("Transformed squares to ways.");
		for (int i = 0; i < cities.size(); i++) {
			if (cities.get(i).getName() == null) 
				cities.remove(i);
		}
		logger.log("Removed nameless cities.");
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public List<Node> getNodes() {
		return nodes;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Way> getWays() {
		return ways;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Area> getAreas() {
		return areas;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<City> getCities() {
		return cities;
	}
}
