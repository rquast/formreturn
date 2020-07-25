package com.ebstrada.formreturn.manager.ui.sdm.panel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.sdm.SourceDataManagerFrame;
import org.jdesktop.swingx.*;

public class PublicationsPropertiesPanel extends SDMPanel {

    private static final long serialVersionUID = 1L;

    private SourceDataManagerFrame sourceDataManagerFrame;

    public PublicationsPropertiesPanel(SourceDataManagerFrame sourceDataManagerFrame) {
        initComponents();
        this.sourceDataManagerFrame = sourceDataManagerFrame;
    }

    private void publicationsRefreshButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.refreshPublicationsButtonActionPerformed(e);
    }

    private void deletePublicationsButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.deletePublicationsButtonActionPerformed(e);
    }

    private void printSelectionButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.printPublicationsButtonActionPerformed(e);
    }

    private void exportPDFButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.exportPublicationsButtonActionPerformed(e);
    }

    private void renamePublicationButtonActionPerformed(ActionEvent e) {
        sourceDataManagerFrame.renamePublicationsButtonActionPerformed(e);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        tableNameLabel = new JLabel();
        tableNameTextField = new JTextField();
        actionsLabel = new JLabel();
        recordsRefreshButton = new JButton();
        renamePublicationButton = new JButton();
        deletePublicationsButton = new JButton();
        printSelectionButton = new JButton();
        exportPDFButton = new JButton();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setOpaque(false);
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "PublicationsPropertiesPanelTitle"));

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights =
                new double[] {1.0, 1.0, 1.0, 1.0, 0.0, 1.0, 1.0, 1.0, 1.0E-4};

            //---- tableNameLabel ----
            tableNameLabel.setFont(UIManager.getFont("Label.font"));
            tableNameLabel
                .setText(Localizer.localize("UI", "PublicationsPropertiesPanelTableNameLabel"));
            panel1.add(tableNameLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- tableNameTextField ----
            tableNameTextField.setEditable(false);
            tableNameTextField.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
            tableNameTextField.setBackground(Color.white);
            panel1.add(tableNameTextField,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- actionsLabel ----
            actionsLabel.setFont(UIManager.getFont("Label.font"));
            actionsLabel
                .setText(Localizer.localize("UI", "PublicationsPropertiesPanelActionsLabel"));
            panel1.add(actionsLabel,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- recordsRefreshButton ----
            recordsRefreshButton.setFont(UIManager.getFont("Button.font"));
            recordsRefreshButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/sdm/database_refresh.png")));
            recordsRefreshButton.setFocusPainted(false);
            recordsRefreshButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    publicationsRefreshButtonActionPerformed(e);
                }
            });
            recordsRefreshButton
                .setText(Localizer.localize("UI", "PublicationsPropertiesPanelRefreshButtonText"));
            panel1.add(recordsRefreshButton,
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
                .setText(Localizer.localize("UI", "PublicationsPropertiesPanelRenameButtonText"));
            panel1.add(renamePublicationButton,
                new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

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
                .setText(Localizer.localize("UI", "PublicationsPropertiesPanelDeleteButtonText"));
            panel1.add(deletePublicationsButton,
                new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- printSelectionButton ----
            printSelectionButton.setFont(UIManager.getFont("Button.font"));
            printSelectionButton.setFocusPainted(false);
            printSelectionButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/preview/printer.png")));
            printSelectionButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    printSelectionButtonActionPerformed(e);
                }
            });
            printSelectionButton
                .setText(Localizer.localize("UI", "PublicationsPropertiesPanelPrintButtonText"));
            panel1.add(printSelectionButton,
                new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- exportPDFButton ----
            exportPDFButton.setFont(UIManager.getFont("Button.font"));
            exportPDFButton.setFocusPainted(false);
            exportPDFButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/preview/page_white_acrobat.png")));
            exportPDFButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    exportPDFButtonActionPerformed(e);
                }
            });
            exportPDFButton.setText(
                Localizer.localize("UI", "PublicationsPropertiesPanelExportPDFButtonText"));
            panel1.add(exportPDFButton,
                new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel tableNameLabel;
    private JTextField tableNameTextField;
    private JLabel actionsLabel;
    private JButton recordsRefreshButton;
    private JButton renamePublicationButton;
    private JButton deletePublicationsButton;
    private JButton printSelectionButton;
    private JButton exportPDFButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    @Override public void removeListeners() {
        // TODO Auto-generated method stub

    }

    @Override public void updatePanel() {
        String tableName = sourceDataManagerFrame.getSelectedTableName();
        tableNameTextField.setText(tableName);
        tableNameTextField.setToolTipText(tableName);
    }

}
