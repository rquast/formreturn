package com.ebstrada.formreturn.manager.gef.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;

/**
 * This class manages the resource locations needed within the application.
 * Already loaded resources are cached. The resources can be searched in
 * different locations.
 */

public class ResourceLoader {
    private static HashMap _resourceCache = new HashMap();
    private static List _resourceLocations = new ArrayList();
    private static List _resourceExtensions = new ArrayList();

    public static ImageIcon lookupIconResource(String resource) {
        return lookupIconResource(resource, resource);
    }

    public static ImageIcon lookupIconResource(String resource, String desc) {
        return lookupIconResource(resource, desc, null);
    }

    public static ImageIcon lookupIconResource(String resource, ClassLoader loader) {
        return lookupIconResource(resource, resource, loader);
    }

    /**
     * This method tries to find an ImageIcon for the given name in all known
     * locations. The file extension of the used image file can be any of the
     * known extensions.
     *
     * @param resource Name of the image to be looked after.
     * @param desc     A description for the ImageIcon.
     * @param loader   The class loader that should be used for loading the
     *                 resource.
     * @return ImageIcon for the given name, null if no image could be found.
     */
    public static ImageIcon lookupIconResource(String resource, String desc, ClassLoader loader) {
        String strippedName = Util.stripJunk(resource);
        if (isInCache(strippedName)) {
            return (ImageIcon) _resourceCache.get(strippedName);
        }

        ImageIcon res = null;
        java.net.URL imgURL = null;
        try {
            for (Iterator extensions = _resourceExtensions.iterator(); extensions.hasNext(); ) {
                String tmpExt = (String) extensions.next();
                for (Iterator locations = _resourceLocations.iterator(); locations.hasNext(); ) {
                    String imageName =
                        (String) locations.next() + "/" + strippedName + "." + tmpExt;
                    // System.out.println("[ResourceLoader] try loading " +
                    // imageName);
                    if (loader == null) {
                        imgURL = ResourceLoader.class.getResource(imageName);
                    } else {
                        imgURL = loader.getResource(imageName);
                    }
                    if (imgURL != null) {
                        break;
                    }
                }
                if (imgURL != null) {
                    break;
                }
            }
            if (imgURL == null) {
                return null;
            }
            res = new ImageIcon(imgURL, desc);
            synchronized (_resourceCache) {
                _resourceCache.put(strippedName, res);
            }
            return res;
        } catch (Exception ex) {
            System.err.println("Exception in looking up IconResource");
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return new ImageIcon(strippedName);
        }
    }

    /**
     * This method adds a new location to the list of known locations.
     *
     * @param location String representation of the new location.
     */
    public static void addResourceLocation(String location) {
        if (!containsLocation(location)) {
            _resourceLocations.add(location);
        }
    }

    /**
     * This method adds a new extension to the list of known extensions.
     *
     * @param extension String representation of the new extension.
     */
    public static void addResourceExtension(String extension) {
        if (!containsExtension(extension)) {
            _resourceExtensions.add(extension);
        }
    }

    /**
     * This method removes a location from the list of known locations.
     *
     * @param location String representation of the location to be removed.
     */
    public static void removeResourceLocation(String location) {
        for (Iterator iter = _resourceLocations.iterator(); iter.hasNext(); ) {
            String loc = (String) iter.next();
            if (loc.equals(location)) {
                _resourceLocations.remove(loc);
                break;
            }
        }
    }

    /**
     * This method removes a extension from the list of known extensions.
     *
     * @param extension String representation of the extension to be removed.
     */
    public static void removeResourceExtension(String extension) {
        for (Iterator iter = _resourceExtensions.iterator(); iter.hasNext(); ) {
            String ext = (String) iter.next();
            if (ext.equals(extension)) {
                _resourceExtensions.remove(ext);
                break;
            }
        }
    }

    public static boolean containsExtension(String extension) {
        return _resourceExtensions.contains(extension);
    }

    public static boolean containsLocation(String location) {
        return _resourceLocations.contains(location);
    }

    public static boolean isInCache(String resource) {
        return _resourceCache.containsKey(resource);
    }
}
