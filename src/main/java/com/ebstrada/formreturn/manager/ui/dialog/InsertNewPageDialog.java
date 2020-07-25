package com.ebstrada.formreturn.manager.ui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.editor.frame.FormFrame;

public class InsertNewPageDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private FormFrame selectedFormFrame;

    public InsertNewPageDialog(Frame owner, FormFrame formFrame) {
        super(owner);
        selectedFormFrame = formFrame;
        initComponents();
        getRootPane().setDefaultButton(okButton);
    }

    public InsertNewPageDialog(Dialog owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(okButton);
    }

    private void okButtonActionPerformed(ActionEvent e) {
        if (beforeCurrentPageButton.isSelected()) {
            selectedFormFrame.addPageBeforeCurrentPage();
        } else if (afterCurrentPageButton.isSelected()) {
            selectedFormFrame.addPageAfterCurrentPage();
        } else if (endOfDocumentButton.isSelected()) {
            selectedFormFrame.addPageAtEndOfDocument();
        }
        this.dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        this.dispose();
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
        beforeCurrentPageButton = new JRadioButton();
        afterCurrentPageButton = new JRadioButton();
        endOfDocumentButton = new JRadioButton();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setModal(true);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UI", "InsertNewPageDialogText"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights =
                    new double[] {1.0, 1.0, 1.0, 1.0E-4};

                //---- beforeCurrentPageButton ----
                beforeCurrentPageButton.setFocusPainted(false);
                beforeCurrentPageButton.setFont(UIManager.getFont("RadioButton.font"));
                beforeCurrentPageButton
                    .setText(Localizer.localize("UI", "BeforeCurrentPageRadioButton"));
                contentPanel.add(beforeCurrentPageButton,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                //---- afterCurrentPageButton ----
                afterCurrentPageButton.setSelected(true);
                afterCurrentPageButton.setFocusPainted(false);
                afterCurrentPageButton.setFont(UIManager.getFont("RadioButton.font"));
                afterCurrentPageButton
                    .setText(Localizer.localize("UI", "AfterCurrentPageRadioButton"));
                contentPanel.add(afterCurrentPageButton,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                //---- endOfDocumentButton ----
                endOfDocumentButton.setFocusPainted(false);
                endOfDocumentButton.setFont(UIManager.getFont("RadioButton.font"));
                endOfDocumentButton.setText(Localizer.localize("UI", "EndOfDocumentRadioButton"));
                contentPanel.add(endOfDocumentButton,
                    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
                okButton.setText(Localizer.localize("UI", "OKButtonText"));
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
                cancelButton.setText(Localizer.localize("UI", "CancelButtonText"));
                buttonBar.add(cancelButton,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(310, 170);
        setLocationRelativeTo(null);

        //---- insertButtonGroup ----
        ButtonGroup insertButtonGroup = new ButtonGroup();
        insertButtonGroup.add(beforeCurrentPageButton);
        insertButtonGroup.add(afterCurrentPageButton);
        insertButtonGroup.add(endOfDocumentButton);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JRadioButton beforeCurrentPageButton;
    private JRadioButton afterCurrentPageButton;
    private JRadioButton endOfDocumentButton;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
