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

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;


public class TunnelStroke implements Stroke {
	
	private Stroke outsideStroke;
	private Stroke insideStroke;
	
	public TunnelStroke(float lineWidth, float borderWidth, float[] pattern) {
		insideStroke = new BasicStroke(lineWidth-borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		outsideStroke = new BasicStroke(borderWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
				10.f, pattern, lineWidth);
	}
	
	@Override
	public Shape createStrokedShape(Shape arg0) {
		return outsideStroke.createStrokedShape(insideStroke.createStrokedShape(arg0));
	}
	
}
