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

package mapModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import utilities.GeometricDataStructure;
import utilities.MercadorProjection;
import dataTypes.TileCoord;


public class NodeData {

	private GeometricDataStructure<Node> nodeStruc;
	private static final int ZOOMLEVEL = dataTypes.Zoomlevel.ZOOMLEVEL_MAX-7;
	
	public NodeData(GeometricDataStructure<Node> nodeStruc) {
		this.nodeStruc = nodeStruc;
	}
	
	public int getNativeZoomlevel() {
		return ZOOMLEVEL;
	}
	
	
	public NodeData() {
		this.nodeStruc = new GeometricDataStructure<Node>();
	}
	
	
	public void addNode(Node node) {
		final List<TileCoord> keys = MercadorProjection.getAffectedTilesForZoom(node, ZOOMLEVEL);
		
		for (TileCoord key : keys) {
			nodeStruc.put(key, node);
		}
	}
	
	
	public Set<Node> getAllNodes() {
		return nodeStruc.getAllElements();
	}
	
	
	public Set<Node> getNodes(int indexX, int indexY, int zoomlevel) {
		if (zoomlevel == ZOOMLEVEL) {
			return nodeStruc.getElements(indexX, indexY);
		} 
		else if (zoomlevel < ZOOMLEVEL) {
			Set<Node> result = new HashSet<Node>();
			
			int factor = (1<<ZOOMLEVEL - zoomlevel);
			
			int xmin = indexX * factor;
			int ymin = indexY * factor;
			int xmax =  xmin + factor;
			int ymax = ymin + factor;
			
			for (int x = xmin; x < xmax; x++) {
				for(int y = ymin; y < ymax; y++) {
					result.addAll(nodeStruc.getElements(x, y));
				}
			}
			
//			System.out.println("" + result.size() + " Nodes found in Tile" + new TileCoord(indexX, indexY).toString());
			return result;
		} else {
			return null;
		}
	}
	
	
	public Set<Entry<TileCoord, ArrayList<Node>>> getAllEntries() {
		return nodeStruc.getEntrySet();
	}
	
	public void trim() {
		nodeStruc.trim();
		for (Node n : nodeStruc.getAllElements()) {
			n.trim();
		}
	}
	
	public Node getNode(int id, int indexX, int indexY) {
		Set<Node> nodes = nodeStruc.getElements(indexX, indexY);
		
		for (Node n: nodes) {
			if (n.getId() == id) return n;
		}
		
		return null;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof NodeData))
			return false;
		
		return this.nodeStruc.equals(((NodeData)o).nodeStruc);
	}
}
