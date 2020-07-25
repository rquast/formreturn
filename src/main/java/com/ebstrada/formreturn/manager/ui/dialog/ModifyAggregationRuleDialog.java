package com.ebstrada.formreturn.manager.ui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.ui.editor.dialog.ValidateAggregationRuleDialog;

public class ModifyAggregationRuleDialog extends JDialog {

    public static final int SAVE_FORM_ONLY = 1;

    public static final int SAVE_WHOLE_PUBLICATION = 2;

    private static final long serialVersionUID = 1L;

    private String aggregationRule;

    private int dialogResult = JOptionPane.CANCEL_OPTION;

    private int saveType = SAVE_FORM_ONLY;

    public ModifyAggregationRuleDialog(Frame owner) {
        super(owner);
        initComponents();
    }

    public ModifyAggregationRuleDialog(Dialog owner) {
        super(owner);
        initComponents();
    }

    public void restoreSettings() {
        aggregationRuleTextField.setText(aggregationRule);
    }

    private void saveThisFormOnlyButtonActionPerformed(ActionEvent e) {
        setDialogResult(JOptionPane.OK_OPTION);
        setAggregationRule(aggregationRuleTextField.getText().trim());
        setSaveType(SAVE_FORM_ONLY);
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void validateAggregationRuleButtonActionPerformed(ActionEvent e) {
        validateAggregationRule();
    }

    private void validateAggregationRule() {

        ValidateAggregationRuleDialog vard = new ValidateAggregationRuleDialog(Main.getInstance());
        vard.setAggregationRule(getAggregationRule());
        vard.setModal(true);
        vard.setVisible(true);
        if (vard.getDialogResult() == JOptionPane.OK_OPTION) {
            aggregationRuleTextField.setText(vard.getAggregationRule());
        }

    }

    private void saveForWholePublicationButtonActionPerformed(ActionEvent e) {
        setDialogResult(JOptionPane.OK_OPTION);
        setAggregationRule(aggregationRuleTextField.getText().trim());
        setSaveType(SAVE_WHOLE_PUBLICATION);
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        panel9 = new JPanel();
        ruleLabel = new JLabel();
        aggregationRuleTextField = new JTextField();
        validateAggregationRuleButton = new JButton();
        markAggregationHelpLabel = new JHelpLabel();
        buttonBar = new JPanel();
        saveThisFormOnlyButton = new JButton();
        saveForWholePublicationButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UI", "ModifyMarkAggregationRuleDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //======== panel9 ========
                {
                    panel9.setOpaque(false);
                    panel9.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel9.getLayout()).columnWidths = new int[] {11, 0, 0, 0, 0, 6, 0};
                    ((GridBagLayout)panel9.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)panel9.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout)panel9.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                    //---- ruleLabel ----
                    ruleLabel.setFont(UIManager.getFont("Label.font"));
                    ruleLabel.setText(Localizer.localize("UI", "AggregationRuleLabel"));
                    panel9.add(ruleLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- aggregationRuleTextField ----
                    aggregationRuleTextField.setFont(UIManager.getFont("TextField.font"));
                    panel9.add(aggregationRuleTextField, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- validateAggregationRuleButton ----
                    validateAggregationRuleButton.setFont(UIManager.getFont("Button.font"));
                    validateAggregationRuleButton.setFocusPainted(false);
                    validateAggregationRuleButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            validateAggregationRuleButtonActionPerformed(e);
                        }
                    });
                    validateAggregationRuleButton.setText(Localizer.localize("UI", "TestRuleButtonText"));
                    panel9.add(validateAggregationRuleButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- markAggregationHelpLabel ----
                    markAggregationHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                    markAggregationHelpLabel.setHelpGUID("mark-area-aggregation-rule");
                    markAggregationHelpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                    panel9.add(markAggregationHelpLabel, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));
                }
                contentPanel.add(panel9, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 0, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 0.0};

                //---- saveThisFormOnlyButton ----
                saveThisFormOnlyButton.setFont(UIManager.getFont("Button.font"));
                saveThisFormOnlyButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveThisFormOnlyButtonActionPerformed(e);
                    }
                });
                saveThisFormOnlyButton.setText(Localizer.localize("UI", "SaveForThisFormOnlyButtonText"));
                buttonBar.add(saveThisFormOnlyButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- saveForWholePublicationButton ----
                saveForWholePublicationButton.setFont(UIManager.getFont("Button.font"));
                saveForWholePublicationButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveForWholePublicationButtonActionPerformed(e);
                    }
                });
                saveForWholePublicationButton.setText(Localizer.localize("UI", "SaveForWholePublicationButtonText"));
                buttonBar.add(saveForWholePublicationButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
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
        setSize(530, 120);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel panel9;
    private JLabel ruleLabel;
    private JTextField aggregationRuleTextField;
    private JButton validateAggregationRuleButton;
    private JHelpLabel markAggregationHelpLabel;
    private JPanel buttonBar;
    private JButton saveThisFormOnlyButton;
    private JButton saveForWholePublicationButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    public String getAggregationRule() {
        return aggregationRule;
    }

    public void setAggregationRule(String aggregationRule) {
        this.aggregationRule = aggregationRule;
    }

    public int getSaveType() {
        return saveType;
    }

    public void setSaveType(int saveType) {
        this.saveType = saveType;
    }
}
