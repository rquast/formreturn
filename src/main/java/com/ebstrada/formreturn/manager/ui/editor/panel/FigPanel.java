package com.ebstrada.formreturn.manager.ui.editor.panel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ebstrada.formreturn.manager.gef.base.Editor;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import org.jdesktop.swingx.*;

public class FigPanel extends EditorPanel {

    private static final long serialVersionUID = 1L;

    private Fig selectedElement;

    public FigPanel() {
        super();
        initComponents();
    }

    @Override public void removeListeners() {
    }

    @Override public void updatePanel() {
        widthSpinner.setValue(new Integer(selectedElement.getWidth()));
        heightSpinner.setValue(new Integer(selectedElement.getHeight()));
        xSpinner.setValue(new Integer(selectedElement.getX()));
        ySpinner.setValue(new Integer(selectedElement.getY()));
    }

    @Override public void setSelectedElement(Fig selectedFig) {
        selectedElement = selectedFig;
    }

    private boolean checkWidth() {

        Editor ce = Globals.curEditor();

        PageAttributes currentPageAttributes = ce.getPageAttributes();
        int croppedWidth = currentPageAttributes.getCroppedWidth();

        Integer x = (Integer) xSpinner.getValue();
        Integer width = (Integer) widthSpinner.getValue();

        if ((x + width) > croppedWidth) {
            return false;
        }

        return true;

    }

    private boolean checkHeight() {

        Editor ce = Globals.curEditor();

        PageAttributes currentPageAttributes = ce.getPageAttributes();
        int croppedHeight = currentPageAttributes.getCroppedHeight();

        Integer y = (Integer) ySpinner.getValue();
        Integer height = (Integer) heightSpinner.getValue();

        if ((y + height) > croppedHeight) {
            return false;
        }

        return true;
    }

    private void widthSpinnerStateChanged(ChangeEvent e) {
        if (checkWidth()) {
            selectedElement.setWidth(((Integer) ((JSpinner) e.getSource()).getValue()).intValue());
            selectedElement.damage();
        } else {
            ((JSpinner) e.getSource()).getModel().setValue(selectedElement.getWidth());
        }
    }

    private void heightSpinnerStateChanged(ChangeEvent e) {
        if (checkHeight()) {
            selectedElement.setHeight(((Integer) ((JSpinner) e.getSource()).getValue()).intValue());
            selectedElement.damage();
        } else {
            ((JSpinner) e.getSource()).getModel().setValue(selectedElement.getHeight());
        }
    }

    private void xSpinnerStateChanged(ChangeEvent e) {
        if (checkWidth()) {
            selectedElement.setX(((Integer) ((JSpinner) e.getSource()).getValue()).intValue());
            selectedElement.damage();
        } else {
            ((JSpinner) e.getSource()).getModel().setValue(selectedElement.getX());
        }
    }

    private void ySpinnerStateChanged(ChangeEvent e) {
        if (checkHeight()) {
            selectedElement.setY(((Integer) ((JSpinner) e.getSource()).getValue()).intValue());
            selectedElement.damage();
        } else {
            ((JSpinner) e.getSource()).getModel().setValue(selectedElement.getY());
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        widthLabel = new JLabel();
        widthSpinner = new JSpinner();
        heightLabel = new JLabel();
        heightSpinner = new JSpinner();
        xLabel = new JLabel();
        xSpinner = new JSpinner();
        yLabel = new JLabel();
        ySpinner = new JSpinner();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setOpaque(false);
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "ElementPanelTitle"));

        //======== panel1 ========
        {
            panel1.setBorder(null);
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights =
                new double[] {1.0, 1.0, 1.0, 1.0, 1.0E-4};

            //---- widthLabel ----
            widthLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            widthLabel.setFont(UIManager.getFont("Label.font"));
            widthLabel.setText(Localizer.localize("UI", "ElementPanelWidthLabel"));
            panel1.add(widthLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

            //---- widthSpinner ----
            widthSpinner.setModel(new SpinnerNumberModel(0, 0, 6000, 1));
            widthSpinner.setFont(UIManager.getFont("Spinner.font"));
            widthSpinner.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    widthSpinnerStateChanged(e);
                }
            });
            panel1.add(widthSpinner,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- heightLabel ----
            heightLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            heightLabel.setFont(UIManager.getFont("Label.font"));
            heightLabel.setText(Localizer.localize("UI", "ElementPanelHeightLabel"));
            panel1.add(heightLabel,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

            //---- heightSpinner ----
            heightSpinner.setModel(new SpinnerNumberModel(0, 0, 9000, 1));
            heightSpinner.setFont(UIManager.getFont("Spinner.font"));
            heightSpinner.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    heightSpinnerStateChanged(e);
                }
            });
            panel1.add(heightSpinner,
                new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- xLabel ----
            xLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            xLabel.setFont(UIManager.getFont("Label.font"));
            xLabel.setText(Localizer.localize("UI", "ElementPanelXLabel"));
            panel1.add(xLabel,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

            //---- xSpinner ----
            xSpinner.setModel(new SpinnerNumberModel(0, 0, 6000, 1));
            xSpinner.setFont(UIManager.getFont("Spinner.font"));
            xSpinner.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    xSpinnerStateChanged(e);
                }
            });
            panel1.add(xSpinner,
                new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- yLabel ----
            yLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            yLabel.setFont(UIManager.getFont("Label.font"));
            yLabel.setText(Localizer.localize("UI", "ElementPanelYLabel"));
            panel1.add(yLabel,
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- ySpinner ----
            ySpinner.setModel(new SpinnerNumberModel(0, 0, 9000, 1));
            ySpinner.setFont(UIManager.getFont("Spinner.font"));
            ySpinner.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    ySpinnerStateChanged(e);
                }
            });
            panel1.add(ySpinner,
                new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1, BorderLayout.CENTER);
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel widthLabel;
    public JSpinner widthSpinner;
    private JLabel heightLabel;
    public JSpinner heightSpinner;
    private JLabel xLabel;
    public JSpinner xSpinner;
    private JLabel yLabel;
    public JSpinner ySpinner;
    // JFormDesigner - End of variables declaration //GEN-END:variables
}
