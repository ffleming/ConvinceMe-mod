package com.codeguild.convinceme.gui;

import com.codeguild.convinceme.model.*;
import com.codeguild.convinceme.utils.Debug;

import java.awt.*;
//import java.util.Vector;

/**
 * <p>Description: Common methods, data members, and statics
 * used for both swing and awt versions</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 * @author Patti Schank
 * @author Forrest Fleming
 */
public class Configuration {

    public static final Font MENU_FONT = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font TEXT_FONT = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 12);

    public static final Color DARK_GRAY = new Color(76, 85, 95);
    public static final Color BACKGROUND = new Color(230, 230, 230);
    public static final Color LIGHT_BLUE = new Color(232, 244, 255);
    public static final Color GRAY = new Color(153, 153, 153);
    public static final Color LIGHT_GRAY = new Color(200, 200, 200);

    protected Argument mArgument;
    protected Encoding mEncoding;

    public Configuration() {
        mArgument = new Argument();
    }

    /**
     * Quit the application or applet
     */
    public void quit() {
        System.exit(0);
    }

    /**
     * @return The current argument
     */
    public Argument getArg() {
        return mArgument;
    }

    /**
     * Delete the current argument
     */
    public void deleteArgument() {
        mArgument = new Argument();
        log("Deleting entire argument...");
        updatePanels();
    }

    /**
     * Run the ECHO simulation, log what happens, and tell the user
     */
    public void runECHO(float excit, float inhib, float dataexcit, float decay) {
        ECHOSimulation simulation = new ECHOSimulation(excit,
                                                       inhib,
                                                       dataexcit,
                                                       decay,
                                                       mArgument);
        String result = simulation.runECHO();
        log(simulation.getLog());
        updatePanels();
        /*
         String message = "Correlation between you and ECHO: "
                        + result
                        + ".\n See log for more information.";
        showCorrelationMessage(message);
        */
    }

    /**
     * Update all panels after adding/deleting. Override me
     */
    public void updatePanels() {
        Debug.println("Override updatePanels method");
    }

    /**
     * Record text to a log. Override me
     * @param text
     */
    public void log(String text) {
        Debug.println("Override log method ");
    }

    /**
     * Show a message to the user about the ECHO correlation with
     * their ratings. Override me
     * @param message
     */
    public void showCorrelationMessage(String message) {
        Debug.println("Override show correlation method");
    }

}
