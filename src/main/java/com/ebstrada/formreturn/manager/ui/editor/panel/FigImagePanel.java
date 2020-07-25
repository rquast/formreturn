package com.ebstrada.formreturn.manager.ui.editor.panel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.ebstrada.formreturn.manager.gef.base.Editor;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigImage;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.dialog.FigImageProperties;
import org.jdesktop.swingx.*;

public class FigImagePanel extends EditorPanel {

    private static final long serialVersionUID = 1L;

    private Fig selectedElement;

    public FigImagePanel() {
        initComponents();
    }

    @Override public void updatePanel() {
        // TODO
    }

    @Override public void removeListeners() {
    }

    @Override public void setSelectedElement(Fig selectedFig) {
        selectedElement = selectedFig;
        retainShapeCheckBox.setSelected(((FigImage) selectedElement).isRetainShape());
    }

    private void imagePropertiesButtonActionPerformed(ActionEvent e) {
        // FigImageProperties.setSegmentType(PageAttributes.BODY);
        FigImageProperties fis = new FigImageProperties(Main.getInstance(), selectedElement);
        fis.setTitle(Localizer.localize("UI", "ImagePropertiesDialogTitle"));
        fis.setModal(true);
        fis.setVisible(true);

        if (fis.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {

            // TODO: do something with checkbox properties.

        }

        fis.dispose();
    }

    private void setToImageSizeButtonActionPerformed(ActionEvent e) {

        Rectangle2D naturalImageSize = ((FigImage) selectedElement).getNaturalImageSize();

        Editor ce = Globals.curEditor();

        PageAttributes currentPageAttributes = ce.getPageAttributes();
        int croppedWidth = currentPageAttributes.getCroppedWidth();
        int croppedHeight = currentPageAttributes.getCroppedHeight();

        int x = selectedElement.getX();
        int y = selectedElement.getY();
        int naturalWidth = (int) naturalImageSize.getWidth();
        int naturalHeight = (int) naturalImageSize.getHeight();

        boolean unableToResize = false;

        if (x + naturalWidth > croppedWidth) {
            if (naturalWidth <= croppedWidth) {
                x = croppedWidth - naturalWidth;
            } else {
                unableToResize = true;
            }
        }

        if (y + naturalHeight > croppedHeight) {
            if (naturalHeight <= croppedHeight) {
                y = croppedHeight - naturalHeight;
            } else {
                unableToResize = true;
            }
        }

        if (!unableToResize) {
            selectedElement.setBounds(x, y, naturalWidth, naturalHeight);
            selectedElement.damage();
        } else {
            String message = Localizer.localize("UI", "ImageAreaUnableToResizeMessage");
            String caption = Localizer.localize("UI", "ImageAreaUnableToResizeTitle");
            javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
                javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.ERROR_MESSAGE);
        }

    }

    private void retainShapeCheckBoxActionPerformed(ActionEvent e) {
        if (retainShapeCheckBox.isSelected()) {
            ((FigImage) selectedElement).setRetainShape(true);
        } else {
            ((FigImage) selectedElement).setRetainShape(false);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        imagePropertiesButton = new JButton();
        setToImageSizeButton = new JButton();
        retainShapeCheckBox = new JCheckBox();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        this.setTitle(Localizer.localize("UI", "ImageAreaPanelTitle"));

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setBorder(null);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0E-4};

            //---- imagePropertiesButton ----
            imagePropertiesButton.setFocusPainted(false);
            imagePropertiesButton.setFont(UIManager.getFont("Button.font"));
            imagePropertiesButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    imagePropertiesButtonActionPerformed(e);
                }
            });
            imagePropertiesButton
                .setText(Localizer.localize("UI", "ImageAreaSelectImageButtonText"));
            panel1.add(imagePropertiesButton,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- setToImageSizeButton ----
            setToImageSizeButton.setFont(UIManager.getFont("Button.font"));
            setToImageSizeButton.setFocusPainted(false);
            setToImageSizeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setToImageSizeButtonActionPerformed(e);
                }
            });
            setToImageSizeButton
                .setText(Localizer.localize("UI", "ImageAreaSetDefaultSizeButtonText"));
            panel1.add(setToImageSizeButton,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- retainShapeCheckBox ----
            retainShapeCheckBox.setFont(UIManager.getFont("Label.font"));
            retainShapeCheckBox.setOpaque(false);
            retainShapeCheckBox.setFocusPainted(false);
            retainShapeCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    retainShapeCheckBoxActionPerformed(e);
                }
            });
            retainShapeCheckBox.setText(Localizer.localize("UI", "ImageAreaRetainShapeCheckBox"));
            panel1.add(retainShapeCheckBox,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1);
        // //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JPanel panel1;
    private JButton imagePropertiesButton;
    private JButton setToImageSizeButton;
    private JCheckBox retainShapeCheckBox;
    // JFormDesigner - End of variables declaration //GEN-END:variables
}
