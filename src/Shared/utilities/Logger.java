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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Logger {

	private static final long UPDATE_INTERVAL = 5000;
	private static final long START_TIME = System.currentTimeMillis();
	
	private long lastTime;
	private long jobStartTime;
	private String lastMessage = "";
	
	public void log(String msg) {
		String currentTime = formatTime(System.currentTimeMillis()-START_TIME);
		System.out.println(currentTime + " [" + Thread.currentThread().getName()+"] "+msg);
	}
	
	public void updateLog(String msg) {
		long diff = System.currentTimeMillis() - lastTime;
		if (diff>UPDATE_INTERVAL && !msg.equals(lastMessage)) {
			lastTime = System.currentTimeMillis();
			lastMessage = msg;
			log(msg);
		}
	}
	
	public void startAndLog(String msg) {
		lastTime = System.currentTimeMillis();
		jobStartTime = lastTime;
		log(msg);
	}
	
	public void stopAndLog(String msg) {
		String diff = formatTime(System.currentTimeMillis() - jobStartTime);
		log(msg+" in "+diff+".");
	}
	
	private String formatTime(long milliseconds) {
		
		int seconds = (int) Math.round((milliseconds / 1000));
		
		/* compute time string */
		SimpleDateFormat sdf;
		
		if (seconds<60) {
			sdf = new SimpleDateFormat("s's'");
		} else if (seconds<60*60) {
			sdf = new SimpleDateFormat("m'm's's'");
		} else {
			sdf = new SimpleDateFormat("H'h'm'm's's'");
		}
		
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.clear();
		cal.set(Calendar.SECOND, seconds);
		return sdf.format(cal.getTime());
		/* --- */
		
	}
	
}
