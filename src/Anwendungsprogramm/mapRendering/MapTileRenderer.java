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
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mapModel.Area;
import mapModel.City;
import mapModel.MapModel;
import mapModel.Node;
import mapModel.Way;
import mapModel.ZoomLevelDependentData;
import utilities.DefaultRenderStyle;
import utilities.MercadorProjection;
import utilities.RenderStyle;
import utilities.TunnelStroke;
import controllers.TileRenderingEventListener;
import dataTypes.Coordinate;
import dataTypes.Zoomlevel;



public class MapTileRenderer extends TileRenderer{
	
	private MapModel mapModel;
	private static RenderStyle renderStyle;
	
	public MapTileRenderer(int numThreads, MapModel mapModel) {
		super(numThreads);
		this.mapModel = mapModel;
		
		if (renderStyle == null) {
			renderStyle = new DefaultRenderStyle();
		}
	}
	

	private void fireFinishedRenderingEvent(MapTile tile) {
        Object[] listeners = listenerList.getListenerList();
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i] == TileRenderingEventListener.class) {
                ((TileRenderingEventListener)listeners[i+1]).finishedRenderingMapTile(tile);
            }
        }
    }
	
	private void fireCanceledRenderingEvent(Zoomlevel zoomlevel, int indexX, int indexY) {
		Object[] listeners = listenerList.getListenerList();
		MapTile tile = new MapTile(zoomlevel, indexX, indexY, null);
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i] == TileRenderingEventListener.class) {
                ((TileRenderingEventListener)listeners[i+1]).canceledRenderingMapTile(tile);
            }
        }
	}

	@Override
	protected TileRenderingJob createTileRenderingJob(int zoomlevel, int indexX, int indexY) {
		return new MapTileRenderingJob(zoomlevel, indexX, indexY);
	}
	
	
	private class MapTileRenderingJob extends TileRenderingJob {
		private ZoomLevelDependentData data;

		private int[] point = new int[2];
		
		public MapTileRenderingJob(int zoomlevel, int indexX, int indexY) {
			super(zoomlevel, indexX, indexY);
			this.data = mapModel.getMapDataForZoomlevel(new Zoomlevel(this.zoomlevel));
		}

		@Override
		public void run() {
			final BufferedImage image = this.createMapImage();
			final MapTile tile = new MapTile(new Zoomlevel(zoomlevel), indexX, indexY, image);
			fireFinishedRenderingEvent(tile);
		}
		
		
		private BufferedImage createMapImage() {
			final BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = image.createGraphics();
			
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			g2d.setBackground(renderStyle.getBackgroundColor());
			g2d.clearRect(0, 0, 256, 256);
			
			//this.drawBackground(g2d);
			drawAreas(g2d);
			drawWays(g2d);
			//drawTile(g2d);
			drawCityNames(g2d);
			
			
			
			g2d.dispose();
			return image;
		}
		
		
		private void drawCityNames(Graphics2D g2d) {
			for (City c : mapModel.getCityData().getCities(indexX, indexY, zoomlevel)) {
				if (renderStyle.isVisible(c, zoomlevel)) {
					g2d.setColor(renderStyle.getFontcolor(c));
					Font f = new Font(renderStyle.getFontname(c), 
						renderStyle.getFontstyle(c), 
						renderStyle.getFontsize(c, zoomlevel));
					g2d.setFont(f);
					this.drawString(c.getName(), c.getPosition(), g2d);
				}
			}
		}
		
		private void drawString(String text, Coordinate c, Graphics2D g2d) {
			coordToInt(c, point);
			
			FontMetrics fm = g2d.getFontMetrics();
			
			int x = point[0] - fm.stringWidth(text)/2;
			int y = point[1] + fm.getHeight()/2;
			
			g2d.drawString(text, x, y);
		}
			
		
		private Set<Area> getAreasToDraw() {
			return data.getAreas(indexX, indexY, this.zoomlevel);
		}
		
		
		private List<Way>[] divideWaysInLayers(final Map<Way, Polygon> ways) {
			 @SuppressWarnings("unchecked")
			final ArrayList<Way>[] layers = new ArrayList[renderStyle.getMaxWayZIndex() + 1];
			 for (int i = 0; i < renderStyle.getMaxWayZIndex() + 1; i++) {
			   	layers[i] = new ArrayList<Way>();
		    }
			 
			 for (Way w : ways.keySet()) {
			   	int zindex = renderStyle.getZIndex(w);
		   		layers[zindex].add(w);
		    }
			return layers;
		}
		
		private List<Area>[] divideAreasInLayers(final Set<Area> areas) {
			@SuppressWarnings("unchecked")
			final ArrayList<Area>[] layers = new ArrayList[renderStyle.getMaxAreaZIndex()+1];
			 for (int i = 0; i < renderStyle.getMaxAreaZIndex()+1; i++) {
			   	layers[i] = new ArrayList<Area>();
		    }
			 
			 for (Area a : areas) {
			   	int zindex = renderStyle.getZIndex(a);
		   		layers[zindex].add(a);
		    }
			return layers;
		}
		
		
		private void drawWayBorder(Way w, Polygon p, Graphics2D g2d) {
			if (renderStyle.isVisible(w, zoomlevel)) {
				if (renderStyle.hasBorder(w)) {
					float strokeWidth = renderStyle.getTotalBorderWidth(w, zoomlevel);
					if (renderStyle.isBorderDashed(w)) {
						g2d.setStroke(new TunnelStroke(strokeWidth, 
								renderStyle.getBorderWidth(w, zoomlevel),
								renderStyle.getBorderDashLenths(w, zoomlevel)));
					} else {
						g2d.setStroke(new BasicStroke(strokeWidth,
								w.getType().isBridge() ? BasicStroke.CAP_BUTT : BasicStroke.CAP_BUTT, 
								BasicStroke.JOIN_ROUND));
					}
					g2d.setColor(renderStyle.getBorderColor(w));
					if (strokeWidth > 0f) {
						g2d.drawPolyline(p.xpoints, p.ypoints, p.npoints);
					}
				}
	    	}
		}
		
		private void drawWayLine(Way w, Polygon p, Graphics2D g2d) {
			if (renderStyle.isVisible(w, zoomlevel)) {
				if (renderStyle.isTransparant(w)) {
					float alpha = 0.3f;
				    int rule = AlphaComposite.SRC_OVER;
				    Composite comp = AlphaComposite.getInstance(rule , alpha);
				    g2d.setComposite(comp );
				} 
					
				float strokeWidth = renderStyle.getLineWidth(w, zoomlevel);
				if (renderStyle.isLineDashed(w)) {
					g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
							10.0f, renderStyle.getLineDashLenths(w, zoomlevel), 0.0f));
				} else {
					g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				}
				g2d.setColor(renderStyle.getLineColor(w));
				if (strokeWidth > 0f) {
					g2d.drawPolyline(p.xpoints, p.ypoints, p.npoints);
				}
			}
			
			
			float alpha = 1f;
		    int rule = AlphaComposite.SRC_OVER;
		    Composite comp = AlphaComposite.getInstance(rule , alpha);
		    g2d.setComposite(comp );
		}
		
		private void drawWays(Graphics2D g2d) {
			final Set<Way> ways = data.getWays(indexX, indexY, this.zoomlevel);
//			System.out.println(""+ ways.size() + " ways on Tile" + new TileCoord(this.indexX, this.indexY).toString());
			final Map<Way, Polygon> waysMap = this.getPolygonsForWays(ways);
			final List<Way>[] layers = this.divideWaysInLayers(waysMap);

			float alpha = 1f;
		    int rule = AlphaComposite.SRC_OVER;
		    Composite comp = AlphaComposite.getInstance(rule , alpha);
		    g2d.setComposite(comp );
		    
		    for (List<Way> layer : layers) {
		    	for (Way w : layer) {
			    	this.drawWayBorder(w, waysMap.get(w), g2d);
				}
		    	
		    	for (Way w : layer) {
					this.drawWayLine(w, waysMap.get(w), g2d);
				}
		    }
		}
		
		private Map<Way, Polygon> getPolygonsForWays(final Set<Way> ways) {
			final HashMap<Way, Polygon> wayMap = new HashMap<Way, Polygon>();
			
			for (Way w : ways) {
				Polygon p = new Polygon();
				for (Node n : w.getNodes()) {
					this.nodeToInt(n, point);
					p.addPoint(point[0], point[1]);
				}
				wayMap.put(w, p);
			}
			
			return wayMap;
		}
		
		
		private void drawAreas(Graphics2D g2d) {
			final Set<Area> areas = this.getAreasToDraw();
			final List<Area>[] layers = this.divideAreasInLayers(areas);
			
			for (List<Area> layer : layers) {
				for(Area a : layer) {
					if (renderStyle.isVisible(a, zoomlevel))
						this.drawArea(a, g2d);
				}
			}
		}
		
		private void drawArea(Area a, Graphics2D g2d) {
			float alpha = 1f;
		    int rule = AlphaComposite.SRC_OVER;
		    Composite comp = AlphaComposite.getInstance(rule , alpha);
		    g2d.setComposite(comp );
		    
		    
		    g2d.setColor(renderStyle.getFillColor(a));
			g2d.fillPolygon(this.getAreaPolygon(a));
			
			if (renderStyle.hasBorder(a)) {
				g2d.setColor(renderStyle.getBorderColor(a));
				g2d.setStroke(new BasicStroke(renderStyle.getBorderWidth(a)));
				g2d.draw(this.getAreaPolygon(a));
			}
		}
		
		private Polygon getAreaPolygon(Area a) {
			Polygon p = new Polygon();
			for (Node n : a.getNodes()) {
				this.nodeToInt(n, point);
				p.addPoint(point[0], point[1]);
			}
			return p;
		}
		
		
		private void nodeToInt(Node n, int[] p) {
			long x1, y1;
			x1 = (long)MercadorProjection.longitudeToPixelX(n.getLongitude(), this.zoomlevel);
			x1 -= 256 * this.indexX;
			y1 = (long)MercadorProjection.latitudeToPixelY(n.getLatitude(), this.zoomlevel);
			y1 -= 256 * this.indexY;
			p[0] = (int)x1;
			p[1] = (int)y1;
		}
		
		private void coordToInt(Coordinate c, int[] p) {
			long x1, y1;
			x1 = (long)MercadorProjection.longitudeToPixelX(c.getLongitude(), this.zoomlevel);
			x1 -= 256 * this.indexX;
			y1 = (long)MercadorProjection.latitudeToPixelY(c.getLatitude(), this.zoomlevel);
			y1 -= 256 * this.indexY;
			p[0] = (int)x1;
			p[1] = (int)y1;
		}
		

		@Override
		public void cancel() {
			fireCanceledRenderingEvent(new Zoomlevel(zoomlevel), indexX, indexY);
		}
		
	}


	@Override
	protected void cancelRenderingJob(Runnable renderingJob) {
		// TODO Auto-generated method stub
		
	}

}
