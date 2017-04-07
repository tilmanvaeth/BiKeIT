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

package runTimeData;

import java.util.List;

import mapModel.Edge;
import mapModel.Node;
import utilities.MercadorProjection;
import dataTypes.Coordinate;
import dataTypes.CoordinateRect;
import dataTypes.Pixel;



public class CalculatedRoute {
	
	private List<Edge> edges;
	
	public CalculatedRoute(List<Edge> e) {
		edges = e;
	}
	
	public List<Edge> getEdges() {
		return edges;
	}
	
	public CoordinateRect getBounds() {
		Node n;
		
		double left = Double.MAX_VALUE;
		double right = 0;
		double up = Double.MAX_VALUE;
		double down = 0;
		
		double x;
		double y;
				
		for(Edge e : edges) {
			x = MercadorProjection.longitudeToPixelX(e.getSource().getLongitude(), 10);
			y = MercadorProjection.latitudeToPixelY(e.getSource().getLatitude(), 10);
			
			if(x < left) {
				left = x;
			}
			if(x > right) {
				right = x;
			}
			if(y > down) {
				down = y;
			}
			if(y < up) {
				up = y;
			}
		}
		
		n  = edges.get(edges.size() - 1).getTarget();
		x = MercadorProjection.longitudeToPixelX(n.getLongitude(), 10);
		y = MercadorProjection.latitudeToPixelY(n.getLatitude(), 10);
		
		if(x < left) {
			left = x;
		}
		if(x > right) {
			right = x;
		}
		if(y > down) {
			down = y;
		}
		if(y < up) {
			up = y;
		}
		
		Pixel p1 = new Pixel((long)left, (long)up);
		Pixel p2 = new Pixel((long)right, (long)down);
		
		Coordinate uL = MercadorProjection.pixelToCoordinate(p1, 10);
		Coordinate lR = MercadorProjection.pixelToCoordinate(p2, 10);
		
		return new CoordinateRect(uL, lR);
	}
}
