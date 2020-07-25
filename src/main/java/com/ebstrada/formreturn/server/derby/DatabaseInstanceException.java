package com.ebstrada.formreturn.server.derby;

public class DatabaseInstanceException extends Exception {

    private static final long serialVersionUID = 1L;

    public DatabaseInstanceException(String msg) {
        super(msg);
    }

    public DatabaseInstanceException(String msg, Throwable t) {
        super(msg, t);
    }

}
