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

import java.io.File;
import java.io.IOException;
import java.util.List;

import mapModel.Area;
import mapModel.City;
import mapModel.Node;
import mapModel.Way;
import dataTypes.AltitudeData;

public class ParserController {
	
	private List<Node> nodes;
	private List<Way> ways;
	private List<Area> areas;
	private List<City> cities;
	private AltitudeData[] altitudeData;
	
	private OSMParser osmParser;
	private SRTMParser srtmParser;
	
	public List<Node> getNodes() {
		return nodes;
	}
	public List<Way> getWays() {
		return ways;
	}
	public List<Area> getAreas() {
		return areas;
	}
	public List<City> getCities() {
		return cities;
	}
	
	public AltitudeData[] getAltitudeData() {
		return altitudeData;
	}
	
	public void parseOSMData(String ptf) {
		osmParser = new OSMParser(ptf);
		osmParser.parseOSM();
		nodes = osmParser.getNodes();
		ways = osmParser.getWays();
		areas = osmParser.getAreas();
		cities = osmParser.getCities();
		
		osmParser = null;
		
	}
		
	public void parseSRTMData(File[] files) {
		srtmParser = new SRTMParser(files);
		try {
			srtmParser.parseSRTM();
		} catch (IOException e) {
			System.err.println("Error: File corrupted.");
		}
		altitudeData = srtmParser.getAltitudeData();
	}
	
}
