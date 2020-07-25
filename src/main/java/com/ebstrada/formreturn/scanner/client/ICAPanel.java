package com.ebstrada.formreturn.scanner.client;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSizeName;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.jpa.IncomingImage;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.image.ImageUtil;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ScannerPreferences;
import com.google.gson.internal.LinkedTreeMap;

import org.jdesktop.swingx.*;

public class ICAPanel extends JPanel implements ICAListener, ScannerPanel {

    private static final long serialVersionUID = 1L;

    private ICAScanner scanner;

    public static int transferCount = 0;

    public static int pageCount = 0;

    private int colorType = ICAScanner.COLOR_BW;

    private BufferedImage previewImage;

    private ScannerClientDialog scannerClientDialog;

    private String[] pageSizes;

    private ScannerPreferences scannerPreferences;

    private LinkedTreeMap documentTypeMap;

    public ICAPanel() {
        initComponents();
        localize();
    }

    private void localize() {

        // tabs
        tabbedPane.setTitleAt(0, Localizer.localize("UI", "ScannerActionsTabTitle"));
        tabbedPane.setTitleAt(1, Localizer.localize("UI", "AdvancedSettingsTabTitle"));

        // advanced controls
        DefaultComboBoxModel colorModel = new DefaultComboBoxModel();
        colorModel.addElement(Localizer.localize("UI", "Color1"));
        colorModel.addElement(Localizer.localize("UI", "Color2"));
        colorComboBox.setModel(colorModel);

        DefaultComboBoxModel duplexModel = new DefaultComboBoxModel();
        duplexModel.addElement(Localizer.localize("UI", "Duplex1"));
        duplexModel.addElement(Localizer.localize("UI", "Duplex2"));
        duplexComboBox.setModel(duplexModel);

    }

    public void initScanner() throws Exception {

        try {
            scanner = ICAScanner.getDevice();
            if (scanner.isBusy()) {
                throw new Exception(Localizer.localize("UI", "ScannerIsBusyMessage"));
            }
            scanner.addListener(this);
        } catch (Exception e) {
            Misc.showExceptionMsg(this, e);
            Misc.printStackTrace(e);
            throw e;
        }

        String[] deviceNames = scanner.getDeviceNames();
        if (deviceNames.length <= 0) {
            throw new Exception(Localizer.localize("UI", "NoScannerFoundMessage"));
        }
        for (String deviceName : deviceNames) {
            deviceComboBox.addItem(deviceName);
        }

        this.scannerPreferences = PreferencesManager.getScannerPreferences();

        String selectedScanner = scanner.getSelectedScanner();
        HashMap<String, LinkedTreeMap> scannerSettings = scanner.getScannerSettings();
        LinkedTreeMap settings = scannerSettings.get(selectedScanner);

        // read-only-settings -> supported-document-types
        LinkedTreeMap readOnlySettings = (LinkedTreeMap) settings.get("read-only-settings");
        LinkedTreeMap supportedDocumentTypes =
            (LinkedTreeMap) readOnlySettings.get("supported-document-types");
        Collection documentTypes = supportedDocumentTypes.values();

        this.pageSizes = (String[]) documentTypes.toArray(new String[documentTypes.size()]);
        this.documentTypeMap = supportedDocumentTypes;

        for (String size : pageSizes) {
            pageSizeComboBox.addItem(size);
        }

        LinkedTreeMap resolutions = (LinkedTreeMap) readOnlySettings.get("preferred-resolutions");
        Set resolutionList = resolutions.keySet();
        String[] resArray = (String[]) resolutionList.toArray(new String[resolutionList.size()]);

        for (String res : resArray) {
            resolutionComboBox.addItem(res);
        }

        if (scannerPreferences == null || scannerPreferences.getDocumentType() == null) {
            restoreDefaults();
            saveSettings(false);
        }

        resolutionComboBox.setSelectedItem(scannerPreferences.getResolution() + "");

        colorComboBox.setSelectedIndex(scannerPreferences.getColorMode());

        String supportsDuplexScanning = (String) readOnlySettings.get("supports-duplex-scanning");
        if (supportsDuplexScanning.equalsIgnoreCase("true")) {
            duplexComboBox.setEnabled(true);
            duplexComboBox.setSelectedIndex(scannerPreferences.getScanSides());
        } else {
            duplexComboBox.setEnabled(false);
        }

        String canUseBlackWhiteThreshold =
            (String) readOnlySettings.get("can-use-black-white-threshold");
        if (canUseBlackWhiteThreshold.equalsIgnoreCase("true")) {
            blackThresholdSpinner.setEnabled(true);
            useDefaultBlackThreshold.setEnabled(true);
            if (scannerPreferences.getBlackThreshold() > 0) {
                blackThresholdSpinner.setValue(scannerPreferences.getBlackThreshold());
            } else {
                String defaultBlackAndWhiteThreshold =
                    (String) readOnlySettings.get("default-black-and-white-threshold");
                blackThresholdSpinner.setValue(Integer.parseInt(defaultBlackAndWhiteThreshold));
            }
            useDefaultBlackThreshold.setSelected(scannerPreferences.isUseDefaultBlackThreshold());
        } else {
            blackThresholdSpinner.setEnabled(false);
            useDefaultBlackThreshold.setEnabled(false);
        }

        if (scannerPreferences.getDocumentType() != null) {
            pageSizeComboBox.setSelectedItem(scannerPreferences.getDocumentType());
        }

    }

    public boolean isA4Paper() {

        String timezone = System.getProperty("user.timezone");
        if (timezone != null && timezone.length() > 0) {
            if (System.getProperty("user.timezone").startsWith("America")) {
                return false;
            } else {
                return true;
            }
        }

        try {
            PrintService pservice = PrintServiceLookup.lookupDefaultPrintService();
            Object obj = pservice.getDefaultAttributeValue(Media.class);
            if (obj instanceof MediaSizeName) {
                MediaSizeName mediaSizeName = (MediaSizeName) obj;
                if (mediaSizeName.equals(MediaSizeName.ISO_A4)) {
                    return true;
                } else {
                    return false;
                }
            }

        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        }

        if (Locale.getDefault().getCountry() == "US" || Locale.getDefault().getCountry() == "CA") {
            return false;
        }

        // default to true
        return true;
    }

    protected void acquireImage() {
        try {
            scanner.select((String) deviceComboBox.getSelectedItem());
            scanner.acquire();
        } catch (Exception e) {
            Misc.showExceptionMsg(this, e);
            Misc.printStackTrace(e);
        }
    }

    private void scanButtonActionPerformed(ActionEvent e) {
        acquireImage();
    }

    public boolean close() {
        if (cancel() != true) {
            return false;
        } else {
            return true;
        }
    }

    private boolean cancel() {
        if (scanner.isBusy()) {
            String title = Localizer.localize("UI", "ScannerBusyCancelTitle");
            String message = Localizer.localize("UI", "ScannerBusyCancelMessage");
            boolean confirm = Misc.showConfirmDialog(this, title, message,
                Localizer.localize("UI", "ScannerBusyCancelCancelButtonText"),
                Localizer.localize("UI", "ScannerBusyCancelContinueButtonText"));
            if (confirm == false) {
                return false;
            }
            try {
                scanner.setCancel(true);
            } catch (Exception ex) {
                Misc.showExceptionMsg(this, ex);
                Misc.printStackTrace(ex);
            }
        }
        return true;
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        cancel();
    }

    private void saveSettingsButtonActionPerformed(ActionEvent e) {
        saveSettings(true);
    }

    private void saveSettings(boolean showSaveNotification) {
        ScannerPreferences scannerPreferences = PreferencesManager.getScannerPreferences();

        scannerPreferences.setDocumentType((String) pageSizeComboBox.getSelectedItem());

        scannerPreferences
            .setResolution(Integer.parseInt((String) resolutionComboBox.getSelectedItem()));

        scannerPreferences.setColorMode(colorComboBox.getSelectedIndex());

        scannerPreferences.setPageSize(pageSizeComboBox.getSelectedIndex());

        scannerPreferences.setScanSides(duplexComboBox.getSelectedIndex());

        scannerPreferences.setBlackThreshold((Integer) blackThresholdSpinner.getValue());

        scannerPreferences.setUseDefaultBlackThreshold(useDefaultBlackThreshold.isSelected());

        try {
            PreferencesManager.savePreferences(Main.getXstream());
            if (showSaveNotification) {
                Misc.showSuccessMsg(getRootPane(),
                    Localizer.localize("UI", "ScannerSettingsSavedSuccessfullyMessage"));
            }
        } catch (IOException ex) {
            Misc.printStackTrace(ex);
            Misc.showExceptionMsg(getRootPane(), ex);
        }
    }

    private void restoreDefaults() {
        if (isA4Paper()) {
            pageSizeComboBox.setSelectedIndex(1);
        } else {
            pageSizeComboBox.setSelectedIndex(3);
        }

        resolutionComboBox.setSelectedItem("300");
        colorComboBox.setSelectedIndex(ScannerPreferences.BLACK_AND_WHITE);
        duplexComboBox.setSelectedIndex(ScannerPreferences.SINGLE_PAGE);
        blackThresholdSpinner.setValue(128);
        useDefaultBlackThreshold.setSelected(true);
    }

    private void restoreDefaultsButtonActionPerformed(ActionEvent e) {

        String title = Localizer.localize("UI", "RestoreDefaultScannerSettingsTitle");
        String message = Localizer.localize("UI", "RestoreDefaultScannerSettingsMessage");
        String confirmText = Localizer.localize("UI", "Yes");
        String cancelText = Localizer.localize("UI", "No");

        if (Misc.showConfirmDialog(getRootPane(), title, message, confirmText, cancelText)) {

            restoreDefaults();

            saveSettings(false);

        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        formReturnScannerIconLabel = new JLabel();
        tabbedPane = new JTabbedPane();
        scannerActionsPanel = new JPanel();
        deviceComboBox = new JComboBox();
        scanButton = new JButton();
        cancelButton = new JButton();
        scannerBusyLabel = new JXBusyLabel();
        advancedSettingsPanel = new JPanel();
        resolutionLabel = new JLabel();
        resolutionComboBox = new JComboBox();
        colorLabel = new JLabel();
        colorComboBox = new JComboBox();
        pageSizeLabel = new JLabel();
        pageSizeComboBox = new JComboBox();
        duplexLabel = new JLabel();
        duplexComboBox = new JComboBox();
        blackThresholdLabel = new JLabel();
        panel3 = new JPanel();
        blackThresholdSpinner = new JSpinner();
        useDefaultBlackThreshold = new JCheckBox();
        panel4 = new JPanel();
        saveSettingsButton = new JButton();
        restoreDefaultsButton = new JButton();
        statusBar = new JPanel();
        statusLabel = new JLabel();

        // ======== this ========
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

        // ======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            // ======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths =
                    new int[] {10, 0, 10, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {0.0, 0.0, 0.0, 1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                // ---- formReturnScannerIconLabel ----
                formReturnScannerIconLabel.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/frscanner_64x64.png")));
                contentPanel.add(formReturnScannerIconLabel,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

                // ======== tabbedPane ========
                {

                    // ======== scannerActionsPanel ========
                    {
                        scannerActionsPanel.setOpaque(false);
                        scannerActionsPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout) scannerActionsPanel.getLayout()).columnWidths =
                            new int[] {15, 0, 0, 0, 0, 10, 0};
                        ((GridBagLayout) scannerActionsPanel.getLayout()).rowHeights =
                            new int[] {0, 0};
                        ((GridBagLayout) scannerActionsPanel.getLayout()).columnWeights =
                            new double[] {0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout) scannerActionsPanel.getLayout()).rowWeights =
                            new double[] {1.0, 1.0E-4};

                        // ---- deviceComboBox ----
                        deviceComboBox.setFont(UIManager.getFont("ComboBox.font"));
                        scannerActionsPanel.add(deviceComboBox,
                            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                        // ---- scanButton ----
                        scanButton.setFont(UIManager.getFont("Button.font"));
                        scanButton.addActionListener(new ActionListener() {
                            @Override public void actionPerformed(ActionEvent e) {
                                scanButtonActionPerformed(e);
                            }
                        });
                        scanButton.setText(Localizer.localize("UI", "ScanButtonText"));
                        scannerActionsPanel.add(scanButton,
                            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                        // ---- cancelButton ----
                        cancelButton.setEnabled(false);
                        cancelButton.setFont(UIManager.getFont("Button.font"));
                        cancelButton.addActionListener(new ActionListener() {
                            @Override public void actionPerformed(ActionEvent e) {
                                cancelButtonActionPerformed(e);
                            }
                        });
                        cancelButton.setText(Localizer.localize("UI", "CancelButtonText"));
                        scannerActionsPanel.add(cancelButton,
                            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
                        scannerActionsPanel.add(scannerBusyLabel,
                            new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
                    }
                    tabbedPane.addTab("Scanner Actions", scannerActionsPanel);

                    // ======== advancedSettingsPanel ========
                    {
                        advancedSettingsPanel.setOpaque(false);
                        advancedSettingsPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout) advancedSettingsPanel.getLayout()).columnWidths =
                            new int[] {15, 0, 0, 10, 0};
                        ((GridBagLayout) advancedSettingsPanel.getLayout()).rowHeights =
                            new int[] {10, 0, 0, 0, 0, 0, 0, 5, 0};
                        ((GridBagLayout) advancedSettingsPanel.getLayout()).columnWeights =
                            new double[] {0.0, 0.0, 1.0, 0.0, 1.0E-4};
                        ((GridBagLayout) advancedSettingsPanel.getLayout()).rowWeights =
                            new double[] {0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 1.0E-4};

                        // ---- resolutionLabel ----
                        resolutionLabel.setText(Localizer.localize("UI", "ResolutionLabelText"));
                        advancedSettingsPanel.add(resolutionLabel,
                            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        // ---- resolutionComboBox ----
                        resolutionComboBox.setFont(UIManager.getFont("ComboBox.font"));
                        advancedSettingsPanel.add(resolutionComboBox,
                            new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        // ---- colorLabel ----
                        colorLabel.setText(Localizer.localize("UI", "ColorLabelText"));
                        advancedSettingsPanel.add(colorLabel,
                            new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        // ---- colorComboBox ----
                        colorComboBox.setModel(new DefaultComboBoxModel(
                            new String[] {"Black & White", "Grayscale", "Color"}));
                        colorComboBox.setFont(UIManager.getFont("ComboBox.font"));
                        advancedSettingsPanel.add(colorComboBox,
                            new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        // ---- pageSizeLabel ----
                        pageSizeLabel.setText(Localizer.localize("UI", "PageSizeLabelText"));
                        advancedSettingsPanel.add(pageSizeLabel,
                            new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        // ---- pageSizeComboBox ----
                        pageSizeComboBox.setFont(UIManager.getFont("ComboBox.font"));
                        advancedSettingsPanel.add(pageSizeComboBox,
                            new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        // ---- duplexLabel ----
                        duplexLabel.setText(Localizer.localize("UI", "DuplexLabelText"));
                        advancedSettingsPanel.add(duplexLabel,
                            new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        // ---- duplexComboBox ----
                        duplexComboBox.setModel(new DefaultComboBoxModel(
                            new String[] {"Scan Single Side", "Scan Both Sides"}));
                        duplexComboBox.setFont(UIManager.getFont("ComboBox.font"));
                        advancedSettingsPanel.add(duplexComboBox,
                            new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        // ---- blackThresholdLabel ----
                        blackThresholdLabel.setText(Localizer.localize("UI", "BlackThresholdText"));
                        advancedSettingsPanel.add(blackThresholdLabel,
                            new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        // ======== panel3 ========
                        {
                            panel3.setOpaque(false);
                            panel3.setLayout(new GridBagLayout());
                            ((GridBagLayout) panel3.getLayout()).columnWidths =
                                new int[] {0, 15, 0, 0};
                            ((GridBagLayout) panel3.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout) panel3.getLayout()).columnWeights =
                                new double[] {0.0, 0.0, 0.0, 1.0E-4};
                            ((GridBagLayout) panel3.getLayout()).rowWeights =
                                new double[] {0.0, 1.0E-4};

                            // ---- blackThresholdSpinner ----
                            blackThresholdSpinner.setModel(new SpinnerNumberModel(128, 0, 255, 1));
                            blackThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
                            panel3.add(blackThresholdSpinner,
                                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            // ---- useDefaultBlackThreshold ----
                            useDefaultBlackThreshold.setOpaque(false);
                            useDefaultBlackThreshold.setSelected(true);
                            useDefaultBlackThreshold.setFont(UIManager.getFont("CheckBox.font"));
                            useDefaultBlackThreshold.setText(
                                Localizer.localize("UI", "UseDefaultBlackThresholdCheckBoxText"));
                            panel3.add(useDefaultBlackThreshold,
                                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
                        }
                        advancedSettingsPanel.add(panel3,
                            new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        // ======== panel4 ========
                        {
                            panel4.setOpaque(false);
                            panel4.setLayout(new GridBagLayout());
                            ((GridBagLayout) panel4.getLayout()).columnWidths = new int[] {0, 0, 0};
                            ((GridBagLayout) panel4.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout) panel4.getLayout()).columnWeights =
                                new double[] {0.0, 0.0, 1.0E-4};
                            ((GridBagLayout) panel4.getLayout()).rowWeights =
                                new double[] {0.0, 1.0E-4};

                            // ---- saveSettingsButton ----
                            saveSettingsButton.setFont(UIManager.getFont("Button.font"));
                            saveSettingsButton.addActionListener(new ActionListener() {
                                @Override public void actionPerformed(ActionEvent e) {
                                    saveSettingsButtonActionPerformed(e);
                                }
                            });
                            saveSettingsButton
                                .setText(Localizer.localize("UI", "SaveSettingsButtonText"));
                            panel4.add(saveSettingsButton,
                                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            // ---- restoreDefaultsButton ----
                            restoreDefaultsButton.setFont(UIManager.getFont("Button.font"));
                            restoreDefaultsButton.addActionListener(new ActionListener() {
                                @Override public void actionPerformed(ActionEvent e) {
                                    restoreDefaultsButtonActionPerformed(e);
                                }
                            });
                            restoreDefaultsButton
                                .setText(Localizer.localize("UI", "RestoreDefaultsButtonText"));
                            panel4.add(restoreDefaultsButton,
                                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
                        }
                        advancedSettingsPanel.add(panel4,
                            new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
                    }
                    tabbedPane.addTab("Advanced Settings", advancedSettingsPanel);

                }
                contentPanel.add(tabbedPane,
                    new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            // ======== statusBar ========
            {
                statusBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                statusBar.setLayout(new GridBagLayout());
                ((GridBagLayout) statusBar.getLayout()).columnWeights = new double[] {1.0};
                statusBar.add(statusLabel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(statusBar, BorderLayout.SOUTH);
        }
        add(dialogPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel formReturnScannerIconLabel;
    private JTabbedPane tabbedPane;
    private JPanel scannerActionsPanel;
    private JComboBox deviceComboBox;
    private JButton scanButton;
    private JButton cancelButton;
    private JXBusyLabel scannerBusyLabel;
    private JPanel advancedSettingsPanel;
    private JLabel resolutionLabel;
    private JComboBox resolutionComboBox;
    private JLabel colorLabel;
    private JComboBox colorComboBox;
    private JLabel pageSizeLabel;
    private JComboBox pageSizeComboBox;
    private JLabel duplexLabel;
    private JComboBox duplexComboBox;
    private JLabel blackThresholdLabel;
    private JPanel panel3;
    private JSpinner blackThresholdSpinner;
    private JCheckBox useDefaultBlackThreshold;
    private JPanel panel4;
    private JButton saveSettingsButton;
    private JButton restoreDefaultsButton;
    private JPanel statusBar;
    private JLabel statusLabel;
    // JFormDesigner - End of variables declaration //GEN-END:variables

    public void uploadImage(BufferedImage bi) {
        EntityManager entityManager = null;
        if (entityManager == null) {
            // TODO: pick the right entity manager... either
            entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
        }

        try {

            entityManager.getTransaction().begin();
            byte[] imageData = ImageUtil.getPNGByteArray(bi);
            if (entityManager != null) {
                IncomingImage incomingImage = new IncomingImage();
                incomingImage.setCaptureTime(new Timestamp(System.currentTimeMillis()));
                incomingImage.setIncomingImageData(imageData);
                incomingImage.setIncomingImageName(System.currentTimeMillis() + ".png");
                incomingImage.setMatchStatus((short) 0);
                incomingImage.setNumberOfPages(1);
                entityManager.persist(incomingImage);
            }
            entityManager.getTransaction().commit();
            entityManager.close();

            entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
            if (entityManager != null) {
                entityManager.getTransaction().begin();
                entityManager.flush();
                entityManager.createNativeQuery("CALL CHECK_INCOMING_IMAGES()").executeUpdate();
                entityManager.getTransaction().commit();
                entityManager.close();
            }
        } catch (Exception ex) {
        } finally {
            if (entityManager != null) {
                if (entityManager.isOpen() && entityManager.getTransaction().isActive()) {
                    try {
                        entityManager.getTransaction().rollback();
                    } catch (Exception ex) {
                    }
                }
                if (entityManager.isOpen()) {
                    entityManager.close();
                }
            }
        }
    }

    public void saveBufferedImage(BufferedImage bi) {
        previewImage = bi;
        uploadImage(bi);
    }

    public void update(ICAScannerMetadata.Type type, ICAScannerMetadata metadata) throws Exception {

        switch (type) {

            case ACQUIRE:

                transferCount = 0;
                pageCount = 0;

                statusLabel.setText(Localizer.localize("UI", "ScanningMessage"));

                break;

            case ACQUIRED:

                String message = String
                    .format(Localizer.localize("UI", "SavingPageMessage"), (pageCount + 1) + "");
                statusLabel.setText(message);
                saveBufferedImage(metadata.getImage());
                pageCount++;

                break;

            case QUERY:

                updateScannerSettings(metadata);
                break;

            case STATECHANGE:

                ICAScanner device = metadata.getDevice();
                device = metadata.getDevice();

                if (device.isBusy()) {
                    scanButton.setEnabled(false);
                    cancelButton.setEnabled(true);
                    statusLabel.setText(Localizer.localize("UI", "ScannerBusyMessage"));
                    scannerBusyLabel.setBusy(true);
                } else {
                    statusLabel.setText(Localizer.localize("UI", "ScannerReadyMessage"));
                    scannerBusyLabel.setBusy(false);
                    cancelButton.setEnabled(false);
                    scanButton.setEnabled(true);
                }

                if (metadata.isFinished()) {
                    device.close();

                    if (getScannerClientDialog() != null) {
                        getScannerClientDialog().dispose();
                    }
                }

                break;

            default:
                break;

        }

    }

    private void updateScannerSettings(ICAScannerMetadata metadata) {

        /*
         * HashMap<String, LinkedTreeMap> scannerSettings =
         * metadata.getDevice().getScannerSettings(); String selectedScanner =
         * metadata.getDevice().getSelectedScanner();
         */

        /*
         * DefaultComboBoxModel<String> deviceComboBoxModel =
         * (DefaultComboBoxModel<String>) this.deviceComboBox.getModel();
         * deviceComboBoxModel.removeAllElements();
         *
         * for (String deviceName: scannerSettings.keySet()) {
         * deviceComboBoxModel.addElement(deviceName); }
         *
         * deviceComboBox.setSelectedItem(selectedScanner);
         */

    }

    public LinkedTreeMap getSettings() {

        LinkedTreeMap settings = new LinkedTreeMap();

        String deviceName = (String) deviceComboBox.getSelectedItem();
        settings.put("device-name", deviceName);
        settings.put("resolution", getDPI() + "");
        settings.put("is-duplex-scanning-enabled", isDuplexScanning() ? "true" : "false");
        settings.put("use-back-white-threshold",
            useDefaultBlackThreshold.isSelected() ? "false" : "true");
        settings.put("threshold-for-black-and-white-scanning", getBlackThreshold() + "");
        settings.put("document-type", (String) pageSizeComboBox.getSelectedItem());
        settings.put("pixel-data-type", getColorType() + "");
        if (getColorType() == 0) {
            settings.put("bit-depth", "1");
        } else {
            settings.put("bit-depth", "8");
        }

        return settings;

    }

    private String getBlackThreshold() {
        return this.blackThresholdSpinner.getValue() + "";
    }

    public int getDPI() {
        return Integer.parseInt((String) resolutionComboBox.getSelectedItem());
    }

    public boolean isDuplexScanning() {
        if (duplexComboBox.getSelectedIndex() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public int getColorType() {

        if (colorComboBox.getSelectedIndex() == 0) {
            return ICAScanner.COLOR_BW;
        } else if (colorComboBox.getSelectedIndex() == 1) {
            return ICAScanner.COLOR_GRAY;
        } else if (colorComboBox.getSelectedIndex() == 2) {
            return ICAScanner.COLOR_RGB;
        } else {
            return colorType;
        }

    }

    public void setButtonFocus() {
        getRootPane().setDefaultButton(scanButton);
    }

    public BufferedImage getPreviewImage() {
        return previewImage;
    }

    public void setPreviewImage(BufferedImage previewImage) {
        this.previewImage = previewImage;
    }

    public ScannerClientDialog getScannerClientDialog() {
        return scannerClientDialog;
    }

    public String[] getPageSizes() {
        return pageSizes;
    }

    @Override public void init() throws Exception {
        initScanner();
    }

    @Override public void focusGained() {
        // TODO Auto-generated method stub

        // TODO: focus on the scan button!!!!

    }

    @Override public void setScannerClientDialog(ScannerClientDialog scannerClientDialog) {
        this.scannerClientDialog = scannerClientDialog;
    }

}
