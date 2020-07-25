package com.ebstrada.formreturn.server.quartz.job;

import java.awt.Frame;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;

import com.ebstrada.formreturn.server.ServerGUI;
import com.ebstrada.formreturn.server.dialog.VacuumingDialog;
import com.ebstrada.formreturn.server.preferences.persistence.TaskSchedulerJobPreferences;

public class VacuumJob extends TaskSchedulerJob implements StatefulJob {

    private static final Logger logger = Logger.getLogger(VacuumJob.class);

    // DO NOT REMOVE THE DEFAULT CONSTRUCTOR - IT IS REQUIRED FOR QUARTZ!
    public VacuumJob(TaskSchedulerJobPreferences jobPreferences) {
        super(jobPreferences);
    }

    public VacuumJob() {
        super();
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (ServerGUI.getInstance() != null) {
            executeGUI();
        } else {
            executeDaemon();
        }
    }

    public void executeGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final VacuumingDialog vd = new VacuumingDialog(
                    (Frame) ServerGUI.getInstance().getServerFrame().getRootPane()
                        .getTopLevelAncestor(), false);
                vd.setVisible(true);

                SwingWorker<Exception, Void> vacuumWorker = new SwingWorker<Exception, Void>() {
                    public Exception doInBackground() {
                        try {
                            ServerGUI.getInstance().vacuumTask();
                        } catch (SchedulerException e) {
                            return e;
                        } catch (InterruptedException e) {
                            return e;
                        }
                        return null;
                    }

                    public void done() {
                        Exception ex = null;
                        try {
                            ex = get();
                        } catch (InterruptedException e) {
                            logger.fatal(e.getLocalizedMessage(), e);
                        } catch (ExecutionException e) {
                            logger.fatal(e.getLocalizedMessage(), e);
                        }
                        if (vd != null) {
                            vd.dispose();
                        }
                        if (ex != null) {
                            logger.fatal(ex.getLocalizedMessage(), ex);
                            return; // abort shutdown!
                        }
                    }
                };
                vacuumWorker.execute();
            }
        });
    }

    public void executeDaemon() {
        try {
            com.ebstrada.formreturn.server.Main.getInstance().vacuumTask();
        } catch (SchedulerException e) {
            logger.fatal(e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            logger.fatal(e.getLocalizedMessage(), e);
        }
    }

}
