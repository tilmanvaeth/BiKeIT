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


import java.util.LinkedList;
import java.util.List;

import mapModel.Area;
import mapModel.City;
import mapModel.Element;
import mapModel.Node;
import mapModel.Way;
import dataTypes.Coordinate;
import dataTypes.CoordinateRect;
import dataTypes.Pixel;
import dataTypes.TileCoord;


public class MercadorProjection {
	
	public static Coordinate pixelToCoordinate(Pixel pixel, int zoomlevel) {
		return new Coordinate((float)MercadorProjection.pixelYToLatitude((double)pixel.getY(), zoomlevel),
				(float)MercadorProjection.pixelXToLongitude((double)pixel.getX(), zoomlevel));
	}
	
	public static double pixelXToLongitude(double x, int zoomlevel) {
		double n = 1<<(zoomlevel+8);
		
		x = x % n;
		x = x < 0 ? x + n : x;
		
		double lon = ((x / n) * 360.0) - 180.0;
		return lon;
	}
	
	public static double pixelYToLatitude(double y, int zoomlevel) {
		double n = 1<<(zoomlevel+8);
		
		double lat = Math.toDegrees(Math.atan(Math.sinh(Math.PI - (2.0 * Math.PI * y) / n)));
		return lat;
	}
	
	public static double latitudeToPixelY(double lat, int zoomlevel) {
		double n = 1 << (zoomlevel+8);
		
		double y = (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1.0 / Math.cos(Math.toRadians(lat))) / Math.PI)  / 2.0 * n;
		return y;
	}
	
	public static double longitudeToPixelX(double lon, int zoomlevel) {
		double n = 1 << (zoomlevel+8);
		
		double x = ((lon + 180.0) / 360.0) * n;
		return x;
	}
	
	public static int[] getIndexXY(Coordinate c, int zoom) {
		float lon = c.getLongitude();
		float lat = c.getLatitude();
			   
		int xtile = (int)Math.floor( (lon + 180) / 360 * (1<<zoom) ) ;
		int ytile = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<zoom) ) ;
		
		return new int[]{xtile, ytile};
	}
	
	public static TileCoord getTileCoord(Coordinate c, int zoom) {
		float lon = c.getLongitude();
		float lat = c.getLatitude();
			   
		int xtile = (int)Math.floor( (lon + 180) / 360 * (1<<zoom) ) ;
		int ytile = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<zoom) ) ;
		
		return new TileCoord(xtile, ytile);
	}
	
	public static CoordinateRect getFrame(int indexX, int indexY, int zoom) {
		return new CoordinateRect(getULCoordinate(indexX, indexY, zoom),
				getULCoordinate(indexX+1,indexY+1, zoom));
	}
	
	public static Coordinate getULCoordinate(int indexX, int indexY, int zoom) {
		double n = 1<<(zoom);
		
		double lon = ((indexX / n) * 360.0) - 180.0;
		double lat = Math.toDegrees(Math.atan(Math.sinh(Math.PI - (2.0 * Math.PI * indexY) / n)));
		return new Coordinate((float)lat, (float)lon);
	}
	
	public static List<TileCoord> getAffectedTilesForZoom(Element element, int zoom) {
		final List<TileCoord> result = new LinkedList<TileCoord>();
		
		if (element instanceof Node) {
			
			Node n = (Node) element;
			
			result.add(MercadorProjection.getTileCoord(n.getPosition(), zoom));
			
		} else if (element instanceof City) {
			
			City c = (City) element;
			
			result.add(MercadorProjection.getTileCoord(c.getPosition(), zoom));
			
		} else if (element instanceof Area) {
			
			Area a = (Area) element;
			
			int minX = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxY = Integer.MIN_VALUE;
			
			for (Node n : a.getNodes()) {
				int[] pos = getIndexXY(n.getPosition(), zoom);
				
				if (pos[0] < minX)
					minX = pos[0];
				if (pos[0] > maxX)
					maxX = pos[0];
				if (pos[1] < minY)
					minY = pos[1];
				if (pos[1] > maxY)
					maxY = pos[1];
				
			}
			for (int x = minX - 1; x <= maxX; x++) {
				for (int y = minY - 1; y <= maxY; y++) {
					//TODO implement this method
					//if (a.isIntersect(MercadorProjection.getFrame(x, y, zoom)))
						result.add(new TileCoord(x,y));
				}
			}
			
		} else if (element instanceof Way) {
			Way w = (Way) element;
			int minX = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxY = Integer.MIN_VALUE;
			
			for (Node n : w.getNodes()) {
				int[] pos = getIndexXY(n.getPosition(), zoom);
				
				if (pos[0] < minX)
					minX = pos[0];
				if (pos[0] > maxX)
					maxX = pos[0];
				if (pos[1] < minY)
					minY = pos[1];
				if (pos[1] > maxY)
					maxY = pos[1];
				
			}
			for (int x = minX; x <= maxX; x++) {
				for (int y = minY; y <= maxY; y++) {
					if (w.isIntersect(MercadorProjection.getFrame(x, y, zoom))) 
						result.add(new TileCoord(x,y));
				}
			}
			
		}
		
		return result;
	}
	
}
