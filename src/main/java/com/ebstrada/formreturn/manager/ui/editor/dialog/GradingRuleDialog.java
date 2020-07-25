package com.ebstrada.formreturn.manager.ui.editor.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.ui.editor.persistence.MarkingRule;
import com.ebstrada.formreturn.manager.util.Misc;

public class GradingRuleDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private int dialogResult = javax.swing.JOptionPane.CANCEL_OPTION;

    private int qualifier = MarkingRule.QUALIFIER_GREATER_THAN_OR_EQUAL_TO;

    private double threshold = 0.0d;

    private int thresholdType = MarkingRule.THRESHOLD_IS_MARK;

    private String grade;

    public GradingRuleDialog(Frame owner, MarkingRule gradingRule) {
        super(owner);
        this.qualifier = gradingRule.getQualifier();
        this.grade = gradingRule.getGrade();
        this.threshold = gradingRule.getThreshold();
        this.thresholdType = gradingRule.getThresholdType();
        initComponents();
        restore();
    }

    public GradingRuleDialog(Dialog owner, MarkingRule gradingRule) {
        super(owner);
        this.qualifier = gradingRule.getQualifier();
        this.grade = gradingRule.getGrade();
        this.threshold = gradingRule.getThreshold();
        this.thresholdType = gradingRule.getThresholdType();
        initComponents();
        restore();
    }

    public void restore() {

        getRootPane().setDefaultButton(okButton);

        this.qualifierComboBox.setModel(MarkingRule.getComboBoxModel());
        this.gradeTextField.setText(this.grade);

        if (this.thresholdType == MarkingRule.THRESHOLD_IS_PERCENTAGE) {
            this.thresholdTextField.setText(this.threshold + "%");
        } else {
            this.thresholdTextField.setText(this.threshold + "");
        }

        this.qualifierComboBox.setSelectedIndex(this.qualifier);
    }

    public int getQualifier() {
        return qualifier;
    }

    public void setQualifier(int qualifier) {
        this.qualifier = qualifier;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public int getThresholdType() {
        return thresholdType;
    }

    public void setThresholdType(int thresholdType) {
        this.thresholdType = thresholdType;
    }

    private void SaveButtonActionPerformed(ActionEvent e) {

        this.grade = this.gradeTextField.getText().trim();
        this.qualifier = this.qualifierComboBox.getSelectedIndex();

        if (this.thresholdTextField.getText().trim().endsWith("%")) {
            this.thresholdType = MarkingRule.THRESHOLD_IS_PERCENTAGE;
        } else {
            this.thresholdType = MarkingRule.THRESHOLD_IS_MARK;
        }

        try {
            this.threshold =
                Misc.parseDoubleString(this.thresholdTextField.getText().trim().replace("%", ""));
        } catch (Exception ex) {
            Misc.showErrorMsg(this, "Invalid threshold mark");
            return;
        }

        this.dialogResult = JOptionPane.OK_OPTION;
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
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
                okButton.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        qualifierLabel = new JLabel();
        panel1 = new JPanel();
        qualifierComboBox = new JComboBox();
        thresholdTextField = new JTextField();
        gradeLabel = new JLabel();
        gradeTextField = new JTextField();
        buttonBar = new JPanel();
        helpLabel = new JHelpLabel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setName("addGradingRuleDialog");
        setModal(true);
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UI", "GradingRuleDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setFont(UIManager.getFont("Panel.font"));
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
                ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};

                //---- qualifierLabel ----
                qualifierLabel.setFont(UIManager.getFont("Label.font"));
                qualifierLabel.setText(Localizer.localize("UI", "QualifierLabelText"));
                contentPanel.add(qualifierLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                    new Insets(0, 0, 5, 5), 0, 0));

                //======== panel1 ========
                {
                    panel1.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0, 0};
                    ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                    ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                    //---- qualifierComboBox ----
                    qualifierComboBox.setFont(UIManager.getFont("ComboBox.font"));
                    panel1.add(qualifierComboBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- thresholdTextField ----
                    thresholdTextField.setFont(UIManager.getFont("TextField.font"));
                    panel1.add(thresholdTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(panel1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 5, 0), 0, 0));

                //---- gradeLabel ----
                gradeLabel.setFont(UIManager.getFont("Label.font"));
                gradeLabel.setText(Localizer.localize("UI", "GradeLabelText"));
                contentPanel.add(gradeLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- gradeTextField ----
                gradeTextField.setFont(UIManager.getFont("TextField.font"));
                contentPanel.add(gradeTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
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
                helpLabel.setHelpGUID("publication-grading-rules");
                helpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                buttonBar.add(helpLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- okButton ----
                okButton.setFont(UIManager.getFont("Button.font"));
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SaveButtonActionPerformed(e);
                    }
                });
                okButton.setText(Localizer.localize("UI", "OKButtonText"));
                buttonBar.add(okButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
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
        setSize(475, 220);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel qualifierLabel;
    private JPanel panel1;
    private JComboBox qualifierComboBox;
    private JTextField thresholdTextField;
    private JLabel gradeLabel;
    private JTextField gradeTextField;
    private JPanel buttonBar;
    private JHelpLabel helpLabel;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
