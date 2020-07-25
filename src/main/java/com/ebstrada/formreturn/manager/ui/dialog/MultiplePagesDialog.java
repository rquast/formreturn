package com.ebstrada.formreturn.manager.ui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;

public class MultiplePagesDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private int pageCount;

    public MultiplePagesDialog(Frame owner, int pageCount) {
        super(owner);
        this.pageCount = pageCount;
        initComponents();
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        for (int i = 1; i <= pageCount; i++) {
            dcbm.addElement(new Integer(i));
        }
        pageNumberComboBox.setModel(dcbm);
        getRootPane().setDefaultButton(chooseButton);
    }

    public MultiplePagesDialog(Dialog owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(chooseButton);
    }

    public int getSelectedPageNumber() {
        return (Integer) pageNumberComboBox.getSelectedItem();
    }

    private void chooseButtonActionPerformed(ActionEvent e) {
        // TODO: something here..
        dispose();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                chooseButton.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        containsMultipleImagesLabel = new JLabel();
        panel1 = new JPanel();
        selectPageNumberToViewLabel = new JLabel();
        pageNumberComboBox = new JComboBox();
        buttonBar = new JPanel();
        chooseButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setModal(true);
        setAlwaysOnTop(true);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UI", "MultiplePagesFoundDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights =
                    new int[] {0, 0, 15, 0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights =
                    new double[] {1.0, 0.0, 0.0, 1.0, 1.0, 1.0E-4};

                //---- containsMultipleImagesLabel ----
                containsMultipleImagesLabel.setFont(UIManager.getFont("Label.font"));
                containsMultipleImagesLabel
                    .setText(Localizer.localize("UI", "ContainsMultipleImagesLabel"));
                contentPanel.add(containsMultipleImagesLabel,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                //======== panel1 ========
                {
                    panel1.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                    ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout) panel1.getLayout()).columnWeights =
                        new double[] {0.0, 0.0, 1.0, 1.0E-4};
                    ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                    //---- selectPageNumberToViewLabel ----
                    selectPageNumberToViewLabel.setFont(UIManager.getFont("Label.font"));
                    selectPageNumberToViewLabel
                        .setText(Localizer.localize("UI", "SelectPageNumberToViewLabel"));
                    panel1.add(selectPageNumberToViewLabel,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                    //---- pageNumberComboBox ----
                    pageNumberComboBox.setFont(UIManager.getFont("ComboBox.font"));
                    pageNumberComboBox.setPrototypeDisplayValue("xxxxxxxx");
                    panel1.add(pageNumberComboBox,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
                }
                contentPanel.add(panel1,
                    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

                //---- chooseButton ----
                chooseButton.setFont(UIManager.getFont("Button.font"));
                chooseButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        chooseButtonActionPerformed(e);
                    }
                });
                chooseButton.setText(Localizer.localize("UI", "ChooseButtonText"));
                buttonBar.add(chooseButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(455, 195);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel containsMultipleImagesLabel;
    private JPanel panel1;
    private JLabel selectPageNumberToViewLabel;
    private JComboBox pageNumberComboBox;
    private JPanel buttonBar;
    private JButton chooseButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
