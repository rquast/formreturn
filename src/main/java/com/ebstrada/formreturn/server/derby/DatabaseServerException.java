package com.ebstrada.formreturn.server.derby;

public class DatabaseServerException extends Exception {

    private static final long serialVersionUID = 1L;

    public DatabaseServerException(String msg) {
        super(msg);
    }

    public DatabaseServerException(String msg, Throwable t) {
        super(msg, t);
    }

}
