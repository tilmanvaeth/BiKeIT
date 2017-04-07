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

package userIterface;
import guiControllers.CalculationEventListener;
import guiControllers.MainGUIController;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import main.Importer;
import runTimeData.CalculatedRoute;
import utilities.GUIUtilities;
import dataTypes.Zoomlevel;



public class MainGUI extends JFrame implements CalculationEventListener {

	private static final long serialVersionUID = 5765267086570271159L;
	
	/* map views */
	private MapView mapView;
	private AltitudeMapView altitudeMapView;
	
	/* route interaction */
	private SearchField searchField;
	private JButton bSearch;
	private JButton bReset;
	private JButton bDescription;
	
	/* map interaction */
	private JButton bUp;
	private JButton bDown;
	private JButton bLeft;
	private JButton bRight;
	private JSlider zoomSlider;
	
	/* menu */
	private JMenuBar menuBar;
	private JMenu mainMenu;
	private JMenu helpMenu;
	private JMenuItem miCity;
	private JMenuItem miSave;
	private JMenuItem miLoad;
	private JMenuItem miFullscreen;
	private JMenuItem miExit;
	private JMenuItem miAbout;
	
	/* status bar */
	private JLabel infoLabel;
	private JLabel zoomLevelLabel;
	
	/* context menu on map */
	private JPopupMenu contextMenu;
	private JPopupMenu waypointContextMenu;
	private JPopupMenu stateContextMenu; //points to the right contextmenu for actual state
	private JMenuItem miStart;
	private JMenuItem miTarget;
	private JMenuItem miAnotherTarget;
	private JMenuItem miRemove;
	
	/* non-visible */
	private MainGUIController controller;
	private boolean isFullscreen = false;
	private static final Rectangle DEFAULT_BOUNDS = new Rectangle(20, 40, 256*3, 256*2);
	private Point lastMousePosition;
	
	/* enumerations for states */
	private MouseState mouseState;
	private RouteState routeState;
	private static enum MouseState{DEFAULT, OVER_WAYPOINT, MAP_DRAGGING, WP_DRAGGING}
	private static enum RouteState{NO_WAYPOINT_SET, ONE_WAYPOINT_SET, MORE_WAYPOINTS_SET, ROUTE_CALCULATING, ROUTE_CALCULATED}
	

	private int waypointIndex;
	private int waypointMouseOffsetX; 
	private int waypointMouseOffsetY;
	
	public MainGUI() {
		
		super();
		controller = new MainGUIController(Importer.readMapModelFromDir());
		controller.addCalculationEventListener(this);
		
		setLookAndFeel();
		initObjects();
		init();
		packComponents();
		updateStatusBar();
		
		addMouseListener(new GUIMouseListener());
		
		// center frame
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
		int top = (int) (screenSize.height - getHeight())/2;
	    int left = (int) (screenSize.width - getWidth())/2; 
	    setLocation(left, top);
	    
	    try {
			setIconImage(ImageIO.read(this.getClass().getClassLoader().getResource("icon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    System.gc();
	    Importer.disposeSplash();
	  	setVisible(true);
	  	
	  	// take focus away from search field
	  	searchField.transferFocus();
	  	
	}
	
	
	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
	
	private void initObjects() {
		/* route interaction */
		bSearch = new JButton(new ImageIcon(this.getClass().getClassLoader().getResource("search.png")));
		bSearch.setPreferredSize(new Dimension(22,22));
		searchField = new SearchField("Ort suchen...");
		bReset = new JButton("Neue Route");
		bDescription = new JButton("Routenbeschreibung");
		
		/* map interaction */
		bUp = new JButton("↑");
		bDown = new JButton("↓");
		bLeft = new JButton("←");
		bRight = new JButton("→");
		zoomSlider = new JSlider(SwingConstants.VERTICAL,
				Zoomlevel.ZOOMLEVEL_MIN, Zoomlevel.ZOOMLEVEL_MAX, Zoomlevel.ZOOMLEVEL_MIN);
		
		/* menu */
		menuBar = new JMenuBar();
		mainMenu = new JMenu("BiKeIT");
		helpMenu = new JMenu("Über");
		miCity = new JMenuItem("Zu Ort springen", KeyEvent.VK_O);
		miSave = new JMenuItem("Route speichern", KeyEvent.VK_S);
		miLoad = new JMenuItem("Route laden", KeyEvent.VK_L);
		miFullscreen = new JMenuItem("Vollbildmodus", KeyEvent.VK_V);
		miExit = new JMenuItem("Beenden", KeyEvent.VK_B);
		miAbout = new JMenuItem("Über BiKeIT", KeyEvent.VK_U);
		
		/* status bar */
		infoLabel = new JLabel();
		zoomLevelLabel = new JLabel();
		
		/* context menu on map */
		contextMenu = new JPopupMenu();
		waypointContextMenu = new JPopupMenu();
		miStart = new JMenuItem("Startpunkt hier setzen");
		miTarget = new JMenuItem("Zielpunkt hier setzen");
		miAnotherTarget = new JMenuItem("Weiteren Zielpunkt hier setzen");
		miRemove = new JMenuItem("Wegpunkt löschen");
	}
	
	private void init()	{
		this.changeRouteState(RouteState.NO_WAYPOINT_SET);
		mouseState = MouseState.DEFAULT;
		
	  	mapView = controller.getMapView();
	  	mapView.addMouseMotionListener(new MapMouseMotionListener());
	  	mapView.addMouseListener(new MapMouseListener());
	  	mapView.addComponentListener(new MapComponentListener());
	  	mapView.addMouseWheelListener(new MapMouseWheelListener());
	  	mapView.addMouseListener(new GUIMouseListener());
	  	
	  	altitudeMapView = controller.getAltitudeMapView();
	  	altitudeMapView.addComponentListener(new AltitudeMapComponentListener());
	  	
	  	// init zoom slider
	  	zoomSlider.setSnapToTicks(true);
	  	zoomSlider.setPaintTrack(true);
	  	zoomSlider.setPaintTicks(false);
	  	zoomSlider.setPaintLabels(false);
	  	zoomSlider.setMajorTickSpacing(1);
	  	zoomSlider.setFocusable(false);
	  	zoomSlider.addChangeListener(new ZoomSliderChangeListener());
	  	
	  	// init menu
	  	menuBar.add(mainMenu);
	  	menuBar.add(helpMenu);
	  	mainMenu.add(miCity);
	  	mainMenu.add(miLoad);
	  	mainMenu.add(miSave);
	  	mainMenu.add(miFullscreen);
	  	mainMenu.add(miExit);
	  	helpMenu.add(miAbout);
	  	
	  	// init context menus
		contextMenu.add(miStart);
		waypointContextMenu.add(miRemove);
	  	
	  	bUp.setMargin(new Insets(0,5,0,5));
	  	bLeft.setMargin(new Insets(0,5,0,5));
	  	bRight.setMargin(new Insets(0,5,0,5));
	  	bDown.setMargin(new Insets(0,5,0,5));
//	  	bCalculate.setMargin(new Insets(0,5,0,5));
	  	
	  	// register listeners
	  	ButtonListener bl = new ButtonListener();
	  	bUp.addActionListener(bl);
	  	bDown.addActionListener(bl);
	  	bLeft.addActionListener(bl);
	  	bRight.addActionListener(bl);
	  	bSearch.addActionListener(bl);
	  	bReset.addActionListener(bl);
	  	bDescription.addActionListener(bl);
	  	miCity.addActionListener(bl);
	  	miLoad.addActionListener(bl);
	  	miSave.addActionListener(bl);
	  	miFullscreen.addActionListener(bl);
	  	miExit.addActionListener(bl);
	  	miAbout.addActionListener(bl);
	  	WaypointListener wl = new WaypointListener();
	  	miStart.addActionListener(wl);
	  	miTarget.addActionListener(wl);
	  	miAnotherTarget.addActionListener(wl);
	  	miRemove.addActionListener(wl);
	  	KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyDispacher());

		setMinimumSize(new Dimension(276, 376));
		setTitle("BiKeIT");
	  	setDefaultCloseOperation(EXIT_ON_CLOSE);
	  	pack();
	  	setBounds(DEFAULT_BOUNDS);
	  
	}
	
	
	private void packComponents() {

		setJMenuBar(menuBar);
		
		// panels
		Container cp = getContentPane();
		JPanel pTop = new JPanel();
		JPanel pMain = new JPanel();
		JPanel pNavigation = new JPanel();
		JPanel pRouteButtons = new JPanel();
		JPanel pArrowButtons = new JPanel();
		JPanel pStatus = new JPanel();
		JPanel pUp = new JPanel();
		JPanel pLeft = new JPanel();
		JPanel pRight = new JPanel();
		JPanel pDown = new JPanel();
		JPanel pSearch = new JPanel();
		
		// set layouts
		cp.setLayout(new BorderLayout());
		pTop.setLayout(new BorderLayout());
		pMain.setLayout(new BorderLayout());
		pNavigation.setLayout(new BorderLayout());
		pRouteButtons.setLayout(new BorderLayout(0,5));
		pArrowButtons.setLayout(new BorderLayout(0,0));
		pStatus.setLayout(new BorderLayout());
		pSearch.setLayout(new BorderLayout(10,0));
		
		// make gaps
		pTop.setBorder(new EmptyBorder(10, 10, 10, 10));
		pMain.setBorder(new EmptyBorder(0, 10, 10, 10));
		pNavigation.setBorder(new EmptyBorder(0, 0, 0, 10));
		pRouteButtons.setBorder(new EmptyBorder(0, 0, 0, 10));
		pArrowButtons.setBorder(new EmptyBorder(0, 0, 10, 0));
		
		// make status bar
		pStatus.setBorder(BorderFactory.createLoweredBevelBorder());
		
		// pack panels
	  	cp.add(pTop, BorderLayout.PAGE_START);
	  	cp.add(pMain, BorderLayout.CENTER);
	  	cp.add(pStatus, BorderLayout.PAGE_END);
	  	
	  	pTop.add(pRouteButtons, BorderLayout.LINE_START);
	  	pTop.add(altitudeMapView, BorderLayout.CENTER);
	  	
	  	pMain.add(pNavigation, BorderLayout.LINE_START);
	  	pMain.add(mapView, BorderLayout.CENTER);
	  	
	  	pRouteButtons.add(pSearch, BorderLayout.PAGE_START);
	  	pRouteButtons.add(bReset, BorderLayout.CENTER);
	  	pRouteButtons.add(bDescription, BorderLayout.PAGE_END);

	  	pSearch.add(searchField, BorderLayout.CENTER);
	  	pSearch.add(bSearch, BorderLayout.LINE_END);
	  	
	  	pNavigation.add(pArrowButtons, BorderLayout.PAGE_START);
	  	pNavigation.add(zoomSlider, BorderLayout.CENTER);

	  	pArrowButtons.add(pUp, BorderLayout.PAGE_START);
	  	pArrowButtons.add(pLeft, BorderLayout.LINE_START);
	  	pArrowButtons.add(pRight, BorderLayout.LINE_END);
	  	pArrowButtons.add(pDown, BorderLayout.PAGE_END);

	  	pUp.add(bUp);
	  	pLeft.add(bLeft);
	  	pRight.add(bRight);
	  	pDown.add(bDown);
	  	
	  	pStatus.add(infoLabel, BorderLayout.CENTER);
	  	pStatus.add(zoomLevelLabel, BorderLayout.LINE_END);
	  	
	}
	  
	private void updateStatusBar() {
		
		switch(routeState) {
		case NO_WAYPOINT_SET:
			infoLabel.setText("Bitte setzten Sie Ihren Startpunkt mittels" +
					" Rechtsklick auf die Karte.");
			break;
		case ONE_WAYPOINT_SET:
			infoLabel.setText("Bitte setzten Sie Ihren Zielpunkt mittels" +
					" Rechtsklick auf die Karte.");
			break;
		case MORE_WAYPOINTS_SET:
			infoLabel.setText("Sie können weitere Zielpunkte auf die" +
					" Karte setzen.");
			break;
		case ROUTE_CALCULATED:
			infoLabel.setText("Streckenlänge: "+GUIUtilities.formatDistance(controller.getRouteLength())
					+ "      "
					+ "Fahrtdauer: "+GUIUtilities.formatDuration(controller.getEstimatedDuration())
					+ "      "
					+ "Höhenmeter: "+GUIUtilities.formatDistance(controller.getAltitudeMeters()));
			break;
		case ROUTE_CALCULATING:
			infoLabel.setText("Die Route wird berechnet. Bitte warten...");
			break;
		}
		zoomLevelLabel.setText(controller.getMapsZoomlevel().toString());
	}
	
	private void updateSlider() {
		zoomSlider.setValue(controller.getMapsZoomlevel().getValue());
	}

	private void toggleFullscreen() {
		GraphicsDevice gd = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		if (!isFullscreen) {
			if (gd.isFullScreenSupported()) {
				if (isDisplayable()) {
	                setVisible(false);
	                dispose();
	            }
				
	            setUndecorated(true);
	            
	            if (!isVisible()) {
	                setVisible(true);
	            }
				System.out.println("Fullscreen is supported!");
				gd.setFullScreenWindow(this);
				

				isFullscreen = true;
			} else {
				System.out.println("Fullscreen is not supported!");
			}
		} else {
			//gd.setFullScreenWindow(null);
			this.setVisible(false);
			this.dispose();
			this.setUndecorated(false);
			this.setVisible(true);
			isFullscreen = false;
		}
	}
	
	private void changeMouseState(MouseState newState) {
		if (newState == mouseState)
			return;
		
		mouseState = newState;
		
		switch(newState) {
		case DEFAULT: 
    		MainGUI.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    		stateContextMenu = contextMenu;
    		break;
		case OVER_WAYPOINT:
			MainGUI.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); 
			stateContextMenu = waypointContextMenu;
			miRemove.setText("Wegpunkt " + waypointIndex + " löschen");
			break;
		case MAP_DRAGGING:
			MainGUI.this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			break;
		case WP_DRAGGING:
			MainGUI.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); 
			break;
		}
	}
	
	
	private void changeRouteState(RouteState newState) {
		if (newState == routeState)
			return;
		
		routeState = newState;
		
		switch(newState) {
		case NO_WAYPOINT_SET:
	    	contextMenu.removeAll();
	    	contextMenu.add(miStart);
	    	bReset.setEnabled(false);
//	    	bCalculate.setEnabled(false);
	    	bDescription.setEnabled(false);
	    	miSave.setEnabled(false);
	    	break;
		case ONE_WAYPOINT_SET:
	    	contextMenu.removeAll();
	    	contextMenu.add(miTarget);
	    	bReset.setEnabled(true);
	    	bDescription.setEnabled(false);
	    	miSave.setEnabled(true);
	    	break;
		case MORE_WAYPOINTS_SET:
			contextMenu.removeAll();
	    	contextMenu.add(miAnotherTarget);
	    	bReset.setEnabled(true);
	    	bDescription.setEnabled(false);
	    	miSave.setEnabled(true);
	    	break;
		case ROUTE_CALCULATED:
	    	bReset.setEnabled(true);
	    	bDescription.setEnabled(true);
	    	miSave.setEnabled(true);
	    	break;
		case ROUTE_CALCULATING:
	    	bReset.setEnabled(true);
	    	bDescription.setEnabled(false);
	    	miSave.setEnabled(true);
	    	break;
		}
		this.updateStatusBar();
		this.updateSlider();
	}
	
	private void updateWaypointState() {
		switch(controller.getNumWaypoints()) {
		case 0: changeRouteState(RouteState.NO_WAYPOINT_SET);break;
		case 1: changeRouteState(RouteState.ONE_WAYPOINT_SET);break;
		default: changeRouteState(RouteState.MORE_WAYPOINTS_SET);break;
		}
	}
	  
	  
	/*
	 * inner listener classes
	 */

	private class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(bUp)) {
		    	controller.moveMap(0,50);
			} else if (e.getSource().equals(bLeft)) {
		    	controller.moveMap(50,0);
			} else if (e.getSource().equals(bRight)) {
		    	controller.moveMap(-50,0);
			} else if (e.getSource().equals(bDown)) {
		    	controller.moveMap(0,-50);
			} else if (e.getSource().equals(bSearch)) {
				String result = searchField.getText();
				if (controller.hasLocation(result)) {
			    	controller.goToLocation(result);
			    } else {
			    	JOptionPane.showMessageDialog(MainGUI.this, "Ort nicht gefunden.");
			    }
			} else if (e.getSource().equals(bReset)) {
		    	controller.resetCalculatedRoute();
		    	controller.resetPlannedWaypoints();
		    	updateWaypointState();
		    	updateStatusBar();
			} else if (e.getSource().equals(bDescription)) {
				controller.openDescriptionGUI();
			} else if (e.getSource().equals(miCity)) {
				
				String result;
				// the loop is for returning to the input dialog when the location
				// has not been found.
				do {
					result = JOptionPane.showInputDialog(MainGUI.this, "Geben Sie einen Ort ein.",
							"Zu Ort springen", JOptionPane.QUESTION_MESSAGE);
					if ((result != null) && (result.length() > 0)) {
					    if (controller.hasLocation(result)) {
					    	controller.goToLocation(result);
					    	result = null;
					    } else {
					    	JOptionPane.showMessageDialog(MainGUI.this, "Ort nicht gefunden.");
					    }
					}
				} while (result != null);
				
			} else if (e.getSource().equals(miSave)) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileFilter(){
					@Override
					public boolean accept(File file) {
						return (file.isDirectory() || file.getName().endsWith(".route"));
					}
					@Override
					public String getDescription() {
						return "BiKeIT-Route";
					}
				});
				fc.setDialogTitle("Route speichern");
				File f = new File("Unbenannt.route");
		        fc.setSelectedFile(f);  
				fc.showSaveDialog(MainGUI.this);
				File selectedFile = fc.getSelectedFile();
				if (selectedFile!=null) {
					controller.saveWaypointsToFile(selectedFile);
				}
			} else if (e.getSource().equals(miLoad)) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileFilter(){
					@Override
					public boolean accept(File file) {
						return (file.isDirectory() || file.getName().endsWith(".route"));
					}
					@Override
					public String getDescription() {
						return "BiKeIT-Route";
					}
				});
				fc.setDialogTitle("Route laden");
				fc.showOpenDialog(MainGUI.this);
				File selectedFile = fc.getSelectedFile();
				if (selectedFile!=null) {
					controller.loadWaypointsFromFile(selectedFile);
			    	updateWaypointState();
			    	updateStatusBar();
				}
			} else if (e.getSource().equals(miFullscreen)) {
				MainGUI.this.toggleFullscreen();
			} else if (e.getSource().equals(miExit)) {
		    	System.exit(0);
			} else if (e.getSource().equals(miAbout)) {
		    	new AboutDialog();
			}
		}
	}
	
	private class WaypointListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(miStart)) {
		    	controller.addWaypoint(lastMousePosition);
				updateWaypointState();
			} else if (e.getSource().equals(miTarget)) {
		    	controller.addWaypoint(lastMousePosition);
	    		updateWaypointState();
		    	controller.calculateRoute();
		    	changeRouteState(RouteState.ROUTE_CALCULATING);
			} else if (e.getSource().equals(miAnotherTarget)) {
		    	controller.addWaypoint(lastMousePosition);
	    		updateWaypointState();
		    	controller.calculateRoute();
		    	changeRouteState(RouteState.ROUTE_CALCULATING);
			} else if (e.getSource().equals(miRemove)) {
		    	controller.removeWaypoint(waypointIndex);
	    		updateWaypointState();
		    	if(controller.getNumWaypoints()>1) {
		    		controller.calculateRoute();
		    		changeRouteState(RouteState.ROUTE_CALCULATING);
		    	}
			}
			updateStatusBar();
		}
	}
	
	private class KeyDispacher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
    			if (e.getKeyChar() == '+') {
    				controller.zoomIn();
    				updateStatusBar();
    		    	updateSlider();
    			} else if (e.getKeyChar() == '-') {
    				controller.zoomOut();
    				updateStatusBar();
    		    	updateSlider();
    			} else if ((e.getKeyCode() == KeyEvent.VK_F) &&
    					(e.isMetaDown() || e.isControlDown()) || e.getKeyCode() == KeyEvent.VK_F11) {
    				MainGUI.this.toggleFullscreen();
    			} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && MainGUI.this.isFullscreen) {
    				MainGUI.this.toggleFullscreen();
    			} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
    				controller.moveMap(0,-50);
    			} else if (e.getKeyCode() == KeyEvent.VK_UP) {
    				controller.moveMap(0,50);
    			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
    				controller.moveMap(-50,0);
    			} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
    				controller.moveMap(50,0);
    			}
    			
            }
            return false;
        }
    }
	
	private class MapMouseWheelListener implements MouseWheelListener {

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getWheelRotation() < 0) {
				controller.zoomInMap(e.getPoint());
				updateStatusBar();
		    	updateSlider();
			} else {
				controller.zoomOut();
				updateStatusBar();
		    	updateSlider();
			}
			// take focus away from search field
			MainGUI.this.searchField.transferFocus();
		}
	}
	
	private class GUIMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent arg0) {
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {
		}
		@Override
		public void mouseExited(MouseEvent arg0) {
		}
		@Override
		public void mousePressed(MouseEvent arg0) {
			// take focus away from search field
			MainGUI.this.searchField.transferFocus();
		}
		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
	}
	
	private class MapMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (MainGUI.this.mouseState != MouseState.WP_DRAGGING) {
				//double click
				if (e.getClickCount() == 2) {
					controller.zoomInMap(e.getPoint());
					updateStatusBar();
			    	updateSlider();
				}
				if (e.getButton() == MouseEvent.BUTTON3) {
					stateContextMenu.show(mapView, e.getX(), e.getY());
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
			this.mouseReleased(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
	    	if (mouseState == MouseState.WP_DRAGGING) {
	    		Point newWPPos = e.getPoint();
	    		newWPPos.translate(waypointMouseOffsetX, waypointMouseOffsetY);
	    		controller.stopDraggingWaypoint(waypointIndex, newWPPos);
	    		updateWaypointState();
		    	if (controller.getNumWaypoints()>1) {
		    		controller.calculateRoute();
		    		changeRouteState(RouteState.ROUTE_CALCULATING);
		    	}
		    	changeMouseState(MouseState.OVER_WAYPOINT);
	    	} else if (mouseState != MouseState.OVER_WAYPOINT){
				changeMouseState(MouseState.DEFAULT);
	    	}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			lastMousePosition = e.getPoint();
			if (mouseState == MouseState.OVER_WAYPOINT) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					changeMouseState(MouseState.WP_DRAGGING);
					Point p = controller.getWaypointPosition(waypointIndex);
					waypointMouseOffsetX = p.x - e.getPoint().x;
					waypointMouseOffsetY = p.y - e.getPoint().y;
					MainGUI.this.controller.startDraggingWaypoint(waypointIndex);
				}
			} else if (mouseState != MouseState.WP_DRAGGING) {
				changeMouseState(MouseState.MAP_DRAGGING);
			}
			// take focus away from search field
			MainGUI.this.searchField.transferFocus();
		}

	}
	
	
	private class MapMouseMotionListener implements MouseMotionListener {
		@Override
	    public void mouseDragged(MouseEvent e) {
	    	Point actualMousePosition = e.getPoint();
	    	int dx = actualMousePosition.x - lastMousePosition.x;
	    	int dy = actualMousePosition.y - lastMousePosition.y;
	    	if (mouseState == MouseState.MAP_DRAGGING) {
	    		controller.moveMap(dx, dy);
	    	} else if (mouseState == MouseState.WP_DRAGGING) {
	    		Point newPos = e.getPoint();
	    		newPos.translate(waypointMouseOffsetX, waypointMouseOffsetY);
	    		controller.dragWaypoint(waypointIndex, newPos);
	    	}
	    	lastMousePosition = actualMousePosition;
	    	updateStatusBar();
	    	updateSlider();
	    }
	    
	    @Override
	    public void mouseMoved(MouseEvent e) {
	    	updateSlider();
	    	if (controller.isWaypoint(e.getPoint())) {
				waypointIndex = controller.getWaypoint(e.getPoint());
				changeMouseState(MouseState.OVER_WAYPOINT);
	    	} else {
				changeMouseState(MouseState.DEFAULT);
	    	}
	    }
	}
	
	private class MapComponentListener implements ComponentListener {
		@Override
		public void componentHidden(ComponentEvent e) {
		}

		@Override
		public void componentMoved(ComponentEvent e) {
		}

		@Override
		public void componentResized(ComponentEvent e) {
			controller.setMapDimension(e.getComponent().getSize());
		}

		@Override
		public void componentShown(ComponentEvent e) {
			controller.setMapDimension(e.getComponent().getSize());
		}
	}
	
	private class AltitudeMapComponentListener implements ComponentListener {
		@Override
		public void componentHidden(ComponentEvent e) {
		}
		@Override
		public void componentMoved(ComponentEvent e) {
		}
		@Override
		public void componentResized(ComponentEvent e) {
			controller.setAltitudeMapDimension(e.getComponent().getSize());
		}
		@Override
		public void componentShown(ComponentEvent e) {
			controller.setAltitudeMapDimension(e.getComponent().getSize());
		}
	}
	
	private class ZoomSliderChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();
			if (source.getValueIsAdjusting() == false) {
				controller.setZoomlevel(new Zoomlevel(source.getValue()));
				updateStatusBar();
			}
		}
	}

	@Override
	public void calculationFinished(CalculatedRoute route) {
		if (route!=null) {
	    	changeRouteState(RouteState.ROUTE_CALCULATED);
		} else {
			// no route found
			changeRouteState(RouteState.MORE_WAYPOINTS_SET);
	    	JOptionPane.showMessageDialog(this, "Es konnte keine Route zwischen den gegebenen Wegpunkten gefunden werden.");
		}
	}

	private class SearchField extends JTextField implements FocusListener, KeyListener {
		private static final long serialVersionUID = 5117565883422541706L;
		private String text;
		public SearchField(String text) {
			super();
			this.text = text.trim();
			setText(text);
			setForeground(SystemColor.textInactiveText);
			addFocusListener(this);
			this.addKeyListener(this);
		}
		@Override
		public void focusGained(FocusEvent arg0) {
			if (getText().trim().equals(text)) {
				setText("");
				setForeground(SystemColor.textText);
			}
		}
		@Override
		public void focusLost(FocusEvent arg0) {
			if (getText().trim().equals("")) {
				setText(text);
				setForeground(SystemColor.textInactiveText);
			}
		}
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER && searchField.hasFocus()) {
				String result = searchField.getText();
				if (controller.hasLocation(result)) {
			    	controller.goToLocation(result);
			    } else {
					// take focus away from search field
					MainGUI.this.searchField.transferFocus();
			    	JOptionPane.showMessageDialog(MainGUI.this, "Ort nicht gefunden.");
			    	System.out.println("fertig");
			    	MainGUI.this.searchField.grabFocus();
			    }
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {
			
		}
		@Override
		public void keyTyped(KeyEvent e) {
		}
	}
	
}
