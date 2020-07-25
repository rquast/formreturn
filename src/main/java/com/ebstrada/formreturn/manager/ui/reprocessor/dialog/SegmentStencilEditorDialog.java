package com.ebstrada.formreturn.manager.ui.reprocessor.dialog;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.ButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;

import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.presentation.FigSegmentArea;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.reader.BarcodeImageReader;
import com.ebstrada.formreturn.manager.logic.recognition.reader.FormReaderException;
import com.ebstrada.formreturn.manager.logic.recognition.structure.BarcodeRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.CheckBoxRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.FragmentRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.OMRMatrix;
import com.ebstrada.formreturn.manager.logic.recognition.structure.OMRRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.SegmentRecognitionStructure;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.persistence.jpa.Segment;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.reprocessor.collection.RecognitionStructureMap;
import com.ebstrada.formreturn.manager.ui.reprocessor.component.CapturedDataItem;
import com.ebstrada.formreturn.manager.ui.reprocessor.component.ReprocessorGraph;
import com.ebstrada.formreturn.manager.ui.reprocessor.frame.ReprocessorFrame;
import com.ebstrada.formreturn.manager.util.Misc;

public class SegmentStencilEditorDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private FigSegmentArea figSegmentArea;

    private long selectedSegmentId;

    private ReprocessorFrame reprocessorFrame;

    private ArrayList<Long> segmentIds;
    private ArrayList<String> segmentNames;

    private int dialogResult = JOptionPane.CANCEL_OPTION;

    private BufferedImage fragmentImage;

    private Float realZoom = 1.0f;


    private class MarkValuesCaretListener implements CaretListener {
        public void caretUpdate(CaretEvent e) {

            int row = dataTable.getSelectedRow();
            int column = dataTable.getSelectedColumn();

            if (row < 0 || column < 0) {
                return;
            }

            JTextField source = (JTextField) e.getSource();
            String newText = source.getText();

            Object obj = dataTable.getValueAt(row, column);

            if (obj instanceof CapturedDataItem) {
                CapturedDataItem cdi = (CapturedDataItem) obj;
                String oldText = cdi.getValue();
                if (oldText == null || !(oldText.equals(newText))) {
                    cdi.setValue(newText);
                    updateField();
                }
            }

        }
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


    private class JTableComponentCellEditor extends AbstractCellEditor implements TableCellEditor {

        private static final long serialVersionUID = 1L;

        JCheckBox checkBox;
        EditorTextField textArea = null;

        public JTableComponentCellEditor() {
            checkBox = null;
            textArea = null;
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
            int row, int column) {
            if (value instanceof CapturedDataItem) {
                CapturedDataItem data = (CapturedDataItem) value;
                if (data.getType() == CapturedDataItem.OMR_FIELD) {
                    checkBox = new JCheckBox();
                    ButtonUI checkBoxUI = checkBox.getUI();
                    checkBox.setFont(UIManager.getFont("CheckBox.font"));
                    checkBox.setHorizontalAlignment(SwingConstants.CENTER);
                    checkBox.setBackground(Color.white);
                    checkBox.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent evt) {
                            fireEditingStopped();
                        }
                    });
                    checkBox.setSelected(data.isMarked());
                    checkBox.setHorizontalAlignment(JLabel.CENTER);
                    checkBox.setText(data.getValue());
                    return checkBox;
                } else if (data.getType() == CapturedDataItem.BARCODE_FIELD) {
                    textArea = new EditorTextField();
                    textArea.setFont(UIManager.getFont("TextArea.font"));
                    textArea.setBackground(Color.white);
                    textArea.addCaretListener(new MarkValuesCaretListener());
                    textArea.setBorder(null);
                    dataTable.setDefaultEditor(Object.class, new DefaultCellEditor(textArea));
                    textArea.setText(data.getValue());
                    return textArea;
                }
            }
            return null;
        }

        public Object getCellEditorValue() {
            CapturedDataItem data = null;
            if (checkBox != null) {
                data = new CapturedDataItem();
                data.setValue(checkBox.getText());
                data.setMarked(checkBox.isSelected());
            } else if (textArea != null) {
                data = new CapturedDataItem();
                data.setType(CapturedDataItem.BARCODE_FIELD);
                data.setValue(textArea.getText());
            }
            return data;
        }

    }


    private class JTableComponentRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        JCheckBox checkBox;
        EditorTextField textArea;

        public JTableComponentRenderer() {
            checkBox = null;
            textArea = null;
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof CapturedDataItem) {
                CapturedDataItem data = (CapturedDataItem) value;
                if (data.getType() == CapturedDataItem.OMR_FIELD) {
                    checkBox = new JCheckBox();
                    ButtonUI checkBoxUI = checkBox.getUI();
                    checkBox.setFont(UIManager.getFont("CheckBox.font"));
                    checkBox.setHorizontalAlignment(SwingConstants.CENTER);
                    checkBox.setBackground(Color.white);
                    checkBox.setSelected(data.isMarked());

                    checkBox.setHorizontalAlignment(JLabel.CENTER);
                    checkBox.setText(data.getValue());
                    return checkBox;
                } else if (data.getType() == CapturedDataItem.BARCODE_FIELD) {
                    textArea = new EditorTextField();
                    textArea.setFont(UIManager.getFont("TextArea.font"));
                    textArea.setBackground(Color.white);
                    textArea.addCaretListener(new MarkValuesCaretListener());
                    textArea.setBorder(null);
                    dataTable.setDefaultEditor(Object.class, new DefaultCellEditor(textArea));
                    textArea.setText(data.getValue());
                    return textArea;
                }
            }
            String str = (value == null) ? "" : value.toString();
            return super
                .getTableCellRendererComponent(table, str, isSelected, hasFocus, row, column);
        }

    }


    private class SelectionListener implements ListSelectionListener {
        JTable table;

        SelectionListener(JTable table) {
            this.table = table;
        }

        public void valueChanged(ListSelectionEvent e) {
            if (e.getSource() == table.getSelectionModel() && table.getRowSelectionAllowed()) {
                if (!(e.getValueIsAdjusting())) {
                    updateDataTable();
                }
            } else if (e.getSource() == table.getColumnModel().getSelectionModel() && table
                .getColumnSelectionAllowed()) {
                if (!(e.getValueIsAdjusting())) {
                    updateDataTable();
                }
            }
        }
    }

    public SegmentStencilEditorDialog(Frame owner, FigSegmentArea figSegmentArea) throws Exception {
        super(owner);
        this.figSegmentArea = figSegmentArea;
        initComponents();

        imagePreviewScrollPane.getVerticalScrollBar().setUnitIncrement(30);
        imagePreviewScrollPane.getVerticalScrollBar().setBlockIncrement(90);
        imagePreviewScrollPane.getHorizontalScrollBar().setUnitIncrement(30);
        imagePreviewScrollPane.getHorizontalScrollBar().setBlockIncrement(90);

        this.fieldTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        imagePreviewLabel.setSasd(this);
        getRootPane().setDefaultButton(selectSegmentButton);
        restore();
        setupTables();
    }

    public void resetFragmentPreviewDivider() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dataTableSplitPane.setDividerLocation(0.5d);
            }
        });
    }

    public void resetFieldDataDivider() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dataTableSplitPane.setDividerLocation(0.4d);
            }
        });
    }

    public void resizeImageLabel() {

        Dimension dim = new Dimension((int) (fragmentImage.getWidth() * realZoom),
            (int) (fragmentImage.getHeight() * realZoom));
        if (imagePreviewLabel != null) {
            imagePreviewLabel.setMaximumSize(dim);
            imagePreviewLabel.setMinimumSize(dim);
            imagePreviewLabel.setPreferredSize(dim);
        } else {
            imagePreviewLabel.setMaximumSize(new Dimension(100, 100));
            imagePreviewLabel.setMinimumSize(new Dimension(100, 100));
            imagePreviewLabel.setPreferredSize(new Dimension(100, 100));
        }
        imagePreviewLabel.revalidate();
        imagePreviewLabel.repaint();

        resetFragmentPreviewDivider();

    }

    public void updateField() {

        int rowIndex = fieldTable.getSelectedRow();

        if (rowIndex == -1) {
            return;
        }

        Object obj = fieldTable.getValueAt(rowIndex, 1);

        if (obj instanceof OMRRecognitionStructure) {
            OMRRecognitionStructure omrrs =
                (OMRRecognitionStructure) fieldTable.getValueAt(rowIndex, 1);

            for (int i = 0; i < dataTable.getRowCount(); i++) {
                for (int j = 0; j < dataTable.getColumnCount(); j++) {
                    CapturedDataItem cdi = (CapturedDataItem) dataTable.getValueAt(i, j);
                    // set the data in omrrs
                    for (CheckBoxRecognitionStructure cbrs : omrrs
                        .getCheckBoxRecognitionStructures()) {
                        if (cbrs.getRow() == i && cbrs.getColumn() == j) {
                            cbrs.setCheckBoxMarked(cdi.isMarked());
                            omrrs.setInvalidated(false);
                        }
                    }
                }
            }

            fieldTable.updateUI();

        } else if (obj instanceof BarcodeRecognitionStructure) {

            BarcodeRecognitionStructure bcrs =
                (BarcodeRecognitionStructure) fieldTable.getValueAt(rowIndex, 1);

            for (int i = 0; i < dataTable.getRowCount(); i++) {
                for (int j = 0; j < dataTable.getColumnCount(); j++) {
                    Object obj2 = dataTable.getValueAt(i, j);
                    if (obj2 instanceof CapturedDataItem) {
                        CapturedDataItem cdi = (CapturedDataItem) obj2;
                        // set the data in the bcrs
                        bcrs.setBarcodeValue(cdi.getValue());
                        bcrs.setInvalidated(false);
                    } else if (obj2 instanceof String) {
                        bcrs.setBarcodeValue((String) obj2);
                        bcrs.setInvalidated(false);
                    }
                }
            }

            fieldTable.updateUI();

        }


    }

    public void updateDataTable() {

        int rowIndex = fieldTable.getSelectedRow();

        if (rowIndex == -1) {
            return;
        }

        Object obj = fieldTable.getValueAt(rowIndex, 1);

        if (obj instanceof OMRRecognitionStructure) {
            OMRRecognitionStructure omrrs =
                (OMRRecognitionStructure) fieldTable.getValueAt(rowIndex, 1);
            updateDataTable(omrrs);
            setFragmentImage(omrrs);
        } else if (obj instanceof BarcodeRecognitionStructure) {
            BarcodeRecognitionStructure bcrs =
                (BarcodeRecognitionStructure) fieldTable.getValueAt(rowIndex, 1);
            updateDataTable(bcrs);
            setFragmentImage(bcrs);
        }

    }

    public void setFragmentImage(OMRRecognitionStructure omrrs) {

        try {
            fragmentImage = getFragmentImage(reprocessorFrame.getSourceImage(), omrrs);
            resizeImageLabel();
        } catch (FormReaderException e) {
            e.printStackTrace();
        }


    }

    public void setFragmentImage(BarcodeRecognitionStructure bcrs) {

        try {
            fragmentImage = getFragmentImage(reprocessorFrame.getSourceImage(), bcrs);
            resizeImageLabel();
        } catch (FormReaderException e) {
            e.printStackTrace();
        }

    }

    private void updateDataTable(final BarcodeRecognitionStructure bcrs) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                DefaultTableModel model =
                    reprocessorFrame.getBarcodeRecognitionStructureTableModel(bcrs);
                model.addTableModelListener(new TableModelListener() {
                    public void tableChanged(TableModelEvent e) {
                        updateField();
                    }
                });
                dataTable.setModel(model);
                for (int i = 0; i < dataTable.getColumnCount(); i++) {
                    TableColumn column = dataTable.getColumnModel().getColumn(i);
                    column.setCellRenderer(new JTableComponentRenderer());
                    column.setCellEditor(new JTableComponentCellEditor());
                }
                dataTable.getTableHeader().setReorderingAllowed(false);
            }
        });
    }

    private void updateDataTable(final OMRRecognitionStructure omrrs) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                DefaultTableModel model =
                    reprocessorFrame.getOMRRecognitionStructureTableModel(omrrs);
                model.addTableModelListener(new TableModelListener() {
                    public void tableChanged(TableModelEvent e) {
                        updateField();
                    }
                });
                dataTable.setModel(model);
                for (int i = 0; i < dataTable.getColumnCount(); i++) {
                    TableColumn column = dataTable.getColumnModel().getColumn(i);
                    column.setCellRenderer(new JTableComponentRenderer());
                    column.setCellEditor(new JTableComponentCellEditor());
                }
                dataTable.getTableHeader().setReorderingAllowed(false);
            }
        });
    }

    private void setupTables() {
        SelectionListener listener = new SelectionListener(fieldTable);
        fieldTable.getSelectionModel().addListSelectionListener(listener);
        fieldTable.getColumnModel().getSelectionModel().addListSelectionListener(listener);
        fieldTable.getTableHeader().setReorderingAllowed(false);
        dataTable.setCellSelectionEnabled(true);
        dataTable.setRowSelectionAllowed(false);
        dataTable.getTableHeader().setReorderingAllowed(false);
    }

    public void captureData() throws FormReaderException {

        FormReaderException fre = null;

        SegmentRecognitionStructure srs = figSegmentArea.getSegmentRecognitionStructure();
        if (srs != null) {

            RecognitionStructureMap fields = new RecognitionStructureMap();

            for (OMRRecognitionStructure omrrs : srs.getOMRRecognitionStructures().values()) {

                OMRMatrix omrMatrix =
                    new OMRMatrix(omrrs, getFragmentImage(reprocessorFrame.getSourceImage(), omrrs),
                        figSegmentArea.getMarkThreshold());

                try {
                    omrMatrix.process();
                    omrrs.setCapturedData(omrMatrix.getOmrBoxMatrix());
                    omrrs.setInvalidated(false);
                } catch (FormReaderException fre1) {
                    fre = fre1;
                    omrrs.setInvalidated(true);
                }

                fields.addStructure(omrrs);

            }

            Map<String, BarcodeRecognitionStructure> BarcodeRecognitionStructures =
                srs.getBarcodeRecognitionStructures();

            if (BarcodeRecognitionStructures != null) {

                for (BarcodeRecognitionStructure bcrs : BarcodeRecognitionStructures.values()) {

                    try {

                        BufferedImage fragmentImage =
                            getFragmentImage(reprocessorFrame.getSourceImage(), bcrs);
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


            setFieldsTable(fields);

        }

        if (fre != null) {
            throw fre;
        }

    }

    private void setFieldsTable(final RecognitionStructureMap fields) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fieldTable.setModel(reprocessorFrame.getSegmentCapturedDataModel(fields));
                fieldTable.getTableHeader().setReorderingAllowed(false);
            }
        });
    }

    private BufferedImage getFragmentImage(BufferedImage sourceImage, OMRRecognitionStructure omrrs)
        throws FormReaderException {
        return reprocessorFrame.getFragmentImage(figSegmentArea, sourceImage, omrrs);
    }

    private BufferedImage getFragmentImage(BufferedImage sourceImage,
        BarcodeRecognitionStructure bcrs) throws FormReaderException {
        return reprocessorFrame.getFragmentImage(figSegmentArea, sourceImage, bcrs);
    }

    public void setFieldnames() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                selectedSegmentId = segmentIds.get(segmentNumberComboBox.getSelectedIndex());
                fieldTable
                    .setModel(reprocessorFrame.getSegmentCapturedDataModel(selectedSegmentId));
                fieldTable.getTableHeader().setReorderingAllowed(false);
            }
        });
    }

    public void restore() throws Exception {
        JGraph graph = figSegmentArea.getGraph();
        if (graph instanceof ReprocessorGraph) {
            reprocessorFrame = ((ReprocessorGraph) graph).getReprocessorFrame();
        }
        if (reprocessorFrame == null || reprocessorFrame.getFormPageId() <= 0) {
            graph.getEditor().getSelectionManager().deselectAll();
            graph.getEditor().remove(figSegmentArea);
            graph.getEditor().damageAll();
            Misc.showErrorMsg(Main.getInstance(),
                Localizer.localize("UI", "ReprocessorFrameNoFormPageSelectedMessage"));
            throw new Exception();
        }

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        int currentSegmentIndex = 0;

        try {
            long formPageId = reprocessorFrame.getFormPageId();
            FormPage formPage = entityManager.find(FormPage.class, formPageId);
            List<Segment> sc = formPage.getSegmentCollection();

            TreeMap<Integer, Segment> sortedSegments = new TreeMap<Integer, Segment>();

            for (Segment segment : sc) {
                sortedSegments.put(Integer.parseInt(segment.getBarcodeOne()), segment);
            }

            segmentIds = new ArrayList<Long>();
            segmentNames = new ArrayList<String>();

            int i = 0;
            for (Segment segment : sortedSegments.values()) {
                if (figSegmentArea != null
                    && figSegmentArea.getSegmentRecognitionStructure() != null
                    && Integer.parseInt(segment.getBarcodeOne()) == figSegmentArea
                    .getSegmentRecognitionStructure().getBarcodeOneValue()) {
                    currentSegmentIndex = i;
                }
                i++;
                segmentIds.add(segment.getSegmentId());
                segmentNames.add(String.format(
                    Localizer.localize("UI", "ReprocessorFrameSegmentAreaSelectionDropdown"),
                    segment.getSegmentId() + "", segment.getBarcodeOne() + "",
                    segment.getBarcodeTwo() + ""));
            }
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            segmentIds = null;
            segmentNames = null;
            return;
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

        if (segmentIds != null && segmentNames != null) {
            DefaultComboBoxModel sncb = new DefaultComboBoxModel();
            for (String segmentName : segmentNames) {
                sncb.addElement(segmentName);
            }
            this.segmentNumberComboBox.setModel(sncb);

            this.segmentNumberComboBox.setSelectedIndex(currentSegmentIndex);

            setFieldnames();
        }

        restoreRecognitionSettings();

    }

    public SegmentStencilEditorDialog(Dialog owner) throws Exception {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(selectSegmentButton);
        restore();
    }

    public FigSegmentArea getFigSegmentArea() {
        return figSegmentArea;
    }

    public void setFigSegmentArea(FigSegmentArea figSegmentArea) {
        this.figSegmentArea = figSegmentArea;
    }

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                segmentNumberComboBox.requestFocusInWindow();
            }
        });
    }

    private void segmentNumberComboBoxActionPerformed(ActionEvent e) {
        setFieldnames();
    }

    private void addToCapturedData() {

        if (fieldTable == null) {
            return;
        }

        if (fieldTable.getRowCount() <= 0) {
            return;
        }


        HashMap<String, Object> capturedData = new HashMap<String, Object>();

        for (int i = 0; i < fieldTable.getRowCount(); i++) {
            capturedData.put((String) fieldTable.getValueAt(i, 0), fieldTable.getValueAt(i, 1));
        }

        reprocessorFrame.appendCapturedData(capturedData);

    }

    private void selectSegmentButtonActionPerformed(ActionEvent e) {
        addToCapturedData();
        selectedSegmentId = segmentIds.get(segmentNumberComboBox.getSelectedIndex());
        setRecognitionSettings();
        figSegmentArea.setSegmentId(selectedSegmentId);
        figSegmentArea.damage();
        setDialogResult(JOptionPane.OK_OPTION);
        dispose();
    }

    private void closeButtonActionPerformed(ActionEvent e) {
        setDialogResult(JOptionPane.CANCEL_OPTION);
        dispose();
    }

    private void restoreRecognitionSettings() {
        luminanceSpinner.setValue(figSegmentArea.getLuminanceThreshold());
        markThresholdSpinner.setValue(figSegmentArea.getRealMarkThreshold());
        fragmentPaddingSpinner.setValue(figSegmentArea.getFragmentPadding());
    }

    private void setRecognitionSettings() {
        Integer luminance = (Integer) luminanceSpinner.getValue();
        Integer markThreshold = (Integer) markThresholdSpinner.getValue();
        Integer padding = (Integer) fragmentPaddingSpinner.getValue();
        figSegmentArea.setLuminanceThreshold(luminance);
        figSegmentArea.setMarkThreshold(markThreshold);
        figSegmentArea.setFragmentPadding(padding);
    }

    private void detectDataButtonActionPerformed(ActionEvent e) {
        try {
            setRecognitionSettings();
            captureData();
        } catch (FormReaderException ex) {
            Misc.showExceptionMsg(this, ex);
        }
    }

    private void zoomBoxItemStateChanged(ItemEvent e) {
        String unparsedString = (String) zoomBox.getSelectedObjects()[0];
        String zoomString = "";
        for (int i = 0; i < unparsedString.length(); i++) {
            if (unparsedString.charAt(i) >= 48 && unparsedString.charAt(i) <= 57) {
                zoomString += unparsedString.charAt(i);
            }
        }
        realZoom = Float.parseFloat(zoomString) / 100.0f;
        resizeImageLabel();
        repaint();
    }

    private void zoomInLabelMouseClicked(MouseEvent e) {
        int selectedIndex = zoomBox.getSelectedIndex();

        if (selectedIndex < (zoomBox.getItemCount() - 1)) {
            zoomBox.setSelectedIndex(selectedIndex + 1);
        }
    }

    private void zoomOutLabelMouseClicked(MouseEvent e) {
        int selectedIndex = zoomBox.getSelectedIndex();

        if (selectedIndex > 0) {
            zoomBox.setSelectedIndex(selectedIndex - 1);
        }
    }

    private void thisWindowActivated(WindowEvent e) {
        resetFieldDataDivider();
        resetFragmentPreviewDivider();
    }

    private void thisComponentResized(ComponentEvent e) {
        resetFieldDataDivider();
        resetFragmentPreviewDivider();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        fieldTablePanel = new JPanel();
        fieldDataSplitPane = new JSplitPane();
        fieldNamesPanel = new JPanel();
        fieldTableScrollPane = new JScrollPane();
        fieldTable = new JTable();
        dataTablePanel = new JPanel();
        dataTableSplitPane = new JSplitPane();
        dataTableScrollPane = new JScrollPane();
        dataTable = new JTable();
        imagePreviewScrollPane = new JScrollPane();
        imagePreviewLabel = new ImagePreviewLabel();
        panel1 = new JPanel();
        zoomBox = new JComboBox();
        zoomInLabel = new JLabel();
        zoomOutLabel = new JLabel();
        bottomPanel = new JPanel();
        recognitionSettingsPanel = new JPanel();
        luminanceLabel = new JLabel();
        luminanceSpinner = new JSpinner();
        markThresholdLabel = new JLabel();
        markThresholdSpinner = new JSpinner();
        fragmentPaddingLabel = new JLabel();
        fragmentPaddingSpinner = new JSpinner();
        segmentNumberPanel = new JPanel();
        segmentNumberComboBox = new JComboBox();
        buttonBar = new JPanel();
        detectDataButton = new JButton();
        selectSegmentButton = new JButton();
        closeButton = new JButton();

        //======== this ========
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override public void windowActivated(WindowEvent e) {
                thisWindowActivated(e);
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                thisComponentResized(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setOpaque(false);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setOpaque(false);
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights =
                    new double[] {1.0, 0.0, 1.0E-4};

                //======== fieldTablePanel ========
                {
                    fieldTablePanel.setOpaque(false);
                    fieldTablePanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) fieldTablePanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout) fieldTablePanel.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout) fieldTablePanel.getLayout()).columnWeights =
                        new double[] {1.0, 1.0E-4};
                    ((GridBagLayout) fieldTablePanel.getLayout()).rowWeights =
                        new double[] {1.0, 1.0E-4};

                    //======== fieldDataSplitPane ========
                    {
                        fieldDataSplitPane.setBorder(null);
                        fieldDataSplitPane.setOpaque(false);
                        fieldDataSplitPane.setResizeWeight(0.45);
                        fieldDataSplitPane.setDividerLocation(350);

                        //======== fieldNamesPanel ========
                        {
                            fieldNamesPanel.setOpaque(false);
                            fieldNamesPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout) fieldNamesPanel.getLayout()).columnWidths =
                                new int[] {0, 0};
                            ((GridBagLayout) fieldNamesPanel.getLayout()).rowHeights =
                                new int[] {0, 0};
                            ((GridBagLayout) fieldNamesPanel.getLayout()).columnWeights =
                                new double[] {1.0, 1.0E-4};
                            ((GridBagLayout) fieldNamesPanel.getLayout()).rowWeights =
                                new double[] {1.0, 1.0E-4};
                            fieldNamesPanel.setBorder(new CompoundBorder(new TitledBorder(
                                Localizer.localize("UI", "ReprocessorFrameFieldNamesPanelTitle")),
                                new EmptyBorder(5, 5, 5, 5)));

                            //======== fieldTableScrollPane ========
                            {

                                //---- fieldTable ----
                                fieldTable.setFont(UIManager.getFont("Table.font"));
                                fieldTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                                fieldTable.setShowVerticalLines(false);
                                fieldTable.getTableHeader()
                                    .setFont(UIManager.getFont("TableHeader.font"));
                                fieldTableScrollPane.setViewportView(fieldTable);
                            }
                            fieldNamesPanel.add(fieldTableScrollPane,
                                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
                        }
                        fieldDataSplitPane.setLeftComponent(fieldNamesPanel);

                        //======== dataTablePanel ========
                        {
                            dataTablePanel.setOpaque(false);
                            dataTablePanel.setLayout(new GridBagLayout());
                            ((GridBagLayout) dataTablePanel.getLayout()).columnWidths =
                                new int[] {300, 0};
                            ((GridBagLayout) dataTablePanel.getLayout()).rowHeights =
                                new int[] {0, 0, 0};
                            ((GridBagLayout) dataTablePanel.getLayout()).columnWeights =
                                new double[] {1.0, 1.0E-4};
                            ((GridBagLayout) dataTablePanel.getLayout()).rowWeights =
                                new double[] {1.0, 0.0, 1.0E-4};
                            dataTablePanel.setBorder(new CompoundBorder(new TitledBorder(
                                Localizer.localize("UI", "ReprocessorFrameDataTablePanelTitle")),
                                new EmptyBorder(5, 5, 5, 5)));

                            //======== dataTableSplitPane ========
                            {
                                dataTableSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
                                dataTableSplitPane.setBorder(null);

                                //======== dataTableScrollPane ========
                                {

                                    //---- dataTable ----
                                    dataTable.setFont(UIManager.getFont("Table.font"));
                                    dataTable.getTableHeader()
                                        .setFont(UIManager.getFont("TableHeader.font"));
                                    dataTableScrollPane.setViewportView(dataTable);
                                }
                                dataTableSplitPane.setTopComponent(dataTableScrollPane);

                                //======== imagePreviewScrollPane ========
                                {
                                    imagePreviewScrollPane.setViewportBorder(null);
                                    imagePreviewScrollPane.setViewportView(imagePreviewLabel);
                                }
                                dataTableSplitPane.setBottomComponent(imagePreviewScrollPane);
                            }
                            dataTablePanel.add(dataTableSplitPane,
                                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 5, 0), 0, 0));

                            //======== panel1 ========
                            {
                                panel1.setOpaque(false);
                                panel1.setLayout(new GridBagLayout());
                                ((GridBagLayout) panel1.getLayout()).columnWidths =
                                    new int[] {0, 0, 0, 0, 0, 0};
                                ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0};
                                ((GridBagLayout) panel1.getLayout()).columnWeights =
                                    new double[] {1.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                                ((GridBagLayout) panel1.getLayout()).rowWeights =
                                    new double[] {0.0, 1.0E-4};

                                //---- zoomBox ----
                                zoomBox.setModel(new DefaultComboBoxModel(
                                    new String[] {"25%", "50%", "100%", "200%", "400%", "800%"}));
                                zoomBox.setSelectedIndex(2);
                                zoomBox.setFont(UIManager.getFont("ComboBox.font"));
                                zoomBox.addItemListener(new ItemListener() {
                                    @Override public void itemStateChanged(ItemEvent e) {
                                        zoomBoxItemStateChanged(e);
                                    }
                                });
                                panel1.add(zoomBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));

                                //---- zoomInLabel ----
                                zoomInLabel.setIcon(new ImageIcon(getClass().getResource(
                                    "/com/ebstrada/formreturn/manager/ui/icons/preview/zoom_in.png")));
                                zoomInLabel.addMouseListener(new MouseAdapter() {
                                    @Override public void mouseClicked(MouseEvent e) {
                                        zoomInLabelMouseClicked(e);
                                    }
                                });
                                zoomInLabel
                                    .setToolTipText(Localizer.localize("UI", "ZoomInToolTip"));
                                panel1.add(zoomInLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));

                                //---- zoomOutLabel ----
                                zoomOutLabel.setIcon(new ImageIcon(getClass().getResource(
                                    "/com/ebstrada/formreturn/manager/ui/icons/preview/zoom_out.png")));
                                zoomOutLabel.setFont(UIManager.getFont("Label.font"));
                                zoomOutLabel.addMouseListener(new MouseAdapter() {
                                    @Override public void mouseClicked(MouseEvent e) {
                                        zoomOutLabelMouseClicked(e);
                                    }
                                });
                                zoomOutLabel
                                    .setToolTipText(Localizer.localize("UI", "ZoomOutToolTip"));
                                panel1.add(zoomOutLabel,
                                    new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                        new Insets(0, 0, 0, 5), 0, 0));
                            }
                            dataTablePanel.add(panel1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        fieldDataSplitPane.setRightComponent(dataTablePanel);
                    }
                    fieldTablePanel.add(fieldDataSplitPane,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(fieldTablePanel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                //======== bottomPanel ========
                {
                    bottomPanel.setOpaque(false);
                    bottomPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) bottomPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
                    ((GridBagLayout) bottomPanel.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout) bottomPanel.getLayout()).columnWeights =
                        new double[] {1.0, 0.0, 1.0E-4};
                    ((GridBagLayout) bottomPanel.getLayout()).rowWeights =
                        new double[] {0.0, 1.0E-4};

                    //======== recognitionSettingsPanel ========
                    {
                        recognitionSettingsPanel.setOpaque(false);
                        recognitionSettingsPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout) recognitionSettingsPanel.getLayout()).columnWidths =
                            new int[] {0, 0, 15, 0, 0, 15, 0, 0, 0};
                        ((GridBagLayout) recognitionSettingsPanel.getLayout()).rowHeights =
                            new int[] {0, 0};
                        ((GridBagLayout) recognitionSettingsPanel.getLayout()).columnWeights =
                            new double[] {0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout) recognitionSettingsPanel.getLayout()).rowWeights =
                            new double[] {0.0, 1.0E-4};
                        recognitionSettingsPanel.setBorder(new CompoundBorder(new TitledBorder(
                            Localizer
                                .localize("UI", "ReprocessorFrameRecognitionSettingsPanelTitle")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //---- luminanceLabel ----
                        luminanceLabel.setFont(UIManager.getFont("Label.font"));
                        luminanceLabel
                            .setText(Localizer.localize("UI", "ReprocessorFrameLuminanceLabel"));
                        recognitionSettingsPanel.add(luminanceLabel,
                            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                        //---- luminanceSpinner ----
                        luminanceSpinner.setFont(UIManager.getFont("Spinner.font"));
                        recognitionSettingsPanel.add(luminanceSpinner,
                            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                        //---- markThresholdLabel ----
                        markThresholdLabel.setFont(UIManager.getFont("Label.font"));
                        markThresholdLabel.setText(
                            Localizer.localize("UI", "ReprocessorFrameMarkThresholdLabel"));
                        recognitionSettingsPanel.add(markThresholdLabel,
                            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                        //---- markThresholdSpinner ----
                        markThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
                        recognitionSettingsPanel.add(markThresholdSpinner,
                            new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                        //---- fragmentPaddingLabel ----
                        fragmentPaddingLabel.setFont(UIManager.getFont("Label.font"));
                        fragmentPaddingLabel.setText(
                            Localizer.localize("UI", "ReprocessorFrameFragmentPaddingLabel"));
                        recognitionSettingsPanel.add(fragmentPaddingLabel,
                            new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                        //---- fragmentPaddingSpinner ----
                        fragmentPaddingSpinner.setFont(UIManager.getFont("Spinner.font"));
                        recognitionSettingsPanel.add(fragmentPaddingSpinner,
                            new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                    }
                    bottomPanel.add(recognitionSettingsPanel,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                    //======== segmentNumberPanel ========
                    {
                        segmentNumberPanel.setOpaque(false);
                        segmentNumberPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout) segmentNumberPanel.getLayout()).columnWidths =
                            new int[] {0, 0};
                        ((GridBagLayout) segmentNumberPanel.getLayout()).rowHeights =
                            new int[] {0, 0};
                        ((GridBagLayout) segmentNumberPanel.getLayout()).columnWeights =
                            new double[] {0.0, 1.0E-4};
                        ((GridBagLayout) segmentNumberPanel.getLayout()).rowWeights =
                            new double[] {0.0, 1.0E-4};
                        segmentNumberPanel.setBorder(new CompoundBorder(new TitledBorder(
                            Localizer.localize("UI", "ReprocessorFrameSegmentNumberPanelTitle")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //---- segmentNumberComboBox ----
                        segmentNumberComboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxx");
                        segmentNumberComboBox.setFont(UIManager.getFont("ComboBox.font"));
                        segmentNumberComboBox.addActionListener(new ActionListener() {
                            @Override public void actionPerformed(ActionEvent e) {
                                segmentNumberComboBoxActionPerformed(e);
                            }
                        });
                        segmentNumberPanel.add(segmentNumberComboBox,
                            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                    }
                    bottomPanel.add(segmentNumberPanel,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(bottomPanel,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setOpaque(false);
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 0, 85, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights =
                    new double[] {0.0, 1.0, 0.0, 0.0};

                //---- detectDataButton ----
                detectDataButton.setFont(UIManager.getFont("Button.font"));
                detectDataButton.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/preview/zoom.png")));
                detectDataButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        detectDataButtonActionPerformed(e);
                    }
                });
                detectDataButton
                    .setText(Localizer.localize("UI", "ReprocessorFrameDetectDataButtonText"));
                buttonBar.add(detectDataButton,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- selectSegmentButton ----
                selectSegmentButton.setFont(UIManager.getFont("Button.font"));
                selectSegmentButton.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/accept.png")));
                selectSegmentButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        selectSegmentButtonActionPerformed(e);
                    }
                });
                selectSegmentButton
                    .setText(Localizer.localize("UI", "RecordEditorUpdateButtonText"));
                buttonBar.add(selectSegmentButton,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- closeButton ----
                closeButton.setFont(UIManager.getFont("Button.font"));
                closeButton.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
                closeButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        closeButtonActionPerformed(e);
                    }
                });
                closeButton.setText(Localizer.localize("UI", "CancelButtonText"));
                buttonBar.add(closeButton,
                    new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(990, 545);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel fieldTablePanel;
    private JSplitPane fieldDataSplitPane;
    private JPanel fieldNamesPanel;
    private JScrollPane fieldTableScrollPane;
    private JTable fieldTable;
    private JPanel dataTablePanel;
    private JSplitPane dataTableSplitPane;
    private JScrollPane dataTableScrollPane;
    private JTable dataTable;
    private JScrollPane imagePreviewScrollPane;
    private ImagePreviewLabel imagePreviewLabel;
    private JPanel panel1;
    private JComboBox zoomBox;
    private JLabel zoomInLabel;
    private JLabel zoomOutLabel;
    private JPanel bottomPanel;
    private JPanel recognitionSettingsPanel;
    private JLabel luminanceLabel;
    private JSpinner luminanceSpinner;
    private JLabel markThresholdLabel;
    private JSpinner markThresholdSpinner;
    private JLabel fragmentPaddingLabel;
    private JSpinner fragmentPaddingSpinner;
    private JPanel segmentNumberPanel;
    private JComboBox segmentNumberComboBox;
    private JPanel buttonBar;
    private JButton detectDataButton;
    private JButton selectSegmentButton;
    private JButton closeButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public void renderFragmentImage(Graphics2D g) {

        g.setColor(Color.darkGray);
        g.fill(new Rectangle2D.Double(0, 0, imagePreviewLabel.getWidth(),
            imagePreviewLabel.getHeight()));
        if (fragmentImage != null) {
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

            double x =
                (imagePreviewLabel.getWidth() / 2) - ((fragmentImage.getWidth() * realZoom) / 2);
            double y =
                (imagePreviewLabel.getHeight() / 2) - ((fragmentImage.getHeight() * realZoom) / 2);
            AffineTransform at = AffineTransform.getTranslateInstance(x, y);
            at.scale(realZoom, realZoom);
            g.drawRenderedImage(fragmentImage, at);
        }
    }

    public void setSelectedRecognitionStructure(final FragmentRecognitionStructure frs) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (int i = 0; i < fieldTable.getRowCount(); i++) {
                    String fieldName = (String) fieldTable.getValueAt(i, 0);
                    if (fieldName.equals(frs.getFieldName())) {
                        fieldTable.setRowSelectionAllowed(true);
                        fieldTable.setRowSelectionInterval(i, i);
                        updateDataTable();
                        return;
                    }
                }
            }
        });
    }
}
