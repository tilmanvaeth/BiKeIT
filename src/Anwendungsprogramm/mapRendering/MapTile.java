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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import dataTypes.Zoomlevel;



public class MapTile extends Tile {
	
	public MapTile(Zoomlevel z, int indexX, int indexY, BufferedImage image)  {
		super(z, indexX, indexY, image);
	}
	
	private static BufferedImage loadingImage = null;
	public static MapTile getPreviewMapTile(Zoomlevel zoomlevel, int indexX, int indexY) {
		if (loadingImage == null) {
			loadingImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = (Graphics2D)loadingImage.getGraphics();
			g2d.setColor(Color.gray);
			g2d.fillRect(0, 0, 256, 256);
			g2d.setColor(Color.DARK_GRAY);
			g2d.drawRect(0, 0, 256, 256);
			for(int i = 0; i < 8; i++) {
				for(int j = 0; j < 3; j++) {
					g2d.drawString("Loading...",j*128, 5 + 64*i);
					g2d.drawString("Loading...", -64 + j * 128, 5 + 64*i+32);
				}
				
			}
		}
		return new MapTile(zoomlevel, indexX, indexY, loadingImage);
	}
}
