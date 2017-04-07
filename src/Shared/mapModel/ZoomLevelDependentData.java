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

import java.util.HashSet;
import java.util.Set;

public class ZoomLevelDependentData {

	private final StreetData streetData;
	private final TerrainData terrainData;
	private final int nativeZoomlevel;
	private final int maxZoomlevel;
	
	public ZoomLevelDependentData(StreetData streetData, TerrainData terrainData, int zoomlevelMax) {
		this.streetData = streetData;
		this.terrainData = terrainData;
		if (streetData.getZoomlevel() != terrainData.getZoomlevel()) {
			System.err.println("native Zoomlevels do not match!");
		}
		this.nativeZoomlevel = streetData.getZoomlevel();
		this.maxZoomlevel = zoomlevelMax;
	}
	
	public int getNativeZoomlevel() {
		return this.nativeZoomlevel;
	}
	
	public int getMaximalZoomlevel() {
		return this.maxZoomlevel;
	}
	
	public Set<Way> getWays(int indexX, int indexY, int zoomlevel) {
		if (zoomlevel == this.nativeZoomlevel) {
			return streetData.getWays(indexX, indexY);
		}
		else if (zoomlevel < this.nativeZoomlevel) {
			Set<Way> result = new HashSet<Way>(20);
			
			int factor = (1 << (this.nativeZoomlevel - zoomlevel));
			
			int xmin = indexX * factor;
			int ymin = indexY * factor;
			int xmax = xmin + factor;
			int ymax = ymin + factor;
			
			for (int x = xmin; x < xmax; x++) {
				for(int y = ymin; y < ymax; y++) {
					for (Way w : streetData.getWays(x,y)) {
						result.add(w);
					}
				}
			}
			return result;
		} else {
			int divident = (1 << (zoomlevel - this.nativeZoomlevel));
			
			return streetData.getWays(indexX / divident, indexY / divident);
		}
	}
	
	public Set<Area> getAreas(int indexX, int indexY, int zoomlevel) {
		if (zoomlevel == this.nativeZoomlevel) {
			return terrainData.getAreas(indexX, indexY);
		}
		else if (zoomlevel < this.nativeZoomlevel) {
			Set<Area> result = new HashSet<Area>(20);
			
			int factor = (1 << (this.nativeZoomlevel - zoomlevel));
			
			int xmin = indexX * factor;
			int ymin = indexY * factor;
			int xmax =  xmin + factor;
			int ymax = ymin + factor;
			
			for (int x = xmin; x < xmax; x++) {
				for(int y = ymin; y < ymax; y++) {
					for (Area a : terrainData.getAreas(x,y)) {
						result.add(a);
					}
				}
			}
			return result;
		} else {
			int divident = (1 << (zoomlevel - this.nativeZoomlevel));
			
			return terrainData.getAreas(indexX / divident, indexY / divident);
		}
	}
	
	public StreetData getStreetData() {
		return streetData;
	}
	
	public TerrainData getTerrainData() {
		return terrainData;
	}
	
	public void trim() {
		this.streetData.trim();
		this.terrainData.trim();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ZoomLevelDependentData))
			return false;
		
		ZoomLevelDependentData other = (ZoomLevelDependentData)o;
		
		if (this.maxZoomlevel != other.maxZoomlevel) {
			return false;
		}
		
		if (this.nativeZoomlevel != other.nativeZoomlevel) {
			return false;
		}
		
		if (!(this.streetData != null ? this.streetData.equals(other.streetData) : other.streetData == null))
			return false;
		
		if (!(this.terrainData != null ? this.terrainData.equals(other.terrainData) : other.terrainData == null))
			return false;
		
		return true;
	}
	
}
