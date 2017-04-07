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

package dataTypes;


public enum WayType{
	CYCLEWAY_T, CYCLEWAY_NT, FOOTWAY, MOTORWAY, MOTORWAY_L, INCITY_T, INCITY_NT, FASTLANE, FASTLANE_L, TERTIARY_T, TERTIARY_NT,
	RIVER, STREAM, RAILWAY, DIRTROAD_T, DIRTROAD_NT, CITYANDCYCLE_T, CITYANDCYCLE_NT, TERTIARYCYCLE_T,
	TERTIARYCYCLE_NT, PATH_T, PATH_NT, UNKNOWN_T, UNKNOWN_NT, PRIMARY, SECONDARY_T, SECONDARY_NT, PRIMARYCYCLE_T,
	PRIMARYCYCLE_NT, SECONDARYCYCLE_T, SECONDARYCYCLE_NT, ROUNDABOUT_T, ROUNDABOUT_NT, SERVICE_T,
	SERVICE_NT, FOOTANDCYCLE_T, FOOTANDCYCLE_NT, STEPS,
	
	CYCLEWAY_B_T, CYCLEWAY_B_NT, FOOTWAY_B, MOTORWAY_B, MOTORWAY_B_L, INCITY_B_T, INCITY_B_NT, FASTLANE_B, FASTLANE_B_L, TERTIARY_B_T, TERTIARY_B_NT,
	RAILWAY_B, CITYANDCYCLE_B_T, CITYANDCYCLE_B_NT, TERTIARYCYCLE_B_T,
	TERTIARYCYCLE_B_NT, UNKNOWN_B_T, UNKNOWN_B_NT, PRIMARY_B, SECONDARY_B_T, SECONDARY_B_NT, PRIMARYCYCLE_B_T,
	PRIMARYCYCLE_B_NT, SECONDARYCYCLE_B_T, SECONDARYCYCLE_B_NT, SERVICE_B_T,
	SERVICE_B_NT, FOOTANDCYCLE_B_T, FOOTANDCYCLE_B_NT,
	
	CYCLEWAY_TU_T, CYCLEWAY_TU_NT, FOOTWAY_TU, MOTORWAY_TU_L, MOTORWAY_TU, INCITY_TU_T, INCITY_TU_NT, FASTLANE_TU, FASTLANE_TU_L,
	TERTIARY_TU_T, TERTIARY_TU_NT, RAILWAY_TU, CITYANDCYCLE_TU_T, CITYANDCYCLE_TU_NT, TERTIARYCYCLE_TU_T,
	TERTIARYCYCLE_TU_NT, UNKNOWN_TU_T, UNKNOWN_TU_NT, PRIMARY_TU, SECONDARY_TU_T, SECONDARY_TU_NT, PRIMARYCYCLE_TU_T,
	PRIMARYCYCLE_TU_NT, SECONDARYCYCLE_TU_T, SECONDARYCYCLE_TU_NT, SERVICE_TU_T,
	SERVICE_TU_NT, FOOTANDCYCLE_TU_T, FOOTANDCYCLE_TU_NT,
	
	BRIDGE, TUNNEL,
	
	SQUARE_T,
	
	STATE_BORDER,
	
	COUNTRY_BORDER;
	
	
	
	
	
	
	public boolean isTrafficable() {
		if (name().endsWith("_T")) 
			return true;
		else 
			return false;
	}
	
	public boolean isTunnel() {
		return (name().contains("_TU"));
	}
	
	public boolean isBridge() {
		return name().contains("_B");
	}
}
