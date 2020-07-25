package com.ebstrada.formreturn.manager.ui.editor.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.font.CachedFont;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.editor.panel.FigTextPanel;

public class EmbeddedNoticeDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private FigTextPanel ftp;

    public EmbeddedNoticeDialog(Frame owner, FigTextPanel ftp) {
        super(owner);
        initComponents();
        this.ftp = ftp;
        CachedFont cf = ftp.getCachedFont();
        fontNameTextField.setText(cf.getFullFontName());
        fontCopyrightTextArea.setText(cf.getCopyright());
        fontLicenseTextArea.setText(cf.getLicense());
        fontLicenseURLTextArea.setText(cf.getLicenseURL());
    }

    public EmbeddedNoticeDialog(Dialog owner) {
        super(owner);
        initComponents();
    }

    private void acceptButtonActionPerformed(ActionEvent e) {
        ftp.embedFont();
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        ftp.cancelEmbed();
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        confirmLabel = new JLabel();
        panel1 = new JPanel();
        fontNameLabel = new JLabel();
        fontNameTextField = new JTextField();
        fontCopyrightLabel = new JLabel();
        scrollPane1 = new JScrollPane();
        fontCopyrightTextArea = new JTextArea();
        fontLicenseInformationLabel = new JLabel();
        scrollPane2 = new JScrollPane();
        fontLicenseTextArea = new JTextArea();
        fontLicenseURLLabel = new JLabel();
        fontLicenseURLTextArea = new JTextField();
        havePermissionLabel = new JLabel();
        buttonBar = new JPanel();
        acceptButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "EmbeddedNoticeDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0, 40, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights =
                    new double[] {0.0, 1.0, 0.0, 1.0E-4};

                //---- confirmLabel ----
                confirmLabel.setFont(UIManager.getFont("Label.font"));
                confirmLabel.setText(Localizer.localize("UI", "EmbeddedNoticeConfirmLabel"));
                contentPanel.add(confirmLabel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                //======== panel1 ========
                {
                    panel1.setBorder(new EmptyBorder(15, 0, 0, 0));
                    panel1.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0, 0};
                    ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
                    ((GridBagLayout) panel1.getLayout()).columnWeights =
                        new double[] {0.0, 1.0, 1.0E-4};
                    ((GridBagLayout) panel1.getLayout()).rowWeights =
                        new double[] {0.0, 1.0, 1.0, 0.0, 1.0E-4};

                    //---- fontNameLabel ----
                    fontNameLabel.setFont(UIManager.getFont("Label.font"));
                    fontNameLabel.setText(Localizer.localize("UI", "EmbeddedNoticeFontNameLabel"));
                    panel1.add(fontNameLabel,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                            GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                    //---- fontNameTextField ----
                    fontNameTextField.setEditable(false);
                    fontNameTextField.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
                    fontNameTextField.setFont(UIManager.getFont("TextField.font"));
                    fontNameTextField.setBackground(Color.white);
                    panel1.add(fontNameTextField,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                    //---- fontCopyrightLabel ----
                    fontCopyrightLabel.setFont(UIManager.getFont("Label.font"));
                    fontCopyrightLabel
                        .setText(Localizer.localize("UI", "EmbeddedNoticeFontCopyrightLabel"));
                    panel1.add(fontCopyrightLabel,
                        new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                            GridBagConstraints.VERTICAL, new Insets(0, 0, 5, 5), 0, 0));

                    //======== scrollPane1 ========
                    {
                        scrollPane1.setBorder(null);

                        //---- fontCopyrightTextArea ----
                        fontCopyrightTextArea
                            .setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
                        fontCopyrightTextArea.setEditable(false);
                        fontCopyrightTextArea.setFont(UIManager.getFont("TextArea.font"));
                        scrollPane1.setViewportView(fontCopyrightTextArea);
                    }
                    panel1.add(scrollPane1,
                        new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                    //---- fontLicenseInformationLabel ----
                    fontLicenseInformationLabel.setFont(UIManager.getFont("Label.font"));
                    fontLicenseInformationLabel.setText(
                        Localizer.localize("UI", "EmbeddedNoticeFontLicenseInformationLabel"));
                    panel1.add(fontLicenseInformationLabel,
                        new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                            GridBagConstraints.VERTICAL, new Insets(0, 0, 5, 5), 0, 0));

                    //======== scrollPane2 ========
                    {
                        scrollPane2.setBorder(null);

                        //---- fontLicenseTextArea ----
                        fontLicenseTextArea.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
                        fontLicenseTextArea.setEditable(false);
                        fontLicenseTextArea.setFont(UIManager.getFont("TextArea.font"));
                        scrollPane2.setViewportView(fontLicenseTextArea);
                    }
                    panel1.add(scrollPane2,
                        new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                    //---- fontLicenseURLLabel ----
                    fontLicenseURLLabel.setFont(UIManager.getFont("Label.font"));
                    fontLicenseURLLabel
                        .setText(Localizer.localize("UI", "EmbeddedNoticeFontLicenseURLLabel"));
                    panel1.add(fontLicenseURLLabel,
                        new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                            GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

                    //---- fontLicenseURLTextArea ----
                    fontLicenseURLTextArea.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
                    fontLicenseURLTextArea.setEditable(false);
                    fontLicenseURLTextArea.setFont(UIManager.getFont("TextField.font"));
                    fontLicenseURLTextArea.setBackground(Color.white);
                    panel1.add(fontLicenseURLTextArea,
                        new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(panel1,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                //---- havePermissionLabel ----
                havePermissionLabel.setFont(UIManager.getFont("Label.font"));
                havePermissionLabel
                    .setText(Localizer.localize("UI", "EmbeddedNoticeHavePermissionLabel"));
                contentPanel.add(havePermissionLabel,
                    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
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

                //---- acceptButton ----
                acceptButton.setFont(UIManager.getFont("Button.font"));
                acceptButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        acceptButtonActionPerformed(e);
                    }
                });
                acceptButton.setText(Localizer.localize("UI", "EmbeddedNoticeAcceptButtonText"));
                buttonBar.add(acceptButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
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
        setSize(655, 345);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel confirmLabel;
    private JPanel panel1;
    private JLabel fontNameLabel;
    private JTextField fontNameTextField;
    private JLabel fontCopyrightLabel;
    private JScrollPane scrollPane1;
    private JTextArea fontCopyrightTextArea;
    private JLabel fontLicenseInformationLabel;
    private JScrollPane scrollPane2;
    private JTextArea fontLicenseTextArea;
    private JLabel fontLicenseURLLabel;
    private JTextField fontLicenseURLTextArea;
    private JLabel havePermissionLabel;
    private JPanel buttonBar;
    private JButton acceptButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
