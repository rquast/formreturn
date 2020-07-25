package com.ebstrada.formreturn.manager.ui.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;

import javax.swing.*;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.ui.IStatusBar;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.panel.DesktopDockingPanel;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class JStatusBar extends JPanel implements IStatusBar {

    private static final long serialVersionUID = 1L;

    private static JStatusBar statusBar;

    public static JStatusBar getInstance() {
        if (statusBar == null) {
            statusBar = new JStatusBar();
        }
        return statusBar;
    }

    public static void reset() {
        getInstance().setProgressLabelText(Localizer.localize("UI", "DoneStatusMessage"));
        getInstance().setProgressValue(0);
    }

    private JStatusBar() {
        initComponents();
        hideControlsButton();
        hideSystemConsoleButton();
        setDatabaseStatusDisconnected();
        String status = Localizer.localize("UI", "LoadingRegistrationDetailsStatusMessage");
        updateRegistrationStatus(status);
    }

    public void setDatabaseStatusConnected() {
        databaseButton.setText(Localizer.localize("UI", "DatabaseConnectedLabel"));
        databaseButton.setIcon(new ImageIcon(
            getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/util/database.png")));
    }

    public void setDatabaseStatusDisconnected() {
        databaseButton.setText(Localizer.localize("UI", "DatabaseDisconnectedLabel"));
        databaseButton.setIcon(new ImageIcon(getClass()
            .getResource("/com/ebstrada/formreturn/manager/ui/icons/util/database_error.png")));
    }

    public void updateRegistrationStatus(String status) {
        registrationButton.setText(status);
        registrationButton.setIcon(new ImageIcon(getClass()
            .getResource("/com/ebstrada/formreturn/manager/ui/icons/application_key.png")));
    }

    public void setProgressLabelText(String text) {

        progressLabel.setText(" " + text + " ");

        Timer progressLabelTimer = new Timer(3000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                progressLabel.setText("");
            }
        });

        progressLabelTimer.setRepeats(false);
        progressLabelTimer.start();

    }

    public void setProgressValue(int value) {
        this.progressBar.setValue(value);
    }

    private void restoreNavigatorButtonActionPerformed(ActionEvent e) {
        DesktopDockingPanel desktopDockingPane = Main.getInstance().getDesktopDockingPanel();
        desktopDockingPane.setNavigatorPanelState(DesktopDockingPanel.PANEL_RESTORED);
        hideControlsButton();
    }

    public void hideControlsButton() {
        controlsButton.setEnabled(false);
        controlsButton.setVisible(false);
    }

    public void showControlsButton() {
        controlsButton.setEnabled(true);
        controlsButton.setVisible(true);
    }

    private void restoreSystemConsoleButtonActionPerformed(ActionEvent e) {
        DesktopDockingPanel desktopDockingPane = Main.getInstance().getDesktopDockingPanel();
        if (desktopDockingPane.getNavigatorPanelState() == DesktopDockingPanel.PANEL_MAXIMIZED) {
            desktopDockingPane.setNavigatorPanelState(DesktopDockingPanel.PANEL_RESTORED);
            desktopDockingPane.setSystemConsolePanelState(DesktopDockingPanel.PANEL_RESTORED);
        } else {
            desktopDockingPane.setSystemConsolePanelState(DesktopDockingPanel.PANEL_RESTORED);
        }
        hideSystemConsoleButton();
    }

    public void hideSystemConsoleButton() {
        systemConsoleButton.setEnabled(false);
        systemConsoleButton.setVisible(false);
    }

    public void showSystemConsoleButton() {
        systemConsoleButton.setEnabled(true);
        systemConsoleButton.setVisible(true);
    }

    private void databaseButtonActionPerformed(ActionEvent e) {
        PreferencesManager.showDatabasePreferences();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        registrationButton = new JButton();
        panel1 = new JPanel();
        databaseButton = new JButton();
        panel2 = new JPanel();
        controlsButton = new JButton();
        systemConsoleButton = new JButton();
        progressLabel = new JLabel();
        progressBar = new JProgressBar();

        //======== this ========
        setBorder(new EmptyBorder(2, 8, 2, 4));
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 5, 0, 0, 0, 0, 0, 0, 102, 16, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {24, 0};
        ((GridBagLayout) getLayout()).columnWeights =
            new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setPreferredSize(new Dimension(3, 20));
            panel1.setMinimumSize(new Dimension(3, 20));
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};
        }
        add(panel1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.NONE, new Insets(0, 0, 0, 2), 0, 0));

        //---- databaseButton ----
        databaseButton.setBorder(new EmptyBorder(3, 6, 3, 6));
        databaseButton.setFocusPainted(false);
        databaseButton.setIconTextGap(6);
        databaseButton.setMargin(new Insets(6, 6, 6, 6));
        databaseButton.setFont(UIManager.getFont("Button.font"));
        databaseButton.setContentAreaFilled(false);
        databaseButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                databaseButtonActionPerformed(e);
            }
        });
        add(databaseButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 2), 0, 0));

        //======== panel2 ========
        {
            panel2.setOpaque(false);
            panel2.setBorder(new MatteBorder(0, 0, 0, 1, Color.gray));
            panel2.setPreferredSize(new Dimension(3, 20));
            panel2.setMinimumSize(new Dimension(3, 20));
            panel2.setLayout(new GridBagLayout());
            ((GridBagLayout) panel2.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel2.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout) panel2.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
            ((GridBagLayout) panel2.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};
        }
        add(panel2, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.NONE, new Insets(0, 0, 0, 2), 0, 0));

        //---- controlsButton ----
        controlsButton.setIcon(new ImageIcon(
            getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/pin_black.png")));
        controlsButton.setMargin(new Insets(6, 6, 6, 6));
        controlsButton.setIconTextGap(6);
        controlsButton.setBorder(new EmptyBorder(3, 6, 3, 3));
        controlsButton.setFocusPainted(false);
        controlsButton.setOpaque(false);
        controlsButton.setContentAreaFilled(false);
        controlsButton.setFont(UIManager.getFont("Button.font"));
        controlsButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                restoreNavigatorButtonActionPerformed(e);
            }
        });
        controlsButton.setText(Localizer.localize("UI", "ControlsButtonText"));
        add(controlsButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 2), 0, 0));

        //---- systemConsoleButton ----
        systemConsoleButton.setIcon(new ImageIcon(
            getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/pin_black.png")));
        systemConsoleButton.setMargin(new Insets(6, 6, 6, 6));
        systemConsoleButton.setIconTextGap(6);
        systemConsoleButton.setBorder(new EmptyBorder(3, 6, 3, 3));
        systemConsoleButton.setFocusPainted(false);
        systemConsoleButton.setOpaque(false);
        systemConsoleButton.setContentAreaFilled(false);
        systemConsoleButton.setFont(UIManager.getFont("Button.font"));
        systemConsoleButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                restoreSystemConsoleButtonActionPerformed(e);
            }
        });
        systemConsoleButton.setText(Localizer.localize("UI", "SystemConsoleButtonText"));
        add(systemConsoleButton,
            new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 2), 0, 0));

        //---- progressLabel ----
        progressLabel.setFont(UIManager.getFont("Label.font"));
        add(progressLabel, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 2), 0, 0));

        //---- progressBar ----
        progressBar.setMinimumSize(new Dimension(10, 16));
        progressBar.setPreferredSize(new Dimension(146, 16));
        progressBar.setMaximumSize(new Dimension(32767, 16));
        add(progressBar, new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 2), 0, 0));
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JButton registrationButton;
    private JPanel panel1;
    private JButton databaseButton;
    private JPanel panel2;
    private JButton controlsButton;
    private JButton systemConsoleButton;
    private JLabel progressLabel;
    private JProgressBar progressBar;
    // JFormDesigner - End of variables declaration //GEN-END:variables

    public void showStatus(String s) {
        setProgressLabelText(s);
    }

}
