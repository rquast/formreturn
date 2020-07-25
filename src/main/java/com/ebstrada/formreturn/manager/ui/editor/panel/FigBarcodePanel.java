package com.ebstrada.formreturn.manager.ui.editor.panel;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigBarcode;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.util.BarcodeCreator;
import com.ebstrada.formreturn.manager.util.Misc;

public class FigBarcodePanel extends EditorPanel {

    private static final long serialVersionUID = 1L;

    private Fig selectedElement;

    public FigBarcodePanel() {
        initComponents();
        restoreSettings();
    }

    public void restoreSettings() {
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        List<String> barcodeTypes = BarcodeCreator.getBarcodeTypes();
        for (String barcodeType : barcodeTypes) {
            dcbm.addElement(barcodeType);
        }
        barcodeTypeComboBox.setModel(dcbm);
    }

    private void applySettingsButtonActionPerformed(ActionEvent e) {

        String barcodeType = "Form ID";
        String barcodeValue = "12345-67890";

        if (((String) barcodeTypeComboBox.getSelectedItem()).equalsIgnoreCase("Form ID")) {
            barcodeType = "Form ID";
            barcodeValue = "12345-67890";

        } else {
            barcodeType = (String) barcodeTypeComboBox.getSelectedItem();
            barcodeValue = barcodeValueTextField.getText();
        }

        try {
            ((FigBarcode) selectedElement)
                .updateBarcode(barcodeType, barcodeValue, showTextCheckBox.isSelected(),
                    quietZoneCheckBox.isSelected(), defaultBarHeightCheckBox.isSelected(),
                    (Integer) barHeightSpinner.getValue());
        } catch (Exception e1) {
            Misc.showErrorMsg(getRootPane().getTopLevelAncestor(), e1.getMessage());
        }

        ((FigBarcode) selectedElement).resizeFigToBarcodeSize();

    }

    private void barcodeTypeComboBoxActionPerformed(ActionEvent e) {
        if (((String) barcodeTypeComboBox.getSelectedItem()).equalsIgnoreCase("Form ID")) {
            barcodeValueTextField.setText("");
            barcodeValueTextField.setEditable(false);
        } else {
            barcodeValueTextField.setText(((FigBarcode) selectedElement).getBarcodeValue());
            barcodeValueTextField.setEditable(true);
        }
    }

    private void defaultBarHeightCheckBoxActionPerformed(ActionEvent e) {
        if (defaultBarHeightCheckBox.isSelected()) {
            barHeightSpinner.setEnabled(false);
        } else {
            barHeightSpinner.setEnabled(true);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        barcodeTypeLabel = new JLabel();
        barcodeTypeComboBox = new JComboBox();
        barcodeValueLabel = new JLabel();
        barcodeValueTextField = new JTextField();
        barcodeBarHeightLabel = new JLabel();
        barHeightSpinner = new JSpinner();
        defaultBarHeightCheckBox = new JCheckBox();
        includeLabel = new JLabel();
        showTextCheckBox = new JCheckBox();
        quietZoneCheckBox = new JCheckBox();
        applySettingsButton = new JButton();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        this.setTitle(Localizer.localize("UI", "BarcodePanelTitle"));

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setBorder(null);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights =
                new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights =
                new double[] {1.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0E-4};

            //---- barcodeTypeLabel ----
            barcodeTypeLabel.setFont(UIManager.getFont("Label.font"));
            barcodeTypeLabel.setText(Localizer.localize("UI", "BarcodePanelBarcodeTypeLabel"));
            panel1.add(barcodeTypeLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

            //---- barcodeTypeComboBox ----
            barcodeTypeComboBox.setFont(UIManager.getFont("ComboBox.font"));
            barcodeTypeComboBox.setPrototypeDisplayValue("xxxxx");
            barcodeTypeComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    barcodeTypeComboBoxActionPerformed(e);
                }
            });
            panel1.add(barcodeTypeComboBox,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- barcodeValueLabel ----
            barcodeValueLabel.setFont(UIManager.getFont("Label.font"));
            barcodeValueLabel.setText(Localizer.localize("UI", "BarcodePanelBarcodeValueLabel"));
            panel1.add(barcodeValueLabel,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

            //---- barcodeValueTextField ----
            barcodeValueTextField.setFont(UIManager.getFont("TextField.font"));
            panel1.add(barcodeValueTextField,
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- barcodeBarHeightLabel ----
            barcodeBarHeightLabel.setFont(UIManager.getFont("Label.font"));
            barcodeBarHeightLabel
                .setText(Localizer.localize("UI", "BarcodePanelBarcodeBarHeightLabel"));
            panel1.add(barcodeBarHeightLabel,
                new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

            //---- barHeightSpinner ----
            barHeightSpinner.setFont(UIManager.getFont("Spinner.font"));
            panel1.add(barHeightSpinner,
                new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- defaultBarHeightCheckBox ----
            defaultBarHeightCheckBox.setFont(UIManager.getFont("CheckBox.font"));
            defaultBarHeightCheckBox.setBackground(null);
            defaultBarHeightCheckBox.setOpaque(false);
            defaultBarHeightCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    defaultBarHeightCheckBoxActionPerformed(e);
                }
            });
            defaultBarHeightCheckBox
                .setText(Localizer.localize("UI", "BarcodePanelDefaultBarHeightCheckBox"));
            panel1.add(defaultBarHeightCheckBox,
                new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

            //---- includeLabel ----
            includeLabel.setFont(UIManager.getFont("Label.font"));
            includeLabel.setText(Localizer.localize("UI", "BarcodePanelIncludeLabel"));
            panel1.add(includeLabel,
                new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

            //---- showTextCheckBox ----
            showTextCheckBox.setFont(UIManager.getFont("CheckBox.font"));
            showTextCheckBox.setFocusPainted(false);
            showTextCheckBox.setBackground(null);
            showTextCheckBox.setOpaque(false);
            showTextCheckBox.setText(Localizer.localize("UI", "BarcodePanelShowTextCheckBox"));
            panel1.add(showTextCheckBox,
                new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

            //---- quietZoneCheckBox ----
            quietZoneCheckBox.setFont(UIManager.getFont("CheckBox.font"));
            quietZoneCheckBox.setFocusPainted(false);
            quietZoneCheckBox.setBackground(null);
            quietZoneCheckBox.setOpaque(false);
            quietZoneCheckBox.setText(Localizer.localize("UI", "BarcodePanelQuietZoneCheckBox"));
            panel1.add(quietZoneCheckBox,
                new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

            //---- applySettingsButton ----
            applySettingsButton.setFont(UIManager.getFont("Button.font"));
            applySettingsButton.setFocusPainted(false);
            applySettingsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    applySettingsButtonActionPerformed(e);
                }
            });
            applySettingsButton.setText(Localizer.localize("UI", "ApplySettingsButtonText"));
            panel1.add(applySettingsButton,
                new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel barcodeTypeLabel;
    private JComboBox barcodeTypeComboBox;
    private JLabel barcodeValueLabel;
    private JTextField barcodeValueTextField;
    private JLabel barcodeBarHeightLabel;
    private JSpinner barHeightSpinner;
    private JCheckBox defaultBarHeightCheckBox;
    private JLabel includeLabel;
    private JCheckBox showTextCheckBox;
    private JCheckBox quietZoneCheckBox;
    private JButton applySettingsButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    @Override public void removeListeners() {
        // TODO Auto-generated method stub

    }

    @Override public void setSelectedElement(Fig selectedFig) {
        selectedElement = selectedFig;
        if (((FigBarcode) selectedElement).getBarcodeType().equalsIgnoreCase("Form ID")) {
            barcodeTypeComboBox.setSelectedItem("Form ID");
            barcodeValueTextField.setText("");
            barcodeValueTextField.setEditable(false);
        } else {
            barcodeTypeComboBox.setSelectedItem(((FigBarcode) selectedElement).getBarcodeType());
            barcodeValueTextField.setText(((FigBarcode) selectedElement).getBarcodeValue());
            barcodeValueTextField.setEditable(true);
        }

        showTextCheckBox.setSelected(((FigBarcode) selectedElement).isShowText());
        quietZoneCheckBox.setSelected(((FigBarcode) selectedElement).isQuietZone());
        defaultBarHeightCheckBox
            .setSelected(((FigBarcode) selectedElement).isUseDefaultBarHeight());
        barHeightSpinner.setValue(((FigBarcode) selectedElement).getBarHeight());

        if (defaultBarHeightCheckBox.isSelected()) {
            barHeightSpinner.setEnabled(false);
        } else {
            barHeightSpinner.setEnabled(true);
        }

    }

    @Override public void updatePanel() {

    }
}
