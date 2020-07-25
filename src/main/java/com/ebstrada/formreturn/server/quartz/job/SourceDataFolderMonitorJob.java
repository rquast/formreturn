package com.ebstrada.formreturn.server.quartz.job;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.openjpa.lib.jdbc.ReportingSQLException;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

import com.ebstrada.formreturn.manager.logic.dataimport.SourceDataCSVImport;
import com.ebstrada.formreturn.manager.persistence.jpa.DataSet;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.server.Main;
import com.ebstrada.formreturn.server.ServerGUI;
import com.ebstrada.formreturn.server.preferences.persistence.SourceDataFolderMonitorJobPreferences;

public class SourceDataFolderMonitorJob extends FolderMonitorJob implements StatefulJob {

    private static final Logger logger = Logger.getLogger(SourceDataFolderMonitorJob.class);

    // DO NOT REMOVE THE DEFAULT CONSTRUCTOR - IT IS REQUIRED FOR QUARTZ!
    public SourceDataFolderMonitorJob() {
        super();
    }

    public SourceDataFolderMonitorJob(SourceDataFolderMonitorJobPreferences jobPreferences) {
        super(jobPreferences);
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        Vector<String> dataFileWhitelist = Misc.getDataFileWhilelist();

        EntityManager entityManager = null;

        try {

            entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

            if (entityManager != null) {

                JobDataMap jdm = jobExecutionContext.getJobDetail().getJobDataMap();

                File unprocessedDirectory = new File(jdm.getString("sourceDirectory"));
                File processedDirectory = new File(jdm.getString("destinationDirectory"));
                HashMap<String, Long> recentFileTimes =
                    (HashMap<String, Long>) jdm.get("recentFileTimes");

                if (unprocessedDirectory.exists() && unprocessedDirectory.isDirectory()
                    && processedDirectory.exists() && processedDirectory.isDirectory()) {
                    String[] files = unprocessedDirectory.list();
                    if (files != null) {
                        for (int i = 0; i < files.length; i++) {
                            try {
                                File unprocessedFile = new File(
                                    unprocessedDirectory.getCanonicalPath() + System
                                        .getProperty("file.separator") + files[i]);

                                if (unprocessedFile != null && unprocessedFile.exists()
                                    && !(unprocessedFile.isDirectory())) {

                                    String extension = files[i]
                                        .substring(files[i].lastIndexOf('.') + 1,
                                            files[i].length());
                                    String name = files[i].substring(0, files[i].lastIndexOf('.'));

                                    if (dataFileWhitelist.contains(extension.trim())
                                        && isFileComplete(unprocessedFile, recentFileTimes)) {

                                        startTransaction(entityManager);

                                        try {

                                            if (extension.equalsIgnoreCase("csv")) {
                                                processCSV(unprocessedFile, name, entityManager);
                                            } else if (extension.equalsIgnoreCase("xml")) {
                                                processXML(unprocessedFile, name, entityManager);
                                            }

                                            entityManager.getTransaction().commit();

                                            File destinationFile = new File(
                                                processedDirectory.getCanonicalFile() + System
                                                    .getProperty("file.separator") + files[i]);
                                            if (destinationFile.exists()) {
                                                FileUtils.forceDelete(destinationFile);
                                                if (destinationFile.exists()) {
                                                    // CAN'T DELETE THE FILE - STOP THE FOLDER MONITOR!!!

                                                    final String destinationFileName =
                                                        destinationFile.getName();
                                                    SwingUtilities.invokeLater(new Runnable() {
                                                        public void run() {
                                                            String message =
                                                                "ERROR - Cannot delete destination file: "
                                                                    + destinationFileName;
                                                            message += "\nStopping task instance.";
                                                            if (ServerGUI.getInstance() == null ||
                                                                ServerGUI.getInstance()
                                                                    .getServerFrame() == null) {
                                                                logger.error(message);
                                                            } else {
                                                                Misc.showErrorMsg(
                                                                    com.ebstrada.formreturn.server.ServerGUI
                                                                        .getInstance()
                                                                        .getServerFrame()
                                                                        .getRootPane()
                                                                        .getTopLevelAncestor(),
                                                                    message);
                                                            }
                                                        }
                                                    });

                                                    if (entityManager.getTransaction().isActive()) {
                                                        try {
                                                            entityManager.getTransaction()
                                                                .rollback();
                                                        } catch (Exception ex) {
                                                            // do nothing.
                                                        }
                                                    }
                                                    stop(jobExecutionContext, new Exception(
                                                        "Cannot delete the file we need to overwrite."));
                                                }
                                            }

                                            // try rename the file, if that doesn't work, copy and delete the old one
                                            try {
                                                FileUtils
                                                    .copyFile(unprocessedFile, destinationFile);
                                                FileUtils.forceDelete(unprocessedFile);
                                            } catch (final Exception ex) {
                                                SwingUtilities.invokeLater(new Runnable() {
                                                    public void run() {
                                                        String message =
                                                            "ERROR - " + ex.getLocalizedMessage();
                                                        message += "\nStopping task instance.";
                                                        if (ServerGUI.getInstance() == null ||
                                                            ServerGUI.getInstance().getServerFrame()
                                                                == null) {
                                                            logger.error(ex.getLocalizedMessage(),
                                                                ex);
                                                        } else {
                                                            Misc.showErrorMsg(
                                                                com.ebstrada.formreturn.server.ServerGUI
                                                                    .getInstance().getServerFrame()
                                                                    .getRootPane()
                                                                    .getTopLevelAncestor(),
                                                                message);
                                                        }
                                                    }
                                                });
                                            }

                                            if (unprocessedFile.exists()) {

                                                // CAN'T DELETE THE FILE - STOP THE FOLDER MONITOR!!!
                                                final String unprocessedFileName =
                                                    unprocessedFile.getName();
                                                SwingUtilities.invokeLater(new Runnable() {
                                                    public void run() {
                                                        String message =
                                                            "ERROR - Cannot delete file: "
                                                                + unprocessedFileName;
                                                        message += "\nStopping task instance.";
                                                        if (ServerGUI.getInstance() == null ||
                                                            ServerGUI.getInstance().getServerFrame()
                                                                == null) {
                                                            logger.error(message);
                                                        } else {
                                                            Misc.showErrorMsg(
                                                                com.ebstrada.formreturn.server.ServerGUI
                                                                    .getInstance().getServerFrame()
                                                                    .getRootPane()
                                                                    .getTopLevelAncestor(),
                                                                message);
                                                        }
                                                    }
                                                });

                                                if (entityManager.getTransaction().isActive()) {
                                                    try {
                                                        entityManager.getTransaction().rollback();
                                                    } catch (Exception ex) {
                                                        // do nothing.
                                                    }
                                                }
                                                stop(jobExecutionContext, new Exception(
                                                    "Cannot delete the source file."));

                                            }

                                        } catch (Exception ex) {
                                            logger.warn(ex.getLocalizedMessage(), ex);
                                            if (entityManager.getTransaction().isActive()) {
                                                entityManager.getTransaction().rollback();
                                            }
                                        }

                                    }
                                }
                            } catch (IOException e1) {
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            if (ex instanceof PersistenceException) {
                logger.warn(ex.getLocalizedMessage(), ex);
            } else if (ex instanceof javax.persistence.RollbackException) {
                logger.warn(ex.getLocalizedMessage(), ex);
            } else if (ex instanceof ReportingSQLException) {
                logger.warn(ex.getLocalizedMessage(), ex);
            } else {
                stop(jobExecutionContext, ex);
            }
        } finally {
            if (entityManager != null) {
                entityManager.close();
                entityManager = null;
            }
        }

    }

    private void processXML(File unprocessedFile, String name, EntityManager entityManager) {
        // TODO Auto-generated method stub

    }

    private void processCSV(File unprocessedFile, String name, EntityManager entityManager)
        throws IOException {

        DataSet dataSet = new DataSet();
        dataSet.setDataSetName(name);
        entityManager.persist(dataSet);
        entityManager.flush();

        SourceDataCSVImport sdci = new SourceDataCSVImport();
        sdci.process(unprocessedFile, name, entityManager, dataSet);

    }

}
