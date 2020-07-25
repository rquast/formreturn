package com.ebstrada.formreturn.manager.ui.component;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.image.BlobExtractor;
import com.ebstrada.formreturn.manager.util.image.BubbleDetection;

public class BlobExtractionDebugPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public static final int BINARIZED_IMAGE = 0;
    public static final int LABEL_FIRST_PARSE = 1;
    public static final int BUBBLE_DETECTION = 4;

    private ArrayList<Point2D> bubbleBoundaryPoints = new ArrayList<Point2D>();
    private ArrayList<Point2D> bubbleCentroidPoints = new ArrayList<Point2D>();

    private BufferedImage sourceImage;

    private Float realZoom = 1.0f;

    private BlobExtractor blobExtraction;

    private int type;

    private int[] regionIndexes;

    public BlobExtractionDebugPanel(BlobExtractor blobExtraction, String zoomLevel, int type) {
        super();

        this.blobExtraction = blobExtraction;
        this.type = type;
        restoreImage();
        initComponents();
        imagePreviewScrollPane.getVerticalScrollBar().setUnitIncrement(30);
        imagePreviewScrollPane.getVerticalScrollBar().setBlockIncrement(90);
        imagePreviewScrollPane.getHorizontalScrollBar().setUnitIncrement(30);
        imagePreviewScrollPane.getHorizontalScrollBar().setBlockIncrement(90);
        if (zoomLevel != null) {
            zoomBox.setSelectedItem(zoomLevel);
        }

        resizeImageLabel();
        repaint();

        setRegionText(regionIndexes);

    }

    public void restoreImage() {

        switch (this.type) {

            case BINARIZED_IMAGE:
                sourceImage = blobExtraction.getBinarizedImage();
                break;
            case LABEL_FIRST_PARSE:
                sourceImage = blobExtraction.getFirstParseImage();
                regionIndexes = blobExtraction.getFirstParseLabels();
                break;
            case BUBBLE_DETECTION:
                sourceImage = blobExtraction.getBubbleDetectionImage();
                regionIndexes = blobExtraction.getBubbleDetectionLabels();
                findBubblePoints();
                for (Point2D point : this.bubbleBoundaryPoints) {
                    sourceImage.setRGB((int) point.getX(), (int) point.getY(), Color.RED.getRGB());
                }
                for (Point2D point : this.bubbleCentroidPoints) {
                    sourceImage
                        .setRGB((int) point.getX(), (int) point.getY(), Color.GREEN.getRGB());
                }
                break;
        }

        resizeImageLabel();
        repaint();

    }

    public void setRegionText(int[] regionIndexes) {

        StringBuilder output = new StringBuilder();

        if (regionIndexes != null) {

            for (int i = 0; i < this.regionIndexes.length; i++) {
                if ((i % sourceImage.getWidth()) == 0 && i > 0) {
                    output.append("\n");
                }
                if (regionIndexes[i] > 0) {
                    output.append(regionIndexes[i]);
                } else {
                    output.append("_");
                }
            }

            if (this.type == BUBBLE_DETECTION) {
                output.append("\n\n*******************************************************\n");
                for (BubbleDetection bubbleDetection : blobExtraction.getBubbleDetections()) {
                    if (bubbleDetection.getPixelCount() > 0) {
                        output.append("\n");
                        output.append("Region: " + bubbleDetection.getLabel() + "\n");
                        output
                            .append("Black Pixel Count: " + bubbleDetection.getPixelCount() + "\n");
                        output
                            .append("White Pixel Count: " + bubbleDetection.getWhiteCount() + "\n");
                        output.append(
                            "Enclosed Pixel Count: " + bubbleDetection.getEnclosedPixelCount()
                                + "\n");
                        output.append(
                            "Enclosure Centroid: X=" + bubbleDetection.getCenterX() + ", Y="
                                + bubbleDetection.getCenterY() + "\n");
                        output.append("Enclosure Bounds: X1=" + bubbleDetection.getMinX() + ", Y1="
                            + bubbleDetection.getMinY() + ", X2=" + bubbleDetection.getMaxX()
                            + ", Y2=" + bubbleDetection.getMaxY() + "\n");
                        output.append("*******************************************************\n");
                    }
                }
            }

            if (output.length() > 0) {
                regionTextArea.setText(output.toString());
            }

        }

    }

    public void findBubblePoints() {

        for (BubbleDetection bubbleDetection : blobExtraction.getBubbleDetections()) {

            if (bubbleDetection.getEnclosedPixelCount() == 0) {
                continue;
            }

            Rectangle2D boundary = bubbleDetection.getRectangle();

            bubbleBoundaryPoints.add(new Point2D.Double(boundary.getMinX(), boundary.getMinY()));
            bubbleBoundaryPoints.add(new Point2D.Double(boundary.getMinX(), boundary.getMaxY()));
            bubbleBoundaryPoints.add(new Point2D.Double(boundary.getMaxX(), boundary.getMinY()));
            bubbleBoundaryPoints.add(new Point2D.Double(boundary.getMaxX(), boundary.getMaxY()));

            bubbleCentroidPoints.add(bubbleDetection.getCentroidPoint());

        }

    }


    public void renderImagePreview(Graphics2D g) {

        if (sourceImage != null) {

            g.setColor(Color.darkGray);
            g.fill(new Rectangle2D.Double(0, 0, imagePreviewLabel.getWidth(),
                imagePreviewLabel.getHeight()));
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            double x =
                (imagePreviewLabel.getWidth() / 2) - ((sourceImage.getWidth() * realZoom) / 2);
            double y =
                (imagePreviewLabel.getHeight() / 2) - ((sourceImage.getHeight() * realZoom) / 2);
            AffineTransform at = AffineTransform.getTranslateInstance(x, y);
            at.scale(realZoom, realZoom);
            g.drawRenderedImage(sourceImage, at);

        }

    }

    public void resizeImageLabel() {

        if (sourceImage == null) {
            return;
        }

        Dimension dim = new Dimension((int) (sourceImage.getWidth() * realZoom),
            (int) (sourceImage.getHeight() * realZoom));
        if (imagePreviewLabel != null) {
            imagePreviewLabel.setMaximumSize(dim);
            imagePreviewLabel.setMinimumSize(dim);
            imagePreviewLabel.setPreferredSize(dim);
            imagePreviewLabel.revalidate();
        }
    }

    private void zoomBoxItemStateChanged(ItemEvent e) {
        String unparsedString = (String) zoomBox.getSelectedObjects()[0];
        String zoomString = "";
        for (int i = 0; i < unparsedString.length(); i++) {
            if (unparsedString.charAt(i) >= 48 && unparsedString.charAt(i) <= 57) {
                zoomString += unparsedString.charAt(i);
            }
        }
        realZoom = Float.parseFloat(zoomString) / 100.0f;
        resizeImageLabel();
        repaint();
    }

    private void zoomInLabelMouseClicked(MouseEvent e) {
        int selectedIndex = zoomBox.getSelectedIndex();

        if (selectedIndex < (zoomBox.getItemCount() - 1)) {
            zoomBox.setSelectedIndex(selectedIndex + 1);
        }
    }

    private void zoomOutLabelMouseClicked(MouseEvent e) {
        int selectedIndex = zoomBox.getSelectedIndex();

        if (selectedIndex > 0) {
            zoomBox.setSelectedIndex(selectedIndex - 1);
        }
    }

    public void imagePreviewClicked(Point point) {

        if (type == BINARIZED_IMAGE) {
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

        int linearPoint = (int) ((point.getY() * sourceImage.getWidth()) + point.getX());

        String message =
            "X = " + point.getX() + " - Y = " + point.getY() + " - Linear Pixel Position: "
                + linearPoint + " - Region Number: " + regionIndexes[linearPoint];

        javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, "Point Details",
            javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.INFORMATION_MESSAGE);

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        splitPane1 = new JSplitPane();
        imagePreviewScrollPane = new JScrollPane();
        imagePreviewLabel = new JImageLabel();
        scrollPane1 = new JScrollPane();
        regionTextArea = new JTextArea();
        masterPanel = new JPanel();
        panel2 = new JPanel();
        zoomBox = new JComboBox();
        zoomInLabel = new JLabel();
        zoomOutLabel = new JLabel();

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

                //======== splitPane1 ========
                {
                    splitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
                    splitPane1.setResizeWeight(0.5);
                    splitPane1.setOneTouchExpandable(true);

                    //======== imagePreviewScrollPane ========
                    {
                        imagePreviewScrollPane.setViewportBorder(null);
                        imagePreviewScrollPane.setBorder(null);

                        //---- imagePreviewLabel ----
                        imagePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        imagePreviewLabel.setBed(this);
                        imagePreviewLabel
                            .setRenderType(JImageLabel.RENDER_BLOB_EXTRACTION_DEBUG_IMAGE);
                        imagePreviewScrollPane.setViewportView(imagePreviewLabel);
                    }
                    splitPane1.setTopComponent(imagePreviewScrollPane);

                    //======== scrollPane1 ========
                    {
                        scrollPane1.setViewportView(regionTextArea);
                    }
                    splitPane1.setBottomComponent(scrollPane1);
                }
                contentPanel.add(splitPane1,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                //======== masterPanel ========
                {
                    masterPanel.setOpaque(false);
                    masterPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) masterPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                    ((GridBagLayout) masterPanel.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout) masterPanel.getLayout()).columnWeights =
                        new double[] {1.0, 0.0, 1.0, 1.0E-4};
                    ((GridBagLayout) masterPanel.getLayout()).rowWeights =
                        new double[] {0.0, 1.0E-4};

                    //======== panel2 ========
                    {
                        panel2.setOpaque(false);
                        panel2.setLayout(new GridBagLayout());
                        ((GridBagLayout) panel2.getLayout()).columnWidths =
                            new int[] {0, 0, 0, 0, 0, 0};
                        ((GridBagLayout) panel2.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout) panel2.getLayout()).columnWeights =
                            new double[] {1.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout) panel2.getLayout()).rowWeights =
                            new double[] {0.0, 1.0E-4};

                        //---- zoomBox ----
                        zoomBox.setModel(new DefaultComboBoxModel(
                            new String[] {"5%", "10%", "25%", "50%", "100%", "200%", "500%",
                                "1000%"}));
                        zoomBox.setSelectedIndex(4);
                        zoomBox.setFont(UIManager.getFont("ComboBox.font"));
                        zoomBox.addItemListener(new ItemListener() {
                            public void itemStateChanged(ItemEvent e) {
                                zoomBoxItemStateChanged(e);
                            }
                        });
                        panel2.add(zoomBox,
                            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                        //---- zoomInLabel ----
                        zoomInLabel.setIcon(new ImageIcon(getClass().getResource(
                            "/com/ebstrada/formreturn/manager/ui/icons/preview/zoom_in.png")));
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
                    }
                    masterPanel.add(panel2,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
    private JSplitPane splitPane1;
    private JScrollPane imagePreviewScrollPane;
    private JImageLabel imagePreviewLabel;
    private JScrollPane scrollPane1;
    private JTextArea regionTextArea;
    private JPanel masterPanel;
    private JPanel panel2;
    private JComboBox zoomBox;
    private JLabel zoomInLabel;
    private JLabel zoomOutLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
