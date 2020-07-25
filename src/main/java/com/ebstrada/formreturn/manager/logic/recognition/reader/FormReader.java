package com.ebstrada.formreturn.manager.logic.recognition.reader;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.ebstrada.aggregation.exception.ErrorFlagException;
import com.ebstrada.aggregation.exception.InvalidRulePartException;
import com.ebstrada.aggregation.exception.NoMatchException;
import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.presentation.FigSegmentArea;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.structure.BarcodeRecognitionData;
import com.ebstrada.formreturn.manager.logic.recognition.structure.BarcodeRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.CheckBoxRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.FormRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.FragmentRecognitionData;
import com.ebstrada.formreturn.manager.logic.recognition.structure.OMRMatrix;
import com.ebstrada.formreturn.manager.logic.recognition.structure.OMRRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.PageRecognitionData;
import com.ebstrada.formreturn.manager.logic.recognition.structure.SegmentRecognitionData;
import com.ebstrada.formreturn.manager.logic.recognition.structure.SegmentRecognitionStructure;
import com.ebstrada.formreturn.manager.persistence.jpa.CheckBox;
import com.ebstrada.formreturn.manager.persistence.jpa.DataSet;
import com.ebstrada.formreturn.manager.persistence.jpa.Form;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentBarcode;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentOmr;
import com.ebstrada.formreturn.manager.persistence.jpa.Log;
import com.ebstrada.formreturn.manager.persistence.jpa.ProcessedImage;
import com.ebstrada.formreturn.manager.persistence.jpa.Publication;
import com.ebstrada.formreturn.manager.persistence.jpa.Record;
import com.ebstrada.formreturn.manager.persistence.jpa.Segment;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceField;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceText;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.image.ImageDeskew;
import com.ebstrada.formreturn.manager.util.image.ImageUtil;
import com.ebstrada.formreturn.manager.util.preferences.persistence.PublicationPreferences;
import com.ebstrada.formreturn.server.thread.FormProcessor;

public class FormReader {

    private BufferedImage sourceImage;

    private BarcodeReader barcodeReader;

    private Map<Integer, Rectangle2D> segmentBoundaries;

    private boolean drawDetectBarcode = false;
    private boolean drawDetectSegment = false;
    private boolean drawDetectfragment = false;
    private boolean drawDetectMarks = false;

    private double markThreshold = 1.2;
    private int fragmentPadding = 1;
    private short luminanceThreshold = 160;
    private short performDeskew = 1;
    private double deskewThreshold = 0.10;

    private double imageSkewAngle = 0.0d;

    private double rotationAngle = 0.0d;

    private Float overlayZoom = 1.0f;

    private FormRecognitionStructure formRecognitionStructure;

    private EntityManager entityManager;

    private FormPage formPage;

    private PageRecognitionData pageRecognitionData = new PageRecognitionData();

    private int publicationType = PublicationPreferences.FORM_ID_RECONCILE_WITH_SOURCE_DATA_RECORD;

    public static final int ORIENTATION_PORTRAIT = 1;
    public static final int ORIENTATION_PORTRAIT_FLIP = 2;
    public static final int ORIENTATION_LANDSCAPE = 3;
    public static final int ORIENTATION_LANDSCAPE_FLIP = 4;

    public static final int ORIENTATION_PORTRAIT_NEXT = ORIENTATION_PORTRAIT_FLIP;
    public static final int ORIENTATION_PORTRAIT_FLIP_NEXT = ORIENTATION_LANDSCAPE;
    public static final int ORIENTATION_LANDSCAPE_NEXT = ORIENTATION_LANDSCAPE_FLIP;
    public static final int ORIENTATION_LANDSCAPE_FLIP_NEXT = ORIENTATION_PORTRAIT;

    public static final double ORIENTATION_PORTRAIT_NEXT_ROTATE = 180;
    public static final double ORIENTATION_PORTRAIT_FLIP_NEXT_ROTATE = -90;
    public static final double ORIENTATION_LANDSCAPE_NEXT_ROTATE = 180;
    public static final double ORIENTATION_LANDSCAPE_FLIP_NEXT_ROTATE = 90;

    private static int lastOrientation = ORIENTATION_PORTRAIT;

    private Exception processingError;

    private String currentFilename;
    private ImageDeskew deskew;

    private boolean scannedInOrder;

    private boolean errorDuplicateScans;

    public Exception getProcessingError() {
        return processingError;
    }

    public FormReader(BufferedImage sourceImage, FormRecognitionStructure formRecognitionStructure,
        EntityManager entityManager, String currentFilename) {
        this.sourceImage = sourceImage;
        this.formRecognitionStructure = formRecognitionStructure;
        this.entityManager = entityManager;
        this.currentFilename = currentFilename;
        barcodeReader = new BarcodeReader(this.sourceImage, pageRecognitionData);
    }

    public FormReader(BufferedImage sourceImage, FormRecognitionStructure formRecognitionStructure,
        EntityManager entityManager, boolean previewOnly) {
        this.sourceImage = sourceImage;
        this.formRecognitionStructure = formRecognitionStructure;
        this.entityManager = entityManager;
        barcodeReader = new BarcodeReader(this.sourceImage, pageRecognitionData);
    }

    public FormReader(BufferedImage sourceImage, JGraph graph) {
        this.sourceImage = sourceImage;
        this.formRecognitionStructure = graph.getFormRecognitionStructure();
    }

    public boolean isErrorDuplicateScans() {
        return errorDuplicateScans;
    }

    public void setErrorDuplicateScans(boolean errorDuplicateScans) {
        this.errorDuplicateScans = errorDuplicateScans;
    }

    public void searchForBarcodes(BufferedImage image) throws FormReaderException {
        searchForBarcodes(image, true);
    }

    public void searchForBarcodes(BufferedImage image, boolean locateFormId)
        throws FormReaderException {
        int orientation = getLastOrientation();
        int recursionLevel = 0;
        searchForBarcodes(image, locateFormId, orientation, recursionLevel);
    }

    public void searchForBarcodes(BufferedImage image, boolean locateFormId, int orientation,
        int recursionLevel) throws FormReaderException {

        if (recursionLevel > 3) {
            if (locateFormId) {
                throw new FormReaderException(FormReaderException.MISSING_FORM_ID_BARCODE);
            } else {
                return;
            }
        }

        // if the last page orientation isn't portrait, rotate to that orientation
        // to start.
        if (recursionLevel == 0) {
            switch (orientation) {
                case ORIENTATION_PORTRAIT_FLIP:
                    image = ImageUtil.rotate(image, 180.0d, 0, 0);
                    rotationAngle += 180.0d;
                    break;
                case ORIENTATION_LANDSCAPE:
                    image = ImageUtil.rotate(image, 90.0d, 0, 0);
                    rotationAngle += 90.0d;
                    break;
                case ORIENTATION_LANDSCAPE_FLIP:
                    image = ImageUtil.rotate(image, 270.0d, 0, 0);
                    rotationAngle += 270.0d;
                    break;
            }
        }

        pageRecognitionData = new PageRecognitionData();
        barcodeReader = new BarcodeReader(image, pageRecognitionData);

        setLastOrientation(orientation);

        switch (orientation) {

            case ORIENTATION_PORTRAIT:
                try {
                    barcodeReader.process(locateFormId);
                } catch (Exception ex) {
                    image = ImageUtil.rotate(image, ORIENTATION_PORTRAIT_NEXT_ROTATE, 0, 0);
                    rotationAngle += ORIENTATION_PORTRAIT_NEXT_ROTATE;
                    searchForBarcodes(image, locateFormId, ORIENTATION_PORTRAIT_NEXT,
                        ++recursionLevel);
                }
                break;
            case ORIENTATION_PORTRAIT_FLIP:
                try {
                    barcodeReader.process(locateFormId);
                } catch (Exception ex) {
                    image = ImageUtil.rotate(image, ORIENTATION_PORTRAIT_FLIP_NEXT_ROTATE, 0, 0);
                    rotationAngle += ORIENTATION_PORTRAIT_FLIP_NEXT_ROTATE;
                    searchForBarcodes(image, locateFormId, ORIENTATION_PORTRAIT_FLIP_NEXT,
                        ++recursionLevel);
                }
                break;
            case ORIENTATION_LANDSCAPE:
                try {
                    barcodeReader.process(locateFormId);
                } catch (Exception ex) {
                    image = ImageUtil.rotate(image, ORIENTATION_LANDSCAPE_NEXT_ROTATE, 0, 0);
                    rotationAngle += ORIENTATION_LANDSCAPE_NEXT_ROTATE;
                    searchForBarcodes(image, locateFormId, ORIENTATION_LANDSCAPE_NEXT,
                        ++recursionLevel);
                }
                break;

            case ORIENTATION_LANDSCAPE_FLIP:
                try {
                    barcodeReader.process(locateFormId);
                } catch (Exception ex) {
                    image = ImageUtil.rotate(image, ORIENTATION_LANDSCAPE_FLIP_NEXT_ROTATE, 0, 0);
                    rotationAngle += ORIENTATION_LANDSCAPE_FLIP_NEXT_ROTATE;
                    searchForBarcodes(image, locateFormId, ORIENTATION_LANDSCAPE_FLIP_NEXT,
                        ++recursionLevel);
                }
                break;

        }

    }

    public BufferedImage registerForm(BufferedImage image) throws FormReaderException {
        return registerForm(image, -1);
    }

    public BufferedImage registerForm(BufferedImage image, long formPageId)
        throws FormReaderException {

        try {
            if (formPageId > 0) {
                searchForBarcodes(image, false);
            } else {
                searchForBarcodes(image, true);
            }
        } catch (FormReaderException ex) {
            try {
                // will deskew and despeckle the image to try find the barcodes
                rotationAngle = 0.0d;
                binarize(this.sourceImage, this.luminanceThreshold, true);
                image = deskew(this.sourceImage, this.deskewThreshold);
                if (formPageId > 0) {
                    searchForBarcodes(image, false);
                } else {
                    searchForBarcodes(image, true);
                }
            } catch (FormReaderException ex2) {
                throw ex2;
            }
        }

        formPage = null;

        if (formPageId <= 0) {

            if (pageRecognitionData.getFormIDBarcode() == null) {
                String msg = Localizer.localize("UI", "BarcodeReaderUnableToLocateFormIDMessage");
                throw new FormReaderException(FormReaderException.MISSING_FORM_ID_BARCODE, msg);
            }

            String formPageIdBarcode = pageRecognitionData.getFormIDBarcode().getValue();
            if (formPageIdBarcode.trim().length() <= 0) {
                String msg = Localizer.localize("UI", "BarcodeReaderUnableToLocateFormIDMessage");
                throw new FormReaderException(FormReaderException.MISSING_FORM_ID_BARCODE, msg);
            }

            String[] barcodeParts = formPageIdBarcode.split("-");
            if (barcodeParts.length == 2) {
                formPageId = Misc.parseLongString(barcodeParts[0]);
                long formPassword = Misc.parseLongString(barcodeParts[1]);
                formPage = entityManager.find(FormPage.class, formPageId);
                if (formPage != null) {
                    if (!(formPage.getFormId().getFormPassword().equals(formPassword + ""))) {
                        formPage = null;
                    }
                }
            }

        } else {
            formPage = entityManager.find(FormPage.class, formPageId);
        }

        if (formPage == null) {

            String msg = Localizer.localize("UI", "FormReaderInvalidFormPageBarcodeMessage");
            FormReaderException fre =
                new FormReaderException(FormReaderException.FORM_ID_NOT_FOUND, msg);
            fre.setMissingFormPageRecord(formRecognitionStructure.getFormPageID());
            throw fre;

        } else {

            Publication publication = formPage.getFormId().getPublicationId();

            // set recognition settings
            setMarkThreshold(publication.getMarkThreshold());
            setFragmentPadding(publication.getFragmentPadding());

            setPublicationType(publication.getPublicationType());

            setScannedInOrder(publication.getScannedInOrder() > 0 ? true : false);

            this.luminanceThreshold = publication.getLuminanceThreshold();
            this.performDeskew = publication.getPerformDeskew();
            this.deskewThreshold = publication.getDeskewThreshold();

            image = binarize(image, this.luminanceThreshold, false);
            if (rotationAngle > 0) {
                image = ImageUtil.rotate(image, rotationAngle, 0, 0);
            }
            if (this.performDeskew > 0) {
                if ((imageSkewAngle > this.deskewThreshold
                    || imageSkewAngle < -(this.deskewThreshold))) {
                    image = ImageUtil.rotate(image, -imageSkewAngle, image.getWidth() / 2,
                        image.getHeight() / 2);
                    imageSkewAngle = 0;
                    pageRecognitionData = new PageRecognitionData();
                    barcodeReader = new BarcodeReader(image, pageRecognitionData);
                    try {
                        barcodeReader.process(false);
                    } catch (Exception e) {
                        com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                    }
                }
            }

        }

        return image;

    }

    public void setScannedInOrder(boolean scannedInOrder) {
        this.scannedInOrder = scannedInOrder;
    }

    public boolean isScannedInOrder() {
        return scannedInOrder;
    }

    public BufferedImage deskew(BufferedImage image, double threshold) {

        BufferedImage newImage;

        if ((imageSkewAngle > threshold || imageSkewAngle < -(threshold))) {
            newImage = ImageUtil
                .rotate(image, -imageSkewAngle, image.getWidth() / 2, image.getHeight() / 2);
            imageSkewAngle = 0;
        } else {
            newImage = image;
        }

        return newImage;

    }

    public BufferedImage binarize(BufferedImage image, short luminanceThreshold,
        boolean despeckle) {

        deskew = new ImageDeskew(image, this.luminanceThreshold, despeckle);
        imageSkewAngle = 0;
        try {
            imageSkewAngle = deskew.getSkewAngle();
        } catch (Exception ex) {
            imageSkewAngle = 0;
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        }

        return deskew.getBinarizedImage();

    }

    public OMRRecognitionStructure getOMRRecognitionStructure(FragmentOmr fragmentOmr) {
        OMRRecognitionStructure omrrs = new OMRRecognitionStructure();
        omrrs.setAggregationRule(fragmentOmr.getAggregationRule());

        // NEW WAY - SINCE 1.0.8
        List<CheckBox> cbc = fragmentOmr.getCheckBoxCollection();
        if (cbc.size() > 0) {

            ArrayList<CheckBoxRecognitionStructure> cbrsArray =
                new ArrayList<CheckBoxRecognitionStructure>();

            int rowCount = 0;
            int columnCount = 0;

            for (CheckBox cb : cbc) {

                CheckBoxRecognitionStructure cbrs = new CheckBoxRecognitionStructure();

                cbrs.setFragmentXRatio(cb.getFragmentXRatio());
                cbrs.setFragmentYRatio(cb.getFragmentYRatio());
                cbrs.setRow(cb.getRowNumber());
                if (rowCount < (cb.getRowNumber() + 1)) {
                    rowCount = (cb.getRowNumber() + 1);
                }

                cbrs.setColumn(cb.getColumnNumber());
                if (columnCount < (cb.getColumnNumber() + 1)) {
                    columnCount = (cb.getColumnNumber() + 1);
                }

                cbrs.setCheckBoxValue(cb.getCheckBoxValue());

                cbrsArray.add(cbrs);

            }

            omrrs.setCheckBoxRecognitionStructures(cbrsArray);
            omrrs.setRowCount(rowCount);
            omrrs.setColumnCount(columnCount);

            // OLD WAY
        } else {
            omrrs.setCharacterData(fragmentOmr.getCharacterData());
        }

        omrrs.setReadDirection(fragmentOmr.getReadDirection());
        omrrs.setMarkFieldName(fragmentOmr.getMarkColumnName());
        omrrs.setMarkOrderIndex((int) fragmentOmr.getMarkOrderIndex());
        omrrs.setOrderIndex((int) fragmentOmr.getOrderIndex());
        omrrs.setPercentX1(fragmentOmr.getX1Percent());
        omrrs.setPercentX2(fragmentOmr.getX2Percent());
        omrrs.setPercentY1(fragmentOmr.getY1Percent());
        omrrs.setPercentY2(fragmentOmr.getY2Percent());
        omrrs.setFieldName(fragmentOmr.getCapturedDataFieldName());
        omrrs.setCombineColumnCharacters(
            (fragmentOmr.getCombineColumnCharacters() == 1) ? true : false);
        omrrs.setReconciliationKey((fragmentOmr.getReconciliationKey() == 1) ? true : false);
        return omrrs;
    }

    public BarcodeRecognitionStructure getBarcodeRecognitionStructure(
        FragmentBarcode fragmentBarcode) {
        BarcodeRecognitionStructure bcrs = new BarcodeRecognitionStructure();
        bcrs.setOrderIndex((int) fragmentBarcode.getOrderIndex());
        bcrs.setPercentX1(fragmentBarcode.getX1Percent());
        bcrs.setPercentX2(fragmentBarcode.getX2Percent());
        bcrs.setPercentY1(fragmentBarcode.getY1Percent());
        bcrs.setPercentY2(fragmentBarcode.getY2Percent());
        bcrs.setFieldName(fragmentBarcode.getCapturedDataFieldName());
        bcrs.setBarcodeType(fragmentBarcode.getBarcodeType());
        bcrs.setReconciliationKey((fragmentBarcode.getReconciliationKey() == 1) ? true : false);
        return bcrs;
    }

    public Form duplicateForm(long formId, long recordId) {

        Form originalForm = entityManager.find(Form.class, formId);

        Form newForm = new Form();
        newForm.setAggregateMark(originalForm.getAggregateMark());
        newForm.setFormPassword(originalForm.getFormPassword());
        Publication publication = entityManager
            .find(Publication.class, originalForm.getPublicationId().getPublicationId());
        newForm.setPublicationId(publication);
        Record record = entityManager.find(Record.class, recordId);
        newForm.setRecordId(record);

        entityManager.persist(newForm);
        entityManager.flush();

        List<FormPage> fpc = originalForm.getFormPageCollection();

        for (FormPage fp : fpc) {

            FormPage newFormPage = new FormPage();

            newFormPage.setCaptureTime(fp.getCaptureTime());
            newFormPage.setScannedPageNumber(fp.getScannedPageNumber());
            newFormPage.setAggregateMark(fp.getAggregateMark());
            newFormPage.setFormId(newForm);
            newFormPage.setFormPageNumber(fp.getFormPageNumber());

            entityManager.persist(newFormPage);
            entityManager.flush();

            List<Segment> sc = fp.getSegmentCollection();

            for (Segment segment : sc) {

                Segment newSegment = new Segment();

                newSegment.setBarcodeOne(segment.getBarcodeOne());
                newSegment.setBarcodeTwo(segment.getBarcodeTwo());
                newSegment.setFormPageId(newFormPage);

                entityManager.persist(newSegment);
                entityManager.flush();

                List<FragmentOmr> foc = segment.getFragmentOmrCollection();

                for (FragmentOmr fragmentOmr : foc) {

                    FragmentOmr newFragmentOmr = new FragmentOmr();

                    newFragmentOmr.setAggregationRule(fragmentOmr.getAggregationRule());
                    newFragmentOmr.setCapturedDataFieldName(fragmentOmr.getCapturedDataFieldName());
                    if (fragmentOmr.getCharacterData() != null) {
                        newFragmentOmr.setCharacterData(fragmentOmr.getCharacterData());
                    }
                    newFragmentOmr
                        .setCombineColumnCharacters(fragmentOmr.getCombineColumnCharacters());
                    newFragmentOmr.setMark(fragmentOmr.getMark());
                    newFragmentOmr.setReconciliationKey(fragmentOmr.getReconciliationKey());
                    newFragmentOmr.setSegmentId(newSegment);
                    newFragmentOmr.setX1Percent(fragmentOmr.getX1Percent());
                    newFragmentOmr.setX2Percent(fragmentOmr.getX2Percent());
                    newFragmentOmr.setY1Percent(fragmentOmr.getY1Percent());
                    newFragmentOmr.setY2Percent(fragmentOmr.getY2Percent());
                    newFragmentOmr.setMarkColumnName(fragmentOmr.getMarkColumnName());
                    newFragmentOmr.setOrderIndex(fragmentOmr.getOrderIndex());
                    newFragmentOmr.setMarkOrderIndex(fragmentOmr.getMarkOrderIndex());
                    newFragmentOmr.setReadDirection((short) fragmentOmr.getReadDirection());

                    entityManager.persist(newFragmentOmr);
                    entityManager.flush();

                    List<CheckBox> cbc = fragmentOmr.getCheckBoxCollection();
                    if (cbc.size() > 0) {
                        for (CheckBox cb : cbc) {

                            CheckBox newCb = new CheckBox();

                            newCb.setCheckBoxValue(cb.getCheckBoxValue());
                            newCb.setColumnNumber(cb.getColumnNumber());
                            newCb.setRowNumber(cb.getRowNumber());
                            newCb.setFragmentXRatio(cb.getFragmentXRatio());
                            newCb.setFragmentYRatio(cb.getFragmentYRatio());
                            newCb.setFragmentOmrId(newFragmentOmr);

                            entityManager.persist(newCb);
                            entityManager.flush();

                        }
                    }

                }

                List<FragmentBarcode> fbc = segment.getFragmentBarcodeCollection();

                for (FragmentBarcode fragmentBarcode : fbc) {

                    FragmentBarcode newFragmentBarcode = new FragmentBarcode();

                    newFragmentBarcode
                        .setCapturedDataFieldName(fragmentBarcode.getCapturedDataFieldName());
                    newFragmentBarcode.setReconciliationKey(fragmentBarcode.getReconciliationKey());
                    newFragmentBarcode.setSegmentId(newSegment);
                    newFragmentBarcode.setX1Percent(fragmentBarcode.getX1Percent());
                    newFragmentBarcode.setX2Percent(fragmentBarcode.getX2Percent());
                    newFragmentBarcode.setY1Percent(fragmentBarcode.getY1Percent());
                    newFragmentBarcode.setY2Percent(fragmentBarcode.getY2Percent());
                    newFragmentBarcode.setBarcodeType(fragmentBarcode.getBarcodeType());
                    newFragmentBarcode.setOrderIndex(fragmentBarcode.getOrderIndex());

                    entityManager.persist(newFragmentBarcode);
                    entityManager.flush();

                }

            }

        }

        return newForm;
    }

    private Record createNewRecord(DataSet dataSetId, Map<String, String> matchMap) {
        Record record = new Record();
        record.setDataSetId(dataSetId);
        record.setRecordCreated(new Timestamp(System.currentTimeMillis()));
        record.setRecordModified(new Timestamp(System.currentTimeMillis()));
        entityManager.persist(record);
        entityManager.flush();

        Set<String> fieldnames = matchMap.keySet();

        // create empty field name records
        for (SourceField sf : dataSetId.getSourceFieldCollection()) {
            if (sf.getSourceFieldType().equals("STRING")) {
                SourceText st = new SourceText();
                st.setRecordId(record);
                st.setSourceFieldId(sf);

                // either set the reconciliation key data, or blank data
                if (fieldnames.contains(sf.getSourceFieldName())) {
                    String keydata = matchMap.get(sf.getSourceFieldName());
                    if (keydata == null) {
                        keydata = "";
                    }
                    st.setSourceTextString(keydata);
                } else {
                    st.setSourceTextString("");
                }
                entityManager.persist(st);
            }
        }
        entityManager.flush();

        return record;
    }

    public void processTemplate(BufferedImage image, Timestamp capturedTimestamp,
        int scannedPageNumber) throws FormReaderException {

        if (formPage == null) {
            String msg = Localizer.localize("UI", "FormReaderNoFormPageLoadedMessage");
            FormReaderException fre =
                new FormReaderException(FormReaderException.FORM_PAGE_RECORD_MISSING, msg);
            fre.setMissingFormPageRecord(formRecognitionStructure.getFormPageID());
            throw fre;
        }

        calculateSegmentBoundaries();

        // get a map of fieldnames => values to match
        Map<String, String> matchMap = new HashMap<String, String>();

        if (segmentBoundaries != null) {
            Iterator<Segment> si = formPage.getSegmentCollection().iterator();
            while (si.hasNext()) {
                Segment segment = si.next();
                int barcodeValue = Misc.parseIntegerString(segment.getBarcodeOne());
                Rectangle2D segmentBoundary = segmentBoundaries.get(barcodeValue);
                if (segmentBoundary != null) {

                    List<FragmentOmr> foc = segment.getFragmentOmrCollection();
                    if (foc != null && foc.size() > 0) {
                        for (FragmentOmr fragmentOmr : foc) {
                            // if is reconciliation key
                            if (fragmentOmr.getReconciliationKey() > 0) {
                                OMRRecognitionStructure omrrs =
                                    getOMRRecognitionStructure(fragmentOmr);
                                Rectangle2D fragmentBoundary =
                                    omrrs.getRecognitionArea(segmentBoundary, fragmentPadding);
                                BufferedImage fragmentImage =
                                    getFragmentImage(image, fragmentBoundary);
                                OMRMatrix omrMatrix =
                                    new OMRMatrix(omrrs, fragmentImage, markThreshold);
                                omrMatrix.process();
                                matchMap.put(omrrs.getFieldName(), omrMatrix.getCapturedString());
                            }
                        }
                    }

                    List<FragmentBarcode> fob = segment.getFragmentBarcodeCollection();
                    if (fob != null && fob.size() > 0) {
                        for (FragmentBarcode fragmentBarcode : fob) {
                            // if is reconciliation key
                            if (fragmentBarcode.getReconciliationKey() > 0) {
                                BarcodeRecognitionStructure bcrs =
                                    getBarcodeRecognitionStructure(fragmentBarcode);
                                Rectangle2D fragmentBoundary =
                                    bcrs.getRecognitionArea(segmentBoundary, fragmentPadding);
                                BufferedImage fragmentImage =
                                    getFragmentImage(image, fragmentBoundary);
                                BarcodeImageReader barcodeImageReader =
                                    new BarcodeImageReader(bcrs, fragmentImage);
                                matchMap
                                    .put(bcrs.getFieldName(), barcodeImageReader.getBarcodeValue());
                            }
                        }
                    }

                }
            }
        }

        ArrayList<String> nonMatchingFields = new ArrayList<String>();
        List<SourceField> matchingSourceFields = new ArrayList<SourceField>();
        List<SourceField> sfc =
            formPage.getFormId().getPublicationId().getDataSetId().getSourceFieldCollection();
        for (SourceField sourceField : sfc) {
            String sourceFieldName = sourceField.getSourceFieldName();
            if (matchMap.keySet().contains(sourceFieldName)) {
                matchingSourceFields.add(sourceField);
            } else {
                nonMatchingFields.add(sourceFieldName);
            }
        }

        HashMap<SourceField, String> fields = new HashMap<SourceField, String>();

        for (SourceField sourceField : matchingSourceFields) {

            String matchingValue = matchMap.get(sourceField.getSourceFieldName());

            // don't match any values that are null
            if (matchingValue == null || matchingValue.trim().length() <= 0) {
                continue;
            }

            if (!(fields.containsKey(sourceField))) {
                fields.put(sourceField, matchingValue.trim());
            }

        }

        Record matchingRecord = null;

        if (matchMap.size() > 0 && fields.size() > 0 && fields.size() >= matchMap.size()) {

            String select = "SELECT st1.RECORD_ID ";
            String from = "FROM SOURCE_TEXT st1 ";
            String where = "WHERE st1.SOURCE_TEXT_STRING = ";

            int i = 1;
            for (SourceField sourceField : fields.keySet()) {

                String matchingValue = fields.get(sourceField);

                if (i == 1) {
                    where += "'" + matchingValue + "' AND st1.SOURCE_FIELD_ID = " + sourceField
                        .getSourceFieldId() + " ";
                }

                if (i > 1) {
                    from += "INNER JOIN SOURCE_TEXT st" + i + " ON st1.RECORD_ID = st" + i
                        + ".RECORD_ID ";
                    where += "AND st" + i + ".SOURCE_TEXT_STRING = '" + matchingValue
                        + "' AND st1.SOURCE_FIELD_ID = " + sourceField.getSourceFieldId() + " ";
                }

                ++i;

            }

            String sql = select + from + where.trim();

            Query recordIdQuery = entityManager.createNativeQuery(sql);
            List<Long> recordIds = recordIdQuery.getResultList();

            if (recordIds != null && recordIds.size() > 0) {
                matchingRecord = entityManager.find(Record.class, recordIds.get(0));
            }

        }

        // matching record was found
        if (matchingRecord != null) {

            // if this record has no form, duplicate
            Collection<Form> fc = matchingRecord.getFormCollection();
            Iterator<Form> fci = fc.iterator();
            long pageNumber = formPage.getFormPageNumber();

            // MAKE SURE PUBLICATION NUMBERS MATCH SO THERE ARE NO CROSSED PUBLICATIONS
            long publicationId = formPage.getFormId().getPublicationId().getPublicationId();

            Form newForm = null;
            if (fci != null) {
                while (fci.hasNext()) {
                    Form nextForm = fci.next();
                    if (nextForm.getPublicationId().getPublicationId() == publicationId) {
                        newForm = nextForm;
                        break;
                    }
                }
            }

            if (newForm != null) {
                long newFormId = newForm.getFormId();
                entityManager.getTransaction().commit();
                entityManager.clear();
                entityManager.getTransaction().begin();
                newForm = entityManager.find(Form.class, newFormId);
            } else {
                newForm =
                    duplicateForm(formPage.getFormId().getFormId(), matchingRecord.getRecordId());
                long newFormId = newForm.getFormId();
                entityManager.getTransaction().commit();
                entityManager.clear();
                entityManager.getTransaction().begin();
                newForm = entityManager.find(Form.class, newFormId);
            }

            // get the page form the loaded form
            List<FormPage> fpc = newForm.getFormPageCollection();
            for (FormPage fp : fpc) {
                if (fp.getFormPageNumber() == pageNumber) {
                    formPage = fp;
                    processForm(image, capturedTimestamp, scannedPageNumber);
                    break;
                }
            }

            // matching record wasn't found
        } else {

            if (this.getPublicationType()
                == PublicationPreferences.RECONCILE_KEY_WITH_SOURCE_DATA_RECORD_CREATE_NEW) {

                long pageNumber = formPage.getFormPageNumber();

                // check for scanned in order reconciliations
                if (matchMap.size() <= 0 && isScannedInOrder()
                    && FormProcessor.lastFormIdProcessed > 0) {

                    Form newForm =
                        entityManager.find(Form.class, FormProcessor.lastFormIdProcessed);

                    List<FormPage> fpc = newForm.getFormPageCollection();
                    for (FormPage fp : fpc) {
                        if (fp.getFormPageNumber() == pageNumber) {
                            formPage = fp;
                            if (fpc.size() == pageNumber) {
                                FormProcessor.lastFormIdProcessed = 0;
                            }
                            processForm(image, capturedTimestamp, scannedPageNumber);
                            if (fpc.size() == pageNumber) {
                                FormProcessor.lastFormIdProcessed = 0;
                            }
                            break;
                        }
                    }

                    // default which requires key field on each page
                } else {

                    // create new source data record
                    Record record =
                        createNewRecord(formPage.getFormId().getPublicationId().getDataSetId(),
                            matchMap);

                    // duplicate form
                    Form newForm =
                        this.duplicateForm(formPage.getFormId().getFormId(), record.getRecordId());
                    long newFormId = newForm.getFormId();
                    entityManager.getTransaction().commit();
                    entityManager.clear();
                    entityManager.getTransaction().begin();

                    newForm = entityManager.find(Form.class, newFormId);

                    // get the page from the loaded form
                    List<FormPage> fpc = newForm.getFormPageCollection();
                    for (FormPage fp : fpc) {
                        if (fp.getFormPageNumber() == pageNumber) {
                            formPage = fp;
                            processForm(image, capturedTimestamp, scannedPageNumber);
                            break;
                        }
                    }

                }

            } else {

                long pageNumber = formPage.getFormPageNumber();

                // check for scanned in order reconciliations
                if (matchMap.size() <= 0 && isScannedInOrder()
                    && FormProcessor.lastFormIdProcessed > 0) {

                    Form newForm =
                        entityManager.find(Form.class, FormProcessor.lastFormIdProcessed);

                    List<FormPage> fpc = newForm.getFormPageCollection();
                    for (FormPage fp : fpc) {
                        if (fp.getFormPageNumber() == pageNumber) {
                            formPage = fp;
                            // dirty dirty code...
                            if (fpc.size() == pageNumber) {
                                FormProcessor.lastFormIdProcessed = 0;
                            }
                            processForm(image, capturedTimestamp, scannedPageNumber);
                            if (fpc.size() == pageNumber) {
                                FormProcessor.lastFormIdProcessed = 0;
                            }
                            break;
                        }
                    }

                    // default which requires key field on each page
                } else {

                    String msg = Localizer
                        .localize("UI", "FormReaderUnableToIdentifyReconciliationKeyMessage");
                    FormReaderException fre =
                        new FormReaderException(FormReaderException.RECONCILIATION_KEY_NOT_FOUND,
                            msg);

                    String nonMatchingFieldsStr = "";
                    for (String nonMatchingField : nonMatchingFields) {
                        nonMatchingFieldsStr += nonMatchingField;
                        nonMatchingFieldsStr += " ";
                    }
                    fre.setReconciliationKeyNotFound(nonMatchingFieldsStr);
                    throw fre;

                }

            }

        }


    }

    public ArrayList<FigSegmentArea> reprocessForm() throws FormReaderException {

        if (formPage == null) {
            String msg = Localizer.localize("UI", "FormReaderNoFormPageLoadedMessage");
            FormReaderException fre =
                new FormReaderException(FormReaderException.FORM_PAGE_RECORD_MISSING, msg);
            fre.setMissingFormPageRecord(formRecognitionStructure.getFormPageID());
            throw fre;
        }

        ArrayList<FigSegmentArea> figSegmentAreas = new ArrayList<FigSegmentArea>();

        calculateSegmentBoundaries();

        if (segmentBoundaries != null) {

            for (Segment segment : formPage.getSegmentCollection()) {

                int barcodeValue = Misc.parseIntegerString(segment.getBarcodeOne());

                Rectangle2D segmentBoundary = segmentBoundaries.get(barcodeValue);

                if (segmentBoundary != null) {
                    FigSegmentArea figSegmentArea = new FigSegmentArea((int) segmentBoundary.getX(),
                        (int) segmentBoundary.getY(), (int) segmentBoundary.getWidth(),
                        (int) segmentBoundary.getHeight());
                    figSegmentArea.setSegmentId(segment.getSegmentId());
                    figSegmentAreas.add(figSegmentArea);
                }

            }
        }

        return figSegmentAreas;

    }

    public void resetCheckBoxRecords(FragmentOmr fragmentOmr) {
        List<CheckBox> cbc = fragmentOmr.getCheckBoxCollection();
        if (cbc.size() > 0) {
            for (CheckBox cb : cbc) {
                cb.setCheckBoxMarked((short) 0);
                entityManager.persist(cb);
            }
        } else {
            if (fragmentOmr.getCharacterData() != null) {
                // create a new checkbox set based on fragmentOmr.getCharacterData()
                String[][] capturedDataArr = fragmentOmr.getCharacterData();
                for (int row = 0; row < capturedDataArr.length; ++row) {
                    for (int column = 0; column < capturedDataArr[0].length; ++column) {
                        CheckBox cb = new CheckBox();
                        cb.setFragmentOmrId(fragmentOmr);
                        cb.setFragmentXRatio(0.0d);
                        cb.setFragmentYRatio(0.0d);
                        cb.setCheckBoxValue(capturedDataArr[row][column]);
                        cb.setRowNumber((short) row);
                        cb.setColumnNumber((short) column);
                        cb.setCheckBoxMarked((short) 0);
                        entityManager.persist(cb);
                    }
                }
            }
        }
    }

    public boolean processForm(BufferedImage image, Timestamp capturedTimestamp,
        int scannedPageNumber) throws FormReaderException {

        if (formPage == null) {
            String msg = Localizer.localize("UI", "FormReaderNoFormPageLoadedMessage");
            FormReaderException fre =
                new FormReaderException(FormReaderException.FORM_PAGE_RECORD_MISSING, msg);
            fre.setMissingFormPageRecord(formRecognitionStructure.getFormPageID());
            throw fre;
        } else if (formPage.getProcessedTime() != null) {

            if (isErrorDuplicateScans()) {
                String msg = Localizer.localize("UI", "FormReaderErrorDuplicateScansMessage");
                FormReaderException fre =
                    new FormReaderException(FormReaderException.FORM_PAGE_DUPLICATE_SCAN, msg);
                throw fre;
            }

        }

        double pageAggregate = 0.0d;
        long errorAggregate = 0;

        calculateSegmentBoundaries();

        if (segmentBoundaries != null) {

            double segmentAggregate = 0.0d;

            for (Segment segment : formPage.getSegmentCollection()) {

                segmentAggregate = 0.0d;

                int barcodeValue = Misc.parseIntegerString(segment.getBarcodeOne());

                Rectangle2D segmentBoundary = segmentBoundaries.get(barcodeValue);

                if (segmentBoundary != null) {

                    for (FragmentOmr fragmentOmr : segment.getFragmentOmrCollection()) {

                        Vector<String> capturedData = new Vector<String>();

                        OMRRecognitionStructure omrrs = getOMRRecognitionStructure(fragmentOmr);
                        Rectangle2D fragmentBoundary =
                            omrrs.getRecognitionArea(segmentBoundary, fragmentPadding);
                        BufferedImage fragmentImage = getFragmentImage(image, fragmentBoundary);

                        OMRMatrix omrMatrix = null;
                        boolean isError = false;
                        try {
                            omrMatrix = new OMRMatrix(omrrs, fragmentImage, markThreshold);
                            omrMatrix.process();
                            capturedData = omrMatrix.getCapturedData();
                        } catch (FormReaderException fre) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(fre);
                            if (omrrs.isReconciliationKey()) {
                                throw fre;
                            }
                            capturedData = new Vector<String>();
                            fragmentOmr.setInvalidated((short) 1);
                            fragmentOmr.setErrorType((short) fre.getError());
                            resetCheckBoxRecords(fragmentOmr);
                            ++errorAggregate;
                            isError = true;
                        } catch (Exception ex) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                            if (omrrs.isReconciliationKey()) {
                                FormReaderException fre =
                                    new FormReaderException(FormReaderException.UNSPECIFIED);
                                throw fre;
                            }
                            capturedData = new Vector<String>();
                            fragmentOmr.setInvalidated((short) 1);
                            fragmentOmr.setErrorType((short) FormReaderException.UNSPECIFIED);
                            resetCheckBoxRecords(fragmentOmr);
                            ++errorAggregate;
                            isError = true;
                        }

                        if (!isError && capturedData == null) {
                            capturedData = new Vector<String>();
                        }

                        if (!isError) {
                            String[] capturedDataStrArray = new String[capturedData.size()];
                            if (capturedData.size() > 0) {
                                for (int i = 0; i < capturedData.size(); i++) {
                                    capturedDataStrArray[i] = capturedData.get(i);
                                }
                            }

                            double mark = 0.0d;

                            try {
                                if (!isError && omrMatrix != null) {
                                    if (omrrs.isReconciliationKey() || omrrs
                                        .isCombineColumnCharacters()) {
                                        String capturedString = omrMatrix.getCapturedString();
                                        fragmentOmr.setCapturedString(capturedString);
                                        try {
                                            mark = Misc.aggregate(0, new String[] {capturedString},
                                                fragmentOmr.getAggregationRule());
                                        } catch (NoMatchException e) {
                                            mark = 0.0d;
                                        }
                                    } else {
                                        try {
                                            mark = Misc.aggregate(0, capturedDataStrArray,
                                                fragmentOmr.getAggregationRule());
                                        } catch (NoMatchException e) {
                                            mark = 0.0d;
                                        }
                                    }
                                } else {
                                    try {
                                        mark = Misc.aggregate(0, capturedDataStrArray,
                                            fragmentOmr.getAggregationRule());
                                    } catch (NoMatchException e) {
                                        mark = 0.0d;
                                    }
                                }

                                pageAggregate += mark;
                                segmentAggregate += mark;

                                fragmentOmr.setMark(mark);

                                fragmentOmr.setCapturedData(null);
                                fragmentOmr.setInvalidated((short) 0);
                                fragmentOmr.setErrorType((short) -1);

                                omrMatrix.applyMarkValuesToFragmentOmr(fragmentOmr, entityManager);

                            } catch (InvalidRulePartException e) {
                                FormReaderException fre = new FormReaderException(
                                    FormReaderException.INVALID_AGGREGATION_RULE);
                                fragmentOmr.setInvalidated((short) 1);
                                fragmentOmr.setErrorType((short) fre.getError());
                                resetCheckBoxRecords(fragmentOmr);
                                ++errorAggregate;
                            } catch (ErrorFlagException e) {
                                FormReaderException fre = new FormReaderException(
                                    FormReaderException.ERROR_CONDITION_MET);
                                fragmentOmr.setInvalidated((short) 1);
                                fragmentOmr.setErrorType((short) fre.getError());
                                resetCheckBoxRecords(fragmentOmr);
                                ++errorAggregate;
                            }

                        }

                        entityManager.persist(fragmentOmr);

                    }

                    Iterator<FragmentBarcode> ifb =
                        segment.getFragmentBarcodeCollection().iterator();
                    while (ifb.hasNext()) {

                        FragmentBarcode fragmentBarcode = ifb.next();

                        BarcodeRecognitionStructure bcrs =
                            getBarcodeRecognitionStructure(fragmentBarcode);
                        Rectangle2D fragmentBoundary =
                            bcrs.getRecognitionArea(segmentBoundary, fragmentPadding);
                        BufferedImage fragmentImage = getFragmentImage(image, fragmentBoundary);
                        BarcodeImageReader barcodeImageReader =
                            new BarcodeImageReader(bcrs, fragmentImage);
                        fragmentBarcode.setBarcodeValue(barcodeImageReader.getBarcodeValue());

                        entityManager.persist(fragmentBarcode);

                    }


                }

                segment.setAggregateMark(segmentAggregate);
                entityManager.persist(segment);

            }
        }

        byte[] binarizedImageData = null;
        try {
            binarizedImageData = ImageUtil.getPNGByteArray(image);
        } catch (IOException e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }

        // remove all other formpage images that may be attached...
        for (ProcessedImage pi : formPage.getProcessedImageCollection()) {
            entityManager.remove(pi);
        }
        entityManager.flush();

        if (binarizedImageData != null) {

            ProcessedImage processedImage = new ProcessedImage();
            processedImage.setProcessedImageName(currentFilename);
            processedImage.setProcessedImageData(binarizedImageData);
            processedImage.setFormPageId(formPage);
            entityManager.persist(processedImage);
            entityManager.flush();

        }

        formPage.setCaptureTime(capturedTimestamp);
        formPage.setScannedPageNumber(scannedPageNumber);
        formPage.setAggregateMark(pageAggregate);
        formPage.setErrorCount(errorAggregate);
        formPage.setProcessedTime(new Timestamp(System.currentTimeMillis()));
        entityManager.persist(formPage);
        entityManager.flush();

        recalculateAggregatesForForm();

        if (isScannedInOrder()) {
            FormProcessor.lastFormIdProcessed = formPage.getFormId().getFormId();
        }

        return true;

    }

    private BufferedImage getFragmentImage(BufferedImage image, Rectangle2D fragmentBoundary)
        throws FormReaderException {

        int x = (int) fragmentBoundary.getX();

        // check low point
        if (x < 0) {
            x = 0;
        }

        int y = (int) fragmentBoundary.getY();

        // check low point
        if (y < 0) {
            y = 0;
        }

        // check high point

        int width = (int) fragmentBoundary.getWidth();
        int height = (int) fragmentBoundary.getHeight();

        if (width == 0) {
            FormReaderException fre =
                new FormReaderException(FormReaderException.MISSING_SEGMENT_BARCODE,
                    Localizer.localize("UI", "FormReaderFragmentWidthZeroErrorMessage"));
            throw fre;
        }

        if (height == 0) {
            throw new FormReaderException(FormReaderException.MISSING_SEGMENT_BARCODE,
                Localizer.localize("UI", "FormReaderFragmentHeightZeroErrorMessage"));
        }

        int x2 = x + width;
        int y2 = y + height;

        if (x2 > image.getWidth()) {
            width = image.getWidth() - x;
        }

        if (y2 > image.getHeight()) {
            height = image.getHeight() - y;
        }

        BufferedImage subImage = null;

        try {
            subImage = image.getSubimage(x, y, width, height);
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        }

        return subImage;
    }

    private void recalculateAggregatesForForm() {

        Form form = formPage.getFormId();
        Iterator<FormPage> fpi = form.getFormPageCollection().iterator();

        double formAggregate = 0;
        long errorAggregate = 0;
        while (fpi.hasNext()) {
            FormPage formPage = fpi.next();
            formAggregate += formPage.getAggregateMark();
            errorAggregate += formPage.getErrorCount();
        }

        form.setAggregateMark(formAggregate);
        form.setErrorCount(errorAggregate);
        entityManager.persist(form);
        entityManager.flush();

    }

    public BufferedImage processPreview(BufferedImage image, boolean isPerformDeskew,
        boolean despeckle) throws FormReaderException {

        processingError = null;

        try {

            barcodeReader = new BarcodeReader(image, pageRecognitionData);

            searchForBarcodes(image, false);

            image = binarize(image, this.luminanceThreshold, despeckle);

            if (rotationAngle > 0) {
                image = ImageUtil.rotate(image, rotationAngle, 0, 0);
            }

            if (isPerformDeskew) {
                if ((imageSkewAngle > this.deskewThreshold
                    || imageSkewAngle < -(this.deskewThreshold))) {
                    image = ImageUtil.rotate(image, -imageSkewAngle, image.getWidth() / 2,
                        image.getHeight() / 2);
                    imageSkewAngle = 0;
                    pageRecognitionData = new PageRecognitionData();
                    barcodeReader = new BarcodeReader(image, pageRecognitionData);
                    try {
                        barcodeReader.process(false);
                    } catch (Exception e) {
                        com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                    }
                }
            }

            calculateSegmentBoundaries();
            processRecognitionStructure(image);

        } catch (FormReaderException fre) {
            fre.setInvalidImage(image);
            throw fre;
        }

        return image;

    }

    public Float getOverlayZoom() {
        return overlayZoom;
    }

    public void setOverlayZoom(Float overlayZoom) {
        this.overlayZoom = overlayZoom;
        barcodeReader.setOverlayZoom(overlayZoom);
    }

    public void setMarkThreshold(double _markThreshold) {
        markThreshold = 1 + (_markThreshold / 100.0);
    }

    public double getMarkThreshold() {
        return ((markThreshold * 100.0d) - 100.0d);
    }

    public void setFragmentPadding(int _fragmentPadding) {
        fragmentPadding = _fragmentPadding;
    }

    public int getFragmentPadding() {
        return this.fragmentPadding;
    }

    public boolean isDrawDetectBarcode() {
        return drawDetectBarcode;
    }

    public void setDrawDetectBarcode(boolean drawDetectBarcode) {
        this.drawDetectBarcode = drawDetectBarcode;
    }

    public boolean isDrawDetectSegment() {
        return drawDetectSegment;
    }

    public void setDrawDetectSegment(boolean drawDetectSegment) {
        this.drawDetectSegment = drawDetectSegment;
    }

    public boolean isDrawDetectfragment() {
        return drawDetectfragment;
    }

    public void setDrawDetectfragment(boolean drawDetectfragment) {
        this.drawDetectfragment = drawDetectfragment;
    }

    public boolean isDrawDetectMarks() {
        return drawDetectMarks;
    }

    public void setDrawDetectMarks(boolean drawDetectMarks) {
        this.drawDetectMarks = drawDetectMarks;
    }

    public void calculateSegmentBoundaries() throws FormReaderException {

        segmentBoundaries = new HashMap<Integer, Rectangle2D>();

        for (Integer barcodeValue : pageRecognitionData.getSegmentBarcodes().keySet()) {

            Rectangle2D barcodeBoundary =
                pageRecognitionData.getSegmentBarcodes().get(barcodeValue).getBarcodeBoundary();

            if ((barcodeValue) % 2.0 > 0.0) {
                segmentBoundaries.put(barcodeValue, barcodeBoundary);
                if (!(pageRecognitionData.getSegmentBarcodes().keySet()
                    .contains(barcodeValue + 1))) {
                    FormReaderException fre =
                        new FormReaderException(FormReaderException.MISSING_SEGMENT_BARCODE);
                    fre.setMissingBarcodeData((barcodeValue + 1) + "");
                    throw fre;
                }
            } else {
                if (segmentBoundaries.get(barcodeValue - 1) != null) {
                    double x1 = barcodeBoundary.getX();
                    double y1 = segmentBoundaries.get(barcodeValue - 1).getY();
                    double x2 = segmentBoundaries.get(barcodeValue - 1).getX() + segmentBoundaries
                        .get(barcodeValue - 1).getWidth();
                    double y2 = barcodeBoundary.getY() + barcodeBoundary.getHeight();
                    segmentBoundaries
                        .put((barcodeValue - 1), new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1));
                } else {
                    FormReaderException fre =
                        new FormReaderException(FormReaderException.MISSING_SEGMENT_BARCODE);
                    fre.setMissingBarcodeData((barcodeValue - 1) + "");
                    throw fre;
                }
            }

        }

    }

    public Map<Integer, Rectangle2D> getSegmentBoundaries() {
        return this.segmentBoundaries;
    }


    public FormRecognitionStructure getFormRecognitionStructure() {
        return this.formRecognitionStructure;
    }

    public void processRecognitionStructure(BufferedImage image) throws FormReaderException {

        FormReaderException fre2 = null;

        if (segmentBoundaries != null) {

            Map<Integer, SegmentRecognitionStructure> segmentRecognitionStructures =
                getFormRecognitionStructure().getSegmentRecognitionStructures();

            if (segmentRecognitionStructures != null) {

                // find the lowest barcode in the segment boundaries
                // this will get around the multiple page issue
                int lowestBoundaryValue = Integer.MAX_VALUE;
                for (int barcodeValue : segmentBoundaries.keySet()) {
                    if (barcodeValue < lowestBoundaryValue) {
                        lowestBoundaryValue = barcodeValue;
                    }
                }

                int lowestSegmentRecognitionStructureValue = Integer.MAX_VALUE;
                for (int barcodeValue : segmentRecognitionStructures.keySet()) {
                    if (barcodeValue < lowestSegmentRecognitionStructureValue) {
                        lowestSegmentRecognitionStructureValue = barcodeValue;
                    }
                }

                int offset = lowestBoundaryValue - lowestSegmentRecognitionStructureValue;

                for (int barcodeValue : segmentBoundaries.keySet()) {
                    Rectangle2D segmentBoundary = segmentBoundaries.get(barcodeValue);

                    if (segmentBoundary != null) {

                        SegmentRecognitionData segmentRecognitionData =
                            new SegmentRecognitionData();
                        segmentRecognitionData.setSegmentBoundary(segmentBoundary);

                        SegmentRecognitionStructure segmentRecognitionStructure =
                            segmentRecognitionStructures.get(barcodeValue - offset);

                        if (segmentRecognitionStructure != null) {

                            Map<String, OMRRecognitionStructure> OMRRecognitionStructures =
                                segmentRecognitionStructure.getOMRRecognitionStructures();

                            if (OMRRecognitionStructures != null) {

                                for (OMRRecognitionStructure omrrs : OMRRecognitionStructures
                                    .values()) {

                                    FragmentRecognitionData fragmentRecognitionData =
                                        new FragmentRecognitionData();

                                    Rectangle2D fragmentBoundary = null;
                                    BufferedImage fragmentImage = null;

                                    try {
                                        fragmentBoundary = omrrs
                                            .getRecognitionArea(segmentBoundary, fragmentPadding);
                                        fragmentImage = getFragmentImage(image, fragmentBoundary);
                                    } catch (FormReaderException ex) {
                                        ex.setCapturedDataFieldName(omrrs.getFieldName());
                                        ex.setMissingBarcodeData(barcodeValue + "");
                                        throw ex;
                                    }

                                    OMRMatrix omrMatrix =
                                        new OMRMatrix(omrrs, fragmentImage, markThreshold);
                                    fragmentRecognitionData.setOmrMatrix(omrMatrix);
                                    fragmentRecognitionData.setFragmentBoundary(fragmentBoundary);

                                    try {

                                        omrMatrix.process();
                                        fragmentRecognitionData
                                            .setType(FragmentRecognitionData.OMR_FRAGMENT);

                                    } catch (FormReaderException fre) {

                                        fre2 = fre;
                                        fre.setCapturedDataFieldName(omrrs.getFieldName());
                                        fragmentRecognitionData
                                            .setType(FragmentRecognitionData.DAMAGED_OMR_FRAGMENT);

                                    }

                                    segmentRecognitionData.getFragmentRecognitionData()
                                        .add(fragmentRecognitionData);

                                }

                            }

                            Map<String, BarcodeRecognitionStructure> BarcodeRecognitionStructures =
                                segmentRecognitionStructure.getBarcodeRecognitionStructures();

                            if (BarcodeRecognitionStructures != null) {

                                for (BarcodeRecognitionStructure bcrs : BarcodeRecognitionStructures
                                    .values()) {

                                    FragmentRecognitionData fragmentRecognitionData =
                                        new FragmentRecognitionData();

                                    Rectangle2D fragmentBoundary =
                                        bcrs.getRecognitionArea(segmentBoundary, fragmentPadding);
                                    BufferedImage fragmentImage =
                                        getFragmentImage(image, fragmentBoundary);
                                    BarcodeImageReader barcodeImageReader =
                                        new BarcodeImageReader(bcrs, fragmentImage);

                                    fragmentRecognitionData.setFragmentBoundary(fragmentBoundary);
                                    fragmentRecognitionData
                                        .setBarcodeImageReader(barcodeImageReader);
                                    fragmentRecognitionData
                                        .setType(FragmentRecognitionData.BARCODE_FRAGMENT);

                                    segmentRecognitionData.getFragmentRecognitionData()
                                        .add(fragmentRecognitionData);

                                }

                            }



                        }

                        pageRecognitionData.getSegmentRecognitionData().add(segmentRecognitionData);

                    }

                }

            }

        }

        if (fre2 != null) {
            throw fre2;
        }

    }

    public void loadRecognitionStucture(BufferedImage image) throws FormReaderException {

        if (formPage == null) {
            String msg = Localizer.localize("UI", "FormReaderNoFormPageLoadedMessage");
            FormReaderException fre =
                new FormReaderException(FormReaderException.FORM_PAGE_RECORD_MISSING, msg);
            fre.setMissingFormPageRecord(formRecognitionStructure.getFormPageID());
            throw fre;
        }

        calculateSegmentBoundaries();

        if (segmentBoundaries != null) {

            if (pageRecognitionData == null) {
                pageRecognitionData = new PageRecognitionData();
            }

            Iterator<Segment> si = formPage.getSegmentCollection().iterator();

            FormReaderException fre2 = null;

            while (si.hasNext()) {

                Segment segment = si.next();

                int barcodeValue = Misc.parseIntegerString(segment.getBarcodeOne());

                Rectangle2D segmentBoundary = segmentBoundaries.get(barcodeValue);

                if (segmentBoundary != null) {

                    SegmentRecognitionData segmentRecognitionData = new SegmentRecognitionData();
                    segmentRecognitionData.setSegmentBoundary(segmentBoundary);

                    Iterator<FragmentOmr> ifo = segment.getFragmentOmrCollection().iterator();
                    while (ifo.hasNext()) {

                        FragmentRecognitionData fragmentRecognitionData =
                            new FragmentRecognitionData();

                        FragmentOmr fragmentOmr = ifo.next();

                        OMRRecognitionStructure omrrs = getOMRRecognitionStructure(fragmentOmr);
                        Rectangle2D fragmentBoundary =
                            omrrs.getRecognitionArea(segmentBoundary, fragmentPadding);
                        BufferedImage fragmentImage = getFragmentImage(image, fragmentBoundary);

                        OMRMatrix omrMatrix = new OMRMatrix(omrrs, fragmentImage, markThreshold);
                        fragmentRecognitionData.setFragmentBoundary(fragmentBoundary);
                        fragmentRecognitionData.setOmrMatrix(omrMatrix);

                        try {

                            omrMatrix.process();
                            fragmentRecognitionData.setType(FragmentRecognitionData.OMR_FRAGMENT);

                        } catch (FormReaderException fre) {

                            fre2 = fre;
                            fragmentRecognitionData
                                .setType(FragmentRecognitionData.DAMAGED_OMR_FRAGMENT);
                            fre.setCapturedDataFieldName(omrrs.getFieldName());

                        }

                        segmentRecognitionData.getFragmentRecognitionData()
                            .add(fragmentRecognitionData);

                    }

                    Iterator<FragmentBarcode> ifb =
                        segment.getFragmentBarcodeCollection().iterator();
                    while (ifb.hasNext()) {

                        FragmentRecognitionData fragmentRecognitionData =
                            new FragmentRecognitionData();

                        FragmentBarcode fragmentBarcode = ifb.next();

                        BarcodeRecognitionStructure bcrs =
                            getBarcodeRecognitionStructure(fragmentBarcode);
                        Rectangle2D fragmentBoundary =
                            bcrs.getRecognitionArea(segmentBoundary, fragmentPadding);
                        BufferedImage fragmentImage = getFragmentImage(image, fragmentBoundary);

                        BarcodeImageReader barcodeImageReader =
                            new BarcodeImageReader(bcrs, fragmentImage);

                        fragmentRecognitionData.setFragmentBoundary(fragmentBoundary);
                        fragmentRecognitionData.setBarcodeImageReader(barcodeImageReader);
                        fragmentRecognitionData.setType(FragmentRecognitionData.BARCODE_FRAGMENT);

                        segmentRecognitionData.getFragmentRecognitionData()
                            .add(fragmentRecognitionData);

                    }

                    pageRecognitionData.getSegmentRecognitionData().add(segmentRecognitionData);

                }

            }

            if (fre2 != null) {
                throw fre2;
            }

        }

    }

    public void drawDetectionOverlay(Graphics2D g2, int x_offset, int y_offset) {

        g2.setStroke(new BasicStroke(1));

        Font font = Main.getCachedFontManager().getDefaultFont().deriveFont(10.0f);
        g2.setFont(font);

        for (SegmentRecognitionData segmentRecognitionData : pageRecognitionData
            .getSegmentRecognitionData()) {

            Rectangle2D segmentBoundary = segmentRecognitionData.getSegmentBoundary();
            Rectangle2D scaledSegmentBoundary = (Rectangle2D) segmentBoundary.clone();
            scaledSegmentBoundary
                .setRect(Math.round(segmentBoundary.getX() * overlayZoom) + x_offset,
                    Math.round(segmentBoundary.getY() * overlayZoom) + y_offset,
                    Math.round(segmentBoundary.getWidth() * overlayZoom),
                    Math.round(segmentBoundary.getHeight() * overlayZoom));

            if (drawDetectSegment == true) {
                g2.setColor(Color.GREEN);
                g2.draw(scaledSegmentBoundary);
            }

            for (FragmentRecognitionData fragmentRecognitionData : segmentRecognitionData
                .getFragmentRecognitionData()) {

                Rectangle2D fragmentBoundary = fragmentRecognitionData.getFragmentBoundary();

                if (drawDetectfragment == true) {
                    g2.setColor(Color.RED);
                    g2.draw(new Rectangle2D.Double(
                        Math.round(fragmentBoundary.getX() * overlayZoom) + x_offset,
                        Math.round(fragmentBoundary.getY() * overlayZoom) + y_offset,
                        Math.round(fragmentBoundary.getWidth() * overlayZoom),
                        Math.round(fragmentBoundary.getHeight() * overlayZoom)));
                }

                if (fragmentRecognitionData.getType() == FragmentRecognitionData.OMR_FRAGMENT) {

                    OMRMatrix omrMatrix = fragmentRecognitionData.getOmrMatrix();

                    String markedCharacters = Localizer.localize("UI", "Marked") + ": ";

                    if (omrMatrix.isCombineColumnCharacters()) {

                        markedCharacters = Localizer.localize("UI", "String") + ": " + omrMatrix
                            .getCapturedString();

                    } else {

                        Vector<String> capturedData = omrMatrix.getCapturedData();

                        if (capturedData.size() > 0) {
                            for (String capturedDataValue : capturedData) {
                                markedCharacters += "[";
                                markedCharacters += capturedDataValue;
                                markedCharacters += "]";
                            }
                        } else {
                            markedCharacters += " " + Localizer.localize("UI", "None");
                        }

                    }

                    if (drawDetectMarks == true) {
                        g2.setColor(new Color(102, 0, 0));
                        g2.drawString(markedCharacters,
                            (int) Math.round(fragmentBoundary.getX() * overlayZoom) + x_offset,
                            (int) Math.round(fragmentBoundary.getY() * overlayZoom) - 4 + y_offset);
                    }

                }

                if (fragmentRecognitionData.getType()
                    == FragmentRecognitionData.DAMAGED_OMR_FRAGMENT) {

                    String markedCharacters = Localizer.localize("UI", "Marked") + ": " + Localizer
                        .localize("UI", "ErrorTitle");

                    if (drawDetectMarks == true) {
                        g2.setColor(Color.ORANGE);
                        g2.drawString(markedCharacters,
                            (int) Math.round(fragmentBoundary.getX() * overlayZoom) + x_offset,
                            (int) Math.round(fragmentBoundary.getY() * overlayZoom) - 4 + y_offset);
                    }

                }

                if (fragmentRecognitionData.getType() == FragmentRecognitionData.BARCODE_FRAGMENT) {

                    BarcodeImageReader bcir = fragmentRecognitionData.getBarcodeImageReader();

                    String barcodeValue =
                        Localizer.localize("UI", "BarcodeValue") + ": " + bcir.getBarcodeValue();

                    if (drawDetectMarks == true) {
                        g2.setColor(new Color(102, 0, 0));
                        g2.drawString(barcodeValue,
                            (int) Math.round(fragmentBoundary.getX() * overlayZoom) + x_offset,
                            (int) Math.round(fragmentBoundary.getY() * overlayZoom) - 4 + y_offset);
                    }

                }

            }

        }

        if (drawDetectBarcode) {
            barcodeReader.drawDetectionOverlay(g2, x_offset, y_offset);
        }

    }

    public short getLuminanceThreshold() {
        return luminanceThreshold;
    }

    public void setLuminanceThreshold(short luminanceThreshold) {
        this.luminanceThreshold = luminanceThreshold;
    }

    public short getPerformDeskew() {
        return performDeskew;
    }

    public void setPerformDeskew(short performDeskew) {
        this.performDeskew = performDeskew;
    }

    public double getDeskewThreshold() {
        return deskewThreshold;
    }

    public void setDeskewThreshold(double deskewThreshold) {
        this.deskewThreshold = deskewThreshold;
    }

    public double getBinarizedImageSkewAngle() {
        return imageSkewAngle;
    }

    public PageRecognitionData getPageRecognitionData() {
        return pageRecognitionData;
    }

    public void setPageRecognitionData(PageRecognitionData pageRecognitionData) {
        this.pageRecognitionData = pageRecognitionData;
    }

    public BarcodeRecognitionData getBarcodeRecognitionDataByPoint(Point2D point) {

        BarcodeRecognitionData formIDBarcode = pageRecognitionData.getFormIDBarcode();

        if (formIDBarcode != null && formIDBarcode.getBarcodeBoundary().contains(point)) {
            return formIDBarcode;
        }

        TreeMap<Integer, BarcodeRecognitionData> segmentBarcodes =
            pageRecognitionData.getSegmentBarcodes();

        if (segmentBarcodes != null) {
            for (BarcodeRecognitionData segmentBarcode : segmentBarcodes.values()) {
                if (segmentBarcode.getBarcodeBoundary().contains(point)) {
                    return segmentBarcode;
                }
            }
        }

        Vector<BarcodeRecognitionData> code128barcodes = pageRecognitionData.getCode128Barcodes();

        if (code128barcodes != null) {
            for (BarcodeRecognitionData code128barcode : code128barcodes) {
                if (code128barcode.getBarcodeBoundary().contains(point)) {
                    return code128barcode;
                }
            }
        }

        return null;

    }

    public FragmentRecognitionData getFragmentRecognitionDataByPoint(Point2D point) {

        for (SegmentRecognitionData segmentRecognitionData : pageRecognitionData
            .getSegmentRecognitionData()) {

            for (FragmentRecognitionData fragmentRecognitionData : segmentRecognitionData
                .getFragmentRecognitionData()) {

                Rectangle2D fragmentBoundary = fragmentRecognitionData.getFragmentBoundary();

                if (fragmentBoundary.contains(point)) {
                    return fragmentRecognitionData;
                }

            }

        }
        return null;
    }

    public int getPublicationType() {
        return publicationType;
    }

    public void setPublicationType(int publicationType) {
        this.publicationType = publicationType;
    }

    public FormPage getFormPage() {
        return formPage;
    }

    public static int getLastOrientation() {
        return lastOrientation;
    }

    public static void setLastOrientation(int lastOrientation) {
        FormReader.lastOrientation = lastOrientation;
    }

}
