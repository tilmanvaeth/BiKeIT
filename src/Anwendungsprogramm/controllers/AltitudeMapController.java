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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import mapModel.Edge;
import runTimeData.CalculatedRoute;

public class AltitudeMapController implements AltitudeMapViewController {

	private int wPanel=1;
	private int hPanel=1;
	private CalculatedRoute route;
	private BufferedImage image;
	private int offsetX=0;
	// offset of the x-axis to the bottom
	private int offsetY=10;
	// offset of the graph to the top
	private int offsetTop=5;
	// offset of the graph to the right
	private int offsetRight=10;
	
	private int wGraph=500;
	private int hGraph=100;
	
	// label steps in meters
	private int stepX=100;
	private int stepY=10;
	
	// scale factors
	private double scaleX=1;
	private double scaleY=1;
	
	
	private final int FONT_SIZE = 8;
	private String distanceUnit = "m";
	
	public void setMapDimension(Dimension d) {
		wPanel = (int) Math.round(d.getWidth());
		hPanel = (int) Math.round(d.getHeight());
		repaint();
	}
	
	public void setCalculatedRoute(CalculatedRoute r) {
		route = r;
		repaint();
	}
	
	@Override
	public BufferedImage getAltitudeMapViewImage() {
		return image;
	}
	
	public void repaint() {
		
		// init drawing area
		image = new BufferedImage(wPanel, hPanel, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) image.getGraphics();
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		double distance = 0;
		int maxHeight = Integer.MIN_VALUE;
		int minHeight = Integer.MAX_VALUE;
		int deltaHeight = 50;
		
		if (route!=null) {
			
			// determine total distance and maximum altitude
			for (Edge e : route.getEdges()) {
				distance += e.getLength();
				if (e.getSource().getAltitude()>maxHeight) maxHeight = e.getSource().getAltitude();
				if (e.getTarget().getAltitude()>maxHeight) maxHeight = e.getTarget().getAltitude();
				if (e.getSource().getAltitude()<minHeight) minHeight = e.getSource().getAltitude();
				if (e.getTarget().getAltitude()<minHeight) minHeight = e.getTarget().getAltitude();
			}
			
			minHeight = roundDownNicely(minHeight);
			
		} else {
			distance = 1600;
			maxHeight = 99;
			minHeight = 0;
		}

		deltaHeight = Math.max(maxHeight-minHeight, 50);
		initParameters(distance, minHeight, deltaHeight);
		
		if (route!=null) {
			paintGraph(g, minHeight);
		}
		
		paintLabels(g, (int) distance, minHeight);
		paintAxes(g);

	}
	
	private void initParameters(double d, int minHeight, int deltaHeight) {

		double distance = d;
		
		// set distance unit (m or km)
		if (distance > 10000) {
			distanceUnit = "km";
		} else {
			distanceUnit = "m";
		}
		
		// offset of the y-Axis to the left
		// according of the length of the labels
		offsetX = ((minHeight+deltaHeight)+distanceUnit).length()*FONT_SIZE;
		
		// init graph width
		wGraph = wPanel - offsetX - offsetRight;
		hGraph = hPanel - offsetY - offsetTop;
		
		scaleX = wGraph*1./distance;
		scaleY = hGraph*1./deltaHeight;
		
		// calculate stepX
		int maxLabelWidth = (((int)distance)+"m").length()*FONT_SIZE+50;
		if (distanceUnit.equals("km")) {
			maxLabelWidth = (((int)(distance/1000))+"km").length()*FONT_SIZE+50;
		}
		int numLabelsX = (int) (wGraph*1. / maxLabelWidth);
		stepX = roundNicely(distance*1./numLabelsX);

		// calculate stepY
		int labelHeight = FONT_SIZE+7;
		int numLabelsY = (int) (hGraph*1. / labelHeight);
		stepY = roundNicely(deltaHeight*1./numLabelsY);
		
		// recalculate offsetX, graphWidth and
		// scaleX according to the altitude labels
		int i=0;
		while (hPanel - (int) ((i*stepY)*scaleY-FONT_SIZE/2.) - FONT_SIZE > 0) {
			i++;
		}
		offsetX = ((minHeight+i*stepY)+"m").length()*FONT_SIZE;
		wGraph = wPanel - offsetX - offsetRight;
		scaleX = wGraph*1./distance;
		
	}
	
	private void paintAxes(Graphics2D g) {
		
		g.setColor(Color.BLACK);
		
		// x-axis
		g.drawLine(offsetX, hPanel-offsetY, wPanel, hPanel-offsetY);
		// arrowhead
		g.drawLine(wPanel-1, hPanel-offsetY, wPanel-3, hPanel-offsetY+2);
		g.drawLine(wPanel-1, hPanel-offsetY, wPanel-3, hPanel-offsetY-2);
		
		// y-axis
		g.drawLine(offsetX, hPanel-offsetY, offsetX, 0);
		// arrowhead
		g.drawLine(offsetX, 0, offsetX-2, 2);
		g.drawLine(offsetX, 0, offsetX+2, 2);
		
	}
	
	private void paintLabels(Graphics2D g, int length, int minHeight) {
		
		g.setColor(Color.BLACK);
		g.setFont(g.getFont().deriveFont(FONT_SIZE*1.f));
		
		// draw distance labels
		for (int i=1;;i++) {
			
			int label = i*stepX;
			
			if (distanceUnit.equals("km")) {
				label /= 1000;
			}
			
			int labelSize = (label+distanceUnit).length()*FONT_SIZE;
			int pos = (int) (offsetX + i*stepX*scaleX - labelSize/2.);
			
			// only paint label if it's completely visible
			if (pos + labelSize < wPanel) {
				g.drawString(label+distanceUnit, pos, hPanel);
			} else {
				break;
			}
			
		}
		
		// draw altitude labels
		for (int i=0;;i++) {
			
			int label = minHeight+i*stepY;
			int labelSize = (label+"m").length()*(FONT_SIZE-3);
			int pos = (int) (i*stepY*scaleY-FONT_SIZE/2.);
			
			// only paint label if it's completely visible
			if (hPanel - offsetY - pos - FONT_SIZE > 0) {
				g.drawString(label+"m", offsetX-labelSize-5, hPanel-offsetY-pos);
			} else {
				break;
			}
			
		}
		
	}
	
	private void paintGraph(Graphics2D g, int minHeight) {
		
		double currentX = offsetX;
		int lastHeight = 0;
		
//		Node oldNode = null;
		
		for (Edge e : route.getEdges()) {

//			if (oldNode != null && oldNode != e.getSource()) {
//				System.err.println("Target und Source passen nicht zusammen!");
//				System.err.println(oldNode == e.getTarget());
//				System.err.println("s: "+e.getSource().getPosition());
//				System.err.println("t: "+e.getTarget().getPosition());
//				continue;
//			}
			
//			oldNode = e.getTarget();
			
			int iCurrentX = (int) Math.round(currentX);
			double d = e.getLength();
			int deltaX = (int) Math.round((d*scaleX));
			int height1 = (int) Math.round((e.getSource().getAltitude()-minHeight)*scaleY);
			int height2 = (int) Math.round((e.getTarget().getAltitude()-minHeight)*scaleY);
			double gradient = (e.getTarget().getAltitude()-e.getSource().getAltitude())/d;

//			System.out.println(Math.round(gradient*10000)/100+"% Steigung");
			
			// make the drawing color dependent on the gradient
			g.setColor(getColor(gradient));
			
			// draw the current part of route
			g.fillPolygon(new int[]{iCurrentX,iCurrentX,iCurrentX+deltaX,iCurrentX+deltaX},
					new int[]{offsetTop+hGraph, offsetTop+hGraph-height1, offsetTop+hGraph-height2, offsetTop+hGraph}, 4);
			
			// draw a black line at the top of the polygon
		    g.setColor(Color.BLACK);
		    g.drawLine(iCurrentX, offsetTop+hGraph-height1, iCurrentX+deltaX, offsetTop+hGraph-height2);
					
			currentX += (d*scaleX);
			lastHeight = height2;
			
		}
		
		// delimiter line on the right
		int iCurrentX = (int) Math.round(currentX);
		g.drawLine(iCurrentX, offsetTop+hGraph-lastHeight, iCurrentX, offsetTop+hGraph);
		
	}
	
	private Color getColor(double gradient) {
		Color c;
		int saturation = (int) Math.max(0, Math.min((1-Math.abs(gradient)*3)*255, 255));
		if (gradient<0)
			// white to red
			c = new Color(saturation,255,saturation);
		else
			// white to green
			c = new Color(255,saturation,saturation);
		return c;
	}
	
	private int roundNicely(double arg) {
		
		int numDigits = String.valueOf((int)arg).length();
		double res = arg / Math.pow(10, numDigits-1);
		
		if (res < 1.5) {
			res = 1;
		} else if (res >= 1.5 && res < 2.25) {
			res = 2;
		} else if (res >= 2.25 && res < 2.75) {
			res = 2.5;
		} else if (res >= 2.75 && res < 7.5) {
			res = 5;
		} else {
			res = 10;
		}
		
		return (int) (res * Math.pow(10, numDigits-1));
		
	}
	
	private int roundDownNicely(double arg) {
		
		int numDigits = String.valueOf((int)arg).length();
		double res = arg / Math.pow(10, numDigits-1);
		
		if (res < 2) {
			res = 1;
		} else if (res < 2.5) {
			res = 2;
		} else if (res < 5) {
			res = 2.5;
		} else if (res < 10) {
			res = 5;
		} else {
			res = 10;
		}
		
		return (int) (res * Math.pow(10, numDigits-1));
		
	}
 
}
