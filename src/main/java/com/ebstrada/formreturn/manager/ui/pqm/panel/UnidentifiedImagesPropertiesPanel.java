package com.ebstrada.formreturn.manager.ui.pqm.panel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.pqm.ProcessingQueueManagerFrame;
import org.jdesktop.swingx.*;

public class UnidentifiedImagesPropertiesPanel extends PQMPanel {

    private static final long serialVersionUID = 1L;

    private ProcessingQueueManagerFrame processingQueueManagerFrame;

    public UnidentifiedImagesPropertiesPanel(
        ProcessingQueueManagerFrame processingQueueManagerFrame) {
        initComponents();
        this.processingQueueManagerFrame = processingQueueManagerFrame;
    }

    @Override public void removeListeners() {
        // TODO Auto-generated method stub

    }

    @Override public void updatePanel() {
    }

    private void refreshUnidentifiedImagesButtonActionPerformed(ActionEvent e) {
        processingQueueManagerFrame.refreshUnidentifiedImagesButtonActionPerformed(e);
    }

    private void deleteUnidentifiedImagesButtonActionPerformed(ActionEvent e) {
        processingQueueManagerFrame.deleteUnidentifiedImagesButtonActionPerformed(e);
    }

    private void reprocessButtonActionPerformed(ActionEvent e) {
        processingQueueManagerFrame.reprocessButtonActionPerformed(e);
    }

    private void exportUnidentifiedImagesButtonActionPerformed(ActionEvent e) {
        processingQueueManagerFrame.exportUnidentifiedImagesActionPerformed(e);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        actionsLabel = new JLabel();
        refreshUnidentifiedImagesButton = new JButton();
        deleteUnidentifiedImagesButton = new JButton();
        exportUnidentifiedImagesButton = new JButton();
        reprocessButton = new JButton();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setOpaque(false);
        setScrollOnExpand(true);
        setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "UnidentifiedImagesPanelTitle"));

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights =
                new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0E-4};

            //---- actionsLabel ----
            actionsLabel.setFont(UIManager.getFont("Label.font"));
            actionsLabel.setText(Localizer.localize("UI", "UnidentifiedImagesPanelActionsLabel"));
            panel1.add(actionsLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- refreshUnidentifiedImagesButton ----
            refreshUnidentifiedImagesButton.setFont(UIManager.getFont("Button.font"));
            refreshUnidentifiedImagesButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/sdm/database_refresh.png")));
            refreshUnidentifiedImagesButton.setFocusPainted(false);
            refreshUnidentifiedImagesButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    refreshUnidentifiedImagesButtonActionPerformed(e);
                }
            });
            refreshUnidentifiedImagesButton
                .setText(Localizer.localize("UI", "UnidentifiedImagesPanelRefreshButtonText"));
            panel1.add(refreshUnidentifiedImagesButton,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- deleteUnidentifiedImagesButton ----
            deleteUnidentifiedImagesButton.setFont(UIManager.getFont("Button.font"));
            deleteUnidentifiedImagesButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/sdm/table_delete.png")));
            deleteUnidentifiedImagesButton.setFocusPainted(false);
            deleteUnidentifiedImagesButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    deleteUnidentifiedImagesButtonActionPerformed(e);
                }
            });
            deleteUnidentifiedImagesButton
                .setText(Localizer.localize("UI", "UnidentifiedImagesPanelDeleteButtonText"));
            panel1.add(deleteUnidentifiedImagesButton,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- exportUnidentifiedImagesButton ----
            exportUnidentifiedImagesButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/folder_go.png")));
            exportUnidentifiedImagesButton.setFont(UIManager.getFont("Button.font"));
            exportUnidentifiedImagesButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    exportUnidentifiedImagesButtonActionPerformed(e);
                }
            });
            exportUnidentifiedImagesButton
                .setText(Localizer.localize("UI", "ExportUnidentifiedImagesButtonText"));
            panel1.add(exportUnidentifiedImagesButton,
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- reprocessButton ----
            reprocessButton.setFocusPainted(false);
            reprocessButton.setFont(UIManager.getFont("Button.font"));
            reprocessButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/preview/image.png")));
            reprocessButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    reprocessButtonActionPerformed(e);
                }
            });
            reprocessButton.setText(Localizer.localize("UI", "ReprocessButtonText"));
            panel1.add(reprocessButton,
                new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel actionsLabel;
    private JButton refreshUnidentifiedImagesButton;
    private JButton deleteUnidentifiedImagesButton;
    private JButton exportUnidentifiedImagesButton;
    private JButton reprocessButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
