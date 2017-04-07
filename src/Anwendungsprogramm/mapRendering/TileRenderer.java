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

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.event.EventListenerList;

import controllers.TileRenderingEventListener;
import dataTypes.Zoomlevel;



public abstract class TileRenderer {
	
	private final ThreadPoolExecutor tPool;
	
	private LinkedBlockingQueue<Runnable> workQueue;
	
	protected EventListenerList listenerList = new EventListenerList();
	
	
	public TileRenderer(int numThreads) {
		this.workQueue = new LinkedBlockingQueue<Runnable>();
		this.tPool = new ThreadPoolExecutor(numThreads, numThreads, Long.MAX_VALUE, TimeUnit.NANOSECONDS, workQueue);
	}
	
	public void startRenderingTile(final Zoomlevel zoomlevel, final int indexX, final int indexY) {
		tPool.execute(createTileRenderingJob(zoomlevel.getValue(), indexX, indexY));
	}
	
	public void emptyWorkingQueue() {
		ArrayList<Runnable> canceledJobs = new ArrayList<Runnable>(workQueue.size());
		workQueue.drainTo(canceledJobs);
		
		for (Runnable renderingJob : canceledJobs) {
			if(renderingJob instanceof TileRenderingJob) {
				((TileRenderingJob)renderingJob).cancel();
			}
		}
	}
	
	public boolean isIdle() {
		return workQueue.size() == 0;
	}
	
	public void addMapTileRenderingEventListener(TileRenderingEventListener listener) {
		listenerList.add(TileRenderingEventListener.class, listener);
	}
	
	public void removeMapTileRenderingEventListener(TileRenderingEventListener listener) {
		listenerList.remove(TileRenderingEventListener.class, listener);
	}
	
	
	
	protected abstract TileRenderingJob createTileRenderingJob(int zoomlevel, int indexX, int indexY);
	
	protected abstract void cancelRenderingJob(Runnable renderingJob);
	
	
	protected abstract class TileRenderingJob implements Runnable {
		protected int zoomlevel;
		protected int indexX;
		protected int indexY;
		
		public TileRenderingJob(int zoomlevel, int indexX, int indexY) {
			this.zoomlevel = zoomlevel;
			this.indexX = indexX;
			this.indexY = indexY;
		}
		
		@Override public abstract void run();
		public abstract void cancel();
	}
}
