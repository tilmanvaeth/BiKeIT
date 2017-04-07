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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import utilities.Logger;
import utilities.LoggerFactory;
import dataTypes.AltitudeData;
import dataTypes.Coordinate;

public class SRTMParser {

	private static final Logger logger = LoggerFactory.getLogger(SRTMParser.class);
	
	private AltitudeData[] altitudeData;
	private BufferedReader[] reader;
	
	/**
	 * 
	 * @param files
	 */
	public SRTMParser(File[] files) { 
		reader = new BufferedReader[files.length];
		try {
			for (int i = 0; i < files.length; i++) {
				reader[i] = new BufferedReader(new FileReader(files[i]));
			}
		} catch (FileNotFoundException e){
			System.err.println("Error: File not found.");
		}
		altitudeData = new AltitudeData[files.length];
	}
	/**
	 * 
	 * @throws IOException
	 */
	public void parseSRTM() throws IOException{
		logger.startAndLog("Start parsing STRM file.");
		String currentLine;
		String[] lineSplit;
		int width;
		int height;
		float[] xCords = new float[reader.length];
		float[] yCords = new float[reader.length];
		double cellSize;
	//	Coordinate[] cords = new Coordinate[reader.size()];
		for (int i = 0; i < reader.length; i++) {
			currentLine = reader[i].readLine();
			lineSplit = currentLine.split("\\s+");
			width = Integer.parseInt(lineSplit[1]);
			
			currentLine = reader[i].readLine();
			lineSplit = currentLine.split("\\s+");
			height = Integer.parseInt(lineSplit[1]);
			altitudeData[i] = new AltitudeData(width, height);
			
			currentLine = reader[i].readLine();
			lineSplit = currentLine.split("\\s+");
			xCords[i] = Float.parseFloat(lineSplit[1]);
			
			currentLine = reader[i].readLine();
			lineSplit = currentLine.split("\\s+");
			yCords[i] = Float.parseFloat(lineSplit[1]);
			altitudeData[i].setLowerLeftCorner(new Coordinate(yCords[i], xCords[i]));
			
			currentLine = reader[i].readLine();
			lineSplit = currentLine.split("\\s+");
			cellSize = Double.parseDouble(lineSplit[1]);
		
			altitudeData[i].setPixelSize(cellSize);

			reader[i].readLine();
		
		
			for (int y = (height - 1); y >= 0; y--) {
				currentLine = reader[i].readLine();
				lineSplit = currentLine.split("\\s+");
				for (int x = 0; x < width; x++) {
					altitudeData[i].setAltitude(x, y, Short.parseShort(lineSplit[x]));
				}
			}
		}
		logger.stopAndLog("Finished parsing " + reader.length + " SRTM files");
	}
	/**
	 * 
	 * @return
	 */
	public AltitudeData[] getAltitudeData() {
		return altitudeData;
	}
	
}
