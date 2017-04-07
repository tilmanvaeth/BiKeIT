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

import utilities.Geometry;
import dataTypes.CoordinateRect;

public class Edge extends Element {
	
	private static final float CYCLEQUANTIFIER = 0.4f;
	private static final float CITYQUANTIFIER = 0.6f;
	private static final float DIRTQUANTIFIER = 1;
	private static final float TERTIARYQUANTIFIER = 0.7f;
	private static final float SECONDARYQUANTIFIER = 0.9f;
	private Node src;
	private Node targ;
	private Way partOf;
	// set all flags to 0 so the arc flag calculator can work properly
	// 0 will only last as the arcFlag(s) of an edge, if the arc flags haven't been calculated
	private volatile long arcFlags = 0; 
	
	public boolean equals(Object o) {
		if (!(o instanceof Edge))
			return false;
		
		Edge other = (Edge)o;
		
		if (this.arcFlags != other.arcFlags)
			return false;
		
		if (this.src.getId() != other.src.getId())
			return false;
		
		if (this.targ.getId() != other.targ.getId())
			return false;
		
		if (this.partOf.getType() != other.partOf.getType())
			return false;
		
		return true;
	}
	
	public Edge (Node src, Node targ, Way partOf) {
		this.src = src;
		this.targ = targ;
		this.partOf = partOf;
//		src.addEdge(this);
//		targ.addIncomingEdge(this);
	}

	private float getQuantifier() {
		float quantifier = 1;
		String name = this.partOf.getType().name();
		
		
		if (name.contains("CYCLEWAY")) 				quantifier = CYCLEQUANTIFIER;
		else if (name.contains("INCITY")) 			quantifier = CITYQUANTIFIER;
		else if (name.contains("TERTIARYCYCLE")) 	quantifier = CYCLEQUANTIFIER;
		else if (name.contains("TERTIARY")) 		quantifier = TERTIARYQUANTIFIER;
		else if (name.contains("SECONDARYCYCLE"))	quantifier = CYCLEQUANTIFIER;
		else if (name.contains("SECONDARY")) 		quantifier = SECONDARYQUANTIFIER;
		else if (name.contains("PRIMARYCYCLE"))		quantifier = CYCLEQUANTIFIER;
		else if (name.contains("CITYANDCYCLE")) 	quantifier = CYCLEQUANTIFIER;
		else if (name.contains("DIRTROAD"))	 		quantifier = DIRTQUANTIFIER;
		else if (name.contains("PATH")) 			quantifier = CYCLEQUANTIFIER;
		else if (name.contains("UNKNOWN")) 			quantifier = CITYQUANTIFIER;
		else if (name.contains("ROUNDABOUT")) 		quantifier = CITYQUANTIFIER;
		else if (name.contains("SERVICE")) 			quantifier = CITYQUANTIFIER;
		else if(name.contains("FOOTANDCYCLE")) 		quantifier = CYCLEQUANTIFIER;
		else if(name.contains("SQUARE"))			quantifier = CITYQUANTIFIER;
		else 										quantifier = DIRTQUANTIFIER;
		
		/*
		WayType wayType = this.partOf.getType();
		switch(wayType) {
		
		case CYCLEWAY_T: quantifier = 0.5f;
		case FOOTWAY: quantifier = 0.5f;
//		case MOTORWAY: quantifier = Float.MAX_VALUE;
		case INCITY_T: quantifier = 1;
//		case FASTLANE: quantifier = Float.MAX_VALUE;
		case TERTIARY_T: quantifier = 1;
//		case WATERWAY: quantifier = Float.MAX_VALUE;
//		case RAILWAY: quantifier = Float.MAX_VALUE;
		case DIRTROAD_T: quantifier = 0.7f;
		case CITYANDCYCLE_T: quantifier = 0.6f;
		case TERTIARYCYCLE_T: quantifier = 0.4f;
		case PATH_T: quantifier = 0.7f;
		case UNKNOWN_T: quantifier = 0.7f;
		case PRIMARY: quantifier = 1;
		case SECONDARY_T: quantifier = 1;
		case PRIMARYCYCLE_T: quantifier = 0.7f;
		case SECONDARYCYCLE_T: quantifier = 0.5f;
		case ROUNDABOUT_T: quantifier = 1;
		case SERVICE_T: quantifier = 1;
		case FOOTANDCYCLE_T: quantifier = 1;
		default: quantifier = 1;
		}*/
		
		return quantifier;
		
	}

	public synchronized void setFlag(byte position) {
		arcFlags |=  (1l << position);
	}

	public Node getSource() {
		return this.src;
	}

	public Node getTarget() {
		return this.targ;
	}

	public Way getWay() {
		return this.partOf;
	}
	
	public float getQuantifiedLength() {
		return getQuantifier() * ((float) src.getPosition().distanceToCoordinate(targ.getPosition()));
	}

	public float getLength() {
		return (float) src.getPosition().distanceToCoordinate(targ.getPosition());
	}

	public boolean getFlag(byte position) {
		/* IMPORTANT: Use the >>> Operators so the sign bit is ignored */
		return (arcFlags & (1l << position)) >>> position == 1;
	}
	
	public long getFlags() {
		return arcFlags;
	}

	public void setFlags(long arcFlags) {
		this.arcFlags = arcFlags;
	}

	/**
	 * Checks if the edge intersects the given frame
	 */
	@Override
	public boolean isIntersect(CoordinateRect frame) {
		return Geometry.isLineIntersectingRectangle(src.getLongitude(), src.getLatitude(), targ.getLongitude(), targ.getLatitude(), 
				frame.getUL().getLongitude(), frame.getUL().getLatitude(), frame.getLR().getLongitude(), frame.getLR().getLatitude());
	}

	public void removeFromNode() {
		this.src.removeEdge(this);
	}
}
