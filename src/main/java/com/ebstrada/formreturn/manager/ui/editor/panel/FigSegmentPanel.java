package com.ebstrada.formreturn.manager.ui.editor.panel;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigSegment;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.editor.dialog.FigSegmentSetup;
import org.jdesktop.swingx.*;

public class FigSegmentPanel extends EditorPanel {

    private static final long serialVersionUID = 1L;

    private Fig selectedElement;

    public FigSegmentPanel() {
        initComponents();
    }

    @Override public void updatePanel() {
    }

    @Override public void removeListeners() {
    }

    @Override public void setSelectedElement(Fig selectedFig) {
        selectedElement = selectedFig;
    }

    private void loadSegmentsButtonActionPerformed(ActionEvent e) {
        FigSegmentSetup fss = new FigSegmentSetup((FigSegment) selectedElement);
        fss.setTitle(Localizer.localize("UI", "SegmentSetupDialogTitle"));
        fss.setModal(true);
        fss.setVisible(true);
        fss.dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        loadSegmentsButton = new JButton();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setOpaque(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        this.setTitle(Localizer.localize("UI", "SegmentAreaPanelTitle"));

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

            //---- loadSegmentsButton ----
            loadSegmentsButton.setFocusPainted(false);
            loadSegmentsButton.setFont(UIManager.getFont("Button.font"));
            loadSegmentsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    loadSegmentsButtonActionPerformed(e);
                }
            });
            loadSegmentsButton
                .setText(Localizer.localize("UI", "SegmentAreaLoadSegmentsButtonText"));
            panel1.add(loadSegmentsButton,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1);
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JPanel panel1;
    private JButton loadSegmentsButton;
    // JFormDesigner - End of variables declaration //GEN-END:variables
}
