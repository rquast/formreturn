package com.ebstrada.formreturn.manager.ui.wizard.firstrun;

import java.util.ArrayList;

import javax.swing.JPanel;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.wizard.IWizardController;
import com.ebstrada.formreturn.manager.ui.wizard.IWizardPanel;
import com.ebstrada.formreturn.manager.util.graph.SizeAttributes;
import com.ebstrada.formreturn.manager.util.graph.SizePresets;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ApplicationStatePreferences;
import com.ebstrada.formreturn.manager.util.preferences.persistence.SoftwareUpdatePreferences;

public class FirstRunController implements IWizardController {

    private IWizardPanel currentPanel;

    @Override public void back() throws Exception {
        currentPanel = currentPanel.back();
    }

    @Override public void next() throws Exception {
        currentPanel = currentPanel.next();
    }

    @Override public void cancel() throws Exception {
        currentPanel.cancel();
    }

    public void finish() throws Exception {

        currentPanel.finish();

        // set language
        ApplicationStatePreferences applicationState = PreferencesManager.getApplicationState();
        applicationState.setLocale((String) settings.get(WelcomePanel.LANGUAGE_KEY));

        // set page size
        String pageSize = (String) settings.get(SelectPageSizePanel.PAGE_SIZE_KEY);
        PreferencesManager.setDefaultSegmentSizeAttributes(
            SizePresets.getPresetSize(pageSize, SizeAttributes.SEGMENT, SizeAttributes.PORTRAIT));
        PreferencesManager.setDefaultFormSizeAttributes(
            SizePresets.getPresetSize(pageSize, SizeAttributes.FORM, SizeAttributes.PORTRAIT));

        // set cjk support
        Boolean useCJKFont = (Boolean) settings.get(SelectPageSizePanel.CJK_SUPPORT_KEY);
        PreferencesManager.setUseCJKFont(useCJKFont);

        // set updates
        SoftwareUpdatePreferences sap = PreferencesManager.getSoftwareUpdatePreferences();
        sap.setSoftwareUpdateEnabled(
            (Boolean) settings.get(CheckForUpdatesPanel.CHECK_FOR_UDPATES_KEY));

        // save preferences
        PreferencesManager.savePreferences(Main.getXstream());

    }

    @Override public JPanel getActivePanel() {

        if (currentPanel == null) {
            currentPanel = new WelcomePanel(settings);
        }

        return (JPanel) currentPanel;
    }

    @Override public ArrayList<Integer> getActiveButtons() {
        return currentPanel.getActiveButtons();
    }

    @Override public String getWizardTitle() {
        return Localizer.localize("UI", "SetupWizardDialogTitle");
    }

}
