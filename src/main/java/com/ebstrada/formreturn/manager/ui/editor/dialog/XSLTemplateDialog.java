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
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.ui.editor.persistence.XSLTemplate;
import com.ebstrada.formreturn.manager.ui.filefilter.FilenameExtensionFilter;
import com.ebstrada.formreturn.manager.util.Misc;

public class XSLTemplateDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private int dialogResult = JOptionPane.CANCEL_OPTION;

    private File templateFile;

    private XSLTemplate xslTemplate;

    public XSLTemplateDialog(Frame owner, XSLTemplate xslTemplate) {
        super(owner);
        this.xslTemplate = xslTemplate;
        initComponents();
        getRootPane().setDefaultButton(saveButton);
    }

    public XSLTemplateDialog(Dialog owner, XSLTemplate xslTemplate) {
        super(owner);
        this.xslTemplate = xslTemplate;
        initComponents();
        getRootPane().setDefaultButton(saveButton);
    }

    public XSLTemplate getXSLTemplate() {
        return xslTemplate;
    }

    public void setXSLTemplate(XSLTemplate xslTemplate) {
        this.xslTemplate = xslTemplate;
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
        filter.addExtension("xsl");

        FileDialog fd = new FileDialog(Main.getInstance(),
            Localizer.localize("UI", "SelectXSLTemplateFileDialogTitle"), FileDialog.LOAD);
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
            this.templateFile = file;
            try {
                this.xslTemplateFileTextField.setText(this.templateFile.getCanonicalPath());
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
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        xslTemplateFilePanel = new JPanel();
        xslTemplateFileLabel = new JLabel();
        xslTemplateFileTextField = new JTextField();
        browseButton = new JButton();
        templateDescriptionPanel = new JPanel();
        templateDescriptionLabel = new JLabel();
        templateDescriptionTextField = new JTextField();
        buttonBar = new JPanel();
        helpLabel = new JHelpLabel();
        saveButton = new JButton();
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
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UI", "XSLTemplateDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};

                //======== xslTemplateFilePanel ========
                {
                    xslTemplateFilePanel.setOpaque(false);
                    xslTemplateFilePanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)xslTemplateFilePanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                    ((GridBagLayout)xslTemplateFilePanel.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)xslTemplateFilePanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};
                    ((GridBagLayout)xslTemplateFilePanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                    //---- xslTemplateFileLabel ----
                    xslTemplateFileLabel.setFont(UIManager.getFont("Label.font"));
                    xslTemplateFileLabel.setText(Localizer.localize("UI", "XSLTemplateFileLabelText"));
                    xslTemplateFilePanel.add(xslTemplateFileLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- xslTemplateFileTextField ----
                    xslTemplateFileTextField.setFont(UIManager.getFont("TextField.font"));
                    xslTemplateFilePanel.add(xslTemplateFileTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- browseButton ----
                    browseButton.setFont(UIManager.getFont("Button.font"));
                    browseButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            browseButtonActionPerformed(e);
                        }
                    });
                    browseButton.setText(Localizer.localize("UI", "BrowseButtonText"));
                    xslTemplateFilePanel.add(browseButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(xslTemplateFilePanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== templateDescriptionPanel ========
                {
                    templateDescriptionPanel.setOpaque(false);
                    templateDescriptionPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)templateDescriptionPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
                    ((GridBagLayout)templateDescriptionPanel.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)templateDescriptionPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                    ((GridBagLayout)templateDescriptionPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                    //---- templateDescriptionLabel ----
                    templateDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                    templateDescriptionLabel.setText(Localizer.localize("UI", "XSLTemplateDescriptionLabelText"));
                    templateDescriptionPanel.add(templateDescriptionLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- templateDescriptionTextField ----
                    templateDescriptionTextField.setFont(UIManager.getFont("TextField.font"));
                    templateDescriptionPanel.add(templateDescriptionTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(templateDescriptionPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0};

                //---- helpLabel ----
                helpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                helpLabel.setHelpGUID("publication-xsl-fo-report-template");
                helpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                buttonBar.add(helpLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- saveButton ----
                saveButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/accept.png")));
                saveButton.setFont(UIManager.getFont("Button.font"));
                saveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveButtonActionPerformed(e);
                    }
                });
                saveButton.setText(Localizer.localize("UI", "OKButtonText"));
                buttonBar.add(saveButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UI", "CancelButtonText"));
                buttonBar.add(cancelButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(550, 180);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel xslTemplateFilePanel;
    private JLabel xslTemplateFileLabel;
    private JTextField xslTemplateFileTextField;
    private JButton browseButton;
    private JPanel templateDescriptionPanel;
    private JLabel templateDescriptionLabel;
    private JTextField templateDescriptionTextField;
    private JPanel buttonBar;
    private JHelpLabel helpLabel;
    private JButton saveButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public String getTemplateDescription() {
        return this.templateDescriptionTextField.getText().trim();
    }

    public String getFileName() {
        return this.templateFile.getName();
    }

    public File getFile() {
        return this.templateFile;
    }

    public String getTemplateGUID() throws IOException {
        return Misc.getMD5Sum(new String(Misc.getBytesFromFile(this.templateFile)));
    }

}
