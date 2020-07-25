package com.ebstrada.formreturn.server;

import java.io.File;
import java.util.Scanner;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

import com.ebstrada.formreturn.server.exception.CorruptPreferencesException;

public class ServerDaemon implements Daemon {

    private static Main server = null;

    private static ServerDaemon instance = new ServerDaemon();

    private static final Logger logger = Logger.getLogger(ServerDaemon.class);

    private boolean stopped = false;

    public static void startCommandLineDaemon(String[] args) {

        File dataDirectory = null;

        if (args.length > 1) {
            dataDirectory = new File(args[1].trim());
            if (dataDirectory.exists() == false || dataDirectory.isDirectory() == false
                || dataDirectory.canWrite() == false || dataDirectory.canRead() == false) {
                dataDirectory = null;
            }
        }

        instance.initialize(dataDirectory);

        Scanner sc = new Scanner(System.in);

        boolean stop = false;

        do {
            try {
                String nextLine = sc.nextLine().trim().toLowerCase();
                if (nextLine.equals("stop")) {
                    break;
                }
            } catch (Exception ex) {
                logger.warn(ex.getLocalizedMessage(), ex);
            }
        } while (stop);

        if (!server.isStopped()) {
            instance.terminate();
        }

        System.exit(0);

    }

    /**
     * Static methods called by prunsrv to start/stop
     * the Windows service.  Pass the argument "start"
     * to start the service, and pass "stop" to
     * stop the service.
     * <p>
     * Taken lock, stock and barrel from Christopher Pierce's blog at http://blog.platinumsolutions.com/node/234
     *
     * @param args Arguments from prunsrv command line
     **/
    public static void windowsService(String args[]) {
        String cmd = "start";
        File dataDirectory = null;
        if (args.length > 0) {
            cmd = args[0];
        }

        if (args.length > 1) {
            dataDirectory = new File(args[1].trim());
            if (dataDirectory.exists() == false || dataDirectory.isDirectory() == false
                || dataDirectory.canWrite() == false || dataDirectory.canRead() == false) {
                dataDirectory = null;
            }
        }

        if ("start".equals(cmd)) {
            instance.windowsStart(dataDirectory);
        } else {
            instance.windowsStop();
        }
    }

    public void windowsStart(File dataDirectory) {
        initialize(dataDirectory);
        stopped = false;
        while (!stopped) {
            synchronized (this) {
                try {
                    this.wait(1000);
                } catch (InterruptedException ie) {
                }
            }
        }
        System.exit(0);
    }

    public void windowsStop() {
        terminate();
        stopped = true;
        synchronized (this) {
            this.notify();
        }
    }

    // Implementing the Daemon interface is not required for Windows but is for Linux
    @Override public void init(DaemonContext arg0) throws Exception {
        logger.debug("Daemon init");
    }

    @Override public void start() {
        logger.debug("Daemon start");
        initialize(null);
    }

    @Override public void stop() {
        logger.debug("Daemon stop");
        terminate();
    }

    @Override public void destroy() {
        logger.debug("Daemon destroy");
    }

    /**
     * Do the work of starting the engine
     *
     * @param dataDirectory
     */
    private void initialize(File dataDirectory) {
        if (server == null) {
            server = new Main();
            server.initConsoleLog4J();
            logger.debug("Loading Preferences");
            loadPreferences(server);
            logger.debug("Running Instance Checker");
            startInstanceChecker();
            logger.debug("Starting Derby");
            startDatabase(server);
            resetUptime();
            logger.debug("Starting Scheduler");
            startScheduler(server);
            logger.debug("Starting Form Processor");
            startFormProcessor();
            logger.info("Server Successfully Started");
        }
    }

    private void loadPreferences(Main server) {
        try {
            server.loadPreferences();
        } catch (CorruptPreferencesException cpe) {
            logger.fatal("Server preferences are corrupt. Aborting launch process.", cpe);
            terminate();
        }
    }

    public void startInstanceChecker() {
        if (server.isInstanceRunning()) {
            System.exit(0);
        }
    }

    public void resetUptime() {
        server.resetUptime();
    }

    private void startFormProcessor() {
        server.startFormProcessor();
    }

    public void startDatabase(Main server) {
        server.startDatabase();
    }

    public void startScheduler(Main server) {
        try {
            server.startScheduler();
        } catch (SchedulerException e) {
            logger.warn(e.getLocalizedMessage(), e);
        }
    }

    /**
     * Cleanly stop the engine.
     */
    public void terminate() {
        if (server != null) {
            logger.debug("Stopping Server");
            try {
                server.stop(false);
            } catch (SchedulerException e) {
                logger.fatal(e.getLocalizedMessage(), e);
            } catch (InterruptedException e) {
                logger.fatal(e.getLocalizedMessage(), e);
            }
        }
    }
}
