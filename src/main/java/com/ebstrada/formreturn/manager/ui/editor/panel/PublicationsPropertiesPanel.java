package com.ebstrada.formreturn.manager.ui.editor.panel;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.swingx.*;

import com.ebstrada.formreturn.api.messaging.ProcessingStatusDialog;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.publish.FormPublisher;
import com.ebstrada.formreturn.manager.logic.publish.FormPublisherException;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.frame.FormFrame;
import com.ebstrada.formreturn.manager.ui.filefilter.FilenameExtensionFilter;
import com.ebstrada.formreturn.manager.util.Misc;

public class PublicationsPropertiesPanel extends JXTaskPane {

    private static final long serialVersionUID = 1L;

    private FormFrame formFrame;

    public PublicationsPropertiesPanel(FormFrame formFrame) {
        initComponents();
        this.formFrame = formFrame;
    }

    private void refreshPubicationsButtonActionPerformed(ActionEvent e) {
        formFrame.getPfp().restorePublications();
    }

    private void printSelectionButtonActionPerformed(ActionEvent e) {

        final long[] publicationIds = formFrame.getPfp().getSelectedPublicationIds();

        if (publicationIds != null && publicationIds.length > 0) {

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    exportPublishedForms(publicationIds[0], FormPublisher.COLLATED_FORMS, true);
                }
            });

        }

    }

    public File getPDFFile(long publicationId) {

        File file = null;
        FilenameExtensionFilter filter = new FilenameExtensionFilter();
        filter.addExtension("pdf");

        FileDialog fd = new FileDialog(Main.getInstance(),
            Localizer.localize("UI", "FormPublisherSavePDFFileDialogTitle"), FileDialog.SAVE);
        fd.setFilenameFilter(filter);

        File lastDir = null;
        if (Globals.getLastDirectory() != null) {

            lastDir = new File(Globals.getLastDirectory());

            if (!(lastDir.exists())) {
                lastDir = null;
            }

        }

        if (lastDir == null) {
            lastDir = new File(System.getProperty("user.home"));
        }

        fd.setFile(String
            .format(Localizer.localize("UI", "FormPublisherPDFExportAllFileNamePrefix"),
                publicationId) + ".pdf");

        try {
            fd.setDirectory(lastDir.getCanonicalPath());
        } catch (IOException e1) {
            fd.setDirectory(".");
        }

        fd.setModal(true);
        fd.setVisible(true);
        if (fd.getFile() != null) {

            String filename = fd.getFile();
            if (!(filename.endsWith(".pdf") || filename.endsWith(".PDF"))) {
                filename += ".pdf";
            }
            file = new File(fd.getDirectory() + filename);
            if (file.isDirectory()) {
                return null;
            }
            try {
                Globals.setLastDirectory(file.getCanonicalPath());
            } catch (IOException ldex) {
            }
        }

        return file;

    }

    public File getPDFDirectory() {
        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileFilter() {

            @Override public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override public String getDescription() {
                return null;
            }

        };
        chooser.setFileFilter(filter);

        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle(
            Localizer.localize("UI", "FormPublisherSelectPublicationExportDirectoryDialogTitle"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        chooser.rescanCurrentDirectory();

        if (chooser.showOpenDialog(Main.getInstance()) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else {
            return null;
        }

    }

    public void exportPublishedForms(long publicationId, final int collation,
        final boolean isPrintJob) {

        final ProcessingStatusDialog publishStatusDialog =
            new ProcessingStatusDialog(Main.getInstance());

        final FormPublisher formPublisher = new FormPublisher(publicationId);

        class PublishRunner implements Runnable {

            public void run() {

                int useCollation = collation;

                if (collation == FormPublisher.SEPARATED_FORMS && isPrintJob) {
                    useCollation = FormPublisher.COLLATED_FORMS;
                }

                try {
                    formPublisher.setCollation(useCollation);
                    formPublisher.setPublishStatusDialog(publishStatusDialog);
                    formPublisher.exportPublication();
                    if (formPublisher.getCollation() == FormPublisher.COLLATED_FORMS
                        || isPrintJob) {
                        final File pdfFile = getPDFFile(formPublisher.getPublicationId());
                        formPublisher.collateToPDF(pdfFile);

                        if (isPrintJob) {

                            if (publishStatusDialog != null) {
                                publishStatusDialog.dispose();
                            }
                            try {
                                Misc.printPDF(pdfFile);
                            } catch (Exception ex) {
                                Misc.printStackTrace(ex);
                                Misc.showErrorMsg(getRootPane(), ex.getLocalizedMessage());
                            }

                        } else {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    String message = Localizer
                                        .localize("UI", "FormPublisherPDFSaveSuccessMessage");
                                    String caption = Localizer
                                        .localize("UI", "FormPublisherPDFSaveSuccessTitle");
                                    javax.swing.JOptionPane
                                        .showConfirmDialog(Main.getInstance(), message, caption,
                                            javax.swing.JOptionPane.DEFAULT_OPTION,
                                            javax.swing.JOptionPane.INFORMATION_MESSAGE);
                                }
                            });
                        }

                    } else {
                        formPublisher.individualPDF(getPDFDirectory());
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                String message =
                                    Localizer.localize("UI", "FormPublisherPDFSaveSuccessMessage");
                                String caption =
                                    Localizer.localize("UI", "FormPublisherPDFSaveSuccessTitle");
                                javax.swing.JOptionPane
                                    .showConfirmDialog(Main.getInstance(), message, caption,
                                        javax.swing.JOptionPane.DEFAULT_OPTION,
                                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                            }
                        });
                    }
                } catch (final FormPublisherException fpe) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(fpe);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            Misc.showErrorMsg(Main.getInstance(), fpe.getErrorTitle());
                        }
                    });
                } catch (final Exception ex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            Misc.showErrorMsg(Main.getInstance(), ex.getMessage());
                        }
                    });
                } finally {
                    if (formPublisher != null) {
                        formPublisher.removeWorkingFiles();
                    }
                    if (publishStatusDialog != null) {
                        publishStatusDialog.dispose();
                    }
                    Globals.curEditor(formFrame.getGraph().getEditor());
                    formFrame.addFigsToActiveLayer();
                }

            }
        }
        ;

        PublishRunner publishRunner = new PublishRunner();
        Thread thread = new Thread(publishRunner);
        thread.start();

        publishStatusDialog.setModal(true);
        publishStatusDialog.setVisible(true);

        if (publishStatusDialog != null) {
            publishStatusDialog.dispose();
        }

    }

    private void exportPDFButtonActionPerformed(ActionEvent e) {

        final long[] publicationIds = formFrame.getPfp().getSelectedPublicationIds();
        if (publicationIds != null && publicationIds.length > 0) {

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {

                    Object[] options =
                        {Localizer.localize("UI", "PublicationPropertiesCollateFormsOption"),
                            Localizer.localize("UI",
                                "PublicationPropertiesExportIndividuallyOption")};

                    String msg =
                        Localizer.localize("UI", "PublicationPropertiesCollateFormsMessage");

                    int result = JOptionPane.showOptionDialog(Main.getInstance(), msg,
                        Localizer.localize("UI", "PublicationPropertiesCollateFormsTitle"),
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
                        options[0]);

                    if (result == 0) {
                        exportPublishedForms(publicationIds[0], FormPublisher.COLLATED_FORMS,
                            false);
                    } else {
                        exportPublishedForms(publicationIds[0], FormPublisher.SEPARATED_FORMS,
                            false);
                    }

                }
            });

        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        actionsLabel = new JLabel();
        refreshTablesButton = new JButton();
        printSelectionButton = new JButton();
        exportPDFButton = new JButton();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setOpaque(false);
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "PublicationsPanelTitle"));

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights =
                new double[] {1.0, 1.0, 1.0, 1.0, 1.0E-4};

            //---- actionsLabel ----
            actionsLabel.setFont(UIManager.getFont("Label.font"));
            actionsLabel.setText(Localizer.localize("UI", "PublicationsPanelActionsLabel"));
            panel1.add(actionsLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- refreshTablesButton ----
            refreshTablesButton.setFont(UIManager.getFont("Button.font"));
            refreshTablesButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/sdm/database_refresh.png")));
            refreshTablesButton.setFocusPainted(false);
            refreshTablesButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    refreshPubicationsButtonActionPerformed(e);
                }
            });
            refreshTablesButton
                .setText(Localizer.localize("UI", "PublicationsPanelRefreshTablesButtonText"));
            panel1.add(refreshTablesButton,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
                .setText(Localizer.localize("UI", "PublicationsPanelPrintSelectionButtonText"));
            panel1.add(printSelectionButton,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
            exportPDFButton
                .setText(Localizer.localize("UI", "PublicationsPanelExportPDFButtonText"));
            panel1.add(exportPDFButton,
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel actionsLabel;
    private JButton refreshTablesButton;
    private JButton printSelectionButton;
    private JButton exportPDFButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
