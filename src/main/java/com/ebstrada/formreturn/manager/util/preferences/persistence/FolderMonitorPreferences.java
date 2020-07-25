package com.ebstrada.formreturn.manager.util.preferences.persistence;

import com.ebstrada.formreturn.manager.util.NoObfuscation;

public class FolderMonitorPreferences implements NoObfuscation {

    private boolean runWhenServerStarts = false;

    private int interval = 30;

    private String unprocessedImagesDirectory = "";

    private String processedImagesDirectory = "";

    public boolean isRunWhenServerStarts() {
        return runWhenServerStarts;
    }

    public void setRunWhenServerStarts(boolean runWhenServerStarts) {
        this.runWhenServerStarts = runWhenServerStarts;
    }

    public String getUnprocessedImagesDirectory() {
        return unprocessedImagesDirectory;
    }

    public void setUnprocessedImagesDirectory(String unprocessedImagesDirectory) {
        this.unprocessedImagesDirectory = unprocessedImagesDirectory;
    }

    public String getProcessedImagesDirectory() {
        return processedImagesDirectory;
    }

    public void setProcessedImagesDirectory(String processedImagesDirectory) {
        this.processedImagesDirectory = processedImagesDirectory;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

}
