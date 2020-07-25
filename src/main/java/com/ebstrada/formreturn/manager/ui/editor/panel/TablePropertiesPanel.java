package com.ebstrada.formreturn.manager.ui.editor.panel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.jdesktop.swingx.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.editor.frame.FormFrame;

public class TablePropertiesPanel extends JXTaskPane {

    private static final long serialVersionUID = 1L;

    private FormFrame formFrame;

    public TablePropertiesPanel(FormFrame formFrame) {
        initComponents();
        this.formFrame = formFrame;
    }

    private void refreshTablesButtonActionPerformed(ActionEvent e) {
        formFrame.getPfp().refresh();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        actionsLabel = new JLabel();
        refreshTablesButton = new JButton();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setOpaque(false);
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "TablesPanelTitle"));

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};

            //---- actionsLabel ----
            actionsLabel.setFont(UIManager.getFont("Label.font"));
            actionsLabel.setText(Localizer.localize("UI", "TablesPanelActionsLabel"));
            panel1.add(actionsLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- refreshTablesButton ----
            refreshTablesButton.setFont(UIManager.getFont("Button.font"));
            refreshTablesButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/sdm/database_refresh.png")));
            refreshTablesButton.setFocusPainted(false);
            refreshTablesButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    refreshTablesButtonActionPerformed(e);
                }
            });
            refreshTablesButton.setText(Localizer.localize("UI", "TablesPanelRefreshButtonText"));
            panel1.add(refreshTablesButton,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel actionsLabel;
    private JButton refreshTablesButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
