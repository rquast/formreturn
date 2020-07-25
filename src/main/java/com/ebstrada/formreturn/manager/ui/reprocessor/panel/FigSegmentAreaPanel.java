package com.ebstrada.formreturn.manager.ui.reprocessor.panel;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.swing.*;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigSegmentArea;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.persistence.jpa.Segment;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.reprocessor.dialog.SegmentStencilEditorDialog;
import com.ebstrada.formreturn.manager.ui.reprocessor.frame.ReprocessorFrame;

import org.jdesktop.swingx.*;

public class FigSegmentAreaPanel extends ReprocessorPanel {

    private static final long serialVersionUID = 1L;

    private Fig selectedElement;

    private ReprocessorFrame reprocessorFrame;

    private ArrayList<Long> segments;

    public FigSegmentAreaPanel(ReprocessorFrame reprocessorFrame) {
        this.reprocessorFrame = reprocessorFrame;
        initComponents();
    }

    @Override public void updatePanel() {
        restoreSegmentStencilComboBox();
    }

    @Override public void removeListeners() {
    }

    @Override public void setSelectedElement(Fig selectedFig) {
        selectedElement = selectedFig;
    }

    private void figSegmentAreaPropertiesButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SegmentStencilEditorDialog sasd;
                try {
                    sasd = new SegmentStencilEditorDialog(Main.getInstance(),
                        (FigSegmentArea) selectedElement);
                    sasd.setTitle(Localizer.localize("UI", "SegmentStencilEditorDialogTitle"));
                    sasd.setModal(true);
                    sasd.setVisible(true);
                    sasd.dispose();
                } catch (Exception ex) {
                }
            }
        });
    }



    private void restoreSegmentStencilComboBox() {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        DefaultComboBoxModel sncb = new DefaultComboBoxModel();

        try {
            long formPageId = reprocessorFrame.getFormPageId();
            FormPage formPage = entityManager.find(FormPage.class, formPageId);
            List<Segment> sc = formPage.getSegmentCollection();

            TreeMap<Integer, Segment> sortedSegments = new TreeMap<Integer, Segment>();

            for (Segment segment : sc) {
                sortedSegments.put(Integer.parseInt(segment.getBarcodeOne()), segment);
            }

            this.segments = new ArrayList<Long>(sortedSegments.size());

            int i = 0;
            int selectedIndex = 0;
            for (Integer barcodeValue : sortedSegments.keySet()) {
                Segment segment = sortedSegments.get(barcodeValue);
                String segmentName = String.format(
                    Localizer.localize("UI", "ReprocessorFrameSegmentAreaSelectionDropdown"),
                    segment.getSegmentId() + "", segment.getBarcodeOne() + "",
                    segment.getBarcodeTwo() + "");
                sncb.addElement(segmentName);
                segments.add(i, new Long(segment.getSegmentId()));
                if (isCurrentStencilIndex(segment.getSegmentId())) {
                    selectedIndex = i;
                }
                i++;
            }
            this.segmentStencilComboBox.setModel(sncb);
            this.segmentStencilComboBox.setSelectedIndex(selectedIndex);
            this.segmentStencilComboBox.revalidate();

        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }


    public void setSegmentArea(Long segmentId) {
        if ((selectedElement == null) || !(selectedElement instanceof FigSegmentArea)) {
            return;
        }
        FigSegmentArea fsa = (FigSegmentArea) selectedElement;
        if (fsa.getSegmentId() == segmentId) {
            return;
        }
        fsa.setSegmentId(segmentId);
        fsa.setLuminanceThreshold(reprocessorFrame.getLuminanceThreshold());
        fsa.setMarkThreshold(reprocessorFrame.getMarkThreshold());
        fsa.setFragmentPadding(reprocessorFrame.getFragmentPadding());
        fsa.damage();
    }


    private boolean isCurrentStencilIndex(long segmentId) {
        if ((selectedElement == null) || !(selectedElement instanceof FigSegmentArea)) {
            return false;
        }
        FigSegmentArea fsa = (FigSegmentArea) selectedElement;
        if (fsa.getSegmentId() == segmentId) {
            return true;
        } else {
            return false;
        }
    }

    private void segmentStencilComboBoxItemStateChanged(ItemEvent e) {
        Long segmentId = this.segments.get(this.segmentStencilComboBox.getSelectedIndex());
        setSegmentArea(segmentId);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        panel2 = new JPanel();
        segmentStencilPanelLabel = new JLabel();
        segmentStencilComboBox = new JComboBox();
        figSegmentAreaPropertiesButton = new JButton();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        this.setTitle(Localizer.localize("UI", "SegmentAreaPanelTitle"));

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setBorder(null);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};

            //======== panel2 ========
            {
                panel2.setOpaque(false);
                panel2.setLayout(new GridBagLayout());
                ((GridBagLayout) panel2.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) panel2.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout) panel2.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout) panel2.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

                //---- segmentStencilPanelLabel ----
                segmentStencilPanelLabel.setFont(UIManager.getFont("Label.font"));
                segmentStencilPanelLabel
                    .setText(Localizer.localize("UI", "SegmentStencilPanelLabelText"));
                panel2.add(segmentStencilPanelLabel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                //---- segmentStencilComboBox ----
                segmentStencilComboBox.setPrototypeDisplayValue("xxxxxxxxxx");
                segmentStencilComboBox.setFont(UIManager.getFont("ComboBox.font"));
                segmentStencilComboBox.addItemListener(new ItemListener() {
                    @Override public void itemStateChanged(ItemEvent e) {
                        segmentStencilComboBoxItemStateChanged(e);
                    }
                });
                panel2.add(segmentStencilComboBox,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            panel1.add(panel2,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- figSegmentAreaPropertiesButton ----
            figSegmentAreaPropertiesButton.setFocusPainted(false);
            figSegmentAreaPropertiesButton.setFont(UIManager.getFont("Button.font"));
            figSegmentAreaPropertiesButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    figSegmentAreaPropertiesButtonActionPerformed(e);
                }
            });
            figSegmentAreaPropertiesButton
                .setText(Localizer.localize("UI", "SegmentAreaPropertiesButtonText"));
            panel1.add(figSegmentAreaPropertiesButton,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1);
        // //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JPanel panel1;
    private JPanel panel2;
    private JLabel segmentStencilPanelLabel;
    private JComboBox segmentStencilComboBox;
    private JButton figSegmentAreaPropertiesButton;
    // JFormDesigner - End of variables declaration //GEN-END:variables
}
