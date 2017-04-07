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

package mapRendering;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import dataTypes.Zoomlevel;


public abstract class TileCache {

	private class TileKey {
		int x,y,z;
		public TileKey(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof TileKey)) {
				return false;
			}
			
			TileKey other = (TileKey)o;
			return (this.x == other.x 
					&& this.y == other.y
					&& this.z == other.z);
		}
		
		@Override
		public int hashCode() {
			return ((7+y*31)-x*5)+3*z;
		}
	}
	
	
	private static final float hashTableLoadFactor = 0.75f;

	protected LinkedHashMap<TileKey, Tile> map;
	private int cacheSize;

	/**
	 * Creates a new LRU Tile cache.
	 * @param cacheSize the maximum number of entries that will be kept in this cache.
	 */
	public TileCache (int cacheSize) {
		this.cacheSize = cacheSize;
		int hashTableCapacity = (int)Math.ceil(cacheSize / hashTableLoadFactor) + 1;
		map = new LinkedHashMap<TileKey, Tile>(hashTableCapacity, hashTableLoadFactor, true) {
			// (an anonymous inner class)
			private static final long serialVersionUID = 1;
			@Override protected boolean removeEldestEntry (Map.Entry<TileKey, Tile> eldest) {
				return size() > TileCache.this.cacheSize; 
			}
			
			
		}; 
	}
	

	/**
	 * Retrieves a Tile from the cache.
	 */
	protected synchronized Tile getTile(Zoomlevel z, int x, int y) {
		return map.get(new TileKey(x,y,z.getValue())); 
	}
	
	/**
	 * Checks whether the cache contains the Tile
	 */
	public synchronized boolean containsTile(Zoomlevel z, int x, int y) {
		return map.containsKey(new TileKey(x,y,z.getValue()));
	}

	/**
	 * Adds a Tile to this cache.
	 * The new entry becomes the MRU (most recently used) entry.
	 */
	protected synchronized void putTile(Tile tile) {
		TileKey key = new TileKey(tile.getIndexX(), tile.getIndexY(), tile.getZoomlevel().getValue());
		map.put (key, tile);
	}
	
	/**
	 * Removes a Tile form this cache.
	 */
	public synchronized void removeTile(Zoomlevel z, int x, int y) {
		map.remove(new TileKey(x,y,z.getValue()));
	}

	/**
	 * Clears the cache.
	 */
	public synchronized void clear() {
		map.clear(); 
	}

	/**
	 * Returns the number of used entries in the cache.
	 * @return the number of entries currently in the cache.
	 */
	public synchronized int usedEntries() {
		return map.size(); 
	}
	
	
	/**
	 * Returns the maximal number of used entries in the cache.
	 * @return
	 */
	public synchronized int getMaximumSize() {
		return this.cacheSize;
	}
	

	/**
	 * Returns a <code>Collection</code> that contains a copy of all cache entries.
	 * @return a <code>Collection</code> with a copy of the cache content.
	 */
	public synchronized Collection<Map.Entry<TileKey, Tile>> getAll() {
		return new ArrayList<Map.Entry<TileKey, Tile>>(map.entrySet()); 
	}	
}