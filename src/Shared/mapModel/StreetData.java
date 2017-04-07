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

package mapModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import utilities.GeometricDataStructure;
import utilities.MercadorProjection;
import dataTypes.TileCoord;


public class StreetData {

	private GeometricDataStructure<Way> ways;
	private int zoomlevel;
	
	public StreetData(int zoomlevel, GeometricDataStructure<Way> ways) {
		this.zoomlevel = zoomlevel;
		this.ways = ways;
	}
	
	
	public StreetData(int zoomlevel) {
		this.zoomlevel = zoomlevel;
		this.ways = new GeometricDataStructure<Way>();
	}
	
	
	public void addWay(Way way) {
		List<TileCoord> keys = MercadorProjection.getAffectedTilesForZoom(way, this.zoomlevel);
		
		for (TileCoord key : keys) {
			ways.put(key, way);
		}
	}
	

	public Set<Way> getWays(int x, int y) {
		return ways.getElements(x, y);
	}
	
	
	public Set<Way> getAllWays() {
		return ways.getAllElements();
	}
	
	
	public int getZoomlevel() {
		return this.zoomlevel;
	}
	
	public Set<Entry<TileCoord, ArrayList<Way>>> getAllEntries() {
		return ways.getEntrySet();
	}
	
	public void trim() {
		ways.trim();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof StreetData))
			return false;
		
		return this.ways.equals(((StreetData)o).ways);
	}
	
}
