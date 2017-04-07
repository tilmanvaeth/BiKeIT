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


public class TerrainData {

	private GeometricDataStructure<Area> areas;
	private int zoomlevel;
	
	public TerrainData(int zoomlevel, GeometricDataStructure<Area> areas) {
		this.zoomlevel = zoomlevel;
		this.areas = areas;
	}
	
	
	public TerrainData(int zoomlevel) {
		this.zoomlevel = zoomlevel;
		this.areas = new GeometricDataStructure<Area>();
	}
	
	
	public void addArea(Area area) {
		List<TileCoord> keys = MercadorProjection.getAffectedTilesForZoom(area, this.zoomlevel);
		
		for (TileCoord key : keys) {
			areas.put(key, area);
		}
	}
	

	public Set<Area> getAreas(int x, int y) {
		return areas.getElements(x, y);
	}
	
	
	public Set<Area> getAllAreas() {
		return areas.getAllElements();
	}
	
	
	public int getZoomlevel() {
		return this.zoomlevel;
	}
	
	public Set<Entry<TileCoord, ArrayList<Area>>> getAllEntries() {
		return areas.getEntrySet();
	}
	
	public void trim() {
		areas.trim();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TerrainData))
			return false;
		
		return this.areas.equals(((TerrainData)o).areas);
	}
	
}
