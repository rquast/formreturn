package com.ebstrada.formreturn.manager.logic.export.csv;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import au.com.bytecode.opencsv.CSVWriter;

import com.ebstrada.formreturn.api.messaging.MessageNotification;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.export.Column;
import com.ebstrada.formreturn.manager.logic.export.ExportMap;
import com.ebstrada.formreturn.manager.logic.export.filter.ExcludeEmptyRecordsFilter;
import com.ebstrada.formreturn.manager.logic.export.filter.Filter;
import com.ebstrada.formreturn.manager.logic.export.stats.Statistic;
import com.ebstrada.formreturn.manager.logic.export.stats.StatisticMap;
import com.ebstrada.formreturn.manager.logic.recognition.reader.FormReaderException;
import com.ebstrada.formreturn.manager.persistence.jpa.CheckBox;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentBarcode;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentOmr;
import com.ebstrada.formreturn.manager.persistence.jpa.Grading;
import com.ebstrada.formreturn.manager.persistence.jpa.Segment;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.persistence.CSVExportPreferences;

public class CSVExporter {

    private int sortType;

    private boolean includePublicationID;
    private boolean includeFormID;
    private boolean includeAggregatedFormScore;
    private boolean includeAggregatedSegmentScore;
    private boolean includeIndividualScores;
    private boolean combineScoresAndData;
    private boolean includeCaptureTimes;
    private boolean includeProcessedTimes;
    private boolean includeImageFileNames;
    private boolean includeScannedPageNumber;
    private boolean includeSourceData;
    private boolean includeCapturedData;
    private boolean includeFieldnamesHeader;
    private boolean includeStatistics;
    private boolean includeErrorMessages;
    private boolean includeFormPageIDs;
    private boolean includeFormPassword;

    private ArrayList<Long> publicationIds;

    private String publicationIdColumnName;
    private String formIdColumnName;
    private String formPasswordColumnName;
    private String formPageIdColumnNamePrefix;
    private String formScoreColumnName;
    private String segmentScoreColumnName;

    private int publicationIdOrderIndex;
    private int formIdOrderIndex;
    private int formPasswordOrderIndex;
    private int formPageIdOrderIndexOffset;
    private int formScoreOrderIndex;
    private int segmentScoreOrderIndex;
    private int captureTimesOrderIndexOffset;
    private int processedTimesOrderIndexOffset;
    private int imageFileNamesOrderOffset;
    private int scannedPageNumberOrderOffset;
    private int sourceDataOrderIndexOffset;
    private int capturedDataOrderIndexOffset;
    private int individualScoresOffset;

    private EntityManager entityManager;

    private ArrayList<Filter> filters;

    private StatisticMap statisticsMap = new StatisticMap();

    public CSVExporter(CSVExportPreferences csvExportPreferences, ArrayList<Filter> filters,
        ArrayList<Long> publicationIds, EntityManager entityManager) {

        this.entityManager = entityManager;
        this.filters = filters;

        this.includeStatistics = csvExportPreferences.isIncludeStatistics();
        this.includeErrorMessages = csvExportPreferences.isIncludeErrorMessages();
        this.sortType = csvExportPreferences.getSortType();
        this.includePublicationID = csvExportPreferences.isIncludePublicationID();
        this.includeFormID = csvExportPreferences.isIncludeFormID();
        this.includeFormPassword = csvExportPreferences.isIncludeFormPassword();
        this.includeFormPageIDs = csvExportPreferences.isIncludeFormPageIDs();
        this.includeAggregatedFormScore = csvExportPreferences.isIncludeAggregatedFormScore();
        this.includeAggregatedSegmentScore = csvExportPreferences.isIncludeAggregatedSegmentScore();
        this.includeIndividualScores = csvExportPreferences.isIncludeIndividualScores();
        this.combineScoresAndData = csvExportPreferences.isCombineScoresAndData();
        this.includeCaptureTimes = csvExportPreferences.isIncludeCaptureTimes();
        this.includeProcessedTimes = csvExportPreferences.isIncludeProcessedTimes();
        this.includeImageFileNames = csvExportPreferences.isIncludeImageFileNames();
        this.includeScannedPageNumber = csvExportPreferences.isIncludeScannedPageNumber();
        this.includeSourceData = csvExportPreferences.isIncludeSourceData();
        this.includeCapturedData = csvExportPreferences.isIncludeCapturedData();
        this.includeFieldnamesHeader = csvExportPreferences.isIncludeFieldnamesHeader();
        this.publicationIds = publicationIds;
        this.publicationIdColumnName = csvExportPreferences.getPublicationIdColumnName();
        this.formIdColumnName = csvExportPreferences.getFormIdColumnName();
        this.formPasswordColumnName = csvExportPreferences.getFormPasswordColumnName();
        this.formPageIdColumnNamePrefix = csvExportPreferences.getFormPageIdColumnNamePrefix();
        this.formScoreColumnName = csvExportPreferences.getFormScoreColumnName();
        this.segmentScoreColumnName = csvExportPreferences.getSegmentScoreColumnName();
        this.publicationIdOrderIndex = csvExportPreferences.getPublicationIdOrderIndex();
        this.formIdOrderIndex = csvExportPreferences.getFormIdOrderIndex();
        this.formPasswordOrderIndex = csvExportPreferences.getFormPasswordOrderIndex();
        this.formPageIdOrderIndexOffset = csvExportPreferences.getFormPageIDsOrderIndexOffset();
        this.formScoreOrderIndex = csvExportPreferences.getFormScoreOrderIndex();
        this.segmentScoreOrderIndex = csvExportPreferences.getSegmentScoreOrderIndex();
        this.captureTimesOrderIndexOffset = csvExportPreferences.getCaptureTimesOrderIndexOffset();
        this.processedTimesOrderIndexOffset =
            csvExportPreferences.getProcessedTimesOrderIndexOffset();
        this.imageFileNamesOrderOffset = csvExportPreferences.getImageFileNamesOrderOffset();
        this.scannedPageNumberOrderOffset = csvExportPreferences.getScannedPageNumberOrderOffset();
        this.sourceDataOrderIndexOffset = csvExportPreferences.getSourceDataOrderIndexOffset();
        this.capturedDataOrderIndexOffset = csvExportPreferences.getCapturedDataOrderIndexOffset();
        this.individualScoresOffset = csvExportPreferences.getIndividualScoresOffset();

    }

    public void writeStats(CSVWriter writer, MessageNotification messageNotification) {

        String[] fieldNamesArray =
            new String[] {"fieldname", "response", "frequency", "correct", "incorrect",
                "total_responses"};

        if (includeFieldnamesHeader) {
            writer.writeNext(fieldNamesArray);
        }

        int rowCount = this.statisticsMap.getFieldNames().size();

        ArrayList<String> fieldNames = this.statisticsMap.getFieldNames();
        ArrayList<Statistic> statistics = this.statisticsMap.getStatistics();

        for (int k = 0; k < rowCount; k++) {
            if (messageNotification.isInterrupted()) {
                break;
            }
            messageNotification.setMessage(String
                .format(Localizer.localize("UICDM", "SavingRecordMessage"), (k + 1), rowCount));
            String[] rowDataArray = new String[fieldNamesArray.length];

            String fieldName = fieldNames.get(k);
            Statistic stat = statistics.get(k);

            ArrayList<String> answers = stat.getAnswers();
            ArrayList<Integer> frequencies = stat.getFrequencies();

            double correct = stat.getPercentageCorrect();
            double incorrect = stat.getPercentageIncorrect();
            int totalResponses = (int) stat.getTotalReponses();

            NumberFormat nf = NumberFormat.getInstance();

            for (String answer : answers) {

                int frequency = frequencies.get(answers.indexOf(answer));

                rowDataArray =
                    new String[] {fieldName, answer, nf.format(frequency), nf.format(correct) + "%",
                        nf.format(incorrect) + "%", totalResponses + ""};

                writer.writeNext(rowDataArray);

            }

        }

    }

    public void write(CSVWriter writer, MessageNotification processingStatusDialog) {

        ExportMap export = new ExportMap();
        export.setSortType(sortType);

        for (long publicationId : this.publicationIds) {

            Grading grading = null;

            String gradingSQL =
                "SELECT MAX(GRADING_ID) FROM GRADING WHERE PUBLICATION_ID = " + publicationId;
            Query gradingIdQuery = entityManager.createNativeQuery(gradingSQL);

            try {
                Long gradingId = (Long) gradingIdQuery.getSingleResult();
                grading = entityManager.find(Grading.class, gradingId);
            } catch (Exception ex) {
            }

            boolean foundExcludeFilter = false;
            for (Filter filter : this.filters) {
                if (filter instanceof ExcludeEmptyRecordsFilter) {
                    foundExcludeFilter = true;
                    break;
                }
            }

            Query formQuery = null;

            if (foundExcludeFilter) {
                formQuery = entityManager.createNativeQuery(
                    "SELECT FORM.FORM_ID, FORM.FORM_PASSWORD, FORM.RECORD_ID, FORM.AGGREGATE_MARK, MIN(FORM_PAGE.PROCESSED_TIME) FROM FORM LEFT JOIN FORM_PAGE ON FORM.FORM_ID = FORM_PAGE.FORM_ID WHERE RECORD_ID IS NOT NULL AND FORM_PAGE.PROCESSED_TIME IS NOT NULL AND FORM.PUBLICATION_ID = "
                        + publicationId
                        + " GROUP BY FORM.FORM_ID, FORM.FORM_PASSWORD, FORM.RECORD_ID, FORM.AGGREGATE_MARK ORDER BY FORM.FORM_ID ASC");
            } else {
                formQuery = entityManager.createNativeQuery(
                    "SELECT FORM_ID, FORM_PASSWORD, RECORD_ID, AGGREGATE_MARK FROM FORM WHERE RECORD_ID IS NOT NULL AND PUBLICATION_ID = "
                        + publicationId + " ORDER BY FORM_ID ASC");
            }

            List<Object[]> resultList = formQuery.getResultList();

            int numberOfForms = resultList.size();
            export.setSize(numberOfForms);

            int rowNumber = 0;
            for (Object[] objArr : resultList) {

                // skip anything where recordId is null
                if (objArr[1] == null) {
                    continue;
                }

                long formId = (Long) objArr[0];
                String formPassword = (String) objArr[1];
                long recordId = (Long) objArr[2];
                double aggregateMark = (Double) objArr[3];

                if (processingStatusDialog.isInterrupted()) {
                    break;
                }
                processingStatusDialog.setMessage(String
                    .format(Localizer.localize("UICDM", "ReadingRecordMessage"), (rowNumber + 1),
                        numberOfForms));

                if (includePublicationID) {
                    export.addData(publicationIdColumnName, publicationId + "", rowNumber, 0,
                        publicationIdOrderIndex);
                }

                if (includeFormID) {
                    export.addData(formIdColumnName, formId + "", rowNumber, 0, formIdOrderIndex);
                }

                if (this.includeFormPassword) {
                    export.addData(formPasswordColumnName, formPassword, rowNumber, 0,
                        formPasswordOrderIndex);
                }


                if (includeAggregatedFormScore) {
                    export.addData(formScoreColumnName, aggregateMark + "", rowNumber, 0,
                        formScoreOrderIndex);
                }

                int orderIndex = 0;

                if (includeAggregatedSegmentScore) {

                    orderIndex = 0;

                    String segmentScoreSQL =
                        "SELECT segment.* FROM FORM frm LEFT JOIN FORM_PAGE fp ON frm.FORM_ID = fp.FORM_ID LEFT JOIN SEGMENT segment ON fp.FORM_PAGE_ID = segment.FORM_PAGE_ID WHERE frm.FORM_ID = "
                            + formId + " ORDER BY segment.BARCODE_ONE";

                    Query segmentScoreQuery =
                        entityManager.createNativeQuery(segmentScoreSQL, Segment.class);
                    List<Segment> segmentResultList = segmentScoreQuery.getResultList();

                    if (segmentResultList != null && segmentResultList.size() > 0) {
                        for (Segment segment : segmentResultList) {
                            if (segment == null) {
                                continue;
                            }
                            String segmentName = segment.getName();
                            if (segmentName == null || segmentName.trim().length() <= 0) {
                                segmentName =
                                    segment.getBarcodeOne() + "_" + segment.getBarcodeTwo();
                            }
                            if (segmentScoreColumnName == null
                                || segmentScoreColumnName.trim().length() <= 0) {
                                segmentScoreColumnName = "";
                            }
                            double segmentAggregate = segment.getAggregateMark();
                            export.addData(segmentScoreColumnName + segmentName,
                                segmentAggregate + "", rowNumber, orderIndex,
                                segmentScoreOrderIndex);
                            ++orderIndex;
                        }
                    }

                }

                if (includeProcessedTimes || includeCaptureTimes || includeImageFileNames) {

                    orderIndex = 0;

                    String formPageSQL =
                        "SELECT fp.* FROM FORM frm LEFT JOIN FORM_PAGE fp ON frm.FORM_ID = fp.FORM_ID WHERE frm.FORM_ID = "
                            + formId + " ORDER BY fp.FORM_PAGE_NUMBER ASC";
                    Query formPageQuery =
                        entityManager.createNativeQuery(formPageSQL, FormPage.class);
                    List<FormPage> formPageResultList = formPageQuery.getResultList();


                    if (includeFormPageIDs) {
                        if (formPageResultList != null && formPageResultList.size() > 0) {
                            for (FormPage formPage : formPageResultList) {
                                if (formPage == null) {
                                    continue;
                                }
                                export.addData(
                                    this.formPageIdColumnNamePrefix + formPage.getFormPageNumber(),
                                    formPage.getFormPageId() + "", rowNumber, orderIndex,
                                    this.formPageIdOrderIndexOffset);
                                ++orderIndex;
                            }
                        }
                    }

                    if (includeCaptureTimes) {

                        String captureTimeString =
                            Localizer.localize("UICDM", "CaptureTimeColumnPrefix");

                        if (formPageResultList != null && formPageResultList.size() > 0) {
                            for (FormPage formPage : formPageResultList) {
                                if (formPage == null) {
                                    continue;
                                }

                                Timestamp captureTime = formPage.getCaptureTime();
                                if (captureTime == null) {
                                    continue;
                                }
                                SimpleDateFormat dateFormat =
                                    new SimpleDateFormat("yyyyMMddHHmmss");
                                String timeString = dateFormat.format(captureTime.getTime());
                                export.addData(String
                                        .format(captureTimeString, formPage.getFormPageNumber() + ""),
                                    timeString, rowNumber, orderIndex,
                                    captureTimesOrderIndexOffset);
                                ++orderIndex;
                            }
                        }

                    }

                    if (includeProcessedTimes) {

                        String processedTimeString =
                            Localizer.localize("UICDM", "ProcessedTimeColumnPrefix");

                        if (formPageResultList != null && formPageResultList.size() > 0) {
                            for (FormPage formPage : formPageResultList) {
                                if (formPage == null) {
                                    continue;
                                }

                                Timestamp processedTime = formPage.getProcessedTime();
                                if (processedTime == null) {
                                    continue;
                                }
                                SimpleDateFormat dateFormat =
                                    new SimpleDateFormat("yyyyMMddHHmmss");
                                String timeString = dateFormat.format(processedTime.getTime());
                                export.addData(String
                                        .format(processedTimeString, formPage.getFormPageNumber() + ""),
                                    timeString, rowNumber, orderIndex,
                                    processedTimesOrderIndexOffset);
                                ++orderIndex;
                            }
                        }

                    }

                    if (includeImageFileNames) {

                        String imageFileNameString =
                            Localizer.localize("UICDM", "ImageFileNameColumnPrefix");

                        if (formPageResultList != null && formPageResultList.size() > 0) {
                            for (FormPage formPage : formPageResultList) {
                                if (formPage == null) {
                                    continue;
                                }

                                String imageNameSQL =
                                    "SELECT PROCESSED_IMAGE_NAME FROM PROCESSED_IMAGE WHERE FORM_PAGE_ID = "
                                        + formPage.getFormPageId();
                                Query imageNameQuery =
                                    entityManager.createNativeQuery(imageNameSQL);
                                List<String> imageNameResultList = imageNameQuery.getResultList();
                                if (imageNameResultList == null
                                    || imageNameResultList.size() <= 0) {
                                    continue;
                                }
                                String imageFileName = imageNameResultList.iterator().next();

                                if (imageFileName == null) {
                                    continue;
                                }

                                export.addData(String
                                        .format(imageFileNameString, formPage.getFormPageNumber() + ""),
                                    imageFileName, rowNumber, orderIndex,
                                    imageFileNamesOrderOffset);
                                ++orderIndex;
                            }
                        }

                    }

                    if (includeScannedPageNumber) {

                        String scannedPageNumberColumnName =
                            Localizer.localize("UICDM", "ScannedPageNumberColumnName");

                        if (formPageResultList != null && formPageResultList.size() > 0) {
                            for (FormPage formPage : formPageResultList) {
                                if (formPage == null) {
                                    continue;
                                }

                                export.addData(String.format(scannedPageNumberColumnName,
                                    formPage.getFormPageNumber()),
                                    formPage.getScannedPageNumber() + "", rowNumber, orderIndex,
                                    scannedPageNumberOrderOffset);
                                ++orderIndex;

                            }

                        }

                    }


                }

                if (includeSourceData) {

                    Query sourceTextQuery = entityManager.createNativeQuery(
                        "SELECT RECORD_ID, SOURCE_TEXT_STRING, SOURCE_FIELD_NAME, ORDER_INDEX FROM SOURCE_TEXT LEFT JOIN SOURCE_FIELD ON SOURCE_TEXT.SOURCE_FIELD_ID = SOURCE_FIELD.SOURCE_FIELD_ID WHERE RECORD_ID = "
                            + recordId);
                    List<Object[]> stResultList = sourceTextQuery.getResultList();


                    for (Object[] stobjs : stResultList) {
                        String fieldName = (String) stobjs[2];
                        String value = (String) stobjs[1];
                        Long orderIndexLong = (Long) stobjs[3];
                        if (orderIndexLong == null) {
                            orderIndexLong = new Long(0);
                        }
                        orderIndex = (orderIndexLong).intValue();
                        if (value == null) {
                            value = "";
                        }
                        export.addData(fieldName, value, rowNumber, orderIndex,
                            sourceDataOrderIndexOffset);
                        ++orderIndex;
                    }

                }

                if (includeCapturedData || includeIndividualScores || includeStatistics) {

                    String fragmentBarcodeSQL =
                        "SELECT fbc.* FROM FORM frm LEFT JOIN FORM_PAGE fp ON frm.FORM_ID = fp.FORM_ID LEFT JOIN SEGMENT seg ON fp.FORM_PAGE_ID = seg.FORM_PAGE_ID LEFT JOIN FRAGMENT_BARCODE fbc ON seg.SEGMENT_ID = fbc.SEGMENT_ID WHERE frm.FORM_ID = "
                            + formId;
                    Query fragmentBarcodeQuery =
                        entityManager.createNativeQuery(fragmentBarcodeSQL, FragmentBarcode.class);
                    List<FragmentBarcode> fragmentBarcodeResultList =
                        fragmentBarcodeQuery.getResultList();
                    if (includeCapturedData && fragmentBarcodeResultList != null
                        && fragmentBarcodeResultList.size() > 0) {
                        for (FragmentBarcode fragmentBarcode : fragmentBarcodeResultList) {
                            if (fragmentBarcode == null) {
                                continue;
                            }
                            String barcodeValue = fragmentBarcode.getBarcodeValue();
                            String fieldName = fragmentBarcode.getCapturedDataFieldName();
                            int fieldNameOrderIndex = (int) fragmentBarcode.getOrderIndex();

                            if (barcodeValue != null && barcodeValue.trim().length() > 0) {
                                export.addData(fieldName, barcodeValue, rowNumber,
                                    fieldNameOrderIndex, capturedDataOrderIndexOffset);
                            } else {
                                export.addData(fieldName, "", rowNumber, fieldNameOrderIndex,
                                    capturedDataOrderIndexOffset);
                            }

                        }
                    }


                    String query =
                        "SELECT fomr.* FROM FORM frm LEFT JOIN FORM_PAGE fp ON frm.FORM_ID = fp.FORM_ID LEFT JOIN SEGMENT seg ON fp.FORM_PAGE_ID = seg.FORM_PAGE_ID LEFT JOIN FRAGMENT_OMR fomr ON seg.SEGMENT_ID = fomr.SEGMENT_ID WHERE frm.FORM_ID = "
                            + formId;
                    Query fragmentQuery = entityManager.createNativeQuery(query, FragmentOmr.class);
                    List<FragmentOmr> fragmentResultList = fragmentQuery.getResultList();

                    for (FragmentOmr fragmentOmr : fragmentResultList) {

                        if (fragmentOmr == null) {
                            continue;
                        }

                        String[] capturedData = null;
                        if (fragmentOmr.getInvalidated() <= 0) {
                            List<CheckBox> cbc = fragmentOmr.getCheckBoxCollection();
                            if (cbc != null && cbc.size() > 0) {
                                Vector<String> capturedValues = new Vector<String>();
                                for (CheckBox cb : cbc) {
                                    if (cb.getCheckBoxMarked() > 0) {
                                        capturedValues.add(cb.getCheckBoxValue());
                                    }
                                }
                                capturedData = new String[capturedValues.size()];
                                for (int i = 0; i < capturedData.length; ++i) {
                                    capturedData[i] = capturedValues.get(i);
                                }
                            }

                            if (capturedData == null) {
                                capturedData = fragmentOmr.getCapturedData();
                            }
                        } else {
                            FormReaderException fre =
                                new FormReaderException((int) fragmentOmr.getErrorType());
                            fre.setCapturedDataFieldName(fragmentOmr.getCapturedDataFieldName());
                            if (includeErrorMessages) {
                                capturedData = new String[] {"!!ERROR!!",
                                    fre.getErrorTitle().replaceAll("\r", " ").replaceAll("\n",
                                        " ")};
                            } else {
                                capturedData = new String[] {};
                            }
                        }

                        String fieldName = fragmentOmr.getCapturedDataFieldName();

                        ArrayList<String> answers = new ArrayList<String>();

                        int fieldNameOrderIndex = (int) fragmentOmr.getOrderIndex();
                        String markColumnName = fragmentOmr.getMarkColumnName();

                        if (markColumnName == null || markColumnName.trim().length() <= 0) {
                            markColumnName =
                                fieldName + Localizer.localize("UICDM", "MarkColumnNameSuffix");
                        }

                        int markColumnOrderIndex = (int) fragmentOmr.getMarkOrderIndex();
                        String capturedString = fragmentOmr.getCapturedString();
                        int combineColumnCharacters = fragmentOmr.getCombineColumnCharacters();
                        double mark = fragmentOmr.getMark();
                        int reconciliationKey = fragmentOmr.getReconciliationKey();

                        // this is here for backwards compatibility
                        if (fieldNameOrderIndex == 0 && markColumnOrderIndex == 0) {
                            fieldNameOrderIndex = orderIndex;
                            markColumnOrderIndex = orderIndex + 1;
                        }

                        if (capturedData != null) {
                            if (combineColumnCharacters > 0 || reconciliationKey > 0) {
                                if (includeCapturedData) {
                                    export.addData(fieldName, capturedString, rowNumber,
                                        fieldNameOrderIndex, capturedDataOrderIndexOffset);
                                }
                                if (includeStatistics) {
                                    answers.add(capturedString);
                                }
                                if (includeIndividualScores && reconciliationKey <= 0) {
                                    if (combineScoresAndData) {
                                        export.addData(markColumnName, mark + "", rowNumber,
                                            markColumnOrderIndex, fieldName + markColumnName,
                                            capturedDataOrderIndexOffset);
                                    } else {
                                        export.addData(markColumnName, mark + "", rowNumber,
                                            markColumnOrderIndex, fieldName + markColumnName,
                                            individualScoresOffset);
                                    }
                                }
                            } else {
                                if (includeCapturedData) {
                                    export.addData(fieldName, Misc.implode(capturedData, ","),
                                        rowNumber, fieldNameOrderIndex,
                                        capturedDataOrderIndexOffset);
                                }
                                if (includeStatistics) {
                                    for (String answer : capturedData) {
                                        answers.add(answer);
                                    }
                                }
                                if (includeIndividualScores) {
                                    if (combineScoresAndData) {
                                        export.addData(markColumnName, mark + "", rowNumber,
                                            markColumnOrderIndex, fieldName + markColumnName,
                                            capturedDataOrderIndexOffset);
                                    } else {
                                        export.addData(markColumnName, mark + "", rowNumber,
                                            markColumnOrderIndex, fieldName + markColumnName,
                                            individualScoresOffset);
                                    }
                                }
                            }
                        } else {
                            if (includeCapturedData) {
                                export.addData(fieldName, "", rowNumber, fieldNameOrderIndex,
                                    capturedDataOrderIndexOffset);
                            }
                            if (includeIndividualScores && reconciliationKey <= 0) {
                                if (combineScoresAndData) {
                                    export.addData(markColumnName, "", rowNumber,
                                        markColumnOrderIndex, fieldName + markColumnName,
                                        capturedDataOrderIndexOffset);
                                } else {
                                    export.addData(markColumnName, "", rowNumber,
                                        markColumnOrderIndex, fieldName + markColumnName,
                                        individualScoresOffset);
                                }
                            }
                        }

                        this.statisticsMap.addAnswers(fieldName, answers, mark);

                        orderIndex += 2;

                    }


                }

                if (grading != null) {

                    double percentage = (aggregateMark / grading.getTotalPossibleScore()) * 100.0d;
                    export.addData(Localizer.localize("UI", "PercentageFieldName"), percentage + "",
                        rowNumber, formScoreOrderIndex + 2, formScoreOrderIndex);

                    String grade =
                        Misc.getGrading(aggregateMark, grading.getGradingRuleCollection(),
                            grading.getTotalPossibleScore());
                    export
                        .addData(Localizer.localize("UI", "GradeFieldName"), grade + "", rowNumber,
                            formScoreOrderIndex + 3, formScoreOrderIndex);

                }

                rowNumber++;

            }

            List<Column> sortedColumns = export.getSortedData();
            String[] fieldNamesArray = new String[sortedColumns.size()];
            int j = 0;
            for (Column sortedColumn : sortedColumns) {
                fieldNamesArray[j] = sortedColumn.getFieldname();
                j++;
            }

            if (includeFieldnamesHeader) {
                writer.writeNext(fieldNamesArray);
            }

            for (int k = 0; k < numberOfForms; k++) {
                if (processingStatusDialog.isInterrupted()) {
                    break;
                }
                processingStatusDialog.setMessage(String
                    .format(Localizer.localize("UICDM", "SavingRecordMessage"), (k + 1),
                        numberOfForms));
                String[] rowDataArray = new String[fieldNamesArray.length];
                int l = 0;
                for (Column sortedColumn : sortedColumns) {
                    rowDataArray[l] = sortedColumn.getColumnValue(k);
                    l++;
                }
                writer.writeNext(rowDataArray);
            }

        }

    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public boolean isIncludePublicationID() {
        return includePublicationID;
    }

    public void setIncludePublicationID(boolean includePublicationID) {
        this.includePublicationID = includePublicationID;
    }

    public boolean isIncludeFormID() {
        return includeFormID;
    }

    public void setIncludeFormID(boolean includeFormID) {
        this.includeFormID = includeFormID;
    }

    public boolean isIncludeAggregatedFormScore() {
        return includeAggregatedFormScore;
    }

    public void setIncludeAggregatedFormScore(boolean includeAggregatedFormScore) {
        this.includeAggregatedFormScore = includeAggregatedFormScore;
    }

    public boolean isIncludeMarkScores() {
        return includeIndividualScores;
    }

    public void setIncludeMarkScores(boolean includeMarkScores) {
        this.includeIndividualScores = includeMarkScores;
    }

    public boolean isIncludeProcessedTimes() {
        return includeProcessedTimes;
    }

    public void setIncludeProcessedTimes(boolean includeProcessedTimes) {
        this.includeProcessedTimes = includeProcessedTimes;
    }

    public boolean isIncludeImageFileNames() {
        return includeImageFileNames;
    }

    public void setIncludeImageFileNames(boolean includeImageFileNames) {
        this.includeImageFileNames = includeImageFileNames;
    }

    public boolean isIncludeSourceData() {
        return includeSourceData;
    }

    public void setIncludeSourceData(boolean includeSourceData) {
        this.includeSourceData = includeSourceData;
    }

    public boolean isIncludeCapturedData() {
        return includeCapturedData;
    }

    public void setIncludeCapturedData(boolean includeCapturedData) {
        this.includeCapturedData = includeCapturedData;
    }

    public boolean isIncludeFieldnamesHeader() {
        return includeFieldnamesHeader;
    }

    public void setIncludeFieldnamesHeader(boolean includeFieldnamesHeader) {
        this.includeFieldnamesHeader = includeFieldnamesHeader;
    }

    public ArrayList<Long> getPublicationIds() {
        return publicationIds;
    }

    public void setPublicationIds(ArrayList<Long> publicationIds) {
        this.publicationIds = publicationIds;
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

    public String getFormScoreColumnName() {
        return formScoreColumnName;
    }

    public void setFormScoreColumnName(String formScoreColumnName) {
        this.formScoreColumnName = formScoreColumnName;
    }

    public int getPublicationIdOrderIndex() {
        return publicationIdOrderIndex;
    }

    public void setPublicationIdOrderIndex(int publicationIdOrderIndex) {
        this.publicationIdOrderIndex = publicationIdOrderIndex;
    }

    public int getFormIdOrderIndex() {
        return formIdOrderIndex;
    }

    public void setFormIdOrderIndex(int formIdOrderIndex) {
        this.formIdOrderIndex = formIdOrderIndex;
    }

    public int getFormScoreOrderIndex() {
        return formScoreOrderIndex;
    }

    public void setFormScoreOrderIndex(int formScoreOrderIndex) {
        this.formScoreOrderIndex = formScoreOrderIndex;
    }

    public int getProcessedTimesOrderIndexOffset() {
        return processedTimesOrderIndexOffset;
    }

    public void setProcessedTimesOrderIndexOffset(int processedTimesOrderIndexOffset) {
        this.processedTimesOrderIndexOffset = processedTimesOrderIndexOffset;
    }

    public int getImageFileNamesOrderOffset() {
        return imageFileNamesOrderOffset;
    }

    public void setImageFileNamesOrderOffset(int imageFileNamesOrderOffset) {
        this.imageFileNamesOrderOffset = imageFileNamesOrderOffset;
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

}
