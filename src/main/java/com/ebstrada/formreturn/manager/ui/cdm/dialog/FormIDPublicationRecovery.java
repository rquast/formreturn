package com.ebstrada.formreturn.manager.ui.cdm.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.model.filter.SearchFilter;
import com.ebstrada.formreturn.manager.persistence.viewer.GenericDataViewer;
import com.ebstrada.formreturn.manager.ui.cdm.model.FormPageDataModel;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.util.Misc;

@SuppressWarnings("serial") public class FormIDPublicationRecovery extends JDialog
    implements GenericDataViewer {

    private FormPageDataModel formPageDataModel;
    private long publicationId;
    private int dialogResult = JOptionPane.CANCEL_OPTION;
    private int firstFormPageId;

    public FormIDPublicationRecovery(Frame owner) {
        super(owner);
        initComponents();
    }

    public FormIDPublicationRecovery(Dialog owner) {
        super(owner);
        initComponents();
    }

    public void restore() {

        formPageDataModel = new FormPageDataModel();
        formPagesFilterPanel.setTableModel(formPageDataModel);
        formPagesFilterPanel.setTableViewer(this);

        // set publication id filter
        Vector<SearchFilter> searchFilters = formPageDataModel.getSearchFilters();
        for (SearchFilter searchFilter : searchFilters) {
            if (searchFilter.getNativeSearchFieldName().equalsIgnoreCase("FORM.PUBLICATION_ID")) {
                searchFilter.setSearchLong(publicationId);
                searchFilter.setEnabled(true);
            }
        }
        formPageDataModel.setSearchActivated(true);

        SelectionListener formPageListener =
            new SelectionListener(formPagesTable, SelectionListener.FORM_PAGE_SELECTION);
        formPagesTable.getSelectionModel().addListSelectionListener(formPageListener);

        refresh();

    }

    public void updateSelectedFormPage() {
        String value = (String) formPagesTable.getValueAt(formPagesTable.getSelectedRow(), 0);
        int formPageId = Misc.parseIntegerString(value);
        try {
            firstFormPageIDSpinner.setValue(new Integer(formPageId));
            this.firstFormPageId = formPageId;
        } catch (Exception ex) {
            Misc.printStackTrace(ex);
        }
    }

    public long getFirstFormPageID() {
        String value = (String) formPagesTable.getValueAt(formPagesTable.getSelectedRow(), 0);
        return Misc.parseLongString(value);
    }

    public void restoreFormPages() {
        formPagesTable.setModel(formPageDataModel.getTableModel());
        formPagesTable.getColumn("ID").setMaxWidth(150);
        formPagesTable.getTableHeader().setReorderingAllowed(false);
    }

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    private void closeWindow() {
        setDialogResult(JOptionPane.CANCEL_OPTION);
        dispose();
    }

    private void thisWindowClosing(WindowEvent e) {
        closeWindow();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                cancelButton.requestFocusInWindow();
            }
        });
    }

    private void processButtonActionPerformed(ActionEvent e) {
        setDialogResult(JOptionPane.OK_OPTION);
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        closeWindow();
    }

    public class SelectionListener implements ListSelectionListener {

        public static final int PUBLICATION_SELECTION = 0;
        public static final int FORM_SELECTION = 1;
        public static final int FORM_PAGE_SELECTION = 2;

        private int selection;

        private JTable table;

        // It is necessary to keep the table since it is not possible
        // to determine the table from the event's source
        SelectionListener(JTable table, int selection) {
            this.table = table;
            this.selection = selection;
        }

        public void valueChanged(ListSelectionEvent e) {
            // If cell selection is enabled, both row and column change events are fired
            if (e.getSource() == table.getSelectionModel() && table.getRowSelectionAllowed()
                && e.getValueIsAdjusting() == false) {
                // Column selection changed
                int first = e.getFirstIndex();
                int last = e.getLastIndex();
                if (selection == FORM_PAGE_SELECTION) {
                    updateSelectedFormPage();
                }

            } else if (e.getSource() == table.getColumnModel().getSelectionModel() && table
                .getColumnSelectionAllowed()) {
                // Row selection changed
                int first = e.getFirstIndex();
                int last = e.getLastIndex();
            }

            if (e.getValueIsAdjusting()) {
                // The mouse button has not yet been released
            }
        }
    }

    private void initComponents() {

        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        contentPanel = new JPanel();
        formIDRecoveryDescriptionPanel = new JPanel();
        formIDRecoveryDescriptionLabel = new JLabel();
        formIDRecoveryDescriptionHelpLabel = new JHelpLabel();
        firstFormPageRecordPanel = new JPanel();
        firstFormPageRecordDescriptionLabel = new JLabel();
        formPageRecordsScrollPane = new JScrollPane();
        formPagesTable = new JTable();
        formPagesFilterPanel = new TableFilterPanel();
        firstFormPageIDPanel = new JPanel();
        firstFormPageIDLabel = new JLabel();
        firstFormPageIDSpinner = new JSpinner();
        buttonPanel = new JPanel();
        processButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                thisWindowClosing(e);
            }
        });
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 0, 0};
        ((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};
        setTitle(Localizer.localize("UICDM", "FormIDPublicationRecoveryDialogTitle"));

        //======== contentPanel ========
        {
            contentPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

            contentPanel.setLayout(new GridBagLayout());
            ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {45, 0, 0};
            ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

            //======== formIDRecoveryDescriptionPanel ========
            {
                formIDRecoveryDescriptionPanel.setBorder(new MatteBorder(0, 0, 3, 0, Color.gray));
                formIDRecoveryDescriptionPanel.setOpaque(false);
                formIDRecoveryDescriptionPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)formIDRecoveryDescriptionPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                ((GridBagLayout)formIDRecoveryDescriptionPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)formIDRecoveryDescriptionPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                ((GridBagLayout)formIDRecoveryDescriptionPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //---- formIDRecoveryDescriptionLabel ----
                formIDRecoveryDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                formIDRecoveryDescriptionLabel.setText("<html><body><strong>" + Localizer.localize("UICDM", "FormIDRecoveryDescriptionLabel") + "</strong></body></html>");
                formIDRecoveryDescriptionPanel.add(formIDRecoveryDescriptionLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- formIDRecoveryDescriptionHelpLabel ----
                formIDRecoveryDescriptionHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                formIDRecoveryDescriptionHelpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                formIDRecoveryDescriptionHelpLabel.setFont(UIManager.getFont("Label.font"));
                formIDRecoveryDescriptionHelpLabel.setHelpGUID("form-id-publication-recovery");
                formIDRecoveryDescriptionHelpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                formIDRecoveryDescriptionPanel.add(formIDRecoveryDescriptionHelpLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 5), 0, 0));
            }
            contentPanel.add(formIDRecoveryDescriptionPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

            //======== firstFormPageRecordPanel ========
            {
                firstFormPageRecordPanel.setOpaque(false);
                firstFormPageRecordPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)firstFormPageRecordPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)firstFormPageRecordPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
                ((GridBagLayout)firstFormPageRecordPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)firstFormPageRecordPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 0.0, 1.0E-4};
                firstFormPageRecordPanel.setBorder(new CompoundBorder(
                    new TitledBorder(Localizer.localize("UICDM", "FirstFormPageRecordPanel")),
                    new EmptyBorder(5, 5, 5, 5)));

                //---- firstFormPageRecordDescriptionLabel ----
                firstFormPageRecordDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                firstFormPageRecordDescriptionLabel.setText(Localizer.localize("UICDM", "FirstFormPageRecordDescriptionLabel"));
                firstFormPageRecordPanel.add(firstFormPageRecordDescriptionLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== formPageRecordsScrollPane ========
                {
                    formPageRecordsScrollPane.setFont(UIManager.getFont("ScrollPane.font"));

                    //---- formPagesTable ----
                    formPagesTable.setFont(UIManager.getFont("Table.font"));
                    formPagesTable.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));
                    formPageRecordsScrollPane.setViewportView(formPagesTable);
                }
                firstFormPageRecordPanel.add(formPageRecordsScrollPane, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));
                firstFormPageRecordPanel.add(formPagesFilterPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== firstFormPageIDPanel ========
                {
                    firstFormPageIDPanel.setOpaque(false);
                    firstFormPageIDPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)firstFormPageIDPanel.getLayout()).columnWidths = new int[] {0, 0, 125, 0, 0};
                    ((GridBagLayout)firstFormPageIDPanel.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)firstFormPageIDPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                    ((GridBagLayout)firstFormPageIDPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                    //---- firstFormPageIDLabel ----
                    firstFormPageIDLabel.setFont(UIManager.getFont("Label.font"));
                    firstFormPageIDLabel.setText(Localizer.localize("UICDM", "FirstFormPageIDLabel"));
                    firstFormPageIDPanel.add(firstFormPageIDLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- firstFormPageIDSpinner ----
                    firstFormPageIDSpinner.setModel(new SpinnerNumberModel(1, 1, null, 1));
                    firstFormPageIDSpinner.setFont(UIManager.getFont("Spinner.font"));
                    firstFormPageIDPanel.add(firstFormPageIDSpinner, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));
                }
                firstFormPageRecordPanel.add(firstFormPageIDPanel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            contentPanel.add(firstFormPageRecordPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(contentPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

        //======== buttonPanel ========
        {
            buttonPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
            buttonPanel.setOpaque(false);
            buttonPanel.setLayout(new GridBagLayout());
            ((GridBagLayout)buttonPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
            ((GridBagLayout)buttonPanel.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout)buttonPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout)buttonPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- processButton ----
            processButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/accept.png")));
            processButton.setFont(UIManager.getFont("Button.font"));
            processButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    processButtonActionPerformed(e);
                }
            });
            processButton.setText(Localizer.localize("UICDM", "ProcessButtonText"));
            buttonPanel.add(processButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));

            //---- cancelButton ----
            cancelButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
            cancelButton.setFont(UIManager.getFont("Button.font"));
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cancelButtonActionPerformed(e);
                }
            });
            cancelButton.setText(Localizer.localize("UICDM", "CancelButtonText"));
            buttonPanel.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(buttonPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        setSize(850, 650);
        setLocationRelativeTo(getOwner());
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel contentPanel;
    private JPanel formIDRecoveryDescriptionPanel;
    private JLabel formIDRecoveryDescriptionLabel;
    private JHelpLabel formIDRecoveryDescriptionHelpLabel;
    private JPanel firstFormPageRecordPanel;
    private JLabel firstFormPageRecordDescriptionLabel;
    private JScrollPane formPageRecordsScrollPane;
    private JTable formPagesTable;
    private TableFilterPanel formPagesFilterPanel;
    private JPanel firstFormPageIDPanel;
    private JLabel firstFormPageIDLabel;
    private JSpinner firstFormPageIDSpinner;
    private JPanel buttonPanel;
    private JButton processButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration //GEN-END:variables

    public void refresh() {
        refresh(true);
    }

    public void refresh(boolean updatePageNumbers) {
        if (formPageDataModel != null) {
            restoreFormPages();
            if (updatePageNumbers) {
                formPagesFilterPanel.updatePageNumbers();
            }
        }
    }

    public void refresh(boolean updatePageNumbers, TableFilterPanel tableFilterPanel) {
        restoreFormPages();
        if (updatePageNumbers) {
            formPagesFilterPanel.updatePageNumbers();
        }
    }

    public void setPublicationId(long publicationId) {
        this.publicationId = publicationId;
        restore();
    }

}
