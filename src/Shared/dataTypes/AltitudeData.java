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

public class AltitudeData {

	private double pixelSize;
	private short[][] data;
	
	private Coordinate lowerLeft;
	
	public AltitudeData(int boundX, int boundY) {
		data = new short[boundX][boundY];
	}
	
	public void setLowerLeftCorner(Coordinate lL) {
		this.lowerLeft = lL;
	}
	
	public void setPixelSize(double pixelSize) {
		this.pixelSize = pixelSize;
	}

	public void setData(short[][] data) {
		this.data = data;
	}

	public Coordinate getLowerLeftCorner() {
		return lowerLeft;
	}

	public double getPixelSize() {
		return pixelSize;
	}

	public short[][] getData() {
		return data;
	}
	
	public void setAltitude(int x, int y, short altitude){
		data[x][y] = altitude;
	}
	
	public short getAltitude(Coordinate c) {
		double y = (c.getLatitude() - lowerLeft.getLatitude()) / pixelSize;
		double x = (c.getLongitude() - lowerLeft.getLongitude()) / pixelSize;
		return data[(int) x][(int) y];
	}
	
	public int getWidth() {
		return data.length;
	}
	
	public int getHeight() {
		return data[0].length;
	}
	
}
