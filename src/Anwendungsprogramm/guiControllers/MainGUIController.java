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
import java.awt.Point;
import java.io.File;

import mapModel.MapModel;
import mapModel.Node;
import runTimeData.CalculatedRoute;
import userIterface.AltitudeMapView;
import userIterface.DescriptionGUI;
import userIterface.MapView;
import controllers.AltitudeMapController;
import dataTypes.Coordinate;
import dataTypes.Zoomlevel;



public class MainGUIController extends GUIController implements CalculationEventListener {

	private AltitudeMapController altitudeMapController;
	private AltitudeMapView altitudeMapView;
	private DescriptionGUIController descriptionGUIController;

	public MainGUIController(MapModel mapModel) {
		super(mapModel);
		altitudeMapController = new AltitudeMapController();
		altitudeMapView = new AltitudeMapView(altitudeMapController);
		descriptionGUIController = new DescriptionGUIController(mapModel);
		calculatingController.addCalculationEventListener(this);
	}
	
	public void addCalculationEventListener(CalculationEventListener listener) {
		calculatingController.addCalculationEventListener(listener);
	}
	
	public Zoomlevel getMapsZoomlevel() {
		return mapController.getZoomlevel();
	}
	
	public MapView getMapView() {
		return mapView;
	}
	
	public AltitudeMapView getAltitudeMapView() {
		return altitudeMapView;
	}

	public void zoomIn() {
		mapController.zoomIn();
		mapView.repaint();
	}

	public void zoomOut() {
		mapController.zoomOut();
		mapView.repaint();
	}
	
	public void setZoomlevel(Zoomlevel z) {
		mapController.setZoomlevel(z);
		mapView.repaint();
	}
	
	public void setMapDimension(Dimension d) {
		mapController.updateDimensions(d);
		mapView.repaint();
	}
	
	public void setAltitudeMapDimension(Dimension d) {
		altitudeMapController.setMapDimension(d);
		altitudeMapView.repaint();
	}
	
	public void zoomInMap(Point p) {
		mapController.zoomIn(p);
		mapView.repaint();
	}
	
	public Coordinate getCoordinateOfPointOnScreen(Point p) {
		return mapController.getCoordinateOfPointOnScreen(p);
	}

	public void moveMap(int deltaX, int deltaY) {
		mapController.moveMap(deltaX, deltaY);
		mapView.repaint();
	}
	
	public boolean isWaypoint(Point p) {
		return getWaypoint(p) != -1;
	}
	
	public int getNumWaypoints() {
		return planningController.getWaypoints().getSize();
	}
	
	public int getWaypoint(Point p) {
		int limit = 14*14;
		int distX;
		int distY;
		int qdist;
		for (Node node : planningController.getWaypoints().getWaypoints()) {
			Point wp = mapController.getPointOfCoordinateOnScreen(node.getPosition());
			if (Math.abs(p.x - wp.x) > 100 || Math.abs(p.y - wp.y) > 100) 
				continue;
			
			wp.y -= 36;
			wp.x -= 1;
			
			distX = wp.x - p.x;
			distY = wp.y - p.y;
			qdist = distX*distX+distY*distY;
			if((qdist) < limit) {
				return planningController.getWaypoints().getWaypoints().indexOf(node);
			}
		}
		return -1;
	}
	
	public Point getWaypointPosition(int index) {
		return  mapController.getPointOfCoordinateOnScreen(planningController.getWaypoints().getWaypoint(index).getPosition());
	}
	
	private void moveWaypoint(int index, Point p) {
		planningController.moveWaypoint(index, this.getNodeFromPoint(p));
		this.updateWaypoints();
		mapView.repaint();
	}
	
	public void startDraggingWaypoint(int index) {
		mapController.showDraggingWP(index, true);
	}
	
	public void dragWaypoint(int index, Point p) {
		mapController.updateDraggingWPPosition(p);
		mapView.repaint();
	}
	
	public void stopDraggingWaypoint(int index, Point p) {
		mapController.showDraggingWP(index, false);
		this.moveWaypoint(index, p);
	}
	
	public void addWaypoint(Point p) {
		Node nearestNode = getNodeFromPoint(p);
		if (nearestNode != null) {
			planningController.addWaypoint(nearestNode);
			updateWaypoints();
			mapView.repaint();
		}
	}
	
	public void removeWaypoint(int index) {
		planningController.deleteWaypoint(index);
		this.updateWaypoints();
		mapView.repaint();
	}
	
	public boolean hasLocation(String s) {
		if (s.toLowerCase().equals("nogger")) {
			return true;
		}
		return mapModel.getCityData().hasCity(s);
	}
	
	public void goToLocation(String s) {
		if (s.toLowerCase().equals("nogger")) {
			mapController.setNogger();
			mapView.setNogger();
			return;
		}
		mapController.centerMapAt(mapModel.getCityData().getCity(s).getPosition());
		mapView.repaint();
	}
	
	public void openDescriptionGUI() {
		new DescriptionGUI(descriptionGUIController);
	}
	
	public void resetCalculatedRoute() {
		//TODO
		System.out.println("STUB reset calculated Route");
		altitudeMapController.setCalculatedRoute(null);
		altitudeMapView.repaint();
		mapView.repaint();
	}
	
	public void resetPlannedWaypoints() {
		planningController.reset();
		this.updateWaypoints();
		mapView.repaint();
	}
	
	public void saveWaypointsToFile(File f) {
		planningController.saveWaypointsToFile(mapModel, f, mapController.getZoomlevel(), mapController.getCoordinateOfCenter());
	}
	
	public void loadWaypointsFromFile(File f) {
		planningController.loadWaypointsFromFile(mapModel, f, mapController);
		calculateRoute();
		this.updateWaypoints();
		mapView.repaint();
	}
	
	public void calculateRoute() {
		calculatingController.findQualifiedRoute(planningController.getWaypoints());
	}

	private void updateWaypoints() {
		mapController.updateWaypoints(planningController.getWaypoints());
	}

	private Node getNodeFromPoint(Point p) {
		return this.mapModel.getNearestNode(this.getCoordinateOfPointOnScreen(p), mapController.getZoomlevel().getValue());
	}

	@Override
	public void calculationFinished(CalculatedRoute route) {
		if (route!=null) {
			// route has been found
			routeInformationController.setCalculatedRoute(route);
			routeInformationController.process();
			this.mapController.updateCalculatedRoute(route);
			this.mapView.repaint();
			this.altitudeMapController.setCalculatedRoute(route);
			this.altitudeMapView.repaint();
			
			System.out.println("Calculated Route: length = " + routeInformationController.getRouteLength());
		}
	}

}
