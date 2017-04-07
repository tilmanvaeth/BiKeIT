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

package controllers;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public interface MapViewController {

	public ArrayList<BufferedImage> getMapTiles();
	
	public ArrayList<BufferedImage> getRouteTiles();
	
	public boolean hasRoute();
	
	public int getOffsetX();
	
	public int getOffsetY();
	
	public int getNumTilesX();
	
	public int getNumTilesY();
	
	public int getTileSizeWidth();
	
	public int getTileSizeHeight();
	
	public Point getDraggingWPPosition();
	
	public boolean hasDraggingWP();
	
	public int getDraggingWPIndex();
}
