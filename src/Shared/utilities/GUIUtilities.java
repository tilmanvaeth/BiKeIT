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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class GUIUtilities {
	
	public static String formatDuration(int seconds) {
		
		/* compute time string */
		SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.clear();
		cal.set(Calendar.SECOND, seconds);
		return sdf.format(cal.getTime())+" h";
		/* --- */
		
	}
	
	public static String formatDistance(int meters) {
		
		float m = meters;
		
		if (meters > 10000) {
			m = meters/1000.f;
		}
		
		DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(Locale.GERMAN);
		df.setGroupingUsed(true);
		df.setMaximumFractionDigits(1);
		
		String result = df.format(m);
		
		if (meters > 10000) {
			result += " km";
		} else {
			result += " m";
		}
		
		return result;
		
	}
	
}
