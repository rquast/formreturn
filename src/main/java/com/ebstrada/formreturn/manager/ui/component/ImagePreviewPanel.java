package com.ebstrada.formreturn.manager.ui.component;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.event.*;

import com.ebstrada.formreturn.manager.ui.dialog.BarcodeDetectionDetailsDialog;
import com.ebstrada.formreturn.manager.ui.dialog.MarkDetectionDetailsDialog;
import com.ebstrada.formreturn.manager.ui.filefilter.FilenameExtensionFilter;

import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.reader.FormReader;
import com.ebstrada.formreturn.manager.logic.recognition.reader.FormReaderException;
import com.ebstrada.formreturn.manager.logic.recognition.structure.BarcodeRecognitionData;
import com.ebstrada.formreturn.manager.logic.recognition.structure.FormRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.FragmentRecognitionData;
import com.ebstrada.formreturn.manager.logic.recognition.structure.OMRMatrix;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.image.ImageUtil;

public class ImagePreviewPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private byte[] originalImageData;

    private BufferedImage sourceImage;

    private BufferedImage convertedImage;

    private boolean detectForm = false;

    private Float realZoom = 1.0f;

    private boolean drawDetectBarcode = true;
    private boolean drawDetectSegment = true;
    private boolean drawDetectfragment = true;
    private boolean drawDetectMarks = true;

    private FormReader formReader;

    private ZoomSettings zoomSettings;

    public ImagePreviewPanel(byte[] imageData, boolean detectForm,
        final ZoomSettings zoomSettings) {
        super();
        this.originalImageData = imageData;
        this.detectForm = detectForm;
        this.zoomSettings = zoomSettings;
        initComponents();
        zoomToFitCheckBox.setSelected(zoomSettings.isZoomToFit());
        restoreImage();
        imagePreviewScrollPane.getVerticalScrollBar().setUnitIncrement(30);
        imagePreviewScrollPane.getVerticalScrollBar().setBlockIncrement(90);
        imagePreviewScrollPane.getHorizontalScrollBar().setUnitIncrement(30);
        imagePreviewScrollPane.getHorizontalScrollBar().setBlockIncrement(90);
        if (!detectForm) {
            disableDetectionCheckBoxes();
            masterPanel.remove(detectionPanel);
        }

        imageFileSizeLabel.setText(String.format(Localizer.localize("UI", "ImagePreviewFileSize"),
            Misc.getSizeString(imageData.length)));
        imagePreviewScrollPane.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                if (zoomSettings.isZoomToFit()) {
                    zoomToFit();
                } else {
                    zoomBox.getModel().setSelectedItem(zoomSettings.getZoomLevel());
                }
            }
        });
    }

    public void disableDetectionCheckBoxes() {
        drawDetectSegmentsCheckBox.setEnabled(false);
        drawDetectFragmentsCheckBox.setEnabled(false);
        drawDetectBarcodesCheckBox.setEnabled(false);
        drawDetectMarksCheckBox.setEnabled(false);
    }

    public void renderImagePreview(Graphics2D g) {

        if (sourceImage != null) {

            if (convertedImage == null) {

                GraphicsDevice gd =
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                GraphicsConfiguration gc = gd.getDefaultConfiguration();

                if (sourceImage.getColorModel() != gc.getColorModel()) {
                    convertedImage =
                        gc.createCompatibleImage(sourceImage.getWidth(), sourceImage.getHeight(),
                            Transparency.OPAQUE);
                    Graphics2D g2d = convertedImage.createGraphics();
                    g2d.drawImage(sourceImage, 0, 0, sourceImage.getWidth(),
                        sourceImage.getHeight(), null);
                    convertedImage.flush();
                    g2d.dispose();
                } else {
                    convertedImage = sourceImage;
                }

            }

            g.setColor(Color.darkGray);
            g.fill(new Rectangle2D.Double(0, 0, imagePreviewLabel.getWidth(),
                imagePreviewLabel.getHeight()));

            // BE SURE TO USE THIS OR IT WILL RENDER SLOW!
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

            double x =
                (imagePreviewLabel.getWidth() / 2) - ((sourceImage.getWidth() * realZoom) / 2);
            double y =
                (imagePreviewLabel.getHeight() / 2) - ((sourceImage.getHeight() * realZoom) / 2);
            AffineTransform at = AffineTransform.getTranslateInstance(x, y);
            at.scale(realZoom, realZoom);
            g.drawRenderedImage(convertedImage, at);

            if (detectForm == true && formReader != null) {
                formReader.setOverlayZoom(realZoom);
                formReader.setDrawDetectBarcode(isDrawDetectBarcode());
                formReader.setDrawDetectSegment(isDrawDetectSegment());
                formReader.setDrawDetectfragment(isDrawDetectfragment());
                formReader.setDrawDetectMarks(isDrawDetectMarks());
                formReader.drawDetectionOverlay(g, (int) x, (int) y);
            }

        }

    }

    public void resizeImageLabel() {
        Dimension dim = new Dimension((int) (sourceImage.getWidth() * realZoom),
            (int) (sourceImage.getHeight() * realZoom));
        if (imagePreviewLabel != null) {
            imagePreviewLabel.setMaximumSize(dim);
            imagePreviewLabel.setMinimumSize(dim);
            imagePreviewLabel.setPreferredSize(dim);
        }
        imagePreviewLabel.revalidate();
    }

    private void zoomBoxItemStateChanged(ItemEvent e) {
        String unparsedString = (String) zoomBox.getSelectedObjects()[0];
        String zoomString = "";
        for (int i = 0; i < unparsedString.length(); i++) {
            if (unparsedString.charAt(i) >= 48 && unparsedString.charAt(i) <= 57) {
                zoomString += unparsedString.charAt(i);
            }
        }
        zoomSettings.setZoomLevel(zoomString);
        realZoom = Float.parseFloat(zoomString) / 100.0f;
        resizeImageLabel();
        repaint();
    }

    private void zoomInLabelMouseClicked(MouseEvent e) {

        String valueString = (String) zoomBox.getModel().getSelectedItem();

        int selectedIndex = zoomBox.getSelectedIndex();

        if (selectedIndex < 0) {

            for (int i = 0; i < zoomBox.getItemCount() - 1; i++) {

                String itemString = (String) zoomBox.getModel().getElementAt(i);

                float item = Float.parseFloat(itemString.replaceAll("%", ""));
                float value = Float.parseFloat(valueString.replaceAll("%", ""));

                if (item > value) {
                    zoomBox.setSelectedIndex(i);
                    break;
                }

            }

        } else {

            if (selectedIndex < (zoomBox.getItemCount() - 1)) {
                zoomBox.setSelectedIndex(selectedIndex + 1);
            }

        }
    }

    private void zoomOutLabelMouseClicked(MouseEvent e) {

        String valueString = (String) zoomBox.getModel().getSelectedItem();

        int selectedIndex = zoomBox.getSelectedIndex();

        if (selectedIndex < 0) {

            for (int i = zoomBox.getItemCount() - 1; i >= 0; i--) {

                String itemString = (String) zoomBox.getModel().getElementAt(i);

                float item = Float.parseFloat(itemString.replaceAll("%", ""));
                float value = Float.parseFloat(valueString.replaceAll("%", ""));

                if (item < value) {
                    zoomBox.setSelectedIndex(i);
                    break;
                }

            }

        } else {

            if (selectedIndex > 0) {
                zoomBox.setSelectedIndex(selectedIndex - 1);
            }

        }

    }

    public void restoreImage() {

        if (sourceImage == null && originalImageData != null) {

            try {
                sourceImage = ImageUtil.readImage(originalImageData, 1);
                sourceImage.flush();
            } catch (final FormReaderException fre) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(fre);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Misc.showErrorMsg(getRootPane(), fre.getErrorTitle());
                    }
                });
                sourceImage = null;
                convertedImage = null;
                resizeImageLabel();
                repaint();
            } catch (final Exception e) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Misc.showErrorMsg(getRootPane(), e.getMessage());
                    }
                });
                sourceImage = null;
                convertedImage = null;
                resizeImageLabel();
                repaint();
            }

            if (detectForm == true) {

                EntityManager entityManager =
                    Main.getInstance().getJPAConfiguration().getEntityManager();
                FormRecognitionStructure frs = new FormRecognitionStructure();
                formReader = new FormReader(sourceImage, frs, entityManager, true);
                try {
                    BufferedImage image = formReader.registerForm(sourceImage);
                    formReader.loadRecognitionStucture(image);
                } catch (final FormReaderException fre) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(fre);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            Misc.showErrorMsg(getRootPane(), fre.getErrorTitle());
                        }
                    });
                }

                entityManager.close();

            }

            resizeImageLabel();
            repaint();

        }

    }

    public void imagePreviewClicked(Point point, boolean debug) {

        if (detectForm == false) {
            return;
        }

        // recalculate point scale
        int panelWidth = imagePreviewLabel.getWidth();
        int scaledPageWidth = (int) (sourceImage.getWidth() * realZoom);
        int panelHeight = imagePreviewLabel.getHeight();
        int scaledPageHeight = (int) (sourceImage.getHeight() * realZoom);

        int x_offset = new Integer((panelWidth - scaledPageWidth) / 2);
        int y_offset = new Integer((panelHeight - scaledPageHeight) / 2);

        point = new Point((int) ((point.getX() - x_offset) / realZoom),
            (int) ((point.getY() - y_offset) / realZoom));

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
                    new MarkDetectionDetailsDialog(getTopLevelAncestor());
                mddd.removeModify();
                mddd.setFragmentRecognitionData(fragmentRecognitionData);

                mddd.setModal(true);
                mddd.setVisible(true);

            } else if ((omrMatrix != null) && (fragmentRecognitionData.getType()
                == FragmentRecognitionData.DAMAGED_OMR_FRAGMENT)) {

                // run omrMatrix in debug mode
                try {
                    omrMatrix.process(debug);
                } catch (Exception ex) {
                }

            }

        } else {

            BarcodeRecognitionData barcode = formReader.getBarcodeRecognitionDataByPoint(point);
            if (barcode != null) {

                Object obj = getTopLevelAncestor();

                if (obj instanceof JFrame) {

                    BarcodeDetectionDetailsDialog bddd =
                        new BarcodeDetectionDetailsDialog((JFrame) obj);
                    bddd.setBarcodeRecognitionData(barcode);

                    bddd.setModal(true);
                    bddd.setVisible(true);

                } else if (obj instanceof JDialog) {

                    BarcodeDetectionDetailsDialog bddd =
                        new BarcodeDetectionDetailsDialog((JDialog) obj);
                    bddd.setBarcodeRecognitionData(barcode);

                    bddd.setModal(true);
                    bddd.setVisible(true);

                }

            }

        }

    }

    private void drawDetectBarcodesCheckBoxActionPerformed(ActionEvent e) {
        setDrawDetectBarcode(drawDetectBarcodesCheckBox.isSelected());
        imagePreviewLabel.revalidate();
        repaint();
    }

    private void drawDetectSegmentsCheckBoxActionPerformed(ActionEvent e) {
        setDrawDetectSegment(drawDetectSegmentsCheckBox.isSelected());
        imagePreviewLabel.revalidate();
        repaint();
    }

    private void drawDetectFragmentsCheckBoxActionPerformed(ActionEvent e) {
        setDrawDetectfragment(drawDetectFragmentsCheckBox.isSelected());
        imagePreviewLabel.revalidate();
        repaint();
    }

    private void drawDetectMarksCheckBoxActionPerformed(ActionEvent e) {
        setDrawDetectMarks(drawDetectMarksCheckBox.isSelected());
        imagePreviewLabel.revalidate();
        repaint();
    }

    private void saveToDiskButtonActionPerformed(ActionEvent e) {

        File imageFile = null;

        try {
            imageFile = getSaveLocation();
            OutputStream out = new FileOutputStream(imageFile);
            out.write(originalImageData);
            out.flush();
            out.close();
            Misc.showSuccessMsg(getRootPane(),
                Localizer.localize("UI", "SuccessfullySavedImageFileMessage"));
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            Misc.showErrorMsg(getRootPane(),
                Localizer.localize("UI", "UnableToSaveImageFileErrorMessage"));
        }

    }

    private File getSaveLocation() throws Exception {

        File file = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();
        filter.addExtension("png");

        Object obj = getTopLevelAncestor();

        FileDialog fd = null;

        if (obj instanceof JFrame) {
            fd =
                new FileDialog((JFrame) obj, Localizer.localize("UI", "SaveImageToDiskDialogTitle"),
                    FileDialog.SAVE);
        } else if (obj instanceof JDialog) {
            fd = new FileDialog((JDialog) obj,
                Localizer.localize("UI", "SaveImageToDiskDialogTitle"), FileDialog.SAVE);
        } else {
            return null;
        }

        fd.setFilenameFilter(filter);
        fd.setFile(Localizer.localize("UI", "ImageFilePrefix") + ".png");

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
                throw new Exception(
                    Localizer.localize("UI", "CannotSaveToDirectoryExceptionMessage"));
            }
            try {
                Globals.setLastDirectory(file.getCanonicalPath());
            } catch (IOException ldex) {
            }
        } else {
            throw new Exception(Localizer.localize("UI", "FilePointerIsNullExceptionMessage"));
        }

        return file;

    }

    private void zoomToFitCheckBoxStateChanged(ChangeEvent e) {

        AbstractButton abstractButton = (AbstractButton) e.getSource();

        ButtonModel buttonModel = abstractButton.getModel();

        if (buttonModel.isPressed()) {
            zoomSettings.setZoomToFit(zoomToFitCheckBox.isSelected());
            if (zoomSettings.isZoomToFit()) {
                zoomToFit();
            }
        }

    }

    private void zoomToFit() {

        if (imagePreviewScrollPane.getWidth() > 0) {

            double width = imagePreviewScrollPane.getWidth();
            double height = imagePreviewScrollPane.getHeight();

            double widthRatio = width / (double) sourceImage.getWidth();
            double heightRatio = height / (double) sourceImage.getHeight();

            if (width > height) {
                this.zoomSettings.setZoomLevel(((int) Math.floor(widthRatio * 100.0d)) + "%");
            } else {
                this.zoomSettings.setZoomLevel(((int) Math.floor(heightRatio * 100.0d)) + "%");
            }

            zoomBox.getModel().setSelectedItem(zoomSettings.getZoomLevel());

        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        imagePreviewScrollPane = new JScrollPane();
        imagePreviewLabel = new JImageLabel();
        masterPanel = new JPanel();
        panel2 = new JPanel();
        zoomBox = new JComboBox();
        zoomInLabel = new JLabel();
        zoomOutLabel = new JLabel();
        zoomToFitCheckBox = new JCheckBox();
        detectionPanel = new JPanel();
        drawDetectBarcodesCheckBox = new JCheckBox();
        drawDetectSegmentsCheckBox = new JCheckBox();
        drawDetectFragmentsCheckBox = new JCheckBox();
        drawDetectMarksCheckBox = new JCheckBox();
        panel1 = new JPanel();
        imageFileSizeLabel = new JLabel();
        saveToDiskButton = new JButton();

        //======== this ========
        setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(null);
            dialogPane.setLayout(new GridBagLayout());
            ((GridBagLayout) dialogPane.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) dialogPane.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout) dialogPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) dialogPane.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

            //======== contentPanel ========
            {
                contentPanel.setBorder(null);
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights =
                    new double[] {1.0, 0.0, 1.0E-4};

                //======== imagePreviewScrollPane ========
                {
                    imagePreviewScrollPane.setViewportBorder(null);
                    imagePreviewScrollPane.setBorder(null);

                    //---- imagePreviewLabel ----
                    imagePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    imagePreviewLabel.setIpd(this);
                    imagePreviewLabel.setRenderType(JImageLabel.RENDER_CAPTURED_IMAGE_PREVIEW);
                    imagePreviewScrollPane.setViewportView(imagePreviewLabel);
                }
                contentPanel.add(imagePreviewScrollPane,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                //======== masterPanel ========
                {
                    masterPanel.setOpaque(false);
                    masterPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) masterPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                    ((GridBagLayout) masterPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                    ((GridBagLayout) masterPanel.getLayout()).columnWeights =
                        new double[] {1.0, 0.0, 1.0, 1.0E-4};
                    ((GridBagLayout) masterPanel.getLayout()).rowWeights =
                        new double[] {0.0, 0.0, 0.0, 1.0E-4};

                    //======== panel2 ========
                    {
                        panel2.setOpaque(false);
                        panel2.setLayout(new GridBagLayout());
                        ((GridBagLayout) panel2.getLayout()).columnWidths =
                            new int[] {0, 0, 0, 0, 0, 0, 0};
                        ((GridBagLayout) panel2.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout) panel2.getLayout()).columnWeights =
                            new double[] {1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout) panel2.getLayout()).rowWeights =
                            new double[] {0.0, 1.0E-4};

                        //---- zoomBox ----
                        zoomBox.setModel(new DefaultComboBoxModel(
                            new String[] {"5%", "10%", "25%", "50%", "100%", "200%", "500%",
                                "1000%"}));
                        zoomBox.setSelectedIndex(4);
                        zoomBox.setFont(UIManager.getFont("ComboBox.font"));
                        zoomBox.setEditable(true);
                        zoomBox.addItemListener(new ItemListener() {
                            @Override public void itemStateChanged(ItemEvent e) {
                                zoomBoxItemStateChanged(e);
                            }
                        });
                        panel2.add(zoomBox,
                            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                        //---- zoomInLabel ----
                        zoomInLabel.setIcon(new ImageIcon(getClass().getResource(
                            "/com/ebstrada/formreturn/manager/ui/icons/preview/zoom_in.png")));
                        zoomInLabel.setFont(UIManager.getFont("Label.font"));
                        zoomInLabel.addMouseListener(new MouseAdapter() {
                            @Override public void mouseClicked(MouseEvent e) {
                                zoomInLabelMouseClicked(e);
                            }
                        });
                        zoomInLabel.setToolTipText(Localizer.localize("UI", "ZoomInToolTip"));
                        panel2.add(zoomInLabel,
                            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

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
                        panel2.add(zoomOutLabel,
                            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                        //---- zoomToFitCheckBox ----
                        zoomToFitCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                        zoomToFitCheckBox.setFocusPainted(false);
                        zoomToFitCheckBox.addChangeListener(new ChangeListener() {
                            @Override public void stateChanged(ChangeEvent e) {
                                zoomToFitCheckBoxStateChanged(e);
                            }
                        });
                        zoomToFitCheckBox
                            .setText(Localizer.localize("UI", "ZoomToFitCheckBoxText"));
                        panel2.add(zoomToFitCheckBox,
                            new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
                    }
                    masterPanel.add(panel2,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

                    //======== detectionPanel ========
                    {
                        detectionPanel.setOpaque(false);
                        detectionPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout) detectionPanel.getLayout()).columnWidths =
                            new int[] {0, 0, 0, 0, 0};
                        ((GridBagLayout) detectionPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout) detectionPanel.getLayout()).columnWeights =
                            new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout) detectionPanel.getLayout()).rowWeights =
                            new double[] {0.0, 1.0E-4};

                        //---- drawDetectBarcodesCheckBox ----
                        drawDetectBarcodesCheckBox.setFocusPainted(false);
                        drawDetectBarcodesCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                        drawDetectBarcodesCheckBox.setSelected(true);
                        drawDetectBarcodesCheckBox.addActionListener(new ActionListener() {
                            @Override public void actionPerformed(ActionEvent e) {
                                drawDetectBarcodesCheckBoxActionPerformed(e);
                            }
                        });
                        drawDetectBarcodesCheckBox
                            .setText(Localizer.localize("UI", "DrawDetectBarcodesCheckBox"));
                        detectionPanel.add(drawDetectBarcodesCheckBox,
                            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                        //---- drawDetectSegmentsCheckBox ----
                        drawDetectSegmentsCheckBox.setFocusPainted(false);
                        drawDetectSegmentsCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                        drawDetectSegmentsCheckBox.setSelected(true);
                        drawDetectSegmentsCheckBox.addActionListener(new ActionListener() {
                            @Override public void actionPerformed(ActionEvent e) {
                                drawDetectSegmentsCheckBoxActionPerformed(e);
                            }
                        });
                        drawDetectSegmentsCheckBox
                            .setText(Localizer.localize("UI", "DrawDetectSegmentsCheckBox"));
                        detectionPanel.add(drawDetectSegmentsCheckBox,
                            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                        //---- drawDetectFragmentsCheckBox ----
                        drawDetectFragmentsCheckBox.setFocusPainted(false);
                        drawDetectFragmentsCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                        drawDetectFragmentsCheckBox.setSelected(true);
                        drawDetectFragmentsCheckBox.addActionListener(new ActionListener() {
                            @Override public void actionPerformed(ActionEvent e) {
                                drawDetectFragmentsCheckBoxActionPerformed(e);
                            }
                        });
                        drawDetectFragmentsCheckBox
                            .setText(Localizer.localize("UI", "DrawDetectFragmentsCheckBox"));
                        detectionPanel.add(drawDetectFragmentsCheckBox,
                            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                        //---- drawDetectMarksCheckBox ----
                        drawDetectMarksCheckBox.setFocusPainted(false);
                        drawDetectMarksCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                        drawDetectMarksCheckBox.setSelected(true);
                        drawDetectMarksCheckBox.addActionListener(new ActionListener() {
                            @Override public void actionPerformed(ActionEvent e) {
                                drawDetectMarksCheckBoxActionPerformed(e);
                            }
                        });
                        drawDetectMarksCheckBox
                            .setText(Localizer.localize("UI", "DrawDetectMarksCheckBox"));
                        detectionPanel.add(drawDetectMarksCheckBox,
                            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                    }
                    masterPanel.add(detectionPanel,
                        new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

                    //======== panel1 ========
                    {
                        panel1.setOpaque(false);
                        panel1.setLayout(new GridBagLayout());
                        ((GridBagLayout) panel1.getLayout()).columnWidths =
                            new int[] {0, 0, 15, 0, 0, 0};
                        ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout) panel1.getLayout()).columnWeights =
                            new double[] {1.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout) panel1.getLayout()).rowWeights =
                            new double[] {0.0, 1.0E-4};

                        //---- imageFileSizeLabel ----
                        imageFileSizeLabel.setText("size");
                        imageFileSizeLabel.setFont(UIManager.getFont("Label.font"));
                        panel1.add(imageFileSizeLabel,
                            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                        //---- saveToDiskButton ----
                        saveToDiskButton.setFont(UIManager.getFont("Button.font"));
                        saveToDiskButton.setIcon(new ImageIcon(getClass()
                            .getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
                        saveToDiskButton.addActionListener(new ActionListener() {
                            @Override public void actionPerformed(ActionEvent e) {
                                saveToDiskButtonActionPerformed(e);
                            }
                        });
                        saveToDiskButton.setText(Localizer.localize("UI", "SaveToDiskButtonText"));
                        panel1.add(saveToDiskButton,
                            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
                    }
                    masterPanel.add(panel1,
                        new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
                }
                contentPanel.add(masterPanel,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(dialogPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JScrollPane imagePreviewScrollPane;
    private JImageLabel imagePreviewLabel;
    private JPanel masterPanel;
    private JPanel panel2;
    private JComboBox zoomBox;
    private JLabel zoomInLabel;
    private JLabel zoomOutLabel;
    private JCheckBox zoomToFitCheckBox;
    private JPanel detectionPanel;
    private JCheckBox drawDetectBarcodesCheckBox;
    private JCheckBox drawDetectSegmentsCheckBox;
    private JCheckBox drawDetectFragmentsCheckBox;
    private JCheckBox drawDetectMarksCheckBox;
    private JPanel panel1;
    private JLabel imageFileSizeLabel;
    private JButton saveToDiskButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public boolean isDrawDetectBarcode() {
        return drawDetectBarcode;
    }

    public void setDrawDetectBarcode(boolean drawDetectBarcode) {
        this.drawDetectBarcode = drawDetectBarcode;
    }

    public boolean isDrawDetectSegment() {
        return drawDetectSegment;
    }

    public void setDrawDetectSegment(boolean drawDetectSegment) {
        this.drawDetectSegment = drawDetectSegment;
    }

    public boolean isDrawDetectfragment() {
        return drawDetectfragment;
    }

    public void setDrawDetectfragment(boolean drawDetectfragment) {
        this.drawDetectfragment = drawDetectfragment;
    }

    public boolean isDrawDetectMarks() {
        return drawDetectMarks;
    }

    public void setDrawDetectMarks(boolean drawDetectMarks) {
        this.drawDetectMarks = drawDetectMarks;
    }

}
