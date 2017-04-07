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



public class RouteTile extends Tile {
	
	private boolean hasRoute;
	
	public RouteTile(Zoomlevel z, int indexX, int indexY, BufferedImage image)  {
		super(z, indexX, indexY, image);
		hasRoute = true;
	}
	
	public RouteTile(Zoomlevel z, int indexX, int indexY) {
		super(z, indexX, indexY, null);
		hasRoute = false;
	}
	
	public boolean hasRoute() {
		return hasRoute;
	}
	
	private static BufferedImage loadingImage = null;
	public static RouteTile getPreviewRouteTile(Zoomlevel zoomlevel, int indexX, int indexY) {
		if (loadingImage == null) {
			loadingImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
		}
		return new RouteTile(zoomlevel, indexX, indexY, loadingImage);
	}
}
