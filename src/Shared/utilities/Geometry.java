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

package utilities;

/**
 * class to make geometric calculations
 * 
 * @author Fabian
 *
 */
public class Geometry {
	
	/**
	 * Check if the given point is inside the given rectangle.
	 * 
	 * @param px, py 					The point
	 * @param r1x, r1y, r2x, r2y 		The rectangle
	 * @return 							True if the given point is inside the given rectangle
	 */
	public static boolean isPointInsideRectangle(float px, float py, 
			float r1x, float r1y, float r2x, float r2y) {
		
		return isBetween(r1x, r2x, px) && isBetween(r1y, r2y, py);
	}
	
	/**
	 * Check if the given line is intersecting the given rectangle.
	 * 
	 * @param l1x, l1y, l2x, l2y		The line
	 * @param r1x, r1y, r2x, r2y		The rectangle
	 * @return 							True if the given line is intersecting the given rectangle
	 */
	public static boolean isLineIntersectingRectangle(float l1x, float l1y, float l2x, float l2y,
			float r1x, float r1y, float r2x, float r2y) {
			
		float rTop = Math.max(r1y, r2y);
		float rBot = Math.min(r1y, r2y);
		float rRight = Math.max(r1x, r2x);
		float rLeft = Math.min(r1x, r2x);
		
		//intersecting if one point is inside the rectangle
		if (Geometry.isPointInsideRectangle(l1x, l1y, r1x, r1y, r2x, r2y)
			|| Geometry.isPointInsideRectangle(l2x, l2y, r1x, r1y, r2x, r2y)) {
			return true;
		}
		
		//if intersecting, two sides of the rectangle are intersected
		//check 3 of them
		//check against top
		if (Geometry.isLineIntersectingLine(l1x, l1y, l2x, l2y, rLeft, rTop, rRight, rTop)) {
			return true;
		}
		
		//check against left
		if (Geometry.isLineIntersectingLine(l1x, l1y, l2x, l2y, rLeft, rBot, rLeft, rTop)) {
			return true;
		}
		
		//check against right
		if (Geometry.isLineIntersectingLine(l1x, l1y, l2x, l2y, rRight, rBot, rRight ,rTop)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Check if the two lines are intersecting.
	 * 
	 * @param x0, y0, x1, y1			The 1st line
	 * @param x2, y2, x3, y				The 2nd line
	 * @return							True if the two lines are intersecting
	 */
	public static boolean isLineIntersectingLine(float x0, float y0, float x1, float y1,
			float x2, float y2, float x3, float y3) {
		int s1 = Geometry.sameSide(x0, y0, x1, y1, x2, y2, x3, y3);
        int s2 = Geometry.sameSide(x2, y2, x3, y3, x0, y0, x1, y1);

        return (s1 <= 0 && s2 <= 0);
	}
	
	
	
	/**
	 * Checks if c is between a and b.
	 */
	private static boolean isBetween(float a, float b, float c) {
		return a < b ? c >= a && c <= b : c >= b && c <= a;
	}
	
	
	
	
	/**
     * Check if two points are on the same side of a given line.
     * Algorithm from Sedgewick page 350.
     *
     * @param x0, y0, x1, y1  		The line.
     * @param p0x, p0y        		First point.
     * @param p1x, p1y        		Second point.
     * @return                		<0 if points on opposite sides.
     *                        		=0 if one of the points is exactly on the line
     *                        		>0 if points on same side.
     */
    private static int sameSide(float x0, float y0, float x1, float y1,
    		float p0x, float p0y, float p1x, float p1y) {
        int sameSide = 0;

        double dx = x1 - x0;
        double dy = y1 - y0;
        double dx1 = p0x - x0;
        double dy1 = p0y - y0;
        double dx2 = p1x - x1;
        double dy2 = p1y - y1;

        // Cross product of the vector from the endpoint of the line to the point
        double c1 = dx * dy1 - dy * dx1;
        double c2 = dx * dy2 - dy * dx2;

        if (c1 != 0 && c2 != 0) {
            sameSide = c1 < 0 != c2 < 0 ? -1 : 1;
        } else if (dx == 0 && dx1 == 0 && dx2 == 0) {
            sameSide = !isBetween(y0, y1, p0y) && !isBetween(y0, y1, p1y) ? 1 : 0;
        } else if (dy == 0 && dy1 == 0 && dy2 == 0) {
            sameSide = !isBetween(x0, x1, p0x) && !isBetween(x0, x1, p1x) ? 1 : 0;
        }

        return sameSide;
    }
}
