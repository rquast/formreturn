package com.ebstrada.formreturn.manager.ui.cdm.panel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.cdm.CapturedDataManagerFrame;
import org.jdesktop.swingx.*;

public class FormPagesPropertiesPanel extends CDMPanel {

    private static final long serialVersionUID = 1L;

    private CapturedDataManagerFrame capturedDataManagerFrame;

    public FormPagesPropertiesPanel(CapturedDataManagerFrame capturedDataManagerFrame) {
        initComponents();
        this.capturedDataManagerFrame = capturedDataManagerFrame;
    }

    @Override public void removeListeners() {
        // TODO Auto-generated method stub

    }

    @Override public void updatePanel() {
    }

    private void refreshFormPagesButtonActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                capturedDataManagerFrame.refreshFormPagesButtonActionPerformed(e);
            }
        });
    }

    private void clearDataButtonActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                capturedDataManagerFrame.clearFormPageCapturedDataButtonActionPerformed(e);
            }
        });
    }

    private void reprocessButtonActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                capturedDataManagerFrame.reprocessButtonActionPerformed(e);
            }
        });
    }

    private void previewButtonActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                capturedDataManagerFrame.previewButtonActionPerformed(e);
            }
        });
    }

    private void clearFilterButtonActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                capturedDataManagerFrame.clearFormPageFilterButtonActionPerformed(e);
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        formPagesPanel = new JPanel();
        parentFilterLabel = new JLabel();
        clearFilterButton = new JButton();
        actionsLabel = new JLabel();
        refreshFormPagesButton = new JButton();
        clearDataButton = new JButton();
        previewButton = new JButton();
        reprocessButton = new JButton();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setOpaque(false);
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UICDM", "FormPagesPanelTitle"));

        //======== formPagesPanel ========
        {
            formPagesPanel.setOpaque(false);
            formPagesPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) formPagesPanel.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) formPagesPanel.getLayout()).rowHeights =
                new int[] {0, 0, 0, 0, 0, 0, 0, 0};
            ((GridBagLayout) formPagesPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) formPagesPanel.getLayout()).rowWeights =
                new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0E-4};

            //---- parentFilterLabel ----
            parentFilterLabel.setFont(UIManager.getFont("Label.font"));
            parentFilterLabel.setText(Localizer.localize("UI", "ParentFilterLabelText"));
            formPagesPanel.add(parentFilterLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- clearFilterButton ----
            clearFilterButton.setFont(UIManager.getFont("Button.font"));
            clearFilterButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/clear_filter.png")));
            clearFilterButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    clearFilterButtonActionPerformed(e);
                }
            });
            clearFilterButton.setText(Localizer.localize("UI", "ClearFilterButtonText"));
            formPagesPanel.add(clearFilterButton,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- actionsLabel ----
            actionsLabel.setFont(UIManager.getFont("Label.font"));
            actionsLabel.setText(Localizer.localize("UICDM", "ActionsLabel"));
            formPagesPanel.add(actionsLabel,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- refreshFormPagesButton ----
            refreshFormPagesButton.setFont(UIManager.getFont("Button.font"));
            refreshFormPagesButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/sdm/database_refresh.png")));
            refreshFormPagesButton.setFocusPainted(false);
            refreshFormPagesButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    refreshFormPagesButtonActionPerformed(e);
                }
            });
            refreshFormPagesButton.setText(Localizer.localize("UICDM", "RefreshButtonText"));
            formPagesPanel.add(refreshFormPagesButton,
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
            formPagesPanel.add(clearDataButton,
                new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- previewButton ----
            previewButton.setFocusPainted(false);
            previewButton.setFont(UIManager.getFont("Button.font"));
            previewButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/preview/image.png")));
            previewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    previewButtonActionPerformed(e);
                }
            });
            previewButton.setText(Localizer.localize("UICDM", "PreviewButtonText"));
            formPagesPanel.add(previewButton,
                new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- reprocessButton ----
            reprocessButton.setFocusPainted(false);
            reprocessButton.setFont(UIManager.getFont("Button.font"));
            reprocessButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/preview/image.png")));
            reprocessButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    reprocessButtonActionPerformed(e);
                }
            });
            reprocessButton.setText(Localizer.localize("UICDM", "ReprocessButtonText"));
            formPagesPanel.add(reprocessButton,
                new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(formPagesPanel, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel formPagesPanel;
    private JLabel parentFilterLabel;
    private JButton clearFilterButton;
    private JLabel actionsLabel;
    private JButton refreshFormPagesButton;
    private JButton clearDataButton;
    private JButton previewButton;
    private JButton reprocessButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
