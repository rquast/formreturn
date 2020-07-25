package com.ebstrada.formreturn.manager.ui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class MessageDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    public MessageDialog(Frame owner) {
        super(owner);
        initComponents();
    }

    public MessageDialog(Dialog owner) {
        super(owner);
        initComponents();
    }

    private void closeButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    public void setMessageText(String message) {
        messageTextPane.setText(message);
    }

    public static void showErrorMessage(Component parent, String title, String message) {

        if (message.length() < 125) {
            javax.swing.JOptionPane
                .showConfirmDialog(parent, message, title, javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        } else {

            MessageDialog md = null;

            if (parent instanceof Dialog) {
                md = new MessageDialog((Dialog) parent);
            } else if (parent instanceof Frame) {
                md = new MessageDialog((Frame) parent);
            } else {
                return;
            }

            md.setTitle(title);
            md.setMessageText(message);
            md.iconLabel.setIcon(UIManager.getIcon("OptionPane.errorIcon"));

            md.setVisible(true);

        }

    }

    public static void showSuccessMessage(Component parent, String title, String message) {

        if (message.length() < 125) {
            javax.swing.JOptionPane
                .showConfirmDialog(parent, message, title, javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } else {

            MessageDialog md = null;

            if (parent instanceof Dialog) {
                md = new MessageDialog((Dialog) parent);
            } else if (parent instanceof Frame) {
                md = new MessageDialog((Frame) parent);
            } else {
                return;
            }

            md.iconLabel.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
            md.setTitle(title);
            md.setMessageText(message);

            md.setVisible(true);

        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        iconLabel = new JLabel();
        messageScrollPane = new JScrollPane();
        messageTextPane = new JTextPane();
        buttonBar = new JPanel();
        closeButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 15, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {0.0, 0.0, 1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                contentPanel.add(iconLabel,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //======== messageScrollPane ========
                {

                    //---- messageTextPane ----
                    messageTextPane.setEditable(false);
                    messageTextPane.setFont(UIManager.getFont("TextPane.font"));
                    messageScrollPane.setViewportView(messageTextPane);
                }
                contentPanel.add(messageScrollPane,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

                //---- closeButton ----
                closeButton.setText("Close");
                closeButton.setFont(UIManager.getFont("Button.font"));
                closeButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        closeButtonActionPerformed(e);
                    }
                });
                buttonBar.add(closeButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(775, 455);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel iconLabel;
    private JScrollPane messageScrollPane;
    private JTextPane messageTextPane;
    private JPanel buttonBar;
    private JButton closeButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
