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

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import mapModel.Area;
import mapModel.City;
import mapModel.Way;
import dataTypes.AreaType;
import dataTypes.CityType;
import dataTypes.WayType;


public abstract class AbstractRenderStyle implements RenderStyle {

	
	private Map<WayType, Group> wayMap;
	private Map<AreaType, Group> areaMap;
	private Map<CityType, Group> cityMap;
	private Map<Group, Properties> props;
	
	private Color backgroundColor;
	
	public AbstractRenderStyle() {
		wayMap = new HashMap<WayType, Group>();
		areaMap = new HashMap<AreaType, Group>();
		cityMap = new HashMap<CityType, Group>();
		props = new HashMap<Group, Properties>();
		
		backgroundColor = new Color(233,233,233);
		
		this.initProperties();
	}
	
	protected abstract void initProperties();
	
	public abstract int getMaxWayZIndex();
	
	public abstract int getMaxAreaZIndex();
	
	protected void setBackgroundColor(String colorString) {
		this.backgroundColor = getColor(colorString);;
	}
	
	protected void setPropertiesForGroup(Group g, String... props) {
		if (this.props.get(g) == null) {
			this.props.put(g, new Properties());
		}
		for (String prop : props) {
			String[] kv = prop.split("=");
			this.props.get(g).setProperty(kv[0].toLowerCase(Locale.ENGLISH), kv[1].toLowerCase(Locale.ENGLISH));
		}
	}
	
	
	protected void setGroup(Group g, WayType... types) {
		for (WayType type : types) {
			wayMap.put(type, g);
		}
	}
	
	protected void setGroup(Group g, CityType... types) {
		for (CityType type : types) {
			cityMap.put(type, g);
		}
	}
	
	protected void setGroup(Group g, AreaType... types) {
		for (AreaType type : types) {
			areaMap.put(type, g);
		}
	}
	
	
	public Color getBackgroundColor() {
		return this.backgroundColor;
	}
	
	public boolean isVisible(Way way, int zoomlevel) {
		Properties props = this.getProps(way);
		if (props == null) {
			return false;
		}
		
		String minLevel = props.getProperty("min-zoom", "0");
		String maxLevel = props.getProperty("max-zoom", "100");
		
		int minZoom = Integer.valueOf(minLevel);
		int maxZoom = Integer.valueOf(maxLevel);
		
		return zoomlevel >= minZoom && zoomlevel <= maxZoom;
	}
	
	public boolean isVisible(Area area, int zoomlevel) {
		Properties props = this.getProps(area);
		if (props == null) {
			return false;
		}
		
		String minLevel = props.getProperty("min-zoom", "0");
		String maxLevel = props.getProperty("max-zoom", "100");
		
		int minZoom = Integer.valueOf(minLevel);
		int maxZoom = Integer.valueOf(maxLevel);
		
		return zoomlevel >= minZoom && zoomlevel <= maxZoom;
	}
	
	public Color getLineColor(Way way) {
		Properties props = this.getProps(way);
		if (props == null) {
			return Color.white;
		}
		
		String colorString = props.getProperty("line-color", "white");
		return getColor(colorString);
	}
	
	
	public Color getBorderColor(Way way) {
		Properties props = this.getProps(way);
		if (props == null) {
			return Color.white;
		}
		
		String colorString = props.getProperty("border-color", "black");
		return getColor(colorString);
	}
	
	public Color getBorderColor(Area area) {
		Properties props = this.getProps(area);
		if (props == null) {
			return Color.white;
		}
		
		String colorString = props.getProperty("border-color", "black");
		return getColor(colorString);
	}
	
	
	public boolean hasBorder(Way way) {
		Properties props = this.getProps(way);
		if (props == null) {
			return false;
		}
		
		return !props.getProperty("border-style", "none").equals("none");
	}
	
	public boolean hasBorder(Area area) {
		Properties props = this.getProps(area);
		if (props == null) {
			return false;
		}
		
		return !props.getProperty("border-style", "none").equals("none");
	}
	
	public float getLineWidth(Way way, int zoomlevel) {
		Properties props = this.getProps(way);
		if (props == null) {
			return 0f;
		}
		
		float width = 0f;
		String widthString = props.getProperty("line-width", "12:1");
		String[] widthStrings = widthString.split(";");
		int[] keys = new int[widthStrings.length];
		float[] values = new float[widthStrings.length];
		for (int i = 0; i < widthStrings.length; i++) {
			String[] kv = widthStrings[i].split(":");
			keys[i] = Integer.parseInt(kv[0]);
			values[i] = Float.valueOf(kv[1]);
			if (zoomlevel >= keys[i]) {
				width = values[i];
			}
		}
		return width;
	}
	
	public float getTotalBorderWidth(Way way, int zoomlevel) {
		Properties props = this.getProps(way);
		if (props == null) {
			return 0f;
		}
		
		String widthString = props.getProperty("border-width", "10%");
		if (widthString.contains("%")) {
			float factor = 1f + 0.02f * Float.valueOf(widthString.replaceAll("%", ""));
			return factor * this.getLineWidth(way, zoomlevel);
		}
		
		float borderWidth = Float.valueOf(widthString);
		float lineWidth = this.getLineWidth(way, zoomlevel);
		return lineWidth == 0f ? borderWidth : 2*borderWidth + lineWidth;
	}
	
	public float getBorderWidth(Way way, int zoomlevel) {
		Properties props = this.getProps(way);
		if (props == null) {
			return 0f;
		}
		
		String widthString = props.getProperty("border-width", "10%");
		if (widthString.contains("%")) {
			float factor = 1f + 0.02f * Float.valueOf(widthString.replaceAll("%", ""));
			return factor * this.getLineWidth(way, zoomlevel);
		}
		
		float borderWidth = Float.valueOf(widthString);
		return borderWidth;
	}
	
	public float getBorderWidth(Area area) {
		Properties props = this.getProps(area);
		if (props == null) {
			return 0f;
		}
		
		String widthString = props.getProperty("border-width", "1");
		
		return Float.valueOf(widthString);
	}
	
	
	
	public Color getFillColor(Area area) {
		Properties props = this.getProps(area);
		if (props == null) {
			return Color.white;
		}
		
		String colorString = props.getProperty("fill-color", "gray");
		return getColor(colorString);
		
	}
	
	private Color getColor(String colorString) {
		if (colorString.startsWith("#")) {
			return Color.decode(colorString);
		}
		else {
			try {
				return (Color)Class.forName("java.awt.Color").getField(colorString).get(null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return Color.red;
			}
		}
	}
	
	@Override
	public int getZIndex(Way way) {
		Properties prop = this.getProps(way);
		
		if (prop == null)
			return 0;
		
		return Integer.parseInt(prop.getProperty("zindex", "1"));
	}
	
	@Override
	public int getZIndex(Area area) {
		Properties prop = this.getProps(area);
		
		if (prop == null)
			return 0;
		
		return Integer.parseInt(prop.getProperty("zindex", "1"));
	}

	

	@Override
	public boolean isLineDashed(Way way) {
		Properties props = this.getProps(way);
		if (props == null) {
			return false;
		}
		
		String lineStyle = props.getProperty("line-style", "solid");
		
		if (lineStyle.equals("solid") ||  lineStyle.equals("transparent")) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isBorderDashed(Way way) {
		Properties props = this.getProps(way);
		if (props == null) {
			return false;
		}
		
		String borderStyle = props.getProperty("border-style", "solid");
		if (borderStyle.equals("solid")) {
			return false;
		} 
		return true;
	}

	@Override
	public float[] getLineDashLenths(Way way, int zoomlevel) {

		Properties props = this.getProps(way);
		if (props == null) {
			return new float[]{0f};
		}
		
		String lineStyle = props.getProperty("line-style", "solid");
		
		if (lineStyle.equals("solid")) {
			return new float[]{0f};
		} else if (lineStyle.equals("dash")) {
			return new float[]{this.getLineWidth(way, zoomlevel) * 4};
		} else if (lineStyle.equals("long-dash")) {
			return new float[]{this.getLineWidth(way, zoomlevel) * 6};
		} else if (lineStyle.equals("h_dash")) {
			return new float[]{1f, 3f};
		} else {
			return new float[]{0f};
		}
	}

	@Override
	public float[] getBorderDashLenths(Way way, int zoomlevel) {
		Properties props = this.getProps(way);
		if (props == null) {
			return new float[]{1f};
		}
		
		String lineStyle = props.getProperty("border-style", "dash");
		
		if (lineStyle.equals("solid")) {
			return new float[]{1f};
		} else if (lineStyle.equals("dash")) {
			return new float[]{this.getLineWidth(way, zoomlevel) * 4};
		} else if (lineStyle.equals("long-dash")) {
			return new float[]{this.getLineWidth(way, zoomlevel) * 6};
		} else if (lineStyle.equals("short-dash")) {
				return new float[]{this.getLineWidth(way, zoomlevel) * 2};
		} else if (lineStyle.equals("dot")) {
			return new float[]{1f, 2f};
		} else {
			return new float[]{0f};
		}
	}
	
	@Override
	public boolean isTransparant(Way way) {
		Properties props = this.getProps(way);
		if (props == null) {
			return false;
		}
	
		String lineStyle = props.getProperty("line-style", "solid");
	
		if (lineStyle.equals("transparent")) {
			return true;
		}
		return false;
	}
	
	@Override
	public String getFontname(City city) {
		Properties props = this.getProps(city);
		if (props == null) {
			return "Arial";
		}
	
		String fontname = props.getProperty("font-name", "Arial");
		return fontname;
	}
	
	@Override
	public int getFontsize(City city, int zoomlevel) {
		Properties props = this.getProps(city);
		if (props == null) {
			return 12;
		}
		
		int size = 0;
		String widthString = props.getProperty("font-size", "12:12");
		String[] widthStrings = widthString.split(";");
		int[] keys = new int[widthStrings.length];
		int[] values = new int[widthStrings.length];
		for (int i = 0; i < widthStrings.length; i++) {
			String[] kv = widthStrings[i].split(":");
			keys[i] = Integer.parseInt(kv[0]);
			values[i] = Float.valueOf(kv[1]).intValue();
			if (zoomlevel >= keys[i]) {
				size = values[i];
			}
		}
		return size;
	}
	
	@Override
	public int getFontstyle(City city) {
		Properties props = this.getProps(city);
		if (props == null) {
			return Font.PLAIN;
		}
	
		String fontname = props.getProperty("font-style", "PLAIN").toUpperCase(Locale.ENGLISH);
		if (fontname.equals("PLAIN")) {
			return Font.PLAIN;
		} else if (fontname.equals("BOLD")) {
			return Font.BOLD;
		} else if (fontname.equals("ITALIC")) {
			return Font.ITALIC;
		} else if (fontname.equals("BOLDITALIC")) {
			return Font.BOLD;
		}
		return Font.PLAIN;
	}
	
	@Override
	public boolean isVisible(City city, int zoomlevel) {
		Properties props = this.getProps(city);
		if (props == null) {
			return false;
		}
		
		String minLevel = props.getProperty("min-zoom", "0");
		String maxLevel = props.getProperty("max-zoom", "100");
		
		int minZoom = Integer.valueOf(minLevel);
		int maxZoom = Integer.valueOf(maxLevel);
		
		return zoomlevel >= minZoom && zoomlevel <= maxZoom;
	}
	
	@Override
	public Color getFontcolor(City city) {
		Properties props = this.getProps(city);
		if (props == null) {
			return Color.black;
		}
		
		String colorString = props.getProperty("font-color", "black");
		return getColor(colorString);
	}
	
	
	protected class Group {
		String name;
		
		public Group(String name) {
			this.name = name;
		}
	}
	
	private Properties getProps(Way way) {
		Group g = wayMap.get(way.getType());
		
		if (g == null) 
			return null;
		
		return props.get(g);
	}
	
	private Properties getProps(Area area) {
		Group g = areaMap.get(area.getType());
		
		if (g == null) 
			return null;
		
		return props.get(g);
	}
	
	private Properties getProps(City city) {
		Group g = cityMap.get(city.getType());
		
		if (g == null) 
			return null;
		
		return props.get(g);
	}
}
