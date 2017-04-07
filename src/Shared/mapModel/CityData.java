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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import utilities.GeometricDataStructure;
import utilities.MercadorProjection;
import dataTypes.TileCoord;


public class CityData {

	private Map<String, City> map;
	private GeometricDataStructure<City> cityStruc;
	private static final int ZOOMLEVEL = dataTypes.Zoomlevel.ZOOMLEVEL_MAX-7;
	
	private static final int V_OFFSET = 1;
	private static final int H_OFFSET = 1;
	
	public CityData(GeometricDataStructure<City> cityStruc) {
		this.map = new HashMap<String, City>();
		this.cityStruc = cityStruc;
		
		for (City city : cityStruc.getAllElements()) {
			map.put(city.getName().toLowerCase(), city);
		}
	}
	
	
	public CityData() {
		this.map = new HashMap<String, City>();
		this.cityStruc = new GeometricDataStructure<City>();
	}
	
	
	public void addCity(City city) {
		final List<TileCoord> keys = MercadorProjection.getAffectedTilesForZoom(city, ZOOMLEVEL);
		
		for (TileCoord key : keys) {
			cityStruc.put(key, city);
		}
		map.put(city.getName().toLowerCase(), city);
	}
	
	
	public boolean hasCity(String s) {
		return map.containsKey(s.toLowerCase());
	}

	
	public List<City> getAllCities() {
		return new LinkedList<City>(map.values());
	}
	
	
	public City getCity(String name) {
		return map.get(name.toLowerCase());
	}
	
	
	public Set<City> getCities(int indexX, int indexY, int zoomlevel) {
		
		Set<City> result = new HashSet<City>();
		
		
				
		if (zoomlevel == ZOOMLEVEL) {
			for (int x = indexX - H_OFFSET; x <= indexX + H_OFFSET; x++) {
				for (int y = indexY - V_OFFSET; y <= indexY + V_OFFSET; y++) {
					if (x >= 0 && y >= 0) {
						result.addAll(cityStruc.getElements(x, y));
					}
				}
			}
		} 
		else if (zoomlevel < ZOOMLEVEL) {
			int factor = (1<<ZOOMLEVEL - zoomlevel);
		
			for (int x = indexX - H_OFFSET; x <= indexX + H_OFFSET; x++) {
				for (int y = indexY - V_OFFSET; y <= indexY + V_OFFSET; y++) {
					int xmin = x * factor;
					int ymin = y * factor;
					int xmax = xmin + factor;
					int ymax = ymin + factor;
						
					for (int ix = xmin; ix < xmax; ix++) {
						for(int iy = ymin; iy < ymax; iy++) {
							result.addAll(cityStruc.getElements(ix, iy));
						}
					}
				}
			}
		}
		else {
			int divident = (1 << (zoomlevel - ZOOMLEVEL));
			
			for (int x = indexX - H_OFFSET; x <= indexX + H_OFFSET; x++) {
				for (int y = indexY - V_OFFSET; y <= indexY + V_OFFSET; y++) {
					result.addAll(cityStruc.getElements(x / divident, y / divident));
				}
			}
		}
		
		return result;
	}
	
	
	public Set<Entry<TileCoord, ArrayList<City>>> getAllEntries() {
		return cityStruc.getEntrySet();
	}	
	
	public void trim() {
		cityStruc.trim();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CityData))
			return false;
		
		return this.cityStruc.equals(((CityData)o).cityStruc);
	}
}
