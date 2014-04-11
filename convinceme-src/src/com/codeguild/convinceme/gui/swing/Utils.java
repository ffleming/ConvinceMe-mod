package com.codeguild.convinceme.gui.swing;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

/**
 * <p>Description: Generic swing UI utilities</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 * @author Patti Schank
 */
public class Utils {

	/**
     * Positions the specified frame in the middle of the screen.
     * @param frame JFrame to position
     */
	public static void centerFrameOnScreen(JFrame frame) {
    	Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    	Dimension f = frame.getSize();
    	frame.setBounds(Math.max(0, (d.width - f.width) / 2),
                   	 	Math.max(0, (d.height - f.height) / 2),
                    	f.width, f.height);
  	}

	/**
	 * Maximized the specified frame to fill the screen.
	 * @param frame the JFrame to maximize
	 */
	public static void maximizeFrameOnScreen(JFrame frame) {
    	Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    	frame.setBounds(0, 0, d.width, d.height - 40);
  	}
}