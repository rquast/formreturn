package com.ebstrada.formreturn.manager.util.preferences.panel;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.structure.PublicationRecognitionStructure;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.persistence.FieldnameDuplicatePresets;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class FormCapturePreferencesPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public FormCapturePreferencesPanel() {
        initComponents();
        restoreSettings();
    }

    private void restoreSettings() {

        // set the recognition settings data first
        PublicationRecognitionStructure prs =
            PreferencesManager.getPublicationRecognitionStructure();
        luminanceThresholdSpinner.setValue(new Integer(prs.getLuminanceCutOff()));
        markThresholdSpinner.setValue(new Integer(prs.getMarkThreshold()));
        fragmentPaddingSpinner.setValue(new Integer(prs.getFragmentPadding()));
        deskewThresholdSpinner.setValue(new Double(prs.getDeskewThreshold()));
        performDeskewCheckBox.setSelected(prs.isPerformDeskew());

        // restore the fieldname duplicate presets
        FieldnameDuplicatePresets fdp = PreferencesManager.getFieldnameDupliatePresets();
        defaultFieldnamePrefixTextField.setText(fdp.getFieldname());
        fieldnameCounterSpinner.setValue(fdp.getCounterStart());
        horizontalDuplicatesSpinner.setValue(fdp.getHorizontalDuplicates());
        verticalDuplicatesSpinner.setValue(fdp.getVerticalDuplicates());
        horizontalSpacingSpinner.setValue(fdp.getHorizontalSpacing());
        verticalSpacingSpinner.setValue(fdp.getVerticalSpacing());

        if (fdp.getNamingDirection()
            == FieldnameDuplicatePresets.DIRECTION_TOP_TO_BOTTOM_LEFT_TO_RIGHT) {
            lrtbButton.setSelected(false);
            tblrButton.setSelected(true);
        }

        if (fdp.getNamingDirection()
            == FieldnameDuplicatePresets.DIRECTION_LEFT_TO_RIGHT_TOP_TO_BOTTOM) {
            tblrButton.setSelected(false);
            lrtbButton.setSelected(true);
        }

    }

    private void resetRecognitionSettingsToDefaultsButtonActionPerformed(ActionEvent e) {

        String message = Localizer
            .localize("Util", "CapturePreferencesConfirmRestoreDefaultRecognitionSettingsMessage");
        int n = JOptionPane.showConfirmDialog(null, message, Localizer
                .localize("Util", "CapturePreferencesConfirmRestoreDefaultRecognitionSettingsTitle"),
            JOptionPane.YES_NO_OPTION);

        if (n != 0) {
            return;
        }

        PublicationRecognitionStructure prs = new PublicationRecognitionStructure();
        prs.setDeskewThreshold(1.05);
        prs.setLuminanceCutOff(200);
        prs.setMarkThreshold(40);
        prs.setFragmentPadding(1);
        prs.setPerformDeskew(true);

        luminanceThresholdSpinner.setValue(new Integer(prs.getLuminanceCutOff()));
        markThresholdSpinner.setValue(new Integer(prs.getMarkThreshold()));
        fragmentPaddingSpinner.setValue(new Integer(prs.getFragmentPadding()));
        deskewThresholdSpinner.setValue(new Double(prs.getDeskewThreshold()));
        performDeskewCheckBox.setSelected(prs.isPerformDeskew());

        PreferencesManager.setPublicationRecognitionStructure(prs);

        try {
            PreferencesManager.savePreferences(Main.getXstream());
            Misc.showSuccessMsg(this,
                Localizer.localize("Util", "CapturePreferencesSavedSuccessfullyMessage"));
        } catch (IOException e1) {
            Misc.showErrorMsg(this, Localizer.localize("Util", "ErrorSavingPreferencesMessage"),
                Localizer.localize("Util", "ErrorSavingPreferencesTitle"));
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
            return;
        }

    }

    private void saveRecognitionSettingsButtonActionPerformed(ActionEvent e) {

        // set recognition settings
        PublicationRecognitionStructure prs =
            PreferencesManager.getPublicationRecognitionStructure();
        prs.setLuminanceCutOff((Integer) luminanceThresholdSpinner.getValue());
        prs.setMarkThreshold((Integer) markThresholdSpinner.getValue());
        prs.setFragmentPadding((Integer) fragmentPaddingSpinner.getValue());
        prs.setDeskewThreshold((Double) deskewThresholdSpinner.getValue());
        prs.setPerformDeskew(performDeskewCheckBox.isSelected());

        try {
            PreferencesManager.savePreferences(Main.getXstream());
            Misc.showSuccessMsg(this,
                Localizer.localize("Util", "CapturePreferencesSavedSuccessfullyMessage"));
        } catch (IOException e1) {
            Misc.showErrorMsg(this, Localizer.localize("Util", "ErrorSavingPreferencesMessage"),
                Localizer.localize("Util", "ErrorSavingPreferencesTitle"));
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
            return;
        }

    }

    private void saveFieldnameSettingsButtonActionPerformed(ActionEvent e) {

        // restore the fieldname duplicate presets
        FieldnameDuplicatePresets fdp = PreferencesManager.getFieldnameDupliatePresets();
        fdp.setFieldname(defaultFieldnamePrefixTextField.getText().trim());
        fdp.setCounterStart((Integer) fieldnameCounterSpinner.getValue());
        fdp.setHorizontalDuplicates((Integer) horizontalDuplicatesSpinner.getValue());
        fdp.setVerticalDuplicates((Integer) verticalDuplicatesSpinner.getValue());
        fdp.setHorizontalSpacing((Integer) horizontalSpacingSpinner.getValue());
        fdp.setVerticalSpacing((Integer) verticalSpacingSpinner.getValue());
        if (tblrButton.isSelected()) {
            fdp.setNamingDirection(FieldnameDuplicatePresets.DIRECTION_TOP_TO_BOTTOM_LEFT_TO_RIGHT);
        } else {
            fdp.setNamingDirection(FieldnameDuplicatePresets.DIRECTION_LEFT_TO_RIGHT_TOP_TO_BOTTOM);
        }
        try {
            PreferencesManager.savePreferences(Main.getXstream());
            Misc.showSuccessMsg(this,
                Localizer.localize("Util", "CapturePreferencesSavedSuccessfullyMessage"));
        } catch (IOException e1) {
            Misc.showErrorMsg(this, Localizer.localize("Util", "ErrorSavingPreferencesMessage"),
                Localizer.localize("Util", "ErrorSavingPreferencesTitle"));
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
            return;
        }
    }

    private void resetFieldnameSettingsToDefaultsButtonActionPerformed(ActionEvent e) {

        String message = Localizer
            .localize("Util", "CapturePreferencesConfirmRestoreDefaultNamingSettingsMessage");
        int n = JOptionPane.showConfirmDialog(null, message, Localizer
                .localize("Util", "CapturePreferencesConfirmRestoreDefaultNamingSettingsTitle"),
            JOptionPane.YES_NO_OPTION);

        if (n != 0) {
            return;
        }

        PreferencesManager.setFieldnameDuplicatePresets(null);
        FieldnameDuplicatePresets fdp = PreferencesManager.getFieldnameDupliatePresets();
        defaultFieldnamePrefixTextField.setText(fdp.getFieldname());
        fieldnameCounterSpinner.setValue(fdp.getCounterStart());
        horizontalDuplicatesSpinner.setValue(fdp.getHorizontalDuplicates());
        verticalDuplicatesSpinner.setValue(fdp.getVerticalDuplicates());
        horizontalSpacingSpinner.setValue(fdp.getHorizontalSpacing());
        verticalSpacingSpinner.setValue(fdp.getVerticalSpacing());

        if (fdp.getNamingDirection()
            == FieldnameDuplicatePresets.DIRECTION_TOP_TO_BOTTOM_LEFT_TO_RIGHT) {
            lrtbButton.setSelected(false);
            tblrButton.setSelected(true);
        }

        if (fdp.getNamingDirection()
            == FieldnameDuplicatePresets.DIRECTION_LEFT_TO_RIGHT_TOP_TO_BOTTOM) {
            tblrButton.setSelected(false);
            lrtbButton.setSelected(true);
        }

        try {
            PreferencesManager.savePreferences(Main.getXstream());
            Misc.showSuccessMsg(this,
                Localizer.localize("Util", "CapturePreferencesSavedSuccessfullyMessage"));
        } catch (IOException e1) {
            Misc.showErrorMsg(this, Localizer.localize("Util", "ErrorSavingPreferencesMessage"),
                Localizer.localize("Util", "ErrorSavingPreferencesTitle"));
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
            return;
        }



    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        fieldNameDefaultsHeadingLabel = new JLabel();
        panel5 = new JPanel();
        panel4 = new JPanel();
        fieldnamePrefixLabel = new JLabel();
        defaultFieldnamePrefixTextField = new JTextField();
        counterStartsAtLabel = new JLabel();
        fieldnameCounterSpinner = new JSpinner();
        horizontalDuplicatesLabel = new JLabel();
        horizontalDuplicatesSpinner = new JSpinner();
        verticalDuplicatesLabel = new JLabel();
        verticalDuplicatesSpinner = new JSpinner();
        horizontalSpacingLabel = new JLabel();
        horizontalSpacingSpinner = new JSpinner();
        verticalSpacingLabel = new JLabel();
        verticalSpacingSpinner = new JSpinner();
        panel12 = new JPanel();
        namingDirectionLabel = new JLabel();
        tblrButton = new JRadioButton();
        lrtbButton = new JRadioButton();
        panel11 = new JPanel();
        resetFieldnameSettingsToDefaultsButton = new JButton();
        saveFieldnameSettingsButton = new JButton();
        panel3 = new JPanel();
        recognitionDefaultsHeadingLabel = new JLabel();
        panel7 = new JPanel();
        panel8 = new JPanel();
        panel9 = new JPanel();
        luminanceLabel = new JLabel();
        luminanceThresholdSpinner = new JSpinner();
        markThresholdLabel = new JLabel();
        markThresholdSpinner = new JSpinner();
        deskewThresholdLabel = new JLabel();
        deskewThresholdSpinner = new JSpinner();
        fragmentPaddingLabel = new JLabel();
        fragmentPaddingSpinner = new JSpinner();
        panel2 = new JPanel();
        performDeskewCheckBox = new JCheckBox();
        panel10 = new JPanel();
        resetRecognitionSettingsToDefaultsButton = new JButton();
        saveRecognitionSettingsButton = new JButton();

        //======== this ========
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {35, 0, 35, 0, 35, 0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights =
            new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0E-4};

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- fieldNameDefaultsHeadingLabel ----
            fieldNameDefaultsHeadingLabel.setFont(UIManager.getFont("Label.font"));
            fieldNameDefaultsHeadingLabel.setText(
                Localizer.localize("Util", "CapturePreferencesFieldNameDefaultsHeadingLabel"));
            panel1.add(fieldNameDefaultsHeadingLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //======== panel5 ========
            {
                panel5.setOpaque(false);
                panel5.setBorder(new MatteBorder(0, 0, 1, 0, Color.lightGray));
                panel5.setLayout(new BorderLayout());
            }
            panel1.add(panel5,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel4 ========
        {
            panel4.setOpaque(false);
            panel4.setBorder(null);
            panel4.setLayout(new GridBagLayout());
            ((GridBagLayout) panel4.getLayout()).columnWidths = new int[] {0, 145, 25, 0, 140, 0};
            ((GridBagLayout) panel4.getLayout()).rowHeights = new int[] {35, 35, 30, 0};
            ((GridBagLayout) panel4.getLayout()).columnWeights =
                new double[] {0.0, 1.0, 0.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel4.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};

            //---- fieldnamePrefixLabel ----
            fieldnamePrefixLabel.setFont(UIManager.getFont("Label.font"));
            fieldnamePrefixLabel
                .setText(Localizer.localize("Util", "CapturePreferencesFieldNamePrefixLabel"));
            panel4.add(fieldnamePrefixLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

            //---- defaultFieldnamePrefixTextField ----
            defaultFieldnamePrefixTextField.setFont(UIManager.getFont("TextField.font"));
            defaultFieldnamePrefixTextField.setText("fieldname");
            panel4.add(defaultFieldnamePrefixTextField,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

            //---- counterStartsAtLabel ----
            counterStartsAtLabel.setFont(UIManager.getFont("Label.font"));
            counterStartsAtLabel
                .setText(Localizer.localize("Util", "CapturePreferencesCounterStartsAtLabel"));
            panel4.add(counterStartsAtLabel,
                new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

            //---- fieldnameCounterSpinner ----
            fieldnameCounterSpinner.setModel(new SpinnerNumberModel(1, 0, 9999999, 1));
            fieldnameCounterSpinner.setFont(UIManager.getFont("Spinner.font"));
            panel4.add(fieldnameCounterSpinner,
                new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- horizontalDuplicatesLabel ----
            horizontalDuplicatesLabel.setFont(UIManager.getFont("Label.font"));
            horizontalDuplicatesLabel
                .setText(Localizer.localize("Util", "CapturePreferencesHorizontalDuplicatesLabel"));
            panel4.add(horizontalDuplicatesLabel,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

            //---- horizontalDuplicatesSpinner ----
            horizontalDuplicatesSpinner.setModel(new SpinnerNumberModel(1, 0, 1000, 1));
            horizontalDuplicatesSpinner.setFont(UIManager.getFont("Spinner.font"));
            panel4.add(horizontalDuplicatesSpinner,
                new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

            //---- verticalDuplicatesLabel ----
            verticalDuplicatesLabel.setFont(UIManager.getFont("Label.font"));
            verticalDuplicatesLabel
                .setText(Localizer.localize("Util", "CapturePreferencesVerticalDuplicatesLabel"));
            panel4.add(verticalDuplicatesLabel,
                new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

            //---- verticalDuplicatesSpinner ----
            verticalDuplicatesSpinner.setModel(new SpinnerNumberModel(1, 0, 1000, 1));
            verticalDuplicatesSpinner.setFont(UIManager.getFont("Spinner.font"));
            panel4.add(verticalDuplicatesSpinner,
                new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- horizontalSpacingLabel ----
            horizontalSpacingLabel.setFont(UIManager.getFont("Label.font"));
            horizontalSpacingLabel
                .setText(Localizer.localize("Util", "CapturePreferencesHorizontalSpacingLabel"));
            panel4.add(horizontalSpacingLabel,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

            //---- horizontalSpacingSpinner ----
            horizontalSpacingSpinner.setModel(new SpinnerNumberModel(20, 0, 5000, 1));
            horizontalSpacingSpinner.setFont(UIManager.getFont("Spinner.font"));
            panel4.add(horizontalSpacingSpinner,
                new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- verticalSpacingLabel ----
            verticalSpacingLabel.setFont(UIManager.getFont("Label.font"));
            verticalSpacingLabel
                .setText(Localizer.localize("Util", "CapturePreferencesVerticalSpacingLabel"));
            panel4.add(verticalSpacingLabel,
                new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

            //---- verticalSpacingSpinner ----
            verticalSpacingSpinner.setModel(new SpinnerNumberModel(20, 0, 5000, 1));
            verticalSpacingSpinner.setFont(UIManager.getFont("Spinner.font"));
            panel4.add(verticalSpacingSpinner,
                new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel4, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel12 ========
        {
            panel12.setOpaque(false);
            panel12.setLayout(new GridBagLayout());
            ((GridBagLayout) panel12.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
            ((GridBagLayout) panel12.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel12.getLayout()).columnWeights =
                new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel12.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- namingDirectionLabel ----
            namingDirectionLabel.setFont(UIManager.getFont("Label.font"));
            namingDirectionLabel
                .setText(Localizer.localize("Util", "CapturePreferencesNamingDirectionLabel"));
            panel12.add(namingDirectionLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

            //---- tblrButton ----
            tblrButton.setFont(UIManager.getFont("RadioButton.font"));
            tblrButton.setSelected(true);
            tblrButton.setBackground(null);
            tblrButton.setOpaque(false);
            tblrButton.setText(Localizer.localize("Util", "CapturePreferencesTBLRButtonText"));
            panel12.add(tblrButton,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- lrtbButton ----
            lrtbButton.setFont(UIManager.getFont("RadioButton.font"));
            lrtbButton.setBackground(null);
            lrtbButton.setOpaque(false);
            lrtbButton.setText(Localizer.localize("Util", "CapturePreferencesLRTBButtonText"));
            panel12.add(lrtbButton,
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
        }
        add(panel12, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel11 ========
        {
            panel11.setOpaque(false);
            panel11.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, Color.lightGray),
                new EmptyBorder(5, 0, 5, 0)));
            panel11.setLayout(new GridBagLayout());
            ((GridBagLayout) panel11.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
            ((GridBagLayout) panel11.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel11.getLayout()).columnWeights =
                new double[] {0.0, 1.0, 0.0, 1.0E-4};
            ((GridBagLayout) panel11.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- resetFieldnameSettingsToDefaultsButton ----
            resetFieldnameSettingsToDefaultsButton.setFocusPainted(false);
            resetFieldnameSettingsToDefaultsButton.setFont(UIManager.getFont("Button.font"));
            resetFieldnameSettingsToDefaultsButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
            resetFieldnameSettingsToDefaultsButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    resetFieldnameSettingsToDefaultsButtonActionPerformed(e);
                }
            });
            resetFieldnameSettingsToDefaultsButton.setText(
                Localizer.localize("Util", "CapturePreferencesRestoreDefaultNamingButtonText"));
            panel11.add(resetFieldnameSettingsToDefaultsButton,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- saveFieldnameSettingsButton ----
            saveFieldnameSettingsButton.setFocusPainted(false);
            saveFieldnameSettingsButton.setFont(UIManager.getFont("Button.font"));
            saveFieldnameSettingsButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
            saveFieldnameSettingsButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    saveFieldnameSettingsButtonActionPerformed(e);
                }
            });
            saveFieldnameSettingsButton.setText(Localizer
                .localize("Util", "CapturePreferencesSaveNamingAndDuplicateSettingsButtonText"));
            panel11.add(saveFieldnameSettingsButton,
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel11, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel3 ========
        {
            panel3.setOpaque(false);
            panel3.setLayout(new GridBagLayout());
            ((GridBagLayout) panel3.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) panel3.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel3.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel3.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- recognitionDefaultsHeadingLabel ----
            recognitionDefaultsHeadingLabel.setFont(UIManager.getFont("Label.font"));
            recognitionDefaultsHeadingLabel.setText(
                Localizer.localize("Util", "CapturePreferencesRecognitionDefaultsHeadingLabel"));
            panel3.add(recognitionDefaultsHeadingLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //======== panel7 ========
            {
                panel7.setOpaque(false);
                panel7.setBorder(new MatteBorder(0, 0, 1, 0, Color.lightGray));
                panel7.setLayout(new BorderLayout());
            }
            panel3.add(panel7,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel3, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel8 ========
        {
            panel8.setOpaque(false);
            panel8.setBorder(null);
            panel8.setLayout(new GridBagLayout());
            ((GridBagLayout) panel8.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel8.getLayout()).rowHeights = new int[] {0, 35, 30, 0};
            ((GridBagLayout) panel8.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel8.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};

            //======== panel9 ========
            {
                panel9.setOpaque(false);
                panel9.setLayout(new GridBagLayout());
                ((GridBagLayout) panel9.getLayout()).columnWidths = new int[] {0, 95, 25, 0, 90, 0};
                ((GridBagLayout) panel9.getLayout()).rowHeights = new int[] {35, 30, 0};
                ((GridBagLayout) panel9.getLayout()).columnWeights =
                    new double[] {0.0, 1.0, 0.0, 0.0, 1.0, 1.0E-4};
                ((GridBagLayout) panel9.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

                //---- luminanceLabel ----
                luminanceLabel.setFont(UIManager.getFont("Label.font"));
                luminanceLabel
                    .setText(Localizer.localize("Util", "CapturePreferencesLuminanceLabel"));
                panel9.add(luminanceLabel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                        GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                //---- luminanceThresholdSpinner ----
                luminanceThresholdSpinner.setModel(new SpinnerNumberModel(200, 0, 255, 1));
                luminanceThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
                panel9.add(luminanceThresholdSpinner,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

                //---- markThresholdLabel ----
                markThresholdLabel.setFont(UIManager.getFont("Label.font"));
                markThresholdLabel
                    .setText(Localizer.localize("Util", "CapturePreferencesMarkThresholdLabel"));
                panel9.add(markThresholdLabel,
                    new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                        GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                //---- markThresholdSpinner ----
                markThresholdSpinner.setModel(new SpinnerNumberModel(40, -10000, 10000, 1));
                markThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
                panel9.add(markThresholdSpinner,
                    new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                //---- deskewThresholdLabel ----
                deskewThresholdLabel.setFont(UIManager.getFont("Label.font"));
                deskewThresholdLabel
                    .setText(Localizer.localize("Util", "CapturePreferencesDeskewThresholdLabel"));
                panel9.add(deskewThresholdLabel,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                        GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

                //---- deskewThresholdSpinner ----
                deskewThresholdSpinner.setModel(new SpinnerNumberModel(1.05, 0.0, 90.0, 0.01));
                deskewThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
                panel9.add(deskewThresholdSpinner,
                    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                //---- fragmentPaddingLabel ----
                fragmentPaddingLabel.setFont(UIManager.getFont("Label.font"));
                fragmentPaddingLabel
                    .setText(Localizer.localize("Util", "CapturePreferencesFragmentPaddingLabel"));
                panel9.add(fragmentPaddingLabel,
                    new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                        GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

                //---- fragmentPaddingSpinner ----
                fragmentPaddingSpinner.setModel(new SpinnerNumberModel(1, 0, 200, 1));
                fragmentPaddingSpinner.setFont(UIManager.getFont("Spinner.font"));
                panel9.add(fragmentPaddingSpinner,
                    new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            }
            panel8.add(panel9,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

            //======== panel2 ========
            {
                panel2.setOpaque(false);
                panel2.setLayout(new GridBagLayout());
                ((GridBagLayout) panel2.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                ((GridBagLayout) panel2.getLayout()).rowHeights = new int[] {30, 0};
                ((GridBagLayout) panel2.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 1.0, 1.0E-4};
                ((GridBagLayout) panel2.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                //---- performDeskewCheckBox ----
                performDeskewCheckBox.setSelected(true);
                performDeskewCheckBox.setFocusPainted(false);
                performDeskewCheckBox.setOpaque(false);
                performDeskewCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                performDeskewCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
                performDeskewCheckBox
                    .setText(Localizer.localize("Util", "CapturePreferencesPerformDeskewCheckBox"));
                panel2.add(performDeskewCheckBox,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
            }
            panel8.add(panel2,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //======== panel10 ========
            {
                panel10.setOpaque(false);
                panel10.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, Color.lightGray),
                    new EmptyBorder(5, 0, 0, 0)));
                panel10.setLayout(new GridBagLayout());
                ((GridBagLayout) panel10.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                ((GridBagLayout) panel10.getLayout()).rowHeights = new int[] {30, 0};
                ((GridBagLayout) panel10.getLayout()).columnWeights =
                    new double[] {0.0, 1.0, 0.0, 1.0E-4};
                ((GridBagLayout) panel10.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                //---- resetRecognitionSettingsToDefaultsButton ----
                resetRecognitionSettingsToDefaultsButton.setFont(UIManager.getFont("Button.font"));
                resetRecognitionSettingsToDefaultsButton.setIcon(new ImageIcon(getClass()
                    .getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
                resetRecognitionSettingsToDefaultsButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        resetRecognitionSettingsToDefaultsButtonActionPerformed(e);
                    }
                });
                resetRecognitionSettingsToDefaultsButton.setText(Localizer.localize("Util",
                    "CapturePreferencesRestoreDefaultRecognitionSettingsButtonText"));
                panel10.add(resetRecognitionSettingsToDefaultsButton,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                //---- saveRecognitionSettingsButton ----
                saveRecognitionSettingsButton.setFocusPainted(false);
                saveRecognitionSettingsButton.setFont(UIManager.getFont("Button.font"));
                saveRecognitionSettingsButton.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
                saveRecognitionSettingsButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        saveRecognitionSettingsButtonActionPerformed(e);
                    }
                });
                saveRecognitionSettingsButton.setText(Localizer
                    .localize("Util", "CapturePreferencesSaveRecognitionSettingsButtonText"));
                panel10.add(saveRecognitionSettingsButton,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            }
            panel8.add(panel10,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel8, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        //---- namingDirectionButtonGroup ----
        ButtonGroup namingDirectionButtonGroup = new ButtonGroup();
        namingDirectionButtonGroup.add(tblrButton);
        namingDirectionButtonGroup.add(lrtbButton);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel fieldNameDefaultsHeadingLabel;
    private JPanel panel5;
    private JPanel panel4;
    private JLabel fieldnamePrefixLabel;
    private JTextField defaultFieldnamePrefixTextField;
    private JLabel counterStartsAtLabel;
    private JSpinner fieldnameCounterSpinner;
    private JLabel horizontalDuplicatesLabel;
    private JSpinner horizontalDuplicatesSpinner;
    private JLabel verticalDuplicatesLabel;
    private JSpinner verticalDuplicatesSpinner;
    private JLabel horizontalSpacingLabel;
    private JSpinner horizontalSpacingSpinner;
    private JLabel verticalSpacingLabel;
    private JSpinner verticalSpacingSpinner;
    private JPanel panel12;
    private JLabel namingDirectionLabel;
    private JRadioButton tblrButton;
    private JRadioButton lrtbButton;
    private JPanel panel11;
    private JButton resetFieldnameSettingsToDefaultsButton;
    private JButton saveFieldnameSettingsButton;
    private JPanel panel3;
    private JLabel recognitionDefaultsHeadingLabel;
    private JPanel panel7;
    private JPanel panel8;
    private JPanel panel9;
    private JLabel luminanceLabel;
    private JSpinner luminanceThresholdSpinner;
    private JLabel markThresholdLabel;
    private JSpinner markThresholdSpinner;
    private JLabel deskewThresholdLabel;
    private JSpinner deskewThresholdSpinner;
    private JLabel fragmentPaddingLabel;
    private JSpinner fragmentPaddingSpinner;
    private JPanel panel2;
    private JCheckBox performDeskewCheckBox;
    private JPanel panel10;
    private JButton resetRecognitionSettingsToDefaultsButton;
    private JButton saveRecognitionSettingsButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
