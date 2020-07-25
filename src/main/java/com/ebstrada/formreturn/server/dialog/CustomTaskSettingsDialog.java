package com.ebstrada.formreturn.server.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import javax.swing.*;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import com.ebstrada.formreturn.manager.ui.component.*;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;

import com.ebstrada.formreturn.api.task.JobPlugin;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.filefilter.FilenameExtensionFilter;
import com.ebstrada.formreturn.manager.ui.filefilter.ExtensionFileFilter;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.server.quartz.ITriggerTypes;

@SuppressWarnings("serial") public class CustomTaskSettingsDialog extends JDialog {

    public static final int CANCEL = 0;

    public static final int SAVE = 1;

    private int dialogResult = CANCEL;

    private HashMap<String, ?> customPreferences;

    private static final Logger logger = Logger.getLogger(CustomTaskSettingsDialog.class);

    public CustomTaskSettingsDialog(Frame owner) {
        super(owner);
        initComponents();
    }

    public CustomTaskSettingsDialog(Dialog owner) {
        super(owner);
        initComponents();
    }

    public HashMap<String, ?> getCustomPreferences() {
        return customPreferences;
    }

    public void setCustomPreferences(HashMap<String, ?> customPreferences) {
        this.customPreferences = customPreferences;
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

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                saveButton.requestFocusInWindow();
            }
        });
    }

    public String getDescription() {
        return descriptionTextField.getText().trim();
    }

    public void setDescription(String description) {
        descriptionTextField.setText(description);
    }

    private void saveButtonActionPerformed(ActionEvent e) {
        if (checkCronExpression()) {
            this.dialogResult = SAVE;
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

    private void configurePluginButtonActionPerformed(ActionEvent e) {

        File jarFile = new File(this.jarPluginLocationTextField.getText().trim());

        if (!(jarFile.exists()) || !(jarFile.canRead())) {
            Misc.showErrorMsg((Frame) getRootPane().getTopLevelAncestor(),
                Localizer.localize("Server", "cantReadJarFileMessage"));
            return;
        }

        Misc.loadJar(jarFile);

        JobPlugin jobPlugin = Misc.getPluginManager().getPlugin(JobPlugin.class);
        if (jobPlugin != null) {
            jobPlugin.setPreferences(customPreferences);
            jobPlugin.configure(getRootPane().getTopLevelAncestor());
            if (jobPlugin.getDialogResult() == JobPlugin.SAVE) {
                this.customPreferences = jobPlugin.getPreferences();
            }
        } else {
            Misc.showErrorMsg(getRootPane().getTopLevelAncestor(),
                Localizer.localize("Server", "cantReadJarFileMessage"));
        }

    }

    private void jarPluginLocationBrowseButtonActionPerformed(ActionEvent e) {

        JFileChooser chooser = new JFileChooser();

        ExtensionFileFilter filter = new ExtensionFileFilter();
        filter.addExtension("jar");

        File lastDir = null;
        if (Globals.getLastDirectory() != null) {

            lastDir = new File(Globals.getLastDirectory());

            if (!(lastDir.exists())) {
                lastDir = null;
            }

        }

        if (lastDir == null) {
            lastDir = new File(System.getProperty("user.home"));
        }

        chooser.setCurrentDirectory(lastDir);
        chooser.setDialogTitle(Localizer.localize("Server", "selectJarFileDialogTitle"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(filter);
        chooser.rescanCurrentDirectory();

        File file = null;

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            if (file.isDirectory()) {
                return;
            }
        } else {
            return;
        }

        if (file != null) {
            open(file);
        }
    }

    private void open(File jarFile) {
        jarPluginLocationTextField.setText(jarFile.getAbsolutePath());
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

    public boolean isAutoStart() {
        return launchOnStartupCheckBox.isSelected();
    }

    public void setAutoStart(boolean autoStart) {
        launchOnStartupCheckBox.setSelected(autoStart);
    }

    public int getInterval() {
        return (Integer) this.pollFrequencySpinner.getValue() * 1000;
    }

    public void setInterval(int interval) {
        this.pollFrequencySpinner.setValue(interval / 1000);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        customTaskDetailsPanel = new JPanel();
        customTaskDetailsSubPanel = new JPanel();
        taskDescriptionLabel = new JLabel();
        descriptionTextField = new JTextField();
        pluginLocationLabel = new JLabel();
        browsePluginPanel = new JPanel();
        jarPluginLocationTextField = new JTextField();
        jarPluginLocationBrowseButton = new JButton();
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
        configurePluginButton = new JButton();
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
                ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};

                //======== customTaskDetailsPanel ========
                {
                    customTaskDetailsPanel.setBorder(new CompoundBorder(
                        new TitledBorder("Custom Task Details"),
                        new EmptyBorder(5, 5, 5, 5)));
                    customTaskDetailsPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)customTaskDetailsPanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)customTaskDetailsPanel.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)customTaskDetailsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)customTaskDetailsPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                    customTaskDetailsPanel.setBorder(new CompoundBorder(
                                        new TitledBorder(Localizer.localize("Server", "customTaskDetailsBorderTitle")),
                                        new EmptyBorder(5, 5, 5, 5)));

                    //======== customTaskDetailsSubPanel ========
                    {
                        customTaskDetailsSubPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)customTaskDetailsSubPanel.getLayout()).columnWidths = new int[] {0, 220, 0};
                        ((GridBagLayout)customTaskDetailsSubPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                        ((GridBagLayout)customTaskDetailsSubPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)customTaskDetailsSubPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};

                        //---- taskDescriptionLabel ----
                        taskDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                        taskDescriptionLabel.setText(Localizer.localize("Server", "TaskDescriptionLabelText"));
                        customTaskDetailsSubPanel.add(taskDescriptionLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.EAST, GridBagConstraints.NONE,
                            new Insets(0, 0, 5, 5), 0, 0));

                        //---- descriptionTextField ----
                        descriptionTextField.setFont(UIManager.getFont("TextField.font"));
                        customTaskDetailsSubPanel.add(descriptionTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //---- pluginLocationLabel ----
                        pluginLocationLabel.setFont(UIManager.getFont("Label.font"));
                        pluginLocationLabel.setText(Localizer.localize("Server", "pluginLocationLabelText"));
                        customTaskDetailsSubPanel.add(pluginLocationLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.EAST, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //======== browsePluginPanel ========
                        {
                            browsePluginPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)browsePluginPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
                            ((GridBagLayout)browsePluginPanel.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)browsePluginPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                            ((GridBagLayout)browsePluginPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                            //---- jarPluginLocationTextField ----
                            jarPluginLocationTextField.setFont(UIManager.getFont("TextField.font"));
                            browsePluginPanel.add(jarPluginLocationTextField, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- jarPluginLocationBrowseButton ----
                            jarPluginLocationBrowseButton.setFont(UIManager.getFont("Button.font"));
                            jarPluginLocationBrowseButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    jarPluginLocationBrowseButtonActionPerformed(e);
                                }
                            });
                            jarPluginLocationBrowseButton.setText(Localizer.localize("UI", "BrowseButtonText"));
                            browsePluginPanel.add(jarPluginLocationBrowseButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        customTaskDetailsSubPanel.add(browsePluginPanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    customTaskDetailsPanel.add(customTaskDetailsSubPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(customTaskDetailsPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
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
                        ((GridBagLayout)triggerSettingsSubPanel.getLayout()).columnWidths = new int[] {0, 0, 15, 220, 0};
                        ((GridBagLayout)triggerSettingsSubPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
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
                        pollFrequencySpinner.setModel(new SpinnerNumberModel(30, 1, 1814400, 1));
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
                contentPanel.add(triggerSettingsPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 35, 0, 0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 0.0, 0.0, 0.0};

                //---- helpLabel ----
                helpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                helpLabel.setHelpGUID("custom-task-settings");
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

                //---- configurePluginButton ----
                configurePluginButton.setFont(UIManager.getFont("Button.font"));
                configurePluginButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        configurePluginButtonActionPerformed(e);
                    }
                });
                configurePluginButton.setText(Localizer.localize("Server", "configurePluginButtonText"));
                buttonBar.add(configurePluginButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- saveButton ----
                saveButton.setFont(UIManager.getFont("Button.font"));
                saveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveButtonActionPerformed(e);
                    }
                });
                saveButton.setText(Localizer.localize("Server", "SaveButtonText"));
                buttonBar.add(saveButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
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
                cancelButton.setText(Localizer.localize("Server", "CancelButtonText"));
                buttonBar.add(cancelButton, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(745, 380);
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
    private JPanel customTaskDetailsPanel;
    private JPanel customTaskDetailsSubPanel;
    private JLabel taskDescriptionLabel;
    private JTextField descriptionTextField;
    private JLabel pluginLocationLabel;
    private JPanel browsePluginPanel;
    private JTextField jarPluginLocationTextField;
    private JButton jarPluginLocationBrowseButton;
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
    private JButton configurePluginButton;
    private JButton saveButton;
    private JButton cancelButton;
    private ButtonGroup triggerTypeButtonGroup;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
