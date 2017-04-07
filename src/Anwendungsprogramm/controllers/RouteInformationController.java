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

import java.util.List;

import mapModel.Edge;
import runTimeData.CalculatedRoute;


public class RouteInformationController {
	
	private CalculatedRoute route;
	private int estimatedDuration = 0;
	private int altitudeMeters = 0;
	private int routeLength = -1;
	//Driving speed in km/h
	private static final double SPEED = 17.0;
	
	public void setCalculatedRoute(CalculatedRoute r) {
		route = r;
	}
	
	public void process() {
		routeLength = calculateRouteLength();
		altitudeMeters = calculateAltitudeMeters();
		estimatedDuration = calculateEstimatedDuration();
	}
	
	public int getEstimatedDuration() {
		return estimatedDuration;
	}
	
	public static final double getSpeed() {
		return SPEED;
	}
	
	public int getAltitudeMeters() {
		return altitudeMeters;
	}
	
	public int getRouteLength() {
		return routeLength;
	}
	
	/*
	 * The estimated trip time in seconds
	 */
	private int calculateEstimatedDuration() {
		if (routeLength==0) calculateRouteLength();
		double secondsPerMeter = 1.0/(SPEED/3.6);
		
		return (int) Math.round(routeLength*secondsPerMeter);
	}
	
	/*
	 * Sum up the rises in meters
	 */
	private int calculateAltitudeMeters() {
		
		List<Edge> edges = route.getEdges();
		double total = 0;
		
		for (Edge e : edges) {
			
			int a1 = e.getSource().getAltitude();
			int a2 = e.getTarget().getAltitude();
			
			if (a2>a1) {
				total += (a2-a1);
			}
			
		}
		
		return (int) Math.round(total);
		
	}
	
	/*
	 * The route length in meters
	 */
	private int calculateRouteLength() {
		
		List<Edge> edges = route.getEdges();
		double length = 0;
		
		for (Edge e : edges) {
			length += e.getLength();
		}
		
		return (int) Math.round(length);
		
	}
	
}
