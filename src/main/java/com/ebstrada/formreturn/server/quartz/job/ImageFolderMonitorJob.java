package com.ebstrada.formreturn.server.quartz.job;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
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

import com.ebstrada.formreturn.manager.persistence.jpa.IncomingImage;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.image.ImageUtil;
import com.ebstrada.formreturn.server.Main;
import com.ebstrada.formreturn.server.ServerGUI;
import com.ebstrada.formreturn.server.preferences.persistence.TaskSchedulerJobPreferences;

public class ImageFolderMonitorJob extends FolderMonitorJob implements StatefulJob {

    private static final Logger logger = Logger.getLogger(ImageFolderMonitorJob.class);

    // DO NOT REMOVE THE DEFAULT CONSTRUCTOR - IT IS REQUIRED FOR QUARTZ!
    public ImageFolderMonitorJob() {
        super();
    }

    public ImageFolderMonitorJob(TaskSchedulerJobPreferences jobPreferences) {
        super(jobPreferences);
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        Vector<String> imageWhitelist = Misc.getImageWhilelist();

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
                                    if (imageWhitelist.contains(extension.trim()) && isFileComplete(
                                        unprocessedFile, recentFileTimes)) {

                                        long pageCount = 1;
                                        try {
                                            pageCount =
                                                ImageUtil.getNumberOfPagesInTiff(unprocessedFile);
                                        } catch (Exception e) {
                                            continue;
                                        }
                                        final long numberOfPagesInFile = pageCount;

                                        startTransaction(entityManager);
                                        byte[] imageData = Misc.getBytesFromFile(unprocessedFile);
                                        if (entityManager != null) {
                                            IncomingImage incomingImage = new IncomingImage();
                                            incomingImage.setCaptureTime(
                                                new Timestamp(System.currentTimeMillis()));
                                            incomingImage.setIncomingImageData(imageData);
                                            incomingImage.setNumberOfPages(numberOfPagesInFile);
                                            incomingImage
                                                .setIncomingImageName(unprocessedFile.getName());
                                            incomingImage.setMatchStatus((short) 0);

                                            long formPageId = Misc.parseIncomingImageFileName(
                                                unprocessedFile.getName());

                                            if (formPageId > 0) {
                                                incomingImage.setAssignToFormPageId(formPageId);
                                            }

                                            entityManager.persist(incomingImage);
                                            incomingImage = null;
                                        }

                                        // end transaction
                                        try {
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
                                                    stop(jobExecutionContext, new Exception(
                                                        "Cannot delete the file we need to overwrite."));
                                                }
                                            }

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
                                                stop(jobExecutionContext, new Exception(
                                                    "Cannot delete the source file."));
                                            }

                                            entityManager.getTransaction().begin();
                                            entityManager
                                                .createNativeQuery("CALL CHECK_INCOMING_IMAGES()")
                                                .executeUpdate();
                                            entityManager.getTransaction().commit();
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

}
