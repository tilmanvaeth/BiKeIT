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

import guiControllers.DescriptionGUIController;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import userIterface.AltitudeMapView;
import userIterface.MapView;
import utilities.GUIUtilities;


public class PrintingController {

	private DescriptionGUIController controller;
	private PrinterJob job;
	private int numElements = 0;
	private int elementIndex = 0;
	private int oldElementIndex = 0;
	private int globalPageIndex = 0;
	
	public PrintingController(DescriptionGUIController c) {
		controller = c;
		job = PrinterJob.getPrinterJob();
		numElements = 3 + controller.getInstructions().size();
	}
	
	public void print() {
		 
	    if ( job.printDialog() == false ) return;
	    job.setPrintable(new PagePrinter());
	    
	    try {
			job.print();
		} catch (PrinterException e) {
			e.printStackTrace();
		}
        
	}
	
	// current y position
	private int y = 0;
	// x position
	private int x = 0;
	// page width
	private int w = 0;
	// page height
	private int h = 0;
	// gap between elements
	private int gap = 15;
	// hight of a string
	private int lineHeight = 15;
	
	private class PagePrinter implements Printable {

		@Override
		public int print(Graphics g, PageFormat pf, int pageIndex)
				throws PrinterException {
			
			/*
			 * Workaround: This method is called multiple times
			 * for each page. With the index, we have to determine
			 * if an element has already been printed.
			 */
			if (pageIndex == globalPageIndex) {
				elementIndex = oldElementIndex;
			} else {
				oldElementIndex = elementIndex;
				globalPageIndex = pageIndex;
			}
			
			// printing finished
			if (elementIndex >= numElements) {
				System.out.println("Printing finished.");
				return NO_SUCH_PAGE;
			}
			
			/* --- init --- */
			Graphics2D g2d = (Graphics2D) g;
		    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			x = (int) pf.getImageableX();
			y = (int) pf.getImageableY();
			w = (int) pf.getImageableWidth();
			h = (int) pf.getImageableHeight();
			/* ------ */

	    	// calculate the height of the altitude map.
	    	// is needed so early for the if()
			double altitudeScale = w*1. / controller.getAltitudeMapView().getWidth();
			int altitudeMapHeight = (int) (controller.getAltitudeMapView().getHeight() * altitudeScale);
	    	
			if (elementIndex==0) {
				printMap(g2d);
			}
			
			if (elementIndex==1 && y + altitudeMapHeight < h ) {
				printAltitudeMap(g2d, altitudeMapHeight);
			}
			
			if ( elementIndex==2 && y+3*lineHeight < h ) {
				printInfo(g2d);
			}

			printInstructions(g2d);
			printFootline(g2d, pageIndex);
			g.dispose();
			
			return PAGE_EXISTS;
			
		}
		
	}
	
	private void printFootline(Graphics2D g, int pageIndex) {
		g.setFont(g.getFont().deriveFont(10.0f));
		String fl = "BiKeIT Routenplaner - Seite "+(pageIndex+1);
		int flY = h + 2*lineHeight;
		int flX = w + 50 - g.getFontMetrics().stringWidth(fl);
		g.drawString(fl, flX, flY);
	}
	
	private void printInstructions(Graphics2D g) {

    	g.setFont(g.getFont().deriveFont(11.5f));
    	lineHeight = g.getFontMetrics().getHeight();
		g.setColor(Color.BLACK);
		
		while ( elementIndex > 2 && elementIndex < numElements && y + lineHeight < h ) {
			
			// print instructions
			
			g.drawString(controller.getInstructions().get(elementIndex-3), x, y+lineHeight);
			elementIndex++;
			y += lineHeight+7;
			
		}
		
	}
	
	private void printInfo(Graphics2D g) {

		// print route information

		g.setFont(g.getFont().deriveFont(14.0f));
    	lineHeight = g.getFontMetrics().getHeight();
		g.setColor(Color.BLACK);
		
		g.drawString("Streckenlänge: "+GUIUtilities.formatDistance(controller.getRouteLength()), x, y+lineHeight);
		y += lineHeight;
		g.drawString("Geschätzte Dauer bei " + (int)RouteInformationController.getSpeed() + " km/h: "+GUIUtilities.formatDuration(controller.getEstimatedDuration()), x, y+lineHeight);
		y += lineHeight;
		g.drawString("Zu bewältigende Höhenmeter: "+GUIUtilities.formatDistance(controller.getAltitudeMeters()), x, y+lineHeight);
		y += lineHeight;
		
		y += gap+5;
		elementIndex++;
		
	}
	
	private void printAltitudeMap(Graphics2D g, int altitudeMapHeight) {
		
		AltitudeMapView altitudeMapView = controller.getAltitudeMapView();
		
		BufferedImage img = new BufferedImage(altitudeMapView.getWidth(),
				altitudeMapView.getHeight(), BufferedImage.TYPE_INT_ARGB);
		altitudeMapView.paintComponent(img.getGraphics());

    	g.drawImage(img, x, y, w, altitudeMapHeight , null);
    	
    	elementIndex++;
    	y += altitudeMapHeight + gap;
		
	}
	
	private void printMap(Graphics2D g2d) {
		
		MapView mapView = controller.getMapView();
		
		double scale = w*1. / mapView.getWidth();
		int mapHeight = (int) (mapView.getHeight() * scale);

//		controller.setMapDimension(new Dimension(w, mapHeight));
		
		mapView.updateValues();
		
		// wait while rendering
		while (!controller.hasFinishedRenderingMap()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		g2d.translate(x, y);
		g2d.scale(scale, scale);
		mapView.paint(g2d);
		g2d.scale(1/scale, 1/scale);
		g2d.translate(-x*1., -y*1.);

//    	controller.setMapDimension(new Dimension(oldWidth, oldHeight));
    	
    	elementIndex++;
    	y += mapHeight + gap;
    	
	}
	
}
