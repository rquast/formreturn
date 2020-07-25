package com.ebstrada.formreturn.scanner.client;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;

@SuppressWarnings("serial") public class AddSaneScannerDialog extends JDialog {

    private int dialogResult = JOptionPane.CANCEL_OPTION;

    public AddSaneScannerDialog(Frame owner) {
        super(owner);
        initComponents();
        setTitle(Localizer.localize("UI", "AddSaneScannerDialogTitle"));
    }

    public AddSaneScannerDialog(Dialog owner) {
        super(owner);
        initComponents();
    }

    private void okButtonActionPerformed(ActionEvent e) {
        this.dialogResult = JOptionPane.OK_OPTION;
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                remoteAddressTextField.requestFocusInWindow();
            }
        });
    }

    public String getHostAddress() {
        return remoteAddressTextField.getText().trim();
    }

    public int getPortNumber() {
        return (Integer) this.portNumberSpinner.getValue();
    }

    public int getDialogResult() {
        return dialogResult;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        remoteAddressLabel = new JLabel();
        remoteAddressTextField = new JTextField();
        portNumberLabel = new JLabel();
        portNumberSpinner = new JSpinner();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {0.0, 1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights =
                    new double[] {1.0, 1.0, 1.0E-4};

                //---- remoteAddressLabel ----
                remoteAddressLabel.setText("Remote Address");
                remoteAddressLabel.setFont(UIManager.getFont("Label.font"));
                contentPanel.add(remoteAddressLabel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                        GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                //---- remoteAddressTextField ----
                remoteAddressTextField.setText("localhost");
                remoteAddressTextField.setHorizontalAlignment(SwingConstants.RIGHT);
                remoteAddressTextField.setFont(UIManager.getFont("TextField.font"));
                contentPanel.add(remoteAddressTextField,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                //---- portNumberLabel ----
                portNumberLabel.setText("Port Number");
                portNumberLabel.setFont(UIManager.getFont("Label.font"));
                contentPanel.add(portNumberLabel,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                        GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

                //---- portNumberSpinner ----
                portNumberSpinner.setModel(new SpinnerNumberModel(6566, 1, 65535, 1));
                portNumberSpinner.setFont(UIManager.getFont("Spinner.font"));
                contentPanel.add(portNumberSpinner,
                    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 0.0};

                //---- okButton ----
                okButton.setText("OK");
                okButton.setFont(UIManager.getFont("Button.font"));
                okButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                buttonBar.add(okButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                buttonBar.add(cancelButton,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(345, 195);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel remoteAddressLabel;
    private JTextField remoteAddressTextField;
    private JLabel portNumberLabel;
    private JSpinner portNumberSpinner;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
