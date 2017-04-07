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

package jUnit;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import utilities.Geometry;


public class GeometryTest {

	@Test
	public void isLineIntersectingLineTest() {
		//Line(0,0)(1,1) - Line (0,1)(1,0) crossing (X)
		assertTrue(Geometry.isLineIntersectingLine(0, 0, 1, 1, 0, 1, 1, 0));
		
		//Line(1,0)(1,1) - Line (0,0)(0,1) parallel (||)
		assertFalse(Geometry.isLineIntersectingLine(1, 0, 1, 1, 0, 0, 0, 1));
		
		//Line(1,0)(1,1) - Line(0,1)(2,1) point on line (T)
		assertTrue(Geometry.isLineIntersectingLine(1, 0, 1, 1, 0, 1, 2, 1));
		
		//Line(1,1)(1,0) - Line(1,1)(0,1) same point (L)
		assertTrue(Geometry.isLineIntersectingLine(1, 1, 1, 0, 1, 1, 0, 1));
		
		//Line(0,0)(0,2) - Line(1,1)(2,1) not intersecting (|-)
		assertFalse(Geometry.isLineIntersectingLine(0, 0, 0, 2, 1, 1, 2, 1));
		
		//Line(0,-1)(0,1) - Line(0,0)(0,2) intersecting linesegment; same x
		assertTrue(Geometry.isLineIntersectingLine(0, -1, 0, 1, 0, 0, 0, 2));
				
		//Line(-1,0)(1,0) - Line(0,0)(2,0) intersecting linesegment; same y
		assertTrue(Geometry.isLineIntersectingLine(-1, 0, 1, 0, 0, 0, 2, 0));
	}
	
	@Test
	public void isPointInsideRectanctanleTest() {
		//Point (0,0)
		float inP1X = 0f;
		float inP1Y = 0f;
				
		//Point (-1, -1);
		float inP2X = -1f;
		float inP2Y = -1f;
		
		//Point (0, 2);
		float outP1X = 0f;
		float outP1Y = 2f;
			
		//Rectangle ((-1,-1), (1,1))
		float r0X = -1f;
		float r0Y = -1f;
		float r1X = 1f;
		float r1Y = 1f;
				
		assertTrue( utilities.Geometry.isPointInsideRectangle(inP1X, inP1Y, r0X, r0Y, r1X, r1Y) );
		assertTrue( utilities.Geometry.isPointInsideRectangle(inP2X, inP2Y, r0X, r0Y, r1X, r1Y) );
		assertFalse( utilities.Geometry.isPointInsideRectangle(outP1X, outP1Y, r0X, r0Y, r1X, r1Y) );
	}
	
	@Test
	public void isLineIntersectingRectangleTest() {
		//Rectangle ((-1,-1), (1,1))
		float r0X = -1f;
		float r0Y = -1f;
		float r1X = 1f;
		float r1Y = 1f;
		
		//Line(0,0)(0.5,0.5) point inside and right side
		assertTrue(Geometry.isLineIntersectingRectangle(0, 0, 0.5f, 0.5f, r0X, r0Y, r1X, r1Y));
		
		//Line(-2,0)(2,0) no point inside, left and right
		assertTrue(Geometry.isLineIntersectingRectangle(-2, 0, 2, 0, r0X, r0Y, r1X, r1Y));
		
		//Line(0,2)(0,-2) no point inside, top and bottom
		assertTrue(Geometry.isLineIntersectingRectangle(0, 2, 0, -2, r0X, r0Y, r1X, r1Y));

		//Line(-2,2)(2,-2) no point inside, edge to edge
		assertTrue(Geometry.isLineIntersectingRectangle(-2, 2, 2, -2, r0X, r0Y, r1X, r1Y));

		//Line(-2,2)(2,2) no point inside no crossing
		assertFalse(Geometry.isLineIntersectingRectangle(-2, 2, 2, 2, r0X, r0Y, r1X, r1Y));
		
		//Line(-1.5,-2)(2,5,2) no point inside cross bottom, right
		assertTrue(Geometry.isLineIntersectingRectangle(-1f, -2, 2, 1, r0X, r0Y, r1X, r1Y));
	}

}
