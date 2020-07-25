package com.ebstrada.formreturn.manager.ui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.structure.BarcodeRecognitionData;
import com.ebstrada.formreturn.manager.ui.component.*;

public class BarcodeDetectionDetailsDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private BarcodeRecognitionData barcode;

    private Float realZoom = 1.0f;

    public BarcodeDetectionDetailsDialog(Frame owner) {
        super(owner);
        initComponents();
        scrollPane1.getVerticalScrollBar().setUnitIncrement(30);
        scrollPane1.getVerticalScrollBar().setBlockIncrement(90);
        scrollPane1.getHorizontalScrollBar().setUnitIncrement(30);
        scrollPane1.getHorizontalScrollBar().setBlockIncrement(90);
        getRootPane().setDefaultButton(closeButton);
    }

    public BarcodeDetectionDetailsDialog(Dialog owner) {
        super(owner);
        initComponents();
        scrollPane1.getVerticalScrollBar().setUnitIncrement(30);
        scrollPane1.getVerticalScrollBar().setBlockIncrement(90);
        scrollPane1.getHorizontalScrollBar().setUnitIncrement(30);
        scrollPane1.getHorizontalScrollBar().setBlockIncrement(90);
        getRootPane().setDefaultButton(closeButton);
    }

    public void setBarcodeRecognitionData(BarcodeRecognitionData barcode) {
        this.barcode = barcode;

        barcodeDataTextField.setText(barcode.getValue());
        barcodeTypeTextField.setText(barcode.getTypeDescription());
        barcodeOrientationTextField.setText(barcode.getOrientationDescription());
        barcodeFormatTextField.setText(barcode.getBarcodeFormatDescription());

        xPosTextField.setText("X1: " + (int) barcode.getX1() + " - X2: " + (int) barcode.getX2());
        yPosTextField.setText("Y1: " + (int) barcode.getY1() + " - Y2: " + (int) barcode.getY2());

        Rectangle2D barcodeBoundary = barcode.getBarcodeBoundary();
        widthTextField.setText((int) barcodeBoundary.getWidth() + "");
        heightTextField.setText((int) barcodeBoundary.getHeight() + "");

        resizeImageLabel();
        repaint();

    }

    public void renderBarcodeAreaImage(Graphics2D g) {

        BufferedImage barcodeImage = barcode.getBarcodeImage();

        if (barcodeImage != null) {
            g.setColor(Color.WHITE);
            g.fill(new Rectangle2D.Double(0, 0, barcodeImageLabel.getWidth(),
                barcodeImageLabel.getHeight()));

            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

            double x =
                (barcodeImageLabel.getWidth() / 2) - ((barcodeImage.getWidth() * realZoom) / 2);
            double y =
                (barcodeImageLabel.getHeight() / 2) - ((barcodeImage.getHeight() * realZoom) / 2);
            AffineTransform at = AffineTransform.getTranslateInstance(x, y);
            at.scale(realZoom, realZoom);
            g.drawRenderedImage(barcodeImage, at);
        }

    }

    private void closeButtonActionPerformed(ActionEvent e) {
        dispose();
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

    public void resizeImageLabel() {
        BufferedImage barcodeImage = barcode.getBarcodeImage();

        Dimension dim = new Dimension((int) (barcodeImage.getWidth() * realZoom),
            (int) (barcodeImage.getHeight() * realZoom));
        if (barcodeImageLabel != null) {
            barcodeImageLabel.setMaximumSize(dim);
            barcodeImageLabel.setMinimumSize(dim);
            barcodeImageLabel.setPreferredSize(dim);
        }
        barcodeImageLabel.revalidate();
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

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                closeButton.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        barcodePreviewPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        barcodeImageLabel = new JImageLabel();
        panel5 = new JPanel();
        zoomBox = new JComboBox();
        zoomInLabel = new JLabel();
        zoomOutLabel = new JLabel();
        barcodeDetailsPanel = new JPanel();
        typeLabel = new JLabel();
        barcodeTypeTextField = new JTextField();
        formatLabel = new JLabel();
        barcodeFormatTextField = new JTextField();
        dataLabel = new JLabel();
        barcodeDataTextField = new JTextField();
        orientationLabel = new JLabel();
        barcodeOrientationTextField = new JTextField();
        xPosLabel = new JLabel();
        xPosTextField = new JTextField();
        yPosLabel = new JLabel();
        yPosTextField = new JTextField();
        widthLabel = new JLabel();
        widthTextField = new JTextField();
        heightLabel = new JLabel();
        heightTextField = new JTextField();
        buttonBar = new JPanel();
        closeButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UI", "BarcodeDetectionDetailsDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {300, 300, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //======== barcodePreviewPanel ========
                {
                    barcodePreviewPanel.setFont(UIManager.getFont("TitledBorder.font"));
                    barcodePreviewPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) barcodePreviewPanel.getLayout()).columnWidths =
                        new int[] {0, 0};
                    ((GridBagLayout) barcodePreviewPanel.getLayout()).rowHeights =
                        new int[] {0, 0, 0};
                    ((GridBagLayout) barcodePreviewPanel.getLayout()).columnWeights =
                        new double[] {1.0, 1.0E-4};
                    ((GridBagLayout) barcodePreviewPanel.getLayout()).rowWeights =
                        new double[] {1.0, 0.0, 1.0E-4};
                    barcodePreviewPanel.setBorder(new CompoundBorder(
                        new TitledBorder(Localizer.localize("UI", "BarcodePreviewBorderTitle")),
                        new EmptyBorder(5, 5, 5, 5)));

                    //======== scrollPane1 ========
                    {
                        scrollPane1.setViewportBorder(null);
                        scrollPane1.setBorder(null);

                        //---- barcodeImageLabel ----
                        barcodeImageLabel.setBddd(this);
                        barcodeImageLabel.setRenderType(JImageLabel.RENDER_BARCODE_AREA_IMAGE);
                        scrollPane1.setViewportView(barcodeImageLabel);
                    }
                    barcodePreviewPanel.add(scrollPane1,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                    //======== panel5 ========
                    {
                        panel5.setLayout(new GridBagLayout());
                        ((GridBagLayout) panel5.getLayout()).columnWidths =
                            new int[] {0, 0, 0, 0, 0, 0};
                        ((GridBagLayout) panel5.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout) panel5.getLayout()).columnWeights =
                            new double[] {1.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout) panel5.getLayout()).rowWeights =
                            new double[] {0.0, 1.0E-4};

                        //---- zoomBox ----
                        zoomBox.setModel(new DefaultComboBoxModel(
                            new String[] {"25%", "50%", "100%", "200%", "400%", "800%"}));
                        zoomBox.setSelectedIndex(2);
                        zoomBox.setFont(UIManager.getFont("ComboBox.font"));
                        zoomBox.addItemListener(new ItemListener() {
                            public void itemStateChanged(ItemEvent e) {
                                zoomBoxItemStateChanged(e);
                            }
                        });
                        panel5.add(zoomBox,
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
                        panel5.add(zoomInLabel,
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
                        panel5.add(zoomOutLabel,
                            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
                    }
                    barcodePreviewPanel.add(panel5,
                        new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(barcodePreviewPanel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

                //======== barcodeDetailsPanel ========
                {
                    barcodeDetailsPanel.setFont(UIManager.getFont("TitledBorder.font"));
                    barcodeDetailsPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) barcodeDetailsPanel.getLayout()).columnWidths =
                        new int[] {0, 0, 0};
                    ((GridBagLayout) barcodeDetailsPanel.getLayout()).rowHeights =
                        new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                    ((GridBagLayout) barcodeDetailsPanel.getLayout()).columnWeights =
                        new double[] {0.0, 1.0, 1.0E-4};
                    ((GridBagLayout) barcodeDetailsPanel.getLayout()).rowWeights =
                        new double[] {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                    barcodeDetailsPanel.setBorder(new CompoundBorder(
                        new TitledBorder(Localizer.localize("UI", "BarcodeDetailsBorderTitle")),
                        new EmptyBorder(5, 5, 5, 5)));

                    //---- typeLabel ----
                    typeLabel.setFont(UIManager.getFont("Label.font"));
                    typeLabel.setText(Localizer.localize("UI", "BarcodeTypeLabel"));
                    barcodeDetailsPanel.add(typeLabel,
                        new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                            GridBagConstraints.VERTICAL, new Insets(0, 0, 5, 5), 0, 0));

                    //---- barcodeTypeTextField ----
                    barcodeTypeTextField.setFont(UIManager.getFont("TextField.font"));
                    barcodeTypeTextField.setEditable(false);
                    barcodeDetailsPanel.add(barcodeTypeTextField,
                        new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                    //---- formatLabel ----
                    formatLabel.setFont(UIManager.getFont("Label.font"));
                    formatLabel.setText(Localizer.localize("UI", "BarcodeFormatLabel"));
                    barcodeDetailsPanel.add(formatLabel,
                        new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                            GridBagConstraints.VERTICAL, new Insets(0, 0, 5, 5), 0, 0));

                    //---- barcodeFormatTextField ----
                    barcodeFormatTextField.setEditable(false);
                    barcodeFormatTextField.setFont(UIManager.getFont("TextField.font"));
                    barcodeDetailsPanel.add(barcodeFormatTextField,
                        new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                    //---- dataLabel ----
                    dataLabel.setFont(UIManager.getFont("Label.font"));
                    dataLabel.setText(Localizer.localize("UI", "BarcodeDataLabel"));
                    barcodeDetailsPanel.add(dataLabel,
                        new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                            GridBagConstraints.VERTICAL, new Insets(0, 0, 5, 5), 0, 0));

                    //---- barcodeDataTextField ----
                    barcodeDataTextField.setFont(UIManager.getFont("TextField.font"));
                    barcodeDataTextField.setEditable(false);
                    barcodeDetailsPanel.add(barcodeDataTextField,
                        new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                    //---- orientationLabel ----
                    orientationLabel.setFont(UIManager.getFont("Label.font"));
                    orientationLabel.setText(Localizer.localize("UI", "BarcodeOrientationLabel"));
                    barcodeDetailsPanel.add(orientationLabel,
                        new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                            GridBagConstraints.VERTICAL, new Insets(0, 0, 5, 5), 0, 0));

                    //---- barcodeOrientationTextField ----
                    barcodeOrientationTextField.setFont(UIManager.getFont("TextField.font"));
                    barcodeOrientationTextField.setEditable(false);
                    barcodeDetailsPanel.add(barcodeOrientationTextField,
                        new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                    //---- xPosLabel ----
                    xPosLabel.setFont(UIManager.getFont("Label.font"));
                    xPosLabel.setText(Localizer.localize("UI", "BarcodeXPosLabel"));
                    barcodeDetailsPanel.add(xPosLabel,
                        new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                            GridBagConstraints.VERTICAL, new Insets(0, 0, 5, 5), 0, 0));

                    //---- xPosTextField ----
                    xPosTextField.setFont(UIManager.getFont("TextField.font"));
                    xPosTextField.setEditable(false);
                    barcodeDetailsPanel.add(xPosTextField,
                        new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                    //---- yPosLabel ----
                    yPosLabel.setFont(UIManager.getFont("Label.font"));
                    yPosLabel.setText(Localizer.localize("UI", "BarcodeYPosLabel"));
                    barcodeDetailsPanel.add(yPosLabel,
                        new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                            GridBagConstraints.VERTICAL, new Insets(0, 0, 5, 5), 0, 0));

                    //---- yPosTextField ----
                    yPosTextField.setFont(UIManager.getFont("TextField.font"));
                    yPosTextField.setEditable(false);
                    barcodeDetailsPanel.add(yPosTextField,
                        new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                    //---- widthLabel ----
                    widthLabel.setFont(UIManager.getFont("Label.font"));
                    widthLabel.setText(Localizer.localize("UI", "BarcodeWidthLabel"));
                    barcodeDetailsPanel.add(widthLabel,
                        new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                            GridBagConstraints.VERTICAL, new Insets(0, 0, 5, 5), 0, 0));

                    //---- widthTextField ----
                    widthTextField.setFont(UIManager.getFont("TextField.font"));
                    widthTextField.setEditable(false);
                    barcodeDetailsPanel.add(widthTextField,
                        new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                    //---- heightLabel ----
                    heightLabel.setFont(UIManager.getFont("Label.font"));
                    heightLabel.setText(Localizer.localize("UI", "BarcodeHeightLabel"));
                    barcodeDetailsPanel.add(heightLabel,
                        new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                            GridBagConstraints.VERTICAL, new Insets(0, 0, 5, 5), 0, 0));

                    //---- heightTextField ----
                    heightTextField.setFont(UIManager.getFont("TextField.font"));
                    heightTextField.setEditable(false);
                    barcodeDetailsPanel.add(heightTextField,
                        new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
                }
                contentPanel.add(barcodeDetailsPanel,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

                //---- closeButton ----
                closeButton.setFont(UIManager.getFont("Button.font"));
                closeButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        closeButtonActionPerformed(e);
                    }
                });
                closeButton.setText(Localizer.localize("UI", "CloseButtonText"));
                buttonBar.add(closeButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(730, 445);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel barcodePreviewPanel;
    private JScrollPane scrollPane1;
    private JImageLabel barcodeImageLabel;
    private JPanel panel5;
    private JComboBox zoomBox;
    private JLabel zoomInLabel;
    private JLabel zoomOutLabel;
    private JPanel barcodeDetailsPanel;
    private JLabel typeLabel;
    private JTextField barcodeTypeTextField;
    private JLabel formatLabel;
    private JTextField barcodeFormatTextField;
    private JLabel dataLabel;
    private JTextField barcodeDataTextField;
    private JLabel orientationLabel;
    private JTextField barcodeOrientationTextField;
    private JLabel xPosLabel;
    private JTextField xPosTextField;
    private JLabel yPosLabel;
    private JTextField yPosTextField;
    private JLabel widthLabel;
    private JTextField widthTextField;
    private JLabel heightLabel;
    private JTextField heightTextField;
    private JPanel buttonBar;
    private JButton closeButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


}
