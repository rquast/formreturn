package com.ebstrada.formreturn.manager.ui.cdm.panel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.jdesktop.swingx.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.cdm.CapturedDataManagerFrame;

public class FormsPropertiesPanel extends CDMPanel {

    private static final long serialVersionUID = 1L;

    private CapturedDataManagerFrame capturedDataManagerFrame;

    public FormsPropertiesPanel(CapturedDataManagerFrame capturedDataManagerFrame) {
        initComponents();
        this.capturedDataManagerFrame = capturedDataManagerFrame;
    }

    @Override public void removeListeners() {
        // TODO Auto-generated method stub

    }

    @Override public void updatePanel() {
    }

    private void refreshFormsButtonActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                capturedDataManagerFrame.refreshFormsButtonActionPerformed(e);
            }
        });
    }

    private void deleteFormsButtonActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                capturedDataManagerFrame.deleteFormsButtonActionPerformed(e);
            }
        });
    }

    private void exportCapturedDataButtonActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                capturedDataManagerFrame.exportCapturedDataButtonActionPerformed(e);
            }
        });
    }

    private void clearDataButtonActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                capturedDataManagerFrame.clearFormCapturedDataButtonActionPerformed(e);
            }
        });
    }

    private void clearFilterButtonActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                capturedDataManagerFrame.clearFormFilterButtonActionPerformed(e);
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        formsPanel = new JPanel();
        parentFilterLabel = new JLabel();
        clearFilterButton = new JButton();
        actionsLabel = new JLabel();
        refreshFormsButton = new JButton();
        clearDataButton = new JButton();
        deleteFormsButton = new JButton();
        exportCapturedDataButton = new JButton();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setOpaque(false);
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UICDM", "FormsPanelTitle"));

        //======== formsPanel ========
        {
            formsPanel.setOpaque(false);
            formsPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) formsPanel.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) formsPanel.getLayout()).rowHeights =
                new int[] {0, 0, 0, 0, 0, 0, 0, 0};
            ((GridBagLayout) formsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) formsPanel.getLayout()).rowWeights =
                new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0E-4};

            //---- parentFilterLabel ----
            parentFilterLabel.setFont(UIManager.getFont("Label.font"));
            parentFilterLabel.setText(Localizer.localize("UI", "ParentFilterLabelText"));
            formsPanel.add(parentFilterLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- clearFilterButton ----
            clearFilterButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/clear_filter.png")));
            clearFilterButton.setFont(UIManager.getFont("Button.font"));
            clearFilterButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    clearFilterButtonActionPerformed(e);
                }
            });
            clearFilterButton.setText(Localizer.localize("UI", "ClearFilterButtonText"));
            formsPanel.add(clearFilterButton,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- actionsLabel ----
            actionsLabel.setFont(UIManager.getFont("Label.font"));
            actionsLabel.setText(Localizer.localize("UICDM", "ActionsLabel"));
            formsPanel.add(actionsLabel,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- refreshFormsButton ----
            refreshFormsButton.setFont(UIManager.getFont("Button.font"));
            refreshFormsButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/sdm/database_refresh.png")));
            refreshFormsButton.setFocusPainted(false);
            refreshFormsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    refreshFormsButtonActionPerformed(e);
                }
            });
            refreshFormsButton.setText(Localizer.localize("UICDM", "RefreshButtonText"));
            formsPanel.add(refreshFormsButton,
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- clearDataButton ----
            clearDataButton.setFont(UIManager.getFont("Button.font"));
            clearDataButton.setFocusPainted(false);
            clearDataButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/sdm/table_row_delete.png")));
            clearDataButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    clearDataButtonActionPerformed(e);
                }
            });
            clearDataButton.setText(Localizer.localize("UICDM", "ClearDataButtonText"));
            formsPanel.add(clearDataButton,
                new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- deleteFormsButton ----
            deleteFormsButton.setFont(UIManager.getFont("Button.font"));
            deleteFormsButton.setFocusPainted(false);
            deleteFormsButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/sdm/table_row_delete.png")));
            deleteFormsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    deleteFormsButtonActionPerformed(e);
                }
            });
            deleteFormsButton.setText(Localizer.localize("UICDM", "DeleteFormsButtonText"));
            formsPanel.add(deleteFormsButton,
                new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- exportCapturedDataButton ----
            exportCapturedDataButton.setFont(UIManager.getFont("Button.font"));
            exportCapturedDataButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/sdm/table_save.png")));
            exportCapturedDataButton.setFocusPainted(false);
            exportCapturedDataButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    exportCapturedDataButtonActionPerformed(e);
                }
            });
            exportCapturedDataButton
                .setText(Localizer.localize("UICDM", "ExportCapturedDataButtonText"));
            formsPanel.add(exportCapturedDataButton,
                new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(formsPanel, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel formsPanel;
    private JLabel parentFilterLabel;
    private JButton clearFilterButton;
    private JLabel actionsLabel;
    private JButton refreshFormsButton;
    private JButton clearDataButton;
    private JButton deleteFormsButton;
    private JButton exportCapturedDataButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
