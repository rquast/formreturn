package com.ebstrada.formreturn.manager.ui.cdm.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;

public class ConfirmStructureChangesDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private int dialogResult = JOptionPane.CANCEL_OPTION;

    public ConfirmStructureChangesDialog(Frame owner) {
        super(owner);
        initComponents();
    }

    public ConfirmStructureChangesDialog(Dialog owner) {
        super(owner);
        initComponents();
    }

    private void saveChangesButtonActionPerformed(ActionEvent e) {
        setDialogResult(JOptionPane.OK_OPTION);
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        setDialogResult(JOptionPane.CANCEL_OPTION);
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        confirmChangesInformationLabel = new JLabel();
        changesTextScrollPane = new JScrollPane();
        changesTextArea = new JTextArea();
        buttonBar = new JPanel();
        saveChangesButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setModal(true);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UICDM", "ConfirmStructureChangesDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {35, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights =
                    new double[] {0.0, 1.0, 1.0E-4};

                //---- confirmChangesInformationLabel ----
                confirmChangesInformationLabel.setFont(UIManager.getFont("Label.font"));
                confirmChangesInformationLabel
                    .setText(Localizer.localize("UICDM", "ConfirmChangesInformationLabel"));
                contentPanel.add(confirmChangesInformationLabel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                //======== changesTextScrollPane ========
                {

                    //---- changesTextArea ----
                    changesTextArea.setEditable(false);
                    changesTextArea.setFont(UIManager.getFont("TextArea.font"));
                    changesTextScrollPane.setViewportView(changesTextArea);
                }
                contentPanel.add(changesTextScrollPane,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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

                //---- saveChangesButton ----
                saveChangesButton.setFont(UIManager.getFont("Button.font"));
                saveChangesButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveChangesButtonActionPerformed(e);
                    }
                });
                saveChangesButton.setText(Localizer.localize("UICDM", "SaveChangesButtonText"));
                buttonBar.add(saveChangesButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UICDM", "CancelButtonText"));
                buttonBar.add(cancelButton,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(660, 455);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel confirmChangesInformationLabel;
    private JScrollPane changesTextScrollPane;
    private JTextArea changesTextArea;
    private JPanel buttonBar;
    private JButton saveChangesButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    public void setConfirmMessage(String message) {
        changesTextArea.setText(message);
        changesTextArea.setCaretPosition(0);
        changesTextScrollPane.getVerticalScrollBar().setValue(0);
    }
}
