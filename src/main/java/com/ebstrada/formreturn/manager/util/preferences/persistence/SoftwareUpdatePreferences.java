package com.ebstrada.formreturn.manager.util.preferences.persistence;

import java.util.Vector;

import com.ebstrada.formreturn.manager.util.NoObfuscation;

public class SoftwareUpdatePreferences implements NoObfuscation {

    private boolean isSoftwareUpdateEnabled = true;

    private Vector<String> ignoredVersions = new Vector<String>();

    public boolean isSoftwareUpdateEnabled() {
        return isSoftwareUpdateEnabled;
    }

    public void setSoftwareUpdateEnabled(boolean isSoftwareUpdateEnabled) {
        this.isSoftwareUpdateEnabled = isSoftwareUpdateEnabled;
    }

    public Vector<String> getIgnoredVersions() {
        return ignoredVersions;
    }

    public void setIgnoredVersions(Vector<String> ignoredVersions) {
        this.ignoredVersions = ignoredVersions;
    }

}
