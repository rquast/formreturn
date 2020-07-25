package com.ebstrada.formreturn.manager.logic.recognition.reader;

import java.awt.image.BufferedImage;

import com.ebstrada.formreturn.manager.gef.util.Localizer;

public class FormReaderException extends Exception {

    private static final long serialVersionUID = 1L;

    private int error;

    private String capturedDataFieldName = "";

    public static final int UNSPECIFIED = 0;
    public static final int MISSING_FORM_ID_BARCODE = 1;

    public static final int MISSING_SEGMENT_BARCODE = 2;

    public String missingBarcodeData = "";

    public static final int MISSING_CHECKBOX = 3;

    public String missingCheckboxFieldName = "";

    public static final int RECONCILIATION_KEY_NOT_FOUND = 4;

    public String reconciliationKeyNotFound = "";

    public static final int FORM_PAGE_RECORD_MISSING = 5;

    public long missingFormPageRecord = 0;

    public static final int INVALID_IMAGE_FORMAT = 6;

    public String invalidImageFormat = "";

    public static final int FORM_ID_NOT_FOUND = 7;

    public String formIDNotFoundData = "";

    public static final int INVALID_AGGREGATION_RULE = 8;

    public static final int ERROR_CONDITION_MET = 9;

    public static final int FORM_PAGE_DUPLICATE_SCAN = 10;

    private BufferedImage invalidImage;

    public FormReaderException(int error) {
        this.error = error;
    }

    public FormReaderException(String strMessage) {
        this(UNSPECIFIED, strMessage);
    }

    public FormReaderException(int error, String strMessage) {
        super(strMessage);
        this.error = error;
    }

    public String getMessage() {
        return getErrorTitle();
    }

    public String getErrorTitle() {

        String title = "";

        switch (error) {
            case UNSPECIFIED:
                title = Localizer.localize("UI", "UnspecifiedProcessingError");
                break;
            case MISSING_FORM_ID_BARCODE:
                title = Localizer.localize("UI", "BarcodeReaderUnableToLocateFormIDMessage");
                break;
            case MISSING_SEGMENT_BARCODE:
                title = Localizer.localize("UI", "SegmentBarcodeNotFound") + ": "
                    + getMissingBarcodeData();
                break;
            case MISSING_CHECKBOX:
                title = String
                    .format(Localizer.localize("UI", "OMRMatrixCheckboxCountIncorrectMessage"),
                        getCapturedDataFieldName());
                break;
            case RECONCILIATION_KEY_NOT_FOUND:
                title =
                    Localizer.localize("UI", "FormReaderUnableToIdentifyReconciliationKeyMessage");
                break;
            case FORM_PAGE_RECORD_MISSING:
                title = Localizer.localize("UI", "FormReaderNoFormPageLoadedMessage");
                break;
            case INVALID_IMAGE_FORMAT:
                title = Localizer.localize("Server", "InvalidImageFileFormatMessage");
                break;
            case FORM_ID_NOT_FOUND:
                title = Localizer.localize("UI", "FormReaderInvalidFormPageBarcodeMessage");
                break;
            case INVALID_AGGREGATION_RULE:
                title = Localizer.localize("UI", "InvalidAggreationRuleMessageText");
                break;
            case ERROR_CONDITION_MET:
                title = Localizer.localize("UI", "ErrorFlagAggregationRuleMessageText");
                break;
            case FORM_PAGE_DUPLICATE_SCAN:
                title = Localizer.localize("UI", "FormReaderErrorDuplicateScansMessage");
                break;
        }

        return title;
    }

    public String getErrorMessage() {
        return "";
    }

    public String toString() {
        return "FormReaderException[" + error + "]";
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMissingBarcodeData() {
        return missingBarcodeData;
    }

    public void setMissingBarcodeData(String missingBarcodeData) {
        this.missingBarcodeData = missingBarcodeData;
    }

    public String getMissingCheckboxFieldName() {
        return missingCheckboxFieldName;
    }

    public void setMissingCheckboxFieldName(String missingCheckboxFieldName) {
        this.missingCheckboxFieldName = missingCheckboxFieldName;
    }

    public String getReconciliationKeyNotFound() {
        return reconciliationKeyNotFound;
    }

    public void setReconciliationKeyNotFound(String reconciliationKeyNotFound) {
        this.reconciliationKeyNotFound = reconciliationKeyNotFound;
    }

    public long getMissingFormPageRecord() {
        return missingFormPageRecord;
    }

    public void setMissingFormPageRecord(long missingFormPageRecord) {
        this.missingFormPageRecord = missingFormPageRecord;
    }

    public String getInvalidImageFormat() {
        return invalidImageFormat;
    }

    public void setInvalidImageFormat(String invalidImageFormat) {
        this.invalidImageFormat = invalidImageFormat;
    }

    public String getFormIDNotFoundData() {
        return formIDNotFoundData;
    }

    public void setFormIDNotFoundData(String formIDNotFoundData) {
        this.formIDNotFoundData = formIDNotFoundData;
    }

    public String getCapturedDataFieldName() {
        return capturedDataFieldName;
    }

    public void setCapturedDataFieldName(String capturedDataFieldName) {
        this.capturedDataFieldName = capturedDataFieldName;
    }

    public String getErrorData() {
        // TODO Auto-generated method stub

        // TODO: MAKE THIS RETURN XML DATA OF WHATEVER NEEDS TO BE SERIALIZED

        return null;
    }

    public void setInvalidImage(BufferedImage invalidImage) {
        this.invalidImage = invalidImage;
    }

    public BufferedImage getInvalidImage() {
        return this.invalidImage;
    }

}
