package com.ebstrada.formreturn.manager.gef.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Util {

    /**
     * No public constructor, this is a utility class with all static methods,
     * so you never need an instance.
     */
    private Util() {
    }

    /**
     * Fixes a platform dependent filename to standard URI form.
     *
     * @param str The string to fix.
     * @return Returns the fixed URI string.
     */
    public static final String filenameToURI(String str) {
        // handle platform dependent strings
        str = str.replace(java.io.File.separatorChar, '/');
        // Windows fix
        if (str.length() >= 2) {
            if (str.charAt(1) == ':') {
                char ch0 = Character.toUpperCase(str.charAt(0));
                if (ch0 >= 'A' && ch0 <= 'Z') {
                    str = "/" + str;
                }
            }
        }
        return str;
    }

    public static final URL fileToURL(File file) throws MalformedURLException, IOException {
        return new URL("file", "", filenameToURI(file.getCanonicalPath()));
    }

    public static final URL fixURLExtension(URL url, String desiredExtension) {
        if (!url.getFile().endsWith(desiredExtension)) {
            try {
                url = new URL(url, url.getFile() + desiredExtension);
            } catch (java.net.MalformedURLException e) {
                throw new UnexpectedException(e);
            }
        }
        return url;
    }

    public static final URL exchangeURLExtension(URL url, String desiredExtension,
        String oldExtension) {
        if (!url.getFile().endsWith(oldExtension)) {
            // System.out.println("[GEF.Util] exchangeURLExtension: no exchange
            // " + url.getFile());
            return Util.fixURLExtension(url, desiredExtension);
        } else {
            try {
                // System.out.println("[GEF.Util] exchangeURLExtension:
                // exchange");
                String newURL = url.getFile();
                newURL = newURL.substring(0, newURL.lastIndexOf('.'));
                // System.out.println("[GEF.Util] exchangeURLExtension: new url
                // = " + newURL);
                url = new URL(url, newURL);
                // System.out.println("[GEF.Util] exchangeURLExtension:
                // exchanged " + url.getFile());
            } catch (java.net.MalformedURLException mue) {
                throw new UnexpectedException(mue);
            }
        }
        return url;
    }

    /*
     * Strip all characters out of <var>s</var> that could not be part of a
     * valid Java identifier. Return either the given string (if all characters
     * were valid), or a new string with all invalid characters stripped out.
     */
    public static final String stripJunk(String s) {
        int len = s.length();
        int pos = 0;
        for (int i = 0; i < len; i++, pos++) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                break;
            }
        }
        if (pos == len) {
            return s;
        }

        StringBuffer buf = new StringBuffer(len);
        for (int i = 0; i < pos; i++) {
            buf.append(s.charAt(i));
        }

        // skip pos, we know it's not a valid char from above
        for (int i = pos + 1; i < len; i++) {
            char c = s.charAt(i);
            if (Character.isJavaIdentifierPart(c)) {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    /**
     * Fixes a platform dependent filename to standard URI form.
     *
     * @param str The string to fix.
     * @return Returns the fixed URI string.
     */
    public static final String URIToFilename(String str) {
        // Windows fix
        if (str.length() >= 3) {
            if (str.charAt(0) == '/' && str.charAt(2) == ':') {
                char ch1 = Character.toUpperCase(str.charAt(1));
                if (ch1 >= 'A' && ch1 <= 'Z') {
                    str = str.substring(1);
                }
            }
        }
        // handle platform dependent strings
        str = str.replace('/', java.io.File.separatorChar);
        return str;
    }

    public static final File URLToFile(URL url) throws MalformedURLException {
        if (!"file".equals(url.getProtocol())) {
            throw new MalformedURLException("URL protocol must be 'file'.");
        }
        return new File(URIToFilename(url.getFile()));
    }

    public static final String URLToShortName(URL url) {
        String name = url.getFile();
        return name.substring(name.lastIndexOf('/') + 1);
    }

}
