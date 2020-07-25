package com.ebstrada.formreturn.manager.ui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.ui.DocumentAttributes;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.util.Measurement;
import com.ebstrada.formreturn.manager.util.graph.GraphUtils;
import com.ebstrada.formreturn.manager.util.graph.SizeAttributes;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class FormSetupDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

    private int dialogResult = javax.swing.JOptionPane.CANCEL_OPTION;

    private PageAttributes pageAttributes;

    private DocumentAttributes documentAttributes;

    private Main mainInstance;

    private Measurement measurement;

    private int currentUnit = Measurement.PIXELS;

    public FormSetupDialog(Frame parent, boolean modal) {
        this(parent, modal, Localizer.localize("UI", "FormSetupDialogTitle"));
    }

    // a new form constructor
    public FormSetupDialog(Frame parent, boolean modal, String title) {
        super(parent, modal);
        setTitle(title);
        measurement = new Measurement();
        measurement.setFrom(Measurement.PIXELS);
        measurement.setTo(Measurement.PIXELS);
        pageAttributes = new PageAttributes();
        mainInstance = Main.getInstance();
        initComponents();
        setFormNames();
        setReportName(mainInstance.getFirstFreeFormName());

        SizeAttributes sa = PreferencesManager.getDefaultFormSizeAttributes();
        if (sa.getOrientation() == SizeAttributes.PORTRAIT) {
            orientation.setSelectedItem("Portrait");
        } else {
            orientation.setSelectedItem("Landscape");
        }

        pageSize.setSelectedItem(sa.getName());
        if (pageSize.getSelectedIndex() == -1) {
            pageSize.setSelectedIndex(0);
        }

        setPageSize(sa);
        getRootPane().setDefaultButton(okButton);
    }

    private void setFormNames() {
        List<String> formSizeNames = PreferencesManager.getFormSizeNames();

        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        for (String formSizeName : formSizeNames) {
            dcbm.addElement(formSizeName);
        }

        pageSize.setModel(dcbm);
    }

    public void restorePageAttributes() {

        Dimension d = pageAttributes.getDimension();
        int attrPageWidth = (new Double(d.getWidth())).intValue();
        int attrPageHeight = (new Double(d.getHeight())).intValue();

        if (pageAttributes.getPageSize() != null) {
            pageSize.setSelectedItem(pageAttributes.getPageSize());
        }

        if (!(GraphUtils.sizesMatch(getPageDefaultSize((String) pageSize.getSelectedObjects()[0]),
            getPageSize()))) {
            pageSize.setSelectedItem("Custom");
        }

        setReportName(documentAttributes.getName());
        setPageWidth(attrPageWidth);
        setPageHeight(attrPageHeight);
        setLeftMargin(pageAttributes.getLeftMargin());
        setRightMargin(pageAttributes.getRightMargin());
        setTopMargin(pageAttributes.getTopMargin());
        setBottomMargin(pageAttributes.getBottomMargin());
        setOrientation(PageAttributes.getOrientationText(pageAttributes.getOrientation()));

    }

    public DocumentAttributes getDocumentAttributes() {
        if (documentAttributes == null) {
            documentAttributes = new DocumentAttributes();
        }

        documentAttributes.setDocumentType(DocumentAttributes.FORM);
        documentAttributes.setName(getReportName());

        return documentAttributes;
    }

    public PageAttributes getPageAttributes() {

        if (pageAttributes == null) {
            pageAttributes = new PageAttributes();
        }

        pageAttributes.setPageSize((String) pageSize.getSelectedItem());
        pageAttributes.setDimension(new Dimension(getPixelPageWidth(), getPixelPageHeight()));
        pageAttributes.setLeftMargin(getPixelLeftMargin());
        pageAttributes.setRightMargin(getPixelRightMargin());
        pageAttributes.setTopMargin(getPixelTopMargin());
        pageAttributes.setBottomMargin(getPixelBottomMargin());
        pageAttributes.setOrientation(getOrientation());

        return pageAttributes;
    }

    private void measurementComboBoxActionPerformed(ActionEvent e) {

        measurement.setFrom(currentUnit);
        measurement.setTo(getSelectedUnit());

        if (getSelectedUnit() == Measurement.PIXELS) {

            pageWidth.setValue(
                new Double(Math.round(measurement.convert((Double) pageWidth.getValue()))));
            pageHeight.setValue(
                new Double(Math.round(measurement.convert((Double) pageHeight.getValue()))));
            leftMargin.setValue(
                new Double(Math.round(measurement.convert((Double) leftMargin.getValue()))));
            rightMargin.setValue(
                new Double(Math.round(measurement.convert((Double) rightMargin.getValue()))));
            topMargin.setValue(
                new Double(Math.round(measurement.convert((Double) topMargin.getValue()))));
            bottomMargin.setValue(
                new Double(Math.round(measurement.convert((Double) bottomMargin.getValue()))));

        } else {

            pageWidth.setValue(new Double(measurement.convert((Double) pageWidth.getValue())));
            pageHeight.setValue(new Double(measurement.convert((Double) pageHeight.getValue())));
            leftMargin.setValue(new Double(measurement.convert((Double) leftMargin.getValue())));
            rightMargin.setValue(new Double(measurement.convert((Double) rightMargin.getValue())));
            topMargin.setValue(new Double(measurement.convert((Double) topMargin.getValue())));
            bottomMargin
                .setValue(new Double(measurement.convert((Double) bottomMargin.getValue())));

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

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                okButton.requestFocusInWindow();
            }
        });
    }

    @SuppressWarnings("serial") private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        panel3 = new JPanel();
        nameLabel = new JLabel();
        formName = new JTextField();
        panel2 = new JPanel();
        measurementLabel = new JLabel();
        measurementComboBox = new JComboBox();
        panel1 = new JPanel();
        orientationLabel = new JLabel();
        orientation = new JComboBox();
        leftMarginLabel = new JLabel();
        leftMargin = new JSpinner();
        presetSizeLabel = new JLabel();
        pageSize = new JComboBox();
        rightMarginLabel = new JLabel();
        rightMargin = new JSpinner();
        widthLabel = new JLabel();
        pageWidth = new JSpinner();
        topMarginLabel = new JLabel();
        topMargin = new JSpinner();
        heightLabel = new JLabel();
        pageHeight = new JSpinner();
        bottomMarginLabel = new JLabel();
        bottomMargin = new JSpinner();
        buttonBar = new JPanel();
        helpLabel = new JHelpLabel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            dialogPane.setLayout(new GridBagLayout());
            ((GridBagLayout)dialogPane.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout)dialogPane.getLayout()).rowHeights = new int[] {0, 0, 0};
            ((GridBagLayout)dialogPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout)dialogPane.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};

                //======== panel3 ========
                {
                    panel3.setBorder(new EmptyBorder(0, 0, 5, 0));
                    panel3.setOpaque(false);
                    panel3.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {0, 0, 0};
                    ((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                    ((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                    //---- nameLabel ----
                    nameLabel.setFont(UIManager.getFont("Label.font"));
                    nameLabel.setText(Localizer.localize("UI", "FormNameLabel"));
                    panel3.add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- formName ----
                    formName.setFont(UIManager.getFont("TextField.font"));
                    panel3.add(formName, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(panel3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));

                //======== panel2 ========
                {
                    panel2.setOpaque(false);
                    panel2.setBorder(new CompoundBorder(
                        new MatteBorder(1, 0, 0, 0, Color.lightGray),
                        new EmptyBorder(5, 0, 5, 0)));
                    panel2.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                    ((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                    //---- measurementLabel ----
                    measurementLabel.setFont(UIManager.getFont("Label.font"));
                    measurementLabel.setText(Localizer.localize("UI", "MeasurementLabel"));
                    panel2.add(measurementLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- measurementComboBox ----
                    measurementComboBox.setModel(new DefaultComboBoxModel(new String[] {
                        "Pixels",
                        "Millimeters",
                        "Centimeters",
                        "Inches"
                    }));
                    measurementComboBox.setFont(UIManager.getFont("ComboBox.font"));
                    measurementComboBox.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            measurementComboBoxActionPerformed(e);
                        }
                    });
                    panel2.add(measurementComboBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(panel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

                //======== panel1 ========
                {
                    panel1.setBorder(new CompoundBorder(
                        new MatteBorder(1, 0, 0, 0, Color.lightGray),
                        new EmptyBorder(5, 0, 5, 0)));
                    panel1.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 65, 15, 0, 60, 0};
                    ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
                    ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 1.0, 1.0E-4};
                    ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0, 1.0E-4};

                    //---- orientationLabel ----
                    orientationLabel.setFont(UIManager.getFont("Label.font"));
                    orientationLabel.setText(Localizer.localize("UI", "FormOrientationLabel"));
                    panel1.add(orientationLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- orientation ----
                    orientation.setModel(new DefaultComboBoxModel(new String[] {
                        "Portrait",
                        "Landscape"
                    }));
                    orientation.setFont(UIManager.getFont("ComboBox.font"));
                    orientation.setPrototypeDisplayValue("xxxxxxxxxx");
                    orientation.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            orientationItemStateChanged(e);
                        }
                    });
                    panel1.add(orientation, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- leftMarginLabel ----
                    leftMarginLabel.setFont(UIManager.getFont("Label.font"));
                    leftMarginLabel.setText(Localizer.localize("UI", "FormLeftMarginLabel"));
                    panel1.add(leftMarginLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- leftMargin ----
                    leftMargin.setFont(UIManager.getFont("Spinner.font"));
                    leftMargin.setModel(new SpinnerNumberModel(0.0, 0.0, 5990.0, 1.0));
                    panel1.add(leftMargin, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- presetSizeLabel ----
                    presetSizeLabel.setFont(UIManager.getFont("Label.font"));
                    presetSizeLabel.setText(Localizer.localize("UI", "FormPresetSizeLabel"));
                    panel1.add(presetSizeLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- pageSize ----
                    pageSize.setFont(UIManager.getFont("ComboBox.font"));
                    pageSize.setPrototypeDisplayValue("xxxxxxxxxx");
                    pageSize.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            pageSizeItemStateChanged(e);
                        }
                    });
                    panel1.add(pageSize, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- rightMarginLabel ----
                    rightMarginLabel.setFont(UIManager.getFont("Label.font"));
                    rightMarginLabel.setText(Localizer.localize("UI", "FormRightMarginLabel"));
                    panel1.add(rightMarginLabel, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- rightMargin ----
                    rightMargin.setFont(UIManager.getFont("Spinner.font"));
                    rightMargin.setModel(new SpinnerNumberModel(0.0, 0.0, 5990.0, 1.0));
                    panel1.add(rightMargin, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- widthLabel ----
                    widthLabel.setFont(UIManager.getFont("Label.font"));
                    widthLabel.setText(Localizer.localize("UI", "FormWidthLabel"));
                    panel1.add(widthLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- pageWidth ----
                    pageWidth.setModel(new SpinnerNumberModel(0.0, 0.0, 5990.0, 1.0));
                    pageWidth.setFont(UIManager.getFont("Spinner.font"));
                    panel1.add(pageWidth, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- topMarginLabel ----
                    topMarginLabel.setFont(UIManager.getFont("Label.font"));
                    topMarginLabel.setText(Localizer.localize("UI", "FormTopMarginLabel"));
                    panel1.add(topMarginLabel, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- topMargin ----
                    topMargin.setFont(UIManager.getFont("Spinner.font"));
                    topMargin.setModel(new SpinnerNumberModel(0.0, 0.0, 5990.0, 1.0));
                    panel1.add(topMargin, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- heightLabel ----
                    heightLabel.setFont(UIManager.getFont("Label.font"));
                    heightLabel.setText(Localizer.localize("UI", "FormHeightLabel"));
                    panel1.add(heightLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- pageHeight ----
                    pageHeight.setModel(new SpinnerNumberModel(0.0, 0.0, 5990.0, 1.0));
                    pageHeight.setFont(UIManager.getFont("Spinner.font"));
                    panel1.add(pageHeight, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- bottomMarginLabel ----
                    bottomMarginLabel.setFont(UIManager.getFont("Label.font"));
                    bottomMarginLabel.setText(Localizer.localize("UI", "FormBottomMarginLabel"));
                    panel1.add(bottomMarginLabel, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- bottomMargin ----
                    bottomMargin.setFont(UIManager.getFont("Spinner.font"));
                    bottomMargin.setModel(new SpinnerNumberModel(0.0, 0.0, 5990.0, 1.0));
                    panel1.add(bottomMargin, new GridBagConstraints(4, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(panel1, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

            //======== buttonBar ========
            {
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 0, 5, 75, 6, 75, 0};
                ((GridBagLayout)buttonBar.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
                ((GridBagLayout)buttonBar.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                //---- helpLabel ----
                helpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                helpLabel.setHelpGUID("form-setup");
                helpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                buttonBar.add(helpLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));

                //---- okButton ----
                okButton.setFont(UIManager.getFont("Button.font"));
                okButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/accept.png")));
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                okButton.setText(Localizer.localize("UI", "OKButtonText"));
                buttonBar.add(okButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));

                //---- cancelButton ----
                cancelButton.setFocusPainted(false);
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UI", "CancelButtonText"));
                buttonBar.add(cancelButton, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(dialogPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        setSize(550, 305);
        setLocationRelativeTo(null);
        // //GEN-END:initComponents
    }

    private void okButtonActionPerformed(ActionEvent e) {

        if (!(GraphUtils.sizesMatch(getPageDefaultSize((String) pageSize.getSelectedObjects()[0]),
            getPageSize()))) {
            pageSize.setSelectedItem("Custom");
        }

        if (!GraphUtils.checkPageSettings(getPageSize())) {
            return;
        }

        setDialogResult(JOptionPane.OK_OPTION);
        dispose();

    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        setDialogResult(JOptionPane.CANCEL_OPTION);
        dispose();
    }

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    private void setPageSize(SizeAttributes sizeAttributes) {
        setPageWidth(sizeAttributes.getWidth());
        setPageHeight(sizeAttributes.getHeight());
        setLeftMargin(sizeAttributes.getLeftMargin());
        setRightMargin(sizeAttributes.getRightMargin());
        setTopMargin(sizeAttributes.getTopMargin());
        setBottomMargin(sizeAttributes.getBottomMargin());
    }

    private void setPageSize(String size) {

        if (size.equals("Custom")) {
            return;
        }

        setPageSize(getPageDefaultSize(size));

    }

    private SizeAttributes getPageSize() {
        SizeAttributes sizeAttributes = new SizeAttributes();
        sizeAttributes.setWidth(getPixelPageWidth());
        sizeAttributes.setHeight(getPixelPageHeight());
        sizeAttributes.setLeftMargin(getPixelLeftMargin());
        sizeAttributes.setRightMargin(getPixelRightMargin());
        sizeAttributes.setTopMargin(getPixelTopMargin());
        sizeAttributes.setBottomMargin(getPixelBottomMargin());
        sizeAttributes.setOrientation(getOrientation());
        return sizeAttributes;
    }

    private SizeAttributes getPageDefaultSize(String size) {

        String layout = (String) orientation.getSelectedObjects()[0];

        SizeAttributes sizeAttributes = new SizeAttributes();

        if (layout.equals("Portrait")) {
            // get portrait defaults
            sizeAttributes = GraphUtils
                .getDefaultSizeAttributes(SizeAttributes.FORM, SizeAttributes.PORTRAIT, size);

        } else {
            // Landscape Values
            sizeAttributes = GraphUtils
                .getDefaultSizeAttributes(SizeAttributes.FORM, SizeAttributes.LANDSCAPE, size);

        }
        return sizeAttributes;

    }

    private void pageSizeItemStateChanged(ItemEvent e) {
        measurementComboBox.setSelectedIndex(0);
        Object[] selectedObjects = pageSize.getSelectedObjects();
        if (selectedObjects != null && selectedObjects.length > 0) {
            String size = (String) selectedObjects[0];
            setPageSize(size);
        }
    }

    public int getOrientation() {
        if (((String) orientation.getSelectedObjects()[0]).equals("Portrait")) {
            return PageAttributes.PORTRAIT;
        } else if (((String) orientation.getSelectedObjects()[0]).equals("Landscape")) {
            return PageAttributes.LANDSCAPE;
        }
        return PageAttributes.PORTRAIT;
    }

    public void setOrientation(String orientationString) {
        if (orientationString.equals("Portrait")) {
            orientation.setSelectedItem(orientationString);
        } else if (orientationString.equals("Landscape")) {
            orientation.setSelectedItem(orientationString);
        }
    }

    public void setReportName(String name) {
        formName.setText(name);
    }

    public String getReportName() {
        return formName.getText();
    }

    public void setPageWidth(int width) {
        setPageWidth(new Double(width));
    }

    public void setPageWidth(Double width) {
        pageWidth.setValue(width);
    }

    public Double getPageWidth() {
        return (Double) pageWidth.getValue();
    }

    public int getPixelPageWidth() {
        measurement.setFrom(currentUnit);
        measurement.setTo(Measurement.PIXELS);
        return (int) Math.round(measurement.convert(getPageWidth()));
    }

    public void setPageHeight(int height) {
        setPageHeight(new Double(height));
    }

    public void setPageHeight(Double height) {
        pageHeight.setValue(height);
    }

    public Double getPageHeight() {
        return (Double) pageHeight.getValue();
    }

    public int getPixelPageHeight() {
        measurement.setFrom(currentUnit);
        measurement.setTo(Measurement.PIXELS);
        return (int) Math.round(measurement.convert(getPageHeight()));
    }

    public void setLeftMargin(int width) {
        setLeftMargin(new Double(width));
    }

    public void setLeftMargin(Double width) {
        leftMargin.setValue(width);
    }

    public Double getLeftMargin() {
        return (Double) leftMargin.getValue();
    }

    public int getPixelLeftMargin() {
        measurement.setFrom(currentUnit);
        measurement.setTo(Measurement.PIXELS);
        return (int) Math.round(measurement.convert(getLeftMargin()));
    }

    public void setRightMargin(int width) {
        setRightMargin(new Double(width));
    }

    public void setRightMargin(Double width) {
        rightMargin.setValue(width);
    }

    public Double getRightMargin() {
        return (Double) rightMargin.getValue();
    }

    public int getPixelRightMargin() {
        measurement.setFrom(currentUnit);
        measurement.setTo(Measurement.PIXELS);
        return (int) Math.round(measurement.convert(getRightMargin()));
    }

    public void setTopMargin(int height) {
        setTopMargin(new Double(height));
    }

    public void setTopMargin(Double height) {
        topMargin.setValue(height);
    }

    public Double getTopMargin() {
        return (Double) topMargin.getValue();
    }

    public int getPixelTopMargin() {
        measurement.setFrom(currentUnit);
        measurement.setTo(Measurement.PIXELS);
        return (int) Math.round(measurement.convert(getTopMargin()));
    }

    public void setBottomMargin(int height) {
        setBottomMargin(new Double(height));
    }

    public void setBottomMargin(Double height) {
        bottomMargin.setValue(height);
    }

    public Double getBottomMargin() {
        return (Double) bottomMargin.getValue();
    }

    public int getPixelBottomMargin() {
        measurement.setFrom(currentUnit);
        measurement.setTo(Measurement.PIXELS);
        return (int) Math.round(measurement.convert(getBottomMargin()));
    }

    private void orientationItemStateChanged(ItemEvent e) {
        measurementComboBox.setSelectedIndex(0);
        String size = (String) pageSize.getSelectedObjects()[0];
        setPageSize(size);

        if (size.equals("Portrait")) {
            pageAttributes.setOrientation(PageAttributes.PORTRAIT);
        } else if (size.equals("Landscape")) {
            pageAttributes.setOrientation(PageAttributes.LANDSCAPE);
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel panel3;
    private JLabel nameLabel;
    private JTextField formName;
    private JPanel panel2;
    private JLabel measurementLabel;
    private JComboBox measurementComboBox;
    private JPanel panel1;
    private JLabel orientationLabel;
    private JComboBox orientation;
    private JLabel leftMarginLabel;
    private JSpinner leftMargin;
    private JLabel presetSizeLabel;
    private JComboBox pageSize;
    private JLabel rightMarginLabel;
    private JSpinner rightMargin;
    private JLabel widthLabel;
    private JSpinner pageWidth;
    private JLabel topMarginLabel;
    private JSpinner topMargin;
    private JLabel heightLabel;
    private JSpinner pageHeight;
    private JLabel bottomMarginLabel;
    private JSpinner bottomMargin;
    private JPanel buttonBar;
    private JHelpLabel helpLabel;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration //GEN-END:variables

}
