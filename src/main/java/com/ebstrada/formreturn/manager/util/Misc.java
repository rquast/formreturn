package com.ebstrada.formreturn.manager.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSizeName;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.openjpa.persistence.RollbackException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import com.ebstrada.aggregation.Aggregation;
import com.ebstrada.aggregation.Rule;
import com.ebstrada.aggregation.Selection;
import com.ebstrada.aggregation.exception.ErrorFlagException;
import com.ebstrada.aggregation.exception.InvalidRulePartException;
import com.ebstrada.aggregation.exception.NoMatchException;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigBarcodeReader;
import com.ebstrada.formreturn.manager.gef.presentation.FigCheckbox;
import com.ebstrada.formreturn.manager.gef.presentation.FigSegment;
import com.ebstrada.formreturn.manager.gef.presentation.RecognitionStructureFig;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.export.ExportMap;
import com.ebstrada.formreturn.manager.logic.recognition.reader.FormReaderException;
import com.ebstrada.formreturn.manager.persistence.JPAConfiguration;
import com.ebstrada.formreturn.manager.persistence.jpa.CheckBox;
import com.ebstrada.formreturn.manager.persistence.jpa.Form;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentBarcode;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentOmr;
import com.ebstrada.formreturn.manager.persistence.jpa.GradingRule;
import com.ebstrada.formreturn.manager.persistence.jpa.IncomingImage;
import com.ebstrada.formreturn.manager.persistence.jpa.Record;
import com.ebstrada.formreturn.manager.persistence.jpa.Segment;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceText;
import com.ebstrada.formreturn.manager.persistence.xstream.Document;
import com.ebstrada.formreturn.manager.persistence.xstream.Page;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.cdm.dialog.PublicationSettingsDialog;
import com.ebstrada.formreturn.manager.ui.dialog.MessageDialog;
import com.ebstrada.formreturn.manager.ui.editor.persistence.MarkingRule;
import com.ebstrada.formreturn.manager.ui.filefilter.FilenameExtensionFilter;
import com.ebstrada.formreturn.manager.util.image.ImageUtil;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.server.preferences.ServerPreferencesManager;

public class Misc {

    private static RandomAccessFile logFile;

    private static PluginManager pluginManager;

    public Misc() {
    }

    public static void removeTransparencySlider(JColorChooser jc) throws Exception {

        AbstractColorChooserPanel[] colorPanels = jc.getChooserPanels();
        for (int i = 1; i < colorPanels.length; i++) {
            AbstractColorChooserPanel cp = colorPanels[i];

            Field f = cp.getClass().getDeclaredField("panel");
            f.setAccessible(true);

            Object colorPanel = f.get(cp);
            Field f2 = colorPanel.getClass().getDeclaredField("spinners");
            f2.setAccessible(true);
            Object spinners = f2.get(colorPanel);

            Object transpSlispinner = Array.get(spinners, 3);
            if (i == colorPanels.length - 1) {
                transpSlispinner = Array.get(spinners, 4);
            }
            Field f3 = transpSlispinner.getClass().getDeclaredField("slider");
            f3.setAccessible(true);
            JSlider slider = (JSlider) f3.get(transpSlispinner);
            slider.setEnabled(false);
            slider.setVisible(false);
            Field f4 = transpSlispinner.getClass().getDeclaredField("spinner");
            f4.setAccessible(true);
            JSpinner spinner = (JSpinner) f4.get(transpSlispinner);
            spinner.setEnabled(false);
            spinner.setVisible(false);

            Field f5 = transpSlispinner.getClass().getDeclaredField("label");
            f5.setAccessible(true);
            JLabel label = (JLabel) f5.get(transpSlispinner);
            label.setVisible(false);
        }

    }

    @SuppressWarnings("unchecked")
    public static Class registerJarFile(File jarFile, String className)
        throws MalformedURLException, ClassNotFoundException {
        URL url = jarFile.toURL();
        URL[] urls = new URL[] {url};
        ClassLoader cl = new URLClassLoader(urls);
        Class cls = cl.loadClass(className);
        return cls;
    }

    public static void printStackTrace(Exception ex) {
        printStackTrace((Throwable) ex);
    }

    public static boolean isServerInstance() {
        if (com.ebstrada.formreturn.server.Main.getInstance() != null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isA4Paper() {

        String timezone = System.getProperty("user.timezone");
        if (timezone != null && timezone.length() > 0) {
            if (System.getProperty("user.timezone").startsWith("America")) {
                return false;
            } else {
                return true;
            }
        }

        try {
            PrintService pservice = PrintServiceLookup.lookupDefaultPrintService();
            Object obj = pservice.getDefaultAttributeValue(Media.class);
            if (obj instanceof MediaSizeName) {
                MediaSizeName mediaSizeName = (MediaSizeName) obj;
                if (mediaSizeName.equals(MediaSizeName.ISO_A4)) {
                    return true;
                } else {
                    return false;
                }
            }

        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        }

        if (Locale.getDefault().getCountry() == "US" || Locale.getDefault().getCountry() == "CA") {
            return false;
        }

        // default to true
        return true;
    }

    public static RandomAccessFile getLogFile() throws IOException {
        File file = null;
        if (isServerInstance()) {
            file = new File(ServerPreferencesManager.getHomeDirectoryPath() + System
                .getProperty("file.separator") + "server.trace");
        } else {
            file = new File(
                PreferencesManager.getHomeDirectoryPath() + System.getProperty("file.separator")
                    + "application.trace");
        }
        if (!(file.exists())) {
            file.createNewFile();
        } else {
            if (file.length() > 200000000) {
                file.delete();
                file.createNewFile();
            }
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(file.length());
        return raf;
    }

    public static String getDateStamp() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat();
        String timeString = "v" + com.ebstrada.formreturn.manager.ui.Main.VERSION + " build "
            + com.ebstrada.formreturn.manager.ui.Main.buildNumber + " - " + format.format(date);
        String memoryString = "Free memory: " + Runtime.getRuntime().freeMemory() + "\n";
        memoryString += "Max memory: " + Runtime.getRuntime().maxMemory() + "\n";
        memoryString += "Total memory: " + Runtime.getRuntime().totalMemory() + "\n";
        return "----------------------------------------\n" + timeString
            + "\n----------------------------------------\n" + memoryString;
    }

    public static void printStackTrace(Throwable throwable) {

        Throwable cause = throwable.getCause();

        if (logFile == null) {
            try {
                logFile = getLogFile();
            } catch (IOException e) {
            }
        }

        if (!(isServerInstance())) {
            System.err.print(getDateStamp());
            System.err.print(getStackTrace(throwable));
        }

        if (cause != null && !(isServerInstance())) {
            System.err.print(getStackTrace(cause));
        }

        if (logFile == null) {
            return;
        }

        try {
            logFile.writeBytes(getDateStamp());
            logFile.writeBytes(getStackTrace(throwable));
        } catch (IOException e) {
        }

        if (cause != null) {
            try {
                logFile.writeBytes(getStackTrace(cause));
            } catch (IOException e) {
            }
        }

        if (throwable instanceof org.apache.openjpa.persistence.PersistenceException) {
            String message = throwable.getMessage();
            Main.getInstance().setDatabaseStatusDisconnected(message);
        }

    }

    public static String getStackTrace(Throwable th) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        th.printStackTrace(pw);
        pw.flush();
        sw.flush();
        String str = "";
        if (th.getMessage() != null) {
            str += th.getMessage() + "\n";
        }
        str += sw.toString();
        pw.close();
        try {
            sw.close();
        } catch (IOException e) {
        }
        return str;
    }

    public static boolean checkOverwriteFile(File checkFile, Component parent) {

        if (checkFile.exists()) {

            SimpleDateFormat formatter =
                new SimpleDateFormat("dd/MM/yyyy hh:mma", Locale.getDefault());

            Object[] options =
                {Localizer.localize("Util", "Yes"), Localizer.localize("Util", "No")};
            Date existTime = new Date(checkFile.lastModified());

            String msg = Localizer.localize("Util", "FileNameConflictMessage") + "\n";
            msg += "\n" + String
                .format(Localizer.localize("Util", "FileNameConflictFileName"), checkFile.getName())
                + "\n" + String
                .format(Localizer.localize("Util", "FileNameConflictExistingFileName"),
                    formatter.format(existTime));
            msg +=
                " (" + checkFile.length() + " " + Localizer.localize("Util", "Bytes") + ")" + "\n\n"
                    + Localizer.localize("Util", "FileNameConflictConfirmOverwriteMessage");

            int result = JOptionPane
                .showOptionDialog(parent, msg, Localizer.localize("Util", "WarningTitle"),
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
                    options[0]);

            if (result == 0) {
                return true;
            } else {
                return false;
            }

        } else {
            return true;
        }

    }

    public static boolean showConfirmDialog(final Component parent, final String title,
        final String message, final String confirmButtonText, final String cancelButtonText) {

        String options[] = new String[] {confirmButtonText, cancelButtonText};

        int result = JOptionPane
            .showOptionDialog(parent, message, title, JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, options[0]);

        if (result == 0) {
            return true;
        } else {
            return false;
        }

    }


    public static void showExceptionMsg(final Component parent, final Exception ex) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String message = ex.getMessage();
                String caption = Localizer.localize("Util", "ErrorTitle");
                MessageDialog.showErrorMessage(parent, caption, message);
            }
        });
    }

    public static void showErrorMsg(final Component parent, final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String caption = Localizer.localize("Util", "ErrorTitle");
                MessageDialog.showErrorMessage(parent, caption, message);
            }
        });
    }

    public static void showSuccessMsg(final Component parent, final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String caption = Localizer.localize("Util", "SuccessTitle");
                MessageDialog.showSuccessMessage(parent, caption, message);
            }
        });
    }

    public static boolean deleteDirectory(File path) {

        try {
            String homeDirPath = PreferencesManager.getHomeDirectoryPath();
            if (homeDirPath == null) {
                homeDirPath = ServerPreferencesManager.getHomeDirectoryPath();
            }
            if (path.getCanonicalPath().startsWith(homeDirPath)) {
                FileUtils.deleteDirectory(path);
            }
        } catch (IOException ioex) {
            Misc.printStackTrace(ioex);
        }

        if (path.exists()) {
            return false;
        } else {
            return true;
        }

    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
            && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException(String
                .format(Localizer.localize("Util", "GetBytesFromFileCannotReadFileMessage"),
                    file.getName()));
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    public static String implode(Vector<String> capturedData, String delim) {
        String out = "";
        for (int i = 0; i < capturedData.size(); i++) {
            if (i != 0) {
                out += delim;
            }
            out += (String) capturedData.get(i);
        }
        return out;
    }

    public static String implode(Object[] ary, String delim) {
        String out = "";
        for (int i = 0; i < ary.length; i++) {
            if (i != 0) {
                out += delim;
            }
            out += (String) ary[i];
        }
        return out;
    }

    public static String implode(long[] ary, String delim) {
        String out = "";
        for (int i = 0; i < ary.length; i++) {
            if (i != 0) {
                out += delim;
            }
            out += ary[i];
        }
        return out;
    }

    public static String randomPassword() {
        java.util.Random rn = new java.util.Random();
        byte b[] = new byte[10];
        for (int i = 0; i < 8; i++) {
            b[i] = (byte) (rn.nextInt(24) + 65);
        }
        for (int i = 8; i < 10; i++) {
            b[i] = (byte) (rn.nextInt(9) + 48);
        }
        return new String(b);
    }

    public static long parseLongString(String longStr) {
        String longString = "";
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(longStr);
        while (m.find()) {
            longString += longStr.substring(m.start(), m.end());
        }
        if (longString.length() <= 0) {
            return 0;
        }
        return Long.parseLong(longString);
    }

    public static int parseIntegerString(String integerStr) {
        String integerString = "";
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(integerStr);
        while (m.find()) {
            integerString += integerStr.substring(m.start(), m.end());
        }
        if (integerString.length() <= 0) {
            return 0;
        }
        return Integer.parseInt(integerString);
    }

    public static boolean validateSQL92Identifier(String identifier) {

        // if smaller than 1 char
        if (identifier.length() <= 0) {
            return false;
        }

        // if greater than 254 char
        if (identifier.length() > 128) {
            return false;
        }

        // /\w+/  a-Z - 0-9 and underscore
        Pattern p = Pattern.compile("\\w+");
        try {
            Matcher m = p.matcher(identifier);
            m.lookingAt();
            if (m.group().equals(identifier)) {
                return true;
            } else {
                return false;
            }
        } catch (IllegalStateException ise) {
            return false;
        }

    }

    public static boolean validateFieldname(String fieldname) {

        // if smaller than 1 char
        if (fieldname.length() <= 0) {
            return false;
        }

        // if greater than 254 char
        if (fieldname.length() > 254) {
            return false;
        }

        return true;

    }

    public static void copyfile(final String srFile, final String dtFile) throws IOException {

        File f1 = new File(srFile);
        File f2 = new File(dtFile);

        InputStream in = new FileInputStream(f1);
        OutputStream out = new FileOutputStream(f2);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        out.flush();
        out.close();
        in.close();

    }

    public static File readBinaryFileURL(URL fileURL) {
        try {
            URL u = fileURL;
            URLConnection uc = u.openConnection();
            String contentType = uc.getContentType();
            int contentLength = uc.getContentLength();
            if (contentType.startsWith("text/") || contentLength == -1) {
                throw new IOException(
                    Localizer.localize("Util", "ReadBinaryFileNotABinaryFileMessage"));
            }
            InputStream raw = uc.getInputStream();
            InputStream in = new BufferedInputStream(raw);
            byte[] data = new byte[contentLength];
            int bytesRead = 0;
            int offset = 0;
            while (offset < contentLength) {
                bytesRead = in.read(data, offset, data.length - offset);
                if (bytesRead == -1)
                    break;
                offset += bytesRead;
            }
            in.close();

            if (offset != contentLength) {
                throw new IOException(String
                    .format(Localizer.localize("Util", "ReadBinaryFilePartiallyReadFileMessage"),
                        offset + "", contentLength + ""));
            }

            File outFile = new File(u.getFile());

            FileOutputStream out = new FileOutputStream(outFile);
            out.write(data);
            out.flush();
            out.close();
            return outFile;
        } catch (Exception ex) {
            //
        }
        return null;
    }


    public static String getMD5Sum(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            return number.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getHelpDirectory() {
        return System.getProperty("user.dir") + File.separator + "help";
    }

    public static void openURL(String url) {
        String osName = System.getProperty("os.name");

        try {
            if (osName.startsWith("Mac OS")) {
                Class fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] {String.class});
                openURL.invoke(null, new Object[] {url});
            } else if (osName.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else { // assume Unix or Linux
                String[] browsers =
                    {"chromium-browser", "google-chrome", "google-chrome-stable", "chrome",
                        "/opt/google/chrome/chrome", "firefox", "opera", "konqueror", "epiphany",
                        "mozilla", "netscape"};
                String browser = null;
                for (int count = 0; count < browsers.length && browser == null; count++) {
                    if (Runtime.getRuntime().exec(new String[] {"which", browsers[count]}).waitFor()
                        == 0) {
                        browser = browsers[count];
                    }
                }
                if (browser == null) {
                    throw new Exception(Localizer.localize("Util", "LaunchWebBrowserNotFound"));
                } else {
                    Runtime.getRuntime().exec(new String[] {browser, url});
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                Localizer.localize("Util", "LaunchWebBrowserErrorLaunchingBrowser") + "\n" + e
                    .getLocalizedMessage());
        }

    }

    public static String getFormPassword() {
        java.util.Random rn = new java.util.Random();
        return rn.nextInt(1000000) + "";
    }

    public static String getHexColor(Color color) {
        return "#" + Integer.toHexString(color.getRed()) + Integer.toHexString(color.getGreen())
            + Integer.toHexString(color.getBlue());
    }

    public static double aggregate(double aggregate, String[][] capturedData,
        String aggregationRule)
        throws InvalidRulePartException, NoMatchException, ErrorFlagException {

        if (aggregationRule == null || aggregationRule.length() <= 0) {
            throw new NoMatchException();
        }

        Vector<String> capturedDataVec = new Vector<String>();
        for (int i = 0; i < capturedData.length; i++) {
            for (int j = 0; j < capturedData[0].length; j++) {
                if (capturedData[i][j] != null) {
                    capturedDataVec.add(capturedData[i][j]);
                }
            }
        }

        String[] capturedDataArr = new String[capturedDataVec.size()];
        for (int i = 0; i < capturedDataArr.length; i++) {
            capturedDataArr[i] = capturedDataVec.get(i);
        }

        return aggregate(aggregate, capturedDataArr, aggregationRule);
    }

    public static double aggregate(double aggregate, String[] selectedMarks, String aggregationRule)
        throws InvalidRulePartException, NoMatchException, ErrorFlagException {
        if (aggregationRule == null || aggregationRule.length() <= 0) {
            throw new NoMatchException();
        }
        Aggregation aggregation = new Aggregation();
        Rule rule = new Rule();
        rule.parse(aggregationRule);
        aggregation.setRule(rule);
        Selection selection = new Selection(selectedMarks);
        aggregation.setSelection(selection);
        return aggregation.getAggregate();
    }

    public static Vector<String> getFormReturnFileWhitelist() {
        Vector<String> fileWhitelist = new Vector<String>();
        fileWhitelist.add("frs");
        fileWhitelist.add("FRS");
        fileWhitelist.add("frf");
        fileWhitelist.add("FRF");
        return fileWhitelist;
    }

    public static Vector<String> getImageWhilelist() {
        Vector<String> imageWhitelist = new Vector<String>();
        imageWhitelist.add("png");
        imageWhitelist.add("PNG");
        imageWhitelist.add("jpg");
        imageWhitelist.add("jpeg");
        imageWhitelist.add("JPG");
        imageWhitelist.add("JPEG");
        imageWhitelist.add("gif");
        imageWhitelist.add("GIF");
        imageWhitelist.add("tif");
        imageWhitelist.add("TIF");
        imageWhitelist.add("tiff");
        imageWhitelist.add("TIFF");
        imageWhitelist.add("pdf");
        imageWhitelist.add("PDF");
        return imageWhitelist;
    }

    public static Vector<String> getDataFileWhilelist() {
        Vector<String> dataFileWhitelist = new Vector<String>();
        dataFileWhitelist.add("csv");
        dataFileWhitelist.add("CSV");
        dataFileWhitelist.add("xml");
        dataFileWhitelist.add("XML");
        return dataFileWhitelist;
    }

    public static int compareString(String x, String y) {

        // TODO: fix this up one day to sort using natural sort algorithm, rather than java algorithm.

        return x.compareTo(y);
    }

    public static String getSizeString(int length) {

        NumberFormat formatter = new DecimalFormat("#0.0");

        int divisor = length / 1024;

        if (divisor >= 1 && divisor < 1024) {
            double dividedValue = length / 1024;
            String formattedNumber = formatter.format(dividedValue);
            return formattedNumber + " KB";
        } else if (divisor >= 1024 && divisor < (1024 * 1024)) {
            double dividedValue = length / (1024 * 1024);
            String formattedNumber = formatter.format(dividedValue);
            return formattedNumber + " MB";
        } else if (divisor >= (1024 * 1024) && divisor < (1024 * 1024 * 1024)) {
            double dividedValue = length / (1024 * 1024 * 1024);
            String formattedNumber = formatter.format(dividedValue);
            return formattedNumber + " GB";
        } else {
            return length + " " + Localizer.localize("Util", "Bytes");
        }

    }

    public static void showErrorMsg(Component parent, String message, String caption) {
        javax.swing.JOptionPane
            .showConfirmDialog(parent, message, caption, javax.swing.JOptionPane.DEFAULT_OPTION,
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    public static void closeLogFiles() {
        if (logFile != null) {
            try {
                logFile.close();
            } catch (IOException e) {
            }
        }
    }

    public static String getOffsetStr(long offset, long limit) {
        return "OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";
    }

    public static String implode(ArrayList<Long> searchIn, String delim) {
        String out = "";
        for (int i = 0; i < searchIn.size(); i++) {
            if (i != 0) {
                out += delim;
            }
            out += searchIn.get(i);
        }
        return out;
    }

    public static Map<String, String> getRecordMap(Record record) {

        Map<String, String> fields = new HashMap<String, String>();

        if (record == null) {
            return fields;
        }

        List<SourceText> stc = record.getSourceTextCollection();

        if (stc == null) {
            return fields;
        }

        for (SourceText st : stc) {
            String value = st.getSourceTextString() + "";
            fields.put(st.getSourceFieldId().getSourceFieldName(), value);
        }

        return fields;

    }

    public static String parseFields(String t, Map<String, String> recordMap) {

        Pattern p = Pattern.compile("<<[^<<]+?>>");

        String newT = t + "";

        try {
            Matcher m = p.matcher(t);
            while (m.find()) {
                String match = t.substring(m.start(), m.end());
                if (match.length() > 0 && match.length() < 255) {

                    String sourceTextFieldName = match.replace("<<", "").replace(">>", "");
                    String sourceTextValue = "";

                    if (recordMap.containsKey(sourceTextFieldName)) {
                        sourceTextValue = recordMap.get(sourceTextFieldName);
                    } else {
                        sourceTextValue = sourceTextFieldName;
                    }

                    newT = newT.replace(match, sourceTextValue);
                }
            }
        } catch (IllegalStateException ise) {
        }

        return newT;

    }

    public static Document getSelectedSegmentContainer(FigSegment figSegment,
        Map<String, String> recordMap) {

        String linkFieldName = figSegment.getSegmentContainer().getLinkFieldname();
        String defaultSelectedSegment =
            figSegment.getSegmentContainer().getDefaultSelectedSegment();

        // 1st - look at the linkFieldValue, if it is not null and has a value, see if it returns a segment.
        if (linkFieldName != null && linkFieldName.trim().length() > 0) {
            int selectedIndex = figSegment.getSegmentIndexByValue(recordMap.get(linkFieldName));
            if (selectedIndex >= 0) {
                figSegment.setSelectedSegmentIndex(selectedIndex);
                return figSegment.getSegmentContainer().getSegment(selectedIndex);
            }
        }

        // 2nd, check if the segment is random, if it is, select that one
        if (defaultSelectedSegment.equalsIgnoreCase("random")) {

            int selectedIndex = figSegment.getRandomSegmentIndex();
            figSegment.setSelectedSegmentIndex(selectedIndex);
            return figSegment.getSegmentContainer().getSegment(selectedIndex);

            // else, select the segment with the link field name.
        } else {
            int selectedIndex = figSegment.getSegmentIndexByValue(
                figSegment.getSegmentContainer().getDefaultSelectedSegment());
            figSegment.setSelectedSegmentIndex(selectedIndex);
            return figSegment.getSegmentContainer().getSegment(selectedIndex);
        }

    }

    public static Document getSelectedSegmentContainer(FigSegment figSegment,
        Map<String, String> recordMap, Segment segment) {

        String linkFieldName = figSegment.getSegmentContainer().getLinkFieldname();
        String defaultSelectedSegment =
            figSegment.getSegmentContainer().getDefaultSelectedSegment();

        // 1st - look at the linkFieldName, if it is not null and has a value, see if it returns a segment.
        if (linkFieldName != null && linkFieldName.trim().length() > 0) {
            int selectedIndex = figSegment.getSegmentIndexByValue(recordMap.get(linkFieldName));
            if (selectedIndex >= 0) {
                figSegment.setSelectedSegmentIndex(selectedIndex);
                return figSegment.getSegmentContainer().getSegment(selectedIndex);
            }
        }

        if (defaultSelectedSegment.equalsIgnoreCase("random")) {

            // use the segment record to determine which was the "random" segment chosen
            // do this by looking for the same fieldnames in each segment container and matching them to the published records.

            ArrayList<String> OMRFieldNames = new ArrayList<String>();
            ArrayList<String> OMRScoreFieldNames = new ArrayList<String>();
            ArrayList<String> BarcodeFieldNames = new ArrayList<String>();

            for (FragmentOmr fomr : segment.getFragmentOmrCollection()) {
                OMRFieldNames.add(fomr.getCapturedDataFieldName().trim());
                OMRScoreFieldNames.add(fomr.getMarkColumnName().trim());
            }
            for (FragmentBarcode fbc : segment.getFragmentBarcodeCollection()) {
                BarcodeFieldNames.add(fbc.getCapturedDataFieldName().trim());
            }

            // okay, now check these against the figSegment's fields and see if they match
            for (int i = 0; i < figSegment.getSegmentContainer().getSegments().size(); i++) {

                Document fsc = figSegment.getSegmentContainer().getSegment(i);

                for (Page page : fsc.getPages().values()) {
                    for (Fig fig : page.getFigs()) {
                        if (fig instanceof RecognitionStructureFig) {
                            if (fig instanceof FigCheckbox) {
                                FigCheckbox figCheckbox = (FigCheckbox) fig;
                                if (OMRFieldNames.contains(figCheckbox.getFieldname())) {
                                    figSegment.setSelectedSegmentIndex(i);
                                    return fsc;
                                }
                                if (OMRScoreFieldNames.contains(figCheckbox.getMarkFieldname())) {
                                    figSegment.setSelectedSegmentIndex(i);
                                    return fsc;
                                }
                            } else if (fig instanceof FigBarcodeReader) {
                                FigBarcodeReader figBarcodeReader = (FigBarcodeReader) fig;
                                if (BarcodeFieldNames.contains(figBarcodeReader.getFieldname())) {
                                    figSegment.setSelectedSegmentIndex(i);
                                    return fsc;
                                }
                            }
                        }
                    }
                }

            }

            // should never get here.
            int selectedIndex = figSegment.getRandomSegmentIndex();
            figSegment.setSelectedSegmentIndex(selectedIndex);
            return figSegment.getSegmentContainer().getSegment(selectedIndex);

            // else, select the segment with the link field name.
        } else {
            int selectedIndex = figSegment.getSegmentIndexByValue(
                figSegment.getSegmentContainer().getDefaultSelectedSegment());
            figSegment.setSelectedSegmentIndex(selectedIndex);
            return figSegment.getSegmentContainer().getSegment(selectedIndex);
        }

    }

    public static Segment getSegment(FormPage formPage, int barcodeOneValue) {

        for (Segment segment : formPage.getSegmentCollection()) {
            if (Misc.parseIntegerString(segment.getBarcodeOne()) == barcodeOneValue) {
                return segment;
            }
        }

        return null;
    }

    public static void printPDF(File pdfFile) throws IOException, PrinterException {

        PDDocument document = null;
        try {
            document = PDDocument.load(pdfFile);

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(document));
            if (job.printDialog()) {
                job.print();
            }

        } finally {
            if (document != null) {
                document.close();
            }
        }

    }

    public static void printPDF(ByteArrayOutputStream output) throws IOException, PrinterException {

        PDDocument document = null;
        ByteArrayInputStream bais = null;

        try {

            document = PDDocument.load(new ByteArrayInputStream(output.toByteArray()));

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(document));
            if (job.printDialog()) {
                job.print();
            }

        } finally {
            if (document != null) {
                document.close();
            }
            if (bais != null) {
                bais.close();
            }
        }
    }

    public static double parseDoubleString(String doubleStr) {
        return Double.parseDouble(doubleStr);
    }

    public static String getGrading(Double score, List<GradingRule> gradingRules,
        double totalPossibleScore) {

        TreeMap<Integer, GradingRule> sortedRules = new TreeMap<Integer, GradingRule>();

        for (GradingRule gradingRule : gradingRules) {
            int orderIndex = gradingRule.getOrderIndex();
            sortedRules.put(orderIndex, gradingRule);
        }

        for (GradingRule gradingRule : sortedRules.values()) {
            double threshold = gradingRule.getThreshold();
            int thresholdType = gradingRule.getThresholdType();
            int qualifier = gradingRule.getQualifier();
            String grade = gradingRule.getGrade();

            double calcScore = score;

            if (thresholdType == MarkingRule.THRESHOLD_IS_PERCENTAGE) {
                calcScore = (score / totalPossibleScore) * 100.0d;
            }

            switch (qualifier) {
                case MarkingRule.QUALIFIER_GREATER_THAN_OR_EQUAL_TO:
                    if (calcScore >= threshold) {
                        return grade;
                    }
                    break;
                case MarkingRule.QUALIFIER_LESS_THAN:
                    if (calcScore < threshold) {
                        return grade;
                    }
                    break;
                case MarkingRule.QUALIFIER_EQUAL_TO:
                    if (calcScore == threshold) {
                        return grade;
                    }
                    break;
            }

        }

        return "";

    }

    public static String implode(List<String> strList, String delim) {
        String out = "";
        for (int i = 0; i < strList.size(); i++) {
            if (i != 0) {
                out += delim;
            }
            out += (String) strList.get(i);
        }
        return out;
    }

    public static Configuration getFOPConfiguration() {

        DefaultConfiguration fop = new DefaultConfiguration("fop");
        DefaultConfiguration renderers = new DefaultConfiguration("renderers");
        fop.addChild(renderers);

        DefaultConfiguration renderer = new DefaultConfiguration("renderer");
        renderer.addAttribute("mime", "application/pdf");
        renderers.addChild(renderer);

        DefaultConfiguration fonts = new DefaultConfiguration("fonts");
        renderer.addChild(fonts);

        DefaultConfiguration autoDetect = new DefaultConfiguration("auto-detect");
        fonts.addChild(autoDetect);

        return fop;

    }

    public static boolean matchRegex(String regexFilter, String str) {
        if (regexFilter == null || regexFilter.length() <= 0) {
            return true;
        }
        try {
            return str.matches(regexFilter);
        } catch (Exception ex) {
            return true;
        }
    }

    public static void loadJar(File jarFile) {
        if (pluginManager == null) {
            loadPluginManager();
        }
        Misc.pluginManager.addPluginsFrom(jarFile.toURI());
    }

    public static void loadPluginManager() {
        if (Misc.pluginManager != null) {
            return;
        }
        Misc.pluginManager = PluginManagerFactory.createPluginManager();

        // TODO: remove this once we've finished testing.
        // Misc.pluginManager.addPluginsFrom(ClassURI.PLUGIN(com.ebstrada.formreturn.api.export.impl.ReportImpl.class));
    }

    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    public static long parseIncomingImageFileName(String name) {

        String changeString = "_USE_FORM_PAGE_ID_";

        if (name.toUpperCase().contains(changeString)) {
            int index = name.toUpperCase().indexOf(changeString);
            String partNumber = name.substring(index + changeString.length());
            int end = 0;
            for (int i = 0; i < partNumber.length(); i++) {
                char value = partNumber.charAt(i);
                if (value >= 48 && value <= 57) { // ASCII values of 0 to 9
                    continue;
                } else {
                    end = i;
                    break;
                }
            }
            if (end != 0) {
                return Long.parseLong(partNumber.substring(0, end));
            } else {
                return 0;
            }
        }
        return 0;
    }

    public static Map<String, String> getRecordMap(Form form) {
        return Misc.getRecordMap(form.getRecordId());
    }

    public static Map<String, String> getFullRecordMap(Form form) {
        Map<String, String> recordMap = getRecordMap(form.getRecordId());
        recordMap.put("form_id", form.getFormId() + "");
        recordMap.put("form_password", form.getFormPassword());
        recordMap.put("publication_id", form.getPublicationId().getPublicationId() + "");
        recordMap.put("timestamp", getTimestamp());
        for (FormPage formPage : form.getFormPageCollection()) {
            recordMap.put("form_page_" + formPage.getFormPageNumber() + "_id",
                formPage.getFormPageId() + "");
        }
        return recordMap;
    }

    public static String getTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeString = dateFormat.format(new Date().getTime());
        return timeString;
    }

    public static Map<String, String> getFullRecordMap(FormPage formPage) {
        Form form = formPage.getFormId();
        Map<String, String> recordMap = getRecordMap(form.getRecordId());
        recordMap.put("form_page_id", formPage.getFormPageId() + "");
        recordMap.put("form_page_number", formPage.getFormPageNumber() + "");
        recordMap.put("scanned_page_number", formPage.getScannedPageNumber() + "");
        recordMap.put("processed_time", formPage.getProcessedTime() + "");
        recordMap.put("timestamp", getTimestamp());
        recordMap.put("form_id", form.getFormId() + "");
        recordMap.put("form_password", form.getFormPassword());
        recordMap.put("publication_id", form.getPublicationId().getPublicationId() + "");
        return recordMap;
    }

    public static HashMap<String, String> getSourceData(EntityManager entityManager,
        Record record) {
        Query sourceTextQuery = entityManager.createNativeQuery(
            "SELECT RECORD_ID, SOURCE_TEXT_STRING, SOURCE_FIELD_NAME FROM SOURCE_TEXT LEFT JOIN SOURCE_FIELD ON SOURCE_TEXT.SOURCE_FIELD_ID = SOURCE_FIELD.SOURCE_FIELD_ID WHERE RECORD_ID = "
                + record.getRecordId() + " ORDER BY ORDER_INDEX ASC");
        List<Object[]> stResultList = sourceTextQuery.getResultList();

        List<String> hiddenFields = PreferencesManager.getHiddenFields();
        if (hiddenFields == null) {
            hiddenFields = new ArrayList<String>();
        }

        HashMap<String, String> sourceData = new HashMap<String, String>();
        for (Object[] stobjs : stResultList) {
            String fieldName = (String) stobjs[2];
            if (hiddenFields.contains(fieldName)) {
                continue;
            }
            String value = (String) stobjs[1];
            sourceData.put(fieldName, value);
        }
        return sourceData;
    }

    public static void getCapturedData(FormPage formPage, ExportMap exportMap, int rowNumber,
        HashMap<String, Double> marks) {

        for (Segment segment : formPage.getSegmentCollection()) {

            for (FragmentOmr fragmentOmr : segment.getFragmentOmrCollection()) {

                if (fragmentOmr == null) {
                    continue;
                }

                String[] capturedData = null;
                if (fragmentOmr.getInvalidated() <= 0) {
                    List<CheckBox> cbc = fragmentOmr.getCheckBoxCollection();
                    if (cbc != null && cbc.size() > 0) {
                        Vector<String> capturedValues = new Vector<String>();
                        for (CheckBox cb : cbc) {
                            if (cb.getCheckBoxMarked() > 0) {
                                capturedValues.add(cb.getCheckBoxValue());
                            }
                        }
                        capturedData = new String[capturedValues.size()];
                        for (int i = 0; i < capturedData.length; ++i) {
                            capturedData[i] = capturedValues.get(i);
                        }
                    }

                    if (capturedData == null) {
                        capturedData = fragmentOmr.getCapturedData();
                    }
                } else {
                    FormReaderException fre =
                        new FormReaderException((int) fragmentOmr.getErrorType());
                    fre.setCapturedDataFieldName(fragmentOmr.getCapturedDataFieldName());
                    capturedData = new String[] {"!!ERROR!!", fre.getErrorTitle()};
                }

                String fieldName = fragmentOmr.getCapturedDataFieldName();
                String capturedString = fragmentOmr.getCapturedString();
                double mark = fragmentOmr.getMark();
                int fieldNameOrderIndex = (int) fragmentOmr.getOrderIndex();

                if (capturedData != null) {
                    exportMap.addData(fieldName, Misc.implode(capturedData, ","), rowNumber,
                        fieldNameOrderIndex, 0);
                } else {
                    exportMap.addData(fieldName, capturedString, rowNumber, fieldNameOrderIndex, 0);
                }

                if (marks != null) {
                    marks.put(fieldName, new Double(mark));
                }

            }

            for (FragmentBarcode fragmentBarcode : segment.getFragmentBarcodeCollection()) {
                if (fragmentBarcode == null) {
                    continue;
                }
                String fieldName = fragmentBarcode.getCapturedDataFieldName();
                String capturedString = fragmentBarcode.getBarcodeValue();
                int fieldNameOrderIndex = (int) fragmentBarcode.getOrderIndex();
                exportMap.addData(fieldName, capturedString, rowNumber, fieldNameOrderIndex, 0);
            }

        }

    }

    public static void uploadImage(final JPAConfiguration jpaConfiguration, final File file,
        final TemplateFormPageID tfpid, final Component component) throws IOException {

        long pageCount = 1;
        try {
            pageCount = ImageUtil.getNumberOfPagesInTiff(file);
        } catch (Exception e) {
            Misc.showErrorMsg(component, e.getMessage());
            return;
        }
        final long numberOfPagesInFile = pageCount;

        final byte[] imageData = Misc.getBytesFromFile(file);

        Thread th = new Thread() {
            public void run() {

                EntityManager entityManager = null;

                try {

                    entityManager = jpaConfiguration.getEntityManager();
                    if (entityManager == null) {
                        return;
                    }

                    entityManager.getTransaction().begin();
                    entityManager.flush();
                    IncomingImage incomingImage = new IncomingImage();
                    incomingImage.setCaptureTime(new Timestamp(System.currentTimeMillis()));
                    incomingImage.setIncomingImageData(imageData);
                    incomingImage.setIncomingImageName(file.getName());
                    incomingImage.setNumberOfPages(numberOfPagesInFile);
                    incomingImage.setMatchStatus((short) 0);

                    if (tfpid.getFormPageId() <= 0) {
                        long formPageId = Misc.parseIncomingImageFileName(file.getName());
                        if (formPageId > 0) {
                            incomingImage.setAssignToFormPageId(formPageId);
                        }
                    } else {
                        incomingImage.setAssignToFormPageId(tfpid.getFormPageId());
                    }

                    entityManager.persist(incomingImage);
                    entityManager.getTransaction().commit();

                    if (component != null) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                String msg = Localizer
                                    .localize("UI", "ProcessingQueueImageUploadSuccessMessage");
                                Misc.showSuccessMsg(Main.getInstance(), msg);
                            }
                        });
                    }

                } catch (final Exception ex) {
                    if (component != null) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                String msg = Localizer
                                    .localize("UI", "ProcessingQueueImageUploadFailureMessage")
                                    + "\n" + ex.getLocalizedMessage();
                                Misc.showErrorMsg(Main.getInstance(), msg);
                            }
                        });
                    }
                    Misc.printStackTrace(ex);
                } finally {

                    try {
                        if ((entityManager != null) && entityManager.isOpen() && !(entityManager
                            .getTransaction().isActive())) {
                            entityManager.getTransaction().begin();
                            entityManager.createNativeQuery("CALL CHECK_INCOMING_IMAGES()")
                                .executeUpdate();
                            entityManager.getTransaction().commit();
                        }
                    } catch (Exception ex) {
                        Misc.printStackTrace(ex);
                    }

                    if ((entityManager != null) && entityManager.isOpen()) {
                        entityManager.close();
                    }
                }

            }
        };

        th.start();

    }

    public static File getUploadImageFile() throws IOException {

        Vector<String> imageWhitelist = Misc.getImageWhilelist();

        File file = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();
        filter.addExtension("png");
        filter.addExtension("jpg");
        filter.addExtension("jpeg");
        filter.addExtension("gif");
        filter.addExtension("tif");
        filter.addExtension("tiff");
        filter.addExtension("pdf");

        FileDialog fd = new FileDialog(Main.getInstance(),
            Localizer.localize("UI", "ProcessingQueueUploadImageDialogTitle"), FileDialog.LOAD);
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

        fd.setDirectory(lastDir.getCanonicalPath());
        fd.setModal(true);
        fd.setVisible(true);
        if (fd.getFile() != null) {
            String filename = fd.getFile();
            file = new File(fd.getDirectory() + filename);
            if (file.isDirectory()) {
                return null;
            }
            try {
                Globals.setLastDirectory(file.getCanonicalPath());
            } catch (IOException ldex) {
            }
        } else {
            return null;
        }

        String fileName = fd.getFile();
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());

        if (!(imageWhitelist.contains(extension.trim()))) {
            Misc.showErrorMsg(Main.getInstance(),
                Localizer.localize("UI", "ProcessingQueueInvalidImageFileMessage"));
            return null;
        }

        return file;

    }

    public static void uploadImageFolder(final JPAConfiguration jpaConfiguration, final File file,
        final TemplateFormPageID tfpid, final Component component) {

        final Vector<String> imageWhitelist = getImageWhilelist();

        if (file.exists() && file.isDirectory()) {

            final String[] files = file.list();

            if (files != null) {

                Thread th = new Thread() {
                    public void run() {

                        EntityManager entityManager = null;

                        try {

                            entityManager = jpaConfiguration.getEntityManager();
                            if (entityManager == null) {
                                return;
                            }
                            entityManager.getTransaction().begin();

                            for (int i = 0; i < files.length; i++) {
                                try {
                                    File imageFile = new File(file.getCanonicalPath() + System
                                        .getProperty("file.separator") + files[i]);
                                    if (imageFile != null && imageFile.exists() && !(imageFile
                                        .isDirectory())) {

                                        String extension = files[i]
                                            .substring(files[i].lastIndexOf('.') + 1,
                                                files[i].length());

                                        if (imageWhitelist.contains(extension.trim())) {

                                            byte[] imageData = getBytesFromFile(imageFile);

                                            long pageCount = 1;
                                            try {
                                                pageCount =
                                                    ImageUtil.getNumberOfPagesInTiff(imageFile);
                                            } catch (Exception e) {
                                            }
                                            final long numberOfPagesInFile = pageCount;

                                            if (entityManager != null) {
                                                IncomingImage incomingImage = new IncomingImage();
                                                incomingImage.setCaptureTime(
                                                    new Timestamp(System.currentTimeMillis()));
                                                incomingImage.setIncomingImageData(imageData);
                                                incomingImage
                                                    .setIncomingImageName(imageFile.getName());
                                                incomingImage.setNumberOfPages(numberOfPagesInFile);
                                                incomingImage.setMatchStatus((short) 0);

                                                if (tfpid.getFormPageId() <= 0) {
                                                    long formPageId =
                                                        Misc.parseIncomingImageFileName(
                                                            file.getName());
                                                    if (formPageId > 0) {
                                                        incomingImage
                                                            .setAssignToFormPageId(formPageId);
                                                    }
                                                } else {
                                                    incomingImage.setAssignToFormPageId(
                                                        tfpid.getFormPageId());
                                                }

                                                entityManager.persist(incomingImage);
                                            }

                                        }

                                    }
                                } catch (IOException e1) {
                                }
                            }

                            entityManager.getTransaction().commit();

                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    String msg = Localizer
                                        .localize("UI", "ProcessingQueueImageUploadSuccessMessage");
                                    Misc.showSuccessMsg(Main.getInstance(), msg);
                                }
                            });

                        } catch (final Exception ex) {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    String msg = Localizer
                                        .localize("UI", "ProcessingQueueImageUploadFailureMessage")
                                        + "\n" + ex.getLocalizedMessage();
                                    Misc.showErrorMsg(Main.getInstance(), msg);
                                }
                            });
                        } finally {
                            try {
                                if ((entityManager != null) && entityManager.isOpen()
                                    && !(entityManager.getTransaction().isActive())) {
                                    entityManager.getTransaction().begin();
                                    entityManager.createNativeQuery("CALL CHECK_INCOMING_IMAGES()")
                                        .executeUpdate();
                                    entityManager.getTransaction().commit();
                                }
                            } catch (Exception ex) {
                                Misc.printStackTrace(ex);
                            }

                            if ((entityManager != null) && entityManager.isOpen()) {
                                entityManager.close();
                            }
                        }

                    }
                };

                th.start();

            }
        }

    }

    public static File getUploadImageFolder() throws IOException {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle(Localizer.localize("UI", "ProcessingQueueSelectImagesFolderTitle"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.rescanCurrentDirectory();
        if (chooser.showDialog(Main.getInstance(), Localizer.localize("UI", "ChooseButtonText"))
            == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else {
            return null;
        }
    }

    public static String getTimestampPrefixedFilename(String fileStr) {
        File file = new File(fileStr);
        String filename = file.getName();
        String path = file.getPath();
        String trimmedPath = path.substring(0, path.length() - filename.length() - 1);
        String output = trimmedPath + File.separator + getTimestamp() + "_" + filename;
        return output;
    }

}
