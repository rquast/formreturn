package com.ebstrada.formreturn.manager.logic.aggregation;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.ebstrada.aggregation.exception.ErrorFlagException;
import com.ebstrada.aggregation.exception.InvalidRulePartException;
import com.ebstrada.aggregation.exception.NoMatchException;
import com.ebstrada.formreturn.api.messaging.MessageNotification;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.reader.FormReaderException;
import com.ebstrada.formreturn.manager.logic.recognition.structure.CheckBoxRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.OMRRecognitionStructure;
import com.ebstrada.formreturn.manager.persistence.jpa.CheckBox;
import com.ebstrada.formreturn.manager.persistence.jpa.Form;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentOmr;
import com.ebstrada.formreturn.manager.persistence.jpa.Publication;
import com.ebstrada.formreturn.manager.persistence.jpa.Segment;

public class AggregateCalculator {

    public static void recalculateAggregate(EntityManager entityManager, Publication publication,
        MessageNotification messageNotification) throws Exception {

        try {
            startTransaction(entityManager);
            aggregate(entityManager, publication, messageNotification);
            endTransaction(entityManager);
        } catch (final Exception ex) {
            abortTransaction(entityManager);
            throw ex;
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
                entityManager = null;
            }
        }

    }

    private static void aggregate(EntityManager entityManager, Publication publication,
        MessageNotification messageNotification) throws InterruptedException {

        int pageNumber = 1;

        if (publication.getFormCollection() != null) {

            for (Form form : publication.getFormCollection()) {

                if (form.getFormPageCollection() != null) {

                    for (FormPage formPage : form.getFormPageCollection()) {

                        messageNotification.setMessage(String
                            .format(Localizer.localize("UI", "RecalculatingPageNumber"),
                                pageNumber));

                        double pageAggregate = 0.0d;
                        double segmentAggregate = 0.0d;
                        long formPageErrorAggregate = 0;

                        if (formPage.getSegmentCollection() != null) {
                            for (Segment segment : formPage.getSegmentCollection()) {

                                if (messageNotification.isInterrupted()) {
                                    throw new InterruptedException();
                                }

                                segmentAggregate = 0.0d;

                                if (segment.getFragmentOmrCollection() != null) {
                                    for (FragmentOmr fragmentOmr : segment
                                        .getFragmentOmrCollection()) {

                                        OMRRecognitionStructure omrrs =
                                            getOMRRecognitionStructure(fragmentOmr);

                                        try {
                                            omrrs.persistToFragmentOmr(entityManager, fragmentOmr);
                                            pageAggregate += fragmentOmr.getMark();
                                            segmentAggregate += fragmentOmr.getMark();
                                            if (fragmentOmr.getErrorType() > 0) {
                                                ++formPageErrorAggregate;
                                            }
                                        } catch (InvalidRulePartException e) {
                                            fragmentOmr.setErrorType(
                                                (short) FormReaderException.INVALID_AGGREGATION_RULE);
                                            fragmentOmr.setInvalidated((short) 1);
                                            ++formPageErrorAggregate;
                                            entityManager.persist(fragmentOmr);
                                            entityManager.flush();
                                        } catch (ErrorFlagException e) {
                                            fragmentOmr.setErrorType(
                                                (short) FormReaderException.ERROR_CONDITION_MET);
                                            fragmentOmr.setInvalidated((short) 1);
                                            ++formPageErrorAggregate;
                                            entityManager.persist(fragmentOmr);
                                            entityManager.flush();
                                        }

                                    }
                                }

                                segment.setAggregateMark(segmentAggregate);
                                entityManager.persist(segment);

                            }
                        }

                        AggregateCalculator
                            .updateErrorCount(entityManager, formPage, formPageErrorAggregate);
                        AggregateCalculator
                            .updateFormScores(entityManager, formPage, pageAggregate);

                        pageNumber++;

                    }

                }

            }

        }

    }

    public static void startTransaction(EntityManager entityManager) throws Exception {
        try {
            entityManager.getTransaction().begin();
            entityManager.flush();
        } catch (Exception ex) {
            abortTransaction(entityManager);
            throw ex;
        }
    }

    public static void abortTransaction(EntityManager entityManager) {
        if (entityManager.getTransaction().isActive()) {
            try {
                entityManager.getTransaction().rollback();
            } catch (Exception rbex) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(rbex);
            }
        }
    }

    public static void endTransaction(EntityManager entityManager) throws Exception {
        try {
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            abortTransaction(entityManager);
            throw ex;
        }
    }

    public static void updateFormScores(EntityManager entityManager, FormPage formPage,
        double pageAggregate) {
        formPage.setAggregateMark(pageAggregate);
        entityManager.persist(formPage);
        entityManager.flush();
        Form form = formPage.getFormId();
        double markAggregate = 0.0d;
        for (FormPage fp : form.getFormPageCollection()) {
            markAggregate += fp.getAggregateMark();
        }
        form.setAggregateMark(markAggregate);
        entityManager.persist(form);
        entityManager.flush();
    }

    public static void updateErrorCount(EntityManager entityManager, FormPage formPage,
        long formPageErrorAggregate) {
        formPage.setErrorCount(formPageErrorAggregate);
        entityManager.persist(formPage);
        entityManager.flush();
        Form form = formPage.getFormId();
        int formErrorAggregate = 0;
        for (FormPage fp : form.getFormPageCollection()) {
            formErrorAggregate += fp.getErrorCount();
        }
        form.setErrorCount(formErrorAggregate);
        entityManager.persist(form);
        entityManager.flush();
    }

    public static OMRRecognitionStructure getOMRRecognitionStructure(FragmentOmr fragmentOmr) {
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
                cbrs.setCheckBoxMarked(cb.getCheckBoxMarked() > 0 ? true : false);
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

}
