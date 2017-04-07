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

package controllers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import mapModel.MapModel;
import mapModel.Node;
import runTimeData.PlannedWaypoints;
import utilities.MercadorProjection;
import dataTypes.Coordinate;
import dataTypes.Zoomlevel;


public class PlanningController {

	private PlannedWaypoints waypoints;
	
	public PlanningController() {
		waypoints = new PlannedWaypoints();
	}
	
	public void addWaypoint(Node node) {
		waypoints.addWaypoint(node);
		System.out.println("waypoint added: Node(" + node.getId() +  ") at " + node.getPosition().toString());
	}
	
	public void deleteWaypoint(int i) {
		waypoints.deleteWaypoint(i);
		System.out.println("waypoint deleted " + i);
	}
	
	public void moveWaypoint(int index, Node dest) {
		waypoints.moveWaypoint(index, dest);
	}
	
	public boolean hasEnoughWaypoints() {
		return waypoints.getSize() >= 2;
	}
	
	public boolean isWaypoint(Node node) {
		return waypoints.getWaypoints().contains(node);
	}
	
	public void reset() {
		waypoints.reset();
		System.out.println("waypoints reseted");
	}
	
	public PlannedWaypoints getWaypoints() {
		return waypoints;
	}
	
	public void loadWaypointsFromFile(MapModel mapModel, File file, MapController mapController) {
		
		InputStream fileInStream;
		DataInputStream dataInStream = null;
		
		try {
			fileInStream = new FileInputStream(file);
			dataInStream = new DataInputStream (fileInStream);
		} catch (FileNotFoundException e) {
			System.err.println("Could not read the way point file.");
			e.printStackTrace();
		}
		
		if (dataInStream != null) {	
			try {
				int size = dataInStream.readInt();
				
				if (size == 0) return;
				
				PlannedWaypoints plannedWaypoints = new PlannedWaypoints();
				
				for (int i = 0; i < size; i++) {
					int nodeId = dataInStream.readInt();
					int tileX = dataInStream.readInt();
					int tileY = dataInStream.readInt();
					
					Node toAdd = mapModel.getNodeData().getNode(nodeId, tileX, tileY);
					
					if (toAdd != null) plannedWaypoints.addWaypoint(toAdd);
					else System.err.println("Read a way point that could not be found.");
				}
				
				this.waypoints =  plannedWaypoints;
				
				mapController.setZoomlevel(new Zoomlevel(dataInStream.readInt()));
				mapController.centerMapAt(new Coordinate(dataInStream.readFloat(), dataInStream.readFloat()));
				
			} catch (IOException e) {
				System.err.println("An error occured while reading way points from file");
				e.printStackTrace();
			}		
		}
		
	}
	
	public void saveWaypointsToFile(MapModel mapModel, File file, Zoomlevel zoomlevel, Coordinate coordinate) {
		FileOutputStream fileOutStream;
		DataOutputStream dataOutStream = null;
		
		try {
			fileOutStream = new FileOutputStream (file);
			dataOutStream = new DataOutputStream (fileOutStream);
		} catch (FileNotFoundException e) {
			System.err.println("Could not save way points to file.");
			e.printStackTrace();
		}
		
		if (dataOutStream != null) {
			try {
								
				dataOutStream.writeInt(waypoints.getWaypoints().size());
				
				for (Node n: waypoints.getWaypoints()) {
					dataOutStream.writeInt(n.getId());
					
					int[] nodeCoords = MercadorProjection.getIndexXY(new Coordinate(n.getLatitude(), n.getLongitude()),
							mapModel.getNodeData().getNativeZoomlevel());
					
					dataOutStream.writeInt(nodeCoords[0]);
					dataOutStream.writeInt(nodeCoords[1]);
				}
				
				dataOutStream.writeInt(zoomlevel.getValue());
				dataOutStream.writeFloat(coordinate.getLatitude());
				dataOutStream.writeFloat(coordinate.getLongitude());
				
			} catch (IOException e) {
				System.err.println("An error occured while writing way points to file.");
				e.printStackTrace();
			}
		}
	}
}
