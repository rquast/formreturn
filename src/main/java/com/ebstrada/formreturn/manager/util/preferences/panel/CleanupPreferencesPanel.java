package com.ebstrada.formreturn.manager.util.preferences.panel;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ApplicationStatePreferences;

public class CleanupPreferencesPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public CleanupPreferencesPanel() {
        initComponents();
        restoreSettings();
    }

    private void restoreSettings() {
        ApplicationStatePreferences applicationState = PreferencesManager.getApplicationState();
        blurCheckBox.setSelected(applicationState.isBlurIncomingImages());
    }

    private void blurCheckBoxActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ApplicationStatePreferences applicationState =
                    PreferencesManager.getApplicationState();
                applicationState.setBlurIncomingImages(blurCheckBox.isSelected());
                try {
                    PreferencesManager.savePreferences(Main.getXstream());
                } catch (IOException e1) {
                    Misc.printStackTrace(e1);
                }
            }
        });
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        cleanupPreferencesPanel = new JPanel();
        blurIncomingImagesHeadingLabel = new JLabel();
        linePanel1 = new JPanel();
        blurCheckBoxPanel = new JPanel();
        blurDescriptionLabel = new JLabel();
        blurCheckBox = new JCheckBox();

        //======== this ========
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {35, 0, 0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};

        //======== cleanupPreferencesPanel ========
        {
            cleanupPreferencesPanel.setOpaque(false);
            cleanupPreferencesPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) cleanupPreferencesPanel.getLayout()).columnWidths =
                new int[] {0, 0, 0};
            ((GridBagLayout) cleanupPreferencesPanel.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) cleanupPreferencesPanel.getLayout()).columnWeights =
                new double[] {0.0, 1.0, 1.0E-4};
            ((GridBagLayout) cleanupPreferencesPanel.getLayout()).rowWeights =
                new double[] {0.0, 1.0E-4};

            //---- blurIncomingImagesHeadingLabel ----
            blurIncomingImagesHeadingLabel.setFont(UIManager.getFont("Label.font"));
            blurIncomingImagesHeadingLabel.setText(
                Localizer.localize("Util", "CapturePreferencesBlurIncomingImagesHeadingLabel"));
            cleanupPreferencesPanel.add(blurIncomingImagesHeadingLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //======== linePanel1 ========
            {
                linePanel1.setOpaque(false);
                linePanel1.setBorder(new MatteBorder(0, 0, 1, 0, Color.lightGray));
                linePanel1.setLayout(new BorderLayout());
            }
            cleanupPreferencesPanel.add(linePanel1,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(cleanupPreferencesPanel,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //======== blurCheckBoxPanel ========
        {
            blurCheckBoxPanel.setOpaque(false);
            blurCheckBoxPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
            blurCheckBoxPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) blurCheckBoxPanel.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) blurCheckBoxPanel.getLayout()).rowHeights = new int[] {45, 30, 0};
            ((GridBagLayout) blurCheckBoxPanel.getLayout()).columnWeights =
                new double[] {1.0, 1.0E-4};
            ((GridBagLayout) blurCheckBoxPanel.getLayout()).rowWeights =
                new double[] {0.0, 0.0, 1.0E-4};

            //---- blurDescriptionLabel ----
            blurDescriptionLabel.setFont(UIManager.getFont("Label.font"));
            blurDescriptionLabel
                .setText(Localizer.localize("Util", "CapturePreferencesBlurDescriptionLabel"));
            blurCheckBoxPanel.add(blurDescriptionLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 15, 0), 0, 0));

            //---- blurCheckBox ----
            blurCheckBox.setFont(UIManager.getFont("CheckBox.font"));
            blurCheckBox.setOpaque(false);
            blurCheckBox.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    blurCheckBoxActionPerformed(e);
                }
            });
            blurCheckBox.setText(Localizer.localize("Util", "CapturePreferencesBlurCheckBoxText"));
            blurCheckBoxPanel.add(blurCheckBox,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(blurCheckBoxPanel,
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel cleanupPreferencesPanel;
    private JLabel blurIncomingImagesHeadingLabel;
    private JPanel linePanel1;
    private JPanel blurCheckBoxPanel;
    private JLabel blurDescriptionLabel;
    private JCheckBox blurCheckBox;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
