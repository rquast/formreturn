package com.ebstrada.formreturn.manager.ui.editor;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import com.ebstrada.formreturn.api.messaging.ProcessingStatusDialog;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigBarcode;
import com.ebstrada.formreturn.manager.gef.presentation.FigSegment;
import com.ebstrada.formreturn.manager.gef.presentation.FigSegmentArea;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.publish.FormPublisher;
import com.ebstrada.formreturn.manager.logic.publish.FormPublisherException;
import com.ebstrada.formreturn.manager.logic.recognition.structure.FormRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.PublicationRecognitionStructure;
import com.ebstrada.formreturn.manager.persistence.viewer.GenericDataViewer;
import com.ebstrada.formreturn.manager.persistence.xstream.Document;
import com.ebstrada.formreturn.manager.persistence.xstream.Page;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.ui.editor.frame.FormFrame;
import com.ebstrada.formreturn.manager.ui.filefilter.FilenameExtensionFilter;
import com.ebstrada.formreturn.manager.ui.sdm.model.PublicationDataModel;
import com.ebstrada.formreturn.manager.ui.sdm.model.TableDataModel;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.PublicationPreferences;

public class PublishFormPanel extends JPanel implements GenericDataViewer {

    private static final long serialVersionUID = 1L;
    private TableDataModel tableDataModel;
    private PublicationDataModel publicationDataModel;

    private PublicationRecognitionStructure publicationRecognitionStructure;
    private FormFrame formFrame;

    public PublishFormPanel(FormFrame formFrame) {
        initComponents();

        tabbedPane1.setTitleAt(0, Localizer.localize("UI", "PublishTabPanelTitle"));
        tabbedPane1.setTitleAt(1, Localizer.localize("UI", "SettingsTabPanelTitle"));

        this.formFrame = formFrame;
        publishScrollPane.getViewport().setOpaque(false);
        publishScrollPane.getViewport().setBorder(null);
        publishScrollPane.setViewportBorder(null);

        tableDataModel = new TableDataModel();
        tablesTableFilterPanel.setTableModel(tableDataModel);
        tablesTableFilterPanel.setTableViewer(this);

        publicationDataModel = new PublicationDataModel();
        publicationsTableFilterPanel.setTableModel(publicationDataModel);
        publicationsTableFilterPanel.setTableViewer(this);

        refresh();

        SelectionListener listener = new SelectionListener(tablesTable);
        tablesTable.getSelectionModel().addListSelectionListener(listener);
        restoreSettings();
    }

    private void restoreSettings() {
        PublicationPreferences publicationPreferences =
            PreferencesManager.getPublicationPreferences();
        List<String> publicationTypes = PublicationPreferences.getPublicationTypes();
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        for (String publicationType : publicationTypes) {
            dcbm.addElement(publicationType);
        }
        publicationTypeComboBox.setModel(dcbm);
        collatePDFCheckBox.setSelected(publicationPreferences.isCollatePDFPages());
        if (publicationPreferences.getDefaultPublicationType() > 0) {
            publicationTypeComboBox
                .setSelectedIndex((publicationPreferences.getDefaultPublicationType() - 1));
        }
    }

    public class SelectionListener implements ListSelectionListener {
        JTable table;

        // It is necessary to keep the table since it is not possible
        // to determine the table from the event's source
        SelectionListener(JTable table) {
            this.table = table;
        }

        public void valueChanged(ListSelectionEvent e) {
            // If cell selection is enabled, both row and column change events are fired
            if (e.getSource() == table.getSelectionModel() && table.getRowSelectionAllowed()
                && e.getValueIsAdjusting() == false) {
                // Column selection changed
                int first = e.getFirstIndex();
                int last = e.getLastIndex();
                if (tablesTable.getSelectedRow() != -1) {
                    tableDataModel.setSelectedIds(getSelectedDataSetIds());
                    selectedTableTextField.setText(getSelectedDataSetName());
                    if (publicationNameTextField.getText().trim().length() <= 0) {
                        publicationNameTextField.setText(getSelectedDataSetName());
                    }
                    long parentId = -1;
                    long[] selectedIds = getSelectedDataSetIds();
                    if (selectedIds.length > 0) {
                        parentId = selectedIds[0];
                        publicationDataModel.setParentId(parentId);
                    }
                    restorePublications();
                } else {
                    tableDataModel.setSelectedIds(new long[] {});
                    selectedTableTextField.setText(getSelectedDataSetName());
                    if (publicationNameTextField.getText().trim().length() <= 0) {
                        publicationNameTextField.setText(getSelectedDataSetName());
                    }
                    publicationDataModel.setParentId(-1);
                    restorePublications();
                }
            } else if (e.getSource() == table.getColumnModel().getSelectionModel() && table
                .getColumnSelectionAllowed()) {
                // Row selection changed
                int first = e.getFirstIndex();
                int last = e.getLastIndex();
            }

            if (e.getValueIsAdjusting()) {
                // The mouse button has not yet been released
            }
        }

    }

    public void resetVerticalDivider() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                verticalSplitPane.setDividerLocation(0.5d);
            }
        });
    }

    public void refresh() {
        refresh(true);
    }

    public void refresh(boolean updatePageNumbers) {
        restoreTables();
        if (updatePageNumbers) {
            tablesTableFilterPanel.updatePageNumbers();
        }
        restorePublications();
        if (updatePageNumbers) {
            publicationsTableFilterPanel.updatePageNumbers();
        }
    }


    public void refresh(boolean updatePageNumbers, TableFilterPanel tableFilterPanel) {

        if (tableFilterPanel.getTableModel() instanceof TableDataModel) {
            restoreTables();
            if (updatePageNumbers) {
                tablesTableFilterPanel.updatePageNumbers();
            }
        } else if (tableFilterPanel.getTableModel() instanceof PublicationDataModel) {
            restorePublications();
            if (updatePageNumbers) {
                publicationsTableFilterPanel.updatePageNumbers();
            }
        }

    }

    public String getSelectedDataSetName() {
        if (tablesTable.getSelectedRow() != -1) {
            return (String) tablesTable.getValueAt(tablesTable.getSelectedRow(), 1);
        } else {
            return "";
        }
    }

    private void restoreTables() {
        // restore tables table
        String regexFilter = formFrame.getDocumentAttributes().getSourceDataTableFilterRegex();
        tablesTable.setModel(tableDataModel.getTableModel(regexFilter));
        tablesTable.getColumn("ID").setMaxWidth(150);
        tablesTable.getTableHeader().setReorderingAllowed(false);
        tablesTableFilterPanel.updatePageNumbers();
    }

    public void restorePublications() {
        // restore publications table
        publicationsTable.setModel(publicationDataModel.getTableModel());
        publicationsTable.getColumn("ID").setMaxWidth(150);
        publicationsTable.getTableHeader().setReorderingAllowed(false);
        publicationsTableFilterPanel.updatePageNumbers();
    }

    public long[] getSelectedDataSetIds() {
        int selectedDataSetRows[] = tablesTable.getSelectedRows();
        long selectedDataSetIds[] = new long[] {};
        if (selectedDataSetRows.length > 0) {
            selectedDataSetIds = new long[selectedDataSetRows.length];
            for (int i = 0; i < selectedDataSetRows.length; i++) {
                selectedDataSetIds[i] =
                    Long.parseLong((String) tablesTable.getValueAt(selectedDataSetRows[i], 0));
            }
        }
        return selectedDataSetIds;
    }

    public long[] getSelectedPublicationIds() {
        int selectedPublicationRows[] = publicationsTable.getSelectedRows();
        long selectedPublicationIds[] = new long[] {};
        if (selectedPublicationRows.length > 0) {
            selectedPublicationIds = new long[selectedPublicationRows.length];
            for (int i = 0; i < selectedPublicationRows.length; i++) {
                selectedPublicationIds[i] = Long.parseLong(
                    (String) publicationsTable.getValueAt(selectedPublicationRows[i], 0));
            }
        }
        return selectedPublicationIds;
    }

    private void checkFormIDsButtonActionPerformed(ActionEvent e) {
        if (checkFormIds()) {
            String msg = Localizer.localize("UI", "FormIDsFoundMessage");
            Misc.showSuccessMsg(Main.getInstance(), msg);
        } else {
            String msg = Localizer.localize("UI", "FormIDsNotFoundMessage");
            Misc.showErrorMsg(Main.getInstance(), msg);
        }
    }

    private void removeEmptySegmentAreas() {

        ArrayList<FigSegment> removeEmptyAreas = new ArrayList<FigSegment>();

        Map<String, Page> pages = formFrame.getDocument().getPages();
        Iterator<Page> pageIterator = pages.values().iterator();

        while (pageIterator.hasNext()) {
            Page page = pageIterator.next();
            List<Fig> figs = page.getFigs();
            for (Fig fig : figs) {
                if (fig instanceof FigSegment) {
                    FigSegment figSegment = ((FigSegment) fig);
                    if (figSegment.getSegmentContainer() == null) {
                        removeEmptyAreas.add(figSegment);
                        continue;
                    }
                    ArrayList<Document> segments = figSegment.getSegmentContainer().getSegments();
                    if (segments == null || segments.size() <= 0) {
                        removeEmptyAreas.add(figSegment);
                    }
                }
            }
            for (FigSegment figSegment : removeEmptyAreas) {
                page.getFigs().remove(figSegment);
            }
        }

    }

    private boolean checkFormIds() {

        boolean missingBarcodes = false;

        Map<String, Page> pages = formFrame.getDocument().getPages();
        Iterator<Page> pageIterator = pages.values().iterator();
        while (pageIterator.hasNext()) {

            boolean pageHasFormIDBarcode = false;

            Page page = pageIterator.next();
            List<Fig> figs = page.getFigs();
            for (Fig fig : figs) {
                if (fig instanceof FigBarcode) {
                    if (((FigBarcode) fig).getBarcodeType().equalsIgnoreCase("Form ID")) {
                        pageHasFormIDBarcode = true;
                    }
                }
            }

            if (pageHasFormIDBarcode == false) {
                missingBarcodes = true;
            }

        }

        return !missingBarcodes;

    }

    private void publishPDFButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String title = Localizer.localize("UI", "PrintAfterPDFTitle");
                String message = Localizer.localize("UI", "PrintAfterPDFMessage");
                String confirmButtonText = Localizer.localize("UI", "Yes");
                String cancelButtonText = Localizer.localize("UI", "No");
                publish(Misc.showConfirmDialog(getRootPane(), title, message, confirmButtonText,
                    cancelButtonText));
            }
        });
    }

    private void publish(final boolean isPrintJob) {

        if (isReadyToPublish()) {

            // remove any empty segment areas
            removeEmptySegmentAreas();

            // save template file
            Main.getInstance().save(formFrame, false);

            // get template byte array
            File templateFile = formFrame.getGraph().getDocumentPackage().getPackageFile();

            String workingDirName = formFrame.getGraph().getDocumentPackage().getWorkingDirName();

            // clear the parent graph's recognition structure
            formFrame.getGraph().setFormRecognitionStructure(new FormRecognitionStructure());

            byte[] templateFileData;
            try {
                templateFileData = Misc.getBytesFromFile(templateFile);
            } catch (IOException e1) {
                String message = Localizer.localize("UI", "UnableToPublishMessage");
                String caption = Localizer.localize("UI", "ErrorTitle");
                javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
                    javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            saveRecognitionStructure();

            final FormPublisher formPublisher =
                new FormPublisher(getPublicationName(), formFrame.getDocument(),
                    getPublicationRecognitionStructure(), getSelectedDataSetIds()[0],
                    templateFileData, workingDirName);

            final ProcessingStatusDialog publishStatusDialog =
                new ProcessingStatusDialog(Main.getInstance());

            class PublishRunner implements Runnable {

                public void run() {

                    try {
                        formPublisher.setCollation(collatePDFCheckBox.isSelected() ?
                            FormPublisher.COLLATED_FORMS :
                            FormPublisher.SEPARATED_FORMS);
                        formPublisher.setScannedInOrder(scannedInOrderCheckBox.isSelected());
                        formPublisher.setPublishStatusDialog(publishStatusDialog);
                        formPublisher
                            .setPublicationType((publicationTypeComboBox.getSelectedIndex() + 1));
                        formPublisher.createForms(formFrame.getDocument(), publishStatusDialog);
                        formPublisher.exportPublication();
                        if (formPublisher.getCollation() == FormPublisher.COLLATED_FORMS
                            || isPrintJob) {
                            final File pdfFile = getPDFFile(formPublisher.getPublicationId());
                            formPublisher.collateToPDF(pdfFile);

                            if (isPrintJob) {

                                if (publishStatusDialog != null) {
                                    publishStatusDialog.dispose();
                                }
                                try {
                                    Misc.printPDF(pdfFile);
                                } catch (Exception ex) {
                                    Misc.printStackTrace(ex);
                                    Misc.showErrorMsg(getRootPane(), ex.getLocalizedMessage());
                                }

                            } else {
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        String message = Localizer
                                            .localize("UI", "FormPublisherPDFSaveSuccessMessage");
                                        String caption = Localizer
                                            .localize("UI", "FormPublisherPDFSaveSuccessTitle");
                                        javax.swing.JOptionPane
                                            .showConfirmDialog(Main.getInstance(), message, caption,
                                                javax.swing.JOptionPane.DEFAULT_OPTION,
                                                javax.swing.JOptionPane.INFORMATION_MESSAGE);
                                    }
                                });
                            }

                        } else {
                            formPublisher.individualPDF(getPDFDirectory());
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    String message = Localizer
                                        .localize("UI", "FormPublisherPDFSaveSuccessMessage");
                                    String caption = Localizer
                                        .localize("UI", "FormPublisherPDFSaveSuccessTitle");
                                    javax.swing.JOptionPane
                                        .showConfirmDialog(Main.getInstance(), message, caption,
                                            javax.swing.JOptionPane.DEFAULT_OPTION,
                                            javax.swing.JOptionPane.INFORMATION_MESSAGE);
                                }
                            });
                        }
                    } catch (final FormPublisherException fpe) {
                        com.ebstrada.formreturn.manager.util.Misc.printStackTrace(fpe);
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                Misc.showErrorMsg(Main.getInstance(), fpe.getErrorTitle());
                            }
                        });
                    } catch (final Exception ex) {
                        com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                Misc.showErrorMsg(Main.getInstance(), ex.getMessage());
                            }
                        });
                    } finally {
                        if (formPublisher != null) {
                            formPublisher.removeWorkingFiles();
                        }
                        if (publishStatusDialog != null) {
                            publishStatusDialog.dispose();
                        }
                        Globals.curEditor(formFrame.getGraph().getEditor());
                        formFrame.addFigsToActiveLayer();
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                restorePublications();
                            }
                        });
                    }
                }
            }
            ;

            PublishRunner publishRunner = new PublishRunner();
            Thread thread = new Thread(publishRunner);
            thread.start();

            publishStatusDialog.setModal(true);
            publishStatusDialog.setVisible(true);

            if (publishStatusDialog != null) {
                publishStatusDialog.dispose();
            }

        }

    }

    public File getPDFFile(long publicationId) {

        File file = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();
        filter.addExtension("pdf");

        FileDialog fd = new FileDialog(Main.getInstance(),
            Localizer.localize("UI", "FormPublisherSavePDFFileDialogTitle"), FileDialog.SAVE);
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

        fd.setFile(String
            .format(Localizer.localize("UI", "FormPublisherPDFExportAllFileNamePrefix"),
                publicationId) + ".pdf");

        try {
            fd.setDirectory(lastDir.getCanonicalPath());
        } catch (IOException e1) {
            fd.setDirectory(".");
        }

        fd.setModal(true);
        fd.setVisible(true);
        if (fd.getFile() != null) {

            String filename = fd.getFile();
            if (!(filename.endsWith(".pdf") || filename.endsWith(".PDF"))) {
                filename += ".pdf";
            }
            file = new File(fd.getDirectory() + filename);
            if (file.isDirectory()) {
                return null;
            }

        }

        return file;

    }

    public File getPDFDirectory() {
        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileFilter() {

            @Override public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override public String getDescription() {
                return null;
            }

        };
        chooser.setFileFilter(filter);

        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle(
            Localizer.localize("UI", "FormPublisherSelectPublicationExportDirectoryDialogTitle"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        chooser.rescanCurrentDirectory();

        if (chooser.showOpenDialog(Main.getInstance()) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else {
            return null;
        }

    }

    public void exportPublishedForms(long publicationId, final int collation,
        final boolean isPrintJob) {

        final ProcessingStatusDialog publishStatusDialog =
            new ProcessingStatusDialog(Main.getInstance());

        final FormPublisher formPublisher = new FormPublisher(publicationId);

        class PublishRunner implements Runnable {

            public void run() {

                int useCollation = collation;

                if (collation == FormPublisher.SEPARATED_FORMS && isPrintJob) {
                    useCollation = FormPublisher.COLLATED_FORMS;
                }

                try {
                    formPublisher.setCollation(useCollation);
                    formPublisher.setPublishStatusDialog(publishStatusDialog);
                    formPublisher
                        .setPublicationType((publicationTypeComboBox.getSelectedIndex() + 1));
                    formPublisher.exportPublication();
                    if (formPublisher.getCollation() == FormPublisher.COLLATED_FORMS
                        || isPrintJob) {
                        final File pdfFile = getPDFFile(formPublisher.getPublicationId());
                        formPublisher.collateToPDF(pdfFile);

                        if (isPrintJob) {

                            if (publishStatusDialog != null) {
                                publishStatusDialog.dispose();
                            }
                            try {
                                Misc.printPDF(pdfFile);
                            } catch (Exception ex) {
                                Misc.printStackTrace(ex);
                                Misc.showErrorMsg(getRootPane(), ex.getLocalizedMessage());
                            }

                        } else {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    String message = Localizer
                                        .localize("UI", "FormPublisherPDFSaveSuccessMessage");
                                    String caption = Localizer
                                        .localize("UI", "FormPublisherPDFSaveSuccessTitle");
                                    javax.swing.JOptionPane
                                        .showConfirmDialog(Main.getInstance(), message, caption,
                                            javax.swing.JOptionPane.DEFAULT_OPTION,
                                            javax.swing.JOptionPane.INFORMATION_MESSAGE);
                                }
                            });
                        }

                    } else {
                        formPublisher.individualPDF(getPDFDirectory());
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                String message =
                                    Localizer.localize("UI", "FormPublisherPDFSaveSuccessMessage");
                                String caption =
                                    Localizer.localize("UI", "FormPublisherPDFSaveSuccessTitle");
                                javax.swing.JOptionPane
                                    .showConfirmDialog(Main.getInstance(), message, caption,
                                        javax.swing.JOptionPane.DEFAULT_OPTION,
                                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                            }
                        });
                    }
                } catch (final FormPublisherException fpe) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(fpe);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            Misc.showErrorMsg(Main.getInstance(), fpe.getErrorTitle());
                        }
                    });
                } catch (final Exception ex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            Misc.showErrorMsg(Main.getInstance(), ex.getMessage());
                        }
                    });
                } finally {
                    if (formPublisher != null) {
                        formPublisher.removeWorkingFiles();
                    }
                    if (publishStatusDialog != null) {
                        publishStatusDialog.dispose();
                    }
                    Globals.curEditor(formFrame.getGraph().getEditor());
                    formFrame.addFigsToActiveLayer();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            restorePublications();
                        }
                    });
                }

            }
        }
        ;

        PublishRunner publishRunner = new PublishRunner();
        Thread thread = new Thread(publishRunner);
        thread.start();

        publishStatusDialog.setModal(true);
        publishStatusDialog.setVisible(true);

        if (publishStatusDialog != null) {
            publishStatusDialog.dispose();
        }

    }

    public void setPublicationRecognitionStructure(
        PublicationRecognitionStructure publicationRecognitionStructure) {
        this.publicationRecognitionStructure = publicationRecognitionStructure;
    }

    public void restorePublicationRecognitionStructure() {

        PublicationRecognitionStructure publicationRecognitionStructure =
            formFrame.getPublicationRecognitionStructure();

        luminanceSpinner.setValue(publicationRecognitionStructure.getLuminanceCutOff());
        markThresholdSpinner.setValue(publicationRecognitionStructure.getMarkThreshold());
        fragmentPaddingSpinner.setValue(publicationRecognitionStructure.getFragmentPadding());
        deskewThresholdSpinner.setValue(publicationRecognitionStructure.getDeskewThreshold());
        performDeskewCheckBox.setSelected(publicationRecognitionStructure.isPerformDeskew());

        setPublicationRecognitionStructure(publicationRecognitionStructure);

    }

    public void saveRecognitionStructure() {
        PublicationRecognitionStructure publicationRecognitionStructure =
            getPublicationRecognitionStructure();
        publicationRecognitionStructure
            .setDeskewThreshold((Double) deskewThresholdSpinner.getValue());
        publicationRecognitionStructure.setLuminanceCutOff((Integer) luminanceSpinner.getValue());
        publicationRecognitionStructure.setMarkThreshold((Integer) markThresholdSpinner.getValue());
        publicationRecognitionStructure
            .setFragmentPadding((Integer) fragmentPaddingSpinner.getValue());
        publicationRecognitionStructure.setPerformDeskew(performDeskewCheckBox.isSelected());
    }

    public PublicationRecognitionStructure getPublicationRecognitionStructure() {
        return publicationRecognitionStructure;
    }

    private boolean isReadyToPublish() {

        if (!checkFormIds()) {
            String title = Localizer.localize("UI", "WarningTitle");
            String message = Localizer.localize("UI", "FormIDsNotFoundMessage");
            String confirmButtonText = Localizer.localize("UI", "IgnoreButtonText");
            String cancelButtonText = Localizer.localize("UI", "CancelButtonText");

            boolean result =
                Misc.showConfirmDialog(Main.getInstance(), title, message, confirmButtonText,
                    cancelButtonText);

            if (result == false) {
                return false;
            }
        }

        if (publicationNameTextField.getText().trim().length() <= 0
            || publicationNameTextField.getText().trim().length() > 255) {
            String msg = Localizer.localize("UI", "MustEnterPublicationNameMessage");
            Misc.showErrorMsg(Main.getInstance(), msg);
            return false;
        }

        if (tablesTable.getSelectedRow() == -1) {
            String msg = Localizer.localize("UI", "MustSelectTableToPublishMessage");
            Misc.showErrorMsg(Main.getInstance(), msg);
            return false;
        }

        if (formFrame.getGraph().getEditor().hasEditorStateChanged() == false) {
            return true;
        }

        Object[] options = {Localizer.localize("UI", "Yes"), Localizer.localize("UI", "No")};

        String filename = formFrame.getTitle();
        if (formFrame.getGraph().getDocumentPackage().getPackageFile() != null) {
            filename = formFrame.getGraph().getDocumentPackage().getPackageFile().getName();
        }

        String msg = String.format(Localizer.localize("UI", "SaveFormAndPublishOption"), filename);

        int result = JOptionPane.showOptionDialog(Main.getInstance(), msg,
            Localizer.localize("UI", "ProceedWithPublicationTitle"), JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (result == 0) {
            return true;
        } else {
            return false;
        }

    }

    private String getPublicationName() {
        return publicationNameTextField.getText().trim();
    }

    private void thisComponentResized(ComponentEvent e) {
        resetVerticalDivider();
    }

    private void backupHelpLabelMouseClicked(MouseEvent e) {
        Misc.openURL("file://" + Misc.getHelpDirectory() + "/?topic=backup-restore");
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        verticalSplitPane = new JSplitPane();
        tablesPanel = new JPanel();
        scrollPane4 = new JScrollPane();
        tablesTable = new JTable();
        tablesTableFilterPanel = new TableFilterPanel();
        publicationsPanel = new JPanel();
        scrollPane5 = new JScrollPane();
        publicationsTable = new JTable();
        publicationsTableFilterPanel = new TableFilterPanel();
        publishScrollPane = new JScrollPane();
        panel4 = new JPanel();
        tabbedPane1 = new JTabbedPane();
        publishTabPanel = new JPanel();
        step1Panel = new JPanel();
        publicationNameTextField = new JTextField();
        step2Panel = new JPanel();
        selectSourceDataDescriptionLabel = new JLabel();
        panel6 = new JPanel();
        selectedTableTextField = new JTextField();
        step3Panel = new JPanel();
        publicationComboBoxPanel = new JPanel();
        publicationTypeComboBox = new JComboBox();
        publicationTypeHelpLabel = new JHelpLabel();
        step4Panel = new JPanel();
        publishButtonPanel = new JPanel();
        publishPDFButton = new JButton();
        backupNoticeLabel = new JLabel();
        backupHelpLabel = new JLabel();
        settingsTabPanel = new JPanel();
        pdfSettingsPanel = new JPanel();
        collateFormsPanel = new JPanel();
        collatePDFCheckBox = new JCheckBox();
        recognitionSettingsPanel = new JPanel();
        lmfdPanel = new JPanel();
        luminanceLabel = new JLabel();
        luminanceSpinner = new JSpinner();
        markThresholdLabel = new JLabel();
        markThresholdSpinner = new JSpinner();
        fragmentPaddingLabel = new JLabel();
        fragmentPaddingSpinner = new JSpinner();
        deskewThresholdLabel = new JLabel();
        deskewThresholdSpinner = new JSpinner();
        deskewPanel = new JPanel();
        performDeskewCheckBox = new JCheckBox();
        advancedLabel = new JLabel();
        scannedInOrderPanel = new JPanel();
        scannedInOrderSubPanel = new JPanel();
        scannedInOrderCheckBox = new JCheckBox();
        scannedInOrderHelpLabel = new JHelpLabel();

        //======== this ========
        setOpaque(false);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                thisComponentResized(e);
            }
        });

        setLayout(new BorderLayout());

        //======== verticalSplitPane ========
        {
            verticalSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            verticalSplitPane.setOpaque(false);
            verticalSplitPane.setBorder(null);
            verticalSplitPane.setDividerSize(9);

            //======== tablesPanel ========
            {
                tablesPanel.setOpaque(false);
                tablesPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)tablesPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)tablesPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout)tablesPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)tablesPanel.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};
                tablesPanel.setBorder(new CompoundBorder(
                    new TitledBorder(Localizer.localize("UI", "PublishFormTablesBorderTitle")),
                    new EmptyBorder(5, 5, 5, 5)));

                //======== scrollPane4 ========
                {

                    //---- tablesTable ----
                    tablesTable.setShowHorizontalLines(false);
                    tablesTable.setShowVerticalLines(false);
                    tablesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    tablesTable.setFont(UIManager.getFont("Table.font"));
                    tablesTable.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));
                    scrollPane4.setViewportView(tablesTable);
                }
                tablesPanel.add(scrollPane4, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
                tablesPanel.add(tablesTableFilterPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            verticalSplitPane.setTopComponent(tablesPanel);

            //======== publicationsPanel ========
            {
                publicationsPanel.setOpaque(false);
                publicationsPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)publicationsPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)publicationsPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout)publicationsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)publicationsPanel.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};
                publicationsPanel.setBorder(new CompoundBorder(
                    new TitledBorder(Localizer.localize("UI", "PublishFormPublicationsBorderTitle")),
                    new EmptyBorder(5, 5, 5, 5)));

                //======== scrollPane5 ========
                {

                    //---- publicationsTable ----
                    publicationsTable.setShowHorizontalLines(false);
                    publicationsTable.setShowVerticalLines(false);
                    publicationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    publicationsTable.setFont(UIManager.getFont("Table.font"));
                    publicationsTable.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));
                    scrollPane5.setViewportView(publicationsTable);
                }
                publicationsPanel.add(scrollPane5, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
                publicationsPanel.add(publicationsTableFilterPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            verticalSplitPane.setBottomComponent(publicationsPanel);
        }
        add(verticalSplitPane, BorderLayout.CENTER);

        //======== publishScrollPane ========
        {
            publishScrollPane.setBorder(null);
            publishScrollPane.setBackground(null);
            publishScrollPane.setOpaque(false);
            publishScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

            //======== panel4 ========
            {
                panel4.setOpaque(false);
                panel4.setLayout(new GridBagLayout());
                ((GridBagLayout)panel4.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)panel4.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)panel4.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)panel4.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                //======== tabbedPane1 ========
                {
                    tabbedPane1.setFont(UIManager.getFont("TabbedPane.font"));

                    //======== publishTabPanel ========
                    {
                        publishTabPanel.setOpaque(false);
                        publishTabPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
                        publishTabPanel.setFont(UIManager.getFont("Panel.font"));
                        publishTabPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)publishTabPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)publishTabPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0};
                        ((GridBagLayout)publishTabPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)publishTabPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                        //======== step1Panel ========
                        {
                            step1Panel.setOpaque(false);
                            step1Panel.setLayout(new GridBagLayout());
                            ((GridBagLayout)step1Panel.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout)step1Panel.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)step1Panel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                            ((GridBagLayout)step1Panel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};
                            step1Panel.setBorder(new CompoundBorder(
                                new TitledBorder(Localizer.localize("UI", "Step1Panel")),
                                new EmptyBorder(5, 5, 5, 5)));

                            //---- publicationNameTextField ----
                            publicationNameTextField.setFont(UIManager.getFont("TextField.font"));
                            step1Panel.add(publicationNameTextField, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        publishTabPanel.add(step1Panel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== step2Panel ========
                        {
                            step2Panel.setOpaque(false);
                            step2Panel.setLayout(new GridBagLayout());
                            ((GridBagLayout)step2Panel.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout)step2Panel.getLayout()).rowHeights = new int[] {35, 0, 0};
                            ((GridBagLayout)step2Panel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                            ((GridBagLayout)step2Panel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};
                            step2Panel.setBorder(new CompoundBorder(
                                new TitledBorder(Localizer.localize("UI", "Step2Panel")),
                                new EmptyBorder(5, 5, 5, 5)));

                            //---- selectSourceDataDescriptionLabel ----
                            selectSourceDataDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                            selectSourceDataDescriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
                            selectSourceDataDescriptionLabel.setText(Localizer.localize("UI", "SelectSourceDataTableDescription"));
                            step2Panel.add(selectSourceDataDescriptionLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 0), 0, 0));

                            //======== panel6 ========
                            {
                                panel6.setOpaque(false);
                                panel6.setLayout(new GridBagLayout());
                                ((GridBagLayout)panel6.getLayout()).columnWidths = new int[] {0, 0};
                                ((GridBagLayout)panel6.getLayout()).rowHeights = new int[] {0, 0};
                                ((GridBagLayout)panel6.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                                ((GridBagLayout)panel6.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                                //---- selectedTableTextField ----
                                selectedTableTextField.setEnabled(false);
                                selectedTableTextField.setFont(UIManager.getFont("TextField.font"));
                                panel6.add(selectedTableTextField, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
                            }
                            step2Panel.add(panel6, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        publishTabPanel.add(step2Panel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== step3Panel ========
                        {
                            step3Panel.setOpaque(false);
                            step3Panel.setLayout(new GridBagLayout());
                            ((GridBagLayout)step3Panel.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout)step3Panel.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)step3Panel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                            ((GridBagLayout)step3Panel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};
                            step3Panel.setBorder(new CompoundBorder(
                                new TitledBorder(Localizer.localize("UI", "Step3Panel")),
                                new EmptyBorder(5, 5, 5, 5)));

                            //======== publicationComboBoxPanel ========
                            {
                                publicationComboBoxPanel.setOpaque(false);
                                publicationComboBoxPanel.setLayout(new GridBagLayout());
                                ((GridBagLayout)publicationComboBoxPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
                                ((GridBagLayout)publicationComboBoxPanel.getLayout()).rowHeights = new int[] {0, 0};
                                ((GridBagLayout)publicationComboBoxPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                                ((GridBagLayout)publicationComboBoxPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                                //---- publicationTypeComboBox ----
                                publicationTypeComboBox.setPrototypeDisplayValue("xxxxxxxx");
                                publicationTypeComboBox.setMinimumSize(new Dimension(50, 20));
                                publicationTypeComboBox.setPreferredSize(new Dimension(30, 22));
                                publicationTypeComboBox.setFont(UIManager.getFont("ComboBox.font"));
                                publicationComboBoxPanel.add(publicationTypeComboBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));

                                //---- publicationTypeHelpLabel ----
                                publicationTypeHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                                publicationTypeHelpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                publicationTypeHelpLabel.setHelpGUID("publication-types");
                                publicationTypeHelpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                                publicationComboBoxPanel.add(publicationTypeHelpLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
                            }
                            step3Panel.add(publicationComboBoxPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        publishTabPanel.add(step3Panel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== step4Panel ========
                        {
                            step4Panel.setOpaque(false);
                            step4Panel.setLayout(new GridBagLayout());
                            ((GridBagLayout)step4Panel.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout)step4Panel.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)step4Panel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                            ((GridBagLayout)step4Panel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};
                            step4Panel.setBorder(new CompoundBorder(
                                new TitledBorder(Localizer.localize("UI", "Step4Panel")),
                                new EmptyBorder(5, 5, 5, 5)));

                            //======== publishButtonPanel ========
                            {
                                publishButtonPanel.setOpaque(false);
                                publishButtonPanel.setLayout(new GridBagLayout());
                                ((GridBagLayout)publishButtonPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                                ((GridBagLayout)publishButtonPanel.getLayout()).rowHeights = new int[] {0, 0};
                                ((GridBagLayout)publishButtonPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0, 1.0E-4};
                                ((GridBagLayout)publishButtonPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                                //---- publishPDFButton ----
                                publishPDFButton.setFont(UIManager.getFont("Button.font"));
                                publishPDFButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/accept.png")));
                                publishPDFButton.setFocusPainted(false);
                                publishPDFButton.setMargin(new Insets(8, 12, 8, 12));
                                publishPDFButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        publishPDFButtonActionPerformed(e);
                                    }
                                });
                                publishPDFButton.setText(Localizer.localize("UI", "PublishFormPDFButtonText"));
                                publishButtonPanel.add(publishPDFButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                                    new Insets(0, 0, 0, 5), 0, 0));
                            }
                            step4Panel.add(publishButtonPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        publishTabPanel.add(step4Panel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- backupNoticeLabel ----
                        backupNoticeLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        backupNoticeLabel.setFont(UIManager.getFont("Label.font"));
                        backupNoticeLabel.setBorder(new EmptyBorder(0, 10, 0, 10));
                        backupNoticeLabel.setText(Localizer.localize("UI", "BackupNoticeLabelText"));
                        publishTabPanel.add(backupNoticeLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- backupHelpLabel ----
                        backupHelpLabel.setText("<html><p><a href=\"\">Click here for more help</a></p></html>");
                        backupHelpLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                        backupHelpLabel.setHorizontalAlignment(SwingConstants.LEFT);
                        backupHelpLabel.setFont(UIManager.getFont("Label.font"));
                        backupHelpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        backupHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                        backupHelpLabel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                backupHelpLabelMouseClicked(e);
                            }
                        });
                        backupHelpLabel.setText(Localizer.localize("HelpLabel", "f9aa1ec6-1f13-45b8-bc4c-cc28c659377f"));
                        publishTabPanel.add(backupHelpLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    tabbedPane1.addTab("Publish", publishTabPanel);

                    //======== settingsTabPanel ========
                    {
                        settingsTabPanel.setOpaque(false);
                        settingsTabPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
                        settingsTabPanel.setFont(UIManager.getFont("Panel.font"));
                        settingsTabPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)settingsTabPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)settingsTabPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
                        ((GridBagLayout)settingsTabPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)settingsTabPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};

                        //======== pdfSettingsPanel ========
                        {
                            pdfSettingsPanel.setBorder(new CompoundBorder(
                                new TitledBorder("PDF Settings"),
                                new EmptyBorder(5, 5, 5, 5)));
                            pdfSettingsPanel.setOpaque(false);
                            pdfSettingsPanel.setLayout(new BorderLayout());
                            pdfSettingsPanel.setBorder(new CompoundBorder(
                                new TitledBorder(Localizer.localize("UI", "PDFSettingsPanel")),
                                new EmptyBorder(5, 5, 5, 5)));

                            //======== collateFormsPanel ========
                            {
                                collateFormsPanel.setOpaque(false);
                                collateFormsPanel.setLayout(new GridBagLayout());
                                ((GridBagLayout)collateFormsPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                                ((GridBagLayout)collateFormsPanel.getLayout()).rowHeights = new int[] {0, 0};
                                ((GridBagLayout)collateFormsPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0, 1.0E-4};
                                ((GridBagLayout)collateFormsPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                                //---- collatePDFCheckBox ----
                                collatePDFCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                                collatePDFCheckBox.setFocusPainted(false);
                                collatePDFCheckBox.setSelected(true);
                                collatePDFCheckBox.setOpaque(false);
                                collatePDFCheckBox.setText(Localizer.localize("UI", "PublishFormCollatePDFCheckBox"));
                                collateFormsPanel.add(collatePDFCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));
                            }
                            pdfSettingsPanel.add(collateFormsPanel, BorderLayout.CENTER);
                        }
                        settingsTabPanel.add(pdfSettingsPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== recognitionSettingsPanel ========
                        {
                            recognitionSettingsPanel.setBorder(new CompoundBorder(
                                new TitledBorder("Recognition Settings"),
                                new EmptyBorder(0, 5, 0, 5)));
                            recognitionSettingsPanel.setOpaque(false);
                            recognitionSettingsPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)recognitionSettingsPanel.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout)recognitionSettingsPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                            ((GridBagLayout)recognitionSettingsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                            ((GridBagLayout)recognitionSettingsPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};
                            recognitionSettingsPanel.setBorder(new CompoundBorder(
                                new TitledBorder(Localizer.localize("UI", "PublishRecognitionSettingsPanel")),
                                new EmptyBorder(5, 5, 5, 5)));

                            //======== lmfdPanel ========
                            {
                                lmfdPanel.setOpaque(false);
                                lmfdPanel.setBorder(new CompoundBorder(
                                    new MatteBorder(0, 0, 1, 0, Color.lightGray),
                                    new EmptyBorder(0, 0, 5, 0)));
                                lmfdPanel.setLayout(new GridBagLayout());
                                ((GridBagLayout)lmfdPanel.getLayout()).columnWidths = new int[] {0, 85, 0, 0};
                                ((GridBagLayout)lmfdPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
                                ((GridBagLayout)lmfdPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0, 1.0E-4};
                                ((GridBagLayout)lmfdPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};

                                //---- luminanceLabel ----
                                luminanceLabel.setFont(UIManager.getFont("Label.font"));
                                luminanceLabel.setText(Localizer.localize("UI", "PublishFormLuminanceLabel"));
                                lmfdPanel.add(luminanceLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                                    new Insets(0, 0, 2, 5), 0, 0));

                                //---- luminanceSpinner ----
                                luminanceSpinner.setFont(UIManager.getFont("Spinner.font"));
                                luminanceSpinner.setModel(new SpinnerNumberModel(200, 0, 255, 1));
                                lmfdPanel.add(luminanceSpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 2, 5), 0, 0));

                                //---- markThresholdLabel ----
                                markThresholdLabel.setFont(UIManager.getFont("Label.font"));
                                markThresholdLabel.setText(Localizer.localize("UI", "PublishFormMarkThresholdLabel"));
                                lmfdPanel.add(markThresholdLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                                    new Insets(0, 0, 2, 5), 0, 0));

                                //---- markThresholdSpinner ----
                                markThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
                                markThresholdSpinner.setModel(new SpinnerNumberModel(40, -10000, 10000, 1));
                                lmfdPanel.add(markThresholdSpinner, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 2, 5), 0, 0));

                                //---- fragmentPaddingLabel ----
                                fragmentPaddingLabel.setFont(UIManager.getFont("Label.font"));
                                fragmentPaddingLabel.setText(Localizer.localize("UI", "PublishFormFragmentPaddingLabel"));
                                lmfdPanel.add(fragmentPaddingLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                                    new Insets(0, 0, 2, 5), 0, 0));

                                //---- fragmentPaddingSpinner ----
                                fragmentPaddingSpinner.setFont(UIManager.getFont("Spinner.font"));
                                fragmentPaddingSpinner.setModel(new SpinnerNumberModel(1, 0, 200, 1));
                                lmfdPanel.add(fragmentPaddingSpinner, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 2, 5), 0, 0));

                                //---- deskewThresholdLabel ----
                                deskewThresholdLabel.setFont(UIManager.getFont("Label.font"));
                                deskewThresholdLabel.setText(Localizer.localize("UI", "PublishFormDeskewThresholdLabel"));
                                lmfdPanel.add(deskewThresholdLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                                    new Insets(0, 0, 0, 5), 0, 0));

                                //---- deskewThresholdSpinner ----
                                deskewThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
                                deskewThresholdSpinner.setModel(new SpinnerNumberModel(1.05, 0.0, 90.0, 0.01));
                                lmfdPanel.add(deskewThresholdSpinner, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));
                            }
                            recognitionSettingsPanel.add(lmfdPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 0), 0, 0));

                            //======== deskewPanel ========
                            {
                                deskewPanel.setOpaque(false);
                                deskewPanel.setBorder(null);
                                deskewPanel.setLayout(new GridBagLayout());
                                ((GridBagLayout)deskewPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                                ((GridBagLayout)deskewPanel.getLayout()).rowHeights = new int[] {0, 0};
                                ((GridBagLayout)deskewPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0, 1.0E-4};
                                ((GridBagLayout)deskewPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                                //---- performDeskewCheckBox ----
                                performDeskewCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                                performDeskewCheckBox.setFocusPainted(false);
                                performDeskewCheckBox.setSelected(true);
                                performDeskewCheckBox.setOpaque(false);
                                performDeskewCheckBox.setText(Localizer.localize("UI", "PublishFormPerformDeskewCheckBox"));
                                deskewPanel.add(performDeskewCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                                    new Insets(0, 0, 0, 5), 0, 0));
                            }
                            recognitionSettingsPanel.add(deskewPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        settingsTabPanel.add(recognitionSettingsPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- advancedLabel ----
                        advancedLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
                        advancedLabel.setFont(UIManager.getFont("Label.font"));
                        advancedLabel.setText(Localizer.localize("UI", "AdvancedTitle"));
                        settingsTabPanel.add(advancedLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== scannedInOrderPanel ========
                        {
                            scannedInOrderPanel.setBorder(new CompoundBorder(
                                new TitledBorder("Reconcilation Key On First Page"),
                                new EmptyBorder(5, 5, 5, 5)));
                            scannedInOrderPanel.setOpaque(false);
                            scannedInOrderPanel.setLayout(new BorderLayout());
                            scannedInOrderPanel.setBorder(new CompoundBorder(
                                new TitledBorder(Localizer.localize("UI", "ScannedInOrderPanel")),
                                new EmptyBorder(5, 5, 5, 5)));

                            //======== scannedInOrderSubPanel ========
                            {
                                scannedInOrderSubPanel.setOpaque(false);
                                scannedInOrderSubPanel.setLayout(new GridBagLayout());
                                ((GridBagLayout)scannedInOrderSubPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                                ((GridBagLayout)scannedInOrderSubPanel.getLayout()).rowHeights = new int[] {0, 0};
                                ((GridBagLayout)scannedInOrderSubPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                                ((GridBagLayout)scannedInOrderSubPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                                //---- scannedInOrderCheckBox ----
                                scannedInOrderCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                                scannedInOrderCheckBox.setText(Localizer.localize("UI", "ScannedInOrderLabelText"));
                                scannedInOrderSubPanel.add(scannedInOrderCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));

                                //---- scannedInOrderHelpLabel ----
                                scannedInOrderHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                                scannedInOrderHelpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                scannedInOrderHelpLabel.setHelpGUID("reconciliation-key-on-first-page");
                                scannedInOrderHelpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                                scannedInOrderSubPanel.add(scannedInOrderHelpLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));
                            }
                            scannedInOrderPanel.add(scannedInOrderSubPanel, BorderLayout.NORTH);
                        }
                        settingsTabPanel.add(scannedInOrderPanel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    tabbedPane1.addTab("Settings", settingsTabPanel);
                }
                panel4.add(tabbedPane1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            publishScrollPane.setViewportView(panel4);
        }
        add(publishScrollPane, BorderLayout.EAST);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JSplitPane verticalSplitPane;
    private JPanel tablesPanel;
    private JScrollPane scrollPane4;
    private JTable tablesTable;
    private TableFilterPanel tablesTableFilterPanel;
    private JPanel publicationsPanel;
    private JScrollPane scrollPane5;
    private JTable publicationsTable;
    private TableFilterPanel publicationsTableFilterPanel;
    private JScrollPane publishScrollPane;
    private JPanel panel4;
    private JTabbedPane tabbedPane1;
    private JPanel publishTabPanel;
    private JPanel step1Panel;
    private JTextField publicationNameTextField;
    private JPanel step2Panel;
    private JLabel selectSourceDataDescriptionLabel;
    private JPanel panel6;
    private JTextField selectedTableTextField;
    private JPanel step3Panel;
    private JPanel publicationComboBoxPanel;
    private JComboBox publicationTypeComboBox;
    private JHelpLabel publicationTypeHelpLabel;
    private JPanel step4Panel;
    private JPanel publishButtonPanel;
    private JButton publishPDFButton;
    private JLabel backupNoticeLabel;
    private JLabel backupHelpLabel;
    private JPanel settingsTabPanel;
    private JPanel pdfSettingsPanel;
    private JPanel collateFormsPanel;
    private JCheckBox collatePDFCheckBox;
    private JPanel recognitionSettingsPanel;
    private JPanel lmfdPanel;
    private JLabel luminanceLabel;
    private JSpinner luminanceSpinner;
    private JLabel markThresholdLabel;
    private JSpinner markThresholdSpinner;
    private JLabel fragmentPaddingLabel;
    private JSpinner fragmentPaddingSpinner;
    private JLabel deskewThresholdLabel;
    private JSpinner deskewThresholdSpinner;
    private JPanel deskewPanel;
    private JCheckBox performDeskewCheckBox;
    private JLabel advancedLabel;
    private JPanel scannedInOrderPanel;
    private JPanel scannedInOrderSubPanel;
    private JCheckBox scannedInOrderCheckBox;
    private JHelpLabel scannedInOrderHelpLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
