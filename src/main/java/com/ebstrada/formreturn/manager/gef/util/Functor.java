package com.ebstrada.formreturn.manager.gef.util;


/**
 * Interface to define Functor objects. Functor's are described in many papers
 * and books about design patterns or coding idioms. They are basically
 * functions, but implemented as instances of a class with only one method. That
 * allows passing the Functor object around, whereas Java does not allow any
 * operations on functions.
 */

public interface Functor extends java.io.Serializable {
    public Object apply(Object x);
}
