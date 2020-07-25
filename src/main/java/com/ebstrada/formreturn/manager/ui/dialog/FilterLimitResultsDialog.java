package com.ebstrada.formreturn.manager.ui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.model.AbstractDataModel;
import com.ebstrada.formreturn.manager.persistence.model.filter.OrderByFilter;
import com.ebstrada.formreturn.manager.util.Misc;

public class FilterLimitResultsDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private int dialogResult = JOptionPane.CANCEL_OPTION;

    private AbstractDataModel tableModel;

    public FilterLimitResultsDialog(Frame owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(applyLimitButton);
    }

    public FilterLimitResultsDialog(Dialog owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(applyLimitButton);
    }

    public AbstractDataModel getTableModel() {
        return tableModel;
    }

    public void restore() {
        this.limitComboBox.setSelectedItem(tableModel.getLimit() + "");
    }

    public void setTableModel(AbstractDataModel tableModel) {
        this.tableModel = tableModel;
        restore();
    }

    private void applyLimitButtonActionPerformed(ActionEvent e) {

        String limitString = (String) limitComboBox.getSelectedItem();
        if (testLimit(limitString)) {
            int parsedLimit = Misc.parseIntegerString(limitString);
            if (parsedLimit <= 0 || parsedLimit > 10000) {
                parsedLimit = 1000;
            }
            setLimit(parsedLimit);
            setOffset(0); // always reset the offset to 0 when setting the limit!
            setDialogResult(JOptionPane.OK_OPTION);
        }

        dispose();

    }

    private void defaultLimitButtonActionPerformed(ActionEvent e) {
        limitComboBox.getModel().setSelectedItem(tableModel.getDefaultLimit() + "");
    }

    public void setOrderByFilters(Vector<OrderByFilter> orderByFilters) {
        this.tableModel.setOrderByFilters(orderByFilters);
    }

    private boolean testLimit(String limitString) {
        int limit = Misc.parseIntegerString(limitString);
        if (limit <= 0 || limit > 10000) {
            limit = 1000;
        }
        if (limit > 0 && limit < 10000) {
            return true;
        } else {
            String message = Localizer.localize("UI", "SourceDataManagerLimitValueMessage");
            String caption = Localizer.localize("UI", "ErrorTitle");
            javax.swing.JOptionPane.showConfirmDialog(getRootPane(), message, caption,
                javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
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
                limitComboBox.requestFocusInWindow();
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
        limitAmountLabel = new JLabel();
        limitComboBox = new JComboBox();
        buttonBar = new JPanel();
        applyLimitButton = new JButton();
        defaultLimitButton = new JButton();
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
        setTitle(Localizer.localize("UI", "LimitResultsDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {0.0, 1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //---- limitAmountLabel ----
                limitAmountLabel.setFont(UIManager.getFont("Label.font"));
                limitAmountLabel.setText(Localizer.localize("UI", "LimitLabelText"));
                contentPanel.add(limitAmountLabel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                //---- limitComboBox ----
                limitComboBox.setEditable(true);
                limitComboBox.setModel(new DefaultComboBoxModel(
                    new String[] {"5", "10", "15", "20", "25", "40", "50", "75", "100", "125",
                        "150", "175", "200", "350", "400", "500", "1000"}));
                limitComboBox.setRequestFocusEnabled(false);
                limitComboBox.setSelectedIndex(15);
                limitComboBox.setFont(UIManager.getFont("ComboBox.font"));
                contentPanel.add(limitComboBox,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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

                //---- applyLimitButton ----
                applyLimitButton.setFont(UIManager.getFont("Button.font"));
                applyLimitButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        applyLimitButtonActionPerformed(e);
                    }
                });
                applyLimitButton.setText(Localizer.localize("UI", "ApplyButtonText"));
                buttonBar.add(applyLimitButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 5), 0, 0));

                //---- defaultLimitButton ----
                defaultLimitButton.setFont(UIManager.getFont("Button.font"));
                defaultLimitButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        defaultLimitButtonActionPerformed(e);
                    }
                });
                defaultLimitButton.setText(Localizer.localize("UI", "DefaultButtonText"));
                buttonBar.add(defaultLimitButton,
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
        setSize(300, 125);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel limitAmountLabel;
    private JComboBox limitComboBox;
    private JPanel buttonBar;
    private JButton applyLimitButton;
    private JButton defaultLimitButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
