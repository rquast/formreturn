package com.ebstrada.formreturn.manager.ui.sdm.panel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.jdesktop.swingx.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.sdm.SourceDataManagerFrame;

public class FieldPropertiesPanel extends SDMPanel {

    private static final long serialVersionUID = 1L;

    SourceDataManagerFrame sourceDataManagerFrame;

    public FieldPropertiesPanel(SourceDataManagerFrame sourceDataManagerFrame) {
        initComponents();
        this.sourceDataManagerFrame = sourceDataManagerFrame;
    }

    private void addFieldButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.addFieldButtonActionPerformed(e);
    }

    private void removeFieldButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.removeFieldButtonActionPerformed(e);
    }

    private void editFieldButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.editFieldButtonActionPerformed(e);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        actionsLabel = new JLabel();
        addFieldButton = new JButton();
        editFieldButton = new JButton();
        removeFieldButton = new JButton();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setOpaque(false);
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "FieldPropertiesPanelTitle"));

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights =
                new double[] {1.0, 1.0, 1.0, 1.0, 1.0E-4};

            //---- actionsLabel ----
            actionsLabel.setFont(UIManager.getFont("Label.font"));
            actionsLabel.setText(Localizer.localize("UI", "FieldPropertiesPanelActionsLabel"));
            panel1.add(actionsLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- addFieldButton ----
            addFieldButton.setFont(UIManager.getFont("Button.font"));
            addFieldButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/sdm/table_add.png")));
            addFieldButton.setFocusPainted(false);
            addFieldButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addFieldButtonActionPerformed(e);
                }
            });
            addFieldButton
                .setText(Localizer.localize("UI", "FieldPropertiesPanelAddFieldButtonText"));
            panel1.add(addFieldButton,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- editFieldButton ----
            editFieldButton.setFont(UIManager.getFont("Button.font"));
            editFieldButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/sdm/table_edit.png")));
            editFieldButton.setFocusPainted(false);
            editFieldButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editFieldButtonActionPerformed(e);
                }
            });
            editFieldButton
                .setText(Localizer.localize("UI", "FieldPropertiesPanelEditFieldButtonText"));
            panel1.add(editFieldButton,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- removeFieldButton ----
            removeFieldButton.setFont(UIManager.getFont("Button.font"));
            removeFieldButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/sdm/table_delete.png")));
            removeFieldButton.setFocusPainted(false);
            removeFieldButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    removeFieldButtonActionPerformed(e);
                }
            });
            removeFieldButton
                .setText(Localizer.localize("UI", "FieldPropertiesPanelRemoveFieldButtonText"));
            panel1.add(removeFieldButton,
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel actionsLabel;
    private JButton addFieldButton;
    private JButton editFieldButton;
    private JButton removeFieldButton;

    // JFormDesigner - End of variables declaration  //GEN-END:variables
    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.ui.sdm.panel.SDMPanel#removeListeners()
     */
    @Override public void removeListeners() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.ui.sdm.panel.SDMPanel#updatePanel()
     */
    @Override public void updatePanel() {
        // TODO Auto-generated method stub

    }
}
