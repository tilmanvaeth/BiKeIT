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

package guiControllers;
import java.awt.Dimension;
import java.util.LinkedList;

import mapModel.MapModel;
import userIterface.AltitudeMapView;
import userIterface.MapView;
import controllers.AltitudeMapController;
import controllers.InstructionsController;
import controllers.PrintingController;
import dataTypes.CoordinateRect;


public class DescriptionGUIController extends GUIController {

	private AltitudeMapController altitudeMapController;
	private AltitudeMapView altitudeMapView;
	private InstructionsController instructionsController;

	public DescriptionGUIController(MapModel mapModel) {
		super(mapModel);
		altitudeMapController = new AltitudeMapController();
		altitudeMapView = new AltitudeMapView(altitudeMapController);
		instructionsController = new InstructionsController();
	}
	
	public void refresh() {
		
		mapController.updateWaypoints(planningController.getWaypoints());
		mapController.updateCalculatedRoute(calculatingController.getCalculatedRoute());
		altitudeMapController.setCalculatedRoute(calculatingController.getCalculatedRoute());
		altitudeMapView.repaint();
		
		initInstructions();
		initMap();
		
	}
	
	private void initInstructions() {
		instructionsController.setCalculatedRoute(calculatingController.getCalculatedRoute());
		instructionsController.setPlannedWaypoints(planningController.getWaypoints());
		instructionsController.process();
	}
	
	private void initMap() {
		CoordinateRect bounds = calculatingController.getCalculatedRoute().getBounds();
		mapController.zoomToCoordinateRect(bounds,
				(int)mapController.getMapDimension().getWidth(),
				(int) mapController.getMapDimension().getHeight());
		mapView.repaint();
	}
	
	public MapView getMapView() {
		return mapView;
	}
	
	public AltitudeMapView getAltitudeMapView() {
		return altitudeMapView;
	}
	
	public void setMapDimension(Dimension d) {
		mapController.updateDimensions(d);
		initMap();
	}
	
	public void setAltitudeMapDimension(Dimension d) {
		altitudeMapController.setMapDimension(d);
	}
	
	
	public LinkedList<String> getInstructions() {
		return instructionsController.getInstructions();
	}
	
	public void print() {
		new PrintingController(this).print();
	}
	
	public boolean hasFinishedRenderingMap() {
		return mapController.hasFinishedRendering();
	}


}
