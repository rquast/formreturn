package com.ebstrada.formreturn.manager.util.preferences.persistence;

import java.io.File;
import java.io.IOException;

import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.swingsane.preferences.ISwingSanePreferences;
import com.swingsane.preferences.model.ApplicationPreferences;

/**
 * @author Roland Quast (roland@formreturn.com)
 */
public class SwingSanePreferencesImpl implements ISwingSanePreferences {

    private FormReturnPreferences formReturnPreferences;

    public SwingSanePreferencesImpl(FormReturnPreferences formReturnPreferences) {
        this.formReturnPreferences = formReturnPreferences;
    }

    @Override public void load() throws IOException, ClassNotFoundException {
        getTempDirectory().deleteOnExit();
    }

    @Override public void save() throws IOException {
        PreferencesManager.savePreferences(Main.getXstream());
    }

    @Override public void cleanUp() {
        PreferencesManager.cleanUp();
    }

    @Override public ApplicationPreferences getApplicationPreferences() {
        return formReturnPreferences.getSwingSaneApplicationPreferences();
    }

    @Override public File getTempDirectory() {
        return PreferencesManager.getTempDirectory();
    }

}
