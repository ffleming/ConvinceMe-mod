package com.codeguild.convinceme.utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>Title: ImageUtils </p>
 * <p>Description: Utilities for working with images </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Codeguild</p>
 * @author Patti Schank
 * @version 1.0
 */
public class ImageUtils  {

    /**
     * a debug switch for finding images local files rather than in jar.
     * jar is used for production; default = false (reset to true in debug launch) */
    public static boolean sGetImagesLocally = true;

    private static ImageUtils sInstance = new ImageUtils();
    private static JPanel sDummyComponent = new JPanel();

    public static ImageUtils getInstance() {
        return sInstance;
    }

    public ImageUtils() {
		super();  
    }
    
	 /* Wait for an image to load
	  * @param the component loading the image
	  * @param the image to load 
	  */
	public static void waitForImage(Component component, Image image) {
		MediaTracker tracker = new MediaTracker(component);
		try {
			tracker.addImage(image,0);
			tracker.waitForID(0);
		}
		catch (InterruptedException e) {
			Debug.println("Interruption in loading image");
		}
	}
		 
	 /*
	  * Get an image from a URL
	  * @param the string for the URL
	  * @return Image captured
	  */
	 public static Image getImageFromURL(String s) {
	 	   Image image = null;
	 	   try { 	
       			URL url = new URL(s);
      			image = Toolkit.getDefaultToolkit().getImage(url);
      			// m_image = (Image)url.getContent();
			}
        	catch(MalformedURLException evt) {
        		JOptionPane.showMessageDialog(new Frame(), "Invalid URL.", "Sorry", JOptionPane.ERROR_MESSAGE);
        		Debug.println("Invalid image URL.");
        	}
      		catch(IOException e) {
         		Debug.println("Can't get image from URL");
     		}
     		return image;
     	}
     /*
	  * Get an image from a filename
	  * @param the string for the filename	  
	  * @return Image captured
	  */
	 public static Image getImageFromFilename(String s) {
		//Debug.println("Getting image " + s);
	 	   Image image = null;
	 	   try {		   
     			File f = new File(s);
     			image = Toolkit.getDefaultToolkit().getImage(s);
     		}
     		catch (Exception e) {
     			Debug.println("Bad file path");
     		}
     		return image;
     	}


	/**
		retrieves an icon for an image in a jar file.
		a simple wrapper for getImageFromJar, 
		@param filename the filename of the icon image file
	**/
	public static Icon getIconFromJar(String filename) {
		return new ImageIcon(getImageFromJar(filename, com.codeguild.convinceme.gui.Node.class));
	}
	
	/**
	 * 	retrieves an icon for an image that's a file.
	 * a simple wrapper for getImageFromFilename
	 * @param filename the file name of the icon image file
	 * 
	 * @return an ImageIcon of the file
	 * FSFmod
	**/
	public static Icon getIconFromFilename (String filename) {
		return new ImageIcon(getImageFromFilename(filename));
	}
	

       /** Fetch an image out of a jar file.
     * see http://www.javaworld.com/javaworld/jw-07-1998/jw-07-jar.html
     *
     * @param filename the name of the image file inside the jar file.
     * NOTE: The package path of this class will be prepended to the
     * filename. E.g., if the package-qualified name of this class
     * is myStuff.myApplet and the resource file name
     * is "images/myFile.gif", the ClassLoader will search the
     * jar file for "myStuff/images/myFile.gif".
     * Need the 'byte' stuff to get around Netscape 4.0 and MW bug
     * Navigator 4.0 returns null from getResource(String), so
     * getResourceAsStream(String) is the only option, using byte.
     * MRJ works with either getResource or getResourceAsStream.
     * @return an image built from the file if found, null if the file
     * was not successfully retrieved.
     */

    static public Image getImageFromJar(String filename, Class packageclass) {
        if (filename == null) return null;
        
        // we'll kinda hack 'global' image hierarchies.
        // (basically since it's kinda ugly to duplicate icons in...nodestore
        if (packageclass == null) {
            packageclass = com.codeguild.convinceme.gui.Node.class;
        }
        
        Image image = null;
        Toolkit kit = Toolkit.getDefaultToolkit();
        
        try {
            if (sGetImagesLocally) { // debug case--images from local files
                Debug.println("getting images from local file " + filename);
                image = kit.createImage(filename);
            } else {
                Debug.println("getting images from jar: " + filename, Debug.VERBOSE);
                image = kit.createImage(packageclass.getResource(filename));
            }

            /** todo: %% this "fetching" debug line is bothersome, but
             *  if the tracker below fails, it throws an exception in
             *  a separate thread, without a good stack trace to figure
             *  out which image is bad.  even worse is if we don't confirm
             *  the image now, and a stack trace is thrown in some AWT paint
             *  thread.
             *
             *  so workaround is to confirm
             *  each image here, right after a debug line prints to see
             *  what image is about to be tested (in the tracker thread)
             */
            Debug.println("... fetching: " + filename + " ", Debug.VERBOSE );

            MediaTracker tracker = new MediaTracker(sDummyComponent);
            tracker.addImage(image, 0);
            tracker.waitForAll();

        } catch (Throwable t) {
            Debug.println("error getting resource " + filename);
            Debug.printStackTrace(t);
        }

        return image;
    }

}
	 	
