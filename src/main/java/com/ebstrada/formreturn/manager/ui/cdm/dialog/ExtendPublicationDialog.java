package com.ebstrada.formreturn.manager.ui.cdm.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.model.AbstractDataModel;
import com.ebstrada.formreturn.manager.persistence.model.filter.SearchFilter;
import com.ebstrada.formreturn.manager.persistence.viewer.GenericDataViewer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.cdm.CapturedDataManagerFrame;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.ui.sdm.model.RecordDataModel;
import com.ebstrada.formreturn.manager.util.Misc;

public class ExtendPublicationDialog extends JDialog implements GenericDataViewer {

    private static final long serialVersionUID = 1L;

    private ArrayList<Long> recordIds;

    private long publicationId;

    private long dataSetId;

    private RecordDataModel recordDataModel;

    private CapturedDataManagerFrame capturedDataManagerFrame;

    public ExtendPublicationDialog(Frame owner, CapturedDataManagerFrame capturedDataManagerFrame) {
        super(owner);
        initComponents();
        this.capturedDataManagerFrame = capturedDataManagerFrame;
        scrollPane1.getVerticalScrollBar().setUnitIncrement(30);
        scrollPane1.getVerticalScrollBar().setBlockIncrement(90);
        scrollPane1.getHorizontalScrollBar().setUnitIncrement(30);
        scrollPane1.getHorizontalScrollBar().setBlockIncrement(90);
        getRootPane().setDefaultButton(extendAllButton);

        recordDataModel = new RecordDataModel();

        recordsTableFilterPanel.setTableModel(recordDataModel);
        recordsTableFilterPanel.setTableViewer(this);
    }

    private void restore() {
        // restore records table
        recordsTable.setModel(recordDataModel.getTableModel());
        recordsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        recordsTable.getColumn("ID").setMaxWidth(150);
        recordsTable.getTableHeader().setReorderingAllowed(false);
        recordsTableFilterPanel.updatePageNumbers();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    public CapturedDataManagerFrame getCapturedDataManagerFrame() {
        return capturedDataManagerFrame;
    }

    public void setCapturedDataManagerFrame(CapturedDataManagerFrame capturedDataManagerFrame) {
        this.capturedDataManagerFrame = capturedDataManagerFrame;
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                extendAllButton.requestFocusInWindow();
            }
        });
    }

    public long getPublicationId() {
        return publicationId;
    }

    public long getDataSetId() {
        return dataSetId;
    }

    private void extendSelectionButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    capturedDataManagerFrame
                        .extendPublication(getPublicationId(), getSelectedIds());
                } catch (Exception e) {
                    Misc.printStackTrace(e);
                    Misc.showErrorMsg(Main.getInstance(), e.getLocalizedMessage());
                }
                close();
            }
        });
    }

    private void extendAllButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    capturedDataManagerFrame.extendPublication(getPublicationId(), getRecordIds());
                } catch (Exception e) {
                    Misc.printStackTrace(e);
                    Misc.showErrorMsg(Main.getInstance(), e.getLocalizedMessage());
                }
                close();
            }
        });
    }

    public void close() {
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        recordsTable = new JTable();
        recordsTableFilterPanel = new TableFilterPanel();
        buttonBar = new JPanel();
        extendSelectionButton = new JButton();
        extendAllButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UICDM", "ExtendPublicationDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights =
                    new double[] {1.0, 0.0, 1.0E-4};

                //======== scrollPane1 ========
                {
                    scrollPane1.setRequestFocusEnabled(false);

                    //---- recordsTable ----
                    recordsTable.setRequestFocusEnabled(false);
                    recordsTable.setShowHorizontalLines(false);
                    recordsTable.setShowVerticalLines(false);
                    recordsTable.setFont(UIManager.getFont("Table.font"));
                    recordsTable.setShowGrid(false);
                    recordsTable.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));
                    scrollPane1.setViewportView(recordsTable);
                }
                contentPanel.add(scrollPane1,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
                contentPanel.add(recordsTableFilterPanel,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 85, 0, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 0.0, 0.0};

                //---- extendSelectionButton ----
                extendSelectionButton.setFont(UIManager.getFont("Button.font"));
                extendSelectionButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        extendSelectionButtonActionPerformed(e);
                    }
                });
                extendSelectionButton
                    .setText(Localizer.localize("UICDM", "ExtendSelectionButtonText"));
                buttonBar.add(extendSelectionButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- extendAllButton ----
                extendAllButton.setFont(UIManager.getFont("Button.font"));
                extendAllButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        extendAllButtonActionPerformed(e);
                    }
                });
                extendAllButton.setText(Localizer.localize("UICDM", "ExtendAllButtonText"));
                buttonBar.add(extendAllButton,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UICDM", "CancelButtonText"));
                buttonBar.add(cancelButton,
                    new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(670, 480);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JScrollPane scrollPane1;
    private JTable recordsTable;
    private TableFilterPanel recordsTableFilterPanel;
    private JPanel buttonBar;
    private JButton extendSelectionButton;
    private JButton extendAllButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public void setRecordIds(ArrayList<Long> recordIds) {
        this.recordIds = recordIds;
    }

    public ArrayList<Long> getRecordIds() {
        return recordIds;
    }

    public void setPublicationId(long publicationId) {
        this.publicationId = publicationId;
    }

    @Override public void refresh() {
        refresh(true);
    }

    public ArrayList<Long> getSelectedIds() {

        int[] selectedRows = recordsTable.getSelectedRows();

        ArrayList<Long> selectedIds = new ArrayList<Long>();

        for (int i = 0; i < selectedRows.length; i++) {
            Long parsedValue = Misc.parseLongString((String) recordsTable.getValueAt(i, 0));
            selectedIds.add(parsedValue);
        }

        return selectedIds;

    }

    @Override public void refresh(boolean updatePageNumbers, TableFilterPanel tableFilterPanel) {
        if (recordDataModel != null) {
            restore();
            if (updatePageNumbers) {
                recordsTableFilterPanel.updatePageNumbers();
            }
        }
    }

    public void refresh(boolean updatePageNumbers) {
        if (recordDataModel != null) {
            restore();
            if (updatePageNumbers) {
                recordsTableFilterPanel.updatePageNumbers();
            }
        }
    }

    public void setDataSetId(long dataSetId) {
        this.dataSetId = dataSetId;
        recordDataModel.setParentId(dataSetId);
        Vector<SearchFilter> sf = recordDataModel.getSearchFilters();

        SearchFilter idFilter = new SearchFilter();
        idFilter.setEnabled(true);
        idFilter.setSearchType(AbstractDataModel.SEARCH_IN);
        idFilter.setFieldType(AbstractDataModel.FIELD_TYPE_LONG);
        idFilter.setNativeSearchFieldName("RECORD.RECORD_ID");
        idFilter.setSearchFieldName("record.recordId");
        idFilter.setSearchLongArr(getRecordIds());

        sf.add(idFilter);
    }

}
