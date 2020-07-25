package com.ebstrada.formreturn.server.derby;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.derby.drda.NetworkServerControl;
import org.apache.log4j.Logger;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.RandomGUID;
import com.ebstrada.formreturn.server.dialog.BackupStatusDialog;
import com.ebstrada.formreturn.server.preferences.ServerPreferencesManager;
import com.ebstrada.formreturn.server.preferences.persistence.DatabaseServerPreferences;
import com.ebstrada.formreturn.server.ServerGUI;

public class DatabaseServer {

    private Vector<String> listeningAddresses = new Vector<String>();

    private int defaultPort = 1527;

    private NetworkServerControl server;

    private boolean serverActive = false;

    private String derbyHome = ServerPreferencesManager.getDatabaseDirectoryPath();

    private Map<String, DatabaseInstance> databaseInstances =
        new HashMap<String, DatabaseInstance>();

    private static final Logger logger = Logger.getLogger(DatabaseServer.class);

    public DatabaseServer() {

        loadSettings();

        System.setProperty("derby.system.home", derbyHome);
        String[] databaseList = ServerPreferencesManager.getDatabaseList();

        // create the default database
        if (databaseList.length <= 0) {
            createDefaultDatabase();
        } else {
            for (String databaseName : databaseList) {
                DatabaseInstance databaseInstance = new DatabaseInstance(databaseName);
                databaseInstances.put(databaseName, databaseInstance);
            }
        }
    }

    public void loadSettings() {

        DatabaseServerPreferences dsp = ServerPreferencesManager.getDatabaseServer();
        String listeningAddresses = dsp.getListeningAddresses();
        String[] addresses = listeningAddresses.split(",");

        this.listeningAddresses.removeAllElements();

        for (String address : addresses) {
            addListeningAddress(address.trim());
        }

        int portNumber = dsp.getPortNumber();
        if (portNumber < 1 || portNumber >= 65535) {
            portNumber = 1527;
        }

        setDefaultPort(portNumber);

    }

    public boolean ping() {
	
	/*
	boolean isSuccess = true;
	
	try {
	    server.ping();
	} catch ( Exception ex ) {
	    isSuccess = false;
	}
	 */
        return this.serverActive;

    }

    public void removeDatabase(String databaseName) {
        DatabaseInstance databaseInstance = databaseInstances.get(databaseName);
        try {
            databaseInstance.shutdown();
            File databaseDirectory =
                new File(derbyHome + System.getProperty("file.separator") + databaseName);
            if (databaseDirectory.exists() && databaseDirectory.isAbsolute() && databaseDirectory
                .isDirectory()) {
                Misc.deleteDirectory(databaseDirectory);
            }
            logger.info(Localizer.localize("Server", "DatabaseRemovedSuccessMessage"));
            if (ServerGUI.getInstance() != null
                && ServerGUI.getInstance().getServerFrame() != null) {
                Misc.showSuccessMsg(ServerGUI.getInstance().getServerFrame(), String
                    .format(Localizer.localize("Server", "DatabaseRemovedSuccessMessage"),
                        databaseName));
            }
        } catch (DatabaseInstanceException e) {
            logger.warn(e.getLocalizedMessage(), e);
            if (ServerGUI.getInstance() != null
                && ServerGUI.getInstance().getServerFrame() != null) {
                Misc.showExceptionMsg(ServerGUI.getInstance().getServerFrame(), e);
            }
        }
    }

    public DatabaseInstance getDatabaseInstance(String databaseName) {
        return databaseInstances.get(databaseName);
    }

    public void createDatabaseInstance(String databaseName) throws DatabaseServerException {
        DatabaseInstance databaseInstance = new DatabaseInstance(databaseName);
        try {
            databaseInstance.initDB();
            databaseInstances.put(databaseName, databaseInstance);
        } catch (DatabaseInstanceException e) {
            logger.warn(e.getLocalizedMessage(), e);
            if (ServerGUI.getInstance() != null
                && ServerGUI.getInstance().getServerFrame() != null) {
                Misc.showExceptionMsg(ServerGUI.getInstance().getServerFrame(), e);
            }
            throw new DatabaseServerException(String
                .format(Localizer.localize("Server", "CannotCreateDatabaseMessage"), databaseName));
        }
    }

    public void createDefaultDatabase() {
        String databaseName = "FRDB";
        try {
            createDatabaseInstance(databaseName);
        } catch (DatabaseServerException e) {
            logger.warn(e.getLocalizedMessage(), e);
            if (ServerGUI.getInstance() != null
                && ServerGUI.getInstance().getServerFrame() != null) {
                Misc.showExceptionMsg(ServerGUI.getInstance().getServerFrame(), e);
            }
        }
    }

    public void startNetworkServer() {

        if (listeningAddresses.size() <= 0) {
            startNetworkServer("127.0.0.1", defaultPort);
        } else if (listeningAddresses.size() > 2) {
            startNetworkServer("0.0.0.0", defaultPort);
        } else {
            startNetworkServer(listeningAddresses.get(0), defaultPort);
        }

    }

    public void startNetworkServer(String address, int port) {
        try {
            server = new NetworkServerControl(InetAddress.getByName(address), port);
            server.start(null);
            serverActive = true;
        } catch (Exception ex) {
            logger.warn(ex.getLocalizedMessage(), ex);
            if (ServerGUI.getInstance() != null
                && ServerGUI.getInstance().getServerFrame() != null) {
                Misc.showExceptionMsg(ServerGUI.getInstance().getServerFrame(), ex);
            }
        }
    }

    public void restart() {
        shutdown();
        ServerGUI.getInstance().resetUptime();
        startNetworkServer();
        logger.info(Localizer.localize("Server", "DatabaseRestartSuccessMessage"));
        if (ServerGUI.getInstance() != null && ServerGUI.getInstance().getServerFrame() != null) {
            Misc.showSuccessMsg(ServerGUI.getInstance().getServerFrame(),
                Localizer.localize("Server", "DatabaseRestartSuccessMessage"));
        }
    }

    public void shutdown() {

        Collection<DatabaseInstance> dic = databaseInstances.values();
        for (DatabaseInstance databaseInstance : dic) {
            try {
                databaseInstance.shutdown();
                try {
                    server.shutdown();
                } catch (Exception e) {
                    // don't show silly exception (can't connect to database etc)
                }
            } catch (DatabaseInstanceException e) {
                logger.warn(e.getLocalizedMessage(), e);
                if (ServerGUI.getInstance() != null
                    && ServerGUI.getInstance().getServerFrame() != null) {
                    Misc.showExceptionMsg(ServerGUI.getInstance().getServerFrame(), e);
                }
            }
        }
        serverActive = false;

    }

    public String getTempBaseDir() throws Exception {
        return ServerPreferencesManager.getHomeDirectory().getPath();
    }

    public void backupDatabase(BackupStatusDialog backupStatusDialog, String databaseName,
        File backupDirectory) throws Exception {

        String backupDirName = null;
        File backupDirFile = null;

        try {

            // 1. create a temporary backup directory.
            backupStatusDialog.setMessage(
                Localizer.localize("Server", "DatabaseBackupInitializingBackupMessage"));

            String GUID = (new RandomGUID()).toString();
            backupDirName =
                getTempBaseDir() + System.getProperty("file.separator") + "backup" + System
                    .getProperty("file.separator") + GUID;
            backupDirFile = new File(backupDirName);

            while (backupDirFile.exists()) {
                GUID = (new RandomGUID()).toString();
                backupDirName =
                    getTempBaseDir() + System.getProperty("file.separator") + "backup" + System
                        .getProperty("file.separator") + GUID;
                backupDirFile = new File(backupDirName);
            }

            backupDirFile.mkdirs();

            if (backupStatusDialog.isInterrupted()) {
                throw new InterruptedException();
            }

            // 2. run the backup on the temporary backup directory.
            DatabaseInstance databaseInstance = databaseInstances.get(databaseName);
            try {
                backupStatusDialog.setMessage(
                    Localizer.localize("Server", "DatabaseBackupExportingDatabaseMessage"));
                databaseInstance.backupDatabase(backupDirFile);
            } catch (DatabaseInstanceException e) {
                backupStatusDialog.setMessage(
                    Localizer.localize("Server", "DatabaseBackupFailureExportingDatabaseMessage"));
                if (ServerGUI.getInstance().getServerFrame() != null) {
                    Misc.showExceptionMsg(ServerGUI.getInstance().getServerFrame(), e);
                    logger.warn(e.getLocalizedMessage(), e);
                }
                return;
            }

            if (backupStatusDialog.isInterrupted()) {
                throw new InterruptedException();
            }

            // 3. copy the system.password file to the temporary backup directory.
            File systemPasswordFile = new File(
                getTempBaseDir() + System.getProperty("file.separator") + "system.password");
            FileUtils.copyFileToDirectory(systemPasswordFile, backupDirFile, true);

            if (backupStatusDialog.isInterrupted()) {
                throw new InterruptedException();
            }

            // 4. create a file name (databaseName_datebackwards.zip) - zip that temporary backup dir to that file name
            backupStatusDialog
                .setMessage(Localizer.localize("Server", "DatabaseBackupZippingDatabaseMessage"));
            File zipFile = new File(
                backupDirectory.getPath() + System.getProperty("file.separator") + databaseName
                    + "_" + System.currentTimeMillis() + ".zip");

            FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fileOutputStream);
            try {
                addZipDirectory(backupDirFile, zos);
            } catch (Exception ex) {
                throw ex;
            } finally {
                zos.close();
                fileOutputStream.flush();
                fileOutputStream.close();
            }

        } catch (Exception ex) {
            throw ex;
        } finally {
            // clean up the database directory.
            if (backupDirFile != null && backupDirFile.exists()) {
                Misc.deleteDirectory(backupDirFile);
            }
        }

        backupStatusDialog.setMessage(Localizer.localize("Server", "DatabaseBackupDoneMessage"));

        // 5. display a message that the backup with that filename was created.
        if (ServerGUI.getInstance().getServerFrame() != null) {
            Misc.showSuccessMsg(ServerGUI.getInstance().getServerFrame(),
                Localizer.localize("Server", "DatabaseBackupSuccessMessage"));
        }

    }

    private void addZipDirectory(File baseDirectory, ZipOutputStream zos) throws Exception {
        if (baseDirectory.isDirectory()) {
            scanFiles(baseDirectory, baseDirectory, zos);
        }
    }

    private void scanFiles(File baseDirectory, File sourceFile, ZipOutputStream zos)
        throws Exception {
        File[] files = sourceFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isFile()) {
                compressFile(baseDirectory, file, zos);
            } else if (file.isDirectory()) {
                scanFiles(baseDirectory, file, zos);
            }
        }
    }

    private void compressFile(File baseDirectory, File file, ZipOutputStream zos) throws Exception {
        FileInputStream in = new FileInputStream(file);
        try {
            byte[] buf = new byte[2048];
            String basePath = baseDirectory.getPath();
            String filePath = file.getPath();
            String newPath = filePath.substring(basePath.length() + 1, filePath.length());
            ZipEntry ze = new ZipEntry(newPath.replace("\\", "/"));
            long modTime = (new java.util.Date()).getTime();
            ze.setTime(modTime);
            ze.setMethod(ZipEntry.DEFLATED);
            ze.setSize(file.length());
            if (file.length() <= 0) {
                ze.setMethod(ZipEntry.STORED);
            }
            zos.putNextEntry(ze);
            int len;
            while ((len = in.read(buf)) > 0) {
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
        } catch (Exception ex) {
            throw ex;
        } finally {
            in.close();
        }
    }

    public int getDefaultPort() {
        return defaultPort;
    }

    public void setDefaultPort(int defaultPort) {
        this.defaultPort = defaultPort;
    }

    public void addListeningAddress(String address) {
        this.listeningAddresses.add(address);
    }

    public String getListeningAddresses() {
        String str = "";
        str = Misc.implode(listeningAddresses, ",");

        return str;
    }

    public boolean isRunning() {
        return serverActive;
    }

}
