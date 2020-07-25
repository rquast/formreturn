package com.ebstrada.formreturn.scanner.client;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Locale;
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

import org.jdesktop.swingx.*;

import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerDevice;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerListener;
import uk.co.mmscomputing.device.twain.TwainCapability;
import uk.co.mmscomputing.device.twain.TwainConstants;
import uk.co.mmscomputing.device.twain.TwainIOException;
import uk.co.mmscomputing.device.twain.TwainIOMetadata;
import uk.co.mmscomputing.device.twain.TwainSource;
import uk.co.mmscomputing.device.twain.TwainTransfer;
import uk.co.mmscomputing.device.twain.TwainTransfer.MemoryTransfer.Info;

public class TwainPanel extends JPanel implements ScannerListener, ScannerPanel {

    private static final long serialVersionUID = 1L;

    private Scanner scanner;

    public static int transferCount = 0;

    public static int pageCount = 0;

    private int colorType = TwainConstants.TWPT_BW;

    private Vector<BufferedImage> memoryTitles = new Vector<BufferedImage>();
    private Vector<Rectangle2D> memoryImageTileLocations = new Vector<Rectangle2D>();
    private int maxTileWidth = 0;
    private int maxTileHeight = 0;

    private BufferedImage previewImage;

    private ScannerClientDialog scannerClientDialog;

    private static final String[] pageSizes = {"Maximum", // max
        "A4", // 210 x 297
        "B5", // 176 x 250
        "US Letter", // 216 x 280
        "US Legal", // 216 x 356
        "A5", // 148 x 210
        "B4", // 250 x 353
        "B6", // 125 x 176
        "B", // 1000 x 1414
        "US Ledger", // 280 x 432
        "US Executive", // 184 x 267
        "A3", // 297 x 420
        "B3", // 353 x 500
        "A6", // 105 x 148
        "C4", // 229 x 324
        "C5", // 162 x 229
        "C6", // 114 x 162
        "4A0", // 1682 x 2378
        "2A0", // 1189 x 1682
        "A0", // 841 x 1189
        "A1", // 594 x 841
        "A2", // 420 x 594
        "A7", // 74 x 105
        "A8", // 52 x 74
        "A9", // 37 x 52
        "A10", // 26 x 37
        "ISO B0", // 1000 x 1414
        "ISO B1", // 707 x 1000
        "ISO B2", // 500 x 707
        "ISO B5", // 176 x 250
        "ISO B7", // 88 x 125
        "ISO B8", // 62 x 88
        "ISO B9", // 44 x 62
        "ISO B10", // 31 x 44
        "JIS B0", // 1030 x 1456
        "JIS B1", // 728 x 1030
        "JIS B2", // 515 x 728
        "JIS B3", // 364 x 515
        "JIS B4", // 257 x 364
        "JIS B6", // 128 x 182
        "JIS B7", // 91 x 128
        "JIS B8", // 64 x 91
        "JIS B9", // 45 x 64
        "JIS B10", // 32 x 45
        "C0", // 917 x 1297
        "C1", // 648 x 917
        "C2", // 458 x 648
        "C3", // 324 x 458
        "C7", // 81 x 114
        "C8", // 57 x 81
        "C9", // 40 x 57
        "C10", // 28 x 40
        "US Statement", // 140 x 216
        "Business Card", // 90 x 55
    };

    public TwainPanel() {
        initComponents();
        localize();
    }

    private void localize() {

        // tabs
        tabbedPane.setTitleAt(0, Localizer.localize("UI", "ScannerActionsTabTitle"));
        tabbedPane.setTitleAt(1, Localizer.localize("UI", "AdvancedSettingsTabTitle"));

        // advanced controls
        DefaultComboBoxModel scanningClientModel = new DefaultComboBoxModel();
        scanningClientModel.addElement(Localizer.localize("UI", "ScanningClient1"));
        scanningClientModel.addElement(Localizer.localize("UI", "ScanningClient2"));
        uiComboBox.setModel(scanningClientModel);

        DefaultComboBoxModel transferModeModel = new DefaultComboBoxModel();
        transferModeModel.addElement(Localizer.localize("UI", "TransferMode1"));
        transferModeModel.addElement(Localizer.localize("UI", "TransferMode2"));
        transferModeComboBox.setModel(transferModeModel);

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
            scanner = Scanner.getDevice();
            if (!(scanner.isAPIInstalled())) {
                throw new Exception(Localizer.localize("UI", "CannotLoadTwainAPIMessage"));
            }
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

        ScannerPreferences scannerPreferences = PreferencesManager.getScannerPreferences();


        for (String size : pageSizes) {
            pageSizeComboBox.addItem(size);
        }

        if (scannerPreferences.getPageSize() < 0) {

            if (isA4Paper()) {
                pageSizeComboBox.setSelectedIndex(1);
                Misc.showSuccessMsg(getRootPane(),
                    Localizer.localize("UI", "A4DefaultPaperSizeSet"));
            } else {
                pageSizeComboBox.setSelectedIndex(3);
                Misc.showSuccessMsg(getRootPane(),
                    Localizer.localize("UI", "USLetterDefaultPaperSizeSet"));
            }

            saveSettings(false);

        } else {
            pageSizeComboBox.setSelectedIndex(scannerPreferences.getPageSize());
        }

        uiComboBox.setSelectedIndex(scannerPreferences.getScanningSoftware());

        transferModeComboBox.setSelectedIndex(scannerPreferences.getTransferMode());

        resolutionSpinner.setValue(scannerPreferences.getResolution());

        colorComboBox.setSelectedIndex(scannerPreferences.getColorMode());

        pageSizeComboBox.setSelectedIndex(scannerPreferences.getPageSize());

        duplexComboBox.setSelectedIndex(scannerPreferences.getScanSides());

        blackThresholdSpinner.setValue(scannerPreferences.getBlackThreshold());

        useDefaultBlackThreshold.setSelected(scannerPreferences.isUseDefaultBlackThreshold());

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
        } catch (ScannerIOException e) {
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
            } catch (ScannerIOException ex) {
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

        scannerPreferences.setPageSize(pageSizeComboBox.getSelectedIndex());

        scannerPreferences.setScanningSoftware(uiComboBox.getSelectedIndex());

        scannerPreferences.setTransferMode(transferModeComboBox.getSelectedIndex());

        scannerPreferences.setResolution((Integer) resolutionSpinner.getValue());

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

    private void restoreDefaultsButtonActionPerformed(ActionEvent e) {

        String title = Localizer.localize("UI", "RestoreDefaultScannerSettingsTitle");
        String message = Localizer.localize("UI", "RestoreDefaultScannerSettingsMessage");
        String confirmText = Localizer.localize("UI", "Yes");
        String cancelText = Localizer.localize("UI", "No");

        if (Misc.showConfirmDialog(getRootPane(), title, message, confirmText, cancelText)) {

            if (isA4Paper()) {
                pageSizeComboBox.setSelectedIndex(1);
            } else {
                pageSizeComboBox.setSelectedIndex(3);
            }

            uiComboBox.setSelectedIndex(ScannerPreferences.USE_FORMRETURN_SCANNING_SOFTWARE);
            transferModeComboBox.setSelectedIndex(ScannerPreferences.NATIVE_TRANSFER_MODE);
            resolutionSpinner.setValue(300);
            colorComboBox.setSelectedIndex(ScannerPreferences.BLACK_AND_WHITE);
            duplexComboBox.setSelectedIndex(ScannerPreferences.SINGLE_PAGE);
            blackThresholdSpinner.setValue(128);
            useDefaultBlackThreshold.setSelected(true);

            saveSettings(false);

        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
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
        scanningClientLabel = new JLabel();
        uiComboBox = new JComboBox();
        transferModeLabel = new JLabel();
        transferModeComboBox = new JComboBox();
        resolutionLabel = new JLabel();
        resolutionSpinner = new JSpinner();
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

        //======== this ========
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths =
                    new int[] {10, 0, 10, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {0.0, 0.0, 0.0, 1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //---- formReturnScannerIconLabel ----
                formReturnScannerIconLabel.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/frscanner_64x64.png")));
                contentPanel.add(formReturnScannerIconLabel,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

                //======== tabbedPane ========
                {

                    //======== scannerActionsPanel ========
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
                        scannerActionsPanel.add(deviceComboBox,
                            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                        //---- scanButton ----
                        scanButton.addActionListener(new ActionListener() {
                            @Override public void actionPerformed(ActionEvent e) {
                                scanButtonActionPerformed(e);
                            }
                        });
                        scanButton.setText(Localizer.localize("UI", "ScanButtonText"));
                        scannerActionsPanel.add(scanButton,
                            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                        //---- cancelButton ----
                        cancelButton.setEnabled(false);
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

                    //======== advancedSettingsPanel ========
                    {
                        advancedSettingsPanel.setOpaque(false);
                        advancedSettingsPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout) advancedSettingsPanel.getLayout()).columnWidths =
                            new int[] {15, 0, 0, 10, 0};
                        ((GridBagLayout) advancedSettingsPanel.getLayout()).rowHeights =
                            new int[] {10, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0};
                        ((GridBagLayout) advancedSettingsPanel.getLayout()).columnWeights =
                            new double[] {0.0, 0.0, 1.0, 0.0, 1.0E-4};
                        ((GridBagLayout) advancedSettingsPanel.getLayout()).rowWeights =
                            new double[] {0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 1.0E-4};

                        //---- scanningClientLabel ----
                        scanningClientLabel
                            .setText(Localizer.localize("UI", "ScanningClientLabelText"));
                        advancedSettingsPanel.add(scanningClientLabel,
                            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //---- uiComboBox ----
                        uiComboBox.setModel(new DefaultComboBoxModel(
                            new String[] {"FormReturn Scanner",
                                "Scanning Vendor's User Interface"}));
                        advancedSettingsPanel.add(uiComboBox,
                            new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //---- transferModeLabel ----
                        transferModeLabel
                            .setText(Localizer.localize("UI", "TransferModeLabelText"));
                        advancedSettingsPanel.add(transferModeLabel,
                            new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //---- transferModeComboBox ----
                        transferModeComboBox
                            .setModel(new DefaultComboBoxModel(new String[] {"Memory", "Native"}));
                        transferModeComboBox.setSelectedIndex(1);
                        advancedSettingsPanel.add(transferModeComboBox,
                            new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //---- resolutionLabel ----
                        resolutionLabel.setText(Localizer.localize("UI", "ResolutionLabelText"));
                        advancedSettingsPanel.add(resolutionLabel,
                            new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //---- resolutionSpinner ----
                        resolutionSpinner.setModel(new SpinnerNumberModel(300, 200, 400, 20));
                        advancedSettingsPanel.add(resolutionSpinner,
                            new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //---- colorLabel ----
                        colorLabel.setText(Localizer.localize("UI", "ColorLabelText"));
                        advancedSettingsPanel.add(colorLabel,
                            new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //---- colorComboBox ----
                        colorComboBox.setModel(new DefaultComboBoxModel(
                            new String[] {"Black & White", "Grayscale", "Color"}));
                        advancedSettingsPanel.add(colorComboBox,
                            new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //---- pageSizeLabel ----
                        pageSizeLabel.setText(Localizer.localize("UI", "PageSizeLabelText"));
                        advancedSettingsPanel.add(pageSizeLabel,
                            new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
                        advancedSettingsPanel.add(pageSizeComboBox,
                            new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //---- duplexLabel ----
                        duplexLabel.setText(Localizer.localize("UI", "DuplexLabelText"));
                        advancedSettingsPanel.add(duplexLabel,
                            new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //---- duplexComboBox ----
                        duplexComboBox.setModel(new DefaultComboBoxModel(
                            new String[] {"Scan Single Side", "Scan Both Sides"}));
                        advancedSettingsPanel.add(duplexComboBox,
                            new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //---- blackThresholdLabel ----
                        blackThresholdLabel.setText(Localizer.localize("UI", "BlackThresholdText"));
                        advancedSettingsPanel.add(blackThresholdLabel,
                            new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //======== panel3 ========
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

                            //---- blackThresholdSpinner ----
                            blackThresholdSpinner.setModel(new SpinnerNumberModel(128, 0, 255, 1));
                            panel3.add(blackThresholdSpinner,
                                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- useDefaultBlackThreshold ----
                            useDefaultBlackThreshold.setOpaque(false);
                            useDefaultBlackThreshold.setSelected(true);
                            useDefaultBlackThreshold.setText(
                                Localizer.localize("UI", "UseDefaultBlackThresholdCheckBoxText"));
                            panel3.add(useDefaultBlackThreshold,
                                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
                        }
                        advancedSettingsPanel.add(panel3,
                            new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //======== panel4 ========
                        {
                            panel4.setOpaque(false);
                            panel4.setLayout(new GridBagLayout());
                            ((GridBagLayout) panel4.getLayout()).columnWidths = new int[] {0, 0, 0};
                            ((GridBagLayout) panel4.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout) panel4.getLayout()).columnWeights =
                                new double[] {0.0, 0.0, 1.0E-4};
                            ((GridBagLayout) panel4.getLayout()).rowWeights =
                                new double[] {0.0, 1.0E-4};

                            //---- saveSettingsButton ----
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

                            //---- restoreDefaultsButton ----
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
                            new GridBagConstraints(2, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
                    }
                    tabbedPane.addTab("Advanced Settings", advancedSettingsPanel);
                }
                contentPanel.add(tabbedPane,
                    new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== statusBar ========
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
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
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
    private JLabel scanningClientLabel;
    private JComboBox uiComboBox;
    private JLabel transferModeLabel;
    private JComboBox transferModeComboBox;
    private JLabel resolutionLabel;
    private JSpinner resolutionSpinner;
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

    // JFormDesigner - End of variables declaration  //GEN-END:variables

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

    public void update(ScannerIOMetadata.Type type, ScannerIOMetadata metadata) {

        if (type.equals(ScannerIOMetadata.ACQUIRED)) {
            String message =
                String.format(Localizer.localize("UI", "SavingPageMessage"), (pageCount + 1) + "");
            statusLabel.setText(message);
            saveBufferedImage(metadata.getImage());
            pageCount++;
        }

        if (type.equals(ScannerIOMetadata.MEMORY)) {

            try {

                if (metadata instanceof TwainIOMetadata) {

                    String message = String.format(Localizer.localize("UI", "SavingPageMessage"),
                        (pageCount + 1) + "");

                    statusLabel.setText(message);

                    TwainIOMetadata twaindata = (TwainIOMetadata) metadata;

                    TwainTransfer.MemoryTransfer.Info info = twaindata.getMemory();

                    byte[] twainbuf = info.getBuffer();
                    int width = info.getWidth();
                    int height = info.getHeight();

                    updateMemoryImageSize(info);

                    BufferedImage image = null;

                    if (colorType == TwainConstants.TWPT_BW) {
                        width = info.getBytesPerRow() * 8; // the cheap way out
                        // ;)
                        image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
                        byte[] imgbuf =
                            ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
                        System.arraycopy(twainbuf, 0, imgbuf, 0, imgbuf.length);
                        memoryTitles.add(image);
                    } else if (colorType == TwainConstants.TWPT_GRAY) {
                        width = info.getBytesPerRow(); // the cheap way out ;)
                        image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
                        byte[] imgbuf =
                            ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
                        System.arraycopy(twainbuf, 0, imgbuf, 0, imgbuf.length);
                        memoryTitles.add(image);
                    } else if (colorType == TwainConstants.TWPT_RGB) {
                        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                        /*
                         * Hint: If using TWPT_RGB - BufferedImage.TYPE_INT_RGB
                         * : 3 bytes to 1 int If using TWPT_RGB -
                         * BufferedImage.TYPE_3BYTE_BGR : RGB -> BGR
                         */
                        int bpr = info.getBytesPerRow();

                        int r, g, b, row = 0, pixel = 0;
                        for (int y = 0; y < height; y++) {
                            for (int x = 0; x < width; x++) {
                                r = twainbuf[pixel++] & 0x00FF;
                                g = twainbuf[pixel++] & 0x00FF;
                                b = twainbuf[pixel++] & 0x00FF;
                                image.setRGB(x, y, (r << 16) | (g << 8) | b);
                            }
                            row += bpr;
                            pixel = row;
                        }
                        memoryTitles.add(image);
                    }

                    transferCount++;

                    if (((TwainIOMetadata) metadata).getSource().getState() == 7) {

                        // save image
                        saveBufferedImage(getBufferedImageFromTiles());

                        pageCount++;
                    }

                    // System.out.println("info = "+info.toString()+"\n");

                }
            } catch (Exception e) {
                Misc.printStackTrace(e);
            }

        } else if (type.equals(ScannerIOMetadata.NEGOTIATE)) {

            transferCount = 0;
            pageCount = 0;

            statusLabel.setText(Localizer.localize("UI", "NegotiatingWithScannerMessage"));

            ScannerDevice device = metadata.getDevice();
            try {
                device.setShowUserInterface(isUsingVendorUI());
                device.setShowProgressBar(isUsingVendorUI());
                device.setResolution(getDPI());

                if (metadata instanceof TwainIOMetadata) { // TWAIN only!
                    TwainSource source = ((TwainIOMetadata) metadata).getSource();

                    TwainCapability cap;

                    try {
                        cap = source.getCapability(TwainConstants.CAP_FEEDERENABLED);
                        cap.setCurrentValue(true);
                    } catch (Exception ex) {
                        Misc.printStackTrace(ex);
                    }

                    try {
                        cap = source.getCapability(TwainConstants.CAP_AUTOSCAN);
                        cap.setCurrentValue(true);
                    } catch (Exception ex) {
                        Misc.printStackTrace(ex);
                    }

                    try {
                        if (isDuplexScanning()) {
                            cap = source.getCapability(TwainConstants.CAP_DUPLEXENABLED);
                            cap.setCurrentValue(true);
                        } else {
                            cap = source.getCapability(TwainConstants.CAP_DUPLEXENABLED);
                            cap.setCurrentValue(false);
                        }
                    } catch (Exception ex) {
                        Misc.printStackTrace(ex);
                    }

                    try {
                        if (!(useDefaultBlackThreshold.isSelected())) {
                            cap = source.getCapability(TwainConstants.ICAP_THRESHOLD);
                            cap.setCurrentValue(getBlackThreshold());
                        }
                    } catch (Exception ex) {
                        Misc.printStackTrace(ex);
                    }

                    try {
                        cap = source.getCapability(TwainConstants.ICAP_SUPPORTEDSIZES);
                        int selectedSize = pageSizeComboBox.getSelectedIndex();
                        Object[] items = cap.getItems();
                        for (Object obj : items) {
                            if (obj instanceof Integer) {
                                if (selectedSize == ((Integer) obj).intValue()) {
                                    cap.setCurrentValue(selectedSize);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        Misc.printStackTrace(ex);
                    }

                    try {
                        source.setCapability(TwainConstants.ICAP_PIXELTYPE, getColorType());
                    } catch (Exception ex) {
                        Misc.printStackTrace(ex);
                    }

                    if (isMemoryTransferMode()) {
                        try {
                            source.setXferMech(TwainConstants.TWSX_MEMORY);
                        } catch (Exception ex) {
                            Misc.printStackTrace(ex);
                        }
                    }

                }
            } catch (Exception e) {
                Misc.printStackTrace(e);
            }

        } else if (type.equals(ScannerIOMetadata.STATECHANGE)) {

            if (metadata.isFinished()) {
                if (metadata instanceof TwainIOMetadata) {
                    TwainSource source = ((TwainIOMetadata) metadata).getSource();
                    try {
                        source.close();
                    } catch (TwainIOException e) {
                        Misc.printStackTrace(e);
                    }
                    if (getScannerClientDialog() != null) {
                        getScannerClientDialog().dispose();
                    }
                }
            }

            if (metadata.isState(TwainConstants.STATE_TRANSFERREADY)) {
                statusLabel.setText(Localizer.localize("UI", "ScanningMessage"));
            }

            if (metadata.isState(TwainConstants.STATE_SRCMNGOPEN)) { // state = 3

                if (metadata instanceof TwainIOMetadata) {
                    TwainSource source = ((TwainIOMetadata) metadata).getSource();

                    if (source.isBusy()) {

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

                }
            }

        } else if (type.equals(ScannerIOMetadata.EXCEPTION)) {
            Misc.showExceptionMsg(this, metadata.getException());
            Misc.printStackTrace(metadata.getException());
        }

    }


    private void updateMemoryImageSize(Info info) {

        this.memoryImageTileLocations.add(
            new Rectangle2D.Double(info.getLeft(), info.getTop(), info.getWidth(),
                info.getHeight()));

        int thisMaxTileWidth = info.getWidth() + info.getLeft();

        if (thisMaxTileWidth > this.maxTileWidth) {
            this.maxTileWidth = thisMaxTileWidth;
        }

        int thisMaxTileHeight = info.getHeight() + info.getTop();

        if (thisMaxTileHeight > this.maxTileHeight) {
            this.maxTileHeight = thisMaxTileHeight;
        }

    }

    private BufferedImage getBufferedImageFromTiles() {

        BufferedImage bi = null;

        for (int i = 0; i < this.memoryTitles.size(); i++) {

            if (bi == null) {
                bi = new BufferedImage(this.maxTileWidth, this.maxTileHeight,
                    this.memoryTitles.get(i).getType());
            }

            Graphics2D g2d = bi.createGraphics();
            g2d.drawImage(this.memoryTitles.get(i), null,
                (int) this.memoryImageTileLocations.get(i).getMinX(),
                (int) this.memoryImageTileLocations.get(i).getMinY());

        }

        // reset the memory data
        this.maxTileHeight = 0;
        this.maxTileHeight = 0;
        this.memoryTitles = new Vector<BufferedImage>();
        this.memoryImageTileLocations = new Vector<Rectangle2D>();

        return bi;
    }

    private float getBlackThreshold() {
        return ((Integer) this.blackThresholdSpinner.getValue()).floatValue();
    }

    public boolean isUsingVendorUI() {
        if (uiComboBox.getSelectedIndex() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public int getDPI() {
        return (Integer) resolutionSpinner.getValue();
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
            return TwainConstants.TWPT_BW;
        } else if (colorComboBox.getSelectedIndex() == 1) {
            return TwainConstants.TWPT_GRAY;
        } else if (colorComboBox.getSelectedIndex() == 2) {
            return TwainConstants.TWPT_RGB;
        } else {
            return colorType;
        }

    }

    public boolean isMemoryTransferMode() {
        if (transferModeComboBox.getSelectedIndex() == 0) {
            return true;
        } else {
            return false;
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

    public static String[] getPageSizes() {
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
