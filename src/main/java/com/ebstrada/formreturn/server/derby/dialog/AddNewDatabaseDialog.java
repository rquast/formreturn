package com.ebstrada.formreturn.server.derby.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.server.ServerGUI;
import com.ebstrada.formreturn.server.ServerFrame;
import com.ebstrada.formreturn.server.derby.DatabaseServerException;

public class AddNewDatabaseDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private ServerFrame serverFrame;

    public AddNewDatabaseDialog(ServerFrame serverFrame) {
        super(serverFrame);
        initComponents();
        this.serverFrame = serverFrame;
        getRootPane().setDefaultButton(createDatabaseButton);
    }

    public AddNewDatabaseDialog(Dialog owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(createDatabaseButton);
    }

    private void createDatabaseActionPerformed(ActionEvent e) {

        String databaseName = databaseNameTextField.getText().trim();

        if (!(Misc.validateSQL92Identifier(databaseName))) {
            Misc.showErrorMsg(this,
                Localizer.localize("Server", "AddNewDatabaseInvalidDatabaseNameMessage"),
                Localizer.localize("Server", "AddNewDatabaseInvalidDatabaseNameTitle"));
            return;
        }

        try {
            ServerGUI.getInstance().getDatabaseServer().createDatabaseInstance(databaseName);
            serverFrame.updateLocalDatabases();
            Misc.showSuccessMsg(this, String
                .format(Localizer.localize("Server", "AddNewDatabaseCreateSuccessMessage"),
                    databaseName));
            dispose();
        } catch (DatabaseServerException e1) {
            Misc.showExceptionMsg(this, e1);
        }

    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createDatabaseButton.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        databaseNameLabel = new JLabel();
        databaseNameTextField = new JTextField();
        buttonBar = new JPanel();
        createDatabaseButton = new JButton();
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
        this.setTitle(Localizer.localize("Server", "AddNewDatabaseDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {0.0, 1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                //---- databaseNameLabel ----
                databaseNameLabel.setFont(UIManager.getFont("Label.font"));
                databaseNameLabel.setText(Localizer.localize("Server", "AddNewDatabaseNameLabel"));
                contentPanel.add(databaseNameLabel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                        GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 5), 0, 0));

                //---- databaseNameTextField ----
                databaseNameTextField.setFont(UIManager.getFont("TextField.font"));
                contentPanel.add(databaseNameTextField,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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

                //---- createDatabaseButton ----
                createDatabaseButton.setFont(UIManager.getFont("Button.font"));
                createDatabaseButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        createDatabaseActionPerformed(e);
                    }
                });
                createDatabaseButton.setText(
                    Localizer.localize("Server", "AddNewDatabaseCreateDatabaseButtonText"));
                buttonBar.add(createDatabaseButton,
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
        setSize(400, 120);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel databaseNameLabel;
    private JTextField databaseNameTextField;
    private JPanel buttonBar;
    private JButton createDatabaseButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
