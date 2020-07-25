package com.ebstrada.formreturn.manager.logic.export.image;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.ebstrada.formreturn.api.messaging.MessageNotification;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.jpa.Form;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.persistence.jpa.ProcessedImage;
import com.ebstrada.formreturn.manager.persistence.jpa.Publication;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.ui.Main;

public class ImageExporter {

    private MessageNotification messageNotification;

    private ArrayList<Long> publicationIds;

    private EntityManager entityManager;

    private ImageExportPreferences imageExportPreferences;

    private PDDocument doc;

    private PDRectangle pageRectangle;

    public ImageExporter(ArrayList<Long> publicationIds,
        ImageExportPreferences imageExportPreferences) {
        this.publicationIds = publicationIds;
        this.imageExportPreferences = imageExportPreferences;
    }

    public void setMessageNotification(MessageNotification messageNotification) {
        this.messageNotification = messageNotification;
    }

    private EntityManager getEntityManager() {
        if (this.entityManager == null) {
            if (com.ebstrada.formreturn.manager.ui.Main.getInstance() != null) {
                return Main.getInstance().getJPAConfiguration().getEntityManager();
            } else {
                return com.ebstrada.formreturn.server.Main.getInstance().getJPAConfiguration()
                    .getEntityManager();
            }
        } else {
            return this.entityManager;
        }
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void export() throws Exception {

        this.pageRectangle = getPDRectangle();

        try {
            if (getCollation() == Collation.ALL_IMAGES_TOGETHER) {
                this.doc = new PDDocument();
            }
            for (Long publicationId : publicationIds) {
                exportPublication(publicationId);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {

            if (getCollation() == Collation.ALL_IMAGES_TOGETHER) {
                if (doc.getNumberOfPages() > 0) {
                    this.doc.save(getFile());
                }
            }

            if (this.entityManager != null && this.entityManager.isOpen()) {
                this.entityManager.close();
            }

        }

    }

    private File getFile() {
        String fileStr = imageExportPreferences.getFile();
        if (imageExportPreferences.isTimestampFilenamePrefix()) {
            fileStr = Misc.getTimestampPrefixedFilename(fileStr);
        }
        return new File(fileStr);
    }

    private Collation getCollation() {
        return imageExportPreferences.getCollation();
    }

    private void setMessage(String message) {
        if (messageNotification == null) {
            return;
        }
        messageNotification.setMessage(message);
    }

    private void exportPublication(Long publicationId) throws Exception {

        this.entityManager = getEntityManager();

        if (this.entityManager == null) {
            throw new Exception(Localizer.localize("Util", "NullEntityManager"));
        }

        Publication publication = this.entityManager.find(Publication.class, publicationId);

        if (publication == null) {
            throw new PublicationNotFoundException();
        }

        setMessage(Localizer.localize("Util", "LoadingFormRecordsMessage"));

        List<Form> forms = publication.getFormCollection();

        if (forms == null || forms.size() <= 0) {
            throw new PublicationContainsNoFormsException();
        }

        int formCount = forms.size();
        int formNumber = 1;
        for (Form form : forms) {

            if (getCollation() == Collation.FORM_IMAGES_TOGETHER) {
                doc = new PDDocument();
            }

            int formPageNumber = 1;

            for (FormPage formPage : form.getFormPageCollection()) {

                String processingMsg = Localizer.localize("Util", "ExportingFormImageMessage");
                setMessage(String.format(processingMsg, formNumber, formPageNumber, formCount));

                List<ProcessedImage> processedImages = formPage.getProcessedImageCollection();
                if (processedImages == null || processedImages.size() <= 0) {
                    formPageNumber++;
                    continue;
                }

                // write raw image
                if ((getCollation() == Collation.IMAGES_ONLY) && (getOverlay() == null
                    || getOverlay().size() <= 0)) {

                    writeFormPagePNGFile(formPage, getFormPagePNGFile(getFile(), formPage));

                    // write image and overlay to PDF
                } else {

                    if (getCollation() == Collation.IMAGES_ONLY) {
                        doc = new PDDocument();
                    }

                    ImageOverlay imageOverlay =
                        new ImageOverlay(formPage, doc, pageRectangle, this.imageExportPreferences);
                    writeFormPage(formPage, imageOverlay);
                    imageOverlay.closeContentStream();

                    if (getCollation() == Collation.IMAGES_ONLY) {
                        if (doc.getNumberOfPages() > 0) {
                            doc.save(getFormPageImageFile(getFile(), formPage));
                        }
                    }

                }

                formPageNumber++;

            }

            formNumber++;

            if (getCollation() == Collation.FORM_IMAGES_TOGETHER) {
                if (doc.getNumberOfPages() > 0) {
                    doc.save(getFormImageFile(getFile(), form));
                }
            }

        }

    }

    private void writeFormPagePNGFile(FormPage formPage, File formPageImageFile)
        throws IOException {

        List<ProcessedImage> processedImages = formPage.getProcessedImageCollection();
        if (processedImages == null || processedImages.size() <= 0) {
            return;
        }

        ProcessedImage processedImage = processedImages.get(processedImages.size() - 1);
        byte[] data = processedImage.getProcessedImageData();

        FileOutputStream tfos = null;
        BufferedOutputStream bos = null;
        try {
            tfos = new FileOutputStream(formPageImageFile);
            bos = new BufferedOutputStream(tfos);
            bos.write(data);
        } catch (IOException e) {
            throw e;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    Misc.printStackTrace(e);
                }
            }
            if (tfos != null) {
                tfos.close();
            }
        }

    }

    private ArrayList<Overlay> getOverlay() {
        return this.imageExportPreferences.getOverlay();
    }

    private void writeFormPage(FormPage formPage, ImageOverlay imageOverlay) throws Exception {
        List<ProcessedImage> processedImages = formPage.getProcessedImageCollection();
        if (processedImages == null || processedImages.size() <= 0) {
            return;
        }
        ProcessedImage processedImage = processedImages.get(processedImages.size() - 1);
        collateImageToPDFStream(processedImage, imageOverlay, pageRectangle);
    }

    private PDRectangle getPDRectangle() {
        return this.imageExportPreferences.getPDRectangle();
    }

    private File getFormImageFile(File directory, Form form) throws IOException {
        File f = new File(
            directory.getCanonicalPath() + File.separator + parsePrefix(getImageFilePrefix(), form)
                + ".pdf");
        try {
            f.getCanonicalPath();
            return f;
        } catch (IOException e) {
            throw e;
        }
    }

    private File getFormPageImageFile(File directory, FormPage formPage) throws IOException {
        File f = new File(
            directory.getCanonicalPath() + File.separator + parsePrefix(getImageFilePrefix(),
                formPage) + ".pdf");
        try {
            f.getCanonicalPath();
            return f;
        } catch (IOException e) {
            throw e;
        }
    }

    private File getFormPagePNGFile(File directory, FormPage formPage) throws IOException {
        File f = new File(
            directory.getCanonicalPath() + File.separator + parsePrefix(getImageFilePrefix(),
                formPage) + ".png");
        try {
            f.getCanonicalPath();
            return f;
        } catch (IOException e) {
            throw e;
        }
    }

    private String getImageFilePrefix() {
        return this.imageExportPreferences.getImageFilePrefix();
    }

    private String parsePrefix(String filenamePrefix, Form form) {
        Map<String, String> recordMap = new HashMap<String, String>();
        recordMap = Misc.getFullRecordMap(form);
        String prefix = Misc.parseFields(filenamePrefix, recordMap);
        if (prefix.length() <= 0) {
            prefix = form.getFormId() + "";
        }
        return prefix;
    }

    private String parsePrefix(String filenamePrefix, FormPage formPage) {
        Map<String, String> recordMap = new HashMap<String, String>();
        recordMap = Misc.getFullRecordMap(formPage);
        String prefix = Misc.parseFields(filenamePrefix, recordMap);
        if (prefix.length() <= 0) {
            prefix = formPage.getFormPageId() + "";
        }
        return prefix;
    }

    private void collateImageToPDFStream(ProcessedImage processedImage, ImageOverlay imageOverlay,
        PDRectangle pageRectangle) throws Exception {
        isInterrupted();

        imageOverlay.drawImage(processedImage, pageRectangle);
        imageOverlay.drawHeader();
        if (this.imageExportPreferences.getOverlay().contains(Overlay.SOURCE_DATA)) {
            imageOverlay.drawSourceData(this.entityManager);
        }
        if (this.imageExportPreferences.getOverlay().contains(Overlay.CAPTURED_DATA)) {
            imageOverlay.drawCapturedData(this.entityManager);
        }
    }

    private void isInterrupted() throws Exception {
        if (messageNotification == null) {
            return;
        }
        if (messageNotification.isInterrupted()) {
            throw messageNotification.getException();
        }
    }

}
