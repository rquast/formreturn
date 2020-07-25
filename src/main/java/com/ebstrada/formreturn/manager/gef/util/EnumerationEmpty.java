package com.ebstrada.formreturn.manager.gef.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A enumeration that is always empty. Functially equivelant to:
 *
 * <code>(new Vector()).elements();</code>
 * <p>
 * This is useful when you must pass or return an enumeration, but you do not
 * have any elements.
 */

public class EnumerationEmpty implements Enumeration, Iterator, java.io.Serializable {

    public boolean hasMoreElements() {
        return false;
    }

    public Object nextElement() {
        throw new NoSuchElementException();
    }

    protected static EnumerationEmpty _theInstance = new EnumerationEmpty();

    public static EnumerationEmpty theInstance() {
        return _theInstance;
    }

    // Implementing the Iterator interface:
    public boolean hasNext() {
        return hasMoreElements();
    }

    ;

    public Object next() {
        return nextElement();
    }

    ;

    public void remove() {
        throw new java.util.NoSuchElementException();
    }

    ;

} /* end class EnumerationEmpty */
