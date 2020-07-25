package com.ebstrada.formreturn.manager.ui.sdm.panel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.sdm.SourceDataManagerFrame;
import org.jdesktop.swingx.*;

public class TablePropertiesPanel extends SDMPanel {

    private static final long serialVersionUID = 1L;

    private SourceDataManagerFrame sourceDataManagerFrame;

    public TablePropertiesPanel(SourceDataManagerFrame sourceDataManagerFrame) {
        initComponents();
        this.sourceDataManagerFrame = sourceDataManagerFrame;
    }

    @Override public void removeListeners() {
        // TODO Auto-generated method stub

    }

    @Override public void updatePanel() {
    }

    private void refreshTablesButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.refreshTablesButtonActionPerformed(e);
    }

    private void addTableButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.addTableButtonActionPerformed(e);
    }

    private void deleteTableButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.deleteTableButtonActionPerformed(e);
    }

    private void editTableButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.editTableButtonActionPerformed(e);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        actionsLabel = new JLabel();
        refreshTablesButton = new JButton();
        addTableButton = new JButton();
        editTableButton = new JButton();
        deleteTableButton = new JButton();

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
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights =
                new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0E-4};

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
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- addTableButton ----
            addTableButton.setFont(UIManager.getFont("Button.font"));
            addTableButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/sdm/table_add.png")));
            addTableButton.setFocusPainted(false);
            addTableButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addTableButtonActionPerformed(e);
                }
            });
            addTableButton.setText(Localizer.localize("UI", "TablesPanelAddTableButtonText"));
            panel1.add(addTableButton,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- editTableButton ----
            editTableButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/sdm/table_edit.png")));
            editTableButton.setFont(UIManager.getFont("Button.font"));
            editTableButton.setFocusPainted(false);
            editTableButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editTableButtonActionPerformed(e);
                }
            });
            editTableButton.setText(Localizer.localize("UI", "TablesPanelRenameButtonText"));
            panel1.add(editTableButton,
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- deleteTableButton ----
            deleteTableButton.setFont(UIManager.getFont("Button.font"));
            deleteTableButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/sdm/table_delete.png")));
            deleteTableButton.setFocusPainted(false);
            deleteTableButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    deleteTableButtonActionPerformed(e);
                }
            });
            deleteTableButton.setText(Localizer.localize("UI", "TablesPanelDeleteButtonText"));
            panel1.add(deleteTableButton,
                new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1, BorderLayout.CENTER);
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel actionsLabel;
    private JButton refreshTablesButton;
    private JButton addTableButton;
    private JButton editTableButton;
    private JButton deleteTableButton;
    // JFormDesigner - End of variables declaration //GEN-END:variables



}
