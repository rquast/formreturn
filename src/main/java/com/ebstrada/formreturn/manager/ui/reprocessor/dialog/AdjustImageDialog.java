package com.ebstrada.formreturn.manager.ui.reprocessor.dialog;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.util.image.ImageUtil;

public class AdjustImageDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private int dialogResult = JOptionPane.CANCEL_OPTION;

    private BufferedImage sourceImage;

    private BufferedImage displayImage;

    private Float realZoom = 1.0f;

    private double angle = 0.0d;

    private long lastChange;

    public AdjustImageDialog(Frame owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(okButton);
        imageScrollPane.getVerticalScrollBar().setUnitIncrement(30);
        imageScrollPane.getVerticalScrollBar().setBlockIncrement(90);
        imageScrollPane.getHorizontalScrollBar().setUnitIncrement(30);
        imageScrollPane.getHorizontalScrollBar().setBlockIncrement(90);
    }

    public AdjustImageDialog(Dialog owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(okButton);
        imageScrollPane.getVerticalScrollBar().setUnitIncrement(30);
        imageScrollPane.getVerticalScrollBar().setBlockIncrement(90);
        imageScrollPane.getHorizontalScrollBar().setUnitIncrement(30);
        imageScrollPane.getHorizontalScrollBar().setBlockIncrement(90);
    }

    private void okButtonActionPerformed(ActionEvent e) {
        setDialogResult(JOptionPane.OK_OPTION);
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void rotateImage(final double angle) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                displayImage = ImageUtil.rotate(sourceImage, angle, sourceImage.getWidth() / 2,
                    sourceImage.getHeight() / 2);
                resizeImageLabel();
                repaint();
            }
        });
    }

    public void setImage(BufferedImage sourceImage) {
        this.sourceImage = sourceImage;
        this.displayImage =
            sourceImage.getSubimage(0, 0, sourceImage.getWidth(), sourceImage.getHeight());
        resizeImageLabel();
        repaint();
    }

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    public double getRotationAngle() {
        return this.angle;
    }

    public void renderImagePreview(Graphics2D g) {
        if (displayImage != null) {

            g.setColor(Color.darkGray);
            g.fill(new Rectangle2D.Double(0, 0, imageLabel.getWidth(), imageLabel.getHeight()));
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            double x = (imageLabel.getWidth() / 2) - ((displayImage.getWidth() * realZoom) / 2);
            double y = (imageLabel.getHeight() / 2) - ((displayImage.getHeight() * realZoom) / 2);
            AffineTransform at = AffineTransform.getTranslateInstance(x, y);
            at.scale(realZoom, realZoom);
            g.drawRenderedImage(displayImage, at);

        }
    }

    public void resizeImageLabel() {
        Dimension dim = new Dimension((int) (displayImage.getWidth() * realZoom),
            (int) (displayImage.getHeight() * realZoom));
        if (imageLabel != null) {
            imageLabel.setMaximumSize(dim);
            imageLabel.setMinimumSize(dim);
            imageLabel.setPreferredSize(dim);
        }
        imageLabel.revalidate();
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

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                okButton.requestFocusInWindow();
            }
        });
    }

    private void imageRotationSpinnerStateChanged(ChangeEvent e) {
        this.angle = (Double) imageRotationSpinner.getValue();
        if (Math.abs(lastChange - System.currentTimeMillis()) > 2000) {
            rotateImage(this.angle);
            imageRotationSlider.setValue((int) (angle * 10.0d));
            lastChange = System.currentTimeMillis();
        }
    }

    private void imageRotationSliderStateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        double preAngle = imageRotationSlider.getValue();
        this.angle = preAngle / 10.0d;
        imageRotationSpinner.setValue(angle);
        if (!source.getValueIsAdjusting()) {
            rotateImage(this.angle);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        imageScrollPane = new JScrollPane();
        imageLabel = new JImageLabel();
        panel3 = new JPanel();
        imageRotationSpinner = new JSpinner();
        panel1 = new JPanel();
        minus180DegreesLabel = new JLabel();
        imageRotationSlider = new JSlider();
        plus180DegreesLabel = new JLabel();
        panel2 = new JPanel();
        zoomLabel = new JLabel();
        zoomBox = new JComboBox();
        zoomInLabel = new JLabel();
        zoomOutLabel = new JLabel();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "ReprocessorFrameAdjustImageDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights =
                    new double[] {1.0, 0.0, 0.0, 0.0, 1.0E-4};

                //======== imageScrollPane ========
                {

                    //---- imageLabel ----
                    imageLabel.setAid(this);
                    imageLabel.setRenderType(JImageLabel.RENDER_REPROCESSOR_IMAGE_PREVIEW);
                    imageScrollPane.setViewportView(imageLabel);
                }
                contentPanel.add(imageScrollPane,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                //======== panel3 ========
                {
                    panel3.setOpaque(false);
                    panel3.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel3.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                    ((GridBagLayout) panel3.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout) panel3.getLayout()).columnWeights =
                        new double[] {1.0, 0.0, 1.0, 1.0E-4};
                    ((GridBagLayout) panel3.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                    //---- imageRotationSpinner ----
                    imageRotationSpinner.setModel(new SpinnerNumberModel(0.0, -180.0, 180.0, 0.1));
                    imageRotationSpinner.setFont(UIManager.getFont("Spinner.font"));
                    imageRotationSpinner.addChangeListener(new ChangeListener() {
                        @Override public void stateChanged(ChangeEvent e) {
                            imageRotationSpinnerStateChanged(e);
                        }
                    });
                    panel3.add(imageRotationSpinner,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
                }
                contentPanel.add(panel3,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                //======== panel1 ========
                {
                    panel1.setOpaque(false);
                    panel1.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel1.getLayout()).columnWidths =
                        new int[] {105, 0, 0, 0, 100, 0};
                    ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout) panel1.getLayout()).columnWeights =
                        new double[] {0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                    //---- minus180DegreesLabel ----
                    minus180DegreesLabel.setFont(UIManager.getFont("Label.font"));
                    minus180DegreesLabel
                        .setText(Localizer.localize("UI", "ReprocessorFrameMinus180DegreesLabel"));
                    panel1.add(minus180DegreesLabel,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                    //---- imageRotationSlider ----
                    imageRotationSlider.setMaximum(1800);
                    imageRotationSlider.setMinimum(-1800);
                    imageRotationSlider.setValue(0);
                    imageRotationSlider.addChangeListener(new ChangeListener() {
                        @Override public void stateChanged(ChangeEvent e) {
                            imageRotationSliderStateChanged(e);
                        }
                    });
                    panel1.add(imageRotationSlider,
                        new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                    //---- plus180DegreesLabel ----
                    plus180DegreesLabel.setFont(UIManager.getFont("Label.font"));
                    plus180DegreesLabel
                        .setText(Localizer.localize("UI", "ReprocessorFramePlus180DegreesLabel"));
                    panel1.add(plus180DegreesLabel,
                        new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
                }
                contentPanel.add(panel1,
                    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                //======== panel2 ========
                {
                    panel2.setOpaque(false);
                    panel2.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel2.getLayout()).columnWidths =
                        new int[] {0, 0, 0, 0, 0, 0, 0};
                    ((GridBagLayout) panel2.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout) panel2.getLayout()).columnWeights =
                        new double[] {1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                    ((GridBagLayout) panel2.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                    //---- zoomLabel ----
                    zoomLabel.setFont(UIManager.getFont("Label.font"));
                    zoomLabel.setText(Localizer.localize("UI", "ReprocessorFrameZoomLabel"));
                    panel2.add(zoomLabel,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                    //---- zoomBox ----
                    zoomBox.setModel(new DefaultComboBoxModel(
                        new String[] {"5%", "10%", "25%", "50%", "100%", "200%", "500%", "1000%"}));
                    zoomBox.setSelectedIndex(4);
                    zoomBox.setFont(UIManager.getFont("ComboBox.font"));
                    zoomBox.addItemListener(new ItemListener() {
                        @Override public void itemStateChanged(ItemEvent e) {
                            zoomBoxItemStateChanged(e);
                        }
                    });
                    panel2.add(zoomBox,
                        new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
                        new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
                        new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
                }
                contentPanel.add(panel2,
                    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 0.0};

                //---- okButton ----
                okButton.setText("OK");
                okButton.setFont(UIManager.getFont("Button.font"));
                okButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                okButton.setText(Localizer.localize("UI", "OKButtonText"));
                buttonBar.add(okButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UI", "CancelButtonText"));
                buttonBar.add(cancelButton,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(750, 525);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JScrollPane imageScrollPane;
    private JImageLabel imageLabel;
    private JPanel panel3;
    private JSpinner imageRotationSpinner;
    private JPanel panel1;
    private JLabel minus180DegreesLabel;
    private JSlider imageRotationSlider;
    private JLabel plus180DegreesLabel;
    private JPanel panel2;
    private JLabel zoomLabel;
    private JComboBox zoomBox;
    private JLabel zoomInLabel;
    private JLabel zoomOutLabel;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public BufferedImage getDisplayImage() {
        return this.displayImage;
    }

}
