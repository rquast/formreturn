package com.ebstrada.formreturn.manager.ui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ApplicationStatePreferences;

public class ConfirmExitDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private int dialogResult = JOptionPane.CANCEL_OPTION;

    public ConfirmExitDialog(Frame owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(okButton);
    }

    public ConfirmExitDialog(Dialog owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(okButton);
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    private void okButtonActionPerformed(ActionEvent e) {
        if (confirmExitPromptCheckBox.isSelected()) {

            ApplicationStatePreferences applicationState = PreferencesManager.getApplicationState();
            applicationState.setConfirmExit(false);

            try {
                PreferencesManager.savePreferences(Main.getXstream());
            } catch (IOException e1) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
            }
        }
        this.dialogResult = JOptionPane.OK_OPTION;
        dispose();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                okButton.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        exitLabel = new JLabel();
        confirmExitPromptCheckBox = new JCheckBox();
        buttonBar = new JPanel();
        cancelButton = new JButton();
        okButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setAlwaysOnTop(true);
        setResizable(false);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UI", "ConfirmExitDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights =
                    new double[] {0.0, 1.0, 0.0, 1.0E-4};

                //---- exitLabel ----
                exitLabel.setFont(UIManager.getFont("Label.font"));
                exitLabel.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/stop.png")));
                exitLabel.setBorder(new EmptyBorder(5, 6, 5, 5));
                exitLabel.setIconTextGap(10);
                exitLabel.setText(
                    String.format(Localizer.localize("UI", "ExitLabel"), Main.APPLICATION_NAME));
                contentPanel.add(exitLabel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                //---- confirmExitPromptCheckBox ----
                confirmExitPromptCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                confirmExitPromptCheckBox
                    .setText(Localizer.localize("UI", "ConfirmExitPromptComboBox"));
                contentPanel.add(confirmExitPromptCheckBox,
                    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 0.0};

                //---- cancelButton ----
                cancelButton.setRequestFocusEnabled(false);
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
                cancelButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UI", "CancelButtonText"));
                buttonBar.add(cancelButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- okButton ----
                okButton.setFont(UIManager.getFont("Button.font"));
                okButton.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/accept.png")));
                okButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                okButton.setText(Localizer.localize("UI", "OKButtonText"));
                buttonBar.add(okButton,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(385, 200);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel exitLabel;
    private JCheckBox confirmExitPromptCheckBox;
    private JPanel buttonBar;
    private JButton cancelButton;
    private JButton okButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
