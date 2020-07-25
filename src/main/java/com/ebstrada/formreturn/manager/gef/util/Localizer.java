package com.ebstrada.formreturn.manager.gef.util;

import java.awt.Toolkit;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class manages the resource bundle files needed to localize the
 * application. All registered resource files are searched in order to find the
 * localization of a given string.
 */

public class Localizer {
    private static Map resourcesByLocale = new HashMap();
    private static Map resourceNames = new HashMap();
    private static Locale defaultLocale = Locale.getDefault();
    private static Map defaultResources = new HashMap();

    static {
        resourcesByLocale.put(defaultLocale, defaultResources);
    }

    private static Log log = LogFactory.getLog(Localizer.class);

    /**
     * This method tests, if a resource with the given name is registered.
     *
     * @param resource Name of the resource to be tested.
     * @return True, if a resource with the given name is registered, otherwise
     * false.
     */
    public static boolean containsResource(String resource) {
        return resourceNames.containsValue(resource);
    }

    /**
     * This method tests, if the given locale is registered.
     *
     * @param locale Locale to be tested.
     * @return True, if the given locale is registered, otherwise false.
     */
    public static boolean containsLocale(Locale locale) {
        return resourcesByLocale.containsKey(locale);
    }

    /**
     * The method addLocale adds a new locale to the set of known locales for
     * the application. For a new locale, all known ResourceBundles are added
     * when possible.
     *
     * @see java.util.ResourceBundle
     * @see java.util.Locale
     */
    public static void addLocale(Locale locale) {
        Map resources = new HashMap();
        Iterator iter = resourceNames.keySet().iterator();

        while (iter.hasNext()) {
            try {
                String binding = (String) iter.next();
                String resourceName = (String) resourceNames.get(binding);
                ResourceBundle bundle = ResourceBundle.getBundle(resourceName, locale);
                if (bundle == null)
                    continue;

                if (bundle instanceof ResourceBundle)
                    resources.put(binding, bundle);
            } catch (MissingResourceException missing) {
                continue;
            }
        }
        resourcesByLocale.put(locale, resources);
    }

    /**
     * The method changes the current locale to the given one. The resources
     * bound to the given locale are also preloaded. If the given locale is not
     * already registered, it will be registered automatically.
     *
     * @see java.util.Locale
     */
    public static void switchCurrentLocale(Locale locale) {
        if (!resourcesByLocale.containsKey(locale))
            addLocale(locale);

        if (!defaultLocale.equals(locale)) {
            defaultLocale = locale;
            defaultResources = (Map) resourcesByLocale.get(locale);
        }
    }

    /**
     * The method returns the current locale.
     *
     * @return The current locale
     */
    public static Locale getCurrentLocale() {
        return defaultLocale;
    }

    /**
     * The method returns all resources for the given locale.
     *
     * @param locale Resources are searched for this locale.
     * @return Map of all resources and their names bound to the given locale.
     */
    public static Map getResourcesFor(Locale locale) {
        if (!containsLocale(locale))
            return null;

        return (Map) resourcesByLocale.get(locale);
    }

    /**
     * The method adds a new resource under the given name. The resource is
     * preloaded and bound to every registered locale.
     *
     * @param resourceName Name of the resource to be registered.
     * @param binding      Name under which the resource should be registered.
     */
    public static synchronized void addResource(String binding, String resourceName)
        throws MissingResourceException {
        addResource(binding, resourceName, Localizer.class.getClassLoader());
    }

    public static synchronized void addResource(String binding, String resourceName,
        ClassLoader loader) throws MissingResourceException {
        if (containsResource(resourceName))
            return;

        Iterator iter = resourcesByLocale.keySet().iterator();

        while (iter.hasNext()) {
            addResource(binding, resourceName, (Locale) iter.next(), loader);
        }
    }

    public static synchronized void addResource(String binding, String resourceName, Locale locale,
        ClassLoader loader) throws MissingResourceException {
        ResourceBundle resource = null;
        if (containsLocale(locale)) {
            Map resources = (Map) resourcesByLocale.get(locale);
            resource = ResourceBundle.getBundle(resourceName, locale, loader);
            resources.put(binding, resource);
            if (!resourceNames.containsValue(resourceName))
                resourceNames.put(binding, resourceName);
        } else
            throw new MissingResourceException("Locale not found!", locale.toString(),
                resourceName);
    }

    /**
     * The method removes the given resource from the list of used resources.
     * Any binding from any locale to that resource is also removed.
     *
     * @param binding Name under which the resource to be removed is registered.
     */
    public static void removeResource(String binding) {
        Iterator iter = resourcesByLocale.keySet().iterator();

        while (iter.hasNext()) {
            Locale tmpLocale = (Locale) iter.next();
            ((Map) resourcesByLocale.get(tmpLocale)).remove(binding);
        }
        resourceNames.remove(binding);
    }

    /**
     * This function returns a localized string corresponding to the specified
     * key. Searching goes through all registered ResourceBundles
     *
     * @param binding ResourceBundles to search in.
     * @param key     String to be localized.
     * @return First localization for the given string found in the registered
     * ResourceBundles, the key itself if no localization has been
     * found.
     */
    public static String localize(String binding, String key) {
        return localize(binding, key, defaultLocale, defaultResources);
    }

    public static String localize(String binding, String key, boolean localize) {
        return localize(binding, key, defaultLocale, defaultResources, localize);
    }

    public static String localize(String binding, String key, Locale locale, Map resources,
        boolean localize) {
        if (localize) {
            return localize(binding, key, locale, resources);
        } else {
            return key;
        }
    }

    public static String localize(String binding, String key, Locale locale, Map resources) {
        boolean showErrors = false;

        if (locale == null || resources == null || !containsLocale(locale)) {
            if (showErrors) {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    log.warn("Localization failed for key " + key + " (binding: " + binding + ")",
                        e);
                }
            }
            return key;
        }

        String localized = null;

        ResourceBundle resource = (ResourceBundle) resources.get(binding);
        if (resource == null) {
            if (showErrors) {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    log.warn("Localization failed for key " + key + " (binding: " + binding + ")",
                        e);
                }
            }
            return key;
        }

        try {
            localized = resource.getString(key);
        } catch (MissingResourceException e) {
        }
        if (localized == null) {
            if (showErrors) {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    log.warn("Localization failed for key " + key + " (binding: " + binding + ")",
                        e);
                }
            }
            localized = key;
        }

        return localized;
    }

    /**
     * AWT has no standard way to name the platforms default menu shortcut
     * modifier for a KeyStroke, so the localizer replaces each occurence of
     * "shortcut" with Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
     */
    protected final static String SHORTCUT_MODIFIER = "shortcut";

    /**
     * This function returns a localized menu shortcut key to the specified key.
     *
     * @param binding Name of resource to be searched.
     * @param key     Shortcut string to be localized.
     * @return Localized KeyStroke object.
     */
    public static KeyStroke getShortcut(String binding, String key) {
        return getShortcut(binding, key, defaultLocale, defaultResources);
    }

    public static KeyStroke getShortcut(String binding, String key, Locale locale, Map resources) {
        if (locale == null || resources == null || !containsLocale(locale))
            return null;

        KeyStroke stroke = null;
        ResourceBundle resource = (ResourceBundle) resources.get(binding);
        try {
            Object obj = resource.getObject(key);
            if (obj instanceof KeyStroke) {
                stroke = (KeyStroke) obj;
            } else if (obj instanceof String) {
                boolean hasShortcutModifier = false;
                StringBuffer shortcutBuf = new StringBuffer();

                StringTokenizer tokenizer = new StringTokenizer((String) obj);
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();

                    if (token.equals(SHORTCUT_MODIFIER)) {
                        hasShortcutModifier = true;
                    } else {
                        shortcutBuf.append(token);
                        shortcutBuf.append(" ");
                    }
                }
                stroke = KeyStroke.getKeyStroke(shortcutBuf.toString());
                int modifiers = stroke.getModifiers() | (hasShortcutModifier ?
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() :
                    0);
                int keyCode = stroke.getKeyCode();
                stroke = KeyStroke.getKeyStroke(keyCode, modifiers);
            }
        } catch (MissingResourceException e) {
        } catch (ClassCastException e) {

        } catch (NullPointerException e) {
        }
        return stroke;
    }
} /* end class Localizer */
