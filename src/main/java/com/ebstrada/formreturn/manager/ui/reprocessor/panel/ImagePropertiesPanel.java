package com.ebstrada.formreturn.manager.ui.reprocessor.panel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.reprocessor.frame.ReprocessorFrame;

import org.jdesktop.swingx.*;

public class ImagePropertiesPanel extends ReprocessorPanel {

    private static final long serialVersionUID = 1L;
    private ReprocessorFrame reprocessorFrame;

    private Fig selectedFig;

    public ImagePropertiesPanel(ReprocessorFrame reprocessorFrame) {
        initComponents();
        this.reprocessorFrame = reprocessorFrame;
    }


    @Override public void removeListeners() {
        // TODO Auto-generated method stub

    }

    @Override public void updatePanel() {
    }

    private void selectNewImageButtonActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reprocessorFrame.selectNewImageButtonActionPerformed(e);
            }
        });
    }

    private void saveToDiskButtonActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reprocessorFrame.saveToDiskButtonActionPerformed(e);
            }
        });
    }

    private void rotateImageButtonActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reprocessorFrame.rotateImageButtonActionPerformed(e);
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        selectNewImageButton = new JButton();
        rotateImageButton = new JButton();
        saveToDiskButton = new JButton();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setOpaque(false);
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "ReprocessImagePanelTitle"));

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0E-4};

            //---- selectNewImageButton ----
            selectNewImageButton.setFont(UIManager.getFont("Button.font"));
            selectNewImageButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/folder_image.png")));
            selectNewImageButton.setFocusPainted(false);
            selectNewImageButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    selectNewImageButtonActionPerformed(e);
                }
            });
            selectNewImageButton
                .setText(Localizer.localize("UI", "ReprocessImagePanelSelectNewImageButtonText"));
            panel1.add(selectNewImageButton,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- rotateImageButton ----
            rotateImageButton.setFont(UIManager.getFont("Button.font"));
            rotateImageButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/folder_image.png")));
            rotateImageButton.setFocusPainted(false);
            rotateImageButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    rotateImageButtonActionPerformed(e);
                }
            });
            rotateImageButton
                .setText(Localizer.localize("UI", "ReprocessImagePanelRotateImageButtonText"));
            panel1.add(rotateImageButton,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- saveToDiskButton ----
            saveToDiskButton.setFont(UIManager.getFont("Button.font"));
            saveToDiskButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
            saveToDiskButton.setFocusPainted(false);
            saveToDiskButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    saveToDiskButtonActionPerformed(e);
                }
            });
            saveToDiskButton
                .setText(Localizer.localize("UI", "ReprocessImagePanelSaveToDiskButtonText"));
            panel1.add(saveToDiskButton,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JButton selectNewImageButton;
    private JButton rotateImageButton;
    private JButton saveToDiskButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    @Override public void setSelectedElement(Fig selectedFig) {
        this.selectedFig = selectedFig;
    }

}
