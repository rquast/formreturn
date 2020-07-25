package com.ebstrada.formreturn.manager.logic.jpa;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.ebstrada.formreturn.manager.persistence.jpa.DataSet;
import com.ebstrada.formreturn.manager.persistence.jpa.Record;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceField;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceText;
import com.ebstrada.formreturn.manager.ui.Main;

public class RecordController {

    public RecordController() {
    }

    public void createNewRecord(long dataSetId, String[] columnNames, String[] nextLine) {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        try {
            Record record = createNewRecord(dataSetId);
            if (record == null) {
                return;
            }
            entityManager.getTransaction().begin();
            entityManager.flush();
            DataSet dataSet = entityManager.find(DataSet.class, dataSetId);
            for (int i = 0; i < columnNames.length; i++) {

                if (columnNames[i] == null || columnNames[i].trim().length() <= 0) {
                    continue;
                }

                Query sourceFieldQuery =
                    entityManager.createNamedQuery("SourceField.findBySourceFieldName");
                sourceFieldQuery.setParameter("sourceFieldName", columnNames[i].trim());
                sourceFieldQuery.setParameter("dataSetId", dataSet);

                List resultList = sourceFieldQuery.getResultList();
                SourceField sourceField;

                if (resultList.size() <= 0) {
                    sourceField = new SourceField();
                    sourceField.setDataSetId(dataSet);
                    sourceField.setSourceFieldName(columnNames[i].trim());
                    sourceField.setOrderIndex(i + 1);
                    // TODO: this needs to be set when defining the field type
                    sourceField.setSourceFieldType("STRING");
                    entityManager.persist(sourceField);
                } else {
                    Object[] sourceFields = resultList.toArray();
                    sourceField = (SourceField) sourceFields[0];
                }
                SourceText sourceText = new SourceText();
                sourceText.setSourceFieldId(sourceField);
                try {
                    sourceText.setSourceTextString(nextLine[i]);
                } catch (Exception ex) {
                    sourceText.setSourceTextString("");
                }
                sourceText.setRecordId(record);
                entityManager.persist(sourceText);
            }
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            if (entityManager.getTransaction().isActive()) {
                try {
                    entityManager.getTransaction().rollback();
                } catch (Exception rbex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(rbex);
                }
            }
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        } finally {
            entityManager.close();
        }
    }

    public Record createNewRecord(long dataSetId) {
        Record newRecord = new Record();
        newRecord.setRecordCreated(new Timestamp(System.currentTimeMillis()));
        newRecord.setRecordModified(new Timestamp(System.currentTimeMillis()));

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return null;
        }

        try {
            entityManager.getTransaction().begin();
            entityManager.flush();
            newRecord.setDataSetId(entityManager.find(DataSet.class, dataSetId));
            entityManager.persist(newRecord);
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            if (entityManager.getTransaction().isActive()) {
                try {
                    entityManager.getTransaction().rollback();
                } catch (Exception rbex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(rbex);
                }
            }
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return null;
        } finally {
            entityManager.close();
        }
        return newRecord;
    }

    public void removeRecordsById(long[] selectedRecordIds) {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        try {
            entityManager.getTransaction().begin();
            entityManager.flush();
            for (int i = 0; i < selectedRecordIds.length; i++) {
                Record record = entityManager.find(Record.class, selectedRecordIds[i]);
                entityManager.remove(record);
            }
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            if (entityManager.getTransaction().isActive()) {
                try {
                    entityManager.getTransaction().rollback();
                } catch (Exception rbex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(rbex);
                }
            }
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        } finally {
            entityManager.close();
        }

    }

}
