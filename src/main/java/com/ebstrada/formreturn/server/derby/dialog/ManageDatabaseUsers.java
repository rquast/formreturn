package com.ebstrada.formreturn.server.derby.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.server.ServerGUI;
import com.ebstrada.formreturn.server.derby.DatabaseInstanceException;

public class ManageDatabaseUsers extends JDialog {

    private static final long serialVersionUID = 1L;

    private String databaseName;

    public ManageDatabaseUsers(Frame owner, String databaseName) {
        super(owner);
        initComponents();
        this.databaseName = databaseName;
        updateDatabaseUsersList();
    }

    public void updateDatabaseUsersList() {
        ArrayList<String> databaseUsers =
            ServerGUI.getInstance().getDatabaseServer().getDatabaseInstance(databaseName)
                .getDatabaseUsers();
        DefaultListModel dlm = new DefaultListModel();
        for (String databaseUser : databaseUsers) {
            dlm.addElement(databaseUser);
        }
        databaseUserList.setModel(dlm);
    }

    public ManageDatabaseUsers(Dialog owner) {
        super(owner);
        initComponents();
    }

    private void closeButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void removeUserButtonActionPerformed(ActionEvent e) {

        String selectedUser = (String) databaseUserList.getSelectedValue();

        Object[] options =
            {Localizer.localize("Server", "Yes"), Localizer.localize("Server", "No")};
        String msg = String
            .format(Localizer.localize("Server", "ManageDatabaseUsersRemoveUserConfirmMessage"),
                selectedUser);
        int result = JOptionPane.showOptionDialog(this, msg,
            Localizer.localize("Server", "ManageDatabaseUsersRemoveUserConfirmTitle"),
            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        if (result != 1) {
            try {
                ServerGUI.getInstance().getDatabaseServer().getDatabaseInstance(databaseName)
                    .removeDatabaseUser(selectedUser);
                Misc.showSuccessMsg(this, String
                    .format(Localizer.localize("Server", "ManageDatabaseUsersRemoveSuccessMessage"),
                        selectedUser));
            } catch (DatabaseInstanceException e1) {
                if (ServerGUI.getInstance().getServerFrame() != null) {
                    Misc.showExceptionMsg(ServerGUI.getInstance().getServerFrame(), e1);
                }
            }
            updateDatabaseUsersList();
        }

    }

    private void addNewUserButtonActionPerformed(ActionEvent e) {
        AddNewUserDialog anud = new AddNewUserDialog(this, databaseName);
        anud.setVisible(true);
    }

    private void changeUserPasswordButtonActionPerformed(ActionEvent e) {
        if (databaseUserList.getSelectedIndex() != -1) {
            String selectedUserName = (String) databaseUserList.getSelectedValue();
            ChangeUserPasswordDialog cupd =
                new ChangeUserPasswordDialog(this, databaseName, selectedUserName);
            cupd.setVisible(true);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        panel1 = new JPanel();
        scrollPane1 = new JScrollPane();
        databaseUserList = new JList();
        buttonBar = new JPanel();
        removeUserButton = new JButton();
        addNewUserButton = new JButton();
        changeUserPasswordButton = new JButton();
        closebutton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new BorderLayout());

                //======== panel1 ========
                {
                    panel1.setOpaque(false);
                    panel1.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                    //======== scrollPane1 ========
                    {

                        //---- databaseUserList ----
                        databaseUserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        databaseUserList.setFont(UIManager.getFont("List.font"));
                        scrollPane1.setViewportView(databaseUserList);
                    }
                    panel1.add(scrollPane1,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(panel1, BorderLayout.CENTER);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 0, 0, 85, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 0.0, 0.0, 0.0};

                //---- removeUserButton ----
                removeUserButton.setFocusPainted(false);
                removeUserButton.setFont(UIManager.getFont("Button.font"));
                removeUserButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        removeUserButtonActionPerformed(e);
                    }
                });
                removeUserButton.setText(
                    Localizer.localize("Server", "ManageDatabaseUsersRemoveUserButtonText"));
                buttonBar.add(removeUserButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- addNewUserButton ----
                addNewUserButton.setFocusPainted(false);
                addNewUserButton.setFont(UIManager.getFont("Button.font"));
                addNewUserButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        addNewUserButtonActionPerformed(e);
                    }
                });
                addNewUserButton.setText(
                    Localizer.localize("Server", "ManageDatabaseUsersAddNewUserButtonText"));
                buttonBar.add(addNewUserButton,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- changeUserPasswordButton ----
                changeUserPasswordButton.setFocusPainted(false);
                changeUserPasswordButton.setFont(UIManager.getFont("Button.font"));
                changeUserPasswordButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        changeUserPasswordButtonActionPerformed(e);
                    }
                });
                changeUserPasswordButton.setText(Localizer
                    .localize("Server", "ManageDatabaseUsersChangeUserPasswordButtonText"));
                buttonBar.add(changeUserPasswordButton,
                    new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- closebutton ----
                closebutton.setFocusPainted(false);
                closebutton.setFont(UIManager.getFont("Button.font"));
                closebutton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        closeButtonActionPerformed(e);
                    }
                });
                closebutton.setText(Localizer.localize("Server", "CloseButtonText"));
                buttonBar.add(closebutton,
                    new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(800, 300);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel panel1;
    private JScrollPane scrollPane1;
    private JList databaseUserList;
    private JPanel buttonBar;
    private JButton removeUserButton;
    private JButton addNewUserButton;
    private JButton changeUserPasswordButton;
    private JButton closebutton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
