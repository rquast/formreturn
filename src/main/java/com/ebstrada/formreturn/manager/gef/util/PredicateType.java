package com.ebstrada.formreturn.manager.gef.util;

public class PredicateType implements Predicate {

    // //////////////////////////////////////////////////////////////
    // instance variables
    Class _patterns[];
    int _numPats;
    String _printString = null;

    // //////////////////////////////////////////////////////////////
    // constructor
    protected PredicateType(Class pats[]) {
        this(pats, pats.length);
    }

    protected PredicateType(Class pats[], int numPats) {
        _patterns = pats;
        _numPats = numPats;
    }

    public static PredicateType create() {
        return new PredicateType(null, 0);
    }

    public static PredicateType create(Class c0) {
        Class classes[] = new Class[1];
        classes[0] = c0;
        return new PredicateType(classes);
    }

    public static PredicateType create(Class c0, Class c1) {
        Class classes[] = new Class[2];
        classes[0] = c0;
        classes[1] = c1;
        return new PredicateType(classes);
    }

    public static PredicateType create(Class c0, Class c1, Class c2) {
        Class classes[] = new Class[3];
        classes[0] = c0;
        classes[1] = c1;
        classes[2] = c2;
        return new PredicateType(classes);
    }

    // //////////////////////////////////////////////////////////////
    // Predicate implementation
    public boolean predicate(Object o) {
        if (_numPats == 0) {
            return true;
        }
        for (int i = 0; i < _numPats; i++) {
            if (_patterns[i].isInstance(o)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPredicateFor(Object o) {
        for (int i = 0; i < _numPats; i++) {
            if (_patterns[i].equals(o)) {
                return true;
            }
        }
        return false;
    }

    // //////////////////////////////////////////////////////////////
    // printing

    @Override public String toString() {
        if (_printString != null) {
            return _printString;
        }
        if (_numPats == 0) {
            return "Any Type";
        }
        String res = "";
        for (int i = 0; i < _numPats; i++) {
            String clsName = _patterns[i].getName();
            int lastDot = clsName.lastIndexOf(".");
            clsName = clsName.substring(lastDot + 1);
            res += clsName;
            if (i < _numPats - 1) {
                res += ", ";
            }
        }
        _printString = res;
        return res;
    }

}
