package com.ebstrada.formreturn.manager.ui;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.DisplayMode;
import java.awt.FileDialog;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.persistence.EntityManager;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ebstrada.formreturn.manager.ui.tab.MacDesktopTabbedPane;
import org.apache.derby.jdbc.ClientDataSource;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.tools.ant.util.FileUtils;

import com.ebstrada.formreturn.manager.gef.base.Editor;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.base.Layer;
import com.ebstrada.formreturn.manager.gef.font.CachedFontManager;
import com.ebstrada.formreturn.manager.gef.font.FontLocaleUtil;
import com.ebstrada.formreturn.manager.gef.font.FontLocalesImpl;
import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.ui.DocumentAttributes;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.ui.laf.ApplicationLAF;
import com.ebstrada.formreturn.manager.gef.ui.laf.LinuxLAF;
import com.ebstrada.formreturn.manager.gef.ui.laf.MacLAF;
import com.ebstrada.formreturn.manager.gef.ui.laf.WindowsLAF;
import com.ebstrada.formreturn.manager.gef.undo.UndoManager;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.gef.util.ResourceLoader;
import com.ebstrada.formreturn.manager.log4j.ComponentAppender;
import com.ebstrada.formreturn.manager.log4j.ListAppenderScrollPane;
import com.ebstrada.formreturn.manager.log4j.ScrollPaneAppender;
import com.ebstrada.formreturn.manager.persistence.JPAConfiguration;
import com.ebstrada.formreturn.manager.persistence.xstream.Annotations;
import com.ebstrada.formreturn.manager.persistence.xstream.Document;
import com.ebstrada.formreturn.manager.persistence.xstream.DocumentPackage;
import com.ebstrada.formreturn.manager.persistence.xstream.Page;
import com.ebstrada.formreturn.manager.ui.cdm.CapturedDataManagerFrame;
import com.ebstrada.formreturn.manager.ui.component.BlockingGlassPane;
import com.ebstrada.formreturn.manager.ui.component.JStatusBar;
import com.ebstrada.formreturn.manager.ui.component.MainMenu;
import com.ebstrada.formreturn.manager.ui.component.ZoomSettings;
import com.ebstrada.formreturn.manager.ui.dialog.AboutDialog;
import com.ebstrada.formreturn.manager.ui.dialog.ConfirmExitDialog;
import com.ebstrada.formreturn.manager.ui.dialog.FormSetupDialog;
import com.ebstrada.formreturn.manager.ui.dialog.LoadingDialog;
import com.ebstrada.formreturn.manager.ui.dialog.SegmentSetupDialog;
import com.ebstrada.formreturn.manager.ui.dialog.SoftwareUpdateDialog;
import com.ebstrada.formreturn.manager.ui.dialog.SplashDialog;
import com.ebstrada.formreturn.manager.ui.editor.RecognitionPreviewPanel;
import com.ebstrada.formreturn.manager.ui.editor.frame.FormFrame;
import com.ebstrada.formreturn.manager.ui.editor.frame.SegmentFrame;
import com.ebstrada.formreturn.manager.ui.editor.panel.RecognitionPanelController;
import com.ebstrada.formreturn.manager.ui.filefilter.FilenameExtensionFilter;
import com.ebstrada.formreturn.manager.ui.frame.EditorFrame;
import com.ebstrada.formreturn.manager.ui.frame.ErrorLogFrame;
import com.ebstrada.formreturn.manager.ui.messaging.MemoryMapReader;
import com.ebstrada.formreturn.manager.ui.panel.DesktopDockingPanel;
import com.ebstrada.formreturn.manager.ui.panel.PropertiesPanelController;
import com.ebstrada.formreturn.manager.ui.popup.DesktopMenu;
import com.ebstrada.formreturn.manager.ui.pqm.ProcessingQueueManagerFrame;
import com.ebstrada.formreturn.manager.ui.reprocessor.frame.ReprocessorFrame;
import com.ebstrada.formreturn.manager.ui.sdm.SourceDataManagerFrame;
import com.ebstrada.formreturn.manager.ui.tab.CloseListener;
import com.ebstrada.formreturn.manager.ui.tab.DesktopTabbedPane;
import com.ebstrada.formreturn.manager.ui.tab.DefaultDesktopTabbedPane;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.MultipleInstanceChecker;
import com.ebstrada.formreturn.manager.util.OSXAdapter;
import com.ebstrada.formreturn.manager.util.SoftwareUpdateManager;
import com.ebstrada.formreturn.manager.util.TemplateFormPageID;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ApplicationStatePreferences;
import com.ebstrada.formreturn.scanner.client.ScanClientLauncher;
import com.ebstrada.formreturn.server.thread.ScansWatcher;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * Main Application Singleton
 */
public class Main extends JFrame {

    private final class DesktopPaneMouseAdapter extends java.awt.event.MouseAdapter {
        public void mousePressed(java.awt.event.MouseEvent e) {
            desktopPane1MousePressed(e);
        }
    }


    private final class DesktopPaneKeyAdapter extends java.awt.event.KeyAdapter {
        public void keyTyped(java.awt.event.KeyEvent e) {
            desktopPaneKeyTyped(e);
        }
    }


    public static final int RELEASE = 0;

    public static final int RELEASE_CANDIDATE = 1;

    public static final int BETA = 2;

    public static final int ALPHA = 3;

    public static Logger applicationExceptionLog = Logger.getLogger("applicationExceptionLog");

    public static Logger fontExceptionLog = Logger.getLogger("fontExceptionLog");

    public static Logger gefExceptionLog = Logger.getLogger("gefExceptionLog");

    public static Logger jasperExceptionLog = Logger.getLogger("jasperExceptionLog");

    public static boolean MAC_OS_X =
        (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));

    public static boolean LINUX = (System.getProperty("os.name").toLowerCase().startsWith("linux"));

    public static String APPLICATION_NAME = "FormReturn";

    public static String VERSION = "1.7.5";

    public static int releaseType = RELEASE;

    public static int buildNumber = 1;

    public static final int buildDateYear = 2020;

    public static final int buildDateMonth = 7;

    public static final int buildDateDay = 25;

    public static String COPYRIGHT = "2006-2020";

    public static String COMPANY = "EB Strada Holdings Pty Ltd";

    public static boolean WINDOWS =
        (System.getProperty("os.name").toLowerCase().startsWith("windows"));

    private static Main instance = null;

    private static MultipleInstanceChecker multipleInstanceChecker;

    private static CachedFontManager cachedFontManager;

    private static final long serialVersionUID = 1L;

    public JMenu mainRecentFileMenu = new JMenu();

    public JMenu popupRecentFileMenu = new JMenu();

    private Properties applicationProperties;

    private DesktopDockingPanel desktopDockingPanel;

    private JPopupMenu desktopPopupMenu;

    private JPanel errorLogFrame = null;

    private JPanel processingQueueManagerFrame = null;

    private JPanel capturedDataManagerFrame = null;

    private JPanel sourceDataManagerFrame = null;

    private JMenuBar menuBar;

    private ListAppenderScrollPane systemConsoleScrollPane = null;

    private JTable systemConsoleTable;

    private MainMenu mainMenu;

    private JPAConfiguration jpaconfiguration;

    private Stack<File> recentFileStack;

    private static XStream xstream;

    private boolean isDatabaseConnected = false;

    private SoftwareUpdateManager softwareUpdateManager;

    private SwingWorker<EditorFrame, Void> openWorker;

    private Timer databaseInformationTimer;

    private BlockingGlassPane blockingGlassPane;

    private String debugString = "";

    private long debugStringTime = System.currentTimeMillis();

    private boolean debugMode = false;

    private ScanClientLauncher scl;

    private ScansWatcher scansWatcher;

    private ZoomSettings reprocessorZoomSettings = new ZoomSettings();

    public boolean isDebugMode() {
        return debugMode;
    }

    public DesktopTabbedPane getDesktopTabbedPane() {
        if (this.desktopDockingPanel == null) {
            return null;
        }
        return this.desktopDockingPanel.getDesktopTabbedPane();
    }

    public void closeReporcessorFrame(final ReprocessorFrame reprocessorFrame) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getDesktopTabbedPane().remove(reprocessorFrame);
                checkDesktopPaneBG();
                UndoManager.setInstance(new UndoManager());
                UndoManager.getInstance().fireAllEvents();
                System.gc();
                revalidateAllFrames();
            }
        });
    }

    public Main(Map map, String[] args, ApplicationStatePreferences applicationState) {

        if (Main.instance == null) {
            Main.instance = this;
        }

        if (isInstanceRunning()) {
            sendOpenFileMessage(args);
            Misc.closeLogFiles();
            System.exit(0);
        } else {
            File openFileIPC = getOpenFileIPC();
            if (openFileIPC.exists()) {
                try {
                    openFileIPC.delete();
                } catch (Exception ex) {
                    Misc.printStackTrace(ex);
                }
            }
            sendOpenFileMessage(args);
        }

        SplashDialog sp = null;
        if (map.get("splash") != null) {
            sp = (SplashDialog) map.get("splash");
        }

        if (sp != null) {
            sp.updateLoadingStatus(Localizer.localize("UI", "LoadingPreferencesStatusMessage"));
        }

        // load fonts
        cachedFontManager = new CachedFontManager(sp);

        mainMenu = new MainMenu();
        mainRecentFileMenu = mainMenu.getOpenRecentSubMenu();

        recentFileStack = PreferencesManager.getRecentFileStack();
        rebuildRecentFileMenu();

        if (applicationState.isLaunchServerOnStartup()) {
            if (sp != null) {
                sp.updateLoadingStatus(
                    Localizer.localize("UI", "LaunchingFormReturnServerStatusMessage"));
            }
            com.ebstrada.formreturn.server.ServerGUI.startServer();
        }

        if (applicationState.isConnectToDBOnStartup()) {
            if (sp != null) {
                sp.updateLoadingStatus(
                    Localizer.localize("UI", "ConnectingToDatabaseStatusMessage"));
            }
            connectToDatabase(sp);
        }

        if (sp != null) {
            sp.updateLoadingStatus(Localizer.localize("UI", "InitializingDesktopStatusMessage"));
        }

        systemConsoleScrollPane = new ListAppenderScrollPane(this, 100);
        systemConsoleTable = new JTable();
        systemConsoleTable.setBorder(null);
        systemConsoleScrollPane.setViewportView(systemConsoleTable);

        String country = Locale.getDefault().getCountry().toUpperCase();
        String language = Locale.getDefault().getLanguage().toLowerCase();
        DesktopTabbedPane desktopTabbedPane = new DefaultDesktopTabbedPane(country, language);

        try {
            if (Main.MAC_OS_X) {
                desktopTabbedPane = new MacDesktopTabbedPane(country, language);
            }
        } catch (Exception ex) {
            Misc.printStackTrace(ex);
        }

        if (Main.WINDOWS) {
            desktopTabbedPane.setBorder(new MatteBorder(0, 0, 1, 0, Color.gray));
        } else {
            desktopTabbedPane.setBorder(new MatteBorder(0, 1, 1, 0, Color.gray));
        }

        desktopDockingPanel =
            new DesktopDockingPanel(desktopTabbedPane, systemConsoleScrollPane.getScrollPane());

        desktopPopupMenu = new DesktopMenu(this);
        desktopTabbedPane.addMouseListener(new DesktopPaneMouseAdapter());
        desktopTabbedPane.addMouseListener(desktopTabbedPane);
        desktopTabbedPane.addMouseMotionListener(desktopTabbedPane);
        this.setFocusable(true);
        this.addKeyListener(new DesktopPaneKeyAdapter());

        initComponents();
        Globals.setStatusBar(getStatusBar());

        setTitle(Main.APPLICATION_NAME + " " + getVersion());

        setJMenuBar(menuBar);

        desktopTabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                desktopTabbedPaneStateChanged(e);
            }
        });

        desktopTabbedPane.addCloseListener(new CloseListener() {
            public void closeOperation(CloseListener cl, MouseEvent e, final int overTabIndex) {
                close(cl, overTabIndex);
            }

            @Override
            public void closeOperation(CloseListener cl, ActionEvent e, int overTabIndex) {
                close(cl, overTabIndex);
            }

            public void close(final CloseListener cl, final int overTabIndex) {
                closeTab(overTabIndex);
            }

        });

        if (sp != null) {
            sp.updateLoadingStatus(Localizer.localize("UI", "IntializingLoggingStatusMessage"));
        }

        log4j_init();

        resetToolbarTicks();

        desktopDockingPanel.setNavigatorPanelState(DesktopDockingPanel.PANEL_MINIMIZED);
        desktopDockingPanel.setSystemConsolePanelState(DesktopDockingPanel.PANEL_MINIMIZED);

        if (isDatabaseConnected) {
            getStatusBar().setDatabaseStatusConnected();
        }

        checkForUpdates();

        blockingGlassPane = new BlockingGlassPane();
        setGlassPane(blockingGlassPane);

        macOSXRegistration();

        // must set visible before setting size on osx.
        if (Main.MAC_OS_X) {
            setVisible(true);
        }

        // set the screen size
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle maximumWindowSize = graphicsEnvironment.getMaximumWindowBounds();

        if (applicationState.getScreenWidth() > 800 && applicationState.getScreenHeight() > 400) {

            Double storedWidth = new Double(applicationState.getScreenWidth());
            Double storedHeight = new Double(applicationState.getScreenHeight());

            if ((storedWidth >= 800 && storedHeight >= 400) && (
                storedWidth <= maximumWindowSize.getWidth() && storedHeight <= maximumWindowSize
                    .getHeight())) {
                setScreenBounds(new Rectangle(applicationState.getX(), applicationState.getY(),
                    storedWidth.intValue(), storedHeight.intValue()));
            } else {
                setScreenBounds(maximumWindowSize);
            }
        } else {
            setScreenBounds(maximumWindowSize);
        }

        if (!(Main.MAC_OS_X) && applicationState.isMaximized()) {
            this.setExtendedState(
                JFrame.MAXIMIZED_BOTH); // don't maximize in osx.. bug with oracle jdk maximize makes screen shrink to 0.
        }

        // set visible after setting size on others
        if (!(Main.MAC_OS_X)) {
            setVisible(true);
        }

        startMemoryMapReader();

        startScansWatcher();

    }

    public void setScreenBounds(Rectangle bounds) {

        setLocationRelativeTo(null);

        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        DisplayMode dm = graphicsEnvironment.getDefaultScreenDevice().getDisplayMode();

        int x = 0, y = 0, width, height;

        // check the max size is not beyond the default screen size
        if (dm.getWidth() < bounds.width) {
            width = dm.getWidth();
        } else {
            width = bounds.width;
        }

        if (dm.getHeight() < bounds.height) {
            height = dm.getHeight();
        } else {
            height = bounds.height;
        }

        // check the location is not beyond or less than 0
        if (x < 0 || x > dm.getWidth()) {
            x = 0;
        }

        if (y < 0 || y > dm.getHeight()) {
            y = 0;
        }

        Rectangle newBounds = new Rectangle(x, y, width, height);

        setLocation(x, y);
        setSize(newBounds.getSize());
        setPreferredSize(newBounds.getSize());

    }

    public void closeTab(final JComponent closingPanel) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                boolean performedClose = false;

                if (closingPanel instanceof EditorFrame) {
                    performedClose = ((EditorFrame) closingPanel).closeEditorFrame();
                } else if (closingPanel instanceof ReprocessorFrame) {
                    performedClose = ((ReprocessorFrame) closingPanel).closeReprocessorFrame();
                } else if (closingPanel instanceof ErrorLogFrame) {
                    deactivateErrorLogFrame();
                    checkDesktopPaneBG();
                } else if (closingPanel instanceof SourceDataManagerFrame) {
                    deactivateSDMFrame();
                    checkDesktopPaneBG();
                } else if (closingPanel instanceof CapturedDataManagerFrame) {
                    deactivateCDMFrame();
                    checkDesktopPaneBG();
                } else if (closingPanel instanceof ProcessingQueueManagerFrame) {
                    deactivatePQMFrame();
                    checkDesktopPaneBG();
                }

                if (performedClose) {
                    getDesktopTabbedPane().remove(closingPanel);
                    checkDesktopPaneBG();
                    UndoManager.setInstance(new UndoManager());
                    UndoManager.getInstance().fireAllEvents();
                    System.gc();
                }
                revalidateAllFrames();
            }
        });
    }

    public void closeTab(final int overTabIndex) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JPanel closingPanel = (JPanel) getDesktopTabbedPane().getComponentAt(overTabIndex);
                boolean performedClose = false;

                if (closingPanel instanceof EditorFrame) {
                    performedClose = ((EditorFrame) closingPanel).closeEditorFrame();
                } else if (closingPanel instanceof ReprocessorFrame) {
                    performedClose = ((ReprocessorFrame) closingPanel).closeReprocessorFrame();
                } else if (closingPanel instanceof ErrorLogFrame) {
                    deactivateErrorLogFrame();
                    checkDesktopPaneBG();
                } else if (closingPanel instanceof SourceDataManagerFrame) {
                    deactivateSDMFrame();
                    checkDesktopPaneBG();
                } else if (closingPanel instanceof CapturedDataManagerFrame) {
                    deactivateCDMFrame();
                    checkDesktopPaneBG();
                } else if (closingPanel instanceof ProcessingQueueManagerFrame) {
                    deactivatePQMFrame();
                    checkDesktopPaneBG();
                }

                if (performedClose) {
                    getDesktopTabbedPane().remove(overTabIndex);
                    checkDesktopPaneBG();
                    UndoManager.setInstance(new UndoManager());
                    UndoManager.getInstance().fireAllEvents();
                    System.gc();
                }
                revalidateAllFrames();
            }
        });
    }

    public static String getVersion() {

        String versionStr = Main.VERSION;

        switch (releaseType) {

            case RELEASE:
                break;

            case RELEASE_CANDIDATE:
                versionStr += " Release Candidate " + buildNumber;
                break;

            case BETA:
                versionStr += " Beta " + buildNumber;
                break;

            case ALPHA:
                versionStr += " Alpha " + buildNumber;
                break;

        }

        return versionStr;

    }

    private void sendOpenFileMessage(String[] args) {

        File fileToLoad = null;

        if (args.length > 0 && args[0].trim().length() > 0) {
            String fileToLoadString = args[0].trim();
            fileToLoad = new File(fileToLoadString);
            if (!(fileToLoad.exists())) {
                return;
            }

            boolean fileExtensionOK = false;

            Vector<String> extensions = Misc.getFormReturnFileWhitelist();
            for (String extension : extensions) {
                if (fileToLoadString.endsWith(extension)) {
                    fileExtensionOK = true;
                    break;
                }
            }

            if (!fileExtensionOK) {
                return;
            }

        } else {
            return;
        }

        if (args.length <= 0) {
            return;
        }

        byte[] openFileBytes = (fileToLoad.getPath() + "\n").getBytes();

        try {
            FileChannel fc = new RandomAccessFile(getOpenFileIPC(), "rw").getChannel();
            MappedByteBuffer mem = fc.map(FileChannel.MapMode.READ_WRITE, 0, openFileBytes.length);
            mem.put(openFileBytes);
            fc.close();
        } catch (FileNotFoundException e) {
            Misc.printStackTrace(e);
        } catch (IOException e) {
            Misc.printStackTrace(e);
        }

    }

    private void checkForUpdates() {

        if (softwareUpdateManager == null) {
            softwareUpdateManager = new SoftwareUpdateManager();
        }

        SwingWorker<SoftwareUpdateDialog, Void> worker =
            new SwingWorker<SoftwareUpdateDialog, Void>() {

                public SoftwareUpdateDialog doInBackground() {

                    // check preferences to see if we are allowed to check for updates
                    if (softwareUpdateManager.isUpdateEnabled()) {
                        return softwareUpdateManager.checkForUpdates();
                    }

                    return null;

                }

                public void done() {

                    try {
                        final SoftwareUpdateDialog sud = get();
                        if (sud != null) {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    sud.setVisible(true);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                    } catch (ExecutionException e) {
                        com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                    }

                }

            };
        worker.execute();

    }

    public void blockInput() {
        getBlockingGlassPane().block(true);
    }

    public void unblockInput() {
        getBlockingGlassPane().block(false);
    }

    private boolean testConnection(String jdbcURL, String username, String password) {

        String driverName = "org.apache.derby.jdbc.ClientDriver";
        Connection conn = null;
        boolean isSuccess = false;

        try {
            Class.forName(driverName).newInstance();
            Properties props = new Properties();
            props.put("user", username);
            props.put("password", password);
            props.put("securityMechanism",
                ClientDataSource.STRONG_PASSWORD_SUBSTITUTE_SECURITY + "");
            conn = DriverManager.getConnection(jdbcURL, props);
            isSuccess = true;
        } catch (InstantiationException ex) {
            isSuccess = false;
        } catch (IllegalAccessException ex) {
            isSuccess = false;
        } catch (ClassNotFoundException ex) {
            isSuccess = false;
        } catch (SQLException ex) {
            isSuccess = false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception cse) {
                }
            }
        }

        return isSuccess;

    }

    private void connectToDatabase(SplashDialog sp) {
        EntityManager em = null;
        int connectCount = 0;

        String jdbcURL = getJPAConfiguration().getConnectionURL();
        String username = getJPAConfiguration().getConnectionUserName();
        String password = getJPAConfiguration().getConnectionPassword();

        while (connectCount < 41) {
            if (testConnection(jdbcURL, username, password)) {
                isDatabaseConnected = true;
                break;
            } else {
                ++connectCount;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                if (connectCount > 40) {
                    return;
                }
            }

        }

        if (sp != null) {
            sp.updateLoadingStatus(Localizer.localize("UI", "ConnectedStatusMessage"));
        }

        em = getJPAConfiguration().getEntityManager();
        if (em != null) {
            em.close();
        }

        // do a garbage collection
        System.gc();

    }

    protected void deactivateCDMFrame() {
        getDesktopTabbedPane().remove(capturedDataManagerFrame);
    }

    protected void deactivatePQMFrame() {
        getDesktopTabbedPane().remove(processingQueueManagerFrame);
    }

    protected void deactivateSDMFrame() {
        getDesktopTabbedPane().remove(sourceDataManagerFrame);
    }

    protected void updateMenuCheckboxes(EditorFrame editorFrame) {
        JGraph graph = editorFrame.getGraph();
        mainMenu.enableGridMenuItems();
        mainMenu.setShowGridCheckBoxMenuItemSelected(!(graph.getEditor().getGridHidden()));
        mainMenu.setSnapToGridCheckBoxMenuItemSelected(graph.getEditor().isSnapToGrid());
    }

    protected void desktopTabbedPaneStateChanged(ChangeEvent e) {

        JPanel[] frames = getDesktopTabbedPane().getAllFrames();

        for (int i = 0; i < frames.length; ++i) {
            if (frames[i] instanceof EditorFrame) {
                ((EditorFrame) frames[i]).unpressAllButtons();
                if (((EditorFrame) frames[i]).getEditor().getSelectionManager().size() > 0) {
                    ((EditorFrame) frames[i]).getEditor().getSelectionManager().deselectAll();
                }
                ((EditorFrame) frames[i]).getEditor().getModeManager().leaveAll();
            }
        }

        if (getSelectedFrame() != null && getSelectedFrame().getEditor() != null) {
            getSelectedFrame().getEditor().setAsActiveUndoManager();
        }

        if (getDesktopTabbedPane().getSelectedComponent() instanceof EditorFrame) {
            ((EditorFrame) getDesktopTabbedPane().getSelectedComponent()).setActiveEditor();
            updateMenuCheckboxes((EditorFrame) getDesktopTabbedPane().getSelectedComponent());
        } else {
            mainMenu.disableGridMenuItems();
        }

        revalidateAllFrames();

    }

    private void revalidateAllFrames() {

        getPropertiesPanelController().destroyPanels();

        if (getSelectedFrame() != null) {
            if (getSelectedFrame() instanceof FormFrame) {
                FormFrame formFrame = (FormFrame) getSelectedFrame();
                formFrame.updateProperties();
                formFrame.refreshDatabaseTables();
            } else if (getSelectedFrame() instanceof SegmentFrame) {
                SegmentFrame segmentFrame = (SegmentFrame) getSelectedFrame();
                segmentFrame.updateProperties();
            }
        } else {

            if (getDesktopTabbedPane().getSelectedFrame() instanceof ReprocessorFrame) {
                ((ReprocessorFrame) getDesktopTabbedPane().getSelectedFrame()).updateProperties();
            }

            if (getDesktopTabbedPane().getSelectedFrame() instanceof SourceDataManagerFrame) {
                ((SourceDataManagerFrame) getDesktopTabbedPane().getSelectedFrame())
                    .updatePropertyBox();
            }

            if (getDesktopTabbedPane().getSelectedFrame() instanceof CapturedDataManagerFrame) {
                ((CapturedDataManagerFrame) getDesktopTabbedPane().getSelectedFrame())
                    .updatePropertyBox();
                ((CapturedDataManagerFrame) getDesktopTabbedPane().getSelectedFrame()).refresh();
            }

            if (getDesktopTabbedPane().getSelectedFrame() instanceof ProcessingQueueManagerFrame) {
                ((ProcessingQueueManagerFrame) getDesktopTabbedPane().getSelectedFrame())
                    .updatePropertyBox();
                ((ProcessingQueueManagerFrame) getDesktopTabbedPane().getSelectedFrame()).refresh();
            }

        }
    }

    public static Main getInstance() {
        return Main.instance;
    }

    private boolean isInstanceRunning() {
        final int PORT = 44593;
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

    public static void main(final String args[]) {

        if (Main.MAC_OS_X) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        } else if (Main.WINDOWS) {
            System.setProperty("sun.java2d.dpiaware", "false");
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                Localizer
                    .addResource("GefBase", "com.ebstrada.formreturn.language.BaseResourceBundle");

                Localizer.addResource("UI", "com.ebstrada.formreturn.language.UIResourceBundle");
                Localizer
                    .addResource("UICDM", "com.ebstrada.formreturn.language.UICDMResourceBundle");

                Localizer
                    .addResource("Util", "com.ebstrada.formreturn.language.UtilResourceBundle");

                Localizer
                    .addResource("Server", "com.ebstrada.formreturn.language.ServerResourceBundle");

                Localizer
                    .addResource("HelpLabel", "com.ebstrada.formreturn.language.HelpLabelBundle");

                ResourceLoader.addResourceExtension("png");
                ResourceLoader.addResourceExtension("gif");
                ResourceLoader.addResourceLocation("/com/ebstrada/formreturn/manager/gef/Images");

                // load xstream
                setXstream(createXStream());
                getXstream().setMode(XStream.NO_REFERENCES);
                Annotations.load(getXstream());

                // set LAF and other Swing properties
                System.setProperty("org.apache.batik.warn_destination", "false");
                System.setProperty("swing.aatext", "true");

                Globals.setShowFigTips(true);

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

                // Load preferences, if fail warn that preferences are corrupt and ask to create new, else quit.
                try {
                    PreferencesManager.loadPreferences(Main.getXstream());
                } catch (Exception ex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                    String message = Localizer.localize("UI", "PreferencesLoadErrorMessage");
                    int n = JOptionPane.showConfirmDialog(null, message,
                        Localizer.localize("UI", "PreferencesLoadErrorTitle"),
                        JOptionPane.YES_NO_OPTION);

                    if (n != 0) {
                        // Exit program as requested
                        Misc.closeLogFiles();
                        System.exit(0);
                    }

                    // else rebuild the system preferences
                    try {
                        PreferencesManager.loadPreferences(true, Main.getXstream());
                    } catch (Exception ex2) {
                        Misc.showErrorMsg(null,
                            Localizer.localize("UI", "UnableToCreatePreferencesFile"));
                    }

                }

                final ApplicationStatePreferences applicationState =
                    PreferencesManager.getApplicationState();

                // set the locale based on user settings
                String locale = applicationState.getLocale();
                FontLocalesImpl fontLocales = FontLocaleUtil.getFontLocale(locale);
                Locale.setDefault(fontLocales.getLocale());

                if (Localizer.getCurrentLocale() != Locale.getDefault()) {
                    Localizer.addLocale(Locale.getDefault());
                    Localizer.switchCurrentLocale(Locale.getDefault());
                    com.ebstrada.aggregation.i18n.Localizer.setCurrentLocale(Locale.getDefault());
                }

                // Load plugins
                Misc.loadPluginManager();

                final SplashDialog splashDialog = new SplashDialog(null);
                splashDialog.setVisible(true);
                splashDialog.toFront();
                splashDialog.requestFocus();
                splashDialog.setAlwaysOnTop(true);
                splashDialog.setAlwaysOnTop(false);

                final Map<String, SplashDialog> splashDialogMap =
                    new HashMap<String, SplashDialog>();
                splashDialogMap.put("splash", splashDialog);

                SwingWorker<Main, Void> worker = new SwingWorker<Main, Void>() {

                    public Main doInBackground() {

                        Main _mainFrame = new Main(splashDialogMap, args, applicationState);
                        Globals.showStatus(Localizer.localize("UI", "DoneStatusMessage"));

                        ApplicationStatePreferences applicationState =
                            PreferencesManager.getApplicationState();
                        if (multipleInstanceChecker != null && applicationState
                            .isMultipleInstanceCheckerEnabled()) {
                            multipleInstanceChecker.setWindow(_mainFrame);
                        }

                        return _mainFrame;

                    }

                    public void done() {

                        try {
                            final Main _mainFrame = get();
                            if (_mainFrame != null) {
                                java.awt.EventQueue.invokeLater(new Runnable() {
                                    @Override public void run() {
                                        _mainFrame.toFront();
                                        _mainFrame.requestFocus();
                                    }
                                });
                            }
                        } catch (InterruptedException e) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                        } catch (ExecutionException e) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                        }

                        splashDialog.dispose();

                    }

                };
                worker.execute();

            }
        });

    }

    public JPAConfiguration getJPAConfiguration() {
        if (this.jpaconfiguration == null) {
            this.jpaconfiguration = new JPAConfiguration(false);
        }
        return jpaconfiguration;
    }

    public void setJPAConfiguration(JPAConfiguration jpaconfiguration) {
        this.jpaconfiguration = jpaconfiguration;
    }

    public void quit() {
        closeApplication();
    }

    public void about() {
        AboutDialog aboutDialog = new AboutDialog(this);
        aboutDialog.setVisible(true);
    }

    public void addRecentFile(File recentFile) {
        if (!(recentFile.exists())) {
            return;
        }
        if (recentFileStack.contains(recentFile)) {
            return;
        }
        recentFileStack.push(recentFile);
        if (recentFileStack.size() > 10) {
            recentFileStack.removeElementAt(0);
        }
        rebuildRecentFileMenu();
    }

    public void closeApplication() {

        ApplicationStatePreferences applicationState = PreferencesManager.getApplicationState();

        if (applicationState.isConfirmExit()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ConfirmExitDialog ced = new ConfirmExitDialog(Main.getInstance());
                    ced.setVisible(true);
                    if (ced.getDialogResult() == JOptionPane.OK_OPTION) {
                        confirmClose();
                    }
                }
            });
        } else {
            confirmClose();
        }

    }

    public void confirmClose() {

        if (closeAllFrames() == false) {
            return;
        }

        // save recent file list
        PreferencesManager.setRecentFileStack(recentFileStack);

        ApplicationStatePreferences applicationState = PreferencesManager.getApplicationState();

        // save screen size
        if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            applicationState.setMaximized(true);
        } else {
            applicationState.setMaximized(false);
        }

        applicationState.setScreenWidth(getWidth());
        applicationState.setScreenHeight(getHeight());
        applicationState.setX(getX());
        applicationState.setY(getY());

        dispose();
        PreferencesManager.removeWorkingFiles();
        Misc.closeLogFiles();
        System.exit(0);

    }

    public void sourceDataManagerActionPerformed(ActionEvent e) {
        String title = Localizer.localize("UI", "SourceDataWindowTitle");
        openSourceDataManager(title);
    }

    public void createNewForm(ActionEvent e) {

        try {
            FormSetupDialog formSetupDialog = new FormSetupDialog(this, true,
                Localizer.localize("UI", "CreateANewFormDialogTitle"));
            formSetupDialog.setModal(true);
            formSetupDialog.setVisible(true);

            if (formSetupDialog.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {

                PageAttributes pageAttributes = formSetupDialog.getPageAttributes();
                DocumentAttributes documentAttributes = formSetupDialog.getDocumentAttributes();
                JGraph graph = new JGraph();
                graph.createDocument(pageAttributes);
                graph.setPageAttributes(pageAttributes);
                graph.setDocumentAttributes(documentAttributes);
                EditorFrame editorFrame = createEditorFrame(graph);
                addEditorFrame(editorFrame);

            }

        } catch (Exception ex) {
            Misc.showErrorMsg(getInstance(), ex.getLocalizedMessage());
            Misc.printStackTrace(ex);
        }

    }

    public void createNewSegment(ActionEvent e) {

        try {

            SegmentSetupDialog segmentSetupDialog = new SegmentSetupDialog(this, true,
                Localizer.localize("UI", "CreateANewSegmentDialogTitle"));
            segmentSetupDialog.setModal(true);
            segmentSetupDialog.setVisible(true);

            if (segmentSetupDialog.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {

                PageAttributes pageAttributes = segmentSetupDialog.getPageAttributes();
                DocumentAttributes documentAttributes = segmentSetupDialog.getDocumentAttributes();
                JGraph graph = new JGraph();
                graph.createDocument(pageAttributes);
                graph.setPageAttributes(pageAttributes);
                graph.setDocumentAttributes(documentAttributes);

                EditorFrame editorFrame = createEditorFrame(graph);
                addEditorFrame(editorFrame);

            }

        } catch (Exception ex) {
            Misc.showErrorMsg(getInstance(), ex.getLocalizedMessage());
            Misc.printStackTrace(ex);
        }

    }

    public Properties getApplicationProperties() {
        return applicationProperties;
    }

    public JPanel getErrorLogFrame() {
        return errorLogFrame;
    }

    public void activateErrorLogFrame() {
        activateErrorLogFrameNoSwitch();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getDesktopTabbedPane().setSelectedComponent(errorLogFrame);
            }
        });
    }

    public void activateErrorLogFrameNoSwitch() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getDesktopTabbedPane().add(errorLogFrame, 0);
                checkDesktopPaneBG();
            }
        });
    }

    public void activateEditorFrame(final EditorFrame editorFrame) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getDesktopTabbedPane().setSelectedComponent(editorFrame);
            }
        });
    }

    public void activateReprocessorFrame(final ReprocessorFrame reprocessorFrame) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getDesktopTabbedPane().setSelectedComponent(reprocessorFrame);
            }
        });
    }

    public void deactivateErrorLogFrame() {
        getDesktopTabbedPane().remove(errorLogFrame);
    }

    public String getFirstFreeFormName() {

        JPanel[] frames = getDesktopTabbedPane().getAllFrames();

        for (int k = 1; ; k++) {
            String name = Localizer.localize("UI", "UntitledFormPrefix") + k;
            boolean found = false;
            for (int i = 0; i < frames.length; ++i) {
                if (frames[i] instanceof FormFrame) {
                    FormFrame ff = (FormFrame) frames[i];
                    if (ff.getTitle().equalsIgnoreCase(name)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                return name;
            }
        }

    }

    public String getFirstFreeSegmentName() {

        JPanel[] frames = getDesktopTabbedPane().getAllFrames();

        for (int k = 1; ; k++) {
            String name = Localizer.localize("UI", "UntitledSegmentPrefix") + k;
            boolean found = false;
            for (int i = 0; i < frames.length; ++i) {
                if (frames[i] instanceof SegmentFrame) {
                    SegmentFrame segmentFrame = (SegmentFrame) frames[i];
                    if (segmentFrame.getTitle().equalsIgnoreCase(name)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                return name;
            }
        }

    }

    public JMenu getRecentFileMenu(JMenu menu) {
        menu.removeAll();

        for (int i = 0; i < recentFileStack.size(); i++) {
            JMenuItem menuItem = new JMenuItem();
            menuItem.setFont(UIManager.getFont("MenuItem.font"));
            File recentFile = (File) recentFileStack.elementAt(i);
            menuItem.setText(recentFile.getName());
            menuItem.setToolTipText(recentFile.getPath());
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    open(((JMenuItem) e.getSource()).getToolTipText());
                }
            });
            menu.add(menuItem);
        }
        return menu;
    }

    public JScrollPane getSystemConsoleScrollPane() {
        return systemConsoleScrollPane;
    }

    public void macOSXRegistration() {
        if (Main.MAC_OS_X) {
            try {

                OSXAdapter.setQuitHandler(this,
                    getClass().getDeclaredMethod("closeApplication", (Class[]) null));
                OSXAdapter
                    .setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[]) null));
                OSXAdapter.setPreferencesHandler(this,
                    getClass().getDeclaredMethod("preferences", (Class[]) null));
                OSXAdapter.setFileHandler(this,
                    getClass().getDeclaredMethod("open", new Class[] {File.class}));
                if (System.getProperty("apple.laf.useScreenMenuBar") != null) {
                    if (System.getProperty("apple.laf.useScreenMenuBar").equals("true")) {
                        mainMenu.removeOSXMenuItems();
                    }
                }

            } catch (NoClassDefFoundError e) {
                System.err
                    .println(Localizer.localize("UI", "EAWTNotSupportedMessage") + " (" + e + ")");
            } catch (Exception e) {
                System.err.println(Localizer.localize("UI", "OSXAdapterExceptionMessage") + ":");
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
            }
        }
    }

    public void open() {

        File file = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();
        filter.addExtension("frs");
        filter.addExtension("frf");

        FileDialog fd =
            new FileDialog(Main.getInstance(), Localizer.localize("UI", "OpenFileDialogTitle"),
                FileDialog.LOAD);
        fd.setFilenameFilter(filter);

        File lastDir = null;
        if (Globals.getLastDirectory() != null) {

            lastDir = new File(Globals.getLastDirectory());

            if (!(lastDir.exists())) {
                lastDir = null;
            }

        }

        if (lastDir == null) {
            lastDir = new File(System.getProperty("user.home"));
        }

        try {
            fd.setDirectory(lastDir.getCanonicalPath());
        } catch (IOException e1) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
            return;
        }

        fd.setModal(true);
        fd.setVisible(true);
        if (fd.getFile() != null) {
            String filename = fd.getFile();
            file = new File(fd.getDirectory() + filename);
            if (file.isDirectory()) {
                return;
            }
            try {
                Globals.setLastDirectory(file.getCanonicalPath());
            } catch (IOException ldex) {
            }
        } else {
            return;
        }

        open(file);

    }

    public boolean checkReprocessorFrameOpen(long id, int editorType) {
        JPanel[] frames = getDesktopTabbedPane().getAllFrames();
        for (int i = 0; i < frames.length; ++i) {
            if (frames[i] instanceof ReprocessorFrame) {
                ReprocessorFrame reprocessorFrame = (ReprocessorFrame) frames[i];
                if (reprocessorFrame.getEditorType() == editorType
                    && reprocessorFrame.getIncomingImageId() == id) {
                    getDesktopTabbedPane().setSelectedComponent(reprocessorFrame);
                    return true;
                } else if (reprocessorFrame.getEditorType() == editorType
                    && reprocessorFrame.getFormPageId() == id) {
                    getDesktopTabbedPane().setSelectedComponent(reprocessorFrame);
                    return true;
                }
            }
        }
        return false;
    }

    synchronized public void open(final File file) {

        JPanel[] frames = getDesktopTabbedPane().getAllFrames();

        for (int i = 0; i < frames.length; ++i) {
            if (frames[i] instanceof EditorFrame) {
                EditorFrame editorFrame = (EditorFrame) frames[i];
                if (editorFrame.getGraph().getDocumentPackage().getPackageFile() != null) {
                    try {
                        if (editorFrame.getGraph().getDocumentPackage().getPackageFile()
                            .getCanonicalPath() == file.getCanonicalPath()) {
                            getDesktopTabbedPane().setSelectedComponent(editorFrame);
                            return;
                        }
                    } catch (IOException e) {
                        com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                    }
                }
            }
        }

        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.setVisible(true);
        Globals.showStatus(
            Localizer.localize("UI", "OpeningFileStatusMessage") + " " + file.getPath() + "...");

        getStatusBar().setProgressValue(25);

        openWorker = new SwingWorker<EditorFrame, Void>() {

            public EditorFrame doInBackground() {

                EditorFrame editorFrame = null;

                JGraph graph = null;

                if (file != null) {

                    graph = new JGraph();

                    try {
                        graph.getDocumentPackage().open(file, graph);

                        // always load page number 1 as startup page
                        Page pageContainer = graph.getDocument().getPageByPageNumber(1);
                        graph.setCurrentPageNumber(1);
                        graph.setPageAttributes(pageContainer.getPageAttributes());

                        List figs = pageContainer.getFigs();
                        if (figs != null) {
                            Layer lay = graph.getEditor().getLayerManager().getActiveLayer();
                            for (Iterator it = figs.iterator(); it.hasNext(); ) {
                                Fig fig = (Fig) it.next();
                                // this loads the reverse way to the editor.add() method.
                                // this is because we don't want to be notified of the layer adding as a change when loading.
                                lay.add(fig);
                                graph.getEditor().addPropertyChangeListener(fig);
                            }
                        }
                        graph.getEditor().postLoad();
                        graph.getEditor().resetEditorStateChangedFlag();
                    } catch (Exception e) {
                        Main.applicationExceptionLog.error(
                            Localizer.localize("UI", "UnableToOpenFileErrorMessage") + ": " + file
                                .getPath(), e);
                        try {
                            graph.getDocumentPackage().close();
                        } catch (Exception e1) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                        }

                        return null;
                    }
                }

                if (graph != null) {

                    editorFrame = createEditorFrame(graph);

                    addRecentFile(file);
                }

                return editorFrame;

            }

            public void done() {

                getStatusBar().setProgressValue(100);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        EditorFrame editorFrame = null;
                        try {
                            editorFrame = get();
                        } catch (InterruptedException e) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                        } catch (ExecutionException e) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                        }
                        if (editorFrame != null) {
                            addEditorFrame(editorFrame);
                        }
                    }
                });

                if (loadingDialog != null) {
                    loadingDialog.dispose();
                }

                getStatusBar().setProgressValue(0);
                Globals.showStatus(Localizer.localize("UI", "DoneStatusMessage"));

                synchronized (Main.this) {
                    // worker is checked and assigned inside synchronized block
                    Main.this.openWorker = null;
                }

            }

        };
        openWorker.execute();

    }

    public void open(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            open(file);
        } else {
            Main.applicationExceptionLog
                .error(Localizer.localize("UI", "UnableToFindFileErrorMessage") + ": " + filename);
        }
    }

    public void preferences() {
        PreferencesManager.showPreferencesDialog();
    }

    public void rebuildRecentFileMenu() {
        mainRecentFileMenu = getRecentFileMenu(mainRecentFileMenu);
        mainRecentFileMenu.updateUI();
        popupRecentFileMenu = getRecentFileMenu(popupRecentFileMenu);
        popupRecentFileMenu.setFont(UIManager.getFont("Menu.font"));
        popupRecentFileMenu.updateUI();
    }

    public boolean save(EditorFrame selectedFrame, boolean isSaveAs) {

        Editor currentEditor = selectedFrame.getEditor();
        File file = selectedFrame.getGraph().getDocumentPackage().getPackageFile();

        FileDialog fd = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();

        File lastDir = null;
        if (Globals.getLastDirectory() != null) {

            lastDir = new File(Globals.getLastDirectory());

            if (!(lastDir.exists())) {
                lastDir = null;
            }

        }

        if (lastDir == null) {
            lastDir = new File(System.getProperty("user.home"));
        }

        if (selectedFrame instanceof SegmentFrame) {
            String title = Localizer.localize("UI", "SaveSegmentDialogTitle");
            if (isSaveAs) {
                title = Localizer.localize("UI", "SaveSegmentAsDialogTitle");
            }
            filter.addExtension("frs");
            fd = new FileDialog(Main.getInstance(), title, FileDialog.SAVE);
            try {
                fd.setDirectory(lastDir.getCanonicalPath());
            } catch (IOException e1) {
            }
            fd.setFilenameFilter(filter);

            String filename = selectedFrame.getTitle() + ".frs";
            if (selectedFrame.getGraph().getDocumentPackage().getPackageFile() != null) {
                filename = selectedFrame.getGraph().getDocumentPackage().getPackageFile().getPath();
            }

            fd.setFile(filename);

        } else if (selectedFrame instanceof FormFrame) {
            String title = Localizer.localize("UI", "SaveFormDialogTitle");
            if (isSaveAs) {
                title = Localizer.localize("UI", "SaveFormAsDialogTitle");
            }
            filter.addExtension("frf");
            fd = new FileDialog(Main.getInstance(), title, FileDialog.SAVE);
            try {
                fd.setDirectory(lastDir.getCanonicalPath());
            } catch (IOException e1) {
            }
            fd.setFilenameFilter(filter);

            String filename = selectedFrame.getTitle() + ".frf";
            if (selectedFrame.getGraph().getDocumentPackage().getPackageFile() != null) {
                filename = selectedFrame.getGraph().getDocumentPackage().getPackageFile().getPath();
            }

            fd.setFile(filename);
        }

        if (isSaveAs) {
            if (file != null) {
                fd.setDirectory(file.getParentFile().getAbsolutePath());
                fd.setFile(file.getName());
            }
            file = null;
        }

        // set current editor in globals incase not set
        if (currentEditor != null) {
            Globals.curEditor(currentEditor);
        }

        currentEditor.setAntiAlias(true);

        if ((currentEditor != null) && (file == null)) {

            fd.setModal(true);
            fd.setVisible(true);

            if (fd.getFile() != null) {
                String filename = fd.getFile();
                if (selectedFrame instanceof SegmentFrame) {
                    if (!(filename.endsWith(".frs") || filename.endsWith(".FRS"))) {
                        filename += ".frs";
                    }
                } else if (selectedFrame instanceof FormFrame) {
                    if (!(filename.endsWith(".frf") || filename.endsWith(".FRF"))) {
                        filename += ".frf";
                    }
                }
                file = new File(fd.getDirectory() + filename);
                if (file.isDirectory()) {
                    return false;
                }
                return save(currentEditor, file, selectedFrame, false);
            } else {
                return false;
            }

        } else if ((currentEditor != null) && (file != null)) {

            // check that the file is not in any other frame first.
            JPanel[] frames = getDesktopTabbedPane().getAllFrames();

            boolean isOverWrite = true;

            for (int i = 0; i < frames.length; ++i) {
                if ((frames[i] == selectedFrame) || !(frames[i] instanceof EditorFrame)) {
                    continue;
                }
                if (((EditorFrame) frames[i]).getGraph().getDocumentPackage().getPackageFile()
                    != null) {
                    if (file.getPath().equals(
                        ((EditorFrame) frames[i]).getGraph().getDocumentPackage().getPackageFile()
                            .getPath())) {
                        isOverWrite = false;
                    }
                }
            }

            return save(currentEditor, file, selectedFrame, isOverWrite);

        }
        return true;
    }

    synchronized public boolean save(Editor editor, final File file, EditorFrame selectedFrame,
        boolean isOverWrite) {

        if ((file != null) && file.getParentFile().isDirectory()) {
            Globals.setLastDirectory(file.getParentFile().getPath());
        }

        if ((file != null) && (editor != null)) {
            Globals.showStatus(String
                .format(Localizer.localize("UI", "WritingFileStatusMessage"), file.getPath()));
            try {

                // this code stores the page that is currently being edited back to the document
                Document document = selectedFrame.getGraph().getDocument();
                Page pageContainer =
                    document.getPageByPageNumber(selectedFrame.getGraph().getCurrentPageNumber());
                pageContainer.setFigs(editor.getLayerManager().getContents());

                // save the document to a package
                DocumentPackage documentPackage = selectedFrame.getGraph().getDocumentPackage();
                documentPackage.save(file, selectedFrame.getGraph());
                selectedFrame.getGraph().getEditor().resetEditorStateChangedFlag();

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {

                        // update the frame title to the new filename
                        getDesktopTabbedPane()
                            .setTitleAt(getDesktopTabbedPane().getSelectedIndex(), file.getName());

                        // add the new file to the list of recently saved files
                        addRecentFile(file);

                        // reset the undo stack
                        UndoManager undoInstance = UndoManager.getInstance();
                        if (undoInstance != null) {
                            undoInstance.reset();
                            undoInstance.fireAllEvents();
                        }

                    }
                });

            } catch (Exception ex) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                Globals.showStatus(
                    Localizer.localize("UI", "WriteFileFailureStatusMessage") + " " + file
                        .getPath());
            }
        }
        return true;

    }

    public void save(String title, boolean isSaveAs) {
        JPanel frame = getDesktopTabbedPane().getSelectedFrame();
        if (frame instanceof EditorFrame) {
            EditorFrame selectedFrame = (EditorFrame) frame;
            save(selectedFrame, isSaveAs);
        } else if (frame instanceof ReprocessorFrame) {
            ReprocessorFrame selectedFrame = (ReprocessorFrame) frame;
            selectedFrame.saveCapturedDataButtonActionPerformed(null);
        }
    }

    public void setApplicationProperties(Properties properties) {
        applicationProperties = properties;
    }

    public void setErrorLogFrame(JPanel frame) {
        errorLogFrame = frame;
    }

    public void setSourceDataManagerFrame(JPanel frame) {
        sourceDataManagerFrame = frame;
    }

    public void setCapturedDataManagerFrame(JPanel frame) {
        capturedDataManagerFrame = frame;
    }

    public void unpressAllButtons() {
        JPanel selectedFrame = (JPanel) getDesktopTabbedPane().getSelectedFrame();
        ((EditorFrame) selectedFrame).unpressAllButtons();
    }

    public void aboutItemActionPerformed(ActionEvent e) {
        about();
    }

    private boolean closeAllFrames() {
        JPanel[] frames = getDesktopTabbedPane().getAllFrames();

        for (int i = 0; i < frames.length; ++i) {
            if (frames[i] instanceof EditorFrame) {
                if (((EditorFrame) frames[i]).closeEditorFrame() == false) {
                    return false;
                }
            }
            if (frames[i] instanceof ReprocessorFrame) {
                if (((ReprocessorFrame) frames[i]).closeReprocessorFrame() == false) {
                    return false;
                }
            }
        }

        return true;

    }

    private void openSourceDataManager(final String title) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    protected Void doInBackground() throws InterruptedException {
                        if (sourceDataManagerFrame == null) {
                            setSourceDataManagerFrame(new SourceDataManagerFrame());
                        }
                        sourceDataManagerFrame.setName(title);
                        return null;
                    }

                    protected void done() {
                        if (sourceDataManagerFrame != null) {
                            if (sourceDataManagerFrame.getParent() != getDesktopTabbedPane()) {
                                getDesktopTabbedPane().add(sourceDataManagerFrame,
                                    javax.swing.JLayeredPane.DEFAULT_LAYER);
                            }
                            checkDesktopPaneBG();
                            getDesktopTabbedPane().setSelectedComponent(sourceDataManagerFrame);
                            UndoManager.setInstance(new UndoManager());
                            UndoManager.getInstance().fireAllEvents();
                        }
                    }
                };
                worker.execute();

            }
        });

    }

    private void openProcessingQueueManager(final String title) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    protected Void doInBackground() throws InterruptedException {
                        if (processingQueueManagerFrame == null) {
                            setProcessingQueueManagerFrame(new ProcessingQueueManagerFrame());
                            processingQueueManagerFrame.setName(title);
                        }
                        return null;
                    }

                    protected void done() {
                        if (processingQueueManagerFrame.getParent() != getDesktopTabbedPane()) {
                            getDesktopTabbedPane().add(processingQueueManagerFrame,
                                javax.swing.JLayeredPane.DEFAULT_LAYER);
                        }
                        checkDesktopPaneBG();
                        getDesktopTabbedPane().setSelectedComponent(processingQueueManagerFrame);
                        UndoManager.setInstance(new UndoManager());
                        UndoManager.getInstance().fireAllEvents();
                    }
                };
                worker.execute();

            }
        });

    }

    private void openCapturedDataManager(final String title) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    protected Void doInBackground() throws InterruptedException {
                        if (capturedDataManagerFrame == null) {
                            setCapturedDataManagerFrame(new CapturedDataManagerFrame());
                        }
                        capturedDataManagerFrame.setName(title);
                        return null;
                    }

                    protected void done() {

                        if (capturedDataManagerFrame.getParent() != getDesktopTabbedPane()) {
                            getDesktopTabbedPane().add(capturedDataManagerFrame,
                                javax.swing.JLayeredPane.DEFAULT_LAYER);
                        }
                        checkDesktopPaneBG();
                        getDesktopTabbedPane().setSelectedComponent(capturedDataManagerFrame);
                        UndoManager.setInstance(new UndoManager());
                        UndoManager.getInstance().fireAllEvents();
                    }
                };
                worker.execute();

            }
        });

    }

    private EditorFrame createEditorFrame(JGraph graph) {

        EditorFrame editorFrame = null;

        if (graph.getDocumentAttributes().getDocumentType() == DocumentAttributes.FORM) {
            editorFrame = new FormFrame(graph);
        } else if (graph.getDocumentAttributes().getDocumentType() == DocumentAttributes.SEGMENT) {
            editorFrame = new SegmentFrame(graph);
        }

        editorFrame
            .setBounds(0, 0, getDesktopTabbedPane().getWidth(), getDesktopTabbedPane().getHeight());

        return editorFrame;

    }

    private void addEditorFrame(final EditorFrame editorFrame) {
        editorFrame.setFinishedLoading(true);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getDesktopTabbedPane().add(editorFrame, javax.swing.JLayeredPane.DEFAULT_LAYER);
                checkDesktopPaneBG();
                getDesktopTabbedPane().setSelectedComponent(editorFrame);
            }
        });
    }

    public void addReprocessorFrame(final ReprocessorFrame reprocessorFrame) {
        reprocessorFrame.setFinishedLoading(true);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getDesktopTabbedPane()
                    .add(reprocessorFrame, javax.swing.JLayeredPane.DEFAULT_LAYER);
                checkDesktopPaneBG();
                getDesktopTabbedPane().setSelectedComponent(reprocessorFrame);
            }
        });
    }

    private void desktopPaneKeyTyped(KeyEvent e) {

        if (debugStringTime < System.currentTimeMillis() - 3000) {
            debugString = "";
        }

        debugString += e.getKeyChar() + "";
        debugStringTime = System.currentTimeMillis();
        if (debugString.equalsIgnoreCase("debugmode")) {
            if (this.debugMode == false) {
                setSystemDebugMode(true);
                debugString = "";
                Misc.showSuccessMsg(Main.getInstance(),
                    "Debug Mode Active: To Disable Type \"stop\" on the desktop screen.");

            }
        }

        if (debugString.equalsIgnoreCase("stop")) {
            if (this.debugMode == true) {
                setSystemDebugMode(false);
                debugString = "";
                Misc.showSuccessMsg(Main.getInstance(),
                    "Debug Mode Disabled: To Re-enable Type \"debugmode\" on the desktop screen.");
            }
        }

        e.consume();

    }

    private void setSystemDebugMode(boolean b) {
        debugMode = b;
    }

    private void desktopPane1MousePressed(MouseEvent e) {
        if (e.isPopupTrigger() || (e.getButton() == MouseEvent.BUTTON3)) {
            desktopPopupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private void exitForm(WindowEvent e) {
        closeApplication();
    }

    private void initComponents() {
        menuBar = mainMenu.getMenuBar();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(new ImageIcon(getClass()
            .getResource("/com/ebstrada/formreturn/manager/ui/icons/frmanager_256x256.png"))
            .getImage());
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitForm(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        contentPane.add(menuBar, BorderLayout.NORTH);

        contentPane.add(desktopDockingPanel, BorderLayout.CENTER);

    }

    private void log4j_init() {

        try {
            URL u = this.getClass()
                .getResource("/com/ebstrada/formreturn/manager/log4j/log4j.properties");
            PropertyConfigurator.configure(u);
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        }

        // load the error log frame (hidden)
        setErrorLogFrame(new ErrorLogFrame(this));

        // log to listscrollpane
        Appender testapp = ComponentAppender.getAppender("listscrollpane");
        if ((testapp != null) && (testapp instanceof ScrollPaneAppender)) {
            ((ScrollPaneAppender) testapp).setComponent(systemConsoleScrollPane);
        }

        System.setProperty("sun.awt.exception.handler",
            (com.ebstrada.formreturn.manager.log4j.AwtExceptionHandler.class).getName());

    }

    private void checkDesktopPaneBG() {

        getDesktopTabbedPane().setBackground(UIManager.getColor("TabbedPane.background"));

        if (getDesktopTabbedPane().getTabCount() > 0) {
            getDesktopTabbedPane().setOpaque(true);
            desktopDockingPanel.setNavigatorPanelState(DesktopDockingPanel.PANEL_RESTORED);
        } else {
            getDesktopTabbedPane().setOpaque(false);
            desktopDockingPanel.setNavigatorPanelState(DesktopDockingPanel.PANEL_MINIMIZED);
        }

    }

    public void openItemActionPerformed(ActionEvent e) {
        open();
    }

    public void preferencesItemActionPerformed(ActionEvent e) {
        preferences();
    }

    public void quitItemActionPerformed(ActionEvent e) {
        closeApplication();
    }

    public void saveAllItemActionPerformed(ActionEvent e) {
        saveAll(e);
    }

    private void saveAll(ActionEvent e) {
        JPanel[] frames = getDesktopTabbedPane().getAllFrames();

        for (int i = 0; i < frames.length; ++i) {
            if (frames[i] instanceof EditorFrame) {
                getDesktopTabbedPane().setSelectedComponent(frames[i]);
                saveItemActionPerformed(e);
            }
        }
    }

    public void saveAsItemActionPerformed(ActionEvent e) {
        save(Localizer.localize("UI", "SaveFileAsDialogTitle"), true);
    }

    public void saveItemActionPerformed(ActionEvent e) {
        save(Localizer.localize("UI", "SaveFileDialogTitle"), false);
    }

    public EditorFrame getSelectedFrame() {

        if (getDesktopTabbedPane() == null) {
            return null;
        }

        if (getDesktopTabbedPane().getSelectedFrame() instanceof ErrorLogFrame) {
            return null;
        }

        if (getDesktopTabbedPane().getSelectedFrame() instanceof ReprocessorFrame) {
            return null;
        }

        if (getDesktopTabbedPane().getSelectedFrame() instanceof SourceDataManagerFrame) {
            return null;
        }

        if (getDesktopTabbedPane().getSelectedFrame() instanceof CapturedDataManagerFrame) {
            return null;
        }

        if (getDesktopTabbedPane().getSelectedFrame() instanceof ProcessingQueueManagerFrame) {
            return null;
        }

        return (EditorFrame) getDesktopTabbedPane().getSelectedFrame();
    }

    public DesktopDockingPanel getDesktopDockingPanel() {
        return desktopDockingPanel;
    }

    public JStatusBar getStatusBar() {
        if (getDesktopDockingPanel() == null) {
            return null;
        }
        return getDesktopDockingPanel().getStatusBar();
    }

    public void processingQueueManagerActionPerformed(ActionEvent e) {
        String title = Localizer.localize("UI", "ProcessingQueueWindowTitle");
        openProcessingQueueManager(title);
    }

    public void capturedDataManagerItemActionPerformed(ActionEvent e) {
        String title = Localizer.localize("UI", "CapturedDataWindowTitle");
        openCapturedDataManager(title);
    }

    public PropertiesPanelController getPropertiesPanelController() {
        return desktopDockingPanel.getPropertiesPanelController();
    }

    public RecognitionPanelController getRecognitionPanelController(
        RecognitionPreviewPanel recognitionPreviewPanel) {
        return desktopDockingPanel.getRecognitionPanelController(recognitionPreviewPanel);
    }

    public static XStream getXstream() {
        return xstream;
    }

    public static void setXstream(XStream xstream) {
        Main.xstream = xstream;
    }

    public static XStream createXStream() {
        XStream xs = new XStream(new DomDriver("UTF-8")) {
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
        return xs;

    }

    public void addDesktopPopupComponent(JPanel tabPanel) {
        tabPanel.addMouseListener(new DesktopPaneMouseAdapter());
    }

    public static CachedFontManager getCachedFontManager() {
        return cachedFontManager;
    }

    public static void setCachedFontManager(CachedFontManager cachedFontManager) {
        Main.cachedFontManager = cachedFontManager;
    }

    public void resetRecentFileStack() {
        recentFileStack = PreferencesManager.getRecentFileStack();
        rebuildRecentFileMenu();
    }

    public void resetToolbarTicks() {
        JCheckBoxMenuItem statusBarCheckBoxMenuItem = mainMenu.getStatusBarCheckBoxMenuItem();

        ApplicationStatePreferences applicationState = PreferencesManager.getApplicationState();

        if (applicationState.isStatusBarEnabled()) {
            statusBarCheckBoxMenuItem.setSelected(true);
            Main.getInstance().showStatusBar();
        } else {
            statusBarCheckBoxMenuItem.setSelected(false);
            Main.getInstance().removeStatusBar();
        }
        statusBarCheckBoxMenuItem.updateUI();

        JCheckBoxMenuItem quickLauncherCheckBoxMenuItem =
            mainMenu.getQuickLauncherCheckBoxMenuItem();

        if (applicationState.isQuickLauncherEnabled()) {
            quickLauncherCheckBoxMenuItem.setSelected(true);
            Main.getInstance().showQuickLauncher();
        } else {
            quickLauncherCheckBoxMenuItem.setSelected(false);
            Main.getInstance().removeQuickLauncher();
        }
        quickLauncherCheckBoxMenuItem.updateUI();
    }

    public void removeQuickLauncher() {
        getDesktopDockingPanel().hideDesktopToolbar();
    }

    public void showQuickLauncher() {
        getDesktopDockingPanel().showDesktopToolbar();
    }

    public void removeStatusBar() {
        getDesktopDockingPanel().hideStatusBar();
    }

    public void showStatusBar() {
        getDesktopDockingPanel().showStatusBar();
    }

    public void setDatabaseStatusConnected() {
        if (isDatabaseConnected != true) {
            isDatabaseConnected = true;
            getStatusBar().setDatabaseStatusConnected();
        }
    }

    public void setDatabaseStatusDisconnected(String message) {
        if (isDatabaseConnected != false) {
            isDatabaseConnected = false;
            JStatusBar sb = getStatusBar();
            if (sb != null) {
                sb.setDatabaseStatusDisconnected();
            }
            Misc.showErrorMsg(this, message);
        }
    }

    public JPanel getProcessingQueueManagerFrame() {
        return processingQueueManagerFrame;
    }

    public void setProcessingQueueManagerFrame(JPanel processingQueueManagerFrame) {
        this.processingQueueManagerFrame = processingQueueManagerFrame;
    }

    public SoftwareUpdateManager getSoftwareUpdateManager() {
        return softwareUpdateManager;
    }

    public BlockingGlassPane getBlockingGlassPane() {
        return blockingGlassPane;
    }

    public Timer getDatabaseInformationTimer() {
        return databaseInformationTimer;
    }

    public void setDatabaseInformationTimer(Timer databaseInformationTimer) {
        this.databaseInformationTimer = databaseInformationTimer;
    }

    public File getLaunchFile() {
        String applicationDir = PreferencesManager.getApplicationDir();
        return new File(applicationDir + System.getProperty("file.separator") + "launchfile");
    }

    public File getOpenFileIPC() {
        String applicationDir = PreferencesManager.getApplicationDir();
        return new File(applicationDir + System.getProperty("file.separator") + "openfile.ipc");
    }

    private void startMemoryMapReader() {

        File openFileIPC = getOpenFileIPC();

        try {
            MemoryMapReader mmr = new MemoryMapReader(openFileIPC);
            mmr.start();
        } catch (Exception ex) {
            Misc.printStackTrace(ex);
        } catch (Error er) {
            Misc.printStackTrace(er);
        }

    }

    public void startScansWatcher() {
        Thread scanWatcherThread = new Thread(new Runnable() {
            public void run() {
                String parentPath = PreferencesManager.getScanDirectoryPath();
                Path toWatch = Paths.get(parentPath);
                WatchService myWatcher;
                try {
                    myWatcher = toWatch.getFileSystem().newWatchService();
                    scansWatcher = new ScansWatcher(myWatcher, true);
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

    public ScansWatcher getScansWatcher() {
        return this.scansWatcher;
    }

    public void scanFormsItemActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int modifiers = e.getModifiers();
                if (!Main.LINUX && checkMod(modifiers, ActionEvent.SHIFT_MASK) && checkMod(
                    modifiers, ActionEvent.ALT_MASK)) {
                    selectDefaultOrSaneScanning();
                } else {
                    launchFormReturnScanner();
                }
            }
        });
    }

    protected void selectDefaultOrSaneScanning() {
        ApplicationStatePreferences applicationState = PreferencesManager.getApplicationState();
        applicationState.setUsingSaneClient(!(applicationState.isUsingSaneClient()));
        try {
            PreferencesManager.savePreferences(getXstream());
        } catch (IOException e) {
            Misc.printStackTrace(e);
        }
        launchFormReturnScanner();
    }

    private boolean checkMod(int modifiers, int mask) {
        return ((modifiers & mask) == mask);
    }

    public void launchFormReturnScanner() {

        File lockFile =
            new File(PreferencesManager.getScanDirectoryPath() + File.separator + ".preview_lock");

        if (lockFile.exists()) {
            FileUtils.delete(lockFile);
        }

        if (scl == null) {
            scl = new ScanClientLauncher();
        } else {
            if (!(scl.isRunning())) {
                scl = new ScanClientLauncher();
            }
        }
    }

    public void closeSelectedWindow() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JPanel selectedFrame = getDesktopTabbedPane().getSelectedFrame();
                if (selectedFrame != null) {
                    closeTab(selectedFrame);
                }
            }
        });
    }

    public void closeAllWindows() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                DesktopTabbedPane dtp = getDesktopTabbedPane();
                JPanel[] frames = dtp.getAllFrames();
                for (JPanel frame : frames) {
                    closeTab(frame);
                }
            }
        });
    }

    public void uploadImageItemActionPerformed(ActionEvent e) {
        File file;
        try {
            file = Misc.getUploadImageFile();
            if (file == null) {
                return;
            }
            Misc.uploadImage(getJPAConfiguration(), file, new TemplateFormPageID(), this);
        } catch (IOException ioex) {
            Misc.showErrorMsg(this, ioex.getLocalizedMessage());
            Misc.printStackTrace(ioex);
        }
    }

    public void uploadImageFolderItemActionPerformed(ActionEvent e) {
        File file;
        try {
            file = Misc.getUploadImageFolder();
            if (file == null) {
                return;
            }
            Misc.uploadImageFolder(getJPAConfiguration(), file, new TemplateFormPageID(), this);
        } catch (IOException ioex) {
            Misc.showErrorMsg(this, ioex.getLocalizedMessage());
            Misc.printStackTrace(ioex);
        }
    }

    public ScanClientLauncher getScanClientLauncher() {
        return this.scl;
    }

    public ZoomSettings getReprocessorZoom() {
        return this.reprocessorZoomSettings;
    }

}
