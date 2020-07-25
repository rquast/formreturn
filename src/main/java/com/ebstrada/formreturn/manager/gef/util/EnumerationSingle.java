package com.ebstrada.formreturn.manager.gef.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * A enumeration that has exactly one element. Functially equivelant to:
 *
 * <code>
 * Vector v = new Vector();
 * v.addElement(obj);
 * return v.elements();
 * </code>
 * <p>
 * This is useful when you must pass or return an enumeration, but you do not
 * have many elements.
 */

public class EnumerationSingle implements Enumeration, java.io.Serializable {
    Object _element = null;

    public EnumerationSingle(Object ele) {
        _element = ele;
    }

    public boolean hasMoreElements() {
        return _element != null;
    }

    public Object nextElement() {
        if (_element != null) {
            Object o = _element;
            _element = null;
            return o;
        } else {
            throw new NoSuchElementException();
        }
    }
}
