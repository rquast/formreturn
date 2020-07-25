package com.ebstrada.formreturn.server.thread;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.reader.FormReader;
import com.ebstrada.formreturn.manager.logic.recognition.reader.FormReaderException;
import com.ebstrada.formreturn.manager.logic.recognition.structure.FormRecognitionStructure;
import com.ebstrada.formreturn.manager.persistence.jpa.Form;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.persistence.jpa.IncomingImage;
import com.ebstrada.formreturn.manager.persistence.jpa.Log;
import com.ebstrada.formreturn.manager.util.image.ImageUtil;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.PublicationPreferences;
import com.ebstrada.formreturn.server.ServerGUI;
import com.ebstrada.formreturn.server.component.ProcessingQueueStatsPanel;
import com.ebstrada.formreturn.server.Main;

public class FormProcessor extends Thread {

    private EntityManager entityManager;

    private long sleepTime = 30000;

    private boolean runProcess = true;

    private int totalNumberOfImagesLeftToProcess = 0;

    private double lastProcessTime = 0;

    private String currentFilename = "";
    private int currentPage = 0;

    private static final Logger logger = Logger.getLogger(FormProcessor.class);

    public static long lastFormIdProcessed;

    public void processForm(IncomingImage incomingImage) {

        FormRecognitionStructure frs = new FormRecognitionStructure();
        FormReader formReader = null;

        long incomingImageId = incomingImage.getIncomingImageId();

        try {
            int pageCount = 1;

            BufferedImage sourceImage = null;
            byte[] imageData = incomingImage.getIncomingImageData();
            currentFilename = incomingImage.getIncomingImageName();

            try {
                pageCount = ImageUtil.getNumberOfPagesInTiff(imageData);
            } catch (FormReaderException fre) {
                throw fre;
            } catch (Exception ex) {
                String msg = Localizer.localize("Server", "InvalidImageFileFormatMessage");
                FormReaderException fre =
                    new FormReaderException(FormReaderException.INVALID_IMAGE_FORMAT, msg);
                throw fre;
            }

            for (int scannedPageNumber = 1; scannedPageNumber <= pageCount; scannedPageNumber++) {
                long startTime = System.currentTimeMillis();
                currentPage = scannedPageNumber;
                if (lastProcessTime > 0) {
                    updateStatus();
                }

                try {

                    try {
                        sourceImage =
                            ImageUtil.blurImage(ImageUtil.readImage(imageData, scannedPageNumber));
                    } catch (FormReaderException fre) {
                        throw fre;
                    } catch (Exception ex) {
                        String msg = Localizer.localize("Server", "InvalidImageFileFormatMessage");
                        FormReaderException fre =
                            new FormReaderException(FormReaderException.INVALID_IMAGE_FORMAT, msg);
                        throw fre;
                    }
                    sourceImage.flush();

                    BufferedImage checkedImage =
                        ImageUtil.resizePageImageIfTooBig(8000, sourceImage);
                    if (checkedImage != null) {
                        setStatusMessage(
                            Localizer.localize("Server", "ResizingLargeImageMessage") + "\n");
                        sourceImage = checkedImage;
                    }

                    formReader = new FormReader(sourceImage, frs, entityManager, currentFilename);
                    PublicationPreferences publicationPreferences =
                        PreferencesManager.getPublicationPreferences();
                    formReader
                        .setErrorDuplicateScans(publicationPreferences.isErrorDuplicateScans());

                    long formPageId = incomingImage.getAssignToFormPageId();
                    BufferedImage image = null;
                    if (formPageId > 0) {
                        image = formReader.registerForm(sourceImage,
                            getNextFormPageID(entityManager, formPageId, scannedPageNumber));
                    } else {
                        image = formReader.registerForm(sourceImage);
                    }

                    if (formReader.getPublicationType()
                        == PublicationPreferences.FORM_ID_RECONCILE_WITH_SOURCE_DATA_RECORD) {
                        formReader
                            .processForm(image, incomingImage.getCaptureTime(), scannedPageNumber);
                    } else {
                        formReader.processTemplate(image, incomingImage.getCaptureTime(),
                            scannedPageNumber);
                    }
                } catch (FormReaderException fre) {
                    logger.warn(fre.getLocalizedMessage(), fre);
                    markImageFailed(fre, sourceImage, incomingImage.getIncomingImageName(),
                        scannedPageNumber);
                    long endTime = System.currentTimeMillis();
                    setLastProcessTime(endTime - startTime);
                    --totalNumberOfImagesLeftToProcess;
                    appendErrorStatusMessage(fre);
                    continue;
                } catch (Exception e) {
                    logger.warn(e.getLocalizedMessage(), e);
                    markImageFailed(sourceImage, incomingImage.getIncomingImageName(),
                        scannedPageNumber);
                    long endTime = System.currentTimeMillis();
                    setLastProcessTime(endTime - startTime);
                    --totalNumberOfImagesLeftToProcess;
                    appendErrorStatusMessage(e.getLocalizedMessage());
                    continue;
                } catch (Error er) {
                    logger.error(er.getLocalizedMessage(), er);
                    markImageFailed(sourceImage, incomingImage.getIncomingImageName(),
                        scannedPageNumber);
                    long endTime = System.currentTimeMillis();
                    setLastProcessTime(endTime - startTime);
                    --totalNumberOfImagesLeftToProcess;
                    appendErrorStatusMessage(er.getLocalizedMessage());
                    continue;
                }
                long endTime = System.currentTimeMillis();
                setLastProcessTime(endTime - startTime);
                --totalNumberOfImagesLeftToProcess;
                appendStatusMessage();
            }
            incomingImage = entityManager.find(IncomingImage.class, incomingImageId);
            entityManager.remove(incomingImage);

            if (sourceImage == null) {
                String msg = Localizer.localize("Server", "InvalidImageFileFormatMessage");
                FormReaderException fre =
                    new FormReaderException(FormReaderException.INVALID_IMAGE_FORMAT, msg);
                throw fre;
            }
        } catch (FormReaderException fre) {
            markImageFailed(fre, incomingImageId);
        } catch (Exception e) {
            markImageFailed(e, incomingImageId);
        }

    }

    private long getNextFormPageID(EntityManager entityManager, long formPageId,
        int scannedPageNumber) {

        FormPage formPage = entityManager.find(FormPage.class, formPageId);
        int startPageNumber = (int) formPage.getFormPageNumber();

        Form form = formPage.getFormId();

        List<FormPage> fpc = form.getFormPageCollection();
        int formPageCount = fpc.size();
        int formNumber = (scannedPageNumber - 1) / formPageCount;

        boolean isTemplate = (form.getRecordId() == null);

        if (isTemplate) {

            int formPageNumber = scannedPageNumber - (formPageCount * formNumber);

            for (FormPage fp : fpc) {

                if ((fp.getFormPageNumber() == formPageNumber) && (formPageNumber
                    >= startPageNumber)) {
                    return fp.getFormPageId();
                }

            }

        } else {

            // is a form id publication.
            int formPageNumber = scannedPageNumber - (formPageCount * formNumber);

            Query formQuery = entityManager.createNativeQuery(
                "SELECT * FROM FORM WHERE PUBLICATION_ID = " + form.getPublicationId().getPublicationId()
                    + " AND FORM_ID >= " + form.getFormId()
                    + " ORDER BY FORM_ID ASC", Form.class);
            List<Form> fc = formQuery.getResultList();

            int i = 0;
            for (Form frm : fc) {
                if (i == formNumber) {
                    Query query = entityManager.createNamedQuery("FormPage.findByFormPageNumber");
                    query.setParameter("formId", frm);
                    query.setParameter("formPageNumber", formPageNumber);
                    FormPage fp = (FormPage) query.getSingleResult();
                    return fp.getFormPageId();
                }
                i++;
            }

        }

        return formPageId;

    }

    private void appendErrorStatusMessage(String message) {
        String filenameAndPage = String
            .format(Localizer.localize("Server", "CurrentFilenameAndPage"), currentFilename,
                currentPage + "");
        setStatusMessage(
            Localizer.localize("Server", "ErrorTitle") + ": " + filenameAndPage + " - " + message
                + "\n");
    }

    private void appendErrorStatusMessage(FormReaderException fre) {
        String filenameAndPage = String
            .format(Localizer.localize("Server", "CurrentFilenameAndPage"), currentFilename,
                currentPage + "");
        setStatusMessage(
            Localizer.localize("Server", "ErrorTitle") + ": " + filenameAndPage + " - " + fre
                .getErrorTitle() + "\n");
    }

    private void setStatusMessage(String status) {

        logger.info(status);

        if (ServerGUI.getInstance() == null || ServerGUI.getInstance().getServerFrame() == null) {
            return;
        }

        ProcessingQueueStatsPanel pqsp =
            ServerGUI.getInstance().getServerFrame().getProcessingQueueStatsPanel();
        pqsp.appendProcessingQueueStatusTextArea(status);
    }

    private void updateStatus() {

        if (ServerGUI.getInstance() == null || ServerGUI.getInstance().getServerFrame() == null) {
            return;
        }

        ProcessingQueueStatsPanel pqsp =
            ServerGUI.getInstance().getServerFrame().getProcessingQueueStatsPanel();
        pqsp.setImagesInQueue(totalNumberOfImagesLeftToProcess);
        pqsp.setLastProcessTime(lastProcessTime);
        if (totalNumberOfImagesLeftToProcess > 0) {
            pqsp.setFilename(String
                .format(Localizer.localize("Server", "CurrentFilenameAndPage"), currentFilename,
                    currentPage + ""));
        } else {
            pqsp.setFilename("");
        }
    }

    private void appendStatusMessage() {
        String filenameAndPage = String
            .format(Localizer.localize("Server", "CurrentFilenameAndPage"), currentFilename,
                currentPage + "");
        setStatusMessage(String
            .format(Localizer.localize("Server", "ProcessedInStatusMessage"), filenameAndPage,
                ((double) lastProcessTime / 1000) + "") + "\n");
    }

    public void markImageFailed(BufferedImage sourceImage, String imageName, int pageNumber) {
        FormReaderException fre = new FormReaderException(FormReaderException.UNSPECIFIED);
        markImageFailed(fre, sourceImage, imageName, pageNumber);
    }

    public void markImageFailed(FormReaderException fre, BufferedImage sourceImage,
        String imageName, int pageNumber) {
        IncomingImage incomingImage = new IncomingImage();
        incomingImage.setMatchStatus((short) -1);
        incomingImage.setCaptureTime(new Timestamp(System.currentTimeMillis()));
        incomingImage.setMatchErrorType(fre.getError());
        incomingImage.setMatchErrorData(fre.getErrorData());
        incomingImage.setMatchErrorScannedPageNumber(pageNumber);
        incomingImage.setNumberOfPages(1);
        try {
            incomingImage.setIncomingImageData(ImageUtil.getPNGByteArray(sourceImage));
        } catch (IOException e) {
            logger.warn(e.getLocalizedMessage(), e);
        }
        incomingImage.setIncomingImageName(imageName);
        entityManager.persist(incomingImage);
    }

    public void markImageFailed(Exception e, long incomingImageId) {
        FormReaderException fre = new FormReaderException(FormReaderException.UNSPECIFIED);
        markImageFailed(fre, incomingImageId);
    }

    public void markImageFailed(FormReaderException fre, long incomingImageId) {
        IncomingImage incomingImage = entityManager.find(IncomingImage.class, incomingImageId);
        incomingImage.setMatchStatus((short) -1);
        incomingImage.setMatchErrorType(fre.getError());
        incomingImage.setMatchErrorData(fre.getErrorData());
        entityManager.persist(incomingImage);
    }

    public void pollIncomimgImages() {
        while (runProcess) {

            boolean doLoop = false;
            do {

                if (entityManager == null) {
                    entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
                }

                if (entityManager == null) {
                    break;
                }

                doLoop = false;
                startTransaction();

                Query incomingImageQuery =
                    entityManager.createNamedQuery("IncomingImage.findByMatchStatus");
                incomingImageQuery.setParameter("matchStatus", (short) 0);

                List resultList = incomingImageQuery.getResultList();
                if (resultList.size() > 0) {
                    doLoop = true;

                    Query query = entityManager.createNativeQuery(
                        "SELECT SUM(NUMBER_OF_PAGES) FROM INCOMING_IMAGE WHERE MATCH_STATUS = 0");
                    Long totalPageCount = (Long) query.getSingleResult();
                    totalNumberOfImagesLeftToProcess = totalPageCount.intValue();

                    Iterator<IncomingImage> resultListIterator = resultList.iterator();

                    try {
                        while (resultListIterator.hasNext()) {
                            processForm(resultListIterator.next());
                        }
                    } catch (Exception ex) {
                        logger.warn(ex.getLocalizedMessage(), ex);
                    } catch (Error er) {
                        logger.error(er.getLocalizedMessage(), er);
                    }

                }

                endTransaction();

                if (entityManager != null) {
                    entityManager.close();
                    entityManager = null;
                }

                if (!runProcess) {
                    break;
                }

            } while (doLoop);

            totalNumberOfImagesLeftToProcess = 0;
            currentFilename = "";
            currentPage = 0;
            updateStatus();



            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                // just continue
            }

        }

        if (entityManager != null) {
            entityManager.close();
        }

    }

    public boolean startTransaction() {
        if (entityManager == null) {
            return false;
        } else {
            boolean hasTransaction = false;
            while (hasTransaction == false) {
                try {
                    if (entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().rollback();
                    }
                    entityManager.getTransaction().begin();
                    entityManager.flush();
                    hasTransaction = true;
                } catch (Exception ex) {
                    logger.warn(ex.getLocalizedMessage(), ex);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
        return true;
    }

    public void setLastProcessTime(double lastProcessTime) {
        this.lastProcessTime = lastProcessTime;
    }

    public void endTransaction() {
        try {
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            logger.warn(ex.getLocalizedMessage(), ex);
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        }
    }

    public void abortTransaction() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
        }
    }

    public void run() {
        while (runProcess) {
            try {
                pollIncomimgImages();
            } catch (Exception ex) {
                logger.error(ex);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
            } catch (Error er) {
                logger.error(er);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public void setRunProcess(boolean runProcess) {
        this.runProcess = runProcess;
    }

}
