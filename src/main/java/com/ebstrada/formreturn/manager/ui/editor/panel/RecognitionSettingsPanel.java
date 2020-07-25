package com.ebstrada.formreturn.manager.ui.editor.panel;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.jdesktop.swingx.JXTaskPane;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.structure.PublicationRecognitionStructure;
import com.ebstrada.formreturn.manager.ui.editor.RecognitionPreviewPanel;

public class RecognitionSettingsPanel extends JXTaskPane {

    private static final long serialVersionUID = 1L;

    private Double deskewThreshold;
    private Integer luminanceCutOff;
    private Integer markThreshold;
    private Integer fragmentPadding;
    private boolean performDeskew;

    private RecognitionPreviewPanel recognitionPreviewPanel;

    public RecognitionSettingsPanel(RecognitionPreviewPanel recognitionPreviewPanel) {
        this.recognitionPreviewPanel = recognitionPreviewPanel;
        initComponents();
        restoreRecognitionSettings();
    }

    private void restoreRecognitionSettings() {
        PublicationRecognitionStructure publicationRecognitionStructure =
            recognitionPreviewPanel.getPublicationRecognitionStructure();
        deskewThresholdSpinner.setValue(publicationRecognitionStructure.getDeskewThreshold());
        luminanceThresholdSpinner.setValue(publicationRecognitionStructure.getLuminanceCutOff());
        markThresholdSpinner.setValue(publicationRecognitionStructure.getMarkThreshold());
        fragmentPaddingSpinner.setValue(publicationRecognitionStructure.getFragmentPadding());
        performDeskewCheckBox.setSelected(publicationRecognitionStructure.isPerformDeskew());
    }

    private void applySettingsButtonActionPerformed(ActionEvent e) {
        deskewThreshold = (Double) deskewThresholdSpinner.getValue();
        luminanceCutOff = (Integer) luminanceThresholdSpinner.getValue();
        markThreshold = (Integer) markThresholdSpinner.getValue();
        fragmentPadding = (Integer) fragmentPaddingSpinner.getValue();
        performDeskew = performDeskewCheckBox.isSelected();
        recognitionPreviewPanel
            .applySettingsButtonActionPerformed(deskewThreshold, luminanceCutOff, markThreshold,
                fragmentPadding, performDeskew);
    }

    private void defaultSettingsButtonActionPerformed(ActionEvent e) {
        deskewThreshold = 1.05;
        luminanceCutOff = 200;
        markThreshold = 40;
        fragmentPadding = 1;
        performDeskew = true;

        deskewThresholdSpinner.setValue(new Double(deskewThreshold));
        luminanceThresholdSpinner.setValue(new Integer(luminanceCutOff));
        markThresholdSpinner.setValue(new Integer(markThreshold));
        fragmentPaddingSpinner.setValue(new Integer(fragmentPadding));
        performDeskewCheckBox.setSelected(performDeskew);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        luminanceLabel = new JLabel();
        luminanceThresholdSpinner = new JSpinner();
        markThresholdLabel = new JLabel();
        markThresholdSpinner = new JSpinner();
        fragmentPaddingLabel = new JLabel();
        fragmentPaddingSpinner = new JSpinner();
        deskewThresholdLabel = new JLabel();
        deskewThresholdSpinner = new JSpinner();
        performDeskewCheckBox = new JCheckBox();
        defaultSettingsButton = new JButton();
        applySettingsButton = new JButton();

        //======== this ========
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout) contentPane.getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) contentPane.getLayout()).rowHeights =
            new int[] {0, 0, 0, 0, 0, 24, 0, 0, 0, 0, 0, 0};
        ((GridBagLayout) contentPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) contentPane.getLayout()).rowWeights =
            new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0E-4};
        this.setTitle(Localizer.localize("UI", "RecognitionPanelTitle"));

        //---- luminanceLabel ----
        luminanceLabel.setFont(UIManager.getFont("Label.font"));
        luminanceLabel.setText(Localizer.localize("UI", "RecognitionPanelLuminanceLabel"));
        contentPane.add(luminanceLabel,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //---- luminanceThresholdSpinner ----
        luminanceThresholdSpinner.setModel(new SpinnerNumberModel(200, 0, 255, 1));
        luminanceThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
        contentPane.add(luminanceThresholdSpinner,
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //---- markThresholdLabel ----
        markThresholdLabel.setFont(UIManager.getFont("Label.font"));
        markThresholdLabel.setText(Localizer.localize("UI", "RecognitionPanelMarkThresholdLabel"));
        contentPane.add(markThresholdLabel,
            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //---- markThresholdSpinner ----
        markThresholdSpinner.setModel(new SpinnerNumberModel(40, -10000, 10000, 1));
        markThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
        contentPane.add(markThresholdSpinner,
            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //---- fragmentPaddingLabel ----
        fragmentPaddingLabel.setFont(UIManager.getFont("Label.font"));
        fragmentPaddingLabel
            .setText(Localizer.localize("UI", "RecognitionPanelFragmentPaddingLabel"));
        contentPane.add(fragmentPaddingLabel,
            new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //---- fragmentPaddingSpinner ----
        fragmentPaddingSpinner.setModel(new SpinnerNumberModel(1, 0, 200, 1));
        fragmentPaddingSpinner.setFont(UIManager.getFont("Spinner.font"));
        contentPane.add(fragmentPaddingSpinner,
            new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //---- deskewThresholdLabel ----
        deskewThresholdLabel.setFont(UIManager.getFont("Label.font"));
        deskewThresholdLabel
            .setText(Localizer.localize("UI", "RecognitionPanelDeskewThresholdLabel"));
        contentPane.add(deskewThresholdLabel,
            new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //---- deskewThresholdSpinner ----
        deskewThresholdSpinner.setModel(new SpinnerNumberModel(1.05, 0.0, 90.0, 0.01));
        deskewThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
        contentPane.add(deskewThresholdSpinner,
            new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //---- performDeskewCheckBox ----
        performDeskewCheckBox.setSelected(true);
        performDeskewCheckBox.setFocusPainted(false);
        performDeskewCheckBox.setOpaque(false);
        performDeskewCheckBox.setFont(UIManager.getFont("CheckBox.font"));
        performDeskewCheckBox
            .setText(Localizer.localize("UI", "RecognitionPanelPerformDeskewCheckBox"));
        contentPane.add(performDeskewCheckBox,
            new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //---- defaultSettingsButton ----
        defaultSettingsButton.setFont(UIManager.getFont("Button.font"));
        defaultSettingsButton.setFocusPainted(false);
        defaultSettingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                defaultSettingsButtonActionPerformed(e);
            }
        });
        defaultSettingsButton.setText(Localizer.localize("UI", "RestoreDefaultsButtonText"));
        contentPane.add(defaultSettingsButton,
            new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //---- applySettingsButton ----
        applySettingsButton.setFont(UIManager.getFont("Button.font"));
        applySettingsButton.setFocusPainted(false);
        applySettingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applySettingsButtonActionPerformed(e);
            }
        });
        applySettingsButton.setText(Localizer.localize("UI", "ApplySettingsButtonText"));
        contentPane.add(applySettingsButton,
            new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JLabel luminanceLabel;
    private JSpinner luminanceThresholdSpinner;
    private JLabel markThresholdLabel;
    private JSpinner markThresholdSpinner;
    private JLabel fragmentPaddingLabel;
    private JSpinner fragmentPaddingSpinner;
    private JLabel deskewThresholdLabel;
    private JSpinner deskewThresholdSpinner;
    private JCheckBox performDeskewCheckBox;
    private JButton defaultSettingsButton;
    private JButton applySettingsButton;
    // JFormDesigner - End of variables declaration //GEN-END:variables
}
