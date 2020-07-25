package com.ebstrada.formreturn.manager.ui.sdm.dialog;

import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.jpa.RecordController;
import com.ebstrada.formreturn.manager.logic.jpa.SourceTextController;
import com.ebstrada.formreturn.manager.persistence.jpa.DataSet;
import com.ebstrada.formreturn.manager.persistence.jpa.Record;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceField;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceImage;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceText;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.sdm.SourceDataManagerFrame;

public class RecordFieldDataDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private SourceDataManagerFrame sourceDataManagerFrame;

    private long recordId = -1;

    private Map<Long, String> sts;

    private Map<String, Long> sourceFieldNameToIdMap;

    public RecordFieldDataDialog(Frame owner, SourceDataManagerFrame sourceDataManagerFrame,
        boolean isNewRecord) {
        super(owner);
        initComponents();

        EditorTextField textField = new EditorTextField();
        textField.addCaretListener(new MarkValuesCaretListener());
        textField.setBorder(null);
        fieldDataTable.setDefaultEditor(Object.class, new DefaultCellEditor(textField));

        this.sourceDataManagerFrame = sourceDataManagerFrame;
        restoreRecord(isNewRecord);

        if (isNewRecord) {
            setTitle(Localizer.localize("UI", "RecordEditorAddNewRecordDialogTitle"));
        } else {
            setTitle(String.format(Localizer.localize("UI", "RecordEditorEditRecordDialogTitle"),
                recordId + ""));
        }

    }

    public class EditorTextField extends JTextField {

        private static final long serialVersionUID = 1L;

        private boolean appendFirstKey;
        private boolean firstHandled;

        @Override public void addNotify() {
            super.addNotify();
            selectAll();
            firstHandled = false;
        }

        public void setAppendFirstKey(boolean appendFirst) {
            this.appendFirstKey = appendFirst;
        }

        @Override protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition,
            boolean pressed) {
            checkSelection(e);
            return super.processKeyBinding(ks, e, condition, pressed);
        }

        private void checkSelection(KeyEvent e) {
            if (!appendFirstKey || firstHandled)
                return;
            firstHandled = true;
            if ((e == null) || (e.getSource() != this)) {
                clearSelection();
            }
        }

        private void clearSelection() {
            Document doc = getDocument();
            select(doc.getLength(), doc.getLength());
        }

    }


    private class MarkValuesCaretListener implements CaretListener {
        public void caretUpdate(CaretEvent e) {
            JTextField source = (JTextField) e.getSource();
            int column = fieldDataTable.getSelectedColumn();
            int row = fieldDataTable.getSelectedRow();
            String newText = source.getText();
            String oldText = (String) fieldDataTable.getValueAt(row, column);
            if (oldText == null || !(oldText.equals(newText))) {
                fieldDataTable.setValueAt(newText, row, column);
            }
        }
    }

    private void restoreRecord(boolean isNewRecord) {

        if (!isNewRecord) {
            recordId = sourceDataManagerFrame.getSelectedRecordIds()[0];
        }

        DefaultTableModel dtm = new DefaultTableModel() {

            private static final long serialVersionUID = 1L;

            Class[] columnTypes = new Class[] {String.class, String.class};
            boolean[] columnEditable = new boolean[] {false, true};

            @Override public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnEditable[columnIndex];
            }
        };
        dtm.addColumn(Localizer.localize("UI", "RecordEditorFieldNameColumnName"));
        dtm.addColumn(Localizer.localize("UI", "RecordEditorValueColumnName"));

        sts = new HashMap<Long, String>();

        // restore record data
        if (!isNewRecord) {
            EntityManager entityManager =
                Main.getInstance().getJPAConfiguration().getEntityManager();
            try {
                Record record = entityManager.find(Record.class, recordId);
                if (record == null) {
                    return;
                }
                if (record.getSourceTextCollection() != null) {
                    for (SourceText st : record.getSourceTextCollection()) {
                        sts.put(st.getSourceFieldId().getSourceFieldId(), st.getSourceTextString());
                    }
                }
            } catch (Exception ex) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                return;
            } finally {
                entityManager.close();
            }
        }

        List<SourceField> sourceFields =
            sourceDataManagerFrame.getFieldDataModel().getSourceFields();
        if (sourceFields == null) {
            return;
        }
        Iterator<SourceField> sfi = sourceFields.iterator();

        sourceFieldNameToIdMap = new HashMap<String, Long>();

        while (sfi.hasNext()) {
            SourceField sf = sfi.next();
            String stringValue = sts.get(sf.getSourceFieldId());
            if (stringValue == null) {
                stringValue = "";
            }
            sourceFieldNameToIdMap.put(sf.getSourceFieldName(), sf.getSourceFieldId());
            dtm.addRow(new String[] {sf.getSourceFieldName(), stringValue});
        }

        fieldDataTable.setModel(dtm);
        fieldDataTable.getTableHeader().setReorderingAllowed(false);

    }

    public RecordFieldDataDialog(Dialog owner) {
        super(owner);
        initComponents();
    }

    private void saveButtonActionPerformed(ActionEvent e) {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        // if record is new, save it
        Record record = null;

        try {

            if (recordId > 0) {
                record = entityManager.find(Record.class, recordId);
            } else {
                record = new Record();
            }

            if (record.getDataSetId() == null) {
                DataSet ds = entityManager
                    .find(DataSet.class, sourceDataManagerFrame.getSelectedDataSetId());
                record.setDataSetId(ds);
                record.setRecordCreated(new Timestamp(System.currentTimeMillis()));
                record.setRecordModified(new Timestamp(System.currentTimeMillis()));
                entityManager.getTransaction().begin();
                entityManager.flush();
                entityManager.persist(record);
                entityManager.getTransaction().commit();
            }

            int rowCount = fieldDataTable.getRowCount();
            SourceTextController stc = new SourceTextController();

            Vector<Integer> rowIndexVector = new Vector<Integer>();
            for (int i = 0; i < rowCount; i++) {
                rowIndexVector.add(i);
            }

            List<SourceText> stcol = record.getSourceTextCollection();
            if (stcol != null) {
                for (SourceText st : stcol) {
                    for (int i = 0; i < rowCount; i++) {
                        String fieldName = (String) fieldDataTable.getValueAt(i, 0);
                        String fieldValue = (String) fieldDataTable.getValueAt(i, 1);
                        if (st.getSourceFieldId().getSourceFieldName().trim()
                            .equals(fieldName.trim())) {
                            if (st.getSourceTextString() != fieldValue) {
                                stc.updateSourceTextStringValue(st.getSourceTextId(), fieldValue);
                                rowIndexVector.remove(new Integer(i));
                            }
                        }
                    }
                }
            }

            // go through the remaining fields and create new sourcetext records if
            // string length greater than 0
            Iterator<Integer> rivi = rowIndexVector.iterator();
            while (rivi.hasNext()) {
                int rowIndex = rivi.next();
                String fieldName = (String) fieldDataTable.getValueAt(rowIndex, 0);
                String fieldValue = (String) fieldDataTable.getValueAt(rowIndex, 1);
                if (fieldValue.length() > 0) {
                    long sourceFieldId = sourceFieldNameToIdMap.get(fieldName);
                    stc.createSourceTextStringValue(sourceDataManagerFrame.getSelectedDataSetId(),
                        record.getRecordId(), sourceFieldId, fieldValue);
                }
            }

        } catch (Exception ex) {
            if (entityManager.getTransaction().isActive()) {
                try {
                    entityManager.getTransaction().rollback();
                } catch (Exception rbex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(rbex);
                }
            }
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return;
        } finally {
            entityManager.close();
        }

        sourceDataManagerFrame.restoreRecords();

        dispose();

    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        fieldDataTable = new JTable();
        valueEditorInstructionsLabel = new JLabel();
        buttonBar = new JPanel();
        saveButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setOpaque(false);
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights =
                    new double[] {1.0, 0.0, 1.0E-4};

                //======== scrollPane1 ========
                {

                    //---- fieldDataTable ----
                    fieldDataTable.setCellSelectionEnabled(true);
                    fieldDataTable.setFont(UIManager.getFont("Table.font"));
                    fieldDataTable.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));
                    scrollPane1.setViewportView(fieldDataTable);
                }
                contentPanel.add(scrollPane1,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                //---- valueEditorInstructionsLabel ----
                valueEditorInstructionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                valueEditorInstructionsLabel.setFont(UIManager.getFont("Label.font"));
                valueEditorInstructionsLabel
                    .setText(Localizer.localize("UI", "RecordEditorValueEditorInstructionsLabel"));
                contentPanel.add(valueEditorInstructionsLabel,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 0.0};

                //---- saveButton ----
                saveButton.setFocusPainted(false);
                saveButton.setFont(UIManager.getFont("Button.font"));
                saveButton.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
                saveButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        saveButtonActionPerformed(e);
                    }
                });
                saveButton.setText(Localizer.localize("UI", "SaveButtonText"));
                buttonBar.add(saveButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setFocusPainted(false);
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
                cancelButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
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
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JScrollPane scrollPane1;
    private JTable fieldDataTable;
    private JLabel valueEditorInstructionsLabel;
    private JPanel buttonBar;
    private JButton saveButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
