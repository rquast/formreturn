package com.ebstrada.formreturn.manager.ui.reprocessor.panel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.reprocessor.frame.ReprocessorFrame;

import org.jdesktop.swingx.*;

public class TemplatePropertiesPanel extends ReprocessorPanel {

    private static final long serialVersionUID = 1L;
    private ReprocessorFrame reprocessorFrame;

    private Fig selectedFig;

    public TemplatePropertiesPanel(ReprocessorFrame reprocessorFrame) {
        initComponents();
        this.reprocessorFrame = reprocessorFrame;
        restoreSettings();
    }

    private void restoreSettings() {
        setFormPageID(reprocessorFrame.getFormPageId());
    }

    private void setFormPageID(long formPageId) {
        if (formPageId > 0) {
            formPageIDTextField.setText(formPageId + "");
        }
    }

    @Override public void removeListeners() {
        // TODO Auto-generated method stub

    }

    @Override public void updatePanel() {
        restoreSettings();
    }

    private void selectFormPageIDButtonActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reprocessorFrame.selectFormPageIDButtonActionPerformed(e);
                setFormPageID(reprocessorFrame.getFormPageId());
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        formPageIDLabel = new JLabel();
        formPageIDTextField = new JTextField();
        actionsLabel = new JLabel();
        selectFormPageIDButton = new JButton();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setOpaque(false);
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "TemplatePanelTitle"));

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights =
                new double[] {1.0, 1.0, 1.0, 1.0, 1.0E-4};

            //---- formPageIDLabel ----
            formPageIDLabel.setFont(UIManager.getFont("Label.font"));
            formPageIDLabel.setText(Localizer.localize("UI", "TemplatePanelFormPageIDLabel"));
            panel1.add(formPageIDLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- formPageIDTextField ----
            formPageIDTextField.setEditable(false);
            panel1.add(formPageIDTextField,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- actionsLabel ----
            actionsLabel.setFont(UIManager.getFont("Label.font"));
            actionsLabel.setText(Localizer.localize("UI", "TemplatePanelActionsLabel"));
            panel1.add(actionsLabel,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- selectFormPageIDButton ----
            selectFormPageIDButton.setFont(UIManager.getFont("Button.font"));
            selectFormPageIDButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/folder_magnify.png")));
            selectFormPageIDButton.setFocusPainted(false);
            selectFormPageIDButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    selectFormPageIDButtonActionPerformed(e);
                }
            });
            selectFormPageIDButton
                .setText(Localizer.localize("UI", "TemplatePanelSelectFormPageIDButtonText"));
            panel1.add(selectFormPageIDButton,
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel formPageIDLabel;
    private JTextField formPageIDTextField;
    private JLabel actionsLabel;
    private JButton selectFormPageIDButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    @Override public void setSelectedElement(Fig selectedFig) {
        this.selectedFig = selectedFig;
    }

}
