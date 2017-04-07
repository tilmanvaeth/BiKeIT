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

public class CoordinateRect {

	private Coordinate upperLeft;
	private Coordinate lowerRight;
	
	public CoordinateRect(Coordinate uL, Coordinate lR) {
		this.upperLeft = uL;
		this.lowerRight = lR;
	}
	
	public Coordinate getUL() {
		return upperLeft;
	}
	
	public Coordinate getUR() {
		return new Coordinate(lowerRight.getLatitude(), upperLeft.getLongitude());
	}
	
	public Coordinate getLL() {
		return new Coordinate(upperLeft.getLatitude(), lowerRight.getLongitude());
	}
	
	public Coordinate getLR() {
		return lowerRight;
	}
	
	public Coordinate getCenter() {
		return new Coordinate((upperLeft.getLatitude() + lowerRight.getLatitude()) / 2,
				(upperLeft.getLongitude() + lowerRight.getLongitude()) / 2);
	}
	
	public boolean isContains(Coordinate coord) {
		return (coord.getLongitude() >= this.upperLeft.getLongitude() && coord.getLongitude() <= this.lowerRight.getLongitude())
				&& (coord.getLatitude() >= this.upperLeft.getLatitude() && coord.getLatitude() <= this.lowerRight.getLatitude());
	}
}
