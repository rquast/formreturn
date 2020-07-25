package com.ebstrada.formreturn.manager.ui.editor.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.component.*;

public class RestoreMarkAreaPresetStyleDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private int dialogResult = javax.swing.JOptionPane.CANCEL_OPTION;

    public RestoreMarkAreaPresetStyleDialog(Frame owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(restoreButton);
    }

    public RestoreMarkAreaPresetStyleDialog(Dialog owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(restoreButton);
    }

    private void okButtonActionPerformed(ActionEvent e) {
        this.dialogResult = javax.swing.JOptionPane.OK_OPTION;
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        this.dialogResult = javax.swing.JOptionPane.CANCEL_OPTION;
        dispose();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                restoreButton.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        restoreMessageLabel = new JLabel();
        panel1 = new JPanel();
        boxDesignCheckBox = new JCheckBox();
        spacingCheckBox = new JCheckBox();
        markAreaValuesCheckBox = new JCheckBox();
        aggregationRuleCheckBox = new JCheckBox();
        buttonBar = new JPanel();
        helpLabel = new JHelpLabel();
        restoreButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
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
                ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {35, 0, 0};
                ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

                //---- restoreMessageLabel ----
                restoreMessageLabel.setFont(UIManager.getFont("Label.font"));
                restoreMessageLabel.setText(Localizer.localize("UI", "RestorePresetStyleMessageLabel"));
                contentPanel.add(restoreMessageLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== panel1 ========
                {
                    panel1.setOpaque(false);
                    panel1.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {25, 0, 0, 0};
                    ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
                    ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
                    ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0, 1.0E-4};

                    //---- boxDesignCheckBox ----
                    boxDesignCheckBox.setFocusPainted(false);
                    boxDesignCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                    boxDesignCheckBox.setSelected(true);
                    boxDesignCheckBox.setText(Localizer.localize("UI", "RestorePresetBoxDesignCheckBox"));
                    panel1.add(boxDesignCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- spacingCheckBox ----
                    spacingCheckBox.setFocusPainted(false);
                    spacingCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                    spacingCheckBox.setSelected(true);
                    spacingCheckBox.setText(Localizer.localize("UI", "RestorePresetSpacingCheckBox"));
                    panel1.add(spacingCheckBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- markAreaValuesCheckBox ----
                    markAreaValuesCheckBox.setFocusPainted(false);
                    markAreaValuesCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                    markAreaValuesCheckBox.setSelected(true);
                    markAreaValuesCheckBox.setText(Localizer.localize("UI", "RestorePresetMarkAreaValuesCheckBox"));
                    panel1.add(markAreaValuesCheckBox, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- aggregationRuleCheckBox ----
                    aggregationRuleCheckBox.setFocusPainted(false);
                    aggregationRuleCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                    aggregationRuleCheckBox.setSelected(true);
                    aggregationRuleCheckBox.setText(Localizer.localize("UI", "RestorePresetAggregationRuleCheckBox"));
                    panel1.add(aggregationRuleCheckBox, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));
                }
                contentPanel.add(panel1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0};

                //---- helpLabel ----
                helpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                helpLabel.setHelpGUID("mark-area-preset-styles");
                helpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                buttonBar.add(helpLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- restoreButton ----
                restoreButton.setFont(UIManager.getFont("Button.font"));
                restoreButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                restoreButton.setText(Localizer.localize("UI", "RestoreButtonText"));
                buttonBar.add(restoreButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UI", "CancelButtonText"));
                buttonBar.add(cancelButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(500, 270);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel restoreMessageLabel;
    private JPanel panel1;
    private JCheckBox boxDesignCheckBox;
    private JCheckBox spacingCheckBox;
    private JCheckBox markAreaValuesCheckBox;
    private JCheckBox aggregationRuleCheckBox;
    private JPanel buttonBar;
    private JHelpLabel helpLabel;
    private JButton restoreButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public void setRestoreMessage(String restoreMessage) {
        restoreMessageLabel.setText(restoreMessage);
    }

    public boolean isRestoreBoxDesign() {
        return boxDesignCheckBox.isSelected();
    }

    public boolean isRestoreSpacing() {
        return spacingCheckBox.isSelected();
    }

    public boolean isRestoreMarkAreaValues() {
        return markAreaValuesCheckBox.isSelected();
    }

    public boolean isRestoreAggregationRule() {
        return aggregationRuleCheckBox.isSelected();
    }

    public int getDialogResult() {
        return this.dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }
}
