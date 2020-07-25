package com.ebstrada.formreturn.server.derby.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.server.ServerGUI;
import com.ebstrada.formreturn.server.derby.DatabaseInstanceException;

public class ChangeUserPasswordDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private String databaseName;

    private String selectedUserName;

    public ChangeUserPasswordDialog(Frame owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(changePasswordButton);
    }

    public ChangeUserPasswordDialog(Dialog owner, String databaseName, String selectedUserName) {
        super(owner);
        initComponents();
        this.databaseName = databaseName;
        this.selectedUserName = selectedUserName;
        usernameLabel.setText(selectedUserName);
        getRootPane().setDefaultButton(changePasswordButton);
    }

    private void changePasswordButtonActionPerformed(ActionEvent e) {

        String password = new String(passwordPasswordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordPasswordField.getPassword()).trim();

        if (!(password.equals(confirmPassword))) {
            Misc.showErrorMsg(this,
                Localizer.localize("Server", "ChangeUserPasswordConfirmationFailureMessage"),
                Localizer.localize("Server", "ChangeUserPasswordConfirmationFailureTitle"));
            return;
        }

        try {
            ServerGUI.getInstance().getDatabaseServer().getDatabaseInstance(databaseName)
                .changeDatabaseUserPassword(selectedUserName, password);
            Misc.showSuccessMsg(this,
                Localizer.localize("Server", "ChangeUserPasswordSuccessMessage"));
            dispose();
        } catch (DatabaseInstanceException e1) {
            Misc.showExceptionMsg(this, e1);
        }

    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                changePasswordButton.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        usernameDescriptionLabel = new JLabel();
        usernameLabel = new JLabel();
        passwordLabel = new JLabel();
        passwordPasswordField = new JPasswordField();
        confirmPasswordLabel = new JLabel();
        confirmPasswordPasswordField = new JPasswordField();
        buttonBar = new JPanel();
        changePasswordButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("Server", "ChangeUserPasswordDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {0.0, 1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights =
                    new double[] {0.0, 0.0, 0.0, 1.0E-4};

                //---- usernameDescriptionLabel ----
                usernameDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                usernameDescriptionLabel
                    .setText(Localizer.localize("Server", "ChangeUserPasswordUsernameLabel"));
                contentPanel.add(usernameDescriptionLabel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                        GridBagConstraints.VERTICAL, new Insets(0, 0, 5, 5), 0, 0));

                //---- usernameLabel ----
                usernameLabel.setFont(UIManager.getFont("Label.font"));
                contentPanel.add(usernameLabel,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                //---- passwordLabel ----
                passwordLabel.setFont(UIManager.getFont("Label.font"));
                passwordLabel
                    .setText(Localizer.localize("Server", "ChangeUserPasswordPasswordLabel"));
                contentPanel.add(passwordLabel,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                        GridBagConstraints.VERTICAL, new Insets(0, 0, 5, 5), 0, 0));

                //---- passwordPasswordField ----
                passwordPasswordField.setFont(UIManager.getFont("PasswordField.font"));
                contentPanel.add(passwordPasswordField,
                    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                //---- confirmPasswordLabel ----
                confirmPasswordLabel.setFont(UIManager.getFont("Label.font"));
                confirmPasswordLabel.setText(
                    Localizer.localize("Server", "ChangeUserPasswordConfirmPasswordLabel"));
                contentPanel.add(confirmPasswordLabel,
                    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                        GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 5), 0, 0));

                //---- confirmPasswordPasswordField ----
                confirmPasswordPasswordField.setFont(UIManager.getFont("PasswordField.font"));
                contentPanel.add(confirmPasswordPasswordField,
                    new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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

                //---- changePasswordButton ----
                changePasswordButton.setFont(UIManager.getFont("Button.font"));
                changePasswordButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        changePasswordButtonActionPerformed(e);
                    }
                });
                changePasswordButton.setText(
                    Localizer.localize("Server", "ChangeUserPasswordChangePasswordButtonText"));
                buttonBar.add(changePasswordButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("Server", "CancelButtonText"));
                buttonBar.add(cancelButton,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(400, 185);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel usernameDescriptionLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JPasswordField passwordPasswordField;
    private JLabel confirmPasswordLabel;
    private JPasswordField confirmPasswordPasswordField;
    private JPanel buttonBar;
    private JButton changePasswordButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
