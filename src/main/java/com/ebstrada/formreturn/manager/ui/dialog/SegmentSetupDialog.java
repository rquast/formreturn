package com.ebstrada.formreturn.manager.ui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.ui.DocumentAttributes;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.ui.editor.persistence.FieldnameDuplicatePresets;
import com.ebstrada.formreturn.manager.util.Measurement;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.graph.GraphUtils;
import com.ebstrada.formreturn.manager.util.graph.SizeAttributes;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class SegmentSetupDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

    private int dialogResult = javax.swing.JOptionPane.CANCEL_OPTION;

    private PageAttributes pageAttributes;

    private DocumentAttributes documentAttributes;

    private Main mainInstance;

    private Measurement measurement;

    private int currentUnit = Measurement.PIXELS;

    public SegmentSetupDialog(Frame parent, boolean modal, String title) {
        super(parent, modal);
        setTitle(title);
        measurement = new Measurement();
        measurement.setFrom(Measurement.PIXELS);
        measurement.setTo(Measurement.PIXELS);
        mainInstance = Main.getInstance();
        pageAttributes = new PageAttributes();
        initComponents();
        setReportName(mainInstance.getFirstFreeSegmentName());
        setSegmentNames();
        setDefaultSize();
        restoreSettings();
        getRootPane().setDefaultButton(okButton);
    }

    private void setSegmentNames() {
        List<String> segmentSizeNames = PreferencesManager.getSegmentSizeNames();

        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        for (String segmentSizeName : segmentSizeNames) {
            dcbm.addElement(segmentSizeName);
        }

        presetSizeComboBox.setModel(dcbm);
    }

    private void restoreSettings() {
        FieldnameDuplicatePresets fdp = PreferencesManager.getFieldnameDupliatePresets();
        defaultFieldnamePrefixTextField.setText(fdp.getFieldname());
        fieldnameCounterSpinner.setValue(fdp.getCounterStart());

        double defaultSegmentBarcodeSize = PreferencesManager.getDefaultSegmentBarcodeSize();
        segmentBarcodeScaleSpinner.setValue(defaultSegmentBarcodeSize);

        SizeAttributes sa = PreferencesManager.getDefaultSegmentSizeAttributes();
        presetSizeComboBox.setSelectedItem(sa.getName());
        if (presetSizeComboBox.getSelectedIndex() == -1) {
            presetSizeComboBox.setSelectedIndex(0);
        }

    }

    public void setDefaultSize() {

        SizeAttributes sa = PreferencesManager.getDefaultSegmentSizeAttributes();
        if (sa.getOrientation() == SizeAttributes.PORTRAIT) {
            orientation.setSelectedItem("Portrait");
        } else {
            orientation.setSelectedItem("Landscape");
        }

        String size = (String) presetSizeComboBox.getSelectedObjects()[0];
        setPageSize(size);

    }

    public void restorePageAttributes() {

        Dimension d = pageAttributes.getDimension();
        double attrPageWidth = d.getWidth();
        double attrPageHeight = d.getHeight();

        segmentRecognitionCheckBox.setSelected(pageAttributes.hasRecognitionBarcodes());
        segmentBarcodeScaleSpinner.setValue(pageAttributes.getRecognitionBarcodesScale());

        setReportName(documentAttributes.getName());
        segmentWidth.setValue(new Double(attrPageWidth));
        segmentHeight.setValue(new Double(attrPageHeight));

    }

    public DocumentAttributes getDocumentAttributes() {
        if (documentAttributes == null) {
            documentAttributes = new DocumentAttributes();
        }

        documentAttributes.setDocumentType(DocumentAttributes.SEGMENT);
        documentAttributes.setName(getReportName());
        documentAttributes
            .setDefaultCapturedDataFieldname(defaultFieldnamePrefixTextField.getText().trim());
        documentAttributes.setDefaultCDFNIncrementor((Integer) fieldnameCounterSpinner.getValue());

        return documentAttributes;
    }

    public PageAttributes getPageAttributes() {

        if (pageAttributes == null) {
            pageAttributes = new PageAttributes();
        }

        pageAttributes.setDimension(new Dimension(getPixelSegmentWidth(), getPixelSegmentHeight()));

        pageAttributes.setPageSize((String) presetSizeComboBox.getSelectedItem());
        pageAttributes.setRecognitionBarcodes(segmentRecognitionCheckBox.isSelected());
        pageAttributes.setRecognitionBarcodesScale((Double) segmentBarcodeScaleSpinner.getValue());
        pageAttributes.setLeftMargin(0);
        pageAttributes.setRightMargin(0);
        pageAttributes.setTopMargin(0);
        pageAttributes.setBottomMargin(0);

        return pageAttributes;
    }

    private void presetSizeComboBoxActionPerformed(ActionEvent e) {
        measurementComboBox.setSelectedIndex(0);
        Object[] selectedObjects = presetSizeComboBox.getSelectedObjects();
        if (selectedObjects != null && selectedObjects.length > 0) {
            String size = (String) presetSizeComboBox.getSelectedObjects()[0];
            setPageSize(size);
        }
    }

    private void setPageSize(SizeAttributes sizeAttributes) {
        segmentWidth.setValue(new Double(sizeAttributes.getWidth()));
        segmentHeight.setValue(new Double(sizeAttributes.getHeight()));
    }

    private void setPageSize(String size) {

        if (size.equals("Custom")) {
            return;
        }

        // get portrait defaults
        setPageSize(getPageDefaultSize(size));

    }

    private void measurementComboBoxActionPerformed(ActionEvent e) {
        measurement.setFrom(currentUnit);
        measurement.setTo(getSelectedUnit());

        if (getSelectedUnit() == Measurement.PIXELS) {

            segmentWidth.setValue(
                new Double(Math.round(measurement.convert((Double) segmentWidth.getValue()))));
            segmentHeight.setValue(
                new Double(Math.round(measurement.convert((Double) segmentHeight.getValue()))));

        } else {

            segmentWidth
                .setValue(new Double(measurement.convert((Double) segmentWidth.getValue())));
            segmentHeight
                .setValue(new Double(measurement.convert((Double) segmentHeight.getValue())));

        }

        currentUnit = getSelectedUnit();
    }

    private int getSelectedUnit() {
        String selectedUnit = (String) measurementComboBox.getSelectedItem();
        if (selectedUnit.equalsIgnoreCase("Pixels")) {
            return Measurement.PIXELS;
        } else if (selectedUnit.equalsIgnoreCase("Millimeters")) {
            return Measurement.MILLIMETERS;
        } else if (selectedUnit.equalsIgnoreCase("Centimeters")) {
            return Measurement.CENTIMETERS;
        } else if (selectedUnit.equalsIgnoreCase("Inches")) {
            return Measurement.INCHES;
        } else {
            return currentUnit;
        }
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                okButton.requestFocusInWindow();
            }
        });
    }

    private void orientationItemStateChanged(ItemEvent e) {
        measurementComboBox.setSelectedIndex(0);
        String size = (String) presetSizeComboBox.getSelectedObjects()[0];
        setPageSize(size);

        if (size.equals("Portrait")) {
            pageAttributes.setOrientation(PageAttributes.PORTRAIT);
        } else if (size.equals("Landscape")) {
            pageAttributes.setOrientation(PageAttributes.LANDSCAPE);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        panel4 = new JPanel();
        nameLabel = new JLabel();
        segmentName = new JTextField();
        panel10 = new JPanel();
        presetSizeLabel = new JLabel();
        presetSizeComboBox = new JComboBox();
        measurementLabel = new JLabel();
        measurementComboBox = new JComboBox();
        orientation = new JComboBox();
        orientationLabel = new JLabel();
        panel6 = new JPanel();
        widthLabel = new JLabel();
        segmentWidth = new JSpinner();
        heightLabel = new JLabel();
        segmentHeight = new JSpinner();
        panel1 = new JPanel();
        panel3 = new JPanel();
        segmentRecognitionCheckBox = new JCheckBox();
        segmentBarcodeScaleLabel = new JLabel();
        segmentBarcodeScaleSpinner = new JSpinner();
        panel7 = new JPanel();
        panel5 = new JPanel();
        fieldnamePrefixLabel = new JLabel();
        defaultFieldnamePrefixTextField = new JTextField();
        startsAtLabel = new JLabel();
        fieldnameCounterSpinner = new JSpinner();
        buttonBar = new JPanel();
        helpLabel = new JHelpLabel();
        okButton = new JButton();
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
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
                ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0E-4};

                //======== panel4 ========
                {
                    panel4.setBorder(new EmptyBorder(0, 0, 5, 0));
                    panel4.setOpaque(false);
                    panel4.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel4.getLayout()).columnWidths = new int[] {0, 0, 0};
                    ((GridBagLayout)panel4.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)panel4.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                    ((GridBagLayout)panel4.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                    //---- nameLabel ----
                    nameLabel.setFont(UIManager.getFont("Label.font"));
                    nameLabel.setText(Localizer.localize("UI", "SegmentSetupNameLabel"));
                    panel4.add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- segmentName ----
                    segmentName.setFont(UIManager.getFont("TextField.font"));
                    panel4.add(segmentName, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(panel4, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

                //======== panel10 ========
                {
                    panel10.setOpaque(false);
                    panel10.setBorder(new CompoundBorder(
                        new MatteBorder(1, 0, 0, 0, Color.lightGray),
                        new EmptyBorder(5, 0, 5, 0)));
                    panel10.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel10.getLayout()).columnWidths = new int[] {0, 0, 15, 0, 0, 0};
                    ((GridBagLayout)panel10.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout)panel10.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout)panel10.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

                    //---- presetSizeLabel ----
                    presetSizeLabel.setFont(UIManager.getFont("Label.font"));
                    presetSizeLabel.setText(Localizer.localize("UI", "SegmentSetupPresetSizeLabel"));
                    panel10.add(presetSizeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- presetSizeComboBox ----
                    presetSizeComboBox.setFont(UIManager.getFont("ComboBox.font"));
                    presetSizeComboBox.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            presetSizeComboBoxActionPerformed(e);
                        }
                    });
                    panel10.add(presetSizeComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- measurementLabel ----
                    measurementLabel.setFont(UIManager.getFont("Label.font"));
                    measurementLabel.setText(Localizer.localize("UI", "SegmentSetupMeasurementLabel"));
                    panel10.add(measurementLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- measurementComboBox ----
                    measurementComboBox.setModel(new DefaultComboBoxModel(new String[] {
                        "Pixels",
                        "Millimeters",
                        "Centimeters",
                        "Inches"
                    }));
                    measurementComboBox.setFont(UIManager.getFont("ComboBox.font"));
                    measurementComboBox.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            measurementComboBoxActionPerformed(e);
                        }
                    });
                    panel10.add(measurementComboBox, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- orientation ----
                    orientation.setModel(new DefaultComboBoxModel(new String[] {
                        "Portrait",
                        "Landscape"
                    }));
                    orientation.setFont(UIManager.getFont("ComboBox.font"));
                    orientation.setPrototypeDisplayValue("xxxxxxxxxx");
                    orientation.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            orientationItemStateChanged(e);
                        }
                    });
                    panel10.add(orientation, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- orientationLabel ----
                    orientationLabel.setFont(UIManager.getFont("Label.font"));
                    orientationLabel.setText(Localizer.localize("UI", "FormOrientationLabel"));
                    panel10.add(orientationLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));
                }
                contentPanel.add(panel10, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

                //======== panel6 ========
                {
                    panel6.setOpaque(false);
                    panel6.setBorder(new CompoundBorder(
                        new MatteBorder(1, 0, 0, 0, Color.lightGray),
                        new EmptyBorder(5, 0, 5, 0)));
                    panel6.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel6.getLayout()).columnWidths = new int[] {0, 0, 20, 0, 0, 0};
                    ((GridBagLayout)panel6.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)panel6.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 1.0, 1.0E-4};
                    ((GridBagLayout)panel6.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                    //---- widthLabel ----
                    widthLabel.setFont(UIManager.getFont("Label.font"));
                    widthLabel.setText(Localizer.localize("UI", "SegmentSetupWidthLabel"));
                    panel6.add(widthLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 10), 0, 0));

                    //---- segmentWidth ----
                    segmentWidth.setModel(new SpinnerNumberModel(0.0, 0.0, 5990.0, 1.0));
                    segmentWidth.setFont(UIManager.getFont("Spinner.font"));
                    panel6.add(segmentWidth, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 10), 0, 0));

                    //---- heightLabel ----
                    heightLabel.setFont(UIManager.getFont("Label.font"));
                    heightLabel.setText(Localizer.localize("UI", "SegmentSetupHeightLabel"));
                    panel6.add(heightLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 10), 0, 0));

                    //---- segmentHeight ----
                    segmentHeight.setModel(new SpinnerNumberModel(0.0, 0.0, 5990.0, 1.0));
                    segmentHeight.setFont(UIManager.getFont("Spinner.font"));
                    panel6.add(segmentHeight, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(panel6, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

                //======== panel1 ========
                {
                    panel1.setBorder(new CompoundBorder(
                        new MatteBorder(1, 0, 0, 0, Color.lightGray),
                        new EmptyBorder(5, 0, 5, 0)));
                    panel1.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                    //======== panel3 ========
                    {
                        panel3.setOpaque(false);
                        panel3.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {0, 0, 0, 60, 0};
                        ((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                        //---- segmentRecognitionCheckBox ----
                        segmentRecognitionCheckBox.setSelected(true);
                        segmentRecognitionCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                        segmentRecognitionCheckBox.setText(Localizer.localize("UI", "SegmentSetupEnableSegmentBarcodesCheckBox"));
                        panel3.add(segmentRecognitionCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- segmentBarcodeScaleLabel ----
                        segmentBarcodeScaleLabel.setFont(UIManager.getFont("Label.font"));
                        segmentBarcodeScaleLabel.setText(Localizer.localize("UI", "SegmentSetupBarcodeScaleLabel"));
                        panel3.add(segmentBarcodeScaleLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- segmentBarcodeScaleSpinner ----
                        segmentBarcodeScaleSpinner.setModel(new SpinnerNumberModel(0.6, 0.1, 5.0, 0.1));
                        segmentBarcodeScaleSpinner.setFont(UIManager.getFont("Spinner.font"));
                        panel3.add(segmentBarcodeScaleSpinner, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    panel1.add(panel3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(panel1, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

                //======== panel7 ========
                {
                    panel7.setBorder(new CompoundBorder(
                        new MatteBorder(1, 0, 0, 0, Color.lightGray),
                        new EmptyBorder(5, 0, 5, 0)));
                    panel7.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel7.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)panel7.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)panel7.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)panel7.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                    //======== panel5 ========
                    {
                        panel5.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel5.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                        ((GridBagLayout)panel5.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)panel5.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)panel5.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                        //---- fieldnamePrefixLabel ----
                        fieldnamePrefixLabel.setFont(UIManager.getFont("Label.font"));
                        fieldnamePrefixLabel.setText(Localizer.localize("UI", "SegmentSetupFieldnamePrefixLabel"));
                        panel5.add(fieldnamePrefixLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- defaultFieldnamePrefixTextField ----
                        defaultFieldnamePrefixTextField.setFont(UIManager.getFont("TextField.font"));
                        defaultFieldnamePrefixTextField.setText("fieldname");
                        panel5.add(defaultFieldnamePrefixTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- startsAtLabel ----
                        startsAtLabel.setFont(UIManager.getFont("Label.font"));
                        startsAtLabel.setText(Localizer.localize("UI", "SegmentSetupStartsAtLabel"));
                        panel5.add(startsAtLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- fieldnameCounterSpinner ----
                        fieldnameCounterSpinner.setModel(new SpinnerNumberModel(1, 0, 9999999, 1));
                        fieldnameCounterSpinner.setFont(UIManager.getFont("Spinner.font"));
                        panel5.add(fieldnameCounterSpinner, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    panel7.add(panel5, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(panel7, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 0, 75, 6, 75, 0};
                ((GridBagLayout)buttonBar.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};
                ((GridBagLayout)buttonBar.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                //---- helpLabel ----
                helpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                helpLabel.setHelpGUID("segment-setup");
                helpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                buttonBar.add(helpLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));

                //---- okButton ----
                okButton.setFont(UIManager.getFont("Button.font"));
                okButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/accept.png")));
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                okButton.setText(Localizer.localize("UI", "OKButtonText"));
                buttonBar.add(okButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));

                //---- cancelButton ----
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UI", "CancelButtonText"));
                buttonBar.add(cancelButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        setSize(600, 350);
        setLocationRelativeTo(null);
        // //GEN-END:initComponents
    }

    public int getPixelSegmentHeight() {
        measurement.setFrom(currentUnit);
        measurement.setTo(Measurement.PIXELS);
        return (int) Math.round(measurement.convert((Double) segmentHeight.getValue()));
    }

    public int getPixelSegmentWidth() {
        measurement.setFrom(currentUnit);
        measurement.setTo(Measurement.PIXELS);
        return (int) Math.round(measurement.convert((Double) segmentWidth.getValue()));
    }


    private SizeAttributes getPageSize() {
        SizeAttributes sizeAttributes = new SizeAttributes();
        sizeAttributes.setWidth(getPixelSegmentWidth());
        sizeAttributes.setHeight(getPixelSegmentHeight());
        sizeAttributes.setOrientation(SizeAttributes.PORTRAIT);
        return sizeAttributes;
    }

    private SizeAttributes getPageDefaultSize(String size) {

        String layout = (String) orientation.getSelectedObjects()[0];

        SizeAttributes sizeAttributes = new SizeAttributes();

        if (layout.equals("Portrait")) {
            // get portrait defaults
            sizeAttributes = GraphUtils
                .getDefaultSizeAttributes(SizeAttributes.SEGMENT, SizeAttributes.PORTRAIT, size);

        } else {
            // Landscape Values
            sizeAttributes = GraphUtils
                .getDefaultSizeAttributes(SizeAttributes.SEGMENT, SizeAttributes.LANDSCAPE, size);

        }
        return sizeAttributes;

    }


    private void okButtonActionPerformed(ActionEvent e) {

        if (Misc.validateFieldname(defaultFieldnamePrefixTextField.getText().trim()) == false) {
            String msg = Localizer.localize("UI", "SegmentSetupInvalidFieldNameMessage");
            Misc.showErrorMsg(Main.getInstance(), msg);
            return;
        }


        if (!(GraphUtils
            .sizesMatch(getPageDefaultSize((String) presetSizeComboBox.getSelectedObjects()[0]),
                getPageSize()))) {
            presetSizeComboBox.setSelectedItem("Custom");
        }

        if (!GraphUtils.checkPageSettings(getPageSize())) {
            return;
        }

        setDialogResult(JOptionPane.OK_OPTION);
        setVisible(false);

    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        setDialogResult(JOptionPane.CANCEL_OPTION);
        dispose();
    }

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    public void setReportName(String name) {
        segmentName.setText(name);
    }

    public String getReportName() {
        return segmentName.getText();
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel panel4;
    private JLabel nameLabel;
    private JTextField segmentName;
    private JPanel panel10;
    private JLabel presetSizeLabel;
    private JComboBox presetSizeComboBox;
    private JLabel measurementLabel;
    private JComboBox measurementComboBox;
    private JComboBox orientation;
    private JLabel orientationLabel;
    private JPanel panel6;
    private JLabel widthLabel;
    private JSpinner segmentWidth;
    private JLabel heightLabel;
    private JSpinner segmentHeight;
    private JPanel panel1;
    private JPanel panel3;
    private JCheckBox segmentRecognitionCheckBox;
    private JLabel segmentBarcodeScaleLabel;
    private JSpinner segmentBarcodeScaleSpinner;
    private JPanel panel7;
    private JPanel panel5;
    private JLabel fieldnamePrefixLabel;
    private JTextField defaultFieldnamePrefixTextField;
    private JLabel startsAtLabel;
    private JSpinner fieldnameCounterSpinner;
    private JPanel buttonBar;
    private JHelpLabel helpLabel;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration //GEN-END:variables
}
