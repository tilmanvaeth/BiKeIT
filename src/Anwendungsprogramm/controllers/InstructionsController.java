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

import java.util.LinkedList;
import java.util.List;

import mapModel.Edge;
import mapModel.Node;
import mapModel.Way;
import runTimeData.CalculatedRoute;
import runTimeData.PlannedWaypoints;
import utilities.MercadorProjection;


public class InstructionsController {
	
	private LinkedList<String> list;
	private CalculatedRoute route;
	private PlannedWaypoints waypoints;
	
	private enum Direction {
		
		STRAIGHT_ON, LEFT, RIGHT, HALF_LEFT, HALF_RIGHT, TURN_OVER, SHARP_LEFT, SHARP_RIGHT;
		
		@Override
		public String toString() {
			switch(this) {
			case STRAIGHT_ON: return "↑";
			case LEFT: return "↰";
			case RIGHT: return "↱";
			case HALF_LEFT: return "↖";
			case HALF_RIGHT: return "↗";
			case TURN_OVER: return "↷";
			case SHARP_LEFT: return "↙";
			case SHARP_RIGHT: return "↘";
			default: return "undefined";
			}
		}
		
	};
	
	private enum Orientation {
		
		NORTH, SOUTH, EAST, WEST, NORTH_EAST, NORTH_WEST, SOUTH_WEST, SOUTH_EAST;
		
		@Override
		public String toString() {
			switch(this) {
			case NORTH: return "Norden";
			case SOUTH: return "Süden";
			case EAST: return "Osten";
			case WEST: return "Westen";
			case NORTH_EAST: return "Nordosten";
			case NORTH_WEST: return "Nordwesten";
			case SOUTH_EAST: return "Südosten";
			case SOUTH_WEST: return "Südwesten";
			default: return "undefined";
			}
		}
		
	};
	
	private enum SteetNameType {
		MALE, FEMALE, ID, SQUARE;
	}
	
	private class BoolTuple {
		public boolean a;
		public boolean b;
		public BoolTuple(boolean a, boolean b) {
			this.a = a;
			this.b = b;
		}
	}
	
	public void setCalculatedRoute(CalculatedRoute r) {
		route = r;
	}
	
	public void setPlannedWaypoints(PlannedWaypoints p) {
		waypoints = p;
	}
	
	public void process() {

		list = new LinkedList<String>();
		double distance = 0;
		if (route==null)return;
		List<Edge> edges = route.getEdges();
		if (edges == null || edges.size() < 1) return;
		
		// instruction counter
		int num = 1;
		
		/* -- first instruction -- */
		Edge e0 = edges.get(0);
		Way currentWay = e0.getWay();
		String currentStreet = "";
		
		String startInstruction = num+". 0m ⚑ Beginnen Sie ihre Fahrt ";
		if (currentWay != null && currentWay.getName() != null && currentWay.getName() != "") {
			
			startInstruction += "auf ";
			
			currentStreet = currentWay.getName();
			String streetName = prepareStreetName(currentStreet);
			
			SteetNameType gender = getSteetNameType(streetName);
			switch (gender) {
			case MALE:
			case SQUARE:	startInstruction += "dem"; break;
			case FEMALE:
			case ID: 		startInstruction += "der"; break;
			}
			
			startInstruction += " "+streetName+" ";
			
		}
		
		startInstruction += "in Richtung "+getOrientation(e0)+".";
		
		list.add(startInstruction);
		num++;
		/* --- */
		
		int numPossibilitiesLeft = 0;
		int numPossibilitiesRight = 0;
		distance = e0.getLength();
		double distanceSinceLastInstruction = e0.getLength();
		int numTargets = 1;
		
		if (edges.size()==1) {
			if (Math.round(distanceSinceLastInstruction)>0) {
				list.add(num+". "+Math.round(distance)+"m ⚐ Nach "+Math.round(distanceSinceLastInstruction)+"m haben Sie Ihr Ziel ereicht.");
			} else {
				list.add(num+". "+Math.round(distance)+"m ⚐ Sie haben Ihr Ziel ereicht.");
			}
		}
		
		for (int i=0; i<edges.size()-1; i++) {
			
			Edge e = edges.get(i+1);
			distance += e.getLength();
			distanceSinceLastInstruction += e.getLength();
			
			// do we have reached a target (and not the last target)?
			if (waypoints.getWaypoints().contains(e.getSource()) && i+1<edges.size()-1) {
				
				if (Math.round(distanceSinceLastInstruction)>0) {
					list.add(num+". "+Math.round(distance)+"m ⚐ Nach "+Math.round(distanceSinceLastInstruction)+"m haben Sie Ihr "+numTargets+". Zwischenziel ereicht.");
				} else {
					list.add(num+". "+Math.round(distance)+"m ⚐ Sie haben Ihr "+numTargets+". Zwischenziel ereicht.");
				}
				
				num++;
				numPossibilitiesLeft = 0;
				numPossibilitiesRight = 0;
				distanceSinceLastInstruction = 0;
				numTargets++;
				
			}
			
			// is it a crossroad?
			if (e.getSource().getEdges().size() > 2 ) {
				
				BoolTuple turningPossibilities = getTurningPossibilities(edges.get(i));
				if (turningPossibilities.a) numPossibilitiesLeft++;
				if (turningPossibilities.b) numPossibilitiesRight++;
				
				double angle = getAngle(edges.get(i), e);
				Direction direction = getDirection(angle);
				boolean hasName = e.getWay() != null && e.getWay().getName() != null && e.getWay().getName() != "";
				
				// print counter, distance and a direction image
				String instruction = num+". "+Math.round(distance)+"m "+direction+" ";
				
				if (direction == Direction.STRAIGHT_ON && e.getWay().getName().equals("")) {
					// WORKAROUND: the _IF_ is for the following:
					// sometimes every second edge doesn't have a street name. curious.
					numPossibilitiesLeft = 0;
					numPossibilitiesRight = 0;
					continue;
				}
				
				if (direction==Direction.TURN_OVER) {
					
					instruction += "Bitte wenden Sie.";
					
				// this edge is on the same street as the last one
				} else if (e.getWay().getName().equals(currentStreet)
						|| e.getWay().equals(currentWay)) {
					
					if (direction == Direction.STRAIGHT_ON
							|| direction == Direction.HALF_RIGHT
							|| direction == Direction.HALF_LEFT) {
						continue;
					}
					
					if ( hasName ) { 
						
						instruction += "Folgen Sie ";
						String streetName = prepareStreetName(e.getWay().getName());
						
						SteetNameType gender = getSteetNameType(streetName);
						switch (gender) {
						case MALE:
						case SQUARE:	instruction += "dem "; break;
						case FEMALE:
						case ID: 		instruction += "der "; break;
						}
						
						instruction += streetName;
						
					} else {
						instruction += "Folgen Sie dem Straßenverlauf";
					}
					
					switch(direction) {
					case LEFT: instruction += " (links)"; break;
					case RIGHT: instruction += " (rechts)"; break;
					case HALF_LEFT: instruction += " (halb links)"; break;
					case HALF_RIGHT: instruction += " (halb rechts)"; break;
					case TURN_OVER: instruction += ""; break;
					case SHARP_LEFT: instruction += " (scharf links)"; break;
					case SHARP_RIGHT: instruction += " (scharf rechts)"; break;
					default: break;
					}
					
					instruction += ".";
					
				} else {

					currentStreet = e.getWay().getName();
					currentWay = e.getWay();
					
					String sNPL = numPossibilitiesLeft>1 ? "die "+numPossibilitiesLeft+". Möglichkeit " : "";
					String sNPR = numPossibilitiesRight>1 ? "die "+numPossibilitiesRight+". Möglichkeit " : "";
					
					switch(direction) {
					case STRAIGHT_ON: instruction += "Fahren Sie geradeaus"; break;
					case LEFT: instruction += "Biegen Sie "+sNPL+"links ab"; break;
					case RIGHT: instruction += "Biegen Sie "+sNPR+"rechts ab"; break;
					case HALF_LEFT: instruction += "Biegen Sie "+sNPL+"halb links ab"; break;
					case HALF_RIGHT: instruction += "Biegen Sie "+sNPR+"halb rechts ab"; break;
					case TURN_OVER: instruction += "Bitte wenden Sie"; break;
					case SHARP_LEFT: instruction += "Biegen Sie "+sNPL+"scharf links ab"; break;
					case SHARP_RIGHT: instruction += "Biegen Sie "+sNPR+"scharf rechts ab"; break;
					default: break;
					}
					
					if (hasName) { 
						
						String streetName = prepareStreetName(e.getWay().getName());
						
						SteetNameType gender = getSteetNameType(streetName);
						switch (gender) {
						case MALE: instruction += " in den "; break;
						case FEMALE: instruction += " in die "; break;
						case ID: instruction += " auf die "; break;
						case SQUARE: instruction += " auf den "; break;
						}
						
						instruction += prepareStreetName(e.getWay().getName());
						
					}					
					
					instruction += ".";
					
				}
				
				list.add(instruction);
				num++;
				numPossibilitiesLeft = 0;
				numPossibilitiesRight = 0;
				distanceSinceLastInstruction = 0;
			
			}
			
		}
		
		// last instruction
		if (Math.round(distanceSinceLastInstruction)>0) {
			list.add(num+". "+Math.round(distance)+"m ⚐ Nach "+Math.round(distanceSinceLastInstruction)+"m haben Sie Ihr Ziel ereicht.");
		} else {
			list.add(num+". "+Math.round(distance)+"m ⚐ Sie haben Ihr Ziel ereicht.");
		}
		
		num++;
		
	}
	
	public LinkedList<String> getInstructions() {
		
		if (list==null) {
			return new LinkedList<String>();
		}
		
		return list;
		
	}
	
	private BoolTuple getTurningPossibilities(Edge edge) {

		BoolTuple result = new BoolTuple(false, false);
		Node node = edge.getTarget();
		
		for (Edge e : node.getEdges()) {
			
			// is this an incoming edge?
			if (!e.getSource().equals(node)) continue;
			
			Direction direction = getDirection(getAngle(edge, e));
			
			switch(direction) {
			case HALF_LEFT:
			case LEFT:
			case SHARP_LEFT:
				result.a = true; break;
			case HALF_RIGHT:
			case RIGHT:
			case SHARP_RIGHT:
				result.b = true; break;
			}
			
		}
		
		return result;
		
	}
	
	private double getAngle(Edge e1, Edge e2) {
		
		double a1 = getAbsoluteAngle(e1);
		double a2 = getAbsoluteAngle(e2);
		
		double a = a2-a1;
		if (a<=(-1)*Math.PI) {
			a += 2*Math.PI;
		} else if (a>Math.PI) {
			a -= 2*Math.PI;
		}
		
		return a;
	}
	
	private double getAbsoluteAngle(Edge e) {
		
		double x1 = MercadorProjection.longitudeToPixelX(e.getSource().getPosition().getLongitude(), 18);
		double y1 = MercadorProjection.latitudeToPixelY(e.getSource().getPosition().getLatitude(), 18);
		double x2 = MercadorProjection.longitudeToPixelX(e.getTarget().getPosition().getLongitude(), 18);
		double y2 = MercadorProjection.latitudeToPixelY(e.getTarget().getPosition().getLatitude(), 18);
		double dx = x2-x1;
		double dy = y2-y1;
		
		double angle;
		double length = Math.sqrt(dx*dx+dy*dy);
		if (length==0) return 0.0;
		
		if (dx>=0 && dy>=0) {
			angle = Math.acos(dx/length);
		} else if (dx>=0 && dy<0) {
			angle = 2*Math.PI - Math.acos(dx/length);
		} else if (dx<0 && dy>=0) {
			angle = Math.PI - Math.acos(-dx/length);
		} else {
			angle = Math.PI + Math.acos(-dx/length);
		}
		
		angle -= 3.0/2.0*Math.PI;
		if (angle <= (-1)*Math.PI)
			angle += 2*Math.PI;
		
		return angle;
		
	}
	
	private Orientation getOrientation(Edge e) {
		
		double angle = getAbsoluteAngle(e);
		double deg = Math.toDegrees(angle);
		float step = 360.f/16.f;
		
		if (deg >= -step && deg < step) {
			return Orientation.NORTH;
		} else if (deg >= step && deg < 3*step) {
			return Orientation.NORTH_EAST;
		} else if (deg >= 3*step && deg < 5*step) {
			return Orientation.EAST;
		} else if (deg >= 5*step && deg < 7*step) {
			return Orientation.SOUTH_EAST;
		} else if (deg >= 7*step || deg < -7*step) {
			return Orientation.SOUTH;
		} else if (deg >= -7*step || deg < -5*step) {
			return Orientation.SOUTH_WEST;
		} else if (deg >= -5*step && deg < -3*step ) {
			return Orientation.WEST;
		} else {
			return Orientation.NORTH_WEST;
		}
		
	}
	
	private Direction getDirection(double angle) {
		
		double deg = Math.toDegrees(angle);
		
		if (deg >= -15 && deg < 15 ) {
			return Direction.STRAIGHT_ON;
		} else if (deg >= 15 && deg < 45 ) {
			return Direction.HALF_RIGHT;
		} else if (deg >= 45 && deg < 135 ) {
			return Direction.RIGHT;
		} else if (deg >= 135 && deg < 178 ) {
			return Direction.SHARP_RIGHT;
		} else if (deg >= 178 || deg < -178 ) {
			return Direction.TURN_OVER;
		} else if (deg >= -178 && deg < -135 ) {
			return Direction.SHARP_LEFT;
		} else if (deg >= -135 && deg < -45 ) {
			return Direction.LEFT;
		} else {
			return Direction.HALF_LEFT;
		}
		
	}
	
	private String prepareStreetName(String name) {
		
		String lc = name.toLowerCase();
		
		if (lc.contains("straße")
				|| lc.contains("allee")
				|| lc.contains("weg")
				|| lc.contains("pfad")
				|| lc.contains("promenade")
				|| lc.contains("platz")
				|| name.startsWith("A ")
				|| name.startsWith("B ")
				|| name.startsWith("K ")
				|| name.startsWith("L ")) {
			return name;
		} else {
			return "Straße "+name;
		}
		
	}
	
	private SteetNameType getSteetNameType(String name) {
		
		String lc = name.toLowerCase();
		
		if (name.startsWith("A ")
				|| name.startsWith("B ")
				|| name.startsWith("K ")
				|| name.startsWith("L ")) {
			return SteetNameType.ID;
		} else if (lc.contains("weg")
				|| lc.contains("pfad")) {
			return SteetNameType.MALE;
		} else if (lc.contains("platz")) {
			return SteetNameType.SQUARE;
		} else {
			return SteetNameType.FEMALE;
		}
		
	}
	
}
