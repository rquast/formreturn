package com.ebstrada.formreturn.manager.ui.cdm.dialog;

import com.ebstrada.formreturn.manager.gef.util.Localizer;

public class ColumnOption {

    private int type;

    private String description;

    private String title;

    private String fieldName;

    public static final int COLUMN_FORM_PAGE_IDS = 12;

    public static final int COLUMN_FORM_PASSWORD = 11;

    public static final int COLUMN_SCANNED_PAGE_NUMBER = 10;

    public static final int COLUMN_CAPTURE_TIME = 9;

    public static final int COLUMN_SEGMENT_SCORE = 8;

    public static final int COLUMN_INDIVIDUAL_SCORES = 7;

    public static final int COLUMN_CAPTURED_DATA = 6;

    public static final int COLUMN_SOURCE_DATA = 5;

    public static final int COLUMN_IMAGE_FILE_NAMES = 4;

    public static final int COLUMN_PROCESSED_TIME = 3;

    public static final int COLUMN_FORM_SCORE = 2;

    public static final int COLUMN_FORM_ID = 1;

    public static final int COLUMN_PUBLICATION_ID = 0;

    public ColumnOption(int type) {
        setType(type);
    }

    public String getDescription() {
        return description;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(int type) {
        this.type = type;

        switch (type) {

            case ColumnOption.COLUMN_PUBLICATION_ID:
                this.title = Localizer.localize("UICDM", "ColumnOptionPublicationIdTitle");
                this.description = Localizer.localize("UICDM", "ColumnOptionPublicationIdMessage");
                break;

            case ColumnOption.COLUMN_FORM_ID:
                this.title = Localizer.localize("UICDM", "ColumnOptionFormIdTitle");
                this.description = Localizer.localize("UICDM", "ColumnOptionFormIdMessage");
                break;

            case ColumnOption.COLUMN_FORM_PASSWORD:
                this.title = Localizer.localize("UICDM", "ColumnOptionFormPasswordTitle");
                this.description = Localizer.localize("UICDM", "ColumnOptionFormPasswordMessage");
                break;

            case ColumnOption.COLUMN_FORM_PAGE_IDS:
                this.title = Localizer.localize("UICDM", "ColumnOptionFormPageIDsTitle");
                this.description = Localizer.localize("UICDM", "ColumnOptionFormPageIDsMessage");
                break;

            case ColumnOption.COLUMN_FORM_SCORE:
                this.title = Localizer.localize("UICDM", "ColumnOptionFormScoreTitle");
                this.description = Localizer.localize("UICDM", "ColumnOptionFormScoreMessage");
                break;

            case ColumnOption.COLUMN_SEGMENT_SCORE:
                this.title = Localizer.localize("UICDM", "ColumnOptionSegmentScoreTitle");
                this.description = Localizer.localize("UICDM", "ColumnOptionSegmentScoreMessage");
                break;

            case ColumnOption.COLUMN_CAPTURE_TIME:
                this.title = Localizer.localize("UICDM", "ColumnOptionCaptureTimeTitle");
                this.description = Localizer.localize("UICDM", "ColumnOptionCaptureTimeMessage");
                break;

            case ColumnOption.COLUMN_PROCESSED_TIME:
                this.title = Localizer.localize("UICDM", "ColumnOptionProcessedTimeTitle");
                this.description = Localizer.localize("UICDM", "ColumnOptionProcessedTimeMessage");
                break;

            case ColumnOption.COLUMN_IMAGE_FILE_NAMES:
                this.title = Localizer.localize("UICDM", "ColumnOptionImageFileNamesTitle");
                this.description = Localizer.localize("UICDM", "ColumnOptionImageFileNamesMessage");
                break;

            case ColumnOption.COLUMN_SOURCE_DATA:
                this.title = Localizer.localize("UICDM", "ColumnOptionSourceDataTitle");
                this.description = Localizer.localize("UICDM", "ColumnOptionSourceDataMessage");
                break;

            case ColumnOption.COLUMN_CAPTURED_DATA:
                this.title = Localizer.localize("UICDM", "ColumnOptionCapturedDataTitle");
                this.description = Localizer.localize("UICDM", "ColumnOptionCapturedDataMessage");
                break;

            case ColumnOption.COLUMN_INDIVIDUAL_SCORES:
                this.title = Localizer.localize("UICDM", "ColumnOptionIndividualScoresTitle");
                this.description =
                    Localizer.localize("UICDM", "ColumnOptionIndividualScoresMessage");
                break;

            case ColumnOption.COLUMN_SCANNED_PAGE_NUMBER:
                this.title = Localizer.localize("UICDM", "ColumnOptionScannedPageNumberTitle");
                this.description =
                    Localizer.localize("UICDM", "ColumnOptionScannedPageNumberMessage");
                break;

        }

    }

    public String toString() {
        return this.title;
    }

}
