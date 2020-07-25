package com.ebstrada.formreturn.server;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

import com.ebstrada.formreturn.manager.gef.ui.laf.ApplicationLAF;
import com.ebstrada.formreturn.manager.gef.ui.laf.LinuxLAF;
import com.ebstrada.formreturn.manager.gef.ui.laf.MacLAF;
import com.ebstrada.formreturn.manager.gef.ui.laf.WindowsLAF;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.server.component.TaskSchedulerPanel;
import com.ebstrada.formreturn.server.derby.DatabaseServer;
import com.ebstrada.formreturn.server.derby.dialog.ManageDatabaseUsers;
import com.ebstrada.formreturn.server.dialog.VacuumingDialog;
import com.ebstrada.formreturn.server.exception.CannotCreatePreferencesException;
import com.ebstrada.formreturn.server.exception.CorruptPreferencesException;
import com.ebstrada.formreturn.server.thread.FormProcessor;
import com.thoughtworks.xstream.XStream;

public class ServerGUI {

    private ServerFrame serverFrame;

    private static ServerGUI instance;

    private static Main server;

    private static final Logger logger = Logger.getLogger(ServerGUI.class);

    public static void main(final String args[]) {

        if (args != null && args.length > 0 && args[0].trim().equalsIgnoreCase("cli")) {
            ServerDaemon.startCommandLineDaemon(args);
        } else {
            System.setProperty("org.apache.batik.warn_destination", "false");
            System.setProperty("swing.aatext", "true");

            ApplicationLAF applicationLAF = null;

            if (Main.MAC_OS_X) {
                applicationLAF = new MacLAF();
            } else if (Main.WINDOWS) {
                applicationLAF = new WindowsLAF();
            } else if (Main.LINUX) {
                applicationLAF = new LinuxLAF();
            } else {
                System.setProperty("awt.useSystemAAFontSettings", "on");
            }

            if (applicationLAF != null) {
                applicationLAF.setLAF();
            }

            // launch main class
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new ServerGUI();
                }
            });
        }

    }

    public static ServerGUI getInstance() {
        return instance;
    }

    public ServerGUI() {
        instance = this;
        server = new Main();
        loadPreferences(server);
        serverFrame = new ServerFrame();
        startInstanceChecker(serverFrame);
        startDatabase(server);
        resetUptime();
        startScheduler(server);
        startFormProcessor();
        startScansWatcher();
        displayServerFrame(serverFrame);
        logger.info("Server started successfully.");
    }

    public void startScansWatcher() {
        server.startScansWatcher();
    }

    public void resetUptime() {
        server.resetUptime();
    }

    private void startFormProcessor() {
        server.startFormProcessor();
    }

    public void displayServerFrame(final ServerFrame serverFrame) {
        serverFrame.startup();
        serverFrame.setVisible(true);

        // OSX 10.7.2's version of Java has a bug with setState ICONIFIED. Will freeze the EDT.
        if (!(Main.MAC_OS_X && System.getProperty("java.vendor").startsWith("Apple"))) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    serverFrame.setExtendedState(Frame.ICONIFIED);
                }
            });
        }

    }

    private void loadPreferences(Main server) {
        try {
            server.loadPreferences();
        } catch (CorruptPreferencesException cpe) {

            int n = JOptionPane.showConfirmDialog(null,
                Localizer.localize("Server", "PreferencesLoadErrorMessage"),
                Localizer.localize("Server", "PreferencesLoadErrorTitle"),
                JOptionPane.YES_NO_OPTION);

            if (n != 0) {
                System.exit(0);
            }

            try {
                server.rebuildPreferences();
            } catch (CannotCreatePreferencesException ccpe) {
                JOptionPane.showMessageDialog(null,
                    Localizer.localize("Server", "PreferencesCreateErrorMessage"),
                    Localizer.localize("Server", "PreferencesCreateErrorMessage"),
                    JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }

    public void startInstanceChecker(ServerFrame serverFrame) {
        if (server.isInstanceRunning()) {
            System.exit(0);
        }
        if (server.getMultipleInstanceChecker() != null) {
            server.getMultipleInstanceChecker().setWindow(serverFrame);
        }
    }

    public void startDatabase(Main server) {
        server.startDatabase();
    }

    public void startScheduler(Main server) {
        try {
            TaskSchedulerPanel tsp = serverFrame.getTaskSchedulerPanel();
            server.startScheduler();
            tsp.restore(server.getTaskScheduler());
        } catch (SchedulerException e) {
            logger.warn(e.getLocalizedMessage(), e);
        }
    }

    public void stopScheduler(Main server) {
        try {
            server.stopScheduler();
        } catch (SchedulerException e) {
            logger.warn(e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            logger.warn(e.getLocalizedMessage(), e);
        }
    }

    public static void startServer() {

        Thread thread = new Thread() {
            public void run() {

                String mainClass = "com.ebstrada.formreturn.server.ServerGUI";
                String javaHome = System.getProperty("java.home");
                String classPath = System.getProperty("java.class.path");

                String fileSeparator = System.getProperty("file.separator");

                String[] classPathArray = classPath.split(System.getProperty("path.separator"));
                String firstPath = "";
                String endsWith = "lib" + System.getProperty("file.separator") + "formreturn.jar";

                if (Main.MAC_OS_X && System.getProperty("java.vendor").toLowerCase()
                    .startsWith("apple")) {
                    endsWith = "FormReturn.app/Contents/Resources/Java/formreturn.jar";
                } else if (Main.MAC_OS_X) {
                    endsWith = "FormReturn.app/Contents/Java/formreturn.jar";
                }

                if (classPathArray.length > 1) {
                    for (String path : classPathArray) {
                        if (path.endsWith(endsWith)) {
                            firstPath = path;
                        }
                    }
                } else {
                    firstPath = classPath;
                }

                ProcessBuilder processBuilder;

                if (Main.WINDOWS) {
                    if (firstPath.endsWith(endsWith)) {
                        String path =
                            firstPath.substring(0, (firstPath.length() - endsWith.length()));
                        String command = path + "formreturn_server.exe";
                        processBuilder = new ProcessBuilder(command);
                    } else {
                        processBuilder = new ProcessBuilder("cmd", "/c", "rundll32", "shell32.dll",
                            "ShellExec_RunDLL",
                            javaHome + fileSeparator + "bin" + fileSeparator + "java.exe",
                            "-Xms128m", "-Xmx512m", mainClass);
                    }
                } else if (Main.MAC_OS_X) {
                    if (firstPath.endsWith(endsWith)) {
                        String path =
                            firstPath.substring(0, (firstPath.length() - endsWith.length()));
                        String command =
                            path + "FormReturn.app/Contents/Resources/FormReturn Server.app";
                        if (System.getProperty("java.vendor").toLowerCase().startsWith("apple")) {
                            command = path + "FormReturn Server.app";
                        }
                        processBuilder = new ProcessBuilder("open", command);
                    } else {
                        processBuilder = new ProcessBuilder(
                            javaHome + fileSeparator + "bin" + fileSeparator + "java", "-Xmx512m",
                            mainClass);
                    }
                } else {
                    if (firstPath.endsWith(endsWith)) {
                        String path =
                            firstPath.substring(0, (firstPath.length() - endsWith.length()));
                        String command = path + "formreturn_server.sh";
                        processBuilder = new ProcessBuilder(command);
                    } else {
                        processBuilder = new ProcessBuilder(
                            javaHome + fileSeparator + "bin" + fileSeparator + "java", "-Xmx512m",
                            mainClass);
                    }
                }

                Map<String, String> envVars = processBuilder.environment();
                envVars.put("CLASSPATH", classPath);
                envVars.put("JAVA_HOME", javaHome);
                envVars.put("JAVA_OPTS", "-Xmx512m");
                processBuilder.redirectErrorStream(true); // merge stdout and stderr
                Process process;
                try {
                    process = processBuilder.start();
                    process.waitFor();
                    InputStream is = process.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                } catch (InterruptedException e) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                }
            }
        };

        thread.start();

    }

    public boolean stopServer(JFrame serverFrame) {
        Object[] options =
            {Localizer.localize("Server", "Yes"), Localizer.localize("Server", "No")};
        String msg = Localizer.localize("Server", "ConfirmServerShutdownMessage");
        int result = JOptionPane.showOptionDialog(serverFrame, msg,
            Localizer.localize("Server", "ConfirmServerShutdownTitle"), JOptionPane.DEFAULT_OPTION,
            JOptionPane.WARNING_MESSAGE, null, options, options[0]);

        if (result == 0) {
            stopServerConfirm();
            return true;
        } else {
            return false;
        }
    }

    public void stopServerConfirm() {

        if (serverFrame != null) {
            serverFrame.getDatabaseStatusTimer().stop();
            serverFrame.getFormProcessorStatusTimer().stop();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    final VacuumingDialog vd = new VacuumingDialog(serverFrame);
                    vd.setVisible(true);

                    SwingWorker<Exception, Void> vacuumWorker = new SwingWorker<Exception, Void>() {
                        public Exception doInBackground() {
                            try {
                                server.stop(true);
                            } catch (SchedulerException e) {
                                return e;
                            } catch (InterruptedException e) {
                                return e;
                            }
                            return null;
                        }

                        public void done() {
                            Exception ex = null;
                            try {
                                ex = get();
                            } catch (InterruptedException e) {
                                logger.fatal(e.getLocalizedMessage(), e);
                            } catch (ExecutionException e) {
                                logger.fatal(e.getLocalizedMessage(), e);
                            }
                            if (vd != null) {
                                vd.dispose();
                            }
                            if (ex != null) {
                                logger.fatal(ex.getLocalizedMessage(), ex);
                                return; // abort shutdown!
                            }
                            System.exit(0);
                        }
                    };
                    vacuumWorker.execute();
                }
            });
        } else {
            try {
                server.stop(false);
            } catch (SchedulerException e) {
                Misc.printStackTrace(e);
            } catch (InterruptedException e) {
                Misc.printStackTrace(e);
            } finally {
                System.exit(0);
            }
        }

    }

    public void showManageUsersDialog(String databaseName, JFrame frame) {
        ManageDatabaseUsers mdu = new ManageDatabaseUsers(frame, databaseName);
        mdu.setTitle(String
            .format(Localizer.localize("Server", "ManageDatabaseUsersDialogTitle"), databaseName));
        mdu.setVisible(true);
    }

    public DatabaseServer getDatabaseServer() {
        return server.getDatabaseServer();
    }

    public ServerFrame getServerFrame() {
        return serverFrame;
    }

    public FormProcessor getFormProcessor() {
        return server.getFormProcessor();
    }

    public String getDatabaseStatus() {
        return server.getDatabaseStatus();
    }

    public String getFormProcessorStatus() {
        return server.getFormProcessorStatus();
    }

    public void restartServer() throws SchedulerException {
        server.restart();
    }

    public void restartFormProcessor() {
        server.restartFormProcessor();
    }

    public void startFormProcessor(boolean invokedManually) {
        server.startFormProcessor(invokedManually);
    }

    public void stopFormProcessor(boolean invokedManually) {
        server.stopFormProcessor(invokedManually);
    }

    public void restartFormProcessor(boolean invokedManually) {
        server.restartFormProcessor(invokedManually);
    }

    public static XStream getXstream() {
        return server.getXstream();
    }

    public void vacuumTask() throws SchedulerException, InterruptedException {
        server.vacuumTask();
    }

}
