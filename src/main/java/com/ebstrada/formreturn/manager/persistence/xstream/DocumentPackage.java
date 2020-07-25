package com.ebstrada.formreturn.manager.persistence.xstream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.base.Layer;
import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigImage;
import com.ebstrada.formreturn.manager.gef.presentation.FigSegment;
import com.ebstrada.formreturn.manager.gef.presentation.FigText;
import com.ebstrada.formreturn.manager.gef.ui.DocumentAttributes;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.RandomGUID;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;

public class DocumentPackage {

    private File packageFile;

    private Document document;

    private String GUID;

    private String workingDirName;

    private JGraph graph;

    public DocumentPackage() throws Exception {
        initialize();
    }

    public void save(File packageFile, JGraph graph) throws Exception {
        this.packageFile = packageFile;
        this.graph = graph;
        save();
    }

    private void addZipEntryFile(File zipEntryFile, ZipOutputStream zos, String path,
        boolean isCompressed) throws Exception {
        ZipEntry ze = new ZipEntry(path + zipEntryFile.getName());
        long modTime = (new java.util.Date()).getTime();
        ze.setTime(modTime);
        if (isCompressed) {
            ze.setMethod(ZipEntry.DEFLATED);
        } else {
            ze.setMethod(ZipEntry.STORED);
        }
        CRC32 crc = new CRC32();
        byte[] data = getBytes(zipEntryFile);
        if (data != null) {
            crc.update(data);
            ze.setSize(data.length);
        } else {
            ze.setMethod(ZipEntry.STORED);
            ze.setSize(0);
        }
        ze.setCrc(crc.getValue());
        ze.setCompressedSize(-1);
        zos.putNextEntry(ze);
        if (data != null) {
            zos.write(data, 0, data.length);
        }
        zos.closeEntry();
    }

    private byte[] getBytes(File inputFile) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        InputStream is = new BufferedInputStream(new FileInputStream(inputFile));
        byte[] buffer = new byte[4096];
        int r = 0;
        while ((r = is.read(buffer, 0, 4096)) > 0) {
            os.write(buffer, 0, r);
        }
        is.close();
        os.close();
        return os.toByteArray();
    }

    public void save() throws Exception {

        // remove any resource that aren't linked to figs before save
        removeUnlinkedResources();

        // 1. save the document xml file to the working dir
        DocumentAttributes documentAttributes = getDocument().getDocumentAttributes();
        documentAttributes
            .newGUID(); // create a unique file save stamp.. a new document guid each time saved.

        String rootNodeName = "formReturn";

        if (documentAttributes.getDocumentType() == DocumentAttributes.FORM) {
            rootNodeName += "Form";
        } else if (documentAttributes.getDocumentType() == DocumentAttributes.SEGMENT) {
            rootNodeName += "Segment";
        }

        File documentFile =
            new File(getWorkingDirName() + System.getProperty("file.separator") + "document.xml");

        BufferedWriter out =
            new BufferedWriter(new OutputStreamWriter(new FileOutputStream(documentFile), "UTF-8"));
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        getGraph().getEditor().preSave();
        ObjectOutputStream oos =
            Main.getInstance().getXstream().createObjectOutputStream(out, rootNodeName);
        oos.writeObject(document);
        getGraph().getEditor().postSave();
        oos.close();
        getGraph().postSave();

        // 2. create the zip file
        FileOutputStream fileOutputStream = new FileOutputStream(packageFile);
        ZipOutputStream zos = new ZipOutputStream(fileOutputStream);
        addZipEntryFile(documentFile, zos, "", true);

        List images = document.getImages();
        for (Iterator it = images.iterator(); it.hasNext(); ) {
            addZipEntryFile(new File(
                    getWorkingDirName() + System.getProperty("file.separator") + "images" + System
                        .getProperty("file.separator") + (String) it.next()), zos,
                "images" + System.getProperty("file.separator"), true);
        }

        List fonts = document.getFonts();
        for (Iterator it = fonts.iterator(); it.hasNext(); ) {
            addZipEntryFile(new File(
                    getWorkingDirName() + System.getProperty("file.separator") + "fonts" + System
                        .getProperty("file.separator") + (String) it.next()), zos,
                "fonts" + System.getProperty("file.separator"), true);
        }

        List xsl = document.getXSLTemplates();
        if (xsl != null) {
            for (Iterator it = xsl.iterator(); it.hasNext(); ) {
                addZipEntryFile(new File(
                        getWorkingDirName() + System.getProperty("file.separator") + "xsl" + System
                            .getProperty("file.separator") + (String) it.next()), zos,
                    "xsl" + System.getProperty("file.separator"), true);
            }
        }

        zos.close();
        fileOutputStream.flush();
        fileOutputStream.close();

        Globals.showStatus(String
            .format(Localizer.localize("UI", "WroteFileStatusMessage"), packageFile.getPath()));

    }

    public void removeUnlinkedResources() {

        // clone the images array
        List imagesClone = ((List) ((ArrayList) getDocument().getImages()).clone());

        // clone the fonts array
        List fontsClone = ((List) ((ArrayList) getDocument().getFonts()).clone());

        Map pages = (Map) getDocument().getPages();
        Collection pagesCollection = pages.values();
        for (Iterator pageIterator = pagesCollection.iterator(); pageIterator.hasNext(); ) {
            Page page = (Page) pageIterator.next();
            List figs = page.getFigs();
            if (figs != null) {
                for (Iterator figIterator = figs.iterator(); figIterator.hasNext(); ) {
                    Fig fig = (Fig) figIterator.next();
                    if (fig instanceof FigSegment) {
                        List segments =
                            (List) ((FigSegment) fig).getSegmentContainer().getSegments();
                        for (Iterator segmentIterator = segments.iterator(); segmentIterator
                            .hasNext(); ) {
                            Document segment = (Document) segmentIterator.next();
                            Map segmentPages = (Map) segment.getPages();
                            Collection segmentPagesCollection = segmentPages.values();
                            for (Iterator segmentPageIterator =
                                 segmentPagesCollection.iterator(); segmentPageIterator
                                     .hasNext(); ) {
                                Page segmentPage = (Page) segmentPageIterator.next();
                                List segmentFigs = segmentPage.getFigs();
                                for (Iterator segmentFigIterator =
                                     segmentFigs.iterator(); segmentFigIterator.hasNext(); ) {
                                    Fig segmentFig = (Fig) segmentFigIterator.next();
                                    if (segmentFig instanceof FigImage) {
                                        String imageFileName =
                                            ((FigImage) segmentFig).getImageFileName();
                                        imagesClone.remove(imageFileName);
                                    } else if (segmentFig instanceof FigText) {
                                        if (((FigText) segmentFig).isEmbedded()) {
                                            String fontFileName =
                                                ((FigText) segmentFig).getFontFileName();
                                            fontsClone.remove(fontFileName);
                                        }
                                    }
                                }
                            }
                        }
                    } else if (fig instanceof FigImage) {
                        String imageFileName = ((FigImage) fig).getImageFileName();
                        imagesClone.remove(imageFileName);
                    } else if (fig instanceof FigText) {
                        if (((FigText) fig).isEmbedded()) {
                            String fontFileName = ((FigText) fig).getFontFileName();
                            fontsClone.remove(fontFileName);
                        }
                    }
                }
            }
        }

        for (Iterator imagesCloneIterator = imagesClone.iterator(); imagesCloneIterator
            .hasNext(); ) {
            String imageFileName = (String) imagesCloneIterator.next();
            getDocument().getImages().remove(imageFileName);
        }

        for (Iterator fontsCloneIterator = fontsClone.iterator(); fontsCloneIterator.hasNext(); ) {
            String fontFileName = (String) fontsCloneIterator.next();
            getDocument().getFonts().remove(fontFileName);
        }

    }

    public void close() throws Exception {
        // the contains clause is there so that we
        // never have a rouge delete happen.
        File workingDir = new File(workingDirName);
        if (workingDir.getAbsolutePath()
            .startsWith(PreferencesManager.getHomeDirectory().getAbsolutePath())) {
            deleteDirectory(new File(workingDirName));
        }
    }

    private boolean deleteDirectory(File path) {

        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }

        return (path.delete());

    }

    private void initialize() throws Exception {

        GUID = (new RandomGUID()).toString();
        workingDirName =
            getTempBaseDir() + System.getProperty("file.separator") + "working" + System
                .getProperty("file.separator") + GUID;
        File workingDirFile = new File(workingDirName);

        while (workingDirFile.exists()) {
            GUID = (new RandomGUID()).toString();
            workingDirName =
                getTempBaseDir() + System.getProperty("file.separator") + "working" + System
                    .getProperty("file.separator") + GUID;
            workingDirFile = new File(workingDirName);
        }

        workingDirFile.mkdirs();

    }

    public void open(File packageFile, JGraph graph) throws Exception {

        this.packageFile = packageFile;
        this.graph = graph;
        ZipFile packageZipFile = new ZipFile(packageFile);
        Enumeration<? extends ZipEntry> zipEntries = packageZipFile.entries();

        try {
            ZipEntry zipEntry;
            while (zipEntries.hasMoreElements()) {
                zipEntry = zipEntries.nextElement();
                if (!(zipEntry.isDirectory())) {
                    OutputStream os = new DocumentFileOutputStream(workingDirName, zipEntry);
                    if (os != null) {
                        try {
                            byte[] buffer = new byte[4096];
                            int r = 0;
                            InputStream is = packageZipFile.getInputStream(zipEntry);
                            while ((r = is.read(buffer, 0, 4096)) > -1) {
                                os.write(buffer, 0, r);
                            }
                        } catch (Exception ex) {
                            throw ex;
                        } finally {
                            os.close();
                        }
                    }
                }
            }
            verifyPackageFiles();

        } finally {
            packageZipFile.close();
        }

    }

    private class DocumentFileOutputStream extends OutputStream {

        private ZipEntry zipEntry;
        private File tempFile;
        private FileOutputStream tfos;
        private OutputStream bos;

        public DocumentFileOutputStream(String tempDirName, ZipEntry zipEntry) throws Exception {

            super();
            this.zipEntry = zipEntry;
            String fileName = zipEntry.getName();
            if (System.getProperty("file.separator").equals("/")) {
                fileName = fileName.replace('\\', '/') + "";
            }
            tempFile = new File(tempDirName, fileName);
            File parentFile = tempFile.getParentFile();
            parentFile.mkdirs();
            tfos = new FileOutputStream(tempFile);
            bos = new BufferedOutputStream(tfos);
        }

        @Override public void write(byte[] byteArray) {
            try {
                bos.write(byteArray);
            } catch (IOException ex) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            }
        }

        @Override public void write(byte[] byteArray, int offset, int length) {
            try {
                bos.write(byteArray, offset, length);
            } catch (IOException ex) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            }
        }

        public void write(int byteArray) {
            try {
                bos.write(byteArray);
            } catch (IOException ex) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            }
        }

        @Override public void close() {
            try {
                bos.close();
                tfos.close();
                if (zipEntry.getName().equals("document.xml")) {
                    FileInputStream fi = new FileInputStream(tempFile);
                    ObjectInputStream s = Main.getInstance().getXstream()
                        .createObjectInputStream(new InputStreamReader(fi, "UTF-8"));
                    setDocument((Document) s.readObject());
                    if (s != null) {
                        s.close();
                    }
                    if (fi != null) {
                        fi.close();
                    }
                }
            } catch (IOException ex) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
            }
        }
    }

    private String getTempBaseDir() throws Exception {
        return PreferencesManager.getHomeDirectory().getPath();
    }

    private void verifyPackageFiles() throws Exception {
        // TODO: 1. foreach resource in the document container, confirm that the file details and crc match, otherwise throw an exception
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return this.document;
    }

    public File getPackageFile() {
        return packageFile;
    }

    public void setPackageFile(File packageFile) {
        this.packageFile = packageFile;
    }

    public JGraph getGraph() {
        return graph;
    }

    public void setGraph(JGraph graph) {
        this.graph = graph;
    }

    public String getWorkingDirName() {
        return workingDirName;
    }

    public String getGUID() {
        return GUID;
    }


}
