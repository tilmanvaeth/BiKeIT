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

package misc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Stopwatch {

	private static long time;
	
	public static void start() {
		time = System.currentTimeMillis();
	}

	public static long stop() {
		return System.currentTimeMillis() - time;
	}
	
	public static void stopAndPrint() {
		long diff = System.currentTimeMillis() - time;
		String s = formatTime(diff);
		System.out.println("Stopwatch: "+s);
	}
	
	private static String formatTime(long milliseconds) {
		
		/* compute time string */
		SimpleDateFormat sdf;
		
		if (milliseconds<1000) {
			sdf = new SimpleDateFormat("S'ms'");
		} else if (milliseconds<60*1000) {
			sdf = new SimpleDateFormat("s's'S'ms'");
		} else if (milliseconds<60*60*1000) {
			sdf = new SimpleDateFormat("m'm's's'S'ms'");
		} else {
			sdf = new SimpleDateFormat("H'h'm'm's's'S'ms'");
		}
		
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.clear();
		cal.set(Calendar.MILLISECOND, (int) milliseconds);
		return sdf.format(cal.getTime());
		/* --- */
		
	}
	
}
