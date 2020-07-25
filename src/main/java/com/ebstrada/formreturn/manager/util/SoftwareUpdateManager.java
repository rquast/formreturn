package com.ebstrada.formreturn.manager.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.dialog.SoftwareUpdateDialog;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class SoftwareUpdateManager {


    private String latestVersion = "";

    public SoftwareUpdateDialog checkForUpdates() {

        refreshLatestVersion();
        SoftwareUpdateDialog sud = null;

        if (latestVersion.length() > 0 && !(isLatestVersion(latestVersion))) {
            if (!(getIgnoredVersions().contains(latestVersion))) {
                sud = new SoftwareUpdateDialog(Main.getInstance());
            }
        }

        return sud;

    }

    private boolean isLatestVersion(String latestVersion) {

        int currentMajor = 0;
        int currentMinor = 0;
        int currentMaintenance = 0;

        int latestMajor = 0;
        int latestMinor = 0;
        int latestMaintenance = 0;

        // if a proxy shows another message or something goes wrong
        if (latestVersion.length() > 7 || latestVersion.length() < 3) {
            return true;
        }

        String currentVersion = Main.VERSION;

        // append .0 to 1.0 etc to make 3 fields
        if (currentVersion.split("\\.").length == 2) {
            currentVersion += ".0";
        }

        String[] currentVersionArray = currentVersion.split("\\.");
        currentMajor = Misc.parseIntegerString(currentVersionArray[0]);
        currentMinor = Misc.parseIntegerString(currentVersionArray[1]);

        if (currentVersionArray.length > 2) {
            currentMaintenance = Misc.parseIntegerString(currentVersionArray[2]);
        }

        String[] latestVersionArray = latestVersion.split("\\.");
        latestMajor = Misc.parseIntegerString(latestVersionArray[0]);
        latestMinor = Misc.parseIntegerString(latestVersionArray[1]);

        if (latestVersionArray.length > 2) {
            latestMaintenance = Misc.parseIntegerString(latestVersionArray[2]);
        }

        int current = currentMaintenance + (currentMinor * 100) + (currentMajor * 10000);
        int latest = latestMaintenance + (latestMinor * 100) + (latestMajor * 10000);

        if (current >= latest) {
            return true;
        } else {
            return false;
        }

    }

    private String doHttpUrlConnectionAction(String desiredUrl) throws Exception {
        URL url = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder;

        try {
            url = new URL(desiredUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setReadTimeout(15 * 1000);
            connection.connect();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            throw e;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                }
            }
        }
    }

    private void refreshLatestVersion() {
        try {
            String myUrl = "https://www.formreturn.com/releases/versions/latest";
            latestVersion = doHttpUrlConnectionAction(myUrl);
        } catch (Exception e) {
        }
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public boolean isUpdateEnabled() {
        return PreferencesManager.getSoftwareUpdatePreferences().isSoftwareUpdateEnabled();
    }

    public void ignoreVersion(String latestVersion) {
        Vector<String> ignoredVersions =
            PreferencesManager.getSoftwareUpdatePreferences().getIgnoredVersions();
        if (!(ignoredVersions.contains(latestVersion))) {
            ignoredVersions.add(latestVersion);
            try {
                PreferencesManager.getSoftwareUpdatePreferences()
                    .setIgnoredVersions(ignoredVersions);
                PreferencesManager.savePreferences(Main.getXstream());
            } catch (IOException e) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
            }
        }
    }

    public Vector<String> getIgnoredVersions() {
        return PreferencesManager.getSoftwareUpdatePreferences().getIgnoredVersions();
    }

    public void disableSoftwareUpdate() {
        PreferencesManager.getSoftwareUpdatePreferences().setSoftwareUpdateEnabled(false);
        try {
            PreferencesManager.savePreferences(Main.getXstream());
        } catch (IOException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }
    }

    public void enableSoftwareUpdate() {
        PreferencesManager.getSoftwareUpdatePreferences().setSoftwareUpdateEnabled(true);
        try {
            PreferencesManager.savePreferences(Main.getXstream());
        } catch (IOException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }
    }

}
