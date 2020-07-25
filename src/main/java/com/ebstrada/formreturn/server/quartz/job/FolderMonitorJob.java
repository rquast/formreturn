package com.ebstrada.formreturn.server.quartz.job;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ebstrada.formreturn.server.ServerGUI;
import com.ebstrada.formreturn.server.preferences.persistence.FolderMonitorJobPreferences;
import com.ebstrada.formreturn.server.preferences.persistence.TaskSchedulerJobPreferences;

public abstract class FolderMonitorJob extends TaskSchedulerJob {

    private static final Logger logger = Logger.getLogger(FolderMonitorJob.class);

    public static final int IMAGE_FOLDER_MONITOR_JOB = 0;

    public static final int SOURCE_DATA_FOLDER_MONITOR_JOB = 1;

    public FolderMonitorJob(TaskSchedulerJobPreferences jobPreferences) {
        super(jobPreferences);
    }

    public FolderMonitorJob() {
        super();
    }

    public void createJob(Class<?> clazz) {
        super.createJob(clazz);
        job.getJobDataMap().put("sourceDirectory",
            ((FolderMonitorJobPreferences) preferences).getSourceDirectory());
        job.getJobDataMap().put("destinationDirectory",
            ((FolderMonitorJobPreferences) preferences).getDestinationDirectory());
        job.getJobDataMap().put("recentFileTimes", new HashMap<String, Long>());
    }

    protected boolean startTransaction(EntityManager entityManager) {
        if (entityManager == null) {
            return false;
        } else {
            boolean hasTransaction = false;
            while (hasTransaction == false) {
                try {
                    if (entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().rollback();
                    }
                    entityManager.getTransaction().begin();
                    entityManager.flush();
                    hasTransaction = true;
                } catch (Exception ex) {
                    logger.warn(ex.getLocalizedMessage(), ex);
                }
            }
        }
        return true;
    }

    protected void stop(JobExecutionContext jobExecutionContext, Exception ex)
        throws JobExecutionException {
        JobExecutionException jee = new JobExecutionException(ex);
        jee.setErrorCode(JobExecutionException.ERR_UNSPECIFIED);
        jee.setUnscheduleAllTriggers(true);
        throw jee;
    }


    // check if the file is still being updated, do not process if it is
    protected boolean isFileComplete(File imageFile, HashMap<String, Long> recentFileTimes) {

        FileChannel channel;
        try {
            channel = new RandomAccessFile(imageFile, "rw").getChannel();
        } catch (FileNotFoundException e) {
            logger.warn(e.getLocalizedMessage(), e);
            return false;
        }

        FileLock lock = null;
        boolean isComplete = false;

        try {
            // Get an exclusive lock on the whole file
            lock = channel.lock();
            isComplete = true;
        } catch (IOException e) {
            isComplete = false;
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                } catch (IOException e) {
                    logger.warn(e.getLocalizedMessage(), e);
                }
            }
            try {
                channel.close();
            } catch (IOException e) {
                logger.warn(e.getLocalizedMessage(), e);
            }
        }

        if (!isComplete) {
            return false;
        }

        if (imageFile.length() <= 0) {
            return false;
        }

        String imageFileName = null;
        try {
            imageFileName = imageFile.getCanonicalPath();
        } catch (IOException e) {
            return false;
        }

        if (recentFileTimes.containsKey(imageFileName)) {

            long oldLastModified = recentFileTimes.get(imageFileName);
            long currentLastModified = imageFile.lastModified();

            if (oldLastModified != currentLastModified) {
                recentFileTimes.put(imageFileName, currentLastModified);
                isComplete = false;
            } else {
                recentFileTimes.remove(imageFileName);
                isComplete = true;
            }

        } else {

            // add this file and its timestamp to the list
            recentFileTimes.put(imageFileName, imageFile.lastModified());
            isComplete = false;

        }

        return isComplete;

    }

}
