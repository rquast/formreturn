package com.ebstrada.formreturn.manager.ui.editor.panel;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.event.*;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigRRect;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.dialog.ColorChooser;
import com.ebstrada.formreturn.manager.util.Swatch;
import org.jdesktop.swingx.*;

public class FigRRectPanel extends EditorPanel {

    private static final long serialVersionUID = 1L;

    private Fig selectedElement;

    public FigRRectPanel() {
        initComponents();
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
        colorChooserDialog.setTitle(Localizer.localize("UI", "ForegroundColorChooserTitle"));
        colorChooserDialog.setVisible(true);
    }

    private void backgroundColorButtonActionPerformed(ActionEvent e) {
        ColorChooser colorChooserDialog =
            new ColorChooser(Main.getInstance(), selectedElement, false);
        colorChooserDialog.setTitle(Localizer.localize("UI", "BackgroundColorChooserTitle"));
        colorChooserDialog.setVisible(true);
    }

    private void filledCheckBoxActionPerformed(ActionEvent e) {
        selectedElement.setFilled(filledCheckBox.isSelected());
        selectedElement.damage();
    }

    private void radiusSpinnerStateChanged(ChangeEvent e) {
        ((FigRRect) selectedElement).setCornerRadius((Double) radiusSpinner.getValue());
        selectedElement.damage();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        borderWeightLabel = new JLabel();
        lineWidthSpinner = new JSpinner();
        radiusLabel = new JLabel();
        radiusSpinner = new JSpinner();
        borderStyleLabel = new JLabel();
        lineStyleComboBox = new JComboBox();
        colorLabel = new JLabel();
        foregroundColorButton = new JButton();
        backgroundColorButton = new JButton();
        backgroundLabel = new JLabel();
        filledCheckBox = new JCheckBox();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        this.setTitle(Localizer.localize("UI", "RoundRectanglePanelTitle"));

        //======== panel1 ========
        {
            panel1.setBorder(null);
            panel1.setOpaque(false);
            panel1.setFont(UIManager.getFont("Label.font"));
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights =
                new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 25, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights =
                new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0E-4};

            //---- borderWeightLabel ----
            borderWeightLabel.setFont(UIManager.getFont("Label.font"));
            borderWeightLabel
                .setText(Localizer.localize("UI", "RoundRectanglePanelBorderWeightLabel"));
            panel1.add(borderWeightLabel,
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

            //---- radiusLabel ----
            radiusLabel.setFont(UIManager.getFont("Label.font"));
            radiusLabel.setText(Localizer.localize("UI", "RoundRectanglePanelRadiusLabel"));
            panel1.add(radiusLabel,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- radiusSpinner ----
            radiusSpinner.setModel(new SpinnerNumberModel(16.0, 0.0, 1000.0, 1.0));
            radiusSpinner.setFont(UIManager.getFont("Spinner.font"));
            radiusSpinner.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    radiusSpinnerStateChanged(e);
                }
            });
            panel1.add(radiusSpinner,
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- borderStyleLabel ----
            borderStyleLabel.setFont(UIManager.getFont("Label.font"));
            borderStyleLabel
                .setText(Localizer.localize("UI", "RoundRectanglePanelBorderStyleLabel"));
            panel1.add(borderStyleLabel,
                new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
                new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- colorLabel ----
            colorLabel.setFont(UIManager.getFont("Label.font"));
            colorLabel.setText(Localizer.localize("UI", "RoundRectanglePanelColorLabel"));
            panel1.add(colorLabel,
                new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
                .setText(Localizer.localize("UI", "RoundRectanglePanelBorderColorButtonText"));
            panel1.add(foregroundColorButton,
                new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- backgroundColorButton ----
            backgroundColorButton.setMargin(new Insets(2, 5, 2, 5));
            backgroundColorButton.setFont(UIManager.getFont("Button.font"));
            backgroundColorButton.setIconTextGap(5);
            backgroundColorButton.setFocusPainted(false);
            backgroundColorButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    backgroundColorButtonActionPerformed(e);
                }
            });
            if (selectedElement != null) {
                backgroundColorButton.setIcon(com.ebstrada.formreturn.manager.util.Swatch
                    .forColor(selectedElement.getFillColor()));
            }
            backgroundColorButton
                .setText(Localizer.localize("UI", "RoundRectanglePanelBackgroundColorButtonText"));
            panel1.add(backgroundColorButton,
                new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- backgroundLabel ----
            backgroundLabel.setFont(UIManager.getFont("Label.font"));
            backgroundLabel.setText(Localizer.localize("UI", "RoundRectanglePanelBackgroundLabel"));
            panel1.add(backgroundLabel,
                new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- filledCheckBox ----
            filledCheckBox.setFont(UIManager.getFont("CheckBox.font"));
            filledCheckBox.setBackground(null);
            filledCheckBox.setOpaque(false);
            filledCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    filledCheckBoxActionPerformed(e);
                }
            });
            filledCheckBox.setText(Localizer.localize("UI", "RoundRectanglePanelFilledCheckBox"));
            panel1.add(filledCheckBox,
                new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel borderWeightLabel;
    private JSpinner lineWidthSpinner;
    private JLabel radiusLabel;
    private JSpinner radiusSpinner;
    private JLabel borderStyleLabel;
    private JComboBox lineStyleComboBox;
    private JLabel colorLabel;
    private JButton foregroundColorButton;
    private JButton backgroundColorButton;
    private JLabel backgroundLabel;
    private JCheckBox filledCheckBox;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    @Override public void removeListeners() {
    }

    @Override public void setSelectedElement(Fig selectedFig) {
        selectedElement = selectedFig;
        selectedElement.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName() == "lineColor") {
                    foregroundColorButton.setIcon(Swatch.forColor(selectedElement.getLineColor()));
                }
                if (evt.getPropertyName() == "fillColor") {
                    backgroundColorButton.setIcon(Swatch.forColor(selectedElement.getFillColor()));
                }
            }
        });
        filledCheckBox.setSelected(selectedElement.getFilled());
        radiusSpinner.setValue(new Double(((FigRRect) selectedElement).getCornerRadius()));
    }

    @Override public void updatePanel() {
        lineWidthSpinner.setValue(new Double(selectedElement.getLineWidth()));
        lineStyleComboBox.setSelectedItem(selectedElement.getDashedString());
        if (selectedElement != null) {
            foregroundColorButton.setIcon(Swatch.forColor(selectedElement.getLineColor()));
        }
        if (selectedElement != null) {
            backgroundColorButton.setIcon(Swatch.forColor(selectedElement.getFillColor()));
        }
    }
}
