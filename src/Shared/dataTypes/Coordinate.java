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

package dataTypes;

import java.text.DecimalFormat;

import utilities.DistanceBetweenCoordinates;







public class Coordinate {

	private float lat, lon;
	
	private static DecimalFormat format = new DecimalFormat("###0.0000");
	
	public Coordinate(float lat, float lon) {
		this.lat = lat;
		this.lon = lon;
	}
	
	public float getLatitude() {
		return this.lat;
	}
	
	public float getLongitude() {
		return this.lon;
	}
	
	public double distanceToCoordinate(Coordinate c) {
		return DistanceBetweenCoordinates.distance(this, c);
	}
	
	@Override
	public String toString() {
		return "lat: " + format.format(lat) + " lon: " + format.format(lon);
	}
	
}
