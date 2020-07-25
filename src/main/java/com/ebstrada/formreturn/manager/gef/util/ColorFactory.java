package com.ebstrada.formreturn.manager.gef.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * A flyweight factory class used to get color instances. This only creates new
 * instances of a Color if required. Previous instances are cached.
 */
public class ColorFactory {

    /**
     * A map of previously created colors mapped by an RGB string description in
     * the form "rrr ggg bbb" where rrr = red value int ggg = green value int
     * and bbb = blue value int.
     */
    private static final Map USED_COLORS_BY_RGB_STRING = new HashMap();
    private static final Map USED_COLORS_BY_RGB_INTEGER = new HashMap();

    static {
        cacheColor(Color.white);
        cacheColor(Color.black);
        cacheColor(Color.red);
        cacheColor(Color.green);
        cacheColor(Color.blue);
    }

    /**
     * A utility
     */
    private ColorFactory() {
    }

    /**
     * A flyweight factory method for reusing the same Color value multiple
     * times.
     *
     * @param colorDescr   A string of RGB values seperated by space or a color name
     *                     recognised by PGML (later to include SVG)
     * @param defaultColor a color to return if the color description can't be
     *                     interpretted.
     * @return the equivilent Color
     */
    public static Color getColor(String colorDescr, Color defaultColor) {
        Color color = getColor(colorDescr);

        if (color != null) {
            return color;
        }

        return defaultColor;
    }

    /**
     * A flyweight factory method for reusing the same Color value multiple
     * times.
     *
     * @param colorDescr A string of RGB values seperated by space or a color name
     *                   recognised by PGML (later to include SVG)
     * @return the equivilent Color
     */
    public static Color getColor(String colorDescr) {
        Color color = null;
        if (colorDescr.equalsIgnoreCase("white")) {
            color = Color.white;
        } else if (colorDescr.equalsIgnoreCase("black")) {
            color = Color.black;
        } else if (colorDescr.equalsIgnoreCase("red")) {
            color = Color.red;
        } else if (colorDescr.equalsIgnoreCase("green")) {
            color = Color.green;
        } else if (colorDescr.equalsIgnoreCase("blue")) {
            color = Color.blue;
        } else if (colorDescr.indexOf(' ') > 0) {
            // If there any spaces we assume this is a space
            // seperated string of RGB values
            color = getColorByRgb(colorDescr);
        } else {
            // Otherwise we assume its a single integer value
            color = getColorByRgb(Integer.valueOf(colorDescr));
        }
        return color;
    }

    /**
     * Get a color based on a space seperated RGB string.
     *
     * @param colorDescr an RGB description of the color as integers seperated by
     *                   spaces.
     * @return the required Color object.
     */
    private static Color getColorByRgb(String colorDescr) {
        Color color = (Color) USED_COLORS_BY_RGB_STRING.get(colorDescr);
        if (color == null) {
            StringTokenizer st = new StringTokenizer(colorDescr, " ");
            int red = Integer.parseInt(st.nextToken());
            int green = Integer.parseInt(st.nextToken());
            int blue = Integer.parseInt(st.nextToken());
            color = new Color(red, green, blue);
            cacheColor(colorDescr, color);
        }

        return color;
    }

    /**
     * Get a color based on a single RGB integer.
     *
     * @param rgbInteger the integer value of the color.
     * @return the required Color object.
     */
    private static Color getColorByRgb(Integer rgbInteger) {
        Color color = (Color) USED_COLORS_BY_RGB_INTEGER.get(rgbInteger);
        if (color == null) {
            color = Color.decode(rgbInteger.toString());
            cacheColor(rgbInteger, color);
        }

        return color;
    }

    /**
     * Cache a Color the indexes will be deduced.
     *
     * @param color
     */
    private static void cacheColor(Color color) {
        cacheColor(colorToInteger(color), color);
    }

    /**
     * Cache a Color providing the RGB string by which it can be retrieved
     *
     * @param stringIndex
     * @param color
     */
    private static void cacheColor(String stringIndex, Color color) {
        cacheColor(stringIndex, colorToInteger(color), color);
    }

    /**
     * Convert a Color to an single Integer value.
     *
     * @param color The color
     * @return the single integer value representing the Color
     */
    private static Integer colorToInteger(Color color) {
        return new Integer(color.getRGB());
        // Integer.valueOf(color.getRGB()); - TODO when JRE1.4 support dropped
    }

    /**
     * Cache a Color providing the RGB integer by which it can be retrieved
     *
     * @param intIndex
     * @param color
     */
    private static void cacheColor(Integer intIndex, Color color) {
        cacheColor(color.getRed() + " " + color.getGreen() + " " + color.getBlue(), intIndex,
            color);
    }

    /**
     * Cache a Color providing all the indexes by which it can be retrieved
     *
     * @param stringIndex
     * @param intIndex
     * @param color
     */
    private static void cacheColor(String stringIndex, Integer intIndex, Color color) {
        USED_COLORS_BY_RGB_INTEGER.put(intIndex, color);
        USED_COLORS_BY_RGB_STRING.put(stringIndex, color);
    }
}
