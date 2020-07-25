package com.ebstrada.formreturn.manager.util.preferences.panel;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.font.FontLocalesImpl;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.util.AvailableLanguages;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.SoftwareUpdateManager;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ApplicationStatePreferences;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.filefilter.FilenameExtensionFilter;

public class GeneralPreferencesPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private AvailableLanguages availableLanguages;

    public GeneralPreferencesPanel() {
        initComponents();
        restoreSettings();
    }

    private void restoreSettings() {

        this.availableLanguages = new AvailableLanguages();

        ApplicationStatePreferences applicationState = PreferencesManager.getApplicationState();
        // disableMICCheckBox.setSelected(!(applicationState.isMultipleInstanceCheckerEnabled()));
        disableConfirmExitCheckBox.setSelected(!(applicationState.isConfirmExit()));
        fontDirectoriesList.setListData(PreferencesManager.getFontPaths().toArray());
        SoftwareUpdateManager sum = Main.getInstance().getSoftwareUpdateManager();
        checkForUpdatesCheckBox.setSelected(sum.isUpdateEnabled());

        // restore the language combo box.
        languageComboBox.setModel(availableLanguages.getLanguageComboBoxModel());
        String locale = applicationState.getLocale();
        int i = 0;
        for (FontLocalesImpl fontLocale : FontLocalesImpl.values()) {
            if (fontLocale.name().equals(locale)) {
                languageComboBox.setSelectedIndex(i);
                break;
            }
            ++i;
        }

    }

    private void clearRecentlyOpenedFilesButtonActionPerformed(ActionEvent e) {
        PreferencesManager.resetRecentFiles();
        Main.getInstance().resetRecentFileStack();
        try {
            PreferencesManager.savePreferences(Main.getXstream());
            Misc.showSuccessMsg(this, Localizer
                .localize("Util", "GeneralPreferencesRecentFileListClearedSuccessMessage"));
        } catch (IOException e1) {
            Main.applicationExceptionLog.error(
                Localizer.localize("Util", "GeneralPreferencesRecentFileListClearedFailureMessage"),
                e1);
        }
    }

    private void disableConfirmExitCheckBoxActionPerformed(ActionEvent e) {

        ApplicationStatePreferences applicationState = PreferencesManager.getApplicationState();
        applicationState.setConfirmExit(!(disableConfirmExitCheckBox.isSelected()));
        try {
            PreferencesManager.savePreferences(Main.getXstream());
        } catch (IOException e1) {
            Main.applicationExceptionLog.error(Localizer
                .localize("Util", "GeneralPreferencesDisableConfirmExitUpdateFailureMessage"), e1);
        }
    }

    private void removeFontDirectoryButtonActionPerformed(ActionEvent e) {

        String removeFontPath = (String) fontDirectoriesList.getSelectedValue();

        if (removeFontPath != null) {

            Object[] options =
                {Localizer.localize("Util", "Yes"), Localizer.localize("Util", "No")};

            String msg =
                Localizer.localize("Util", "GeneralPreferencesConfirmRemoveFontPathMessage") + "\n";
            msg += removeFontPath;

            int result = JOptionPane.showOptionDialog(this, msg,
                Localizer.localize("Util", "GeneralPreferencesConfirmRemoveFontPathTitle"),
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

            if (result != 1) {

                PreferencesManager.removeFontPath(removeFontPath);
                try {
                    PreferencesManager.savePreferences(Main.getXstream());
                } catch (IOException e1) {
                    Main.applicationExceptionLog.error(Localizer
                        .localize("Util", "GeneralPreferencesRemoveFontPathFailureMessage"), e1);
                }

                fontDirectoriesList.setListData(PreferencesManager.getFontPaths().toArray());

            }

        }

    }

    private void addFontDirectoryButtonActionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle(
            Localizer.localize("Util", "GeneralPreferencesAddFontDirectoryDialogTitle"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        chooser.rescanCurrentDirectory();

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String addFontPath = chooser.getSelectedFile().getPath();
            PreferencesManager.addFontPath(addFontPath);
            try {
                PreferencesManager.savePreferences(Main.getXstream());
            } catch (IOException e1) {
                Main.applicationExceptionLog.error(
                    Localizer.localize("Util", "GeneralPreferencesAddFontDirectoryFailureMessage"),
                    e1);
            }
            fontDirectoriesList.setListData(PreferencesManager.getFontPaths().toArray());
        }
    }

    private void showSystemFontSelectionDialogButtonActionPerformed(ActionEvent e) {

        File file = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();
        filter.addExtension("ttf");

        FileDialog fd = new FileDialog(Main.getInstance(),
            Localizer.localize("Util", "GeneralPreferencesSystemFontSelectionDialogTitle"),
            FileDialog.LOAD);
        fd.setFilenameFilter(filter);

        File lastDir = null;
        if (Globals.getLastDirectory() != null) {

            lastDir = new File(Globals.getLastDirectory());

            if (!(lastDir.exists())) {
                lastDir = null;
            }

        }

        if (lastDir == null) {
            lastDir = new File(System.getProperty("user.home"));
        }

        try {
            fd.setDirectory(lastDir.getCanonicalPath());
        } catch (IOException e1) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
            return;
        }
        fd.setLocationByPlatform(false);
        fd.setLocationRelativeTo(Main.getInstance());
        fd.setModal(true);
        fd.setVisible(true);
        if (fd.getFile() != null) {
            String filename = fd.getFile();
            file = new File(fd.getDirectory() + filename);
            if (file.isDirectory()) {
                return;
            }
            try {
                Globals.setLastDirectory(file.getCanonicalPath());
            } catch (IOException ldex) {
            }
        } else {
            return;
        }

        if (file != null) {

            int result = 0;

            Object[] options =
                {Localizer.localize("Util", "Yes"), Localizer.localize("Util", "No")};

            String msg = "";
            try {
                msg = String.format(
                    Localizer.localize("Util", "GeneralPreferencesConfirmSystemFontFileMessage"),
                    file.getCanonicalPath());
            } catch (IOException e1) {
                return;
            }

            result = JOptionPane.showOptionDialog(this, msg,
                Localizer.localize("Util", "GeneralPreferencesConfirmSystemFontFileTitle"),
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

            if (result == 1) {
                return;
            }

            try {
                Main.getCachedFontManager().replaceSystemFont(file);
                Misc.showSuccessMsg(Main.getInstance(),
                    Localizer.localize("Util", "GeneralPreferencesSetSystemFontSuccessMessage"));
            } catch (Exception ex) {
                Misc.showErrorMsg(Main.getInstance(),
                    Localizer.localize("Util", "GGeneralPreferencesSetSystemFontFailureMessage"));
            }
        }

    }

    private void resetFontPathsButtonActionPerformed(ActionEvent e) {

        Object[] options = {Localizer.localize("Util", "Yes"), Localizer.localize("Util", "No")};

        String msg = Localizer
            .localize("Util", "GeneralPreferencesConfirmRestoreDefaultFontDirectoriesMessage");

        int result = JOptionPane.showOptionDialog(this, msg, Localizer
                .localize("Util", "GeneralPreferencesConfirmRestoreDefaultFontDirectoriesTitle"),
            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

        if (result != 1) {
            PreferencesManager.setFontPaths(new ArrayList<String>());
            Main.getCachedFontManager().resetFontPaths(PreferencesManager.getFontPaths());
            fontDirectoriesList.setListData(PreferencesManager.getFontPaths().toArray());
        }

    }

    private void checkForUpdatesCheckBoxActionPerformed(ActionEvent e) {
        SoftwareUpdateManager sum = Main.getInstance().getSoftwareUpdateManager();
        if (checkForUpdatesCheckBox.isSelected()) {
            sum.enableSoftwareUpdate();
        } else {
            sum.disableSoftwareUpdate();
        }
    }

    private void restoreDefaultSystemFontButtonActionPerformed(ActionEvent e) {
        int result = 0;

        Object[] options = {Localizer.localize("Util", "Cancel"), Localizer.localize("Util", "No"),
            Localizer.localize("Util", "Yes")};

        String msg =
            Localizer.localize("Util", "GeneralPreferencesChooseDefaultSystemFontFileMessage");


        result = JOptionPane.showOptionDialog(this, msg, Localizer
                .localize("Util", "GeneralPreferencesConfirmRestoreDefaultSystemFontFileTitle"),
            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

        if (result == 0) {
            return;
        }

        try {
            if (result == 1) {
                Main.getCachedFontManager().restoreDefaultSystemFont();
            } else if (result == 2) {
                Main.getCachedFontManager().restoreCJKSystemFont();
            }
            Misc.showSuccessMsg(Main.getInstance(),
                Localizer.localize("Util", "GeneralPreferencesSetSystemFontSuccessMessage"));
        } catch (Exception ex) {
            Misc.showErrorMsg(Main.getInstance(),
                Localizer.localize("Util", "GeneralPreferencesSetSystemFontFailureMessage"));
        }
    }

    private void changeLanguageButtonActionPerformed(ActionEvent e) {
        ApplicationStatePreferences applicationState = PreferencesManager.getApplicationState();

        int selectedIndex = languageComboBox.getSelectedIndex();
        int i = 0;
        for (FontLocalesImpl fontLocale : FontLocalesImpl.values()) {
            if (i == selectedIndex) {
                // set the value of the fontLocale as the locale
                applicationState.setLocale(fontLocale.name());
                break;
            }
            ++i;
        }
        try {
            PreferencesManager.savePreferences(Main.getXstream());
            Misc.showSuccessMsg(Main.getInstance(),
                Localizer.localize("Util", "GeneralPreferencesChangeLanguageSuccessMessage"));
        } catch (IOException e1) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
            Misc.showErrorMsg(Main.getInstance(),
                Localizer.localize("Util", "GeneralPreferencesChangeLanguageFailureMessage"));
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel15 = new JPanel();
        panel1 = new JPanel();
        startupHeadingLabel = new JLabel();
        panel5 = new JPanel();
        panel12 = new JPanel();
        shutdownHeadingLabel = new JLabel();
        panel13 = new JPanel();
        checkForUpdatesCheckBox = new JCheckBox();
        disableConfirmExitCheckBox = new JCheckBox();
        panel17 = new JPanel();
        panel3 = new JPanel();
        historyHeadingLabel = new JLabel();
        panel7 = new JPanel();
        panel14 = new JPanel();
        languageHeadingLabel = new JLabel();
        panel16 = new JPanel();
        panel4 = new JPanel();
        clearRecentFilesLabel = new JLabel();
        clearRecentlyOpenedFilesButton = new JButton();
        panel18 = new JPanel();
        languageComboBox = new JComboBox();
        changeLanguageButton = new JButton();
        panel2 = new JPanel();
        fontDirectoriesHeadingLabel = new JLabel();
        panel6 = new JPanel();
        panel19 = new JPanel();
        scrollPane1 = new JScrollPane();
        fontDirectoriesList = new JList();
        panel8 = new JPanel();
        resetFontPathsButton = new JButton();
        addFontDirectoryButton = new JButton();
        removeFontDirectoryButton = new JButton();
        panel9 = new JPanel();
        systemFontHeadingLabel = new JLabel();
        panel10 = new JPanel();
        panel11 = new JPanel();
        customFontFileLabel = new JLabel();
        showSystemFontSelectionDialogButton = new JButton();
        restoreDefaultSystemFontButton = new JButton();
        customFontFileInstructionLabel = new JLabel();

        //======== this ========
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {0, 0, 35, 0, 35, 35, 30, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights =
            new double[] {0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};

        //======== panel15 ========
        {
            panel15.setOpaque(false);
            panel15.setLayout(new GridBagLayout());
            ((GridBagLayout) panel15.getLayout()).columnWidths = new int[] {0, 15, 0, 0};
            ((GridBagLayout) panel15.getLayout()).rowHeights = new int[] {35, 30, 0};
            ((GridBagLayout) panel15.getLayout()).columnWeights =
                new double[] {1.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel15.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

            //======== panel1 ========
            {
                panel1.setOpaque(false);
                panel1.setLayout(new GridBagLayout());
                ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0, 0};
                ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout) panel1.getLayout()).columnWeights =
                    new double[] {0.0, 1.0, 1.0E-4};
                ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //---- startupHeadingLabel ----
                startupHeadingLabel.setFont(UIManager.getFont("Label.font"));
                startupHeadingLabel
                    .setText(Localizer.localize("Util", "GeneralPreferencesStartupHeadingLabel"));
                panel1.add(startupHeadingLabel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //======== panel5 ========
                {
                    panel5.setOpaque(false);
                    panel5.setBorder(new MatteBorder(0, 0, 1, 0, Color.lightGray));
                    panel5.setLayout(new BorderLayout());
                }
                panel1.add(panel5,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            panel15.add(panel1,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

            //======== panel12 ========
            {
                panel12.setOpaque(false);
                panel12.setLayout(new GridBagLayout());
                ((GridBagLayout) panel12.getLayout()).columnWidths = new int[] {0, 0, 0};
                ((GridBagLayout) panel12.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout) panel12.getLayout()).columnWeights =
                    new double[] {0.0, 1.0, 1.0E-4};
                ((GridBagLayout) panel12.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //---- shutdownHeadingLabel ----
                shutdownHeadingLabel.setFont(UIManager.getFont("Label.font"));
                shutdownHeadingLabel
                    .setText(Localizer.localize("Util", "GeneralPreferencesShutdownHeadingLabel"));
                panel12.add(shutdownHeadingLabel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //======== panel13 ========
                {
                    panel13.setOpaque(false);
                    panel13.setBorder(new MatteBorder(0, 0, 1, 0, Color.lightGray));
                    panel13.setLayout(new BorderLayout());
                }
                panel12.add(panel13,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            panel15.add(panel12,
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

            //---- checkForUpdatesCheckBox ----
            checkForUpdatesCheckBox.setFont(UIManager.getFont("CheckBox.font"));
            checkForUpdatesCheckBox.setFocusPainted(false);
            checkForUpdatesCheckBox.setOpaque(false);
            checkForUpdatesCheckBox.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    checkForUpdatesCheckBoxActionPerformed(e);
                }
            });
            checkForUpdatesCheckBox
                .setText(Localizer.localize("Util", "GeneralPreferencesCheckForUpdatesCheckBox"));
            panel15.add(checkForUpdatesCheckBox,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- disableConfirmExitCheckBox ----
            disableConfirmExitCheckBox.setFont(UIManager.getFont("CheckBox.font"));
            disableConfirmExitCheckBox.setOpaque(false);
            disableConfirmExitCheckBox.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    disableConfirmExitCheckBoxActionPerformed(e);
                }
            });
            disableConfirmExitCheckBox.setText(
                Localizer.localize("Util", "GeneralPreferencesDisableConfirmExitCheckBox"));
            panel15.add(disableConfirmExitCheckBox,
                new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel15, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel17 ========
        {
            panel17.setOpaque(false);
            panel17.setLayout(new GridBagLayout());
            ((GridBagLayout) panel17.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) panel17.getLayout()).rowHeights = new int[] {0, 0, 0};
            ((GridBagLayout) panel17.getLayout()).columnWeights = new double[] {1.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel17.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

            //======== panel3 ========
            {
                panel3.setOpaque(false);
                panel3.setLayout(new GridBagLayout());
                ((GridBagLayout) panel3.getLayout()).columnWidths = new int[] {0, 0, 0};
                ((GridBagLayout) panel3.getLayout()).rowHeights = new int[] {30, 0};
                ((GridBagLayout) panel3.getLayout()).columnWeights =
                    new double[] {0.0, 1.0, 1.0E-4};
                ((GridBagLayout) panel3.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                //---- historyHeadingLabel ----
                historyHeadingLabel.setFont(UIManager.getFont("Label.font"));
                historyHeadingLabel
                    .setText(Localizer.localize("Util", "GeneralPreferencesHistoryHeadingLabel"));
                panel3.add(historyHeadingLabel,
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
            panel17.add(panel3,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

            //======== panel14 ========
            {
                panel14.setOpaque(false);
                panel14.setLayout(new GridBagLayout());
                ((GridBagLayout) panel14.getLayout()).columnWidths = new int[] {0, 0, 0};
                ((GridBagLayout) panel14.getLayout()).rowHeights = new int[] {30, 0};
                ((GridBagLayout) panel14.getLayout()).columnWeights =
                    new double[] {0.0, 1.0, 1.0E-4};
                ((GridBagLayout) panel14.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                //---- languageHeadingLabel ----
                languageHeadingLabel.setFont(UIManager.getFont("Label.font"));
                languageHeadingLabel
                    .setText(Localizer.localize("Util", "GeneralPreferencesLanguageHeadingLabel"));
                panel14.add(languageHeadingLabel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //======== panel16 ========
                {
                    panel16.setOpaque(false);
                    panel16.setBorder(new MatteBorder(0, 0, 1, 0, Color.lightGray));
                    panel16.setLayout(new BorderLayout());
                }
                panel14.add(panel16,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            panel17.add(panel14,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

            //======== panel4 ========
            {
                panel4.setOpaque(false);
                panel4.setLayout(new GridBagLayout());
                ((GridBagLayout) panel4.getLayout()).columnWidths = new int[] {0, 0, 0};
                ((GridBagLayout) panel4.getLayout()).rowHeights = new int[] {30, 0};
                ((GridBagLayout) panel4.getLayout()).columnWeights =
                    new double[] {0.0, 0.0, 1.0E-4};
                ((GridBagLayout) panel4.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                //---- clearRecentFilesLabel ----
                clearRecentFilesLabel.setFont(UIManager.getFont("Label.font"));
                clearRecentFilesLabel
                    .setText(Localizer.localize("Util", "GeneralPreferencesClearRecentFilesLabel"));
                panel4.add(clearRecentFilesLabel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                //---- clearRecentlyOpenedFilesButton ----
                clearRecentlyOpenedFilesButton.setFont(UIManager.getFont("Button.font"));
                clearRecentlyOpenedFilesButton.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/bin.png")));
                clearRecentlyOpenedFilesButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        clearRecentlyOpenedFilesButtonActionPerformed(e);
                    }
                });
                clearRecentlyOpenedFilesButton.setText(Localizer
                    .localize("Util", "GeneralPreferencesClearRecentlyOpenedFilesButtonText"));
                panel4.add(clearRecentlyOpenedFilesButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            }
            panel17.add(panel4,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

            //======== panel18 ========
            {
                panel18.setOpaque(false);
                panel18.setLayout(new GridBagLayout());
                ((GridBagLayout) panel18.getLayout()).columnWidths = new int[] {0, 0, 0};
                ((GridBagLayout) panel18.getLayout()).rowHeights = new int[] {30, 0};
                ((GridBagLayout) panel18.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 1.0E-4};
                ((GridBagLayout) panel18.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                //---- languageComboBox ----
                languageComboBox.setPrototypeDisplayValue("xxxxxx");
                languageComboBox.setFont(UIManager.getFont("ComboBox.font"));
                panel18.add(languageComboBox,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                //---- changeLanguageButton ----
                changeLanguageButton.setFont(UIManager.getFont("Button.font"));
                changeLanguageButton.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
                changeLanguageButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        changeLanguageButtonActionPerformed(e);
                    }
                });
                changeLanguageButton.setText(
                    Localizer.localize("Util", "GeneralPreferencesChangeLanguageButtonText"));
                panel18.add(changeLanguageButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            }
            panel17.add(panel18,
                new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel17, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel2 ========
        {
            panel2.setOpaque(false);
            panel2.setLayout(new GridBagLayout());
            ((GridBagLayout) panel2.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) panel2.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel2.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel2.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- fontDirectoriesHeadingLabel ----
            fontDirectoriesHeadingLabel.setFont(UIManager.getFont("Label.font"));
            fontDirectoriesHeadingLabel.setText(
                Localizer.localize("Util", "GeneralPreferencesFontDirectoriesHeadingLabel"));
            panel2.add(fontDirectoriesHeadingLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //======== panel6 ========
            {
                panel6.setOpaque(false);
                panel6.setBorder(new MatteBorder(0, 0, 1, 0, Color.lightGray));
                panel6.setLayout(new BorderLayout());
            }
            panel2.add(panel6,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel2, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel19 ========
        {
            panel19.setOpaque(false);
            panel19.setLayout(new GridBagLayout());
            ((GridBagLayout) panel19.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) panel19.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout) panel19.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
            ((GridBagLayout) panel19.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

            //======== scrollPane1 ========
            {

                //---- fontDirectoriesList ----
                fontDirectoriesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                fontDirectoriesList.setFont(UIManager.getFont("List.font"));
                scrollPane1.setViewportView(fontDirectoriesList);
            }
            panel19.add(scrollPane1,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

            //======== panel8 ========
            {
                panel8.setOpaque(false);
                panel8.setLayout(new GridBagLayout());
                ((GridBagLayout) panel8.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) panel8.getLayout()).rowHeights = new int[] {35, 0, 35, 30, 0};
                ((GridBagLayout) panel8.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
                ((GridBagLayout) panel8.getLayout()).rowWeights =
                    new double[] {0.0, 1.0, 0.0, 0.0, 1.0E-4};

                //---- resetFontPathsButton ----
                resetFontPathsButton.setFont(UIManager.getFont("Button.font"));
                resetFontPathsButton.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
                resetFontPathsButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        resetFontPathsButtonActionPerformed(e);
                    }
                });
                resetFontPathsButton.setText(
                    Localizer.localize("Util", "GeneralPreferencesRestoreDefaultsButtonText"));
                panel8.add(resetFontPathsButton,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                //---- addFontDirectoryButton ----
                addFontDirectoryButton.setFont(UIManager.getFont("Button.font"));
                addFontDirectoryButton.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/add.png")));
                addFontDirectoryButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        addFontDirectoryButtonActionPerformed(e);
                    }
                });
                addFontDirectoryButton.setText(
                    Localizer.localize("Util", "GeneralPreferencesAddFontDirectoryButtonText"));
                panel8.add(addFontDirectoryButton,
                    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                //---- removeFontDirectoryButton ----
                removeFontDirectoryButton.setFont(UIManager.getFont("Button.font"));
                removeFontDirectoryButton.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/delete.png")));
                removeFontDirectoryButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        removeFontDirectoryButtonActionPerformed(e);
                    }
                });
                removeFontDirectoryButton.setText(
                    Localizer.localize("Util", "GeneralPreferencesRemoveFontDirectoryButtonText"));
                panel8.add(removeFontDirectoryButton,
                    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            }
            panel19.add(panel8,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel19, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel9 ========
        {
            panel9.setOpaque(false);
            panel9.setLayout(new GridBagLayout());
            ((GridBagLayout) panel9.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) panel9.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel9.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel9.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- systemFontHeadingLabel ----
            systemFontHeadingLabel.setFont(UIManager.getFont("Label.font"));
            systemFontHeadingLabel
                .setText(Localizer.localize("Util", "GeneralPreferencesSystemFontHeadingLabel"));
            panel9.add(systemFontHeadingLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //======== panel10 ========
            {
                panel10.setOpaque(false);
                panel10.setBorder(new MatteBorder(0, 0, 1, 0, Color.lightGray));
                panel10.setLayout(new BorderLayout());
            }
            panel9.add(panel10,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel9, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel11 ========
        {
            panel11.setOpaque(false);
            panel11.setLayout(new GridBagLayout());
            ((GridBagLayout) panel11.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
            ((GridBagLayout) panel11.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel11.getLayout()).columnWeights =
                new double[] {0.0, 0.0, 1.0, 0.0, 1.0E-4};
            ((GridBagLayout) panel11.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

            //---- customFontFileLabel ----
            customFontFileLabel.setFont(UIManager.getFont("Label.font"));
            customFontFileLabel
                .setText(Localizer.localize("Util", "GeneralPreferencesCustomFontFileLabel"));
            panel11.add(customFontFileLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

            //---- showSystemFontSelectionDialogButton ----
            showSystemFontSelectionDialogButton.setFont(UIManager.getFont("Button.font"));
            showSystemFontSelectionDialogButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/font.png")));
            showSystemFontSelectionDialogButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    showSystemFontSelectionDialogButtonActionPerformed(e);
                }
            });
            showSystemFontSelectionDialogButton.setText(
                Localizer.localize("Util", "GeneralPreferencesShowSystemFontSelectionButtonText"));
            panel11.add(showSystemFontSelectionDialogButton,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

            //---- restoreDefaultSystemFontButton ----
            restoreDefaultSystemFontButton.setFont(UIManager.getFont("Button.font"));
            restoreDefaultSystemFontButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
            restoreDefaultSystemFontButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    restoreDefaultSystemFontButtonActionPerformed(e);
                }
            });
            restoreDefaultSystemFontButton.setText(Localizer
                .localize("Util", "GeneralPreferencesRestoreDefaultSystemFontFileButtonText"));
            panel11.add(restoreDefaultSystemFontButton,
                new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel11, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //---- customFontFileInstructionLabel ----
        customFontFileInstructionLabel.setFont(UIManager.getFont("Label.font"));
        customFontFileInstructionLabel.setText(
            Localizer.localize("Util", "GeneralPreferencesCustomFontFileInstructionLabel"));
        add(customFontFileInstructionLabel,
            new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel15;
    private JPanel panel1;
    private JLabel startupHeadingLabel;
    private JPanel panel5;
    private JPanel panel12;
    private JLabel shutdownHeadingLabel;
    private JPanel panel13;
    private JCheckBox checkForUpdatesCheckBox;
    private JCheckBox disableConfirmExitCheckBox;
    private JPanel panel17;
    private JPanel panel3;
    private JLabel historyHeadingLabel;
    private JPanel panel7;
    private JPanel panel14;
    private JLabel languageHeadingLabel;
    private JPanel panel16;
    private JPanel panel4;
    private JLabel clearRecentFilesLabel;
    private JButton clearRecentlyOpenedFilesButton;
    private JPanel panel18;
    private JComboBox languageComboBox;
    private JButton changeLanguageButton;
    private JPanel panel2;
    private JLabel fontDirectoriesHeadingLabel;
    private JPanel panel6;
    private JPanel panel19;
    private JScrollPane scrollPane1;
    private JList fontDirectoriesList;
    private JPanel panel8;
    private JButton resetFontPathsButton;
    private JButton addFontDirectoryButton;
    private JButton removeFontDirectoryButton;
    private JPanel panel9;
    private JLabel systemFontHeadingLabel;
    private JPanel panel10;
    private JPanel panel11;
    private JLabel customFontFileLabel;
    private JButton showSystemFontSelectionDialogButton;
    private JButton restoreDefaultSystemFontButton;
    private JLabel customFontFileInstructionLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
