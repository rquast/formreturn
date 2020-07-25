package com.ebstrada.formreturn.manager.gef.util;

public class PredicateEquals implements Predicate {
    protected Object _pattern;

    public PredicateEquals(Object p) {
        _pattern = p;
    }

    public boolean predicate(Object obj) {
        if (_pattern == null) {
            return obj == null;
        }
        return _pattern.equals(obj);
    }
}
