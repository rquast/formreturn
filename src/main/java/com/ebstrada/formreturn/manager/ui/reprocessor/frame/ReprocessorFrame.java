package com.ebstrada.formreturn.manager.ui.reprocessor.frame;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;

import com.ebstrada.aggregation.exception.ErrorFlagException;
import com.ebstrada.aggregation.exception.InvalidRulePartException;
import com.ebstrada.aggregation.exception.NoMatchException;
import com.ebstrada.formreturn.manager.gef.base.Editor;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.base.Layer;
import com.ebstrada.formreturn.manager.gef.base.LayerDiagram;
import com.ebstrada.formreturn.manager.gef.base.LayerGrid;
import com.ebstrada.formreturn.manager.gef.base.LayerManager;
import com.ebstrada.formreturn.manager.gef.base.ZoomAction;
import com.ebstrada.formreturn.manager.gef.event.GraphSelectionEvent;
import com.ebstrada.formreturn.manager.gef.event.GraphSelectionListener;
import com.ebstrada.formreturn.manager.gef.graph.GraphModel;
import com.ebstrada.formreturn.manager.gef.graph.presentation.*;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigImage;
import com.ebstrada.formreturn.manager.gef.presentation.FigSegmentArea;
import com.ebstrada.formreturn.manager.gef.ui.*;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.aggregation.AggregateCalculator;
import com.ebstrada.formreturn.manager.logic.recognition.reader.BarcodeImageReader;
import com.ebstrada.formreturn.manager.logic.recognition.reader.FormReader;
import com.ebstrada.formreturn.manager.logic.recognition.reader.FormReaderException;
import com.ebstrada.formreturn.manager.logic.recognition.structure.BarcodeRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.CheckBoxRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.FormRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.FragmentRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.OMRMatrix;
import com.ebstrada.formreturn.manager.logic.recognition.structure.OMRRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.SegmentRecognitionStructure;
import com.ebstrada.formreturn.manager.persistence.jpa.CheckBox;
import com.ebstrada.formreturn.manager.persistence.jpa.DataSet;
import com.ebstrada.formreturn.manager.persistence.jpa.Form;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentBarcode;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentOmr;
import com.ebstrada.formreturn.manager.persistence.jpa.IncomingImage;
import com.ebstrada.formreturn.manager.persistence.jpa.ProcessedImage;
import com.ebstrada.formreturn.manager.persistence.jpa.Publication;
import com.ebstrada.formreturn.manager.persistence.jpa.Record;
import com.ebstrada.formreturn.manager.persistence.jpa.Segment;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceField;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceText;
import com.ebstrada.formreturn.manager.persistence.xstream.Document;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.ui.dialog.LoadingDialog;
import com.ebstrada.formreturn.manager.ui.dialog.MessageDialog;
import com.ebstrada.formreturn.manager.ui.dialog.MultiplePagesDialog;
import com.ebstrada.formreturn.manager.ui.filefilter.FilenameExtensionFilter;
import com.ebstrada.formreturn.manager.ui.panel.PropertiesPanelController;
import com.ebstrada.formreturn.manager.ui.reprocessor.collection.RecognitionStructureComparator;
import com.ebstrada.formreturn.manager.ui.reprocessor.collection.RecognitionStructureMap;
import com.ebstrada.formreturn.manager.ui.reprocessor.component.*;
import com.ebstrada.formreturn.manager.ui.reprocessor.dialog.AdjustImageDialog;
import com.ebstrada.formreturn.manager.ui.reprocessor.dialog.FormPageSelectionDialog;
import com.ebstrada.formreturn.manager.ui.reprocessor.dialog.SegmentStencilEditorDialog;
import com.ebstrada.formreturn.manager.ui.reprocessor.panel.DetectionPropertiesPanel;
import com.ebstrada.formreturn.manager.ui.reprocessor.panel.ImagePropertiesPanel;
import com.ebstrada.formreturn.manager.ui.reprocessor.panel.TemplatePropertiesPanel;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.image.ImageUtil;
import com.ebstrada.formreturn.manager.util.preferences.persistence.PublicationPreferences;

public class ReprocessorFrame extends JPanel {

    private static final long serialVersionUID = 1L;

    private boolean finishedLoading = false;

    private BufferedImage sourceImage;

    private long incomingImageId;

    private TemplatePropertiesPanel templatePropertiesPanel;

    private ImagePropertiesPanel imagePropertiesPanel;

    private DetectionPropertiesPanel detectionPropertiesPanel;

    private long formPageId;

    private int editorType;

    private ZoomAction zoomAction;

    private float realZoom = 1.0f;

    private PageAttributes pageAttributes;

    private FigImage backgroundImage;

    public static final int FORM_PAGE = 0;
    public static final int UNIDENTIFIED_IMAGE = 1;

    public String incomingImageName;

    private SwingWorker<File, Void> browseImageFileWorker;

    private int fragmentPadding;

    private int markThreshold;

    private short luminanceThreshold;

    private ZoomSettings zoomSettings;

    public ReprocessorFrame(byte[] imageData, long incomingImageId, String incomingImageName,
        final ZoomSettings zoomSettings) throws Exception {
        sourceImage = ImageUtil.blurImage(ImageUtil.readImage(imageData, 1));
        sourceImage.flush();

        this.incomingImageId = incomingImageId;
        this.incomingImageName = incomingImageName;
        this.zoomSettings = zoomSettings;
        this.editorType = UNIDENTIFIED_IMAGE;
        initComponents();
        initEditor();
        zoomToFitCheckBox.setSelected(zoomSettings.isZoomToFit());

        graphScrollPane.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                if (zoomSettings.isZoomToFit()) {
                    zoomToFit();
                } else {
                    editorZoomComboBox.getModel().setSelectedItem(zoomSettings.getZoomLevel());
                }
            }
        });
    }

    public ReprocessorFrame(byte[] imageData, long formPageId, final ZoomSettings zoomSettings)
        throws Exception {
        sourceImage = ImageUtil.blurImage(ImageUtil.readImage(imageData, 1));
        sourceImage.flush();

        this.formPageId = formPageId;
        this.editorType = FORM_PAGE;
        this.zoomSettings = zoomSettings;
        initComponents();
        initEditor();
        zoomToFitCheckBox.setSelected(zoomSettings.isZoomToFit());
        graphScrollPane.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                if (zoomSettings.isZoomToFit()) {
                    zoomToFit();
                } else {
                    editorZoomComboBox.getModel().setSelectedItem(zoomSettings.getZoomLevel());
                }
            }
        });
    }

    private void zoomToFit() {

        if (graphScrollPane.getWidth() > 0) {

            double width = graphScrollPane.getWidth();
            double height = graphScrollPane.getHeight();

            double widthRatio = width / (double) sourceImage.getWidth();
            double heightRatio = height / (double) sourceImage.getHeight();

            if (width > height) {
                this.zoomSettings.setZoomLevel(((int) Math.floor(widthRatio * 100.0d)) + "%");
            } else {
                this.zoomSettings.setZoomLevel(((int) Math.floor(heightRatio * 100.0d)) + "%");
            }

            editorZoomComboBox.getModel().setSelectedItem(zoomSettings.getZoomLevel());

        }

    }

    public void updateGraphSize() {
        updateGraphSize(_graph.getDocument(), _graph.getDocumentAttributes());
    }

    public void updateGraphSize(Document document, DocumentAttributes documentAttributes) {
        pageAttributes = new PageAttributes();
        pageAttributes.setCroppedWidth(sourceImage.getWidth());
        pageAttributes.setCroppedHeight(sourceImage.getHeight());
        pageAttributes.setBackgroundImage(sourceImage);
        _graph.setDocument(document);
        _graph.createPage(document, pageAttributes);
        _graph.setDocumentAttributes(documentAttributes);
        _graph.setPageAttributes(pageAttributes);
        _graph.updateGraphBoundaries();
    }

    public void initEditor() {

        splitPane.setDividerLocation(Main.getInstance().getDesktopTabbedPane().getWidth() - 250);

        Document document = new Document();
        DocumentAttributes documentAttributes = new DocumentAttributes();
        documentAttributes.setDocumentType(DocumentAttributes.REPROCESSOR);

        updateGraphSize(document, documentAttributes);

        GraphModel graphModel = null;

        if (_graph.getGraphModel() == null) {
            graphModel = new DefaultGraphModel();
            _graph.setGraphModel(graphModel);
        }

        GraphSelectionListener gsl = new GraphSelectionListener() {

            public void selectionChanged(GraphSelectionEvent gse) {
                getPropertiesPanelController().destroyPanels();
                Vector sels = gse.getSelections();
                updatePropertyBox(sels);
                unpressAllButtons();
            }

        };

        _graph.addGraphSelectionListener(gsl);

        Editor ce = _graph.getEditor();
        Layer oldGrid = ce.getLayerManager().findLayerNamed("Grid");
        Layer newGrid = new LayerGrid(Color.white, Color.white, 8, true);
        ce.getLayerManager().replaceLayer(oldGrid, newGrid);
        ce.setAntiAlias(true);

        Vector sels = _graph.selectedFigs();
        updatePropertyBox(sels);
        unpressAllButtons();

        graphScrollPane.getVerticalScrollBar().setUnitIncrement(30);
        graphScrollPane.getVerticalScrollBar().setBlockIncrement(90);
        graphScrollPane.getHorizontalScrollBar().setUnitIncrement(30);
        graphScrollPane.getHorizontalScrollBar().setBlockIncrement(90);

        ce.setReprocessorFrame(this);

    }

    public void restoreCapturedDataTable() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                capturedDataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                capturedDataTable.setModel(getFormPageCapturedDataModel());
                capturedDataTable.getTableHeader().setReorderingAllowed(false);
            }
        });
    }

    public void selectNewImageButtonActionPerformed(ActionEvent e) {

        if (browseImageFileWorker != null && !(browseImageFileWorker.isDone())) {
            return;
        }

        browseImageFileWorker = new SwingWorker<File, Void>() {
            protected File doInBackground() throws InterruptedException {
                File file = null;
                FilenameExtensionFilter filter = new FilenameExtensionFilter();
                filter.addExtension("png");
                filter.addExtension("jpg");
                filter.addExtension("jpeg");
                filter.addExtension("gif");
                filter.addExtension("tif");
                filter.addExtension("tiff");
                filter.addExtension("pdf");

                FileDialog fd = new FileDialog(Main.getInstance(),
                    Localizer.localize("UI", "ReprocessorFrameSelectNewImageFileDialogTitle"),
                    FileDialog.LOAD);
                fd.setModal(true);
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
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                    return null;
                }

                fd.setModal(true);
                fd.setVisible(true);
                if (fd.getFile() != null) {
                    String filename = fd.getFile();
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

            protected void done() {

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {

                        File file = null;
                        try {
                            file = get();
                        } catch (InterruptedException e) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                        } catch (ExecutionException e) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                        }

                        if (file != null) {
                            try {
                                Main.getInstance().blockInput();
                                loadImage(file);
                            } catch (Exception ex) {
                                Misc.printStackTrace(ex);
                            } finally {
                                Main.getInstance().unblockInput();
                                setFinishedLoading(true);
                            }
                        }

                    }
                });

            }

        };
        setFinishedLoading(false);
        browseImageFileWorker.execute();

    }

    private void loadImage(File imageFile) {

        try {

            int pageCount = ImageUtil.getNumberOfPagesInTiff(imageFile);
            int selectedPageNumber = 1;

            if (pageCount > 1) {
                MultiplePagesDialog mpd = new MultiplePagesDialog(Main.getInstance(), pageCount);
                mpd.setModal(true);
                mpd.setVisible(true);

                selectedPageNumber = mpd.getSelectedPageNumber();
            }

            sourceImage = ImageUtil.readImage(imageFile, selectedPageNumber);
            sourceImage.flush();
            this.backgroundImage = null;

            restore();

        } catch (Exception ex) {

            final String message = String
                .format(Localizer.localize("UI", "UnableToReadFileMessage"), imageFile.toString());

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String caption = Localizer.localize("UI", "ErrorTitle");
                    javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
                        javax.swing.JOptionPane.DEFAULT_OPTION,
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            });

            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);

        }

        setFinishedLoading(true);

    }

    public void saveToDiskButtonActionPerformed(ActionEvent e) {
        File imageFile = null;

        try {
            imageFile = getSaveLocation();
            OutputStream out = new FileOutputStream(imageFile);
            out.write(ImageUtil.getPNGByteArray(sourceImage));
            out.flush();
            out.close();
            Misc.showSuccessMsg(Main.getInstance(),
                Localizer.localize("UI", "SuccessfullySavedImageFileMessage"));
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            Misc.showErrorMsg(Main.getInstance(),
                Localizer.localize("UI", "UnableToSaveImageFileErrorMessage"));
        }
    }

    private File getSaveLocation() throws Exception {

        File file = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();
        filter.addExtension("png");

        FileDialog fd = new FileDialog(Main.getInstance(),
            Localizer.localize("UI", "SaveImageToDiskDialogTitle"), FileDialog.SAVE);
        fd.setFilenameFilter(filter);
        if (incomingImageName != null) {
            fd.setFile(incomingImageName + ".png");
        } else {
            fd.setFile(Localizer.localize("UI", "ImageFilePrefix") + ".png");
        }

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

        fd.setModal(true);
        fd.setVisible(true);
        if (fd.getFile() != null) {
            String filename = fd.getFile();
            file = new File(fd.getDirectory() + filename);
            if (file.isDirectory()) {
                throw new Exception(
                    Localizer.localize("UI", "CannotSaveToDirectoryExceptionMessage"));
            }
        } else {
            throw new Exception(Localizer.localize("UI", "FilePointerIsNullExceptionMessage"));
        }

        return file;

    }

    public void rotateImageButtonActionPerformed(ActionEvent e) {
        if (adjustImage()) {
            try {
                restore();
            } catch (Exception ex) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            }
        }
    }

    public DefaultTableModel getBarcodeRecognitionStructureTableModel(
        BarcodeRecognitionStructure bcrs) {

        DefaultTableModel dtm = new DefaultTableModel() {

            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked") @Override public Class getColumnClass(int column) {
                return super.getValueAt(0, column).getClass();
            }

            @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
                if (super.getValueAt(rowIndex, columnIndex) instanceof CapturedDataItem) {
                    return true;
                } else {
                    return false;
                }
            }

            public void setValueAt(Object value, int row, int col) {
                super.setValueAt(value, row, col);
                fireTableCellUpdated(row, col);
            }

        };

        dtm.addColumn(Localizer.localize("UI", "ReprocessorFrameFieldNameColumnTitle"));
        dtm.addColumn(Localizer.localize("UI", "ReprocessorFrameBarcodeValueColumnTitle"));

        CapturedDataItem data = new CapturedDataItem();
        String capturedData = bcrs.getBarcodeValue();
        data.setType(CapturedDataItem.BARCODE_FIELD);
        data.setValue(capturedData);

        dtm.addRow(new Object[] {bcrs.getFieldName(), data});

        return dtm;

    }

    public DefaultTableModel getOMRRecognitionStructureTableModel(OMRRecognitionStructure omrrs) {

        DefaultTableModel dtm = new DefaultTableModel() {

            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked") @Override public Class getColumnClass(int column) {
                return super.getValueAt(0, column).getClass();
            }

            @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
                return true;
            }

            public void setValueAt(Object value, int row, int col) {
                super.setValueAt(value, row, col);
                fireTableCellUpdated(row, col);
            }

        };

        String[][] capturedData = omrrs.getCapturedData();
        for (int i = 0; i < capturedData[0].length; i++) {
            dtm.addColumn((i + 1) + "");
        }

        CapturedDataItem[][] tableData =
            new CapturedDataItem[omrrs.getNumberOfRows()][omrrs.getNumberOfColumns()];
        for (CheckBoxRecognitionStructure cbrs : omrrs.getCheckBoxRecognitionStructures()) {
            CapturedDataItem data = new CapturedDataItem();
            data.setType(CapturedDataItem.OMR_FIELD);
            data.setValue(cbrs.getCheckBoxValue());
            data.setMarked(cbrs.isCheckBoxMarked());
            data.setRow(cbrs.getRow());
            data.setColumn(cbrs.getColumn());
            tableData[cbrs.getRow()][cbrs.getColumn()] = data;
        }

        for (CapturedDataItem[] tableRow : tableData) {
            dtm.addRow(tableRow);
        }

        return dtm;

    }

    public DefaultTableModel getSegmentCapturedDataModel(RecognitionStructureMap fields) {
        DefaultTableModel dtm = new DefaultTableModel() {

            private static final long serialVersionUID = 1L;

            Class[] columnTypes = new Class[] {String.class, Object.class, String.class};
            boolean[] columnEditable = new boolean[] {false, false, false};

            @Override public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnEditable[columnIndex];
            }
        };
        dtm.addColumn(Localizer.localize("UI", "ReprocessorFrameFieldNameColumnTitle"));
        dtm.addColumn(Localizer.localize("UI", "ReprocessorFrameValueColumnTitle"));
        dtm.addColumn(Localizer.localize("UI", "ErrorTitle"));

        for (RecognitionStructureComparator rsc : fields.getSortedData()) {
            FragmentRecognitionStructure frs = rsc.getData();
            if (frs instanceof OMRRecognitionStructure) {
                OMRRecognitionStructure omrrs = (OMRRecognitionStructure) frs;
                dtm.addRow(new Object[] {omrrs.getFieldName(), omrrs,
                    omrrs.isInvalidated() ? Localizer.localize("UI", "ErrorTitle") : ""});
            } else if (frs instanceof BarcodeRecognitionStructure) {
                BarcodeRecognitionStructure bcrs = (BarcodeRecognitionStructure) frs;
                dtm.addRow(new Object[] {bcrs.getFieldName(), bcrs,
                    bcrs.isInvalidated() ? Localizer.localize("UI", "ErrorTitle") : ""});
            }
        }

        return dtm;
    }


    public DefaultTableModel getSegmentCapturedDataModel(long segmentId) {

        DefaultTableModel dtm = new DefaultTableModel();

        if (segmentId <= 0) {
            return dtm;
        }

        RecognitionStructureMap orderedFields = new RecognitionStructureMap();

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return dtm;
        }

        try {
            Segment segment = entityManager.find(Segment.class, segmentId);
            if (segment == null) {
                return dtm;
            }

            HashMap<String, OMRRecognitionStructure> currentOMRData =
                new HashMap<String, OMRRecognitionStructure>();
            HashMap<String, BarcodeRecognitionStructure> currentBarcodeData =
                new HashMap<String, BarcodeRecognitionStructure>();

            for (int i = 0; i < this.capturedDataTable.getRowCount(); i++) {
                Object obj = this.capturedDataTable.getValueAt(i, 1);
                if (obj instanceof OMRRecognitionStructure) {
                    currentOMRData.put((String) this.capturedDataTable.getValueAt(i, 0),
                        (OMRRecognitionStructure) obj);
                } else if (obj instanceof BarcodeRecognitionStructure) {
                    currentBarcodeData.put((String) this.capturedDataTable.getValueAt(i, 0),
                        (BarcodeRecognitionStructure) obj);
                }
            }

            if (segment.getFragmentOmrCollection() != null) {
                for (FragmentOmr fragmentOmr : segment.getFragmentOmrCollection()) {

                    OMRRecognitionStructure newOmrrs = new OMRRecognitionStructure(fragmentOmr);

                    OMRRecognitionStructure oldOmrrs =
                        currentOMRData.get(fragmentOmr.getCapturedDataFieldName());

                    // set the data in omrrs
                    for (CheckBoxRecognitionStructure oldCbrs : oldOmrrs
                        .getCheckBoxRecognitionStructures()) {
                        for (CheckBoxRecognitionStructure newCbrs : newOmrrs
                            .getCheckBoxRecognitionStructures()) {
                            if (oldCbrs.getRow() == newCbrs.getRow()
                                && oldCbrs.getColumn() == newCbrs.getColumn()) {
                                newCbrs.setCheckBoxMarked(oldCbrs.isCheckBoxMarked());
                            }
                        }
                    }

                    newOmrrs.setInvalidated(oldOmrrs.isInvalidated());

                    orderedFields.addStructure(newOmrrs);
                }
            }
            if (segment.getFragmentBarcodeCollection() != null) {
                for (FragmentBarcode fragmentBarcode : segment.getFragmentBarcodeCollection()) {

                    BarcodeRecognitionStructure newBcrs =
                        new BarcodeRecognitionStructure(fragmentBarcode);

                    BarcodeRecognitionStructure oldBcrs =
                        currentBarcodeData.get(fragmentBarcode.getCapturedDataFieldName());

                    newBcrs.setBarcodeValue(oldBcrs.getBarcodeValue());
                    newBcrs.setInvalidated(oldBcrs.isInvalidated());
                    newBcrs.setPercentX1(oldBcrs.getPercentX1());
                    newBcrs.setPercentX2(oldBcrs.getPercentX2());
                    newBcrs.setPercentY1(oldBcrs.getPercentY1());
                    newBcrs.setPercentY2(oldBcrs.getPercentY2());

                    newBcrs.setBarcodeType(oldBcrs.getBarcodeType());
                    newBcrs.setFieldName(oldBcrs.getFieldName());

                    newBcrs.setReconciliationKey(oldBcrs.isReconciliationKey());

                    orderedFields.addStructure(newBcrs);
                }
            }

        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return dtm;
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return getSegmentCapturedDataModel(orderedFields);

    }

    public DefaultTableModel getFormPageCapturedDataModel() {

        DefaultTableModel dtm = new DefaultTableModel() {

            private static final long serialVersionUID = 1L;

            Class[] columnTypes = new Class[] {String.class, Object.class, String.class};
            boolean[] columnEditable = new boolean[] {false, false, false};

            @Override public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnEditable[columnIndex];
            }
        };
        dtm.addColumn(Localizer.localize("UI", "ReprocessorFrameFieldNameColumnTitle"));
        dtm.addColumn(Localizer.localize("UI", "ReprocessorFrameValueColumnTitle"));
        dtm.addColumn(Localizer.localize("UI", "ErrorTitle"));

        if (formPageId <= 0) {
            return dtm;
        }

        RecognitionStructureMap orderedFields = new RecognitionStructureMap();

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return dtm;
        }

        try {
            FormPage formPage = entityManager.find(FormPage.class, formPageId);
            if (formPage == null) {
                return dtm;
            }

            if (formPage.getSegmentCollection() != null) {
                for (Segment segment : formPage.getSegmentCollection()) {
                    if (segment.getFragmentOmrCollection() != null) {
                        for (FragmentOmr fragmentOmr : segment.getFragmentOmrCollection()) {
                            orderedFields.addStructure(new OMRRecognitionStructure(fragmentOmr));
                        }
                    }
                    if (segment.getFragmentBarcodeCollection() != null) {
                        for (FragmentBarcode fragmentBarcode : segment
                            .getFragmentBarcodeCollection()) {
                            orderedFields
                                .addStructure(new BarcodeRecognitionStructure(fragmentBarcode));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return dtm;
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

        for (RecognitionStructureComparator rsc : orderedFields.getSortedData()) {
            FragmentRecognitionStructure frs = rsc.getData();
            if (frs instanceof OMRRecognitionStructure) {
                OMRRecognitionStructure omrrs = (OMRRecognitionStructure) frs;
                dtm.addRow(new Object[] {omrrs.getFieldName(), omrrs,
                    omrrs.isInvalidated() ? Localizer.localize("UI", "ErrorTitle") : ""});
            } else if (frs instanceof BarcodeRecognitionStructure) {
                BarcodeRecognitionStructure bcrs = (BarcodeRecognitionStructure) frs;
                dtm.addRow(new Object[] {bcrs.getFieldName(), bcrs,
                    bcrs.isInvalidated() ? Localizer.localize("UI", "ErrorTitle") : ""});
            }

        }

        return dtm;

    }

    public void removeAllFigs() {
        Enumeration<Fig> figs = _graph.getEditor().figs();
        while (figs.hasMoreElements()) {
            _graph.getEditor().removePropertyChangeListener(figs.nextElement());
        }
        _graph.getEditor().getSelectionManager().deselectAll();
        _graph.getEditor().getLayerManager().removeAll();
        _graph.getEditor().damageAll();
    }

    public void restore() throws Exception {

        removeAllFigs();

        if (editorType == FORM_PAGE) {
            String title = String
                .format(Localizer.localize("UI", "ReprocessorFrameFormPageIDTitle"),
                    formPageId + "");
            setTitle(title);
        } else if (editorType == UNIDENTIFIED_IMAGE) {
            String title = String
                .format(Localizer.localize("UI", "ReprocessorFrameUnidentifiedImageIDTitle"),
                    incomingImageId + "");
            setTitle(title);
        }

        addBackgroundImageToGraph(getBackgroundImage());
        updateGraphSize();
        reprocessForm();

    }

    public void reprocessForm() throws Exception {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager != null) {

            FormRecognitionStructure frs = new FormRecognitionStructure();

            try {
                FormReader formReader =
                    new FormReader(sourceImage, frs, entityManager, incomingImageName);
                BufferedImage image;
                if (formPageId > 0) {
                    image = formReader.registerForm(sourceImage, formPageId);
                } else {
                    image = formReader.registerForm(sourceImage);
                }
                sourceImage = ImageUtil.getCompatibleBufferedImage(image);
                this.backgroundImage = null;
                addBackgroundImageToGraph(getBackgroundImage());
                updateGraphSize();
                formPageId = formReader.getFormPage().getFormPageId();

                restoreSegments(formReader);

            } catch (final FormReaderException fre) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Misc.showErrorMsg(Main.getInstance(), fre.getErrorTitle());
                        switch (fre.getError()) {
                            case FormReaderException.FORM_ID_NOT_FOUND:
                            case FormReaderException.FORM_PAGE_RECORD_MISSING:
                            case FormReaderException.MISSING_FORM_ID_BARCODE:

                                if (selectFormPage()) {
                                    final LoadingDialog ld = new LoadingDialog(Main.getInstance());
                                    ld.setVisible(true);

                                    Main.getInstance().blockInput();

                                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                                        protected Void doInBackground()
                                            throws InterruptedException {
                                            try {
                                                restore();
                                            } catch (Exception ex) {
                                                com.ebstrada.formreturn.manager.util.Misc
                                                    .printStackTrace(ex);
                                            }
                                            return null;
                                        }

                                        protected void done() {
                                            Main.getInstance().unblockInput();
                                            ld.dispose();
                                            try {
                                                get();
                                            } catch (InterruptedException e) {
                                            } catch (ExecutionException e) {
                                            } catch (Exception ex) {
                                                com.ebstrada.formreturn.manager.util.Misc
                                                    .printStackTrace(ex);
                                            }
                                        }
                                    };
                                    worker.execute();
                                }

                                break;
                            default:
                        }
                    }
                });
            } catch (final Exception e) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Misc.showErrorMsg(Main.getInstance(), e.getMessage());
                    }
                });
                throw e;
            } finally {
                if (entityManager.isOpen()) {
                    entityManager.close();
                    entityManager = null;
                }
                if (formPageId > 0) {
                    restoreCapturedDataTable();
                }
            }

        }
    }

    private void restoreSegments(final FormReader formReader) throws FormReaderException {

        setLuminanceThreshold(formReader.getLuminanceThreshold());
        setMarkThreshold((int) formReader.getMarkThreshold());
        setFragmentPadding(formReader.getFragmentPadding());

        final ArrayList<FigSegmentArea> figSegmentAreas = formReader.reprocessForm();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (FigSegmentArea figSegmentArea : figSegmentAreas) {
                    figSegmentArea.setLuminanceThreshold(getLuminanceThreshold());
                    figSegmentArea.setMarkThreshold(getMarkThreshold());
                    figSegmentArea.setFragmentPadding(getFragmentPadding());
                    _graph.getEditor().add(figSegmentArea);
                }
                _graph.getEditor().damageAll();
            }
        });

    }

    private void setFragmentPadding(int fragmentPadding) {
        this.fragmentPadding = fragmentPadding;
    }

    private void setMarkThreshold(int markThreshold) {
        this.markThreshold = markThreshold;
    }

    private void setLuminanceThreshold(short luminanceThreshold) {
        this.luminanceThreshold = luminanceThreshold;
    }

    public int getFragmentPadding() {
        return fragmentPadding;
    }

    public int getMarkThreshold() {
        return markThreshold;
    }

    public short getLuminanceThreshold() {
        return luminanceThreshold;
    }

    public FigImage getBackgroundImage() {

        if (backgroundImage == null) {
            backgroundImage =
                new FigImage(0, 0, sourceImage.getWidth(), sourceImage.getHeight(), sourceImage);
            backgroundImage.damage();
        }

        backgroundImage.setLocked(true);
        backgroundImage.setMovable(false);
        backgroundImage.setResizable(false);

        return backgroundImage;
    }

    public void addBackgroundImageToGraph(FigImage backgroundImage) {

        LayerManager lm = _graph.getEditor().getLayerManager();

        Layer backgroundLayer = lm.findLayerNamed("backgroundImage");
        if (backgroundLayer != null) {
            backgroundLayer.removeAll();
            lm.removeLayer(backgroundLayer);
        }

        Layer activeLayer = lm.getActiveLayer();
        lm.removeLayer(activeLayer);
        LayerDiagram ld = new LayerDiagram("backgroundImage");
        ld.add(backgroundImage);
        lm.addLayer(ld, false);
        lm.addLayer(activeLayer, true);
        _graph.getEditor().damageAll();

    }

    public void setTitle(String title) {
        this.setName(title);
    }

    public String getTitle() {
        return this.getName();
    }

    public void unpressAllButtons() {
        if (reprocessPalette != null) {
            reprocessPalette.unpressAllButtons();
        }
    }

    public void updatePropertyBox(Vector sels) {

        if (!isFinishedLoading()) {
            return;
        }

        Iterator i = sels.iterator();

        // only change properties panel on single selections
        if (sels.size() == 1) {

            Object o = i.next();

            if (o instanceof Fig) {

                // clear the properties panel
                getPropertiesPanelController().destroyPanels();

                Fig selectedFig = (Fig) o;

                getPropertiesPanelController().initFig(selectedFig);

                if (formPageId > 0) {
                    if (detectionPropertiesPanel == null) {
                        detectionPropertiesPanel = new DetectionPropertiesPanel(this);
                    }
                    getPropertiesPanelController().createPanel(detectionPropertiesPanel);
                }

            }

        } else {

            updateProperties();

        }

    }

    public void updateProperties() {
        if (getPropertiesPanelController() != null) {
            getPropertiesPanelController().destroyPanels();

            if (editorType == UNIDENTIFIED_IMAGE) {
                if (templatePropertiesPanel == null) {
                    templatePropertiesPanel = new TemplatePropertiesPanel(this);
                }
                getPropertiesPanelController().createPanel(templatePropertiesPanel);
            }

            if (imagePropertiesPanel == null) {
                imagePropertiesPanel = new ImagePropertiesPanel(this);
            }
            getPropertiesPanelController().createPanel(imagePropertiesPanel);

            if (detectionPropertiesPanel == null) {
                detectionPropertiesPanel = new DetectionPropertiesPanel(this);
            }
            getPropertiesPanelController().createPanel(detectionPropertiesPanel);

        }
    }

    public boolean closeReprocessorFrame() {


        Editor ce = _graph.getEditor();

        if (ce.hasEditorStateChanged() != false) {

            Main.getInstance().activateReprocessorFrame(this);

            String message = Localizer.localize("UI", "ReprocessorFrameConfirmCloseMessage");
            String caption = Localizer.localize("UI", "ReprocessorFrameConfirmCloseTitle");

            int ret = javax.swing.JOptionPane
                .showConfirmDialog(Main.getInstance(), message, caption,
                    javax.swing.JOptionPane.YES_NO_OPTION);

            if (ret == javax.swing.JOptionPane.NO_OPTION) {
                return false;
            }

        }

        getPropertiesPanelController().destroyPanels();

        // do this or there will be a memory leak because of the property change listener on figs.
        removeAllFigs();
        Globals.curEditor(null);
        Globals.mode(null);
        System.gc();

        return true;
    }

    public PropertiesPanelController getPropertiesPanelController() {
        return Main.getInstance().getPropertiesPanelController();
    }

    private void editorZoomComboBoxActionPerformed(ActionEvent e) {
        String unparsedString = (String) editorZoomComboBox.getSelectedObjects()[0];
        String zoomString = "";
        for (int i = 0; i < unparsedString.length(); i++) {
            if (unparsedString.charAt(i) >= 48 && unparsedString.charAt(i) <= 57) {
                zoomString += unparsedString.charAt(i);
            }
        }
        float newZoom = Float.parseFloat(zoomString) / 100.0f;

        if (newZoom >= 0.1 && newZoom <= 10) {
            realZoom = newZoom;
            zoomAction = new ZoomAction(realZoom);
            zoomAction.actionPerformed(e);
        } else {
            editorZoomComboBox.setSelectedItem(((int) (realZoom * 100)) + "%");
        }

        zoomSettings.setZoomLevel(zoomString);

        repaint();
    }

    private void zoomInLabelMouseClicked(MouseEvent e) {

        String valueString = (String) editorZoomComboBox.getModel().getSelectedItem();

        int selectedIndex = editorZoomComboBox.getSelectedIndex();

        if (selectedIndex < 0) {

            for (int i = 0; i < editorZoomComboBox.getItemCount() - 1; i++) {

                String itemString = (String) editorZoomComboBox.getModel().getElementAt(i);

                float item = Float.parseFloat(itemString.replaceAll("%", ""));
                float value = Float.parseFloat(valueString.replaceAll("%", ""));

                if (item > value) {
                    editorZoomComboBox.setSelectedIndex(i);
                    break;
                }

            }

        } else {

            if (selectedIndex < (editorZoomComboBox.getItemCount() - 1)) {
                editorZoomComboBox.setSelectedIndex(selectedIndex + 1);
            }

        }

    }

    private void zoomOutLabelMouseClicked(MouseEvent e) {

        String valueString = (String) editorZoomComboBox.getModel().getSelectedItem();

        int selectedIndex = editorZoomComboBox.getSelectedIndex();

        if (selectedIndex < 0) {

            for (int i = editorZoomComboBox.getItemCount() - 1; i >= 0; i--) {

                String itemString = (String) editorZoomComboBox.getModel().getElementAt(i);

                float item = Float.parseFloat(itemString.replaceAll("%", ""));
                float value = Float.parseFloat(valueString.replaceAll("%", ""));

                if (item < value) {
                    editorZoomComboBox.setSelectedIndex(i);
                    break;
                }

            }

        } else {

            if (selectedIndex > 0) {
                editorZoomComboBox.setSelectedIndex(selectedIndex - 1);
            }

        }

    }


    public BufferedImage getFragmentImage(FigSegmentArea figSegmentArea, BufferedImage sourceImage2,
        BarcodeRecognitionStructure bcrs) throws FormReaderException {

        // binarize the image
        Rectangle2D segmentBoundary =
            new Rectangle2D.Double(figSegmentArea.getX(), figSegmentArea.getY(),
                figSegmentArea.getWidth(), figSegmentArea.getHeight());
        Rectangle2D fragmentBoundary =
            bcrs.getRecognitionArea(segmentBoundary, figSegmentArea.getFragmentPadding());

        return getFragmentImage(figSegmentArea, sourceImage, fragmentBoundary);
    }

    public BufferedImage getFragmentImage(FigSegmentArea figSegmentArea, BufferedImage sourceImage,
        OMRRecognitionStructure omrrs) throws FormReaderException {

        // binarize the image
        Rectangle2D segmentBoundary =
            new Rectangle2D.Double(figSegmentArea.getX(), figSegmentArea.getY(),
                figSegmentArea.getWidth(), figSegmentArea.getHeight());
        Rectangle2D fragmentBoundary =
            omrrs.getRecognitionArea(segmentBoundary, figSegmentArea.getFragmentPadding());
        return getFragmentImage(figSegmentArea, sourceImage, fragmentBoundary);
    }

    public BufferedImage getFragmentImage(FigSegmentArea figSegmentArea, BufferedImage sourceImage,
        Rectangle2D fragmentBoundary) throws FormReaderException {

        int x = (int) fragmentBoundary.getX();

        // check low point
        if (x < 0) {
            x = 0;
        }

        int y = (int) fragmentBoundary.getY();

        // check low point
        if (y < 0) {
            y = 0;
        }

        // check high point

        int width = (int) fragmentBoundary.getWidth();
        int height = (int) fragmentBoundary.getHeight();

        if (width == 0) {
            FormReaderException fre =
                new FormReaderException(FormReaderException.MISSING_SEGMENT_BARCODE,
                    Localizer.localize("UI", "FormReaderFragmentWidthZeroErrorMessage"));
            throw fre;
        }

        if (height == 0) {
            throw new FormReaderException(FormReaderException.MISSING_SEGMENT_BARCODE,
                Localizer.localize("UI", "FormReaderFragmentHeightZeroErrorMessage"));
        }

        int x2 = x + width;
        int y2 = y + height;

        if (x2 > sourceImage.getWidth()) {
            width = sourceImage.getWidth() - x;
        }

        if (y2 > sourceImage.getHeight()) {
            height = sourceImage.getHeight() - y;
        }

        BufferedImage subImage = null;

        try {
            subImage = ImageUtil.binarizeImage(sourceImage.getSubimage(x, y, width, height),
                figSegmentArea.getLuminanceThreshold(), false);
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        }

        return subImage;
    }

    private void detectAllData() throws FormReaderException {

        FormReaderException fre = null;

        List figs = _graph.getEditor().getLayerManager().getActiveLayer().getContents();
        for (Object obj : figs) {
            if (obj instanceof FigSegmentArea) {
                FigSegmentArea figSegmentArea = (FigSegmentArea) obj;
                SegmentRecognitionStructure srs = figSegmentArea.getSegmentRecognitionStructure();
                if (srs != null) {

                    RecognitionStructureMap fields = new RecognitionStructureMap();

                    for (OMRRecognitionStructure omrrs : srs.getOMRRecognitionStructures()
                        .values()) {
                        OMRMatrix omrMatrix = new OMRMatrix(omrrs,
                            getFragmentImage(figSegmentArea, getSourceImage(), omrrs),
                            figSegmentArea.getMarkThreshold());
                        try {
                            omrMatrix.process();
                            omrrs.setCapturedData(omrMatrix.getOmrBoxMatrix());
                            if (omrrs.isReconciliationKey() || omrrs.isCombineColumnCharacters()) {
                                String capturedString = omrrs
                                    .getCapturedString(omrrs.getCapturedData(),
                                        omrrs.getReadDirection());
                                Misc.aggregate(0.0d, new String[] {capturedString},
                                    omrrs.getAggregationRule());
                            } else {
                                Misc.aggregate(0.0d, omrrs.getCapturedData(),
                                    omrrs.getAggregationRule());
                            }
                            omrrs.setInvalidated(false);
                        } catch (FormReaderException fre1) {
                            fre = fre1;
                            omrrs.setInvalidated(true);
                        } catch (InvalidRulePartException e) {
                            fre = new FormReaderException(
                                FormReaderException.INVALID_AGGREGATION_RULE);
                            omrrs.setInvalidated(true);
                        } catch (NoMatchException e) {
                            omrrs.setInvalidated(false);
                        } catch (ErrorFlagException e) {
                            fre = new FormReaderException(FormReaderException.ERROR_CONDITION_MET);
                            omrrs.setInvalidated(true);
                        }
                        fields.addStructure(omrrs);
                    }

                    Map<String, BarcodeRecognitionStructure> BarcodeRecognitionStructures =
                        srs.getBarcodeRecognitionStructures();

                    if (BarcodeRecognitionStructures != null) {

                        for (BarcodeRecognitionStructure bcrs : BarcodeRecognitionStructures
                            .values()) {

                            try {

                                BufferedImage fragmentImage =
                                    getFragmentImage(figSegmentArea, getSourceImage(), bcrs);
                                BarcodeImageReader barcodeImageReader =
                                    new BarcodeImageReader(bcrs, fragmentImage);

                                bcrs.setBarcodeValue(barcodeImageReader.getBarcodeValue());
                                bcrs.setInvalidated(false);

                            } catch (FormReaderException fre1) {
                                fre = fre1;
                                bcrs.setInvalidated(true);
                            }

                            fields.addStructure(bcrs);

                        }

                    }

                    updateDataTable(fields);

                }
            }
        }

        if (fre != null) {
            throw fre;
        }

    }

    public void updateDataTable(RecognitionStructureMap fields) {

        HashMap<String, Object> capturedData = new HashMap<String, Object>();

        for (RecognitionStructureComparator rsc : fields.getSortedData()) {
            FragmentRecognitionStructure frs = rsc.getData();
            if (frs instanceof OMRRecognitionStructure) {
                OMRRecognitionStructure omrrs = (OMRRecognitionStructure) frs;
                capturedData.put(omrrs.getFieldName(), omrrs);
            } else if (frs instanceof BarcodeRecognitionStructure) {
                BarcodeRecognitionStructure bcrs = (BarcodeRecognitionStructure) frs;
                capturedData.put(bcrs.getFieldName(), bcrs);
            }
        }

        appendCapturedData(capturedData);

    }

    public void detectDataButtonActionPerformed(ActionEvent e) {
        try {
            detectAllData();
        } catch (Exception ex) {
            Misc.showExceptionMsg(Main.getInstance(), ex);
        }
    }

    public void revertButtonActionPerformed(ActionEvent e) {
        if (formPageId > 0) {
            restoreCapturedDataTable();
        }
    }

    public void saveCapturedDataButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                saveCapturedDataTable();
            }
        });
    }

    public void startTransaction(EntityManager entityManager) throws Exception {
        try {
            entityManager.getTransaction().begin();
            entityManager.flush();
        } catch (Exception ex) {
            abortTransaction(entityManager);
            throw ex;
        }
    }

    public void endTransaction(EntityManager entityManager) throws Exception {
        try {
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            abortTransaction(entityManager);
            throw ex;
        }
    }

    public void abortTransaction(EntityManager entityManager) {
        if (entityManager.getTransaction().isActive()) {
            try {
                entityManager.getTransaction().rollback();
            } catch (Exception rbex) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(rbex);
            }
        }
    }

    private void saveCapturedDataTable() {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        try {
            FormPage formPage = entityManager.find(FormPage.class, formPageId);
            if (formPage == null) {
                return;
            }

            startTransaction(entityManager);

            // check if it is a form id or template publication
            boolean isTemplateForm = false;

            if (formPage.getFormId().getRecordId() == null) {
                isTemplateForm = true;
            }

            // if it is a template publication, it needs to check if the form page id is the first one (the template)
            if (isTemplateForm) {

                // create a new record and save - then switch the formPage record to the newly created record
                formPage = processTemplateFormPage(entityManager, formPage);

            } // if it isn't just continue without creating a new record.

            double pageAggregate = 0.0d;
            double segmentAggregate = 0.0d;
            long formPageErrorAggregate = 0;

            if (formPage.getSegmentCollection() != null) {
                for (Segment segment : formPage.getSegmentCollection()) {

                    segmentAggregate = 0.0d;

                    if (segment.getFragmentOmrCollection() != null) {
                        for (FragmentOmr fragmentOmr : segment.getFragmentOmrCollection()) {
                            String fieldname = fragmentOmr.getCapturedDataFieldName();
                            for (int i = 0; i < capturedDataTable.getRowCount(); i++) {
                                if (fieldname.equals(capturedDataTable.getValueAt(i, 0))) {
                                    OMRRecognitionStructure omrrs =
                                        (OMRRecognitionStructure) capturedDataTable
                                            .getValueAt(i, 1);
                                    fragmentOmr.setErrorType((short) 0);
                                    fragmentOmr.setInvalidated((short) 0);
                                    omrrs.setInvalidated(false);
                                    try {
                                        omrrs.persistToFragmentOmr(entityManager, fragmentOmr);
                                        pageAggregate += fragmentOmr.getMark();
                                        segmentAggregate += fragmentOmr.getMark();
                                    } catch (InvalidRulePartException e) {
                                        fragmentOmr.setErrorType(
                                            (short) FormReaderException.INVALID_AGGREGATION_RULE);
                                        fragmentOmr.setInvalidated((short) 1);
                                        ++formPageErrorAggregate;
                                        entityManager.persist(fragmentOmr);
                                        entityManager.flush();
                                    } catch (ErrorFlagException e) {
                                        fragmentOmr.setErrorType(
                                            (short) FormReaderException.ERROR_CONDITION_MET);
                                        fragmentOmr.setInvalidated((short) 1);
                                        ++formPageErrorAggregate;
                                        entityManager.persist(fragmentOmr);
                                        entityManager.flush();
                                    }
                                }
                            }
                        }
                    }
                    if (segment.getFragmentBarcodeCollection() != null) {
                        for (FragmentBarcode fragmentBarcode : segment
                            .getFragmentBarcodeCollection()) {
                            String fieldname = fragmentBarcode.getCapturedDataFieldName();
                            for (int i = 0; i < capturedDataTable.getRowCount(); i++) {
                                if (fieldname.equals(capturedDataTable.getValueAt(i, 0))) {
                                    BarcodeRecognitionStructure bcrs =
                                        (BarcodeRecognitionStructure) capturedDataTable
                                            .getValueAt(i, 1);
                                    if (bcrs.getBarcodeValue() != null) {
                                        if (!(bcrs.getBarcodeValue()
                                            .equals(fragmentBarcode.getBarcodeValue()))) {
                                            fragmentBarcode.setBarcodeValue(bcrs.getBarcodeValue());
                                            entityManager.persist(fragmentBarcode);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    segment.setAggregateMark(segmentAggregate);
                    entityManager.persist(segment);

                }
            }

            AggregateCalculator.updateErrorCount(entityManager, formPage, formPageErrorAggregate);
            AggregateCalculator.updateFormScores(entityManager, formPage, pageAggregate);
            saveProcessedImage(entityManager, formPage);

            formPage.setProcessedTime(new Timestamp(System.currentTimeMillis()));
            entityManager.persist(formPage);
            entityManager.flush();

            endTransaction(entityManager);

            closeThisFrame();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String caption = Localizer.localize("Util", "SuccessTitle");
                    MessageDialog.showSuccessMessage(Main.getInstance(), caption,
                        Localizer.localize("UI", "ReprocessorFrameReprocessingSuccessfulMessage"));
                }
            });

        } catch (final Exception e) {
            abortTransaction(entityManager);
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Misc.showErrorMsg(Main.getInstance(), e.getMessage());
                }
            });
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
                entityManager = null;
            }
            closeThisFrame();
        }

    }

    private FormPage processTemplateFormPage(EntityManager entityManager, FormPage formPage)
        throws FormReaderException {

        // NOTE: THIS IS ALMOST THE SAME CODE AS IS IN THE FORM READER - TRY TO MERGE IT ONE DAY!!!
        // ALSO MOVE createNewRecord() and duplicateForm() back to the FormReader or some central area.

        short publicationType = formPage.getFormId().getPublicationId().getPublicationType();

        // 1. get a list of all reconciliation keys
        // get a map of fieldnames => values to match
        Map<String, String> matchMap = new HashMap<String, String>();
        for (int i = 0; i < capturedDataTable.getRowCount(); i++) {
            Object obj = capturedDataTable.getValueAt(i, 1);
            if (obj instanceof OMRRecognitionStructure) {
                OMRRecognitionStructure omrrs = (OMRRecognitionStructure) obj;
                if (omrrs.isReconciliationKey()) {
                    matchMap.put(omrrs.getFieldName(),
                        omrrs.getCapturedString(omrrs.getCapturedData(), omrrs.getReadDirection()));
                }
            } else if (obj instanceof BarcodeRecognitionStructure) {
                BarcodeRecognitionStructure bcrs = (BarcodeRecognitionStructure) obj;
                if (bcrs.isReconciliationKey()) {
                    matchMap.put(bcrs.getFieldName(), bcrs.toString());
                }
            }
        }

        // 2. somehow create the records etc the same way it is done in the form reader.
        ArrayList<String> nonMatchingFields = new ArrayList<String>();
        List<SourceField> matchingSourceFields = new ArrayList<SourceField>();
        List<SourceField> sfc =
            formPage.getFormId().getPublicationId().getDataSetId().getSourceFieldCollection();
        for (SourceField sourceField : sfc) {
            String sourceFieldName = sourceField.getSourceFieldName();
            if (matchMap.keySet().contains(sourceFieldName)) {
                matchingSourceFields.add(sourceField);
            } else {
                nonMatchingFields.add(sourceFieldName);
            }
        }

        List<SourceText> previousResultList = null;

        for (SourceField sourceField : matchingSourceFields) {

            String matchingValue = matchMap.get(sourceField.getSourceFieldName());

            // don't match any values that are null
            if (matchingValue.trim().length() <= 0) {
                continue;
            }

            Query sourceTextQuery =
                entityManager.createNamedQuery("SourceText.findBySourceFieldIdAndSourceTextString");
            sourceTextQuery.setParameter("sourceFieldId", sourceField);
            sourceTextQuery.setParameter("sourceTextString", matchingValue);

            // this is a list of sourceText's that have a matching value
            List<SourceText> resultList = sourceTextQuery.getResultList();

            if (previousResultList == null) {
                previousResultList = resultList;
            } else {
                List<SourceText> newResultList = new ArrayList<SourceText>();
                for (SourceText sourceText : resultList) {
                    if (previousResultList.contains(sourceText)) {
                        newResultList.add(sourceText);
                    }
                }
                previousResultList = newResultList;
            }

        }

        // the previousresultlist is a list of sourceText's that have the same record.. get the first record id
        Record matchingRecord = null;
        if (previousResultList != null && previousResultList.size() > 0) {
            matchingRecord = previousResultList.get(0).getRecordId();
        }

        // matching record was found
        if (matchingRecord != null) {

            // if this record has no form, duplicate
            Collection<Form> fc = matchingRecord.getFormCollection();
            Iterator<Form> fci = fc.iterator();
            long pageNumber = formPage.getFormPageNumber();

            // MAKE SURE PUBLICATION NUMBERS MATCH SO THERE ARE NO CROSSED PUBLICATIONS
            long publicationId = formPage.getFormId().getPublicationId().getPublicationId();

            Form newForm = null;
            if (fci != null) {
                while (fci.hasNext()) {
                    Form nextForm = fci.next();
                    if (nextForm.getPublicationId().getPublicationId() == publicationId) {
                        newForm = nextForm;
                        break;
                    }
                }
            }

            if (newForm != null) {
                long newFormId = newForm.getFormId();
                entityManager.getTransaction().commit();
                entityManager.clear();
                entityManager.getTransaction().begin();
                newForm = entityManager.find(Form.class, newFormId);
            } else {
                newForm = duplicateForm(entityManager, formPage.getFormId().getFormId(),
                    matchingRecord.getRecordId());
                long newFormId = newForm.getFormId();
                entityManager.getTransaction().commit();
                entityManager.clear();
                entityManager.getTransaction().begin();
                newForm = entityManager.find(Form.class, newFormId);
            }

            // get the page form the loaded form
            List<FormPage> fpc = newForm.getFormPageCollection();
            for (FormPage fp : fpc) {
                if (fp.getFormPageNumber() == pageNumber) {
                    formPage = fp;
                    break;
                }
            }

            // matching record wasn't found
        } else {

            if (publicationType
                == PublicationPreferences.RECONCILE_KEY_WITH_SOURCE_DATA_RECORD_CREATE_NEW) {

                // create new source data record
                Record record = createNewRecord(entityManager,
                    formPage.getFormId().getPublicationId().getDataSetId(), matchMap);

                long pageNumber = formPage.getFormPageNumber();

                // duplicate form
                Form newForm = duplicateForm(entityManager, formPage.getFormId().getFormId(),
                    record.getRecordId());
                long newFormId = newForm.getFormId();
                entityManager.getTransaction().commit();
                entityManager.clear();
                entityManager.getTransaction().begin();
                newForm = entityManager.find(Form.class, newFormId);

                // get the page form the loaded form

                List<FormPage> fpc = newForm.getFormPageCollection();
                for (FormPage fp : fpc) {
                    if (fp.getFormPageNumber() == pageNumber) {
                        formPage = fp;
                        break;
                    }
                }

            } else {

                String msg =
                    Localizer.localize("UI", "FormReaderUnableToIdentifyReconciliationKeyMessage");
                FormReaderException fre =
                    new FormReaderException(FormReaderException.RECONCILIATION_KEY_NOT_FOUND, msg);

                String nonMatchingFieldsStr = "";
                for (String nonMatchingField : nonMatchingFields) {
                    nonMatchingFieldsStr += nonMatchingField;
                    nonMatchingFieldsStr += " ";
                }
                fre.setReconciliationKeyNotFound(nonMatchingFieldsStr);
                throw fre;

            }

        }

        return formPage;
    }

    private Record createNewRecord(EntityManager entityManager, DataSet dataSetId,
        Map<String, String> matchMap) {
        Record record = new Record();
        record.setDataSetId(dataSetId);
        record.setRecordCreated(new Timestamp(System.currentTimeMillis()));
        record.setRecordModified(new Timestamp(System.currentTimeMillis()));
        entityManager.persist(record);
        entityManager.flush();

        Set<String> fieldnames = matchMap.keySet();

        // create empty field name records
        for (SourceField sf : dataSetId.getSourceFieldCollection()) {
            if (sf.getSourceFieldType().equals("STRING")) {
                SourceText st = new SourceText();
                st.setRecordId(record);
                st.setSourceFieldId(sf);

                // either set the reconciliation key data, or blank data
                if (fieldnames.contains(sf.getSourceFieldName())) {
                    String keydata = matchMap.get(sf.getSourceFieldName());
                    if (keydata == null) {
                        keydata = "";
                    }
                    st.setSourceTextString(keydata);
                } else {
                    st.setSourceTextString("");
                }
                entityManager.persist(st);
            }
        }
        entityManager.flush();

        return record;
    }

    public Form duplicateForm(EntityManager entityManager, long formId, long recordId) {

        Form originalForm = entityManager.find(Form.class, formId);

        Form newForm = new Form();
        newForm.setAggregateMark(originalForm.getAggregateMark());
        newForm.setFormPassword(originalForm.getFormPassword());
        Publication publication = entityManager
            .find(Publication.class, originalForm.getPublicationId().getPublicationId());
        newForm.setPublicationId(publication);
        Record record = entityManager.find(Record.class, recordId);
        newForm.setRecordId(record);

        entityManager.persist(newForm);
        entityManager.flush();

        List<FormPage> fpc = originalForm.getFormPageCollection();

        for (FormPage fp : fpc) {

            FormPage newFormPage = new FormPage();

            newFormPage.setCaptureTime(fp.getCaptureTime());
            newFormPage.setScannedPageNumber(fp.getScannedPageNumber());
            newFormPage.setAggregateMark(fp.getAggregateMark());
            newFormPage.setFormId(newForm);
            newFormPage.setFormPageNumber(fp.getFormPageNumber());

            entityManager.persist(newFormPage);
            entityManager.flush();

            List<Segment> sc = fp.getSegmentCollection();

            for (Segment segment : sc) {

                Segment newSegment = new Segment();

                newSegment.setBarcodeOne(segment.getBarcodeOne());
                newSegment.setBarcodeTwo(segment.getBarcodeTwo());
                newSegment.setFormPageId(newFormPage);

                entityManager.persist(newSegment);
                entityManager.flush();

                List<FragmentOmr> foc = segment.getFragmentOmrCollection();

                for (FragmentOmr fragmentOmr : foc) {

                    FragmentOmr newFragmentOmr = new FragmentOmr();

                    newFragmentOmr.setAggregationRule(fragmentOmr.getAggregationRule());
                    newFragmentOmr.setCapturedDataFieldName(fragmentOmr.getCapturedDataFieldName());
                    if (fragmentOmr.getCharacterData() != null) {
                        newFragmentOmr.setCharacterData(fragmentOmr.getCharacterData());
                    }
                    newFragmentOmr
                        .setCombineColumnCharacters(fragmentOmr.getCombineColumnCharacters());
                    newFragmentOmr.setMark(fragmentOmr.getMark());
                    newFragmentOmr.setReconciliationKey(fragmentOmr.getReconciliationKey());
                    newFragmentOmr.setSegmentId(newSegment);
                    newFragmentOmr.setX1Percent(fragmentOmr.getX1Percent());
                    newFragmentOmr.setX2Percent(fragmentOmr.getX2Percent());
                    newFragmentOmr.setY1Percent(fragmentOmr.getY1Percent());
                    newFragmentOmr.setY2Percent(fragmentOmr.getY2Percent());
                    newFragmentOmr.setMarkColumnName(fragmentOmr.getMarkColumnName());
                    newFragmentOmr.setOrderIndex(fragmentOmr.getOrderIndex());
                    newFragmentOmr.setMarkOrderIndex(fragmentOmr.getMarkOrderIndex());
                    newFragmentOmr.setReadDirection((short) fragmentOmr.getReadDirection());

                    entityManager.persist(newFragmentOmr);
                    entityManager.flush();

                    List<CheckBox> cbc = fragmentOmr.getCheckBoxCollection();
                    if (cbc.size() > 0) {
                        for (CheckBox cb : cbc) {

                            CheckBox newCb = new CheckBox();

                            newCb.setCheckBoxValue(cb.getCheckBoxValue());
                            newCb.setColumnNumber(cb.getColumnNumber());
                            newCb.setRowNumber(cb.getRowNumber());
                            newCb.setFragmentXRatio(cb.getFragmentXRatio());
                            newCb.setFragmentYRatio(cb.getFragmentYRatio());
                            newCb.setFragmentOmrId(newFragmentOmr);

                            entityManager.persist(newCb);
                            entityManager.flush();

                        }
                    }

                }

                List<FragmentBarcode> fbc = segment.getFragmentBarcodeCollection();

                for (FragmentBarcode fragmentBarcode : fbc) {

                    FragmentBarcode newFragmentBarcode = new FragmentBarcode();

                    newFragmentBarcode
                        .setCapturedDataFieldName(fragmentBarcode.getCapturedDataFieldName());
                    newFragmentBarcode.setReconciliationKey(fragmentBarcode.getReconciliationKey());
                    newFragmentBarcode.setSegmentId(newSegment);
                    newFragmentBarcode.setX1Percent(fragmentBarcode.getX1Percent());
                    newFragmentBarcode.setX2Percent(fragmentBarcode.getX2Percent());
                    newFragmentBarcode.setY1Percent(fragmentBarcode.getY1Percent());
                    newFragmentBarcode.setY2Percent(fragmentBarcode.getY2Percent());
                    newFragmentBarcode.setBarcodeType(fragmentBarcode.getBarcodeType());
                    newFragmentBarcode.setOrderIndex(fragmentBarcode.getOrderIndex());

                    entityManager.persist(newFragmentBarcode);
                    entityManager.flush();

                }

            }

        }

        return newForm;
    }

    private void saveProcessedImage(EntityManager entityManager, FormPage formPage)
        throws IOException {

        if (editorType == FORM_PAGE) {

            byte[] binarizedImageData = null;
            binarizedImageData = ImageUtil.getPNGByteArray(sourceImage);

            for (ProcessedImage processedImage : formPage.getProcessedImageCollection()) {
                processedImage.setProcessedImageData(binarizedImageData);
                entityManager.persist(processedImage);
                entityManager.flush();
            }

        } else if (editorType == UNIDENTIFIED_IMAGE) {

            byte[] binarizedImageData = null;
            binarizedImageData = ImageUtil.getPNGByteArray(sourceImage);

            IncomingImage incomingImage = entityManager.find(IncomingImage.class, incomingImageId);
            int scannedPageNumber = incomingImage.getMatchErrorScannedPageNumber();
            formPage.setScannedPageNumber(scannedPageNumber);
            entityManager.persist(formPage);
            entityManager.flush();
            entityManager.remove(incomingImage);
            entityManager.flush();

            if (formPage.getProcessedImageCollection() != null
                && formPage.getProcessedImageCollection().size() > 0) {

                for (ProcessedImage processedImage : formPage.getProcessedImageCollection()) {
                    processedImage.setFormPageId(formPage);
                    processedImage.setProcessedImageData(binarizedImageData);
                    processedImage.setProcessedImageName(incomingImageName);
                    entityManager.persist(processedImage);
                    entityManager.flush();
                }

            } else {

                ProcessedImage processedImage = new ProcessedImage();
                processedImage.setFormPageId(formPage);
                processedImage.setProcessedImageData(binarizedImageData);
                processedImage.setProcessedImageName(incomingImageName);
                entityManager.persist(processedImage);
                entityManager.flush();

            }



        }

    }

    private void closeThisFrame() {

        getPropertiesPanelController().destroyPanels();

        // do this or there will be a memory leak because of the property change listener on figs.
        removeAllFigs();
        Globals.curEditor(null);
        Globals.mode(null);

        Main.getInstance().closeReporcessorFrame(this);

    }

    public void resetDivider() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                splitPane.setDividerLocation(0.7d);
            }
        });
    }

    private void segmentAreaSelectionPanelComponentResized(ComponentEvent e) {
        resetDivider();
    }

    private void capturedDataTableMouseClicked(final MouseEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (e.getClickCount() > 1) {
                    int row = capturedDataTable.getSelectedRow();
                    if (row >= 0) {
                        openSelectionDialog(
                            (FragmentRecognitionStructure) capturedDataTable.getModel()
                                .getValueAt(row, 1));
                    }
                }
            }
        });
    }

    private void openSelectionDialog(FragmentRecognitionStructure frs) {

        List<Fig> figs = _graph.getEditor().getLayerManager().getContents();

        for (Fig fig : figs) {
            if (fig instanceof FigSegmentArea) {

                FigSegmentArea figSegmentArea = (FigSegmentArea) fig;
                SegmentRecognitionStructure srs = figSegmentArea.getSegmentRecognitionStructure();

                if (srs == null) {
                    continue;
                }

                Map<String, OMRRecognitionStructure> omrrs = srs.getOMRRecognitionStructures();
                Map<String, BarcodeRecognitionStructure> bcrs =
                    srs.getBarcodeRecognitionStructures();

                for (String fieldName : omrrs.keySet()) {
                    if (fieldName.equals(frs.getFieldName())) {
                        showSegmentStencilEditorDialog(figSegmentArea, omrrs.get(fieldName));
                        return;
                    }
                }

                for (String fieldName : bcrs.keySet()) {
                    if (fieldName.equals(frs.getFieldName())) {
                        showSegmentStencilEditorDialog(figSegmentArea, bcrs.get(fieldName));
                        return;
                    }
                }

            }
        }

    }

    private void showSegmentStencilEditorDialog(FigSegmentArea fsa,
        FragmentRecognitionStructure frs) {
        SegmentStencilEditorDialog sasd;
        try {
            sasd = new SegmentStencilEditorDialog((Frame) getRootPane().getTopLevelAncestor(), fsa);
            sasd.setTitle(Localizer.localize("UI", "SegmentStencilEditorDialogTitle"));
            sasd.setSelectedRecognitionStructure(frs);
            sasd.setModal(true);
            sasd.setVisible(true);
            sasd.dispose();
        } catch (Exception e) {
        }
    }

    private void zoomToFitCheckBoxStateChanged(ChangeEvent e) {

        AbstractButton abstractButton = (AbstractButton) e.getSource();

        ButtonModel buttonModel = abstractButton.getModel();

        if (buttonModel.isPressed()) {
            zoomSettings.setZoomToFit(zoomToFitCheckBox.isSelected());
            if (zoomSettings.isZoomToFit()) {
                zoomToFit();
            }
        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        segmentAreaSelectionPanel = new JPanel();
        splitPane = new JSplitPane();
        dataPanel = new JPanel();
        doubleClickFieldsDescriptionPanel = new JPanel();
        doubleClickFieldsDescriptionLabel = new JLabel();
        capturedDataScrollPane = new JScrollPane();
        capturedDataTable = new JTable();
        editorPanel = new JPanel();
        reprocessorFrameDescriptionPanel = new JPanel();
        reprocessorFrameDescriptionLabel = new JLabel();
        reprocessorFrameDescriptionHelpLabel = new JHelpLabel();
        toolbarContainerPanel = new JPanel();
        reprocessPalette = new ReprocessPalette();
        graphScrollPane = new JScrollPane();
        graphBackground = new JPanel();
        pagePanel = new JPanel();
        _graph = new ReprocessorGraph();
        zoomPanel = new JPanel();
        editorZoomComboBox = new JComboBox();
        zoomInLabel = new JLabel();
        zoomOutLabel = new JLabel();
        zoomToFitCheckBox = new JCheckBox();

        //======== this ========

        setLayout(new GridBagLayout());
        ((GridBagLayout)getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout)getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout)getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout)getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

        //======== segmentAreaSelectionPanel ========
        {
            segmentAreaSelectionPanel.setOpaque(false);
            segmentAreaSelectionPanel.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    segmentAreaSelectionPanelComponentResized(e);
                }
            });
            segmentAreaSelectionPanel.setLayout(new GridBagLayout());
            ((GridBagLayout)segmentAreaSelectionPanel.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout)segmentAreaSelectionPanel.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout)segmentAreaSelectionPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout)segmentAreaSelectionPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

            //======== splitPane ========
            {
                splitPane.setBorder(null);
                splitPane.setOpaque(false);
                splitPane.setResizeWeight(0.7);
                splitPane.setDividerSize(9);

                //======== dataPanel ========
                {
                    dataPanel.setOpaque(false);
                    dataPanel.setBorder(null);
                    dataPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)dataPanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)dataPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout)dataPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)dataPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

                    //======== doubleClickFieldsDescriptionPanel ========
                    {
                        doubleClickFieldsDescriptionPanel.setBorder(new MatteBorder(0, 0, 3, 0, Color.gray));
                        doubleClickFieldsDescriptionPanel.setOpaque(false);
                        doubleClickFieldsDescriptionPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)doubleClickFieldsDescriptionPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)doubleClickFieldsDescriptionPanel.getLayout()).rowHeights = new int[] {30, 0};
                        ((GridBagLayout)doubleClickFieldsDescriptionPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)doubleClickFieldsDescriptionPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                        //---- doubleClickFieldsDescriptionLabel ----
                        doubleClickFieldsDescriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        doubleClickFieldsDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        doubleClickFieldsDescriptionLabel.setText("<html><body><strong>" + Localizer.localize("UICDM", "DoubleClickFieldsDescriptionLabel") + "</strong></body></html>");
                        doubleClickFieldsDescriptionPanel.add(doubleClickFieldsDescriptionLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    dataPanel.add(doubleClickFieldsDescriptionPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));

                    //======== capturedDataScrollPane ========
                    {

                        //---- capturedDataTable ----
                        capturedDataTable.setFont(UIManager.getFont("Table.font"));
                        capturedDataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        capturedDataTable.setShowVerticalLines(false);
                        capturedDataTable.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                capturedDataTableMouseClicked(e);
                            }
                        });
                        capturedDataTable.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));
                        capturedDataScrollPane.setViewportView(capturedDataTable);
                    }
                    dataPanel.add(capturedDataScrollPane, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                splitPane.setRightComponent(dataPanel);

                //======== editorPanel ========
                {
                    editorPanel.setOpaque(false);
                    editorPanel.setBorder(null);
                    editorPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)editorPanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)editorPanel.getLayout()).rowHeights = new int[] {40, 65, 0, 5, 0, 0};
                    ((GridBagLayout)editorPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)editorPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};

                    //======== reprocessorFrameDescriptionPanel ========
                    {
                        reprocessorFrameDescriptionPanel.setBorder(new MatteBorder(0, 0, 3, 0, Color.gray));
                        reprocessorFrameDescriptionPanel.setOpaque(false);
                        reprocessorFrameDescriptionPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)reprocessorFrameDescriptionPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                        ((GridBagLayout)reprocessorFrameDescriptionPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)reprocessorFrameDescriptionPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)reprocessorFrameDescriptionPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                        //---- reprocessorFrameDescriptionLabel ----
                        reprocessorFrameDescriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        reprocessorFrameDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        reprocessorFrameDescriptionLabel.setText("<html><body><strong>" + Localizer.localize("UICDM", "ReprocessorFrameDescriptionLabel") + "</strong></body></html>");
                        reprocessorFrameDescriptionPanel.add(reprocessorFrameDescriptionLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- reprocessorFrameDescriptionHelpLabel ----
                        reprocessorFrameDescriptionHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                        reprocessorFrameDescriptionHelpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        reprocessorFrameDescriptionHelpLabel.setFont(UIManager.getFont("Label.font"));
                        reprocessorFrameDescriptionHelpLabel.setHelpGUID("form-pages-error-reprocessing");
                        reprocessorFrameDescriptionHelpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                        reprocessorFrameDescriptionPanel.add(reprocessorFrameDescriptionHelpLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    editorPanel.add(reprocessorFrameDescriptionPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));

                    //======== toolbarContainerPanel ========
                    {
                        toolbarContainerPanel.setOpaque(false);
                        toolbarContainerPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)toolbarContainerPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                        ((GridBagLayout)toolbarContainerPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)toolbarContainerPanel.getLayout()).columnWeights = new double[] {1.0, 1.0, 1.0, 1.0E-4};
                        ((GridBagLayout)toolbarContainerPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                        //======== reprocessPalette ========
                        {
                            reprocessPalette.unpressAllButtons();
                        }
                        toolbarContainerPanel.add(reprocessPalette, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    editorPanel.add(toolbarContainerPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0), 0, 0));

                    //======== graphScrollPane ========
                    {
                        graphScrollPane.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));

                        //======== graphBackground ========
                        {
                            graphBackground.setBackground(Color.darkGray);
                            graphBackground.setBorder(null);
                            graphBackground.setLayout(new GridBagLayout());
                            ((GridBagLayout)graphBackground.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout)graphBackground.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                            ((GridBagLayout)graphBackground.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                            ((GridBagLayout)graphBackground.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0, 1.0E-4};

                            //======== pagePanel ========
                            {
                                pagePanel.setLayout(new GridBagLayout());
                                ((GridBagLayout)pagePanel.getLayout()).columnWidths = new int[] {0, 0};
                                ((GridBagLayout)pagePanel.getLayout()).rowHeights = new int[] {0, 0};
                                ((GridBagLayout)pagePanel.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
                                ((GridBagLayout)pagePanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                                //---- _graph ----
                                _graph.setToolTipText("");
                                _graph.setBorder(null);
                                _graph.setPreferredSize(new Dimension(300, 300));
                                _graph.setReprocessorFrame(this);
                                pagePanel.add(_graph, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
                            }
                            graphBackground.add(pagePanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                                new Insets(0, 0, 5, 0), 0, 0));
                        }
                        graphScrollPane.setViewportView(graphBackground);
                    }
                    editorPanel.add(graphScrollPane, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));

                    //======== zoomPanel ========
                    {
                        zoomPanel.setOpaque(false);
                        zoomPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)zoomPanel.getLayout()).columnWidths = new int[] {15, 0, 0, 0, 0, 0, 10, 0};
                        ((GridBagLayout)zoomPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)zoomPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0E-4};
                        ((GridBagLayout)zoomPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                        //---- editorZoomComboBox ----
                        editorZoomComboBox.setModel(new DefaultComboBoxModel(new String[] {
                            "10%",
                            "25%",
                            "50%",
                            "75%",
                            "100%",
                            "125%",
                            "150%",
                            "200%",
                            "250%",
                            "350%",
                            "500%",
                            "700%",
                            "1000%"
                        }));
                        editorZoomComboBox.setSelectedIndex(4);
                        editorZoomComboBox.setFont(UIManager.getFont("ComboBox.font"));
                        editorZoomComboBox.setEditable(true);
                        editorZoomComboBox.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                editorZoomComboBoxActionPerformed(e);
                            }
                        });
                        zoomPanel.add(editorZoomComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.WEST, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- zoomInLabel ----
                        zoomInLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/preview/zoom_in.png")));
                        zoomInLabel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                zoomInLabelMouseClicked(e);
                            }
                        });
                        zoomInLabel.setToolTipText(Localizer.localize("UI", "ZoomInToolTip"));
                        zoomPanel.add(zoomInLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- zoomOutLabel ----
                        zoomOutLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/preview/zoom_out.png")));
                        zoomOutLabel.setFont(UIManager.getFont("Label.font"));
                        zoomOutLabel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                zoomOutLabelMouseClicked(e);
                            }
                        });
                        zoomOutLabel.setToolTipText(Localizer.localize("UI", "ZoomOutToolTip"));
                        zoomPanel.add(zoomOutLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- zoomToFitCheckBox ----
                        zoomToFitCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                        zoomToFitCheckBox.setFocusPainted(false);
                        zoomToFitCheckBox.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                zoomToFitCheckBoxStateChanged(e);
                            }
                        });
                        zoomToFitCheckBox.setText(Localizer.localize("UI", "ZoomToFitCheckBoxText"));
                        zoomPanel.add(zoomToFitCheckBox, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    editorPanel.add(zoomPanel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                splitPane.setLeftComponent(editorPanel);
            }
            segmentAreaSelectionPanel.add(splitPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        add(segmentAreaSelectionPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel segmentAreaSelectionPanel;
    private JSplitPane splitPane;
    private JPanel dataPanel;
    private JPanel doubleClickFieldsDescriptionPanel;
    private JLabel doubleClickFieldsDescriptionLabel;
    private JScrollPane capturedDataScrollPane;
    private JTable capturedDataTable;
    private JPanel editorPanel;
    private JPanel reprocessorFrameDescriptionPanel;
    private JLabel reprocessorFrameDescriptionLabel;
    private JHelpLabel reprocessorFrameDescriptionHelpLabel;
    private JPanel toolbarContainerPanel;
    private ReprocessPalette reprocessPalette;
    private JScrollPane graphScrollPane;
    private JPanel graphBackground;
    private JPanel pagePanel;
    private ReprocessorGraph _graph;
    private JPanel zoomPanel;
    private JComboBox editorZoomComboBox;
    private JLabel zoomInLabel;
    private JLabel zoomOutLabel;
    private JCheckBox zoomToFitCheckBox;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private double angle;

    public boolean isFinishedLoading() {
        return finishedLoading;
    }

    public void setFinishedLoading(boolean finishedLoading) {
        this.finishedLoading = finishedLoading;
    }

    public long getIncomingImageId() {
        return incomingImageId;
    }

    public void setIncomingImageId(long incomingImageId) {
        this.incomingImageId = incomingImageId;
    }

    public boolean selectFormPage() {
        FormPageSelectionDialog fpsd = new FormPageSelectionDialog(Main.getInstance(), this);
        fpsd.setTitle(Localizer.localize("UI", "FormPageSelectionDialogTitle"));
        fpsd.setModal(true);
        fpsd.setVisible(true);
        if (fpsd.getDialogResult() == JOptionPane.OK_OPTION) {
            if (fpsd.getSelectedFormPageId() <= 0) {
                return false;
            }
            formPageId = fpsd.getSelectedFormPageId();
            updateProperties();
            return true;
        } else {
            return false;
        }
    }

    public long getFormPageId() {
        return formPageId;
    }

    public int getEditorType() {
        return editorType;
    }

    public void setEditorType(int editorType) {
        this.editorType = editorType;
    }

    public void setFormPageId(long formPageId) {
        this.formPageId = formPageId;
    }

    public PageAttributes getPageAttributes() {
        return pageAttributes;
    }

    public void setPageAttributes(PageAttributes pageAttributes) {
        this.pageAttributes = pageAttributes;
    }

    public boolean adjustImage() {
        AdjustImageDialog aid = new AdjustImageDialog(Main.getInstance());
        aid.setImage(this.sourceImage);
        aid.setModal(true);
        aid.setVisible(true);
        if (aid.getDialogResult() == JOptionPane.OK_OPTION) {
            angle = aid.getRotationAngle();
            this.sourceImage = aid.getDisplayImage();
            this.backgroundImage = null; // remove existing fig
            return true;
        } else {
            return false;
        }
    }

    public BufferedImage getSourceImage() {
        return sourceImage;
    }

    public void appendCapturedData(HashMap<String, Object> capturedData) {

        if (capturedData == null || capturedData.size() <= 0) {
            return;
        }

        for (String fieldname : capturedData.keySet()) {
            for (int i = 0; i < capturedDataTable.getRowCount(); i++) {
                if (((String) capturedDataTable.getValueAt(i, 0)).equals(fieldname)) {

                    Object obj = capturedData.get(fieldname);

                    if (obj instanceof OMRRecognitionStructure) {
                        OMRRecognitionStructure omrrs = (OMRRecognitionStructure) obj;
                        capturedDataTable.setValueAt(omrrs, i, 1);
                        capturedDataTable.setValueAt(
                            omrrs.isInvalidated() ? Localizer.localize("UI", "ErrorTitle") : "", i,
                            2);
                    } else if (obj instanceof BarcodeRecognitionStructure) {
                        BarcodeRecognitionStructure bcrs = (BarcodeRecognitionStructure) obj;
                        capturedDataTable.setValueAt(bcrs, i, 1);
                        capturedDataTable.setValueAt(
                            bcrs.isInvalidated() ? Localizer.localize("UI", "ErrorTitle") : "", i,
                            2);
                    }
                }
            }
        }

    }

    public void selectFormPageIDButtonActionPerformed(ActionEvent e) {
        if (selectFormPage()) {
            final LoadingDialog ld = new LoadingDialog(Main.getInstance());
            ld.setVisible(true);

            Main.getInstance().blockInput();

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                protected Void doInBackground() throws InterruptedException {
                    try {
                        restore();
                    } catch (Exception ex) {
                        com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                    }
                    return null;
                }

                protected void done() {
                    Main.getInstance().unblockInput();
                    ld.dispose();
                    try {
                        get();
                    } catch (InterruptedException e) {
                    } catch (ExecutionException e) {
                    } catch (Exception ex) {
                        com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                    }
                }
            };
            worker.execute();
        }
    }

    public FigSegmentArea getNextUnusedSegmentArea() {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return null;
        }

        try {
            long formPageId = getFormPageId();
            FormPage formPage = entityManager.find(FormPage.class, formPageId);
            List<Segment> sc = formPage.getSegmentCollection();

            TreeMap<Integer, Segment> sortedSegments = new TreeMap<Integer, Segment>();

            for (Segment segment : sc) {
                sortedSegments.put(Integer.parseInt(segment.getBarcodeOne()), segment);
            }

            for (Integer barcodeValue : sortedSegments.keySet()) {
                Segment nextSegment = sortedSegments.get(barcodeValue);
                FigSegmentArea fsa = new FigSegmentArea(200, 200);
                fsa.setSegmentId(nextSegment.getSegmentId());
                fsa.setLuminanceThreshold(getLuminanceThreshold());
                fsa.setMarkThreshold(getMarkThreshold());
                fsa.setFragmentPadding(getFragmentPadding());
                fsa.damage();
                return fsa;
            }

        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return null;

    }

}
