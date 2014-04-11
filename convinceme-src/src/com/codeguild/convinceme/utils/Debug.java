package com.codeguild.convinceme.utils;

/**
 * <p>Description: Print debug statements</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 * @author Patti Schank
 */

public class Debug {

    public static final int ALWAYS = 1;             // ALWAYS print this message
    public static final int LIGHT = 2;              // print if LIGHT debugging
    public static final int VERBOSE = 3;            // print if VERBOSE debugging
    public static final int DEFAULT_LEVEL = LIGHT;  // default debug level
    public static final String PREFIX = "CM Debug: ";

    public static int sDebugLevel = DEFAULT_LEVEL;  // print below or equal to this level

    /**
     * Print the given object to the output without a newline, using DEFAULT_LEVEL for debug
     * @param o  object to print
     */
    public static void print(Object o) {
        print(o, DEFAULT_LEVEL);
    }

    /**
     * Print the given object to the output, without a newline
     * @param  o  object to print
     * @param  level debug level (ALWAYS, LIGHT, or VERBOSE)
     */
    public static void print(Object o, int level) {
        if (level <= sDebugLevel) {
            System.out.print(PREFIX + o.toString());
        }
    }

    /**
     * Print the given string to the output without a newline, using DEFAULT_LEVEL for debug
     * @param s string to print
     */
    public static void print(String s) {
        print(s, DEFAULT_LEVEL);
    }

    /**
     * Print the given string to the output without a newline
     * @param s string to print
     * @param level debug level (ALWAYS, LIGHT, or VERBOSE)
     */
    public static void print(String s, int level) {
        if (level <= sDebugLevel) {
            System.out.print(PREFIX + s);
        }
    }

    /**
     * Print the given object to the output with a newline, using DEFAULT_LEVEL for debug
     * @param o  object to print
     */
    public static void println(Object o) {
        println(o, DEFAULT_LEVEL);
    }

    /**
     * Print the given object to the output with a newline
     * @param o  object to print
     * @param level debug level (ALWAYS, LIGHT, or VERBOSE)
     */
    public static void println(Object o, int level) {
        if (level <= sDebugLevel) {
            System.out.println(PREFIX + o.toString());
        }
    }

    /**
     * Print the given string to the output with a newline, using DEFAULT_LEVEL for debug
     * @param s string to print
     */
    public static void println(String s) {
        println(s, DEFAULT_LEVEL);
    }

    /**
     * Print the given string to the output, with a newline
     * @param s string to print
     * @param level debug level (ALWAYS, LIGHT, or VERBOSE)
     */
    public static void println(String s, int level) {
        if (level <= sDebugLevel) {
            System.out.println(PREFIX + s);
        }
    }

    /**
     * Print the stack trace for the given Throwable, using DEFAULT_LEVEL for debug
     * @param t throwable to print
     */
    public static void printStackTrace(Throwable t) {
        printStackTrace(t, DEFAULT_LEVEL);
    }

    /**
     * Print the stack trace for the given Throwable
     * @param t throwable to print
     * @param level debug level (ALWAYS, LIGHT, or VERBOSE)
     */
    public static void printStackTrace(Throwable t, int level) {
        if (level <= sDebugLevel) {
            t.printStackTrace();
        }
    }

    /**
     * Set the debug level, which defaults to DEBUG.LIGHT
     */
    public static void setDebug(int value) {
        sDebugLevel = value;
        if (value < ALWAYS) { // some values always get printed
            sDebugLevel = ALWAYS;
        }
    }
}
