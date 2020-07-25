package com.ebstrada.formreturn.manager.ui.sdm.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.swing.*;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

import au.com.bytecode.opencsv.CSVReader;

import com.ebstrada.formreturn.api.messaging.ProcessingStatusDialog;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.jpa.RecordController;
import com.ebstrada.formreturn.manager.persistence.jpa.DataSet;
import com.ebstrada.formreturn.manager.persistence.jpa.QueryProfile;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.ui.filefilter.FilenameExtensionFilter;
import com.ebstrada.formreturn.manager.ui.sdm.SourceDataManagerFrame;
import com.ebstrada.formreturn.manager.ui.sdm.persistence.JDBCProfile;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.UnicodeReader;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class ImportRecordsDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final String[][] SEPARATORS =
        new String[][] {new String[] {",", ", (Comma)"}, new String[] {"\t", "\\t (Tab)"}};

    private static final String[][] QUOTES =
        new String[][] {new String[] {"", "none"}, new String[] {"'", "' (Single Quote)"},
            new String[] {"\"", "\" (Double Quote)"}};

    private static final String[][] LINE_ENDINGS =
        new String[][] {new String[] {"\r\n", "\\r\\n (Windows)"},
            new String[] {"\n", "\\n (Unix)"}, new String[] {"\r", "\\r (Mac)"}};

    private SourceDataManagerFrame sourceDataManagerFrame;

    private long dataSetId;

    private File CSVFile;

    private Map<String, String> jdbcClassMapping;

    private JDBCProfile currentJDBCProfile;

    public ImportRecordsDialog(Frame owner, SourceDataManagerFrame sourceDataManagerFrame) {
        super(owner);
        initComponents();

        importRecordsTabbedPane
            .setTitleAt(0, Localizer.localize("UI", "ImportRecordsCSVImportTabTitle"));
        importRecordsTabbedPane
            .setTitleAt(1, Localizer.localize("UI", "ImportRecordsJDBCImportTabTitle"));

        this.sourceDataManagerFrame = sourceDataManagerFrame;
        this.dataSetId = sourceDataManagerFrame.getSelectedDataSetId();

        restoreCSVParams();
        restoreProfileList();
        restoreJDBCClassMapping();
        restoreQueryProfileDetails();

    }

    private void restoreCSVParams() {

        DefaultComboBoxModel dtcbm = new DefaultComboBoxModel();
        for (int i = 0; i < SEPARATORS.length; i++) {
            dtcbm.addElement(SEPARATORS[i][1]);
        }
        delimiterTypeComboBox.setModel(dtcbm);

        DefaultComboBoxModel qtcbm = new DefaultComboBoxModel();
        for (int i = 0; i < QUOTES.length; i++) {
            qtcbm.addElement(QUOTES[i][1]);
        }
        quoteTypeComboBox.setModel(qtcbm);

    }

    private void restoreJDBCClassMapping() {

        if (jdbcClassMapping == null) {
            jdbcClassMapping = new HashMap<String, String>();
            jdbcClassMapping.put("com.mysql.jdbc.Driver", "mysql");
            jdbcClassMapping.put("org.apache.derby.jdbc.ClientDriver", "derby");
        }

        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();

        Iterator<String> jcmki = jdbcClassMapping.keySet().iterator();
        while (jcmki.hasNext()) {
            dcbm.addElement(jcmki.next());
        }

        wizardProtocolComboBox.setModel(dcbm);

    }

    private void restoreQueryProfileDetails() {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        try {

            DataSet ds =
                entityManager.find(DataSet.class, sourceDataManagerFrame.getSelectedDataSetId());

            Iterator<QueryProfile> qpi = ds.getQueryProfileCollection().iterator();

            QueryProfile qp = null;
            if (qpi.hasNext()) {
                qp = qpi.next();
                currentJDBCProfile = getJDBCProfileFromXMLByteArray(qp.getQueryProfileXml());
            }

            if (currentJDBCProfile == null) {
                currentJDBCProfile = new JDBCProfile();
            }

            restoreJDBCProfile();

        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return;
        } finally {
            entityManager.close();
        }

    }

    private void createJDBCURLButtonActionPerformed(ActionEvent e) {

        String jdbcURLMapping =
            jdbcClassMapping.get((String) wizardProtocolComboBox.getSelectedItem());

        String jdbcURLString = "jdbc:" + jdbcURLMapping + "://";

        jdbcURLString += wizardServerIPAddressTextField.getText().trim();

        if (wizardPortNumberTextField.getText().length() > 0 && !(wizardPortNumberTextField
            .getText().trim().equals("(default)"))) {
            jdbcURLString += ":" + wizardPortNumberTextField.getText().trim();
        }

        jdbcURLString += "/" + databaseNameTextField.getText().trim();

        JDBCURLTextField.setText(jdbcURLString);

    }

    private void runQueryViewOutputButtonActionPerformed(ActionEvent e) {
        runQuery(true);
    }

    private void runQueryImportDataButtonActionPerformed(ActionEvent e) {
        runQuery(false);
    }

    private void runQuery(final boolean isPreview) {

        final Properties properties = new Properties();
        if (usernameTextField.getText() != null && usernameTextField.getText().length() > 0) {
            properties.put("user", usernameTextField.getText());
        }
        if (passwordPasswordField.getPassword() != null
            && passwordPasswordField.getPassword().length > 0) {
            properties.put("password", new String(passwordPasswordField.getPassword()));
        }



        final File jdbcDriverFile = new File(jdbcDriverPathTextField.getText().trim());
        if (!(jdbcDriverFile.exists())) {

            String message =
                Localizer.localize("UI", "ImportRecordsConnectionErrorDriverFileNotFoundMessage");
            String caption = Localizer.localize("UI", "ImportRecordsConnectionErrorTitle");
            javax.swing.JOptionPane
                .showConfirmDialog(this, message, caption, javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.ERROR_MESSAGE);

            return;
        }

        final ProcessingStatusDialog publishStatusDialog =
            new ProcessingStatusDialog(Main.getInstance());
        publishStatusDialog.setAlwaysOnTop(false);

        class QueryRunner implements Runnable {

            public void run() {

                Connection conn = null;

                try {
                    Driver d = (Driver) Misc.registerJarFile(jdbcDriverFile,
                        (String) wizardProtocolComboBox.getSelectedItem()).newInstance();
                    conn = d.connect(JDBCURLTextField.getText(), properties);

                    Statement stmt = conn.createStatement();
                    if (isPreview) {
                        stmt.setMaxRows(200);
                    }

                    ResultSet rs = stmt.executeQuery(sqlQueryTextArea.getText().trim());

                    ResultSetMetaData rsmd = rs.getMetaData();
                    String[] columnNames = new String[rsmd.getColumnCount()];
                    for (int i = 0; i < rsmd.getColumnCount(); i++) {
                        columnNames[i] = rsmd.getColumnLabel(i + 1);
                    }

                    if (isPreview) {

                        // create DefaultTableModel of 200 records and pass to dialog
                        DefaultTableModel dtm = new DefaultTableModel(columnNames, 0);
                        int recordNumber = 1;
                        while (rs.next()) {

                            if (publishStatusDialog != null) {
                                if (publishStatusDialog.isInterrupted()) {
                                    break;
                                }
                                publishStatusDialog.setMessage(String.format(
                                    Localizer.localize("UI", "ImportRecordsLoadingRecordStatus"),
                                    recordNumber + ""));
                            }

                            String[] rowData = new String[columnNames.length];
                            for (int i = 0; i < columnNames.length; i++) {
                                rowData[i] = rs.getString(columnNames[i]);
                            }
                            dtm.addRow(rowData);
                            recordNumber++;
                        }

                        rs.close();
                        stmt.close();

                        if (publishStatusDialog != null) {
                            publishStatusDialog.dispose();
                        }

                        ImportRecordsPreviewDialog irpd =
                            new ImportRecordsPreviewDialog(Main.getInstance(), dtm);
                        irpd.setVisible(true);

                    } else {

                        RecordController rc = new RecordController();
                        int recordNumber = 1;
                        while (rs.next()) {

                            if (publishStatusDialog != null) {
                                if (publishStatusDialog.isInterrupted()) {
                                    break;
                                }
                                publishStatusDialog.setMessage(String.format(
                                    Localizer.localize("UI", "ImportRecordsImportingRecordStatus"),
                                    recordNumber + ""));
                            }

                            String[] rowData = new String[columnNames.length];
                            for (int i = 0; i < columnNames.length; i++) {
                                rowData[i] = rs.getString(columnNames[i]);
                            }
                            rc.createNewRecord(dataSetId, columnNames, rowData);
                            recordNumber++;
                        }

                        rs.close();
                        stmt.close();

                        if (publishStatusDialog != null) {
                            publishStatusDialog.dispose();
                        }

                        sourceDataManagerFrame.restoreRecords();
                        sourceDataManagerFrame.restoreFields();

                    }

                } catch (MalformedURLException ex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                } catch (InstantiationException ex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                } catch (IllegalAccessException ex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                } catch (ClassNotFoundException ex) {
                    String message = Localizer.localize("UI",
                        "ImportRecordsConnectionErrorInvalidDriverClassNameMessage");
                    String caption = Localizer.localize("UI", "ImportRecordsConnectionErrorTitle");
                    javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
                        javax.swing.JOptionPane.DEFAULT_OPTION,
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    String message =
                        Localizer.localize("UI", "ImportRecordsConnectionErrorInvalidDatabaseName")
                            + "\n";
                    message += ex.getMessage();
                    String caption = Localizer.localize("UI", "ImportRecordsConnectionErrorTitle");
                    javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
                        javax.swing.JOptionPane.DEFAULT_OPTION,
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                } finally {
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (Exception cse) {

                        }
                    }
                }

            }

        }
        ;

        QueryRunner queryRunner = new QueryRunner();
        Thread thread = new Thread(queryRunner);
        thread.start();

        publishStatusDialog.setModal(true);
        publishStatusDialog.setVisible(true);

        publishStatusDialog.dispose();

        if (!isPreview) {
            dispose();
        }

    }

    private void restoreJDBCProfile() {
        profileNameTextField.setText(currentJDBCProfile.getProfileName());
        wizardServerIPAddressTextField.setText(currentJDBCProfile.getServerIPAddress());
        wizardPortNumberTextField.setText(currentJDBCProfile.getServerPortNumber());
        if (currentJDBCProfile.getJdbcProtocol() != null) {
            wizardProtocolComboBox.setSelectedItem(currentJDBCProfile.getJdbcProtocol());
        }
        usernameTextField.setText(currentJDBCProfile.getUsername());
        passwordPasswordField.setText(currentJDBCProfile.getPassword());
        jdbcDriverPathTextField.setText(currentJDBCProfile.getJdbcDriverFilePath());
        JDBCURLTextField.setText(currentJDBCProfile.getJdbcURL());
        sqlQueryTextArea.setText(currentJDBCProfile.getSqlQuery());
        databaseNameTextField.setText(currentJDBCProfile.getDatabaseName());
    }


    private void setJDBCProfileData() {
        currentJDBCProfile = new JDBCProfile();
        currentJDBCProfile.setProfileName(profileNameTextField.getText());
        currentJDBCProfile.setServerIPAddress(wizardServerIPAddressTextField.getText());
        currentJDBCProfile.setServerPortNumber(wizardPortNumberTextField.getText());
        currentJDBCProfile.setJDBCProtocol((String) wizardProtocolComboBox.getSelectedItem());
        currentJDBCProfile.setUsername(usernameTextField.getText());
        currentJDBCProfile.setPassword(new String(passwordPasswordField.getPassword()));
        currentJDBCProfile.setJDBCDriverFilePath(jdbcDriverPathTextField.getText());
        currentJDBCProfile.setJDBCURL(JDBCURLTextField.getText());
        currentJDBCProfile.setSQLQuery(sqlQueryTextArea.getText());
        currentJDBCProfile.setDatabaseName(databaseNameTextField.getText());
    }

    private void testConnectionButtonActionPerformed(ActionEvent e) {

        Properties properties = new Properties();
        if (usernameTextField.getText() != null && usernameTextField.getText().length() > 0) {
            properties.put("user", usernameTextField.getText());
        }
        if (passwordPasswordField.getPassword() != null
            && passwordPasswordField.getPassword().length > 0) {
            properties.put("password", new String(passwordPasswordField.getPassword()));
        }

        Connection conn = null;

        File jdbcDriverFile = new File(jdbcDriverPathTextField.getText().trim());
        if (!(jdbcDriverFile.exists())) {

            String message =
                Localizer.localize("UI", "ImportRecordsConnectionErrorDriverFileNotFoundMessage");
            String caption = Localizer.localize("UI", "ImportRecordsConnectionErrorTitle");
            javax.swing.JOptionPane
                .showConfirmDialog(this, message, caption, javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.ERROR_MESSAGE);

            return;
        }

        try {
            Driver d = (Driver) Misc
                .registerJarFile(jdbcDriverFile, (String) wizardProtocolComboBox.getSelectedItem())
                .newInstance();
            conn = d.connect(JDBCURLTextField.getText(), properties);
            String message = Localizer.localize("UI", "ImportRecordsConnectionSuccessMessage");
            String caption = Localizer.localize("UI", "ImportRecordsConnectionSuccessTitle");
            javax.swing.JOptionPane
                .showConfirmDialog(this, message, caption, javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } catch (MalformedURLException ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        } catch (InstantiationException ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        } catch (IllegalAccessException ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        } catch (ClassNotFoundException ex) {
            String message = Localizer
                .localize("UI", "ImportRecordsConnectionErrorInvalidDriverClassNameMessage");
            String caption = Localizer.localize("UI", "ImportRecordsConnectionErrorTitle");
            javax.swing.JOptionPane
                .showConfirmDialog(this, message, caption, javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            String message =
                Localizer.localize("UI", "ImportRecordsConnectionErrorInvalidDatabaseName") + "\n";
            message += ex.getMessage();
            String caption = Localizer.localize("UI", "ImportRecordsConnectionErrorTitle");
            javax.swing.JOptionPane
                .showConfirmDialog(this, message, caption, javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception cse) {

                }
            }
        }

    }

    public ImportRecordsDialog(Dialog owner) {
        super(owner);
        initComponents();
    }

    public void restoreProfileList() {

        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();

        Iterator<JDBCProfile> jdbcProfilesIterator =
            PreferencesManager.getJDBCProfiles().iterator();
        while (jdbcProfilesIterator.hasNext()) {
            JDBCProfile jdbcProfile = jdbcProfilesIterator.next();
            dcbm.addElement(jdbcProfile.getProfileName());
        }

        existingProfileComboBox.setModel(dcbm);

    }

    private void importCSVButtonActionPerformed(ActionEvent e) {

        if (CSVFile == null) {
            String message = Localizer.localize("UI", "ImportRecordsNoCSVFileSelectedMessage");
            String caption = Localizer.localize("UI", "ImportRecordsNoCSVFileSelectedTitle");
            javax.swing.JOptionPane
                .showConfirmDialog(this, message, caption, javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        final ProcessingStatusDialog publishStatusDialog =
            new ProcessingStatusDialog(Main.getInstance());
        publishStatusDialog.setAlwaysOnTop(false);

        class CSVImportRunner implements Runnable {

            public void run() {

                CSVReader reader = null;

                try {
                    String separator = SEPARATORS[delimiterTypeComboBox.getSelectedIndex()][0];
                    String quotechar = QUOTES[quoteTypeComboBox.getSelectedIndex()][0];
                    if (quotechar.length() > 0) {
                        reader =
                            new CSVReader(new UnicodeReader(new FileInputStream(CSVFile), "UTF-8"),
                                separator.charAt(0), quotechar.charAt(0));
                    } else {
                        reader =
                            new CSVReader(new UnicodeReader(new FileInputStream(CSVFile), "UTF-8"),
                                separator.charAt(0));
                    }
                } catch (FileNotFoundException ex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                }

                String[] nextLine;
                String[] columnNames;

                try {

                    columnNames = reader.readNext();
                    if (columnNames == null) {
                        return;
                    }

                    RecordController rc = new RecordController();

                    int rowNumber = 1;
                    while ((nextLine = reader.readNext()) != null) {
                        if (publishStatusDialog != null) {
                            if (publishStatusDialog.isInterrupted()) {
                                break;
                            } else {
                                publishStatusDialog.setMessage(String.format(
                                    Localizer.localize("UI", "ImportRecordsImportingRowStatus"),
                                    rowNumber + ""));
                            }
                        }
                        rc.createNewRecord(dataSetId, columnNames, nextLine);
                        rowNumber++;
                    }

                } catch (IOException ex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                }

                if (publishStatusDialog != null) {
                    publishStatusDialog.dispose();
                }

            }

        }
        ;

        CSVImportRunner importRunner = new CSVImportRunner();
        Thread thread = new Thread(importRunner);
        thread.start();

        publishStatusDialog.setModal(true);
        publishStatusDialog.setVisible(true);

        publishStatusDialog.dispose();

        sourceDataManagerFrame.restoreRecords();
        sourceDataManagerFrame.restoreFields();
        dispose();

    }

    private void cancelButtonCSVActionPerformed(ActionEvent e) {
        CSVFile = null;
        dispose();
    }

    private void browseCSVFileButtonActionPerformed(ActionEvent e) {

        File file = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();
        filter.addExtension("csv");
        filter.addExtension("tab");
        filter.addExtension("tsv");

        FileDialog fd = new FileDialog(Main.getInstance(),
            Localizer.localize("UI", "ImportRecordsBrowseCSVFileDialogTitle"), FileDialog.LOAD);
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

        CSVFile = file;

        createCSVPreview(CSVFile);

    }

    public void createCSVPreview(File CSVFile) {
        CSVReader reader = null;

        try {
            String separator = SEPARATORS[delimiterTypeComboBox.getSelectedIndex()][0];
            String quotechar = QUOTES[quoteTypeComboBox.getSelectedIndex()][0];
            if (quotechar.length() > 0) {
                reader = new CSVReader(new UnicodeReader(new FileInputStream(CSVFile), "UTF-8"),
                    separator.charAt(0), quotechar.charAt(0));
            } else {
                reader = new CSVReader(new UnicodeReader(new FileInputStream(CSVFile), "UTF-8"),
                    separator.charAt(0));
            }
        } catch (FileNotFoundException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }

        String[] nextLine;
        String[] columnNames;

        Vector<String> columnNamesVector;
        Vector<Vector<String>> dataVector = new Vector<Vector<String>>();

        try {

            columnNames = reader.readNext();
            if (columnNames == null) {
                return;
            } else {
                columnNamesVector = new Vector<String>(Arrays.asList(columnNames));
            }

            int i = 0;
            while ((nextLine = reader.readNext()) != null && i < 200) {
                dataVector.add(new Vector<String>(Arrays.asList(nextLine)));
                i++;
            }

            DefaultTableModel dtm = new DefaultTableModel();
            dtm.setDataVector(dataVector, columnNamesVector);
            csvPreviewTable.setModel(dtm);
            csvPreviewTable.getTableHeader().setReorderingAllowed(false);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }

    }

    private void cancelButtonJDBCActionPerformed(ActionEvent e) {
        dispose();
    }

    private void restoreProfileButtonActionPerformed(ActionEvent e) {
        String profileName = (String) existingProfileComboBox.getSelectedItem();

        Iterator<JDBCProfile> jdbcProfilesIterator =
            PreferencesManager.getJDBCProfiles().iterator();
        while (jdbcProfilesIterator.hasNext()) {
            JDBCProfile jdbcProfile = jdbcProfilesIterator.next();
            if (jdbcProfile.getProfileName().trim().equals(profileName.trim())) {
                currentJDBCProfile = jdbcProfile;
                break;
            }
        }

        restoreJDBCProfile();

    }

    private void removeProfileButtonActionPerformed(ActionEvent e) {

        String profileName = (String) existingProfileComboBox.getSelectedItem();

        // then iterate through the list of JDBCProfiles and delete ones with the same name

        Iterator<JDBCProfile> jdbcProfilesIterator =
            PreferencesManager.getJDBCProfiles().iterator();
        Vector<JDBCProfile> jdbcProfiles = new Vector<JDBCProfile>();
        boolean savePreferences = false;
        while (jdbcProfilesIterator.hasNext()) {
            jdbcProfiles.add(jdbcProfilesIterator.next());
        }

        Iterator<JDBCProfile> jpi = jdbcProfiles.iterator();
        while (jpi.hasNext()) {
            JDBCProfile jdbcProfile = jpi.next();
            if (jdbcProfile.getProfileName().trim().equals(profileName.trim())) {

                Object[] options =
                    {Localizer.localize("UI", "Yes"), Localizer.localize("UI", "No")};

                String msg = Localizer.localize("UI", "ImportRecordsRemoveProfileMessage") + "\n";
                msg += "\"" + profileName.trim() + "\" ?";

                int result = JOptionPane
                    .showOptionDialog(this, msg, Localizer.localize("UI", "WarningTitle"),
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
                        options[0]);

                if (result != 1) {
                    PreferencesManager.removeJDBCProfile(jdbcProfile);
                    savePreferences = true;
                }

            }
        }

        if (savePreferences) {
            try {
                PreferencesManager.savePreferences(Main.getXstream());
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
            }
        }
        restoreProfileList();

    }

    private void saveAsNewProfileButtonActionPerformed(ActionEvent e) {

        String newProfileName = profileNameTextField.getText().trim();

        if (newProfileName.length() > 255 || newProfileName.length() < 1) {
            String message = Localizer.localize("UI", "ImportRecordsInvalidProfileNameMessage");
            String caption = Localizer.localize("UI", "ImportRecordsInvalidProfileNameTitle");
            javax.swing.JOptionPane
                .showConfirmDialog(this, message, caption, javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        // check for a duplicate name in the list
        for (int i = 0; i < existingProfileComboBox.getItemCount(); i++) {
            String profileName = (String) existingProfileComboBox.getItemAt(i);
            if (newProfileName.equals(profileName.trim())) {
                String message =
                    Localizer.localize("UI", "ImportRecordsDuplicateProfileNameMessage");
                String caption = Localizer.localize("UI", "ImportRecordsDuplicateProfileNameTitle");
                javax.swing.JOptionPane.showConfirmDialog(this, message, caption,
                    javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        setJDBCProfileData();
        PreferencesManager.addJDBCProfile(currentJDBCProfile);
        try {
            PreferencesManager.savePreferences(Main.getXstream());
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
        }

        profileNameTextField.setText("");
        restoreProfileList();

        existingProfileComboBox.setSelectedItem(newProfileName);
        existingProfileComboBox.updateUI();

    }

    private void browseJDBCDriverFileButtonActionPerformed(ActionEvent e) {

        File file = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();
        filter.addExtension("jar");

        FileDialog fd = new FileDialog(Main.getInstance(),
            Localizer.localize("UI", "ImportRecordsBrowseJDBCDriverFileDialogTitle"),
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
        } else {
            return;
        }

        jdbcDriverPathTextField.setText(fd.getDirectory() + fd.getFile());

    }

    private void saveQueryDetailsButtonActionPerformed(ActionEvent e) {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        try {

            DataSet ds =
                entityManager.find(DataSet.class, sourceDataManagerFrame.getSelectedDataSetId());

            entityManager.getTransaction().begin();
            entityManager.flush();

            Iterator<QueryProfile> qpi = ds.getQueryProfileCollection().iterator();

            while (qpi.hasNext()) {
                QueryProfile queryProfile = qpi.next();
                entityManager.remove(queryProfile);
            }

            // add records
            setJDBCProfileData();

            QueryProfile queryProfile = new QueryProfile();
            queryProfile.setDataSetId(ds);

            queryProfile.setQueryProfileXml(getXMLByteArrayFromJDBCProfile(currentJDBCProfile));

            entityManager.persist(queryProfile);

            entityManager.getTransaction().commit();

        } catch (Exception ex) {
            if (entityManager.getTransaction().isActive()) {
                try {
                    entityManager.getTransaction().rollback();
                } catch (Exception rbex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(rbex);
                }
            }
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        } finally {
            entityManager.close();
        }

    }

    private JDBCProfile getJDBCProfileFromXMLByteArray(byte[] queryProfileXml) {

        ByteArrayInputStream s = new ByteArrayInputStream(queryProfileXml);
        ObjectInputStream ois;

        try {
            ois = Main.getInstance().getXstream().createObjectInputStream(s);
            JDBCProfile jdbcProfile = (JDBCProfile) ois.readObject();
            return jdbcProfile;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }

        return null;

    }

    private byte[] getXMLByteArrayFromJDBCProfile(JDBCProfile jdbcProfile) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;

        try {
            oos = Main.getInstance().getXstream().createObjectOutputStream(baos);
            oos.writeObject(jdbcProfile);
            oos.close();
            return baos.toByteArray();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }

        return null;

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        importRecordsTabbedPane = new JTabbedPane();
        panel5 = new JPanel();
        filePreviewPanel = new JPanel();
        csvPreviewScrollPane = new JScrollPane();
        csvPreviewTable = new JTable();
        panel6 = new JPanel();
        previewRecordsLimitedLabel = new JLabel();
        csvFileMustIncludeHeaderWarningLabel = new JLabel();
        buttonBar = new JPanel();
        panel15 = new JPanel();
        separatorLabel = new JLabel();
        delimiterTypeComboBox = new JComboBox();
        quoteCharacterLabel = new JLabel();
        quoteTypeComboBox = new JComboBox();
        panel16 = new JPanel();
        browseCSVFileButton = new JButton();
        importCSVButton = new JButton();
        cancelButtonCSV = new JButton();
        panel2 = new JPanel();
        profileManagementPanel = new JPanel();
        panel13 = new JPanel();
        existingProfileLabel = new JLabel();
        existingProfileComboBox = new JComboBox();
        restoreProfileButton = new JButton();
        removeProfileButton = new JButton();
        panel3 = new JPanel();
        newProfileNameLabel = new JLabel();
        profileNameTextField = new JTextField();
        saveAsNewProfileButton = new JButton();
        jdbcURLWizardPanel = new JPanel();
        panel17 = new JPanel();
        serverIPLabel = new JLabel();
        wizardServerIPAddressTextField = new JTextField();
        portNumberLabel = new JLabel();
        wizardPortNumberTextField = new JTextField();
        databaseNameLabel = new JLabel();
        databaseNameTextField = new JTextField();
        panel19 = new JPanel();
        jdbcProtocolLabel = new JLabel();
        wizardProtocolComboBox = new JComboBox();
        newJDBCURLLabel = new JLabel();
        createJDBCURLButton = new JButton();
        jdbcDetailsPanel = new JPanel();
        panel14 = new JPanel();
        useJDBCWizardNotificationLabel = new JLabel();
        passwordsStoredInClearTextNotificationLabel = new JLabel();
        panel12 = new JPanel();
        usernameLabel = new JLabel();
        usernameTextField = new JTextField();
        passwordLabel = new JLabel();
        passwordPasswordField = new JPasswordField();
        panel8 = new JPanel();
        selectJDBCDriverFileLabel = new JLabel();
        jdbcDriverPathTextField = new JTextField();
        browseJDBCDriverFileButton = new JButton();
        jdbcURLLabel = new JLabel();
        JDBCURLTextField = new JTextField();
        testConnectionButton = new JButton();
        sqlQueryPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        sqlQueryTextArea = new JTextArea();
        panel11 = new JPanel();
        runQueryViewOutputButton = new JButton();
        runQueryImportDataButton = new JButton();
        saveQueryDetailsButton = new JButton();
        cancelButtonJDBC = new JButton();

        //======== this ========
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "ImportRecordsDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 5, 5, 5));
            dialogPane.setLayout(new GridBagLayout());
            ((GridBagLayout) dialogPane.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) dialogPane.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout) dialogPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) dialogPane.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

            //======== importRecordsTabbedPane ========
            {
                importRecordsTabbedPane.setFont(UIManager.getFont("TabbedPane.font"));

                //======== panel5 ========
                {
                    panel5.setOpaque(false);
                    panel5.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel5.getLayout()).columnWidths = new int[] {5, 0, 5, 0};
                    ((GridBagLayout) panel5.getLayout()).rowHeights = new int[] {0, 30, 0, 0};
                    ((GridBagLayout) panel5.getLayout()).columnWeights =
                        new double[] {0.0, 1.0, 0.0, 1.0E-4};
                    ((GridBagLayout) panel5.getLayout()).rowWeights =
                        new double[] {1.0, 0.0, 0.0, 1.0E-4};

                    //======== filePreviewPanel ========
                    {
                        filePreviewPanel.setOpaque(false);
                        filePreviewPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout) filePreviewPanel.getLayout()).columnWidths =
                            new int[] {0, 0};
                        ((GridBagLayout) filePreviewPanel.getLayout()).rowHeights =
                            new int[] {0, 0};
                        ((GridBagLayout) filePreviewPanel.getLayout()).columnWeights =
                            new double[] {1.0, 1.0E-4};
                        ((GridBagLayout) filePreviewPanel.getLayout()).rowWeights =
                            new double[] {1.0, 1.0E-4};
                        filePreviewPanel.setBorder(new CompoundBorder(new TitledBorder(
                            Localizer.localize("UI", "ImportRecordsFilePreviewPanelTitle")),
                            new EmptyBorder(2, 2, 2, 2)));

                        //======== csvPreviewScrollPane ========
                        {
                            csvPreviewScrollPane.setRequestFocusEnabled(false);

                            //---- csvPreviewTable ----
                            csvPreviewTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                            csvPreviewTable.setRequestFocusEnabled(false);
                            csvPreviewTable.setFont(UIManager.getFont("Table.font"));
                            csvPreviewTable.getTableHeader()
                                .setFont(UIManager.getFont("TableHeader.font"));
                            csvPreviewScrollPane.setViewportView(csvPreviewTable);
                        }
                        filePreviewPanel.add(csvPreviewScrollPane,
                            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                    }
                    panel5.add(filePreviewPanel,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

                    //======== panel6 ========
                    {
                        panel6.setOpaque(false);
                        panel6.setLayout(new GridBagLayout());
                        ((GridBagLayout) panel6.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout) panel6.getLayout()).rowHeights = new int[] {6, 10, 0, 0};
                        ((GridBagLayout) panel6.getLayout()).columnWeights =
                            new double[] {1.0, 1.0E-4};
                        ((GridBagLayout) panel6.getLayout()).rowWeights =
                            new double[] {0.0, 0.0, 0.0, 1.0E-4};

                        //---- previewRecordsLimitedLabel ----
                        previewRecordsLimitedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                        previewRecordsLimitedLabel.setFont(UIManager.getFont("Label.font"));
                        previewRecordsLimitedLabel.setText(
                            Localizer.localize("UI", "ImportRecordsPreviewRecordsLimitedLabel"));
                        panel6.add(previewRecordsLimitedLabel,
                            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                        //---- csvFileMustIncludeHeaderWarningLabel ----
                        csvFileMustIncludeHeaderWarningLabel
                            .setHorizontalAlignment(SwingConstants.CENTER);
                        csvFileMustIncludeHeaderWarningLabel
                            .setFont(UIManager.getFont("Label.font"));
                        csvFileMustIncludeHeaderWarningLabel.setText(Localizer
                            .localize("UI", "ImportRecordsCSVFileMustIncludeHeaderWarningLabel"));
                        panel6.add(csvFileMustIncludeHeaderWarningLabel,
                            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                    }
                    panel5.add(panel6,
                        new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

                    //======== buttonBar ========
                    {
                        buttonBar.setBorder(new EmptyBorder(0, 0, 5, 0));
                        buttonBar.setOpaque(false);
                        buttonBar.setLayout(new GridBagLayout());
                        ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] {1.0};

                        //======== panel15 ========
                        {
                            panel15.setOpaque(false);
                            panel15.setLayout(new GridBagLayout());
                            ((GridBagLayout) panel15.getLayout()).columnWidths =
                                new int[] {0, 0, 0, 15, 0, 0, 0, 0};
                            ((GridBagLayout) panel15.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout) panel15.getLayout()).columnWeights =
                                new double[] {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                            ((GridBagLayout) panel15.getLayout()).rowWeights =
                                new double[] {0.0, 1.0E-4};

                            //---- separatorLabel ----
                            separatorLabel.setFont(UIManager.getFont("Label.font"));
                            separatorLabel
                                .setText(Localizer.localize("UI", "ImportRecordsSeparatorLabel"));
                            panel15.add(separatorLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- delimiterTypeComboBox ----
                            delimiterTypeComboBox.setRequestFocusEnabled(false);
                            delimiterTypeComboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxxx");
                            delimiterTypeComboBox.setFont(UIManager.getFont("ComboBox.font"));
                            panel15.add(delimiterTypeComboBox,
                                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- quoteCharacterLabel ----
                            quoteCharacterLabel.setFont(UIManager.getFont("Label.font"));
                            quoteCharacterLabel.setText(
                                Localizer.localize("UI", "ImportRecordsQuoteCharacterLabel"));
                            panel15.add(quoteCharacterLabel,
                                new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- quoteTypeComboBox ----
                            quoteTypeComboBox.setRequestFocusEnabled(false);
                            quoteTypeComboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxxx");
                            quoteTypeComboBox.setFont(UIManager.getFont("ComboBox.font"));
                            panel15.add(quoteTypeComboBox,
                                new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));
                        }
                        buttonBar.add(panel15,
                            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                        //======== panel16 ========
                        {
                            panel16.setOpaque(false);
                            panel16.setLayout(new GridBagLayout());
                            ((GridBagLayout) panel16.getLayout()).columnWidths =
                                new int[] {0, 0, 0, 0, 0, 0};
                            ((GridBagLayout) panel16.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout) panel16.getLayout()).columnWeights =
                                new double[] {1.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                            ((GridBagLayout) panel16.getLayout()).rowWeights =
                                new double[] {0.0, 1.0E-4};

                            //---- browseCSVFileButton ----
                            browseCSVFileButton.setFocusPainted(false);
                            browseCSVFileButton.setFont(UIManager.getFont("Button.font"));
                            browseCSVFileButton.setIcon(new ImageIcon(getClass().getResource(
                                "/com/ebstrada/formreturn/manager/ui/icons/folder_go.png")));
                            browseCSVFileButton.addActionListener(new ActionListener() {
                                @Override public void actionPerformed(ActionEvent e) {
                                    browseCSVFileButtonActionPerformed(e);
                                }
                            });
                            browseCSVFileButton.setText(
                                Localizer.localize("UI", "ImportRecordsSelectCSVFileButtonText"));
                            panel16.add(browseCSVFileButton,
                                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- importCSVButton ----
                            importCSVButton.setFocusPainted(false);
                            importCSVButton.setFont(UIManager.getFont("Button.font"));
                            importCSVButton.setIcon(new ImageIcon(getClass().getResource(
                                "/com/ebstrada/formreturn/manager/ui/icons/sdm/table_go.png")));
                            importCSVButton.addActionListener(new ActionListener() {
                                @Override public void actionPerformed(ActionEvent e) {
                                    importCSVButtonActionPerformed(e);
                                }
                            });
                            importCSVButton.setText(
                                Localizer.localize("UI", "ImportRecordsImportCSVButtonText"));
                            panel16.add(importCSVButton,
                                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- cancelButtonCSV ----
                            cancelButtonCSV.setFocusPainted(false);
                            cancelButtonCSV.setFont(UIManager.getFont("Button.font"));
                            cancelButtonCSV.setIcon(new ImageIcon(getClass().getResource(
                                "/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
                            cancelButtonCSV.addActionListener(new ActionListener() {
                                @Override public void actionPerformed(ActionEvent e) {
                                    cancelButtonCSVActionPerformed(e);
                                }
                            });
                            cancelButtonCSV.setText(Localizer.localize("UI", "CancelButtonText"));
                            panel16.add(cancelButtonCSV,
                                new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));
                        }
                        buttonBar.add(panel16,
                            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                    }
                    panel5.add(buttonBar,
                        new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                }
                importRecordsTabbedPane.addTab("CSV Import", panel5);

                //======== panel2 ========
                {
                    panel2.setOpaque(false);
                    panel2.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel2.getLayout()).columnWidths = new int[] {15, 0, 10, 0};
                    ((GridBagLayout) panel2.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
                    ((GridBagLayout) panel2.getLayout()).columnWeights =
                        new double[] {0.0, 1.0, 0.0, 1.0E-4};
                    ((GridBagLayout) panel2.getLayout()).rowWeights =
                        new double[] {0.0, 0.0, 0.0, 1.0, 0.0, 1.0E-4};

                    //======== profileManagementPanel ========
                    {
                        profileManagementPanel.setOpaque(false);
                        profileManagementPanel.setFont(UIManager.getFont("Label.font"));
                        profileManagementPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout) profileManagementPanel.getLayout()).columnWidths =
                            new int[] {15, 0, 10, 0};
                        ((GridBagLayout) profileManagementPanel.getLayout()).rowHeights =
                            new int[] {0, 0, 0};
                        ((GridBagLayout) profileManagementPanel.getLayout()).columnWeights =
                            new double[] {0.0, 1.0, 0.0, 1.0E-4};
                        ((GridBagLayout) profileManagementPanel.getLayout()).rowWeights =
                            new double[] {0.0, 0.0, 1.0E-4};
                        profileManagementPanel.setBorder(new CompoundBorder(new TitledBorder(
                            Localizer.localize("UI", "ImportRecordsProfileManagementPanelTitle")),
                            new EmptyBorder(2, 2, 2, 2)));

                        //======== panel13 ========
                        {
                            panel13.setOpaque(false);
                            panel13.setLayout(new GridBagLayout());
                            ((GridBagLayout) panel13.getLayout()).columnWidths =
                                new int[] {0, 305, 0, 0, 0};
                            ((GridBagLayout) panel13.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout) panel13.getLayout()).columnWeights =
                                new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
                            ((GridBagLayout) panel13.getLayout()).rowWeights =
                                new double[] {0.0, 1.0E-4};

                            //---- existingProfileLabel ----
                            existingProfileLabel.setFont(UIManager.getFont("Label.font"));
                            existingProfileLabel.setText(
                                Localizer.localize("UI", "ImportRecordsExistingProfileLabel"));
                            panel13.add(existingProfileLabel,
                                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- existingProfileComboBox ----
                            existingProfileComboBox.setRequestFocusEnabled(false);
                            existingProfileComboBox.setMaximumSize(new Dimension(300, 32767));
                            existingProfileComboBox.setMinimumSize(new Dimension(300, 10));
                            existingProfileComboBox
                                .setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxxxxxxxxx");
                            existingProfileComboBox.setFont(UIManager.getFont("ComboBox.font"));
                            panel13.add(existingProfileComboBox,
                                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- restoreProfileButton ----
                            restoreProfileButton.setFocusPainted(false);
                            restoreProfileButton.setFont(UIManager.getFont("Button.font"));
                            restoreProfileButton.setIcon(new ImageIcon(getClass().getResource(
                                "/com/ebstrada/formreturn/manager/ui/icons/accept.png")));
                            restoreProfileButton.addActionListener(new ActionListener() {
                                @Override public void actionPerformed(ActionEvent e) {
                                    restoreProfileButtonActionPerformed(e);
                                }
                            });
                            restoreProfileButton.setText(
                                Localizer.localize("UI", "ImportRecordsRestoreProfileButtonText"));
                            panel13.add(restoreProfileButton,
                                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- removeProfileButton ----
                            removeProfileButton.setFocusPainted(false);
                            removeProfileButton.setFont(UIManager.getFont("Button.font"));
                            removeProfileButton.setIcon(new ImageIcon(getClass().getResource(
                                "/com/ebstrada/formreturn/manager/ui/icons/delete.png")));
                            removeProfileButton.addActionListener(new ActionListener() {
                                @Override public void actionPerformed(ActionEvent e) {
                                    removeProfileButtonActionPerformed(e);
                                }
                            });
                            removeProfileButton.setText(
                                Localizer.localize("UI", "ImportRecordsRemoveProfileButtonText"));
                            panel13.add(removeProfileButton,
                                new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 0), 0, 0));
                        }
                        profileManagementPanel.add(panel13,
                            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

                        //======== panel3 ========
                        {
                            panel3.setOpaque(false);
                            panel3.setLayout(new GridBagLayout());
                            ((GridBagLayout) panel3.getLayout()).columnWidths =
                                new int[] {0, 0, 0, 0};
                            ((GridBagLayout) panel3.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout) panel3.getLayout()).columnWeights =
                                new double[] {0.0, 1.0, 0.0, 1.0E-4};
                            ((GridBagLayout) panel3.getLayout()).rowWeights =
                                new double[] {0.0, 1.0E-4};

                            //---- newProfileNameLabel ----
                            newProfileNameLabel.setFont(UIManager.getFont("Label.font"));
                            newProfileNameLabel.setText(
                                Localizer.localize("UI", "ImportRecordsNewProfileNameLabel"));
                            panel3.add(newProfileNameLabel,
                                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- profileNameTextField ----
                            profileNameTextField.setFont(UIManager.getFont("TextField.font"));
                            panel3.add(profileNameTextField,
                                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- saveAsNewProfileButton ----
                            saveAsNewProfileButton.setFocusPainted(false);
                            saveAsNewProfileButton.setFont(UIManager.getFont("Button.font"));
                            saveAsNewProfileButton.setIcon(new ImageIcon(getClass().getResource(
                                "/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
                            saveAsNewProfileButton.addActionListener(new ActionListener() {
                                @Override public void actionPerformed(ActionEvent e) {
                                    saveAsNewProfileButtonActionPerformed(e);
                                }
                            });
                            saveAsNewProfileButton.setText(Localizer
                                .localize("UI", "ImportRecordsSaveAsNewProfileButtonText"));
                            panel3.add(saveAsNewProfileButton,
                                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 0), 0, 0));
                        }
                        profileManagementPanel.add(panel3,
                            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
                    }
                    panel2.add(profileManagementPanel,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

                    //======== jdbcURLWizardPanel ========
                    {
                        jdbcURLWizardPanel.setOpaque(false);
                        jdbcURLWizardPanel.setFont(UIManager.getFont("Label.font"));
                        jdbcURLWizardPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout) jdbcURLWizardPanel.getLayout()).columnWidths =
                            new int[] {15, 235, 10, 0};
                        ((GridBagLayout) jdbcURLWizardPanel.getLayout()).rowHeights =
                            new int[] {0, 0, 0};
                        ((GridBagLayout) jdbcURLWizardPanel.getLayout()).columnWeights =
                            new double[] {0.0, 1.0, 0.0, 1.0E-4};
                        ((GridBagLayout) jdbcURLWizardPanel.getLayout()).rowWeights =
                            new double[] {0.0, 0.0, 1.0E-4};
                        jdbcURLWizardPanel.setBorder(new CompoundBorder(new TitledBorder(
                            Localizer.localize("UI", "ImportRecordsJDBCURLWizardPanelTitle")),
                            new EmptyBorder(2, 2, 2, 2)));

                        //======== panel17 ========
                        {
                            panel17.setOpaque(false);
                            panel17.setLayout(new GridBagLayout());
                            ((GridBagLayout) panel17.getLayout()).columnWidths =
                                new int[] {0, 155, 15, 0, 85, 15, 0, 150, 0};
                            ((GridBagLayout) panel17.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout) panel17.getLayout()).columnWeights =
                                new double[] {1.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0, 1.0, 1.0E-4};
                            ((GridBagLayout) panel17.getLayout()).rowWeights =
                                new double[] {0.0, 1.0E-4};

                            //---- serverIPLabel ----
                            serverIPLabel.setFont(UIManager.getFont("Label.font"));
                            serverIPLabel
                                .setText(Localizer.localize("UI", "ImportRecordsServerIPLabel"));
                            panel17.add(serverIPLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.EAST, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- wizardServerIPAddressTextField ----
                            wizardServerIPAddressTextField
                                .setMaximumSize(new Dimension(130, 2147483647));
                            wizardServerIPAddressTextField
                                .setFont(UIManager.getFont("TextField.font"));
                            panel17.add(wizardServerIPAddressTextField,
                                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- portNumberLabel ----
                            portNumberLabel.setFont(UIManager.getFont("Label.font"));
                            portNumberLabel
                                .setText(Localizer.localize("UI", "ImportRecordsPortNumberLabel"));
                            panel17.add(portNumberLabel,
                                new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- wizardPortNumberTextField ----
                            wizardPortNumberTextField
                                .setMaximumSize(new Dimension(130, 2147483647));
                            wizardPortNumberTextField.setFont(UIManager.getFont("TextField.font"));
                            wizardPortNumberTextField.setText(
                                Localizer.localize("UI", "ImportRecordsPortNumberDefaultText"));
                            panel17.add(wizardPortNumberTextField,
                                new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- databaseNameLabel ----
                            databaseNameLabel.setFont(UIManager.getFont("Label.font"));
                            databaseNameLabel.setText(
                                Localizer.localize("UI", "ImportRecordsDatabaseNameLabel"));
                            panel17.add(databaseNameLabel,
                                new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- databaseNameTextField ----
                            databaseNameTextField.setMaximumSize(new Dimension(130, 2147483647));
                            databaseNameTextField.setFont(UIManager.getFont("TextField.font"));
                            panel17.add(databaseNameTextField,
                                new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 0), 0, 0));
                        }
                        jdbcURLWizardPanel.add(panel17,
                            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

                        //======== panel19 ========
                        {
                            panel19.setOpaque(false);
                            panel19.setLayout(new GridBagLayout());
                            ((GridBagLayout) panel19.getLayout()).columnWidths =
                                new int[] {0, 305, 15, 0, 0, 0};
                            ((GridBagLayout) panel19.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout) panel19.getLayout()).columnWeights =
                                new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
                            ((GridBagLayout) panel19.getLayout()).rowWeights =
                                new double[] {0.0, 1.0E-4};

                            //---- jdbcProtocolLabel ----
                            jdbcProtocolLabel.setFont(UIManager.getFont("Label.font"));
                            jdbcProtocolLabel.setText(
                                Localizer.localize("UI", "ImportRecordsJDBCProtocolLabel"));
                            panel19.add(jdbcProtocolLabel,
                                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- wizardProtocolComboBox ----
                            wizardProtocolComboBox.setRequestFocusEnabled(false);
                            wizardProtocolComboBox.setMaximumSize(new Dimension(250, 32767));
                            wizardProtocolComboBox.setEditable(true);
                            wizardProtocolComboBox
                                .setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxxxxxxxxx");
                            wizardProtocolComboBox.setFont(UIManager.getFont("ComboBox.font"));
                            panel19.add(wizardProtocolComboBox,
                                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- newJDBCURLLabel ----
                            newJDBCURLLabel.setFont(UIManager.getFont("Label.font"));
                            newJDBCURLLabel
                                .setText(Localizer.localize("UI", "ImportRecordsNewJDBCURLLabel"));
                            panel19.add(newJDBCURLLabel,
                                new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- createJDBCURLButton ----
                            createJDBCURLButton.setFocusPainted(false);
                            createJDBCURLButton.setFont(UIManager.getFont("Button.font"));
                            createJDBCURLButton.setIcon(new ImageIcon(getClass().getResource(
                                "/com/ebstrada/formreturn/manager/ui/icons/wand.png")));
                            createJDBCURLButton.addActionListener(new ActionListener() {
                                @Override public void actionPerformed(ActionEvent e) {
                                    createJDBCURLButtonActionPerformed(e);
                                }
                            });
                            createJDBCURLButton.setText(
                                Localizer.localize("UI", "ImportRecordsCreateJDBCURLButtonText"));
                            panel19.add(createJDBCURLButton,
                                new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 0), 0, 0));
                        }
                        jdbcURLWizardPanel.add(panel19,
                            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
                    }
                    panel2.add(jdbcURLWizardPanel,
                        new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

                    //======== jdbcDetailsPanel ========
                    {
                        jdbcDetailsPanel.setOpaque(false);
                        jdbcDetailsPanel.setFont(UIManager.getFont("Label.font"));
                        jdbcDetailsPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout) jdbcDetailsPanel.getLayout()).columnWidths =
                            new int[] {15, 0, 10, 0};
                        ((GridBagLayout) jdbcDetailsPanel.getLayout()).rowHeights =
                            new int[] {0, 0, 0, 0};
                        ((GridBagLayout) jdbcDetailsPanel.getLayout()).columnWeights =
                            new double[] {0.0, 1.0, 0.0, 1.0E-4};
                        ((GridBagLayout) jdbcDetailsPanel.getLayout()).rowWeights =
                            new double[] {0.0, 0.0, 0.0, 1.0E-4};
                        jdbcDetailsPanel.setBorder(new CompoundBorder(new TitledBorder(
                            Localizer.localize("UI", "ImportRecordsJDBCDetailsPanelTitle")),
                            new EmptyBorder(2, 2, 2, 2)));

                        //======== panel14 ========
                        {
                            panel14.setOpaque(false);
                            panel14.setLayout(new GridBagLayout());
                            ((GridBagLayout) panel14.getLayout()).columnWidths =
                                new int[] {0, 0, 0};
                            ((GridBagLayout) panel14.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout) panel14.getLayout()).columnWeights =
                                new double[] {1.0, 1.0, 1.0E-4};
                            ((GridBagLayout) panel14.getLayout()).rowWeights =
                                new double[] {0.0, 1.0E-4};

                            //---- useJDBCWizardNotificationLabel ----
                            useJDBCWizardNotificationLabel
                                .setFont(new Font("Lucida Grande", Font.PLAIN, 10));
                            useJDBCWizardNotificationLabel.setText(Localizer
                                .localize("UI", "ImportRecordsUseJDBCWizardNotificationLabel"));
                            panel14.add(useJDBCWizardNotificationLabel,
                                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- passwordsStoredInClearTextNotificationLabel ----
                            passwordsStoredInClearTextNotificationLabel
                                .setHorizontalAlignment(SwingConstants.RIGHT);
                            passwordsStoredInClearTextNotificationLabel
                                .setFont(new Font("Lucida Grande", Font.PLAIN, 10));
                            passwordsStoredInClearTextNotificationLabel.setText(Localizer
                                .localize("UI",
                                    "ImportRecordsPasswordsStoredInClearTextNotificationLabel"));
                            panel14.add(passwordsStoredInClearTextNotificationLabel,
                                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
                        }
                        jdbcDetailsPanel.add(panel14,
                            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

                        //======== panel12 ========
                        {
                            panel12.setOpaque(false);
                            panel12.setLayout(new GridBagLayout());
                            ((GridBagLayout) panel12.getLayout()).columnWidths =
                                new int[] {0, 205, 15, 0, 205, 0, 0};
                            ((GridBagLayout) panel12.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout) panel12.getLayout()).columnWeights =
                                new double[] {1.0, 1.0, 0.0, 1.0, 1.0, 1.0, 1.0E-4};
                            ((GridBagLayout) panel12.getLayout()).rowWeights =
                                new double[] {0.0, 1.0E-4};

                            //---- usernameLabel ----
                            usernameLabel.setFont(UIManager.getFont("Label.font"));
                            usernameLabel
                                .setText(Localizer.localize("UI", "ImportRecordsUsernameLabel"));
                            panel12.add(usernameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.EAST, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- usernameTextField ----
                            usernameTextField.setMaximumSize(new Dimension(150, 2147483647));
                            usernameTextField.setFont(UIManager.getFont("TextField.font"));
                            panel12.add(usernameTextField,
                                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- passwordLabel ----
                            passwordLabel.setFont(UIManager.getFont("Label.font"));
                            passwordLabel
                                .setText(Localizer.localize("UI", "ImportRecordsPasswordLabel"));
                            panel12.add(passwordLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.EAST, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- passwordPasswordField ----
                            passwordPasswordField.setMaximumSize(new Dimension(150, 2147483647));
                            passwordPasswordField.setFont(UIManager.getFont("PasswordField.font"));
                            panel12.add(passwordPasswordField,
                                new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));
                        }
                        jdbcDetailsPanel.add(panel12,
                            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

                        //======== panel8 ========
                        {
                            panel8.setOpaque(false);
                            panel8.setLayout(new GridBagLayout());
                            ((GridBagLayout) panel8.getLayout()).columnWidths =
                                new int[] {0, 0, 0, 0};
                            ((GridBagLayout) panel8.getLayout()).rowHeights = new int[] {0, 0, 0};
                            ((GridBagLayout) panel8.getLayout()).columnWeights =
                                new double[] {0.0, 1.0, 0.0, 1.0E-4};
                            ((GridBagLayout) panel8.getLayout()).rowWeights =
                                new double[] {0.0, 0.0, 1.0E-4};

                            //---- selectJDBCDriverFileLabel ----
                            selectJDBCDriverFileLabel.setFont(UIManager.getFont("Label.font"));
                            selectJDBCDriverFileLabel.setText(
                                Localizer.localize("UI", "ImportRecordsSelectJDBCDriverFileLabel"));
                            panel8.add(selectJDBCDriverFileLabel,
                                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                                    new Insets(0, 0, 5, 5), 0, 0));

                            //---- jdbcDriverPathTextField ----
                            jdbcDriverPathTextField.setFont(UIManager.getFont("TextField.font"));
                            panel8.add(jdbcDriverPathTextField,
                                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 5, 5), 0, 0));

                            //---- browseJDBCDriverFileButton ----
                            browseJDBCDriverFileButton.setFocusPainted(false);
                            browseJDBCDriverFileButton.setFont(UIManager.getFont("Button.font"));
                            browseJDBCDriverFileButton.setIcon(new ImageIcon(getClass().getResource(
                                "/com/ebstrada/formreturn/manager/ui/icons/folder_page_white.png")));
                            browseJDBCDriverFileButton.addActionListener(new ActionListener() {
                                @Override public void actionPerformed(ActionEvent e) {
                                    browseJDBCDriverFileButtonActionPerformed(e);
                                }
                            });
                            browseJDBCDriverFileButton
                                .setText(Localizer.localize("UI", "BrowseButtonText"));
                            panel8.add(browseJDBCDriverFileButton,
                                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                                    new Insets(0, 0, 5, 0), 0, 0));

                            //---- jdbcURLLabel ----
                            jdbcURLLabel.setFont(UIManager.getFont("Label.font"));
                            jdbcURLLabel
                                .setText(Localizer.localize("UI", "ImportRecordsJDBCURLLabel"));
                            panel8.add(jdbcURLLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- JDBCURLTextField ----
                            JDBCURLTextField.setFont(UIManager.getFont("TextField.font"));
                            panel8.add(JDBCURLTextField,
                                new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- testConnectionButton ----
                            testConnectionButton.setFocusPainted(false);
                            testConnectionButton.setFont(UIManager.getFont("Button.font"));
                            testConnectionButton.setIcon(new ImageIcon(getClass().getResource(
                                "/com/ebstrada/formreturn/manager/ui/icons/util/database_connect.png")));
                            testConnectionButton.addActionListener(new ActionListener() {
                                @Override public void actionPerformed(ActionEvent e) {
                                    testConnectionButtonActionPerformed(e);
                                }
                            });
                            testConnectionButton.setText(
                                Localizer.localize("UI", "ImportRecordsTestConnectionButtonText"));
                            panel8.add(testConnectionButton,
                                new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                                    new Insets(0, 0, 0, 0), 0, 0));
                        }
                        jdbcDetailsPanel.add(panel8,
                            new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
                    }
                    panel2.add(jdbcDetailsPanel,
                        new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

                    //======== sqlQueryPanel ========
                    {
                        sqlQueryPanel.setOpaque(false);
                        sqlQueryPanel.setFont(UIManager.getFont("Label.font"));
                        sqlQueryPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout) sqlQueryPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout) sqlQueryPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout) sqlQueryPanel.getLayout()).columnWeights =
                            new double[] {1.0, 1.0E-4};
                        ((GridBagLayout) sqlQueryPanel.getLayout()).rowWeights =
                            new double[] {1.0, 1.0E-4};
                        sqlQueryPanel.setBorder(new CompoundBorder(new TitledBorder(
                            Localizer.localize("UI", "ImportRecordsSQLQueryPanelTitle")),
                            new EmptyBorder(2, 2, 2, 2)));

                        //======== scrollPane1 ========
                        {

                            //---- sqlQueryTextArea ----
                            sqlQueryTextArea.setFont(new Font("Courier New", Font.BOLD, 13));
                            scrollPane1.setViewportView(sqlQueryTextArea);
                        }
                        sqlQueryPanel.add(scrollPane1,
                            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                    }
                    panel2.add(sqlQueryPanel,
                        new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

                    //======== panel11 ========
                    {
                        panel11.setOpaque(false);
                        panel11.setBorder(new EmptyBorder(0, 0, 5, 0));
                        panel11.setLayout(new GridBagLayout());
                        ((GridBagLayout) panel11.getLayout()).columnWidths =
                            new int[] {0, 0, 0, 0, 0, 0, 0};
                        ((GridBagLayout) panel11.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout) panel11.getLayout()).columnWeights =
                            new double[] {1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout) panel11.getLayout()).rowWeights =
                            new double[] {0.0, 1.0E-4};

                        //---- runQueryViewOutputButton ----
                        runQueryViewOutputButton.setFocusPainted(false);
                        runQueryViewOutputButton.setFont(UIManager.getFont("Button.font"));
                        runQueryViewOutputButton.setIcon(new ImageIcon(getClass().getResource(
                            "/com/ebstrada/formreturn/manager/ui/icons/sdm/table.png")));
                        runQueryViewOutputButton.addActionListener(new ActionListener() {
                            @Override public void actionPerformed(ActionEvent e) {
                                runQueryViewOutputButtonActionPerformed(e);
                            }
                        });
                        runQueryViewOutputButton.setText(
                            Localizer.localize("UI", "ImportRecordsRunQueryViewOutputButtonText"));
                        panel11.add(runQueryViewOutputButton,
                            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

                        //---- runQueryImportDataButton ----
                        runQueryImportDataButton.setFocusPainted(false);
                        runQueryImportDataButton.setFont(UIManager.getFont("Button.font"));
                        runQueryImportDataButton.setIcon(new ImageIcon(getClass().getResource(
                            "/com/ebstrada/formreturn/manager/ui/icons/sdm/table_save.png")));
                        runQueryImportDataButton.addActionListener(new ActionListener() {
                            @Override public void actionPerformed(ActionEvent e) {
                                runQueryImportDataButtonActionPerformed(e);
                            }
                        });
                        runQueryImportDataButton.setText(
                            Localizer.localize("UI", "ImportRecordsRunQueryImportDataButtonText"));
                        panel11.add(runQueryImportDataButton,
                            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                        //---- saveQueryDetailsButton ----
                        saveQueryDetailsButton.setFocusPainted(false);
                        saveQueryDetailsButton.setFont(UIManager.getFont("Button.font"));
                        saveQueryDetailsButton.setIcon(new ImageIcon(getClass()
                            .getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
                        saveQueryDetailsButton.addActionListener(new ActionListener() {
                            @Override public void actionPerformed(ActionEvent e) {
                                saveQueryDetailsButtonActionPerformed(e);
                            }
                        });
                        saveQueryDetailsButton.setText(
                            Localizer.localize("UI", "ImportRecordsSaveQueryDetailsButtonText"));
                        panel11.add(saveQueryDetailsButton,
                            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                        //---- cancelButtonJDBC ----
                        cancelButtonJDBC.setFocusPainted(false);
                        cancelButtonJDBC.setFont(UIManager.getFont("Button.font"));
                        cancelButtonJDBC.setIcon(new ImageIcon(getClass()
                            .getResource("/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
                        cancelButtonJDBC.addActionListener(new ActionListener() {
                            @Override public void actionPerformed(ActionEvent e) {
                                cancelButtonJDBCActionPerformed(e);
                                cancelButtonJDBCActionPerformed(e);
                            }
                        });
                        cancelButtonJDBC.setText(Localizer.localize("UI", "CancelButtonText"));
                        panel11.add(cancelButtonJDBC,
                            new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
                    }
                    panel2.add(panel11,
                        new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
                }
                importRecordsTabbedPane.addTab("JDBC Import (advanced)", panel2);
            }
            dialogPane.add(importRecordsTabbedPane,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(950, 565);
        setLocationRelativeTo(null);
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JTabbedPane importRecordsTabbedPane;
    private JPanel panel5;
    private JPanel filePreviewPanel;
    private JScrollPane csvPreviewScrollPane;
    private JTable csvPreviewTable;
    private JPanel panel6;
    private JLabel previewRecordsLimitedLabel;
    private JLabel csvFileMustIncludeHeaderWarningLabel;
    private JPanel buttonBar;
    private JPanel panel15;
    private JLabel separatorLabel;
    private JComboBox delimiterTypeComboBox;
    private JLabel quoteCharacterLabel;
    private JComboBox quoteTypeComboBox;
    private JPanel panel16;
    private JButton browseCSVFileButton;
    private JButton importCSVButton;
    private JButton cancelButtonCSV;
    private JPanel panel2;
    private JPanel profileManagementPanel;
    private JPanel panel13;
    private JLabel existingProfileLabel;
    private JComboBox existingProfileComboBox;
    private JButton restoreProfileButton;
    private JButton removeProfileButton;
    private JPanel panel3;
    private JLabel newProfileNameLabel;
    private JTextField profileNameTextField;
    private JButton saveAsNewProfileButton;
    private JPanel jdbcURLWizardPanel;
    private JPanel panel17;
    private JLabel serverIPLabel;
    private JTextField wizardServerIPAddressTextField;
    private JLabel portNumberLabel;
    private JTextField wizardPortNumberTextField;
    private JLabel databaseNameLabel;
    private JTextField databaseNameTextField;
    private JPanel panel19;
    private JLabel jdbcProtocolLabel;
    private JComboBox wizardProtocolComboBox;
    private JLabel newJDBCURLLabel;
    private JButton createJDBCURLButton;
    private JPanel jdbcDetailsPanel;
    private JPanel panel14;
    private JLabel useJDBCWizardNotificationLabel;
    private JLabel passwordsStoredInClearTextNotificationLabel;
    private JPanel panel12;
    private JLabel usernameLabel;
    private JTextField usernameTextField;
    private JLabel passwordLabel;
    private JPasswordField passwordPasswordField;
    private JPanel panel8;
    private JLabel selectJDBCDriverFileLabel;
    private JTextField jdbcDriverPathTextField;
    private JButton browseJDBCDriverFileButton;
    private JLabel jdbcURLLabel;
    private JTextField JDBCURLTextField;
    private JButton testConnectionButton;
    private JPanel sqlQueryPanel;
    private JScrollPane scrollPane1;
    private JTextArea sqlQueryTextArea;
    private JPanel panel11;
    private JButton runQueryViewOutputButton;
    private JButton runQueryImportDataButton;
    private JButton saveQueryDetailsButton;
    private JButton cancelButtonJDBC;
    // JFormDesigner - End of variables declaration //GEN-END:variables
}
