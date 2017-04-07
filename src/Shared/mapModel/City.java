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

import dataTypes.CityType;
import dataTypes.Coordinate;
import dataTypes.CoordinateRect;

public class City extends Element{

	private String name;
	private Coordinate position;
	private CityType type;
	
	public City(String name, Coordinate position) {
		this.name = name;
		this.position = position;
	}
	public void setName (String name) {
		this.name = name;
	}
	public void setType (CityType type) {
		this.type = type;
	}
	public CityType getType() {
		return type;
	}
	public String getName() {
		return name;
	}

	public Coordinate getPosition() {
		return position;
	}
	@Override
	public boolean isIntersect(CoordinateRect frame) {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean equals(Object o) {
		return (o instanceof City && ((City) o).getName().equals(getName()));
	}
	
	
}
