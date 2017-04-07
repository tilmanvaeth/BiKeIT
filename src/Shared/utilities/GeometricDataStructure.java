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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import mapModel.Element;
import dataTypes.TileCoord;


public class GeometricDataStructure<T extends Element> {

	private Map<TileCoord, ArrayList<T>> map;
	
	public GeometricDataStructure(Set<Entry<TileCoord, ArrayList<T>>> entrySet) {
		this.map = new HashMap<TileCoord, ArrayList<T>>();
		for (Entry<TileCoord, ArrayList<T>> entry : entrySet) {
			map.put(entry.getKey(), entry.getValue());
		}
	}
	
	
	public GeometricDataStructure() {
		this.map = new HashMap<TileCoord, ArrayList<T>>();
	}
	
	
	public void put(TileCoord key, T element) {
		if (!map.containsKey(key)) {
			map.put(key, new ArrayList<T>(10));
		}
		
		map.get(key).add(element);
	}
	
	
	public Set<T> getElements(int indexX, int indexY) {
		TileCoord key = new TileCoord(indexX, indexY);
		if (map.containsKey(key)) {
			return new HashSet<T>(map.get(key));
		}

		return Collections.emptySet();
		
	}
	
	
	public Set<T> getAllElements() {
		Set<T> result = new HashSet<T>();
		
		for (List<T> sublist : map.values()) {
			result.addAll(sublist);
		}
		
		return result;
	}
	
	
	public Set<Entry<TileCoord, ArrayList<T>>> getEntrySet() {
		return map.entrySet();
	}
	
	
	public void trim() {
		for (ArrayList<T> list : map.values()) {
			list.trimToSize();
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof GeometricDataStructure))
			return false;
		
		@SuppressWarnings("unchecked")
		GeometricDataStructure<T> other = (GeometricDataStructure<T>)o;
		
		return this.map.equals(other.map);
	}
	
}
