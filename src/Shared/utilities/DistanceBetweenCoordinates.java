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

import dataTypes.Coordinate;

public class DistanceBetweenCoordinates {

	public static double distance(Coordinate c1, Coordinate c2) {
		double lon1 = c1.getLongitude() / 180 * Math.PI;
		double lon2 = c2.getLongitude() / 180 * Math.PI;
		double lat1 = c1.getLatitude() / 180 * Math.PI;
		double lat2 = c2.getLatitude() / 180 * Math.PI;
		double radius = 6378.137;
		double temp1 = Math.sin(lat1)*Math.sin(lat2);
		double temp2 = Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1);
		double result = Math.acos(temp1 + temp2) * radius * 1000;
		return result;
	}
	
}
