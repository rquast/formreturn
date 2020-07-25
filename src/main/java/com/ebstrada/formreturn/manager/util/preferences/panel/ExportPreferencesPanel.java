package com.ebstrada.formreturn.manager.util.preferences.panel;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.export.ExportMap;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.cdm.dialog.ColumnOption;
import com.ebstrada.formreturn.manager.ui.cdm.dialog.ExportOptionsDialog;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.CSVExportPreferences;

public class ExportPreferencesPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public ExportPreferencesPanel() {
        initComponents();
        restoreSettings();
    }

    private void restoreColumns(CSVExportPreferences ep) {
        DefaultListModel includeDLM = new DefaultListModel();
        DefaultListModel availableDLM = new DefaultListModel();

        ExportOptionsDialog
            .setDefaultColumns(ep.getOrderedColumnKeys(), includeDLM, availableDLM, ep);

        this.includedColumnsList.setModel(includeDLM);
        this.availableColumnsList.setModel(availableDLM);
    }

    private void restoreSettings() {

        CSVExportPreferences ep = PreferencesManager.getCSVExportPreferences();
        ep.getPublicationIdColumnName(); // here to get the udpate to run

        switch (ep.getSortType()) {
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

        restoreColumns(ep);

        combineScoresAndDataCheckBox.setSelected(ep.isCombineScoresAndData());

        includeFieldnamesHeaderCheckBox.setSelected(ep.isIncludeFieldnamesHeader());

        includeStatisticsCheckBox.setSelected(ep.isIncludeStatistics());

        includeErrorMessagesCheckBox.setSelected(ep.isIncludeErrorMessages());

        switch (ep.getDelimiterType()) {
            case CSVExportPreferences.TSV_DELIMITER:
                delimiterComboBox.setSelectedIndex(1);
                break;
            case CSVExportPreferences.CSV_DELIMITER:
            default:
                delimiterComboBox.setSelectedIndex(0);
        }

        switch (ep.getQuotesType()) {
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

    private void setExportPreferences(DefaultListModel dlm, CSVExportPreferences ep,
        boolean selected) {

        for (int i = 0; i < dlm.getSize(); i++) {

            ColumnOption col = (ColumnOption) dlm.getElementAt(i);
            int type = col.getType();

            switch (type) {

                case ColumnOption.COLUMN_FORM_SCORE:
                    ep.setIncludeAggregated(selected);
                    break;

                case ColumnOption.COLUMN_SEGMENT_SCORE:
                    ep.setIncludeAggregatedSegment(selected);
                    break;

                case ColumnOption.COLUMN_CAPTURE_TIME:
                    ep.setIncludeCaptureTime(selected);
                    break;

                case ColumnOption.COLUMN_PROCESSED_TIME:
                    ep.setIncludeProcessedTime(selected);
                    break;

                case ColumnOption.COLUMN_IMAGE_FILE_NAMES:
                    ep.setIncludeImageFileNames(selected);
                    break;

                case ColumnOption.COLUMN_SCANNED_PAGE_NUMBER:
                    ep.setIncludeScannedPageNumber(selected);
                    break;

                case ColumnOption.COLUMN_INDIVIDUAL_SCORES:
                    ep.setIncludeMarkScores(selected);
                    break;

                case ColumnOption.COLUMN_FORM_ID:
                    ep.setIncludeFormID(selected);
                    break;

                case ColumnOption.COLUMN_FORM_PASSWORD:
                    ep.setIncludeFormPassword(selected);
                    break;

                case ColumnOption.COLUMN_FORM_PAGE_IDS:
                    ep.setIncludeFormPageIDs(selected);
                    break;

                case ColumnOption.COLUMN_PUBLICATION_ID:
                    ep.setIncludePublicationID(selected);
                    break;

                case ColumnOption.COLUMN_SOURCE_DATA:
                    ep.setIncludeSourceData(selected);
                    break;

                case ColumnOption.COLUMN_CAPTURED_DATA:
                    ep.setIncludeCapturedData(selected);
                    break;

            }

        }

    }

    private boolean isValidColumnNames() {

        ListModel model = this.includedColumnsList.getModel();

        for (int i = 0; i < model.getSize(); i++) {

            ColumnOption col = (ColumnOption) model.getElementAt(i);

            switch (col.getType()) {

                case ColumnOption.COLUMN_FORM_SCORE:
                case ColumnOption.COLUMN_SEGMENT_SCORE:
                case ColumnOption.COLUMN_PUBLICATION_ID:
                case ColumnOption.COLUMN_FORM_ID:
                case ColumnOption.COLUMN_FORM_PASSWORD:
                case ColumnOption.COLUMN_FORM_PAGE_IDS:
                    if (col.getFieldName().trim().length() <= 0) {
                        return false;
                    }

            }

        }

        return true;

    }

    private void saveColumnSettings(CSVExportPreferences ep) {

        ArrayList<Integer> orderedColumnKeys = new ArrayList<Integer>();

        ListModel model = this.includedColumnsList.getModel();

        if (model.getSize() > 0) {

            for (int i = 0; i < model.getSize(); i++) {

                ColumnOption col = (ColumnOption) model.getElementAt(i);

                orderedColumnKeys.add(col.getType());

                switch (col.getType()) {

                    case ColumnOption.COLUMN_FORM_SCORE:
                        ep.setFormScoreColumnName(col.getFieldName());
                        break;

                    case ColumnOption.COLUMN_SEGMENT_SCORE:
                        ep.setSegmentScoreColumnName(col.getFieldName());
                        break;

                    case ColumnOption.COLUMN_PUBLICATION_ID:
                        ep.setPublicationIdColumnName(col.getFieldName());
                        break;

                    case ColumnOption.COLUMN_FORM_ID:
                        ep.setFormIdColumnName(col.getFieldName());
                        break;

                    case ColumnOption.COLUMN_FORM_PASSWORD:
                        ep.setFormPasswordColumnName(col.getFieldName());
                        break;

                    case ColumnOption.COLUMN_FORM_PAGE_IDS:
                        ep.setFormPageIDsColumnNamePrefix(col.getFieldName());
                        break;

                }

            }

        }

        model = this.availableColumnsList.getModel();

        if (model.getSize() > 0) {

            for (int i = 0; i < model.getSize(); i++) {

                ColumnOption col = (ColumnOption) model.getElementAt(i);

                orderedColumnKeys.add(col.getType());

            }

        }

        ep.setOrderedColumnKeys(orderedColumnKeys);

    }

    private void saveExportSettingsButtonActionPerformed(ActionEvent e) {

        if (!isValidColumnNames()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String message = Localizer
                        .localize("Util", "ExportPreferencesInvalidColumnNameLengthMessage");
                    Misc.showErrorMsg(getRootPane().getTopLevelAncestor(), message, Localizer
                        .localize("Util", "ExportPreferencesInvalidColumnNameLengthTitle"));
                }
            });
            return;
        }

        CSVExportPreferences ep = PreferencesManager.getCSVExportPreferences();

        DefaultListModel includedDLM = (DefaultListModel) this.includedColumnsList.getModel();
        DefaultListModel availableDLM = (DefaultListModel) this.availableColumnsList.getModel();

        setExportPreferences(includedDLM, ep, true);
        setExportPreferences(availableDLM, ep, false);

        saveColumnSettings(ep);

        ep.setCombineScoresAndData(combineScoresAndDataCheckBox.isSelected());
        ep.setIncludeFieldnamesHeader(includeFieldnamesHeaderCheckBox.isSelected());
        ep.setIncludeErrorMessages(includeErrorMessagesCheckBox.isSelected());
        ep.setIncludeStatistics(includeStatisticsCheckBox.isSelected());

        ep.setSortType(this.sortTypeComboBox.getSelectedIndex() + 1);
        ep.setDelimiterType(this.delimiterComboBox.getSelectedIndex() + 1);
        ep.setQuotesType(this.quotesComboBox.getSelectedIndex() + 1);


        try {
            PreferencesManager.savePreferences(Main.getXstream());
            String message =
                Localizer.localize("Util", "ExportPreferencesSavedSuccessfullyMessage");
            Misc.showSuccessMsg(this, message);
        } catch (IOException e1) {
            Misc.showErrorMsg(this, Localizer.localize("Util", "ErrorSavingPreferencesMessage"),
                Localizer.localize("Util", "ErrorSavingPreferencesTitle"));
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
            return;
        }


    }

    private void restoreDefaultExportSettingsButtonActionPerformed(ActionEvent e) {

        sortTypeComboBox.setSelectedIndex(0);

        // restores the default columns
        CSVExportPreferences ep = PreferencesManager.getCSVExportPreferences();
        ep.getPublicationIdColumnName(); // here to get the udpate to run
        ep.setOrderedColumnKeys(null);

        ep.setPublicationIdColumnName("publication_id");
        ep.setFormIdColumnName("form_id");
        ep.setFormPasswordColumnName("form_password");
        ep.setFormPageIDsColumnNamePrefix("form_page_id_");
        ep.setFormScoreColumnName("form_score");

        ep.setIncludeAggregated(true);
        ep.setIncludeAggregatedSegment(true);
        ep.setIncludeCapturedData(true);
        ep.setIncludeFormID(true);
        ep.setIncludeFormPassword(true);
        ep.setIncludeFormPageIDs(true);
        ep.setIncludeImageFileNames(true);
        ep.setIncludeScannedPageNumber(true);
        ep.setIncludeMarkScores(true);
        ep.setIncludeCaptureTime(true);
        ep.setIncludeProcessedTime(true);
        ep.setIncludePublicationID(true);
        ep.setTimestampFilenamePrefix(false);
        ep.setIncludeSourceData(true);

        restoreColumns(ep);

        combineScoresAndDataCheckBox.setSelected(true);
        includeFieldnamesHeaderCheckBox.setSelected(true);
        delimiterComboBox.setSelectedIndex(0);
        quotesComboBox.setSelectedIndex(0);

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

    private void columnNameTextFieldCaretUpdate(CaretEvent e) {
        updateColumnName();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        columnOrderingHeadingLabel = new JLabel();
        panel5 = new JPanel();
        panel2 = new JPanel();
        orderColumnsLabel = new JLabel();
        sortTypeComboBox = new JComboBox();
        panel9 = new JPanel();
        includeColumnsInExportHeadingLabel = new JLabel();
        panel10 = new JPanel();
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
        combineScoresPanel = new JPanel();
        combineScoresAndDataCheckBox = new JCheckBox();
        panel7 = new JPanel();
        delimitedFileOutputHeadingLabel = new JLabel();
        panel8 = new JPanel();
        panel11 = new JPanel();
        delimiterLabel = new JLabel();
        delimiterComboBox = new JComboBox();
        quotesLabel = new JLabel();
        quotesComboBox = new JComboBox();
        panel6 = new JPanel();
        includeFieldnamesHeaderCheckBox = new JCheckBox();
        includeStatisticsCheckBox = new JCheckBox();
        includeErrorMessagesCheckBox = new JCheckBox();
        panel12 = new JPanel();
        restoreDefaultExportSettingsButton = new JButton();
        saveExportSettingsButton = new JButton();

        //======== this ========
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {35, 35, 35, 0, 0, 0, 0, 0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights =
            new double[] {0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- columnOrderingHeadingLabel ----
            columnOrderingHeadingLabel.setFont(UIManager.getFont("Label.font"));
            columnOrderingHeadingLabel
                .setText(Localizer.localize("Util", "ExportPreferencesColumnOrderingHeadingLabel"));
            panel1.add(columnOrderingHeadingLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //======== panel5 ========
            {
                panel5.setOpaque(false);
                panel5.setBorder(new MatteBorder(0, 0, 1, 0, Color.lightGray));
                panel5.setLayout(new BorderLayout());
            }
            panel1.add(panel5,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel2 ========
        {
            panel2.setBorder(null);
            panel2.setOpaque(false);
            panel2.setLayout(new GridBagLayout());
            ((GridBagLayout) panel2.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
            ((GridBagLayout) panel2.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel2.getLayout()).columnWeights =
                new double[] {0.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel2.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- orderColumnsLabel ----
            orderColumnsLabel.setFont(UIManager.getFont("Label.font"));
            orderColumnsLabel
                .setText(Localizer.localize("Util", "ExportPreferencesOrderColumnsLabel"));
            panel2.add(orderColumnsLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- sortTypeComboBox ----
            sortTypeComboBox.setFont(UIManager.getFont("ComboBox.font"));
            sortTypeComboBox.setModel(new DefaultComboBoxModel(
                new String[] {Localizer.localize("Util", "ColumnOrderingOption0"),
                    Localizer.localize("Util", "ColumnOrderingOption1"),
                    Localizer.localize("Util", "ColumnOrderingOption2")}));
            panel2.add(sortTypeComboBox,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
        }
        add(panel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel9 ========
        {
            panel9.setOpaque(false);
            panel9.setLayout(new GridBagLayout());
            ((GridBagLayout) panel9.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) panel9.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel9.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel9.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- includeColumnsInExportHeadingLabel ----
            includeColumnsInExportHeadingLabel.setFont(UIManager.getFont("Label.font"));
            includeColumnsInExportHeadingLabel.setText(
                Localizer.localize("Util", "ExportPreferencesIncludeColumnsInExportHeadingLabel"));
            panel9.add(includeColumnsInExportHeadingLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //======== panel10 ========
            {
                panel10.setOpaque(false);
                panel10.setBorder(new MatteBorder(0, 0, 1, 0, Color.lightGray));
                panel10.setLayout(new BorderLayout());
            }
            panel9.add(panel10,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel9, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //======== includedColumnsInExportPanel ========
        {
            includedColumnsInExportPanel.setOpaque(false);
            includedColumnsInExportPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) includedColumnsInExportPanel.getLayout()).columnWidths =
                new int[] {0, 0};
            ((GridBagLayout) includedColumnsInExportPanel.getLayout()).rowHeights =
                new int[] {0, 35, 30, 0};
            ((GridBagLayout) includedColumnsInExportPanel.getLayout()).columnWeights =
                new double[] {1.0, 1.0E-4};
            ((GridBagLayout) includedColumnsInExportPanel.getLayout()).rowWeights =
                new double[] {1.0, 0.0, 0.0, 1.0E-4};

            //======== columnsPanel ========
            {
                columnsPanel.setOpaque(false);
                columnsPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) columnsPanel.getLayout()).columnWidths =
                    new int[] {255, 65, 255, 60, 0};
                ((GridBagLayout) columnsPanel.getLayout()).rowHeights = new int[] {35, 0, 0};
                ((GridBagLayout) columnsPanel.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 1.0, 0.0, 1.0E-4};
                ((GridBagLayout) columnsPanel.getLayout()).rowWeights =
                    new double[] {0.0, 1.0, 1.0E-4};

                //---- availableColumnsLabel ----
                availableColumnsLabel.setFont(UIManager.getFont("Label.font"));
                availableColumnsLabel.setText(Localizer.localize("UICDM", "AvailableColumnsLabel"));
                columnsPanel.add(availableColumnsLabel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

                //---- includedColumnsLabel ----
                includedColumnsLabel.setFont(UIManager.getFont("Label.font"));
                includedColumnsLabel.setText(Localizer.localize("UICDM", "IncludedColumnsLabel"));
                columnsPanel.add(includedColumnsLabel,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

                //======== includedColumnsScrollPane ========
                {

                    //---- availableColumnsList ----
                    availableColumnsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    availableColumnsList.setFont(UIManager.getFont("List.font"));
                    includedColumnsScrollPane.setViewportView(availableColumnsList);
                }
                columnsPanel.add(includedColumnsScrollPane,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //======== addRemoveColumnsPanel ========
                {
                    addRemoveColumnsPanel.setOpaque(false);
                    addRemoveColumnsPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) addRemoveColumnsPanel.getLayout()).columnWidths =
                        new int[] {0, 0};
                    ((GridBagLayout) addRemoveColumnsPanel.getLayout()).rowHeights =
                        new int[] {35, 0, 30, 0};
                    ((GridBagLayout) addRemoveColumnsPanel.getLayout()).columnWeights =
                        new double[] {1.0, 1.0E-4};
                    ((GridBagLayout) addRemoveColumnsPanel.getLayout()).rowWeights =
                        new double[] {0.0, 1.0, 0.0, 1.0E-4};

                    //---- addColumnButton ----
                    addColumnButton.setText(">>");
                    addColumnButton.setFont(UIManager.getFont("Button.font"));
                    addColumnButton.addActionListener(new ActionListener() {
                        @Override public void actionPerformed(ActionEvent e) {
                            addColumnButtonActionPerformed(e);
                        }
                    });
                    addRemoveColumnsPanel.add(addColumnButton,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                    //---- removeColumnButton ----
                    removeColumnButton.setText("<<");
                    removeColumnButton.setFont(UIManager.getFont("Button.font"));
                    removeColumnButton.addActionListener(new ActionListener() {
                        @Override public void actionPerformed(ActionEvent e) {
                            removeColumnButtonActionPerformed(e);
                        }
                    });
                    addRemoveColumnsPanel.add(removeColumnButton,
                        new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                }
                columnsPanel.add(addRemoveColumnsPanel,
                    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //======== includedColumnsListScrollPane ========
                {

                    //---- includedColumnsList ----
                    includedColumnsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    includedColumnsList.setFont(UIManager.getFont("List.font"));
                    includedColumnsList.addListSelectionListener(new ListSelectionListener() {
                        @Override public void valueChanged(ListSelectionEvent e) {
                            includedColumnsListValueChanged(e);
                        }
                    });
                    includedColumnsListScrollPane.setViewportView(includedColumnsList);
                }
                columnsPanel.add(includedColumnsListScrollPane,
                    new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //======== columnsUpDownPanel ========
                {
                    columnsUpDownPanel.setOpaque(false);
                    columnsUpDownPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) columnsUpDownPanel.getLayout()).columnWidths =
                        new int[] {0, 0};
                    ((GridBagLayout) columnsUpDownPanel.getLayout()).rowHeights =
                        new int[] {35, 15, 30, 0};
                    ((GridBagLayout) columnsUpDownPanel.getLayout()).columnWeights =
                        new double[] {1.0, 1.0E-4};
                    ((GridBagLayout) columnsUpDownPanel.getLayout()).rowWeights =
                        new double[] {0.0, 0.0, 0.0, 1.0E-4};

                    //---- columnUpButton ----
                    columnUpButton.setFont(UIManager.getFont("Button.font"));
                    columnUpButton.setIcon(new ImageIcon(getClass()
                        .getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_up.png")));
                    columnUpButton.addActionListener(new ActionListener() {
                        @Override public void actionPerformed(ActionEvent e) {
                            columnUpButtonActionPerformed(e);
                        }
                    });
                    columnUpButton.setText(Localizer.localize("UICDM", "Up"));
                    columnsUpDownPanel.add(columnUpButton,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                    //---- columnDownButton ----
                    columnDownButton.setFont(UIManager.getFont("Button.font"));
                    columnDownButton.setIcon(new ImageIcon(getClass()
                        .getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_down.png")));
                    columnDownButton.addActionListener(new ActionListener() {
                        @Override public void actionPerformed(ActionEvent e) {
                            columnDownButtonActionPerformed(e);
                        }
                    });
                    columnDownButton.setText(Localizer.localize("UICDM", "Down"));
                    columnsUpDownPanel.add(columnDownButton,
                        new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                }
                columnsPanel.add(columnsUpDownPanel,
                    new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            includedColumnsInExportPanel.add(columnsPanel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

            //======== columnNamePanel ========
            {
                columnNamePanel.setOpaque(false);
                columnNamePanel.setLayout(new GridBagLayout());
                ((GridBagLayout) columnNamePanel.getLayout()).columnWidths =
                    new int[] {0, 0, 305, 0, 0};
                ((GridBagLayout) columnNamePanel.getLayout()).rowHeights = new int[] {30, 0};
                ((GridBagLayout) columnNamePanel.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                ((GridBagLayout) columnNamePanel.getLayout()).rowWeights =
                    new double[] {0.0, 1.0E-4};

                //---- columnNameLabel ----
                columnNameLabel.setFont(UIManager.getFont("Label.font"));
                columnNameLabel.setText(Localizer.localize("UICDM", "ColumnNameLabel"));
                columnNamePanel.add(columnNameLabel,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                //---- columnNameTextField ----
                columnNameTextField.setEnabled(false);
                columnNameTextField.setFont(UIManager.getFont("TextField.font"));
                columnNameTextField.setOpaque(false);
                columnNameTextField.addCaretListener(new CaretListener() {
                    @Override public void caretUpdate(CaretEvent e) {
                        columnNameTextFieldCaretUpdate(e);
                    }
                });
                columnNamePanel.add(columnNameTextField,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
            }
            includedColumnsInExportPanel.add(columnNamePanel,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //======== combineScoresPanel ========
            {
                combineScoresPanel.setOpaque(false);
                combineScoresPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) combineScoresPanel.getLayout()).columnWidths =
                    new int[] {0, 0, 0, 0};
                ((GridBagLayout) combineScoresPanel.getLayout()).rowHeights = new int[] {30, 0};
                ((GridBagLayout) combineScoresPanel.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 1.0, 1.0E-4};
                ((GridBagLayout) combineScoresPanel.getLayout()).rowWeights =
                    new double[] {0.0, 1.0E-4};

                //---- combineScoresAndDataCheckBox ----
                combineScoresAndDataCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
                combineScoresAndDataCheckBox.setSelected(true);
                combineScoresAndDataCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                combineScoresAndDataCheckBox.setOpaque(false);
                combineScoresAndDataCheckBox
                    .setText(Localizer.localize("UICDM", "CombineScoresAndDataCheckBoxText"));
                combineScoresPanel.add(combineScoresAndDataCheckBox,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
            }
            includedColumnsInExportPanel.add(combineScoresPanel,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(includedColumnsInExportPanel,
            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel7 ========
        {
            panel7.setOpaque(false);
            panel7.setLayout(new GridBagLayout());
            ((GridBagLayout) panel7.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) panel7.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel7.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel7.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- delimitedFileOutputHeadingLabel ----
            delimitedFileOutputHeadingLabel.setFont(UIManager.getFont("Label.font"));
            delimitedFileOutputHeadingLabel.setText(
                Localizer.localize("Util", "ExportPreferencesDelimitedFileOutputHeadingLabel"));
            panel7.add(delimitedFileOutputHeadingLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //======== panel8 ========
            {
                panel8.setOpaque(false);
                panel8.setBorder(new MatteBorder(0, 0, 1, 0, Color.lightGray));
                panel8.setLayout(new BorderLayout());
            }
            panel7.add(panel8,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel7, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel11 ========
        {
            panel11.setOpaque(false);
            panel11.setLayout(new GridBagLayout());
            ((GridBagLayout) panel11.getLayout()).columnWidths =
                new int[] {0, 0, 0, 15, 0, 0, 0, 0};
            ((GridBagLayout) panel11.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel11.getLayout()).columnWeights =
                new double[] {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel11.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- delimiterLabel ----
            delimiterLabel.setFont(UIManager.getFont("Label.font"));
            delimiterLabel.setText(Localizer.localize("Util", "ExportPreferencesDelimiterLabel"));
            panel11.add(delimiterLabel,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- delimiterComboBox ----
            delimiterComboBox.setModel(new DefaultComboBoxModel(
                new String[] {"CSV - Comma Separated Values", "TSV - Tab Separated Values"}));
            delimiterComboBox.setFont(UIManager.getFont("ComboBox.font"));
            delimiterComboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxxxxxx");
            panel11.add(delimiterComboBox,
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- quotesLabel ----
            quotesLabel.setFont(UIManager.getFont("Label.font"));
            quotesLabel.setText(Localizer.localize("Util", "ExportPreferencesQuotesLabel"));
            panel11.add(quotesLabel,
                new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- quotesComboBox ----
            quotesComboBox.setModel(new DefaultComboBoxModel(
                new String[] {"\" (Double Quotes)", "' (Single Quotes)", "None"}));
            quotesComboBox.setFont(UIManager.getFont("ComboBox.font"));
            panel11.add(quotesComboBox,
                new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
        }
        add(panel11, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel6 ========
        {
            panel6.setOpaque(false);
            panel6.setBorder(null);
            panel6.setLayout(new GridBagLayout());
            ((GridBagLayout) panel6.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0};
            ((GridBagLayout) panel6.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel6.getLayout()).columnWeights =
                new double[] {1.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel6.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

            //---- includeFieldnamesHeaderCheckBox ----
            includeFieldnamesHeaderCheckBox.setFont(UIManager.getFont("CheckBox.font"));
            includeFieldnamesHeaderCheckBox.setSelected(true);
            includeFieldnamesHeaderCheckBox.setOpaque(false);
            includeFieldnamesHeaderCheckBox.setText(
                Localizer.localize("Util", "ExportPreferencesIncludeFieldNamesHeaderCheckBox"));
            panel6.add(includeFieldnamesHeaderCheckBox,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));

            //---- includeStatisticsCheckBox ----
            includeStatisticsCheckBox.setFont(UIManager.getFont("CheckBox.font"));
            includeStatisticsCheckBox.setOpaque(false);
            includeStatisticsCheckBox
                .setText(Localizer.localize("UICDM", "IncludeStatisticsCheckBox"));
            panel6.add(includeStatisticsCheckBox,
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));

            //---- includeErrorMessagesCheckBox ----
            includeErrorMessagesCheckBox.setFont(UIManager.getFont("CheckBox.font"));
            includeErrorMessagesCheckBox.setSelected(true);
            includeErrorMessagesCheckBox.setOpaque(false);
            includeErrorMessagesCheckBox
                .setText(Localizer.localize("UICDM", "IncludeErrorMessagesCheckBox"));
            panel6.add(includeErrorMessagesCheckBox,
                new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));
        }
        add(panel6, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel12 ========
        {
            panel12.setOpaque(false);
            panel12.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, Color.lightGray),
                new EmptyBorder(5, 0, 0, 0)));
            panel12.setLayout(new GridBagLayout());
            ((GridBagLayout) panel12.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
            ((GridBagLayout) panel12.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel12.getLayout()).columnWeights =
                new double[] {0.0, 1.0, 0.0, 1.0E-4};
            ((GridBagLayout) panel12.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- restoreDefaultExportSettingsButton ----
            restoreDefaultExportSettingsButton.setFont(UIManager.getFont("Button.font"));
            restoreDefaultExportSettingsButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
            restoreDefaultExportSettingsButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    restoreDefaultExportSettingsButtonActionPerformed(e);
                }
            });
            restoreDefaultExportSettingsButton
                .setText(Localizer.localize("Util", "ExportPreferencesRestoreDefaultsButtonText"));
            panel12.add(restoreDefaultExportSettingsButton,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- saveExportSettingsButton ----
            saveExportSettingsButton.setFont(UIManager.getFont("Button.font"));
            saveExportSettingsButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
            saveExportSettingsButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    saveExportSettingsButtonActionPerformed(e);
                }
            });
            saveExportSettingsButton.setText(
                Localizer.localize("Util", "ExportPreferencesSaveExportSettingsButtonText"));
            panel12.add(saveExportSettingsButton,
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel12, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel columnOrderingHeadingLabel;
    private JPanel panel5;
    private JPanel panel2;
    private JLabel orderColumnsLabel;
    private JComboBox sortTypeComboBox;
    private JPanel panel9;
    private JLabel includeColumnsInExportHeadingLabel;
    private JPanel panel10;
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
    private JPanel combineScoresPanel;
    private JCheckBox combineScoresAndDataCheckBox;
    private JPanel panel7;
    private JLabel delimitedFileOutputHeadingLabel;
    private JPanel panel8;
    private JPanel panel11;
    private JLabel delimiterLabel;
    private JComboBox delimiterComboBox;
    private JLabel quotesLabel;
    private JComboBox quotesComboBox;
    private JPanel panel6;
    private JCheckBox includeFieldnamesHeaderCheckBox;
    private JCheckBox includeStatisticsCheckBox;
    private JCheckBox includeErrorMessagesCheckBox;
    private JPanel panel12;
    private JButton restoreDefaultExportSettingsButton;
    private JButton saveExportSettingsButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
