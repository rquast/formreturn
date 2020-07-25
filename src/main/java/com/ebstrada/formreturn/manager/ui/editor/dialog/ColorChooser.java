package com.ebstrada.formreturn.manager.ui.editor.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.util.Misc;

public class ColorChooser extends JDialog {

    private Fig selectedElement;

    private boolean isForegroundUpdate;

    private Vector selectedElements;

    public ColorChooser(Frame owner, Fig selectedElement, boolean isForegroundUpdate) {
        super(owner);
        this.selectedElement = selectedElement;
        this.isForegroundUpdate = isForegroundUpdate;
        initComponents();
        try {
            Misc.removeTransparencySlider(this.colorChooser);
        } catch (Exception e) {
            Misc.printStackTrace(e);
        }
        getRootPane().setDefaultButton(okButton);
    }

    public ColorChooser(Frame owner, Vector selectedElements, boolean isForegroundUpdate) {
        super(owner);
        this.selectedElements = selectedElements;
        this.isForegroundUpdate = isForegroundUpdate;
        initComponents();
        getRootPane().setDefaultButton(okButton);
    }

    public ColorChooser(Dialog owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(okButton);
    }

    private void okButtonActionPerformed(ActionEvent e) {

        if (selectedElements != null && selectedElements.size() > 0) {

            for (Object o : selectedElements) {

                Fig selectedElement = (Fig) o;
                if (isForegroundUpdate) {
                    selectedElement.setLineColor(colorChooser.getColor());
                } else {
                    selectedElement.setFillColor(colorChooser.getColor());
                }
                selectedElement.damage();

            }

            this.dispose();

        } else if (selectedElement != null) {

            if (isForegroundUpdate) {
                selectedElement.setLineColor(colorChooser.getColor());
            } else {
                selectedElement.setFillColor(colorChooser.getColor());
            }
            selectedElement.damage();
            this.dispose();
        }

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
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        colorChooser = new JColorChooser();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        // ======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        // ======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            // ======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                // ---- colorChooser ----
                colorChooser.setFont(UIManager.getFont("ColorChooser.font"));
                contentPanel.add(colorChooser,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            // ======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 0.0};

                // ---- okButton ----
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

                // ---- cancelButton ----
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
        setSize(500, 450);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JColorChooser colorChooser;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration //GEN-END:variables
}
