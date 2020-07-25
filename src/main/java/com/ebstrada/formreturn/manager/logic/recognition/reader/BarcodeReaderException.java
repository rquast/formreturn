package com.ebstrada.formreturn.manager.logic.recognition.reader;

public class BarcodeReaderException extends Exception {

    private static final long serialVersionUID = 1L;

    private int intError;

    BarcodeReaderException(int intErrNo) {
        intError = intErrNo;
    }

    BarcodeReaderException(String strMessage) {
        super(strMessage);
    }

    public String toString() {
        return "BarcodeReaderException[" + intError + "]";
    }

}
