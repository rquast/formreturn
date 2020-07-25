package com.ebstrada.formreturn.manager.util.preferences.persistence;

import com.ebstrada.formreturn.manager.util.NoObfuscation;

public class FormProcessorPreferences implements NoObfuscation {

    private boolean runWhenServerStarts = true;

    public boolean isRunWhenServerStarts() {
        return runWhenServerStarts;
    }

    public void setRunWhenServerStarts(boolean runWhenServerStarts) {
        this.runWhenServerStarts = runWhenServerStarts;
    }

}
