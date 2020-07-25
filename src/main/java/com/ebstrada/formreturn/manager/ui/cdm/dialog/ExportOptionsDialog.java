package com.ebstrada.formreturn.manager.ui.cdm.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.export.ExportMap;
import com.ebstrada.formreturn.manager.logic.export.ExportOptions;
import com.ebstrada.formreturn.manager.logic.export.filter.Filter;
import com.ebstrada.formreturn.manager.logic.export.image.Collation;
import com.ebstrada.formreturn.manager.logic.export.image.ImageExportPreferences;
import com.ebstrada.formreturn.manager.logic.export.image.Overlay;
import com.ebstrada.formreturn.manager.logic.export.xml.XMLExportPreferences;
import com.ebstrada.formreturn.manager.persistence.jpa.Publication;
import com.ebstrada.formreturn.manager.persistence.jpa.PublicationXSL;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.ui.editor.persistence.XSLTemplate;
import com.ebstrada.formreturn.manager.ui.editor.persistence.Templates;
import com.ebstrada.formreturn.manager.ui.filefilter.FilenameExtensionFilter;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.graph.GraphUtils;
import com.ebstrada.formreturn.manager.util.graph.SizeAttributes;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.CSVExportPreferences;

@SuppressWarnings("serial") public class ExportOptionsDialog extends JDialog {

    private int dialogResult = JOptionPane.CANCEL_OPTION;

    private int exportType = ExportOptions.EXPORT_CSV;

    private ArrayList<Filter> filters = new ArrayList<Filter>();

    private ArrayList<Long> publicationIds;

    private HashMap<XSLTemplate, File> xslReports = new HashMap<XSLTemplate, File>();

    private File xmlFile;

    private File xslFile;

    private File csvFile;

    private File imagesFile;

    private File pdfFile;

    private File csvStatsFile;

    private Collation collation;

    private ArrayList<Overlay> overlay = new ArrayList<Overlay>();

    public ExportOptionsDialog(Frame owner) {
        super(owner);
        init(null);
    }

    public ExportOptionsDialog(Dialog owner) {
        super(owner);
        init(null);
    }

    public ExportOptionsDialog(Frame owner, ExportOptions exportOptions) {
        super(owner);
        init(exportOptions);
    }

    public ExportOptionsDialog(Dialog owner, ExportOptions exportOptions) {
        super(owner);
        init(exportOptions);
    }

    private void init(ExportOptions exportOptions) {
        initComponents();
        restoreExportOptions(exportOptions);
        getRootPane().setDefaultButton(createCSVButton);
        localizeHeadings();
        selectExportType();
    }

    private void selectExportType() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                switch (exportType) {

                    case ExportOptions.EXPORT_CSV:
                        tabbedPane.setSelectedIndex(0);
                        break;

                    case ExportOptions.IMAGE_EXPORT:
                        tabbedPane.setSelectedIndex(1);
                        break;

                    case ExportOptions.EXPORT_XML:
                    case ExportOptions.EXPORT_XSLFO_FROM_DATABASE:
                    case ExportOptions.EXPORT_XSLFO_FROM_FILE:
                        tabbedPane.setSelectedIndex(2);
                        break;
                }

            }
        });
    }

    public ExportOptions buildExportOptions() throws IOException {

        ExportOptions exportOptions = new ExportOptions();

        exportOptions.setExportType(getExportType());
        exportOptions.setFilters(getFilters());

        exportOptions.setCsvExportPreferences(buildCSVExportPreferences());
        exportOptions.setImageExportPreferences(buildImageExportPreferences());
        exportOptions.setXmlExportPerferences(buildXMLExportPreferences());

        // set the files last as the paths may change and throw an exception
        exportOptions.setXmlFile(getFileString(getXMLFile()));
        exportOptions.setCsvFile(getFileString(getCSVFile()));
        exportOptions.setCsvStatsFile(getFileString(getCSVStatsFile()));
        exportOptions.setPdfFile(getFileString(getPDFFile()));
        exportOptions.setXslFile(getFileString(getXSLFile()));

        return exportOptions;

    }

    private ImageExportPreferences buildImageExportPreferences() throws IOException {

        ImageExportPreferences imageExportPreferences = new ImageExportPreferences();

        File file = getImagesFile();

        Collation collation = buildCollation();
        ArrayList<Overlay> overlay = buildOverlay();

        SizeAttributes sizeAttributes = getSizeAttributes();

        imageExportPreferences
            .setTimestampFilenamePrefix(this.imageTimestampPrefixCheckBox.isSelected());
        imageExportPreferences.setSizeAttributes(sizeAttributes);
        imageExportPreferences.setCollation(collation);
        imageExportPreferences.setOverlay(overlay);
        imageExportPreferences.setFile(file);
        imageExportPreferences.setCapturedDataColumnCount(getCapturedDataColumnCount());
        imageExportPreferences.setSourceDataColumnCount(getSourceDataColumnCount());
        imageExportPreferences.setColumnFontSize(getColumnFontSize());
        imageExportPreferences.setRotateImage(isRotateImage());

        String imageFilePrefix = "";
        if (collation == Collation.FORM_IMAGES_TOGETHER) {
            imageFilePrefix = getFormImagesFilenamePrefix();
        } else if (collation == Collation.IMAGES_ONLY) {
            imageFilePrefix = getIndividualImagesFilenamePrefix();
        }

        imageExportPreferences.setImageFilePrefix(imageFilePrefix);

        return imageExportPreferences;

    }

    private ArrayList<Overlay> buildOverlay() {
        ArrayList<Overlay> overlayArrayList = new ArrayList<Overlay>();

        if (this.includeGradesCheckBox.isSelected()) {
            overlayArrayList.add(Overlay.GRADES);
        }

        if (this.includeFormScoreCheckBox.isSelected()) {
            overlayArrayList.add(Overlay.FORM_SCORE);
        }

        if (this.includePageScoreCheckBox.isSelected()) {
            overlayArrayList.add(Overlay.PAGE_SCORE);
        }

        if (this.includeSourceDataCheckBox.isSelected()) {
            overlayArrayList.add(Overlay.SOURCE_DATA);
        }

        if (this.includeCapturedDataCheckBox.isSelected()) {
            overlayArrayList.add(Overlay.CAPTURED_DATA);
        }

        if (this.includeIndividualScoresCheckBox.isSelected()) {
            overlayArrayList.add(Overlay.INDIVIDUAL_SCORES);
        }

        return overlayArrayList;
    }

    private Collation buildCollation() {

        if (this.collateImagesRadioButton.isSelected()) {
            return Collation.ALL_IMAGES_TOGETHER;
        }

        if (this.collateFormImagesRadioButton.isSelected()) {
            return Collation.FORM_IMAGES_TOGETHER;
        }

        if (this.individualImagesRadioButton.isSelected()) {
            return Collation.IMAGES_ONLY;
        }

        return null;

    }

    private CSVExportPreferences getCSVExportPreferences(ExportOptions exportOptions) {
        if ((exportOptions != null) && (exportOptions.getCsvExportPreferences() != null)) {
            return exportOptions.getCsvExportPreferences();
        } else {
            return PreferencesManager.getCSVExportPreferences();
        }
    }

    private String getFileString(File file) throws IOException {
        if (file != null) {
            return file.getCanonicalPath();
        } else {
            return null;
        }
    }

    public HashMap<XSLTemplate, File> getXSLReports() {
        return xslReports;
    }

    public void localizeHeadings() {
        setTitle(Localizer.localize("UICDM", "ExportOptionsDialogTitle"));
        this.tabbedPane.setTitleAt(0, Localizer.localize("UICDM", "ExportDelimitedFileTabTitle"));
        this.tabbedPane.setTitleAt(1, Localizer.localize("UICDM", "ExportScannedImagesTabTitle"));
        this.tabbedPane.setTitleAt(2, Localizer.localize("UICDM", "ExportXMLTabTitle"));
    }

    private CSVExportPreferences buildCSVExportPreferences() {

        CSVExportPreferences csvExportPreferences = new CSVExportPreferences();

        csvExportPreferences
            .setTimestampFilenamePrefix(this.csvTimestampPrefixCheckBox.isSelected());
        csvExportPreferences.setOrderedColumnKeys(getOrderedColumnKeys());
        csvExportPreferences.setIncludeStatistics(isIncludeStatistics());
        csvExportPreferences.setIncludeErrorMessages(isIncludeErrorMessages());
        csvExportPreferences.setSortType(getSortType());
        csvExportPreferences.setIncludePublicationID(isIncludePublicationID());
        csvExportPreferences.setIncludeFormID(isIncludeFormID());
        csvExportPreferences.setIncludeFormPassword(isIncludeFormPassword());
        csvExportPreferences.setIncludeFormPageIDs(isIncludeFormPageIDs());
        csvExportPreferences.setIncludeAggregatedFormScore(isIncludeAggregatedFormScore());
        csvExportPreferences.setIncludeAggregatedSegmentScore(isIncludeAggregatedSegmentScore());
        csvExportPreferences.setIncludeIndividualScores(isIncludeIndividualScores());
        csvExportPreferences.setCombineScoresAndData(isCombineScoresAndData());
        csvExportPreferences.setIncludeCaptureTimes(isIncludeCaptureTimes());
        csvExportPreferences.setIncludeProcessedTimes(isIncludeProcessedTimes());
        csvExportPreferences.setIncludeImageFileNames(isIncludeImageFileNames());
        csvExportPreferences.setIncludeScannedPageNumber(isIncludeScannedPageNumber());
        csvExportPreferences.setIncludeSourceData(isIncludeSourceData());
        csvExportPreferences.setIncludeCapturedData(isIncludeCapturedData());
        csvExportPreferences.setIncludeFieldnamesHeader(isIncludeFieldnamesHeader());
        csvExportPreferences.setPublicationIdColumnName(getPublicationIdColumnName());
        csvExportPreferences.setFormIdColumnName(getFormIdColumnName());
        csvExportPreferences.setFormPasswordColumnName(getFormPasswordColumnName());
        csvExportPreferences.setFormPageIdColumnNamePrefix(getFormPageIdColumnNamePrefix());
        csvExportPreferences.setFormScoreColumnName(getFormScoreColumnName());
        csvExportPreferences.setSegmentScoreColumnName(getSegmentScoreColumnName());
        csvExportPreferences.setPublicationIdOrderIndex(getPublicationIdOrderIndex());
        csvExportPreferences.setFormIdOrderIndex(getFormIdOrderIndex());
        csvExportPreferences.setFormPasswordOrderIndex(getFormPasswordOrderIndex());
        csvExportPreferences.setFormPageIDsOrderIndexOffset(getFormPageIDsOrderIndexOffset());
        csvExportPreferences.setFormScoreOrderIndex(getFormScoreOrderIndex());
        csvExportPreferences.setSegmentScoreOrderIndex(getSegmentScoreOrderIndex());
        csvExportPreferences.setCaptureTimesOrderIndexOffset(getCaptureTimesOrderIndexOffset());
        csvExportPreferences.setProcessedTimesOrderIndexOffset(getProcessedTimesOrderIndexOffset());
        csvExportPreferences.setImageFileNamesOrderOffset(getImageFileNamesOrderOffset());
        csvExportPreferences.setScannedPageNumberOrderOffset(getScannedPageNumberOrderOffset());
        csvExportPreferences.setSourceDataOrderIndexOffset(getSourceDataOrderIndexOffset());
        csvExportPreferences.setCapturedDataOrderIndexOffset(getCapturedDataOrderIndexOffset());
        csvExportPreferences.setIndividualScoresOffset(getIndividualScoresOffset());
        csvExportPreferences.setDelimiterType(getDelimiterType());
        csvExportPreferences.setQuotesType(getQuotesType());

        return csvExportPreferences;

    }

    private ArrayList<Integer> getOrderedColumnKeys() {

        ArrayList<Integer> orderedColumnKeys = new ArrayList<Integer>();

        ListModel<ColumnOption> icl = this.includedColumnsList.getModel();

        for (int i = 0; i < icl.getSize(); i++) {
            ColumnOption columnOption = icl.getElementAt(i);
            orderedColumnKeys.add(new Integer(columnOption.getType()));
        }

        return orderedColumnKeys;
    }

    public static void setDefaultColumns(ArrayList<Integer> orderedColumnKeys,
        DefaultListModel includeDLM, DefaultListModel availableDLM,
        CSVExportPreferences csvExportPreferences) {

        ColumnOption col = null;

        HashMap<Integer, ColumnOption> cols = new HashMap<Integer, ColumnOption>();

        col = new ColumnOption(ColumnOption.COLUMN_FORM_SCORE);
        col.setFieldName(csvExportPreferences.getFormScoreColumnName() + "");
        cols.put(col.getType(), col);

        col = new ColumnOption(ColumnOption.COLUMN_SEGMENT_SCORE);
        col.setFieldName(csvExportPreferences.getSegmentScoreColumnName() + "");
        cols.put(col.getType(), col);

        col = new ColumnOption(ColumnOption.COLUMN_CAPTURE_TIME);
        cols.put(col.getType(), col);

        col = new ColumnOption(ColumnOption.COLUMN_PROCESSED_TIME);
        cols.put(col.getType(), col);

        col = new ColumnOption(ColumnOption.COLUMN_SCANNED_PAGE_NUMBER);
        cols.put(col.getType(), col);

        col = new ColumnOption(ColumnOption.COLUMN_IMAGE_FILE_NAMES);
        cols.put(col.getType(), col);

        col = new ColumnOption(ColumnOption.COLUMN_INDIVIDUAL_SCORES);
        cols.put(col.getType(), col);

        col = new ColumnOption(ColumnOption.COLUMN_FORM_ID);
        col.setFieldName(csvExportPreferences.getFormIdColumnName() + "");
        cols.put(col.getType(), col);

        col = new ColumnOption(ColumnOption.COLUMN_FORM_PAGE_IDS);
        col.setFieldName(csvExportPreferences.getFormPageIDsColumnNamePrefix() + "");
        cols.put(col.getType(), col);

        col = new ColumnOption(ColumnOption.COLUMN_FORM_PASSWORD);
        col.setFieldName(csvExportPreferences.getFormPasswordColumnName() + "");
        cols.put(col.getType(), col);

        col = new ColumnOption(ColumnOption.COLUMN_PUBLICATION_ID);
        col.setFieldName(csvExportPreferences.getPublicationIdColumnName() + "");
        cols.put(col.getType(), col);

        col = new ColumnOption(ColumnOption.COLUMN_SOURCE_DATA);
        cols.put(col.getType(), col);

        col = new ColumnOption(ColumnOption.COLUMN_CAPTURED_DATA);
        cols.put(col.getType(), col);

        for (int type : orderedColumnKeys) {

            if (cols.get(type) == null) {
                continue;
            }

            switch (type) {

                case ColumnOption.COLUMN_FORM_SCORE:
                    if (csvExportPreferences.isIncludeAggregated()) {
                        includeDLM.addElement(cols.get(ColumnOption.COLUMN_FORM_SCORE));
                    } else {
                        availableDLM.addElement(cols.get(ColumnOption.COLUMN_FORM_SCORE));
                    }
                    cols.remove(ColumnOption.COLUMN_FORM_SCORE);
                    break;

                case ColumnOption.COLUMN_SEGMENT_SCORE:
                    if (csvExportPreferences.isIncludeAggregatedSegment()) {
                        includeDLM.addElement(cols.get(ColumnOption.COLUMN_SEGMENT_SCORE));
                    } else {
                        availableDLM.addElement(cols.get(ColumnOption.COLUMN_SEGMENT_SCORE));
                    }
                    cols.remove(ColumnOption.COLUMN_SEGMENT_SCORE);
                    break;

                case ColumnOption.COLUMN_CAPTURE_TIME:
                    if (csvExportPreferences.isIncludeCaptureTime()) {
                        includeDLM.addElement(cols.get(ColumnOption.COLUMN_CAPTURE_TIME));
                    } else {
                        availableDLM.addElement(cols.get(ColumnOption.COLUMN_CAPTURE_TIME));
                    }
                    cols.remove(ColumnOption.COLUMN_CAPTURE_TIME);
                    break;

                case ColumnOption.COLUMN_PROCESSED_TIME:
                    if (csvExportPreferences.isIncludeProcessedTime()) {
                        includeDLM.addElement(cols.get(ColumnOption.COLUMN_PROCESSED_TIME));
                    } else {
                        availableDLM.addElement(cols.get(ColumnOption.COLUMN_PROCESSED_TIME));
                    }
                    cols.remove(ColumnOption.COLUMN_PROCESSED_TIME);
                    break;

                case ColumnOption.COLUMN_IMAGE_FILE_NAMES:
                    if (csvExportPreferences.isIncludeImageFileNames()) {
                        includeDLM.addElement(cols.get(ColumnOption.COLUMN_IMAGE_FILE_NAMES));
                    } else {
                        availableDLM.addElement(cols.get(ColumnOption.COLUMN_IMAGE_FILE_NAMES));
                    }
                    cols.remove(ColumnOption.COLUMN_IMAGE_FILE_NAMES);
                    break;

                case ColumnOption.COLUMN_SCANNED_PAGE_NUMBER:
                    if (csvExportPreferences.isIncludeScannedPageNumber()) {
                        includeDLM.addElement(cols.get(ColumnOption.COLUMN_SCANNED_PAGE_NUMBER));
                    } else {
                        availableDLM.addElement(cols.get(ColumnOption.COLUMN_SCANNED_PAGE_NUMBER));
                    }
                    cols.remove(ColumnOption.COLUMN_SCANNED_PAGE_NUMBER);
                    break;

                case ColumnOption.COLUMN_INDIVIDUAL_SCORES:
                    if (csvExportPreferences.isIncludeMarkScores()) {
                        includeDLM.addElement(cols.get(ColumnOption.COLUMN_INDIVIDUAL_SCORES));
                    } else {
                        availableDLM.addElement(cols.get(ColumnOption.COLUMN_INDIVIDUAL_SCORES));
                    }
                    cols.remove(ColumnOption.COLUMN_INDIVIDUAL_SCORES);
                    break;

                case ColumnOption.COLUMN_FORM_ID:
                    if (csvExportPreferences.isIncludeFormID()) {
                        includeDLM.addElement(cols.get(ColumnOption.COLUMN_FORM_ID));
                    } else {
                        availableDLM.addElement(cols.get(ColumnOption.COLUMN_FORM_ID));
                    }
                    cols.remove(ColumnOption.COLUMN_FORM_ID);
                    break;

                case ColumnOption.COLUMN_FORM_PASSWORD:
                    if (csvExportPreferences.isIncludeFormPassword()) {
                        includeDLM.addElement(cols.get(ColumnOption.COLUMN_FORM_PASSWORD));
                    } else {
                        availableDLM.addElement(cols.get(ColumnOption.COLUMN_FORM_PASSWORD));
                    }
                    cols.remove(ColumnOption.COLUMN_FORM_PASSWORD);
                    break;

                case ColumnOption.COLUMN_FORM_PAGE_IDS:
                    if (csvExportPreferences.isIncludeFormPageIDs()) {
                        includeDLM.addElement(cols.get(ColumnOption.COLUMN_FORM_PAGE_IDS));
                    } else {
                        availableDLM.addElement(cols.get(ColumnOption.COLUMN_FORM_PAGE_IDS));
                    }
                    cols.remove(ColumnOption.COLUMN_FORM_PAGE_IDS);
                    break;

                case ColumnOption.COLUMN_PUBLICATION_ID:
                    if (csvExportPreferences.isIncludePublicationID()) {
                        includeDLM.addElement(cols.get(ColumnOption.COLUMN_PUBLICATION_ID));
                    } else {
                        availableDLM.addElement(cols.get(ColumnOption.COLUMN_PUBLICATION_ID));
                    }
                    cols.remove(ColumnOption.COLUMN_PUBLICATION_ID);
                    break;

                case ColumnOption.COLUMN_SOURCE_DATA:
                    if (csvExportPreferences.isIncludeSourceData()) {
                        includeDLM.addElement(cols.get(ColumnOption.COLUMN_SOURCE_DATA));
                    } else {
                        availableDLM.addElement(cols.get(ColumnOption.COLUMN_SOURCE_DATA));
                    }
                    cols.remove(ColumnOption.COLUMN_SOURCE_DATA);
                    break;

                case ColumnOption.COLUMN_CAPTURED_DATA:
                    if (csvExportPreferences.isIncludeCapturedData()) {
                        includeDLM.addElement(cols.get(ColumnOption.COLUMN_CAPTURED_DATA));
                    } else {
                        availableDLM.addElement(cols.get(ColumnOption.COLUMN_CAPTURED_DATA));
                    }
                    cols.remove(ColumnOption.COLUMN_CAPTURED_DATA);
                    break;

            }

        }

        for (ColumnOption co : cols.values()) {
            availableDLM.addElement(co);
        }

    }

    public void restoreExportOptions(ExportOptions exportOptions) {
        if (exportOptions != null) {
            setExportType(exportOptions.getExportType());
            setPublicationIds(exportOptions.getPublicationIds());
        }
        restoreCSVExportOptions(exportOptions);
        restoreImageExportOptions(exportOptions);
        restoreXMLExportOptions(exportOptions);
        restoreFilterOptions(exportOptions);
    }

    private void restoreCSVExportOptions(ExportOptions exportOptions) {
        CSVExportPreferences csvExportPreferences = getCSVExportPreferences(exportOptions);
        restoreCSVExportOptions(csvExportPreferences);
    }

    private void restoreCSVExportOptions(CSVExportPreferences csvExportPreferences) {

        restorePageSizeComboBox();

        switch (csvExportPreferences.getSortType()) {
            case ExportMap.SORT_NATRUAL_ASCENDING:
                sortTypeComboBox.setSelectedIndex(1);
                break;
            case ExportMap.SORT_NATURAL_DECENDING:
                sortTypeComboBox.setSelectedIndex(2);
                break;
            case ExportMap.SORT_BY_ORDER_INDEX:
            default:
                sortTypeComboBox.setSelectedIndex(0);
        }

        DefaultListModel includeDLM = new DefaultListModel();
        DefaultListModel availableDLM = new DefaultListModel();

        setDefaultColumns(csvExportPreferences.getOrderedColumnKeys(), includeDLM, availableDLM,
            csvExportPreferences);

        this.includedColumnsList.setModel(includeDLM);
        this.availableColumnsList.setModel(availableDLM);

        combineScoresAndDataCheckBox.setSelected(csvExportPreferences.isCombineScoresAndData());

        includeFieldnamesHeaderCheckBox
            .setSelected(csvExportPreferences.isIncludeFieldnamesHeader());

        includeStatisticsCheckBox.setSelected(csvExportPreferences.isIncludeStatistics());
        includeErrorMessagesCheckBox.setSelected(csvExportPreferences.isIncludeErrorMessages());
        this.csvTimestampPrefixCheckBox
            .setSelected(csvExportPreferences.isTimestampFilenamePrefix());

        switch (csvExportPreferences.getDelimiterType()) {
            case CSVExportPreferences.TSV_DELIMITER:
                delimiterComboBox.setSelectedIndex(1);
                break;
            case CSVExportPreferences.CSV_DELIMITER:
            default:
                delimiterComboBox.setSelectedIndex(0);
        }

        switch (csvExportPreferences.getQuotesType()) {
            case CSVExportPreferences.NO_QUOTES:
                quotesComboBox.setSelectedIndex(2);
                break;
            case CSVExportPreferences.SINGLE_QUOTES:
                quotesComboBox.setSelectedIndex(1);
                break;
            case CSVExportPreferences.DOUBLE_QUOTES:
            default:
                quotesComboBox.setSelectedIndex(0);
        }

    }

    private ImageExportPreferences getImageExportPreferences(ExportOptions exportOptions) {

        if (exportOptions != null && exportOptions.getImageExportPreferences() != null) {
            return exportOptions.getImageExportPreferences();
        } else {
            return PreferencesManager.getImageExportPreferences();
        }
    }

    private XMLExportPreferences getXMLExportPreferences(ExportOptions exportOptions) {
        if (exportOptions != null && exportOptions.getXmlExportPreferences() != null) {
            return exportOptions.getXmlExportPreferences();
        } else {
            return PreferencesManager.getXMLExportPreferences();
        }
    }

    private void restoreImageExportOptions(ExportOptions exportOptions) {
        ImageExportPreferences imageExportPreferences = getImageExportPreferences(exportOptions);
        restoreImageExportOptions(imageExportPreferences);
    }

    private void restoreImageExportOptions(ImageExportPreferences imageExportPreferences) {

        restoreOverlay(imageExportPreferences.getOverlay());
        restoreCollation(imageExportPreferences.getCollation());

        if (imageExportPreferences.getCollation() == Collation.FORM_IMAGES_TOGETHER) {
            this.formImagesFilenamePrefixTextField.setText(getFormImagesFilenamePrefix());
        } else if (imageExportPreferences.getCollation() == Collation.IMAGES_ONLY) {
            this.individualImagesFilenamePrefixTextField
                .setText(getIndividualImagesFilenamePrefix());
        }

        SizeAttributes sizeAttributes = imageExportPreferences.getSizeAttributes();
        if (sizeAttributes != null) {
            pageSizeComboBox.setSelectedItem(sizeAttributes.getName());
            if (pageSizeComboBox.getSelectedIndex() == -1) {
                pageSizeComboBox.setSelectedIndex(0);
            }
        }
        this.imageTimestampPrefixCheckBox
            .setSelected(imageExportPreferences.isTimestampFilenamePrefix());
        this.capturedDataColumnsSpinner
            .setValue(imageExportPreferences.getCapturedDataColumnCount());
        this.sourceDataColumnsSpinner.setValue(imageExportPreferences.getSourceDataColumnCount());
        this.columnFontSizeSpinner
            .setValue(new Float(imageExportPreferences.getColumnFontSize()).intValue());
        this.rotateImageCheckBox.setSelected(imageExportPreferences.isImageRotated());

    }

    private void restoreCollation(Collation collation) {

        if (collation == null) {
            return;
        }

        switch (collation) {
            case ALL_IMAGES_TOGETHER:
                this.collateImagesRadioButton.setSelected(true);
                break;

            case FORM_IMAGES_TOGETHER:
                this.collateFormImagesRadioButton.setSelected(true);
                break;

            case IMAGES_ONLY:
                this.individualImagesRadioButton.setSelected(true);
                break;
        }
    }

    private void restoreOverlay(ArrayList<Overlay> overlayArrayList) {

        if (overlayArrayList == null) {
            return;
        }

        this.includeGradesCheckBox.setSelected(false);
        this.includeFormScoreCheckBox.setSelected(false);
        this.includePageScoreCheckBox.setSelected(false);
        this.includeSourceDataCheckBox.setSelected(false);
        this.includeCapturedDataCheckBox.setSelected(false);
        this.includeIndividualScoresCheckBox.setSelected(false);

        for (Overlay overlay : overlayArrayList) {

            switch (overlay) {

                case GRADES:
                    this.includeGradesCheckBox.setSelected(true);
                    break;

                case FORM_SCORE:
                    this.includeFormScoreCheckBox.setSelected(true);
                    break;

                case PAGE_SCORE:
                    this.includePageScoreCheckBox.setSelected(true);
                    break;

                case SOURCE_DATA:
                    this.includeSourceDataCheckBox.setSelected(true);
                    break;

                case CAPTURED_DATA:
                    this.includeCapturedDataCheckBox.setSelected(true);
                    break;

                case INDIVIDUAL_SCORES:
                    this.includeIndividualScoresCheckBox.setSelected(true);
                    break;

            }
        }

    }

    private void restoreXMLExportOptions(ExportOptions exportOptions) {
        XMLExportPreferences xmlExportPreferences = getXMLExportPreferences(exportOptions);
        restoreXMLExportOptions(xmlExportPreferences);
    }

    private void restoreXMLExportOptions(XMLExportPreferences xmlExportPreferences) {
        this.xmlTimestampPrefixCheckBox
            .setSelected(xmlExportPreferences.isTimestampFilenamePrefix());
        this.includeIndividualScoresXMLCheckBox
            .setSelected(xmlExportPreferences.isIncludeIndividualScores());
        this.includeIndividualResponsesXMLCheckBox
            .setSelected(xmlExportPreferences.isIncludeIndividualResponses());
        this.includeStatisticsXMLCheckBox.setSelected(xmlExportPreferences.isIncludeStatistics());
        this.includeXMLHeaderCheckBox.setSelected(xmlExportPreferences.isIncludeXMLHeader());
        this.indentXMLContentCheckBox.setSelected(xmlExportPreferences.isIndentXMLContent());
        setSelectedXSLTemplate(xmlExportPreferences.getSelectedXSLTemplate());
    }

    private XMLExportPreferences buildXMLExportPreferences() {
        XMLExportPreferences xmlExportPreferences = new XMLExportPreferences();
        xmlExportPreferences
            .setTimestampFilenamePrefix(this.xmlTimestampPrefixCheckBox.isSelected());
        xmlExportPreferences
            .setIncludeIndividualScores(includeIndividualScoresXMLCheckBox.isSelected());
        xmlExportPreferences
            .setIncludeIndividualResponses(includeIndividualResponsesXMLCheckBox.isSelected());
        xmlExportPreferences.setIncludeStatistics(includeStatisticsXMLCheckBox.isSelected());
        xmlExportPreferences.setIncludeXMLHeader(includeXMLHeaderCheckBox.isSelected());
        xmlExportPreferences.setIndentXMLContent(indentXMLContentCheckBox.isSelected());
        xmlExportPreferences.setSelectedXSLTemplate(getSelectedXSLTemplate());
        return xmlExportPreferences;
    }

    private void setSelectedXSLTemplate(XSLTemplate selectedXSLTemplate) {
        if (this.xslTemplateList.getModel().getSize() > 0) {
            ListModel<XSLTemplate> model = this.xslTemplateList.getModel();
            for (int i = 0; i < model.getSize(); i++) {
                XSLTemplate xslTemplate = model.getElementAt(i);
                if (xslTemplate.getTemplateGUID()
                    .equalsIgnoreCase(selectedXSLTemplate.getTemplateGUID())) {
                    xslTemplateList.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private XSLTemplate getSelectedXSLTemplate() {
        if (this.xslTemplateList.getSelectedIndex() >= 0) {
            return (XSLTemplate) this.xslTemplateList.getSelectedValue();
        } else {
            return null;
        }
    }

    private void restoreFilterOptions(ExportOptions exportOptions) {
        this.filters = getFilterPreferences(exportOptions);
    }

    private ArrayList<Filter> getFilterPreferences(ExportOptions exportOptions) {
        if (exportOptions != null && exportOptions.getFilters() != null) {
            return exportOptions.getFilters();
        } else {
            return PreferencesManager.getExportFilterPreferences();
        }
    }

    private void restorePageSizeComboBox() {
        List<String> pageSizeNames = PreferencesManager.getFormSizeNames();

        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        for (String pageSizeName : pageSizeNames) {
            if (pageSizeName.equalsIgnoreCase("custom")) {
                continue;
            }
            dcbm.addElement(pageSizeName);
        }

        pageSizeComboBox.setModel(dcbm);

        SizeAttributes sa = PreferencesManager.getDefaultFormSizeAttributes();

        if (sa == null) {
            return;
        }

        String sizeName = sa.getName();
        if (sizeName == null) {
            return;
        }

        pageSizeComboBox.setSelectedItem(sa.getName());
        if (pageSizeComboBox.getSelectedIndex() == -1) {
            pageSizeComboBox.setSelectedIndex(0);
        }

    }

    private SizeAttributes getNamedPageSize(String size) {
        SizeAttributes sizeAttributes = new SizeAttributes();
        sizeAttributes =
            GraphUtils.getDefaultSizeAttributes(SizeAttributes.FORM, SizeAttributes.PORTRAIT, size);
        return sizeAttributes;
    }

    private void restoreXSLTemplatesList() {

        EntityManager entityManager = null;
        try {
            entityManager = getEntityManager();
            DefaultListModel dlm = new DefaultListModel();
            for (long publicationId : this.publicationIds) {
                Publication publication = entityManager.find(Publication.class, publicationId);
                List<PublicationXSL> xslTemplates = publication.getPublicationXSLCollection();
                for (PublicationXSL publicationXSL : xslTemplates) {
                    XSLTemplate xlsTemplate = Templates.createXSLTemplate(publicationXSL);
                    dlm.addElement(xlsTemplate);
                }
            }

            this.xslTemplateList.setModel(dlm);

        } catch (Exception ex) {
            Misc.printStackTrace(ex);
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

    private EntityManager getEntityManager() {
        if (com.ebstrada.formreturn.manager.ui.Main.getInstance() != null) {
            return com.ebstrada.formreturn.manager.ui.Main.getInstance().getJPAConfiguration()
                .getEntityManager();
        } else {
            return com.ebstrada.formreturn.server.Main.getInstance().getJPAConfiguration()
                .getEntityManager();
        }
    }

    public File getXMLFile() {
        return xmlFile;
    }

    public File getCSVFile() {
        return this.csvFile;
    }

    public File getImagesFile() {
        return this.imagesFile;
    }

    public File getCSVStatsFile() {
        return this.csvStatsFile;
    }

    private void createCSVButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                File csvFile = selectCSVFileAndExport();
                if (csvFile == null) {
                    return;
                }
                File csvStatsFile = null;
                if (includeStatisticsCheckBox.isSelected()) {
                    csvStatsFile = selectStatisticsCSVFileAndExport();
                    if (csvStatsFile == null) {
                        return;
                    }
                }
                exportCSV(csvFile, csvStatsFile);
            }
        });
    }

    private File selectImagesDirectory() {

        File lastDir = null;
        if (Globals.getLastDirectory() != null) {

            lastDir = new File(Globals.getLastDirectory());

            if (!(lastDir.exists())) {
                lastDir = null;
            }

        }

        if (lastDir == null) {
            lastDir = new File(System.getProperty("user.home"));
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(lastDir);
        chooser.setDialogTitle(Localizer.localize("UICDM", "ExportOptionsSelectImagesFolderTitle"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        chooser.rescanCurrentDirectory();

        if (chooser.showDialog(this, Localizer.localize("UI", "ChooseButtonText"))
            == JFileChooser.APPROVE_OPTION) {
            File imagesDirectory = chooser.getSelectedFile();
            return imagesDirectory;
        } else {
            return null;
        }

    }

    public File selectStatisticsCSVFileAndExport() {

        FileDialog fd = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();

        File csvOutputFile = null;

        File lastDir = null;
        if (Globals.getLastDirectory() != null) {

            lastDir = new File(Globals.getLastDirectory());

            if (!(lastDir.exists())) {
                lastDir = null;
            }

        }

        if (lastDir == null) {
            lastDir = new File(System.getProperty("user.home"));
        }

        filter.addExtension("csv");
        fd = new FileDialog(this, Localizer.localize("UICDM", "SaveStatsCSVExportFileMessage"),
            FileDialog.SAVE);
        try {
            fd.setDirectory(lastDir.getCanonicalPath());
        } catch (IOException e1) {
        }
        fd.setFilenameFilter(filter);

        String filename =
            Localizer.localize("UICDM", "CapturedDataExportCSVStatsFilePrefix") + ".csv";

        fd.setFile(filename);
        fd.setModal(true);
        fd.setVisible(true);

        if (fd.getFile() != null) {
            String fileString =
                fd.getDirectory() + System.getProperty("file.separator") + fd.getFile();

            if (!(fileString.endsWith(".csv") || fileString.endsWith(".CSV"))) {
                fileString += ".csv";
            }

            csvOutputFile = new File(fileString);

            if (csvOutputFile.isDirectory()) {
                return null;
            }

            return csvOutputFile;

        } else {
            return null;
        }

    }

    public int getSortType() {
        switch (sortTypeComboBox.getSelectedIndex()) {
            case 1:
                return ExportMap.SORT_NATRUAL_ASCENDING;
            case 2:
                return ExportMap.SORT_NATURAL_DECENDING;
            case 0:
            default:
                return ExportMap.SORT_BY_ORDER_INDEX;
        }
    }

    public int getExportType() {
        return exportType;
    }

    public void setExportType(int exportType) {
        this.exportType = exportType;
    }

    public ColumnOption getColumnOption(int type, ListModel dlm) {
        for (int i = 0; i < dlm.getSize(); i++) {
            ColumnOption col = (ColumnOption) dlm.getElementAt(i);
            if (col.getType() == type) {
                return col;
            }
        }
        return null;
    }

    public boolean isIncludeAggregatedFormScore() {
        ColumnOption col =
            getColumnOption(ColumnOption.COLUMN_FORM_SCORE, this.includedColumnsList.getModel());
        if (col != null) {
            return true;
        } else {
            return false;
        }
    }


    public boolean isIncludeAggregatedSegmentScore() {
        ColumnOption col =
            getColumnOption(ColumnOption.COLUMN_SEGMENT_SCORE, this.includedColumnsList.getModel());
        if (col != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isIncludeIndividualScores() {
        ColumnOption col = getColumnOption(ColumnOption.COLUMN_INDIVIDUAL_SCORES,
            this.includedColumnsList.getModel());
        if (col != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isIncludePublicationID() {
        ColumnOption col = getColumnOption(ColumnOption.COLUMN_PUBLICATION_ID,
            this.includedColumnsList.getModel());
        if (col != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isIncludeFormID() {
        ColumnOption col =
            getColumnOption(ColumnOption.COLUMN_FORM_ID, this.includedColumnsList.getModel());
        if (col != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isIncludeSourceData() {
        ColumnOption col =
            getColumnOption(ColumnOption.COLUMN_SOURCE_DATA, this.includedColumnsList.getModel());
        if (col != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isIncludeCapturedData() {
        ColumnOption col =
            getColumnOption(ColumnOption.COLUMN_CAPTURED_DATA, this.includedColumnsList.getModel());
        if (col != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isIncludeCaptureTimes() {
        ColumnOption col =
            getColumnOption(ColumnOption.COLUMN_CAPTURE_TIME, this.includedColumnsList.getModel());
        if (col != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isIncludeProcessedTimes() {
        ColumnOption col = getColumnOption(ColumnOption.COLUMN_PROCESSED_TIME,
            this.includedColumnsList.getModel());
        if (col != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isIncludeImageFileNames() {
        ColumnOption col = getColumnOption(ColumnOption.COLUMN_IMAGE_FILE_NAMES,
            this.includedColumnsList.getModel());
        if (col != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isIncludeScannedPageNumber() {
        ColumnOption col = getColumnOption(ColumnOption.COLUMN_SCANNED_PAGE_NUMBER,
            this.includedColumnsList.getModel());
        if (col != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isIncludeFieldnamesHeader() {
        return this.includeFieldnamesHeaderCheckBox.isSelected();
    }

    public int getIncludedColumnIndex(int type) {
        ListModel model = this.includedColumnsList.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            ColumnOption col = (ColumnOption) model.getElementAt(i);
            if (col.getType() == type) {
                return i;
            }
        }
        return 0;
    }

    public int getImageFileNamesOrderOffset() {
        return getIncludedColumnIndex(ColumnOption.COLUMN_IMAGE_FILE_NAMES);
    }

    public int getScannedPageNumberOrderOffset() {
        return getIncludedColumnIndex(ColumnOption.COLUMN_SCANNED_PAGE_NUMBER);
    }

    public int getCapturedDataOrderIndexOffset() {
        return getIncludedColumnIndex(ColumnOption.COLUMN_CAPTURED_DATA);
    }

    public int getPublicationIdOrderIndex() {
        return getIncludedColumnIndex(ColumnOption.COLUMN_PUBLICATION_ID);
    }

    public int getSourceDataOrderIndexOffset() {
        return getIncludedColumnIndex(ColumnOption.COLUMN_SOURCE_DATA);
    }

    public int getCaptureTimesOrderIndexOffset() {
        return getIncludedColumnIndex(ColumnOption.COLUMN_CAPTURE_TIME);
    }

    public int getProcessedTimesOrderIndexOffset() {
        return getIncludedColumnIndex(ColumnOption.COLUMN_PROCESSED_TIME);
    }

    public int getFormScoreOrderIndex() {
        return getIncludedColumnIndex(ColumnOption.COLUMN_FORM_SCORE);
    }

    public int getSegmentScoreOrderIndex() {
        return getIncludedColumnIndex(ColumnOption.COLUMN_SEGMENT_SCORE);
    }

    public int getFormIdOrderIndex() {
        return getIncludedColumnIndex(ColumnOption.COLUMN_FORM_ID);
    }

    public int getDelimiterType() {
        switch (delimiterComboBox.getSelectedIndex()) {
            case 1:
                return CSVExportPreferences.TSV_DELIMITER;
            case 0:
            default:
                return CSVExportPreferences.CSV_DELIMITER;
        }
    }

    public int getQuotesType() {
        switch (quotesComboBox.getSelectedIndex()) {
            case 2:
                return CSVExportPreferences.NO_QUOTES;
            case 1:
                return CSVExportPreferences.SINGLE_QUOTES;
            case 0:
            default:
                return CSVExportPreferences.DOUBLE_QUOTES;
        }
    }

    public String getFormIdColumnName() {
        ColumnOption col =
            getColumnOption(ColumnOption.COLUMN_FORM_ID, this.includedColumnsList.getModel());
        if (col == null) {
            col =
                getColumnOption(ColumnOption.COLUMN_FORM_ID, this.availableColumnsList.getModel());
        }
        return col.getFieldName();
    }

    public String getFormScoreColumnName() {
        ColumnOption col =
            getColumnOption(ColumnOption.COLUMN_FORM_SCORE, this.includedColumnsList.getModel());
        if (col == null) {
            col = getColumnOption(ColumnOption.COLUMN_FORM_SCORE,
                this.availableColumnsList.getModel());
        }
        return col.getFieldName();
    }

    public String getSegmentScoreColumnName() {
        ColumnOption col =
            getColumnOption(ColumnOption.COLUMN_SEGMENT_SCORE, this.includedColumnsList.getModel());
        if (col == null) {
            col = getColumnOption(ColumnOption.COLUMN_SEGMENT_SCORE,
                this.availableColumnsList.getModel());
        }
        return col.getFieldName();
    }

    public String getPublicationIdColumnName() {
        ColumnOption col = getColumnOption(ColumnOption.COLUMN_PUBLICATION_ID,
            this.includedColumnsList.getModel());
        if (col == null) {
            col = getColumnOption(ColumnOption.COLUMN_PUBLICATION_ID,
                this.availableColumnsList.getModel());
        }
        return col.getFieldName();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createCSVButton.requestFocusInWindow();
            }
        });
    }

    private void exportXMLButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                selectXMLFileAndExport();
            }
        });
    }

    private File selectCSVFileAndExport() {

        FileDialog fd = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();

        File csvOutputFile = null;

        File lastDir = null;
        if (Globals.getLastDirectory() != null) {

            lastDir = new File(Globals.getLastDirectory());

            if (!(lastDir.exists())) {
                lastDir = null;
            }

        }

        if (lastDir == null) {
            lastDir = new File(System.getProperty("user.home"));
        }

        filter.addExtension("csv");
        fd = new FileDialog(this, Localizer.localize("UICDM", "SaveCSVExportFileMessage"),
            FileDialog.SAVE);
        try {
            fd.setDirectory(lastDir.getCanonicalPath());
        } catch (IOException e1) {
        }
        fd.setFilenameFilter(filter);

        String filename = Localizer.localize("UICDM", "CapturedDataExportCSVFilePrefix") + ".csv";

        fd.setFile(filename);
        fd.setModal(true);
        fd.setVisible(true);

        if (fd.getFile() != null) {
            String fileString =
                fd.getDirectory() + System.getProperty("file.separator") + fd.getFile();

            if (!(fileString.endsWith(".csv") || fileString.endsWith(".CSV"))) {
                fileString += ".csv";
            }

            csvOutputFile = new File(fileString);

            if (csvOutputFile.isDirectory()) {
                return null;
            }

            return csvOutputFile;

        } else {
            return null;
        }

    }


    private void selectXMLFileAndExport() {

        FileDialog fd = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();

        File xmlOutputFile = null;

        File lastDir = null;
        if (Globals.getLastDirectory() != null) {

            lastDir = new File(Globals.getLastDirectory());

            if (!(lastDir.exists())) {
                lastDir = null;
            }

        }

        if (lastDir == null) {
            lastDir = new File(System.getProperty("user.home"));
        }

        filter.addExtension("xml");
        fd = new FileDialog(this, "Export Captured Data XML File", FileDialog.SAVE);
        try {
            fd.setDirectory(lastDir.getCanonicalPath());
        } catch (IOException e1) {
        }
        fd.setFilenameFilter(filter);

        String filename = "captured_data_export.xml";

        fd.setFile(filename);
        fd.setModal(true);
        fd.setVisible(true);

        if (fd.getFile() != null) {
            String fileString =
                fd.getDirectory() + System.getProperty("file.separator") + fd.getFile();

            if (!(fileString.endsWith(".xml") || fileString.endsWith(".xml"))) {
                fileString += ".xml";
            }

            xmlOutputFile = new File(fileString);

            if (xmlOutputFile.isDirectory()) {
                return;
            }

            exportXML(xmlOutputFile);
        } else {
            return;
        }

    }

    private void exportXML(File xmlOutputFile) {
        this.xmlFile = xmlOutputFile;
        this.dialogResult = JOptionPane.OK_OPTION;
        this.exportType = ExportOptions.EXPORT_XML;
        dispose();
    }

    private void exportCSV(File csvOutputFile, File csvStatsFile) {
        this.csvFile = csvOutputFile;
        this.csvStatsFile = csvStatsFile;
        this.dialogResult = JOptionPane.OK_OPTION;
        if (this.includeStatisticsCheckBox.isSelected()) {
            this.exportType = ExportOptions.EXPORT_CSV_WITH_STATS;
        } else {
            this.exportType = ExportOptions.EXPORT_CSV;
        }
        dispose();
    }

    public ArrayList<Filter> getFilters() {
        return filters;
    }

    private void createXSLFOReportButtonActionPerformed(ActionEvent e) {
        final ExportOptionsDialog thisDialog = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    selectXSLReport();
                } catch (Exception ex) {
                    Misc.showExceptionMsg(thisDialog, ex);
                }
            }
        });
    }

    private File selectPDFOutputFile(String inputFileName) {

        FileDialog fd = null;

        try {

            FilenameExtensionFilter filter = new FilenameExtensionFilter();

            File file = null;

            File lastDir = null;
            if (Globals.getLastDirectory() != null) {

                lastDir = new File(Globals.getLastDirectory());

                if (!(lastDir.exists())) {
                    lastDir = null;
                }

            }

            if (lastDir == null) {
                lastDir = new File(System.getProperty("user.home"));
            }

            filter.addExtension("pdf");
            fd = new FileDialog(this, "Save XSL Report As", FileDialog.SAVE);
            try {
                fd.setDirectory(lastDir.getCanonicalPath());
            } catch (IOException e1) {
            }
            fd.setFilenameFilter(filter);

            String xslFileName = inputFileName;
            String[] nameParts = xslFileName.split("\\.");
            String filename = ((nameParts.length > 0) ? nameParts[0] : xslFileName) + ".pdf";

            fd.setFile(filename);
            fd.setModal(true);
            fd.setVisible(true);

            if (fd.getFile() != null) {
                String fileString =
                    fd.getDirectory() + System.getProperty("file.separator") + fd.getFile();
                file = new File(fileString);
                return file;
            } else {
                return null;
            }

        } finally {
            if (fd != null) {
                fd.dispose();
            }
        }

    }

    private File selectXSLFile() throws IOException {
        FilenameExtensionFilter filter = new FilenameExtensionFilter();
        filter.addExtension("xsl");

        FileDialog fd = new FileDialog((Dialog) getRootPane().getTopLevelAncestor(),
            Localizer.localize("UI", "SelectXSLTemplateFileDialogTitle"), FileDialog.LOAD);
        fd.setFilenameFilter(filter);

        File lastDir = null;
        if (Globals.getLastDirectory() != null) {

            lastDir = new File(Globals.getLastDirectory());

            if (!(lastDir.exists())) {
                lastDir = null;
            }

        }

        if (lastDir == null) {
            lastDir = new File(System.getProperty("user.home"));
        }

        fd.setDirectory(lastDir.getCanonicalPath());

        fd.setLocationByPlatform(false);
        fd.setLocationRelativeTo((Dialog) getRootPane().getTopLevelAncestor());
        fd.setModal(true);
        fd.setVisible(true);
        if (fd.getFile() != null) {
            String filename = fd.getFile();
            return new File(fd.getDirectory() + filename);
        } else {
            return null;
        }

    }

    private void selectXSLReport() throws IOException {

        XSLTemplate xslTemplate = (XSLTemplate) this.xslTemplateList.getSelectedValue();

        if (xslTemplate == null || !(xslTemplate instanceof XSLTemplate)) {

            String msg =
                "An XSL template was not selected from the list.\nWould you like to select an XSL file from your computer?";
            Object[] options =
                {Localizer.localize("Util", "Yes"), Localizer.localize("Util", "No")};

            int result = JOptionPane.showOptionDialog(getRootPane().getTopLevelAncestor(), msg,
                Localizer.localize("Util", "WarningTitle"), JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, options[0]);

            if (result != 0) {
                return;
            }

            File xslFile = selectXSLFile();
            File pdfFile = selectPDFOutputFile(xslFile.getName());
            if (xslFile != null && pdfFile != null) {
                exportXSLReport(xslFile, pdfFile);
                this.exportType = ExportOptions.EXPORT_XSLFO_FROM_FILE;
                this.dialogResult = JOptionPane.OK_OPTION;
                dispose();
            } else {
                return;
            }
        } else {
            String inputFileName = xslTemplate.getFileName();
            File pdfFile = selectPDFOutputFile(inputFileName);
            if (pdfFile != null) {
                exportXSLReport(xslTemplate, pdfFile);
                this.exportType = ExportOptions.EXPORT_XSLFO_FROM_DATABASE;
                this.dialogResult = JOptionPane.OK_OPTION;
                dispose();
            } else {
                return;
            }
        }

    }

    private void exportXSLReport(File xslFile, File pdfFile) {
        this.xslFile = xslFile;
        this.pdfFile = pdfFile;
    }

    public File getXSLFile() {
        return xslFile;
    }

    public File getPDFFile() {
        return pdfFile;
    }

    private void exportXSLReport(XSLTemplate xslTemplate, File file) {
        this.xslReports.put(xslTemplate, file);
    }

    private void exportFilterSettingsButtonActionPerformed(ActionEvent e) {
        final ExportOptionsDialog thisDialog = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ExportFilterSettingsDialog efsd =
                    new ExportFilterSettingsDialog(thisDialog, filters);
                efsd.setModal(true);
                efsd.setVisible(true);
                if (efsd.getDialogResult() == JOptionPane.OK_OPTION) {
                    filters = efsd.getFilters();
                }
            }
        });
    }

    private void updateColumnName() {
        int index = this.includedColumnsList.getSelectedIndex();
        if (index < 0) {
            return;
        }
        if (columnNameTextField.getText().trim().length() <= 0) {
            return;
        }
        ColumnOption col = (ColumnOption) this.includedColumnsList.getModel().getElementAt(index);
        col.setFieldName(columnNameTextField.getText().trim());
    }

    private void includedColumnsListValueChanged(ListSelectionEvent e) {

        this.columnNameTextField.setText("");

        int index = this.includedColumnsList.getSelectedIndex();
        if (index < 0) {
            return;
        }

        ColumnOption col = (ColumnOption) this.includedColumnsList.getModel().getElementAt(index);

        int colType = col.getType();

        switch (colType) {
            case ColumnOption.COLUMN_PUBLICATION_ID:
            case ColumnOption.COLUMN_FORM_ID:
            case ColumnOption.COLUMN_FORM_PASSWORD:
            case ColumnOption.COLUMN_FORM_PAGE_IDS:
            case ColumnOption.COLUMN_FORM_SCORE:
            case ColumnOption.COLUMN_SEGMENT_SCORE:
                this.columnNameTextField.setEnabled(true);
                this.columnNameTextField.setText(col.getFieldName());
                break;

            default:
                this.columnNameTextField.setEnabled(false);
                return;
        }


    }

    private void addColumnButtonActionPerformed(ActionEvent e) {
        final int index = this.availableColumnsList.getSelectedIndex();
        if (index < 0) {
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ColumnOption col =
                    (ColumnOption) availableColumnsList.getModel().getElementAt(index);
                ((DefaultListModel) includedColumnsList.getModel()).addElement(col);
                ((DefaultListModel) availableColumnsList.getModel()).removeElement(col);
            }
        });
    }

    private void removeColumnButtonActionPerformed(ActionEvent e) {
        final int index = this.includedColumnsList.getSelectedIndex();
        if (index < 0) {
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ColumnOption col =
                    (ColumnOption) includedColumnsList.getModel().getElementAt(index);
                ((DefaultListModel) availableColumnsList.getModel()).addElement(col);
                ((DefaultListModel) includedColumnsList.getModel()).removeElement(col);
            }
        });
    }

    private void columnUpButtonActionPerformed(ActionEvent e) {
        final int index = this.includedColumnsList.getSelectedIndex();
        if (index < 0 || (index - 1) < 0) {
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ColumnOption col1 =
                    (ColumnOption) includedColumnsList.getModel().getElementAt(index - 1);
                ColumnOption col2 =
                    (ColumnOption) includedColumnsList.getModel().getElementAt(index);
                DefaultListModel dlm = (DefaultListModel) includedColumnsList.getModel();
                dlm.set(index - 1, col2);
                dlm.set(index, col1);
                includedColumnsList.setSelectedIndex(index - 1);
            }
        });
    }

    private void columnDownButtonActionPerformed(ActionEvent e) {
        final int index = this.includedColumnsList.getSelectedIndex();
        if (index < 0 || (index + 2) > includedColumnsList.getModel().getSize()) {
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ColumnOption col1 =
                    (ColumnOption) includedColumnsList.getModel().getElementAt(index);
                ColumnOption col2 =
                    (ColumnOption) includedColumnsList.getModel().getElementAt(index + 1);
                DefaultListModel dlm = (DefaultListModel) includedColumnsList.getModel();
                dlm.set(index, col2);
                dlm.set(index + 1, col1);
                includedColumnsList.setSelectedIndex(index + 1);
            }
        });
    }

    private void columnNameTextFieldCaretUpdate(CaretEvent e) {
        updateColumnName();
    }

    private void exportImagesButtonActionPerformed(ActionEvent e) {

        this.exportType = ExportOptions.IMAGE_EXPORT;
        if (this.collateImagesRadioButton.isSelected()) {
            this.collation = Collation.ALL_IMAGES_TOGETHER;
        } else if (this.collateFormImagesRadioButton.isSelected()) {
            this.collation = Collation.FORM_IMAGES_TOGETHER;
        } else if (this.individualImagesRadioButton.isSelected()) {
            this.collation = Collation.IMAGES_ONLY;
        }

        if (this.includeGradesCheckBox.isSelected()) {
            this.overlay.add(Overlay.GRADES);
        }
        if (this.includeFormScoreCheckBox.isSelected()) {
            this.overlay.add(Overlay.FORM_SCORE);
        }
        if (this.includePageScoreCheckBox.isSelected()) {
            this.overlay.add(Overlay.PAGE_SCORE);
        }
        if (this.includeSourceDataCheckBox.isSelected()) {
            this.overlay.add(Overlay.SOURCE_DATA);
        }
        if (this.includeCapturedDataCheckBox.isSelected()) {
            this.overlay.add(Overlay.CAPTURED_DATA);
        }
        if (this.includeIndividualScoresCheckBox.isSelected()) {
            this.overlay.add(Overlay.INDIVIDUAL_SCORES);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                File file = null;
                if (collation == Collation.ALL_IMAGES_TOGETHER) {
                    file = selectImagesFile();
                } else {
                    file = selectImagesDirectory();
                }
                if (file == null) {
                    return;
                }
                exportImages(file);
            }
        });
    }

    protected File selectImagesFile() {

        FileDialog fd = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();

        File imagesOutputFile = null;

        File lastDir = null;
        if (Globals.getLastDirectory() != null) {

            lastDir = new File(Globals.getLastDirectory());

            if (!(lastDir.exists())) {
                lastDir = null;
            }

        }

        if (lastDir == null) {
            lastDir = new File(System.getProperty("user.home"));
        }

        filter.addExtension("pdf");
        fd = new FileDialog(this, Localizer.localize("UICDM", "SaveImagesExportFileMessage"),
            FileDialog.SAVE);
        try {
            fd.setDirectory(lastDir.getCanonicalPath());
        } catch (IOException e1) {
        }
        fd.setFilenameFilter(filter);

        String filename =
            Localizer.localize("UICDM", "CapturedDataExportImagesFilePrefix") + ".pdf";

        fd.setFile(filename);
        fd.setModal(true);
        fd.setVisible(true);

        if (fd.getFile() != null) {
            String fileString =
                fd.getDirectory() + System.getProperty("file.separator") + fd.getFile();

            if (!(fileString.endsWith(".pdf") || fileString.endsWith(".PDF"))) {
                fileString += ".pdf";
            }

            imagesOutputFile = new File(fileString);

            if (imagesOutputFile.isDirectory()) {
                return null;
            }

            return imagesOutputFile;

        } else {
            return null;
        }

    }

    private void exportImages(File directory) {
        this.imagesFile = directory;
        this.dialogResult = JOptionPane.OK_OPTION;
        dispose();
    }

    private void tabbedPaneStateChanged(ChangeEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                switch (tabbedPane.getSelectedIndex()) {
                    case 0:
                        createCSVButton.requestFocusInWindow();
                        getRootPane().setDefaultButton(createCSVButton);
                        break;
                    case 1:
                        exportImagesButton.requestFocusInWindow();
                        getRootPane().setDefaultButton(exportImagesButton);
                        break;
                    case 2:
                        exportXMLButton.requestFocusInWindow();
                        getRootPane().setDefaultButton(exportXMLButton);
                        break;
                }
            }
        });
    }

    private void restoreCSVDefaultsButtonActionPerformed(ActionEvent e) {
        PreferencesManager.resetCSVExportPreferences();
        restoreCSVExportOptions(PreferencesManager.getCSVExportPreferences());
    }

    private void setAsCSVDefaultButtonActionPerformed(ActionEvent e) {
        PreferencesManager.setDefaultCSVExportPreferences(buildCSVExportPreferences());
        savePreferences();
    }

    private void savePreferences() {
        if (com.ebstrada.formreturn.manager.ui.Main.getInstance() != null) {
            try {
                PreferencesManager
                    .savePreferences(com.ebstrada.formreturn.manager.ui.Main.getXstream());
            } catch (IOException e) {
                Misc.showErrorMsg(this, e.getLocalizedMessage());
            }
        } else {
            try {
                PreferencesManager.savePreferences(
                    com.ebstrada.formreturn.server.Main.getInstance().getXstream());
            } catch (IOException e) {
                Misc.showErrorMsg(this, e.getLocalizedMessage());
            }
        }
    }

    private void restoreImagesDefaultsButtonActionPerformed(ActionEvent e) {
        PreferencesManager.resetImageExportPreferences();
        restoreImageExportOptions(PreferencesManager.getImageExportPreferences());
    }

    private void setAsImagesDefaultButtonActionPerformed(ActionEvent e) {
        try {
            PreferencesManager.setDefaultImageExportPreferences(buildImageExportPreferences());
            savePreferences();
        } catch (IOException e1) {
            Misc.showErrorMsg(this, e1.getLocalizedMessage());
        }
    }

    private void restoreXMLDefaultsButtonActionPerformed(ActionEvent e) {
        PreferencesManager.resetXMLExportPreferences();
        restoreXMLExportOptions(PreferencesManager.getXMLExportPreferences());
    }

    private void setAsXMLDefaultButtonActionPerformed(ActionEvent e) {
        PreferencesManager.setDefaultXMLExportPreferences(buildXMLExportPreferences());
        savePreferences();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        tabbedPane = new JTabbedPane();
        delimitedExportPanel = new JPanel();
        exportCSVDescriptionPanel = new JPanel();
        exportCSVDescriptionLabel = new JLabel();
        exportCSVHelpLabel = new JHelpLabel();
        columnOrderingPanel = new JPanel();
        orderColumnsLabel = new JLabel();
        sortTypeComboBox = new JComboBox();
        includedColumnsInExportPanel = new JPanel();
        columnsPanel = new JPanel();
        availableColumnsLabel = new JLabel();
        includedColumnsLabel = new JLabel();
        includedColumnsScrollPane = new JScrollPane();
        availableColumnsList = new JList();
        addRemoveColumnsPanel = new JPanel();
        addColumnButton = new JButton();
        removeColumnButton = new JButton();
        includedColumnsListScrollPane = new JScrollPane();
        includedColumnsList = new JList();
        columnsUpDownPanel = new JPanel();
        columnUpButton = new JButton();
        columnDownButton = new JButton();
        columnNamePanel = new JPanel();
        columnNameLabel = new JLabel();
        columnNameTextField = new JTextField();
        panel1 = new JPanel();
        combineScoresAndDataCheckBox = new JCheckBox();
        delimitedFileOutputPanel = new JPanel();
        panel5 = new JPanel();
        label19 = new JLabel();
        delimiterComboBox = new JComboBox();
        label20 = new JLabel();
        quotesComboBox = new JComboBox();
        panel3 = new JPanel();
        includeFieldnamesHeaderCheckBox = new JCheckBox();
        includeStatisticsCheckBox = new JCheckBox();
        includeErrorMessagesCheckBox = new JCheckBox();
        panel6 = new JPanel();
        csvTimestampPrefixCheckBox = new JCheckBox();
        csvExportButtonsPanel = new JPanel();
        restoreCSVDefaultsButton = new JButton();
        setAsCSVDefaultButton = new JButton();
        createCSVButton = new JButton();
        exportImagesPanel = new JPanel();
        exportImagesDescriptionPanel = new JPanel();
        exportImagesDescriptionLabel = new JLabel();
        exportImagesHelpLabel = new JHelpLabel();
        collateImagesPanel = new JPanel();
        collateImagesRadioButton = new JRadioButton();
        panel2 = new JPanel();
        collateFormImagesRadioButton = new JRadioButton();
        separatorLabel1 = new JLabel();
        exportImagesFilenamePrefixLabel = new JLabel();
        formImagesFilenamePrefixTextField = new JTextField();
        panel9 = new JPanel();
        individualImagesRadioButton = new JRadioButton();
        separatorLabel2 = new JLabel();
        exportImagesFilenamePrefixLabel2 = new JLabel();
        individualImagesFilenamePrefixTextField = new JTextField();
        imageTimestampPrefixCheckBox = new JCheckBox();
        includedInExportPanel = new JPanel();
        includedInExportSubPanel = new JPanel();
        includeGradesCheckBox = new JCheckBox();
        includeFormScoreCheckBox = new JCheckBox();
        includePageScoreCheckBox = new JCheckBox();
        includeSourceDataCheckBox = new JCheckBox();
        includeCapturedDataCheckBox = new JCheckBox();
        includeIndividualScoresCheckBox = new JCheckBox();
        exportOptionsPanel = new JPanel();
        exportOptionsSubPanel1 = new JPanel();
        pageSizeLabel = new JLabel();
        pageSizeComboBox = new JComboBox();
        columnFontSizeLabel = new JLabel();
        columnFontSizeSpinner = new JSpinner();
        exportOptionsSubPanel2 = new JPanel();
        sourceDataColumnsLabel = new JLabel();
        sourceDataColumnsSpinner = new JSpinner();
        capturedDataColumnsLabel = new JLabel();
        capturedDataColumnsSpinner = new JSpinner();
        exportOptionsSubPanel3 = new JPanel();
        rotateImageCheckBox = new JCheckBox();
        exportImagesButtonsPanel = new JPanel();
        restoreImagesDefaultsButton = new JButton();
        setAsImagesDefaultButton = new JButton();
        exportImagesButton = new JButton();
        xmlExportPanel = new JPanel();
        xmlExportOptionsPanel = new JPanel();
        exportXMLDescriptionPanel = new JPanel();
        exportXMLDescriptionLabel = new JLabel();
        exportXMLHelpLabel = new JHelpLabel();
        includedXMLElementsPanel = new JPanel();
        includeIndividualScoresXMLCheckBox = new JCheckBox();
        includeIndividualResponsesXMLCheckBox = new JCheckBox();
        includeStatisticsXMLCheckBox = new JCheckBox();
        xmlOutputOptionsPanel = new JPanel();
        includeXMLHeaderCheckBox = new JCheckBox();
        indentXMLContentCheckBox = new JCheckBox();
        xmlTimestampPrefixCheckBox = new JCheckBox();
        panel4 = new JPanel();
        restoreXMLDefaultsButton = new JButton();
        setAsXMLDefaultButton = new JButton();
        xslTemplatePanel = new JPanel();
        xslTemplateListScrollPane = new JScrollPane();
        xslTemplateList = new JList();
        xmlExportButtonsPanel = new JPanel();
        createXSLFOReportButton = new JButton();
        exportXMLButton = new JButton();
        buttonBar = new JPanel();
        exportFilterSettingsButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            dialogPane.setLayout(new GridBagLayout());
            ((GridBagLayout)dialogPane.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout)dialogPane.getLayout()).rowHeights = new int[] {0, 0, 0};
            ((GridBagLayout)dialogPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout)dialogPane.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

            //======== tabbedPane ========
            {
                tabbedPane.setFont(UIManager.getFont("TabbedPane.font"));
                tabbedPane.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        tabbedPaneStateChanged(e);
                    }
                });

                //======== delimitedExportPanel ========
                {
                    delimitedExportPanel.setOpaque(false);
                    delimitedExportPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                    delimitedExportPanel.setFont(UIManager.getFont("Panel.font"));
                    delimitedExportPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)delimitedExportPanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)delimitedExportPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
                    ((GridBagLayout)delimitedExportPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)delimitedExportPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};

                    //======== exportCSVDescriptionPanel ========
                    {
                        exportCSVDescriptionPanel.setBorder(new MatteBorder(0, 0, 3, 0, Color.gray));
                        exportCSVDescriptionPanel.setOpaque(false);
                        exportCSVDescriptionPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)exportCSVDescriptionPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                        ((GridBagLayout)exportCSVDescriptionPanel.getLayout()).rowHeights = new int[] {40, 0};
                        ((GridBagLayout)exportCSVDescriptionPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)exportCSVDescriptionPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- exportCSVDescriptionLabel ----
                        exportCSVDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        exportCSVDescriptionLabel.setText("<html><body><strong>" + Localizer.localize("UICDM", "ExportCSVDescriptionLabel") + "</strong></body></html>");
                        exportCSVDescriptionPanel.add(exportCSVDescriptionLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- exportCSVHelpLabel ----
                        exportCSVHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                        exportCSVHelpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        exportCSVHelpLabel.setFont(UIManager.getFont("Label.font"));
                        exportCSVHelpLabel.setHelpGUID("delimited-file-export");
                        exportCSVHelpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                        exportCSVDescriptionPanel.add(exportCSVHelpLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    delimitedExportPanel.add(exportCSVDescriptionPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== columnOrderingPanel ========
                    {
                        columnOrderingPanel.setOpaque(false);
                        columnOrderingPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)columnOrderingPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                        ((GridBagLayout)columnOrderingPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)columnOrderingPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)columnOrderingPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};
                        columnOrderingPanel.setBorder(new CompoundBorder(
                                            new TitledBorder(Localizer.localize("UICDM", "ColumnOrderingBorderTitle")),
                                            new EmptyBorder(5, 5, 5, 5)));

                        //---- orderColumnsLabel ----
                        orderColumnsLabel.setFont(UIManager.getFont("Label.font"));
                        orderColumnsLabel.setText("Order Columns");
                        orderColumnsLabel.setText(Localizer.localize("UICDM", "OrderColumnsLabel"));
                        columnOrderingPanel.add(orderColumnsLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- sortTypeComboBox ----
                        sortTypeComboBox.setFont(UIManager.getFont("ComboBox.font"));
                        sortTypeComboBox.setModel(new DefaultComboBoxModel(new String[] {
                            Localizer.localize("UICDM", "ColumnOrderByIndex"),
                            Localizer.localize("UICDM", "ColumnOrderByFieldNameAscending"),
                            Localizer.localize("UICDM", "ColumnOrderByFieldNameDecending")
                        }));
                        columnOrderingPanel.add(sortTypeComboBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.WEST, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    delimitedExportPanel.add(columnOrderingPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== includedColumnsInExportPanel ========
                    {
                        includedColumnsInExportPanel.setOpaque(false);
                        includedColumnsInExportPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)includedColumnsInExportPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)includedColumnsInExportPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                        ((GridBagLayout)includedColumnsInExportPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)includedColumnsInExportPanel.getLayout()).rowWeights = new double[] {1.0, 0.0, 0.0, 1.0E-4};
                        includedColumnsInExportPanel.setBorder(new CompoundBorder(
                            new TitledBorder(Localizer.localize("UICDM", "IncludedColumnsBorderTitle")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //======== columnsPanel ========
                        {
                            columnsPanel.setOpaque(false);
                            columnsPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)columnsPanel.getLayout()).columnWidths = new int[] {255, 65, 255, 60, 0};
                            ((GridBagLayout)columnsPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                            ((GridBagLayout)columnsPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0, 0.0, 1.0E-4};
                            ((GridBagLayout)columnsPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

                            //---- availableColumnsLabel ----
                            availableColumnsLabel.setFont(UIManager.getFont("Label.font"));
                            availableColumnsLabel.setText(Localizer.localize("UICDM", "AvailableColumnsLabel"));
                            columnsPanel.add(availableColumnsLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 5), 0, 0));

                            //---- includedColumnsLabel ----
                            includedColumnsLabel.setFont(UIManager.getFont("Label.font"));
                            includedColumnsLabel.setText(Localizer.localize("UICDM", "IncludedColumnsLabel"));
                            columnsPanel.add(includedColumnsLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 5), 0, 0));

                            //======== includedColumnsScrollPane ========
                            {

                                //---- availableColumnsList ----
                                availableColumnsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                                availableColumnsList.setFont(UIManager.getFont("List.font"));
                                includedColumnsScrollPane.setViewportView(availableColumnsList);
                            }
                            columnsPanel.add(includedColumnsScrollPane, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //======== addRemoveColumnsPanel ========
                            {
                                addRemoveColumnsPanel.setOpaque(false);
                                addRemoveColumnsPanel.setLayout(new GridBagLayout());
                                ((GridBagLayout)addRemoveColumnsPanel.getLayout()).columnWidths = new int[] {0, 0};
                                ((GridBagLayout)addRemoveColumnsPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                                ((GridBagLayout)addRemoveColumnsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                                ((GridBagLayout)addRemoveColumnsPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};

                                //---- addColumnButton ----
                                addColumnButton.setText(">>");
                                addColumnButton.setFont(UIManager.getFont("Button.font"));
                                addColumnButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        addColumnButtonActionPerformed(e);
                                    }
                                });
                                addRemoveColumnsPanel.add(addColumnButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 5, 0), 0, 0));

                                //---- removeColumnButton ----
                                removeColumnButton.setText("<<");
                                removeColumnButton.setFont(UIManager.getFont("Button.font"));
                                removeColumnButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        removeColumnButtonActionPerformed(e);
                                    }
                                });
                                addRemoveColumnsPanel.add(removeColumnButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
                            }
                            columnsPanel.add(addRemoveColumnsPanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //======== includedColumnsListScrollPane ========
                            {

                                //---- includedColumnsList ----
                                includedColumnsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                                includedColumnsList.setFont(UIManager.getFont("List.font"));
                                includedColumnsList.addListSelectionListener(new ListSelectionListener() {
                                    @Override
                                    public void valueChanged(ListSelectionEvent e) {
                                        includedColumnsListValueChanged(e);
                                    }
                                });
                                includedColumnsListScrollPane.setViewportView(includedColumnsList);
                            }
                            columnsPanel.add(includedColumnsListScrollPane, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //======== columnsUpDownPanel ========
                            {
                                columnsUpDownPanel.setOpaque(false);
                                columnsUpDownPanel.setLayout(new GridBagLayout());
                                ((GridBagLayout)columnsUpDownPanel.getLayout()).columnWidths = new int[] {0, 0};
                                ((GridBagLayout)columnsUpDownPanel.getLayout()).rowHeights = new int[] {0, 15, 0, 0};
                                ((GridBagLayout)columnsUpDownPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                                ((GridBagLayout)columnsUpDownPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};

                                //---- columnUpButton ----
                                columnUpButton.setFont(UIManager.getFont("Button.font"));
                                columnUpButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_up.png")));
                                columnUpButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        columnUpButtonActionPerformed(e);
                                    }
                                });
                                columnUpButton.setText(Localizer.localize("UICDM", "Up"));
                                columnsUpDownPanel.add(columnUpButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 5, 0), 0, 0));

                                //---- columnDownButton ----
                                columnDownButton.setFont(UIManager.getFont("Button.font"));
                                columnDownButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_down.png")));
                                columnDownButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        columnDownButtonActionPerformed(e);
                                    }
                                });
                                columnDownButton.setText(Localizer.localize("UICDM", "Down"));
                                columnsUpDownPanel.add(columnDownButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
                            }
                            columnsPanel.add(columnsUpDownPanel, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        includedColumnsInExportPanel.add(columnsPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== columnNamePanel ========
                        {
                            columnNamePanel.setOpaque(false);
                            columnNamePanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)columnNamePanel.getLayout()).columnWidths = new int[] {0, 0, 305, 0, 0};
                            ((GridBagLayout)columnNamePanel.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)columnNamePanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                            ((GridBagLayout)columnNamePanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                            //---- columnNameLabel ----
                            columnNameLabel.setFont(UIManager.getFont("Label.font"));
                            columnNameLabel.setText(Localizer.localize("UICDM", "ColumnNameLabel"));
                            columnNamePanel.add(columnNameLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- columnNameTextField ----
                            columnNameTextField.setEnabled(false);
                            columnNameTextField.setFont(UIManager.getFont("TextField.font"));
                            columnNameTextField.setOpaque(false);
                            columnNameTextField.addCaretListener(new CaretListener() {
                                @Override
                                public void caretUpdate(CaretEvent e) {
                                    columnNameTextFieldCaretUpdate(e);
                                }
                            });
                            columnNamePanel.add(columnNameTextField, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 5), 0, 0));
                        }
                        includedColumnsInExportPanel.add(columnNamePanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== panel1 ========
                        {
                            panel1.setOpaque(false);
                            panel1.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                            ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0, 1.0E-4};
                            ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                            //---- combineScoresAndDataCheckBox ----
                            combineScoresAndDataCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
                            combineScoresAndDataCheckBox.setSelected(true);
                            combineScoresAndDataCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            combineScoresAndDataCheckBox.setOpaque(false);
                            combineScoresAndDataCheckBox.setText(Localizer.localize("UICDM", "CombineScoresAndDataCheckBoxText"));
                            panel1.add(combineScoresAndDataCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));
                        }
                        includedColumnsInExportPanel.add(panel1, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    delimitedExportPanel.add(includedColumnsInExportPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== delimitedFileOutputPanel ========
                    {
                        delimitedFileOutputPanel.setOpaque(false);
                        delimitedFileOutputPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)delimitedFileOutputPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)delimitedFileOutputPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                        ((GridBagLayout)delimitedFileOutputPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)delimitedFileOutputPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
                        delimitedFileOutputPanel.setBorder(new CompoundBorder(
                            new TitledBorder(Localizer.localize("UICDM", "DelimitedFileOutputBorderTitle")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //======== panel5 ========
                        {
                            panel5.setOpaque(false);
                            panel5.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel5.getLayout()).columnWidths = new int[] {0, 0, 0, 15, 0, 0, 0, 0};
                            ((GridBagLayout)panel5.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)panel5.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                            ((GridBagLayout)panel5.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                            //---- label19 ----
                            label19.setFont(UIManager.getFont("Label.font"));
                            label19.setText(Localizer.localize("UICDM", "DelimiterLabel"));
                            panel5.add(label19, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- delimiterComboBox ----
                            delimiterComboBox.setFont(UIManager.getFont("ComboBox.font"));
                            delimiterComboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxxxxxxxx");
                            delimiterComboBox.setModel(new DefaultComboBoxModel(new String[] {
                                Localizer.localize("UICDM", "DelimiterCSV"),
                                Localizer.localize("UICDM", "DelimiterTSV")
                            }));
                            panel5.add(delimiterComboBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- label20 ----
                            label20.setFont(UIManager.getFont("Label.font"));
                            label20.setText(Localizer.localize("UICDM", "QuotesLabel"));
                            panel5.add(label20, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- quotesComboBox ----
                            quotesComboBox.setFont(UIManager.getFont("ComboBox.font"));
                            quotesComboBox.setModel(new DefaultComboBoxModel(new String[] {
                                Localizer.localize("UICDM", "DoubleQuotes"),
                                Localizer.localize("UICDM", "SingleQuotes"),
                                Localizer.localize("UICDM", "NoQuotes")
                            }));
                            panel5.add(quotesComboBox, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));
                        }
                        delimitedFileOutputPanel.add(panel5, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== panel3 ========
                        {
                            panel3.setOpaque(false);
                            panel3.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0};
                            ((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                            ((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                            //---- includeFieldnamesHeaderCheckBox ----
                            includeFieldnamesHeaderCheckBox.setText("Include Field Names Header");
                            includeFieldnamesHeaderCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            includeFieldnamesHeaderCheckBox.setSelected(true);
                            includeFieldnamesHeaderCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
                            includeFieldnamesHeaderCheckBox.setOpaque(false);
                            includeFieldnamesHeaderCheckBox.setText(Localizer.localize("UICDM", "IncludeFieldNamesHeaderCheckBox"));
                            panel3.add(includeFieldnamesHeaderCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 10), 0, 0));

                            //---- includeStatisticsCheckBox ----
                            includeStatisticsCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            includeStatisticsCheckBox.setOpaque(false);
                            includeStatisticsCheckBox.setText(Localizer.localize("UICDM", "IncludeStatisticsCheckBox"));
                            panel3.add(includeStatisticsCheckBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 10), 0, 0));

                            //---- includeErrorMessagesCheckBox ----
                            includeErrorMessagesCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            includeErrorMessagesCheckBox.setSelected(true);
                            includeErrorMessagesCheckBox.setOpaque(false);
                            includeErrorMessagesCheckBox.setText(Localizer.localize("UICDM", "IncludeErrorMessagesCheckBox"));
                            panel3.add(includeErrorMessagesCheckBox, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 10), 0, 0));
                        }
                        delimitedFileOutputPanel.add(panel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== panel6 ========
                        {
                            panel6.setOpaque(false);
                            panel6.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel6.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                            ((GridBagLayout)panel6.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)panel6.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0, 1.0E-4};
                            ((GridBagLayout)panel6.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                            //---- csvTimestampPrefixCheckBox ----
                            csvTimestampPrefixCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            csvTimestampPrefixCheckBox.setOpaque(false);
                            csvTimestampPrefixCheckBox.setText(Localizer.localize("UI", "TimestampPrefixCheckBox"));
                            panel6.add(csvTimestampPrefixCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));
                        }
                        delimitedFileOutputPanel.add(panel6, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    delimitedExportPanel.add(delimitedFileOutputPanel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== csvExportButtonsPanel ========
                    {
                        csvExportButtonsPanel.setOpaque(false);
                        csvExportButtonsPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)csvExportButtonsPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 80, 0};
                        ((GridBagLayout)csvExportButtonsPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)csvExportButtonsPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 0.0, 1.0E-4};
                        ((GridBagLayout)csvExportButtonsPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- restoreCSVDefaultsButton ----
                        restoreCSVDefaultsButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
                        restoreCSVDefaultsButton.setFont(UIManager.getFont("Button.font"));
                        restoreCSVDefaultsButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                restoreCSVDefaultsButtonActionPerformed(e);
                            }
                        });
                        restoreCSVDefaultsButton.setText(Localizer.localize("UI", "RestoreDefaultsButtonText"));
                        csvExportButtonsPanel.add(restoreCSVDefaultsButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- setAsCSVDefaultButton ----
                        setAsCSVDefaultButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/star.png")));
                        setAsCSVDefaultButton.setFont(UIManager.getFont("Button.font"));
                        setAsCSVDefaultButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                setAsCSVDefaultButtonActionPerformed(e);
                            }
                        });
                        setAsCSVDefaultButton.setText(Localizer.localize("UI", "SetAsDefaultButtonText"));
                        csvExportButtonsPanel.add(setAsCSVDefaultButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- createCSVButton ----
                        createCSVButton.setFont(UIManager.getFont("Button.font"));
                        createCSVButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/page_white_office.png")));
                        createCSVButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                createCSVButtonActionPerformed(e);
                            }
                        });
                        createCSVButton.setText(Localizer.localize("UICDM", "CreateCSVButtonText"));
                        csvExportButtonsPanel.add(createCSVButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    delimitedExportPanel.add(csvExportButtonsPanel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                tabbedPane.addTab("Export Delimited File", delimitedExportPanel);

                //======== exportImagesPanel ========
                {
                    exportImagesPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                    exportImagesPanel.setFont(UIManager.getFont("Panel.font"));
                    exportImagesPanel.setOpaque(false);
                    exportImagesPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)exportImagesPanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)exportImagesPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
                    ((GridBagLayout)exportImagesPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)exportImagesPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0, 1.0, 0.0, 1.0E-4};

                    //======== exportImagesDescriptionPanel ========
                    {
                        exportImagesDescriptionPanel.setBorder(new MatteBorder(0, 0, 3, 0, Color.gray));
                        exportImagesDescriptionPanel.setOpaque(false);
                        exportImagesDescriptionPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)exportImagesDescriptionPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                        ((GridBagLayout)exportImagesDescriptionPanel.getLayout()).rowHeights = new int[] {40, 0};
                        ((GridBagLayout)exportImagesDescriptionPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)exportImagesDescriptionPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- exportImagesDescriptionLabel ----
                        exportImagesDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        exportImagesDescriptionLabel.setText("<html><body><strong>" + Localizer.localize("UICDM", "ExportImagesDescriptionLabel") + "</strong></body></html>");
                        exportImagesDescriptionPanel.add(exportImagesDescriptionLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- exportImagesHelpLabel ----
                        exportImagesHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                        exportImagesHelpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        exportImagesHelpLabel.setFont(UIManager.getFont("Label.font"));
                        exportImagesHelpLabel.setHelpGUID("export-scanned-images");
                        exportImagesHelpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                        exportImagesDescriptionPanel.add(exportImagesHelpLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    exportImagesPanel.add(exportImagesDescriptionPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== collateImagesPanel ========
                    {
                        collateImagesPanel.setOpaque(false);
                        collateImagesPanel.setFont(UIManager.getFont("Panel.font"));
                        collateImagesPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)collateImagesPanel.getLayout()).columnWidths = new int[] {45, 0, 0};
                        ((GridBagLayout)collateImagesPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
                        ((GridBagLayout)collateImagesPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)collateImagesPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0, 1.0E-4};
                        collateImagesPanel.setBorder(new CompoundBorder(
                                            new TitledBorder(Localizer.localize("UICDM", "CollateImagesPanelTitle")),
                                            new EmptyBorder(5, 5, 5, 5)));

                        //---- collateImagesRadioButton ----
                        collateImagesRadioButton.setText("Merge and export all images into a single file");
                        collateImagesRadioButton.setFont(UIManager.getFont("RadioButton.font"));
                        collateImagesRadioButton.setSelected(true);
                        collateImagesRadioButton.setOpaque(false);
                        collateImagesRadioButton.setText(Localizer.localize("UICDM", "CollateImagesRadioButtonText"));
                        collateImagesPanel.add(collateImagesRadioButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.WEST, GridBagConstraints.NONE,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== panel2 ========
                        {
                            panel2.setOpaque(false);
                            panel2.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0, 0, 205, 40, 0};
                            ((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0, 0.0, 1.0E-4};
                            ((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                            //---- collateFormImagesRadioButton ----
                            collateFormImagesRadioButton.setText("Merge and export form images together");
                            collateFormImagesRadioButton.setFont(UIManager.getFont("RadioButton.font"));
                            collateFormImagesRadioButton.setOpaque(false);
                            collateFormImagesRadioButton.setText(Localizer.localize("UICDM", "CollateFormImagesRadioButtonText"));
                            panel2.add(collateFormImagesRadioButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- separatorLabel1 ----
                            separatorLabel1.setText("-");
                            separatorLabel1.setFont(UIManager.getFont("Label.font"));
                            panel2.add(separatorLabel1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- exportImagesFilenamePrefixLabel ----
                            exportImagesFilenamePrefixLabel.setFont(UIManager.getFont("Label.font"));
                            exportImagesFilenamePrefixLabel.setText(Localizer.localize("UICDM", "ExportImagesFilenamePrefixLabel"));
                            panel2.add(exportImagesFilenamePrefixLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- formImagesFilenamePrefixTextField ----
                            formImagesFilenamePrefixTextField.setFont(UIManager.getFont("TextField.font"));
                            formImagesFilenamePrefixTextField.setText("<<form_id>>");
                            panel2.add(formImagesFilenamePrefixTextField, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));
                        }
                        collateImagesPanel.add(panel2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.WEST, GridBagConstraints.NONE,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== panel9 ========
                        {
                            panel9.setOpaque(false);
                            panel9.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel9.getLayout()).columnWidths = new int[] {0, 0, 0, 205, 40, 0};
                            ((GridBagLayout)panel9.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)panel9.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0, 0.0, 1.0E-4};
                            ((GridBagLayout)panel9.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                            //---- individualImagesRadioButton ----
                            individualImagesRadioButton.setFont(UIManager.getFont("RadioButton.font"));
                            individualImagesRadioButton.setOpaque(false);
                            individualImagesRadioButton.setText(Localizer.localize("UICDM", "IndividualImagesRadioButtonText"));
                            panel9.add(individualImagesRadioButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- separatorLabel2 ----
                            separatorLabel2.setText("-");
                            separatorLabel2.setFont(UIManager.getFont("Label.font"));
                            panel9.add(separatorLabel2, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- exportImagesFilenamePrefixLabel2 ----
                            exportImagesFilenamePrefixLabel2.setText("Filename Prefix:");
                            exportImagesFilenamePrefixLabel2.setFont(UIManager.getFont("Label.font"));
                            exportImagesFilenamePrefixLabel2.setText(Localizer.localize("UICDM", "ExportImagesFilenamePrefixLabel"));
                            panel9.add(exportImagesFilenamePrefixLabel2, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- individualImagesFilenamePrefixTextField ----
                            individualImagesFilenamePrefixTextField.setText("<<form_page_id>>");
                            individualImagesFilenamePrefixTextField.setFont(UIManager.getFont("TextField.font"));
                            panel9.add(individualImagesFilenamePrefixTextField, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));
                        }
                        collateImagesPanel.add(panel9, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.WEST, GridBagConstraints.NONE,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- imageTimestampPrefixCheckBox ----
                        imageTimestampPrefixCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                        imageTimestampPrefixCheckBox.setOpaque(false);
                        imageTimestampPrefixCheckBox.setText(Localizer.localize("UI", "TimestampPrefixCheckBox"));
                        collateImagesPanel.add(imageTimestampPrefixCheckBox, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.WEST, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    exportImagesPanel.add(collateImagesPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== includedInExportPanel ========
                    {
                        includedInExportPanel.setOpaque(false);
                        includedInExportPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)includedInExportPanel.getLayout()).columnWidths = new int[] {45, 0, 40, 0};
                        ((GridBagLayout)includedInExportPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)includedInExportPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};
                        ((GridBagLayout)includedInExportPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                        includedInExportPanel.setBorder(new CompoundBorder(
                                            new TitledBorder(Localizer.localize("UICDM", "IncludedInExportPanelTitle")),
                                            new EmptyBorder(5, 5, 5, 5)));

                        //======== includedInExportSubPanel ========
                        {
                            includedInExportSubPanel.setOpaque(false);
                            includedInExportSubPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)includedInExportSubPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0};
                            ((GridBagLayout)includedInExportSubPanel.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)includedInExportSubPanel.getLayout()).columnWeights = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0E-4};
                            ((GridBagLayout)includedInExportSubPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                            //---- includeGradesCheckBox ----
                            includeGradesCheckBox.setText("Grades");
                            includeGradesCheckBox.setSelected(true);
                            includeGradesCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            includeGradesCheckBox.setOpaque(false);
                            includeGradesCheckBox.setText(Localizer.localize("UICDM", "IncludeGradesCheckBox"));
                            includedInExportSubPanel.add(includeGradesCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 10), 0, 0));

                            //---- includeFormScoreCheckBox ----
                            includeFormScoreCheckBox.setText("Form Score");
                            includeFormScoreCheckBox.setSelected(true);
                            includeFormScoreCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            includeFormScoreCheckBox.setOpaque(false);
                            includeFormScoreCheckBox.setText(Localizer.localize("UICDM", "IncludeFormScoreCheckBox"));
                            includedInExportSubPanel.add(includeFormScoreCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 10), 0, 0));

                            //---- includePageScoreCheckBox ----
                            includePageScoreCheckBox.setSelected(true);
                            includePageScoreCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            includePageScoreCheckBox.setOpaque(false);
                            includePageScoreCheckBox.setText(Localizer.localize("UICDM", "IncludePageScoreCheckBox"));
                            includedInExportSubPanel.add(includePageScoreCheckBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 10), 0, 0));

                            //---- includeSourceDataCheckBox ----
                            includeSourceDataCheckBox.setSelected(true);
                            includeSourceDataCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            includeSourceDataCheckBox.setOpaque(false);
                            includeSourceDataCheckBox.setText(Localizer.localize("UICDM", "IncludeSourceDataCheckBox"));
                            includedInExportSubPanel.add(includeSourceDataCheckBox, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 10), 0, 0));

                            //---- includeCapturedDataCheckBox ----
                            includeCapturedDataCheckBox.setText("Captured Data");
                            includeCapturedDataCheckBox.setSelected(true);
                            includeCapturedDataCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            includeCapturedDataCheckBox.setOpaque(false);
                            includeCapturedDataCheckBox.setText(Localizer.localize("UICDM", "IncludeCapturedDataCheckBox"));
                            includedInExportSubPanel.add(includeCapturedDataCheckBox, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 10), 0, 0));

                            //---- includeIndividualScoresCheckBox ----
                            includeIndividualScoresCheckBox.setSelected(true);
                            includeIndividualScoresCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            includeIndividualScoresCheckBox.setOpaque(false);
                            includeIndividualScoresCheckBox.setText(Localizer.localize("UICDM", "IncludeIndividualScoresCheckBox"));
                            includedInExportSubPanel.add(includeIndividualScoresCheckBox, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        includedInExportPanel.add(includedInExportSubPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    exportImagesPanel.add(includedInExportPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== exportOptionsPanel ========
                    {
                        exportOptionsPanel.setFont(UIManager.getFont("Panel.font"));
                        exportOptionsPanel.setOpaque(false);
                        exportOptionsPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)exportOptionsPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)exportOptionsPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                        ((GridBagLayout)exportOptionsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)exportOptionsPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0E-4};
                        exportOptionsPanel.setBorder(new CompoundBorder(
                                            new TitledBorder(Localizer.localize("UICDM", "ExportOptionsPanelTitle")),
                                            new EmptyBorder(5, 5, 5, 5)));

                        //======== exportOptionsSubPanel1 ========
                        {
                            exportOptionsSubPanel1.setOpaque(false);
                            exportOptionsSubPanel1.setLayout(new GridBagLayout());
                            ((GridBagLayout)exportOptionsSubPanel1.getLayout()).columnWidths = new int[] {45, 0, 255, 15, 0, 80, 0};
                            ((GridBagLayout)exportOptionsSubPanel1.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)exportOptionsSubPanel1.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
                            ((GridBagLayout)exportOptionsSubPanel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                            //---- pageSizeLabel ----
                            pageSizeLabel.setFont(UIManager.getFont("Label.font"));
                            pageSizeLabel.setText(Localizer.localize("UICDM", "PageSizeLabel"));
                            exportOptionsSubPanel1.add(pageSizeLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- pageSizeComboBox ----
                            pageSizeComboBox.setFont(UIManager.getFont("ComboBox.font"));
                            exportOptionsSubPanel1.add(pageSizeComboBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- columnFontSizeLabel ----
                            columnFontSizeLabel.setFont(UIManager.getFont("Label.font"));
                            columnFontSizeLabel.setText(Localizer.localize("UICDM", "ColumnFontSizeLabel"));
                            exportOptionsSubPanel1.add(columnFontSizeLabel, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- columnFontSizeSpinner ----
                            columnFontSizeSpinner.setModel(new SpinnerNumberModel(8, 5, 20, 1));
                            columnFontSizeSpinner.setFont(UIManager.getFont("Spinner.font"));
                            exportOptionsSubPanel1.add(columnFontSizeSpinner, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        exportOptionsPanel.add(exportOptionsSubPanel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== exportOptionsSubPanel2 ========
                        {
                            exportOptionsSubPanel2.setOpaque(false);
                            exportOptionsSubPanel2.setLayout(new GridBagLayout());
                            ((GridBagLayout)exportOptionsSubPanel2.getLayout()).columnWidths = new int[] {45, 0, 85, 15, 0, 80, 0};
                            ((GridBagLayout)exportOptionsSubPanel2.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)exportOptionsSubPanel2.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
                            ((GridBagLayout)exportOptionsSubPanel2.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                            //---- sourceDataColumnsLabel ----
                            sourceDataColumnsLabel.setFont(UIManager.getFont("Label.font"));
                            sourceDataColumnsLabel.setText(Localizer.localize("UICDM", "SourceDataColumnsLabel"));
                            exportOptionsSubPanel2.add(sourceDataColumnsLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- sourceDataColumnsSpinner ----
                            sourceDataColumnsSpinner.setModel(new SpinnerNumberModel(3, 1, 8, 1));
                            sourceDataColumnsSpinner.setFont(UIManager.getFont("Spinner.font"));
                            exportOptionsSubPanel2.add(sourceDataColumnsSpinner, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- capturedDataColumnsLabel ----
                            capturedDataColumnsLabel.setFont(UIManager.getFont("Label.font"));
                            capturedDataColumnsLabel.setText(Localizer.localize("UICDM", "CapturedDataColumnsLabel"));
                            exportOptionsSubPanel2.add(capturedDataColumnsLabel, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- capturedDataColumnsSpinner ----
                            capturedDataColumnsSpinner.setModel(new SpinnerNumberModel(3, 0, 8, 1));
                            capturedDataColumnsSpinner.setFont(UIManager.getFont("Spinner.font"));
                            exportOptionsSubPanel2.add(capturedDataColumnsSpinner, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        exportOptionsPanel.add(exportOptionsSubPanel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== exportOptionsSubPanel3 ========
                        {
                            exportOptionsSubPanel3.setOpaque(false);
                            exportOptionsSubPanel3.setLayout(new GridBagLayout());
                            ((GridBagLayout)exportOptionsSubPanel3.getLayout()).columnWidths = new int[] {45, 0, 0};
                            ((GridBagLayout)exportOptionsSubPanel3.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)exportOptionsSubPanel3.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                            ((GridBagLayout)exportOptionsSubPanel3.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                            //---- rotateImageCheckBox ----
                            rotateImageCheckBox.setSelected(true);
                            rotateImageCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            rotateImageCheckBox.setOpaque(false);
                            rotateImageCheckBox.setText(Localizer.localize("UICDM", "RotateImageCheckBoxText"));
                            exportOptionsSubPanel3.add(rotateImageCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        exportOptionsPanel.add(exportOptionsSubPanel3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    exportImagesPanel.add(exportOptionsPanel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== exportImagesButtonsPanel ========
                    {
                        exportImagesButtonsPanel.setOpaque(false);
                        exportImagesButtonsPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)exportImagesButtonsPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                        ((GridBagLayout)exportImagesButtonsPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)exportImagesButtonsPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 0.0, 1.0E-4};
                        ((GridBagLayout)exportImagesButtonsPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- restoreImagesDefaultsButton ----
                        restoreImagesDefaultsButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
                        restoreImagesDefaultsButton.setFont(UIManager.getFont("Button.font"));
                        restoreImagesDefaultsButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                restoreImagesDefaultsButtonActionPerformed(e);
                            }
                        });
                        restoreImagesDefaultsButton.setText(Localizer.localize("UI", "RestoreDefaultsButtonText"));
                        exportImagesButtonsPanel.add(restoreImagesDefaultsButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- setAsImagesDefaultButton ----
                        setAsImagesDefaultButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/star.png")));
                        setAsImagesDefaultButton.setFont(UIManager.getFont("Button.font"));
                        setAsImagesDefaultButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                setAsImagesDefaultButtonActionPerformed(e);
                            }
                        });
                        setAsImagesDefaultButton.setText(Localizer.localize("UI", "SetAsDefaultButtonText"));
                        exportImagesButtonsPanel.add(setAsImagesDefaultButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- exportImagesButton ----
                        exportImagesButton.setFont(UIManager.getFont("Button.font"));
                        exportImagesButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/images.png")));
                        exportImagesButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                exportImagesButtonActionPerformed(e);
                            }
                        });
                        exportImagesButton.setText(Localizer.localize("UICDM", "ProceedWithImagesExportButtonText"));
                        exportImagesButtonsPanel.add(exportImagesButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    exportImagesPanel.add(exportImagesButtonsPanel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                tabbedPane.addTab("Export Scanned Images", exportImagesPanel);

                //======== xmlExportPanel ========
                {
                    xmlExportPanel.setOpaque(false);
                    xmlExportPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                    xmlExportPanel.setFont(UIManager.getFont("Panel.font"));
                    xmlExportPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)xmlExportPanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)xmlExportPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout)xmlExportPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)xmlExportPanel.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

                    //======== xmlExportOptionsPanel ========
                    {
                        xmlExportOptionsPanel.setOpaque(false);
                        xmlExportOptionsPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)xmlExportOptionsPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)xmlExportOptionsPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
                        ((GridBagLayout)xmlExportOptionsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)xmlExportOptionsPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};

                        //======== exportXMLDescriptionPanel ========
                        {
                            exportXMLDescriptionPanel.setBorder(new MatteBorder(0, 0, 3, 0, Color.gray));
                            exportXMLDescriptionPanel.setOpaque(false);
                            exportXMLDescriptionPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)exportXMLDescriptionPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                            ((GridBagLayout)exportXMLDescriptionPanel.getLayout()).rowHeights = new int[] {40, 0};
                            ((GridBagLayout)exportXMLDescriptionPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                            ((GridBagLayout)exportXMLDescriptionPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                            //---- exportXMLDescriptionLabel ----
                            exportXMLDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                            exportXMLDescriptionLabel.setText("<html><body><strong>" + Localizer.localize("UICDM", "ExportXMLDescriptionLabel") + "</strong></body></html>");
                            exportXMLDescriptionPanel.add(exportXMLDescriptionLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- exportXMLHelpLabel ----
                            exportXMLHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                            exportXMLHelpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                            exportXMLHelpLabel.setFont(UIManager.getFont("Label.font"));
                            exportXMLHelpLabel.setHelpGUID("publication-xsl-fo-report-template");
                            exportXMLHelpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                            exportXMLDescriptionPanel.add(exportXMLHelpLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 5), 0, 0));
                        }
                        xmlExportOptionsPanel.add(exportXMLDescriptionPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== includedXMLElementsPanel ========
                        {
                            includedXMLElementsPanel.setOpaque(false);
                            includedXMLElementsPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)includedXMLElementsPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                            ((GridBagLayout)includedXMLElementsPanel.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)includedXMLElementsPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
                            ((GridBagLayout)includedXMLElementsPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};
                            includedXMLElementsPanel.setBorder(new CompoundBorder(
                                                new TitledBorder(Localizer.localize("UICDM", "IncludedXMLElementsBorderTitle")),
                                                new EmptyBorder(5, 5, 5, 5)));

                            //---- includeIndividualScoresXMLCheckBox ----
                            includeIndividualScoresXMLCheckBox.setOpaque(false);
                            includeIndividualScoresXMLCheckBox.setSelected(true);
                            includeIndividualScoresXMLCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            includeIndividualScoresXMLCheckBox.setText(Localizer.localize("UICDM", "IncludeIndividualScoresXMLCheckBox"));
                            includedXMLElementsPanel.add(includeIndividualScoresXMLCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 20), 0, 0));

                            //---- includeIndividualResponsesXMLCheckBox ----
                            includeIndividualResponsesXMLCheckBox.setOpaque(false);
                            includeIndividualResponsesXMLCheckBox.setSelected(true);
                            includeIndividualResponsesXMLCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            includeIndividualResponsesXMLCheckBox.setText(Localizer.localize("UICDM", "IncludeIndividualResponsesXMLCheckBox"));
                            includedXMLElementsPanel.add(includeIndividualResponsesXMLCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 20), 0, 0));

                            //---- includeStatisticsXMLCheckBox ----
                            includeStatisticsXMLCheckBox.setOpaque(false);
                            includeStatisticsXMLCheckBox.setSelected(true);
                            includeStatisticsXMLCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            includeStatisticsXMLCheckBox.setText(Localizer.localize("UICDM", "IncludeStatisticsXMLCheckBox"));
                            includedXMLElementsPanel.add(includeStatisticsXMLCheckBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        xmlExportOptionsPanel.add(includedXMLElementsPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== xmlOutputOptionsPanel ========
                        {
                            xmlOutputOptionsPanel.setOpaque(false);
                            xmlOutputOptionsPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)xmlOutputOptionsPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                            ((GridBagLayout)xmlOutputOptionsPanel.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)xmlOutputOptionsPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
                            ((GridBagLayout)xmlOutputOptionsPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};
                            xmlOutputOptionsPanel.setBorder(new CompoundBorder(
                                                new TitledBorder(Localizer.localize("UICDM", "XMLOutputOptionsBorderTitle")),
                                                new EmptyBorder(5, 5, 5, 5)));

                            //---- includeXMLHeaderCheckBox ----
                            includeXMLHeaderCheckBox.setOpaque(false);
                            includeXMLHeaderCheckBox.setSelected(true);
                            includeXMLHeaderCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            includeXMLHeaderCheckBox.setText(Localizer.localize("UICDM", "IncludeXMLHeaderCheckBox"));
                            xmlOutputOptionsPanel.add(includeXMLHeaderCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 20), 0, 0));

                            //---- indentXMLContentCheckBox ----
                            indentXMLContentCheckBox.setOpaque(false);
                            indentXMLContentCheckBox.setSelected(true);
                            indentXMLContentCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            indentXMLContentCheckBox.setText(Localizer.localize("UICDM", "IndentXMLContentCheckBox"));
                            xmlOutputOptionsPanel.add(indentXMLContentCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 20), 0, 0));

                            //---- xmlTimestampPrefixCheckBox ----
                            xmlTimestampPrefixCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            xmlTimestampPrefixCheckBox.setOpaque(false);
                            xmlTimestampPrefixCheckBox.setText(Localizer.localize("UI", "TimestampPrefixCheckBox"));
                            xmlOutputOptionsPanel.add(xmlTimestampPrefixCheckBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        xmlExportOptionsPanel.add(xmlOutputOptionsPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== panel4 ========
                        {
                            panel4.setOpaque(false);
                            panel4.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel4.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                            ((GridBagLayout)panel4.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)panel4.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0E-4};
                            ((GridBagLayout)panel4.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                            //---- restoreXMLDefaultsButton ----
                            restoreXMLDefaultsButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
                            restoreXMLDefaultsButton.setFont(UIManager.getFont("Button.font"));
                            restoreXMLDefaultsButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    restoreXMLDefaultsButtonActionPerformed(e);
                                }
                            });
                            restoreXMLDefaultsButton.setText(Localizer.localize("UI", "RestoreDefaultsButtonText"));
                            panel4.add(restoreXMLDefaultsButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- setAsXMLDefaultButton ----
                            setAsXMLDefaultButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/star.png")));
                            setAsXMLDefaultButton.setFont(UIManager.getFont("Button.font"));
                            setAsXMLDefaultButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    setAsXMLDefaultButtonActionPerformed(e);
                                }
                            });
                            setAsXMLDefaultButton.setText(Localizer.localize("UI", "SetAsDefaultButtonText"));
                            panel4.add(setAsXMLDefaultButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        xmlExportOptionsPanel.add(panel4, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== xslTemplatePanel ========
                        {
                            xslTemplatePanel.setOpaque(false);
                            xslTemplatePanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)xslTemplatePanel.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout)xslTemplatePanel.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)xslTemplatePanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                            ((GridBagLayout)xslTemplatePanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                            xslTemplatePanel.setBorder(new CompoundBorder(
                                                new TitledBorder(Localizer.localize("UICDM", "XSLTemplateBorderTitle")),
                                                new EmptyBorder(5, 5, 5, 5)));

                            //======== xslTemplateListScrollPane ========
                            {

                                //---- xslTemplateList ----
                                xslTemplateList.setFont(UIManager.getFont("List.font"));
                                xslTemplateListScrollPane.setViewportView(xslTemplateList);
                            }
                            xslTemplatePanel.add(xslTemplateListScrollPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        xmlExportOptionsPanel.add(xslTemplatePanel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    xmlExportPanel.add(xmlExportOptionsPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== xmlExportButtonsPanel ========
                    {
                        xmlExportButtonsPanel.setOpaque(false);
                        xmlExportButtonsPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)xmlExportButtonsPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                        ((GridBagLayout)xmlExportButtonsPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)xmlExportButtonsPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)xmlExportButtonsPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- createXSLFOReportButton ----
                        createXSLFOReportButton.setFont(UIManager.getFont("Button.font"));
                        createXSLFOReportButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/preview/page_white_acrobat.png")));
                        createXSLFOReportButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                createXSLFOReportButtonActionPerformed(e);
                            }
                        });
                        createXSLFOReportButton.setText(Localizer.localize("UICDM", "CreateXSLFOReportButtonText"));
                        xmlExportButtonsPanel.add(createXSLFOReportButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- exportXMLButton ----
                        exportXMLButton.setFont(UIManager.getFont("Button.font"));
                        exportXMLButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/page_white_code_red.png")));
                        exportXMLButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                exportXMLButtonActionPerformed(e);
                            }
                        });
                        exportXMLButton.setText(Localizer.localize("UICDM", "ExportXMLButtonText"));
                        xmlExportButtonsPanel.add(exportXMLButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    xmlExportPanel.add(xmlExportButtonsPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                tabbedPane.addTab("Export XML File or XSL-FO Report", xmlExportPanel);
            }
            dialogPane.add(tabbedPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 0, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0};

                //---- exportFilterSettingsButton ----
                exportFilterSettingsButton.setText("Export Filter Settings ...");
                exportFilterSettingsButton.setFont(UIManager.getFont("Button.font"));
                exportFilterSettingsButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/table_filter.png")));
                exportFilterSettingsButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        exportFilterSettingsButtonActionPerformed(e);
                    }
                });
                buttonBar.add(exportFilterSettingsButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UICDM", "CancelButtonText"));
                buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(880, 655);
        setLocationRelativeTo(getOwner());

        //---- collateImagesButtonGroup ----
        ButtonGroup collateImagesButtonGroup = new ButtonGroup();
        collateImagesButtonGroup.add(collateImagesRadioButton);
        collateImagesButtonGroup.add(collateFormImagesRadioButton);
        collateImagesButtonGroup.add(individualImagesRadioButton);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel dialogPane;
    private JTabbedPane tabbedPane;
    private JPanel delimitedExportPanel;
    private JPanel exportCSVDescriptionPanel;
    private JLabel exportCSVDescriptionLabel;
    private JHelpLabel exportCSVHelpLabel;
    private JPanel columnOrderingPanel;
    private JLabel orderColumnsLabel;
    private JComboBox sortTypeComboBox;
    private JPanel includedColumnsInExportPanel;
    private JPanel columnsPanel;
    private JLabel availableColumnsLabel;
    private JLabel includedColumnsLabel;
    private JScrollPane includedColumnsScrollPane;
    private JList availableColumnsList;
    private JPanel addRemoveColumnsPanel;
    private JButton addColumnButton;
    private JButton removeColumnButton;
    private JScrollPane includedColumnsListScrollPane;
    private JList includedColumnsList;
    private JPanel columnsUpDownPanel;
    private JButton columnUpButton;
    private JButton columnDownButton;
    private JPanel columnNamePanel;
    private JLabel columnNameLabel;
    private JTextField columnNameTextField;
    private JPanel panel1;
    private JCheckBox combineScoresAndDataCheckBox;
    private JPanel delimitedFileOutputPanel;
    private JPanel panel5;
    private JLabel label19;
    private JComboBox delimiterComboBox;
    private JLabel label20;
    private JComboBox quotesComboBox;
    private JPanel panel3;
    private JCheckBox includeFieldnamesHeaderCheckBox;
    private JCheckBox includeStatisticsCheckBox;
    private JCheckBox includeErrorMessagesCheckBox;
    private JPanel panel6;
    private JCheckBox csvTimestampPrefixCheckBox;
    private JPanel csvExportButtonsPanel;
    private JButton restoreCSVDefaultsButton;
    private JButton setAsCSVDefaultButton;
    private JButton createCSVButton;
    private JPanel exportImagesPanel;
    private JPanel exportImagesDescriptionPanel;
    private JLabel exportImagesDescriptionLabel;
    private JHelpLabel exportImagesHelpLabel;
    private JPanel collateImagesPanel;
    private JRadioButton collateImagesRadioButton;
    private JPanel panel2;
    private JRadioButton collateFormImagesRadioButton;
    private JLabel separatorLabel1;
    private JLabel exportImagesFilenamePrefixLabel;
    private JTextField formImagesFilenamePrefixTextField;
    private JPanel panel9;
    private JRadioButton individualImagesRadioButton;
    private JLabel separatorLabel2;
    private JLabel exportImagesFilenamePrefixLabel2;
    private JTextField individualImagesFilenamePrefixTextField;
    private JCheckBox imageTimestampPrefixCheckBox;
    private JPanel includedInExportPanel;
    private JPanel includedInExportSubPanel;
    private JCheckBox includeGradesCheckBox;
    private JCheckBox includeFormScoreCheckBox;
    private JCheckBox includePageScoreCheckBox;
    private JCheckBox includeSourceDataCheckBox;
    private JCheckBox includeCapturedDataCheckBox;
    private JCheckBox includeIndividualScoresCheckBox;
    private JPanel exportOptionsPanel;
    private JPanel exportOptionsSubPanel1;
    private JLabel pageSizeLabel;
    private JComboBox pageSizeComboBox;
    private JLabel columnFontSizeLabel;
    private JSpinner columnFontSizeSpinner;
    private JPanel exportOptionsSubPanel2;
    private JLabel sourceDataColumnsLabel;
    private JSpinner sourceDataColumnsSpinner;
    private JLabel capturedDataColumnsLabel;
    private JSpinner capturedDataColumnsSpinner;
    private JPanel exportOptionsSubPanel3;
    private JCheckBox rotateImageCheckBox;
    private JPanel exportImagesButtonsPanel;
    private JButton restoreImagesDefaultsButton;
    private JButton setAsImagesDefaultButton;
    private JButton exportImagesButton;
    private JPanel xmlExportPanel;
    private JPanel xmlExportOptionsPanel;
    private JPanel exportXMLDescriptionPanel;
    private JLabel exportXMLDescriptionLabel;
    private JHelpLabel exportXMLHelpLabel;
    private JPanel includedXMLElementsPanel;
    private JCheckBox includeIndividualScoresXMLCheckBox;
    private JCheckBox includeIndividualResponsesXMLCheckBox;
    private JCheckBox includeStatisticsXMLCheckBox;
    private JPanel xmlOutputOptionsPanel;
    private JCheckBox includeXMLHeaderCheckBox;
    private JCheckBox indentXMLContentCheckBox;
    private JCheckBox xmlTimestampPrefixCheckBox;
    private JPanel panel4;
    private JButton restoreXMLDefaultsButton;
    private JButton setAsXMLDefaultButton;
    private JPanel xslTemplatePanel;
    private JScrollPane xslTemplateListScrollPane;
    private JList xslTemplateList;
    private JPanel xmlExportButtonsPanel;
    private JButton createXSLFOReportButton;
    private JButton exportXMLButton;
    private JPanel buttonBar;
    private JButton exportFilterSettingsButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    public boolean isCombineScoresAndData() {
        return this.combineScoresAndDataCheckBox.isSelected();
    }

    public int getIndividualScoresOffset() {
        return getIncludedColumnIndex(ColumnOption.COLUMN_INDIVIDUAL_SCORES);
    }

    public boolean isIncludeErrorMessages() {
        return includeErrorMessagesCheckBox.isSelected();
    }

    public String getFormImagesFilenamePrefix() {
        return this.formImagesFilenamePrefixTextField.getText();
    }

    public String getIndividualImagesFilenamePrefix() {
        return this.individualImagesFilenamePrefixTextField.getText();
    }

    public Collation getCollation() {
        return this.collation;
    }

    public ArrayList<Overlay> getOverlay() {
        return this.overlay;
    }

    public SizeAttributes getSizeAttributes() {

        Object[] selectedObjects = pageSizeComboBox.getSelectedObjects();
        if (selectedObjects != null && selectedObjects.length > 0) {
            String size = (String) selectedObjects[0];
            return getNamedPageSize(size);
        }
        return PreferencesManager.getDefaultFormSizeAttributes();
    }

    public int getSourceDataColumnCount() {
        return (Integer) this.sourceDataColumnsSpinner.getValue();
    }

    public int getCapturedDataColumnCount() {
        return (Integer) this.capturedDataColumnsSpinner.getValue();
    }

    public float getColumnFontSize() {
        return ((Integer) this.columnFontSizeSpinner.getValue()).floatValue();
    }

    public boolean isRotateImage() {
        return this.rotateImageCheckBox.isSelected();
    }

    public boolean isIncludeFormPassword() {
        ColumnOption col =
            getColumnOption(ColumnOption.COLUMN_FORM_PASSWORD, this.includedColumnsList.getModel());
        if (col != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isIncludeFormPageIDs() {
        ColumnOption col =
            getColumnOption(ColumnOption.COLUMN_FORM_PAGE_IDS, this.includedColumnsList.getModel());
        if (col != null) {
            return true;
        } else {
            return false;
        }
    }

    public int getFormPasswordOrderIndex() {
        return getIncludedColumnIndex(ColumnOption.COLUMN_FORM_PASSWORD);
    }

    public int getFormPageIDsOrderIndexOffset() {
        return getIncludedColumnIndex(ColumnOption.COLUMN_FORM_PAGE_IDS);
    }

    public String getFormPasswordColumnName() {
        ColumnOption col =
            getColumnOption(ColumnOption.COLUMN_FORM_PASSWORD, this.includedColumnsList.getModel());
        if (col == null) {
            col = getColumnOption(ColumnOption.COLUMN_FORM_PASSWORD,
                this.availableColumnsList.getModel());
        }
        return col.getFieldName();
    }

    public String getFormPageIdColumnNamePrefix() {
        ColumnOption col =
            getColumnOption(ColumnOption.COLUMN_FORM_PAGE_IDS, this.includedColumnsList.getModel());
        if (col == null) {
            col = getColumnOption(ColumnOption.COLUMN_FORM_PAGE_IDS,
                this.availableColumnsList.getModel());
        }
        return col.getFieldName();
    }

    public void setPublicationIds(ArrayList<Long> publicationIds) {
        this.publicationIds = publicationIds;
        restoreXSLTemplatesList();
    }

    public boolean isIncludeStatistics() {
        return this.includeStatisticsCheckBox.isSelected();
    }

    public boolean isXMLTimestampFilenamePrefix() {
        return this.xmlTimestampPrefixCheckBox.isSelected();
    }

}
