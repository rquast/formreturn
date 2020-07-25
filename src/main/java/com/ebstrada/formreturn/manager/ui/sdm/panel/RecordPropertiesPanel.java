package com.ebstrada.formreturn.manager.ui.sdm.panel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.sdm.SourceDataManagerFrame;
import org.jdesktop.swingx.*;

public class RecordPropertiesPanel extends SDMPanel {

    private static final long serialVersionUID = 1L;

    private SourceDataManagerFrame sourceDataManagerFrame;

    public RecordPropertiesPanel(SourceDataManagerFrame sourceDataManagerFrame) {
        initComponents();
        this.sourceDataManagerFrame = sourceDataManagerFrame;
    }

    private void recordsRefreshButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.refreshRecordsButtonActionPerformed(e);
    }

    private void addRecordButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.addRecordButtonActionPerformed(e);
    }

    private void deleteRecordButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.deleteRecordButtonActionPerformed(e);
    }

    private void editRecordButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.editRecordButtonActionPerformed(e);
    }

    private void importButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.importButtonActionPerformed(e);
    }

    private void exportRecordsButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.exportRecordsButtonActionPerformed(e);
    }

    private void exportAllButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.exportAllRecordsButtonActionPerformed(e);
    }

    private void fillButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.fillButtonActionPerformed(e);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        tableNameLabel = new JLabel();
        tableNameTextField = new JTextField();
        actionsLabel = new JLabel();
        recordsRefreshButton = new JButton();
        addRecordButton = new JButton();
        editRecordButton = new JButton();
        importButton = new JButton();
        fillButton = new JButton();
        deleteRecordButton = new JButton();
        exportRecordsButton = new JButton();
        exportAllButton = new JButton();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setOpaque(false);
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "RecordsPanelTitle"));

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights =
                new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights =
                new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 1.0, 1.0, 1.0, 1.0E-4};

            //---- tableNameLabel ----
            tableNameLabel.setFont(UIManager.getFont("Label.font"));
            tableNameLabel.setText(Localizer.localize("UI", "RecordsPanelTableNameLabel"));
            panel1.add(tableNameLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- tableNameTextField ----
            tableNameTextField.setEditable(false);
            tableNameTextField.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
            tableNameTextField.setBackground(Color.white);
            panel1.add(tableNameTextField,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- actionsLabel ----
            actionsLabel.setFont(UIManager.getFont("Label.font"));
            actionsLabel.setText(Localizer.localize("UI", "RecordsPanelActionsLabel"));
            panel1.add(actionsLabel,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- recordsRefreshButton ----
            recordsRefreshButton.setFont(UIManager.getFont("Button.font"));
            recordsRefreshButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/sdm/database_refresh.png")));
            recordsRefreshButton.setFocusPainted(false);
            recordsRefreshButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    recordsRefreshButtonActionPerformed(e);
                }
            });
            recordsRefreshButton.setText(Localizer.localize("UI", "RecordsPanelRefreshButtonText"));
            panel1.add(recordsRefreshButton,
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- addRecordButton ----
            addRecordButton.setFont(UIManager.getFont("Button.font"));
            addRecordButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/sdm/table_row_insert.png")));
            addRecordButton.setFocusPainted(false);
            addRecordButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    addRecordButtonActionPerformed(e);
                }
            });
            addRecordButton.setText(Localizer.localize("UI", "RecordsPanelAddNewButtonText"));
            panel1.add(addRecordButton,
                new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- editRecordButton ----
            editRecordButton.setFont(UIManager.getFont("Button.font"));
            editRecordButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/sdm/table_edit.png")));
            editRecordButton.setFocusPainted(false);
            editRecordButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    editRecordButtonActionPerformed(e);
                }
            });
            editRecordButton.setText(Localizer.localize("UI", "RecordsPanelEditButtonText"));
            panel1.add(editRecordButton,
                new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- importButton ----
            importButton.setFont(UIManager.getFont("Button.font"));
            importButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/sdm/table_row_insert.png")));
            importButton.setFocusPainted(false);
            importButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    importButtonActionPerformed(e);
                }
            });
            importButton.setText(Localizer.localize("UI", "RecordsPanelImportButtonText"));
            panel1.add(importButton,
                new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- fillButton ----
            fillButton.setFont(UIManager.getFont("Button.font"));
            fillButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/paintcan.png")));
            fillButton.setFocusPainted(false);
            fillButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    fillButtonActionPerformed(e);
                }
            });
            fillButton.setText(Localizer.localize("UI", "RecordsPanelFillButtonText"));
            panel1.add(fillButton,
                new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

            //---- deleteRecordButton ----
            deleteRecordButton.setFont(UIManager.getFont("Button.font"));
            deleteRecordButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/sdm/table_row_delete.png")));
            deleteRecordButton.setFocusPainted(false);
            deleteRecordButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    deleteRecordButtonActionPerformed(e);
                }
            });
            deleteRecordButton.setText(Localizer.localize("UI", "RecordsPanelDeleteButtonText"));
            panel1.add(deleteRecordButton,
                new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- exportRecordsButton ----
            exportRecordsButton.setFont(UIManager.getFont("Button.font"));
            exportRecordsButton.setFocusPainted(false);
            exportRecordsButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/sdm/table_save.png")));
            exportRecordsButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    exportRecordsButtonActionPerformed(e);
                }
            });
            exportRecordsButton
                .setText(Localizer.localize("UI", "RecordsPanelExportSelectionButtonText"));
            panel1.add(exportRecordsButton,
                new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- exportAllButton ----
            exportAllButton.setFont(UIManager.getFont("Button.font"));
            exportAllButton.setFocusPainted(false);
            exportAllButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/sdm/table_save.png")));
            exportAllButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    exportAllButtonActionPerformed(e);
                }
            });
            exportAllButton.setText(Localizer.localize("UI", "RecordsPanelExportAllButtonText"));
            panel1.add(exportAllButton,
                new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel tableNameLabel;
    private JTextField tableNameTextField;
    private JLabel actionsLabel;
    private JButton recordsRefreshButton;
    private JButton addRecordButton;
    private JButton editRecordButton;
    private JButton importButton;
    private JButton fillButton;
    private JButton deleteRecordButton;
    private JButton exportRecordsButton;
    private JButton exportAllButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    @Override public void removeListeners() {
        // TODO Auto-generated method stub

    }

    @Override public void updatePanel() {
        String tableName = sourceDataManagerFrame.getSelectedTableName();
        tableNameTextField.setText(tableName);
        tableNameTextField.setToolTipText(tableName);
    }

}
