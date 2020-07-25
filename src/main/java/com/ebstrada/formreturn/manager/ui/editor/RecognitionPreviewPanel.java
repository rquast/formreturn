package com.ebstrada.formreturn.manager.ui.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.io.FileUtils;

import au.com.southsky.jfreesane.SanePasswordProvider;

import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.ui.dialog.BarcodeDetectionDetailsDialog;
import com.ebstrada.formreturn.manager.ui.dialog.LoadingDialog;
import com.ebstrada.formreturn.manager.ui.dialog.MarkDetectionDetailsDialog;
import com.ebstrada.formreturn.manager.ui.dialog.MultiplePagesDialog;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.publish.Graphics2DDocumentExporter;
import com.ebstrada.formreturn.manager.logic.publish.PDFDocumentExporter;
import com.ebstrada.formreturn.manager.logic.recognition.reader.FormReader;
import com.ebstrada.formreturn.manager.logic.recognition.reader.FormReaderException;
import com.ebstrada.formreturn.manager.logic.recognition.structure.BarcodeRecognitionData;
import com.ebstrada.formreturn.manager.logic.recognition.structure.FragmentRecognitionData;
import com.ebstrada.formreturn.manager.logic.recognition.structure.OMRMatrix;
import com.ebstrada.formreturn.manager.logic.recognition.structure.PublicationRecognitionStructure;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.dialog.PreviewDataFieldsDialog;
import com.ebstrada.formreturn.manager.ui.editor.panel.RecognitionPanelController;
import com.ebstrada.formreturn.manager.ui.filefilter.FilenameExtensionFilter;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.image.ImageUtil;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ApplicationStatePreferences;
import com.ebstrada.formreturn.scanner.client.SwingSaneFrame;
import com.ebstrada.formreturn.scanner.client.ICAPanel;
import com.ebstrada.formreturn.scanner.client.ScannerClientDialog;
import com.ebstrada.formreturn.scanner.client.TwainPanel;
import com.swingsane.business.auth.SwingSanePasswordProvider;
import com.swingsane.business.scanning.IScanService;
import com.swingsane.business.scanning.ScanEvent;
import com.swingsane.business.scanning.ScanServiceImpl;
import com.swingsane.gui.controller.IScanEventHandler;
import com.swingsane.preferences.IPreferredDefaults;
import com.swingsane.preferences.PreferredDefaultsImpl;

public class RecognitionPreviewPanel extends JPanel implements IScanEventHandler {

    private static final long serialVersionUID = 1L;

    private Float realZoom = 1.0f;
    private Float testImageRealZoom = 1.0f;

    private File previewImageFile = null;
    private BufferedImage testPreviewImage = null;
    private BufferedImage detectionPreviewImage = null;
    private BufferedImage binarizedPreviewImage = null;

    private FormReader testFormReader;
    private FormReader formReader;

    private boolean formDetectBarcode = false;
    private boolean formDetectSegment = false;
    private boolean formDetectfragment = false;
    private boolean formDetectMarks = false;

    private boolean testDetectBarcode = false;
    private boolean testDetectSegment = false;
    private boolean testDetectfragment = false;
    private boolean testDetectMarks = false;

    private boolean finishedLoading = false;

    private RecognitionPanelController recognitionPanelController;

    private JGraph graph;

    private PublicationRecognitionStructure publicationRecognitionStructure;

    private SwingWorker<File, Void> browseTestFileWorker;

    private Graphics2DDocumentExporter g2dde;

    private SwingSaneFrame swingSaneFrame;

    public RecognitionPreviewPanel(JGraph _graph, PageAttributes pageAttributes,
        PublicationRecognitionStructure publicationRecognitionStructure) {

        graph = _graph;
        this.publicationRecognitionStructure = publicationRecognitionStructure;
        recognitionPanelController = Main.getInstance().getRecognitionPanelController(this);
        initComponents();

        recognitionPreviewTabbedPane
            .setTitleAt(0, Localizer.localize("UI", "PrintPreviewTabTitle"));
        recognitionPreviewTabbedPane.setTitleAt(1, Localizer.localize("UI", "TestPreviewTabTitle"));

        scrollPane1.getVerticalScrollBar().setUnitIncrement(30);
        scrollPane1.getVerticalScrollBar().setBlockIncrement(90);
        scrollPane2.getVerticalScrollBar().setUnitIncrement(30);
        scrollPane2.getVerticalScrollBar().setBlockIncrement(90);

        scrollPane1.getHorizontalScrollBar().setUnitIncrement(30);
        scrollPane1.getHorizontalScrollBar().setBlockIncrement(90);
        scrollPane2.getHorizontalScrollBar().setUnitIncrement(30);
        scrollPane2.getHorizontalScrollBar().setBlockIncrement(90);

        try {
            preRender();
        } catch (Exception e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
            Misc.showErrorMsg(Main.getInstance(), e.getMessage());
        }

        Main.getInstance().getScansWatcher().setScanEventHandler(this);
        Main.getInstance().getScansWatcher().setPreviewComponent(this);

        scanImageButton.setVisible(true);

    }


    public void applySettingsButtonActionPerformed(final Double deskewThreshold,
        final int luminanceCutOff, final int markThreshold, final int fragmentPadding,
        final boolean performDeskew) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                PublicationRecognitionStructure publicationRecognitionStructure =
                    getPublicationRecognitionStructure();
                publicationRecognitionStructure.setDeskewThreshold(deskewThreshold);
                publicationRecognitionStructure.setLuminanceCutOff(luminanceCutOff);
                publicationRecognitionStructure.setMarkThreshold(markThreshold);
                publicationRecognitionStructure.setFragmentPadding(fragmentPadding);
                publicationRecognitionStructure.setPerformDeskew(performDeskew);

                binarizedPreviewImage = null;
                formReader = null;
                testFormReader = null;
                System.gc();

                if (previewImageFile != null) {
                    loadPreviewImage(previewImageFile);
                } else {
                    repaint();
                }

            }
        });
    }

    public void detectBarcodesCheckBoxItemStateChanged(ItemEvent e,
        final JCheckBox detectBarcodesCheckBox) {
        if (recognitionPreviewTabbedPane.getSelectedIndex() == 0) {
            formDetectBarcode = detectBarcodesCheckBox.isSelected();
            if (detectionPreviewImage != null) {
                repaint();
            }
        } else if (recognitionPreviewTabbedPane.getSelectedIndex() == 1) {
            testDetectBarcode = detectBarcodesCheckBox.isSelected();
            if (testPreviewImage != null) {
                repaint();
            }
        }
    }

    public void detectSegmentsCheckBoxItemStateChanged(ItemEvent e,
        final JCheckBox detectSegmentsCheckBox) {
        if (recognitionPreviewTabbedPane.getSelectedIndex() == 0) {
            formDetectSegment = detectSegmentsCheckBox.isSelected();
            if (detectionPreviewImage != null) {
                repaint();
            }
        } else if (recognitionPreviewTabbedPane.getSelectedIndex() == 1) {
            testDetectSegment = detectSegmentsCheckBox.isSelected();
            if (testPreviewImage != null) {
                repaint();
            }
        }
    }

    public void detectFragmentsCheckBoxItemStateChanged(ItemEvent e,
        final JCheckBox detectFragmentsCheckBox) {
        if (recognitionPreviewTabbedPane.getSelectedIndex() == 0) {
            formDetectfragment = detectFragmentsCheckBox.isSelected();
            if (detectionPreviewImage != null) {
                repaint();
            }
        } else if (recognitionPreviewTabbedPane.getSelectedIndex() == 1) {
            testDetectfragment = detectFragmentsCheckBox.isSelected();
            if (testPreviewImage != null) {
                repaint();
            }
        }
    }

    public void detectMarkedCharactersCheckBoxItemStateChanged(ItemEvent e,
        final JCheckBox detectMarkedCharactersCheckBox) {
        if (recognitionPreviewTabbedPane.getSelectedIndex() == 0) {
            formDetectMarks = detectMarkedCharactersCheckBox.isSelected();
            if (detectionPreviewImage != null) {
                repaint();
            }
        } else if (recognitionPreviewTabbedPane.getSelectedIndex() == 1) {
            testDetectMarks = detectMarkedCharactersCheckBox.isSelected();
            if (testPreviewImage != null) {
                repaint();
            }
        }
    }

    private void loadPreviewImage(File _previewImageFile) {

        clearImage();

        try {

            int pageCount = ImageUtil.getNumberOfPagesInTiff(_previewImageFile);
            int selectedPageNumber = 1;

            if (pageCount > 1) {
                MultiplePagesDialog mpd = new MultiplePagesDialog(Main.getInstance(), pageCount);
                mpd.setModal(true);
                mpd.setVisible(true);

                selectedPageNumber = mpd.getSelectedPageNumber();
            }

            testPreviewImage =
                ImageUtil.blurImage(ImageUtil.readImage(_previewImageFile, selectedPageNumber));
            testPreviewImage.flush();

            previewImageFile = _previewImageFile;
            repaint();

        } catch (Exception ex) {

            final String message = String
                .format(Localizer.localize("UI", "UnableToReadFileMessage"),
                    _previewImageFile.toString());

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String caption = Localizer.localize("UI", "ErrorTitle");
                    javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
                        javax.swing.JOptionPane.DEFAULT_OPTION,
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                    testFormReader = null;
                    testPreviewImage = null;
                }
            });

            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);

        }

        setFinishedLoading(true);

    }

    public void renderTestImage(Graphics2D g) {

        if (testPreviewImage != null && binarizedPreviewImage != null) {

            g.setColor(Color.darkGray);
            g.fill(new Rectangle2D.Double(0, 0, testPageImageLabel.getWidth(),
                testPageImageLabel.getHeight()));

            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            double x = (testPageImageLabel.getWidth() / 2) - (
                (binarizedPreviewImage.getWidth() * testImageRealZoom) / 2);
            double y = (testPageImageLabel.getHeight() / 2) - (
                (binarizedPreviewImage.getHeight() * testImageRealZoom) / 2);
            AffineTransform at = AffineTransform.getTranslateInstance(x, y);
            at.scale(testImageRealZoom, testImageRealZoom);

            g.drawRenderedImage(binarizedPreviewImage, at);

            if (testFormReader != null) {
                testFormReader.setDrawDetectBarcode(testDetectBarcode);
                testFormReader.setDrawDetectfragment(testDetectfragment);
                testFormReader.setDrawDetectSegment(testDetectSegment);
                testFormReader.setDrawDetectMarks(testDetectMarks);
                testFormReader.setOverlayZoom(testImageRealZoom);
                testFormReader.drawDetectionOverlay(g, (int) x, (int) y);
            }

        }

    }

    public void renderDetectionImage() throws Exception {
        if (detectionPreviewImage == null) {

            PageAttributes currentPageAttributes = graph.getPageAttributes();

            detectionPreviewImage = new BufferedImage(currentPageAttributes.getFullWidth() * 2,
                currentPageAttributes.getFullHeight() * 2, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = (Graphics2D) detectionPreviewImage.createGraphics();
            graphics.scale(2.0d, 2.0d);

            if (g2dde == null) {
                g2dde = new Graphics2DDocumentExporter(graph);
            }
            g2dde.export(graphics);

            graphics.dispose();

        }
    }

    public void preRender() throws Exception, FormReaderException {

        renderDetectionImage();

        if (formReader == null) {
            formReader = new FormReader(detectionPreviewImage, graph);
            FormReader.setLastOrientation(FormReader.ORIENTATION_PORTRAIT);

            PublicationRecognitionStructure publicationRecognitionStructure =
                getPublicationRecognitionStructure();

            formReader.setMarkThreshold(publicationRecognitionStructure.getMarkThreshold());
            formReader.setFragmentPadding(publicationRecognitionStructure.getFragmentPadding());
            formReader.setLuminanceThreshold(
                (short) publicationRecognitionStructure.getLuminanceCutOff());

            binarizedPreviewImage = ImageUtil.getCompatibleBufferedImage(
                formReader.processPreview(detectionPreviewImage, false, false));
        }

    }

    public void renderPreview(Graphics2D g2d) {

        try {

            preRender();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            PageAttributes currentPageAttributes = graph.getPageAttributes();

            // render the image panel image

            int panelWidth = pageImageLabel.getWidth();
            int scaledPageWidth = (int) (currentPageAttributes.getFullWidth() * realZoom);
            int panelHeight = pageImageLabel.getHeight();
            int scaledPageHeight = (int) (currentPageAttributes.getFullHeight() * realZoom);

            pageImageLabel.setPreferredSize(new Dimension(scaledPageWidth, scaledPageHeight));

            g2d.setColor(Color.darkGray);
            g2d.fill(new Rectangle2D.Double(0, 0, pageImageLabel.getWidth(),
                pageImageLabel.getHeight()));

            int x_offset = new Integer((panelWidth - scaledPageWidth) / 2);
            int y_offset = new Integer((panelHeight - scaledPageHeight) / 2);

            AffineTransform at = new AffineTransform();
            at.translate(x_offset, y_offset);
            at.scale(realZoom, realZoom);
            g2d.transform(at);

            Shape oldClip = g2d.getClip();

            g2d.clip(new Rectangle(0, 0, currentPageAttributes.getFullWidth(),
                currentPageAttributes.getFullHeight()));

            if (g2dde == null) {
                g2dde = new Graphics2DDocumentExporter(graph);
            }
            g2dde.export(g2d);

            g2d.setClip(oldClip);

            if (formReader != null) {
                formReader.setDrawDetectBarcode(formDetectBarcode);
                formReader.setDrawDetectfragment(formDetectfragment);
                formReader.setDrawDetectSegment(formDetectSegment);
                formReader.setDrawDetectMarks(formDetectMarks);
                formReader.setOverlayZoom(0.5f);
                formReader.drawDetectionOverlay(g2d, 0, 0);
            }

        } catch (FormReaderException fre) {

            final String message = fre.getErrorTitle() + ":\n" + fre.getErrorMessage();
            Misc.printStackTrace(fre);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Misc.showErrorMsg(Main.getInstance(), message);
                }
            });

        } catch (Exception e) {

            final String message = e.getLocalizedMessage();

            Misc.printStackTrace(e);

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Misc.showErrorMsg(Main.getInstance(), message);
                }
            });

        }

    }

    @Override public void repaint() {

        if (g2dde != null && g2dde.getGraphics() != null
            && recognitionPreviewTabbedPane.getSelectedIndex() == 0) {

            PageAttributes currentPageAttributes = graph.getPageAttributes();

            Dimension dim = new Dimension((int) (currentPageAttributes.getFullWidth() * realZoom),
                (int) (currentPageAttributes.getFullHeight() * realZoom));

            if (pageImageLabel != null) {
                pageImageLabel.setMaximumSize(dim);
                pageImageLabel.setMinimumSize(dim);
                pageImageLabel.setPreferredSize(dim);
                pageImageLabel.revalidate();
            }

        }

        if (testPreviewImage != null && recognitionPreviewTabbedPane.getSelectedIndex() == 1) {

            final LoadingDialog ld = new LoadingDialog(Main.getInstance());

            if (testFormReader == null || binarizedPreviewImage == null) {
                ld.setVisible(true);
                Main.getInstance().blockInput();
            }

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                private double testFormReaderSkewAngle;

                protected Void doInBackground() throws InterruptedException {

                    PublicationRecognitionStructure publicationRecognitionStructure =
                        getPublicationRecognitionStructure();

                    if (testFormReader == null || binarizedPreviewImage == null) {
                        testFormReader = new FormReader(testPreviewImage, graph);
                        testFormReader
                            .setMarkThreshold(publicationRecognitionStructure.getMarkThreshold());
                        testFormReader.setFragmentPadding(
                            publicationRecognitionStructure.getFragmentPadding());
                        testFormReader.setDeskewThreshold(
                            publicationRecognitionStructure.getDeskewThreshold());
                        testFormReader.setPerformDeskew(
                            (short) (publicationRecognitionStructure.isPerformDeskew() ? 1 : 0));
                        testFormReader.setLuminanceThreshold(
                            (short) publicationRecognitionStructure.getLuminanceCutOff());
                        try {
                            binarizedPreviewImage = ImageUtil.getCompatibleBufferedImage(
                                testFormReader.processPreview(testPreviewImage,
                                    publicationRecognitionStructure.isPerformDeskew(), false));
                        } catch (FormReaderException fre) {
                            binarizedPreviewImage =
                                ImageUtil.getCompatibleBufferedImage(fre.getInvalidImage());
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(fre);
                            Misc.showErrorMsg(Main.getInstance(), fre.getErrorTitle());
                        } catch (Exception ex) {
                            binarizedPreviewImage =
                                ImageUtil.getCompatibleBufferedImage(testPreviewImage);
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                            Misc.showErrorMsg(Main.getInstance(), ex.getMessage());
                        }

                    }

                    try {
                        renderDetectionImage();
                    } catch (Exception e) {
                        detectionPreviewImage = null;
                    }

                    if (testFormReader != null) {
                        testFormReaderSkewAngle = testFormReader.getBinarizedImageSkewAngle();
                    }

                    return null;
                }

                protected void done() {

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {

                            int pixelSize = testPreviewImage.getColorModel().getPixelSize();

                            NumberFormat nf = NumberFormat.getInstance();
                            nf.setMaximumFractionDigits(2);

                            imageDetailsLabel.setText(
                                "" + testPreviewImage.getWidth() + "x" + testPreviewImage
                                    .getHeight() + " (" + pixelSize + " BPP), " + String
                                    .format(Localizer.localize("UI", "ImageSkewDetails"),
                                        nf.format(testFormReaderSkewAngle)));

                            Dimension dim = new Dimension(
                                (int) (binarizedPreviewImage.getWidth() * testImageRealZoom),
                                (int) (binarizedPreviewImage.getHeight() * testImageRealZoom));
                            if (testPageImageLabel != null) {
                                testPageImageLabel.setMaximumSize(dim);
                                testPageImageLabel.setMinimumSize(dim);
                                testPageImageLabel.setPreferredSize(dim);
                            }

                            testPageImageLabel.revalidate();
                            superRepaint();

                            if (testFormReader.getProcessingError() != null) {
                                Misc.printStackTrace(testFormReader.getProcessingError());
                                Misc.showErrorMsg(Main.getInstance(),
                                    testFormReader.getProcessingError().getMessage());
                            }

                        }
                    });

                    setFinishedLoading(true);
                    if (testPageImageLabel != null) {
                        updatePanels();
                    } else {
                        recognitionPanelController.destroyPanels();
                    }

                    Main.getInstance().unblockInput();
                    ld.dispose();

                }
            };
            worker.execute();

        }

        super.repaint();

    }

    public void superRepaint() {
        super.repaint();
    }

    private void printThisPreviewButtonActionPerformed(ActionEvent e) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ByteArrayOutputStream output = null;
                try {
                    output = new ByteArrayOutputStream();
                    PDFDocumentExporter pdfd = new PDFDocumentExporter(graph,
                        graph.getDocumentPackage().getWorkingDirName(), output);
                    pdfd.createPreviewPDF(graph.getRecordMap());
                    Misc.printPDF(output);
                } catch (Exception ex) {
                    Misc.printStackTrace(ex);
                    Misc.showErrorMsg(getRootPane(), ex.getLocalizedMessage());
                } finally {
                    if (output != null) {
                        try {
                            output.flush();
                            output.close();
                        } catch (IOException e) {
                            Misc.printStackTrace(e);
                        }
                    }
                }
            }
        });

    }

    private void savePreviewPDFButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                savePreviewPDF();
            }
        });
    }

    private void savePreviewPDF() {

        File file = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();
        filter.addExtension("pdf");

        FileDialog fd = new FileDialog(Main.getInstance(),
            Localizer.localize("UI", "SavePreviewPDFDialogTitle"), FileDialog.SAVE);
        fd.setFilenameFilter(filter);
        fd.setFile(Localizer.localize("UI", "PreviewPDFFilePrefix") + ".pdf");

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
            if (!(filename.endsWith(".pdf") || filename.endsWith(".PDF"))) {
                filename += ".pdf";
            }

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

        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            PDFDocumentExporter pdfd =
                new PDFDocumentExporter(graph, graph.getDocumentPackage().getWorkingDirName(),
                    output);
            pdfd.createPreviewPDF(graph.getRecordMap());
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String message = Localizer.localize("UI", "PreviewPDFFileSavedSuccessMessage");
                    String caption = Localizer.localize("UI", "SuccessTitle");
                    javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
                        javax.swing.JOptionPane.DEFAULT_OPTION,
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }
            });
        } catch (Exception ex) {
            Misc.printStackTrace(ex);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String message = Localizer.localize("UI", "PreviewPDFFileSavedFailureMessage");
                    String caption = Localizer.localize("UI", "ErrorTitle");
                    javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
                        javax.swing.JOptionPane.DEFAULT_OPTION,
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            });
        } finally {
            if (output != null) {
                try {
                    output.flush();
                    output.close();
                } catch (IOException e) {
                    Misc.printStackTrace(e);
                }

            }
        }

    }


    private void browseTestImageButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                browseTestImage();
            }
        });
    }

    private void browseTestImage() {

        if (browseTestFileWorker != null && !(browseTestFileWorker.isDone())) {
            return;
        }

        browseTestFileWorker = new SwingWorker<File, Void>() {
            protected File doInBackground() throws InterruptedException {
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
                    Localizer.localize("UI", "LoadTestPreviewImageFileDialogTitle"),
                    FileDialog.LOAD);
                fd.setModal(true);
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
                    return null;
                }

                fd.setModal(true);
                fd.setVisible(true);
                if (fd.getFile() != null) {
                    String filename = fd.getFile();
                    file = new File(fd.getDirectory() + filename);
                    if (file.isDirectory()) {
                        return null;
                    }
                } else {
                    return null;
                }
                return file;
            }

            protected void done() {

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {

                        File file = null;
                        try {
                            file = get();
                        } catch (InterruptedException e) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                        } catch (ExecutionException e) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                        }

                        if (file != null) {
                            loadPreviewImage(file);
                        }

                    }
                });

            }

        };
        browseTestFileWorker.execute();

    }

    public void lockTabs() {

        if (recognitionPreviewTabbedPane == null) {
            return;
        }

        if (recognitionPreviewTabbedPane.getTabCount() != 2) {
            return;
        }

        recognitionPreviewTabbedPane.setEnabledAt(0, false);
        recognitionPreviewTabbedPane.setEnabledAt(1, false);

    }

    public void unlockTabs() {

        if (recognitionPreviewTabbedPane == null) {
            return;
        }

        if (recognitionPreviewTabbedPane.getTabCount() != 2) {
            return;
        }

        recognitionPreviewTabbedPane.setEnabledAt(0, true);
        recognitionPreviewTabbedPane.setEnabledAt(1, true);

    }

    public void updatePanels() {

        if (!isFinishedLoading()) {
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (recognitionPreviewTabbedPane.getSelectedIndex() == 0) {
                    if (recognitionPanelController != null) {
                        recognitionPanelController.updateAllPanels();
                        recognitionPanelController
                            .setSelectedCheckBoxes(formDetectBarcode, formDetectSegment,
                                formDetectfragment, formDetectMarks);
                    }
                } else if (recognitionPreviewTabbedPane.getSelectedIndex() == 1) {
                    if (recognitionPanelController != null) {
                        recognitionPanelController.updateAllPanels();
                        recognitionPanelController
                            .setSelectedCheckBoxes(testDetectBarcode, testDetectSegment,
                                testDetectfragment, testDetectMarks);
                    }
                }
            }
        });

    }

    public void recognitionPreviewTabbedPaneStateChanged(ChangeEvent e) {

        if (!isFinishedLoading()) {
            return;
        }

        lockTabs();

        if (recognitionPreviewTabbedPane.getSelectedIndex() == 0) {
            updatePanels();
            unlockTabs();
        } else if (recognitionPreviewTabbedPane.getSelectedIndex() == 1) {
            recognitionPanelController.destroyPanels();
            if (previewImageFile != null) {
                loadPreviewImage(previewImageFile);
            }
            unlockTabs();
        }

    }

    private void clearImageButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                clearImage();
            }
        });
    }

    private void clearImage() {
        testPreviewImage = null;
        previewImageFile = null;
        testFormReader = null;
        binarizedPreviewImage = null;
        imageDetailsLabel.setText(Localizer.localize("UI", "NoImageLoadedStatusMessage"));

        Dimension dim = new Dimension(10, 10);
        if (testPageImageLabel != null) {
            testPageImageLabel.setMaximumSize(dim);
            testPageImageLabel.setMinimumSize(dim);
            testPageImageLabel.setPreferredSize(dim);
        }
        testPageImageLabel.revalidate();

        System.gc();
        repaint();
        recognitionPanelController.destroyPanels();
    }

    private void setPreviewFieldDataButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PreviewDataFieldsDialog pdfd =
                    new PreviewDataFieldsDialog(Main.getInstance(), getGraph());
                pdfd.setModal(true);
                pdfd.setVisible(true);

                if (pdfd.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {
                    refresh();
                }
            }
        });
    }

    private void refresh() {
        Main.getInstance().getSelectedFrame().rebuildPreview();
    }

    private void zoomBoxItemStateChanged(ItemEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String unparsedString = (String) zoomBox.getSelectedObjects()[0];
                String zoomString = "";
                for (int i = 0; i < unparsedString.length(); i++) {
                    if (unparsedString.charAt(i) >= 48 && unparsedString.charAt(i) <= 57) {
                        zoomString += unparsedString.charAt(i);
                    }
                }
                realZoom = Float.parseFloat(zoomString) / 100.0f;
                repaint();
            }
        });
    }

    private void testImageZoomBoxItemStateChanged(ItemEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String unparsedString = (String) testImageZoomBox.getSelectedObjects()[0];
                String zoomString = "";
                for (int i = 0; i < unparsedString.length(); i++) {
                    if (unparsedString.charAt(i) >= 48 && unparsedString.charAt(i) <= 57) {
                        zoomString += unparsedString.charAt(i);
                    }
                }
                testImageRealZoom = Float.parseFloat(zoomString) / 100.0f;
                repaint();
            }
        });
    }

    private void zoomInLabelMouseClicked(MouseEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int selectedIndex = zoomBox.getSelectedIndex();

                if (selectedIndex < (zoomBox.getItemCount() - 1)) {
                    zoomBox.setSelectedIndex(selectedIndex + 1);
                }
            }
        });
    }

    private void testImageZoomInLabelMouseClicked(MouseEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int selectedIndex = testImageZoomBox.getSelectedIndex();

                if (selectedIndex < (testImageZoomBox.getItemCount() - 1)) {
                    testImageZoomBox.setSelectedIndex(selectedIndex + 1);
                }
            }
        });
    }

    private void zoomOutLabelMouseClicked(MouseEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int selectedIndex = zoomBox.getSelectedIndex();

                if (selectedIndex > 0) {
                    zoomBox.setSelectedIndex(selectedIndex - 1);
                }
            }
        });
    }

    private void testImageZoomOutLabelMouseClicked(MouseEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int selectedIndex = testImageZoomBox.getSelectedIndex();

                if (selectedIndex > 0) {
                    testImageZoomBox.setSelectedIndex(selectedIndex - 1);
                }
            }
        });
    }

    private void loadPreviewImage(final BufferedImage previewImage) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                clearImage();
                GraphicsDevice gd =
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                GraphicsConfiguration gc = gd.getDefaultConfiguration();
                if (gc.getColorModel() != previewImage.getColorModel()) {
                    testPreviewImage = ImageUtil.getCompatibleBufferedImage(previewImage);
                } else {
                    testPreviewImage = previewImage;
                }
                previewImageFile = null;
                repaint();
            }
        });
    }

    private void scanImageButtonActionPerformed(ActionEvent e) {

        ApplicationStatePreferences applicationState = PreferencesManager.getApplicationState();
        String classPath = System.getProperty("java.class.path");

        String[] classPathArray = classPath.split(System.getProperty("path.separator"));
        String firstPath = "";
        if (classPathArray.length > 1) {
            firstPath = classPathArray[0];
        } else {
            firstPath = classPath;
        }
        ProcessBuilder processBuilder;
        String endsWith = "lib" + System.getProperty("file.separator") + "formreturn.jar";


        ScannerClientDialog scd = null;

        if (Main.WINDOWS && !(applicationState.isUsingSaneClient())) {
            try {
                scd = new ScannerClientDialog(Main.getInstance(), new TwainPanel());
                scd.setTitle(Localizer.localize("UI", "FormReturnScannerDialogTitle"));
                scd.setModal(true);
                scd.setVisible(true);
            } catch (Exception ex) {
                if (scd != null) {
                    scd.dispose();
                }
            }
            if (scd.hasScannedImage()) {
                loadPreviewImage(scd.getBufferedImage());
            }

        } else if (Main.MAC_OS_X && !(applicationState.isUsingSaneClient())) {

            try {
                scd = new ScannerClientDialog(Main.getInstance(), new ICAPanel());
                scd.setTitle(Localizer.localize("UI", "FormReturnScannerDialogTitle"));
                scd.setModal(true);
                scd.setVisible(true);
            } catch (Exception ex) {
                if (scd != null) {
                    scd.dispose();
                }
            }
            if (scd.hasScannedImage()) {
                loadPreviewImage(scd.getBufferedImage());
            }

        } else {
            if (swingSaneFrame == null) {
                swingSaneFrame =
                    new SwingSaneFrame(Main.getInstance().getRootPane().getTopLevelAncestor());
                swingSaneFrame
                    .setApplicationName(Localizer.localize("UI", "FormReturnScannerDialogTitle"));
                swingSaneFrame.setXstream(Main.getXstream());
                swingSaneFrame.setScanEventHandler(this);
                swingSaneFrame.setPreferences(PreferencesManager.getSwingSanePreferences());
                swingSaneFrame.setScanService(getScanService());
                swingSaneFrame.setPreferredDefaults(getPreferredDefaults());
                swingSaneFrame.initialize();
            }
            swingSaneFrame.setVisible(true);
        }

    }

    private IScanService getScanService() {
        IScanService scanService = new ScanServiceImpl();
        scanService.setPasswordProvider(getPasswordProvider());
        scanService.setSaneServiceIdentity(
            PreferencesManager.getSwingSanePreferences().getApplicationPreferences()
                .getSaneServiceIdentity());
        return scanService;
    }

    private SanePasswordProvider getPasswordProvider() {
        return new SwingSanePasswordProvider(
            PreferencesManager.getSwingSanePreferences().getApplicationPreferences()
                .getSaneLogins());
    }

    private IPreferredDefaults getPreferredDefaults() {
        return new PreferredDefaultsImpl();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        recognitionPreviewTabbedPane = new JTabbedPane();
        printPreviewPanel = new JPanel();
        panel1 = new JPanel();
        zoomBox = new JComboBox();
        zoomInLabel = new JLabel();
        zoomOutLabel = new JLabel();
        setPreviewFieldDataButton = new JButton();
        printThisPreviewButton = new JButton();
        savePreviewPDFButton = new JButton();
        scrollPane1 = new JScrollPane();
        pageImageLabel = new JImageLabel();
        testPreviewPanel = new JPanel();
        panel9 = new JPanel();
        label2 = new JLabel();
        imageDetailsLabel = new JLabel();
        scanImageButton = new JButton();
        browseTestImageButton = new JButton();
        clearImageButton = new JButton();
        scrollPane2 = new JScrollPane();
        testPageImageLabel = new JImageLabel();
        panel2 = new JPanel();
        testImageZoomBox = new JComboBox();
        testImageZoomInLabel = new JLabel();
        testImageZoomOutLabel = new JLabel();

        //======== this ========
        setOpaque(false);
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {279, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

        //======== recognitionPreviewTabbedPane ========
        {
            recognitionPreviewTabbedPane.setFont(UIManager.getFont("TabbedPane.font"));
            recognitionPreviewTabbedPane.addChangeListener(new ChangeListener() {
                @Override public void stateChanged(ChangeEvent e) {
                    recognitionPreviewTabbedPaneStateChanged(e);
                }
            });

            //======== printPreviewPanel ========
            {
                printPreviewPanel.setOpaque(false);
                printPreviewPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) printPreviewPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) printPreviewPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout) printPreviewPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) printPreviewPanel.getLayout()).rowWeights =
                    new double[] {0.0, 1.0, 1.0E-4};

                //======== panel1 ========
                {
                    panel1.setOpaque(false);
                    panel1.setBorder(new EmptyBorder(3, 3, 3, 3));
                    panel1.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel1.getLayout()).columnWidths =
                        new int[] {0, 0, 0, 0, 0, 0, 0, 0};
                    ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout) panel1.getLayout()).columnWeights =
                        new double[] {0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                    //---- zoomBox ----
                    zoomBox.setModel(new DefaultComboBoxModel(
                        new String[] {"10%", "25%", "50%", "75%", "100%", "125%", "150%", "200%",
                            "250%", "350%", "500%", "700%", "1000%"}));
                    zoomBox.setSelectedIndex(4);
                    zoomBox.setFont(UIManager.getFont("ComboBox.font"));
                    zoomBox.addItemListener(new ItemListener() {
                        @Override public void itemStateChanged(ItemEvent e) {
                            zoomBoxItemStateChanged(e);
                        }
                    });
                    panel1.add(zoomBox,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                    //---- zoomInLabel ----
                    zoomInLabel.setIcon(new ImageIcon(getClass().getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/preview/zoom_in.png")));
                    zoomInLabel.addMouseListener(new MouseAdapter() {
                        @Override public void mouseClicked(MouseEvent e) {
                            zoomInLabelMouseClicked(e);
                        }
                    });
                    zoomInLabel.setToolTipText(Localizer.localize("UI", "ZoomInToolTip"));
                    panel1.add(zoomInLabel,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                    //---- zoomOutLabel ----
                    zoomOutLabel.setIcon(new ImageIcon(getClass().getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/preview/zoom_out.png")));
                    zoomOutLabel.setFont(UIManager.getFont("Label.font"));
                    zoomOutLabel.addMouseListener(new MouseAdapter() {
                        @Override public void mouseClicked(MouseEvent e) {
                            zoomOutLabelMouseClicked(e);
                        }
                    });
                    zoomOutLabel.setToolTipText(Localizer.localize("UI", "ZoomOutToolTip"));
                    panel1.add(zoomOutLabel,
                        new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                    //---- setPreviewFieldDataButton ----
                    setPreviewFieldDataButton.setFont(UIManager.getFont("Button.font"));
                    setPreviewFieldDataButton.setIcon(new ImageIcon(getClass().getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/page_white_database.png")));
                    setPreviewFieldDataButton.setFocusPainted(false);
                    setPreviewFieldDataButton.addActionListener(new ActionListener() {
                        @Override public void actionPerformed(ActionEvent e) {
                            setPreviewFieldDataButtonActionPerformed(e);
                        }
                    });
                    setPreviewFieldDataButton
                        .setText(Localizer.localize("UI", "PreviewFieldDataButtonText"));
                    panel1.add(setPreviewFieldDataButton,
                        new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                    //---- printThisPreviewButton ----
                    printThisPreviewButton.setFocusPainted(false);
                    printThisPreviewButton.setFont(UIManager.getFont("Button.font"));
                    printThisPreviewButton.setIcon(new ImageIcon(getClass().getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/preview/printer.png")));
                    printThisPreviewButton.addActionListener(new ActionListener() {
                        @Override public void actionPerformed(ActionEvent e) {
                            printThisPreviewButtonActionPerformed(e);
                        }
                    });
                    printThisPreviewButton
                        .setText(Localizer.localize("UI", "PrintRecognitionPreviewButtonText"));
                    panel1.add(printThisPreviewButton,
                        new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                    //---- savePreviewPDFButton ----
                    savePreviewPDFButton.setFocusPainted(false);
                    savePreviewPDFButton.setFont(UIManager.getFont("Button.font"));
                    savePreviewPDFButton.setIcon(new ImageIcon(getClass().getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/preview/page_white_acrobat.png")));
                    savePreviewPDFButton.addActionListener(new ActionListener() {
                        @Override public void actionPerformed(ActionEvent e) {
                            savePreviewPDFButtonActionPerformed(e);
                        }
                    });
                    savePreviewPDFButton
                        .setText(Localizer.localize("UI", "SaveRecognitionPreviewPDFButtonText"));
                    panel1.add(savePreviewPDFButton,
                        new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                }
                printPreviewPanel.add(panel1,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportBorder(null);
                    scrollPane1.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));

                    //---- pageImageLabel ----
                    pageImageLabel.setViewer(this);
                    pageImageLabel.setRenderType(JImageLabel.RENDER_PREVIEW);
                    scrollPane1.setViewportView(pageImageLabel);
                }
                printPreviewPanel.add(scrollPane1,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            recognitionPreviewTabbedPane.addTab("Print Preview", printPreviewPanel);

            //======== testPreviewPanel ========
            {
                testPreviewPanel.setOpaque(false);
                testPreviewPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) testPreviewPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) testPreviewPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                ((GridBagLayout) testPreviewPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) testPreviewPanel.getLayout()).rowWeights =
                    new double[] {0.0, 1.0, 0.0, 1.0E-4};

                //======== panel9 ========
                {
                    panel9.setOpaque(false);
                    panel9.setBorder(new EmptyBorder(3, 3, 3, 3));
                    panel9.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel9.getLayout()).columnWidths =
                        new int[] {0, 0, 0, 0, 0, 0, 0};
                    ((GridBagLayout) panel9.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout) panel9.getLayout()).columnWeights =
                        new double[] {0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout) panel9.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                    //---- label2 ----
                    label2.setIcon(new ImageIcon(getClass().getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/preview/image.png")));
                    label2.setFont(UIManager.getFont("Label.font"));
                    panel9.add(label2,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                    //---- imageDetailsLabel ----
                    imageDetailsLabel.setText("(No Image Loaded)");
                    imageDetailsLabel.setFont(UIManager.getFont("Label.font"));
                    imageDetailsLabel
                        .setText(Localizer.localize("UI", "NoImageLoadedStatusMessage"));
                    panel9.add(imageDetailsLabel,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                    //---- scanImageButton ----
                    scanImageButton.setIcon(new ImageIcon(getClass().getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/frscanner_16x16.png")));
                    scanImageButton.setVisible(false);
                    scanImageButton.setFont(UIManager.getFont("Button.font"));
                    scanImageButton.addActionListener(new ActionListener() {
                        @Override public void actionPerformed(ActionEvent e) {
                            scanImageButtonActionPerformed(e);
                        }
                    });
                    scanImageButton.setText(Localizer.localize("UI", "ScanImageButtonText"));
                    panel9.add(scanImageButton,
                        new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                    //---- browseTestImageButton ----
                    browseTestImageButton.setFont(UIManager.getFont("Button.font"));
                    browseTestImageButton.setFocusPainted(false);
                    browseTestImageButton.setIcon(new ImageIcon(getClass().getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/preview/image_add.png")));
                    browseTestImageButton.addActionListener(new ActionListener() {
                        @Override public void actionPerformed(ActionEvent e) {
                            browseTestImageButtonActionPerformed(e);
                        }
                    });
                    browseTestImageButton
                        .setText(Localizer.localize("UI", "BrowseImageButtonText"));
                    panel9.add(browseTestImageButton,
                        new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                    //---- clearImageButton ----
                    clearImageButton.setFont(UIManager.getFont("Button.font"));
                    clearImageButton.setFocusPainted(false);
                    clearImageButton.setIcon(new ImageIcon(getClass().getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/preview/image_delete.png")));
                    clearImageButton.addActionListener(new ActionListener() {
                        @Override public void actionPerformed(ActionEvent e) {
                            clearImageButtonActionPerformed(e);
                        }
                    });
                    clearImageButton.setText(Localizer.localize("UI", "ClearImageButtonText"));
                    panel9.add(clearImageButton,
                        new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                }
                testPreviewPanel.add(panel9,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

                //======== scrollPane2 ========
                {
                    scrollPane2.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
                    scrollPane2.setViewportBorder(null);

                    //---- testPageImageLabel ----
                    testPageImageLabel.setViewer(this);
                    testPageImageLabel.setRenderType(JImageLabel.RENDER_TEST_IMAGE);
                    scrollPane2.setViewportView(testPageImageLabel);
                }
                testPreviewPanel.add(scrollPane2,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

                //======== panel2 ========
                {
                    panel2.setOpaque(false);
                    panel2.setBorder(new EmptyBorder(3, 3, 3, 3));
                    panel2.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel2.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                    ((GridBagLayout) panel2.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout) panel2.getLayout()).columnWeights =
                        new double[] {0.0, 0.0, 0.0, 1.0, 1.0E-4};
                    ((GridBagLayout) panel2.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                    //---- testImageZoomBox ----
                    testImageZoomBox.setModel(new DefaultComboBoxModel(
                        new String[] {"10%", "25%", "50%", "75%", "100%", "125%", "150%", "200%",
                            "250%", "350%", "500%", "700%", "1000%"}));
                    testImageZoomBox.setSelectedIndex(4);
                    testImageZoomBox.setFont(UIManager.getFont("ComboBox.font"));
                    testImageZoomBox.addItemListener(new ItemListener() {
                        @Override public void itemStateChanged(ItemEvent e) {
                            testImageZoomBoxItemStateChanged(e);
                        }
                    });
                    panel2.add(testImageZoomBox,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                    //---- testImageZoomInLabel ----
                    testImageZoomInLabel.setIcon(new ImageIcon(getClass().getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/preview/zoom_in.png")));
                    testImageZoomInLabel.addMouseListener(new MouseAdapter() {
                        @Override public void mouseClicked(MouseEvent e) {
                            testImageZoomInLabelMouseClicked(e);
                        }
                    });
                    testImageZoomInLabel.setToolTipText(Localizer.localize("UI", "ZoomInToolTip"));
                    panel2.add(testImageZoomInLabel,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                    //---- testImageZoomOutLabel ----
                    testImageZoomOutLabel.setIcon(new ImageIcon(getClass().getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/preview/zoom_out.png")));
                    testImageZoomOutLabel.setFont(UIManager.getFont("Label.font"));
                    testImageZoomOutLabel.addMouseListener(new MouseAdapter() {
                        @Override public void mouseClicked(MouseEvent e) {
                            testImageZoomOutLabelMouseClicked(e);
                        }
                    });
                    testImageZoomOutLabel
                        .setToolTipText(Localizer.localize("UI", "ZoomOutToolTip"));
                    panel2.add(testImageZoomOutLabel,
                        new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
                }
                testPreviewPanel.add(panel2,
                    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            recognitionPreviewTabbedPane.addTab("Test Preview", testPreviewPanel);
        }
        add(recognitionPreviewTabbedPane,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JTabbedPane recognitionPreviewTabbedPane;
    private JPanel printPreviewPanel;
    private JPanel panel1;
    private JComboBox zoomBox;
    private JLabel zoomInLabel;
    private JLabel zoomOutLabel;
    private JButton setPreviewFieldDataButton;
    private JButton printThisPreviewButton;
    private JButton savePreviewPDFButton;
    private JScrollPane scrollPane1;
    private JImageLabel pageImageLabel;
    private JPanel testPreviewPanel;
    private JPanel panel9;
    private JLabel label2;
    private JLabel imageDetailsLabel;
    private JButton scanImageButton;
    private JButton browseTestImageButton;
    private JButton clearImageButton;
    private JScrollPane scrollPane2;
    private JImageLabel testPageImageLabel;
    private JPanel panel2;
    private JComboBox testImageZoomBox;
    private JLabel testImageZoomInLabel;
    private JLabel testImageZoomOutLabel;
    // JFormDesigner - End of variables declaration //GEN-END:variables

    public RecognitionPanelController getRecognitionPanelController() {
        return recognitionPanelController;
    }

    public JGraph getGraph() {
        return graph;
    }

    public PublicationRecognitionStructure getPublicationRecognitionStructure() {
        return publicationRecognitionStructure;
    }

    public void previewClicked(Point2D point, boolean debug) {

        PageAttributes currentPageAttributes = graph.getPageAttributes();

        // recalculate point scale
        int panelWidth = pageImageLabel.getWidth();
        int scaledPageWidth = (int) (currentPageAttributes.getFullWidth() * realZoom);
        int panelHeight = pageImageLabel.getHeight();
        int scaledPageHeight = (int) (currentPageAttributes.getFullHeight() * realZoom);

        int x_offset = new Integer((panelWidth - scaledPageWidth) / 2);
        int y_offset = new Integer((panelHeight - scaledPageHeight) / 2);

        point = new Point((int) (((point.getX() - x_offset) * 2) / realZoom),
            (int) (((point.getY() - y_offset) * 2) / realZoom));

        FragmentRecognitionData fragmentRecognitionData =
            formReader.getFragmentRecognitionDataByPoint(point);

        if (fragmentRecognitionData != null) {

            final OMRMatrix omrMatrix = fragmentRecognitionData.getOmrMatrix();

            if ((omrMatrix != null) && (fragmentRecognitionData.getType()
                == FragmentRecognitionData.OMR_FRAGMENT)) {

                String[] capturedData = new String[omrMatrix.getCapturedData().size()];
                int i = 0;
                for (String value : omrMatrix.getCapturedData()) {
                    capturedData[i] = value;
                    i++;
                }

                MarkDetectionDetailsDialog mddd =
                    new MarkDetectionDetailsDialog(Main.getInstance());
                mddd.removeModify();
                mddd.setFragmentRecognitionData(fragmentRecognitionData);

                mddd.setModal(true);
                mddd.setVisible(true);

            } else if ((omrMatrix != null) && (fragmentRecognitionData.getType()
                == FragmentRecognitionData.DAMAGED_OMR_FRAGMENT)) {

                // run omrMatrix in debug mode
                try {
                    omrMatrix.processDebug();
                } catch (Exception ex) {
                }

            }

        } else {

            BarcodeRecognitionData barcode = formReader.getBarcodeRecognitionDataByPoint(point);
            if (barcode != null) {

                BarcodeDetectionDetailsDialog bddd =
                    new BarcodeDetectionDetailsDialog(Main.getInstance());
                bddd.setBarcodeRecognitionData(barcode);

                bddd.setModal(true);
                bddd.setVisible(true);

            }

        }

    }

    public void testImageClicked(Point2D point) {

        if (binarizedPreviewImage == null) {
            return;
        }

        // recalculate point scale
        int panelWidth = testPageImageLabel.getWidth();
        int scaledPageWidth = (int) (binarizedPreviewImage.getWidth() * testImageRealZoom);
        int panelHeight = testPageImageLabel.getHeight();
        int scaledPageHeight = (int) (binarizedPreviewImage.getHeight() * testImageRealZoom);

        int x_offset = new Integer((panelWidth - scaledPageWidth) / 2);
        int y_offset = new Integer((panelHeight - scaledPageHeight) / 2);

        point = new Point((int) ((point.getX() - x_offset) / testImageRealZoom),
            (int) ((point.getY() - y_offset) / testImageRealZoom));

        FragmentRecognitionData fragmentRecognitionData =
            testFormReader.getFragmentRecognitionDataByPoint(point);

        if (fragmentRecognitionData != null) {

            final OMRMatrix omrMatrix = fragmentRecognitionData.getOmrMatrix();

            if ((omrMatrix != null) && (fragmentRecognitionData.getType()
                == FragmentRecognitionData.OMR_FRAGMENT)) {

                String[] capturedData = new String[omrMatrix.getCapturedData().size()];
                int i = 0;
                for (String value : omrMatrix.getCapturedData()) {
                    capturedData[i] = value;
                    i++;
                }

                MarkDetectionDetailsDialog mddd =
                    new MarkDetectionDetailsDialog(Main.getInstance());
                mddd.removeModify();
                mddd.setFragmentRecognitionData(fragmentRecognitionData);

                mddd.setModal(true);
                mddd.setVisible(true);

            } else if ((omrMatrix != null) && (fragmentRecognitionData.getType()
                == FragmentRecognitionData.DAMAGED_OMR_FRAGMENT)) {

                // run omrMatrix in debug mode
                try {
                    omrMatrix.processDebug();
                } catch (Exception ex) {
                }

            }

        } else {

            BarcodeRecognitionData barcode = testFormReader.getBarcodeRecognitionDataByPoint(point);
            if (barcode != null) {

                BarcodeDetectionDetailsDialog bddd =
                    new BarcodeDetectionDetailsDialog(Main.getInstance());
                bddd.setBarcodeRecognitionData(barcode);

                bddd.setModal(true);
                bddd.setVisible(true);

            }

        }

    }

    public void showErrorMessages() {
        if (formReader != null && formReader.getProcessingError() != null) {
            Misc.printStackTrace(formReader.getProcessingError());
            Misc.showErrorMsg(Main.getInstance(), formReader.getProcessingError().getMessage());
        }
    }

    public boolean isFinishedLoading() {
        return finishedLoading;
    }


    public void setFinishedLoading(boolean finishedLoading) {
        this.finishedLoading = finishedLoading;
    }

    @Override public void scanPerformed(ScanEvent scanEvent) {
        if (swingSaneFrame != null) {
            swingSaneFrame.dispose();
            swingSaneFrame = null;
        }
        loadPreviewImage(scanEvent.getBufferedImage());
    }

}
