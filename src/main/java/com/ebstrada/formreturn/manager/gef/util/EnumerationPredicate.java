package com.ebstrada.formreturn.manager.gef.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Step through the elements of some other enumeration, but skip over any
 * elements that do not satisfy the given predicate.
 */

public class EnumerationPredicate implements Enumeration, java.io.Serializable {
    // //////////////////////////////////////////////////////////////
    // instance variables

    /**
     * The normal enumeration that this EnumerationPredicate is moving through.
     */
    protected Enumeration _enum = null;

    /**
     * The predicate that must be satisfied in order for a given element to be
     * returned by nextElement().
     */
    protected Predicate _filter = null;

    /**
     * The element that will be returned on the next call to nextElement(). This
     * element is "on deck".
     */
    protected Object _nextElement = null;

    // //////////////////////////////////////////////////////////////
    // constructors

    public EnumerationPredicate(Enumeration e, Predicate p) {
        _enum = e;
        _filter = p;
        findNext();
    }

    // //////////////////////////////////////////////////////////////
    // Enumeration API

    /**
     * Reply true iff there are more elements in the given enumeration that
     * satisfy the given predicate.
     */
    public boolean hasMoreElements() {
        return _nextElement != null;
    }

    /**
     * Reply the next element in the given enumeration that satisfies the given
     * predicate.
     */
    public Object nextElement() {
        if (!hasMoreElements()) {
            throw (new NoSuchElementException());
        }
        Object res = _nextElement;
        findNext();
        return res;
    }

    /**
     * Internal method to find the next element that satisfies the predicate and
     * store it in _nextElement.
     */
    protected void findNext() {
        _nextElement = null;
        while (_enum.hasMoreElements() && _nextElement == null) {
            Object o = _enum.nextElement();
            if (_filter.predicate(o)) {
                _nextElement = o;
            }
        }
    }
}
