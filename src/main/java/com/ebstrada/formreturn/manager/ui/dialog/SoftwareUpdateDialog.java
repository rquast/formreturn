package com.ebstrada.formreturn.manager.ui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.SoftwareUpdateManager;
import com.ebstrada.formreturn.manager.ui.Main;

public class SoftwareUpdateDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    public SoftwareUpdateDialog(Frame owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(okButton);
    }

    public SoftwareUpdateDialog(Dialog owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(okButton);
    }

    private void okButtonActionPerformed(ActionEvent e) {

        SoftwareUpdateManager sum = Main.getInstance().getSoftwareUpdateManager();
        if (ignoreThisVersionCheckBox.isSelected()) {
            sum.ignoreVersion(sum.getLatestVersion());
        }

        if (disableSoftwareUpdateCheckBox.isSelected()) {
            sum.disableSoftwareUpdate();
        }

        if (yesRadioButton.isSelected()) {
            Misc.openURL("https://www.formreturn.com/download");
        }

        dispose();

    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                okButton.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label3 = new JLabel();
        panel2 = new JPanel();
        newVersionAvailableLabel = new JLabel();
        wouldYouLikeToDownloadLabel = new JLabel();
        panel1 = new JPanel();
        yesRadioButton = new JRadioButton();
        noRadioButton = new JRadioButton();
        ignoreThisVersionCheckBox = new JCheckBox();
        disableSoftwareUpdateCheckBox = new JCheckBox();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        setAlwaysOnTop(true);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UI", "SoftwareUpdateDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0, 50, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //---- label3 ----
                label3.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/images/softwareupdate.png")));
                contentPanel.add(label3,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                //======== panel2 ========
                {
                    panel2.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel2.getLayout()).columnWidths = new int[] {25, 0, 0};
                    ((GridBagLayout) panel2.getLayout()).rowHeights =
                        new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
                    ((GridBagLayout) panel2.getLayout()).columnWeights =
                        new double[] {0.0, 1.0, 1.0E-4};
                    ((GridBagLayout) panel2.getLayout()).rowWeights =
                        new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                    //---- newVersionAvailableLabel ----
                    newVersionAvailableLabel.setHorizontalAlignment(SwingConstants.LEFT);
                    newVersionAvailableLabel.setFont(UIManager.getFont("Label.font"));
                    newVersionAvailableLabel
                        .setText(Localizer.localize("UI", "SoftwareUpdateNewVersionLabel"));
                    panel2.add(newVersionAvailableLabel,
                        new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                    //---- wouldYouLikeToDownloadLabel ----
                    wouldYouLikeToDownloadLabel.setFont(UIManager.getFont("Label.font"));
                    wouldYouLikeToDownloadLabel
                        .setText(Localizer.localize("UI", "SoftwareUpdateLikeToDownloadLabel"));
                    panel2.add(wouldYouLikeToDownloadLabel,
                        new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                    //======== panel1 ========
                    {
                        panel1.setBorder(new EmptyBorder(5, 0, 0, 0));
                        panel1.setLayout(new GridBagLayout());
                        ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {25, 0, 0};
                        ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0};
                        ((GridBagLayout) panel1.getLayout()).columnWeights =
                            new double[] {0.0, 1.0, 1.0E-4};
                        ((GridBagLayout) panel1.getLayout()).rowWeights =
                            new double[] {0.0, 0.0, 1.0E-4};

                        //---- yesRadioButton ----
                        yesRadioButton.setSelected(true);
                        yesRadioButton.setFont(UIManager.getFont("RadioButton.font"));
                        yesRadioButton.setText(Localizer.localize("UI", "YesRadioButtonText"));
                        panel1.add(yesRadioButton,
                            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                        //---- noRadioButton ----
                        noRadioButton.setFont(UIManager.getFont("RadioButton.font"));
                        noRadioButton.setText(Localizer.localize("UI", "NoRadioButtonText"));
                        panel1.add(noRadioButton,
                            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                    }
                    panel2.add(panel1,
                        new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                    //---- ignoreThisVersionCheckBox ----
                    ignoreThisVersionCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
                    ignoreThisVersionCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                    ignoreThisVersionCheckBox
                        .setText(Localizer.localize("UI", "SoftwareUpdateIgnoreVersionCheckBox"));
                    panel2.add(ignoreThisVersionCheckBox,
                        new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                    //---- disableSoftwareUpdateCheckBox ----
                    disableSoftwareUpdateCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                    disableSoftwareUpdateCheckBox
                        .setText(Localizer.localize("UI", "SoftwareUpdateDisableCheckBox"));
                    panel2.add(disableSoftwareUpdateCheckBox,
                        new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
                }
                contentPanel.add(panel2,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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

                //---- okButton ----
                okButton.setFont(UIManager.getFont("Button.font"));
                okButton.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/accept.png")));
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
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
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
        setSize(565, 280);
        setLocationRelativeTo(null);

        //---- downloadNewVersionButtonGroup ----
        ButtonGroup downloadNewVersionButtonGroup = new ButtonGroup();
        downloadNewVersionButtonGroup.add(yesRadioButton);
        downloadNewVersionButtonGroup.add(noRadioButton);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label3;
    private JPanel panel2;
    private JLabel newVersionAvailableLabel;
    private JLabel wouldYouLikeToDownloadLabel;
    private JPanel panel1;
    private JRadioButton yesRadioButton;
    private JRadioButton noRadioButton;
    private JCheckBox ignoreThisVersionCheckBox;
    private JCheckBox disableSoftwareUpdateCheckBox;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
