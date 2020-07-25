package com.ebstrada.formreturn.manager.util.preferences.panel;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.PublicationPreferences;

public class PublisherPreferencesPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public PublisherPreferencesPanel() {
        initComponents();
        restoreSettings();
    }

    private void restoreSettings() {
        PublicationPreferences publicationPreferences =
            PreferencesManager.getPublicationPreferences();

        List<String> publicationTypes = PublicationPreferences.getPublicationTypes();
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        for (String publicationType : publicationTypes) {
            dcbm.addElement(publicationType);
        }
        defaultPublicationTypeComboBox.setModel(dcbm);
        if (publicationPreferences.getDefaultPublicationType() > 0) {
            defaultPublicationTypeComboBox
                .setSelectedIndex((publicationPreferences.getDefaultPublicationType() - 1));
        }
        collatePDFPagesCheckBox.setSelected(publicationPreferences.isCollatePDFPages());
        errorDuplicateScansCheckBox.setSelected(publicationPreferences.isErrorDuplicateScans());
    }

    private void savePublicationSettingsButtonActionPerformed(ActionEvent e) {

        if (defaultPublicationTypeComboBox.getSelectedIndex() == -1) {
            return;
        }

        PublicationPreferences publicationPreferences =
            PreferencesManager.getPublicationPreferences();
        publicationPreferences
            .setDefaultPublicationType((defaultPublicationTypeComboBox.getSelectedIndex() + 1));
        publicationPreferences.setCollatePDFPages(collatePDFPagesCheckBox.isSelected());
        publicationPreferences.setErrorDuplicateScans(errorDuplicateScansCheckBox.isSelected());

        try {
            PreferencesManager.savePreferences(Main.getXstream());
            String message =
                Localizer.localize("Util", "PublishingPreferencesSavedSuccessfullyMessage");
            Misc.showSuccessMsg(this, message);
        } catch (IOException e1) {
            Misc.showErrorMsg(this, Localizer.localize("Util", "ErrorSavingPreferencesMessage"),
                Localizer.localize("Util", "ErrorSavingPreferencesTitle"));
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
            return;
        }

    }

    private void restoreDefaultsButtonActionPerformed(ActionEvent e) {
        defaultPublicationTypeComboBox.setSelectedIndex(0);
        collatePDFPagesCheckBox.setSelected(true);
        errorDuplicateScansCheckBox.setSelected(false);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        publicationOptionsHeadingLabel = new JLabel();
        panel5 = new JPanel();
        panel2 = new JPanel();
        defaultPublicationTypeLabel = new JLabel();
        defaultPublicationTypeComboBox = new JComboBox();
        panel4 = new JPanel();
        collatePDFPagesCheckBox = new JCheckBox();
        panel6 = new JPanel();
        errorDuplicateScansCheckBox = new JCheckBox();
        panel3 = new JPanel();
        restoreDefaultsButton = new JButton();
        savePublicationSettingsButton = new JButton();

        //======== this ========
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {35, 35, 35, 35, 0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- publicationOptionsHeadingLabel ----
            publicationOptionsHeadingLabel.setFont(UIManager.getFont("Label.font"));
            publicationOptionsHeadingLabel.setText(
                Localizer.localize("Util", "PublishingPreferencesPublicationOptionsHeadingLabel"));
            panel1.add(publicationOptionsHeadingLabel,
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

        //======== panel2 ========
        {
            panel2.setOpaque(false);
            panel2.setLayout(new GridBagLayout());
            ((GridBagLayout) panel2.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
            ((GridBagLayout) panel2.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel2.getLayout()).columnWeights =
                new double[] {1.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel2.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- defaultPublicationTypeLabel ----
            defaultPublicationTypeLabel.setFont(UIManager.getFont("Label.font"));
            defaultPublicationTypeLabel.setText(
                Localizer.localize("Util", "PublishingPreferencesDefaultPublicationTypeLabel"));
            panel2.add(defaultPublicationTypeLabel,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- defaultPublicationTypeComboBox ----
            defaultPublicationTypeComboBox.setFont(UIManager.getFont("ComboBox.font"));
            panel2.add(defaultPublicationTypeComboBox,
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel4 ========
        {
            panel4.setOpaque(false);
            panel4.setLayout(new GridBagLayout());
            ((GridBagLayout) panel4.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
            ((GridBagLayout) panel4.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel4.getLayout()).columnWeights =
                new double[] {1.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel4.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- collatePDFPagesCheckBox ----
            collatePDFPagesCheckBox.setFont(UIManager.getFont("CheckBox.font"));
            collatePDFPagesCheckBox.setOpaque(false);
            collatePDFPagesCheckBox.setText(
                Localizer.localize("Util", "PublishingPreferencesCollatePDFPagesCheckBox"));
            panel4.add(collatePDFPagesCheckBox,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
        }
        add(panel4, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel6 ========
        {
            panel6.setOpaque(false);
            panel6.setLayout(new GridBagLayout());
            ((GridBagLayout) panel6.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
            ((GridBagLayout) panel6.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout) panel6.getLayout()).columnWeights =
                new double[] {1.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel6.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

            //---- errorDuplicateScansCheckBox ----
            errorDuplicateScansCheckBox.setText("Error Duplicate Scans");
            errorDuplicateScansCheckBox.setOpaque(false);
            errorDuplicateScansCheckBox.setFont(UIManager.getFont("CheckBox.font"));
            errorDuplicateScansCheckBox.setText(
                Localizer.localize("Util", "PublishingPreferencesErrorDuplicateScansCheckBox"));
            panel6.add(errorDuplicateScansCheckBox,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
        }
        add(panel6, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel3 ========
        {
            panel3.setOpaque(false);
            panel3.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, Color.lightGray),
                new EmptyBorder(5, 0, 0, 0)));
            panel3.setLayout(new GridBagLayout());
            ((GridBagLayout) panel3.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
            ((GridBagLayout) panel3.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel3.getLayout()).columnWeights =
                new double[] {0.0, 1.0, 0.0, 1.0E-4};
            ((GridBagLayout) panel3.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- restoreDefaultsButton ----
            restoreDefaultsButton.setFont(UIManager.getFont("Button.font"));
            restoreDefaultsButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
            restoreDefaultsButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    restoreDefaultsButtonActionPerformed(e);
                }
            });
            restoreDefaultsButton.setText(
                Localizer.localize("Util", "PublishingPreferencesRestoreDefaultsButtonText"));
            panel3.add(restoreDefaultsButton,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- savePublicationSettingsButton ----
            savePublicationSettingsButton.setFont(UIManager.getFont("Button.font"));
            savePublicationSettingsButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
            savePublicationSettingsButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    savePublicationSettingsButtonActionPerformed(e);
                }
            });
            savePublicationSettingsButton.setText(Localizer
                .localize("Util", "PublishingPreferencesSavePublicationSettingsButtonText"));
            panel3.add(savePublicationSettingsButton,
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel3, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel publicationOptionsHeadingLabel;
    private JPanel panel5;
    private JPanel panel2;
    private JLabel defaultPublicationTypeLabel;
    private JComboBox defaultPublicationTypeComboBox;
    private JPanel panel4;
    private JCheckBox collatePDFPagesCheckBox;
    private JPanel panel6;
    private JCheckBox errorDuplicateScansCheckBox;
    private JPanel panel3;
    private JButton restoreDefaultsButton;
    private JButton savePublicationSettingsButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
