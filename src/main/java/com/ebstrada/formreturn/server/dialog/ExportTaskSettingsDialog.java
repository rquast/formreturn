package com.ebstrada.formreturn.server.dialog;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;
import com.ebstrada.formreturn.manager.ui.component.*;

import org.quartz.CronTrigger;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.export.ExportOptions;
import com.ebstrada.formreturn.manager.ui.cdm.dialog.ExportOptionsDialog;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.server.quartz.ITriggerTypes;

@SuppressWarnings("serial") public class ExportTaskSettingsDialog extends JDialog {

    public static final int CANCEL = 0;

    public static final int SAVE = 1;

    private int dialogResult = CANCEL;

    private ExportOptionsDialog eod;

    private ExportOptions exportOptions;

    public ExportTaskSettingsDialog(Frame owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(saveButton);
    }

    private void restoreExportOptionsDialog(ExportOptions exportOptions) {
        if (exportOptions != null) {
            eod = new ExportOptionsDialog((Dialog) getRootPane().getTopLevelAncestor(),
                exportOptions);
        } else {
            eod = new ExportOptionsDialog((Dialog) getRootPane().getTopLevelAncestor());
        }
    }

    public ExportTaskSettingsDialog(Dialog owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(saveButton);
    }

    public String getDescription() {
        return taskDescriptionTextField.getText().trim();
    }

    public void setDescription(String description) {
        taskDescriptionTextField.setText(description);
    }

    public void setPublicationIds(ArrayList<Long> publicationIds) {
        this.publicationIDsTextField.setText(Misc.implode(publicationIds, ", "));
    }

    public ArrayList<Long> getPublicationIds() {
        String str = publicationIDsTextField.getText();
        String[] strArr = str.split(",");

        ArrayList<Long> publicationIds = new ArrayList<Long>();
        for (String strPart : strArr) {
            long publicationId = Misc.parseLongString(strPart);
            publicationIds.add(publicationId);
        }

        return publicationIds;
    }

    public void restorePreferences(ExportOptions exportOptions) {
        this.exportOptions = exportOptions;
        setPublicationIds(exportOptions.getPublicationIds());
        restoreExportOptionsDialog(exportOptions);
    }

    public boolean isAutoStart() {
        return launchOnStartupCheckBox.isSelected();
    }

    public void setAutoStart(boolean autoStart) {
        launchOnStartupCheckBox.setSelected(autoStart);
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                saveButton.requestFocusInWindow();
            }
        });
    }

    public int getDialogResult() {
        return this.dialogResult;
    }

    public String getCronExpression() {
        return cronExpressionTriggerTextField.getText().trim();
    }

    public void setCronExpression(String cronExpression) {
        cronExpressionTriggerTextField.setText(cronExpression);
    }

    private void saveButtonActionPerformed(ActionEvent e) {
        if (checkCronExpression()) {
            if (eod == null) {
                restoreExportOptionsDialog(this.exportOptions);
                eod.setModal(true);
                eod.setVisible(true);
                if (eod.getDialogResult() != JOptionPane.OK_OPTION) {
                    this.eod = null;
                    return;
                } else {
                    this.dialogResult = SAVE;
                }
            } else {
                this.dialogResult = SAVE;
            }
            dispose();
        }
    }

    private boolean checkCronExpression() {
        if (pollTriggerRadioButton.isSelected()) {
            return true;
        }
        CronTrigger cronTrigger = new CronTrigger();
        try {
            cronTrigger.setCronExpression(getCronExpression());
            return true;
        } catch (ParseException e) {
            Misc.showExceptionMsg(getRootPane().getTopLevelAncestor(), e);
            return false;
        }
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        this.dialogResult = CANCEL;
        dispose();
    }

    public int getInterval() {
        return (Integer) this.pollFrequencySpinner.getValue() * 1000;
    }

    public void setInterval(int interval) {
        this.pollFrequencySpinner.setValue(interval / 1000);
    }

    public int getTriggerType() {
        if (pollTriggerRadioButton.isSelected()) {
            return ITriggerTypes.SIMPLE_TRIGGER;
        } else if (cronTriggerRadioButton.isSelected()) {
            return ITriggerTypes.CRON_TRIGGER;
        } else {
            return ITriggerTypes.SIMPLE_TRIGGER;
        }
    }

    public void setTriggerType(int triggerType) {
        if (triggerType == ITriggerTypes.SIMPLE_TRIGGER) {
            pollTriggerRadioButton.setSelected(true);
        } else if (triggerType == ITriggerTypes.CRON_TRIGGER) {
            cronTriggerRadioButton.setSelected(true);
        } else {
            pollTriggerRadioButton.setSelected(true);
        }
    }

    private void exportSettingsButtonActionPerformed(ActionEvent e) {
        if (eod == null) {
            restoreExportOptionsDialog(this.exportOptions);
        }
        eod.setModal(true);
        eod.setVisible(true);
        if (eod.getDialogResult() != JOptionPane.OK_OPTION) {
            this.eod = null;
        }
    }

    public ExportOptionsDialog getExportOptionsDialog() {
        return this.eod;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        contentBorderPanel = new JPanel();
        taskDescriptionPanel = new JPanel();
        taskDescriptionTextField = new JTextField();
        triggerSettingsPanel = new JPanel();
        triggerSettingsSubPanel = new JPanel();
        pollTriggerRadioButton = new JRadioButton();
        pollFrequencyLabel = new JLabel();
        pollFrequencySpinner = new JSpinner();
        cronTriggerRadioButton = new JRadioButton();
        cronExpressionLabel = new JLabel();
        cronExpressionTriggerTextField = new JTextField();
        publicationsToExportPanel = new JPanel();
        publicationIDsTextField = new JTextField();
        exportSettingsPanel = new JPanel();
        exportSettingsButton = new JButton();
        exportSettingsDescriptionLabel = new JLabel();
        buttonBar = new JPanel();
        helpLabel = new JHelpLabel();
        launchOnStartupCheckBox = new JCheckBox();
        saveButton = new JButton();
        cancelButton = new JButton();
        triggerTypeButtonGroup = new ButtonGroup();

        //======== this ========
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
                ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //======== contentBorderPanel ========
                {
                    contentBorderPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                    contentBorderPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)contentBorderPanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)contentBorderPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
                    ((GridBagLayout)contentBorderPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)contentBorderPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0, 1.0E-4};
                    triggerSettingsPanel.setBorder(new CompoundBorder(
                                        new TitledBorder(Localizer.localize("Server", "TriggerSettingsBorderTitle")),
                                        new EmptyBorder(5, 5, 5, 5)));

                    //======== taskDescriptionPanel ========
                    {
                        taskDescriptionPanel.setFont(UIManager.getFont("Panel.font"));
                        taskDescriptionPanel.setOpaque(false);
                        taskDescriptionPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)taskDescriptionPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)taskDescriptionPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)taskDescriptionPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)taskDescriptionPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                        taskDescriptionPanel.setBorder(new CompoundBorder(
                            new TitledBorder(Localizer.localize("Server", "TaskDescriptionBorderTitle")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //---- taskDescriptionTextField ----
                        taskDescriptionTextField.setFont(UIManager.getFont("TextField.font"));
                        taskDescriptionPanel.add(taskDescriptionTextField, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    contentBorderPanel.add(taskDescriptionPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== triggerSettingsPanel ========
                    {
                        triggerSettingsPanel.setFont(UIManager.getFont("Panel.font"));
                        triggerSettingsPanel.setOpaque(false);
                        triggerSettingsPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)triggerSettingsPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)triggerSettingsPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)triggerSettingsPanel.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
                        ((GridBagLayout)triggerSettingsPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                        triggerSettingsPanel.setBorder(new CompoundBorder(
                            new TitledBorder(Localizer.localize("Server", "TriggerSettingsBorderTitle")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //======== triggerSettingsSubPanel ========
                        {
                            triggerSettingsSubPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)triggerSettingsSubPanel.getLayout()).columnWidths = new int[] {0, 0, 15, 220, 0};
                            ((GridBagLayout)triggerSettingsSubPanel.getLayout()).rowHeights = new int[] {35, 30, 0};
                            ((GridBagLayout)triggerSettingsSubPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
                            ((GridBagLayout)triggerSettingsSubPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};

                            //---- pollTriggerRadioButton ----
                            pollTriggerRadioButton.setHorizontalAlignment(SwingConstants.RIGHT);
                            pollTriggerRadioButton.setSelected(true);
                            pollTriggerRadioButton.setFont(UIManager.getFont("RadioButton.font"));
                            triggerSettingsSubPanel.add(pollTriggerRadioButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 5, 5), 0, 0));

                            //---- pollFrequencyLabel ----
                            pollFrequencyLabel.setFont(UIManager.getFont("Label.font"));
                            pollFrequencyLabel.setText(Localizer.localize("Server", "PollFrequencyLabelText"));
                            triggerSettingsSubPanel.add(pollFrequencyLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.WEST, GridBagConstraints.NONE,
                                new Insets(0, 0, 5, 5), 0, 0));

                            //---- pollFrequencySpinner ----
                            pollFrequencySpinner.setModel(new SpinnerNumberModel(86400, 1, 1814400, 1));
                            pollFrequencySpinner.setFont(UIManager.getFont("Spinner.font"));
                            triggerSettingsSubPanel.add(pollFrequencySpinner, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 5, 0), 0, 0));

                            //---- cronTriggerRadioButton ----
                            cronTriggerRadioButton.setHorizontalAlignment(SwingConstants.RIGHT);
                            cronTriggerRadioButton.setFont(UIManager.getFont("RadioButton.font"));
                            triggerSettingsSubPanel.add(cronTriggerRadioButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- cronExpressionLabel ----
                            cronExpressionLabel.setFont(UIManager.getFont("Label.font"));
                            cronExpressionLabel.setText(Localizer.localize("Server", "CronExpressionLabelText"));
                            triggerSettingsSubPanel.add(cronExpressionLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.WEST, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- cronExpressionTriggerTextField ----
                            cronExpressionTriggerTextField.setText("0 30 3 ? * *");
                            cronExpressionTriggerTextField.setFont(UIManager.getFont("TextField.font"));
                            cronExpressionTriggerTextField.setHorizontalAlignment(SwingConstants.RIGHT);
                            triggerSettingsSubPanel.add(cronExpressionTriggerTextField, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        triggerSettingsPanel.add(triggerSettingsSubPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    contentBorderPanel.add(triggerSettingsPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== publicationsToExportPanel ========
                    {
                        publicationsToExportPanel.setFont(UIManager.getFont("Panel.font"));
                        publicationsToExportPanel.setOpaque(false);
                        publicationsToExportPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)publicationsToExportPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)publicationsToExportPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)publicationsToExportPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)publicationsToExportPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                        publicationsToExportPanel.setBorder(new CompoundBorder(
                            new TitledBorder(Localizer.localize("Server", "PublicationsToExportBorderTitle")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //---- publicationIDsTextField ----
                        publicationIDsTextField.setFont(UIManager.getFont("TextField.font"));
                        publicationsToExportPanel.add(publicationIDsTextField, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    contentBorderPanel.add(publicationsToExportPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== exportSettingsPanel ========
                    {
                        exportSettingsPanel.setFont(UIManager.getFont("Panel.font"));
                        exportSettingsPanel.setOpaque(false);
                        exportSettingsPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)exportSettingsPanel.getLayout()).columnWidths = new int[] {45, 0, 15, 0, 0};
                        ((GridBagLayout)exportSettingsPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)exportSettingsPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)exportSettingsPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                        exportSettingsPanel.setBorder(new CompoundBorder(
                            new TitledBorder(Localizer.localize("Server", "ExportSettingsBorderTitle")),
                            new EmptyBorder(5, 5, 5, 5)));

                        //---- exportSettingsButton ----
                        exportSettingsButton.setFont(UIManager.getFont("Button.font"));
                        exportSettingsButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/sdm/table_go.png")));
                        exportSettingsButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                exportSettingsButtonActionPerformed(e);
                            }
                        });
                        exportSettingsButton.setText(Localizer.localize("Server", "ExportSettingsButtonText"));
                        exportSettingsPanel.add(exportSettingsButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- exportSettingsDescriptionLabel ----
                        exportSettingsDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        exportSettingsDescriptionLabel.setText(Localizer.localize("Server", "ExportSettingsDescriptionLabelText"));
                        exportSettingsPanel.add(exportSettingsDescriptionLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    contentBorderPanel.add(exportSettingsPanel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(contentBorderPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 35, 0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 0.0, 0.0};

                //---- helpLabel ----
                helpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                helpLabel.setHelpGUID("export-task");
                helpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                buttonBar.add(helpLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- launchOnStartupCheckBox ----
                launchOnStartupCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
                launchOnStartupCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                launchOnStartupCheckBox.setSelected(true);
                launchOnStartupCheckBox.setText(Localizer.localize("Server", "LaunchOnStartupCheckBoxText"));
                buttonBar.add(launchOnStartupCheckBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- saveButton ----
                saveButton.setFont(UIManager.getFont("Button.font"));
                saveButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
                saveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveButtonActionPerformed(e);
                    }
                });
                saveButton.setText(Localizer.localize("Server", "SaveButtonText"));
                buttonBar.add(saveButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
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
                cancelButton.setText(Localizer.localize("Server", "CancelButtonText"));
                buttonBar.add(cancelButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(775, 440);
        setLocationRelativeTo(getOwner());

        //---- triggerTypeButtonGroup ----
        triggerTypeButtonGroup.add(pollTriggerRadioButton);
        triggerTypeButtonGroup.add(cronTriggerRadioButton);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel contentBorderPanel;
    private JPanel taskDescriptionPanel;
    private JTextField taskDescriptionTextField;
    private JPanel triggerSettingsPanel;
    private JPanel triggerSettingsSubPanel;
    private JRadioButton pollTriggerRadioButton;
    private JLabel pollFrequencyLabel;
    private JSpinner pollFrequencySpinner;
    private JRadioButton cronTriggerRadioButton;
    private JLabel cronExpressionLabel;
    private JTextField cronExpressionTriggerTextField;
    private JPanel publicationsToExportPanel;
    private JTextField publicationIDsTextField;
    private JPanel exportSettingsPanel;
    private JButton exportSettingsButton;
    private JLabel exportSettingsDescriptionLabel;
    private JPanel buttonBar;
    private JHelpLabel helpLabel;
    private JCheckBox launchOnStartupCheckBox;
    private JButton saveButton;
    private JButton cancelButton;
    private ButtonGroup triggerTypeButtonGroup;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
