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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Font;
import java.util.Collections;

import mapModel.Area;
import mapModel.City;
import mapModel.Node;
import mapModel.Way;

import org.junit.Test;

import utilities.AbstractRenderStyle;
import utilities.RenderStyle;

import dataTypes.AreaType;
import dataTypes.CityType;
import dataTypes.WayType;


public class AbstractRenderStyleTest {

	private static class TestStyle extends AbstractRenderStyle {

		@Override
		public int getMaxWayZIndex() {
			return 2;
		}

		@Override
		public int getMaxAreaZIndex() {
			return 1;
		}

		@Override
		protected void initProperties() {
			this.setBackgroundColor("#123456");
			
			Group gTest = new Group("Test");
			this.setPropertiesForGroup(gTest,
					"zindex=1",
					"min-zoom=5",
					"max-zoom=10",
					"line-width=5:1;10:4.3",
					"line-color=white",
					"border-style=solid",
					"border-width=2%",
					"border-color=green");
			this.setGroup(gTest, WayType.BRIDGE, WayType.CITYANDCYCLE_B_NT);
			
			Group gTest2 = new Group("Test 2");
			this.setPropertiesForGroup(gTest2,
					"line-style=blabla",
					"border-style=blabla",
					"border-width=1",
					"line-width=1:5");
			this.setGroup(gTest2, WayType.MOTORWAY);
			
			Group gTestA = new Group("Test Area");
			this.setPropertiesForGroup(gTestA,
					"zindex=2",
					"min-zoom=4",
					"max-zoom=100",
					"fill-color=red",
					"border-color=cyan",
					"borderStyle=none");
			this.setGroup(gTestA, AreaType.BUILDING);
			
			Group gTestC = new Group("Test City");
			this.setPropertiesForGroup(gTestC,
					"min-zoom=8",
					"max-zoom=21",
					"font-name=Arial",
					"font-color=black",
					"font-stale=PLAIN",
					"font-size=8:12;10:20");
			this.setGroup(gTestC, CityType.CITY);
			
			
		}
		
	}
	
	private static RenderStyle testStyle = new TestStyle();
	private static Way testWay1 = new Way();
	private static Way testWay2 = new Way();
	private static Way testWay3 = new Way();
	private static Way testWay4 = new Way();
	private static Area testArea = new Area("TestName", Collections.<Node> emptyList());
	private static City testCity = new City("TestName", null);
	
	static {
		testWay1.setType(WayType.BRIDGE);
		testWay2.setType(WayType.CITYANDCYCLE_B_NT);
		testWay3.setType(WayType.CITYANDCYCLE_B_T);
		testWay4.setType(WayType.MOTORWAY);
		testArea.setType(AreaType.BUILDING);
		testCity.setType(CityType.CITY);
	}
	
	@Test
	public void LineWidthTest() {
		assertEquals(testStyle.getLineWidth(testWay1, 5), 1f, 0f);
		assertEquals(testStyle.getLineWidth(testWay1, 7), 1f, 0f);
		assertEquals(testStyle.getLineWidth(testWay1, 10), 4.3f, 0f);
		assertEquals(testStyle.getLineWidth(testWay1, 3), 0f, 0f);
	}
	
	@Test
	public void GroupTest() {
		for (int i = 0; i < 50; i++) {
			int zoom = (int)(java.lang.Math.random() * 20);
			assertEquals(testStyle.getLineWidth(testWay1, zoom), 
					testStyle.getLineWidth(testWay2, zoom), 0f);
		}
	}
	
	@Test
	public void LineColorTest() {
		assertSame(java.awt.Color.white, testStyle.getLineColor(testWay1));
	}
	
	@Test
	public void isVisibleTest() {
		assertFalse(testStyle.isVisible(testWay1, 4));
		assertTrue(testStyle.isVisible(testWay1, 5));
		assertFalse(testStyle.isVisible(testWay1, 11));
		
		assertFalse(testStyle.isVisible(testArea, 3));
		assertTrue(testStyle.isVisible(testArea, 6));
		assertTrue(testStyle.isVisible(testArea, 100));
		

		assertFalse(testStyle.isVisible(testCity, 3));
		assertTrue(testStyle.isVisible(testCity, 8));
		assertFalse(testStyle.isVisible(testCity, 22));
	}
	
	@Test
	public void getBackgroundColorTest() {
		assertEquals(testStyle.getBackgroundColor(), Color.decode("#123456"));
	}
	
	@Test
	public void noPropertyTest() {
		assertFalse(testStyle.isVisible(testWay3, 3));
		assertFalse(testStyle.isVisible(testWay3, 0));
	}
	
	@Test
	public void getBorderColorTest() {
		assertSame(testStyle.getBorderColor(testWay1), Color.green);
	}
	
	@Test
	public void getBorderWidthTest() {
		assertEquals(testStyle.getBorderWidth(testWay1, 5),
				testStyle.getLineWidth(testWay1, 5)*1.04f, 0.0f);
		assertEquals(testStyle.getTotalBorderWidth(testWay1, 5),
				testStyle.getLineWidth(testWay1, 5)*1.04f, 0.0f);
		
		assertEquals(testStyle.getBorderWidth(testArea), 1f, 0f);
		assertEquals(testStyle.getTotalBorderWidth(testWay4, 3), 7f, 0f);
		assertEquals(testStyle.getBorderWidth(testWay4, 3), 1f, 0f);
	}
	
	@Test
	public void getDashLengthTest() {
		assertArrayEquals(testStyle.getBorderDashLenths(testWay1, 5), new float[]{1f}, 0f);
		assertArrayEquals(testStyle.getLineDashLenths(testWay1, 5), new float[]{0f}, 0f);
		assertArrayEquals(testStyle.getLineDashLenths(testWay4, 5), new float[]{0f}, 0f);
		assertArrayEquals(testStyle.getBorderDashLenths(testWay4, 5), new float[]{0f}, 0f);
	}
	
	@Test
	public void isBorderDashedTest() {
		assertFalse(testStyle.isBorderDashed(testWay1));
	}
	
	@Test
	public void getZIndexTest() {
		assertEquals(testStyle.getZIndex(testArea), 2);
		assertEquals(testStyle.getZIndex(testWay1), 1);
	}
	
	@Test
	public void hasBorderTest() {
		assertTrue(testStyle.hasBorder(testWay1));
		assertFalse(testStyle.hasBorder(testArea));
	}
	
	@Test
	public void fontTest() {
		assertEquals(testStyle.getFontname(testCity), "arial");
		assertEquals(testStyle.getFontcolor(testCity), Color.black);
		assertEquals(testStyle.getFontsize(testCity, 9), 12);
		assertEquals(testStyle.getFontsize(testCity, 13), 20);
		assertEquals(testStyle.getFontstyle(testCity), Font.PLAIN);
	}
	
	@Test
	public void isTransparentTest() {
		assertFalse(testStyle.isTransparant(testWay1));
	}
	
	@Test
	public void getFillColorTest() {
		assertSame(testStyle.getFillColor(testArea), Color.red);
	}
	
	@Test
	public void isLineDashedTest() {
		assertFalse(testStyle.isLineDashed(testWay1));
	}
	
	@Test
	public void getBorderColor() {
		assertSame(testStyle.getBorderColor(testArea), Color.cyan);
	}

}
