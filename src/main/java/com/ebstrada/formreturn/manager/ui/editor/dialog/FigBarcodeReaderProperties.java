package com.ebstrada.formreturn.manager.ui.editor.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigBarcodeReader;
import com.ebstrada.formreturn.manager.gef.presentation.FigCheckbox;
import com.ebstrada.formreturn.manager.gef.undo.memento.BarcodeReaderMemento;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.reader.BarcodeReaderTypes;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.util.Misc;

public class FigBarcodeReaderProperties extends JDialog {

    private static final long serialVersionUID = 1L;

    private FigBarcodeReader figBarcodeReader;

    private int dialogResult = javax.swing.JOptionPane.CANCEL_OPTION;

    public FigBarcodeReaderProperties(FigBarcodeReader figBarcodeReader) {
        super(Main.getInstance(), true);
        this.figBarcodeReader = figBarcodeReader;
        initComponents();
        restoreSettings();
        getRootPane().setDefaultButton(okButton);
    }

    public void restoreSettings() {

        // TODO: turn this into something that shows all of the
        // barcodes detectable by zxing

        // probably best to make the barcode types a contsant too

        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        List<String> barcodeTypes = BarcodeReaderTypes.getBarcodeTypes();
        for (String barcodeType : barcodeTypes) {
            dcbm.addElement(barcodeType);
        }
        barcodeTypeComboBox.setModel(dcbm);

        setShowText(figBarcodeReader.isShowText());
        setReconciliationKey(figBarcodeReader.isReconciliationKey());
        setFieldnameOrderIndex(figBarcodeReader.getFieldnameOrderIndex());
        fieldNameTextField.setText(figBarcodeReader.getFieldname());
        setCornerDivisor(figBarcodeReader.getCornerDivisor());
        setBarcodeAreaText(figBarcodeReader.getBarcodeAreaText());
        setShowCorners(figBarcodeReader.isShowCorners());
        setBarcodeType(figBarcodeReader.getBarcodeType());

    }

    public boolean existsDuplicateFieldname(String newFieldName) {

        if (figBarcodeReader != null) {
            List layerContents =
                figBarcodeReader.getGraph().getEditor().getLayerManager().getContents();
            for (int i = 0; i < layerContents.size(); i++) {
                Fig fig = (Fig) layerContents.get(i);
                if (fig instanceof FigCheckbox) {
                    if (fig == figBarcodeReader) {
                        continue;
                    }
                    if (((FigCheckbox) fig).getFieldname().equalsIgnoreCase(newFieldName.trim())) {
                        return true;
                    }
                }
            }
        }

        return false;

    }

    private boolean validateSettings() {

        if (existsDuplicateFieldname(getFieldname())) {
            String msg =
                Localizer.localize("UI", "BarcodeReaderPropertiesFieldnameAlreadyExistsMessage");
            Misc.showErrorMsg(Main.getInstance(), msg);
            return false;
        }

        if (Misc.validateFieldname(getFieldname()) == false) {
            String msg =
                Localizer.localize("UI", "BarcodeReaderPropertiesFieldnameInvalidLengthMessage");
            Misc.showErrorMsg(Main.getInstance(), msg);
            return false;
        }

        return true;
    }

    private void defaultsButtonActionPerformed(ActionEvent e) {

        RestoreMarkAreaPresetStyleDialog rmapsd = new RestoreMarkAreaPresetStyleDialog(this);
        rmapsd
            .setTitle(Localizer.localize("UI", "BarcodeReaderPropertiesRestoreDefaultStyleTitle"));
        rmapsd.setRestoreMessage(
            Localizer.localize("UI", "BarcodeReaderPropertiesRestoreDefaultStyleMessage"));
        rmapsd.setModal(true);
        rmapsd.setVisible(true);

        if (rmapsd.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {

            if (rmapsd.isRestoreBoxDesign()) {
                setShowText(true);
                setReconciliationKey(false);
            }

        } else {
            rmapsd.dispose();
            return;
        }

        rmapsd.dispose();

    }

    private boolean isReconciliationKey() {
        return reconciliationKeyCheckBox.isSelected();
    }

    private void setReconciliationKey(boolean isReconciliationKey) {
        reconciliationKeyCheckBox.setSelected(isReconciliationKey);
    }

    private String getFieldname() {
        return fieldNameTextField.getText().trim();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                okButton.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        panel4 = new JPanel();
        dataExportSettingsPanel = new JPanel();
        cdfnLabel = new JLabel();
        fieldNameTextField = new JTextField();
        cdfnOrderLabel = new JLabel();
        cdfnOrderIndexSpinner = new JSpinner();
        barcodeCaptureSettingsPanel = new JPanel();
        panel15 = new JPanel();
        barcodeIDAreaLabel = new JLabel();
        panel11 = new JPanel();
        reconciliationKeyCheckBox = new JCheckBox();
        panel1 = new JPanel();
        detectBarcodeTypeLabel = new JLabel();
        barcodeTypeComboBox = new JComboBox();
        barcodeAreaDesignPanel = new JPanel();
        panel5 = new JPanel();
        showBarcodeAreaCornersLabel = new JLabel();
        showBarcodeAreaCornersCheckBox = new JCheckBox();
        panel3 = new JPanel();
        cornerLengthDivisorLabel = new JLabel();
        cornerDivisorSpinner = new JSpinner();
        panel17 = new JPanel();
        visibleCheckboxTextLabel = new JLabel();
        showTextCheckBox = new JCheckBox();
        panel2 = new JPanel();
        barcodeAreaTextLabel = new JLabel();
        barcodeAreaTextTextField = new JTextField();
        buttonBar = new JPanel();
        helpLabel = new JHelpLabel();
        defaultsButton = new JButton();
        okButton = new JButton();
        cancelButton = new JButton();

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
        this.setTitle(Localizer.localize("UI", "BarcodeReaderPropertiesDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            dialogPane.setLayout(new GridBagLayout());
            ((GridBagLayout)dialogPane.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout)dialogPane.getLayout()).rowHeights = new int[] {0, 0, 0};
            ((GridBagLayout)dialogPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout)dialogPane.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

            //======== panel4 ========
            {
                panel4.setOpaque(false);
                panel4.setLayout(new GridBagLayout());
                ((GridBagLayout)panel4.getLayout()).columnWidths = new int[] {250, 0};
                ((GridBagLayout)panel4.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                ((GridBagLayout)panel4.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)panel4.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0E-4};

                //======== dataExportSettingsPanel ========
                {
                    dataExportSettingsPanel.setOpaque(false);
                    dataExportSettingsPanel.setFont(UIManager.getFont("TitledBorder.font"));
                    dataExportSettingsPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)dataExportSettingsPanel.getLayout()).columnWidths = new int[] {10, 0, 0, 0, 5, 0};
                    ((GridBagLayout)dataExportSettingsPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout)dataExportSettingsPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout)dataExportSettingsPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};
                    dataExportSettingsPanel.setBorder(new CompoundBorder(
                        new TitledBorder(Localizer.localize("UI", "BarcodeReaderPropertiesDataExportSettingsPanelTitle")),
                        new EmptyBorder(2, 2, 2, 2)));

                    //---- cdfnLabel ----
                    cdfnLabel.setFont(UIManager.getFont("Label.font"));
                    cdfnLabel.setText(Localizer.localize("UI", "BarcodeReaderPropertiesCDFNLabel"));
                    dataExportSettingsPanel.add(cdfnLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- fieldNameTextField ----
                    fieldNameTextField.setFont(UIManager.getFont("TextField.font"));
                    dataExportSettingsPanel.add(fieldNameTextField, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- cdfnOrderLabel ----
                    cdfnOrderLabel.setFont(UIManager.getFont("Label.font"));
                    cdfnOrderLabel.setText(Localizer.localize("UI", "BarcodeReaderPropertiesCDFNOrderLabel"));
                    dataExportSettingsPanel.add(cdfnOrderLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- cdfnOrderIndexSpinner ----
                    cdfnOrderIndexSpinner.setModel(new SpinnerNumberModel(0, 0, 100000, 1));
                    cdfnOrderIndexSpinner.setFont(UIManager.getFont("Spinner.font"));
                    dataExportSettingsPanel.add(cdfnOrderIndexSpinner, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));
                }
                panel4.add(dataExportSettingsPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== barcodeCaptureSettingsPanel ========
                {
                    barcodeCaptureSettingsPanel.setOpaque(false);
                    barcodeCaptureSettingsPanel.setFont(UIManager.getFont("TitledBorder.font"));
                    barcodeCaptureSettingsPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)barcodeCaptureSettingsPanel.getLayout()).columnWidths = new int[] {7, 0, 5, 0};
                    ((GridBagLayout)barcodeCaptureSettingsPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout)barcodeCaptureSettingsPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};
                    ((GridBagLayout)barcodeCaptureSettingsPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};
                    barcodeCaptureSettingsPanel.setBorder(new CompoundBorder(
                        new TitledBorder(Localizer.localize("UI", "BarcodeReaderPropertiesCaptureSettingsPanelTitle")),
                        new EmptyBorder(5, 5, 5, 5)));

                    //======== panel15 ========
                    {
                        panel15.setOpaque(false);
                        panel15.setBorder(null);
                        panel15.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel15.getLayout()).columnWidths = new int[] {0, 0, 10, 0};
                        ((GridBagLayout)panel15.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)panel15.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)panel15.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- barcodeIDAreaLabel ----
                        barcodeIDAreaLabel.setFont(UIManager.getFont("Label.font"));
                        barcodeIDAreaLabel.setText(Localizer.localize("UI", "BarcodeReaderPropertiesBarcodeIDAreaLabel"));
                        panel15.add(barcodeIDAreaLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.EAST, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //======== panel11 ========
                        {
                            panel11.setOpaque(false);
                            panel11.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel11.getLayout()).columnWidths = new int[] {0, 0, 0};
                            ((GridBagLayout)panel11.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)panel11.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
                            ((GridBagLayout)panel11.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                            //---- reconciliationKeyCheckBox ----
                            reconciliationKeyCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            panel11.add(reconciliationKeyCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        panel15.add(panel11, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    barcodeCaptureSettingsPanel.add(panel15, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 2, 2), 0, 0));

                    //======== panel1 ========
                    {
                        panel1.setOpaque(false);
                        panel1.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                        ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- detectBarcodeTypeLabel ----
                        detectBarcodeTypeLabel.setFont(UIManager.getFont("Label.font"));
                        detectBarcodeTypeLabel.setText(Localizer.localize("UI", "BarcodeReaderPropertiesDetectBarcodeTypeLabel"));
                        panel1.add(detectBarcodeTypeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- barcodeTypeComboBox ----
                        barcodeTypeComboBox.setFont(UIManager.getFont("ComboBox.font"));
                        barcodeTypeComboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxxx");
                        panel1.add(barcodeTypeComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    barcodeCaptureSettingsPanel.add(panel1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 2), 0, 0));
                }
                panel4.add(barcodeCaptureSettingsPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== barcodeAreaDesignPanel ========
                {
                    barcodeAreaDesignPanel.setOpaque(false);
                    barcodeAreaDesignPanel.setFont(UIManager.getFont("TitledBorder.font"));
                    barcodeAreaDesignPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)barcodeAreaDesignPanel.getLayout()).columnWidths = new int[] {7, 0, 5, 0};
                    ((GridBagLayout)barcodeAreaDesignPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
                    ((GridBagLayout)barcodeAreaDesignPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};
                    ((GridBagLayout)barcodeAreaDesignPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0, 1.0E-4};
                    barcodeAreaDesignPanel.setBorder(new CompoundBorder(
                        new TitledBorder(Localizer.localize("UI", "BarcodeReaderPropertiesDesignPanelTitle")),
                        new EmptyBorder(5, 5, 5, 5)));

                    //======== panel5 ========
                    {
                        panel5.setOpaque(false);
                        panel5.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel5.getLayout()).columnWidths = new int[] {0, 0, 0};
                        ((GridBagLayout)panel5.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)panel5.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)panel5.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- showBarcodeAreaCornersLabel ----
                        showBarcodeAreaCornersLabel.setText("Show Barcode Area Corners");
                        showBarcodeAreaCornersLabel.setFont(UIManager.getFont("Label.font"));
                        showBarcodeAreaCornersLabel.setText(Localizer.localize("UI", "BarcodeReaderPropertiesShowBarcodeAreaCornersLabel"));
                        panel5.add(showBarcodeAreaCornersLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));
                        panel5.add(showBarcodeAreaCornersCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    barcodeAreaDesignPanel.add(panel5, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 2, 2), 0, 0));

                    //======== panel3 ========
                    {
                        panel3.setOpaque(false);
                        panel3.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {0, 0, 0};
                        ((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- cornerLengthDivisorLabel ----
                        cornerLengthDivisorLabel.setFont(UIManager.getFont("Label.font"));
                        cornerLengthDivisorLabel.setText(Localizer.localize("UI", "BarcodeReaderPropertiesCornerLengthDivisorLabel"));
                        panel3.add(cornerLengthDivisorLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- cornerDivisorSpinner ----
                        cornerDivisorSpinner.setModel(new SpinnerNumberModel(4, 4, 128, 1));
                        cornerDivisorSpinner.setFont(UIManager.getFont("Spinner.font"));
                        panel3.add(cornerDivisorSpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    barcodeAreaDesignPanel.add(panel3, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 2, 2), 0, 0));

                    //======== panel17 ========
                    {
                        panel17.setOpaque(false);
                        panel17.setBorder(new EmptyBorder(0, 0, 2, 0));
                        panel17.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel17.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                        ((GridBagLayout)panel17.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)panel17.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)panel17.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- visibleCheckboxTextLabel ----
                        visibleCheckboxTextLabel.setFont(UIManager.getFont("Label.font"));
                        visibleCheckboxTextLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                        visibleCheckboxTextLabel.setText(Localizer.localize("UI", "BarcodeReaderPropertiesVisibleCheckboxTextLabel"));
                        panel17.add(visibleCheckboxTextLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- showTextCheckBox ----
                        showTextCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                        panel17.add(showTextCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    barcodeAreaDesignPanel.add(panel17, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 2, 2), 0, 0));

                    //======== panel2 ========
                    {
                        panel2.setOpaque(false);
                        panel2.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0, 0};
                        ((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- barcodeAreaTextLabel ----
                        barcodeAreaTextLabel.setFont(UIManager.getFont("Label.font"));
                        barcodeAreaTextLabel.setText(Localizer.localize("UI", "BarcodeReaderPropertiesBarcodeAreaTextLabel"));
                        panel2.add(barcodeAreaTextLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- barcodeAreaTextTextField ----
                        barcodeAreaTextTextField.setFont(UIManager.getFont("TextField.font"));
                        panel2.add(barcodeAreaTextTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    barcodeAreaDesignPanel.add(panel2, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 2), 0, 0));
                }
                panel4.add(barcodeAreaDesignPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(panel4, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

            //======== buttonBar ========
            {
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 0, 75, 5, 0, 6, 75, 0};
                ((GridBagLayout)buttonBar.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
                ((GridBagLayout)buttonBar.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                //---- helpLabel ----
                helpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                helpLabel.setHelpGUID("barcode-reader-area-properties");
                helpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                buttonBar.add(helpLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));

                //---- defaultsButton ----
                defaultsButton.setFont(UIManager.getFont("Button.font"));
                defaultsButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
                defaultsButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        defaultsButtonActionPerformed(e);
                    }
                });
                defaultsButton.setText(Localizer.localize("UI", "ResetToDefaultsButtonText"));
                buttonBar.add(defaultsButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
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
                buttonBar.add(okButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
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
                buttonBar.add(cancelButton, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(515, 550);
        setLocationRelativeTo(null);
        // //GEN-END:initComponents
    }

    public int getFieldnameOrderIndex() {
        return (Integer) this.cdfnOrderIndexSpinner.getValue();
    }

    public void setFieldnameOrderIndex(int fieldnameOrderIndex) {
        cdfnOrderIndexSpinner.setValue(fieldnameOrderIndex);
    }

    private void okButtonActionPerformed(ActionEvent e) {

        if (validateSettings() == false) {
            return;
        }

        BarcodeReaderMemento memento = figBarcodeReader.getUpdateMemento();
        memento.setOldFieldname(figBarcodeReader.getFieldname());
        memento.setOldShowText(figBarcodeReader.isShowText());
        memento.setOldReconciliationKey(figBarcodeReader.isReconciliationKey());
        memento.setOldFieldnameOrderIndex(figBarcodeReader.getFieldnameOrderIndex());
        memento.setOldCornerDivisor(figBarcodeReader.getCornerDivisor());
        memento.setOldBarcodeAreaText(figBarcodeReader.getBarcodeAreaText());
        memento.setOldShowCorners(figBarcodeReader.isShowCorners());
        memento.setOldBarcodeType(figBarcodeReader.getBarcodeType());

        memento.setNewFieldname(fieldNameTextField.getText());
        memento.setNewShowText(isShowText());
        memento.setNewReconciliationKey(isReconciliationKey());
        memento.setNewFieldnameOrderIndex(getFieldnameOrderIndex());
        memento.setNewCornerDivisor(getCornerDivisor());
        memento.setNewBarcodeAreaText(getBarcodeAreaText());
        memento.setNewShowCorners(isShowCorners());
        memento.setNewBarcodeType(getBarcodeType());

        // update properties of figBarcodeReader
        figBarcodeReader.setFieldname(fieldNameTextField.getText());
        figBarcodeReader.setShowText(isShowText());
        figBarcodeReader.setReconciliationKey(isReconciliationKey());
        figBarcodeReader.setFieldnameOrderIndex(getFieldnameOrderIndex());
        figBarcodeReader.setCornerDivisor(getCornerDivisor());
        figBarcodeReader.setBarcodeAreaText(getBarcodeAreaText());
        figBarcodeReader.setShowCorners(isShowCorners());
        figBarcodeReader.setBarcodeType(getBarcodeType());

        setDialogResult(JOptionPane.OK_OPTION);
        dispose();

        figBarcodeReader.damage();

    }

    private void setShowText(boolean showText) {
        showTextCheckBox.setSelected(showText);
    }

    private boolean isShowText() {
        return showTextCheckBox.isSelected();
    }

    public int getCornerDivisor() {
        return (Integer) cornerDivisorSpinner.getValue();
    }

    public void setCornerDivisor(int cornerDivisor) {
        cornerDivisorSpinner.setValue(cornerDivisor);
    }

    public String getBarcodeAreaText() {
        return barcodeAreaTextTextField.getText();
    }

    public void setBarcodeAreaText(String barcodeAreaText) {
        barcodeAreaTextTextField.setText(barcodeAreaText);
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

    public boolean isShowCorners() {
        return showBarcodeAreaCornersCheckBox.isSelected();
    }

    public void setShowCorners(boolean showCorners) {
        showBarcodeAreaCornersCheckBox.setSelected(showCorners);
    }

    public int getBarcodeType() {
        return barcodeTypeComboBox.getSelectedIndex();
    }

    public void setBarcodeType(int barcodeType) {
        barcodeTypeComboBox.setSelectedIndex(barcodeType);
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel dialogPane;
    private JPanel panel4;
    private JPanel dataExportSettingsPanel;
    private JLabel cdfnLabel;
    private JTextField fieldNameTextField;
    private JLabel cdfnOrderLabel;
    private JSpinner cdfnOrderIndexSpinner;
    private JPanel barcodeCaptureSettingsPanel;
    private JPanel panel15;
    private JLabel barcodeIDAreaLabel;
    private JPanel panel11;
    private JCheckBox reconciliationKeyCheckBox;
    private JPanel panel1;
    private JLabel detectBarcodeTypeLabel;
    private JComboBox barcodeTypeComboBox;
    private JPanel barcodeAreaDesignPanel;
    private JPanel panel5;
    private JLabel showBarcodeAreaCornersLabel;
    private JCheckBox showBarcodeAreaCornersCheckBox;
    private JPanel panel3;
    private JLabel cornerLengthDivisorLabel;
    private JSpinner cornerDivisorSpinner;
    private JPanel panel17;
    private JLabel visibleCheckboxTextLabel;
    private JCheckBox showTextCheckBox;
    private JPanel panel2;
    private JLabel barcodeAreaTextLabel;
    private JTextField barcodeAreaTextTextField;
    private JPanel buttonBar;
    private JHelpLabel helpLabel;
    private JButton defaultsButton;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration //GEN-END:variables

}
