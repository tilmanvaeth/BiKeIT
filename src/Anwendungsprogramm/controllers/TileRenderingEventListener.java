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
import java.util.EventListener;

import mapRendering.MapTile;
import mapRendering.RouteTile;



public interface TileRenderingEventListener extends EventListener{
	
	public void finishedRenderingMapTile(MapTile tile);
	
	public void canceledRenderingMapTile(MapTile tile);
	
	public void finishedRenderingRouteTile(RouteTile tile);
	
	public void canceledRenderingRouteTile(RouteTile tile);
}
