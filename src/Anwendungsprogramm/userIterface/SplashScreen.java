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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

public class SplashScreen extends JFrame {

	private static final long serialVersionUID = 7939423503350585666L;
	private JProgressBar progress;
	
	public SplashScreen() {
		
		JLabel logo = new JLabel(new ImageIcon(this.getClass().getClassLoader().getResource("logo.png")));
		logo.setBorder(new EmptyBorder(0,0,10,0));
		
		progress = new JProgressBar();
		progress.setValue(0);
		
		JPanel c = (JPanel) getContentPane();
		c.setLayout(new BorderLayout());
		c.setBorder(new EmptyBorder(10,10,10,10));
		
		c.add(logo, BorderLayout.PAGE_START);
		c.add(progress, BorderLayout.CENTER);
		
		// center frame
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int top = (int) (screenSize.height - getHeight())/2-100;
	    int left = (int) (screenSize.width - getWidth())/2-160; 
	    setLocation(left, top);

	    try {
			setIconImage(ImageIO.read(this.getClass().getClassLoader().getResource("icon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    setUndecorated(true);
	    setTitle("Lädt");
		pack();
		setVisible(true);
		
	}
	
	public void setPercentage(double p) {
		progress.setValue((int) Math.max(0, Math.min(100, p)));
	}
	
}
