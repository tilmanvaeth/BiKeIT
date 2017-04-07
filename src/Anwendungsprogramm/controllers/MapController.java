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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import mapModel.City;
import mapModel.MapModel;
import mapRendering.MapTile;
import mapRendering.MapTileCache;
import mapRendering.MapTileRenderer;
import mapRendering.RouteTile;
import mapRendering.RouteTileCache;
import mapRendering.RouteTileRenderer;
import runTimeData.CalculatedRoute;
import runTimeData.PlannedWaypoints;
import utilities.MercadorProjection;
import dataTypes.Coordinate;
import dataTypes.CoordinateRect;
import dataTypes.Pixel;
import dataTypes.Zoomlevel;



public class MapController implements MapViewController, TileRenderingEventListener{

	private static final int TILE_SIZE_WIDTH = 256;
	
	private static final int TILE_SIZE_HEIGHT = 256;

	private static final Coordinate KARLSRUHE = new Coordinate(49.0139f, 8.4044f);
	
	private static final Zoomlevel DEFAULT_ZOOM = new Zoomlevel(14);
	
	private static final Dimension DEFAULT_DIMENSION = new Dimension(0, 0);
	
	private static final int MAP_CACH_SIZE = 200;
	
	private static final int ROUTE_CACH_SIZE = 200;
	
	private static final int OFF_SCREEN_BUFFER_WIDTH = 3;
	
	private Zoomlevel zoomlevel;
	
	private Dimension mapDimension;
	
	private int numTilesX;
	
	private int numTilesY;
	
	private int offsetX;
	
	private int offsetY;
	
	private int leftIndexX;
	
	private int topIndexY;
	
	private MapTileCache mapCache;
	
	private RouteTileCache routeCache;
	
	private ArrayList<BufferedImage> mapTileBuffer;
	
	private ArrayList<BufferedImage> routeTileBuffer;
	
	private MapTileRenderer mapRenderer;
	
	private RouteTileRenderer routeRenderer;
	
	private MODE mode;
	
	private boolean showDraggingWP;
	
	private Point draggingWPPosition;
	
	private int draggingWPIndex;
	
	
	private enum MODE {
		ZOOMED_IN,
		ZOOMED_OUT,
		NEW_LOADED
	}
	
	
	public MapController(MapModel mapModel) {
		mode = MODE.NEW_LOADED;
		this.mapCache = new MapTileCache(MAP_CACH_SIZE);
		this.routeCache = new RouteTileCache(ROUTE_CACH_SIZE);
		this.mapRenderer = new MapTileRenderer(Runtime.getRuntime().availableProcessors(), mapModel);
		this.routeRenderer = new RouteTileRenderer(Runtime.getRuntime().availableProcessors());
		
		this.mapRenderer.addMapTileRenderingEventListener(this);
		this.routeRenderer.addMapTileRenderingEventListener(this);
		
		this.mapTileBuffer = new ArrayList<BufferedImage>(42);
		this.routeTileBuffer = new ArrayList<BufferedImage>(42);
		
		this.mapDimension = DEFAULT_DIMENSION;
		this.zoomlevel = DEFAULT_ZOOM;

		if (mapModel.getCityData().getAllCities().contains(new City("Karlsruhe", null))) {
			this.centerMapAt(KARLSRUHE);
		} else {
			this.centerMapAt(mapModel.getCityData().getAllCities().get(0).getPosition());
		}
	}
	
	public void setNogger() {
		try {
			BufferedImage marker = ImageIO.read(this.getClass().getClassLoader().getResource("nogger.png"));
			routeRenderer.setMarkerImage(marker);
			routeRenderer.emptyWorkingQueue();
			routeCache.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void moveMap(int deltaX, int deltaY) {
		this.updatePosition(this.offsetX + deltaX, this.offsetY + deltaY);
	}
	
	
	
	public void zoomIn(Point p) {
		this.zoomIn(this.getCoordinateOfPointOnScreen(p));
	}
	
	public void zoomIn() {
		this.zoomIn(this.getCoordinateOfCenter());
	}
	
	public void zoomOut() {
		mode = MODE.ZOOMED_OUT;
		mapRenderer.emptyWorkingQueue();
		//routeRenderer.emptyWorkingQueue();
		//routeCache.clear();
		
		Coordinate c = this.getCoordinateOfCenter();
		this.zoomlevel.decrease();
		this.setCenterToCoordinate(c);
	}
	
	public void setZoomlevel(Zoomlevel z) {
		mapRenderer.emptyWorkingQueue();
		routeRenderer.emptyWorkingQueue();
		routeCache.clear();
		
		Coordinate c = this.getCoordinateOfCenter();
		this.zoomlevel = z;
		this.setCenterToCoordinate(c);
	}
	
	public Zoomlevel getZoomlevel() {
		return this.zoomlevel;
	}
	
	
	public void centerMapAt(Coordinate center) {
		this.setCenterToCoordinate(center);
	}
	
	public void updateDimensions(Dimension d) {
		this.setMapDimension(d);
	}
	

	public Coordinate getCoordinateOfPointOnScreen(Point p) {
		long x = p.x -this.offsetX + leftIndexX * TILE_SIZE_WIDTH;
		long y = p.y -this.offsetY + topIndexY * TILE_SIZE_HEIGHT;
		
		return MercadorProjection.pixelToCoordinate(new Pixel(x, y), zoomlevel.getValue());
	}
	
	public Point getPointOfCoordinateOnScreen(Coordinate c) {
		long x = (long) MercadorProjection.longitudeToPixelX(c.getLongitude(), zoomlevel.getValue());
		long y = (long) MercadorProjection.latitudeToPixelY(c.getLatitude(), zoomlevel.getValue());
		
		x = x +this.offsetX - leftIndexX * TILE_SIZE_WIDTH;
		y = y +this.offsetY - topIndexY * TILE_SIZE_HEIGHT;
		
		return new Point((int)x, (int)y);
	}
	
	
	public void updateWaypoints(PlannedWaypoints waypoints) {
		this.routeRenderer.emptyWorkingQueue();
		this.routeRenderer.setWaypoints(waypoints);
		this.routeRenderer.setCalculatedRoute(null);
		this.routeCache.clear();
	}
	
	
	public void updateCalculatedRoute(CalculatedRoute route) {
		this.routeRenderer.emptyWorkingQueue();
		this.routeRenderer.setCalculatedRoute(route);
		this.routeCache.clear();
	}
	
	public boolean hasFinishedRendering() {
		return routeRenderer.isIdle() && mapRenderer.isIdle();
	}
	
	
	/*
	 * Interface MapViewController methods
	 */
	@Override
	public ArrayList<BufferedImage> getMapTiles() {
		int rightIndexX = leftIndexX + numTilesX;
		int botIndexY = topIndexY + numTilesY;
		Zoomlevel currentZoom = new Zoomlevel(this.zoomlevel.getValue());

		mapTileBuffer.clear();
		for (int y = topIndexY; y < botIndexY; y++) {
			for (int x = leftIndexX; x < rightIndexX; x++) {
				mapTileBuffer.add(this.getMapTileImage(currentZoom, x, y));
			}
		}
		
		return mapTileBuffer;
	}
	
	@Override
	public ArrayList<BufferedImage> getRouteTiles() {
		int rightIndexX = leftIndexX + numTilesX;
		int botIndexY = topIndexY + numTilesY;
		Zoomlevel currentZoom = new Zoomlevel(this.zoomlevel.getValue());

		routeTileBuffer.clear();
		for (int y = topIndexY; y < botIndexY; y++) {
			for (int x = leftIndexX; x < rightIndexX; x++) {
				routeTileBuffer.add(this.getRouteTileImage(currentZoom, x, y));
			}
		}
		
		return routeTileBuffer;
	}
	

	@Override
	public boolean hasRoute() {
		return routeRenderer.hasRoute();
	}

	@Override
	public int getOffsetX() {
		return offsetX;
	}

	@Override
	public int getOffsetY() {
		return offsetY;
	}

	@Override
	public int getNumTilesX() {
		return numTilesX;
	}

	@Override
	public int getNumTilesY() {
		return numTilesY;
	}

	@Override
	public int getTileSizeWidth() {
		return TILE_SIZE_WIDTH;
	}

	@Override
	public int getTileSizeHeight() {
		return TILE_SIZE_HEIGHT;
	}
	
	
	
	private void setMapDimension(Dimension d) {
		Coordinate c = this.getCoordinateOfCenter();
		this.mapDimension = d;		
		numTilesX = (int)Math.ceil((double)d.getWidth()/(double)TILE_SIZE_WIDTH) + 1;
		numTilesY = (int)Math.ceil((double)d.getHeight()/(double)TILE_SIZE_HEIGHT) + 1;
		this.setCenterToCoordinate(c);
	}
	
	
	private BufferedImage getMapTileImage(Zoomlevel zoomlevel, int indexX, int indexY) {
		indexX = (int)this.getCyclicIndexX(indexX);
		if (indexY < 0 || indexY >= (1 << zoomlevel.getValue())) {
			return null;
		}
		
		if (mapCache.containsTile(zoomlevel, indexX, indexY)) {
			return mapCache.getTile(zoomlevel, indexX, indexY).getImage();
		} else {
			MapTile tile = this.getPreviewMapTile(zoomlevel, indexX, indexY);
			mapCache.putTile(tile);
			mapRenderer.startRenderingTile(zoomlevel, indexX, indexY);
			return tile.getImage();
		}
	}
	
	private static int[] indicesX = new int[2];
	private static int[] indicesY = new int[2];
	
	private MapTile getPreviewMapTile(Zoomlevel zoomlevel, int indexX, int indexY) {
		
		switch (mode) {
		case ZOOMED_IN:
			int x = indexX/2;
			int y = indexY/2;
			Zoomlevel z = new Zoomlevel(zoomlevel.getValue()-1);
			
			if (mapCache.containsTile(z, x, y)) {
				int ofX = -(indexX % 2) * 256 ;
				int ofY = -(indexY % 2) * 256;
				BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = img.createGraphics();
				
				BufferedImage image = mapCache.getTile(z, x, y).getImage();
				g.drawImage(image, ofX, ofY, 256*2, 256*2, null);
				g.dispose();
				
				return new MapTile(zoomlevel, indexX, indexY, img);
			}
			break;
		case ZOOMED_OUT:
			indicesX[0] = indexX*2;
			indicesX[1] = indexX*2+1;
			indicesY[0] = indexY*2;
			indicesY[1] = indexY*2+1;
			
			z = new Zoomlevel(zoomlevel.getValue()+1);
			
			BufferedImage[][] images = new BufferedImage[2][2];
			for (int iX = 0; iX < 2; iX++) {
				for (int iY = 0; iY < 2; iY++) {
					if (!mapCache.containsTile(z, indicesX[iX], indicesY[iY])) {
						images[iX][iY] = MapTile.getPreviewMapTile(zoomlevel, indexX, indexY).getImage();
					} else {
						images[iX][iY] = mapCache.getTile(z, indicesX[iX], indicesY[iY]).getImage();
					}
				}
			}
			
			BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			
			for (int iX = 0; iX < 2; iX++) {
				for (int iY = 0; iY < 2; iY++) {
					g.drawImage(images[iX][iY], iX * 128, iY * 128, 128, 128, null);
				}
			}
			g.dispose();
			return new MapTile(zoomlevel, indexX, indexY, image);
		case NEW_LOADED:
			return MapTile.getPreviewMapTile(zoomlevel, indexX, indexY);
		}
		return MapTile.getPreviewMapTile(zoomlevel, indexX, indexY);
	}
	
	private BufferedImage getRouteTileImage(Zoomlevel zoomlevel, int indexX, int indexY) {
		indexX = (int)this.getCyclicIndexX(indexX);
		if (indexY < 0 || indexY >= (1 << zoomlevel.getValue())) {
			return null;
		}
		
		if (routeCache.containsTile(zoomlevel, indexX, indexY)) {
			return routeCache.getTile(zoomlevel, indexX, indexY).getImage();
		} else {
			RouteTile tile = RouteTile.getPreviewRouteTile(zoomlevel, indexX, indexY);
			routeCache.putTile(tile);
			routeRenderer.startRenderingTile(zoomlevel, indexX, indexY);
			return tile.getImage();
		}
	}
	
	
	private void zoomIn(Coordinate c) {
		mode = MODE.ZOOMED_IN;
		mapRenderer.emptyWorkingQueue();
		//routeRenderer.emptyWorkingQueue();
		//routeCache.clear();
		Point p = this.getPointOfCoordinateOnScreen(c);
		this.zoomlevel.increase();
		this.setPointToCoordinate(c, p);
		//this.setCenterToCoordinate(c);
	}
	
	private void setPointToCoordinate(Coordinate c, Point p) {
		double lon = c.getLongitude();
		double lat = c.getLatitude();
		
		double x = MercadorProjection.longitudeToPixelX(lon, zoomlevel.getValue());
		double y = MercadorProjection.latitudeToPixelY(lat, zoomlevel.getValue());
		
		x -= (double)p.x;
		y -= (double)p.y;
		
		double xTile = Math.floor((x / (double)TILE_SIZE_WIDTH));
		double yTile = Math.floor((y / (double)TILE_SIZE_HEIGHT));
		
		double xOffset = -Math.floor(x - (xTile * TILE_SIZE_WIDTH));
		double yOffset = -Math.floor(y - (yTile * TILE_SIZE_HEIGHT));
		
		this.leftIndexX = (int)xTile;
		this.topIndexY = (int)yTile;
		
		this.offsetX = (int)xOffset;
		this.offsetY = (int)yOffset;
		
		if (mode != MODE.NEW_LOADED && mapRenderer.isIdle())
			this.bufferMapTiles();
	}
	
	private void setCenterToCoordinate(Coordinate c) {
		Point p = new Point(mapDimension.width/2, mapDimension.height/2);
		this.setPointToCoordinate(c, p);
	}
	
	
	private long getCyclicIndexX(long indexX) {
		long n = (1<<zoomlevel.getValue());
		
		long cIndexX = indexX % n;
		return cIndexX < 0 ? cIndexX + n : cIndexX;
	}
	
	
	public Coordinate getCoordinateOfCenter() {
		return this.getCoordinateOfPointOnScreen(this.getCenterOfMapAsPoint());
	}
	
	
	private Point getCenterOfMapAsPoint() {
		return new Point(mapDimension.width/2, mapDimension.height/2);
	}
	
	private void updatePosition(int newOffsetX, int newOffsetY) {
		this.mapRenderer.emptyWorkingQueue();
		
		if(newOffsetX > 0) {
			this.offsetX = -TILE_SIZE_WIDTH + newOffsetX % TILE_SIZE_WIDTH;
			this.leftIndexX -= newOffsetX / TILE_SIZE_WIDTH + 1;
		} 
		else if (newOffsetX < -TILE_SIZE_WIDTH) {
			this.offsetX = newOffsetX % TILE_SIZE_WIDTH;
			this.leftIndexX -= newOffsetX / TILE_SIZE_WIDTH;
		}
		else {
			this.offsetX = newOffsetX;
		}
		
		
		if(newOffsetY > 0) {
			this.offsetY = -TILE_SIZE_HEIGHT + newOffsetY % TILE_SIZE_HEIGHT;
			this.topIndexY -= newOffsetY / TILE_SIZE_HEIGHT + 1;
		} 
		else if (newOffsetY < -TILE_SIZE_HEIGHT) {
			this.offsetY = newOffsetY % TILE_SIZE_HEIGHT;
			this.topIndexY -= newOffsetY / TILE_SIZE_HEIGHT;
		}
		else {
			this.offsetY = newOffsetY;
		}
		
		if (mapRenderer.isIdle())
			this.bufferMapTiles();
	}

	private void bufferMapTiles() {
		final Zoomlevel zoomlevel = new Zoomlevel(this.zoomlevel.getValue());
		
		//visible area
		for (int x = this.leftIndexX; x < this.leftIndexX + this.numTilesX; x ++) {
			for (int y = this.topIndexY; y < this.topIndexY + this.numTilesY; y++) {
				this.getMapTileImage(zoomlevel, x, y);
			}
		}
		
		for (int offset = 1; offset < OFF_SCREEN_BUFFER_WIDTH; offset++) {
			//left border
			for (int y = this.topIndexY - offset; y <= this.topIndexY + this.numTilesY + offset - 1; y++) {
				this.getMapTileImage(zoomlevel, this.leftIndexX - offset, y);
			}
			
		
			//right border
			for (int y = this.topIndexY - offset; y <= this.topIndexY + this.numTilesY + offset - 1; y++) {
				this.getMapTileImage(zoomlevel, this.leftIndexX + this.numTilesX + offset - 1, y);
			}
		
		
			//top border
			for (int x = this.leftIndexX; x <= this.leftIndexX + this.numTilesX - 1; x++) {
				this.getMapTileImage(zoomlevel, x, this.topIndexY - offset);
			}
		
			//bottom border
			for (int x = this.leftIndexX; x <= this.leftIndexX + this.numTilesX - 1; x++) {
				this.getMapTileImage(zoomlevel, x, this.topIndexY + this.numTilesY + offset - 1);
			}
		}
	}

	@Override
	public void finishedRenderingMapTile(MapTile tile) {
		mapCache.putTile(tile);
	}
	
	@Override
	public void canceledRenderingMapTile(MapTile tile) {
		mapCache.removeTile(tile.getZoomlevel(), tile.getIndexX(), tile.getIndexY());
	}


	@Override
	public void finishedRenderingRouteTile(RouteTile tile) {
		routeCache.putTile(tile);
	}


	@Override
	public void canceledRenderingRouteTile(RouteTile tile) {
		routeCache.removeTile(tile.getZoomlevel(), tile.getIndexX(), tile.getIndexY());
	}
	
	public void addMapTileRenderingEventListener(TileRenderingEventListener listener) {
		mapRenderer.addMapTileRenderingEventListener(listener);
		routeRenderer.addMapTileRenderingEventListener(listener);
	}
	
	public void removeMapTileRenderingEventListener(TileRenderingEventListener listener) {
		mapRenderer.removeMapTileRenderingEventListener(listener);
		routeRenderer.removeMapTileRenderingEventListener(listener);
	}

	/*
	 * Sets position and zoomlevel in a way that
	 * the whole coordinate rect is visible.
	 */
	public void zoomToCoordinateRect(CoordinateRect c, int mapWidth, int mapHeight) {
		
		centerMapAt(c.getCenter());
		
		double x, x1, x2;
		double y, y1, y2;
		
		int currentZoom = Zoomlevel.ZOOMLEVEL_MIN;
		int zoomlvl = currentZoom;
		
		x1 = MercadorProjection.longitudeToPixelX(c.getUL().getLongitude(), currentZoom);
		y1 = MercadorProjection.latitudeToPixelY(c.getUL().getLatitude(), currentZoom);
		
		x2 = MercadorProjection.longitudeToPixelX(c.getLR().getLongitude(), currentZoom);
		y2 = MercadorProjection.latitudeToPixelY(c.getLR().getLatitude(), currentZoom);
		
		x = x2 - x1;
		y = y2 - y1;
				
		double factor;
		
		/* map coords to max zoomlevel*/
		factor = 1<<(Zoomlevel.ZOOMLEVEL_MAX - currentZoom);
		
		x *= factor;
		y *= factor;		
		
		for(int i = Zoomlevel.ZOOMLEVEL_MAX; i > Zoomlevel.ZOOMLEVEL_MIN; i--) {
			/* frame size of mapView = (500, 300) due too rendering and 
			 * marker icons the limits are lower -> (480, 220) */
			if(x < mapWidth-20 && y < mapHeight-80) {
				 //the zoomlevel for the frame
				zoomlvl = i;
				i = 0;
			}
			else {
				y /= 2;
				x /= 2;
			}
		}
		setZoomlevel(new Zoomlevel(zoomlvl));
		centerMapAt(c.getCenter());	
	}
	
	public Dimension getMapDimension() {
		return mapDimension;
	}

	
	public void showDraggingWP(int index, boolean show) {
		this.draggingWPIndex = index;
		this.showDraggingWP = show;
	}
	
	public void updateDraggingWPPosition(Point p) {
		this.draggingWPPosition = p;
	}

	@Override
	public Point getDraggingWPPosition() {
		return this.draggingWPPosition;
	}


	@Override
	public boolean hasDraggingWP() {
		return this.showDraggingWP;
	}


	@Override
	public int getDraggingWPIndex() {
		return this.draggingWPIndex;
	}	
}
