package com.ebstrada.formreturn.server.component;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.UnableToInterruptJobException;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.cdm.dialog.ExportOptionsDialog;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.server.ServerGUI;
import com.ebstrada.formreturn.server.dialog.CustomTaskSettingsDialog;
import com.ebstrada.formreturn.server.dialog.ExportTaskSettingsDialog;
import com.ebstrada.formreturn.server.dialog.FolderMonitorTaskSettingsDialog;
import com.ebstrada.formreturn.server.dialog.NewTaskSelectionDialog;
import com.ebstrada.formreturn.server.dialog.VacuumTaskSettingsDialog;
import com.ebstrada.formreturn.server.preferences.ServerPreferencesManager;
import com.ebstrada.formreturn.server.preferences.persistence.CustomJobPreferences;
import com.ebstrada.formreturn.server.preferences.persistence.ExportJobPreferences;
import com.ebstrada.formreturn.server.preferences.persistence.TaskSchedulerJobPreferences;
import com.ebstrada.formreturn.server.preferences.persistence.VacuumJobPreferences;
import com.ebstrada.formreturn.server.quartz.TaskScheduler;
import com.ebstrada.formreturn.server.quartz.job.CustomJob;
import com.ebstrada.formreturn.server.quartz.job.ExportJob;
import com.ebstrada.formreturn.server.quartz.job.ImageFolderMonitorJob;
import com.ebstrada.formreturn.server.quartz.job.SourceDataFolderMonitorJob;
import com.ebstrada.formreturn.server.quartz.job.TaskSchedulerJob;
import com.ebstrada.formreturn.server.quartz.job.VacuumJob;

@SuppressWarnings("serial") public class TaskSchedulerPanel extends JPanel {

    private TaskScheduler taskScheduler;


    public class TaskListSchedulerListener implements SchedulerListener {

        @Override public void jobScheduled(Trigger trigger) {
            updateJobState();
        }

        @Override public void jobUnscheduled(String triggerName, String triggerGroup) {
            updateJobState();
        }

        @Override public void triggerFinalized(Trigger trigger) {
            updateJobState();
        }

        @Override public void triggersPaused(String triggerName, String triggerGroup) {
            updateJobState();
        }

        @Override public void triggersResumed(String triggerName, String triggerGroup) {
            updateJobState();
        }

        @Override public void jobAdded(JobDetail jobDetail) {
            updateJobState();
        }

        @Override public void jobDeleted(String jobName, String groupName) {
            updateJobState();
        }

        @Override public void jobsPaused(String jobName, String jobGroup) {
            updateJobState();
        }

        @Override public void jobsResumed(String jobName, String jobGroup) {
            updateJobState();
        }

        @Override public void schedulerError(String msg, SchedulerException cause) {
        }

        @Override public void schedulerInStandbyMode() {
        }

        @Override public void schedulerStarted() {
            updateJobState();
        }

        @Override public void schedulerShutdown() {
        }

        @Override public void schedulerShuttingdown() {
        }

    }

    public TaskSchedulerPanel() {
        initComponents();
    }

    public void restore(TaskScheduler taskScheduler) throws SchedulerException {
        this.taskScheduler = taskScheduler;
        addSchedulerListener(taskScheduler.getScheduler());
    }

    private void addSchedulerListener(Scheduler scheduler) throws SchedulerException {
        scheduler.addSchedulerListener(new TaskListSchedulerListener());
    }

    private void logError(Exception ex) {
        Misc.printStackTrace(ex);
        Misc.showExceptionMsg(getRootPane().getTopLevelAncestor(), ex);
    }


    public synchronized void updateJobState() {
        try {
            ArrayList<TaskSchedulerJob> jobList = taskScheduler.getJobList();
            for (TaskSchedulerJob job : jobList) {
                int state = taskScheduler.getScheduler()
                    .getTriggerState(job.getGUID() + "Trigger", job.getGUID() + "TriggerGroup");
                job.setState(state);
            }
            updateTaskList();
        } catch (SchedulerException e) {
            logError(e);
        }
    }

    public synchronized void updateTaskList() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                DefaultListModel<TaskSchedulerJob> dlm = new DefaultListModel<TaskSchedulerJob>();
                ArrayList<TaskSchedulerJob> jobList = taskScheduler.getJobList();
                for (TaskSchedulerJob job : jobList) {
                    dlm.addElement(job);
                }
                taskList.setModel(dlm);
            }
        });
    }

    private void createTaskButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                NewTaskSelectionDialog ntsd =
                    new NewTaskSelectionDialog((Frame) getRootPane().getTopLevelAncestor());
                ntsd.setTitle(Localizer.localize("Server", "createANewTaskDialogTitle"));
                ntsd.setModal(true);
                ntsd.setVisible(true);

                switch (ntsd.getSelection()) {

                    case NewTaskSelectionDialog.FOLDER_MONITOR:
                        FolderMonitorTaskSettingsDialog fmtsd = new FolderMonitorTaskSettingsDialog(
                            (Frame) getRootPane().getTopLevelAncestor());

                        String dialogTitle =
                            Localizer.localize("Server", "CreateNewFolderMonitorDialogTitle");
                        fmtsd.setTitle(dialogTitle);

                        fmtsd.setModal(true);
                        fmtsd.setVisible(true);
                        if (fmtsd.getDialogResult() == JOptionPane.OK_OPTION) {

                            TaskSchedulerJobPreferences jobPreferences =
                                fmtsd.getTaskSchedulerJobPreferences();
                            TaskSchedulerJob job = null;
                            try {
                                job = taskScheduler.createJob(jobPreferences);
                                taskScheduler.addJobToPreferences(job.getPreferences());
                                ServerPreferencesManager.savePreferences(ServerGUI.getXstream());
                            } catch (SchedulerException e) {
                                logError(e);
                            } catch (IOException e) {
                                logError(e);
                            }

                            String title = Localizer.localize("Server", "StartTaskQuestionTitle");
                            String message =
                                Localizer.localize("Server", "StartTaskQuestionMessage");
                            int n = JOptionPane
                                .showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
                            if (n == 0 && job != null) {
                                try {
                                    taskScheduler.loadJob(job);
                                    taskScheduler.startJob(job);
                                } catch (SchedulerException e) {
                                    logError(e);
                                }
                            }

                        }
                        break;
                    case NewTaskSelectionDialog.VACUUM:
                        VacuumTaskSettingsDialog vtsd = new VacuumTaskSettingsDialog(
                            (Frame) getRootPane().getTopLevelAncestor());
                        vtsd.setTitle(
                            Localizer.localize("Server", "createVacuumTaskSettingsDialogTitle"));
                        vtsd.setModal(true);
                        vtsd.setVisible(true);
                        if (vtsd.getDialogResult() == VacuumTaskSettingsDialog.SAVE) {
                            VacuumJobPreferences vacuumJobPreferences = new VacuumJobPreferences();
                            vacuumJobPreferences.setCronExpression(vtsd.getCronExpression());
                            vacuumJobPreferences.setAutoStart(vtsd.isAutoStart());
                            vacuumJobPreferences.setDescription(
                                Localizer.localize("Server", "vacuumJobPreferencesDescription"));
                            vacuumJobPreferences.setTriggerType(vtsd.getTriggerType());
                            vacuumJobPreferences.setInterval(vtsd.getInterval());
                            TaskSchedulerJob job = null;
                            try {
                                job = taskScheduler.createJob(vacuumJobPreferences);
                                taskScheduler.addJobToPreferences(job.getPreferences());
                                ServerPreferencesManager.savePreferences(ServerGUI.getXstream());
                                String title =
                                    Localizer.localize("Server", "StartTaskQuestionTitle");
                                String message =
                                    Localizer.localize("Server", "StartTaskQuestionMessage");
                                int n = JOptionPane.showConfirmDialog(null, message, title,
                                    JOptionPane.YES_NO_OPTION);
                                if (n == 0 && job != null) {
                                    try {
                                        taskScheduler.loadJob(job);
                                        taskScheduler.startJob(job);
                                    } catch (SchedulerException e) {
                                        logError(e);
                                    }
                                }
                            } catch (SchedulerException e) {
                                logError(e);
                            } catch (IOException e) {
                                logError(e);
                            }
                        }
                        break;

                    case NewTaskSelectionDialog.EXPORT:

                        ExportTaskSettingsDialog etsd = new ExportTaskSettingsDialog(
                            (Frame) getRootPane().getTopLevelAncestor());
                        etsd.setTitle(
                            Localizer.localize("Server", "createExportTaskSettingsDialogTitle"));
                        etsd.setModal(true);
                        etsd.setVisible(true);
                        if (etsd.getDialogResult() == ExportTaskSettingsDialog.SAVE) {

                            TaskSchedulerJobPreferences exportJobPreferences =
                                new ExportJobPreferences();
                            exportJobPreferences.setCronExpression(etsd.getCronExpression());
                            exportJobPreferences.setAutoStart(etsd.isAutoStart());
                            exportJobPreferences.setDescription(etsd.getDescription());
                            exportJobPreferences.setTriggerType(etsd.getTriggerType());
                            exportJobPreferences.setInterval(etsd.getInterval());
                            try {
                                setExportOptions(exportJobPreferences,
                                    etsd.getExportOptionsDialog(), etsd.getPublicationIds());
                            } catch (IOException e) {
                                logError(e);
                            }

                            TaskSchedulerJob job = null;
                            try {
                                job = taskScheduler.createJob(exportJobPreferences);
                                taskScheduler.addJobToPreferences(job.getPreferences());
                                ServerPreferencesManager.savePreferences(ServerGUI.getXstream());
                                String title =
                                    Localizer.localize("Server", "StartTaskQuestionTitle");
                                String message =
                                    Localizer.localize("Server", "StartTaskQuestionMessage");
                                int n = JOptionPane.showConfirmDialog(null, message, title,
                                    JOptionPane.YES_NO_OPTION);
                                if (n == 0 && job != null) {
                                    try {
                                        taskScheduler.loadJob(job);
                                        taskScheduler.startJob(job);
                                    } catch (SchedulerException e) {
                                        logError(e);
                                    }
                                }
                            } catch (SchedulerException e) {
                                logError(e);
                            } catch (IOException e) {
                                logError(e);
                            }
                        }

                        break;

                    case NewTaskSelectionDialog.CUSTOM:
                        CustomTaskSettingsDialog ctsd = new CustomTaskSettingsDialog(
                            (Frame) getRootPane().getTopLevelAncestor());
                        ctsd.setTitle(
                            Localizer.localize("Server", "createCustomTaskSettingsDialogTitle"));
                        ctsd.setModal(true);
                        ctsd.setVisible(true);
                        if (ctsd.getDialogResult() == VacuumTaskSettingsDialog.SAVE) {
                            CustomJobPreferences customJobPreferences = new CustomJobPreferences();
                            customJobPreferences.setCronExpression(ctsd.getCronExpression());
                            customJobPreferences.setAutoStart(ctsd.isAutoStart());
                            customJobPreferences.setDescription(ctsd.getDescription());
                            customJobPreferences.setTriggerType(ctsd.getTriggerType());
                            customJobPreferences.setInterval(ctsd.getInterval());
                            customJobPreferences.setCustomPreferences(ctsd.getCustomPreferences());
                            TaskSchedulerJob job = null;
                            try {
                                job = taskScheduler.createJob(customJobPreferences);
                                taskScheduler.addJobToPreferences(job.getPreferences());
                                ServerPreferencesManager.savePreferences(ServerGUI.getXstream());
                                String title =
                                    Localizer.localize("Server", "StartTaskQuestionTitle");
                                String message =
                                    Localizer.localize("Server", "StartTaskQuestionMessage");
                                int n = JOptionPane.showConfirmDialog(null, message, title,
                                    JOptionPane.YES_NO_OPTION);
                                if (n == 0 && job != null) {
                                    try {
                                        taskScheduler.loadJob(job);
                                        taskScheduler.startJob(job);
                                    } catch (SchedulerException e) {
                                        logError(e);
                                    }
                                }
                            } catch (SchedulerException e) {
                                logError(e);
                            } catch (IOException e) {
                                logError(e);
                            }
                        }
                        break;
                    case NewTaskSelectionDialog.NONE_SELECTED:
                    default:
                }

            }
        });
    }

    protected void setExportOptions(TaskSchedulerJobPreferences exportJobPreferences,
        ExportOptionsDialog exportOptionsDialog, ArrayList<Long> publicationIds)
        throws IOException {
        ((ExportJobPreferences) exportJobPreferences)
            .setExportOptions(exportOptionsDialog.buildExportOptions());
        ((ExportJobPreferences) exportJobPreferences).getExportOptions()
            .setPublicationIds(publicationIds);
    }

    private void removeTaskButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int selectedIndex = taskList.getSelectedIndex();
                if (selectedIndex < 0) {
                    return;
                }
                TaskSchedulerJob job = (TaskSchedulerJob) taskList.getSelectedValue();
                try {
                    taskScheduler.removeJob(job);
                    ServerPreferencesManager.savePreferences(ServerGUI.getXstream());
                } catch (SchedulerException ex) {
                    logError(ex);
                } catch (Exception ex) {
                    logError(ex);
                }
            }
        });
    }

    private void editTaskButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                int selectedIndex = taskList.getSelectedIndex();
                if (selectedIndex < 0) {
                    return;
                }

                TaskSchedulerJob job = (TaskSchedulerJob) taskList.getSelectedValue();

                if (job instanceof ImageFolderMonitorJob
                    || job instanceof SourceDataFolderMonitorJob) {

                    FolderMonitorTaskSettingsDialog fmtsd = new FolderMonitorTaskSettingsDialog(
                        (Frame) getRootPane().getTopLevelAncestor());
                    String title = Localizer.localize("Server", "EditFolderMonitorDialogTitle");
                    fmtsd.setTitle(title);

                    fmtsd.restorePreferences(job.getPreferences());
                    fmtsd.setModal(true);
                    fmtsd.setVisible(true);
                    if (fmtsd.getDialogResult() == JOptionPane.OK_OPTION) {
                        TaskSchedulerJobPreferences jobPreferences =
                            fmtsd.getTaskSchedulerJobPreferences();
                        try {
                            taskScheduler.rescheduleJob(job, jobPreferences);
                            try {
                                ServerPreferencesManager.savePreferences(ServerGUI.getXstream());
                            } catch (IOException e) {
                                logError(e);
                            }
                        } catch (SchedulerException ex) {
                            logError(ex);
                        } catch (ParseException e) {
                            logError(e);
                        }
                    }

                } else if (job instanceof VacuumJob) {

                    VacuumJobPreferences vjp = (VacuumJobPreferences) job.getPreferences();

                    VacuumTaskSettingsDialog vtsd =
                        new VacuumTaskSettingsDialog((Frame) getRootPane().getTopLevelAncestor());
                    String title =
                        Localizer.localize("Server", "editVacuumTaskSettingsDialogTitle");
                    vtsd.setTitle(title);
                    vtsd.setCronExpression(vjp.getCronExpression());
                    vtsd.setAutoStart(vjp.isAutoStart());
                    vtsd.setTriggerType(vjp.getTriggerType());
                    vtsd.setInterval(vjp.getInterval());

                    vtsd.setModal(true);
                    vtsd.setVisible(true);
                    if (vtsd.getDialogResult() == VacuumTaskSettingsDialog.SAVE) {

                        vjp.setAutoStart(vtsd.isAutoStart());
                        vjp.setCronExpression(vtsd.getCronExpression());
                        vjp.setInterval(vtsd.getInterval());
                        vjp.setTriggerType(vtsd.getTriggerType());

                        try {
                            taskScheduler.rescheduleJob(job, vjp);
                            try {
                                ServerPreferencesManager.savePreferences(ServerGUI.getXstream());
                            } catch (IOException e) {
                                logError(e);
                            }
                        } catch (SchedulerException ex) {
                            logError(ex);
                        } catch (ParseException e) {
                            logError(e);
                        }
                    }

                } else if (job instanceof ExportJob) {

                    ExportJobPreferences ejp = (ExportJobPreferences) job.getPreferences();

                    ExportTaskSettingsDialog etsd =
                        new ExportTaskSettingsDialog((Frame) getRootPane().getTopLevelAncestor());
                    String title =
                        Localizer.localize("Server", "editExportTaskSettingsDialogTitle");
                    etsd.setTitle(title);
                    etsd.setDescription(ejp.getDescription());
                    etsd.restorePreferences(ejp.getExportOptions());
                    etsd.setCronExpression(ejp.getCronExpression());
                    etsd.setAutoStart(ejp.isAutoStart());
                    etsd.setTriggerType(ejp.getTriggerType());
                    etsd.setInterval(ejp.getInterval());

                    etsd.setModal(true);
                    etsd.setVisible(true);
                    if (etsd.getDialogResult() == ExportTaskSettingsDialog.SAVE) {

                        ejp.setDescription(etsd.getDescription());
                        ejp.setAutoStart(etsd.isAutoStart());
                        ejp.setCronExpression(etsd.getCronExpression());
                        ejp.setInterval(etsd.getInterval());
                        ejp.setTriggerType(etsd.getTriggerType());
                        try {
                            setExportOptions(ejp, etsd.getExportOptionsDialog(),
                                etsd.getPublicationIds());
                        } catch (IOException ioex) {
                            logError(ioex);
                        }

                        try {
                            taskScheduler.rescheduleJob(job, ejp);
                            try {
                                ServerPreferencesManager.savePreferences(ServerGUI.getXstream());
                            } catch (IOException e) {
                                logError(e);
                            }
                        } catch (SchedulerException ex) {
                            logError(ex);
                        } catch (ParseException e) {
                            logError(e);
                        }
                    }

                } else if (job instanceof CustomJob) {

                    CustomJobPreferences cjp = (CustomJobPreferences) job.getPreferences();

                    CustomTaskSettingsDialog ctsd =
                        new CustomTaskSettingsDialog((Frame) getRootPane().getTopLevelAncestor());
                    String title =
                        Localizer.localize("Server", "editCustomTaskSettingsDialogTitle");
                    ctsd.setTitle(title);
                    ctsd.setCronExpression(cjp.getCronExpression());
                    ctsd.setAutoStart(cjp.isAutoStart());
                    ctsd.setTriggerType(cjp.getTriggerType());
                    ctsd.setInterval(cjp.getInterval());
                    ctsd.setDescription(cjp.getDescription());
                    ctsd.setCustomPreferences(cjp.getCustomPreferences());

                    ctsd.setModal(true);
                    ctsd.setVisible(true);
                    if (ctsd.getDialogResult() == VacuumTaskSettingsDialog.SAVE) {

                        cjp.setAutoStart(ctsd.isAutoStart());
                        cjp.setCronExpression(ctsd.getCronExpression());
                        cjp.setInterval(ctsd.getInterval());
                        cjp.setTriggerType(ctsd.getTriggerType());
                        cjp.setCustomPreferences(ctsd.getCustomPreferences());

                        try {
                            taskScheduler.rescheduleJob(job, cjp);
                            try {
                                ServerPreferencesManager.savePreferences(ServerGUI.getXstream());
                            } catch (IOException e) {
                                logError(e);
                            }
                        } catch (SchedulerException ex) {
                            logError(ex);
                        } catch (ParseException e) {
                            logError(e);
                        }
                    }

                }
            }
        });
    }

    private void startTaskButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ArrayList<TaskSchedulerJob> jobList = taskScheduler.getJobList();
                int selectedIndex = taskList.getSelectedIndex();
                if (selectedIndex < 0) {
                    return;
                }
                try {
                    taskScheduler.startJob(jobList.get(selectedIndex));
                } catch (SchedulerException ex) {
                    logError(ex);
                }
            }
        });
    }

    private void stopTaskButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int selectedIndex = taskList.getSelectedIndex();
                if (selectedIndex < 0) {
                    return;
                }
                TaskSchedulerJob job = (TaskSchedulerJob) taskList.getSelectedValue();
                try {
                    taskScheduler.stopJob(job);
                } catch (UnableToInterruptJobException ex) {
                    logError(ex);
                } catch (SchedulerException ex) {
                    logError(ex);
                }
            }
        });
    }

    private void stopAllTasksButtonActionPerformed(ActionEvent e) {
        ArrayList<TaskSchedulerJob> jobList = taskScheduler.getJobList();
        for (TaskSchedulerJob job : jobList) {
            try {
                taskScheduler.stopJob(job);
            } catch (UnableToInterruptJobException ex) {
                logError(ex);
            } catch (SchedulerException ex) {
                logError(ex);
            }
        }
    }

    private void startAllTasksButtonActionPerformed(ActionEvent e) {
        ArrayList<TaskSchedulerJob> jobList = taskScheduler.getJobList();
        for (TaskSchedulerJob job : jobList) {
            try {
                taskScheduler.startJob(job);
            } catch (UnableToInterruptJobException ex) {
                logError(ex);
            } catch (SchedulerException ex) {
                logError(ex);
            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        taskListPanel = new JPanel();
        taskListScrollPane = new JScrollPane();
        taskList = new JList();
        taskListButtonPanel = new JPanel();
        createFolderMonitorTaskButton = new JButton();
        removeTaskButton = new JButton();
        editTaskButton = new JButton();
        startTaskButton = new JButton();
        stopTaskButton = new JButton();
        schedulerPanel = new JPanel();
        stopAllTasksButton = new JButton();
        startAllTasksButton = new JButton();

        //======== this ========
        setOpaque(false);
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {0, 0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

        //======== taskListPanel ========
        {
            taskListPanel.setBorder(
                new CompoundBorder(new TitledBorder("Task List"), new EmptyBorder(5, 5, 5, 5)));
            taskListPanel.setOpaque(false);
            taskListPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) taskListPanel.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) taskListPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
            ((GridBagLayout) taskListPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) taskListPanel.getLayout()).rowWeights =
                new double[] {1.0, 0.0, 1.0E-4};
            taskListPanel.setBorder(new CompoundBorder(
                new TitledBorder(Localizer.localize("Server", "TaskListBorderTitle")),
                new EmptyBorder(5, 5, 5, 5)));

            //======== taskListScrollPane ========
            {

                //---- taskList ----
                taskList.setFont(UIManager.getFont("List.font"));
                taskListScrollPane.setViewportView(taskList);
            }
            taskListPanel.add(taskListScrollPane,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

            //======== taskListButtonPanel ========
            {
                taskListButtonPanel.setOpaque(false);
                taskListButtonPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) taskListButtonPanel.getLayout()).columnWidths =
                    new int[] {0, 0, 0, 0, 0, 0, 0, 0};
                ((GridBagLayout) taskListButtonPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout) taskListButtonPanel.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                ((GridBagLayout) taskListButtonPanel.getLayout()).rowWeights =
                    new double[] {0.0, 1.0E-4};

                //---- createFolderMonitorTaskButton ----
                createFolderMonitorTaskButton.setFont(UIManager.getFont("Button.font"));
                createFolderMonitorTaskButton.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/add.png")));
                createFolderMonitorTaskButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        createTaskButtonActionPerformed(e);
                    }
                });
                createFolderMonitorTaskButton
                    .setText(Localizer.localize("Server", "CreateFolderMonitorTaskButtonText"));
                taskListButtonPanel.add(createFolderMonitorTaskButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- removeTaskButton ----
                removeTaskButton.setFont(UIManager.getFont("Button.font"));
                removeTaskButton.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/delete.png")));
                removeTaskButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        removeTaskButtonActionPerformed(e);
                    }
                });
                removeTaskButton.setText(Localizer.localize("Server", "RemoveTaskButtonText"));
                taskListButtonPanel.add(removeTaskButton,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- editTaskButton ----
                editTaskButton.setFont(UIManager.getFont("Button.font"));
                editTaskButton.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/pencil.png")));
                editTaskButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        editTaskButtonActionPerformed(e);
                    }
                });
                editTaskButton.setText(Localizer.localize("Server", "EditTaskButtonText"));
                taskListButtonPanel.add(editTaskButton,
                    new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- startTaskButton ----
                startTaskButton.setFont(UIManager.getFont("Button.font"));
                startTaskButton.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/time_go.png")));
                startTaskButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        startTaskButtonActionPerformed(e);
                    }
                });
                startTaskButton.setText(Localizer.localize("Server", "StartTaskButtonText"));
                taskListButtonPanel.add(startTaskButton,
                    new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- stopTaskButton ----
                stopTaskButton.setFont(UIManager.getFont("Button.font"));
                stopTaskButton.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/stop.png")));
                stopTaskButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        stopTaskButtonActionPerformed(e);
                    }
                });
                stopTaskButton.setText(Localizer.localize("Server", "StopTaskButtonText"));
                taskListButtonPanel.add(stopTaskButton,
                    new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
            }
            taskListPanel.add(taskListButtonPanel,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(taskListPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== schedulerPanel ========
        {
            schedulerPanel.setBorder(
                new CompoundBorder(new TitledBorder("Scheduler"), new EmptyBorder(5, 5, 5, 5)));
            schedulerPanel.setOpaque(false);
            schedulerPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) schedulerPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) schedulerPanel.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout) schedulerPanel.getLayout()).columnWeights =
                new double[] {0.0, 0.0, 1.0E-4};
            ((GridBagLayout) schedulerPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};
            schedulerPanel.setBorder(new CompoundBorder(
                new TitledBorder(Localizer.localize("Server", "SchedulerBorderTitle")),
                new EmptyBorder(5, 5, 5, 5)));

            //---- stopAllTasksButton ----
            stopAllTasksButton.setFont(UIManager.getFont("Button.font"));
            stopAllTasksButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/stop.png")));
            stopAllTasksButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    stopAllTasksButtonActionPerformed(e);
                }
            });
            stopAllTasksButton.setText(Localizer.localize("Server", "StopAllTasksButtonText"));
            schedulerPanel.add(stopAllTasksButton,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

            //---- startAllTasksButton ----
            startAllTasksButton.setFont(UIManager.getFont("Button.font"));
            startAllTasksButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/time_go.png")));
            startAllTasksButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    startAllTasksButtonActionPerformed(e);
                }
            });
            startAllTasksButton.setText(Localizer.localize("Server", "StartAllTasksButtonText"));
            schedulerPanel.add(startAllTasksButton,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(schedulerPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel taskListPanel;
    private JScrollPane taskListScrollPane;
    private JList taskList;
    private JPanel taskListButtonPanel;
    private JButton createFolderMonitorTaskButton;
    private JButton removeTaskButton;
    private JButton editTaskButton;
    private JButton startTaskButton;
    private JButton stopTaskButton;
    private JPanel schedulerPanel;
    private JButton stopAllTasksButton;
    private JButton startAllTasksButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
