package com.ebstrada.formreturn.manager.persistence.xstream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.ebstrada.formreturn.manager.gef.ui.DocumentAttributes;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.persistence.JARPlugin;
import com.ebstrada.formreturn.manager.ui.editor.persistence.XSLTemplate;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("document") public class Document implements NoObfuscation {

    @XStreamAlias("version") private String version = Main.VERSION;

    @XStreamAlias("attributes") private DocumentAttributes documentAttributes;

    @XStreamAlias("pages") private Map<String, Page> pages = new HashMap<String, Page>();

    @XStreamAlias("pagenumbers") private List<String> pageNumbers = new ArrayList<String>();

    @XStreamAlias("images") private List<String> images = new ArrayList<String>();

    @XStreamAlias("fonts") private List<String> fonts = new ArrayList<String>();

    @XStreamAlias("xsl") private List<String> xsl = new ArrayList<String>();

    @XStreamAlias("jar") private List<String> jar = new ArrayList<String>();

    public DocumentAttributes getDocumentAttributes() {
        return documentAttributes;
    }

    public void setDocumentAttributes(DocumentAttributes documentAttributes) {
        this.documentAttributes = documentAttributes;
    }

    public void addPage(Page page) {
        String pageID = page.getPageAttributes().getGUID();
        pages.put(pageID, page);
        pageNumbers.add(pageID);
    }

    public String addImage(File imageFile, String workingDirectory)
        throws NoSuchAlgorithmException, IOException {

        String newFileName = "";

        // get the file extension, convert to lower case.
        String filename = imageFile.getName();
        String ext = (filename.lastIndexOf(".") == -1) ?
            null :
            filename.substring(filename.lastIndexOf(".") + 1, filename.length());

        // get the contents of the file into a byte array and run an md5
        MessageDigest digest = MessageDigest.getInstance("MD5");
        InputStream is = new FileInputStream(imageFile);
        byte[] buffer = new byte[8192];
        int read = 0;
        String md5string;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            md5string = bigInt.toString(16);
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
            }
        }

        // create a new identifier string (file length)-(md5sum).extension
        String fileSize = "" + imageFile.length();
        newFileName = fileSize + "-" + md5string + "." + ext;

        // if not in the images stack, add to the images stack and copy the file to the working images dir
        if (!(images.contains(newFileName))) {

            File imagesDirectory =
                new File(workingDirectory + System.getProperty("file.separator") + "images");
            imagesDirectory.mkdirs();

            File imageResourceFile = new File(
                workingDirectory + System.getProperty("file.separator") + "images" + System
                    .getProperty("file.separator") + newFileName);

            Misc.copyfile(imageFile.getAbsolutePath(), imageResourceFile.getAbsolutePath());

            images.add(newFileName);
        }

        // return the identifier string
        return newFileName;
    }

    public String addFont(File fontFile, String workingDirectory)
        throws NoSuchAlgorithmException, IOException {
        String newFileName = "";

        // get the file extension, convert to lower case.
        String filename = fontFile.getName();
        String ext = (filename.lastIndexOf(".") == -1) ?
            null :
            filename.substring(filename.lastIndexOf(".") + 1, filename.length());

        // get the contents of the file into a byte array and run an md5
        MessageDigest digest = MessageDigest.getInstance("MD5");
        InputStream is = new FileInputStream(fontFile);
        byte[] buffer = new byte[8192];
        int read = 0;
        String md5string;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            md5string = bigInt.toString(16);
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
            }
        }

        // create a new identifier string (file length)-(md5sum).extension
        String fileSize = "" + fontFile.length();
        newFileName = fileSize + "-" + md5string + "." + ext;

        // if not in the fonts stack, add to the fonts stack and copy the file to the working fonts dir
        if (!(fonts.contains(newFileName))) {

            File fontsDirectory =
                new File(workingDirectory + System.getProperty("file.separator") + "fonts");
            fontsDirectory.mkdirs();

            File fontResourceFile = new File(
                workingDirectory + System.getProperty("file.separator") + "fonts" + System
                    .getProperty("file.separator") + newFileName);

            Misc.copyfile(fontFile.getAbsolutePath(), fontResourceFile.getAbsolutePath());

            fonts.add(newFileName);
        }

        // return the identifier string
        return newFileName;
    }

    public void addPageAtIndex(Page page, int insertAtIndex) {
        String pageID = page.getPageAttributes().getGUID();
        pages.put(pageID, page);
        pageNumbers.add(insertAtIndex, pageID);
    }

    public Page getPageByPageNumber(int pageNumber) {
        String pageID = this.getPageIDByPageNumber(pageNumber);
        if (pageID == null) {
            return null;
        }
        return pages.get(pageID);
    }

    public String getPageIDByPageNumber(int pageNumber) {
        try {
            return (String) pageNumbers.get(pageNumber - 1);
        } catch (Exception ex) {
            return null;
        }
    }

    public int getNumberOfPages() {
        return pages.size();
    }

    public void removePageByPageNumber(int currentPageNumber) {
        pages.remove(getPageIDByPageNumber(currentPageNumber));
        pageNumbers.remove(currentPageNumber - 1);
    }

    public List<String> getImages() {
        return images;
    }

    public List<String> getFonts() {
        return fonts;
    }

    public Map<String, Page> getPages() {
        return pages;
    }

    public void removeMissingXSLFiles(ArrayList<String> guids, String workingDirectory) {

        String xslDirPath = workingDirectory + System.getProperty("file.separator") + "xsl";
        File xslDirectory = new File(xslDirPath);

        if (xslDirectory.exists() && xslDirectory.isDirectory()) {
            File[] files = xslDirectory.listFiles();
            for (File file : files) {
                if (file.isFile() && !(file.isDirectory())) {
                    String basename = file.getName().toLowerCase().replace(".xsl", "");
                    if (!(guids.contains(basename))) {
                        // can't find the file.. delete it!
                        file.delete();
                    }
                }
            }
        }

    }

    public void copyXSLFiles(String workingDirectory, ArrayList<XSLTemplate> xslTemplates)
        throws IOException {

        String xslDirPath = workingDirectory + System.getProperty("file.separator") + "xsl";
        File xslDirectory = new File(xslDirPath);

        if (xslTemplates.size() > 0 && !(xslDirectory.exists())) {
            xslDirectory.mkdirs();
            if (!(xslDirectory.exists())) {
                throw new IOException("Unnable to create xsl directory.");
            }
        }

        this.xsl = new ArrayList<String>();

        for (XSLTemplate xslTemplate : xslTemplates) {
            String xslFileName = xslTemplate.getTemplateGUID() + ".xsl";
            File toFile = new File(
                xslDirectory.getCanonicalFile() + System.getProperty("file.separator")
                    + xslFileName);
            if (!(toFile.exists())) {
                File fromFile = xslTemplate.getFile();
                FileUtils.copyFile(fromFile, toFile);
            }
            this.xsl.add(xslFileName);
        }

    }

    public List<String> getXSLTemplates() {
        return this.xsl;
    }

    public List<String> getJARPlugins() {
        return jar;
    }

    public void removeMissingJARFiles(ArrayList<String> guids, String workingDirectory) {
        String jarDirPath = workingDirectory + System.getProperty("file.separator") + "jar";
        File jarDirectory = new File(jarDirPath);

        if (jarDirectory.exists() && jarDirectory.isDirectory()) {
            File[] files = jarDirectory.listFiles();
            for (File file : files) {
                if (file.isFile() && !(file.isDirectory())) {
                    String basename = file.getName().toLowerCase().replace(".jar", "");
                    if (!(guids.contains(basename))) {
                        // can't find the file.. delete it!
                        file.delete();
                    }
                }
            }
        }
    }

    public void copyJARFiles(String workingDirectory, ArrayList<JARPlugin> jarPlugins)
        throws IOException {
        String jarDirPath = workingDirectory + System.getProperty("file.separator") + "jar";
        File jarDirectory = new File(jarDirPath);

        if (jarPlugins.size() > 0 && !(jarDirectory.exists())) {
            jarDirectory.mkdirs();
            if (!(jarDirectory.exists())) {
                throw new IOException("Unnable to create jar directory.");
            }
        }

        this.jar = new ArrayList<String>();

        for (JARPlugin jarPlugin : jarPlugins) {
            String jarFileName = jarPlugin.getPluginGUID() + ".jar";
            File toFile = new File(
                jarDirectory.getCanonicalFile() + System.getProperty("file.separator")
                    + jarFileName);
            if (!(toFile.exists())) {
                File fromFile = jarPlugin.getFile();
                FileUtils.copyFile(fromFile, toFile);
            }
            this.jar.add(jarFileName);
        }
    }

}
