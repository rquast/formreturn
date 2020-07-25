package com.ebstrada.formreturn.manager.ui.sdm.dialog;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.cdm.logic.FillTableController;
import com.ebstrada.formreturn.manager.ui.component.*;

@SuppressWarnings("serial") public class FillTableDialog extends JDialog {

    private int dialogResult = javax.swing.JOptionPane.CANCEL_OPTION;

    public FillTableDialog(Frame owner) {
        super(owner);
        initComponents();
        setTitle(Localizer.localize("UI", "FillTableDialogTitle"));
        getRootPane().setDefaultButton(fillButton);
    }

    public FillTableDialog(Dialog owner) {
        super(owner);
        initComponents();
        setTitle(Localizer.localize("UI", "FillTableDialogTitle"));
        getRootPane().setDefaultButton(fillButton);
    }

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    private void fillButtonActionPerformed(ActionEvent e) {
        setDialogResult(javax.swing.JOptionPane.OK_OPTION);
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        setDialogResult(JOptionPane.CANCEL_OPTION);
        dispose();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fillButton.requestFocusInWindow();
            }
        });
    }

    private void thisWindowClosing(WindowEvent e) {
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        fillTableDescriptionPanel = new JPanel();
        fillTableDescriptionLabel = new JLabel();
        fillTableHelpLabel = new JHelpLabel();
        fillTypePanel = new JPanel();
        seriesRadioButton = new JRadioButton();
        stepSizePanel = new JPanel();
        stepSizeSpinner = new JSpinner();
        duplicatesRadioButton = new JRadioButton();
        cellsToFillPanel = new JPanel();
        cellCountSpinner = new JSpinner();
        buttonBar = new JPanel();
        fillButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                thisWindowClosing(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UI", "FillTableDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            dialogPane.setLayout(new GridBagLayout());
            ((GridBagLayout)dialogPane.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout)dialogPane.getLayout()).rowHeights = new int[] {0, 0, 0};
            ((GridBagLayout)dialogPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout)dialogPane.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};

                //======== fillTableDescriptionPanel ========
                {
                    fillTableDescriptionPanel.setBorder(new CompoundBorder(
                        new MatteBorder(0, 0, 3, 0, Color.gray),
                        new EmptyBorder(5, 5, 5, 5)));
                    fillTableDescriptionPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)fillTableDescriptionPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                    ((GridBagLayout)fillTableDescriptionPanel.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)fillTableDescriptionPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                    ((GridBagLayout)fillTableDescriptionPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                    //---- fillTableDescriptionLabel ----
                    fillTableDescriptionLabel.setText("<html><body><strong>Fill From Selected Value</strong></body></html>");
                    fillTableDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                    fillTableDescriptionLabel.setText("<html><body><strong>" + Localizer.localize("UI", "FillTableDescriptionLabel") + "</strong></body></html>");
                    fillTableDescriptionPanel.add(fillTableDescriptionLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- fillTableHelpLabel ----
                    fillTableHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                    fillTableHelpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    fillTableHelpLabel.setFont(UIManager.getFont("Label.font"));
                    fillTableHelpLabel.setHelpGUID("source-data-fill-cells");
                    fillTableHelpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                    fillTableDescriptionPanel.add(fillTableHelpLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));
                }
                contentPanel.add(fillTableDescriptionPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== fillTypePanel ========
                {
                    fillTypePanel.setOpaque(false);
                    fillTypePanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)fillTypePanel.getLayout()).columnWidths = new int[] {0, 15, 0, 0};
                    ((GridBagLayout)fillTypePanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout)fillTypePanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
                    ((GridBagLayout)fillTypePanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};
                    fillTypePanel.setBorder(new CompoundBorder(
                        new TitledBorder(Localizer.localize("UI", "FillTypePanel")),
                        new EmptyBorder(5, 5, 5, 5)));

                    //---- seriesRadioButton ----
                    seriesRadioButton.setSelected(true);
                    seriesRadioButton.setFont(UIManager.getFont("RadioButton.font"));
                    seriesRadioButton.setOpaque(false);
                    seriesRadioButton.setText(Localizer.localize("UI", "FillSeriesRadioButtonText"));
                    fillTypePanel.add(seriesRadioButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //======== stepSizePanel ========
                    {
                        stepSizePanel.setOpaque(false);
                        stepSizePanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)stepSizePanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)stepSizePanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)stepSizePanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)stepSizePanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};
                        stepSizePanel.setBorder(new CompoundBorder(
                            new TitledBorder(Localizer.localize("UI", "StepSizePanel")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //---- stepSizeSpinner ----
                        stepSizeSpinner.setFont(UIManager.getFont("Spinner.font"));
                        stepSizeSpinner.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
                        stepSizePanel.add(stepSizeSpinner, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    fillTypePanel.add(stepSizePanel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- duplicatesRadioButton ----
                    duplicatesRadioButton.setFont(UIManager.getFont("RadioButton.font"));
                    duplicatesRadioButton.setOpaque(false);
                    duplicatesRadioButton.setText(Localizer.localize("UI", "FillDuplicatesRadioButtonText"));
                    fillTypePanel.add(duplicatesRadioButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));
                }
                contentPanel.add(fillTypePanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== cellsToFillPanel ========
                {
                    cellsToFillPanel.setOpaque(false);
                    cellsToFillPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)cellsToFillPanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)cellsToFillPanel.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)cellsToFillPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)cellsToFillPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};
                    cellsToFillPanel.setBorder(new CompoundBorder(
                        new TitledBorder(Localizer.localize("UI", "CellsToFillPanel")),
                        new EmptyBorder(5, 5, 5, 5)));

                    //---- cellCountSpinner ----
                    cellCountSpinner.setFont(UIManager.getFont("Spinner.font"));
                    cellCountSpinner.setModel(new SpinnerNumberModel(1, 1, 10000, 1));
                    cellsToFillPanel.add(cellCountSpinner, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(cellsToFillPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

                //---- fillButton ----
                fillButton.setFont(UIManager.getFont("Button.font"));
                fillButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/paintcan.png")));
                fillButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        fillButtonActionPerformed(e);
                    }
                });
                fillButton.setText(Localizer.localize("UI", "FillButtonText"));
                buttonBar.add(fillButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UI", "CancelButtonText"));
                buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(340, 335);
        setLocationRelativeTo(getOwner());

        //---- fillTypeButtonGroup ----
        ButtonGroup fillTypeButtonGroup = new ButtonGroup();
        fillTypeButtonGroup.add(seriesRadioButton);
        fillTypeButtonGroup.add(duplicatesRadioButton);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel fillTableDescriptionPanel;
    private JLabel fillTableDescriptionLabel;
    private JHelpLabel fillTableHelpLabel;
    private JPanel fillTypePanel;
    private JRadioButton seriesRadioButton;
    private JPanel stepSizePanel;
    private JSpinner stepSizeSpinner;
    private JRadioButton duplicatesRadioButton;
    private JPanel cellsToFillPanel;
    private JSpinner cellCountSpinner;
    private JPanel buttonBar;
    private JButton fillButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public int getFillType() {
        if (seriesRadioButton.isSelected()) {
            return FillTableController.FILL_NUMERIC_SERIES;
        } else {
            return FillTableController.FILL_DUPLICATE;
        }
    }

    public int getStepSize() {
        return (Integer) this.stepSizeSpinner.getValue();
    }

    public int getCellsToFill() {
        return (Integer) this.cellCountSpinner.getValue();
    }

}
