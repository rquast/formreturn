package com.ebstrada.formreturn.manager.ui.editor.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.aggregation.exception.ErrorFlagException;
import com.ebstrada.aggregation.exception.InvalidRulePartException;
import com.ebstrada.aggregation.exception.NoMatchException;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.util.Misc;

public class ValidateAggregationRuleDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private int dialogResult = JOptionPane.NO_OPTION;

    public ValidateAggregationRuleDialog(Frame owner) {
        super(owner);
        initComponents();
    }

    public ValidateAggregationRuleDialog(Dialog owner) {
        super(owner);
        initComponents();
    }

    public int getDialogResult() {
        return dialogResult;
    }

    public String getAggregationRule() {
        return aggregationRuleTextField.getText();
    }

    public void setAggregationRule(String aggregationRule) {
        aggregationRuleTextField.setText(aggregationRule);
    }

    private void testRuleButtonActionPerformed(ActionEvent e) {
        try {
            String[] selection = null;
            if (selectedMarksTextField.getText().trim().length() > 0) {
                selection = selectedMarksTextField.getText().trim().split("\\,");
            }
            double aggregate = Misc.aggregate(0, selection, getAggregationRule());
            resultTextField.setText(aggregate + "");
        } catch (InvalidRulePartException e1) {
            resultTextField.setText(Localizer.localize("UI", "InvalidAggreationRuleMessageText"));
        } catch (NoMatchException e1) {
            resultTextField.setText("0");
        } catch (ErrorFlagException e1) {
            resultTextField
                .setText(Localizer.localize("UI", "ErrorFlagAggregationRuleMessageText"));
        }
    }

    private void saveRuleButtonActionPerformed(ActionEvent e) {
        dialogResult = JOptionPane.OK_OPTION;
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dialogResult = JOptionPane.CANCEL_OPTION;
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        aggregationRuleLabel = new JLabel();
        aggregationRuleTextField = new JTextField();
        markedResponsesLabel = new JLabel();
        selectedMarksTextField = new JTextField();
        calculatedScoreLabel = new JLabel();
        resultTextField = new JTextField();
        buttonBar = new JPanel();
        helpLabel = new JHelpLabel();
        testRuleButton = new JButton();
        saveRuleButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "TestAggregationRuleDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
                ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0E-4};

                //---- aggregationRuleLabel ----
                aggregationRuleLabel.setFont(UIManager.getFont("Label.font"));
                aggregationRuleLabel.setText(Localizer.localize("UI", "TestAggregationRuleLabel"));
                contentPanel.add(aggregationRuleLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                    new Insets(0, 0, 5, 5), 0, 0));

                //---- aggregationRuleTextField ----
                aggregationRuleTextField.setFont(UIManager.getFont("TextField.font"));
                contentPanel.add(aggregationRuleTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 5, 0), 0, 0));

                //---- markedResponsesLabel ----
                markedResponsesLabel.setFont(UIManager.getFont("Label.font"));
                markedResponsesLabel.setText(Localizer.localize("UI", "TestAggregationRuleMarkedResponsesLabel"));
                contentPanel.add(markedResponsesLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                    new Insets(0, 0, 5, 5), 0, 0));

                //---- selectedMarksTextField ----
                selectedMarksTextField.setFont(UIManager.getFont("TextField.font"));
                contentPanel.add(selectedMarksTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 5, 0), 0, 0));

                //---- calculatedScoreLabel ----
                calculatedScoreLabel.setFont(UIManager.getFont("Label.font"));
                calculatedScoreLabel.setText(Localizer.localize("UI", "TestAggregationRuleCalculatedScoreLabel"));
                contentPanel.add(calculatedScoreLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- resultTextField ----
                resultTextField.setEditable(false);
                resultTextField.setFont(UIManager.getFont("TextField.font"));
                contentPanel.add(resultTextField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 0, 0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0};

                //---- helpLabel ----
                helpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                helpLabel.setHelpGUID("test-aggregation-rule");
                helpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                buttonBar.add(helpLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- testRuleButton ----
                testRuleButton.setFocusPainted(false);
                testRuleButton.setFont(UIManager.getFont("Button.font"));
                testRuleButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        testRuleButtonActionPerformed(e);
                    }
                });
                testRuleButton.setText(Localizer.localize("UI", "TestAggregationRuleTestRuleButtonText"));
                buttonBar.add(testRuleButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- saveRuleButton ----
                saveRuleButton.setFocusPainted(false);
                saveRuleButton.setFont(UIManager.getFont("Button.font"));
                saveRuleButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveRuleButtonActionPerformed(e);
                    }
                });
                saveRuleButton.setText(Localizer.localize("UI", "TestAggregationRuleSaveRuleButtonText"));
                buttonBar.add(saveRuleButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setFocusPainted(false);
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UI", "CancelButtonText"));
                buttonBar.add(cancelButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(510, 190);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel aggregationRuleLabel;
    private JTextField aggregationRuleTextField;
    private JLabel markedResponsesLabel;
    private JTextField selectedMarksTextField;
    private JLabel calculatedScoreLabel;
    private JTextField resultTextField;
    private JPanel buttonBar;
    private JHelpLabel helpLabel;
    private JButton testRuleButton;
    private JButton saveRuleButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
