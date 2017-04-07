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

public class TileCoord {
	private int indexX;
	private int indexY;
	
	public TileCoord(int indexX, int indexY) {
		this.indexX = indexX;
		this.indexY = indexY;
	}
	
	public int getX() {
		return indexX;
	}
	
	public int getY() {
		return indexY;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TileCoord))
			return false;
		
		TileCoord other = (TileCoord)o;
		return other.indexX == this.indexX && other.indexY == this.indexY;
	}
	
	@Override
	public int hashCode() {
		int hash = 11 * indexX + 7 * indexY;
		return hash;
	}
	
	@Override
	public String toString() {
		return "("+indexX +"|"+ indexY+")";
	}
}
