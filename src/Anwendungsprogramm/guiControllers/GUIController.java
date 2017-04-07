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

import mapModel.MapModel;
import mapRendering.MapTile;
import mapRendering.RouteTile;
import userIterface.MapView;
import controllers.CalculatingController;
import controllers.MapController;
import controllers.PlanningController;
import controllers.RouteInformationController;
import controllers.TileRenderingEventListener;

public abstract class GUIController implements TileRenderingEventListener {
	
	protected static PlanningController planningController;
	protected static CalculatingController calculatingController;
	protected static RouteInformationController routeInformationController;
	protected MapView mapView;
	protected MapController mapController;
	protected MapModel mapModel;
	
	public GUIController(MapModel mapModel) {
		this.mapModel = mapModel;
		mapController = new MapController(mapModel);
		mapView = new MapView(mapController);
		mapController.addMapTileRenderingEventListener(this);
		planningController = new PlanningController();
		calculatingController = new CalculatingController();
		routeInformationController = new RouteInformationController();
	}
	
	public int getEstimatedDuration() {
		return routeInformationController.getEstimatedDuration();
	}
	
	public int getRouteLength() {
		return routeInformationController.getRouteLength();
	}
	
	public int getAltitudeMeters() {
		return routeInformationController.getAltitudeMeters();
	}

	@Override
	public void finishedRenderingMapTile(MapTile tile) {
		mapView.repaint();
	}

	@Override
	public void canceledRenderingMapTile(MapTile tile) {
//		mapView.repaint();
	}

	@Override
	public void finishedRenderingRouteTile(RouteTile tile) {
		mapView.repaint();
	}

	@Override
	public void canceledRenderingRouteTile(RouteTile tile) {
//		mapView.repaint();
	}
	
}
