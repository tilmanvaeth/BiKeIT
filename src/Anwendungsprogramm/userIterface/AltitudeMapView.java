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

import java.awt.Graphics;

import javax.swing.JPanel;

import controllers.AltitudeMapViewController;


public class AltitudeMapView extends JPanel {
	
	private static final long serialVersionUID = 4374433622334579408L;
	AltitudeMapViewController controller;
	
	public AltitudeMapView(AltitudeMapViewController c) {
		super();
		controller = c;
//		setBorder(BorderFactory.createLoweredBevelBorder());
	}
	
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(controller.getAltitudeMapViewImage(), 0, 0, null);
	}
	
}
