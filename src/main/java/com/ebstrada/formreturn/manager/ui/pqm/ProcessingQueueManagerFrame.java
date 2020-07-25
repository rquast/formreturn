package com.ebstrada.formreturn.manager.ui.pqm;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.event.*;

import org.apache.commons.io.FileUtils;
import org.apache.openjpa.persistence.RollbackException;

import com.ebstrada.formreturn.api.messaging.ProcessingStatusDialog;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.JPAConfiguration;
import com.ebstrada.formreturn.manager.persistence.jpa.IncomingImage;
import com.ebstrada.formreturn.manager.persistence.viewer.GenericDataViewer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.ImagePreviewPanel;
import com.ebstrada.formreturn.manager.ui.component.TableFilterPanel;
import com.ebstrada.formreturn.manager.ui.component.ZoomSettings;
import com.ebstrada.formreturn.manager.ui.dialog.ImagePreviewFrame;
import com.ebstrada.formreturn.manager.ui.dialog.LoadingDialog;
import com.ebstrada.formreturn.manager.ui.filefilter.FilenameExtensionFilter;
import com.ebstrada.formreturn.manager.ui.panel.PropertiesPanelController;
import com.ebstrada.formreturn.manager.ui.pqm.panel.UnidentifiedImagesPropertiesPanel;
import com.ebstrada.formreturn.manager.ui.pqm.panel.UnprocessedImagesPropertiesPanel;
import com.ebstrada.formreturn.manager.ui.pqm.model.UnidentifiedImageDataModel;
import com.ebstrada.formreturn.manager.ui.pqm.model.UnprocessedImageDataModel;
import com.ebstrada.formreturn.manager.ui.reprocessor.frame.ReprocessorFrame;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.TemplateFormPageID;

public class ProcessingQueueManagerFrame extends JPanel implements GenericDataViewer {

    private static final long serialVersionUID = 1L;
    private UnprocessedImagesPropertiesPanel unprocessedImagesPropertiesPanel;
    private UnidentifiedImagesPropertiesPanel unidentifiedImagesPropertiesPanel;

    private UnidentifiedImageDataModel unidentifiedImageDataModel;
    private UnprocessedImageDataModel unprocessedImageDataModel;

    private SwingWorker<ImagePreviewPanel, Void> worker;

    private SwingWorker<Void, Void> exportWorker;

    private ZoomSettings zoomSettings = new ZoomSettings();

    public ProcessingQueueManagerFrame() {
        initComponents();

        PQMTabbedPane
            .setTitleAt(0, Localizer.localize("UI", "ProcessingQueueUnprocessedImagesTabTitle"));
        PQMTabbedPane
            .setTitleAt(1, Localizer.localize("UI", "ProcessingQueueUnidentifiedImagesTabTitle"));

        unprocessedImageDataModel = new UnprocessedImageDataModel();
        unprocessedFilterPanel.setTableModel(unprocessedImageDataModel);
        unprocessedFilterPanel.setTableViewer(this);

        unidentifiedImageDataModel = new UnidentifiedImageDataModel();
        unidentifiedFilterPanel.setTableModel(unidentifiedImageDataModel);
        unidentifiedFilterPanel.setTableViewer(this);

        refresh();

        SelectionListener unidentifiedImageListener = new SelectionListener(unidentifiedImagesTable,
            SelectionListener.UNIDENTIFIED_SELECTION);
        unidentifiedImagesTable.getSelectionModel()
            .addListSelectionListener(unidentifiedImageListener);
    }

    public class SelectionListener implements ListSelectionListener {

        public static final int UNPROCESSED_SELECTION = 0;
        public static final int UNIDENTIFIED_SELECTION = 1;

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
                if (selection == UNPROCESSED_SELECTION) {
                    if (unprocessedImagesTable.getSelectedRow() != -1) {
                        unprocessedImageDataModel.setSelectedIds(getSelectedUnprocessedImageIds());
                    } else {
                        unprocessedImageDataModel.setSelectedIds(new long[] {});
                    }
                }
                if (selection == UNIDENTIFIED_SELECTION) {
                    if (unidentifiedImagesTable.getSelectedRow() != -1) {
                        unidentifiedImageDataModel
                            .setSelectedIds(getSelectedUnidentifiedImageIds());
                        try {
                            loadUnidentifiedImagePreview();
                        } catch (IOException e1) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                        }
                    } else {
                        unidentifiedImageDataModel.setSelectedIds(new long[] {});
                        clearUnidentifiedImagePreview();
                    }
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


    public PropertiesPanelController getPropertiesPanelController() {
        return Main.getInstance().getPropertiesPanelController();
    }

    public long[] getSelectedUnidentifiedImageIds() {
        int selectedUnidentifiedImageRows[] = unidentifiedImagesTable.getSelectedRows();
        long selectedUnidentifiedImageIds[] = new long[] {};
        if (selectedUnidentifiedImageRows.length > 0) {
            selectedUnidentifiedImageIds = new long[selectedUnidentifiedImageRows.length];
            for (int i = 0; i < selectedUnidentifiedImageRows.length; i++) {
                selectedUnidentifiedImageIds[i] = Long.parseLong((String) unidentifiedImagesTable
                    .getValueAt(selectedUnidentifiedImageRows[i], 0));
            }
        }
        return selectedUnidentifiedImageIds;
    }

    public long[] getSelectedUnprocessedImageIds() {
        int selectedUnprocessedImageRows[] = unprocessedImagesTable.getSelectedRows();
        long selectedUnprocessedImageIds[] = new long[] {};
        if (selectedUnprocessedImageRows.length > 0) {
            selectedUnprocessedImageIds = new long[selectedUnprocessedImageRows.length];
            for (int i = 0; i < selectedUnprocessedImageRows.length; i++) {
                selectedUnprocessedImageIds[i] = Long.parseLong(
                    (String) unprocessedImagesTable.getValueAt(selectedUnprocessedImageRows[i], 0));
            }
        }
        return selectedUnprocessedImageIds;
    }

    private void restoreUnprocessedImages() {
        unprocessedImagesTable.setModel(unprocessedImageDataModel.getTableModel());
        unprocessedImagesTable.getColumn("ID").setMaxWidth(150);
        unprocessedImagesTable.getTableHeader().setReorderingAllowed(false);
    }

    private void restoreUnidentifiedImages() {
        unidentifiedImagesTable.setModel(unidentifiedImageDataModel.getTableModel());
        unidentifiedImagesTable.getColumn("ID").setMaxWidth(150);
        unidentifiedImagesTable.getTableHeader().setReorderingAllowed(false);
    }

    public void loadUnidentifiedImagePreview() throws IOException {

        if (this.worker != null) {
            return;
        }

        worker = new SwingWorker<ImagePreviewPanel, Void>() {

            public ImagePreviewPanel doInBackground() {
                byte[] imageData = unidentifiedImageDataModel.getUnidentifiedImage();
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
        worker.execute();
        clearUnidentifiedImagePreview();

    }

    public void closeWorker() {
        this.worker = null;
    }

    public void clearUnidentifiedImagePreview() {
        imagePreviewContainer.removeAll();
        imagePreviewContainer.revalidate();
        repaint();
    }

    public void updatePropertyBox() {

        getPropertiesPanelController().destroyPanels();
        if (unprocessedImagesPropertiesPanel == null) {
            unprocessedImagesPropertiesPanel = new UnprocessedImagesPropertiesPanel(this);
        }
        if (unidentifiedImagesPropertiesPanel == null) {
            unidentifiedImagesPropertiesPanel = new UnidentifiedImagesPropertiesPanel(this);
        }

        if (PQMTabbedPane.getSelectedIndex() == 0) {
            getPropertiesPanelController().createPanel(unprocessedImagesPropertiesPanel);
        }

        if (PQMTabbedPane.getSelectedIndex() == 1) {
            getPropertiesPanelController().createPanel(unidentifiedImagesPropertiesPanel);
        }

    }

    public void refreshUnprocessedImagesButtonActionPerformed(ActionEvent e) {
        restoreUnprocessedImages();
    }

    public void reprocessButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reprocessNew();
            }
        });
    }

    public void reprocessNew() {

        final LoadingDialog ld = new LoadingDialog(Main.getInstance());
        ld.setVisible(true);

        Main.getInstance().blockInput();

        SwingWorker<ReprocessorFrame, Void> worker = new SwingWorker<ReprocessorFrame, Void>() {
            protected ReprocessorFrame doInBackground() throws InterruptedException {
                byte[] imageData = unidentifiedImageDataModel.getUnidentifiedImage();
                long incomingImageId = unidentifiedImageDataModel.getSelectedIds()[0];
                String incomingImageName = unidentifiedImageDataModel.getUnidentifiedImageName();
                if (imageData != null && imageData.length > 0) {
                    if (Main.getInstance().checkReprocessorFrameOpen(incomingImageId,
                        ReprocessorFrame.UNIDENTIFIED_IMAGE) == true) {
                        return null;
                    }
                    try {
                        ReprocessorFrame reprocessorFrame =
                            new ReprocessorFrame(imageData, incomingImageId, incomingImageName,
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

    public void reprocess() {

        final LoadingDialog ld = new LoadingDialog(Main.getInstance());
        ld.setVisible(true);

        Main.getInstance().blockInput();

        SwingWorker<ImagePreviewFrame, Void> worker = new SwingWorker<ImagePreviewFrame, Void>() {
            protected ImagePreviewFrame doInBackground() throws InterruptedException {
                byte[] imageData = unidentifiedImageDataModel.getUnidentifiedImage();
                if (imageData != null && imageData.length > 0) {
                    ImagePreviewFrame ipf =
                        new ImagePreviewFrame(Main.getInstance(), imageData, false);
                    return ipf;
                } else {
                    return null;
                }
            }

            protected void done() {
                ImagePreviewFrame ipd = null;
                Main.getInstance().unblockInput();
                ld.dispose();

                try {
                    ipd = get();
                } catch (InterruptedException e) {
                } catch (ExecutionException e) {
                }
                if (ipd != null) {
                    ipd.setVisible(true);
                }
            }
        };
        worker.execute();

    }

    public void uploadImageFolderButtonActionPerformed(ActionEvent e) {
        JPAConfiguration jpaConfiguration = Main.getInstance().getJPAConfiguration();
        File file;
        try {
            file = Misc.getUploadImageFolder();
            if (file == null) {
                return;
            }
            Misc.uploadImageFolder(jpaConfiguration, file, new TemplateFormPageID(), this);
        } catch (IOException ioex) {
            Misc.showErrorMsg(this, ioex.getLocalizedMessage());
            Misc.printStackTrace(ioex);
        }
    }

    public void uploadImagesButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                uploadImages();
            }
        });
    }

    public void uploadImages() {
        JPAConfiguration jpaConfiguration = Main.getInstance().getJPAConfiguration();
        File file;
        try {
            file = Misc.getUploadImageFile();
            if (file == null) {
                return;
            }
            Misc.uploadImage(jpaConfiguration, file, new TemplateFormPageID(), this);
        } catch (IOException e) {
            Misc.showErrorMsg(this, e.getLocalizedMessage());
            Misc.printStackTrace(e);
        }
    }

    public void deleteUnprocessedImagesButtonActionPerformed(ActionEvent e) {
        long[] selectedUnprocessedImageIds = getSelectedUnprocessedImageIds();
        if (selectedUnprocessedImageIds != null && selectedUnprocessedImageIds.length > 0) {
            if (deleteUnprocessedImages(selectedUnprocessedImageIds)) {
                restoreUnprocessedImages();
            }
        }
    }

    public void refreshUnidentifiedImagesButtonActionPerformed(ActionEvent e) {
        restoreUnidentifiedImages();
    }

    public void deleteUnidentifiedImagesButtonActionPerformed(ActionEvent e) {
        long[] selectedUnidentifiedImageIds = getSelectedUnidentifiedImageIds();
        if (selectedUnidentifiedImageIds != null && selectedUnidentifiedImageIds.length > 0) {
            if (deleteUnidentifiedImages(getSelectedUnidentifiedImageIds())) {
                restoreUnidentifiedImages();
            }
        }
    }

    public boolean deleteUnidentifiedImages(long[] selectedUnidentifiedImageIds) {
        if (selectedUnidentifiedImageIds.length > 0) {
            String message =
                Localizer.localize("UI", "ProcessingQueueConfirmRemoveUnidentifiedImageMessage");
            String caption =
                Localizer.localize("UI", "ProcessingQueueConfirmRemoveUnidentifiedImageTitle");
            int ret = javax.swing.JOptionPane
                .showConfirmDialog(Main.getInstance(), message, caption,
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (ret == javax.swing.JOptionPane.YES_OPTION) {
                for (int i = 0; i < selectedUnidentifiedImageIds.length; i++) {

                    EntityManager entityManager =
                        Main.getInstance().getJPAConfiguration().getEntityManager();

                    if (entityManager == null) {
                        return false;
                    }

                    try {
                        entityManager.getTransaction().begin();
                        entityManager.flush();
                        IncomingImage incomingImage = entityManager
                            .find(IncomingImage.class, selectedUnidentifiedImageIds[i]);
                        entityManager.remove(incomingImage);
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
                        return false;
                    } finally {
                        entityManager.close();
                    }

                }
                return true;
            }
        }
        return false;
    }

    public boolean deleteUnprocessedImages(long[] selectedUnprocessedImageIds) {
        if (selectedUnprocessedImageIds.length > 0) {
            String message =
                Localizer.localize("UI", "ProcessingQueueConfirmRemoveUnprocessedImageMessage");
            String caption =
                Localizer.localize("UI", "ProcessingQueueConfirmRemoveUnprocessedImageTitle");
            int ret = javax.swing.JOptionPane
                .showConfirmDialog(Main.getInstance(), message, caption,
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (ret == javax.swing.JOptionPane.YES_OPTION) {
                for (int i = 0; i < selectedUnprocessedImageIds.length; i++) {

                    EntityManager entityManager =
                        Main.getInstance().getJPAConfiguration().getEntityManager();

                    if (entityManager == null) {
                        return false;
                    }

                    try {
                        entityManager.getTransaction().begin();
                        entityManager.flush();
                        IncomingImage incomingImage =
                            entityManager.find(IncomingImage.class, selectedUnprocessedImageIds[i]);
                        entityManager.remove(incomingImage);
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
                        return false;
                    } finally {
                        entityManager.close();
                    }

                }
                return true;
            }
        }
        return false;
    }

    private void PQMTabbedPaneStateChanged(ChangeEvent e) {
        refresh();
    }

    public void resetVerticalDivider() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                unidentifiedImageSplitPane.setDividerLocation(0.7d);
            }
        });
    }

    private void unidentifiedImagesPanelComponentResized(ComponentEvent e) {
        resetVerticalDivider();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        PQMTabbedPane = new JTabbedPane();
        unprocessedImagesPanel = new JPanel();
        unprocessedImagesScrollPane = new JScrollPane();
        unprocessedImagesTable = new JTable();
        unprocessedFilterPanel = new TableFilterPanel();
        unidentifiedImagesPanel = new JPanel();
        unidentifiedImageSplitPane = new JSplitPane();
        unidentifiedImagesListPanel = new JPanel();
        unidentifiedImagesListScrollPane = new JScrollPane();
        unidentifiedImagesTable = new JTable();
        unidentifiedFilterPanel = new TableFilterPanel();
        imagePreviewContainer = new JPanel();

        //======== this ========
        setLayout(new BorderLayout());

        //======== PQMTabbedPane ========
        {
            PQMTabbedPane.setFont(UIManager.getFont("TabbedPane.font"));
            PQMTabbedPane.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    PQMTabbedPaneStateChanged(e);
                }
            });

            //======== unprocessedImagesPanel ========
            {
                unprocessedImagesPanel.setOpaque(false);
                unprocessedImagesPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) unprocessedImagesPanel.getLayout()).columnWidths =
                    new int[] {0, 0};
                ((GridBagLayout) unprocessedImagesPanel.getLayout()).rowHeights =
                    new int[] {0, 0, 0};
                ((GridBagLayout) unprocessedImagesPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) unprocessedImagesPanel.getLayout()).rowWeights =
                    new double[] {1.0, 0.0, 1.0E-4};

                //======== unprocessedImagesScrollPane ========
                {

                    //---- unprocessedImagesTable ----
                    unprocessedImagesTable.setShowHorizontalLines(false);
                    unprocessedImagesTable.setShowVerticalLines(false);
                    unprocessedImagesTable.setFont(UIManager.getFont("Table.font"));
                    unprocessedImagesTable.setShowGrid(false);
                    unprocessedImagesTable.getTableHeader()
                        .setFont(UIManager.getFont("TableHeader.font"));
                    unprocessedImagesScrollPane.setViewportView(unprocessedImagesTable);
                }
                unprocessedImagesPanel.add(unprocessedImagesScrollPane,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                unprocessedImagesPanel.add(unprocessedFilterPanel,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            PQMTabbedPane.addTab("Unprocessed Images", new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/one.png")),
                unprocessedImagesPanel);


            //======== unidentifiedImagesPanel ========
            {
                unidentifiedImagesPanel.setOpaque(false);
                unidentifiedImagesPanel.addComponentListener(new ComponentAdapter() {
                    @Override public void componentResized(ComponentEvent e) {
                        unidentifiedImagesPanelComponentResized(e);
                    }
                });
                unidentifiedImagesPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) unidentifiedImagesPanel.getLayout()).columnWidths =
                    new int[] {0, 0};
                ((GridBagLayout) unidentifiedImagesPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout) unidentifiedImagesPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) unidentifiedImagesPanel.getLayout()).rowWeights =
                    new double[] {1.0, 1.0E-4};

                //======== unidentifiedImageSplitPane ========
                {
                    unidentifiedImageSplitPane.setResizeWeight(0.7);
                    unidentifiedImageSplitPane.setBorder(null);
                    unidentifiedImageSplitPane.setOpaque(false);
                    unidentifiedImageSplitPane.setDividerSize(9);

                    //======== unidentifiedImagesListPanel ========
                    {
                        unidentifiedImagesListPanel.setOpaque(false);
                        unidentifiedImagesListPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout) unidentifiedImagesListPanel.getLayout()).columnWidths =
                            new int[] {0, 0};
                        ((GridBagLayout) unidentifiedImagesListPanel.getLayout()).rowHeights =
                            new int[] {0, 0, 0};
                        ((GridBagLayout) unidentifiedImagesListPanel.getLayout()).columnWeights =
                            new double[] {1.0, 1.0E-4};
                        ((GridBagLayout) unidentifiedImagesListPanel.getLayout()).rowWeights =
                            new double[] {1.0, 0.0, 1.0E-4};

                        //======== unidentifiedImagesListScrollPane ========
                        {

                            //---- unidentifiedImagesTable ----
                            unidentifiedImagesTable.setShowHorizontalLines(false);
                            unidentifiedImagesTable.setShowVerticalLines(false);
                            unidentifiedImagesTable.setFont(UIManager.getFont("Table.font"));
                            unidentifiedImagesTable.setShowGrid(false);
                            unidentifiedImagesTable.getTableHeader()
                                .setFont(UIManager.getFont("TableHeader.font"));
                            unidentifiedImagesListScrollPane
                                .setViewportView(unidentifiedImagesTable);
                        }
                        unidentifiedImagesListPanel.add(unidentifiedImagesListScrollPane,
                            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                        unidentifiedImagesListPanel.add(unidentifiedFilterPanel,
                            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                    }
                    unidentifiedImageSplitPane.setLeftComponent(unidentifiedImagesListPanel);

                    //======== imagePreviewContainer ========
                    {
                        imagePreviewContainer.setBackground(Color.white);
                        imagePreviewContainer.setLayout(new BorderLayout());
                    }
                    unidentifiedImageSplitPane.setRightComponent(imagePreviewContainer);
                }
                unidentifiedImagesPanel.add(unidentifiedImageSplitPane,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            PQMTabbedPane.addTab("Unidentified Images", new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/two.png")),
                unidentifiedImagesPanel);

        }
        add(PQMTabbedPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTabbedPane PQMTabbedPane;
    private JPanel unprocessedImagesPanel;
    private JScrollPane unprocessedImagesScrollPane;
    private JTable unprocessedImagesTable;
    private TableFilterPanel unprocessedFilterPanel;
    private JPanel unidentifiedImagesPanel;
    private JSplitPane unidentifiedImageSplitPane;
    private JPanel unidentifiedImagesListPanel;
    private JScrollPane unidentifiedImagesListScrollPane;
    private JTable unidentifiedImagesTable;
    private TableFilterPanel unidentifiedFilterPanel;
    private JPanel imagePreviewContainer;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    public void refresh() {
        refresh(true);
    }

    public void refresh(boolean updatePageNumbers) {
        if (PQMTabbedPane.getSelectedIndex() == 0) {
            if (unprocessedImageDataModel != null) {
                restoreUnprocessedImages();
                if (updatePageNumbers) {
                    unprocessedFilterPanel.updatePageNumbers();
                }
            }
        } else if (PQMTabbedPane.getSelectedIndex() == 1) {
            if (unidentifiedImageDataModel != null) {
                restoreUnidentifiedImages();
                if (updatePageNumbers) {
                    unidentifiedFilterPanel.updatePageNumbers();
                }
            }
        }
        updatePropertyBox();
    }

    public void refresh(boolean updatePageNumbers, TableFilterPanel tableFilterPanel) {

        if (tableFilterPanel.getTableModel() instanceof UnprocessedImageDataModel) {
            restoreUnprocessedImages();
            if (updatePageNumbers) {
                unprocessedFilterPanel.updatePageNumbers();
            }
        } else if (tableFilterPanel.getTableModel() instanceof UnidentifiedImageDataModel) {
            restoreUnidentifiedImages();
            if (updatePageNumbers) {
                unidentifiedFilterPanel.updatePageNumbers();
            }
        }
        updatePropertyBox();

    }

    public void exportUnidentifiedImagesActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                long[] selectedUnidentifiedImageIds = getSelectedUnidentifiedImageIds();
                if (selectedUnidentifiedImageIds != null
                    && selectedUnidentifiedImageIds.length > 0) {
                    exportUnidentifiedImages(getSelectedUnidentifiedImageIds());
                }
            }
        });
    }

    private void exportUnidentifiedImages(final long[] selectedUnidentifiedImageIds) {

        if (this.exportWorker != null) {
            return;
        }

        final JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser
            .setDialogTitle(Localizer.localize("UI", "UnidentifiedImagesSelectExportFolderTitle"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.rescanCurrentDirectory();
        if (chooser.showDialog(Main.getInstance(), Localizer.localize("UI", "ChooseButtonText"))
            == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile() == null) {
                return;
            }
        } else {
            return;
        }

        final ProcessingStatusDialog statusDialog =
            new ProcessingStatusDialog((Frame) getRootPane().getTopLevelAncestor());

        exportWorker = new SwingWorker<Void, Void>() {

            public Void doInBackground() {

                int imageNumber = 1;

                EntityManager entityManager =
                    Main.getInstance().getJPAConfiguration().getEntityManager();

                if (entityManager == null) {
                    return null;
                }

                for (long id : selectedUnidentifiedImageIds) {

                    statusDialog.setMessage(String
                        .format(Localizer.localize("UI", "ExportUnidentifiedImagesStatusMessage"),
                            imageNumber, selectedUnidentifiedImageIds.length));

                    IncomingImage incomingImage = entityManager.find(IncomingImage.class, id);

                    String originalName = incomingImage.getIncomingImageName();

                    File outputFile =
                        new File(chooser.getSelectedFile() + File.separator + originalName);

                    if (!(Misc.checkOverwriteFile(outputFile, Main.getInstance()))) {
                        continue;
                    }

                    byte[] pngData = incomingImage.getIncomingImageData();

                    try {
                        FileUtils.writeByteArrayToFile(outputFile, pngData);
                    } catch (IOException e) {
                        Misc.printStackTrace(e);
                    }

                    imageNumber++;

                }

                return null;

            }

            public void done() {
                try {
                    get();
                } catch (InterruptedException e) {
                    Misc.printStackTrace(e);
                } catch (ExecutionException e) {
                    Misc.printStackTrace(e);
                } finally {
                    if (exportWorker != null) {
                        exportWorker = null;
                    }
                    if (statusDialog != null && statusDialog.isVisible()) {
                        statusDialog.dispose();
                    }
                }
            }

        };
        exportWorker.execute();
        statusDialog.setModal(true);
        if (exportWorker != null) {
            statusDialog.setVisible(true);
        }
    }

}
