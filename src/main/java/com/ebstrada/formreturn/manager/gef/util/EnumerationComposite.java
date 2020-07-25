package com.ebstrada.formreturn.manager.gef.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * This class concatenates Enumerations. Successive calls to nextElement return
 * elements from each Enumeration until that enumeration is exhausted.
 */

public class EnumerationComposite implements Enumeration, java.io.Serializable {
    // //////////////////////////////////////////////////////////////
    // instance variables

    /**
     * The enumerations being concatenated
     */
    private Vector _subs = new Vector();

    /**
     * The next element to return from nextElement().
     */
    private Object _nextElement = null;

    // //////////////////////////////////////////////////////////////
    // constructors
    public EnumerationComposite() {
    }

    public EnumerationComposite(Enumeration e1) {
        addSub(e1);
    }

    public EnumerationComposite(Enumeration e1, Enumeration e2) {
        addSub(e1);
        addSub(e2);
    }

    public EnumerationComposite(Enumeration e1, Enumeration e2, Enumeration e3) {
        addSub(e1);
        addSub(e2);
        addSub(e3);
    }

    /**
     * Concatenate the given Enumeration to the end of the receiving
     * EnumerationComposite.
     */
    public void addSub(Enumeration e) {
        if (e != null && e.hasMoreElements()) {
            _subs.addElement(e);
            findNext();
        }
    }

    /**
     * Concatenate the elements() of the given Vector to the end of the
     * receiving EnumerationComposite.
     */
    public void addSub(Vector v) {
        if (v != null) {
            addSub(v.elements());
        }
    }

    /**
     * Reply true iff this EnumerationComposite has more elements.
     */
    public boolean hasMoreElements() {
        return _nextElement != null;
    }

    /**
     * Reply the next element, or raise an execption if there is none.
     */
    public Object nextElement() {
        if (!hasMoreElements()) {
            throw new NoSuchElementException();
        }
        Object res = _nextElement;
        _nextElement = null;
        findNext();
        return res;
    }

    /**
     * Internal function to find the element to return on the next call to
     * nextElement().
     */
    protected void findNext() {
        if (_nextElement != null) {
            return;
        }
        while (!_subs.isEmpty() && !((Enumeration) _subs.firstElement()).hasMoreElements()) {
            _subs.removeElementAt(0);
        }
        if (!_subs.isEmpty()) {
            _nextElement = ((Enumeration) _subs.firstElement()).nextElement();
        }
    }
} /* end class EnumerationComposite */
