package com.ebstrada.formreturn.manager.logic.publish;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.batik.util.CleanerThread;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import com.ebstrada.formreturn.api.messaging.MessageNotification;
import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigSegment;
import com.ebstrada.formreturn.manager.gef.presentation.RecognitionStructureFig;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.structure.BarcodeRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.CheckBoxRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.FormRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.OMRRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.PublicationRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.SegmentRecognitionStructure;
import com.ebstrada.formreturn.manager.persistence.jpa.CheckBox;
import com.ebstrada.formreturn.manager.persistence.jpa.DataSet;
import com.ebstrada.formreturn.manager.persistence.jpa.Form;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentBarcode;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentOmr;
import com.ebstrada.formreturn.manager.persistence.jpa.Grading;
import com.ebstrada.formreturn.manager.persistence.jpa.Publication;
import com.ebstrada.formreturn.manager.persistence.jpa.PublicationJAR;
import com.ebstrada.formreturn.manager.persistence.jpa.PublicationXSL;
import com.ebstrada.formreturn.manager.persistence.jpa.Record;
import com.ebstrada.formreturn.manager.persistence.jpa.Segment;
import com.ebstrada.formreturn.manager.persistence.xstream.Document;
import com.ebstrada.formreturn.manager.persistence.xstream.Page;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.persistence.JARPlugin;
import com.ebstrada.formreturn.manager.ui.editor.persistence.MarkingRule;
import com.ebstrada.formreturn.manager.ui.editor.persistence.MarkingProperties;
import com.ebstrada.formreturn.manager.ui.editor.persistence.Plugins;
import com.ebstrada.formreturn.manager.ui.editor.persistence.XSLTemplate;
import com.ebstrada.formreturn.manager.ui.editor.persistence.Templates;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.RandomGUID;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.PublicationPreferences;

public class FormPublisher {

    private String publicationName;
    private long sourceDataSetId = 0;
    private long publicationId = 0;
    private PublicationRecognitionStructure publicationRecognitionStructure;
    private Document document;
    private byte[] templateFile;
    private String workingDirName;

    private File exportDirectory;

    private com.ebstrada.formreturn.api.messaging.MessageNotification publishStatusDialog;

    private int publicationType = PublicationPreferences.FORM_ID_RECONCILE_WITH_SOURCE_DATA_RECORD;
    private int collation = COLLATED_FORMS;
    private boolean interrupted = false;
    private boolean scannedInOrder = false;

    public static final int COLLATED_FORMS = 0;
    public static final int SEPARATED_FORMS = 1;


    private class NumberedFileComparator implements Comparator<String> {

        public int compare(String f1, String f2) {
            int val = f1.compareTo(f2);
            if (val != 0) {
                int int1 = Misc.parseIntegerString(f1);
                int int2 = Misc.parseIntegerString(f2);
                val = int1 - int2;
            }
            return val;
        }

    }

    public FormPublisher(String publicationName, Document document,
        PublicationRecognitionStructure publicationRecognitionStructure, long sourceDataSetId,
        byte[] templateFile, String workingDirName) {
        this.publicationName = publicationName;
        this.sourceDataSetId = sourceDataSetId;
        this.document = document;
        this.templateFile = templateFile;
        this.workingDirName = workingDirName;
        this.publicationRecognitionStructure = publicationRecognitionStructure;
    }

    public FormPublisher(long publicationId) {
        this.publicationId = publicationId;
    }

    private String getTempBaseDir() throws Exception {
        return PreferencesManager.getHomeDirectory().getPath();
    }

    private void setExportDirectory() throws Exception {

        String tempBaseDir = getTempBaseDir();

        String GUID = (new RandomGUID()).toString();
        String workingDirName =
            tempBaseDir + System.getProperty("file.separator") + "working" + System
                .getProperty("file.separator") + GUID;
        File workingDirFile = new File(workingDirName);

        while (workingDirFile.exists()) {
            GUID = (new RandomGUID()).toString();
            workingDirName = tempBaseDir + System.getProperty("file.separator") + "working" + System
                .getProperty("file.separator") + GUID;
            workingDirFile = new File(workingDirName);
        }

        workingDirFile.mkdirs();

        exportDirectory = workingDirFile;

    }

    public void removeWorkingFiles() {
        if (exportDirectory != null && exportDirectory.exists()) {
            Misc.deleteDirectory(exportDirectory);
        }
    }

    private void cleanUp() {
        // this exits the batik cleaner thread
        // developed from patch - https://issues.apache.org/bugzilla/show_bug.cgi?id=48771
        CleanerThread.THREAD.exit();
        System.gc();
    }

    public void exportPublication() throws Exception {
        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
        if (entityManager == null) {
            throw new FormPublisherException(FormPublisherException.NO_ENTITY_MANAGER);
        }
        try {
            Publication publication = entityManager.find(Publication.class, publicationId);
            exportPublication(entityManager, publication, this.publishStatusDialog);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
            cleanUp();
        }
    }

    public void extendPublication(EntityManager entityManager, long publicationId,
        ArrayList<Long> recordIds, MessageNotification publishStatusDialog) throws Exception {

        startTransaction(entityManager);

        JGraph graph = null;
        File file = null;
        File workingDirFile = null;

        try {

            Publication publication = entityManager.find(Publication.class, publicationId);

            templateFile = publication.getTemplateFile();
            String tempBaseDir = getTempBaseDir();
            String GUID = (new RandomGUID()).toString();
            String workingDirName =
                tempBaseDir + System.getProperty("file.separator") + "working" + System
                    .getProperty("file.separator") + GUID;
            workingDirFile = new File(workingDirName);

            while (workingDirFile.exists()) {
                GUID = (new RandomGUID()).toString();
                workingDirName =
                    tempBaseDir + System.getProperty("file.separator") + "working" + System
                        .getProperty("file.separator") + GUID;
                workingDirFile = new File(workingDirName);
            }

            workingDirFile.mkdirs();

            file = new File(
                workingDirFile.getAbsoluteFile() + System.getProperty("file.separator") + Localizer
                    .localize("UI", "FormPublisherTemplateFileNamePrefix") + ".frf");
            FileOutputStream fos;
            fos = new FileOutputStream(file);
            fos.write(templateFile);
            fos.flush();
            fos.close();

            if (file != null) {
                graph = new JGraph();
                graph.getDocumentPackage().open(file, graph);
                graph.getEditor().postLoad();
            }

            document = graph.getDocument();

            List<Record> records = new ArrayList<Record>();

            for (long recordId : recordIds) {
                records.add(entityManager.find(Record.class, recordId));
            }

            publishForms(entityManager, document, publication, records, publishStatusDialog);

            endTransaction(entityManager);

        } catch (Exception ex) {
            abortTransaction(entityManager);
            throw ex;
        } finally {
            if (graph != null) {
                graph.getDocumentPackage().close();
            }
            if (file != null && file.exists()) {
                file.delete();
            }
            if (workingDirFile != null) {
                workingDirFile.delete();
            }
        }

    }


    private void exportPublication(EntityManager entityManager, Publication publication,
        MessageNotification messageNotification) throws Exception {

        templateFile = publication.getTemplateFile();

        String tempBaseDir = getTempBaseDir();

        JGraph graph = null;

        String GUID = (new RandomGUID()).toString();
        String workingDirName =
            tempBaseDir + System.getProperty("file.separator") + "working" + System
                .getProperty("file.separator") + GUID;
        File workingDirFile = new File(workingDirName);

        while (workingDirFile.exists()) {
            GUID = (new RandomGUID()).toString();
            workingDirName = tempBaseDir + System.getProperty("file.separator") + "working" + System
                .getProperty("file.separator") + GUID;
            workingDirFile = new File(workingDirName);
        }

        workingDirFile.mkdirs();

        File file = new File(
            workingDirFile.getAbsoluteFile() + System.getProperty("file.separator") + Localizer
                .localize("UI", "FormPublisherTemplateFileNamePrefix") + ".frf");
        FileOutputStream fos;
        fos = new FileOutputStream(file);
        fos.write(templateFile);
        fos.flush();
        fos.close();

        if (file != null) {

            graph = new JGraph();

            graph.getDocumentPackage().open(file, graph);
            graph.getEditor().postLoad();

        }

        document = graph.getDocument();

        setExportDirectory();

        try {

            List<Form> forms = null;

            if (publication.getPublicationType()
                == PublicationPreferences.FORM_ID_RECONCILE_WITH_SOURCE_DATA_RECORD) {
                // all forms
                Query query = entityManager.createNamedQuery("Form.findByPublicationId");
                query.setParameter("publicationId", publication);
                forms = query.getResultList();
            } else {
                // template form
                Query query = entityManager.createNamedQuery("Form.findTemplateByPublicationId");
                query.setParameter("publicationId", publication);
                forms = query.getResultList();
            }

            if (forms != null && forms.size() > 0) {
                int i = 1;
                for (Form form : forms) {
                    if (isInterrupted()) {
                        throw new FormPublisherException(FormPublisherException.INTERRUPTED);
                    }
                    messageNotification.setMessage(String
                        .format(Localizer.localize("UI", "FormPublisherCreatingPDFPagesMessage"),
                            i + "", forms.size() + ""));
                    createPDFFile(form.getFormId(), document,
                        graph.getDocumentPackage().getWorkingDirName());
                    i++;
                    cleanUp();
                }
            }

        } catch (Exception ex) {
            throw ex;
        }

        graph.getDocumentPackage().close();
        file.delete();
        workingDirFile.delete();

    }

    private void createPDFFile(long formId, Document document, String workingDirName)
        throws Exception {

        File pdfFile = new File(
            exportDirectory.getCanonicalPath() + System.getProperty("file.separator") + formId
                + ".pdf");

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
        if (entityManager == null) {
            throw new FormPublisherException(FormPublisherException.NO_ENTITY_MANAGER);
        }
        FileOutputStream output = new FileOutputStream(pdfFile);
        try {
            PDFDocumentExporter pdfd = new PDFDocumentExporter(document, workingDirName, output);
            pdfd.createPDF(entityManager, formId);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
            output.flush();
            output.close();
        }

    }

    public void createForms(Document document, MessageNotification messageNotification)
        throws Exception {
        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();
        if (entityManager == null) {
            throw new FormPublisherException(FormPublisherException.NO_ENTITY_MANAGER);
        }

        try {

            DataSet dataSet = entityManager.find(DataSet.class, sourceDataSetId);

            List<Record> records = null;

            if (getPublicationType()
                == PublicationPreferences.FORM_ID_RECONCILE_WITH_SOURCE_DATA_RECORD) {
                Query query = entityManager.createQuery(
                    "SELECT r FROM Record r WHERE r.dataSetId = :dataSetId ORDER BY r.recordId ASC",
                    Record.class);
                query.setParameter("dataSetId", dataSet);
                records = query.getResultList();
            }

            startTransaction(entityManager);

            try {
                Publication publication = createPublicationRecord(entityManager, dataSet);
                createGradingRecord(entityManager, publication, document);
                createPublicationXSLRecords(entityManager, publication, document, workingDirName);
                createPublicationJARecords(entityManager, publication, document, workingDirName);
                publishForms(entityManager, document, publication, records, messageNotification);
                endTransaction(entityManager);
            } catch (Exception ex) {
                abortTransaction(entityManager);
                throw ex;
            }

        } catch (Exception ex) {
            throw ex;
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
            cleanUp();
        }
    }

    private void createPublicationJARecords(EntityManager entityManager, Publication publication,
        Document document, String workingDirName) {

        if (document.getDocumentAttributes().getJARPlugins() == null) {
            return;
        }

        Plugins jarPlugins = document.getDocumentAttributes().getJARPlugins();

        if (jarPlugins.getJARPlugins().size() <= 0) {
            return;
        }

        int i = 0;
        for (JARPlugin jarPlugin : jarPlugins.getJARPlugins()) {
            try {
                PublicationJAR publicationJAR = new PublicationJAR();
                publicationJAR.setPublicationId(publication);
                publicationJAR.setFileName(jarPlugin.getFileName());
                publicationJAR.setGuid(jarPlugin.getPluginGUID());
                publicationJAR.setDescription(jarPlugin.getDescription());
                publicationJAR.setOrderIndex(i);
                publicationJAR.setJarData(getJARData(jarPlugin, workingDirName));
                i++;
                entityManager.persist(publicationJAR);
                entityManager.flush();
            } catch (Exception ex) {
                Misc.printStackTrace(ex);
            }
        }

    }

    private void createPublicationXSLRecords(EntityManager entityManager, Publication publication,
        Document document, String workingDirName) {

        if (document.getDocumentAttributes().getXSLTemplates() == null) {
            return;
        }

        Templates xslTemplates = document.getDocumentAttributes().getXSLTemplates();

        if (xslTemplates.getXSLTemplates().size() <= 0) {
            return;
        }

        int i = 0;
        for (XSLTemplate xslTemplate : xslTemplates.getXSLTemplates()) {
            try {
                PublicationXSL publicationXSL = new PublicationXSL();
                publicationXSL.setPublicationId(publication);
                publicationXSL.setFileName(xslTemplate.getFileName());
                publicationXSL.setGuid(xslTemplate.getTemplateGUID());
                publicationXSL.setDescription(xslTemplate.getTemplateDescription());
                publicationXSL.setOrderIndex(i);
                publicationXSL.setXslData(getXSLData(xslTemplate, workingDirName));
                i++;
                entityManager.persist(publicationXSL);
                entityManager.flush();
            } catch (Exception ex) {
                Misc.printStackTrace(ex);
            }
        }

    }

    private byte[] getXSLData(XSLTemplate xslTemplate, String workingDirectory) throws IOException {
        String guid = xslTemplate.getTemplateGUID();
        String xslFileName =
            workingDirectory + System.getProperty("file.separator") + "xsl" + System
                .getProperty("file.separator") + guid + ".xsl";
        File xslFile = new File(xslFileName);
        return Misc.getBytesFromFile(xslFile);
    }

    private byte[] getJARData(JARPlugin jarPlugin, String workingDirectory) throws IOException {
        String guid = jarPlugin.getPluginGUID();
        String jarFileName =
            workingDirectory + System.getProperty("file.separator") + "jar" + System
                .getProperty("file.separator") + guid + ".jar";
        File jarFile = new File(jarFileName);
        return Misc.getBytesFromFile(jarFile);
    }

    public FormRecognitionStructure getFormRecognitionStructure(Document document, Record record) {

        FormRecognitionStructure formRecognitionStructure = new FormRecognitionStructure();

        int barcodeOneValue = 1;

        for (int i = 0; i < document.getNumberOfPages(); i++) {

            int pageNumber = i + 1;

            Page pageContainer = document.getPageByPageNumber(pageNumber);

            List<Fig> figs = pageContainer.getFigs();

            if (figs != null) {
                for (Iterator<Fig> it = figs.iterator(); it.hasNext(); ) {
                    Fig fig = it.next();
                    if (fig instanceof FigSegment) {
                        FigSegment figSegment = (FigSegment) fig;

                        SegmentRecognitionStructure segmentRecognitionStructure =
                            new SegmentRecognitionStructure();
                        segmentRecognitionStructure.setBarcodeOneValue(barcodeOneValue);
                        segmentRecognitionStructure.setBarcodeTwoValue(barcodeOneValue + 1);

                        segmentRecognitionStructure.setPageNumber(pageNumber);

                        Map<String, String> recordMap = Misc.getRecordMap(record);

                        Document segment = Misc.getSelectedSegmentContainer(figSegment, recordMap);

                        if (segment == null) {
                            continue;
                        }

                        if (segment.getPages() == null) {
                            continue;
                        }

                        segmentRecognitionStructure
                            .setName(segment.getDocumentAttributes().getName());

                        for (Page page : segment.getPages().values()) {

                            segmentRecognitionStructure
                                .setWidth(page.getPageAttributes().getCroppedWidth());
                            segmentRecognitionStructure
                                .setHeight(page.getPageAttributes().getCroppedHeight());

                            for (Fig fsf : page.getFigs()) {
                                if (fsf instanceof RecognitionStructureFig) {
                                    RecognitionStructureFig rsf = (RecognitionStructureFig) fsf;
                                    rsf.addRecognitionStructure(segmentRecognitionStructure);
                                }
                            }
                        }

                        formRecognitionStructure
                            .addSegmentRecognitionStructure(segmentRecognitionStructure);
                        barcodeOneValue += 2;

                    }
                }
            }

        }

        return formRecognitionStructure;

    }

    public void publishForms(EntityManager entityManager, Document document,
        Publication publication, List<Record> records, MessageNotification messageNotification)
        throws Exception {

        if (publication.getPublicationType()
            == PublicationPreferences.FORM_ID_RECONCILE_WITH_SOURCE_DATA_RECORD) {

            // create all forms

            if (records == null || records.size() <= 0) {
                throw new FormPublisherException(
                    FormPublisherException.NO_SOURCE_DATA_RECORDS_TO_PUBLISH);
            }

            int i = 1;
            for (Record record : records) {
                if (isInterrupted()) {
                    throw new FormPublisherException(FormPublisherException.INTERRUPTED);
                }
                messageNotification.setMessage(String
                    .format(Localizer.localize("UI", "FormPublisherPublishingFormMessage"), i + "",
                        records.size() + ""));
                Form form = createFormRecord(entityManager, publication, record);

                FormRecognitionStructure formRecognitionStructure =
                    getFormRecognitionStructure(document, record);

                FormPage[] formPages = new FormPage[document.getNumberOfPages()];

                for (int j = 0; j < document.getNumberOfPages(); j++) {
                    formPages[j] = createFormPageRecord(entityManager, form, j + 1);
                }

                createRecognitionRecords(entityManager, form, formRecognitionStructure, formPages);

                i++;
            }

        } else {

            // create the template
            try {
                publishStatusDialog
                    .setMessage(Localizer.localize("UI", "FormPublisherPublishingTemplateMessage"));
                Form form = createFormRecord(entityManager, publication, null);

                FormRecognitionStructure formRecognitionStructure =
                    getFormRecognitionStructure(document, null);

                FormPage[] formPages = new FormPage[document.getNumberOfPages()];

                for (int j = 0; j < document.getNumberOfPages(); j++) {
                    formPages[j] = createFormPageRecord(entityManager, form, j + 1);
                }

                createRecognitionRecords(entityManager, form, formRecognitionStructure, formPages);

            } catch (Exception ex) {
                throw ex;
            }

        }

    }

    private Form createFormRecord(EntityManager entityManager, Publication publication,
        Record record) {

        Form form = new Form();
        form.setPublicationId(publication);

        if (this.getPublicationType()
            == PublicationPreferences.FORM_ID_RECONCILE_WITH_SOURCE_DATA_RECORD) {
            form.setRecordId(record);
        }

        form.setFormPassword(Misc.getFormPassword());
        entityManager.persist(form);
        entityManager.flush();
        return form;

    }

    public void startTransaction(EntityManager entityManager) throws Exception {
        try {
            entityManager.getTransaction().begin();
            entityManager.flush();
        } catch (Exception ex) {
            abortTransaction(entityManager);
            throw ex;
        }
    }

    public void endTransaction(EntityManager entityManager) throws Exception {
        try {
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            abortTransaction(entityManager);
            throw ex;
        }
    }

    public void abortTransaction(EntityManager entityManager) {
        if (entityManager.getTransaction().isActive()) {
            try {
                entityManager.getTransaction().rollback();
            } catch (Exception rbex) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(rbex);
            }
        }
    }

    public void createRecognitionRecords(EntityManager entityManager, Form form,
        FormRecognitionStructure formRecognitionStructure, FormPage[] formPages)
        throws FormPublisherException {

        // loop through the FormRecognitionStructure and create records
        for (FormPage formPage : formPages) {

            if (isInterrupted()) {
                throw new FormPublisherException(FormPublisherException.INTERRUPTED);
            }

            Map<Integer, SegmentRecognitionStructure> segmentRecognitionStructures =
                formRecognitionStructure.getSegmentRecognitionStructures();
            Iterator<SegmentRecognitionStructure> srs =
                segmentRecognitionStructures.values().iterator();
            while (srs.hasNext()) {

                SegmentRecognitionStructure segmentRecognitionStructure = srs.next();

                if (formPage.getFormPageNumber() != segmentRecognitionStructure.getPageNumber()) {
                    continue;
                }

                // create segment records
                Segment segment = new Segment();
                segment.setBarcodeOne(segmentRecognitionStructure.getBarcodeOneValue() + "");
                segment.setBarcodeTwo(segmentRecognitionStructure.getBarcodeTwoValue() + "");
                segment.setName(segmentRecognitionStructure.getName());
                segment.setFormPageId(formPage);
                entityManager.persist(segment);

                Map<String, OMRRecognitionStructure> OMRRecognitionStructures =
                    segmentRecognitionStructure.getOMRRecognitionStructures();
                Iterator<OMRRecognitionStructure> fitr =
                    OMRRecognitionStructures.values().iterator();
                while (fitr.hasNext()) {
                    OMRRecognitionStructure omrrs = fitr.next();

                    // create fragmentOmr records
                    FragmentOmr fragmentOmr = new FragmentOmr();
                    fragmentOmr.setSegmentId(segment);
                    fragmentOmr.setCapturedDataFieldName(omrrs.getFieldName());
                    fragmentOmr.setAggregationRule(omrrs.getAggregationRule());
                    fragmentOmr.setMarkColumnName(omrrs.getMarkFieldName());
                    fragmentOmr.setMarkOrderIndex(omrrs.getMarkOrderIndex());
                    fragmentOmr.setOrderIndex(omrrs.getOrderIndex());
                    fragmentOmr.setReadDirection((short) omrrs.getReadDirection());

                    fragmentOmr.setX1Percent(omrrs.getPercentX1());
                    fragmentOmr.setX2Percent(omrrs.getPercentX2());
                    fragmentOmr.setY1Percent(omrrs.getPercentY1());
                    fragmentOmr.setY2Percent(omrrs.getPercentY2());

                    if (omrrs.getCharacterData() != null) {
                        fragmentOmr.setCharacterData(omrrs.getCharacterData());
                    }

                    fragmentOmr.setCombineColumnCharacters(
                        omrrs.isCombineColumnCharacters() ? (short) 1 : (short) 0);
                    fragmentOmr
                        .setReconciliationKey(omrrs.isReconciliationKey() ? (short) 1 : (short) 0);

                    entityManager.persist(fragmentOmr);

                    ArrayList<CheckBoxRecognitionStructure> cbrsArray =
                        omrrs.getCheckBoxRecognitionStructures();
                    for (CheckBoxRecognitionStructure cbrs : cbrsArray) {
                        CheckBox cb = new CheckBox();
                        cb.setCheckBoxValue(cbrs.getCheckBoxValue());
                        cb.setColumnNumber(cbrs.getColumn());
                        cb.setRowNumber(cbrs.getRow());
                        cb.setFragmentXRatio(cbrs.getFragmentXRatio());
                        cb.setFragmentYRatio(cbrs.getFragmentYRatio());
                        cb.setFragmentOmrId(fragmentOmr);
                        entityManager.persist(cb);
                    }

                }

                Map<String, BarcodeRecognitionStructure> barcodeRecognitionStructures =
                    segmentRecognitionStructure.getBarcodeRecognitionStructures();
                Iterator<BarcodeRecognitionStructure> bitr =
                    barcodeRecognitionStructures.values().iterator();
                while (bitr.hasNext()) {
                    BarcodeRecognitionStructure bcrs = bitr.next();

                    // create fragmentBarcode records
                    FragmentBarcode fragmentBarcode = new FragmentBarcode();
                    fragmentBarcode.setSegmentId(segment);
                    fragmentBarcode.setCapturedDataFieldName(bcrs.getFieldName());
                    fragmentBarcode.setOrderIndex(bcrs.getOrderIndex());

                    fragmentBarcode.setX1Percent(bcrs.getPercentX1());
                    fragmentBarcode.setX2Percent(bcrs.getPercentX2());
                    fragmentBarcode.setY1Percent(bcrs.getPercentY1());
                    fragmentBarcode.setY2Percent(bcrs.getPercentY2());

                    fragmentBarcode.setBarcodeType((short) bcrs.getBarcodeType());

                    fragmentBarcode
                        .setReconciliationKey(bcrs.isReconciliationKey() ? (short) 1 : (short) 0);

                    entityManager.persist(fragmentBarcode);

                }

            }

        }

    }

    private FormPage createFormPageRecord(EntityManager entityManager, Form form, long pageNumber) {

        FormPage formPage = new FormPage();
        formPage.setFormId(form);
        formPage.setFormPageNumber(pageNumber);
        entityManager.persist(formPage);
        entityManager.flush();

        return formPage;
    }

    private void createGradingRecord(EntityManager entityManager, Publication publication,
        Document document) {

        if (document.getDocumentAttributes().getMarkingProperties() == null) {
            return;
        }

        MarkingProperties markingProperties =
            document.getDocumentAttributes().getMarkingProperties();

        if (markingProperties.getGradingRules().size() <= 0) {
            return;
        }

        Grading grading = new Grading();

        grading.setPublicationId(publication);
        grading.setTotalPossibleScore(markingProperties.getTotalPossibleScore());

        entityManager.persist(grading);
        entityManager.flush();

        int i = 0;

        for (MarkingRule gradingRule : markingProperties.getGradingRules()) {
            com.ebstrada.formreturn.manager.persistence.jpa.GradingRule rule =
                new com.ebstrada.formreturn.manager.persistence.jpa.GradingRule();

            rule.setGradingId(grading);
            rule.setQualifier((short) gradingRule.getQualifier());
            rule.setGrade(gradingRule.getGrade());
            rule.setOrderIndex(i);
            rule.setThreshold(gradingRule.getThreshold());
            rule.setThresholdType((short) gradingRule.getThresholdType());
            i++;

            entityManager.persist(rule);
            entityManager.flush();

        }

    }

    private Publication createPublicationRecord(EntityManager entityManager, DataSet dataSet) {

        Publication publication = new Publication();
        publication.setDataSetId(dataSet);
        publication.setPublicationName(publicationName);
        publication.setPublicationCreated(new Timestamp(System.currentTimeMillis()));

        publication.setDeskewThreshold(publicationRecognitionStructure.getDeskewThreshold());
        publication
            .setFragmentPadding((short) publicationRecognitionStructure.getFragmentPadding());
        publication
            .setLuminanceThreshold((short) publicationRecognitionStructure.getLuminanceCutOff());
        publication.setMarkThreshold((short) publicationRecognitionStructure.getMarkThreshold());
        publication
            .setPerformDeskew((short) (publicationRecognitionStructure.isPerformDeskew() ? 1 : 0));
        publication.setPublicationType((short) getPublicationType());

        publication.setScannedInOrder((short) (scannedInOrder ? 1 : 0));

        publication.setTemplateFile(templateFile);

        entityManager.persist(publication);
        entityManager.flush();

        this.publicationId = publication.getPublicationId();

        return publication;
    }

    public void collateToPDF(File outputFile) throws IOException, FormPublisherException {

        String[] files = exportDirectory.list();
        Arrays.sort(files, new NumberedFileComparator());

        PDFMergerUtility mergePdf = new PDFMergerUtility();

        String numberOfPDFFiles = files.length + "";

        int i = 1;
        for (String filename : files) {
            if (isInterrupted()) {
                removeWorkingFiles();
                throw new FormPublisherException(FormPublisherException.INTERRUPTED);
            }
            publishStatusDialog.setMessage(String
                .format(Localizer.localize("UI", "FormPublisherCollatingPDFPageMessage"), i + "",
                    numberOfPDFFiles));
            mergePdf.addSource(exportDirectory.getCanonicalPath() + File.separator + filename);
            ++i;
        }

        mergePdf.setDestinationFileName(outputFile.getCanonicalPath());
        MemoryUsageSetting mus = MemoryUsageSetting.setupTempFileOnly();
        mergePdf.mergeDocuments(mus);

        // remove files
        for (String filename : files) {
            File removeFile = new File(filename);
            if (removeFile.exists()) {
                removeFile.delete();
            }
        }

    }

    public void individualPDF(File outputDirectory) throws FormPublisherException, IOException {

        String[] files = exportDirectory.list();
        Arrays.sort(files, new NumberedFileComparator());

        String numberOfPDFFiles = files.length + "";

        int i = 1;
        for (String filename : files) {
            if (isInterrupted()) {
                removeWorkingFiles();
                throw new FormPublisherException(FormPublisherException.INTERRUPTED);
            }
            publishStatusDialog.setMessage(String
                .format(Localizer.localize("UI", "FormPublisherMovingPDFFileMessage"), i + "",
                    numberOfPDFFiles));
            File sourceFile =
                new File(exportDirectory.getCanonicalPath() + File.separator + filename);
            if (sourceFile.exists()) {
                File outputFile =
                    new File(outputDirectory.getCanonicalPath() + File.separator + filename);
                if (outputFile.exists()) {
                    FormPublisherException fpe =
                        new FormPublisherException(FormPublisherException.CANNOT_OVERWITE_FILE);
                    fpe.setOverwriteFilename(outputFile.getCanonicalPath());
                    throw fpe;
                }
                sourceFile.renameTo(outputFile);
            } else {
                FormPublisherException fpe =
                    new FormPublisherException(FormPublisherException.CANNOT_FIND_FILE);
                fpe.setUnfoundFilename(sourceFile.getCanonicalPath());
                throw fpe;
            }
            ++i;
        }

    }

    public int getPublicationType() {
        return publicationType;
    }

    public void setPublicationType(int publicationType) {
        this.publicationType = publicationType;
    }

    public int getCollation() {
        return collation;
    }

    public void setCollation(int collation) {
        this.collation = collation;
    }

    public void interrupt() {
        this.interrupted = true;
    }

    public boolean isInterrupted() {
        if (publishStatusDialog != null) {
            return publishStatusDialog.isInterrupted();
        }
        return this.interrupted;
    }

    public com.ebstrada.formreturn.api.messaging.MessageNotification getPublishStatusDialog() {
        return publishStatusDialog;
    }

    public void setPublishStatusDialog(
        com.ebstrada.formreturn.api.messaging.MessageNotification publishStatusDialog) {
        this.publishStatusDialog = publishStatusDialog;
    }

    public long getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(long publicationId) {
        this.publicationId = publicationId;
    }

    public void setScannedInOrder(boolean scannedInOrder) {
        this.scannedInOrder = scannedInOrder;
    }

}
