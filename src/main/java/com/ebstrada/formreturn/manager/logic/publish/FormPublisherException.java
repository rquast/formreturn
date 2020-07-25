package com.ebstrada.formreturn.manager.logic.publish;

import com.ebstrada.formreturn.manager.gef.util.Localizer;

public class FormPublisherException extends Exception {

    private static final long serialVersionUID = 1L;

    private int error;

    private String overwriteFilename;

    private String unfoundFilename;

    public static final int INTERRUPTED = 0;
    public static final int NO_ENTITY_MANAGER = 1;
    public static final int NO_SOURCE_DATA_RECORDS_TO_PUBLISH = 2;
    public static final int CANNOT_OVERWITE_FILE = 3;
    public static final int CANNOT_FIND_FILE = 4;

    public FormPublisherException(int error) {
        this.error = error;
    }

    public String getMessage() {
        return getErrorTitle();
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getErrorTitle() {

        String errorTitle = "";

        switch (error) {

            case INTERRUPTED:
                errorTitle = Localizer.localize("UI", "TransactionAbortedMessage");
                break;
            case NO_ENTITY_MANAGER:
                errorTitle = Localizer.localize("UI", "NoEntityManagerMessage");
                break;
            case NO_SOURCE_DATA_RECORDS_TO_PUBLISH:
                errorTitle = Localizer.localize("UI", "NoSourceDataRecordsToPublishMessage");
                break;
            case CANNOT_OVERWITE_FILE:
                errorTitle = String.format(Localizer.localize("UI", "CannotOverwriteFileMessage"),
                    overwriteFilename);
                break;
            case CANNOT_FIND_FILE:
                errorTitle = String
                    .format(Localizer.localize("UI", "CannotFindFileMessage"), unfoundFilename);
                break;
        }

        return errorTitle;

    }

    public void setOverwriteFilename(String overwriteFilename) {
        this.overwriteFilename = overwriteFilename;
    }

    public void setUnfoundFilename(String unfoundFilename) {
        this.unfoundFilename = unfoundFilename;
    }

}
