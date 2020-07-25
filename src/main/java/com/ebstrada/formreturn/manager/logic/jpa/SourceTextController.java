package com.ebstrada.formreturn.manager.logic.jpa;

import javax.persistence.EntityManager;

import com.ebstrada.formreturn.manager.persistence.jpa.Record;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceField;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceText;
import com.ebstrada.formreturn.manager.ui.Main;

public class SourceTextController {

    public SourceTextController() {
    }

    public void updateSourceTextStringValue(long sourceTextId, String fieldValue) {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        try {
            SourceText st = entityManager.find(SourceText.class, sourceTextId);
            if (st != null) {
                st.setSourceTextString(fieldValue);
                entityManager.getTransaction().begin();
                entityManager.flush();
                entityManager.persist(st);
                entityManager.getTransaction().commit();
            }
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

    public void createSourceTextStringValue(long dataSetId, long recordId, long sourceFieldId,
        String fieldValue) {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        try {
            SourceField sf = entityManager.find(SourceField.class, sourceFieldId);
            Record record = entityManager.find(Record.class, recordId);

            SourceText st = new SourceText();
            st.setRecordId(record);
            st.setSourceFieldId(sf);
            st.setSourceTextString(fieldValue);

            entityManager.getTransaction().begin();
            entityManager.flush();
            entityManager.persist(st);
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
