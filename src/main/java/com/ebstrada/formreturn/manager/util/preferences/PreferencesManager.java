package com.ebstrada.formreturn.manager.util.preferences;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.prefs.Preferences;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.export.filter.Filter;
import com.ebstrada.formreturn.manager.logic.export.image.ImageExportPreferences;
import com.ebstrada.formreturn.manager.logic.export.xml.XMLExportPreferences;
import com.ebstrada.formreturn.manager.logic.recognition.structure.PublicationRecognitionStructure;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.persistence.FieldnameDuplicatePresets;
import com.ebstrada.formreturn.manager.ui.editor.persistence.MarkAreaPresetStyle;
import com.ebstrada.formreturn.manager.ui.frame.WizardDialog;
import com.ebstrada.formreturn.manager.ui.sdm.persistence.JDBCProfile;
import com.ebstrada.formreturn.manager.ui.wizard.firstrun.FirstRunController;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.graph.SizeAttributes;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ApplicationStatePreferences;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ClientDatabasePreferences;
import com.ebstrada.formreturn.manager.util.preferences.persistence.CSVExportPreferences;
import com.ebstrada.formreturn.manager.util.preferences.persistence.FormReturnPreferences;
import com.ebstrada.formreturn.manager.util.preferences.persistence.PublicationPreferences;
import com.ebstrada.formreturn.manager.util.preferences.persistence.SoftwareUpdatePreferences;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ScannerPreferences;
import com.ebstrada.formreturn.manager.util.preferences.persistence.SwingSanePreferencesImpl;
import com.google.common.io.Files;
import com.swingsane.preferences.ISwingSanePreferences;
import com.thoughtworks.xstream.XStream;

public class PreferencesManager {

    private static File formReturnPreferencesFile;

    private static File systemPasswordFile;

    private static String homeDirectoryPath;

    private static String fontDirectoryPath;

    private static String scanDirectoryPath;

    private static String databaseDirectoryPath;

    private static PreferencesDialog preferencesDialog;

    private static FormReturnPreferences formReturnPreferences;

    private static ISwingSanePreferences swingSanePreferences;

    private static File workingDirectory;

    private static final File tmpDir = Files.createTempDir();

    public PreferencesManager() {
    }

    public static void savePreferences(XStream xstream) throws IOException {

        synchronized (PreferencesManager.class) {

            String rootNodeName = "formReturn";
            BufferedWriter out = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(formReturnPreferencesFile), "UTF-8"));
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            ObjectOutputStream oos;
            oos = xstream.createObjectOutputStream(out, rootNodeName);
            oos.writeObject(formReturnPreferences);
            oos.close();

        }

    }

    public static SoftwareUpdatePreferences getSoftwareUpdatePreferences() {
        return formReturnPreferences.getSoftwareUpdatePreferences();
    }

    public static ClientDatabasePreferences getClientDatabase() {
        return formReturnPreferences.getClientDatabase();
    }

    public static boolean getFlag(String flagName) {
        return formReturnPreferences.getFlag(flagName);
    }

    public static void setFlag(String flagName, boolean value) {
        formReturnPreferences.setFlag(flagName, value);
    }

    public static String getStringValue(String flagName) {
        return formReturnPreferences.getStringValue(flagName);
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
            throw new IllegalArgumentException(
                Localizer.localize("Util", "PreferencesSystemPasswordFileNullMessage"));
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

        if (!(systemPasswordFile.exists())) {
            return null;
        }

        StringBuilder contents = new StringBuilder();

        try {
            BufferedReader input = new BufferedReader(new FileReader(systemPasswordFile));
            try {
                contents.append(input.readLine());
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        }

        return contents.toString();

    }

    public static List<JDBCProfile> getJDBCProfiles() {
        return formReturnPreferences.getJDBCProfiles();
    }

    public static void setStringValue(String flagName, String value) {
        formReturnPreferences.setStringValue(flagName, value);
    }

    public static TableModel getDatabaseTableModel() {

        DefaultTableModel databaseTableModel = new DefaultTableModel();
        databaseTableModel
            .addColumn(Localizer.localize("Util", "PreferencesDialogDatabaseNameColumnName"));
        databaseTableModel
            .addColumn(Localizer.localize("Util", "PreferencesDialogActiveColumnName"));

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

                        File databaseLockFile = new File(
                            databaseDataDir.getAbsoluteFile() + System.getProperty("file.separator")
                                + "db.lck");

                        String databaseActiveStatus = Localizer.localize("Util", "False");
                        if (databaseLockFile.exists()) {
                            databaseActiveStatus = Localizer.localize("Util", "True");
                        }

                        databaseTableModel.addRow(new String[] {files[i], databaseActiveStatus});

                    }

                }
            }
        }

        return databaseTableModel;

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
        applicationSupportDirString = null;
        try {
            applicationSupportDirString = prefs.get(key, null);

        } catch (Exception ex) {
            try {
                Preferences.userRoot().node("com.ebstrada.formreturn").flush();
            } catch (Exception ex2) {

            }
            Misc.printStackTrace(ex);
        }

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

        formReturnPreferences = new FormReturnPreferences();

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

        fontDirectoryPath = getHomeDirectoryPath() + System.getProperty("file.separator") + "fonts";
        File fontDirectory = new File(fontDirectoryPath);
        if (!(fontDirectory.exists()) || fontDirectory.isFile()) {
            if (fontDirectory.isFile()) {
                fontDirectory.delete();
            }

            fontDirectory.mkdirs();
        }

        scanDirectoryPath = getHomeDirectoryPath() + System.getProperty("file.separator") + "scans";
        File scanDirectory = new File(scanDirectoryPath);
        if (!(scanDirectory.exists()) || scanDirectory.isFile()) {
            if (scanDirectory.isFile()) {
                scanDirectory.delete();
            }

            scanDirectory.mkdirs();
        }

        boolean isFirstRun = false;

        formReturnPreferencesFile = new File(
            getHomeDirectoryPath() + System.getProperty("file.separator") + "preferences.xml");
        if (!(formReturnPreferencesFile.exists()) || formReturnPreferencesFile.isDirectory()
            || isNewPreferencesFile) {

            if (formReturnPreferencesFile.isDirectory()) {
                formReturnPreferencesFile.delete();
            }

            // set default settings
            formReturnPreferences.setFlag("launchLocalDBOnStartup", true);
            formReturnPreferences.setFlag("connectToDBOnStartup", true);

            formReturnPreferences.setFlag("collatePDFPages", true);

            // set default page size
            Map<String, SizeAttributes> dsa = formReturnPreferences.getDefaultSizeAttributes();
            if (dsa == null || dsa.size() < 2) {
                dsa = new HashMap<String, SizeAttributes>();
                isFirstRun = true;
            }

            try {
                savePreferences(xstream);
            } catch (IOException ex) {
                Main.applicationExceptionLog
                    .error(Localizer.localize("Util", "PreferencesFileCreationErrorMessage"), ex);
            }

        }

        try {
            FileInputStream fis = new FileInputStream(formReturnPreferencesFile);
            ObjectInputStream s;
            if (xstream != null) {
                s = xstream.createObjectInputStream(new InputStreamReader(fis, "UTF-8"));
            } else if (Main.getXstream() != null) {
                s = Main.getXstream().createObjectInputStream(new InputStreamReader(fis, "UTF-8"));
            } else {
                s = com.ebstrada.formreturn.server.ServerGUI.getXstream()
                    .createObjectInputStream(new InputStreamReader(fis, "UTF-8"));
            }
            formReturnPreferences = ((FormReturnPreferences) s.readObject());


            if (s != null) {
                s.close();
            }
            if (fis != null) {
                fis.close();
            }
        } catch (IOException ex) {
            Main.applicationExceptionLog
                .error(Localizer.localize("Util", "PreferencesFileCreationErrorMessage"), ex);
        } catch (ClassNotFoundException e) {
            Main.applicationExceptionLog
                .error(Localizer.localize("Util", "PreferencesFileCreationErrorMessage"), e);
        }

        swingSanePreferences = new SwingSanePreferencesImpl(formReturnPreferences);

        if (isFirstRun) {
            firstRun();
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
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                }
            }
        }

    }

    private static void firstRun() {
        WizardDialog wizardFrame = new WizardDialog();
        FirstRunController controller = new FirstRunController();
        wizardFrame.setController(controller);
        wizardFrame.hideCancelButton();
        wizardFrame.setVisible(true);
    }

    public static Stack<File> getRecentFileStack() {
        Stack<File> recentFileStack = new Stack<File>();
        List<String> recentFilesList = null;
        try {
            recentFilesList = formReturnPreferences.getRecentFiles();
            Iterator<String> rfli = recentFilesList.iterator();
            while (rfli.hasNext()) {
                File recentFile = new File(rfli.next());
                if (recentFile.exists()) {
                    recentFileStack.push(recentFile);
                }
            }
        } catch (Exception ex) {
            formReturnPreferences.removeAllRecentFiles();
            recentFilesList = formReturnPreferences.getRecentFiles();
        }

        return recentFileStack;
    }

    public static List<String> getHiddenFields() {
        return formReturnPreferences.getHiddenFields();
    }

    public static void setHiddenFields(ArrayList<String> hiddenFields) {
        formReturnPreferences.setHiddenFields(hiddenFields);
    }

    public static void setRecentFileStack(Stack<File> recentFileStack) {
        for (int i = 0; i < recentFileStack.size(); i++) {
            formReturnPreferences.addRecentFile(((File) recentFileStack.elementAt(i)).getPath());
        }
    }

    public static List<String> getFontPaths() {
        return formReturnPreferences.getFontPaths();
    }

    public static void setFontPaths(ArrayList<String> fontPaths) {
        formReturnPreferences.setFontPaths(fontPaths);
    }

    public static void addFontPath(String fontPath) {
        formReturnPreferences.addFontPath(fontPath);
    }

    public static void removeFontPath(String fontPath) {
        formReturnPreferences.removeFontPath(fontPath);
    }

    public static SizeAttributes getDefaultSizeAttributes(int graphType, int orientation) {

        if (orientation == SizeAttributes.FORM) {
            String formSizeName = "";
            return getDefaultSizeAttributes(graphType, orientation, formSizeName);
        } else {
            String segmentSizeName = "";
            return getDefaultSizeAttributes(graphType, orientation, segmentSizeName);
        }

    }

    public static SizeAttributes getDefaultSegmentSizeAttributes() {
        Map<String, SizeAttributes> dsa = formReturnPreferences.getDefaultSizeAttributes();

        SizeAttributes sa = dsa.get("defaultSegmentSize");

        if (sa == null) {
            sa = getDefaultSizeAttributes(SizeAttributes.SEGMENT, SizeAttributes.PORTRAIT);
        }

        return sa;
    }

    public static void setDefaultSegmentSizeAttributes(
        SizeAttributes defaultSegmentSizeAttributes) {
        Map<String, SizeAttributes> dsa = formReturnPreferences.getDefaultSizeAttributes();
        dsa.remove("defaultSegmentSize");
        dsa.put("defaultSegmentSize", defaultSegmentSizeAttributes);
    }

    public static SizeAttributes getDefaultFormSizeAttributes() {
        Map<String, SizeAttributes> dsa = formReturnPreferences.getDefaultSizeAttributes();

        SizeAttributes sa = dsa.get("defaultFormSize");

        if (sa == null) {
            sa = getDefaultSizeAttributes(SizeAttributes.FORM, SizeAttributes.PORTRAIT);
        } else {
            // ugly hack to fix the broken margin bug caused by 1.4.3
	    /*
	    if ( sa.getBottomMargin() == 0 && sa.getTopMargin() == 0 && sa.getLeftMargin() == 0 && sa.getRightMargin() == 0 ) {
		PreferencesManager.setDefaultSegmentSizeAttributes(SizePresets.getPresetSize(sa.getName(), SizeAttributes.FORM, SizeAttributes.PORTRAIT));
		PreferencesManager.setDefaultSegmentSizeAttributes(SizePresets.getPresetSize(sa.getName(), SizeAttributes.SEGMENT, SizeAttributes.PORTRAIT));
		try {
		    PreferencesManager.savePreferences(xstream);
		} catch (IOException e) {
		    Misc.printStackTrace(e);
		}
	    }
	    */
        }

        return sa;
    }

    public static void setDefaultFormSizeAttributes(SizeAttributes defaultFormSizeAttributes) {
        Map<String, SizeAttributes> dsa = formReturnPreferences.getDefaultSizeAttributes();
        dsa.remove("defaultFormSize");
        dsa.put("defaultFormSize", defaultFormSizeAttributes);
    }

    public static SizeAttributes getDefaultSizeAttributes(int graphType, int orientation,
        String presetName) {
        SizeAttributes sizeAttributes = new SizeAttributes();

        if (presetName.equals("Custom") || presetName.equals("custom")) {
            return sizeAttributes;
        }

        if (graphType == SizeAttributes.FORM) {
            return formReturnPreferences.getFormSizeAttribute(presetName, orientation);
        } else if (graphType == SizeAttributes.SEGMENT) {
            return formReturnPreferences.getSegmentSizeAttribute(presetName, orientation);
        }

        return sizeAttributes;

    }

    public static ApplicationStatePreferences getApplicationState() {
        return formReturnPreferences.getApplicationState();
    }

    public static void setApplicationState(
        ApplicationStatePreferences applicationStatePreferences) {
        formReturnPreferences.setApplicationState(applicationStatePreferences);
    }

    public static void showPreferencesDialog() {
        if (preferencesDialog == null) {
            preferencesDialog = new PreferencesDialog();
        }
        preferencesDialog.setVisible(true);
        preferencesDialog.resetButtons();
    }

    public static void showDatabasePreferences() {
        if (preferencesDialog == null) {
            preferencesDialog = new PreferencesDialog();
        }
        preferencesDialog.setVisible(true);
        preferencesDialog.selectDatabasePanel();
    }

    public static File getHomeDirectory() {
        return new File(homeDirectoryPath);
    }

    public static void resetRecentFiles() {
        formReturnPreferences.removeAllRecentFiles();
    }

    public static String getDatabaseDirectoryPath() {
        return databaseDirectoryPath;
    }

    public static void setDatabaseDirectoryPath(String databaseDirectoryPath) {
        PreferencesManager.databaseDirectoryPath = databaseDirectoryPath;
    }

    public static String getHomeDirectoryPath() {
        return homeDirectoryPath;
    }

    public static String getFontDirectoryPath() {
        return fontDirectoryPath;
    }

    public static File getFontDirectory() {
        return new File(fontDirectoryPath);
    }

    public static String getScanDirectoryPath() {
        return scanDirectoryPath;
    }

    public static void addJDBCProfile(JDBCProfile currentJDBCProfile) {
        formReturnPreferences.addJDBCProfile(currentJDBCProfile);
    }

    public static void removeJDBCProfile(JDBCProfile jdbcProfile) {
        formReturnPreferences.removeJDBCProfile(jdbcProfile);
    }

    public static List<MarkAreaPresetStyle> getMarkAreaPresetStyles() {
        return formReturnPreferences.getMarkAreaPresetStyles();
    }

    public static void addMarkAreaPresetStyle(MarkAreaPresetStyle maps) {
        formReturnPreferences.addMarkAreaPresetStyle(maps);
    }

    public static void removeMarkAreaPresetStyle(MarkAreaPresetStyle removeStyle) {
        formReturnPreferences.removeMarkAreaPresetStyle(removeStyle);
    }

    public static File getWorkingDirectory() {
        workingDirectory = new File(
            getHomeDirectory().getPath() + System.getProperty("file.separator") + "working");
        if (workingDirectory.exists() && workingDirectory.isDirectory()) {
            return workingDirectory;
        } else {
            try {
                workingDirectory.mkdirs();
                return workingDirectory;
            } catch (Exception ex) {
                return null;
            }
        }
    }

    public static void removeWorkingFiles() {
        if (getWorkingDirectory() != null) {
            Misc.deleteDirectory(getWorkingDirectory());
        }
    }

    public static PublicationRecognitionStructure getPublicationRecognitionStructure() {
        return formReturnPreferences.getPublicationRecognitionStructure();
    }

    public static void setPublicationRecognitionStructure(
        PublicationRecognitionStructure publicationRecognitionStructure) {
        formReturnPreferences.setPublicationRecognitionStructure(publicationRecognitionStructure);
    }

    public static FieldnameDuplicatePresets getFieldnameDupliatePresets() {
        return formReturnPreferences.getFieldnameDupliatePresets();
    }

    public static void setFieldnameDuplicatePresets(
        FieldnameDuplicatePresets fieldnameDuplicatePresets) {
        formReturnPreferences.setFieldnameDuplicatePresets(fieldnameDuplicatePresets);
    }

    public static List<String> getFormSizeNames() {
        return formReturnPreferences.getFormSizeNames();
    }

    public static List<String> getSegmentSizeNames() {
        return formReturnPreferences.getSegmentSizeNames();
    }

    public static double getDefaultSegmentBarcodeSize() {
        return formReturnPreferences.getDefaultSegmentBarcodeSize();
    }

    public static void setDefaultSegmentBarcodeSize(double defaultSegmentBarcodeSize) {
        formReturnPreferences.setDefaultSegmentBarcodeSize(defaultSegmentBarcodeSize);
    }

    public static List<SizeAttributes[]> getSegmentSizeAttributes() {
        return formReturnPreferences.getSegmentSizeAttributes();
    }

    public static List<SizeAttributes[]> getFormSizeAttributes() {
        return formReturnPreferences.getFormSizeAttributes();
    }

    public static void resetSegmentSizeAttributes() {
        formReturnPreferences.resetSegmentSizeAttributes();
    }

    public static void resetFormSizeAttributes() {
        formReturnPreferences.resetFormSizeAttributes();
    }

    public static PublicationPreferences getPublicationPreferences() {
        return formReturnPreferences.getPublicationPreferences();
    }

    public static void setPublicationPreferences(PublicationPreferences publicationPreferences) {
        formReturnPreferences.setPublicationPreferences(publicationPreferences);
    }

    public static CSVExportPreferences getCSVExportPreferences() {
        return formReturnPreferences.getCSVExportPreferences();
    }

    public static void setExportPreferences(CSVExportPreferences exportPreferences) {
        formReturnPreferences.setExportPreferences(exportPreferences);
    }

    public static ScannerPreferences getScannerPreferences() {
        return formReturnPreferences.getScannerPreferences();
    }

    public static void setScannerPreferences(ScannerPreferences scannerPreferences) {
        formReturnPreferences.setScannerPreferences(scannerPreferences);
    }

    public static void setUseCJKFont(Boolean useCJKFont) {
        formReturnPreferences.setUseCJKFont(useCJKFont);
    }

    public static boolean getUseCJKFont() {
        return formReturnPreferences.getUseCJKFont();
    }

    public static ImageExportPreferences getImageExportPreferences() {
        return formReturnPreferences.getImageExportPreferences();
    }

    public static XMLExportPreferences getXMLExportPreferences() {
        return formReturnPreferences.getXMLExportPreferences();
    }

    public static ArrayList<Filter> getExportFilterPreferences() {
        return formReturnPreferences.getExportFilterPreferences();
    }

    public static void resetCSVExportPreferences() {
        formReturnPreferences.resetCSVExportPreferences();
    }

    public static void resetImageExportPreferences() {
        formReturnPreferences.resetImageExportPreferences();
    }

    public static void resetXMLExportPreferences() {
        formReturnPreferences.resetXMLExportPreferences();
    }

    public static void setDefaultCSVExportPreferences(CSVExportPreferences csvExportPreferences) {
        formReturnPreferences.setDefaultCSVExportPreferences(csvExportPreferences);
    }

    public static void setDefaultImageExportPreferences(
        ImageExportPreferences imageExportPreferences) {
        formReturnPreferences.setDefaultImageExportPreferences(imageExportPreferences);
    }

    public static void setDefaultXMLExportPreferences(XMLExportPreferences xmlExportPreferences) {
        formReturnPreferences.setDefaultXMLExportPreferences(xmlExportPreferences);
    }

    public static ISwingSanePreferences getSwingSanePreferences() {
        return swingSanePreferences;
    }

    public static void cleanUp() {
        tmpDir.delete();
    }

    public static File getTempDirectory() {
        return tmpDir;
    }

}
