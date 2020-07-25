package com.ebstrada.formreturn.server.preferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ClientDatabasePreferences;
import com.ebstrada.formreturn.manager.util.preferences.persistence.FolderMonitorPreferences;
import com.ebstrada.formreturn.manager.util.preferences.persistence.FormProcessorPreferences;

import com.ebstrada.formreturn.server.preferences.persistence.DatabaseServerPreferences;
import com.ebstrada.formreturn.server.preferences.persistence.ServerPreferences;
import com.ebstrada.formreturn.server.preferences.persistence.TaskSchedulerPreferences;
import com.thoughtworks.xstream.XStream;

public class ServerPreferencesManager {
    private static File serverPreferencesFile;

    private static File systemPasswordFile;

    private static String homeDirectoryPath;

    private static String databaseDirectoryPath;

    private static ServerPreferences serverPreferences;

    private static final Logger logger = Logger.getLogger(ServerPreferencesManager.class);

    public ServerPreferencesManager() {
    }

    public static void savePreferences(XStream xstream) throws IOException {
        String rootNodeName = "formReturn";
        BufferedWriter out = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(serverPreferencesFile), "UTF-8"));
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        ObjectOutputStream oos;
        oos = xstream.createObjectOutputStream(out, rootNodeName);
        oos.writeObject(serverPreferences);
        oos.close();
    }

    public static String generateSystemPassword() {
        return Misc.randomPassword();
    }

    private static File getSystemPasswordFile() {
        return systemPasswordFile;
    }

    private static void setSystemPassword(String systemPassword) throws IOException {

        File systemPasswordFile = getSystemPasswordFile();

        if (systemPasswordFile == null) {
            throw new IllegalArgumentException(Localizer.localize("Server", "NullFileMessage"));
        }

        Writer output = new BufferedWriter(new FileWriter(systemPasswordFile));
        try {
            output.write(systemPassword);
        } finally {
            output.close();
        }

    }

    public static String getSystemPassword() {

        File systemPasswordFile = getSystemPasswordFile();
        StringBuilder contents = new StringBuilder();

        if (!(systemPasswordFile.exists())) {
            return null;
        }

        try {
            BufferedReader input = new BufferedReader(new FileReader(systemPasswordFile));
            try {
                contents.append(input.readLine());
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            logger.warn(ex.getLocalizedMessage(), ex);
        }

        return contents.toString();

    }

    public static TableModel getDatabaseTableModel(XStream xstream) {

        DefaultTableModel databaseTableModel = new DefaultTableModel();
        databaseTableModel
            .addColumn(Localizer.localize("Server", "DatabaseTableModelDatabaseNameColumnName"));
        databaseTableModel.addColumn(
            Localizer.localize("Server", "DatabaseTableModelFormProcessingDBColumnName"));

        String formProcessingDBName = getFormProcessingDatabaseName();

        boolean foundFormProcessingDBName = false;

        // read the database directory
        File databaseDirectory = new File(getDatabaseDirectoryPath());

        if (databaseDirectory.exists() && databaseDirectory.isDirectory()) {

            String[] files = databaseDirectory.list();

            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File databaseDataDir = new File(
                        getDatabaseDirectoryPath() + System.getProperty("file.separator")
                            + files[i]);
                    if (databaseDataDir.exists() && databaseDataDir.isDirectory()) {
                        if (files[i].trim().equals(formProcessingDBName.trim())) {
                            foundFormProcessingDBName = true;
                            databaseTableModel.addRow(new String[] {files[i],
                                Localizer.localize("Server",
                                    "DatabaseTableModelActiveDatabaseValue")});
                        } else {
                            databaseTableModel.addRow(new String[] {files[i], ""});
                        }
                    }
                }
            }
        }

        if (!foundFormProcessingDBName) {
            // reset the one in the db to the first db in this list!
            if (databaseTableModel.getRowCount() > 0) {
                setFormProcessingDatabaseName((String) databaseTableModel.getValueAt(0, 0));
                try {
                    savePreferences(xstream);
                } catch (IOException e) {
                    logger.warn(e.getLocalizedMessage(), e);
                }
            }
        }

        return databaseTableModel;

    }

    public static void setFormProcessingDatabaseName(String databaseName) {
        serverPreferences.setFormProcessingDatabaseName(databaseName);
    }

    public static String getFormProcessingDatabaseName() {
        return serverPreferences.getFormProcessingDatabaseName();
    }

    public static String[] getDatabaseList() {

        ArrayList<String> databaseList = new ArrayList<String>();

        // read the database directory
        File databaseDirectory = new File(getDatabaseDirectoryPath());

        if (databaseDirectory.exists() && databaseDirectory.isDirectory()) {

            String[] files = databaseDirectory.list();

            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File databaseDataDir = new File(
                        getDatabaseDirectoryPath() + System.getProperty("file.separator")
                            + files[i]);
                    if (databaseDataDir.exists() && databaseDataDir.isDirectory()) {
                        // add to an arraylist
                        databaseList.add(files[i]);
                    }

                }
            }
        }

        String[] databaseNames = new String[databaseList.size()];
        int i = 0;
        for (String databaseName : databaseList) {
            databaseNames[i] = databaseName;
            i++;
        }
        return databaseNames;

    }

    public static void loadPreferences(XStream xstream) {
        loadPreferences(false, xstream);
    }

    public static String getApplicationDir() {

        String applicationSupportDirString;

        Preferences prefs = Preferences.userRoot().node("com.ebstrada.formreturn");
        String key = "datadir";
        applicationSupportDirString = prefs.get(key, null);

        if (applicationSupportDirString != null) {
            File dataDir = new File(applicationSupportDirString);
            if (dataDir.isDirectory() && dataDir.canWrite() && dataDir.canRead()) {
                return applicationSupportDirString;
            }
        }

        File userHome = new File(System.getProperty("user.home"));

        try {
            applicationSupportDirString = userHome.getCanonicalPath();
        } catch (IOException e) {
            applicationSupportDirString = System.getProperty("user.home");
        }

        File alreadyExistingDir = new File(
            applicationSupportDirString + System.getProperty("file.separator") + ".formreturn");
        if (alreadyExistingDir.exists() && alreadyExistingDir.isDirectory()) {
            return applicationSupportDirString + System.getProperty("file.separator")
                + ".formreturn";
        }

        if (Main.MAC_OS_X) {

            applicationSupportDirString += System.getProperty("file.separator") + "Library" + System
                .getProperty("file.separator") + "Application Support";
            File applicationSupportDir = new File(applicationSupportDirString);
            if (applicationSupportDir.exists() && applicationSupportDir.isDirectory()) {
                return applicationSupportDirString + System.getProperty("file.separator")
                    + "FormReturn";
            }

        } else if (Main.WINDOWS) {

            applicationSupportDirString +=
                System.getProperty("file.separator") + "Local Settings" + System
                    .getProperty("file.separator") + "Application Data";
            File applicationSupportDir = new File(applicationSupportDirString);
            if (applicationSupportDir.exists() && applicationSupportDir.isDirectory()) {
                return applicationSupportDirString + System.getProperty("file.separator")
                    + "FormReturn";
            }

        }

        return applicationSupportDirString + System.getProperty("file.separator") + ".formreturn";
    }

    public static void loadPreferences(boolean isNewPreferencesFile, XStream xstream) {

        serverPreferences = new ServerPreferences();

        homeDirectoryPath = getApplicationDir();
        File homeDirectory = new File(homeDirectoryPath);
        if (!(homeDirectory.exists()) || homeDirectory.isFile()) {
            if (homeDirectory.isFile()) {
                homeDirectory.delete();
            }

            homeDirectory.mkdirs();
        }

        databaseDirectoryPath =
            getHomeDirectoryPath() + System.getProperty("file.separator") + "databases";
        File databaseDirectory = new File(databaseDirectoryPath);
        if (!(databaseDirectory.exists()) || databaseDirectory.isFile()) {
            if (databaseDirectory.isFile()) {
                databaseDirectory.delete();
            }

            databaseDirectory.mkdirs();
        }

        serverPreferencesFile =
            new File(getHomeDirectoryPath() + System.getProperty("file.separator") + "server.xml");
        if (!(serverPreferencesFile.exists()) || serverPreferencesFile.isDirectory()
            || isNewPreferencesFile) {

            if (serverPreferencesFile.isDirectory()) {
                serverPreferencesFile.delete();
            }

            try {
                savePreferences(xstream);
            } catch (IOException ex) {
                logger.warn(ex.getLocalizedMessage(), ex);
            }

        }

        try {
            FileInputStream fis = new FileInputStream(serverPreferencesFile);
            ObjectInputStream s;
            s = xstream.createObjectInputStream(new InputStreamReader(fis, "UTF-8"));
            serverPreferences = ((ServerPreferences) s.readObject());
            if (s != null) {
                s.close();
            }
            if (fis != null) {
                fis.close();
            }
        } catch (IOException ex) {
            logger.warn(ex.getLocalizedMessage(), ex);
        } catch (ClassNotFoundException e) {
            logger.warn(e.getLocalizedMessage(), e);
        }

        systemPasswordFile = new File(
            getHomeDirectoryPath() + System.getProperty("file.separator") + "system.password");
        if (!(systemPasswordFile.exists()) || systemPasswordFile.isDirectory()) {
            if (systemPasswordFile.isDirectory()) {
                systemPasswordFile.delete();
            }
            if (getSystemPassword() == null || getSystemPassword().trim().length() <= 0) {
                try {
                    setSystemPassword(generateSystemPassword());
                } catch (IOException e) {
                    logger.warn(e.getLocalizedMessage(), e);
                }
            }
        }

    }

    public static File getHomeDirectory() {
        return new File(homeDirectoryPath);
    }

    public static String getDatabaseDirectoryPath() {
        return databaseDirectoryPath;
    }

    public static void setDatabaseDirectoryPath(String databaseDirectoryPath) {
        ServerPreferencesManager.databaseDirectoryPath = databaseDirectoryPath;
    }

    public static String getHomeDirectoryPath() {
        return homeDirectoryPath;
    }

    public static DatabaseServerPreferences getDatabaseServer() {
        return serverPreferences.getDatabaseServer();
    }

    public static TaskSchedulerPreferences getTaskSchedulerPreferences() {
        return serverPreferences.getTaskSchedulerPreferences();
    }

    public static ClientDatabasePreferences getClientDatabase() {
        ClientDatabasePreferences cdp = new ClientDatabasePreferences();
        cdp.setDatabaseName(getFormProcessingDatabaseName());
        cdp.setPassword(getSystemPassword());
        cdp.setUsername("formreturn");
        cdp.setServerIPAddress(
            serverPreferences.getDatabaseServer().getListeningAddresses().split(",")[0].trim());
        cdp.setPortNumber(serverPreferences.getDatabaseServer().getPortNumber());
        return cdp;
    }

    public static FolderMonitorPreferences getFolderMonitorPreferences() {
        return serverPreferences.getFolderMonitorPreferences();
    }

    public static FormProcessorPreferences getFormProcessorPreferences() {
        return serverPreferences.getFormProcessorPreferences();
    }

    public static String getLocale() {
        return serverPreferences.getLocale();
    }

    public static void setLocale(String locale) {
        serverPreferences.setLocale(locale);
    }

}
