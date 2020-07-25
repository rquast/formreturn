package com.ebstrada.formreturn.server.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.server.preferences.persistence.FolderMonitorJobPreferences;
import com.ebstrada.formreturn.server.preferences.persistence.ImageFolderMonitorJobPreferences;
import com.ebstrada.formreturn.server.preferences.persistence.SourceDataFolderMonitorJobPreferences;
import com.ebstrada.formreturn.server.preferences.persistence.TaskSchedulerJobPreferences;
import com.ebstrada.formreturn.server.quartz.ITriggerTypes;
import com.ebstrada.formreturn.server.quartz.job.FolderMonitorJob;

public class FolderMonitorTaskSettingsDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private int dialogResult = JOptionPane.CANCEL_OPTION;

    private FolderMonitorJobPreferences taskSchedulerJobPreferences;

    public FolderMonitorTaskSettingsDialog(Frame owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(saveButton);
        localize();
    }

    private void localize() {
        typeOfMonitorComboBox.setModel(new DefaultComboBoxModel(
            new String[] {Localizer.localize("Server", "FolderMonitorType0"),
                Localizer.localize("Server", "FolderMonitorType1")}));
    }

    public FolderMonitorTaskSettingsDialog(Dialog owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(saveButton);
        localize();
    }

    public void restorePreferences(TaskSchedulerJobPreferences preferences) {
        this.taskSchedulerJobPreferences = (FolderMonitorJobPreferences) preferences;
        this.descriptionTextField.setText(preferences.getDescription());
        int type = 0;
        if (preferences instanceof ImageFolderMonitorJobPreferences) {
            type = FolderMonitorJob.IMAGE_FOLDER_MONITOR_JOB;
            this.unprocessedFolderTextField
                .setText(((ImageFolderMonitorJobPreferences) preferences).getSourceDirectory());
            this.processedFolderTextField.setText(
                ((ImageFolderMonitorJobPreferences) preferences).getDestinationDirectory());
        } else if (preferences instanceof SourceDataFolderMonitorJobPreferences) {
            type = FolderMonitorJob.SOURCE_DATA_FOLDER_MONITOR_JOB;
            this.unprocessedFolderTextField.setText(
                ((SourceDataFolderMonitorJobPreferences) preferences).getSourceDirectory());
            this.processedFolderTextField.setText(
                ((SourceDataFolderMonitorJobPreferences) preferences).getDestinationDirectory());
        }
        this.typeOfMonitorComboBox.setSelectedIndex(type);
        this.pollFrequencySpinner.setValue(preferences.getInterval() / 1000);
        this.launchOnStartupCheckBox.setSelected(preferences.isAutoStart());
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                saveButton.requestFocusInWindow();
            }
        });
    }

    public TaskSchedulerJobPreferences getTaskSchedulerJobPreferences() {
        return taskSchedulerJobPreferences;
    }

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    private void unprocessedFolderBrowseButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle(
                    Localizer.localize("Server", "SelectUnprocessedFolderDialogTitle"));
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                chooser.rescanCurrentDirectory();

                if (chooser.showOpenDialog(getRootPane().getTopLevelAncestor())
                    == JFileChooser.APPROVE_OPTION) {
                    File unprocessedImagesDirectory = chooser.getSelectedFile();
                    unprocessedFolderTextField.setText(unprocessedImagesDirectory.getPath());
                }
            }
        });
    }

    private void processedFolderBrowseButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle(
                    Localizer.localize("Server", "SelectProcessedFolderDialogTitle"));
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                chooser.rescanCurrentDirectory();

                if (chooser.showOpenDialog(getRootPane().getTopLevelAncestor())
                    == JFileChooser.APPROVE_OPTION) {
                    File processedImagesDirectory = chooser.getSelectedFile();
                    processedFolderTextField.setText(processedImagesDirectory.getPath());
                }
            }
        });
    }

    private void saveButtonActionPerformed(ActionEvent e) {

        switch (this.typeOfMonitorComboBox.getSelectedIndex()) {

            case FolderMonitorJob.IMAGE_FOLDER_MONITOR_JOB:
                this.taskSchedulerJobPreferences = new ImageFolderMonitorJobPreferences();
                break;
            case FolderMonitorJob.SOURCE_DATA_FOLDER_MONITOR_JOB:
                this.taskSchedulerJobPreferences = new SourceDataFolderMonitorJobPreferences();
                break;
        }

        this.taskSchedulerJobPreferences
            .setSourceDirectory(this.unprocessedFolderTextField.getText());
        this.taskSchedulerJobPreferences
            .setDestinationDirectory(this.processedFolderTextField.getText());
        this.taskSchedulerJobPreferences
            .setInterval((Integer) this.pollFrequencySpinner.getValue() * 1000);
        this.taskSchedulerJobPreferences.setDescription(this.descriptionTextField.getText().trim());
        this.taskSchedulerJobPreferences.setAutoStart(this.launchOnStartupCheckBox.isSelected());
        this.taskSchedulerJobPreferences
            .setCronExpression(this.cronExpressionTriggerTextField.getText().trim());

        if (pollTriggerRadioButton.isSelected()) {
            this.taskSchedulerJobPreferences.setTriggerType(ITriggerTypes.SIMPLE_TRIGGER);
        } else if (cronTriggerRadioButton.isSelected()) {
            this.taskSchedulerJobPreferences.setTriggerType(ITriggerTypes.CRON_TRIGGER);
        }

        this.dialogResult = JOptionPane.OK_OPTION;
        dispose();

    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        folderMonitorDetailsPanel = new JPanel();
        detailsSubPanel = new JPanel();
        taskDescriptionLabel = new JLabel();
        descriptionTextField = new JTextField();
        typeOfMonitorLabel = new JLabel();
        folderMonitorTypePanel = new JPanel();
        typeOfMonitorComboBox = new JComboBox();
        folderLocationsPanel = new JPanel();
        unprocessedFolderLabel = new JLabel();
        unprocessedFolderTextField = new JTextField();
        unprocessedFolderBrowseButton = new JButton();
        processedFolderLabel = new JLabel();
        processedFolderTextField = new JTextField();
        processedFolderBrowseButton = new JButton();
        triggerSettingsPanel = new JPanel();
        triggerSettingsSubPanel = new JPanel();
        pollTriggerRadioButton = new JRadioButton();
        pollFrequencyLabel = new JLabel();
        pollFrequencySpinner = new JSpinner();
        cronTriggerRadioButton = new JRadioButton();
        cronExpressionLabel = new JLabel();
        cronExpressionTriggerTextField = new JTextField();
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
                ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0E-4};

                //======== folderMonitorDetailsPanel ========
                {
                    folderMonitorDetailsPanel.setBorder(new CompoundBorder(
                        new TitledBorder("Folder Monitor Details"),
                        new EmptyBorder(5, 5, 5, 5)));
                    folderMonitorDetailsPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)folderMonitorDetailsPanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)folderMonitorDetailsPanel.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)folderMonitorDetailsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)folderMonitorDetailsPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                    folderMonitorDetailsPanel.setBorder(new CompoundBorder(
                                        new TitledBorder(Localizer.localize("Server", "GeneralSettingsBorderTitle")),
                                        new EmptyBorder(5, 5, 5, 5)));

                    //======== detailsSubPanel ========
                    {
                        detailsSubPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)detailsSubPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
                        ((GridBagLayout)detailsSubPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                        ((GridBagLayout)detailsSubPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)detailsSubPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};

                        //---- taskDescriptionLabel ----
                        taskDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        taskDescriptionLabel.setText(Localizer.localize("Server", "TaskDescriptionLabelText"));
                        detailsSubPanel.add(taskDescriptionLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 5, 5), 0, 0));

                        //---- descriptionTextField ----
                        descriptionTextField.setFont(UIManager.getFont("TextField.font"));
                        detailsSubPanel.add(descriptionTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- typeOfMonitorLabel ----
                        typeOfMonitorLabel.setFont(UIManager.getFont("Label.font"));
                        typeOfMonitorLabel.setText(Localizer.localize("Server", "TypeOfMonitorLabelText"));
                        detailsSubPanel.add(typeOfMonitorLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //======== folderMonitorTypePanel ========
                        {
                            folderMonitorTypePanel.setLayout(new BorderLayout());

                            //---- typeOfMonitorComboBox ----
                            typeOfMonitorComboBox.setFont(UIManager.getFont("ComboBox.font"));
                            folderMonitorTypePanel.add(typeOfMonitorComboBox, BorderLayout.WEST);
                        }
                        detailsSubPanel.add(folderMonitorTypePanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    folderMonitorDetailsPanel.add(detailsSubPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(folderMonitorDetailsPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== folderLocationsPanel ========
                {
                    folderLocationsPanel.setBorder(new CompoundBorder(
                        new TitledBorder("Folder Locations"),
                        new EmptyBorder(5, 5, 5, 5)));
                    folderLocationsPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)folderLocationsPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                    ((GridBagLayout)folderLocationsPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout)folderLocationsPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};
                    ((GridBagLayout)folderLocationsPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};
                    folderLocationsPanel.setBorder(new CompoundBorder(
                                        new TitledBorder(Localizer.localize("Server", "FolderLocationsBorderTitle")),
                                        new EmptyBorder(5, 5, 5, 5)));

                    //---- unprocessedFolderLabel ----
                    unprocessedFolderLabel.setFont(UIManager.getFont("Label.font"));
                    unprocessedFolderLabel.setText(Localizer.localize("Server", "UnprocessedFolderLabelText"));
                    folderLocationsPanel.add(unprocessedFolderLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- unprocessedFolderTextField ----
                    unprocessedFolderTextField.setFont(UIManager.getFont("TextField.font"));
                    folderLocationsPanel.add(unprocessedFolderTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- unprocessedFolderBrowseButton ----
                    unprocessedFolderBrowseButton.setFont(UIManager.getFont("Button.font"));
                    unprocessedFolderBrowseButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/folder_image.png")));
                    unprocessedFolderBrowseButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            unprocessedFolderBrowseButtonActionPerformed(e);
                        }
                    });
                    unprocessedFolderBrowseButton.setText(Localizer.localize("Server", "BrowseButtonText"));
                    folderLocationsPanel.add(unprocessedFolderBrowseButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- processedFolderLabel ----
                    processedFolderLabel.setFont(UIManager.getFont("Label.font"));
                    processedFolderLabel.setText(Localizer.localize("Server", "ProcessedFolderLabelText"));
                    folderLocationsPanel.add(processedFolderLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- processedFolderTextField ----
                    processedFolderTextField.setFont(UIManager.getFont("TextField.font"));
                    folderLocationsPanel.add(processedFolderTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- processedFolderBrowseButton ----
                    processedFolderBrowseButton.setFont(UIManager.getFont("Button.font"));
                    processedFolderBrowseButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/folder_image.png")));
                    processedFolderBrowseButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            processedFolderBrowseButtonActionPerformed(e);
                        }
                    });
                    processedFolderBrowseButton.setText(Localizer.localize("Server", "BrowseButtonText"));
                    folderLocationsPanel.add(processedFolderBrowseButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(folderLocationsPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== triggerSettingsPanel ========
                {
                    triggerSettingsPanel.setBorder(new CompoundBorder(
                        new TitledBorder("Trigger Settings"),
                        new EmptyBorder(5, 5, 5, 5)));
                    triggerSettingsPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)triggerSettingsPanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)triggerSettingsPanel.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)triggerSettingsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)triggerSettingsPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                    triggerSettingsPanel.setBorder(new CompoundBorder(
                                        new TitledBorder(Localizer.localize("Server", "TriggerSettingsBorderTitle")),
                                        new EmptyBorder(5, 5, 5, 5)));

                    //======== triggerSettingsSubPanel ========
                    {
                        triggerSettingsSubPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)triggerSettingsSubPanel.getLayout()).columnWidths = new int[] {0, 0, 15, 225, 0, 0};
                        ((GridBagLayout)triggerSettingsSubPanel.getLayout()).rowHeights = new int[] {35, 30, 0};
                        ((GridBagLayout)triggerSettingsSubPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
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
                        pollFrequencySpinner.setModel(new SpinnerNumberModel(30, 1, 1814400, 1));
                        pollFrequencySpinner.setFont(UIManager.getFont("Spinner.font"));
                        triggerSettingsSubPanel.add(pollFrequencySpinner, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 5, 5), 0, 0));

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
                        cronExpressionTriggerTextField.setFont(UIManager.getFont("TextField.font"));
                        cronExpressionTriggerTextField.setHorizontalAlignment(SwingConstants.RIGHT);
                        triggerSettingsSubPanel.add(cronExpressionTriggerTextField, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    triggerSettingsPanel.add(triggerSettingsSubPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(triggerSettingsPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
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
                helpLabel.setHelpGUID("folder-monitor-task");
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
        setSize(630, 410);
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
    private JPanel folderMonitorDetailsPanel;
    private JPanel detailsSubPanel;
    private JLabel taskDescriptionLabel;
    private JTextField descriptionTextField;
    private JLabel typeOfMonitorLabel;
    private JPanel folderMonitorTypePanel;
    private JComboBox typeOfMonitorComboBox;
    private JPanel folderLocationsPanel;
    private JLabel unprocessedFolderLabel;
    private JTextField unprocessedFolderTextField;
    private JButton unprocessedFolderBrowseButton;
    private JLabel processedFolderLabel;
    private JTextField processedFolderTextField;
    private JButton processedFolderBrowseButton;
    private JPanel triggerSettingsPanel;
    private JPanel triggerSettingsSubPanel;
    private JRadioButton pollTriggerRadioButton;
    private JLabel pollFrequencyLabel;
    private JSpinner pollFrequencySpinner;
    private JRadioButton cronTriggerRadioButton;
    private JLabel cronExpressionLabel;
    private JTextField cronExpressionTriggerTextField;
    private JPanel buttonBar;
    private JHelpLabel helpLabel;
    private JCheckBox launchOnStartupCheckBox;
    private JButton saveButton;
    private JButton cancelButton;
    private ButtonGroup triggerTypeButtonGroup;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


}
