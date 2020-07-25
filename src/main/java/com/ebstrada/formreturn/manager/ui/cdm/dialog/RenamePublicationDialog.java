package com.ebstrada.formreturn.manager.ui.cdm.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.jpa.PublicationController;
import com.ebstrada.formreturn.manager.persistence.jpa.Publication;
import com.ebstrada.formreturn.manager.ui.cdm.CapturedDataManagerFrame;

public class RenamePublicationDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private CapturedDataManagerFrame capturedDataManagerFrame;
    private long publicationId;

    public RenamePublicationDialog(Frame owner, CapturedDataManagerFrame capturedDataManagerFrame) {
        super(owner);
        initComponents();
        this.capturedDataManagerFrame = capturedDataManagerFrame;
        this.publicationId = capturedDataManagerFrame.getSelectedPublicationIds()[0];
        restoreName();
        getRootPane().setDefaultButton(okButton);
    }

    public RenamePublicationDialog(Dialog owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(okButton);
    }

    private void restoreName() {
        PublicationController pc = new PublicationController();
        Publication publication = pc.getPublicationById(publicationId);
        if (publication != null) {
            publicationNameTextField.setText(publication.getPublicationName());
        }
    }

    private void okButtonActionPerformed(ActionEvent e) {
        PublicationController pc = new PublicationController();

        int stringLength = publicationNameTextField.getText().trim().length();
        if (stringLength > 0 && stringLength < 255) {
            pc.renamePublication(publicationId, publicationNameTextField.getText().trim());
            capturedDataManagerFrame.restorePublications();
            dispose();
        } else {
            String message = Localizer.localize("UICDM", "RenamePublicatonErrorMessage");
            String caption = Localizer.localize("UICDM", "ErrorTitle");
            javax.swing.JOptionPane
                .showConfirmDialog(this, message, caption, javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                publicationNameTextField.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        publicationNameTextField = new JTextField();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setModal(true);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UICDM", "RenamePublicationDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 200, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {0.0, 1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //---- label1 ----
                label1.setFont(UIManager.getFont("Label.font"));
                label1.setText(Localizer.localize("UICDM", "PublicationNameLabel"));
                contentPanel.add(label1,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                //---- publicationNameTextField ----
                publicationNameTextField.setFont(UIManager.getFont("TextField.font"));
                contentPanel.add(publicationNameTextField,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                okButton.setText(Localizer.localize("UICDM", "OKButtonText"));
                buttonBar.add(okButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UICDM", "CancelButtonText"));
                buttonBar.add(cancelButton,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(450, 125);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label1;
    private JTextField publicationNameTextField;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
