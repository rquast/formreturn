package com.ebstrada.formreturn.manager.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.ebstrada.aggregation.exception.ErrorFlagException;
import com.ebstrada.aggregation.exception.InvalidRulePartException;
import com.ebstrada.aggregation.exception.NoMatchException;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.reader.FormReaderException;
import com.ebstrada.formreturn.manager.logic.recognition.structure.FragmentRecognitionData;
import com.ebstrada.formreturn.manager.logic.recognition.structure.OMRBox;
import com.ebstrada.formreturn.manager.logic.recognition.structure.OMRMatrix;
import com.ebstrada.formreturn.manager.logic.recognition.structure.OMRRecognitionStructure;
import com.ebstrada.formreturn.manager.persistence.jpa.Form;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentOmr;
import com.ebstrada.formreturn.manager.persistence.jpa.Publication;
import com.ebstrada.formreturn.manager.persistence.jpa.Segment;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.cdm.dialog.PublicationSettingsDialog;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.ui.component.JImageLabel;
import com.ebstrada.formreturn.manager.util.Misc;

public class MarkDetectionDetailsDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private FragmentRecognitionData fragmentRecognitionData;

    private Float realZoom = 1.0f;

    private BufferedImage silhouettesImage;

    private long publicationId;

    private long formId;

    private Vector<Integer> selectedIndexes = new Vector<Integer>();

    public MarkDetectionDetailsDialog(Object owner) {
        super((JFrame) owner);
        initComponents();
        scrollPane1.getVerticalScrollBar().setUnitIncrement(30);
        scrollPane1.getVerticalScrollBar().setBlockIncrement(90);
        scrollPane1.getHorizontalScrollBar().setUnitIncrement(30);
        scrollPane1.getHorizontalScrollBar().setBlockIncrement(90);
        getRootPane().setDefaultButton(closeButton);
    }

    private void setTableModel() {

        if (Main.getInstance().isDebugMode()) {
            BlobExtractionDebugDialog bed = new BlobExtractionDebugDialog(Main.getInstance(),
                fragmentRecognitionData.getOmrMatrix());
            bed.setVisible(true);
        }

        OMRMatrix omrMatrix = fragmentRecognitionData.getOmrMatrix();
        OMRBox[][] omrBoxMatrix = omrMatrix.getOmrBoxMatrix();

        int rowCount = omrMatrix.getOmrrs().getNumberOfRows();
        int columnCount = omrMatrix.getOmrrs().getNumberOfColumns();

        silhouettesImage = omrMatrix.getBlobExtractionImage(null, true);

        String[] columns = new String[] {Localizer.localize("UI", "SilhouettesTableShowColumnText"),
            Localizer.localize("UI", "SilhouettesTableSilhouetteNumberColumnText"),
            Localizer.localize("UI", "SilhouettesTableNumberOfPixelsColumnText"),
            Localizer.localize("UI", "SilhouettesTableNumberOfEnclosedPixelsColumnText"),
            Localizer.localize("UI", "SilhouettesTableNumberOfWhitePixelsColumnText"),
            Localizer.localize("UI", "SilhouettesTableMarkThresholdColumnText"),
            Localizer.localize("UI", "SilhouettesTableMarkValueColumnText")};
        Object[][] data = new Object[(rowCount * columnCount)][7];

        double calculatedThreshold = 0.0d;

        for (int i = 0; i < omrBoxMatrix.length; i++) {
            for (int j = 0; j < omrBoxMatrix[i].length; j++) {

                data[(i * columnCount) + j][0] = new Boolean(true);
                data[(i * columnCount) + j][1] = omrBoxMatrix[i][j].getRegionIndex() + "";
                data[(i * columnCount) + j][2] = omrBoxMatrix[i][j].getPixelCount() + "";

                double whiteCount = omrBoxMatrix[i][j].getWhiteCount();
                double numberOfPixels = omrBoxMatrix[i][j].getPixelCount();
                if (numberOfPixels > 0) {
                    calculatedThreshold = ((numberOfPixels / whiteCount) * 100.0d) - 100.0d;
                }

                data[(i * columnCount) + j][3] = omrBoxMatrix[i][j].getEnclosedPixelCount() + "";
                data[(i * columnCount) + j][4] = omrBoxMatrix[i][j].getWhiteCount() + "";


                if (numberOfPixels > 0) {
                    data[(i * columnCount) + j][5] = Math.round(calculatedThreshold) + "%";
                } else {
                    data[(i * columnCount) + j][5] =
                        Localizer.localize("UI", "SilhouettesTableNotApplicable");
                }



                data[(i * columnCount) + j][6] = omrBoxMatrix[i][j].getValue();
                selectedIndexes.add(new Integer(omrBoxMatrix[i][j].getRegionIndex()));

            }
        }

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            public Class<?> getColumnClass(int columnIndex) {
                Object o = getValueAt(0, columnIndex);
                if (o != null)
                    return o.getClass();
                return super.getColumnClass(columnIndex);
            }
        };

        silhouettesTable.setCellSelectionEnabled(true);
        silhouettesTable.setModel(model);

        BooleanEditor bEditor = new BooleanEditor();
        silhouettesTable.setDefaultEditor(Boolean.class, bEditor);
        bEditor.addCellEditorListener(new CheckBoxListener(model));

        TableColumn col;
        col = silhouettesTable.getColumnModel().getColumn(0);
        col.setPreferredWidth(40);
        col = silhouettesTable.getColumnModel().getColumn(1);
        col.setPreferredWidth(40);

    }


    private class CheckBoxListener implements CellEditorListener {
        TableModel model;

        public CheckBoxListener(TableModel model) {
            this.model = model;
        }

        public void editingCanceled(ChangeEvent e) {
        }

        public void editingStopped(ChangeEvent e) {
            try {
                updateSelectedIndexes(model);
            } catch (FormReaderException e1) {
                selectedIndexes.removeAllElements();
                Misc.showErrorMsg(Main.getInstance(), e1.getMessage());
            }
        }
    }

    private void updateSelectedIndexes(TableModel model) throws FormReaderException {
        selectedIndexes.removeAllElements();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (((Boolean) model.getValueAt(i, 0)).booleanValue() == true) {
                Integer selectedIndex = Integer.parseInt((String) model.getValueAt(i, 1));
                selectedIndexes.add(selectedIndex);
            }
        }
        updateSilhouetteImage();
    }

    private static class BooleanEditor extends DefaultCellEditor {
        int lastRow = -1;

        public BooleanEditor() {
            super(new JCheckBox());
            JCheckBox checkBox = (JCheckBox) getComponent();
            checkBox.setHorizontalAlignment(JCheckBox.CENTER);
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
            int row, int column) {
            lastRow = row;
            return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        }

        public int getLastRow() {
            return lastRow;
        }
    }

    public void updateSilhouetteImage() {
        OMRMatrix omrMatrix = fragmentRecognitionData.getOmrMatrix();
        silhouettesImage = omrMatrix.getBlobExtractionImage(selectedIndexes, false);
        repaint();
    }


    public void renderMarkAreaImage(Graphics2D g) {

        if (silhouettesImage != null) {
            g.setColor(Color.darkGray);
            g.fill(new Rectangle2D.Double(0, 0, markAreaImageLabel.getWidth(),
                markAreaImageLabel.getHeight()));

            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

            double x =
                (markAreaImageLabel.getWidth() / 2) - ((silhouettesImage.getWidth() * realZoom)
                    / 2);
            double y =
                (markAreaImageLabel.getHeight() / 2) - ((silhouettesImage.getHeight() * realZoom)
                    / 2);
            AffineTransform at = AffineTransform.getTranslateInstance(x, y);
            at.scale(realZoom, realZoom);
            g.drawRenderedImage(silhouettesImage, at);
        }

    }

    public void setFragmentRecognitionData(FragmentRecognitionData fragmentRecognitionData) {

        this.fragmentRecognitionData = fragmentRecognitionData;

        OMRMatrix omrMatrix = fragmentRecognitionData.getOmrMatrix();

        String aggregateRule = omrMatrix.getOmrrs().getAggregationRule();
        OMRRecognitionStructure omrrs = omrMatrix.getOmrrs();
        capturedDataNameTextField.setText(omrrs.getFieldName());
        markThresholdTextField.setText((int) ((omrMatrix.getMarkThreshold() * 100) - 100) + "%");

        NumberFormat formatter = new DecimalFormat("#0.00");

        x1y1TextField.setText(String.format(Localizer.localize("UI", "DetectionX1Y1TextFieldValue"),
            formatter.format(omrrs.getPercentX1()), formatter.format(omrrs.getPercentY1())));
        x2y2TextField.setText(String.format(Localizer.localize("UI", "DetectionX2Y2TextFieldValue"),
            formatter.format(omrrs.getPercentX2()), formatter.format(omrrs.getPercentY2())));

        int rowCount = omrrs.getNumberOfRows();
        int columnCount = omrrs.getNumberOfColumns();

        rowsColumnsTextField.setText(String
            .format(Localizer.localize("UI", "DetectionRowsColumnsTextFieldValue"), rowCount,
                columnCount));

        String[] capturedData = new String[omrMatrix.getCapturedData().size()];
        int i = 0;
        for (String value : omrMatrix.getCapturedData()) {
            capturedData[i] = value;
            i++;
        }

        double mark = 0.0d;

        try {
            if (omrrs.isReconciliationKey() || omrrs.isCombineColumnCharacters()) {
                String capturedString = omrMatrix.getCapturedString();
                mark = Misc.aggregate(0, new String[] {capturedString}, aggregateRule);
            } else {
                mark = Misc.aggregate(0, capturedData, aggregateRule);
            }
            this.calculatedScoreTextField.setText(mark + "");
        } catch (InvalidRulePartException e) {
            this.calculatedScoreTextField
                .setText(Localizer.localize("UI", "InvalidAggreationRuleMessageText"));
        } catch (NoMatchException e) {
            mark = 0.0d;
        } catch (ErrorFlagException e) {
            this.calculatedScoreTextField
                .setText(Localizer.localize("UI", "ErrorFlagAggregationRuleMessageText"));
        }

        if (omrrs.isReconciliationKey() || omrrs.isCombineColumnCharacters()) {
            String capturedString = omrMatrix.getCapturedString();
            if (capturedString.trim().length() > 0) {
                capturedDataTextField.setText(capturedString);
            } else {
                capturedDataTextField
                    .setText(Localizer.localize("UI", "DetectionNoMarkFoundMessage"));
            }
        } else {
            if (capturedData.length > 0) {
                capturedDataTextField.setText(Misc.implode(capturedData, ", "));
            } else {
                capturedDataTextField
                    .setText(Localizer.localize("UI", "DetectionNoMarkFoundMessage"));
            }
        }


        aggregateRuleTextField.setText(aggregateRule);
        setTableModel();

        resizeImageLabel();
        repaint();

    }

    public void resizeImageLabel() {
        BufferedImage fragmentImage = fragmentRecognitionData.getFragmentImage();

        Dimension dim = new Dimension((int) (fragmentImage.getWidth() * realZoom),
            (int) (fragmentImage.getHeight() * realZoom));
        if (markAreaImageLabel != null) {
            markAreaImageLabel.setMaximumSize(dim);
            markAreaImageLabel.setMinimumSize(dim);
            markAreaImageLabel.setPreferredSize(dim);
        }
        markAreaImageLabel.revalidate();
    }

    private void closeButtonActionPerformed(ActionEvent e) {
        dispose();
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

    private void modifyRuleButtonActionPerformed(ActionEvent e) {

        if (formId > 0) {

            String fieldname = fragmentRecognitionData.getOmrMatrix().getOmrrs().getFieldName();
            String aggregationRule =
                fragmentRecognitionData.getOmrMatrix().getOmrrs().getAggregationRule();

            ModifyAggregationRuleDialog mard = new ModifyAggregationRuleDialog(Main.getInstance());
            mard.setAggregationRule(aggregationRule);
            mard.restoreSettings();
            mard.setModal(true);
            mard.setVisible(true);

            if (mard.getDialogResult() == JOptionPane.OK_OPTION) {

                // save rule for form only
                if (mard.getSaveType() == ModifyAggregationRuleDialog.SAVE_FORM_ONLY) {

                    // find form object
                    EntityManager entityManager =
                        Main.getInstance().getJPAConfiguration().getEntityManager();

                    try {

                        entityManager.getTransaction().begin();
                        entityManager.flush();
                        Form form = entityManager.find(Form.class, formId);

                        for (FormPage formPage : form.getFormPageCollection()) {
                            for (Segment segment : formPage.getSegmentCollection()) {
                                for (FragmentOmr fomr : segment.getFragmentOmrCollection()) {
                                    if (fomr.getCapturedDataFieldName().trim().equals(fieldname)) {
                                        fomr.setAggregationRule(mard.getAggregationRule());
                                        entityManager.persist(fomr);
                                    }
                                }
                            }
                        }

                        entityManager.getTransaction().commit();
                        Misc.showSuccessMsg(Main.getInstance(),
                            Localizer.localize("UI", "AggregationRuleUpdateSuccessMessage"));

                    } catch (Exception ex) {
                        if (entityManager.getTransaction().isActive()) {
                            try {
                                entityManager.getTransaction().rollback();
                            } catch (Exception ex2) {
                            }
                        }
                        Misc.showErrorMsg(Main.getInstance(),
                            Localizer.localize("UI", "AggregationRuleUpdateFailureMessage"));
                    }

                    entityManager.close();

                } else {
                    // else save rule for whole publication
                    Publication publication = null;
                    EntityManager entityManager =
                        Main.getInstance().getJPAConfiguration().getEntityManager();

                    try {

                        entityManager.getTransaction().begin();
                        entityManager.flush();

                        // find publication object
                        if (publicationId > 0) {
                            publication = entityManager.find(Publication.class, publicationId);
                        } else {
                            Form form = entityManager.find(Form.class, formId);
                            publication = form.getPublicationId();
                        }

                        for (Form form : publication.getFormCollection()) {
                            for (FormPage formPage : form.getFormPageCollection()) {
                                for (Segment segment : formPage.getSegmentCollection()) {
                                    for (FragmentOmr fomr : segment.getFragmentOmrCollection()) {
                                        if (fomr.getCapturedDataFieldName().trim()
                                            .equals(fieldname)) {
                                            fomr.setAggregationRule(mard.getAggregationRule());
                                            entityManager.persist(fomr);
                                        }
                                    }
                                }
                            }
                        }

                        entityManager.getTransaction().commit();
                        Misc.showSuccessMsg(Main.getInstance(),
                            Localizer.localize("UI", "AggregationRuleUpdateSuccessMessage"));

                    } catch (Exception ex) {
                        if (entityManager.getTransaction().isActive()) {
                            try {
                                entityManager.getTransaction().rollback();
                            } catch (Exception ex2) {
                            }
                        }
                        Misc.showErrorMsg(Main.getInstance(),
                            Localizer.localize("UI", "AggregationRuleUpdateFailureMessage"));
                    }

                    entityManager.close();

                }


            }

        }

    }

    private void modifyPublicationButtonActionPerformed(ActionEvent e) {

        if (publicationId > 0) {

            PublicationSettingsDialog psd = new PublicationSettingsDialog(Main.getInstance());
            long[] publicationIds = new long[1];
            publicationIds[0] = publicationId;
            psd.setPublicationIds(publicationIds);
            psd.restoreSettings();
            psd.setModal(true);
            psd.setVisible(true);

        }

    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                closeButton.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        panel2 = new JPanel();
        markAreaPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        markAreaImageLabel = new JImageLabel();
        panel5 = new JPanel();
        zoomBox = new JComboBox();
        zoomInLabel = new JLabel();
        zoomOutLabel = new JLabel();
        silhouettesPanel = new JPanel();
        scrollPane3 = new JScrollPane();
        silhouettesTable = new JTable();
        markScorePanel = new JPanel();
        markThresholdLabel = new JLabel();
        markThresholdTextField = new JTextField();
        fieldNameLabel = new JLabel();
        capturedDataNameTextField = new JTextField();
        detectedMarksLabel = new JLabel();
        capturedDataTextField = new JTextField();
        x1y1label = new JLabel();
        x1y1TextField = new JTextField();
        aggregateRuleLabel = new JLabel();
        aggregateRuleTextField = new JTextField();
        x2y2label = new JLabel();
        x2y2TextField = new JTextField();
        calculatedScoreLabel = new JLabel();
        calculatedScoreTextField = new JTextField();
        rowsColumnsLabel = new JLabel();
        rowsColumnsTextField = new JTextField();
        modifyPanel = new JPanel();
        modifyRuleButton = new JButton();
        modifyPublicationButton = new JButton();
        buttonBar = new JPanel();
        helpLabel = new JHelpLabel();
        closeButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UI", "MarkDetectionDetailsDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {1.0, 0.0, 0.0, 1.0E-4};

                //======== panel2 ========
                {
                    panel2.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 320, 0};
                    ((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                    ((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                    //======== markAreaPanel ========
                    {
                        markAreaPanel.setFont(UIManager.getFont("TitledBorder.font"));
                        markAreaPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)markAreaPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)markAreaPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                        ((GridBagLayout)markAreaPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)markAreaPanel.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};
                        markAreaPanel.setBorder(new CompoundBorder(
                            new TitledBorder(Localizer.localize("UI", "MarkAreaBorderTitle")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //======== scrollPane1 ========
                        {
                            scrollPane1.setBorder(null);
                            scrollPane1.setViewportBorder(null);

                            //---- markAreaImageLabel ----
                            markAreaImageLabel.setMddd(this);
                            markAreaImageLabel.setRenderType(JImageLabel.RENDER_MARK_AREA_IMAGE);
                            scrollPane1.setViewportView(markAreaImageLabel);
                        }
                        markAreaPanel.add(scrollPane1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== panel5 ========
                        {
                            panel5.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel5.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0};
                            ((GridBagLayout)panel5.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)panel5.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                            ((GridBagLayout)panel5.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                            //---- zoomBox ----
                            zoomBox.setModel(new DefaultComboBoxModel(new String[] {
                                "25%",
                                "50%",
                                "100%",
                                "200%",
                                "400%",
                                "800%"
                            }));
                            zoomBox.setSelectedIndex(2);
                            zoomBox.setFont(UIManager.getFont("ComboBox.font"));
                            zoomBox.addItemListener(new ItemListener() {
                                @Override
                                public void itemStateChanged(ItemEvent e) {
                                    zoomBoxItemStateChanged(e);
                                }
                            });
                            panel5.add(zoomBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- zoomInLabel ----
                            zoomInLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/preview/zoom_in.png")));
                            zoomInLabel.setFont(UIManager.getFont("Label.font"));
                            zoomInLabel.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    zoomInLabelMouseClicked(e);
                                }
                            });
                            zoomInLabel.setToolTipText(Localizer.localize("UI", "ZoomInToolTip"));
                            panel5.add(zoomInLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
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
                            panel5.add(zoomOutLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));
                        }
                        markAreaPanel.add(panel5, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    panel2.add(markAreaPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //======== silhouettesPanel ========
                    {
                        silhouettesPanel.setFont(UIManager.getFont("TitledBorder.font"));
                        silhouettesPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)silhouettesPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)silhouettesPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)silhouettesPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)silhouettesPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                        silhouettesPanel.setBorder(new CompoundBorder(
                            new TitledBorder(Localizer.localize("UI", "SilhouettesBorderTitle")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //======== scrollPane3 ========
                        {
                            scrollPane3.setBorder(null);

                            //---- silhouettesTable ----
                            silhouettesTable.setShowHorizontalLines(false);
                            silhouettesTable.setShowVerticalLines(false);
                            silhouettesTable.setFont(UIManager.getFont("Table.font"));
                            silhouettesTable.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));
                            scrollPane3.setViewportView(silhouettesTable);
                        }
                        silhouettesPanel.add(scrollPane3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    panel2.add(silhouettesPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(panel2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== markScorePanel ========
                {
                    markScorePanel.setFont(UIManager.getFont("TitledBorder.font"));
                    markScorePanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)markScorePanel.getLayout()).columnWidths = new int[] {0, 205, 15, 0, 200, 0};
                    ((GridBagLayout)markScorePanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
                    ((GridBagLayout)markScorePanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 1.0, 1.0E-4};
                    ((GridBagLayout)markScorePanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
                    markScorePanel.setBorder(new CompoundBorder(
                        new TitledBorder(Localizer.localize("UI", "MarkScoreCalculationBorderTitle")),
                        new EmptyBorder(5, 5, 5, 5)));

                    //---- markThresholdLabel ----
                    markThresholdLabel.setFont(UIManager.getFont("Label.font"));
                    markThresholdLabel.setText(Localizer.localize("UI", "MarkDetectionThresholdLabel"));
                    markScorePanel.add(markThresholdLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- markThresholdTextField ----
                    markThresholdTextField.setEditable(false);
                    markScorePanel.add(markThresholdTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- fieldNameLabel ----
                    fieldNameLabel.setFont(UIManager.getFont("Label.font"));
                    fieldNameLabel.setText(Localizer.localize("UI", "DetectionFieldNameLabel"));
                    markScorePanel.add(fieldNameLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- capturedDataNameTextField ----
                    capturedDataNameTextField.setEditable(false);
                    markScorePanel.add(capturedDataNameTextField, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- detectedMarksLabel ----
                    detectedMarksLabel.setFont(UIManager.getFont("Label.font"));
                    detectedMarksLabel.setText(Localizer.localize("UI", "DetectedMarksLabel"));
                    markScorePanel.add(detectedMarksLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- capturedDataTextField ----
                    capturedDataTextField.setEditable(false);
                    markScorePanel.add(capturedDataTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- x1y1label ----
                    x1y1label.setFont(UIManager.getFont("Label.font"));
                    x1y1label.setText(Localizer.localize("UI", "DetectionX1Y1Label"));
                    markScorePanel.add(x1y1label, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- x1y1TextField ----
                    x1y1TextField.setEditable(false);
                    markScorePanel.add(x1y1TextField, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- aggregateRuleLabel ----
                    aggregateRuleLabel.setFont(UIManager.getFont("Label.font"));
                    aggregateRuleLabel.setText(Localizer.localize("UI", "DetectionAggregateRuleLabel"));
                    markScorePanel.add(aggregateRuleLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- aggregateRuleTextField ----
                    aggregateRuleTextField.setEditable(false);
                    markScorePanel.add(aggregateRuleTextField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- x2y2label ----
                    x2y2label.setFont(UIManager.getFont("Label.font"));
                    x2y2label.setText(Localizer.localize("UI", "DetectionX2Y2Label"));
                    markScorePanel.add(x2y2label, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- x2y2TextField ----
                    x2y2TextField.setEditable(false);
                    markScorePanel.add(x2y2TextField, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- calculatedScoreLabel ----
                    calculatedScoreLabel.setFont(UIManager.getFont("Label.font"));
                    calculatedScoreLabel.setText(Localizer.localize("UI", "DetectionCalculatedScoreLabel"));
                    markScorePanel.add(calculatedScoreLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- calculatedScoreTextField ----
                    calculatedScoreTextField.setEditable(false);
                    markScorePanel.add(calculatedScoreTextField, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- rowsColumnsLabel ----
                    rowsColumnsLabel.setFont(UIManager.getFont("Label.font"));
                    rowsColumnsLabel.setText(Localizer.localize("UI", "DetectionRowsColumnsLabel"));
                    markScorePanel.add(rowsColumnsLabel, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- rowsColumnsTextField ----
                    rowsColumnsTextField.setEditable(false);
                    markScorePanel.add(rowsColumnsTextField, new GridBagConstraints(4, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(markScorePanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== modifyPanel ========
                {
                    modifyPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)modifyPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                    ((GridBagLayout)modifyPanel.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)modifyPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                    ((GridBagLayout)modifyPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                    //---- modifyRuleButton ----
                    modifyRuleButton.setFont(UIManager.getFont("Button.font"));
                    modifyRuleButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            modifyRuleButtonActionPerformed(e);
                        }
                    });
                    modifyRuleButton.setText(Localizer.localize("UI", "ModifyRuleButtonText"));
                    modifyPanel.add(modifyRuleButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- modifyPublicationButton ----
                    modifyPublicationButton.setFont(UIManager.getFont("Button.font"));
                    modifyPublicationButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            modifyPublicationButtonActionPerformed(e);
                        }
                    });
                    modifyPublicationButton.setText(Localizer.localize("UI", "ModifyPublicationButtonText"));
                    modifyPanel.add(modifyPublicationButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));
                }
                contentPanel.add(modifyPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 0, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0};

                //---- helpLabel ----
                helpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                helpLabel.setHelpGUID("mark-detection-details");
                helpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                buttonBar.add(helpLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- closeButton ----
                closeButton.setFont(UIManager.getFont("Button.font"));
                closeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        closeButtonActionPerformed(e);
                    }
                });
                closeButton.setText(Localizer.localize("UI", "CloseButtonText"));
                buttonBar.add(closeButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(725, 545);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel panel2;
    private JPanel markAreaPanel;
    private JScrollPane scrollPane1;
    private JImageLabel markAreaImageLabel;
    private JPanel panel5;
    private JComboBox zoomBox;
    private JLabel zoomInLabel;
    private JLabel zoomOutLabel;
    private JPanel silhouettesPanel;
    private JScrollPane scrollPane3;
    private JTable silhouettesTable;
    private JPanel markScorePanel;
    private JLabel markThresholdLabel;
    private JTextField markThresholdTextField;
    private JLabel fieldNameLabel;
    private JTextField capturedDataNameTextField;
    private JLabel detectedMarksLabel;
    private JTextField capturedDataTextField;
    private JLabel x1y1label;
    private JTextField x1y1TextField;
    private JLabel aggregateRuleLabel;
    private JTextField aggregateRuleTextField;
    private JLabel x2y2label;
    private JTextField x2y2TextField;
    private JLabel calculatedScoreLabel;
    private JTextField calculatedScoreTextField;
    private JLabel rowsColumnsLabel;
    private JTextField rowsColumnsTextField;
    private JPanel modifyPanel;
    private JButton modifyRuleButton;
    private JButton modifyPublicationButton;
    private JPanel buttonBar;
    private JHelpLabel helpLabel;
    private JButton closeButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public long getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(long publicationId) {
        this.publicationId = publicationId;
    }

    public long getFormId() {
        return formId;
    }

    public void setFormId(long formId) {
        this.formId = formId;
    }

    public void removeModify() {
        contentPanel.remove(modifyPanel);
    }

}
