package com.ebstrada.formreturn.manager.ui.editor.panel;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.jdesktop.swingx.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.editor.dialog.FormPropertiesDialog;
import com.ebstrada.formreturn.manager.ui.editor.frame.FormFrame;
import com.ebstrada.formreturn.manager.ui.frame.EditorFrame;

public class FormPropertiesPanel extends JXTaskPane {

    private static final long serialVersionUID = 1L;

    private FormFrame _formFrame;

    public FormPropertiesPanel(FormFrame _formFrame) {
        this._formFrame = _formFrame;
        initComponents();
    }

    private void setPropertiesButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                FormPropertiesDialog fpd =
                    new FormPropertiesDialog((Frame) _formFrame.getTopLevelAncestor());
                fpd.setSelectedFrame((EditorFrame) _formFrame);
                fpd.setTitle(Localizer.localize("UI", "FormPropertiesDialogTitle"));
                fpd.setVisible(true);
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        setPropertiesButton = new JButton();

        //======== this ========
        setOpaque(false);
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout) contentPane.getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) contentPane.getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout) contentPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) contentPane.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
        this.setTitle(Localizer.localize("UI", "FormPropertiesPanelTitle"));

        //---- setPropertiesButton ----
        setPropertiesButton.setFont(UIManager.getFont("Button.font"));
        setPropertiesButton.setFocusPainted(false);
        setPropertiesButton.setRequestFocusEnabled(false);
        setPropertiesButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                setPropertiesButtonActionPerformed(e);
            }
        });
        setPropertiesButton.setText(Localizer.localize("UI", "SetFormPropertiesButtonText"));
        contentPane.add(setPropertiesButton,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JButton setPropertiesButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public FormFrame getFormFrame() {
        return _formFrame;
    }

    public void setFormFrame(FormFrame formFrame) {
        this._formFrame = formFrame;
    }
}
