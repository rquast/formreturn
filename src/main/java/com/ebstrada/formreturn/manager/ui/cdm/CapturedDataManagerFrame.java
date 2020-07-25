package com.ebstrada.formreturn.manager.ui.cdm;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.TableModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import au.com.bytecode.opencsv.CSVWriter;

import com.ebstrada.formreturn.api.messaging.MessageNotification;
import com.ebstrada.formreturn.api.messaging.ProcessingStatusDialog;
import com.ebstrada.formreturn.manager.ui.component.*;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.commons.lang.ArrayUtils;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.export.ExportOptions;
import com.ebstrada.formreturn.manager.logic.export.csv.CSVExporter;
import com.ebstrada.formreturn.manager.logic.export.filter.Filter;
import com.ebstrada.formreturn.manager.logic.export.image.ImageExporter;
import com.ebstrada.formreturn.manager.logic.export.xml.XMLExporter;
import com.ebstrada.formreturn.manager.logic.jpa.PublicationController;
import com.ebstrada.formreturn.manager.logic.publish.FormPublisher;
import com.ebstrada.formreturn.manager.logic.publish.FormPublisherException;
import com.ebstrada.formreturn.manager.persistence.jpa.CheckBox;
import com.ebstrada.formreturn.manager.persistence.jpa.Form;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentBarcode;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentOmr;
import com.ebstrada.formreturn.manager.persistence.jpa.ProcessedImage;
import com.ebstrada.formreturn.manager.persistence.jpa.PublicationXSL;
import com.ebstrada.formreturn.manager.persistence.jpa.Segment;
import com.ebstrada.formreturn.manager.persistence.viewer.GenericDataViewer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.cdm.dialog.ExportOptionsDialog;
import com.ebstrada.formreturn.manager.ui.cdm.dialog.ExtendPublicationDialog;
import com.ebstrada.formreturn.manager.ui.cdm.dialog.PublicationSettingsDialog;
import com.ebstrada.formreturn.manager.ui.cdm.dialog.RenamePublicationDialog;
import com.ebstrada.formreturn.manager.ui.cdm.model.FormDataModel;
import com.ebstrada.formreturn.manager.ui.cdm.model.FormPageDataModel;
import com.ebstrada.formreturn.manager.ui.cdm.model.PublicationDataModel;
import com.ebstrada.formreturn.manager.ui.cdm.panel.FormsPropertiesPanel;
import com.ebstrada.formreturn.manager.ui.cdm.panel.FormPagesPropertiesPanel;
import com.ebstrada.formreturn.manager.ui.cdm.panel.PublicationsPropertiesPanel;
import com.ebstrada.formreturn.manager.ui.dialog.ImagePreviewFrame;
import com.ebstrada.formreturn.manager.ui.dialog.LoadingDialog;
import com.ebstrada.formreturn.manager.ui.editor.persistence.XSLTemplate;
import com.ebstrada.formreturn.manager.ui.panel.PropertiesPanelController;
import com.ebstrada.formreturn.manager.ui.reprocessor.frame.ReprocessorFrame;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.persistence.CSVExportPreferences;
import com.ebstrada.formreturn.manager.util.preferences.persistence.PublicationPreferences;

public class CapturedDataManagerFrame extends JPanel implements GenericDataViewer {

    private static final long serialVersionUID = 1L;
    private PublicationsPropertiesPanel publicationsPropertiesPanel;
    private FormsPropertiesPanel formsPropertiesPanel;
    private FormPagesPropertiesPanel formPagesPropertiesPanel;

    private PublicationDataModel publicationDataModel;
    private FormDataModel formDataModel;
    private FormPageDataModel formPageDataModel;

    private List<Long> lastPublicationIds = new ArrayList<Long>();
    private List<Long> lastFormIds = new ArrayList<Long>();

    private SwingWorker<ImagePreviewPanel, Void> openWorker;

    private ZoomSettings zoomSettings = new ZoomSettings();

    public CapturedDataManagerFrame() {

        initComponents();

        publicationDataModel = new PublicationDataModel();
        publicationsFilterPanel.setTableModel(publicationDataModel);
        publicationsFilterPanel.setTableViewer(this);

        formDataModel = new FormDataModel();
        formsFilterPanel.setTableModel(formDataModel);
        formsFilterPanel.setTableViewer(this);

        formPageDataModel = new FormPageDataModel();
        formPagesFilterPanel.setTableModel(formPageDataModel);
        formPagesFilterPanel.setTableViewer(this);

        refresh();

        SelectionListener publicationListener =
            new SelectionListener(publicationsTable, SelectionListener.PUBLICATION_SELECTION);
        publicationsTable.getSelectionModel().addListSelectionListener(publicationListener);

        SelectionListener formListener =
            new SelectionListener(formsTable, SelectionListener.FORM_SELECTION);
        formsTable.getSelectionModel().addListSelectionListener(formListener);

        SelectionListener formPageListener =
            new SelectionListener(formPagesTable, SelectionListener.FORM_PAGE_SELECTION);
        formPagesTable.getSelectionModel().addListSelectionListener(formPageListener);

        CDMTabbedPane.setTitleAt(0, Localizer.localize("UICDM", "PublicationsTabTitle"));
        CDMTabbedPane.setTitleAt(1, Localizer.localize("UICDM", "FormsTabTitle"));
        CDMTabbedPane.setTitleAt(2, Localizer.localize("UICDM", "FormPagesTabTitle"));

    }

    public void restorePublications() {
        publicationsTable.setModel(publicationDataModel.getTableModel());
        publicationsTable.getColumn("ID").setMaxWidth(150);
        publicationsTable.getTableHeader().setReorderingAllowed(false);
    }

    public void restoreForms() {
        formsTable.setModel(formDataModel.getTableModel());
        formsTable.getColumn("ID").setMaxWidth(150);
        formsTable.getTableHeader().setReorderingAllowed(false);
    }

    public void restoreFormPages() {
        formPagesTable.setModel(formPageDataModel.getTableModel());
        formPagesTable.getColumn("ID").setMaxWidth(150);
        formPagesTable.getTableHeader().setReorderingAllowed(false);
    }

    public void restoreCapturedData() {
        capturedDataTable.setModel(
            formPageDataModel.getCapturedDataModel(pageScoreTextField, formScoreTextField));
        capturedDataTable.getTableHeader().setReorderingAllowed(false);
    }

    public void clearFormPageImagePreview() {
        imagePreviewContainer.removeAll();
        imagePreviewContainer.revalidate();
        pageScoreTextField.setText("");
        formScoreTextField.setText("");
        repaint();
    }

    public void loadFormPageImagePreview() throws IOException {

        if (this.openWorker != null) {
            return;
        }

        openWorker = new SwingWorker<ImagePreviewPanel, Void>() {

            public ImagePreviewPanel doInBackground() {
                byte[] imageData = formPageDataModel.getImage();
                if (imageData != null) {
                    return new ImagePreviewPanel(imageData, false, zoomSettings);
                } else {
                    return null;
                }
            }

            public void done() {
                ImagePreviewPanel ipp = null;
                try {
                    ipp = get();
                    imagePreviewContainer.removeAll();
                    if (ipp != null) {
                        imagePreviewContainer.add(ipp, BorderLayout.CENTER);
                        ipp.revalidate();
                        repaint();
                    }
                } catch (InterruptedException e) {
                    imagePreviewContainer.removeAll();
                    closeWorker();
                    return;
                } catch (CancellationException e) {
                    imagePreviewContainer.removeAll();
                    closeWorker();
                    return;
                } catch (ExecutionException e) {
                    imagePreviewContainer.removeAll();
                    closeWorker();
                    return;
                }

                closeWorker();
            }

        };
        openWorker.execute();
        clearFormPageImagePreview();

    }

    public void closeWorker() {
        this.openWorker = null;
    }

    public void updateSelectedPublication() {
        if (publicationsTable.getSelectedRow() != -1) {
            updateSelectedPublicationIds(getSelectedPublicationIds());
        } else {
            updateSelectedPublicationIds(new long[] {});
        }
    }

    public void updateSelectedPublicationIds(long[] selectedIds) {
        publicationDataModel.setSelectedIds(selectedIds);
        if (selectedIds.length > 0) {
            formDataModel.setParentId(selectedIds[0]);
            lastPublicationIds = java.util.Arrays.asList(ArrayUtils.toObject(selectedIds));
        } else {
            formDataModel.setParentId(-1);
        }
    }

    public void updateSelectedForm() {
        if (formsTable.getSelectedRow() != -1) {
            updateSelectedFormIds(getSelectedFormIds());
        } else {
            updateSelectedFormIds(new long[] {});
        }
    }

    public void updateSelectedFormIds(long[] selectedIds) {
        formDataModel.setSelectedIds(selectedIds);
        if (selectedIds.length > 0) {
            formPageDataModel.setParentId(selectedIds[0]);
            lastFormIds = java.util.Arrays.asList(ArrayUtils.toObject(selectedIds));
        } else {
            formPageDataModel.setParentId(-1);
        }
    }

    public void updateSelectedFormPage() {
        if (formPagesTable.getSelectedRow() != -1) {
            updateSelectedFormPageIds(getSelectedFormPageIds());
            try {
                loadFormPageImagePreview();
            } catch (IOException e1) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
            }
            restoreCapturedData();
        } else {
            updateSelectedFormPageIds(new long[] {});
            clearFormPageImagePreview();
            restoreCapturedData();
        }
    }

    public void updateSelectedFormPageIds(long[] selectedIds) {
        // TODO: fix this code so it works the new way
        formPageDataModel.setSelectedIds(selectedIds);
    }

    public class SelectionListener implements ListSelectionListener {

        public static final int PUBLICATION_SELECTION = 0;
        public static final int FORM_SELECTION = 1;
        public static final int FORM_PAGE_SELECTION = 2;

        private int selection;

        private JTable table;

        // It is necessary to keep the table since it is not possible
        // to determine the table from the event's source
        SelectionListener(JTable table, int selection) {
            this.table = table;
            this.selection = selection;
        }

        public void valueChanged(ListSelectionEvent e) {
            // If cell selection is enabled, both row and column change events are fired
            if (e.getSource() == table.getSelectionModel() && table.getRowSelectionAllowed()
                && e.getValueIsAdjusting() == false) {
                // Column selection changed
                int first = e.getFirstIndex();
                int last = e.getLastIndex();
                if (selection == PUBLICATION_SELECTION) {
                    updateSelectedPublication();
                }
                if (selection == FORM_SELECTION) {
                    updateSelectedForm();
                }
                if (selection == FORM_PAGE_SELECTION) {
                    updateSelectedFormPage();
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

    public String getSelectedPublicationName() {

        if (publicationsTable.getSelectedRow() == -1) {
            return Localizer.localize("UICDM", "NoneSelected");
        }

        String publicationName =
            (String) publicationsTable.getValueAt(publicationsTable.getSelectedRow(), 1);
        if (publicationName == null) {
            return "";
        }
        return publicationName;

    }

    public long[] getSelectedFormPageIds() {
        int selectedFormPageRows[] = formPagesTable.getSelectedRows();
        long selectedFormPageIds[] = new long[] {};
        if (selectedFormPageRows.length > 0) {
            selectedFormPageIds = new long[selectedFormPageRows.length];
            for (int i = 0; i < selectedFormPageRows.length; i++) {
                selectedFormPageIds[i] =
                    Long.parseLong((String) formPagesTable.getValueAt(selectedFormPageRows[i], 0));
            }
        }
        return selectedFormPageIds;
    }

    public long[] getSelectedFormIds() {
        int selectedFormRows[] = formsTable.getSelectedRows();
        long selectedFormIds[] = new long[] {};
        if (selectedFormRows.length > 0) {
            selectedFormIds = new long[selectedFormRows.length];
            for (int i = 0; i < selectedFormRows.length; i++) {
                if (formsTable.getValueAt(selectedFormRows[i], 0) instanceof String) {
                    selectedFormIds[i] =
                        Long.parseLong((String) formsTable.getValueAt(selectedFormRows[i], 0));
                } else {
                    selectedFormIds[i] = (Long) formsTable.getValueAt(selectedFormRows[i], 0);
                }
            }
        }
        return selectedFormIds;
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

    public String getSelectedFormID() {
        if (formsTable.getSelectedRow() == -1) {
            return Localizer.localize("UICDM", "NoneSelected");
        }

        String formID = (String) formsTable.getValueAt(formsTable.getSelectedRow(), 0);
        if (formID == null) {
            return "";
        }
        return formID;
    }

    public PropertiesPanelController getPropertiesPanelController() {
        return Main.getInstance().getPropertiesPanelController();
    }

    public void updatePropertyBox() {

        getPropertiesPanelController().destroyPanels();

        if (publicationsPropertiesPanel == null) {
            publicationsPropertiesPanel = new PublicationsPropertiesPanel(this);
        }

        if (formsPropertiesPanel == null) {
            formsPropertiesPanel = new FormsPropertiesPanel(this);
        }

        if (formPagesPropertiesPanel == null) {
            formPagesPropertiesPanel = new FormPagesPropertiesPanel(this);
        }

        if (CDMTabbedPane.getSelectedIndex() == 0) {
            getPropertiesPanelController().createPanel(publicationsPropertiesPanel);
        }

        if (CDMTabbedPane.getSelectedIndex() == 1) {
            getPropertiesPanelController().createPanel(formsPropertiesPanel);
        }

        if (CDMTabbedPane.getSelectedIndex() == 2) {
            getPropertiesPanelController().createPanel(formPagesPropertiesPanel);
        }

    }

    private void CDMTabbedPaneStateChanged(ChangeEvent e) {
        CDMTabbedPaneStateChanged();
    }

    private void CDMTabbedPaneStateChanged() {
        refresh();
    }

    public void refresh() {
        refresh(true);
    }

    public void refresh(boolean updatePageNumbers) {
        if (CDMTabbedPane.getSelectedIndex() == 0) {
            if (publicationDataModel != null) {
                restorePublications();
                if (updatePageNumbers) {
                    publicationsFilterPanel.updatePageNumbers();
                }
                reselectPublications();
            }
        } else if (CDMTabbedPane.getSelectedIndex() == 1) {
            if (formDataModel != null) {
                restoreForms();
                if (updatePageNumbers) {
                    formsFilterPanel.updatePageNumbers();
                }
                reselectForms();
            }
        } else if (CDMTabbedPane.getSelectedIndex() == 2) {
            if (formPageDataModel != null) {
                restoreFormPages();
                restoreCapturedData();
                if (updatePageNumbers) {
                    formPagesFilterPanel.updatePageNumbers();
                }
            }
        }
        updatePropertyBox();
    }


    private void reselectForms() {
        try {
            TableModel tm = formsTable.getModel();
            int size = tm.getRowCount();

            for (int i = 0; i < size; i++) {
                Object idValue = tm.getValueAt(i, 0);
                Long value = null;
                if (idValue instanceof Long) {
                    value = (Long) idValue;
                } else {
                    value = Long.parseLong((String) idValue);
                }
                if (lastFormIds.contains(value)) {
                    formsTable.setRowSelectionInterval(i, i);
                }
            }
        } catch (Exception ex) {
            Misc.printStackTrace(ex);
        }
    }

    private void reselectPublications() {
        try {
            TableModel tm = publicationsTable.getModel();
            int size = tm.getRowCount();

            for (int i = 0; i < size; i++) {
                Object idValue = tm.getValueAt(i, 0);
                Long value = null;
                if (idValue instanceof Long) {
                    value = (Long) idValue;
                } else {
                    value = Long.parseLong((String) idValue);
                }
                if (lastPublicationIds.contains(value)) {
                    publicationsTable.setRowSelectionInterval(i, i);
                }
            }
        } catch (Exception ex) {
            Misc.printStackTrace(ex);
        }
    }

    public void publicationSettingsButtonActionPerformed(ActionEvent e) {

        if (publicationsTable.getSelectedRow() == -1) {
            return;
        }

        PublicationSettingsDialog psd = new PublicationSettingsDialog(Main.getInstance());
        psd.setPublicationIds(getSelectedPublicationIds());
        psd.restoreSettings();
        psd.setModal(true);
        psd.setVisible(true);

    }

    public void refreshPublicationsButtonActionPerformed(ActionEvent e) {
        restorePublications();
    }

    public void refreshFormsButtonActionPerformed(ActionEvent e) {
        restoreForms();
    }

    public void refreshFormPagesButtonActionPerformed(ActionEvent e) {
        restoreFormPages();
    }


    public void renamePublicationButtonActionPerformed(ActionEvent e) {
        if (publicationsTable.getSelectedRow() != -1) {
            RenamePublicationDialog renamePublicationDialog =
                new RenamePublicationDialog(Main.getInstance(), this);
            renamePublicationDialog.setVisible(true);
        }
    }

    public void deletePublicationsButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                deletePublications();
            }
        });
    }

    public void deletePublications() {

        if (publicationsTable.getSelectedRows().length > 0) {
            // get id from row
            String message = Localizer
                .localize("UICDM", "CapturedDataManagerFrameConfirmRemovePublicationMessage");
            String caption = Localizer
                .localize("UICDM", "CapturedDataManagerFrameConfirmRemovePublicationTitle");
            int ret = javax.swing.JOptionPane
                .showConfirmDialog(Main.getInstance(), message, caption,
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (ret == javax.swing.JOptionPane.YES_OPTION) {

                int[] selectedRows = publicationsTable.getSelectedRows();
                final long[] publicationIDs = new long[selectedRows.length];
                for (int i = 0; i < selectedRows.length; i++) {
                    publicationIDs[i] =
                        Long.parseLong((String) publicationsTable.getValueAt(selectedRows[i], 0));
                }

                final ProcessingStatusDialog statusDialog =
                    new ProcessingStatusDialog((Frame) getRootPane().getTopLevelAncestor());
                statusDialog
                    .setMessage(Localizer.localize("UICDM", "DeletePublicationsStatusMessage"));

                SwingWorker<Void, Void> deletePublicationsWorker = new SwingWorker<Void, Void>() {

                    public Void doInBackground() {
                        PublicationController pc = new PublicationController();
                        pc.removePublicationsById(publicationIDs, statusDialog);
                        return null;
                    }

                    public void done() {
                        try {
                            get();
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    if (statusDialog != null) {
                                        statusDialog.dispose();
                                    }
                                    restorePublications();
                                    Misc.showSuccessMsg(Main.getInstance(), Localizer
                                        .localize("UICDM",
                                            "CapturedDataManagerFrameDeletePublicationSuccessMessage"));
                                }
                            });
                        } catch (final Exception e) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                            if (statusDialog != null) {
                                statusDialog.dispose();
                            }
                            Main.applicationExceptionLog.error(Localizer.localize("UICDM",
                                "CapturedDataManagerFrameDeletePublicationFailureMessage"), e);
                        }
                    }

                };
                deletePublicationsWorker.execute();
                statusDialog.setModal(true);
                statusDialog.setVisible(true);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (statusDialog != null) {
                            statusDialog.dispose();
                        }
                    }
                });

            }
        }
    }

    public void clearFormCapturedDataButtonActionPerformed(ActionEvent e) {
        if (formsTable.getSelectedRow() != -1) {
            // get id from row
            String message = Localizer.localize("UICDM",
                "CapturedDataManagerFrameConfirmClearCapturedDataForFormsMessage");
            String caption =
                Localizer.localize("UICDM", "CapturedDataManagerFrameClearCapturedDataTitle");
            int ret = javax.swing.JOptionPane
                .showConfirmDialog(Main.getInstance(), message, caption,
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (ret == javax.swing.JOptionPane.YES_OPTION) {

                final LoadingDialog loadingDialog = new LoadingDialog(Main.getInstance());
                loadingDialog
                    .setMessage(Localizer.localize("UICDM", "ClearFormCapturedDataStatusMessage"));

                SwingWorker<Void, Void> clearFormDataWorker = new SwingWorker<Void, Void>() {

                    public Void doInBackground() {
                        EntityManager entityManager =
                            Main.getInstance().getJPAConfiguration().getEntityManager();
                        int[] selectedRows = formsTable.getSelectedRows();
                        for (int i = 0; i < selectedRows.length; i++) {
                            Object idObj = formsTable.getValueAt(selectedRows[i], 0);
                            long formID = -1;
                            if (idObj instanceof String) {
                                formID = Long.parseLong((String) idObj);
                            } else if (idObj instanceof Long) {
                                formID = (Long) idObj;
                            }
                            if (formID <= 0) {
                                return null;
                            }
                            clearForm(entityManager, formID);
                        }
                        return null;
                    }

                    public void done() {
                        try {
                            get();
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    if (loadingDialog != null) {
                                        loadingDialog.dispose();
                                    }
                                    restoreForms();
                                    Misc.showSuccessMsg(Main.getInstance(), Localizer
                                        .localize("UICDM", "CapturedDataRemovalSuccessMessage"));
                                }
                            });
                        } catch (final Exception e) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    if (loadingDialog != null) {
                                        loadingDialog.dispose();
                                    }
                                    Main.applicationExceptionLog.error(Localizer
                                        .localize("UICDM", "CapturedDataRemovalFailureMessage"), e);
                                }
                            });
                        }
                    }

                };
                clearFormDataWorker.execute();
                loadingDialog.setModal(true);
                loadingDialog.setVisible(true);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (loadingDialog != null) {
                            loadingDialog.dispose();
                        }
                    }
                });

            }
        }
    }

    public void clearPublicationCapturedDataButtonActionPerformed(ActionEvent e) {
        if (publicationsTable.getSelectedRow() != -1) {
            // get id from row
            String message = Localizer.localize("UICDM",
                "CapturedDataManagerFrameConfirmClearCapturedDataForPublicationsMessage");
            String caption =
                Localizer.localize("UICDM", "CapturedDataManagerFrameClearCapturedDataTitle");
            int ret = javax.swing.JOptionPane
                .showConfirmDialog(Main.getInstance(), message, caption,
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (ret == javax.swing.JOptionPane.YES_OPTION) {

                final LoadingDialog loadingDialog = new LoadingDialog(Main.getInstance());
                loadingDialog
                    .setMessage(Localizer.localize("UICDM", "ClearFormCapturedDataStatusMessage"));

                SwingWorker<Void, Void> clearFormDataWorker = new SwingWorker<Void, Void>() {

                    public Void doInBackground() {
                        EntityManager entityManager =
                            Main.getInstance().getJPAConfiguration().getEntityManager();
                        int[] selectedRows = publicationsTable.getSelectedRows();
                        for (int i = 0; i < selectedRows.length; i++) {
                            long publicationID = Long.parseLong(
                                (String) publicationsTable.getValueAt(selectedRows[i], 0));
                            clearPublication(entityManager, publicationID);
                        }
                        return null;
                    }

                    public void done() {
                        try {
                            get();
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    if (loadingDialog != null) {
                                        loadingDialog.dispose();
                                    }
                                    restorePublications();
                                    Misc.showSuccessMsg(Main.getInstance(), Localizer
                                        .localize("UICDM", "CapturedDataRemovalSuccessMessage"));
                                }
                            });
                        } catch (final Exception e) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    if (loadingDialog != null) {
                                        loadingDialog.dispose();
                                    }
                                    Main.applicationExceptionLog.error(Localizer
                                        .localize("UICDM", "CapturedDataRemovalFailureMessage"), e);
                                }
                            });
                        }
                    }

                };
                clearFormDataWorker.execute();
                loadingDialog.setModal(true);
                loadingDialog.setVisible(true);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (loadingDialog != null) {
                            loadingDialog.dispose();
                        }
                    }
                });

            }
        }
    }

    public void clearFormPageCapturedDataButtonActionPerformed(ActionEvent e) {
        if (formPagesTable.getSelectedRow() != -1) {
            // get id from row
            String message = Localizer.localize("UICDM",
                "CapturedDataManagerFrameConfirmClearCapturedDataForFormPagesMessage");
            String caption =
                Localizer.localize("UICDM", "CapturedDataManagerFrameClearCapturedDataTitle");
            int ret = javax.swing.JOptionPane
                .showConfirmDialog(Main.getInstance(), message, caption,
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (ret == javax.swing.JOptionPane.YES_OPTION) {

                final LoadingDialog loadingDialog = new LoadingDialog(Main.getInstance());
                loadingDialog
                    .setMessage(Localizer.localize("UICDM", "ClearFormCapturedDataStatusMessage"));

                SwingWorker<Void, Void> clearFormDataWorker = new SwingWorker<Void, Void>() {

                    public Void doInBackground() {
                        EntityManager entityManager =
                            Main.getInstance().getJPAConfiguration().getEntityManager();
                        int[] selectedRows = formPagesTable.getSelectedRows();
                        for (int i = 0; i < selectedRows.length; i++) {
                            long formPageID = Long.parseLong(
                                (String) formPagesTable.getValueAt(selectedRows[i], 0));
                            clearFormPage(entityManager, formPageID);
                        }
                        return null;
                    }

                    public void done() {
                        try {
                            get();
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    if (loadingDialog != null) {
                                        loadingDialog.dispose();
                                    }
                                    restoreFormPages();
                                    Misc.showSuccessMsg(Main.getInstance(), Localizer
                                        .localize("UICDM", "CapturedDataRemovalSuccessMessage"));
                                }
                            });
                        } catch (final Exception e) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    if (loadingDialog != null) {
                                        loadingDialog.dispose();
                                    }
                                    Main.applicationExceptionLog.error(Localizer
                                        .localize("UICDM", "CapturedDataRemovalFailureMessage"), e);
                                }
                            });
                        }
                    }

                };
                clearFormDataWorker.execute();
                loadingDialog.setModal(true);
                loadingDialog.setVisible(true);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (loadingDialog != null) {
                            loadingDialog.dispose();
                        }
                    }
                });

            }
        }
    }

    public void clearPublication(EntityManager entityManager, long publicationID) {

        Query formPageQuery = entityManager.createNativeQuery(
            "SELECT fp.FORM_PAGE_ID, fp.FORM_ID, frm.FORM_ID, frm.PUBLICATION_ID, pub.PUBLICATION_ID FROM FORM_PAGE fp LEFT JOIN FORM frm ON fp.FORM_ID = frm.FORM_ID LEFT JOIN PUBLICATION pub ON frm.PUBLICATION_ID = pub.PUBLICATION_ID WHERE pub.PUBLICATION_ID = "
                + publicationID, FormPage.class);
        List<FormPage> resultList = formPageQuery.getResultList();

        if (resultList.size() < 0) {
            return;
        }

        ArrayList<Long> formPageIDs = new ArrayList<Long>();
        for (FormPage formPage : resultList) {
            formPageIDs.add(formPage.getFormPageId());
        }

        entityManager.clear();

        if (formPageIDs.size() > 0) {
            for (Long formPageID : formPageIDs) {
                clearFormPage(entityManager, formPageID);
            }
        }

    }

    public void clearForm(EntityManager entityManager, long formID) {

        Query formPageQuery = entityManager.createNativeQuery(
            "SELECT fp.FORM_PAGE_ID, fp.FORM_ID, frm.FORM_ID FROM FORM_PAGE fp LEFT JOIN FORM frm ON fp.FORM_ID = frm.FORM_ID WHERE frm.FORM_ID = "
                + formID, FormPage.class);
        List<FormPage> resultList = formPageQuery.getResultList();

        if (resultList.size() < 0) {
            return;
        }

        ArrayList<Long> formPageIDs = new ArrayList<Long>();
        for (FormPage formPage : resultList) {
            formPageIDs.add(formPage.getFormPageId());
        }

        entityManager.clear();

        if (formPageIDs.size() > 0) {
            for (Long formPageID : formPageIDs) {
                clearFormPage(entityManager, formPageID);
            }
        }

    }

    public void clearFormPage(EntityManager entityManager, long formPageID) {

        entityManager.getTransaction().begin();

        // flush changes
        entityManager.flush();

        FormPage formPage = entityManager.find(FormPage.class, formPageID);

        Iterator<ProcessedImage> pii = formPage.getProcessedImageCollection().iterator();
        while (pii.hasNext()) {
            ProcessedImage processedImage = pii.next();
            entityManager.remove(processedImage);
        }

        Iterator<Segment> si = formPage.getSegmentCollection().iterator();
        while (si.hasNext()) {
            Segment segment = si.next();
            Iterator<FragmentOmr> fi = segment.getFragmentOmrCollection().iterator();
            while (fi.hasNext()) {
                FragmentOmr fragmentOmr = fi.next();
                fragmentOmr.setCapturedData(null);
                fragmentOmr.setCapturedString(null);
                fragmentOmr.setMark(0.0d);
                List<CheckBox> cbc = fragmentOmr.getCheckBoxCollection();
                if (cbc.size() > 0) {
                    for (CheckBox cb : cbc) {
                        cb.setCheckBoxMarked((short) 0);
                        entityManager.persist(cb);
                    }
                }
                entityManager.persist(fragmentOmr);
            }

            Iterator<FragmentBarcode> bi = segment.getFragmentBarcodeCollection().iterator();
            while (bi.hasNext()) {
                FragmentBarcode fragmentBarcode = bi.next();
                fragmentBarcode.setBarcodeValue(null);
                entityManager.persist(fragmentBarcode);
            }
        }

        // update form page and form
        formPage.setCaptureTime(null);
        formPage.setScannedPageNumber(0);
        formPage.setAggregateMark(0.0d);
        formPage.setProcessedTime(null);
        formPage.setErrorCount(0);
        entityManager.persist(formPage);

        Form form = formPage.getFormId();
        form.setAggregateMark(0.0d);
        form.setErrorCount(0);
        entityManager.persist(form);

        entityManager.getTransaction().commit();

        // detatch entities
        entityManager.clear();

    }

    public void exportCapturedDataButtonActionPerformed(ActionEvent e) {
        if (publicationsTable.getSelectedRow() != -1) {
            exportCapturedData(Long.parseLong(
                (String) publicationsTable.getValueAt(publicationsTable.getSelectedRow(), 0)));
        }
    }

    public void exportCSV(final File csvFile, final ArrayList<Long> publicationIds,
        final ExportOptions exportOptions) throws Exception {

        final ProcessingStatusDialog processingStatusDialog =
            new ProcessingStatusDialog((Frame) getRootPane().getTopLevelAncestor());

        class CSVFiller implements Runnable {

            public void run() {

                EntityManager entityManager =
                    Main.getInstance().getJPAConfiguration().getEntityManager();

                if (entityManager == null) {
                    return;
                }

                CSVWriter writer = null;
                CSVWriter statsWriter = null;



                try {
                    CSVExportPreferences csvep = exportOptions.getCsvExportPreferences();
                    String csvFileStr = csvFile.getCanonicalPath();
                    if (csvep.isTimestampFilenamePrefix()) {
                        csvFileStr = Misc.getTimestampPrefixedFilename(csvFileStr);
                    }
                    writer = new CSVWriter(
                        new OutputStreamWriter(new FileOutputStream(csvFileStr), "UTF-8"),
                        getDelimiterCharacter(csvep.getDelimiterType()),
                        getQuoteCharacter(csvep.getQuotesType()));
                    CSVExporter csve =
                        new CSVExporter(csvep, exportOptions.getFilters(), publicationIds,
                            entityManager);
                    csve.write(writer, processingStatusDialog);
                    if (csvep.isIncludeStatistics()) {
                        statsWriter = new CSVWriter(new OutputStreamWriter(
                            new FileOutputStream(exportOptions.getCsvStatsFile()), "UTF-8"),
                            getDelimiterCharacter(csvep.getDelimiterType()),
                            getQuoteCharacter(csvep.getQuotesType()));
                        csve.writeStats(statsWriter, processingStatusDialog);
                    }
                } catch (org.apache.openjpa.persistence.PersistenceException pe) {
                    processingStatusDialog.setException(pe);
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(pe);
                } catch (Exception ex) {
                    processingStatusDialog.setException(ex);
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                } finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (IOException e) {
                            processingStatusDialog.setException(e);
                        }
                    }
                    if (statsWriter != null) {
                        try {
                            statsWriter.close();
                        } catch (IOException e) {
                            processingStatusDialog.setException(e);
                        }
                    }
                    if (entityManager != null) {
                        entityManager.close();
                    }
                    if (processingStatusDialog != null) {
                        processingStatusDialog.dispose();
                    }
                }

            }
        }

        CSVFiller filler = new CSVFiller();
        Thread thread = new Thread(filler);
        thread.start();

        processingStatusDialog.setModal(true);
        processingStatusDialog.setVisible(true);

        if (processingStatusDialog != null) {
            if (processingStatusDialog.isInterrupted()) {
                processingStatusDialog.dispose();
                throw new Exception(Localizer.localize("UICDM", "UserAbortedExceptionMessage"));
            } else if (processingStatusDialog.getException() != null) {
                processingStatusDialog.dispose();
                throw processingStatusDialog.getException();
            }
            processingStatusDialog.dispose();
        }

    }

    public char getQuoteCharacter(int quoteType) {
        switch (quoteType) {
            case CSVExportPreferences.NO_QUOTES:
                return CSVWriter.NO_QUOTE_CHARACTER;
            case CSVExportPreferences.SINGLE_QUOTES:
                return "'".charAt(0);
            case CSVExportPreferences.DOUBLE_QUOTES:
            default:
                return "\"".charAt(0);
        }
    }

    public char getDelimiterCharacter(int delimiterType) {
        switch (delimiterType) {
            case CSVExportPreferences.TSV_DELIMITER:
                return "\t".charAt(0);
            case CSVExportPreferences.CSV_DELIMITER:
            default:
                return ",".charAt(0);
        }
    }

    public void createXSLReport(ArrayList<Long> publicationIds, byte[] xslData, String pdffile,
        MessageNotification messageNotification, ArrayList<Filter> filters) throws Exception {

        EntityManager entityManager = null;
        OutputStream out = null;
        FileOutputStream fos = null;
        ByteArrayInputStream bais = null;

        try {
            entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
            XMLExporter xmle =
                new XMLExporter(XMLExporter.PUBLICATION_EXPORT, publicationIds, entityManager,
                    filters);
            DOMSource domSource = xmle.getDomSource(messageNotification);

            FopFactory fopFactory = FopFactory.newInstance();
            Configuration cfg = Misc.getFOPConfiguration();
            fopFactory.setUserConfig(cfg);
            fopFactory.setStrictValidation(false);

            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            fos = new java.io.FileOutputStream(pdffile);
            out = new java.io.BufferedOutputStream(fos);
            bais = new ByteArrayInputStream(xslData);

            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(bais));
            transformer.setParameter("versionParam", "2.0");
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8");
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(domSource, res);
        } catch (Exception ex) {
            Misc.printStackTrace(ex);
            throw ex;
        } finally {
            if (bais != null) {
                try {
                    bais.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

    public void exportCapturedData(long publicationId) {
        ArrayList<Long> publicationIds = new ArrayList<Long>();
        publicationIds.add(publicationId);
        exportCapturedData(publicationIds);
    }

    public void exportCapturedData(ArrayList<Long> publicationIds) {

        ExportOptionsDialog eod =
            new ExportOptionsDialog((Frame) getRootPane().getTopLevelAncestor());
        eod.setPublicationIds(publicationIds);
        eod.setModal(true);
        eod.setVisible(true);

        if (eod.getDialogResult() != JOptionPane.OK_OPTION) {
            return;
        }

        ArrayList<Filter> filters = eod.getFilters();

        switch (eod.getExportType()) {

            case ExportOptions.EXPORT_CSV_WITH_STATS:
            case ExportOptions.EXPORT_CSV:

                File csvFile = eod.getCSVFile();
                if (csvFile != null) {
                    try {
                        exportCSV(csvFile, publicationIds, eod.buildExportOptions());
                        String message = Localizer.localize("UICDM", "CSVFileSavedMessage");
                        String caption = Localizer.localize("UICDM", "SuccessTitle");
                        javax.swing.JOptionPane
                            .showConfirmDialog(Main.getInstance(), message, caption,
                                javax.swing.JOptionPane.DEFAULT_OPTION,
                                javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e1) {
                        String message = Localizer.localize("UICDM", "FailureSavingCSVFileMessage");
                        String caption = Localizer.localize("UICDM", "ErrorTitle");
                        javax.swing.JOptionPane
                            .showConfirmDialog(Main.getInstance(), message, caption,
                                javax.swing.JOptionPane.DEFAULT_OPTION,
                                javax.swing.JOptionPane.ERROR_MESSAGE);
                        com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                    }
                }

                break;

            case ExportOptions.IMAGE_EXPORT:

                try {
                    exportImages(publicationIds, eod.buildExportOptions());
                    String message = Localizer.localize("UICDM", "ImagesExportedSuccessMessage");
                    String caption = Localizer.localize("UICDM", "SuccessTitle");
                    javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
                        javax.swing.JOptionPane.DEFAULT_OPTION,
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    String message = Localizer.localize("UICDM", "FailureExportingImagesMessage");
                    String caption = Localizer.localize("UICDM", "ErrorTitle");
                    javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
                        javax.swing.JOptionPane.DEFAULT_OPTION,
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                }

                break;

            case ExportOptions.EXPORT_XML:

                File xmlFile = eod.getXMLFile();

                if (xmlFile != null) {
                    try {
                        String xmlFileStr = xmlFile.getPath();
                        if (eod.isXMLTimestampFilenamePrefix()) {
                            xmlFileStr = Misc.getTimestampPrefixedFilename(xmlFileStr);
                        }
                        exportXML(publicationIds, xmlFileStr, filters);
                        String message = Localizer.localize("UICDM", "XMLFileSavedMessage");
                        String caption = Localizer.localize("UICDM", "SuccessTitle");
                        javax.swing.JOptionPane
                            .showConfirmDialog(Main.getInstance(), message, caption,
                                javax.swing.JOptionPane.DEFAULT_OPTION,
                                javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e1) {
                        String message = Localizer.localize("UICDM", "FailureSavingXMLFileMessage");
                        String caption = Localizer.localize("UICDM", "ErrorTitle");
                        javax.swing.JOptionPane
                            .showConfirmDialog(Main.getInstance(), message, caption,
                                javax.swing.JOptionPane.DEFAULT_OPTION,
                                javax.swing.JOptionPane.ERROR_MESSAGE);
                        com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                    }
                }

                break;

            case ExportOptions.EXPORT_XSLFO_FROM_FILE:

                if (eod.getPDFFile() != null && eod.getXSLFile() != null) {
                    try {
                        String pdfFileStr = eod.getPDFFile().getPath();
                        if (eod.isXMLTimestampFilenamePrefix()) {
                            pdfFileStr = Misc.getTimestampPrefixedFilename(pdfFileStr);
                        }
                        exportXSL(publicationIds, pdfFileStr, eod.getXSLFile(), filters);
                        String message = Localizer.localize("UICDM", "XSLFileSavedMessage");
                        String caption = Localizer.localize("UICDM", "SuccessTitle");
                        javax.swing.JOptionPane
                            .showConfirmDialog(Main.getInstance(), message, caption,
                                javax.swing.JOptionPane.DEFAULT_OPTION,
                                javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e1) {
                        String message = Localizer.localize("UICDM", "FailureSavingXSLFileMessage");
                        String caption = Localizer.localize("UICDM", "ErrorTitle");
                        javax.swing.JOptionPane
                            .showConfirmDialog(Main.getInstance(), message, caption,
                                javax.swing.JOptionPane.DEFAULT_OPTION,
                                javax.swing.JOptionPane.ERROR_MESSAGE);
                        com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                    }
                }

                break;

            case ExportOptions.EXPORT_XSLFO_FROM_DATABASE:

                HashMap<XSLTemplate, File> xslReports = eod.getXSLReports();

                for (XSLTemplate xslTemplate : xslReports.keySet()) {

                    File pdfFile = xslReports.get(xslTemplate);

                    if (pdfFile != null) {
                        try {
                            String pdfFileStr = pdfFile.getPath();
                            if (eod.isXMLTimestampFilenamePrefix()) {
                                pdfFileStr = Misc.getTimestampPrefixedFilename(pdfFileStr);
                            }
                            exportXSL(publicationIds, pdfFileStr, xslTemplate.getPublicationXSLId(),
                                filters);
                            String message = Localizer.localize("UICDM", "XSLFileSavedMessage");
                            String caption = Localizer.localize("UICDM", "SuccessTitle");
                            javax.swing.JOptionPane
                                .showConfirmDialog(Main.getInstance(), message, caption,
                                    javax.swing.JOptionPane.DEFAULT_OPTION,
                                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception e1) {
                            String message =
                                Localizer.localize("UICDM", "FailureSavingXSLFileMessage");
                            String caption = Localizer.localize("UICDM", "ErrorTitle");
                            javax.swing.JOptionPane
                                .showConfirmDialog(Main.getInstance(), message, caption,
                                    javax.swing.JOptionPane.DEFAULT_OPTION,
                                    javax.swing.JOptionPane.ERROR_MESSAGE);
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                        }
                    }

                }

                break;

        }

    }

    private void exportImages(final ArrayList<Long> publicationIds,
        final ExportOptions exportOptions) throws Exception {
        final ProcessingStatusDialog processingStatusDialog =
            new ProcessingStatusDialog((Frame) getRootPane().getTopLevelAncestor());

        class ExportImagesTask implements Runnable {
            public void run() {
                try {
                    ImageExporter ie = new ImageExporter(publicationIds,
                        exportOptions.getImageExportPreferences());
                    ie.setMessageNotification(processingStatusDialog);
                    ie.export();
                } catch (Exception ex) {
                    processingStatusDialog.setException(ex);
                    Misc.printStackTrace(ex);
                } finally {
                    if (processingStatusDialog != null) {
                        processingStatusDialog.dispose();
                    }
                }
            }
        }

        ExportImagesTask exportImagesTask = new ExportImagesTask();
        Thread thread = new Thread(exportImagesTask);
        thread.start();

        processingStatusDialog.setModal(true);
        processingStatusDialog.setVisible(true);

        if (processingStatusDialog != null) {
            if (processingStatusDialog.isInterrupted()) {
                processingStatusDialog.dispose();
                throw new Exception(Localizer.localize("UICDM", "UserAbortedExceptionMessage"));
            } else if (processingStatusDialog.getException() != null) {
                processingStatusDialog.dispose();
                throw processingStatusDialog.getException();
            }
            processingStatusDialog.dispose();
        }

    }

    private void exportXSL(final ArrayList<Long> publicationIds, final String pdfFile,
        final File xslFile, final ArrayList<Filter> filters) throws Exception {
        final ProcessingStatusDialog processingStatusDialog =
            new ProcessingStatusDialog((Frame) getRootPane().getTopLevelAncestor());

        class XSLFiller implements Runnable {
            public void run() {
                try {
                    byte[] xslData = Misc.getBytesFromFile(xslFile);
                    createXSLReport(publicationIds, xslData, pdfFile, processingStatusDialog,
                        filters);
                } catch (Exception ex) {
                    processingStatusDialog.setException(ex);
                    Misc.printStackTrace(ex);
                } finally {
                    if (processingStatusDialog != null) {
                        processingStatusDialog.dispose();
                    }
                }
            }
        }

        XSLFiller filler = new XSLFiller();
        Thread thread = new Thread(filler);
        thread.start();

        processingStatusDialog.setModal(true);
        processingStatusDialog.setVisible(true);

        if (processingStatusDialog != null) {
            if (processingStatusDialog.isInterrupted()) {
                processingStatusDialog.dispose();
                throw new Exception(Localizer.localize("UICDM", "UserAbortedExceptionMessage"));
            } else if (processingStatusDialog.getException() != null) {
                processingStatusDialog.dispose();
                throw processingStatusDialog.getException();
            }
            processingStatusDialog.dispose();
        }

    }

    private void exportXSL(final ArrayList<Long> publicationIds, final String pdfOutputFile,
        final long publicationXSLId, final ArrayList<Filter> filters) throws Exception {

        final ProcessingStatusDialog processingStatusDialog =
            new ProcessingStatusDialog((Frame) getRootPane().getTopLevelAncestor());

        class XSLFiller implements Runnable {
            public void run() {
                EntityManager entityManager = null;
                try {
                    entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
                    PublicationXSL pxsl =
                        entityManager.find(PublicationXSL.class, publicationXSLId);
                    byte[] xslData = pxsl.getXslData();
                    createXSLReport(publicationIds, xslData, pdfOutputFile, processingStatusDialog,
                        filters);
                } catch (Exception ex) {
                    processingStatusDialog.setException(ex);
                    Misc.printStackTrace(ex);
                } finally {
                    if (entityManager != null && entityManager.isOpen()) {
                        entityManager.close();
                    }
                    if (processingStatusDialog != null) {
                        processingStatusDialog.dispose();
                    }
                }
            }
        }

        XSLFiller filler = new XSLFiller();
        Thread thread = new Thread(filler);
        thread.start();

        processingStatusDialog.setModal(true);
        processingStatusDialog.setVisible(true);

        if (processingStatusDialog != null) {
            if (processingStatusDialog.isInterrupted()) {
                processingStatusDialog.dispose();
                throw new Exception(Localizer.localize("UICDM", "UserAbortedExceptionMessage"));
            } else if (processingStatusDialog.getException() != null) {
                processingStatusDialog.dispose();
                throw processingStatusDialog.getException();
            }
            processingStatusDialog.dispose();
        }

    }

    private void exportXML(final ArrayList<Long> publicationIds, final String outputXMLFile,
        final ArrayList<Filter> filters) throws Exception {

        final ProcessingStatusDialog processingStatusDialog =
            new ProcessingStatusDialog((Frame) getRootPane().getTopLevelAncestor());

        class XMLFiller implements Runnable {

            public void run() {
                EntityManager entityManager = null;
                OutputStreamWriter osw = null;
                FileOutputStream fos = null;
                BufferedWriter bufferedWriter = null;
                try {
                    entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
                    fos = new FileOutputStream(outputXMLFile);
                    osw = new OutputStreamWriter(fos, "UTF-8");
                    bufferedWriter = new BufferedWriter(osw);
                    XMLExporter xmle =
                        new XMLExporter(XMLExporter.PUBLICATION_EXPORT, publicationIds,
                            entityManager, filters);
                    xmle.write(bufferedWriter, processingStatusDialog);
                } catch (IOException e) {
                    processingStatusDialog.setException(e);
                    Misc.printStackTrace(e);
                } catch (ParserConfigurationException e) {
                    processingStatusDialog.setException(e);
                    Misc.printStackTrace(e);
                } catch (TransformerException e) {
                    processingStatusDialog.setException(e);
                    Misc.printStackTrace(e);
                } catch (InterruptedException e) {
                    processingStatusDialog.setException(e);
                    Misc.printStackTrace(e);
                } finally {
                    if (bufferedWriter != null) {
                        try {
                            bufferedWriter.close();
                        } catch (IOException e) {
                        }
                    }
                    if (osw != null) {
                        try {
                            osw.close();
                        } catch (IOException e) {
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                        }
                    }
                    if (entityManager != null && entityManager.isOpen()) {
                        entityManager.close();
                    }
                    if (processingStatusDialog != null) {
                        processingStatusDialog.dispose();
                    }
                }
            }
        }

        XMLFiller filler = new XMLFiller();
        Thread thread = new Thread(filler);
        thread.start();

        processingStatusDialog.setModal(true);
        processingStatusDialog.setVisible(true);

        if (processingStatusDialog != null) {
            if (processingStatusDialog.isInterrupted()) {
                processingStatusDialog.dispose();
                throw new Exception(Localizer.localize("UICDM", "UserAbortedExceptionMessage"));
            } else if (processingStatusDialog.getException() != null) {
                processingStatusDialog.dispose();
                throw processingStatusDialog.getException();
            }
            processingStatusDialog.dispose();
        }

    }

    public void deleteFormsButtonActionPerformed(ActionEvent e) {
        if (formsTable.getSelectedRows().length > 0) {
            // get id from row
            String message =
                Localizer.localize("UICDM", "CapturedDataManagerFrameConfirmRemoveFormMessage");
            String caption =
                Localizer.localize("UICDM", "CapturedDataManagerFrameConfirmRemoveFormTitle");
            int ret = javax.swing.JOptionPane
                .showConfirmDialog(Main.getInstance(), message, caption,
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (ret == javax.swing.JOptionPane.YES_OPTION) {

                final ProcessingStatusDialog processingStatusDialog =
                    new ProcessingStatusDialog((Frame) getRootPane().getTopLevelAncestor());
                processingStatusDialog.setMessage(
                    Localizer.localize("UICDM", "CapturedDataManagerFrameDeleteFormStatusMessage"));

                SwingWorker<Void, Void> clearFormDataWorker = new SwingWorker<Void, Void>() {

                    public Void doInBackground() {
                        EntityManager entityManager =
                            Main.getInstance().getJPAConfiguration().getEntityManager();
                        entityManager.getTransaction().begin();
                        entityManager.flush();
                        int[] selectedRows = formsTable.getSelectedRows();
                        for (int i = 0; i < selectedRows.length; i++) {
                            Object obj = formsTable.getValueAt(selectedRows[i], 0);
                            long formID = -1;
                            if (obj instanceof Long) {
                                formID = (Long) obj;
                            } else if (obj instanceof String) {
                                formID = Long.parseLong((String) obj);
                            }

                            processingStatusDialog.setMessage(String
                                .format(Localizer.localize("UI", "DeletingFormStatusMessageText"),
                                    (i + 1) + "", selectedRows.length + ""));

                            String deleteSql = "DELETE FROM FORM WHERE FORM_ID = " + formID;
                            Query deleteFormQuery = entityManager.createNativeQuery(deleteSql);
                            int result = deleteFormQuery.executeUpdate();

                            if (result <= 0) {
                                if (entityManager.getTransaction().isActive()) {
                                    try {
                                        entityManager.getTransaction().rollback();
                                    } catch (Exception rbex) {
                                        com.ebstrada.formreturn.manager.util.Misc
                                            .printStackTrace(rbex);
                                    }
                                }
                                processingStatusDialog.setException(new Exception(String
                                    .format(Localizer.localize("UI", "DeleteFormErrorMessageText"),
                                        formID + "")));
                                processingStatusDialog.setInterrupted(true);
                                return null;
                            }

                        }
                        entityManager.getTransaction().commit();
                        return null;
                    }

                    public void done() {
                        try {
                            get();
                            if (processingStatusDialog != null) {
                                if (processingStatusDialog.isInterrupted()) {
                                    throw processingStatusDialog.getException();
                                }
                                processingStatusDialog.dispose();
                            }
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    restoreForms();
                                    Misc.showSuccessMsg(Main.getInstance(), Localizer
                                        .localize("UICDM",
                                            "CapturedDataManagerFrameDeleteFormSuccessMessage"));
                                }
                            });
                        } catch (final Exception e) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    if (processingStatusDialog != null) {
                                        processingStatusDialog.dispose();
                                    }
                                    Main.applicationExceptionLog.error(Localizer.localize("UICDM",
                                        "CapturedDataManagerFrameDeleteFormFailureMessage"), e);
                                }
                            });
                        }
                    }

                };
                clearFormDataWorker.execute();
                processingStatusDialog.setModal(true);
                processingStatusDialog.setVisible(true);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (processingStatusDialog != null) {
                            processingStatusDialog.dispose();
                        }
                    }
                });


            }
        }
    }

    public void reprocessButtonActionPerformed(ActionEvent e) {

        long[] selectedFormPageIds = getSelectedFormPageIds();

        if (selectedFormPageIds == null || selectedFormPageIds.length <= 0) {
            return;
        }

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        byte[] imageData = null;

        if (entityManager == null) {
            return;
        }

        long formPageId = 0;

        try {
            FormPage formPage = entityManager.find(FormPage.class, selectedFormPageIds[0]);
            formPageId = formPage.getFormPageId();
            List<ProcessedImage> pic = formPage.getProcessedImageCollection();
            if (pic == null || pic.size() <= 0) {
                return;
            }
            ProcessedImage pi = pic.iterator().next();
            imageData = pi.getProcessedImageData();
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return;
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

        final byte[] finalImageData = imageData;
        final long finalFormPageId = formPageId;

        Main.getInstance().blockInput();

        final LoadingDialog ld = new LoadingDialog(Main.getInstance());
        ld.setVisible(true);

        SwingWorker<ReprocessorFrame, Void> worker = new SwingWorker<ReprocessorFrame, Void>() {
            protected ReprocessorFrame doInBackground() throws InterruptedException {
                if (finalImageData != null && finalImageData.length > 0) {

                    if (Main.getInstance()
                        .checkReprocessorFrameOpen(finalFormPageId, ReprocessorFrame.FORM_PAGE)
                        == true) {
                        return null;
                    }

                    try {
                        ReprocessorFrame reprocessorFrame =
                            new ReprocessorFrame(finalImageData, finalFormPageId,
                                Main.getInstance().getReprocessorZoom());
                        reprocessorFrame.restore();
                        return reprocessorFrame;
                    } catch (Exception ex) {
                        com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                        return null;
                    }
                } else {
                    return null;
                }
            }

            protected void done() {
                ReprocessorFrame reprocessorFrame = null;
                Main.getInstance().unblockInput();
                ld.dispose();

                try {
                    reprocessorFrame = get();
                    if (reprocessorFrame != null) {
                        Main.getInstance().addReprocessorFrame(reprocessorFrame);
                    }
                } catch (InterruptedException e) {
                } catch (ExecutionException e) {
                } catch (Exception ex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                }
            }
        };
        worker.execute();

    }

    public void previewButtonActionPerformed(ActionEvent e) {

        final byte[] imageData = formPageDataModel.getImage();

        if (imageData == null || imageData.length <= 0) {
            return;
        }

        final LoadingDialog ld = new LoadingDialog(Main.getInstance());
        ld.setVisible(true);

        Main.getInstance().blockInput();

        SwingWorker<ImagePreviewFrame, Void> worker = new SwingWorker<ImagePreviewFrame, Void>() {
            protected ImagePreviewFrame doInBackground() throws InterruptedException {
                return new ImagePreviewFrame(Main.getInstance(), imageData, true);
            }

            protected void done() {
                ImagePreviewFrame ipf;
                Main.getInstance().unblockInput();
                ld.dispose();
                try {
                    ipf = get();
                    if (ipf != null) {
                        ipf.setVisible(true);
                    }
                } catch (InterruptedException e) {
                } catch (ExecutionException e) {
                }
            }
        };
        worker.execute();

    }

    public void resetHorizontalDivider() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                horizontalSplitPane.setDividerLocation(0.7d);
            }
        });
    }

    public void resetVerticalDivider() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                verticalSplitPane.setDividerLocation(0.4d);
            }
        });
    }

    private void formPagesPanelComponentResized(ComponentEvent e) {
        resetHorizontalDivider();
        resetVerticalDivider();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        CDMTabbedPane = new JTabbedPane();
        publicationsPanel = new JPanel();
        publicationsScrollPane = new JScrollPane();
        publicationsTable = new JTable();
        publicationsFilterPanel = new TableFilterPanel();
        formsPanel = new JPanel();
        formsScrollPane = new JScrollPane();
        formsTable = new JTable();
        formsFilterPanel = new TableFilterPanel();
        formPagesPanel = new JPanel();
        horizontalSplitPane = new JSplitPane();
        verticalSplitPane = new JSplitPane();
        formPagesListPanel = new JPanel();
        formPagesScrollPane = new JScrollPane();
        formPagesTable = new JTable();
        formPagesFilterPanel = new TableFilterPanel();
        fragmentDataPanel = new JPanel();
        fragmentDataScrollPane = new JScrollPane();
        capturedDataTable = new JTable();
        formPageDetailsPanel = new JPanel();
        pageScoreLabel = new JLabel();
        pageScoreTextField = new JTextField();
        formScoreLabel = new JLabel();
        formScoreTextField = new JTextField();
        imagePreviewContainer = new JPanel();

        //======== this ========

        setLayout(new BorderLayout());

        //======== CDMTabbedPane ========
        {
            CDMTabbedPane.setFont(UIManager.getFont("TabbedPane.font"));
            CDMTabbedPane.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    CDMTabbedPaneStateChanged(e);
                }
            });

            //======== publicationsPanel ========
            {
                publicationsPanel.setOpaque(false);
                publicationsPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)publicationsPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)publicationsPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout)publicationsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)publicationsPanel.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

                //======== publicationsScrollPane ========
                {

                    //---- publicationsTable ----
                    publicationsTable.setShowHorizontalLines(false);
                    publicationsTable.setShowVerticalLines(false);
                    publicationsTable.setFont(UIManager.getFont("Table.font"));
                    publicationsTable.setShowGrid(false);
                    publicationsTable.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));
                    publicationsScrollPane.setViewportView(publicationsTable);
                }
                publicationsPanel.add(publicationsScrollPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
                publicationsPanel.add(publicationsFilterPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            CDMTabbedPane.addTab("Publications", new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/one.png")), publicationsPanel);

            //======== formsPanel ========
            {
                formsPanel.setOpaque(false);
                formsPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)formsPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)formsPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout)formsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)formsPanel.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

                //======== formsScrollPane ========
                {

                    //---- formsTable ----
                    formsTable.setShowHorizontalLines(false);
                    formsTable.setShowVerticalLines(false);
                    formsTable.setFont(UIManager.getFont("Table.font"));
                    formsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    formsTable.setShowGrid(false);
                    formsTable.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));
                    formsScrollPane.setViewportView(formsTable);
                }
                formsPanel.add(formsScrollPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
                formsPanel.add(formsFilterPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            CDMTabbedPane.addTab("Forms", new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/two.png")), formsPanel);

            //======== formPagesPanel ========
            {
                formPagesPanel.setOpaque(false);
                formPagesPanel.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        formPagesPanelComponentResized(e);
                    }
                });
                formPagesPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)formPagesPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)formPagesPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)formPagesPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)formPagesPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //======== horizontalSplitPane ========
                {
                    horizontalSplitPane.setBorder(null);
                    horizontalSplitPane.setOpaque(false);
                    horizontalSplitPane.setDividerSize(9);

                    //======== verticalSplitPane ========
                    {
                        verticalSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
                        verticalSplitPane.setOpaque(false);
                        verticalSplitPane.setBorder(null);
                        verticalSplitPane.setResizeWeight(0.4);
                        verticalSplitPane.setDividerSize(9);

                        //======== formPagesListPanel ========
                        {
                            formPagesListPanel.setOpaque(false);
                            formPagesListPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)formPagesListPanel.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout)formPagesListPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                            ((GridBagLayout)formPagesListPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                            ((GridBagLayout)formPagesListPanel.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

                            //======== formPagesScrollPane ========
                            {

                                //---- formPagesTable ----
                                formPagesTable.setShowHorizontalLines(false);
                                formPagesTable.setShowVerticalLines(false);
                                formPagesTable.setFont(UIManager.getFont("Table.font"));
                                formPagesTable.setShowGrid(false);
                                formPagesTable.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));
                                formPagesScrollPane.setViewportView(formPagesTable);
                            }
                            formPagesListPanel.add(formPagesScrollPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                            formPagesListPanel.add(formPagesFilterPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        verticalSplitPane.setTopComponent(formPagesListPanel);

                        //======== fragmentDataPanel ========
                        {
                            fragmentDataPanel.setOpaque(false);
                            fragmentDataPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)fragmentDataPanel.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout)fragmentDataPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                            ((GridBagLayout)fragmentDataPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                            ((GridBagLayout)fragmentDataPanel.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

                            //======== fragmentDataScrollPane ========
                            {

                                //---- capturedDataTable ----
                                capturedDataTable.setShowHorizontalLines(false);
                                capturedDataTable.setShowVerticalLines(false);
                                capturedDataTable.setFont(UIManager.getFont("Table.font"));
                                capturedDataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                                capturedDataTable.setShowGrid(false);
                                capturedDataTable.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));
                                fragmentDataScrollPane.setViewportView(capturedDataTable);
                            }
                            fragmentDataPanel.add(fragmentDataScrollPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));

                            //======== formPageDetailsPanel ========
                            {
                                formPageDetailsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                                formPageDetailsPanel.setOpaque(false);
                                formPageDetailsPanel.setLayout(new GridBagLayout());
                                ((GridBagLayout)formPageDetailsPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 15, 0, 0, 0, 0};
                                ((GridBagLayout)formPageDetailsPanel.getLayout()).rowHeights = new int[] {0, 0};
                                ((GridBagLayout)formPageDetailsPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, 1.0E-4};
                                ((GridBagLayout)formPageDetailsPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                                //---- pageScoreLabel ----
                                pageScoreLabel.setFont(UIManager.getFont("Label.font"));
                                pageScoreLabel.setText(Localizer.localize("UICDM", "PageScoreLabel"));
                                formPageDetailsPanel.add(pageScoreLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));
                                formPageDetailsPanel.add(pageScoreTextField, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));

                                //---- formScoreLabel ----
                                formScoreLabel.setFont(UIManager.getFont("Label.font"));
                                formScoreLabel.setText(Localizer.localize("UICDM", "FormScoreLabel"));
                                formPageDetailsPanel.add(formScoreLabel, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));
                                formPageDetailsPanel.add(formScoreTextField, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));
                            }
                            fragmentDataPanel.add(formPageDetailsPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        verticalSplitPane.setBottomComponent(fragmentDataPanel);
                    }
                    horizontalSplitPane.setLeftComponent(verticalSplitPane);

                    //======== imagePreviewContainer ========
                    {
                        imagePreviewContainer.setBackground(Color.white);
                        imagePreviewContainer.setLayout(new BorderLayout());
                    }
                    horizontalSplitPane.setRightComponent(imagePreviewContainer);
                }
                formPagesPanel.add(horizontalSplitPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            CDMTabbedPane.addTab("Form Pages", new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/three.png")), formPagesPanel);
        }
        add(CDMTabbedPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JTabbedPane CDMTabbedPane;
    private JPanel publicationsPanel;
    private JScrollPane publicationsScrollPane;
    private JTable publicationsTable;
    private TableFilterPanel publicationsFilterPanel;
    private JPanel formsPanel;
    private JScrollPane formsScrollPane;
    private JTable formsTable;
    private TableFilterPanel formsFilterPanel;
    private JPanel formPagesPanel;
    private JSplitPane horizontalSplitPane;
    private JSplitPane verticalSplitPane;
    private JPanel formPagesListPanel;
    private JScrollPane formPagesScrollPane;
    private JTable formPagesTable;
    private TableFilterPanel formPagesFilterPanel;
    private JPanel fragmentDataPanel;
    private JScrollPane fragmentDataScrollPane;
    private JTable capturedDataTable;
    private JPanel formPageDetailsPanel;
    private JLabel pageScoreLabel;
    private JTextField pageScoreTextField;
    private JLabel formScoreLabel;
    private JTextField formScoreTextField;
    private JPanel imagePreviewContainer;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public void refresh(boolean updatePageNumbers, TableFilterPanel tableFilterPanel) {
        if (tableFilterPanel.getTableModel() instanceof FormPageDataModel) {
            restoreFormPages();
            restoreCapturedData();
            if (updatePageNumbers) {
                formPagesFilterPanel.updatePageNumbers();
            }
        } else if (tableFilterPanel.getTableModel() instanceof FormDataModel) {
            restoreForms();
            if (updatePageNumbers) {
                formsFilterPanel.updatePageNumbers();
            }
        } else if (tableFilterPanel.getTableModel() instanceof PublicationDataModel) {
            restorePublications();
            if (updatePageNumbers) {
                publicationsFilterPanel.updatePageNumbers();
            }
        }
        updatePropertyBox();
    }

    public void clearFormPageFilterButtonActionPerformed(ActionEvent e) {
        if (formPageDataModel != null) {
            formPageDataModel.resetParentSearchFilters();
            refresh();
        }
    }

    public void clearFormFilterButtonActionPerformed(ActionEvent e) {
        if (formDataModel != null) {
            formDataModel.resetParentSearchFilters();
            refresh();
        }
    }

    public void extendCapturedDataActionPerformed(ActionEvent e) {

        // 1. check that something was selected
        if (publicationsTable.getSelectedRow() == -1) {
            return;
        }

        // get publication id
        long publicationId = Misc.parseLongString(
            (String) publicationsTable.getValueAt(publicationsTable.getSelectedRow(), 0));

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
        if (entityManager == null) {
            return;
        }

        String sql =
            "SELECT PUBLICATION.PUBLICATION_TYPE, DATA_SET.DATA_SET_ID FROM PUBLICATION INNER JOIN DATA_SET ON PUBLICATION.DATA_SET_ID = DATA_SET.DATA_SET_ID WHERE PUBLICATION.PUBLICATION_ID = "
                + publicationId;
        Query query = entityManager.createNativeQuery(sql);
        Object[] result = (Object[]) query.getSingleResult();

        int publicationType = (Integer) result[0];

        // 2. make sure that what was selected was a form id publication
        if (publicationType != PublicationPreferences.FORM_ID_RECONCILE_WITH_SOURCE_DATA_RECORD) {
            Misc.showErrorMsg(getRootPane(),
                Localizer.localize("UICDM", "CannotExtendNonFormIDPublicationText"));
            return;
        }

        long dataSetId = (Long) result[1];

        sql =
            "SELECT RECORD.RECORD_ID FROM DATA_SET INNER JOIN RECORD ON DATA_SET.DATA_SET_ID = RECORD.DATA_SET_ID LEFT OUTER JOIN FORM ON RECORD.RECORD_ID = FORM.RECORD_ID WHERE FORM.FORM_ID IS NULL AND DATA_SET.DATA_SET_ID = "
                + dataSetId;
        query = entityManager.createNativeQuery(sql);
        List<Long> resultList = query.getResultList();

        // 3. make sure that the source data table that was published contains records that aren't linked to this publication
        if (resultList.size() <= 0) {
            Misc.showErrorMsg(getRootPane(),
                Localizer.localize("UICDM", "NoNewSourceRecordsForExtensionText"));
            return;
        }

        ArrayList<Long> recordIds = new ArrayList<Long>();

        for (Long recordId : resultList) {
            recordIds.add(recordId);
        }

        ExtendPublicationDialog epd = new ExtendPublicationDialog(Main.getInstance(), this);
        epd.setRecordIds(recordIds);
        epd.setPublicationId(publicationId);
        epd.setDataSetId(dataSetId);
        epd.refresh();
        epd.setModal(true);
        epd.setVisible(true);
    }

    public void extendPublication(long publicationId, ArrayList<Long> recordIds) throws Exception {

        FormPublisher formPublisher = new FormPublisher(publicationId);
        MessageNotification publishStatusDialog = new ProcessingStatusDialog(Main.getInstance());

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
        if (entityManager == null) {
            throw new FormPublisherException(FormPublisherException.NO_ENTITY_MANAGER);
        }

        try {
            formPublisher
                .extendPublication(entityManager, publicationId, recordIds, publishStatusDialog);
            Misc.showSuccessMsg(getRootPane(),
                Localizer.localize("UI", "PublicationExtendedSuccessfullyMessage"));
        } catch (Exception ex) {
            Misc.showErrorMsg(getRootPane(),
                Localizer.localize("UI", "PublicationExtendedFailureMessage"));
            throw ex;
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

}
