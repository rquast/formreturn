package com.ebstrada.formreturn.manager.gef.util;

/**
 * A class that implements Predicate and always returns true. This is useful
 * when you are calling a method that accepts a Predicate to filter or select
 * objects, but you just want all the objects, or the first object.
 */

public class PredicateTrue implements Predicate {

    public PredicateTrue() {
    }

    public boolean predicate(Object obj) {
        return true;
    }

    private static PredicateTrue _theInstance = new PredicateTrue();

    public static PredicateTrue theInstance() {
        return _theInstance;
    }
}
