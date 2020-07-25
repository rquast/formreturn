package com.ebstrada.formreturn.manager.ui.editor.panel;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.dialog.ColorChooser;
import com.ebstrada.formreturn.manager.util.Swatch;

public class FigLinePanel extends EditorPanel {

    private static final long serialVersionUID = 1L;

    private Fig selectedElement;

    public FigLinePanel() {
        initComponents();
    }

    @Override public void updatePanel() {
        lineWidthSpinner.setValue(new Double(selectedElement.getLineWidth()));
        lineStyleComboBox.setSelectedItem(selectedElement.getDashedString());
        if (selectedElement != null) {
            foregroundColorButton.setIcon(Swatch.forColor(selectedElement.getLineColor()));
        }
    }

    @Override public void removeListeners() {
    }

    @Override public void setSelectedElement(Fig selectedFig) {
        selectedElement = selectedFig;
        selectedElement.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName() == "lineColor") {
                    foregroundColorButton.setIcon(Swatch.forColor(selectedElement.getLineColor()));
                }
            }
        });
    }

    private void lineWidthSpinnerStateChanged(ChangeEvent e) {
        double newWidthValue = new Double(((JSpinner) e.getSource()).getValue() + "");

        if (newWidthValue != selectedElement.getLineWidth()) {
            selectedElement.setLineWidth(new Float(newWidthValue));
            selectedElement.damage();
            lineWidthSpinner.setValue(new Double(newWidthValue));
        }
    }

    private void lineStyleComboBoxActionPerformed(ActionEvent e) {
        selectedElement.setDashedString((String) lineStyleComboBox.getSelectedItem());
        selectedElement.damage();
    }

    private void foregroundColorButtonActionPerformed(ActionEvent e) {
        ColorChooser colorChooserDialog =
            new ColorChooser(Main.getInstance(), selectedElement, true);
        colorChooserDialog.setTitle(Localizer.localize("UI", "LineColorChooserTitle"));
        colorChooserDialog.setVisible(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        lineWeightLabel = new JLabel();
        lineWidthSpinner = new JSpinner();
        lineStyleLabel = new JLabel();
        lineStyleComboBox = new JComboBox();
        colorLabel = new JLabel();
        foregroundColorButton = new JButton();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setOpaque(false);
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        this.setTitle(Localizer.localize("UI", "LinePanelTitle"));

        //======== panel1 ========
        {
            panel1.setBorder(null);
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights =
                new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0E-4};

            //---- lineWeightLabel ----
            lineWeightLabel.setFont(UIManager.getFont("Label.font"));
            lineWeightLabel.setText(Localizer.localize("UI", "LineWeightLabel"));
            panel1.add(lineWeightLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- lineWidthSpinner ----
            lineWidthSpinner.setModel(new SpinnerNumberModel(1.0F, 0.01F, 200.0F, 0.01F));
            lineWidthSpinner.setFont(UIManager.getFont("Spinner.font"));
            lineWidthSpinner.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    lineWidthSpinnerStateChanged(e);
                }
            });
            panel1.add(lineWidthSpinner,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- lineStyleLabel ----
            lineStyleLabel.setFont(UIManager.getFont("Label.font"));
            lineStyleLabel.setText(Localizer.localize("UI", "LineStyleLabel"));
            panel1.add(lineStyleLabel,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- lineStyleComboBox ----
            lineStyleComboBox.setModel(
                new DefaultComboBoxModel(new String[] {"Solid", "Dashed", "Dotted", "Double"}));
            lineStyleComboBox.setFont(UIManager.getFont("ComboBox.font"));
            lineStyleComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    lineStyleComboBoxActionPerformed(e);
                }
            });
            panel1.add(lineStyleComboBox,
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- colorLabel ----
            colorLabel.setFont(UIManager.getFont("Label.font"));
            colorLabel.setText(Localizer.localize("UI", "LineColorLabel"));
            panel1.add(colorLabel,
                new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- foregroundColorButton ----
            foregroundColorButton.setMargin(new Insets(2, 5, 2, 5));
            foregroundColorButton.setFont(UIManager.getFont("Button.font"));
            foregroundColorButton.setIconTextGap(5);
            foregroundColorButton.setFocusPainted(false);
            foregroundColorButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    foregroundColorButtonActionPerformed(e);
                }
            });
            if (selectedElement != null) {
                foregroundColorButton.setIcon(com.ebstrada.formreturn.manager.util.Swatch
                    .forColor(selectedElement.getLineColor()));
            }
            foregroundColorButton
                .setText(Localizer.localize("UI", "LineForegroundColorButtonText"));
            panel1.add(foregroundColorButton,
                new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1);
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel lineWeightLabel;
    private JSpinner lineWidthSpinner;
    private JLabel lineStyleLabel;
    private JComboBox lineStyleComboBox;
    private JLabel colorLabel;
    private JButton foregroundColorButton;
    // JFormDesigner - End of variables declaration //GEN-END:variables
}
