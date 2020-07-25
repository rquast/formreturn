package com.ebstrada.formreturn.manager.ui.cdm.panel;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.jdesktop.swingx.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.cdm.CapturedDataManagerFrame;

public class PublicationsPropertiesPanel extends CDMPanel {

    private static final long serialVersionUID = 1L;

    private CapturedDataManagerFrame capturedDataManagerFrame;

    public PublicationsPropertiesPanel(CapturedDataManagerFrame capturedDataManagerFrame) {
        initComponents();
        this.capturedDataManagerFrame = capturedDataManagerFrame;
    }

    @Override public void removeListeners() {
        // TODO Auto-generated method stub

    }

    @Override public void updatePanel() {
    }

    private void refreshPublicationsButtonActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                capturedDataManagerFrame.refreshPublicationsButtonActionPerformed(e);
            }
        });
    }

    private void renamePublicationButtonActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                capturedDataManagerFrame.renamePublicationButtonActionPerformed(e);
            }
        });
    }

    private void deletePublicationsButtonActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                capturedDataManagerFrame.deletePublicationsButtonActionPerformed(e);
            }
        });
    }

    private void publicationSettingsButtonActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                capturedDataManagerFrame.publicationSettingsButtonActionPerformed(e);
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
                capturedDataManagerFrame.clearPublicationCapturedDataButtonActionPerformed(e);
            }
        });
    }

    private void extendCapturedDataActionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                capturedDataManagerFrame.extendCapturedDataActionPerformed(e);
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        actionsLabel = new JLabel();
        refreshPublicationsButton = new JButton();
        clearDataButton = new JButton();
        deletePublicationsButton = new JButton();
        exportCapturedDataButton = new JButton();
        renamePublicationButton = new JButton();
        publicationSettingsButton = new JButton();
        extendCapturedDataButton = new JButton();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setOpaque(false);
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UICDM", "PublicationsPanelTitle"));

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights =
                new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0E-4};

            //---- actionsLabel ----
            actionsLabel.setFont(UIManager.getFont("Label.font"));
            actionsLabel.setText(Localizer.localize("UICDM", "ActionsLabel"));
            panel1.add(actionsLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- refreshPublicationsButton ----
            refreshPublicationsButton.setFont(UIManager.getFont("Button.font"));
            refreshPublicationsButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/sdm/database_refresh.png")));
            refreshPublicationsButton.setFocusPainted(false);
            refreshPublicationsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    refreshPublicationsButtonActionPerformed(e);
                }
            });
            refreshPublicationsButton.setText(Localizer.localize("UICDM", "RefreshButtonText"));
            panel1.add(refreshPublicationsButton,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
            panel1.add(clearDataButton,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- deletePublicationsButton ----
            deletePublicationsButton.setFont(UIManager.getFont("Button.font"));
            deletePublicationsButton.setFocusPainted(false);
            deletePublicationsButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/sdm/table_row_delete.png")));
            deletePublicationsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    deletePublicationsButtonActionPerformed(e);
                }
            });
            deletePublicationsButton
                .setText(Localizer.localize("UICDM", "DeletePublicationsButtonText"));
            panel1.add(deletePublicationsButton,
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
            panel1.add(exportCapturedDataButton,
                new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- renamePublicationButton ----
            renamePublicationButton.setFont(UIManager.getFont("Button.font"));
            renamePublicationButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/sdm/table_edit.png")));
            renamePublicationButton.setFocusPainted(false);
            renamePublicationButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    renamePublicationButtonActionPerformed(e);
                }
            });
            renamePublicationButton
                .setText(Localizer.localize("UICDM", "RenamePublicationButtonText"));
            panel1.add(renamePublicationButton,
                new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- publicationSettingsButton ----
            publicationSettingsButton.setFont(UIManager.getFont("Button.font"));
            publicationSettingsButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cog.png")));
            publicationSettingsButton.setFocusPainted(false);
            publicationSettingsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    publicationSettingsButtonActionPerformed(e);
                }
            });
            publicationSettingsButton
                .setText(Localizer.localize("UICDM", "PublicationSettingsButtonText"));
            panel1.add(publicationSettingsButton,
                new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- extendCapturedDataButton ----
            extendCapturedDataButton.setFont(UIManager.getFont("Button.font"));
            extendCapturedDataButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/add.png")));
            extendCapturedDataButton.setFocusPainted(false);
            extendCapturedDataButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    extendCapturedDataActionPerformed(e);
                }
            });
            extendCapturedDataButton
                .setText(Localizer.localize("UICDM", "ExtendCapturedDataButtonText"));
            panel1.add(extendCapturedDataButton,
                new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel actionsLabel;
    private JButton refreshPublicationsButton;
    private JButton clearDataButton;
    private JButton deletePublicationsButton;
    private JButton exportCapturedDataButton;
    private JButton renamePublicationButton;
    private JButton publicationSettingsButton;
    private JButton extendCapturedDataButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}
