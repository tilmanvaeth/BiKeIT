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

import guiControllers.DescriptionGUIController;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import utilities.GUIUtilities;
import controllers.RouteInformationController;




public class DescriptionGUI extends JFrame {

	private static final long serialVersionUID = -426723467344225279L;
	private DescriptionGUIController controller;
	
	private JPanel dataBox;
	private Box instructionsBox;
	private MapView mapView;
	private AltitudeMapView altitudeMapView;
	private JPanel content;
	private JButton btnPrint = new JButton("Drucken");
	private JButton btnClose = new JButton("Schließen");
	
	public DescriptionGUI(DescriptionGUIController controller) {
		
		this.controller = controller;
		controller.refresh();

	    try {
			setIconImage(ImageIO.read(this.getClass().getClassLoader().getResource("icon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
		init();
		initData();
		initInstructions();
		packComponents();
		configureFrame();
		
	}
	
	private void initData() {
		
		Box left = Box.createVerticalBox();
		Box right = Box.createVerticalBox();
		
		dataBox.add(left, BorderLayout.LINE_START);
		dataBox.add(right, BorderLayout.CENTER);
		
		JLabel l1 = new JLabel("Streckenlänge:   ");
		JLabel l2 = new JLabel("Geschätzte Fahrtdauer bei " + (int)RouteInformationController.getSpeed() + " km/h :   ");
		JLabel l3 = new JLabel("Zu bewältigende Höhenmeter:   ");
		
		JLabel r1 = new JLabel(GUIUtilities.formatDistance(controller.getRouteLength()));
		JLabel r2 = new JLabel(GUIUtilities.formatDuration(controller.getEstimatedDuration()));
		JLabel r3 = new JLabel(GUIUtilities.formatDistance(controller.getAltitudeMeters()));
		
		l1.setAlignmentX(RIGHT_ALIGNMENT);
		l2.setAlignmentX(RIGHT_ALIGNMENT);
		l3.setAlignmentX(RIGHT_ALIGNMENT);
		
		r1.setAlignmentX(LEFT_ALIGNMENT);
		r2.setAlignmentX(LEFT_ALIGNMENT);
		r3.setAlignmentX(LEFT_ALIGNMENT);
		
		left.add(l1);
		left.add(l2);
		left.add(l3);

		right.add(r1);
		right.add(r2);
		right.add(r3);
		
	}
	
	private void initInstructions() {

		for (String s : controller.getInstructions()) {
			
//			Box line = Box.createHorizontalBox();
			JPanel line = new JPanel();
			line.setLayout(new FlowLayout(FlowLayout.LEADING));
	        
			instructionsBox.add(line);
			line.setAlignmentX(LEFT_ALIGNMENT);
			instructionsBox.add(Box.createVerticalStrut(10));
			
			String[] parts = s.split(" ", 4);
			
			
			// number
			JLabel l1 = new JLabel(parts[0]+" ");
			int width = l1.getFontMetrics(l1.getFont()).stringWidth(controller.getInstructions().size()+". ");
			l1.setForeground(SystemColor.textInactiveText);
			l1.setPreferredSize(new Dimension(width, (int) l1.getPreferredSize().getHeight()));
			line.add(l1);
			
			// distance
			JLabel l2 = new JLabel(parts[1]);
			l2.setFont(Font.decode(Font.MONOSPACED));
			l2.setForeground(SystemColor.windowText.brighter());
			width = l2.getFontMetrics(l2.getFont()).stringWidth(controller.getRouteLength()+"m");
			l2.setPreferredSize(new Dimension(width, (int) l2.getPreferredSize().getHeight()));
			line.add(l2);
			
			// image
			String character = parts[2];
			String filename;
			if (character.endsWith("↑")) {
				filename = "straight_on";
			} else if (character.endsWith("↰")) {
				filename = "left";
			} else if (character.endsWith("↱")) {
				filename = "right";
			} else if (character.endsWith("↖")) {
				filename = "half_left";
			} else if (character.endsWith("↗")) {
				filename = "half_right";
			} else if (character.endsWith("↷")) {
				filename = "turn_over";
			} else if (character.endsWith("↙")) {
				filename = "sharp_left";
			} else if (character.endsWith("↘")) {
				filename = "sharp_right";
			} else if (character.endsWith("⚑")) {
				filename = "start";
			} else {
				filename = "target";
			}
			
			ImageIcon icon = new ImageIcon(this.getClass().getClassLoader().getResource(filename+".png"));
			icon.setImage(icon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH));
			JLabel l3 = new JLabel(icon);
			l3.setPreferredSize(new Dimension(42,22));
			l3.setForeground(Color.BLUE);
			l3.setFont(l3.getFont().deriveFont(l3.getFont().getSize()+10.0f));
			line.add(l3);
			
			// text
			JLabel l4 = new JLabel(parts[3]);
			l4.setForeground(SystemColor.windowText);
			line.add(l4);
			
		}
		
	}
	
	private void init() {
		
		dataBox = new JPanel();
		dataBox.setLayout(new BorderLayout());
		dataBox.setBorder(new EmptyBorder(5,5,5,5));
		
		instructionsBox = Box.createVerticalBox();
		instructionsBox.setBorder(new EmptyBorder(5,5,5,5));
		
		mapView = controller.getMapView();
		mapView.setPreferredSize(new Dimension(500,300));
		controller.setMapDimension(new Dimension(500,300));
		mapView.repaint();
		
		altitudeMapView = controller.getAltitudeMapView();
		altitudeMapView.setPreferredSize(new Dimension(500,100));
		controller.setAltitudeMapDimension(new Dimension(500,100));
		
		ButtonListener bl = new ButtonListener();
		btnClose.addActionListener(bl);
		btnPrint.addActionListener(bl);
		
	}
	
	private void packComponents() {
		
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		
		content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		content.setBorder(new EmptyBorder(10,10,10,10));
		
		JScrollPane scroll = new JScrollPane(content);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		JPanel pMap = new JPanel();
		pMap.setBorder(new TitledBorder("Kartenausschnitt"));
		
		JPanel pAltMap = new JPanel();
		pAltMap.setBorder(new TitledBorder("Höhenverlauf"));
		
		JPanel pData = new JPanel();
		pData.setLayout(new BorderLayout());
		pData.setBorder(new TitledBorder("Routendaten"));
		
		JPanel pInstructions = new JPanel();
		pInstructions.setLayout(new GridLayout(1,1));
		pInstructions.setBorder(new TitledBorder("Beschreibung"));
		
		JPanel pButtons = new JPanel();
		pButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		pMap.add(mapView);
		pAltMap.add(altitudeMapView);
		pData.add(dataBox);
		pInstructions.add(instructionsBox);
		
		pButtons.add(btnPrint);
		pButtons.add(btnClose);
		
		c.add(scroll, BorderLayout.CENTER);
		c.add(pButtons, BorderLayout.PAGE_END);
		
		content.add(pMap);
		content.add(Box.createVerticalStrut(10));
		content.add(pAltMap);
		content.add(Box.createVerticalStrut(10));
		content.add(pData);
		content.add(Box.createVerticalStrut(10));
		content.add(pInstructions);

		pack();
		
	}
	
	private void configureFrame() {
		
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
	    
	    // set size
		/* --- workaround: setMaximumSize() doesn't work if the frame contains boxes --- */
		int wMax = (int) screenSize.getWidth() - 20;
		int hMax = (int) screenSize.getHeight() - 100;
		int w = (int) getPreferredSize().getWidth();
		int h = (int) getPreferredSize().getHeight();
		if (w>wMax) w = wMax;
		if (h>hMax) h = hMax;
	    setSize(w,h);
	    /* --- */

		// center frame
		int top = (int) (screenSize.height - getHeight())/2;
	    int left = (int) (screenSize.width - getWidth())/2; 
	    setLocation(left, top);
	    
		setTitle("Routenbeschreibung");
		setVisible(true);
		
	}
	
	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (e.getSource().equals(btnClose)) {
				DescriptionGUI.this.dispose();
			} else if (e.getSource().equals(btnPrint)) {
				mapView.repaint();
				controller.print();
			}
			
		}
		
	}
	
}