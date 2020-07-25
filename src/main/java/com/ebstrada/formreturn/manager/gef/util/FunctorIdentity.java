package com.ebstrada.formreturn.manager.gef.util;


/**
 * A useful implementation of interface Functor that simply returns the same
 * object that was passed to it. Use this class when you want to use a method
 * that takes a Functor, but you do not actually want to apply any function.
 */

public class FunctorIdentity implements Functor {
    public Object apply(Object x) {
        return x;
    }
}
