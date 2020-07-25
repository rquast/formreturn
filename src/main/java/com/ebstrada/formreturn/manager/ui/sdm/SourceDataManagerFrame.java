package com.ebstrada.formreturn.manager.ui.sdm;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.*;

import au.com.bytecode.opencsv.CSVWriter;

import com.ebstrada.formreturn.api.messaging.ProcessingStatusDialog;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.jpa.DataSetController;
import com.ebstrada.formreturn.manager.logic.jpa.PublicationController;
import com.ebstrada.formreturn.manager.logic.jpa.RecordController;
import com.ebstrada.formreturn.manager.logic.jpa.SourceFieldController;
import com.ebstrada.formreturn.manager.logic.publish.FormPDFGenerator;
import com.ebstrada.formreturn.manager.logic.publish.FormPublisher;
import com.ebstrada.formreturn.manager.persistence.jpa.DataSet;
import com.ebstrada.formreturn.manager.persistence.jpa.Record;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceField;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceText;
import com.ebstrada.formreturn.manager.persistence.viewer.GenericDataViewer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.cdm.logic.FillTableController;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.ui.filefilter.FilenameExtensionFilter;
import com.ebstrada.formreturn.manager.ui.panel.PropertiesPanelController;
import com.ebstrada.formreturn.manager.ui.sdm.dialog.AddFieldDialog;
import com.ebstrada.formreturn.manager.ui.sdm.dialog.AddTableDialog;
import com.ebstrada.formreturn.manager.ui.sdm.dialog.EditFieldDialog;
import com.ebstrada.formreturn.manager.ui.sdm.dialog.EditTableDialog;
import com.ebstrada.formreturn.manager.ui.sdm.dialog.FillTableDialog;
import com.ebstrada.formreturn.manager.ui.sdm.dialog.ImportRecordsDialog;
import com.ebstrada.formreturn.manager.ui.sdm.dialog.RecordFieldDataDialog;
import com.ebstrada.formreturn.manager.ui.sdm.dialog.RenamePublicationDialog;
import com.ebstrada.formreturn.manager.ui.sdm.model.FieldDataModel;
import com.ebstrada.formreturn.manager.ui.sdm.model.PublicationDataModel;
import com.ebstrada.formreturn.manager.ui.sdm.model.RecordDataModel;
import com.ebstrada.formreturn.manager.ui.sdm.model.TableDataModel;
import com.ebstrada.formreturn.manager.ui.sdm.panel.FieldPropertiesPanel;
import com.ebstrada.formreturn.manager.ui.sdm.panel.PublicationsPropertiesPanel;
import com.ebstrada.formreturn.manager.ui.sdm.panel.RecordPropertiesPanel;
import com.ebstrada.formreturn.manager.ui.sdm.panel.TablePropertiesPanel;
import com.ebstrada.formreturn.manager.util.Misc;

public class SourceDataManagerFrame extends JPanel implements GenericDataViewer {

    private static final long serialVersionUID = 1L;

    private TableDataModel tableDataModel;

    private FieldDataModel fieldDataModel;

    private RecordDataModel recordDataModel;

    private PublicationDataModel publicationDataModel;

    private TablePropertiesPanel tablePropertiesPanel;

    private FieldPropertiesPanel fieldPropertiesPanel;

    private RecordPropertiesPanel recordPropertiesPanel;

    private PublicationsPropertiesPanel publicationsPropertiesPanel;

    private long activeParentId = -1;

    public SourceDataManagerFrame() {
        initComponents();

        SDMTabbedPane.setTitleAt(0, Localizer.localize("UI", "SourceDataManagerTablesTabTitle"));
        SDMTabbedPane.setTitleAt(1, Localizer.localize("UI", "SourceDataManagerRecordsTabTitle"));
        SDMTabbedPane
            .setTitleAt(2, Localizer.localize("UI", "SourceDataManagerPublicationsTabTitle"));

        tableDataModel = new TableDataModel();
        tablesTableFilterPanel.setTableModel(tableDataModel);
        tablesTableFilterPanel.setTableViewer(this);

        fieldDataModel = new FieldDataModel();

        recordDataModel = new RecordDataModel();
        recordsTableFilterPanel.setTableModel(recordDataModel);
        recordsTableFilterPanel.setTableViewer(this);

        publicationDataModel = new PublicationDataModel();
        publicationsTableFilterPanel.setTableModel(publicationDataModel);
        publicationsTableFilterPanel.setTableViewer(this);

        refresh();

        SelectionListener listener = new SelectionListener(tablesTable);
        tablesTable.getSelectionModel().addListSelectionListener(listener);
    }

    public void resetVerticalDivider() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                tablesSplitPane.setDividerLocation(0.7d);
            }
        });
    }

    public void updateSelectedDataSetIds(long[] selectedIds) {

        tableDataModel.setSelectedIds(selectedIds);

        long parentId = -1;
        if (selectedIds.length > 0) {
            parentId = selectedIds[0];
            activeParentId = parentId;

            fieldDataModel = new FieldDataModel();
            fieldDataModel.setParentId(parentId);

            recordDataModel = new RecordDataModel();
            recordDataModel.setParentId(parentId);
            recordsTableFilterPanel.setTableModel(recordDataModel);

            publicationDataModel = new PublicationDataModel();
            publicationDataModel.setParentId(parentId);
            publicationsTableFilterPanel.setTableModel(publicationDataModel);
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
                    updateSelectedDataSetIds(getSelectedDataSetIds());
                    restoreFields();
                } else {
                    updateSelectedDataSetIds(new long[] {});
                    restoreFields();
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

    public void refresh() {
        refresh(true);
    }

    public void refresh(boolean updatePageNumbers) {
        if (SDMTabbedPane.getSelectedIndex() == 1) {
            if (recordDataModel != null) {
                restoreRecords();
                if (updatePageNumbers) {
                    recordsTableFilterPanel.updatePageNumbers();
                }
            }
        } else if (SDMTabbedPane.getSelectedIndex() == 2) {
            if (publicationDataModel != null) {
                restorePublications();
                if (updatePageNumbers) {
                    publicationsTableFilterPanel.updatePageNumbers();
                }
            }
        } else {
            if (tableDataModel != null) {
                restoreTables();
                if (updatePageNumbers) {
                    tablesTableFilterPanel.updatePageNumbers();
                }
                restoreFields();
            }
        }
        updatePropertyBox();
    }

    public void refresh(boolean updatePageNumbers, TableFilterPanel tableFilterPanel) {

        if (tableFilterPanel.getTableModel() instanceof TableDataModel) {
            restoreTables();
            if (updatePageNumbers) {
                tablesTableFilterPanel.updatePageNumbers();
            }
            restoreFields();
        } else if (tableFilterPanel.getTableModel() instanceof FieldDataModel) {
            restoreTables();
            if (updatePageNumbers) {
                tablesTableFilterPanel.updatePageNumbers();
            }
            restoreFields();
        } else if (tableFilterPanel.getTableModel() instanceof RecordDataModel) {
            restoreRecords();
            if (updatePageNumbers) {
                recordsTableFilterPanel.updatePageNumbers();
            }
        } else if (tableFilterPanel.getTableModel() instanceof PublicationDataModel) {
            restorePublications();
            if (updatePageNumbers) {
                publicationsTableFilterPanel.updatePageNumbers();
            }
        }
        updatePropertyBox();
    }

    public long getSelectedDataSetId() {
        return tableDataModel.getSelectedIds()[0];
    }

    public void restoreTables() {
        // restore tables table
        tablesTable.setModel(tableDataModel.getTableModel());
        tablesTable.getColumn("ID").setMaxWidth(150);
        tablesTable.getTableHeader().setReorderingAllowed(false);
        tablesTableFilterPanel.updatePageNumbers();
    }

    public void restoreFields() {
        // restore fields table
        fieldsTable.setModel(fieldDataModel.getTableModel());
        fieldsTable.getColumn("ID").setMaxWidth(150);
        fieldsTable.getTableHeader().setReorderingAllowed(false);
    }

    public void restoreRecords() {
        // restore records table
        recordsTable.setModel(recordDataModel.getTableModel());
        recordsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        recordsTable.getColumn("ID").setMaxWidth(150);
        recordsTable.getTableHeader().setReorderingAllowed(false);
        recordsTableFilterPanel.updatePageNumbers();
    }

    public void restorePublications() {
        // restore publications table
        publicationsTable.setModel(publicationDataModel.getTableModel());
        publicationsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        publicationsTable.getColumn("ID").setMaxWidth(150);
        publicationsTable.getTableHeader().setReorderingAllowed(false);
        publicationsTableFilterPanel.updatePageNumbers();
    }

    public PropertiesPanelController getPropertiesPanelController() {
        return Main.getInstance().getPropertiesPanelController();
    }

    public void updatePropertyBox() {

        getPropertiesPanelController().destroyPanels();
        if (tablePropertiesPanel == null) {
            tablePropertiesPanel = new TablePropertiesPanel(this);
        }
        if (fieldPropertiesPanel == null) {
            fieldPropertiesPanel = new FieldPropertiesPanel(this);
        }
        if (recordPropertiesPanel == null) {
            recordPropertiesPanel = new RecordPropertiesPanel(this);
        }
        if (publicationsPropertiesPanel == null) {
            publicationsPropertiesPanel = new PublicationsPropertiesPanel(this);
        }

        if (SDMTabbedPane.getSelectedIndex() == 0) {
            getPropertiesPanelController().createPanel(tablePropertiesPanel);
            getPropertiesPanelController().createPanel(fieldPropertiesPanel);
        }

        if (SDMTabbedPane.getSelectedIndex() == 1) {
            getPropertiesPanelController().createPanel(recordPropertiesPanel);
        }

        if (SDMTabbedPane.getSelectedIndex() == 2) {
            getPropertiesPanelController().createPanel(publicationsPropertiesPanel);
        }

    }

    public void addTableButtonActionPerformed(ActionEvent e) {
        AddTableDialog addTableDialog = new AddTableDialog(Main.getInstance(), this);
        addTableDialog.setVisible(true);
    }

    public void refreshTablesButtonActionPerformed(ActionEvent e) {
        refresh();
    }

    public void refreshRecordsButtonActionPerformed(ActionEvent e) {
        restoreRecords();
    }

    public void deleteTableButtonActionPerformed(ActionEvent e) {
        if (tablesTable.getSelectedRow() != -1) {
            String message = Localizer.localize("UI", "SourceDataModelRemoveTablesMessage");
            String caption = Localizer.localize("UI", "SourceDataModelRemoveTablesTitle");
            int ret = javax.swing.JOptionPane
                .showConfirmDialog(Main.getInstance(), message, caption,
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (ret == javax.swing.JOptionPane.YES_OPTION) {
                DataSetController dsc = new DataSetController();
                long[] selectedDataSetIds = getSelectedDataSetIds();
                for (int i = 0; i < selectedDataSetIds.length; i++) {
                    dsc.removeDataSetById(selectedDataSetIds[i]);
                }
                refresh();
            }
        }
    }

    public void editTableButtonActionPerformed(ActionEvent e) {
        if (tablesTable.getSelectedRow() != -1) {
            EditTableDialog editTableDialog = new EditTableDialog(Main.getInstance(), this);
            editTableDialog.setVisible(true);
        }
    }

    public void addFieldButtonActionPerformed(ActionEvent e) {
        if (tablesTable.getSelectedRow() != -1) {
            AddFieldDialog addFieldDialog =
                new AddFieldDialog(Main.getInstance(), this, getNextAvailableOrderIndex());
            addFieldDialog.setVisible(true);
        }
    }

    public int getNextAvailableOrderIndex() {
        return getFieldDataModel().getMaximumOrderIndex();
    }

    public void removeFieldButtonActionPerformed(ActionEvent e) {
        if (fieldsTable.getSelectedRow() != -1) {
            // get id from row
            String message = Localizer.localize("UI", "SourceDataManagerRemoveFieldsMessage");
            String caption = Localizer.localize("UI", "SourceDataManagerRemoveFieldsTitle");
            int ret = javax.swing.JOptionPane
                .showConfirmDialog(Main.getInstance(), message, caption,
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (ret == javax.swing.JOptionPane.YES_OPTION) {
                long[] selectedSourceFieldIds = getSelectedSourceFieldIds();
                SourceFieldController sfc = new SourceFieldController();
                for (int i = 0; i < selectedSourceFieldIds.length; i++) {
                    sfc.removeSourceFieldById(selectedSourceFieldIds[i]);
                }
                restoreFields();
            }
        }
    }

    public void editFieldButtonActionPerformed(ActionEvent e) {
        if (fieldsTable.getSelectedRow() != -1) {
            EditFieldDialog editFieldDialog = new EditFieldDialog(Main.getInstance(), this);
            editFieldDialog.setVisible(true);
        }
    }

    public void addRecordButtonActionPerformed(ActionEvent e) {
        if (tablesTable.getSelectedRow() != -1) {
            RecordFieldDataDialog rfdd = new RecordFieldDataDialog(Main.getInstance(), this, true);
            rfdd.setVisible(true);
        }
    }

    public void deleteRecordButtonActionPerformed(ActionEvent e) {
        if (recordsTable.getSelectedRows().length > 0) {
            // get id from row
            String message = Localizer.localize("UI", "SourceDataManagerRemoveRecordsMessage");
            String caption = Localizer.localize("UI", "SourceDataManagerRemoveRecordsTitle");
            int ret = javax.swing.JOptionPane
                .showConfirmDialog(Main.getInstance(), message, caption,
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (ret == javax.swing.JOptionPane.YES_OPTION) {

                int[] selectedRows = recordsTable.getSelectedRows();
                long[] recordIDs = new long[selectedRows.length];
                for (int i = 0; i < selectedRows.length; i++) {
                    recordIDs[i] =
                        Long.parseLong((String) recordsTable.getValueAt(selectedRows[i], 0));
                }

                RecordController rc = new RecordController();
                rc.removeRecordsById(recordIDs);
                restoreRecords();
            }
        }
    }

    public void editRecordButtonActionPerformed(ActionEvent e) {
        if (recordsTable.getSelectedRow() != -1) {
            RecordFieldDataDialog rfdd = new RecordFieldDataDialog(Main.getInstance(), this, false);
            rfdd.setVisible(true);
        }
    }

    private void SDMTabbedPaneStateChanged(ChangeEvent e) {
        refresh();
    }

    public void importButtonActionPerformed(ActionEvent e) {
        if (tablesTable.getSelectedRow() != -1) {
            ImportRecordsDialog ird = new ImportRecordsDialog(Main.getInstance(), this);
            ird.setVisible(true);
        }
    }

    public void exportRecordsButtonActionPerformed(ActionEvent e) {
        if (recordsTable.getSelectedRow() != -1) {
            exportCSVData(true);
        }
    }

    public void exportAllRecordsButtonActionPerformed(ActionEvent e) {
        if (tablesTable.getSelectedRow() != -1) {
            exportCSVData(false);
        }
    }

    public void exportCSVData(boolean selectedRecordsOnly) {
        CSVWriter writer;
        File CSVFile = null;
        try {
            CSVFile = selectCSVFile();
            if (CSVFile != null) {
                writer =
                    new CSVWriter(new OutputStreamWriter(new FileOutputStream(CSVFile), "UTF-8"));

                EntityManager entityManager =
                    Main.getInstance().getJPAConfiguration().getEntityManager();
                Query sourceFieldQuery =
                    entityManager.createNamedQuery("SourceField.findByDataSetId");
                sourceFieldQuery.setParameter("dataSetId",
                    entityManager.find(DataSet.class, getSelectedDataSetId()));
                Object[] sourceFields = sourceFieldQuery.getResultList().toArray();
                if (sourceFields.length <= 0) {
                    return;
                }

                Vector<Long> sourceFieldIds = new Vector<Long>();

                String[] columns = new String[sourceFields.length];
                for (int i = 0; i < sourceFields.length; i++) {
                    SourceField sourceField = (SourceField) sourceFields[i];
                    sourceFieldIds.add(sourceField.getSourceFieldId());
                    columns[i] = sourceField.getSourceFieldName();
                }
                writer.writeNext(columns);

                Query recordQuery = null;

                if (selectedRecordsOnly) {

                    recordQuery = entityManager.createNamedQuery("Record.findByRecordIds");
                    long[] selectedRecordIds = getSelectedRecordIds();
                    List<Long> recordIds = new ArrayList<Long>();
                    for (int i = 0; i < selectedRecordIds.length; i++) {
                        recordIds.add(selectedRecordIds[i]);
                    }
                    recordQuery.setParameter("recordIds", recordIds);

                } else {

                    recordQuery = entityManager.createNamedQuery("Record.findByDataSetId");
                    recordQuery.setParameter("dataSetId",
                        entityManager.find(DataSet.class, getSelectedDataSetId()));

                }

                Iterator<Record> resultListIterator = recordQuery.getResultList().iterator();
                while (resultListIterator.hasNext()) {
                    Record rf = resultListIterator.next();

                    String[] rowData = new String[sourceFieldIds.size()];
                    Iterator<SourceText> stci = rf.getSourceTextCollection().iterator();
                    while (stci.hasNext()) {
                        SourceText st = stci.next();
                        int sfIndex =
                            sourceFieldIds.indexOf(st.getSourceFieldId().getSourceFieldId());
                        rowData[sfIndex] = st.getSourceTextString() + "";
                    }
                    writer.writeNext(rowData);
                }

                writer.close();
                entityManager.close();

                String message = Localizer.localize("UI", "CSVFileSaveSuccessMessage");
                String caption = Localizer.localize("UI", "SuccessTitle");
                javax.swing.JOptionPane.showConfirmDialog(this, message, caption,
                    javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);

            }
        } catch (Exception e1) {
            String message = Localizer.localize("UI", "CSVFileSaveFailureMessage");
            String caption = Localizer.localize("UI", "ErrorTitle");
            javax.swing.JOptionPane
                .showConfirmDialog(this, message, caption, javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
        }

    }

    private File selectCSVFile() {

        File file = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();
        filter.addExtension("csv");

        FileDialog fd = new FileDialog(Main.getInstance(),
            Localizer.localize("UI", "SourceDataManagerSaveCSVExportDataTitle"), FileDialog.SAVE);
        fd.setFilenameFilter(filter);
        fd.setFile(
            Localizer.localize("UI", "SourceDataManagerRecordsExportFilenamePrefix") + ".csv");

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
            try {
                Globals.setLastDirectory(file.getCanonicalPath());
            } catch (IOException ldex) {
            }
        } else {
            return null;
        }

        return file;

    }

    private void tablesPanelComponentResized(ComponentEvent e) {
        resetVerticalDivider();
    }

    public void initComponents() {

        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        SDMTabbedPane = new JTabbedPane();
        tablesPanel = new JPanel();
        tablesSplitPane = new JSplitPane();
        tableListPanel = new JPanel();
        scrollPane3 = new JScrollPane();
        tablesTable = new JTable();
        tablesTableFilterPanel = new TableFilterPanel();
        fieldsScrollPane = new JScrollPane();
        fieldsTable = new JTable();
        recordsPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        recordsTable = new JTable();
        recordsTableFilterPanel = new TableFilterPanel();
        publicationsPanel = new JPanel();
        scrollPane4 = new JScrollPane();
        publicationsTable = new JTable();
        panel1 = new JPanel();
        publicationsTableFilterPanel = new TableFilterPanel();

        //======== this ========
        setVisible(true);
        setBorder(null);
        setLayout(new BorderLayout());

        //======== SDMTabbedPane ========
        {
            SDMTabbedPane.setFont(UIManager.getFont("TabbedPane.font"));
            SDMTabbedPane.addChangeListener(new ChangeListener() {
                @Override public void stateChanged(ChangeEvent e) {
                    SDMTabbedPaneStateChanged(e);
                }
            });

            //======== tablesPanel ========
            {
                tablesPanel.setOpaque(false);
                tablesPanel.addComponentListener(new ComponentAdapter() {
                    @Override public void componentResized(ComponentEvent e) {
                        tablesPanelComponentResized(e);
                    }
                });
                tablesPanel.setLayout(new BorderLayout());

                //======== tablesSplitPane ========
                {
                    tablesSplitPane.setBorder(null);
                    tablesSplitPane.setResizeWeight(0.7);
                    tablesSplitPane.setOpaque(false);
                    tablesSplitPane.setDividerSize(9);

                    //======== tableListPanel ========
                    {
                        tableListPanel.setOpaque(false);
                        tableListPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout) tableListPanel.getLayout()).columnWidths =
                            new int[] {0, 0};
                        ((GridBagLayout) tableListPanel.getLayout()).rowHeights =
                            new int[] {0, 0, 0};
                        ((GridBagLayout) tableListPanel.getLayout()).columnWeights =
                            new double[] {1.0, 1.0E-4};
                        ((GridBagLayout) tableListPanel.getLayout()).rowWeights =
                            new double[] {1.0, 0.0, 1.0E-4};

                        //======== scrollPane3 ========
                        {
                            scrollPane3.setRequestFocusEnabled(false);

                            //---- tablesTable ----
                            tablesTable.setRequestFocusEnabled(false);
                            tablesTable.setShowHorizontalLines(false);
                            tablesTable.setShowVerticalLines(false);
                            tablesTable.setFont(UIManager.getFont("Table.font"));
                            tablesTable.setShowGrid(false);
                            tablesTable.getTableHeader()
                                .setFont(UIManager.getFont("TableHeader.font"));
                            scrollPane3.setViewportView(tablesTable);
                        }
                        tableListPanel.add(scrollPane3,
                            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                        tableListPanel.add(tablesTableFilterPanel,
                            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                    }
                    tablesSplitPane.setLeftComponent(tableListPanel);

                    //======== fieldsScrollPane ========
                    {
                        fieldsScrollPane.setRequestFocusEnabled(false);

                        //---- fieldsTable ----
                        fieldsTable.setRequestFocusEnabled(false);
                        fieldsTable.setShowHorizontalLines(false);
                        fieldsTable.setShowVerticalLines(false);
                        fieldsTable.setFont(UIManager.getFont("Table.font"));
                        fieldsTable.setShowGrid(false);
                        fieldsTable.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));
                        fieldsScrollPane.setViewportView(fieldsTable);
                    }
                    tablesSplitPane.setRightComponent(fieldsScrollPane);
                }
                tablesPanel.add(tablesSplitPane, BorderLayout.CENTER);
            }
            SDMTabbedPane.addTab("Tables", new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/one.png")),
                tablesPanel);

            //======== recordsPanel ========
            {
                recordsPanel.setOpaque(false);
                recordsPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) recordsPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) recordsPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout) recordsPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) recordsPanel.getLayout()).rowWeights =
                    new double[] {1.0, 0.0, 1.0E-4};

                //======== scrollPane1 ========
                {
                    scrollPane1.setRequestFocusEnabled(false);

                    //---- recordsTable ----
                    recordsTable.setRequestFocusEnabled(false);
                    recordsTable.setShowHorizontalLines(false);
                    recordsTable.setShowVerticalLines(false);
                    recordsTable.setFont(UIManager.getFont("Table.font"));
                    recordsTable.setCellSelectionEnabled(true);
                    recordsTable.setShowGrid(false);
                    recordsTable.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));
                    scrollPane1.setViewportView(recordsTable);
                }
                recordsPanel.add(scrollPane1,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                recordsPanel.add(recordsTableFilterPanel,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            SDMTabbedPane.addTab("Records", new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/two.png")),
                recordsPanel);

            //======== publicationsPanel ========
            {
                publicationsPanel.setOpaque(false);
                publicationsPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) publicationsPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) publicationsPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout) publicationsPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) publicationsPanel.getLayout()).rowWeights =
                    new double[] {1.0, 0.0, 1.0E-4};

                //======== scrollPane4 ========
                {

                    //---- publicationsTable ----
                    publicationsTable.setShowHorizontalLines(false);
                    publicationsTable.setShowVerticalLines(false);
                    publicationsTable.setFont(UIManager.getFont("Table.font"));
                    publicationsTable.setShowGrid(false);
                    publicationsTable.getTableHeader()
                        .setFont(UIManager.getFont("TableHeader.font"));
                    scrollPane4.setViewportView(publicationsTable);
                }
                publicationsPanel.add(scrollPane4,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

                //======== panel1 ========
                {
                    panel1.setOpaque(false);
                    panel1.setLayout(new BorderLayout());
                    panel1.add(publicationsTableFilterPanel, BorderLayout.CENTER);
                }
                publicationsPanel.add(panel1,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            SDMTabbedPane.addTab("Publications", new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/three.png")),
                publicationsPanel);
        }
        add(SDMTabbedPane, BorderLayout.CENTER);
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JTabbedPane SDMTabbedPane;
    private JPanel tablesPanel;
    private JSplitPane tablesSplitPane;
    private JPanel tableListPanel;
    private JScrollPane scrollPane3;
    private JTable tablesTable;
    private TableFilterPanel tablesTableFilterPanel;
    private JScrollPane fieldsScrollPane;
    private JTable fieldsTable;
    private JPanel recordsPanel;
    private JScrollPane scrollPane1;
    private JTable recordsTable;
    private TableFilterPanel recordsTableFilterPanel;
    private JPanel publicationsPanel;
    private JScrollPane scrollPane4;
    private JTable publicationsTable;
    private JPanel panel1;
    private TableFilterPanel publicationsTableFilterPanel;
    // JFormDesigner - End of variables declaration //GEN-END:variables

    public TableDataModel getTableDataModel() {
        return tableDataModel;
    }

    public FieldDataModel getFieldDataModel() {
        return fieldDataModel;
    }

    public RecordDataModel getRecordDataModel() {
        return recordDataModel;
    }

    public PublicationDataModel getPublicationDataModel() {
        return publicationDataModel;
    }

    public TablePropertiesPanel getTablePanel() {
        return tablePropertiesPanel;
    }

    public void setTablePanel(TablePropertiesPanel tablePanel) {
        this.tablePropertiesPanel = tablePanel;
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

    public long[] getSelectedSourceFieldIds() {
        int selectedSourceFieldRows[] = fieldsTable.getSelectedRows();
        long selectedSourceFieldIds[] = new long[] {};
        if (selectedSourceFieldRows.length > 0) {
            selectedSourceFieldIds = new long[selectedSourceFieldRows.length];
            for (int i = 0; i < selectedSourceFieldRows.length; i++) {
                selectedSourceFieldIds[i] =
                    Long.parseLong((String) fieldsTable.getValueAt(selectedSourceFieldRows[i], 0));
            }
        }
        return selectedSourceFieldIds;
    }

    public long[] getSelectedRecordIds() {
        int selectedRecordRows[] = recordsTable.getSelectedRows();
        long selectedRecordIds[] = new long[] {};

        if (selectedRecordRows.length > 0) {
            selectedRecordIds = new long[selectedRecordRows.length];
            for (int i = 0; i < selectedRecordRows.length; i++) {
                selectedRecordIds[i] =
                    Long.parseLong((String) recordsTable.getValueAt(selectedRecordRows[i], 0));
            }
        }
        return selectedRecordIds;
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

    public String getSelectedTableName() {

        if (this.activeParentId == -1) {
            return Localizer.localize("UI", "SourceDataManagerNoneSelectedMessage");
        }

        String activeParentIdStr = activeParentId + "";

        for (int i = 0; i < tablesTable.getRowCount(); i++) {
            if (tablesTable.getValueAt(i, 0).equals(activeParentIdStr)) {
                return (String) tablesTable.getValueAt(i, 1);
            }
        }

        return "";

    }

    public void refreshPublicationsButtonActionPerformed(ActionEvent e) {
        restorePublications();
    }

    public void deletePublicationsButtonActionPerformed(ActionEvent e) {
        if (publicationsTable.getSelectedRows().length > 0) {
            // get id from row
            String message = Localizer.localize("UI", "SourceDataManagerRemovePublicationsMessage");
            String caption = Localizer.localize("UI", "SourceDataManagerRemovePublicationsTitle");
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

    public void printPublicationsButtonActionPerformed(ActionEvent e) {

        final long[] publicationIds = getSelectedPublicationIds();

        if (publicationIds != null && publicationIds.length > 0) {

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    FormPDFGenerator
                        .exportPublishedForms(publicationIds[0], FormPublisher.COLLATED_FORMS, true,
                            getRootPane());
                }
            });

        }

    }

    public void exportPublicationsButtonActionPerformed(ActionEvent e) {

        final long[] publicationIds = getSelectedPublicationIds();
        if (publicationIds != null && publicationIds.length > 0) {

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {

                    Object[] options =
                        {Localizer.localize("UI", "PublicationPropertiesCollateFormsOption"),
                            Localizer.localize("UI",
                                "PublicationPropertiesExportIndividuallyOption")};

                    String msg =
                        Localizer.localize("UI", "PublicationPropertiesCollateFormsMessage");

                    int result = JOptionPane.showOptionDialog(Main.getInstance(), msg,
                        Localizer.localize("UI", "PublicationPropertiesCollateFormsTitle"),
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
                        options[0]);

                    if (result == 0) {
                        FormPDFGenerator
                            .exportPublishedForms(publicationIds[0], FormPublisher.COLLATED_FORMS,
                                false, getRootPane());
                    } else {
                        FormPDFGenerator
                            .exportPublishedForms(publicationIds[0], FormPublisher.SEPARATED_FORMS,
                                false, getRootPane());
                    }

                }
            });

        }

    }

    public void renamePublicationsButtonActionPerformed(ActionEvent e) {
        if (publicationsTable.getSelectedRow() != -1) {
            RenamePublicationDialog renamePublicationDialog =
                new RenamePublicationDialog(Main.getInstance(), this);
            renamePublicationDialog.setVisible(true);
        }
    }

    public long getSelectedPublicationId() {
        if (getSelectedPublicationIds() != null && getSelectedPublicationIds().length > 0) {
            return getSelectedPublicationIds()[0];
        } else {
            return 0;
        }
    }

    public void fillButtonActionPerformed(ActionEvent e) {
        if (recordsTable.getSelectedRow() != -1 && recordsTable.getSelectedColumn() != -1
            && recordsTable.getSelectedColumn() > 0) {
            String fieldname =
                recordsTable.getModel().getColumnName(recordsTable.getSelectedColumn());
            showFillTableDialog(getSelectedRecordIds()[0], fieldname, (String) recordsTable
                .getValueAt(recordsTable.getSelectedRow(), recordsTable.getSelectedColumn()));
        } else {
            Misc.showErrorMsg(getRootPane().getTopLevelAncestor(),
                Localizer.localize("UI", "FillTablePleaseSelectCellErrorMessage"));
        }
    }

    private void showFillTableDialog(final long recordId, final String fieldname,
        final String value) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                FillTableDialog ftd =
                    new FillTableDialog((Frame) getRootPane().getTopLevelAncestor());
                ftd.setVisible(true);
                if (ftd.getDialogResult() == JOptionPane.OK_OPTION) {

                    int fillType = ftd.getFillType();
                    int stepSize = ftd.getStepSize();
                    final int total = ftd.getCellsToFill();
                    String valueStr = value;
                    if ((fillType == FillTableController.FILL_NUMERIC_SERIES) && (value == null)) {
                        valueStr = "0";
                    }
                    final FillTableController ftc =
                        new FillTableController(recordId, fieldname, valueStr, fillType, stepSize,
                            total);

                    final ProcessingStatusDialog fillStatusDialog =
                        new ProcessingStatusDialog(Main.getInstance());
                    class FillRunner implements Runnable {

                        public void run() {

                            try {
                                ftc.begin();
                                do {
                                    fillStatusDialog.setMessage(String
                                        .format(Localizer.localize("UI", "FillTableStatusMessage"),
                                            ftc.getCount(), ftc.getTotal()));
                                } while (!(fillStatusDialog.isInterrupted()) && ftc.fillNext());
                                ftc.commit();
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        refresh();
                                    }
                                });
                            } catch (Exception ex) {
                                ftc.rollback();
                            } finally {
                                if (fillStatusDialog != null) {
                                    fillStatusDialog.dispose();
                                }
                            }
                        }
                    }

                    FillRunner fillRunner = new FillRunner();
                    Thread thread = new Thread(fillRunner);
                    thread.start();

                    fillStatusDialog.setModal(true);
                    fillStatusDialog.setVisible(true);

                    if (fillStatusDialog != null) {
                        fillStatusDialog.dispose();
                    }

                }
            }
        });
    }

}
