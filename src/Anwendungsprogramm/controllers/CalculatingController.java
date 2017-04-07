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

import guiControllers.CalculationEventListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.event.EventListenerList;

import mapModel.Edge;
import mapModel.Node;
import runTimeData.CalculatedRoute;
import runTimeData.PlannedWaypoints;
import utilities.Dijkstra;
import utilities.Logger;
import utilities.LoggerFactory;



public class CalculatingController {
	
	private static Logger logger = LoggerFactory.getLogger(CalculatingController.class);
	private CalculatedRoute route;
	private EventListenerList listenerList = new EventListenerList();
	private static final byte TIMEOUT = 40; // timeout for calculating a part of the route (in seconds)
	
	private static final LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
	private static final ThreadPoolExecutor tPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 
																			Runtime.getRuntime().availableProcessors(), Long.MAX_VALUE, 
																			TimeUnit.NANOSECONDS, workQueue);
	
	public CalculatedRoute getCalculatedRoute() {
		return route;
	}
	
	public boolean isCalculated() {
		return (route!=null);
	}
	
	private Future<List<Edge>> getPartialRoute(final Node node1, final Node node2) {
		return tPool.submit( new Callable<List<Edge>>() {

			@Override
			public List<Edge> call() throws Exception {
				List<Edge> subEdges = new LinkedList<Edge>();
				//every Thread gets its own Dijkstra (need it for the memory)
				subEdges = new Dijkstra().findQualifiedPath(node1, node2);
				
				if(subEdges == null) {
					logger.log("No Route found!");
				} else {
					logger.log("A part of the route has been calculated.");
				}
				return subEdges;
			}
			
		});
	}
	
	public void findQualifiedRoute(final PlannedWaypoints waypoints) {
		new Thread( new Runnable(){
				public void run() {
					logger.startAndLog("Start calculating route.");
					final List<Node> routePoints = waypoints.getWaypoints();
					final List<Edge> routeEdges = new ArrayList<Edge>(20);
					
					final int numPartialRoutes = routePoints.size() - 1;
					@SuppressWarnings("unchecked")
					Future<List<Edge>>[] partialRoutes= new Future[numPartialRoutes];
					
					for(int i = 0; i < numPartialRoutes; i++) {
						partialRoutes[i] = getPartialRoute(routePoints.get(i), routePoints.get(i+1));
					}
					
					for (int i = 0; i < numPartialRoutes; i++) {
						// Waits for all TileRoutes
						try {
							List<Edge> edges = partialRoutes[i].get(TIMEOUT, TimeUnit.SECONDS);
							if (edges!=null) {
								routeEdges.addAll(edges);
							} else {
								// no route found for at least one target
								// => return null
								route = null;
								CalculatingController.this.fireCalculationFinishedEvent();
								return;
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (TimeoutException e) {
							System.out.println("Part " + (i+1) + " of the route has not been found in "+TIMEOUT+" Seconds.");
							route = null;
							CalculatingController.this.fireCalculationFinishedEvent();
							return;
						}
					}
					route = new CalculatedRoute(routeEdges);
					
					CalculatingController.this.fireCalculationFinishedEvent();
					logger.stopAndLog("Finished calculating route in ");
				}
			}).start();
		
	}
	
	public void addCalculationEventListener(CalculationEventListener l) {
		listenerList.add(CalculationEventListener.class, l);
	}
	
	public void removeCalculationEventListener(CalculationEventListener l) {
		listenerList.remove(CalculationEventListener.class, l);
	}
	
	private void fireCalculationFinishedEvent() {
		Object[] listeners = listenerList.getListenerList();
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i] == CalculationEventListener.class) {
                ((CalculationEventListener)listeners[i+1]).calculationFinished(this.route);
            }
        }
	}
	
}
