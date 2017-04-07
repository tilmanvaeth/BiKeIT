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
import java.awt.image.BufferedImage;

import dataTypes.Zoomlevel;



public abstract class Tile {

	private Zoomlevel zoomlevel;
	
	private int indexX;
	
	private int indexY;
	
	private BufferedImage image;
	
	
	public Tile(Zoomlevel z, int indexX, int indexY, BufferedImage image)  {
		this.zoomlevel = z;
		this.indexX = indexX;
		this.indexY = indexY;
		this.image = image;
	}
	
	public Zoomlevel getZoomlevel() {
		return zoomlevel;
	}
	
	public int getIndexX() {
		return indexX;
	}
	
	public int getIndexY() {
		return indexY;
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	@Override
	public int hashCode() {
		return (12341+indexX*11)-7*indexY;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tile))
			return false;
		
		Tile other = (Tile) o;
		
		return (this.indexX == other.indexX
				&& this.indexY == other.indexY
				&& this.zoomlevel.getValue() == other.zoomlevel.getValue());
	}
	
	@Override
	public String toString() {
		return Tile.getStringRepresentation(zoomlevel, indexX, indexY);
	}
	
	public static String getStringRepresentation(Zoomlevel zoomlevel, int indexX, int indexY) {
		return "" + zoomlevel.getValue()
				+ "/" + indexX 
				+ "/" + indexY;
	}
}
