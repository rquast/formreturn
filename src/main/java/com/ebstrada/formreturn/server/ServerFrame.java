package com.ebstrada.formreturn.server;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.border.*;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

import com.ebstrada.formreturn.server.Main;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.ui.dialog.AboutDialog;
import com.ebstrada.formreturn.server.ServerGUI;
import com.ebstrada.formreturn.manager.gef.font.FontLocalesImpl;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.JPAConfiguration;
import com.ebstrada.formreturn.manager.persistence.jpa.IncomingImage;
import com.ebstrada.formreturn.manager.util.AvailableLanguages;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.OSXAdapter;
import com.ebstrada.formreturn.manager.util.TemplateFormPageID;
import com.ebstrada.formreturn.manager.util.image.ImageUtil;
import com.ebstrada.formreturn.manager.util.preferences.persistence.FormProcessorPreferences;
import com.ebstrada.formreturn.server.component.*;
import com.ebstrada.formreturn.server.derby.DatabaseServer;
import com.ebstrada.formreturn.server.derby.dialog.AddNewDatabaseDialog;
import com.ebstrada.formreturn.server.dialog.BackupStatusDialog;
import com.ebstrada.formreturn.server.preferences.ServerPreferencesManager;
import com.ebstrada.formreturn.server.preferences.persistence.DatabaseServerPreferences;

public class ServerFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private Timer databaseStatusTimer;
    private Timer formProcessorStatusTimer;
    private AvailableLanguages availableLanguages;

    private static final Logger logger = Logger.getLogger(ServerFrame.class);

    public ServerFrame() {
        this.taskSchedulerPanel = new TaskSchedulerPanel();
    }

    public void startup() {
        initComponents();
        setTitle(String.format(Localizer.localize("Server", "ServerFrameTitle"), Main.VERSION));
        serverTabbedPane.setTitleAt(0, Localizer.localize("Server", "ServerFrameDatabaseTabTitle"));
        serverTabbedPane
            .setTitleAt(1, Localizer.localize("Server", "ServerFrameFormProcessorTabTitle"));
        serverTabbedPane
            .setTitleAt(2, Localizer.localize("Server", "ServerFrameTaskSchedulerTabTitle"));
        updateLocalDatabases();
        restorePreferences();
        initStatusUpdate();
    }

    public TaskSchedulerPanel getTaskSchedulerPanel() {
        return taskSchedulerPanel;
    }

    public boolean closeApplication() {
        return stopServer();
    }

    public void about() {
        AboutDialog aboutDialog = new AboutDialog(this);
        aboutDialog.setVisible(true);
    }

    public void macOSXRegistration() {
        if (Main.MAC_OS_X) {
            try {
                OSXAdapter.setQuitHandler(this,
                    getClass().getDeclaredMethod("closeApplication", (Class[]) null));
                OSXAdapter
                    .setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[]) null));
            } catch (NoClassDefFoundError e) {
                logger.warn(e.getLocalizedMessage(), e);
            } catch (Exception e) {
                logger.warn(e.getLocalizedMessage(), e);
            }
        }
    }

    public Timer getDatabaseStatusTimer() {
        return this.databaseStatusTimer;
    }

    public Timer getFormProcessorStatusTimer() {
        return this.formProcessorStatusTimer;
    }

    private void initStatusUpdate() {

        databaseStatusTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                databaseStatusComponent.setText(ServerGUI.getInstance().getDatabaseStatus());
            }
        });

        formProcessorStatusTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                formProcessorStatusComponent
                    .setText(ServerGUI.getInstance().getFormProcessorStatus());
            }
        });

        databaseStatusTimer.start();
        formProcessorStatusTimer.start();

    }

    private void restorePreferences() {

        DatabaseServerPreferences dsp = ServerPreferencesManager.getDatabaseServer();
        listeningAddressTextField.setText(dsp.getListeningAddresses());
        listeningPortTextField.setText(dsp.getPortNumber() + "");

        FormProcessorPreferences fpp = ServerPreferencesManager.getFormProcessorPreferences();
        formProcessorStartupCheckBox.setSelected(fpp.isRunWhenServerStarts());

        // restore the language combo box.
        this.availableLanguages = new AvailableLanguages();
        languageComboBox.setModel(availableLanguages.getLanguageComboBoxModel());
        String locale = ServerPreferencesManager.getLocale();
        int i = 0;
        for (FontLocalesImpl fontLocale : FontLocalesImpl.values()) {
            if (fontLocale.name().equals(locale)) {
                languageComboBox.setSelectedIndex(i);
                break;
            }
            ++i;
        }

    }

    private void saveDatabasePreferences() {
        DatabaseServerPreferences dsp = ServerPreferencesManager.getDatabaseServer();
        dsp.setListeningAddresses(listeningAddressTextField.getText().trim());

        int portNumber = Misc.parseIntegerString(listeningPortTextField.getText());
        if (portNumber < 1 || portNumber >= 65535) {
            portNumber = 1527;
            listeningPortTextField.setText(portNumber + "");
        }

        dsp.setPortNumber(portNumber);
        try {
            ServerPreferencesManager.savePreferences(ServerGUI.getXstream());
            Misc.showSuccessMsg(this,
                Localizer.localize("Server", "ServerFrameNetworkDetailsSavedMessage"));
        } catch (IOException e) {
            Misc.showExceptionMsg(this, e);
        }
    }

    public void updateLocalDatabases() {
        localDatabaseTable
            .setModel(ServerPreferencesManager.getDatabaseTableModel(ServerGUI.getXstream()));
    }

    private void closeServerButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                stopServer();
            }
        });
    }

    private boolean stopServer() {
        return ServerGUI.getInstance().stopServer(this);
    }

    private void thisWindowClosing(WindowEvent e) {
        stopServer();
    }

    private void manageUsersButtonActionPerformed(ActionEvent e) {
        if (localDatabaseTable.getSelectedRow() != -1) {
            int selectedRow = localDatabaseTable.getSelectedRow();
            String databaseName = (String) localDatabaseTable.getValueAt(selectedRow, 0);
            ServerGUI.getInstance().showManageUsersDialog(databaseName, this);
        }
    }

    private void removeDatabaseButtonActionPerformed(ActionEvent e) {
        if (localDatabaseTable.getSelectedRow() != -1) {
            int selectedRow = localDatabaseTable.getSelectedRow();
            String databaseName = (String) localDatabaseTable.getValueAt(selectedRow, 0);
            Object[] options =
                {Localizer.localize("Server", "Yes"), Localizer.localize("Server", "No")};
            String msg = String
                .format(Localizer.localize("Server", "ServerFrameConfirmRemoveDatabaseMessage"),
                    databaseName);
            int result = JOptionPane.showOptionDialog(this, msg,
                Localizer.localize("Server", "ServerFrameConfirmRemoveDatabaseTitle"),
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if (result != 1) {
                DatabaseServer databaseServer = ServerGUI.getInstance().getDatabaseServer();
                databaseServer.removeDatabase(databaseName);
                updateLocalDatabases();
            }
        }
    }

    private void createDatabaseButtonActionPerformed(ActionEvent e) {
        AddNewDatabaseDialog andd = new AddNewDatabaseDialog(this);
        andd.setVisible(true);
    }

    private void backupDatabaseButtonActionPerformed(ActionEvent e) {
        if (localDatabaseTable.getSelectedRow() != -1) {
            File backupDirectory = null;

            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser
                .setDialogTitle(Localizer.localize("Server", "BackupDatabaseDirectoryDialogTitle"));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.rescanCurrentDirectory();

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                backupDirectory = chooser.getSelectedFile();
                int selectedRow = localDatabaseTable.getSelectedRow();
                String databaseName = (String) localDatabaseTable.getValueAt(selectedRow, 0);
                DatabaseServer databaseServer = ServerGUI.getInstance().getDatabaseServer();
                performBackup(databaseServer, databaseName, backupDirectory);
            }
        }
    }

    private void performBackup(final DatabaseServer databaseServer, final String databaseName,
        final File backupDirectory) {
        final BackupStatusDialog backupStatusDialog =
            new BackupStatusDialog(getRootPane().getTopLevelAncestor());

        class BackupRunner implements Runnable {
            public void run() {
                try {
                    databaseServer
                        .backupDatabase(backupStatusDialog, databaseName, backupDirectory);
                } catch (InterruptedException ie) {
                    // do nothing.
                } catch (Exception e) {
                    logger.error(e.getLocalizedMessage(), e);
                    if (ServerGUI.getInstance().getServerFrame() != null) {
                        Misc.showExceptionMsg(getRootPane().getTopLevelAncestor(), e);
                    }
                } finally {
                    backupStatusDialog.dispose();
                }
            }
        }
        ;

        BackupRunner backupRunner = new BackupRunner();
        Thread thread = new Thread(backupRunner);
        thread.start();

        backupStatusDialog.setModal(true);
        backupStatusDialog.setVisible(true);

        if (backupStatusDialog != null) {
            backupStatusDialog.dispose();
        }
    }

    private void restartServerButtonActionPerformed(ActionEvent e) {
        Object[] options =
            {Localizer.localize("Server", "Yes"), Localizer.localize("Server", "No")};
        String msg = Localizer.localize("Server", "ServerFrameConfirmRestartServerMessage");
        int result = JOptionPane.showOptionDialog(this, msg,
            Localizer.localize("Server", "ServerFrameConfirmRestartServerTitle"),
            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        if (result != 1) {
            try {
                ServerGUI.getInstance().restartServer();
            } catch (SchedulerException se) {
                logger.warn(se.getLocalizedMessage(), se);
            } finally {
                updateLocalDatabases();
            }

        }
    }

    private void defaultPortButtonActionPerformed(ActionEvent e) {
        listeningPortTextField.setText(1527 + "");
    }

    private void saveNetworkDetailsActionPerformed(ActionEvent e) {
        saveDatabasePreferences();
    }

    public void uploadImageFolderButtonActionPerformed(ActionEvent e) {
        File file;
        try {
            file = Misc.getUploadImageFolder();
            if (file == null) {
                return;
            }
            Misc.uploadImageFolder(Main.getInstance().getJPAConfiguration(), file,
                new TemplateFormPageID(), this);
        } catch (IOException ioex) {
            Misc.showErrorMsg(this, ioex.getLocalizedMessage());
            Misc.printStackTrace(ioex);
        }
    }

    public void uploadImageButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                uploadImages();
            }
        });
    }

    private void uploadImages() {
        JPAConfiguration jpaConfiguration = Main.getInstance().getJPAConfiguration();
        File file;
        try {
            file = Misc.getUploadImageFile();
            if (file == null) {
                return;
            }
            Misc.uploadImage(jpaConfiguration, file, new TemplateFormPageID(), this);
        } catch (IOException e) {
            Misc.showErrorMsg(this, e.getLocalizedMessage());
            Misc.printStackTrace(e);
        }
    }

    private void setAsServerDefaultButtonActionPerformed(ActionEvent e) {
        int selectedRow = localDatabaseTable.getSelectedRow();
        if (selectedRow != -1) {
            String databaseName = (String) localDatabaseTable.getValueAt(selectedRow, 0);
            ServerPreferencesManager.setFormProcessingDatabaseName(databaseName);
            try {
                ServerPreferencesManager.savePreferences(ServerGUI.getXstream());
            } catch (IOException e1) {
            }
            ServerGUI.getInstance().restartFormProcessor();

            updateLocalDatabases();
        }
    }

    private void startFormProcessorButtonActionPerformed(ActionEvent e) {
        ServerGUI.getInstance().startFormProcessor(true);
    }

    private void stopFormProcessorButtonActionPerformed(ActionEvent e) {
        ServerGUI.getInstance().stopFormProcessor(true);
    }

    private void restartFormProcessorButtonActionPerformed(ActionEvent e) {
        ServerGUI.getInstance().restartFormProcessor(true);
    }

    private void formProcessorStartupCheckBoxActionPerformed(ActionEvent e) {

        FormProcessorPreferences fpp = ServerPreferencesManager.getFormProcessorPreferences();
        fpp.setRunWhenServerStarts(formProcessorStartupCheckBox.isSelected());

        try {
            ServerPreferencesManager.savePreferences(ServerGUI.getXstream());
        } catch (Exception ex) {
            Misc.showExceptionMsg(this, ex);
        }

    }

    public ProcessingQueueStatsPanel getProcessingQueueStatsPanel() {
        return processingQueueStatsPanel1;
    }

    private void changeLanguageButtonActionPerformed(ActionEvent e) {

        int selectedIndex = languageComboBox.getSelectedIndex();
        int i = 0;
        for (FontLocalesImpl fontLocale : FontLocalesImpl.values()) {
            if (i == selectedIndex) {
                // set the value of the fontLocale as the locale
                ServerPreferencesManager.setLocale(fontLocale.name());
                break;
            }
            ++i;
        }
        try {
            ServerPreferencesManager.savePreferences(ServerGUI.getXstream());
            Misc.showSuccessMsg(this, Localizer.localize("Server", "ChangeLanguageSuccessMessage"));
        } catch (IOException ioex) {
            logger.error(ioex.getLocalizedMessage(), ioex);
            Misc.showErrorMsg(this, Localizer.localize("Server", "ChangeLanguageFailureMessage"));
        }

    }

    private void defaultAddressesButtonActionPerformed(ActionEvent e) {
        this.listeningAddressTextField.setText("127.0.0.1");
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        panel1 = new JPanel();
        panel8 = new JPanel();
        processingQueueStatusPanel = new JPanel();
        processingQueueStatsPanel1 = new ProcessingQueueStatsPanel();
        label7 = new JLabel();
        serverTabbedPane = new JTabbedPane();
        databasePanel = new JPanel();
        databaseStatusPanel = new JPanel();
        databaseStatusComponent = new JLabel();
        networkDetailsPanel = new JPanel();
        panel4 = new JPanel();
        listeningAddressLabel = new JLabel();
        listeningAddressTextField = new JTextField();
        defaultAddressesButton = new JButton();
        databasePortLabel = new JLabel();
        listeningPortTextField = new JTextField();
        defaultPortButton = new JButton();
        panel11 = new JPanel();
        saveNetworkDetailsButton = new JButton();
        localDatabasesPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        localDatabaseTable = new JTable();
        panel5 = new JPanel();
        removeDatabaseButton = new JButton();
        createDatabaseButton = new JButton();
        backupDatabaseButton = new JButton();
        manageUsersButton = new JButton();
        setAsServerDefaultButton = new JButton();
        formProcessorPanel = new JPanel();
        formProcessorStatusPanel = new JPanel();
        formProcessorStatusComponent = new JLabel();
        panel14 = new JPanel();
        startFormProcessorButton = new JButton();
        stopFormProcessorButton = new JButton();
        restartFormProcessorButton = new JButton();
        formProcessorStartupCheckBox = new JCheckBox();
        manuallyLoadImagesPanel = new JPanel();
        panel9 = new JPanel();
        uploadImageLabel = new JLabel();
        uploadImageFolderButton = new JButton();
        uploadFolderLabel = new JLabel();
        uploadImageButton = new JButton();
        taskSchedulerPanel = taskSchedulerPanel;
        panel2 = new JPanel();
        panel7 = new JPanel();
        helpLabel = new JHelpLabel();
        languageComboBox = new JComboBox();
        changeLanguageButton = new JButton();
        restartServerButton = new JButton();
        closeServerButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/formreturn_server_256x256.png")).getImage());
        setName("serverFrame");
        setResizable(false);
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                thisWindowClosing(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setBorder(null);

            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {110, 0, 40, 0};
            ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};

            //======== panel8 ========
            {
                panel8.setOpaque(false);
                panel8.setBorder(new EmptyBorder(2, 2, 0, 5));
                panel8.setLayout(new BorderLayout());

                //======== processingQueueStatusPanel ========
                {
                    processingQueueStatusPanel.setOpaque(false);
                    processingQueueStatusPanel.setBorder(new CompoundBorder(
                        new TitledBorder("Queue Information"),
                        new EmptyBorder(2, 2, 2, 2)));
                    processingQueueStatusPanel.setFont(UIManager.getFont("TitledBorder.font"));
                    processingQueueStatusPanel.setLayout(new BoxLayout(processingQueueStatusPanel, BoxLayout.X_AXIS));
                    processingQueueStatusPanel.setBorder(new CompoundBorder(
                        new TitledBorder(null, Localizer.localize("Server", "ServerFrameProcessingQueueStatusBorderTitle"), TitledBorder.LEADING, TitledBorder.TOP),
                        new EmptyBorder(5, 5, 5, 5)));
                    processingQueueStatusPanel.add(processingQueueStatsPanel1);
                }
                panel8.add(processingQueueStatusPanel, BorderLayout.CENTER);
            }
            panel1.add(panel8, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

            //======== serverTabbedPane ========
            {
                serverTabbedPane.setFont(UIManager.getFont("TabbedPane.font"));
                serverTabbedPane.setBorder(new EmptyBorder(5, 5, 5, 5));

                //======== databasePanel ========
                {
                    databasePanel.setOpaque(false);
                    databasePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                    databasePanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)databasePanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)databasePanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                    ((GridBagLayout)databasePanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)databasePanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};

                    //======== databaseStatusPanel ========
                    {
                        databaseStatusPanel.setOpaque(false);
                        databaseStatusPanel.setFont(UIManager.getFont("TitledBorder.font"));
                        databaseStatusPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)databaseStatusPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)databaseStatusPanel.getLayout()).rowHeights = new int[] {40, 0};
                        ((GridBagLayout)databaseStatusPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)databaseStatusPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                        databaseStatusPanel.setBorder(new CompoundBorder(
                            new TitledBorder(null, Localizer.localize("Server", "ServerFrameDatabaseStatusBorderTitle"), TitledBorder.LEADING, TitledBorder.TOP),
                            new EmptyBorder(5, 5, 5, 5)));

                        //---- databaseStatusComponent ----
                        databaseStatusComponent.setFont(UIManager.getFont("Label.font"));
                        databaseStatusPanel.add(databaseStatusComponent, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    databasePanel.add(databaseStatusPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== networkDetailsPanel ========
                    {
                        networkDetailsPanel.setOpaque(false);
                        networkDetailsPanel.setFont(UIManager.getFont("TitledBorder.font"));
                        networkDetailsPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)networkDetailsPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
                        ((GridBagLayout)networkDetailsPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)networkDetailsPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                        ((GridBagLayout)networkDetailsPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                        networkDetailsPanel.setBorder(new TitledBorder(null, Localizer.localize("Server", "ServerFrameNetworkDetailsBorderTitle"), TitledBorder.LEADING, TitledBorder.TOP));

                        //======== panel4 ========
                        {
                            panel4.setOpaque(false);
                            panel4.setBorder(new EmptyBorder(5, 5, 5, 5));
                            panel4.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel4.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                            ((GridBagLayout)panel4.getLayout()).rowHeights = new int[] {0, 0, 0};
                            ((GridBagLayout)panel4.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};
                            ((GridBagLayout)panel4.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};

                            //---- listeningAddressLabel ----
                            listeningAddressLabel.setFont(UIManager.getFont("Label.font"));
                            listeningAddressLabel.setText(Localizer.localize("Server", "ServerFrameDatabaseAddressesLabel"));
                            panel4.add(listeningAddressLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.EAST, GridBagConstraints.NONE,
                                new Insets(0, 0, 5, 5), 0, 0));

                            //---- listeningAddressTextField ----
                            listeningAddressTextField.setText("127.0.0.1");
                            panel4.add(listeningAddressTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 5, 5), 0, 0));

                            //---- defaultAddressesButton ----
                            defaultAddressesButton.setFont(UIManager.getFont("Button.font"));
                            defaultAddressesButton.setFocusPainted(false);
                            defaultAddressesButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
                            defaultAddressesButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    defaultAddressesButtonActionPerformed(e);
                                }
                            });
                            defaultAddressesButton.setText(Localizer.localize("Server", "ServerFrameDefaultAddressesButtonText"));
                            panel4.add(defaultAddressesButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.WEST, GridBagConstraints.NONE,
                                new Insets(0, 0, 5, 0), 0, 0));

                            //---- databasePortLabel ----
                            databasePortLabel.setFont(UIManager.getFont("Label.font"));
                            databasePortLabel.setText(Localizer.localize("Server", "ServerFrameDatabasePortLabel"));
                            panel4.add(databasePortLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.EAST, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- listeningPortTextField ----
                            listeningPortTextField.setText("1527");
                            listeningPortTextField.setFont(UIManager.getFont("TextField.font"));
                            listeningPortTextField.setMinimumSize(new Dimension(60, 28));
                            listeningPortTextField.setMaximumSize(new Dimension(60, 2147483647));
                            panel4.add(listeningPortTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- defaultPortButton ----
                            defaultPortButton.setFont(UIManager.getFont("Button.font"));
                            defaultPortButton.setFocusPainted(false);
                            defaultPortButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
                            defaultPortButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    defaultPortButtonActionPerformed(e);
                                }
                            });
                            defaultPortButton.setText(Localizer.localize("Server", "ServerFrameDefaultPortButtonText"));
                            panel4.add(defaultPortButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.WEST, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        networkDetailsPanel.add(panel4, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //======== panel11 ========
                        {
                            panel11.setOpaque(false);
                            panel11.setBorder(new EmptyBorder(5, 5, 5, 5));
                            panel11.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel11.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout)panel11.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)panel11.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
                            ((GridBagLayout)panel11.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                            //---- saveNetworkDetailsButton ----
                            saveNetworkDetailsButton.setFont(UIManager.getFont("Button.font"));
                            saveNetworkDetailsButton.setFocusPainted(false);
                            saveNetworkDetailsButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
                            saveNetworkDetailsButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    saveNetworkDetailsActionPerformed(e);
                                }
                            });
                            saveNetworkDetailsButton.setText(Localizer.localize("Server", "ServerFrameSaveNetworkSettingsButtonText"));
                            panel11.add(saveNetworkDetailsButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        networkDetailsPanel.add(panel11, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    databasePanel.add(networkDetailsPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== localDatabasesPanel ========
                    {
                        localDatabasesPanel.setOpaque(false);
                        localDatabasesPanel.setFont(UIManager.getFont("TitledBorder.font"));
                        localDatabasesPanel.setLayout(new BorderLayout());
                        localDatabasesPanel.setBorder(new TitledBorder(null, Localizer.localize("Server", "ServerFrameLocalDatabasesBorderTitle"), TitledBorder.LEADING, TitledBorder.TOP));

                        //======== scrollPane1 ========
                        {

                            //---- localDatabaseTable ----
                            localDatabaseTable.setGridColor(Color.white);
                            localDatabaseTable.setShowHorizontalLines(false);
                            localDatabaseTable.setShowVerticalLines(false);
                            localDatabaseTable.setFont(UIManager.getFont("Table.font"));
                            localDatabaseTable.setShowGrid(false);
                            localDatabaseTable.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));
                            scrollPane1.setViewportView(localDatabaseTable);
                        }
                        localDatabasesPanel.add(scrollPane1, BorderLayout.CENTER);

                        //======== panel5 ========
                        {
                            panel5.setOpaque(false);
                            panel5.setBorder(new EmptyBorder(5, 5, 3, 5));
                            panel5.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel5.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0};
                            ((GridBagLayout)panel5.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)panel5.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                            ((GridBagLayout)panel5.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                            //---- removeDatabaseButton ----
                            removeDatabaseButton.setFont(UIManager.getFont("Button.font"));
                            removeDatabaseButton.setFocusPainted(false);
                            removeDatabaseButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/delete.png")));
                            removeDatabaseButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    removeDatabaseButtonActionPerformed(e);
                                }
                            });
                            removeDatabaseButton.setText(Localizer.localize("Server", "ServerFrameRemoveDatabaseButtonText"));
                            panel5.add(removeDatabaseButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- createDatabaseButton ----
                            createDatabaseButton.setFont(UIManager.getFont("Button.font"));
                            createDatabaseButton.setFocusPainted(false);
                            createDatabaseButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/add.png")));
                            createDatabaseButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    createDatabaseButtonActionPerformed(e);
                                }
                            });
                            createDatabaseButton.setText(Localizer.localize("Server", "ServerFrameCreateDatabaseButtonText"));
                            panel5.add(createDatabaseButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- backupDatabaseButton ----
                            backupDatabaseButton.setFont(UIManager.getFont("Button.font"));
                            backupDatabaseButton.setFocusPainted(false);
                            backupDatabaseButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk_multiple.png")));
                            backupDatabaseButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    backupDatabaseButtonActionPerformed(e);
                                }
                            });
                            backupDatabaseButton.setText(Localizer.localize("Server", "ServerFrameBackupDatabaseButtonText"));
                            panel5.add(backupDatabaseButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- manageUsersButton ----
                            manageUsersButton.setFont(UIManager.getFont("Button.font"));
                            manageUsersButton.setFocusPainted(false);
                            manageUsersButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/monitor.png")));
                            manageUsersButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    manageUsersButtonActionPerformed(e);
                                }
                            });
                            manageUsersButton.setText(Localizer.localize("Server", "ServerFrameManageUsersButtonText"));
                            panel5.add(manageUsersButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- setAsServerDefaultButton ----
                            setAsServerDefaultButton.setFocusPainted(false);
                            setAsServerDefaultButton.setFont(UIManager.getFont("Button.font"));
                            setAsServerDefaultButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/accept.png")));
                            setAsServerDefaultButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    setAsServerDefaultButtonActionPerformed(e);
                                }
                            });
                            setAsServerDefaultButton.setText(Localizer.localize("Server", "ServerFrameSetAsServerDefaultButtonText"));
                            panel5.add(setAsServerDefaultButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));
                        }
                        localDatabasesPanel.add(panel5, BorderLayout.SOUTH);
                    }
                    databasePanel.add(localDatabasesPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                serverTabbedPane.addTab("Database", databasePanel);

                //======== formProcessorPanel ========
                {
                    formProcessorPanel.setOpaque(false);
                    formProcessorPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                    formProcessorPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)formProcessorPanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)formProcessorPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                    ((GridBagLayout)formProcessorPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)formProcessorPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};

                    //======== formProcessorStatusPanel ========
                    {
                        formProcessorStatusPanel.setOpaque(false);
                        formProcessorStatusPanel.setFont(UIManager.getFont("TitledBorder.font"));
                        formProcessorStatusPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)formProcessorStatusPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)formProcessorStatusPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                        ((GridBagLayout)formProcessorStatusPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)formProcessorStatusPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};
                        formProcessorStatusPanel.setBorder(new CompoundBorder(
                            new TitledBorder(null, Localizer.localize("Server", "ServerFrameFormProcessorStatusBorderTitle"), TitledBorder.LEADING, TitledBorder.TOP),
                            new EmptyBorder(5, 5, 5, 5)));

                        //---- formProcessorStatusComponent ----
                        formProcessorStatusComponent.setFont(UIManager.getFont("Label.font"));
                        formProcessorStatusPanel.add(formProcessorStatusComponent, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== panel14 ========
                        {
                            panel14.setOpaque(false);
                            panel14.setBorder(new CompoundBorder(
                                new MatteBorder(1, 0, 0, 0, Color.lightGray),
                                new EmptyBorder(4, 0, 0, 0)));
                            panel14.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel14.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0};
                            ((GridBagLayout)panel14.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)panel14.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0, 0.0, 1.0E-4};
                            ((GridBagLayout)panel14.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                            //---- startFormProcessorButton ----
                            startFormProcessorButton.setFocusPainted(false);
                            startFormProcessorButton.setFont(UIManager.getFont("Button.font"));
                            startFormProcessorButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/time_go.png")));
                            startFormProcessorButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    startFormProcessorButtonActionPerformed(e);
                                }
                            });
                            startFormProcessorButton.setText(Localizer.localize("Server", "ServerFrameStartFormProcessorButtonText"));
                            panel14.add(startFormProcessorButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- stopFormProcessorButton ----
                            stopFormProcessorButton.setFocusPainted(false);
                            stopFormProcessorButton.setFont(UIManager.getFont("Button.font"));
                            stopFormProcessorButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/stop.png")));
                            stopFormProcessorButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    stopFormProcessorButtonActionPerformed(e);
                                }
                            });
                            stopFormProcessorButton.setText(Localizer.localize("Server", "ServerFrameStopFormProcessorButtonText"));
                            panel14.add(stopFormProcessorButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- restartFormProcessorButton ----
                            restartFormProcessorButton.setFocusPainted(false);
                            restartFormProcessorButton.setFont(UIManager.getFont("Button.font"));
                            restartFormProcessorButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
                            restartFormProcessorButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    restartFormProcessorButtonActionPerformed(e);
                                }
                            });
                            restartFormProcessorButton.setText(Localizer.localize("Server", "ServerFrameRestartFormProcessorButtonText"));
                            panel14.add(restartFormProcessorButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- formProcessorStartupCheckBox ----
                            formProcessorStartupCheckBox.setFocusPainted(false);
                            formProcessorStartupCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            formProcessorStartupCheckBox.setOpaque(false);
                            formProcessorStartupCheckBox.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    formProcessorStartupCheckBoxActionPerformed(e);
                                }
                            });
                            formProcessorStartupCheckBox.setText(Localizer.localize("Server", "ServerFrameFormProcessorStartupCheckBox"));
                            panel14.add(formProcessorStartupCheckBox, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        formProcessorStatusPanel.add(panel14, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    formProcessorPanel.add(formProcessorStatusPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== manuallyLoadImagesPanel ========
                    {
                        manuallyLoadImagesPanel.setOpaque(false);
                        manuallyLoadImagesPanel.setFont(UIManager.getFont("TitledBorder.font"));
                        manuallyLoadImagesPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)manuallyLoadImagesPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)manuallyLoadImagesPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)manuallyLoadImagesPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)manuallyLoadImagesPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};
                        manuallyLoadImagesPanel.setBorder(new TitledBorder(null, Localizer.localize("Server", "ServerFrameManuallyLoadImagesBorderTitle"), TitledBorder.LEADING, TitledBorder.TOP));

                        //======== panel9 ========
                        {
                            panel9.setOpaque(false);
                            panel9.setBorder(new EmptyBorder(5, 5, 5, 5));
                            panel9.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel9.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                            ((GridBagLayout)panel9.getLayout()).rowHeights = new int[] {0, 0, 0};
                            ((GridBagLayout)panel9.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                            ((GridBagLayout)panel9.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

                            //---- uploadImageLabel ----
                            uploadImageLabel.setFont(UIManager.getFont("Label.font"));
                            uploadImageLabel.setText(Localizer.localize("Server", "ServerFrameUploadImageLabel"));
                            panel9.add(uploadImageLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                                new Insets(0, 0, 5, 5), 0, 0));

                            //---- uploadImageFolderButton ----
                            uploadImageFolderButton.setFont(UIManager.getFont("Button.font"));
                            uploadImageFolderButton.setFocusPainted(false);
                            uploadImageFolderButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/preview/image.png")));
                            uploadImageFolderButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    uploadImageButtonActionPerformed(e);
                                }
                            });
                            uploadImageFolderButton.setText(Localizer.localize("Server", "ServerFrameUploadImageFolderButtonText"));
                            panel9.add(uploadImageFolderButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 5), 0, 0));

                            //---- uploadFolderLabel ----
                            uploadFolderLabel.setFont(UIManager.getFont("Label.font"));
                            uploadFolderLabel.setText(Localizer.localize("Server", "ServerFrameUploadFolderLabel"));
                            panel9.add(uploadFolderLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- uploadImageButton ----
                            uploadImageButton.setFont(UIManager.getFont("Button.font"));
                            uploadImageButton.setFocusPainted(false);
                            uploadImageButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/folder_image.png")));
                            uploadImageButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    uploadImageFolderButtonActionPerformed(e);
                                }
                            });
                            uploadImageButton.setText(Localizer.localize("Server", "ServerFrameUploadImageButtonText"));
                            panel9.add(uploadImageButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));
                        }
                        manuallyLoadImagesPanel.add(panel9, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    formProcessorPanel.add(manuallyLoadImagesPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));
                }
                serverTabbedPane.addTab("Form Processor", formProcessorPanel);
                serverTabbedPane.addTab("Task Scheduler", taskSchedulerPanel);
            }
            panel1.add(serverTabbedPane, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

            //======== panel2 ========
            {
                panel2.setOpaque(false);
                panel2.setLayout(new GridBagLayout());
                ((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //======== panel7 ========
                {
                    panel7.setOpaque(false);
                    panel7.setBorder(new EmptyBorder(5, 12, 5, 12));
                    panel7.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel7.getLayout()).columnWidths = new int[] {0, 35, 0, 0, 0, 0, 0, 0};
                    ((GridBagLayout)panel7.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)panel7.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout)panel7.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                    //---- helpLabel ----
                    helpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                    helpLabel.setHelpGUID("server-database-settings");
                    helpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                    panel7.add(helpLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- languageComboBox ----
                    languageComboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxxxxx");
                    languageComboBox.setFont(UIManager.getFont("ComboBox.font"));
                    panel7.add(languageComboBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- changeLanguageButton ----
                    changeLanguageButton.setText("Change Language");
                    changeLanguageButton.setFont(UIManager.getFont("Button.font"));
                    changeLanguageButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/world.png")));
                    changeLanguageButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            changeLanguageButtonActionPerformed(e);
                        }
                    });
                    changeLanguageButton.setText(Localizer.localize("Server", "ChangeLanguageButtonText"));
                    panel7.add(changeLanguageButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- restartServerButton ----
                    restartServerButton.setFocusPainted(false);
                    restartServerButton.setFont(UIManager.getFont("Button.font"));
                    restartServerButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
                    restartServerButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            restartServerButtonActionPerformed(e);
                        }
                    });
                    restartServerButton.setText(Localizer.localize("Server", "ServerFrameRestartButtonText"));
                    panel7.add(restartServerButton, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- closeServerButton ----
                    closeServerButton.setFocusPainted(false);
                    closeServerButton.setFont(UIManager.getFont("Button.font"));
                    closeServerButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/door_open.png")));
                    closeServerButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            closeServerButtonActionPerformed(e);
                        }
                    });
                    closeServerButton.setText(Localizer.localize("Server", "ServerFrameShutdownButtonText"));
                    panel7.add(closeServerButton, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                panel2.add(panel7, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            panel1.add(panel2, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1, BorderLayout.CENTER);
        setSize(950, 590);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents

        macOSXRegistration();
        setLocationByPlatform(true);

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel panel1;
    private JPanel panel8;
    private JPanel processingQueueStatusPanel;
    private ProcessingQueueStatsPanel processingQueueStatsPanel1;
    private JLabel label7;
    private JTabbedPane serverTabbedPane;
    private JPanel databasePanel;
    private JPanel databaseStatusPanel;
    private JLabel databaseStatusComponent;
    private JPanel networkDetailsPanel;
    private JPanel panel4;
    private JLabel listeningAddressLabel;
    private JTextField listeningAddressTextField;
    private JButton defaultAddressesButton;
    private JLabel databasePortLabel;
    private JTextField listeningPortTextField;
    private JButton defaultPortButton;
    private JPanel panel11;
    private JButton saveNetworkDetailsButton;
    private JPanel localDatabasesPanel;
    private JScrollPane scrollPane1;
    private JTable localDatabaseTable;
    private JPanel panel5;
    private JButton removeDatabaseButton;
    private JButton createDatabaseButton;
    private JButton backupDatabaseButton;
    private JButton manageUsersButton;
    private JButton setAsServerDefaultButton;
    private JPanel formProcessorPanel;
    private JPanel formProcessorStatusPanel;
    private JLabel formProcessorStatusComponent;
    private JPanel panel14;
    private JButton startFormProcessorButton;
    private JButton stopFormProcessorButton;
    private JButton restartFormProcessorButton;
    private JCheckBox formProcessorStartupCheckBox;
    private JPanel manuallyLoadImagesPanel;
    private JPanel panel9;
    private JLabel uploadImageLabel;
    private JButton uploadImageFolderButton;
    private JLabel uploadFolderLabel;
    private JButton uploadImageButton;
    private TaskSchedulerPanel taskSchedulerPanel;
    private JPanel panel2;
    private JPanel panel7;
    private JHelpLabel helpLabel;
    private JComboBox languageComboBox;
    private JButton changeLanguageButton;
    private JButton restartServerButton;
    private JButton closeServerButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
