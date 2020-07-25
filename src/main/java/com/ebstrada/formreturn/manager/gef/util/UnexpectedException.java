package com.ebstrada.formreturn.manager.gef.util;

public class UnexpectedException extends RuntimeException {
    // //////////////////////////////////////////////////////////////
    // instance variables
    private Throwable nestedException;

    // //////////////////////////////////////////////////////////////
    // constructors
    public UnexpectedException(String s, Throwable t) {
        super(s);
        nestedException = t;
    }

    public UnexpectedException(Throwable e) {
        super();
        nestedException = e;
    }

    // //////////////////////////////////////////////////////////////
    // accessors
    public Throwable getNestedException() {
        return nestedException;
    }

}
