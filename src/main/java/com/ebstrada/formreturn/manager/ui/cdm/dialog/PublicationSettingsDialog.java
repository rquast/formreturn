package com.ebstrada.formreturn.manager.ui.cdm.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.Document;

import org.apache.openjpa.persistence.RollbackException;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.ebstrada.formreturn.api.messaging.ProcessingStatusDialog;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigBarcodeReader;
import com.ebstrada.formreturn.manager.gef.presentation.FigCheckbox;
import com.ebstrada.formreturn.manager.gef.presentation.FigSegment;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.aggregation.AggregateCalculator;
import com.ebstrada.formreturn.manager.logic.publish.FormPDFGenerator;
import com.ebstrada.formreturn.manager.logic.publish.FormPublisher;
import com.ebstrada.formreturn.manager.persistence.JPAConfiguration;
import com.ebstrada.formreturn.manager.persistence.jpa.Form;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentBarcode;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentOmr;
import com.ebstrada.formreturn.manager.persistence.jpa.Grading;
import com.ebstrada.formreturn.manager.persistence.jpa.GradingRule;
import com.ebstrada.formreturn.manager.persistence.jpa.IncomingImage;
import com.ebstrada.formreturn.manager.persistence.jpa.Publication;
import com.ebstrada.formreturn.manager.persistence.jpa.PublicationXSL;
import com.ebstrada.formreturn.manager.persistence.jpa.Segment;
import com.ebstrada.formreturn.manager.persistence.xstream.Page;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.ui.editor.dialog.GradingRuleDialog;
import com.ebstrada.formreturn.manager.ui.editor.dialog.XSLTemplateDialog;
import com.ebstrada.formreturn.manager.ui.editor.persistence.MarkingProperties;
import com.ebstrada.formreturn.manager.ui.editor.persistence.MarkingRule;
import com.ebstrada.formreturn.manager.ui.editor.persistence.Templates;
import com.ebstrada.formreturn.manager.ui.editor.persistence.XSLTemplate;
import com.ebstrada.formreturn.manager.ui.filefilter.FilenameExtensionFilter;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.RandomGUID;
import com.ebstrada.formreturn.manager.util.TemplateFormPageID;
import com.ebstrada.formreturn.manager.util.image.ImageUtil;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.PublicationPreferences;


public class PublicationSettingsDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private int dialogResult = JOptionPane.CANCEL_OPTION;

    private long[] publicationIds;

    private long publicationId;

    private JGraph graph;

    private File templateFile;

    private SwingWorker<Void, Void> recalculateScoresWorker;

    private int publicationType;

    private MarkingProperties markingProperties = new MarkingProperties();

    private Templates xslTemplates = new Templates();

    public static final int TYPE_COLUMN = 0;
    public static final int OLD_FIELD_NAME_COLUMN = 1;
    public static final int NEW_FIELD_NAME_COLUMN = 2;
    public static final int OLD_ORDER_INDEX_COLUMN = 3;
    public static final int NEW_ORDER_INDEX_COLUMN = 4;
    public static final int OLD_AGGREGATE_RULE_COLUMN = 5;
    public static final int NEW_AGGREGATE_RULE_COLUMN = 6;

    public PublicationSettingsDialog(Frame owner) {
        super(owner);
        initComponents();
        localizeHeadings();

        EditorTextField textField = new EditorTextField();
        textField.addCaretListener(new MarkValuesCaretListener());
        textField.setBorder(null);
        formStructureTable.setDefaultEditor(Object.class, new DefaultCellEditor(textField));

        getRootPane().setDefaultButton(closeButton);
    }

    public PublicationSettingsDialog(Dialog owner) {
        super(owner);
        initComponents();
        localizeHeadings();

        EditorTextField textField = new EditorTextField();
        textField.addCaretListener(new MarkValuesCaretListener());
        textField.setBorder(null);
        formStructureTable.setDefaultEditor(Object.class, new DefaultCellEditor(textField));

        getRootPane().setDefaultButton(closeButton);
    }

    public void localizeHeadings() {
        publicationSettingsTabbedPane
            .setTitleAt(0, Localizer.localize("UI", "PublicationSettingsAnswerKeyTabTitle"));
        publicationSettingsTabbedPane
            .setTitleAt(1, Localizer.localize("UI", "FormPropertiesMarkingTabTitle"));
        publicationSettingsTabbedPane
            .setTitleAt(2, Localizer.localize("UI", "FormPropertiesXSLFOReportTabTitle"));
        publicationSettingsTabbedPane.setTitleAt(3,
            Localizer.localize("UI", "PublicationSettingsRecognitionSettingsTabTitle"));
        publicationSettingsTabbedPane
            .setTitleAt(4, Localizer.localize("UI", "PublicationSettingsPublicationFilesTabTitle"));
        publicationSettingsTabbedPane
            .setTitleAt(5, Localizer.localize("UI", "PublicationSettingsAdvancedTabTitle"));
    }

    public class EditorTextField extends JTextField {

        private static final long serialVersionUID = 1L;

        private boolean appendFirstKey;
        private boolean firstHandled;

        @Override public void addNotify() {
            super.addNotify();
            selectAll();
            firstHandled = false;
        }

        public void setAppendFirstKey(boolean appendFirst) {
            this.appendFirstKey = appendFirst;
        }

        @Override protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition,
            boolean pressed) {
            checkSelection(e);
            return super.processKeyBinding(ks, e, condition, pressed);
        }

        private void checkSelection(KeyEvent e) {
            if (!appendFirstKey || firstHandled)
                return;
            firstHandled = true;
            if ((e == null) || (e.getSource() != this)) {
                clearSelection();
            }
        }

        private void clearSelection() {
            Document doc = getDocument();
            select(doc.getLength(), doc.getLength());
        }

    }


    private class MarkValuesCaretListener implements CaretListener {
        public void caretUpdate(CaretEvent e) {
            JTextField source = (JTextField) e.getSource();
            int column = formStructureTable.getSelectedColumn();
            int row = formStructureTable.getSelectedRow();
            String newText = source.getText();
            String oldText = (String) formStructureTable.getValueAt(row, column);
            if (oldText == null || !(oldText.equals(newText))) {
                formStructureTable.setValueAt(newText, row, column);
            }
        }
    }

    public void restoreFormStructureTable() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    formStructureTable.setModel(getFormStructureModel());
                    formStructureTable.getTableHeader().setReorderingAllowed(false);
                } catch (Exception e) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                }
            }
        });
    }

    public TableModel getFormStructureModel() throws Exception {


        DefaultTableModel dtm = new DefaultTableModel() {

            private static final long serialVersionUID = 1L;

            Class[] columnTypes =
                new Class[] {String.class, String.class, String.class, String.class, String.class,
                    String.class, String.class};
            boolean[] columnEditable = new boolean[] {false, false, true, false, true, false, true};

            @Override public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnEditable[columnIndex];
            }
        };

        dtm.addColumn(Localizer.localize("UICDM", "TypeColumnName"));
        dtm.addColumn(Localizer.localize("UICDM", "OldFieldNameColumnName"));
        dtm.addColumn(Localizer.localize("UICDM", "NewFieldNameColumnName"));
        dtm.addColumn(Localizer.localize("UICDM", "OldOrderIndexColumnName"));
        dtm.addColumn(Localizer.localize("UICDM", "NewOrderIndexColumnName"));
        dtm.addColumn(Localizer.localize("UICDM", "OldAggregationRuleColumnName"));
        dtm.addColumn(Localizer.localize("UICDM", "NewAggregationRuleColumnName"));

        if (graph == null) {
            return dtm;
        }

        ArrayList<Fig> sortedData = new ArrayList<Fig>();

        if (publicationIds == null || publicationIds.length <= 0) {
            return dtm;
        }

        // READ GRAPH STRUCTURE
        Map<String, Page> pages = graph.getDocument().getPages();

        if (pages.size() <= 0) {
            return dtm;
        }

        for (Page page : pages.values()) {
            ArrayList<Fig> figs = page.getFigs();
            if (figs.size() > 0) {
                for (Fig fig : figs) {

                    if (fig instanceof FigSegment) {

                        FigSegment figSegment = (FigSegment) fig;

                        ArrayList<com.ebstrada.formreturn.manager.persistence.xstream.Document>
                            segments = figSegment.getSegmentContainer().getSegments();

                        for (com.ebstrada.formreturn.manager.persistence.xstream.Document segmentDocument : segments) {
                            for (Page segmentPage : segmentDocument.getPages().values()) {
                                ArrayList<Fig> segmentFigs = segmentPage.getFigs();

                                for (Fig segmentFig : segmentFigs) {
                                    if (segmentFig instanceof FigBarcodeReader) {
                                        sortedData.add(segmentFig);
                                    }
                                    if (segmentFig instanceof FigCheckbox) {
                                        sortedData.add(segmentFig);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for (Object obj : sortedData) {

            if (obj instanceof FigCheckbox) {

                FigCheckbox figCheckbox = (FigCheckbox) obj;
                long orderIndex = figCheckbox.getFieldnameOrderIndex();
                String fieldname = figCheckbox.getFieldname();
                String markFieldname = figCheckbox.getMarkFieldname();
                if (markFieldname == null || markFieldname.trim().length() <= 0) {
                    markFieldname = fieldname + Localizer.localize("UICDM", "MarkColumnNameSuffix");
                }
                long markOrderIndex = figCheckbox.getMarkFieldnameOrderIndex();
                String aggregationRule = figCheckbox.getAggregationRule();
                dtm.addRow(
                    new String[] {Localizer.localize("UICDM", "Checkbox"), fieldname, fieldname,
                        orderIndex + "", orderIndex + "", aggregationRule, aggregationRule});
                dtm.addRow(new String[] {Localizer.localize("UICDM", "Score"), markFieldname,
                    markFieldname, markOrderIndex + "", markOrderIndex + "", "", ""});

            } else if (obj instanceof FigBarcodeReader) {

                FigBarcodeReader figBarcodeReader = (FigBarcodeReader) obj;
                long orderIndex = figBarcodeReader.getFieldnameOrderIndex();
                String fieldname = figBarcodeReader.getFieldname();
                dtm.addRow(
                    new String[] {Localizer.localize("UICDM", "Barcode"), fieldname, fieldname,
                        orderIndex + "", orderIndex + "", "", ""});
            }
        }

        return dtm;
    }

    private void loadPublication() {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null || publicationIds.length <= 0) {
            return;
        }

        publicationId = publicationIds[0];

        templateFile = null;

        try {
            templateFile = getTemplateFileAndPublicationType();
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return;
        }

        // READ STRUCTURE
        graph = new JGraph();

        try {
            graph.getDocumentPackage().open(templateFile, graph);
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            graph = null;
        }

        setTitle(Localizer.localize("UICDM", "PublicationSettingsDialogTitle") + " (ID "
            + publicationIds[0] + ")");
    }

    public void restoreSettings() {

        if (publicationIds != null && publicationIds.length > 0) {
            loadPublication();

            if (publicationId > 0) {
                restoreGradingProperties();
                restoreXSLTemplateProperties();
                restoreRecognitionSettings();
                restoreFormStructureTable();
                restorePublicationType();
            } else {
                dispose();
            }

        } else {
            dispose();
        }

    }

    private void restoreXSLTemplateProperties() {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        Publication publication;

        try {
            publication = entityManager.find(Publication.class, publicationId);
            if (publication != null) {
                restoreXSLTemplateProperties(publication);
            }
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return;
        } finally {
            entityManager.close();
        }

    }

    private void restoreXSLTemplateProperties(Publication publication) {
        this.xslTemplates = new Templates();
        List<PublicationXSL> xslTemplateCollection = publication.getPublicationXSLCollection();
        for (PublicationXSL publicationXSL : xslTemplateCollection) {
            this.xslTemplates.restore(publicationXSL);
        }
        refreshXSLTemplatesList();
    }

    private void restoreGradingProperties() {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        Publication publication;

        try {
            publication = entityManager.find(Publication.class, publicationId);
            if (publication != null) {
                restoreGradingProperties(publication);
            }
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return;
        } finally {
            entityManager.close();
        }

    }


    private void saveXSLTemplateProperties() {
        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        Publication publication;

        try {
            publication = entityManager.find(Publication.class, publicationId);
            if (publication != null) {
                entityManager.getTransaction().begin();
                saveXSLTemplateProperties(entityManager, publication);
                entityManager.getTransaction().commit();
                String message = Localizer.localize("UI", "XSLTemplatesSavedSuccessfully");
                Misc.showSuccessMsg(this, message);
            }
        } catch (Exception ex) {
            Misc.showErrorMsg(this, ex.getLocalizedMessage());
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return;
        } finally {
            entityManager.close();
        }
    }

    private void saveXSLTemplateProperties(EntityManager entityManager, Publication publication)
        throws IOException {
        List<PublicationXSL> xslTemplateCollection = publication.getPublicationXSLCollection();
        if (xslTemplateCollection == null) {
            xslTemplateCollection = new ArrayList<PublicationXSL>();
        } else {

            ListModel xfl = this.xslFileList.getModel();

            ArrayList<Long> modifiedPublicationXSLIDs = new ArrayList<Long>();

            if (xfl.getSize() > 0) {
                for (int i = 0; i < xfl.getSize(); i++) {
                    XSLTemplate xslTemplate = (XSLTemplate) xfl.getElementAt(i);
                    boolean foundTemplate = false;

                    // update existing template records
                    for (PublicationXSL publicationXSL : xslTemplateCollection) {
                        if (xslTemplate.getPublicationXSLId() == publicationXSL
                            .getPublicationXSLId()) {
                            publicationXSL.setOrderIndex(i);
                            entityManager.persist(publicationXSL);
                            modifiedPublicationXSLIDs
                                .add(new Long(publicationXSL.getPublicationXSLId()));
                            foundTemplate = true;
                        }
                    }

                    // create new template record
                    if (foundTemplate == false) {
                        PublicationXSL publicationXSL = new PublicationXSL();
                        publicationXSL.setPublicationId(publication);
                        publicationXSL.setOrderIndex(i);
                        publicationXSL.setDescription(xslTemplate.getTemplateDescription());
                        publicationXSL.setFileName(xslTemplate.getFileName());
                        publicationXSL.setGuid(xslTemplate.getTemplateGUID());
                        publicationXSL.setXslData(getXSLData(xslTemplate.getFile()));
                        xslTemplateCollection.add(publicationXSL);
                        entityManager.persist(publicationXSL);
                        entityManager.flush();
                        modifiedPublicationXSLIDs
                            .add(new Long(publicationXSL.getPublicationXSLId()));
                    }

                }
            }

            // remove publications that aren't in the xslFileList anymore.
            for (PublicationXSL publicationXSL : xslTemplateCollection) {
                if (!(modifiedPublicationXSLIDs.contains(publicationXSL.getPublicationXSLId()))) {
                    entityManager.remove(publicationXSL);
                }
            }

            // save publication
            entityManager.persist(publication);
            entityManager.flush();

        }

    }

    private byte[] getXSLData(File file) throws IOException {
        return Misc.getBytesFromFile(file);
    }

    private void saveGradingProperties() {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        Publication publication;

        try {
            publication = entityManager.find(Publication.class, publicationId);
            if (publication != null) {
                entityManager.getTransaction().begin();
                saveGradingProperties(entityManager, publication);
                entityManager.getTransaction().commit();
                String message = Localizer.localize("UI", "GradingRulesSavedSuccessfully");
                Misc.showSuccessMsg(this, message);
            }
        } catch (Exception ex) {
            Misc.showErrorMsg(this, ex.getLocalizedMessage());
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return;
        } finally {
            entityManager.close();
        }

    }

    private void saveGradingProperties(EntityManager entityManager, Publication publication)
        throws Exception {

        List<Grading> gradingCollection = publication.getGradingCollection();
        if (gradingCollection == null) {
            gradingCollection = new ArrayList<Grading>();
        } else {
            for (Grading grading : gradingCollection) {
                entityManager.remove(grading);
            }
            gradingCollection = new ArrayList<Grading>();
        }

        ArrayList<MarkingRule> markingRules = new ArrayList<MarkingRule>();

        ListModel glm = this.gradingList.getModel();

        if (glm.getSize() > 0) {

            ArrayList<GradingRule> gradingRuleCollection = new ArrayList<GradingRule>();

            Grading grading = new Grading();
            grading.setPublicationId(publication);
            grading
                .setTotalPossibleScore(Misc.parseDoubleString(this.totalScoreTextField.getText()));
            grading.setGradingRuleCollection(gradingRuleCollection);
            entityManager.persist(grading);
            entityManager.flush();

            for (int i = 0; i < glm.getSize(); i++) {
                MarkingRule markingRule = (MarkingRule) glm.getElementAt(i);
                GradingRule gradingRule = markingRule.getGradingRule();
                gradingRule.setGradingId(grading);
                gradingRule.setOrderIndex(i);
                gradingRuleCollection.add(gradingRule);
                entityManager.persist(gradingRule);
            }

            entityManager.persist(grading);

            gradingCollection.add(grading);
            publication.setGradingCollection(gradingCollection);

        }

        entityManager.persist(publication);
        entityManager.flush();

    }

    private void restoreGradingProperties(Publication publication) {
        List<Grading> gradingCollection = publication.getGradingCollection();
        for (Grading grading : gradingCollection) {
            this.markingProperties.restore(grading);
            break; // only want first one - shouldn't be a collection (error in db design).
        }
        refreshGradingRuleList();
        this.totalScoreTextField.setText(markingProperties.getTotalPossibleScore() + "");
    }

    public void restorePublicationType() {
        PublicationPreferences publicationPreferences =
            PreferencesManager.getPublicationPreferences();
        List<String> publicationTypes = PublicationPreferences.getPublicationTypes();
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        for (String publicationTypeStr : publicationTypes) {
            dcbm.addElement(publicationTypeStr);
        }
        publicationTypeComboBox.setModel(dcbm);
        publicationTypeComboBox.setSelectedIndex(this.publicationType - 1);
        if (this.publicationType
            == PublicationPreferences.FORM_ID_RECONCILE_WITH_SOURCE_DATA_RECORD) {
            publicationTypeComboBox.setEnabled(false);
        } else {
            publicationTypeComboBox.setEnabled(true);
        }
    }

    public void restoreRecognitionSettings() {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        Publication publication;

        try {
            publication = entityManager.find(Publication.class, publicationId);
            if (publication != null) {
                luminanceThresholdSpinner
                    .setValue(new Integer(publication.getLuminanceThreshold()));
                markThresholdSpinner.setValue(new Integer(publication.getMarkThreshold()));
                fragmentPaddingSpinner.setValue(new Integer(publication.getFragmentPadding()));
                deskewThresholdSpinner.setValue(new Double(publication.getDeskewThreshold()));
                performDeskewCheckBox
                    .setSelected(publication.getPerformDeskew() == 1 ? true : false);
                scannedInOrderCheckBox
                    .setSelected(publication.getScannedInOrder() == 1 ? true : false);
            }
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return;
        } finally {
            entityManager.close();
        }

    }

    private void updateRecognitionStructureButtonActionPerformed(ActionEvent e) {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        Publication publication;

        try {
            publication = entityManager.find(Publication.class, publicationId);
            entityManager.getTransaction().begin();
            entityManager.flush();

            if (publication != null) {

                publication.setMarkThreshold((short) getMarkThreshold());
                publication.setLuminanceThreshold((short) getLuminance());
                publication.setFragmentPadding((short) getFragmentPadding());
                publication.setDeskewThreshold(getDeskewThreshold());

                publication.setPerformDeskew((short) (isPerformDeskew() ? 1 : 0));
                entityManager.persist(publication);

            }

            entityManager.getTransaction().commit();

            restoreRecognitionSettings();

            Misc.showSuccessMsg(Main.getInstance(), "Recognition Settings Updated Successfully");

        } catch (Exception ex) {

            Misc.showErrorMsg(Main.getInstance(), "Error Updating Recognition Settings");

            if (entityManager.getTransaction().isActive()) {
                try {
                    entityManager.getTransaction().rollback();
                } catch (Exception rbex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(rbex);
                }
            }

            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return;

        } finally {
            entityManager.close();
        }

    }

    private void closeButtonActionPerformed(ActionEvent e) {
        closeWindow();
    }

    private File selectTemplateFile() {
        File file = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();
        filter.addExtension("frf");

        FileDialog fd = new FileDialog(Main.getInstance(),
            Localizer.localize("UICDM", "SaveFormTemplateFileDialogTitle"), FileDialog.SAVE);
        fd.setFilenameFilter(filter);
        fd.setFile(
            Localizer.localize("UICDM", "PublicationTemplateFilenamePrefix") + publicationIds[0]
                + ".frf");
        File lastDir = null;
        if (Globals.getLastDirectory() != null) {

            lastDir = new File(Globals.getLastDirectory());

            if (!(lastDir.exists())) {
                lastDir = null;
            }

        }

        try {
            if (lastDir == null) {
                lastDir = new File(System.getProperty("user.home"));
            }
            fd.setDirectory(lastDir.getCanonicalPath());
        } catch (IOException e1) {
            fd.setDirectory(".");
        }
        fd.setModal(true);
        fd.setVisible(true);
        if (fd.getFile() != null) {

            String filename = fd.getFile();
            if (!(filename.endsWith(".frf") || filename.endsWith(".FRF"))) {
                filename += ".frf";
            }

            file = new File(fd.getDirectory() + filename);

            if (file.isDirectory()) {
                return null;
            }

        }

        return file;

    }

    private void downloadPDFButtonActionPerformed(ActionEvent e) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                Object[] options =
                    {Localizer.localize("UI", "PublicationPropertiesCollateFormsOption"),
                        Localizer.localize("UI", "PublicationPropertiesExportIndividuallyOption")};

                String msg = Localizer.localize("UI", "PublicationPropertiesCollateFormsMessage");

                int result = JOptionPane.showOptionDialog(Main.getInstance(), msg,
                    Localizer.localize("UI", "PublicationPropertiesCollateFormsTitle"),
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
                    options[0]);

                if (result == 0) {
                    FormPDFGenerator
                        .exportPublishedForms(publicationId, FormPublisher.COLLATED_FORMS, false,
                            getRootPane());
                } else {
                    FormPDFGenerator
                        .exportPublishedForms(publicationId, FormPublisher.SEPARATED_FORMS, false,
                            getRootPane());
                }

            }
        });

    }

    public int getLuminance() {
        return (Integer) luminanceThresholdSpinner.getValue();
    }

    public int getMarkThreshold() {
        return (Integer) markThresholdSpinner.getValue();
    }

    public int getFragmentPadding() {
        return (Integer) fragmentPaddingSpinner.getValue();
    }

    public double getDeskewThreshold() {
        return (Double) deskewThresholdSpinner.getValue();
    }

    public boolean isPerformDeskew() {
        return performDeskewCheckBox.isSelected();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                closeButton.requestFocusInWindow();
            }
        });
    }

    private void importAnswerKeyButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                CSVReader reader;
                File CSVFile = null;
                try {
                    CSVFile = selectAnswerKeyImportFile();
                    if (CSVFile != null) {
                        reader = new CSVReader(
                            new InputStreamReader(new FileInputStream(CSVFile), "UTF-8"),
                            ",".charAt(0), "\"".charAt(0));
                        importAnswerKey(reader);
                        String message =
                            Localizer.localize("UICDM", "AnswerKeyFileImportedMessage");
                        String caption = Localizer.localize("UICDM", "SuccessTitle");
                        javax.swing.JOptionPane
                            .showConfirmDialog(Main.getInstance(), message, caption,
                                javax.swing.JOptionPane.DEFAULT_OPTION,
                                javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e1) {
                    String message =
                        Localizer.localize("UICDM", "FailureImportingAnswerKeyFileMessage");
                    String caption = Localizer.localize("UICDM", "ErrorTitle");
                    javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
                        javax.swing.JOptionPane.DEFAULT_OPTION,
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                }

            }

        });
    }

    private void importAnswerKey(CSVReader reader) throws IOException {

        List<String[]> lines = reader.readAll();
        for (String[] line : lines) {
            for (int i = 0; i < formStructureTable.getRowCount(); i++) {
                if (formStructureTable.getValueAt(i, TYPE_COLUMN)
                    .equals(Localizer.localize("Util", "Checkbox"))) {
                    if (line[0].trim()
                        .equals((String) formStructureTable.getValueAt(i, OLD_FIELD_NAME_COLUMN))) {
                        formStructureTable.setValueAt(line[1].trim(), i, NEW_AGGREGATE_RULE_COLUMN);
                    }
                }
            }
        }

        try {
            reader.close();
        } catch (IOException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }

    }

    private File selectAnswerKeyImportFile() {

        File file = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();
        filter.addExtension("csv");

        FileDialog fd = new FileDialog(Main.getInstance(),
            Localizer.localize("UICDM", "ImportAnswerKeyFileMessage"), FileDialog.LOAD);
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

        try {
            fd.setDirectory(lastDir.getCanonicalPath());
        } catch (IOException e1) {
            fd.setDirectory(".");
        }
        fd.setModal(true);
        fd.setVisible(true);
        if (fd.getFile() != null) {

            String filename = fd.getFile();
            if (!(filename.endsWith(".csv") || filename.endsWith(".CSV"))) {
                filename += ".csv";
            }

            file = new File(fd.getDirectory() + filename);

            if (file.isDirectory()) {
                return null;
            }

        }

        return file;
    }

    private File selectAnswerKeyExportFile() {

        File file = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();
        filter.addExtension("csv");

        FileDialog fd = new FileDialog(Main.getInstance(),
            Localizer.localize("UICDM", "SaveAnswerKeyFileMessage"), FileDialog.SAVE);
        fd.setFilenameFilter(filter);
        fd.setFile(Localizer.localize("UICDM", "AnswerKeyFilePrefix") + ".csv");
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

        try {
            fd.setDirectory(lastDir.getCanonicalPath());
        } catch (IOException e1) {
            fd.setDirectory(".");
        }
        fd.setModal(true);
        fd.setVisible(true);
        if (fd.getFile() != null) {

            String filename = fd.getFile();
            if (!(filename.endsWith(".csv") || filename.endsWith(".CSV"))) {
                filename += ".csv";
            }

            file = new File(fd.getDirectory() + filename);

            if (file.isDirectory()) {
                return null;
            }

        }

        return file;
    }

    private void exportAnswerKeyButtonActionPerformed(ActionEvent e) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                CSVWriter writer;
                File CSVFile = null;
                try {
                    CSVFile = selectAnswerKeyExportFile();
                    if (CSVFile != null) {
                        writer = new CSVWriter(
                            new OutputStreamWriter(new FileOutputStream(CSVFile), "UTF-8"),
                            ",".charAt(0), "\"".charAt(0));
                        exportAnswerKey(writer);
                        String message = Localizer.localize("UICDM", "AnswerKeyFileSavedMessage");
                        String caption = Localizer.localize("UICDM", "SuccessTitle");
                        javax.swing.JOptionPane
                            .showConfirmDialog(Main.getInstance(), message, caption,
                                javax.swing.JOptionPane.DEFAULT_OPTION,
                                javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e1) {
                    String message =
                        Localizer.localize("UICDM", "FailureSavingAnswerKeyFileMessage");
                    String caption = Localizer.localize("UICDM", "ErrorTitle");
                    javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
                        javax.swing.JOptionPane.DEFAULT_OPTION,
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                }

            }
        });

    }

    private void exportAnswerKey(CSVWriter writer) {

        for (int i = 0; i < formStructureTable.getRowCount(); i++) {

            if (formStructureTable.getValueAt(i, TYPE_COLUMN)
                .equals(Localizer.localize("Util", "Checkbox"))) {
                writer.writeNext(
                    new String[] {(String) formStructureTable.getValueAt(i, OLD_FIELD_NAME_COLUMN),
                        (String) formStructureTable.getValueAt(i, OLD_AGGREGATE_RULE_COLUMN)});
            }

        }

        try {
            writer.close();
        } catch (IOException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }

    }

    private void updateStructureButtonActionPerformed(ActionEvent e) {

        // 0.1 validate that all fieldnames are unique

        ArrayList<String> fieldnames = new ArrayList<String>();
        for (int i = 0; i < formStructureTable.getRowCount(); i++) {
            if (fieldnames
                .contains((String) formStructureTable.getValueAt(i, NEW_FIELD_NAME_COLUMN))) {
                Misc.showErrorMsg(Main.getInstance(),
                    "Duplicate field names found. Make sure each field name is unique.");
                return;
            }
            fieldnames.add((String) formStructureTable.getValueAt(i, NEW_FIELD_NAME_COLUMN));
        }
        fieldnames = null;

        // 1. get a list of differences

        // 1.1 compare aggregation rules
        Map<String, String[]> aggregationRuleDifferences = new HashMap<String, String[]>();
        for (int i = 0; i < formStructureTable.getRowCount(); i++) {
            String oldAggregationRule =
                (String) formStructureTable.getValueAt(i, OLD_AGGREGATE_RULE_COLUMN);
            String newAggregationRule =
                (String) formStructureTable.getValueAt(i, NEW_AGGREGATE_RULE_COLUMN);
            if (!(oldAggregationRule.equals(newAggregationRule))) {
                String fieldname = (String) formStructureTable.getValueAt(i, OLD_FIELD_NAME_COLUMN);
                aggregationRuleDifferences
                    .put(fieldname, new String[] {oldAggregationRule, newAggregationRule});
            }
        }

        // 1.2 compare order indexes
        Map<String, String[]> orderIndexDifferences = new HashMap<String, String[]>();
        for (int i = 0; i < formStructureTable.getRowCount(); i++) {
            String oldOrderIndex =
                (String) formStructureTable.getValueAt(i, OLD_ORDER_INDEX_COLUMN);
            String newOrderIndex =
                (String) formStructureTable.getValueAt(i, NEW_ORDER_INDEX_COLUMN);

            if (!(oldOrderIndex.equals(newOrderIndex))) {
                String fieldname = (String) formStructureTable.getValueAt(i, OLD_FIELD_NAME_COLUMN);

                // 1.2.1 VALIDATE NEW ORDER INDEXES
                if (!validOrderIndex(newOrderIndex)) {
                    Misc.showErrorMsg(Main.getInstance(),
                        "Invalid order index for fieldname: " + fieldname);
                    return;
                }
                orderIndexDifferences.put(fieldname,
                    new String[] {oldOrderIndex, Misc.parseLongString(newOrderIndex) + ""});
            }
        }

        // 1.3 compare fieldnames
        Map<String, String[]> fieldNameDifferences = new HashMap<String, String[]>();
        for (int i = 0; i < formStructureTable.getRowCount(); i++) {
            String oldFieldName = (String) formStructureTable.getValueAt(i, OLD_FIELD_NAME_COLUMN);
            String newFieldName = (String) formStructureTable.getValueAt(i, NEW_FIELD_NAME_COLUMN);

            if (!(oldFieldName.equals(newFieldName))) {
                String fieldname = (String) formStructureTable.getValueAt(i, OLD_FIELD_NAME_COLUMN);

                // 1.3.1 VALIDATE NEW FIELDNAMES
                if (!validFieldName(newFieldName)) {
                    Misc.showErrorMsg(Main.getInstance(),
                        "Invalid new fieldname for fieldname: " + fieldname);
                    return;
                }

                fieldNameDifferences.put(fieldname, new String[] {oldFieldName, newFieldName});
            }
        }

        // 2. list the differences and confirm the update in a yes/no dialog

        // list changes in a message string
        String message = "";
        if (fieldNameDifferences.size() > 0) {
            message += "Field Name Differences:\n";
            for (String[] fieldNames : fieldNameDifferences.values()) {
                message +=
                    "Current Field Name: " + fieldNames[0] + " - New Field Name: " + fieldNames[1]
                        + "\n";
            }
            message += "\n";
        }

        if (orderIndexDifferences.size() > 0) {
            message += "Order Index Differences:\n";
            Iterator<String> oidki = orderIndexDifferences.keySet().iterator();
            while (oidki.hasNext()) {
                String fieldname = oidki.next();
                String[] orderIndexes = orderIndexDifferences.get(fieldname);
                message += "Field Name: " + fieldname + " - Current Order Index: " + orderIndexes[0]
                    + " - New Order Index: " + orderIndexes[1] + "\n";
            }
            message += "\n";
        }

        if (aggregationRuleDifferences.size() > 0) {
            message += "Aggregation Rule Differences:\n";
            Iterator<String> ardki = aggregationRuleDifferences.keySet().iterator();
            while (ardki.hasNext()) {
                String fieldname = ardki.next();
                String[] aggregationRules = aggregationRuleDifferences.get(fieldname);
                message += "Field Name: " + fieldname + " - Current Aggregation Rule: "
                    + aggregationRules[0] + " - New Aggregation Rule: " + aggregationRules[1]
                    + "\n";
            }
        }

        ConfirmStructureChangesDialog cscd = new ConfirmStructureChangesDialog(Main.getInstance());
        cscd.setConfirmMessage(message);
        cscd.setModal(true);
        cscd.setVisible(true);
        if (cscd.getDialogResult() != JOptionPane.OK_OPTION) {
            return;
        }

        // 3. loop through each "old" fieldname, update everything else first except the name, then update the name
        // if the name is different too

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        Publication publication;

        try {

            publication = entityManager.find(Publication.class, publicationId);

            entityManager.getTransaction().begin();

            // flush changes
            entityManager.flush();

            // SAVE GRAPH FIRST
            Map<String, Page> pages = graph.getDocument().getPages();

            if (pages.size() <= 0) {
                return;
            }

            for (Page page : pages.values()) {
                ArrayList<Fig> figs = page.getFigs();
                if (figs.size() > 0) {
                    for (Fig fig : figs) {

                        if (fig instanceof FigSegment) {

                            FigSegment figSegment = (FigSegment) fig;

                            ArrayList<com.ebstrada.formreturn.manager.persistence.xstream.Document>
                                segments = figSegment.getSegmentContainer().getSegments();

                            for (com.ebstrada.formreturn.manager.persistence.xstream.Document segmentDocument : segments) {
                                for (Page segmentPage : segmentDocument.getPages().values()) {
                                    ArrayList<Fig> segmentFigs = segmentPage.getFigs();

                                    for (Fig segmentFig : segmentFigs) {
                                        if (segmentFig instanceof FigBarcodeReader) {

                                            FigBarcodeReader figBarcodeReader =
                                                (FigBarcodeReader) segmentFig;

                                            String fieldname = figBarcodeReader.getFieldname();

                                            // check order index
                                            if (orderIndexDifferences.containsKey(fieldname)) {
                                                figBarcodeReader.setFieldnameOrderIndex(
                                                    Misc.parseIntegerString(
                                                        orderIndexDifferences.get(fieldname)[1]));
                                            }

                                        }
                                        if (segmentFig instanceof FigCheckbox) {

                                            FigCheckbox figCheckbox = (FigCheckbox) segmentFig;

                                            String fieldname = figCheckbox.getFieldname();
                                            String markFieldname = figCheckbox.getMarkFieldname();
                                            if (markFieldname == null
                                                || markFieldname.trim().length() <= 0) {
                                                markFieldname = fieldname + Localizer
                                                    .localize("UICDM", "MarkColumnNameSuffix");
                                            }

                                            // check order index
                                            if (orderIndexDifferences.containsKey(fieldname)) {
                                                figCheckbox.setFieldnameOrderIndex(
                                                    Misc.parseIntegerString(
                                                        orderIndexDifferences.get(fieldname)[1]));
                                            }

                                            // check aggregation rule
                                            if (aggregationRuleDifferences
                                                .containsKey(figCheckbox.getFieldname())) {
                                                figCheckbox.setAggregationRule(
                                                    aggregationRuleDifferences.get(fieldname)[1]);
                                            }

                                            // check mark order index
                                            if (orderIndexDifferences.containsKey(markFieldname)) {
                                                figCheckbox.setMarkFieldnameOrderIndex(
                                                    Misc.parseIntegerString(orderIndexDifferences
                                                        .get(markFieldname)[1]));
                                            }

                                        }
                                    }

                                    for (Fig segmentFig : segmentFigs) {
                                        if (segmentFig instanceof FigBarcodeReader) {

                                            FigBarcodeReader figBarcodeReader =
                                                (FigBarcodeReader) segmentFig;
                                            String fieldname = figBarcodeReader.getFieldname();

                                            // check fieldname
                                            if (fieldNameDifferences.containsKey(fieldname)) {
                                                figBarcodeReader.setFieldname(
                                                    fieldNameDifferences.get(fieldname)[1]);
                                            }
                                        }
                                        if (segmentFig instanceof FigCheckbox) {

                                            FigCheckbox figCheckbox = (FigCheckbox) segmentFig;

                                            String fieldname = figCheckbox.getFieldname();
                                            String markFieldname = figCheckbox.getMarkFieldname();
                                            if (markFieldname == null
                                                || markFieldname.trim().length() <= 0) {
                                                markFieldname = fieldname + Localizer
                                                    .localize("UICDM", "MarkColumnNameSuffix");
                                            }

                                            // check fieldname
                                            if (fieldNameDifferences.containsKey(fieldname)) {
                                                figCheckbox.setFieldname(
                                                    fieldNameDifferences.get(fieldname)[1]);
                                            }

                                            // check mark fieldname
                                            if (fieldNameDifferences.containsKey(markFieldname)) {
                                                figCheckbox.setMarkFieldname(
                                                    fieldNameDifferences.get(markFieldname)[1]);
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }

            // throws exception
            graph.getDocumentPackage().save();

            // throws exception
            publication.setTemplateFile(Misc.getBytesFromFile(templateFile));


            // DELETE TEMPLATE FILE
            templateFile.delete();

            List<Form> fc = publication.getFormCollection();

            for (Form form : fc) {

                List<FormPage> fpc = form.getFormPageCollection();

                for (FormPage formPage : fpc) {
                    List<Segment> sc = formPage.getSegmentCollection();
                    if (sc.size() > 0) {
                        for (Segment segment : sc) {
                            List<FragmentBarcode> fbc = segment.getFragmentBarcodeCollection();
                            if (fbc.size() > 0) {
                                for (FragmentBarcode fragmentBarcode : fbc) {

                                    String fieldname = fragmentBarcode.getCapturedDataFieldName();

                                    // check order index
                                    if (orderIndexDifferences.containsKey(fieldname)) {
                                        fragmentBarcode.setOrderIndex(Misc.parseLongString(
                                            orderIndexDifferences.get(fieldname)[1]));
                                    }

                                    entityManager.persist(fragmentBarcode);

                                }

                                for (FragmentBarcode fragmentBarcode : fbc) {

                                    String fieldname = fragmentBarcode.getCapturedDataFieldName();

                                    // check fieldname
                                    if (fieldNameDifferences.containsKey(fieldname)) {
                                        fragmentBarcode.setCapturedDataFieldName(
                                            fieldNameDifferences.get(fieldname)[1]);
                                    }

                                    entityManager.persist(fragmentBarcode);

                                }

                            }
                            List<FragmentOmr> foc = segment.getFragmentOmrCollection();
                            if (foc.size() > 0) {
                                for (FragmentOmr fragmentOmr : foc) {

                                    String fieldname = fragmentOmr.getCapturedDataFieldName();
                                    String markFieldname = fragmentOmr.getMarkColumnName();
                                    if (markFieldname == null
                                        || markFieldname.trim().length() <= 0) {
                                        markFieldname = fieldname + Localizer
                                            .localize("UICDM", "MarkColumnNameSuffix");
                                    }

                                    // check order index
                                    if (orderIndexDifferences.containsKey(fieldname)) {
                                        fragmentOmr.setOrderIndex(Misc.parseLongString(
                                            orderIndexDifferences.get(fieldname)[1]));
                                    }

                                    // check aggregation rule
                                    if (aggregationRuleDifferences
                                        .containsKey(fragmentOmr.getCapturedDataFieldName())) {
                                        fragmentOmr.setAggregationRule(
                                            aggregationRuleDifferences.get(fieldname)[1]);
                                    }

                                    // check mark order index
                                    if (orderIndexDifferences.containsKey(markFieldname)) {
                                        fragmentOmr.setMarkOrderIndex(Misc.parseLongString(
                                            orderIndexDifferences.get(markFieldname)[1]));
                                    }

                                    entityManager.persist(fragmentOmr);

                                }

                                for (FragmentOmr fragmentOmr : foc) {

                                    String fieldname = fragmentOmr.getCapturedDataFieldName();
                                    String markFieldname = fragmentOmr.getMarkColumnName();
                                    if (markFieldname == null
                                        || markFieldname.trim().length() <= 0) {
                                        markFieldname = fieldname + Localizer
                                            .localize("UICDM", "MarkColumnNameSuffix");
                                    }

                                    // check fieldname
                                    if (fieldNameDifferences.containsKey(fieldname)) {
                                        fragmentOmr.setCapturedDataFieldName(
                                            fieldNameDifferences.get(fieldname)[1]);
                                    }

                                    // check mark fieldname
                                    if (fieldNameDifferences.containsKey(markFieldname)) {
                                        fragmentOmr.setMarkColumnName(
                                            fieldNameDifferences.get(markFieldname)[1]);
                                    }

                                    entityManager.persist(fragmentOmr);

                                }

                            }
                        }
                    }
                }
            }

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            if (entityManager.getTransaction().isActive()) {
                try {
                    entityManager.getTransaction().rollback();
                } catch (Exception rbex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(rbex);
                }
            }
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        } finally {
            entityManager.close();
        }

        // 4. reload the structure
        restoreFormStructureTable();

        // 5. show success or failure message
        Misc.showSuccessMsg(Main.getInstance(), "Form Structure Updated Successfully");

    }

    private File getTemplateFileAndPublicationType() throws Exception {

        String workingDirectory = PreferencesManager.getWorkingDirectory().getCanonicalPath();

        File publicationTemplateFile = new File(
            workingDirectory + System.getProperty("file.separator") + "template"
                + (new RandomGUID()).toString());
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(publicationTemplateFile);

            EntityManager entityManager =
                Main.getInstance().getJPAConfiguration().getEntityManager();

            if (entityManager == null) {
                throw new Exception();
            }

            Publication publication;

            byte[] templateFileData = null;
            try {
                publication = entityManager.find(Publication.class, publicationId);
                templateFileData = publication.getTemplateFile();
                this.publicationType = publication.getPublicationType();
            } catch (Exception ex) {
                throw ex;
            } finally {
                entityManager.close();
            }
            fos.write(templateFileData);
            fos.close();
        } catch (Exception e1) {
            throw e1;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                }
            }

        }

        return publicationTemplateFile;

    }

    private boolean validFieldName(String newFieldName) {
        return Misc.validateFieldname(newFieldName);
    }

    private boolean validOrderIndex(String newOrderIndex) {
        long orderIndex = Misc.parseLongString(newOrderIndex);
        if (orderIndex < 0 || orderIndex > 1000000) {
            return false;
        } else {
            return true;
        }
    }

    private void reloadStructureButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                restoreFormStructureTable();
            }
        });
    }

    private void reloadRecognitionSettingsButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                restoreRecognitionSettings();
            }
        });
    }

    private void closeWindow() {
        setDialogResult(JOptionPane.CLOSED_OPTION);
        dispose();
    }

    private void thisWindowClosing(WindowEvent e) {
        closeWindow();
    }

    private void scannedInOrderCheckBoxActionPerformed(ActionEvent e) {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        Publication publication;

        try {

            publication = entityManager.find(Publication.class, publicationId);
            if (publication != null) {
                publication
                    .setScannedInOrder((short) (scannedInOrderCheckBox.isSelected() ? 1 : 0));
                entityManager.getTransaction().begin();
                entityManager.flush();
                entityManager.persist(publication);
                entityManager.getTransaction().commit();
            }
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return;
        } finally {
            entityManager.close();
        }

    }

    private void recalculateScoresButtonActionPerformed(ActionEvent e) {

        final EntityManager entityManager =
            Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        final Publication publication = entityManager.find(Publication.class, publicationId);
        if (publication == null) {
            return;
        }

        final ProcessingStatusDialog messageNotification =
            new ProcessingStatusDialog((JDialog) getRootPane().getTopLevelAncestor());

        recalculateScoresWorker = new SwingWorker<Void, Void>() {

            public Void doInBackground() throws Exception {
                messageNotification.setVisible(true);
                AggregateCalculator
                    .recalculateAggregate(entityManager, publication, messageNotification);
                return null;
            }

            public void done() {
                try {
                    get();
                    Misc.showSuccessMsg(getRootPane().getTopLevelAncestor(),
                        Localizer.localize("UI", "RecalculateScoresSuccessMessage"));
                } catch (InterruptedException e) {
                    Misc.printStackTrace(e);
                    Misc.showErrorMsg(getRootPane().getTopLevelAncestor(),
                        Localizer.localize("UI", "FailedToRecalculateScoresMessage"));
                } catch (ExecutionException e) {
                    Misc.printStackTrace(e);
                    Misc.showErrorMsg(getRootPane().getTopLevelAncestor(),
                        Localizer.localize("UI", "FailedToRecalculateScoresMessage"));
                } finally {
                    messageNotification.dispose();
                }
            }
        };

        recalculateScoresWorker.execute();

    }

    private void downloadTemplateButtonActionPerformed(ActionEvent e) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                File publicationTemplateFile = selectTemplateFile();

                if (publicationTemplateFile == null) {
                    return;
                }

                FileOutputStream fos = null;

                try {
                    fos = new FileOutputStream(publicationTemplateFile);
                    EntityManager entityManager =
                        Main.getInstance().getJPAConfiguration().getEntityManager();

                    if (entityManager == null) {
                        return;
                    }

                    Publication publication;
                    byte[] templateFileData = null;
                    try {
                        publication = entityManager.find(Publication.class, publicationId);
                        templateFileData = publication.getTemplateFile();
                    } catch (Exception ex) {
                        throw ex;
                    } finally {
                        entityManager.close();
                    }
                    fos.write(templateFileData);
                    fos.close();
                    Misc.showSuccessMsg(Main.getInstance(),
                        Localizer.localize("UICDM", "FormFileSavedSuccessfullyMessage"));
                } catch (Exception e1) {
                    Misc.showErrorMsg(Main.getInstance(),
                        Localizer.localize("UICDM", "ErrorSavingFormFileMessage"));
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e1) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                        }
                    }

                }
            }
        });

    }

    private void uploadImageButtonActionPerformed(ActionEvent e) {
        if (getPublicationType()
            == PublicationPreferences.FORM_ID_RECONCILE_WITH_SOURCE_DATA_RECORD) {
            FormIDPublicationRecovery fidpr = new FormIDPublicationRecovery(this);
            fidpr.setPublicationId(this.publicationId);
            fidpr.setVisible(true);
            fidpr.setModal(true);
            if (fidpr.getDialogResult() == JOptionPane.OK_OPTION) {
                recoverFormIDPublication(fidpr.getFirstFormPageID());
            }
        } else {
            recoverKeyFieldPublication();
        }

    }

    private void recoverKeyFieldPublication() {

        TemplateFormPageID tfpid;
        try {
            tfpid = getTemplateFormPageId();
        } catch (Exception e1) {
            Misc.showErrorMsg(this, e1.getLocalizedMessage());
            return;
        }

        JPAConfiguration jpaConfiguration = Main.getInstance().getJPAConfiguration();
        File file;

        try {
            file = Misc.getUploadImageFile();
            if (file == null) {
                return;
            }
            Misc.uploadImage(jpaConfiguration, file, tfpid, this);
        } catch (IOException e) {
            Misc.showErrorMsg(this, e.getLocalizedMessage());
            Misc.printStackTrace(e);
        }

    }

    private TemplateFormPageID getTemplateFormPageId() throws Exception {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
        if (entityManager == null) {
            throw new Exception("Null entityManager");
        }

        try {
            return getTemplateFormPageId(entityManager);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if ((entityManager != null) && entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

    protected TemplateFormPageID getTemplateFormPageId(EntityManager entityManager) {

        Publication publication = entityManager.find(Publication.class, publicationId);
        Query query = entityManager.createNamedQuery("Form.findTemplateByPublicationId");
        query.setParameter("publicationId", publication);
        List<Form> forms = query.getResultList();

        TemplateFormPageID tfpid = new TemplateFormPageID();

        for (Form form : forms) {
            List<FormPage> formPageCollection = form.getFormPageCollection();

            if (formPageCollection.size() > 1) {
                MutliFormPageImageUploadDialog mfpiud =
                    new MutliFormPageImageUploadDialog(this, formPageCollection.size());
                mfpiud.setVisible(true);
                if (mfpiud.getDialogResult() == JOptionPane.OK_OPTION) {
                    int startPage = mfpiud.getStartPage();
                    tfpid.setStartPage(startPage);
                }
            }

            for (FormPage formPage : formPageCollection) {
                if (formPage.getFormPageNumber() == tfpid.getStartPage()) {
                    tfpid.setFirstFormPageId(formPage.getFormPageId());
                    return tfpid;
                }
            }
        }

        return tfpid;

    }

    private void recoverFormIDPublication(long firstFormPageID) {

        TemplateFormPageID tfpid = new TemplateFormPageID();
        tfpid.setFirstFormPageId(firstFormPageID);
        tfpid.setStartPage(1);

        JPAConfiguration jpaConfiguration = Main.getInstance().getJPAConfiguration();
        File file;

        try {
            file = Misc.getUploadImageFile();
            if (file == null) {
                return;
            }
            Misc.uploadImage(jpaConfiguration, file, tfpid, this);
        } catch (IOException e) {
            Misc.showErrorMsg(this, e.getLocalizedMessage());
            Misc.printStackTrace(e);
        }

    }

    private int getPublicationType() {
        return this.publicationType;
    }

    private void publicationTypeComboBoxItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if ((publicationTypeComboBox.getSelectedIndex() + 1)
                == PublicationPreferences.FORM_ID_RECONCILE_WITH_SOURCE_DATA_RECORD) {
                publicationTypeComboBox.setSelectedIndex(this.publicationType - 1);
                Misc.showErrorMsg(this,
                    Localizer.localize("UICDM", "CannotChangePublicationTypeMessage"));
            } else {
                if (this.publicationType == (publicationTypeComboBox.getSelectedIndex() + 1)) {
                    return; // skip because already changed.
                }
                try {
                    EntityManager entityManager =
                        Main.getInstance().getJPAConfiguration().getEntityManager();
                    if (entityManager == null) {
                        throw new Exception();
                    }
                    Publication publication;
                    try {
                        publication = entityManager.find(Publication.class, publicationId);
                        entityManager.getTransaction().begin();
                        entityManager.flush();
                        publication.setPublicationType(
                            (short) (publicationTypeComboBox.getSelectedIndex() + 1));
                        this.publicationType = (publicationTypeComboBox.getSelectedIndex() + 1);
                        entityManager.persist(publication);
                        entityManager.getTransaction().commit();
                    } catch (Exception ex) {
                        throw ex;
                    } finally {
                        entityManager.close();
                    }
                } catch (Exception e1) {
                    Misc.showErrorMsg(this,
                        Localizer.localize("UICDM", "ErrorChangingPublicationTypeMessage"));
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                }
            }
        }
    }

    private void gradingRuleUpButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MarkingRule gradingRule = (MarkingRule) gradingList.getSelectedValue();

                if (gradingRule == null) {
                    return;
                }

                ArrayList<MarkingRule> gradingRules = markingProperties.getGradingRules();

                if (gradingRules.contains(gradingRule)) {
                    int selectedIndex = gradingRules.indexOf(gradingRule);
                    if (selectedIndex > 0) {
                        Collections.swap(gradingRules, selectedIndex, selectedIndex - 1);
                    }
                }

                refreshGradingRuleList();
            }
        });
    }

    private void gradingRuleDownButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MarkingRule gradingRule = (MarkingRule) gradingList.getSelectedValue();

                if (gradingRule == null) {
                    return;
                }

                ArrayList<MarkingRule> gradingRules = markingProperties.getGradingRules();

                if (gradingRules.contains(gradingRule)) {
                    int selectedIndex = gradingRules.indexOf(gradingRule);
                    if (selectedIndex < (gradingRules.size() - 1)) {
                        Collections.swap(gradingRules, selectedIndex, selectedIndex + 1);
                    }
                }

                refreshGradingRuleList();
            }
        });
    }

    private void addGradingRuleButtonActionPerformed(ActionEvent e) {
        final Dialog thisDialog = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                MarkingRule gradingRule = new MarkingRule();

                GradingRuleDialog grd = new GradingRuleDialog(thisDialog, gradingRule);
                grd.setModal(true);
                grd.setVisible(true);
                if (grd.getDialogResult() != JOptionPane.CANCEL_OPTION) {

                    String grade = grd.getGrade();
                    int qualifier = grd.getQualifier();
                    double threshold = grd.getThreshold();
                    int thresholdType = grd.getThresholdType();

                    gradingRule.setGrade(grade);
                    gradingRule.setQualifier(qualifier);
                    gradingRule.setThreshold(threshold);
                    gradingRule.setThresholdType(thresholdType);

                    markingProperties.getGradingRules().add(gradingRule);

                    refreshGradingRuleList();

                }
            }
        });
    }

    private void refreshGradingRuleList() {
        this.gradingList.setModel(markingProperties.getGradingRulesListModel());
        this.gradingList.validate();
    }

    private void refreshXSLTemplatesList() {
        this.xslFileList.setModel(this.xslTemplates.getXSLTemplatesListModel());
        this.xslFileList.validate();
    }

    private void editGradingRuleButtonActionPerformed(ActionEvent e) {
        final Dialog thisDialog = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                MarkingRule gradingRule = (MarkingRule) gradingList.getSelectedValue();

                if (gradingRule == null) {
                    return;
                }

                GradingRuleDialog grd = new GradingRuleDialog(thisDialog, gradingRule);
                grd.setModal(true);
                grd.setVisible(true);
                if (grd.getDialogResult() != JOptionPane.CANCEL_OPTION) {

                    String grade = grd.getGrade();
                    int qualifier = grd.getQualifier();
                    double threshold = grd.getThreshold();
                    int thresholdType = grd.getThresholdType();

                    gradingRule.setGrade(grade);
                    gradingRule.setQualifier(qualifier);
                    gradingRule.setThreshold(threshold);
                    gradingRule.setThresholdType(thresholdType);

                    ArrayList<MarkingRule> gradingRules = markingProperties.getGradingRules();

                    if (!(gradingRules.contains(gradingRule))) {
                        gradingRules.add(gradingRule);
                    }

                    refreshGradingRuleList();

                }
            }
        });
    }

    private void removeGradingRuleButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MarkingRule gradingRule = (MarkingRule) gradingList.getSelectedValue();

                if (gradingRule == null) {
                    return;
                }

                ArrayList<MarkingRule> gradingRules = markingProperties.getGradingRules();

                if (gradingRules.contains(gradingRule)) {
                    gradingRules.remove(gradingRule);
                }

                refreshGradingRuleList();
            }
        });
    }

    private void revertChangesGradingButtonActionPerformed(ActionEvent e) {
        restoreGradingProperties();
    }

    private void saveGradingChangesButtonActionPerformed(ActionEvent e) {
        saveGradingProperties();
    }

    private void xslFileUpButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                XSLTemplate xslTemplate = (XSLTemplate) xslFileList.getSelectedValue();

                if (xslTemplate == null) {
                    return;
                }

                ArrayList<XSLTemplate> templates = xslTemplates.getXSLTemplates();

                if (templates.contains(xslTemplate)) {
                    int selectedIndex = templates.indexOf(xslTemplate);
                    if (selectedIndex > 0) {
                        Collections.swap(templates, selectedIndex, selectedIndex - 1);
                    }
                }

                refreshXSLTemplatesList();
            }
        });
    }

    private void xslFileDownButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                XSLTemplate xslTemplate = (XSLTemplate) xslFileList.getSelectedValue();

                if (xslTemplate == null) {
                    return;
                }

                ArrayList<XSLTemplate> templates = xslTemplates.getXSLTemplates();

                if (templates.contains(xslTemplate)) {
                    int selectedIndex = templates.indexOf(xslTemplate);
                    if (selectedIndex < (templates.size() - 1)) {
                        Collections.swap(templates, selectedIndex, selectedIndex + 1);
                    }
                }

                refreshXSLTemplatesList();
            }
        });
    }

    private void addXSLFileButtonActionPerformed(ActionEvent e) {
        final Dialog thisDialog = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                XSLTemplate xslTemplate = new XSLTemplate();

                XSLTemplateDialog xtd = new XSLTemplateDialog(thisDialog, xslTemplate);
                xtd.setModal(true);
                xtd.setVisible(true);
                if (xtd.getDialogResult() == JOptionPane.OK_OPTION) {

                    try {
                        xslTemplate.setFileName(xtd.getFileName());
                        xslTemplate.setFile(xtd.getFile());
                        xslTemplate.setDescription(xtd.getTemplateDescription());
                        xslTemplate.setGUID(xtd.getTemplateGUID());

                        for (XSLTemplate t : xslTemplates.getXSLTemplates()) {
                            if (xslTemplate.getTemplateGUID().equals(t.getTemplateGUID())) {
                                throw new Exception(
                                    "Template already in list of templates. (" + t.getFileName()
                                        + ")");
                            }
                        }

                        xslTemplates.getXSLTemplates().add(xslTemplate);
                        refreshXSLTemplatesList();

                    } catch (Exception ex) {
                        Misc.showExceptionMsg(thisDialog, ex);
                    }

                }
            }
        });
    }

    private void removeXSLFileButtonActionPerformed(ActionEvent e) {
        final Dialog thisDialog = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                XSLTemplate xslTemplate = (XSLTemplate) xslFileList.getSelectedValue();

                if (xslTemplate == null) {
                    return;
                }

                xslTemplates.getXSLTemplates().remove(xslTemplate);
                refreshXSLTemplatesList();

            }
        });
    }

    private void revertChangesXSLReportButtonActionPerformed(ActionEvent e) {
        restoreXSLTemplateProperties();
    }

    private void saveXSLReportChangesButtonActionPerformed(ActionEvent e) {
        saveXSLTemplateProperties();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        publicationSettingsTabbedPane = new JTabbedPane();
        answerKeyPanel = new JPanel();
        answerKeyDescriptionPanel = new JPanel();
        answerKeyDescriptionLabel = new JLabel();
        answerKeyDescriptionHelpLabel = new JHelpLabel();
        answerKeyTablePanel = new JPanel();
        scrollPane1 = new JScrollPane();
        formStructureTable = new JTable();
        answerKeyButtonPanel = new JPanel();
        importAnswerKeyButton = new JButton();
        exportAnswerKeyButton = new JButton();
        recalculateScoresButton = new JButton();
        revertStructureButton = new JButton();
        saveChangesButton = new JButton();
        markAggregationGradingPanel = new JPanel();
        gradingDescriptionPanel = new JPanel();
        gradingDescriptionLabel = new JLabel();
        gradingDescriptionHelpLabel = new JHelpLabel();
        gradingCalculationsPanel = new JPanel();
        totalPossibleScorePanel = new JPanel();
        totalPossibleFormScoreLabel = new JLabel();
        totalScoreTextField = new JTextField();
        gradingRulesLabel = new JLabel();
        gradingRulesPanel = new JPanel();
        gradingListScrollPane = new JScrollPane();
        gradingList = new JList();
        gradingRulesButtonPanel = new JPanel();
        gradingRuleUpButton = new JButton();
        gradingRuleDownButton = new JButton();
        addGradingRuleButton = new JButton();
        editGradingRuleButton = new JButton();
        removeGradingRuleButton = new JButton();
        revertChangesGradingButton = new JButton();
        saveGradingChangesButton = new JButton();
        xslFoReportsPanel = new JPanel();
        xslReportDescriptionPanel = new JPanel();
        xslReportDescriptionLabel = new JLabel();
        xslReportDescriptionHelpLabel = new JHelpLabel();
        embeddedReportTemplatesPanel = new JPanel();
        xslFileListScrollPane = new JScrollPane();
        xslFileList = new JList();
        xslFileListButtonPanel = new JPanel();
        xslFileUpButton = new JButton();
        xslFileDownButton = new JButton();
        addXSLFileButton = new JButton();
        removeXSLFileButton = new JButton();
        revertChangesXSLReportButton = new JButton();
        saveXSLReportChangesButton = new JButton();
        recognitionSettingsPanel = new JPanel();
        recognitionSettingsDescriptionPanel = new JPanel();
        recognitionSettingsDescriptionLabel = new JLabel();
        recognitionSettingsHelpLabel = new JHelpLabel();
        luminanceThresholdPanel = new JPanel();
        luminanceThresholdSpinner = new JSpinner();
        luminanceThresholdDescriptionLabel = new JLabel();
        markThresholdPanel = new JPanel();
        markThresholdSpinner = new JSpinner();
        markThresholdDescriptionLabel = new JLabel();
        fragmentPaddingPanel = new JPanel();
        fragmentPaddingSpinner = new JSpinner();
        fragmentPaddingDescriptionLabel = new JLabel();
        deskewPanel = new JPanel();
        performDeskewCheckBox = new JCheckBox();
        deskewThresholdSpinner = new JSpinner();
        deskewDescriptionLabel = new JLabel();
        recognitionSettingsButtonPanel = new JPanel();
        recognitionSettingsNoteLabel = new JLabel();
        revertRecognitionSettingsButton = new JButton();
        saveChangesRecognitionSettingsButton = new JButton();
        publicationFilesPanel = new JPanel();
        formTemplateFilePanel = new JPanel();
        saveTemplateToDiskButton = new JButton();
        templateDescriptionLabel = new JLabel();
        pdfFilePanel = new JPanel();
        savePDFToDiskButton = new JButton();
        pdfDescriptionLabel = new JLabel();
        advancedPanel = new JPanel();
        advancedDescriptionPanel = new JPanel();
        advancedDescriptionLabel = new JLabel();
        advancedHelpLabel = new JHelpLabel();
        uploadImagesPanel = new JPanel();
        uploadImageButton = new JButton();
        uploadImageDescriptionLabel = new JLabel();
        scannedInOrderPanel = new JPanel();
        scannedInOrderCheckBox = new JCheckBox();
        scannedInOrderDescriptionLabel = new JLabel();
        changePublicationTypePanel = new JPanel();
        publicationTypeComboBox = new JComboBox();
        publicationTypeDescriptionLabel = new JLabel();
        buttonBar = new JPanel();
        closeButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setModal(true);
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                thisWindowClosing(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UICDM", "PublicationSettingsDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            dialogPane.setLayout(new GridBagLayout());
            ((GridBagLayout)dialogPane.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout)dialogPane.getLayout()).rowHeights = new int[] {0, 0, 0};
            ((GridBagLayout)dialogPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout)dialogPane.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

            //======== publicationSettingsTabbedPane ========
            {
                publicationSettingsTabbedPane.setFont(UIManager.getFont("TabbedPane.font"));

                //======== answerKeyPanel ========
                {
                    answerKeyPanel.setOpaque(false);
                    answerKeyPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                    answerKeyPanel.setFont(UIManager.getFont("Panel.font"));
                    answerKeyPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)answerKeyPanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)answerKeyPanel.getLayout()).rowHeights = new int[] {35, 0, 0, 0};
                    ((GridBagLayout)answerKeyPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)answerKeyPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};

                    //======== answerKeyDescriptionPanel ========
                    {
                        answerKeyDescriptionPanel.setBorder(new MatteBorder(0, 0, 3, 0, Color.gray));
                        answerKeyDescriptionPanel.setOpaque(false);
                        answerKeyDescriptionPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)answerKeyDescriptionPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                        ((GridBagLayout)answerKeyDescriptionPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)answerKeyDescriptionPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)answerKeyDescriptionPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                        //---- answerKeyDescriptionLabel ----
                        answerKeyDescriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        answerKeyDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        answerKeyDescriptionLabel.setText("<html><body><strong>" + Localizer.localize("UICDM", "AnswerKeyDescriptionLabel") + "</strong></body></html>");
                        answerKeyDescriptionPanel.add(answerKeyDescriptionLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- answerKeyDescriptionHelpLabel ----
                        answerKeyDescriptionHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                        answerKeyDescriptionHelpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        answerKeyDescriptionHelpLabel.setFont(UIManager.getFont("Label.font"));
                        answerKeyDescriptionHelpLabel.setHelpGUID("publication-answer-key");
                        answerKeyDescriptionHelpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                        answerKeyDescriptionPanel.add(answerKeyDescriptionHelpLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    answerKeyPanel.add(answerKeyDescriptionPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== answerKeyTablePanel ========
                    {
                        answerKeyTablePanel.setOpaque(false);
                        answerKeyTablePanel.setBorder(null);
                        answerKeyTablePanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)answerKeyTablePanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)answerKeyTablePanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)answerKeyTablePanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)answerKeyTablePanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                        //======== scrollPane1 ========
                        {

                            //---- formStructureTable ----
                            formStructureTable.setCellSelectionEnabled(true);
                            formStructureTable.setGridColor(Color.lightGray);
                            formStructureTable.setFont(UIManager.getFont("Table.font"));
                            formStructureTable.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));
                            scrollPane1.setViewportView(formStructureTable);
                        }
                        answerKeyTablePanel.add(scrollPane1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    answerKeyPanel.add(answerKeyTablePanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== answerKeyButtonPanel ========
                    {
                        answerKeyButtonPanel.setOpaque(false);
                        answerKeyButtonPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)answerKeyButtonPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0};
                        ((GridBagLayout)answerKeyButtonPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)answerKeyButtonPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)answerKeyButtonPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- importAnswerKeyButton ----
                        importAnswerKeyButton.setFont(UIManager.getFont("Button.font"));
                        importAnswerKeyButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/sdm/table_row_insert.png")));
                        importAnswerKeyButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                importAnswerKeyButtonActionPerformed(e);
                            }
                        });
                        importAnswerKeyButton.setText(Localizer.localize("UICDM", "ImportAnswerKeyButtonText"));
                        answerKeyButtonPanel.add(importAnswerKeyButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- exportAnswerKeyButton ----
                        exportAnswerKeyButton.setFont(UIManager.getFont("Button.font"));
                        exportAnswerKeyButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/sdm/table_save.png")));
                        exportAnswerKeyButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                exportAnswerKeyButtonActionPerformed(e);
                            }
                        });
                        exportAnswerKeyButton.setText(Localizer.localize("UICDM", "ExportAnswerKeyButtonText"));
                        answerKeyButtonPanel.add(exportAnswerKeyButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- recalculateScoresButton ----
                        recalculateScoresButton.setFont(UIManager.getFont("Button.font"));
                        recalculateScoresButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/calculator.png")));
                        recalculateScoresButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                recalculateScoresButtonActionPerformed(e);
                            }
                        });
                        recalculateScoresButton.setText(Localizer.localize("UI", "RecalculateScoresButtonText"));
                        answerKeyButtonPanel.add(recalculateScoresButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- revertStructureButton ----
                        revertStructureButton.setFont(UIManager.getFont("Button.font"));
                        revertStructureButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
                        revertStructureButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                reloadStructureButtonActionPerformed(e);
                            }
                        });
                        revertStructureButton.setText(Localizer.localize("UICDM", "RevertButtonText"));
                        answerKeyButtonPanel.add(revertStructureButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- saveChangesButton ----
                        saveChangesButton.setFont(UIManager.getFont("Button.font"));
                        saveChangesButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
                        saveChangesButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                updateStructureButtonActionPerformed(e);
                            }
                        });
                        saveChangesButton.setText(Localizer.localize("UICDM", "SaveChangesButtonText"));
                        answerKeyButtonPanel.add(saveChangesButton, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    answerKeyPanel.add(answerKeyButtonPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                publicationSettingsTabbedPane.addTab("Answer Key", answerKeyPanel);

                //======== markAggregationGradingPanel ========
                {
                    markAggregationGradingPanel.setOpaque(false);
                    markAggregationGradingPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                    markAggregationGradingPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)markAggregationGradingPanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)markAggregationGradingPanel.getLayout()).rowHeights = new int[] {35, 0, 0, 0};
                    ((GridBagLayout)markAggregationGradingPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)markAggregationGradingPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};

                    //======== gradingDescriptionPanel ========
                    {
                        gradingDescriptionPanel.setBorder(new MatteBorder(0, 0, 3, 0, Color.gray));
                        gradingDescriptionPanel.setOpaque(false);
                        gradingDescriptionPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)gradingDescriptionPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                        ((GridBagLayout)gradingDescriptionPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)gradingDescriptionPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)gradingDescriptionPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                        //---- gradingDescriptionLabel ----
                        gradingDescriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        gradingDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        gradingDescriptionLabel.setText("<html><body><strong>" + Localizer.localize("UICDM", "GradingDescriptionLabel") + "</strong></body></html>");
                        gradingDescriptionPanel.add(gradingDescriptionLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- gradingDescriptionHelpLabel ----
                        gradingDescriptionHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                        gradingDescriptionHelpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        gradingDescriptionHelpLabel.setFont(UIManager.getFont("Label.font"));
                        gradingDescriptionHelpLabel.setHelpGUID("publication-grading-rules");
                        gradingDescriptionHelpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                        gradingDescriptionPanel.add(gradingDescriptionHelpLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    markAggregationGradingPanel.add(gradingDescriptionPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== gradingCalculationsPanel ========
                    {
                        gradingCalculationsPanel.setOpaque(false);
                        gradingCalculationsPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)gradingCalculationsPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)gradingCalculationsPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                        ((GridBagLayout)gradingCalculationsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)gradingCalculationsPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};

                        //======== totalPossibleScorePanel ========
                        {
                            totalPossibleScorePanel.setOpaque(false);
                            totalPossibleScorePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                            totalPossibleScorePanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)totalPossibleScorePanel.getLayout()).columnWidths = new int[] {0, 0, 100, 0};
                            ((GridBagLayout)totalPossibleScorePanel.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)totalPossibleScorePanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0E-4};
                            ((GridBagLayout)totalPossibleScorePanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                            //---- totalPossibleFormScoreLabel ----
                            totalPossibleFormScoreLabel.setFont(UIManager.getFont("Label.font"));
                            totalPossibleFormScoreLabel.setText(Localizer.localize("UI", "TotalPossibleFormScoreText"));
                            totalPossibleScorePanel.add(totalPossibleFormScoreLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 10), 0, 0));

                            //---- totalScoreTextField ----
                            totalScoreTextField.setFont(UIManager.getFont("TextField.font"));
                            totalPossibleScorePanel.add(totalScoreTextField, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        gradingCalculationsPanel.add(totalPossibleScorePanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- gradingRulesLabel ----
                        gradingRulesLabel.setBorder(new CompoundBorder(
                            new MatteBorder(1, 0, 0, 0, Color.lightGray),
                            new EmptyBorder(5, 0, 0, 0)));
                        gradingRulesLabel.setFont(UIManager.getFont("Label.font"));
                        gradingRulesLabel.setText(Localizer.localize("UI", "GradingRulesLabelText"));
                        gradingCalculationsPanel.add(gradingRulesLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== gradingRulesPanel ========
                        {
                            gradingRulesPanel.setOpaque(false);
                            gradingRulesPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)gradingRulesPanel.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout)gradingRulesPanel.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)gradingRulesPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                            ((GridBagLayout)gradingRulesPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                            //======== gradingListScrollPane ========
                            {

                                //---- gradingList ----
                                gradingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                                gradingList.setFont(UIManager.getFont("List.font"));
                                gradingListScrollPane.setViewportView(gradingList);
                            }
                            gradingRulesPanel.add(gradingListScrollPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        gradingCalculationsPanel.add(gradingRulesPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    markAggregationGradingPanel.add(gradingCalculationsPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== gradingRulesButtonPanel ========
                    {
                        gradingRulesButtonPanel.setOpaque(false);
                        gradingRulesButtonPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)gradingRulesButtonPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
                        ((GridBagLayout)gradingRulesButtonPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)gradingRulesButtonPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)gradingRulesButtonPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- gradingRuleUpButton ----
                        gradingRuleUpButton.setFont(UIManager.getFont("Button.font"));
                        gradingRuleUpButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_up.png")));
                        gradingRuleUpButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                gradingRuleUpButtonActionPerformed(e);
                            }
                        });
                        gradingRuleUpButton.setText(Localizer.localize("UI", "UpText"));
                        gradingRulesButtonPanel.add(gradingRuleUpButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- gradingRuleDownButton ----
                        gradingRuleDownButton.setFont(UIManager.getFont("Button.font"));
                        gradingRuleDownButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_down.png")));
                        gradingRuleDownButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                gradingRuleDownButtonActionPerformed(e);
                            }
                        });
                        gradingRuleDownButton.setText(Localizer.localize("UI", "DownText"));
                        gradingRulesButtonPanel.add(gradingRuleDownButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- addGradingRuleButton ----
                        addGradingRuleButton.setFont(UIManager.getFont("Button.font"));
                        addGradingRuleButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/add.png")));
                        addGradingRuleButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                addGradingRuleButtonActionPerformed(e);
                            }
                        });
                        addGradingRuleButton.setText(Localizer.localize("UI", "AddGradingRuleButtonText"));
                        gradingRulesButtonPanel.add(addGradingRuleButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- editGradingRuleButton ----
                        editGradingRuleButton.setFont(UIManager.getFont("Button.font"));
                        editGradingRuleButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/pencil.png")));
                        editGradingRuleButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                editGradingRuleButtonActionPerformed(e);
                            }
                        });
                        editGradingRuleButton.setText(Localizer.localize("UI", "EditGradingRuleButtonText"));
                        gradingRulesButtonPanel.add(editGradingRuleButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- removeGradingRuleButton ----
                        removeGradingRuleButton.setFont(UIManager.getFont("Button.font"));
                        removeGradingRuleButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/delete.png")));
                        removeGradingRuleButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                removeGradingRuleButtonActionPerformed(e);
                            }
                        });
                        removeGradingRuleButton.setText(Localizer.localize("UI", "RemoveGradingRuleButtonText"));
                        gradingRulesButtonPanel.add(removeGradingRuleButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- revertChangesGradingButton ----
                        revertChangesGradingButton.setText("Revert Changes");
                        revertChangesGradingButton.setFont(UIManager.getFont("Button.font"));
                        revertChangesGradingButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
                        revertChangesGradingButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                revertChangesGradingButtonActionPerformed(e);
                            }
                        });
                        gradingRulesButtonPanel.add(revertChangesGradingButton, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- saveGradingChangesButton ----
                        saveGradingChangesButton.setText("Save Changes");
                        saveGradingChangesButton.setFont(UIManager.getFont("Button.font"));
                        saveGradingChangesButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
                        saveGradingChangesButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                saveGradingChangesButtonActionPerformed(e);
                            }
                        });
                        gradingRulesButtonPanel.add(saveGradingChangesButton, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    markAggregationGradingPanel.add(gradingRulesButtonPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                publicationSettingsTabbedPane.addTab("Automatic Grading", markAggregationGradingPanel);

                //======== xslFoReportsPanel ========
                {
                    xslFoReportsPanel.setOpaque(false);
                    xslFoReportsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                    xslFoReportsPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)xslFoReportsPanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)xslFoReportsPanel.getLayout()).rowHeights = new int[] {35, 0, 0, 0};
                    ((GridBagLayout)xslFoReportsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)xslFoReportsPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};

                    //======== xslReportDescriptionPanel ========
                    {
                        xslReportDescriptionPanel.setBorder(new MatteBorder(0, 0, 3, 0, Color.gray));
                        xslReportDescriptionPanel.setOpaque(false);
                        xslReportDescriptionPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)xslReportDescriptionPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                        ((GridBagLayout)xslReportDescriptionPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)xslReportDescriptionPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)xslReportDescriptionPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                        //---- xslReportDescriptionLabel ----
                        xslReportDescriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        xslReportDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        xslReportDescriptionLabel.setText("<html><body><strong>" + Localizer.localize("UICDM", "XSLReportDescriptionLabel") + "</strong></body></html>");
                        xslReportDescriptionPanel.add(xslReportDescriptionLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- xslReportDescriptionHelpLabel ----
                        xslReportDescriptionHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                        xslReportDescriptionHelpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        xslReportDescriptionHelpLabel.setFont(UIManager.getFont("Label.font"));
                        xslReportDescriptionHelpLabel.setHelpGUID("publication-xsl-fo-report-template");
                        xslReportDescriptionHelpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                        xslReportDescriptionPanel.add(xslReportDescriptionHelpLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    xslFoReportsPanel.add(xslReportDescriptionPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== embeddedReportTemplatesPanel ========
                    {
                        embeddedReportTemplatesPanel.setOpaque(false);
                        embeddedReportTemplatesPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)embeddedReportTemplatesPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)embeddedReportTemplatesPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)embeddedReportTemplatesPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)embeddedReportTemplatesPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                        //======== xslFileListScrollPane ========
                        {

                            //---- xslFileList ----
                            xslFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                            xslFileList.setFont(UIManager.getFont("List.font"));
                            xslFileListScrollPane.setViewportView(xslFileList);
                        }
                        embeddedReportTemplatesPanel.add(xslFileListScrollPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    xslFoReportsPanel.add(embeddedReportTemplatesPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== xslFileListButtonPanel ========
                    {
                        xslFileListButtonPanel.setOpaque(false);
                        xslFileListButtonPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)xslFileListButtonPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0, 0};
                        ((GridBagLayout)xslFileListButtonPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)xslFileListButtonPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)xslFileListButtonPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- xslFileUpButton ----
                        xslFileUpButton.setFont(UIManager.getFont("Button.font"));
                        xslFileUpButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_up.png")));
                        xslFileUpButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                xslFileUpButtonActionPerformed(e);
                            }
                        });
                        xslFileUpButton.setText(Localizer.localize("UI", "UpText"));
                        xslFileListButtonPanel.add(xslFileUpButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- xslFileDownButton ----
                        xslFileDownButton.setFont(UIManager.getFont("Button.font"));
                        xslFileDownButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_down.png")));
                        xslFileDownButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                xslFileDownButtonActionPerformed(e);
                            }
                        });
                        xslFileDownButton.setText(Localizer.localize("UI", "DownText"));
                        xslFileListButtonPanel.add(xslFileDownButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- addXSLFileButton ----
                        addXSLFileButton.setFont(UIManager.getFont("Button.font"));
                        addXSLFileButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/add.png")));
                        addXSLFileButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                addXSLFileButtonActionPerformed(e);
                            }
                        });
                        addXSLFileButton.setText(Localizer.localize("UI", "AddXSLFileButtonText"));
                        xslFileListButtonPanel.add(addXSLFileButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- removeXSLFileButton ----
                        removeXSLFileButton.setFont(UIManager.getFont("Button.font"));
                        removeXSLFileButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/delete.png")));
                        removeXSLFileButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                removeXSLFileButtonActionPerformed(e);
                            }
                        });
                        removeXSLFileButton.setText(Localizer.localize("UI", "RemoveXSLFileButtonText"));
                        xslFileListButtonPanel.add(removeXSLFileButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- revertChangesXSLReportButton ----
                        revertChangesXSLReportButton.setText("Revert Changes");
                        revertChangesXSLReportButton.setFont(UIManager.getFont("Button.font"));
                        revertChangesXSLReportButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
                        revertChangesXSLReportButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                revertChangesXSLReportButtonActionPerformed(e);
                            }
                        });
                        xslFileListButtonPanel.add(revertChangesXSLReportButton, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- saveXSLReportChangesButton ----
                        saveXSLReportChangesButton.setText("Save Changes");
                        saveXSLReportChangesButton.setFont(UIManager.getFont("Button.font"));
                        saveXSLReportChangesButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
                        saveXSLReportChangesButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                saveXSLReportChangesButtonActionPerformed(e);
                            }
                        });
                        xslFileListButtonPanel.add(saveXSLReportChangesButton, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    xslFoReportsPanel.add(xslFileListButtonPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                publicationSettingsTabbedPane.addTab("XSL-FO Report Templates", xslFoReportsPanel);

                //======== recognitionSettingsPanel ========
                {
                    recognitionSettingsPanel.setOpaque(false);
                    recognitionSettingsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                    recognitionSettingsPanel.setFont(UIManager.getFont("Panel.font"));
                    recognitionSettingsPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)recognitionSettingsPanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)recognitionSettingsPanel.getLayout()).rowHeights = new int[] {35, 0, 0, 0, 0, 0, 0};
                    ((GridBagLayout)recognitionSettingsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)recognitionSettingsPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0, 1.0, 1.0, 0.0, 1.0E-4};

                    //======== recognitionSettingsDescriptionPanel ========
                    {
                        recognitionSettingsDescriptionPanel.setBorder(new MatteBorder(0, 0, 3, 0, Color.gray));
                        recognitionSettingsDescriptionPanel.setOpaque(false);
                        recognitionSettingsDescriptionPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)recognitionSettingsDescriptionPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                        ((GridBagLayout)recognitionSettingsDescriptionPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)recognitionSettingsDescriptionPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)recognitionSettingsDescriptionPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                        //---- recognitionSettingsDescriptionLabel ----
                        recognitionSettingsDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        recognitionSettingsDescriptionLabel.setText("<html><body><strong>" + Localizer.localize("UICDM", "RecognitionSettingsDescriptionLabel") + "</strong></body></html>");
                        recognitionSettingsDescriptionPanel.add(recognitionSettingsDescriptionLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- recognitionSettingsHelpLabel ----
                        recognitionSettingsHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                        recognitionSettingsHelpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        recognitionSettingsHelpLabel.setFont(UIManager.getFont("Label.font"));
                        recognitionSettingsHelpLabel.setHelpGUID("publication-recognition-settings");
                        recognitionSettingsHelpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                        recognitionSettingsDescriptionPanel.add(recognitionSettingsHelpLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    recognitionSettingsPanel.add(recognitionSettingsDescriptionPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== luminanceThresholdPanel ========
                    {
                        luminanceThresholdPanel.setOpaque(false);
                        luminanceThresholdPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)luminanceThresholdPanel.getLayout()).columnWidths = new int[] {45, 105, 15, 0, 0};
                        ((GridBagLayout)luminanceThresholdPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)luminanceThresholdPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)luminanceThresholdPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                        luminanceThresholdPanel.setBorder(new CompoundBorder(
                            new TitledBorder(Localizer.localize("UI", "LuminanceThresholdPanel")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //---- luminanceThresholdSpinner ----
                        luminanceThresholdSpinner.setModel(new SpinnerNumberModel(200, 0, 255, 1));
                        luminanceThresholdSpinner.setToolTipText("Luminance is the \"black pixel\" cuttoff value.");
                        luminanceThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
                        luminanceThresholdPanel.add(luminanceThresholdSpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- luminanceThresholdDescriptionLabel ----
                        luminanceThresholdDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        luminanceThresholdDescriptionLabel.setText(Localizer.localize("UICDM", "LuminanceThresholdDescriptionLabel"));
                        luminanceThresholdPanel.add(luminanceThresholdDescriptionLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    recognitionSettingsPanel.add(luminanceThresholdPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== markThresholdPanel ========
                    {
                        markThresholdPanel.setOpaque(false);
                        markThresholdPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)markThresholdPanel.getLayout()).columnWidths = new int[] {45, 105, 15, 0, 0};
                        ((GridBagLayout)markThresholdPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)markThresholdPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)markThresholdPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                        markThresholdPanel.setBorder(new CompoundBorder(
                            new TitledBorder(Localizer.localize("UI", "MarkThresholdPanel")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //---- markThresholdSpinner ----
                        markThresholdSpinner.setModel(new SpinnerNumberModel(40, -10000, 10000, 1));
                        markThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
                        markThresholdPanel.add(markThresholdSpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- markThresholdDescriptionLabel ----
                        markThresholdDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        markThresholdDescriptionLabel.setText(Localizer.localize("UICDM", "MarkThresholdDescriptionLabel"));
                        markThresholdPanel.add(markThresholdDescriptionLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    recognitionSettingsPanel.add(markThresholdPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== fragmentPaddingPanel ========
                    {
                        fragmentPaddingPanel.setOpaque(false);
                        fragmentPaddingPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)fragmentPaddingPanel.getLayout()).columnWidths = new int[] {45, 105, 15, 0, 0};
                        ((GridBagLayout)fragmentPaddingPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)fragmentPaddingPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)fragmentPaddingPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                        fragmentPaddingPanel.setBorder(new CompoundBorder(
                            new TitledBorder(Localizer.localize("UI", "FragmentPaddingPanel")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //---- fragmentPaddingSpinner ----
                        fragmentPaddingSpinner.setModel(new SpinnerNumberModel(1, 0, 200, 1));
                        fragmentPaddingSpinner.setFont(UIManager.getFont("Spinner.font"));
                        fragmentPaddingPanel.add(fragmentPaddingSpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- fragmentPaddingDescriptionLabel ----
                        fragmentPaddingDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        fragmentPaddingDescriptionLabel.setText(Localizer.localize("UICDM", "FragmentPaddingDescriptionLabel"));
                        fragmentPaddingPanel.add(fragmentPaddingDescriptionLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    recognitionSettingsPanel.add(fragmentPaddingPanel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== deskewPanel ========
                    {
                        deskewPanel.setOpaque(false);
                        deskewPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)deskewPanel.getLayout()).columnWidths = new int[] {45, 0, 105, 15, 0, 0};
                        ((GridBagLayout)deskewPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)deskewPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)deskewPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                        deskewPanel.setBorder(new CompoundBorder(
                            new TitledBorder(Localizer.localize("UI", "DeskewPanel")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //---- performDeskewCheckBox ----
                        performDeskewCheckBox.setSelected(true);
                        performDeskewCheckBox.setFocusPainted(false);
                        performDeskewCheckBox.setOpaque(false);
                        performDeskewCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                        performDeskewCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
                        performDeskewCheckBox.setText(Localizer.localize("UICDM", "AutomaticDeskewEnabledCheckboxLabel"));
                        deskewPanel.add(performDeskewCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- deskewThresholdSpinner ----
                        deskewThresholdSpinner.setModel(new SpinnerNumberModel(1.05, 0.0, 90.0, 0.01));
                        deskewThresholdSpinner.setToolTipText("The number of degrees difference before a deskew is performed");
                        deskewThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
                        deskewPanel.add(deskewThresholdSpinner, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- deskewDescriptionLabel ----
                        deskewDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        deskewDescriptionLabel.setText(Localizer.localize("UICDM", "DeskewDescriptionLabel"));
                        deskewPanel.add(deskewDescriptionLabel, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    recognitionSettingsPanel.add(deskewPanel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== recognitionSettingsButtonPanel ========
                    {
                        recognitionSettingsButtonPanel.setOpaque(false);
                        recognitionSettingsButtonPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)recognitionSettingsButtonPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                        ((GridBagLayout)recognitionSettingsButtonPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)recognitionSettingsButtonPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)recognitionSettingsButtonPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- recognitionSettingsNoteLabel ----
                        recognitionSettingsNoteLabel.setFont(UIManager.getFont("Label.font"));
                        recognitionSettingsNoteLabel.setForeground(Color.red);
                        recognitionSettingsNoteLabel.setText("<html><body><strong>" + Localizer.localize("UICDM", "RecognitionSettingsNoteLabel") + "</strong></body></html>");
                        recognitionSettingsButtonPanel.add(recognitionSettingsNoteLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- revertRecognitionSettingsButton ----
                        revertRecognitionSettingsButton.setFont(UIManager.getFont("Button.font"));
                        revertRecognitionSettingsButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
                        revertRecognitionSettingsButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                reloadRecognitionSettingsButtonActionPerformed(e);
                            }
                        });
                        revertRecognitionSettingsButton.setText(Localizer.localize("UICDM", "RevertButtonText"));
                        recognitionSettingsButtonPanel.add(revertRecognitionSettingsButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- saveChangesRecognitionSettingsButton ----
                        saveChangesRecognitionSettingsButton.setFont(UIManager.getFont("Button.font"));
                        saveChangesRecognitionSettingsButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
                        saveChangesRecognitionSettingsButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                updateRecognitionStructureButtonActionPerformed(e);
                            }
                        });
                        saveChangesRecognitionSettingsButton.setText(Localizer.localize("UICDM", "SaveChangesButtonText"));
                        recognitionSettingsButtonPanel.add(saveChangesRecognitionSettingsButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    recognitionSettingsPanel.add(recognitionSettingsButtonPanel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                publicationSettingsTabbedPane.addTab("Recognition Settings", recognitionSettingsPanel);

                //======== publicationFilesPanel ========
                {
                    publicationFilesPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                    publicationFilesPanel.setOpaque(false);
                    publicationFilesPanel.setFont(UIManager.getFont("Panel.font"));
                    publicationFilesPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)publicationFilesPanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)publicationFilesPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout)publicationFilesPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)publicationFilesPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};

                    //======== formTemplateFilePanel ========
                    {
                        formTemplateFilePanel.setOpaque(false);
                        formTemplateFilePanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)formTemplateFilePanel.getLayout()).columnWidths = new int[] {45, 0, 15, 0, 0, 0};
                        ((GridBagLayout)formTemplateFilePanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)formTemplateFilePanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)formTemplateFilePanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                        formTemplateFilePanel.setBorder(new CompoundBorder(
                            new TitledBorder(Localizer.localize("UICDM", "FormTemplateFileBorderTitle")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //---- saveTemplateToDiskButton ----
                        saveTemplateToDiskButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
                        saveTemplateToDiskButton.setFont(UIManager.getFont("Button.font"));
                        saveTemplateToDiskButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                downloadTemplateButtonActionPerformed(e);
                            }
                        });
                        saveTemplateToDiskButton.setText(Localizer.localize("UICDM", "SaveToDiskButtonText"));
                        formTemplateFilePanel.add(saveTemplateToDiskButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- templateDescriptionLabel ----
                        templateDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        templateDescriptionLabel.setText(Localizer.localize("UICDM", "TemplateDescriptionLabel"));
                        formTemplateFilePanel.add(templateDescriptionLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    publicationFilesPanel.add(formTemplateFilePanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== pdfFilePanel ========
                    {
                        pdfFilePanel.setOpaque(false);
                        pdfFilePanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)pdfFilePanel.getLayout()).columnWidths = new int[] {45, 0, 15, 0, 0, 0};
                        ((GridBagLayout)pdfFilePanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)pdfFilePanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)pdfFilePanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                        pdfFilePanel.setBorder(new CompoundBorder(
                            new TitledBorder(Localizer.localize("UICDM", "PDFFileBorderTitle")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //---- savePDFToDiskButton ----
                        savePDFToDiskButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
                        savePDFToDiskButton.setFont(UIManager.getFont("Button.font"));
                        savePDFToDiskButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                downloadPDFButtonActionPerformed(e);
                            }
                        });
                        savePDFToDiskButton.setText(Localizer.localize("UICDM", "SaveToDiskButtonText"));
                        pdfFilePanel.add(savePDFToDiskButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- pdfDescriptionLabel ----
                        pdfDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        pdfDescriptionLabel.setText(Localizer.localize("UICDM", "PDFDescriptionLabel"));
                        pdfFilePanel.add(pdfDescriptionLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    publicationFilesPanel.add(pdfFilePanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                publicationSettingsTabbedPane.addTab("Publication Files", publicationFilesPanel);

                //======== advancedPanel ========
                {
                    advancedPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                    advancedPanel.setOpaque(false);
                    advancedPanel.setFont(UIManager.getFont("Panel.font"));
                    advancedPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)advancedPanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)advancedPanel.getLayout()).rowHeights = new int[] {35, 0, 0, 0, 0};
                    ((GridBagLayout)advancedPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)advancedPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0, 1.0, 1.0E-4};

                    //======== advancedDescriptionPanel ========
                    {
                        advancedDescriptionPanel.setBorder(new MatteBorder(0, 0, 3, 0, Color.gray));
                        advancedDescriptionPanel.setOpaque(false);
                        advancedDescriptionPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)advancedDescriptionPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                        ((GridBagLayout)advancedDescriptionPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)advancedDescriptionPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)advancedDescriptionPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                        //---- advancedDescriptionLabel ----
                        advancedDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        advancedDescriptionLabel.setText("<html><body><strong>" + Localizer.localize("UICDM", "AdvancedDescriptionLabel") + "</strong></body></html>");
                        advancedDescriptionPanel.add(advancedDescriptionLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- advancedHelpLabel ----
                        advancedHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                        advancedHelpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        advancedHelpLabel.setFont(UIManager.getFont("Label.font"));
                        advancedHelpLabel.setHelpGUID("publication-advanced-settings");
                        advancedHelpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                        advancedDescriptionPanel.add(advancedHelpLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    advancedPanel.add(advancedDescriptionPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== uploadImagesPanel ========
                    {
                        uploadImagesPanel.setOpaque(false);
                        uploadImagesPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)uploadImagesPanel.getLayout()).columnWidths = new int[] {45, 0, 15, 0, 0};
                        ((GridBagLayout)uploadImagesPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)uploadImagesPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)uploadImagesPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                        uploadImagesPanel.setBorder(new CompoundBorder(
                            new TitledBorder(Localizer.localize("UI", "UploadImagesPanel")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //---- uploadImageButton ----
                        uploadImageButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/preview/image.png")));
                        uploadImageButton.setFont(UIManager.getFont("Button.font"));
                        uploadImageButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                uploadImageButtonActionPerformed(e);
                            }
                        });
                        uploadImageButton.setText(Localizer.localize("UI", "UnprocessedImagesPanelUploadImageButtonText"));
                        uploadImagesPanel.add(uploadImageButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- uploadImageDescriptionLabel ----
                        uploadImageDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        uploadImageDescriptionLabel.setText(Localizer.localize("UICDM", "UploadImageDescriptionLabel"));
                        uploadImagesPanel.add(uploadImageDescriptionLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    advancedPanel.add(uploadImagesPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== scannedInOrderPanel ========
                    {
                        scannedInOrderPanel.setOpaque(false);
                        scannedInOrderPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)scannedInOrderPanel.getLayout()).columnWidths = new int[] {45, 0, 15, 0, 0};
                        ((GridBagLayout)scannedInOrderPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)scannedInOrderPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)scannedInOrderPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                        scannedInOrderPanel.setBorder(new CompoundBorder(
                            new TitledBorder(Localizer.localize("UI", "ScannedInOrderPanel")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //---- scannedInOrderCheckBox ----
                        scannedInOrderCheckBox.setText("Scanned Images Are In Order");
                        scannedInOrderCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                        scannedInOrderCheckBox.setOpaque(false);
                        scannedInOrderCheckBox.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                scannedInOrderCheckBoxActionPerformed(e);
                            }
                        });
                        scannedInOrderCheckBox.setText(Localizer.localize("UI", "ScannedInOrderLabelText"));
                        scannedInOrderPanel.add(scannedInOrderCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- scannedInOrderDescriptionLabel ----
                        scannedInOrderDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        scannedInOrderDescriptionLabel.setText(Localizer.localize("UICDM", "ScannedInOrderDescriptionLabel"));
                        scannedInOrderPanel.add(scannedInOrderDescriptionLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    advancedPanel.add(scannedInOrderPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== changePublicationTypePanel ========
                    {
                        changePublicationTypePanel.setOpaque(false);
                        changePublicationTypePanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)changePublicationTypePanel.getLayout()).columnWidths = new int[] {45, 0, 15, 0, 0, 0};
                        ((GridBagLayout)changePublicationTypePanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)changePublicationTypePanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)changePublicationTypePanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                        changePublicationTypePanel.setBorder(new CompoundBorder(
                            new TitledBorder(Localizer.localize("UI", "ChangePublicationTypePanel")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //---- publicationTypeComboBox ----
                        publicationTypeComboBox.setFont(UIManager.getFont("ComboBox.font"));
                        publicationTypeComboBox.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                publicationTypeComboBoxItemStateChanged(e);
                            }
                        });
                        changePublicationTypePanel.add(publicationTypeComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- publicationTypeDescriptionLabel ----
                        publicationTypeDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        publicationTypeDescriptionLabel.setText(Localizer.localize("UICDM", "PublicationTypeDescriptionLabel"));
                        changePublicationTypePanel.add(publicationTypeDescriptionLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    advancedPanel.add(changePublicationTypePanel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                publicationSettingsTabbedPane.addTab("Advanced", advancedPanel);
            }
            dialogPane.add(publicationSettingsTabbedPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

                //---- closeButton ----
                closeButton.setFont(UIManager.getFont("Button.font"));
                closeButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
                closeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        closeButtonActionPerformed(e);
                    }
                });
                closeButton.setText(Localizer.localize("UICDM", "CloseButtonText"));
                buttonBar.add(closeButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(990, 540);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel dialogPane;
    private JTabbedPane publicationSettingsTabbedPane;
    private JPanel answerKeyPanel;
    private JPanel answerKeyDescriptionPanel;
    private JLabel answerKeyDescriptionLabel;
    private JHelpLabel answerKeyDescriptionHelpLabel;
    private JPanel answerKeyTablePanel;
    private JScrollPane scrollPane1;
    private JTable formStructureTable;
    private JPanel answerKeyButtonPanel;
    private JButton importAnswerKeyButton;
    private JButton exportAnswerKeyButton;
    private JButton recalculateScoresButton;
    private JButton revertStructureButton;
    private JButton saveChangesButton;
    private JPanel markAggregationGradingPanel;
    private JPanel gradingDescriptionPanel;
    private JLabel gradingDescriptionLabel;
    private JHelpLabel gradingDescriptionHelpLabel;
    private JPanel gradingCalculationsPanel;
    private JPanel totalPossibleScorePanel;
    private JLabel totalPossibleFormScoreLabel;
    private JTextField totalScoreTextField;
    private JLabel gradingRulesLabel;
    private JPanel gradingRulesPanel;
    private JScrollPane gradingListScrollPane;
    private JList gradingList;
    private JPanel gradingRulesButtonPanel;
    private JButton gradingRuleUpButton;
    private JButton gradingRuleDownButton;
    private JButton addGradingRuleButton;
    private JButton editGradingRuleButton;
    private JButton removeGradingRuleButton;
    private JButton revertChangesGradingButton;
    private JButton saveGradingChangesButton;
    private JPanel xslFoReportsPanel;
    private JPanel xslReportDescriptionPanel;
    private JLabel xslReportDescriptionLabel;
    private JHelpLabel xslReportDescriptionHelpLabel;
    private JPanel embeddedReportTemplatesPanel;
    private JScrollPane xslFileListScrollPane;
    private JList xslFileList;
    private JPanel xslFileListButtonPanel;
    private JButton xslFileUpButton;
    private JButton xslFileDownButton;
    private JButton addXSLFileButton;
    private JButton removeXSLFileButton;
    private JButton revertChangesXSLReportButton;
    private JButton saveXSLReportChangesButton;
    private JPanel recognitionSettingsPanel;
    private JPanel recognitionSettingsDescriptionPanel;
    private JLabel recognitionSettingsDescriptionLabel;
    private JHelpLabel recognitionSettingsHelpLabel;
    private JPanel luminanceThresholdPanel;
    private JSpinner luminanceThresholdSpinner;
    private JLabel luminanceThresholdDescriptionLabel;
    private JPanel markThresholdPanel;
    private JSpinner markThresholdSpinner;
    private JLabel markThresholdDescriptionLabel;
    private JPanel fragmentPaddingPanel;
    private JSpinner fragmentPaddingSpinner;
    private JLabel fragmentPaddingDescriptionLabel;
    private JPanel deskewPanel;
    private JCheckBox performDeskewCheckBox;
    private JSpinner deskewThresholdSpinner;
    private JLabel deskewDescriptionLabel;
    private JPanel recognitionSettingsButtonPanel;
    private JLabel recognitionSettingsNoteLabel;
    private JButton revertRecognitionSettingsButton;
    private JButton saveChangesRecognitionSettingsButton;
    private JPanel publicationFilesPanel;
    private JPanel formTemplateFilePanel;
    private JButton saveTemplateToDiskButton;
    private JLabel templateDescriptionLabel;
    private JPanel pdfFilePanel;
    private JButton savePDFToDiskButton;
    private JLabel pdfDescriptionLabel;
    private JPanel advancedPanel;
    private JPanel advancedDescriptionPanel;
    private JLabel advancedDescriptionLabel;
    private JHelpLabel advancedHelpLabel;
    private JPanel uploadImagesPanel;
    private JButton uploadImageButton;
    private JLabel uploadImageDescriptionLabel;
    private JPanel scannedInOrderPanel;
    private JCheckBox scannedInOrderCheckBox;
    private JLabel scannedInOrderDescriptionLabel;
    private JPanel changePublicationTypePanel;
    private JComboBox publicationTypeComboBox;
    private JLabel publicationTypeDescriptionLabel;
    private JPanel buttonBar;
    private JButton closeButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    public long[] getPublicationIds() {
        return publicationIds;
    }

    public void setPublicationIds(long[] publicationIds) {
        this.publicationIds = publicationIds;
    }

}
