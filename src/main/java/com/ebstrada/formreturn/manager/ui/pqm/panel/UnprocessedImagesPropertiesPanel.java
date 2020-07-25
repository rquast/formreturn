package com.ebstrada.formreturn.manager.ui.pqm.panel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.pqm.ProcessingQueueManagerFrame;
import org.jdesktop.swingx.*;

public class UnprocessedImagesPropertiesPanel extends PQMPanel {

    private static final long serialVersionUID = 1L;

    private ProcessingQueueManagerFrame processingQueueManagerFrame;

    public UnprocessedImagesPropertiesPanel(
        ProcessingQueueManagerFrame processingQueueManagerFrame) {
        initComponents();
        this.processingQueueManagerFrame = processingQueueManagerFrame;
    }

    @Override public void removeListeners() {
        // TODO Auto-generated method stub

    }

    @Override public void updatePanel() {
    }

    private void refreshUnprocessedImagesButtonActionPerformed(ActionEvent e) {
        processingQueueManagerFrame.refreshUnprocessedImagesButtonActionPerformed(e);
    }

    private void uploadImageFolderButtonActionPerformed(ActionEvent e) {
        processingQueueManagerFrame.uploadImageFolderButtonActionPerformed(e);
    }

    private void deleteUnprocessedImagesButtonActionPerformed(ActionEvent e) {
        processingQueueManagerFrame.deleteUnprocessedImagesButtonActionPerformed(e);
    }

    private void uploadImagesButtonActionPerformed(ActionEvent e) {
        processingQueueManagerFrame.uploadImagesButtonActionPerformed(e);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        actionsLabel = new JLabel();
        refreshUnprocessedImagesButton = new JButton();
        uploadImagesButton = new JButton();
        uploadImageFolderButton = new JButton();
        deleteUnprocessedImagesButton = new JButton();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setOpaque(false);
        setScrollOnExpand(true);
        setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "UnprocessedImagesPanelTitle"));

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
            actionsLabel.setText(Localizer.localize("UI", "UnprocessedImagesPanelActionsLabel"));
            panel1.add(actionsLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- refreshUnprocessedImagesButton ----
            refreshUnprocessedImagesButton.setFont(UIManager.getFont("Button.font"));
            refreshUnprocessedImagesButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/sdm/database_refresh.png")));
            refreshUnprocessedImagesButton.setFocusPainted(false);
            refreshUnprocessedImagesButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    refreshUnprocessedImagesButtonActionPerformed(e);
                }
            });
            refreshUnprocessedImagesButton
                .setText(Localizer.localize("UI", "UnprocessedImagesPanelRefreshButtonText"));
            panel1.add(refreshUnprocessedImagesButton,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- uploadImagesButton ----
            uploadImagesButton.setFont(UIManager.getFont("Button.font"));
            uploadImagesButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/preview/image.png")));
            uploadImagesButton.setFocusPainted(false);
            uploadImagesButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    uploadImagesButtonActionPerformed(e);
                }
            });
            uploadImagesButton
                .setText(Localizer.localize("UI", "UnprocessedImagesPanelUploadImageButtonText"));
            panel1.add(uploadImagesButton,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- uploadImageFolderButton ----
            uploadImageFolderButton.setFont(UIManager.getFont("Button.font"));
            uploadImageFolderButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/folder_image.png")));
            uploadImageFolderButton.setFocusPainted(false);
            uploadImageFolderButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    uploadImageFolderButtonActionPerformed(e);
                }
            });
            uploadImageFolderButton
                .setText(Localizer.localize("UI", "UnprocessedImagesPanelUploadFolderButtonText"));
            panel1.add(uploadImageFolderButton,
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- deleteUnprocessedImagesButton ----
            deleteUnprocessedImagesButton.setFont(UIManager.getFont("Button.font"));
            deleteUnprocessedImagesButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/sdm/table_delete.png")));
            deleteUnprocessedImagesButton.setFocusPainted(false);
            deleteUnprocessedImagesButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    deleteUnprocessedImagesButtonActionPerformed(e);
                }
            });
            deleteUnprocessedImagesButton
                .setText(Localizer.localize("UI", "UnprocessedImagesPanelDeleteButtonText"));
            panel1.add(deleteUnprocessedImagesButton,
                new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel actionsLabel;
    private JButton refreshUnprocessedImagesButton;
    private JButton uploadImagesButton;
    private JButton uploadImageFolderButton;
    private JButton deleteUnprocessedImagesButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
