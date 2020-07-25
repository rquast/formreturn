package com.ebstrada.formreturn.api.task;

import java.awt.Component;
import java.util.HashMap;

import net.xeoh.plugins.base.Plugin;

public abstract class JobPlugin implements Plugin {

    public static final int CANCEL = 0;

    public static final int SAVE = 1;

    public static int dialogResult = CANCEL;

    protected HashMap<String, ?> preferences;

    public abstract void configure(Component parent);

    public void setPreferences(HashMap<String, ?> preferences) {
        this.preferences = preferences;
    }

    public HashMap<String, ?> getPreferences() {
        return this.preferences;
    }

    public abstract void execute();

    public int getDialogResult() {
        return dialogResult;
    }

}
