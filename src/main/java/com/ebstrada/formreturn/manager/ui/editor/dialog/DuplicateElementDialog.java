package com.ebstrada.formreturn.manager.ui.editor.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.ui.DocumentAttributes;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.ui.editor.persistence.FieldnameDuplicatePresets;

public class DuplicateElementDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private DocumentAttributes documentAttributes;

    private int dialogResult = javax.swing.JOptionPane.CANCEL_OPTION;

    public DuplicateElementDialog(Frame owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(duplicateButton);
    }

    public DuplicateElementDialog(Dialog owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(duplicateButton);
    }

    private void duplicateButtonActionPerformed(ActionEvent e) {
        dialogResult = javax.swing.JOptionPane.OK_OPTION;
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dialogResult = javax.swing.JOptionPane.CANCEL_OPTION;
        dispose();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                duplicateButton.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        duplicationSettingsPanel = new JPanel();
        horizontalDuplicatesLabel = new JLabel();
        horizontalDuplicatesSpinner = new JSpinner();
        verticalDuplicatesLabel = new JLabel();
        verticalDuplicatesSpinner = new JSpinner();
        horizontalSpacingLabel = new JLabel();
        horizontalSpacingSpinner = new JSpinner();
        verticalSpacingLabel = new JLabel();
        verticalSpacingSpinner = new JSpinner();
        capturedDataFieldNameSettingsPanel = new JPanel();
        fieldnamePrefixLabel = new JLabel();
        fieldnamePrefixTextField = new JTextField();
        counterStartsAtLabel = new JLabel();
        panel3 = new JPanel();
        fieldnameCounterSpinner = new JSpinner();
        namingDirectionLabel = new JLabel();
        panel4 = new JPanel();
        tblrButton = new JRadioButton();
        lrtbButton = new JRadioButton();
        buttonBar = new JPanel();
        helpLabel = new JHelpLabel();
        duplicateButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "DuplicateElementsDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};

                //======== duplicationSettingsPanel ========
                {
                    duplicationSettingsPanel.setOpaque(false);
                    duplicationSettingsPanel.setFont(UIManager.getFont("TitledBorder.font"));
                    duplicationSettingsPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)duplicationSettingsPanel.getLayout()).columnWidths = new int[] {0, 0, 25, 0, 0, 0};
                    ((GridBagLayout)duplicationSettingsPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout)duplicationSettingsPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 1.0, 1.0E-4};
                    ((GridBagLayout)duplicationSettingsPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};
                    duplicationSettingsPanel.setBorder(new CompoundBorder(
                        new TitledBorder(Localizer.localize("UI", "DuplicateElementsDuplicationSettingsPanel")),
                        new EmptyBorder(5, 5, 5, 5)));

                    //---- horizontalDuplicatesLabel ----
                    horizontalDuplicatesLabel.setFont(UIManager.getFont("Label.font"));
                    horizontalDuplicatesLabel.setText(Localizer.localize("UI", "DuplicateElementsHorizontalDuplicatesLabel"));
                    duplicationSettingsPanel.add(horizontalDuplicatesLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- horizontalDuplicatesSpinner ----
                    horizontalDuplicatesSpinner.setModel(new SpinnerNumberModel(1, 0, 1000, 1));
                    horizontalDuplicatesSpinner.setFont(UIManager.getFont("Spinner.font"));
                    duplicationSettingsPanel.add(horizontalDuplicatesSpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- verticalDuplicatesLabel ----
                    verticalDuplicatesLabel.setFont(UIManager.getFont("Label.font"));
                    verticalDuplicatesLabel.setText(Localizer.localize("UI", "DuplicateElementsVerticalDuplicatesLabel"));
                    duplicationSettingsPanel.add(verticalDuplicatesLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- verticalDuplicatesSpinner ----
                    verticalDuplicatesSpinner.setModel(new SpinnerNumberModel(1, 0, 1000, 1));
                    verticalDuplicatesSpinner.setFont(UIManager.getFont("Spinner.font"));
                    duplicationSettingsPanel.add(verticalDuplicatesSpinner, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- horizontalSpacingLabel ----
                    horizontalSpacingLabel.setFont(UIManager.getFont("Label.font"));
                    horizontalSpacingLabel.setText(Localizer.localize("UI", "DuplicateElementsHorizontalSpacingLabel"));
                    duplicationSettingsPanel.add(horizontalSpacingLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- horizontalSpacingSpinner ----
                    horizontalSpacingSpinner.setModel(new SpinnerNumberModel(20, 0, 5000, 1));
                    horizontalSpacingSpinner.setFont(UIManager.getFont("Spinner.font"));
                    duplicationSettingsPanel.add(horizontalSpacingSpinner, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- verticalSpacingLabel ----
                    verticalSpacingLabel.setFont(UIManager.getFont("Label.font"));
                    verticalSpacingLabel.setText(Localizer.localize("UI", "DuplicateElementsVerticalSpacingLabel"));
                    duplicationSettingsPanel.add(verticalSpacingLabel, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- verticalSpacingSpinner ----
                    verticalSpacingSpinner.setModel(new SpinnerNumberModel(20, 0, 5000, 1));
                    verticalSpacingSpinner.setFont(UIManager.getFont("Spinner.font"));
                    duplicationSettingsPanel.add(verticalSpacingSpinner, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(duplicationSettingsPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== capturedDataFieldNameSettingsPanel ========
                {
                    capturedDataFieldNameSettingsPanel.setOpaque(false);
                    capturedDataFieldNameSettingsPanel.setFont(UIManager.getFont("TitledBorder.font"));
                    capturedDataFieldNameSettingsPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)capturedDataFieldNameSettingsPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
                    ((GridBagLayout)capturedDataFieldNameSettingsPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                    ((GridBagLayout)capturedDataFieldNameSettingsPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                    ((GridBagLayout)capturedDataFieldNameSettingsPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0E-4};
                    capturedDataFieldNameSettingsPanel.setBorder(new CompoundBorder(
                        new TitledBorder(Localizer.localize("UI", "DuplicateElementsCDFNSettingsPanel")),
                        new EmptyBorder(5, 5, 5, 5)));

                    //---- fieldnamePrefixLabel ----
                    fieldnamePrefixLabel.setFont(UIManager.getFont("Label.font"));
                    fieldnamePrefixLabel.setText(Localizer.localize("UI", "DuplicateElementsFieldnamePrefixLabel"));
                    capturedDataFieldNameSettingsPanel.add(fieldnamePrefixLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- fieldnamePrefixTextField ----
                    fieldnamePrefixTextField.setText("fieldname");
                    capturedDataFieldNameSettingsPanel.add(fieldnamePrefixTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- counterStartsAtLabel ----
                    counterStartsAtLabel.setFont(UIManager.getFont("Label.font"));
                    counterStartsAtLabel.setText(Localizer.localize("UI", "DuplicateElementsCouterStartsAtLabel"));
                    capturedDataFieldNameSettingsPanel.add(counterStartsAtLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //======== panel3 ========
                    {
                        panel3.setOpaque(false);
                        panel3.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {105, 0, 0};
                        ((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- fieldnameCounterSpinner ----
                        fieldnameCounterSpinner.setModel(new SpinnerNumberModel(2, 0, 9999999, 1));
                        fieldnameCounterSpinner.setFont(UIManager.getFont("Spinner.font"));
                        panel3.add(fieldnameCounterSpinner, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    capturedDataFieldNameSettingsPanel.add(panel3, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- namingDirectionLabel ----
                    namingDirectionLabel.setFont(UIManager.getFont("Label.font"));
                    namingDirectionLabel.setText(Localizer.localize("UI", "DuplicateElementsNamingDirectionLabel"));
                    capturedDataFieldNameSettingsPanel.add(namingDirectionLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //======== panel4 ========
                    {
                        panel4.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel4.getLayout()).columnWidths = new int[] {0, 0, 0};
                        ((GridBagLayout)panel4.getLayout()).rowHeights = new int[] {0, 0, 0};
                        ((GridBagLayout)panel4.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)panel4.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

                        //---- tblrButton ----
                        tblrButton.setSelected(true);
                        tblrButton.setFont(UIManager.getFont("RadioButton.font"));
                        tblrButton.setOpaque(false);
                        tblrButton.setText(Localizer.localize("UI", "DuplicateElementsTBLRButtonText"));
                        panel4.add(tblrButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 5), 0, 0));

                        //---- lrtbButton ----
                        lrtbButton.setFont(UIManager.getFont("RadioButton.font"));
                        lrtbButton.setOpaque(false);
                        lrtbButton.setText(Localizer.localize("UI", "DuplicateElementsLRTBButtonText"));
                        panel4.add(lrtbButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    capturedDataFieldNameSettingsPanel.add(panel4, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(capturedDataFieldNameSettingsPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
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
                helpLabel.setHelpGUID("duplicate");
                helpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                buttonBar.add(helpLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- duplicateButton ----
                duplicateButton.setFont(UIManager.getFont("Button.font"));
                duplicateButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        duplicateButtonActionPerformed(e);
                    }
                });
                duplicateButton.setText(Localizer.localize("UI", "DuplicateButtonText"));
                buttonBar.add(duplicateButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
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
        setSize(565, 355);
        setLocationRelativeTo(null);

        //---- namingDirectionButtonGroup ----
        ButtonGroup namingDirectionButtonGroup = new ButtonGroup();
        namingDirectionButtonGroup.add(tblrButton);
        namingDirectionButtonGroup.add(lrtbButton);
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel duplicationSettingsPanel;
    private JLabel horizontalDuplicatesLabel;
    private JSpinner horizontalDuplicatesSpinner;
    private JLabel verticalDuplicatesLabel;
    private JSpinner verticalDuplicatesSpinner;
    private JLabel horizontalSpacingLabel;
    private JSpinner horizontalSpacingSpinner;
    private JLabel verticalSpacingLabel;
    private JSpinner verticalSpacingSpinner;
    private JPanel capturedDataFieldNameSettingsPanel;
    private JLabel fieldnamePrefixLabel;
    private JTextField fieldnamePrefixTextField;
    private JLabel counterStartsAtLabel;
    private JPanel panel3;
    private JSpinner fieldnameCounterSpinner;
    private JLabel namingDirectionLabel;
    private JPanel panel4;
    private JRadioButton tblrButton;
    private JRadioButton lrtbButton;
    private JPanel buttonBar;
    private JHelpLabel helpLabel;
    private JButton duplicateButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration //GEN-END:variables

    public void setFieldnameCounter(int fieldnameCounterStart) {
        fieldnameCounterSpinner.setValue(new Integer(fieldnameCounterStart));
    }

    public void setFieldnamePrefix(String fieldnamePrefix) {
        fieldnamePrefixTextField.setText(fieldnamePrefix);
    }

    public int getNamingDirection() {
        if (tblrButton.isSelected()) {
            return FieldnameDuplicatePresets.DIRECTION_TOP_TO_BOTTOM_LEFT_TO_RIGHT;
        } else {
            return FieldnameDuplicatePresets.DIRECTION_LEFT_TO_RIGHT_TOP_TO_BOTTOM;
        }
    }

    public int getCounterStart() {
        return (Integer) fieldnameCounterSpinner.getValue();
    }

    public String getFieldnamePrefix() {
        return fieldnamePrefixTextField.getText().trim();
    }

    public int getVerticalSpacing() {
        return (Integer) verticalSpacingSpinner.getValue();
    }

    public int getHorizontalSpacing() {
        return (Integer) horizontalSpacingSpinner.getValue();
    }

    public int getVerticalDuplicates() {
        return (Integer) verticalDuplicatesSpinner.getValue();
    }

    public int getHorizontalDuplicates() {
        return (Integer) horizontalDuplicatesSpinner.getValue();
    }

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    public DocumentAttributes getDocumentAttributes() {
        return documentAttributes;
    }

    public void setDocumentAttributes(DocumentAttributes documentAttributes) {
        this.documentAttributes = documentAttributes;

        horizontalDuplicatesSpinner
            .setValue(documentAttributes.getFieldnameDuplicatePresets().getHorizontalDuplicates());
        verticalDuplicatesSpinner
            .setValue(documentAttributes.getFieldnameDuplicatePresets().getVerticalDuplicates());
        horizontalSpacingSpinner
            .setValue(documentAttributes.getFieldnameDuplicatePresets().getHorizontalSpacing());
        verticalSpacingSpinner
            .setValue(documentAttributes.getFieldnameDuplicatePresets().getVerticalSpacing());

        // set direction
        int nd = documentAttributes.getFieldnameDuplicatePresets().getNamingDirection();
        if (nd == FieldnameDuplicatePresets.DIRECTION_TOP_TO_BOTTOM_LEFT_TO_RIGHT) {
            lrtbButton.setSelected(false);
            tblrButton.setSelected(true);
        } else {
            tblrButton.setSelected(false);
            lrtbButton.setSelected(true);
        }

    }
}
