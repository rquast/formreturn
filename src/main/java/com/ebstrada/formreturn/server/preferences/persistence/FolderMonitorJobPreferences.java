package com.ebstrada.formreturn.server.preferences.persistence;

import com.ebstrada.formreturn.manager.util.NoObfuscation;

public abstract class FolderMonitorJobPreferences extends TaskSchedulerJobPreferences
    implements NoObfuscation {

    private String sourceDirectory;

    private String destinationDirectory;

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public String getDestinationDirectory() {
        return destinationDirectory;
    }

    public void setDestinationDirectory(String destinationDirectory) {
        this.destinationDirectory = destinationDirectory;
    }

}
