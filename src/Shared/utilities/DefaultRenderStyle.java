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

import dataTypes.AreaType;
import dataTypes.CityType;
import dataTypes.WayType;

public class DefaultRenderStyle extends AbstractRenderStyle {

	@Override
	public int getMaxWayZIndex() {
		return 30;
	}
	
	@Override
	public int getMaxAreaZIndex() {
		return 15;
	}
	
	@Override
	protected void initProperties() {

		this.setBackgroundColor("#F2EFE9");
		
		Group gMotorway = new Group("Motorway");
		this.setPropertiesForGroup(gMotorway, 
				"zindex=19",
				"min-zoom=7",
				"line-width=7:1.25;13:2.5;14:3.5;15:4;16:8;17:10;18:12",
				"line-color=#FFC345",
				"border-style=solid",
				"border-color=#fd923a",
				"border-width=15%");
		this.setGroup(gMotorway, 
				WayType.MOTORWAY,
				WayType.FASTLANE);
		
		Group gMotorway_L = new Group("Motorway Link");
		this.setPropertiesForGroup(gMotorway_L, 
				"zindex=17",
				"min-zoom=7",
				"line-width=7:1;13:2;14:3;15:3;16:6;17:8;18:10",
				"line-color=#FFE068",
				"border-style=solid",
				"border-color=#fd923a",
				"border-width=10%");
		this.setGroup(gMotorway_L, 
				WayType.MOTORWAY_L,
				WayType.MOTORWAY_B_L,
				WayType.MOTORWAY_TU_L,
				WayType.FASTLANE_L,
				WayType.FASTLANE_B_L,
				WayType.FASTLANE_TU_L);
		
		Group gMotorway_TU = new Group("Motorway Tunnel");
		this.setPropertiesForGroup(gMotorway_TU, 
				"zindex=9",
				"min-zoom=7",
				"line-width=7:1.25;13:2.5;14:3.5;15:4;16:8;17:10;18:12",
				"line-color=#FFC345",
				"line-style=transparent",
				"border-style=dot",
				"border-color=#fd923a",
				"border-width=2");
		this.setGroup(gMotorway_TU, 
				WayType.MOTORWAY_TU,
				WayType.FASTLANE_TU);
		
		Group gMotorway_B = new Group("Motorway Bridge");
		this.setPropertiesForGroup(gMotorway_B, 
				"zindex=29",
				"min-zoom=7",
				"line-width=7:1.25;13:2.5;14:3.5;15:4;16:8;17:10;18:12",
				"line-color=#FFC345",
				"border-style=solid",
				"border-color=#fd923a",
				"border-width=40%");
		this.setGroup(gMotorway_B, 
				WayType.MOTORWAY_B,
				WayType.FASTLANE_B);
		
		/*
		 * Major Road
		 */
		Group gMajorRoad_T = new Group("Major Road trafficable");
		this.setPropertiesForGroup(gMajorRoad_T,
				"zindex=18",
				"min-zoom=8",
				"line-color=#fffd8b",
				"line-width=10:1;13:2;14:3;15:4;16:8;17:10;18:12",
				"border-style=solid",
				"border-color=#999933",
				"border-width=5%");
		this.setGroup(gMajorRoad_T,
				WayType.TERTIARY_T,
				WayType.TERTIARYCYCLE_T,
				WayType.PRIMARYCYCLE_T,
				WayType.SECONDARY_T,
				WayType.SECONDARYCYCLE_T);
		//TODO untrafficable ways different
		this.setGroup(gMajorRoad_T,
				WayType.TERTIARY_NT,
				WayType.TERTIARYCYCLE_NT,
				WayType.PRIMARY,
				WayType.PRIMARYCYCLE_NT,
				WayType.SECONDARY_NT,
				WayType.SECONDARYCYCLE_NT);
		
		
		Group gMajorRoad_T_TU = new Group("Major Road trafficable tunnel");
		this.setPropertiesForGroup(gMajorRoad_T_TU,
				"zindex=8",
				"min-zoom=8",
				"line-color=#fffd8b",
				"line-width=10:1;13:2;14:3;15:4;16:8;17:10;18:12",
				"line-style=transparent",
				"border-style=dot",
				"border-color=#999933",
				"border-width=1");
		this.setGroup(gMajorRoad_T_TU,
				WayType.TERTIARY_TU_T,
				WayType.TERTIARYCYCLE_TU_T,
				WayType.PRIMARYCYCLE_TU_T,
				WayType.SECONDARY_TU_T,
				WayType.SECONDARYCYCLE_TU_T);
		//TODO untrafficable ways different
		this.setGroup(gMajorRoad_T_TU,
				WayType.TERTIARY_TU_NT,
				WayType.TERTIARYCYCLE_TU_NT,
				WayType.PRIMARY_TU,
				WayType.PRIMARYCYCLE_TU_NT,
				WayType.SECONDARY_TU_NT,
				WayType.SECONDARYCYCLE_TU_NT);
		
		Group gMajorRoad_T_B = new Group("Major Road trafficable Bridge");
		this.setPropertiesForGroup(gMajorRoad_T_B,
				"zindex=28",
				"min-zoom=8",
				"line-color=#fffd8b",
				"line-width=10:1;13:2;14:3;15:4;16:8;17:10;18:12",
				"border-style=solid",
				"border-color=#999933",
				"border-width=30%");
		this.setGroup(gMajorRoad_T_B,
				WayType.TERTIARY_B_T,
				WayType.TERTIARYCYCLE_B_T,
				WayType.PRIMARYCYCLE_B_T,
				WayType.SECONDARY_B_T,
				WayType.SECONDARYCYCLE_B_T);
		//TODO untrafficable ways different
		this.setGroup(gMajorRoad_T_B,
				WayType.TERTIARY_B_NT,
				WayType.TERTIARYCYCLE_B_NT,
				WayType.PRIMARY_B,
				WayType.PRIMARYCYCLE_B_NT,
				WayType.SECONDARY_B_NT,
				WayType.SECONDARYCYCLE_B_NT);
		
		
		
		
		/*
		 * Minor Road
		 */
		Group gMinorRoad_T = new Group("Minor Road trafficable");
		this.setPropertiesForGroup(gMinorRoad_T, 
				"zindex=17",
				"min-zoom=12", 
				"line-color=white", 
				"line-width=13:1.5;14:2.5;15:4;16:8;17:9.5;18:11",
				"border-style=solid",
				"border-color=#D4CCB8",
				"border-width=5%");
		this.setGroup(gMinorRoad_T, 
				WayType.CITYANDCYCLE_T,
				WayType.INCITY_T,
				WayType.SERVICE_T,
				WayType.ROUNDABOUT_T,
				WayType.SERVICE_T,
				WayType.UNKNOWN_T);
		//TODO untrafficable ways different	
		this.setGroup(gMinorRoad_T, 
				WayType.CITYANDCYCLE_NT,
				WayType.INCITY_NT,
				WayType.SERVICE_NT,
				WayType.ROUNDABOUT_NT,
				WayType.SERVICE_NT,
				WayType.UNKNOWN_NT);
		
		
		Group gMinorRoad_TU_T = new Group("Minor Road tunnel trafficable");
		this.setPropertiesForGroup(gMinorRoad_TU_T, 
				"zindex=7",
				"min-zoom=12", 
				"line-color=white", 
				"line-style=transparent",
				"line-width=13:1.5;14:2.5;15:4;16:8;17:9.5;18:11",
				"border-style=dot",
				"border-color=#D4CCB8",
				"border-width=1");
		this.setGroup(gMinorRoad_TU_T, 
				WayType.CITYANDCYCLE_TU_T,
				WayType.INCITY_TU_T,
				WayType.SERVICE_TU_T,
				WayType.SERVICE_TU_T,
				WayType.UNKNOWN_TU_T);
		//TODO untrafficable ways different	
		this.setGroup(gMinorRoad_TU_T, 
				WayType.CITYANDCYCLE_TU_NT,
				WayType.INCITY_TU_NT,
				WayType.SERVICE_TU_NT,
				WayType.SERVICE_TU_NT,
				WayType.UNKNOWN_TU_NT);
		
		Group gMinorRoad_B_T = new Group("Minor Road Bridge trafficable");
		this.setPropertiesForGroup(gMinorRoad_B_T, 
				"zindex=27",
				"min-zoom=12", 
				"line-color=white", 
				"line-width=13:1.5;14:2.5;15:4;16:8;17:9.5;18:11",
				"border-style=solid",
				"border-color=#D4CCB8",
				"border-width=30%");
		this.setGroup(gMinorRoad_B_T, 
				WayType.CITYANDCYCLE_B_T,
				WayType.INCITY_B_T,
				WayType.SERVICE_B_T,
				WayType.SERVICE_B_T,
				WayType.UNKNOWN_B_T);
		//TODO untrafficable ways different	
		this.setGroup(gMinorRoad_T, 
				WayType.CITYANDCYCLE_B_NT,
				WayType.INCITY_B_NT,
				WayType.SERVICE_B_NT,
				WayType.SERVICE_B_NT,
				WayType.UNKNOWN_B_NT);
		
		
		
		/*
		 * Path 
		 */
		Group gCicle = new Group("Cicle trafficable");
		this.setPropertiesForGroup(gCicle, 
				"zindex=20",
				"min-zoom=13",
				"line-color=white",  
				"line-width=13:1.5;14:2.5;15:3",
				"border-style=solid",
				"border-width=5%",
				"border-color=#D4CCB8");
		this.setGroup(gCicle,
				WayType.CYCLEWAY_T,
				WayType.FOOTANDCYCLE_T);
		//TODO untrafficable ways different
		this.setGroup(gCicle,
				WayType.CYCLEWAY_NT,
				WayType.FOOTANDCYCLE_NT);
		
		
		Group gPath_T = new Group("Path trafficable");
		this.setPropertiesForGroup(gPath_T, 
				"zindex=16",
				"min-zoom=13",
				"line-color=white",  
				"line-width=13:1;14:1;15:2",
				"border-style=solid",
				"border-width=5%",
				"border-color=#D4CCB8");
		this.setGroup(gPath_T,
				WayType.DIRTROAD_T,
				WayType.PATH_T);
		//TODO untrafficable ways different
		this.setGroup(gPath_T,
				WayType.DIRTROAD_NT,
				WayType.FOOTWAY,
				WayType.PATH_NT,
				WayType.STEPS);
		
		Group gDirt_T = new Group("Dirtroad trafficable");
		this.setPropertiesForGroup(gDirt_T, 
				"zindex=15",
				"min-zoom=13",
				"line-color=#CDB38B",  
				"line-width=13:1;14:1;15:2",
				"border-style=solid",
				"border-width=5%",
				"border-color=#D4CCB8");
		this.setGroup(gDirt_T, WayType.DIRTROAD_T);
		// TODO untrafficable ways different
		this.setGroup(gDirt_T, WayType.DIRTROAD_NT);
		
		Group gPath_TU_T = new Group("Path Tunnel trafficable");
		this.setPropertiesForGroup(gPath_TU_T, 
				"zindex=6",
				"min-zoom=13",
				"line-color=white",  
				"line-width=13:1;14:1;15:2",
				"line-style=transparent",
				"border-style=dot",
				"border-width=1.5",
				"border-color=#D4CCB8");
		this.setGroup(gPath_TU_T,
				WayType.CYCLEWAY_TU_T,
				WayType.FOOTANDCYCLE_TU_T);
		//TODO untrafficable ways different
		this.setGroup(gPath_TU_T,
				WayType.CYCLEWAY_TU_NT,
				WayType.FOOTANDCYCLE_TU_NT,
				WayType.FOOTWAY_TU);
		
		Group gPath_B_T = new Group("Path Bridge trafficable");
		this.setPropertiesForGroup(gPath_B_T, 
				"zindex=26",
				"min-zoom=13",
				"line-color=white",  
				"line-width=13:1;14:1;15:2",
				"border-style=solid",
				"border-width=40%",
				"border-color=#D4CCB8");
		this.setGroup(gPath_B_T,
				WayType.CYCLEWAY_B_T,
				WayType.FOOTANDCYCLE_B_T);
		//TODO untrafficable ways different
		this.setGroup(gPath_B_T,
				WayType.CYCLEWAY_B_NT,
				WayType.FOOTANDCYCLE_B_NT,
				WayType.FOOTWAY_B);
		
		
		
		/*
		 * Steps
		 */
		Group gSteps = new Group("Steps");
		this.setPropertiesForGroup(gSteps, 
				"zindex=16",
				"min-zoom=13",
				"line-color=#D4CCB8",  
				"line-width=13:2;14:2;15:4",
				"line-style=h_dash",
				"border-style=solid",
				"border-width=0",
				"border-color=white");
		this.setGroup(gSteps, WayType.STEPS);
		
		/*
		 * Railway
		 */
		Group gRailway = new Group("Railway");
		this.setPropertiesForGroup(gRailway, 
				"zindex=19",
				"min-zoom=6",
				"line-color=white",
				"line-width=6:0.01;13:2",
				"line-style=long-dash",
				"border-style=solid",
				"border-color=#a1a1a1",
				"border-width=0.5"); 
		this.setGroup(gRailway, 
				WayType.RAILWAY);
		
		Group gRailway_B = new Group("Railway Bridge");
		this.setPropertiesForGroup(gRailway_B, 
				"zindex=29",
				"min-zoom=6",
				"line-color=white",
				"line-width=6:0.01;13:2",
				"line-style=long-dash",
				"border-style=solid",
				"border-color=#a1a1a1",
				"border-width=1"); 
		this.setGroup(gRailway_B, 
				WayType.RAILWAY_B);
		
		Group gRailway_TU = new Group("Railway Tunnel");
		this.setPropertiesForGroup(gRailway_TU, 
				"zindex=9",
				"min-zoom=6",
				"line-color=white",
				"line-width=6:0.01;13:2",
				"line-style=transparent",
				"border-style=dash",
				"border-color=#a1a1a1",
				"border-width=1"); 
		this.setGroup(gRailway_TU, 
				WayType.RAILWAY_TU);
				
		
		Group gSquare_T = new Group("Square trafficable");
		this.setPropertiesForGroup(gSquare_T, 
				"min-zoom=20",
				"line-color=#D8D4D1",
				"line-width=10:1",
				"border-style=none");
		this.setGroup(gSquare_T, WayType.SQUARE_T);
		
		
		/*
		 *	River
		 */
		Group gRiver = new Group("River");
		this.setPropertiesForGroup(gRiver,
				"zindex=10",
				"min-zoom=12",
				"line-color=#99B3CC",
				"line-width=14:1;16:5;");
		this.setGroup(gRiver, WayType.RIVER);
		
		Group gStream = new Group("Stream");
		this.setPropertiesForGroup(gStream,
				"zindex=10",
				"min-zoom=16",
				"line-color=#99B3CC",
				"line-width=16:2;");
		this.setGroup(gStream, WayType.STREAM);
		
		
		
		/*
		 * Borders
		 */
		Group gState = new Group("State Border");
		this.setPropertiesForGroup(gState, 
				"zindex=30",
				"min-zoom=8",
				"line-color=red",
				"line-width=8:5;13:10;16:15",
				"line-style=transparent",
				"border-style=none");
		//this.setGroup(gState, WayType.STATE_BORDER);
		
		Group gCountry = new Group("Country Border");
		this.setPropertiesForGroup(gCountry, 
				"zindex=30",
				"min-zoom=6",
				"line-color=red",
				"line-width=6:5;8:10;13:15;16:21",
				"line-style=transparent",
				"border-style=none");
		//this.setGroup(gCountry, WayType.COUNTRY_BORDER);
	
		
		
		
		/*
		 *	AREAS 
		 */
		
		
		Group gBuilding = new Group("Building");
		this.setPropertiesForGroup(gBuilding,
				"zindex=7",
				"min-zoom=15",
				"fill-Color=#EBE6DC",
				"border-style=solid",
				"border-color=#D8D4D1",
				"border-width=1");
		this.setGroup(gBuilding, AreaType.BUILDING);
		
		
		Group gSquare = new Group("Square");
		this.setPropertiesForGroup(gSquare, 
				"zindex=4",
				"min-zoom=10",
				"fill-Color=#d1d0cd",
				"border-style=solid",
				"border-color=#D8D4D1",
				"border-width=1");
		this.setGroup(gSquare, AreaType.SQUARE);
		
		
		Group gForest = new Group("Forest");
		this.setPropertiesForGroup(gForest,
				"zindex=4",
				"min-zoom=9", 
				"fill-Color=#CBD8C3");
		this.setGroup(gForest, AreaType.FOREST);
		
	
		
		
		Group gGrass = new Group("Grass");
		this.setPropertiesForGroup(gGrass,
				"zindex=3",
				"min-zoom=9",
				"fill-Color=#b5d29c");
		this.setGroup(gGrass, 
				AreaType.GRASS,
				AreaType.ISLAND);
		
		Group gField = new Group("Field");
		this.setPropertiesForGroup(gField, 
				"zindex=2",
				"min-zoom=9",
		// TODO better coloring
				"fill-Color=#EEDC82");
		this.setGroup(gField, AreaType.FIELD);
		
		Group gSand = new Group("Sand");
		this.setPropertiesForGroup(gSand, 
				"zindex=2",
				"min-zoom=9",
		// TODO better coloring
				"fill-Color=#EEEE00");
		this.setGroup(gSand, AreaType.SAND);
		
		
		Group gCity = new Group("City");
		this.setPropertiesForGroup(gCity,
				"zindex=1", 
				"min-zoom=9",
				"fill-Color=#BFBFBF");
		this.setGroup(gCity, AreaType.CITY);
		
		
		Group gWater = new Group("Water");
		this.setPropertiesForGroup(gWater,
				"zindex=6",
				"min-zoom=9", 
				"fill-Color=#99B3CC");
		this.setGroup(gWater, 
				AreaType.WATER,
				AreaType.RIVERBANK);
		
		
		Group gUncassifiedA = new Group("Unclassified Areas");
		this.setPropertiesForGroup(gUncassifiedA,
				"zindex=5",
				"min-zoom=10",
				"fill-Color=#FF4040",
				"border-style=solid",
				"border-color=#D8D4D1",
				"border-width=1");
		this.setGroup(gUncassifiedA, AreaType.UNCLASSIFIED);
		
		
		/*
		 * Citys
		 */
		Group gVillage = new Group("Village");
		this.setPropertiesForGroup(gVillage,
				"min-zoom=14",
				"font-size=12:10;20:20",
				"font-style=plain",
				"font-name=Arial",
				"font-color=black");
		this.setGroup(gVillage, CityType.VILLAGE);
		
		Group gSuburb = new Group("Suburb");
		this.setPropertiesForGroup(gSuburb,
				"min-zoom=15",
				"font-size=13:10;20:20",
				"font-style=plain",
				"font-name=Arial",
				"font-color=black");
		this.setGroup(gSuburb, CityType.SUBRUB);
		
		Group gTown = new Group("Town");
		this.setPropertiesForGroup(gTown,
				"min-zoom=11",
				"max-zoom=16",
				"font-size=8:10;11:15;20:18",
				"font-style=plain",
				"font-name=Arial",
				"font-color=black");
		this.setGroup(gTown, CityType.TOWN);
		
		Group gCityC = new Group("City citytype");
		this.setPropertiesForGroup(gCityC,
				"min-zoom=6",
				"max-zoom=16",
				"font-size=8:14;11:17;14:18",
				"font-style=plain",
				"font-name=Arial",
				"font-color=black");
		this.setGroup(gCityC, CityType.CITY);
		
		Group gStateC = new Group("State citytype");
		this.setPropertiesForGroup(gStateC,
				"min-zoom=6",
				"max-zoom=10",
				"font-size=6:14;8:17;10:18",
				"font-style=bold",
				"font-name=Arial",
				"font-color=black");
		this.setGroup(gStateC, CityType.STATE);
	}	
}
