package com.ebstrada.formreturn.manager.logic.publish;

import java.awt.Component;
import java.awt.FileDialog;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import com.ebstrada.formreturn.api.messaging.ProcessingStatusDialog;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.filefilter.FilenameExtensionFilter;
import com.ebstrada.formreturn.manager.util.Misc;

public class FormPDFGenerator {

    public static void exportPublishedForms(long publicationId, final int collation,
        final boolean isPrintJob, final Component rootPane) {

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
                                Misc.showErrorMsg(rootPane, ex.getLocalizedMessage());
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

    public static File getPDFFile(long publicationId) {

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

    public static File getPDFDirectory() {
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


}
