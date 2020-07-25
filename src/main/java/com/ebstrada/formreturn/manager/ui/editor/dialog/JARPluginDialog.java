package com.ebstrada.formreturn.manager.ui.editor.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.persistence.JARPlugin;
import com.ebstrada.formreturn.manager.ui.filefilter.FilenameExtensionFilter;
import com.ebstrada.formreturn.manager.util.Misc;

public class JARPluginDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private int dialogResult = JOptionPane.CANCEL_OPTION;

    private File jarFile;

    private JARPlugin jarPlugin;

    public JARPluginDialog(Frame owner, JARPlugin jarPlugin) {
        super(owner);
        this.jarPlugin = jarPlugin;
        initComponents();
        getRootPane().setDefaultButton(saveButton);
    }

    public JARPluginDialog(Dialog owner, JARPlugin jarPlugin) {
        super(owner);
        this.jarPlugin = jarPlugin;
        initComponents();
        getRootPane().setDefaultButton(saveButton);
    }

    public JARPlugin getJarPlugin() {
        return jarPlugin;
    }

    public void setJarPlugin(JARPlugin jarPlugin) {
        this.jarPlugin = jarPlugin;
    }

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    private void browseButtonActionPerformed(ActionEvent e) {
        File file = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();
        filter.addExtension("jar");

        FileDialog fd = new FileDialog(Main.getInstance(),
            Localizer.localize("UI", "SelectJARPluginFileDialogTitle"), FileDialog.LOAD);
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
            return;
        }
        fd.setLocationByPlatform(false);
        fd.setLocationRelativeTo(Main.getInstance());
        fd.setModal(true);
        fd.setVisible(true);
        if (fd.getFile() != null) {
            String filename = fd.getFile();
            file = new File(fd.getDirectory() + filename);
            if (file.isDirectory()) {
                return;
            }
        } else {
            return;
        }

        if (file != null) {
            this.jarFile = file;
            try {
                this.jarPluginFileTextField.setText(this.jarFile.getCanonicalPath());
            } catch (IOException e1) {
            }
        }
    }

    private void saveButtonActionPerformed(ActionEvent e) {
        this.dialogResult = JOptionPane.OK_OPTION;
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                saveButton.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        jarPluginFilePanel = new JPanel();
        jarPluginFileLabel = new JLabel();
        jarPluginFileTextField = new JTextField();
        browseButton = new JButton();
        pluginDescriptionPanel = new JPanel();
        pluginDescriptionLabel = new JLabel();
        pluginDescriptionTextField = new JTextField();
        buttonBar = new JPanel();
        saveButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UI", "JARPluginDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights =
                    new double[] {1.0, 1.0, 1.0E-4};

                //======== jarPluginFilePanel ========
                {
                    jarPluginFilePanel.setOpaque(false);
                    jarPluginFilePanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) jarPluginFilePanel.getLayout()).columnWidths =
                        new int[] {0, 0, 0, 0};
                    ((GridBagLayout) jarPluginFilePanel.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout) jarPluginFilePanel.getLayout()).columnWeights =
                        new double[] {0.0, 1.0, 0.0, 1.0E-4};
                    ((GridBagLayout) jarPluginFilePanel.getLayout()).rowWeights =
                        new double[] {0.0, 1.0E-4};

                    //---- jarPluginFileLabel ----
                    jarPluginFileLabel.setText(Localizer.localize("UI", "JARPluginFileLabelText"));
                    jarPluginFilePanel.add(jarPluginFileLabel,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
                    jarPluginFilePanel.add(jarPluginFileTextField,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                    //---- browseButton ----
                    browseButton.addActionListener(new ActionListener() {
                        @Override public void actionPerformed(ActionEvent e) {
                            browseButtonActionPerformed(e);
                        }
                    });
                    browseButton.setText(Localizer.localize("UI", "BrowseButtonText"));
                    jarPluginFilePanel.add(browseButton,
                        new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(jarPluginFilePanel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                //======== pluginDescriptionPanel ========
                {
                    pluginDescriptionPanel.setOpaque(false);
                    pluginDescriptionPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) pluginDescriptionPanel.getLayout()).columnWidths =
                        new int[] {0, 0, 0};
                    ((GridBagLayout) pluginDescriptionPanel.getLayout()).rowHeights =
                        new int[] {0, 0};
                    ((GridBagLayout) pluginDescriptionPanel.getLayout()).columnWeights =
                        new double[] {0.0, 1.0, 1.0E-4};
                    ((GridBagLayout) pluginDescriptionPanel.getLayout()).rowWeights =
                        new double[] {0.0, 1.0E-4};

                    //---- pluginDescriptionLabel ----
                    pluginDescriptionLabel
                        .setText(Localizer.localize("UI", "JARPluginDescriptionLabelText"));
                    pluginDescriptionPanel.add(pluginDescriptionLabel,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
                    pluginDescriptionPanel.add(pluginDescriptionTextField,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(pluginDescriptionPanel,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 0.0};

                //---- saveButton ----
                saveButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        saveButtonActionPerformed(e);
                    }
                });
                saveButton.setText(Localizer.localize("UI", "SaveButtonText"));
                buttonBar.add(saveButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
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
        setSize(500, 180);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel jarPluginFilePanel;
    private JLabel jarPluginFileLabel;
    private JTextField jarPluginFileTextField;
    private JButton browseButton;
    private JPanel pluginDescriptionPanel;
    private JLabel pluginDescriptionLabel;
    private JTextField pluginDescriptionTextField;
    private JPanel buttonBar;
    private JButton saveButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public String getPluginDescription() {
        return this.pluginDescriptionTextField.getText().trim();
    }

    public String getFileName() {
        return this.jarFile.getName();
    }

    public File getFile() {
        return this.jarFile;
    }

    public String getPluginGUID() throws IOException {
        return Misc.getMD5Sum(new String(Misc.getBytesFromFile(this.jarFile)));
    }

}
