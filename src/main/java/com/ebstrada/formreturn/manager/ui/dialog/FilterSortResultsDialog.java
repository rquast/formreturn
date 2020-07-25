package com.ebstrada.formreturn.manager.ui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.model.AbstractDataModel;
import com.ebstrada.formreturn.manager.persistence.model.filter.OrderByFilter;

public class FilterSortResultsDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private int dialogResult = JOptionPane.CANCEL_OPTION;

    private AbstractDataModel tableModel;

    public FilterSortResultsDialog(Frame owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(applyButton);
    }

    public FilterSortResultsDialog(Dialog owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(applyButton);
    }

    public void restore() {
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        dcbm.addElement(Localizer.localize("UI", "SortFilterActiveText"));
        dcbm.addElement(Localizer.localize("UI", "SortFilterInactiveText"));
        activatedComboBox.setModel(dcbm);
        Vector<OrderByFilter> orderByFilters = tableModel.getOrderByFilters();
        restore(orderByFilters);
    }

    public void restore(Vector<OrderByFilter> orderByFilters) {

        DefaultComboBoxModel fieldNameComboBoxModel = new DefaultComboBoxModel();

        for (int i = 0; i < orderByFilters.size(); i++) {
            OrderByFilter orderByFilter = orderByFilters.get(i);
            fieldNameComboBoxModel.addElement(orderByFilter.getName());
        }

        fieldNameComboBox.setModel(fieldNameComboBoxModel);

        // TODO: currently, we will only use one filter, not more - change in the future.
        for (int i = 0; i < orderByFilters.size(); i++) {
            OrderByFilter orderByFilter = orderByFilters.get(i);
            if (orderByFilter.isEnabled()) {
                fieldNameComboBox.setSelectedIndex(i);
                directionComboBox.setSelectedIndex(orderByFilter.getOrder());
            }
        }

        this.activatedComboBox.setSelectedIndex(this.tableModel.isOrderByActivated() ?
            AbstractDataModel.FILTER_ACTIVE :
            AbstractDataModel.FILTER_INNACTIVE);

    }

    public AbstractDataModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(AbstractDataModel tableModel) {
        this.tableModel = tableModel;
        restore();
    }

    private void applyButtonActionPerformed(ActionEvent e) {

        Vector<OrderByFilter> orderByFilters = tableModel.getOrderByFilters();

        for (int i = 0; i < orderByFilters.size(); i++) {
            OrderByFilter orderByFilter = orderByFilters.get(i);
            if (fieldNameComboBox.getSelectedIndex() == i) {
                orderByFilter.setOrder(directionComboBox.getSelectedIndex());
                orderByFilter.setEnabled(true);
            } else {
                orderByFilter.setEnabled(false);
            }
        }

        tableModel.setOrderByActivated(
            activatedComboBox.getSelectedIndex() == AbstractDataModel.FILTER_ACTIVE ? true : false);

        setDialogResult(JOptionPane.OK_OPTION);

        dispose();

    }

    private void defaultButtonActionPerformed(ActionEvent e) {
        Vector<OrderByFilter> orderByFilters = tableModel.getDefaultOrderByFilters();
        restore(orderByFilters);
    }

    public void setOrderByFilters(Vector<OrderByFilter> orderByFilters) {
        this.tableModel.setOrderByFilters(orderByFilters);
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    public long getLimit() {
        return this.tableModel.getLimit();
    }

    public void setLimit(long limit) {
        this.tableModel.setLimit(limit);
    }

    public long getOffset() {
        return this.tableModel.getOffset();
    }

    public void setOffset(long offset) {
        this.tableModel.setOffset(offset);
    }

    public Vector<OrderByFilter> getOrderByFilters() {
        return this.tableModel.getOrderByFilters();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fieldNameComboBox.requestFocusInWindow();
            }
        });
    }

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        activatedLabel = new JLabel();
        activatedComboBox = new JComboBox();
        fieldLabel = new JLabel();
        fieldNameComboBox = new JComboBox();
        sortDirectionLabel = new JLabel();
        directionComboBox = new JComboBox();
        buttonBar = new JPanel();
        applyButton = new JButton();
        defaultButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        setName("filterLimitResultsDialog");
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UI", "SortResultsDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {0.0, 1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights =
                    new double[] {1.0, 1.0, 1.0, 1.0E-4};

                //---- activatedLabel ----
                activatedLabel.setFont(UIManager.getFont("Label.font"));
                activatedLabel.setText(Localizer.localize("UI", "ActivatedLabelText"));
                contentPanel.add(activatedLabel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                        GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                //---- activatedComboBox ----
                activatedComboBox.setFont(UIManager.getFont("ComboBox.font"));
                contentPanel.add(activatedComboBox,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                //---- fieldLabel ----
                fieldLabel.setFont(UIManager.getFont("Label.font"));
                fieldLabel.setText(Localizer.localize("UI", "FieldLabelText"));
                contentPanel.add(fieldLabel,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                        GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                //---- fieldNameComboBox ----
                fieldNameComboBox.setFont(UIManager.getFont("ComboBox.font"));
                contentPanel.add(fieldNameComboBox,
                    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                //---- sortDirectionLabel ----
                sortDirectionLabel.setFont(UIManager.getFont("Label.font"));
                sortDirectionLabel.setText(Localizer.localize("UI", "SortDirectionLabelText"));
                contentPanel.add(sortDirectionLabel,
                    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                        GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

                //---- directionComboBox ----
                directionComboBox
                    .setModel(new DefaultComboBoxModel(new String[] {"Ascending", "Descending"}));
                directionComboBox.setFont(UIManager.getFont("ComboBox.font"));
                contentPanel.add(directionComboBox,
                    new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 85, 0, 85, 0};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights =
                    new double[] {1.0, 1.0, 1.0, 1.0, 1.0};

                //---- applyButton ----
                applyButton.setFont(UIManager.getFont("Button.font"));
                applyButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        applyButtonActionPerformed(e);
                    }
                });
                applyButton.setText(Localizer.localize("UI", "ApplyButtonText"));
                buttonBar.add(applyButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 5), 0, 0));

                //---- defaultButton ----
                defaultButton.setFont(UIManager.getFont("Button.font"));
                defaultButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        defaultButtonActionPerformed(e);
                    }
                });
                defaultButton.setText(Localizer.localize("UI", "DefaultButtonText"));
                buttonBar.add(defaultButton,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UI", "CancelButtonText"));
                buttonBar.add(cancelButton,
                    new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 5), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(300, 190);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel activatedLabel;
    private JComboBox activatedComboBox;
    private JLabel fieldLabel;
    private JComboBox fieldNameComboBox;
    private JLabel sortDirectionLabel;
    private JComboBox directionComboBox;
    private JPanel buttonBar;
    private JButton applyButton;
    private JButton defaultButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
