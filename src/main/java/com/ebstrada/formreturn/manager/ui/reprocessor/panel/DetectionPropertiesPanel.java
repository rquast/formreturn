package com.ebstrada.formreturn.manager.ui.reprocessor.panel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.reprocessor.frame.ReprocessorFrame;

import org.jdesktop.swingx.*;

public class DetectionPropertiesPanel extends ReprocessorPanel {

    private static final long serialVersionUID = 1L;
    private ReprocessorFrame reprocessorFrame;

    private Fig selectedFig;

    public DetectionPropertiesPanel(ReprocessorFrame reprocessorFrame) {
        initComponents();
        this.reprocessorFrame = reprocessorFrame;
    }


    @Override public void removeListeners() {
        // TODO Auto-generated method stub

    }

    @Override public void updatePanel() {
    }

    private void detectDataButtonActionPerformed(ActionEvent e) {
        reprocessorFrame.detectDataButtonActionPerformed(e);
    }

    private void revertButtonActionPerformed(ActionEvent e) {
        reprocessorFrame.revertButtonActionPerformed(e);
    }

    private void saveButtonActionPerformed(ActionEvent e) {
        reprocessorFrame.saveCapturedDataButtonActionPerformed(e);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        detectDataButton = new JButton();
        revertButton = new JButton();
        saveButton = new JButton();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setOpaque(false);
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "DetectionPanelTitle"));

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0E-4};

            //---- detectDataButton ----
            detectDataButton.setFont(UIManager.getFont("Button.font"));
            detectDataButton.setFocusPainted(false);
            detectDataButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/preview/zoom.png")));
            detectDataButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    detectDataButtonActionPerformed(e);
                }
            });
            detectDataButton.setText(Localizer.localize("UI", "DetectionPanelFindDataButtonText"));
            panel1.add(detectDataButton,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- revertButton ----
            revertButton.setFont(UIManager.getFont("Button.font"));
            revertButton.setFocusPainted(false);
            revertButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/preview/arrow_rotate_anticlockwise.png")));
            revertButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    revertButtonActionPerformed(e);
                }
            });
            revertButton.setText(Localizer.localize("UI", "DetectionPanelRevertButtonText"));
            panel1.add(revertButton,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- saveButton ----
            saveButton.setFont(UIManager.getFont("Button.font"));
            saveButton.setFocusPainted(false);
            saveButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
            saveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    saveButtonActionPerformed(e);
                }
            });
            saveButton.setText(Localizer.localize("UI", "DetectionPanelSaveButtonText"));
            panel1.add(saveButton,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JButton detectDataButton;
    private JButton revertButton;
    private JButton saveButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    @Override public void setSelectedElement(Fig selectedFig) {
        this.selectedFig = selectedFig;
    }

}
