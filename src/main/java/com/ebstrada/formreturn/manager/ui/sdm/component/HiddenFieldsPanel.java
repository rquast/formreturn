package com.ebstrada.formreturn.manager.ui.sdm.component;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class HiddenFieldsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public HiddenFieldsPanel() {
        initComponents();
    }

    public void restoreHiddenFieldsList() {
        List<String> hiddenFields = PreferencesManager.getHiddenFields();
        DefaultListModel dlm = new DefaultListModel();
        for (String hiddenField : hiddenFields) {
            dlm.addElement(hiddenField);
        }
        this.hiddenFieldsList.setModel(dlm);
    }

    private void hideFromViewButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String fieldName = hiddenFieldNameTextField.getText().trim();
                List<String> hiddenFields = PreferencesManager.getHiddenFields();
                if (!(hiddenFields.contains(fieldName))) {
                    hiddenFields.add(fieldName);
                    try {
                        PreferencesManager.savePreferences(Main.getXstream());
                    } catch (IOException ex) {
                        Misc.showExceptionMsg(getRootPane().getTopLevelAncestor(), ex);
                    }
                }
                restoreHiddenFieldsList();
            }
        });
    }

    private void removeFromViewButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String fieldName = ((String) hiddenFieldsList.getSelectedValue()).trim();
                List<String> hiddenFields = PreferencesManager.getHiddenFields();
                if (hiddenFields.contains(fieldName)) {
                    hiddenFields.remove(fieldName);
                    try {
                        PreferencesManager.savePreferences(Main.getXstream());
                    } catch (IOException ex) {
                        Misc.showExceptionMsg(getRootPane().getTopLevelAncestor(), ex);
                    }
                }
                restoreHiddenFieldsList();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        hideFieldNamePanel = new JPanel();
        fieldNameLabel = new JLabel();
        hiddenFieldNameTextField = new JTextField();
        hideFromViewButton = new JButton();
        hiddenFieldsListPanel = new JPanel();
        hiddenFieldsListScrollPane = new JScrollPane();
        hiddenFieldsList = new JList();
        hiddenFieldsListButtonPanel = new JPanel();
        removeFromViewButton = new JButton();

        //======== this ========
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {0, 0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

        //======== hideFieldNamePanel ========
        {
            hideFieldNamePanel.setOpaque(false);
            hideFieldNamePanel.setBorder(
                new CompoundBorder(new TitledBorder("Hide A Field Name From View In Captured Data"),
                    new EmptyBorder(5, 5, 5, 5)));
            hideFieldNamePanel.setLayout(new GridBagLayout());
            ((GridBagLayout) hideFieldNamePanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
            ((GridBagLayout) hideFieldNamePanel.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout) hideFieldNamePanel.getLayout()).columnWeights =
                new double[] {0.0, 1.0, 0.0, 1.0E-4};
            ((GridBagLayout) hideFieldNamePanel.getLayout()).rowWeights =
                new double[] {0.0, 1.0E-4};
            hideFieldNamePanel.setBorder(new CompoundBorder(
                new TitledBorder(Localizer.localize("UI", "HideFieldNameBorderTitle")),
                new EmptyBorder(5, 5, 5, 5)));

            //---- fieldNameLabel ----
            fieldNameLabel.setFont(UIManager.getFont("Label.font"));
            fieldNameLabel.setText(Localizer.localize("UI", "AddNewFieldFieldNameLabel"));
            hideFieldNamePanel.add(fieldNameLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- hiddenFieldNameTextField ----
            hiddenFieldNameTextField.setFont(UIManager.getFont("TextField.font"));
            hideFieldNamePanel.add(hiddenFieldNameTextField,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- hideFromViewButton ----
            hideFromViewButton.setFont(UIManager.getFont("Button.font"));
            hideFromViewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    hideFromViewButtonActionPerformed(e);
                }
            });
            hideFromViewButton.setText(Localizer.localize("UI", "HideFromViewButtonText"));
            hideFieldNamePanel.add(hideFromViewButton,
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(hideFieldNamePanel,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== hiddenFieldsListPanel ========
        {
            hiddenFieldsListPanel.setOpaque(false);
            hiddenFieldsListPanel.setBorder(
                new CompoundBorder(new TitledBorder("Hidden Fields In Captured Data View"),
                    new EmptyBorder(5, 5, 5, 5)));
            hiddenFieldsListPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) hiddenFieldsListPanel.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) hiddenFieldsListPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
            ((GridBagLayout) hiddenFieldsListPanel.getLayout()).columnWeights =
                new double[] {1.0, 1.0E-4};
            ((GridBagLayout) hiddenFieldsListPanel.getLayout()).rowWeights =
                new double[] {1.0, 0.0, 1.0E-4};
            hiddenFieldsListPanel.setBorder(new CompoundBorder(
                new TitledBorder(Localizer.localize("UI", "HiddenFieldsListBorderTitle")),
                new EmptyBorder(5, 5, 5, 5)));

            //======== hiddenFieldsListScrollPane ========
            {

                //---- hiddenFieldsList ----
                hiddenFieldsList.setFont(UIManager.getFont("List.font"));
                hiddenFieldsListScrollPane.setViewportView(hiddenFieldsList);
            }
            hiddenFieldsListPanel.add(hiddenFieldsListScrollPane,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

            //======== hiddenFieldsListButtonPanel ========
            {
                hiddenFieldsListButtonPanel.setOpaque(false);
                hiddenFieldsListButtonPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) hiddenFieldsListButtonPanel.getLayout()).columnWidths =
                    new int[] {0, 0, 0};
                ((GridBagLayout) hiddenFieldsListButtonPanel.getLayout()).rowHeights =
                    new int[] {0, 0};
                ((GridBagLayout) hiddenFieldsListButtonPanel.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 1.0E-4};
                ((GridBagLayout) hiddenFieldsListButtonPanel.getLayout()).rowWeights =
                    new double[] {0.0, 1.0E-4};

                //---- removeFromViewButton ----
                removeFromViewButton.setFont(UIManager.getFont("Button.font"));
                removeFromViewButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        removeFromViewButtonActionPerformed(e);
                    }
                });
                removeFromViewButton.setText(Localizer.localize("UI", "RemoveFromViewButtonText"));
                hiddenFieldsListButtonPanel.add(removeFromViewButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            hiddenFieldsListPanel.add(hiddenFieldsListButtonPanel,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(hiddenFieldsListPanel,
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel hideFieldNamePanel;
    private JLabel fieldNameLabel;
    private JTextField hiddenFieldNameTextField;
    private JButton hideFromViewButton;
    private JPanel hiddenFieldsListPanel;
    private JScrollPane hiddenFieldsListScrollPane;
    private JList hiddenFieldsList;
    private JPanel hiddenFieldsListButtonPanel;
    private JButton removeFromViewButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public void setFieldName(String fieldName) {
        this.hiddenFieldNameTextField.setText(fieldName);
    }
}
