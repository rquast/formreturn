package com.ebstrada.formreturn.manager.ui.cdm.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.export.filter.ExcludeEmptyRecordsFilter;
import com.ebstrada.formreturn.manager.logic.export.filter.Filter;

public class ExportFilterSettingsDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private int dialogResult = JOptionPane.CANCEL_OPTION;

    private ArrayList<Filter> filters;

    public ExportFilterSettingsDialog(Frame owner, ArrayList<Filter> filters) {
        super(owner);
        this.filters = filters;
        initComponents();
        restore();
    }

    public ExportFilterSettingsDialog(Dialog owner, ArrayList<Filter> filters) {
        super(owner);
        this.filters = filters;
        initComponents();
        restore();
    }

    public void restore() {
        getRootPane().setDefaultButton(saveButton);

        for (Filter filter : this.filters) {
            if (filter instanceof ExcludeEmptyRecordsFilter) {
                this.includeEmptyRecordsCheckBox.setSelected(false);
            }
        }
    }

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    private void saveButtonActionPerformed(ActionEvent e) {

        this.dialogResult = JOptionPane.OK_OPTION;

        this.filters = new ArrayList<Filter>();

        if (!(this.includeEmptyRecordsCheckBox.isSelected())) {
            filters.add(new ExcludeEmptyRecordsFilter());
        }

        dispose();

    }

    public ArrayList<Filter> getFilters() {
        return filters;
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                saveButton.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        generalFiltersPanel = new JPanel();
        includeEmptyRecordsCheckBox = new JCheckBox();
        buttonBar = new JPanel();
        saveButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UICDM", "ExportFilterSettingsDialogTitle"));

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
                    new double[] {0.0, 1.0, 1.0E-4};

                //======== generalFiltersPanel ========
                {
                    generalFiltersPanel.setOpaque(false);
                    generalFiltersPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) generalFiltersPanel.getLayout()).columnWidths =
                        new int[] {0, 0};
                    ((GridBagLayout) generalFiltersPanel.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout) generalFiltersPanel.getLayout()).columnWeights =
                        new double[] {0.0, 1.0E-4};
                    ((GridBagLayout) generalFiltersPanel.getLayout()).rowWeights =
                        new double[] {0.0, 1.0E-4};
                    generalFiltersPanel.setBorder(new CompoundBorder(
                        new TitledBorder(Localizer.localize("UICDM", "GeneralFiltersBorderTitle")),
                        new EmptyBorder(5, 5, 5, 5)));

                    //---- includeEmptyRecordsCheckBox ----
                    includeEmptyRecordsCheckBox.setOpaque(false);
                    includeEmptyRecordsCheckBox.setSelected(true);
                    includeEmptyRecordsCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                    includeEmptyRecordsCheckBox
                        .setText(Localizer.localize("UICDM", "IncludeEmptyRecordsCheckBox"));
                    generalFiltersPanel.add(includeEmptyRecordsCheckBox,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(generalFiltersPanel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
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
                saveButton.setText("Save");
                saveButton.setFont(UIManager.getFont("Button.font"));
                saveButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveButtonActionPerformed(e);
                    }
                });
                buttonBar.add(saveButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                buttonBar.add(cancelButton,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(610, 175);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel generalFiltersPanel;
    private JCheckBox includeEmptyRecordsCheckBox;
    private JPanel buttonBar;
    private JButton saveButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
