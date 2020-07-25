package com.ebstrada.formreturn.manager.gef.util;

import java.util.Enumeration;

/**
 * This Class is a utility to convert java.util.*-classes to java.util.*-classes
 */

public class Converter {

    public static java.util.Hashtable convert(java.util.Hashtable oldOne) {
        if (oldOne == null) {
            return null;
        }
        java.util.Hashtable newOne = new java.util.Hashtable();
        Enumeration oldKeys = oldOne.keys();
        while (oldKeys.hasMoreElements()) {
            Object o = oldKeys.nextElement();
            newOne.put(o, oldOne.get(o));
        }

        return newOne;
    }

    public static java.util.Vector convert(java.util.Vector oldOne) {
        if (oldOne == null) {
            return null;
        }
        java.util.Vector newOne = new java.util.Vector();
        for (int i = 0; i < oldOne.size(); i++) {
            newOne.addElement(oldOne.elementAt(i));
        }
        return newOne;
    }

    public static java.util.Vector convertCollection(java.util.Collection oldCol) {
        if (oldCol == null) {
            return null;
        }

        java.util.Vector newVec = new java.util.Vector();
        java.util.Iterator iter = oldCol.iterator();
        while (iter.hasNext()) {
            newVec.addElement(iter.next());
        }
        return newVec;
    }
}
/* end class Converter */
