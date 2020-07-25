package com.ebstrada.formreturn.manager.ui.editor.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.*;

public class PreviewDataFieldsDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private JGraph graph;

    private int dialogResult;

    private Map<String, String> recordMap;

    public PreviewDataFieldsDialog(Frame owner, JGraph graph) {
        super(owner);
        initComponents();
        this.graph = graph;
        repopulate();
    }

    public PreviewDataFieldsDialog(Dialog owner, JGraph graph) {
        super(owner);
        initComponents();
        this.graph = graph;
        repopulate();
    }

    private void repopulate() {

        DefaultTableModel tableModel = new DefaultTableModel();

        tableModel.addColumn(Localizer.localize("UI", "PreviewDataFieldsFieldNameColumnName"));
        tableModel.addColumn(Localizer.localize("UI", "PreviewDataFieldsValueColumnName"));

        previewDataFieldsTable.removeAll();
        previewDataFieldsTable.setModel(tableModel);

        recordMap = graph.getRecordMap();

        if (recordMap != null) {
            Iterator<String> fieldNamesIterator = recordMap.keySet().iterator();
            while (fieldNamesIterator.hasNext()) {
                String fieldName = fieldNamesIterator.next();
                tableModel.addRow(new String[] {fieldName, recordMap.get(fieldName)});
            }
            previewDataFieldsTable.setModel(tableModel);
        } else {
            recordMap = new HashMap<String, String>();
        }

        previewDataFieldsTable.getTableHeader().setReorderingAllowed(false);

    }

    private void setRecordMap() {
        recordMap = new HashMap<String, String>();
        for (int i = 0; i < previewDataFieldsTable.getRowCount(); i++) {
            recordMap.put(previewDataFieldsTable.getValueAt(i, 0) + "",
                previewDataFieldsTable.getValueAt(i, 1) + "");
        }
    }

    public int getDialogResult() {
        return dialogResult;
    }

    private void saveButtonActionPerformed(ActionEvent e) {
        setRecordMap();
        graph.setRecordMap(recordMap);
        setDialogResult(JOptionPane.OK_OPTION);
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        setDialogResult(JOptionPane.CANCEL_OPTION);
        dispose();
    }

    private void addNewFieldButtonActionPerformed(ActionEvent e) {
        AddNewPreviewFieldDialog anpfd =
            new AddNewPreviewFieldDialog(Main.getInstance(), getSelectedFieldName(),
                getSelectedFieldValue());
        anpfd.setModal(true);
        anpfd.setVisible(true);

        if (anpfd.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {
            recordMap.put(anpfd.getFieldName(), anpfd.getFieldValue());
            repopulate();
        }
    }

    private String getSelectedFieldValue() {
        int selectedRow = previewDataFieldsTable.getSelectedRow();
        if (selectedRow != -1) {
            return previewDataFieldsTable.getValueAt(selectedRow, 1) + "";
        } else {
            return "";
        }
    }

    private String getSelectedFieldName() {
        int selectedRow = previewDataFieldsTable.getSelectedRow();
        if (selectedRow != -1) {
            return previewDataFieldsTable.getValueAt(selectedRow, 0) + "";
        } else {
            return "";
        }
    }

    private void removeFieldButtonActionPerformed(ActionEvent e) {
        int selectedRow = previewDataFieldsTable.getSelectedRow();
        if (selectedRow != -1) {
            recordMap.remove(getSelectedFieldName());
            repopulate();
        }
    }

    public JGraph getGraph() {
        return graph;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        previewDataFieldsScrollPane = new JScrollPane();
        previewDataFieldsTable = new JTable();
        buttonBar = new JPanel();
        helpLabel = new JHelpLabel();
        addNewFieldButton = new JButton();
        removeFieldButton = new JButton();
        saveButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "PreviewDataFieldsDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //======== previewDataFieldsScrollPane ========
                {

                    //---- previewDataFieldsTable ----
                    previewDataFieldsTable.setModel(new DefaultTableModel(
                        new Object[][] {
                            {null, null},
                            {null, null},
                        },
                        new String[] {
                            "Field Name", "Value"
                        }
                    ));
                    previewDataFieldsTable.setFont(UIManager.getFont("Table.font"));
                    previewDataFieldsTable.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));
                    previewDataFieldsScrollPane.setViewportView(previewDataFieldsTable);
                }
                contentPanel.add(previewDataFieldsScrollPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0, 0.0};

                //---- helpLabel ----
                helpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                helpLabel.setHelpGUID("preview-data-fields");
                helpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                buttonBar.add(helpLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- addNewFieldButton ----
                addNewFieldButton.setFocusPainted(false);
                addNewFieldButton.setFont(UIManager.getFont("Button.font"));
                addNewFieldButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        addNewFieldButtonActionPerformed(e);
                    }
                });
                addNewFieldButton.setText(Localizer.localize("UI", "PreviewDataFieldsAddNewFieldButtonText"));
                buttonBar.add(addNewFieldButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- removeFieldButton ----
                removeFieldButton.setFocusPainted(false);
                removeFieldButton.setFont(UIManager.getFont("Button.font"));
                removeFieldButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        removeFieldButtonActionPerformed(e);
                    }
                });
                removeFieldButton.setText(Localizer.localize("UI", "PreviewDataFieldsRemoveFieldButtonText"));
                buttonBar.add(removeFieldButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- saveButton ----
                saveButton.setFocusPainted(false);
                saveButton.setFont(UIManager.getFont("Button.font"));
                saveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveButtonActionPerformed(e);
                    }
                });
                saveButton.setText(Localizer.localize("UI", "SaveButtonText"));
                buttonBar.add(saveButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setFocusPainted(false);
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UI", "CancelButtonText"));
                buttonBar.add(cancelButton, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(635, 505);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JScrollPane previewDataFieldsScrollPane;
    private JTable previewDataFieldsTable;
    private JPanel buttonBar;
    private JHelpLabel helpLabel;
    private JButton addNewFieldButton;
    private JButton removeFieldButton;
    private JButton saveButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
