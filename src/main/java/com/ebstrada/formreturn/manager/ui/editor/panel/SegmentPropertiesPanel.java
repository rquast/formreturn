package com.ebstrada.formreturn.manager.ui.editor.panel;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;

import org.jdesktop.swingx.*;

import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.editor.dialog.SegmentPropertiesDialog;
import com.ebstrada.formreturn.manager.ui.editor.frame.SegmentFrame;
import com.ebstrada.formreturn.manager.util.Measurement;
import com.ebstrada.formreturn.manager.util.graph.GraphUtils;
import com.ebstrada.formreturn.manager.util.graph.SizeAttributes;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class SegmentPropertiesPanel extends JXTaskPane {

    private static final long serialVersionUID = 1L;

    private JGraph _graph;

    private SegmentFrame _segmentFrame;

    private Measurement measurement;

    private int currentUnit = Measurement.PIXELS;

    public SegmentPropertiesPanel(JGraph _graph, SegmentFrame _segmentFrame) {
        this._graph = _graph;
        this._segmentFrame = _segmentFrame;
        measurement = new Measurement();
        measurement.setFrom(Measurement.PIXELS);
        measurement.setTo(Measurement.PIXELS);
        initComponents();
        setSegmentNames();
        restorePageAttributes();
    }

    private void setSegmentNames() {
        List<String> segmentSizeNames = PreferencesManager.getSegmentSizeNames();

        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        for (String segmentSizeName : segmentSizeNames) {
            dcbm.addElement(segmentSizeName);
        }

        presetSizeComboBox.setModel(dcbm);
    }

    public void restorePageAttributes() {

        PageAttributes pageAttributes = _graph.getPageAttributes();

        if (pageAttributes != null) {

            Dimension d = pageAttributes.getDimension();
            double attrPageWidth = d.getWidth();
            double attrPageHeight = d.getHeight();

            if (pageAttributes.getPageSize() != null) {
                presetSizeComboBox.setSelectedItem(pageAttributes.getPageSize());
            }

            if (!(GraphUtils
                .sizesMatch(getPageDefaultSize((String) presetSizeComboBox.getSelectedObjects()[0]),
                    getPageSize()))) {
                presetSizeComboBox.setSelectedItem("Custom");
            }

            widthSpinner.setValue(new Double(attrPageWidth));
            heightSpinner.setValue(new Double(attrPageHeight));

            orientationComboBox.setSelectedItem(
                PageAttributes.getOrientationText(pageAttributes.getOrientation()));

        }

    }

    public PageAttributes getPageAttributes() {

        PageAttributes pageAttributes = _graph.getPageAttributes();

        if (pageAttributes == null) {
            pageAttributes = new PageAttributes();
        }

        pageAttributes.setPageSize((String) presetSizeComboBox.getSelectedItem());
        pageAttributes.setDimension(new Dimension(getPixelSegmentWidth(), getPixelSegmentHeight()));
        pageAttributes.setLeftMargin(0);
        pageAttributes.setRightMargin(0);
        pageAttributes.setTopMargin(0);
        pageAttributes.setBottomMargin(0);
        if (((String) orientationComboBox.getSelectedObjects()[0]).equals("Portrait")) {
            pageAttributes.setOrientation(PageAttributes.PORTRAIT);
        } else if (((String) orientationComboBox.getSelectedObjects()[0]).equals("Landscape")) {
            pageAttributes.setOrientation(PageAttributes.LANDSCAPE);
        } else {
            pageAttributes.setOrientation(PageAttributes.PORTRAIT);
        }

        return pageAttributes;
    }

    public int getPixelSegmentHeight() {
        measurement.setFrom(currentUnit);
        measurement.setTo(Measurement.PIXELS);
        return (int) Math.round(measurement.convert((Double) heightSpinner.getValue()));
    }

    public int getPixelSegmentWidth() {
        measurement.setFrom(currentUnit);
        measurement.setTo(Measurement.PIXELS);
        return (int) Math.round(measurement.convert((Double) widthSpinner.getValue()));
    }

    private void applySettingsButtonActionPerformed(ActionEvent e) {

        if (!(GraphUtils
            .sizesMatch(getPageDefaultSize((String) presetSizeComboBox.getSelectedObjects()[0]),
                getPageSize()))) {
            presetSizeComboBox.setSelectedItem("Custom");
        }

        if (!GraphUtils.checkPageSettings(getPageSize())) {
            return;
        }

        _segmentFrame.setPageAttributes(getPageAttributes());
        _segmentFrame.restorePageAttributes();
    }

    private void presetSizeComboBoxActionPerformed(ActionEvent e) {
        measurementComboBox.setSelectedIndex(0);
        String size = (String) presetSizeComboBox.getSelectedObjects()[0];
        setPageSize(size);
    }

    private void setPageSize(SizeAttributes sizeAttributes) {
        widthSpinner.setValue(new Double(sizeAttributes.getWidth()));
        heightSpinner.setValue(new Double(sizeAttributes.getHeight()));
    }

    private void setPageSize(String size) {

        if (size.equals("Custom")) {
            return;
        }

        setPageSize(getPageDefaultSize(size));

    }

    private SizeAttributes getPageSize() {
        SizeAttributes sizeAttributes = new SizeAttributes();
        sizeAttributes.setWidth(getPixelSegmentWidth());
        sizeAttributes.setHeight(getPixelSegmentHeight());
        sizeAttributes.setOrientation(PageAttributes.PORTRAIT);
        return sizeAttributes;
    }

    private SizeAttributes getPageDefaultSize(String size) {

        String layout = (String) orientationComboBox.getSelectedObjects()[0];
        SizeAttributes sizeAttributes = new SizeAttributes();

        if (layout.equals("Portrait")) {
            // get portrait defaults
            sizeAttributes = GraphUtils
                .getDefaultSizeAttributes(SizeAttributes.SEGMENT, SizeAttributes.PORTRAIT, size);
        } else {
            // Landscape Values
            sizeAttributes = GraphUtils
                .getDefaultSizeAttributes(SizeAttributes.SEGMENT, SizeAttributes.LANDSCAPE, size);
        }

        return sizeAttributes;

    }

    private void measurementComboBoxActionPerformed(ActionEvent e) {
        measurement.setFrom(currentUnit);
        measurement.setTo(getSelectedUnit());

        if (getSelectedUnit() == Measurement.PIXELS) {

            widthSpinner.setValue(
                new Double(Math.round(measurement.convert((Double) widthSpinner.getValue()))));
            heightSpinner.setValue(
                new Double(Math.round(measurement.convert((Double) heightSpinner.getValue()))));

        } else {

            widthSpinner
                .setValue(new Double(measurement.convert((Double) widthSpinner.getValue())));
            heightSpinner
                .setValue(new Double(measurement.convert((Double) heightSpinner.getValue())));

        }

        currentUnit = getSelectedUnit();
    }

    private int getSelectedUnit() {
        String selectedUnit = (String) measurementComboBox.getSelectedItem();
        if (selectedUnit.equalsIgnoreCase("Pixels")) {
            return Measurement.PIXELS;
        } else if (selectedUnit.equalsIgnoreCase("Millimeters")) {
            return Measurement.MILLIMETERS;
        } else if (selectedUnit.equalsIgnoreCase("Centimeters")) {
            return Measurement.CENTIMETERS;
        } else if (selectedUnit.equalsIgnoreCase("Inches")) {
            return Measurement.INCHES;
        } else {
            return currentUnit;
        }
    }

    private void propertiesButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SegmentPropertiesDialog spd =
                    new SegmentPropertiesDialog((Frame) getRootPane().getTopLevelAncestor(),
                        _graph.getPageAttributes());
                spd.setSelectedFrame(getSegmentFrame());
                spd.setModal(true);
                spd.setVisible(true);
                if (spd.getDialogResult() == JOptionPane.OK_OPTION) {
                    applySettingsButtonActionPerformed(null);
                }
            }
        });
    }

    private void orientationComboBoxActionPerformed(ActionEvent e) {
        measurementComboBox.setSelectedIndex(0);
        String size = (String) presetSizeComboBox.getSelectedObjects()[0];
        setPageSize(size);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel4 = new JPanel();
        measurementLabel = new JLabel();
        measurementComboBox = new JComboBox();
        orientationLabel = new JLabel();
        orientationComboBox = new JComboBox();
        presetSizeLabel = new JLabel();
        presetSizeComboBox = new JComboBox();
        widthLabel = new JLabel();
        widthSpinner = new JSpinner();
        heightLabel = new JLabel();
        heightSpinner = new JSpinner();
        applySettingsButton = new JButton();
        otherPropertiesLabel = new JLabel();
        propertiesButton = new JButton();

        //======== this ========
        setOpaque(false);
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout) contentPane.getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) contentPane.getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout) contentPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) contentPane.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
        this.setTitle(Localizer.localize("UI", "SegmentPanelTitle"));

        //======== panel4 ========
        {
            panel4.setBorder(null);
            panel4.setOpaque(false);
            panel4.setLayout(new GridBagLayout());
            ((GridBagLayout) panel4.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel4.getLayout()).rowHeights =
                new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 43, 0, 0, 0};
            ((GridBagLayout) panel4.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel4.getLayout()).rowWeights =
                new double[] {0.0, 1.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 1.0,
                    1.0E-4};

            //---- measurementLabel ----
            measurementLabel.setFont(UIManager.getFont("Label.font"));
            measurementLabel.setText(Localizer.localize("UI", "SegmentPanelMeasurementLabel"));
            panel4.add(measurementLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 3, 0), 0, 0));

            //---- measurementComboBox ----
            measurementComboBox.setModel(new DefaultComboBoxModel(
                new String[] {"Pixels", "Millimeters", "Centimeters", "Inches"}));
            measurementComboBox.setFont(UIManager.getFont("ComboBox.font"));
            measurementComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    measurementComboBoxActionPerformed(e);
                }
            });
            panel4.add(measurementComboBox,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 3, 0), 0, 0));

            //---- orientationLabel ----
            orientationLabel.setFont(UIManager.getFont("Label.font"));
            orientationLabel.setText(Localizer.localize("UI", "PagePropertiesOrientationLabel"));
            panel4.add(orientationLabel,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 3, 0), 0, 0));

            //---- orientationComboBox ----
            orientationComboBox
                .setModel(new DefaultComboBoxModel(new String[] {"Portrait", "Landscape"}));
            orientationComboBox.setFont(UIManager.getFont("ComboBox.font"));
            orientationComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    orientationComboBoxActionPerformed(e);
                }
            });
            panel4.add(orientationComboBox,
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 3, 0), 0, 0));

            //---- presetSizeLabel ----
            presetSizeLabel.setFont(UIManager.getFont("Label.font"));
            presetSizeLabel.setText(Localizer.localize("UI", "SegmentPanelPresetSizeLabel"));
            panel4.add(presetSizeLabel,
                new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 3, 0), 0, 0));

            //---- presetSizeComboBox ----
            presetSizeComboBox.setFont(UIManager.getFont("ComboBox.font"));
            presetSizeComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    presetSizeComboBoxActionPerformed(e);
                }
            });
            panel4.add(presetSizeComboBox,
                new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 3, 0), 0, 0));

            //---- widthLabel ----
            widthLabel.setFont(UIManager.getFont("Label.font"));
            widthLabel.setText(Localizer.localize("UI", "SegmentPanelWidthLabel"));
            panel4.add(widthLabel,
                new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 3, 0), 0, 0));

            //---- widthSpinner ----
            widthSpinner.setModel(new SpinnerNumberModel(0.0, 0.0, 6000.0, 1.0));
            widthSpinner.setFont(UIManager.getFont("Spinner.font"));
            panel4.add(widthSpinner,
                new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 3, 0), 0, 0));

            //---- heightLabel ----
            heightLabel.setFont(UIManager.getFont("Label.font"));
            heightLabel.setText(Localizer.localize("UI", "SegmentPanelHeightLabel"));
            panel4.add(heightLabel,
                new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 3, 0), 0, 0));

            //---- heightSpinner ----
            heightSpinner.setModel(new SpinnerNumberModel(0.0, 0.0, 6000.0, 1.0));
            heightSpinner.setFont(UIManager.getFont("Spinner.font"));
            panel4.add(heightSpinner,
                new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 3, 0), 0, 0));

            //---- applySettingsButton ----
            applySettingsButton.setFont(UIManager.getFont("Button.font"));
            applySettingsButton.setFocusPainted(false);
            applySettingsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    applySettingsButtonActionPerformed(e);
                }
            });
            applySettingsButton.setText(Localizer.localize("UI", "ApplySettingsButtonText"));
            panel4.add(applySettingsButton,
                new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 3, 0), 0, 0));

            //---- otherPropertiesLabel ----
            otherPropertiesLabel.setFont(UIManager.getFont("Label.font"));
            otherPropertiesLabel.setText(Localizer.localize("UI", "OtherPropertiesLabel"));
            panel4.add(otherPropertiesLabel,
                new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 3, 0), 0, 0));

            //---- propertiesButton ----
            propertiesButton.setFont(UIManager.getFont("Button.font"));
            propertiesButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    propertiesButtonActionPerformed(e);
                }
            });
            propertiesButton.setText(Localizer.localize("UI", "SetPropertiesButtonText"));
            panel4.add(propertiesButton,
                new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel4,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel4;
    private JLabel measurementLabel;
    private JComboBox measurementComboBox;
    private JLabel orientationLabel;
    private JComboBox orientationComboBox;
    private JLabel presetSizeLabel;
    private JComboBox presetSizeComboBox;
    private JLabel widthLabel;
    private JSpinner widthSpinner;
    private JLabel heightLabel;
    private JSpinner heightSpinner;
    private JButton applySettingsButton;
    private JLabel otherPropertiesLabel;
    private JButton propertiesButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public SegmentFrame getSegmentFrame() {
        return _segmentFrame;
    }

    public void setsegmentFrame(SegmentFrame segmentFrame) {
        this._segmentFrame = segmentFrame;
    }
}
