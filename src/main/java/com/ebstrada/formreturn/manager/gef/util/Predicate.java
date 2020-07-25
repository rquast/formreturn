package com.ebstrada.formreturn.manager.gef.util;

/**
 * Interface for objects that act as predicate functions. For example, if you
 * want to find an object in a Set that fits a certain condition, then write a
 * (anonymous?) class that implements predicate and use it in
 * Set.findSuchThat().
 */

public interface Predicate extends java.io.Serializable {

    public boolean predicate(Object obj);

}
