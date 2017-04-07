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

import java.awt.Color;

import mapModel.Area;
import mapModel.City;
import mapModel.Way;




public interface RenderStyle {
	
	public Color getBackgroundColor();
	
	public boolean isVisible(Way way, int zoomlevel);
	
	public Color getLineColor(Way way);
	
	public Color getBorderColor(Way way);
	
	public boolean isLineDashed(Way way);
	
	public boolean isBorderDashed(Way way);
	
	public float[] getLineDashLenths(Way way, int zoomlevel);
	
	public float[] getBorderDashLenths(Way way, int zoomlevel);
	
	public boolean hasBorder(Way way);
	
	public float getLineWidth(Way way, int zoomlevel);
	
	public float getBorderWidth(Way way, int zoomlevel);
	
	public float getTotalBorderWidth(Way way, int zoomlevel);
	
	public int getZIndex(Way way);
	
	public int getZIndex(Area way);
	
	public boolean isVisible(Area area, int zoomlevel);
	
	public float getBorderWidth(Area area);
	
	public Color getBorderColor(Area area);
	
	public boolean hasBorder(Area area);
	
	public Color getFillColor(Area area);
	
	public boolean isTransparant(Way way);
	
	public boolean isVisible(City city, int zoomlevel);
	
	public String getFontname(City city);
	
	public int getFontsize(City city, int zoomlevel);
	
	public int getFontstyle(City city);
	
	public Color getFontcolor(City city);
	
	public int getMaxWayZIndex();
	
	public int getMaxAreaZIndex();
}
