package com.ebstrada.formreturn.manager.gef.util;

import java.util.Enumeration;

/**
 * Interface used in Set to compute transitive closures. This is basically a
 * Functor interface for generating (expanding) the children of some object.
 */

public interface ChildGenerator extends java.io.Serializable {
    /**
     * Reply a Enumeration of the children of the given Object
     */
    public Enumeration gen(Object o);
} /* end interface ChildGenerator */
