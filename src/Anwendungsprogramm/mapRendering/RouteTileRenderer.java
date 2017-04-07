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
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import mapModel.Edge;
import mapModel.Node;
import runTimeData.CalculatedRoute;
import runTimeData.PlannedWaypoints;
import utilities.MercadorProjection;
import controllers.TileRenderingEventListener;
import dataTypes.Coordinate;
import dataTypes.Zoomlevel;



public class RouteTileRenderer extends TileRenderer{

	private static BufferedImage markerImage;
	
	private boolean hasRoute;
	private boolean hasWaypoints;
	
	private PlannedWaypoints waypoints;
	private CalculatedRoute route;
	
	public RouteTileRenderer(int numThreads) {
		super(numThreads);
		hasRoute = false;
		hasWaypoints = false;
		try {
			markerImage = ImageIO.read(this.getClass().getClassLoader().getResource("marker.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setMarkerImage(BufferedImage image) {
		markerImage = image;
	}
	
	public boolean hasRoute() {
		return hasWaypoints;
	}
	
	public void setWaypoints(PlannedWaypoints waypoints) {
		this.waypoints = waypoints;
		hasWaypoints = (waypoints.getSize() > 0);
	}
	
	public void setCalculatedRoute(CalculatedRoute route) {
		this.route = route;
		hasRoute = (route != null);
	}
	

	private void fireFinishedRenderingEvent(RouteTile tile) {
        Object[] listeners = listenerList.getListenerList();
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i] == TileRenderingEventListener.class) {
                ((TileRenderingEventListener)listeners[i+1]).finishedRenderingRouteTile(tile);
            }
        }
    }
	
	private void fireCanceledRenderingEvent(Zoomlevel zoomlevel, int indexX, int indexY) {
		Object[] listeners = listenerList.getListenerList();
		RouteTile tile = new RouteTile(zoomlevel, indexX, indexY, null);
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i] == TileRenderingEventListener.class) {
                ((TileRenderingEventListener)listeners[i+1]).canceledRenderingRouteTile(tile);
            }
        }
	}

	
	@Override
	protected void cancelRenderingJob(Runnable renderingJob) {
		if(renderingJob.getClass() == TileRenderingJob.class) {
			((TileRenderingJob)renderingJob).cancel();
		}
	}

	@Override
	protected TileRenderingJob createTileRenderingJob(int zoomlevel, int indexX, int indexY) {
		return new RouteTileRenderingJob(zoomlevel, indexX, indexY);
	}
	
	
	private class RouteTileRenderingJob extends TileRenderingJob {
		
		public RouteTileRenderingJob(int zoomlevel, int indexX, int indexY) {
			super(zoomlevel, indexX, indexY);
		}

		@Override
		public void run() {
			BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
			if (hasWaypoints) {
				Graphics2D g2 = img.createGraphics();
			    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			    
				if (hasRoute) {
					//render Route
					final Polygon routePoly = this.concatEdges(route.getEdges());
					this.drawRoute(routePoly, g2);
				}
				//render Waypoints
				for (Node node : waypoints.getWaypoints()) {
					this.drawWaypoint(node, g2);
				}
				
			    g2.dispose();
			    RouteTile tile = new RouteTile(new Zoomlevel(zoomlevel), indexX, indexY, img);
				fireFinishedRenderingEvent(tile);
			}
			else {
				RouteTile tile = new RouteTile(new Zoomlevel(zoomlevel), indexX, indexY, null);
				fireFinishedRenderingEvent(tile);
			}
		}
		

		
		private int[] nodeToInt(Node n) {
			long x1, y1;
			x1 = (long)MercadorProjection.longitudeToPixelX(n.getPosition().getLongitude(), this.zoomlevel);
			x1 -= 256 * this.indexX;
			y1 = (long)MercadorProjection.latitudeToPixelY(n.getPosition().getLatitude(), this.zoomlevel);
			y1 -= 256 * this.indexY;
			int[] point = new int[]{(int)x1,(int)y1};
			return point;
		}
		
		private Polygon concatEdges(final List<Edge> edges) {
			final Polygon p = new Polygon();
			int[] start = nodeToInt(edges.get(0).getSource());
			p.addPoint(start[0], start[1]);
			for (Edge e : edges) {
				int[] point = this.nodeToInt(e.getTarget());
				p.addPoint(point[0], point[1]);
			}
			
			return p;
		}
		
		
		private void drawRoute(Polygon p, Graphics2D g2d) {
			float alpha = 0.5f;
		    int rule = AlphaComposite.SRC_OVER;
		    Composite comp = AlphaComposite.getInstance(rule , alpha);
		    g2d.setComposite(comp );
		    
			float strokeWidth = 10f;
			g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2d.setColor(Color.blue);
			if (strokeWidth > 0f) {
				g2d.drawPolyline(p.xpoints, p.ypoints, p.npoints);
			}
		}
		
		
		private void drawWaypoint(Node node, Graphics2D g2) {
			Coordinate c = node.getPosition();
			double y = MercadorProjection.latitudeToPixelY(c.getLatitude(), zoomlevel);
			double x = MercadorProjection.longitudeToPixelX(c.getLongitude(), zoomlevel);
			
			x -= indexX * 256;
			y -= indexY * 256;
			
			if (x > -markerImage.getWidth()/2 && x < 256+markerImage.getWidth()/2 && y > 0 && y < 256 +markerImage.getHeight()) {
			    float alpha = 0.75f;
			    int rule = AlphaComposite.SRC_OVER;
			    Composite comp = AlphaComposite.getInstance(rule , alpha);
			    g2.setComposite(comp );
			    
				g2.drawImage(markerImage, (int)x - markerImage.getWidth()/2, (int)y - markerImage.getHeight(), null);
				
				g2.setColor(Color.black);
				g2.drawString(""+waypoints.getWaypoints().indexOf(node), (int)x - 4, (int)y- 30);
			}
		}

		@Override
		public void cancel() {
			fireCanceledRenderingEvent(new Zoomlevel(zoomlevel), indexX, indexY);
		}
		
	}
}
