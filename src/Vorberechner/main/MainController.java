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

package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import dataTypes.AltitudeData;
import mapModel.MapModel;
import utilities.Logger;
import utilities.LoggerFactory;
import mapModelCreator.ArcFlagCalculator;
import mapModelCreator.MapModelBuilder;
import parser.ParserController;
import utilities.Exporter;

public class MainController {

	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	private static final String SEPERATOR = System.getProperty("line.separator");

	private static final String NOOSMFOUND = "Es wurden keine OSM-Daten gefunden." + SEPERATOR 
			+ "Bitte platzieren Sie OSM-Daten in den Arbeitsordner und starten Sie das Prorgamm erneut!";

	private static final String NOSRTMFOUND = "Es wurden keine SRTM-Daten im ArcInfo-Ascii-Format gefunden."
			+ SEPERATOR + "Bitte wählen sie eine andere Option!";
	private static final int STANDARDOPTION = 4;
	private static final String STANDARDDIR = System.getProperty("user.home") + System.getProperty("file.separator") + "BiKeIT";
	private static final String STARTMESSAGE = 
			"Dieses Programm dient zur Vorberechnung und Strukturierung von Rohdaten."+ SEPERATOR +
			"Mit ihm werden die für BiKeIT benötigten Daten erzeugt.";
	private static final String MULTIPLEOSM = "Es wurden mehrere OSM-Datensätze gefunden, welcher soll benutzt werden?";
	private static final String CHOOSEOSM = "OSM-Datensatz: ";
	private static final String DIRMESSAGE = "Bitte geben sie den Ordner, mit dem gearbeitet werden soll.";
	private static final String OPTIONS = "Bitte wählen sie eine der folgenden Optionen.";
	private static final String NEWDIR1 = "Es wurde ein neuer Ordner ";
	private static final String NEWDIR2 = " erzeugt, bitte legen sie die OSM- und SRTM-Daten in diesem ab.";
	private static final String ANYKEY = "Drücken Sie eine beliebige Taste, um fortzufahren.";
	private static final String OPTIONONE = 
			"1 - Es werden Kartendaten mit Arc-Flags und mit Höhendaten erzeugt.";
	private static final String OPTIONFOUR =
			"4 - Es werden Kartendaten ohne Arc-Flags und ohne Höhendaten erzeugt.";
	private static final String OPTIONTHREE = 
			"3 - Es werden Kartendaten ohne Arc-Flags, aber mit Höhendaten erzeugt";
	private static final String OPTIONTWO = 
			"2 - Es werden Kartendaten mit Arc-Flags, aber ohne Höhendaten erzeugt";
	private static final String QUIT = "0 - Beenden.";
	private static final String OPTIONCHOOSE = "Wählen sie bitte eine Option (Standard: " + STANDARDOPTION + "): ";
	private static final String DIRCHOOSE = "Pfad zum Ordner (Standard: "+ STANDARDDIR + "): ";
	private static final String FALSEINPUT = "Fehlerhafte Eingabe, bitte geben Sie ihre Wahl erneut ein!";
	
	private static String suffix = "";
	
	//Exceptionhandling and Idiothandling needed
	public static void main(String args[]) {
		File dir;
		File[] osmData;
		File[] srtmData;
		String dirPath = "";
		String osmPath = "";
		String outputPath;
		MapModel mapModel = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println(STARTMESSAGE + SEPERATOR);
		System.out.println(DIRMESSAGE);
		System.out.println(DIRCHOOSE);
		dir = dirChoose(reader);
		dirPath = dir.toString();
		osmData = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File f) {
				return f.toString().endsWith("osm");
			}
		});
		
		osmPath = osmChoose(reader, osmData);
		
		srtmData = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File f) {
				return f.toString().endsWith("asc");
			}
		});
		
		System.out.println(OPTIONS + SEPERATOR);
		System.out.println(OPTIONONE);
		System.out.println(OPTIONTWO);
		System.out.println(OPTIONTHREE);
		System.out.println(OPTIONFOUR);
		System.out.println(QUIT);
		
		mapModel = modelChoose(reader, osmPath, srtmData);
		
		outputPath = dirPath + System.getProperty("file.separator") + (new File(osmPath)).getName().split("[.]")[0] + suffix;
		File output = new File(outputPath);
		if (!output.exists())
			output.mkdir();
		Exporter.saveMapModelToDir(output.toString(), mapModel);
		logger.stopAndLog("Finished preprocessing");
		logger.log("The output is located at " + output.toString());
	}
	
	private static File dirChoose(BufferedReader reader) {
		File dir = null;
		String dirPath = "";
		try {
			dirPath = reader.readLine();
		} catch (IOException e) {
			System.err.println("Critical error: " + SEPERATOR + e);
			System.exit(0);
		}
		if (dirPath.equals("")) 
			dirPath = STANDARDDIR;
		dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdir();
			System.out.println(NEWDIR1 + dirPath + NEWDIR2 + SEPERATOR);
			System.out.println(ANYKEY);
			try {
				System.in.read();
			} catch (IOException e) {
				System.err.println("Critical error: " + SEPERATOR + e);
				System.exit(0);
			}
		}
		return dir;
	}
	
	private static String osmChoose(BufferedReader reader, File[] osmData) {
		boolean wrongInput = false;
		String osmPath = "";
		if (osmData.length > 1) {
			System.out.println(MULTIPLEOSM + SEPERATOR);
			for (int i = 0; i < osmData.length; i++) {
				System.out.println((i + 1) + " - " + osmData[i].getName() + "  " + osmData[i].length()/1048576 + "MB");
			}
			do {
				wrongInput = false;
				System.out.print(CHOOSEOSM);
				String osm = "";
				try {
					osm = reader.readLine();
				} catch (IOException e) {
					System.err.println("Critical error: " + SEPERATOR + e);
					System.exit(0);
				}
				if (osm.matches("[0-9]+"))
				{
					int osmID = Integer.parseInt(osm) - 1;
					if (osmID <= osmData.length)
						osmPath = osmData[osmID].toString();
					else {
						System.out.println(FALSEINPUT);
						wrongInput = true;
					}
				} else {
					wrongInput = true;
					System.out.println(FALSEINPUT);
				}
			} while (wrongInput);
		} else if (osmData.length == 1) {
			osmPath = osmData[0].toString();
		} else {
			System.out.println(NOOSMFOUND);
			System.exit(0);
		}
		return osmPath;
	}
	
	private static MapModel modelChoose(BufferedReader reader, String osmPath, File[] srtmData) {
		int option;
		MapModel mapModel = null;
		ParserController pCtrl = new ParserController();
		ArcFlagCalculator arcFlagCalc = null;
		boolean wrongInput;
		do {
			wrongInput = false;
			System.out.print(OPTIONCHOOSE);
			String op = "";
			try {
				op = reader.readLine();
			} catch (IOException e) {
				System.err.println("Critical error: " + SEPERATOR + e);
				System.exit(0);
			}
			if (op.equals("")) 
				option = STANDARDOPTION;
			else {
				try {
				option = Integer.parseInt(op);
				} catch (NumberFormatException e) {
					option = 9;
					
				}
			}
			
			switch (option){
			case (0): System.exit(0); break;
			case (1): 
				if (srtmData.length != 0) {
					logger.startAndLog("Start reading.");
					pCtrl.parseOSMData(osmPath);
					pCtrl.parseSRTMData(srtmData);
					mapModel = buildModel(pCtrl, pCtrl.getAltitudeData());
					arcFlagCalc = new ArcFlagCalculator(pCtrl.getWays());
					arcFlagCalc.addArcFlagsToMapModel();
					suffix = "_ARC_ALT";
				} else {
					System.out.println(NOSRTMFOUND);
					wrongInput = true;
				}
				break;
			case (2): 
				logger.startAndLog("Start reading.");
				pCtrl.parseOSMData(osmPath);
				mapModel = buildModel(pCtrl, null);
				arcFlagCalc = new ArcFlagCalculator(pCtrl.getWays());
				arcFlagCalc.addArcFlagsToMapModel();
				suffix = "_ARC";
				break;
			case (3): 
				if (srtmData.length != 0) {
					logger.startAndLog("Start reading.");
					pCtrl.parseOSMData(osmPath);
					pCtrl.parseSRTMData(srtmData);
					mapModel = buildModel(pCtrl, pCtrl.getAltitudeData());
					suffix = "_ALT";
				} else {
					System.out.println(NOSRTMFOUND);
					wrongInput = true;
				}
				break;
			case (4):
				logger.startAndLog("Start reading.");
				pCtrl.parseOSMData(osmPath);
				mapModel = buildModel(pCtrl, null);
				break;
			default: 
				System.out.println(FALSEINPUT);
				wrongInput = true;
			}
		} while (wrongInput);
		return mapModel;
	}
	
	
	private static MapModel buildModel(ParserController pCtrl, AltitudeData[] srtm) {
		MapModelBuilder mapMBuilder = new MapModelBuilder();
		mapMBuilder.setNodes(pCtrl.getNodes());
		mapMBuilder.setCities(pCtrl.getCities());
		mapMBuilder.setWays(pCtrl.getWays());
		mapMBuilder.setAreas(pCtrl.getAreas());
		
		if (srtm != null) {
			mapMBuilder.setAltitudeData(srtm);
		}
		
		mapMBuilder.buildMapModelData();
		MapModel mapModel = mapMBuilder.getMapModel();
		mapMBuilder = null;
		return mapModel;
	}
	
}
