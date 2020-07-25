package com.ebstrada.formreturn.manager.util.preferences.persistence;

import java.util.ArrayList;

import com.ebstrada.formreturn.manager.logic.export.ExportMap;
import com.ebstrada.formreturn.manager.ui.cdm.dialog.ColumnOption;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("exportPreferences") public class CSVExportPreferences implements NoObfuscation {

    public final static transient int CSV_DELIMITER = 1;

    public final static transient int TSV_DELIMITER = 2;

    public final static transient int DOUBLE_QUOTES = 1;

    public final static transient int SINGLE_QUOTES = 2;

    public final static transient int NO_QUOTES = 3;

    private boolean timestampFilenamePrefix = false;
    private boolean includeMarkScores = true;
    private boolean includeAggregated = true;
    private boolean includePublicationID = true;
    private boolean includeCaptureTime = true;
    private boolean includeProcessedTime = true;
    private boolean includeImageFileNames = true;
    private boolean includeScannedPageNumber = true;
    private boolean includeFormID = true;
    private boolean includeSourceData = true;
    private boolean includeCapturedData = true;
    private boolean combineScoresAndData = true;
    private boolean includeStatistics = false;
    private boolean includeErrorMessages = true;
    private boolean includeAggregatedFormScore = true;
    private boolean includeAggregatedSegmentScore = true;
    private boolean includeCaptureTimes = true;
    private boolean includeIndividualScores = true;
    private boolean includeProcessedTimes = true;
    private boolean includeFieldnamesHeader = true;
    private boolean includeAggregatedSegment = true;
    private boolean includeFormPassword = true;
    private boolean includeFormPageIDs = true;

    private String formPageIdColumnNamePrefix;

    private int formIdOrderIndex;
    private int formPageIDsOrderIndexOffset;
    private int formPasswordOrderIndex;
    private int formScoreOrderIndex;
    private int processedTimesOrderIndexOffset;
    private int publicationIdOrderIndex;
    private int individualScoresOffset;
    private int segmentScoreOrderIndex;
    private int scannedPageNumberOrderOffset;
    private int sourceDataOrderIndexOffset;
    private int capturedDataOrderIndexOffset;
    private int captureTimesOrderIndexOffset;
    private int imageFileNamesOrderOffset;

    private int sortType = ExportMap.SORT_BY_ORDER_INDEX;
    private String publicationIdColumnName = "publication_id";
    private String formIdColumnName = "form_id";
    private String formScoreColumnName = "form_score";
    private int delimiterType = CSV_DELIMITER;
    private int quotesType = DOUBLE_QUOTES;
    private ArrayList<Integer> orderedColumnKeys = new ArrayList<Integer>();
    private String segmentScoreColumnName = "segment_score_";
    private String formPasswordColumnName = "form_password";
    private String formPageIDsColumnNamePrefix = "form_page_id_";

    public boolean isTimestampFilenamePrefix() {
        return timestampFilenamePrefix;
    }

    public void setTimestampFilenamePrefix(boolean timestampFilenamePrefix) {
        this.timestampFilenamePrefix = timestampFilenamePrefix;
    }

    public boolean isIncludeMarkScores() {
        return includeMarkScores;
    }

    public void setIncludeMarkScores(boolean includeMarkScores) {
        this.includeMarkScores = includeMarkScores;
    }

    public boolean isIncludeAggregated() {
        return includeAggregated;
    }

    public void setIncludeAggregated(boolean includeAggregated) {
        this.includeAggregated = includeAggregated;
    }

    public boolean isIncludePublicationID() {
        return includePublicationID;
    }

    public void setIncludePublicationID(boolean includePublicationID) {
        this.includePublicationID = includePublicationID;
    }

    public int getSourceDataOrderIndexOffset() {
        return sourceDataOrderIndexOffset;
    }

    public void setSourceDataOrderIndexOffset(int sourceDataOrderIndexOffset) {
        this.sourceDataOrderIndexOffset = sourceDataOrderIndexOffset;
    }

    public int getCapturedDataOrderIndexOffset() {
        return capturedDataOrderIndexOffset;
    }

    public void setCapturedDataOrderIndexOffset(int capturedDataOrderIndexOffset) {
        this.capturedDataOrderIndexOffset = capturedDataOrderIndexOffset;
    }

    public int getCaptureTimesOrderIndexOffset() {
        return captureTimesOrderIndexOffset;
    }

    public void setCaptureTimesOrderIndexOffset(int captureTimesOrderIndexOffset) {
        this.captureTimesOrderIndexOffset = captureTimesOrderIndexOffset;
    }

    public int getImageFileNamesOrderOffset() {
        return imageFileNamesOrderOffset;
    }

    public void setImageFileNamesOrderOffset(int imageFileNamesOrderOffset) {
        this.imageFileNamesOrderOffset = imageFileNamesOrderOffset;
    }

    public boolean isIncludeFormID() {
        return includeFormID;
    }

    public void setIncludeFormID(boolean includeFormID) {
        this.includeFormID = includeFormID;
    }

    public boolean isIncludeSourceData() {
        return includeSourceData;
    }

    public void setIncludeSourceData(boolean includeSourceData) {
        this.includeSourceData = includeSourceData;
    }


    public boolean isIncludeCaptureTime() {
        return includeCaptureTime;
    }

    public boolean isIncludeProcessedTime() {
        return includeProcessedTime;
    }

    public boolean isIncludeCapturedData() {
        return includeCapturedData;
    }

    public void setIncludeCapturedData(boolean includeCapturedData) {
        this.includeCapturedData = includeCapturedData;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public String getPublicationIdColumnName() {
        return publicationIdColumnName;
    }

    public void setPublicationIdColumnName(String publicationIdColumnName) {
        this.publicationIdColumnName = publicationIdColumnName;
    }

    public String getFormIdColumnName() {
        return formIdColumnName;
    }

    public void setFormIdColumnName(String formIdColumnName) {
        this.formIdColumnName = formIdColumnName;
    }

    public int getProcessedTimesOrderIndexOffset() {
        return processedTimesOrderIndexOffset;
    }

    public void setProcessedTimesOrderIndexOffset(int processedTimesOrderIndexOffset) {
        this.processedTimesOrderIndexOffset = processedTimesOrderIndexOffset;
    }

    public int getPublicationIdOrderIndex() {
        return publicationIdOrderIndex;
    }

    public void setPublicationIdOrderIndex(int publicationIdOrderIndex) {
        this.publicationIdOrderIndex = publicationIdOrderIndex;
    }

    public int getIndividualScoresOffset() {
        return individualScoresOffset;
    }

    public void setIndividualScoresOffset(int individualScoresOffset) {
        this.individualScoresOffset = individualScoresOffset;
    }

    public int getSegmentScoreOrderIndex() {
        return segmentScoreOrderIndex;
    }

    public void setSegmentScoreOrderIndex(int segmentScoreOrderIndex) {
        this.segmentScoreOrderIndex = segmentScoreOrderIndex;
    }

    public int getScannedPageNumberOrderOffset() {
        return scannedPageNumberOrderOffset;
    }

    public void setScannedPageNumberOrderOffset(int scannedPageNumberOrderOffset) {
        this.scannedPageNumberOrderOffset = scannedPageNumberOrderOffset;
    }

    public String getFormScoreColumnName() {
        return formScoreColumnName;
    }

    public String getSegmentScoreColumnName() {
        if (segmentScoreColumnName == null) {
            this.segmentScoreColumnName = "segment_score_";
        }
        return this.segmentScoreColumnName;
    }

    public void setFormScoreColumnName(String formScoreColumnName) {
        this.formScoreColumnName = formScoreColumnName;
    }

    public boolean isIncludeFieldnamesHeader() {
        return includeFieldnamesHeader;
    }

    public void setIncludeFieldnamesHeader(boolean includeFieldnamesHeader) {
        this.includeFieldnamesHeader = includeFieldnamesHeader;
    }

    public int getDelimiterType() {
        return delimiterType;
    }

    public void setDelimiterType(int delimiterType) {
        this.delimiterType = delimiterType;
    }

    public int getQuotesType() {
        return quotesType;
    }

    public void setQuotesType(int quotesType) {
        this.quotesType = quotesType;
    }

    public void setIncludeCaptureTime(boolean includeCaptureTime) {
        this.includeCaptureTime = includeCaptureTime;
    }

    public void setIncludeProcessedTime(boolean includeProcessedTime) {
        this.includeProcessedTime = includeProcessedTime;
    }

    public boolean isIncludeImageFileNames() {
        return this.includeImageFileNames;
    }

    public void setIncludeImageFileNames(boolean includeImageFileNames) {
        this.includeImageFileNames = includeImageFileNames;
    }

    public boolean isIncludeAggregatedFormScore() {
        return includeAggregatedFormScore;
    }

    public void setIncludeAggregatedFormScore(boolean includeAggregatedFormScore) {
        this.includeAggregatedFormScore = includeAggregatedFormScore;
    }

    public boolean isIncludeAggregatedSegmentScore() {
        return includeAggregatedSegmentScore;
    }

    public void setIncludeAggregatedSegmentScore(boolean includeAggregatedSegmentScore) {
        this.includeAggregatedSegmentScore = includeAggregatedSegmentScore;
    }

    public boolean isIncludeCaptureTimes() {
        return includeCaptureTimes;
    }

    public void setIncludeCaptureTimes(boolean includeCaptureTimes) {
        this.includeCaptureTimes = includeCaptureTimes;
    }

    public boolean isIncludeIndividualScores() {
        return includeIndividualScores;
    }

    public void setIncludeIndividualScores(boolean includeIndividualScores) {
        this.includeIndividualScores = includeIndividualScores;
    }

    public boolean isIncludeProcessedTimes() {
        return includeProcessedTimes;
    }

    public void setIncludeProcessedTimes(boolean includeProcessedTimes) {
        this.includeProcessedTimes = includeProcessedTimes;
    }

    public ArrayList<Integer> getOrderedColumnKeys() {
        if (this.orderedColumnKeys == null || this.orderedColumnKeys.size() <= 0) {
            this.orderedColumnKeys = new ArrayList<Integer>();
            this.orderedColumnKeys.add(ColumnOption.COLUMN_CAPTURE_TIME);
            this.orderedColumnKeys.add(ColumnOption.COLUMN_PROCESSED_TIME);
            this.orderedColumnKeys.add(ColumnOption.COLUMN_IMAGE_FILE_NAMES);
            this.orderedColumnKeys.add(ColumnOption.COLUMN_SCANNED_PAGE_NUMBER);
            this.orderedColumnKeys.add(ColumnOption.COLUMN_PUBLICATION_ID);
            this.orderedColumnKeys.add(ColumnOption.COLUMN_FORM_ID);
            this.orderedColumnKeys.add(ColumnOption.COLUMN_FORM_PAGE_IDS);
            this.orderedColumnKeys.add(ColumnOption.COLUMN_FORM_PASSWORD);
            this.orderedColumnKeys.add(ColumnOption.COLUMN_FORM_SCORE);
            this.orderedColumnKeys.add(ColumnOption.COLUMN_SEGMENT_SCORE);
            this.orderedColumnKeys.add(ColumnOption.COLUMN_SOURCE_DATA);
            this.orderedColumnKeys.add(ColumnOption.COLUMN_CAPTURED_DATA);
            this.orderedColumnKeys.add(ColumnOption.COLUMN_INDIVIDUAL_SCORES);
            this.combineScoresAndData = true;
            this.includeAggregated = true;
            this.includeAggregatedSegment = true;
            this.includeCapturedData = true;
            this.includeFormID = true;
            this.includeFormPageIDs = true;
            this.includeFormPassword = true;
            this.includeImageFileNames = true;
            this.includeScannedPageNumber = true;
            this.includeMarkScores = true;
            this.includeCaptureTime = true;
            this.includeProcessedTime = true;
            this.includePublicationID = true;
            this.includeSourceData = true;
        }

        if (this.includeAggregated == false && this.includeAggregatedSegment == false
            && this.includeCapturedData == false && this.includeFormID == false
            && this.includeFormPageIDs == false && this.includeFormPassword == false
            && this.includeImageFileNames == false && this.includeScannedPageNumber == false
            && this.includeMarkScores == false && this.includeCaptureTime == false
            && this.includeProcessedTime == false && this.includePublicationID == false
            && this.includeSourceData == false) {
            this.combineScoresAndData = true;
            this.includeAggregated = true;
            this.includeAggregatedSegment = true;
            this.includeCapturedData = true;
            this.includeFormID = true;
            this.includeFormPageIDs = true;
            this.includeFormPassword = true;
            this.includeImageFileNames = true;
            this.includeScannedPageNumber = true;
            this.includeMarkScores = true;
            this.includeCaptureTime = true;
            this.includeProcessedTime = true;
            this.includePublicationID = true;
            this.includeSourceData = true;
        }

        return this.orderedColumnKeys;
    }

    public int getFormIdOrderIndex() {
        return formIdOrderIndex;
    }

    public void setFormIdOrderIndex(int formIdOrderIndex) {
        this.formIdOrderIndex = formIdOrderIndex;
    }

    public String getFormPageIdColumnNamePrefix() {
        return formPageIdColumnNamePrefix;
    }

    public void setFormPageIdColumnNamePrefix(String formPageIdColumnNamePrefix) {
        this.formPageIdColumnNamePrefix = formPageIdColumnNamePrefix;
    }

    public int getFormPageIDsOrderIndexOffset() {
        return formPageIDsOrderIndexOffset;
    }

    public void setFormPageIDsOrderIndexOffset(int formPageIDsOrderIndexOffset) {
        this.formPageIDsOrderIndexOffset = formPageIDsOrderIndexOffset;
    }

    public int getFormPasswordOrderIndex() {
        return formPasswordOrderIndex;
    }

    public void setFormPasswordOrderIndex(int formPasswordOrderIndex) {
        this.formPasswordOrderIndex = formPasswordOrderIndex;
    }

    public int getFormScoreOrderIndex() {
        return formScoreOrderIndex;
    }

    public void setFormScoreOrderIndex(int formScoreOrderIndex) {
        this.formScoreOrderIndex = formScoreOrderIndex;
    }

    public void setOrderedColumnKeys(ArrayList<Integer> orderedColumnKeys) {
        this.orderedColumnKeys = orderedColumnKeys;
    }

    public boolean isCombineScoresAndData() {
        return combineScoresAndData;
    }

    public void setCombineScoresAndData(boolean combineScoresAndData) {
        this.combineScoresAndData = combineScoresAndData;
    }

    public boolean isIncludeStatistics() {
        return includeStatistics;
    }

    public void setIncludeStatistics(boolean includeStatistics) {
        this.includeStatistics = includeStatistics;
    }

    public boolean isIncludeAggregatedSegment() {
        return this.includeAggregatedSegment;
    }

    public void setIncludeAggregatedSegment(boolean includeAggregatedSegment) {
        this.includeAggregatedSegment = includeAggregatedSegment;
    }

    public void setSegmentScoreColumnName(String segmentScoreColumnName) {
        this.segmentScoreColumnName = segmentScoreColumnName;
    }

    public void setIncludeErrorMessages(boolean includeErrorMessages) {
        this.includeErrorMessages = includeErrorMessages;
    }

    public boolean isIncludeErrorMessages() {
        return this.includeErrorMessages;
    }

    public boolean isIncludeScannedPageNumber() {
        return this.includeScannedPageNumber;
    }

    public void setIncludeScannedPageNumber(boolean includeScannedPageNumber) {
        this.includeScannedPageNumber = includeScannedPageNumber;
    }

    public String getFormPasswordColumnName() {
        if (formPasswordColumnName == null) {
            formPasswordColumnName = "form_password";
        }
        return formPasswordColumnName;
    }

    public String getFormPageIDsColumnNamePrefix() {
        if (formPageIDsColumnNamePrefix == null) {
            formPageIDsColumnNamePrefix = "form_page_id_";
        }
        return formPageIDsColumnNamePrefix;
    }

    public boolean isIncludeFormPassword() {
        return includeFormPassword;
    }

    public boolean isIncludeFormPageIDs() {
        return includeFormPageIDs;
    }

    public void setIncludeFormPassword(boolean includeFormPassword) {
        this.includeFormPassword = includeFormPassword;
    }

    public void setIncludeFormPageIDs(boolean includeFormPageIDs) {
        this.includeFormPageIDs = includeFormPageIDs;
    }

    public void setFormPasswordColumnName(String formPasswordColumnName) {
        this.formPasswordColumnName = formPasswordColumnName;
    }

    public void setFormPageIDsColumnNamePrefix(String formPageIDsColumnNamePrefix) {
        this.formPageIDsColumnNamePrefix = formPageIDsColumnNamePrefix;
    }

}
