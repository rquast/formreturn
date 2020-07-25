package com.ebstrada.formreturn.manager.ui.cdm.logic;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.ebstrada.formreturn.manager.persistence.jpa.DataSet;
import com.ebstrada.formreturn.manager.persistence.jpa.Record;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceField;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceText;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;

public class FillTableController {

    public static final int FILL_NUMERIC_SERIES = 0;

    public static final int FILL_DUPLICATE = 1;

    private long recordId;

    private String value;

    private int fillType;

    private int stepSize;

    private int total;

    private int count = 0;

    private EntityManager entityManager;

    private TypedQuery<Record> query;

    private DataSet dataSetId;

    private long existingCount;

    private String fieldname;

    private SourceField sourceField;

    public FillTableController(long recordId, String fieldname, String value, int fillType,
        int stepSize, int total) {
        this.recordId = recordId;
        this.fieldname = fieldname;
        this.value = value;
        this.fillType = fillType;
        this.stepSize = stepSize;
        this.total = total;
        this.entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        findRecords();

    }

    private void findRecords() {
        Record record = entityManager.find(Record.class, recordId);
        dataSetId = record.getDataSetId();
        sourceField = entityManager.createQuery(
            "SELECT sf FROM SourceField sf WHERE sf.dataSetId = :dataSetId AND sf.sourceFieldName = :sourceFieldName",
            SourceField.class).setParameter("dataSetId", dataSetId)
            .setParameter("sourceFieldName", this.fieldname).setMaxResults(1).getSingleResult();
        this.existingCount = (Long) entityManager.createQuery(
            "SELECT count(r.recordId) FROM Record r WHERE r.dataSetId = :dataSetId AND r.recordId > :recordId")
            .setParameter("dataSetId", dataSetId).setParameter("recordId", record.getRecordId())
            .getSingleResult();
        query = entityManager.createQuery(
            "SELECT r FROM Record r WHERE r.dataSetId = :dataSetId AND r.recordId > :recordId ORDER BY r.recordId ASC",
            Record.class);
        query.setParameter("dataSetId", dataSetId);
        query.setParameter("recordId", record.getRecordId());
        query.setMaxResults(1);
    }

    public boolean fillNext() {
        Record record = getNextRecord();
        SourceText sourceText = getSourceTextRecord(record);
        updateSourceTextValue(sourceText);
        ++count;
        return (count < total);
    }

    private void updateSourceTextValue(SourceText sourceText) {
        switch (fillType) {
            case FILL_NUMERIC_SERIES:
                int seriesValue =
                    Misc.parseIntegerString(this.value) + ((this.count + 1) * this.stepSize);
                sourceText.setSourceTextString(seriesValue + "");
                break;
            case FILL_DUPLICATE:
                sourceText.setSourceTextString(this.value);
                break;
        }
        entityManager.persist(sourceText);
    }

    private SourceText getSourceTextRecord(Record record) {

        TypedQuery<SourceText> sourceTextQuery = entityManager.createQuery(
            "SELECT st FROM SourceText st WHERE st.recordId = :recordId AND st.sourceFieldId = :sourceFieldId",
            SourceText.class);
        sourceTextQuery.setParameter("recordId", record);
        sourceTextQuery.setParameter("sourceFieldId", this.sourceField);

        SourceText sourceText = null;
        try {
            sourceText = sourceTextQuery.getSingleResult();
        } catch (NoResultException nrex) {
            sourceText = createSourceTextRecord(sourceText, record);
        }

        if (sourceText == null) {
            sourceText = createSourceTextRecord(sourceText, record);
        }

        return sourceText;
    }

    private SourceText createSourceTextRecord(SourceText sourceText, Record record) {
        sourceText = new SourceText();
        sourceText.setRecordId(record);
        sourceText.setSourceFieldId(this.sourceField);
        entityManager.persist(sourceText);
        entityManager.flush();
        return sourceText;
    }

    private Record getNextRecord() {
        if (count < existingCount) {
            query.setFirstResult(count);
            return this.query.getSingleResult();
        } else {
            return createNewRecord();
        }
    }

    private Record createNewRecord() {
        Record record = new Record();
        record.setDataSetId(dataSetId);
        entityManager.persist(record);
        entityManager.flush();
        return record;
    }

    public int getCount() {
        return this.count;
    }

    public int getTotal() {
        return this.total;
    }

    public void begin() {
        entityManager.getTransaction().begin();
    }

    public void commit() {
        entityManager.getTransaction().commit();
    }

    public void rollback() {
        entityManager.getTransaction().rollback();
    }

}
