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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class AboutDialog extends JFrame {
	
	private static final long serialVersionUID = -1136257179823883234L;

	public AboutDialog() {
		
		JLabel logo = new JLabel(new ImageIcon(this.getClass().getClassLoader().getResource("logo.png")));
		JLabel desc = new JLabel("Ein Routenplaner für Fahrradfahrer");
		JLabel copy = new JLabel("© 2011-2012");
		JLabel label1 = new JLabel("Sven Esser");
		JLabel label2 = new JLabel("Manuel Fink");
		JLabel label3 = new JLabel("Thomas Keh");
		JLabel label4 = new JLabel("Tilman Väth");
		JLabel label5 = new JLabel("Lukas Vojcović");
		JLabel label6 = new JLabel("Fabian Winnen");
		JLabel osm1 = new JLabel("Kartendaten:");
		JLabel osm2 = new JLabel("© 2004-2012");
		JLabel osm3 = new JLabel("OpenStreetMap.org");

		logo.setAlignmentX(CENTER_ALIGNMENT);
		desc.setAlignmentX(CENTER_ALIGNMENT);
		copy.setAlignmentX(CENTER_ALIGNMENT);
		label1.setAlignmentX(CENTER_ALIGNMENT);
		label2.setAlignmentX(CENTER_ALIGNMENT);
		label3.setAlignmentX(CENTER_ALIGNMENT);
		label4.setAlignmentX(CENTER_ALIGNMENT);
		label5.setAlignmentX(CENTER_ALIGNMENT);
		label6.setAlignmentX(CENTER_ALIGNMENT);
		osm1.setAlignmentX(CENTER_ALIGNMENT);
		osm2.setAlignmentX(CENTER_ALIGNMENT);
		osm3.setAlignmentX(CENTER_ALIGNMENT);
		
		Container c = this.getContentPane();
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		p.setBorder(new EmptyBorder(10,10,10,10));
		
		c.add(p);
		p.add(logo);
		p.add(Box.createVerticalStrut(10));
		p.add(desc);
		p.add(Box.createVerticalStrut(20));
		p.add(copy);
		p.add(Box.createVerticalStrut(10));
		p.add(label1);
		p.add(label2);
		p.add(label3);
		p.add(label4);
		p.add(label5);
		p.add(label6);
		p.add(Box.createVerticalStrut(20));
		p.add(osm1);
		p.add(Box.createVerticalStrut(10));
		p.add(osm2);
		p.add(osm3);

		pack();

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
	    
		setTitle("Über BiKeIT");
		setVisible(true);
		
	}
	
}
