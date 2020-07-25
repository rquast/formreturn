package com.ebstrada.formreturn.manager.ui.editor.panel;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXTaskPane;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.editor.RecognitionPreviewPanel;

public class PreFlightCheckPanel extends JXTaskPane {

    private static final long serialVersionUID = 1L;

    private RecognitionPreviewPanel recognitionPreviewPanel;

    public PreFlightCheckPanel(RecognitionPreviewPanel recognitionPreviewPanel) {
        this.recognitionPreviewPanel = recognitionPreviewPanel;
        initComponents();
    }

    public void setSelectedCheckBoxes(boolean formDetectBarcode, boolean formDetectSegment,
        boolean formDetectfragment, boolean formDetectMarks) {
        detectBarcodesCheckBox.setSelected(formDetectBarcode);
        detectSegmentsCheckBox.setSelected(formDetectSegment);
        detectFragmentsCheckBox.setSelected(formDetectfragment);
        detectMarkedCharactersCheckBox.setSelected(formDetectMarks);
    }

    private void detectBarcodesCheckBoxItemStateChanged(ItemEvent e) {
        recognitionPreviewPanel.detectBarcodesCheckBoxItemStateChanged(e, detectBarcodesCheckBox);
    }

    private void detectSegmentsCheckBoxItemStateChanged(ItemEvent e) {
        recognitionPreviewPanel.detectSegmentsCheckBoxItemStateChanged(e, detectSegmentsCheckBox);
    }

    private void detectFragmentsCheckBoxItemStateChanged(ItemEvent e) {
        recognitionPreviewPanel.detectFragmentsCheckBoxItemStateChanged(e, detectFragmentsCheckBox);
    }

    private void detectMarkedCharactersCheckBoxItemStateChanged(ItemEvent e) {
        recognitionPreviewPanel
            .detectMarkedCharactersCheckBoxItemStateChanged(e, detectMarkedCharactersCheckBox);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        panel4 = new JPanel();
        detectBarcodesCheckBox = new JCheckBox();
        detectSegmentsCheckBox = new JCheckBox();
        detectFragmentsCheckBox = new JCheckBox();
        detectMarkedCharactersCheckBox = new JCheckBox();

        //======== this ========
        setOpaque(false);
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout) contentPane.getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) contentPane.getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout) contentPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) contentPane.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
        this.setTitle(Localizer.localize("UI", "PreviewCheckPanelTitle"));

        //======== panel4 ========
        {
            panel4.setBorder(null);
            panel4.setOpaque(false);
            panel4.setLayout(new GridBagLayout());
            ((GridBagLayout) panel4.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel4.getLayout()).rowHeights = new int[] {32, 32, 32, 30, 0};
            ((GridBagLayout) panel4.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel4.getLayout()).rowWeights =
                new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};

            //---- detectBarcodesCheckBox ----
            detectBarcodesCheckBox.setFont(UIManager.getFont("Label.font"));
            detectBarcodesCheckBox.setFocusPainted(false);
            detectBarcodesCheckBox.setOpaque(false);
            detectBarcodesCheckBox.addItemListener(new ItemListener() {
                @Override public void itemStateChanged(ItemEvent e) {
                    detectBarcodesCheckBoxItemStateChanged(e);
                }
            });
            detectBarcodesCheckBox
                .setText(Localizer.localize("UI", "PreviewCheckPanelDetectBarcodesCheckBox"));
            panel4.add(detectBarcodesCheckBox,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 2, 0), 0, 0));

            //---- detectSegmentsCheckBox ----
            detectSegmentsCheckBox.setFont(UIManager.getFont("Label.font"));
            detectSegmentsCheckBox.setFocusPainted(false);
            detectSegmentsCheckBox.setOpaque(false);
            detectSegmentsCheckBox.addItemListener(new ItemListener() {
                @Override public void itemStateChanged(ItemEvent e) {
                    detectSegmentsCheckBoxItemStateChanged(e);
                }
            });
            detectSegmentsCheckBox
                .setText(Localizer.localize("UI", "PreviewCheckPanelDetectSegmentsCheckBox"));
            panel4.add(detectSegmentsCheckBox,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 2, 0), 0, 0));

            //---- detectFragmentsCheckBox ----
            detectFragmentsCheckBox.setFont(UIManager.getFont("Label.font"));
            detectFragmentsCheckBox.setFocusPainted(false);
            detectFragmentsCheckBox.setOpaque(false);
            detectFragmentsCheckBox.addItemListener(new ItemListener() {
                @Override public void itemStateChanged(ItemEvent e) {
                    detectFragmentsCheckBoxItemStateChanged(e);
                }
            });
            detectFragmentsCheckBox
                .setText(Localizer.localize("UI", "PreviewCheckPanelDetectFragmentsCheckBox"));
            panel4.add(detectFragmentsCheckBox,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 2, 0), 0, 0));

            //---- detectMarkedCharactersCheckBox ----
            detectMarkedCharactersCheckBox.setFont(UIManager.getFont("Label.font"));
            detectMarkedCharactersCheckBox.setFocusPainted(false);
            detectMarkedCharactersCheckBox.setOpaque(false);
            detectMarkedCharactersCheckBox.addItemListener(new ItemListener() {
                @Override public void itemStateChanged(ItemEvent e) {
                    detectMarkedCharactersCheckBoxItemStateChanged(e);
                }
            });
            detectMarkedCharactersCheckBox.setText(
                Localizer.localize("UI", "PreviewCheckPanelDetectMarkedCharactersCheckBox"));
            panel4.add(detectMarkedCharactersCheckBox,
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel4,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JPanel panel4;
    private JCheckBox detectBarcodesCheckBox;
    private JCheckBox detectSegmentsCheckBox;
    private JCheckBox detectFragmentsCheckBox;
    private JCheckBox detectMarkedCharactersCheckBox;
    // JFormDesigner - End of variables declaration //GEN-END:variables
}
