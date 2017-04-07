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

public class Zoomlevel {

	public static final int ZOOMLEVEL_MIN = 8;
	public static final int ZOOMLEVEL_MAX = 18;
	private int value;

	public Zoomlevel(int value) {
		this.value = value;
	}
	
	public void setValue(int value) {
		if (value >= ZOOMLEVEL_MIN && value <= ZOOMLEVEL_MAX)
			this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void increase() {
		if (this.value < ZOOMLEVEL_MAX)
			this.value++;
	}
	
	public void decrease() {
		if (this.value > ZOOMLEVEL_MIN)
			this.value--;
	}
	
	@Override
	public String toString() {
		switch(value) {
		case 0: return "1:500 Mio.";
		case 1: return "1:250 Mio.";
		case 2: return "1:150 Mio.";
		case 3: return "1:70 Mio.";
		case 4: return "1:35 Mio.";
		case 5: return "1:15 Mio.";
		case 6: return "1:10 Mio.";
		case 7: return "1:4 Mio.";
		case 8: return "1:2 Mio.";
		case 9: return "1:1 Mio.";
		case 10: return "1:500.000";
		case 11: return "1:250.000";
		case 12: return "1:150.000";
		case 13: return "1:70.000";
		case 14: return "1:35.000";
		case 15: return "1:15.000";
		case 16: return "1:8.000";
		case 17: return "1:4.000";
		case 18: return "1:2000";
		case 19: return "1:1000";
		case 20: return "1:500";
		default: return "1:" + (int)(500*Math.pow(2, -value + 20));
		}
	}
	
}
