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

package mapModelCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import utilities.Logger;
import utilities.LoggerFactory;

import mapModel.Edge;
import mapModel.Node;
import mapModel.Way;
import utilities.Exporter;
import utilities.PreprocessingDijkstra;

public class ArcFlagCalculator {
	
	private static final Logger logger = LoggerFactory.getLogger(MapModelBuilder.class);
	
	private List<Way> ways;
	private int counter = 0;
	private int total = 0;
	
	public ArcFlagCalculator(List<Way> ways) {
		this.ways = ways;
	}
	
	public void addArcFlagsToMapModel() {
		
		final List<Node> nodes = getNodesWithRegions();
		
		// thread pool
		final LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
		final ThreadPoolExecutor pool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), Long.MAX_VALUE, TimeUnit.NANOSECONDS, workQueue);
		LinkedList<Future<?>> futures = new LinkedList<Future<?>>();
		// ---
		
		logger.startAndLog("Start calculating arc flags.");
		
		for (final Node n: nodes) {
			for (final Edge e: n.getIncomingEdges()) {

				// set the flag for the own region
				e.setFlag(n.getRegion());
				
				if (e.getSource().getRegion() != e.getTarget().getRegion()) {
					/*
					 * Only take the border edges for
					 */
					
					// increase number of total jobs to work off
					total++;
					
					futures.add(pool.submit(new Runnable() {
						@Override
						public void run() {
							new PreprocessingDijkstra().computeArcFlags(n, nodes);
							synchronized(ArcFlagCalculator.this) {
								// increase number of finished jobs
								counter++;
							}
							//if ((total>1000)&&counter%(total/1000)==0) {
								double percentage = Math.round((counter*1./total*100.)*10)/10.;
								logger.updateLog("Calculated "+percentage+"% of the arc flags.");
							//}
						}
					}));
					
				}
				
			}
			
		}
		
		// wait for all jobs
		for (Future<?> future : futures) {
			try {
				future.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		pool.shutdown();
		logger.stopAndLog("Added arc flags to the edges of " + nodes.size() + " nodes");
		
	}

	private List<Node> getNodesWithRegions() {
		Set<Node> nodeSet = new HashSet<Node>();
		
		for (Way w: ways) {
			if (w.isTrafficable()) {
				nodeSet.addAll(w.getNodes());
			}
		}
		
		List<Node> nodes = new ArrayList<Node>(nodeSet);
		
		Exporter.writeMetisInputFile(nodes);
		
		String os = System.getProperty("os.name").toLowerCase();
		
		if (os.contains("win")) {
			logger.log("Runnning METIS on Windows.");
			execute("cmd /c gpmetis.exe graphInput.txt 64");
		} else if (os.contains("linux")) {
			logger.log("Runnning METIS on Linux.");
			execute("./gpmetis graphInput.txt 64");
		} else if (os.contains("mac")) {
			logger.log("Runnning METIS on Mac OS.");
			execute("gpmetis graphInput.txt 64");
		} else {
			logger.log("No METIS support on "+os+" yet.");
			System.exit(0);
		}
		
		Exporter.readMetisOutputFile(nodes);
		
		return nodes;
	}
	
	/*
	 * Execuctes a program and prints it's output
	 */
	private void execute(String command) {
		
		String text = "";
		
		try {
			
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
	        
	        while ((text = in.readLine()) != null) {
	        	logger.log("METIS: "+text);
	        }
	        
	        while ((text = err.readLine()) != null) {
	        	logger.log("METIS: "+text);
	        }

	        try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	        
		} catch (IOException e) {
			logger.log("Please install METIS on your System!");
			logger.log("The gpmetis executable has to be in your current directory.");
			System.exit(0);
		}
		
	}
	

}
