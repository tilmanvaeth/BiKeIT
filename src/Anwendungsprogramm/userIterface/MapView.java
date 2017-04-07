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

package userIterface;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import controllers.MapViewController;


public class MapView extends JPanel {
	
	private static final long serialVersionUID = -7081517290750627650L;
	
	private MapViewController controller;

	private int imageWidth;
	private int imageHeight;
	private int offsetX;
	private int offsetY;
	private int numTilesX;
	private int numTilesY;
	private int culumn;
	private int row;
	
	private JPanel mapLayer;
	private JPanel routeLayer;
	
	ArrayList<BufferedImage> mapImages;
	ArrayList<BufferedImage> routeImages;
	
	private boolean showDraggingWP;
	private Point draggingWPPosition;
	private int draggingWPIndex;
	
	private static BufferedImage markerImage;

	public MapView(MapViewController c) {
		super();
		this.controller = c;
		this.offsetX = 0;
		this.offsetY = 0;
		this.imageWidth = controller.getTileSizeWidth();
		this.imageHeight = controller.getTileSizeHeight();
		
		this.setLayout(new OverlayLayout(this));
		
		mapLayer = new JPanel() {			
			/**
			 * 
			 */
			private static final long serialVersionUID = 9091839945967908770L;

			@Override
			public void paint(Graphics g) {
				MapView.this.paintMap(g);
			}
		};
		
		routeLayer = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -7043664459794671853L;

			@Override
			public void paint(Graphics g) {
				MapView.this.paintRoute(g);
				MapView.this.paintDraggingWP(g);
			}
		};

		mapLayer.setOpaque(true);
		routeLayer.setOpaque(true);
		
		this.add(routeLayer);
		this.add(mapLayer);
		
	}
	
	public void setNogger() {
		try {
			markerImage = ImageIO.read(this.getClass().getClassLoader().getResource("nogger.png"));
			repaint();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void paint(Graphics g) {

		this.updateValues();
		super.paint(g);
		
	}
	
	public void paintDraggingWP(Graphics g) {
		if (this.showDraggingWP) {
			Point p = this.draggingWPPosition;
			Graphics2D g2 = (Graphics2D) g;
			
			float alpha = 1f;
			int rule = AlphaComposite.SRC_OVER;
			Composite comp = AlphaComposite.getInstance(rule , alpha);
			g2.setComposite(comp );
			    
			g2.drawImage(getMarkerImage(), p.x - markerImage.getWidth()/2, p.y - markerImage.getHeight()- 10, null);
			g2.setColor(Color.black);
			g2.drawLine(p.x-10, p.y-5, p.x+10, p.y+5);
			g2.drawLine(p.x+10, p.y-5, p.x-10, p.y+5);
			g2.drawString(""+ this.draggingWPIndex, p.x - 4, p.y- 40);
		}
	}
	
	private BufferedImage getMarkerImage() {
		if (markerImage == null) {
			try {
				markerImage = ImageIO.read(this.getClass().getClassLoader().getResource("marker.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return markerImage;
	}

	

	public void updateValues() {
		this.mapImages = this.controller.getMapTiles();
		this.routeImages = this.controller.hasRoute() ? this.controller.getRouteTiles() : null;
		this.offsetX = this.controller.getOffsetX();
		this.offsetY = this.controller.getOffsetY();
		this.numTilesX = this.controller.getNumTilesX();
		this.numTilesY = this.controller.getNumTilesY();
		this.showDraggingWP = this.controller.hasDraggingWP();
		this.draggingWPPosition = this.controller.getDraggingWPPosition();
		this.draggingWPIndex = this.controller.getDraggingWPIndex();
	}
	
	
	private void paintMap(Graphics g) {
		
		this.culumn = 0;
		this.row = 0;
		
		if (numTilesX * numTilesY != mapImages.size()) {
			System.err.print("MapViewController's getMapTiles() does not match ArrayList's size()");
		}

		for (BufferedImage image : mapImages) {
			g.drawImage(image, 
					culumn * imageWidth + offsetX, 
					row * imageHeight + offsetY, 
					this);
			
			culumn++;
			if (culumn >= numTilesX) {
				row++;
				culumn = 0;
			}
		}
	}
	
	
	private void paintRoute(Graphics g) {
		if (routeImages == null) {
			return;
		}
		
		this.culumn = 0;
		this.row = 0;
		
		if (numTilesX * numTilesY != routeImages.size()) {
			System.err.print("MapViewController's getRouteTiles() does not match ArrayList's size()");
		}
		
		for (BufferedImage image : routeImages) {
			g.drawImage(image, 
					culumn * imageWidth + offsetX, 
					row * imageHeight + offsetY, 
					this);
			culumn++;
			if (culumn >= numTilesX) {
				row++;
				culumn = 0;
			}
		}
		
	}
	
}
