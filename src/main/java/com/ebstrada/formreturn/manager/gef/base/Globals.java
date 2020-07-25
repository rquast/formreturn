package com.ebstrada.formreturn.manager.gef.base;

import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MediaTracker;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ebstrada.formreturn.manager.gef.ui.IStatusBar;

/**
 * This class stores global info that is needed by all Editors. For example, it
 * aids in communication between the various Palette's and Editor's by holding
 * the next global Mode.
 */

public class Globals {

    // //////////////////////////////////////////////////////////////
    // constants

    public static final String REMOVE = "remove";
    // public static final String HIGHLIGHT = "highlight";
    // public static final String UNHIGHLIGHT = "unhighlight";

    // //////////////////////////////////////////////////////////////
    // applet related methods

    protected static Applet _applet;
    protected static MediaTracker _tracker;

    // protected static TabPropFrame _tabPropFrame;

    /**
     * String to display when nothing important is being displayed. By default
     * the string " " is used. Some applications may want to change this to
     * "Ready." It should not be set to "", because that can mess up window
     * layouts, causing the status line to disappear.
     */
    public static String defaultStatus = "  ";

    // private static Log LOG = LogFactory.getLog(Globals.class);

    /**
     * If we are running as an applet, Store the Applet and AppletContext in a
     * well known place.
     */
    public static void setApplet(Applet a) {
        _applet = a;
        clearStatus();
        _tracker = new MediaTracker(a);
    }

    public static AppletContext getAppletContext() {
        try {
            return _applet.getAppletContext();
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public static Applet getApplet() {
        return _applet;
    }

    /**
     * Until jdk 1.2, this will be our clipboard TODO we have now gone beyond
     * JDK1.2
     */
    public static Vector clipBoard;

    public static boolean pastable = false;

    /*
     * The directory most recently used in an open or save dialog
     */
    private static String LastDirectory = null;

    public static String getLastDirectory() {
        if (LastDirectory == null) {
            LastDirectory = System.getProperty("user.home");
        }
        return LastDirectory;
    }

    public static void setLastDirectory(String s) {

        File lastDir = new File(s);

        if (lastDir.isDirectory()) {

            LastDirectory = s;

        } else if (lastDir.isFile()) {

            File parentFile = lastDir.getParentFile();

            LastDirectory = parentFile.getPath();

        }

    }

    /**
     * Determines if tool tips be are shown on Figs in diagrams, including
     * ToDoItem headlines.
     */
    public static boolean ShowFigTips = true;

    public static boolean getShowFigTips() {
        return ShowFigTips;
    }

    public static void setShowFigTips(boolean b) {
        ShowFigTips = b;
    }

    /**
     * The place where status messages will be written.
     */
    public static IStatusBar _StatusBar = null;

    /**
     * Sets the place where status messages will be written.
     */
    public static void setStatusBar(IStatusBar sb) {
        _StatusBar = sb;
    }

    /**
     * Show a string on the some status bar, or print a message on the console.
     */
    public static void showStatus(String s) {
        if (_StatusBar != null) {
            _StatusBar.showStatus(s);
        } else if (getAppletContext() != null) {
            _applet.getAppletContext().showStatus(s);
        }
        // else System.out.println(s);
    }

    /**
     * Sets the status to its default (blank) string.
     */
    public static void clearStatus() {
        showStatus(defaultStatus);
    }

    /**
     * If we are running as an applet, ask the browser to display the given URL.
     */
    public static void showDocument(URL url) {
        if (getAppletContext() != null) {
            _applet.getAppletContext().showDocument(url);
        }
    }

    /**
     * If we are running as an applet, ask the browser to display the given URL.
     */
    public static void showDocument(URL url, String target) {
        if (getAppletContext() != null) {
            _applet.getAppletContext().showDocument(url, target);
        }
    }

    /**
     * If we are running as an applet, ask the browser to display the given URL.
     */
    public static void showDocument(String urlString) {
        try {
            showDocument(new URL(urlString));
        } catch (MalformedURLException ignore) {
        }
    }

    /**
     * Get an image from the given URL (usually a gif file).
     */
    public static Image getImage(URL url) {
        Image img = null;
        if (getAppletContext() != null) {
            img = getAppletContext().getImage(url);
        }
        if (_tracker != null && img != null) {
            _tracker.addImage(img, 1);
        }
        return img;
    }

    /**
     * Get an image from the given URL (usually a gif file).
     */
    public static Image getImage(String urlStr) {
        try {
            Image img = null;
            if (getAppletContext() != null) {
                img = getAppletContext().getImage(new URL(urlStr));
            }
            if (_tracker != null && img != null) {
                _tracker.addImage(img, 1);
            }
            return img;
        } catch (java.net.MalformedURLException e) {
            return null;
        }
    }

    /**
     * Wait for all images to download.
     */
    public static void waitForImages() {
        if (_tracker == null) {
            return;
        }
        try {
            _tracker.waitForAll();
        } catch (InterruptedException e) {
        }
    }

    // //////////////////////////////////////////////////////////////
    // user preferences

    /**
     * The user's preferences for various editor features.
     */
    protected static Prefs _prefs = new Prefs();

    /**
     * Reply the user's preferences.
     */
    public static Prefs getPrefs() {
        return _prefs;
    }

    /**
     * Return an exisiting instance of class Frame. This is needed to create
     * off-screen bit-maps. Needs-more-work: I think Swing keeps its own.
     */
    public static Frame someFrame() {
        Editor ce = curEditor();
        if (ce == null) {
            return null;
        }
        Component c = ce.getJComponent();
        while (c != null && !(c instanceof Frame)) {
            c = c.getParent();
        }
        return (Frame) c;
    }

    // //////////////////////////////////////////////////////////////
    // Editor Modes

    /**
     * True if the next global Mode should remain as the next global mode even
     * after it has been given to one editor.
     */
    protected static boolean _sticky = false;

    /**
     * Set whether the next global mode should remain as the next global mode
     * even after it has been given to one editor.
     */
    public static void setSticky(boolean b) {
        _sticky = b;
    }

    public static boolean getSticky() {
        return _sticky;
    }

    /**
     * The next global mode. This is given to an editor on mouse entry
     */
    protected static Mode _mode;

    /**
     * What mode should Editor's go into if the Palette is not requesting a
     * specific mode?
     */
    protected static Mode defaultMode() {
        return new ModeSelect();
    }

    /**
     * Set the next global mode.
     */
    public static void mode(Mode m) {
        _mode = m;
    }

    /**
     * Set the next global mode, and set it's stickiness.
     */
    public static void mode(Mode m, boolean s) {
        _mode = m;
        _sticky = s;
    }

    /**
     * Reply the next global mode.
     */
    public static Mode mode() {
        if (_mode == null) {
            _mode = defaultMode();
        }
        return _mode;
    }

    /**
     * Determine the next global mode. This is called when a mode finishes
     * whatever it was meant to accomplish. If the sticky flag is set, the
     * current mode stays the next global mode. Otherwise the defaultMode
     * becomes the next global mode.
     *
     * @see Mode#done
     */
    public static Mode nextMode() {
        if (!_sticky) {
            _mode = defaultMode();
        }
        return _mode;
    }

    // //////////////////////////////////////////////////////////////
    // Methods that keep track of the current editor

    /**
     * The Editor that most recently contained the mouse.
     */
    protected static Editor _curEditor;

    /**
     * Set the current Editor.
     */
    public static void curEditor(Editor ce) {
        _curEditor = ce;
    }

    /**
     * Reply the Editor that most recently contained the mouse.
     */
    public static Editor curEditor() {
        return _curEditor;
    }

    // //////////////////////////////////////////////////////////////
    // a light-weight data-structure for tracking PropertyChangeListeners

    /**
     * A global dictionary of PropertyChangeListeners for Figs. Most Figs will
     * not have any listeners at any given moment, so I did not want to allocate
     * an instance variable to hold listeners. Instead I use this global
     * Hashtable with Figs ans keys and arrays of up to 4 listeners as values.
     * <p>
     * <p>
     * Note: It is important that all listeners eventually remove themselves by
     * calling removePropertyChangeListener. Otherwise this table will keep
     * pointers that can reduce grabage collection.
     */

    protected static HashMap _pcListeners = new HashMap();
    protected static PropertyChangeListener universalListener = null;

    /**
     * The most listeners a Fig can have, 4.
     */
    public static int MAX_LISTENERS = 4;

    /**
     * Add a listener to a Fig. Now the listener will get notifications of all
     * property change events from that Fig.
     */
    public static void addPropertyChangeListener(Object src, PropertyChangeListener l) {
        PropertyChangeListener listeners[] = (PropertyChangeListener[]) _pcListeners.get(src);
        if (listeners == null) {
            listeners = new PropertyChangeListener[MAX_LISTENERS];
            _pcListeners.put(src, listeners);
        }
        // debugging warning
        // if (LOG.isDebugEnabled() && _pcListeners.size() > 100) {
        // LOG.debug("_pcListeners size = " + _pcListeners.size());
        // }
        for (int i = 0; i < MAX_LISTENERS; ++i) {
            if (listeners[i] == null) {
                listeners[i] = l;
                return;
            }
        }
        // if (LOG.isDebugEnabled()) {
        // LOG.debug("ran out of listeners!");
        // }
    }

    public static void addUniversalPropertyChangeListener(PropertyChangeListener pcl) {
        universalListener = pcl;
    }

    public static void removeUniversalPropertyChangeListener() {
        universalListener = null;
    }

    public static void removePropertyChangeListener(Object s, PropertyChangeListener listener) {
        PropertyChangeListener listeners[] = (PropertyChangeListener[]) _pcListeners.get(s);
        boolean found = false;
        if (listeners == null) {
            return;
        }
        for (int i = 0; i < MAX_LISTENERS; ++i) {
            if (listeners[i] == listener) {
                listeners[i] = null;
                found = true;
            }
        }
        // if (LOG.isDebugEnabled() && !found) {
        // LOG.debug("listener not found!");
        // }
        for (int i = 0; i < MAX_LISTENERS; ++i) {
            if (listeners[i] != null) {
                return;
            }
        }
        // s has no listeners, keep Hashtable size reasonable
        _pcListeners.remove(s);
    }

    /**
     * Send a property change event to listeners of the src Fig.
     */
    public static void firePropChange(Object src, String propName, boolean oldV, boolean newV) {
        firePropChange(src, propName, new Boolean(oldV), new Boolean(newV));
    }

    /**
     * Send a property change event to listeners of the src Fig.
     */
    public static void firePropChange(Object src, String propName, int oldV, int newV) {
        firePropChange(src, propName, new Integer(oldV), new Integer(newV));
    }

    /**
     * Send a property change event to listeners of the src Fig.
     */
    public static void firePropChange(Object src, String propName, Object oldValue,
        Object newValue) {
        if (oldValue != null && oldValue.equals(newValue)) {
            return;
        }
        PropertyChangeListener listeners[] = (PropertyChangeListener[]) _pcListeners.get(src);
        if (listeners == null && universalListener == null) {
            return;
        }
        PropertyChangeEvent evt = new PropertyChangeEvent(src, propName, oldValue, newValue);
        if (listeners != null) {
            // needs-more-work: should be thread safe, clone array?
            for (int i = 0; i < MAX_LISTENERS; ++i) {
                if (listeners[i] != null) {
                    listeners[i].propertyChange(evt);
                }
            }
        }
        if (universalListener != null) {
            universalListener.propertyChange(evt);
        }
    }

} /* end class Globals */
