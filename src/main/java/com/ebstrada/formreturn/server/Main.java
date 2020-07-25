package com.ebstrada.formreturn.server;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Locale;

import javax.persistence.EntityManager;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;

import com.ebstrada.formreturn.manager.gef.font.FontLocaleUtil;
import com.ebstrada.formreturn.manager.gef.font.FontLocalesImpl;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.JPAConfiguration;
import com.ebstrada.formreturn.manager.persistence.xstream.Annotations;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.MultipleInstanceChecker;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ApplicationStatePreferences;
import com.ebstrada.formreturn.manager.util.preferences.persistence.FormProcessorPreferences;
import com.ebstrada.formreturn.server.derby.DatabaseServer;
import com.ebstrada.formreturn.server.exception.CannotCreatePreferencesException;
import com.ebstrada.formreturn.server.exception.CorruptPreferencesException;
import com.ebstrada.formreturn.server.preferences.ServerPreferencesManager;
import com.ebstrada.formreturn.server.quartz.TaskScheduler;
import com.ebstrada.formreturn.server.quartz.job.TaskSchedulerJob;
import com.ebstrada.formreturn.server.thread.FormProcessor;
import com.ebstrada.formreturn.server.thread.ScansWatcher;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class Main implements NoObfuscation {

    public static final String VERSION = com.ebstrada.formreturn.manager.ui.Main.VERSION;

    public static final String SERVICE_NAME = "FormReturn Server";

    public static boolean MAC_OS_X =
        (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));

    public static boolean LINUX = (System.getProperty("os.name").toLowerCase().startsWith("linux"));

    public static boolean WINDOWS =
        (System.getProperty("os.name").toLowerCase().startsWith("windows"));

    private static XStream xstream;

    private DatabaseServer databaseServer;

    private FormProcessor formProcessor;

    private TaskScheduler taskScheduler;

    private long formProcessorUptimeStart = 0;

    private MultipleInstanceChecker multipleInstanceChecker;

    private JPAConfiguration jpaconfiguration;

    private static Main instance;

    private static final Logger logger = Logger.getLogger(Main.class);


    private class NoStackTracePatternLayout extends PatternLayout {

        public NoStackTracePatternLayout(String str) {
            super(str);
        }

        @Override public boolean ignoresThrowable() {
            return false;
        }
    }

    public Main() {

        Main.instance = this;

        if (Main.WINDOWS) {
            System.setProperty("sun.java2d.dpiaware", "false");
        }

        Logger.getRootLogger().getLoggerRepository().resetConfiguration();

        // DERBY DEBUG PROPERTIES
        // System.setProperty("derby.locks.monitor", "true");
        // System.setProperty("derby.locks.deadlockTrace", "true");
        // System.setProperty("derby.stream.error.logSeverityLevel", "0");
        // System.setProperty("derby.language.logStatementText", "true");

        initLocalization();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    if (!instance.isStopped()) {
                        logger.info("Shutting down server gracefully.");
                        instance.stop(false);
                        logger.info("Server gracefully shut down.");
                    }
                } catch (SchedulerException e) {
                    logger.fatal(e.getLocalizedMessage(), e);
                } catch (InterruptedException e) {
                    logger.fatal(e.getLocalizedMessage(), e);
                }
            }
        });

    }

    public static Main getInstance() {
        return Main.instance;
    }

    public static void processIncomingImages() {
        if (Main.instance != null) {
            if (Main.instance.getFormProcessor() != null) {
                FormProcessor fp = Main.instance.getFormProcessor();
                fp.interrupt();
            }
        }
    }

    public void initLocalization() {
        Localizer.addResource("GefBase", "com.ebstrada.formreturn.language.BaseResourceBundle");
        Localizer.addResource("UI", "com.ebstrada.formreturn.language.UIResourceBundle");
        Localizer.addResource("UICDM", "com.ebstrada.formreturn.language.UICDMResourceBundle");
        Localizer.addResource("Util", "com.ebstrada.formreturn.language.UtilResourceBundle");
        Localizer.addResource("Server", "com.ebstrada.formreturn.language.ServerResourceBundle");
        Localizer.addLocale(Locale.getDefault());
        Localizer.switchCurrentLocale(Locale.getDefault());
    }

    public void initConsoleLog4J() {
        NoStackTracePatternLayout layout =
            new NoStackTracePatternLayout("[%d{dd MMM yyyy HH:mm:ss,SSS}]: %m%n");
        ConsoleAppender consoleAppender = new ConsoleAppender(layout);
        Logger.getRootLogger().addAppender(consoleAppender);
        Logger.getRootLogger().setLevel(Level.INFO);
    }

    public JPAConfiguration getJPAConfiguration() {
        if (this.jpaconfiguration == null) {
            try {
                this.jpaconfiguration = new JPAConfiguration(true);
            } catch (Exception e) {
            }
        }
        return jpaconfiguration;
    }

    public void rebuildPreferences() throws CannotCreatePreferencesException {
        try {
            ServerPreferencesManager.loadPreferences(true, getXstream());
        } catch (Exception ex) {
            throw new CannotCreatePreferencesException(ex);
        }
    }

    public static XStream createXStream() {
        return new XStream(new DomDriver("UTF-8")) {
            @Override protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new MapperWrapper(next) {
                    @Override
                    public boolean shouldSerializeMember(Class definedIn, String fieldName) {
                        if (definedIn == Object.class) {
                            return false;
                        }
                        return super.shouldSerializeMember(definedIn, fieldName);
                    }
                };
            }
        };
    }

    public void loadPreferences() throws CorruptPreferencesException {
        // load xstream
        xstream = createXStream();
        xstream.setMode(XStream.NO_REFERENCES);
        Annotations.load(xstream);

        try {
            ServerPreferencesManager.loadPreferences(getXstream());
        } catch (Exception ex) {
            throw new CorruptPreferencesException(ex);
        }

        initLog4JRollingFile();

        // set the locale based on server settings
        String locale = ServerPreferencesManager.getLocale();

        // load application preferences
        PreferencesManager.loadPreferences(getXstream());

        if (locale == null) {
            try {
                ApplicationStatePreferences appstate = PreferencesManager.getApplicationState();
                if (appstate != null) {
                    locale = appstate.getLocale();
                }
            } catch (Exception ex) {
            }
            if (locale == null) {
                locale = FontLocaleUtil.getFontLocale(Locale.getDefault()).name();
            }
            ServerPreferencesManager.setLocale(locale);
        }

        FontLocalesImpl fontLocales = FontLocaleUtil.getFontLocale(locale);
        Locale.setDefault(fontLocales.getLocale());

        if (Localizer.getCurrentLocale() != Locale.getDefault()) {
            Localizer.addLocale(Locale.getDefault());
            Localizer.switchCurrentLocale(Locale.getDefault());
            com.ebstrada.aggregation.i18n.Localizer.setCurrentLocale(Locale.getDefault());
        }

        // Load plugins
        Misc.loadPluginManager();

    }

    private void initLog4JRollingFile() {
        PatternLayout layout =
            new PatternLayout("%d{dd MMM yyyy HH:mm:ss,SSS} [%t] %-5p %c %x - %m%n");
        try {
            DailyRollingFileAppender rollingAppender = new DailyRollingFileAppender(layout,
                ServerPreferencesManager.getHomeDirectoryPath() + System
                    .getProperty("file.separator") + "server.log", "'.'yyyy-MM-dd-HH");
            Logger.getRootLogger().addAppender(rollingAppender);
            Logger.getRootLogger().setLevel(Level.INFO);
        } catch (IOException e) {
            logger.warn(e.getLocalizedMessage(), e);
        }
    }

    public synchronized void vacuumDatabase() throws SchedulerException {

        if (this.databaseServer == null || this.databaseServer.isRunning() == false) {
            return;
        }

        EntityManager entityManager = null;
        if (entityManager == null) {
            entityManager = getJPAConfiguration().getEntityManager();
        }

        if (entityManager != null) {
            entityManager.getTransaction().begin();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'CHECK_BOX', 1)")
                .executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'DATA_SET', 1)")
                .executeUpdate();
            entityManager
                .createNativeQuery("call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'FORM', 1)")
                .executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'FORM_PAGE', 1)")
                .executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'FRAGMENT_BARCODE', 1)")
                .executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'FRAGMENT_IMAGE_ZONE', 1)")
                .executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'FRAGMENT_OMR', 1)")
                .executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'FRAGMENT_OCR', 1)")
                .executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'GRADING', 1)").executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'GRADING_RULE', 1)")
                .executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'INCOMING_IMAGE', 1)")
                .executeUpdate();
            entityManager
                .createNativeQuery("call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'LOG', 1)")
                .executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'PROCESSED_IMAGE', 1)")
                .executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'PUBLICATION', 1)")
                .executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'PUBLICATION_JAR', 1)")
                .executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'PUBLICATION_XSL', 1)")
                .executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'QUERY_PROFILE', 1)")
                .executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'RECORD', 1)").executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'SEGMENT', 1)").executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'SOURCE_FIELD', 1)")
                .executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'SOURCE_IMAGE', 1)")
                .executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'SOURCE_TEXT', 1)")
                .executeUpdate();
            entityManager.createNativeQuery(
                "call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('FORMRETURN', 'SYSTEM_PROPERTIES', 1)")
                .executeUpdate();
            entityManager.getTransaction().commit();
        }

    }

    public void vacuumTask() throws SchedulerException, InterruptedException {
        ArrayList<TaskSchedulerJob> activeJobs = new ArrayList<TaskSchedulerJob>();
        if (isTaskSchedulerRunning()) {
            ArrayList<TaskSchedulerJob> jobList = taskScheduler.getJobList();
            try {
                for (TaskSchedulerJob job : jobList) {
                    taskScheduler.stopJob(job);
                    activeJobs.add(job);
                }
            } catch (SchedulerException e) {
                logger.warn(e.getLocalizedMessage(), e);
            }
        }
        stopFormProcessor();
        vacuumDatabase();
        startFormProcessor();
        if (isTaskSchedulerRunning()) {
            try {
                for (TaskSchedulerJob job : activeJobs) {
                    taskScheduler.startJob(job);
                }
            } catch (SchedulerException e) {
                logger.warn(e.getLocalizedMessage(), e);
            }
        }
    }

    public void stop(boolean vacuumDatabase) throws SchedulerException, InterruptedException {
        try {
            stopScheduler();
        } catch (InterruptedException e) {
            logger.warn(e.getLocalizedMessage(), e);
        }
        stopFormProcessor();
        if (vacuumDatabase) {
            vacuumDatabase();
        }
        stopDatabase();
    }

    public boolean isStopped() {
        try {
            if (taskScheduler != null && taskScheduler.isRunning()) {
                return false;
            }
        } catch (SchedulerException e) {
            return false;
        }

        if (formProcessor != null && formProcessor.isAlive()) {
            return false;
        }

        if (databaseServer != null && databaseServer.isRunning()) {
            return false;
        }

        return true;
    }

    public synchronized void startDatabase() {
        if (databaseServer == null) {
            databaseServer = new DatabaseServer();
        }
        if (databaseServer.isRunning() == false) {
            databaseServer.startNetworkServer();
        }
    }

    public synchronized void stopDatabase() {
        if (databaseServer != null && databaseServer.isRunning()) {
            databaseServer.shutdown();
        }
    }

    public synchronized void startScheduler() throws SchedulerException {
        if (taskScheduler == null) {
            taskScheduler = new TaskScheduler();
        }
        if (taskScheduler.isRunning() == false) {
            taskScheduler.restoreJobsFromPreferences();
            taskScheduler.start();
        }
    }

    public TaskScheduler getTaskScheduler() {
        return this.taskScheduler;
    }

    public synchronized boolean isTaskSchedulerRunning() throws SchedulerException {
        if (this.taskScheduler == null || this.taskScheduler.isRunning() == false) {
            return false;
        } else {
            return true;
        }
    }

    public synchronized void stopScheduler() throws SchedulerException, InterruptedException {
        if (taskScheduler != null) {
            ArrayList<TaskSchedulerJob> jobList = taskScheduler.getJobList();
            try {
                for (TaskSchedulerJob job : jobList) {
                    taskScheduler.stopJob(job);
                }
                taskScheduler.stop();
                taskScheduler = null;
            } catch (UnableToInterruptJobException ex) {
                throw ex;
            } catch (SchedulerException ex) {
                throw ex;
            } catch (Exception e) {
                logger.warn(e.getLocalizedMessage(), e);
            }
        }
    }

    public FormProcessor getFormProcessor() {
        return this.formProcessor;
    }

    public void stopFormProcessor() {
        stopFormProcessor(false);
    }

    public synchronized void stopFormProcessor(boolean invokedManually) {

        if (this.formProcessor == null) {
            return;
        }

        if (!invokedManually) {
            FormProcessorPreferences fpp = ServerPreferencesManager.getFormProcessorPreferences();
            if (!(fpp.isRunWhenServerStarts())) {
                return;
            }
        }

        if (getFormProcessor() != null && getFormProcessor().isAlive()) {
            getFormProcessor().setRunProcess(false);
            getFormProcessor().interrupt();

            try {
                getFormProcessor().join(5000);
            } catch (InterruptedException e1) {
                logger.warn(e1);
            }
        }
    }

    public void startFormProcessor() {
        startFormProcessor(false);
    }

    public synchronized void startScansWatcher() {
        Thread scanWatcherThread = new Thread(new Runnable() {
            public void run() {
                String parentPath = PreferencesManager.getScanDirectoryPath();
                Path toWatch = Paths.get(parentPath);
                WatchService myWatcher;
                try {
                    myWatcher = toWatch.getFileSystem().newWatchService();
                    ScansWatcher scansWatcher = new ScansWatcher(myWatcher);
                    Thread th = new Thread(scansWatcher, "FileWatcher");
                    th.start();
                    toWatch.register(myWatcher, ENTRY_MODIFY);
                    th.join();
                } catch (IOException e) {
                    Misc.printStackTrace(e);
                } catch (InterruptedException e) {
                    Misc.printStackTrace(e);
                }
            }
        });
        scanWatcherThread.start();
    }

    public synchronized void startFormProcessor(boolean invokedManually) {

        if (!invokedManually) {
            FormProcessorPreferences fpp = ServerPreferencesManager.getFormProcessorPreferences();
            if (!(fpp.isRunWhenServerStarts())) {
                return;
            }
        }

        if (getFormProcessor() == null) {
            formProcessor = new FormProcessor();
            formProcessor.start();
        }

        if (!(getFormProcessor().isAlive())) {
            formProcessor = new FormProcessor();
            formProcessor.start();
        }
    }

    public String getDatabaseStatus() {

        int time =
            (int) (Math.ceil((System.currentTimeMillis() - formProcessorUptimeStart) / 1000.00));

        int seconds = time % 60;
        int minutes = (time / 60) % 60;
        int hours = (time / 3600) % 24;
        int days = time / 86400;

        String uptime = "";
        if (days > 0) {
            uptime += String.format("%02d", days) + ":";
        }
        if (hours > 0) {
            uptime += String.format("%02d", hours) + ":";
        }
        if (minutes > 0) {
            uptime += String.format("%02d", minutes) + ":";
        }
        uptime += String.format("%02d", seconds);

        String status = "";

        if (getDatabaseServer().ping()) {
            status = String.format(Localizer.localize("Server", "DatabaseActiveStatusMessage"),
                getDatabaseServer().getListeningAddresses() + ":" + getDatabaseServer()
                    .getDefaultPort(), uptime.trim());
        } else {
            formProcessorUptimeStart = System.currentTimeMillis();
            status = Localizer.localize("Server", "DatabaseDownStatusMessage");
        }

        return status;

    }

    public void resetUptime() {
        formProcessorUptimeStart = System.currentTimeMillis();
    }

    public String getFormProcessorStatus() {
        int time =
            (int) (Math.ceil((System.currentTimeMillis() - formProcessorUptimeStart) / 1000.00));

        int seconds = time % 60;
        int minutes = (time / 60) % 60;
        int hours = (time / 3600) % 24;
        int days = time / 86400;

        String uptime = "";
        if (days > 0) {
            uptime += String.format("%02d", days) + ":";
        }
        if (hours > 0) {
            uptime += String.format("%02d", hours) + ":";
        }
        if (minutes > 0) {
            uptime += String.format("%02d", minutes) + ":";
        }
        uptime += String.format("%02d", seconds);

        String status = "";

        if (formProcessor != null && formProcessor.isAlive()) {
            status = String.format(Localizer.localize("Server", "FormProcessorActiveStatusMessage"),
                uptime.trim());
        } else {
            formProcessorUptimeStart = System.currentTimeMillis();
            status = Localizer.localize("Server", "FormProcessorDownStatusMessage");
        }

        return status;
    }

    public void restartFormProcessor() {
        restartFormProcessor(false);
    }

    public synchronized void restartFormProcessor(boolean invokedManually) {
        if (!invokedManually) {
            FormProcessorPreferences fpp = ServerPreferencesManager.getFormProcessorPreferences();
            if (!(fpp.isRunWhenServerStarts())) {
                return;
            }
        }

        if (getFormProcessor() == null) {
            formProcessor = new FormProcessor();
        }

        if (getFormProcessor().isAlive()) {
            getFormProcessor().setRunProcess(false);
            getFormProcessor().interrupt();

            try {
                getFormProcessor().join(5000);
            } catch (InterruptedException e1) {
                logger.warn(e1);
            }
        }
        formProcessor = new FormProcessor();
        formProcessor.start();
    }

    public DatabaseServer getDatabaseServer() {
        return databaseServer;
    }

    public synchronized void restart() throws SchedulerException {
        getDatabaseServer().loadSettings();
        try {
            stopScheduler();
        } catch (InterruptedException e) {
            logger.warn(e.getLocalizedMessage(), e);
        }
        stopFormProcessor();
        getDatabaseServer().restart();
        startFormProcessor();
        startScheduler();
    }

    public XStream getXstream() {
        return xstream;
    }

    public boolean isInstanceRunning() {
        final int PORT = 44693;
        final byte[] SIGNATURE = new byte[] {0x34, 0x55, 0x7c, 0x03, 0x64, 0x22, 0x1e, 0x4a};
        multipleInstanceChecker = new MultipleInstanceChecker(SIGNATURE, PORT);
        int result = multipleInstanceChecker.check();
        switch (result) {
            case (MultipleInstanceChecker.STATUS_FIRST_INSTANCE): {
                return false;
            }
            case (MultipleInstanceChecker.STATUS_INSTANCE_EXISTS): {
                return true;
            }
            case (MultipleInstanceChecker.STATUS_SECURITY_EXCEPTION): {
                return false;
            }
        }
        return false;
    }

    public MultipleInstanceChecker getMultipleInstanceChecker() {
        return this.multipleInstanceChecker;
    }

}
