package com.ebstrada.formreturn.manager.ui.dialog;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class AboutDialog extends JDialog implements ClipboardOwner {

    private static final long serialVersionUID = 1L;

    public AboutDialog(Frame owner) {
        super(owner);
        initComponents();
        setSystemPropertiesTable();
        setThirdPartyLicense();
        aboutDialogTabbedPane.setTitleAt(0, Localizer.localize("UI", "SystemInformationTabTitle"));
        aboutDialogTabbedPane.setTitleAt(1, Localizer.localize("UI", "LicenseAgreementTabTitle"));
        aboutDialogTabbedPane.setTitleAt(2, Localizer.localize("UI", "ThirdPartyLicensesTabTitle"));
        getRootPane().setDefaultButton(closeButton);
    }



    public AboutDialog(Dialog owner) {
        super(owner);
        initComponents();
        setSystemPropertiesTable();
        setThirdPartyLicense();
        aboutDialogTabbedPane.setTitleAt(0, Localizer.localize("UI", "SystemInformationTabTitle"));
        aboutDialogTabbedPane.setTitleAt(1, Localizer.localize("UI", "LicenseAgreementTabTitle"));
        aboutDialogTabbedPane.setTitleAt(2, Localizer.localize("UI", "ThirdPartyLicensesTabTitle"));
        getRootPane().setDefaultButton(closeButton);
    }

    private void setThirdPartyLicense() {
        InputStream in = getClass().getResourceAsStream("/com/ebstrada/formreturn/NOTICE");
        try {
            thirdPartyLicenseTextPane.read(new InputStreamReader(in), null);
        } catch (IOException ex) {
            Misc.printStackTrace(ex);
        }
    }

    public void lostOwnership(Clipboard aClipboard, Transferable aContents) {
        //do nothing
    }

    public void setClipboardContents(String aString) {
        StringSelection stringSelection = new StringSelection(aString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, this);
    }

    @SuppressWarnings("serial") private void setSystemPropertiesTable() {
        Object[] keys = System.getProperties().keySet().toArray();
        Object[] values = System.getProperties().values().toArray();

        DefaultTableModel dtm = new DefaultTableModel(
            new Object[] {Localizer.localize("UI", "SystemKeyColumnText"),
                Localizer.localize("UI", "SystemValueColumnText")}, 0) {
            boolean[] columnEditable = new boolean[] {false, false};

            @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnEditable[columnIndex];
            }
        };
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != null && keys[i] != "") {
                dtm.insertRow(i, new Object[] {keys[i], values[i]});
            }
        }
        systemInformationTable.setModel(dtm);
    }

    private void okButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void copyToClipboardButtonActionPerformed(ActionEvent e) {
        String propertiesString = "";
        String lineEnding = System.getProperty("line.separator");

        Object[] keys = System.getProperties().keySet().toArray();
        Object[] values = System.getProperties().values().toArray();

        for (int i = 0; i < keys.length; i++) {
            propertiesString += keys[i] + " = " + values[i] + lineEnding;
        }

        setClipboardContents(propertiesString);
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                closeButton.requestFocusInWindow();
            }
        });
    }

    private void openDataFolderButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (Misc.showConfirmDialog(getRootPane().getTopLevelAncestor(),
                    Localizer.localize("UI", "OpenDataFolderTitle"),
                    Localizer.localize("UI", "OpenDataFolderMessage"),
                    Localizer.localize("UI", "Yes"), Localizer.localize("UI", "No"))) {
                    openDataFolder();
                }
            }
        });
    }

    private void openDataFolder() {
        File location = new File(PreferencesManager.getApplicationDir());
        try {
            Desktop.getDesktop().open(location);
        } catch (IOException e) {
            try {
                Misc.printStackTrace(e);
                Misc.showErrorMsg(getRootPane().getTopLevelAncestor(), String
                    .format(Localizer.localize("UI", "CouldNotOpenDataFolderMessage"),
                        location.getCanonicalPath()));
            } catch (IOException e1) {
                Misc.printStackTrace(e1);
            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        panel1 = new JPanel();
        panel8 = new JPanel();
        panel9 = new JPanel();
        versionLabel = new JLabel();
        copyrightLabel = new JLabel();
        publishedUnderLicenseLabel = new JLabel();
        logoIconLabel = new JLabel();
        aboutDialogTabbedPane = new JTabbedPane();
        panel3 = new JPanel();
        scrollPane1 = new JScrollPane();
        systemInformationTable = new JTable();
        panel5 = new JPanel();
        openDataFolderButton = new JButton();
        copyToClipboardButton = new JButton();
        panel6 = new JPanel();
        scrollPane3 = new JScrollPane();
        licenseTextPane = new JTextPane();
        panel4 = new JPanel();
        scrollPane2 = new JScrollPane();
        thirdPartyLicenseTextPane = new JTextPane();
        buttonBar = new JPanel();
        closeButton = new JButton();

        //======== this ========
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(
            String.format(Localizer.localize("UI", "AboutDialogTitle"), Main.APPLICATION_NAME));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new BorderLayout());

                //======== panel1 ========
                {
                    panel1.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout) panel1.getLayout()).rowWeights =
                        new double[] {0.0, 1.0, 1.0E-4};

                    //======== panel8 ========
                    {
                        panel8.setOpaque(false);
                        panel8.setLayout(new GridBagLayout());
                        ((GridBagLayout) panel8.getLayout()).columnWidths = new int[] {0, 0, 0};
                        ((GridBagLayout) panel8.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout) panel8.getLayout()).columnWeights =
                            new double[] {1.0, 0.0, 1.0E-4};
                        ((GridBagLayout) panel8.getLayout()).rowWeights =
                            new double[] {0.0, 1.0E-4};

                        //======== panel9 ========
                        {
                            panel9.setLayout(new GridBagLayout());
                            ((GridBagLayout) panel9.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout) panel9.getLayout()).rowHeights =
                                new int[] {0, 0, 0, 0};
                            ((GridBagLayout) panel9.getLayout()).columnWeights =
                                new double[] {1.0, 1.0E-4};
                            ((GridBagLayout) panel9.getLayout()).rowWeights =
                                new double[] {0.0, 0.0, 0.0, 1.0E-4};

                            //---- versionLabel ----
                            versionLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
                            versionLabel.setText(String
                                .format(Localizer.localize("UI", "VersionLabel"),
                                    Main.APPLICATION_NAME, Main.VERSION));
                            panel9.add(versionLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 0), 0, 0));
                        }
                        panel8.add(panel9,
                            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                        //---- logoIconLabel ----
                        logoIconLabel.setIcon(new ImageIcon(getClass().getResource(
                            "/com/ebstrada/formreturn/manager/ui/images/logoIcon.png")));
                        panel8.add(logoIconLabel,
                            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                    }
                    panel1.add(panel8,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                    //======== aboutDialogTabbedPane ========
                    {
                        aboutDialogTabbedPane.setFont(UIManager.getFont("TabbedPane.font"));

                        //======== panel3 ========
                        {
                            panel3.setOpaque(false);
                            panel3.setLayout(new GridBagLayout());
                            ((GridBagLayout) panel3.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout) panel3.getLayout()).rowHeights = new int[] {0, 0, 0};
                            ((GridBagLayout) panel3.getLayout()).columnWeights =
                                new double[] {1.0, 1.0E-4};
                            ((GridBagLayout) panel3.getLayout()).rowWeights =
                                new double[] {1.0, 0.0, 1.0E-4};

                            //======== scrollPane1 ========
                            {

                                //---- systemInformationTable ----
                                systemInformationTable.setShowHorizontalLines(false);
                                systemInformationTable.setShowVerticalLines(false);
                                systemInformationTable.setFont(UIManager.getFont("Table.font"));
                                systemInformationTable.getTableHeader()
                                    .setFont(UIManager.getFont("TableHeader.font"));
                                scrollPane1.setViewportView(systemInformationTable);
                            }
                            panel3.add(scrollPane1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 0), 0, 0));

                            //======== panel5 ========
                            {
                                panel5.setOpaque(false);
                                panel5.setLayout(new GridBagLayout());
                                ((GridBagLayout) panel5.getLayout()).columnWidths =
                                    new int[] {0, 0, 0, 0};
                                ((GridBagLayout) panel5.getLayout()).rowHeights = new int[] {0, 0};
                                ((GridBagLayout) panel5.getLayout()).columnWeights =
                                    new double[] {1.0, 0.0, 0.0, 1.0E-4};
                                ((GridBagLayout) panel5.getLayout()).rowWeights =
                                    new double[] {0.0, 1.0E-4};

                                //---- openDataFolderButton ----
                                openDataFolderButton.setFont(UIManager.getFont("Button.font"));
                                openDataFolderButton.addActionListener(new ActionListener() {
                                    @Override public void actionPerformed(ActionEvent e) {
                                        openDataFolderButtonActionPerformed(e);
                                    }
                                });
                                openDataFolderButton
                                    .setText(Localizer.localize("UI", "OpenDataFolderButtonText"));
                                panel5.add(openDataFolderButton,
                                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                        new Insets(0, 0, 0, 5), 0, 0));

                                //---- copyToClipboardButton ----
                                copyToClipboardButton.setFont(UIManager.getFont("Button.font"));
                                copyToClipboardButton.addActionListener(new ActionListener() {
                                    @Override public void actionPerformed(ActionEvent e) {
                                        copyToClipboardButtonActionPerformed(e);
                                    }
                                });
                                copyToClipboardButton.setText(Localizer
                                    .localize("UI", "CopySystemPropertiesToClipboardButtonText"));
                                panel5.add(copyToClipboardButton,
                                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                        new Insets(0, 0, 0, 0), 0, 0));
                            }
                            panel3.add(panel5, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        aboutDialogTabbedPane.addTab("System Information", panel3);

                        //======== panel6 ========
                        {
                            panel6.setOpaque(false);
                            panel6.setLayout(new GridBagLayout());
                            ((GridBagLayout) panel6.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout) panel6.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout) panel6.getLayout()).columnWeights =
                                new double[] {1.0, 1.0E-4};
                            ((GridBagLayout) panel6.getLayout()).rowWeights =
                                new double[] {1.0, 1.0E-4};

                            //======== scrollPane3 ========
                            {
                                scrollPane3.setFont(UIManager.getFont("ScrollPane.font"));

                                //---- licenseTextPane ----
                                licenseTextPane.setEditable(false);
                                licenseTextPane.setText(
                                    "Copyright 2020 EB Strada Holdings Pty Ltd and Contributors\n" +
                                    "\n" +
                                    "Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:\n" +
                                    "\n" +
                                    "The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.\n" +
                                    "\n" +
                                    "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE."
                                );
                                licenseTextPane.setFont(UIManager.getFont("TextPane.font"));
                                licenseTextPane.setCaretPosition(0);
                                scrollPane3.setViewportView(licenseTextPane);
                            }
                            panel6.add(scrollPane3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        aboutDialogTabbedPane.addTab("License Agreement", panel6);

                        //======== panel4 ========
                        {
                            panel4.setOpaque(false);
                            panel4.setLayout(new GridBagLayout());
                            ((GridBagLayout) panel4.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout) panel4.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout) panel4.getLayout()).columnWeights =
                                new double[] {1.0, 1.0E-4};
                            ((GridBagLayout) panel4.getLayout()).rowWeights =
                                new double[] {1.0, 1.0E-4};

                            //======== scrollPane2 ========
                            {

                                //---- thirdPartyLicenseTextPane ----
                                thirdPartyLicenseTextPane.setEditable(false);
                                thirdPartyLicenseTextPane
                                    .setFont(UIManager.getFont("TextPane.font"));
                                thirdPartyLicenseTextPane
                                    .setText(Localizer.localize("UI", "ThirdPartyLicenseTextPane"));
                                scrollPane2.setViewportView(thirdPartyLicenseTextPane);
                            }
                            panel4.add(scrollPane2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        aboutDialogTabbedPane.addTab("Third Party Licenses", panel4);
                    }
                    panel1.add(aboutDialogTabbedPane,
                        new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(panel1, BorderLayout.CENTER);
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
                closeButton.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
                closeButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
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
        setSize(990, 625);
        setLocationRelativeTo(null);
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel panel1;
    private JPanel panel8;
    private JPanel panel9;
    private JLabel versionLabel;
    private JLabel copyrightLabel;
    private JLabel publishedUnderLicenseLabel;
    private JLabel logoIconLabel;
    private JTabbedPane aboutDialogTabbedPane;
    private JPanel panel3;
    private JScrollPane scrollPane1;
    private JTable systemInformationTable;
    private JPanel panel5;
    private JButton openDataFolderButton;
    private JButton copyToClipboardButton;
    private JPanel panel6;
    private JScrollPane scrollPane3;
    private JTextPane licenseTextPane;
    private JPanel panel4;
    private JScrollPane scrollPane2;
    private JTextPane thirdPartyLicenseTextPane;
    private JPanel buttonBar;
    private JButton closeButton;
    // JFormDesigner - End of variables declaration //GEN-END:variables
}
