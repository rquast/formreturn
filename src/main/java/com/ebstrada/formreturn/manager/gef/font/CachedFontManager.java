package com.ebstrada.formreturn.manager.gef.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.dialog.SplashDialog;
import com.ebstrada.formreturn.manager.ui.model.SortedComboBoxModel;

import javax.swing.DefaultComboBoxModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.font.Font2D;

import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class CachedFontManager {

    /*
     * FSTYPE_INSTALLABLE_EMBEDDING
     * Fonts with no fsType bit set may be embedded and
     * permanently installed on the remote system by an application.
     *
     * FSTYPE_RESTRICTED_LICENSE_EMBEDDING
     * Fonts that have only this bit set must not be modified, embedded or
     * exchanged in any manner without first obtaining permission of the font software copyright owner.
     *
     * FSTYPE_PREVIEW_AND_PRINT_EMBEDDING
     * If this bit is set, the font may be embedded and temporarily loaded on
     * the remote system. Documents containing Preview & Print fonts must be
     * opened 'read-only'; no edits can be applied to the document.
     *
     * FSTYPE_EDITABLE_EMBEDDING
     * If this bit is set, the font may be embedded but must only be installed
     * temporarily on other systems. In contrast to Preview & Print fonts,
     * documents containing editable fonts may be opened for reading,
     * editing is permitted, and changes may be saved.
     *
     * FSTYPE_NO_SUBSETTING
     * If this bit is set, the font may not be subsetted prior to embedding.
     *
     * FSTYPE_BITMAP_EMBEDDING_ONLY
     * If this bit is set, only bitmaps contained in the font may be embedded;
     * no outline data may be embedded. If there are no bitmaps
     * available in the font, then the font is unembeddable.
     *
     */

    public static final int FSTYPE_INSTALLABLE_EMBEDDING = 0x0000;
    public static final int FSTYPE_RESTRICTED_LICENSE_EMBEDDING = 0x0002;
    public static final int FSTYPE_PREVIEW_AND_PRINT_EMBEDDING = 0x0004;
    public static final int FSTYPE_EDITABLE_EMBEDDING = 0x0008;
    public static final int FSTYPE_NO_SUBSETTING = 0x0100;
    public static final int FSTYPE_BITMAP_EMBEDDING_ONLY = 0x0200;

    private transient static Log log = LogFactory.getLog(CachedFontManager.class);

    private CachedFontGroup cachedFontGroup = new CachedFontGroup();

    private transient SortedComboBoxModel cachedFontList;

    private String defaultFontDirectory;

    private transient GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

    private transient ArrayList<String> systemNames = new ArrayList<String>();
    private transient HashMap<String, Font> systemFonts = new HashMap<String, Font>();

    private transient Method registerFontMethod = null;

    private transient Method getFont2DMethod = null;

    private int invokeType = 0;

    private static final String boldNames[] =
        {"bold", "demibold", "demi-bold", "demi bold", "negreta", "demi"};
    private static final String italicNames[] = {"italic", "cursiva", "oblique", "inclined"};
    private static final String boldItalicNames[] =
        {"bolditalic", "bold-italic", "bold italic", "boldoblique", "bold-oblique", "bold oblique",
            "demibold italic", "negreta cursiva", "demi oblique"};

    public CachedFontManager(SplashDialog sp) {

        setDefaultFontDirectory(PreferencesManager.getFontDirectoryPath());

        if (sp != null) {
            sp.updateLoadingStatus(Localizer.localize("GefBase", "ScanningFontDirectoriesMessage"));
        }

        // standardised registerFont method in GraphicsEnvironment (1.6+)
        try {
            Class<?> clazz1 = ge.getClass();
            Method[] methods = clazz1.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals("registerFont")) {
                    registerFontMethod = clazz1.getMethod("registerFont", new Class[] {Font.class});
                    invokeType = 3;
                }
            }
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        }

        try {

            Class<?> c = null;

            try {
                c = Class.forName("sun.font.FontManager"); // java 1.6 and lower
            } catch (ClassNotFoundException cnfe) {
                c = Class.forName("sun.font.FontUtilities"); // java 1.7+
            }

            if (c == null) {
                throw new ClassNotFoundException();
            }

            Method[] methods = c.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals("getFont2D")) {
                    getFont2DMethod = c.getMethod("getFont2D", new Class[] {Font.class});
                }
            }
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        }

        // proprietary registerFont methods (1.5)
        if (invokeType != 3) {
            try {
                Class<?> clazz2 = Class.forName("sun.font.FontManager");
                Method[] methods = clazz2.getMethods();
                for (int i = 0; i < methods.length; i++) {
                    if (methods[i].getName().equals("registerGenericFont")) {
                        // OSX java 1.5
                        registerFontMethod = clazz2
                            .getMethod("registerGenericFont", new Class[] {sun.font.Font2D.class});
                        invokeType = 0;
                    } else if (methods[i].getName().equals("registerFont")) {
                        Class<?>[] parameterTypes = methods[i].getParameterTypes();
                        if (parameterTypes[0].getName().endsWith("Font")) {
                            // most other java 1.5
                            registerFontMethod =
                                clazz2.getMethod("registerFont", new Class[] {Font.class});
                            invokeType = 1;
                        } else {
                            registerFontMethod = clazz2
                                .getMethod("registerFont", new Class[] {sun.font.Font2D.class});
                            invokeType = 2;
                        }
                    }
                }
            } catch (Exception ex) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            }
        }

        File oldVeraFontFile = new File(
            PreferencesManager.getFontDirectoryPath() + System.getProperty("file.separator")
                + "Vera.ttf");
        if (oldVeraFontFile.exists()) {
            oldVeraFontFile.delete();
        }

        File veraFontFile = new File(
            PreferencesManager.getHomeDirectoryPath() + System.getProperty("file.separator")
                + "Vera.ttf");
        if (!(veraFontFile.exists())) {
            try {
                extract(getJarFileName(), PreferencesManager.getHomeDirectory());
            } catch (Exception ioex) {
                log.error(Localizer.localize("GefBase", "FontCopyErrorMessage"), ioex);
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ioex);
            }
        }

        File oldCyberbitFontFile = new File(
            PreferencesManager.getFontDirectoryPath() + System.getProperty("file.separator")
                + "Cyberbit.ttf");
        if (oldCyberbitFontFile.exists()) {
            oldCyberbitFontFile.delete();
        }

        File cyberbitFontFile = new File(
            PreferencesManager.getHomeDirectoryPath() + System.getProperty("file.separator")
                + "Cyberbit.ttf");
        if (!(cyberbitFontFile.exists())) {
            try {
                extract(getJarFileName(), PreferencesManager.getHomeDirectory());
            } catch (Exception ioex) {
                log.error(Localizer.localize("GefBase", "FontCopyErrorMessage"), ioex);
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ioex);
            }
        }

        File defaultFontFile =
            new File(defaultFontDirectory + System.getProperty("file.separator") + "default.ttf");
        if (!(defaultFontFile.exists())) {
            try {
                if (PreferencesManager.getUseCJKFont()) {
                    Misc.copyfile(cyberbitFontFile.getCanonicalPath(),
                        defaultFontFile.getCanonicalPath());
                } else {
                    Misc.copyfile(veraFontFile.getCanonicalPath(),
                        defaultFontFile.getCanonicalPath());
                }
            } catch (Exception ioex) {
                log.error(Localizer.localize("GefBase", "FontCopyErrorMessage"), ioex);
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ioex);
            }
        }

        readAvailableFontPSNames();

        scanFontDir(defaultFontDirectory, sp);

        detectSystemFontDir(sp);

        // run a garbage collection
        System.gc();

    }

    public void readAvailableFontPSNames() {
        Font[] fonts = ge.getAllFonts();
        for (Font font : fonts) {
            this.systemNames.add(font.getPSName());
            this.systemFonts.put(font.getPSName(), font);
        }
    }

    public static int getStyle(String fontName) {
        String s = fontName.toLowerCase();
        for (int i = 0; i < boldItalicNames.length; i++)
            if (s.indexOf(boldItalicNames[i]) != -1) {
                return 3;
            }

        for (int j = 0; j < italicNames.length; j++)
            if (s.indexOf(italicNames[j]) != -1) {
                return 2;
            }

        for (int k = 0; k < boldNames.length; k++)
            if (s.indexOf(boldNames[k]) != -1) {
                return 1;
            }
        return 0;
    }

    public void registerEmbeddedFont(String workingDirName, String embeddedFontFileName) {
        String workingFontDir = workingDirName + System.getProperty("file.separator") + "fonts";
        try {
            loadFontFromFile(workingFontDir, embeddedFontFileName);
        } catch (FileNotFoundException e) {
            Misc.printStackTrace(e);
        } catch (FontFormatException e) {
            Misc.printStackTrace(e);
        } catch (IOException e) {
            Misc.printStackTrace(e);
        }
    }

    public void resetFontPaths(List<String> fontPaths) {
        if (Main.MAC_OS_X) {
            fontPaths.add(System.getProperty("user.home") + "/Library/Fonts");
            fontPaths.add("/Library/Fonts");
            fontPaths.add("/System/Library/Fonts");
            fontPaths.add("/Network/Library/Fonts");
        } else if (Main.WINDOWS) {
            StringTokenizer pathTokens =
                new StringTokenizer(System.getProperty("java.library.path"),
                    System.getProperty("path.separator"));
            while (pathTokens.hasMoreTokens()) {
                String pathToken = pathTokens.nextToken();
                if (pathToken.toLowerCase().endsWith("system32")) {
                    String windowsFontPath =
                        pathToken.substring(0, pathToken.length() - 8) + "Fonts";
                    if (!(fontPaths.contains(windowsFontPath))) {
                        fontPaths.add(windowsFontPath);
                    }
                }
            }
        } else if (Main.LINUX) {
            fontPaths.add("/usr/share/fonts/truetype");
        }

        try {
            PreferencesManager.savePreferences(Main.getXstream());
        } catch (IOException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }
    }

    public void detectSystemFontDir(SplashDialog sp) {

        ArrayList<String> fontPaths = (ArrayList<String>) PreferencesManager.getFontPaths();

        if (fontPaths.size() <= 0) {
            resetFontPaths(fontPaths);
        }

        // scan font dirs
        for (int i = 0; i < fontPaths.size(); i++) {
            File fontDir = new File(fontPaths.get(i));
            if (fontDir.exists()) {
                scanFontDir(fontPaths.get(i), sp);
            }
        }

    }

    public String getJarFileName() {
        try {
            URI uri = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            if (!"file".equals(uri.getScheme())) {
                throw new Exception(String
                    .format(Localizer.localize("GefBase", "JarFileReadErrorMessage"),
                        uri.toString()));
            }
            return new File(uri.getSchemeSpecificPart()).getCanonicalPath();
        } catch (Exception e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }
        return null;
    }

    public void extract(String zipfile, File outputDir) {
        File currentArchive = new File(zipfile);

        byte[] buf = new byte[1024];
        ZipFile zf = null;
        FileOutputStream out = null;
        InputStream in = null;

        try {
            zf = new ZipFile(currentArchive);

            String path = "com/ebstrada/formreturn/manager/gef/font/";
            String[] filenames = {"Vera.ttf", "Cyberbit.ttf"};

            for (String filename : filenames) {
                ZipEntry entry = zf.getEntry(path + filename);
                in = zf.getInputStream(entry);
                File outFile = new File(outputDir, filename);
                Date archiveTime = new Date(entry.getTime());
                File parent = new File(outFile.getParent());

                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }

                out = new FileOutputStream(outFile);

                while (true) {
                    int nRead = in.read(buf, 0, buf.length);
                    if (nRead <= 0) {
                        break;
                    }
                    out.write(buf, 0, nRead);
                }
                out.close();
                outFile.setLastModified(archiveTime.getTime());
            }
            zf.close();

        } catch (Exception ex) {

            System.out.println(ex);

            if (zf != null) {
                try {
                    zf.close();
                } catch (IOException ioex) {
                    System.out.println(ioex);
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException ioex) {
                    System.out.println(ioex);
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioex) {
                    System.out.println(ioex);
                }
            }

        }

    }

    public void scanFontDir(String directory, SplashDialog sp) {

        File fontDirectory = new File(directory);

        if (fontDirectory.exists() && fontDirectory.isDirectory()) {
            String[] files = fontDirectory.list();

            if (files != null) {
                for (int i = 0; i < files.length; i++) {

                    // this try/catch block is here incase the fonts aren't read correctly
                    // and throw an exception
                    try {

                        File file =
                            new File(directory + System.getProperty("file.separator") + files[i]);

                        if (file.isDirectory()) {
                            scanFontDir(file.getCanonicalPath(), sp);
                        } else if (file.getName().toLowerCase().endsWith(".ttf")) {

                            if (sp != null) {
                                sp.updateLoadingStatus(String.format(
                                    Localizer.localize("GefBase", "LoadingFontStatusMessage"),
                                    files[i]));
                            }

                            loadFontFromFile(directory, files[i]);

                        }

                    } catch (Exception ex) {
                        com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                        Misc.showErrorMsg(null, String
                            .format(Localizer.localize("GefBase", "FontLoadErrorMessage"),
                                ex.getMessage(), ex.toString()));
                        log.error(ex);
                    }

                }
            }
        }
    }

    public void loadFontFromFile(String directory, String filename)
        throws FileNotFoundException, FontFormatException, IOException {

        Font font = null;

        CachedFont cf = null;

        try {
            cf = new CachedFont(directory, filename);
        } catch (Exception ex) {
            Misc.printStackTrace(ex);
            return;
        }

        if (cf == null || getCachedFont(cf.getStyle(), cf.getFamily()) != null) {
            return; // already loaded or cannot read.
        }

        int fsType = cf.getFsType();

        if (fsType == FSTYPE_RESTRICTED_LICENSE_EMBEDDING || fsType == (
            FSTYPE_RESTRICTED_LICENSE_EMBEDDING + 1)) {
            return; // will not load fonts with embedded licensing restrictions.
        }

        String postScriptName = cf.getPostScriptName();

        if (systemNames.contains(postScriptName)) {
            font = systemFonts.get(postScriptName);
        } else {
            try {
                font = Font.createFont(Font.TRUETYPE_FONT,
                    new FileInputStream(directory + File.separator + filename));
                registerFont(font);
            } catch (Exception ex) {
                // ignore... don't back up logs with this message.
                // Misc.printStackTrace(ex);
                return; // something went wrong when trying to register the font
            }
        }

        if (font != null && !(font.getFontName().trim().equalsIgnoreCase("new"))) {
            cf.setFont(font);
            cachedFontGroup.addCachedFont(cf);
        }

    }

    public void registerFont(Font font) throws FontAlreadyRegisteredException {

        if (systemNames.contains(font.getPSName())) {
            return;
        }

        if (cachedFontGroup.getCachedFontPostScriptNames().containsKey(font.getPSName())) {
            throw new FontAlreadyRegisteredException();
        }

        try {
            switch (invokeType) {
                case 0:
                    sun.font.Font2D f2d1 = (Font2D) getFont2DMethod.invoke(null, font);
                    registerFontMethod.invoke(null, new Object[] {f2d1});
                    break;
                case 3:
                    registerFontMethod.invoke(ge, new Object[] {font});
                    break;
                case 1:
                    registerFontMethod.invoke(null, new Object[] {font});
                    break;
                case 2:
                    sun.font.Font2D f2d2 = (Font2D) getFont2DMethod.invoke(null, font);
                    registerFontMethod.invoke(null, new Object[] {f2d2});
                    break;
                default:
            }
        } catch (IllegalArgumentException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        } catch (IllegalAccessException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        } catch (InvocationTargetException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }

    }

    public Font getFont(String fontName) {
        return getCachedFont(fontName).getFont();
    }

    public Font getDefaultFont() {

        CachedFont cf = null;

        cf = this.getCachedFontByFilename("default.ttf");

        if (cf == null) {
            cf = getCachedFont(Font.PLAIN, "Arial");
        }

        if (cf == null) {
            cf = getCachedFont(Font.PLAIN, "Microsoft Sans Serif");
        }

        if (cf == null) {
            cf = getCachedFont(Font.PLAIN, "Verdana");
        }

        if (cf == null) {
            cf = getCachedFont(Font.PLAIN, "Tahoma");
        }

        if (cf == null) {
            cf = getCachedFont(Font.PLAIN, "Times New Roman");
        }

        Font defaultFont = cf.getFont();

        return defaultFont;
    }

    public CachedFont getCachedFontByFilename(String fontFileName) {
        return cachedFontGroup.getCachedFontByFilename(fontFileName);
    }

    public CachedFont getCachedFont(String fontName) {
        return cachedFontGroup.getCachedFont(fontName);
    }

    public CachedFont getCachedFont(int style, String family) {
        return cachedFontGroup.getCachedFont(style, family);
    }

    public CachedFontFamily getCachedFontFamily(String family) {
        return cachedFontGroup.getCachedFontFamily(family);
    }

    public DefaultComboBoxModel getCachedFontList() {
        cachedFontList = new SortedComboBoxModel();
        Map<String, CachedFontFamily> cachedFontFamilies = cachedFontGroup.getCachedFontFamilies();
        Iterator cffi = cachedFontFamilies.values().iterator();
        while (cffi.hasNext()) {
            CachedFontFamily cachedFontFamily = (CachedFontFamily) cffi.next();
            String fontFamilyName = cachedFontFamily.getLocalizedFamilyName();
            cachedFontList.addElement(fontFamilyName);
        }
        return cachedFontList;
    }


    public String getDefaultFontDirectory() {
        return defaultFontDirectory;
    }

    public void setDefaultFontDirectory(String defaultFontDirectory) {
        this.defaultFontDirectory = defaultFontDirectory;
    }

    public void replaceSystemFont(File file) throws Exception {

        File defaultFontFile =
            new File(defaultFontDirectory + System.getProperty("file.separator") + "default.ttf");

        Misc.copyfile(file.getCanonicalPath(), defaultFontFile.getCanonicalPath());

        if (!(defaultFontFile.exists())) {
            throw new Exception();
        }

    }

    public void restoreDefaultSystemFont() {

        File veraFontFile = new File(
            PreferencesManager.getHomeDirectoryPath() + System.getProperty("file.separator")
                + "Vera.ttf");
        if (!(veraFontFile.exists())) {
            try {
                extract(getJarFileName(), new File(defaultFontDirectory));
            } catch (Exception ioex) {
                log.error(Localizer.localize("GefBase", "FontCopyErrorMessage"), ioex);
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ioex);
            }
        }

        File defaultFontFile =
            new File(defaultFontDirectory + System.getProperty("file.separator") + "default.ttf");

        defaultFontFile.delete();

        if (!(defaultFontFile.exists())) {
            try {
                Misc.copyfile(veraFontFile.getCanonicalPath(), defaultFontFile.getCanonicalPath());
            } catch (Exception ioex) {
                log.error(Localizer.localize("GefBase", "FontCopyErrorMessage"), ioex);
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ioex);
            }
        }

    }

    public void restoreCJKSystemFont() {

        File cjkFontFile = new File(
            PreferencesManager.getHomeDirectoryPath() + System.getProperty("file.separator")
                + "Cyberbit.ttf");
        if (!(cjkFontFile.exists())) {
            try {
                extract(getJarFileName(), new File(defaultFontDirectory));
            } catch (Exception ioex) {
                log.error(Localizer.localize("GefBase", "FontCopyErrorMessage"), ioex);
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ioex);
            }
        }

        File defaultFontFile =
            new File(defaultFontDirectory + System.getProperty("file.separator") + "default.ttf");

        defaultFontFile.delete();

        if (!(defaultFontFile.exists())) {
            try {
                Misc.copyfile(cjkFontFile.getCanonicalPath(), defaultFontFile.getCanonicalPath());
            } catch (Exception ioex) {
                log.error(Localizer.localize("GefBase", "FontCopyErrorMessage"), ioex);
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ioex);
            }
        }

    }

    public String getCachedFontMappingInfo() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String mappings = "";
        ObjectOutputStream oos = null;
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(baos, "UTF-8"));
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            oos = Main.getInstance().getXstream().createObjectOutputStream(out);
            oos.writeObject(cachedFontGroup);


        } catch (Exception ex) {

        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                }
            }
        }

        mappings += baos.toString();

        return mappings;
    }


    public CachedFontFamily getLocalizedCachedFontFamily(String localizedFontFamily) {
        return cachedFontGroup.getLocalizedCachedFontFamily(localizedFontFamily);
    }

    public CachedFont getLocalizedCachedFont(int style, String fontFamily) {
        return cachedFontGroup.getLocalizedCachedFont(style, fontFamily);
    }

    public String getLocalizedCachedFontFamilyName(String familyName) {
        return cachedFontGroup.getLocalizedCachedFontFamilyName(familyName);
    }

}
