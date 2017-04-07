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
import java.util.EventObject;

import dataTypes.Zoomlevel;



public class TileRenderingEvent extends EventObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private MapTile tile = null;
	
	private Zoomlevel zoomlevel;
	private int indexX;
	private int indexY;

	public TileRenderingEvent(Object source, MapTile tile) {
		super(source);
		this.tile = tile;
		this.zoomlevel = tile.getZoomlevel();
		this.indexX = tile.getIndexX();
		this.indexY = tile.getIndexY();
	}
	
	public TileRenderingEvent(Object source, Zoomlevel zoomlevel, int indexX, int indexY) {
		super(source);
		this.zoomlevel = zoomlevel;
		this.indexX = indexX;
		this.indexY = indexY;
	}
	
	public MapTile getMapTiel() {
		return tile;
	}
	
	public Zoomlevel getZoomlevel() {
		return this.zoomlevel;
	}
	
	public int getIndexX() {
		return this.indexX;
	}
	
	public int getIndexY() {
		return this.indexY;
	}

}
