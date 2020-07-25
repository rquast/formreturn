package com.ebstrada.formreturn.manager.ui.editor.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;

public class AddNewPreviewFieldDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private int dialogResult;

    public AddNewPreviewFieldDialog(Frame owner, String fieldName, String fieldValue) {
        super(owner);
        initComponents();
        fieldNameTextField.setText(fieldName);
        valueTextField.setText(fieldValue);
        getRootPane().setDefaultButton(okButton);
    }

    public AddNewPreviewFieldDialog(Dialog owner, String fieldName, String fieldValue) {
        super(owner);
        initComponents();
        fieldNameTextField.setText(fieldName);
        valueTextField.setText(fieldValue);
        getRootPane().setDefaultButton(okButton);
    }

    private void okButtonActionPerformed(ActionEvent e) {
        setDialogResult(JOptionPane.OK_OPTION);
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        setDialogResult(JOptionPane.CANCEL_OPTION);
        dispose();
    }

    public String getFieldValue() {
        return valueTextField.getText().trim();
    }

    public String getFieldName() {
        return fieldNameTextField.getText().trim();
    }


    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fieldNameTextField.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        fieldNameLabel = new JLabel();
        fieldNameTextField = new JTextField();
        valueLabel = new JLabel();
        valueTextField = new JTextField();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "AddNewPreviewFieldDialogTitle"));

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

                //---- fieldNameLabel ----
                fieldNameLabel.setFont(UIManager.getFont("Label.font"));
                fieldNameLabel.setText(Localizer.localize("UI", "AddPreviewFieldNameLabel"));
                contentPanel.add(fieldNameLabel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                        GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                //---- fieldNameTextField ----
                fieldNameTextField.setFont(UIManager.getFont("TextField.font"));
                contentPanel.add(fieldNameTextField,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                //---- valueLabel ----
                valueLabel.setFont(UIManager.getFont("Label.font"));
                valueLabel.setText(Localizer.localize("UI", "AddPreviewValueLabel"));
                contentPanel.add(valueLabel,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                        GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

                //---- valueTextField ----
                valueTextField.setFont(UIManager.getFont("TextField.font"));
                contentPanel.add(valueTextField,
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
                okButton.setFont(UIManager.getFont("Button.font"));
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                okButton.setText(Localizer.localize("UI", "OKButtonText"));
                buttonBar.add(okButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UI", "CancelButtonText"));
                buttonBar.add(cancelButton,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(400, 160);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel fieldNameLabel;
    private JTextField fieldNameTextField;
    private JLabel valueLabel;
    private JTextField valueTextField;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
